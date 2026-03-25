Here’s the repo-grounded extraction after minimal kernel rehearsal landed.

## Bottom line

1. **Much of this note’s implementation checklist is now stale.**
   Rehearsal already has request/dispatch rules, a real `run-family-plan` branch, kernel-owned provenance, source-use/outcome recording, and stored family-plan episodes.
2. **The most useful remaining design extract is the separation between `:emotion-strength` and rehearsal-specific `:motivation-strength`.**
   Rehearsal in this repo is not driven by raw affect alone. It is driven by regulation need plus affordance.
3. **The second useful extract is about retrieval shape, not family semantics.**
   Rehearsal episodes should keep full routine content in payload/support indices but use a compressed retrieval-index subset so they do not disappear behind their own threshold.
4. **The generic reappraisal hook is still a live architectural seam.**
   The benchmark proves the reread behavior, but the repo still does not have one kernel post-effect reappraisal pass.
5. **The longer-lap warning is basically confirmed.**
   The miniworld is alive, but the 50–100 cycle tail becomes periodic. More laps alone are not the next architectural step.

## What is now stale

The original note assumed a much earlier repo state.

Already shipped:

- rehearsal request/dispatch rules in `goal_family_rules.clj`
- rehearsal support in `run-family-plan`
- rehearsal family-plan provenance and kernel-owned episode storage
- generic source-use / outcome recording through the family-plan path
- live Graffito-side memory movement beyond simple reread: dynamic wins, cross-family wins, promotion, durable episodes, and one frontier opening

So the old checklist “make rehearsal a real kernel family” is no longer the right headline. The planning/execution seam is already real.

## What remains genuinely useful

### 1. Rehearsal should carry `:motivation-strength`, not just raw `:emotion-strength`

This is the strongest still-live idea in the note.

Why it matters:

- reversal and rationalization can still mostly rank on trigger emotion strength
- rehearsal in this repo is different: it depends on control deficit, entrainment, agency band, readiness, and whether a routine is available
- the current Graffito chooser is already encoding that distinction benchmark-locally

So when rehearsal gets full trigger/activation kernelization, it should preserve a separate scalar such as `:motivation-strength` rather than pretending rehearsal intensity is just raw negative affect.

That is a real next-step design improvement, not a speculative extra.

### 2. Keep `regulation-need + affordance` as the default rehearsal trigger

This part still stands.

For this repo, rehearsal is currently a control-repair move, not a curiosity move.

So the default trigger should still be:

- failed or strained concern still live
- negative/pressured state present
- usable routine affordance available
- regulation need still real

Mueller-style positive-interest rehearsal can still exist later, but as a second trigger rule, probably frontier-first.

The modeling constraint also still stands: do not hardcode the trigger against raw Graffito-specific cues. Promote benchmark-local readiness into a typed affordance fact.

### 3. Compress rehearsal retrieval indices

This is the second concrete extract worth keeping.

The note is right that rehearsal episodes should not dump every routine fact and precondition into `:retrieval-indices`.

Why:

- in this repo, content indices raise retrieval threshold
- too many indices can make an episode harder to retrieve
- the full routine can still live in payload/support metadata without all of it becoming threshold-bearing content

So the right split is:

- full routine facts in `:episode-payload`
- verbose operator/routine/family metadata in `:support-indices`
- a small portable subset in `:retrieval-indices`

That is a useful cleanup target for the current kernel-owned rehearsal seam.

### 4. Reappraisal should become a generic post-effect kernel pass

This part remains live too, though it is not new relative to the earlier rehearsal note.

The desired order is still:

1. family executes
2. typed effects apply
3. canonical state delta settles
4. appraisal reruns on the relevant live situation
5. then episode storage and later memory bookkeeping proceed

The current Graffito benchmarks prove the reread pattern, but they still do not express it as one reusable kernel hook.

So this is still a real architectural seam. It just is not rehearsal-only.

### 5. Character state first, object state later

This prioritization also remains useful.

The current chooser does not yet treat object phase as a load-bearing decision input. So it is reasonable to:

- kernelize canonical character-state delta first
- leave richer object-phase gating for later, once object state actually constrains selection

That keeps the next step narrow instead of bundling two abstractions together.

### 6. Longer laps confirm periodicity, not failure

The note’s attractor warning has now been borne out.

Current practical read:

- the miniworld is alive enough to keep
- longer runs are still useful as a baseline
- but more laps by themselves are not the next architectural move
- the next lesson has to come from broader family ecology, not from replaying the same rehearsal-centered path longer

## What I would not take as the immediate next move

Two parts of the original note are plausible later, but not the urgent seam now.

### Rehearsal promotion-eligibility via routine facts

This is a reasonable later design option, but it is not required for the current Graffito path.

Right now the live promotion path is still carried by rationalization episodes consumed by rehearsal, not by rehearsal episodes themselves.

So this is not the first thing to change.

### Another benchmark-local bridge

The longer-run read argues against solving the next problem with more local bridge invention.

The useful next move is a more reusable family seam, not another one-off miniworld hack.

## Usable planning read now

If this note is used for planning today, the practical interpretation should be:

1. **Do not re-do the shipped rehearsal request/dispatch/execution seam.**
2. **If rehearsal work continues, make the next step trigger/activation kernelization.**
3. **Add a rehearsal-specific `:motivation-strength` path rather than overloading `:emotion-strength`.**
4. **Compress rehearsal retrieval indices while keeping full routine payload/support metadata.**
5. **Keep reappraisal as a generic post-effect process, not a rehearsal-only trick.**
6. **Treat longer-run periodicity as a reason to broaden family ecology, not as a reason to abandon the miniworld.**

## Short version

The 5 Pro note is still useful, but mainly for two concrete design extracts:

- rehearsal should activate on regulation need with its own motivation scalar
- kernel-owned rehearsal episodes should use compressed retrieval indices

Everything else is best read as confirmation that the next step is broader kernel family ecology, not more local benchmark glue.
