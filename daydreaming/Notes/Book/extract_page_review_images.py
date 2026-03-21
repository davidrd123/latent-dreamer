#!/usr/bin/env python3

from __future__ import annotations

import argparse
from pathlib import Path
import shutil
import subprocess


PDF_DEFAULT = Path(
    "daydreaming/Notes/Book/"
    "Daydreaming_in_humans_and_machines_a_computer_model_of_the_Erik.pdf"
)
OUTPUT_DEFAULT = Path(
    "daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images"
)


def expand_ranges(*parts: tuple[int, int] | int) -> list[int]:
    pages: list[int] = []
    for part in parts:
        if isinstance(part, int):
            pages.append(part)
        else:
            start, end = part
            pages.extend(range(start, end + 1))
    return pages


PAGE_GROUPS: dict[str, list[int]] = {
    "front-matter-layout": expand_ranges((1, 15), (47, 48)),
    "chapter-1-layout": expand_ranges((21, 46)),
    "figure-pages": expand_ranges(
        54,
        (56, 58),
        (60, 62),
        64,
        78,
        (82, 83),
        (108, 110),
        (112, 114),
        121,
        125,
        147,
        (149, 150),
        159,
        165,
        198,
        200,
        (206, 207),
        212,
        (216, 218),
        221,
        (225, 227),
        229,
        300,
    ),
    "table-pages": expand_ranges(74, 79, 103, 132),
    "chapter-7-layout": expand_ranges((187, 190)),
    "chapter-9-layout": expand_ranges((231, 252)),
    "appendix-a-traces": expand_ranges((323, 370)),
    "appendix-b-layout": expand_ranges((374, 383)),
}


def render_page(pdf: Path, page: int, destination: Path, dpi: int) -> None:
    prefix = destination / f"page-{page:03d}"
    subprocess.run(
        [
            "pdftoppm",
            "-f",
            str(page),
            "-l",
            str(page),
            "-jpeg",
            "-jpegopt",
            "quality=90",
            "-r",
            str(dpi),
            str(pdf),
            str(prefix),
        ],
        check=True,
    )


def write_manifest(output_dir: Path, dpi: int) -> None:
    lines = [
        "# Page Review Images",
        "",
        "This directory contains page images extracted for layout-sensitive review.",
        "",
        f"- Render DPI: `{dpi}`",
        "- Source page numbers are PDF page numbers.",
        "",
        "## Groups",
        "",
    ]
    for name, pages in PAGE_GROUPS.items():
        lines.append(f"- `{name}`: {', '.join(str(page) for page in pages)}")
    lines.append("")
    (output_dir / "README.md").write_text("\n".join(lines), encoding="utf-8")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Extract layout-sensitive review pages as JPEG images."
    )
    parser.add_argument("--pdf", type=Path, default=PDF_DEFAULT)
    parser.add_argument("--output", type=Path, default=OUTPUT_DEFAULT)
    parser.add_argument("--dpi", type=int, default=220)
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    pdf = args.pdf.resolve()
    output = args.output

    if output.exists():
        shutil.rmtree(output)
    output.mkdir(parents=True, exist_ok=True)

    total = 0
    for group_name, pages in PAGE_GROUPS.items():
        group_dir = output / group_name
        group_dir.mkdir(parents=True, exist_ok=True)
        for page in pages:
            render_page(pdf, page, group_dir, args.dpi)
            total += 1

    write_manifest(output, args.dpi)

    print(f"Rendered {total} review pages to {output}")
    for group_name, pages in PAGE_GROUPS.items():
        print(f"{group_name}: {len(pages)} pages")


if __name__ == "__main__":
    main()
