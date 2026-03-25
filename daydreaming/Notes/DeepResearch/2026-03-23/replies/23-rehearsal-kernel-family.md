Here’s the repo-grounded update after minimal kernel rehearsal landed.

## Bottom line

1. **Minimal kernel rehearsal is now real.**
   The request/dispatch/execution/storage seam described in the earlier draft is no longer hypothetical. `goal_family_rules.clj` now has rehearsal request/dispatch rules, `goal_families.clj` runs `:rehearsal` in `run-family-plan`, and rehearsal provenance, episode storage, and generic source-use/outcome recording are now kernel-owned.
2. **What is still missing is the front half of rehearsal kernelization, not the back half.**
   The live repo still lacks a generic rehearsal trigger/activation path in `activate-family-goals`, and it still lacks a generic post-effect reappraisal hook. Those are now the real remaining seams.
3. **The longer-lap warning was right.**
   The Graffito miniworld is active at 50 and 100 cycles, but it settles into a narrow repeating ecology. That makes sense as a current benchmark read; it does not mean the miniworld is dead, only that the next lesson has to come from a broader family ecology rather than more of the same local pattern.

## What this note got right and what the repo has already absorbed

The old draft was directionally right, but part of it has now been shipped.

Already absorbed:

- `goal_family_rules.clj`: rehearsal plan request/dispatch rules exist.
- `goal_families.clj`: rehearsal has a real `run-family-plan` branch.
- rehearsal family-plan provenance, source-use recording, outcome resolution, and episode storage are kernel-owned.
- `rules.clj`: no new executor model was needed.
- `episodic_memory.clj`: the existing membrane logic was enough to support Graffito-side cross-family use, promotion, and durable movement once the benchmark fed it real evidence.

That means the earlier recommendation “make rehearsal a real kernel family” is now only **half-live**. The repo has already implemented the planning/execution half.

## What remains live now

### 1. Rehearsal trigger and activation should be kernelized next

This is the biggest remaining point from the original note.

Current repo state:

- `goal_family_rules.clj` has rehearsal request/dispatch rules.
- `goal_families.clj` supports `:rehearsal` in `run-family-plan`.
- but `activate-family-goals` still only emits and dispatches generic trigger facts for `:reversal`, `:rationalization`, and `:roving`.

So the next rehearsal-kernel step is not more execution plumbing. It is:

- a rehearsal trigger rule
- a rehearsal activation rule
- inclusion of rehearsal in generic trigger dispatch

The primary trigger should still be **regulation-need + affordance**, not Mueller’s positive-interest trigger.

Why that still holds:

- the Graffito miniworld uses rehearsal as a control-repair move
- the current benchmark readiness logic is about available routine plus state need, not curiosity
- a positive-interest/afterglow form can still exist later as a second trigger rule, probably frontier-first

The important modeling constraint still stands: do **not** hardcode the trigger against raw Graffito-specific facts. Promote the benchmark-local readiness check into a typed fact such as `:rehearsal-affordance`.

### 2. Reappraisal should still become a generic post-effect kernel pass

The old note was also right on placement.

Desired order:

1. family executes
2. effects apply
3. local state settles
4. appraisal reruns
5. then the next family-selection pass sees the updated reading

The repo is not fully there yet.

What exists now:

- the Graffito benchmarks already do same-situation reread and prove the pattern
- but the reread hook is still benchmark-local, not a generic kernel pass
- in the ordinary miniworld goal path, `run-family-plan` happens before the benchmark reruns mural appraisal
- in the rehearsal path, the Tony/object delta and reread happen benchmark-locally before the kernel rehearsal family runs

So the behavior is proven, but not yet generalized into one clean kernel reappraisal hook.

### 3. Rehearsal does not need to become promotion-eligible yet

This is where the old note now needs correction.

The live Graffito promotion path is currently carried by **rationalization episodes consumed by rehearsal**, not by rehearsal episodes themselves.

So there is no immediate need to make rehearsal family-plan episodes promotion-eligible just to preserve current Graffito behavior. That is a later design choice, not the next required step.

### 4. The long-run attractor warning is now confirmed

The repo has since run the longer Graffito laps.

Current read:

- 50 cycles stays active and non-collapsed
- 100 cycles is still active
- but the tail settles into a narrow repeating pattern around a small number of durable cross-family sources

So the useful lesson is:

- the miniworld is alive enough to keep
- more laps alone are not the next architectural step
- the next lesson has to come from a broader family ecology, not more local reinforcement of the same rehearsal-targeted rationalization path

## Usable next-step read

If this note is used for planning today, the practical interpretation should be:

1. **Do not re-do minimal kernel rehearsal.** That part is shipped.
2. **If rehearsal work continues, make it about trigger/activation kernelization.**
3. **Keep regulation-need + affordance as the default rehearsal semantics.**
4. **Keep reappraisal as a process, not a new family.**
5. **Do not rush to make rehearsal episodes promotion-eligible.** The current Graffito membrane story does not require that yet.
6. **Treat longer-run periodicity as a reason to broaden the family ecology, not as a reason to abandon the miniworld.**

## Short version

The old note is still useful, but only if read as:

- back half absorbed
- front half still live
- long-run warning confirmed

In concrete terms: rehearsal is now a real kernel family at the planning/execution seam, but not yet at the trigger/activation seam, and reappraisal is still benchmark-local rather than a generic kernel post-effect hook.
