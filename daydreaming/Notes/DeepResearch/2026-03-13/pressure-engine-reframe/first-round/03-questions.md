## 1. Top-line judgment

The reframe is good upstream and wrong downstream.

It is a good **research-track cleanup** for world-building and character exploration. It is a bad description of the **shipping mainline** if you apply it wholesale. The current shipping architecture is still the narrower one: **authored graph → traversal/adaptation → normalized playback packet → renderer/stage**, with Director feedback as a separate perturbation loop, not as authoring, not as traversal replacement, and not as renderer logic. That is what `17-game-engine-architecture.md`, `21-graffito-v0-playback-contract.md`, `12-director-prompt-spec.md`, and `18-graffito-vertical-slice.md` are actually saying.

So the blunt answer to the biggest question is:

**No, this is not one architecture.**
It is **two related architectures with shared control geometry**.

* **Levels 1 and 2** can plausibly share one pressure-driven hypothetical engine$_{80%}$.
* **Level 3** is not that engine. It is a **dramaturgical traversal controller over authored material**$_{90%}$.

If you keep saying “same loop at all three levels,” you will blur a real boundary that the current system already discovered the hard way.

The concrete code path makes this obvious. In `puppet_knows_autonomous.clj`, the live conducted loop is not “Concern all the way down.” It is:

`compute-goal-candidates -> sync-goals -> control/run-cycle -> apply-family-plan -> adapter projection -> choose-node -> maybe-apply-director-feedback`

That separation is not incidental. It is the architecture.

So my honest rewrite is:

* **Shipping/art track:** keep the authored-graph performance model narrow. Get the watched Graffito run.
* **Research/cognition track:** use the pressure-engine reframe to clean up authoring-time exploration and kernel-side reasoning.
* **Do not** force level 3 into the same `Concern / Context / CommitRecord` mold just because it sounds unified.

---

## 2. Category errors / hidden contradictions

### A. False symmetry: level 3 is not the same machine as levels 1 and 2

Levels 1 and 2 are about **hypothetical expansion of causal state**.
Level 3 is about **selection, ordering, and dwell over already-authored nodes**.

That matters.

At levels 1 and 2, `Context`, `assumption_patch`, `Proposal`, and `CommitRecord` make sense:

* fork a context,
* state the changed assumption,
* explore consequences,
* maybe commit policy/salience/ontic updates.

At level 3, that shape is strained. Most of the time you are not forking the world. You are choosing the next node, maybe revisiting, maybe shifting situations, maybe dwelling. If you have to invent fake `assumption_patch` values just to keep the abstraction uniform, the abstraction already failed.

So:

* **L1/L2:** hypothetical engine
* **L3:** traversal controller

Related, yes. Same thing, no.

### B. `Concern` is overloaded

Using one cross-level `Concern` type is too mushy.

“Concern” sounds psychological. That fits level 2. It does **not** cleanly fit:

* world-building gaps,
* tonal imbalance,
* exhausted traversal regions,
* unearned climax pressure.

If you want one shared umbrella, call it **Pressure**, not `Concern`.

Then split it:

* `CreativePressure`
* `CharacterConcern`
* `TraversalPressure`

Shared fields should stay minimal:

* `id`
* `level`
* `owner_id`
* `source_ids`
* `intensity`
* `urgency`
* `valence`
* `unresolved`

Everything else should be subtype-specific. `blockers`, `supporting_ids`, `decay_rate`, even `target_id` are not equally natural at all three levels.

### C. Operators and adapters are being conflated

`04-kernel-gap-analysis.md` says benchmark-specific adapters could become operator plugins. I think that is wrong.

These are different jobs:

* **Operator:** explores *inside* a level-specific state space.
* **Adapter / projection layer:** translates one vocabulary into another.
* **Renderer:** turns already-selected material into model-ready prompt form.

Current code already separates them:

* `goal_families.clj` contains operator-like family behaviors.
* `puppet_knows_adapter.clj`, `arctic_expedition_adapter.clj`, and `stalker_zone_adapter.clj` project branch-local facts into situation/node vocabulary.
* `choose-node` in `puppet_knows_autonomous.clj` does retrieval + ranking over graph nodes.
* `trace.clj` exposes the Director-facing packet.
* `director.clj` applies conceptual feedback to next-cycle state.

That repeated adapter pattern is not glue you can hand-wave away. It is the evidence that the engine’s internal semantics and the conducted system’s traversal semantics are **not identical**.

Do not bury that behind “operator plugins.” You will lose the seam that currently lets you evaluate the system.

### D. “The graph is not a hand-authored asset” is wrong for the current project

That claim in `01-reframe-summary.md` directly collides with the current mainline docs.

`17-game-engine-architecture.md` is explicit: the dream graph is the main creative artifact, node quality is load-bearing, and a thin graph cannot be rescued live. `18-graffito-vertical-slice.md` is even narrower: for Graffito v0, hand-author the slice, trace-player first, get the watched run, do **not** put the Clojure kernel on the critical path.

So the honest synthesis is:

* **Shipping track:** the graph is a human-authored, human-curated asset.
* **Research track:** engine-assisted authoring may later propose candidates into that graph.

But “the graph is not a hand-authored asset” is not a brave future-facing statement. It is just false for the system you actually have today.

### E. One `CommitRecord` taxonomy does not fit all levels cleanly

`ontic / policy / salience / none` is good for L1/L2.

At L3 it gets weird.

At traversal level, the natural updates are things like:

* `selected_node`
* `activation_shift`
* `recall_bias`
* `dwell_extension`
* `canon_event_committed`

Calling those “policy” is vague. It hides the real difference between:

* world facts changing,
* character readiness shifting,
* traversal bias shifting.

So keep a shared **commit supertype** if you want, but make the enums level-specific.

My recommendation:

* **L1/L2:** `ontic | policy | salience | none`
* **L3:** `canon_event | traversal_bias | activation_shift | none`

### F. Do not overload `family` with director-operator semantics

Current runtime contracts already have a stable seam. `21-graffito-v0-playback-contract.md` defines the normalized cycle packet. The renderer and narration both depend on it.

If you want director-level operators like `build`, `release`, `shift`, `recall`, `juxtapose`, `dwell`, do **not** jam them into `family`.

Keep `family` / `goal_type` for current benchmark compatibility and current Graffito semantics.

If you add anything, add an **optional** field like:

* `traversal_intent`

That lets you experiment without muddying the current packet. Changing the packet to center `Concern` now would be breaking the wrong seam.

### G. Hidden schedule contradiction: the reframe quietly steals attention from the watched run

This is the biggest practical problem.

`13-project-state.md`, `14-operating-map.md`, and `reply.md` all say the same thing: the main unresolved question is still whether watched output feels like a mind doing something.

`18-graffito-vertical-slice.md` says trace-player first.
`21-graffito-v0-playback-contract.md` says the next seam is playback join / trace-player.
`14-operating-map.md` says the critical path is evaluation through the live loop.

So any reframe that pulls focus toward:

* multi-level generalization,
* mainline Concern packets,
* adapter-to-operator refactors,
* engine-generated graph authoring,

before the watched Graffito run is mis-prioritized.

---

## 3. Revised architecture

Here is the version I actually believe.

```text
RESEARCH / AUTHORING LANE
=========================

Source Bible / World Sketch
        |
        v
L1 Creative Pressure Engine
(world gaps, contradictions, tonal imbalance)
        |
        v
L2 Character Pressure Engine
(injury, desire, avoidance, rehearsal, retaliation)
        |
        v
Candidate scenes / events / variants
        |
        v
Human curation + authoring
        |
        v
Authored Graph


SHIPPING / PERFORMANCE LANE
===========================

Authored Graph
   + traversal state
   + visit history
   + Director feedback
        |
        v
Traversal Controller
(selection / recall / shift / dwell / release)
        |
        v
Adapter / Projection Layer
(graph + state -> normalized playback packet)
        |
        v
Normalized Playback Packet
        |
        +--> Renderer
        +--> Narration
        +--> Stage / Music / Control


SHARED INFRASTRUCTURE
=====================

- validation
- episodic retrieval
- trace/export
- optional hypothetical context engine
- world-state services
```

### What each level owns

**L1 owns:** discovering world material worth having.
**L2 owns:** motivated scene/event candidates.
**L3 owns:** traversal over curated material.

### Where operators live

Operators are valid at all levels, but they are **not the same class**.

* **L1/L2 operators** are exploratory and branch-generating.
* **L3 operators** are controller verbs over traversal state.

Same word, different category family. Treat them as sibling interfaces, not one interface with wishful type erasure.

### Where adapters live

Adapters live **between** engine semantics and graph/stage semantics.
They are not exploratory operators. They are translators.

### Where the graph sits

The graph sits between research-time generation and performance-time traversal.

It is the crystallized artifact of authoring plus curation.
That is true even if later you populate more of it with engine assistance.

### Where the performer sits

The performer steers:

* pressure,
* pacing,
* situation emphasis,
* music,
* maybe family override,

but **not** low-level scene semantics directly.

That matches the current conducted-story: steer the traversal, do not rewrite the graph live.

### Revised glossary

Use this glossary and half the confusion disappears:

* **Pressure:** shared prioritization signal across levels
* **CreativePressure / CharacterConcern / TraversalPressure:** level-specific kinds of pressure
* **Operator:** within-level exploration or control move
* **Adapter:** translation between vocabularies or layers
* **Traversal controller:** level-3 selector over authored nodes
* **Playback packet:** stable runtime contract for renderer/narration/stage
* **Commit:** accepted update to a level-specific state, with level-specific enums

---

## 4. What to change now vs later

### Change now

1. **Rewrite the architecture story as two lanes, not one engine.**
   Say plainly that levels 1-2 are the exploration engine and level 3 is the traversal controller.

2. **Rename or split `Concern`.**
   My vote: use `Pressure` as the umbrella, keep `Concern` only for character-level use.

3. **Keep operators and adapters separate in the docs.**
   The code already does this. The docs should stop pretending otherwise.

4. **Keep the current runtime seams stable.**
   That means:

   * `dreamer-state-packet`
   * Director feedback schema
   * normalized playback packet

5. **If you want to experiment with level-3 verbs, add `traversal_intent` in trace/debug first, not in the Graffito runtime contract.**

### Defer until after a watched run

1. Engine-assisted graph authoring from compressed world state
2. Multi-level runner unification
3. Cross-level commit formalization
4. Full `assumption_patch` / `Proposal` / `CommitRecord` refactor of the kernel
5. Any attempt to make Graffito v0 depend on the pressure engine

### Likely wrong turn

1. **Injecting `Concern` into the live playback packet now**
2. **Turning benchmark adapters into operator plugins**
3. **Replacing hand-authored Graffito graph work with engine-generated authoring**
4. **Re-centering the mainline around level-1/2 abstractions before the watched run**
5. **Pretending level 3 is context-forking cognition instead of traversal control**

If you do those now, you will buy abstraction purity by spending the only thing that matters right now: time to first watched judgment.

---

## 5. One falsifiable next experiment

Test the weakest claim in the reframe first: **that level 3 deserves to be re-described as pressure-driven traversal rather than just current family/tension scheduling**.

### Experiment

Use the existing Graffito v0 graph and keep the renderer path unchanged.

Build a tiny **TraversalPressure controller** with only four pressures:

* `tension_deficit`
* `exhausted_situation`
* `avoidance`
* `recall_opportunity`

Map those to an optional debug-only field:

* `traversal_intent = build | shift | confront | recall | dwell | release`

Do **not** change the normalized playback packet yet. Put `traversal_intent` in trace/debug only.

Then generate three 18-cycle sequences over the same graph:

1. **Current authored trace baseline**
2. **Weighted-random graph walk baseline**
3. **TraversalPressure controller**

Render them as either:

* cheap storyboard/animatic runs, or
* prompt logs + timing + node sequence if rendering cost is high.

Blind-rate them on two questions:

1. Does this feel like a mind moving through charged material?
2. Is the arc legible without explanation?

### Falsification rule

* If the TraversalPressure run does **not** beat weighted random and does **not** clearly add legibility beyond the current baseline, then do **not** import the level-3 reframe into the mainline.
* If it does beat them, then you have evidence for a distinct level-3 traversal-pressure layer.

That experiment is small, cheap, and hits the actual uncertainty. It preserves the shipping track, and it tests the research claim where it matters.

---

Bottom line: keep the reframe, narrow it hard.

* **Upstream:** yes, use pressure-driven exploration for research and authoring.
* **Downstream:** keep the mainline honest. It is still an authored-graph performance system with explicit traversal/adaptation seams.
* **Do not** unify the names faster than the architecture has unified in reality.

Look away from the screen for a second and relax your shoulders.
