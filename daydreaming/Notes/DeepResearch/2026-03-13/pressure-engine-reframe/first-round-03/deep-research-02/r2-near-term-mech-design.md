You don’t need a new architecture. You need four local mechanisms hung off the current Python harness.

The guardrails are already clear in the repo: `27-authoring-time-generation-reframe.md` says the bottleneck is material supply, not more `L3` theorizing; `28-l2-schema-from-5pro.md` freezes the `v1` middle layer and retrieval/reappraisal rules; `21-graph-interface-contract.md` says live `L2` state stays out of the shared graph; `30-authoring-time-generation-prototype-spec.md` says the prototype stays narrow and graph-compilable. The recent memos and traces say the current middle layer is already doing real work: graph-valid generation is stable, and `CausalSlice` / controllability / practice changes move operator choice in intelligible ways. So the right move is to extend the current pipeline, not replace it.

One cross-cutting rule first: because `authoring_time_generation_prototype.py` is still partly fixture-truth-driven, each new mechanism should land in **shadow mode** first. Compute it, trace it, compare it to the fixture’s seeded values, then let it drive generation once it stops lying.

## 1. Concern extraction from sparse primitives

**Minimum viable mechanism**

Use a **scored rule-abduction layer**, not a full abductive theorem prover.

Concretely: normalize the fixture’s `theme_rules` / `concern_extraction_rules` and `practice_bias_rules` into one internal rule format. Let rules fire over sparse primitives like character contradictions/defaults, active situation facts, backstory episode tags, and a few explicit current-state booleans. Each fired rule contributes weighted evidence to a candidate `(concern_type, target_ref)` pair, plus optional hints for downstream `CausalSlice` construction such as likely threatened goal, likely self-options, and likely other-options.

That is the minimum version of “weighted abduction” that actually fits this harness. Anything bigger is theater.

**How it connects to the current `v1` contract**

It plugs in **before** `select_dominant_concern`, and it outputs the same shape the harness already expects: `extracted_concerns` with `id`, `concern_type`, `target_ref`, `base_intensity`, `source_rule_ids`, `source_episode_ids`, `unresolved`. That keeps you inside the `v1` object contract from `28-l2-schema-from-5pro.md` and the prototype scope in `30-authoring-time-generation-prototype-spec.md`.

It also respects `21-graph-interface-contract.md` because none of this belongs in the graph seam. It is authoring-time inference and trace material, not shared graph residue.

**Concrete implementation step in the current pipeline**

Add:

* `normalize_concern_rules(fixture)`
* `infer_concerns_from_primitives(fixture)`

Then change `run_arm_middle()` from:

* read `fixture["concern_extraction"]["extracted_concerns"]`
* `select_dominant_concern(fixture)`

to:

* `inferred = infer_concerns_from_primitives(fixture)`
* if fixture has seeded concerns, compare `inferred` vs seeded in trace
* optionally choose dominant concern from seeded for regression mode, inferred for exploratory mode

Do not delete seeded concerns yet. Use them as the oracle while the mechanism matures.

Also: unify the fixture schema first. Right now Kai/Maren and Rhea don’t even name the extraction rules the same way. Fix that before you pretend you’re doing inference.

**Failure signal**

It isn’t earning its keep if:

* it cannot recover the fixture-seeded dominant concern on the current regression trio (`Kai`, `Maren`, `Rhea`);
* swapping sparse primitives across fixtures does not change the inferred dominant concern in the expected direction;
* the inferred concern never changes operator choice or generated output relative to the hand-seeded concern path;
* you keep hand-authoring `extracted_concerns` because the inferred path is too noisy to trust.

If it doesn’t beat hand-seeded concern state on those terms, cut it back to explicit seeded concerns and move on.

## 2. Multi-step generation with accumulation

**Minimum viable mechanism**

Implement **accepted-only writeback + one-hop reminding**, not a full kernel port.

The missing thing in the Python harness is not “multi-step cognition” in the grand sense. It is just this:

1. run one middle-layer step,
2. accept or reject the candidate,
3. if accepted, write it back as a retrievable episode for the **next** step,
4. reappraise concerns with the existing `reappraise_concerns()` logic,
5. run retrieval again with current exact-match keys plus a small active-index memory,
6. generate the next step.

That gives you accumulation.

Do **not** immediately re-enter the just-generated node into retrieval for the same step. `28-l2-schema-from-5pro.md` explicitly says accepted generated episodes re-enter on the next step or next batch only. Keep that rule. It is there to stop dumb self-echo.

For reminding, use a **single extra hop** over active indices. The kernel’s `episodic_memory.clj` and walkthrough show the richer target: coincidence counting plus reminding cascade. The Python harness currently has only flat exact-match retrieval. The near-term fix is not a recursive cascade. It is one reminder pass with recency suppression.

**How it connects to the current `v1` contract**

This preserves the `v1` sequence:

`CharacterConcern → CausalSliceV1 → AppraisalFrame → PracticeContextV1 → retrieval → operator scoring → generation → graph projection + sidecar → commit type → reappraisal`

You are not changing the middle layer. You are looping it.

It also stays inside `21-graph-interface-contract.md`: the accumulating memory state, recent indices, recent accepted episodes, and dominance shifts stay in authoring-time runtime state / trace, not in the graph seam.

And it respects the current evidence in `32-authoring-time-generation-comparison-memo.md` and `33-cross-benchmark-generation-memo.md`: the operator path already responds to causal interpretation, controllability, and practice context. So don’t replace that path. Feed it over multiple steps.

**Concrete implementation step in the current pipeline**

Add a wrapper around `run_arm_middle()`:

* `run_generation_sequence(fixture, provider, model, steps=N)`

Maintain a small state object:

* `accepted_episode_bank`
* `recent_episode_ids`
* `active_indices`
* `current_concerns`
* `accepted_nodes`

After each accepted step:

* derive retrieval tags from the accepted node’s sidecar and graph projection:

  * `concern_type`
  * `target_ref`
  * `situation_id`
  * `practice_type`
  * selected operator family
  * maybe setup/payoff refs as secondary indices
* append a short summary to `accepted_episode_bank`
* update `active_indices`
* update concerns with existing `reappraise_concerns()`
* choose the next dominant concern
* rerun retrieval with:

  * current exact-match keys
  * plus one-hop coincidence marks over `active_indices`
  * plus recency exclusion on just-used accepted episodes

The kernel precedent matters here. Copy the idea, not the whole Clojure file:

* coincidence marks,
* recency exclusion,
* one reminder hop.

That is enough for `v1`.

**Failure signal**

It isn’t earning its keep if:

* step 2 and step 3 are basically paraphrases of step 1;
* accepted-node writeback never changes retrieval ranking, operator choice, or generated material;
* the sequence can be reproduced by three independent single-step runs with no shared state;
* reappraisal never changes the dominant concern ordering across steps;
* reminding causes immediate self-loops or repetitive collapse.

Bluntly: if accumulation does not create a trajectory, it is fake.

## 3. Candidate-set compilation

**Minimum viable mechanism**

Use a **hard-filter + soft reranker + subset chooser**. Do not jump straight to MaxSAT.

The current harness generates one candidate and validates it. That is too thin. The minimum useful compiler is:

1. generate `K` candidates for the same step,
2. throw out anything that violates hard seam rules,
3. score the survivors with a small explicit utility function,
4. pick the best `1–2` for admission.

Hard constraints:

* `validate_graph_projection()` passes,
* refs resolve,
* no contradiction with current canon for intended-canonical candidates,
* no duplicate node ids,
* no obvious near-duplicate candidate text / graph projections.

Soft criteria:

* behavioral specificity,
* operator legibility,
* provenance fit,
* graph usefulness,
* novelty relative to already accepted candidates,
* pressure/practice/setup/payoff coverage.

Those four middle criteria are already the right ones. `32-authoring-time-generation-comparison-memo.md` basically tells you the token checker has hit its limit and that stronger evaluation should use human judgment around behavioral specificity, operator legibility, provenance fit, and graph usefulness. So make the compiler proxy those, not surface tokens.

**How it connects to the current `v1` contract**

It sits **after** generation and validation, and **before** writeback / graph admission.

It uses the current graph-native contract from `21-graph-interface-contract.md` and `30-authoring-time-generation-prototype-spec.md`:

* graph payload stays thin,
* provenance sidecar carries the rich middle-layer state,
* no live appraisal or reminder pool leaks into the graph.

This is exactly where a compiler belongs: between candidate supply and seam admission.

**Concrete implementation step in the current pipeline**

Add:

* `generate_candidate_set(...)`
* `score_candidate(...)`
* `compile_candidate_set(candidates, existing_graph_context)`

Practical version:

* sample `K=4–8` candidates by rerunning the same step with different seeds / temperatures / prompt jitter, or by asking the provider for multiple JSON outputs if the provider path allows it;
* reuse `validate_graph_projection()` as the hard gate;
* compute pairwise duplicate penalties from:

  * overlapping `setup_refs`,
  * overlapping `payoff_refs`,
  * same `practice_tags`,
  * same `origin_pressure_refs`,
  * high text overlap;
* score each candidate for:

  * graph validity,
  * operator/provenance consistency,
  * distinctiveness,
  * coverage gain against already accepted nodes.

Then choose the top candidate, or top two if they are genuinely different and both graph-useful.

No solver yet. A greedy reranker is enough to prove the point.

**Failure signal**

It isn’t earning its keep if:

* the compiled winner is almost always the same candidate you’d have taken by naive first-valid or top-local score;
* it does not reduce duplicate/near-duplicate admissions;
* human keep/reject decisions are not faster or clearer with the compiler than with the raw candidate list;
* the compiler’s extra logic never changes the admitted set on the regression fixtures.

If it never changes outcomes, delete it.

## 4. Event boundaries in generated streams

**Minimum viable mechanism**

Use a **boundary detector over state deltas plus a tiny temporal relation set**.

Do not import a full discourse theory stack. The problem is simpler: once you have multi-step generation, you need to know when a stream of successive moments should stay one node and when it should split into two.

Minimum rule set:

Start a new boundary when one of these changes:

* `situation_id`
* temporal mode: present vs prospective vs counterfactual
* threshold state flips:

  * Kai: `letter_opened`, `reply_sent`
  * Maren: `entry_made`, `first_line_spoken`
  * Rhea: `meeting_started`, `door_opened`
* `option_effect` crosses from `none/clarify` to `open/close`
* dominant concern or practice context changes enough to change operator family or available affordances
* the text introduces an irreversible state change

And tag adjacent moments with one tiny relation vocabulary:

* `before`
* `during`
* `after`
* `rehearsal_for`
* `counterfactual_of`

That is enough. Anything bigger is premature.

**How it connects to the current `v1` contract**

This does **not** replace the “one moment → one node” contract. It is what decides when the stream has reached a new moment.

It also respects `21-graph-interface-contract.md`:

* the graph still gets stable fields like `situation_id`, `setup_refs`, `payoff_refs`, `option_effect`, `pressure_tags`, `practice_tags`;
* boundary reasons and temporal labels can stay in trace / sidecar unless you later decide one of them deserves a stable graph field.

This is also consistent with `27-authoring-time-generation-reframe.md`: the problem is producing usable graph material from primitives. Boundary detection is part of making streams compile into usable nodes.

**Concrete implementation step in the current pipeline**

After you have multi-step accumulation, add:

* `detect_boundary(prev_step, curr_step)`
* `infer_temporal_relation(prev_step, curr_step)`

Run them after each accepted step.

If `detect_boundary(...)` is false:

* keep accumulating into the current node draft,
* or mark the new step as a continuation of the current node.

If true:

* finalize the previous node,
* start a new node draft,
* record `boundary_reason` and `temporal_relation` in trace.

Use what the fixtures already give you:

* `current_state` booleans,
* `situation_id`,
* `option_effect`,
* `practice_tags`,
* `origin_pressure_refs`,
* commit type,
* plus a few text-level trigger phrases only where the state model can’t see the transition directly.

This is where `02a_reply.md` was right: event segmentation without some temporal discipline is mush. But the minimum temporal discipline is tiny.

**Failure signal**

It isn’t earning its keep if:

* it cuts every sentence into a node;
* it never cuts until the whole stream turns into sludge;
* setup/payoff mapping does not get cleaner;
* human curators keep manually splitting and merging nodes because the detector’s cuts are wrong;
* adjacent nodes still feel like the same beat, or a single node clearly contains two different beats.

If the curator has to keep rescuing it, the detector is not good enough.

## The short build order

In the current Python harness, the near-term sequence is:

1. `infer_concerns_from_primitives()` in shadow mode
2. `run_generation_sequence()` with accepted-only writeback and one-hop reminding
3. `compile_candidate_set()` over `K` generated candidates
4. `detect_boundary()` + tiny temporal relations over accepted sequences

That stays inside the canonical docs, preserves the current `v1` contract, uses the existing harness instead of waiting for a full kernel refactor, and directly answers the four mechanism gaps.

The main thing to not do is obvious: don’t disappear into elegant imports. The repo already tells you where the pressure is.
