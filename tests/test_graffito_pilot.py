from __future__ import annotations

import importlib.util
import sys
import tempfile
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
MODULE_PATH = ROOT / "daydreaming" / "graffito_pilot.py"
SPEC = importlib.util.spec_from_file_location("graffito_pilot", MODULE_PATH)
assert SPEC is not None
assert SPEC.loader is not None
graffito_pilot = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = graffito_pilot
SPEC.loader.exec_module(graffito_pilot)


GRAPH_PATH = ROOT / "daydreaming" / "fixtures" / "graffito_v0_scenes_3_4.yaml"


class GraffitoPilotTests(unittest.TestCase):
    def test_graph_fixture_has_edges_and_annotations(self) -> None:
        graph = graffito_pilot.Graph.load(GRAPH_PATH)
        self.assertEqual(graph.graph_id, "graffito_v0_scenes_3_4")
        self.assertGreaterEqual(len(graph.nodes_by_id), 13)
        self.assertGreater(len(graph.edges_from), 0)
        first_node = graph.nodes_by_id["g_s1_n01_feet_pounding"]
        self.assertIn("delta_tension", first_node)
        self.assertIn("delta_energy", first_node)

    def test_baseline_and_pilot_emit_trace_and_debug(self) -> None:
        graph = graffito_pilot.Graph.load(GRAPH_PATH)
        modes = ("baseline", "pilot")
        for mode in modes:
            config = graffito_pilot.PilotConfig(
                mode=mode,
                cycles=8,
                start_node="g_s1_n01_feet_pounding",
                seed=11,
                activation_decay=0.86,
                activation_boost=0.32,
                revisit_penalty=0.55,
                continuity_bias=1.18,
                temperature=1.0,
                conductor_tension_bias=0.0,
                conductor_energy_bias=0.0,
                initial_tension=0.2,
                initial_energy=0.4,
            )
            trace_payload, debug_rows = graffito_pilot.run_pilot(graph, config)
            self.assertEqual(trace_payload["graph_id"], graph.graph_id)
            self.assertEqual(len(trace_payload["trace"]), 8)
            self.assertEqual(len(debug_rows), 8)
            self.assertEqual(trace_payload["trace"][0]["node"], "g_s1_n01_feet_pounding")
            if mode == "pilot":
                self.assertIn("highest_priority", debug_rows[1]["selection"])

    def test_write_outputs_creates_files(self) -> None:
        graph = graffito_pilot.Graph.load(GRAPH_PATH)
        config = graffito_pilot.PilotConfig(
            mode="pilot",
            cycles=5,
            start_node="g_s1_n01_feet_pounding",
            seed=13,
            activation_decay=0.86,
            activation_boost=0.32,
            revisit_penalty=0.55,
            continuity_bias=1.18,
            temperature=1.0,
            conductor_tension_bias=0.0,
            conductor_energy_bias=0.0,
            initial_tension=0.2,
            initial_energy=0.4,
        )
        trace_payload, debug_rows = graffito_pilot.run_pilot(graph, config)

        with tempfile.TemporaryDirectory() as tmpdir:
            trace_path = Path(tmpdir) / "trace.yaml"
            debug_path = Path(tmpdir) / "debug.jsonl"
            graffito_pilot.write_outputs(trace_payload, debug_rows, trace_path, debug_path)
            self.assertTrue(trace_path.exists())
            self.assertTrue(debug_path.exists())
            self.assertIn("graph_id: graffito_v0_scenes_3_4", trace_path.read_text(encoding="utf-8"))
            self.assertGreater(len(debug_path.read_text(encoding="utf-8").splitlines()), 0)

    def test_debug_rows_snapshot_mutable_state(self) -> None:
        graph = graffito_pilot.Graph.load(GRAPH_PATH)
        config = graffito_pilot.PilotConfig(
            mode="pilot",
            cycles=6,
            start_node="g_s1_n01_feet_pounding",
            seed=7,
            activation_decay=0.86,
            activation_boost=0.32,
            revisit_penalty=0.55,
            continuity_bias=1.18,
            temperature=1.0,
            conductor_tension_bias=0.0,
            conductor_energy_bias=0.0,
            initial_tension=0.2,
            initial_energy=0.4,
        )
        _trace_payload, debug_rows = graffito_pilot.run_pilot(graph, config)

        first_row = debug_rows[0]
        self.assertEqual(first_row["selected_node"], "g_s1_n01_feet_pounding")
        self.assertEqual(
            first_row["situation_activation"],
            {
                "s1_street_overwhelm": 0.32,
                "s2_grandmas_apartment": 0.0,
            },
        )

        second_row = debug_rows[1]
        self.assertEqual(
            second_row["last_seen_cycle"],
            {
                "g_s1_n01_feet_pounding": 1,
                "g_s1_n02_hand_over_graffiti": 2,
            },
        )


if __name__ == "__main__":
    unittest.main()
