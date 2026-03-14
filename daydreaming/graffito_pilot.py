#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import math
import random
from collections import defaultdict
from dataclasses import dataclass
from pathlib import Path
from typing import Any

import yaml


MAX_RECENT_PATH = 6


def clamp(value: float, low: float = 0.0, high: float = 1.0) -> float:
    return max(low, min(high, value))


def round_metric(value: float) -> float:
    return round(value, 3)


@dataclass(slots=True)
class Graph:
    graph_id: str
    nodes_by_id: dict[str, dict[str, Any]]
    edges_from: dict[str, list[str]]

    @classmethod
    def load(cls, path: Path) -> "Graph":
        raw = yaml.safe_load(path.read_text(encoding="utf-8"))
        nodes_by_id = {node["id"]: node for node in raw["nodes"]}
        edges_from: dict[str, list[str]] = defaultdict(list)
        for edge in raw.get("edges", []):
            edges_from[edge["from"]].append(edge["to"])
        return cls(
            graph_id=raw["graph_id"],
            nodes_by_id=nodes_by_id,
            edges_from=dict(edges_from),
        )

    def outgoing(self, node_id: str) -> list[str]:
        return list(self.edges_from.get(node_id, []))


@dataclass(slots=True)
class PilotConfig:
    mode: str
    cycles: int
    start_node: str
    seed: int
    activation_decay: float
    activation_boost: float
    revisit_penalty: float
    continuity_bias: float
    temperature: float
    conductor_tension_bias: float
    conductor_energy_bias: float
    initial_tension: float
    initial_energy: float
    release_start_progress: float = 0.70
    target_tension_peak: float = 0.90
    target_tension_end: float = 0.15
    target_energy_peak: float = 0.75
    target_energy_end: float = 0.45


class TraversalState:
    def __init__(self, graph: Graph, config: PilotConfig):
        self.graph = graph
        self.config = config
        self.current_node_id = config.start_node
        self.recent_path: list[str] = []
        self.visit_count: dict[str, int] = defaultdict(int)
        self.last_seen_cycle: dict[str, int] = {}
        self.situation_activation: dict[str, float] = {
            situation_id: 0.0
            for situation_id in self._all_situations()
        }
        self.tension = config.initial_tension
        self.energy = config.initial_energy
        self.recent_emotional_trajectory: list[dict[str, float]] = []
        self.event_approach_count: dict[str, int] = defaultdict(int)

    def _all_situations(self) -> list[str]:
        seen: set[str] = set()
        for node in self.graph.nodes_by_id.values():
            for situation_id in node.get("situation_ids", []):
                seen.add(situation_id)
        return sorted(seen)

    def decay(self) -> None:
        for situation_id, value in list(self.situation_activation.items()):
            self.situation_activation[situation_id] = round_metric(
                value * self.config.activation_decay
            )

    def apply_visit(self, cycle: int, node_id: str) -> None:
        node = self.graph.nodes_by_id[node_id]
        self.current_node_id = node_id
        self.visit_count[node_id] += 1
        self.last_seen_cycle[node_id] = cycle
        self.recent_path.append(node_id)
        if len(self.recent_path) > MAX_RECENT_PATH:
            self.recent_path = self.recent_path[-MAX_RECENT_PATH:]

        self.tension = round_metric(
            clamp(self.tension + float(node.get("delta_tension", 0.0)))
        )
        self.energy = round_metric(
            clamp(self.energy + float(node.get("delta_energy", 0.0)))
        )
        self.recent_emotional_trajectory.append(
            {"tension": self.tension, "energy": self.energy}
        )
        if len(self.recent_emotional_trajectory) > MAX_RECENT_PATH:
            self.recent_emotional_trajectory = self.recent_emotional_trajectory[-MAX_RECENT_PATH:]

        event_id = node.get("event_id")
        if event_id:
            self.event_approach_count[event_id] += 1

        for situation_id in node.get("situation_ids", []):
            previous = self.situation_activation.get(situation_id, 0.0)
            self.situation_activation[situation_id] = round_metric(
                clamp(previous + self.config.activation_boost)
            )

    def primary_situation(self, node_id: str) -> str | None:
        node = self.graph.nodes_by_id[node_id]
        situation_ids = node.get("situation_ids", [])
        return situation_ids[0] if situation_ids else None


def _segment_fraction(progress: float, start: float, end: float) -> float:
    span = max(end - start, 0.01)
    return clamp((progress - start) / span)


def target_tension(progress: float, config: PilotConfig) -> float:
    if progress <= 0.35:
        return 0.25 + (0.50 * (progress / 0.35))
    if progress <= config.release_start_progress:
        return 0.75 + (
            (config.target_tension_peak - 0.75)
            * _segment_fraction(progress, 0.35, config.release_start_progress)
        )
    return config.target_tension_peak - (
        (config.target_tension_peak - config.target_tension_end)
        * _segment_fraction(progress, config.release_start_progress, 1.0)
    )


def target_energy(progress: float, config: PilotConfig) -> float:
    if progress <= 0.40:
        return 0.50 + (
            (config.target_energy_peak - 0.50)
            * (progress / 0.40)
        )
    if progress <= config.release_start_progress:
        return config.target_energy_peak - (
            (config.target_energy_peak - 0.65)
            * _segment_fraction(progress, 0.40, config.release_start_progress)
        )
    return 0.65 - (
        (0.65 - config.target_energy_end)
        * _segment_fraction(progress, config.release_start_progress, 1.0)
    )


def infer_family(previous_node: dict[str, Any], node: dict[str, Any]) -> str | None:
    if node.get("variant_family"):
        return node["variant_family"]
    previous_situation = previous_node.get("situation_ids", [None])[0]
    current_situation = node.get("situation_ids", [None])[0]
    if previous_situation != current_situation and float(node.get("delta_tension", 0.0)) < -0.10:
        return "ROVING"
    return None


def recency_weight(state: TraversalState, candidate_id: str, cycle: int) -> float:
    last_seen = state.last_seen_cycle.get(candidate_id)
    visits = state.visit_count.get(candidate_id, 0)
    if last_seen is None:
        return 1.15
    gap = cycle - last_seen
    if gap <= 1:
        return state.config.revisit_penalty * 0.45
    if gap == 2:
        return state.config.revisit_penalty * 0.75
    return max(0.35, 1.0 - (visits * 0.08))


def continuity_weight(state: TraversalState, current_id: str, candidate_id: str) -> float:
    current_situation = state.primary_situation(current_id)
    candidate_situation = state.primary_situation(candidate_id)
    if current_situation and current_situation == candidate_situation:
        return state.config.continuity_bias
    return 0.95


def activation_weight(state: TraversalState, candidate_id: str) -> float:
    situation_id = state.primary_situation(candidate_id)
    if not situation_id:
        return 1.0
    return 0.85 + state.situation_activation.get(situation_id, 0.0)


def weighted_choice(rng: random.Random, weighted_candidates: list[tuple[str, float]]) -> str:
    total = sum(weight for _candidate_id, weight in weighted_candidates)
    if total <= 0:
        return weighted_candidates[0][0]
    roll = rng.random() * total
    upto = 0.0
    for candidate_id, weight in weighted_candidates:
        upto += weight
        if upto >= roll:
            return candidate_id
    return weighted_candidates[-1][0]


def available_candidates(graph: Graph, state: TraversalState, current_id: str) -> list[str]:
    candidate_ids = graph.outgoing(current_id)
    if not candidate_ids:
        candidate_ids = [
            node_id
            for node_id in graph.nodes_by_id
            if node_id != current_id
        ]

    if len(candidate_ids) <= 1:
        return candidate_ids

    previous_id = state.recent_path[-2] if len(state.recent_path) >= 2 else None
    if previous_id in candidate_ids:
        filtered = [candidate_id for candidate_id in candidate_ids if candidate_id != previous_id]
        if filtered:
            candidate_ids = filtered

    recent_counts = {
        candidate_id: state.recent_path.count(candidate_id)
        for candidate_id in candidate_ids
    }
    filtered = [
        candidate_id
        for candidate_id in candidate_ids
        if recent_counts[candidate_id] < 2
    ]
    if filtered:
        candidate_ids = filtered

    return candidate_ids


def choose_baseline(
    graph: Graph,
    state: TraversalState,
    cycle: int,
    rng: random.Random,
) -> tuple[str, dict[str, Any]]:
    current_id = state.current_node_id
    candidate_ids = available_candidates(graph, state, current_id)

    evaluations: list[dict[str, Any]] = []
    weighted_candidates: list[tuple[str, float]] = []
    for candidate_id in candidate_ids:
        weight = (
            recency_weight(state, candidate_id, cycle)
            * continuity_weight(state, current_id, candidate_id)
            * activation_weight(state, candidate_id)
        )
        weighted_candidates.append((candidate_id, max(weight, 0.001)))
        evaluations.append(
            {
                "candidate_id": candidate_id,
                "weight": round_metric(weight),
                "recency_weight": round_metric(recency_weight(state, candidate_id, cycle)),
                "continuity_weight": round_metric(continuity_weight(state, current_id, candidate_id)),
                "activation_weight": round_metric(activation_weight(state, candidate_id)),
            }
        )

    selected = weighted_choice(rng, weighted_candidates)
    return selected, {"arm": "baseline", "evaluations": evaluations}


def _trajectory_score(target_slope: float, node_delta: float) -> float:
    return math.exp(-abs(target_slope - node_delta) * 4.0)


def _priority_for_candidate(
    state: TraversalState,
    current_id: str,
    candidate_id: str,
    target_tension_slope: float,
) -> tuple[int, list[str]]:
    node = state.graph.nodes_by_id[candidate_id]
    reasons: list[str] = []
    priority = int(node.get("priority_tier", 0))

    if node.get("variant_family") and target_tension_slope > 0.12:
        priority += 1
        reasons.append("variant_matches_rising_arc")

    if float(node.get("delta_tension", 0.0)) < -0.12 and state.tension > 0.65:
        priority += 1
        reasons.append("release_from_high_tension")

    current_situation = state.primary_situation(current_id)
    candidate_situation = state.primary_situation(candidate_id)
    if (
        current_situation
        and candidate_situation
        and current_situation != candidate_situation
        and state.situation_activation.get(current_situation, 0.0) > 0.70
    ):
        priority += 1
        reasons.append("shift_out_of_hot_cluster")

    return priority, reasons


def choose_pilot(
    graph: Graph,
    state: TraversalState,
    cycle: int,
    total_cycles: int,
    rng: random.Random,
) -> tuple[str, dict[str, Any]]:
    current_id = state.current_node_id
    candidate_ids = available_candidates(graph, state, current_id)

    progress = cycle / max(total_cycles, 1)
    desired_tension = clamp(target_tension(progress, state.config) + state.config.conductor_tension_bias)
    desired_energy = clamp(target_energy(progress, state.config) + state.config.conductor_energy_bias)
    target_tension_slope = desired_tension - state.tension
    target_energy_slope = desired_energy - state.energy

    evaluations: list[dict[str, Any]] = []
    candidate_meta: list[tuple[str, int, float]] = []
    for candidate_id in candidate_ids:
        node = graph.nodes_by_id[candidate_id]
        priority, priority_reasons = _priority_for_candidate(
            state, current_id, candidate_id, target_tension_slope
        )
        tension_score = _trajectory_score(
            target_tension_slope, float(node.get("delta_tension", 0.0))
        )
        energy_score = _trajectory_score(
            target_energy_slope, float(node.get("delta_energy", 0.0))
        )
        trajectory_score = (0.65 * tension_score) + (0.35 * energy_score)
        soft_weight = (
            recency_weight(state, candidate_id, cycle)
            * continuity_weight(state, current_id, candidate_id)
            * activation_weight(state, candidate_id)
        )
        total_weight = max(trajectory_score * soft_weight, 0.001)
        candidate_meta.append((candidate_id, priority, total_weight))
        evaluations.append(
            {
                "candidate_id": candidate_id,
                "priority": priority,
                "priority_reasons": priority_reasons,
                "trajectory_score": round_metric(trajectory_score),
                "tension_score": round_metric(tension_score),
                "energy_score": round_metric(energy_score),
                "soft_weight": round_metric(soft_weight),
                "total_weight": round_metric(total_weight),
                "delta_tension": round_metric(float(node.get("delta_tension", 0.0))),
                "delta_energy": round_metric(float(node.get("delta_energy", 0.0))),
            }
        )

    highest_priority = max(priority for _candidate_id, priority, _weight in candidate_meta)
    shortlisted = [
        (candidate_id, weight / max(state.config.temperature, 0.05))
        for candidate_id, priority, weight in candidate_meta
        if priority == highest_priority
    ]
    selected = weighted_choice(rng, shortlisted)
    return selected, {
        "arm": "pilot",
        "desired_tension": round_metric(desired_tension),
        "desired_energy": round_metric(desired_energy),
        "target_tension_slope": round_metric(target_tension_slope),
        "target_energy_slope": round_metric(target_energy_slope),
        "highest_priority": highest_priority,
        "evaluations": evaluations,
    }


def run_pilot(graph: Graph, config: PilotConfig) -> tuple[dict[str, Any], list[dict[str, Any]]]:
    rng = random.Random(config.seed)
    state = TraversalState(graph, config)
    trace_rows: list[dict[str, Any]] = []
    debug_rows: list[dict[str, Any]] = []

    initial_node = graph.nodes_by_id[config.start_node]
    state.apply_visit(1, config.start_node)
    trace_rows.append(
        {
            "cycle": 1,
            "node": config.start_node,
            "family": infer_family(initial_node, initial_node),
            "tension": round(state.tension, 1),
            "energy": round(state.energy, 1),
        }
    )
    debug_rows.append(
        {
            "cycle": 1,
            "arm": config.mode,
            "selection_type": "initial",
            "selected_node": config.start_node,
            "tension": state.tension,
            "energy": state.energy,
            "situation_activation": dict(state.situation_activation),
            "event_approach_count": dict(state.event_approach_count),
        }
    )

    for cycle in range(2, config.cycles + 1):
        state.decay()
        previous_id = state.current_node_id
        previous_node = graph.nodes_by_id[previous_id]

        if config.mode == "baseline":
            selected_id, selection_meta = choose_baseline(graph, state, cycle, rng)
        else:
            selected_id, selection_meta = choose_pilot(graph, state, cycle, config.cycles, rng)

        selected_node = graph.nodes_by_id[selected_id]
        family = infer_family(previous_node, selected_node)
        state.apply_visit(cycle, selected_id)

        trace_rows.append(
            {
                "cycle": cycle,
                "node": selected_id,
                "family": family,
                "tension": round(state.tension, 1),
                "energy": round(state.energy, 1),
            }
        )
        debug_rows.append(
            {
                "cycle": cycle,
                "arm": config.mode,
                "previous_node": previous_id,
                "selected_node": selected_id,
                "selected_family": family,
                "tension": state.tension,
                "energy": state.energy,
                "visit_count": dict(state.visit_count),
                "last_seen_cycle": dict(state.last_seen_cycle),
                "recent_path": list(state.recent_path),
                "situation_activation": dict(state.situation_activation),
                "event_approach_count": dict(state.event_approach_count),
                "selection": selection_meta,
            }
        )

    return {"graph_id": graph.graph_id, "trace": trace_rows}, debug_rows


def write_outputs(trace_payload: dict[str, Any], debug_rows: list[dict[str, Any]], trace_path: Path, debug_path: Path) -> None:
    trace_path.parent.mkdir(parents=True, exist_ok=True)
    debug_path.parent.mkdir(parents=True, exist_ok=True)
    trace_path.write_text(yaml.safe_dump(trace_payload, sort_keys=False), encoding="utf-8")
    with debug_path.open("w", encoding="utf-8") as handle:
        for row in debug_rows:
            handle.write(json.dumps(row) + "\n")


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="Run the traversal pilot harness.")
    parser.add_argument("--graph", default="daydreaming/fixtures/graffito_v0_scenes_3_4.yaml")
    parser.add_argument("--mode", choices=["baseline", "pilot"], required=True)
    parser.add_argument("--cycles", type=int, default=18)
    parser.add_argument("--start-node", default="g_s1_n01_feet_pounding")
    parser.add_argument("--seed", type=int, default=7)
    parser.add_argument("--trace-output", required=True)
    parser.add_argument("--debug-output", required=True)
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

    graph = Graph.load(Path(args.graph))
    config = PilotConfig(
        mode=args.mode,
        cycles=args.cycles,
        start_node=args.start_node,
        seed=args.seed,
        activation_decay=args.activation_decay,
        activation_boost=args.activation_boost,
        revisit_penalty=args.revisit_penalty,
        continuity_bias=args.continuity_bias,
        temperature=args.temperature,
        conductor_tension_bias=args.conductor_tension_bias,
        conductor_energy_bias=args.conductor_energy_bias,
        initial_tension=args.initial_tension,
        initial_energy=args.initial_energy,
        release_start_progress=args.release_start_progress,
        target_tension_peak=args.target_tension_peak,
        target_tension_end=args.target_tension_end,
        target_energy_peak=args.target_energy_peak,
        target_energy_end=args.target_energy_end,
    )
    trace_payload, debug_rows = run_pilot(graph, config)
    write_outputs(
        trace_payload,
        debug_rows,
        Path(args.trace_output),
        Path(args.debug_output),
    )
    print(f"Wrote {len(trace_payload['trace'])} cycles to {args.trace_output}")
    print(f"Wrote debug JSONL to {args.debug_output}")


if __name__ == "__main__":
    main()
