# Bridge Input Assembly: Spec

## Problem

The narration bridge currently passes Flash:
- Raw node ID (`g_s1_n03v_legs_overwhelm`)
- Raw situation ID (`s1_street_overwhelm`)
- Full T2V captioning-voice description (invites paraphrase)
- Family + tension (no energy)
- No trajectory context (visit count, previous node, shift)
- No structured scene metadata (characters, objects, valences)

This produces generic mood text or prompt paraphrasing. Flash
needs narration-shaped input, not renderer-shaped input.

## Target input shape

```
Scene: Feet Pounding (street overwhelm)
- boy running between adult legs
- crowd pressure, no clear exit
- dread 0.7, hope 0.2

Trajectory:
- visit 3 to this node
- previous: camera enters window (grandmas apartment)
- shift: grandmas apartment → street overwhelm
- return: yes

State:
- family: reversal
- tension: 0.82
- energy: 0.46

[optional frame details if screenshot present]

Write 1-3 sentences of inner monologue.
```

## Implementation

### 1. Enrich graph loader

`load_graph_prompts()` → `load_graph_nodes()`

Currently loads only prompt/visual_description as a flat string.
Change to load full node records:

```python
def load_graph_nodes(graph_path: Path | None) -> dict[str, dict]:
    """Load full node records from a dream graph fixture."""
    # Returns dict indexed by node_id
    # Each value is the full node dict with whatever fields exist
```

The bridge should be schema-agnostic — different graphs have
different fields. Known field names across fixtures:

| Field | Graffito | Puppet Knows | Used by gist? |
|---|---|---|---|
| `id` | yes | yes | always (key) |
| `name` | after enrichment | no | yes, preferred label |
| `prompt` | yes | no | fallback only |
| `visual_description` | no (uses `prompt`) | no | fallback only |
| `tags` | yes | yes | yes |
| `indices` | yes | yes | no (retrieval only) |
| `place` | yes | yes | yes |
| `characters` | after enrichment | no | yes |
| `objects` | after enrichment | no | yes |
| `valences` | after enrichment | no | yes |
| `mind` | yes | yes | no (Dreamer state) |
| `world` | yes | yes | situation source |

The gist compiler uses what's available and degrades gracefully.

### 2. Humanize IDs

```python
import re

def humanize_id(raw_id: str) -> str:
    """Humanize a raw node or situation ID for display.

    g_s1_n03_forest_of_legs   → forest of legs
    g_s1_n03v_legs_overwhelm  → legs overwhelm
    s1_street_overwhelm       → street overwhelm
    n01_notice_seams          → notice seams
    """
    # Strip common prefixes: g_sN_, nNN_, sN_, nNNv_
    label = re.sub(r'^(g_)?s\d+_', '', raw_id)
    label = re.sub(r'^n\d+v?_', '', label)
    return label.replace('_', ' ')
```

Prefer node `name` field when present. Fall back to humanized ID
when not. Flash should never see raw IDs.

### 3. Scene gist compiler

```python
def compile_scene_gist(node: dict, situation: str) -> str:
    """Build a narration-facing scene summary from structured fields.

    Uses whatever fields exist in the node. Priority:
    1. name (or humanized id) + humanized situation
    2. tags (as bullet points)
    3. characters + objects (if present)
    4. valences (if present, as "dread 0.7, hope 0.2")
    5. place (if present)

    Falls back to truncated visual_description only if no
    structured fields exist at all.
    """
```

Example output with rich node (Graffito schema):
```
Scene: Forest of Adult Legs (street overwhelm)
- running, crowd, legs, overwhelm, anonymous figures
- characters: Tony
- dread 0.6, threat 0.5, hope 0.1
```

Example output with sparse node (Puppet Knows schema):
```
Scene: Notice Seams (seeing through)
- doorway, fingerprint, self scrutiny
- place: checkpoint
```

### 4. Trajectory tracker

Bridge-local state, accumulated from the JSONL stream:

```python
class TrajectoryTracker:
    def __init__(self):
        self.visit_counts: dict[str, int] = {}
        self.previous_node: str | None = None
        self.previous_situation: str | None = None
        self.previous_family: str | None = None

    def update(self, node_id, situation, family) -> dict:
        """Update state and return trajectory context."""
        self.visit_counts[node_id] = self.visit_counts.get(node_id, 0) + 1
        trajectory = {
            "visit_count": self.visit_counts[node_id],
            "previous_node": self.previous_node,
            "previous_situation": self.previous_situation,
            "is_return": self.visit_counts[node_id] > 1,
            "situation_changed": (
                self.previous_situation is not None
                and self.previous_situation != situation
            ),
            "shift": self._describe_shift(situation),
        }
        self.previous_node = node_id
        self.previous_situation = situation
        self.previous_family = family
        return trajectory

    def _describe_shift(self, current_situation):
        if self.previous_situation and self.previous_situation != current_situation:
            prev = humanize_id(self.previous_situation)
            curr = humanize_id(current_situation)
            return f"{prev} → {curr}"
        return None
```

### 5. Compile trajectory block

```python
def compile_trajectory_block(trajectory: dict) -> str:
    """Build the trajectory section of the Flash prompt."""
    lines = []
    vc = trajectory["visit_count"]
    if vc > 1:
        lines.append(f"- visit {vc} to this node")
        lines.append("- return: yes")
    else:
        lines.append("- first visit")

    if trajectory["previous_node"]:
        prev_label = humanize_id(trajectory["previous_node"])
        lines.append(f"- previous: {prev_label}")

    if trajectory["shift"]:
        lines.append(f"- shift: {trajectory['shift']}")

    return "Trajectory:\n" + "\n".join(lines) if lines else ""
```

### 6. Rewrite build_flash_request()

Replace the current monolithic text prompt with assembled sections:

```
{scene_gist}

{trajectory_block}

State:
- family: {family or 'drifting'}
- tension: {tension:.2f}
- energy: {energy:.2f}

Write 1-3 sentences of inner monologue.
Do not describe the image unless a visual detail changes the feeling.
```

### 7. Do NOT include

- Previous narration text (echo/drift risk per Codex review)
- Full visual_description as primary input (paraphrase risk)
- Raw IDs anywhere in the prompt
- selection_reasons or other engine debug data (bridge can't
  interpret these meaningfully)

## Graceful degradation

The gist compiler must handle:
- Nodes with full doc 19 schema (name, characters, objects,
  valences, tags) → rich gist
- Nodes with Puppet Knows schema (tags, place, indices) →
  adequate gist
- Nodes with only id + visual_description → humanized name +
  truncated description (worst case, still better than raw prompt)

## Testing

Test against Puppet Knows fixture first (exists, 20 nodes, sparse
schema). Verify:
- Humanized IDs read naturally
- Gist compiles from tags + place
- Trajectory tracks visits and shifts across cycles
- Energy appears in state block
- No raw IDs in Flash prompt
- Fallback works when structured fields are missing

When Graffito nodes are enriched (parallel Codex task), verify:
- Gist uses characters, objects, valences
- Richer gist produces better narration
