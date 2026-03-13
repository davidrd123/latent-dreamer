# Cognitive Trace Visualizer — Spec

**Date:** 2026-03-12 (revised after Codex 1/2/Opus review)
**Input:** Any kernel trace JSON (autonomous, semi-unscripted, or scripted)
**Output:** Standalone HTML/JS page, no server, reads trace JSON via drag-drop
**Build:** `bb dream-viz` → `out/puppet_knows_autonomous_cognitive.html`

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

2. **Two lanes.** Keyed off `selected_goal.planning_type` ("real" vs
   "imaginary"). When the selected goal's planning type switches, the
   timeline path crosses from reality to daydreaming. This is the stable
   field in the trace — not an abstract global mode.

3. **Emotional state is color/size, not numbers.** Situations are rendered as
   regions whose visual properties (warmth, size, brightness, edge softness)
   map to their metrics. When dread rises, you see it darken. When hope
   jumps after rationalization, you see it warm.

4. **Goal competition is a race.** Candidate goals are bars or lines whose
   heights change each cycle. You watch rationalization climb while rehearsal
   stays flat, then the moment one overtakes the other.

5. **Branch events are visible.** When the kernel sprouts a branch, a
   marker appears on the timeline with the family type and fact IDs. When
   the adapter propagates branch facts into situation state, an annotation
   connects the branch event to the situation landscape. (v1 shows per-cycle
   branch events, not a full persistent context tree — see panel D.)

6. **The thought stream narrates.** A template-driven running text that
   translates the cycle into readable cognition. No LLM — the trace has
   enough structured signals for deterministic narration: selected goal,
   branch mutations, adapter deltas, emotional shifts.

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

**Data source:** `cycle.top_candidates` — typically 15-20 candidates per
cycle in autonomous traces (confirmed), each with goal_type, strength,
situation_id, reasons. Semi-unscripted traces may have fewer candidates.

**Rendering:** The visualizer aggregates candidates by goal_type, taking the
max strength per type per cycle. This produces 5-7 goal-type lines across
cycles. Displayed as a bump chart (lines) or grouped bar chart. Each goal
type has a consistent color:
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

## D. Branch Events (v1) / Branch Tree (v2)

**Data source:** `cycle.branch_events`, `cycle.mutations`,
`cycle.sprouted_contexts`.

**Canonical trace shape:** `branch_events` normalizes family-specific branch
information into:
- `family`
- `source_context`
- `target_context`
- `fact_ids`
- `fact_types`
- optional `retracted_fact_ids` / `retracted_fact_types`

Family-specific `selection` keys still exist for detailed inspection, but the
visualizer should treat `branch_events` as the primary branch API.

**v1 Rendering:** Per-cycle branch event list. For each cycle with mutations
or sprouted contexts, show:
- Family type (reversal/rationalization/roving) with color
- Source → target context IDs
- Fact IDs asserted or retracted
- Whether the adapter fired (adapter_policy != no_branch_context)

If no branching in the current cycle, panel shows "no branch activity" or
collapses.

**v2 (future):** Full context tree with ancestry. Requires a normalized
`branch_events` / `context_edges` export in the trace schema (not yet
implemented). The tree would show the full sprouting history across cycles
with fact-flow arrows back to the situation landscape.

If no branching has happened at all yet, panel is collapsed.

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

Generation rules (deterministic templates, no LLM):
- Situation name from `selected_goal.situation_id`
- Goal type in plain English ("imagining an alternative past" for reversal,
  "escaping to a pleasant memory" for roving, "reinterpreting the failure"
  for rationalization)
- Metric changes when adapter fires (delta from previous cycle's situations)
- Branch sprouting noted when `branch_events` is non-empty
- Emotional shifts when present (e.g., "Dread drops 0.82 → 0.55. Hope rises.")
  keyed off `emotion_shifts` / `emotional_state`
- Node selection with edge_kind annotation
- Fatigue/escape noted when candidate reasons include `:fatigue_escape` or
  `:mission_escape`

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

Input: drag-drop or embedded JSON matching the kernel's reporter schema.

### Cycle object (all fields optional except `cycle`)

```json
{
  "cycle": 1,
  "timestamp": "2026-03-12T12:30:01Z",
  "selected_goal": {
    "id": "g-5",
    "goal_type": "rationalization",
    "strength": 0.519,
    "planning_type": "imaginary",
    "situation_id": "s1_seeing_through"
  },
  "top_candidates": [
    {
      "id": "g-5",
      "goal_type": "rationalization",
      "strength": 0.519,
      "planning_type": "imaginary",
      "situation_id": "s1_seeing_through",
      "reasons": ["failed_pressure", "negative_affect"]
    }
  ],
  "activations": [
    {
      "goal_id": "g-5",
      "goal_type": "rationalization",
      "trigger_context_id": "cx-1",
      "failed_goal_id": null,
      "emotion_id": "...",
      "emotion_strength": 0.519,
      "activation_policy": "autonomous_adapter",
      "activation_reasons": ["failed_pressure", "negative_affect"]
    }
  ],
  "mutations": [
    {
      "family": "reversal",
      "source-context": "cx-2",
      "target-context": "cx-38",
      "input-facts": [{"type": "counterfactual", "id": "..."}],
      "retracted-facts": [{"type": "assumption", "id": "..."}]
    }
  ],
  "branch_events": [
    {
      "family": "reversal",
      "source_context": "cx-2",
      "target_context": "cx-38",
      "fact_ids": ["performance_is_admitted", "s4_the_ring"],
      "fact_types": ["counterfactual", "situation"],
      "retracted_fact_ids": ["performance_stays_hidden"],
      "retracted_fact_types": ["assumption"]
    }
  ],
  "situations": {
    "s1_seeing_through": {
      "activation": 0.55, "ripeness": 0.72, "hope": 0.19, "threat": 0.47,
      "anger": 0.24, "grief": 0.0, "waiting": 0.0, "visits": 3,
      "description": "...", "place": "checkpoint", "indices": ["..."],
      "inferred": true, "external": false, "directed-target": "the_set"
    }
  },
  "active_indices": ["perception", "awareness", "seam"],
  "retrieved": [
    {
      "node_id": "n02_corridor_repeat",
      "retrieval_score": 5.0,
      "marks": 3,
      "threshold": 2,
      "overlap": ["perception", "awareness"]
    }
  ],
  "chosen_node_id": "n02_corridor_repeat",
  "sprouted_contexts": ["cx-38"],
  "selection": {
    "policy": "highest_score",
    "score": 8.1,
    "reasons": ["retrieval:5", "goal_match", "situation_match"],
    "goal_family": "reversal",
    "edge_kind": "counterfactual_bridge"
  },
  "emotion_shifts": [
    {
      "emotion_id": "e_fixture_room_dread",
      "from_strength": 0.82,
      "to_strength": 0.55,
      "delta": -0.27,
      "valence": "negative",
      "role": "trigger"
    }
  ],
  "emotional_state": [
    {
      "emotion_id": "rf_zone_mercy-hope",
      "strength": 0.27,
      "valence": "positive",
      "affect": "hope",
      "situation_id": "s5_the_guide",
      "role": "reframe"
    }
  ],
  "feedback_applied": null,
  "serendipity_bias": null
}
```

### Selection keys by family (present only when that family fires)

**Reversal:** `reversal_branch_context`, `reversal_branch_contexts`,
`reversal_branch_count`, `reversal_source_context`, `reversal_target_goal`,
`reversal_target_policy`, `reversal_leaf_goal`, `reversal_leaf_strength`,
`reversal_leaf_depth`, `reversal_leaf_emotion_pressure`,
`reversal_leaf_policy`, `reversal_leaf_reasons`,
`reversal_leaf_retracted_facts`, `reversal_counterfactual_policy`,
`reversal_counterfactual_source`, `reversal_counterfactual_goal`,
`reversal_counterfactual_reasons`, `reversal_counterfactual_fact_ids`

**Rationalization:** `rationalization_branch_context`,
`rationalization_frame_id`, `rationalization_frame_goal`,
`rationalization_reframe_fact_ids`, `rationalization_selection_policy`,
`rationalization_frame_reasons`, `rationalization_trigger_emotion_id`,
`rationalization_trigger_emotion_before`, `rationalization_trigger_emotion_after`,
`rationalization_hope_emotion_id`

**Roving:** `roving_branch_context`, `roving_seed_episode`,
`roving_reminded_episodes`, `roving_active_indices`, `roving_selection_policy`

**Adapter:** `adapter_policy`, `adapter_branch_context`,
`adapter_visible_fact_ids`, `adapter_selected_situation`,
`adapter_active_indices`

### Situation metrics by trace type

| Metric | Scripted | Semi-unscripted | Autonomous |
|--------|----------|-----------------|------------|
| activation | yes | yes | yes |
| ripeness | yes | yes | yes |
| hope | yes | yes | yes |
| threat | yes | yes | yes |
| anger | yes | yes | yes |
| grief | — | — | yes |
| waiting | — | — | yes |
| visits | — | — | yes |
| description | — | — | yes |
| place | — | — | yes |
| indices | — | — | yes |
| inferred | — | — | yes |
| external | — | — | yes |
| directed-target | — | — | yes |

### The visualizer must handle

- 2-cycle benchmarks (semi-unscripted) — show both cycles
- 12-cycle autonomous traces — the primary use case
- Missing fields (graceful degradation, not errors)
- Family-specific selection keys (detect by presence, not by trace type)
- `top_candidates` count varies: ~20 in autonomous, fewer in benchmarks

---

## Implementation Notes

- Standalone HTML + JS + CSS, no framework, no build step
- Vanilla JS or lightweight lib (d3.js for the charts/animation is fine)
- Input: drag-drop JSON onto page, or embedded JSON in generated HTML
- `bb dream-viz` generates `out/puppet_knows_autonomous_cognitive.html`
  with the trace JSON embedded (no separate file needed for default case)
- Drag-drop allows loading any other trace JSON for comparison
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

## Versioning

### v1 (build now)

1. Situation Landscape (A) + Goal Race (B) + Timeline (C) + Thought Stream (E)
2. Branch Events panel (D) — per-cycle event list, not full tree
3. Play/pause, step, speed controls
4. Drag-drop JSON input + embedded default trace
5. Dark theme, CSS transitions between cycle states

### v2 (after trace schema normalization)

1. Full Branch Tree with context ancestry and fact-flow arrows
2. Requires normalized `branch_events` / `context_edges` in trace export
3. Click-to-expand situation detail with metric history sparklines
4. Comparison mode (load two traces side by side)
5. Ambient animation (situation circles breathing, goal bars easing)
