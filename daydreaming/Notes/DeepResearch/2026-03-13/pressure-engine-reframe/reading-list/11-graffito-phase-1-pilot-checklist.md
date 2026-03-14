# Graffito Phase 1 Pilot Checklist

Purpose: a shipping-lane pilot for the L3 scheduler shape on the
existing Graffito v0 slice.

This is **not** the full Experiment 1 substrate.
It is the smallest watched-run pilot that proves whether a
Façade-shaped traversal pipeline is worth integrating at all.

Inputs:
- `08-l3-experiment-1-synthesis.md`
- `../11-settled-architecture.md`
- `../../../experiential-design/18-graffito-vertical-slice.md`
- `../../../experiential-design/21-graffito-v0-playback-contract.md`
- `../../../experiential-design/22-traversal-sandbox-candidates.md`
- `../../../../fixtures/graffito_v0_scenes_3_4.yaml`
- `../../../../fixtures/graffito_v0_trace.yaml`

---

## Goal

Prove that a small stateful traversal scheduler over the existing
Graffito slice produces more legible movement than a naive walk or
replayed trace, without destabilizing the watched-run path.

If this pilot fails, do not add DODM features or Suspenser
refinements yet.

---

## Scope

### In scope

- shared traversal state
- explicit graph edges
- availability filtering
- priority tiers
- target trajectory over tension / energy
- simple soft weighting from recency, continuity, and conductor bias
- debug-facing `SelectionRecord`
- watched-run comparison against a simple baseline

### Out of scope

- DODM feature registry
- structural tension
- event-homing logic
- full three-arm comparison
- graph expansion beyond the current slice
- changing the renderer contract
- changing the narration contract
- importing any L2 semantics into L3

---

## Why Graffito is only a pilot

The current fixture is:

- `13` nodes
- `2` situations
- empty authored edges
- playback-oriented rather than event-oriented

That is enough to test traversal state, recency, tiering, and
trajectory bias.
It is not enough to test full scheduler leverage.

---

## Build checklist

### 1. Freeze the pilot substrate

- Keep the existing node set in `graffito_v0_scenes_3_4.yaml`
- Do not expand the slice to satisfy research curiosity
- Treat the current authored trace as a comparison artifact, not as the
  thing to preserve exactly

### 2. Add explicit edges

- Author a minimal edge set for the 13-node graph
- Make sure every node has at least one plausible continuation
- Preserve the obvious street -> apartment and apartment -> street
  crossings already implied by the trace
- Avoid overfitting the edge set to one hand-authored run

Deliverable:
- non-empty `edges:` block in `graffito_v0_scenes_3_4.yaml`

### 3. Add minimum pilot annotations

For each node, add:

- `delta_tension`
- `delta_energy`
- optional `priority_tier` or a simple `priority_test`
- optional lightweight continuity hints if needed

Do not add:

- `setup_refs`
- `event_id`
- `resolution_path_count`
- `option_effect`

Those belong to the full experiment substrate, not the pilot.

### 4. Implement shared traversal state

Minimum state:

- current node
- recent path
- visit count
- last seen cycle
- situation activation
- recent emotional trajectory
- conductor bias

This state should be available to both the simple baseline and the
Façade pilot so the comparison is fair.

### 5. Implement two pilot arms

#### A. Simple baseline

- available-node walk
- recency suppression
- local continuity bias
- optional conductor bias
- no target trajectory
- no priority tiers

#### B. Graffito pilot scheduler

- availability
- priority tiers
- target trajectory slope
- soft weighting from recency / continuity / conductor bias
- node selection emits `SelectionRecord`

### 6. Keep the stage seam unchanged

- do not alter the normalized playback packet contract
- keep traversal internals in debug / trace output
- if needed, add `traversal_intent` in debug first, not as a hard
  renderer dependency

### 7. Produce watched runs

- run the same Graffito slice through both arms
- generate comparable trace outputs
- if practical, render or at least play back both through the existing
  watched-run path

---

## Evaluation checklist

### Mechanical checks

- run completes without dead ends
- no node dominates excessively
- returns feel deliberate rather than accidental
- cross-situation moves happen without obvious thrashing

### Watched-run checks

- humans can describe why the run moved when it did
- returns to the street or apartment feel timed, not random
- the pilot feels more intentional than the simple baseline

### Failure signals

- pilot behavior is reproducible by trivial edge weighting
- priority tiers never matter
- target trajectory does not visibly affect movement
- the watched run does not look more intentional than baseline

If these hold, stop here and reconsider L3 scope before adding
feature layers.

---

## Exit criteria

This pilot is done when:

- the 13-node graph has explicit edges
- both arms run on shared traversal state
- the Façade-shaped arm produces legibly better watched runs
- the renderer and narration seams remain intact

At that point, move to the City Routes substrate for the real
Experiment 1 comparison.
