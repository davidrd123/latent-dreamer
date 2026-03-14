# City Routes Arm C Pass

Date: 2026-03-14

## Purpose

Add the first DODM-style feature-registry arm to the shared traversal
harness and test whether it produces a meaningfully different traversal
from arm B on the tuned City Routes substrate.

This is the first direct test of whether the extra L3 scoring layer
earns its complexity.

Inputs:

- `daydreaming/fixtures/city_routes_experiment_1_v0.yaml`
- `daydreaming/graffito_pilot.py`
- `daydreaming/city_routes_pilot.py`

Outputs:

- `daydreaming/out/city_routes_run_3_*`
- `daydreaming/out/city_routes_run_4_*`

## What was added

Arm C keeps the same broad runtime shape as arm B:

- availability
- priority tiers
- shared traversal state
- target trajectory
- soft local bias

But it adds a small feature registry over candidate nodes:

- `trajectory_fit`
- `preparation_satisfied`
- `situation_mixing`
- `event_homing`
- `recall_value`
- `structural_tension`
- `exhaustion_penalty`
- `manipulation_penalty`

All of these remain graph-native.
Nothing in this pass imports L2 concern objects or contexts.

## First arm-C result

The first arm-C run worked technically but failed the real test.

### What happened

- arm C produced the same traversal as arm B on the same seed
- debug rows showed that the feature registry was changing candidate
  preferences
- but the existing continuity / recency / activation multiplier still
  dominated the final choice too often

In other words:

- arm C initially gave better introspection
- but not a meaningfully different traversal

That was not good enough.

## One tuning correction

The correction was narrow and principled.

For arm C only:

- keep the feature score as the primary authorial scorer
- reduce the old local soft-weight multiplier into a gentler
  soft-bias term

This matches the intended pipeline better:

- feature registry first
- soft local bias second

## Final arm-C result

The tuned second pass is the one to keep.

### Baseline

- `18` unique nodes
- situations:
  - `s1_the_run: 5`
  - `s2_the_detour: 4`
  - `s5_the_threshold: 3`
  - `s6_the_exchange: 4`
  - `s3_false_refuge: 2`
- events:
  - `e1_train_missed`
  - `e3_wrong_handoff` x2

### Arm B / scheduler

- `15` unique nodes
- `7` situation switches
- situations:
  - `s1_the_run: 4`
  - `s2_the_detour: 5`
  - `s4_public_spectacle: 3`
  - `s6_the_exchange: 3`
  - `s3_false_refuge: 3`
- events:
  - `e1_train_missed`
  - `e3_wrong_handoff`

The tuned arm-B path still misses `s5_the_threshold`.

### Arm C / feature registry

- `16` unique nodes
- `6` situation switches
- situations:
  - `s1_the_run: 3`
  - `s2_the_detour: 2`
  - `s4_public_spectacle: 2`
  - `s5_the_threshold: 5`
  - `s6_the_exchange: 3`
  - `s3_false_refuge: 3`
- events:
  - `e1_train_missed`
  - `e2_blackout_siren`
  - `e4_bridge_lockdown` x2
  - `e3_wrong_handoff`

This is a genuinely different traversal.

It does not just smooth arm B.
It reorganizes the route.

## What changed structurally

Arm C now does three things arm B did not:

### 1. It pulls threshold material back in

Arm B dropped `s5_the_threshold` entirely on this seed.

Arm C reintroduced it and made it central:

- `ladder_climb`
- `sodium_bridge_view`
- `checkpoint_seen_from_above`

That is the clearest sign that the feature registry is doing real work.

### 2. It approaches more event material explicitly

Arm C touches:

- `e2_blackout_siren`
- `e4_bridge_lockdown`
- `e3_wrong_handoff`

Arm B only touched:

- `e1_train_missed`
- `e3_wrong_handoff`

So the event-oriented terms are not cosmetic.
They change the run.

### 3. It uses spectacle as setup, not only as texture

The arm-C route goes:

- `strobe_crowd_swallow`
- `stage_light_freeze`
- `ladder_climb`
- `sodium_bridge_view`
- `checkpoint_seen_from_above`

That is not the same structural logic as arm B.
It turns spectacle into a setup path toward threshold and event pressure.

## Why it changed

The debug output shows the new arm behaving differently for reasons that
match the intended feature meanings.

Examples:

- at cycle 6, `strobe_crowd_swallow` beats the local detour continuation
  because `situation_mixing` and `structural_tension` make it worth the
  shift
- at cycle 9, `sodium_bridge_view` beats `roof_gap_commit`, pulling the
  run toward event-bearing threshold material rather than pure escalation
- at cycle 10, `checkpoint_seen_from_above` becomes attractive through
  `event_homing`

That is the kind of explanation arm C was supposed to make possible.

## Interpretation

This is enough to say that arm C has cleared the first value bar.

Not because it scored better on paper.
Because it produced a traversal that arm B did not.

The differences are structural:

- threshold is back
- more event material is legible
- spectacle becomes setup rather than only color

That is the first real evidence that the DODM-style layer can add
something beyond a Façade-shaped trajectory scorer.

## Remaining caution

Arm C is promising, but not settled.

The current run still shows one pressure point:

- `e4_bridge_lockdown` is touched twice

So the feature layer is now strong enough to steer into event-bearing
threshold material, but not yet disciplined enough to avoid some
event-heavy reuse.

That is a real next-pass tuning question, not a reason to discard arm C.

## Recommended Next Move

Do not widen the graph again.

The next pass should stay on the current substrate and ask:

1. can arm C keep its threshold/event gains while reducing repeated
   event-heavy reuse?
2. does arm C keep its advantage across a few more seeds and conductor
   settings?

If yes, then the feature registry has started to earn its complexity for
real.
