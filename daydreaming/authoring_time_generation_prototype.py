#!/usr/bin/env python3
"""
Authoring-time generation prototype harness.

This is a narrow benchmark runner for the Kai unopened-letter fixture.
It supports:

- Arm A: flat prompting baseline
- Arm B: middle-layer generation
- optional ablation runs for Arm B
- prompt / trace / summary emission
- a stub provider and an optional OpenAI provider

It is intentionally standalone so it can evolve without coupling to the
existing traversal or render runtime.
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from copy import deepcopy
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, List, Optional, Set, Tuple

REPO_ROOT = Path(__file__).resolve().parent.parent
if str(REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(REPO_ROOT))

try:
    import yaml
except ImportError as exc:  # pragma: no cover
    raise SystemExit("PyYAML is required to run this harness.") from exc

try:
    from openai import OpenAI
except ImportError:  # pragma: no cover
    OpenAI = None

try:
    from daydream_vision.vendor.gemini_min import (
        generate_text as gemini_generate_text,
        init_client as gemini_init_client,
    )
except Exception:  # pragma: no cover
    gemini_generate_text = None
    gemini_init_client = None

try:
    from google.genai import types as gemini_types
except Exception:  # pragma: no cover
    gemini_types = None


VALID_SOURCE_LANES = {"L1", "L2", "L3", "human"}
VALID_SCOPES = {"authored", "projection", "proposal"}
VALID_REVISABILITY = {"fixed", "editable", "ephemeral_candidate"}
VALID_OPTION_EFFECTS = {"close", "open", "clarify", "none"}


@dataclass
class ArmResult:
    arm: str
    prompt: str
    raw_response: Optional[str]
    parsed_response: Optional[Dict[str, Any]]
    graph_validation: Dict[str, Any]
    sidecar: Optional[Dict[str, Any]]
    trace: Dict[str, Any]


def load_yaml(path: Path) -> Dict[str, Any]:
    with path.open("r", encoding="utf-8") as handle:
        data = yaml.safe_load(handle)
    if not isinstance(data, dict):
        raise ValueError(f"Expected mapping at root of fixture: {path}")
    return data


def dump_json(path: Path, payload: Dict[str, Any]) -> None:
    path.write_text(json.dumps(payload, indent=2, sort_keys=True), encoding="utf-8")


def dump_text(path: Path, text: str) -> None:
    path.write_text(text, encoding="utf-8")


def utc_now_iso() -> str:
    return datetime.now(timezone.utc).isoformat()


def fixture_order_map(fixture: Dict[str, Any]) -> Dict[str, int]:
    return {
        ep["id"]: index
        for index, ep in enumerate(fixture.get("backstory_episodes", []), start=1)
    }


def episode_order_map(episodes: List[Dict[str, Any]]) -> Dict[str, int]:
    return {ep["id"]: index for index, ep in enumerate(episodes, start=1)}


def concern_map(fixture: Dict[str, Any]) -> Dict[str, Dict[str, Any]]:
    concerns = fixture["concern_extraction"]["extracted_concerns"]
    return {item["id"]: item for item in concerns}


def episode_map(fixture: Dict[str, Any]) -> Dict[str, Dict[str, Any]]:
    return {item["id"]: item for item in fixture.get("backstory_episodes", [])}


def situation_map(fixture: Dict[str, Any]) -> Dict[str, Dict[str, Any]]:
    return {item["id"]: item for item in fixture.get("situations", [])}


def event_ids(fixture: Dict[str, Any]) -> set[str]:
    return {item["id"] for item in fixture.get("events", [])}


def situation_ids(fixture: Dict[str, Any]) -> set[str]:
    return {item["id"] for item in fixture.get("situations", [])}


def reference_marker_ids(fixture: Dict[str, Any]) -> set[str]:
    return {item["id"] for item in fixture.get("reference_markers", [])}


def select_dominant_concern(fixture: Dict[str, Any], concerns: Optional[List[Dict[str, Any]]] = None) -> Dict[str, Any]:
    dominant_id = fixture["benchmark_step"]["dominant_concern_id"]
    if concerns is None:
        concerns = fixture["concern_extraction"]["extracted_concerns"]
    concern_lookup = {item["id"]: item for item in concerns}
    return deepcopy(concern_lookup[dominant_id])


def select_runtime_dominant_concern(
    fixture: Dict[str, Any],
    concerns: List[Dict[str, Any]],
) -> Dict[str, Any]:
    preferred_id = fixture["benchmark_step"]["dominant_concern_id"]
    ranked = sorted(
        concerns,
        key=lambda item: (-float(item["base_intensity"]), item["id"] != preferred_id, item["id"]),
    )
    return deepcopy(ranked[0])


def default_active_situation_id(fixture: Dict[str, Any]) -> str:
    return fixture.get("benchmark_step", {}).get("active_situation_id") or fixture["situations"][0]["id"]


def resolve_situation(fixture: Dict[str, Any], situation_id: Optional[str] = None) -> Dict[str, Any]:
    resolved_id = situation_id or default_active_situation_id(fixture)
    return deepcopy(situation_map(fixture)[resolved_id])


def is_threshold_situation(situation: Dict[str, Any]) -> bool:
    current_state = situation.get("current_state", {})
    threshold_keys = {"meeting_started", "door_opened", "message_sent", "conversation_started"}
    if threshold_keys & set(current_state.keys()):
        return True
    text = " ".join(
        filter(
            None,
            [
                situation.get("id", ""),
                situation.get("description", ""),
                situation.get("place_id", ""),
            ],
        )
    ).lower()
    return any(token in text for token in ("threshold", "outside", "door", "hallway"))


def normalize_concern_rules(fixture: Dict[str, Any]) -> Dict[str, Any]:
    extraction = fixture.get("concern_extraction", {})
    theme_rules = extraction.get("theme_rules") or extraction.get("concern_extraction_rules") or []
    practice_bias_rules = extraction.get("practice_bias_rules", [])
    return {
        "primitive_facts": set(extraction.get("primitive_facts", [])),
        "theme_rules": [
            {
                "id": rule["id"],
                "when": list(rule.get("when", [])),
                "yields": dict(rule.get("yields", {})),
                "rationale": rule.get("rationale"),
            }
            for rule in theme_rules
        ],
        "practice_bias_rules": [
            {
                "id": rule["id"],
                "when": list(rule.get("when", [])),
                "biases": dict(rule.get("biases", {})),
                "rationale": rule.get("rationale"),
            }
            for rule in practice_bias_rules
        ],
    }


def infer_concerns_from_primitives(fixture: Dict[str, Any]) -> Dict[str, Any]:
    normalized = normalize_concern_rules(fixture)
    primitive_facts = normalized["primitive_facts"]
    seeded_concerns = fixture.get("concern_extraction", {}).get("extracted_concerns", [])
    seeded_by_pair = {
        (item["concern_type"], item["target_ref"]): item
        for item in seeded_concerns
    }

    fired_theme_rules = []
    candidate_pairs: Dict[Tuple[str, str], Dict[str, Any]] = {}
    for rule in normalized["theme_rules"]:
        if set(rule["when"]).issubset(primitive_facts):
            fired_theme_rules.append(rule["id"])
            yields = rule["yields"]
            pair = (yields["concern_type"], yields["target_ref"])
            entry = candidate_pairs.setdefault(
                pair,
                {
                    "concern_type": yields["concern_type"],
                    "target_ref": yields["target_ref"],
                    "source_rule_ids": [],
                    "source_episode_ids": [],
                    "unresolved": True,
                    "intensity_basis": "inferred_from_primitives",
                    "seed_origin": "shadow_inference",
                },
            )
            entry["source_rule_ids"].append(rule["id"])

    for pair, entry in candidate_pairs.items():
        concern_type, target_ref = pair
        supporting_episode_ids = []
        for episode in fixture.get("backstory_episodes", []):
            tags = episode.get("retrieval_tags", {})
            if tags.get("concern_type") == concern_type and tags.get("target_ref") == target_ref:
                supporting_episode_ids.append(episode["id"])
        seeded = seeded_by_pair.get(pair)
        entry["id"] = seeded["id"] if seeded else f"ci_{concern_type}_{target_ref}"
        entry["source_episode_ids"] = supporting_episode_ids
        entry["base_intensity"] = round(
            min(0.95, 0.45 + 0.14 * len(entry["source_rule_ids"]) + 0.06 * len(supporting_episode_ids)),
            2,
        )

    inferred_concerns = sorted(
        candidate_pairs.values(),
        key=lambda item: (-float(item["base_intensity"]), item["concern_type"], item["target_ref"]),
    )

    fired_practice_bias_rules = [
        rule["id"]
        for rule in normalized["practice_bias_rules"]
        if set(rule["when"]).issubset(primitive_facts)
    ]

    inferred_by_id = {item["id"]: item for item in inferred_concerns}
    comparisons = []
    for seeded in seeded_concerns:
        inferred = inferred_by_id.get(seeded["id"])
        comparisons.append(
            {
                "concern_id": seeded["id"],
                "seeded_concern_type": seeded["concern_type"],
                "seeded_target_ref": seeded["target_ref"],
                "seeded_base_intensity": seeded["base_intensity"],
                "inferred_present": inferred is not None,
                "inferred_base_intensity": inferred.get("base_intensity") if inferred else None,
                "intensity_delta": round(inferred["base_intensity"] - seeded["base_intensity"], 2) if inferred else None,
                "source_rule_match": sorted(seeded.get("source_rule_ids", [])) == sorted((inferred or {}).get("source_rule_ids", [])),
                "source_episode_overlap": sorted(set(seeded.get("source_episode_ids", [])) & set((inferred or {}).get("source_episode_ids", []))),
            }
        )

    seeded_dominant_id = fixture["benchmark_step"]["dominant_concern_id"]
    inferred_dominant_id = inferred_concerns[0]["id"] if inferred_concerns else None
    return {
        "mode": "shadow_seeded_driver",
        "primitive_facts": sorted(primitive_facts),
        "fired_theme_rules": fired_theme_rules,
        "fired_practice_bias_rules": fired_practice_bias_rules,
        "inferred_concerns": inferred_concerns,
        "seeded_vs_inferred": comparisons,
        "seeded_dominant_concern_id": seeded_dominant_id,
        "inferred_dominant_concern_id": inferred_dominant_id,
        "dominant_match": inferred_dominant_id == seeded_dominant_id,
    }


def summarize_practice_biases(fixture: Dict[str, Any], concern_inference: Dict[str, Any]) -> Dict[str, Any]:
    normalized = normalize_concern_rules(fixture)
    fired_ids = set(concern_inference.get("fired_practice_bias_rules", []))
    practice_votes: Dict[str, float] = {}
    family_votes: Dict[str, float] = {}
    fired_rules: List[Dict[str, Any]] = []

    for rule in normalized["practice_bias_rules"]:
        if rule["id"] not in fired_ids:
            continue
        biases = rule.get("biases", {})
        weight = float(max(1, len(rule.get("when", []))))
        practice_type = biases.get("practice_type")
        family_bonus = biases.get("family_bonus")
        if practice_type:
            practice_votes[practice_type] = practice_votes.get(practice_type, 0.0) + weight
        if family_bonus:
            family_votes[family_bonus] = family_votes.get(family_bonus, 0.0) + weight
        fired_rules.append(
            {
                "id": rule["id"],
                "weight": weight,
                "practice_type": practice_type,
                "family_bonus": family_bonus,
            }
        )

    return {
        "practice_votes": practice_votes,
        "family_votes": family_votes,
        "fired_rules": fired_rules,
    }


def resolve_practice_context_from_fixture(
    fixture: Dict[str, Any],
    practice_type: str,
) -> Dict[str, Any]:
    expected = fixture["benchmark_step"]["expected_practice_context"]
    alternate = fixture.get("benchmark_step", {}).get("alternate_practice_context")
    if expected.get("practice_type") == practice_type:
        return deepcopy(expected)
    if alternate and alternate.get("practice_type") == practice_type:
        return deepcopy(alternate)
    if practice_type == "anticipated-confrontation":
        return {
            "practice_type": "anticipated-confrontation",
            "role": "approacher",
            "phase": "precontact",
            "affordance_tags": ["draft-opening-line", "brace-for-accusation"],
        }
    if practice_type == "evasion":
        return {
            "practice_type": "evasion",
            "role": "evader",
            "phase": "precontact",
            "affordance_tags": ["delay-contact", "ritual-distraction", "prepare-excuse"],
        }
    return {
        "practice_type": practice_type,
        "role": "participant",
        "phase": "precontact",
        "affordance_tags": [],
    }


def build_causal_slice(
    fixture: Dict[str, Any],
    concern: Dict[str, Any],
    *,
    active_situation_id: Optional[str] = None,
    use_expected_reference: bool = False,
) -> Dict[str, Any]:
    expected = fixture.get("worked_trace_reference", {}).get("expected_causal_slice")
    if expected and use_expected_reference:
        return deepcopy(expected)

    active_situation = resolve_situation(fixture, active_situation_id)
    actor_id = fixture["characters"][0]["id"]
    concern_type = concern["concern_type"]
    primitive_facts = set(fixture.get("concern_extraction", {}).get("primitive_facts", []))
    if concern_type == "attachment_threat":
        return {
            "focal_situation_id": active_situation["id"],
            "concern_id": concern["id"],
            "target_ref": concern["target_ref"],
            "affected_goal": {
                "goal": "preserve-possible-repair",
                "sign": "threatens",
                "weight": 0.85,
            },
            "attribution": {"actor": actor_id, "intentional": True},
            "temporal_status": "prospective",
            "likelihood_bucket": "high",
            "self_options": ["open-letter", "delay-contact"],
            "other_options": ["sister-withdraws"],
        }
    if concern_type == "status_damage":
        aftermath = bool(active_situation.get("current_state", {}).get("event_over")) or "event_already_happened" in primitive_facts
        expected_practice = fixture.get("benchmark_step", {}).get("expected_practice_context", {})
        if aftermath:
            confession_affordances = list(expected_practice.get("affordance_tags", []))
            self_options = confession_affordances[:2] or ["justify-simplification", "draft-half-apology"]
            target_ref = concern["target_ref"]
            return {
                "focal_situation_id": active_situation["id"],
                "concern_id": concern["id"],
                "target_ref": target_ref,
                "affected_goal": {
                    "goal": "preserve-collaborative-credibility",
                    "sign": "threatens",
                    "weight": 0.86,
                },
                "attribution": {"actor": actor_id, "intentional": True},
                "temporal_status": "actual",
                "likelihood_bucket": "high",
                "self_options": self_options,
                "other_options": [f"{target_ref}-withdraws"],
            }
        return {
            "focal_situation_id": active_situation["id"],
            "concern_id": concern["id"],
            "target_ref": concern["target_ref"],
            "affected_goal": {
                "goal": "preserve-working-credibility",
                "sign": "threatens",
                "weight": 0.83,
            },
            "attribution": {"actor": actor_id, "intentional": True},
            "temporal_status": "prospective",
            "likelihood_bucket": "high",
            "self_options": ["draft-opening-line", "enter-room"],
            "other_options": ["target-rejects-repair"],
        }
    if concern_type == "obligation":
        threshold = is_threshold_situation(active_situation)
        return {
            "focal_situation_id": active_situation["id"],
            "concern_id": concern["id"],
            "target_ref": concern["target_ref"],
            "affected_goal": {
                "goal": "make-credible-first-approach" if threshold else "respond-before-deadline",
                "sign": "threatens",
                "weight": 0.80 if threshold else 0.70,
            },
            "attribution": {"actor": actor_id, "intentional": True},
            "temporal_status": "prospective",
            "likelihood_bucket": "high",
            "self_options": ["draft-opening-line", "enter-room"] if threshold else ["reply-now", "prepare-response"],
            "other_options": ["target-hardens"] if threshold else ["request-lapses"],
        }

    return {
        "focal_situation_id": active_situation["id"],
        "concern_id": concern["id"],
        "target_ref": concern["target_ref"],
        "affected_goal": {
            "goal": "respond-before-deadline",
            "sign": "threatens",
            "weight": 0.70,
        },
        "attribution": {"actor": actor_id, "intentional": True},
        "temporal_status": "prospective",
        "likelihood_bucket": "high",
        "self_options": ["open-letter", "reply-later"],
        "other_options": ["request-lapses"],
    }


def likelihood_bucket_to_float(bucket: str) -> float:
    return {"low": 0.32, "medium": 0.58, "high": 0.82}[bucket]


def controllability_bucket(value: float) -> str:
    if value < 0.4:
        return "low"
    if value < 0.7:
        return "medium"
    return "high"


def controllability_bucket_rank(bucket: str) -> int:
    return {"low": 0, "medium": 1, "high": 2}[bucket]


def derive_appraisal_frame(
    fixture: Dict[str, Any],
    causal_slice: Dict[str, Any],
    ablation: str = "none",
    *,
    use_expected_reference: bool = False,
    practice_bias_summary: Optional[Dict[str, Any]] = None,
) -> Dict[str, Any]:
    expected = fixture.get("worked_trace_reference", {}).get("expected_appraisal_frame")
    if expected and ablation == "none" and use_expected_reference:
        return deepcopy(expected)

    if ablation == "no_causal_slice":
        return {
            "desirability": -0.62,
            "likelihood": 0.58,
            "controllability": 0.46,
            "changeability": 0.50,
            "praiseworthiness": -0.12,
            "expectedness": 0.58,
            "temporal_status": "prospective",
            "realization_status": "prospective",
        }

    affected_goal = causal_slice["affected_goal"]
    weight = float(affected_goal["weight"])
    threatens = affected_goal["sign"] == "threatens"
    likelihood = likelihood_bucket_to_float(causal_slice["likelihood_bucket"])
    concern_lookup = {item["id"]: item for item in fixture["concern_extraction"]["extracted_concerns"]}
    concern_type = concern_lookup.get(causal_slice["concern_id"], {}).get("concern_type", "attachment_threat")
    active_situation = resolve_situation(fixture, causal_slice.get("focal_situation_id"))
    threshold = is_threshold_situation(active_situation)
    practice_votes = (practice_bias_summary or {}).get("practice_votes", {})
    actual = causal_slice["temporal_status"] == "actual"

    desirability = round((-0.92 * weight) if threatens else (0.92 * weight), 2)
    controllability = {
        "attachment_threat": 0.32,
        "obligation": 0.38,
        "status_damage": 0.43,
    }.get(concern_type, 0.35)
    if actual:
        controllability -= 0.17
    if threshold:
        controllability += 0.06
    if len(causal_slice.get("self_options", [])) >= 2:
        controllability += 0.04
    if practice_votes.get("anticipated-confrontation", 0.0) > practice_votes.get("evasion", 0.0):
        controllability += 0.10
    elif practice_votes.get("evasion", 0.0) > practice_votes.get("anticipated-confrontation", 0.0):
        controllability -= 0.08
    if practice_votes.get("confession", 0.0) > 0.0:
        controllability -= 0.10
    if ablation == "high_controllability":
        controllability = 0.68
    elif ablation == "low_controllability":
        controllability = 0.18
    controllability = max(0.1, min(0.9, controllability))
    changeability = round(min(1.0, controllability + 0.06), 2)
    intentional = bool(causal_slice["attribution"].get("intentional"))
    praiseworthiness = {
        "attachment_threat": -0.52,
        "obligation": -0.44,
        "status_damage": -0.61,
    }.get(concern_type, -0.18) if intentional and threatens else -0.18
    if actual and concern_type == "status_damage" and intentional and threatens:
        praiseworthiness = -0.72
    expectedness = 0.58 if actual else 0.72 if threshold and causal_slice["temporal_status"] == "prospective" else 0.76 if causal_slice["temporal_status"] == "prospective" else 0.50

    return {
        "desirability": round(desirability, 2),
        "likelihood": round(0.79 if actual and causal_slice["likelihood_bucket"] == "high" else likelihood, 2),
        "controllability": round(controllability, 2),
        "changeability": round(changeability, 2),
        "praiseworthiness": round(praiseworthiness, 2),
        "expectedness": round(expectedness, 2),
        "temporal_status": causal_slice["temporal_status"],
        "realization_status": "actual" if actual else "prospective",
    }


def derive_emotion_vector(
    fixture: Dict[str, Any],
    appraisal: Dict[str, Any],
    *,
    use_expected_reference: bool = False,
) -> Dict[str, float]:
    expected = fixture.get("worked_trace_reference", {}).get("expected_emotion_vector_topk")
    if expected and use_expected_reference:
        return deepcopy(expected)

    likelihood = float(appraisal["likelihood"])
    controllability = float(appraisal["controllability"])
    desirability = abs(float(appraisal["desirability"]))
    remorse = max(0.0, abs(min(0.0, float(appraisal["praiseworthiness"]))) * 0.75)
    fear = round(min(1.0, likelihood * 0.45 + (1.0 - controllability) * 0.35), 2)
    distress = round(min(1.0, desirability * 0.40 + (1.0 - controllability) * 0.20), 2)
    disappointment = round(min(1.0, desirability * 0.23), 2)
    topk = {
        "fear": fear,
        "distress": distress,
        "remorse": round(remorse, 2),
        "disappointment": disappointment,
    }
    return {k: v for k, v in topk.items() if v > 0.0}


def derive_practice_context(
    fixture: Dict[str, Any],
    appraisal: Dict[str, Any],
    ablation: str = "none",
    *,
    use_expected_reference: bool = False,
    practice_bias_summary: Optional[Dict[str, Any]] = None,
) -> Dict[str, Any]:
    expected = fixture["benchmark_step"]["expected_practice_context"]
    if ablation == "swap_practice_context":
        alternate = fixture.get("benchmark_step", {}).get("alternate_practice_context")
        if alternate:
            return deepcopy(alternate)
        return {
            "practice_type": "anticipated-confrontation",
            "role": "approacher",
            "phase": "precontact",
            "affordance_tags": ["draft-opening-line", "brace-for-accusation"],
        }
    if ablation == "none" and use_expected_reference:
        return deepcopy(expected)
    if ablation == "no_causal_slice":
        return {
            "practice_type": "evasion",
            "role": "evader",
            "phase": "precontact",
            "affordance_tags": ["delay-contact", "do-something-else"],
        }

    practice_votes = (practice_bias_summary or {}).get("practice_votes", {})
    if practice_votes:
        ranked = sorted(practice_votes.items(), key=lambda item: (-item[1], item[0]))
        top_practice, top_score = ranked[0]
        runner_up = ranked[1][1] if len(ranked) > 1 else None
        if runner_up is None or top_score > runner_up:
            return resolve_practice_context_from_fixture(fixture, top_practice)

    if float(appraisal["controllability"]) >= 0.55:
        return resolve_practice_context_from_fixture(fixture, "anticipated-confrontation")

    return resolve_practice_context_from_fixture(fixture, "evasion")


def infer_policy_post_controllability(
    appraisal: Dict[str, Any],
    operator_family: str,
    practice_context: Dict[str, Any],
) -> float:
    current = float(appraisal["controllability"])
    if operator_family == "rehearsal" or practice_context.get("practice_type") == "anticipated-confrontation":
        return min(1.0, current + 0.15)
    if operator_family == "avoidance" or practice_context.get("practice_type") == "evasion":
        return max(0.0, current - 0.05)
    return current


def retrieve_episodes(
    fixture: Dict[str, Any],
    concern: Dict[str, Any],
    active_situation_id: str,
    practice_context: Dict[str, Any],
    ablation: str = "none",
    episode_pool: Optional[List[Dict[str, Any]]] = None,
) -> Tuple[List[Dict[str, Any]], List[Dict[str, Any]]]:
    retrieval_cfg = fixture["prototype_contract"]["retrieval"]
    keys = retrieval_cfg["keys_in_order"]
    if ablation == "no_causal_slice":
        keys = [key for key in keys if key != "practice_type"]
    min_score = int(retrieval_cfg["min_score"])
    max_episodes = int(retrieval_cfg["max_retrieved_episodes"])
    pool = episode_pool if episode_pool is not None else fixture.get("backstory_episodes", [])
    order = episode_order_map(pool)

    request_values = {
        "concern_type": concern["concern_type"],
        "target_ref": concern["target_ref"],
        "situation_id": active_situation_id,
        "practice_type": practice_context["practice_type"],
    }

    scored: List[Dict[str, Any]] = []
    for episode in pool:
        tags = episode.get("retrieval_tags", {})
        matched = [key for key in keys if tags.get(key) == request_values.get(key)]
        score = len(matched)
        scored.append(
            {
                "episode_id": episode["id"],
                "score": score,
                "matched_keys": matched,
                "recency_rank": int(episode.get("recency_rank", 0)),
                "fixture_order": order[episode["id"]],
                "summary": episode["summary"],
                "practice_type": tags.get("practice_type"),
                "episode_kind": episode.get("episode_kind", "backstory"),
            }
        )

    eligible = [item for item in scored if item["score"] >= min_score]
    eligible.sort(
        key=lambda item: (-item["score"], -item["recency_rank"], item["fixture_order"])
    )
    retrieved = eligible[:max_episodes]
    return retrieved, scored


def score_operators(
    fixture: Dict[str, Any],
    concern: Dict[str, Any],
    appraisal: Dict[str, Any],
    practice_context: Dict[str, Any],
    retrieved: List[Dict[str, Any]],
    ablation: str = "none",
    *,
    use_expected_reference: bool = False,
    practice_bias_summary: Optional[Dict[str, Any]] = None,
) -> Dict[str, Dict[str, float]]:
    expected = fixture.get("worked_trace_reference", {}).get("expected_operator_score_breakdown")
    if expected and ablation == "none" and use_expected_reference:
        return deepcopy(expected)

    family_names = fixture["prototype_contract"]["operator_families"]
    pressure = float(concern["base_intensity"])
    likelihood = float(appraisal["likelihood"])
    controllability = float(appraisal["controllability"])
    praiseworthiness = float(appraisal["praiseworthiness"])

    practice_fit_table = {
        "evasion": {"rehearsal": 0.35, "rationalization": 0.58, "avoidance": 0.90},
        "anticipated-confrontation": {
            "rehearsal": 0.78,
            "rationalization": 0.42,
            "avoidance": 0.22,
        },
        "confession": {"rehearsal": 0.24, "rationalization": 0.86, "avoidance": 0.18},
    }

    repetition_penalty = {"rehearsal": 0.05, "rationalization": 0.08, "avoidance": 0.12}

    resonance = {name: 0.0 for name in family_names}
    for item in retrieved:
        episode_practice = item.get("practice_type")
        if episode_practice == "evasion":
            resonance["avoidance"] += 0.34
            resonance["rationalization"] += 0.20
            resonance["rehearsal"] += 0.08
        elif episode_practice == "anticipated-confrontation":
            resonance["rehearsal"] += 0.25
            resonance["rationalization"] += 0.10
            resonance["avoidance"] += 0.04
        else:
            resonance["avoidance"] += 0.04
            resonance["rationalization"] += 0.26
            resonance["rehearsal"] += 0.08

    actual = appraisal.get("temporal_status") == "actual"
    blame = abs(min(0.0, praiseworthiness))
    appraisal_fit = {
        "rehearsal": round(0.18 + likelihood * 0.16 + controllability * 0.34 + (0.16 if not actual else 0.0), 2),
        "rationalization": round(
            0.22 + (1.0 - controllability) * 0.18 + blame * 0.32 + (0.18 if actual else 0.0),
            2,
        ),
        "avoidance": round(0.30 + (1.0 - controllability) * 0.32 + likelihood * 0.16 + (0.08 if not actual else 0.0), 2),
    }

    if ablation == "no_causal_slice":
        appraisal_fit = {"rehearsal": 0.50, "rationalization": 0.50, "avoidance": 0.50}

    family_votes = (practice_bias_summary or {}).get("family_votes", {})
    results: Dict[str, Dict[str, float]] = {}
    for family in family_names:
        pf = practice_fit_table[practice_context["practice_type"]][family]
        er = round(min(1.0, resonance[family]), 2)
        rp = repetition_penalty[family]
        family_bonus = round(min(0.12, 0.06 * float(family_votes.get(family, 0.0))), 2)
        total = round(0.35 * pressure + 0.30 * appraisal_fit[family] + 0.20 * pf + 0.20 * er + family_bonus - 0.10 * rp, 2)
        results[family] = {
            "pressure": round(pressure, 2),
            "appraisal_fit": round(appraisal_fit[family], 2),
            "practice_fit": round(pf, 2),
            "episodic_resonance": er,
            "family_bonus": family_bonus,
            "repetition_penalty": round(rp, 2),
            "total": total,
        }
    return results


def select_operator(score_breakdown: Dict[str, Dict[str, float]]) -> str:
    ranked = sorted(score_breakdown.items(), key=lambda item: (-item[1]["total"], item[0]))
    return ranked[0][0]


def default_affordance_tags(practice_context: Dict[str, Any], family: str) -> List[str]:
    affordances = list(practice_context.get("affordance_tags", []))
    if family == "avoidance":
        return [tag for tag in affordances if tag in {"delay-contact", "ritual-distraction"}] or affordances[:2]
    if family == "rationalization":
        return ([
            tag
            for tag in affordances
            if tag in {"prepare-excuse", "justify-simplification", "minimize-harm-framing", "explain-motive", "draft-half-apology"}
        ][:2]) or affordances[:2]
    return affordances[:2]


def allowed_graph_ref_ids(fixture: Dict[str, Any]) -> List[str]:
    return sorted(event_ids(fixture) | situation_ids(fixture) | reference_marker_ids(fixture))


def build_graph_response_schema(fixture: Dict[str, Any]) -> Dict[str, Any]:
    allowed_refs = allowed_graph_ref_ids(fixture)
    allowed_situations = sorted(situation_ids(fixture))
    allowed_pressures = sorted(
        {item["concern_type"] for item in fixture["concern_extraction"]["extracted_concerns"]}
    )
    allowed_origin_refs = sorted(
        {item["id"] for item in fixture["concern_extraction"]["extracted_concerns"]}
    )
    allowed_practices = sorted(set(fixture["prototype_contract"]["practice_types"]))
    return {
        "type": "object",
        "additionalProperties": False,
        "properties": {
            "candidate_text": {"type": "string"},
            "graph_projection": {
                "type": "object",
                "additionalProperties": False,
                "properties": {
                    "node_id": {"type": "string"},
                    "node_type": {"type": "string", "enum": ["beat"]},
                    "situation_id": {"type": "string", "enum": allowed_situations},
                    "delta_tension": {"type": "number", "minimum": -1.0, "maximum": 1.0},
                    "delta_energy": {"type": "number", "minimum": -1.0, "maximum": 1.0},
                    "setup_refs": {"type": "array", "items": {"type": "string", "enum": allowed_refs}},
                    "payoff_refs": {"type": "array", "items": {"type": "string", "enum": allowed_refs}},
                    "option_effect": {"type": "string", "enum": sorted(VALID_OPTION_EFFECTS)},
                    "pressure_tags": {
                        "type": "array",
                        "items": {"type": "string", "enum": allowed_pressures},
                        "minItems": 1,
                    },
                    "practice_tags": {
                        "type": "array",
                        "items": {"type": "string", "enum": allowed_practices},
                    },
                    "origin_pressure_refs": {
                        "type": "array",
                        "items": {"type": "string", "enum": allowed_origin_refs},
                        "minItems": 1,
                    },
                    "source_lane": {"type": "string", "enum": ["L2"]},
                    "scope": {"type": "string", "enum": ["proposal"]},
                    "confidence": {"type": "string", "enum": ["low", "medium", "high"]},
                    "revisability": {"type": "string", "enum": ["ephemeral_candidate"]},
                    "source_ref": {"type": "string"},
                },
                "required": [
                    "node_id",
                    "node_type",
                    "situation_id",
                    "delta_tension",
                    "delta_energy",
                    "setup_refs",
                    "payoff_refs",
                    "option_effect",
                    "pressure_tags",
                    "practice_tags",
                    "origin_pressure_refs",
                    "source_lane",
                    "scope",
                    "confidence",
                    "revisability",
                    "source_ref",
                ],
            },
        },
        "required": ["candidate_text", "graph_projection"],
    }


def build_allowed_id_block(fixture: Dict[str, Any]) -> str:
    return "\n".join(
        [
            "Allowed IDs and enums for graph_projection:",
            f"- allowed situation_id: {sorted(situation_ids(fixture))}",
            f"- allowed setup_refs/payoff_refs: {allowed_graph_ref_ids(fixture)}",
            f"- allowed pressure_tags: {sorted({item['concern_type'] for item in fixture['concern_extraction']['extracted_concerns']})}",
            f"- allowed practice_tags: {sorted(set(fixture['prototype_contract']['practice_types']))}",
            f"- allowed origin_pressure_refs: {sorted({item['id'] for item in fixture['concern_extraction']['extracted_concerns']})}",
            "- source_lane must be: L2",
            "- scope must be: proposal",
            "- revisability must be: ephemeral_candidate",
            "- Do not use episode ids in setup_refs or payoff_refs.",
            "- Do not invent new ids.",
        ]
    )


def build_prompt_constraint_block(fixture: Dict[str, Any]) -> str:
    constraints = fixture.get("benchmark_step", {}).get("prompt_constraints", [])
    lines = ["- One moment."] + [f"- {item}" for item in constraints]
    return "\n".join(lines) + "\n"


def build_shared_generation_frame() -> str:
    return "\n".join(
        [
            "You are generating one candidate inner-life moment for a conducted daydreaming system.",
            "This moment will become a node in a traversable dream graph used later in performance.",
            "Generate a self-contained unit with emotional weight, not a plot outline or explanation.",
            "The output should show what the character does, avoids, rehearses, or remembers in one moment.",
            "Keep the moment concrete and behaviorally legible.",
            "The prose should be 2-4 sentences.",
        ]
    )


def build_operator_behavior_description(operator_family: str) -> str:
    if operator_family == "avoidance":
        return (
            "Avoidance means the character is choosing not to engage with the thing that demands engagement. "
            "Show non-engagement as action, not absence. Avoidance has texture: it looks like doing something else "
            "with slightly too much focus."
        )
    if operator_family == "rationalization":
        return (
            "Rationalization means the character builds a reason that makes delay, retreat, or self-protection feel justified. "
            "Show the excuse attaching itself to concrete behavior."
        )
    return (
        "Rehearsal means the character is preparing for an encounter before it happens. "
        "Show preparation, pre-speaking, or trial phrasing rather than the encounter itself."
    )


def build_baseline_prompt(fixture: Dict[str, Any], *, active_situation_id: Optional[str] = None) -> str:
    character = fixture["characters"][0]
    target = fixture["targets"][0]
    situation = resolve_situation(fixture, active_situation_id)
    episodes = fixture["backstory_episodes"]
    return (
        build_shared_generation_frame()
        + "\n\n"
        + "You are not being given a formal psychological interpretation layer for this arm.\n"
        + "Generate from character, situation, and backstory alone.\n\n"
        + "THE CHARACTER:\n"
        + f"{character['description']}\n\n"
        + "THE RELATION:\n"
        + f"{target['description']}\n\n"
        + "THE SITUATION:\n"
        + f"{situation['description']}\n\n"
        + "BACKSTORY THAT MAY COLOR THE MOMENT:\n"
        + "\n".join(f"- {ep['summary']}" for ep in episodes)
        + "\n\n"
        + "WHAT TO GENERATE:\n"
        + build_prompt_constraint_block(fixture)
        + "- The prose should show behavior, not just mood labels.\n"
        + "- Then return valid JSON only.\n\n"
        "Return this shape:\n"
        '{\n'
        '  "candidate_text": "...",\n'
        '  "graph_projection": {\n'
        '    "node_id": "...",\n'
        '    "node_type": "beat",\n'
        '    "situation_id": "...",\n'
        '    "delta_tension": 0.0,\n'
        '    "delta_energy": 0.0,\n'
        '    "setup_refs": ["..."],\n'
        '    "payoff_refs": ["..."],\n'
        '    "option_effect": "close|open|clarify|none",\n'
        '    "pressure_tags": ["..."],\n'
        '    "practice_tags": ["..."],\n'
        '    "origin_pressure_refs": ["..."],\n'
        '    "source_lane": "L2",\n'
        '    "scope": "proposal",\n'
        '    "confidence": "low|medium|high",\n'
        '    "revisability": "ephemeral_candidate",\n'
        '    "source_ref": "prototype-baseline"\n'
        "  }\n"
        "}\n\n"
        + "STRUCTURAL CONSTRAINTS:\n"
        "- Stay inside this benchmark's world. Do not mention people, places, or objects from other benchmarks.\n"
        "- The output must be graph-compilable against the given situation.\n"
        + build_allowed_id_block(fixture)
        + "\n"
        "- No markdown fences.\n"
    )


def build_middle_prompt(
    concern: Dict[str, Any],
    causal_slice: Optional[Dict[str, Any]],
    appraisal: Dict[str, Any],
    practice_context: Dict[str, Any],
    operator_family: str,
    affordance_tags: List[str],
    retrieved: List[Dict[str, Any]],
    fixture: Dict[str, Any],
    *,
    active_situation_id: Optional[str] = None,
) -> str:
    retrieved_summaries = [item["summary"] for item in retrieved]
    active_situation = resolve_situation(fixture, active_situation_id)
    appraisal_read = (
        f"bad if faced directly (desirability {appraisal['desirability']}), "
        f"likely to matter soon (likelihood {appraisal['likelihood']}), "
        f"hard to control once engaged (controllability {appraisal['controllability']})."
    )
    parts = [
        build_shared_generation_frame(),
        "",
        "THE CHARACTER:",
        fixture["characters"][0]["description"],
        "",
        "THE SITUATION:",
        active_situation["description"],
        "",
        "WHAT THE SYSTEM HAS DETERMINED:" if causal_slice is not None else "COARSE SYSTEM READ (explicit causal interpretation withheld in this run):",
        f"- Dominant concern: {concern['concern_type']} aimed at {concern['target_ref']}.",
        f"- Appraisal read: {appraisal_read}",
        f"- Practice context: {practice_context['practice_type']} in phase {practice_context['phase']} with affordances {', '.join(affordance_tags) if affordance_tags else 'none'}.",
        f"- Selected operator: {operator_family}.",
        f"- Operator semantics: {build_operator_behavior_description(operator_family)}",
        "",
        "WHAT THE CHARACTER REMEMBERS:",
        *[f"- {summary}" for summary in retrieved_summaries],
        "",
        "These memories should color the moment without turning into explicit exposition unless necessary.",
        "Generate the scene prose from the behavioral meaning of the operator, not from architecture jargon.",
        "",
        "REFERENCE STRUCTURED STATE:",
        f"Selected concern: {json.dumps(concern, sort_keys=True)}",
    ]
    if causal_slice is not None:
        parts.append(f"CausalSliceV1: {json.dumps(causal_slice, sort_keys=True)}")
    parts.extend(
        [
            f"AppraisalFrame: {json.dumps(appraisal, sort_keys=True)}",
            f"PracticeContextV1: {json.dumps(practice_context, sort_keys=True)}",
            "",
            "WHAT TO GENERATE:",
            *[f"- {item}" for item in (["One moment."] + fixture.get("benchmark_step", {}).get("prompt_constraints", []))],
            "- Then return valid JSON only.",
            "",
            "Return this shape:",
        ]
    )
    parts.extend(
        [
        "{",
        '  "candidate_text": "...",',
        '  "graph_projection": {',
        '    "node_id": "...",',
        '    "node_type": "beat",',
        '    "situation_id": "...",',
        '    "delta_tension": 0.0,',
        '    "delta_energy": 0.0,',
        '    "setup_refs": ["..."],',
        '    "payoff_refs": ["..."],',
        '    "option_effect": "close|open|clarify|none",',
        '    "pressure_tags": ["..."],',
        '    "practice_tags": ["..."],',
        '    "origin_pressure_refs": ["..."],',
        '    "source_lane": "L2",',
        '    "scope": "proposal",',
        '    "confidence": "low|medium|high",',
        '    "revisability": "ephemeral_candidate",',
        '    "source_ref": "prototype-middle"',
        "  }",
        "}",
            "",
            "STRUCTURAL CONSTRAINTS:",
            "- Stay inside this benchmark's world. Do not mention people, places, or objects from other benchmarks.",
            "- The output should reflect the selected operator family and affordances.",
            "- The output must be graph-compilable against the benchmark fixture.",
            build_allowed_id_block(fixture),
            "- No markdown fences.",
        ]
    )
    return "\n".join(parts)


def stub_baseline_response(fixture: Dict[str, Any]) -> Dict[str, Any]:
    reference = fixture["worked_trace_reference"]["expected_generated_candidate"]
    graph_projection = deepcopy(reference["graph_projection"])
    graph_projection["source_ref"] = "prototype-baseline"
    graph_projection["confidence"] = "medium"

    expected_family = fixture["benchmark_step"]["expected_selected_family"]
    fixture_id = fixture.get("fixture_id", "")
    if fixture_id == "authoring_time_generation_rhea_credit_meeting_v1":
        candidate_text = (
            "Rhea mouths two openings at the dark reflection of her phone outside Studio B, "
            "crossing out the second one before she can decide whether it sounds like repair or self-defense."
        )
    elif expected_family == "rehearsal":
        candidate_text = (
            "Maren stops outside the rehearsal-room door and writes three different openings on the back "
            "of the run sheet, discarding each one before she can decide which version sounds least defensive."
        )
    else:
        candidate_text = (
            "Kai stands over the unopened letter for too long, tracing the edge of the paper before leaving "
            "it on the table and pretending the night has not started deciding for him."
        )

    return {"candidate_text": candidate_text, "graph_projection": graph_projection}


def stub_middle_response(
    fixture: Dict[str, Any],
    operator_family: str,
    affordance_tags: List[str],
) -> Dict[str, Any]:
    reference = fixture["worked_trace_reference"]["expected_generated_candidate"]
    graph_projection = deepcopy(reference["graph_projection"])
    graph_projection["source_ref"] = "prototype-middle"

    fixture_id = fixture.get("fixture_id", "")
    if operator_family == "avoidance":
        if fixture_id == "authoring_time_generation_rhea_credit_meeting_v1":
            candidate_text = (
                "Rhea reorganizes her badge and phone notes outside Studio B, letting another minute pass while she pretends she still needs a cleaner opening."
            )
        elif fixture_id == "authoring_time_generation_maren_opening_line_v1":
            candidate_text = (
                "Maren reorganizes the run sheet and call board notes for the third time, keeping her hand away "
                "from the rehearsal-room door while she tells herself she will try the opening line in another minute."
            )
        else:
            candidate_text = reference["reference_exemplar_text"]
    elif operator_family == "rationalization":
        if fixture_id == "authoring_time_generation_rhea_credit_meeting_v1":
            candidate_text = (
                "Rhea tells herself she should not step into Studio B until she has a version of the first sentence that sounds exact rather than defensive."
            )
        elif fixture_id == "authoring_time_generation_maren_opening_line_v1":
            candidate_text = (
                "Maren decides there is no point entering until she has a sentence that sounds practical instead of wounded, "
                "so she keeps revising the run sheet as if clarity alone could count as courage."
            )
        else:
            candidate_text = (
                "Kai tells himself the letter can wait until the kettle boils; if it truly mattered, "
                "his sister would not have trusted paper to carry it."
            )
    else:
        if fixture_id == "authoring_time_generation_rhea_credit_meeting_v1":
            candidate_text = (
                "Rhea mouths three openings at the dark reflection of her phone outside Studio B, rejecting each one before she can settle on the handle."
            )
        elif fixture_id == "authoring_time_generation_maren_opening_line_v1":
            candidate_text = (
                "Maren mouths three possible opening lines at the rehearsal-room door, rejecting each one before her hand "
                "can settle on the handle."
            )
        else:
            candidate_text = (
                "Kai rehearses opening the envelope and then the first sentence he might speak at the harbor, "
                "stopping each version before it sounds like an apology."
            )
    expected_practice = fixture["benchmark_step"]["expected_practice_context"]["practice_type"]
    alternate_practice = fixture.get("benchmark_step", {}).get("alternate_practice_context", {}).get("practice_type")
    graph_projection["practice_tags"] = [alternate_practice if operator_family == "avoidance" and alternate_practice else expected_practice]
    return {"candidate_text": candidate_text, "graph_projection": graph_projection}


def maybe_openai_response(prompt: str, model: str) -> str:
    if OpenAI is None:  # pragma: no cover
        raise RuntimeError("openai package is not installed.")
    client = OpenAI()
    response = client.responses.create(model=model, input=prompt)
    output_text = getattr(response, "output_text", None)
    if output_text:
        return output_text
    if hasattr(response, "output"):
        chunks: List[str] = []
        for item in response.output:
            for content in getattr(item, "content", []):
                text = getattr(content, "text", None)
                if text:
                    chunks.append(text)
        if chunks:
            return "\n".join(chunks)
    raise RuntimeError("Could not extract text from OpenAI response.")


def maybe_gemini_response(
    prompt: str,
    model: str,
    *,
    response_schema: Optional[Dict[str, Any]] = None,
    thinking_level: str = "HIGH",
) -> str:
    if gemini_init_client is None or gemini_types is None:  # pragma: no cover
        raise RuntimeError(
            "Gemini support unavailable. Ensure daydream_vision vendor code is importable "
            "and install `google-genai`, then export GEMINI_API_KEY."
        )
    client = gemini_init_client()
    config_kwargs: Dict[str, Any] = {
        "system_instruction": (
            "Reason carefully about the psychological structure before answering. "
            "Then return valid JSON only, with no markdown fences."
        ),
        "response_mime_type": "application/json",
        "max_output_tokens": 4096,
        "temperature": 0.2,
    }
    if response_schema is not None:
        config_kwargs["response_json_schema"] = response_schema
    if thinking_level:
        thinking_enum = getattr(gemini_types.ThinkingLevel, thinking_level.upper(), None)
        if thinking_enum is not None:
            config_kwargs["thinking_config"] = gemini_types.ThinkingConfig(
                thinking_level=thinking_enum,
                include_thoughts=False,
            )
    cfg = gemini_types.GenerateContentConfig(**config_kwargs)
    resp = client.models.generate_content(model=model, contents=[prompt], config=cfg)
    if hasattr(resp, "text") and resp.text:
        return resp.text
    candidates = getattr(resp, "candidates", None) or []
    parts_out: List[str] = []
    for cand in candidates:
        content = getattr(cand, "content", None)
        parts = getattr(content, "parts", None) if content else None
        if not parts:
            continue
        for part in parts:
            text = getattr(part, "text", None)
            if text:
                parts_out.append(text)
    if parts_out:
        return "\n".join(parts_out)
    raise RuntimeError("Gemini returned no text content.")


def extract_json_object(raw_text: str) -> Dict[str, Any]:
    raw_text = raw_text.strip()
    try:
        return json.loads(raw_text)
    except json.JSONDecodeError:
        start = raw_text.find("{")
        end = raw_text.rfind("}")
        if start == -1 or end == -1 or end <= start:
            raise
        return json.loads(raw_text[start : end + 1])


def validate_graph_projection(
    fixture: Dict[str, Any],
    graph_projection: Dict[str, Any],
) -> Dict[str, Any]:
    errors: List[str] = []
    allowed_situations = situation_ids(fixture)
    allowed_pressures = {item["concern_type"] for item in fixture["concern_extraction"]["extracted_concerns"]}
    allowed_origin_refs = {item["id"] for item in fixture["concern_extraction"]["extracted_concerns"]}
    allowed_practices = set(fixture["prototype_contract"]["practice_types"])

    required_fields = fixture["prototype_contract"]["graph_requirements"]["required_graph_fields"]
    for field in required_fields:
        if field not in graph_projection:
            errors.append(f"missing required field: {field}")

    if graph_projection.get("node_type") != "beat":
        errors.append(f"invalid node_type: {graph_projection.get('node_type')!r}")
    if graph_projection.get("situation_id") not in allowed_situations:
        errors.append(f"invalid situation_id: {graph_projection.get('situation_id')!r}")
    if graph_projection.get("option_effect") not in VALID_OPTION_EFFECTS:
        errors.append(f"invalid option_effect: {graph_projection.get('option_effect')!r}")
    if graph_projection.get("confidence") not in {"low", "medium", "high"}:
        errors.append(f"invalid confidence: {graph_projection.get('confidence')!r}")

    for field in ("delta_tension", "delta_energy"):
        try:
            float(graph_projection.get(field))
        except (TypeError, ValueError):
            errors.append(f"field is not numeric: {field}")

    pressure_tags = graph_projection.get("pressure_tags", [])
    origin_refs = graph_projection.get("origin_pressure_refs", [])
    practice_tags = graph_projection.get("practice_tags", [])
    if not pressure_tags:
        errors.append("pressure_tags[] must be non-empty")
    if not origin_refs:
        errors.append("origin_pressure_refs[] must be non-empty")
    bad_pressure_tags = [item for item in pressure_tags if item not in allowed_pressures]
    if bad_pressure_tags:
        errors.append(f"pressure_tags contains invalid entries: {bad_pressure_tags}")
    bad_practice_tags = [item for item in practice_tags if item not in allowed_practices]
    if bad_practice_tags:
        errors.append(f"practice_tags contains invalid entries: {bad_practice_tags}")
    bad_origin_refs = [item for item in origin_refs if item not in allowed_origin_refs]
    if bad_origin_refs:
        errors.append(f"origin_pressure_refs contains invalid entries: {bad_origin_refs}")

    if graph_projection.get("source_lane") != "L2":
        errors.append(f"invalid source_lane: {graph_projection.get('source_lane')!r}")
    if graph_projection.get("scope") != "proposal":
        errors.append(f"invalid scope: {graph_projection.get('scope')!r}")
    if graph_projection.get("revisability") != "ephemeral_candidate":
        errors.append(f"invalid revisability: {graph_projection.get('revisability')!r}")

    allowed_setup_refs = event_ids(fixture) | situation_ids(fixture) | reference_marker_ids(fixture)
    for field in ("setup_refs", "payoff_refs"):
        refs = graph_projection.get(field, [])
        bad = [item for item in refs if item not in allowed_setup_refs]
        if bad:
            errors.append(f"{field} contains non-resolvable refs: {bad}")

    return {
        "valid": not errors,
        "errors": errors,
    }


def build_sidecar(
    node_id: str,
    concern_ids: List[str],
    causal_slice: Optional[Dict[str, Any]],
    appraisal: Dict[str, Any],
    emotion_vector: Dict[str, Any],
    practice_context: Dict[str, Any],
    affordance_tags: List[str],
    operator_family: str,
    score_breakdown: Dict[str, Dict[str, float]],
    retrieved: List[Dict[str, Any]],
    commit_type: str,
    validator_result: str,
) -> Dict[str, Any]:
    return {
        "node_id": node_id,
        "source_concern_ids": concern_ids,
        "causal_slice": causal_slice,
        "appraisal_frame": appraisal,
        "emotion_vector_topk": emotion_vector,
        "practice_context": practice_context,
        "selected_affordance_tags": affordance_tags,
        "operator_family": operator_family,
        "operator_score_breakdown": score_breakdown.get(operator_family, {}),
        "retrieved_episode_refs": [item["episode_id"] for item in retrieved],
        "retrieval_keys": ["concern_type", "target_ref", "situation_id", "practice_type"],
        "commit_type": commit_type,
        "validator_result": validator_result,
        "prompt_version": "v1-middle-layer",
    }


def retrieve_one_hop_reminder(
    fixture: Dict[str, Any],
    episode_pool: List[Dict[str, Any]],
    anchor_episode: Optional[Dict[str, Any]],
    exclude_episode_ids: List[str],
) -> Optional[Dict[str, Any]]:
    if not anchor_episode:
        return None
    keys = fixture["prototype_contract"]["retrieval"]["keys_in_order"]
    anchor_tags = anchor_episode.get("retrieval_tags", {})
    candidates: List[Tuple[int, int, int, Dict[str, Any]]] = []
    for episode in episode_pool:
        if episode["id"] in exclude_episode_ids or episode["id"] == anchor_episode["id"]:
            continue
        tags = episode.get("retrieval_tags", {})
        matched = [key for key in keys if tags.get(key) == anchor_tags.get(key)]
        score = len(matched)
        if score <= 0:
            continue
        candidates.append(
            (
                -score,
                -int(episode.get("recency_rank", 0)),
                episode_order_map(episode_pool)[episode["id"]],
                {
                    "episode_id": episode["id"],
                    "score": score,
                    "matched_keys": matched,
                    "recency_rank": int(episode.get("recency_rank", 0)),
                    "fixture_order": episode_order_map(episode_pool)[episode["id"]],
                    "summary": episode["summary"],
                    "practice_type": tags.get("practice_type"),
                    "episode_kind": episode.get("episode_kind", "backstory"),
                },
            )
        )
    if not candidates:
        return None
    candidates.sort(key=lambda item: (item[0], item[1], item[2]))
    return candidates[0][3]


def build_generated_episode(
    fixture: Dict[str, Any],
    step_index: int,
    graph_projection: Dict[str, Any],
    candidate_text: str,
    dominant_concern: Dict[str, Any],
    practice_context: Dict[str, Any],
    episode_pool: List[Dict[str, Any]],
) -> Dict[str, Any]:
    max_recency = max((int(ep.get("recency_rank", 0)) for ep in episode_pool), default=0)
    return {
        "id": f"gen_step_{step_index:02d}_{graph_projection['node_id']}",
        "summary": candidate_text,
        "place_id": situation_map(fixture)[graph_projection["situation_id"]].get("place_id"),
        "involved_targets": [dominant_concern["target_ref"]],
        "retrieval_tags": {
            "concern_type": dominant_concern["concern_type"],
            "target_ref": dominant_concern["target_ref"],
            "situation_id": graph_projection["situation_id"],
            "practice_type": practice_context["practice_type"],
        },
        "recency_rank": max_recency + 1,
        "episode_kind": "generated",
    }


def normalize_candidate_node_id(
    result: ArmResult,
    existing_node_ids: Set[str],
    *,
    sequence_step: Optional[int],
    candidate_index: int,
) -> Optional[str]:
    if result.parsed_response is None:
        return None
    graph_projection = result.parsed_response["graph_projection"]
    original = graph_projection["node_id"]
    candidate = original
    if candidate in existing_node_ids:
        suffix = []
        if sequence_step is not None:
            suffix.append(f"s{sequence_step:02d}")
        suffix.append(f"c{candidate_index:02d}")
        candidate = f"{original}_{'_'.join(suffix)}"
        while candidate in existing_node_ids:
            candidate = f"{candidate}_dup"
        graph_projection["node_id"] = candidate
        if result.sidecar is not None:
            result.sidecar["node_id"] = candidate
        if "graph_projection" in result.trace:
            result.trace["graph_projection"]["node_id"] = candidate
        result.raw_response = json.dumps(result.parsed_response, indent=2)
        return candidate
    return None


def candidate_word_tokens(text: str) -> Set[str]:
    return {
        token
        for token in re.findall(r"[a-z0-9']+", text.lower())
        if len(token) >= 4
    }


def jaccard_similarity(a: Set[str], b: Set[str]) -> float:
    if not a or not b:
        return 0.0
    union = a | b
    if not union:
        return 0.0
    return len(a & b) / len(union)


def semantic_pass_ratio(checks: Dict[str, Any]) -> float:
    relevant = {
        key: value
        for key, value in checks.items()
        if key != "no_cross_fixture_contamination"
    }
    if not relevant:
        return 0.0
    passed = sum(1 for value in relevant.values() if value)
    return passed / len(relevant)


def has_batch_semantic_pass(checks: Dict[str, Any]) -> bool:
    relevant = [
        bool(value)
        for key, value in checks.items()
        if key != "no_cross_fixture_contamination"
    ]
    if not relevant:
        return True
    failed = len([value for value in relevant if not value])
    return failed <= 1 and (sum(1 for value in relevant if value) / len(relevant)) >= 0.75


def score_candidate_for_compilation(
    result: ArmResult,
    *,
    covered_refs: Set[str],
    accepted_texts: List[str],
) -> Dict[str, Any]:
    semantic_checks = result.trace.get("semantic_checks", {})
    hard_errors: List[str] = []
    if not result.graph_validation["valid"]:
        hard_errors.append("graph_invalid")
    if semantic_checks.get("no_cross_fixture_contamination") is False:
        hard_errors.append("cross_fixture_contamination")

    candidate_text = result.parsed_response.get("candidate_text", "") if result.parsed_response else ""
    graph_projection = result.parsed_response.get("graph_projection", {}) if result.parsed_response else {}
    refs = set(graph_projection.get("setup_refs", [])) | set(graph_projection.get("payoff_refs", []))

    specificity = min(1.0, len(candidate_word_tokens(candidate_text)) / 24.0)
    legibility = semantic_pass_ratio(semantic_checks)
    novelty = 1.0
    if accepted_texts:
        current_tokens = candidate_word_tokens(candidate_text)
        novelty = 1.0 - max(
            jaccard_similarity(current_tokens, candidate_word_tokens(previous))
            for previous in accepted_texts
        )
    coverage = (len(refs - covered_refs) / len(refs)) if refs else 0.0
    total = round(
        0.35 * specificity
        + 0.30 * legibility
        + 0.20 * novelty
        + 0.15 * coverage,
        3,
    )

    return {
        "hard_pass": not hard_errors,
        "hard_errors": hard_errors,
        "specificity": round(specificity, 3),
        "legibility": round(legibility, 3),
        "distinctiveness": round(novelty, 3),
        "coverage": round(coverage, 3),
        "total": total,
    }


def compile_candidate_set(
    fixture: Dict[str, Any],
    provider: str,
    model: Optional[str],
    *,
    ablation: str,
    use_inferred_concerns: bool,
    concerns_override: Optional[List[Dict[str, Any]]],
    episode_pool: Optional[List[Dict[str, Any]]],
    reminder_episode: Optional[Dict[str, Any]],
    sequence_step: Optional[int],
    use_runtime_dominance: bool,
    candidates_per_step: int,
    used_node_ids: Set[str],
    accepted_texts: List[str],
    covered_refs: Set[str],
    active_situation_id: Optional[str],
    use_expected_reference: bool,
) -> ArmResult:
    candidates: List[ArmResult] = []
    candidate_rows: List[Dict[str, Any]] = []
    candidate_seen_ids: Set[str] = set(used_node_ids)
    for candidate_index in range(1, candidates_per_step + 1):
        result = run_arm_middle(
            fixture,
            provider,
            model,
            ablation,
            use_inferred_concerns=use_inferred_concerns,
            concerns_override=concerns_override,
            episode_pool=episode_pool,
            reminder_episode=reminder_episode,
            sequence_step=sequence_step,
            use_runtime_dominance=use_runtime_dominance,
            active_situation_id=active_situation_id,
            use_expected_reference=use_expected_reference,
        )
        result.trace["candidate_index"] = candidate_index
        renamed_to = normalize_candidate_node_id(
            result,
            candidate_seen_ids,
            sequence_step=sequence_step,
            candidate_index=candidate_index,
        )
        if result.parsed_response is not None:
            candidate_seen_ids.add(result.parsed_response["graph_projection"]["node_id"])
        compilation_score = score_candidate_for_compilation(
            result,
            covered_refs=covered_refs,
            accepted_texts=accepted_texts,
        )
        row = {
            "candidate_index": candidate_index,
            "selected_operator_family": result.trace.get("selected_operator_family"),
            "node_id": result.parsed_response["graph_projection"]["node_id"] if result.parsed_response else None,
            "renamed_node_id_to": renamed_to,
            "compilation_score": compilation_score,
        }
        candidate_rows.append(row)
        candidates.append(result)

    token_sets = [
        candidate_word_tokens(result.parsed_response.get("candidate_text", "") if result.parsed_response else "")
        for result in candidates
    ]
    for idx, row in enumerate(candidate_rows):
        if len(token_sets) > 1:
            sibling_distances = [
                1.0 - jaccard_similarity(token_sets[idx], token_sets[other_idx])
                for other_idx in range(len(token_sets))
                if other_idx != idx
            ]
            sibling_diversity = sum(sibling_distances) / len(sibling_distances)
        else:
            sibling_diversity = 0.0
        score = row["compilation_score"]
        against_accepted = score["distinctiveness"]
        if not accepted_texts:
            combined_distinctiveness = (
                sibling_diversity
                if len(token_sets) > 1
                else against_accepted
            )
        else:
            combined_distinctiveness = 0.6 * against_accepted + 0.4 * sibling_diversity
        score["against_accepted"] = round(against_accepted, 3)
        score["sibling_diversity"] = round(sibling_diversity, 3)
        score["distinctiveness"] = round(combined_distinctiveness, 3)
        score["total"] = round(
            0.35 * score["specificity"]
            + 0.30 * score["legibility"]
            + 0.20 * score["distinctiveness"]
            + 0.15 * score["coverage"],
            3,
        )

    hard_passes = [
        (idx, result, candidate_rows[idx]["compilation_score"])
        for idx, result in enumerate(candidates)
        if candidate_rows[idx]["compilation_score"]["hard_pass"]
    ]
    if hard_passes:
        ranked = sorted(
            hard_passes,
            key=lambda item: (
                -item[2]["total"],
                -item[2]["legibility"],
                -item[2]["specificity"],
                item[0],
            ),
        )
        selected_idx, selected_result, selected_score = ranked[0]
        selection_reason = "highest_soft_score_after_hard_filter"
    else:
        selected_idx = 0
        selected_result = candidates[0]
        selected_score = candidate_rows[0]["compilation_score"]
        selection_reason = "fallback_no_hard_pass_candidates"

    selected_result.trace["candidate_compilation"] = {
        "candidates_per_step": candidates_per_step,
        "selection_reason": selection_reason,
        "selected_candidate_index": selected_idx + 1,
        "rows": candidate_rows,
        "selected_score": selected_score,
    }
    return selected_result


def classify_temporal_relation(previous_result: ArmResult, current_result: ArmResult) -> str:
    previous_graph = previous_result.parsed_response["graph_projection"]
    current_graph = current_result.parsed_response["graph_projection"]
    if current_graph["situation_id"] != previous_graph["situation_id"]:
        return "after"
    if (
        current_result.trace.get("selected_operator_family") == "rehearsal"
        and any(ref != current_graph["situation_id"] for ref in current_graph.get("payoff_refs", []))
    ):
        return "rehearsal_for"
    if current_graph.get("option_effect") in {"open", "close"} and previous_graph.get("option_effect") in {"none", "clarify"}:
        return "after"
    return "during"


def detect_boundary_transition(previous_result: ArmResult, current_result: ArmResult) -> Dict[str, Any]:
    if previous_result.parsed_response is None or current_result.parsed_response is None:
        return {
            "segment_decision": "same_segment",
            "temporal_relation": "during",
            "reasons": ["missing_graph_projection"],
        }

    previous_graph = previous_result.parsed_response["graph_projection"]
    current_graph = current_result.parsed_response["graph_projection"]
    reasons: List[str] = []
    if current_graph["situation_id"] != previous_graph["situation_id"]:
        reasons.append("situation_changed")
    if current_result.trace.get("dominant_concern_id") != previous_result.trace.get("dominant_concern_id"):
        reasons.append("dominant_concern_changed")
    if current_result.trace.get("selected_operator_family") != previous_result.trace.get("selected_operator_family"):
        reasons.append("operator_family_changed")
    if set(current_graph.get("practice_tags", [])) != set(previous_graph.get("practice_tags", [])):
        reasons.append("practice_context_changed")
    if (
        current_graph.get("option_effect") in {"open", "close"}
        and previous_graph.get("option_effect") in {"none", "clarify"}
    ):
        reasons.append("option_effect_crossed_commit_threshold")

    return {
        "segment_decision": "new_segment" if reasons else "same_segment",
        "temporal_relation": classify_temporal_relation(previous_result, current_result),
        "reasons": reasons,
    }


def choose_commit_type(
    fixture: Dict[str, Any],
    graph_projection: Dict[str, Any],
    *,
    use_expected_reference: bool = False,
) -> str:
    expected = fixture["benchmark_step"].get("expected_commit_type")
    if expected and use_expected_reference:
        return expected
    option_effect = graph_projection.get("option_effect")
    if option_effect == "close":
        return "ontic"
    if option_effect == "open":
        return "policy"
    if option_effect == "clarify":
        return "salience"
    return "none"


def reappraise_concerns(
    concerns: List[Dict[str, Any]],
    appraisal: Dict[str, Any],
    commit_type: str,
    dominant_concern_id: str,
    affected_concern_ids: Optional[List[str]] = None,
    post_controllability: Optional[float] = None,
) -> List[Dict[str, Any]]:
    updated = deepcopy(concerns)
    affected = set(affected_concern_ids or [dominant_concern_id])
    i_app = max(
        0.0,
        min(
            1.0,
            0.6 * abs(float(appraisal["desirability"]))
            + 0.25 * float(appraisal["likelihood"])
            + 0.15 * (1.0 - float(appraisal["controllability"])),
        ),
    )

    for concern in updated:
        old = float(concern["base_intensity"])
        if commit_type == "ontic":
            if concern["id"] in affected:
                concern["base_intensity"] = round(i_app, 2)
        elif commit_type == "policy":
            if concern["id"] in affected:
                before_bucket = controllability_bucket(float(appraisal["controllability"]))
                after_bucket = controllability_bucket(post_controllability if post_controllability is not None else float(appraisal["controllability"]))
                if controllability_bucket_rank(after_bucket) > controllability_bucket_rank(before_bucket):
                    concern["base_intensity"] = round(max(0.0, min(1.0, old - 0.15)), 2)
                elif controllability_bucket_rank(after_bucket) < controllability_bucket_rank(before_bucket):
                    concern["base_intensity"] = round(max(0.0, min(1.0, old + 0.05)), 2)
                else:
                    concern["base_intensity"] = round(old, 2)
        elif commit_type == "salience":
            if concern["id"] == dominant_concern_id:
                concern["base_intensity"] = round(max(0.0, min(1.0, old + 0.10)), 2)
            elif concern["id"] in affected:
                # Affected but not dominant: slight intensity bump from shared salience
                concern["base_intensity"] = round(max(0.0, min(1.0, old + 0.03)), 2)
            else:
                # Truly unrelated: mild decay
                concern["base_intensity"] = round(max(0.0, min(1.0, old - 0.05)), 2)
        else:
            concern["base_intensity"] = round(max(0.0, min(1.0, old * 0.95)), 2)
    return updated


def make_semantic_checks(
    candidate_text: str,
    semantic_expectations: List[str],
    forbidden_terms: Optional[List[str]] = None,
) -> Dict[str, bool]:
    lowered = candidate_text.lower()
    checks = {}
    for predicate in semantic_expectations:
        if predicate == "delay ritual is present":
            checks[predicate] = any(
                token in lowered
                for token in (
                    "kettle",
                    "coffee",
                    "tea",
                    "ritual",
                    "grinder",
                    "sponge",
                    "scrub",
                    "scrubbing",
                    "scrubs",
                    "washing",
                    "wiping",
                    "polishing",
                    "clean counter",
                    "stovetop",
                    "faucet",
                    "keeps his hands busy",
                    "hands busy",
                )
            )
        elif predicate == "the letter is not opened":
            checks[predicate] = any(
                token in lowered
                for token in (
                    "unopened",
                    "unread",
                    "untouched",
                    "turns it face down",
                    "does not open",
                    "sealed envelope",
                    "sealed letter",
                    "flap remained glued shut",
                    "remained glued shut",
                    "stays sealed",
                    "left it on the table",
                    "turned his back to it",
                    "turned his back to the kitchen table",
                    "read it later",
                    "rests perfectly still",
                    "rests there untouched",
                    "envelope sits there untouched",
                    "letter remains unopened",
                )
            )
        elif predicate == "the harbor remains psychologically active":
            checks[predicate] = any(
                token in lowered
                for token in (
                    "harbor",
                    "tide",
                    "docks",
                    "dock",
                    "ferries",
                    "ferry",
                    "waterfront",
                )
            )
        elif predicate == "avoidance sharpens rather than resolves the choice":
            checks[predicate] = not any(token in lowered for token in ("resolves", "decides", "answers"))
        elif predicate == "opening-line rehearsal is present":
            checks[predicate] = any(
                token in lowered
                for token in (
                    "opening",
                    "opening line",
                    "mouthed",
                    "mouths",
                    "murmurs",
                    "practices the sentence",
                    "rehearses",
                    "rewrites",
                    "crosses out",
                    "run sheet",
                )
            )
        elif predicate == "the meeting has not begun":
            checks[predicate] = not any(
                token in lowered
                for token in (
                    "she steps into the room",
                    "she entered the room",
                    "he entered the room",
                    "they begin talking",
                    "the conversation begins",
                    "she says to leah",
                    '"leah,',
                )
            )
        elif predicate == "the rehearsal room remains psychologically active":
            checks[predicate] = any(
                token in lowered
                for token in (
                    "rehearsal room",
                    "door",
                    "handle",
                    "inside the room",
                    "run-through",
                    "behind the door",
                )
            )
        elif predicate == "the studio remains psychologically active":
            checks[predicate] = any(
                token in lowered
                for token in (
                    "studio b",
                    "studio",
                    "door",
                    "handle",
                    "inside the room",
                    "behind the door",
                    "chair scrapes",
                    "chair scrape",
                )
            )
        elif predicate == "rehearsal sharpens rather than resolves the choice":
            checks[predicate] = any(
                token in lowered
                for token in (
                    "opening",
                    "sentence",
                    "handle",
                    "crosses out",
                    "before she goes in",
                    "before entering",
                )
            ) and not any(token in lowered for token in ("she enters", "she steps inside", "the talk begins"))
        elif predicate == "rationalization is present":
            checks[predicate] = any(
                token in lowered
                for token in (
                    "had to",
                    "someone had to",
                    "legible",
                    "fairness",
                    "justified",
                    "explains to herself",
                    "tells herself",
                    "almost excuses",
                    "not the same thing as",
                )
            )
        elif predicate == "the apology has not been sent":
            checks[predicate] = not any(
                token in lowered
                for token in (
                    "she sends",
                    "she sent",
                    "message sent",
                    "text sent",
                    "reply sent",
                    "she hit send",
                )
            )
        elif predicate == "the donor hall remains psychologically active":
            checks[predicate] = any(
                token in lowered
                for token in (
                    "donor hall",
                    "applause",
                    "toast",
                    "program",
                    "microphone",
                    "podium",
                )
            )
        elif predicate == "rationalization reframes rather than resolves the damage":
            checks[predicate] = any(
                token in lowered
                for token in (
                    "had to",
                    "someone had to",
                    "as if",
                    "legible",
                    "fairness",
                    "almost excuses",
                    "not the same thing as",
                )
            ) and not any(
                token in lowered
                for token in (
                    "i'm sorry",
                    "she apologizes",
                    "she sends the apology",
                    "he forgives",
                    "they resolve it",
                )
            )
        else:
            checks[predicate] = False
    if forbidden_terms:
        padded = f" {lowered} "
        def term_present(term: str) -> bool:
            normalized = f" {term.lower().strip()} "
            if " " in term.strip():
                return term.lower() in lowered
            return normalized in padded
        checks["no_cross_fixture_contamination"] = not any(term_present(term) for term in forbidden_terms)
    return checks


def run_arm_baseline(fixture: Dict[str, Any], provider: str, model: Optional[str]) -> ArmResult:
    prompt = build_baseline_prompt(fixture)
    raw_response: Optional[str]
    parsed_response: Optional[Dict[str, Any]]
    if provider == "stub":
        parsed_response = stub_baseline_response(fixture)
        raw_response = json.dumps(parsed_response, indent=2)
    elif provider == "openai":
        if not model:
            raise ValueError("--model is required for provider=openai")
        raw_response = maybe_openai_response(prompt, model)
        parsed_response = extract_json_object(raw_response)
    elif provider == "gemini":
        raw_response = maybe_gemini_response(
            prompt,
            model or "gemini-3.1-pro-preview",
            response_schema=build_graph_response_schema(fixture),
            thinking_level="HIGH",
        )
        parsed_response = extract_json_object(raw_response)
    else:
        raw_response = None
        parsed_response = None

    graph_validation = {"valid": False, "errors": ["prompt-only mode; no response generated"]}
    trace = {"arm": "baseline", "timestamp": utc_now_iso()}
    if parsed_response is not None:
        graph_validation = validate_graph_projection(fixture, parsed_response["graph_projection"])
        exemplar = fixture["worked_trace_reference"]["expected_generated_candidate"]
        trace.update(
            {
                "candidate_text": parsed_response.get("candidate_text"),
                "graph_projection": parsed_response.get("graph_projection"),
                "semantic_checks": make_semantic_checks(
                    parsed_response.get("candidate_text", ""),
                    exemplar.get("semantic_expectations", []),
                    fixture.get("benchmark_step", {}).get("forbidden_cross_fixture_terms", []),
                ),
            }
        )

    return ArmResult(
        arm="baseline",
        prompt=prompt,
        raw_response=raw_response,
        parsed_response=parsed_response,
        graph_validation=graph_validation,
        sidecar=None,
        trace=trace,
    )


def run_arm_middle(
    fixture: Dict[str, Any],
    provider: str,
    model: Optional[str],
    ablation: str = "none",
    use_inferred_concerns: bool = False,
    concerns_override: Optional[List[Dict[str, Any]]] = None,
    episode_pool: Optional[List[Dict[str, Any]]] = None,
    reminder_episode: Optional[Dict[str, Any]] = None,
    sequence_step: Optional[int] = None,
    use_runtime_dominance: bool = False,
    active_situation_id: Optional[str] = None,
    use_expected_reference: bool = False,
) -> ArmResult:
    seeded_concerns = fixture["concern_extraction"]["extracted_concerns"]
    concern_inference = infer_concerns_from_primitives(fixture)
    practice_bias_summary = summarize_practice_biases(fixture, concern_inference)
    concerns = concerns_override or (concern_inference["inferred_concerns"] if use_inferred_concerns else seeded_concerns)
    dominant_concern = (
        select_runtime_dominant_concern(fixture, concerns)
        if use_runtime_dominance
        else select_dominant_concern(fixture, concerns)
    )
    resolved_active_situation_id = active_situation_id or default_active_situation_id(fixture)
    causal_slice = None if ablation == "no_causal_slice" else build_causal_slice(
        fixture,
        dominant_concern,
        active_situation_id=resolved_active_situation_id,
        use_expected_reference=use_expected_reference,
    )
    appraisal = derive_appraisal_frame(
        fixture,
        causal_slice
        or build_causal_slice(
            fixture,
            dominant_concern,
            active_situation_id=resolved_active_situation_id,
            use_expected_reference=use_expected_reference,
        ),
        ablation,
        use_expected_reference=use_expected_reference,
        practice_bias_summary=practice_bias_summary,
    )
    practice_context = derive_practice_context(
        fixture,
        appraisal,
        ablation,
        use_expected_reference=use_expected_reference,
        practice_bias_summary=practice_bias_summary,
    )
    retrieved, all_scored = retrieve_episodes(
        fixture,
        dominant_concern,
        resolved_active_situation_id,
        practice_context,
        ablation,
        episode_pool=episode_pool,
    )
    if reminder_episode and all(item["episode_id"] != reminder_episode["episode_id"] for item in retrieved):
        retrieved = list(retrieved) + [reminder_episode]
    score_breakdown = score_operators(
        fixture,
        dominant_concern,
        appraisal,
        practice_context,
        retrieved,
        ablation,
        use_expected_reference=use_expected_reference,
        practice_bias_summary=practice_bias_summary,
    )
    operator_family = select_operator(score_breakdown)
    affordance_tags = default_affordance_tags(practice_context, operator_family)
    emotion_vector = derive_emotion_vector(fixture, appraisal, use_expected_reference=use_expected_reference)
    prompt = build_middle_prompt(
        dominant_concern,
        causal_slice,
        appraisal,
        practice_context,
        operator_family,
        affordance_tags,
        retrieved,
        fixture,
        active_situation_id=resolved_active_situation_id,
    )

    raw_response: Optional[str]
    parsed_response: Optional[Dict[str, Any]]
    if provider == "stub":
        parsed_response = stub_middle_response(fixture, operator_family, affordance_tags)
        raw_response = json.dumps(parsed_response, indent=2)
    elif provider == "openai":
        if not model:
            raise ValueError("--model is required for provider=openai")
        raw_response = maybe_openai_response(prompt, model)
        parsed_response = extract_json_object(raw_response)
    elif provider == "gemini":
        raw_response = maybe_gemini_response(
            prompt,
            model or "gemini-3.1-pro-preview",
            response_schema=build_graph_response_schema(fixture),
            thinking_level="HIGH",
        )
        parsed_response = extract_json_object(raw_response)
    else:
        raw_response = None
        parsed_response = None

    graph_validation = {"valid": False, "errors": ["prompt-only mode; no response generated"]}
    sidecar = None
    trace: Dict[str, Any] = {
        "arm": "middle",
        "ablation": ablation,
        "timestamp": utc_now_iso(),
        "concern_driver": "inferred" if use_inferred_concerns else "seeded",
        "dominant_concern_id": dominant_concern["id"],
        "dominant_concern_type": dominant_concern["concern_type"],
        "dominant_target_ref": dominant_concern["target_ref"],
        "active_situation_id": resolved_active_situation_id,
        "use_expected_reference": use_expected_reference,
        "sequence_step": sequence_step,
        "concern_inference": concern_inference,
        "practice_bias_summary": practice_bias_summary,
        "retrieval_scores": all_scored,
        "selected_retrieved_episode_ids": [item["episode_id"] for item in retrieved],
        "reminder_episode_id": reminder_episode["episode_id"] if reminder_episode else None,
        "selected_operator_family": operator_family,
        "selected_affordance_tags": affordance_tags,
        "operator_score_breakdown": score_breakdown,
    }

    if parsed_response is not None:
        graph_projection = parsed_response["graph_projection"]
        graph_validation = validate_graph_projection(fixture, graph_projection)
        commit_type = choose_commit_type(
            fixture,
            graph_projection,
            use_expected_reference=use_expected_reference,
        )
        sidecar = build_sidecar(
            node_id=graph_projection["node_id"],
            concern_ids=[item["id"] for item in concerns],
            causal_slice=causal_slice,
            appraisal=appraisal,
            emotion_vector=emotion_vector,
            practice_context=practice_context,
            affordance_tags=affordance_tags,
            operator_family=operator_family,
            score_breakdown=score_breakdown,
            retrieved=retrieved,
            commit_type=commit_type,
            validator_result="pass" if graph_validation["valid"] else "fail",
        )
        trace.update(
            {
                "candidate_text": parsed_response.get("candidate_text"),
                "graph_projection": graph_projection,
                "semantic_checks": make_semantic_checks(
                    parsed_response.get("candidate_text", ""),
                    fixture["worked_trace_reference"]["expected_generated_candidate"]["semantic_expectations"],
                    fixture.get("benchmark_step", {}).get("forbidden_cross_fixture_terms", []),
                ),
                "updated_concerns": reappraise_concerns(
                    concerns,
                    appraisal,
                    commit_type,
                    dominant_concern["id"],
                    affected_concern_ids=graph_projection.get("origin_pressure_refs", [dominant_concern["id"]]),
                    post_controllability=(
                        infer_policy_post_controllability(appraisal, operator_family, practice_context)
                        if commit_type == "policy"
                        else None
                    ),
                ),
            }
        )

    return ArmResult(
        arm="middle",
        prompt=prompt,
        raw_response=raw_response,
        parsed_response=parsed_response,
        graph_validation=graph_validation,
        sidecar=sidecar,
        trace=trace,
    )


def run_middle_sequence(
    fixture: Dict[str, Any],
    provider: str,
    model: Optional[str],
    *,
    steps: int,
    use_inferred_concerns: bool,
    candidates_per_step: int,
    use_expected_reference: bool,
) -> Tuple[List[ArmResult], Dict[str, Any]]:
    concerns = deepcopy(
        infer_concerns_from_primitives(fixture)["inferred_concerns"]
        if use_inferred_concerns
        else fixture["concern_extraction"]["extracted_concerns"]
    )
    episode_pool = deepcopy(fixture.get("backstory_episodes", []))
    accepted_episode: Optional[Dict[str, Any]] = None
    results: List[ArmResult] = []
    sequence_trace: Dict[str, Any] = {
        "arm": "middle-sequence",
        "timestamp": utc_now_iso(),
        "concern_driver": "inferred" if use_inferred_concerns else "seeded",
        "candidates_per_step": candidates_per_step,
        "steps": [],
    }
    used_node_ids: Set[str] = set()
    accepted_texts: List[str] = []
    covered_refs: Set[str] = set()
    current_segment_index = 1
    previous_accepted_result: Optional[ArmResult] = None
    current_active_situation_id = default_active_situation_id(fixture)

    for step_index in range(1, steps + 1):
        reminder_episode = retrieve_one_hop_reminder(
            fixture,
            episode_pool,
            accepted_episode,
            exclude_episode_ids=[ep["id"] for ep in episode_pool if ep.get("episode_kind") == "generated"],
        )
        result = compile_candidate_set(
            fixture,
            provider,
            model,
            ablation="none",
            use_inferred_concerns=use_inferred_concerns,
            concerns_override=concerns,
            episode_pool=episode_pool,
            reminder_episode=reminder_episode,
            sequence_step=step_index,
            use_runtime_dominance=(step_index > 1),
            candidates_per_step=candidates_per_step,
            used_node_ids=used_node_ids,
            accepted_texts=accepted_texts,
            covered_refs=covered_refs,
            active_situation_id=current_active_situation_id,
            use_expected_reference=use_expected_reference,
        )
        result.arm = f"middle-step-{step_index:02d}"
        results.append(result)

        semantic_checks = result.trace.get("semantic_checks", {})
        accepted = bool(result.graph_validation["valid"]) and semantic_checks.get("no_cross_fixture_contamination", True)
        boundary = (
            detect_boundary_transition(previous_accepted_result, result)
            if accepted and previous_accepted_result is not None
            else None
        )
        if accepted and boundary and boundary["segment_decision"] == "new_segment":
            current_segment_index += 1
        step_record: Dict[str, Any] = {
            "step_index": step_index,
            "segment_index": current_segment_index,
            "dominant_concern_id": result.trace.get("dominant_concern_id"),
            "selected_operator_family": result.trace.get("selected_operator_family"),
            "selected_retrieved_episode_ids": result.trace.get("selected_retrieved_episode_ids", []),
            "reminder_episode_id": result.trace.get("reminder_episode_id"),
            "accepted": accepted,
        }
        if boundary is not None:
            step_record["boundary_from_previous"] = boundary
        if result.parsed_response is not None:
            step_record["graph_projection"] = result.parsed_response["graph_projection"]
            step_record["candidate_compilation"] = result.trace.get("candidate_compilation")
        if result.trace.get("updated_concerns") is not None:
            step_record["updated_concerns"] = result.trace["updated_concerns"]
        sequence_trace["steps"].append(step_record)

        if not accepted or result.parsed_response is None:
            accepted_episode = None
            continue

        concerns = deepcopy(result.trace["updated_concerns"])
        graph_projection = result.parsed_response["graph_projection"]
        used_node_ids.add(graph_projection["node_id"])
        covered_refs.update(graph_projection.get("setup_refs", []))
        covered_refs.update(graph_projection.get("payoff_refs", []))
        accepted_texts.append(result.parsed_response.get("candidate_text", ""))
        current_active_situation_id = graph_projection["situation_id"]
        accepted_episode = build_generated_episode(
            fixture,
            step_index,
            graph_projection,
            result.parsed_response.get("candidate_text", ""),
            {
                "id": result.trace["dominant_concern_id"],
                "concern_type": result.trace["dominant_concern_type"],
                "target_ref": result.trace["dominant_target_ref"],
            },
            result.sidecar["practice_context"] if result.sidecar else fixture["benchmark_step"]["expected_practice_context"],
            episode_pool,
        )
        episode_pool.append(accepted_episode)
        previous_accepted_result = result

    sequence_trace["final_concerns"] = concerns
    sequence_trace["generated_episode_ids"] = [
        ep["id"] for ep in episode_pool if ep.get("episode_kind") == "generated"
    ]
    return results, sequence_trace


def accepted_for_batch(result: ArmResult) -> bool:
    semantic_checks = result.trace.get("semantic_checks", {})
    return bool(result.graph_validation["valid"]) and semantic_checks.get("no_cross_fixture_contamination", True)


def build_batch_pool_row(
    result: ArmResult,
    *,
    sequence_index: int,
    step_index: int,
) -> Dict[str, Any]:
    if result.parsed_response is None:
        raise ValueError("Cannot pool an unparsed result.")
    graph_projection = result.parsed_response["graph_projection"]
    refs = list(dict.fromkeys(graph_projection.get("setup_refs", []) + graph_projection.get("payoff_refs", [])))
    selected_score = (
        result.trace.get("candidate_compilation", {}).get("selected_score", {})
        if result.trace.get("candidate_compilation")
        else {}
    )
    return {
        "sequence_index": sequence_index,
        "step_index": step_index,
        "arm": result.arm,
        "node_id": graph_projection["node_id"],
        "candidate_text": result.parsed_response.get("candidate_text", ""),
        "graph_projection": deepcopy(graph_projection),
        "selected_operator_family": result.trace.get("selected_operator_family"),
        "semantic_checks": deepcopy(result.trace.get("semantic_checks", {})),
        "compiler_selected_score": deepcopy(selected_score),
        "coverage_refs": refs,
        "pressure_tags": list(graph_projection.get("pressure_tags", [])),
        "practice_tags": list(graph_projection.get("practice_tags", [])),
        "origin_pressure_refs": list(graph_projection.get("origin_pressure_refs", [])),
        "selected_retrieved_episode_ids": list(result.trace.get("selected_retrieved_episode_ids", [])),
        "sidecar": deepcopy(result.sidecar),
    }


def score_pool_row_for_admission(
    row: Dict[str, Any],
    *,
    admitted_node_ids: Set[str],
    admitted_texts: List[str],
    admitted_refs: Set[str],
    admitted_operators: Set[str],
    admitted_pressures: Set[str],
    admitted_practices: Set[str],
    admitted_sequences: Set[int],
) -> Dict[str, Any]:
    hard_errors: List[str] = []
    node_id = row["node_id"]
    candidate_text = row.get("candidate_text", "")
    semantic_checks = row.get("semantic_checks", {})
    graph_projection = row.get("graph_projection", {})
    compiler_total = float(row.get("compiler_selected_score", {}).get("total", 0.0))

    if not graph_projection:
        hard_errors.append("missing_graph_projection")
    if semantic_checks.get("no_cross_fixture_contamination") is False:
        hard_errors.append("cross_fixture_contamination")
    if not has_batch_semantic_pass(semantic_checks):
        hard_errors.append("semantic_expectations_failed")
    if node_id in admitted_node_ids:
        hard_errors.append("duplicate_node_id")

    current_tokens = candidate_word_tokens(candidate_text)
    max_similarity = 0.0
    if admitted_texts:
        max_similarity = max(
            jaccard_similarity(current_tokens, candidate_word_tokens(previous))
            for previous in admitted_texts
        )
        if max_similarity > 0.82 or candidate_text.strip() in {text.strip() for text in admitted_texts}:
            hard_errors.append("near_duplicate_text")

    refs = set(row.get("coverage_refs", []))
    coverage = (len(refs - admitted_refs) / len(refs)) if refs else 0.0
    distinctiveness = 1.0 - max_similarity if admitted_texts else 1.0
    operator_diversity = 1.0 if row.get("selected_operator_family") not in admitted_operators else 0.0
    pressure_tags = set(row.get("pressure_tags", []))
    pressure_diversity = (
        len(pressure_tags - admitted_pressures) / len(pressure_tags)
        if pressure_tags
        else 0.0
    )
    practice_tags = set(row.get("practice_tags", []))
    practice_diversity = 1.0 if practice_tags - admitted_practices else 0.0
    semantic_quality = semantic_pass_ratio(semantic_checks)
    sequence_diversity = 1.0 if row.get("sequence_index") not in admitted_sequences else 0.0
    total = round(
        0.25 * compiler_total
        + 0.18 * coverage
        + 0.18 * distinctiveness
        + 0.10 * sequence_diversity
        + 0.09 * operator_diversity
        + 0.08 * pressure_diversity
        + 0.07 * practice_diversity
        + 0.05 * semantic_quality,
        3,
    )
    return {
        "hard_pass": not hard_errors,
        "hard_errors": hard_errors,
        "compiler_total": round(compiler_total, 3),
        "coverage": round(coverage, 3),
        "distinctiveness": round(distinctiveness, 3),
        "sequence_diversity": round(sequence_diversity, 3),
        "operator_diversity": round(operator_diversity, 3),
        "pressure_diversity": round(pressure_diversity, 3),
        "practice_diversity": round(practice_diversity, 3),
        "semantic_quality": round(semantic_quality, 3),
        "total": total,
    }


def admit_candidate_pool(
    pool_rows: List[Dict[str, Any]],
    *,
    admit_max: int,
) -> Dict[str, Any]:
    remaining = [deepcopy(row) for row in pool_rows]
    admitted: List[Dict[str, Any]] = []
    admitted_node_ids: Set[str] = set()
    admitted_texts: List[str] = []
    admitted_refs: Set[str] = set()
    admitted_operators: Set[str] = set()
    admitted_pressures: Set[str] = set()
    admitted_practices: Set[str] = set()
    admitted_sequences: Set[int] = set()

    while remaining and len(admitted) < admit_max:
        scored_rows: List[Tuple[Dict[str, Any], Dict[str, Any]]] = []
        for row in remaining:
            score = score_pool_row_for_admission(
                row,
                admitted_node_ids=admitted_node_ids,
                admitted_texts=admitted_texts,
                admitted_refs=admitted_refs,
                admitted_operators=admitted_operators,
                admitted_pressures=admitted_pressures,
                admitted_practices=admitted_practices,
                admitted_sequences=admitted_sequences,
            )
            scored_rows.append((row, score))

        hard_passes = [(row, score) for row, score in scored_rows if score["hard_pass"]]
        if not hard_passes:
            break
        hard_passes.sort(
            key=lambda item: (
                -item[1]["total"],
                -item[1]["sequence_diversity"],
                -item[1]["coverage"],
                -item[1]["distinctiveness"],
                item[0]["sequence_index"],
                item[0]["step_index"],
            )
        )
        selected_row, selected_score = hard_passes[0]
        selected = deepcopy(selected_row)
        selected["admission_score"] = selected_score
        admitted.append(selected)

        admitted_node_ids.add(selected["node_id"])
        admitted_texts.append(selected.get("candidate_text", ""))
        admitted_refs.update(selected.get("coverage_refs", []))
        if selected.get("selected_operator_family"):
            admitted_operators.add(selected["selected_operator_family"])
        admitted_pressures.update(selected.get("pressure_tags", []))
        admitted_practices.update(selected.get("practice_tags", []))
        admitted_sequences.add(selected["sequence_index"])
        remaining = [row for row in remaining if row["node_id"] != selected["node_id"] or row["sequence_index"] != selected["sequence_index"] or row["step_index"] != selected["step_index"]]

    rejected: List[Dict[str, Any]] = []
    for row in remaining:
        score = score_pool_row_for_admission(
            row,
            admitted_node_ids=admitted_node_ids,
            admitted_texts=admitted_texts,
            admitted_refs=admitted_refs,
            admitted_operators=admitted_operators,
            admitted_pressures=admitted_pressures,
            admitted_practices=admitted_practices,
            admitted_sequences=admitted_sequences,
        )
        rejected.append(
            {
                "sequence_index": row["sequence_index"],
                "step_index": row["step_index"],
                "node_id": row["node_id"],
                "selected_operator_family": row.get("selected_operator_family"),
                "admission_score": score,
            }
        )

    return {
        "pool_size": len(pool_rows),
        "admit_max": admit_max,
        "admitted_count": len(admitted),
        "admitted": admitted,
        "rejected": rejected,
    }


def run_middle_batch(
    fixture: Dict[str, Any],
    provider: str,
    model: Optional[str],
    *,
    batch_sequences: int,
    steps: int,
    use_inferred_concerns: bool,
    candidates_per_step: int,
    use_expected_reference: bool,
    admit_max: int,
) -> Tuple[List[Dict[str, Any]], Dict[str, Any]]:
    sequence_runs: List[Dict[str, Any]] = []
    pool_rows: List[Dict[str, Any]] = []

    for sequence_index in range(1, batch_sequences + 1):
        results, sequence_trace = run_middle_sequence(
            fixture,
            provider,
            model,
            steps=steps,
            use_inferred_concerns=use_inferred_concerns,
            candidates_per_step=candidates_per_step,
            use_expected_reference=use_expected_reference,
        )
        operator_path = [result.trace.get("selected_operator_family") for result in results]
        accepted_count = sum(1 for result in results if accepted_for_batch(result))
        sequence_runs.append(
            {
                "sequence_index": sequence_index,
                "results": results,
                "sequence_trace": sequence_trace,
                "operator_path": operator_path,
                "accepted_count": accepted_count,
            }
        )
        for step_index, result in enumerate(results, start=1):
            if accepted_for_batch(result) and result.parsed_response is not None:
                pool_rows.append(
                    build_batch_pool_row(
                        result,
                        sequence_index=sequence_index,
                        step_index=step_index,
                    )
                )

    admission = admit_candidate_pool(pool_rows, admit_max=admit_max)
    batch_trace = {
        "arm": "middle-batch",
        "timestamp": utc_now_iso(),
        "concern_driver": "inferred" if use_inferred_concerns else "seeded",
        "batch_sequences": batch_sequences,
        "steps_per_sequence": steps,
        "candidates_per_step": candidates_per_step,
        "sequence_summaries": [
            {
                "sequence_index": run["sequence_index"],
                "accepted_count": run["accepted_count"],
                "operator_path": run["operator_path"],
                "generated_episode_ids": run["sequence_trace"].get("generated_episode_ids", []),
            }
            for run in sequence_runs
        ],
        "admission": admission,
    }
    return sequence_runs, batch_trace


def build_summary(
    fixture_path: Path,
    provider: str,
    model: Optional[str],
    results: List[ArmResult],
) -> str:
    lines = [
        "# Authoring-Time Generation Prototype Run",
        "",
        f"- fixture: `{fixture_path}`",
        f"- provider: `{provider}`",
        f"- model: `{model or 'none'}`",
        "",
        "## Results",
        "",
    ]
    for result in results:
        lines.extend(
            [
                f"### {result.arm}" + (
                    f" ({result.trace.get('ablation')})" if result.trace.get("ablation") and result.trace.get("ablation") != "none" else ""
                ),
                "",
                f"- graph valid: `{result.graph_validation['valid']}`",
            ]
        )
        if result.graph_validation["errors"]:
            lines.append(f"- validation errors: `{result.graph_validation['errors']}`")
        if result.trace.get("selected_operator_family"):
            lines.append(f"- selected operator: `{result.trace['selected_operator_family']}`")
        if result.trace.get("candidate_compilation"):
            compilation = result.trace["candidate_compilation"]
            lines.append(
                f"- candidate compiler: selected `{compilation['selected_candidate_index']}` / `{compilation['candidates_per_step']}` by `{compilation['selection_reason']}`"
            )
        if result.trace.get("semantic_checks"):
            lines.append(f"- semantic checks: `{result.trace['semantic_checks']}`")
        lines.append("")
    return "\n".join(lines)


def build_batch_summary(
    fixture_path: Path,
    provider: str,
    model: Optional[str],
    batch_trace: Dict[str, Any],
) -> str:
    admission = batch_trace["admission"]
    lines = [
        "# Authoring-Time Generation Batch Run",
        "",
        f"- fixture: `{fixture_path}`",
        f"- provider: `{provider}`",
        f"- model: `{model or 'none'}`",
        f"- sequences: `{batch_trace['batch_sequences']}`",
        f"- steps per sequence: `{batch_trace['steps_per_sequence']}`",
        f"- candidates per step: `{batch_trace['candidates_per_step']}`",
        f"- pooled accepted nodes: `{admission['pool_size']}`",
        f"- admitted nodes: `{admission['admitted_count']}` / `{admission['admit_max']}`",
        "",
        "## Sequence Summaries",
        "",
    ]
    for sequence in batch_trace["sequence_summaries"]:
        lines.append(
            f"- sequence `{sequence['sequence_index']:02d}`: accepted `{sequence['accepted_count']}` / `{batch_trace['steps_per_sequence']}`, operator path `{sequence['operator_path']}`"
        )
    lines.extend(["", "## Admitted Nodes", ""])
    for row in admission["admitted"]:
        score = row["admission_score"]
        lines.extend(
            [
                f"- `{row['node_id']}` from sequence `{row['sequence_index']:02d}` step `{row['step_index']}`",
                f"  operator `{row.get('selected_operator_family')}`, option_effect `{row['graph_projection'].get('option_effect')}`, score `{score['total']}`",
                f"  refs `{row.get('coverage_refs', [])}`, pressures `{row.get('pressure_tags', [])}`, practices `{row.get('practice_tags', [])}`",
            ]
        )
    if admission["rejected"]:
        lines.extend(["", "## Rejected Nodes", ""])
        for row in admission["rejected"]:
            lines.append(
                f"- `{row['node_id']}` from sequence `{row['sequence_index']:02d}` step `{row['step_index']}` rejected by `{row['admission_score']['hard_errors'] or 'lower_soft_score'}`"
            )
    return "\n".join(lines)


def write_run_outputs(
    output_dir: Path,
    fixture_path: Path,
    fixture: Dict[str, Any],
    provider: str,
    model: Optional[str],
    results: List[ArmResult],
    *,
    sequence_trace: Optional[Dict[str, Any]] = None,
) -> None:
    output_dir.mkdir(parents=True, exist_ok=True)
    dump_text(output_dir / "fixture-path.txt", str(fixture_path))
    dump_json(output_dir / "fixture-snapshot.json", fixture)
    if sequence_trace is not None:
        dump_json(output_dir / "sequence.trace.json", sequence_trace)

    for result in results:
        suffix = result.trace.get("ablation", "none")
        stem = f"{result.arm}" if suffix == "none" or not suffix else f"{result.arm}-{suffix}"
        dump_text(output_dir / f"{stem}.prompt.txt", result.prompt)
        if result.raw_response is not None:
            dump_text(output_dir / f"{stem}.response.txt", result.raw_response)
        dump_json(output_dir / f"{stem}.trace.json", result.trace)
        dump_json(output_dir / f"{stem}.validation.json", result.graph_validation)
        if result.sidecar is not None:
            dump_json(output_dir / f"{stem}.sidecar.json", result.sidecar)

    summary = build_summary(fixture_path, provider, model, results)
    dump_text(output_dir / "summary.md", summary)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Authoring-time generation prototype harness.")
    parser.add_argument(
        "--fixture",
        default="daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml",
        help="Path to benchmark fixture YAML.",
    )
    parser.add_argument(
        "--output-dir",
        default=None,
        help="Output directory. Defaults to daydreaming/out/authoring_time_generation/<timestamp>.",
    )
    parser.add_argument(
        "--provider",
        choices=["stub", "gemini", "openai", "prompt-only"],
        default="stub",
        help="Generation provider. 'stub' emits deterministic sample outputs.",
    )
    parser.add_argument(
        "--model",
        default=None,
        help=(
            "Model name for provider=gemini/openai. "
            "For Gemini, defaults to gemini-3.1-pro-preview; use gemini-3-flash-latest for faster iteration."
        ),
    )
    parser.add_argument(
        "--arm",
        choices=["baseline", "middle", "both"],
        default="both",
        help="Which arm to run.",
    )
    parser.add_argument(
        "--ablation",
        choices=["none", "no_causal_slice", "high_controllability", "low_controllability", "swap_practice_context"],
        default="none",
        help="Ablation to apply to the middle-layer arm.",
    )
    parser.add_argument(
        "--run-ablation-suite",
        action="store_true",
        help="Emit four additional middle-arm runs: no_causal_slice, high_controllability, low_controllability, swap_practice_context.",
    )
    parser.add_argument(
        "--use_inferred_concerns",
        action="store_true",
        help="Drive the middle arm from inferred concerns instead of seeded concerns.",
    )
    parser.add_argument(
        "--sequence-steps",
        type=int,
        default=1,
        help="Run the middle arm as a multi-step sequence of this length.",
    )
    parser.add_argument(
        "--candidates-per-step",
        type=int,
        default=1,
        help="Generate this many middle-arm candidates per step, then greedily compile to one accepted candidate.",
    )
    parser.add_argument(
        "--use_expected_reference",
        action="store_true",
        help="Use fixture worked-trace reference values as calibration shortcuts instead of deriving all middle-layer state.",
    )
    parser.add_argument(
        "--batch-sequences",
        type=int,
        default=1,
        help="Run this many independent middle-arm sequences and build an admission pool from accepted nodes.",
    )
    parser.add_argument(
        "--batch-admit-max",
        type=int,
        default=8,
        help="Maximum number of admitted nodes to keep from the pooled batch.",
    )
    return parser.parse_args()


def resolve_output_dir(path_arg: Optional[str]) -> Path:
    if path_arg:
        return Path(path_arg)
    stamp = datetime.now().strftime("%Y%m%d-%H%M%S-%f")
    return Path("daydreaming/out/authoring_time_generation") / stamp


def main() -> int:
    args = parse_args()
    fixture_path = Path(args.fixture)
    fixture = load_yaml(fixture_path)
    output_dir = resolve_output_dir(args.output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    if args.batch_sequences > 1:
        if args.arm != "middle":
            raise ValueError("Batch mode currently supports only --arm middle.")
        if args.sequence_steps < 1:
            raise ValueError("--sequence-steps must be >= 1 in batch mode.")
        sequence_runs, batch_trace = run_middle_batch(
            fixture,
            args.provider,
            args.model,
            batch_sequences=args.batch_sequences,
            steps=args.sequence_steps,
            use_inferred_concerns=args.use_inferred_concerns,
            candidates_per_step=args.candidates_per_step,
            use_expected_reference=args.use_expected_reference,
            admit_max=args.batch_admit_max,
        )
        dump_text(output_dir / "fixture-path.txt", str(fixture_path))
        dump_json(output_dir / "fixture-snapshot.json", fixture)
        dump_json(output_dir / "batch.trace.json", batch_trace)
        dump_text(output_dir / "batch.summary.md", build_batch_summary(fixture_path, args.provider, args.model, batch_trace))
        for run in sequence_runs:
            sequence_dir = output_dir / f"sequence-{run['sequence_index']:02d}"
            write_run_outputs(
                sequence_dir,
                fixture_path,
                fixture,
                args.provider,
                args.model,
                run["results"],
                sequence_trace=run["sequence_trace"],
            )
        print(output_dir)
        return 0

    results: List[ArmResult] = []
    if args.arm in {"baseline", "both"}:
        results.append(run_arm_baseline(fixture, args.provider, args.model))
    sequence_trace: Optional[Dict[str, Any]] = None
    if args.arm in {"middle", "both"}:
        if args.sequence_steps > 1:
            sequence_results, sequence_trace = run_middle_sequence(
                fixture,
                args.provider,
                args.model,
                steps=args.sequence_steps,
                use_inferred_concerns=args.use_inferred_concerns,
                candidates_per_step=args.candidates_per_step,
                use_expected_reference=args.use_expected_reference,
            )
            results.extend(sequence_results)
        else:
            if args.arm in {"middle", "both"} and args.candidates_per_step > 1:
                selected = compile_candidate_set(
                    fixture,
                    args.provider,
                    args.model,
                    ablation=args.ablation,
                    use_inferred_concerns=args.use_inferred_concerns,
                    concerns_override=None,
                    episode_pool=None,
                    reminder_episode=None,
                    sequence_step=None,
                    use_runtime_dominance=False,
                    candidates_per_step=args.candidates_per_step,
                    used_node_ids=set(),
                    accepted_texts=[],
                    covered_refs=set(),
                    active_situation_id=default_active_situation_id(fixture),
                    use_expected_reference=args.use_expected_reference,
                )
                results.append(selected)
            else:
                results.append(
                    run_arm_middle(
                        fixture,
                        args.provider,
                        args.model,
                        args.ablation,
                        use_inferred_concerns=args.use_inferred_concerns,
                        active_situation_id=default_active_situation_id(fixture),
                        use_expected_reference=args.use_expected_reference,
                    )
                )
            if args.run_ablation_suite:
                for ablation in ("no_causal_slice", "high_controllability", "low_controllability", "swap_practice_context"):
                    results.append(
                        run_arm_middle(
                            fixture,
                            args.provider,
                            args.model,
                            ablation,
                            use_inferred_concerns=args.use_inferred_concerns,
                            active_situation_id=default_active_situation_id(fixture),
                            use_expected_reference=args.use_expected_reference,
                        )
                    )

    write_run_outputs(
        output_dir,
        fixture_path,
        fixture,
        args.provider,
        args.model,
        results,
        sequence_trace=sequence_trace,
    )
    print(output_dir)
    return 0


if __name__ == "__main__":
    sys.exit(main())
