from __future__ import annotations

import unittest
from pathlib import Path

import networkx as nx
import yaml


ROOT = Path(__file__).resolve().parents[1]
FIXTURE = ROOT / "daydreaming" / "fixtures" / "city_routes_experiment_1_v0.yaml"
START_NODE = "c_s1_n01_last_train_doors_shut"


class CityRoutesFixtureTests(unittest.TestCase):
    def _load_fixture(self) -> dict:
        return yaml.safe_load(FIXTURE.read_text(encoding="utf-8"))

    def _load_graph(self) -> tuple[dict, nx.DiGraph]:
        data = self._load_fixture()
        graph = nx.DiGraph()
        for node in data["nodes"]:
            graph.add_node(node["id"], **node)
        for edge in data["edges"]:
            graph.add_edge(edge["from"], edge["to"])
        return data, graph

    def test_fixture_matches_experiment_targets(self) -> None:
        data = self._load_fixture()

        self.assertEqual(data["graph_id"], "city_routes_experiment_1_v0")
        self.assertGreaterEqual(len(data["nodes"]), 28)
        self.assertLessEqual(len(data["nodes"]), 32)
        self.assertEqual(len(data["situations"]), 6)
        self.assertEqual(len(data["events"]), 4)
        self.assertGreaterEqual(len(data["edges"]), 30)
        self.assertLessEqual(len(data["edges"]), 40)

    def test_nodes_have_required_scheduler_annotations(self) -> None:
        data = self._load_fixture()
        required = {
            "availability_test",
            "priority_tier",
            "delta_tension",
            "delta_energy",
            "origin_pressure_refs",
            "setup_refs",
            "payoff_refs",
            "line_id",
            "subplot_id",
            "situation_id",
            "situation_ids",
            "pressure_tags",
            "option_effect",
            "importance",
            "resolution_path_count",
        }
        for node in data["nodes"]:
            self.assertTrue(required.issubset(node.keys()), node["id"])

    def test_ref_namespaces_are_mechanically_resolvable(self) -> None:
        data = self._load_fixture()
        allowed_setup_payoff = (
            {event["id"] for event in data["events"]}
            | {situation["id"] for situation in data["situations"]}
            | {marker["id"] for marker in data["reference_markers"]}
        )
        allowed_pressure_refs = {pressure["id"] for pressure in data["origin_pressures"]}
        allowed_option_effects = set(data["ref_resolution"]["option_effect_vocabulary"])

        multi_pressure_nodes = 0
        for node in data["nodes"]:
            self.assertIn(node["option_effect"], allowed_option_effects, node["id"])
            self.assertTrue(node["origin_pressure_refs"], node["id"])
            for pressure_ref in node["origin_pressure_refs"]:
                self.assertIn(pressure_ref, allowed_pressure_refs, node["id"])
            for pressure_ref in node.get("supporting_pressure_refs", []):
                self.assertIn(pressure_ref, allowed_pressure_refs, node["id"])
            if node.get("supporting_pressure_refs"):
                multi_pressure_nodes += 1
            for field in ("setup_refs", "payoff_refs"):
                for ref in node.get(field, []):
                    self.assertIn(ref, allowed_setup_payoff, (node["id"], field, ref))

        self.assertGreaterEqual(multi_pressure_nodes, 10)

    def test_graph_is_reachable_and_has_no_sinks(self) -> None:
        _data, graph = self._load_graph()
        reachable = nx.descendants(graph, START_NODE) | {START_NODE}
        self.assertEqual(len(reachable), len(graph.nodes))
        sinks = [node_id for node_id in graph.nodes if graph.out_degree(node_id) == 0]
        self.assertEqual(sinks, [])

    def test_graph_has_cycles_and_attractor_nodes(self) -> None:
        _data, graph = self._load_graph()
        cycles = list(nx.simple_cycles(graph))
        self.assertGreaterEqual(len(cycles), 4)

        in_degrees = [graph.in_degree(node_id) for node_id in graph.nodes]
        mean_in = sum(in_degrees) / len(in_degrees)
        std_in = (
            sum((value - mean_in) ** 2 for value in in_degrees) / len(in_degrees)
        ) ** 0.5
        attractors = [
            node_id
            for node_id in graph.nodes
            if graph.in_degree(node_id) > (mean_in + std_in)
        ]
        self.assertGreaterEqual(len(attractors), 2)

    def test_delta_distributions_and_edge_continuity_are_mixed(self) -> None:
        data, graph = self._load_graph()
        tension_values = [float(node["delta_tension"]) for node in data["nodes"]]
        energy_values = [float(node["delta_energy"]) for node in data["nodes"]]

        self.assertGreaterEqual(sum(value < -0.05 for value in tension_values), 4)
        self.assertGreaterEqual(sum(value > 0.05 for value in tension_values), 10)
        self.assertGreaterEqual(sum(value < -0.05 for value in energy_values), 3)
        self.assertGreaterEqual(sum(value > 0.05 for value in energy_values), 10)

        edge_jumps = []
        for source, target in graph.edges:
            source_delta = float(graph.nodes[source]["delta_tension"])
            target_delta = float(graph.nodes[target]["delta_tension"])
            edge_jumps.append(abs(target_delta - source_delta))
        self.assertLessEqual(max(edge_jumps), 0.4)

    def test_situation_seams_are_not_single_entry_everywhere(self) -> None:
        data, graph = self._load_graph()
        nodes = {node["id"]: node for node in data["nodes"]}
        situation_ids = sorted({node["situation_id"] for node in data["nodes"]})

        multi_entry_situations = 0
        multi_exit_situations = 0
        for situation_id in situation_ids:
            situation_nodes = [
                node["id"] for node in data["nodes"] if node["situation_id"] == situation_id
            ]
            entry_nodes = {
                node_id
                for node_id in situation_nodes
                for source in graph.predecessors(node_id)
                if nodes[source]["situation_id"] != situation_id
            }
            exit_nodes = {
                node_id
                for node_id in situation_nodes
                if any(
                    nodes[target]["situation_id"] != situation_id
                    for target in graph.successors(node_id)
                )
            }
            self.assertGreaterEqual(len(entry_nodes), 1, situation_id)
            self.assertGreaterEqual(len(exit_nodes), 1, situation_id)
            if len(entry_nodes) >= 2:
                multi_entry_situations += 1
            if len(exit_nodes) >= 2:
                multi_exit_situations += 1

        self.assertGreaterEqual(multi_entry_situations, 5)
        self.assertGreaterEqual(multi_exit_situations, 4)

    def test_cluster_balance_is_authored_not_shuffled(self) -> None:
        _data, graph = self._load_graph()
        same_situation_edges = 0
        cross_situation_edges = 0
        for source, target in graph.edges:
            if graph.nodes[source]["situation_id"] == graph.nodes[target]["situation_id"]:
                same_situation_edges += 1
            else:
                cross_situation_edges += 1

        ratio = same_situation_edges / max(cross_situation_edges, 1)
        self.assertGreaterEqual(ratio, 1.0)
        self.assertLessEqual(ratio, 3.0)


if __name__ == "__main__":
    unittest.main()
