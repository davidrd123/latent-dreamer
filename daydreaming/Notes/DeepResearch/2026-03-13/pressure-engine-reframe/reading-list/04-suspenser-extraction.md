# Suspenser — L3 Extraction

Source: Cheong & Young, "A Computational Model of Narrative
Generation for Suspense" (AAAI 2006)

Source file: `sources/markdown/Suspenser/source.md`

Purpose: Extract the suspense/tension scoring model for use as
a traversal diagnostic in the L3 scheduler.

---

## What the paper actually describes

Suspenser is a system that manipulates story structure to control
the audience's suspense level. It operates on the insight from
Gerrig & Bernardo (1994):

> A reader's suspense level is inversely proportional to the
> number of solutions available to the protagonist's dilemma.

Fewer escape routes = more suspense. The system doesn't generate
new story content — it selects and orders existing story elements
(from a fabula) to achieve a target suspense level.

---

## The suspense model

### Core heuristic

```
SL(G, Z, L, R) = 1 / success(G, Z, L, R)
```

Where:
- `G` = protagonist's goal (set of goal literals)
- `Z` = sjuzhet (the presented story content and ordering)
- `L` = plan library (possible actions available)
- `R` = reasoning bound (audience's cognitive limit)
- `success()` = number of paths to make G true given Z and R

**High suspense** = few perceived solutions for the protagonist.
**Low suspense** = many perceived solutions.

### How it controls suspense

The system has two levers:

**Hypothesis 1 (increase suspense):** Adding actions whose
effects *negate* the protagonist's goals reduces the number of
solutions → increases suspense. (Show the antagonist gaining
advantage, show resources being lost, show options closing.)

**Hypothesis 2 (decrease suspense):** Adding actions whose
effects *unify with* the protagonist's goals increases the
number of solutions → decreases suspense. (Show an ally
arriving, show a tool being found, show an escape route.)

### The two-phase algorithm

1. **Skeleton building:** Identify the core story events that
   can't be removed without breaking comprehensibility. Rate each
   event by its causal relationship to protagonist goals.

2. **Structure building:** For high suspense, add events that
   close off solutions (Hypothesis 1). For low suspense, add
   events that open solutions (Hypothesis 2). Then order the
   presentation to defer revelation of solution-closing events
   until after the audience has had time to notice the dilemma.

---

## What maps to L3

Suspenser's model is **not** a traversal scheduler. It's a
story-structure optimizer. But it provides a computable definition
of tension that the L3 scheduler can use as a **scoring variable**.

### The stealable insight

Tension/suspense is not a single authored number per node. It's a
**function of what's been shown so far relative to unresolved
stakes.**

Specifically:

- **Perceived solution space** — how many ways could the current
  situation resolve? Fewer options = higher tension.
- **Option-closing events** — showing material that removes
  options increases tension.
- **Option-opening events** — showing material that adds options
  releases tension.

This gives us a way to reason about tension that goes beyond
"this node has delta_tension = +0.3." The tension of a node
depends on **what the audience has already seen**.

---

## Mapping to L3 diagnostics

| Suspenser concept | L3 equivalent | Application |
|------------------|---------------|-------------|
| Solution count for protagonist's goal | Perceived resolution paths for active situation | Fewer visible paths = higher tension |
| Option-closing events | Nodes that narrow the situation (blocker, threat, loss) | Selecting these escalates tension |
| Option-opening events | Nodes that widen the situation (ally, tool, escape) | Selecting these releases tension |
| Skeleton (core events) | Nodes with high causal centrality | Must-show material — high priority tier |
| Reasoning bound | Audience attention model | How much context the viewer retains |
| Suspense as 1/solutions | Dynamic tension scoring | Not a static annotation but a computation |

### What this changes about doc 11's tension diagnostics

Doc 11 defines:

- `tension_flatline` — trajectory has been flat too long
- `tension_spike` — unearned intensity jump

These are **trajectory-shape** diagnostics — they look at the
recent sequence of tension values.

Suspenser adds a **structural** tension model: tension is high
when the current situation has few perceived resolution paths.
This is orthogonal to trajectory shape. You could have a
structurally tense situation (few options) with a flat trajectory
(nothing has changed recently). Suspenser would say "tension is
high even if it feels flat — the flatness IS the tension because
options are narrowing silently."

### Two kinds of tension scoring

The L3 scheduler could track both:

1. **Trajectory tension** (Facade-style): where are we on the
   arc? Are we flat, climbing, or falling?

2. **Structural tension** (Suspenser-style): how constrained is
   the current situation? How many resolution paths are visible?

These can conflict productively. Structural tension rising while
trajectory tension is flat = mounting dread. Structural tension
dropping while trajectory tension spikes = catharsis.

---

## Practical applicability to Experiment 1

Suspenser's full model requires:

- A plan library
- Goal literals for protagonists
- Causal link structure
- A planner to enumerate solutions

That's too heavy for Experiment 1. But the **conceptual model**
is usable:

### Minimum viable steal

1. **Per-situation resolution-path count** as an authored
   annotation on the Graffito graph. For each situation/tension,
   how many resolution paths exist? Authored as a small integer
   (1-5), not computed.

2. **Option-closing / option-opening tags** on nodes. Does this
   node narrow or widen the resolution space? Binary tag per node.

3. **Structural tension as a derived score:**
   ```
   structural_tension(situation) =
     1 / resolution_path_count(situation)
   ```
   Weighted by how many option-closing nodes have been shown
   vs. option-opening nodes.

4. **Use both tension signals in the scoring function:**
   - Trajectory tension (from `recent_emotional_trajectory`)
   - Structural tension (from resolution-path state)

### What this enables

- The `tension_flatline` diagnostic becomes richer: if trajectory
  is flat but structural tension is high, the flatline is
  *intentional* (suspense through stillness).
- The `tension_spike` diagnostic becomes more precise: a spike is
  unearned if structural tension hasn't been built first (no
  option-closing precursors).
- The `escalate` intent gains a structural dimension: escalate
  could mean "show option-closing material" not just "pick a
  high-intensity node."

---

## What to take for Experiment 1

1. **The conceptual model** — tension as a function of perceived
   resolution paths, not just a static annotation.

2. **Option-closing / option-opening tags** on Graffito graph
   nodes as lightweight authored metadata.

3. **Structural tension as a second scoring variable** alongside
   trajectory tension.

What NOT to take:

- The full planning-based solution enumeration (too heavy)
- The sjuzhet/fabula/discourse formalism (wrong granularity)
- The specific Suspenser implementation
- Any assumption that we need a planner to compute tension
  (authored annotations are sufficient for Experiment 1)

---

## What this paper adds to the lineage

Suspenser is not another scheduler architecture paper. It is a
proposal for one particularly useful **feature family** inside a
drama-management stack.

- **Facade** contributes pipeline and arc tracking
- **DODM** contributes declarative feature scoring
- **Suspenser** contributes a structural definition of tension
  based on perceived solution space

That makes it a better source for one scoring term than for the
overall design of L3.

---

## Minimal structural-tension contract implied by Suspenser

If we steal the useful part of Suspenser without dragging in the
planner, L3 needs a lightweight representation of perceived
resolution space.

### Situation-side annotations

At minimum, each active situation or event cluster should expose:

| Field | Why it exists |
|-------|---------------|
| `resolution_path_count` | How many plausible ways out currently exist |
| `core_path_count` | How many of those are high-salience/credible |
| `stakes_level` | Why narrowing matters |
| `active_goal?` | Whose problem this currently is |

This does not need to be computed by planning for v0. It can be
authored or derived from simple graph metadata.

### Node-side annotations

Each node that changes the situation should expose:

| Field | Why it exists |
|-------|---------------|
| `option_effect` | `close | open | clarify | none` |
| `affected_situation_id` | Which tension space it modifies |
| `stakes_delta?` | Optional change in how costly failure feels |

This is enough to make "escalate" and "release" structurally
legible rather than only intensity-shaped.

### Derived feature

Within the DODM-style feature registry, Suspenser most naturally
becomes a feature like:

`structural_tension(state, candidate_or_path)`

This should be evaluated alongside:

- trajectory fit
- motivation satisfied
- exhaustion penalty
- recall value

In other words, Suspenser belongs inside the scoring layer, not
next to it as a separate architecture.

---

## What not to cargo-cult from Suspenser

### 1. Do not import the full planner stack

The paper's formal model depends on plan libraries, causal links,
reasoning bounds, and explicit solution enumeration. That is far
too heavy for the watched-run path.

### 2. Do not overformalize fabula/sjuzhet at runtime

The distinction is useful analytically, but our runtime unit is
still authored graph material plus traversal state. Recasting the
entire system in Suspenser's narratology vocabulary would create
more friction than leverage.

### 3. Do not collapse all tension into solution count

This paper gives one structural source of tension. It does not
replace arc-shape tension, musical tension, or affective
intensity. The correct use is additive, not totalizing.

### 4. Do not pretend the audience model is solved

Its "reasoning bound" is a stand-in for audience cognition. For
our system, the right v0 move is a crude retention/attention
approximation, not a grand model of the viewer.

---

## Experiment 1 delta after reading Suspenser

Suspenser suggests one specific refinement to the current L3
experiment: split "tension" into at least two partially
independent signals.

### Signals to track

1. **Trajectory tension**
   - from recent emotional movement over time

2. **Structural tension**
   - from perceived narrowing or widening of resolution paths

### Minimal implementation

Add only:

1. per-situation `resolution_path_count`
2. per-node `option_effect`
3. one `structural_tension` feature in the weighted score
4. one rule for interpreting flatness differently when structural
   tension is high

Do **not** add:

- planning-based success enumeration
- large causal-plan models
- discourse ordering machinery beyond what traversal already does

### What this would clarify

- whether some apparent `tension_flatline` cases are actually
  sustained suspense rather than failure
- whether `tension_spike` can be explained as structural
  underpreparation
- whether `escalate` should sometimes mean "close options" rather
  than simply "choose a stronger node"

### Best use

Suspenser is probably not one of the first three things to
implement. But it is an excellent second-pass refinement once
the basic Façade/DODM scheduler exists and needs a richer model
of what "pressure increasing" really means.
