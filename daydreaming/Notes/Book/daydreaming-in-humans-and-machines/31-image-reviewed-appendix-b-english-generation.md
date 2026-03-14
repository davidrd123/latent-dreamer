# Image-Reviewed Appendix B: English Generation

This note is reconstructed from the page images, not from the OCR text layer. It covers the layout-sensitive core of `Appendix B. English Generation for Daydreaming`, PDF pages `374-383`.

Source images:

- [page-374-374.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-b-layout/page-374-374.jpg)
- [page-375-375.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-b-layout/page-375-375.jpg)
- [page-376-376.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-b-layout/page-376-376.jpg)
- [page-377-377.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-b-layout/page-377-377.jpg)
- [page-378-378.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-b-layout/page-378-378.jpg)
- [page-379-379.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-b-layout/page-379-379.jpg)
- [page-380-380.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-b-layout/page-380-380.jpg)
- [page-381-381.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-b-layout/page-381-381.jpg)
- [page-382-382.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-b-layout/page-382-382.jpg)
- [page-383-383.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/appendix-b-layout/page-383-383.jpg)

## Scope

Appendix B is essentially a compact English-generation specification for DAYDREAMER. It defines:

- lexical templates for concept types
- goal and emotion phrasing
- pronoun and reference handling
- global sentence alterations
- pruning rules for suppressing low-value generations
- extra templates used in minimization and episodic reminding

## B.2 Generational Templates

The appendix repeatedly uses template forms such as:

```text
head(bp) verb [text] [slot(con)]
head(bp) verb [text1] [slot(con)] [text2]
head(bp) keep head(bp)+POSS belongings
```

Selected lexicalized concept rows from page `374`:

| Type | Verb | Text | Slot |
| --- | --- | --- | --- |
| ACQUAINTED | be | acquainted |  |
| AT | be | at | obj |
| ATTRACTIVE | be | cute |  |
| COLLEGE | go | to school at | obj |
| EXECUTIVE | be | a powerful executive |  |
| HURT | be | injured |  |
| INSURED | be | insured |  |
| M-AGREE | agree |  | obj+INF |
| M-BEAT-UP | beat | up | obj |
| M-BREAK-UP | break | up with his boyfriend |  |
| M-DATE | go | out on a date |  |
| M-KISS | kiss |  |  |
| M-LOGIN | log | into | obj |
| M-MOVIE | go | see a movie |  |
| M-PHONE | call |  | to |
| M-PUTON | put | on | obj |
| M-STUDY | study | to be | obj |
| M-WORK | work |  | obj |
| POSS | have |  | obj |
| PTRANS | go | to | to |
| PTRANS1 | go | to | to |
| RICH | be | rich |  |
| RPROX | be | in | location |
| SELLS | sell |  | obj |
| STAR | be | a movie star |  |
| UNDER-DOORWAY | be | under a doorway |  |
| WELL-DRESSED | be | dressed to kill |  |

The next template table on page `375` handles multi-part constructions:

| Type | Verb | Text1 | Text2 | Slot |
| --- | --- | --- | --- | --- |
| EMPLOYMENT | have | a job with | at | org. |
| LOVERS | go+PROGR | out with |  |  |
| M-CONVERSATION | have | a conv. with |  |  |
| M-RESTAURANT | have | dinner with | at a rest. |  |
| MTRANS-ACCEPTABLE | break | the ice with |  |  |

Appendix B also specifies special cases:

- `M-PURCHASE`: `actors(con) buy obj(con) from from(con)`
- `ATRANS`: `actors(con) pay to(con)` if `obj(con)` is `CASH`, otherwise `actors(con) give obj(con) to to(con)`
- `EARTHQUAKE`: `obj(con) has an earthquake`
- `EARTHQUAKE-ONSET`: `an earthquake start in obj(con)`
- `VPROX`: if the concept is `VPROX` and `head(bp)` is one of `actors(con)`, generate `head(bp) be near others`

For `KNOW`, Appendix B distinguishes between non-person actors and person actors:

- if `actor(con)` is not a `PERSON`: `obj(con) be in actor(con)`
- otherwise: `actor(con) verb obj(con)+SIMPLIFY-TENSE+BP=tail(BP)`

It also uses possessive slots for some object types:

| Concept | Text | Slot |
| --- | --- | --- |
| TELNO | telephone number | actor |
| MOVIES | movies | obj |
| CLOTHES | clothes | obj |

## B.2.2 Daydreaming Goals and B.2.3 Goals

Page `376` gives a clean table for daydreaming-goal objectives:

| Daydream Goal | Verb | Text | Slot |
| --- | --- | --- | --- |
| RATIONALIZATION | rationalize |  | obj+GER |
| ROVING | think | about something else |  |
| REVENGE | get | even with | to |
| REVERSAL | reverse |  | obj+GER |
| RECOVERY | recover | from | obj+GER |
| REHEARSAL | rehearse |  | obj+GER |
| REPERCUSSIONS | consider |  | obj+GER |

Some personal-goal objectives are specified separately:

| Type | Verb | Text |
| --- | --- | --- |
| ENTERTAINMENT | be | entertained |
| MONEY | have | enough money |

Key goal-generation formulas:

- `SUCCEEDED-GOAL`: `head(bp) succeed at obj(con)+GER`
- `FAILED-GOAL` for an existing `LOVERS` relationship: `head(bp) lose (actors(obj(con)) - head(bp))`
- `FAILED-GOAL` for an existing `EMPLOYMENT`: `head(bp) lose head(bp)+POSS job`
- other `FAILED-GOAL`s: `head(bp) fail at obj(con)+GER`

Page `377` defines `ACTIVE-GOAL` generation:

- if `obj(con)` is an `EMPLOYMENT` and `head(bp)` is an `ACTOR`: `head(bp) need work`
- otherwise if `obj(con)` is `EMPLOYMENT`: `head(bp) verb a job with other`
- if the concept is a top-level goal: `head(bp) want obj(con)+INF`
- if it is a subgoal: `obj(con)+HAVETO`
- if embedded in an `MTRANS`, `would` is substituted for `want`

The examples on the page show the contrast clearly:

```text
I have to go to the store.
I want to go to the store.
I would like to go to the store.
```

## B.2.4 Emotions

Page `377` introduces the general emotion template:

```text
head(bp) feel [strength(con)] emotion [to(con)] [reason]
```

The next page gives a condition table that maps goal structure to emotion pairs. The exact table is layout-sensitive, but the important generated pairs are clear from the image:

- `relieved` / `regretful`
- `interested`
- `hopeful` / `worried`
- `proud` / `humiliated`
- `grateful to` / `angry at`
- `poised` / `embarrassed`
- `proud` / `ashamed`
- `broken hearted`
- `rejected`
- `amused`
- `satiated` / `starved`
- `pleased` / `displeased`

The prose under the table defines the condition columns:

- `to`: whether the emotion is directed toward a person
- `alt?`: whether the emotion came from an alternative-past scenario
- `to gl`: the active top-level goal or concern associated with the emotion
- `from gl`: the succeeded or failed goal associated with it
- `to?` and `reason`: whether a `to` slot or an explanatory reason should be generated

It also gives example outputs:

```text
I feel interested in being entertained.
I feel displeased about failing at being entertained.
```

## B.2.5-B.2.9 Attitudes, Beliefs, Objects, Persons, Lists

Pages `379-380` move from goals and emotions into lower-level lexicalization rules:

- `POS-ATTITUDE`: `head(bp) like obj(con)`
- `NEG-ATTITUDE` for persons: `head(bp) think +NEG much of obj(con)`
- other `NEG-ATTITUDE`: `head(bp) dislike obj(con)`
- `ROMANTIC-INTEREST`: `head(bp) be interested in obj(con)`

Belief generation is split too:

- if `obj(con)` is a `MENTAL-STATE` and not a `KNOW` or `BELIEVE`: `obj(con)+BP=cons(actor(con),BP)`
- otherwise: `actor(con) verb obj(con)+SIMPLIFY-TENSE+BP=tail(BP)`
- the verb is `think` when `obj(con)` is `ATTRACTIVE`, otherwise `believe`

Objects and locations are largely literalized from concept names. The page explicitly calls out examples such as:

- `MOVIE` -> `a movie`
- `FASHIONABLE-CLOTHES` -> `my cute outfit`
- `TIME-OF-DAY` -> `the time`
- `ALUMNI-DIR` -> `the obj(obj(con)) Alumni Directory`

Page `380` gives the pronoun system driven by the global `*REFERENCES*` variable:

| Concept | Nominative | Possessive | Reflexive | Objective |
| --- | --- | --- | --- | --- |
| Me | I | my | myself | me |
| FEMALE-PERSON | she | her | herself | her |
| MALE-PERSON | he | his | himself | him |

It also states that otherwise a `PERSON` is generated as:

```text
first-name(con) [last-name(con)] ['s]
```

and defines generic list generation and `NOT`:

- list with more than one element containing `Me`: `element1 element2 ... elementn and Me`
- ordinary list: `element1 element2 ... elementn-1 and elementn`
- `NOT`: `obj(con)+NEG?=negate(NEG?)`

## B.3 Global Generation Alterations

Pages `381-382` describe sentence-wide postprocessing and context-dependent changes:

- particle insertion after pronouns: `Karen took him on.` vs `Karen took on Thomas.`
- capitalization of the first word
- sentence-final punctuation, with `SURPRISE` ending in `!`
- paragraph breaks when backtracking or shifting concerns
- alternative-past contexts defaulting to conditional-present-perfect forms such as:

```text
I would have told him I would like to know the time.
```

- low-strength concepts generated as:

```text
say con
```

- subgoal-relaxation concepts in daydreaming mode rendered in past-subjunctive style:

```text
Say he thought I was cute.
```

- initial hypothetical concepts rendered as:

```text
what if con+PAST-SUBJ+QUEST
```

with examples:

```text
What if I were going out with him?
What if there were an earthquake in Los Angeles?
```

The same section also gives a few additional global templates:

- `HYPOTHESIZE`: `head(bp) imagine state(con)+GER`
- `LEADTO`: `ante(con)+POSS-SUBJ+GER lead to conseq(con)+GER`
- `ACTIVE-P-GOAL`: `Our relationship be in trouble`
- `PRESERVATION`: `head(bp) continue obj(con)+INF`

## B.4 Generation Pruning

Pages `382-383` explain how the generator suppresses unhelpful output. Rules may have optional `inf-no-gen` and `plan-no-gen` slots, and planning rules can carry one flag per subgoal:

| Flag | Meaning |
| --- | --- |
| NIL | may generate subgoal |
| T | never generate subgoal |
| 'ACTIVATE | generate subgoal only when activated |
| 'OUTCOME | generate subgoal only on outcomes |

The appendix then states the resulting policies:

- an activated goal is only generated if its objective is not already satisfied and its flag is either `NIL` or `'ACTIVATE`
- a goal outcome is only generated if it is not a daydreaming goal and its flag is either `NIL` or `'OUTCOME`
- a goal success is not generated if the objective had already been satisfied when the goal was activated
- an activated top-level goal is only generated if it is not itself a daydreaming goal
- input states such as `Harrison Ford is at the Nuart` are always generated

## B.5 Additional Generation

Page `383` covers special outputs that happen outside ordinary concept assertion:

- in minimization, a negated fact and its justification are generated as:

```text
anyway con+PAST because causes
```

with the example:

```text
Anyway, I was well dressed because I was wearing a necklace.
```

- when an attitude is inverted in minimization:

```text
anyway con
```

with the example:

```text
Anyway, I don't think much of him.
```

- episodic reminding is generated as:

```text
[I remember the time] goal+PAST by subgoal1+GER subgoal2+GER ... and by subgoalN+GER
```

This final section is especially useful because it directly explains several of the outputs that show up in Appendix A's traces.

Appendix B's image-reviewed core ends here. The next numbered pages move into the index.
