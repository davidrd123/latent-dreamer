# Codex Task: Author Graffito Slice v0 Dream Graph

## Goal

Author 12-15 dream graph nodes for Graffito scenes 3-4 (street
montage → Grandma's apartment). Output: a single YAML file
following the frozen node schema from doc 19.

## Output File

Write to: `daydreaming/fixtures/graffito_v0_scenes_3_4.yaml`

## Schema

Every node follows this shape:

```yaml
graph_id: graffito_v0_scenes_3_4
situations:
  - s1_street_overwhelm
  - s2_grandmas_apartment

nodes:
  - id: g_s1_n01_feet_pounding
    name: Feet pounding pavement
    scene_ref: "Scene 3, Shot 5"
    variant_of: null
    variant_family: null
    situation_ids: [s1_street_overwhelm]
    dwell_s: 5.5
    visual_description: >
      Graffito Mixed-Media Stop-Motion — ...
    characters: [Tony]
    objects: []
    valences:
      hope: 0.0
      threat: 0.3
      dread: 0.2
      joy: 0.0
      wonder: 0.0
    tags: [running, feet, pavement, street]

edges: []
```

## Full Schema Reference

Read: `daydreaming/Notes/experiential-design/19-graffito-node-schema.md`

## Source Material (READ ALL BEFORE AUTHORING)

1. **Shot breakdowns (primary):**
   `/Users/daviddickinson/Projects/Lora/ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/Script/shots.md`
   — Shots 5-13 cover scenes 3-4.

2. **Script (for dialogue context and emotional beats):**
   `/Users/daviddickinson/Projects/Lora/ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/Script/script.md`
   — Scenes 3-4 (lines ~32-101).

3. **Director's scene notes:**
   `/Users/daviddickinson/Projects/Lora/ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/Mark/scenes3_and_4.md`
   — Mark's micro-beat descriptions for these scenes.

4. **Captioning spec (the voice):**
   `/Users/daviddickinson/Projects/Lora/ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/Training/captioning_sysprompt_graffito_video_v3.md`
   — Character profiles, material vocabulary, captioning rules.

5. **Training captions (examples of the voice):**
   `/Users/daviddickinson/Projects/Lora/ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/Training/all_captions_graffito_video_v3.md`
   — Read a few to internalize the voice before writing.

6. **Graffito material/motion lexicon:**
   `/Users/daviddickinson/Projects/Lora/ComfyPromptByAPI/WorkingSpace/davidrd/OutsideStruct/PromptWriting/Prompt Writing Strategy 5_ The _Graffito_ Lexicon (Material & Motion Translation).md`
   — The "How Would They Build It?" and "How Would They Animate It?" tests.

## Situations

**s1_street_overwhelm** (Scene 3)
Tony running through the South Bronx to find Monk. Sensory montage.
Tactile, close details of running — then overwhelm from the cab
near-miss — then recovery and drone pullback. Emotional register:
urgency, vulnerability, sensory chaos, then reset.

**s2_grandmas_apartment** (Scene 4)
Camera enters through window. Monk dancing/painting in the living
room. Tony bursts in. They paint-dance together to Fela. Paint
flies everywhere. Grandma enters with food, grounds the scene.
Emotional register: warmth, freedom, joy, then grounding tension.

## Target Node List

### Scene 3 — Street / Overwhelm (s1)

Base nodes:
- **g_s1_n01_feet_pounding** — Shot 5. Tony's feet slapping pavement.
  Close detail, tactile.
- **g_s1_n02_hand_over_graffiti** — Shot 6. Tony's hand pulling over
  fresh graffiti mural. CU on hand and paint.
- **g_s1_n03_forest_of_legs** — Shot 7. Slaloming through adult legs.
  Low angle, Tony small among towering figures.
- **g_s1_n04_eyes_searching** — Shot 8. Tony's mouth yelling "Monk!",
  eyes searching side to side — not looking ahead.
- **g_s1_n05_cab_near_miss** — Shot 9. The gypsy cab screeches.
  Jarring, sudden. Sensory explosion.
- **g_s1_n06_drone_pullback** — Shot 10. ECU on terrified eyes →
  pulls back → drone shot of the whole neighborhood. Tony running,
  getting smaller.

REVERSAL variants:
- **g_s1_n05v_cab_overwhelm** — Variant of n05. The near-miss
  triggers full sensory cacophony: animatic burst of single-frame
  images, sounds, the cab filling the frame. Tony frozen.
  `variant_of: g_s1_n05_cab_near_miss, variant_family: REVERSAL`
- **g_s1_n03v_legs_overwhelm** — Variant of n03. The forest of legs
  becomes threatening — legs closing in, claustrophobic, Tony's
  perception warping the crowd into sensory chaos.
  `variant_of: g_s1_n03_forest_of_legs, variant_family: REVERSAL`

### Scene 4 — Grandma's Apartment (s2)

Base nodes:
- **g_s2_n01_camera_enters_window** — Shot 10 cont. Camera pulls from
  street into apartment through window. Interior revealed: books,
  eclectic art, found objects, warm outsider sensibility. Fela beats.
- **g_s2_n02_monk_painting** — Monk alone, dancing as he paints.
  Furniture pushed aside, living room turned art studio. Rhythmic,
  fluid, paint-splattered.
- **g_s2_n03_tony_bursts_in** — Tony crashes through the front door.
  Colored paint flies. "DAD! YOU'RE BACK!" Explosion of energy.
- **g_s2_n04_dance_together** — Monk and Tony painting/dancing to
  Afrobeat horns. Arms tromboning, paint going everywhere. Two boys
  playing. Pure freedom.
- **g_s2_n05_grandma_enters** — Grandma marches in with steaming
  bowls. Looking wise and serious. Assesses the damage. Energy
  shifts — the kinetic freedom is interrupted by grounding authority.

## Writing Instructions

For each node:

1. Read the shot description in shots.md
2. Read Mark's scene notes for additional detail
3. Write the `visual_description` in Graffito captioning voice:
   - Prepend "Graffito Mixed-Media Stop-Motion — "
   - Present tense, third person
   - 75-150 words
   - Use material vocabulary: PAINTED PAPER CUTOUTS, PHOTOGRAPHIC
     CUTOUTS, THICK IMPASTO PAINT, PAINTED CARDBOARD, CRUMPLED PAPER,
     VISIBLE BRUSHSTROKES, RAW TORN EDGES
   - Use motion vocabulary: jerky stop-motion steps, deliberate turn,
     segmented quality, articulated paper arms, fluid rhythmic sway
   - ALL CAPS for iconic objects: THE MAGIC SPRAY CAN, TONYS
     SKETCHBOOK, THE STUFFED ELEPHANT
   - Embed camera/shot/lighting in the prose
   - Characters: first mention describe then name, subsequent name only
4. Set `dwell_s` based on the moment's weight (3.0-8.0 seconds range)
5. Set `valences` honestly — what is the intrinsic emotional character
   of this moment? Not how the Dreamer feels, but what the scene IS.
6. Set `tags` — concept words for retrieval (concrete + abstract)
7. Leave `edges: []` at graph level — we'll add edges in a second pass

## Quality Gate

Every node should pass: "Would I want to watch 5-8 seconds of this?"

The visual_description is the creative material. In the game engine
model, no live LLM agent rescues a bland node. If the description
is thin, the output will be thin. Write rich, specific, compelling
descriptions that a video generation model can execute.

## Do NOT

- Do not invent scenes or shots not in the source material
- Do not add Dreamer state (tension, goal_type, energy) to nodes —
  that comes from the trace at runtime
- Do not add edges — we do those in a second pass
- Do not write descriptions longer than 150 words — the renderer
  adds modulation on top, and the T5 encoder has attention limits
- Do not use markdown formatting in visual_description — T5 doesn't
  understand it
