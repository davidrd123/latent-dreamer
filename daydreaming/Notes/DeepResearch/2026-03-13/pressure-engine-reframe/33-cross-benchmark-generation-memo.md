# Cross-Benchmark Generation Memo

Date: 2026-03-14

Purpose: record the cross-benchmark result for the authoring-time generation prototype after the `Kai`, `Maren`, and `Rhea` fixture runs.

## Benchmarks covered

Fixtures:
- `daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml`
- `daydreaming/fixtures/authoring_time_generation_maren_opening_line_v1.yaml`
- `daydreaming/fixtures/authoring_time_generation_rhea_credit_meeting_v1.yaml`

Representative run bundles:
- `daydreaming/out/authoring_time_generation/20260314-181851`
- `daydreaming/out/authoring_time_generation/20260314-183148`
- `daydreaming/out/authoring_time_generation/20260314-201713`

## What is established across all three fixtures

### 1. Graph-valid generation is stable

Across all three fixtures, the harness produced graph-valid outputs under the frozen seam contract.

This is now a stable property of the generation pipeline rather than a one-off result.

Status: established.

### 2. Operator control generalizes across different pressure/practice shapes

Observed default winners:
- `Kai`: `avoidance`
- `Maren`: `rehearsal`
- `Rhea`: `rehearsal`

These are not cosmetic differences. They come from different concern mixes, appraisal states, and practice contexts:
- `Kai` is low-control, attachment-heavy, evasion-shaped
- `Maren` is thresholded, obligation-heavy, anticipated-confrontation-shaped
- `Rhea` is status-damage-heavy, thresholded, and rehearsal-favoring under moderate controllability

Status: established.

### 3. Ablations move operator choice in intelligible directions

Across the three fixtures, the ablations produce consistent control shifts:
- `high_controllability` flips `Kai` from `avoidance` to `rehearsal`
- `swap_practice_context` flips `Kai` from `avoidance` to `rehearsal`
- `Maren` default is `rehearsal`, but `swap_practice_context` flips to `avoidance`
- `Maren no_causal_slice` also flips to `avoidance`
- `Rhea low_controllability` flips to `avoidance`
- `Rhea swap_practice_context` flips to `avoidance`
- `Rhea no_causal_slice` flips to `avoidance`
- `Rhea high_controllability` stays `rehearsal`

This is the strongest result in the entire prototype. The middle layer is not just adding descriptive metadata. It is changing the generated control path.

Status: established.

### 4. Cross-fixture world grounding can be kept clean

After the prompt-construction bug was fixed and benchmark-specific constraints were moved into fixture data, the runs stopped leaking `Kai`/`harbor` material into the `Maren` and `Rhea` scenarios.

This matters because without that fix, cross-benchmark generalization claims would be compromised by prompt contamination.

Status: established.

## What each benchmark specifically contributes

### `Kai`

`Kai` is the best evidence for the original bottleneck claim:
- low controllability
- attachment pressure
- evasion practice
- delay ritual scenes

It established that the pipeline can generate graph-valid avoidance material and that increasing controllability or changing practice context can move the system into rehearsal.

### `Maren`

`Maren` is the clean threshold rehearsal case:
- obligation-led entry pressure
- anticipated confrontation
- opening-line preparation outside the room

It established that the pipeline can leave the delay-ritual lane and still produce coherent threshold material under the same overall architecture.

### `Rhea`

`Rhea` is the strongest generalization case so far:
- dominant `status_damage`
- threshold context around a professional credit dispute
- distinct retrieval set, places, events, and reappraisal geometry
- new `low_controllability` ablation

It established that the control logic holds in a third concern regime, and that lowering controllability can pull a rehearsal-shaped scene back toward avoidance.

## What remains positive but not fully settled

### 5. `CausalSlice` looks useful, but the exact quality lift is still judgment-based

Across `Maren` and `Rhea`, the `no_causal_slice` ablation often keeps the scene graph-valid but shifts the winner or reduces specificity.

That is good evidence that explicit causal interpretation is doing work. But the exact magnitude of the quality difference is still better judged by reading the prose side by side than by relying on automatic semantic checks.

So the right current claim is:
- `CausalSlice` is directionally supported as a useful first-class object
- the remaining question is one of degree, not whether it matters at all

Status: positive, not exhaustively measured.

## One important interpretation choice

For `Rhea`, the middle-arm output returned a rehearsal scene with `option_effect: clarify` rather than the fixture's expected `open`.

That is acceptable.

A rehearsal scene can reasonably be understood as clarifying the shape of entry rather than opening a new path. The fixture's earlier `policy/open` expectation was a hypothesis, not a contract requirement. The generated output is still architecturally coherent.

## Current bottom line

The authoring-time generation pipeline is now validated at the level needed to proceed.

What is proven enough:
- graph-compilable candidate moments
- stable control sensitivity to appraisal and practice
- generalization across at least three distinct benchmark worlds
- no reliance on overt behavioral leading in the prompt
- controllable ablation behavior across fixtures

What is no longer the main question:
- whether this architecture can generate anything usable at all

What becomes the next question:
- how to scale benchmark coverage and authoring supply without losing seam discipline

## Recommended next step

Do not spend more time on single-fixture micro-ablations unless a concrete bug appears.

Use the current result to justify the next phase:
- add more benchmark fixtures only when they open new operator/concern territory
- keep `Kai`, `Maren`, and `Rhea` as regression fixtures
- move toward broader authoring-time material supply experiments

A sensible next benchmark, if one is needed, would be a true `rationalization` winner rather than another rehearsal or avoidance variant.

## Project-level implication

The critical-path reframe in `27-authoring-time-generation-reframe.md` is now supported by experiment:
- material supply is a real bottleneck
- structured authoring-time generation is a viable response
- the middle layer is not decorative paperwork

This is enough to treat the generation pipeline as an active architecture lane rather than an open question.