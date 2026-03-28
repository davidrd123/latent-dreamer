"""Minimal Fal helper functions for Daydream image generation.

Uses the official fal-client SDK and downloads returned image URLs to disk.
"""

from __future__ import annotations

import mimetypes
import os
import time
import urllib.request
from pathlib import Path
from typing import Any, Dict, List, Optional

try:
    import fal_client
except Exception:  # pragma: no cover - runtime import guard
    fal_client = None  # type: ignore[assignment]


ImageJobResult = Dict[str, Any]


def _ensure_fal() -> None:
    if fal_client is None:
        raise RuntimeError(
            "fal-client package not installed. Add it to your venv: `pip install fal-client`"
        )
    try:
        from dotenv import load_dotenv  # type: ignore

        load_dotenv()
    except Exception:
        pass
    if not os.getenv("FAL_KEY"):
        raise RuntimeError("FAL_KEY not set; export it in your environment or .env")


def _ext_from_content_type(content_type: Optional[str], url: Optional[str]) -> str:
    normalized = (content_type or "").strip().lower()
    if normalized == "image/png":
        return ".png"
    if normalized in {"image/jpeg", "image/jpg"}:
        return ".jpg"
    if normalized == "image/webp":
        return ".webp"
    if normalized == "image/gif":
        return ".gif"
    if normalized == "application/zip":
        return ".zip"

    if url:
        guessed, _ = mimetypes.guess_type(url.split("?", 1)[0])
        if guessed:
            return _ext_from_content_type(guessed, None)
        suffix = Path(url.split("?", 1)[0]).suffix.lower()
        if suffix:
            return suffix
    return ".bin"


def _download_to_bytes(url: str) -> tuple[bytes, Optional[str]]:
    with urllib.request.urlopen(url) as response:  # noqa: S310 - provider URL download
        data = response.read()
        content_type = response.headers.get_content_type()
    return data, content_type


def generate(
    *,
    model_ref: str,
    params: dict,
    out_dir: Path,
    base_name: str,
) -> ImageJobResult:
    """Blocking Fal image generation helper."""
    _ensure_fal()
    out_dir.mkdir(parents=True, exist_ok=True)

    t0 = time.perf_counter()
    outputs: List[dict[str, Any]] = []
    error: Optional[dict[str, str]] = None
    timings: Dict[str, Any] = {}

    try:
        result = fal_client.subscribe(model_ref, arguments=params)  # type: ignore[union-attr]
        raw_images = result.get("images") or []
        timings = result.get("timings") or {}
        for idx, item in enumerate(raw_images):
            if not isinstance(item, dict):
                continue
            url = item.get("url")
            if not isinstance(url, str) or not url:
                continue
            bytes_out, header_content_type = _download_to_bytes(url)
            content_type = item.get("content_type") if isinstance(item.get("content_type"), str) else header_content_type
            ext = _ext_from_content_type(content_type, url)
            output_path = out_dir / f"{base_name}_{idx}{ext}"
            if output_path.exists():
                output_path = out_dir / f"{base_name}_{idx}_{int(time.time() * 1000) % 100000000}{ext}"
            output_path.write_bytes(bytes_out)
            outputs.append(
                {
                    "path": str(output_path),
                    "url": url,
                    "bytes": len(bytes_out),
                    "content_type": content_type,
                    "width": item.get("width"),
                    "height": item.get("height"),
                }
            )
    except Exception as exc:  # pragma: no cover - exercised by live runs
        error = {"message": str(exc), "type": exc.__class__.__name__}

    elapsed = time.perf_counter() - t0
    success = error is None

    return {
        "success": success,
        "status": "ok" if success else "error",
        "error": error,
        "model": {"id": model_ref},
        "inputs": params,
        "resolved_params": params,
        "outputs": outputs,
        "metrics": {
            "elapsed_s": elapsed,
            "timings": timings,
        },
    }
