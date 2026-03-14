from __future__ import annotations

import datetime
import json
import os
import re
from pathlib import Path
from typing import Any, Optional, Tuple


def repo_root() -> Path:
    return Path(__file__).resolve().parent.parent


def windows_to_local(path_str: str) -> str:
    match = re.match(r"^([A-Za-z]):[\\/](.*)$", path_str)
    if not match:
        return path_str
    drive = match.group(1).lower()
    rest = match.group(2).replace("\\", "/")
    return f"/mnt/{drive}/{rest}"


def local_to_windows(path_str: str) -> Optional[str]:
    match = re.match(r"^/mnt/([a-zA-Z])/(.*)$", path_str)
    if not match:
        return None
    drive = match.group(1).upper()
    rest = match.group(2).replace("/", "\\")
    return f"{drive}:\\{rest}"


def resolve_path(path_str: str) -> Path:
    return Path(windows_to_local(path_str)).expanduser().resolve()


def slugify(text: str, *, max_len: int = 60) -> str:
    value = text.strip().lower()
    value = re.sub(r"\s+", "-", value)
    value = re.sub(r"[^a-z0-9_-]+", "", value)
    value = re.sub(r"-{2,}", "-", value)
    value = value.strip("-_")
    if not value:
        return "untitled"
    return value[:max_len]


def prompt_stem(prompt: str) -> str:
    words = [word for word in re.split(r"\s+", prompt.strip()) if word]
    if not words:
        return "untitled"
    return slugify(" ".join(words[:3]))


def default_output_root() -> Path:
    env_path = os.getenv("DAYDREAM_VISION_OUTPUT_DIR")
    if env_path:
        return resolve_path(env_path)
    return (repo_root() / "daydreaming" / "out" / "visual-anchors").resolve()


def dated_output_dir(output_dir: Optional[str], *, subdir: Optional[str] = None) -> Tuple[Path, Path]:
    root = resolve_path(output_dir) if output_dir else default_output_root()
    dated = root / datetime.date.today().isoformat()
    if subdir:
        dated = dated / subdir
    dated.mkdir(parents=True, exist_ok=True)
    return root, dated.resolve()


def path_record(path: Path, *, root: Optional[Path] = None) -> dict[str, Any]:
    absolute = path.resolve()
    record: dict[str, Any] = {"path": str(absolute)}
    windows_path = local_to_windows(str(absolute))
    if windows_path:
        record["windowsPath"] = windows_path
    if root is not None:
        try:
            record["relativePath"] = str(absolute.relative_to(root.resolve()))
        except Exception:
            pass
    return record


def write_json(path: Path, payload: Any) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(payload, indent=2, sort_keys=True) + "\n", encoding="utf-8")
