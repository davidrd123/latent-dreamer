# Graffito Visual References — Vendored

Vendored: 2026-03-25
Origin: ComfyPromptByAPI/WorkingSpace/Projects/Graffito/ClipLibrary/

These are reference images for grounding image generation in Mark
Friedberg's actual fabricated world. Use them as `ref_image_paths`
when calling `daydream_vision` or Gemini image generation.

## Character references

Use 1-2 character refs per generation to keep characters on-model.

| File | Character | Shows | Best for |
|------|-----------|-------|----------|
| `character_references/tony/TONY_wide_standing_cityscape.png` | Tony | Full body, blue shirt, cityscape | Establishing Tony's look |
| `character_references/tony/TONY_wide_orangeDoor_portal.png` | Tony | At the Orange Door, mural wall | Mural scenes, threshold moments |
| `character_references/tony/TONY_wide_space_sprayCan.png` | Tony | In space, holding the Can | Dream world, Can in use |
| `character_references/tony/TONY_action_swinging_rope_cityscape.png` | Tony | Action pose, rope, city | Movement, street energy |
| `character_references/monk/MONK_closeup_taxi_background.png` | Monk | Closeup, beard, glasses, papier-mache | Any scene with Monk |
| `character_references/grandma/GRANDMA_extreme_closeup_generated.png` | Grandma | Face, glasses, degraded photo | Apartment scenes |

## Scene references

Use 1 scene ref per generation to ground the location's material
quality and lighting.

| File | Location | Shows | Best for |
|------|----------|-------|----------|
| `scene_references/street_01.png` | Street | Urban street, painted sets | Street overload scenes |
| `scene_references/apartment_01.png` | Apartment | Interior, warm, cluttered | Apartment support/rehearsal |
| `scene_references/apartment_02.png` | Apartment | Higher quality frame | Apartment close-ups |
| `scene_references/mural_01.png` | Mural wall | Night painting scene | Mural crisis scenes |

## How to use in the thought stream pipeline

For each miniworld cycle, select refs based on the situation:

```python
SITUATION_REFS = {
    "graffito_street_overload": [
        "character_references/tony/TONY_wide_standing_cityscape.png",
        "scene_references/street_01.png",
    ],
    "graffito_grandma_apartment": [
        "character_references/tony/TONY_wide_standing_cityscape.png",
        "character_references/monk/MONK_closeup_taxi_background.png",
        "scene_references/apartment_02.png",
    ],
    "graffito_night_mural": [
        "character_references/tony/TONY_wide_orangeDoor_portal.png",
        "character_references/monk/MONK_closeup_taxi_background.png",
        "scene_references/mural_01.png",
    ],
}
```

Include Grandma ref only for apartment scenes where she's prominent.

## Prompting guidelines

When generating images with these references:

1. **Open with "Generate in the exact same handmade style as the
   reference images."** This is the strongest style-grounding cue.
2. **Name the materials explicitly:** papier-mache, xerox, torn paper,
   thick impasto oil paint, painted cardboard, visible brushstrokes.
3. **ALL CAPS for iconic objects:** THE MAGIC SPRAY CAN, TONY'S
   SKETCHBOOK, THE ORANGE DOOR (matches the captioning spec from
   Patrick's vault).
4. **Describe motion quality:** jerky stop-motion, Monk's fluid dance,
   Tony's tentative movements.
5. **Don't over-describe faces.** The photographic xerox-degraded faces
   are the style — let the references carry that.
6. **Max 3-4 reference images per call.** More than that dilutes the
   signal.

## Canonical source

Full clip library lives at:
`ComfyPromptByAPI/WorkingSpace/Projects/Graffito/ClipLibrary/`

Patrick's production vault with captioning specs:
`ComfyPromptByAPI/WorkingSpace/patrick/vault_graffito/`

Character bible (Mark's voice):
`ComfyPromptByAPI/WorkingSpace/patrick/vault_graffito/world/character-bible.md`
