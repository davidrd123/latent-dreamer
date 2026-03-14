# Image-Reviewed Appendix A: LOVERS1 Batch 01

This note is reconstructed from the page images, not from the OCR text layer. It covers the opening of `A.1 LOVERS1 Experience`, from initialization through the first embarrassment response and the activation of `REVERSAL` and `RATIONALIZATION`.

Source images:

- [page-323-323.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-323-323.jpg)
- [page-324-324.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-324-324.jpg)
- [page-325-325.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-325-325.jpg)
- [page-326-326.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-326-326.jpg)
- [page-327-327.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-327-327.jpg)
- [page-328-328.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-328-328.jpg)
- [page-329-329.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-329-329.jpg)
- [page-330-330.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-330-330.jpg)
- [page-331-331.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-331-331.jpg)

## Sequence Overview

Across pages `323-331`, the opening `LOVERS1` trace proceeds like this:

1. DAYDREAMER initializes a reality context and activates a `LOVERS` goal because relationship-related needs are below threshold.
2. An `ENTERTAINMENT` concern becomes more urgent and takes control, yielding an `M-MOVIE` plan.
3. That plan instantiates a trip to the Nuart Theater and forces a switch from daydreaming mode to performance mode so the action can actually be carried out.
4. External input states that Harrison Ford is at the Nuart, which triggers a `VPROX` inference and a serendipitous path toward the `LOVERS` concern.
5. DAYDREAMER applies an analogical plan: get acquainted, have a conversation, ask for the time, and use that as the opening move.
6. The interaction backfires into embarrassment, which then activates `REVERSAL`, `RATIONALIZATION`, and related daydreaming goals.

## Initialization and First Concerns

PDF page: `323`

```text
Initialize DAYDREAMER
Creating initial reality context Cx.3
State changes from SUSPENDED to DAYDREAMING

*****************
Lovers-theme fired as inference in Cx.3
------------------------------------------------------------
IF   self not LOVERS with anyone and
     SEX or LOVE-GIVING or LOVE-RECEIVING or COMPANIONSHIP
         below threshold
THEN activate goal for LOVERS with a person
------------------------------------------------------------
Activate top-level goal #{OB.1882: (ACTIVE-GOAL obj (LOVERS actor ...) ...)}
| I want to be going out with someone.
```

The prose on the same page explains the initial reality context: DAYDREAMER has a job, is not in a romantic relationship, is romantically interested in Harrison Ford, is at home, and knows where the Nuart Theater is.

## Entertainment Takes Control

PDF page: `324`

```text
*****************
Entertainment-theme fired as inference in Cx.3
------------------------------------------------------------
IF   ENTERTAINMENT need below threshold
THEN activate goal for ENTERTAINMENT
------------------------------------------------------------
*****************
Activate top-level goal #{OB.1887: (ACTIVE-GOAL obj (ENTERTAINMENT ...) ...)}
| I want to be entertained.
| I feel interested in being entertained.
Concerns:
#{OB.1887: (ACTIVE-GOAL obj (ENTERTAINMENT ...) ...)} (0.6)
#{OB.1882: (ACTIVE-GOAL obj (LOVERS actor ...) ...)} (0.9) HALTED

Running emotion-driven control loop...
Switching to new top-level goal #{OB.1887: (ACTIVE-GOAL obj (ENTERTAINMENT))}
#{Cx.3: (CX)} --> #{Cx.4: (CX)}
```

The explanatory text makes the control logic explicit: the `LOVERS` goal is initially invoked as a halted concern, while `ENTERTAINMENT` becomes the most highly motivated nonhalted concern and receives the next unit of planning.

## M-MOVIE Planning and the Nuart Trip

PDF pages: `325-326`

```text
Entertainment-plan1 fired as plan
for #{OB.1887: (ACTIVE-GOAL obj (ENTERTAINMENT ...) ...)}
in Cx.3 sprouting Cx.4

IF   goal for ENTERTAINMENT
THEN subgoal for M-MOVIE

| I have to go see a movie.

#{Cx.4: (CX)} --> #{Cx.5: (CX)}
M-movie-alone-plan fired as plan
for #{OB.1897: (ACTIVE-GOAL obj (M-MOVIE actor ...) ...)}
in Cx.4 sprouting Cx.5

IF   goal for M-MOVIE alone
THEN subgoals to PTRANS to theater, MTRANS movie, and
     PTRANS back to original location

#{Cx.5: (CX)} --> #{Cx.6: (CX)}
Fact plan #{OB.286: (AT actor Nuart-theater obj ...)} found
(AT actor Nuart-theater obj ...)
Goal #{OB.1902: (ACTIVE-GOAL obj (AT actor ...) top-level-goal ...)} succeeds
Instantiating Plan
```

```text
#{Cx.6: (CX)} --> #{Cx.7: (CX)}
Ptrans-plan fired as plan
for #{OB.1923: (ACTIVE-GOAL obj (PTRANS actor ...) ...)}
in Cx.6 sprouting Cx.7

IF   goal for person to PTRANS to a location
THEN subgoal for person to KNOW that location

Goal #{OB.1933: (ACTIVE-GOAL obj (KNOW actor ...) ...)} succeeds
Instantiating plan.

Subgoals of #{OB.1923: (ACTIVE-GOAL obj (PTRANS actor ...) ...)} completed
About to perform real action but not in performance mode
No more concerns to process; switching to performance mode
State changes from DAYDREAMING to PERFORMANCE
Perform external action
Perform action goal #{OB.1923: (ACTIVE-GOAL obj (PTRANS actor ...) ...)}
Goal #{OB.1923: (ACTIVE-GOAL obj (PTRANS actor ...) ...)} succeeds
Instantiating plan.
| I go to the Nuart.
```

The trace then immediately fires `At-plan2` in `Cx.7`, inferring that once a person `PTRANS`es from one location to another, the person is at the new location and no longer at the old one.

## External Input and Serendipity

PDF pages: `327-328`

```text
Taking optional external input
Enter concepts in #{Cx.7: (CX)}
| Input: Harrison Ford is at the Nuart.

Vprox-plan1 fired as inference in Cx.7
------------------------------------------------------------
IF   AT location of person
THEN VPROX that person
```

Page `327` marks the next phase as `Serendipity!! (personal goal)`. The retrieved path on the page links the active `LOVERS` concern to the new input state through a chain of prior episodes involving `ACQUAINTED`, `M-CONVERSATION`, `MTRANS-ACCEPTABLE`, `VPROX`, `AT`, `INTRODUCTION`, `ROMANTIC-INTEREST`, `BELIEVE`, `M-DATE`, and `M-AGREE`.

The image-led takeaway is structural: the serendipity mechanism notices that the fact `Harrison Ford is at the Nuart` can be used to reactivate the previously halted `LOVERS` concern. The trace explicitly emits:

```text
Generate surprise emotion
| What do you know!

Concerns:
#{OB.1887: (ACTIVE-GOAL obj (ENTERTAINMENT ...) ...)} (0.6)
#{OB.1882: (ACTIVE-GOAL obj (LOVERS actor ...) ...)} (1.15)
Switching to new top-level goal #{OB.1882: (ACTIVE-GOAL obj (LOVERS actor))}
```

Page `328` explains the control consequence in prose: a positive `surprise` emotion is associated with the `LOVERS` concern, increasing its motivation from `0.9` to `1.15`, so it now displaces the previously current `ENTERTAINMENT` concern.

## Analogical Plan: Ask Harrison Ford for the Time

PDF pages: `328-329`

```text
Run analogical plan for #{OB.1882: (ACTIVE-GOAL obj (LOVERS actor ...) ...)}
#{Cx.9: (CX)} --> #{Cx.11: (CX)}
Apply existing analogical plan

Lovers-plan fired as analogical plan
for #{OB.1882: (ACTIVE-GOAL obj (LOVERS actor ...) ...)}
in Cx.9 sprouting Cx.11

IF   goal for LOVERS with a person
THEN subgoals for ACQUAINTED with person and
     ROMANTIC-INTEREST in person and
     person have ACTIVE-GOAL of LOVERS with self and
     self and
     person M-DATE self and
     person M-AGREE to LOVERS
```

```text
Acquainted-plan fired as analogical plan
for #{OB.2249: (ACTIVE-GOAL obj (ACQUAINTED ...) ...)}
in Cx.11 sprouting Cx.12

IF   goal to be ACQUAINTED with person
THEN subgoal for M-CONVERSATION with person

| I have to have a conversation with him.

M-conversation-plan fired as analogical plan
for #{OB.2269: (ACTIVE-GOAL obj (M-CONVERSATION ...) ...)}
in Cx.12 sprouting Cx.13
```

```text
Mtrans-acceptable-inf2 fired as analogical plan
for #{OB.2274: (ACTIVE-GOAL obj (MTRANS-ACCEPTABLE ...) ...)}
in Cx.13 sprouting Cx.14

IF   goal for MTRANS-ACCEPTABLE between self and other
THEN subgoal for self to MTRANS to other that self has ACTIVE-GOAL
         to KNOW the time

Mtrans-plan2 fired as analogical plan
for #{OB.2290: (ACTIVE-GOAL obj (MTRANS actor ...) ...)}
in Cx.14 sprouting Cx.15

IF   goal to MTRANS mental state to person
THEN subgoal to be VPROX that person

Vprox-plan1 fired as analogical plan
for #{OB.2295: (ACTIVE-GOAL obj (VPROX actor ...) ...)}
in Cx.15 sprouting Cx.16

IF   goal to be VPROX to person
THEN subgoal to be AT location of person
```

```text
Goal #{OB.2316: (ACTIVE-GOAL obj (MTRANS actor ...) ...)} succeeds
| I tell Harrison Ford I would like to know the time.
```

The prose on pages `329-330` makes the analogy explicit: the chosen prior episode suggests using a question about the time as a plausible opening move toward acquaintance.

## Negative Attitude, Embarrassment, and New Daydreaming Goals

PDF pages: `330-331`

```text
Neg-att-inf fired as inference in Cx.17
------------------------------------------------------------
IF   person is RICH and
     self is AT same location as person and
     self not WELL-DRESSED
THEN person forms NEG-ATTITUDE toward self
------------------------------------------------------------
| He does not think much of me because I am not well dressed.
```

```text
Social-esteem-monitor fired as inference in Cx.17
------------------------------------------------------------
IF   person has NEG-ATTITUDE toward self and
     self has POS-ATTITUDE toward person
THEN failure of social esteem goal for person to have POS-ATTITUDE
     toward self
------------------------------------------------------------
| I fail at him thinking highly of me.
Personal goal outcome
| I feel really embarrassed.
```

Page `331` then shows the first reaction to that failure:

```text
Reversal-theme fired as inference in Cx.17
------------------------------------------------------------
IF   NEG-EMOTION resulting from a FAILED-GOAL
THEN activate daydreaming goal for REVERSAL of failure
------------------------------------------------------------
Activate top-level goal #{OB.2412: (ACTIVE-GOAL obj (REVERSAL ...) ...)}
Concerns:
#{OB.2412: (ACTIVE-GOAL obj (REVERSAL ...) ...)} (0.98)
#{OB.1887: (ACTIVE-GOAL obj (ENTERTAINMENT ...) ...)} (0.6)
#{OB.1882: (ACTIVE-GOAL obj (LOVERS actor ...) ...)} (1.15)
#{Cx.17: (CX)} --> #{Cx.18: (CX)}
```

```text
Rationalization-theme fired as inference in Cx.17
------------------------------------------------------------
IF   NEG-EMOTION of sufficient strength resulting from
     a FAILED-GOAL
THEN activate daydreaming goal for RATIONALIZATION
     of failure
------------------------------------------------------------
Activate top-level goal #{OB.1420: (ACTIVE-GOAL obj (RATIONALIZATION ...) ...)}
Concerns:
#{OB.1420: (ACTIVE-GOAL obj (RATIONALIZATION ...) ...)} (0.97)
#{OB.2412: (ACTIVE-GOAL obj (REVERSAL ...) ...)} (0.98)
#{OB.1887: (ACTIVE-GOAL obj (ENTERTAINMENT ...) ...)} (0.6)
#{OB.1882: (ACTIVE-GOAL obj (LOVERS actor ...) ...)} (1.15)
#{Cx.17: (CX)} --> #{Cx.19: (CX)}
```

The explanatory paragraph on page `331` is important enough to preserve in substance: DAYDREAMER responds to embarrassment by activating `RATIONALIZATION` to explain Harrison Ford's negative attitude, `ROVING` to shift attention to more pleasant thoughts, `REVERSAL` to avoid similar embarrassments in the future, and `RECOVERY` to generate a future plan in which Harrison Ford has a positive attitude toward her. Those new concerns are motivated, but not as strongly as the still-active `LOVERS` concern.

This is the end of the first coherent `LOVERS1` image-review batch. The next batch should continue from the newly activated daydreaming goals on page `331`.
