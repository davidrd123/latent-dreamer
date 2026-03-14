#!/usr/bin/env python3
from __future__ import annotations

import argparse
from pathlib import Path

from graffito_pilot import Graph, PilotConfig, run_pilot, write_outputs
from render_trace import render_v0


DEFAULT_GRAPH = Path("daydreaming/fixtures/city_routes_experiment_1_v0.yaml")
DEFAULT_START = "c_s1_n01_last_train_doors_shut"


def render_playlist(graph: Graph, trace_payload: dict, output_path: Path) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)
    lines: list[str] = []
    for entry in trace_payload["trace"]:
        node = graph.nodes_by_id[entry["node"]]
        lines.append(render_v0(node, entry.get("family")))
    output_path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def config_for(mode: str, args: argparse.Namespace) -> PilotConfig:
    return PilotConfig(
        mode=mode,
        cycles=args.cycles,
        start_node=args.start_node,
        seed=args.seed,
        activation_decay=args.activation_decay,
        activation_boost=args.activation_boost,
        revisit_penalty=args.revisit_penalty,
        continuity_bias=args.continuity_bias,
        temperature=args.temperature,
        conductor_tension_bias=args.conductor_tension_bias if mode in {"pilot", "feature"} else 0.0,
        conductor_energy_bias=args.conductor_energy_bias if mode in {"pilot", "feature"} else 0.0,
        initial_tension=args.initial_tension,
        initial_energy=args.initial_energy,
        release_start_progress=args.release_start_progress,
        target_tension_peak=args.target_tension_peak,
        target_tension_end=args.target_tension_end,
        target_energy_peak=args.target_energy_peak,
        target_energy_end=args.target_energy_end,
    )


def run_arm(
    graph: Graph,
    args: argparse.Namespace,
    mode: str,
    label: str,
) -> tuple[Path, Path, Path]:
    config = config_for(mode, args)
    trace_path = args.output_dir / f"{args.prefix}_{label}_trace.yaml"
    debug_path = args.output_dir / f"{args.prefix}_{label}_debug.jsonl"
    playlist_path = args.output_dir / f"{args.prefix}_{label}_playlist.txt"

    trace_payload, debug_rows = run_pilot(graph, config)
    write_outputs(trace_payload, debug_rows, trace_path, debug_path)
    if not args.skip_render:
        render_playlist(graph, trace_payload, playlist_path)
    return trace_path, debug_path, playlist_path


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Run baseline, Façade, and feature-registry arms on the City Routes graph."
    )
    parser.add_argument("--graph", type=Path, default=DEFAULT_GRAPH)
    parser.add_argument("--start-node", default=DEFAULT_START)
    parser.add_argument("--cycles", type=int, default=18)
    parser.add_argument("--seed", type=int, default=7)
    parser.add_argument("--output-dir", type=Path, default=Path("daydreaming/out"))
    parser.add_argument("--prefix", default="city_routes_run_3")
    parser.add_argument("--skip-render", action="store_true")
    parser.add_argument("--activation-decay", type=float, default=0.86)
    parser.add_argument("--activation-boost", type=float, default=0.32)
    parser.add_argument("--revisit-penalty", type=float, default=0.55)
    parser.add_argument("--continuity-bias", type=float, default=1.18)
    parser.add_argument("--temperature", type=float, default=1.0)
    parser.add_argument("--conductor-tension-bias", type=float, default=0.0)
    parser.add_argument("--conductor-energy-bias", type=float, default=0.0)
    parser.add_argument("--initial-tension", type=float, default=0.2)
    parser.add_argument("--initial-energy", type=float, default=0.4)
    parser.add_argument("--release-start-progress", type=float, default=0.70)
    parser.add_argument("--target-tension-peak", type=float, default=0.90)
    parser.add_argument("--target-tension-end", type=float, default=0.15)
    parser.add_argument("--target-energy-peak", type=float, default=0.75)
    parser.add_argument("--target-energy-end", type=float, default=0.45)
    return parser


def main() -> None:
    parser = build_parser()
    args = parser.parse_args()
    graph = Graph.load(args.graph)

    results = [
        ("baseline", "baseline"),
        ("pilot", "scheduler"),
        ("feature", "feature"),
    ]
    for mode, label in results:
        trace_path, debug_path, playlist_path = run_arm(graph, args, mode, label)
        print(f"{label}:")
        print(f"  trace: {trace_path}")
        print(f"  debug: {debug_path}")
        if not args.skip_render:
            print(f"  playlist: {playlist_path}")


if __name__ == "__main__":
    main()
