from __future__ import annotations

from pathlib import Path
from typing import Any, Dict, List, Optional, Sequence, TypedDict
import base64
import io
import mimetypes

try:
    from PIL import Image
except Exception:  # pragma: no cover
    Image = None  # type: ignore[assignment]

try:  # Optional; functions will raise if missing
    from google import genai  # type: ignore
    from google.genai import types as gtypes  # type: ignore
except Exception:  # pragma: no cover
    genai = None  # type: ignore[assignment]
    gtypes = None  # type: ignore[assignment]

from .gemini_min import init_client

MAX_REF_IMAGES = 14


class GeminiImageResult(TypedDict, total=False):
    images: List["Image.Image"]  # type: ignore[name-defined]
    text: Optional[str]
    grounding: Any
    config: Dict[str, Any]


def _build_image_config(aspect_ratio: Optional[str], image_size: Optional[str]):
    if not aspect_ratio and not image_size:
        return None
    if gtypes is None or not hasattr(gtypes, "ImageConfig"):
        raise RuntimeError("google-genai ImageConfig not found; upgrade google-genai to a version with types.ImageConfig support.")
    return gtypes.ImageConfig(  # type: ignore[call-arg]
        aspect_ratio=aspect_ratio or None,
        image_size=image_size or None,
    )


def _load_image(path: Path) -> "Image.Image":  # type: ignore[name-defined]
    if Image is None:
        raise RuntimeError("Pillow not installed. `pip install pillow`.")
    if not path.is_file():
        raise FileNotFoundError(f"Image not found: {path}")
    mt, _ = mimetypes.guess_type(str(path))
    if not (mt and mt.startswith("image/")):
        raise RuntimeError(f"Expected image/* file, got {mt or 'unknown'}: {path}")
    img = Image.open(path)
    return img.convert("RGB")


def _decode_inline_image(part: Any) -> Optional["Image.Image"]:  # type: ignore[name-defined]
    inline = getattr(part, "inline_data", None)
    if inline is None:
        return None
    mt = getattr(inline, "mime_type", None)
    if not (isinstance(mt, str) and mt.startswith("image/")):
        return None
    data = getattr(inline, "data", None)
    raw: Optional[bytes]
    if isinstance(data, (bytes, bytearray)):
        raw = bytes(data)
    elif isinstance(data, str):
        try:
            raw = base64.b64decode(data)
        except Exception:
            raw = None
    else:
        raw = None
    if not raw:
        return None
    try:
        buf = io.BytesIO(raw)
        img = Image.open(buf)  # type: ignore[call-arg]
        return img.convert("RGB")
    except Exception:
        return None


def generate_images(
    *,
    client: Any,
    model: str,
    user_prompt: str,
    system_prompt: str = "",
    image_paths: Sequence[Path] = (),
    ref_image_paths: Sequence[Path] = (),
    aspect_ratio: Optional[str] = None,
    image_size: Optional[str] = None,
    temperature: float = 0.7,
    enable_search: bool = False,
) -> GeminiImageResult:
    """Generate (or edit) images using Gemini 3 Pro Image preview model."""
    if gtypes is None:
        raise RuntimeError("google-genai not installed. `pip install google-genai`.")
    if Image is None:
        raise RuntimeError("Pillow not installed. `pip install pillow`.")

    all_imgs: List[Path] = list(image_paths) + list(ref_image_paths)
    if len(all_imgs) > MAX_REF_IMAGES:
        raise RuntimeError(f"Too many reference images ({len(all_imgs)}); max {MAX_REF_IMAGES}.")

    contents: List[Any] = [user_prompt]
    loaded: List["Image.Image"] = []  # type: ignore[name-defined]
    for p in all_imgs:
        img = _load_image(p)
        contents.append(img)
        loaded.append(img)

    cfg_kwargs: Dict[str, Any] = {
        "response_modalities": ["IMAGE", "TEXT"],
        "system_instruction": system_prompt or None,
        "temperature": temperature,
    }
    image_cfg = _build_image_config(aspect_ratio, image_size)
    if image_cfg:
        cfg_kwargs["image_config"] = image_cfg
    if enable_search:
        cfg_kwargs["tools"] = [{"google_search": {}}]

    cfg = gtypes.GenerateContentConfig(**{k: v for k, v in cfg_kwargs.items() if v is not None})
    resp = client.models.generate_content(model=model, contents=contents, config=cfg)

    images: List["Image.Image"] = []  # type: ignore[name-defined]
    texts: List[str] = []

    candidates = getattr(resp, "candidates", None) or []
    for cand in candidates:
        cont = getattr(cand, "content", None) or cand
        parts = getattr(cont, "parts", None)
        if not parts:
            continue
        for part in parts:
            # as_image convenience if present
            if hasattr(part, "as_image") and callable(getattr(part, "as_image")):
                try:
                    img_obj = part.as_image()  # type: ignore[call-arg]
                    if img_obj is not None:
                        images.append(img_obj.convert("RGB"))
                        continue
                except Exception:
                    pass
            # inline_data fallback
            inline_img = _decode_inline_image(part)
            if inline_img is not None:
                images.append(inline_img)
                continue
            text_val = getattr(part, "text", None)
            if text_val:
                texts.append(text_val)

    if not images:
        raise RuntimeError("No image parts returned from Gemini image generation call.")

    text_out = "\n".join([t for t in texts if t]).strip() or None
    grounding = getattr(resp, "grounding_metadata", None)
    return {
        "images": images,
        "text": text_out,
        "grounding": grounding,
        "config": {
            "model": model,
            "aspect_ratio": aspect_ratio,
            "image_size": image_size,
            "temperature": temperature,
            "enable_search": enable_search,
        },
    }


__all__ = ["generate_images", "GeminiImageResult", "init_client", "MAX_REF_IMAGES"]
