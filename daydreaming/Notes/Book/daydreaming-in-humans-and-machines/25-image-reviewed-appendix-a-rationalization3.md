# Image-Reviewed Appendix A: RATIONALIZATION3

This note is reconstructed from the page images, not from the OCR text layer. It covers `A.5 RATIONALIZATION3 Daydream`, beginning near the bottom of PDF page `350` and concluding on PDF page `352` before `A.6 ROVING1 Daydream`.

Source images:

- [page-350-350.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-350-350.jpg)
- [page-351-351.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-351-351.jpg)
- [page-352-352.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-352-352.jpg)

## Sequence Overview

`RATIONALIZATION3` switches from imagined alternative stories to explicit minimization:

1. `Rationalization-plan3` reframes rationalization as a `MINIMIZATION` problem.
2. The coded minimization plan negates and justifies antecedents of the failure.
3. DAYDREAMER asserts that she was well dressed because she was wearing a necklace.
4. She also downgrades Harrison Ford himself: `Anyway, I do not think much of Harrison Ford.`
5. These revisions reduce the embarrassment enough that `RATIONALIZATION` succeeds and the episode is stored.

## Minimize the Failure

PDF pages: `350-351`

The section opens with a new rationalization subplan:

```text
Rationalization-plan3 fired as plan
for #{OB.1420: (ACTIVE-GOAL obj (RATIONALIZATION ...) ...)}
in Cx.19 sprouting Cx.110

IF   goal to RATIONALIZE failure
THEN subgoal to MINIMIZE failure
```

That minimization is carried out by a coded plan:

```text
Minimization-plan fired as coded plan
for #{OB.5356: (ACTIVE-GOAL obj (MINIMIZATION ...) ...)}
in Cx.110 sprouting Cx.111

IF   goal for MINIMIZATION of failure
THEN negate and justify antecedents of failure
```

The generated outputs are the important part of this section:

```text
| Anyway, I was well dressed because I was wearing a necklace.
| I feel less embarrassed.
| Anyway, I do not think much of Harrison Ford.
| I feel less embarrassed.
```

## Rationalization Succeeds

PDF pages: `351-352`

The final inference explicitly checks whether the negative emotion has been reduced enough:

```text
Rationalization-inf1 fired as inference in Cx.111

IF   NEG-EMOTION associated with failure less than a
     certain strength or POS-EMOTION associated with
     failure
THEN RATIONALIZATION of failure

| I rationalize being turned down by him.
```

That condition is met, so the top-level goal finishes:

```text
Goal #{OB.1420: (ACTIVE-GOAL obj (RATIONALIZATION ...) ...)} succeeds
Terminating planning for top-level goal
#{OB.1420: (SUCCEEDED-GOAL obj (RATIONALIZATION ...) ...)}
Removing motivations
Store episode #{OB.1420: (SUCCEEDED-GOAL obj (RATIONALIZATION ...) ...)}
```

The prose on page `352` explains why. The minimization strategy rationalizes by finding antecedents of the failure and either negating them or changing their significance. In this case, the embarrassing antecedents are reduced by asserting that DAYDREAMER was in fact well dressed and by weakening her positive attitude toward Harrison Ford. That drops the negative emotion below threshold, so the rationalization goal succeeds.

## What RATIONALIZATION3 Shows

`RATIONALIZATION3` is the direct “minimization” version of rationalization. Unlike `RATIONALIZATION1` and `RATIONALIZATION2`, it does not need an alternative scenario with hidden costs or later rewards. It simply changes the local interpretation of the failed encounter until the embarrassment is weak enough to tolerate.

The next appendix section, [page-352-352.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-352-352.jpg), begins `A.6 ROVING1 Daydream`.
