# Generation Experiment Review Checklist

Purpose: catch contamination and experimental design errors before
running LLM-mediated generation comparisons.

Run this checklist against both prompts and evaluation criteria
before every comparison run.

---

## Prompt review

### 1. No behavioral prescription

Does the prompt tell the model what the output should look like
behaviorally?

- "the selected operator should be visible in behavior" — FAIL
- "show avoidance as action, not absence" — FAIL
- "the moment should carry the weight of the retrieved memories" — FAIL

The middle-layer inputs should *cause* these qualities. The prompt
should not *instruct* them. If removing all middle-layer inputs
leaves the prompt still describing the desired behavior, the prompt
is doing the work.

### 2. Symmetric context between arms

Is everything that isn't the middle layer identical between Arm A
and Arm B?

- Same character description — CHECK
- Same situation description — CHECK
- Same backstory summaries — CHECK
- Same artifact framing (what this is, what kind of output) — CHECK
- Same format constraints — CHECK

If Arm B gets richer character context, more backstory, or
different framing, the comparison tests context quantity, not
middle-layer quality.

### 3. No answer leakage from fixture

Does the prompt or system message include:

- Expected operator outcome — FAIL
- Expected affordance selection — FAIL (for Arm A)
- Reference exemplar text — FAIL
- Expected graph field values — FAIL

The model should only see inputs, never expected outputs. The
fixture's `expected_*` fields are for validation after generation,
not for prompt construction.

### 4. Operator semantics are descriptive, not prescriptive

The Arm B prompt should explain what the operator *means*
psychologically, not what the output *should contain*.

- "Avoidance means choosing not to engage with what demands
  engagement" — OK (describes the cognitive move)
- "The scene should show non-engagement as action" — BORDERLINE
  (prescribes the output form)
- "Include a delay ritual like making coffee" — FAIL (prescribes
  specific content)

The line: explain what the operator IS, not what the scene
SHOULD LOOK LIKE.

---

## Evaluation review

### 5. Evaluation criteria don't mirror generation instructions

If the prompt says "show X" and the rubric scores for "X is
present," you're measuring instruction-following, not generation
quality.

Rubric criteria should be:

- psychological specificity (not "is avoidance visible")
- situational grounding (not "does it mention the harbor")
- graph compilability (structural, not content-dependent)
- curation keepability (would a human author keep this?)

### 6. Semantic checker tests structure, not surface tokens

The checker should test:

- structural: letter stays unopened, no resolution occurs, concern
  is not magically resolved
- NOT surface tokens from one exemplar ("kettle," "coffee," "face
  down")

A valid avoidance moment might involve scrubbing a counter,
reorganizing a shelf, checking a phone, or staring out a window.
None of those would match "kettle." All would be valid.

### 7. Ablation removes the right thing

For each ablation, verify:

- The removed input is actually absent from the prompt
- No other part of the prompt compensates for the removal
- Behavioral instructions don't tell the model what to produce
  regardless of the ablation

If the prompt says "show avoidance behavior" and you ablate the
CausalSlice, the model will still show avoidance because the
instruction told it to. The ablation tests nothing.

---

## Pre-run sign-off

Before running a comparison:

- [ ] Both prompts reviewed against items 1-4
- [ ] Evaluation rubric reviewed against items 5-6
- [ ] Each ablation reviewed against item 7
- [ ] Someone who didn't write the prompts has read them
