---
status: active
owner: davidrd
last_updated: 2025-09-07
tags: [graffito, context, film, documentation]
---

# Graffito – Project Context Guide

TL;DR
- Short film in a mixed‑media stop‑motion style (painted paper cutouts + photographic faces + thick impasto paint) following Tony, Monk, and Grandma.
- This folder aggregates the creative source: script and shot plans, style language, captioning/training materials, and iterative feedback.
- Goal: develop a coherent visual language and repeatable workflow to generate exploratory clips and, over time, stitch sequences that map to the script.

Table of Contents
- Project Overview
- Directory Map
- Creative Vision & Visual Language
- Story & Structure
- Shot Planning
- Training Data & Captioning
- Feedback & Iteration Notes
- Working With This Context
- Contribution Workflow
- Open Questions
- Next Steps
- Related Materials

## Project Overview

Graffito is a kinetic, hand‑crafted short set in the South Bronx. Tony, a sensitive 7‑year‑old with a handmade sketchbook and an old spray can, reunites with his father Monk, an artist who paints through dance. A near‑miss and a flood of sensory overload reveal Tony’s latent ability: the Magic Spray Can that paints doors, worlds, and ways through fear. With Grandma’s grounding wisdom, Tony learns to turn his inner chaos into agency. Themes: transformation, craft, community, and finding calm through creation.

This context captures the film’s evolving language: story beats, shot design, material vocabulary, captioning conventions for training, and feedback driving iteration.

## Directory Map

- `Script/`
  - `script_from_pdf.md` – Current narrative draft (scenes 1–18), including dialogue and key beats (Orange Door, Motherload, Elephant).
  - `shots.md` – Shot‑by‑shot breakdown per scene (camera moves, pacing, animatic bursts, motifs).
- `Mark/`
  - `scenes2_and_3.md`, `scenes3_and_4.md` – Mark’s scene intent notes: setting, macro/micro beats, transitions.
- `Training/`
  - `captioning_sysprompt_graffito_video_v3.md` – System prompt/spec for dense, grounded captions (present tense, no markdown, ALL‑CAPS for iconic objects).
  - `all_captions_graffito_video_v3.md` – Curated caption set reflecting the mixed‑media style (material taxonomy, motion quality, camera notes).
- `Feedback/`
  - `08-18-25.md`, `08-23-25.md`, `08-31-25.md` – Session notes (references, style guidance, next probes; e.g., Fisher King, Gravity, Pata Pata cadence, “make a shot”).

## Creative Vision & Visual Language

- Mixed‑media stop‑motion: PAINTED PAPER CUTOUTS (bodies/clothes/sets), PHOTOGRAPHIC CUTOUT faces, THICK IMPASTO PAINT textures, PAINTED CARDBOARD props, CRUMPLED PAPER surfaces.
- Kinetic, handcrafted feel: VISIBLE BRUSHSTROKES, RAW TORN EDGES, layered collage depth; interplay of 2D faces with 3D‑ish bodies.
- Urban fable: gritty city tones punctuated by primary color bursts (graffiti splatters, murals, the ORANGE DOOR).
- Motion grammar: jerky stop‑motion steps; Monk “paints by dancing”; Tony’s animatic sensory bursts as both overwhelm and superpower.
- Motifs: pigeons swarm, subway rumble, spray‑to‑object transformations, trombone‑brush play, doors as passage/choice.

## Story & Structure

- Reference: `Script/script_from_pdf.md` (V10). Beats include: school release swarm → stoop kids → street montage/near‑miss → Grandma’s apartment (dance/paint) → cop‑light crisis → Tony paints the ORANGE DOOR → Space void → Motherload and Elephant → return through the mural.
- Emotional axis: Tony’s regulation arc (overwhelm → flow), Monk’s belonging vs hiding, Grandma’s boundary and love.

## Shot Planning

- Reference: `Script/shots.md` (V9). Scene‑by‑scene shot lists with notes on animatic bursts, POV alternation, drone‑like pull‑backs, transitions through windows, and Dutch angles.
- Mark’s notes (`Mark/*.md`) nuance the macro/micro: feet/hand POVs, crowd legs forest, cab near‑miss framed as sensory event, paint‑dance coverage, window ingress.
- Practical notes: resolve facial expression language (eyebrows/lip‑flap vs cutout swaps), hair silhouettes (curls), and how edges articulate.

## Training Data & Captioning

- Use `Training/captioning_sysprompt_graffito_video_v3.md` when authoring captions:
  - Present tense, third person; 75–300 words (40–90 for very short clips).
  - A‑Tier naming: Tony, Monk, Grandma (respect scene rules: outdoor figures are anonymous except Tony).
  - B‑Tier: Motherload, Elephant (introduce visually then name).
  - Others remain descriptive (no names); emphasize iconic items in ALL CAPS: THE MAGIC SPRAY CAN, TONYS SKETCHBOOK, THE ORANGE DOOR.
  - Describe materials, motion quality, camera moves, lighting; avoid non‑visual inference.
  - No markdown/quotes in final text (T5‑optimized).
- Append new examples to `Training/all_captions_graffito_video_v3.md` with source clip context (scene/shot) and keep material vocabulary consistent.

## Feedback & Iteration Notes

- Session logs in `Feedback/` drive probes: 
  - 2025‑08‑18 – Dragon reference (Fisher King), can behavior phases (mist → fluid → solid), music‑driven flow, “genie in a bottle” trust theme.
  - 2025‑08‑23 – Outdoor figures anonymity, Tony expression beats, montage scaffolding, Dutch angles, Bronx 70s flavor.
  - 2025‑08‑31 – “Make a shot”, transitions through surfaces, automated search for establishing images, train on selects, reuse seeds.
- Practice: date‑stamp entries, fold actionable shifts back into scripts/shots/captions to keep artifacts convergent.

## Working With This Context

- For exploratory generations:
  - Start from `shots.md` beats; choose 1–2 shots to “make a shot”.
  - Use caption spec to generate dense, style‑true training captions or evaluation prompts.
  - Keep materials vocabulary stable to reinforce style conditioning across runs.
- For pipeline integration (ComfyUI automation):
  - Use project Templates and the automation CLI to render short probes; keep outputs organized by session.
  - Log requested vs applied settings in run notes; snapshot seeds for reproducibility.

## Contribution Workflow

- New scene or beat: update `Script/*` first; reflect coverage in `shots.md`.
- New style rule or material term: add to this README and align in `Training/*` captions.
- Add feedback: create `Feedback/YYYY-MM-DD.md` with summary → decisions → next probes.
- Naming: prefer kebab‑case for files; include version suffixes where drafts diverge (`shots_v10.md`) and date‑stamp feedback.

## Open Questions

- Faces: best practice for expression language (eyebrows vs swapped cutouts vs light play)?
- Cops: keep off‑screen with light/sound only or abstract further?
- Can behavior: standardize “mist → fluid → solid” phases and when Tony gains control.
- Motherload/Elephant: final silhouettes and interaction grammar (save/fall beat coverage).
- Transitions: surface‑through moves (window/door/phone) vocabulary and when to deploy.

## Next Steps

- “Make a shot” targets: Scene 2 (stoop/phone), Scene 3 (near‑miss + animatic), Scene 4 (paint‑dance 2‑shot), Scene 18 (mural door exit).
- Train/eval loop: curate selects → caption with spec → run small batches → select seeds → iterate composition/lighting.
- Assets: incorporate the school set; stabilize Tony’s curls, Grandma’s silhouette; refine boombox/ taxi props.
- Document: add example side‑by‑side frames (ref → result) in a future appendix.

Example session (ready to run)
- S03–S04 T2V OneBatch (native Wan 2.2 T2V): [WorkingSpace/Projects/Graffito/Sessions/S03-S04-T2V-OneBatch/out/t2v_batch.yaml](../../../../WorkingSpace/Projects/Graffito/Sessions/S03-S04-T2V-OneBatch/out/t2v_batch.yaml)

## Related Materials

- ComfyUI Automation Deep Dive: [Notes/Documentation/dev_doc.md](../../../Notes/Documentation/dev_doc.md) (system overview for the generation pipeline).
- Templates & Workflows: see [Templates/](../../../Templates/).
