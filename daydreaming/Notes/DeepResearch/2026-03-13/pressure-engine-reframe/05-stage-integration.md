# Stage Integration: Concrete Architecture

## Source

Distilled from a deep research report that examined both the
latent-dreamer kernel and scope-drd codebase in detail
(`../re-evaluation/dr/deep-research-report (4).md`). This doc
extracts the concrete integration pieces that survive the pressure
engine reframe.

---

## Core Principle

**Treat the stage as a deterministic actuator. Treat the upstream
engine (whether Mueller-family-driven or pressure-operator-driven)
as a decision-and-memory engine that emits small, explicit
directives.**

This principle is invariant across the reframe. Whatever drives
decisions upstream — Mueller families, pressure operators, a live
human conductor — the stage interface is the same.

---

## Scope-DRD Integration Points

These are the actual code-level surfaces in scope-drd that the
pressure engine would drive.

### FrameProcessor

Server-side real-time loop. Drains mailbox-style parameter updates,
applies them deterministically at chunk boundaries, calls the
pipeline. Contains:

- WorldState model (typed semantic state of the scene)
- StyleManifest (visual style configuration)
- Prompt compiler (WorldState → pipeline prompts)
- Timeline recorder (session recording)

### ControlBus

Deterministic ordering policy. Guarantees that if the engine emits
multiple control intents within a cycle, the stage applies them in
predictable order at chunk boundaries (pause/resume → prompt → seed →
etc.).

### Reserved Control Keys

FrameProcessor accepts these through the parameters update path:

| Key | What it does |
|-----|-------------|
| `_rcp_world_state` | Replace WorldState via validation |
| `_rcp_set_style` | Swap style manifest, optionally trigger cache reset + LoRA update |
| `_rcp_soft_transition` | Temporarily alter cache bias for soft visual changes |
| `_rcp_snapshot_request` | Create stage snapshot (GPU tensor clone, LRU max 10) |
| `_rcp_restore_snapshot` | Restore to a previous snapshot |
| `_rcp_session_recording_start/stop` | Timeline recording |

### REST Endpoints

The realtime REST API accepts prompt changes and parameter updates
that feed directly into FrameProcessor's update pipeline. This is
the lowest-friction integration path — no WebRTC client needed.

---

## DreamDirective Schema

The contract between the upstream engine and the stage. Intentionally
aligned to what FrameProcessor and ControlBus already process.

```json
{
  "cycle_id": "2026-03-14T03:12:45.123Z#000412",

  "engine": {
    "mode": "exploring",
    "level": "director",
    "active_concern": {
      "type": "tension_deficit",
      "intensity": 0.72,
      "source": "s1_seeing_through"
    },
    "operator": "build",
    "context_id": "cx-19",
    "assumption_patch": "what if tension escalates through recall",
    "active_indices": ["situation:s1", "operator:build", "pressure:negative"],
    "retrieved": [{"episode_id": "ep-27", "marks": 2, "threshold": 2}],
    "commit_policy": {"kind": "defer", "requires_confirm": true}
  },

  "stage": {
    "prompts": [{"text": "...", "weight": 1.0}],
    "transition": null,
    "reset_cache": false,
    "_rcp_soft_transition": null,
    "_rcp_set_style": "noir-v3",
    "_rcp_world_state": {
      "setting": "Backstage hallway",
      "action": "hesitation",
      "characters": [{"name": "Puppet", "emotion": "anger", "intensity": 0.7}]
    },
    "base_seed": 123456789,
    "denoising_step_list": [999, 950, 900],
    "kv_cache_attention_bias": 0.35
  }
}
```

**Key design decision:** The `engine` block is for logging, debugging,
and trace replay. The `stage` block is the only part Scope must
understand. All keys in `stage` map to existing FrameProcessor update
paths. No Mueller/pressure-engine concepts leak into scope-drd.

### What changed from the original proposal

The original deep research report used `"dreamer"` as the key name
and `"goal_type": "reversal"` as the selection vocabulary. The
pressure engine reframe changes:

- `dreamer` → `engine` (level-agnostic)
- `selected_goal` → `active_concern` + `operator` (pressure-driven)
- `goal_type` → `operator` (pluggable, not Mueller-fixed)
- Added `level`, `assumption_patch` (provenance)

The `stage` block is unchanged. That's the point — the stage
contract is stable regardless of what drives decisions.

---

## Integration Topology

```
+------------------------------------------+
|  Upstream Engine (latent-dreamer)         |
|                                          |
|  Concern scheduling                      |
|  Operator selection + execution          |
|  Context forking + backtracking          |
|  Episodic memory + retrieval             |
|  Proposal scoring + commit policy        |
|                                          |
|  Output: DreamDirective (engine + stage) |
+------------------+-----------------------+
                   |
                   | REST POST /api/v1/realtime/parameters
                   | (one directive per chunk boundary)
                   |
+------------------v-----------------------+
|  Stage (scope-drd)                       |
|                                          |
|  ControlBus (ordering)                   |
|  FrameProcessor (mailbox merge + apply)  |
|  WorldState → Compiler → Prompt          |
|  Pipeline (video generation)             |
|  SessionRecorder (timeline export)       |
|                                          |
|  Input: stage block of DreamDirective    |
+------------------------------------------+
```

### What the performer steers

In **batch mode:** the engine runs autonomously, emitting directives.
The DreamDirective stream is the session artifact.

In **conducted mode:** the APC Mini performer can:
- Override concern priorities (faders → situation activation weights)
- Force operator selection (buttons → trigger specific operators)
- Modulate stage parameters directly (knobs → tension, energy, dwell)
- Trigger commits (button → realize a hypothetical)

The conductor's inputs enter the same DreamDirective pipeline. The
engine and the conductor are parallel control sources that must not
both drive the same parameters simultaneously — the ControlBus
ordering handles this at the stage level, but arbitration policy
(who wins when both want to act) is an engine-level concern.

---

## Commit Policy Options

### Option A: Manual commit (V1, recommended)

- Engine emits candidate proposals as directives with
  `commit_policy: {kind: "defer", requires_confirm: true}`
- Stage always renders whatever the directive says
- Canonical world state in the engine only changes on explicit
  operator action (MIDI button, UI toggle, or commit endpoint)
- Safest for live performance — prevents runaway canon drift

### Option B: Threshold commit (Mueller-flavored)

- A proposal becomes commit-eligible after repeated retrieval or
  repeated selection (same proposal appears in K contexts or is
  selected N times without backtracking)
- Mirrors coincidence-counting flavor of episodic retrieval
- Semi-automatic — feels more like "the system is deciding"

### Option C: Outcome-based commit (goal termination)

- Commit happens only when a concern resolves (or an operator
  terminates as succeeded)
- Commit payload derived from the resolution context
- Most Mueller-faithful
- Best for specific operator types: rationalization success commits
  "emotional reinterpretation," reversal success commits
  "counterfactual admitted"

V1: Option A. V2: Option C for specific operators, Option A as
fallback for everything else.

---

## Semantic Mismatch Risks

These are the actual integration hazards, independent of the
reframe.

### 1. Cognitive context ≠ diffusion branch

Mueller/pressure-engine contexts are symbolic inherited fact sets.
Scope branching (snapshot/restore, seed offsets, transition bias) is
generator-state and output continuity.

**Rule:** Do not map every engine context fork to a stage snapshot.
Most contexts are purely symbolic. Only create stage snapshots at
explicit "audition points" (when the engine intends to visually
compare alternatives). Keep snapshot count within the LRU budget
(max 10).

### 2. Planner step ≠ stage step

The engine runs on its own cycle (concern scheduling, operator
execution, backtracking). The stage runs on chunk boundaries
(~0.5-2 seconds depending on pipeline config).

**Rule:** One directive per chunk boundary. The engine's internal
cycle can be faster or slower — the directive is the synchronization
point.

### 3. "Commit" means different things

In the engine: promoting a hypothetical to canonical state (ontic
commit). In the stage: the prompt/world-state is always whatever
the latest directive says — the stage doesn't know or care about
canonical vs. hypothetical.

**Rule:** Commit semantics live entirely in the engine. The stage
is stateless with respect to canonicity. It renders whatever it's
told. The engine tracks what's canonical.

### 4. Material substrate drift

Notes reference palette/daydream assets not present in scope-drd's
content tree. The dream graph and the stage's content directory are
separate concerns.

**Rule:** Treat the graph as an engine-side asset. The stage only
needs prompts, style references, and world-state patches. The
engine's planner adapter translates graph nodes into stage-ready
directives.

---

## Required Changes Per Repo (V1)

### latent-dreamer

| Change | Effort | Notes |
|--------|--------|-------|
| Define DreamDirective schema (JSON + EDN) | Low | `engine` block carries pressure-engine state; `stage` block carries FrameProcessor params |
| Build directive exporter from engine state | Medium | Maps concern + operator + context → engine block; maps node + situation → stage block |
| Implement REST client for scope-drd | Low | POST to /api/v1/realtime/parameters; stage block only |
| Planner adapter: engine state → stage directive | Medium | Initially heuristic; later LLM-assisted offline |

### scope-drd

| Change | Effort | Notes |
|--------|--------|-------|
| None required for V1 | — | Existing REST endpoints and FrameProcessor update paths are sufficient |
| Optional: expose snapshot response in REST ack | Low | Lets engine know snapshot ID for later restore |

---

## V1 → V2 → V3 Roadmap

### V1: Thin integration (one stream, deterministic stage control)

- Engine runs one concern/operator loop
- Emits one DreamDirective per chunk boundary
- Stage renders it via existing FrameProcessor path
- Manual commit policy
- Prove: does the loop produce visible, non-random stage changes?

### V2: Hypothetical contexts as stage branches

- Map engine context IDs to stage snapshots at audition points
- Add commit policy enforcement (defer vs. commit)
- Replay and comparison tooling
- Prove: does forking/backtracking produce visually distinct
  alternatives?

### V3: Per-character streams (only if needed)

- Phase A: single engine, multi-character concern state, one unified
  directive per chunk
- Phase B: per-character context trees, conductor policy selects
  which character's directive controls the stage
- Phase C: multiple Scope sessions (only if compute and UX support
  it)

---

## Relationship to Other Reframe Docs

- `01-reframe-summary.md` — defines the three-level pressure engine
  architecture. This doc specifies how level 3 (director traversal)
  connects to the stage.
- `02-state-model.md` — defines Concern, Context, Proposal,
  CommitRecord. The DreamDirective is the serialized output of a
  Proposal + CommitRecord, translated for the stage.
- `03-questions-for-5pro.md` — question 6 asks how level 3 operators
  map into the playback contract. This doc provides the concrete
  answer from the stage side.
- `04-kernel-gap-analysis.md` — maps kernel modules to the pressure
  engine model. The "directive exporter" is the new module that
  bridges kernel output to the DreamDirective schema.
