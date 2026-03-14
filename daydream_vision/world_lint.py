from __future__ import annotations

from pathlib import Path
import re
from typing import Any, Iterable, Optional

from .brief_bridge import load_brief_note
from .paths import resolve_path


_STOPWORDS = {
    "the",
    "a",
    "an",
    "and",
    "or",
    "to",
    "of",
    "in",
    "on",
    "for",
    "with",
    "from",
    "into",
    "at",
    "by",
    "it",
    "is",
    "are",
    "be",
    "this",
    "that",
    "where",
    "what",
    "who",
    "when",
    "through",
    "across",
    "under",
    "over",
    "their",
    "them",
    "they",
    "only",
    "once",
    "still",
}


def _clean_text(value: Any) -> Optional[str]:
    if not isinstance(value, str):
        return None
    cleaned = " ".join(value.split())
    return cleaned or None


def _tokenize(text: str) -> list[str]:
    return re.findall(r"[a-z0-9]+", text.casefold())


def _distinctive_terms(text: str, *, min_len: int = 4) -> list[str]:
    terms: list[str] = []
    seen: set[str] = set()
    for token in _tokenize(text):
        if len(token) < min_len or token in _STOPWORDS:
            continue
        if token in seen:
            continue
        seen.add(token)
        terms.append(token)
    return terms


def _item_label(item: Any) -> Optional[str]:
    cleaned = _clean_text(item)
    if not cleaned:
        return None
    label = cleaned.split("(", 1)[0].strip()
    label = re.sub(r"^(the|a|an)\s+", "", label, flags=re.IGNORECASE)
    return label or None


def _contains_term(text: str, term: str) -> bool:
    return bool(re.search(rf"\b{re.escape(term)}\b", text.casefold()))


def _surface_entries(brief: dict[str, Any]) -> list[dict[str, str]]:
    entries: list[dict[str, str]] = []
    creative_brief = brief.get("creativeBrief") or {}
    world_data = brief.get("worldData") or {}

    concept = _clean_text(creative_brief.get("concept"))
    if concept:
        entries.append({"kind": "concept", "id": "concept", "text": concept})

    for key in ("core_tensions", "interpretive_angles", "obsessions"):
        values = creative_brief.get(key)
        if not isinstance(values, list):
            continue
        for index, value in enumerate(values, start=1):
            cleaned = _clean_text(value)
            if cleaned:
                entries.append({"kind": key, "id": f"{key}:{index}", "text": cleaned})

    places = brief.get("places") or []
    for entry in places:
        if not isinstance(entry, dict):
            continue
        description = _clean_text(entry.get("description"))
        identifier = _clean_text(entry.get("id")) or "place"
        if description:
            entries.append({"kind": "place", "id": identifier, "text": description})

    situations = brief.get("situations") or []
    for entry in situations:
        if not isinstance(entry, dict):
            continue
        description = _clean_text(entry.get("description"))
        identifier = _clean_text(entry.get("id")) or "situation"
        if description:
            entries.append({"kind": "situation", "id": identifier, "text": description})
        indices = entry.get("indices")
        if isinstance(indices, list):
            flattened = ", ".join(
                cleaned
                for cleaned in (_clean_text(item) for item in indices)
                if cleaned
            )
            if flattened:
                entries.append({"kind": "situation_indices", "id": identifier, "text": flattened})

    events = world_data.get("events")
    if isinstance(events, list):
        for entry in events:
            if not isinstance(entry, dict):
                continue
            description = _clean_text(entry.get("description"))
            identifier = _clean_text(entry.get("id")) or "event"
            if description:
                entries.append({"kind": "event", "id": identifier, "text": description})

    return entries


def _matching_entries(entries: Iterable[dict[str, str]], terms: list[str]) -> list[dict[str, str]]:
    matches: list[dict[str, str]] = []
    if not terms:
        return matches
    for entry in entries:
        text = entry.get("text", "")
        if any(_contains_term(text, term) for term in terms):
            matches.append(entry)
    return matches


def _evidence_labels(entries: list[dict[str, str]], *, max_items: int = 3) -> list[str]:
    labels: list[str] = []
    seen: set[str] = set()
    for entry in entries:
        label = f"{entry.get('kind')}:{entry.get('id')}"
        if label in seen:
            continue
        seen.add(label)
        labels.append(label)
        if len(labels) >= max_items:
            break
    return labels


def _make_finding(
    *,
    severity: str,
    code: str,
    entity_type: str,
    entity_id: str,
    message: str,
    suggestion: Optional[str] = None,
    evidence: Optional[list[str]] = None,
) -> dict[str, Any]:
    finding: dict[str, Any] = {
        "severity": severity,
        "code": code,
        "entityType": entity_type,
        "entityId": entity_id,
        "message": message,
    }
    if suggestion:
        finding["suggestion"] = suggestion
    if evidence:
        finding["evidence"] = evidence
    return finding


def lint_brief_world(brief_path: str | Path) -> dict[str, Any]:
    resolved = resolve_path(str(brief_path))
    brief = load_brief_note(resolved)
    creative_brief = brief.get("creativeBrief") or {}
    world_data = brief.get("worldData") or {}
    entries = _surface_entries(brief)
    findings: list[dict[str, Any]] = []

    characters = brief.get("characters") or []
    places = brief.get("places") or []
    situations = brief.get("situations") or []
    events = world_data.get("events") if isinstance(world_data.get("events"), list) else []
    charged_objects = creative_brief.get("charged_objects") if isinstance(creative_brief.get("charged_objects"), list) else []

    place_ids = {
        place_id
        for place in places
        if isinstance(place, dict)
        for place_id in [_clean_text(place.get("id"))]
        if place_id
    }

    place_usage_counts: dict[str, int] = {place_id: 0 for place_id in place_ids}
    for situation in situations:
        if not isinstance(situation, dict):
            continue
        situation_id = _clean_text(situation.get("id")) or "situation"
        place_id = _clean_text(situation.get("place"))
        if not place_id:
            findings.append(
                _make_finding(
                    severity="warn",
                    code="situation_without_place",
                    entity_type="situation",
                    entity_id=situation_id,
                    message="Situation has no associated place.",
                    suggestion="Attach the situation to a place so route and staging logic can surface it spatially.",
                )
            )
            continue
        if place_id not in place_ids:
            findings.append(
                _make_finding(
                    severity="error",
                    code="undefined_place_reference",
                    entity_type="situation",
                    entity_id=situation_id,
                    message=f"Situation references undefined place `{place_id}`.",
                    suggestion="Define the place in world.yaml or change the situation's place reference.",
                )
            )
            continue
        place_usage_counts[place_id] = place_usage_counts.get(place_id, 0) + 1

    for place in places:
        if not isinstance(place, dict):
            continue
        place_id = _clean_text(place.get("id")) or "place"
        if place_usage_counts.get(place_id, 0) > 0:
            continue
        findings.append(
            _make_finding(
                severity="warn",
                code="place_without_role",
                entity_type="place",
                entity_id=place_id,
                message="Place is defined but no situation is anchored to it.",
                suggestion="Add at least one situation, event consequence, or route dependency that makes this place matter.",
            )
        )

    non_character_entries = [entry for entry in entries if entry.get("kind") != "character"]
    for character in characters:
        if not isinstance(character, dict):
            continue
        character_id = _clean_text(character.get("id")) or "character"
        name = _clean_text(character.get("name")) or character_id
        terms = _distinctive_terms(f"{character_id} {name}", min_len=4)
        matches = _matching_entries(non_character_entries, terms)
        if matches:
            continue
        findings.append(
            _make_finding(
                severity="warn",
                code="character_without_surface",
                entity_type="character",
                entity_id=character_id,
                message="Character is declared but does not surface in concept, situations, or events.",
                suggestion="Add at least one situation, event, or tension sentence that makes this character legible in the world.",
            )
        )

    support_entries = [entry for entry in entries if entry.get("kind") not in {"event"}]
    for event in events:
        if not isinstance(event, dict):
            continue
        event_id = _clean_text(event.get("id")) or "event"
        description = _clean_text(event.get("description"))
        if not description:
            findings.append(
                _make_finding(
                    severity="warn",
                    code="event_without_description",
                    entity_type="event",
                    entity_id=event_id,
                    message="Event is present without a description.",
                    suggestion="Give the event a concrete description so the world can surface it downstream.",
                )
            )
            continue
        terms = _distinctive_terms(description, min_len=5)
        matches = _matching_entries(support_entries, terms)
        if matches:
            continue
        findings.append(
            _make_finding(
                severity="warn",
                code="event_without_surface",
                entity_type="event",
                entity_id=event_id,
                message="Event does not appear to reshape any situation or broader world text.",
                suggestion="Echo the event in a situation, place description, or tension so it has visible downstream consequence.",
            )
        )

    object_support_entries = [entry for entry in entries if entry.get("kind") != "place"]
    for index, item in enumerate(charged_objects, start=1):
        label = _item_label(item)
        if not label:
            continue
        object_id = slug = re.sub(r"\s+", "_", label.casefold())
        terms = _distinctive_terms(label, min_len=5)
        matches = _matching_entries(object_support_entries, terms)
        if matches:
            continue
        findings.append(
            _make_finding(
                severity="warn",
                code="charged_object_without_function",
                entity_type="charged_object",
                entity_id=object_id,
                message="Charged object is declared but does not surface anywhere outside the charged-object list.",
                suggestion="Give the object at least one visible role in a situation, event, or route dependency.",
            )
        )

    severity_order = {"error": 0, "warn": 1, "info": 2}
    findings.sort(key=lambda item: (severity_order.get(str(item.get("severity")), 9), str(item.get("code")), str(item.get("entityId"))))

    counts = {"error": 0, "warn": 0, "info": 0}
    for finding in findings:
        severity = str(finding.get("severity"))
        if severity in counts:
            counts[severity] += 1

    return {
        "command": "lint-brief",
        "briefPath": str(resolved),
        "worldSummary": {
            "characterCount": len(characters),
            "placeCount": len(places),
            "situationCount": len(situations),
            "eventCount": len(events),
            "chargedObjectCount": len(charged_objects),
        },
        "counts": {
            **counts,
            "total": len(findings),
        },
        "findings": findings,
    }
