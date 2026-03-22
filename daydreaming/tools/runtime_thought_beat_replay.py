#!/usr/bin/env python3
"""Offline replay harness for RuntimeThoughtBeatV1.

Builds a compact runtime packet from saved kernel cycle traces and optionally
realizes each cycle into a short inner-life beat with an LLM.

Default mode is packet-only so the harness can be inspected without making API
calls. Anthropic generation is supported as the first runtime comparison path.
"""

from __future__ import annotations

import argparse
import json
import time
from collections import Counter
from copy import deepcopy
from datetime import datetime
from pathlib import Path
from typing import Any, Dict, Iterable, List, Optional

try:  # pragma: no cover
    from anthropic import Anthropic
except Exception:  # pragma: no cover
    Anthropic = None


RUNTIME_THOUGHT_BEAT_OUTPUT_EXAMPLE = {
    "thought_beat_text": (
        "He tells himself the delay is caution, not fear, but the explanation "
        "keeps catching on the memory it is trying to pass around."
    ),
    "mood_tags": ["guarded", "self_dividing", "pressurized"],
    "residue_summary": "Delay recast as prudence, but the old accusation stays active.",
    "image_hint": "hand stalled over an object that would force contact",
    "audio_hint": "contained tension with a held metallic hum underneath",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Replay saved kernel cycles into RuntimeThoughtBeatV1 packets.")
    parser.add_argument(
        "--trace-path",
        type=Path,
        default=Path("out/puppet_knows_autonomous.json"),
        help="Path to the kernel JSON trace.",
    )
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=None,
        help="Where to write packets/prompts/beats. Defaults to a timestamped replay dir.",
    )
    parser.add_argument(
        "--start-cycle",
        type=int,
        default=1,
        help="1-based cycle index to start from.",
    )
    parser.add_argument(
        "--max-cycles",
        type=int,
        default=12,
        help="Maximum number of cycles to replay.",
    )
    parser.add_argument(
        "--provider",
        choices=["prompt-only", "anthropic"],
        default="prompt-only",
        help="Whether to only build packets/prompts or also call a model.",
    )
    parser.add_argument(
        "--model",
        default="",
        help="Model name to use when provider is anthropic.",
    )
    parser.add_argument(
        "--temperature",
        type=float,
        default=0.5,
        help="Sampling temperature for beat realization.",
    )
    parser.add_argument(
        "--routing-policy",
        choices=["fixed", "haiku_default"],
        default="fixed",
        help="Model routing policy for anthropic generation.",
    )
    parser.add_argument(
        "--escalation-model",
        default="claude-sonnet-4-6",
        help="Escalation model used when routing-policy is haiku_default.",
    )
    parser.add_argument(
        "--escalation-goals",
        default="reversal",
        help="Comma-separated goal families that should escalate under haiku_default.",
    )
    return parser.parse_args()


def resolve_output_dir(path_arg: Optional[Path]) -> Path:
    if path_arg is not None:
        return path_arg
    stamp = datetime.now().strftime("%Y%m%d-%H%M%S-%f")
    return Path("daydreaming/out/runtime_thought_replay") / stamp


def load_trace(path: Path) -> Dict[str, Any]:
    with path.open("r", encoding="utf-8") as handle:
        return json.load(handle)


def normalize_goal(goal: Dict[str, Any]) -> Dict[str, Any]:
    return {
        "id": goal.get("id"),
        "goal_type": goal.get("goal_type"),
        "strength": goal.get("strength"),
        "planning_type": goal.get("planning_type"),
        "situation_id": goal.get("situation_id"),
        "reasons": list(goal.get("reasons", [])),
    }


def top_emotions(cycle: Dict[str, Any], limit: int = 3) -> List[Dict[str, Any]]:
    ranked = sorted(
        cycle.get("emotional_state", []),
        key=lambda item: (-float(item.get("strength", 0.0)), str(item.get("emotion_id", ""))),
    )
    return [
        {
            "emotion_id": item.get("emotion_id"),
            "affect": item.get("affect"),
            "valence": item.get("valence"),
            "strength": item.get("strength"),
            "role": item.get("role"),
            "situation_id": item.get("situation_id"),
        }
        for item in ranked[:limit]
    ]


def top_retrievals(cycle: Dict[str, Any], limit: int = 2) -> List[Dict[str, Any]]:
    retrievals = sorted(
        cycle.get("retrieved", []),
        key=lambda item: (-float(item.get("retrieval_score", 0.0)), str(item.get("node_id") or item.get("episode_id") or "")),
    )
    return [
        {
            "node_id": item.get("node_id"),
            "episode_id": item.get("episode_id"),
            "retrieval_score": item.get("retrieval_score"),
            "overlap": list(item.get("overlap", [])),
            "threshold": item.get("threshold"),
        }
        for item in retrievals[:limit]
    ]


def top_candidates(cycle: Dict[str, Any], limit: int = 3) -> List[Dict[str, Any]]:
    return [normalize_goal(item) for item in cycle.get("top_candidates", [])[:limit]]


def situation_payload(cycle: Dict[str, Any], situation_id: Optional[str]) -> Dict[str, Any]:
    situations = cycle.get("situations", {})
    if not situation_id or situation_id not in situations:
        return {}
    current = deepcopy(situations[situation_id])
    return {
        "id": current.get("id"),
        "description": current.get("description"),
        "place": current.get("place"),
        "activation": current.get("activation"),
        "threat": current.get("threat"),
        "hope": current.get("hope"),
        "waiting": current.get("waiting"),
        "ripeness": current.get("ripeness"),
        "grief": current.get("grief"),
        "anger": current.get("anger"),
        "indices": list(current.get("indices", [])),
        "external": current.get("external"),
        "directed_target": current.get("directed-target"),
    }


def branch_payload(cycle: Dict[str, Any], limit: int = 2) -> List[Dict[str, Any]]:
    return [
        {
            "family": item.get("family"),
            "source_context": item.get("source_context"),
            "target_context": item.get("target_context"),
            "fact_ids": list(item.get("fact_ids", [])),
            "fact_types": list(item.get("fact_types", [])),
            "episode_ids": list(item.get("episode_ids", [])),
        }
        for item in cycle.get("branch_events", [])[:limit]
    ]


def selection_payload(cycle: Dict[str, Any]) -> Dict[str, Any]:
    selection = cycle.get("selection") or {}
    return {
        "goal_family": selection.get("goal_family"),
        "policy": selection.get("policy"),
        "reasons": list(selection.get("reasons", [])),
        "active_indices": list(selection.get("adapter_active_indices", [])),
        "branch_context": selection.get("adapter_branch_context"),
        "visible_fact_ids": list(selection.get("adapter_visible_fact_ids", [])),
    }


def build_runtime_thought_packet(
    cycle: Dict[str, Any],
    *,
    previous_residue_summary: Optional[str] = None,
) -> Dict[str, Any]:
    selected_goal = normalize_goal(cycle.get("selected_goal", {}))
    situation_id = selected_goal.get("situation_id")
    return {
        "packet_type": "RuntimeThoughtBeatV1",
        "cycle": cycle.get("cycle"),
        "selected_goal": selected_goal,
        "chosen_node_id": cycle.get("chosen_node_id"),
        "active_indices": list(cycle.get("active_indices", [])),
        "situation": situation_payload(cycle, situation_id),
        "retrieved_fragments": top_retrievals(cycle),
        "top_competing_goals": top_candidates(cycle),
        "branch_events": branch_payload(cycle),
        "selection": selection_payload(cycle),
        "emotional_state": top_emotions(cycle),
        "feedback_applied": cycle.get("feedback_applied"),
        "serendipity_bias": cycle.get("serendipity_bias"),
        "previous_residue_summary": previous_residue_summary,
    }


def build_runtime_thought_system_prompt() -> str:
    return (
        "You realize one beat of runtime inner life from a cognitive cycle packet.\n\n"
        "Write from inside the character's thought process, not as an external scene summary. "
        "Stay tightly grounded in the selected operator, current situation, retrieved fragments, "
        "and emotional state. Use 2-3 sentences only. Keep it specific, readable, and psychologically legible.\n\n"
        "Return JSON only with exactly these fields:\n"
        "- thought_beat_text: string\n"
        "- mood_tags: array of 2-4 short strings\n"
        "- residue_summary: one sentence carrying forward what should linger into the next cycle\n"
        "- image_hint: short phrase for visual direction\n"
        "- audio_hint: short phrase for audio/musical direction\n\n"
        "Do not mention packets, operators, schemas, or system internals in the output."
    )


def build_runtime_thought_prompt(packet: Dict[str, Any]) -> str:
    return (
        "RuntimeThoughtBeatV1 packet:\n\n"
        + json.dumps(packet, indent=2)
        + "\n\nReturn JSON matching this example shape exactly:\n\n"
        + json.dumps(RUNTIME_THOUGHT_BEAT_OUTPUT_EXAMPLE, indent=2)
    )


def parse_json_text(text: str) -> Dict[str, Any]:
    cleaned = text.strip()
    if cleaned.startswith("```"):
        cleaned = cleaned.removeprefix("```json").removeprefix("```").strip()
        if cleaned.endswith("```"):
            cleaned = cleaned[:-3].strip()
    start = cleaned.find("{")
    end = cleaned.rfind("}")
    if start >= 0 and end >= start:
        cleaned = cleaned[start : end + 1]
    return json.loads(cleaned)


def anthropic_json_response(prompt: str, model: str, *, temperature: float) -> Dict[str, Any]:
    if Anthropic is None:  # pragma: no cover
        raise RuntimeError("anthropic package is not installed.")
    client = Anthropic()
    response = client.messages.create(
        model=model,
        max_tokens=1024,
        temperature=temperature,
        system=build_runtime_thought_system_prompt(),
        messages=[{"role": "user", "content": prompt}],
    )
    text_parts: List[str] = []
    for block in getattr(response, "content", []) or []:
        if getattr(block, "type", None) == "text" and getattr(block, "text", None):
            text_parts.append(block.text)
    if not text_parts:
        raise RuntimeError("Anthropic returned no text content.")
    return parse_json_text("\n".join(text_parts))


def parse_csv_set(raw: str) -> set[str]:
    return {item.strip() for item in raw.split(",") if item.strip()}


def route_model_for_cycle(
    cycle: Dict[str, Any],
    previous_cycle: Optional[Dict[str, Any]],
    *,
    routing_policy: str,
    base_model: str,
    escalation_model: str,
    escalation_goals: set[str],
) -> tuple[str, List[str]]:
    if routing_policy == "fixed":
        return base_model, []

    selected_goal = cycle.get("selected_goal") or {}
    goal_type = selected_goal.get("goal_type")
    previous_goal_type = None
    if previous_cycle is not None:
        previous_goal = previous_cycle.get("selected_goal") or {}
        previous_goal_type = previous_goal.get("goal_type")

    reasons: List[str] = []
    if goal_type and goal_type in escalation_goals:
        reasons.append(f"goal_family:{goal_type}")
    if previous_goal_type and goal_type and goal_type != previous_goal_type:
        reasons.append("goal_family_change")
    if (cycle.get("branch_events") or []) and goal_type and goal_type in escalation_goals:
        reasons.append("branch_event")

    if reasons:
        return escalation_model, reasons
    return base_model, []


def selected_cycles(trace_data: Dict[str, Any], start_cycle: int, max_cycles: int) -> Iterable[Dict[str, Any]]:
    cycles = trace_data.get("cycles", [])
    for cycle in cycles:
        cycle_num = int(cycle.get("cycle", 0))
        if cycle_num < start_cycle:
            continue
        if cycle_num >= start_cycle + max_cycles:
            break
        yield cycle


def dump_json(path: Path, payload: Any) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8") as handle:
        json.dump(payload, handle, indent=2)


def dump_text(path: Path, text: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8")


def append_jsonl(path: Path, row: Dict[str, Any]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("a", encoding="utf-8") as handle:
        handle.write(json.dumps(row, ensure_ascii=True) + "\n")


def main() -> int:
    args = parse_args()
    if args.provider == "anthropic" and not args.model:
        raise ValueError("--model is required when --provider anthropic.")

    trace_data = load_trace(args.trace_path)
    output_dir = resolve_output_dir(args.output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    packets_jsonl = output_dir / "runtime_thought_packets.jsonl"
    beats_jsonl = output_dir / "runtime_thought_beats.jsonl"
    previous_residue_summary: Optional[str] = None
    processed_cycles: List[int] = []
    previous_cycle: Optional[Dict[str, Any]] = None
    escalation_goals = parse_csv_set(args.escalation_goals)
    model_usage_counts: Counter[str] = Counter()
    route_reason_counts: Counter[str] = Counter()
    elapsed_seconds_total = 0.0

    for cycle in selected_cycles(trace_data, args.start_cycle, args.max_cycles):
        cycle_num = int(cycle["cycle"])
        processed_cycles.append(cycle_num)
        packet = build_runtime_thought_packet(cycle, previous_residue_summary=previous_residue_summary)
        prompt = build_runtime_thought_prompt(packet)

        dump_json(output_dir / f"cycle-{cycle_num:02d}.packet.json", packet)
        dump_text(output_dir / f"cycle-{cycle_num:02d}.prompt.txt", prompt)
        append_jsonl(
            packets_jsonl,
            {
                "cycle": cycle_num,
                "packet": packet,
            },
        )

        if args.provider == "anthropic":
            selected_model, route_reasons = route_model_for_cycle(
                cycle,
                previous_cycle,
                routing_policy=args.routing_policy,
                base_model=args.model,
                escalation_model=args.escalation_model,
                escalation_goals=escalation_goals,
            )
            started = time.perf_counter()
            beat = anthropic_json_response(prompt, selected_model, temperature=args.temperature)
            elapsed_seconds = time.perf_counter() - started
            dump_json(output_dir / f"cycle-{cycle_num:02d}.beat.json", beat)
            append_jsonl(
                beats_jsonl,
                {
                    "cycle": cycle_num,
                    "model": selected_model,
                    "temperature": args.temperature,
                    "elapsed_seconds": round(elapsed_seconds, 3),
                    "routing_policy": args.routing_policy,
                    "route_reasons": route_reasons,
                    "beat": beat,
                },
            )
            elapsed_seconds_total += elapsed_seconds
            model_usage_counts[selected_model] += 1
            for reason in route_reasons:
                route_reason_counts[reason] += 1
            previous_residue_summary = beat.get("residue_summary") or previous_residue_summary
        previous_cycle = cycle

    summary = {
        "trace_path": str(args.trace_path),
        "provider": args.provider,
        "model": args.model or None,
        "routing_policy": args.routing_policy,
        "escalation_model": args.escalation_model if args.provider == "anthropic" else None,
        "escalation_goals": sorted(escalation_goals) if args.provider == "anthropic" else [],
        "temperature": args.temperature,
        "start_cycle": args.start_cycle,
        "max_cycles": args.max_cycles,
        "processed_cycles": processed_cycles,
        "model_usage_counts": dict(model_usage_counts),
        "route_reason_counts": dict(route_reason_counts),
        "elapsed_seconds_total": round(elapsed_seconds_total, 3) if args.provider == "anthropic" else None,
        "elapsed_seconds_avg": round(elapsed_seconds_total / len(processed_cycles), 3)
        if args.provider == "anthropic" and processed_cycles
        else None,
        "output_dir": str(output_dir),
    }
    dump_json(output_dir / "summary.json", summary)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
