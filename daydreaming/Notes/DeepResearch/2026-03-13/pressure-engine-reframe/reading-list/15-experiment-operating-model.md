# Experiment Operating Model

Adapted from Karpathy's autoresearch pattern. Not an autonomous
loop — a research discipline for the generation pipeline.

---

## The pattern

**Human writes the program** (research agenda, evaluation criteria,
constraints). **Agent searches within those constraints.** Human
reviews the shortlist, not the raw output.

---

## Five practices

### 1. Frozen evaluation harness

Same fixtures, same batch size, same admission config, same metrics
for a whole tuning round. Don't change evaluation and generation
parameters in the same commit.

This is the target discipline, not a claim about the current state.
Right now:
- `Kai` batch is stable enough to serve as a control
- `Tessa` batch is the active benchmark under hardening
- the patch test is not built yet

So: declare the round explicitly before comparing runs. Do not assume
the harness is already frozen just because the note exists.

Current eval suite:
- Kai batch (avoidance, narrow world)
- Tessa batch (rationalization, pending)
- Later: Kai micrograph patch test

Current metrics:
- distinct_function_count
- unique pressure/practice/operator coverage
- multi-pressure projection rate
- overdetermined node count
- human keep / keep-with-edit / reject (when reviewed)

These definitions must also stay frozen for the round:
- function-signature policy
- semantic-checker policy
- projection counting rules

**The eval harness is your prepare.py.** If it's not trustworthy,
autonomous iteration is hill-climbing against noise. The semantic
checker is currently too noisy for automated accept/discard. Fix
evaluation trustworthiness before automating the loop.

### 2. Single mutable surface per sweep

For **round** runs, change one intervention family per run:
- retrieval balancing OR
- admission threshold OR
- projection enrichment OR
- operator exploration policy

Not all at once. If you change retrieval and admission
simultaneously, you can't tell which one helped.

For **diagnostic** runs, bundled changes are acceptable when:
- the expected signals are orthogonal
- those signals are directly measured
- the run is logged as diagnostic rather than compared as a round run

Before the sweep, write down:
- **expected signal** — the primary metric that should move if the
  intervention worked
- **expected non-signal** — the metrics that should stay stable if
  the mechanism attribution is clean
- **null hypothesis** — what it looks like if the intervention does
  nothing meaningful

Example:
- sweep: retrieval source balancing
- expected signal: `backstory_retrieval_rate` goes up
- expected non-signal: `operator_coverage` stays the same
- null hypothesis: no shortlist change because the authored backstory
  episodes reinforce the same operator lane as generated episodes

### 3. Run ledger

Every batch run logs one row. This is the biggest immediate steal.

```
run_id  round_id  timestamp  fixture  provider  model
  admitted  fn_diversity  op_spread  pressure_cov
  overdetermination  human_keep_rate  run_status  notes
```

`run_status`: `complete` | `crash`

Intervention decisions should be tracked separately as
`decision_status`: `pending` | `keep` | `discard`

The ledger makes "did this change help?" answerable by looking at
a table instead of re-reading output dumps.

The logger is not optional now. It should ship before the next serious
comparison round.

### 4. Simplicity bias

Directly from autoresearch:

> "A small improvement that adds ugly complexity is not worth it.
> Removing something and getting equal or better results is a great
> outcome — that's a simplification win."

This is already working in the project:
- Removing behavioral prescriptions from prompts (contamination)
- Removing expected_* reads from runtime (benchmark leak)
- Removing calibration shortcuts as the default path

Make it explicit in the review checklist: **if you can remove
something and the benchmark still passes, the removal is a better
outcome than the addition.**

### 5. Accept/discard discipline

If metrics improved on the frozen harness → keep the change.
If metrics are equal or worse → revert.
If the change is a simplification with equal metrics → keep.

But: **only automate up to the shortlist.** Human still judges
whether shortlisted nodes are worth keeping. Structural metrics
can drive automated accept/discard for parameter tuning. Curatorial
quality still needs human eyes.

---

## What NOT to steal from autoresearch

- **Single-number optimization.** Your quality is multi-dimensional.
  A Pareto ledger (log all metrics, keep changes that improve at
  least one without degrading any, flag tradeoffs for human
  decision) is more honest than a scalar.

- **Fully autonomous overnight looping.** Your evaluation harness
  isn't trustworthy enough yet (semantic checks false-positive and
  false-negative). Solve evaluation before automating the loop.

- **Blind accept/revert on structural metrics alone.** Graph-valid
  is not graph-useful. Admitted-count-up doesn't mean quality-up.

---

## The right adaptation

This should be treated as an **operating model**, not as an
`autoresearch` clone.

The nearest correct translation is:

- freeze a tiny benchmark suite
- freeze the metrics for one tuning round
- mutate one intervention family at a time
- log every run
- keep only Pareto-improving changes
- stop automation at the shortlist boundary

So the "single mutable surface" is **not** literally one file.
For this project it means one intervention family per sweep, for
example:

- retrieval source balancing
- admission threshold / duplicate-function policy
- projection enrichment
- scene-state carry-forward

not mixed patches that make attribution impossible.

And the keep/revert rule is **not** "did one scalar go up?"
The honest rule is:

- keep a change if it improves at least one core metric without
  clearly degrading the others
- keep a simplification if it preserves the benchmark result
- escalate to human judgment when the change trades one dimension
  against another

This is a **Pareto discipline**, not a single-number optimizer.

---

## Automation gates

The project is not ready for blind overnight autonomy yet, but it is
ready for staged automation.

### Stage 0. Manual research loop

- run a batch
- inspect shortlist
- update the queue

This stage should be split explicitly into two modes:

- **diagnostic runs**
  - cheap
  - mixed interventions allowed
  - logged
  - no attribution claims
  - used for discovery

- **round runs**
  - frozen fixture / checker / signature policy
  - one intervention family
  - comparable
  - used for tuning decisions

The current mixed `Tessa` rerun is a good example of a **diagnostic**
run, not a round run.

### Stage 1. Structured experiment logging

- batch runner writes a machine-readable result record
- every run records batch config, intervention family, key structural
  metrics, and human verdict when reviewed

This is the current minimum viable steal from `autoresearch`.
It should ship before the next meaningful rerun, so post-patch
comparisons do not require rereading output dumps by hand.

Bundled changes are acceptable in **diagnostic** mode when the
expected signals are orthogonal and directly measured. Example:

- retrieval balancing should move `backstory_retrieval_rate`
- duplicate-function tightening should move `same_function_admit_rate`

If both metrics move, attribution is still legible enough for a
diagnostic run. If the expected signals overlap, separate the changes
or treat the result as exploratory only.

### Stage 2. Narrow auto-keep / auto-revert

Only once the harness is stable enough:

- auto-keep changes that improve structural metrics on the frozen
  suite with no obvious regressions
- auto-revert changes that fail the fixed batch harness

But still:
- no automatic claim that "better metrics = graph-ready material"
- human still reviews the shortlist

### Stage 3. Longer autonomous sweeps

Only after:

- semantic checks are less noisy
- shortlist metrics correlate with human keepability
- the benchmark suite is stable for a full tuning round

Then an overnight loop starts to make sense.

---

## The separation of concerns

| Role | Who | What |
|------|-----|------|
| Program | Human | Character seeds, evaluation rubric, constraints, research agenda |
| Search | Agent (codex) | Generate, admit, score within frozen constraints |
| Judgment | Human | Review shortlist, keep/edit/reject, steer next round |

The question bank (`00-5pro-question-bank.md`, `01-5pro-question-bank.md`)
is already playing the "program" role. The fixtures are the evaluation
harness. The prototype is the mutable surface. The gap is that the
evaluation harness isn't yet fixed and trustworthy the way autoresearch's
`prepare.py` is. Closing that gap is prerequisite to real automation.

The logger is a **comparison tool**, not proof. A cleaner ledger does
not make an unstable harness trustworthy by itself.

---

## Immediate next step

Add a `results.jsonl` logger to the batch runner. One line per run.
No autonomous loop needed yet — just comparable records that make
the parameter-tuning history readable.

Also add a small round declaration alongside it, for example
`rounds.jsonl` or one markdown file per round. The round declaration
holds the frozen evaluation surface and the hypothesis for that round.

Diagnostic runs can leave `round_id` empty or use a diagnostic-only ID.

Suggested minimum round declaration fields:

- `round_id`
- `fixture_set`
- `intervention_family`
- `expected_signal`
- `expected_non_signal`
- `null_hypothesis`
- `function_signature_version`
- `semantic_checker_version`
- `projection_counting_rule_version`
- `notes`

Suggested minimum fields:

- `run_id`
- `round_id`
- `timestamp`
- `fixture`
- `provider`
- `model`
- `batch_sequences`
- `steps`
- `candidates_per_step`
- `intervention_family`
- `intervention_note`
- `run_mode` (`diagnostic|round`)
- `bundle_path`
- `git_commit`
- `function_signature_version`
- `semantic_checker_version`
- `projection_counting_rule_version`
- `pool_size`
- `admitted_count`
- `distinct_function_count`
- `same_function_admit_rate`
- `operator_coverage`
- `practice_coverage`
- `pressure_coverage`
- `multi_pressure_projection_rate`
- `overdetermined_node_count`
- `backstory_retrieval_rate`
- `generated_retrieval_rate`
- `run_status`
- `decision_status`
- `human_verdict` (`pending|reviewed`)
- `keeper_count`
- `keeper_with_edit_count`
- `reject_count`
- `dominant_failure_mode`
- `notes`

Once the logger exists, the exact field schema should live near the
code. This note should stay focused on operating discipline and minimum
required concepts, not become the canonical schema source.

Guardrail:

- Do not compare runs across different function-signature policies or
  semantic-checker versions; start a new tuning round instead.

Additional guardrails:

- Do not compare `diagnostic` and `round` runs as if they had the same
  evidentiary status.
- Keep a small **reserve bucket** of rejected-but-interesting nodes in
  curation. A node can be duplicate-like at batch level and still be
  useful later for patch assembly.
- Watch for benchmark overfitting. If Kai/Tessa start looking "won,"
  add a fresh holdout substrate or a human spot-check pass before
  drawing strong conclusions.
