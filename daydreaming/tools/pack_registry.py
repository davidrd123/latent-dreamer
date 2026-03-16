from __future__ import annotations

import argparse
import copy
import json
from pathlib import Path
from typing import Any, Dict, Iterable, List

import yaml

DEFAULT_BANK = Path("daydreaming/out/authoring_time_generation/keeper_bank_supply_v1.jsonl")
DEFAULT_REGISTRY = Path("daydreaming/fixtures/patch_pack_registry_supply_v1.yaml")


def load_jsonl(path: Path) -> List[Dict[str, Any]]:
    if not path.exists():
        return []
    rows: List[Dict[str, Any]] = []
    for line in path.read_text().splitlines():
        line = line.strip()
        if line:
            rows.append(json.loads(line))
    return rows


def load_yaml(path: Path) -> Dict[str, Any]:
    data = yaml.safe_load(path.read_text())
    if not isinstance(data, dict):
        raise SystemExit(f"expected mapping at top level: {path}")
    return data


def write_yaml(path: Path, data: Dict[str, Any]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(yaml.safe_dump(data, sort_keys=False, allow_unicode=True))


def index_bank(rows: Iterable[Dict[str, Any]]) -> Dict[str, Dict[str, Any]]:
    return {row["bank_id"]: row for row in rows}


def get_pack_spec(registry: Dict[str, Any], pack_id: str) -> Dict[str, Any]:
    for pack in registry.get("packs", []):
        if pack.get("pack_id") == pack_id:
            return pack
    raise SystemExit(f"pack_id not found in registry: {pack_id}")


def apply_patch_nodes(
    base_doc: Dict[str, Any],
    pack_spec: Dict[str, Any],
    bank_index: Dict[str, Dict[str, Any]],
) -> Dict[str, Any]:
    doc = copy.deepcopy(base_doc)
    if pack_spec.get("title"):
        doc["title"] = pack_spec["title"]
    if pack_spec.get("graph_id"):
        doc["graph_id"] = pack_spec["graph_id"]

    node_index = {node["id"]: node for node in doc.get("nodes", [])}
    for patch in pack_spec.get("patch_nodes", []):
        patch_node_id = patch["patch_node_id"]
        if patch_node_id not in node_index:
            raise SystemExit(f"patch node not found in base fixture: {patch_node_id}")
        node = node_index[patch_node_id]
        bank_id = patch.get("bank_id")
        bank_row = bank_index.get(bank_id) if bank_id else None
        overrides = patch.get("overrides", {})

        if bank_row and "visual_description" not in overrides:
            overrides = {"visual_description": bank_row.get("candidate_text"), **overrides}

        for key, value in overrides.items():
            node[key] = value

    return doc


def cmd_build_pack(args: argparse.Namespace) -> None:
    registry_path = Path(args.registry).resolve()
    bank_path = Path(args.bank).resolve()
    registry = load_yaml(registry_path)
    pack_spec = get_pack_spec(registry, args.pack_id)
    base_path = Path(pack_spec["base_fixture"]).resolve()
    output_path = Path(args.output).resolve() if args.output else Path(pack_spec["output_fixture"]).resolve()
    base_doc = load_yaml(base_path)
    bank_rows = load_jsonl(bank_path)
    built = apply_patch_nodes(base_doc, pack_spec, index_bank(bank_rows))
    write_yaml(output_path, built)
    print(f"pack_id: {pack_spec['pack_id']}")
    print(f"output: {output_path}")


def cmd_build_all(args: argparse.Namespace) -> None:
    registry_path = Path(args.registry).resolve()
    bank_path = Path(args.bank).resolve()
    registry = load_yaml(registry_path)
    bank_rows = load_jsonl(bank_path)
    bank_index = index_bank(bank_rows)
    for pack_spec in registry.get("packs", []):
        base_path = Path(pack_spec["base_fixture"]).resolve()
        output_path = Path(pack_spec["output_fixture"]).resolve()
        base_doc = load_yaml(base_path)
        built = apply_patch_nodes(base_doc, pack_spec, bank_index)
        write_yaml(output_path, built)
        print(f"built: {pack_spec['pack_id']} -> {output_path}")


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="Build generated patch-pack fixtures from a registry.")
    subparsers = parser.add_subparsers(dest="cmd", required=True)

    build_pack = subparsers.add_parser("build-pack")
    build_pack.add_argument("--pack-id", required=True)
    build_pack.add_argument("--registry", default=str(DEFAULT_REGISTRY))
    build_pack.add_argument("--bank", default=str(DEFAULT_BANK))
    build_pack.add_argument("--output")
    build_pack.set_defaults(func=cmd_build_pack)

    build_all = subparsers.add_parser("build-all")
    build_all.add_argument("--registry", default=str(DEFAULT_REGISTRY))
    build_all.add_argument("--bank", default=str(DEFAULT_BANK))
    build_all.set_defaults(func=cmd_build_all)

    return parser


def main() -> None:
    parser = build_parser()
    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
