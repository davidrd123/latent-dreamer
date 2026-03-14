# Narration Layer

Makes the Dreamer's inner cognitive process visible alongside the
visual output. The visuals show what the dream sees; the narration
shows what the dream thinks.

## Why

The visual output alone is a hallucinatory montage. Without knowing
what the Dreamer is doing — which family is active, what emotional
state is driving scene selection, why this scene followed that one —
the audience has no handhold. The narration layer externalizes the
cognitive process as part of the performance.

This is the doc 01 experience: oscillation between immersion in the
dream (the visuals) and meta-awareness of the dreaming process
(the narration).

For Graffito slice v0, the graph-fixture + trace-fixture join that feeds
this layer is specified separately in doc 21
(`21-graffito-v0-playback-contract.md`).

## Design Intent

The narration should feel like a dreamer's inner voice commenting
on what they're actually seeing — not a disconnected mood generator.
This requires grounding Flash in three things:

1. **What was prompted** — the node's `visual_description` from the
   dream graph (the author's intent for this scene)
2. **What got rendered** — a screenshot from Scope's live output
   (what the LoRA and diffusion model actually produced)
3. **Why we're here** — the cognitive state (family, tension, energy)
   that drove the Dreamer to this node

The gap between (1) and (2) is where the dream lives. The prompt
says "feet pounding wet pavement" but the LoRA renders something
with its own texture, drift, and accumulated visual history. Flash
sees both and narrates the gap — that's the inner voice noticing
the dream's distortions.

### Grounding sources

| Source | What | How acquired | Latency |
|---|---|---|---|
| Dream graph | `visual_description`, `characters`, `objects`, `valences` | Loaded at startup via `--graph <path>`, indexed by node id | 0 (dict lookup) |
| Scope frame | PNG screenshot of current rendered output | `GET /api/v1/realtime/frame/latest` → `image/png` | ~50ms (local HTTP) |
| Engine JSONL | `graph_node_id`, `mind.*`, `world.*`, `narration.text` | Piped from stdin | 0 (already available) |

### What Flash receives (proposed)

Per cycle, the bridge assembles:
- The node's `visual_description` (text, from graph lookup)
- The current Scope frame (image, from screenshot endpoint)
- Cognitive state: family, tension, energy, situation
- The engine's template narration (as optional color, not primary input)

Flash is multimodal — it can read the image and the text together.
The system prompt tells it to narrate what it sees in the frame,
informed by the description and emotional state, as the dreamer's
inner voice.

### The screenshot endpoint

Scope exposes `GET /api/v1/realtime/frame/latest`:
- Returns the latest rendered frame as `image/png`
- Non-destructive — clones the frame without consuming from output queue
- Returns 404 if no frames generated yet
- Lives in `scope-drd/src/scope/server/app.py`

The bridge hits this once per cycle. At 5-10s dwell times, this is
negligible load. The `--scope-base-url` flag (already used by the
engine) provides the base URL.

## Where Things Live

This feature spans two repos. The spec lives here; the code lives
in scope-drd.

| What | Repo | Path |
|---|---|---|
| **This spec** | latent-dreamer | `daydreaming/Notes/experiential-design/20-narration-layer.md` |
| **Bridge script** | scope-drd | `tools/narration_bridge.py` |
| **Companion page** | scope-drd | `tools/narration_companion.html` |
| **Engine (producer)** | scope-drd | `tools/daydream_engine.py` |
| **Director (same model)** | scope-drd | `tools/daydream_director.py` |
| **Dream graphs** | scope-drd | `content/daydreams/<brief>/dream_graph.json` |

### Porting path

The bridge and companion currently live in `scope-drd/tools/` as
standalone scripts. When ready to integrate into Scope's frontend:

1. The WebSocket contract (`{ cycle, node_id, family, tension,
   energy, situation, dwell_s, narration }`) stays the same
2. The bridge becomes a ScopeDRD sub-module or built-in service
   (no more pipe — the engine calls it directly)
3. The companion HTML becomes a React component consuming the
   same WebSocket, rendered as a panel or overlay in Scope's UI
4. The HTTP file server (port 8766) goes away — Scope serves it

The contract is the stable seam. The delivery mechanism changes.

## Current status

This document is now partly a spec and partly a status note. The code
in `scope-drd` does **not** yet implement the full multimodal design
described below.

**What exists now:**
- Standalone bridge script in `scope-drd/tools/narration_bridge.py`
- Standalone companion page in `scope-drd/tools/narration_companion.html`
- Text-only Flash call per cycle
- WebSocket broadcast to browser companion
- Simple HTTP server for serving the companion page

**What is specified here but not yet implemented:**
- `--graph` flag and node lookup by `graph_node_id`
- Scope screenshot fetch via `/api/v1/realtime/frame/latest`
- Multimodal Flash call with image + scene text
- Shared scene contract between narration bridge and live Director loop

Treat the next section as the **target architecture**, not a claim
about what the code already does.

## Target Architecture

```
daydream_engine.py (scope-drd/tools/)
    │
    │  JSONL to stdout (one JSON object per cycle)
    │  contains: graph_node_id, mind.goal_type, mind.tension,
    │            mind.energy, world.situation_ids, narration.text,
    │            dwell_s
    │
    ▼
narration_bridge.py (scope-drd/tools/)
    │
    │  At startup:
    │    - Loads dream graph (--graph <path>), indexes nodes by id
    │
    │  Per cycle:
    │    1. Reads one JSONL line from stdin
    │    2. Looks up node's visual_description from graph
    │    3. Fetches current frame from Scope screenshot endpoint
    │    4. Calls Gemini Flash (multimodal: text + image)
    │    5. Pushes result to browser via WebSocket
    │
    │  Gemini model: gemini-3-flash-preview
    │  (same model used by daydream_director.py)
    │
    │  Inputs to Flash:
    │    - visual_description (text, from graph)
    │    - current frame (image/png, from Scope)
    │    - cognitive state (family, tension, energy, situation)
    │
    ├──→ Scope API: GET /api/v1/realtime/frame/latest
    │        reads: current rendered frame (PNG)
    │
    ├──→ WebSocket on ws://localhost:8765
    │        pushes: { cycle, node_id, family, tension,
    │                  energy, situation, dwell_s, narration }
    │
    └──→ HTTP on http://localhost:8766
             serves: narration_companion.html
```

### Files

| File | Repo | What | Port |
|---|---|---|---|
| `tools/narration_bridge.py` | scope-drd | Bridge: stdin → Flash → WebSocket | 8765 (WS), 8766 (HTTP) |
| `tools/narration_companion.html` | scope-drd | Browser companion page | served by bridge |

### Dependencies

- `websockets` (already installed in scope-drd)
- `httpx` (already installed in scope-drd)
- `GEMINI_API_KEY` or `GOOGLE_API_KEY` env var

### Target usage

```bash
# Terminal: pipe engine output through the narration bridge
# PYTHONUNBUFFERED=1 prevents pipe buffering delays
PYTHONUNBUFFERED=1 uv run python tools/daydream_engine.py \
    --world content/daydreams/graffito/world.yaml \
    --graph content/daydreams/graffito/dream_graph.json \
    --cycles 18 \
    --live-stage \
    --scope-base-url http://localhost:8000 \
    --interval-s 0 \
  | uv run python tools/narration_bridge.py \
    --graph content/daydreams/graffito/dream_graph.json \
    --scope-base-url http://localhost:8000

# Browser: open companion page
open http://localhost:8766/narration_companion.html
```

The bridge needs two things beyond stdin:
- `--graph` — path to the dream graph JSON (for visual_description lookup)
- `--scope-base-url` — Scope API base URL (for screenshot endpoint)

The engine sends prompts to Scope (visual) and JSONL to stdout.
The bridge reads that JSONL, looks up the node in the graph, fetches
the current frame from Scope, calls Flash with text + image, and
pushes the narration to the browser. Three screens: Scope video,
companion narration, and the terminal showing raw state.

## The Flash Call

### What it receives

Per cycle, from three sources:

**From the dream graph (loaded at startup):**
- `visual_description` — the 75-150 word captioning-voice scene
  description (what was prompted to Scope)
- `characters`, `objects` — who and what is in this node
- `valences` — hope/threat/dread/joy/wonder (0-1 each)

**From the Scope screenshot endpoint:**
- Current rendered frame as PNG — what the audience is actually
  seeing right now (the LoRA's interpretation of the prompt,
  accumulated visual drift, style artifacts)

**From the engine JSONL (stdin):**
- `mind.goal_type` — cognitive family (REVERSAL, ROVING, etc.)
- `mind.tension` — emotional pressure (0-1)
- `mind.energy` — activity level (0-1)
- `world.situation_ids` — active situation(s)
- `narration.text` — engine's template narration (optional color)

Flash receives a multimodal prompt: image + text. The image is the
frame; the text includes the visual_description, cognitive state,
and optionally the template narration.

### What it produces

1-3 sentences of inner monologue. First or second person, present
tense. Grounded in what's on screen but filtered through the
emotional state. The cognitive family shapes tone (REVERSAL =
wrongness, ROVING = escape, etc.) but is never named explicitly.
The audience should feel the dream's inner voice, not read a
debug log.

The narration should respond to what Flash *sees in the frame*,
not just parrot the visual_description back. When the rendered
output diverges from the prompt — as it always does with LoRA
drift — that divergence is material. The dreamer notices.

### System prompt

The system prompt instructs Flash to be "the inner voice of a
dreaming mind" — stream of consciousness, grounded in the image
it sees and the scene description, shaped by the cognitive state,
brief and evocative. See `narration_bridge.py` for the full
prompt.

### Graceful degradation

If the screenshot endpoint returns 404 (no frames yet) or the
bridge can't reach Scope, fall back to text-only: visual_description
+ cognitive state, no image. If the graph isn't loaded, fall back
to template narration + cognitive state (current behavior). The
bridge should always produce *something*.

## Companion Page

### Display

- **Hero narration** — large italic text, centered, fades between
  cycles with smooth transitions
- **Family badge** — color-coded label (REVERSAL=blue, ROVING=gold,
  etc.)
- **Tension/energy meters** — thin bars showing current levels
- **Situation label** — human-readable situation name
- **Cycle counter** — current cycle number
- **History** — previous narrations scroll below with metadata,
  fading with a gradient mask

### Design

Dark background, Cormorant Garamond for narration text, JetBrains
Mono for metadata. Subtle film-grain overlay. Ambient glow that
shifts with tension. Each family has a distinct color. Typography-
forward — the narration text is the entire point.

## Future: Text-to-Speech

The narration text produced by Flash is suitable for TTS. A future
layer could send the narration to a speech synthesis API and play
it as audio alongside the visual output. The dream would have a
whispered inner voice.

Pipeline: Flash narration → TTS API → audio output (mixed with
parametric Lyria music).

## Future: Integration with Scope Frontend

The companion page is standalone for now. It could move into
Scope's React frontend as a panel or overlay. The WebSocket
contract (cycle, node_id, family, tension, energy, situation,
narration) would be the same — just consumed by a React component
instead of a standalone HTML page.

## What This Does NOT Do

- Does not change the engine's behavior or traversal
- Does not modify the visual output sent to Scope
- Does not replace the parametric music layer (doc 17)
- Does not feed back into the Dreamer's cognitive state
- Is purely observational / presentational

## Known Issues

### Must fix before next test

- **Spec/code drift (critical).** The document describes a multimodal
  bridge with graph lookup and screenshot grounding, but the current
  code is still text-only. The spec should be read as target state;
  the implementation needs to catch up.
- **No visual grounding (critical).** The bridge currently sends
  Flash the engine's template narration text or the bare node id.
  Flash has nothing scene-specific to narrate. Fix: load dream
  graph at startup, look up the node's authored prompt by id, and
  fetch Scope frame via screenshot endpoint. Send both to Flash as a
  multimodal prompt.
- **Missing CLI contract.** The doc now assumes `--graph` and
  `--scope-base-url` flags on the bridge, but the bridge does not yet
  implement that CLI surface. The runtime contract needs to exist in
  code before the usage block is accurate.
- **innerHTML XSS in companion page.** `pushToHistory()` injects
  `data.narration` via innerHTML. Fix: use `textContent` or DOM
  API for all user-facing text.
- **API key exposure in logs.** The current Gemini request path sends
  the API key in the request URL, and INFO-level HTTP client logging
  can expose that key in terminal logs. This must be fixed before the
  bridge is treated as operationally safe.
- **Director/narration payload mismatch.** The live Director packet
  and narration bridge currently use different, under-specified scene
  payloads. Both interpretive layers should converge on one shared
  scene contract grounded in the node's authored prompt.

### Open questions (for chorus review)

- **How much text context?** Should Flash get just the current
  node's visual_description, or also the previous 1-2 narrations
  for continuity? More context = more coherent inner voice, but
  also more token cost and risk of repetition.
- **Frame timing.** The screenshot captures whatever Scope is
  rendering *right now*, which may be mid-transition or from a
  previous prompt that hasn't fully resolved. Is that a feature
  (the dream's blur between moments) or a bug?
- **Narration cadence.** Currently one narration per engine cycle.
  Should narration run on its own clock (e.g., every N seconds
  regardless of cycle boundaries)? The dwell_s varies per node.
- **System prompt tuning.** The current prompt is generic. With
  visual grounding, it may need to shift — e.g., explicitly
  instruct Flash to describe what it *sees* vs. what was prompted.
- **TTS integration point.** If narration feeds TTS, the cadence
  and length constraints change. A 3-sentence narration at 5s
  dwell might not finish speaking before the next cycle.

### Operational

- The engine's JSONL uses Python pipe buffering. If narration
  appears delayed, set `PYTHONUNBUFFERED=1` in the environment.
- The bridge currently keeps serving after stdin closes by blocking
  indefinitely. This preserves browser history, but the runtime
  contract should be treated as "manual shutdown with Ctrl-C," not a
  clean process exit.
- The Flash call adds ~200-500ms latency per cycle (text-only).
  With screenshot fetch + multimodal call, expect ~500-1000ms.
  Still fine for 5-10s dwell times.

## Proposed implementation sequence

1. **Stabilize the bridge contract.**
   Add explicit bridge CLI flags for `--graph` and `--scope-base-url`.
   Keep the bridge standalone for now; do not move it into Scope yet.

2. **Ground narration in authored scene text.**
   Load the graph at bridge startup, index nodes by id, and resolve
   `graph_node_id -> prompt` (or `visual_description`) per cycle.
   This is the highest-leverage quality fix.

3. **Add screenshot grounding as a second pass.**
   Fetch `/api/v1/realtime/frame/latest` once per cycle and feed it to
   Flash alongside the node prompt. If no frame is available, degrade
   cleanly to text-only mode.

4. **Fix security and rendering hygiene.**
   Remove key leakage from logs and replace `innerHTML` with plain-text
   DOM rendering in the companion.

5. **Unify scene payloads across repos.**
   Define one shared scene contract for narration and Director
   interpretation so both layers consume the same grounded material.

6. **Only then tune prompt quality and cadence.**
   After the bridge is grounded and safe, tune system prompt wording,
   context window, and narration cadence. TTS remains Phase 2.
