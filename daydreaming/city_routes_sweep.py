#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
from collections import Counter, defaultdict
from dataclasses import dataclass, field
from pathlib import Path
from statistics import mean

from graffito_pilot import Graph, PilotConfig, run_pilot


DEFAULT_GRAPH = Path("daydreaming/fixtures/city_routes_experiment_1_v0.yaml")
DEFAULT_START = "c_s1_n01_last_train_doors_shut"
DEFAULT_OUTPUT_JSON = Path("daydreaming/out/city_routes_sweep_results.json")
DEFAULT_NOTE = Path(
    "daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/"
    "21-city-routes-robustness-sweep.md"
)


@dataclass(frozen=True, slots=True)
class ConductorPreset:
    name: str
    tension_bias: float
    energy_bias: float
    situation_biases: dict[str, float] = field(default_factory=dict)
    event_biases: dict[str, float] = field(default_factory=dict)


def default_presets() -> list[ConductorPreset]:
    return [
        ConductorPreset("neutral", 0.0, 0.0),
        ConductorPreset("sustained_high", 0.15, 0.10),
        ConductorPreset("early_release", -0.15, -0.10),
    ]


def build_config(
    mode: str,
    start_node: str,
    cycles: int,
    seed: int,
    preset: ConductorPreset,
) -> PilotConfig:
    return PilotConfig(
        mode=mode,
        cycles=cycles,
        start_node=start_node,
        seed=seed,
        activation_decay=0.86,
        activation_boost=0.32,
        revisit_penalty=0.55,
        continuity_bias=1.18,
        temperature=1.0,
        conductor_tension_bias=preset.tension_bias if mode in {"pilot", "feature"} else 0.0,
        conductor_energy_bias=preset.energy_bias if mode in {"pilot", "feature"} else 0.0,
        initial_tension=0.2,
        initial_energy=0.4,
        release_start_progress=0.70,
        target_tension_peak=0.90,
        target_tension_end=0.15,
        target_energy_peak=0.75,
        target_energy_end=0.45,
        conductor_situation_biases=dict(preset.situation_biases),
        conductor_event_biases=dict(preset.event_biases),
    )


def surfaced_refs_from_node(node: dict) -> set[str]:
    surfaced: set[str] = {str(node["id"])}
    event_id = node.get("event_id")
    if event_id:
        surfaced.add(str(event_id))
    surfaced.add(str(node.get("situation_id", "")))
    for situation_id in node.get("situation_ids", []):
        surfaced.add(str(situation_id))
    for payoff_ref in node.get("payoff_refs", []):
        surfaced.add(str(payoff_ref))
    return surfaced


def summarize_trace(graph: Graph, trace_payload: dict) -> dict:
    trace = trace_payload["trace"]
    nodes = graph.nodes_by_id
    node_path = [step["node"] for step in trace]
    situations = [nodes[node_id]["situation_id"] for node_id in node_path]
    event_ids = [str(nodes[node_id]["event_id"]) for node_id in node_path if nodes[node_id].get("event_id")]
    tension_values = [float(step["tension"]) for step in trace]

    surfaced_refs: set[str] = set()
    prepared_event_visits_any = 0
    prepared_event_visits_full = 0
    event_prep_details: list[dict[str, object]] = []
    for node_id in node_path:
        node = nodes[node_id]
        event_id = node.get("event_id")
        if event_id:
            setup_refs = [str(ref) for ref in node.get("setup_refs", [])]
            matched = [ref for ref in setup_refs if ref in surfaced_refs]
            if matched:
                prepared_event_visits_any += 1
            if setup_refs and len(matched) == len(setup_refs):
                prepared_event_visits_full += 1
            event_prep_details.append(
                {
                    "node_id": node_id,
                    "event_id": str(event_id),
                    "setup_refs": setup_refs,
                    "matched_setup_refs": matched,
                }
            )
        surfaced_refs.update(surfaced_refs_from_node(node))

    event_counts = Counter(event_ids)
    release_moves = sum(
        1 for index in range(1, len(tension_values))
        if (tension_values[index - 1] - tension_values[index]) >= 0.1
    )
    situation_switches = sum(
        1 for index in range(1, len(situations))
        if situations[index - 1] != situations[index]
    )

    return {
        "path": node_path,
        "unique_nodes": len(set(node_path)),
        "situations_visited": len(set(situations)),
        "situation_counts": dict(Counter(situations)),
        "situation_switches": situation_switches,
        "threshold_present": "s5_the_threshold" in situations,
        "event_counts": dict(event_counts),
        "prepared_event_visits_any": prepared_event_visits_any,
        "prepared_event_visits_full": prepared_event_visits_full,
        "event_prep_details": event_prep_details,
        "max_event_reuse": max(event_counts.values(), default=0),
        "reused_events": sorted([event_id for event_id, count in event_counts.items() if count > 1]),
        "peak_tension": max(tension_values),
        "end_tension": tension_values[-1],
        "release_moves": release_moves,
    }


def run_suite(
    graph: Graph,
    start_node: str,
    cycles: int,
    seeds: list[int],
    presets: list[ConductorPreset],
) -> dict:
    arms = [("pilot", "scheduler"), ("feature", "feature")]
    results: list[dict] = []

    for preset in presets:
        for seed in seeds:
            for mode, arm_label in arms:
                config = build_config(mode, start_node, cycles, seed, preset)
                trace_payload, _debug_rows = run_pilot(graph, config)
                summary = summarize_trace(graph, trace_payload)
                results.append(
                    {
                        "arm": arm_label,
                        "mode": mode,
                        "seed": seed,
                        "preset": {
                            "name": preset.name,
                            "tension_bias": preset.tension_bias,
                            "energy_bias": preset.energy_bias,
                        },
                        "summary": summary,
                    }
                )

    aggregate: dict[str, object] = {
        "scheduler": defaultdict(int),
        "feature": defaultdict(int),
        "comparisons": {
            "feature_threshold_advantage_runs": 0,
            "feature_prepared_event_advantage_runs": 0,
            "feature_more_event_diversity_runs": 0,
            "feature_same_path_runs": 0,
            "feature_more_release_moves_runs": 0,
        },
    }

    grouped_by_case: dict[tuple[str, int], dict[str, dict]] = defaultdict(dict)
    for result in results:
        arm = result["arm"]
        summary = result["summary"]
        grouped_by_case[(result["preset"]["name"], result["seed"])][arm] = result
        aggregate[arm]["runs"] += 1
        if summary["threshold_present"]:
            aggregate[arm]["threshold_runs"] += 1
        if summary["prepared_event_visits_any"] > 0:
            aggregate[arm]["prepared_any_runs"] += 1
        if summary["prepared_event_visits_full"] > 0:
            aggregate[arm]["prepared_full_runs"] += 1
        if summary["max_event_reuse"] > 1:
            aggregate[arm]["event_reuse_runs"] += 1
        if "e4_bridge_lockdown" in summary["reused_events"]:
            aggregate[arm]["e4_reuse_runs"] += 1
        if "e2_blackout_siren" in summary["event_counts"]:
            aggregate[arm]["e2_runs"] += 1
        if "e4_bridge_lockdown" in summary["event_counts"]:
            aggregate[arm]["e4_runs"] += 1

    for case in grouped_by_case.values():
        scheduler = case["scheduler"]["summary"]
        feature = case["feature"]["summary"]
        if feature["threshold_present"] and not scheduler["threshold_present"]:
            aggregate["comparisons"]["feature_threshold_advantage_runs"] += 1
        if feature["prepared_event_visits_any"] > scheduler["prepared_event_visits_any"]:
            aggregate["comparisons"]["feature_prepared_event_advantage_runs"] += 1
        if len(feature["event_counts"]) > len(scheduler["event_counts"]):
            aggregate["comparisons"]["feature_more_event_diversity_runs"] += 1
        if feature["path"] == scheduler["path"]:
            aggregate["comparisons"]["feature_same_path_runs"] += 1
        if feature["release_moves"] > scheduler["release_moves"]:
            aggregate["comparisons"]["feature_more_release_moves_runs"] += 1

    aggregate["scheduler"]["avg_situations"] = round(
        mean(result["summary"]["situations_visited"] for result in results if result["arm"] == "scheduler"),
        2,
    )
    aggregate["feature"]["avg_situations"] = round(
        mean(result["summary"]["situations_visited"] for result in results if result["arm"] == "feature"),
        2,
    )
    aggregate["scheduler"]["avg_release_moves"] = round(
        mean(result["summary"]["release_moves"] for result in results if result["arm"] == "scheduler"),
        2,
    )
    aggregate["feature"]["avg_release_moves"] = round(
        mean(result["summary"]["release_moves"] for result in results if result["arm"] == "feature"),
        2,
    )

    return {
        "graph_id": graph.graph_id,
        "cycles": cycles,
        "seeds": seeds,
        "presets": [
            {
                "name": preset.name,
                "tension_bias": preset.tension_bias,
                "energy_bias": preset.energy_bias,
            }
            for preset in presets
        ],
        "results": results,
        "aggregate": aggregate,
    }


def write_markdown_report(payload: dict, output_path: Path) -> None:
    aggregate = payload["aggregate"]
    lines = [
        "# City Routes Robustness Sweep",
        "",
        f"Date: 2026-03-14",
        "",
        "## Purpose",
        "",
        "Check whether arm C's threshold/event gains hold across a small",
        "seed and conductor sweep, rather than only on the seed-7 neutral",
        "path that first exposed the feature registry's value.",
        "",
        "## Sweep",
        "",
        f"- seeds: `{', '.join(str(seed) for seed in payload['seeds'])}`",
        f"- presets: `{', '.join(preset['name'] for preset in payload['presets'])}`",
        f"- cycles per run: `{payload['cycles']}`",
        "- arms compared: `scheduler` vs `feature`",
        "",
        "## Aggregate Comparison",
        "",
        f"- scheduler threshold runs: `{aggregate['scheduler']['threshold_runs']}/{aggregate['scheduler']['runs']}`",
        f"- feature threshold runs: `{aggregate['feature']['threshold_runs']}/{aggregate['feature']['runs']}`",
        f"- scheduler prepared-event runs: `{aggregate['scheduler']['prepared_any_runs']}/{aggregate['scheduler']['runs']}`",
        f"- feature prepared-event runs: `{aggregate['feature']['prepared_any_runs']}/{aggregate['feature']['runs']}`",
        f"- scheduler `e4` reuse runs: `{aggregate['scheduler']['e4_reuse_runs']}/{aggregate['scheduler']['runs']}`",
        f"- feature `e4` reuse runs: `{aggregate['feature']['e4_reuse_runs']}/{aggregate['feature']['runs']}`",
        f"- scheduler avg situations visited: `{aggregate['scheduler']['avg_situations']}`",
        f"- feature avg situations visited: `{aggregate['feature']['avg_situations']}`",
        f"- scheduler avg release moves: `{aggregate['scheduler']['avg_release_moves']}`",
        f"- feature avg release moves: `{aggregate['feature']['avg_release_moves']}`",
        "",
        "## Case-level Wins For Arm C",
        "",
        f"- threshold advantage over arm B: `{aggregate['comparisons']['feature_threshold_advantage_runs']}` cases",
        f"- more prepared-event visits than arm B: `{aggregate['comparisons']['feature_prepared_event_advantage_runs']}` cases",
        f"- more distinct events than arm B: `{aggregate['comparisons']['feature_more_event_diversity_runs']}` cases",
        f"- more release moves than arm B: `{aggregate['comparisons']['feature_more_release_moves_runs']}` cases",
        f"- identical path to arm B: `{aggregate['comparisons']['feature_same_path_runs']}` cases",
        "",
        "## Per-run Summary",
        "",
        "| Preset | Seed | Arm | Situations | s5 | Events | Prepared | e4 reuse | Release moves | End tension |",
        "|---|---:|---|---:|---|---|---:|---|---:|---:|",
    ]

    for result in payload["results"]:
        summary = result["summary"]
        events = ",".join(sorted(summary["event_counts"].keys())) or "-"
        e4_reuse = "yes" if "e4_bridge_lockdown" in summary["reused_events"] else "no"
        lines.append(
            "| "
            + f"{result['preset']['name']} | "
            + f"{result['seed']} | "
            + f"{result['arm']} | "
            + f"{summary['situations_visited']} | "
            + f"{'yes' if summary['threshold_present'] else 'no'} | "
            + f"{events} | "
            + f"{summary['prepared_event_visits_any']} | "
            + f"{e4_reuse} | "
            + f"{summary['release_moves']} | "
            + f"{summary['end_tension']:.2f} |"
        )

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Run a small seed/conductor sweep over the City Routes scheduler and feature arms."
    )
    parser.add_argument("--graph", type=Path, default=DEFAULT_GRAPH)
    parser.add_argument("--start-node", default=DEFAULT_START)
    parser.add_argument("--cycles", type=int, default=18)
    parser.add_argument("--seeds", default="3,7,11,19,23")
    parser.add_argument("--output-json", type=Path, default=DEFAULT_OUTPUT_JSON)
    parser.add_argument("--note-output", type=Path, default=DEFAULT_NOTE)
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    seeds = [int(seed.strip()) for seed in args.seeds.split(",") if seed.strip()]
    graph = Graph.load(args.graph)
    payload = run_suite(
        graph=graph,
        start_node=args.start_node,
        cycles=args.cycles,
        seeds=seeds,
        presets=default_presets(),
    )
    args.output_json.parent.mkdir(parents=True, exist_ok=True)
    args.output_json.write_text(json.dumps(payload, indent=2), encoding="utf-8")
    write_markdown_report(payload, args.note_output)
    print(f"wrote json: {args.output_json}")
    print(f"wrote note: {args.note_output}")


if __name__ == "__main__":
    main()
