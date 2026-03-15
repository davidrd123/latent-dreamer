# Q10. Evaluation infrastructure: can we actually measure diversity?

Grounding: `daydreaming/authoring_time_generation_prototype.py`, `tests/test_authoring_time_generation_prototype.py`, the four `authoring_time_generation_*.yaml` fixtures, `31-generation-experiment-review-checklist.md`, `32-authoring-time-generation-comparison-memo.md`, `Q2-rationalization-bench.md`, `Q2a-rationalization-bench.md`, and `Q3-gen-quality-curation-rev.md`.

## 1. MVP recommendation

**Make one evaluator split, not one more token hack.**

Right now `make_semantic_checks()` is doing three jobs at once:

1. cheap prose smoke test
2. compilation signal via `semantic_pass_ratio()` inside `score_candidate_for_compilation()`
3. shortlist gate via `has_batch_semantic_pass()` inside `score_pool_row_for_admission()`

That is the wrong coupling. The same brittle surface heuristic is deciding both **which candidate wins a step** and **which row survives batch admission**. That is why a Tessa line can clearly read as rationalization to a human while still failing the strict predicate, and why the March 15 hardening can fix one fixture while the evaluator remains noisy across the set.

**The MVP is to split evaluation into three layers:**

### A. Structural hard checks
These should be deterministic and should decide admission failure.

Use data you already have:
- `graph_projection`
- `sidecar`
- active `situation.current_state`
- selected `operator_family`
- `PracticeContextV1`
- `AppraisalFrame`

Hard-fail only on things of this kind:
- graph invalid under `validate_graph_projection()`
- cross-fixture contamination
- unresolved state violated
- operator/commit contradiction
- graph/sidecar inconsistency

### B. Operator-signature soft checks
These should score prose quality and operator legibility, but **not** hard-reject by themselves.

This replaces the current all-or-nothing semantic checker with:
- cue families, not single magic phrases
- negation guards
- cross-operator discrimination
- operator margin rather than independent pass/fail booleans

### C. Batch-level diversity metrics
These should measure whether an intervention widened the pool **before** admission pressure collapses it.

This is separate from candidate validity.

You already have some of the ingredients:
- `batch_function_signature()`
- pressure tags
- practice tags
- coverage refs
- sequence index
- duplicate-function detection

Promote those from implicit admission machinery into explicit evaluation outputs.

## 2. Why it works

### 2.1 The current checker is overloaded

The repo already tells you this in three different places, and it is right.

- The checklist says semantic checking should test structure, not surface tokens.
- The comparison memo says the token checker reached its limit and is now a smoke test.
- The Kai/Rhea review says the real bottleneck is diversity and graph function, not sentence-level fluency.

So stop asking one lexical function to carry the whole evaluation burden.

### 2.2 The prototype already exposes better structural signals than it currently uses

The important point is not “we need a smarter prose checker.”

The important point is: **the prototype already emits a richer structural object than the evaluator consumes.**

`build_sidecar()` gives you:
- `source_concern_ids`
- `causal_slice`
- `appraisal_frame`
- `practice_context`
- `selected_affordance_tags`
- `operator_family`
- `retrieved_episode_refs`
- `commit_type`

`validate_graph_projection()` already checks:
- graph enums
- resolvable refs
- pressure/practice/origin vocabularies
- lane/scope/revisability

`normalize_operator_graph_projection()` already contains one operator-aware structural rule:
- rationalization + `close` + no outward act → normalize to `clarify`

That is the pattern to generalize.

### 2.3 What the shared evaluator should actually compute

Add a new path that returns something like:

```python
{
  "hard_failures": [...],
  "structural_scores": {
    "state_fidelity": 0.0-1.0,
    "graph_sidecar_consistency": 0.0-1.0,
    "commit_consistency": 0.0-1.0,
  },
  "operator_scores": {
    "avoidance": 0.0-1.0,
    "rehearsal": 0.0-1.0,
    "rationalization": 0.0-1.0,
  },
  "selected_operator_margin": float,
  "anchor_activation": 0.0-1.0,
  "harm_acknowledgment": 0.0-1.0,
}
```

Then:
- `score_candidate_for_compilation()` uses this instead of `semantic_pass_ratio()`.
- `score_pool_row_for_admission()` hard-rejects only `hard_failures`, not “fewer than 75% of lexical checks passed.”
- batch reporting logs operator spread, function spread, pressure spread, and ref spread explicitly.

### 2.4 The shared predicates are already visible across Kai, Maren, Rhea, and Tessa

The four fixtures look different on the surface, but the evaluator logic is mostly the same.

The current semantic expectation slots collapse to four shared predicate families:

1. **operator is behaviorally realized**
2. **the unresolved action has not already happened**
3. **the charged place remains psychologically active**
4. **the move intensifies / clarifies / reframes rather than resolving the situation**

That means the evaluator can be shared.

What changes per fixture is not the predicate code. What changes is the authored cue pack.

## 3. What can be parameterized vs. what still needs fixture authoring

## Shared across fixtures now

These can be implemented once.

### Shared structural checks
- unresolved-state preservation from `situations[].current_state`
- graph/sidecar consistency
- operator/commit compatibility
- practice-tag ↔ `PracticeContextV1` consistency
- pressure-tag ↔ `source_concern_ids` consistency
- cross-fixture contamination

### Shared operator profiles
Each operator can have a reusable profile.

#### Avoidance
Structural prior:
- unresolved contact or action remains pending
- no outward irreversible engagement has happened
- prospective or threshold pressure still live

Positive cue families:
- displacement activity
- ritualized busyness
- hands occupied by substitute task
- delay framing

Negative cue families:
- explicit justification of already-done harm
- opening-line rehearsal before entry
- completed repair / send / entry / confrontation

#### Rehearsal
Structural prior:
- prospective or threshold situation
- encounter not yet begun
- speech or entry remains pending

Positive cue families:
- trial phrasing
- mouthing / drafting / crossing out openings
- bracing for first sentence
- handle / doorway / threshold orientation

Negative cue families:
- domestic displacement with no approach vector
- post-hoc self-exoneration about already-done harm
- conversation already underway

#### Rationalization
Structural prior:
- harm already happened
- `realization_status = actual` or equivalent aftermath state
- repair not yet completed

Positive cue families:
- justification / necessity framing
- motive editing
- blame-softening explanation
- apology competing with explanation

Negative cue families:
- simple delay with no reinterpretation
- future opening-line preparation
- actual repair / send / closure

### Shared negation logic
Implement once:
- local negation window around cue hits
- phrase-level exceptions
- “no / not / never / didn’t / hasn’t / without” style guards

This is enough to stop garbage matches like “did not go to the harbor” from counting as active harbor imagery.

### Shared batch diversity metrics
Implement once:
- operator coverage / entropy
- pressure coverage
- practice coverage
- `batch_function_signature()` count
- ref coverage
- option-effect distribution
- within-batch near-duplicate rate

## Still fixture-authored

This does **not** go away.

### Anchor-place cue packs
The shared predicate is “the charged place remains psychologically active.”
The authored part is what counts as evidence.

Examples:
- Kai: `harbor`, `ferry`, `tide`, `dock`, `waterfront`
- Maren: `rehearsal room`, `door`, `handle`, `run-through`, `inside the room`
- Rhea: `Studio B`, `door`, `chair scrape`, `hallway threshold`
- Tessa: `donor hall`, `program`, `toast`, `podium`, `applause`

### Harm-schema cue packs
The shared predicate is “the specific harm or pressure is acknowledged.”
The authored part is what the harm looks like in this world.

Examples:
- Tessa: omission, simplification, credit cut, toast, what she did to Eli
- Kai: letter, silence, tonight, harbor meeting, sister’s bid for contact
- Maren/Rhea: first sentence, credit dispute, correction, run-through threshold

### Action-class cue packs
This is the one part that remains somewhat fixture-shaped.

Examples:
- Kai avoidance: domestic delay ritual
- Maren/Rhea rehearsal: threshold phrasing behavior
- Tessa rationalization: explanation displacing apology

These should be authored as **cue families**, not single terms.

## How much of the current evaluator becomes shared?

**Most of it.**

The right way to say it is:
- **the predicate logic can be shared almost completely**
- **the lexical and imagistic support remains partly authored**

In practical terms, I would expect about **75–80% of the evaluation code** to become shared and about **20–25% of the authoring burden** to remain fixture-specific cue packs.

Another way to put it: the current four semantic-check slots per fixture can all become shared predicate templates; what remains authored is the cue vocabulary and the state/harm metadata that feeds them.

## 4. Concrete MVP changes in this codebase

### 4.1 Do not keep expanding `make_semantic_checks()`

That function is already a mixed bag of:
- token lookup
- structural hints
- fixture-specific assumptions
- contamination guard

If you keep extending it, you will get a longer and more fragile pile of fixture exceptions.

Instead:

### 4.2 Add an `evaluation_profile` block to fixtures

Minimal new fixture authoring:

```yaml
evaluation_profile:
  unresolved_state_constraints:
    - key: letter_opened
      expected: false
    - key: reply_sent
      expected: false

  anchor_place:
    id: harbor
    cue_families:
      - [harbor, ferries, dock, tide, waterfront]

  harm_schema:
    cue_families:
      - [letter, envelope, meeting tonight, silence, sister]

  operator_cue_overrides:
    avoidance:
      enactment_families:
        - [kettle, sponge, scrub, wipe, polish, reorganize, clean]
```

The point is not to move more hand-authored strings into YAML for their own sake.
The point is to separate:
- **shared evaluator logic**
- **fixture-specific evidence vocabularies**

### 4.3 Add a structural evaluator

New function shape:

```python
def evaluate_candidate_structure(fixture, result) -> dict:
    ...
```

Use:
- `result.parsed_response["graph_projection"]`
- `result.sidecar`
- resolved active situation
- current state flags

This evaluator should answer questions like:
- did the text/graph imply the letter was opened even though `letter_opened=false`?
- did a rehearsal scene start the conversation?
- did a rationalization beat claim closure without an outward act?
- do `practice_tags` and `sidecar.practice_context` agree?
- do `origin_pressure_refs` reflect the active concerns the prose appears to carry?

### 4.4 Replace independent booleans with operator competition

Current problem: a candidate can pass cues for more than one operator because the checks are independent.

Fix:
- compute an `avoidance_score`
- compute a `rehearsal_score`
- compute a `rationalization_score`
- use the **margin** of the selected operator over the next-best alternative

This is the cheapest non-LLM way to test “is this really doing a different dramatic move?”

### 4.5 Move batch hard gating to structural failure only

Right now `semantic_expectations_failed` is a hard error.
That is too aggressive for noisy lexical checks.

Keep hard rejection for:
- graph invalid
- contamination
- impossible state transition
- operator/commit contradiction
- graph/sidecar contradiction

Let operator-legibility and anchor-place evidence remain a soft score until the evaluator is calibrated.

## 5. Failure modes of the MVP

### 5.1 You can still pass structure with bland prose

Yes. A structurally coherent but dull candidate can still pass.
That is fine. Structural evaluation is not trying to replace human curation of exceptional prose.
It is trying to stop fake wins and brittle losses.

### 5.2 Cue packs can still overfit the fixture

If you author bad cue families, you will still get false positives and false negatives.
The difference is that the failure will now live in a small authored vocabulary pack, not in tangled Python conditionals.

### 5.3 Graph/sidecar consistency can reward self-consistent thinness

Q3 already identified this: the graph seam can be thin while the prose is richer.
So do **not** let graph consistency become the whole evaluator. It is one channel, not the truth.

### 5.4 Softening the semantic gate can let more mediocre rows into the pool

True.
But that is less dangerous than hard-rejecting real rationalization because the prose used “necessary” instead of “had to.”

The admission layer already has:
- duplicate-function detection
- near-duplicate detection
- coverage pressure
- overdetermination gain

Use those. Do not pretend the lexical checker should do all jobs.

## 6. Higher-octane design

**Build a small embedding-based operator/function discriminator on top of the structural evaluator.**

Not an LLM judge. Not pairwise prompting. Just a lightweight encoder plus curated exemplar banks.

### 6.1 Would a small open embedding model work?

**Yes, probably.**

A small sentence-transformer class model is plausible here because your candidates are short and your label space is tiny:
- avoidance
- rehearsal
- rationalization

You are not solving open-ended literary criticism. You are solving “which dramatic move is this closest to?” plus “is it a near-duplicate of material we already have?”

That is a much easier problem.

### 6.2 The smallest useful version

Use a nearest-centroid or nearest-neighbor setup first.

For each operator family, build a curated bank of exemplars:
- 2–3 hand-picked reference exemplars / worked-trace exemplars
- 3–5 curated accepted outputs from different fixtures

Then:
- embed each candidate text
- compute distance to each family bank centroid
- compute operator margin
- compute within-family novelty against already admitted rows

Use it first as **post-hoc evaluation**, not as an admission gate.

### 6.3 How many exemplars?

For a first useful bank:
- **5–8 clean exemplars per operator family** is enough to test whether the signal exists
- **10–20 per family** is where I would start trusting it more

Do **not** dump all existing accepted outputs in immediately.
The current accepted pool is already narrow in places. If you seed the bank with too many Kai-style kitchen-avoidance rows, the model will learn “avoidance = kitchen ritual” instead of “avoidance = displacement under unresolved pressure.”

### 6.4 Can exemplars come from existing batch outputs?

Yes, but only after curation.

Best seed mix:
- worked-trace exemplars
- edited keeper rows
- fixture-diverse rows
- some deliberately hard borderline negatives

That last part matters. The discriminator should learn not just “what avoidance is,” but also “what rationalization is not.”

### 6.5 Latency overhead

For short 1–4 sentence candidates, a small encoder should be cheap enough for batch scoring.

My rough guess:
- **CPU batch scoring in low tens of milliseconds per candidate**$_{70\%}$
- lower on GPU

That is fine for:
- post-hoc batch analysis
- compilation soft scoring

I would **not** put it into a brittle hard gate yet.

### 6.6 Where it belongs in the pipeline

Correct order:

1. **post-hoc batch evaluation metric**
2. then **soft compilation term**
3. only later, if it calibrates well, consider limited admission use

Do not make it the new semantic gate on day one.

### 6.7 What the higher-octane version buys that the MVP does not

The structural MVP can tell you:
- whether a candidate is coherent
- whether it contradicts its own state
- whether it resembles the claimed operator at a rule level

The embedding discriminator can tell you:
- whether two candidates are doing the same dramatic work despite different vocabulary
- whether a “new” candidate is just a paraphrase of an existing operator template
- whether an intervention actually widened the local operator manifold or just changed surface diction

That is the missing measurement piece for Q5/Q8/Q12.

## 7. Connection to other questions

### Q5 and Q12: operator diversity / practice-context stationarity
The MVP’s operator-margin score is the right readout for whether rotation or practice evolution changed the dramatic move rather than just the label.

### Q6: graph seam richness
A graph/sidecar consistency subscore lets you measure when mixed-pressure prose collapses into a thin graph projection.

### Q8 and Q13: retrieval balancing
Batch diversity metrics can show whether a retrieval change widened function signatures and pressure spread, or merely swapped one narrow template for another.

### Q11: prompt architecture
If you move from JSON dump to given-circumstances prompting, the evaluator should become **less** sensitive to exact wording. That is good. A structural evaluator will survive that prompt change better than the current token list will.

## 8. What NOT to build yet

- **Do not** keep adding more special-case token lists to `make_semantic_checks()`.
- **Do not** replace human curation with a judge model.
- **Do not** hard-gate on embeddings before you have a curated exemplar bank.
- **Do not** train a heavy supervised evaluator yet.
- **Do not** pretend graph consistency alone measures prose quality.

The correct immediate move is smaller:

**Refactor evaluation into structural hard checks + shared operator-signature scoring + explicit batch diversity reporting.**

That gets you a measurement layer that is good enough to compare Q5/Q8 interventions honestly, without building evaluation theater.
