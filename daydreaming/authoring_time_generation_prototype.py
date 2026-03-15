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
import sys
from copy import deepcopy
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, List, Optional, Tuple

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


def build_causal_slice(fixture: Dict[str, Any], concern: Dict[str, Any]) -> Dict[str, Any]:
    expected = fixture.get("worked_trace_reference", {}).get("expected_causal_slice")
    if expected:
        return deepcopy(expected)

    active_situation = fixture["situations"][0]
    concern_type = concern["concern_type"]
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
            "attribution": {"actor": "kai", "intentional": True},
            "temporal_status": "prospective",
            "likelihood_bucket": "high",
            "self_options": ["open-letter", "delay-contact"],
            "other_options": ["sister-withdraws"],
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
        "attribution": {"actor": "kai", "intentional": True},
        "temporal_status": "prospective",
        "likelihood_bucket": "high",
        "self_options": ["open-letter", "reply-later"],
        "other_options": ["request-lapses"],
    }


def likelihood_bucket_to_float(bucket: str) -> float:
    return {"low": 0.32, "medium": 0.58, "high": 0.82}[bucket]


def derive_appraisal_frame(
    fixture: Dict[str, Any],
    causal_slice: Dict[str, Any],
    ablation: str = "none",
) -> Dict[str, Any]:
    expected = fixture.get("worked_trace_reference", {}).get("expected_appraisal_frame")
    if expected and ablation == "none":
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

    desirability = round((-0.92 * weight) if threatens else (0.92 * weight), 2)
    controllability = 0.28
    if ablation == "high_controllability":
        controllability = 0.68
    elif ablation == "low_controllability":
        controllability = 0.18
    changeability = round(min(1.0, controllability + 0.06), 2)
    intentional = bool(causal_slice["attribution"].get("intentional"))
    praiseworthiness = -0.52 if intentional and threatens else -0.18
    expectedness = 0.76 if causal_slice["temporal_status"] == "prospective" else 0.50

    return {
        "desirability": round(desirability, 2),
        "likelihood": round(likelihood, 2),
        "controllability": round(controllability, 2),
        "changeability": round(changeability, 2),
        "praiseworthiness": round(praiseworthiness, 2),
        "expectedness": round(expectedness, 2),
        "temporal_status": causal_slice["temporal_status"],
        "realization_status": "prospective",
    }


def derive_emotion_vector(
    fixture: Dict[str, Any],
    appraisal: Dict[str, Any],
) -> Dict[str, float]:
    expected = fixture.get("worked_trace_reference", {}).get("expected_emotion_vector_topk")
    if expected:
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
    if ablation == "none":
        return deepcopy(expected)
    if ablation == "no_causal_slice":
        return {
            "practice_type": "evasion",
            "role": "evader",
            "phase": "precontact",
            "affordance_tags": ["delay-contact", "do-something-else"],
        }

    if float(appraisal["controllability"]) >= 0.55:
        return {
            "practice_type": "anticipated-confrontation",
            "role": "approacher",
            "phase": "precontact",
            "affordance_tags": ["draft-opening-line", "brace-for-accusation"],
        }

    return {
        "practice_type": "evasion",
        "role": "evader",
        "phase": "precontact",
        "affordance_tags": ["delay-contact", "ritual-distraction", "prepare-excuse"],
    }


def retrieve_episodes(
    fixture: Dict[str, Any],
    concern: Dict[str, Any],
    active_situation_id: str,
    practice_context: Dict[str, Any],
    ablation: str = "none",
) -> Tuple[List[Dict[str, Any]], List[Dict[str, Any]]]:
    retrieval_cfg = fixture["prototype_contract"]["retrieval"]
    keys = retrieval_cfg["keys_in_order"]
    if ablation == "no_causal_slice":
        keys = [key for key in keys if key != "practice_type"]
    min_score = int(retrieval_cfg["min_score"])
    max_episodes = int(retrieval_cfg["max_retrieved_episodes"])
    order = fixture_order_map(fixture)

    request_values = {
        "concern_type": concern["concern_type"],
        "target_ref": concern["target_ref"],
        "situation_id": active_situation_id,
        "practice_type": practice_context["practice_type"],
    }

    scored: List[Dict[str, Any]] = []
    for episode in fixture.get("backstory_episodes", []):
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
) -> Dict[str, Dict[str, float]]:
    expected = fixture.get("worked_trace_reference", {}).get("expected_operator_score_breakdown")
    if expected and ablation == "none":
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
        "confession": {"rehearsal": 0.40, "rationalization": 0.30, "avoidance": 0.15},
    }

    repetition_penalty = {"rehearsal": 0.05, "rationalization": 0.08, "avoidance": 0.12}

    resonance = {name: 0.0 for name in family_names}
    for item in retrieved:
        episode = episode_map(fixture)[item["episode_id"]]
        episode_practice = episode.get("retrieval_tags", {}).get("practice_type")
        if episode_practice == "evasion":
            resonance["avoidance"] += 0.34
            resonance["rationalization"] += 0.20
            resonance["rehearsal"] += 0.08
        elif episode_practice == "anticipated-confrontation":
            resonance["rehearsal"] += 0.25
            resonance["rationalization"] += 0.10
            resonance["avoidance"] += 0.04
        else:
            resonance["avoidance"] += 0.08
            resonance["rationalization"] += 0.10
            resonance["rehearsal"] += 0.12

    appraisal_fit = {
        "rehearsal": round(0.25 + likelihood * 0.20 + controllability * 0.35, 2),
        "rationalization": round(
            0.30 + (1.0 - controllability) * 0.20 + abs(min(0.0, praiseworthiness)) * 0.35 + likelihood * 0.04,
            2,
        ),
        "avoidance": round(0.38 + (1.0 - controllability) * 0.40 + likelihood * 0.18, 2),
    }

    if ablation == "no_causal_slice":
        appraisal_fit = {"rehearsal": 0.50, "rationalization": 0.50, "avoidance": 0.50}

    results: Dict[str, Dict[str, float]] = {}
    for family in family_names:
        pf = practice_fit_table[practice_context["practice_type"]][family]
        er = round(min(1.0, resonance[family]), 2)
        rp = repetition_penalty[family]
        total = round(0.35 * pressure + 0.30 * appraisal_fit[family] + 0.20 * pf + 0.20 * er - 0.10 * rp, 2)
        results[family] = {
            "pressure": round(pressure, 2),
            "appraisal_fit": round(appraisal_fit[family], 2),
            "practice_fit": round(pf, 2),
            "episodic_resonance": er,
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
        return [tag for tag in affordances if tag in {"prepare-excuse"}] or affordances[:1]
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


def build_baseline_prompt(fixture: Dict[str, Any]) -> str:
    character = fixture["characters"][0]
    target = fixture["targets"][0]
    situation = fixture["situations"][0]
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
) -> str:
    retrieved_summaries = [episode_map(fixture)[item["episode_id"]]["summary"] for item in retrieved]
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
        fixture["situations"][0]["description"],
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

    required_fields = fixture["prototype_contract"]["graph_requirements"]["required_graph_fields"]
    for field in required_fields:
        if field not in graph_projection:
            errors.append(f"missing required field: {field}")

    if graph_projection.get("option_effect") not in VALID_OPTION_EFFECTS:
        errors.append(f"invalid option_effect: {graph_projection.get('option_effect')!r}")

    for field in ("delta_tension", "delta_energy"):
        try:
            float(graph_projection.get(field))
        except (TypeError, ValueError):
            errors.append(f"field is not numeric: {field}")

    pressure_tags = graph_projection.get("pressure_tags", [])
    origin_refs = graph_projection.get("origin_pressure_refs", [])
    if not pressure_tags:
        errors.append("pressure_tags[] must be non-empty")
    if not origin_refs:
        errors.append("origin_pressure_refs[] must be non-empty")

    if graph_projection.get("source_lane") not in VALID_SOURCE_LANES:
        errors.append(f"invalid source_lane: {graph_projection.get('source_lane')!r}")
    if graph_projection.get("scope") not in VALID_SCOPES:
        errors.append(f"invalid scope: {graph_projection.get('scope')!r}")
    if graph_projection.get("revisability") not in VALID_REVISABILITY:
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


def choose_commit_type(
    fixture: Dict[str, Any],
    graph_projection: Dict[str, Any],
) -> str:
    expected = fixture["benchmark_step"].get("expected_commit_type")
    if expected:
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
) -> List[Dict[str, Any]]:
    updated = deepcopy(concerns)
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
            concern["base_intensity"] = round(i_app, 2)
        elif commit_type == "policy":
            concern["base_intensity"] = round(max(0.0, min(1.0, old - 0.15)), 2)
        elif commit_type == "salience":
            if concern["id"] == dominant_concern_id:
                concern["base_intensity"] = round(max(0.0, min(1.0, old + 0.10)), 2)
            else:
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
) -> ArmResult:
    seeded_concerns = fixture["concern_extraction"]["extracted_concerns"]
    concern_inference = infer_concerns_from_primitives(fixture)
    concerns = concern_inference["inferred_concerns"] if use_inferred_concerns else seeded_concerns
    dominant_concern = select_dominant_concern(fixture, concerns)
    active_situation_id = fixture["situations"][0]["id"]
    causal_slice = None if ablation == "no_causal_slice" else build_causal_slice(fixture, dominant_concern)
    appraisal = derive_appraisal_frame(fixture, causal_slice or build_causal_slice(fixture, dominant_concern), ablation)
    practice_context = derive_practice_context(fixture, appraisal, ablation)
    retrieved, all_scored = retrieve_episodes(
        fixture,
        dominant_concern,
        active_situation_id,
        practice_context,
        ablation,
    )
    score_breakdown = score_operators(fixture, dominant_concern, appraisal, practice_context, retrieved, ablation)
    operator_family = select_operator(score_breakdown)
    affordance_tags = default_affordance_tags(practice_context, operator_family)
    emotion_vector = derive_emotion_vector(fixture, appraisal)
    prompt = build_middle_prompt(
        dominant_concern,
        causal_slice,
        appraisal,
        practice_context,
        operator_family,
        affordance_tags,
        retrieved,
        fixture,
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
        "concern_inference": concern_inference,
        "retrieval_scores": all_scored,
        "selected_operator_family": operator_family,
        "selected_affordance_tags": affordance_tags,
        "operator_score_breakdown": score_breakdown,
    }

    if parsed_response is not None:
        graph_projection = parsed_response["graph_projection"]
        graph_validation = validate_graph_projection(fixture, graph_projection)
        commit_type = choose_commit_type(fixture, graph_projection)
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
        if result.trace.get("semantic_checks"):
            lines.append(f"- semantic checks: `{result.trace['semantic_checks']}`")
        lines.append("")
    return "\n".join(lines)


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

    results: List[ArmResult] = []
    if args.arm in {"baseline", "both"}:
        results.append(run_arm_baseline(fixture, args.provider, args.model))
    if args.arm in {"middle", "both"}:
        results.append(
            run_arm_middle(
                fixture,
                args.provider,
                args.model,
                args.ablation,
                use_inferred_concerns=args.use_inferred_concerns,
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
                    )
                )

    dump_text(output_dir / "fixture-path.txt", str(fixture_path))
    dump_json(output_dir / "fixture-snapshot.json", fixture)

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

    summary = build_summary(fixture_path, args.provider, args.model, results)
    dump_text(output_dir / "summary.md", summary)
    print(output_dir)
    return 0


if __name__ == "__main__":
    sys.exit(main())
