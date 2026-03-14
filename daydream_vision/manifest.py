from __future__ import annotations

import datetime
import json
from pathlib import Path
from typing import Any, Optional

from .anchor_spec import merge_anchor_spec, normalize_anchor_spec
from .paths import resolve_path, write_json


def _utc_now() -> str:
    return datetime.datetime.now(datetime.timezone.utc).isoformat()


def _default_manifest() -> dict[str, Any]:
    now = _utc_now()
    return {
        "schemaVersion": 4,
        "createdAt": now,
        "updatedAt": now,
        "records": [],
        "anchors": [],
    }


def _summarize_result(result: dict[str, Any]) -> dict[str, Any]:
    summary: dict[str, Any] = {}
    for key in (
        "command",
        "model",
        "prompt",
        "basePrompt",
        "output_count",
        "mask_count",
        "media_count",
        "anchorId",
        "candidateCount",
        "sourceSelection",
        "renderPrompt",
        "comparisonPrompt",
    ):
        if key in result:
            summary[key] = result[key]
    comparison = result.get("comparison")
    if isinstance(comparison, dict):
        summary["comparison"] = {
            key: comparison[key]
            for key in ("bestCandidateIndex", "ranking", "overallNotes")
            if key in comparison
        }
    anchor_spec = result.get("anchorSpec")
    if isinstance(anchor_spec, dict):
        summary["anchorSpec"] = {
            key: anchor_spec[key]
            for key in ("entityType", "name", "brief")
            if key in anchor_spec
        }
    outputs = result.get("outputs")
    if isinstance(outputs, list):
        summary["outputs"] = [item.get("path") for item in outputs if isinstance(item, dict)]
    masks = result.get("masks")
    if isinstance(masks, list):
        summary["masks"] = [item.get("path") for item in masks if isinstance(item, dict)]
    text = result.get("text")
    if isinstance(text, str) and text:
        summary["textPreview"] = text[:280]
    return summary


def _anchor_history_brief(anchor: dict[str, Any]) -> Optional[str]:
    spec = anchor.get("spec")
    if isinstance(spec, dict):
        brief = spec.get("brief")
        if isinstance(brief, str) and brief.strip():
            return brief.strip()

    history = anchor.get("history")
    if isinstance(history, list):
        for entry in history:
            if not isinstance(entry, dict):
                continue
            if entry.get("command") != "anchor":
                continue
            prompt = entry.get("prompt")
            if isinstance(prompt, str) and prompt.strip():
                return prompt.strip()

    prompt = anchor.get("prompt")
    if isinstance(prompt, str) and prompt.strip():
        return prompt.strip()
    return None


def _normalize_anchor_record(anchor: dict[str, Any]) -> dict[str, Any]:
    label = anchor.get("label")
    fallback_name = label if isinstance(label, str) else None
    fallback_brief = _anchor_history_brief(anchor)
    anchor["spec"] = merge_anchor_spec(
        anchor.get("spec"),
        None,
        fallback_brief=fallback_brief,
        fallback_name=fallback_name,
    )
    if anchor["spec"].get("brief"):
        anchor["prompt"] = anchor["spec"]["brief"]
    if not isinstance(anchor.get("history"), list):
        anchor["history"] = []
    if not isinstance(anchor.get("candidates"), list):
        anchor["candidates"] = []
    comparisons = anchor.get("comparisons")
    if not isinstance(comparisons, list):
        comparisons = []
        anchor["comparisons"] = comparisons
    latest_comparison = anchor.get("latestComparison")
    if not isinstance(latest_comparison, dict):
        if comparisons and isinstance(comparisons[-1], dict):
            anchor["latestComparison"] = comparisons[-1]
        else:
            anchor["latestComparison"] = None
    return anchor


def _review_list(candidate: dict[str, Any]) -> list[dict[str, Any]]:
    reviews = candidate.get("reviews")
    if not isinstance(reviews, list):
        reviews = []
        candidate["reviews"] = reviews
    return reviews


def _candidate_status(candidate: dict[str, Any]) -> str:
    status = candidate.get("status")
    return status if isinstance(status, str) and status else "candidate"


def _candidate_status_counts(anchor: dict[str, Any]) -> dict[str, int]:
    counts: dict[str, int] = {}
    candidates = anchor.get("candidates")
    if not isinstance(candidates, list):
        return counts
    for candidate in candidates:
        if not isinstance(candidate, dict):
            continue
        status = _candidate_status(candidate)
        counts[status] = counts.get(status, 0) + 1
    return counts


def _recompute_anchor_status(anchor: dict[str, Any]) -> str:
    counts = _candidate_status_counts(anchor)
    has_approved_image = isinstance(anchor.get("approvedImage"), dict)
    has_unreviewed = counts.get("candidate", 0) > 0
    has_deferred = counts.get("deferred", 0) > 0
    has_rejected = counts.get("rejected", 0) > 0

    if has_approved_image:
        if has_unreviewed or has_deferred:
            return "review_pending"
        return "approved"
    if has_unreviewed:
        return "candidate"
    if has_deferred:
        return "deferred"
    if has_rejected:
        return "rejected"
    return "candidate"


def _migrate_manifest(payload: dict[str, Any]) -> dict[str, Any]:
    if payload.get("schemaVersion") == 4 and isinstance(payload.get("anchors"), list):
        if not isinstance(payload.get("records"), list):
            payload["records"] = []
        payload["anchors"] = [
            _normalize_anchor_record(anchor)
            for anchor in payload["anchors"]
            if isinstance(anchor, dict)
        ]
        return payload

    migrated = _default_manifest()
    migrated["createdAt"] = payload.get("createdAt", migrated["createdAt"])
    migrated["updatedAt"] = payload.get("updatedAt", migrated["updatedAt"])
    records = payload.get("records")
    if isinstance(records, list):
        migrated["records"] = records
    anchors = payload.get("anchors")
    if isinstance(anchors, list):
        migrated["anchors"] = [
            _normalize_anchor_record(anchor)
            for anchor in anchors
            if isinstance(anchor, dict)
        ]
    return migrated


def _load_manifest(manifest_path: str | Path) -> tuple[Path, dict[str, Any]]:
    path = resolve_path(str(manifest_path))
    if path.exists():
        payload = json.loads(path.read_text(encoding="utf-8"))
        if not isinstance(payload, dict):
            raise RuntimeError(f"Invalid manifest shape: {path}")
        payload = _migrate_manifest(payload)
    else:
        payload = _default_manifest()
    return path, payload


def _find_anchor(payload: dict[str, Any], anchor_id: str) -> Optional[dict[str, Any]]:
    anchors = payload.get("anchors")
    if not isinstance(anchors, list):
        return None
    for anchor in anchors:
        if isinstance(anchor, dict) and anchor.get("anchorId") == anchor_id:
            return anchor
    return None


def _ensure_anchor(
    payload: dict[str, Any],
    *,
    anchor_id: str,
    label: Optional[str],
    prompt: Optional[str],
    anchor_spec: Optional[dict[str, Any]],
) -> dict[str, Any]:
    anchor = _find_anchor(payload, anchor_id)
    merged_spec = merge_anchor_spec(
        anchor.get("spec") if isinstance(anchor, dict) else None,
        anchor_spec,
        fallback_brief=prompt,
        fallback_name=(label or (anchor.get("label") if isinstance(anchor, dict) else None)),
    )
    if anchor is None:
        anchor = {
            "anchorId": anchor_id,
            "label": label,
            "status": "candidate",
            "prompt": (merged_spec.get("brief") or prompt),
            "spec": merged_spec,
            "approvedCandidateIndex": None,
            "approvedImage": None,
            "candidates": [],
            "history": [],
            "createdAt": _utc_now(),
            "updatedAt": _utc_now(),
        }
        payload["anchors"].append(anchor)
    else:
        if label:
            anchor["label"] = label
        anchor["spec"] = merged_spec
        if merged_spec.get("brief"):
            anchor["prompt"] = merged_spec["brief"]
        elif prompt:
            anchor["prompt"] = prompt
        anchor["updatedAt"] = _utc_now()
    return anchor


def _update_anchor_from_result(
    payload: dict[str, Any],
    *,
    anchor_id: str,
    label: Optional[str],
    result: dict[str, Any],
) -> None:
    prompt = result.get("prompt") if isinstance(result.get("prompt"), str) else None
    anchor_spec = normalize_anchor_spec(result.get("anchorSpec")) if isinstance(result.get("anchorSpec"), dict) else None
    anchor = _ensure_anchor(
        payload,
        anchor_id=anchor_id,
        label=label,
        prompt=prompt,
        anchor_spec=anchor_spec,
    )

    command = result.get("command")
    timestamp = _utc_now()
    anchor["updatedAt"] = timestamp

    if command in {"anchor", "anchor-refine"}:
        candidates = result.get("candidates") or []
        if isinstance(candidates, list):
            had_approved = bool(anchor.get("approvedImage"))
            anchor["status"] = "review_pending" if had_approved else "candidate"
            anchor["approvedCandidateIndex"] = None
            anchor["candidates"] = candidates
            anchor["latestSession"] = {
                "command": command,
                "timestamp": timestamp,
                "outputRoot": result.get("outputRoot"),
                "outputDir": result.get("outputDir"),
                "editModel": result.get("editModel"),
                "analyzeModel": result.get("analyzeModel"),
                "candidateCount": result.get("candidateCount"),
                "sourceSelection": result.get("sourceSelection"),
                "renderPrompt": result.get("renderPrompt"),
            }
            anchor["history"].append(
                {
                    "timestamp": timestamp,
                    "command": command,
                    "prompt": prompt,
                    "basePrompt": result.get("basePrompt"),
                    "candidateCount": result.get("candidateCount"),
                    "sourceSelection": result.get("sourceSelection"),
                    "renderPrompt": result.get("renderPrompt"),
                }
            )
        return

    anchor["history"].append(
        {
            "timestamp": timestamp,
            "command": command,
            "prompt": prompt,
            "summary": _summarize_result(result),
        }
    )
    if command == "compare":
        comparison = result.get("comparison")
        comparison_entry = {
            "timestamp": timestamp,
            "model": result.get("model"),
            "prompt": prompt,
            "comparisonPrompt": result.get("comparisonPrompt"),
            "candidateCount": result.get("candidateCount"),
            "comparison": comparison if isinstance(comparison, dict) else None,
        }
        comparisons = anchor.get("comparisons")
        if not isinstance(comparisons, list):
            comparisons = []
            anchor["comparisons"] = comparisons
        comparisons.append(comparison_entry)
        anchor["latestComparison"] = comparison_entry


def append_manifest_record(
    manifest_path: str | Path,
    *,
    command: str,
    label: Optional[str],
    anchor_id: Optional[str],
    result: dict[str, Any],
) -> Path:
    path, payload = _load_manifest(manifest_path)
    timestamp = _utc_now()
    payload["updatedAt"] = timestamp
    payload["records"].append(
        {
            "timestamp": timestamp,
            "command": command,
            "label": label,
            "anchorId": anchor_id,
            "summary": _summarize_result(result),
            "result": result,
        }
    )

    if anchor_id:
        _update_anchor_from_result(payload, anchor_id=anchor_id, label=label, result=result)

    write_json(path, payload)
    return path


def approve_anchor_candidate(
    manifest_path: str | Path,
    *,
    anchor_id: str,
    candidate_index: int,
) -> dict[str, Any]:
    result = review_anchor_candidate(
        manifest_path,
        anchor_id=anchor_id,
        candidate_index=candidate_index,
        decision="approve",
    )
    result["command"] = "approve"
    return result


def review_anchor_candidate(
    manifest_path: str | Path,
    *,
    anchor_id: str,
    candidate_index: int,
    decision: str,
    reason: Optional[str] = None,
    note: Optional[str] = None,
) -> dict[str, Any]:
    if candidate_index < 1:
        raise RuntimeError("candidate_index must be >= 1")
    if decision not in {"approve", "reject", "defer"}:
        raise RuntimeError("decision must be one of: approve, reject, defer")

    path, payload = _load_manifest(manifest_path)
    anchor = _find_anchor(payload, anchor_id)
    if anchor is None:
        raise RuntimeError(f"ANCHOR_NOT_FOUND: {anchor_id}")

    candidates = anchor.get("candidates")
    if not isinstance(candidates, list) or not candidates:
        raise RuntimeError(f"ANCHOR_HAS_NO_CANDIDATES: {anchor_id}")

    chosen: Optional[dict[str, Any]] = None
    for candidate in candidates:
        if not isinstance(candidate, dict):
            continue
        if candidate.get("candidateIndex") == candidate_index:
            chosen = candidate

    if chosen is None:
        raise RuntimeError(f"CANDIDATE_NOT_FOUND: {anchor_id}#{candidate_index}")

    now = _utc_now()
    cleaned_reason = reason.strip() if isinstance(reason, str) and reason.strip() else None
    cleaned_note = note.strip() if isinstance(note, str) and note.strip() else None

    review_entry: dict[str, Any] = {
        "timestamp": now,
        "decision": decision,
    }
    if cleaned_reason:
        review_entry["reason"] = cleaned_reason
    if cleaned_note:
        review_entry["note"] = cleaned_note

    reviews = _review_list(chosen)
    reviews.append(review_entry)

    if decision == "approve":
        chosen["status"] = "approved"
        for candidate in candidates:
            if not isinstance(candidate, dict):
                continue
            if candidate.get("candidateIndex") == candidate_index:
                continue
            if candidate.get("status") == "approved":
                candidate["status"] = "candidate"
        anchor["approvedCandidateIndex"] = candidate_index
        anchor["approvedImage"] = chosen.get("image")
    elif decision == "reject":
        chosen["status"] = "rejected"
        if anchor.get("approvedCandidateIndex") == candidate_index:
            anchor["approvedCandidateIndex"] = None
            anchor["approvedImage"] = None
    else:
        chosen["status"] = "deferred"
        if anchor.get("approvedCandidateIndex") == candidate_index:
            anchor["approvedCandidateIndex"] = None
            anchor["approvedImage"] = None

    anchor["status"] = _recompute_anchor_status(anchor)
    anchor["updatedAt"] = now
    history = anchor.get("history")
    if not isinstance(history, list):
        history = []
        anchor["history"] = history
    history.append(
        {
            "timestamp": now,
            "command": "review",
            "decision": decision,
            "candidateIndex": candidate_index,
            "reason": cleaned_reason,
            "note": cleaned_note,
        }
    )

    payload["updatedAt"] = now
    payload["records"].append(
        {
            "timestamp": now,
            "command": "review",
            "label": anchor.get("label"),
            "anchorId": anchor_id,
            "summary": {
                "command": "review",
                "decision": decision,
                "anchorId": anchor_id,
                "candidateIndex": candidate_index,
                "reason": cleaned_reason,
                "candidateStatus": chosen.get("status"),
                "approvedCandidateIndex": anchor.get("approvedCandidateIndex"),
                "approvedImage": (
                    chosen.get("image", {}).get("path")
                    if isinstance(chosen.get("image"), dict)
                    else None
                ),
            },
            "result": {
                "command": "review",
                "anchorId": anchor_id,
                "candidateIndex": candidate_index,
                "decision": decision,
                "reason": cleaned_reason,
                "note": cleaned_note,
                "candidateStatus": chosen.get("status"),
                "approvedImage": anchor.get("approvedImage"),
            },
        }
    )

    write_json(path, payload)
    return {
        "command": "review",
        "manifestPath": str(path),
        "anchorId": anchor_id,
        "decision": decision,
        "reason": cleaned_reason,
        "note": cleaned_note,
        "status": anchor["status"],
        "candidateIndex": candidate_index,
        "candidateStatus": chosen.get("status"),
        "approvedCandidateIndex": anchor.get("approvedCandidateIndex"),
        "approvedImage": anchor.get("approvedImage"),
    }


def get_anchor_record(
    manifest_path: str | Path,
    *,
    anchor_id: str,
) -> dict[str, Any]:
    path, payload = _load_manifest(manifest_path)
    anchor = _find_anchor(payload, anchor_id)
    if anchor is None:
        raise RuntimeError(f"ANCHOR_NOT_FOUND: {anchor_id}")
    return {
        "command": "show-anchor",
        "manifestPath": str(path),
        "anchor": anchor,
    }


def resolve_anchor_source_image(
    manifest_path: str | Path,
    *,
    anchor_id: str,
    candidate_index: Optional[int] = None,
) -> dict[str, Any]:
    path, payload = _load_manifest(manifest_path)
    anchor = _find_anchor(payload, anchor_id)
    if anchor is None:
        raise RuntimeError(f"ANCHOR_NOT_FOUND: {anchor_id}")

    candidates = anchor.get("candidates")
    if not isinstance(candidates, list):
        candidates = []

    if candidate_index is not None:
        for candidate in candidates:
            if not isinstance(candidate, dict):
                continue
            if candidate.get("candidateIndex") != candidate_index:
                continue
            image = candidate.get("image")
            if not isinstance(image, dict) or not isinstance(image.get("path"), str):
                break
            return {
                "manifestPath": str(path),
                "anchor": anchor,
                "sourceSelection": f"candidate:{candidate_index}",
                "sourceImagePath": image["path"],
                "candidateIndex": candidate_index,
            }
        raise RuntimeError(f"CANDIDATE_NOT_FOUND: {anchor_id}#{candidate_index}")

    approved = anchor.get("approvedImage")
    if isinstance(approved, dict) and isinstance(approved.get("path"), str):
        return {
            "manifestPath": str(path),
            "anchor": anchor,
            "sourceSelection": "approved",
            "sourceImagePath": approved["path"],
            "candidateIndex": anchor.get("approvedCandidateIndex"),
        }

    for candidate in candidates:
        if not isinstance(candidate, dict):
            continue
        image = candidate.get("image")
        if not isinstance(image, dict) or not isinstance(image.get("path"), str):
            continue
        return {
            "manifestPath": str(path),
            "anchor": anchor,
            "sourceSelection": f"candidate:{candidate.get('candidateIndex')}",
            "sourceImagePath": image["path"],
            "candidateIndex": candidate.get("candidateIndex"),
        }

    raise RuntimeError(f"ANCHOR_HAS_NO_SOURCE_IMAGE: {anchor_id}")
