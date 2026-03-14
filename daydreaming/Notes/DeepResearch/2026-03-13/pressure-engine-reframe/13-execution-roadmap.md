# Execution Roadmap

Purpose: capture the current execution order after the architecture,
prior-work, and experiment-shaping passes.

This note is the working roadmap from here, not a theory note.

Related:
- `11-settled-architecture.md`
- `reading-list/08-l3-experiment-1-synthesis.md`
- `reading-list/11-graffito-phase-1-pilot-checklist.md`
- `reading-list/12-city-routes-experiment-1-checklist.md`

---

## Top-line sequence

1. **Graffito L3 pilot** (shipping lane, first)
2. **Conductor mapping spike** (small, before full L3 claim)
3. **City Routes full L3 experiment** (research lane, third)
4. **L1 authoring critic test** (research lane, fourth)
5. **L2 internal refactor** (research lane, fifth)

This order is deliberate:

- Graffito already exists and touches the watched run.
- The conductor mapping needs one small playability spike before the
  full expressivity claim is taken seriously.
- City Routes is the first graph large enough to test the narrow L3
  thesis properly.
- L1 and L2 remain important, but they are not the next falsifier.

---

## 1. Graffito L3 Pilot

Goal: prove that a small Façade-shaped traversal scheduler over the
existing Graffito slice produces a more legible watched run than a
naive traversal.

### Why first

- It uses the existing authored Graffito slice.
- It stays on the shipping lane.
- It tests the scheduler shape without dragging in DODM features,
  structural tension, or L2 refactors.

### Concrete work

- Keep the playback/render seam unchanged.
- Upgrade `daydreaming/fixtures/graffito_v0_scenes_3_4.yaml` from a
  playback fixture to a traversal-capable fixture:
  - add explicit `edges`
  - add `delta_tension`
  - add `delta_energy`
  - optional simple `priority_tier`
- Add a small Python pilot harness beside
  `daydreaming/render_trace.py`.
- Implement shared traversal state:
  - current node
  - recent path
  - visit count
  - last seen cycle
  - situation activation
  - recent tension / energy
  - optional conductor bias
- Implement two arms:
  - simple baseline walk
  - Façade-shaped pilot arm
- Emit:
  - trace YAML compatible with the current render path
  - debug JSONL with candidate sets, scores, tier promotions, and
    reasons

### Success condition

- The pilot arm feels more intentional than the simple walk.
- Returns feel timed rather than accidental.
- The result is not explainable by a trivial two-rule heuristic.
- The existing renderer and narration paths remain intact.

### Failure condition

- The extra machinery does not visibly improve the watched run.

### Checklist

See:
- `reading-list/11-graffito-phase-1-pilot-checklist.md`

---

## 2. City Routes Full L3 Experiment

Goal: validate the full narrow L3 claim with explicit ablations rather
than one bundled rich arm.

### Why second

- It comes after a small conductor-mapping spike, so `conductor
  expressivity` is not purely theoretical.
- Graffito is only a pilot substrate.
- City Routes is explicitly designed to stress traversal:
  `28-32` nodes, `6` situations, `4` events, `30-40` edges.

### Concrete work

- Author the City Routes graph from
  `daydreaming/Notes/experiential-design/23-brief-city-night-pursuit.md`.
- Freeze graph-native scheduler annotations via
  `21-graph-interface-contract.md`.
- Run the ablated comparison:
  - weighted-random baseline
  - Façade baseline
  - C1 = Façade + DODM-style feature registry
  - C2 = C1 + Suspenser structural tension
  - C3 = C2 + shallow lookahead only if immediate-node scoring proves
    too weak
- Evaluate:
  - run quality
  - controlled variety
  - graph-change resilience
  - conductor expressivity
  - equivalent-policy complexity

### Success condition

- The Façade baseline beats the naive baseline.
- C1 beats the Façade baseline clearly enough to justify the feature
  registry.
- C2 adds value beyond C1 rather than merely increasing complexity.
- C3 is only kept if it adds clear marginal value.

### Failure condition

- The richer arms do not beat the simpler ones clearly.
- Or the wins are not attributable because the additions remain bundled.

### Checklist

See:
- `reading-list/12-city-routes-experiment-1-checklist.md`

---

## 3. City Routes Full L3 Experiment
## 4. L1 Authoring Critic Test

Goal: test whether a narrow deficiency-driven authoring critic beats
one-shot structured prompting.

### Concrete work

- Seed a typed world fixture with intentional defects.
- Run a small pressure/operator set.
- Compare against one-shot prompting with the same model budget.
- Measure:
  - defect reduction
  - accepted proposals
  - edit burden
  - usable graph material yield

### Note

This is a separate research experiment.
It should not be confused with the City Routes L3 traversal test.

---

## 5. L2 Internal Refactor

Goal: improve concern/appraisal/provenance clarity without changing the
stage-facing output.

### Concrete work

- cleaner appraisal pass
- clearer `CharacterConcern` lifecycle
- stronger trace provenance / `CommitRecord`
- later: social practices and assumption management

### Note

This remains later because it is not the next falsifier.

---

## 2. Conductor Mapping Spike

Goal: validate that the APC Mini can produce a playable, legible bias
surface before the full `conductor expressivity` claim is used in the
City Routes evaluation.

### Concrete work

- map a minimal subset of conductor controls:
  - situation boosts
  - hold / release bias
  - escalation bias
  - recall bias
  - intensity envelope
- drive those against the pilot scheduler or a trace-replay harness
- note which controls collapse together or feel physically unusable

### Success condition

- the performer can reliably produce intended scheduler biases
- the mapping feels playable rather than theoretically elegant

### Failure condition

- multiple dimensions collapse together
- intended bias changes are hard to produce on the instrument
- scheduler expressivity claims outrun instrument reality

---

## Cross-cutting questions

These matter, but they are not the current bottleneck:

- cross-level pressure propagation
- multimodal world-building expansion
- deeper L2 theory pass
- richer visual-anchor workflows

They should not displace the Graffito pilot.

---

## Immediate next move

Start the Graffito pilot in this exact order:

1. edit `daydreaming/fixtures/graffito_v0_scenes_3_4.yaml`
   - add edges
   - add `delta_tension`
   - add `delta_energy`
2. add a Python pilot harness beside `daydreaming/render_trace.py`
3. implement shared traversal state and the two pilot arms
4. emit trace YAMLs plus debug JSONL
5. render both runs and compare the watched output

That is the current execution path from here.
