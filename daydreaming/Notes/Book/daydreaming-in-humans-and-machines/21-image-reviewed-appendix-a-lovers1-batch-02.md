# Image-Reviewed Appendix A: LOVERS1 Batch 02

This note is reconstructed from the page images, not from the OCR text layer. It continues `A.1 LOVERS1 Experience` from the post-embarrassment recovery attempt through the end of the section, covering PDF pages `332-340`.

Source images:

- [page-332-332.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-332-332.jpg)
- [page-333-333.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-333-333.jpg)
- [page-334-334.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-334-334.jpg)
- [page-335-335.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-335-335.jpg)
- [page-336-336.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-336-336.jpg)
- [page-337-337.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-337-337.jpg)
- [page-338-338.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-338-338.jpg)
- [page-339-339.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-339-339.jpg)
- [page-340-340.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-340-340.jpg)

## Sequence Overview

Across pages `332-340`, the trace completes the first `LOVERS1` episode like this:

1. Harrison Ford introduces himself back to DAYDREAMER, so the `M-CONVERSATION` and `ACQUAINTED` goals succeed.
2. Through backward other planning, DAYDREAMER tries to make him interested in her by making him believe she is attractive and by telling him she likes his movies.
3. A relaxed subgoal lets her assume a weakly plausible state like `Maybe he thinks I am cute` and later `Maybe he wants to be going out with me`.
4. She escalates to an `M-DATE` plan and asks him to dinner.
5. He declines, which collapses the `M-AGREE`, `M-DATE`, and top-level `LOVERS` goal.
6. The failed request produces anger, then the system falls back to the still-open `ENTERTAINMENT` concern, watches a movie, goes home, and closes the section with additional daydreaming goals activated from the failed romantic attempt.

## Self-Introduction Completes the Conversation

PDF page: `332`

The first visible trace on this page shows the pending `M-CONVERSATION` repair succeeding because Harrison Ford responds with his own introduction:

```text
Goal #{OB.2327: (ACTIVE-GOAL obj (MTRANS actor ...) ...)} succeeds
| I introduce myself to him.

Mtrans-plan2 fired as plan
for #{OB.2323: (ACTIVE-GOAL obj (MTRANS actor ...) ...)}
in Cx.20 sprouting Cx.21

IF   goal to MTRANS mental state to person
THEN subgoal to be VPROX that person

Goal #{OB.2476: (ACTIVE-GOAL obj (VPROX actor ...) ...)} succeeds
Perform other action #{OB.2323: (ACTIVE-GOAL obj (MTRANS actor ...) ...)}
Enter concepts in #{Cx.21: (CX)}
| Input: He introduces himself to me.
| He introduces himself to me.

Goal #{OB.2323: (ACTIVE-GOAL obj (MTRANS actor ...) ...)} succeeds
```

The prose below the trace matters: for subgoals whose objective is an action performed by another person, DAYDREAMER waits for a matching input action. Here the expected action arrives, so the subgoal succeeds.

## Backward Other Planning for Romantic Interest

PDF pages: `333-335`

With `M-CONVERSATION` complete, the trace promotes acquaintance and then starts planning from Harrison Ford's side:

```text
Acquainted-plan fired as inference in Cx.21

IF   M-CONVERSATION with person
THEN ACQUAINTED with person

Goal #{OB.2356: (ACTIVE-GOAL obj (ACQUAINTED ...) ...)} succeeds

Lovers-theme-plan fired as backward other plan
for #{OB.2348: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)}
in Cx.21 sprouting Cx.22

IF   goal for self ACTIVE-GOAL of LOVERS with a person
THEN subgoals for ROMANTIC-INTEREST in person and
     not LOVERS with anyone

| He has to be interested in me.
| He cannot be going out with anyone.
```

The explanatory paragraph on page `333` says this is a true backward-other-planning step: to get Harrison Ford to have an `ACTIVE-GOAL of LOVERS` with DAYDREAMER, the system decomposes his state into subgoals that he be romantically interested in her and believe he is not already involved with anyone else.

The next trace block keeps decomposing that target belief:

```text
Romantic-interest-plan fired as backward other plan
for #{OB.2527: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)}
in Cx.22 sprouting Cx.23

IF   goal to have ROMANTIC-INTEREST in person
THEN subgoal to have POS-ATTITUDE toward person and
     person to be ATTRACTIVE

Subgoal relaxation, #{OB.2539: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)} succeeds
| Maybe he thinks I am cute.
```

Page `334` then pushes the positive-attitude subgoal through a movie-related compliment:

```text
Positive-attitude-plan2 fired as backward other plan
for #{OB.2543: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)}
in Cx.24 sprouting Cx.26

IF   goal to have POS-ATTITUDE toward a person
THEN subgoal for person to have POS-ATTITUDE toward self MOVIES

Believe-plan1 fired as plan
for #{OB.2566: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)}
in Cx.26 sprouting Cx.27

Mtrans-plan2 fired as plan
for #{OB.2570: (ACTIVE-GOAL obj (MTRANS actor ...) ...)}
in Cx.27 sprouting Cx.28

Goal #{OB.2570: (ACTIVE-GOAL obj (MTRANS actor ...) ...)} succeeds
| I tell him I like his movies.
```

The succeeding lines on pages `334-335` show the intended inference chain:

```text
Goal #{OB.2566: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)} succeeds
Goal #{OB.2543: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)} succeeds
| He thinks I am nice.

Subgoal relaxation, #{OB.2532: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)} succeeds
| Maybe he is not going out with anyone.

Goal #{OB.2348: (ACTIVE-GOAL obj (BELIEVE actor ...) ...)} succeeds
| Maybe he wants to be going out with me.
```

The prose on page `335` is explicit that these are low-realism conclusions: after subgoal relaxation, DAYDREAMER believes with low certainty that Harrison Ford wants to be romantically involved with her.

## Dinner Invitation and Collapse of the LOVERS Plan

PDF pages: `335-336`

The trace then escalates from speculative attraction to an actual date request:

```text
M-date-plan fired as plan
for #{OB.2345: (ACTIVE-GOAL obj (M-DATE actor ...) ...)}
in Cx.29 sprouting Cx.30

IF   goal for self and other to M-DATE
THEN subgoals for other to have POS-ATTITUDE toward activity and
     self to have POS-ATTITUDE toward activity and self and
     other to M-AGREE to activity and self and
     other to ENABLE-FUTURE-VPROX and
     to wait until a later time

M-agree-plan fired as plan
for #{OB.2671: (ACTIVE-GOAL obj (M-AGREE actor ...) ...)}
in Cx.30 sprouting Cx.31
```

On page `336`, the request is made directly:

```text
Goal #{OB.2704: (ACTIVE-GOAL obj (MTRANS actor ...) ...)} succeeds
| I tell him I would like to have dinner with him at a restaurant.

Enter concepts in #{Cx.33: (CX)}
| Input: He declines.

Goal #{OB.2709: (ACTIVE-GOAL obj (MTRANS actor ...) ...)} fails in #{Cx.33: (CX)}
Goal #{OB.2671: (ACTIVE-GOAL obj (M-AGREE actor ...) ...)} fails in #{Cx.33: (CX)}
Subgoal of #{OB.2345: (ACTIVE-GOAL obj (M-DATE actor ...) ...)} failed
Goal #{OB.2345: (ACTIVE-GOAL obj (M-DATE actor ...) ...)} fails in #{Cx.33: (CX)}
Subgoal of #{OB.1882: (ACTIVE-GOAL obj (LOVERS actor ...) ...)} failed
Goal #{OB.1882: (ACTIVE-GOAL obj (LOVERS actor ...) ...)} fails in #{Cx.33: (CX)}
Attempting to backtrack
Top-level goal #{OB.1882: (ACTIVE-GOAL obj (LOVERS actor ...) ...)} fails
All possibilities failed

Goal #{OB.1882: (ACTIVE-GOAL obj (LOVERS actor ...) ...)} fails in #{Cx.33: (CX)}
| I fail at going out with him.
```

This is the hard failure point of the whole `LOVERS1` episode: the refusal on the dinner request propagates upward through `M-AGREE`, then `M-DATE`, then the top-level `LOVERS` concern.

## Anger and the Limits of the Current Planner

PDF page: `337`

The emotional summary at the top of the page removes prior motivating emotions and emits:

```text
Emotional response
| I feel really angry at him.
```

The surrounding prose is important. It explains that the negative emotion is anger, directed at Harrison Ford because he performed the action that caused the personal-goal failure. It also points out a planning limitation: people often precede a request with a softening utterance such as “What are you doing Friday night?” to remove obvious obstacles, but DAYDREAMER does not have detailed conversational planning of that form here. Instead, obstacles without available plans are only hypothesized away through subgoal relaxation, producing lines such as `Maybe he is not going out with anyone` and `Maybe he thinks I am cute`.

## Fall Back to Entertainment and Spawn New Daydreaming Goals

PDF pages: `338-339`

Once the `LOVERS` concern is terminated, control falls back to the still-open `ENTERTAINMENT` concern:

```text
Concerns:
#{OB.1420: (ACTIVE-GOAL obj (RATIONALIZATION ...) ...)} (0.97)
#{OB.2412: (ACTIVE-GOAL obj (REVERSAL ...) ...)} (0.98)
#{OB.1887: (ACTIVE-GOAL obj (ENTERTAINMENT ...) ...)} (0.6)
Switching to new top-level goal #{OB.1887: (ACTIVE-GOAL obj (ENTERTAINMENT))}

Mtrans-movie-plan fired as plan
for #{OB.1919: (ACTIVE-GOAL obj (MTRANS actor ...) ...)}
in Cx.34 sprouting Cx.35

Goal #{OB.1919: (ACTIVE-GOAL obj (MTRANS actor ...) ...)} succeeds
| I watch a movie at the Nuart.
```

The explanatory sentence below that trace states the control story plainly: after the `LOVERS` concern is terminated, processing shifts back to `ENTERTAINMENT` and DAYDREAMER watches a movie at the Nuart Theater.

Immediately after that, the failed romantic goal seeds a larger family of daydreaming concerns:

```text
Reversal-theme fired as inference in Cx.36
Rationalization-theme fired as inference in Cx.36
Activate top-level goal #{OB.2861: (ACTIVE-GOAL obj (RATIONALIZATION ...) ...)}

Revenge-theme fired as inference in Cx.7
IF   NEG-EMOTION toward person resulting from a FAILED-GOAL
THEN activate daydreaming goal to gain REVENGE against person

Activate top-level goal #{OB.1889: (ACTIVE-GOAL obj (REVENGE obj ...) ...)}

Roving-theme fired as inference in Cx.7
Activate top-level goal #{OB.1901: (ACTIVE-GOAL obj (ROVING))}

Recovery-theme fired as inference in Cx.3
Activate top-level goal #{OB.1662: (ACTIVE-GOAL obj (RECOVERY ...) ...)}

Lovers-theme fired as inference in Cx.36
Activate top-level goal #{OB.2872: (ACTIVE-GOAL obj (LOVERS actor ...) ...)}
| I want to be going out with someone.
| I feel really interested in going out with someone.
```

The prose on pages `339-340` interprets those activations: anger from the rejection activates `RATIONALIZATION`, `ROVING`, `REVENGE`, `REVERSAL`, and `RECOVERY`. Because the need states behind `LOVERS` are still unsatisfied, a fresh `LOVERS` goal is activated too, now not tied specifically to Harrison Ford.

## Go Home, Satisfy Entertainment, End A.1

PDF page: `340`

The end of the section closes out the residual performance-mode obligations:

```text
Ptrans-plan fired as plan
for #{OB.1916: (ACTIVE-GOAL obj (PTRANS actor ...) ...)}
in Cx.36 sprouting Cx.39

Goal #{OB.1916: (ACTIVE-GOAL obj (PTRANS actor ...) ...)} succeeds
| I go home.

At-plan2 fired as inference in Cx.39
IF   person PTRANS from one location to another
THEN person AT new location and
     no longer AT old location

Subgoals of #{OB.1887: (ACTIVE-GOAL obj (M-MOVIE actor ...) ...)} completed
Goal #{OB.1887: (ACTIVE-GOAL obj (ENTERTAINMENT ...) ...)} succeeds
| I succeed at being entertained.
Terminating planning for top-level goal
```

This page closes `A.1 LOVERS1 Experience`. The trace ends as a mixed result: the romantic attempt fails and produces anger plus several new daydreaming goals, but the entertainment goal succeeds and is stored as a positive experience.

The next page, [page-341-341.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-341-341.jpg), begins `A.2 REVENGE1 Daydream`.
