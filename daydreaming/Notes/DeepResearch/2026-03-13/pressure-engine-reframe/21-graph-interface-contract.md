# Graph Interface Contract

Purpose: freeze the minimum cross-lane field contract for the shared
graph/interface seam.

This note operationalizes the membrane role described in
`11-settled-architecture.md`. The goal is not a rich ontology. The goal
is a minimal, stable contract that lets `L1`, `L2`, and `L3`
interoperate without leaking runtime internals across lanes.

---

## Core Rule

The shared graph stores:

- authored graph structure
- stable cross-lane annotations
- proposal and provenance records that must survive beyond one cycle

The shared graph does **not** store:

- live `L2` concern stack state
- reminded episode refs from the current cycle
- appraisal objects
- context trees
- live `L3` diagnostics
- lookahead paths
- raw conductor state as graph mutation

Runtime state remains in lane-local state and logs. The graph carries
stable residue and authored structure.

---

## Three Channels

### 1. Authored graph asset

Human-authored or human-curated structure that exists independent of any
single run.

### 2. Stable graph-readable residue

Small, durable annotations that let lanes compose without sharing
runtime ontology.

### 3. Runtime streams and logs

Lane-local state used by the scheduler, kernel, dashboard, or stage.
This may be logged, but it does not become the cross-lane graph
contract by default.

---

## What Must Live in the Graph

### Node and edge identity

- `node_id`
- `edge_id`
- `node_type`
- `line_id`
- `subplot_id?`
- `situation_id`
- `event_id?`

### Authored traversal fields

- `availability_test`
- `priority_tier` or `priority_test`
- `delta_tension`
- `delta_energy`
- `setup_refs[]`
- `payoff_refs[]`
- `importance?`
- `event_commit_potential?`
- `option_effect?`
- `resolution_path_count?`

Rules:

- `setup_refs[]` and `payoff_refs[]` must be mechanically resolvable.
- In v1 they may resolve only against:
  - `events[].id`
  - `situations[].id`
  - `reference_markers[].id`
- `option_effect` is a closed vocabulary:
  - `close`
  - `open`
  - `clarify`
  - `none`

### Cross-lane tags

- `pressure_tags[]`
- `practice_tags[]`
- `contrast_tags[]?`
- `origin_pressure_refs[]`
- `branch_outcome_tags[]?`

These are the graph-readable residues that matter early. They describe
what the authored material is about or what kind of pressure it bears,
not the current contents of the live kernel.

Canonical lineage rule:

- the one stable cross-lane lineage strip is `origin_pressure_refs[]`
- do not introduce `derived_from_concerns[]` or parallel aliases

### L1 critic annotations

- `deficiency_type`
- `detector_basis`
- `proposal_refs[]`
- `evaluation_summary`

These are records about graph repair proposals, not silent world
mutations.

---

## What Must Stay Out of the Graph

Do not write these into the shared graph contract:

- `active_concern_refs`
- `reminded_episode_refs`
- full appraisal records
- current emotion stack
- lane-internal assumption patches
- hypothetical context trees
- raw `TraversalDiagnostic` sets
- beam-search or lookahead internals
- current conductor bias vector

These belong to `L2` runtime state, `L3` runtime state, dashboard state,
or logs.

---

## Provenance Requirements

Any stable annotation or proposal record written to the graph should
carry:

- `source_lane`: `L1` | `L2` | `L3` | `human`
- `scope`: `authored` | `projection` | `proposal`
- `confidence`: coarse confidence or validation state
- `revisability`: `fixed` | `editable` | `ephemeral_candidate`
- `source_ref`: note, run, trace, or fixture origin

This is enough to avoid blending authored facts, projections, and
proposals into one undifferentiated layer.

---

## Lane-by-Lane Contract

### `L1`

`L1` may read:

- authored graph structure
- stable tags
- proposal and deficiency records

`L1` may write:

- deficiency annotations
- candidate repair proposals
- evaluation summaries

`L1` may not silently rewrite canon through the graph seam.

### `L2`

`L2` may read:

- authored graph structure
- stable pressure and practice tags

`L2` may project only stable residue such as:

- `pressure_tags[]`
- `practice_tags[]`
- `origin_pressure_refs[]`
- `branch_outcome_tags[]`

`L2` runtime internals stay in kernel state and dashboard feeds.

### `L3`

`L3` may read:

- authored graph structure
- traversal annotations
- stable pressure and event tags

`L3` runtime state stays outside the graph:

- current node
- recent path
- visit counts
- event-approach counts
- diagnostic scores
- lookahead paths

Those belong to `TraversalState`, logs, and evaluation artifacts.

---

## Minimum Viable Seam for Early Experiments

For early work, the canonical seam is:

- authored nodes and edges
- traversal fields: availability, priority, tension/energy deltas
- event fields: event id, commit potential, resolution-path count,
  option effect
- cross-lane tags: pressure, practice, contrast, origin refs
- L1 annotations: deficiencies and proposal records
- provenance fields on any projected or critic-written annotation

Nothing more is required for:

- Graffito pilot
- City Routes L3 experiment
- narrow L1 critic test
- early L2 refactor boundary discipline

---

## Practical Rule of Thumb

If a field changes every cycle, it probably does not belong in the
shared graph.

If a field helps another lane interpret authored material across runs,
it may belong in the shared graph.
