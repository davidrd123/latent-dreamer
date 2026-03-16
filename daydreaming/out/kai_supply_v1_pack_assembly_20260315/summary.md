# Kai supply_v1 pack assembly summary

Date: 2026-03-15

Assembled generated packs:
- `daydreaming/fixtures/kai_patch_test_generated_pack_a_v1.yaml`
- `daydreaming/fixtures/kai_patch_test_generated_pack_b_v1.yaml`

Bank basis:
- pack A uses the clean keeper set from `supply-v1-kai-batch1-20260315`
- pack B uses weaker second-line material from `supply-v1-kai-batch2-20260315` plus one moderate bridge promotion

Traversal settings:
- arm: `feature`
- cycles: `12`
- start node: `k_start_table_letter`
- seeds: `3`, `7`, `19`

## Outcome

Both generated packs survive traversal.

Across all 3 seeds for both pack A and pack B:
- both situations are visited
- threshold region is reached in all seeds
- no early tight loop appears before cycle 8
- patch nodes are actually selected

Patch visitation pattern:
- seed 3: `anchor -> bridge`
- seed 7: `intensifier -> bridge`
- seed 19: `contrast -> threshold -> bridge`

## Interpretation

This is enough to treat `Kai` as a narrow supply pass for `supply_v1`.

The bank was structurally narrow at admission time, but actual pack assembly shows:
- pack A can be built cleanly from keeper-grade material
- pack B can be built from second-line material with one moderate bridge edit

So the real gate is met:
- two distinct generated patch packs can be assembled for `Kai`
- traversal survives both
- the second pack costs more curator effort, but it is not blocked

## Implication

`supply_v1` now passes in the practical sense:
- `Tessa` passes cleanly
- `Kai` passes narrowly, with higher curation burden on the second pack
