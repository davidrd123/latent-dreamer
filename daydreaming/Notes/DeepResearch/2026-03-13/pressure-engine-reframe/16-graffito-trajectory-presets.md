# Graffito Trajectory Presets

Date: 2026-03-14

## Purpose

Run a minimal conductor-expressivity test on the Graffito pilot
without changing the graph again.

The question was:

- do simple conductor bias presets produce meaningfully different
  traversals over the same graph?

If yes, the conductor surface is already reaching the scheduler.
If no, priority tiers or graph shape are still dominating.

## Presets

All runs used the same graph and the same seed.

### 1. Neutral

- `conductor_tension_bias: 0.0`
- `conductor_energy_bias: 0.0`

Artifacts:

- `daydreaming/out/graffito_pilot_neutral_trace.yaml`
- `daydreaming/out/graffito_pilot_neutral_debug.jsonl`
- `daydreaming/out/graffito_pilot_neutral_playlist.txt`

### 2. Sustained High

- `conductor_tension_bias: +0.15`
- `conductor_energy_bias: +0.10`

Artifacts:

- `daydreaming/out/graffito_pilot_high_trace.yaml`
- `daydreaming/out/graffito_pilot_high_debug.jsonl`
- `daydreaming/out/graffito_pilot_high_playlist.txt`

### 3. Early Release

- `conductor_tension_bias: -0.15`
- `conductor_energy_bias: -0.10`

Artifacts:

- `daydreaming/out/graffito_pilot_early_release_trace.yaml`
- `daydreaming/out/graffito_pilot_early_release_debug.jsonl`
- `daydreaming/out/graffito_pilot_early_release_playlist.txt`

## Result

The conductor surface is **partially** working.

### Neutral vs. Sustained High

These two runs were effectively identical.

- same node sequence
- same switch timing
- same node coverage
- same final contour

Interpretation:

- positive bias is not yet strong enough to change the winning
  traversal under the current tier + scoring balance
- the scheduler is still being governed mostly by graph shape plus
  priority structure in the upward-pressure case

### Early Release

This run diverged.

Differences from neutral:

- dropped back into release-oriented `s2` material earlier
- delayed the second big street escalation
- never reached the same high-tension second build profile
- ended on a renewed street escalation rather than a softer apartment
  release

This means the downward bias is already legible enough to bend the
traversal.

## Structural Summary

### Neutral

- `9` unique nodes
- `11` cycles in `s1`
- `7` cycles in `s2`
- switch cycles: `5, 8, 10, 12, 13, 15`
- max tension: `0.9`

### Sustained High

- identical to neutral on this run

### Early Release

- `9` unique nodes
- `11` cycles in `s1`
- `7` cycles in `s2`
- switch cycles: `5, 8, 9, 11, 12, 14`
- max tension: `0.8`

So the difference is not gross coverage.
It is timing and contour.

## Takeaway

This is a useful result, not a failed one.

The conductor surface is real enough to matter, but asymmetric:

- negative / release-oriented bias is already visible
- positive / sustained-high bias is not yet strong enough to pull the
  traversal away from the same neutral path

That suggests the next issue is not whether conductor bias reaches the
model at all.
It does.

The issue is that the current positive-pressure side is still too
constrained by the small graph and the existing tier structure.

## Next Move

The next narrow question is:

- do we strengthen the conductor bias effect in scoring
- or do we give the graph one more high-pressure alternative so a
  positive bias has somewhere else to go?

For Graffito, the more honest answer is probably the second one.
The current graph has more release routes than sustained high-tension
routes, so the positive bias has less room to express itself.
