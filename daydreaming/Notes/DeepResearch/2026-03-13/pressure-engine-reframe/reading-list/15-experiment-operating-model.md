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

**The eval harness is your prepare.py.** If it's not trustworthy,
autonomous iteration is hill-climbing against noise. The semantic
checker is currently too noisy for automated accept/discard. Fix
evaluation trustworthiness before automating the loop.

### 2. Single mutable surface per sweep

Change one intervention family per run:
- retrieval balancing OR
- admission threshold OR
- projection enrichment OR
- operator exploration policy

Not all at once. If you change retrieval and admission
simultaneously, you can't tell which one helped.

### 3. Run ledger

Every batch run logs one row. This is the biggest immediate steal.

```
run_id  timestamp  fixture  provider  model  intervention
  admitted  fn_diversity  op_spread  pressure_cov
  overdetermination  human_keep_rate  status  notes
```

Status: `keep` | `discard` | `crash`

The ledger makes "did this change help?" answerable by looking at
a table instead of re-reading output dumps.

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

---

## Immediate next step

Add a `results.jsonl` logger to the batch runner. One line per run.
No autonomous loop needed yet — just comparable records that make
the parameter-tuning history readable.
