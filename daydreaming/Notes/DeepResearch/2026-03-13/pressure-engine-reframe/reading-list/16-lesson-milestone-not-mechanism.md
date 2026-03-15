# Lesson: Milestone Not Mechanism

Date: 2026-03-15

---

## What happened

We spent a long cycle optimizing generation diversity as an
intermediate mechanism:

- Q5: operator stationarity diagnosis and exploration policy design
- Q8: retrieval self-priming diagnosis and slot reservation
- Q10: evaluator split (structural checks vs. lexical noise)
- Q11: prompt compiler (JSON dump vs. given circumstances)
- Q12: practice stationarity diagnosis and state machine design
- Q13: retrieval vs. exploration ordering analysis
- 5 Pro questions written, sent, reviewed, integrated

Meanwhile:

- Temperature was set to 0.2 (near-greedy decoding)
- The system prompt said "reason carefully, return JSON only"
- The situation description specified physical blocking, not just
  circumstances

The cheapest diversity levers were never tested until late in
the cycle. When they were tested, they helped immediately —
temperature 0.7 + a "consider alternatives" system prompt
produced more varied physical openings than any of the
architectural fixes.

Each architectural diagnosis was **correct** — operator
stationarity, retrieval self-priming, practice anchoring, and
prompt-format effects are all real. But we kept treating diversity
as a sequence of isolated single-cause proofs instead of asking
the milestone question: **do we have enough usable material to
attempt the patch test?**

---

## The rules

### 1. Check the cheapest lever first

Temperature, system prompt, and sampling parameters before
scoring formulas, state machines, and evaluator refactors.

The operating model says "try the simplest thing first."
Actually do it. A 2-line parameter change should be tested
before a 200-line architectural fix.

Exception: if the current run surface is not interpretable or is
silently losing information the runtime already knows, a **minimal**
structural honesty fix may come first. Seam/projection honesty and
basic measurement stability are not the same category of work as
diversity chasing.

### 2. The milestone is the success criterion, not the mechanism

"4 usable keepers for the patch test" is the goal.
"Solve diversity" is not.

If 3 generated + 1 hand-authored fills the slots, that's
success. Don't keep iterating on diversity when the supply
is sufficient.

### 3. Don't over-isolate in discovery mode

Single-surface attribution discipline is good for tuning rounds.
But in discovery mode, stack cheap levers (temperature + system
prompt + framing) and see if the combined effect is enough.

Isolating each one is rigorous but slow when the milestone is
waiting.

### 4. Set a concrete exit condition before starting a diagnostic chain

"We stop diversity work when we can fill 4 patch slots" should
have been declared before the first batch, not after the fifth.

Before starting any multi-step diagnostic sequence, write down:

- **(a)** what milestone this serves
- **(b)** what the exit condition is
- **(c)** what the cheapest untested lever is

Check (c) before building anything.
Evaluate against (b) after each experiment.
Stop when (b) is met, even if the mechanism isn't fully
understood.

### 5. Structural work is not wasted, but it must be sequenced honestly

Q6 (deterministic lineage) and Q10 (evaluator split) were worth
doing early — they made runs interpretable and projections honest.

Q8 (retrieval cap) was a real bug fix.

Q5, Q11, Q12 are real mechanisms that solve real problems. But
they should have been sequenced **after** the cheap levers, not
before.

The distinction is:

- cheap levers first for **creative spread**
- minimal structural fixes early for **honesty and measurement**

Do the smallest amount of structural work needed to trust what the
run means. Do not let that turn into a large mechanism program before
cheap prompt/sampling tests have happened.

---

## The correct sequencing pattern

```
0. If the current run surface is not trustworthy, land the smallest
   seam/measurement fix that makes it interpretable
1. State the milestone and exit condition
2. Check sampling parameters (temperature, top_p)
3. Check system prompt stance (creative vs. constrained)
4. Try prompt-level variation (framing, contrastive hints)
5. Stack cheap levers and test combined effect
6. Only if insufficient: build structural mechanisms
7. After each experiment: evaluate against exit condition
8. Stop when the exit condition is met
```

---

## Applies to

- Generation pipeline diversity work
- Any future mechanism-design cycle in this project
- The operating model's "simplicity bias" practice
