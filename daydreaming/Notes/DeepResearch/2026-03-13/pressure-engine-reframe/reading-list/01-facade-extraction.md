# Facade Beat Sequencing — L3 Extraction

Source: Mateas & Stern, "Facade: An Experiment in Building a
Fully-Realized Interactive Drama" (GDC 2003)

Source file: `sources/markdown/mateas-gdc2003/source.md`

Purpose: Extract what's operationally relevant for the L3
traversal scheduler proposed in `11-settled-architecture.md`.

---

## What the paper actually describes

Facade is a real-time interactive drama (~20 minutes) where two
characters (Grace and Trip, a couple with a crumbling marriage)
interact with a human player. The architecture has two layers:

1. **Within a beat:** How characters respond to player action
   (local agency — not our concern here)
2. **Between beats:** How the beat sequencer chooses what happens
   next (global agency — this is the L3-relevant part)

~200 beats authored. ~18 per playthrough. Only ~25% of content
seen per session. The authored content is deliberately bigger
than any single run.

---

## The beat sequencing algorithm

The sequencer fires when the current beat terminates or aborts.
Five steps:

```
1. Evaluate preconditions for all unused beats
   → Satisfied set

2. Evaluate priority tests on Satisfied
   → HighestPriority set (highest tier only — hard filter)

3. Score each beat in HighestPriority against desired tension arc
   → ScoredHighestPriority

4. Multiply scores by static/dynamic weights
   → WeightedScoredHighestPriority

5. Randomly select from weighted probability distribution
```

### The scoring formula

```
score = 1 / e^|slope_target - delta_T_beat|
```

Where:

- `slope_target = (T_target - T_current) / beats_remaining`
  — the desired tension slope given current position and arc target
- `delta_T_beat` — the tension change this candidate beat would
  produce (from its authored `effects` annotation)

Beats that match the desired trajectory score high. Mismatches
fall off via exponential decay — sharply, not linearly.

### Cumulative error tracking

The sequencer tracks:

```
error_n = sqrt(sum_i=1..n (A_i - A_i_opt)^2)
```

When cumulative error grows too large or the candidate set
empties, that signals the beat collection isn't rich enough to
approximate the desired arc given the current interaction history.
This is diagnosed as an **authoring problem**, not a runtime
failure.

---

## Selection knowledge (per-beat annotations)

Every beat is annotated with:

| Annotation | Purpose |
|-----------|---------|
| `precondition` | Is this beat available given current state? Boolean gate. |
| `priority` / `priority_test` | Static or conditional priority tier. Hard filter — only the highest-populated tier gets scored. |
| `weight` / `weigh_test` | Static or conditional probability modifier. Soft bias within tier. |
| `effects` | What story values change if beat completes successfully. Drives the scoring formula. |

### The tier system

Priority tiers are a **hard pre-filter** before scoring. Critical
plot points go in high tiers with narrow preconditions. This means
the system can force must-happen events without special-case logic
— they're just in a tier that outranks everything else when their
preconditions are met.

---

## Key structural decisions

### Beats are a bag, not a sequence

Facade explicitly chose a story type (kitchen-sink domestic drama)
that survives resequencing. The partial ordering comes from
preconditions and effects on shared state, not from authored
sequence. This is "character-oriented" rather than "plot-oriented"
— Mateas explicitly contrasts it with tightly-plotted drama
(Casablanca) where reordering would break coherence.

### The system is an editor, not a generator

Mateas: "The system's role is that of an editor, assembling a
story from an array of story fragments, where each fragment was
created by a human author, who presumably possessed some original
intent on how the fragments could ultimately fit together."

This directly matches doc 11's position: L3 is a traversal
scheduler over authored material, not a generative engine.

### The desired arc is explicit

Facade has a target tension curve (Aristotelian shape) that the
scorer actively tracks against. The scoring is always *relative to
where you are on the arc* — a high-tension beat scores well when
the arc says "build" but poorly when the arc says "release."

---

## Mapping to L3 scheduler

| Facade concept | L3 equivalent | Notes |
|----------------|---------------|-------|
| Beat | Graph node or node cluster | Nodes are the selectable units |
| Precondition | Node availability given `TraversalState` | Visit history, recency, situation activation |
| Priority tier | Diagnostic-driven tier promotion | Overdue nodes, conductor-forced nodes get promoted |
| Tension arc scoring | Score against `recent_emotional_trajectory` + target shape | See "What's new" below |
| Effects | Authored `delta_tension`, `delta_energy` per node | Authoring burden but necessary for scoring |
| Weight | `conductor_bias` + `situation_activation` weights | Maps to fader positions and activation state |
| Cumulative error | New diagnostic: `arc_drift` | How far has actual trajectory deviated from intended shape? |
| Bag-of-beats | Dream graph node pool | Already how the graph works |
| ~25% content per run | Natural for dream material | Revisit, recall, and avoidance are features, not bugs |

---

## What's genuinely new vs. doc 11

### 1. Explicit target arc (doc 11 doesn't have this)

Doc 11's diagnostics (`tension_flatline`, `tension_spike`) detect
local problems but don't reference a global target shape. Facade's
scorer always knows *where it should be* on the arc.

**Question for our system:** Should the L3 scheduler have an
explicit arc target (authored or tidal-oscillator-derived), or
just local diagnostics? The conductor's tidal oscillators from
doc 11 could serve as the arc target, which would make the
conductor the *author of the arc shape* in real time.

### 2. Priority tiers as hard pre-filter (doc 11 uses soft scoring)

Doc 11 treats diagnostics and intents as a scoring/ranking
problem. Facade shows that a tier system (hard filter first, then
soft scoring within tier) is simpler and more controllable.

**Implication:** The conductor could effectively promote nodes to
a higher tier via hard override, while normal selection stays
in the default tier with soft scoring. This matches doc 11's
three conductor modes (autonomous / assisted / forced) — forced
mode is just "conductor promotes something to top tier."

### 3. Per-node effects are mandatory authoring

Every Facade beat declares what tension change it produces.
For our system, graph nodes need authored `delta_tension`,
`delta_energy` values — not just content. This is an authoring
burden that must be part of the Graffito graph spec.

### 4. Cumulative error as diagnostic

Facade tracks how far the actual arc has drifted from the
desired arc. This is a diagnostic the current architecture
doesn't have. High cumulative error could trigger:
- Scheduler takes corrective action (escalate or release)
- Conductor gets a signal that the graph isn't rich enough
  for the current trajectory
- The system flags that it's "off course"

### 5. Scoring is continuous, not intent-categorical

Facade uses a single continuous scoring function (exponential
decay from ideal slope) rather than discrete intent categories
(dwell, shift, recall, escalate, release). The intents in doc 11
could be an *interpretation* of the scoring result rather than
an *input* to it — the scheduler scores, selects, and then the
intent is derived from what kind of move it made.

---

## What to take for Experiment 1

The minimal steal for the L3 scheduler test:

1. **The precondition/priority/score/weight pipeline** as the
   selection architecture. Four separable stages, each with
   independent authoring knobs.

2. **Per-node `delta_tension` annotations** on Graffito graph
   nodes, so scoring against a trajectory is possible.

3. **The exponential scoring formula** (or a variant) for
   matching candidates to desired trajectory slope.

4. **Cumulative arc error tracking** as a diagnostic signal.

5. **The conductor as arc-shape authority** — tidal oscillators
   or fader-driven trajectory targets replace Facade's static
   Aristotelian curve with a live, performer-shaped one.

What NOT to take:

- The NLP/discourse-act system (irrelevant — no player input)
- The internal beat-goal structure (our nodes are simpler)
- The specific ABL behavior language
- The player model (no player — conductor replaces this)

---

## Minimal L3 contract implied by Facade

If we actually steal the useful part of Facade, the L3 scheduler
needs more than "pick a node with some heuristics." It needs an
explicit selection contract over authored graph material.

### Node-side annotations

Each selectable graph node or node-cluster should expose:

| Field | Why it exists |
|-------|---------------|
| `availability_test` | Precondition gate from traversal state |
| `priority_tier` or `priority_test` | Hard promotion for must-happen material |
| `delta_tension` | Predicted tension change if selected |
| `delta_energy` | Predicted energy shift if selected |
| `situation_id` / cluster id | For exposure and shift logic |
| `cooldown` / recency sensitivity | To avoid exhausted-node loops |
| `event_proximity_tag?` | Whether this node advances toward a committable event |

This is the minimum annotation burden required to make trajectory
matching and tiered selection real rather than rhetorical.

### Scheduler-side pipeline

For doc 11's `TraversalState` and `TraversalDecision`, the
Façade-shaped pipeline becomes:

```
1. Filter available nodes
   using node availability tests

2. Promote into priority tiers
   using diagnostics + conductor overrides

3. Score the highest populated tier
   against desired trajectory slope

4. Weight scored candidates
   by recency, activation, recall pressure,
   conductor bias, and optional randomness

5. Select one node
   and emit TraversalDecision
```

This is a better fit than a flat "score everything once" pass,
because it cleanly separates:

- hard must/must-not logic
- authorial trajectory matching
- soft performer bias

That separation is one of the main practical lessons from Façade.

### Conductor boundary

Façade helps clarify a boundary that doc 11 already gestures at:
the conductor should not *be* the scheduler.

The scheduler owns:
- availability
- priority tier resolution
- trajectory scoring
- selection provenance

The conductor owns:
- biasing target arc shape
- nudging weight balance
- optionally forcing top-tier promotion in explicit override mode

So the conductor is above the pipeline, not inside every scoring
term. Otherwise "live control" turns into opaque hand puppeting
and the scheduler stops being legible.

---

## What not to cargo-cult from Façade

Façade is the right ancestor for L3, but it is still easy to copy
the wrong parts.

### 1. Do not copy the fixed Aristotelian arc literally

Façade's target curve is authored in advance. Our system should
prefer a live target shaped by conductor bias, oscillator state,
or authored run mode. The steal is "score against an explicit
trajectory," not "always use one canonical curve."

### 2. Do not make node internals as heavy as beats

Façade beats are rich authored units with internal goals and
performance structure. Graffito nodes should stay lighter. L3 is
choosing traversal units, not replaying a full beat engine.

### 3. Do not let priority tiers become hidden special cases

Priority tiers are good when they encode legible policy:
- this material is overdue
- this event is now eligible
- this recall is important

They are bad when they become an invisible bag of exceptions. If a
node only ever works because it is permanently top-tier, that is a
graph or authoring problem.

### 4. Do not confuse selection with commitment

Façade's beat choice advances the run, but in our architecture
ordinary node selection is still a traversal decision, not an
ontic commit. Keep the L3 commit vocabulary from doc 11:
`canon_event | traversal_bias | activation_shift | none`.

### 5. Do not import player-model assumptions

Façade spends much of its complexity reacting to player action and
repairing discourse-level coherence. Our scheduler has no player
language loop. The interesting analogue is not "player input," but
"performer bias plus graph state."

---

## Experiment 1 framing after reading Façade

The useful v0 experiment is now clearer:

### Question

Does a Façade-shaped scheduler over the current Graffito graph
produce more legible trajectory control than weighted-random or
pure heuristic traversal?

### Minimal implementation

Add only:

1. per-node `availability`
2. per-node `delta_tension` and `delta_energy`
3. tier promotion from a small diagnostic set
4. slope-matching score against a target trajectory
5. cumulative `arc_drift` tracking

Do **not** add:

- beam search
- context trees
- proposal objects
- L2-style hypothetical branching
- a new giant ontology for nodes

### Baselines

Compare three traversers:

1. weighted-random over available nodes
2. heuristic scorer without tiers or target slope
3. Façade-shaped tier + slope + weight pipeline

### What to measure

- trajectory fit to target tension/energy shape
- amount of repeat/exhaustion
- recall quality
- event approach legibility
- authorial burden of annotations
- subjective run coherence when watching the inner-life dashboard

The key measure is not "did it tell a masterpiece story?" It is:
"does this scheduler give more authorial leverage over the watched
run than simpler alternatives?"

### Likely outcome

My expectation is that tiers + target slope will help quickly, but
only if the graph has enough well-annotated alternatives. If the
result is brittle, that is evidence about graph richness and
authoring requirements, not proof that L3 should become a
cognitive engine.
