from __future__ import annotations

import json
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

from PIL import Image

from daydream_vision.brief_bridge import build_world_pack_from_brief
from daydream_vision.manifest import append_manifest_record, get_anchor_record, review_anchor_candidate
from daydream_vision.service import FAL_QWEN_MODEL, GRAFFITO_QWEN_MODEL, apply_edit_presets, edit_image
from daydream_vision.world_lint import lint_brief_world
from daydream_vision.world_pack import evaluate_world_pack, get_world_pack_status


def _candidate(candidate_index: int, path: str, *, status: str = "candidate") -> dict[str, object]:
    return {
        "candidateIndex": candidate_index,
        "status": status,
        "image": {
            "path": path,
            "relativePath": Path(path).name,
            "width": 1024,
            "height": 1024,
        },
        "analysis": {
            "model": "test-model",
            "prompt": "test prompt",
            "text": "{}",
            "structured": {
                "description": "test candidate",
                "match": "strong",
                "identityFit": "strong",
                "paletteFit": "strong",
                "constraintViolations": [],
                "keepOrRework": "keep",
                "suggestedRefinement": "",
                "notes": "",
            },
        },
    }


def _anchor_result(*, command: str, prompt: str, candidate_total: int) -> dict[str, object]:
    candidates = [
        _candidate(index, f"/tmp/test-anchor-{command}-{index}.png")
        for index in range(1, candidate_total + 1)
    ]
    return {
        "command": command,
        "prompt": prompt,
        "anchorSpec": {
            "entityType": "prop",
            "name": "Test Anchor",
            "brief": prompt,
        },
        "candidateCount": candidate_total,
        "candidates": candidates,
        "outputs": [candidate["image"] for candidate in candidates],
        "outputRoot": "/tmp",
        "outputDir": "/tmp",
        "editModel": "test-edit-model",
        "analyzeModel": "test-analyze-model",
        "renderPrompt": prompt,
    }


def _compare_result(prompt: str) -> dict[str, object]:
    return {
        "command": "compare",
        "prompt": prompt,
        "anchorSpec": {
            "entityType": "prop",
            "name": "Test Anchor",
            "brief": prompt,
        },
        "model": "test-compare-model",
        "comparisonPrompt": "compare these candidates",
        "candidateCount": 2,
        "comparison": {
            "bestCandidateIndex": 2,
            "ranking": [2, 1],
            "overallNotes": "candidate 2 is stronger",
            "candidates": [
                {
                    "candidateIndex": 1,
                    "description": "candidate one",
                    "match": "partial",
                    "identityFit": "partial",
                    "paletteFit": "strong",
                    "constraintViolations": ["visible seam"],
                    "keepOrRework": "refine",
                    "suggestedRefinement": "remove the seam",
                    "strengths": ["centered"],
                    "issues": ["visible seam"],
                    "overallScore": 72,
                    "notes": "close but off",
                },
                {
                    "candidateIndex": 2,
                    "description": "candidate two",
                    "match": "strong",
                    "identityFit": "strong",
                    "paletteFit": "strong",
                    "constraintViolations": [],
                    "keepOrRework": "keep",
                    "suggestedRefinement": "",
                    "strengths": ["clean", "glossy"],
                    "issues": [],
                    "overallScore": 94,
                    "notes": "best fit",
                },
            ],
        },
        "candidates": [
            {
                "candidateIndex": 1,
                "media": {"path": "/tmp/test-anchor-anchor-1.png"},
            },
            {
                "candidateIndex": 2,
                "media": {"path": "/tmp/test-anchor-anchor-2.png"},
            },
        ],
        "text": "{}",
    }


class DaydreamVisionManifestTests(unittest.TestCase):
    def setUp(self) -> None:
        self.temp_dir = tempfile.TemporaryDirectory()
        self.manifest_path = Path(self.temp_dir.name) / "manifest.json"

    def tearDown(self) -> None:
        self.temp_dir.cleanup()

    def test_review_approve_then_refine_moves_anchor_to_review_pending(self) -> None:
        prompt = "single centered glossy red rubber ball on a plain white background"
        append_manifest_record(
            self.manifest_path,
            command="anchor",
            label="compare-ball",
            anchor_id="prop:compare-ball",
            result=_anchor_result(command="anchor", prompt=prompt, candidate_total=2),
        )

        approve_result = review_anchor_candidate(
            self.manifest_path,
            anchor_id="prop:compare-ball",
            candidate_index=1,
            decision="approve",
            reason="strong_match",
        )
        self.assertEqual(approve_result["status"], "review_pending")
        self.assertEqual(approve_result["approvedCandidateIndex"], 1)

        approved_anchor = get_anchor_record(self.manifest_path, anchor_id="prop:compare-ball")["anchor"]
        self.assertEqual(approved_anchor["status"], "review_pending")
        self.assertEqual(approved_anchor["approvedCandidateIndex"], 1)
        self.assertEqual(approved_anchor["approvedImage"]["path"], "/tmp/test-anchor-anchor-1.png")

        append_manifest_record(
            self.manifest_path,
            command="anchor-refine",
            label="compare-ball",
            anchor_id="prop:compare-ball",
            result=_anchor_result(command="anchor-refine", prompt=prompt, candidate_total=1),
        )

        refined_anchor = get_anchor_record(self.manifest_path, anchor_id="prop:compare-ball")["anchor"]
        self.assertEqual(refined_anchor["status"], "review_pending")
        self.assertEqual(refined_anchor["approvedImage"]["path"], "/tmp/test-anchor-anchor-1.png")
        self.assertIsNone(refined_anchor["approvedCandidateIndex"])
        self.assertEqual(len(refined_anchor["candidates"]), 1)

    def test_manifest_migrates_schema_3_anchor_records(self) -> None:
        legacy_payload = {
            "schemaVersion": 3,
            "createdAt": "2026-03-14T00:00:00+00:00",
            "updatedAt": "2026-03-14T00:00:00+00:00",
            "records": [],
            "anchors": [
                {
                    "anchorId": "prop:legacy-ball",
                    "label": "legacy-ball",
                    "prompt": "legacy prompt",
                    "status": "candidate",
                }
            ],
        }
        self.manifest_path.write_text(json.dumps(legacy_payload), encoding="utf-8")

        anchor = get_anchor_record(self.manifest_path, anchor_id="prop:legacy-ball")["anchor"]
        self.assertEqual(anchor["spec"]["brief"], "legacy prompt")
        self.assertEqual(anchor["comparisons"], [])
        self.assertIsNone(anchor["latestComparison"])
        self.assertEqual(anchor["candidates"], [])

    def test_review_reject_sets_anchor_rejected(self) -> None:
        prompt = "single centered glossy red rubber ball on a plain white background"
        append_manifest_record(
            self.manifest_path,
            command="anchor",
            label="compare-ball",
            anchor_id="prop:compare-ball",
            result=_anchor_result(command="anchor", prompt=prompt, candidate_total=1),
        )

        result = review_anchor_candidate(
            self.manifest_path,
            anchor_id="prop:compare-ball",
            candidate_index=1,
            decision="reject",
            reason="off_brief",
            note="shape is wrong",
        )
        self.assertEqual(result["status"], "rejected")
        self.assertEqual(result["candidateStatus"], "rejected")

        anchor = get_anchor_record(self.manifest_path, anchor_id="prop:compare-ball")["anchor"]
        self.assertEqual(anchor["status"], "rejected")
        self.assertEqual(anchor["history"][-1]["reason"], "off_brief")
        self.assertEqual(anchor["history"][-1]["note"], "shape is wrong")


class DaydreamVisionProviderTests(unittest.TestCase):
    def test_graffito_preset_can_target_fal(self) -> None:
        model, lora = apply_edit_presets(
            graffito=True,
            provider="fal",
            model="gemini-3.1-flash-image-preview",
            lora_weights=None,
        )
        self.assertEqual(model, FAL_QWEN_MODEL)
        self.assertIn("graffito_synthetic_qwen", lora or "")

    def test_graffito_preset_defaults_to_replicate_qwen(self) -> None:
        model, lora = apply_edit_presets(
            graffito=True,
            provider="auto",
            model="gemini-3.1-flash-image-preview",
            lora_weights=None,
        )
        self.assertEqual(model, GRAFFITO_QWEN_MODEL)
        self.assertIn("graffito_synthetic_qwen", lora or "")

    def test_edit_image_routes_fal_qwen_and_renames_outputs(self) -> None:
        with tempfile.TemporaryDirectory() as temp_dir:
            out_dir = Path(temp_dir)
            raw_dir = out_dir / "2026-03-25"
            raw_dir.mkdir(parents=True, exist_ok=True)
            raw_file = raw_dir / "test-raw_0.png"
            Image.new("RGB", (32, 24), (10, 20, 30)).save(raw_file)

            with patch(
                "daydream_vision.service.fal_min.generate",
                return_value={
                    "success": True,
                    "outputs": [
                        {
                            "path": str(raw_file),
                            "width": 32,
                            "height": 24,
                            "content_type": "image/png",
                        }
                    ],
                    "metrics": {"timings": {"inference": 1.0}},
                },
            ) as mock_generate:
                result = edit_image(
                    prompt="Tony at the mural wall",
                    model="fal-ai/qwen-image",
                    provider="fal",
                    output_dir=str(out_dir),
                    label="fal-test",
                    lora_weights="https://example.com/graffito.safetensors",
                )

            self.assertEqual(result["provider"], "fal")
            self.assertEqual(result["output_count"], 1)
            self.assertEqual(Path(result["outputs"][0]["path"]).suffix, ".png")
            self.assertTrue(Path(result["outputs"][0]["path"]).exists())
            called_params = mock_generate.call_args.kwargs["params"]
            self.assertEqual(called_params["output_format"], "png")
            self.assertEqual(called_params["loras"][0]["path"], "https://example.com/graffito.safetensors")


class DaydreamVisionPackTests(unittest.TestCase):
    def setUp(self) -> None:
        self.temp_dir = tempfile.TemporaryDirectory()
        self.manifest_path = Path(self.temp_dir.name) / "manifest.json"
        self.pack_path = Path(self.temp_dir.name) / "pack.json"
        prompt = "single centered glossy red rubber ball on a plain white background"
        append_manifest_record(
            self.manifest_path,
            command="anchor",
            label="compare-ball",
            anchor_id="prop:compare-ball",
            result=_anchor_result(command="anchor", prompt=prompt, candidate_total=2),
        )
        self.pack_path.write_text(
            json.dumps(
                {
                    "name": "compare-pack",
                    "anchors": [
                        {
                            "anchorId": "prop:compare-ball",
                            "label": "compare-ball",
                            "prompt": prompt,
                            "entityType": "prop",
                            "brief": prompt,
                        }
                    ],
                }
            ),
            encoding="utf-8",
        )

    def tearDown(self) -> None:
        self.temp_dir.cleanup()

    def test_pack_evaluate_persists_latest_comparison(self) -> None:
        prompt = "single centered glossy red rubber ball on a plain white background"
        with patch("daydream_vision.world_pack.compare_media_candidates", return_value=_compare_result(prompt)):
            result = evaluate_world_pack(
                pack_path=self.pack_path,
                manifest_path=self.manifest_path,
            )

        self.assertEqual(result["command"], "pack-evaluate")
        self.assertEqual(result["evaluatedCount"], 1)
        self.assertEqual(result["results"][0]["bestCandidateIndex"], 2)

        anchor = get_anchor_record(self.manifest_path, anchor_id="prop:compare-ball")["anchor"]
        latest = anchor["latestComparison"]
        self.assertEqual(latest["comparison"]["bestCandidateIndex"], 2)
        self.assertEqual(latest["comparison"]["ranking"], [2, 1])

        status = get_world_pack_status(
            pack_path=self.pack_path,
            manifest_path=self.manifest_path,
        )
        self.assertEqual(status["items"][0]["latestComparison"]["bestCandidateIndex"], 2)
        self.assertEqual(status["items"][0]["latestComparison"]["ranking"], [2, 1])


class DaydreamVisionBriefBridgeTests(unittest.TestCase):
    def setUp(self) -> None:
        self.temp_dir = tempfile.TemporaryDirectory()
        self.brief_path = Path(self.temp_dir.name) / "brief.md"
        self.brief_path.write_text(
            """# Test Brief

## `creative_brief.yaml`

```yaml
title: "The City Routes Around You"
concept: >
  A courier is carrying something small but decisive across the city at
  night while every district reads the courier differently.
charged_objects:
  - the silver package case (small enough to carry under one arm)
  - the apartment key on red string (temporary refuge made portable)
```

## `style_extensions.yaml`

```yaml
negative_space:
  - no open daylight
```

## Recommended `world.yaml`

```yaml
characters:
  - id: courier
    name: The Courier
  - id: hostess
    name: The Hostess

places:
  - id: apartment
    description: "The borrowed kitchen refuge where the package can be set down exactly once."
  - id: platform
    description: "The elevated train platform where missed timing becomes route destiny."

situations:
  - id: s1_the_run
    description: >
      The courier is in motion and must stay in motion. Timing is the only
      thing that still feels objective.
    place: platform
    indices:
      - urgency
      - route
  - id: s2_false_refuge
    description: >
      There is a temporary interior where the package can be set down,
      a drink can be poured, and the route can be reconsidered.
    place: apartment
    indices:
      - refuge
      - shelter
```
""",
            encoding="utf-8",
        )

    def tearDown(self) -> None:
        self.temp_dir.cleanup()

    def test_character_anchor_enrichment_uses_brief_mentions_and_objects(self) -> None:
        pack = build_world_pack_from_brief(self.brief_path)
        courier = next(item for item in pack["anchors"] if item["anchorId"] == "character:courier")

        self.assertIn("carrying something small but decisive", courier["prompt"])
        self.assertIn("carrying something small but decisive", courier["brief"])
        self.assertTrue(
            any("silver package case" in item for item in courier["visualConstraints"]),
            courier,
        )
        self.assertTrue(
            any("associated place:" in note for note in courier.get("consistencyNotes", [])),
            courier,
        )

    def test_character_anchor_falls_back_to_role_hints_when_brief_is_thin(self) -> None:
        pack = build_world_pack_from_brief(self.brief_path)
        hostess = next(item for item in pack["anchors"] if item["anchorId"] == "character:hostess")

        self.assertIn("temporary interior refuge", hostess["prompt"])
        self.assertIn("protective interior figure", hostess["brief"])
        self.assertIn("borrowed kitchen tungsten pool", hostess["visualConstraints"])


class DaydreamVisionWorldLintTests(unittest.TestCase):
    def setUp(self) -> None:
        self.temp_dir = tempfile.TemporaryDirectory()
        self.brief_path = Path(self.temp_dir.name) / "lint-brief.md"
        self.brief_path.write_text(
            """# Lint Brief

## `creative_brief.yaml`

```yaml
title: "Thin World"
concept: >
  A courier crosses a city with a package while the route keeps changing.
charged_objects:
  - the silver package case (central to the mission)
  - the bridge wristband (proof of an earlier route)
```

## `style_extensions.yaml`

```yaml
negative_space:
  - no daylight
```

## Recommended `world.yaml`

```yaml
characters:
  - id: courier
    name: The Courier
  - id: hostess
    name: The Hostess

places:
  - id: platform
    description: "The train platform."
  - id: warehouse
    description: "The warehouse."
  - id: club
    description: "The club."

events:
  - id: e1_train_missed
    description: "The last train doors close before the courier reaches them."
  - id: e2_dead_drop
    description: "A dead drop waits at the warehouse loading bay."

situations:
  - id: s1_run
    description: >
      The courier is in motion and must stay in motion.
    place: platform
    indices:
      - urgency
      - route
  - id: s2_refuge
    description: >
      There is a temporary interior where the route can be reconsidered.
    indices:
      - shelter
  - id: s3_exchange
    description: >
      The loading bay waits for a transfer that never touches the main route.
    place: warehouse
    indices:
      - exchange
      - loading_bay
```
""",
            encoding="utf-8",
        )

    def tearDown(self) -> None:
        self.temp_dir.cleanup()

    def test_lint_brief_reports_expected_structural_findings(self) -> None:
        result = lint_brief_world(self.brief_path)
        codes = {finding["code"] for finding in result["findings"]}

        self.assertIn("situation_without_place", codes)
        self.assertIn("place_without_role", codes)
        self.assertIn("character_without_surface", codes)
        self.assertIn("charged_object_without_function", codes)
        self.assertIn("disconnected_subgraph", codes)


if __name__ == "__main__":  # pragma: no cover
    unittest.main()
