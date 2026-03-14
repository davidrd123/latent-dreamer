# Graffito Vertical Slice

The first real test of the game engine model (doc 17). Graffito has
a script, a trained LoRA, codified prompt engineering rubrics, a clip
library with Mark's selects, and an active collaborator (Mark
Friedberg). This doc inventories what exists and what needs to be
built to get a watchable dream-driven render.

## Why Graffito

- **Active collaborator.** Mark Friedberg wrote the script and gives
  regular feedback. His reaction to the first rendered run is the
  evaluation that matters.
- **Trained LoRA.** The renderer targets a model that already knows
  the Graffito visual world. The production design is in the weights.
- **Codified prompt craft.** The team's prompt writing strategies
  (9 strategy docs + shared patterns) are the renderer's rubrics.
  They're already written.
- **Existing clip library.** Mark has selected clips organized by
  scene. We know what good Graffito output looks like.
- **Richest source material.** Full script (18 scenes), shot-by-shot
  breakdowns, character profiles, material/motion lexicons, director's
  scene notes, feedback sessions.
- **Narrative structure.** Unlike the synthetic benchmarks (mood
  landscapes), Graffito has characters, arcs, and emotional stakes.
  The Mueller families map to real story beats.

## What Exists (Inventory)

### Creative Source (the brief)

All paths relative to `ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/`

| File | What it is | Maps to (doc 17) |
|---|---|---|
| `Script/script.md` | V10 script, 18 scenes, full dialogue | Situation definitions, emotional arc |
| `Script/shots.md` | V9 shot-by-shot breakdown per scene | Node descriptions (raw material) |
| `Mark/scenes2_and_3.md` | Director's scene 2-3 intent notes | Authoring guidance |
| `Mark/scenes3_and_4.md` | Director's scene 3-4 intent notes | Authoring guidance |
| `README.md` | Visual language guide, creative vision | Creative brief / production design |
| `AGENTS.md` | Agent workflow + prompt dev pipeline | Process reference |
| `TASKS.md` | Near-term creative targets | Priority reference |

### Training & Captioning (the voice)

| File | What it is | Maps to (doc 17) |
|---|---|---|
| `Training/captioning_sysprompt_graffito_video_v3.md` | Captioning spec: character tiers, material vocabulary, ALL CAPS conventions, T5-optimized format | Renderer voice / vocabulary |
| `Training/all_captions_graffito_video_v3.md` | Curated caption set (90s of training footage) | Renderer few-shot examples |

### Prompt Engineering Rubrics (the craft)

All paths relative to `ComfyPromptByAPI/WorkingSpace/davidrd/OutsideStruct/PromptWriting/`

| File | Strategy | Renderer role |
|---|---|---|
| `Prompt_Writing_Patterns_Shared.md` | Style-agnostic patterns (one core idea, attention budget, structural rhythm, etc.) | General prompt craft |
| `...Strategy 2...Conceptual Distance...md` | Managing OOD concepts, attention budget | How much detail the renderer can afford |
| `...Strategy 2.1...OOD...md` | Generalization strategies | Handling novel visual concepts |
| `...Strategy 3...Strategic Simplification...md` | "Clear to a kid reader" | Cutting secondary detail |
| `...Strategy 4.1...Thematic Capstone...md` | Closing sentence about feel/tone | Where Dreamer family/tension colors the prompt |
| `...Strategy 4.2...Aesthetic Control...md` | Cinematography & style control | Camera, lighting, atmosphere |
| `...Strategy 5...Graffito Lexicon...md` | Material & motion translation | PAINTED CARDBOARD, jerky stop-motion, etc. |
| `...Strategy 6...Character Consistency...md` | First-mention / subsequent-mention | Tony, Monk, Grandma descriptions |
| `...Strategy 7...Directing Performance...md` | Emotion + physicality pairing | Dreamer state → physical cues |
| `...Strategy 8...Active Props...md` | Props as participants, two-stage VFX | THE MAGIC SPRAY CAN, THE ORANGE DOOR |
| `...Strategy 9...Technical Analogy...md` | Domain-specific metaphors | Advanced prompt vocabulary |

Also available as skill references:
- `.claude/skills/prompt-engineering-toolkit/references/pws-05-graffito-lexicon.md`
- `.claude/skills/prompt-engineering-toolkit-standalone/strategies/pws-05-graffito-lexicon.md`

### Render Assets (reference material, not the v0 runtime path)

| Asset | Location | What it is |
|---|---|---|
| T2V workflow template | `Templates/Graffito/Graffito-wan2_2_t2v_template_vramhog.json` | Earlier Graffito ComfyUI workflow using WanVideo T2V |
| API variant | `Templates/Graffito/other-formats/Graffito-wan2_2_t2v_api.json` | API-compatible export of the earlier T2V workflow |
| Seed search template | `Graffito/Prompts/Templates/seed_search_t2v.yaml` | Earlier prompt exploration template (1312x576, 81 frames) |
| Soul transfer template | `Graffito/Prompts/Templates/soul_transfer_i2v.yaml` | Earlier I2V performance iteration template, seed-locked |
| Reference alignment | `Graffito/Prompts/Templates/reference_alignment.yaml` | Earlier I2V alignment search template |
| Model | `wan2.2_t2v_high_noise_14B_fp16.safetensors` | WanVideo 14B, fp16_fast, sageattn |
| Text encoder | `umt5-xxl-enc-bf16.safetensors` | T5 encoder for prompt processing |

### Clip Library (Mark's selects)

`WorkingSpace/Projects/Graffito/ClipLibrary/Mark_Selects_shot_grouping/`

| Scene dir | Content | Clips |
|---|---|---|
| `SC002_MEAN_KIDS/` | Scene 2 — stoop, phone kick | ~40 clips + metadata |
| `SC003_STREETS/` | Scene 3 — running montage, graffiti, legs | Multiple clips |
| `SC004_APARTMENT/` | Scene 4 — Grandma's apt, dance/paint | Multiple clips |
| `SC007_GRAFFITO_MYTH/` | Scenes 7-9 — mythic sequence | Multiple clips |
| `SC013_PAINTING_MURAL_DOOR/` | Scene 13 — mural, Orange Door | Multiple clips |
| `SC014_SPACE/` | Scene 14 — space/void, spray variants | Multiple + VACE I2V iterations |
| `SC015_IMAGINARY_STREET/` | Scene 15 — imaginary street | I2V results |
| `SC016_CASTLE_WALL/` | Scene 16 — castle, Motherload | With metadata |
| `SC017_CASTLE_WALL_MORNING/` | Scene 17 variation | Multiple clips |
| `SPRAY/` | Spray can magic sequences | Across scenes |

### Working Sessions

| Session | Focus |
|---|---|
| `Sessions/S02-MonkIsBack-Overpass/` | Scene 2 exploration |
| `Sessions/S03-NearMiss/` | Scene 3 near-miss animatic |
| `Sessions/S03-S04-StreetToStudio/` | Scenes 3-4 transition |
| `Sessions/S03S04-T2V-QuickTest/` | Rapid iteration |

### Feedback with Mark

| File | Date | Key points |
|---|---|---|
| `Feedback/D_Notes/08-18-25.md` | 2025-08-18 | Dragon ref (Fisher King), can behavior phases, music-driven flow |
| `Feedback/D_Notes/08-23-25.md` | 2025-08-23 | Outdoor anonymity, Tony expressions, Dutch angles, Bronx 70s |
| `Feedback/D_Notes/08-31-25.md` | 2025-08-31 | "Make a shot", transitions through surfaces |
| `Feedback/VideoRecording/08-17-25/Segments/S01-S03.md` | 2025-08-17 | "City Drift" success, mural exploration, continued training recs |

### Consistency System

| Asset | What |
|---|---|
| `WorkingSpace/PadlockingSystem/` | CTP (Consistency Through Padlocking) — emotional arc testing, anti-erasure techniques |
| `WorkingSpace/davidrd/Contexts/Graffito/Prompting/gravity/` | Gravity/emotional arc experiments |
| `WorkingSpace/davidrd/Contexts/Graffito/Prompting/School/` | School push-in variations (FFLF base, speedup) |

## Characters

From the captioning spec + script:

**Tony** — 7yo. PAINTED PAPER CUTOUT body: light blue shirt, red
collar, beige pants. PHOTOGRAPHIC CUTOUT face: curly hair, green
eyes. Quick, clumsy stop-motion movement. Carries OLD DECREPIT
SPRAY CAN and HANDMADE SKETCHBOOK. Sensory overwhelm as both
vulnerability and superpower. Arc: overwhelm → agency.

**Monk** — 27yo father. Taller. PAINTED PAPER CUTOUT body splattered
with THICK IMPASTO PAINT. Multi-colored shirt. PHOTOGRAPHIC CUTOUT
face: beard, glasses. Fluid, rhythmic, dance-like movement. "Put
paint whar' it ain't." Arc: belonging vs hiding.

**Grandma** — Wise, serious. PAINTED PAPER CUTOUT body: teal top,
pearl-like necklace (sculpted beads). PHOTOGRAPHIC CUTOUT face:
glasses, dark styled hair. Deliberate, purposeful movement. The
boundary-setter and ground.

**Motherload** — Dragon. Giant, sideways-flying, gnarled talons,
crooked, messed up. Grandma's avatar in the fantasy world. "Maybe
what is wrong with you is exactly what's right with you."

**Elephant** — Tony's stuffed toy → life-sized guide in fantasy.
"She's been waiting a long time."

## Situations (Draft Mapping)

| Situation | Scenes | Emotional register | Key objects |
|---|---|---|---|
| s1: Street / overwhelm | 1-3 | Sensory chaos, vulnerability, running | Pigeons, graffiti, forest of legs, gypsy cab |
| s2: Grandma's apartment | 4-6 | Warmth, dance, paint, grounding, tension | Fela, brushes, rice/lentils, Elephant |
| s3: Graffito mythology | 7-9 | Fantasy-within-story, noble outlaws | Masked figures, ropes, THE ORANGE DOOR |
| s4: Night mission / crisis | 11-13 | Painting, teaching, cop lights, terror | Mural, dragon painting, SPRAY CAN, cop lights |
| s5: Space / transformation | 14 | Blackness, wonder, the Can works | Stars, nebulae, subway staircase |
| s6: Motherload's world | 15-17 | Quest, castle, falling, identity | Breadcrumbs, castle wall, Motherload, Elephant |
| s7: Return | 18 | Agency, rescue, "Freeze!" | THE ORANGE DOOR, mural, painted star |

## Mueller Families → Story Beats

| Family | Graffito mapping | Visual change? |
|---|---|---|
| REVERSAL | Tony's sensory overwhelm moments — school cacophony, cab near-miss, cop lights. "What if I could control this?" | Yes — variant nodes (same street, different perception) |
| ROVING | Escape into fantasy — Space, Motherload's mountains, imaginary street. The Can opens doors. | Yes — different situation entirely |
| RATIONALIZATION | Motherload's reframe: "what's wrong = what's right." Grandma: "searching out there for something he'll only find inside." | No — same scene, mood shifts. Music + color. |
| REHEARSAL | Tony practicing — mime spraying, painting shaky mountains, trying to make the Can work. Each attempt builds toward the moment it fires. | No — same scene, repetition with variation. |
| REVENGE | Directed at the cops? At the stoop kids? At the situation itself? Tony's "Freeze!" at the end. | Maybe — depends on whether visual content changes. |

## Slice v0: Scenes 3-4 (Narrow Scope)

The full Graffito graph (40-50 nodes, 7 situations) is the
destination. The first slice is narrow:

**Scene window:** Scenes 3-4 (street montage → Grandma's apartment)
**Why these:** Strongest existing material — Mark's selects in
SC003_STREETS and SC004_APARTMENT, earlier prompt/render experiments
for these scenes, and director's notes.

**Situations:** 2
- s1: Street / overwhelm (scene 3 — running, graffiti, legs, cab)
- s2: Grandma's apartment (scene 4 — warmth, dance, paint, Fela)

**Nodes:** 12-15
- ~5-6 base nodes per situation
- 2-3 REVERSAL variant nodes (same street, different perception —
  sensory overwhelm vs focused running)

**Families:** 1-2
- REVERSAL (overwhelm — Tony's sensory cacophony moments)
- ROVING (escape from street dread to apartment warmth) — natural
  transition between the two situations

**This is a scripted real-time implementation of the doc 17 runtime
model.** Not the full live performance stack. Not the full game
engine. The goal is to answer one question: does dream-driven
scene selection produce output that feels like anything when you
watch it?

## What Needs to Be Built

### 1. Slice Dream Graph (12-15 nodes)

Author nodes for scenes 3-4 only:
- ~5-6 street nodes: feet pounding, hand over graffiti, forest of
  legs, Tony's eyes searching, cab near-miss, drone pullback
- ~5-6 apartment nodes: camera enters window, Monk painting/swaying,
  Tony bursts in, dance together, Grandma enters, paint flying
- 2-3 REVERSAL variants: cab near-miss as sensory overwhelm, street
  as animatic burst, corridor reperceived

Each node needs:
- Rich visual description (authored in Graffito captioning voice)
- Situation affiliation (s1 or s2)
- Emotional valences (hope, threat, anger, grief, dread)
- Tags / indices (for Dreamer retrieval)
- Connections to other nodes

Source material: `shots.md` shots 5-13 provide the raw descriptions.
The captioning spec provides the voice. Mark's selects in SC003 and
SC004 provide visual reference for what good output looks like.

### 2. Renderer

Single LLM call per cycle:
- **System prompt:** assembled from the prompt writing strategies +
  captioning spec + character profiles
- **User prompt:** node visual description + Dreamer state (family,
  tension, emotion, visit_count)
- **Output:** T5-optimized visual prompt + transition_type + dwell_time

The rubrics are written. The voice is established. This is
a system prompt assembly + output schema definition task.

### 3. Dreamer (use the fastest path)

**Do not put the Clojure kernel on the critical path for slice v0.**
The kernel is the research track. The first watchable run should use
whatever gets to watched output fastest:

**Option A: Python scheduler (daydream_engine.py).** Already proven
for obsession/drift/return dynamics. Wire it to consume the slice
graph. Fastest if the scheduler can already walk a graph.

**Option B: Trace-driven player.** Hand-author or auto-generate a
plausible 12-20 cycle trace (which node, which family, what emotion
at each step). Feed the trace to the renderer. No Dreamer at all —
just a sequence of game states. Fastest overall, answers the visual
quality question without any Dreamer integration.

**Option C: Clojure kernel.** Wire as fourth benchmark adapter
(same pattern as Puppet Knows/Arctic/Zone). Most faithful to
Mueller, but adds integration work. Save for slice v1 comparison.

Recommended: **start with Option B** (trace player) to get a first
watchable run in front of Mark as fast as possible. Then try Option A
or C to see if dynamic traversal produces more interesting sequences.

### 4. Renderer → Live Scope Wiring

The rendered prompts need to flow into the existing Scope real-time
stage:
- `PUT /api/v1/realtime/prompt`
- `POST /api/v1/realtime/soft-cut` or `hard-cut`
- `POST /api/v1/realtime/parameters`
- What's needed: an adapter from renderer output + playback packet →
  Scope API calls with the right dwell/transition timing

The shared join between graph fixture + trace fixture + consumer packet
is defined in doc 21 (`21-graffito-v0-playback-contract.md`).

Scripted real-time path for slice v0:
1. Trace (hand-authored or Dreamer-generated) → cycle sequence
2. Playback join resolves each cycle against the graph fixture
3. Renderer processes each joined cycle → Scope prompt + transition +
   dwell
4. Trace player sends the cycle to Scope in real time
5. Watch or capture the run in sequence

### 5. Parametric Music (parallel track, not blocking)

Dreamer state → Lyria parameters. Needs Lyria validation first
(can it shift key/density mid-stream?). Not blocking the first
visual-only watchable run.

## Path to Showing Mark Something

1. **Author slice graph** — 12-15 nodes for scenes 3-4
2. **Build renderer** — system prompt from rubrics + captioning spec
3. **Author or generate trace** — 12-20 cycle sequence through the graph
4. **Run the slice** — trace → playback join → renderer → Scope
   real-time stage
5. **Watch/capture** — does it feel like anything?
6. **Show Mark** — his reaction is the evaluation

Steps 1 and 2 can run in parallel. Step 3 is fast (hand-authorable
in an hour, or auto-generated from a simple walk). Step 4 depends
on 1+2+3. The first watchable run is visual-only and scripted
(no music, no feedback loop, no manual live control yet).

**What "done" looks like:** a 12-20 cycle real-time run that forms a
coherent dream-driven sequence through scenes 3-4 of Graffito,
using the trained LoRA via Scope, that Mark can watch and react to.

## After Slice v0

If the visual output is compelling:
1. Expand to full 40-50 node graph (all 7 situations)
2. Wire the Clojure kernel as Dreamer (comparison against trace player)
3. Add Director feedback loop (does it improve traversal?)
4. Add parametric music
5. Add manual live-stage control (Scope + Lyria + APC Mini)

If the visual output is flat:
1. Diagnose — is it the nodes (bland descriptions), the renderer
   (bad prompts), or the LoRA (can't execute)?
2. Compare renderer output against Mark's existing best prompts
3. Iterate on the weakest link before expanding scope

## What This Doesn't Replace

- The synthetic benchmarks (Puppet Knows, Arctic, Zone) — still
  useful for testing kernel machinery in isolation
- The kernel's Mueller architecture — Graffito is a consumer of
  the kernel, not a replacement. Kernel wiring is slice v1, not v0.
- The prompt engineering work the team is doing independently —
  Graffito vertical slice consumes their rubrics, doesn't change them
