from __future__ import annotations

import importlib.util
import sys
import tempfile
import types
import unittest
from pathlib import Path

import yaml


ROOT = Path(__file__).resolve().parents[1]
DAYDREAMING_DIR = ROOT / "daydreaming"
if str(DAYDREAMING_DIR) not in sys.path:
    sys.path.insert(0, str(DAYDREAMING_DIR))

MODULE_PATH = DAYDREAMING_DIR / "city_routes_pilot.py"
SPEC = importlib.util.spec_from_file_location("city_routes_pilot", MODULE_PATH)
assert SPEC is not None
assert SPEC.loader is not None
city_routes_pilot = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = city_routes_pilot
SPEC.loader.exec_module(city_routes_pilot)


class CityRoutesPilotTests(unittest.TestCase):
    def _args(self, output_dir: Path) -> types.SimpleNamespace:
        return types.SimpleNamespace(
            cycles=12,
            start_node="c_s1_n01_last_train_doors_shut",
            seed=7,
            output_dir=output_dir,
            prefix="city_routes_test",
            skip_render=False,
            activation_decay=0.86,
            activation_boost=0.32,
            revisit_penalty=0.55,
            continuity_bias=1.18,
            temperature=1.0,
            conductor_tension_bias=0.0,
            conductor_energy_bias=0.0,
            initial_tension=0.2,
            initial_energy=0.4,
            release_start_progress=0.70,
            target_tension_peak=0.90,
            target_tension_end=0.15,
            target_energy_peak=0.75,
            target_energy_end=0.45,
        )

    def test_runner_writes_city_routes_outputs_for_both_arms(self) -> None:
        graph = city_routes_pilot.Graph.load(city_routes_pilot.DEFAULT_GRAPH)
        with tempfile.TemporaryDirectory() as tmpdir:
            args = self._args(Path(tmpdir))
            for mode, label in [("baseline", "baseline"), ("pilot", "scheduler")]:
                trace_path, debug_path, playlist_path = city_routes_pilot.run_arm(
                    graph, args, mode, label
                )
                self.assertTrue(trace_path.exists())
                self.assertTrue(debug_path.exists())
                self.assertTrue(playlist_path.exists())

                trace_payload = yaml.safe_load(trace_path.read_text(encoding="utf-8"))
                self.assertEqual(trace_payload["graph_id"], "city_routes_experiment_1_v0")
                self.assertEqual(len(trace_payload["trace"]), 12)

    def test_debug_rows_include_event_tracking(self) -> None:
        graph = city_routes_pilot.Graph.load(city_routes_pilot.DEFAULT_GRAPH)
        config = city_routes_pilot.config_for("pilot", self._args(Path("/tmp")))
        trace_payload, debug_rows = city_routes_pilot.run_pilot(graph, config)

        self.assertEqual(trace_payload["graph_id"], "city_routes_experiment_1_v0")
        self.assertEqual(debug_rows[0]["event_approach_count"], {"e1_train_missed": 1})
        self.assertIn("event_approach_count", debug_rows[1])


if __name__ == "__main__":
    unittest.main()
