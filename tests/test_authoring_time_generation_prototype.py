import unittest
from pathlib import Path

from daydreaming.authoring_time_generation_prototype import (
    ArmResult,
    classify_temporal_relation,
    compile_candidate_set,
    default_active_situation_id,
    detect_boundary_transition,
    load_yaml,
)


REPO_ROOT = Path(__file__).resolve().parents[1]
KAI_FIXTURE = REPO_ROOT / "daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml"


class AuthoringTimeGenerationPrototypeTests(unittest.TestCase):
    def setUp(self) -> None:
        self.fixture = load_yaml(KAI_FIXTURE)

    def test_single_candidate_compilation_keeps_prior_distinctiveness(self) -> None:
        result = compile_candidate_set(
            self.fixture,
            "stub",
            None,
            ablation="none",
            use_inferred_concerns=True,
            concerns_override=None,
            episode_pool=None,
            reminder_episode=None,
            sequence_step=1,
            use_runtime_dominance=False,
            candidates_per_step=1,
            used_node_ids=set(),
            accepted_texts=[],
            covered_refs=set(),
            active_situation_id=default_active_situation_id(self.fixture),
            use_expected_reference=False,
        )

        selected_score = result.trace["candidate_compilation"]["selected_score"]
        self.assertEqual(selected_score["against_accepted"], 1.0)
        self.assertEqual(selected_score["sibling_diversity"], 0.0)
        self.assertEqual(selected_score["distinctiveness"], 1.0)

    def test_multi_candidate_compilation_uses_sibling_diversity_before_accepts(self) -> None:
        result = compile_candidate_set(
            self.fixture,
            "stub",
            None,
            ablation="none",
            use_inferred_concerns=True,
            concerns_override=None,
            episode_pool=None,
            reminder_episode=None,
            sequence_step=1,
            use_runtime_dominance=False,
            candidates_per_step=3,
            used_node_ids=set(),
            accepted_texts=[],
            covered_refs=set(),
            active_situation_id=default_active_situation_id(self.fixture),
            use_expected_reference=False,
        )

        rows = result.trace["candidate_compilation"]["rows"]
        self.assertEqual(len(rows), 3)
        for row in rows:
            self.assertEqual(row["compilation_score"]["against_accepted"], 1.0)
            self.assertEqual(row["compilation_score"]["sibling_diversity"], 0.0)
            self.assertEqual(row["compilation_score"]["distinctiveness"], 0.0)

    def test_boundary_transition_flags_new_segment_on_situation_change(self) -> None:
        previous = ArmResult(
            arm="middle-step-01",
            prompt="",
            raw_response=None,
            parsed_response={
                "candidate_text": "Kai delays.",
                "graph_projection": {
                    "node_id": "n1",
                    "situation_id": "sit_unopened_letter",
                    "payoff_refs": ["sit_threshold_departure"],
                    "option_effect": "clarify",
                    "practice_tags": ["evasion"],
                },
            },
            graph_validation={"valid": True, "errors": []},
            sidecar=None,
            trace={
                "dominant_concern_id": "cc_attachment_threat",
                "selected_operator_family": "avoidance",
            },
        )
        current = ArmResult(
            arm="middle-step-02",
            prompt="",
            raw_response=None,
            parsed_response={
                "candidate_text": "Kai leaves for the harbor.",
                "graph_projection": {
                    "node_id": "n2",
                    "situation_id": "sit_threshold_departure",
                    "payoff_refs": ["ev_harbor_meeting_tonight"],
                    "option_effect": "open",
                    "practice_tags": ["anticipated-confrontation"],
                },
            },
            graph_validation={"valid": True, "errors": []},
            sidecar=None,
            trace={
                "dominant_concern_id": "cc_attachment_threat",
                "selected_operator_family": "rehearsal",
            },
        )

        boundary = detect_boundary_transition(previous, current)
        self.assertEqual(boundary["segment_decision"], "new_segment")
        self.assertIn("situation_changed", boundary["reasons"])
        self.assertEqual(classify_temporal_relation(previous, current), "after")


if __name__ == "__main__":
    unittest.main()
