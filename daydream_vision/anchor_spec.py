from __future__ import annotations

import json
from typing import Any, Optional

from .paths import resolve_path

TEXT_ALIASES = {
    "entityType": ("entityType", "entity_type"),
    "name": ("name",),
    "brief": ("brief",),
}

LIST_ALIASES = {
    "styleTags": ("styleTags", "style_tags"),
    "visualConstraints": ("visualConstraints", "visual_constraints"),
    "negativeConstraints": ("negativeConstraints", "negative_constraints"),
    "consistencyNotes": ("consistencyNotes", "consistency_notes"),
    "sourceRefs": ("sourceRefs", "source_refs"),
}


def _clean_text(value: Any) -> Optional[str]:
    if not isinstance(value, str):
        return None
    cleaned = value.strip()
    return cleaned or None


def _clean_list(value: Any) -> list[str]:
    if value is None:
        return []
    if isinstance(value, str):
        raw_values = [value]
    elif isinstance(value, list):
        raw_values = value
    else:
        return []

    cleaned: list[str] = []
    seen: set[str] = set()
    for item in raw_values:
        if not isinstance(item, str):
            continue
        normalized = item.strip()
        if not normalized:
            continue
        dedupe_key = normalized.casefold()
        if dedupe_key in seen:
            continue
        seen.add(dedupe_key)
        cleaned.append(normalized)
    return cleaned


def _first_present(spec: dict[str, Any], aliases: tuple[str, ...]) -> Any:
    for key in aliases:
        if key in spec:
            return spec.get(key)
    return None


def normalize_anchor_spec(
    spec: Optional[dict[str, Any]],
    *,
    fallback_brief: Optional[str] = None,
    fallback_name: Optional[str] = None,
) -> dict[str, Any]:
    payload = spec if isinstance(spec, dict) else {}
    normalized: dict[str, Any] = {}

    for key, aliases in TEXT_ALIASES.items():
        value = _clean_text(_first_present(payload, aliases))
        if value:
            normalized[key] = value

    entity_type = normalized.get("entityType")
    if isinstance(entity_type, str):
        normalized["entityType"] = entity_type.replace(" ", "_").lower()

    for key, aliases in LIST_ALIASES.items():
        values = _clean_list(_first_present(payload, aliases))
        if values:
            normalized[key] = values

    if fallback_name and "name" not in normalized:
        normalized["name"] = fallback_name.strip()
    if fallback_brief and "brief" not in normalized:
        normalized["brief"] = fallback_brief.strip()

    return normalized


def merge_anchor_spec(
    base_spec: Optional[dict[str, Any]],
    overlay_spec: Optional[dict[str, Any]],
    *,
    fallback_brief: Optional[str] = None,
    fallback_name: Optional[str] = None,
) -> dict[str, Any]:
    merged = normalize_anchor_spec(base_spec)
    merged.update(normalize_anchor_spec(overlay_spec))
    if fallback_name and not merged.get("name"):
        merged["name"] = fallback_name.strip()
    if fallback_brief and not merged.get("brief"):
        merged["brief"] = fallback_brief.strip()
    return merged


def load_anchor_spec_file(spec_path: Optional[str]) -> dict[str, Any]:
    if not spec_path:
        return {}
    path = resolve_path(spec_path)
    payload = json.loads(path.read_text(encoding="utf-8"))
    if isinstance(payload, dict) and isinstance(payload.get("anchorSpec"), dict):
        payload = payload["anchorSpec"]
    if not isinstance(payload, dict):
        raise RuntimeError(f"ANCHOR_SPEC_INVALID: expected object in {path}")
    return normalize_anchor_spec(payload)


def build_anchor_spec_input(
    *,
    base_spec: Optional[dict[str, Any]] = None,
    spec_path: Optional[str] = None,
    entity_type: Optional[str] = None,
    name: Optional[str] = None,
    brief: Optional[str] = None,
    style_tags: Optional[list[str]] = None,
    visual_constraints: Optional[list[str]] = None,
    negative_constraints: Optional[list[str]] = None,
    consistency_notes: Optional[list[str]] = None,
    source_refs: Optional[list[str]] = None,
    fallback_brief: Optional[str] = None,
    fallback_name: Optional[str] = None,
) -> dict[str, Any]:
    file_spec = load_anchor_spec_file(spec_path)
    cli_spec = normalize_anchor_spec(
        {
            "entityType": entity_type,
            "name": name,
            "brief": brief,
            "styleTags": style_tags or [],
            "visualConstraints": visual_constraints or [],
            "negativeConstraints": negative_constraints or [],
            "consistencyNotes": consistency_notes or [],
            "sourceRefs": source_refs or [],
        }
    )
    return merge_anchor_spec(
        merge_anchor_spec(base_spec, file_spec),
        cli_spec,
        fallback_brief=fallback_brief,
        fallback_name=fallback_name,
    )


def format_anchor_spec(
    spec: Optional[dict[str, Any]],
    *,
    fallback_brief: Optional[str] = None,
    include_source_refs: bool = False,
) -> str:
    normalized = normalize_anchor_spec(spec, fallback_brief=fallback_brief)
    lines: list[str] = []

    entity_type = normalized.get("entityType")
    name = normalized.get("name")
    if isinstance(entity_type, str) and isinstance(name, str):
        lines.append(f"Entity: {entity_type} ({name})")
    elif isinstance(entity_type, str):
        lines.append(f"Entity type: {entity_type}")
    elif isinstance(name, str):
        lines.append(f"Name: {name}")

    brief = normalized.get("brief")
    if isinstance(brief, str):
        lines.append(f"Brief: {brief}")

    for key, label in (
        ("styleTags", "Style tags"),
        ("visualConstraints", "Must include"),
        ("negativeConstraints", "Avoid"),
        ("consistencyNotes", "Consistency notes"),
    ):
        values = normalized.get(key)
        if isinstance(values, list) and values:
            lines.append(f"{label}: {', '.join(values)}")

    if include_source_refs:
        refs = normalized.get("sourceRefs")
        if isinstance(refs, list) and refs:
            lines.append(f"Source refs: {', '.join(refs)}")

    return "\n".join(lines)
