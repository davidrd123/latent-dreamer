# AGENTS.md

Scope: applies to this repository root unless a deeper `AGENTS.md`
overrides it.

## Python environment

- Use `uv run` for Python commands in this repo.
- Do not assume the shell's plain `python` is the correct interpreter.
  In this workspace it may resolve to a separate Miniconda install and
  miss project dependencies.
- For scripts, module checks, and one-off Python commands, prefer:
  - `uv run python ...`
  - `uv run python -c "..."`
- This matters for local project dependencies and Gemini support in
  particular. For example, `google.genai` is available under `uv run`
  here even when it is not available under the shell's default
  interpreter.

## Environment assumptions

- Prefer the repo's `pyproject.toml`, `uv.lock`, and `.venv`-managed
  environment over ad hoc system Python state.
- If a Python command fails for a missing package, first check whether it
  should have been run under `uv run` before changing dependencies.
