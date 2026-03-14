# Authoring-Time Generation Reframe

Purpose: freeze the new framing after the `L3` experiment passes and the
material-supply discussion on `2026-03-14`.

This is not a replacement for `11-settled-architecture.md`.
It is a convergence note about what the next bottleneck actually is and
what experiment should happen next.

Related:
- `11-settled-architecture.md`
- `13-execution-roadmap.md`
- `21-graph-interface-contract.md`
- `reading-list/13-l2-refactor-synthesis.md`
- `reading-list/14-l1-critic-test-synthesis.md`
- `19-city-routes-arms-a-b-pass.md`
- `20-city-routes-arm-c-pass.md`
- `22-city-routes-robustness-interpretation.md`
- `26-city-routes-contrasting-conductor-interpretation.md`

---

## Bottom line

The current bottleneck is no longer `L3` scheduler theory.

The current bottleneck is material supply:

- how to go from narrative primitives to enough usable graph material
- without hand-authoring every node
- while preserving the graph seam discipline already settled in
  `21-graph-interface-contract.md`

The next critical-path experiment is therefore not a richer scheduler
pass and not a pure `L1` critic test.
It is an authoring-time generation prototype.

---

## What remains settled

These claims still stand:

- `L3` is the shipping traversal layer, not cognition.
- The authored graph remains the membrane between lanes.
- The graph contract stays frozen and minimal.
- `L2` remains the engine core for motivated inner processing.
- `L1` is not autonomous worldbuilding in the shipping architecture.

The City Routes passes changed the practical sequencing, not the
architecture:

- the narrow `L3` thesis is now provisionally supported
- conductor influence is real but limited
- the next open problem is upstream of traversal: graph material supply

---

## The reframe

The earlier `L1` question was:

- can a narrow deficiency-driven critic improve an authored world

The more urgent question is:

- how do we generate enough good world material at all

That shifts the next experiment from evaluation-first to generation-first.

The right framing is:

- `L1` does not collapse into `L2`
- but `L1` becomes an authoring-time orchestration layer over
  `L2`-style machinery

In other words:

- one engine family
- two modes

`L2` concerns:

- character motivation
- appraisal
- episodic reminding
- operator-driven inner processing

`L1` concerns:

- graph coverage
- situation balance
- tonal variety
- event/set-up/payoff sufficiency
- usable material yield

So the boundary softens, but it does not disappear.

---

## What Mueller buys at authoring time

Mueller is not just historical justification here.
He suggests the actual mechanism that might make authoring-time
generation better than flat scene prompting.

What matters:

- active concerns
- typed cognitive operators
- episodic memory
- recursive reminding
- serendipity / overdetermination

This is the candidate engine for producing moments from narrative
primitives.

Without that machinery, generation risks becoming:

- plausible
- fluent
- generic

With that machinery, the claim is that generation becomes:

- motivated
- stateful
- accumulative
- more naturally pre-annotated for traversal

That claim needs an experiment, not another architecture note.

---

## Narrative primitives, not seed nodes

The material-supply problem should not start from hand-authored nodes.
It should start from narrative primitives such as:

- characters
- contradictions
- relationships
- backstory events
- places with emotional charge
- active situations
- world rules / emotional physics
- resonance notes

Those are the authored inputs.

The engine then produces candidate moments from collisions among:

- active concerns
- retrieved episodes
- operator choice
- place / situation context

That is the missing `0 -> usable graph material` stage.

---

## Two-stage `L1`

`L1` now has two stages.

### Stage 1: generation

Goal:

- produce candidate moment material from narrative primitives

Mechanism:

- authoring-time `L2`-style generation
- human curation
- graph compilation

### Stage 2: critic / repair

Goal:

- find structural gaps once material exists

Mechanism:

- deficiency detection
- targeted repair proposals
- constrained completion

This keeps `reading-list/14-l1-critic-test-synthesis.md` alive, but
repositions it.
It is not wrong.
It is just not first.

---

## Hard constraints on the prototype

The prototype must stay narrow.

### Required

- one character
- three authored backstory episodes
- one active situation
- three operators
- simple tag-based retrieval first
- human-readable provenance
- output that compiles to the frozen graph seam
- flat-prompt baseline for comparison

### Not required in phase 1

- embeddings
- aesthetic filtering
- full kernel refactor
- assumption management
- multi-character social practice machinery
- conductor integration

Those are accelerants or later refinements, not prerequisites.

---

## Compile-to-graph discipline

The prototype is only useful if the generated candidates can land in the
existing seam.

Each accepted candidate moment should be expressible in graph-native
terms such as:

- `situation_id`
- `delta_tension`
- `delta_energy`
- `pressure_tags[]`
- `origin_pressure_refs[]`
- `setup_refs[]`
- `payoff_refs[]`

The key claim to test is:

- operator provenance should make much of this annotation nearly free

Examples:

- a rehearsal moment should tend to increase tension by construction
- an avoidance moment should often defer closure while maintaining
  pressure
- a recall / reminding moment should carry direct `origin_pressure_refs`
  and plausible setup/payoff implications

If the candidates cannot be compiled cleanly to the graph contract, the
prototype has failed even if the writing is interesting.

---

## What success would mean

Success for the prototype would mean:

- operator-driven generation produces materially better candidate moments
  than flat prompting
- episodic retrieval changes the generated material in an observable way
- the generated candidates compile cleanly to the graph seam
- human curation yield is high enough to make graph growth practical

The output does not need to be perfect.
It only needs to prove that Mueller-shaped generation is earning its
keep.

---

## What failure would mean

Failure would mean one of these:

- operator labels do not improve specificity or psychological depth
- retrieval does not materially alter the moments
- output is fluent but graph-useless
- the curation burden remains as high as hand-authoring

If that happens, the right move is not to enrich the architecture.
The right move is to simplify:

- fall back toward structured prompt pipelines
- keep the graph contract
- retain `L3`
- demote Mueller-at-authoring-time to a weak influence rather than a
  core mechanism

---

## Revised build-order consequence

The old order was:

1. `L3`
2. conductor spike
3. `L1` critic test
4. `L2` refactor

The new order is:

1. authoring-time generation prototype
2. conductor mapping spike
3. watched-run / performance-lane validation on existing `L3`
4. `L1` critic / repair pass on generated material
5. deeper `L2` refactor if the prototype proves out

This is the order that matches the actual bottleneck.

---

## Immediate next move

Write a narrow prototype spec and implement only this:

1. one character with contradictions
2. three backstory episodes
3. one active unresolved situation
4. three operators
5. tag-based retrieval over authored and generated episodes
6. candidate moments with provenance and graph-compilable fields
7. side-by-side comparison against flat prompting

That is the new critical path.
