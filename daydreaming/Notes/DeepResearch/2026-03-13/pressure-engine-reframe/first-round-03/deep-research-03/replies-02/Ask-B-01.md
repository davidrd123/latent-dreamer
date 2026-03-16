`Provocation Generator` is the right doc term. `Provocateur` is a fine nickname. Use the boring name in architecture notes.

Three things I’d challenge in your update before anything else:

1. “No shared code” is too strong. Different prompts, different contracts, different behavior, yes. But they should absolutely share world/brief loaders, ID validation, provenance helpers, and schema serializers. Otherwise you get drift for no gain.

2. “Neither system knows about the other” is also too strong. They should not know about each other’s implementation, but the Provocation Generator does need a reduced summary of accumulated internal progression. Otherwise its world pushes will be generic.

3. `FixtureDeltaV1` is fine as the current harness/schema name. Conceptually, though, it is a world-authoring delta, not a fixture-only concept. If you forget that, prototype vocabulary will leak upward.

The clean premise underneath all of this is already stable in the broader architecture: authored world → graph → traversal → render, with traversal at performance time explicitly distinct from generation, and with canon/constraints/motifs already separated upstream. That argues against one giant merged artifact. 

## 1. 2-axis vocabulary map

The mistake is trying to force a 1:1 synonym map. These vocabularies cut the system along different axes.

| Time        | Function                             | Experiential term             | Pressure-engine term                             | What it actually is                                                           |
| ----------- | ------------------------------------ | ----------------------------- | ------------------------------------------------ | ----------------------------------------------------------------------------- |
| Authoring   | World / meaning substrate            | **Prep**                      | upstream assets (no L-number)                    | `world` + `creative_brief` + `style_extensions` + authored scaffolds          |
| Authoring   | World-event introduction             | **Provocation Generator**     | no L-number, but lives inside the authoring lane | emits `FixtureDeltaV1` proposals                                              |
| Authoring   | Material-supply orchestration        | *(gap in experiential vocab)* | **L1**                                           | decides what material/world gap to work on; manages proposal/curation flow    |
| Authoring   | Cognition-shaped reaction generation | *(gap in experiential vocab)* | **L2 (authoring mode)**                          | concerns → causal slice → appraisal → practice → operator → candidate moments |
| Performance | Traversal + optional live cognition  | **Dreamer**                   | **L3 + optional live L2**                        | runtime bundle; today mostly traversal, later possibly richer live inner life |
| Performance | Interpretive perturbation            | **Director**                  | no L-number                                      | reads reduced cycle packet + brief; nudges next-cycle runtime state           |
| Performance | Externalization                      | **Stage**                     | adapter + outputs                                | dashboard, narration, audio, visuals                                          |

Two important consequences:

* **Prep is not L1.** Prep is authored input. L1 is authoring-time machine work over those inputs.
* **Dreamer is not just L3.** At runtime it is the bundle that currently contains L3 and could later contain live L2. Your intermediate map is right on that.

The real gap is **L1 has no experiential name**. That is not a bug in your analysis. It is a real omission in the experiential vocabulary.

## 2. Coherent architecture versions

### Version A: one “Director” in two modes

**Authoring loop:** Director emits world provocations → pipeline reacts
**Performance loop:** Director emits runtime feedback echoes

This is the seductive version. I think it is wrong.

Why it breaks:

* same word, two different contracts
* same persona, two different timescales
* constant confusion in notes and code reviews
* people will start hand-waving “Director” when they mean incompatible things

Codebase match: weak.

Verdict: reject.

---

### Version B: **Provocation Generator** at authoring time, **Director** at runtime

This is your current lean, and it is the right one$_{85%}$.

**Authoring loop**

1. Prep assets load
2. L1 picks a target gap / situation / coverage hole
3. Provocation Generator reads brief + world view + reduced internal progression summary
4. It writes `FixtureDeltaV1`
5. Pipeline applies delta to make an updated fixture view
6. L2 authoring-mode generation produces reactions
7. Human curates
8. Graph compiler admits stable graph material + sidecars

**Performance loop**

1. Graph + runtime state + conductor feed Dreamer
2. Dreamer selects / traverses
3. Adapter emits normalized cycle packet
4. Director reads cycle packet + brief
5. Director emits feedback echo
6. Runtime state mutates for the next cycle only
7. Stage consumes packet

What I’d keep from your phrasing:

* separate prompts
* separate outputs
* separate responsibilities

What I would not keep:

* “no shared code”
* “neither system knows about the other”

They should share infra, and they should communicate through reduced contracts.

Codebase match: strongest.

Verdict: use this as the governing version.

---

### Version C: human-authored provocations only, Director runtime only

This is simpler, but it ducks the current bottleneck.

Why it breaks:

* leaves world-event introduction upstream mostly manual
* does not solve material-supply scaling
* turns the authoring lane into “reaction generator waiting for humans”

Codebase match: workable, but strategically weak.

Verdict: too timid.

## 3. Brief ↔ fixture ↔ graph: what should unify, what should not

Full merge is wrong. Full separation is also wrong.

The right answer is **shared IDs + layer-specific views**.

Comparable prose/graph systems still do not solve automatic bidirectional sync cleanly. One mutable “master artifact” sounds elegant and usually turns into drift, hidden coupling, and provenance loss. Shared IDs with compiled views is the sane compromise. 

### Artifact split I would use

| Artifact                                     | Purpose                                                     | Writer                        | Reader                                                          |
| -------------------------------------------- | ----------------------------------------------------------- | ----------------------------- | --------------------------------------------------------------- |
| `world.yaml`                                 | canonical entities, places, situations, events, constraints | human                         | Prep, Provocation Generator, authoring pipeline, graph compiler |
| `canonical_state.*`                          | mutable cross-session world facts                           | runtime/event system          | future authoring runs, traversal                                |
| `creative_brief.yaml`                        | concept, tensions, obsessions, interpretive angles          | human                         | Provocation Generator, Director, renderer/narration             |
| `style_extensions.yaml`                      | capstones, negative space, style vocabulary                 | human                         | Director, renderer, narration                                   |
| `authoring_fixture.yaml`                     | benchmark slice, prototype knobs, eval scaffolding          | human/research                | L1/L2 authoring tooling                                         |
| `fixture_deltas.jsonl`                       | append-only `FixtureDeltaV1` proposals during runs          | Provocation Generator / human | fixture-view compiler, graph compiler                           |
| curated fixture delta YAML under `fixtures/` | accepted delta history                                      | human                         | future authoring runs                                           |
| `dream_graph.*`                              | compiled shared graph seam                                  | human + compiler              | Dreamer / Stage                                                 |
| sidecars / traces                            | rich provenance, appraisal, retrieval, diagnostics          | machine                       | humans, dashboard, debug                                        |

Directly: **the fixture is not the world**. It is an authoring/test view over the world.

### Two schema nits on `FixtureDeltaV1`

1. **`source_lane` is currently wrong.**
   `provocation_generator` is not a lane. It is an agent.

   Better:

   ```yaml
   source_lane: human | L1
   source_agent: human | provocation_generator
   ```

2. **Reserve `updates.events` now.**
   If you do not, you will encode event evolution as situation churn.

   Even if you leave it unused at first:

   ```yaml
   updates:
     events: []
     situations: []
     reference_markers: []
   ```

I agree with your ban on `description_append`. That is the right Q7 discipline.

## 4. The open seam: `FixtureDeltaV1` vs Q7 `DerivedSituationState`

This is the place where your current decomposition is still slightly wrong.

**`DerivedSituationState` is overloaded. Split it.**

You need at least two internal objects:

### A. `SituationLocalStateV1`

State tied to the currently active situation.

Examples:

* letter face-down
* phone unlocked
* hand on door handle
* local affordance exhaustion
* local threshold posture

This does **not** survive a situation boundary intact.

### B. `CarryForwardStateV1`

Cross-situation internal residue.

Examples:

* concern intensities
* avoidance momentum entrenched
* rehearsal momentum building
* surfaced memory salience
* blame/defensiveness register
* operator repetition / fatigue
* “world pushback due” counters

This **does** survive a situation boundary.

### C. `FixtureDeltaV1`

External world-authoring change.

Examples:

* second text arrives
* new meeting situation appears
* existing situation current_state patch
* new event or reference marker

This is **not** internal state.

---

### So what happens when a new situation arrives?

A new `FixtureDeltaV1` situation does **not** reset internal progression.

It should trigger a **remap**:

1. Apply accepted deltas to get `UpdatedFixtureViewV1`
2. Compress the previous local state into `CarryForwardStateV1`
3. Initialize a new `SituationLocalStateV1` from:

   * the new situation in `UpdatedFixtureViewV1`
   * the carried-forward internal residue

In code-ish form:

```text
updated_fixture_view = apply_fixture_deltas(base_fixture, accepted_deltas)

carry_forward = summarize_internal_progression(
    previous_situation_local_state,
    recent_operator_path,
    recent_commit_types,
    current_concerns
)

next_situation_local_state = initialize_local_state(
    updated_fixture_view.active_situation,
    carry_forward
)

generation_input = {
  updated_fixture_view,
  carry_forward,
  next_situation_local_state
}
```

### Should the Provocation Generator see internal state?

**Yes.** But not the full raw Q7 object.

Give it a reduced summary, not the whole state dump.

Call it something like:

```yaml
ProvocationContextV1:
  active_situation_id: str
  dominant_concern_ids: [str]
  concern_intensities: {concern_id: float}
  recent_operator_path: [avoidance, avoidance, rationalization]
  carry_forward_tags:
    - ritual_momentum_entrenched
    - harbor_salience_surfaced
    - confrontation_not_crossed
  boundary_count: int
  coverage_targets: [...]
```

That is enough for the world to push back intelligently.

If the Provocation Generator does **not** see this, it will generate generic interruptions.
If it sees the full raw state, the prompt gets noisy and brittle.

### What carries, what resets?

| State kind                               | Example                                                                               | On new situation |
| ---------------------------------------- | ------------------------------------------------------------------------------------- | ---------------- |
| **Carry forward**                        | concern intensities, avoidance momentum, surfaced memory salience, repetition/fatigue | carry            |
| **Recompute from carry + new situation** | target immediacy, interaction phase, available affordances                            | recompute        |
| **Reset**                                | exact object-handling details tied to prior situation                                 | reset            |

Concrete example:

* previous local state: letter turned face-down, harbor memory surfaced, ritual momentum entrenched
* new delta: Eli sends a second text / sister sends a follow-up
* carry: harbor salience, attachment threat, avoidance momentum
* reset/remap: the exact envelope-handling details stop mattering; new local affordances become `read-text`, `ignore`, `draft-reply`

That is the right relationship. Not reset. Not full carry. **Carry + remap.**

## 5. Visualization / playback contract

You need **one normalized runtime packet** and **one optional dashboard overlay**.

Do not let every consumer read raw traces differently.

The broader architecture already wants graph/engine communication to happen as data, not implicit function-call coupling. Keep that discipline here too. 

### `CyclePacketV1` — for renderer, narration, audio, dashboard

Required fields:

```yaml
run_id
cycle
timestamp

node_id
scene_ref
family
goal_type
traversal_intent
selection_reason

tension
energy
hold

situation_ids
primary_situation_id
event_commit_state

dwell_s
visit_count
last_seen_cycle

visual_description
characters
objects
valences
tags

director_feedback_summary
```

### `DashboardOverlayV1` — dashboard only

Optional, richer, still summarized:

```yaml
selected_goal
top_candidates[]
active_indices[]
retrieval_hits[]
emotion_shifts[]
branch_event_summaries[]
```

### Keep out of the visualization contract by default

* full `CausalSlice`
* full `AppraisalFrame`
* full context trees
* raw conductor vector
* beam-search/lookahead internals
* full diagnostic sets

The dashboard is not entitled to raw ontology just because it is called “inner-life.”
It should get a projection, same as every other consumer.

Also: your current runtime kernel already emits most of the right shape. Reuse that. Do not invent a second incompatible packet family.

## 6. The real design choices now

These are the choices that actually matter now:

1. **Freeze Dreamer semantics.**
   Dreamer = runtime bundle. Not just L3.

2. **Freeze the split between external and internal authoring state.**
   `FixtureDeltaV1` = external world delta
   `CarryForwardStateV1` = internal cross-situation residue
   `SituationLocalStateV1` = local active-situation state

3. **Freeze the authoring integration sentence more accurately.**
   Your current one is slightly too clean.

   Better:

   > “The Provocation Generator writes `FixtureDeltaV1`. The authoring loop applies accepted deltas to build an updated fixture view. The generation pipeline combines that view with carried-forward internal state to generate reactions.”

4. **Freeze provenance levels.**
   Separate lane, agent, and scope. Do not mix them in one enum.

5. **Freeze the normalized cycle packet.**
   The dashboard, narration, audio, and visual layers should all read projections of the same packet.

## 7. Cheapest experiment

Do not wire runtime Director into this first. That is unnecessary.

Use one existing authoring fixture. Kai is fine.

### Minimal test

1. Run 2–3 authoring steps normally to accumulate real internal carry state
2. Build `ProvocationContextV1` from that carry state
3. Have the Provocation Generator emit **one** `FixtureDeltaV1`
4. Apply it to make an ephemeral `UpdatedFixtureViewV1`
5. Run one reaction-generation pass from that updated view
6. Compare against a no-delta control

### What to measure

* did operator ranking change?
* did retrieval set change?
* did the generated node open a genuinely new branch, or just paraphrase the prior situation?
* did human keep-rate improve?

### Success condition

At least one admitted candidate should be structurally new because of the delta, not just tonally different.

If the delta only changes wording, the seam is fake.

## 8. Scope boundary: what not to decide yet

Do **not** decide these now:

* whether Provocation Generator and Director should share a voice/model later
* final live L2 runtime engine design inside Dreamer
* conductor physical mapping
* final renderer/audio contract beyond `CyclePacketV1`
* multi-character cross-pressure architecture
* full world-state backend
* final name of the conceptual object above `FixtureDeltaV1`

One last blunt point:

Your current direction is mostly right. The place it still wants to cheat is the seam between external world change and internal carried state. If you do **not** split `DerivedSituationState` now, `FixtureDeltaV1` will slowly absorb internal progression and turn into a junk drawer.

That is the thing I would fix next.
