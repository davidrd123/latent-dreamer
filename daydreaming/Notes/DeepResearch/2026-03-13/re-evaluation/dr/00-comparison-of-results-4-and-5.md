# Comparison of Deep Research Results 4 and 5

This note is a short synthesis of the two deep-research outputs in this
folder:

- `deep-research-report (4).md`
- `deep-research-report (5).md`

They are not duplicates. They are answering different questions from
different architectural vantage points.

---

## Top-line read

### Result 4

**Best read as:** a `scope-drd` integration memo.

Its central question is:

> How do the current DAYDREAMER-shaped kernel and the existing Scope
> runtime actually hook together with the least disruption?

Its strongest contribution is the thin external bridge model:

- Dreamer stays outside the stage runtime
- Scope stays a deterministic actuator
- the seam is a small explicit directive payload

This is the most useful of the two documents if the next move is:

- get the cognitive system talking to `scope-drd`
- define a stable runtime control contract
- avoid embedding kernel logic inside the stage too early

### Result 5

**Best read as:** a `latent-dreamer` minimal-v1 memo.

Its central question is:

> What is the smallest honest version of this architecture that should
> exist inside `latent-dreamer` before any ambitious integration move?

Its strongest contribution is the graph-first discipline:

- hand-authored fixture first
- traversal harness first
- explicit commit policy first
- prove "return / drift / hold / counterfactual jump" before heavier
  generation machinery

This is the more useful of the two documents if the next move is:

- build the smallest executable vertical slice
- reduce spec sprawl
- pin down the missing operational terms in the current notes

---

## Where they agree

Despite different emphases, the two reports agree on the main structure:

- The valuable part of Mueller is the control logic, context handling,
  retrieval, and commit discipline, not surface text generation.
- Canonical world state must remain separate from hypothetical
  exploration.
- Event commitment must be explicit and testable.
- A small stable contract between cognition and stage is necessary.
- The first real proof should be a bounded v1, not an immediate grand
  rebuild.

So the documents are not in opposition on fundamentals.

---

## Where they diverge

### 1. Immediate center of gravity

Result 4 centers the stage seam:

- define a directive payload
- drive Scope through existing REST/control surfaces
- let the stage remain dumb and deterministic

Result 5 centers the offline substrate:

- define fixtures
- define graph store
- define traversal harness
- define commit policy and appraisal machinery

This is the cleanest way to describe the split:

- Result 4 asks "how do we hook in?"
- Result 5 asks "what must exist before hooking in is meaningful?"

### 2. V1 starting point

Result 4's recommended V1 is a thin integration bridge.

Result 5's recommended V1 is a tiny executable graph/traversal slice.

These are compatible only if sequenced correctly:

- Result 5 should dominate the definition of the minimal cognitive
  artifact
- Result 4 should dominate the definition of the runtime control seam

### 3. Relation to the pressure-engine reframe

Neither report fully internalizes the newer pressure-engine reframe.

Result 4 still mostly thinks in terms of:

- kernel goals
- context facts
- planner adapter
- stage directive

Result 5 is cleaner structurally, but it still treats Mueller
goal-types as more primitive than the reframe now wants.

That means both reports are useful inputs, but neither is the final
architectural answer after the reframe.

### 4. Degree of ambition

Result 4 starts pushing toward:

- per-character streams
- stage snapshots as branch support
- richer live integration semantics

That is useful long-horizon thinking, but it risks moving too quickly
from symbolic contexts to diffusion/runtime branches.

Result 5 is more restrained and more consistent with the current
graph-first discipline.

---

## Which one to trust for what

### Use Result 4 for:

- `scope-drd` hookup strategy
- runtime seam design
- "stage as deterministic actuator" framing
- what the live bridge could look like

### Use Result 5 for:

- minimal v1 definition
- fixture/schema/traversal discipline
- explicit commit policy
- deciding what must be executable before deeper integration

### Do not use either one as:

- the final answer to the pressure-engine reframe
- the final vocabulary for `Concern`, `Operator`, `Context`,
  `CommitRecord`, and `Adapter`
- a settled migration plan

Both reports are still better treated as comparison inputs than as
authoritative internal architecture notes.

---

## Main unresolved tension

The real unresolved question now is not "which report is right?"

It is:

> After the pressure-engine reframe, what is the right boundary between:
> 1. the minimal graph-first shipping path,
> 2. the runtime stage hook-in,
> 3. the broader pressure-engine generalization?

This is the question neither report fully answers.

---

## Recommended next question for GPT-5 Pro

If sending a follow-up question to GPT-5 Pro, the most productive one is:

> Given Result 4, Result 5, and the pressure-engine reframe, what is the
> correct sequencing and boundary discipline?
>
> Specifically:
> - What must exist in the minimal graph-first v1 before live hook-in?
> - What should the runtime seam to `scope-drd` be called and contain?
> - Which parts of the pressure-engine reframe should change the
>   near-term mainline now, and which should remain research framing?
> - How should `Concern`, `Operator`, `Context`, `CommitRecord`, and
>   `Adapter` be separated so the project does not collapse into one
>   mushy abstraction?

That is the next question because it arbitrates the real tension between
the two reports instead of merely asking for another rewrite of either.

---

## Bottom line

Result 4 is stronger on **live integration shape**.

Result 5 is stronger on **minimal executable architecture**.

If forced to choose a default planning posture right now:

- follow Result 5 for what to build first
- follow Result 4 for how that thing should eventually talk to Scope
- use GPT-5 Pro to adjudicate the pressure-engine boundary questions
  neither report has fully settled
