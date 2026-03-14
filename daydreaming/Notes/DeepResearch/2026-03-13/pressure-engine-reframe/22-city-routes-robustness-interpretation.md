# City Routes Robustness Interpretation

Date: 2026-03-14

## Purpose

Interpret the generated sweep report in:

- `21-city-routes-robustness-sweep.md`

The report has the raw counts.
This note says what they mean.

## Top-line read

Arm C's threshold / event gains are real enough to survive a small
multi-seed sweep.

What is **not** yet real is conductor expressivity on City Routes.

So the result is:

- **feature-registry robustness across seeds:** yes, enough to matter
- **feature-registry validation in all respects:** not yet
- **conductor robustness / expressivity:** not yet shown on this graph

## What held up

### 1. Threshold came back reliably

Across `15` sweep cases:

- arm B visited `s5_the_threshold` in `9/15`
- arm C visited `s5_the_threshold` in `15/15`

That is the cleanest robustness result in the sweep.

The feature layer is not just occasionally recovering threshold
material.
It is systematically pulling it back into the route.

### 2. Arm C stayed structurally broader

Across the same sweep:

- arm B average situations visited: `5.6`
- arm C average situations visited: `6.0`

So arm C is not only revisiting threshold.
It is also consistently maintaining full situation coverage.

### 3. Event approach improved in the cases that mattered

Arm B and arm C both had at least one prepared event in every run,
so the broadest metric is not very discriminating.

The real comparison is the weaker arm-B cases:

- in the `seed 7` and `seed 11` families, arm B missed threshold and
  only reached `e1` and `e3`
- arm C pulled in `e2` and `e4` and increased prepared event visits

That happened in `6/15` cases.

Those are exactly the cases where arm C seems to be earning itself.

### 4. Arm C is not just arm B with nicer debug

Only `3/15` sweep cases produced an identical arm-B / arm-C path.

That means the feature layer is usually changing the actual traversal,
not merely how the traversal is explained after the fact.

## What did not hold up yet

### 1. Conductor presets barely moved the traversal

This is the biggest unresolved result in the sweep.

For arm C:

- each seed produced the same path across `neutral`,
  `sustained_high`, and `early_release`

For arm B:

- only one seed produced more than one path across presets

So this sweep is mostly telling us about **seed robustness**.
It is not yet telling us much about **conductor expressivity**.

That means the current bias terms are still too weak relative to the
graph structure and feature weights, or the graph does not have enough
conductor-sensitive alternatives for the current bias range to matter.

### 2. Event reuse moved, but did not disappear

The original concern was repeated `e4_bridge_lockdown`.

The sweep shows:

- arm B has `e4` reuse in `3/15` runs
- arm C also has `e4` reuse in `3/15` runs

So arm C did **not** solve event-heavy reuse outright.

What it often did instead was shift the reuse pressure:

- some arm-C runs trade repeated `e4` for repeated `e3`

That is still better than pure threshold fixation in some cases, but it
means the feature layer needs another pass if the goal is cleaner event
discipline rather than simply more event contact.

### 3. Release contour is not well captured by the current metric

The sweep's `release_moves` metric stayed at `0` across the board.

That does **not** mean all runs were flat.
It means the current contour metric is too blunt for these rounded,
short traces.

So the robustness story should not lean on that column.

## Practical interpretation

The feature registry has now cleared a stronger bar than the single
seed-7 neutral result.

It is not just a lucky path.

The narrow claim we can defend now is:

- the DODM-style feature layer consistently helps recover threshold
  material and broader event structure on the City Routes graph
- especially in the cases where the Façade-only scheduler underperforms

The narrower claim we **cannot** defend yet is:

- that the same layer already gives you a musically or performatively
  expressive conductor surface

That remains open.

## Recommended Next Move

Keep the graph fixed and do one more narrow tuning pass on arm C:

1. reduce repeated event-heavy reuse
2. then retest conductor expressivity with stronger or differently
   targeted bias terms

In other words:

- do **not** go back to abstract architecture work
- do **not** widen the graph again yet
- do **not** declare the conductor problem solved

The next pass is now a focused tuning/evaluation pass, not a discovery
pass.
