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

## Project orientation

Do not recover project state by guessing from scattered notes.
Start from the control plane:

1. `daydreaming/Notes/current-sprint.md`
   - current objective
   - frozen recipe
   - current status
   - decision gate
2. `daydreaming/Notes/executor-verifier-protocol.md`
   - executor / verifier workflow
   - milestone evidence requirements
   - escalation triggers
3. `daydreaming/Notes/dashboard.md`
   - broader project map
   - parallel fronts
   - milestone execution vs verification state
4. `daydreaming/Notes/canonical-map.md`
   - where the important docs and code live

Rules:

- Treat `current-sprint.md` as the authoritative source for the active
  sprint.
- Treat `dashboard.md` as the broader map, not the sprint contract.
- If a newer note materially changes the objective, success criteria, or
  scope, update `current-sprint.md` — don't leave it stale while treating
  the newer note as the de facto contract.
- Do not rely on another agent's summary by itself for consequential
  claims. Verify against artifacts.
- For sprint-state claims, prefer artifact files such as:
  - `daydreaming/out/authoring_time_generation/keeper_bank_supply_v1.jsonl`
  - batch summaries / keeper packets in `daydreaming/out/...`
  - pack assembly summaries in `daydreaming/out/...`
