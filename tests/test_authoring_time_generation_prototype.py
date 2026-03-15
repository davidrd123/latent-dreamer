import unittest
from pathlib import Path

from daydreaming.authoring_time_generation_prototype import (
    ArmResult,
    build_causal_slice,
    classify_temporal_relation,
    choose_commit_type,
    compile_candidate_set,
    default_affordance_tags,
    default_active_situation_id,
    derive_appraisal_frame,
    derive_practice_context,
    detect_boundary_transition,
    infer_concerns_from_primitives,
    load_yaml,
    reappraise_concerns,
    retrieve_episodes,
    score_operators,
    select_operator,
    summarize_practice_biases,
)


REPO_ROOT = Path(__file__).resolve().parents[1]
KAI_FIXTURE = REPO_ROOT / "daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml"
RHEA_FIXTURE = REPO_ROOT / "daydreaming/fixtures/authoring_time_generation_rhea_credit_meeting_v1.yaml"
MAREN_FIXTURE = REPO_ROOT / "daydreaming/fixtures/authoring_time_generation_maren_opening_line_v1.yaml"
TESSA_FIXTURE = REPO_ROOT / "daydreaming/fixtures/authoring_time_generation_tessa_toast_rationalization_v1.yaml"


class AuthoringTimeGenerationPrototypeTests(unittest.TestCase):
    def setUp(self) -> None:
        self.fixture = load_yaml(KAI_FIXTURE)
        self.rhea_fixture = load_yaml(RHEA_FIXTURE)
        self.maren_fixture = load_yaml(MAREN_FIXTURE)
        self.tessa_fixture = load_yaml(TESSA_FIXTURE)

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

    def test_choose_commit_type_prefers_derived_effect_by_default(self) -> None:
        commit_type = choose_commit_type(
            self.fixture,
            {"option_effect": "none"},
            use_expected_reference=False,
        )
        self.assertEqual(commit_type, "none")

    def test_choose_commit_type_can_use_expected_reference_in_calibration_mode(self) -> None:
        commit_type = choose_commit_type(
            self.fixture,
            {"option_effect": "none"},
            use_expected_reference=True,
        )
        self.assertEqual(commit_type, self.fixture["benchmark_step"]["expected_commit_type"])

    def test_policy_reappraisal_only_updates_affected_concerns(self) -> None:
        concerns = [
            {"id": "cc_attachment_threat", "base_intensity": 0.72},
            {"id": "cc_obligation", "base_intensity": 0.64},
        ]
        updated = reappraise_concerns(
            concerns,
            {"desirability": -0.4, "likelihood": 0.7, "controllability": 0.5},
            "policy",
            "cc_attachment_threat",
            affected_concern_ids=["cc_attachment_threat"],
            post_controllability=0.72,
        )
        self.assertEqual(updated[0]["base_intensity"], 0.57)
        self.assertEqual(updated[1]["base_intensity"], 0.64)

    def test_policy_reappraisal_uses_controllability_bucket_shift(self) -> None:
        concerns = [{"id": "cc_status_damage", "base_intensity": 0.76}]
        unchanged = reappraise_concerns(
            concerns,
            {"desirability": -0.4, "likelihood": 0.7, "controllability": 0.57},
            "policy",
            "cc_status_damage",
            affected_concern_ids=["cc_status_damage"],
            post_controllability=0.63,
        )
        improved = reappraise_concerns(
            concerns,
            {"desirability": -0.4, "likelihood": 0.7, "controllability": 0.57},
            "policy",
            "cc_status_damage",
            affected_concern_ids=["cc_status_damage"],
            post_controllability=0.71,
        )
        worsened = reappraise_concerns(
            concerns,
            {"desirability": -0.4, "likelihood": 0.7, "controllability": 0.57},
            "policy",
            "cc_status_damage",
            affected_concern_ids=["cc_status_damage"],
            post_controllability=0.32,
        )
        self.assertEqual(unchanged[0]["base_intensity"], 0.76)
        self.assertEqual(improved[0]["base_intensity"], 0.61)
        self.assertEqual(worsened[0]["base_intensity"], 0.81)

    def test_rhea_practice_bias_prefers_anticipated_confrontation(self) -> None:
        concern_inference = infer_concerns_from_primitives(self.rhea_fixture)
        bias_summary = summarize_practice_biases(self.rhea_fixture, concern_inference)
        practice_context = derive_practice_context(
            self.rhea_fixture,
            {"controllability": 0.28},
            ablation="none",
            use_expected_reference=False,
            practice_bias_summary=bias_summary,
        )
        self.assertEqual(practice_context["practice_type"], "anticipated-confrontation")

    def test_maren_obligation_slice_uses_threshold_actions(self) -> None:
        concern = self.maren_fixture["concern_extraction"]["extracted_concerns"][0]
        concern_inference = infer_concerns_from_primitives(self.maren_fixture)
        bias_summary = summarize_practice_biases(self.maren_fixture, concern_inference)
        causal_slice = build_causal_slice(
            self.maren_fixture,
            concern,
            active_situation_id=default_active_situation_id(self.maren_fixture),
            use_expected_reference=False,
        )
        appraisal = derive_appraisal_frame(
            self.maren_fixture,
            causal_slice,
            ablation="none",
            use_expected_reference=False,
            practice_bias_summary=bias_summary,
        )
        self.assertEqual(causal_slice["self_options"], ["draft-opening-line", "enter-room"])
        self.assertEqual(causal_slice["other_options"], ["target-hardens"])
        self.assertGreaterEqual(appraisal["controllability"], 0.5)

    def test_tessa_derived_path_prefers_rationalization(self) -> None:
        concern_inference = infer_concerns_from_primitives(self.tessa_fixture)
        bias_summary = summarize_practice_biases(self.tessa_fixture, concern_inference)
        dominant = concern_inference["inferred_concerns"][0]
        active_situation_id = default_active_situation_id(self.tessa_fixture)
        causal_slice = build_causal_slice(
            self.tessa_fixture,
            dominant,
            active_situation_id=active_situation_id,
            use_expected_reference=False,
        )
        appraisal = derive_appraisal_frame(
            self.tessa_fixture,
            causal_slice,
            ablation="none",
            use_expected_reference=False,
            practice_bias_summary=bias_summary,
        )
        practice_context = derive_practice_context(
            self.tessa_fixture,
            appraisal,
            ablation="none",
            use_expected_reference=False,
            practice_bias_summary=bias_summary,
        )
        retrieved, _ = retrieve_episodes(
            self.tessa_fixture,
            dominant,
            active_situation_id,
            practice_context,
            ablation="none",
        )
        score_breakdown = score_operators(
            self.tessa_fixture,
            dominant,
            appraisal,
            practice_context,
            retrieved,
            ablation="none",
            use_expected_reference=False,
            practice_bias_summary=bias_summary,
        )

        self.assertEqual(causal_slice["temporal_status"], "actual")
        self.assertEqual(causal_slice["self_options"], ["justify-simplification", "minimize-harm-framing"])
        self.assertLess(appraisal["controllability"], 0.35)
        self.assertEqual(appraisal["realization_status"], "actual")
        self.assertEqual(practice_context["practice_type"], "confession")
        self.assertEqual(select_operator(score_breakdown), "rationalization")
        self.assertEqual(
            default_affordance_tags(practice_context, "rationalization"),
            ["justify-simplification", "minimize-harm-framing"],
        )


if __name__ == "__main__":
    unittest.main()
