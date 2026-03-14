# Kernel Gap Analysis: Current State vs. Pressure Engine

## What the kernel currently has and what it maps to

This document maps every significant piece of the existing Clojure
kernel to its role in the pressure engine reframe — what survives,
what changes shape, and what's genuinely missing.

---

## Modules That Survive Largely Intact

### context.clj (194 lines) → Context mechanism

**Current:** Sprouting, pseudo-sprouting, fact assertion/retraction,
visibility (parent + add-obs - remove-obs), tree navigation, copying.

**In pressure engine:** This IS the fork mechanism. Sprouting a
context = forking a hypothetical. Pseudo-sprouting = copying a past
state as a new exploration branch.

**What changes:**
- Add mandatory `assumption_patch` field to every sprouted context
- Add `operator` field (which operator caused this fork)
- Add `dominant_pressure_id` and `supporting_pressure_ids`
- Add `modality` field (canonical | hypothetical | memory | forecast |
  rehearsal | fantasy | counterfactual)
- Add `level` field (:world-building | :character)

**Boundary correction from 03 review:** L3 does not use kernel Contexts.
Traversal control keeps its own history/bias state and should not be
forced into `assumption_patch` just for symmetry.

**Effort:** Moderate. Fields added, no logic rewritten.

### episodic_memory.clj (189 lines) → Episode storage and retrieval

**Current:** Mark/threshold coincidence retrieval, reminding cascade,
serendipity threshold lowering, recent-index FIFO, recent-episode
FIFO.

**In pressure engine:** Largely unchanged. Coincidence-mark retrieval
is a good mechanism. Reminding cascade is valuable.

**What changes:**
- Add post-hoc serendipity scan (check if retrieved episode
  accidentally serves a non-dominant active Concern)
- Add richer retrieval filtering: role similarity, valence match,
  operator compatibility (not just index intersection)
- Add `level` to episodes
- Episodes should reference Pressures, not just emotion IDs

**Effort:** Moderate. New retrieval filters, serendipity scan added.
Core mark/threshold logic unchanged.

### trace.clj (369 lines) → Trace and export

**Current:** Cycle snapshots with goal selection, context state,
retrievals, backtrack events, activation events, mutation events,
termination events, emotional shifts.

**In pressure engine:** Expands to include Pressure deltas, operator
invocations, CommitRecords, assumption_patches.

**Effort:** Moderate. More fields to snapshot, same structure.

---

## Modules That Change Shape

### goals.clj (133 lines) → CharacterConcern + Operator activation

**Current:** Goal lifecycle (activate, compete, halt, fail, succeed),
strength competition, planning-type (real/imaginary), context
pointers.

**In pressure engine:**
- Goals split into two concepts:
  1. **Concern** (the pressure: intensity, urgency, valence, type)
  2. **Operator activation** (the response: which operator, which
     context, what assumption_patch)
- Goal competition becomes Pressure priority scheduling
- Planning-type (real/imaginary) maps to Context.modality
- Strength becomes Concern.intensity

**What's preserved:**
- Competition logic (select highest priority)
- Status lifecycle (active, halted, waiting, failed, resolved)
- Context pointers (where born, where planning, backtrack wall)

**What's new:**
- Concern as a standalone entity (not tied 1:1 to a goal)
- Multiple operators can respond to the same Concern
- Concerns can be served by proposals from different operators
  (overdetermination)

**Effort:** Significant. This is the main refactoring surface.

### control.clj (297 lines) → Pressure scheduling + mode oscillation

**Current:** Per-cycle decay, family activation, goal competition,
mode oscillation (performance/daydreaming), per-goal step execution,
backtracking.

**In pressure engine:**
- Decay applies to Concerns, not just emotions/needs
- "Family activation" becomes "operator activation based on Concern
  profile matching"
- Goal competition becomes Pressure priority scheduling
- Mode oscillation generalizes: not just performance/daydreaming,
  but potentially multi-level (which level is currently active?)
- Per-goal step execution becomes per-operator-invocation step

**What's preserved:**
- The cycle structure (decay → activate → select → step)
- Mode oscillation logic (switch when current mode exhausted)
- Backtracking logic (walk up context tree seeking unvisited siblings)

**Effort:** Significant. Same loop shape, different entities flowing
through it.

### goal_families.clj (1,067 lines) → Operator implementations

**Current:** Three family implementations (REVERSAL, ROVING,
RATIONALIZATION) with activation predicates, candidate selection,
and plan bodies.

**In pressure engine:**
- These become three Level 2 (character) operators
- The activation predicates become `operator.activates?(concern,
  world_state)`
- The plan bodies become `operator.execute(concern, context,
  world_state, episodes)`
- New exploratory operators added at Level 1; L3 gets separate
  traversal controller verbs

**What's preserved:**
- REVERSAL's counterfactual branching logic
- ROVING's episode seed + reminding cascade
- RATIONALIZATION's emotion diversion mechanics

**What changes:**
- Concerns replace emotion+failed-goal as activation trigger
- Proposals replace raw context facts as output
- Operators get a common interface (activates?, priority, execute,
  scan_serendipity)

**Effort:** Significant. Refactor to common interface, but plan body
logic is preserved.

---

## Modules That Expand in Role

### director.clj (541 lines) → Feedback source (NOT an L3 operator)

**Current:** HTTP call to Gemini, message building, feedback
normalization, feedback application (situation boosts, emotional
episodes, serendipity bias, valence delta, director concepts).

**In pressure engine:**
- Director remains an external feedback source, not an operator.
  (The 5 Pro review confirmed: do not decompose the Director into
  typed L3 operators. The Director is a perturbation loop, not a
  traversal controller.)
- Director feedback becomes input to TraversalPressures (e.g.,
  director_concepts create resonance_opportunity pressures)
- Director concepts continue to feed into episodic memory as indices

**What's preserved:**
- The feedback schema (concepts, boosts, valence, episodes)
- Integration with episodic memory
- Its role as a mechanism against closure over the authored graph

**Effort:** Low. Keep as-is, add pressure translation layer.

### runner.clj (425 lines) → Research-lane orchestration (not shipping critical path)

**Current:** Orchestrates benchmark runs: load fixtures, activate
goals, run scripted cycles, apply family plans, export traces.

**In pressure engine:**
- May later support authoring-mode execution (levels 1+2, interactive)
- Needs human curation gates between levels
- Should not become a prerequisite for the first watched run
- L3 batch experiments should sit beside current playback tooling first,
  not inside a grand multi-level runner

**Effort:** Significant expansion.

---

## Modules That Are New (Don't Exist Yet)

### pressure.clj — Pressure entity and lifecycle

- Pressure base type with shared fields (intensity, urgency,
  valence, unresolved)
- CharacterConcern subtype for L2 (the kernel's native domain)
- CreativePressure subtype for L1 (research track only)
- Pressure creation from event appraisal
- Pressure decay and resolution tracking
- Pressure priority scheduling

### operators/ directory — L1/L2 operator implementations

- Level 1 (research): complicate, ground, connect, contrast,
  historicize
- Level 2 (proven): revenge/reversal, rehearse, rationalize,
  recover, avoid, rove, confront
- Common operator interface for exploratory/branch-generating
  operators

**Note:** L3 does NOT use exploratory operators. L3 uses traversal
controller verbs (select, recall, shift, dwell, release) which are
a different category — see traversal_controller.clj below.

### traversal_controller.clj — L3 traversal control (new)

- TraversalPressure detection from traversal state (tension
  trajectory, visit history, situation activation)
- Controller verbs: select, recall, shift, dwell, release
- Optional `traversal_intent` field for trace/debug
- Level-specific commit enums: `canon_event | traversal_bias |
  activation_shift | none`

**Note:** This is a controller/scheduler, not an exploration
engine. It does not fork contexts or use assumption_patches.

### commit.clj — CommitRecord and commit logic

- Level-specific commit enums:
  - L1/L2: `ontic | policy | salience | none`
  - L3: `canon_event | traversal_bias | activation_shift | none`
- Commit application to level-specific state
- Commit provenance tracking
- Human curation gate for ontic commits at authoring time

### proposal.clj — Proposal entity (L1/L2 only)

- Structured output of L1/L2 operator execution
- event_semantics / render_surface split
- Scoring components
- Serendipity scan integration

**Note:** L3 does not produce Proposals. It produces traversal
decisions (node selection + intent + transition).

### appraisal.clj — Event-to-Pressure translation

- Event arrives → which Pressures does it create/modify?
- Level-specific appraisal:
  - L2: event → CharacterConcern (the kernel's proven domain)
  - L1: world state scan → CreativePressure (research track)
  - L3: traversal history → TraversalPressure

---

## Benchmark Impact

### Existing benchmarks (Puppet Knows, Arctic, Stalker)

These should continue to pass. Refactoring path:

1. Introduce CharacterConcern as a wrapper around existing
   emotion+goal (L2 only)
2. Introduce L2 Operator interface that existing family plan bodies
   implement
3. Introduce CommitRecord as a formalization of what adapters
   already do (adapters stay as adapters — they are translators,
   not operators)
4. Existing benchmarks remain Level 2 (character) exercises
5. Add L3 experiment (see below) — not new benchmarks yet

### Next experiment (from 5 Pro review)

**L3 falsification test:** Use the existing Graffito v0 graph.
Build a tiny TraversalPressure controller with four pressures
(`tension_deficit`, `exhausted_situation`, `avoidance`,
`recall_opportunity`). Map to an optional debug-only
`traversal_intent` field. Generate three 18-cycle sequences:

1. Current authored trace baseline
2. Weighted-random graph walk baseline
3. TraversalPressure controller

Blind-rate on: "does this feel like a mind?" and "is the arc
legible?" If the pressure controller doesn't beat weighted random,
do not import L3 into the mainline.

### Future benchmarks (deferred)

- **Level 1 benchmark (research track):** Given compressed initial
  state, does the engine produce interesting world-building
  proposals? Requires computable pressures first.

- **Cross-level benchmark (research track):** Do levels 1→2
  produce material that L3 can traverse compellingly?

---

## Estimated Refactoring Scope

The 03 architectural review changes the schedule more than the schema:
most kernel refactors belong to the research track unless they directly
improve trace clarity or enable a small falsification test.

### Shipping/performance track (keep narrow)

| Component | Effort | Risk | Dependency |
|-----------|--------|------|------------|
| L3 TraversalPressure experiment against authored graph | Medium | Low | Existing graph + trace tooling |
| Add traversal_intent to trace/debug only | Low | Low | None |
| Trace/export additions for comparing traversers | Low | Low | None |
| No mandatory kernel refactor before first watched run | — | — | — |

### Research/kernel track (defer unless it directly changes comparison results)

| Component | Effort | Risk | Dependency |
|-----------|--------|------|------------|
| Add CharacterConcern entity (L2) | Medium | Low | None |
| Refactor goals→CharacterConcerns | High | Medium | CharacterConcern |
| Add L2 Operator interface | Medium | Low | None |
| Refactor families→L2 Operators | High | Low | Operator interface |
| Add CommitRecord (L1/L2 enums) | Low | Low | None |
| Add assumption_patch to Context | Low | Low | None |
| CreativePressure entity (L1) | Medium | High | Computable pressures |
| L1 operators | High | High | CreativePressure |
| Serendipity scan | Medium | Medium | Pressure entity |
| Proposal entity | Medium | Low | Operator interface |
| Multi-level runner | High | High | All operators |

**Critical path (shipping):** authored graph → playback contract /
trace-player / renderer → optional traversal debug instrumentation →
L3 comparison experiment → watched run.

**Critical path (research):** CharacterConcern/goals cleanup → L2
operator refactor → benchmark comparison → computable world-building
pressures (pending 5 Pro response to doc 06) → CreativePressure →
L1 operators.
