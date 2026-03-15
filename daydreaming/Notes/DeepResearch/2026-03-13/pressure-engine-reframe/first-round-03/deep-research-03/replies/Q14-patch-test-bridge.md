The right first patch test is narrower than people want. Do not ask Kai to prove generated whole-graph authoring. Ask one thing only: can four generated-curated kitchen beats replace one authored kitchen patch inside a tiny Kai micrograph and still survive L3 traversal? That is already the direction in the existing bridge memo, and it matches the actual Kai signal: the pool is mechanically valid and curator-usable, but narrow, mostly precontact avoidance, weak on progression and closure (`replies/Q4-end-to-end.md`; `34-kai-batch-curation-packet.md`).

## 1. MVP recommendation

Build **two versions of the same 10-node Kai micrograph**.

**World scope**
Use only the live Kai fixture territory:

* `sit_unopened_letter`
* `sit_threshold_departure`
* `ev_harbor_meeting_tonight`
  (from `authoring_time_generation_kai_letter_v1.yaml` and `29-worked-trace-kai-unopened-letter.md`)

**Graph shape**

* **6 authored scaffold nodes**
* **4 replaceable patch nodes**

**Patch region**

* Precontact kitchen only
* All 4 patch nodes live in `sit_unopened_letter`
* Exactly **1** patch node is allowed to point its `payoff_refs` toward `sit_threshold_departure`
* **0** patch nodes should directly fire the harbor event

That matters because current Kai generation is good at avoidance texture and bad at closure. So do not make the generator prove departure or harbor commitment yet. Keep threshold and event-approach logic in the authored scaffold, where L3 is already stable (`34-kai-batch-curation-packet.md`; `00-5pro-question-bank.md`).

**Concrete scaffold / patch role split**

Scaffold nodes:

1. kitchen entry/start node
2. threshold bridge node
3. doorway hesitation node
4. threshold fallback / return node
5. harbor-approach node
6. release-or-stall node

Patch nodes:

1. kitchen avoidance anchor
2. kitchen avoidance intensifier
3. contrast ritual variant
4. bridge-to-threshold beat

**Supply procedure**
Use the existing generation harness, not a new subsystem:

* `run_middle_sequence()`
* `compile_candidate_set()`
* `build_batch_pool_row()`
* `admit_candidate_pool()`

Smallest honest batch:

* **4 sequences**
* **2 steps each**
* **2 candidates per step**

That gives **16 raw generations** total. Because `compile_candidate_set()` picks one selected candidate per step, you get at most **8 pooled accepted nodes**, then final admission narrows that to **4 keepers** (`authoring_time_generation_prototype.py`).

So the keeper bar is:

> **4 distinct keepers from ~16 raw candidates, with light edit burden, and at least one usable bridge-to-threshold node.**

If you cannot get that, stop there. That is failure.

**What generated nodes already provide**
The current generation seam already gives:

* `delta_tension`
* `delta_energy`
* `setup_refs`
* `payoff_refs`
* `option_effect`
* `pressure_tags`
* `practice_tags`
* `origin_pressure_refs`
* provenance sidecar

That is enforced by `validate_graph_projection()` and the frozen seam docs (`authoring_time_generation_prototype.py`; `21-graph-interface-contract.md`; `30-authoring-time-generation-prototype-spec.md`).

**What the curator still has to add**
For traversal, the patch nodes still need authored graph fields that the generator does not supply:

* `situation_ids`
* `line_id`
* `subplot_id`
* `availability_test`
* `priority_tier`
* `importance`
* `resolution_path_count`
* `dwell_s`
* optional `event_id` / `event_commit_potential`
* explicit edges

That is normal. `graffito_pilot.py` consumes those traversal-facing fields; generation does not currently emit them.

**H vs G conditions**

* **H**: fully hand-authored kitchen patch
* **G**: same scaffold, same topology slots, but the 4 kitchen patch nodes are generated-curated

Keep the scaffold fixed. Keep the patch slot count fixed. Keep the start node fixed.

**Traversal run**
Use the current **feature arm** from `graffito_pilot.py`, because that is the real L3 scorer:

* same `mode="feature"`
* same start node
* same cycle budget: **12**
* same **3 fixed seeds**
* same neutral conductor settings
* same `PilotConfig`

Run H×3 seeds and G×3 seeds, exactly like the same-graph/same-seed comparison style in `city_routes_pilot.py`.

## 2. Why it works

This works because it isolates the actual unknown.

The generator already makes graph-valid Kai material and the batch admission path already exists. The question is not “can it write sentences?” or “can it emit seam fields?” Those are mostly settled. The question is whether curated generated material can occupy a live graph slot and still behave under `choose_feature()` selection pressure, where candidates are scored by trajectory fit, preparation satisfaction, situation mixing, event homing, recall value, structural tension, and overdetermination (`graffito_pilot.py`).

This design also matches the real Kai weakness. The curation packet says the batch is narrow: good domestic ritual texture, weak progression, weak closure, almost no practice spread (`34-kai-batch-curation-packet.md`). So the patch should use generated material where it is already strongest: the kitchen avoidance lane. Threshold progression and harbor-approach stay scaffolded.

The result is clean failure attribution:

* bad supply → generation failed
* good supply but unusable patch → admission/curation failed
* usable patch but bad traversal → traversal integration failed

That is what you want from a bridge experiment.

## 3. Failure modes

### Generation failure

Any of these count:

* fewer than **4** distinct keepers from ~16 raw candidates
* no keeper that can serve as the bridge node
* curator has to rewrite prose substantially
* curator has to rewrite `option_effect`, `setup_refs`, or `payoff_refs` for more than one node
* all keepers collapse to the same dramatic function

That last one matters. If the four “keepers” are just sink-scrub / grout-scrub / silverware-polish / coffee-maker-clean and nothing else, the generator gave you atmospheric supply, not a patch.

### Admission failure

Good candidates exist, but they do not compile into a coherent 4-slot patch without hand-authoring away the problem.

Concrete signs:

* the admitted set cannot cover the four fixed roles
* the patch needs new seam fields or invented refs
* you have to change topology to rescue the generated nodes
* you have to hand-normalize all the traversal-facing annotations instead of lightly slotting them

If you are effectively authoring a new graph around the prose, that is not a patch test. That is disguised manual graph writing.

### Traversal failure

The G graph inserts cleanly, but L3 tells you it is not usable.

Measure:

* **patch visitation count**
* **first threshold reach cycle**
* **first harbor-approach reach cycle**
* **first pathological loop cycle**
* **revisit gap** for patch nodes
* whether patch `payoff_refs` are actually paid off downstream
* whether pressure continuity holds across the selected path

Success bar:

* both situations visited in **at least 2 of 3 seeds**
* threshold / event-approach region reached in **at least 2 of 3 seeds**
* no tight 2-node loop before **cycle 8**
* generated patch nodes are actually selected, not dead leaves

Do **not** measure conductor responsiveness in this first test. That is a confound. Keep conductor neutral. The first bridge is about traversal survivability, not performance control (`replies/Q4-end-to-end.md`).

## 4. Higher-octane design

The smallest useful extension is **not** “patch two Kai regions.”

That mostly repeats the same narrow failure mode, because Kai is still avoidance-heavy and weak on closure (`34-kai-batch-curation-packet.md`).

The next experiment that increases confidence is:

> **Run the exact same single-region patch test on Tessa.**

Why Tessa:

* different operator family: rationalization instead of avoidance
* actual-damage / aftermath structure instead of precontact hesitation
* better test of whether the bridge works across operator families rather than one cozy Kai lane
  (from `session-2026-03-15-generation-pipeline.md` and the shared context in `01-5pro-question-bank.md`)

So the sequence should be:

1. Kai single-region patch
2. Tessa single-region patch
3. only then consider a two-region patch in one scaffold

If both Kai and Tessa pass, then the next integration test is:

* one scaffold
* two generated patches
* one pre-event region and one aftermath/threshold region

But not before.

A second useful extension, after Kai passes, is to let generated nodes beat the hand-authored baseline on **multi-pressure projection** when warranted. If a generated node clearly carries both `attachment_threat` and `obligation`, let it keep that richer projection instead of flattening it. But that should be recorded as bonus signal, not made the primary variable in the first bridge.

## 5. Connection to other questions

**Q6 seam richness**
Do not block the Kai patch on richer projection. The first bridge can run on the current seam. But log when the prose and sidecar clearly justify multi-pressure projection and the graph flattens it. That will tell you whether the patch is surviving despite seam loss, or because the seam happened not to matter yet.

**Q7 scene-state carry-forward**
Not required for the first patch. The scaffold handles threshold progression. That is exactly why the patch should stay in the kitchen. Do not ask the current Kai generator to prove real scene-state advancement it does not yet reliably produce.

**Q8 / Q13 retrieval balancing**
Relevant, but not blocking. The first patch only needs four good kitchen keepers. Retrieval self-priming matters more when you try to widen the pool or get later-step functional diversity.

**Q5 / Q12 operator and practice stationarity**
Also not blocking for Kai MVP. Kai is an avoidance lane test. Do not turn the first bridge into an operator-diversity benchmark. That is a different experiment.

**Q9 intervention ordering**
The patch test belongs after the pipeline can honestly produce four distinct keepers. It does **not** need scene calculus, POCL, or a stronger conductor. It **does** need the current admission hardening and a keeper set that is not pure duplicate fog.

## 6. What NOT to build yet

Do not build:

* full generated Kai graph
* two-region Kai patch
* live graph repair
* solver-based admission
* conductor A/B presets
* richer rendering as part of pass/fail
* new seam fields
* event-node generation
* whole-world traversal comparisons against City Routes or Graffito

The first bridge should be boring:

* one Kai micrograph
* one authored scaffold
* one replaceable 4-node kitchen patch
* ~16 raw candidates
* 4 admitted keepers
* H vs G
* same L3 feature arm
* same seeds
* same neutral conductor

If that fails, you learn something precise. If it passes, you have earned the next bridge.
