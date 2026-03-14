# L3 Experiment 1 Synthesis

Purpose: Collapse the four-paper L3 lineage into one short,
buildable experiment spec.

Inputs:
- `01-facade-extraction.md`
- `02-dodm-extraction.md`
- `03-authorial-leverage-extraction.md`
- `04-suspenser-extraction.md`
- `../11-settled-architecture.md`
- `../21-graph-interface-contract.md`

---

## One-line lineage

These four papers form a coherent design line:

- **Facade** gives the traversal pipeline
- **DODM** gives the declarative scoring architecture
- **Authorial Leverage** gives the experiment rubric
- **Suspenser** gives one important structural-tension feature

So the proposed L3 scheduler is:

**a Façade-shaped traversal pipeline, scored by a small
DODM-style feature registry, evaluated with authorial-leverage
criteria, with Suspenser contributing one structural-tension term.**

---

## Experiment 1 question

Does a small drama-management scheduler over the current Graffito
graph provide more legible, controllable, and authorially useful
traversal than simpler baselines?

This is not testing whether L3 is a full planning system. It is
testing whether a modest scheduler over authored material earns
its keep.

---

## Proposed runtime shape

### 1. Scheduler pipeline from Facade

```
available nodes
  -> priority tiers
  -> feature scoring
  -> soft weighting
  -> select next node
```

This gives four separate control surfaces:

- availability
- hard promotion
- authorial scoring
- soft bias / randomness

### 2. Feature registry from DODM

The first-pass score should be a weighted sum of named features,
not a pile of unstructured heuristics.

```
score(candidate_node) =
  w1 * trajectory_fit +
  w2 * preparation_satisfied +
  w3 * situation_mixing +
  w4 * event_homing +
  w5 * recall_value +
  w6 * structural_tension +
  penalties(exhaustion, manipulation)
```

The runtime scoring unit should be the **immediate candidate node**.
If shallow lookahead is added later, it should only project forward to
improve the score of that candidate node, not replace the runtime unit
with paths or clusters.

`preparation_satisfied` is intentionally graph-safe vocabulary. It
means "has the authored precursor material for this node or event been
shown?" It does **not** mean importing L2 concern objects into L3.

### 3. Structural tension from Suspenser

Use a lightweight approximation:

- authored `resolution_path_count` per active situation
- node tags for `option_effect = close | open | clarify | none`

This yields a second tension signal:

- **trajectory tension** = shape over time
- **structural tension** = how narrowed the situation currently is

### 4. Evaluation frame from Authorial Leverage

Judge the scheduler on:

- equivalent policy complexity
- resilience under graph changes
- controlled variety
- watched-run quality
- conductor expressivity

---

## Minimal graph annotation burden

Canonical contract note:
- `../21-graph-interface-contract.md`

Each candidate node should have:

| Field | Source |
|-------|--------|
| `availability_test` | Facade |
| `priority_tier` or `priority_test` | Facade |
| `delta_tension` | Facade |
| `delta_energy` | Facade |
| `setup_refs[]` / `payoff_refs[]` | DODM, graph-safe earnedness |
| `line_id` / `subplot_id` / `situation_id` | DODM |
| `event_id?` / `event_commit_potential?` | DODM + doc 11 event proximity |
| `pressure_tags[]?` | graph-readable thematic / pressure grouping |
| `resolution_path_count?` | Suspenser, situation-level |
| `option_effect?` | Suspenser |
| `importance?` | DODM / leverage |

This is enough for a real first pass. It is not enough for full
planning, and that is fine.

Note the boundary: these are **authored graph tags readable by a
scheduler**, not character concerns, appraisals, or hypothetical
contexts.

---

## Proposed comparison arms

Compare the baseline arms plus an ablated C arm over the same graph.

All three arms should share the same minimum traversal state:

- current node
- recent path
- visit history / recency
- situation activation
- event-approach counts

Otherwise the richer arms can win simply by being the first ones with
real state.

### A. Weighted-random baseline

- available nodes
- simple recency suppression / revisit weighting
- situation-local continuity bias
- no target arc
- no feature registry

### B. Facade baseline

- availability
- priority tiers
- shared traversal history state
- target trajectory slope
- soft weighting from recency / continuity / conductor bias

### C1. Façade + DODM feature registry

- Façade pipeline
- DODM-style weighted feature sum
- no structural tension yet
- no lookahead

### C2. C1 + Suspenser structural tension

- C1
- Suspenser-style structural tension feature
- no lookahead

### C3. C2 + optional shallow lookahead

- C2
- shallow lookahead only if immediate-node scoring proves too weak

If the richer arms do not clearly beat the simpler ones on leverage and
run quality, then the extra machinery is not justified.

The specific comparison question is:

- does B beat A because tiered trajectory control matters?
- does C1 beat B because the feature registry matters?
- does C2 beat C1 because structural tension matters?
- does C3 earn its keep, or just add complexity?

---

## What to measure

### Run quality

- trajectory fit
- overexposure / exhaustion
- event-approach legibility
- recall quality
- subjective coherence of watched runs

### Leverage

- equivalent-policy complexity
- graph-change resilience
- controlled variety across runs
- conductor expressivity

### Structural tension value

- does it explain some apparent flatlines as sustained suspense?
- does it reduce unearned spikes?
- does it improve escalate/release behavior?

---

## Suggested implementation order

### Phase 1

Implement the shared traversal substrate plus the Façade skeleton:

- edge walking
- current node / recent path / visit history
- situation activation
- availability
- priority tiers
- `delta_tension` / `delta_energy`
- target trajectory

### Phase 2

Add the DODM feature registry:

- `preparation_satisfied`
- `situation_mixing`
- `event_homing`
- `recall_value`
- `exhaustion_penalty`
- `manipulation_penalty`

### Phase 3

Add the Suspenser refinement:

- `resolution_path_count`
- `option_effect`
- `structural_tension`

### Phase 4

Evaluate with the Authorial Leverage scorecard and keep the richer arms
ablated long enough to see which addition actually paid for itself.

This order matters. It keeps the experiment falsifiable and stops
the scheduler from becoming a research sink before it proves
value.

---

## What not to build in Experiment 1

Do not add:

- full expectimax to story end
- player models
- L2-style contexts or proposal objects
- large planning ontologies
- full fabula/sjuzhet/discourse machinery
- deep lookahead unless shallow scoring clearly fails

If the experiment needs these to look good, the current L3 thesis
is probably overstated.

---

## Current graph suitability

The current Graffito v0 fixture is useful as a **Phase 1 pilot**, not as
the full Experiment 1 substrate.

Why:

- it has `13` nodes across only `2` situations in
  `daydreaming/fixtures/graffito_v0_scenes_3_4.yaml`
- its edges are still empty
- it was authored as a playback fixture, not as an event-bearing
  traversal benchmark

That means it can answer:

- does a small stateful scheduler beat a hand-authored trace or
  weighted-random walk on this slice?
- do tiers and trajectory control produce visibly better movement than
  naive traversal?

It cannot yet cleanly answer:

- event homing
- cross-situation mixing over a larger graph
- structural tension over multiple active routes
- resilience under meaningful graph expansion

The better full substrate is the City Routes brief, which explicitly
targets `28-32` nodes, `6` situations, `4` named events, and `30-40`
edges.

---

## Current best hypothesis

The likely winning shape is:

- **Façade for pipeline**
- **DODM for scoring**
- **Authorial Leverage for judgment**
- **Suspenser as one additional feature**

Not:

- Façade plus DODM plus Suspenser all as equal architectures
- Suspenser as a whole scheduler
- L3 as a planner
- L3 as a second cognitive engine

That is the narrow, defensible version of the L3 claim.
