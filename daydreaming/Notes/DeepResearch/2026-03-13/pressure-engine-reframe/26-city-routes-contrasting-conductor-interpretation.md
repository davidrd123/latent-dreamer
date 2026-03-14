# City Routes Contrasting Conductor Interpretation

Date: 2026-03-14

## Purpose

Interpret the result of:

- `25-city-routes-contrasting-conductor.md`

This is the final narrow scheduler-side conductor check on the current
City Routes substrate.

## Result

The contrasting presets produced **real but still limited** movement.

Across seeds `7, 11, 19`:

- seeds with distinct preset paths: `2/3`
- average distinct paths per seed: `1.67`

Seeds `11` and `19` showed preset divergence.

The divergent paths came from:

- `threshold_drive`
- `exchange_fast`

Those presets:

- either pulled `s5_the_threshold` and `e4_bridge_lockdown` back in
  earlier (`threshold_drive`)
- or cut threshold material out and reached `e3_wrong_handoff` earlier
  (`exchange_fast`)

`spectacle_hold` and `refuge_hold` stayed much closer to neutral in this
rerun.

## What changed from the first targeted pass

This is no longer a weak edge-case result.

The first targeted-conductor experiment in
`24-city-routes-targeted-conductor-interpretation.md` produced `0/3`
seeds with distinct preset paths.

The first contrasting pass produced `1/3`.

After the overdetermination/scoring cleanup, the rerun now produces
`2/3`.

So the conductor is not completely washed out by the feature registry.
It can bend the traversal in more than one direction when asked to
oppose the default route rather than reinforce it.

## What this means

The conductor surface on City Routes is:

- real
- moderate but still limited
- asymmetric

It is real because contrasting presets can change the path.

It is still limited because one seed (`7`) remained unchanged across all
presets.

It is asymmetric because only some targeted presets currently earn clear
structural differences. `threshold_drive` and `exchange_fast` moved the
route. `spectacle_hold` and `refuge_hold` largely did not.

## Current diagnosis

The likely issue is still not that the conductor hooks are broken.

The more plausible diagnosis is:

1. arm C's structural feature layer is already strongly shaping the
   route
2. conductor bias now has enough leverage to move the route when it
   aligns with strong alternative graph structure
3. some requested directions still do not have enough distinct support
   in the graph/weights to produce a visibly different traversal

So the scheduler can now be bent, but it is not yet a broad or mature
performance surface on this substrate.

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
2. test whether the now-partial conductor control is more legible in the
   real playback stack than it is in trace-only analysis
3. if live control still matters, revisit conductor mapping at the level
   of performance controls rather than more offline feature tweaking
