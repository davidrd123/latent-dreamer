# Rehearsal as a Real Kernel Family + Longer Miniworld Soak Design

You have the full repo context. This prompt asks for deep code
reasoning over the existing kernel, not literature review. Read the
actual source files before answering.

## Context

The Graffito miniworld (`kernel/src/daydreamer/benchmarks/graffito_miniworld.clj`)
runs 20 autonomous cycles across three situations (street overload,
apartment support, mural crisis) with:

- persistent Tony character state (sensory-load, entrainment,
  felt-agency, perceived-control)
- regulation mode derived from continuous vars
- reappraisal after every family execution
- 6 autonomous reappraisal flips (threat → challenge at the mural)
- 2 episodes promoted to durable, 1 frontier rule opened
- 3 families active: rationalization, reversal, rehearsal

But rehearsal is **benchmark-local**. The miniworld has a
`rehearsal-routine` that directly updates Tony's state and reruns
appraisal. It is NOT a kernel family with trigger/activation/dispatch
rules in `goal_family_rules.clj`.

The three real kernel families (roving, rationalization, reversal)
each have:
- a trigger rule (emotional condition → family-trigger fact)
- an activation rule (family-trigger → goal activation)
- a plan-dispatch rule (family-plan-ready → executor call)
- declared effect schemas
- episode storage through `store-family-plan-episode`
- participation in the membrane (use-history, promotion, flags)

Rehearsal has NONE of these. It's listed as `:supported` in
`goal_family_rules.clj:5` but has no rules.

## Question 1: Design rehearsal as a real kernel family

Read these files carefully first:
- `kernel/src/daydreamer/goal_family_rules.clj` — existing trigger/activation/dispatch rules for roving, rationalization, reversal
- `kernel/src/daydreamer/goal_families.clj` — the family plan execution machinery, effect application, episode storage
- `kernel/src/daydreamer/benchmarks/graffito_miniworld.clj` — the benchmark-local rehearsal that works
- `kernel/src/daydreamer/benchmarks/graffito_rehearsal_slice.clj` — the earlier deterministic rehearsal proof
- `kernel/src/daydreamer/rules.clj` — executor boundary, effect schemas, apply-effects

Then answer:

### 1a. Trigger conditions

What should activate rehearsal? Mueller says: "positive interest
attached to an active goal." But the Graffito miniworld activates
rehearsal from a regulation-need signal (Tony needs control, rhythm
is available, co-regulator is present).

- Should rehearsal trigger from positive emotion on an active goal
  (Mueller-faithful)?
- Or from a regulation-need + affordance signal (Graffito-faithful)?
- Or both, with different trigger rules for each?
- What does the trigger rule's antecedent schema look like in
  RuleV1 terms?

### 1b. What rehearsal produces

The current benchmark-local rehearsal:
- updates Tony's character state (entrainment, felt-agency,
  perceived-control)
- triggers mural reappraisal
- does NOT store a family-plan episode (no episode-id in trace)
- does NOT participate in the membrane

A real kernel family should:
- execute through `execute-rule` and `apply-effects`
- store a family-plan episode with typed facts
- participate in the membrane (use-history, promotion)

What should the rehearsal plan body return? What effects should it
declare? Specifically:

- What facts go into the episode payload? (Not prose — typed
  structured facts about what was rehearsed)
- What `:reframe-facts` or equivalent makes a rehearsal episode
  promotion-eligible? (Rationalization uses `:reframe-facts`,
  reversal uses `:input-facts`. What's the rehearsal equivalent?)
- How should rehearsal update Tony's character state through the
  executor boundary rather than direct world mutation?
- Should rehearsal episodes be promotion-eligible by default, or
  should they require a different evidence path?

### 1c. The reappraisal connection

The benchmark-local rehearsal directly calls `project-mural-appraisal`
after updating Tony state. In a real kernel family, reappraisal
should happen as a kernel process after family effects settle (per
the 5 Pro research: after effect application, before episode storage).

- How should the reappraisal pass work when rehearsal is a real
  kernel family instead of benchmark-local?
- Should reappraisal be triggered by any family that modifies
  character state, or specifically by rehearsal?
- Where in the `run-family-plan` flow does the Tony state update
  and reappraisal insertion point belong?

### 1d. Interaction with other families

Rehearsal episodes should be reusable by other families (that's how
cross-family evidence accumulates and the membrane exercises).

- What indices should rehearsal episodes carry so that
  rationalization and reversal can find them through retrieval?
- Should rehearsal episodes be valid stored sources for
  rationalization (as reframe material) or for reversal (as
  counterfactual sources)? Or neither — just as retrieval hits that
  contribute to provenance?
- What's the most natural cross-family bridge from rehearsal to
  another family?

## Question 2: What happens at 50-100 cycles?

The current miniworld runs 20 cycles. We want to run 50-100 and
observe. But before running, we should know what to watch for.

Read the miniworld code carefully — its pressure dynamics, decay
rates, situation cycling logic, candidate scoring, fatigue
adjustments, recent-choice tracking.

### 2a. Likely failure modes

Given the current miniworld implementation:

- Where would the dynamics likely collapse at 50+ cycles? Would one
  family dominate? Would one situation get starved? Would Tony's
  regulation state saturate?
- The miniworld has fatigue adjustments (exact-repeat penalty,
  situation fatigue, family fatigue). Are these strong enough to
  prevent collapse over 50 cycles? Or too strong (everything
  flattens to uniform)?
- Tony's state uses `blend-tony-state` with fixed deltas per
  operator. Over many cycles, does the state converge to a fixed
  point? Oscillate? Drift?
- The two promoted episodes get reused through the rehearsal probe.
  Over 50 cycles, would the same two episodes keep winning every
  race, or would new episodes eventually compete?

### 2b. What to measure

What trace fields and summary counters would distinguish these three
outcomes:

1. **Real accumulation**: the system at cycle 50 can do things it
   couldn't at cycle 1 (different episodes available, different
   families winning, different situations accessible)
2. **Stable cycling**: the system is non-collapsed but repetitive
   (same pattern every 4-8 cycles, no new episodes promoted, no new
   bridges)
3. **Degraded dynamics**: collapse into one family or one situation,
   or Tony state saturated at ceiling/floor

Propose specific measurements, not just "track everything."

### 2c. What 50 cycles should look like if it's working

If the miniworld is producing genuine accumulation, what would the
50-cycle summary look like compared to the 20-cycle summary?

- More promoted episodes, or about the same?
- More distinct cross-family paths, or the same two?
- Tony state: should it stabilize, keep climbing, or oscillate?
- Family distribution: should it shift over time as the concern
  landscape changes?
- What would make you say "this system learned something between
  cycle 20 and cycle 50"?

## Output preference

For Question 1: give specific RuleV1 antecedent schemas, effect
declarations, and episode shapes. Not prose descriptions — actual
Clojure-shaped data structures that match the patterns in
`goal_family_rules.clj`.

For Question 2: give specific failure-mode predictions grounded in
the actual code (cite the decay rates, fatigue coefficients, blend
deltas you're reasoning about), and specific measurements with
threshold values where possible.
