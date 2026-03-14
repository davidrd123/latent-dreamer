# City Routes Experiment 1 Checklist

Purpose: the full research-lane substrate for Experiment 1.

This is the first world that is large enough to test the L3 thesis
properly rather than merely pilot the scheduler shape.

Inputs:
- `08-l3-experiment-1-synthesis.md`
- `../11-settled-architecture.md`
- `../21-graph-interface-contract.md`
- `../../../experiential-design/22-traversal-sandbox-candidates.md`
- `../../../experiential-design/23-brief-city-night-pursuit.md`

---

## Goal

Run the full L3 experiment on a graph that is large enough to test:

- traversal pipeline quality
- feature-registry value
- structural-tension value
- whether shallow lookahead is actually needed
- authorial leverage

This is the experiment that should falsify or strengthen the narrow
L3 claim.

---

## Required substrate

Target from the City Routes brief:

- `28-32` nodes
- `6` situations
- `4` named events
- `30-40` edges

The graph should support:

- multiple exits per hot cluster
- at least one refuge zone
- at least one spectacle zone
- event-bearing nodes
- at least one meaningful return after refuge

---

## Build checklist

### 1. Author the graph substrate

- author node clusters for the six recommended situations
- connect them with explicit edges
- make sure no single cluster monopolizes all event pressure
- ensure at least one event can be approached from more than one path

Deliverable:
- authored City Routes graph fixture with explicit nodes and edges

### 2. Add required scheduler annotations

For each node, add:

- `availability_test`
- `priority_tier` or `priority_test`
- `delta_tension`
- `delta_energy`
- `setup_refs[]` / `payoff_refs[]`
- `line_id` / `subplot_id` / `situation_id`
- `event_id?`
- `event_commit_potential?`
- `pressure_tags[]?`
- `option_effect?`
- `importance?`

For each situation or event-bearing cluster, add:

- `resolution_path_count`

Boundary rule:
- these must stay graph-readable scheduler tags
- do not import L2 concern objects, appraisal records, or contexts

### 3. Implement the experiment arms

All arms must share:

- current node
- recent path
- visit history / recency
- situation activation
- event-approach counts
- conductor bias surface

#### A. Weighted-random baseline

- availability
- recency suppression
- local continuity bias
- no target trajectory
- no feature registry

#### B. Façade baseline

- availability
- priority tiers
- target trajectory slope
- soft weighting
- no DODM feature registry

#### C1. Façade + DODM feature registry

- Façade pipeline
- DODM-style weighted feature sum
- no structural tension yet
- no lookahead

#### C2. C1 + structural tension

- C1
- Suspenser-style structural tension term
- no lookahead

#### C3. C2 + shallow lookahead only if needed

- C2
- optional shallow lookahead only if immediate-node scoring proves too weak

### 4. Implement the feature registry

First-pass feature set:

- `trajectory_fit`
- `preparation_satisfied`
- `situation_mixing`
- `event_homing`
- `recall_value`
- `structural_tension`
- `exhaustion_penalty`
- `manipulation_penalty`

Keep the scoring unit as the immediate candidate node.
If lookahead is added, it may only refine candidate-node scores.

### 5. Instrument the runs

Per cycle, record at least:

- candidate set size
- selected node
- score breakdown by feature
- priority tier used
- visit count / recency
- current situation activation
- event-approach state
- current target trajectory
- conductor bias snapshot

This should be enough to explain why the scheduler moved where it did.

---

## Evaluation checklist

### Runtime quality

- trajectory fit
- overexposure / exhaustion
- event-approach legibility
- recall quality
- subjective coherence of watched runs

### Authorial leverage

- equivalent-policy complexity
- graph-change resilience
- controlled run diversity
- conductor expressivity

### Structural tension value

- does `structural_tension` explain some apparent flatlines?
- does it reduce unearned spikes?
- does it improve escalate / release choices?

---

## Change-resilience checks

Make at least one local graph change after the initial runs:

- add `2-3` nodes to one situation
- or add one event-bearing branch
- or change one event's preparation structure

Then rerun all three arms.

Question:
- does the full scheduler absorb this with local metadata edits, or
  does it require broad retuning?

If the latter, the leverage claim weakens immediately.

---

## Variety checks

Run multiple traversals over the same graph:

- same graph, same weights, different randomness seeds
- same graph, different conductor biases

Track:

- unique node sequences
- distinct arc shapes
- event ordering variation
- whether variation preserves legibility

Randomness alone does not count as success.
The goal is controlled variety.

---

## Non-goals

Do not add:

- full expectimax
- simulated player branches
- L2-style contexts
- planning ontologies
- broad narratology machinery
- feature explosion before the first pass is judged

If the experiment only works after these additions, the current L3
claim is too ambitious.

---

## Success condition

The experiment succeeds if:

- arm B clearly beats arm A on movement legibility
- arm C1 clearly beats arm B on at least some combination of
  event approach, recall timing, and leverage
- arm C2 clearly adds value over C1 on at least some combination of
  event approach, recall timing, structural tension, and leverage
- arm C3 is only retained if it adds clear marginal value over C2
- the feature registry remains understandable
- graph changes are absorbed mostly locally
- different conductor bias produces meaningful, legible differences

If the richer C arms do not clearly beat the simpler ones, stop
expanding L3 and keep the shipping path narrow.
