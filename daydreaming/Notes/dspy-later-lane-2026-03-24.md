# DSPy Later Lane

Last updated: 2026-03-24

This note captures where DSPy-like optimization could fit in this repo
later, without turning it into a premature tuning project.

It is not an active branch.
It is a parking-and-shaping note for bounded future use.

---

## Short answer

DSPy looks like a good eventual fit for this project when all of the
following are true:

- the seam is narrow and explicit
- the inputs/outputs are typed
- the metric is real and not hand-wavy
- the memory ecology is trustworthy enough that optimization will not
  simply learn the current leaks

Bad fit:

- optimizing the whole kernel
- optimizing the Graffito miniworld as a whole
- optimizing for vague targets like "feels alive"
- optimizing for raw divergence by itself

Good fit:

- optimizing a bounded LLM interface around the kernel

---

## Sequencing guardrail

Do not run DSPy-style optimization on top of a leaky memory ecology.

The existing project guardrail still holds:

- first make the membrane trustworthy
- then define the seam
- then define the metric
- then collect examples
- then optimize

If the substrate still confuses:

- benchmark-projected vs carried state
- cheap self-reuse vs durable value
- noisy novelty vs useful later influence

then DSPy will learn the wrong thing faster.

---

## Best candidate seams

Ranked from most plausible first use to least plausible early use.

### 1. Typed concern / issue proposal

The LLM reads real working material and proposes a small typed packet:

- issue
- contradiction
- question
- hypothesis
- aesthetic bet

The kernel validates, normalizes, scores, and decides what enters the
concern economy.

Why this is a good DSPy candidate:

- naturally typed IO
- clear bounded prompt task
- directly relevant to the collaborator pivot
- easy to compare against human judgment or later usefulness

### 2. Retrieval reformulation

The LLM does not retrieve by itself.
It reformulates the current need into one or more discrete retrieval
queries / index bundles / counterevidence requests.

Why this is a good DSPy candidate:

- narrow prompt boundary
- can be scored downstream by retrieval usefulness
- does not require changing kernel authority over memory

### 3. Return-ticket realization

The kernel decides something deserves resurfacing.
The LLM turns that into a concrete return object:

- a marginal note
- a contradiction prompt
- a question to reopen
- a rehearsal snippet

Why this is a good DSPy candidate:

- language quality matters
- easier to evaluate with human uptake
- lower architectural risk than concern entry

### 4. Typed residue / write interface

This was the earlier DSPy-shaped idea in the repo:
optimize what gets written back into memory.

Still plausible, but riskier:

- easy to optimize for noise
- easy to exploit partial leaks
- metrics are harder than they first appear

Use this only after the earlier seams are better understood.

---

## Candidate metrics

The metric must reward later usefulness and hygiene, not just novelty.

### Concern / issue proposal metrics

- structural validity
  - packet parses
  - required fields present
  - type assignment plausible
- deduplication quality
  - does not restate an already-live concern in dressed-up language
- later usefulness
  - enters competition and remains relevant beyond one turn
  - leads to retrieval or contestation that a human judges as real
- overgeneration penalty
  - too many weak issues should count against the optimizer

### Retrieval reformulation metrics

- retrieval hit quality
  - retrieved objects judged relevant
- counterevidence yield
  - ability to surface non-confirming material
- grounded surprise
  - returns are non-obvious but explainable
- query economy
  - fewer, better reformulations beat many diffuse ones

### Return-ticket metrics

- human uptake
  - kept
  - reopened
  - replied to
  - copied into work
- timing fitness
  - felt well-timed rather than annoying
- distinctiveness
  - not interchangeable with a generic summary

### Metrics to avoid as the main objective

- raw trace divergence
- prose richness alone
- self-scored "interestingness"
- engagement time

---

## Eval set shapes

### Concern / issue proposal eval set

Small curated set of real materials with hand-marked outputs:

- conversation excerpts
- research notes
- architecture discussions
- annotated journal / vault entries

For each example:

- source text chunk
- expected issue density range
- acceptable object types
- gold or semi-gold examples
- obvious failure cases

The first eval set can be tiny.
20-40 examples is enough to learn whether the seam is promising.

### Retrieval reformulation eval set

For each example:

- current live issue / concern
- available memory store slice
- candidate reformulations
- judged useful retrieved objects
- judged distractor returns

This is more expensive to construct, but still feasible.

### Return-ticket eval set

For each example:

- latent issue / resurfacing candidate
- current context
- desired return style
- positive and negative uptake labels

This is partly human-labeled and will likely need live use data.

---

## What success looks like

### Success for concern / issue proposal

After 1 session:

- at least one proposed object survives validation
- it feels nontrivial, not just summary in disguise
- it enters the current kernel machinery without custom hacks

After 3-5 sessions:

- proposal quality is stable enough that the seam feels real
- later usefulness beats a naive baseline
- overgeneration is controlled
- the seam teaches something about what objects the kernel actually
  wants

### Success for retrieval reformulation

After a small run:

- reformulated retrieval produces more useful or more contestable
  returns than the fixed baseline
- the improvement is visible in a way the human can explain

### Success for return-ticket realization

After a small run:

- surfaced returns feel timely and specific
- uptake beats a generic resurfacing baseline

---

## What failure would mean

Failure is not just "the output was mediocre."

Important failure signals:

- the seam only produces dressed-up summaries
- the optimizer learns to exploit measurement artifacts
- the packet schema is too blunt to be worth optimizing
- human judges cannot reliably distinguish optimized output from a
  simpler baseline
- the seam works only on repo-native language and fails on less
  architecture-shaped material

If that happens, change the seam or the object model before trying more
optimization.

Do not just run longer searches over a bad objective.

---

## Recommended first DSPy lane

When the project is ready, the best first lane is:

### Optimize typed concern / issue proposal

Shape:

- input: bounded chunk of real material
- output: small typed issue packet
- kernel: validates, normalizes, scores, competes, stores
- metric: structural validity + non-duplication + later usefulness

Why this lane first:

- closest to the current collaborator pivot
- bounded enough to evaluate
- does not require giving up kernel authority
- teaches the most about whether the current architecture can accept
  non-hand-authored content entry

---

## Suggested optimizer posture

If/when this starts:

- begin with prompt/instruction optimization, not finetuning
- keep examples small and curated
- inspect failures manually
- prefer a metric bundle over a single scalar

Likely DSPy posture:

- start with an instruction/few-shot optimizer
- consider reflective/textual-feedback optimizers if the seam needs
  richer critique
- only consider finetuning after the seam and metric are already
  trustworthy

---

## Practical rule

Do not ask:

"Can DSPy optimize this repo?"

Ask:

"Is there one typed LLM seam here with a real metric and enough
examples to justify optimization?"

If yes, DSPy may help.
If no, do more architecture first.
