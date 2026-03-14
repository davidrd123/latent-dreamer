#!/usr/bin/env python3

from __future__ import annotations

import argparse
import csv
from dataclasses import dataclass
import html
from pathlib import Path
import re
import shutil
import subprocess
import tempfile


ROOT_DEFAULT = Path(
    "daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/sources"
)

GENERATED_FILE_NAMES = {
    "README.md",
    "plain.txt",
    "layout.txt",
    "plain-pages.md",
    "layout-pages.md",
    "source.md",
    "page-classification.csv",
    "raw.html",
}
GENERATED_DIR_NAMES = {
    "page-review-images",
}


@dataclass
class PageRecord:
    number: int
    plain: str
    layout: str
    classification: str
    reason: str


def normalize_line(line: str, *, collapse_spaces: bool = True) -> str:
    line = line.replace("\u00ad", "")
    line = line.replace("ﬁ", "fi").replace("ﬂ", "fl")
    if collapse_spaces:
        line = re.sub(r"[ \t]+", " ", line)
    return line.rstrip()


def read_pages(pdf_path: Path, *, layout: bool = False) -> list[str]:
    with tempfile.NamedTemporaryFile(suffix=".txt", delete=False) as tmp:
        tmp_path = Path(tmp.name)
    try:
        cmd = ["pdftotext"]
        if layout:
            cmd.append("-layout")
        cmd.extend([str(pdf_path), str(tmp_path)])
        subprocess.run(cmd, check=True)
        text = tmp_path.read_text(encoding="utf-8", errors="replace")
    finally:
        tmp_path.unlink(missing_ok=True)

    pages = text.split("\f")
    while pages and not pages[-1].strip():
        pages.pop()
    return pages


def nonempty_lines(text: str, *, collapse_spaces: bool = True) -> list[str]:
    return [
        normalize_line(line, collapse_spaces=collapse_spaces)
        for line in text.splitlines()
        if line.strip()
    ]


def strip_page_number_lines(lines: list[str]) -> list[str]:
    working = list(lines)
    if working and re.fullmatch(r"\d+", working[0].strip()):
        working = working[1:]
    if working and re.fullmatch(r"\d+", working[-1].strip()):
        working = working[:-1]
    return working


def looks_table_like(lines: list[str]) -> bool:
    if not lines:
        return False
    spaced = sum(1 for line in lines if re.search(r"\S {3,}\S", line))
    numeric = sum(1 for line in lines if re.search(r"\d", line))
    return spaced >= 4 and numeric >= 4


def classify_page(page_number: int, total_pages: int, plain: str, layout: str) -> tuple[str, str]:
    plain_lines = strip_page_number_lines(nonempty_lines(plain))
    layout_lines = strip_page_number_lines(nonempty_lines(layout, collapse_spaces=False))

    if not plain_lines and not layout_lines:
        return "non-content-candidate", "no text extracted"

    top_plain = [line.lower().strip() for line in plain_lines[:8]]
    combined_plain = "\n".join(plain_lines).lower()

    if page_number == 1 and ("pdf download" in combined_plain[:1200] or "latest updates:" in combined_plain[:1200]):
        return "non-content-candidate", "publisher wrapper page"

    if page_number == 1:
        return "layout-sensitive", "title/authors/abstract page"

    if any(line in {"references", "bibliography"} for line in top_plain):
        return "layout-sensitive", "references page"

    if page_number == total_pages and len(plain_lines) <= 3:
        return "non-content-candidate", "sparse final page"

    short_plain = 0.0
    if plain_lines:
        short_plain = sum(1 for line in plain_lines if len(line.strip()) < 60) / len(plain_lines)

    spaced_layout = 0.0
    if layout_lines:
        spaced_layout = sum(1 for line in layout_lines if re.search(r"\S {3,}\S", line)) / len(layout_lines)

    if looks_table_like(layout_lines):
        return "layout-sensitive", "tabular spacing heuristic"

    if len(layout_lines) >= 18 and short_plain >= 0.45:
        return "layout-sensitive", "short-line density suggests columns"

    if spaced_layout >= 0.18:
        return "layout-sensitive", "layout spacing suggests columns or figures"

    return "prose", "plain text layer looks usable"


def join_fragments(parts: list[str]) -> str:
    text = ""
    for part in parts:
        chunk = part.strip()
        if not chunk:
            continue
        if not text:
            text = chunk
        elif text.endswith("-") and chunk[:1].islower():
            text = text[:-1] + chunk
        elif text.endswith("-"):
            text += chunk
        else:
            text += " " + chunk
    text = re.sub(r"\s+([,.;:?!])", r"\1", text)
    text = re.sub(r"\(\s+", "(", text)
    text = re.sub(r"\s+\)", ")", text)
    return text.strip()


def render_paragraph_blocks(lines: list[str]) -> str:
    blocks: list[str] = []
    current: list[str] = []
    i = 0

    def flush() -> None:
        nonlocal current
        if current:
            blocks.append(join_fragments(current))
            current = []

    while i < len(lines):
        stripped = lines[i].strip()
        if not stripped:
            flush()
            i += 1
            continue
        if stripped.startswith("<!--") or stripped.startswith("#"):
            flush()
            blocks.append(stripped)
            i += 1
            continue
        if stripped.startswith(("- ", "* ")) or re.match(r"^\d+\.\s+", stripped):
            flush()
            parts = [stripped]
            i += 1
            while i < len(lines):
                continuation = lines[i].strip()
                if not continuation:
                    break
                if continuation.startswith("<!--") or continuation.startswith("#"):
                    break
                if continuation.startswith(("- ", "* ")) or re.match(r"^\d+\.\s+", continuation):
                    break
                parts.append(continuation)
                i += 1
            blocks.append(join_fragments(parts))
            continue
        current.append(stripped)
        i += 1

    flush()
    return "\n\n".join(block for block in blocks if block)


def clean_plain_page(page: str) -> list[str]:
    lines = strip_page_number_lines(nonempty_lines(page))
    return lines


def clean_layout_page(page: str) -> list[str]:
    lines = strip_page_number_lines(nonempty_lines(page, collapse_spaces=False))
    return [line.rstrip() for line in lines]


def render_plain_pages(records: list[PageRecord], title: str) -> str:
    lines = [f"# {title}", "", "Plain `pdftotext` extraction with page markers.", ""]
    for record in records:
        cleaned = clean_plain_page(record.plain)
        if not cleaned:
            continue
        lines.append(f"<!-- page: {record.number} -->")
        lines.extend(cleaned)
        lines.append("")
    return render_paragraph_blocks(lines).strip() + "\n"


def render_layout_pages(records: list[PageRecord], title: str) -> str:
    lines = [f"# {title}", "", "Layout-preserving `pdftotext -layout` extraction.", ""]
    for record in records:
        cleaned = clean_layout_page(record.layout)
        if not cleaned:
            continue
        lines.append(f"<!-- page: {record.number} -->")
        lines.append("```text")
        lines.extend(cleaned)
        lines.append("```")
        lines.append("")
    return "\n".join(lines).strip() + "\n"


def render_source_markdown(records: list[PageRecord], title: str) -> str:
    lines = [f"# {title}", ""]
    for record in records:
        lines.append(f"<!-- page: {record.number} -->")
        if record.classification == "prose":
            cleaned = clean_plain_page(record.plain)
            if cleaned:
                lines.append(render_paragraph_blocks(cleaned))
        elif record.classification == "layout-sensitive":
            lines.append(f"_Layout-sensitive page. Review page image if fidelity matters. Reason: {record.reason}._")
            lines.append("")
            lines.append("```text")
            lines.extend(clean_layout_page(record.layout))
            lines.append("```")
        else:
            lines.append(f"_Non-content candidate. Review page image before omitting. Reason: {record.reason}._")
            layout_lines = clean_layout_page(record.layout)
            if layout_lines:
                lines.append("")
                lines.append("```text")
                lines.extend(layout_lines)
                lines.append("```")
        lines.append("")
    return "\n".join(lines).strip() + "\n"


def render_images(pdf_path: Path, output_dir: Path, page_count: int, dpi: int) -> None:
    images_dir = output_dir / "page-review-images"
    if images_dir.exists():
        shutil.rmtree(images_dir)
    images_dir.mkdir(parents=True, exist_ok=True)

    for page_number in range(1, page_count + 1):
        prefix = images_dir / f"page-{page_number:03d}"
        subprocess.run(
            [
                "pdftoppm",
                "-f",
                str(page_number),
                "-l",
                str(page_number),
                "-jpeg",
                "-jpegopt",
                "quality=88",
                "-r",
                str(dpi),
                str(pdf_path),
                str(prefix),
            ],
            check=True,
        )


def write_classification_csv(records: list[PageRecord], output_dir: Path) -> None:
    with (output_dir / "page-classification.csv").open("w", newline="", encoding="utf-8") as handle:
        writer = csv.writer(handle)
        writer.writerow(["page", "classification", "reason"])
        for record in records:
            writer.writerow([record.number, record.classification, record.reason])


def preserve_companion_files(output_dir: Path) -> Path | None:
    if not output_dir.exists():
        return None

    backup_root = Path(tempfile.mkdtemp(prefix="source-bundle-preserve-"))
    copied = False

    for path in output_dir.rglob("*"):
        if path.is_dir():
            continue
        rel = path.relative_to(output_dir)
        if rel.parts and rel.parts[0] in GENERATED_DIR_NAMES:
            continue
        if path.name in GENERATED_FILE_NAMES:
            continue
        destination = backup_root / rel
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(path, destination)
        copied = True

    if copied:
        return backup_root

    shutil.rmtree(backup_root)
    return None


def restore_companion_files(backup_root: Path | None, output_dir: Path) -> None:
    if backup_root is None or not backup_root.exists():
        return

    for path in backup_root.rglob("*"):
        if path.is_dir():
            continue
        rel = path.relative_to(backup_root)
        destination = output_dir / rel
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(path, destination)

    shutil.rmtree(backup_root)


def extract_html_text(raw_html: str) -> str:
    main_match = re.search(r"<main[^>]*>(.*?)</main>", raw_html, flags=re.IGNORECASE | re.DOTALL)
    text = main_match.group(1) if main_match else raw_html
    text = re.sub(r"<script[^>]*>.*?</script>", "", text, flags=re.IGNORECASE | re.DOTALL)
    text = re.sub(r"<style[^>]*>.*?</style>", "", text, flags=re.IGNORECASE | re.DOTALL)
    text = re.sub(r"<br\s*/?>", "\n", text, flags=re.IGNORECASE)
    text = re.sub(r"</?(?:p|div|section|article|header|footer|aside|main|h[1-6]|li|ul|ol|blockquote|pre|tr)>", "\n", text, flags=re.IGNORECASE)
    text = re.sub(r"</?(?:td|th)>", " ", text, flags=re.IGNORECASE)
    text = re.sub(r"<[^>]+>", " ", text)
    text = html.unescape(text)
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    text = re.sub(r"[ \t]+", " ", text)
    text = re.sub(r"\n{3,}", "\n\n", text)
    lines = [line.strip() for line in text.splitlines()]
    return "\n".join(line for line in lines if line).strip() + "\n"


def write_html_bundle(raw_path: Path, output_root: Path) -> None:
    title = raw_path.stem
    raw_html = raw_path.read_text(encoding="utf-8", errors="replace")
    extracted = extract_html_text(raw_html)

    output_dir = output_root / title
    preserved = preserve_companion_files(output_dir)
    if output_dir.exists():
        shutil.rmtree(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    (output_dir / "raw.html").write_text(raw_html, encoding="utf-8")
    (output_dir / "plain.txt").write_text(extracted, encoding="utf-8")
    (output_dir / "layout.txt").write_text(extracted, encoding="utf-8")

    markdown_lines = [
        f"# {title}",
        "",
        "<!-- page: 1 -->",
        extracted,
    ]
    markdown = "\n".join(markdown_lines).strip() + "\n"
    (output_dir / "plain-pages.md").write_text(markdown, encoding="utf-8")
    (output_dir / "layout-pages.md").write_text(markdown, encoding="utf-8")
    (output_dir / "source.md").write_text(markdown, encoding="utf-8")

    with (output_dir / "page-classification.csv").open("w", newline="", encoding="utf-8") as handle:
        writer = csv.writer(handle)
        writer.writerow(["page", "classification", "reason"])
        writer.writerow([1, "prose", "HTML source converted to plain text"])

    summary = [
        f"# {title}",
        "",
        f"Source: `{raw_path}`",
        "",
        "Generated files:",
        "- `raw.html`: original HTML source.",
        "- `plain.txt`: extracted article text.",
        "- `layout.txt`: same as plain text for non-PDF HTML sources.",
        "- `plain-pages.md`: page-anchored markdown with a single synthetic page.",
        "- `layout-pages.md`: same as plain-pages for non-PDF HTML sources.",
        "- `source.md`: normalized markdown source.",
        "- `page-classification.csv`: synthetic single-page classification.",
        "- Existing companion notes are preserved across rebuilds.",
        "",
        "Classifications:",
        "- `prose`: 1 pages",
    ]
    (output_dir / "README.md").write_text("\n".join(summary) + "\n", encoding="utf-8")
    restore_companion_files(preserved, output_dir)


def write_bundle(pdf_path: Path, output_root: Path, dpi: int) -> None:
    title = pdf_path.stem
    plain_pages = read_pages(pdf_path)
    layout_pages = read_pages(pdf_path, layout=True)
    page_count = max(len(plain_pages), len(layout_pages))

    if len(plain_pages) < page_count:
        plain_pages.extend([""] * (page_count - len(plain_pages)))
    if len(layout_pages) < page_count:
        layout_pages.extend([""] * (page_count - len(layout_pages)))

    records: list[PageRecord] = []
    for page_number in range(1, page_count + 1):
        plain = plain_pages[page_number - 1]
        layout = layout_pages[page_number - 1]
        classification, reason = classify_page(page_number, page_count, plain, layout)
        records.append(
            PageRecord(
                number=page_number,
                plain=plain,
                layout=layout,
                classification=classification,
                reason=reason,
            )
        )

    output_dir = output_root / title
    preserved = preserve_companion_files(output_dir)
    if output_dir.exists():
        shutil.rmtree(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    (output_dir / "plain.txt").write_text("\f".join(plain_pages), encoding="utf-8")
    (output_dir / "layout.txt").write_text("\f".join(layout_pages), encoding="utf-8")
    (output_dir / "plain-pages.md").write_text(render_plain_pages(records, title), encoding="utf-8")
    (output_dir / "layout-pages.md").write_text(render_layout_pages(records, title), encoding="utf-8")
    (output_dir / "source.md").write_text(render_source_markdown(records, title), encoding="utf-8")
    write_classification_csv(records, output_dir)
    render_images(pdf_path, output_dir, page_count, dpi)

    summary = [
        f"# {title}",
        "",
        f"Source: `{pdf_path}`",
        "",
        "Generated files:",
        "- `plain.txt`: plain `pdftotext` extraction.",
        "- `layout.txt`: layout-preserving `pdftotext -layout` extraction.",
        "- `plain-pages.md`: page-anchored plain-text markdown.",
        "- `layout-pages.md`: page-anchored layout markdown.",
        "- `source.md`: conservative hybrid markdown using page classification.",
        "- `page-classification.csv`: per-page heuristic classification.",
        "- `page-review-images/`: JPEG page renders for direct visual review.",
        "- Existing companion notes are preserved across rebuilds.",
        "",
        "Classifications:",
    ]
    counts: dict[str, int] = {}
    for record in records:
        counts[record.classification] = counts.get(record.classification, 0) + 1
    for classification in sorted(counts):
        summary.append(f"- `{classification}`: {counts[classification]} pages")
    (output_dir / "README.md").write_text("\n".join(summary) + "\n", encoding="utf-8")
    restore_companion_files(preserved, output_dir)


def is_pdf_like(path: Path) -> bool:
    try:
        return path.read_bytes().startswith(b"%PDF")
    except OSError:
        return False


def is_html_like(path: Path) -> bool:
    try:
        head = path.read_text(encoding="utf-8", errors="replace")[:4096].lower()
    except OSError:
        return False
    return "<html" in head or "<body" in head or "<!doctype html" in head


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Build page-anchored source bundles for reading-list PDFs."
    )
    parser.add_argument("--root", type=Path, default=ROOT_DEFAULT)
    parser.add_argument("--dpi", type=int, default=180)
    parser.add_argument(
        "--source",
        action="append",
        help="Optional raw source key to convert, e.g. Tanagra or ATMS.",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    root = args.root.resolve()
    raw_dir = root / "raw"
    output_root = root / "markdown"
    output_root.mkdir(parents=True, exist_ok=True)

    if args.source:
        candidates = [raw_dir / f"{key}.raw" for key in args.source]
    else:
        candidates = sorted(path for path in raw_dir.glob("*.raw"))

    converted = 0
    skipped: list[str] = []
    for pdf_path in candidates:
        if not pdf_path.exists():
            skipped.append(f"{pdf_path.name}: missing")
            continue
        if is_pdf_like(pdf_path):
            write_bundle(pdf_path, output_root, args.dpi)
            converted += 1
            print(f"converted {pdf_path.name}")
            continue
        if is_html_like(pdf_path):
            write_html_bundle(pdf_path, output_root)
            converted += 1
            print(f"converted {pdf_path.name}")
            continue
        skipped.append(f"{pdf_path.name}: unsupported format")

    print(f"converted total: {converted}")
    for item in skipped:
        print(f"skipped {item}")


if __name__ == "__main__":
    main()
