# Graffito Pilot Run 1

Date: 2026-03-14

## Scope

This was the first executed pass of the Graffito L3 pilot described in `13-execution-roadmap.md`.

The goal was deliberately narrow:

- upgrade the existing `graffito_v0_scenes_3_4.yaml` slice into a traversal-capable graph
- build a standalone Python scheduler harness beside the current render path
- compare two generated arms over the same graph:
  - baseline walk
  - Façade-shaped pilot scheduler
- keep the output compatible with the existing trace -> playlist -> Scope path

## What Was Built

- Added explicit edges plus `delta_tension`, `delta_energy`, and `priority_tier` to `daydreaming/fixtures/graffito_v0_scenes_3_4.yaml`
- Added standalone harness `daydreaming/graffito_pilot.py`
- Added unit coverage in `tests/test_graffito_pilot.py`
- Generated both trace YAML and debug JSONL for each arm
- Rendered both generated traces through `daydreaming/render_trace.py`

## Artifacts

- `daydreaming/out/graffito_pilot_baseline_trace.yaml`
- `daydreaming/out/graffito_pilot_baseline_debug.jsonl`
- `daydreaming/out/graffito_pilot_baseline_playlist.txt`
- `daydreaming/out/graffito_pilot_scheduler_trace.yaml`
- `daydreaming/out/graffito_pilot_scheduler_debug.jsonl`
- `daydreaming/out/graffito_pilot_scheduler_playlist.txt`

## Result

The pilot arm is already meaningfully different from the simple baseline.

Baseline:

- stayed entirely inside `s1`
- used 8 unique nodes over 18 cycles
- made 0 cross-situation moves
- saturated tension/energy quickly and then mostly cycled inside the street cluster

Pilot:

- used 10 unique nodes over 18 cycles
- split `10` cycles in `s1` and `8` in `s2`
- made `5` cross-situation moves
- produced visible `ROVING` shifts and a more legible release/escalation rhythm

## What Improved During Run 1

The first live pass exposed too much ping-pong on one cross-situation edge. The harness was then tightened so both arms share the same minimum availability filter:

- avoid immediate back-edge reversals when other options exist
- suppress candidates that are already overrepresented in the recent path

After that change, the pilot stopped immediately bouncing and started producing clearer cluster-level movement.

## What Still Looks Weak

- The baseline remains almost completely trapped in the street cluster
- The pilot still leans heavily on `g_s2_n01_camera_enters_window` as the main crossing seam
- The graph is still a pilot substrate, not a full experiment substrate
- Target-trajectory behavior is visible, but still needs tuning against a few explicit harness knobs rather than being treated as settled

## Immediate Takeaway

This run clears the bar for continuing.

The Façade-shaped scheduler is doing something legible that the baseline is not: it is using tension/release plus cluster-shift logic to create a more intentional traversal over the same authored material.

That does **not** prove the full L3 thesis. It does justify a second pilot pass rather than abandoning the scheduler shape.

## Next Adjustments

- expose a few harness knobs explicitly:
  - situation activation decay
  - revisit suppression strength
  - target trajectory shape
  - randomness temperature
- reduce over-reliance on the single `s1 <-> s2` seam node
- keep the Graffito pilot narrow
- do **not** add DODM features, Suspenser terms, or L2-style semantics yet
