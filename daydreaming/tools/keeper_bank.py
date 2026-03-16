from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any, Dict, Iterable, List, Optional, Tuple


DEFAULT_RESULTS = Path("daydreaming/out/authoring_time_generation/results.jsonl")
DEFAULT_BANK = Path("daydreaming/out/authoring_time_generation/keeper_bank_supply_v1.jsonl")


def load_json(path: Path) -> Any:
    return json.loads(path.read_text())


def load_jsonl(path: Path) -> List[Dict[str, Any]]:
    if not path.exists():
        return []
    rows: List[Dict[str, Any]] = []
    for line in path.read_text().splitlines():
        line = line.strip()
        if line:
            rows.append(json.loads(line))
    return rows


def write_jsonl(path: Path, rows: Iterable[Dict[str, Any]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w") as handle:
        for row in rows:
            handle.write(json.dumps(row, sort_keys=True))
            handle.write("\n")


def batch_function_signature(row: Dict[str, Any]) -> Tuple[Any, ...]:
    graph_projection = row.get("graph_projection", {})
    return (
        row.get("selected_operator_family"),
        tuple(sorted(row.get("practice_tags", []))),
        graph_projection.get("option_effect"),
        graph_projection.get("situation_id"),
        tuple(row.get("selected_affordance_tags", [])[:2]),
    )


def signature_key(signature: Tuple[Any, ...]) -> str:
    parts: List[str] = []
    for item in signature:
        if isinstance(item, tuple):
            parts.append("+".join(str(v) for v in item))
        else:
            parts.append(str(item))
    return " | ".join(parts)


def find_run_record(results_path: Path, bundle_path: Path) -> Dict[str, Any]:
    bundle_resolved = str(bundle_path.resolve())
    for row in reversed(load_jsonl(results_path)):
        if row.get("bundle_path") == bundle_resolved:
            return row
    raise SystemExit(f"bundle not found in results ledger: {bundle_resolved}")


def build_admission_index(batch_trace: Dict[str, Any]) -> Dict[Tuple[int, int, str], Dict[str, Any]]:
    index: Dict[Tuple[int, int, str], Dict[str, Any]] = {}
    for status in ("admitted", "rejected"):
        for row in batch_trace["admission"][status]:
            key = (row["sequence_index"], row["step_index"], row["node_id"])
            index[key] = {
                "status": status,
                "row": row,
            }
    return index


def build_bank_rows(bundle_path: Path, run_record: Dict[str, Any]) -> List[Dict[str, Any]]:
    batch_trace = load_json(bundle_path / "batch.trace.json")
    admission_index = build_admission_index(batch_trace)
    rows: List[Dict[str, Any]] = []
    for sequence_dir in sorted(p for p in bundle_path.iterdir() if p.is_dir() and p.name.startswith("sequence-")):
        sequence_trace = load_json(sequence_dir / "sequence.trace.json")
        sequence_index = sequence_trace["sequence_index"]
        for step in sequence_trace["steps"]:
            if not step.get("accepted"):
                continue
            step_index = step["step_index"]
            step_trace_path = sequence_dir / f"middle-step-{step_index:02d}.trace.json"
            step_sidecar_path = sequence_dir / f"middle-step-{step_index:02d}.sidecar.json"
            step_validation_path = sequence_dir / f"middle-step-{step_index:02d}.validation.json"
            step_response_path = sequence_dir / f"middle-step-{step_index:02d}.response.txt"
            step_trace = load_json(step_trace_path)
            step_sidecar = load_json(step_sidecar_path)
            step_validation = load_json(step_validation_path)
            graph_projection = step_trace["graph_projection"]
            node_id = graph_projection["node_id"]
            admission = admission_index.get((sequence_index, step_index, node_id), {"status": "unknown", "row": {}})
            practice_type = step_sidecar.get("practice_context", {}).get("practice_type")
            row = {
                "bank_id": f"{run_record.get('run_id')}:s{sequence_index:02d}:st{step_index:02d}:{node_id}",
                "fixture": run_record.get("fixture"),
                "round_id": run_record.get("round_id"),
                "bundle_path": str(bundle_path.resolve()),
                "run_id": run_record.get("run_id"),
                "timestamp": run_record.get("timestamp"),
                "provider": run_record.get("provider"),
                "model": run_record.get("model"),
                "intervention_family": run_record.get("intervention_family"),
                "intervention_note": run_record.get("intervention_note"),
                "run_mode": run_record.get("run_mode"),
                "prompt_style": step_trace.get("prompt_style"),
                "system_prompt_style": step_trace.get("system_prompt_style"),
                "generation_temperature": step_trace.get("generation_temperature"),
                "situation_framing_mode": sequence_trace.get("situation_framing_mode"),
                "situation_framing_variant": step_trace.get("situation_framing_variant"),
                "sequence_index": sequence_index,
                "step_index": step_index,
                "node_id": node_id,
                "source_path": str(step_response_path.resolve()),
                "trace_path": str(step_trace_path.resolve()),
                "sidecar_path": str(step_sidecar_path.resolve()),
                "validation_path": str(step_validation_path.resolve()),
                "candidate_text": step_trace.get("candidate_text"),
                "selected_operator_family": step_trace.get("selected_operator_family"),
                "practice_type": practice_type,
                "situation_id": graph_projection.get("situation_id"),
                "option_effect": graph_projection.get("option_effect"),
                "pressure_tags": graph_projection.get("pressure_tags", []),
                "practice_tags": graph_projection.get("practice_tags", []),
                "origin_pressure_refs": graph_projection.get("origin_pressure_refs", []),
                "selected_affordance_tags": step_trace.get("selected_affordance_tags", []),
                "selected_retrieved_episode_ids": step_trace.get("selected_retrieved_episode_ids", []),
                "function_signature": list(batch_function_signature(step_trace)),
                "function_signature_key": signature_key(batch_function_signature(step_trace)),
                "admission_status": admission["status"],
                "admission_reasons": admission["row"].get("admission_score", {}).get("hard_errors", []),
                "compiler_selected_score": step_trace.get("candidate_compilation", {}).get("selected_score"),
                "admission_score_total": admission["row"].get("admission_score", {}).get("total"),
                "structural_valid": step_validation.get("valid"),
                "verdict": "pending",
                "role": None,
                "patchable": None,
                "edit_notes": "",
                "edit_minutes": None,
            }
            rows.append(row)
    return rows


def merge_rows(existing: List[Dict[str, Any]], new_rows: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
    merged: Dict[str, Dict[str, Any]] = {row["bank_id"]: row for row in existing}
    for row in new_rows:
        preserved = merged.get(row["bank_id"], {})
        row["verdict"] = preserved.get("verdict", row["verdict"])
        row["role"] = preserved.get("role", row["role"])
        row["patchable"] = preserved.get("patchable", row["patchable"])
        row["edit_notes"] = preserved.get("edit_notes", row["edit_notes"])
        row["edit_minutes"] = preserved.get("edit_minutes", row["edit_minutes"])
        merged[row["bank_id"]] = row
    return [merged[key] for key in sorted(merged)]


def write_packet(packet_path: Path, run_record: Dict[str, Any], rows: List[Dict[str, Any]]) -> None:
    packet_path.parent.mkdir(parents=True, exist_ok=True)
    lines: List[str] = []
    lines.append(f"# Keeper packet: {run_record.get('run_id')}")
    lines.append("")
    lines.append(f"- fixture: `{run_record.get('fixture')}`")
    lines.append(f"- bundle: `{run_record.get('bundle_path')}`")
    lines.append(f"- provider/model: `{run_record.get('provider')}` / `{run_record.get('model')}`")
    lines.append(f"- prompt style: `{rows[0].get('prompt_style') if rows else None}`")
    lines.append(f"- system prompt style: `{rows[0].get('system_prompt_style') if rows else None}`")
    lines.append(f"- temperature: `{rows[0].get('generation_temperature') if rows else None}`")
    lines.append("")
    admitted = [row for row in rows if row["admission_status"] == "admitted"]
    rejected = [row for row in rows if row["admission_status"] != "admitted"]
    lines.append("## Admitted")
    lines.append("")
    for row in admitted:
        lines.extend(render_packet_row(row))
    lines.append("## Rejected from final admission")
    lines.append("")
    for row in rejected:
        lines.extend(render_packet_row(row))
    packet_path.write_text("\n".join(lines).strip() + "\n")


def render_packet_row(row: Dict[str, Any]) -> List[str]:
    lines: List[str] = []
    lines.append(f"### s{row['sequence_index']:02d} step {row['step_index']}: {row['node_id']}")
    lines.append(f"- admission: `{row['admission_status']}`")
    lines.append(f"- operator/practice: `{row['selected_operator_family']}` / `{row['practice_type']}`")
    lines.append(f"- option effect: `{row['option_effect']}`")
    lines.append(f"- function signature: `{row['function_signature_key']}`")
    lines.append(f"- framing variant: `{row['situation_framing_variant']}`")
    lines.append(f"- retrieved: `{row['selected_retrieved_episode_ids']}`")
    lines.append(f"- source: `{row['source_path']}`")
    lines.append("")
    lines.append(row["candidate_text"] or "")
    lines.append("")
    return lines


def cmd_seed_bundle(args: argparse.Namespace) -> None:
    bundle_path = Path(args.bundle).resolve()
    results_path = Path(args.results).resolve()
    bank_path = Path(args.bank).resolve()
    packet_path = Path(args.packet).resolve() if args.packet else bundle_path / "keeper_packet.md"
    run_record = find_run_record(results_path, bundle_path)
    new_rows = build_bank_rows(bundle_path, run_record)
    merged_rows = merge_rows(load_jsonl(bank_path), new_rows)
    write_jsonl(bank_path, merged_rows)
    write_packet(packet_path, run_record, new_rows)
    print(f"bank: {bank_path}")
    print(f"packet: {packet_path}")
    print(f"rows_added_or_updated: {len(new_rows)}")


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="Seed keeper-bank rows from a batch bundle.")
    subparsers = parser.add_subparsers(dest="cmd", required=True)

    seed = subparsers.add_parser("seed-bundle")
    seed.add_argument("--bundle", required=True, help="Path to a batch bundle containing batch.trace.json.")
    seed.add_argument("--results", default=str(DEFAULT_RESULTS), help="Path to the run ledger results.jsonl.")
    seed.add_argument("--bank", default=str(DEFAULT_BANK), help="Path to the keeper-bank JSONL file.")
    seed.add_argument("--packet", help="Optional output markdown packet path.")
    seed.set_defaults(func=cmd_seed_bundle)

    return parser


def main() -> None:
    parser = build_parser()
    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
