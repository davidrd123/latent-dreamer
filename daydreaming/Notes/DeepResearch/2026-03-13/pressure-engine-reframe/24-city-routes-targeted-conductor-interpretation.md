# City Routes Targeted Conductor Interpretation

Date: 2026-03-14

## Purpose

Interpret the outcomes of:

- `21-city-routes-robustness-sweep.md`
- `23-city-routes-targeted-conductor.md`

This note covers two separate follow-on questions:

1. did the new event-reuse tuning materially improve arm C?
2. did the first targeted conductor experiment actually move the path?

## 1. Event-reuse tuning result

The added event-reuse penalty was too weak to change the aggregate
robustness picture.

The sweep stayed effectively the same:

- arm C still hit `s5_the_threshold` in `15/15` cases
- arm C still broadened event structure in the weaker arm-B cases
- `e4` reuse remained `3/15`

So the penalty did not hurt the arm-C structural gains.
But it also did not solve the event-reuse problem yet.

That means the tuning was safe, but not strong enough.

## 2. Targeted conductor result

The first targeted conductor pass produced **zero** path divergence.

Across seeds `7, 11, 19` and the presets:

- `neutral`
- `spectacle_blackout`
- `threshold_lockdown`
- `refuge_pause`

the feature arm produced the same path for each seed.

The report in `23-city-routes-targeted-conductor.md` says this plainly:

- seeds with distinct preset paths: `0/3`

That is a real result.

## Why the targeted conductor failed

The failure is informative.

The problem is not that the conductor hooks are broken.
The problem is that the first targeted presets mostly reinforced the path
arm C already prefers.

Examples:

- arm C already likes `s4 -> s5 -> s6 -> s3`
- boosting `s4_public_spectacle` and `e2_blackout_siren` pushes toward
  the same broad route
- boosting `s5_the_threshold` and `e4_bridge_lockdown` also pushes toward
  the same broad route
- boosting `s3_false_refuge` still rewards a path arm C already reaches
  late

So this first targeted-conductor pass was not really adversarial enough
to test expressivity.

It asked:

- "can the conductor push harder toward the route arm C already thinks is
  best?"

The answer was:

- "yes, but that does not change the selected path"

## The deeper diagnosis

There are two likely mechanisms here.

### A. The conductor entered too low in the stack

The targeted conductor currently enters arm C as a bounded addition to
feature score.

That means it can only change selection when:

- competing nodes survive the same priority tier filtering
- and their feature scores are already close enough that the bias can
  break the tie

If the structural route is already strongly preferred, the conductor
never gets enough leverage.

### B. The tested presets were convergent, not contrasting

None of the first targeted presets tried to pull the traversal
*away* from the default arm-C route.

So they were not the right test of expressivity.

To test conductor range, the presets need to create structural tension
with the default policy, not simply reinforce it.

## What this means

The current state is:

- arm C is robust as a structural scheduler
- arm C is not yet expressive as a conducted scheduler on City Routes
- the null result does **not** mean conductor is impossible
- it means the first targeted experiment was too aligned with the
  feature layer's existing preferences

## Better next conductor test

The next conductor experiment should use **contrasting** presets rather
than reinforcing ones.

Examples:

- `spectacle_hold`
  - boost `s4`
  - suppress `s5`
  - suppress `e4`
- `threshold_drive`
  - boost `s5`
  - suppress `s3`
  - boost `e4`
- `refuge_hold`
  - boost `s3`
  - suppress `s6`
  - suppress `e3`
- `exchange_fast`
  - boost `s6`
  - suppress `s4`
  - boost `e3`

Those presets would ask for genuinely different route shapes.

That is a better expressivity test than simply saying "more spectacle"
or "more threshold" when the neutral arm-C path already wants both.

## Recommended Next Move

Do not widen the graph.

Do not go back to abstract architecture work.

The next pass should be:

1. strengthen the event-reuse penalty or make it more recency-sensitive
2. test a second targeted-conductor pass with **contrasting** presets
3. only then decide whether conductor needs to enter earlier in the
   stack, such as priority promotion instead of feature-only bias
