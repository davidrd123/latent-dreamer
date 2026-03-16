# Kai patch test summary

Date: 2026-03-15

Graphs:
- H: `daydreaming/fixtures/kai_patch_test_hand_v0.yaml`
- G: `daydreaming/fixtures/kai_patch_test_generated_v0.yaml`

Traversal settings:
- arm: `feature`
- cycles: `12`
- start node: `k_start_table_letter`
- seeds: `3`, `7`, `19`

## Outcome

The generated Kai patch survives traversal.

Across all 3 seeds for both H and G:
- both situations are visited
- threshold region is reached in all seeds
- no tight 2-node loop appears before cycle 8
- patch nodes are actually selected, not dead leaves

Patch visitation by seed:
- seed 3: `anchor -> bridge`
- seed 7: `intensifier -> bridge`
- seed 19: `contrast -> threshold -> bridge`

Threshold reach:
- seed 3: cycle 4
- seed 7: cycle 4
- seed 19: cycle 3

## Critical interpretation

H and G produced identical traversal traces.

That is expected for the current fixture design.
The Kai hand and generated patch graphs hold traversal-facing structure constant:
- deltas
- refs
- pressures
- option effects
- priorities
- edges

So the current `graffito_pilot.py` feature arm sees the same structural graph even when the patch prose differs.

## What this proves

This is a real bridge success, but a narrow one.

It proves:
- generated-curated Kai nodes can be slotted into a live micrograph
- the patch does not break traversal
- the scheduler will actually visit generated patch nodes
- threshold progression still works with the generated patch inserted

It does not prove:
- the scheduler prefers generated prose over hand-authored prose
- prose-level dramatic differences matter to L3 yet
- the generated structural graph is better than the hand-authored one

## Human H vs G read

Rendered playlists:
- `daydreaming/out/kai_patch_test_20260315/hand_seed3_playlist.txt`
- `daydreaming/out/kai_patch_test_20260315/generated_seed3_playlist.txt`
- `daydreaming/out/kai_patch_test_20260315/hand_seed7_playlist.txt`
- `daydreaming/out/kai_patch_test_20260315/generated_seed7_playlist.txt`
- `daydreaming/out/kai_patch_test_20260315/hand_seed19_playlist.txt`
- `daydreaming/out/kai_patch_test_20260315/generated_seed19_playlist.txt`

Judgment:
- G is better than H on prose, though by a smaller margin than in Tessa
- G keeps the same traversal behavior

Why G wins:
- more specific physical anchoring
- better embodiment of avoidance through ritual action
- stronger harbor carryover inside the kitchen scene

Representative gains:
- the generated anchor uses the water glass, sink window, and remembered harbor railing instead of summarizing avoidance as "facing everywhere except the table"
- the generated bridge makes the return address physically disappear under the mug and lets the silence start composing itself into a message
- the generated contrast turns tide knowledge and coffee hiss into scene pressure instead of naming the pattern abstractly

## Next implication

The Tessa result transfers to Kai in the same narrow sense:
- the generation lane can now supply patchable material across two operator families
- graph insertion and traversal survive that material in both fixtures

If a stronger H vs G comparison is needed next, it should come from:
- human judgment over rendered traces / playlists
- prose-sensitive traversal features
- or a downstream layer that actually consumes patch text
