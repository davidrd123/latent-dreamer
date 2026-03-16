# Executor / Verifier Protocol

Last updated: 2026-03-15

Use this when a sprint should run with milestone-based autonomy but
independent quality control.

---

## Purpose

Separate execution from verification.

- The executor moves the sprint forward.
- The verifier checks artifacts, not summaries.
- The arbiter only intervenes at milestone boundaries or when there is
  disagreement.

This is the default mode once:
- the recipe is frozen
- the decision gate is explicit
- the evaluation surface is stable enough to compare runs honestly

---

## Roles

### 1. Execution agent

Owns the sprint work:
- run batches
- curate outputs
- populate the keeper bank
- assemble patch packs
- write milestone summaries

The execution agent does not get final authority over claims about:
- keeper yield
- role coverage
- patch-pack viability
- sprint gate status

### 2. Artifact verifier

Checks milestone claims against files:
- batch summaries
- keeper packets
- keeper bank rows
- pack assembly summaries
- traversal traces, if relevant

The verifier should assume the executor summary may be incomplete,
optimistic, or stale.

### 3. Control-plane verifier

Checks whether the sprint docs still match reality:
- `current-sprint.md`
- `dashboard.md`
- `canonical-map.md`

This role catches:
- stale task lists
- missing verification state
- decision gates that no longer match the actual work

### 4. Arbiter

Resolves milestone calls:
- accepts verified milestone status
- decides whether escalation is required
- updates control docs if the sprint state changed materially

In practice, the arbiter can be:
- the supervising agent
- a second-pass reviewer
- or the human when the decision is consequential

---

## Milestone Workflow

For each consequential milestone:

1. Executor completes the work.
2. Executor writes or updates the relevant artifacts.
3. Executor gives a concise milestone claim.
4. Artifact verifier checks the claim against files.
5. Control-plane verifier checks whether the sprint docs still reflect
   reality.
6. Arbiter records the current state:
   - `claimed`
   - `verified`
   - `disputed`
7. Only then does the sprint move to the next milestone.

---

## Required Evidence By Milestone

### Batch complete

Minimum evidence:
- `batch.summary.md`
- `keeper_packet.md` if generated
- `results.jsonl` row

### Curation pass complete

Minimum evidence:
- `keeper_bank_supply_v1.jsonl` or current sprint bank
- explicit `keeper` / `reserve` / `pending` verdicts
- role labels if role coverage is part of the gate

### Patch-pack assembly attempt complete

Minimum evidence:
- pack assembly summary
- pack definitions or referenced source rows
- traversal outputs if the assembly claim depends on traversal

### Sprint conclusion

Minimum evidence:
- current bank totals
- pack assembly status per fixture
- edit-cost read
- explicit pass / partial pass / fail decision against the sprint gate

---

## Escalation Triggers

Pause the sprint only if:

- the frozen recipe needs to change
- the decision gate is crossed or clearly blocked
- a structural / architectural change is being proposed
- the results contradict the current dashboard arc
- a verification pass disagrees with the executor in a way that changes
  the sprint decision
- a new external architecture reply changes the project framing

Otherwise, continue autonomously.

---

## Reporting Cadence

Do not report every micro-step.

Report only at:
- batch complete
- curation complete
- patch-pack assembly complete
- sprint closeout

Interim chatter is optional and should be brief.

---

## Current Pattern

For `supply_v1`:

- executor: Codex1
- artifact verifier: second agent pass or direct artifact inspection
- control-plane verifier: second agent pass or direct doc inspection
- arbiter: supervising agent / human at gate decisions

This pattern should stay in place until the sprint closes.

