from __future__ import annotations

import datetime
import json
import re
from pathlib import Path
from typing import Any, Optional

from .paths import resolve_path, slugify

try:
    import yaml
except Exception:  # pragma: no cover
    yaml = None  # type: ignore[assignment]


_CHARACTER_STOPWORDS = {
    "the",
    "a",
    "an",
    "of",
    "and",
    "or",
    "to",
    "in",
    "on",
    "for",
}

_ROLE_HINTS: dict[str, dict[str, list[str] | str]] = {
    "courier": {
        "prompt": "carrying something small but decisive through the night",
        "brief": "a pressured night courier whose route keeps rewriting the mission",
        "visualConstraints": ["small silver package case"],
        "consistencyNotes": ["urgent transit posture", "route pressure should stay visible"],
    },
    "runner": {
        "prompt": "fast, alert, route-smart, built for detours",
        "brief": "a quick-moving street runner shaped by reroutes and thresholds",
        "consistencyNotes": ["mid-run posture", "streetwise detour energy"],
    },
    "hostess": {
        "prompt": "keeper of a temporary interior refuge under borrowed tungsten light",
        "brief": "a protective interior figure who offers brief shelter without stopping the night",
        "visualConstraints": ["borrowed kitchen tungsten pool"],
        "consistencyNotes": ["temporary refuge energy", "steady under pressure"],
    },
    "crowd": {
        "prompt": "a collective street body that can conceal or expose in the same motion",
        "brief": "not an individual but a weather-like crowd current of concealment, drag, and sudden exposure",
        "visualConstraints": ["many figures moving as one"],
        "consistencyNotes": ["collective rather than singular", "social pressure should feel active"],
    },
    "guide": {
        "prompt": "a watchful pathfinder reading a dangerous landscape",
        "brief": "a guide whose belief is holding the path together",
        "visualConstraints": ["metal bolt wrapped in cloth"],
        "consistencyNotes": ["faith and dread held together"],
    },
    "stalker": {
        "prompt": "a weathered guide moving with reverent dread",
        "brief": "a sincere but fragile believer in the Zone's reading of desire",
        "visualConstraints": ["metal bolt wrapped in cloth"],
        "consistencyNotes": ["meditative danger", "weathered patience"],
    },
    "crew": {
        "prompt": "a cold-weather shipboard survivor in ritual maintenance",
        "brief": "a crew member enduring frozen stasis through routine, repair, and shared purpose",
        "visualConstraints": ["felt wool coat"],
        "consistencyNotes": ["shipboard maintenance energy", "cold endurance"],
    },
    "sailor": {
        "prompt": "a cold-weather sailor maintaining a ship that no longer moves",
        "brief": "a sailor caught between duty, beauty, and dread in frozen stasis",
        "visualConstraints": ["felt wool coat"],
        "consistencyNotes": ["shipboard endurance", "frozen routine"],
    },
}


def _require_yaml() -> Any:
    if yaml is None:
        raise RuntimeError("PyYAML is required for brief bridge commands. Run `uv sync` to install dependencies.")
    return yaml


def _clean_text(value: Any) -> Optional[str]:
    if not isinstance(value, str):
        return None
    cleaned = " ".join(value.split())
    return cleaned or None


def _slug_list(values: list[str], *, max_items: int = 3) -> list[str]:
    tags: list[str] = []
    seen: set[str] = set()
    for value in values:
        tag = slugify(value, max_len=32)
        if not tag or tag in seen:
            continue
        seen.add(tag)
        tags.append(tag)
        if len(tags) >= max_items:
            break
    return tags


def _dedupe_preserve_order(values: list[str], *, max_items: Optional[int] = None) -> list[str]:
    cleaned: list[str] = []
    seen: set[str] = set()
    for value in values:
        normalized = _clean_text(value)
        if not normalized:
            continue
        key = normalized.casefold()
        if key in seen:
            continue
        seen.add(key)
        cleaned.append(normalized)
        if max_items is not None and len(cleaned) >= max_items:
            break
    return cleaned


def _first_sentence(text: Optional[str], *, fallback_words: int = 24) -> Optional[str]:
    if not text:
        return None
    normalized = _clean_text(text)
    if not normalized:
        return None
    match = re.match(r"(.+?[.!?])(?:\s|$)", normalized)
    if match:
        return match.group(1).strip()
    words = normalized.split()
    return " ".join(words[:fallback_words]).strip()


def _title_from_id(identifier: str) -> str:
    value = identifier
    value = re.sub(r"^[a-z]\d+_", "", value)
    value = value.replace("-", " ").replace("_", " ")
    value = re.sub(r"\s+", " ", value).strip()
    return value.title() if value else identifier


def _tokenize_text(text: str) -> list[str]:
    return re.findall(r"[a-z0-9]+", text.casefold())


def _role_terms(identifier: str, name: str) -> list[str]:
    tokens: list[str] = []
    for source in (identifier, name):
        for token in _tokenize_text(source):
            if len(token) < 3 or token in _CHARACTER_STOPWORDS:
                continue
            tokens.append(token)
    return _dedupe_preserve_order(tokens)


def _text_mentions_terms(text: str, terms: list[str]) -> bool:
    lowered = text.casefold()
    return any(re.search(rf"\b{re.escape(term)}\b", lowered) for term in terms)


def _keyword_overlap(left: list[str], right: list[str]) -> int:
    left_set = {token for token in left if len(token) >= 3 and token not in _CHARACTER_STOPWORDS}
    right_set = {token for token in right if len(token) >= 3 and token not in _CHARACTER_STOPWORDS}
    return len(left_set & right_set)


def _first_clause(text: Optional[str], *, max_words: int = 14) -> Optional[str]:
    if not text:
        return None
    cleaned = _clean_text(text)
    if not cleaned:
        return None
    chunk = re.split(r"[.;:]", cleaned, maxsplit=1)[0].strip()
    words = chunk.split()
    return " ".join(words[:max_words]).strip()


def _prompt_clause(text: Optional[str], *, max_words: int = 14) -> Optional[str]:
    clause = _first_clause(text, max_words=max_words)
    if not clause:
        return None
    clause = re.sub(r"\([^)]*\)", "", clause).strip()
    if "(" in clause and ")" not in clause:
        clause = clause.split("(", 1)[0].strip()
    clause = re.sub(r"\s+", " ", clause).strip(" ,;:-")
    return clause or None


def _leading_object_label(text: str) -> Optional[str]:
    cleaned = _clean_text(text)
    if not cleaned:
        return None
    label = cleaned.split("(", 1)[0].strip()
    label = re.sub(r"^(the|a|an)\s+", "", label, flags=re.IGNORECASE)
    return label or None


def _role_hint_for_terms(terms: list[str]) -> Optional[dict[str, list[str] | str]]:
    for term in terms:
        hint = _ROLE_HINTS.get(term)
        if hint:
            return hint
    return None


def _place_description_map(brief: dict[str, Any]) -> dict[str, str]:
    mapping: dict[str, str] = {}
    for entry in brief.get("places") or []:
        if not isinstance(entry, dict):
            continue
        identifier = _clean_text(entry.get("id"))
        description = _clean_text(entry.get("description"))
        if identifier and description:
            mapping[identifier] = description
    return mapping


def _character_context(entry: dict[str, Any], *, brief: dict[str, Any]) -> dict[str, list[str] | Optional[str]]:
    creative_brief = brief["creativeBrief"]
    world_data = brief.get("worldData") or {}
    identifier = _clean_text(entry.get("id")) or ""
    name = _clean_text(entry.get("name")) or _title_from_id(identifier or "character")
    terms = _role_terms(identifier, name)
    role_hint = _role_hint_for_terms(terms)

    mentions: list[str] = []
    associated_place_ids: list[str] = []

    concept = _first_sentence(_clean_text(creative_brief.get("concept")))
    if concept and _text_mentions_terms(concept, terms):
        mentions.append(concept)

    for key in ("core_tensions", "interpretive_angles", "obsessions"):
        values = creative_brief.get(key)
        if not isinstance(values, list):
            continue
        for item in values:
            cleaned = _clean_text(item)
            if cleaned and _text_mentions_terms(cleaned, terms):
                mentions.append(_first_sentence(cleaned) or cleaned)

    events = world_data.get("events")
    if isinstance(events, list):
        for event in events:
            if not isinstance(event, dict):
                continue
            description = _clean_text(event.get("description"))
            if description and _text_mentions_terms(description, terms):
                mentions.append(_first_sentence(description) or description)

    situations = brief.get("situations") or []
    if isinstance(situations, list):
        for situation in situations:
            if not isinstance(situation, dict):
                continue
            description = _clean_text(situation.get("description"))
            indices = situation.get("indices")
            index_match = False
            if isinstance(indices, list):
                for item in indices:
                    cleaned = _clean_text(item)
                    if cleaned and _text_mentions_terms(cleaned.replace("emotion:", ""), terms):
                        index_match = True
                        break
            if description and _text_mentions_terms(description, terms):
                mentions.append(_first_sentence(description) or description)
                place_id = _clean_text(situation.get("place"))
                if place_id:
                    associated_place_ids.append(place_id)
            elif index_match:
                situation_summary = _first_sentence(description) if description else None
                if situation_summary:
                    mentions.append(situation_summary)
                place_id = _clean_text(situation.get("place"))
                if place_id:
                    associated_place_ids.append(place_id)

    mention_tokens: list[str] = []
    for mention in mentions:
        mention_tokens.extend(_tokenize_text(mention))

    associated_objects: list[str] = []
    charged_objects = creative_brief.get("charged_objects")
    if isinstance(charged_objects, list):
        for item in charged_objects:
            cleaned = _clean_text(item)
            if not cleaned:
                continue
            label = _leading_object_label(cleaned)
            if not label:
                continue
            overlap = _keyword_overlap(_tokenize_text(label), terms + mention_tokens)
            if overlap > 0:
                associated_objects.append(label)

    place_descriptions = _place_description_map(brief)
    associated_places = [
        place_descriptions[place_id]
        for place_id in _dedupe_preserve_order(associated_place_ids)
        if place_id in place_descriptions
    ]

    return {
        "mentions": _dedupe_preserve_order(mentions, max_items=3),
        "associatedObjects": _dedupe_preserve_order(associated_objects, max_items=3),
        "associatedPlaces": _dedupe_preserve_order(associated_places, max_items=2),
        "rolePrompt": (_clean_text(role_hint.get("prompt")) if isinstance(role_hint, dict) else None),
        "roleBrief": (_clean_text(role_hint.get("brief")) if isinstance(role_hint, dict) else None),
        "roleConstraints": (
            _dedupe_preserve_order(role_hint.get("visualConstraints", []), max_items=2)
            if isinstance(role_hint, dict)
            else []
        ),
        "roleNotes": (
            _dedupe_preserve_order(role_hint.get("consistencyNotes", []), max_items=2)
            if isinstance(role_hint, dict)
            else []
        ),
    }


def _extract_palette_context(markdown_text: str) -> dict[str, Any]:
    match = re.search(r"^Palette:\s+`([^`]+)`\s+\((.*?)\)\s*$", markdown_text, flags=re.MULTILINE | re.DOTALL)
    if not match:
        return {}

    palette_id = match.group(1).strip()
    descriptor_blob = " ".join(match.group(2).split())
    raw_descriptors = [item.strip() for item in descriptor_blob.split(",") if item.strip()]
    return {
        "paletteId": palette_id,
        "paletteDescriptors": raw_descriptors,
    }


def _extract_yaml_blocks(markdown_text: str) -> list[dict[str, str]]:
    blocks: list[dict[str, str]] = []
    lines = markdown_text.splitlines()
    current_heading = ""
    index = 0

    while index < len(lines):
        line = lines[index]
        heading_match = re.match(r"^(##+)\s+(.*)$", line)
        if heading_match:
            current_heading = heading_match.group(2).strip()
            index += 1
            continue

        if line.strip() == "```yaml":
            index += 1
            payload_lines: list[str] = []
            while index < len(lines) and lines[index].strip() != "```":
                payload_lines.append(lines[index])
                index += 1
            blocks.append(
                {
                    "heading": current_heading,
                    "text": "\n".join(payload_lines).strip(),
                }
            )
        index += 1

    return blocks


def _normalize_loose_yaml(text: str) -> str:
    lines = text.splitlines()
    normalized: list[str] = []
    index = 0

    while index < len(lines):
        line = lines[index]
        match = re.match(r"^(\s*)-\s+(.*)$", line)
        if not match:
            normalized.append(line)
            index += 1
            continue

        indent = match.group(1)
        remainder = match.group(2).strip()
        is_mapping_item = bool(re.match(r"^[A-Za-z_][A-Za-z0-9_-]*\s*:", remainder))
        if is_mapping_item:
            normalized.append(line)
            index += 1
            continue

        parts = [remainder]
        index += 1
        while index < len(lines):
            next_line = lines[index]
            next_indent = len(next_line) - len(next_line.lstrip(" "))
            current_indent = len(indent)
            if next_line.strip() == "":
                index += 1
                continue
            if next_indent <= current_indent:
                break
            if re.match(rf"^{re.escape(indent)}-\s+", next_line):
                break
            parts.append(next_line.strip())
            index += 1

        normalized.append(f"{indent}- {json.dumps(' '.join(parts))}")

    return "\n".join(normalized)


def _safe_load_yaml(text: str) -> Any:
    yaml_lib = _require_yaml()
    try:
        return yaml_lib.safe_load(text)
    except Exception:
        return yaml_lib.safe_load(_normalize_loose_yaml(text))


def _load_yaml_block(blocks: list[dict[str, str]], *, heading_contains: str) -> Optional[dict[str, Any]]:
    for block in blocks:
        heading = block.get("heading", "")
        if heading_contains not in heading:
            continue
        payload = _safe_load_yaml(block.get("text", ""))
        if isinstance(payload, dict):
            return payload
    return None


def _load_yaml_list_block(blocks: list[dict[str, str]], *, heading_contains: str, field_name: str) -> Optional[list[dict[str, Any]]]:
    for block in blocks:
        heading = block.get("heading", "")
        if heading_contains not in heading:
            continue
        payload = _safe_load_yaml(block.get("text", ""))
        if isinstance(payload, dict):
            values = payload.get(field_name)
            if isinstance(values, list):
                return [item for item in values if isinstance(item, dict)]
    return None


def load_brief_note(brief_path: str | Path) -> dict[str, Any]:
    path = resolve_path(str(brief_path))
    markdown_text = path.read_text(encoding="utf-8")
    blocks = _extract_yaml_blocks(markdown_text)

    creative_brief = _load_yaml_block(blocks, heading_contains="creative_brief.yaml")
    if creative_brief is None:
        raise RuntimeError(f"BRIEF_PARSE_ERROR: creative_brief.yaml block not found in {path}")

    style_extensions = _load_yaml_block(blocks, heading_contains="style_extensions.yaml") or {}
    world_data = _load_yaml_block(blocks, heading_contains="world.yaml") or {}
    if not isinstance(world_data, dict):
        world_data = {}

    situations = world_data.get("situations")
    if not isinstance(situations, list):
        situations = _load_yaml_list_block(blocks, heading_contains="Situations", field_name="situations") or []

    characters = world_data.get("characters") if isinstance(world_data.get("characters"), list) else []
    places = world_data.get("places") if isinstance(world_data.get("places"), list) else []

    palette_context = _extract_palette_context(markdown_text)

    return {
        "briefPath": str(path),
        "creativeBrief": creative_brief,
        "styleExtensions": style_extensions,
        "worldData": world_data,
        "characters": [item for item in characters if isinstance(item, dict)],
        "places": [item for item in places if isinstance(item, dict)],
        "situations": [item for item in situations if isinstance(item, dict)],
        "paletteContext": palette_context,
    }


def _defaults_from_brief(brief: dict[str, Any]) -> dict[str, Any]:
    creative_brief = brief["creativeBrief"]
    style_extensions = brief["styleExtensions"]
    palette = brief.get("paletteContext") or {}
    palette_tags = []
    palette_id = palette.get("paletteId")
    if isinstance(palette_id, str):
        palette_tags.append(palette_id)
    descriptors = palette.get("paletteDescriptors")
    if isinstance(descriptors, list):
        palette_tags.extend(_slug_list([item for item in descriptors if isinstance(item, str)], max_items=3))

    negative_space = style_extensions.get("negative_space")
    negative_constraints = [item for item in negative_space if isinstance(item, str)] if isinstance(negative_space, list) else []

    defaults: dict[str, Any] = {
        "numOutputs": 1,
        "sourceRefs": [brief["briefPath"]],
    }
    if palette_tags:
        defaults["styleTags"] = palette_tags
    if negative_constraints:
        defaults["negativeConstraints"] = negative_constraints

    world_data = brief.get("worldData") or {}
    source_palette = world_data.get("source_palette")
    if isinstance(source_palette, str) and source_palette.strip():
        defaults["sourcePalette"] = source_palette.strip()

    title = creative_brief.get("title")
    if isinstance(title, str) and title.strip():
        defaults["worldTitle"] = title.strip()

    return defaults


def _character_anchor(entry: dict[str, Any], *, brief: dict[str, Any]) -> dict[str, Any]:
    creative_brief = brief["creativeBrief"]
    name = _clean_text(entry.get("name")) or _title_from_id(str(entry.get("id", "character")))
    identifier = _clean_text(entry.get("id")) or slugify(name)
    title = _clean_text(creative_brief.get("title")) or "this world"
    concept = _first_sentence(_clean_text(creative_brief.get("concept")))
    context = _character_context(entry, brief=brief)

    prompt_parts = [f"portrait of {name}"]
    mentions = context.get("mentions")
    focus_prompt = mentions[0] if isinstance(mentions, list) and mentions else None
    focus_prompt = _prompt_clause(focus_prompt) or _clean_text(context.get("rolePrompt")) or concept
    if focus_prompt:
        prompt_parts.append(focus_prompt)
    place_prompt = None
    associated_places = context.get("associatedPlaces")
    if isinstance(associated_places, list) and associated_places:
        place_prompt = _prompt_clause(str(associated_places[0]))
    if place_prompt:
        prompt_parts.append(place_prompt)
    else:
        prompt_parts.append(f"in {title}")

    anchor_brief_parts = [f"{name} in {title}."]
    if isinstance(mentions, list):
        anchor_brief_parts.extend(mentions[:2])
    if not isinstance(mentions, list) or not mentions:
        role_brief = _clean_text(context.get("roleBrief"))
        if role_brief:
            anchor_brief_parts.append(role_brief)
        elif concept:
            anchor_brief_parts.append(concept)

    visual_constraints = []
    associated_objects = context.get("associatedObjects")
    if isinstance(associated_objects, list):
        visual_constraints.extend(associated_objects[:2])
    role_constraints = context.get("roleConstraints")
    if isinstance(role_constraints, list):
        visual_constraints.extend(role_constraints[:2])
    visual_constraints = _dedupe_preserve_order(visual_constraints, max_items=3)

    consistency_notes = []
    if isinstance(associated_places, list) and associated_places:
        consistency_notes.append(f"associated place: {associated_places[0]}")
    role_notes = context.get("roleNotes")
    if isinstance(role_notes, list):
        consistency_notes.extend(role_notes[:2])
    consistency_notes = _dedupe_preserve_order(consistency_notes, max_items=3)

    anchor = {
        "anchorId": f"character:{identifier}",
        "label": identifier,
        "prompt": "; ".join(_dedupe_preserve_order(prompt_parts, max_items=3)),
        "entityType": "character",
        "name": name,
        "brief": " ".join(_dedupe_preserve_order(anchor_brief_parts, max_items=3)),
    }
    if visual_constraints:
        anchor["visualConstraints"] = visual_constraints
    if consistency_notes:
        anchor["consistencyNotes"] = consistency_notes
    return anchor


def _place_anchor(entry: dict[str, Any]) -> dict[str, Any]:
    identifier = _clean_text(entry.get("id")) or "place"
    name = _clean_text(entry.get("name")) or _title_from_id(identifier)
    description = _clean_text(entry.get("description")) or name
    return {
        "anchorId": f"location:{identifier}",
        "label": identifier,
        "prompt": description,
        "entityType": "location",
        "name": name,
        "brief": description,
    }


def _situation_anchor(entry: dict[str, Any]) -> dict[str, Any]:
    identifier = _clean_text(entry.get("id")) or "situation"
    name = _title_from_id(identifier)
    description = _clean_text(entry.get("description")) or name
    consistency_notes: list[str] = []
    place = _clean_text(entry.get("place"))
    if place:
        consistency_notes.append(f"associated place: {place}")
    indices = entry.get("indices")
    if isinstance(indices, list):
        keywords = [item for item in indices if isinstance(item, str) and not item.startswith("emotion:")]
        if keywords:
            consistency_notes.append(f"indices: {', '.join(keywords[:4])}")
    anchor: dict[str, Any] = {
        "anchorId": f"moodboard:{identifier}",
        "label": identifier,
        "prompt": description,
        "entityType": "moodboard",
        "name": name,
        "brief": description,
    }
    if consistency_notes:
        anchor["consistencyNotes"] = consistency_notes
    return anchor


def build_world_pack_from_brief(
    brief_path: str | Path,
    *,
    include_characters: bool = True,
    include_places: bool = True,
    include_situations: bool = True,
) -> dict[str, Any]:
    brief = load_brief_note(brief_path)
    creative_brief = brief["creativeBrief"]
    title = _clean_text(creative_brief.get("title")) or Path(str(brief_path)).stem

    anchors: list[dict[str, Any]] = []
    if include_characters:
        anchors.extend(_character_anchor(item, brief=brief) for item in brief["characters"])
    if include_places:
        anchors.extend(_place_anchor(item) for item in brief["places"])
    if include_situations:
        anchors.extend(_situation_anchor(item) for item in brief["situations"])

    if not anchors:
        raise RuntimeError(f"BRIEF_PACK_EMPTY: no anchors could be derived from {brief['briefPath']}")

    pack = {
        "name": f"{title} Brief Pack",
        "generatedFrom": "pack-from-brief",
        "generatedAt": datetime.datetime.now(datetime.timezone.utc).isoformat(),
        "sourceBriefPath": brief["briefPath"],
        "defaults": _defaults_from_brief(brief),
        "anchors": anchors,
    }

    palette = brief.get("paletteContext") or {}
    if palette:
        pack["palette"] = palette
    return pack


def write_world_pack_from_brief(
    brief_path: str | Path,
    *,
    output_path: str | Path,
    include_characters: bool = True,
    include_places: bool = True,
    include_situations: bool = True,
) -> dict[str, Any]:
    yaml_lib = _require_yaml()
    pack = build_world_pack_from_brief(
        brief_path,
        include_characters=include_characters,
        include_places=include_places,
        include_situations=include_situations,
    )
    output = resolve_path(str(output_path))
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(yaml_lib.safe_dump(pack, sort_keys=False, allow_unicode=False), encoding="utf-8")

    anchor_type_counts = {
        "characters": sum(1 for item in pack["anchors"] if str(item.get("anchorId", "")).startswith("character:")),
        "places": sum(1 for item in pack["anchors"] if str(item.get("anchorId", "")).startswith("location:")),
        "situations": sum(1 for item in pack["anchors"] if str(item.get("anchorId", "")).startswith("moodboard:")),
    }

    return {
        "command": "pack-from-brief",
        "briefPath": pack["sourceBriefPath"],
        "packPath": str(output),
        "packName": pack["name"],
        "counts": anchor_type_counts,
        "included": {
            "characters": include_characters,
            "places": include_places,
            "situations": include_situations,
        },
    }
