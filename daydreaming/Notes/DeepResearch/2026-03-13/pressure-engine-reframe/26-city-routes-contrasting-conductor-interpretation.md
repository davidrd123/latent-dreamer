# City Routes Contrasting Conductor Interpretation

Date: 2026-03-14

## Purpose

Interpret the result of:

- `25-city-routes-contrasting-conductor.md`

This is the final narrow scheduler-side conductor check on the current
City Routes substrate.

## Result

The contrasting presets produced **some** movement, but not much.

Across seeds `7, 11, 19`:

- seeds with distinct preset paths: `1/3`
- average distinct paths per seed: `1.33`

Only seed `19` showed preset divergence.

The divergent paths came from:

- `spectacle_hold`
- `refuge_hold`

Those presets:

- reduced late `s5_the_threshold` reuse
- removed `e4_bridge_lockdown`
- kept most of the rest of the route intact

`threshold_drive` and `exchange_fast` did not produce stronger targeted
behavior than neutral in this pass.

## What changed from the first targeted pass

This is no longer a total null result.

The first targeted-conductor experiment in
`24-city-routes-targeted-conductor-interpretation.md` produced `0/3`
seeds with distinct preset paths.

This contrasting pass produced `1/3`.

So the conductor is not completely washed out by the feature registry.
It can bend the traversal a little when asked to oppose the default
route rather than reinforce it.

## What this means

The conductor surface on City Routes is:

- real
- weak
- asymmetric

It is real because contrasting presets can change the path.

It is weak because this only happened in one seed.

It is asymmetric because suppressive presets moved the traversal more
than additive ones. The system could prune the late threshold/event pull
more easily than it could force a stronger alternative route.

## Current diagnosis

The likely issue is not that the conductor hooks are broken.

The more plausible diagnosis is:

1. arm C's structural feature layer is already strongly shaping the
   route
2. conductor bias enters too late or too softly to override that shape
   consistently
3. suppressive pressure is easier to express than additive rerouting on
   this graph

So the scheduler can be nudged, but not yet performed with robust range
on this substrate.

## Conclusion

This is probably the natural endpoint of the scheduler-side L3
experiment on City Routes.

The important questions are now answered well enough:

- Façade-shaped traversal beats baseline
- that shape scales to a richer graph
- DODM-style features add seed-robust structural behavior
- conductor influence exists, but is not yet strong or expressive enough
  to count as a mature performance surface on City Routes

## Recommended Next Move

Do not keep tuning the scheduler in circles.

Treat the L3 scheduler experiment as functionally complete for this
substrate and move the next questions to the performance lane:

1. render and watch representative arm-B and arm-C runs
2. test whether conductor control is more legible in the real playback
   stack than it is in trace-only analysis
3. if live control still matters, revisit conductor mapping at the level
   of performance controls rather than more offline feature tweaking
