# daydream-round-03

This repository is currently a design workspace for the **dream/narrative layer**.

The **stage layer** (real-time T2V + MIDI instrument + Lyria) already exists in the
previous-round codebase (`scope-drd`). Round 03 should stand on that renderer rather
than re-invent it.

It contains three distinct things:

1. `daydreamer/`
   Erik Mueller's original Common Lisp DAYDREAMER source and documentation. This is the architectural ancestor and reference implementation for the emotion-driven control loop, episodic retrieval, mutation, and context branching.

2. `daydreaming/Notes/`
   The active design work for the "conducted daydreaming" adaptation: architecture notes, companion notes, deep-research writeups, and exported Claude chat transcripts from March 12, 2026.

3. `patterns/`
   Currently empty scratch space.

## Start Here

If you are reopening this repo and need orientation, read in this order:

1. [`daydreaming/Notes/home-base.md`](daydreaming/Notes/home-base.md)
2. [`daydreaming/Notes/focused-source-trace.md`](daydreaming/Notes/focused-source-trace.md)
3. [`daydreaming/Notes/language-decision-memo.md`](daydreaming/Notes/language-decision-memo.md)
4. [`daydreaming/Notes/daydream-to-stage-contract.md`](daydreaming/Notes/daydream-to-stage-contract.md)
5. [`daydreaming/Notes/ProspectiveDesign/v2/conducted-daydreaming-architecture-v2.md`](daydreaming/Notes/ProspectiveDesign/v2/conducted-daydreaming-architecture-v2.md)
6. [`daydreaming/Notes/ProspectiveDesign/chats/2026-03-12_01-01-48_Claude_Chat_Daydreamer_architecture_split_the_three_systems.md`](daydreaming/Notes/ProspectiveDesign/chats/2026-03-12_01-01-48_Claude_Chat_Daydreamer_architecture_split_the_three_systems.md)
7. [`daydreaming/Notes/ProspectiveDesign/chats/2026-03-12_01-01-16_Claude_Chat_Building_computational_daydreaming_models_today.md`](daydreaming/Notes/ProspectiveDesign/chats/2026-03-12_01-01-16_Claude_Chat_Building_computational_daydreaming_models_today.md)
8. [`daydreamer/README.md`](daydreamer/README.md)

## Current Canon

The compressed architecture in `v2` is the most useful single spec in the repo right now.

- Treat `v2/conducted-daydreaming-architecture-v2.md` as the current architectural baseline.
- Treat the `v1` docs and exported chats as design lineage and source material.
- Treat `daydreamer/` as reference code to port ideas from, not as the product codebase.

## Current State

- This repo does not contain the Scope renderer, palette pipeline, or Lyria runtime.
- The repo does not yet contain a world bible fixture, dream graph fixture, or traversal runtime.
- Several appendix-referenced notes mentioned in the design docs live in the previous round (`scope-drd`) rather than here.

## What Seems Stable

- The base product is a fictional persistent audiovisual world first.
- Autobiographical vault mode is deferred.
- Biometric input is deferred and should modulate traversal, not unilaterally choose irreversible events.
- The core split is offline generation -> graph -> real-time traversal -> video/audio rendering.
- Canonical world state and counterfactual daydream state must stay separate.

## Immediate Next Slice

The next concrete build slice in *this* repo should not be "full engine." It should be:

1. a tiny authored world bible (or a minimal situation set over an existing palette),
2. a hand-authored dream graph that references palette cells,
3. a traversal harness that outputs Scope REST + Lyria directives (or a replayable timeline),
4. a session log format,
5. optional narration (captions first; voice later).

## Previous Round (Stage Layer)

Round 02 lives in `scope-drd` and already includes:

- Scope real-time video stage + control surface (`video-cli`, REST endpoints)
- `.palette.yaml` pipeline (64 prompts) + enrichment scripts for Lyria prompts
- APC Mini MK2 bridge + Lyria RealTime integration notes and reference docs
