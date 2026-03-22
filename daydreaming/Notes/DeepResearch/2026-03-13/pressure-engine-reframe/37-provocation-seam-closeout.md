# Provocation Seam Experiment Closeout

Date: 2026-03-16

## Purpose

Test whether a typed provocation sidecar can change authoring-time generation in a traceable way without mutating the base fixture.

Fixture used:

- `daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml`

Provocation sidecar:

- `daydreaming/fixtures/provocations/kai_letter_provocations_v1.yaml`

## Matrix 1: plumbing pass

Artifacts:

- `daydreaming/out/authoring_time_generation/kai-prov-matrix-baseline-20260315/`
- `daydreaming/out/authoring_time_generation/kai-prov-matrix-harbor-memory-20260315/`
- `daydreaming/out/authoring_time_generation/kai-prov-matrix-ferry-schedule-20260315/`
- `daydreaming/out/authoring_time_generation/kai-prov-matrix-dara-texts-20260315/`

What Matrix 1 proved:

- provocation sidecars load correctly
- overlays apply to an in-memory fixture view only
- `provocation.trace.json` is written
- the base fixture on disk is not mutated

What Matrix 1 did **not** prove:

- early decision surfaces did not move reliably
- retrieval, practice, and operator choice were still mostly baseline-like

Interpretation:

- the seam existed mechanically
- it was not load-bearing yet because the patched fields were not reaching concern inference, practice bias, or appraisal strongly enough

## Matrix 2: propagation pass

Artifacts:

- `daydreaming/out/authoring_time_generation/kai-prov-matrix2-baseline-20260315/`
- `daydreaming/out/authoring_time_generation/kai-prov-matrix2-harbor-memory-20260315/`
- `daydreaming/out/authoring_time_generation/kai-prov-matrix2-ferry-schedule-20260315/`
- `daydreaming/out/authoring_time_generation/kai-prov-matrix2-dara-texts-20260315/`

Propagation changes made before Matrix 2:

- synthesize runtime primitive facts from provoked fixture state
- feed those facts into concern inference and practice-bias votes
- let causal-slice and appraisal consume the resulting state

What Matrix 2 proved:

### `patch_situation_state` is load-bearing

The ferry provocation now moves the early control surface:

- step-1 retrieval reorders to `ep_last_rupture`, then `ep_avoidance_consequence`
- practice shifts from `evasion` to `anticipated-confrontation`
- operator flips from `avoidance` to `rehearsal`
- admitted count widens to `4`

### `add_event` is load-bearing

The Dara provocation also moves the early control surface:

- step-1 retrieval reorders to `ep_last_rupture`, then `ep_avoidance_consequence`
- practice shifts from `evasion` to `anticipated-confrontation`
- operator flips from `avoidance` to `rehearsal`

### current salience-only memory overlays are **not** load-bearing for early decision surfaces

The harbor-memory provocation remains baseline-like:

- retrieval order does not change
- practice does not shift
- operator does not flip

## Matrix 3: harbor salvage attempt

Artifacts:

- `daydreaming/out/authoring_time_generation/kai-prov-matrix3-baseline-20260315/`
- `daydreaming/out/authoring_time_generation/kai-prov-matrix3-harbor-memory-20260315/`

This was a narrow follow-up attempt to make the harbor-memory provocation more retrieval-relevant.

Result:

- it did not change the outcome
- harbor remained baseline-like

This should **not** be treated as part of the core seam win. It only confirms that the current salience-memory path is weak.

## Final result

The provocation seam experiment passes.

What is proven:

- a typed provocation sidecar can be loaded, traced, and applied without mutating canon
- world/state provocations can change authoring-time cognition in a traceable way
- event provocations can change authoring-time cognition in a traceable way

What is **not** proven:

- that pure memory-salience overlays can move early decision surfaces in the current pipeline
- that runtime Director is already this provocation mechanism
- that every provocation type needs to be equally strong to justify the seam

## Interpretation

The current authoring pipeline is situation-first.

Control flow is effectively:

1. situation / event state
2. concern inference
3. practice bias
4. operator scoring
5. retrieval query
6. candidate generation

Because of that shape:

- provocations that change what is true about the situation work
- provocations that only change memory salience do not currently propagate far enough

This is a useful architectural finding, not a failure.

## Claim boundary to carry forward

Use this result narrowly:

- `patch_situation_state`: proven
- `add_event`: proven
- current salience-only overlay ops: not proven for early cognition

Do **not** add an opaque retrieval bonus just to rescue the harbor case inside this experiment. If memory-led provocation is revisited later, it should be a new experiment with an explicit contract.

## Handoff

The provocation seam experiment is complete enough to stop iterating on it.

Return to the architecture lane:

- `36-current-synthesis.md`
- `02-5pro-architecture-reconciliation.md`
- `Ask B`
- watchable-runtime / Director / Provocation Generator integration
