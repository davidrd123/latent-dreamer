# Execution Roadmap

Purpose: capture the current execution order after the architecture,
prior-work, and experiment-shaping passes.

This note is the working roadmap from here, not a theory note.

Related:
- `11-settled-architecture.md`
- `21-graph-interface-contract.md`
- `27-authoring-time-generation-reframe.md`
- `reading-list/08-l3-experiment-1-synthesis.md`
- `reading-list/13-l2-refactor-synthesis.md`
- `reading-list/14-l1-critic-test-synthesis.md`
- `reading-list/11-graffito-phase-1-pilot-checklist.md`
- `reading-list/12-city-routes-experiment-1-checklist.md`

---

## Top-line sequence

1. **Authoring-time generation prototype** (new critical path)
2. **Conductor mapping spike** (small, still needed)
3. **Watched-run / performance-lane validation** (use existing `L3`)
4. **L1 critic / repair pass** (after material exists)
5. **L2 internal refactor** (only if the prototype proves out)

This order is deliberate:

- the `L3` scheduler question is no longer the main bottleneck
- the current bottleneck is graph material supply
- the conductor mapping still needs a physical playability check
- watched output still matters, but it no longer blocks the upstream
  authoring experiment
- `L1` critique only becomes useful once enough material exists to
  critique
- deeper `L2` cleanup should follow proof, not lead it

---

## 1. Authoring-Time Generation Prototype

Goal: test whether Mueller-shaped authoring-time generation can produce
better graph-ready candidate moments than flat prompting.

### Why first

- the biggest constraint is material supply, not traversal theory
- the next high-value falsifier is upstream of `L3`
- this is the shortest path to learning whether the Mueller machinery
  helps generate usable world material

### Concrete work

- define one narrow narrative seed:
  - one character
  - three authored backstory episodes
  - one active unresolved situation
- choose three operators only
- implement simple tag-based retrieval over:
  - authored backstory episodes
  - generated episodes accepted into the working memory set
- run two arms:
  - flat prompting baseline
  - operator-driven generation with retrieval and provenance
- require candidate outputs to compile to graph-native fields:
  - `situation_id`
  - `delta_tension`
  - `delta_energy`
  - `pressure_tags[]`
  - `origin_pressure_refs[]`
  - `setup_refs[]`
  - `payoff_refs[]`
- record:
  - generation prompt inputs
  - retrieved episode set
  - operator provenance
  - accepted / rejected candidate moments
  - curation yield

### Success condition

- the operator-driven arm produces materially better candidates than the
  flat baseline
- retrieval changes the generated material in an observable way
- accepted candidates compile cleanly to the frozen graph contract
- human curation yield is high enough to make graph growth plausible

### Failure condition

- the operator arm does not beat the flat baseline
- retrieval adds ceremony but not value
- generated material remains graph-useless
- curation burden stays close to hand-authoring

---

## 2. Conductor Mapping Spike

Goal: validate that the APC Mini can produce a playable, legible bias
surface before stronger `conducting` claims harden.

### Why still second

- this is a physical-instrument question, not a theory note
- it can run beside or just after the authoring prototype
- it determines whether the system is actually playable as an
  instrument rather than just tunable

### Concrete work

- map a minimal subset of conductor controls:
  - situation boosts
  - hold / release bias
  - escalation bias
  - recall bias
  - intensity envelope
- drive those against the current scheduler or a trace-replay harness
- note which controls collapse together or feel physically unusable

### Success condition

- the performer can reliably produce intended scheduler biases
- the mapping feels playable rather than theoretically elegant

### Failure condition

- multiple dimensions collapse together
- intended bias changes are hard to produce on the instrument
- instrument reality remains weaker than the conducting claim

---

## 3. Watched-Run / Performance-Lane Validation

Goal: move from scheduler traces to watched runs and determine whether
the existing `L3` work reads as performance.

### Why now

- the scheduler question is far enough along for current purposes
- the next `L3` uncertainty is experiential, not structural
- current City Routes results should be evaluated in playback, not only
  in trace analysis

### Concrete work

- render representative City Routes runs:
  - baseline / simpler arm
  - richer arm
  - at least one contrasting conductor pair
- inspect:
  - legibility of setup / payoff
  - readability of threshold recovery
  - whether conductor influence is visible in playback
  - packet ownership / stage-packet vocabulary clarity

### Success condition

- watched output confirms the trace-level wins are visible
- conductor differences are at least sometimes legible in playback
- runtime packet boundaries stay clean

### Failure condition

- watched runs flatten the distinctions seen in traces
- conductor effects vanish in actual playback
- runtime packet vocabulary leaks across seams

---

## 4. L1 Critic / Repair Pass

Goal: test whether a narrow deficiency-driven authoring critic improves
material once enough candidate graph content exists.

### Why fourth

- critique is useful only after generation supplies material
- this is a stage-two tool, not the way out of the `0 -> usable graph`
  problem

### Concrete work

- seed a typed graph slice with intentional structural defects
- run the critic over material that already exists
- compare against one-shot structured prompting with the same model
  budget
- measure:
  - defect reduction
  - accepted proposals
  - edit burden
  - usable graph material yield

### Success condition

- critic proposals reduce structural defects with lower edit burden than
  baseline prompting
- the critic improves graph quality without trying to become a full
  generator

### Failure condition

- one-shot prompting or ad hoc human patching remains just as effective
- the critic drifts into vague worldbuilding rather than targeted repair

---

## 5. L2 Internal Refactor

Goal: improve concern/appraisal/provenance clarity without changing the
stage-facing output.

### Why fifth

- the prototype should prove the authoring-time mechanism first
- deeper cleanup only pays off if the mechanism earns its keep

### Concrete work

- cleaner appraisal pass
- clearer `CharacterConcern` lifecycle
- stronger trace provenance / `CommitRecord`
- clearer theme-rule concern initiation
- stronger recursive reminding / serendipity handling
- later: social practices and assumption management

### Note

This remains later because it should follow proof, not lead it.

---

## Cross-cutting questions

These matter, but they are not the current bottleneck:

- cross-level pressure propagation
- embeddings / richer retrieval
- aesthetic scoring / LLM filtering
- multimodal world-building expansion
- deeper L2 theory pass
- richer visual-anchor workflows

They should not displace the authoring-time generation prototype.

---

## Immediate next move

Start the authoring-time generation prototype in this exact order:

1. define one character with contradictions
2. author three backstory episodes plus one active unresolved situation
3. choose three operators
4. implement simple tag-based retrieval
5. run flat baseline vs operator-driven generation
6. require graph-compilable output fields on every accepted candidate
7. compare curation yield and usable graph material

That is the current execution path from here.
