# Home Base

This note is the repo-level orientation layer that was missing from the workspace. It summarizes what is here, what looks solid, what is still speculative, and what the next buildable slice should be.

## One-Sentence Read

The real project hiding in these notes is: a persistent audiovisual daydream system where an offline engine prepares a branching scene graph around unresolved situations, and a real-time runtime traverses that graph into video and Lyria-driven audio while canonical world state only changes at explicit event commits.

## What Is Actually In This Repo

### 1. Original DAYDREAMER source

`daydreamer/` is the original Mueller codebase and should be treated as architectural source material, not as the starting point for a direct product implementation.

The most relevant files for adaptation are:

- `daydreamer/dd_cntrl.cl`
  The emotion-driven control loop. `daydreamer-control0` is the clearest ancestor of the generation scheduler.
- `daydreamer/dd_epis.cl`
  Episodic memory, threshold retrieval, and serendipity logic.
- `daydreamer/dd_mutation.cl`
  Mutation as a structured expansion mechanism rather than random temperature.
- `daydreamer/gate_cx.cl`
  Context sprouting and inherited branching state, which is the conceptual ancestor of the new graph/counterfactual layer.
- `daydreamer/dd_reversal.cl`
  Alternative-outcome branching and emotional carry-over.

### 2. Active design notes

`daydreaming/Notes/ProspectiveDesign/` contains the adaptation work.

- `v2/conducted-daydreaming-architecture-v2.md`
  Best current single document. This is the closest thing to a canonical spec.
- `v1/conducted-daydreaming-architecture.md`
  Longer and more exploratory. Still useful for translator examples and build-order detail.
- `v1/conducted-daydreaming-interaction.md`
  The strongest material on world state, counterfactual rendering, and event commitment.
- `v1/conducted-daydreaming-hardware.md`
  Useful later, but mostly deferred for now.

### 3. Chat artifacts

The exported Claude chats on March 12, 2026 are not noise. They contain a lot of the narrowing moves that the shorter `v2` doc now depends on.

- `chats/2026-03-12_01-01-48_Claude_Chat_Daydreamer_architecture_split_the_three_systems.md`
  Best "what should be cut, split, or deferred" artifact.
- `chats/2026-03-12_01-01-16_Claude_Chat_Building_computational_daydreaming_models_today.md`
  Best source for the prompt-state bridge between scene traversal and dual video/audio rendering.

The current source-grounded correction to those chat summaries is:

- `daydreaming/Notes/focused-source-trace.md`

### 4. Research note

- `daydreaming/Notes/DeepResearch/2026-03-11/bidirectional_knowledge_text_coupling.md`
  Interesting, but peripheral to the current build. Keep as background, not as a blocking dependency.

## What Is In The Previous Round (Real Stage Layer)

Round 02 already built a real, working stage layer in `scope-drd`:

- Scope real-time video (prompt transitions, LoRA blending, depth conditioning)
- Hardware control surface (APC Mini MK2, faders + pads)
- Palette system (`.palette.yaml` + `.txt`) with generation + enrichment scripts
- Lyria RealTime integration strategy (weighted prompts + config mapping)

This round's work should not rebuild the renderer. It should emit a dream graph
and/or timeline in a form the stage layer can consume.

Current runtime picture:

- `tools/apc_mini_bridge.py` is the current live performance instrument.
- `tools/daydream_engine.py` is the scheduler and trace engine.
- `tools/daydream_engine.py --live-stage` now provides the missing direct
  engine-to-stage adapter, so the engine can drive Scope and local Lyria
  without routing through the APC bridge.

The contract and run modes for those three paths are captured in:

- `daydreaming/Notes/daydream-to-stage-contract.md`

The most relevant "single sources of truth" in `scope-drd` are:

- `/Users/daviddickinson/Projects/Lora/scope-drd/notes/daydream/self_docs/project_page_v2/2026-02-23-draft.md`
- `/Users/daviddickinson/Projects/Lora/scope-drd/notes/daydream/self_docs/midway_point/demo_cheatsheet.md`
- `/Users/daviddickinson/Projects/Lora/scope-drd/notes/architecture/final-push/beyond/proposals/001-apc-mini-mk2-bridge/phase-2-lyria-music/lyria-reference.md`
- `/Users/daviddickinson/Projects/Lora/scope-drd/notes/architecture/final-push/beyond/proposals/001-apc-mini-mk2-bridge/phase-2-lyria-music/palette-pipeline-reference.md`

For the current Python-vs-Clojure sequencing decision, use:

- `daydreaming/Notes/language-decision-memo.md`

## What Seems Most Solid

These decisions show up repeatedly across the artifacts and should be treated as current defaults unless there is a strong reason to reopen them.

### Product focus

Start with a fictional persistent world. Do not make the MVP juggle fictional canon, autobiographical vault ingestion, and high-authority biometric control at the same time.

### Core pipeline

The cleanest and most workable shape is:

`material -> offline dreaming -> dream graph -> real-time traversal -> Scope (video) + Lyria (audio) (+ optional narration)`

This is the strongest decision in the repo because it converts a vague "live AI daydream" idea into something debuggable and staged.

### State split

The separation between canonical world state and ephemeral daydream state is the key structural move.

- World state persists and mutates through events.
- Daydream state can branch, revisit, and contradict canon.
- Rendering treatment communicates the difference.

Without that split, the project collapses into either mood video or messy game logic.

### Event model

Irreversible state changes should not be selected by raw biometric salience. The most stable v1/v2 position is:

- event candidates surface through traversal,
- repeated approach matters,
- explicit confirmation commits,
- post-event graph repair retags stale material and biases traversal into aftermath.

### Biometrics

Deferred for the base product, and when they return they should modulate emphasis, pacing, hold, or weighting. They should not get unilateral authority over major commits.

### Language boundary

Nothing in this repo forces the implementation language yet. The strongest architectural boundary is process-level, not language-level:

- symbolic/offline engine on one side,
- media/runtime traversal on the other,
- graph and session state as the interchange seam.

The notes lean toward Python for runtime/media and leave the engine open to Python or Clojure.

Current recommendation:

- Python for the first behavioral `v0`
- pure data boundaries between engine and stage
- Clojure spike only after `v0` exposes the true symbolic pain points

## What Is Still Missing

This repo has a lot of concept and almost no build substrate yet.

### Missing concrete artifacts

- No world bible fixture (or "situation set" over an existing palette)
- No standalone dream graph fixture
- No dream graph schema file
- No event schema file
- No session log schema
- No traversal harness that targets Scope+Lyria
- No narration strategy beyond concept notes

### Missing source documents

Some appendices in the design notes mention files that are not present in this repo. Many of them exist in Round 02 under `scope-drd`, including:

- `instrument-stack-architecture.md`
- `narrative-conducting.md`
- `generative-steering.md`
- `conductor-landscape.md`
- `ims-exploration-notes.md`

This repo still cannot rely on those as *local* dependencies. If a concept from one of those matters for Round 03, restate it in the docs here and/or link to its canonical location in `scope-drd`.

### Missing implementation discipline

A lot of the repo still speaks in evocative language where build-time language should exist instead.

The concepts that most need hard operational definitions are:

- `hold`
- `ripeness`
- `activation`
- `attractor`
- graph edge weighting
- post-event repair rules
- prompt translator contracts

`v2` improves this a lot, but those are still the spots most likely to drift.

## Blunt Assessment

The repo is not directionless, but it is over-distributed.

The good news:

- the actual medium thesis is strong,
- the prep/performance split is real,
- the world-state/counterfactual split is real,
- the prompt-state bridge to video plus Lyria is real,
- the legacy DAYDREAMER mapping is not cosmetic.

The problem:

- too much of the important reasoning is trapped in chats,
- several documents are companions to documents that are missing,
- there is still no agreed tiny test fixture that forces the architecture to become executable.

So the right read is not "this is a mess." The right read is "the concept has converged enough that the next bottleneck is lack of a small concrete build target."

## Recommended Next Slice

Do not jump straight to the offline generator.

The next useful move is a hand-authored vertical slice that proves the runtime shape before any LLM orchestration exists. The stage already works; the missing piece is "a thing that can dream coherently" and emit a score the stage can play.

### Stage A: tiny fictional fixture

Create one minimal world with:

- 2 characters
- 3 places
- 3 unresolved situations
- 4 to 6 events

The Kai/Maren harbor-letter material already sketched in the notes is good enough for this.

### Stage B: hand-authored graph

Build a graph with:

- 20 to 30 nodes
- clear node kinds
- clear compatibility statuses
- loops
- at least one event candidate and one aftermath region

The point is to prove that traversal feels like circling, avoiding, and returning rather than random wandering or slideshow playback.

### Stage C: one runtime harness

Implement only enough runtime to:

- load material (palette(s) + minimal situation set),
- load dream graph,
- track activation and hold,
- choose edges,
- optionally commit one event,
- optionally retag stale nodes,
- write a session log.

No LLM yet.

### Stage D: one translator seam

Use the existing palette pipeline + Lyria reference conventions, and define a single node payload that can drive:

- Scope prompt + transition endpoints
- Lyria weighted prompts + config (with reset rules)
- optional narration (caption first)

## Suggested Working Canon

If you keep working in this repo, use this hierarchy:

1. `daydreaming/Notes/home-base.md`
2. `daydreaming/Notes/ProspectiveDesign/v2/conducted-daydreaming-architecture-v2.md`
3. `daydreaming/Notes/ProspectiveDesign/v1/conducted-daydreaming-interaction.md`
4. The two March 12, 2026 chat exports
5. Selected files from `daydreamer/`

Anything else is background unless it gets promoted into those layers.

## If You Reopen This Cold

Read `home-base.md`, then `v2`, then decide only one thing:

What is the smallest authored world that can support a first hand-built graph?

That is the current forcing function.
