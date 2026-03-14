# Graffito Dream Graph Node Schema (v0)

Frozen schema for the Graffito vertical slice. Defines what a dream
graph node contains, what format it takes, and what each consumer
needs from it.

## File Format

One YAML file per slice graph. All nodes in one file.

```yaml
graph_id: graffito_v0_scenes_3_4
situations:
  - s1_street_overwhelm
  - s2_grandmas_apartment

nodes:
  - id: ...
    # ... node fields

edges: []  # second pass after nodes are authored
```

## Node Fields

```yaml
# === Identity ===
id: g_s1_n03_forest_of_legs       # unique, prefixed by situation
name: Forest of adult legs         # human-readable
scene_ref: "Scene 3, Shot 7"      # traceability to shots.md
variant_of: null                   # base node ID if this is a variant
variant_family: null               # REVERSAL, REVENGE, etc. (null for base)

# === Situation ===
situation_ids: [s1_street_overwhelm]  # list — some nodes straddle

# === Duration ===
dwell_s: 5.5                       # how long this moment should last

# === Visual (renderer input) ===
# 75-150 words. Written in Graffito captioning voice:
#   - Present tense, third person
#   - Material vocabulary (PAINTED PAPER CUTOUTS, THICK IMPASTO PAINT, etc.)
#   - Camera/shot/lighting embedded in prose
#   - ALL CAPS for iconic objects (THE MAGIC SPRAY CAN, etc.)
#   - Prepend "Graffito Mixed-Media Stop-Motion — "
visual_description: >
  Graffito Mixed-Media Stop-Motion — [description here]

characters: [Tony]                 # A/B-Tier characters present
objects: []                        # iconic objects (ALL CAPS names)

# === Dreamer (v1 — author now, consumed later) ===
valences:                          # node's intrinsic emotional character
  hope: 0.1                       #   (0.0 - 1.0)
  threat: 0.5
  dread: 0.6
  joy: 0.0
  wonder: 0.1
tags: [running, overwhelm, street, crowd, legs, anonymous_figures]
```

## What Each Consumer Reads

| Field | v0 trace player | v0 renderer | v1 Dreamer/adapter |
|---|---|---|---|
| id | yes (lookup) | no | yes (retrieval) |
| name | no | no | no |
| scene_ref | no | no | no |
| variant_of | no | no | yes (variant logic) |
| variant_family | no | no | yes (family matching) |
| situation_ids | no | no | yes (goal competition) |
| dwell_s | yes (timing) | yes (output) | maybe |
| visual_description | no | yes (primary input) | no |
| characters | no | yes (consistency rubric) | no |
| objects | no | yes (ALL CAPS treatment) | no |
| valences | no | no | yes (emotional matching) |
| tags | no | no | yes (index retrieval) |

## What Is NOT on the Node

These are properties of the Dreamer's state at traversal time, not
of the node. They live in the trace, not the graph:

- `goal_type` — which family is active (REVERSAL, ROVING, etc.)
- `tension` — current emotional pressure
- `energy` — current activity level
- `emotion` — current dominant emotion
- `visit_count` — how many times this node has been visited
- `last_seen_cycle` — when the node was last visited

The renderer receives these from the trace entry alongside the
node's visual_description. The same node visited via REVERSAL
feels different from the same node visited via ROVING — that
difference comes from the trace, not from the node.

## Edges (Second Pass)

Edges define which nodes can follow which. They live at graph
level, not inside nodes:

```yaml
edges:
  - from: g_s1_n03_forest_of_legs
    to: g_s1_n04_eyes_searching
  - from: g_s1_n03_forest_of_legs
    to: g_s1_n05_cab_near_miss
  - from: g_s1_n03_forest_of_legs
    to: g_s2_n01_camera_enters_window
```

Flat list, no type annotations. The Dreamer decides why a
transition happens based on family activation, not edge labels.
Author nodes first, edges second.

## Naming Conventions

- Node IDs: `g_{situation}_{number}_{short_name}`
  - `g_s1_n01_feet_pounding`
  - `g_s2_n03_dance_together`
- Variant IDs: `g_{situation}_{base_number}v_{variant_name}`
  - `g_s1_n05v_cab_overwhelm` (REVERSAL variant of n05)
- Situation IDs: `s{number}_{short_name}`
  - `s1_street_overwhelm`
  - `s2_grandmas_apartment`

## Authoring Voice Guide

Node descriptions are written in the Graffito captioning voice.
Reference: `captioning_sysprompt_graffito_video_v3.md`

Key rules:
- Prepend "Graffito Mixed-Media Stop-Motion — " to every description
- Present tense, third person
- Material vocabulary: PAINTED PAPER CUTOUTS, PHOTOGRAPHIC CUTOUTS,
  THICK IMPASTO PAINT, PAINTED CARDBOARD, CRUMPLED PAPER, VISIBLE
  BRUSHSTROKES, RAW TORN EDGES
- Motion vocabulary: jerky stop-motion steps, deliberate turn,
  segmented quality, articulated paper arms
- ALL CAPS for iconic objects: THE MAGIC SPRAY CAN, TONYS
  SKETCHBOOK, THE ORANGE DOOR, THE STUFFED ELEPHANT
- Camera/shot/lighting embedded in the prose, not separate fields
- Character first mention: describe then name. Subsequent: name only.
- 75-150 words per description

Quality gate: "would I want to watch 5-8 seconds of this?"
