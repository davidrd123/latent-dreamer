# Current Sprint

Last updated: 2026-03-15

---

## Objective

Prove repeatable keeper yield from the generation pipeline.
The bridge test passed — now prove the supply chain.

## Frozen Recipe

- provider: anthropic
- model: claude-sonnet-4-6
- prompt_style: legacy_json
- temperature: 0.7
- system_prompt_style: consider_alternatives
- framing: round_robin_authored (where available)
- no mechanism changes during the pilot

## Active Pilot

**supply_v1**: 2 Tessa batches + 2 Kai batches on the frozen recipe.
Keeper bank captures verdicts. Edit cost measured per node.

Decision gate: can we assemble 2 distinct patch packs per fixture
with acceptable edit minutes?

- Yes → generation lane ready, move to multi-situation / Graffito scaffold
- No (close) → light curation + hand-authored fill, then move on
- No (collapsed) → one targeted structural fix (likely Q5), then retest

## Immediate Tasks

1. Codex1: run first supply_v1 Tessa production batch
2. Codex1: curate immediately, populate keeper bank
3. Codex1: run first supply_v1 Kai production batch
4. Curate Kai, check keeper yield
5. Attempt patch-pack assembly from the bank

## Autonomy Contract

This sprint runs in milestone mode, not step-by-step supervision.

- Codex1 executes the sprint within the frozen recipe.
- Reporting happens only at milestone boundaries:
  - batch complete
  - curation pass complete
  - patch-pack assembly attempt complete
  - sprint conclusion
- Codex1 should not pause for routine uncertainty or minor quality judgment.
- The sprint objective is not to understand every failure mode. It is to
  answer the supply_v1 gate honestly.

## Verification Protocol

Codex1 self-report is not sufficient evidence by itself.

- Every milestone result gets one independent verification pass against the
  actual artifacts:
  - bundle outputs
  - keeper bank rows
  - patch-pack definitions
  - traversal outputs, if used
- Independent verification can be done by:
  - a second agent pass
  - direct artifact inspection
  - or both when the claim is consequential
- Claims about keeper yield, role coverage, pack assembly, or gate status
  should be treated as provisional until verified against files.

## Escalation Triggers

Pause and ask for direction only if one of these happens:

- the frozen recipe needs to change
- the decision gate is crossed or clearly blocked
- a structural / architectural change is being proposed
- the results contradict the current dashboard arc
- a new 5 Pro reply materially changes the project framing
- a verification pass disagrees with Codex1's summary in a way that affects
  the sprint decision

Otherwise, keep running the sprint.

## What is NOT in this sprint

- Q5/Q7/Q12 mechanism work (banked for multi-situation phase)
- Conductor or Scope work (parallel track, not blocking)
- L2 kernel refactor (deep build, not blocking)
- New 5 Pro questions
- Prompt architecture experiments beyond the frozen recipe
- Evaluation refinement beyond Q10 minimal
