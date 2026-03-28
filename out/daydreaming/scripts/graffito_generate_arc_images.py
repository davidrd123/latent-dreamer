import json
import sys
from pathlib import Path

sys.path.insert(0, str(Path.cwd()))

from daydream_vision.vendor.gemini_image import generate_images
from daydream_vision.vendor.gemini_min import init_client

VENDOR = Path("daydreaming/vendor/graffito")

SITUATION_REFS = {
    "graffito_street_overload": [
        VENDOR / "character_references/tony/TONY_wide_standing_cityscape.png",
        VENDOR / "scene_references/street_01.png",
    ],
    "graffito_grandma_apartment": [
        VENDOR / "character_references/tony/TONY_wide_standing_cityscape.png",
        VENDOR / "character_references/monk/MONK_closeup_taxi_background.png",
        VENDOR / "scene_references/apartment_02.png",
    ],
    "graffito_night_mural": [
        VENDOR / "character_references/tony/TONY_wide_orangeDoor_portal.png",
        VENDOR / "character_references/monk/MONK_closeup_taxi_background.png",
        VENDOR / "scene_references/mural_01.png",
    ],
}

SITUATION_PROMPTS = {
    "graffito_street_overload": (
        "Scene: An urban SOUTH BRONX STREET near the elevated subway. "
        "Old prewar buildings, street vendors, graffiti-covered walls. "
        "Gritty, crumbling, colorful. Under the elevated track."
    ),
    "graffito_grandma_apartment": (
        "Scene: Inside GRANDMA'S APARTMENT. Warm, cluttered with books, "
        "sculptures, found objects, kid art. Furniture pushed aside, "
        "canvas draped over chairs. Paint-spattered floor."
    ),
    "graffito_night_mural": (
        "Scene: THE SCHOOL WALL AT NIGHT. A large wall being painted "
        "with a celestial sky mural. Van Gogh meets Basquiat meets "
        "the Bronx in 1977. Police lights in the distance."
    ),
}

STYLE_PREFIX = (
    "Generate an image in the exact same handmade stop-motion style "
    "as the reference images. MATCH THE MATERIAL QUALITY: papier-mache "
    "bodies, photographic xerox-degraded faces, torn paper, thick "
    "impasto oil paint, visible brushstrokes, raw torn edges, painted "
    "cardboard surfaces. Jerky stop-motion movement quality.\n\n"
)

with open("out/graffito_thought_stream.json") as f:
    all_cycles = json.load(f)

TARGET_CYCLES = [1, 2, 6, 8, 13, 20]

cycle_map = {c["cycle"]: c for c in all_cycles}

client = init_client()

for cn in TARGET_CYCLES:
    c = cycle_map[cn]
    sit = c.get("situation") or "graffito_grandma_apartment"
    scene = c.get("scene", "")
    image_hint = c.get("image_hint", "")
    reg = c.get("regulation_mode", "bracing")
    family = c.get("family", "")
    flip = c.get("flip", False)

    action_lines = []
    if "apartment" in sit:
        if family == "rehearsal":
            action_lines.append(
                "TONY sits near paper on the floor, his 2D hand gripping "
                "THE MAGIC SPRAY CAN, practicing counted strokes. "
                "His body sways to a rhythm."
            )
            action_lines.append(
                "MONK spins nearby, arm arcing a brush, paint flying."
            )
        else:
            action_lines.append(
                "TONY watches MONK paint, holding his small notebook. "
                "MONK dances as he paints, fluid and energetic."
            )
            action_lines.append(
                "GRANDMA moves slowly through the space."
            )
    elif "mural" in sit:
        action_lines.append(
            "MONK dances as he paints a swirling celestial sky on the wall. "
            "Virtuoso. Energetic but lyrical."
        )
        action_lines.append(
            "TONY stands nearby gripping THE MAGIC SPRAY CAN, "
            "the old defunct can with M and T painted on the bottom."
        )
        if reg in ("entraining", "flowing", "creating"):
            action_lines.append(
                "Tony's body sways. He is finding the rhythm. "
                "The lights feel like a beat now, not a threat."
            )
        else:
            action_lines.append(
                "Blue and red police lights pulse. Sirens approaching. "
                "The frame is kinetic and disjointed. Scary."
            )
    elif "street" in sit:
        if reg in ("entraining", "flowing", "creating"):
            action_lines.append(
                "TONY moves through the crowd with growing confidence. "
                "His body finds a rhythm in the chaos."
            )
        else:
            action_lines.append(
                "TONY freezes amid a surge of bodies, backpacks, phones. "
                "Sensory overload. The frame fragments."
            )

    prompt = (
        STYLE_PREFIX
        + SITUATION_PROMPTS.get(sit, "")
        + "\n\n"
        + " ".join(action_lines)
        + "\n\n"
        + f"Mood: {scene}"
    )

    refs = [r for r in SITUATION_REFS.get(sit, []) if r.exists()]

    print(f"C{cn:02d} | {sit.split('_')[-1]} | {family} | {reg} | refs:{len(refs)} | flip:{flip}")

    try:
        result = generate_images(
            client=client,
            model="gemini-2.5-flash-image",
            user_prompt=prompt,
            ref_image_paths=refs,
            aspect_ratio="16:9",
        )
        if result.get("images"):
            img = result["images"][0]
            out = Path(f"out/graffito_arc_C{cn:02d}.png")
            img.save(out)
            print(f"  -> {out} ({img.size[0]}x{img.size[1]})")
        else:
            print(f"  -> no image: {result.get('text', '?')[:200]}")
    except Exception as e:
        print(f"  -> ERROR: {e}")

print("\nDone.")
