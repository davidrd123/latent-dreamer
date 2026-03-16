# supply_v1 closeout

Date: 2026-03-15

## Outcome

`supply_v1` passes in the practical sense.

- `Tessa`: clean pass
- `Kai`: narrow pass

The gate for this sprint was not raw node count. It was:

- can we assemble `2` generated patch packs per fixture
- with acceptable curator effort
- and have those packs survive traversal

That gate is now met.

## What was proven

1. The frozen recipe can produce repeatable keeper supply.
   - `provider=anthropic`
   - `model=claude-sonnet-4-6`
   - `prompt_style=legacy_json`
   - `generation_temperature=0.7`
   - `system_prompt_style=consider_alternatives`
   - authored framing rotation where available

2. The keeper bank is usable as a production artifact.
   - [keeper_bank_supply_v1.jsonl](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/out/authoring_time_generation/keeper_bank_supply_v1.jsonl)

3. Pack assembly is no longer manual for the validated supply packs.
   - [pack_registry.py](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/tools/pack_registry.py)
   - [patch_pack_registry_supply_v1.yaml](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/fixtures/patch_pack_registry_supply_v1.yaml)

4. Generated patch packs exist for both fixtures.
   - `Tessa`
     - [tessa_patch_test_generated_pack_a_v1.yaml](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/fixtures/tessa_patch_test_generated_pack_a_v1.yaml)
     - [tessa_patch_test_generated_pack_b_v1.yaml](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/fixtures/tessa_patch_test_generated_pack_b_v1.yaml)
   - `Kai`
     - [kai_patch_test_generated_pack_a_v1.yaml](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/fixtures/kai_patch_test_generated_pack_a_v1.yaml)
     - [kai_patch_test_generated_pack_b_v1.yaml](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/fixtures/kai_patch_test_generated_pack_b_v1.yaml)

## Interpretation

`Tessa` is no longer the bottleneck.

`Kai` still has a narrower role distribution and a higher curation burden, but not enough to fail the pilot. Pack B required one moderate bridge promotion; it did not require a new mechanism round.

So the correct read is:

- supply is viable
- curation cost is real
- the lane is good enough to stop proving the same thing

## What this does not prove

- that traversal is sensitive to prose quality
- that the current scheduler values better writing
- that graph-function diversity is solved in the general case

Those are separate questions.

## Exit condition reached

The sprint question was: can the generation lane supply curator-usable patch material repeatedly enough to support graph assembly?

Answer: yes, narrowly but sufficiently.

## Next phase

Do not spend more time on sprint-document tuning.

The next useful work is upstream of supply:

1. architecture clarification from the `5 Pro` reconciliation work, especially the Director/world-update interface
2. the first serious `watch something` pass:
   - inner-life visualization contract
   - audio/rendering surface

The generation/supply lane has earned a pause.
