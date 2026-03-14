# City Routes Arms A/B Pass

Date: 2026-03-14

## Purpose

Carry the Graffito pilot harness over to the first full City Routes
substrate and run arms A and B before adding the DODM feature registry.

This pass had two goals:

- confirm that the shared traversal harness works on the larger graph
- determine whether the first City Routes behavior problems belong to
  the scheduler or the authored graph

Fixture:

- `daydreaming/fixtures/city_routes_experiment_1_v0.yaml`

Runner:

- `daydreaming/city_routes_pilot.py`

Outputs:

- `daydreaming/out/city_routes_run_1_*`
- `daydreaming/out/city_routes_run_2_*`

## First Run

The first A/B run proved the harness carry-over immediately:

- both arms generated valid traces and playlists
- the scheduler used the richer graph without code changes
- the debug output was interpretable

But it also surfaced a real substrate issue:

- both arms underused `s3_false_refuge` and `s4_public_spectacle`
- both arms converged on a strong `s2 -> s5 -> s6` spine
- threshold and exchange nodes had more forced continuations than the
  refuge and spectacle lanes

This was not a Graffito-style single-seam failure.
It was a richer-graph version of the same underlying problem:

- too much authored pressure was concentrated in one lane
- the scheduler spent its structural freedom there

## Tuning Pass

The fix stayed in the graph, not the scheduler.

### What changed

- rerouted one early detour exit from threshold into spectacle:
  - `c_s2_n02_produce_cart_bottleneck -> c_s4_n01_strobe_crowd_swallow`
- rerouted exchange aftermath into refuge instead of directly back to
  threshold:
  - `c_s6_n05_corrected_transfer -> c_s3_n03_map_spread_under_tungsten`
- softened two escalation auto-wins:
  - `c_s5_n02_roof_gap_commit` priority `1 -> 0`
  - `c_s6_n02_wrong_receiver_silhouette` priority `1 -> 0`
- restored a second graph attractor without recreating the old spine:
  - `c_s5_n05_checkpoint_seen_from_above -> c_s6_n03_silver_case_under_headlight_sweep`

### Why these edits

The first run showed that the scheduler was not malfunctioning.
It was following the authored graph exactly where the graph had the most
urgent-looking continuations.

So the right intervention was:

- give spectacle an earlier chance to win
- give refuge a real exchange aftermath return
- stop a couple of escalation nodes from beating more legible
  alternatives purely on tier

## Structural Guardrails

The NetworkX-based fixture tests mattered here.

One intermediate tuning version improved coverage but collapsed the
attractor distribution from two strong return points down to one.

That got caught immediately by:

- `tests/test_city_routes_fixture.py`

The final tuned fixture restored two attractors:

- `c_s3_n03_map_spread_under_tungsten`
- `c_s6_n03_silver_case_under_headlight_sweep`

So the current run is not just behaviorally better.
It is still structurally inside the authored graph constraints.

## Second Run

The second A/B run is the version to keep.

### Baseline

- `18` unique nodes
- situations:
  - `s1_the_run: 5`
  - `s2_the_detour: 4`
  - `s5_the_threshold: 3`
  - `s6_the_exchange: 4`
  - `s3_false_refuge: 2`
- still follows a relatively literal route spine
- now reaches refuge after exchange instead of looping back to threshold

### Scheduler

- `15` unique nodes
- `7` situation switches across `18` cycles
- situations:
  - `s1_the_run: 4`
  - `s2_the_detour: 5`
  - `s4_public_spectacle: 3`
  - `s6_the_exchange: 3`
  - `s3_false_refuge: 3`
- event touches:
  - `e1_train_missed`
  - `e3_wrong_handoff`

The important difference is not raw node count.
It is contour:

- the scheduler now routes through spectacle and refuge
- it no longer collapses into the threshold/exchange loop from run 1
- it uses exchange as a middle-to-late structural move rather than a
  basin it cannot leave

## Interpretation

This is the result we wanted before arm C.

The larger graph already teaches something useful:

- City Routes does not have Graffito's single-seam problem
- but authored priority and edge structure still dominate behavior if
  left untuned
- structural graph evaluation catches these problems much faster than
  scheduler-only inspection

Most importantly:

- arm B is now clearly operating on a richer substrate than Graffito
- the next layer of improvement no longer obviously belongs to graph
  triage alone

That means the full experiment can move forward.

## Recommended Next Move

Do not widen the fixture again immediately.

The next step should be:

1. keep this tuned `v0` substrate
2. add arm C as the DODM-style feature registry layer
3. compare A / B / C on the current City Routes graph

If arm C does not materially improve on this tuned arm-B behavior, then
the named feature registry has not earned its complexity.
