# Puppet Knows Benchmark Re-entry

Date: 2026-03-22

## Status

- state: planned
- benchmark: `Puppet Knows`
- harness: `kernel/src/daydreamer/benchmarks/puppet_knows_autonomous.clj`

## Purpose

Return to the first benchmark after the current Step 1 / Step 2
hardening wave and check the new memory membrane and executor seam in
an end-to-end run.

This is not the verified-paths milestone. It is the first honest
benchmark re-entry in the new context.

## Re-entry trigger

Run this note when all of the following are true:

- rule-access gates live activation and dispatch, not only graph views
- the current source-use lifecycle is stable enough to read:
  `pending -> vindicated -> promoted/demoted`
- the current effect-program seam issues are closed enough that a
  benchmark failure is meaningful rather than ambiguous

If those conditions are not met, do not treat benchmark behavior as a
real architectural verdict yet.

## Stage 1: 12-cycle regression return

### Commands

```bash
cd kernel
bb check
bb puppet-knows-autonomous
bb puppet-knows-autonomous-cognitive
```

### Expected artifacts

- `kernel/out/puppet_knows_autonomous.json`
- `kernel/out/puppet_knows_autonomous.html`
- `kernel/out/puppet_knows_autonomous_cognitive.html`

### Questions to answer

- Does the run complete cleanly under the new Step 1 / Step 2
  contract?
- Do family-plan episodes remain `:trace` / `:provisional` at store
  time?
- Are same-family source uses recorded as pending rather than
  self-vindicated?
- Does later cross-family evidence actually drive vindication and
  promotion?
- Do stale / contradicted / backfired episodes now affect retrieval
  and admission in the trace?
- If rule-access gating is live, do quarantined / frontier rules stop
  behaving like ordinary always-accessible rules?

### Pass / fail

- `bb check`: `pass | fail | not run`
- 12-cycle run completes: `pass | fail | not run`
- artifacts rendered: `pass | fail | partial`
- admission behavior matches docs: `pass | fail | unclear`
- use/vindication behavior visible in trace: `pass | fail | unclear`
- rule-access behavior visible in trace: `pass | fail | unclear`
- overall Stage 1 verdict: `pass | fail | partial`

### Notes

- If Stage 1 fails because the semantics are still moving, update
  `current-sprint.md` before rerunning.
- If Stage 1 fails because the benchmark assumptions are stale, fix the
  benchmark before drawing architecture conclusions.

## Stage 2: 50-cycle soak

Only run this if Stage 1 is clean enough to be informative.

### Command

```bash
cd kernel
clojure -M:trace -m daydreamer.export-trace \
  --benchmark autonomous \
  --cycles 50 \
  --out out/puppet_knows_autonomous_50.json \
  --render-html \
  --html-out out/puppet_knows_autonomous_50.html \
  --print-summary
```

### Expected artifacts

- `kernel/out/puppet_knows_autonomous_50.json`
- `kernel/out/puppet_knows_autonomous_50.html`

### Questions to answer

- Does the memory membrane prevent groove formation over a longer run?
- Do pending source uses actually resolve later, or mostly accumulate?
- Do stale episodes rehabilitate only after repeated cross-family
  evidence?
- Does quarantine / reopening produce meaningful rule-access movement?
- Does the system stay cognitively legible rather than becoming
  membrane-correct but behaviorally dead?

### Pass / fail

- 50-cycle run completes: `pass | fail | not run`
- trace remains legible: `pass | fail | unclear`
- no obvious groove lock: `pass | fail | unclear`
- stale rehabilitation looks sane: `pass | fail | unclear`
- quarantine / reopening looks sane: `pass | fail | unclear`
- overall Stage 2 verdict: `pass | fail | partial`

## Stage 3: benchmark comparison return

Only run this after the 50-cycle soak is worth reading.

### Goal

Compare the new membrane-aware benchmark behavior against the earlier
`Puppet Knows` baseline and the runtime-thought feedback runs.

Reference note:

- `daydreaming/Notes/runtime-thought-feedback-comparison.md`

### Questions to answer

- What behavior changed because of the new memory membrane, not just
  because the benchmark was rerun?
- Is the benchmark now showing real use/outcome/promotion dynamics?
- Is rule-access still mostly metadata, or has it become behavioral?
- Does the new contract produce better long-run behavior, or only
  stricter bookkeeping?

### Pass / fail

- comparison packet assembled: `pass | fail | not run`
- benchmark difference is interpretable: `pass | fail | unclear`
- architecture story strengthened: `pass | fail | mixed`
- overall Stage 3 verdict: `pass | fail | partial`

## Exit criteria

This benchmark re-entry is successful when:

- the 12-cycle run passes cleanly
- the longer soak is informative rather than noisy
- the trace shows real membrane behavior:
  pending use, later vindication, promotion/demotion, and rule-access
  consequences

If those do not show up, do not claim that Step 1 and Step 2 have
become behaviorally real just because the unit tests pass.

## Follow-up outputs

When this note is executed, produce:

- a short milestone summary
- artifact paths
- explicit pass / fail values for each stage above
- any control-plane updates required in `current-sprint.md` or
  `dashboard.md`
