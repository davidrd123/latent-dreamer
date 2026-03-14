# Sentient Sketchbook — L1 Extraction

Source: Liapis, Yannakakis, Togelius, "Sentient Sketchbook:
Computer-Assisted Game Level Authoring" (FDG 2013)

Source file: `sources/markdown/SentientSketchbook/source.md`

Purpose: Extract the mixed-initiative design workflow model
for L1 world-building / authoring assistant.

---

## What Sentient Sketchbook actually is

A tool for designing strategy game levels. The human draws a
coarse map sketch; the tool:

1. **Tests playability constraints** in real time
2. **Evaluates quality metrics** (balance, pacing, safety)
3. **Generates alternative suggestions** via constrained novelty
   search
4. **Visualizes structural properties** (navmesh, choke points,
   safe areas)

Tested with 5 industry experts across 24 design sessions.

---

## The workflow model (the stealable thing)

```
Human draws sketch
  → tool validates constraints (feasibility)
  → tool evaluates fitness dimensions (quality)
  → tool generates 12 alternative suggestions
     - 6 from objective-based optimization
     - 6 from novelty search (intentionally different)
  → human inspects, accepts/rejects suggestions
  → cycle repeats
```

### Key properties of the workflow

1. **Continuous evaluation.** Fitness dimensions update with
   every user action, not just on request. The designer always
   knows the current state of the design.

2. **Visible metrics.** Fitness displayed as progress bars.
   Comparison between current map and suggestion shown with
   color-coded deltas (green = improved, red = declined).

3. **Suggestion diversity.** Half the suggestions optimize
   quality; half optimize **novelty** (maximum difference from
   current design). This prevents the tool from only offering
   minor variations.

4. **Rejection is cheap.** Browsing and dismissing suggestions
   costs nothing. The designer can stay focused on their own
   vision and only adopt suggestions when genuinely useful.

5. **Suggestions start from the current state.** Alternatives
   are mutations of the user's current sketch, not random
   generation from scratch. This keeps suggestions relevant.

---

## Fitness dimensions (evaluation criteria)

| Dimension | What it measures |
|-----------|-----------------|
| Resource safety | How close resources are to player bases |
| Safe area | How much controllable territory each player has |
| Exploration | How difficult each base is to find |
| Resource safety balance | Equal safety across players |
| Safe area balance | Equal territory across players |
| Exploration balance | Equal findability across players |

Each dimension is computed from the sketch structure using
pathfinding and flood-fill algorithms. These are **computable
from typed state** — no LLM judgment needed.

---

## What maps to L1

| Sentient Sketchbook concept | L1 equivalent | Notes |
|---------------------------|---------------|-------|
| Map sketch | World state (characters, places, tensions) | The artifact being authored |
| Playability constraints | Structural validity (connectivity, completeness) | Does the world hold together? |
| Fitness dimensions | ArtifactDeficiency metrics | Are there structural gaps? |
| Alternative suggestions | L1 operator proposals | Engine-generated enrichments |
| Novelty search suggestions | Divergent proposals | Intentionally different options |
| Visual fitness display | Deficiency dashboard | Author sees what's weak |
| Accept/reject cycle | Human curation gate | Author controls canon |

---

## What's genuinely new vs. doc 11's L1

### 1. Evaluation before proposal

Sentient Sketchbook evaluates the current design *continuously*,
not just when generating proposals. The author always knows
what's strong and what's weak. Doc 11's L1 runs detectors
to find deficiencies, which is similar — but the insight is
that **visible, continuous evaluation is the primary value**,
even before the proposal engine runs.

### 2. Novelty as a diversity term among deficiency-grounded candidates

In Sentient Sketchbook, half the suggestions optimize novelty
(maximum difference from current design). For L1, the
translation is **not** "generate free-standing divergent
proposals." Doc 11 is clear: only computable structural
deficiencies are schedulable. Novelty is useful as a
**ranking/diversity term among deficiency-grounded candidates**
— when multiple proposals address the same deficiency, prefer
the one most different from recently accepted material. The
`couple` and `concretize` operators from doc 11 are the right
scope: they respond to detected deficiencies, not to a
standalone novelty drive.

### 3. Feasibility before quality

Sentient Sketchbook hard-gates on feasibility (reachability,
base count) before evaluating quality. For L1, this means:
proposals that break world consistency are rejected before
scoring, not scored low. The three gates in doc 11 (validity,
structural utility, human keepability) match this pattern.

### 4. Suggestions as mutations of current state

Generated alternatives start from the current sketch, not from
scratch. For L1, this means operators should mutate the existing
world state, not generate new worlds. The input to `obstruct`
should be the current character's desire + current blockers, not
"imagine a character with obstacles."

---

## What to take

1. **The continuous evaluation + visible metrics model.** Before
   building a full proposal engine, build a deficiency dashboard
   that shows the author what's structurally weak in real time.

2. **Novelty as a ranking term, not a driver.** When multiple
   proposals address the same deficiency, prefer the one most
   different from recently accepted material. This prevents
   the proposal engine from converging on one pattern. But
   all proposals must still be grounded in a detected
   deficiency — novelty without structural justification is
   not a schedulable pressure (per doc 11).

3. **Mutation from current state, not generation from scratch.**
   Operators should take the world as-is and make targeted
   changes, not invent new material in isolation.

4. **The cheap rejection pattern.** Browsing and dismissing
   proposals must cost nothing. The author should never feel
   obligated to accept a suggestion.

What NOT to take:

- The game-map-specific metrics (tile types, flood-fill)
- The genetic algorithm implementation details
- Any assumption about real-time performance requirements
  (L1 is research-time, not live performance)
- The specific 5-expert study methodology
