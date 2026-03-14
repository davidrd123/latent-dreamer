# Image-Reviewed Appendix A: REVENGE1

This note is reconstructed from the page images, not from the OCR text layer. It covers `A.2 REVENGE1 Daydream`, beginning on PDF page `341` and ending in the trace lines at the top of PDF page `345`, before `A.3 RATIONALIZATION1 Daydream` begins in earnest.

Source images:

- [page-341-341.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-341-341.jpg)
- [page-342-342.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-342-342.jpg)
- [page-343-343.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-343-343.jpg)
- [page-344-344.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-344-344.jpg)
- [page-345-345.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-345-345.jpg)

## Sequence Overview

`REVENGE1` imagines turning the failed Harrison Ford episode inside out:

1. DAYDREAMER selects the newly activated `REVENGE` concern after the failed `LOVERS1` attempt.
2. The revenge plan seeks a mirrored failure: the movie star should want a `LOVERS` relationship and then fail to get it.
3. To make that plausible, DAYDREAMER imagines herself as a bigger movie star than Harrison Ford and imagines him becoming interested in her.
4. He then breaks up with his girlfriend and wants to go out with her.
5. He calls her up, and she turns him down.
6. That reversal succeeds as revenge, producing a pleased emotion and a stored successful revenge episode.

## Revenge Goal Setup

PDF page: `341`

The section opens immediately by switching from the previous failure to the revenge concern:

```text
Switching to new top-level goal #{OB.1889: (ACTIVE-GOAL obj (REVENGE obj ...))}
#{Cx.8: (CX)} --> #{Cx.10: (CX)}

Revenge-plan1 fired as plan
for #{OB.1889: (ACTIVE-GOAL obj (REVENGE obj ...) ...)}
```

The first plan states the overall revenge schema: if the goal is to gain revenge against a person for causing self a failed positive-relationship goal, the subgoal is for that person to suffer failure of the same kind of positive-relationship goal.

## Make Him Want the Relationship

PDF pages: `342-343`

The trace first constructs a failed `LOVERS` goal on Harrison Ford's side:

```text
Failed-lovers-goal-plan1 fired as plan
for #{OB.1917: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)}
in Cx.10 sprouting Cx.11

IF   goal for person to have FAILED-GOAL of LOVERS
THEN subgoal for person to have ACTIVE-GOAL of LOVERS
     with person and
     then BELIEVE that person does not have ACTIVE-GOAL
     of LOVERS
```

That active goal is then decomposed in a way that mirrors the earlier `LOVERS1` sequence:

```text
Lovers-theme-plan fired as plan
for #{OB.1924: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)}
in Cx.11 sprouting Cx.12

IF   goal for self ACTIVE-GOAL of LOVERS with a person
THEN subgoals for ROMANTIC-INTEREST in person and
     not LOVERS with anyone
```

The interesting twist is how romantic interest is generated. The trace imagines DAYDREAMER becoming more of a star than the movie star:

```text
Romantic-interest-plan4 fired as plan
for #{OB.1938: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)}
in Cx.12 sprouting Cx.13

IF   goal to have POS-ATTITUDE toward a person and
     self is STAR
THEN subgoal for person to be greater STAR

Star-plan fired as plan
for #{OB.2007: (ACTIVE-GOAL obj (STAR actor ...) ...)}
in Cx.15 sprouting Cx.16

IF   goal for self to be a STAR
THEN subgoal for self to M-STUDY to be an ACTOR
```

The imagined success sequence on page `343` is explicit:

```text
Goal #{OB.2012: (ACTIVE-GOAL obj (M-STUDY actor ...) ...)} succeeds
| I study to be an actor.

Goal #{OB.2007: (ACTIVE-GOAL obj (STAR actor ...) ...)} succeeds
| I am a movie star even more famous than he is.

Goal #{OB.1969: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)} succeeds
Personal goal outcome
| I feel pleased.
| He is interested in me.
```

The revenge fantasy works by inverting the status relation from the failed real-world attempt: in the imagined scenario, she outranks him as a star, which makes his romantic interest easier to generate.

## Break His Existing Relationship

PDF page: `343`

Once his interest is in place, the trace ensures he is no longer attached to someone else:

```text
Not-lovers-plan1 fired as plan
for #{OB.1964: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)}
in Cx.17 sprouting Cx.18

M-break-up-plan2 fired as plan
for #{OB.2088: (ACTIVE-GOAL obj (M-BREAK-UP ...) ...)}
in Cx.18 sprouting Cx.19

Goal #{OB.2088: (ACTIVE-GOAL obj (M-BREAK-UP ...) ...)} succeeds
| He breaks up with his girlfriend.

Goal #{OB.1982: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)} succeeds
| He wants to be going out with me.
```

This is the revenge mirror of the earlier failure: the other person is now invested in the relationship and available for it.

## He Calls, She Rejects Him

PDF pages: `344-345`

The final revenge move is built through phone contact and a rejecting reply:

```text
Mtrans-plan2 fired as plan
for #{OB.2142: (ACTIVE-GOAL obj (MTRANS actor ...) ...)}
in Cx.20 sprouting Cx.21

Candidates: #{OB.1889: (ACTIVE-GOAL obj (REVENGE obj ...) ...)} (1.2)

Vprox-plan2 fired as plan
for #{OB.2147: (ACTIVE-GOAL obj (VPROX actor ...) ...)}
in Cx.21 sprouting Cx.23

M-phone-plan1 fired as plan
for #{OB.2160: (ACTIVE-GOAL obj (M-PHONE actor ...) ...)}
in Cx.23 sprouting Cx.25

Goal #{OB.2160: (ACTIVE-GOAL obj (M-PHONE actor ...) ...)} succeeds
| He calls me up.
```

The rejection itself lands at the bottom of page `344`:

```text
Goal #{OB.2142: (ACTIVE-GOAL obj (MTRANS actor ...) ...)} succeeds
| I turn him down.
```

And the top of page `345` closes the revenge episode:

```text
Goal #{OB.1975: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)} succeeds
Goal #{OB.1989: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)} succeeds
Subgoals of #{OB.1889: (ACTIVE-GOAL obj (REVENGE obj ...) ...)} completed

Goal #{OB.1889: (ACTIVE-GOAL obj (REVENGE obj ...) ...)} succeeds
| I get even with him.
Emotional response.
| I feel pleased.
Terminating planning for top-level goal
Removing motivating emotions
Store episode #{OB.1889: (SUCCEEDED-GOAL obj (REVENGE ...) ...)}
```

## What REVENGE1 Shows

The short prose summary on page `345` is worth preserving in substance. It explains that the revenge strategy is a table-turning strategy: cause the other person to suffer a relationship failure parallel to the one they caused for DAYDREAMER. To do that here, the system imagines a reversed status relationship, uses backward-other planning to make Harrison Ford want a relationship with DAYDREAMER, and then imagines turning him down. The result is a positive emotion and a stored revenge episode for later reuse.

The next appendix section, [page-345-345.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-345-345.jpg), continues with `A.3 RATIONALIZATION1 Daydream`.
