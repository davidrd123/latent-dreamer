# Multi-Language Process Contract

How the current Clojure kernel, Python tooling, and future JS/TS surfaces
should fit together without turning the repo into three competing runtimes.

Last updated: 2026-03-24

---

## Purpose

This document is not a language-debate memo. The repo already has a real
Clojure kernel, real Python tooling, and a likely future need for a live UI.

The question now is:

> What process boundaries and message contracts keep that split coherent?

The contract below is the answer.

---

## Short Version

- **Clojure owns cognition.**
  World state, cycle execution, rule firing, memory, family plans, promotion,
  access control, persistence, and benchmark adapters live here.
- **Python owns tooling and media-side utilities.**
  Artifact rendering, analysis, packaging, image/audio helpers, batch
  experiments, and model/media helper services live here.
- **JS/TS should own operator-facing surfaces.**
  Trace viewers, conductor controls, session inspectors, and stage dashboards
  should live here.

The main rule is:

> Clojure exports data and accepts bounded commands.
> Python and JS never become the authoritative owner of cognitive state.

---

## Current Process Sketch

```text
Human / CLI / Conductor UI
    |
    v
Clojure session / benchmark runner
[kernel/src/daydreamer/conductor.clj]
    |
    +--> benchmark adapter
    |    e.g. Graffito miniworld / Puppet Knows
    |
    +--> kernel core
    |    rules / control / memory / contexts / goal families
    |
    +--> optional runtime-thought edge
    |    packet -> Anthropic HTTP -> normalized residue -> writeback
    |
    +--> optional director edge
    |    packet -> Gemini HTTP -> bounded feedback -> apply to world
    |
    v
trace snapshots / reporter log / out artifacts
    |
    +--> Python tooling
    |    analysis / rendering / audio / world pack / model helpers
    |
    +--> future JS/TS UI
         trace viewer / controls / session inspector
```

This is already close to the real repo:

- outer loop: `kernel/src/daydreamer/conductor.clj`
- kernel core: `kernel/src/daydreamer/*.clj`
- runtime thought edge: `kernel/src/daydreamer/runtime_thought.clj`
- director edge: `kernel/src/daydreamer/director.clj`
- trace projection: `kernel/src/daydreamer/trace.clj`
- Python audio/render/tooling:
  - `daydreaming/listen_trace.py`
  - `daydreaming/render_trace.py`
  - `daydream_vision/service.py`

---

## Ownership Model

### Clojure Owns

- authoritative world state
- cycle loop and benchmark session loop
- rule matching and execution
- episodic retrieval and storage
- family activation and planning
- promotion, flags, rule access, provenance
- trace snapshots and canonical cycle summaries

If a subsystem decides what the mind does next, it belongs here.

### Python Owns

- artifact rendering and report generation
- batch experiments and offline analysis
- image/audio/model helper services
- world pack assembly and linting
- non-authoritative transforms over saved snapshots or packets

Python can derive, annotate, summarize, or render. It should not own live
kernel truth.

### JS/TS Owns

- live trace and session viewing
- conductor controls
- dashboard/status views
- stage/operator interfaces

JS should send commands and display events. It should not maintain the only
copy of dream state.

---

## Boundary Rules

### Rule 1: One owner for live cognitive state

The only authoritative world state is the Clojure world map.

Python and JS may cache projections of that state, but they do not get to
mutate cognition directly. They request changes through bounded commands.

### Rule 2: Data at boundaries, not callbacks

The cross-language boundary should use plain data envelopes:

- JSON for network / process boundaries
- EDN only when both sides are JVM/Clojure-facing and the interface is not
  intended for JS or Python

No cross-language boundary should depend on ad hoc function callbacks or object
identity.

### Rule 3: Bounded side effects

External model/media calls stay behind explicit edges:

- runtime thought edge
- director edge
- image/audio tool services

The kernel decides when to call them and how their results re-enter state.

### Rule 4: Hot loop stays local

Do not bounce cognitive selection or retrieval across process boundaries every
cycle unless there is a strong measured reason. Python/JS may observe every
cycle. They should not have to decide every cycle.

### Rule 5: Stable canonical projections

The kernel should expose stable projections of internal state instead of making
other languages understand the whole internal world shape.

The trace/reporter layer is the main place to do that.

---

## Canonical Process Modes

### Mode A: Offline benchmark run

```text
CLI -> Clojure benchmark runner -> artifacts -> Python analysis
```

Used for:

- Graffito miniworld
- Puppet Knows
- membrane assays
- test and verification runs

### Mode B: Live conducted session

```text
Conductor UI -> Clojure kernel session -> stage commands / thought edges
                                   |
                                   +-> event stream to UI
                                   +-> artifacts to Python tools
```

Used for:

- APC-driven or UI-driven session biasing
- live stage playback
- runtime thought / director / audio integration

### Mode C: Tooling-only analysis

```text
saved artifacts -> Python tools -> reports / media / summaries
```

Used for:

- post-run diagnostics
- render passes
- batch comparisons
- figure generation

---

## Canonical Message Types

These are the main envelopes that should exist across boundaries.

### 1. `CycleSnapshot`

Authoritative per-cycle projection emitted by the kernel trace layer.

Producer:
- Clojure

Consumers:
- Python analysis
- JS trace viewer
- runtime thought packet builders
- director packet builders

Minimum fields:

```json
{
  "session_id": "sess-2026-03-24-a",
  "cycle": 12,
  "timestamp": "2026-03-24T21:18:00Z",
  "mode": "daydreaming",
  "selected_goal": {...},
  "top_candidates": [...],
  "active_indices": [...],
  "retrievals": [...],
  "activations": [...],
  "branch_events": [...],
  "emotion_shifts": [...],
  "emotional_state": [...],
  "selection": {...},
  "rule_provenance": {...},
  "situations": {...},
  "feedback_applied": {...},
  "runtime_thought": {...}
}
```

Notes:

- This is a projection, not the full world map.
- It should stay stable enough that Python/JS do not have to track internal
  kernel refactors.

### 2. `RuntimeThoughtPacket`

Compact packet for a per-cycle inner-life realization call.

Producer:
- Clojure adapter / runtime-thought packet builder

Consumer:
- LLM edge

Minimum fields:

```json
{
  "cycle": 12,
  "selected_family": "reversal",
  "selected_goal": {...},
  "situation_ids": ["mural_crisis"],
  "emotional_state": [...],
  "retrieved_fragments": [...],
  "active_indices": [...],
  "allowed_residue_indices": [...]
}
```

Result comes back as normalized bounded feedback, not free-form authority over
the world.

### 3. `DirectorPacket`

Reduced scene-level packet for conducted-daydreaming interpretation feedback.

Producer:
- Clojure adapter / director packet builder

Consumer:
- director edge

Minimum fields:

```json
{
  "packet": {...},
  "scene": {...},
  "world_situations": [...],
  "previous_director_feedback": {...}
}
```

### 4. `ConductorCommand`

Bounded operator request sent from UI or CLI into the kernel session.

Producer:
- JS/TS UI
- CLI tool

Consumer:
- Clojure session

Minimum shape:

```json
{
  "type": "bias_situation",
  "session_id": "sess-2026-03-24-a",
  "cycle_target": "next",
  "payload": {
    "situation_id": "mural_crisis",
    "delta": 0.15,
    "reason": "operator_fader"
  }
}
```

Other likely command types:

- `bias_situation`
- `set_mode_bias`
- `inject_provocation`
- `request_snapshot`
- `pause_session`
- `resume_session`
- `advance_one_cycle`
- `start_session`
- `stop_session`

Rule:

- commands request a kernel operation
- they do not contain direct world mutations in arbitrary shape

### 5. `StageCommand`

Renderer-facing command derived from kernel state or node/session traversal.

Producer:
- Clojure session directly, or Python translation layer over saved/session data

Consumer:
- JS stage UI
- Scope bridge
- audio bridge

Minimum shape:

```json
{
  "type": "stage_update",
  "session_id": "sess-2026-03-24-a",
  "cycle": 12,
  "video": {
    "transition": {"kind": "soft_cut", "chunks": 4},
    "prompt_ref": "graffito_mural_03",
    "parameters": {...}
  },
  "audio": {
    "music_prompt": "...",
    "config": {...},
    "reset_context": false
  },
  "narration": {
    "mode": "caption",
    "text": "..."
  }
}
```

Rule:

- stage payload is a projection or render directive
- it is not the canonical world state

### 6. `ArtifactSummary`

Offline summary emitted after a run.

Producer:
- Clojure benchmark adapter or Python analysis tool

Consumers:
- notes
- dashboards
- comparison scripts
- UI history panels

Minimum shape:

```json
{
  "run_id": "graffito-miniworld-2026-03-24-001",
  "benchmark": "graffito_miniworld",
  "cycles": 50,
  "family_counts": {
    "rationalization": 25,
    "reversal": 13,
    "rehearsal": 12
  },
  "cross_family_source_win_cycles": 12,
  "durable_episode_count": 2,
  "rule_access_transition_count": 1,
  "artifact_paths": [...]
}
```

---

## Suggested Transport Choices

### Clojure <-> Python

Use one of:

- saved JSON/JSONL artifacts for offline work
- subprocess JSON over stdin/stdout for bounded tool calls
- local HTTP only when a long-lived Python helper service is justified

Prefer artifacts first. Add a service only when the call pattern is frequent or
interactive enough to justify it.

### Clojure <-> JS/TS UI

Use:

- WebSocket or SSE for outgoing event stream
- HTTP or WebSocket messages for incoming commands

The UI should subscribe to session events rather than polling the whole world
map repeatedly.

### Clojure <-> external model APIs

Stay behind explicit namespaces:

- `runtime_thought.clj`
- `director.clj`
- later evaluator / generator edges

Do not spread provider-specific HTTP code across benchmarks.

---

## Minimal Event Stream Contract

If a live UI is added soon, the first useful event stream can stay very small.

### Events from kernel to UI

```json
{"type":"session_started","session_id":"sess-1","config":{...}}
{"type":"cycle_completed","session_id":"sess-1","cycle":12,"snapshot":{...}}
{"type":"session_paused","session_id":"sess-1"}
{"type":"session_resumed","session_id":"sess-1"}
{"type":"session_finished","session_id":"sess-1","summary":{...}}
{"type":"kernel_error","session_id":"sess-1","error":{...}}
```

### Commands from UI to kernel

```json
{"type":"start_session","payload":{...}}
{"type":"advance_one_cycle","session_id":"sess-1"}
{"type":"pause_session","session_id":"sess-1"}
{"type":"resume_session","session_id":"sess-1"}
{"type":"request_snapshot","session_id":"sess-1"}
{"type":"bias_situation","session_id":"sess-1","payload":{...}}
```

That is enough to build:

- trace viewer
- cycle inspector
- conductor control panel
- session replay UI

---

## What Should Not Be Cross-Language Yet

These should remain kernel-local until there is strong pressure otherwise:

- rule matching
- family activation and plan choice
- episodic retrieval thresholding
- promotion / demotion / flag logic
- rule-access state
- provenance bookkeeping

If these move out, the repo stops having one cognitive runtime.

---

## What Is Safe To Keep Outside The Kernel

These are good cross-language or tooling-side candidates:

- report formatting
- HTML renderers
- image/audio prompt assembly
- world linting and pack generation
- analysis dashboards
- batch comparisons
- stage adaptors and operator UI

---

## Near-Term Integration Plan

### Step 1: treat `CycleSnapshot` as the canonical boundary object

Keep evolving `trace.clj` so Python and a future UI can consume stable per-cycle
data without knowing the whole world map shape.

### Step 2: add a small live session event stream

Implement only:

- `session_started`
- `cycle_completed`
- `session_finished`
- `kernel_error`

That is enough for a first operator-facing dashboard.

### Step 3: add bounded `ConductorCommand`s

Start with:

- `advance_one_cycle`
- `pause_session`
- `resume_session`
- `bias_situation`
- `request_snapshot`

### Step 4: keep Python on the tooling side

Do not move Python into hot-loop cognition. Use it for analysis, rendering,
audio/image helper services, and batch tooling.

---

## Anti-Patterns

Avoid these:

- JS owning the only live session state
- Python deciding family selection per cycle
- different languages each mutating the same world representation
- kernel internals leaked directly to every consumer
- ad hoc one-off JSON shapes per benchmark with no stable projection layer
- adding another runtime loop that competes with `conductor.clj`

---

## Decision Rule

When deciding where new code belongs, ask:

1. Does it decide live cognitive state or future cognitive reachability?
   - If yes, Clojure.
2. Is it a renderer, analysis tool, packager, or media/model helper?
   - If yes, Python or JS.
3. Has the pattern been proven enough times that it should become a reusable
   kernel seam rather than benchmark-local glue?
   - If yes, move the reusable mechanism to Clojure and keep only
     fixture-specific shaping outside.

That is the contract.
