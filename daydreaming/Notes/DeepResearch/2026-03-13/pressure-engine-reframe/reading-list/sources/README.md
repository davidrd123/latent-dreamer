# Source Ingest Protocol

Use this workflow for PDF-to-markdown conversion and OCR review in this folder.

## Core Protocol

1. Extract two text views of each PDF.
   - Use plain `pdftotext` for normal prose.
   - Use `pdftotext -layout` for columned, tabular, indented, or preformatted pages.

2. Keep page provenance everywhere.
   - Preserve PDF page numbers.
   - In markdown, insert `<!-- page: N -->` before each page-derived block.

3. Classify pages before cleaning.
   - `prose`: clean from the text layer.
   - `layout-sensitive`: review from page images.
   - `non-content tail`: omit after image check if the page is blank, endpaper, back cover, or scan noise.

4. Use page images whenever structure matters or OCR looks suspicious.
   - Render review pages with `pdftoppm -jpeg`.
   - Read the page image directly for TOC/list pages, tables, diagrams, appendix traces, template pages, and pages where the text layer interleaves columns or corrupts entries.

5. Do not force semantic reconstruction when layout extraction is already the safest representation.
   - For prose chapters, normalize paragraphs and headings.
   - For references, prefer `-layout` plus references-specific block joining.
   - For indexes, prefer page-anchored preformatted blocks over brittle parsing.

6. Treat image-reviewed material as companion notes when full integration is risky.
   - Keep a canonical chapter file.
   - Put manual reconstructions of figures, tables, and traces in separate markdown notes.
   - Preserve those notes across rebuilds.

7. Verify document tails by image, not OCR.
   - Use direct image review to identify blank end matter, endpapers, or back cover pages.

## Local Conventions

- `human-fetched/` holds manually downloaded source PDFs.
- `raw/` holds canonical locally ingested source files.
- `text/` holds extracted text artifacts.
- `source-manifest.md` records current ingest status, not the workflow itself.

## Helper Scripts

- [`build_daydreamer_markdown.py`](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/build_daydreamer_markdown.py)
- [`extract_page_review_images.py`](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/extract_page_review_images.py)
- [`build_source_markdown.py`](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/sources/build_source_markdown.py)

These scripts are the concrete examples for the hybrid extraction workflow described above.
