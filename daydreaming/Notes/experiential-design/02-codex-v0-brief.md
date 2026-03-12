# Codex Brief: v0 Scheduler & Traversal Harness

Self-contained brief for building the mechanical v0. This track can proceed
independently of the creative/experiential exploration.

## What To Build

A Python program that:

1. Loads a world fixture (YAML) and a dream graph (JSON)
2. Runs a Mueller-inspired scheduler loop
3. Outputs a sequence of stage directives (DreamNodes with timing)
4. Writes a session log

The v0 does NOT need:
- An LLM
- A real connection to Scope or Lyria (output directives to stdout/file)
- Perfect scenarios or situations (use the escape_new_york palette)
- Narration generation (template placeholders are fine)
- Interior state visualization

## Where It Lives

`/Users/daviddickinson/Projects/Lora/scope-drd/tools/daydream_engine.py`

This follows the existing bridge pattern: `apc_mini_bridge.py` drives the stage
from MIDI events, `daydream_engine.py` drives the stage from a dream loop.

## Reference Documents

Read these before building:

- `daydream-round-03/daydreaming/Notes/focused-source-trace.md`
  (Mueller's actual mechanisms — the source of truth for what to adapt)
- `daydream-round-03/daydreaming/Notes/mueller-adaptation-working-spec.md`
  (what to port vs skip)
- `daydream-round-03/daydreaming/Notes/daydream-to-stage-contract.md`
  (DreamNode / DreamEdge / DreamGraph schemas)
- `daydream-round-03/daydreaming/Notes/end-to-end-flow.md`
  (top-level architecture)
- `daydream-round-03/daydreaming/Notes/round-03-starter-kit.md`
  (v0 material strategy)

For the stage API contracts:
- `scope-drd/tools/apc_mini_bridge.py` (the pattern to follow)
- `scope-drd/tools/lyria_session.py` (Lyria command queue)

## Data Structures

### World Fixture (input)

```yaml
# world.yaml
characters:
  - id: kai
    name: Kai
  - id: maren
    name: Maren

places:
  - id: harbor
  - id: checkpoint
  - id: underground

situations:
  - id: s1_escape
    description: "I need to get out, but the wall is everywhere."
    ripeness: 0.6
    activation: 0.3
  - id: s2_bargain
    description: "Someone offers safety, but it costs something."
    ripeness: 0.4
    activation: 0.1
  - id: s3_betrayal
    description: "A small sabotage changes what the whole place is for."
    ripeness: 0.3
    activation: 0.0
```

### Dream Graph (input)

Follows the `DreamGraph` schema from `daydream-to-stage-contract.md`. For v0,
hand-author 20-30 nodes over the escape_new_york palette. Each node references
a palette cell and carries goal_type, tension, energy, situation_ids.

### DreamNode (output)

See `daydream-to-stage-contract.md` for the full schema. The v0 engine emits
these as its output — one per traversal step.

## The Scheduler Loop (Adapted from Mueller)

From `focused-source-trace.md` sections 2-3:

```
every cycle:
    1. decay activations (factor ~0.95)
    2. decay emotions / tensions (factor ~0.95, GC threshold ~0.15)
    3. for each situation, compute competing goal strengths:
       - check trigger conditions for each of the 7 goal types
       - supplemental strengths create priority ordering:
         rationalization(0.06) > revenge(0.05) > roving(0.04) >
         reversal(0.03) > recovery(0.02) > rehearsal(0.01)
    4. select highest-strength goal (random tiebreak)
    5. retrieve episodes via coincidence counting:
       - compute active indices (situation, goal_type, emotion, place, recency)
       - count index overlaps per episode/node
       - surface when count >= threshold (or threshold-1 for serendipity)
    6. choose next node:
       - prefer nodes matching retrieved episodes + current goal type
       - prefer local continuity (nearby in graph) as default
       - return pressure: boost edges toward activated situation regions
       - hold: dwell longer when multiple situations highly active
       - counterfactual jump: 5-10% chance, mark as counterfactual
    7. if stuck (no good candidates for N cycles):
       - mutate: swap one element (situation, goal_type, place)
       - check if mutated variant connects via serendipity
    8. emit DreamNode as stage directive
    9. update state:
       - boost activation on visited situation
       - update recent_indices (FIFO, max 6)
       - update recent_nodes (for stuckness detection)
       - log to session
```

## What "Works" Means for v0

The v0 doesn't need to feel like a finished piece. It needs to demonstrate
five behaviors from `focused-source-trace.md` section 9:

1. Unresolved situations activate distinct daydream modes
2. The scheduler obsesses and switches based on changing goal strength
3. Recall happens through coincidence, not just nearest-neighbor
4. Stuck sequences mutate structurally
5. Counterfactual planning emits nodes without corrupting canon

A successful v0 produces a session log where you can read the traversal path
and say "yes, that feels like a mind circling, returning, drifting, getting
stuck and unstuck."

## Practical Notes

- Use `asyncio` to match the existing `apc_mini_bridge.py` pattern
- Cycle time: ~2-6 seconds per node (matches Scope chunk timing)
- For v0, emit to stdout as JSON lines. Real Scope/Lyria integration later.
- Tag each emitted node with the scheduler's internal state (which goal won,
  what activation levels were, why this node was chosen) for debugging
- The hand-authored dream graph is a separate deliverable. If it doesn't exist
  yet, the engine should be able to run on a minimal test graph (even 5-10
  nodes is enough to test the loop mechanics).
