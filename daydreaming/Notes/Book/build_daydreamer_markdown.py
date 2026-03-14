#!/usr/bin/env python3

from __future__ import annotations

import argparse
from dataclasses import dataclass
from pathlib import Path
import re
import shutil
import subprocess
import tempfile
from typing import Iterable


PDF_DEFAULT = Path(
    "daydreaming/Notes/Book/"
    "Daydreaming_in_humans_and_machines_a_computer_model_of_the_Erik.pdf"
)
FRONT_MATTER_CANONICAL = Path(__file__).with_name("front-matter-canonical.md")
OUTPUT_DEFAULT = Path(
    "daydreaming/Notes/Book/daydreaming-in-humans-and-machines"
)

CANONICAL_CHAPTER_TITLES = {
    1: "Introduction",
    2: "Architecture of DAYDREAMER",
    3: "Emotions and Daydreaming",
    4: "Learning through Daydreaming",
    5: "Everyday Creativity in Daydreaming",
    6: "Daydreaming in the Interpersonal Domain",
    7: "Implementation of DAYDREAMER",
    8: "Comparison of Episodic Memory Schemes",
    9: "Review of the Literature on Daydreaming",
    10: "Underpinnings of a Daydreaming Theory",
    11: "Future Work and Conclusions",
}

CANONICAL_APPENDIX_TITLES = {
    "A": "Annotated Traces from DAYDREAMER",
    "B": "English Generation for Daydreaming",
}

SECTION_END_PAGE_OVERRIDES = {
    "15-index": 413,
}


@dataclass
class Section:
    kind: str
    title: str
    start_page: int
    slug: str
    chapter_number: int | None = None
    appendix_letter: str | None = None
    end_page: int | None = None


def slugify(text: str) -> str:
    text = text.lower()
    text = text.replace("&", "and")
    text = re.sub(r"[^a-z0-9]+", "-", text)
    return text.strip("-")


def normalize_line(line: str, *, collapse_spaces: bool = True) -> str:
    line = line.replace("\u00ad", "")
    line = line.replace("—-", "—")
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
    pages = [page for page in text.split("\f")]
    while pages and not pages[-1].strip():
        pages.pop()
    return pages


def next_nonempty(lines: list[str], start: int) -> tuple[int | None, str | None]:
    for idx in range(start, len(lines)):
        stripped = lines[idx].strip()
        if stripped:
            return idx, stripped
    return None, None


def collect_title_lines(lines: list[str], start: int) -> list[str]:
    title_lines: list[str] = []
    idx, current = next_nonempty(lines, start)
    while idx is not None and current is not None:
        if is_probable_body_line(current) and title_lines:
            break
        title_lines.append(current)
        next_idx = idx + 1
        if next_idx >= len(lines) or not lines[next_idx].strip():
            break
        idx, current = next_nonempty(lines, next_idx)
    return title_lines


def is_probable_body_line(line: str) -> bool:
    stripped = line.strip()
    if not stripped:
        return False
    if stripped.endswith((".", "?", "!", ":")):
        return True
    if len(stripped) > 90:
        return True
    words = stripped.split()
    if len(words) > 8:
        return True
    return False


def extract_page_marker(page_text: str) -> Section | None:
    lines = [normalize_line(line) for line in page_text.splitlines()]
    top = lines[:14]

    idx, line = next_nonempty(top, 0)
    if line is None:
        return None

    if line == "REFERENCES":
        return Section(kind="references", title="References", start_page=0, slug="")

    if line == "INDEX":
        return Section(kind="index", title="Index", start_page=0, slug="")

    chapter_match = re.fullmatch(r"Chapter (\d+)", line)
    if chapter_match:
        chapter_number = int(chapter_match.group(1))
        title = " ".join(collect_title_lines(top, idx + 1))
        return Section(
            kind="chapter",
            title=title,
            start_page=0,
            slug="",
            chapter_number=chapter_number,
        )

    if line == "Chapter":
        num_idx, number_line = next_nonempty(top, idx + 1)
        if number_line and number_line.isdigit():
            title = " ".join(collect_title_lines(top, (num_idx or idx) + 1))
            return Section(
                kind="chapter",
                title=title,
                start_page=0,
                slug="",
                chapter_number=int(number_line),
            )

    appendix_match = re.fullmatch(r"Appendix ([A-Z])", line)
    if appendix_match:
        letter = appendix_match.group(1)
        title = " ".join(collect_title_lines(top, idx + 1))
        return Section(
            kind="appendix",
            title=title,
            start_page=0,
            slug="",
            appendix_letter=letter,
        )

    if line == "Appendix":
        letter_idx, letter_line = next_nonempty(top, idx + 1)
        if letter_line and re.fullmatch(r"[A-Z]", letter_line):
            title = " ".join(collect_title_lines(top, (letter_idx or idx) + 1))
            return Section(
                kind="appendix",
                title=title,
                start_page=0,
                slug="",
                appendix_letter=letter_line,
            )

    return None


def build_sections(pages: list[str]) -> list[Section]:
    sections = [
        Section(
            kind="front_matter",
            title="Front Matter",
            start_page=1,
            slug="00-front-matter",
        )
    ]

    for page_number, page_text in enumerate(pages, start=1):
        marker = extract_page_marker(page_text)
        if marker is None:
            continue

        marker.start_page = page_number

        if marker.kind == "chapter":
            marker.title = CANONICAL_CHAPTER_TITLES.get(
                marker.chapter_number or 0,
                marker.title,
            )
            marker.slug = f"{marker.chapter_number:02d}-{slugify(marker.title)}"
        elif marker.kind == "appendix":
            marker.title = CANONICAL_APPENDIX_TITLES.get(
                marker.appendix_letter or "",
                marker.title,
            )
            order = {"A": 13, "B": 14}.get(marker.appendix_letter or "", 99)
            marker.slug = f"{order:02d}-appendix-{(marker.appendix_letter or '').lower()}-{slugify(marker.title)}"
        elif marker.kind == "references":
            marker.slug = "12-references"
        elif marker.kind == "index":
            marker.slug = "15-index"

        last = sections[-1]
        is_duplicate = (
            last.kind == marker.kind
            and last.title == marker.title
            and last.chapter_number == marker.chapter_number
            and last.appendix_letter == marker.appendix_letter
        )
        if not is_duplicate:
            sections.append(marker)

    for current, nxt in zip(sections, sections[1:]):
        current.end_page = nxt.start_page - 1
    sections[-1].end_page = len(pages)
    for section in sections:
        override = SECTION_END_PAGE_OVERRIDES.get(section.slug)
        if override is not None:
            section.end_page = min(section.end_page or override, override)
    return sections


def is_page_number(line: str) -> bool:
    stripped = line.strip()
    return bool(
        re.fullmatch(r"\d+", stripped)
        or re.fullmatch(r"[ivxlcdmIVXLCDM]+", stripped)
        or re.fullmatch(r"[XVI]+[0-9]*", stripped)
    )


def is_header_page_combo(line: str, section: Section) -> bool:
    stripped = line.strip()
    if not stripped:
        return False
    if section.kind == "references":
        return bool(re.fullmatch(r"(?:REFERENCES\s+\d+|\d+\s+REFERENCES)", stripped))
    if section.kind == "index":
        return bool(re.fullmatch(r"(?:INDEX\s+\d+|\d+\s+INDEX)", stripped))
    return False


def looks_like_running_header(line: str) -> bool:
    stripped = line.strip()
    if not stripped:
        return False
    if stripped in {"CONTENTS", "INDEX", "REFERENCES"}:
        return True
    if stripped.startswith(("CHAPTER ", "APPENDIX ")):
        return True
    if "TRACES FROM DAYDREAMER" in stripped:
        return True
    if re.fullmatch(r"[A-Z0-9 .:'*-]{6,}", stripped):
        return True
    if re.fullmatch(r"(?:[A-Z]|\d+)\.\d+(?:\.\d+)?\.?(?: [A-Z0-9 .'-]+)?", stripped):
        return True
    return False


def strip_header_and_footer(
    lines: list[str], section: Section, is_first_page: bool, collapse_spaces: bool = True
) -> list[str]:
    working = [normalize_line(line, collapse_spaces=collapse_spaces) for line in lines]

    while working and not working[0].strip():
        working.pop(0)
    while working and not working[-1].strip():
        working.pop()

    if is_first_page:
        if section.kind == "chapter":
            idx, line = next_nonempty(working, 0)
            if line == "Chapter":
                idx, _ = next_nonempty(working, (idx or 0) + 1)
            elif line and re.fullmatch(r"Chapter \d+", line):
                idx = idx
            if idx is not None:
                body_start = idx + 1
                while body_start < len(working):
                    candidate = working[body_start].strip()
                    if not candidate:
                        body_start += 1
                        continue
                    if is_probable_body_line(candidate):
                        break
                    body_start += 1
                working = working[body_start:]
        elif section.kind == "appendix":
            idx, line = next_nonempty(working, 0)
            if line == "Appendix":
                idx, _ = next_nonempty(working, (idx or 0) + 1)
            elif line and re.fullmatch(r"Appendix [A-Z]", line):
                idx = idx
            if idx is not None:
                body_start = idx + 1
                while body_start < len(working):
                    candidate = working[body_start].strip()
                    if not candidate:
                        body_start += 1
                        continue
                    if is_probable_body_line(candidate):
                        break
                    body_start += 1
                working = working[body_start:]
        elif section.kind in {"references", "index"}:
            if working and (
                looks_like_running_header(working[0])
                or is_header_page_combo(working[0], section)
            ):
                working = working[1:]
            while working and not working[0].strip():
                working = working[1:]
            if working and is_page_number(working[0]):
                working = working[1:]
            while working and not working[0].strip():
                working = working[1:]
            if working and working[0].strip().lower() == section.title.lower():
                working = working[1:]
    else:
        top_nonempty = [line.strip() for line in working[:8] if line.strip()]
        if (
            any(
                is_page_number(line) or is_header_page_combo(line, section)
                for line in top_nonempty[:6]
            )
            and any(
                looks_like_running_header(line) or is_header_page_combo(line, section)
                for line in top_nonempty[:6]
            )
        ):
            while working and (
                not working[0].strip()
                or is_page_number(working[0])
                or looks_like_running_header(working[0])
                or is_header_page_combo(working[0], section)
            ):
                working.pop(0)
        elif (
            len(top_nonempty) >= 2
            and (looks_like_running_header(top_nonempty[0]) or is_header_page_combo(top_nonempty[0], section))
            and (is_page_number(top_nonempty[1]) or is_header_page_combo(top_nonempty[1], section))
        ):
            while working and (
                not working[0].strip()
                or is_page_number(working[0])
                or looks_like_running_header(working[0])
                or is_header_page_combo(working[0], section)
            ):
                working.pop(0)

    while working and is_page_number(working[-1]):
        working.pop()
    while working and not working[-1].strip():
        working.pop()

    return working


def promote_split_headings(lines: list[str]) -> list[str]:
    output: list[str] = []
    i = 0
    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        if stripped.startswith("<!--") or not stripped:
            output.append(line)
            i += 1
            continue

        match = re.fullmatch(r"((?:\d+\.\d+(?:\.\d+)*)|(?:[A-Z]\.\d+(?:\.\d+)*))\.?", stripped)
        if match:
            number = match.group(1)
            title_lines: list[str] = []
            j = i + 1
            blank_streak = 0
            while j < len(lines):
                candidate = lines[j].strip()
                if not candidate:
                    blank_streak += 1
                    if blank_streak > 2 and title_lines:
                        break
                    j += 1
                    continue
                blank_streak = 0
                if candidate.startswith("<!--"):
                    break
                if is_page_number(candidate):
                    break
                if re.fullmatch(r"((?:\d+\.\d+(?:\.\d+)*)|(?:[A-Z]\.\d+(?:\.\d+)*))\.?", candidate):
                    break
                if title_lines and is_probable_body_line(candidate):
                    break
                title_lines.append(candidate)
                if len(title_lines) >= 2:
                    j += 1
                    break
                j += 1
            if title_lines and any(re.search(r"[A-Za-z]", item) for item in title_lines):
                depth = 1 + number.count(".")
                output.append(f'{"#" * depth} {number} {" ".join(title_lines)}')
                i = j
                continue

        inline = re.fullmatch(
            r"((?:\d+\.\d+(?:\.\d+)*)|(?:[A-Z]\.\d+(?:\.\d+)*))\.?\s+(.+)",
            stripped,
        )
        if inline:
            number = inline.group(1)
            title = inline.group(2).strip()
            if re.search(r"[A-Za-z]", title):
                depth = 1 + number.count(".")
                output.append(f'{"#" * depth} {number} {title}')
                i += 1
                continue

        output.append(line)
        i += 1
    return output


def normalize_list_marker(line: str) -> str:
    stripped = line.strip()
    if re.match(r"^[eo]\s+", stripped):
        return "- " + stripped[2:].strip()
    if stripped.startswith("• "):
        return "- " + stripped[2:].strip()
    return stripped


def join_fragments(parts: Iterable[str]) -> str:
    text = ""
    for part in parts:
        chunk = part.strip()
        if not chunk:
            continue
        if not text:
            text = chunk
            continue
        if text.endswith("-") and chunk[:1].islower():
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
        raw = lines[i]
        stripped = raw.strip()
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


def looks_like_reference_start(line: str) -> bool:
    stripped = line.strip()
    if not stripped or stripped.startswith(("<!--", "#", "(")):
        return False
    if stripped.lower() == "and":
        return False
    if not re.search(r"\(\d{4}[a-z]?\)", stripped):
        return False
    prefix = stripped.split("(", 1)[0].strip()
    if "," not in prefix:
        return False
    return bool(re.match(r"^[A-Za-z]", stripped))


def looks_like_author_fragment(line: str) -> bool:
    stripped = line.strip()
    return bool(
        re.fullmatch(
            r"(?:[A-Z][A-Za-z'`\-]+|[Dd]e [A-Z][A-Za-z'`\-]+|[Vv]an [A-Z][A-Za-z'`\-]+|[Vv]on [A-Z][A-Za-z'`\-]+),",
            stripped,
        )
    )


def author_fragment_starts_reference(lines: list[str], start: int) -> bool:
    first_idx = start
    while first_idx < len(lines) and not lines[first_idx].strip():
        first_idx += 1
    if first_idx >= len(lines) or not looks_like_author_fragment(lines[first_idx]):
        return False

    nonempty_followups: list[str] = []
    j = first_idx + 1
    while j < len(lines) and len(nonempty_followups) < 2:
        candidate = lines[j].strip()
        if candidate:
            nonempty_followups.append(candidate)
        j += 1

    return any(re.search(r"\(\d{4}[a-z]?\)", item) for item in nonempty_followups)


def reference_block_incomplete(parts: list[str]) -> bool:
    text = join_fragments(parts)
    if not re.search(r"\(\d{4}[a-z]?\)", text):
        return True
    return text.endswith((",", "&", ":")) or text.lower().endswith(" and")


def render_reference_blocks(lines: list[str]) -> str:
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
            next_stripped = ""
            j = i + 1
            while j < len(lines):
                candidate = lines[j].strip()
                if candidate:
                    next_stripped = candidate
                    break
                j += 1
            if current and not reference_block_incomplete(current):
                if (
                    not next_stripped
                    or next_stripped.startswith(("<!--", "#"))
                    or looks_like_reference_start(next_stripped)
                    or author_fragment_starts_reference(lines, i + 1)
                ):
                    flush()
            i += 1
            continue

        if stripped.startswith(("<!--", "#")):
            flush()
            blocks.append(stripped)
            i += 1
            continue

        if stripped.lower() == "and":
            i += 1
            continue

        if current and not reference_block_incomplete(current) and (
            looks_like_reference_start(stripped)
            or author_fragment_starts_reference(lines, i)
        ):
            flush()

        current.append(stripped)
        i += 1

    flush()
    return "\n\n".join(block for block in blocks if block)


def render_index_section(section: Section, layout_pages: list[str]) -> str:
    lines: list[str] = [title_heading(section), ""]

    for page_number in range(section.start_page, (section.end_page or section.start_page) + 1):
        page = layout_pages[page_number - 1]
        cleaned = strip_header_and_footer(
            page.splitlines(),
            section=section,
            is_first_page=page_number == section.start_page,
            collapse_spaces=False,
        )
        if not cleaned:
            continue

        lines.append(f"<!-- page: {page_number} -->")
        lines.append("```text")
        lines.extend(line.rstrip() for line in cleaned)
        lines.append("```")
        lines.append("")

    return "\n".join(lines).strip() + "\n"


def render_line_preserving_blocks(lines: list[str]) -> str:
    compact: list[str] = []
    previous_blank = False
    for raw in lines:
        stripped = raw.rstrip()
        if not stripped.strip():
            if not previous_blank:
                compact.append("")
            previous_blank = True
            continue
        compact.append(stripped.strip())
        previous_blank = False
    return "\n".join(compact).strip()


def title_heading(section: Section) -> str:
    if section.kind == "chapter":
        return f"# Chapter {section.chapter_number}: {section.title}"
    if section.kind == "appendix":
        return f"# Appendix {section.appendix_letter}: {section.title}"
    return f"# {section.title}"


def render_section(section: Section, pages: list[str]) -> str:
    page_lines: list[str] = [title_heading(section), ""]

    for page_number in range(section.start_page, (section.end_page or section.start_page) + 1):
        page = pages[page_number - 1]
        cleaned = strip_header_and_footer(
            page.splitlines(),
            section=section,
            is_first_page=page_number == section.start_page,
        )
        if not cleaned:
            continue
        page_lines.append(f"<!-- page: {page_number} -->")
        page_lines.extend(cleaned)
        page_lines.append("")

    promoted = promote_split_headings(page_lines)

    if section.kind in {"appendix", "index"}:
        body = render_line_preserving_blocks(promoted)
    elif section.kind == "references":
        body = render_reference_blocks(promoted)
    else:
        normalized = [normalize_list_marker(line) for line in promoted]
        body = render_paragraph_blocks(normalized)

    return body.strip() + "\n"


def render_references_section(section: Section, layout_pages: list[str]) -> str:
    page_lines: list[str] = [title_heading(section), ""]

    for page_number in range(section.start_page, (section.end_page or section.start_page) + 1):
        page = layout_pages[page_number - 1]
        cleaned = strip_header_and_footer(
            page.splitlines(),
            section=section,
            is_first_page=page_number == section.start_page,
            collapse_spaces=False,
        )
        if not cleaned:
            continue
        page_lines.append(f"<!-- page: {page_number} -->")
        page_lines.extend(cleaned)
        page_lines.append("")

    promoted = promote_split_headings(page_lines)
    body = render_reference_blocks(promoted)
    return body.strip() + "\n"


def write_sections(
    output_dir: Path,
    sections: list[Section],
    pages: list[str],
    layout_pages: list[str],
    pdf_path: Path,
) -> None:
    output_dir.mkdir(parents=True, exist_ok=True)

    raw_path = output_dir / "raw.txt"
    raw_path.write_text("\f".join(pages), encoding="utf-8")

    for section in sections:
        if section.slug == "00-front-matter":
            rendered = FRONT_MATTER_CANONICAL.read_text(encoding="utf-8")
        elif section.kind == "references":
            rendered = render_references_section(section, layout_pages)
        elif section.kind == "index":
            rendered = render_index_section(section, layout_pages)
        else:
            rendered = render_section(section, pages)
        (output_dir / f"{section.slug}.md").write_text(rendered, encoding="utf-8")

    generated_files = {f"{section.slug}.md" for section in sections}
    generated_files.update({"README.md", "raw.txt"})
    companion_notes = sorted(
        path.name
        for path in output_dir.glob("*.md")
        if path.name not in generated_files
    )
    companion_dirs = []
    if (output_dir / "page-review-images").exists():
        companion_dirs.append("page-review-images/")

    readme_lines = [
        "# Daydreaming In Humans And Machines",
        "",
        f"Source PDF: `{pdf_path}`",
        "",
        "This directory was generated by `build_daydreamer_markdown.py` using `pdftotext`.",
        "",
        "Notes:",
        "- `raw.txt` is the plain-text extraction with form-feed page breaks preserved.",
        "- Each markdown file includes `<!-- page: N -->` comments keyed to PDF page numbers.",
        "- `00-front-matter.md` has been manually cleaned from page images and OCR cleanup.",
        "- `12-references.md` now uses layout-preserving extraction with a references-specific cleanup pass.",
        "- `15-index.md` now uses layout-preserving extraction with page-anchored preformatted blocks.",
        "- Appendix A preserves more line breaks so the DAYDREAMER traces stay legible.",
        "- Existing companion notes in this directory are preserved across rebuilds and listed below.",
        "",
        "Files:",
    ]
    for section in sections:
        page_span = f"{section.start_page}-{section.end_page}"
        readme_lines.append(f"- `{section.slug}.md` ({page_span})")
    for note_name in companion_notes:
        readme_lines.append(f"- `{note_name}`")
    for dir_name in companion_dirs:
        readme_lines.append(f"- `{dir_name}`")
    (output_dir / "README.md").write_text("\n".join(readme_lines) + "\n", encoding="utf-8")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Convert the DAYDREAMER book PDF into page-anchored markdown."
    )
    parser.add_argument("--pdf", type=Path, default=PDF_DEFAULT)
    parser.add_argument("--output", type=Path, default=OUTPUT_DEFAULT)
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    pages = read_pages(args.pdf)
    layout_pages = read_pages(args.pdf, layout=True)
    sections = build_sections(pages)
    write_sections(args.output, sections, pages, layout_pages, args.pdf)

    print(f"Wrote {len(sections)} markdown sections to {args.output}")
    for section in sections:
        print(f"{section.slug}: pages {section.start_page}-{section.end_page}")


if __name__ == "__main__":
    main()
