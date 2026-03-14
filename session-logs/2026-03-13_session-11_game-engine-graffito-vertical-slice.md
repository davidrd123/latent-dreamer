# Session 11: Game Engine Architecture → Graffito Vertical Slice

**Date:** 2026-03-13
**Duration:** Long session, architecture → implementation

## What Happened

### 1. Doc 17 — Game Engine Architecture (new, supersedes doc 16)

Converged on a simplified runtime model through multi-agent discussion.
The three-agent model from doc 16 (Director/Cinematographer/Composer)
collapsed into:

- **Pre-authored dream graph** = the product (not live LLM improvisation)
- **One renderer** per cycle (node description → Scope prompt)
- **Music as parametric modulation** (not LLM composition) — music IS
  the emotional modulation layer, responding to Dreamer state
- **Deterministic post-effects** by cognitive family (color cast)
- **Cognitive feedback loop** (doc 12) unchanged — replaces rendering,
  not cognition

Key architectural decisions:
- Music carries emotional modulation, visuals stay grounded in nodes
- Dreamer state (family, tension) → Lyria parameters (not per-cycle LLM)
- Music should read trajectory (sliding window), not just snapshot
- The adapter/selection seam between Dreamer and nodes stays explicit
- Families determine visual content change: ROVING/REVERSAL/REVENGE =
  different nodes; RATIONALIZATION/REHEARSAL = same node, music+color

File: `daydreaming/Notes/experiential-design/17-game-engine-architecture.md`

### 2. Graffito as Primary Creative Brief

Identified Graffito (Mark Friedberg's short film) as the real brief
for the first watchable run. Has everything: script, shots, trained
LoRA, captioning spec, prompt engineering rubrics (9 strategy docs),
clip library with Mark's selects, active collaborator.

Source material: `/Users/daviddickinson/Projects/Lora/ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/`

### 3. Doc 18 — Graffito Vertical Slice

Full inventory of existing Graffito assets mapped to doc 17 architecture.
Scoped to scenes 3-4 (street montage → Grandma's apartment) as slice v0.
12-15 nodes, 2 situations, narrow scope.

File: `daydreaming/Notes/experiential-design/18-graffito-vertical-slice.md`

### 4. Doc 19 — Node Schema (frozen)

Defined and froze the dream graph node schema:
- id, name, scene_ref, variant_of, variant_family
- situation_ids (list), dwell_s
- visual_description (75-150 words, Graffito captioning voice)
- characters, objects
- valences (hope/threat/dread/joy/wonder, 0-1)
- tags (flat list for retrieval)

What's NOT on the node: goal_type, tension, energy (those are trace
state, not node properties).

File: `daydreaming/Notes/experiential-design/19-graffito-node-schema.md`

### 5. Dream Graph Authored (13 nodes)

Codex 1 authored 13 nodes for scenes 3-4:
- 8 street nodes (s1_street_overwhelm) including 2 REVERSAL variants
- 5 apartment nodes (s2_grandmas_apartment)
- All in Graffito captioning voice, material vocabulary, 75-150 words

File: `daydreaming/fixtures/graffito_v0_scenes_3_4.yaml`

### 6. Hand-Authored Trace (18 cycles)

Dream-logic sequence through the 13 nodes:
- Not linear narrative — emotional oscillation between street and apartment
- 5 revisitations (same node, different emotional context)
- REVERSAL for overwhelm, ROVING for escape, null for default traversal
- Key dream-logic moments: safety yanked away (cycle 9), still running
  after the dance (cycle 12), street calmer post-grounding (cycles 16-17)

File: `daydreaming/fixtures/graffito_v0_trace.yaml`

### 7. Pipeline Discovery: Scope, NOT ComfyUI

**Critical correction:** The live pipeline is APC Mini → ScopeDRD → Scope,
NOT ComfyUI batch YAML. ComfyPromptByAPI is a separate, unrelated system.
The daydream engine already exists in scope-drd and sends prompts to
Scope via REST API.

Saved as feedback memory: `feedback_scope_not_comfy.md`

### 8. Template Renderer + Playlist

Built `daydreaming/render_trace.py` — reads graph + trace, applies
template passthrough renderer (visual_description + optional family
capstone), outputs a Scope playlist (one prompt per line).

Playlist placed at: `scope-drd/content/playlists/graffito/Captioning/graffito_v0_dream_trace_captions.txt`

Usage: `video-cli playlist load graffito`

### 9. Full Daydream Engine Adaptation

Created Graffito content for the existing daydream engine:
- `scope-drd/content/daydreams/graffito/world.yaml` — 2 situations,
  2 places, 3 characters, 2 events
- `scope-drd/content/daydreams/graffito/dream_graph.json` — 13 nodes
  with mind/world blocks, 20 typed edges, full engine format

Patched `daydream_engine.py` to handle inline prompts (no palette):
- Lines 920-923: handle missing `ref.row`/`ref.col`
- Lines 901-904: handle empty palettes
- Lines 973-976: handle missing palette path
- Lines 104-109: changed revenge/repercussions from hard_cut to soft_cut

**Known issue:** Engine gets stuck in s1_street_overwhelm — doesn't
cross to apartment. The REVERSAL variants dominate retrieval scoring.
Needs cross-situation index overlap tuning. The hand-authored trace
(playlist path) works correctly.

### 10. Doc 20 — Narration Layer (spec written, code in progress)

Architecture for making the Dreamer's cognitive process visible:
- `narration_bridge.py` reads engine JSONL, calls Gemini Flash
  (`gemini-3-flash-preview`), pushes narration to browser via WebSocket
- `narration_companion.html` displays narration with family badges,
  tension meters, history
- Piped: `daydream_engine | narration_bridge`

Files created in `scope-drd/tools/`:
- `narration_bridge.py` — bridge script (WS on 8765, HTTP on 8766)
- `narration_companion.html` — companion page

**Bugs fixed during session:**
- Model: changed from deprecated `gemini-2.0-flash-lite` to `gemini-3-flash-preview`
- `clients` set variable shadowing in broadcast()
- HTTP serving: separated from WebSocket (different ports)
- Pipe buffering: needs `PYTHONUNBUFFERED=1`

**Still needs work:**
- The companion page connects but may not receive data if engine
  finishes before browser opens (fixed with keepalive)
- Flash narration quality depends on what's in the JSONL (currently
  just the engine's template narration, not the full visual prompt)
- Engine stuck-in-s1 tuning

## Files Created/Modified

### latent-dreamer (this repo)
- `daydreaming/Notes/experiential-design/17-game-engine-architecture.md` — NEW
- `daydreaming/Notes/experiential-design/18-graffito-vertical-slice.md` — NEW
- `daydreaming/Notes/experiential-design/19-graffito-node-schema.md` — NEW
- `daydreaming/Notes/experiential-design/20-narration-layer.md` — NEW
- `daydreaming/fixtures/graffito_v0_scenes_3_4.yaml` — NEW (13 nodes)
- `daydreaming/fixtures/graffito_v0_trace.yaml` — NEW (18 cycles)
- `daydreaming/render_trace.py` — NEW (trace → playlist)
- `daydreaming/codex-tasks/graffito-slice-graph-authoring.md` — NEW

### scope-drd (sibling repo)
- `content/daydreams/graffito/world.yaml` — NEW
- `content/daydreams/graffito/dream_graph.json` — NEW
- `content/playlists/graffito/Captioning/graffito_v0_dream_trace_captions.txt` — NEW
- `tools/narration_bridge.py` — NEW
- `tools/narration_companion.html` — NEW
- `tools/daydream_engine.py` — MODIFIED (inline prompt support, soft_cut transitions)

### Memory
- `memory/project_graffito_brief.md` — NEW
- `memory/feedback_scope_not_comfy.md` — NEW
- `memory/MEMORY.md` — UPDATED

## What's Next

### Immediate
1. **Fix narration bridge bugs** — test end-to-end with Flash narration
2. **Fix engine stuck-in-s1** — add cross-situation index overlap to
   Graffito graph nodes so the scheduler crosses to apartment
3. **Test playlist path with Scope** — `video-cli playlist load graffito`
   with Graffito LoRA loaded

### Short-term
4. **TTS layer** — send Flash narration to speech synthesis
5. **Parametric music spike** — validate Lyria mid-stream modulation
6. **Expand graph** — 40-50 nodes across all 7 Graffito situations

### Architecture settled
- Doc 17 is runtime canon
- Graffito is primary creative brief
- Playlist for manual nav, daydream engine for autonomous
- Narration layer for making inner process visible
- Music as modulation (pending Lyria validation)
