"""Minimal Gemini provider for exploratory video/image analysis.

Shared functions for CLI and MCP wrappers. Keeps behavior simple and
text-first (no strict schemas). Uses google-genai SDK.
"""
from __future__ import annotations

from pathlib import Path
from typing import Any, Iterable, List, Optional, Tuple, Dict, Any as AnyType, Callable
import mimetypes
import os
import time
import json
import random
import warnings
import io
import base64

try:  # Optional at import time; fail at call time if missing
    from google import genai
    from google.genai import types as gtypes
except Exception:  # pragma: no cover - runtime import
    genai = None  # type: ignore[assignment]
    gtypes = None  # type: ignore[assignment]

try:
    from PIL import Image
except Exception:  # pragma: no cover
    Image = None  # type: ignore[assignment]


if ".webp" not in mimetypes.types_map:
    mimetypes.add_type("image/webp", ".webp")


def init_client() -> "genai.Client":  # type: ignore[name-defined]
    if genai is None:
        raise RuntimeError("google-genai not installed. `pip install google-genai`.")
    # Best-effort: load .env if python-dotenv is available
    try:
        from dotenv import load_dotenv  # type: ignore
        load_dotenv()  # Loads .env from CWD or parent dirs
    except Exception:
        pass
    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        raise RuntimeError("GEMINI_API_KEY not set; export it in your environment.")
    return genai.Client(api_key=api_key)


def upload_and_poll_video(
    client: "genai.Client",  # type: ignore[name-defined]
    path: str,
    timeout_s: int = 300,
) -> "gtypes.File":  # type: ignore[name-defined]
    f = client.files.upload(file=path)
    delay, waited = 1.0, 0.0
    while getattr(f, "state", None) and f.state.name == "PROCESSING":
        if waited >= timeout_s:
            try:
                client.files.delete(name=f.name)
            except Exception:
                pass
        
            raise RuntimeError(f"Video processing timed out after {timeout_s}s")
        time.sleep(delay)
        waited += delay
        delay = min(delay * 2, 30.0)
        f = client.files.get(name=f.name)
    if not getattr(f, "state", None) or f.state.name != "ACTIVE":
        st = getattr(getattr(f, "state", None), "name", "unknown")
        raise RuntimeError(f"Video processing failed: {st}")
    return f


def prepare_content(
    client: "genai.Client",  # type: ignore[name-defined]
    media_path: Path,
    user_prompt: str,
    fps: Optional[float] = None,
) -> Tuple[List[Any], Optional["gtypes.File"]]:  # type: ignore[name-defined]
    mt, _ = mimetypes.guess_type(str(media_path))
    if mt and mt.startswith("image/"):
        if Image is None:
            raise RuntimeError("Pillow not installed. `pip install pillow`.")
        img = Image.open(media_path).convert("RGB")
        return [img, user_prompt], None
    if mt and mt.startswith("video/"):
        f = upload_and_poll_video(client, str(media_path))
        part_kwargs = dict(file_data=gtypes.FileData(file_uri=f.uri, mime_type=mt or "video/mp4"))
        if fps is not None:
            part_kwargs["video_metadata"] = gtypes.VideoMetadata(
                fps=int(fps) if fps == int(fps) else fps,
                start_offset="0s",
            )
        return [gtypes.Part(**part_kwargs), user_prompt], f
    raise RuntimeError(f"Unsupported media type: {mt or 'unknown'} for {media_path}")


def generate_image_edits(
    client: "genai.Client",  # type: ignore[name-defined]
    *,
    model: str,
    contents: List[Any],
    system_prompt: str,
    temperature: float = 0.7,
) -> List["Image.Image"]:  # type: ignore[name-defined]
    """Generate edited image(s) using an image-capable Gemini model.

    Returns a list of PIL Image objects. In typical usage we expect a single
    output image, but multiple image parts are supported and will all be
    returned in a stable order.
    """
    if Image is None:
        raise RuntimeError("Pillow not installed. `pip install pillow`.")
    if gtypes is None:
        raise RuntimeError("google-genai types not available.")

    # For image-edit flows we follow the official examples:
    # - provide image + text in `contents`
    # - rely on default response modalities (Image + optional Text)
    # We only build a config when we actually have a non-empty system prompt
    # or a non-default temperature.
    if system_prompt or temperature != 0.7:
        cfg = gtypes.GenerateContentConfig(
            system_instruction=system_prompt or None,
            temperature=temperature,
        )
        resp = client.models.generate_content(model=model, contents=contents, config=cfg)
    else:
        resp = client.models.generate_content(model=model, contents=contents)

    images: List["Image.Image"] = []  # type: ignore[name-defined]

    def _decode_inline_image(data: Any, mt: Optional[str]) -> Optional["Image.Image"]:  # type: ignore[name-defined]
        if data is None:
            return None
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
            img = Image.open(io.BytesIO(raw))
            return img.convert("RGB")
        except Exception:
            # Let caller decide how to surface failure
            return None

    candidates = getattr(resp, "candidates", None) or []
    for cand in candidates:
        cont = getattr(cand, "content", None)
        parts = getattr(cont, "parts", None) if cont is not None else None
        if not parts:
            continue
        for p in parts:
            inline = getattr(p, "inline_data", None)
            if inline is not None:
                mt = getattr(inline, "mime_type", None)
                if isinstance(mt, str) and mt.startswith("image/"):
                    data = getattr(inline, "data", None)
                    img_obj = _decode_inline_image(data, mt)
                    if img_obj is not None:
                        images.append(img_obj)

    if not images:
        raise RuntimeError("No image parts returned from Gemini image-edit call.")

    return images


def generate_text(
    client: "genai.Client",  # type: ignore[name-defined]
    *,
    model: str,
    contents: List[Any],
    system_prompt: str,
    max_output_tokens: int = 65536,
    temperature: float = 0.7,
    stop_sequences: Optional[List[str]] = None,
) -> str:
    cfg = gtypes.GenerateContentConfig(
        system_instruction=system_prompt,
        response_mime_type="text/plain",
        max_output_tokens=max_output_tokens,
        temperature=temperature,
        stop_sequences=stop_sequences,
    )
    resp = client.models.generate_content(model=model, contents=contents, config=cfg)
    # Prefer the convenience accessor when available
    try:
        if hasattr(resp, "text") and resp.text:
            return resp.text
    except Exception:
        pass
    # Fallback: assemble text from candidate parts
    if getattr(resp, "candidates", None):
        parts_out: List[str] = []
        for c in resp.candidates:
            cont = getattr(c, "content", None)
            parts = getattr(cont, "parts", None) if cont else None
            if parts:
                for p2 in parts:
                    # skip thought parts if present (defensive)
                    if getattr(p2, "thought", False):
                        continue
                    t = getattr(p2, "text", None)
                    if t:
                        parts_out.append(t)
        if parts_out:
            return "\n".join(parts_out)
    return "(no text content returned)"


def _summarize_usage(usage_obj: AnyType) -> Dict[str, Optional[int]]:
    """Extract a safe, lightweight summary of token usage from the SDK object.

    Returns a dict with keys that are commonly present in google-genai usage_metadata.
    Missing fields are returned as None.
    """
    def gi(name: str) -> Optional[int]:
        return getattr(usage_obj, name, None)

    return {
        "prompt_token_count": gi("prompt_token_count"),
        "candidates_token_count": gi("candidates_token_count"),
        "thoughts_token_count": gi("thoughts_token_count"),
        "total_token_count": gi("total_token_count"),
        "cached_content_token_count": gi("cached_content_token_count"),
    }


def generate_text_with_usage(
    client: "genai.Client",  # type: ignore[name-defined]
    *,
    model: str,
    contents: List[Any],
    system_prompt: str,
    max_output_tokens: int = 65536,
    temperature: float = 0.7,
    stop_sequences: Optional[List[str]] = None,
) -> Tuple[str, Optional[Dict[str, Optional[int]]]]:
    """Like generate_text, but also returns a usage summary dict when available.

    Returns (text, usage_summary) where usage_summary contains token counts or None when not provided by the API.
    """
    cfg = gtypes.GenerateContentConfig(
        system_instruction=system_prompt,
        response_mime_type="text/plain",
        max_output_tokens=max_output_tokens,
        temperature=temperature,
        stop_sequences=stop_sequences,
    )
    resp = client.models.generate_content(model=model, contents=contents, config=cfg)

    # Extract text (reuse the same logic as generate_text)
    text: str
    try:
        if hasattr(resp, "text") and resp.text:
            text = resp.text
        else:
            raise AttributeError
    except Exception:
        parts_out: List[str] = []
        if getattr(resp, "candidates", None):
            for c in resp.candidates:
                cont = getattr(c, "content", None)
                parts = getattr(cont, "parts", None) if cont else None
                if parts:
                    for p2 in parts:
                        if getattr(p2, "thought", False):
                            continue
                        t = getattr(p2, "text", None)
                        if t:
                            parts_out.append(t)
        text = "\n".join(parts_out) if parts_out else "(no text content returned)"

    usage_summary: Optional[Dict[str, Optional[int]]] = None
    try:
        um = getattr(resp, "usage_metadata", None)
        if um is not None:
            usage_summary = _summarize_usage(um)
    except Exception:
        usage_summary = None

    return text, usage_summary


def cleanup_uploaded(client: "genai.Client", uploaded: Optional["gtypes.File"]) -> None:  # type: ignore[name-defined]
    if uploaded and hasattr(uploaded, "name"):
        try:
            client.files.delete(name=uploaded.name)
        except Exception:
            pass

# ============================
# Batch helpers (JSONL-first)
# ============================

# Map camelCase (SDK-style) to snake_case (REST file mode)
_CAMEL_TO_REST = {
    "fileData": "file_data",
    "fileUri": "file_uri",
    "mimeType": "mime_type",
    "videoMetadata": "video_metadata",
    "startOffset": "start_offset",
    "endOffset": "end_offset",
}

def _to_rest_snake(obj: Any) -> Any:
    if isinstance(obj, dict):
        out: Dict[str, Any] = {}
        for k, v in obj.items():
            k2 = _CAMEL_TO_REST.get(k, k)
            out[k2] = _to_rest_snake(v)
        return out
    if isinstance(obj, list):
        return [_to_rest_snake(x) for x in obj]
    return obj

def upload_image_file(client: "genai.Client", path: str) -> "gtypes.File":  # type: ignore[name-defined]
    f = client.files.upload(file=path)
    # Images are typically immediately ACTIVE; no polling loop needed
    return f


def prepare_batch_ref(
    client: "genai.Client",
    media_path: Path,
    fps: Optional[float] = None,
) -> Tuple[Dict[str, Any], "gtypes.File"]:  # type: ignore[name-defined]
    """Upload media to Files API and return a JSON-serializable file part for JSONL.

    Returns (part_dict, uploaded_file). Caller is responsible for cleanup.
    part_dict uses API field casing (fileData, mimeType, videoMetadata, fileUri).
    """
    mt, _ = mimetypes.guess_type(str(media_path))
    if mt and mt.startswith("image/"):
        f = upload_image_file(client, str(media_path))
        part: Dict[str, Any] = {
            "fileData": {
                "fileUri": getattr(f, "uri", None),
                "mimeType": getattr(f, "mime_type", None) or mt,
            }
        }
        return part, f
    if mt and mt.startswith("video/"):
        f = upload_and_poll_video(client, str(media_path))
        part: Dict[str, Any] = {
            "fileData": {
                "fileUri": getattr(f, "uri", None),
                "mimeType": getattr(f, "mime_type", None) or mt,
            }
        }
        if fps is not None:
            fps_val = int(fps) if fps == int(fps) else fps
            part["videoMetadata"] = {
                "fps": fps_val,
                "startOffset": "0s",
            }
        return part, f
    raise RuntimeError(f"Unsupported media type: {mt or 'unknown'} for {media_path}")


def build_jsonl_line(
    *,
    key: str,
    system_prompt: str,
    user_prompt: str,
    file_part: Dict[str, Any],
    max_output_tokens: int,
    temperature: float,
    stop_sequences: Optional[List[str]] = None,
    response_mime_type: str = "text/plain",
) -> Dict[str, Any]:
    """Build one JSONL entry ({key, request}) for Batch file mode.

    Uses API field casing for request object: generation_config, system_instruction, contents/parts.
    """
    # Build request combining REST snake_case for content parts and
    # the documented REST casing for config/system per official examples.
    # - system_instruction uses an object with parts/text
    # - generationConfig uses camelCase keys (responseMimeType, maxOutputTokens, ...)
    part_conv = _to_rest_snake(file_part)
    req: Dict[str, Any] = {
        "system_instruction": {
            "parts": [{"text": system_prompt}]
        },
        "contents": [
            {
                "role": "user",
                "parts": [part_conv, {"text": user_prompt}],
            }
        ],
        "generationConfig": {
            "responseMimeType": response_mime_type,
            "maxOutputTokens": max_output_tokens,
            "temperature": temperature,
        },
    }
    if stop_sequences:
        req["generationConfig"]["stopSequences"] = stop_sequences
    return {"key": key, "request": req}


def create_batch_from_file(
    client: "genai.Client",
    *,
    model: str,
    uploaded_jsonl_file_name: str,
    display_name: str,
):
    return client.batches.create(model=model, src=uploaded_jsonl_file_name, config={"display_name": display_name})


def upload_jsonl_file(client: "genai.Client", path: str, display_name: str) -> "gtypes.File":  # type: ignore[name-defined]
    """Upload a JSONL file with explicit MIME type and display name.

    Prefer passing `mime_type` argument directly; then try typed and dict configs.
    """
    # First: pass explicit mime_type argument directly (SDK error suggests this path)
    for mt in ("application/x-ndjson", "application/jsonl", "application/json", "jsonl"):
        try:
            return client.files.upload(file=path, mime_type=mt, display_name=display_name)
        except Exception:
            continue

    # Typed config fallback
    if gtypes is not None and hasattr(gtypes, "UploadFileConfig"):
        for mt in ("application/x-ndjson", "application/jsonl", "application/json", "jsonl"):
            try:
                return client.files.upload(
                    file=path,
                    config=gtypes.UploadFileConfig(display_name=display_name, mime_type=mt),
                )
            except Exception:
                continue

    # Dict config fallback (snake_case and camelCase)
    for k_display, k_mime in (("display_name", "mime_type"), ("displayName", "mimeType")):
        for mt in ("application/x-ndjson", "application/jsonl", "application/json", "jsonl"):
            try:
                return client.files.upload(file=path, config={k_display: display_name, k_mime: mt})
            except Exception:
                continue

    # Final attempt (may fail without mime)
    return client.files.upload(file=path)


def _with_backoff(fn, *, retries: int = 3, base_delay: float = 1.0, max_delay: float = 8.0):
    last = None
    for i in range(retries + 1):
        try:
            return fn()
        except Exception as e:  # status-based in caller where possible
            last = e
            if i >= retries:
                break
            jitter = random.random() * 0.2
            delay = min(base_delay * (2 ** i), max_delay) * (1.0 + jitter)
            time.sleep(delay)
    raise last  # type: ignore[misc]


def poll_batch(
    client: "genai.Client",
    *,
    name: str,
    interval_s: float = 30.0,
    timeout_s: Optional[float] = None,
    on_state: Optional[Callable[[Optional[str], float], None]] = None,
):
    start = time.time()
    term = {"JOB_STATE_SUCCEEDED", "JOB_STATE_FAILED", "JOB_STATE_CANCELLED", "JOB_STATE_EXPIRED"}
    while True:
        def _get():
            # Suppress noisy enum warnings from SDK
            with warnings.catch_warnings():
                warnings.filterwarnings("ignore", message=r".*not a valid JobState.*", category=UserWarning)
                return client.batches.get(name=name)

        job = _with_backoff(_get)
        state = getattr(job, "state", None)
        state_name = getattr(state, "name", None)
        # Normalize newer BATCH_STATE_* to JOB_STATE_*
        if isinstance(state_name, str) and state_name.startswith("BATCH_STATE_"):
            state_name = "JOB_STATE_" + state_name.split("BATCH_STATE_", 1)[1]
        if on_state is not None:
            try:
                on_state(state_name, time.time() - start)
            except Exception:
                pass
        if state_name in term:
            return job
        if timeout_s is not None and (time.time() - start) >= timeout_s:
            return job
        time.sleep(interval_s)


def download_results_file(client: "genai.Client", *, file_name: str) -> bytes:
    def _dl():
        return client.files.download(file=file_name)

    return _with_backoff(_dl)


def parse_results_jsonl(data: bytes) -> Iterable[Dict[str, Any]]:
    """Yield parsed results from a JSONL file buffer.

    Each yielded item: {key, ok, text, error}
    """
    def _extract_text(resp_obj: Dict[str, Any]) -> str:
        # Prefer top-level text if present
        t = resp_obj.get("text")
        if isinstance(t, str) and t:
            return t
        # Fallback: assemble from candidates[].content.parts[].text
        texts: List[str] = []
        for c in resp_obj.get("candidates", []) or []:
            cont = c.get("content", {}) or {}
            for p in cont.get("parts", []) or []:
                if isinstance(p, dict) and p.get("text"):
                    texts.append(p["text"]) 
        return "\n".join(texts).strip()

    for line in data.decode("utf-8", errors="replace").splitlines():
        line = line.strip()
        if not line:
            continue
        try:
            obj = json.loads(line)
        except Exception as e:
            yield {"key": None, "ok": False, "text": None, "error": f"jsonl parse error: {e}"}
            continue
        key = obj.get("key")
        # Response line
        if obj.get("response") is not None:
            text = _extract_text(obj["response"]) if isinstance(obj["response"], dict) else None
            yield {"key": key, "ok": True if text is not None else False, "text": text, "error": None}
            continue
        # Error/status line
        if obj.get("error") is not None or obj.get("status") is not None:
            err = obj.get("error") or obj.get("status")
            yield {"key": key, "ok": False, "text": None, "error": err}
            continue
        # Unknown shape
        yield {"key": key, "ok": False, "text": None, "error": "unknown result line"}


def cleanup_files(client: "genai.Client", names: List[str]) -> None:
    for n in names:
        try:
            client.files.delete(name=n)
        except Exception:
            pass
