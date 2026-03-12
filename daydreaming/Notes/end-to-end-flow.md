# End-to-End Flow

How world state becomes a dream, and how a dream can change the world.

## The Loop

```
 ┌─────────────────────────────────────────────────────────┐
 │                     WORLD STATE                         │
 │  characters, places, situations (ripeness + activation) │
 │  persists across sessions, only changes via committed   │
 │  events                                                 │
 └────────────┬────────────────────────────▲───────────────┘
              │                            │
         unresolved                   event commit
         situations                   (explicit, rare)
              │                            │
              ▼                            │
 ┌────────────────────────────────┐        │
 │       DAYDREAM ENGINE          │        │
 │  (offline, Mueller-inspired)   │        │
 │                                │        │
 │  1. read current world state   │        │
 │  2. identify unresolved        │        │
 │     situations                 │        │
 │  3. activate competing goal    │        │
 │     types based on emotional / │        │
 │     situational pressure       │        │
 │  4. retrieve episodes by       │        │
 │     coincidence counting       │        │
 │  5. propose candidate scenes   │        │
 │  6. mutate if stuck            │        │
 │  7. emit DreamNodes +          │        │
 │     DreamEdges                 │        │
 └────────────┬───────────────────┘        │
              │                            │
         dream graph                       │
              │                            │
              ▼                            │
 ┌────────────────────────────────┐        │
 │       DREAM GRAPH              │        │
 │  nodes: scene units with       │        │
 │    mood, palette ref, stage    │        │
 │    config, audio config,       │        │
 │    world compatibility flag    │        │
 │  edges: connections with kind, │        │
 │    weight, situation alignment │        │
 └────────────┬───────────────────┘        │
              │                            │
         loaded into                       │
              │                            │
              ▼                            │
 ┌────────────────────────────────┐        │
 │       TRAVERSAL RUNTIME        │        │
 │  (real-time, Python)           │        │
 │                                │        │
 │  1. track situation activation │        │
 │     and hold states            │        │
 │  2. choose next edge:          │        │
 │     - local continuity (drift) │        │
 │     - return pressure (pull    │        │
 │       toward activated         │        │
 │       situation regions)       │        │
 │     - hold (dwell when         │        │
 │       multiple situations      │        │
 │       active)                  │        │
 │     - counterfactual jump      │        │
 │       (rare, marked)           │        │
 │  3. detect event candidates    │        │
 │     via repeated approach      │        │
 │  4. commit events explicitly   │────────┘
 │  5. drive stage outputs        │
 └────────────┬───────────────────┘
              │
         stage directives
              │
              ▼
 ┌────────────────────────────────┐
 │       STAGE                    │
 │                                │
 │  Scope: video prompt updates,  │
 │    soft/hard cuts, parameter   │
 │    changes                     │
 │  Lyria: music prompt updates,  │
 │    instrument/production       │
 │    layering, context resets    │
 │  Narration: captions or TTS    │
 │    (optional)                  │
 └────────────────────────────────┘
```

## What Each Layer Does (And Doesn't Do)

### World State

Holds the canonical truth. Small: a few characters, a few places, a handful of
situations. Each situation has:

- **ripeness**: slow-moving, cross-session. How close is this situation to
  mattering? Rises over sessions, not within them.
- **activation**: fast-moving, within-session. How present is this situation
  right now? Rises and falls as traversal touches related nodes.

World state only changes through explicit event commits. Dreams cannot
silently alter canon. This is the firewall between imagination and fact.

### Daydream Engine

The Mueller-inspired core. Its job is to take unresolved situations and produce
a graph of imagined scenes worth traversing. It does not produce finished
narrative. It produces a branching possibility space.

What Mueller contributes here:

- **Goal-type selection.** Not "imagine something." Instead: given this
  unresolved situation and the emotional pressure around it, which *kind* of
  imagining? Rehearsal (practice a pending thing), reversal (explore a
  different outcome), rationalization (reframe a failure), roving (drift to
  something pleasant), recovery (find how to continue), revenge (directed
  grievance), repercussions (project consequences forward).

- **Competing activation.** Multiple goal types activate simultaneously from the
  same situation. The engine doesn't pick one — it lets them compete by
  strength. The scheduler oscillates. This is where obsessive return and
  involuntary topic-switching come from.

- **Coincidence retrieval.** Episodes (past scenes, past graph fragments) are
  indexed under multiple cues. When enough cues converge on the same episode,
  it surfaces. This is associative recall, not nearest-neighbor search.
  Serendipity lowers the threshold by one — "almost enough reasons became
  enough."

- **Mutation on stuckness.** When a daydream path dead-ends (all possibilities
  fail), the engine swaps one element and checks whether the new version
  connects via serendipity. Structured, not random.

What Mueller does *not* contribute:

- The specific content of scenes (that comes from palettes and world material)
- The graph structure that the audience traverses (that's the DreamGraph)
- Any video/audio/narration decisions (that's the stage contract)

### Dream Graph

The bridge between engine and runtime. Pure data. Each node carries:

- A palette cell reference (the visual/audio seed)
- Goal type and tension/energy metadata
- World compatibility (present-compatible vs. counterfactual)
- Stage hints (transition type, Scope params, Lyria config)
- Optional narration

Each edge carries:

- Kind (drift, return, jump, hold)
- Weight
- Situation alignment (which situation this edge relates to)

The graph is prepared offline. The runtime doesn't generate new nodes — it
walks the graph that was built.

### Traversal Runtime

Real-time. Loads a dream graph and walks it, making moment-to-moment decisions
about which edge to follow. Four behaviors:

1. **Drift** — default. Follow local continuity. Neighboring nodes, gentle
   transitions.
2. **Return** — when a situation's activation rises, probability increases for
   edges leading back into that situation's region. This creates obsessive
   circling.
3. **Hold** — when multiple situations are active simultaneously, dwell longer
   on nodes that sit at their intersection. Charged stillness.
4. **Counterfactual jump** — rare (5-10%). Jump to a far node marked
   counterfactual. "What if" intrusions.

The runtime also tracks event candidates. Some nodes are flagged as potential
world-state changes. If traversal repeatedly approaches one (not just visits
once), the event becomes eligible for commitment. Commitment is explicit — it
writes back to world state and triggers post-event repair (retagging affected
nodes/edges).

### Stage

Translates traversal decisions into actual media output:

- **Scope**: receives prompt updates and transition directives (soft dissolve vs.
  hard cut). The palette cell reference on each node maps to a prompt the
  Scope system already knows how to render.
- **Lyria**: receives music prompt updates with instrument/production layering.
  Context resets when the emotional landscape shifts sharply.
- **Narration** (optional): captions rendered as text overlay, or TTS queued for
  voice-over. Templates keyed by goal type give the inner-monologue feel.

## What "Feeling Like Dreaming" Actually Means In This System

The pipeline is engineered to produce specific phenomenological qualities:

- **Drift and return.** You wander, then circle back. Same situation, slightly
  different angle. This comes from activation pressure + local continuity.
- **Repetition with variation.** The same scene region gets revisited but through
  different goal types. Rehearsal asks "what if I try again?" Reversal asks
  "what if it had gone differently?" Same material, different angle.
- **Involuntary intrusion.** Counterfactual jumps are rare but disorienting.
  They feel like the mind suddenly going somewhere unbidden.
- **Obsessive charge.** When situations are ripe and activated, the graph pulls
  traversal toward them. You can't escape what's unresolved.
- **Surprise.** Coincidence retrieval surfaces episodes that weren't directly
  sought. Serendipity connects things that almost connect. This is the "oh,
  that reminds me of—" moment.
- **Gradual stakes.** Events don't commit on first pass. Repeated approach
  creates a slow sense of inevitability before anything actually changes.

## What Exists vs. What Needs Building

### Exists now

- Mueller's source code (Common Lisp reference in `daydreamer/`)
- Design documents and adaptation specs (this directory)
- Scope video system (in `scope-drd`, separate repo)
- Lyria integration (existing, separate)
- Palette pipeline (v2 layered palettes exist)

### Needs building (in roughly this order)

1. **World fixture** — tiny authored world. 2-3 characters, 3 places, 3
   situations with ripeness/activation values.
2. **Hand-authored dream graph** — 20-30 nodes over existing palette cells.
   Demonstrates all edge kinds, situation alignment, at least one event
   candidate.
3. **Traversal harness** — loads graph, tracks activation, chooses edges,
   outputs stage directives. Can commit events.
4. **Stage translator** — maps node payloads to Scope REST calls + Lyria
   directives.
5. **Session logging** — records traversal path, activation history, event
   commits.
6. **Daydream engine v0** — replaces hand-authored graph with generated one.
   This is where Mueller's goal types, coincidence retrieval, and mutation
   enter as code.

Steps 1-5 can work with a hand-built graph and no engine. Step 6 is where
the Mueller adaptation actually runs as software. The documents recommend
this ordering because the hand-built version answers whether the traversal
*feels right* before investing in the generation machinery.
