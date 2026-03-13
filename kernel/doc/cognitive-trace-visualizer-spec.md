# Cognitive Trace Visualizer — Spec

**Date:** 2026-03-12
**Input:** Any kernel trace JSON (autonomous, semi-unscripted, or scripted)
**Output:** Standalone HTML/JS page, no server, reads trace JSON directly

---

## What This Is

An animated visualization of the kernel's cognitive process — the Mueller
DAYDREAMER "watching a mind think" experience. Not a dashboard or data table.
A temporal, spatial rendering of goal competition, emotional drift, mode
switching, and branch sprouting as the kernel moves through a dream.

The existing HTML reports are post-hoc summaries. This is the trace *as
experience* — you watch it unfold.

---

## Design Principles

1. **Time is primary.** The visualization advances cycle by cycle (auto-play
   or step). Everything animates between states. You see the system *change*,
   not just snapshots.

2. **Two lanes.** Mueller's two modes — reality processing and daydreaming —
   are spatially separated. When the kernel switches from reality to
   imaginary planning (REVERSAL, ROVING, RATIONALIZATION), you see it cross
   the boundary.

3. **Emotional state is color/size, not numbers.** Situations are rendered as
   regions whose visual properties (warmth, size, brightness, edge softness)
   map to their metrics. When dread rises, you see it darken. When hope
   jumps after rationalization, you see it warm.

4. **Goal competition is a race.** Candidate goals are bars or lines whose
   heights change each cycle. You watch rationalization climb while rehearsal
   stays flat, then the moment one overtakes the other.

5. **Branch sprouting is spatial.** When the kernel sprouts cx-16 for a
   reversal branch, a new region appears connected to its parent context.
   Facts flow from the branch back into the main trace. The counterfactual
   bridge is *visible*.

6. **The thought stream narrates.** A running text that translates the cycle
   into readable cognition: "Failed at rehearsal. Dread is high. Imagining
   an alternative past. Performance is admitted. The ring wakes up."

---

## Layout (Single Page, Five Regions)

```
+-------------------------------------------------------------+
|  [controls: play/pause, step, speed, trace selector]        |
+-------------------------------------------------------------+
|                                                              |
|  A. SITUATION LANDSCAPE          |  B. GOAL RACE            |
|  (emotional state of all         |  (candidate strengths    |
|   situations as a shifting       |   over time, selected    |
|   field)                         |   goal highlighted)      |
|                                  |                          |
+----------------------------------+--------------------------+
|                                                              |
|  C. TIMELINE + MODE LANES                                    |
|  (horizontal timeline, reality lane above, daydream below,   |
|   current cycle marker, branch sprouting shown as forks)     |
|                                                              |
+-------------------------------------------------------------+
|                                                              |
|  D. BRANCH TREE               |  E. THOUGHT STREAM          |
|  (context ancestry,           |  (natural-language narration |
|   sprouted branches,          |   of current cycle's         |
|   fact flow arrows)           |   cognitive process)         |
|                               |                              |
+-------------------------------+------------------------------+
```

---

## A. Situation Landscape

**Data source:** `cycle.situations` — 4-5 situations, each with activation,
ripeness, hope, threat, anger, grief, waiting.

**Rendering:** Each situation is a soft circular region. Size = activation.
Color maps to dominant emotional valence:
- High threat + anger → dark red/crimson
- High hope → warm gold
- High grief → deep blue-gray
- High waiting → muted amber
- Neutral → cool gray

Ripeness modulates edge sharpness (ripe = defined, unripe = diffuse).

Situations are positioned in a stable layout (don't jump around). When a
situation's activation rises, it grows and brightens. When it drops, it
shrinks and fades.

**Key moment:** When the adapter fires and s4_the_ring jumps from 0.15 →
0.49, you see a small dim circle suddenly expand and warm up. That's the
counterfactual bridge made visible.

**Label:** Each situation shows its ID (abbreviated) and the dominant metric
value. On hover: full metric breakdown.

---

## B. Goal Race

**Data source:** `cycle.top_candidates` — 15-20 candidates per cycle, each
with goal_type, strength, situation_id, reasons.

**Rendering:** Horizontal bar chart or bump chart showing the top 6-8
candidates' strengths across cycles. Each goal type has a consistent color:
- reversal: indigo
- rationalization: teal
- roving: amber
- rehearsal: sage green
- revenge: crimson
- repercussions: dark orange
- recovery: light blue

The selected goal's bar is highlighted (bright, maybe pulsing). Losers are
dimmed.

**Key moment:** Cycle 10 (Puppet Knows semi-unscripted) — reversal at 0.74
overtakes rehearsal at 0.42. You see the indigo bar surge past the green one.
That's the kernel choosing to imagine an alternative past instead of
practicing for the future.

**Annotations:** When a goal wins, a small tag shows the selection policy
("highest_strength", "counterfactual_bridge", etc.).

---

## C. Timeline + Mode Lanes

**Data source:** `cycle.selected_goal.planning_type` ("real" vs "imaginary"),
`cycle.sprouted_contexts`, `cycle.mutations`.

**Rendering:** Horizontal timeline with cycle markers. Two lanes:
- Upper: reality (real planning type)
- Lower: daydreaming (imaginary planning type)

Each cycle is a node on the timeline, positioned in its lane. When the kernel
switches from reality to daydreaming, the path crosses the boundary.

Branch sprouting: when `sprouted_contexts` is non-empty, a fork appears from
the current node. The branch hangs below the timeline in the daydream lane.
Lines connect the branch back to the main timeline when the adapter fires
(fact flow).

**Key moment:** The fork at cycle 8 (REVERSAL activates), branch hanging
below through cycles 8-9, then the arrow flowing back up at cycle 10 as
counterfactual facts propagate into the main trace.

Current cycle is highlighted. Past cycles are visible but dimmed. Future
cycles hidden until reached.

---

## D. Branch Tree

**Data source:** `cycle.mutations`, `cycle.selection` (branch context IDs,
fact IDs), `cycle.sprouted_contexts`.

**Rendering:** Small tree diagram showing context ancestry:
```
cx-1 (root)
├── cx-2 (reality lookahead)
│   ├── cx-10 (reversal: reverse-leafs)
│   │   └── facts: performance_is_admitted, s4_the_ring
│   └── cx-16 (reversal: alternative-past)
└── cx-3 (roving side-channel)
    └── facts: pleasant-episode-seed
```

Nodes represent contexts. Edges show parent-child. Fact labels on leaf nodes
show what the branch produced.

When the adapter reads branch facts and propagates them into situation state,
an animated arrow flows from the branch fact to the situation landscape.

If no branching has happened yet, this panel is minimal/collapsed.

---

## E. Thought Stream

**Data source:** All cycle fields, synthesized into natural language.

**Rendering:** Scrolling text log, newest at bottom. Each cycle gets 1-3
lines:

```
Cycle 1  Seeing through the set. Repercussions dominate. [n01_stored_scenery]
Cycle 2  Still dwelling on the apparatus. Anger rising.
Cycle 3  Revenge stirs — directed at the set. [n03_apparatus_dread]
...
Cycle 8  Rehearsal failed. Dread peaks. REVERSAL activates — imagining
         what if performance were admitted?
Cycle 9  Revenge wins. Still in reality, but reversal is climbing.
Cycle 10 REVERSAL takes over. Counterfactual: performance IS admitted.
         s4_the_ring wakes up (0.15 → 0.49). The ring arrives.
         [n10_honest_ring via counterfactual_bridge]
```

Generation rules:
- Situation name from `selected_goal.situation_id`
- Goal type in plain English ("imagining an alternative past" for reversal,
  "escaping to a pleasant memory" for roving, "reinterpreting the failure"
  for rationalization)
- Metric changes when adapter fires (delta from previous cycle)
- Branch sprouting noted when `mutations` is non-empty
- Node selection with edge_kind annotation

---

## Interaction

- **Play/Pause** — auto-advance through cycles (default: 2s per cycle)
- **Step** — manual forward/back one cycle
- **Speed** — 0.5x, 1x, 2x, 4x
- **Trace selector** — dropdown to load different trace JSONs
- **Hover** — any element shows full data tooltip
- **Click situation** — expands to show full metric breakdown + history chart
- **Click goal bar** — shows activation reasons and trigger context

---

## Data Contract

Input: any file matching the kernel's reporter JSON schema:

```json
{
  "cycles": [
    {
      "cycle": 1,
      "selected_goal": { "goal_type": "...", "strength": 0.0, "situation_id": "...", "planning_type": "..." },
      "top_candidates": [ { "goal_type": "...", "strength": 0.0, ... }, ... ],
      "activations": [ ... ],
      "mutations": [ ... ],
      "situations": { "situation_id": { "activation": 0.0, "hope": 0.0, ... }, ... },
      "active_indices": [ "..." ],
      "retrieved": [ { "node_id": "...", "retrieval_score": 0, "overlap": [...] } ],
      "chosen_node_id": "...",
      "sprouted_contexts": [ "..." ],
      "selection": { "policy": "...", ... }
    }
  ],
  "started_at": "...",
  "seed": "...",
  ...
}
```

The visualizer must handle:
- 2-cycle benchmarks (Puppet Knows semi-unscripted) — show both cycles
- 12-cycle autonomous traces — the primary use case
- Missing fields (graceful degradation, not errors)
- Autonomous-specific fields (visits, grief, waiting, description)

---

## Implementation Notes

- Standalone HTML + JS + CSS, no framework, no build step
- Vanilla JS or lightweight lib (d3.js for the charts/animation is fine)
- Reads JSON via file input or URL parameter
- All animation via requestAnimationFrame or CSS transitions
- Dark background (the kernel is dreaming, not presenting a spreadsheet)
- Typographic: monospace for data, serif for thought stream
- Target: works in Chrome/Safari, doesn't need to be responsive

---

## Palette

Dark theme evoking a dreaming mind:
- Background: near-black (#0a0a0f) with subtle radial gradient
- Situation regions: translucent colored circles on dark field
- Goal bars: saturated colors against dark
- Timeline: thin white line, cycle markers as dots
- Branch tree: dim gray lines, brighter for active branches
- Thought stream: warm off-white text on dark
- Accents: gold for hope, crimson for threat, indigo for reversal,
  teal for rationalization, amber for roving

---

## Priority Order

1. Situation Landscape + Goal Race + Timeline (the core experience)
2. Thought Stream (makes it legible without reading numbers)
3. Branch Tree (shows the "what if" structure)
4. Interactions (hover, click, speed control)
5. Polish (transitions, particle effects, ambient animation)
