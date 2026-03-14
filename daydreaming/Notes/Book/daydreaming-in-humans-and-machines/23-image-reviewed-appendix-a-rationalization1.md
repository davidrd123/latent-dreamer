# Image-Reviewed Appendix A: RATIONALIZATION1

This note is reconstructed from the page images, not from the OCR text layer. It covers `A.3 RATIONALIZATION1 Daydream`, beginning near the bottom of PDF page `345` and concluding at the top of PDF page `349`.

Source images:

- [page-345-345.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-345-345.jpg)
- [page-346-346.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-346-346.jpg)
- [page-347-347.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-347-347.jpg)
- [page-348-348.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-348-348.jpg)
- [page-349-349.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-349-349.jpg)

## Sequence Overview

`RATIONALIZATION1` imagines that the failed Harrison Ford relationship had actually succeeded, then follows the consequences until that imagined success itself produces a costly failure:

1. DAYDREAMER switches to the `RATIONALIZATION` concern after the failed `LOVERS1` attempt.
2. `Rationalization-plan1` frames the task as finding an imagined success that would `LEADTO` failure.
3. The trace hypothesizes the success case: `What if I were going out with him?`
4. Forward other planning for Harrison Ford imagines that he would need acting work and would therefore have to go to Cairo.
5. Because he is then remote from DAYDREAMER, the system activates a preservation goal for the relationship and imagines DAYDREAMER moving to Cairo too.
6. That imagined move causes a new failure: she would lose her job at May Company.
7. Since this job failure occurs in an alternative past, it generates relief and weakens the original negative emotion, but not enough to finish rationalization entirely.

## Set Up a LEADTO Failure

PDF pages: `345-346`

The trace starts by selecting the `RATIONALIZATION` goal and turning it into a `LEADTO` search:

```text
Switching to new top-level goal #{OB.1420: (ACTIVE-GOAL obj (RATIONALIZATION))}
#{Cx.6: (CX)} --> #{Cx.7: (CX)}
#{Cx.6: (CX)} --> #{Cx.8: (CX)}

Rationalization-plan1 fired as plan
for #{OB.1420: (ACTIVE-GOAL obj (RATIONALIZATION ...) ...)}
in Cx.6 sprouting Cx.7

IF   goal to RATIONALIZE failure
THEN subgoal for imagined success to LEADTO failure
```

That subgoal is made explicit one page later:

```text
Leadto-plan1 fired as coded plan
for #{OB.1441: (ACTIVE-GOAL obj (LEADTO ante ...) ...)}
in Cx.7 sprouting Cx.9

IF   goal for success to LEADTO failure
THEN hypothesize success and
     explore consequences

| What if I were going out with him?
```

The prose on page `346` explains the strategy directly: rationalization can work by imagining that if the failed goal had succeeded, it would have produced an equal or worse failure.

## Imagine Harrison Ford Moving to Cairo

PDF pages: `346-347`

The consequences of that imagined success are generated through forward other planning:

```text
Other-rule1 fired as inference in Cx.9

IF   LOVERS with a person
THEN initiate forward other planning for person

Acting-job-theme fired as inference in Cx.9

IF   actor does not have an ACTING-EMPLOY
THEN activate goal to have an ACTING-EMPLOY

Activate top-level goal #{OB.1464: (ACTIVE-GOAL obj (ACTING-EMPLOY ...) ...)}
| He would need work.
```

The next step is driven by episodic reminding:

```text
Episodic reminding of #{EPISODE.1: (EPISODE rule Episodic-rule.1 ...)}
| I remember the time he had a job with Paramount Pictures in Cairo.

Episode-rule-1 fired as analogical plan
for #{OB.1464: (ACTIVE-GOAL obj (ACTING-EMPLOY ...) ...)}
in Cx.9 sprouting Cx.10
| He would have to be in Cairo.

Ptrans1-plan fired as plan
for #{OB.1504: (ACTIVE-GOAL obj (PTRANS1 actor ...) ...)}
in Cx.11 sprouting Cx.12

Goal #{OB.1504: (ACTIVE-GOAL obj (PTRANS1 actor ...) ...)} succeeds
| He would go to Cairo.
```

The surrounding paragraph says the earlier episode of Harrison Ford going to Egypt for a film shoot is analogically reused here, so the imagined relationship success leads to Harrison Ford relocating to Cairo.

## Preserve the Relationship, Lose the Job

PDF pages: `347-349`

Once the star is in Cairo and DAYDREAMER is not, a preservation concern activates:

```text
LOVERS-p-goal fired as inference in Cx.12

IF   LOVERS with person who is RPROX one city and
     self is not RPROX that city
THEN activate preservation goal on LOVERS

| Our relationship would be in trouble.
```

The system then imagines DAYDREAMER relocating to keep the relationship viable:

```text
Rprox-plan fired as plan
for #{OB.1688: (ACTIVE-GOAL obj (RPROX actor ...) ...)}
in Cx.13 sprouting Cx.14

Ptrans1-plan fired as plan
for #{OB.1694: (ACTIVE-GOAL obj (PTRANS1 actor ...) ...)}
in Cx.14 sprouting Cx.15

Goal #{OB.1694: (ACTIVE-GOAL obj (PTRANS1 actor ...) ...)} succeeds
| I would go to Cairo.
```

That triggers the hidden cost:

```text
Job-failure fired as inference in Cx.15

IF   have EMPLOYMENT with organization which is RPROX
     one city and
     self is not RPROX that city
THEN goal to have EMPLOYMENT fails

| I would lose my job at May Company.
Personal goal outcome
| I feel relieved.
```

The prose at the top of page `349` explains why the resulting emotion is relief, not distress: this is an alternative past, so the job failure acts as a cost attached to the imagined romantic success and offsets some of the original negative emotion from the failed Harrison Ford encounter.

## What RATIONALIZATION1 Shows

`RATIONALIZATION1` is a hidden-cost rationalization. The imagined “success” of going out with Harrison Ford leads to Harrison Ford moving to Cairo, which then forces DAYDREAMER either to lose proximity to him or to move as well. Moving produces a job loss, so the original failed romance is recast as something that may have spared her a different loss. The relief is real but insufficient, which is why the trace immediately proceeds into `A.4 RATIONALIZATION2 Daydream`.
