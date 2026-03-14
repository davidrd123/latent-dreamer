#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
from dataclasses import dataclass
from pathlib import Path
from statistics import mean

from city_routes_sweep import ConductorPreset, build_config
from graffito_pilot import Graph, run_pilot


DEFAULT_GRAPH = Path("daydreaming/fixtures/city_routes_experiment_1_v0.yaml")
DEFAULT_START = "c_s1_n01_last_train_doors_shut"
DEFAULT_OUTPUT_JSON = Path("daydreaming/out/city_routes_contrasting_conductor_results.json")
DEFAULT_NOTE = Path(
    "daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/"
    "25-city-routes-contrasting-conductor.md"
)


@dataclass(frozen=True, slots=True)
class TargetFocus:
    situation_id: str | None
    event_id: str | None


PRESET_TARGETS: dict[str, TargetFocus] = {
    "neutral": TargetFocus(None, None),
    "spectacle_hold": TargetFocus("s4_public_spectacle", "e2_blackout_siren"),
    "threshold_drive": TargetFocus("s5_the_threshold", "e4_bridge_lockdown"),
    "refuge_hold": TargetFocus("s3_false_refuge", None),
    "exchange_fast": TargetFocus("s6_the_exchange", "e3_wrong_handoff"),
}


def targeted_presets() -> list[ConductorPreset]:
    return [
        ConductorPreset("neutral", 0.0, 0.0),
        ConductorPreset(
            "spectacle_hold",
            0.02,
            0.04,
            situation_biases={
                "s4_public_spectacle": 0.70,
                "s5_the_threshold": -0.55,
                "s6_the_exchange": -0.25,
            },
            event_biases={
                "e2_blackout_siren": 0.60,
                "e4_bridge_lockdown": -0.50,
            },
        ),
        ConductorPreset(
            "threshold_drive",
            0.10,
            0.06,
            situation_biases={
                "s5_the_threshold": 0.75,
                "s3_false_refuge": -0.45,
                "s4_public_spectacle": -0.20,
            },
            event_biases={
                "e4_bridge_lockdown": 0.65,
                "e2_blackout_siren": -0.30,
            },
        ),
        ConductorPreset(
            "refuge_hold",
            -0.08,
            -0.08,
            situation_biases={
                "s3_false_refuge": 0.75,
                "s6_the_exchange": -0.55,
                "s5_the_threshold": -0.25,
            },
            event_biases={
                "e3_wrong_handoff": -0.55,
                "e4_bridge_lockdown": -0.25,
            },
        ),
        ConductorPreset(
            "exchange_fast",
            0.08,
            0.02,
            situation_biases={
                "s6_the_exchange": 0.75,
                "s4_public_spectacle": -0.45,
                "s3_false_refuge": -0.20,
            },
            event_biases={
                "e3_wrong_handoff": 0.65,
                "e2_blackout_siren": -0.45,
            },
        ),
    ]


def summarize_feature_run(graph: Graph, trace_payload: dict) -> dict:
    trace = trace_payload["trace"]
    nodes = graph.nodes_by_id
    path = [step["node"] for step in trace]
    situation_counts: dict[str, int] = {}
    event_counts: dict[str, int] = {}
    first_situation_cycle: dict[str, int] = {}
    first_event_cycle: dict[str, int] = {}

    for step in trace:
        node = nodes[step["node"]]
        situation_id = str(node["situation_id"])
        event_id = node.get("event_id")
        situation_counts[situation_id] = situation_counts.get(situation_id, 0) + 1
        first_situation_cycle.setdefault(situation_id, step["cycle"])
        if event_id:
            event_key = str(event_id)
            event_counts[event_key] = event_counts.get(event_key, 0) + 1
            first_event_cycle.setdefault(event_key, step["cycle"])

    return {
        "path": path,
        "situation_counts": situation_counts,
        "event_counts": event_counts,
        "first_situation_cycle": first_situation_cycle,
        "first_event_cycle": first_event_cycle,
    }


def run_targeted_suite(
    graph: Graph,
    start_node: str,
    cycles: int,
    seeds: list[int],
) -> dict:
    presets = targeted_presets()
    results: list[dict] = []

    for seed in seeds:
        for preset in presets:
            config = build_config(
                mode="feature",
                start_node=start_node,
                cycles=cycles,
                seed=seed,
                preset=preset,
            )
            trace_payload, _debug_rows = run_pilot(graph, config)
            summary = summarize_feature_run(graph, trace_payload)
            results.append(
                {
                    "seed": seed,
                    "preset": preset.name,
                    "situation_biases": preset.situation_biases,
                    "event_biases": preset.event_biases,
                    "summary": summary,
                }
            )

    grouped: dict[int, dict[str, dict]] = {}
    for result in results:
        grouped.setdefault(result["seed"], {})[result["preset"]] = result["summary"]

    aggregate = {
        "seeds_with_distinct_paths": 0,
        "spectacle_hold_strengthened": 0,
        "threshold_drive_strengthened": 0,
        "refuge_hold_strengthened": 0,
        "exchange_fast_strengthened": 0,
        "spectacle_hold_e2_earlier": 0,
        "threshold_drive_e4_earlier": 0,
        "exchange_fast_e3_earlier": 0,
    }

    for seed, cases in grouped.items():
        distinct_paths = {tuple(summary["path"]) for summary in cases.values()}
        if len(distinct_paths) > 1:
            aggregate["seeds_with_distinct_paths"] += 1

        neutral = cases["neutral"]
        spectacle = cases["spectacle_hold"]
        threshold = cases["threshold_drive"]
        refuge = cases["refuge_hold"]
        exchange = cases["exchange_fast"]

        if spectacle["situation_counts"].get("s4_public_spectacle", 0) > neutral["situation_counts"].get("s4_public_spectacle", 0):
            aggregate["spectacle_hold_strengthened"] += 1
        if threshold["situation_counts"].get("s5_the_threshold", 0) > neutral["situation_counts"].get("s5_the_threshold", 0):
            aggregate["threshold_drive_strengthened"] += 1
        if refuge["situation_counts"].get("s3_false_refuge", 0) > neutral["situation_counts"].get("s3_false_refuge", 0):
            aggregate["refuge_hold_strengthened"] += 1
        if exchange["situation_counts"].get("s6_the_exchange", 0) > neutral["situation_counts"].get("s6_the_exchange", 0):
            aggregate["exchange_fast_strengthened"] += 1

        neutral_blackout = neutral["first_event_cycle"].get("e2_blackout_siren", 999)
        spectacle_blackout = spectacle["first_event_cycle"].get("e2_blackout_siren", 999)
        if spectacle_blackout < neutral_blackout:
            aggregate["spectacle_hold_e2_earlier"] += 1

        neutral_lockdown = neutral["first_event_cycle"].get("e4_bridge_lockdown", 999)
        threshold_lockdown = threshold["first_event_cycle"].get("e4_bridge_lockdown", 999)
        if threshold_lockdown < neutral_lockdown:
            aggregate["threshold_drive_e4_earlier"] += 1

        neutral_exchange = neutral["first_event_cycle"].get("e3_wrong_handoff", 999)
        exchange_fast = exchange["first_event_cycle"].get("e3_wrong_handoff", 999)
        if exchange_fast < neutral_exchange:
            aggregate["exchange_fast_e3_earlier"] += 1

    aggregate["avg_distinct_paths_per_seed"] = round(
        mean(
            len({tuple(summary["path"]) for summary in cases.values()})
            for cases in grouped.values()
        ),
        2,
    )

    return {
        "graph_id": graph.graph_id,
        "cycles": cycles,
        "seeds": seeds,
        "results": results,
        "aggregate": aggregate,
    }


def write_markdown(payload: dict, output_path: Path) -> None:
    aggregate = payload["aggregate"]
    lines = [
        "# City Routes Contrasting Conductor Sweep",
        "",
        "Date: 2026-03-14",
        "",
        "## Purpose",
        "",
        "Test whether contrasting feature-level conductor biases can",
        "bend the City Routes feature arm away from its default",
        "structural route rather than simply reinforcing it.",
        "",
        "## Sweep",
        "",
        f"- seeds: `{', '.join(str(seed) for seed in payload['seeds'])}`",
        "- presets: `neutral`, `spectacle_hold`, `threshold_drive`, `refuge_hold`, `exchange_fast`",
        "- arm: `feature` only",
        "",
        "## Aggregate",
        "",
        f"- seeds with distinct preset paths: `{aggregate['seeds_with_distinct_paths']}/{len(payload['seeds'])}`",
        f"- avg distinct paths per seed: `{aggregate['avg_distinct_paths_per_seed']}`",
        f"- spectacle hold increased `s4` presence: `{aggregate['spectacle_hold_strengthened']}` seeds",
        f"- threshold drive increased `s5` presence: `{aggregate['threshold_drive_strengthened']}` seeds",
        f"- refuge hold increased `s3` presence: `{aggregate['refuge_hold_strengthened']}` seeds",
        f"- exchange fast increased `s6` presence: `{aggregate['exchange_fast_strengthened']}` seeds",
        f"- spectacle hold reached `e2` earlier: `{aggregate['spectacle_hold_e2_earlier']}` seeds",
        f"- threshold drive reached `e4` earlier: `{aggregate['threshold_drive_e4_earlier']}` seeds",
        f"- exchange fast reached `e3` earlier: `{aggregate['exchange_fast_e3_earlier']}` seeds",
        "",
        "## Per-run Summary",
        "",
        "| Seed | Preset | s3 | s4 | s5 | s6 | Events | first e2 | first e3 | first e4 |",
        "|---|---|---:|---:|---:|---:|---|---:|---:|---:|",
    ]

    for result in payload["results"]:
        summary = result["summary"]
        events = ",".join(sorted(summary["event_counts"].keys())) or "-"
        lines.append(
            "| "
            + f"{result['seed']} | "
            + f"{result['preset']} | "
            + f"{summary['situation_counts'].get('s3_false_refuge', 0)} | "
            + f"{summary['situation_counts'].get('s4_public_spectacle', 0)} | "
            + f"{summary['situation_counts'].get('s5_the_threshold', 0)} | "
            + f"{summary['situation_counts'].get('s6_the_exchange', 0)} | "
            + f"{events} | "
            + f"{summary['first_event_cycle'].get('e2_blackout_siren', '-')} | "
            + f"{summary['first_event_cycle'].get('e3_wrong_handoff', '-')} | "
            + f"{summary['first_event_cycle'].get('e4_bridge_lockdown', '-')} |"
        )

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Run a targeted-conductor sweep over the City Routes feature arm."
    )
    parser.add_argument("--graph", type=Path, default=DEFAULT_GRAPH)
    parser.add_argument("--start-node", default=DEFAULT_START)
    parser.add_argument("--cycles", type=int, default=18)
    parser.add_argument("--seeds", default="7,11,19")
    parser.add_argument("--output-json", type=Path, default=DEFAULT_OUTPUT_JSON)
    parser.add_argument("--note-output", type=Path, default=DEFAULT_NOTE)
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    seeds = [int(seed.strip()) for seed in args.seeds.split(",") if seed.strip()]
    graph = Graph.load(args.graph)
    payload = run_targeted_suite(
        graph=graph,
        start_node=args.start_node,
        cycles=args.cycles,
        seeds=seeds,
    )
    args.output_json.parent.mkdir(parents=True, exist_ok=True)
    args.output_json.write_text(json.dumps(payload, indent=2), encoding="utf-8")
    write_markdown(payload, args.note_output)
    print(f"wrote json: {args.output_json}")
    print(f"wrote note: {args.note_output}")


if __name__ == "__main__":
    main()
