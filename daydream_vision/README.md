# Daydream Vision CLI

Thin multimodal CLI helpers for world-building and visual-anchor work.

This is the reusable Gemini slice pulled into `latent-dreamer` without
dragging in the full DMPOST AE/Illustrator stack.

## What it does

- `edit`: generate or edit images with Gemini
- `analyze`: analyze images or videos with Gemini
- `compare`: compare multiple images for the same target and return a structured ranking
- `segment`: segment an image with SAM3 on Replicate
- `anchor`: generate candidate visual anchors and auto-analyze them against a brief
- `anchor-refine`: take an approved or candidate anchor image and generate refined candidates from it
- `review`: approve, reject, or defer a manifest-backed candidate with a reason code
- `approve`: mark one manifest-backed candidate as the approved anchor
- `show-anchor`: inspect the current stored state of one anchor
- `pack-from-brief`: derive a tiny world pack from an experiential-design brief note
- `pack-run`: run a tiny world pack of anchor definitions
- `pack-status`: compare a tiny world pack against the current manifest
- `pack-evaluate`: compare current manifest-backed candidates for anchors in a pack
- `lint-brief`: flag thin-world defects in a brief's YAML structures

Anchors can now carry a small structured `anchorSpec` with:

- `entityType`
- `name`
- `brief`
- `styleTags`
- `visualConstraints`
- `negativeConstraints`
- `consistencyNotes`
- `sourceRefs`

The design is intentionally split into:

- `vendor/`: copied provider helpers
- `service.py`: local Daydream-owned service layer
- `cli.py`: thin shell-friendly entrypoint
- `manifest.py`: lightweight record appender for future visual-anchor workflows

That makes it easier to grow this into a fuller `VisualAnchorManifest`
loop later without replacing the CLI.

## Install

Preferred:

```bash
uv sync
```

Fallback:

```bash
python3 -m venv .venv-daydream-vision
.venv-daydream-vision/bin/pip install -r daydream_vision/requirements.txt
```

Required env vars:

- `GEMINI_API_KEY` for `edit` and `analyze`
- `REPLICATE_API_TOKEN` for `segment`

Optional env vars:

- `DAYDREAM_VISION_OUTPUT_DIR`
  Default: `daydreaming/out/visual-anchors/`

## Usage

From the repo root:

```bash
./tools/vision --pretty edit "moody detective portrait in a rain-soaked alley" --label detective-alley
./tools/vision --pretty analyze "Does this match the brief?" path/to/image.png
./tools/vision --pretty compare "single centered glossy red rubber ball on a plain white background" \
  daydreaming/out/visual-anchors/2026-03-14/compare-ball_001.png \
  daydreaming/out/visual-anchors/2026-03-14/compare-ball_002.png
./tools/vision --pretty segment path/to/image.png --prompt "person"
./tools/vision --pretty anchor character:detective "moody detective portrait in a rain-soaked alley" --label detective-alley
./tools/vision --pretty anchor character:detective "three-quarter portrait in the alley" \
  --entity-type character \
  --name "Detective Voss" \
  --brief "a worn private detective in a rain-soaked neo-noir city" \
  --style-tag neo-noir \
  --style-tag cinematic \
  --visual-constraint "fedora" \
  --consistency-note "left cheek scar" \
  --source-ref daydreaming/Notes/worlds/noir-voss.md
./tools/vision --pretty --manifest daydreaming/out/visual-anchor-manifest.json anchor-refine character:detective "make him older, more tired, and slightly more severe"
./tools/vision --pretty --manifest daydreaming/out/visual-anchor-manifest.json review defer character:detective 1 --reason needs_refinement --note "good silhouette, face still too young"
./tools/vision --pretty --manifest daydreaming/out/visual-anchor-manifest.json review approve character:detective 1 --reason strong_match
./tools/vision --pretty --manifest daydreaming/out/visual-anchor-manifest.json approve character:detective 1
./tools/vision --pretty --manifest daydreaming/out/visual-anchor-manifest.json show-anchor character:detective
./tools/vision --pretty pack-from-brief daydreaming/Notes/experiential-design/23-brief-city-night-pursuit.md
./tools/vision --pretty --manifest daydreaming/out/visual-anchor-manifest.json pack-run daydream_vision/examples/tiny-world-pack.yaml
./tools/vision --pretty --manifest daydreaming/out/visual-anchor-manifest.json pack-status daydream_vision/examples/tiny-world-pack.yaml
./tools/vision --pretty --manifest daydreaming/out/visual-anchor-manifest.json pack-evaluate daydream_vision/examples/tiny-world-pack.yaml
./tools/vision --pretty lint-brief daydreaming/Notes/experiential-design/23-brief-city-night-pursuit.md
```

Common flags like `--pretty`, `--manifest`, and `--label` currently need
to appear before the subcommand.

When `uv` is installed, `./tools/vision` prefers `uv run` automatically.

Optional metadata flags:

- `--json-out path/to/result.json`
- `--manifest path/to/visual-anchor-manifest.json`
- `--anchor-id character:detective`
- `--label detective-alley`
- `--spec path/to/anchor-spec.json`
- `--entity-type character|location|prop|relationship|moodboard`
- `--name "Detective Voss"`
- `--brief "stable anchor brief"`
- `--style-tag ...`
- `--visual-constraint ...`
- `--negative-constraint ...`
- `--consistency-note ...`
- `--source-ref ...`

## Notes

- `segment` is the only command that currently depends on Replicate.
- `edit` supports both pure generation and image-edit flows.
- `compare` sends multiple images to Gemini at once and returns a
  structured ranking with per-candidate scores, issues, strengths, and a
  recommended best candidate.
- `anchor` is the first tiny world-building loop: it runs `edit`, then
  automatically runs `analyze` on each generated candidate.
- Anchor evaluation is now stored in both human-readable text and a
  structured object with `match`, `identityFit`, `paletteFit`,
  `constraintViolations`, `keepOrRework`, and `suggestedRefinement`.
- `anchor` and `anchor-refine` both accept a small structured anchor
  spec either from CLI flags or from `--spec path/to/file.json`.
- `--spec` can point either to a raw spec object or to a JSON object
  with an `anchorSpec` field.
- `anchor-refine` uses the approved image by default, or a specific
  candidate with `--candidate-index`, then generates/analyzes revised
  candidates while preserving the previously approved image until you
  explicitly approve a new one.
- `review` is the preferred curation command now. It records a decision
  (`approve`, `reject`, or `defer`) plus an optional reason code and note
  on the candidate itself.
- `pack-run` takes a tiny JSON or YAML file with `defaults` and
  `anchors`, runs each anchor sequentially, and skips anchors that are
  already present in the manifest unless you pass `--refresh-existing`.
- `pack-status` shows whether each pack anchor is `missing`,
  `candidate`, `review_pending`, `approved`, `deferred`, or `rejected`.
  It also summarizes evaluation counts across candidates and surfaces the
  latest persisted comparison result when one exists.
- `pack-evaluate` runs the same multi-image comparison logic over the
  current manifest-backed candidates for each selected anchor in a pack
  and persists the ranking back into the anchor record.
- `lint-brief` is the first thin-world linter. It checks for things like
  situations without places, places with no role, characters that never
  surface, events with no downstream surface, and charged objects that
  never become functional in the brief.
- `pack-from-brief` parses the embedded YAML blocks in notes like
  [23-brief-city-night-pursuit.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/experiential-design/23-brief-city-night-pursuit.md)
  and writes a normal pack file you can immediately run.
- When `--manifest` is supplied, `anchor` now upserts a first-class
  anchor record with candidates, status, and history.
- `approve` promotes one candidate to the anchor's approved image.
- Results are printed as JSON and can also be appended into a lightweight
  manifest for later refinement loops.

Common review reason codes:

- `strong_match`
- `identity_mismatch`
- `palette_mismatch`
- `constraint_violation`
- `composition_weak`
- `low_quality`
- `duplicate`
- `needs_refinement`
- `keep_for_later`
- `off_brief`

## Tiny World Pack Format

See [tiny-world-pack.yaml](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydream_vision/examples/tiny-world-pack.yaml) for a concrete example.

Minimal shape:

```yaml
name: Tiny Noir Test Pack
defaults:
  numOutputs: 1
  styleTags: [cinematic, neo-noir]
anchors:
  - anchorId: character:detective-voss
    label: detective-voss
    prompt: three-quarter portrait in a rain-soaked alley
    entityType: character
    name: Detective Voss
    brief: a worn private detective in a rain-soaked neo-noir city
    visualConstraints: [fedora]
    consistencyNotes: [left cheek scar]
  - anchorId: location:alley-night
    prompt: empty alley at night with wet pavement and deep shadows
    entityType: location
    name: Back Alley
```

Useful pack-run options:

- `--only anchor:id` to run a subset
- `--refresh-existing` to regenerate anchors that already exist
- `--fail-fast` to stop on the first error
- `--limit N` to cap a run while testing

Useful pack-evaluate options:

- `--only anchor:id` to evaluate a subset
- `--fail-fast` to stop on the first comparison failure
- `--limit N` to cap evaluation while testing
- `--comparison-prompt "..."` to override the default ranking rubric

## Pack From Brief

Example:

```bash
./tools/vision --pretty pack-from-brief \
  daydreaming/Notes/experiential-design/23-brief-city-night-pursuit.md \
  --output daydreaming/out/world-packs/city-night-pursuit.yaml
```

By default this includes whatever the brief exposes:

- `characters` from an embedded `world.yaml`, when present
- `places` from an embedded `world.yaml`, when present
- `situations` from either `world.yaml` or a standalone situations block

Useful options:

- `--no-characters`
- `--no-places`
- `--no-situations`
- `--output path/to/pack.yaml`
