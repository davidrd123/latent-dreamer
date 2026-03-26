# Steering Balance

Last updated: 2026-03-25

This note is not a sprint contract. It is a steering aid.

Use it when multiple true things are competing for attention:

- the active Graffito/kernel branch
- benchmark honesty / interpretability cleanup
- the larger collaborative-companion pivot

The point is to keep those from collapsing into one undifferentiated
"everything matters" queue.

---

## Current balance

Rough weighting for attention and implementation energy:

- **Now**: `60-70%`
- **Next**: `20-25%`
- **Later**: `10-20%`
- **Parked**: `0%` unless a concrete trigger appears

This is not a precision budgeting tool.
It is a reminder that strategic rethinking should shape judgment
without constantly preempting the live reusable-seam work.

---

## Now

The primary branch is still the Graffito mechanistic harness.

This means:

- keep the miniworld as the active domain assay
- use longer laps as observation, not as architecture by themselves
- continue pulling inward only the seams that have clearly proven
  reusable
- keep benchmark-local world math and reread projection local unless a
  reusable pattern is clearly exposed

Concrete work in this bucket:

- broader use/outcome tracking
- using the now-exposed `:episode-lifecycle` surface to read the
  miniworld's dynamics directly in the canopy/debug export
- generic post-effect hook usage
- 20 / 50 / 100 cycle comparisons
- a short 50 / 100 cycle attractor digest: when the loop settles,
  which episodes dominate it, and what still changes inside the
  attractor
- identifying the next family-ecology move from observed need
- keeping the current trace/debug/canopy good enough to read the
  benchmark honestly

**Parallel thread: pivot-readiness loading.**

While doing the above, keep building a clean sense of what transfers
versus what is benchmark scenery. The goal is not to start the pivot
early; it is to know enough to choose the first real content-side
experiment intelligently.

This is not a separate work stream. It is a lens on the current work:

- as you touch use/outcome tracking, note how human uptake could enter
  the same path
- as you touch family plan execution, note where LLM proposal would
  plug in
- as you touch benchmark-local world logic, ask whether it is really
  kernel control or a stand-in for a future knowledge-entry seam
- as you touch the membrane, note what changes if the objects are
  claims/questions instead of episodes
- keep the distinction explicit between:
  - **control substrate**: competition, memory, promotion, rule access,
    use/outcome
  - **knowledge-entry layer**: what new issues, contradictions,
    hypotheses, or concerns enter the system in the first place

That thread has now crossed the first threshold:
the repo has a bounded typed issue-entry slice in code.
It is still small enough to count as a seam test, not a broad rewrite.

When this thread reaches the next threshold — repeated grounded packets
with real later resurfacing effects — it moves to its own larger branch.

This is still the center of gravity.

---

## Next

These are important, but they should not continuously interrupt the
primary branch.

### 1. Benchmark honesty / interpretability

The Graffito provenance issue belonged here, and the main fix is now
done.

Goal:

- make the assay honest
- make provenance explicit
- avoid misreading benchmark-projected facts as kernel-persistent state

This includes:

- main provenance fix landed in `fcb9d7b`: emotions now derive from
  Tony + situation deltas and export as
  `:benchmark-projected :derived`
- keeping those provenance labels visible and trustworthy as the canopy
  evolves
- canopy/debug provenance labels such as:
  - `kernel-persistent`
  - `benchmark-carried`
  - `benchmark-projected`
  - `derived`

This does **not** currently mean:

- route the miniworld through `control/run-cycle`
- absorb the whole outer loop into the kernel

### 2. Reusable seam cleanup

If the current miniworld exposes another clear reusable seam, it moves
here first, not straight to "Later".

Examples:

- a post-effect pattern that generalizes
- a source-use rule that clearly belongs in the kernel
- a debug/export seam that should stop being Graffito-specific

---

## Later

This is the larger pivot direction.
It is real, but it is not the current implementation branch.

The current notes suggest the likely later architecture is not
"more Mueller families" but a broader collaborative substrate built
around:

- issue-centered objects
- common-ground tracking
- relational state
- transformation lineage
- return-ticket / resurfacing policy
- human uptake entering memory authority
- a more porous proposer/committer split:
  - `LLM = strong proposer, weak committer`
  - `Kernel = weak proposer, strong committer`

Likely first pivot seam, when this branch becomes active:

- not "replace Mueller's rule base with prompts"
- not full planning decomposition via LLM
- first try a small typed proposal seam such as:
  - issue / concern initiation
  - contradiction / contestation proposals
  - retrieval reformulation

The working idea is:

- let the LLM propose a small number of typed objects
- normalize and validate them strictly
- let kernel competition, memory discipline, and outcome tracking decide
  what persists

That keeps the collaborator pivot compatible with the current kernel
discipline instead of turning into a prompt-per-rule rewrite.

The first small live version of that is now started:

- bounded architecture packet
- proposal packet from the model
- kernel validation / admission / activation
- issue retrieval through shared bridge cues
- no direct path to durable authority or rule access

Near-pivot trigger:

- if the current attractor / lifecycle read stabilizes and no new
  clearly reusable Graffito seam appears, let this branch graduate
  sooner into one bounded proposal experiment rather than extending the
  miniworld indefinitely
- the intended scale is still small: one typed proposal seam, not a
  broad collaborator rewrite

Use this bucket as:

- a design lens
- a research lens
- a filter on what seems reusable now

Do **not** use it as a reason to constantly reopen the active branch.

---

## Parked

These are real fronts, but they are not current steering targets.

- full collaborative object-model rewrite
- full relational-layer implementation
- full common-ground ledger implementation
- full polyphonic daemon architecture
- prompt-replacing the full Mueller planning / inference layer
- serendipity / analogical planning jump
- forcing the miniworld through the full control loop
- broad kernelization of Graffito world logic
- treating the Graffito miniworld itself as the collaborator prototype
- more benchmark-local bridge invention unless a concrete leak appears
- creative-output evaluation as the main driver

If one of these becomes urgent, name the trigger explicitly before
moving it out of `Parked`.

---

## Decision rules

When a new idea appears, ask:

### 1. Is this changing the sprint branch?

If no:
keep it out of the live queue.

### 2. Is this an honesty fix or a new branch?

If it only improves interpretability or prevents self-misreading:
put it in `Next`, not `Now`.

### 3. Did the miniworld expose a reusable seam, or just another local
ecology trick?

If reusable:
move inward.

If benchmark-shaped:
keep it local.

### 4. Is this strategic pressure, or implementation pressure?

If strategic:
let it shape evaluation criteria, not task order.

### 5. What would be displaced if this moved to the front?

If the answer is "the current reusable-seam queue," require a stronger
reason than interest alone.

### 6. Is this a knowledge-entry seam or a control-substrate rewrite?

If it is a **knowledge-entry seam**:
capture it for pivot-readiness / `Later`.

If it is a **control-substrate rewrite**:
park it unless the current branch is blocked or the seam has already
shown clear reuse in more than one context.

---

## Current read

At this moment:

- **Now** = Graffito miniworld as mechanistic harness, with use/outcome
  broadening, lifecycle visibility, and longer-run attractor evidence
- **Next** = bounded honesty maintenance if a new interpretability leak
  appears, plus any further clear reusable seam
- **Later** = collaborative companion pivot: issue objects, common
  ground, relational state, proposer/committer rebalance
- **Possible near-pivot move after the current read** = one bounded
  typed proposal experiment if the miniworld is informative but no
  longer opening new reusable seams
- **Parked** = full rewrite, full relational buildout, full control
  loop integration, deep engine jump

That is the balance to return to when everything starts sounding
important at once.

---

## Related notes

- `daydreaming/Notes/right-now.md`
- `daydreaming/Notes/current-sprint.md`
- `daydreaming/Notes/collaborative-pivot-synthesis-2026-03-24.md`
- `daydreaming/Notes/graffito-miniworld-emotion-provenance-decision-2026-03-24.md`
- `daydreaming/Notes/missing-engine-map-2026-03-23.md`
