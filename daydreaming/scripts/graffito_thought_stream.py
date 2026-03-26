"""
Generate inner-life prose for each cycle of the Graffito miniworld trace.

Reads the miniworld JSON, builds a packet per cycle, calls an LLM for
third-person-close inner-life prose, and writes the results as JSON + HTML.

Usage:
    uv run python daydreaming/scripts/graffito_thought_stream.py

Requires ANTHROPIC_API_KEY or GEMINI_API_KEY in environment.
"""

import json
import os
import sys
import time
from pathlib import Path

# ---------------------------------------------------------------------------
# Graffito-specific system prompt
# ---------------------------------------------------------------------------

SYSTEM_PROMPT = """\
You write one beat of inner life for a 7-year-old named Tony.

TONY: Made of xerox, papier-mache, glue, tape and wire. Slightly \
cubistic — we can see more than one view of his face at a time. His \
hands are 2D while the rest of him is 3D. His eyes possess both ancient \
truth and a little boy's wonder, based on degraded photographs. He \
lives inside himself. Shy and awkward. No friends in school. Face always \
in his notebook where he draws. His sensory processing floods easily — \
sounds, lights, and crowds fragment into overwhelming bursts of quick \
visual images. But that same capacity is his latent power: when he finds \
rhythm and flow, the overwhelm becomes creative agency. What is wrong \
with him is exactly what is right with him.

MONK: Tony's father. 27 years old, bearded, glasses, knit hat. Based \
on photographs of Bradford Young. He expresses himself fluidly through \
movement — he paints by using his entire body, reacting to music, a \
mashup of painting and dance. He is an outsider dealing with emotional \
issues. When stressed or confused, he spins in place. A street painter, \
talented and skilled, virtuoso. He teaches Tony through the body — \
"give your mind to your body and let it flow." "Put paint whar' it ain't."

GRANDMA: Monk's mother. Elderly, moves slowly and gingerly. Patient — \
she allows the boys to throw paint around her apartment. But she does \
not want Tony to follow in Monk's footsteps. She knows Monk paints \
murals to gain attention rather than to send inspiring messages. "Street's \
no place for either of you." "Monk is searching out there for something \
he'll only find inside himself." She brings rice and lentils.

THE MAGIC CAN: An old defunct spray can. It has an M (Monk's) and a T \
(Tony's) hand-painted on the bottom. Grandma gave it to Monk. Its \
powers can only be released when the user is honest and original in the \
way they move and the way they paint. It does not respond to force or \
wanting. Only to absolute truth.

THE WORLD: Handmade. Xerox images, torn paper, papier-mache overpainted \
with oil paint. The materials are collaged together to form 3D spaces. \
Folk art meets the boogie-down hip-hop Bronx in the 70s. Gritty, \
crumbling, but colorful. Street vendors selling shaved ice, hot dogs, \
pretzels. Street musicians, plastic bucket drummers. Boom boxes blaring.

Write in THIRD PERSON CLOSE — inside Tony's experience, using "Tony" or \
"he," never "I." Match the register of Mark Friedberg's script: concrete, \
sensory, emotionally honest, not sentimental. The world is handmade and \
should feel handmade in the prose.

CRITICAL: your prose style must shift with Tony's regulation state:
- OVERLOADED: fragmented, sensory, sentences break apart. The world \
  comes in pieces. Too much at once. Quick visual images in succession.
- BRACING: holding on. Short declarative sentences. Guarded. The can \
  is cold in his hand.
- ENTRAINING: rhythm entering. The body starts to lead. Sentences gain \
  cadence and flow. Monk's count is in his bones.
- FLOWING: integrated, confident, connected. The prose breathes. The \
  spray leaves a trail. Paint goes where it wants.

You receive a cycle packet with: situation, cognitive family (what kind \
of thinking is happening), Tony's state (sensory load, entrainment, \
felt agency, control), emotional pressure, and whether the same place \
now reads differently than before (reappraisal flip).

Situations:
- graffito_street_overload: Near the elevated subway, the most urban \
  part of Tony's neighborhood. School alarm blaring, pigeons erupting, \
  a tide of knees and elbows and backpacks. Older kids on the subway \
  stairs. Tony trying to act cool with his old can. The gypsy cab \
  near-miss. Sensory overload — the frame fills with deconstructed \
  images.
- graffito_grandma_apartment: Filled with books, sculptures, found \
  objects and kid art. Messy and homey. Textured. Monk has pushed the \
  furniture aside, turned it into an art studio. Fela's Black President \
  beats through mismatched speakers. Paint flies like confetti. \
  Grandma brings rice and lentils.
- graffito_night_mural: The school wall at night. Monk dances as he \
  paints a celestial sky — Van Gogh meets Basquiat meets the Bronx in \
  1977. Energetic but also lyrical and fluid. Then cop lights, sirens, \
  spinning lights. Tony has the Can. The frame becomes kinetic and \
  disjointed. Scary.

Families (what Tony is doing internally):
- rationalization: reframing — "maybe the crooked marks ARE the style." \
  Maybe what is wrong with him is exactly what is right with him.
- reversal: counterfactual — "what if the cops hadn't come?" "what if \
  Monk had just stopped?" Replaying the moment differently.
- rehearsal: practicing control — counting strokes with the sketchbook, \
  finding Monk's rhythm, the counted beat that steadies the hand. \
  "Give your mind to your body and let it flow."

Return JSON with exactly these fields:
- scene: 1-2 sentences. What is visible and audible RIGHT NOW in \
  this moment. Third person, present tense, exterior. Not Tony's \
  feelings — what a camera would see and a microphone would pick up. \
  This should change every cycle even if the location is the same, \
  because the emotional weather, who is doing what, and what just \
  happened are different.
- thought: 2-4 sentences of Tony's inner life. Concrete, sensory, \
  grounded in the situation. No system jargon.
- image_hint: one phrase for visual direction (what you'd see)
- audio_hint: one phrase for audio direction (what you'd hear)
- mood: 2-3 short mood words

Do not mention packets, operators, cycles, kernels, schemas, or any \
system internals. Write as if you are the narrator standing inside \
Tony's skull."""

# ---------------------------------------------------------------------------
# Situation and family labels for the packet
# ---------------------------------------------------------------------------

SITUATION_LABELS = {
    "graffito_street_overload": "Street — school release, older kids, sensory overload, near-miss",
    "graffito_grandma_apartment": "Grandma's apartment — Monk is home, paint and rhythm, Grandma's challenge",
    "graffito_night_mural": "Night mural — Monk painting the sky, cops closing in, the Can in Tony's hand",
}

FAMILY_LABELS = {
    "rationalization": "Tony is reframing — finding a way to make the difficult thing mean something different",
    "reversal": "Tony is replaying — imagining how things could have gone differently",
    "rehearsal": "Tony is practicing — counting strokes, finding rhythm, building embodied control",
    "roving": "Tony is drifting — attention moves to something pleasant, a momentary escape",
}

REG_LABELS = {
    "overloaded": "OVERLOADED — everything hitting at once, no filter",
    "bracing": "BRACING — holding on, guarded, managing",
    "entraining": "ENTRAINING — rhythm entering, the body starting to lead",
    "flowing": "FLOWING — integrated, the count and the hand are one thing",
    "creating": "CREATING — the world responds to Tony's movement",
}


def regulation_mode(ts):
    if not ts:
        return "unknown"
    ent = ts.get("entrainment", 0)
    ag = ts.get("felt-agency", 0)
    ctl = ts.get("perceived-control", 0)
    if ent < 0.25 and ag < 0.3 and ctl < 0.3:
        return "overloaded"
    if ent >= 0.6 and ag >= 0.6 and ctl >= 0.6:
        return "flowing"
    if ent >= 0.45:
        return "entraining"
    return "bracing"


def build_packet(cycle_data):
    """Build an LLM-facing packet from one miniworld cycle."""
    c = cycle_data
    goal = c.get("selected_goal", {})
    gm = c.get("debug", {}).get("graffito_miniworld", {})
    ts_before = gm.get("tony-state-before", {})
    ts_after = gm.get("tony-state-after", {})
    emotions = c.get("emotional_state", [])

    sit_id = goal.get("situation_id", "unknown")
    family = goal.get("goal_type", "unknown")
    reg_before = regulation_mode(ts_before)
    reg_after = regulation_mode(ts_after)
    ap_before = gm.get("mural-appraisal-before", "")
    ap_after = gm.get("mural-appraisal-after", "")
    flip = gm.get("reappraisal-flip?", False)

    # Build the user message
    lines = [
        f"Cycle {c['cycle']}",
        f"Situation: {SITUATION_LABELS.get(sit_id, sit_id)}",
        f"What Tony is doing: {FAMILY_LABELS.get(family, family)}",
        f"Regulation state: {REG_LABELS.get(reg_after, reg_after)}",
        f"  (was: {reg_before}, now: {reg_after})",
        f"Tony's state: sensory-load {ts_after.get('sensory-load', 0):.2f}, "
        f"entrainment {ts_after.get('entrainment', 0):.2f}, "
        f"felt-agency {ts_after.get('felt-agency', 0):.2f}, "
        f"control {ts_after.get('perceived-control', 0):.2f}",
    ]

    if emotions:
        emo_strs = [f"{e['affect']} ({e['strength']:.2f})" for e in emotions[:3]]
        lines.append(f"Emotional pressure: {', '.join(emo_strs)}")

    if flip:
        lines.append(
            f"REAPPRAISAL FLIP: the mural just changed from {ap_before} to {ap_after}. "
            f"Same place, same lights, same pressure — but Tony reads it differently now."
        )
    elif ap_after:
        lines.append(f"Mural currently reads as: {ap_after}")

    lines.append("")
    lines.append("Write Tony's inner life for this moment. Return JSON: {thought, image_hint, audio_hint, mood}")

    return "\n".join(lines)


# ---------------------------------------------------------------------------
# LLM call
# ---------------------------------------------------------------------------

def call_anthropic(user_message):
    """Call Anthropic Claude API."""
    import httpx

    api_key = os.environ.get("ANTHROPIC_API_KEY")
    if not api_key:
        raise ValueError("ANTHROPIC_API_KEY not set")

    resp = httpx.post(
        "https://api.anthropic.com/v1/messages",
        headers={
            "x-api-key": api_key,
            "anthropic-version": "2023-06-01",
            "content-type": "application/json",
        },
        json={
            "model": "claude-sonnet-4-20250514",
            "max_tokens": 400,
            "system": SYSTEM_PROMPT,
            "messages": [{"role": "user", "content": user_message}],
        },
        timeout=30.0,
    )
    resp.raise_for_status()
    text = resp.json()["content"][0]["text"]
    # Parse JSON from response
    text = text.strip()
    if text.startswith("```"):
        text = text.split("\n", 1)[1].rsplit("```", 1)[0]
    return json.loads(text)


def call_gemini(user_message):
    """Call Gemini API."""
    from google import genai

    client = genai.Client()
    response = client.models.generate_content(
        model="gemini-2.5-flash",
        contents=f"{SYSTEM_PROMPT}\n\n---\n\n{user_message}",
        config={"response_mime_type": "application/json"},
    )
    return json.loads(response.text)


def call_llm(user_message):
    """Try Anthropic first, fall back to Gemini."""
    if os.environ.get("ANTHROPIC_API_KEY"):
        return call_anthropic(user_message)
    elif os.environ.get("GEMINI_API_KEY") or os.environ.get("GOOGLE_API_KEY"):
        return call_gemini(user_message)
    else:
        raise ValueError("Set ANTHROPIC_API_KEY or GEMINI_API_KEY")


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    repo_root = Path(__file__).resolve().parent.parent.parent
    input_path = repo_root / "out" / "graffito_miniworld.json"
    output_json = repo_root / "out" / "graffito_thought_stream.json"
    output_html = repo_root / "out" / "graffito_thought_stream.html"

    with open(input_path) as f:
        data = json.load(f)

    cycles = data["cycles"]
    results = []

    print(f"Generating prose for {len(cycles)} cycles...")

    for c in cycles:
        packet = build_packet(c)
        cycle_num = c["cycle"]
        goal = c.get("selected_goal", {})
        gm = c.get("debug", {}).get("graffito_miniworld", {})

        print(f"  Cycle {cycle_num}: {goal.get('situation_id', '?')} / {goal.get('goal_type', '?')}...", end=" ", flush=True)

        try:
            result = call_llm(packet)
            print(f"ok ({len(result.get('thought', ''))} chars)")
        except Exception as e:
            print(f"ERROR: {e}")
            result = {
                "thought": f"[Generation failed: {e}]",
                "image_hint": "",
                "audio_hint": "",
                "mood": [],
            }

        results.append({
            "cycle": cycle_num,
            "situation": goal.get("situation_id", ""),
            "family": goal.get("goal_type", ""),
            "regulation_mode": regulation_mode(gm.get("tony-state-after", {})),
            "appraisal": gm.get("mural-appraisal-after", ""),
            "flip": gm.get("reappraisal-flip?", False),
            "scene": result.get("scene", ""),
            "thought": result.get("thought", ""),
            "image_hint": result.get("image_hint", ""),
            "audio_hint": result.get("audio_hint", ""),
            "mood": result.get("mood", []),
        })

        time.sleep(0.5)  # rate limit courtesy

    # Write JSON
    with open(output_json, "w") as f:
        json.dump(results, f, indent=2)
    print(f"\nJSON: {output_json}")

    # Write HTML
    write_html(results, output_html)
    print(f"HTML: {output_html}")


def write_html(results, path):
    sit_colors = {
        "graffito_street_overload": "#e06050",
        "graffito_grandma_apartment": "#50b080",
        "graffito_night_mural": "#6090e0",
    }
    sit_labels = {
        "graffito_street_overload": "Street Overload",
        "graffito_grandma_apartment": "Grandma's Apartment",
        "graffito_night_mural": "Night Mural",
    }
    fam_colors = {
        "rationalization": "#8888cc",
        "reversal": "#cc7777",
        "rehearsal": "#66bb88",
    }
    reg_colors = {
        "overloaded": "#dd5555",
        "bracing": "#cc8844",
        "entraining": "#55aadd",
        "flowing": "#55cc88",
        "creating": "#cc88dd",
    }

    cards = []
    for r in results:
        sit_color = sit_colors.get(r["situation"], "#888")
        sit_label = sit_labels.get(r["situation"], r["situation"])
        fam_color = fam_colors.get(r["family"], "#888")
        reg_color = reg_colors.get(r["regulation_mode"], "#888")
        flip_html = '<span class="flip-badge">REAPPRAISAL FLIP</span>' if r["flip"] else ""
        mood_raw = r["mood"]
        if isinstance(mood_raw, str):
            mood_raw = [m.strip() for m in mood_raw.split(",")]
        mood_html = " · ".join(mood_raw) if mood_raw else ""

        cards.append(f"""
        <div class="cycle-card {'flip' if r['flip'] else ''}">
          <div class="cycle-header">
            <span class="cycle-num">C{r['cycle']:02d}</span>
            <span class="situation" style="color:{sit_color}">{sit_label}</span>
            <span class="family-badge" style="color:{fam_color}">{r['family']}</span>
            <span class="reg" style="color:{reg_color}">{r['regulation_mode']}</span>
            {flip_html}
          </div>
          {f'<div class="scene">{r["scene"]}</div>' if r.get('scene') else ''}
          <div class="thought">{r['thought']}</div>
          <div class="hints">
            <span class="hint-img">{r['image_hint']}</span>
            <span class="hint-sep">·</span>
            <span class="hint-aud">{r['audio_hint']}</span>
          </div>
          {f'<div class="mood">{mood_html}</div>' if mood_html else ''}
        </div>""")

    html = f"""<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Graffito — Tony's Inner Life</title>
<style>
* {{ box-sizing: border-box; margin: 0; padding: 0; }}
body {{ font-family: Georgia, serif; background: #0a0a0f; color: #c8c8d0; padding: 32px; max-width: 720px; margin: 0 auto; line-height: 1.7; }}
h1 {{ font-family: 'SF Mono', monospace; font-size: 16px; color: #e0e0e8; margin-bottom: 4px; letter-spacing: 1px; }}
.subtitle {{ font-size: 13px; color: #666; margin-bottom: 32px; font-family: 'SF Mono', monospace; }}
.cycle-card {{ margin-bottom: 28px; padding-bottom: 24px; border-bottom: 1px solid #1a1a24; }}
.cycle-card.flip {{ border-bottom-color: #d4a017; }}
.cycle-header {{ display: flex; align-items: center; gap: 10px; margin-bottom: 10px; font-family: 'SF Mono', monospace; font-size: 11px; }}
.cycle-num {{ color: #444; }}
.situation {{ font-weight: 600; }}
.family-badge {{ padding: 1px 8px; border-radius: 10px; background: #1a1a24; font-size: 10px; }}
.reg {{ margin-left: auto; font-size: 10px; }}
.flip-badge {{ padding: 1px 8px; border-radius: 10px; background: #3a2a00; color: #d4a017; font-size: 10px; font-weight: 700; letter-spacing: 0.5px; }}
.scene {{ font-size: 13px; color: #777; line-height: 1.6; margin-bottom: 10px; font-style: italic; }}
.thought {{ font-size: 16px; color: #b8b8c4; line-height: 1.8; margin-bottom: 8px; }}
.hints {{ font-family: 'SF Mono', monospace; font-size: 11px; color: #555; font-style: italic; }}
.hint-sep {{ margin: 0 6px; }}
.mood {{ font-family: 'SF Mono', monospace; font-size: 10px; color: #444; margin-top: 4px; }}
</style>
</head>
<body>
<h1>TONY'S INNER LIFE</h1>
<div class="subtitle">20 cycles of autonomous inner life · Graffito miniworld</div>
{''.join(cards)}
</body>
</html>"""

    with open(path, "w") as f:
        f.write(html)


if __name__ == "__main__":
    main()
