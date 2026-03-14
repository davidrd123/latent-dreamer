# Graffito Pilot Run 2

Date: 2026-03-14

## Purpose

This was the second Graffito pilot pass after `14-graffito-pilot-run-1.md`.

The aim was narrow:

- widen the crossing seam so `g_s2_n01_camera_enters_window` is not the only meaningful entry into `s2`
- expose the main target-trajectory knobs in the harness
- rerun the same two-arm comparison without adding any new feature layer

## Changes

### Graph changes

In `daydreaming/fixtures/graffito_v0_scenes_3_4.yaml`:

- added `g_s1_n06_drone_pullback -> g_s2_n02_monk_painting`
- added `g_s1_n05v_cab_overwhelm -> g_s2_n03_tony_bursts_in`
- added `g_s2_n02_monk_painting -> g_s1_n06_drone_pullback`
- tuned `g_s2_n02_monk_painting` into a more usable calm seam:
  - `delta_tension: -0.14`
  - `priority_tier: 1`
- reduced `g_s1_n06_drone_pullback` base `priority_tier` from `1` to `0`
  so it would stop forcing premature returns from `s2`

### Harness changes

In `daydreaming/graffito_pilot.py`:

- added explicit target-trajectory knobs:
  - `--release-start-progress`
  - `--target-tension-peak`
  - `--target-tension-end`
  - `--target-energy-peak`
  - `--target-energy-end`
- fixed debug snapshotting so `situation_activation` and
  `last_seen_cycle` are recorded per cycle instead of leaking final
  mutable state into earlier rows

## Verification

- `uv run --project . python -m unittest tests.test_graffito_pilot`
- `uv run --project . python -m py_compile daydreaming/graffito_pilot.py tests/test_graffito_pilot.py`
- reran pilot traces and playlists through the existing render path

## Result

The second pass improved on the first pass.

Pilot run 2:

- used `9` unique nodes over `18` cycles
- split `11` cycles in `s1` and `7` in `s2`
- made `6` cross-situation moves
- now visits:
  - `g_s2_n02_monk_painting`
  - `g_s2_n03_tony_bursts_in`
  - `g_s1_n06_drone_pullback`
- no longer collapses into the earlier `camera -> monk -> drone`
  micro-loop

The current pilot trace is still not "finished," but it is more
credible:

- build in `s1`
- release into `s2`
- stay in `s2` long enough to surface apartment material
- re-enter `s1` for renewed escalation

Importantly, cross-situation movement is no longer only a release move.
The graph already includes the rising bridge
`g_s2_n03_tony_bursts_in -> g_s1_n04_eyes_searching`, and run 2
actually used it. That means the pilot now has at least a minimal
structural choice between:

- cross-situation for release
- cross-situation for renewed escalation

## What The Second Pass Clarified

- the scheduler shape is still holding up
- graph authoring matters as much as scheduler logic at this scale
- base priority tiers on seam nodes can distort traversal much more than
  they first appear to
- the pilot remains narrow enough that graph-seam edits are still the
  right lever

## Remaining Weaknesses

- `g_s2_n01_camera_enters_window` is still the dominant ingress seam
- the new `g_s1_n05v_cab_overwhelm -> g_s2_n03_tony_bursts_in` edge is
  available but did not surface in this run
- the graph is still too small to support broader claims
- trajectory presets have become configurable, but they have not yet
  been explored systematically

## Current Read

This is a successful second pass.

The pilot still earns continuation, and the result is stronger than run
1. The next move should stay narrow:

- try 1-2 trajectory presets or conductor-bias settings
- optionally add one more ingress seam if `camera_enters_window`
  continues to dominate
- do not add DODM or Suspenser layers yet
