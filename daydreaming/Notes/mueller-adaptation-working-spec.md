# Mueller DAYDREAMER Adaptation (Working Spec)

This is a living document that translates the **actual** DAYDREAMER code
(Mueller) into a minimal, buildable engine that can drive your **existing stage**
(Scope real-time video + Lyria + MIDI).

It is not a rehash of the long v1/v2 notes. The goal is to make the port
operational: data structures + loop + what-to-port vs what-to-skip.

Companion docs in this repo:

- `daydreaming/Notes/language-decision-memo.md`
- `daydreaming/Notes/focused-source-trace.md`
- `daydreaming/Notes/daydream-to-stage-contract.md`
- `daydreaming/Notes/round-03-starter-kit.md`

Primary source files in this repo (`daydreamer/`):

- Control loop: `dd_cntrl.cl`
- Daydream goal types + rules: `dd_kb.cl`
- Episodic threshold retrieval: `dd_epis.cl`
- Serendipity / rule intersection: `dd_ri.cl`
- Context tree: `gate_cx.cl`
- Mutation: `dd_mutation.cl`

## 1. What Mueller Actually Built (Code Facts)

### 1.1 The top-level scheduler is a tight loop

In `dd_cntrl.cl`, `daydreamer-control0` is literally:

- decay needs (`need-decay`)
- decay emotions (`emotion-decay`)
- pick most motivated runable top-level goals (`most-highly-motivated-goals`)
- run one planning step for the chosen goal (`daydreamer-control1`)
- switch between `performance` and `daydreaming` mode when no goals remain

Key detail: motivation is stored as the goal’s own `strength`. The scheduler
does not require a big “conductor vector” to work.

### 1.2 Daydream goal types are first-class goal objects

In `dd_kb.cl`, Mueller defines the seven goal-object types as types:

- `RATIONALIZATION`, `ROVING`, `REVENGE` (fanciful)
- `RECOVERY`, `REHEARSAL`, `REVERSAL`, `REPERCUSSIONS` (realistic)

These aren’t just labels; they are the control system’s actual “how to think”
switch. The engine generates a different kind of scene depending on which goal
type is active.

### 1.3 Episodic memory is coincidence counting (threshold retrieval)

In `dd_epis.cl`, retrieval works by:

- storing each episode under multiple indices
- marking episodes as indices become active
- retrieving episodes once `marks >= threshold`
- “serendipity” mode reduces threshold by 1 (as if an extra index existed)

This is not semantic similarity. It is “how many independent reasons converge on
the same stored thing right now?”

### 1.4 Contexts are a branching inheritance structure

In `gate_cx.cl`, `cx$sprout` creates a new context inheriting from parent:

- parent pointers + ancestor chain
- copy of asserted objects list (`all-obs`)
- metadata like `mutations-tried?`, `touched-facts`

Mueller uses this as the branching record of explored alternatives while
planning. This is the ancestor of the “counterfactual graph” idea, but the shape
is a planning decomposition tree.

### 1.5 Mutation is an explicit creativity mechanism

In `dd_mutation.cl`, mutation is not “increase temperature.” It:

- enumerates mutations of an action
- tries a mutated action
- checks if serendipity can make it pan out

For our port, we’ll translate mutation to “scene element mutation” when local
generation/traversal gets stuck.

## 2. Porting Stance (What We Are and Aren’t Doing)

### 2.1 What we port (v0)

- The *scheduler shape*: repeated short cycles with decay + motivated selection.
- The *seven goal types* as first-class scene/posture types.
- The *coincidence/threshold retrieval* layer (serendipity by threshold lowering).
- Mutation as a structured fallback when stuck.

### 2.2 What we skip (v0)

- GATE theorem proving/unification as implemented.
- The English generator (`dd_gen.cl`).
- Mueller’s full planning rulebase as-written.

We will replace the “planner/rules” step with:

- either a hand-authored graph step (no LLM)
- or an offline LLM “propose -> validate -> admit” step

The important part is to preserve the control logic: *how the system chooses
what kind of daydream to do next, and how memory resurfaces via coincidences*.

## 3. How This Sits On Top Of Material

DAYDREAMER needs “material” in two forms:

1. **World facts / constraints** (canon-ish state)
2. **Episodes/fragments** to retrieve and recombine

For Round 03, the fastest “episode store” you already have is:

- Palette cells: each cell is a renderable scene prompt + a music prompt
  (`scope-drd/content/palettes/*.palette.yaml`)

So in v0, material can be:

- `Episode = palette_cell`
- `Indices = tags derived from (situation, mood, character, place, goal_type, recent_history)`

You can later swap `Episode` to:

- world-bible fragments extracted from a film/book
- your own authored world bible
- personal vault notes (deferred)

The engine should not care as long as “episodes have indices” and “scenes can be
emitted.”

## 4. The Minimal Adapted Engine (v0)

### 4.1 State

Keep the state small and explicit:

- `needs`: optional for v0 (can be stubbed)
- `situations`: `{id, ripeness, activation}`
- `emotions`: a few scalars (or “overall valence” + “arousal”)
- `top_level_goals`: derived from situations + emotion triggers
- `recent_indices`: rolling list (Mueller caps these)
- `recent_nodes`: rolling window for stuckness detection

### 4.2 Active Indices (coincidence layer)

At each cycle, compute a set of active indices like:

- `situation:s1_escape`
- `goal_type:rehearsal`
- `emotion:neg` / `emotion:pos`
- `place:harbor` / `motif:rain` (if present in material)
- `recent:episode:<id>` (recency effects)

Retrieval is then:

- count overlaps for each episode
- surface if `count >= threshold` (or `>= threshold-1` in serendipity mode)

### 4.3 Goal Type Selection

Start with a small trigger policy:

- high negative + failure memory -> `rationalization` or `reversal`
- high tension + high ripeness -> `rehearsal` or `repercussions`
- directed anger -> `revenge`
- overload -> `roving`
- after failure with time passed -> `recovery`

You do not need perfect psychology; you need stable, testable switching.

### 4.4 One-cycle “planning step” (what replaces `run-rules`)

In Mueller, `daydreamer-control1` advances planning by one step and moves into a
sprouted context.

In our port, one “step” can be:

- choose a goal type
- retrieve 1–K episodes/fragments via threshold retrieval
- propose the next `DreamNode`:
  - simplest: pick a palette cell whose indices match the goal type/situation
  - richer: offline LLM writes a node that *references* palette cells
- admit node into graph / timeline
- update activation + recent indices + stuckness metrics

## 5. How It Drives The Stage

The engine should emit `DreamNode`s conforming to:

- `daydreaming/Notes/daydream-to-stage-contract.md`

Key point: the node should be renderable without an LLM during performance.

For v0, narration should be:

- captions generated from goal type templates
- or offline LLM one-liners per node (not in the real-time loop)

## 6. Concrete Next Deliverable (to avoid “design loop”)

Produce an executable “ugly v0” with no LLM:

- Pick 1 palette as the episode store.
- Hand-tag regions/cells to 3 situations.
- Implement:
  - goal-type switching
  - threshold retrieval
  - mutation when stuck (swap region / goal type)
  - node emission referencing palette cells
  - caption templates by goal type
- Output: a dream graph/timeline + a session log.

Only after that feels like “return/drift/hold” do we spend money on LLM calls.
