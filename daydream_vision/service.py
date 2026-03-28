from __future__ import annotations

import importlib.metadata
import json
import mimetypes
import re
import time
import zipfile
from pathlib import Path
from typing import Any, Dict, List, Optional

try:
    from google.genai import types as gtypes  # type: ignore
except Exception:  # pragma: no cover
    gtypes = None  # type: ignore[assignment]

try:
    from PIL import Image
except Exception:  # pragma: no cover
    Image = None  # type: ignore[assignment]

from .anchor_spec import format_anchor_spec, merge_anchor_spec
from .paths import dated_output_dir, path_record, prompt_stem, resolve_path, slugify
from .vendor import fal_min
from .vendor import replicate_min
from .vendor.gemini_min import (
    cleanup_uploaded,
    generate_image_edits,
    generate_text,
    init_client,
    upload_and_poll_video,
)

try:
    from .vendor.gemini_image import generate_images
except Exception:  # pragma: no cover
    generate_images = None  # type: ignore[assignment]


FAL_QWEN_MODEL = "fal-ai/qwen-image"
GRAFFITO_QWEN_MODEL = "qwen/qwen-image"
GRAFFITO_QWEN_LORA_URL = (
    "https://huggingface.co/davidrd123/graffito_synthetic_qwen/resolve/main/"
    "pytorch_lora_weights.safetensors"
)


def _google_genai_version() -> str:
    try:
        return importlib.metadata.version("google-genai")
    except Exception:
        return "unknown"


def _replicate_version() -> str:
    try:
        return importlib.metadata.version("replicate")
    except Exception:
        return "unknown"


def _fal_version() -> str:
    try:
        return importlib.metadata.version("fal-client")
    except Exception:
        return "unknown"


def _supports_image_config() -> bool:
    return bool(gtypes is not None and hasattr(gtypes, "ImageConfig"))


def _select_image_provider(*, model: str, provider: str = "auto") -> str:
    requested = (provider or "auto").strip().lower()
    if requested in {"gemini", "replicate", "fal"}:
        return requested
    normalized_model = (model or "").strip().lower()
    if normalized_model.startswith("gemini-"):
        return "gemini"
    if normalized_model.startswith("fal-ai/"):
        return "fal"
    if "/" in normalized_model:
        return "replicate"
    return "gemini"


def apply_edit_presets(
    *,
    graffito: bool = False,
    provider: str = "auto",
    model: str,
    lora_weights: Optional[str],
) -> tuple[str, Optional[str]]:
    if not graffito:
        return model, lora_weights
    selected_provider = _select_image_provider(model=model, provider=provider)
    graffito_model = FAL_QWEN_MODEL if selected_provider == "fal" else GRAFFITO_QWEN_MODEL
    return graffito_model, GRAFFITO_QWEN_LORA_URL


def _has_replicate_image_options(*, seed: Optional[int], guidance: Optional[float], strength: Optional[float], go_fast: Optional[bool], lora_scale: Optional[float], lora_weights: Optional[str], output_format: Optional[str], enhance_prompt: Optional[bool], output_quality: Optional[int], negative_prompt: Optional[str], extra_lora_scale: Optional[List[float]], extra_lora_weights: Optional[List[str]], num_inference_steps: Optional[int], disable_safety_checker: Optional[bool]) -> bool:
    return any(
        value is not None and value != []
        for value in (
            seed,
            guidance,
            strength,
            go_fast,
            lora_scale,
            lora_weights,
            output_format,
            enhance_prompt,
            output_quality,
            negative_prompt,
            extra_lora_scale,
            extra_lora_weights,
            num_inference_steps,
            disable_safety_checker,
        )
    )


def _record_output_file(path: Path, *, root: Path) -> Dict[str, Any]:
    metadata = path_record(path, root=root)
    mime_type, _ = mimetypes.guess_type(str(path))
    if Image is not None and mime_type and mime_type.startswith("image/"):
        try:
            with Image.open(path) as img:  # type: ignore[call-arg]
                metadata.update(_img_size(img))
        except Exception:
            pass
    return metadata


def _build_qwen_image_params(
    *,
    prompt: str,
    has_input_image: bool,
    seed: Optional[int],
    guidance: Optional[float],
    strength: Optional[float],
    go_fast: Optional[bool],
    aspect_ratio: Optional[str],
    image_size: Optional[str],
    lora_scale: Optional[float],
    lora_weights: Optional[str],
    output_format: Optional[str],
    enhance_prompt: Optional[bool],
    output_quality: Optional[int],
    negative_prompt: Optional[str],
    extra_lora_scale: Optional[List[float]],
    extra_lora_weights: Optional[List[str]],
    num_inference_steps: Optional[int],
    disable_safety_checker: Optional[bool],
) -> Dict[str, Any]:
    if extra_lora_scale and extra_lora_weights and len(extra_lora_scale) != len(extra_lora_weights):
        raise RuntimeError("extra_lora_scale must match extra_lora_weights length")

    params: Dict[str, Any] = {
        "prompt": prompt,
        "go_fast": True if go_fast is None else bool(go_fast),
        "guidance": 3 if guidance is None else float(guidance),
        "aspect_ratio": aspect_ratio or "16:9",
        "image_size": image_size or "optimize_for_quality",
        "lora_scale": 1 if lora_scale is None else float(lora_scale),
        "output_format": output_format or "png",
        "enhance_prompt": False if enhance_prompt is None else bool(enhance_prompt),
        "output_quality": 80 if output_quality is None else int(output_quality),
        "negative_prompt": " " if negative_prompt is None else str(negative_prompt),
        "num_inference_steps": 30 if num_inference_steps is None else int(num_inference_steps),
        "disable_safety_checker": False if disable_safety_checker is None else bool(disable_safety_checker),
    }

    if seed is not None:
        params["seed"] = int(seed)
    if has_input_image:
        params["strength"] = 0.9 if strength is None else float(strength)
    if lora_weights:
        params["lora_weights"] = lora_weights
    if extra_lora_weights:
        params["extra_lora_weights"] = extra_lora_weights
    if extra_lora_scale:
        params["extra_lora_scale"] = [float(value) for value in extra_lora_scale]
    return params


_FAL_IMAGE_SIZE_ENUMS = {
    "square_hd",
    "square",
    "portrait_4_3",
    "portrait_16_9",
    "landscape_4_3",
    "landscape_16_9",
}

_FAL_IMAGE_SIZE_FROM_ASPECT_RATIO = {
    "1:1": "square_hd",
    "4:3": "landscape_4_3",
    "3:4": "portrait_4_3",
    "16:9": "landscape_16_9",
    "9:16": "portrait_16_9",
}


def _resolve_fal_image_size(
    *,
    aspect_ratio: Optional[str],
    image_size: Optional[str],
) -> Optional[str]:
    if image_size and image_size in _FAL_IMAGE_SIZE_ENUMS:
        return image_size
    if image_size and image_size not in {"optimize_for_quality", "optimize_for_speed"}:
        raise RuntimeError(
            "Fal image_size must be one of square_hd, square, portrait_4_3, portrait_16_9, landscape_4_3, or landscape_16_9"
        )
    if aspect_ratio:
        mapped = _FAL_IMAGE_SIZE_FROM_ASPECT_RATIO.get(aspect_ratio)
        if mapped:
            return mapped
        raise RuntimeError(
            "Fal Qwen image currently supports aspect ratios 1:1, 4:3, 3:4, 16:9, and 9:16"
        )
    return None


def _normalize_fal_output_format(output_format: Optional[str]) -> str:
    if output_format in {None, "", "png"}:
        return "png"
    if output_format == "jpg":
        return "jpeg"
    if output_format == "jpeg":
        return "jpeg"
    raise RuntimeError("Fal Qwen image supports only png or jpg/jpeg output")


def _build_fal_qwen_params(
    *,
    prompt: str,
    requested_outputs: int,
    seed: Optional[int],
    guidance: Optional[float],
    go_fast: Optional[bool],
    aspect_ratio: Optional[str],
    image_size: Optional[str],
    lora_scale: Optional[float],
    lora_weights: Optional[str],
    output_format: Optional[str],
    negative_prompt: Optional[str],
    extra_lora_scale: Optional[List[float]],
    extra_lora_weights: Optional[List[str]],
    num_inference_steps: Optional[int],
    disable_safety_checker: Optional[bool],
) -> Dict[str, Any]:
    if extra_lora_scale and extra_lora_weights and len(extra_lora_scale) != len(extra_lora_weights):
        raise RuntimeError("extra_lora_scale must match extra_lora_weights length")

    loras: List[Dict[str, Any]] = []
    if lora_weights:
        loras.append({"path": lora_weights, "scale": 1 if lora_scale is None else float(lora_scale)})
    for index, path in enumerate(extra_lora_weights or []):
        scale = 1.0
        if extra_lora_scale and index < len(extra_lora_scale):
            scale = float(extra_lora_scale[index])
        loras.append({"path": path, "scale": scale})
    if len(loras) > 3:
        raise RuntimeError("Fal Qwen image supports at most 3 LoRAs total")

    params: Dict[str, Any] = {
        "prompt": prompt,
        "num_images": int(requested_outputs),
        "guidance_scale": 2.5 if guidance is None else float(guidance),
        "output_format": _normalize_fal_output_format(output_format),
        "negative_prompt": " " if negative_prompt is None else str(negative_prompt),
        "num_inference_steps": 30 if num_inference_steps is None else int(num_inference_steps),
        "enable_safety_checker": True if disable_safety_checker is None else (not bool(disable_safety_checker)),
        # Inference from Fal docs: `regular` is the closest equivalent to a simple speed toggle.
        "acceleration": "regular" if go_fast else "none",
    }
    resolved_image_size = _resolve_fal_image_size(aspect_ratio=aspect_ratio, image_size=image_size)
    if resolved_image_size:
        params["image_size"] = resolved_image_size
    if seed is not None:
        params["seed"] = int(seed)
    if loras:
        params["loras"] = loras
    return params


def _run_fal_image_model(
    *,
    model: str,
    prompt: str,
    resolved_inputs: List[Path],
    requested_outputs: int,
    output_dir: Optional[str],
    label: Optional[str],
    seed: Optional[int],
    guidance: Optional[float],
    go_fast: Optional[bool],
    aspect_ratio: Optional[str],
    image_size: Optional[str],
    lora_scale: Optional[float],
    lora_weights: Optional[str],
    output_format: Optional[str],
    negative_prompt: Optional[str],
    extra_lora_scale: Optional[List[float]],
    extra_lora_weights: Optional[List[str]],
    num_inference_steps: Optional[int],
    disable_safety_checker: Optional[bool],
) -> Dict[str, Any]:
    if resolved_inputs:
        raise RuntimeError(
            "Fal qwen-image is currently wired as text-to-image only in this repo; image input is not implemented yet"
        )

    root, out_dir = dated_output_dir(output_dir)
    stem = slugify(label) if label else prompt_stem(prompt)
    params = _build_fal_qwen_params(
        prompt=prompt,
        requested_outputs=requested_outputs,
        seed=seed,
        guidance=guidance,
        go_fast=go_fast,
        aspect_ratio=aspect_ratio,
        image_size=image_size,
        lora_scale=lora_scale,
        lora_weights=lora_weights,
        output_format=output_format,
        negative_prompt=negative_prompt,
        extra_lora_scale=extra_lora_scale,
        extra_lora_weights=extra_lora_weights,
        num_inference_steps=num_inference_steps,
        disable_safety_checker=disable_safety_checker,
    )
    response = fal_min.generate(
        model_ref=model,
        params=params,
        out_dir=out_dir,
        base_name=f"{stem}_raw",
    )
    if not response.get("success"):
        error = response.get("error") or {}
        message = error.get("message") if isinstance(error, dict) else None
        raise RuntimeError(f"FAL_ERROR: {message or 'unknown error'}")

    raw_outputs = response.get("outputs") or []
    if not raw_outputs:
        raise RuntimeError("NO_IMAGE_RETURNED")

    seq0 = _next_seq(out_dir, stem, suffix=None)
    outputs: List[Dict[str, Any]] = []
    for index, item in enumerate(raw_outputs):
        raw_path = item.get("path") if isinstance(item, dict) else None
        if not isinstance(raw_path, str):
            continue
        source_path = Path(raw_path)
        ext = source_path.suffix.lower() or ".bin"
        target_path = (out_dir / f"{stem}_{seq0 + index:03d}{ext}").resolve()
        source_path.replace(target_path)
        metadata = _record_output_file(target_path, root=root)
        if isinstance(item, dict):
            if item.get("width") is not None:
                metadata["width"] = item.get("width")
            if item.get("height") is not None:
                metadata["height"] = item.get("height")
            if item.get("content_type") is not None:
                metadata["contentType"] = item.get("content_type")
        outputs.append(metadata)

    return {
        "command": "edit",
        "provider": "fal",
        "prompt": prompt,
        "model": model,
        "input_count": 0,
        "inputs": [],
        "requested_outputs": requested_outputs,
        "max_attempts": 1,
        "attempts": 1,
        "output_count": len(outputs),
        "outputRoot": str(root),
        "outputDir": str(out_dir),
        "sdk": {"falClientVersion": _fal_version()},
        "fal": {
            "params": params,
            "metrics": response.get("metrics") or {},
        },
        "outputs": outputs,
    }


def _run_replicate_image_model(
    *,
    model: str,
    prompt: str,
    resolved_inputs: List[Path],
    requested_outputs: int,
    output_dir: Optional[str],
    label: Optional[str],
    seed: Optional[int],
    guidance: Optional[float],
    strength: Optional[float],
    go_fast: Optional[bool],
    aspect_ratio: Optional[str],
    image_size: Optional[str],
    lora_scale: Optional[float],
    lora_weights: Optional[str],
    output_format: Optional[str],
    enhance_prompt: Optional[bool],
    output_quality: Optional[int],
    negative_prompt: Optional[str],
    extra_lora_scale: Optional[List[float]],
    extra_lora_weights: Optional[List[str]],
    num_inference_steps: Optional[int],
    disable_safety_checker: Optional[bool],
) -> Dict[str, Any]:
    if len(resolved_inputs) > 1:
        raise RuntimeError("Replicate image models currently support at most one input image")

    root, out_dir = dated_output_dir(output_dir)
    stem = slugify(label) if label else prompt_stem(prompt)
    seq0 = _next_seq(out_dir, stem, suffix=None)
    outputs: List[Dict[str, Any]] = []
    run_metrics: List[Dict[str, Any]] = []

    for request_index in range(requested_outputs):
        effective_seed = None if seed is None else int(seed) + request_index
        params = _build_qwen_image_params(
            prompt=prompt,
            has_input_image=bool(resolved_inputs),
            seed=effective_seed,
            guidance=guidance,
            strength=strength,
            go_fast=go_fast,
            aspect_ratio=aspect_ratio,
            image_size=image_size,
            lora_scale=lora_scale,
            lora_weights=lora_weights,
            output_format=output_format,
            enhance_prompt=enhance_prompt,
            output_quality=output_quality,
            negative_prompt=negative_prompt,
            extra_lora_scale=extra_lora_scale,
            extra_lora_weights=extra_lora_weights,
            num_inference_steps=num_inference_steps,
            disable_safety_checker=disable_safety_checker,
        )
        raw_base = f"{stem}_raw_{seq0 + request_index:03d}"
        if resolved_inputs:
            response = replicate_min.edit(
                model_ref=model,
                image=resolved_inputs[0],
                params=params,
                out_dir=out_dir,
                base_name=raw_base,
                timeout_s=900,
            )
        else:
            response = replicate_min.generate(
                model_ref=model,
                params=params,
                out_dir=out_dir,
                base_name=raw_base,
                timeout_s=900,
            )

        if not response.get("success"):
            error = response.get("error") or {}
            message = error.get("message") if isinstance(error, dict) else None
            raise RuntimeError(f"REPLICATE_ERROR: {message or 'unknown error'}")

        raw_outputs = response.get("outputs") or []
        if not raw_outputs:
            raise RuntimeError("NO_IMAGE_RETURNED")

        run_metrics.append({
            "requestIndex": request_index + 1,
            "seed": effective_seed,
            "metrics": response.get("metrics") or {},
            "coldStart": response.get("cold_start"),
        })

        for subindex, item in enumerate(raw_outputs, start=1):
            raw_path = item.get("path") if isinstance(item, dict) else None
            if not isinstance(raw_path, str):
                continue
            source_path = Path(raw_path)
            ext = source_path.suffix.lower() or ".bin"
            if len(raw_outputs) == 1:
                filename = f"{stem}_{seq0 + len(outputs):03d}{ext}"
            else:
                filename = f"{stem}_{seq0 + request_index:03d}_{subindex:02d}{ext}"
            target_path = (out_dir / filename).resolve()
            source_path.replace(target_path)
            metadata = _record_output_file(target_path, root=root)
            metadata["seed"] = effective_seed
            outputs.append(metadata)

    return {
        "command": "edit",
        "provider": "replicate",
        "prompt": prompt,
        "model": model,
        "input_count": len(resolved_inputs),
        "inputs": [path_record(path) for path in resolved_inputs],
        "requested_outputs": requested_outputs,
        "max_attempts": requested_outputs,
        "attempts": requested_outputs,
        "output_count": len(outputs),
        "outputRoot": str(root),
        "outputDir": str(out_dir),
        "sdk": {"replicateVersion": _replicate_version()},
        "replicate": {
            "params": _build_qwen_image_params(
                prompt=prompt,
                has_input_image=bool(resolved_inputs),
                seed=seed,
                guidance=guidance,
                strength=strength,
                go_fast=go_fast,
                aspect_ratio=aspect_ratio,
                image_size=image_size,
                lora_scale=lora_scale,
                lora_weights=lora_weights,
                output_format=output_format,
                enhance_prompt=enhance_prompt,
                output_quality=output_quality,
                negative_prompt=negative_prompt,
                extra_lora_scale=extra_lora_scale,
                extra_lora_weights=extra_lora_weights,
                num_inference_steps=num_inference_steps,
                disable_safety_checker=disable_safety_checker,
            ),
            "runs": run_metrics,
        },
        "outputs": outputs,
    }


def _require_image_config_support(*, aspect_ratio: Optional[str], image_size: Optional[str]) -> None:
    if not aspect_ratio and not image_size:
        return
    if _supports_image_config():
        return
    raise RuntimeError(
        "IMAGE_CONFIG_UNSUPPORTED: Installed google-genai "
        f"{_google_genai_version()} does not expose types.ImageConfig. "
        "Upgrade the runtime to google-genai>=1.49.0 and restart your shell."
    )


def _img_size(img: Any) -> Dict[str, int]:
    try:
        width, height = img.size  # type: ignore[attr-defined]
        return {"width": int(width), "height": int(height)}
    except Exception:
        return {"width": 0, "height": 0}


def _next_seq(out_dir: Path, stem: str, *, suffix: Optional[str] = ".png") -> int:
    max_value = 0
    pattern = f"{stem}_*{suffix}" if suffix else f"{stem}_*"
    for entry in out_dir.glob(pattern):
        name = entry.stem
        if not name.startswith(f"{stem}_"):
            continue
        seq_part = name[len(stem) + 1 :]
        try:
            max_value = max(max_value, int(seq_part))
        except Exception:
            continue
    return max_value + 1


def _compose_anchor_generation_prompt(
    *,
    prompt: str,
    anchor_spec: Optional[dict[str, Any]],
) -> str:
    spec_text = format_anchor_spec(anchor_spec, fallback_brief=prompt)
    lines = [
        "Create a single visual anchor image.",
    ]
    if spec_text:
        lines.append(spec_text)
    if prompt.strip() and prompt.strip() != (anchor_spec or {}).get("brief"):
        lines.append(f"Focus request: {prompt.strip()}")
    lines.append("Return one coherent image, not a collage or multi-panel layout.")
    return "\n".join(lines)


def _compose_anchor_refinement_prompt(
    *,
    prompt: str,
    anchor_spec: Optional[dict[str, Any]],
    base_prompt: Optional[str],
) -> str:
    spec_text = format_anchor_spec(anchor_spec, fallback_brief=base_prompt)
    lines = [
        "Edit the supplied source image while preserving the established identity and continuity.",
    ]
    if spec_text:
        lines.append(spec_text)
    lines.append(f"Refinement instruction: {prompt.strip()}")
    lines.append("Return one coherent updated image.")
    return "\n".join(lines)


def _compose_anchor_analysis_prompt(
    *,
    anchor_spec: Optional[dict[str, Any]],
    base_prompt: Optional[str],
    refinement_prompt: Optional[str] = None,
) -> str:
    spec_text = format_anchor_spec(anchor_spec, fallback_brief=base_prompt)
    lines = [
        "Review this image as a visual anchor candidate.",
    ]
    if spec_text:
        lines.append(spec_text)
    elif base_prompt:
        lines.append(f"Brief: {base_prompt}")
    if refinement_prompt:
        lines.append(f"Refinement instruction: {refinement_prompt}")
    lines.extend(
        [
            "Return JSON only with exactly these keys:",
            "{",
            '  "description": "short visual summary",',
            '  "match": "strong | partial | weak",',
            '  "identityFit": "strong | partial | weak | not_applicable",',
            '  "paletteFit": "strong | partial | weak | not_applicable",',
            '  "constraintViolations": ["brief strings"],',
            '  "keepOrRework": "keep | refine | reject",',
            '  "suggestedRefinement": "empty string if none",',
            '  "notes": "short justification"',
            "}",
            "Use empty array or empty string when there is nothing to add.",
        ]
    )
    return "\n".join(lines)


def _compose_anchor_compare_prompt(
    *,
    anchor_spec: Optional[dict[str, Any]],
    base_prompt: Optional[str],
    candidate_count: int,
) -> str:
    spec_text = format_anchor_spec(anchor_spec, fallback_brief=base_prompt)
    lines = [
        f"Review {candidate_count} candidate images for the same visual anchor.",
        "The images are provided in order. Candidate 1 is the first image, Candidate 2 is the second image, and so on.",
    ]
    if spec_text:
        lines.append(spec_text)
    elif base_prompt:
        lines.append(f"Brief: {base_prompt}")
    lines.extend(
        [
            "Use this scoring rubric for overallScore (0-100):",
            "- brief and identity fit: 35",
            "- explicit constraints and continuity fit: 35",
            "- palette and style fit: 20",
            "- composition and clarity: 10",
            "Treat 'Must include', 'Avoid', and 'Consistency notes' as hard evaluation criteria, not soft preferences.",
            "If a candidate misses an explicit requirement or violates an avoid constraint, record that in constraintViolations and penalize overallScore heavily.",
            "Prefer the most constraint-complete image when two candidates are otherwise similar.",
            "Return JSON only with exactly these keys:",
            "{",
            '  "bestCandidateIndex": 1,',
            '  "ranking": [1, 2, 3],',
            '  "overallNotes": "short comparison summary",',
            '  "candidates": [',
            "    {",
            '      "candidateIndex": 1,',
            '      "description": "short visual summary",',
            '      "match": "strong | partial | weak",',
            '      "identityFit": "strong | partial | weak | not_applicable",',
            '      "paletteFit": "strong | partial | weak | not_applicable",',
            '      "constraintViolations": ["brief strings"],',
            '      "keepOrRework": "keep | refine | reject",',
            '      "suggestedRefinement": "empty string if none",',
            '      "strengths": ["brief strings"],',
            '      "issues": ["brief strings"],',
            '      "overallScore": 0,',
            '      "notes": "short justification"',
            "    }",
            "  ]",
            "}",
            "Every candidate must appear exactly once in the candidates array.",
            "Use empty arrays or empty strings when there is nothing to add.",
        ]
    )
    return "\n".join(lines)


def _extract_json_object(text: str) -> Optional[dict[str, Any]]:
    if not text.strip():
        return None

    fenced = re.search(r"```(?:json)?\s*(\{.*\})\s*```", text, flags=re.DOTALL)
    candidates = [fenced.group(1)] if fenced else []

    stripped = text.strip()
    if stripped.startswith("{") and stripped.endswith("}"):
        candidates.append(stripped)

    first = text.find("{")
    last = text.rfind("}")
    if first != -1 and last != -1 and last > first:
        candidates.append(text[first : last + 1])

    for candidate in candidates:
        try:
            payload = json.loads(candidate)
        except Exception:
            continue
        if isinstance(payload, dict):
            return payload
    return None


def _clean_eval_text(value: Any) -> str:
    if not isinstance(value, str):
        return ""
    return " ".join(value.split()).strip()


def _clean_eval_enum(value: Any, allowed: set[str], default: str) -> str:
    if isinstance(value, str):
        normalized = value.strip().lower().replace("-", "_").replace(" ", "_")
        if normalized in allowed:
            return normalized
    return default


def _clean_eval_list(value: Any) -> list[str]:
    if isinstance(value, str):
        value = [value]
    if not isinstance(value, list):
        return []
    cleaned: list[str] = []
    seen: set[str] = set()
    for item in value:
        if not isinstance(item, str):
            continue
        normalized = " ".join(item.split()).strip()
        if not normalized:
            continue
        key = normalized.casefold()
        if key in seen:
            continue
        seen.add(key)
        cleaned.append(normalized)
    return cleaned


def _parse_labeled_line(text: str, label: str) -> str:
    match = re.search(rf"^{re.escape(label)}:\s*(.+)$", text, flags=re.MULTILINE)
    if not match:
        return ""
    return _clean_eval_text(match.group(1))


def _structured_anchor_analysis(text: str) -> dict[str, Any]:
    payload = _extract_json_object(text) or {}

    description = _clean_eval_text(payload.get("description")) or _parse_labeled_line(text, "Description")
    match = _clean_eval_enum(payload.get("match") or _parse_labeled_line(text, "Match"), {"strong", "partial", "weak"}, "partial")
    identity_fit = _clean_eval_enum(
        payload.get("identityFit"),
        {"strong", "partial", "weak", "not_applicable"},
        "not_applicable",
    )
    palette_fit = _clean_eval_enum(
        payload.get("paletteFit"),
        {"strong", "partial", "weak", "not_applicable"},
        "not_applicable",
    )
    constraint_violations = _clean_eval_list(payload.get("constraintViolations"))
    suggested_refinement = _clean_eval_text(payload.get("suggestedRefinement"))
    notes = _clean_eval_text(payload.get("notes")) or _parse_labeled_line(text, "Notes")
    keep_or_rework = _clean_eval_enum(
        payload.get("keepOrRework"),
        {"keep", "refine", "reject"},
        ("keep" if match == "strong" else ("refine" if match == "partial" else "reject")),
    )

    if not suggested_refinement and keep_or_rework == "refine":
        suggested_refinement = notes

    return {
        "description": description,
        "match": match,
        "identityFit": identity_fit,
        "paletteFit": palette_fit,
        "constraintViolations": constraint_violations,
        "keepOrRework": keep_or_rework,
        "suggestedRefinement": suggested_refinement,
        "notes": notes,
    }


def _clean_eval_score(value: Any, default: int) -> int:
    try:
        score = int(round(float(value)))
    except Exception:
        return default
    return max(0, min(100, score))


def _default_score_for_match(match: str) -> int:
    return {"strong": 90, "partial": 60, "weak": 30}.get(match, 60)


def _sanitize_comparison_candidate(payload: dict[str, Any], *, candidate_index: int) -> dict[str, Any]:
    match = _clean_eval_enum(payload.get("match"), {"strong", "partial", "weak"}, "partial")
    keep_or_rework = _clean_eval_enum(
        payload.get("keepOrRework"),
        {"keep", "refine", "reject"},
        ("keep" if match == "strong" else ("refine" if match == "partial" else "reject")),
    )
    structured = {
        "candidateIndex": candidate_index,
        "description": _clean_eval_text(payload.get("description")),
        "match": match,
        "identityFit": _clean_eval_enum(
            payload.get("identityFit"),
            {"strong", "partial", "weak", "not_applicable"},
            "not_applicable",
        ),
        "paletteFit": _clean_eval_enum(
            payload.get("paletteFit"),
            {"strong", "partial", "weak", "not_applicable"},
            "not_applicable",
        ),
        "constraintViolations": _clean_eval_list(payload.get("constraintViolations")),
        "keepOrRework": keep_or_rework,
        "suggestedRefinement": _clean_eval_text(payload.get("suggestedRefinement")),
        "strengths": _clean_eval_list(payload.get("strengths")),
        "issues": _clean_eval_list(payload.get("issues")),
        "overallScore": _clean_eval_score(payload.get("overallScore"), _default_score_for_match(match)),
        "notes": _clean_eval_text(payload.get("notes")),
    }
    if not structured["suggestedRefinement"] and structured["keepOrRework"] == "refine":
        structured["suggestedRefinement"] = structured["notes"]
    return structured


def _structured_anchor_comparison(text: str, *, candidate_count: int) -> dict[str, Any]:
    payload = _extract_json_object(text) or {}
    raw_candidates = payload.get("candidates")
    candidate_map: dict[int, dict[str, Any]] = {}
    if isinstance(raw_candidates, list):
        for item in raw_candidates:
            if not isinstance(item, dict):
                continue
            raw_index = item.get("candidateIndex")
            try:
                candidate_index = int(raw_index)
            except Exception:
                continue
            if candidate_index < 1 or candidate_index > candidate_count:
                continue
            candidate_map[candidate_index] = _sanitize_comparison_candidate(item, candidate_index=candidate_index)

    candidates: list[dict[str, Any]] = []
    for index in range(1, candidate_count + 1):
        candidates.append(candidate_map.get(index) or _sanitize_comparison_candidate({}, candidate_index=index))

    ranking_raw = payload.get("ranking")
    ranking: list[int] = []
    if isinstance(ranking_raw, list):
        seen: set[int] = set()
        for item in ranking_raw:
            try:
                candidate_index = int(item)
            except Exception:
                continue
            if candidate_index < 1 or candidate_index > candidate_count or candidate_index in seen:
                continue
            seen.add(candidate_index)
            ranking.append(candidate_index)
    if len(ranking) != candidate_count:
        ranked = sorted(candidates, key=lambda item: item.get("overallScore", 0), reverse=True)
        ranking = [int(item["candidateIndex"]) for item in ranked]

    best_candidate_index = payload.get("bestCandidateIndex")
    try:
        best_candidate = int(best_candidate_index)
    except Exception:
        best_candidate = ranking[0] if ranking else 1
    if best_candidate not in ranking:
        best_candidate = ranking[0] if ranking else 1

    return {
        "bestCandidateIndex": best_candidate,
        "ranking": ranking,
        "overallNotes": _clean_eval_text(payload.get("overallNotes")),
        "candidates": candidates,
    }


def _evaluate_anchor_candidate(
    *,
    output_path: str,
    analyze_model: str,
    analyze_prompt: str,
    analyze_system_prompt: str,
) -> dict[str, Any]:
    analysis = analyze_media(
        prompt=analyze_prompt,
        media_paths=[output_path],
        model=analyze_model,
        temperature=0.2,
        system_prompt=analyze_system_prompt,
    )
    return {
        "model": analysis["model"],
        "prompt": analysis["prompt"],
        "text": analysis["text"],
        "structured": _structured_anchor_analysis(analysis["text"]),
    }


def compare_media_candidates(
    *,
    prompt: str,
    media_paths: List[str],
    anchor_spec: Optional[dict[str, Any]] = None,
    model: str = "gemini-3.1-flash-lite-preview",
    system_prompt: str = "",
    comparison_prompt: Optional[str] = None,
) -> Dict[str, Any]:
    if not prompt.strip():
        raise RuntimeError("prompt is required")
    if len(media_paths) < 1:
        raise RuntimeError("media_paths is required")

    normalized_spec = merge_anchor_spec(
        anchor_spec,
        None,
        fallback_brief=prompt,
    )
    resolved_media = [resolve_path(path) for path in media_paths]
    for path in resolved_media:
        if not path.is_file():
            raise RuntimeError(f"UNSUPPORTED_MEDIA: file not found: {path}")

    analysis_prompt = comparison_prompt or _compose_anchor_compare_prompt(
        anchor_spec=normalized_spec,
        base_prompt=prompt,
        candidate_count=len(resolved_media),
    )
    analysis = analyze_media(
        prompt=analysis_prompt,
        media_paths=[str(path) for path in resolved_media],
        model=model,
        temperature=0.2,
        system_prompt=system_prompt,
    )
    structured = _structured_anchor_comparison(analysis["text"], candidate_count=len(resolved_media))

    candidate_records: list[dict[str, Any]] = []
    structured_by_index = {
        int(item["candidateIndex"]): item
        for item in structured.get("candidates", [])
        if isinstance(item, dict) and isinstance(item.get("candidateIndex"), int)
    }
    for index, path in enumerate(resolved_media, start=1):
        candidate_records.append(
            {
                "candidateIndex": index,
                "media": path_record(path),
                "comparison": structured_by_index.get(index),
            }
        )

    return {
        "command": "compare",
        "prompt": prompt,
        "anchorSpec": normalized_spec,
        "model": analysis["model"],
        "comparisonPrompt": analysis["prompt"],
        "text": analysis["text"],
        "comparison": structured,
        "candidateCount": len(candidate_records),
        "candidates": candidate_records,
    }


def edit_image(
    *,
    prompt: str,
    image_paths: Optional[List[str]] = None,
    num_outputs: int = 1,
    output_dir: Optional[str] = None,
    label: Optional[str] = None,
    model: str = "gemini-3.1-flash-image-preview",
    provider: str = "auto",
    temperature: float = 0.7,
    system_prompt: str = "",
    aspect_ratio: Optional[str] = None,
    image_size: Optional[str] = None,
    seed: Optional[int] = None,
    guidance: Optional[float] = None,
    strength: Optional[float] = None,
    go_fast: Optional[bool] = None,
    lora_scale: Optional[float] = None,
    lora_weights: Optional[str] = None,
    output_format: Optional[str] = None,
    enhance_prompt: Optional[bool] = None,
    output_quality: Optional[int] = None,
    negative_prompt: Optional[str] = None,
    extra_lora_scale: Optional[List[float]] = None,
    extra_lora_weights: Optional[List[str]] = None,
    num_inference_steps: Optional[int] = None,
    disable_safety_checker: Optional[bool] = None,
) -> Dict[str, Any]:
    if not prompt.strip():
        raise RuntimeError("prompt is required")
    if num_outputs < 1 or num_outputs > 4:
        raise RuntimeError("num_outputs must be between 1 and 4")
    if Image is None:
        raise RuntimeError("Pillow not installed. `pip install pillow`.")

    input_paths = image_paths or []
    resolved_inputs = [resolve_path(path) for path in input_paths]
    for path in resolved_inputs:
        if not path.is_file():
            raise RuntimeError(f"IMAGE_NOT_FOUND: {path}")

    selected_provider = _select_image_provider(model=model, provider=provider)
    normalized_model = (model or "").strip().lower()
    if selected_provider == "fal" and not normalized_model.startswith("fal-ai/"):
        raise RuntimeError("Fal provider requires a fal-ai/... model ref")
    if selected_provider == "replicate" and "/" not in normalized_model:
        raise RuntimeError("Replicate provider requires a provider/model style model ref")
    if selected_provider == "replicate":
        return _run_replicate_image_model(
            model=model,
            prompt=prompt,
            resolved_inputs=resolved_inputs,
            requested_outputs=num_outputs,
            output_dir=output_dir,
            label=label,
            seed=seed,
            guidance=guidance,
            strength=strength,
            go_fast=go_fast,
            aspect_ratio=aspect_ratio,
            image_size=image_size,
            lora_scale=lora_scale,
            lora_weights=lora_weights,
            output_format=output_format,
            enhance_prompt=enhance_prompt,
            output_quality=output_quality,
            negative_prompt=negative_prompt,
            extra_lora_scale=extra_lora_scale,
            extra_lora_weights=extra_lora_weights,
            num_inference_steps=num_inference_steps,
            disable_safety_checker=disable_safety_checker,
        )
    if selected_provider == "fal":
        if strength is not None:
            raise RuntimeError("Fal qwen-image does not currently support local img2img strength in this repo")
        if enhance_prompt is not None:
            raise RuntimeError("Fal qwen-image does not support --enhance-prompt in this repo")
        if output_quality is not None:
            raise RuntimeError("Fal qwen-image does not support --output-quality in this repo")
        return _run_fal_image_model(
            model=model,
            prompt=prompt,
            resolved_inputs=resolved_inputs,
            requested_outputs=num_outputs,
            output_dir=output_dir,
            label=label,
            seed=seed,
            guidance=guidance,
            go_fast=go_fast,
            aspect_ratio=aspect_ratio,
            image_size=image_size,
            lora_scale=lora_scale,
            lora_weights=lora_weights,
            output_format=output_format,
            negative_prompt=negative_prompt,
            extra_lora_scale=extra_lora_scale,
            extra_lora_weights=extra_lora_weights,
            num_inference_steps=num_inference_steps,
            disable_safety_checker=disable_safety_checker,
        )

    if _has_replicate_image_options(
        seed=seed,
        guidance=guidance,
        strength=strength,
        go_fast=go_fast,
        lora_scale=lora_scale,
        lora_weights=lora_weights,
        output_format=output_format,
        enhance_prompt=enhance_prompt,
        output_quality=output_quality,
        negative_prompt=negative_prompt,
        extra_lora_scale=extra_lora_scale,
        extra_lora_weights=extra_lora_weights,
        num_inference_steps=num_inference_steps,
        disable_safety_checker=disable_safety_checker,
    ):
        raise RuntimeError(
            "Non-Gemini image options were supplied while using the Gemini image path"
        )

    _require_image_config_support(aspect_ratio=aspect_ratio, image_size=image_size)
    root, out_dir = dated_output_dir(output_dir)

    contents: List[Any] = [prompt]
    if generate_images is None:
        for path in resolved_inputs:
            img = Image.open(path).convert("RGB")
            contents.append(img)

    client = init_client()
    requested_outputs = num_outputs
    max_attempts = num_outputs
    images: List[Any] = []
    attempts = 0

    while len(images) < requested_outputs and attempts < max_attempts:
        attempts += 1
        if generate_images is not None:
            response = generate_images(
                client=client,
                model=model,
                user_prompt=prompt,
                system_prompt=system_prompt,
                image_paths=resolved_inputs,
                temperature=temperature,
                aspect_ratio=aspect_ratio,
                image_size=image_size,
            )
            images.extend(response.get("images") or [])
        else:
            images.extend(
                generate_image_edits(
                    client,
                    model=model,
                    contents=contents,
                    system_prompt=system_prompt,
                    temperature=temperature,
                )
            )

    images = images[:requested_outputs]
    if not images:
        raise RuntimeError("NO_IMAGE_RETURNED")

    stem = slugify(label) if label else prompt_stem(prompt)
    seq0 = _next_seq(out_dir, stem)
    outputs: List[Dict[str, Any]] = []
    for index, img in enumerate(images):
        filename = f"{stem}_{seq0 + index:03d}.png"
        output_path = (out_dir / filename).resolve()
        img.save(str(output_path), format="PNG")
        metadata = path_record(output_path, root=root)
        metadata.update(_img_size(img))
        outputs.append(metadata)

    return {
        "command": "edit",
        "provider": "gemini",
        "prompt": prompt,
        "model": model,
        "input_count": len(resolved_inputs),
        "inputs": [path_record(path) for path in resolved_inputs],
        "requested_outputs": requested_outputs,
        "max_attempts": max_attempts,
        "attempts": attempts,
        "output_count": len(outputs),
        "outputRoot": str(root),
        "outputDir": str(out_dir),
        "sdk": {
            "googleGenaiVersion": _google_genai_version(),
            "imageConfigSupported": _supports_image_config(),
        },
        "outputs": outputs,
    }


def segment_image(
    *,
    image_path: str,
    prompt: str = "person",
    mask_only: bool = True,
    threshold: float = 0.5,
    output_dir: Optional[str] = None,
    label: Optional[str] = None,
) -> Dict[str, Any]:
    if not image_path.strip():
        raise RuntimeError("image_path is required")
    if not prompt.strip():
        raise RuntimeError("prompt is required")
    if Image is None:
        raise RuntimeError("Pillow not installed. `pip install pillow`.")

    try:
        threshold_value = float(threshold)
    except Exception as exc:
        raise RuntimeError("threshold must be a number between 0.0 and 1.0") from exc
    if threshold_value < 0.0 or threshold_value > 1.0:
        raise RuntimeError("threshold must be between 0.0 and 1.0")

    source = resolve_path(image_path)
    if not source.is_file():
        raise RuntimeError(f"IMAGE_NOT_FOUND: {source}")

    root, out_dir = dated_output_dir(output_dir, subdir="segments")
    stem_base = slugify(label) if label else prompt_stem(prompt)
    stem = f"{stem_base}_seg_{int(time.time() * 1000)}"

    response = replicate_min.edit(
        model_ref="mattsays/sam3-image:d73db077226443ba4fafd34e233b3626b552eac2a433f90c7c32a9ac89bd9e72",
        image=source,
        params={
            "prompt": prompt,
            "mask_only": bool(mask_only),
            "threshold": threshold_value,
            "return_zip": True,
            "save_overlay": (not bool(mask_only)),
            "mask_color": "green",
            "mask_opacity": 0.5,
        },
        out_dir=out_dir,
        base_name=f"{stem}_raw",
        timeout_s=600,
    )

    if not response.get("success"):
        error = response.get("error") or {}
        message = error.get("message") if isinstance(error, dict) else None
        raise RuntimeError(f"REPLICATE_ERROR: {message or 'unknown error'}")

    outputs = response.get("outputs") or []
    zip_path: Optional[Path] = None
    for item in outputs:
        if not isinstance(item, dict):
            continue
        candidate = item.get("path")
        if isinstance(candidate, str) and candidate.lower().endswith(".zip"):
            zip_path = Path(candidate)
            break
    if zip_path is None:
        raise RuntimeError("OUTPUT_ZIP_NOT_FOUND")

    overlay_record: Optional[Dict[str, Any]] = None
    mask_paths: List[Path] = []

    with zipfile.ZipFile(str(zip_path), "r") as archive:
        pngs = [name for name in archive.namelist() if name.lower().endswith(".png") and not name.endswith("/")]
        if not pngs:
            raise RuntimeError("NO_MASKS_IN_ZIP")

        overlay_member: Optional[str] = None
        if not mask_only:
            for name in pngs:
                if "overlay" in Path(name).name.lower():
                    overlay_member = name
                    break

        mask_members = [name for name in pngs if name != overlay_member]
        mask_members.sort()

        if overlay_member is not None:
            overlay_path = (out_dir / f"{stem}_overlay.png").resolve()
            overlay_path.write_bytes(archive.read(overlay_member))
            overlay_record = path_record(overlay_path, root=root)
            if Image is not None:
                with Image.open(overlay_path) as overlay_img:  # type: ignore[call-arg]
                    overlay_record.update(_img_size(overlay_img))

        for index, name in enumerate(mask_members, start=1):
            output_path = (out_dir / f"{stem}_mask_{index:03d}.png").resolve()
            output_path.write_bytes(archive.read(name))
            mask_paths.append(output_path)

    try:
        zip_path.unlink()
    except Exception:
        pass

    masks: List[Dict[str, Any]] = []
    for path in mask_paths:
        metadata = path_record(path, root=root)
        if Image is not None:
            with Image.open(path) as mask_img:  # type: ignore[call-arg]
                metadata.update(_img_size(mask_img))
        masks.append(metadata)

    metrics_in = response.get("metrics") if isinstance(response, dict) else None
    metrics: Dict[str, Any] = {"cold_start": response.get("cold_start")}
    if isinstance(metrics_in, dict):
        for key in ("predict_time_s", "download_time_s", "elapsed_s"):
            if key in metrics_in:
                metrics[key] = metrics_in.get(key)

    result: Dict[str, Any] = {
        "command": "segment",
        "prompt": prompt,
        "model": "mattsays/sam3-image",
        "mask_only": bool(mask_only),
        "threshold": threshold_value,
        "input_image": path_record(source),
        "outputRoot": str(root),
        "outputDir": str(out_dir),
        "mask_count": len(masks),
        "masks": masks,
        "metrics": metrics,
    }
    if overlay_record is not None:
        result["overlay"] = overlay_record
    return result


def analyze_media(
    *,
    prompt: str,
    media_paths: List[str],
    model: str = "gemini-3.1-flash-lite-preview",
    temperature: float = 0.7,
    system_prompt: str = "",
    max_tokens: int = 65536,
    fps: Optional[float] = None,
    timeout_s: int = 300,
) -> Dict[str, Any]:
    if not prompt.strip():
        raise RuntimeError("prompt is required")
    if not media_paths:
        raise RuntimeError("media_paths is required")
    if gtypes is None:
        raise RuntimeError("google-genai not installed. `pip install google-genai`.")
    if Image is None:
        raise RuntimeError("Pillow not installed. `pip install pillow`.")

    client = init_client()
    contents: List[Any] = [prompt]
    uploaded: List[Any] = []
    resolved_media = [resolve_path(path) for path in media_paths]

    try:
        for path in resolved_media:
            if not path.is_file():
                raise RuntimeError(f"UNSUPPORTED_MEDIA: file not found: {path}")
            mime_type, _ = mimetypes.guess_type(str(path))
            mime_type = mime_type or ""
            if mime_type.startswith("image/"):
                img = Image.open(path).convert("RGB")
                contents.append(img)
                continue
            if mime_type.startswith("video/"):
                uploaded_file = upload_and_poll_video(client, str(path), timeout_s=timeout_s)
                uploaded.append(uploaded_file)
                part_kwargs: Dict[str, Any] = dict(
                    file_data=gtypes.FileData(file_uri=uploaded_file.uri, mime_type=mime_type or "video/mp4")
                )
                if fps is not None:
                    part_kwargs["video_metadata"] = gtypes.VideoMetadata(
                        fps=int(fps) if fps == int(fps) else fps,
                        start_offset="0s",
                    )
                contents.append(gtypes.Part(**part_kwargs))
                continue
            raise RuntimeError(
                f"UNSUPPORTED_MEDIA: unsupported media type: {mime_type or 'unknown'} for {path}"
            )

        text = generate_text(
            client,
            model=model,
            contents=contents,
            system_prompt=system_prompt,
            max_output_tokens=max_tokens,
            temperature=temperature,
        )
        return {
            "command": "analyze",
            "prompt": prompt,
            "model": model,
            "media_count": len(resolved_media),
            "media": [path_record(path) for path in resolved_media],
            "text": text,
        }
    finally:
        for uploaded_file in uploaded:
            try:
                cleanup_uploaded(client, uploaded_file)
            except Exception:
                pass


def build_visual_anchor(
    *,
    anchor_id: str,
    prompt: str,
    anchor_spec: Optional[dict[str, Any]] = None,
    num_outputs: int = 1,
    output_dir: Optional[str] = None,
    label: Optional[str] = None,
    edit_model: str = "gemini-3.1-flash-image-preview",
    analyze_model: str = "gemini-3.1-flash-lite-preview",
    temperature: float = 0.7,
    system_prompt: str = "",
    analyze_system_prompt: str = "",
    analysis_prompt: Optional[str] = None,
    aspect_ratio: Optional[str] = None,
    image_size: Optional[str] = None,
) -> Dict[str, Any]:
    if not anchor_id.strip():
        raise RuntimeError("anchor_id is required")
    normalized_spec = merge_anchor_spec(
        anchor_spec,
        None,
        fallback_brief=prompt,
        fallback_name=label,
    )
    render_prompt = _compose_anchor_generation_prompt(
        prompt=prompt,
        anchor_spec=normalized_spec,
    )

    edit_result = edit_image(
        prompt=render_prompt,
        image_paths=None,
        num_outputs=num_outputs,
        output_dir=output_dir,
        label=label,
        model=edit_model,
        temperature=temperature,
        system_prompt=system_prompt,
        aspect_ratio=aspect_ratio,
        image_size=image_size,
    )

    generated_outputs = edit_result.get("outputs") or []
    if not isinstance(generated_outputs, list) or not generated_outputs:
        raise RuntimeError("VISUAL_ANCHOR_NO_OUTPUTS")

    analysis_text_prompt = analysis_prompt or _compose_anchor_analysis_prompt(
        anchor_spec=normalized_spec,
        base_prompt=prompt,
    )

    candidates: List[Dict[str, Any]] = []
    for index, output in enumerate(generated_outputs, start=1):
        if not isinstance(output, dict):
            continue
        output_path = output.get("path")
        if not isinstance(output_path, str):
            continue
        analysis = _evaluate_anchor_candidate(
            output_path=output_path,
            analyze_model=analyze_model,
            analyze_prompt=analysis_text_prompt,
            analyze_system_prompt=analyze_system_prompt,
        )
        candidates.append(
            {
                "candidateIndex": index,
                "status": "candidate",
                "image": output,
                "analysis": analysis,
            }
        )

    return {
        "command": "anchor",
        "anchorId": anchor_id,
        "label": label,
        "prompt": prompt,
        "renderPrompt": render_prompt,
        "anchorSpec": normalized_spec,
        "outputRoot": edit_result.get("outputRoot"),
        "outputDir": edit_result.get("outputDir"),
        "editModel": edit_result.get("model"),
        "analyzeModel": analyze_model,
        "candidateCount": len(candidates),
        "outputs": generated_outputs,
        "candidates": candidates,
    }


def refine_visual_anchor(
    *,
    anchor_id: str,
    prompt: str,
    source_image_path: str,
    source_selection: str,
    base_prompt: Optional[str] = None,
    anchor_spec: Optional[dict[str, Any]] = None,
    num_outputs: int = 1,
    output_dir: Optional[str] = None,
    label: Optional[str] = None,
    edit_model: str = "gemini-3.1-flash-image-preview",
    analyze_model: str = "gemini-3.1-flash-lite-preview",
    temperature: float = 0.7,
    system_prompt: str = "",
    analyze_system_prompt: str = "",
    analysis_prompt: Optional[str] = None,
    aspect_ratio: Optional[str] = None,
    image_size: Optional[str] = None,
) -> Dict[str, Any]:
    if not anchor_id.strip():
        raise RuntimeError("anchor_id is required")
    if not prompt.strip():
        raise RuntimeError("prompt is required")
    normalized_spec = merge_anchor_spec(
        anchor_spec,
        None,
        fallback_brief=base_prompt,
        fallback_name=label,
    )
    render_prompt = _compose_anchor_refinement_prompt(
        prompt=prompt,
        anchor_spec=normalized_spec,
        base_prompt=base_prompt,
    )

    edit_result = edit_image(
        prompt=render_prompt,
        image_paths=[source_image_path],
        num_outputs=num_outputs,
        output_dir=output_dir,
        label=label,
        model=edit_model,
        temperature=temperature,
        system_prompt=system_prompt,
        aspect_ratio=aspect_ratio,
        image_size=image_size,
    )

    generated_outputs = edit_result.get("outputs") or []
    if not isinstance(generated_outputs, list) or not generated_outputs:
        raise RuntimeError("VISUAL_ANCHOR_NO_OUTPUTS")

    analysis_text_prompt = analysis_prompt or _compose_anchor_analysis_prompt(
        anchor_spec=normalized_spec,
        base_prompt=base_prompt,
        refinement_prompt=prompt,
    )

    candidates: List[Dict[str, Any]] = []
    for index, output in enumerate(generated_outputs, start=1):
        if not isinstance(output, dict):
            continue
        output_path = output.get("path")
        if not isinstance(output_path, str):
            continue
        analysis = _evaluate_anchor_candidate(
            output_path=output_path,
            analyze_model=analyze_model,
            analyze_prompt=analysis_text_prompt,
            analyze_system_prompt=analyze_system_prompt,
        )
        candidates.append(
            {
                "candidateIndex": index,
                "status": "candidate",
                "image": output,
                "analysis": analysis,
            }
        )

    return {
        "command": "anchor-refine",
        "anchorId": anchor_id,
        "label": label,
        "prompt": prompt,
        "basePrompt": base_prompt,
        "renderPrompt": render_prompt,
        "anchorSpec": normalized_spec,
        "sourceSelection": source_selection,
        "sourceImage": path_record(resolve_path(source_image_path)),
        "outputRoot": edit_result.get("outputRoot"),
        "outputDir": edit_result.get("outputDir"),
        "editModel": edit_result.get("model"),
        "analyzeModel": analyze_model,
        "candidateCount": len(candidates),
        "outputs": generated_outputs,
        "candidates": candidates,
    }
