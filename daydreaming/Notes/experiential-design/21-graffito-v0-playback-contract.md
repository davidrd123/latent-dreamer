# Graffito v0 Playback Contract

Defines the stable seam between the authored Graffito slice fixtures and
the systems that consume them.

This doc exists because the slice now has more than one downstream
consumer:

- the **renderer path** needs a per-cycle packet that turns authored
  nodes plus Dreamer state into prompts
- the **narration path** needs the same per-cycle packet, plus the live
  Scope frame, to produce grounded inner voice

The graph fixture and trace fixture should not be interpreted ad hoc by
each consumer. They should be joined once into a shared playback packet.

## Scope

This contract is for **Graffito slice v0** only:

- scenes 3-4
- hand-authored graph fixture
- hand-authored trace fixture
- scripted trace-player path into the real-time Scope stage (doc 18
  Option B)

It does **not** define:

- the full Dreamer engine trace schema from `scope-drd`
- the renderer prompt itself
- the narration prompt itself
- the full Scope API adapter implementation
- the future full-graph traversal logic

Those are downstream consumers of this seam.

## Source Files

Current authored inputs live in this repo:

- Graph fixture:
  `daydreaming/fixtures/graffito_v0_scenes_3_4.yaml`
- Trace fixture:
  `daydreaming/fixtures/graffito_v0_trace.yaml`

The graph fixture is the canonical source for node content:

- `visual_description`
- `characters`
- `objects`
- `valences`
- `tags`
- `scene_ref`
- `situation_ids`
- `dwell_s`

The trace fixture is the canonical source for cycle-local state:

- `cycle`
- `node`
- `family`
- `tension`
- `energy`

## Why This Seam Exists

Doc 18 originally only needed `trace -> renderer -> Scope playback`.
Doc 20 adds a second consumer: `trace + graph + Scope frame ->
narration`.

That means the authored slice needs one explicit contract in between:

```text
graph fixture + trace fixture
    -> playback join
    -> normalized cycle packet
    -> consumer-specific projection
```

Without this seam:

- the renderer and narration bridge can drift in what they think a cycle
  contains
- `family: null` semantics become inconsistent
- visit/revisit logic gets reimplemented in multiple places
- the graph fixture gets treated as renderer-only, when it is now also a
  narration-grounding source

## Author-Facing Fixture Semantics

### Graph fixture

The graph fixture is authored in a human-readable, scene-grounded form.
It uses uppercase variant-family labels on nodes where relevant:

- `variant_family: REVERSAL`

This is an authoring convention, not yet a runtime packet.

### Trace fixture

The Graffito trace fixture is also author-facing. It uses:

- uppercase family labels for readability (`REVERSAL`, `ROVING`)
- `null` when no explicit family modulation is active

For v0, `family: null` means:

- no family-specific capstone sentence
- no family-specific visual modulation beyond what the node itself
  already contains
- consumers should fall back to neutral/default behavior, not error

The trace does **not** currently specify `dwell_s`. For v0, dwell comes
from the graph node.

## Join Rules

The playback join reads the graph fixture and trace fixture and produces
one normalized packet per cycle.

### Algorithm

1. Load the graph fixture and index nodes by `id`.
2. Load the trace fixture in cycle order.
3. For each trace row:
   - look up the node by `trace.node`
   - fail hard if the node does not exist
   - copy cycle-local fields from the trace
   - copy node metadata from the graph fixture
   - derive revisit fields (`visit_count`, `last_seen_cycle`)
4. Emit a normalized packet for the cycle.

### Derived fields

These are not authored directly in either fixture but are produced by
the join:

- `visit_count`
  Count of how many times this node has appeared up to and including the
  current cycle. First appearance = `1`.
- `last_seen_cycle`
  Previous cycle where this node appeared, or `null` on first visit.
- `primary_situation_id`
  First entry of `situation_ids`.
- `goal_type`
  Lowercase form of `family` for engine-compatible consumers.
  Examples:
  - `REVERSAL -> reversal`
  - `ROVING -> roving`
  - `null -> null`

The source `family` value should still be preserved in the normalized
packet for author-facing consumers and debugging.

### Dwell precedence

For v0:

1. `node.dwell_s` from the graph fixture is authoritative
2. if a future trace adds a cycle-specific override, that should be
   modeled explicitly as `dwell_override_s`
3. do not silently mutate dwell in consumers

## Normalized Cycle Packet

The playback join emits this logical packet:

```yaml
cycle: 5
graph_id: graffito_v0_scenes_3_4

node_id: g_s1_n03v_legs_overwhelm
node_name: Forest of legs closes in
scene_ref: "Scene 3, Shot 7"

family: REVERSAL
goal_type: reversal
tension: 0.8
energy: 0.8

situation_ids: [s1_street_overwhelm]
primary_situation_id: s1_street_overwhelm

dwell_s: 5.5
visit_count: 1
last_seen_cycle: null

variant_of: g_s1_n03_forest_of_legs
variant_family: REVERSAL

visual_description: >
  Graffito Mixed-Media Stop-Motion - ...
characters: [Tony]
objects: []
valences:
  hope: 0.0
  threat: 0.7
  dread: 0.8
  joy: 0.0
  wonder: 0.0
tags: [reversal, overload, crowd, claustrophobic, legs, threat]
```

### Required fields

- `cycle`
- `graph_id`
- `node_id`
- `node_name`
- `scene_ref`
- `family`
- `goal_type`
- `tension`
- `energy`
- `situation_ids`
- `primary_situation_id`
- `dwell_s`
- `visit_count`
- `last_seen_cycle`
- `visual_description`
- `characters`
- `objects`
- `valences`
- `tags`

### Optional fields

- `variant_of`
- `variant_family`

## Runtime Attachments

Not every consumer needs the same runtime material.

The **normalized cycle packet** is deterministic and comes entirely from
the two fixtures plus derived join fields.

Some consumers add runtime attachments:

- Narration bridge:
  current Scope frame (`image/png`)
- Renderer:
  previous cycle prompt, if doing continuity modulation
- Future music layer:
  previous N cycles of emotional state

These attachments are **not** part of the core playback packet. They are
consumer-specific enrichments layered on top.

## Consumer Projections

### 1. Renderer projection

Input:

- normalized cycle packet
- optional previous rendered prompt / continuity context

Consumes:

- `visual_description`
- `family` or `goal_type`
- `tension`
- `energy`
- `visit_count`
- `last_seen_cycle`
- `characters`
- `objects`

Produces:

- `visual_prompt`
- `transition_type`
- `dwell_time`
- optional `negative_prompt`

The renderer should not parse the raw graph fixture or raw trace
directly. It should consume the joined packet.

### 2. Narration projection

Input:

- normalized cycle packet
- current Scope frame
- optional engine template narration

Consumes:

- `visual_description`
- `characters`
- `objects`
- `valences`
- `family` or `goal_type`
- `tension`
- `energy`
- `primary_situation_id`
- runtime frame

Produces the WebSocket payload defined in doc 20:

```yaml
cycle: 5
node_id: g_s1_n03v_legs_overwhelm
family: REVERSAL
tension: 0.8
energy: 0.8
situation: s1_street_overwhelm
dwell_s: 5.5
narration: "..."
```

The narration layer should use the joined packet as its text grounding
source and add the live frame as the perceptual grounding source.

### 3. Real-time stage projection

Input:

- renderer output
- normalized cycle packet

Consumes:

- `cycle`
- `node_id`
- `scene_ref`
- `dwell_s`
- renderer prompt fields
- `transition_type`
- optional renderer parameter updates

Produces:

- the sequence of Scope API calls needed to play the cycle:
  - `PUT /api/v1/realtime/prompt`
  - optional `POST /api/v1/realtime/parameters`
  - optional `POST /api/v1/realtime/soft-cut` or `hard-cut`
  - dwell-based wait before the next cycle

This projection is where timing, transition policy, and renderer
parameter application get resolved. It should not leak backward into the
core playback packet.

## Failure and Degradation Rules

### Missing node id

If the trace references a node that is absent from the graph fixture:

- fail hard
- emit the missing `node_id`
- do not attempt fallback traversal

This is an authoring error.

### `family: null`

Consumers should treat this as:

- `goal_type: null`
- neutral/default behavior
- no family-specific capstone or color logic

Consumers may render this as `drift` in the UI, but that is a
presentation fallback, not a change to the authored trace.

### Missing runtime frame

Narration should degrade as:

1. packet + frame
2. packet only
3. engine template narration + family/tension

Renderer should not depend on the runtime frame at all in v0.

## Promotion Path to `scope-drd`

The current Graffito slice is authored here because the spec and the
creative reasoning live here first.

When promoted into `scope-drd`, keep the same seam:

1. authored graph becomes `content/daydreams/graffito/...`
2. authored trace becomes a trace-player input or fixture in
   `scope-drd`
3. playback join emits the same normalized packet
4. narration and renderer continue to consume that packet

The delivery mechanism may change. The contract should not.

## Immediate Use

For the current Graffito v0 state, this doc means:

- [graffito_v0_scenes_3_4.yaml](../../fixtures/graffito_v0_scenes_3_4.yaml)
  is no longer renderer-only authoring material
- [graffito_v0_trace.yaml](../../fixtures/graffito_v0_trace.yaml) is
  sufficient to drive v0 playback
- the next implementation seam is **not** more graph authoring or more
  trace tuning
- the next implementation seam is the playback join / trace-player that
  produces normalized cycle packets for both narration and rendering

## Open Questions

- Should the joined packet preserve both `family` and `goal_type`, or is
  one enough once the adapter exists?
- Should `visit_count` start at `0` or `1` on first visit? This doc
  assumes `1`.
- Should the real-time stage projection keep a short history of prior
  prompts/parameter changes for debugging and replay?
- When the renderer gets revisit context, should it consume
  `last_seen_cycle` only, or also a short trailing packet history?
