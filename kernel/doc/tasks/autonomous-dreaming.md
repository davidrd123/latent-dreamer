# Task: Fixture-Driven Autonomous Trace Against Puppet Knows

## Goal

Build the minimum adapter/glue needed for the existing kernel to run
fixture-driven cycles without scripted cycle inputs, and emit readable
terminal + HTML traces.

Do not build a new planner. Do not put file I/O in the pure kernel.
Promote the existing semi-unscripted benchmark into an autonomous run.

## Starting Point

The semi-unscripted benchmark (`benchmarks/puppet_knows.clj`,
`run-semi-unscripted-benchmark`) is already close. It uses real
REVERSAL/ROVING primitives and auto-activated family plans. What it
still gets from scripts: situations, active indices, retrievals,
chosen nodes, feedback.

Existing HTML outputs already work:
- `out/puppet_knows_semi_unscripted.html`
- `out/puppet_knows_compare.html`

The new autonomous run should plug into the same flow.

## What To Build

### 1. Fixture adapter (edge, not core)

**Location:** `src/daydreamer/benchmarks/` or a new `src/daydreamer/adapters/`
namespace — NOT in the pure kernel namespaces (context, control, goals,
episodic_memory, goal_families).

Load `scope-drd/content/daydreams/puppet_knows/world.yaml` and
`scope-drd/content/daydreams/puppet_knows/dream_graph.json`.

Translate into kernel data:
- situations (indices, activation, ripeness, emotion fields)
- graph nodes as retrievable candidates
- places, characters, events as reference data

File I/O stays at this edge. The kernel receives plain maps.

### 2. Autonomous goal activation (adapter layer)

Each cycle, derive top-level goal pressure from situation state — not
from scripted cycle entries. The seven goal types compete based on
situation emotions:
- high anger → REVENGE candidate
- high threat → REHEARSAL candidate
- high hope → RECOVERY candidate
- high grief → RATIONALIZATION candidate
- low activation across the board → ROVING candidate
- etc.

Create/update goals in the kernel's goal map so `control/run-cycle`
can select among them by strength. Model this after the Python
engine's `compute_goal_candidates()` in `daydream_engine.py`.

This is a conducted-system adapter, not Mueller core logic. Keep it
clearly separated.

### 3. Autonomous retrieval/projection (adapter layer)

Each cycle, retrieve against the dream graph nodes using current
active indices. Match node indices against active indices, count
hits, apply threshold. Return candidate nodes ranked by retrieval
score. This replaces the scripted `:retrievals` and `:chosen-node-id`.

Use the existing `episodic_memory.clj` threshold-counting mechanism
where possible, but keep graph-specific retrieval in the adapter.

### 4. Autonomous runner

Wire the above into a loop: decay → activate goals → select goal →
retrieve nodes → choose best node → update state → trace.

**Two output modes:**
- Reporter-compatible trace (for the HTML viewer, same format as
  `trace/reporter-log`) — plugs into the same `bb` tasks and HTML
  reporter as existing benchmarks
- Human-readable terminal summary per cycle:
  ```
  Cycle 3 | REHEARSAL × s2_the_mission | n03_mission_walk
    tension: 0.52  energy: 0.54  hold: false
    retrieved: n04_spotlight_choices (3 hits), n13_countdown_clock (2 hits)
    sprouted: cx-7 (ordering 0.9)
    mode: daydreaming
  ```

Add a `bb` task: `bb dream` that runs 12 cycles against Puppet Knows
and prints the summary + generates HTML.

## Constraints

- **Pure kernel, impure edges.** No file I/O, YAML parsing, or
  fixture-specific logic in context.clj, control.clj, goals.clj,
  episodic_memory.clj, or goal_families.clj.
- Use/extend existing `control/run-cycle`, don't replace it
- Trace output compatible with `daydreamer.trace/reporter-log`
- `bb check` passes after every change
- runner.clj is a harness, not the missing planner — respect that
  boundary

## Success Criteria

- `bb test` passes
- `bb dream` runs 12 cycles against Puppet Knows with no scripted
  inputs
- The trace shows goal competition, situation switching, and at least
  one REVERSAL or ROVING episode
- The terminal output is readable enough to follow the dream's logic
  cycle by cycle
- An HTML report is generated in `out/` viewable in the same format
  as existing benchmark reports
- The trace exports through the same HTML reporter path as
  `puppet_knows_semi_unscripted.html`

## Read First

- `kernel/AGENTS.md`
- `kernel/doc/reference/agent_guardrails.md`
- `kernel/src/daydreamer/runner.clj`
- `kernel/src/daydreamer/control.clj`
- `kernel/src/daydreamer/benchmarks/puppet_knows.clj`
- `kernel/src/daydreamer/benchmarks/puppet_knows_adapter.clj`
- `scope-drd/content/daydreams/puppet_knows/world.yaml`
- `scope-drd/content/daydreams/puppet_knows/dream_graph.json`
- `scope-drd/tools/daydream_engine.py` (for goal activation logic)
