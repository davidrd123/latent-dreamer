from __future__ import annotations

import argparse
import json
import re
from pathlib import Path
from typing import Any, Sequence

from .anchor_spec import build_anchor_spec_input
from .brief_bridge import write_world_pack_from_brief
from .manifest import (
    append_manifest_record,
    approve_anchor_candidate,
    get_anchor_record,
    review_anchor_candidate,
    resolve_anchor_source_image,
)
from .paths import repo_root, slugify, write_json
from .service import analyze_media, build_visual_anchor, compare_media_candidates, edit_image, refine_visual_anchor, segment_image
from .world_lint import lint_brief_world
from .world_pack import evaluate_world_pack, get_world_pack_status, run_world_pack


def _add_anchor_spec_arguments(parser: argparse.ArgumentParser) -> None:
    parser.add_argument("--spec", help="Optional JSON file containing an anchorSpec object or a raw spec object.")
    parser.add_argument(
        "--entity-type",
        choices=["character", "location", "prop", "relationship", "moodboard"],
        help="Optional structured entity type for the anchor.",
    )
    parser.add_argument("--name", help="Optional display name for the thing being anchored.")
    parser.add_argument("--brief", help="Optional stable brief stored on the anchor record.")
    parser.add_argument(
        "--style-tag",
        dest="style_tags",
        action="append",
        default=[],
        help="Optional style tag. Repeat for multiple tags.",
    )
    parser.add_argument(
        "--visual-constraint",
        dest="visual_constraints",
        action="append",
        default=[],
        help="Optional must-have visual constraint. Repeat for multiple entries.",
    )
    parser.add_argument(
        "--negative-constraint",
        dest="negative_constraints",
        action="append",
        default=[],
        help="Optional thing to avoid. Repeat for multiple entries.",
    )
    parser.add_argument(
        "--consistency-note",
        dest="consistency_notes",
        action="append",
        default=[],
        help="Optional continuity note. Repeat for multiple entries.",
    )
    parser.add_argument(
        "--source-ref",
        dest="source_refs",
        action="append",
        default=[],
        help="Optional note/path/reference that informed the anchor. Repeat for multiple entries.",
    )


def _anchor_spec_from_args(
    args: argparse.Namespace,
    *,
    base_spec: dict[str, Any] | None = None,
    fallback_brief: str | None = None,
    fallback_name: str | None = None,
) -> dict[str, Any]:
    return build_anchor_spec_input(
        base_spec=base_spec,
        spec_path=getattr(args, "spec", None),
        entity_type=getattr(args, "entity_type", None),
        name=getattr(args, "name", None),
        brief=getattr(args, "brief", None),
        style_tags=getattr(args, "style_tags", None),
        visual_constraints=getattr(args, "visual_constraints", None),
        negative_constraints=getattr(args, "negative_constraints", None),
        consistency_notes=getattr(args, "consistency_notes", None),
        source_refs=getattr(args, "source_refs", None),
        fallback_brief=fallback_brief,
        fallback_name=fallback_name,
    )


def _default_pack_output_path(brief_path: str) -> str:
    source = Path(brief_path).expanduser()
    stem_source = re.sub(r"^\d+-", "", source.stem)
    stem_source = stem_source.replace("brief-", "")
    stem = slugify(stem_source)
    if not stem:
        stem = "world-pack"
    output = repo_root() / "daydreaming" / "out" / "world-packs" / f"{stem}.yaml"
    return str(output)


def _build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="vision",
        description="Multimodal CLI helpers for Daydream world-building.",
    )
    parser.add_argument(
        "--json-out",
        help="Optional path to also write the JSON result to disk.",
    )
    parser.add_argument(
        "--manifest",
        help="Optional manifest JSON path to append a summarized record to.",
    )
    parser.add_argument(
        "--anchor-id",
        help="Optional stable ID for the thing this result belongs to (e.g. character:detective).",
    )
    parser.add_argument(
        "--label",
        help="Optional short label used for filenames and manifest records.",
    )
    parser.add_argument(
        "--pretty",
        action="store_true",
        help="Pretty-print JSON output.",
    )

    subparsers = parser.add_subparsers(dest="command", required=True)

    edit = subparsers.add_parser("edit", help="Generate or edit images with Gemini.")
    edit.add_argument("prompt", help="Prompt or edit instruction.")
    edit.add_argument(
        "--image",
        dest="images",
        action="append",
        default=[],
        help="Optional input image path. Repeat for multiple images.",
    )
    edit.add_argument("--num-outputs", type=int, default=1, help="Number of output images (1-4).")
    edit.add_argument("--output-dir", help="Optional output root directory.")
    edit.add_argument("--model", default="gemini-3.1-flash-image-preview")
    edit.add_argument("--temperature", type=float, default=0.7)
    edit.add_argument("--system-prompt", default="")
    edit.add_argument("--aspect-ratio")
    edit.add_argument("--image-size")

    segment = subparsers.add_parser("segment", help="Segment an image with SAM3 on Replicate.")
    segment.add_argument("image_path", help="Input image path.")
    segment.add_argument("--prompt", default="person")
    segment.add_argument("--output-dir", help="Optional output root directory.")
    segment.add_argument("--threshold", type=float, default=0.5)
    segment.add_argument(
        "--overlay",
        action="store_true",
        help="Also keep an overlay image instead of mask-only output.",
    )

    analyze = subparsers.add_parser("analyze", help="Analyze images or videos with Gemini.")
    analyze.add_argument("prompt", help="Analysis prompt.")
    analyze.add_argument("media", nargs="+", help="One or more image/video paths.")
    analyze.add_argument("--model", default="gemini-3.1-flash-lite-preview")
    analyze.add_argument("--temperature", type=float, default=0.7)
    analyze.add_argument("--system-prompt", default="")
    analyze.add_argument("--max-tokens", type=int, default=65536)
    analyze.add_argument("--fps", type=float)
    analyze.add_argument("--timeout-s", type=int, default=300)

    compare = subparsers.add_parser(
        "compare",
        help="Compare multiple images for the same visual anchor and return a structured ranking.",
    )
    compare.add_argument("prompt", help="Brief or evaluation target the images should satisfy.")
    compare.add_argument("media", nargs="+", help="Two or more image/video paths to compare.")
    compare.add_argument("--model", default="gemini-3.1-flash-lite-preview")
    compare.add_argument("--system-prompt", default="")
    compare.add_argument("--comparison-prompt", help="Optional custom comparison prompt.")
    _add_anchor_spec_arguments(compare)

    anchor = subparsers.add_parser(
        "anchor",
        help="Generate one or more candidate visual anchors and analyze them against the brief.",
    )
    anchor.add_argument("anchor_id", help="Stable ID for the thing being anchored (e.g. character:detective).")
    anchor.add_argument("prompt", help="Brief or visual description to satisfy.")
    anchor.add_argument("--num-outputs", type=int, default=1, help="Number of candidate images (1-4).")
    anchor.add_argument("--output-dir", help="Optional output root directory.")
    anchor.add_argument("--edit-model", default="gemini-3.1-flash-image-preview")
    anchor.add_argument("--analyze-model", default="gemini-3.1-flash-lite-preview")
    anchor.add_argument("--temperature", type=float, default=0.7)
    anchor.add_argument("--system-prompt", default="")
    anchor.add_argument("--analyze-system-prompt", default="")
    anchor.add_argument("--analysis-prompt", help="Optional custom analysis prompt.")
    anchor.add_argument("--aspect-ratio")
    anchor.add_argument("--image-size")
    _add_anchor_spec_arguments(anchor)

    refine = subparsers.add_parser(
        "anchor-refine",
        help="Refine an approved or candidate anchor image with a targeted edit prompt.",
    )
    refine.add_argument("anchor_id", help="Stable ID for the anchor to refine.")
    refine.add_argument("prompt", help="Refinement instruction.")
    refine.add_argument("--candidate-index", type=int, help="Refine a specific candidate instead of the approved image.")
    refine.add_argument("--num-outputs", type=int, default=1, help="Number of refined candidates (1-4).")
    refine.add_argument("--output-dir", help="Optional output root directory.")
    refine.add_argument("--edit-model", default="gemini-3.1-flash-image-preview")
    refine.add_argument("--analyze-model", default="gemini-3.1-flash-lite-preview")
    refine.add_argument("--temperature", type=float, default=0.7)
    refine.add_argument("--system-prompt", default="")
    refine.add_argument("--analyze-system-prompt", default="")
    refine.add_argument("--analysis-prompt", help="Optional custom analysis prompt.")
    refine.add_argument("--aspect-ratio")
    refine.add_argument("--image-size")
    _add_anchor_spec_arguments(refine)

    approve = subparsers.add_parser(
        "approve",
        help="Mark one candidate in a manifest-backed visual anchor as approved.",
    )
    approve.add_argument("anchor_id", help="Stable ID for the anchor to update.")
    approve.add_argument("candidate_index", type=int, help="1-based candidate index to approve.")

    review = subparsers.add_parser(
        "review",
        help="Review one candidate in a manifest-backed visual anchor.",
    )
    review.add_argument("decision", choices=["approve", "reject", "defer"])
    review.add_argument("anchor_id", help="Stable ID for the anchor to update.")
    review.add_argument("candidate_index", type=int, help="1-based candidate index to review.")
    review.add_argument(
        "--reason",
        choices=[
            "strong_match",
            "identity_mismatch",
            "palette_mismatch",
            "constraint_violation",
            "composition_weak",
            "low_quality",
            "duplicate",
            "needs_refinement",
            "keep_for_later",
            "off_brief",
        ],
        help="Optional reason code for the decision.",
    )
    review.add_argument("--note", help="Optional free-form note for the review history.")

    show_anchor = subparsers.add_parser(
        "show-anchor",
        help="Show the current manifest-backed record for one anchor.",
    )
    show_anchor.add_argument("anchor_id", help="Stable ID for the anchor to inspect.")

    pack_run = subparsers.add_parser(
        "pack-run",
        help="Run a tiny world pack of anchor definitions against the manifest.",
    )
    pack_run.add_argument("pack_path", help="Path to a JSON or YAML world-pack file.")
    pack_run.add_argument(
        "--only",
        dest="only_ids",
        action="append",
        default=[],
        help="Run only the specified anchor ID. Repeat for multiple IDs.",
    )
    pack_run.add_argument(
        "--refresh-existing",
        action="store_true",
        help="Regenerate anchors even if they already exist in the manifest.",
    )
    pack_run.add_argument(
        "--fail-fast",
        action="store_true",
        help="Stop at the first failed anchor generation.",
    )
    pack_run.add_argument(
        "--limit",
        type=int,
        help="Optional maximum number of selected anchors to process.",
    )

    pack_status = subparsers.add_parser(
        "pack-status",
        help="Compare a tiny world pack against the current manifest.",
    )
    pack_status.add_argument("pack_path", help="Path to a JSON or YAML world-pack file.")
    pack_status.add_argument(
        "--only",
        dest="only_ids",
        action="append",
        default=[],
        help="Report only the specified anchor ID. Repeat for multiple IDs.",
    )

    pack_evaluate = subparsers.add_parser(
        "pack-evaluate",
        help="Compare manifest-backed candidates for anchors in a tiny world pack.",
    )
    pack_evaluate.add_argument("pack_path", help="Path to a JSON or YAML world-pack file.")
    pack_evaluate.add_argument(
        "--only",
        dest="only_ids",
        action="append",
        default=[],
        help="Evaluate only the specified anchor ID. Repeat for multiple IDs.",
    )
    pack_evaluate.add_argument(
        "--limit",
        type=int,
        help="Optional maximum number of selected anchors to evaluate.",
    )
    pack_evaluate.add_argument(
        "--fail-fast",
        action="store_true",
        help="Stop at the first failed comparison.",
    )
    pack_evaluate.add_argument("--model", default="gemini-3.1-flash-lite-preview")
    pack_evaluate.add_argument("--system-prompt", default="")
    pack_evaluate.add_argument("--comparison-prompt", help="Optional custom comparison prompt.")

    pack_from_brief = subparsers.add_parser(
        "pack-from-brief",
        help="Generate a tiny world-pack file from an experiential-design brief note.",
    )
    pack_from_brief.add_argument("brief_path", help="Path to a brief markdown note.")
    pack_from_brief.add_argument("--output", help="Optional YAML output path for the generated pack.")
    pack_from_brief.add_argument(
        "--no-characters",
        action="store_true",
        help="Omit character anchors from the generated pack.",
    )
    pack_from_brief.add_argument(
        "--no-places",
        action="store_true",
        help="Omit place anchors from the generated pack.",
    )
    pack_from_brief.add_argument(
        "--no-situations",
        action="store_true",
        help="Omit situation/moodboard anchors from the generated pack.",
    )

    lint_brief = subparsers.add_parser(
        "lint-brief",
        help="Lint a brief note for thin-world defects and missing structural links.",
    )
    lint_brief.add_argument("brief_path", help="Path to a brief markdown note.")

    return parser


def _emit_result(args: argparse.Namespace, result: dict[str, Any]) -> None:
    if args.manifest and result.get("command") not in {
        "approve",
        "review",
        "show-anchor",
        "pack-run",
        "pack-status",
        "pack-evaluate",
        "pack-from-brief",
        "lint-brief",
    }:
        append_manifest_record(
            args.manifest,
            command=result["command"],
            label=getattr(args, "label", None),
            anchor_id=getattr(args, "anchor_id", None),
            result=result,
        )
    if args.json_out:
        write_json(Path(args.json_out).expanduser().resolve(), result)
    print(json.dumps(result, indent=2 if args.pretty else None, sort_keys=args.pretty))


def main(argv: Sequence[str] | None = None) -> int:
    parser = _build_parser()
    args = parser.parse_args(argv)

    if args.command == "edit":
        result = edit_image(
            prompt=args.prompt,
            image_paths=args.images,
            num_outputs=args.num_outputs,
            output_dir=args.output_dir,
            label=args.label,
            model=args.model,
            temperature=args.temperature,
            system_prompt=args.system_prompt,
            aspect_ratio=args.aspect_ratio,
            image_size=args.image_size,
        )
    elif args.command == "segment":
        result = segment_image(
            image_path=args.image_path,
            prompt=args.prompt,
            mask_only=(not args.overlay),
            threshold=args.threshold,
            output_dir=args.output_dir,
            label=args.label,
        )
    elif args.command == "analyze":
        result = analyze_media(
            prompt=args.prompt,
            media_paths=args.media,
            model=args.model,
            temperature=args.temperature,
            system_prompt=args.system_prompt,
            max_tokens=args.max_tokens,
            fps=args.fps,
            timeout_s=args.timeout_s,
        )
    elif args.command == "compare":
        base_spec: dict[str, Any] | None = None
        fallback_name = args.label
        if args.manifest and args.anchor_id:
            try:
                record = get_anchor_record(args.manifest, anchor_id=args.anchor_id)
            except RuntimeError as exc:
                if not str(exc).startswith("ANCHOR_NOT_FOUND:"):
                    raise
            else:
                anchor = record.get("anchor")
                if isinstance(anchor, dict):
                    existing_spec = anchor.get("spec")
                    if isinstance(existing_spec, dict):
                        base_spec = existing_spec
                    if not fallback_name and isinstance(anchor.get("label"), str):
                        fallback_name = anchor["label"]
        anchor_spec = _anchor_spec_from_args(
            args,
            base_spec=base_spec,
            fallback_brief=args.prompt,
            fallback_name=fallback_name,
        )
        result = compare_media_candidates(
            prompt=args.prompt,
            media_paths=args.media,
            anchor_spec=anchor_spec,
            model=args.model,
            system_prompt=args.system_prompt,
            comparison_prompt=args.comparison_prompt,
        )
    elif args.command == "anchor":
        anchor_spec = _anchor_spec_from_args(
            args,
            fallback_brief=args.prompt,
            fallback_name=args.label,
        )
        result = build_visual_anchor(
            anchor_id=args.anchor_id,
            prompt=args.prompt,
            anchor_spec=anchor_spec,
            num_outputs=args.num_outputs,
            output_dir=args.output_dir,
            label=args.label,
            edit_model=args.edit_model,
            analyze_model=args.analyze_model,
            temperature=args.temperature,
            system_prompt=args.system_prompt,
            analyze_system_prompt=args.analyze_system_prompt,
            analysis_prompt=args.analysis_prompt,
            aspect_ratio=args.aspect_ratio,
            image_size=args.image_size,
        )
    elif args.command == "anchor-refine":
        if not args.manifest:
            parser.error("--manifest is required for anchor-refine")
            return 2
        source = resolve_anchor_source_image(
            args.manifest,
            anchor_id=args.anchor_id,
            candidate_index=args.candidate_index,
        )
        anchor = source["anchor"]
        anchor_spec = _anchor_spec_from_args(
            args,
            base_spec=(anchor.get("spec") if isinstance(anchor, dict) else None),
            fallback_brief=(
                (anchor.get("spec") or {}).get("brief")
                if isinstance(anchor, dict) and isinstance(anchor.get("spec"), dict)
                else (anchor.get("prompt") if isinstance(anchor, dict) else None)
            ),
            fallback_name=(args.label or (anchor.get("label") if isinstance(anchor, dict) else None)),
        )
        result = refine_visual_anchor(
            anchor_id=args.anchor_id,
            prompt=args.prompt,
            source_image_path=source["sourceImagePath"],
            source_selection=source["sourceSelection"],
            base_prompt=(anchor.get("prompt") if isinstance(anchor, dict) else None),
            anchor_spec=anchor_spec,
            num_outputs=args.num_outputs,
            output_dir=args.output_dir,
            label=(args.label or (anchor.get("label") if isinstance(anchor, dict) else None)),
            edit_model=args.edit_model,
            analyze_model=args.analyze_model,
            temperature=args.temperature,
            system_prompt=args.system_prompt,
            analyze_system_prompt=args.analyze_system_prompt,
            analysis_prompt=args.analysis_prompt,
            aspect_ratio=args.aspect_ratio,
            image_size=args.image_size,
        )
    elif args.command == "approve":
        if not args.manifest:
            parser.error("--manifest is required for approve")
            return 2
        result = approve_anchor_candidate(
            args.manifest,
            anchor_id=args.anchor_id,
            candidate_index=args.candidate_index,
        )
    elif args.command == "review":
        if not args.manifest:
            parser.error("--manifest is required for review")
            return 2
        result = review_anchor_candidate(
            args.manifest,
            anchor_id=args.anchor_id,
            candidate_index=args.candidate_index,
            decision=args.decision,
            reason=args.reason,
            note=args.note,
        )
    elif args.command == "show-anchor":
        if not args.manifest:
            parser.error("--manifest is required for show-anchor")
            return 2
        result = get_anchor_record(
            args.manifest,
            anchor_id=args.anchor_id,
        )
    elif args.command == "pack-run":
        if not args.manifest:
            parser.error("--manifest is required for pack-run")
            return 2
        result = run_world_pack(
            pack_path=args.pack_path,
            manifest_path=args.manifest,
            only_ids=args.only_ids,
            refresh_existing=args.refresh_existing,
            fail_fast=args.fail_fast,
            limit=args.limit,
        )
    elif args.command == "pack-status":
        if not args.manifest:
            parser.error("--manifest is required for pack-status")
            return 2
        result = get_world_pack_status(
            pack_path=args.pack_path,
            manifest_path=args.manifest,
            only_ids=args.only_ids,
        )
    elif args.command == "pack-evaluate":
        if not args.manifest:
            parser.error("--manifest is required for pack-evaluate")
            return 2
        result = evaluate_world_pack(
            pack_path=args.pack_path,
            manifest_path=args.manifest,
            only_ids=args.only_ids,
            limit=args.limit,
            fail_fast=args.fail_fast,
            model=args.model,
            system_prompt=args.system_prompt,
            comparison_prompt=args.comparison_prompt,
        )
    elif args.command == "pack-from-brief":
        output_path = args.output or _default_pack_output_path(args.brief_path)
        result = write_world_pack_from_brief(
            args.brief_path,
            output_path=output_path,
            include_characters=(not args.no_characters),
            include_places=(not args.no_places),
            include_situations=(not args.no_situations),
        )
    elif args.command == "lint-brief":
        result = lint_brief_world(args.brief_path)
    else:  # pragma: no cover
        parser.error(f"Unknown command: {args.command}")
        return 2

    _emit_result(args, result)
    return 0


if __name__ == "__main__":  # pragma: no cover
    raise SystemExit(main())
