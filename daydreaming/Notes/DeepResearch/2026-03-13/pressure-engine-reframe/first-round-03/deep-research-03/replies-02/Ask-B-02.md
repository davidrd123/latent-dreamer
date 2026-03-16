Bluntly: these are not two vocabularies for one already-coherent system. They are two different cuts over a stack that is still split in code.

Right now the repo has:

1. an **authoring-time L2-style generation harness** (`daydreaming/authoring_time_generation_prototype.py`)
2. a **runtime Director feedback edge** (`kernel/src/daydreamer/director.clj` + `kernel/src/daydreamer/trace.clj`)
3. a **runtime L3 traversal pilot** (`daydreaming/graffito_pilot.py`)
4. a **specified-but-not-yet-central playback/projection seam** (`experiential-design/21-graffito-v0-playback-contract.md`)

The canonical docs unify those with the authored graph as membrane. The missing object is the thing Ask B is circling around: a typed upstream **world/situation provocation proposal** that can sit between “something changed in the world” and “generate character reactions to that change”.

## 1. 2-axis vocabulary map

### Time × function grid

| Function                                             | Authoring / offline                                                                                                                                                                                                         | Runtime / performance                                                                                                                                                                                                                   |
| ---------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **World / design input**                             | **Prep**: `creative_brief.yaml`, `style_extensions.yaml`, `world.yaml` in the experiential docs (`06-prep-layer.md`)                                                                                                        | Runtime world/situation state as consumed by Director and traversal                                                                                                                                                                     |
| **Character cognition / motivated scene generation** | **L2** in the generation sense: `build_causal_slice` → `derive_appraisal_frame` → `derive_practice_context` → retrieval → operator scoring → graph proposal + provenance sidecar (`authoring_time_generation_prototype.py`) | **L2** in the canonical research sense: live cognitive engine in `kernel/`, but this is not the main shipping runtime yet                                                                                                               |
| **Authoring orchestration / curation**               | **L1**: not a separate engine in code, but functionally present as batch running, admission, keeper banking, and curation workflow around the generation harness                                                            | none                                                                                                                                                                                                                                    |
| **Traversal / drama management**                     | none                                                                                                                                                                                                                        | **L3**: actual Python pilots with `TraversalState`, feature scoring, situation activation, event approach counts, conductor bias, and emitted `traversal_intent` (`graffito_pilot.py`)                                                  |
| **Interpretive perturbation**                        | none implemented                                                                                                                                                                                                            | **Director**: brief-grounded runtime echo agent producing `director_concepts`, `situation_boosts`, `valence_delta`, `surprise`, optional `emotional_episodes`, and `interpretation_note` (`12-director-prompt-spec.md`, `director.clj`) |
| **Projection / playback / stage**                    | graph compilation output                                                                                                                                                                                                    | adapter / playback join / normalized cycle packet / narration / renderer / audio / dashboard (`21-graffito-v0-playback-contract.md`)                                                                                                    |

### The important reconciliation

* **Prep** has no L-number. It is upstream authored input.
* **L1** has no clean experiential-design name. It is the authoring-time orchestration/candidate-management layer.
* **Dreamer** is overloaded. In the experience docs it means “the internal system.” In the technical docs it should be split into:

  * **L2** = cognition / generation logic
  * **L3** = traversal scheduler
* **Director** has no L-number. It sits beside the runtime Dreamer stack, not inside L2 or L3.
* **Stage** also has no L-number. It is downstream of adapter/projection.
* The **adapter/playback join** is the missing named layer in the experiential vocabulary. It is explicit in the canonical docs, but easy to lose when people say “Dreamer → Director → Stage”.

So the clean reconciliation is:

* **Prep** = upstream authored inputs
* **L1/L2** = authoring lane
* **L3 + Director + conductor bias + adapter** = runtime lane
* **Stage** = downstream consumers

That is the canonical map. The current code only partially instantiates it.

## 2. Three coherent versions of the architecture

## Version A — **As-built split system**

**Best match to the repo right now**

### Authoring-time loop

* benchmark fixture YAML
* middle-layer generation harness
* graph proposal + provenance sidecar
* human/batch admission
* keeper bank / pack assembly

This is real code now. It is not wired to the runtime Director.

### Performance-time loop

* authored graph / micrograph
* L3 traversal pilot
* reduced Dreamer packet
* runtime Director echo
* lane-local state update for next cycle
* trace/debug/playback-ish outputs

This is also real code now.

### Shared contracts actually present

* graph proposal fields and provenance sidecar from the generation harness
* `dreamer-state-packet` from `trace.clj`
* Director feedback schema from `12-director-prompt-spec.md` and `director.clj`
* traversal debug outputs from `graffito_pilot.py`

### What breaks

* no typed source of new world events for authoring-time generation
* no principled bridge from Director outputs into the generation pipeline
* no unified world-definition layer
* “Dreamer” means different things in different docs

### My assessment

This is the honest current state. Don’t pretend it’s already the unified two-lane system.

---

## Version B — **Canonical two-lane membrane architecture**

**Best match to the docs**

### Authoring-time loop

* source bible / world sketch
* L1 authoring assistance over L2-style motivated generation
* human curation
* authored graph

### Performance-time loop

* authored graph
* L3 traversal scheduler
* conductor bias
* Director feedback
* adapter / normalized cycle packet
* dashboard / narration / renderer / music / stage

### Shared contracts

* frozen graph interface contract
* thin runtime playback packet
* Dreamer→Director packet
* Director feedback schema

### What breaks

The docs still duck the upstream “who actually creates new situation/event material?” question. That is why Ask B feels slippery.

In particular, several docs talk as if the Director can “introduce events” or “rearrange the world”. The **implemented** Director does not do that. It updates lane-local activation/indices/valence-like state for the next cycle. That mismatch is real, not semantic.

### My assessment

This is the right architecture frame, but one crucial interface object is missing, so the story smears.

---

## Version C — **Recommended next-phase architecture**

**Canonical frame + one missing typed object**

Add a new authoring-time object:

## `SituationProvocationProposal`

It is **not** the current runtime Director output. It is **not** a direct fixture rewrite. It is **not** a graph node yet.

It is a typed proposal sidecar that says, in effect:

> “Given base situation X, here is a candidate pressure/world/knowledge shift that the generation pipeline should react to.”

### Proposed shape

You do not need to freeze field names yet, but conceptually:

* `id`
* `base_situation_id`
* `provocation_type`
  (`external_event`, `knowledge_shift`, `pressure_shift`, `threshold_shift`)
* `commit_semantics`
  (`ontic`, `policy`, `salience`)
* `fact_deltas[]` or `state_overrides{}`
* `successor_situation_stub?`
* `pressure_tags[]`
* `source_lane`
* `scope: proposal`
* `confidence`
* `revisability`
* `source_ref`

### Who writes it

* **now:** human or a very narrow authoring-time LLM prompt
* **later:** a Director-like provocation generator

### Who reads it

* generation pipeline
* later, graph compiler / curation tooling

### Mutability

* append-only as proposal ledger during exploration
* compiled or discarded after review
* not silently merged into canon

### Where it lives

* **short term:** separate sidecar artifact next to the fixture / batch bundle
* **not** in the base fixture
* **not** in the runtime packet
* **not** directly in the shared graph seam until curated

### Why this is the right missing object

It gives you a typed bridge between:

* hand-authored world/event design
* future authoring-time “Director” provocations
* lane-local generation-time state overlays

without letting any of them directly mutate canon.

That fits the repo’s existing discipline:

* generation harness already distinguishes graph payload vs provenance sidecar
* canonical docs already insist live L2/L3 state stay out of the graph
* Q7 already says the missing bridge should be lane-local `DerivedSituationState`, not graph mutation

### My assessment

This is the version that should govern the next phase.

Not because it is elegant. Because it is the smallest honest extension of what exists.

## 3. What provides world updates?

This needs a direct answer, because the docs blur it.

### Implemented now

* **Hand authoring** provides actual world/event material.
* **Generation pipeline** does **not** mutate canon. It produces candidate graph nodes and provenance. It carries forward memory and `current_active_situation_id`, but not a principled world-state update.
* **Current Director** does **not** provide world updates. It perturbs lane-local runtime state: situation activation, recent indices, valence-ish situation fields, emotional episodes, serendipity bias.

### Canonical docs claim

* only explicit commits/events should mutate world state
* graph is membrane
* L3 ordinary selection is not a commit
* runtime state stays lane-local

That part is right.

### Still only proposal

You need three distinct update layers:

1. **Hand-authored canon/events**
   Source of truth. Actual world updates.

2. **`DerivedSituationState`**
   Lane-local authoring-time progression overlay.
   This is Q7’s missing mechanism. It should affect the next `CausalSlice`, not pretend canon changed.

3. **`SituationProvocationProposal`**
   Candidate authored-world or successor-situation shift, human-gated before compilation.

That separation matters because only **ontic** changes should count as world mutation. Salience/policy shifts are not canon changes.

## 4. Director at authoring time vs performance time

Do not use one mushy word for three roles.

| Role                                              | Status    | What it does                                                      |
| ------------------------------------------------- | --------- | ----------------------------------------------------------------- |
| **Current implemented Director**                  | real      | runtime interpretive perturbation                                 |
| **Possible authoring-time provocation generator** | not built | proposes typed world/situation shifts for the generation pipeline |
| **Hand-authored world/event design**              | real      | actual source-of-truth authorship                                 |

### Are authoring-time and runtime Director the same agent?

Not in any useful technical sense.

Use the same model family and the same brief if you want. Fine. But they should be **different configurations with different contracts**:

* **Runtime Director**
  lossy, tiny, fast, no world-mutation authority

* **Authoring-time provocation generator**
  batch/offline, typed proposal output, allowed to suggest world/situation change proposals

Treat them as siblings, not one agent operating at two timescales.

If you call both “Director”, you will keep confusing interpretive echo with world authorship.

## 5. Brief ↔ fixture unification

### Implemented now

They are separate artifact families.

* Prep side: brief / style / world
* Generation side: benchmark fixture YAMLs with characters, backstory episodes, situations, concern extraction, benchmark steps, expected practice contexts, worked-trace scaffolding

Those are not the same thing.

### Canonical docs imply

Also separate. `06-prep-layer.md` clearly gives brief/style/world different jobs from generation fixtures.

### Recommendation

**Do not merge them now.**
**Do move toward a shared source bundle with compiled views later.**

The right long-term shape is:

* **shared authored world source**

  * world facts/entities/situations/events
  * brief
  * style/motifs

compiled into:

* **runtime view** for Director / traversal / stage
* **generation-fixture view** for authoring-time L2 harness
* **graph-compilation view** for curation and admission

Why not merge now?
Because the current generation fixtures contain experimental scaffolding (`benchmark_step`, expected retrieval candidates, worked-trace references, etc.). That is not world canon. Stuffing that into one mega-file would rot instantly.

So:

* **short term:** keep separate
* **medium term:** shared source bundle + compiled views
* **do not:** unify by brute-force file merge

## 6. Visualization contract

The current `dreamer-state-packet` is too thin for a viewer-facing inner-life surface. It is a **Director-facing packet**, not a visualization contract.

### Minimum visualization contract should subscribe to four streams

## A. `cycle_packet`

The joined packet for “what is happening now”

Minimum fields:

* `cycle`
* `node_id`
* `scene_ref`
* `situation_ids`
* `family` / `goal_type`
* `tension`
* `energy`
* `dwell_s`
* `visit_count`
* `last_seen_cycle`
* `tags` / `valences`
* `traversal_intent` if available

This is basically the playback-contract packet plus `traversal_intent` when the runtime can supply it.

## B. `selection_record`

Why this node was chosen

Minimum fields:

* `previous_node_id`
* `selected_node_id`
* `traversal_intent`
* shortlist / candidate scores
* reason / diagnostics
* conductor bias snapshot

This can come from the L3 debug row. You do not need full beam-search internals.

## C. `director_feedback`

Exactly the existing runtime Director schema:

* `director_concepts`
* `situation_boosts`
* `valence_delta`
* `surprise`
* `emotional_episodes`
* `interpretation_note`

## D. `state_snapshot`

The slow state that makes the system legible across cycles:

* `situation_activation`
* `recent_path`
* `event_approach_count`
* maybe `recent_emotional_trajectory`

### Optional fifth stream

If the Clojure kernel becomes live runtime:

* `activations`
* `branch_events`
* `emotion_shifts`
* `emotional_state`

### What not to expose directly

* raw fixture parsing
* full L2 scratch state
* current concern stack in graph form
* lookahead internals
* the entire conductor vector if it is not actionable on screen

## 7. Real design choices

These are the ones that matter.

### 1. Does the current runtime Director get world-mutation authority?

My answer: **no**. Not now.

It is currently an interpretive perturbation agent. Let it stay that way until the event/provocation schema exists.

### 2. What is the typed upstream object between “world change” and “generate reactions”?

This is the big one.
My answer: **`SituationProvocationProposal` + lane-local `DerivedSituationState`**.

### 3. What is the actual source of truth?

Right now it is fragmented across:

* prep docs
* benchmark fixtures
* graph fixtures
* trace fixtures

You do not need one file now, but you do need a decision that **benchmark fixtures are not canon**. They are compiled/task views.

### 4. Is `traversal_intent` debug-only or stable stage vocabulary?

This one is real. Current code emits it in debug/traces. Canonical docs want it in the runtime vocabulary. Decide that explicitly.

My take: keep it **debug-first now**, but design the playback packet so it can be added cleanly.

### 5. Is “Dreamer” a technical component name?

For experience docs, maybe.
For architecture docs, **no**. Split it into L2 and L3 when you need precision.

## 8. Cheapest experiment

Do **not** start with “make the runtime Director generate events”. That is not the cheapest test.

The cheapest test is:

## hand-authored provocation → fixture overlay → generation pass

Concretely:

1. Finish the current `supply_v1` sprint first. Do not violate the frozen-recipe discipline.
2. Pick one existing fixture, probably **Kai** or **Tessa**.
3. Hand-author **2-3 `SituationProvocationProposal` sidecars**:

   * one salience sharpen
   * one threshold/knowledge shift
   * one ontic successor stub
4. Overlay one proposal into the generation harness **before** `build_causal_slice()`.
5. Run:

   * one short multi-step sequence
   * one small batch
6. Evaluate:

   * did the next-step material change in the intended direction?
   * did graph projections stay valid?
   * did keeper yield hold up?
   * did Q7-style progression become more real?

Only after that works should you let an LLM emit the same proposal schema.

That experiment resolves the highest-value ambiguity:

* what the interface object is
* where it lives
* whether the pipeline can actually consume it

without touching runtime Director, L3, stage, or big schema unification.

## 9. What not to decide yet

* whether authoring-time provocation and runtime Director share one persona/prompt
* whether runtime Director can directly commit events
* full brief/fixture/world unification schema
* whether Clojure L2 becomes the live Dreamer
* whether `traversal_intent` must be in the stage packet immediately
* broad multimodal world-building / DMPOST integration
* full conductor mapping

Those are real questions, but they are downstream of the missing interface object.

## Bottom line

The clean answer to Ask B is:

* **Implemented now:** split system
  authoring-time generation harness, runtime Director echo, deterministic L3 pilots

* **Canonical docs claim:** two-lane architecture
  authored graph as membrane, runtime L3 + Director + adapter into stage

* **Still only proposal:** typed upstream world/situation provocation bridge
  plus lane-local derived situation state for real sequence progression

If you force one sentence:

> The current Director is a runtime interpretive perturbation agent, not a world-event author; the generation pipeline is an authoring-time L2-style candidate factory, not a full world simulator; and the next missing seam is a typed `SituationProvocationProposal` sidecar that lets authoring-time world changes exist as proposals before they are compiled into the graph.

Look away for a second. Unclench your jaw.
