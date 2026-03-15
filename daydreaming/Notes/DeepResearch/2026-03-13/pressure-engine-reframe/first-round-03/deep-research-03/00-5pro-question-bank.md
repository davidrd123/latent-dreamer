# 5 Pro Question Bank

Date: 2026-03-15

Purpose: collect the highest-value narrow questions to send to GPT-5
Pro from the current plateau. Each question is written to be
standalone so it can be run in parallel without reconstructing the
whole project context.

Use: say "answer question X" and send the corresponding prompt block
below, plus the listed attachments.

---

## Shared context

Current state:

- the authoring-time generation prototype exists in
  `daydreaming/authoring_time_generation_prototype.py`
- Kai, Maren, and Rhea are benchmark fixtures
- avoidance and rehearsal are validated on the derived path
- rationalization is in progress via
  `daydreaming/fixtures/authoring_time_generation_tessa_toast_rationalization_v1.yaml`
- inferred concerns are the normal path
- `--use_expected_reference` is calibration-only
- the next scaling move is batch generation -> admission -> human
  curation
- the narrow L3 result is already strong enough; do not reopen the L3
  architecture unless the question explicitly asks for it

Default answer shape to request:

1. findings first
2. smallest viable patch or experiment plan
3. what not to change yet

Recommended order:

1. `Q2` rationalization benchmark review
2. `Q1` batch admission and curation packet design
3. `Q3` generation quality and acting-craft review
4. `Q4` end-to-end generate -> curate -> traverse experiment design

If only one question goes out now, send `Q2`.

---

## Q1. Batch Admission Policy Design

### Goal

Design the smallest authoring-batch generation and graph-admission
pass that can scale the current prototype without jumping to
embeddings, solvers, or a refactor.

### Attach / point to

- `daydreaming/authoring_time_generation_prototype.py`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/28-l2-schema-from-5pro.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/30-authoring-time-generation-prototype-spec.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/31-generation-experiment-review-checklist.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/33-cross-benchmark-generation-memo.md`
- one or two representative `sequence.trace.json` files if available

### Prompt

```text
Please design the smallest authoring-batch generation and graph-admission pass for the current prototype.

Context:
We have an authoring-time generation prototype that produces graph-compilable candidate moments one at a time. We are moving to batch generation:
- multiple short sequences per character
- roughly 15-25 candidates per batch
- curate down to a smaller subset worth graph admission and human review

This is not a request for a major architecture rethink. Do not propose embeddings, a solver, or a kernel refactor yet.

The current seam is frozen. Each candidate has:
- graph projection: node_id, situation_id, delta_tension/energy, setup_refs, payoff_refs, option_effect, pressure_tags, practice_tags, origin_pressure_refs, provenance fields
- provenance sidecar: concern ids, causal slice, appraisal frame, practice context, operator family, retrieved episodes, commit type, score breakdown

Please answer:

1. What should the batch unit be?
2. What are the hard gates?
3. What are the soft preferences?
4. How should near-duplicates be handled?
   - same operator
   - same concern
   - similar scene texture
5. How should overdetermination be scored across a batch, not just per-candidate?
6. What should the simplest greedy admission pass look like before any solver exists?
7. What should the human curation packet contain?
8. What failure signals would justify moving to a soft-constraint admission compiler later?

Please give:
1. findings first
2. a minimal architecture sketch
3. a concrete prototype order
4. what not to build yet
```

---

## Q2. Rationalization Benchmark Review

### Goal

Check whether the new rationalization fixture and current prototype
actually test rationalization on the derived path, or whether the code
is still structurally biased toward prospective / rehearsal-style
interpretation.

### Attach / point to

- `daydreaming/authoring_time_generation_prototype.py`
- `daydreaming/fixtures/authoring_time_generation_tessa_toast_rationalization_v1.yaml`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/28-l2-schema-from-5pro.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/30-authoring-time-generation-prototype-spec.md`
- optionally `tests/test_authoring_time_generation_prototype.py`

### Prompt

```text
Please do a narrow design/code review of the rationalization benchmark.

Context:
The current authoring-time generation prototype has avoidance and rehearsal benchmarked. We added a fourth fixture meant to validate rationalization:
daydreaming/fixtures/authoring_time_generation_tessa_toast_rationalization_v1.yaml

The goal is not generic critique. I want to know whether this fixture and the current prototype actually test rationalization on the derived path, or whether the code is still structurally biased toward prospective/rehearsal-style interpretation.

Please answer:

1. Does the Tessa fixture represent the right kind of rationalization case?
   - actual, not prospective
   - damage already done
   - low controllability
   - high self-blame / praiseworthiness pressure
   - reframe as the live move

2. What makes rationalization behaviorally distinct from avoidance in generated scene output?
   - what does a rationalization scene look like as action under pressure, not internal monologue?
   - what appraisal profile should favor rationalization over avoidance?
   - what practice context makes rationalization the legible move?

3. Does the current prototype logic support that case, or is it still encoding the wrong causal/appraisal structure?

4. What is the smallest correct patch set to make rationalization a real benchmark?
   - CausalSlice changes?
   - Appraisal changes?
   - PracticeContext changes?
   - operator scoring changes?
   - affordance selection changes?
   - semantic checks?

5. What would a good ablation look like?

6. What should not be changed yet?

Please give findings first, then the smallest viable patch plan.
```

---

## Q3. Generation Quality And Curation Review

### Goal

Judge the actual generated material, not just the mechanics, before the
system scales to larger batches.

### Attach / point to

- actual Gemini outputs from the best Kai and Rhea runs:
  - prompt files
  - response files
  - `sequence.trace.json`
  - sidecar files if useful
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/28-l2-schema-from-5pro.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/31-generation-experiment-review-checklist.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/32-authoring-time-generation-comparison-memo.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/33-cross-benchmark-generation-memo.md`
- `daydreaming/Notes/explainers/landscape-map-2026-03-15.md` if useful

### Prompt

```text
Please review the quality of the current authoring-time generation outputs, not just the architecture.

Context:
We have built and validated the authoring-time generation prototype. Avoidance is proven, rehearsal is proven, rationalization is in progress. Concern inference from primitives works at benchmark scale. Multi-step accumulation produces real carry-forward. Candidate compilation can select non-trivial candidates.

We are about to scale from single-benchmark validation to batch generation and human curation.

I am attaching the actual generated Gemini outputs from the best Kai and Rhea runs:
- prompt files
- response files
- sequence.trace.json
- sidecars where useful

Please answer:

1. Is the prose quality good enough that a human curator would keep these moments for a dream graph?
2. Where does the quality break?
   - scene texture
   - psychological specificity
   - graph annotation accuracy
   - repetition
   - something else
3. Does the operator actually show up in behavior?
   - Kai: avoidance
   - Rhea: rehearsal
   Or are the moments only labeled correctly while the prose stays generic?
4. Does multi-step accumulation produce visible trajectory?
   - does step 3 feel meaningfully shaped by step 1?
   - or are the steps mostly paraphrases?
5. Do the moments read as action under pressure, or as emotional narration / indicating?
6. What should the curation rubric be for the next phase?
7. At batch scale, what failure modes should we expect that single-candidate tests will not reveal?
8. Is the current greedy compiler enough for the next step, or does batch scale immediately justify a stronger admission policy?

Please give:
1. findings first
2. a fast curation rubric
3. the most dangerous scaling risks
4. what not to spend time on yet
```

---

## Q4. End-To-End Generate -> Curate -> Traverse Experiment

### Goal

Design the smallest falsifiable experiment that connects the
authoring-time generation lane to the L3 traversal lane.

### Attach / point to

- `daydreaming/authoring_time_generation_prototype.py`
- `daydreaming/graffito_pilot.py`
- `daydreaming/city_routes_pilot.py`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/11-settled-architecture.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/13-execution-roadmap.md`
- optionally `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/19-city-routes-arms-a-b-pass.md`
- optionally `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/20-city-routes-arm-c-pass.md`

### Prompt

```text
Please propose the smallest falsifiable end-to-end experiment that connects the authoring-time generation lane to the L3 traversal lane.

Context:
L3 is proven enough on authored graphs. The generation prototype can produce graph-compilable candidate moments. The missing experiment is:
generate -> curate/admit -> traverse

I do not want a broad architecture rethink. I want the smallest experiment that tells us whether generated material can survive curation and still support meaningful traversal.

Please answer:

1. What is the smallest end-to-end substrate?
2. How many generated nodes are enough?
3. What curation/admission rules should be used?
4. What traversal comparison should be run?
   - generated-then-curated material
   - versus hand-authored material
5. What are the success and failure conditions?
6. What would make this result actually informative rather than just a demo?
7. What should be instrumented so we can tell whether failure came from generation, admission, or traversal?

Please keep it concrete and experiment-shaped.
Give findings first, then the smallest viable experimental design.
```

---

## Notes

- `Q1` and `Q2` are the most immediately actionable.
- `Q3` is the best external quality check before scaling hard.
- `Q4` matters once batch generation and admission exist in practice.
- Do not send a broad "what else should we read?" request right now.
- Prefer narrow review/design asks with actual files attached.
