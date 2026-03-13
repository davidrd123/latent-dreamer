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


def escape_json_for_html(payload: dict[str, Any]) -> str:
    raw = json.dumps(payload, ensure_ascii=False)
    return (
        raw.replace("&", "\\u0026")
        .replace("<", "\\u003c")
        .replace(">", "\\u003e")
        .replace("</script", "<\\/script")
    )


def build_html(payload: dict[str, Any], title: str) -> str:
    template = TEMPLATE_PATH.read_text(encoding="utf-8")
    embedded = escape_json_for_html(payload)
    return (
        template.replace("__TITLE__", title)
        .replace("__EMBEDDED_TRACE__", embedded)
    )


def main() -> None:
    args = parse_args()
    payload = load_trace(args.trace)
    html_text = build_html(payload, args.title)
    args.out.parent.mkdir(parents=True, exist_ok=True)
    args.out.write_text(html_text, encoding="utf-8")
    print(args.out.resolve())


if __name__ == "__main__":
    main()
