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

MODULE_PATH = DAYDREAMING_DIR / "city_routes_targeted_conductor.py"
SPEC = importlib.util.spec_from_file_location("city_routes_targeted_conductor", MODULE_PATH)
assert SPEC is not None
assert SPEC.loader is not None
city_routes_targeted_conductor = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = city_routes_targeted_conductor
SPEC.loader.exec_module(city_routes_targeted_conductor)


class CityRoutesTargetedConductorTests(unittest.TestCase):
    def test_targeted_suite_emits_multiple_presets(self) -> None:
        graph = city_routes_targeted_conductor.Graph.load(
            city_routes_targeted_conductor.DEFAULT_GRAPH
        )
        payload = city_routes_targeted_conductor.run_targeted_suite(
            graph=graph,
            start_node=city_routes_targeted_conductor.DEFAULT_START,
            cycles=12,
            seeds=[7],
        )

        self.assertEqual(payload["graph_id"], "city_routes_experiment_1_v0")
        self.assertEqual(len(payload["results"]), 5)
        preset_names = {result["preset"] for result in payload["results"]}
        self.assertEqual(
            preset_names,
            {"neutral", "spectacle_hold", "threshold_drive", "refuge_hold", "exchange_fast"},
        )

    def test_targeted_markdown_report_writes_expected_sections(self) -> None:
        graph = city_routes_targeted_conductor.Graph.load(
            city_routes_targeted_conductor.DEFAULT_GRAPH
        )
        payload = city_routes_targeted_conductor.run_targeted_suite(
            graph=graph,
            start_node=city_routes_targeted_conductor.DEFAULT_START,
            cycles=12,
            seeds=[7],
        )

        with tempfile.TemporaryDirectory() as tmpdir:
            output = Path(tmpdir) / "report.md"
            city_routes_targeted_conductor.write_markdown(payload, output)
            text = output.read_text(encoding="utf-8")
            self.assertIn("# City Routes Targeted Conductor Sweep", text)
            self.assertIn("Aggregate", text)
            self.assertIn("Per-run Summary", text)


if __name__ == "__main__":
    unittest.main()
