#!/usr/bin/env python3
"""Render a standalone cognitive playback HTML page from a kernel trace JSON."""

from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any

TEMPLATE_PATH = Path(__file__).parent / "cognitive_viz_template.html"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("trace", type=Path, help="Kernel trace JSON path")
    parser.add_argument("--out", type=Path, required=True, help="HTML output path")
    parser.add_argument(
        "--thought-beats",
        type=Path,
        default=None,
        help="Optional runtime thought replay dir or runtime_thought_beats.jsonl file to embed.",
    )
    parser.add_argument(
        "--title",
        default="Cognitive Trace Playback",
        help="Page title for the generated visualizer",
    )
    return parser.parse_args()


def load_trace(path: Path) -> dict[str, Any]:
    with path.open("r", encoding="utf-8") as handle:
        payload = json.load(handle)
    if not isinstance(payload, dict) or "cycles" not in payload:
        raise ValueError(f"{path} is not a valid trace payload")
    return payload


def load_runtime_thought(path: Path | None) -> dict[str, Any]:
    if path is None:
        return {}

    if path.is_dir():
        beats_path = path / "runtime_thought_beats.jsonl"
        summary_path = path / "summary.json"
    else:
        beats_path = path
        summary_path = path.parent / "summary.json"

    if not beats_path.exists():
        raise ValueError(f"{beats_path} does not exist")

    beats: list[dict[str, Any]] = []
    with beats_path.open("r", encoding="utf-8") as handle:
        for raw_line in handle:
            line = raw_line.strip()
            if not line:
                continue
            beats.append(json.loads(line))

    summary: dict[str, Any] = {}
    if summary_path.exists():
        with summary_path.open("r", encoding="utf-8") as handle:
            maybe_summary = json.load(handle)
        if isinstance(maybe_summary, dict):
            summary = maybe_summary

    return {
        "summary": summary,
        "beats": beats,
    }


def escape_json_for_html(payload: Any) -> str:
    raw = json.dumps(payload, ensure_ascii=False)
    return (
        raw.replace("&", "\\u0026")
        .replace("<", "\\u003c")
        .replace(">", "\\u003e")
        .replace("</script", "<\\/script")
    )


def build_html(payload: dict[str, Any], title: str, runtime_thought: dict[str, Any]) -> str:
    template = TEMPLATE_PATH.read_text(encoding="utf-8")
    embedded = escape_json_for_html(payload)
    embedded_runtime_thought = escape_json_for_html(runtime_thought)
    return (
        template.replace("__TITLE__", title)
        .replace("__EMBEDDED_TRACE__", embedded)
        .replace("__EMBEDDED_RUNTIME_THOUGHT__", embedded_runtime_thought)
    )


def main() -> None:
    args = parse_args()
    payload = load_trace(args.trace)
    runtime_thought = load_runtime_thought(args.thought_beats)
    html_text = build_html(payload, args.title, runtime_thought)
    args.out.parent.mkdir(parents=True, exist_ok=True)
    args.out.write_text(html_text, encoding="utf-8")
    print(args.out.resolve())


if __name__ == "__main__":
    main()
