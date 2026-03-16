# Tessa patch test summary

Date: 2026-03-15

Graphs:
- H: `daydreaming/fixtures/tessa_patch_test_hand_v0.yaml`
- G: `daydreaming/fixtures/tessa_patch_test_generated_v0.yaml`

Traversal settings:
- arm: `feature`
- cycles: `12`
- start node: `t_start_afterimage`
- seeds: `3`, `7`, `19`

## Outcome

The generated patch survives traversal.

Across all 3 seeds for both H and G:
- both situations are visited
- threshold region is reached in all seeds
- no tight 2-node loop appears before cycle 8
- patch nodes are actually selected, not dead leaves

Patch visitation by seed:
- seed 3: `anchor -> bridge`
- seed 7: `intensifier -> bridge`
- seed 19: `contrast -> threshold -> bridge`

So collectively, the patch covers:
- anchor
- intensifier
- contrast
- bridge

Threshold reach:
- seed 3: cycle 4
- seed 7: cycle 4
- seed 19: cycle 3

## Critical interpretation

H and G produced identical traversal traces.

That does **not** mean the generated prose is equivalent to the hand-authored prose.
It means the current `graffito_pilot.py` feature arm selects nodes from:
- `delta_tension`
- `delta_energy`
- `setup_refs`
- `payoff_refs`
- `origin_pressure_refs`
- `pressure_tags`
- `event_commit_potential`
- `resolution_path_count`
- `priority_tier`
- situation mixing / recall / penalties

It does **not** inspect `visual_description` or other prose content during node selection.
So once the generated nodes were curated into the same traversal-facing slots, the scheduler treated H and G the same.

## What this proves

This is a real bridge success, but a narrow one.

It proves:
- generated-curated Tessa nodes can be slotted into a live micrograph
- the patch does not break traversal
- the scheduler will actually visit generated patch nodes
- threshold progression still works with the generated patch inserted

It does not prove:
- the scheduler prefers generated prose over hand-authored prose
- prose-level dramatic differences matter to L3 yet
- H vs G can currently be distinguished by the feature arm alone

## Next implication

If we want a stronger H vs G comparison, we need one of:
- human judgment over rendered traces / playlists
- content-sensitive traversal features derived from prose or sidecar semantics
- a downstream layer that consumes the patch text itself, not just graph annotations

But as a first bridge test, this passed:
- the generated patch survives insertion and traversal
- no additional diversity work is required before moving on
