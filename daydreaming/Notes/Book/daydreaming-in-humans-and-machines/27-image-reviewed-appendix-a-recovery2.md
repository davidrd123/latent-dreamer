# Image-Reviewed Appendix A: RECOVERY2

This note is reconstructed from the page images, not from the OCR text layer. It covers `A.7 RECOVERY2 Daydream`, from PDF page `353` through PDF page `361`.

Source images:

- [page-353-353.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-353-353.jpg)
- [page-354-354.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-354-354.jpg)
- [page-355-355.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-355-355.jpg)
- [page-356-356.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-356-356.jpg)
- [page-357-357.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-357-357.jpg)
- [page-358-358.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-358-358.jpg)
- [page-359-359.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-359-359.jpg)
- [page-360-360.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-360-360.jpg)
- [page-361-361.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-361-361.jpg)

## Sequence Overview

`RECOVERY2` is one of the richest traces in the appendix:

1. DAYDREAMER tries to recover from the failed `LOVERS1` episode by asking the movie star out again.
2. The direct plan fails because she has no realistic way to contact him and does not know his telephone number.
3. She mutates the failed action into `Suppose he tells someone else his telephone number`.
4. Serendipity recognizes that if the star tells some third person his number, that person could then tell DAYDREAMER.
5. The unknown third person is instantiated as Karen, a rich and attractive friend.
6. The trace imagines Harrison Ford becoming interested in Karen, breaking up with his girlfriend, and telling Karen his number.
7. DAYDREAMER then calls Karen, gets the number, calls Harrison Ford, and asks him out.
8. The `RECOVERY` goal succeeds and the resulting episode is stored.

## Direct Recovery Attempt Fails

PDF pages: `353-354`

The initial recovery goal is explicit:

```text
Switching to new top-level goal #{OB.1662: (ACTIVE-GOAL obj (RECOVERY ...) ...)}

Recovery-plan1 fired as plan
for #{OB.1662: (ACTIVE-GOAL obj (RECOVERY ...) ...)}
in Cx.4 sprouting Cx.5

IF   goal for RECOVERY from a LOVERS failure
THEN subgoal to ask out person again

| I have to ask him out.
```

That immediately drives toward a phone-contact problem:

```text
Vprox-plan2 fired as plan
...
| I have to call him.

M-phone-plan1 fired as plan
...
| I have to know his telephone number.
```

The attempt bottoms out on page `354`:

```text
Attempting to backtrack for top-level goal
#{OB.1662: (ACTIVE-GOAL obj (RECOVERY ...) ...)}
Top-level goal #{OB.1662: (ACTIVE-GOAL obj (RECOVERY ...) ...)} fails
All possibilities failed for #{OB.1662: (ACTIVE-GOAL obj (RECOVERY ...) ...)}
```

The prose below that failure is important: DAYDREAMER cannot generate a high-realism scenario in which she can get in touch with the star, so it tries mutations of the failed action goals instead.

## Mutation and Serendipity

PDF page: `355`

The crucial mutation is stated directly:

```text
Action mutations for #{OB.1662: (ACTIVE-GOAL obj (RECOVERY ...) ...)}
Mutation action goal #{OB.1741: (ACTIVE-GOAL obj (MTRANS actor ...) ...)}
| Suppose he tells someone else his telephone number.
```

That mutated action triggers a serendipitous retrieval:

```text
Serendipity!! (daydreaming goal)
...
| What do you know!

Know-plan3 (daydreaming goal)
for #{OB.2063: (ACTIVE-GOAL obj (KNOW actor ...) ...)}

IF   goal for person to KNOW TELNO of another
THEN subgoal for someone to MTRANS TELNO from to that person

| This person has to tell his telephone number.
```

The prose on page `355` explains the logic cleanly: if the movie star tells someone else his telephone number, that other person will know it and can later tell it to DAYDREAMER.

## Use Karen as the Bridge

PDF pages: `356-358`

The analogical plan now has to choose a concrete value for the mysterious “someone else.” The surrounding prose identifies the chosen candidate: Karen, DAYDREAMER's rich and attractive friend.

The trace builds the needed conditions:

```text
Jiffy-rule1 fired as plan

IF   goal for person to have ACTIVE-GOAL to KNOW TELNO
     of another person
THEN subgoal for person to have ACTIVE-GOAL of LOVERS
     with person

| He has to want to be going out with this person.
```

Then it instantiates Karen:

```text
Belief-pers-attr3 fired as plan
...
Fact plan #{OB.1606: (ATTRACTIVE obj Karen)} found

| He believes that Karen is attractive.
| He is interested in her.

M-break-up-plan2 fired as plan
...
| He breaks up with his girlfriend.
| He wants to be going out with her.
```

This leads to the number-transfer step:

```text
Activate top-level goal #{OB.2884: (ACTIVE-GOAL obj (KNOW actor ...) ...)}
for #MOVIE-STAR1 ...

Subgoals of #{OB.2674: (ACTIVE-GOAL obj (MTRANS actor ...) ...)} completed
| He tells her his telephone number.
```

The prose on page `358` summarizes the situation: Karen asks the movie star for his telephone number, he gives it to her, and now DAYDREAMER must get the number from Karen.

## DAYDREAMER Gets the Number and Calls Him

PDF pages: `358-360`

The remaining subproblem is to get Karen to pass the number along:

```text
Believe-plan2 fired as plan
...
| I have to tell her that I want to know his telephone number.

M-phone-plan1 fired as plan
...
| I call her.

| I tell her that I want to know his telephone number.
...
| She tells me his telephone number.
```

Page `360` then spells out the payoff:

```text
DAYDREAMER calls Karen and gets the star's number. Now she can call
him and ask him out:

| I call him.
...
| I ask him out.
```

At that point the top-level recovery goal closes:

```text
Subgoals of #{OB.1662: (ACTIVE-GOAL obj (RECOVERY ...) ...)} completed
Terminating planning for top-level goal
```

Page `361` stores the successful recovery episode.

## What RECOVERY2 Shows

`RECOVERY2` is a strong demonstration of action mutation plus serendipity. The direct plan fails because DAYDREAMER cannot realistically contact Harrison Ford. Instead of stopping there, the system mutates the failed action, finds an analogous episode in which third-party transmission solves the problem, instantiates the third party as Karen, and builds a believable route from Karen to the star's number and back to DAYDREAMER.

The next appendix section, [page-362-362.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-a-traces/page-362-362.jpg), begins `A.8 RECOVERY3 Daydream`.
