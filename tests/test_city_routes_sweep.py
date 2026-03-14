from __future__ import annotations

import importlib.util
import sys
import tempfile
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DAYDREAMING_DIR = ROOT / "daydreaming"
if str(DAYDREAMING_DIR) not in sys.path:
    sys.path.insert(0, str(DAYDREAMING_DIR))

MODULE_PATH = DAYDREAMING_DIR / "city_routes_sweep.py"
SPEC = importlib.util.spec_from_file_location("city_routes_sweep", MODULE_PATH)
assert SPEC is not None
assert SPEC.loader is not None
city_routes_sweep = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = city_routes_sweep
SPEC.loader.exec_module(city_routes_sweep)


class CityRoutesSweepTests(unittest.TestCase):
    def test_run_suite_emits_scheduler_and_feature_results(self) -> None:
        graph = city_routes_sweep.Graph.load(city_routes_sweep.DEFAULT_GRAPH)
        payload = city_routes_sweep.run_suite(
            graph=graph,
            start_node=city_routes_sweep.DEFAULT_START,
            cycles=12,
            seeds=[7],
            presets=[city_routes_sweep.ConductorPreset("neutral", 0.0, 0.0)],
        )

        self.assertEqual(payload["graph_id"], "city_routes_experiment_1_v0")
        self.assertEqual(len(payload["results"]), 2)
        arms = {result["arm"] for result in payload["results"]}
        self.assertEqual(arms, {"scheduler", "feature"})

        feature_result = next(result for result in payload["results"] if result["arm"] == "feature")
        self.assertIn("threshold_present", feature_result["summary"])
        self.assertIn("prepared_event_visits_any", feature_result["summary"])
        self.assertIn("release_moves", feature_result["summary"])

    def test_markdown_report_writes_expected_sections(self) -> None:
        graph = city_routes_sweep.Graph.load(city_routes_sweep.DEFAULT_GRAPH)
        payload = city_routes_sweep.run_suite(
            graph=graph,
            start_node=city_routes_sweep.DEFAULT_START,
            cycles=12,
            seeds=[7],
            presets=[city_routes_sweep.ConductorPreset("neutral", 0.0, 0.0)],
        )

        with tempfile.TemporaryDirectory() as tmpdir:
            output = Path(tmpdir) / "report.md"
            city_routes_sweep.write_markdown_report(payload, output)
            text = output.read_text(encoding="utf-8")
            self.assertIn("# City Routes Robustness Sweep", text)
            self.assertIn("Aggregate Comparison", text)
            self.assertIn("Per-run Summary", text)


if __name__ == "__main__":
    unittest.main()
