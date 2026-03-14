from __future__ import annotations

import json
from pathlib import Path
from typing import Any, Optional

from .anchor_spec import build_anchor_spec_input
from .manifest import append_manifest_record, get_anchor_record
from .paths import resolve_path
from .service import build_visual_anchor, compare_media_candidates

try:
    import yaml
except Exception:  # pragma: no cover
    yaml = None  # type: ignore[assignment]


def _load_structured_file(path: Path) -> dict[str, Any]:
    suffix = path.suffix.lower()
    text = path.read_text(encoding="utf-8")
    if suffix == ".json":
        payload = json.loads(text)
    elif suffix in {".yaml", ".yml"}:
        if yaml is None:
            raise RuntimeError("PyYAML is required for YAML world packs. Run `uv sync` to install dependencies.")
        payload = yaml.safe_load(text)
    else:
        raise RuntimeError(f"WORLD_PACK_UNSUPPORTED_FORMAT: {path}")

    if not isinstance(payload, dict):
        raise RuntimeError(f"WORLD_PACK_INVALID: expected object in {path}")
    return payload


def _clean_string(value: Any) -> Optional[str]:
    if not isinstance(value, str):
        return None
    cleaned = value.strip()
    return cleaned or None


def _clean_int(value: Any) -> Optional[int]:
    if value is None:
        return None
    try:
        return int(value)
    except Exception:
        return None


def _clean_float(value: Any) -> Optional[float]:
    if value is None:
        return None
    try:
        return float(value)
    except Exception:
        return None


def _normalize_anchor_entry(entry: dict[str, Any], defaults: dict[str, Any]) -> dict[str, Any]:
    anchor_id = _clean_string(entry.get("anchorId") or entry.get("id"))
    if not anchor_id:
        raise RuntimeError("WORLD_PACK_INVALID_ANCHOR: each anchor needs anchorId or id")

    prompt = _clean_string(entry.get("prompt"))
    if not prompt:
        raise RuntimeError(f"WORLD_PACK_INVALID_ANCHOR: {anchor_id} is missing prompt")

    label = _clean_string(entry.get("label"))
    num_outputs = _clean_int(entry.get("numOutputs"))
    if num_outputs is None:
        num_outputs = _clean_int(defaults.get("numOutputs")) or 1

    temperature = _clean_float(entry.get("temperature"))
    if temperature is None:
        default_temperature = _clean_float(defaults.get("temperature"))
        temperature = default_temperature if default_temperature is not None else 0.7

    anchor_spec = build_anchor_spec_input(
        base_spec=(entry.get("anchorSpec") if isinstance(entry.get("anchorSpec"), dict) else None),
        entity_type=_clean_string(entry.get("entityType") or defaults.get("entityType")),
        name=_clean_string(entry.get("name")),
        brief=_clean_string(entry.get("brief")),
        style_tags=(entry.get("styleTags") or defaults.get("styleTags")),
        visual_constraints=(entry.get("visualConstraints") or defaults.get("visualConstraints")),
        negative_constraints=(entry.get("negativeConstraints") or defaults.get("negativeConstraints")),
        consistency_notes=(entry.get("consistencyNotes") or defaults.get("consistencyNotes")),
        source_refs=(entry.get("sourceRefs") or defaults.get("sourceRefs")),
        fallback_brief=prompt,
        fallback_name=label,
    )

    return {
        "anchorId": anchor_id,
        "label": label,
        "prompt": prompt,
        "anchorSpec": anchor_spec,
        "numOutputs": num_outputs,
        "outputDir": _clean_string(entry.get("outputDir") or defaults.get("outputDir")),
        "editModel": _clean_string(entry.get("editModel") or defaults.get("editModel"))
        or "gemini-3.1-flash-image-preview",
        "analyzeModel": _clean_string(entry.get("analyzeModel") or defaults.get("analyzeModel"))
        or "gemini-3.1-flash-lite-preview",
        "temperature": temperature,
        "systemPrompt": _clean_string(entry.get("systemPrompt") or defaults.get("systemPrompt")) or "",
        "analyzeSystemPrompt": _clean_string(
            entry.get("analyzeSystemPrompt") or defaults.get("analyzeSystemPrompt")
        )
        or "",
        "analysisPrompt": _clean_string(entry.get("analysisPrompt") or defaults.get("analysisPrompt")),
        "aspectRatio": _clean_string(entry.get("aspectRatio") or defaults.get("aspectRatio")),
        "imageSize": _clean_string(entry.get("imageSize") or defaults.get("imageSize")),
    }


def load_world_pack(pack_path: str | Path) -> dict[str, Any]:
    path = resolve_path(str(pack_path))
    payload = _load_structured_file(path)
    defaults = payload.get("defaults")
    if defaults is None:
        defaults = {}
    if not isinstance(defaults, dict):
        raise RuntimeError(f"WORLD_PACK_INVALID: defaults must be an object in {path}")

    anchors_raw = payload.get("anchors")
    if not isinstance(anchors_raw, list) or not anchors_raw:
        raise RuntimeError(f"WORLD_PACK_INVALID: anchors must be a non-empty list in {path}")

    anchors = []
    for raw in anchors_raw:
        if not isinstance(raw, dict):
            raise RuntimeError(f"WORLD_PACK_INVALID_ANCHOR: expected object in {path}")
        anchors.append(_normalize_anchor_entry(raw, defaults))

    return {
        "command": "load-pack",
        "packPath": str(path),
        "name": _clean_string(payload.get("name")) or path.stem,
        "defaults": defaults,
        "anchors": anchors,
    }


def _get_existing_anchor(manifest_path: str | Path, *, anchor_id: str) -> Optional[dict[str, Any]]:
    try:
        record = get_anchor_record(manifest_path, anchor_id=anchor_id)
    except RuntimeError as exc:
        if str(exc).startswith("ANCHOR_NOT_FOUND:"):
            return None
        raise
    anchor = record.get("anchor")
    return anchor if isinstance(anchor, dict) else None


def _candidate_evaluation_summary(candidates: Any) -> dict[str, dict[str, int]]:
    match_counts: dict[str, int] = {}
    keep_counts: dict[str, int] = {}
    if not isinstance(candidates, list):
        return {
            "matchCounts": match_counts,
            "keepOrReworkCounts": keep_counts,
        }

    for candidate in candidates:
        if not isinstance(candidate, dict):
            continue
        analysis = candidate.get("analysis")
        if not isinstance(analysis, dict):
            continue
        structured = analysis.get("structured")
        if not isinstance(structured, dict):
            continue
        match = structured.get("match")
        if isinstance(match, str) and match:
            match_counts[match] = match_counts.get(match, 0) + 1
        keep = structured.get("keepOrRework")
        if isinstance(keep, str) and keep:
            keep_counts[keep] = keep_counts.get(keep, 0) + 1

    return {
        "matchCounts": match_counts,
        "keepOrReworkCounts": keep_counts,
    }


def _latest_comparison_summary(anchor: dict[str, Any]) -> Optional[dict[str, Any]]:
    latest = anchor.get("latestComparison")
    if not isinstance(latest, dict):
        return None
    comparison = latest.get("comparison")
    if not isinstance(comparison, dict):
        return None
    return {
        "timestamp": latest.get("timestamp"),
        "candidateCount": latest.get("candidateCount"),
        "bestCandidateIndex": comparison.get("bestCandidateIndex"),
        "ranking": comparison.get("ranking"),
        "overallNotes": comparison.get("overallNotes"),
    }


def _candidate_media_paths(anchor: dict[str, Any]) -> list[str]:
    candidates = anchor.get("candidates")
    if not isinstance(candidates, list):
        return []

    sortable: list[tuple[int, str]] = []
    for candidate in candidates:
        if not isinstance(candidate, dict):
            continue
        image = candidate.get("image")
        if not isinstance(image, dict):
            continue
        path = image.get("path")
        if not isinstance(path, str) or not path:
            continue
        try:
            candidate_index = int(candidate.get("candidateIndex"))
        except Exception:
            candidate_index = len(sortable) + 1
        sortable.append((candidate_index, path))
    sortable.sort(key=lambda item: item[0])
    return [path for _, path in sortable]


def run_world_pack(
    *,
    pack_path: str | Path,
    manifest_path: str | Path,
    only_ids: Optional[list[str]] = None,
    refresh_existing: bool = False,
    fail_fast: bool = False,
    limit: Optional[int] = None,
) -> dict[str, Any]:
    pack = load_world_pack(pack_path)
    selected = set(only_ids or [])

    results: list[dict[str, Any]] = []
    generated_count = 0
    skipped_count = 0
    failed_count = 0
    processed = 0

    for item in pack["anchors"]:
        anchor_id = item["anchorId"]
        if selected and anchor_id not in selected:
            continue
        if limit is not None and processed >= limit:
            break
        processed += 1

        existing = _get_existing_anchor(manifest_path, anchor_id=anchor_id)
        if existing is not None and not refresh_existing:
            skipped_count += 1
            results.append(
                {
                    "anchorId": anchor_id,
                    "label": item.get("label"),
                    "status": "skipped_existing",
                    "existingStatus": existing.get("status"),
                    "approvedImage": existing.get("approvedImage"),
                }
            )
            continue

        try:
            result = build_visual_anchor(
                anchor_id=anchor_id,
                prompt=item["prompt"],
                anchor_spec=item.get("anchorSpec"),
                num_outputs=item["numOutputs"],
                output_dir=item.get("outputDir"),
                label=item.get("label"),
                edit_model=item["editModel"],
                analyze_model=item["analyzeModel"],
                temperature=item["temperature"],
                system_prompt=item["systemPrompt"],
                analyze_system_prompt=item["analyzeSystemPrompt"],
                analysis_prompt=item.get("analysisPrompt"),
                aspect_ratio=item.get("aspectRatio"),
                image_size=item.get("imageSize"),
            )
            append_manifest_record(
                manifest_path,
                command=result["command"],
                label=item.get("label"),
                anchor_id=anchor_id,
                result=result,
            )
            generated_count += 1
            results.append(
                {
                    "anchorId": anchor_id,
                    "label": item.get("label"),
                    "status": "generated",
                    "candidateCount": result.get("candidateCount"),
                    "evaluationSummary": _candidate_evaluation_summary(result.get("candidates")),
                    "outputPaths": [
                        output.get("path")
                        for output in result.get("outputs", [])
                        if isinstance(output, dict) and isinstance(output.get("path"), str)
                    ],
                }
            )
        except Exception as exc:
            failed_count += 1
            results.append(
                {
                    "anchorId": anchor_id,
                    "label": item.get("label"),
                    "status": "failed",
                    "error": str(exc),
                }
            )
            if fail_fast:
                break

    return {
        "command": "pack-run",
        "packPath": pack["packPath"],
        "packName": pack["name"],
        "manifestPath": str(resolve_path(str(manifest_path))),
        "selectedCount": processed,
        "generatedCount": generated_count,
        "skippedCount": skipped_count,
        "failedCount": failed_count,
        "results": results,
    }


def evaluate_world_pack(
    *,
    pack_path: str | Path,
    manifest_path: str | Path,
    only_ids: Optional[list[str]] = None,
    limit: Optional[int] = None,
    fail_fast: bool = False,
    model: str = "gemini-3.1-flash-lite-preview",
    system_prompt: str = "",
    comparison_prompt: Optional[str] = None,
) -> dict[str, Any]:
    pack = load_world_pack(pack_path)
    selected = set(only_ids or [])

    results: list[dict[str, Any]] = []
    evaluated_count = 0
    skipped_missing_count = 0
    skipped_no_candidates_count = 0
    failed_count = 0
    processed = 0

    for item in pack["anchors"]:
        anchor_id = item["anchorId"]
        if selected and anchor_id not in selected:
            continue
        if limit is not None and processed >= limit:
            break
        processed += 1

        existing = _get_existing_anchor(manifest_path, anchor_id=anchor_id)
        if existing is None:
            skipped_missing_count += 1
            results.append(
                {
                    "anchorId": anchor_id,
                    "label": item.get("label"),
                    "status": "missing",
                }
            )
            continue

        media_paths = _candidate_media_paths(existing)
        if not media_paths:
            skipped_no_candidates_count += 1
            results.append(
                {
                    "anchorId": anchor_id,
                    "label": existing.get("label") or item.get("label"),
                    "status": "no_candidates",
                }
            )
            continue

        prompt = None
        existing_spec = existing.get("spec")
        if isinstance(existing_spec, dict):
            prompt = _clean_string(existing_spec.get("brief"))
        if not prompt:
            prompt = _clean_string(existing.get("prompt"))
        if not prompt:
            item_spec = item.get("anchorSpec")
            if isinstance(item_spec, dict):
                prompt = _clean_string(item_spec.get("brief"))
        if not prompt:
            prompt = item["prompt"]

        try:
            result = compare_media_candidates(
                prompt=prompt,
                media_paths=media_paths,
                anchor_spec=(existing_spec if isinstance(existing_spec, dict) else item.get("anchorSpec")),
                model=model,
                system_prompt=system_prompt,
                comparison_prompt=comparison_prompt,
            )
            append_manifest_record(
                manifest_path,
                command=result["command"],
                label=(existing.get("label") or item.get("label")),
                anchor_id=anchor_id,
                result=result,
            )
            comparison = result.get("comparison") if isinstance(result.get("comparison"), dict) else {}
            evaluated_count += 1
            results.append(
                {
                    "anchorId": anchor_id,
                    "label": existing.get("label") or item.get("label"),
                    "status": "evaluated",
                    "candidateCount": result.get("candidateCount"),
                    "bestCandidateIndex": comparison.get("bestCandidateIndex"),
                    "ranking": comparison.get("ranking"),
                    "overallNotes": comparison.get("overallNotes"),
                }
            )
        except Exception as exc:
            failed_count += 1
            results.append(
                {
                    "anchorId": anchor_id,
                    "label": existing.get("label") or item.get("label"),
                    "status": "failed",
                    "error": str(exc),
                }
            )
            if fail_fast:
                break

    return {
        "command": "pack-evaluate",
        "packPath": pack["packPath"],
        "packName": pack["name"],
        "manifestPath": str(resolve_path(str(manifest_path))),
        "selectedCount": processed,
        "evaluatedCount": evaluated_count,
        "skippedMissingCount": skipped_missing_count,
        "skippedNoCandidatesCount": skipped_no_candidates_count,
        "failedCount": failed_count,
        "results": results,
    }


def get_world_pack_status(
    *,
    pack_path: str | Path,
    manifest_path: str | Path,
    only_ids: Optional[list[str]] = None,
) -> dict[str, Any]:
    pack = load_world_pack(pack_path)
    selected = set(only_ids or [])

    items: list[dict[str, Any]] = []
    counts = {
        "defined": 0,
        "missing": 0,
        "candidate": 0,
        "review_pending": 0,
        "approved": 0,
        "deferred": 0,
        "rejected": 0,
    }

    for item in pack["anchors"]:
        anchor_id = item["anchorId"]
        if selected and anchor_id not in selected:
            continue

        counts["defined"] += 1
        existing = _get_existing_anchor(manifest_path, anchor_id=anchor_id)
        if existing is None:
            counts["missing"] += 1
            items.append(
                {
                    "anchorId": anchor_id,
                    "label": item.get("label"),
                    "status": "missing",
                    "brief": (item.get("anchorSpec") or {}).get("brief"),
                }
            )
            continue

        status = existing.get("status") if isinstance(existing.get("status"), str) else "unknown"
        if status in counts:
            counts[status] += 1

        approved_image = existing.get("approvedImage") if isinstance(existing.get("approvedImage"), dict) else None
        candidate_status_counts: dict[str, int] = {}
        candidates = existing.get("candidates")
        if isinstance(candidates, list):
            for candidate in candidates:
                if not isinstance(candidate, dict):
                    continue
                candidate_status = candidate.get("status")
                if not isinstance(candidate_status, str) or not candidate_status:
                    candidate_status = "candidate"
                candidate_status_counts[candidate_status] = candidate_status_counts.get(candidate_status, 0) + 1
        items.append(
            {
                "anchorId": anchor_id,
                "label": existing.get("label") or item.get("label"),
                "status": status,
                "brief": ((existing.get("spec") or {}).get("brief") if isinstance(existing.get("spec"), dict) else None),
                "candidateCount": len(existing.get("candidates") or []),
                "candidateStatusCounts": candidate_status_counts,
                "evaluationSummary": _candidate_evaluation_summary(existing.get("candidates")),
                "latestComparison": _latest_comparison_summary(existing),
                "approvedImage": approved_image,
            }
        )

    return {
        "command": "pack-status",
        "packPath": pack["packPath"],
        "packName": pack["name"],
        "manifestPath": str(resolve_path(str(manifest_path))),
        "counts": counts,
        "items": items,
    }
