# Image-Reviewed Appendix A: RECOVERY3

This note is reconstructed from the page images, not from the OCR text layer. It covers `A.8 RECOVERY3 Daydream`, from PDF page `362` through the top of PDF page `367`.

Source images:

- [page-362-362.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-362-362.jpg)
- [page-363-363.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-363-363.jpg)
- [page-364-364.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-364-364.jpg)
- [page-365-365.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-365-365.jpg)
- [page-366-366.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-366-366.jpg)
- [page-367-367.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-367-367.jpg)

## Sequence Overview

`RECOVERY3` is a mixed performance/daydream trace:

1. DAYDREAMER first re-enters performance mode through an `ENTERTAINMENT` goal to get and read the mail.
2. The mail includes a UCLA Alumni directory.
3. New world-state input says Carol Burnett went to UCLA and that her telephone number is in that directory.
4. From that input, the system induces a new rule: an alumni directory for a college can contain the telephone numbers of people who attended it.
5. Serendipity recognizes this rule as relevant to the recovery goal of contacting the movie star.
6. After finishing the entertainment task, DAYDREAMER returns to the recovery concern and analogically plans to read Harrison Ford's number in the alumni directory of his college.
7. That in turn creates a new subproblem: first discover what college he attended.
8. An analogy with Brooke Shields and People magazine supplies that next step, and the episode is stored for later use.

## Entertainment Produces the Alumni Directory

PDF pages: `362-365`

The section opens with an `ENTERTAINMENT` plan rather than a direct romantic-recovery move:

```text
Entertainment-theme fired as inference in Cx.4

Activate top-level goal #{OB.1835: (ACTIVE-GOAL obj (ENTERTAINMENT ...) ...)}
| I want to be entertained.
| I feel interested in being entertained.

Entertainment-plan2 fired as plan
for #{OB.1835: (ACTIVE-GOAL obj (ENTERTAINMENT ...) ...)}
in Cx.4 sprouting Cx.7

IF   goal for ENTERTAINMENT
THEN subgoal to MTRANS mail
```

That leads through ordinary mail-handling actions:

```text
Goal #{OB.1938: (ACTIVE-GOAL obj (PTRANS actor ...) ...)} succeeds
| I go outside.

Goal #{OB.1912: (ACTIVE-GOAL obj (GRAB actor ...) ...)} succeeds
| I grab the mail.
...
Poss-mail-inf fired as inference in Cx.12
| I have the UCLA Alumni directory.
```

The serendipity turn happens on page `364`, when new input is provided:

```text
| Input: Carol Burnett went to UCLA.
| Input: Carol's telephone number is in the UCLA Alumni directory.
Adding rule #{INDUCED-RULE.1: ...}
Serendipity!! (daydreaming goal)
| What do you know!
```

The prose on that page explains the significance: DAYDREAMER infers a rule of the form “an alumni directory of a given college contains the telephone number of a person if that person went to that college.” That new rule is immediately recognized as applicable to the active goal of getting in touch with the movie star.

Before using it, however, DAYDREAMER has to finish the current entertainment sequence:

```text
Goal #{OB.2650: (ACTIVE-GOAL obj (MTRANS actor ...) ...)} succeeds
| I read her telephone number in the UCLA Alumni directory.

Entertainment-inf2 fired as inference in Cx.23
Goal #{OB.1835: (ACTIVE-GOAL obj (ENTERTAINMENT ...) ...)} succeeds
| I succeed at being entertained.
Emotion responses.
| I feel amused.
No more goals to run; switching to daydreaming mode
State changes from PERFORMANCE to DAYDREAMING
```

## Use the Directory Idea for Recovery

PDF pages: `365-367`

With performance-mode obligations finished, focus returns to `RECOVERY`:

```text
Now that DAYDREAMER has completed its ENTERTAINMENT goal, focus
shifts to RECOVERY and getting in touch with the star:

Switching to new top-level goal #{OB.1810: (ACTIVE-GOAL obj (RECOVERY ...) ...)}
Apply existing analogical plan
```

The analogical plan maps the newly learned directory trick onto the movie-star problem:

```text
Know-plan1 fired as analogical plan
...
| I have to read Harrison Ford's telephone number in the Alumni directory.
```

But that introduces a new prerequisite:

```text
Induced-rule.1 fired as analogical plan
...
Inverse-college-plan fired as plan
for #{OB.2788: (ACTIVE-GOAL obj (COLLEGE actor ...) ...)}
in Cx.30 sprouting Cx.31

| I have to know where he went to college.
```

The solution comes from another episodic analogy:

```text
Episodic reminding of #{EPISODE.3: (EPISODE rule Know-plan goal ...)}
| I remember the time I knew where Brooke Shields went to
| college by reading that she went to Princeton University in
| People magazine.

Know-plan fired as analogical plan
for #{OB.2865: (ACTIVE-GOAL obj (KNOW actor ...) ...)}
in Cx.31 sprouting Cx.32

| I have to read where he went to college in People magazine.
```

The page closes by storing the recovery episode for later reuse.

## What RECOVERY3 Shows

`RECOVERY3` does not directly ask the movie star out. Instead, it builds a future information-gathering strategy. A mundane entertainment action yields an object, the UCLA Alumni directory, which produces a new induced rule. Serendipity spots that the rule is relevant to the recovery concern, and analogical planning converts it into a plan for finding Harrison Ford's college and then his telephone number.

The next appendix section, [page-367-367.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-367-367.jpg), begins `A.9 REVENGE3 Daydream`.
