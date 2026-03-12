User
Akira Captioning Style and Prompting Guide (v4)
Purpose
Provide a compact, repeatable prompting style that mirrors the captioning language used for training while remaining flexible for novel and outâ€‘ofâ€‘distribution (OOD) scenes.
Help humans and agents compose new prompts that stay â€œon modelâ€ for visual grammar, materials, lighting, motion, and camera language specific to the 1988 cel animation look.
Core Voice and Structure
Optional opener: â€œ1988 Cel Animation â€” â€¦â€ anchors style and era.
Single paragraph per shot; declarative, visual-first phrasing.
Order: composition â†’ subject â†’ background â†’ palette/materials â†’ camera â†’ motion â†’ effects â†’ timing.
Prefer precise nouns and fixed phrases; avoid modern/CG terms unless explicitly contrasting.
Visual Grammar (Materials and Surfaces)
FLAT-COLORED CELS; BOLD BLACK INK OUTLINES
DENSELY DETAILED PAINTED BACKGROUND (DDPB)
OPAQUE PAINTED SMOKE PLUMES; painted dust/debris; handâ€‘drawn sparks
GRADIENT AIRBRUSHING (glows/halos); TRANSLUCENT ENERGY AURA
ANAMORPHIC-STYLE LENS FLARE; SOFT BOKEH
LIGHT TRAILS; MOTION BLUR LINES
Lighting Language
DEEP BLACKS (shadow mass); BRILLIANT HIGHLIGHTS (specular hits)
Overexposure/whiteout/impact frame; silhouette against glow
Colored neons (pink, teal, magenta), warm firelight cores (yellow/orange)
Camera and Framing
Static wide/medium/close; low/high angle; topâ€‘down; fisheye
TRACKING SHOT; pushâ€‘in/pullâ€‘out; simulated zoom; iris wipe; whip pan
POV shots; overâ€‘theâ€‘shoulder; dolly parallax (MULTI-LAYER PARALLAX)
Motion and Timing
â€œanimated on ones/twosâ€; subtle vibration; slow pushâ€‘in; fast pullâ€‘out
Impact frame (white); strobing muzzle flashes; drifting smoke; debris float
Canon Tokens (consistent identifiers)
KANEDAS RED MOTORCYCLE; RED RIDING SUIT; THE PILL CAPSULE SYMBOL
TETSUOS MUTATING ARM / BODY HORROR; RED CAPE; CYBERNETIC PROSTHETICS
LASER CANNON (ground); LASER CANNON SATELLITE (SOL)
Palette and Atmosphere
Nocturnal NEOâ€‘TOKYO: teal/blue shadows, neon pinks, magentas, cyans
Fire/explosion cores: yellowâ†’orange with dark red/black plumes
Industrial greens, ochres, dusty violets for rubble/smoke/water grime
Negative Space (avoid unless specified)
Modern anime style, cute/moe/chibi, CGI/rendered/3D look, sketch/comic tropes
Overâ€‘smoothing/airbrushed skin, plastic look, compression artifacts, overlays
Prompt Scaffolds
Baseline shot
1988 Cel Animation â€” [composition + angle + lens], [subject + pose/expression], against a [DDPB of setting]. Materials: [flatâ€‘colored cels with BOLD BLACK INK OUTLINES], lighting with [DEEP BLACKS and BRILLIANT HIGHLIGHTS]. Camera: [TRACKING SHOT/pushâ€‘in/pullâ€‘out/fisheye]. Motion: [animated on ones/twos], [MOTION BLUR LINES/LIGHT TRAILS]. FX: [OPAQUE PAINTED SMOKE PLUMES/ANAMORPHIC-STYLE LENS FLARE].
Action FX
1988 Cel Animation â€” [impact action], painted [yellow/orange] core with [OPAQUE PAINTED SMOKE PLUMES], debris [shards/sparks]. A [white IMPACT FRAME] punches the cut before returning to [DDPB].
Water/Reflections
1988 Cel Animation â€” Lowâ€‘angle close on a wheel plowing a puddle; handâ€‘drawn white spray; quick pullâ€‘up into a tracking shot with MULTIâ€‘LAYER PARALLAX; turquoise water reflects hub assembly on a separate cel.
SOL / Laser
1988 Cel Animation â€” A razorâ€‘thin greenâ€‘white beam from the LASER CANNON SATELLITE (SOL) slices through stylized cloud plumes; the strike resolves in a white impact frame, then expanding RED AND ORANGE PAINTED EXPLOSION.
Composition Building Blocks (phrase bank)
Materials: flatâ€‘colored cels; bold outlines; painted background; airbrushed glow; opaque smoke plumes; translucent aura; handâ€‘drawn sparks; motion blur lines; light trails
Camera: static wide; lowâ€‘angle; topâ€‘down; fisheye; POV; tracking shot; dolly; pushâ€‘in; pullâ€‘out; simulated zoom; iris wipe; whip pan
Lighting: deep blacks; brilliant highlights; overexposed bloom; silhouettes; colored neon spill; warm core/cool rim
Motion: animated on ones/twos; slow pushâ€‘in; rapid pullâ€‘out; subtle vibration; parallax layers
OOD Prompting Strategies (staying inâ€‘style)
Preserve grammar, materials, and timing terms while swapping setting/props.
Anchor with DDPB + cel/outline; then introduce novel subject matter (e.g., maritime cranes, elevated gardens, stormâ€‘lit rooftops) described in the same painted/inked terms.
Use one or two signature FX max (e.g., lens flare + smoke), not a laundry list.
Keep color narrative coherent (neon night vs firelight vs overcast day).
Mini Examples (mix-and-match)
1988 Cel Animation â€” A lowâ€‘angle TRACKING SHOT along a rainâ€‘slick causeway as KANEDAS RED MOTORCYCLE leans into a slide, handâ€‘drawn white spray arcing from the wheels. The DENSELY DETAILED PAINTED BACKGROUND recedes in MULTIâ€‘LAYER PARALLAX under DEEP BLACKS with neon magenta spill, animated on twos.
1988 Cel Animation â€” Topâ€‘down static wide of a rooftop plaza; a glowing fountain ripples with airbrushed light while thin MOTION BLUR LINES trace two distant couriers crossing, their flatâ€‘colored cels edged in BOLD BLACK INK OUTLINES.
1988 Cel Animation â€” An iris wipe reveals a fisheye view of an industrial stadium shell as a greenâ€‘white SOL beam cuts the overcast, snapping to a white IMPACT FRAME before RED AND ORANGE PAINTED EXPLOSION plumes fill the frame.
Choice Example Captions (verbatim style)
Alley Crash with Spotlights (099)
1988 Cel Animation â€” From a high, canted angle overlooking a DENSELY DETAILED PAINTED BACKGROUND of a dark NEO-TOKYO alley, a single bright spotlight sweeps across the grimy building facades, rendered in deep blacks and muted blues. A second spotlight joins it, illuminating two small motorcycles as they race through the chasm below, their brilliant headlights and red taillights creating faint LIGHT TRAILS. The scene cuts to a low-angle view from within a trash-choked side street, where a clown gang biker on a custom chopper plows into frame. The biker wears a purple helmet with a painted clown face and a light blue mask. His bike's headlights cast a bright glow as he crashes through piles of debris with fluid motion. A glass bottle shatters, sending a splash of hand-drawn purple liquid arcing through the air while wooden crates splinter into sharp fragments. He bursts out of the alley, tumbling violently with his motorcycle in a shower of debris before coming to rest in a heap in front of a detailed storefront with a red, dragon-painted security gate.
SOL Bridge Strike (1769)
1988 Cel Animation â€” From a high-angle perspective, dozens of civilians stand scattered across a wide concrete bridge, observing an off-screen event, set against a DENSELY DETAILED PAINTED BACKGROUND of a massive construction site. A brilliant green-and-white horizontal beam from a LASER CANNON SATELLITE streaks across the scene from the left, striking the bridge with a bright white IMPACT FRAME. Instantly, a massive RED AND ORANGE PAINTED EXPLOSION erupts, its force violently throwing the civilian figures into the air. Their bodies tumble like ragdolls as OPAQUE PAINTED SMOKE PLUMES with BOLD BLACK INK OUTLINES billow outwards and upwards, filling the screen with dark red and fiery yellow animated clouds. Sharp shards of debris are violently ejected from the blast's epicenter as the explosion continues to expand with immense force.
Akira Slide Reflection (2341)
1988 Cel Animation â€” From a high-angle perspective, the front wheel of KANEDAS RED MOTORCYCLE executes an AKIRA SLIDE, scraping sideways from right to left across a cracked, DENSELY DETAILED PAINTED BACKGROUND of dark asphalt. The wheel, rendered with BOLD BLACK INK OUTLINES and flat red and green colored cels, casts a hard shadow as the camera performs a smooth tracking shot. The rear wheel slides into view, and small, hand-drawn white sparks with yellow centers arc from the friction point of the front wheel. As the slide continues, the rear wheel passes over a puddle of turquoise water, its intricate red hub assembly creating a perfect, clear reflection on the water's surface, painted on a separate cel layer to create depth.
For more examples, see the batch YAMLs in WorkingSpace/davidrd/Contexts/Akira/Prompting/ (Motion, CameraFX, ActionFX, SOL, Water).
Operational Tips (for agents)
Use this guide as a token bank: sample a few phrases from each category rather than copying entire sentences.
Keep shots atomic: one location, one primary action, one camera move.
Log requested vs applied values at boundaries (resolution/frames/LoRA/negatives) and use applied values thereafter.
Appendix: Suggested Negative Prompt (copyable)
è‰²è°ƒè‰³ä¸½, è¿‡æ›, é™æ€, ç»†èŠ‚æ¨¡ç³Šä¸æ¸…, å­—å¹•, é£Žæ ¼, ä½œå“, ç”»ä½œ, ç”»é¢, é™æ­¢, æ•´ä½“å‘ç°, æœ€å·®è´¨é‡, ä½Žè´¨é‡, JPEGåŽ‹ç¼©æ®‹ç•™, ä¸‘é™‹çš„, æ®‹ç¼ºçš„, å¤šä½™çš„æ‰‹æŒ‡, ç”»å¾—ä¸å¥½çš„æ‰‹éƒ¨, ç”»å¾—ä¸å¥½çš„è„¸éƒ¨, ç•¸å½¢çš„, æ¯å®¹çš„, å½¢æ€ç•¸å½¢çš„è‚¢ä½“, æ‰‹æŒ‡èžåˆ, æ‚ä¹±çš„èƒŒæ™¯, èƒŒæ™¯äººå¾ˆå¤š, å€’ç€èµ°, split screen, text, signature, watermark, logo, timestamp, cartoon, modern anime style, moe, chibi, illustration, 3d render, CGI, rendered, painting, sketch, comic book style, plastic skin, overly smooth features, airbrushed, doll-like, mannequin, untextured surfaces, artifacting, compression artifacts, glitch, digital noise, banding, pixelation, frame, border, collage, oversaturated, pastel colors, soft colors, flat lighting, low contrast, low detail background, empty background, out of focus detail, extreme color cast, monochrome unless specified, generic AI look, uncanny valley, poorly fused elements, inconsistent lighting unless specified
