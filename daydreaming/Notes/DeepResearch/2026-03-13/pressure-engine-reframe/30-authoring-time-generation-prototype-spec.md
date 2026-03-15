# Authoring-Time Generation Prototype Spec

Purpose: specify the smallest implementation that can test whether the
`v1` middle layer produces better graph-ready candidate moments than a
flat prompting baseline.

This note is an implementation spec, not a theory note.

Related:
- `27-authoring-time-generation-reframe.md`
- `28-l2-schema-from-5pro.md`
- `29-worked-trace-kai-unopened-letter.md`
- `21-graph-interface-contract.md`
- `reading-list/13-l2-refactor-synthesis.md`

---

## Prototype question

Can a narrow authoring-time generation loop built from:

- `CharacterConcern`
- `CausalSliceV1`
- `AppraisalFrame`
- `PracticeContextV1`
- operator scoring
- simple episodic retrieval

produce better graph-compilable candidate moments than a flat prompting
baseline on the same seed material?

---

## Success criteria

The prototype succeeds if:

1. the middle-layer arm produces candidate moments that are more
   specific and psychologically grounded than the flat baseline
2. accepted candidates compile cleanly to the graph seam
3. retrieval and middle-layer changes visibly alter operator ranking and
   generated output
4. human curation yield is meaningfully better than hand-authoring or
   flat prompting alone

The prototype fails if:

1. the middle layer does not beat flat prompting
2. graph-compilable output is weak or inconsistent
3. retrieval adds ceremony but not value
4. ablations show one or more middle-layer objects are just paperwork

---

## Scope

### Include

- one character: `Kai`
- one target relation: `sister`
- one active situation: unopened letter requesting a harbor meeting
- three authored backstory episodes
- two active concerns:
  - `attachment_threat`
  - `obligation`
- three operator families:
  - `rehearsal`
  - `rationalization`
  - `avoidance`
- one lightweight practice layer:
  - `evasion`
  - `anticipated-confrontation`
  - `confession`
- exact-match retrieval only
- one generation step at a time
- graph node output plus provenance sidecar

### Exclude

- embeddings
- aesthetic scorer
- multi-character generation
- full `SocialPracticeInstance`
- full OCC runtime taxonomy
- conductor integration
- runtime `L3` traversal
- ATMS / assumption management

---

## Canonical benchmark

Use the scenario from
`29-worked-trace-kai-unopened-letter.md`.

Character seed:

- Kai wants repair, but not at the cost of first exposure
- Kai defaults to delay rituals under attachment pressure

Backstory episodes:

1. last rupture at the harbor
2. older harbor memory of closeness
3. prior avoidance with consequences

Active situation:

- unopened letter from estranged sister
- asks to meet that night at the harbor

## Concern extraction in `v1`

The benchmark may begin from hand-specified active concerns, but the
path from narrative primitives to those concerns must still be recorded.

For `v1`:

- use a tiny theme-rule-style extraction layer
- keep the rules explicit in the benchmark fixture
- allow the benchmark to start from hand-specified concern instances
  after those rules fire

This keeps the first prototype reproducible without pretending concern
extraction has already been solved generally.

Minimum expectation:

- 2-3 explicit concern extraction rules
- a visible mapping from:
  - character seed
  - backstory episodes
  - active situation
  to:
  - initial concerns

The benchmark fixture should therefore contain both:

- extraction rules
- the resulting initial concern state used for the first run

---

## Prototype arms

### Arm A: flat prompting baseline

Inputs:

- character seed
- backstory summaries
- active situation

Prompt shape:

- ask for one candidate moment
- require graph-compilable annotation fields
- no operator label
- no `CausalSlice`
- no `AppraisalFrame`
- no `PracticeContext`

### Arm B: middle-layer generation

Inputs:

- selected concern
- `CausalSliceV1`
- `AppraisalFrame`
- `PracticeContextV1`
- retrieved episode summaries
- selected operator family
- selected affordance tags

Prompt shape:

- ask for one candidate moment consistent with the supplied structured
  state
- require graph-compilable annotation fields
- do not include `EmotionVector`

---

## Required runtime objects

### 1. CharacterConcern

Minimum fields:

- `id`
- `concern_type`
- `target_ref`
- `intensity`
- `unresolved`

### 2. CausalSliceV1

Use the `v1` contract from `28-l2-schema-from-5pro.md`:

- `focal_situation_id`
- `concern_id`
- `target_ref`
- `affected_goal`
- `attribution`
- `temporal_status`
- `likelihood_bucket`
- `self_options`
- `other_options`

### 3. AppraisalFrame

Minimum `v1` fields:

- `desirability`
- `likelihood`
- `controllability`
- `changeability`
- `praiseworthiness`
- `expectedness`
- `temporal_status`
- `realization_status`

### 4. PracticeContextV1

Minimum fields:

- `practice_type`
- `role`
- `phase`
- `affordance_tags`

### 5. Derived EmotionVector

Used only for:

- trace output
- dashboard-style inspection

Not used for:

- prompt input
- durable control state

---

## Retrieval rule

Exact-match retrieval only.

Keys in order:

1. `concern_type`
2. `target_ref`
3. `situation_id`
4. `practice_type`

Scoring:

```text
score(ep) = number of exact key matches
keep up to top 2 episodes with score >= 2
```

Implementation note:

- expose the retrieval threshold and `max_retrieved_episodes` as config
- default:
  - `min_score = 2`
  - `max_retrieved_episodes = 2`

Episode re-entry rule:

- only accepted generated episodes re-enter memory
- they do not re-enter retrieval until the next step or next batch

---

## Operator scoring

Use boring explicit scoring.

Shared structure:

```text
score = w_p * pressure
      + w_a * appraisal_fit
      + w_s * practice_fit
      + w_e * episodic_resonance
      - w_r * repetition_penalty
```

Prototype default weights:

- `w_p = 0.35`
- `w_a = 0.30`
- `w_s = 0.20`
- `w_e = 0.20`
- `w_r = 0.10`

The implementation must emit full score breakdown per family.

---

## Reappraisal/update rule

Use the `v1` rule from `28-l2-schema-from-5pro.md`.

First compute:

```text
I_app = clamp_[0,1](0.6 * |desirability|
                  + 0.25 * likelihood
                  + 0.15 * (1 - controllability))
```

Commit handling:

- `ontic`
  - rebuild `CausalSlice`
  - reappraise
  - set intensity to `I_app`
  - may resolve concern
- `policy`
  - intensity changes by controllability bucket shift
- `salience`
  - intensify `+0.10` or divert `-0.05`
- `none`
  - decay by `* 0.95`

Constraint:

- only `ontic` may resolve concern or change concern type in `v1`

---

## Generation output requirements

Every accepted candidate must produce:

### Graph node payload

- `node_id`
- `node_type`
- `situation_id`
- `delta_tension`
- `delta_energy`
- `setup_refs[]`
- `payoff_refs[]`
- `option_effect`
- `pressure_tags[]`
- `practice_tags[]`
- `origin_pressure_refs[]`
- `source_lane`
- `scope`
- `confidence`
- `revisability`
- `source_ref`

### Provenance sidecar payload

- `node_id`
- `source_concern_ids[]`
- `causal_slice`
- `appraisal_frame`
- `emotion_vector_topk`
- `practice_context`
- `selected_affordance_tags[]`
- `operator_family`
- `operator_score_breakdown`
- `retrieved_episode_refs[]`
- `retrieval_keys[]`
- `commit_type`
- `validator_result`
- `prompt_version`

The graph payload must respect `21-graph-interface-contract.md`.
Do not add `appraisal_summary_tags[]`.

---

## Prototype flow

For each step in Arm B:

1. select dominant concern
2. build `CausalSliceV1`
3. derive `AppraisalFrame`
4. derive `PracticeContextV1`
5. retrieve episodes
6. score operators
7. select operator family and affordance tags
8. prompt generation model
9. validate graph-compilable output
10. attach provenance sidecar
11. choose commit type
12. reappraise affected concerns

For Arm A:

1. prompt generation model from seed material only
2. validate graph-compilable output
3. record candidate and curation decision

---

## Validation rules

Each generated candidate must pass:

1. required field presence
2. valid `option_effect` in canonical vocabulary:
   - `close`
   - `open`
   - `clarify`
   - `none`
3. `delta_tension` and `delta_energy` parse as numeric
4. `pressure_tags[]` and `origin_pressure_refs[]` are non-empty
5. `practice_tags[]` consistent with selected `PracticeContextV1`
6. `source_lane = l2_generation`

Candidates that fail validation are rejected or regenerated once.

---

## Evaluation

### Primary comparisons

Compare Arm A vs Arm B on:

1. candidate specificity
2. psychological grounding
3. graph-compilability
4. curation yield
5. edit burden

### Minimal scoring rubric

For each candidate, rate:

- `specificity`
- `psychological legibility`
- `situational fit`
- `graph readiness`
- `keep / reject`

Simple `1-5` human rubric is enough for the first pass.

---

## Required ablations

These are part of the prototype, not optional later cleanup.

### Ablation 1: no CausalSlice

Prompt from concern + situation only.
If operator rankings and output quality stay the same, cut `CausalSlice`.

### Ablation 2: vary controllability

Change controllability while holding other factors fixed.
If operator scores do not move, `AppraisalFrame` is not doing work.

### Ablation 3: swap practice context

Switch `evasion` to `anticipated-confrontation`.
If affordances and generated node shape do not change, practice context
is not doing work.

### Ablation 4: remove EmotionVector from prompt

This should change nothing because `EmotionVector` should never be in
the prompt for `v1`.

### Ablation 5: remove provenance sidecar during review

If curation decisions are no faster or clearer, the sidecar is not
earning its keep.

---

## Deliverables

The prototype should produce:

1. one benchmark fixture with:
   - character seed
   - backstory episodes
   - active situation
   - initial concerns
2. one trace for Arm A
3. one trace for Arm B
4. at least three ablation traces
5. accepted/rejected candidate set
6. short comparison memo

---

## Non-goals

Do not let this prototype expand into:

- full L2 runtime redesign
- full narrative planner
- world simulation
- multi-step traversal engine
- dashboard UI work
- conductor work
- embedding retrieval
- aesthetic judge loops

This prototype only needs to answer whether the middle layer improves
authoring-time candidate generation.

---

## Immediate implementation order

1. create the benchmark fixture from the worked trace
2. implement Arm A prompt and validator
3. implement `CausalSliceV1`
4. implement `AppraisalFrame`
5. implement `PracticeContextV1`
6. implement retrieval
7. implement operator scoring
8. implement Arm B prompt and validator
9. run the required ablations
10. write the comparison memo

That is the smallest prototype that can answer the question.
