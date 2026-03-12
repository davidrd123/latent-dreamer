# Daydream-to-Stage Contract (Round 3)

This repo's output is a **dream director**: a system that can "dream coherently"
over some source material and emit a stream or graph of **render directives**.

The renderer already exists in `scope-drd`:

- Scope server (real-time T2V): prompt + transitions + parameters via REST
- Palettes: `.palette.yaml` (64 cells) + `.txt` playlist
- Lyria RealTime: weighted prompts + config + context resets

This document defines the minimal shared interface between:

- **Dreaming layer** (narrative backbone, daydream logic, narration)
- **Stage layer** (Scope video + Lyria audio + optional captions/voice)

## Goals

- Make "coherent dreaming" implementable without rewriting Scope.
- Keep narration optional and orthogonal (caption or voice).
- Allow starting from existing tuned material (palettes) or authored worlds.

## Core Objects

### `DreamNode`

One unit of experience. A node should be renderable without asking an LLM.

```json
{
  "id": "n_001",
  "dwell_s": 6.0,

  "ref": {
    "palette_id": "escape_new_york",
    "row": 0,
    "col": 3
  },

  "mind": {
    "goal_type": "rehearsal",
    "tension": 0.7,
    "energy": 0.6,
    "novelty": 0.2,
    "hold": false
  },

  "world": {
    "compatibility": "present_compatible",
    "situation_ids": ["the_letter"],
    "event_id": null
  },

  "stage": {
    "transition_out": {"kind": "soft_cut", "chunks": 4, "temp_bias": 0.15},
    "scope_params": {"kv_cache_attention_bias": 0.3}
  },

  "audio": {
    "music_prompt": "identity=Cold minimal synth score, tense and mechanical | instruments=TR-909 Drum Machine, Boomy Bass, Synth Pads | production=Dark sparse mix, cold reverb",
    "config": {"density": 0.35, "brightness": 0.4, "guidance": 3.5},
    "reset_context": false
  },

  "narration": {
    "text": "I keep rehearsing what I would say if I finally opened it.",
    "mode": "caption"
  }
}
```

Notes:

- `ref` is preferred over embedding the full prompt text. The stage runtime can
  look up `prompt` + `music_prompt` from the palette file.
- `music_prompt` supports the pipe-delimited layering convention used by the
  APC Mini bridge (`identity=... | instruments=... | production=...`).
- `compatibility` exists to support counterfactual rendering treatment; it does
  not require a full world-state engine in v0.

### `DreamEdge`

```json
{
  "from": "n_001",
  "to": "n_014",
  "kind": "association",
  "weight": 0.7,
  "situation_alignment": ["the_letter"],
  "justification": "returns to the same charged object from a new angle"
}
```

### `DreamGraph`

```json
{
  "version": 1,
  "palettes": {
    "escape_new_york": {
      "palette_path": "/Users/daviddickinson/Projects/Lora/scope-drd/content/palettes/escape_new_york.v2-layered.palette.yaml"
    }
  },
  "nodes": [],
  "edges": [],
  "start_node_id": "n_001"
}
```

## Stage Mapping (Concrete)

### Scope (video)

At minimum, a node needs to map to:

- prompt set: `PUT /api/v1/realtime/prompt {"prompt": "..."}`
- transition choice:
  - hard cut: `POST /api/v1/realtime/hard-cut`
  - soft cut: `POST /api/v1/realtime/soft-cut {"temp_bias": ..., "num_chunks": ...}`
  - embedding transition: handled by the bridge / palette pipeline (optional)
- optional parameter updates: `POST /api/v1/realtime/parameters {...}`

### Lyria (audio)

A node maps to:

- `set_weighted_prompts([WeightedPrompt(text=..., weight=...)])`
- `set_music_generation_config(config)` (send full config struct)
- optional `reset_context()` (required after BPM/scale changes)

Music prompt hygiene:

- Do not encode BPM or key in text prompts. Those are config and may be fader-controlled.
- Prefer the constrained Lyria instrument vocab in prompts.

### Narration (optional)

Treat narration as a third renderer driven by the same node.

Two practical modes:

- `caption`: render text overlay at node entry or during hold states
- `voice`: queue text into a TTS system (offline pre-gen preferred; live TTS later)

In v0, narration can be template-based from (`goal_type`, `situation_ids`,
`compatibility`) to avoid adding an LLM dependency in the live loop.

## What “Material” Means Under This Contract

Material is anything that can compile into `DreamNode`s:

1. **Palettes (recommended v0):** your existing `.palette.yaml` prompts already
   render well in Scope and already have (or can have) Lyria prompts.
2. **World bible + seed scenes:** authored characters/places/situations that
   generate new nodes offline, then lock them into a graph for performance.
3. **Personal notes (deferred):** vault fragments can become situations and
   narration seeds, but requires provenance and privacy decisions.

For coherence in the shortest time, start with 1 palette + 3 situations +
template narration. Expand later.
