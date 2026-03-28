"""
Minimal Replicate helper functions for DMPOST MCP tools.

Ported from PromptByAPI:
  /home/davidrd/Projects/Lora/Gens/Source/PromptByAPI/comfy_automation/generation_providers/replicate_min.py

Notes:
- This module is intentionally small and runtime-guarded (Replicate may not be installed).
- Unlike the upstream version, we allow non-image outputs like `.zip` because SAM3 returns a ZIP.
"""

from __future__ import annotations

import os
import time
from pathlib import Path
from typing import Any, Dict, Iterable, List, Optional

try:
    import replicate
except Exception:  # pragma: no cover - runtime import guard
    replicate = None  # type: ignore[assignment]


ImageJobResult = Dict[str, Any]


def _ensure_replicate() -> None:
    if replicate is None:
        raise RuntimeError(
            "replicate package not installed. Add it to your venv: `pip install replicate`"
        )
    # Best-effort: load .env if python-dotenv is available (align with other tools).
    try:
        from dotenv import load_dotenv  # type: ignore

        load_dotenv()
    except Exception:
        pass
    if not os.getenv("REPLICATE_API_TOKEN"):
        raise RuntimeError(
            "REPLICATE_API_TOKEN not set; export it in your environment or .env"
        )


def _get_url(item: Any) -> Optional[str]:
    if hasattr(item, "url") and callable(getattr(item, "url")):
        try:
            return item.url()
        except Exception:
            return None
    if isinstance(item, str):
        return item
    return None


def _read_bytes(item: Any) -> bytes:
    if hasattr(item, "read") and callable(getattr(item, "read")):
        return item.read()
    if isinstance(item, (bytes, bytearray)):
        return bytes(item)
    raise TypeError("Unsupported output item type; expected file-like with read() or bytes")


def _ext_from_url(url: Optional[str]) -> str:
    if not url:
        return ".bin"
    part = url.split("?", 1)[0].rsplit("/", 1)[-1]
    if "." not in part:
        return ".bin"
    ext = "." + part.rsplit(".", 1)[-1].lower()
    # Allow images plus common container outputs (SAM3 returns a ZIP).
    if ext in {".jpg", ".jpeg", ".png", ".webp", ".gif", ".zip"}:
        return ".jpg" if ext == ".jpeg" else ext
    return ".bin"


def _ext_from_bytes(data: bytes) -> str:
    if data.startswith(b"\x89PNG\r\n\x1a\n"):
        return ".png"
    if data.startswith(b"\xff\xd8\xff"):
        return ".jpg"
    if data.startswith((b"GIF87a", b"GIF89a")):
        return ".gif"
    if data.startswith(b"PK\x03\x04"):
        return ".zip"
    if len(data) >= 12 and data[:4] == b"RIFF" and data[8:12] == b"WEBP":
        return ".webp"
    return ".bin"


def _write_outputs(items: Iterable[Any], out_dir: Path, base_name: str) -> List[dict]:
    out_dir.mkdir(parents=True, exist_ok=True)
    outputs: List[dict] = []
    start_dl = time.perf_counter()
    for idx, item in enumerate(items):
        url = _get_url(item)
        ext = _ext_from_url(url)
        data = _read_bytes(item)
        if ext == ".bin":
            ext = _ext_from_bytes(data)
        p = out_dir / f"{base_name}_{idx}{ext}"
        if p.exists():
            p = out_dir / f"{base_name}_{idx}_{int(time.time() * 1000) % 100000000}{ext}"
        p.write_bytes(data)
        outputs.append(
            {
                "path": str(p),
                "url": url,
                "bytes": len(data),
                "expires_at": time.strftime(
                    "%Y-%m-%dT%H:%M:%SZ", time.gmtime(time.time() + 3600)
                ),
            }
        )
    dl_time = time.perf_counter() - start_dl
    if outputs:
        outputs[0].setdefault("_metrics", {})
        outputs[0]["_metrics"]["download_time_s"] = dl_time
    return outputs


def generate(
    *,
    model_ref: str,
    params: dict,
    out_dir: Path,
    base_name: str,
    timeout_s: int = 300,
    context: Optional[dict] = None,
) -> ImageJobResult:
    """Minimal wrapper around replicate.run. Blocking, downloads outputs, returns a sidecar dict."""
    _ensure_replicate()
    t0 = time.perf_counter()
    success = False
    outputs: List[dict] = []
    error: Optional[dict] = None
    t_predict: Optional[float] = None

    try:
        t_predict_start = time.perf_counter()
        wait_s = min(timeout_s, 60)
        try:
            files = replicate.run(model_ref, input=params, wait=wait_s)
        except TypeError:
            try:
                files = replicate.run(model_ref, input=params, timeout=timeout_s)  # type: ignore[call-arg]
            except TypeError:
                files = replicate.run(model_ref, input=params)
        t_predict = time.perf_counter() - t_predict_start

        if not isinstance(files, (list, tuple)):
            files = [files]
        outputs = _write_outputs(files, out_dir, base_name)
        success = True
    except Exception as e:  # pragma: no cover - exercised by live runs
        error_detail = str(e)
        if hasattr(e, "response"):
            try:
                error_detail = f"{e} | response: {e.response.text}"
            except Exception:
                pass
        error = {"message": error_detail, "type": e.__class__.__name__}

    elapsed = time.perf_counter() - t0
    sidecar: ImageJobResult = {
        "success": success,
        "prediction_id": None,
        "status": "ok" if success else "error",
        "error": error,
        "model": {
            "id": model_ref.split(":")[0],
            "version": (model_ref.split(":", 1)[1] if ":" in model_ref else "@latest"),
        },
        # Avoid leaking file handles and large blobs; caller can pass through what it wants.
        "inputs": {k: v for k, v in params.items() if k not in {"image", "image_input", "input_images"}},
        "resolved_params": {k: v for k, v in params.items() if k not in {"image", "image_input", "input_images"}},
        "outputs": outputs,
        "cold_start": (t_predict or 0.0) >= 40.0,
        "metrics": {
            "predict_time_s": t_predict,
            "download_time_s": (outputs[0].get("_metrics", {}).get("download_time_s") if outputs else None),
            "elapsed_s": elapsed,
        },
    }
    return sidecar


def edit(
    *,
    model_ref: str,
    image: Path,
    params: dict,
    out_dir: Path,
    base_name: str,
    timeout_s: int = 300,
    context: Optional[dict] = None,
) -> ImageJobResult:
    """Edit/run a model with a local image file handle."""
    _ensure_replicate()
    if not image.is_file():
        raise FileNotFoundError(f"Image not found: {image}")

    pdict = dict(params)
    # Prefer 'image' key when present, otherwise try image_input.
    if "image" in pdict:
        pdict["image"] = open(image, "rb")
    elif "image_input" in pdict:
        pdict["image_input"] = [open(image, "rb")]
    else:
        pdict["image"] = open(image, "rb")

    try:
        sidecar = generate(
            model_ref=model_ref,
            params=pdict,
            out_dir=out_dir,
            base_name=base_name,
            timeout_s=timeout_s,
            context=context,
        )
    finally:
        handles_to_close = [pdict.get("image")]
        image_input = pdict.get("image_input")
        if isinstance(image_input, list):
            handles_to_close.extend(image_input)
        elif image_input is not None:
            handles_to_close.append(image_input)
        for v in handles_to_close:
            try:
                if v is not None and hasattr(v, "close"):
                    v.close()
            except Exception:
                pass

    safe_params = dict(params)
    safe_params["image"] = str(image)
    sidecar["resolved_params"] = safe_params
    return sidecar
