#!/usr/bin/env python3
"""
Listen to a traversal trace through Lyria RealTime.

Maps traversal state (tension, energy, intent, scene text)
to Lyria parameters (prompt, BPM, density, brightness, guidance)
and plays the trace as a continuous musical experience.

Usage:
    python listen_trace.py \
        --dir daydreaming/out/city_routes_watchtest \
        --arm feature

    # With custom dwell time (seconds per cycle):
    python listen_trace.py \
        --dir daydreaming/out/city_routes_watchtest \
        --arm feature \
        --dwell 8

    # Also show the inner-life view alongside audio:
    python listen_trace.py \
        --dir daydreaming/out/city_routes_watchtest \
        --arm feature \
        --show-trace

Requires: google-genai, sounddevice (or pyaudio)
Set GEMINI_API_KEY or GOOGLE_API_KEY in environment.
"""

import argparse
import json
import sys
import time
from pathlib import Path

# Import LyriaSession from local copy
sys.path.insert(0, str(Path(__file__).parent / "tools"))
from lyria_session import LyriaSession  # noqa: E402


# ---------------------------------------------------------------------------
# Traversal state → Lyria parameter mapping
# ---------------------------------------------------------------------------
# These are starting points. Tune them interactively.

INTENT_BPM = {
    "dwell": 85,
    "escalate": 130,
    "shift": 105,
    "release": 72,
    "hold": 90,
    "recall": 95,
}

FAMILY_BRIGHTNESS = {
    "ROVING": 0.75,
    "REVERSAL": 0.25,
    "RATIONALIZATION": 0.45,
    "REHEARSAL": 0.55,
    "REVENGE": 0.20,
    "RECOVERY": 0.65,
    "REPERCUSSIONS": 0.35,
}

DEFAULT_BPM = 100
DEFAULT_BRIGHTNESS = 0.5
DEFAULT_DWELL_S = 6.0


def tension_to_guidance(tension: float) -> float:
    """Higher tension → stronger prompt adherence (more intense)."""
    return 1.0 + tension * 5.0  # 1.0-6.0


def energy_to_density(energy: float) -> float:
    """Direct mapping — higher energy → denser arrangement."""
    return max(0.1, min(1.0, energy))


def derive_music_prompt(scene_text: str, intent: str, family: str | None) -> str:
    """Derive a Lyria-friendly music prompt from scene text + state.

    Keeps it atmospheric rather than literal. Prepends mood framing
    based on intent/family, then uses the scene text's emotional
    content.
    """
    mood_prefix = ""
    if intent == "escalate":
        mood_prefix = "Tense, building, urgent. "
    elif intent == "dwell":
        mood_prefix = "Suspended, still, watchful. "
    elif intent == "shift":
        mood_prefix = "Transitional, unsettled, moving. "
    elif intent == "release":
        mood_prefix = "Releasing, exhaling, settling. "

    if family == "ROVING":
        mood_prefix = "Warm, expansive, drifting. "
    elif family == "REVERSAL":
        mood_prefix = "Dark, sudden shift, dissonant. "

    # Use the scene text but keep it short for Lyria
    # (Lyria works best with mood/atmosphere descriptions)
    scene_short = scene_text[:200] if len(scene_text) > 200 else scene_text

    return f"{mood_prefix}{scene_short}"


def format_cycle_brief(cycle_data: dict, scene_text: str) -> str:
    """One-line cycle summary for console during playback."""
    cycle = cycle_data.get("cycle", "?")
    intent = cycle_data.get("traversal_intent", "—")
    tension = cycle_data.get("tension", 0.0)
    energy = cycle_data.get("energy", 0.0)
    family = cycle_data.get("selected_family") or ""
    node = cycle_data.get("selected_node", "?")

    # Shorten node ID
    short_node = node.split("_", 2)[-1] if "_" in node else node

    t_bar = "█" * int(tension * 10) + "░" * (10 - int(tension * 10))
    e_bar = "█" * int(energy * 10) + "░" * (10 - int(energy * 10))

    family_tag = f" [{family}]" if family else ""
    return (
        f"  Cycle {cycle:>2}  {intent.upper():<10}{family_tag}\n"
        f"           T {t_bar} {tension:.2f}  E {e_bar} {energy:.2f}\n"
        f"           {short_node}\n"
        f"           {scene_text[:80]}..."
        if len(scene_text) > 80
        else f"  Cycle {cycle:>2}  {intent.upper():<10}{family_tag}\n"
        f"           T {t_bar} {tension:.2f}  E {e_bar} {energy:.2f}\n"
        f"           {short_node}\n"
        f"           {scene_text}"
    )


def main():
    parser = argparse.ArgumentParser(description="Listen to a traversal trace")
    parser.add_argument("--debug", type=Path, help="Path to debug JSONL")
    parser.add_argument("--playlist", type=Path, help="Path to playlist TXT")
    parser.add_argument("--dir", type=Path, help="Output directory")
    parser.add_argument("--arm", default="feature", help="Arm name when using --dir")
    parser.add_argument(
        "--dwell", type=float, default=DEFAULT_DWELL_S,
        help=f"Seconds per cycle (default: {DEFAULT_DWELL_S})"
    )
    parser.add_argument(
        "--audio-sink", default="speaker",
        choices=["speaker", "wav"],
        help="Audio output (default: speaker)"
    )
    parser.add_argument(
        "--show-trace", action="store_true",
        help="Also print the full inner-life view per cycle"
    )
    parser.add_argument(
        "--initial-bpm", type=int, default=DEFAULT_BPM,
        help=f"Starting BPM (default: {DEFAULT_BPM})"
    )
    args = parser.parse_args()

    # Resolve file paths
    if args.dir:
        candidates = list(args.dir.glob(f"*_{args.arm}_debug.jsonl"))
        if not candidates:
            raise FileNotFoundError(f"No debug JSONL for arm '{args.arm}' in {args.dir}")
        args.debug = candidates[0]
        playlist_candidates = list(args.dir.glob(f"*_{args.arm}_playlist.txt"))
        if not playlist_candidates:
            raise FileNotFoundError(f"No playlist for arm '{args.arm}' in {args.dir}")
        args.playlist = playlist_candidates[0]

    # Load data
    cycles = []
    with open(args.debug) as f:
        for line in f:
            line = line.strip()
            if line:
                cycles.append(json.loads(line))

    scenes = args.playlist.read_text().strip().split("\n")

    print()
    print("╔═══════════════════════════════════════════════════════╗")
    print("║        TRAVERSAL TRACE — LYRIA AUDIO PLAYBACK        ║")
    print("╠═══════════════════════════════════════════════════════╣")
    print(f"║  Cycles: {len(cycles):<44} ║")
    print(f"║  Dwell:  {args.dwell}s per cycle{' ' * (35 - len(str(args.dwell)))} ║")
    print(f"║  Sink:   {args.audio_sink:<44} ║")
    print("╚═══════════════════════════════════════════════════════╝")
    print()

    # Start Lyria session
    print("  Starting Lyria session...")
    session = LyriaSession(
        initial_bpm=args.initial_bpm,
        audio_sink=args.audio_sink,
    )
    session.start()
    time.sleep(2)  # Let connection establish

    try:
        # Import WeightedPrompt
        from google.genai import types
        WeightedPrompt = types.WeightedPrompt

        for i, cycle_data in enumerate(cycles):
            scene = scenes[i] if i < len(scenes) else "(no scene text)"

            intent = cycle_data.get("traversal_intent", "dwell")
            tension = cycle_data.get("tension", 0.5)
            energy = cycle_data.get("energy", 0.5)
            family = cycle_data.get("selected_family")

            # Derive music parameters
            music_prompt = derive_music_prompt(scene, intent, family)
            bpm = INTENT_BPM.get(intent, DEFAULT_BPM)
            density = energy_to_density(energy)
            guidance = tension_to_guidance(tension)
            brightness = FAMILY_BRIGHTNESS.get(family, DEFAULT_BRIGHTNESS)

            # Send to Lyria
            session.enqueue_prompts([
                WeightedPrompt(text=music_prompt, weight=1.0)
            ])
            session.enqueue_config(
                density=density,
                brightness=brightness,
                guidance=guidance,
            )

            # BPM changes require choreographed reset
            if i == 0:
                session.enqueue_config(bpm=bpm)
                session.enqueue_play()
            elif bpm != INTENT_BPM.get(
                cycles[i - 1].get("traversal_intent", "dwell"), DEFAULT_BPM
            ):
                session.enqueue_choreographed_reset(bpm=bpm)

            # Display
            if args.show_trace:
                # Import and use watch_trace formatter
                sys.path.insert(0, str(Path(__file__).parent.parent))
                from daydreaming.watch_trace import format_cycle
                print(format_cycle(cycle_data, scene))
            else:
                print(format_cycle_brief(cycle_data, scene))

            print(
                f"  ♪ bpm={bpm} density={density:.2f} "
                f"brightness={brightness:.2f} guidance={guidance:.1f}"
            )
            print()

            # Dwell
            time.sleep(args.dwell)

        # Let the last cycle ring out
        print("  ... letting final cycle ring out (5s)")
        time.sleep(5)

    except KeyboardInterrupt:
        print("\n  Interrupted.")
    finally:
        print("  Stopping Lyria session...")
        session.stop()
        print("  Done.")


if __name__ == "__main__":
    main()
