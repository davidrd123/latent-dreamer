#!/usr/bin/env python3
"""
Watch a traversal trace as a human-readable temporal sequence.

Formats debug JSONL + playlist into a per-cycle experience:
  - what the scheduler chose and why
  - the inner-life state (tension, energy, intent)
  - situation activation landscape
  - the scene text

Usage:
    python watch_trace.py \
        --debug daydreaming/out/city_routes_watchtest/city_routes_run_3_feature_debug.jsonl \
        --playlist daydreaming/out/city_routes_watchtest/city_routes_run_3_feature_playlist.txt

    # Or just point at a directory and arm:
    python watch_trace.py \
        --dir daydreaming/out/city_routes_watchtest \
        --arm feature
"""

import argparse
import json
from pathlib import Path


def bar(value: float, width: int = 20) -> str:
    filled = int(value * width)
    return "█" * filled + "░" * (width - filled)


def format_cycle(cycle_data: dict, scene_text: str) -> str:
    lines = []
    cycle = cycle_data.get("cycle", "?")
    node = cycle_data.get("selected_node", cycle_data.get("previous_node", "?"))
    intent = cycle_data.get("traversal_intent", "—")
    tension = cycle_data.get("tension", 0.0)
    energy = cycle_data.get("energy", 0.0)
    family = cycle_data.get("selected_family")

    # Header
    lines.append(f"━━━ Cycle {cycle} ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    lines.append("")

    # Intent + family
    intent_display = intent.upper()
    if family:
        intent_display += f"  [{family}]"
    lines.append(f"  Intent:  {intent_display}")
    lines.append("")

    # Tension / energy bars
    lines.append(f"  Tension: {bar(tension)} {tension:.2f}")
    lines.append(f"  Energy:  {bar(energy)} {energy:.2f}")
    lines.append("")

    # Situation activation
    sit_act = cycle_data.get("situation_activation", {})
    if sit_act:
        active = {k: v for k, v in sit_act.items() if v > 0.01}
        if active:
            lines.append("  Situations alive:")
            for sit_id, activation in sorted(active.items(), key=lambda x: -x[1]):
                short_name = sit_id.replace("s", "").replace("_", " ").strip()
                lines.append(f"    {bar(activation, 12)} {activation:.2f}  {short_name}")
            lines.append("")

    # Selection reason (if available)
    selection = cycle_data.get("selection", {})
    if selection:
        evals = selection.get("evaluations", [])
        if len(evals) > 1:
            # Show competition
            ranked = sorted(evals, key=lambda e: -e.get("total_weight", 0))
            winner = ranked[0]
            runner = ranked[1] if len(ranked) > 1 else None
            winner_id = winner.get("candidate_id", "?").split("_", 2)[-1] if winner else "?"
            lines.append(f"  Chose:   {winner_id}")
            lines.append(f"           score {winner.get('feature_score', 0):.3f}  weight {winner.get('total_weight', 0):.3f}")
            if runner:
                runner_id = runner.get("candidate_id", "?").split("_", 2)[-1] if runner else "?"
                lines.append(f"  Over:    {runner_id}")
                lines.append(f"           score {runner.get('feature_score', 0):.3f}  weight {runner.get('total_weight', 0):.3f}")

            # Why this one won
            reasons = winner.get("priority_reasons", [])
            if reasons:
                lines.append(f"  Because: {', '.join(reasons)}")

            lines.append("")

    # Event approach
    event_counts = cycle_data.get("event_approach_count", {})
    approaching = {k: v for k, v in event_counts.items() if v > 0}
    if approaching:
        lines.append(f"  Events approaching: {', '.join(f'{k}({v})' for k, v in approaching.items())}")
        lines.append("")

    # The scene
    lines.append(f"  ┌─────────────────────────────────────────────────")
    # Word-wrap scene text at ~60 chars
    words = scene_text.strip().split()
    current_line = "  │ "
    for word in words:
        if len(current_line) + len(word) + 1 > 62:
            lines.append(current_line)
            current_line = "  │ " + word
        else:
            current_line += (" " if len(current_line) > 4 else "") + word
    if current_line.strip("│ "):
        lines.append(current_line)
    lines.append(f"  └─────────────────────────────────────────────────")
    lines.append("")

    return "\n".join(lines)


def main():
    parser = argparse.ArgumentParser(description="Watch a traversal trace")
    parser.add_argument("--debug", type=Path, help="Path to debug JSONL")
    parser.add_argument("--playlist", type=Path, help="Path to playlist TXT")
    parser.add_argument("--dir", type=Path, help="Output directory (alternative to --debug/--playlist)")
    parser.add_argument("--arm", default="feature", help="Arm name when using --dir")
    parser.add_argument("--pause", action="store_true", help="Pause between cycles (press enter to advance)")
    args = parser.parse_args()

    if args.dir:
        # Find files by pattern
        candidates = list(args.dir.glob(f"*_{args.arm}_debug.jsonl"))
        if not candidates:
            raise FileNotFoundError(f"No debug JSONL found for arm '{args.arm}' in {args.dir}")
        args.debug = candidates[0]
        playlist_candidates = list(args.dir.glob(f"*_{args.arm}_playlist.txt"))
        if not playlist_candidates:
            raise FileNotFoundError(f"No playlist found for arm '{args.arm}' in {args.dir}")
        args.playlist = playlist_candidates[0]

    # Load debug data
    cycles = []
    with open(args.debug) as f:
        for line in f:
            line = line.strip()
            if line:
                cycles.append(json.loads(line))

    # Load playlist
    scenes = args.playlist.read_text().strip().split("\n")

    # Print header
    print()
    print("╔═══════════════════════════════════════════════════════╗")
    print("║           TRAVERSAL TRACE — INNER LIFE VIEW          ║")
    print("╠═══════════════════════════════════════════════════════╣")
    print(f"║  Source: {args.debug.name:<44} ║")
    print(f"║  Cycles: {len(cycles):<43} ║")
    print("╚═══════════════════════════════════════════════════════╝")
    print()

    for i, cycle_data in enumerate(cycles):
        scene = scenes[i] if i < len(scenes) else "(no scene text)"
        output = format_cycle(cycle_data, scene)
        print(output)
        if args.pause:
            input("  [press enter for next cycle]")


if __name__ == "__main__":
    main()
