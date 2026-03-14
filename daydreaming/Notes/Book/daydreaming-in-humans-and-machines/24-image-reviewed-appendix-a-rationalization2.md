# Image-Reviewed Appendix A: RATIONALIZATION2

This note is reconstructed from the page images, not from the OCR text layer. It covers `A.4 RATIONALIZATION2 Daydream`, beginning on PDF page `349` and concluding near the bottom of PDF page `350`.

Source images:

- [page-349-349.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-349-349.jpg)
- [page-350-350.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-350-350.jpg)

## Sequence Overview

`RATIONALIZATION2` tries a different tactic from `RATIONALIZATION1`:

1. DAYDREAMER backtracks within the same `RATIONALIZATION` goal.
2. `Rationalization-plan2` looks for a case where the failed goal would `LEADTO` a later success instead of a later failure.
3. Episodic reminding recalls a case where being turned down by Irving eventually led to success with Chris.
4. Through analogical planning, DAYDREAMER imagines going to a bar, meeting someone else, and succeeding at the `LOVERS` goal in that later scene.
5. That imagined future success produces a positive emotion which is diverted into the original negative emotion from the Harrison Ford rejection.

## Failure Leads to Later Success

PDF pages: `349-350`

The plan is stated explicitly:

```text
Attempting to backtrack for top-level goal
#{OB.1420: (ACTIVE-GOAL obj (RATIONALIZATION ...) ...)}
Backtracking to next context of #{Cx.8: (CX)}

Rationalization-plan2 fired as plan
for #{OB.1420: (ACTIVE-GOAL obj (RATIONALIZATION ...) ...)}
in Cx.6 sprouting Cx.8

IF   goal to RATIONALIZE failure
THEN subgoal for failure to LEADTO success
```

The trace then recalls a different episode pattern:

```text
Episodic reminding of #{EPISODE.5: (EPISODE rule Episodic-rule.4 ...)}
| I remember the time my being turned down by
| Irving led to a success by being turned down by
| him leading to succeeding at going out with Chris.
```

The prose below that reminder summarizes the point: another method of rationalization is to imagine future scenarios in which the goal failure leads to an equal or more important success.

## Imagined Future Success at the Bar

PDF page: `350`

The remembered episode is applied analogically:

```text
Episode-rule-4 fired as analogical plan
for #{OB.1448: (ACTIVE-GOAL obj (LEADTO ante ...) ...)}
in Cx.8 sprouting Cx.16

Apply existing analogical plan

Episode-rule-2 fired as analogical plan
for #{OB.1776: (ACTIVE-GOAL obj (LOVERS actor ...) ...)}
in Cx.17 sprouting Cx.18

At-plan2 fired as analogical plan
for #{OB.1781: (ACTIVE-GOAL obj (AT actor ...) top-level-goal ...)}
in Cx.18 sprouting Cx.19

Ptrans-plan fired as plan
for #{OB.1789: (ACTIVE-GOAL obj (PTRANS actor ...) ...)}
in Cx.19 sprouting Cx.20

Goal #{OB.1789: (ACTIVE-GOAL obj (PTRANS actor ...) ...)} succeeds
| I go to Mom's.
```

The resulting emotional payoff is direct:

```text
Personal goal outcome
Emotional response
| I feel pleased.
| I am going out with him.
```

The prose at the bottom of page `350` interprets this imagined future: DAYDREAMER meets someone else, “Chris or someone similar,” at a bar, and the future `LOVERS` success generates a positive emotion that is diverted into the original negative emotion.

## What RATIONALIZATION2 Shows

`RATIONALIZATION2` is a future-compensation rationalization. Instead of showing that success with Harrison Ford would have been costly, it shows that failure with Harrison Ford could lead to a different romantic success later. That imagined later success supplies a compensating positive emotion, but the `RATIONALIZATION` sequence still continues, which leads directly into `A.5 RATIONALIZATION3 Daydream`.
