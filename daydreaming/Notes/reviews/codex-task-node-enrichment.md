# Codex Task: Enrich Graffito Graph Nodes

## What

Add structured metadata fields to each of the 13 Graffito v0 nodes
per the frozen schema (doc 19). The information is already implicit
in the visual_descriptions — this task pulls it out into structured
fields.

## Why

The narration bridge needs structured scene metadata (characters,
objects, valences, tags) to build a narration-facing "scene gist"
instead of passing the raw T2V caption to Flash. The renderer
consumes visual_description; the narration bridge consumes the
structured fields. Same node, different consumers, different shapes.

## Schema reference

From `daydreaming/Notes/experiential-design/19-graffito-node-schema.md`:

```yaml
# These fields need to be added/verified for each node:
characters: [Tony]              # who is in this scene
objects: []                     # ALL CAPS iconic objects, e.g. [THE MAGIC SPRAY CAN]
valences:                       # intrinsic emotional character of the node (0.0-1.0)
  hope: 0.1
  threat: 0.5
  dread: 0.6
  joy: 0.0
  wonder: 0.1
tags: [running, overwhelm, street, crowd, legs]  # for retrieval + gist
```

## Input

The Graffito v0 dream graph fixture:
`scope-drd/content/daydreams/graffito/dream_graph.json`

13 nodes. Each currently has these fields:
```
id, dwell_s, mind, world, place, threshold, tags, indices, prompt
```

Note the actual field names:
- `prompt` (not `visual_description`) — the T2V captioning text
- `world.situation_ids` (not `situation`) — list of situation IDs
- `tags` and `indices` — already populated with retrieval metadata
- `mind` — contains goal_type, tension, energy, novelty, hold
- `place` — simple string (e.g., "street", "apartment")

Also read:
- `daydreaming/Notes/experiential-design/19-graffito-node-schema.md`
  (target schema with example)
- `daydreaming/Notes/experiential-design/18-graffito-vertical-slice.md`
  (character profiles, situation descriptions, scene breakdowns)
- The Graffito captioning spec for character/object conventions
  (ALL CAPS for iconic objects, character tier system)

## Rules

### Add these new fields to each node:

1. **name** — short human-readable label.
   e.g., "Feet pounding", "Forest of adult legs", "Cab near miss",
   "Monk painting". Used for display and narration gist.

2. **characters** — use the names from doc 18's character section:
   Tony, Monk, Grandma, Motherload, Elephant. Only include
   characters actually present/visible in the node.

3. **objects** — ALL CAPS for iconic objects per the captioning
   spec conventions: THE MAGIC SPRAY CAN, TONYS SKETCHBOOK,
   THE ORANGE DOOR, etc. Empty list if no iconic objects.

4. **valences** — five dimensions, each 0.0-1.0:
   - hope, threat, dread, joy, wonder
   - These are the *node's intrinsic* emotional character, not
     the Dreamer's state. A threatening street scene has high
     threat regardless of how the Dreamer arrived.
   - Use increments of 0.1. Don't overthink — rough is fine.

### Preserve all existing fields:

5. Do NOT modify: `id`, `dwell_s`, `mind`, `world`, `place`,
   `threshold`, `prompt`, `indices`. These are working retrieval
   and engine fields.

6. **tags** — already populated. You may add 1-2 tags if clearly
   missing, but do not remove or rewrite existing tags. The
   existing tags are used for retrieval matching.

## Output

The enriched graph fixture with all 13 nodes having characters,
objects, valences, tags, and name fields populated.

## Verification

After enrichment, every node should have:
- [ ] `name` — non-empty string
- [ ] `characters` — list (may be empty for no-character scenes)
- [ ] `objects` — list (may be empty)
- [ ] `valences` — dict with all five keys, values 0.0-1.0
- [ ] `tags` — list of 4-8 strings
