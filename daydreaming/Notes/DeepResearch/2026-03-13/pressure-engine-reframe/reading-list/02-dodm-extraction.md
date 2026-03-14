# DODM — L3 Extraction

Source: Nelson, Mateas, Roberts, Isbell, "Declarative Optimization-
Based Drama Management in Interactive Fiction" (IEEE CGA 2006)

Source file: `sources/markdown/DODM/source.md`

Purpose: Extract what DODM adds beyond Facade's beat sequencing
for the L3 traversal scheduler.

---

## What DODM actually is

DODM is the generalized, formalized version of Facade's drama
management. Where Facade hardcodes a tension-arc scorer, DODM
makes the evaluation function **declarative and author-specified**
— a linear weighted sum of composable features.

The core model:

```
Plot points       — important events that can occur
DM actions         — interventions the DM can take
Evaluation function — author-specified goodness over complete
                      sequences, expressed as weighted features
```

The DM's job: after each plot point, choose actions (or no action)
to maximize future goodness of the complete story, using
**expectimax game-tree search** over plot points and DM actions.

---

## The evaluation feature toolbox

This is the main stealable contribution. Instead of one hardcoded
tension-arc formula, DODM provides **composable evaluation
features** that authors weight and combine:

### Standard features (from the paper and Weyhrauch's dissertation)

| Feature | What it scores |
|---------|---------------|
| Motivation | For each plot point in sequence, did its `motivated_by` plot points happen earlier? |
| Plot mixing | Are subplot/quest lines interleaved rather than resolved serially? |
| Plot homing | Is the sequence converging toward the desired ending? |
| Ordering preferences | Author-specified partial orderings respected? |
| Completeness | What fraction of important plot points occurred? |
| Pacing | Distribution of plot points over time |

### How it works

The evaluation function is:

```
goodness(sequence) = sum_i (weight_i * feature_i(sequence))
```

Authors tune the experience by:

1. Adjusting feature weights (emphasize pacing vs. motivation)
2. Adding/removing features
3. Changing plot point annotations (what motivates what)

Simple parameter changes → drastically different policies.

---

## DM actions (intervention types)

| Action type | What it does |
|------------|-------------|
| Cause | Force a plot point to happen |
| Hint | Make it more likely a plot point will happen |
| Deny | Prevent a plot point from happening |
| Un-deny | Re-enable a previously denied plot point |

Each action also has a `MANIPULATION` score (0.0-1.0) — how
rail-roaded the experience would feel. The evaluation function
can penalize high-manipulation actions.

---

## The expectimax search

DODM uses game-tree search to look ahead:

```
Current state
  → enumerate possible DM actions (including no-action)
  → for each action, enumerate possible player-caused plot points
  → recurse to end of story
  → evaluate complete sequences with the evaluation function
  → back up evaluations using expectimax
  → choose action that maximizes expected story goodness
```

This is expensive but produces globally-aware decisions. The
system considers not just "is this the best next step?" but
"does this next step lead to the best *complete story*?"

---

## Plot point annotations

Each plot point is described by attribute/value pairs:

```
get_sword:
  QUEST = sword
  MOTIVATED_BY = {info_sword, info_loc_sword}
  COORD = 6 0
```

- `QUEST` — which subplot/quest line this belongs to
- `MOTIVATED_BY` — which plot points should happen before this
  one for it to feel earned
- `COORD` — location information

These annotations are what the evaluation features read.
The authoring burden is in **annotating plot points with
structural metadata**, not in writing trigger logic.

---

## Mapping to L3 scheduler

| DODM concept | L3 equivalent | Notes |
|-------------|---------------|-------|
| Plot point | Graph node / event | Selectable unit |
| DM action (cause/hint/deny/un-deny) | Traversal intents + conductor overrides | `escalate` ≈ hint/cause; `release` ≈ deny high-tension nodes |
| Evaluation function | Traversal scoring function | Weighted sum of diagnostic-derived features |
| Feature weights | Conductor-tunable parameters | Faders could map to feature weights |
| Expectimax search | Lookahead (doc 11's `LookaheadPath`) | 2-3 step beam search, not full game tree |
| MOTIVATION feature | `event_approach_count` + setup/payoff tracking | Has this node's precursor material been shown? |
| Plot mixing | `overexposed_situation` diagnostic (inverted) | Are we interleaving situations? |
| Plot homing | `event_proximity` diagnostic | Are we converging toward committable events? |
| MANIPULATION score | Could map to conductor mode | High-manipulation moves only in forced mode |

---

## What's genuinely new vs. Facade and doc 11

### 1. Declarative evaluation as weighted feature sum

This is the main upgrade. Instead of one tension-arc formula
(Facade) or seven separate diagnostics (doc 11), DODM says:
make the evaluation function **a weighted sum of named features**
that can be tuned independently.

**Implication:** Doc 11's seven diagnostics (overexposed_situation,
recall_due, tension_flatline, tension_spike, exhausted_node,
event_proximity, avoided_material) could become evaluation
features with author/conductor-tunable weights rather than
independent critic signals.

### 2. Global lookahead vs. local scoring

Facade scores one step ahead. DODM searches to the end of the
story. Our system probably wants something in between — doc 11's
2-3 step beam search (`LookaheadPath`). The question is whether
local scoring (Facade-style) is good enough or whether even
shallow lookahead measurably improves arc legibility.

### 3. DM actions as typed interventions

DODM's cause/hint/deny/un-deny is a richer intervention
vocabulary than just "select the next node." The L3 scheduler
could:

- **Hint**: boost situation activation (make it more likely
  to be selected soon)
- **Deny**: temporarily exclude a node/situation (too early,
  overexposed)
- **Cause**: conductor force-selects (forced mode)
- **Un-deny**: re-enable previously excluded material (recall)

This maps cleanly to doc 11's conductor modes.

### 4. Motivation tracking

DODM's `MOTIVATED_BY` annotation and motivation feature directly
address the "earned vs. unearned" problem. A climactic node
should score low unless its motivating precursor material has
been shown. This is what doc 11's `event_proximity` and
`tension_spike` (unearned intensity) diagnostics are reaching
toward, but DODM makes it explicit per-node authoring.

### 5. Feature weights as the conductor surface

If the evaluation function is `sum(weight_i * feature_i)`, then
the conductor's faders could directly control those weights:

- Fader 1: motivation weight (how much does "earned" matter?)
- Fader 2: mixing weight (interleave situations vs. focus?)
- Fader 3: pacing weight (faster vs. slower?)
- Fader 4: tension-arc weight (track arc vs. free-form?)

This gives the conductor **structural control** rather than
just intensity/energy knobs.

---

## What to take for Experiment 1

1. **Weighted feature sum as evaluation architecture.** Make the
   scoring function a tunable sum rather than hardcoded logic.

2. **Per-node `MOTIVATED_BY` annotations** on Graffito graph
   nodes — what must have been shown before this node feels
   earned?

3. **Deny/un-deny as traversal mechanism** — the scheduler can
   temporarily exclude overexposed or premature material, then
   re-enable it when conditions change. Cleaner than trying to
   score everything continuously.

4. **Shallow lookahead** (2-3 steps) rather than full expectimax.
   Score candidate 2-3-step paths using the feature sum, pick
   the best-scoring short path, execute the first step.

What NOT to take:

- Full expectimax search to end of story (too expensive, wrong
  granularity for continuous conducted performance)
- Player modeling (no player — conductor replaces this)
- The specific EMPath game domain

---

## Minimal L3 contract implied by DODM

DODM sharpens the next step after Façade. Façade gives the shape
of the scheduler pipeline. DODM says the scoring layer should be
**declarative, named, and author-tunable** rather than baked into
one formula.

### Node-side annotations

To make DODM-style evaluation real, authored graph material needs
slightly richer structural metadata:

| Field | Why it exists |
|-------|---------------|
| `motivated_by[]` | What precursor material makes this node feel earned |
| `line_id` / `subplot_id` | For mixing and focus features |
| `availability_test` | Mechanical gate from traversal state |
| `delta_tension`, `delta_energy` | Predicted trajectory effect |
| `denyable?` / `hintable?` | Whether the scheduler can suppress or promote it |
| `importance` | Whether omission should be penalized |

This is still graph annotation, not a new ontology. The main
change is that "earnedness" and "subplot interleaving" stop being
hand-wavy and become readable from node metadata.

### Feature registry

The core DODM steal is a named feature registry:

```
TraversalFeature {
  name
  weight
  evaluate(state, candidate_or_path) -> score
}
```

For the current L3 architecture, the first-pass registry could be:

- `trajectory_fit`
- `motivation_satisfied`
- `situation_mixing`
- `event_homing`
- `recall_value`
- `exhaustion_penalty`
- `manipulation_penalty`

That is a better fit for doc 11 than treating diagnostics as a bag
of unrelated warnings. Diagnostics stay useful, but DODM suggests
they should feed a coherent feature sum.

### Intervention vocabulary

DODM also clarifies that the scheduler does more than "pick the
best next node." It can shape availability.

For Daydream, the minimum intervention vocabulary should be:

- `promote` — temporarily increase priority / weight
- `suppress` — temporarily deny or cool down material
- `release` — remove a suppression when conditions change
- `select` — actually choose the next node

This still fits doc 11's traversal-control framing. It does **not**
mean introducing heavyweight DM actions or world edits. It means
L3 can manage the candidate set, not only rank it.

### Lookahead boundary

DODM is a warning as much as a model. The useful steal is
feature-scored lookahead, not expectimax to story end.

For our system, the right boundary still looks like:

- score immediate candidates by feature sum
- optionally score 2-3 step `LookaheadPath`s
- execute only the first step

That keeps L3 as a live scheduler, not a full planner.

---

## What not to cargo-cult from DODM

### 1. Do not import the fake player model

DODM needs a player model because it is optimizing under uncertain
player action. We do not have that problem. Replacing the player
with a sloppy pseudo-player would just add bad assumptions.

The analogue in our stack is conductor bias plus graph dynamics,
not "predicted user behavior."

### 2. Do not over-atomize the graph

The paper is explicit that plot-point granularity is a hard
authoring problem. If every tiny beatlet becomes a node, the
feature signal gets noisy and the graph becomes tedious to
annotate. If nodes are too coarse, the scheduler loses leverage.

That means Graffito nodes should represent meaningful traversal
units, not every possible micro-transition.

### 3. Do not let evaluation replace curation

DODM assumes an author-specified goodness function can stand in
for aesthetic judgment. That is useful for traversal control, but
it should not be mistaken for full artistic evaluation. Weighted
features are a control surface, not a substitute for authorship.

### 4. Do not make "manipulation" a moral absolute

The manipulation penalty is useful, but in our system some high-
control moves are legitimate, especially in conductor-forced mode.
The right lesson is "track manipulation explicitly," not "avoid it
at all costs."

### 5. Do not turn L3 into a full planner

Once you have weighted features and lookahead, it becomes tempting
to keep adding search depth and intervention types. That is the
wrong direction for the watched-run path. L3 should remain a
traversal scheduler over authored material, not a global story
planner.

---

## Experiment 1 delta after reading DODM

Façade gives the scheduler shape. DODM gives it a better scoring
surface.

### The useful v0 question

Does replacing ad-hoc traversal scoring with a named weighted
feature sum improve legibility and controllability of runs over
the Graffito graph?

### Minimal additions beyond the Façade pass

Add only:

1. per-node `motivated_by[]`
2. per-node `line_id` / situation lineage
3. a small named feature registry
4. one explicit `manipulation_penalty`
5. optional 2-step lookahead scored by the same feature sum

Do **not** add:

- full DM action search
- long-horizon planning
- simulated player branches
- many new feature types at once

### Suggested comparison

Compare:

1. Façade-style slope matcher only
2. weighted feature sum without lookahead
3. weighted feature sum with shallow lookahead

If 2 clearly beats 1, DODM earns its keep immediately. If 3 only
barely beats 2 while adding opacity, that is evidence to keep
lookahead minimal.

### What this would clarify

- whether `motivated_by` annotations meaningfully reduce unearned
  jumps
- whether mixing / homing deserve first-class feature status
- whether the conductor should control feature weights directly
  or stay at the trajectory-bias level
- whether L3's main problem is scoring architecture or graph
  richness
