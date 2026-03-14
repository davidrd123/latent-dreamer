# Appendix A: Annotated Traces from DAYDREAMER

<!-- page: 323 -->
In this appendix, we present annotated traces of some of the daydreams and
external experiences produced by DAYDREAMER. Because of space limitations,
not all daydreams and experiences produced by the program can be reproduced
here. Portions have also been omitted from some of the traces; these are marked
by ellipses.

## A.1 LOVERS1 Experience

Initialize DAYDREAMER
Creating initial reality context Cx.3
State changes from SUSPENDED to DAYDREAMING

The program first creates an initial reality context—the context designated
as containing the state of the simulated “real world” as seen by the program.
Various initial facts are asserted into this context: DAYDREAMER has a job;
she is not involved in a romantic relationship; she is romantically interested in
Harrison Ford; she is currently at home; she knows where the Nuart Theater is;
and so on. DAYDREAMER then starts out in daydreaming mode.
FI
II

IKK

Lovers-theme

fired

IF

LOVERS

THEN

self

not

as

inference

with

in

anyone

Cx.3

and

SEX or LOVE-GIVING

or LOVE-RECEIVING

below threshold
activate goal for

LOVERS

Activate

top-level

| I want

to be going

with

goal #{0b.1882:
out

with

or COMPANIONSHIP

a person

(ACTIVE-GOAL

someone.

303

obj

(LOVERS

actor

...)...)}

<!-- page: 324 -->
| I feel

really

Concerns:
#{0B.1882:

interested

(ACTIVE-GOAL

in going

out

(LOVERS

actor

obj

with

someone.

eee

«8

COn9)

sHAETED

DAYDREAMER activates a top-level LOVERS goal because she is not involved in such a relationship and one or more of her need states subsumed by
the relationship are unsatisfied. A new concern is created and a positive motivating emotion of interest is created and associated with the new concern. The
intrinsic importance of the LOVERS goal is 0.9. This becomes the magnitude
of the motivating emotion, as well as the current value for the motivation of the
concern.
The top-level goal is stated in terms of a variable: The objective of the goal
is a relationship whose participants are the daydreamer and some appropriate
male. A variable is generated in English as “someone” or “something.”
A
concrete value for this variable will have to be found in order to achieve the
goal.
Whenever facts are asserted into a context, they are converted into English
and produced as output. A simple collection of heuristics associated with classes
of representations and particular rules, however, selectively omits generation of
certain facts in order to make the English output sound more natural.
The LOVERS goal is first invoked as a halted concern; work therefore cannot
proceed toward this goal until a serendipity occurs. In the current version of
DAYDREAMER, this is done in lieu of having the program go through several
plans to achieve this goal, all of which are unsuccessful, and then halting the
goal until a new plan is suggested through serendipity or fortuitous success of
a subgoal.
FIIO

RIK

Entertainment-theme

IF
THEN

fired

as

inference

in

Cx.3

ENTERTAINMENT need below threshold
activate goal for ENTERTAINMENT

FEI
III I IK
Activate
| I want
| I feel

top-level goal #{0B.1887: (ACTIVE-GOAL
to be entertained.
interested in being entertained.

obj

(ENTERTAINMENT...)...)}

Concerns:

#{0B.1887:
#{0B.1882:

(ACTIVE-GOAL
(ACTIVE-GOAL

obj
obj

(ENTERTAINMENT...)...)}
(LOVERS actor ...)...)}

(0.6)
(0.9)

HALTED

Next DAYDREAMER activates an ENTERTAINMENT goal because its need
for entertainment is unsatisfied. Another concern and associated motivating
emotion are activated.
Running

emotion-driven

Switching

#{CX.3:

control

to new top-level

(CX)} --> #{CX.4:

FESO
IG IO IOIII HK aK

loop...

goal #{0B.1887:

(CX)}

(ACTIVE-GOAL

obj

(ENTERTAINMENT)}

<!-- page: 325 -->
## A.1 LOVERS1 EXPERIENCE

Entertainment-plani

fired

as

for #{0B.1887: (ACTIVE-GOAL
in Cx.3 sprouting Cx.4
IF

goal for ENTERTAINMENT

THEN

subgoal

305

plan

obj

(ENTERTAINMENT...)...)}

for M-MOVIE

| I have to go see a movie.

The top-level control loop is invoked once DAYDREAMER has gotten off
the ground by applying inferences and, as a result, creating two concerns. The
most highly motivated nonhalted concern is ENTERTAINMENT
and therefore
a unit of planning is performed for this concern. The program has a rule which
states that a goal for ENTERTAINMENT may be achieved by achieving an MMOVIE subgoal. Thus a new context is sprouted in which the top-level goal
for ENTERTAINMENT is connected to a subgoal for M-MOVIE. (Hereafter, we
call this “sprouting a plan.”) This context becomes the new reality context.
#{CX.4: (CX)} --> #{CX.5:
FIII IC
M-movie-alone-plan

fired

(CX)}
as plan

for #{0B.1897: (ACTIVE-GOAL
in Cx.4 sprouting Cx.5
IF

goal

THEN

subgoals

for

PTRANS

#{CX.5:

M-MOVIE
to

back

obj

(M-MOVIE

actor

alone

PTRANS

to

theater,

to original

(CX)} --> #{CX.6:

MTRANS

movie,

and

location

(CX)}

Fact plan #{0B.286: (AT actor Nuart-theater obj
(AT actor Nuart-theater obj ...)
FEO
OI ACR IK
Goal #{0B.1902: (ACTIVE-GOAL obj (AT actor ...)
Instantiating

...)...)}

...)} found

top-level-goal

...)} succeeds

Plan

A plan is sprouted for M-MOVIE: go to a theater, watch the movie, and
. go home. The particular theater is not specified by this plan; it is initially a
variable. Next, however, a fact is found in the current reality context which
unifies with (matches) an active subgoal. This fact gives the location of a
specific theater—the Nuart Theater. Therefore, a new context is sprouted in
which the active subgoal becomes a succeeded subgoal with the variable replaced by the specific theater. In fact, in this new context, the entire plan
for ENTERTAINMENT—the top-level goal and all descendant subgoals—is instantiated with the variable bindings (values) resulting from the unification.’ In
general, a variable may be propagated from subgoal to further subgoal, so that
when a value for that variable is found, it is necessary to replace the variable
with its value in any and all subgoals containing that variable.
1The following optimization is performed in DAYDREAMER:
if it contains variables.

a subgoal is only instantiated

<!-- page: 326 -->
#{CKE6s)

(CMTE

——>

FEO
O
IG
Ptrans-plan

IF
THEN

(CXF

IGK

fired

for #{0B.1923:
in Cx.6

HICK

TRACES FROM DAYDREAMER

as plan

(ACTIVE-GOAL

sprouting

obj

(PTRANS

actor

...)...)}

Cx.7

goal for person to PTRANS to a location
subgoal for person to KNOW that location

FEO IK
Goal #{0B.1933: (ACTIVE-GOAL
Instantiating

obj

(KNOW actor

...)...)}

succeeds

plan.

A plan is sprouted for the subgoal of going to the Nuart Theater: DAYDREAMER must know where the theater is located. This subgoal then succeeds
because this fact is found in the current reality context.
Subgoals
About

of #{0B.1923:

to perform

(ACTIVE-GOAL

real

action

but

#{0B.1887:

not

obj

(PTRANS

Current

concern

waits

No more

concerns

to process;

switching

Waking up concern

#{0B.1887:

(ACTIVE-GOAL

State

changes

from DAYDREAMING

Subgoals

of #{0B.1923:

Perform

external

actor

in performance

(ACTIVE-GOAL

obj

completed

(ENTERTAINMENT...)...)}

to performance

obj

...)...)}

mode
mode

(ENTERTAINMENT...)...)}

to PERFORMANCE

(ACTIVE-GOAL

obj

(PTRANS

actor

...)...)}

completed

action

Perform action goal #{0B.1923: (ACTIVE-GOAL obj (PTRANS actor ...)...)
FESS aK
Goal #{0B.1923: (ACTIVE-GOAL obj (PTRANS actor ...)...)} succeeds
Instantiating

plan.

| I go to the Nuart.

FOCI
OIA IK
At-plan2
IF
THEN

fired

as

inference

in Cx.7

person PTRANS from one location
person AT new location and
no longer AT old location

to another

Next, all of the preconditions (subgoals) of the PTRANS subgoal have succeeded, and so this action may be performed. However, actions may only be
performed in performance mode and DAYDREAMER is currently in daydreaming mode. Therefore, the ENTERTAINMENT concern is placed into a waiting
condition. Next, the top-level control loop fails to find any concerns to process,
since all concerns are either waiting or halted. Therefore, the system enters
performance mode. Whenever the system enters performance mode, all waiting
concerns are woken up. Thus the ENTERTAINMENT concern is woken up.
Now since all of the subgoals of PTRANS have succeeded and the system 7s in
performance mode, the external action of PTRANS is performed. An inference
retracts the old location of DAYDREAMER (home) in the current reality context
and asserts the new one (the Nuart Theater).

<!-- page: 327 -->
## A.1 LOVERS1 EXPERIENCE

Taking

optional

external

Enter

concepts

in #{CX.7:

| Input:

Harrison

Ford

307

input

(CX)}

is at the

Nuart.

FCI
III IK
Vprox-plani

IF
THEN

fired

as

inference

in Cx.7

AT location of person
VPROX that person

Serendipity!!

(personal

goal)

([OB.1958: (AG. (LOVERS actor
([0B.2180: (AG. (ACQUAINTED

(OB.2199:

(AG.

([OB.2176:
obj

(M-CONVERSATION

(AG.

(OB.2213:

Me Movie-stari)) EPISODE.1]
actor Me Movie-stari)) EPISODE.2]

actor Me Movie-star1))

(MTRANS-ACCEPTABLE

(AG.

(MTRANS

(ACTIVE-GOAL

obj

actor
(KNOW

actor

EPISODE.3]

Me Movie-stari))

EPISODE.4]

Me from Me to Movie-star1

actor
obj

Me
(TIME-OF-DAY)))))

EPISODE.5]

[0B.2221: (AG. (VPROX actor Movie-star1 Me)) EPISODE.6]
[OB.2225: (AG. (AT actor Movie-star1 obj Nuart-location))]
[0B.2228: (AG. (AT actor Me obj Nuart-location))]
[0B.2205: (AG. (MTRANS actor Me from Me to Movie-star1i
obj

(INTRODUCTION)
))]

(0B.2209:

(AG.

(MTRANS

actor

Movie-star1

from Movie-stari

to Me

obj (INTRODUCTION)))]
[O0B.2184: (AG. (ROMANTIC-INTEREST obj Movie-star1))]
([OB.2187: (AG. (BELIEVE actor Movie-star1
obj (ACTIVE-GOAL obj (LOVERS actor Me Movie-star1))))]
([OB.2192: (AG. (M-DATE actor Me Movie-star1))]
(O0B.2195: (AG. (M-AGREE actor Me Movie-star1
obj (LOVERS actor Me Movie-star1)))]
HUCK
CCX) a= =>
Generate surprise

| What

CK 9s
emotion

(CX):

(CX)} --> #{CX.10:

(CX)}

do you know!

#{CX.7:
Concerns:

#{0B.1887:

(ACTIVE-GOAL

obj

(ENTERTAINMENT...)...)}

(0.6)

#{0B.1882:

(ACTIVE-GOAL

obj

(LOVERS

(1.15)

Switching

to new top-level

actor

goal #{0B.1882:

...)...)}

(ACTIVE-GOAL

obj

(LOVERS

actor))}

Now DAYDREAMER is provided with an input state: Harrison Ford happens
to be at the Nuart Theater. The English sentence is mapped to the corresponding fact by the parser and asserted into the current reality context. The fact
that the movie star and DAYDREAMER are VPROX—able to communicate—is
then inferred.
Next, an input-state-driven serendipity is detected. The input state that
Harrison Ford is AT the Nuart Theater is applicable to the active LOVERS

goal of DAYDREAMER, since: (a) in order to be LOVERS with someone, one
must be ACQUAINTED with the person, (b) in order to be ACQUAINTED
with the person, one may have an M-CONVERSATION with the person, (c) in
order to have an M-CONVERSATION with the person, it must be MTRANSACCEPTABLE for one to talk to the person, (d) in order for it to be MTRANSACCEPTABLE to talk to the person, one may MTRANS a simple favor to the

<!-- page: 328 -->
person, such as knowing the time of day, (e) in order to MTRANS

to the person,

one must be VPROX the person, and (f) in order to be VPROX to the person,
one may be AT the same location as the person. Several serendipities are in
fact possible here; the one with the longest path—shorter than some maximum
length—is chosen.
The serendipity results in an episode suitable for use by the analogical planning mechanism. This episode is associated with the LOVERS goal as a suggestion for how to achieve that goal.
As a result of serendipity, the LOVERS concern is unhalted and a surprise
emotion is generated and associated with the concern. The dynamic importance or level of motivation of the LOVERS concern thus increases from 0.9 to
## 1.15 Now the top-level control loop selects the LOVERS concern for processing,
switching from the previously current ENTERTAINMENT concern.
Run analogical

#{CX.9:
Apply

plan for #{0B.1882:

(CX)} --> #{CX.11:
existing

(ACTIVE-GOAL

obj

(LOVERS

actor

...)...)

(CX)}

analogical

plan

FESR
IIK AK
Lovers-plan

fired

for #{0B.1882:
in Cx.9

IF
THEN

as

analogical

(ACTIVE-GOAL

sprouting

plan

obj (LOVERS actor

...)...)}

Cx.i1

goal for LOVERS with a person
subgoals for ACQUAINTED with person
ROMANTIC-INTEREST in person and
person have ACTIVE-GOAL of LOVERS
self and
person M-DATE self and
person M-AGREE to LOVERS

and

with

self

and

#{CX.11: (CX)} --> #{CK.12: (CX)}
SECO
I IO CICK
Goal #{0B.2353: (ACTIVE-GOAL obj (ROMANTIC-INTEREST...)...)}
Instantiating plan.
Apply existing analogical

plan

SEO
I IGG IaK
Acquainted-plan

fired

for #{0B.2249:

(ACTIVE-GOAL

in Cx.11
IF
THEN

sprouting

as

analogical

obj

Cx.12

goal to be ACQUAINTED with
subgoal for M-CONVERSATION

| I have

to have

#{CX.12:

(CX)} --> #{CX.13:

a conversation

Apply

existing

FIO

IG OIC IK

analogical

M-conversation-plan

plan

(ACQUAINTED...)...)}

fired

person
with person
with

him.

(CX)}
plan
as

analogical

for #{0B.2269: (ACTIVE-GOAL
in Cx.12 sprouting Cx.13

obj

IF

between

goal for M-CONVERSATION

plan

(M-CONVERSATION...)...)}

personi

and

succeeds

<!-- page: 329 -->
## A.1 THEN LOVERS1

EXPERIENCE

309

person2
subgoals for MTRANS-ACCEPTABLE between
and
person2 and
personil to MTRANS to person2 something
person2 to MTRANS to personi something

#{CX.13:

(CX)} --> #{CX.14:

personl

and

(CX)}

Apply existing analogical plan
FAO
I IO IK
Mtrans-acceptable-inf2 fired as analogical plan
for #{0B.2274: (ACTIVE-GOAL obj (MTRANS-ACCEPTABLE...)...)}
in Cx.13 sprouting Cx.14
IF

goal for MTRANS-ACCEPTABLE

between

self

and

THEN

other
subgoal
to KNOW

to other

that

self

actor

...)...)}

#{CX.14:
Apply

for
the

(CX)}

existing

self
time

-->

to MTRANS

#{CX.15:

analogical

has

ACTIVE-GOAL

(CX)}
plan

FI
AIO AC RICK
Mtrans-plan2

fired

for #{0B.2290:
in Cx.14
IF
THEN

as

analogical

(ACTIVE-GOAL

sprouting

plan

obj

(MTRANS

Cx.15

goal to MTRANS mental state to person
subgoal to be VPROX that person

#{CX.15:
Apply

(CX)} --> #{CX.16:

existing

analogical

(CX)}
plan

FEI
II I IR 1k
Vprox-plani fired as analogical

plan

for #{0B.2295:

(VPROX

in Cx.15
IF
THEN

(ACTIVE-GOAL

sprouting

obj

actor

...)...)}

Cx.16

goal to be VPROX to person
subgoal to be AT location of person

FI
IOI
OR AOI
Goal #{0B.2303: (ACTIVE-GOAL obj (AT actor ...)
top-level-goal ...)} succeeds
FAO
OIC IO IOC
Goal #{0B.2300: (ACTIVE-GOAL obj (AT actor ...)
top-level-goal ...)} succeeds
FOO IO IC I IC IC
Goal #{0B.2300: (ACTIVE-GOAL obj (VPROX actor ...)
top-level-goal ...)} succeeds
Subgoals of #{0B.2369: (SUCCEEDED-GOAL obj (VPROX...)...)} completed
Subgoals of #{0B.2316: (ACTIVE-GOAL obj (MTRANS actor ...)...)} completed
Perform

external

action

Perform action goal #{0B.2316: (ACTIVE-GOAL obj (MTRANS actor ...)...)
FEO
COO ICCC
Goal #{0B.2316: (ACTIVE-GOAL obj (MTRANS actor ...)...)} succeeds
| I tell

Harrison

Ford

I would

like

to know

the

time.

Planning for the LOVERS goal then proceeds as suggested by the analogical
plan associated with the goal during the above serendipity. DAYDREAMER

<!-- page: 330 -->
finally performs the action of asking the movie star for the time. The program

will ask anyone for the time, unless (a) a high realism plan to achieve a LOVERS
goal with the person has been previously daydreamed

and incorporated into

episodic memory, and (b) this episode has a low desirability (i.e., the outcome
is negative). Thus, given a new person, DAYDREAMER will attempt to start a
conversation just in case she decides she is interested in the person (for example,
if the other person likes the same things that she does). Here, the subgoal
for ROMANTIC-INTEREST succeeds immediately because DAYDREAMER is
already romantically interested in Harrison Ford.
FAO
II II
Neg-att-inf fired
IF

THEN

as

inference

person is RICH and
self is AT same location as person and
self not WELL-DRESSED
person forms NEG-ATTITUDE toward self

| He does not think
FEO RIK

much

Believe-plani

as

IF
THEN

in Cx.17

MTRANS
person

FEI

fired

me

because

inference

mental state
BELIEVE self

IIR

of

I am

not

well

dressed.

in Cx.17

to person
mental state

ACK

Mtrans-acceptable-inf2
IF

self
KNOW

MTRANS to
the time

THEN

MTRANS-ACCEPTABLE

fired

other

as

inference

that

self

has

between

self

and

fired

inference

in Cx.17

ACTIVE-GOAL

to

other

SOO

IOI

III

RICK

Social-esteem-monitor

IF
THEN

as

in

Cx.17

person has NEG-ATTITUDE toward self and
self has POS-ATTITUDE toward person
failure of social esteem goal for person
toward self

| I fail

at him thinking

Personal
| I feel

goal outcome
really embarrassed.

highly

to have

POS-ATTITUDE

of me.

DAYDREAMER now infers that because she has spoken to the movie star
and she is not well dressed, he forms a negative attitude toward her. Thus
her SOCIAL ESTEEM personal goal fails and an emotional response of embarrassment is generated. However, since she has asked him the time, it is now
acceptable for the two to communicate.
FR

I A

RR

I kk okickcok

<!-- page: 331 -->
## A.1 LOVERS1 EXPERIENCE Reversal-theme
IF
THEN

fired

as

311

inference

in Cx.17

NEG-EMOTION resulting from a FAILED-GOAL
activate daydreaming goal for REVERSAL of failure

FEO

III

Activate

top-level

goal #{0B.2412:

(ACTIVE-GOAL

obj

(REVERSAL...)...)

Concerns:
#{0B.2412: (ACTIVE-GOAL obj
#{0B.1887: (ACTIVE-GOAL obj
#{0B.1882: (ACTIVE-GOAL obj
#{CX.17: (CX)} --> #{CX.18:
FEO
I RICK

(REVERSAL...)...)} (0.98)
(ENTERTAINMENT...)...)} (0.6)
(LOVERS actor ...)...)} (1.15)
(CX)}

Rationalization-theme

as

inference

in Cx.17

NEG-EMOTION of sufficient
a FAILED-GOAL
activate daydreaming goal
of failure

strength

resulting

IF
THEN

FEI
IOI IO
Activate top-level

fired

for

goal #{0B.1420:

from

RATIONALIZATION

(ACTIVE-GOAL

obj

Concerns:
#{0B.1420:

(ACTIVE-GOAL

obj

(RATIONALIZATION...)...)}

#{0B.1887:
#{0B.1882:

(ACTIVE-GOAL
(ACTIVE-GOAL

obj
obj

(ENTERTAINMENT...)...)}
(LOVERS actor ...)...)}

#{0B.2412:
#{CX.17:

(RATIONALIZATION...)...)
(0.97)

(ACTIVE-GOAL obj (REVERSAL...)...)} (0.98)
(CX)}

--> #{CX.19:

(0.6)
(1.15)

(CX)}

In response to the negative emotion of embarrassment, DAYDREAMER activates a number of daydreaming goals: RATIONALIZATION (rationalize the

fact that Harrison Ford has a negative attitude toward her), ROVING (divert
attention from embarrassment to more pleasant thoughts), REVERSAL (plan
to avoid similar embarrassments in the future), and RECOVERY (generate a
future plan for Harrison Ford to have a positive, rather than negative, attitude
toward her). The level of motivation for these concerns is not as high as the
active LOVERS concern. However, even if they were higher, they would not
be processed until later since daydreaming goals are not processed when the
system is in performance mode.
FIO
I I ICR IAC

Goal #{0B.2331:

#{CX.17:

(ACTIVE-GOAL

obj

(CX)} --> #{CX.20:

(CX)}

FAI
IOC IIR
Mtrans-plan2 fired as plan
for #{0B.2327: (ACTIVE-GOAL
in Cx.17 sprouting Cx.20
IF
THEN

obj

(MTRANS-ACCEPTABLE...)...)}

(MTRANS

actor

succeeds

...)...)}

goal to MTRANS mental state to person
subgoal to be VPROX that person

FEI
ICI IAC
Goal #{0B.2444: (ACTIVE-GOAL

obj

(VPROX

actor

...)...)}

succeeds

<!-- page: 332 -->
Subgoals

of #{0B.2327:

Perform

external

(ACTIVE-GGAL

actor

(MTRANS

obj

FROM DAYDREAMER

TRACES

= +) .«.)

}rcompleted

action

Perform action goal #{0B.2327: (ACTIVE-GOAL obj (MTRANS actor ...)...)
FEO
GRC
C
Goal #{0B.2327: (ACTIVE-GOAL obj (MTRANS actor ...)...)} succeeds
| I introduce myself to him.
FEO
I I III RK
Believe-plani
IF
THEN

MTRANS
person

#{CX.20:

fired

as

inference

mental state
BELIEVE self

in Cx.20

to person
mental state

(CX)} --> #{CK.21:

(CX)}

FERC
IOI IO II
Mtrans-plan2

fired

for #{0B.2323:
in Cx.20

IF
THEN

as

plan

(ACTIVE-GOAL

sprouting

obj

(MTRANS

actor

...)...)}

Cx.21

goal to MTRANS mental state to person
subgoal to be VPROX that person

FeGOO
IRA AK
Goal #{0B.2476: (ACTIVE-GOAL obj (VPROX actor ...)...)} succeeds
Subgoals of #{0B.2323: (ACTIVE-GOAL obj (MTRANS actor ...)...)} completed
Perform other action #{0B.2323: (ACTIVE-GOAL obj (MTRANS actor ...)...)
Enter concepts in #{CX.21: (CX)}
Input: He introduces himself to me.
| He introduces himself to me.

Je OG
ok toktok
Goal

#{0B.2323:

(ACTIVE-GOAL

obj

(MTRANS

actor

...)...)}

succeeds

In order to achieve the M-CONVERSATION subgoal, DAYDREAMER introduces herself to the movie star who must then introduce himself to her. When
the preconditions (subgoals) succeed for a subgoal whose objective is an action performed by another person, DAYDREAMER waits for an input action.
If the input action unifies with (matches) the objective of the subgoal, then
the subgoal succeeds. Otherwise the subgoal fails. Here the expected action is
performed and the subgoal succeeds.
FER ICI
Believe-plani fired
IF
THEN

MTRANS
person

as

inference

mental state
BELIEVE self

in Cx.21

to person
mental state

FOO
IO IO IOI AIK
M-conversation-plan
IF

THEN

fired

as

inference

in Cx.21

MTRANS-ACCEPTABLE between personi and
person2 and
personi MTRANS to personi something and
person2 MTRANS to personi something
M-CONVERSATION between personi and

<!-- page: 333 -->
person2

FIC

III CK

Acquainted-plan

IF
THEN

fired

as

inference

in Cx.21

M-CONVERSATION with person
ACQUAINTED with person

FEII IK
Goal

#{0B.2356:

(ACTIVE-GOAL

HICK. 21% (CX)I} <->
FIO
IK IIR
Lovers-theme-plan

fired

(ACQUAINTED...)...)}

obj

other

plan

(BELIEVE

actor

goal for self ACTIVE-GOAL of LOVERS with
subgoals for ROMANTIC-INTEREST in person
not LOVERS with anyone

| He has to be interested

in me.

| He cannot

with

be going

out

succeeds

(CX):

as backward

for #{0B.2348: (ACTIVE-GOAL
in Cx.21 sprouting Cx.22
IF
THEN

obj

#(CX 322%

...)...)}

a person
and

anyone.

DAYDREAMER has the subgoal for Harrison Ford to have the goal to be
romantically involved with her. A plan for this subgoal is generated through
backward other planning: applying a rule stated in terms of the self to another
person. Thus in order for Harrison to have an ACTIVE-GOAL of LOVERS with
DAYDREAMER, he must have ROMANTIC-INTEREST toward her and must
BELIEVE he is not LOVERS with anyone.
FEII RIK
Romantic-interest-plan

for #{0B.2527:
in Cx.22
IF
THEN

fired

(ACTIVE-GOAL

sprouting

as backward

obj

other

(BELIEVE actor

#{CX.23:

(CX)} --> #{CX.24:
relaxation,

...)...)}

Cx.23

goal to have ROMANTIC-INTEREST in person
subgoal to have POS-ATTITUDE toward person
person to be ATTRACTIVE

Subgoal

plan

and

(CX)}

#{0B.2539:

(ACTIVE-GOAL

obj

(BELIEVE

actor

...)...)}

succeeds

| Maybe he thinks I am cute.
#{CX.24: (CX)} --> #{CX.25: (CX)}
#{CX.24: (CX)} --> #{CX.26: (CX)}

Again through backward other planning, DAYDREAMER breaks down the
subgoal for Harrison to be romantically interested in her into subgoals for him
to have a positive attitude toward her and to think she is attractive. There are
no plans to make Harrison think she is attractive. Therefore, DAYDREAMER
employs the heuristic that a desired mental state of another person may be assumed if there is no information to the contrary. Although this is a heuristic for

<!-- page: 334 -->
daydreaming, it is also used in performance mode since the only other alternative would be to declare failure of the subgoal, which would result in a top-level

goal failure (since backtracking is not permitted in performance mode).
FEO
I AI IIOK
Positive-attitude-plan2

for #{0B.2543:
in Cx.24
IF
THEN

fired

(ACTIVE-GOAL

sprouting

as backward

obj

(BELIEVE

other

plan

actor

...)...)}

Cx.26

goal to have POS-ATTITUDE toward a person
subgoal for person to have POS-ATTITUDE toward

self

MOVIES
#{CX.26: (CX)} --> #{CX.27:
BOIS
IOC IK KK
Believe-plani

fired

as plan

for #{0B.2565: (ACTIVE-GOAL
in Cx.26 sprouting Cx.27
IF
THEN

(CX)}

obj

(BELIEVE

actor

...)...)}

goal for person to BELIEVE self mental state
subgoal to MTRANS mental state to person

#{CX.27:

(CX)} --> #{CX.28:

(CX)}

FEI
IC AIOK
Mtrans-plan2

fired

as

plan

for #{0B.2570: (ACTIVE-GOAL
in Cx.27 sprouting Cx.28
IF
THEN

obj

(MTRANS

actor

...)...)}

goal to MTRANS mental state to person
subgoal to be VPROX that person

FEC
I
aC IR
Goal #{0B.2575: (ACTIVE-GOAL obj (VPROX actor ...)...)} succeeds
Subgoals of #{0B.2570: (ACTIVE-GOAL obj (MTRANS actor ...)...)} completed
Perform

external

action

Perform action goal #{0B.2570: (ACTIVE-GOAL obj (MTRANS actor ...)...)
FEO
IC II IK
Goal #{0B.2570: (ACTIVE-GOAL obj (MTRANS actor ...)...)} succeeds
| I tell him I like his movies.
FICO
II I IGE
Believe-plani
IF
THEN

MTRANS
person

fired

as

inference

mental state
BELIEVE self

in Cx.28

to person
mental state

SOOO
IOI IK aK

Goal

#{0B.2565:

(ACTIVE-GOAL

obj

(BELIEVE

actor

...)...)}

succeeds

Subgoals of #{0B.2610: (SUCCEEDED-GOAL obj (BELIEVE...)...)} completed
Subgoals of #{0B.2543: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} completed
FESO
III AIK
Goal #{0B.2543: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds
| He thinks highly of me.
Subgoals of #{0B.2527: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} completed
FOSS I AIOAI IK
Goal #{0B.2527: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds

<!-- page: 335 -->
## A.1 LOVERS1 EXPERIENCE

| He is interested

#{CX.28:
Subgoal
| Maybe

315

in me.

(CX)} --> #{CX.29:

(CX)}

relaxation, #{0B.2532: (ACTIVE-GOAL
he is not going out with anyone.

obj

(BELIEVE

actor

...)...)}

succeeds

FEI II IAIK
Goal #{0B.2532: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds
Subgoals of #{0B.2348: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} completed
FICCI
II AIK
Goal #{0B.2348: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds
| Maybe he wants to be going out with me.

#{CX.29:

(CX)} --> #{CX.30:

(CX)}

DAYDREAMER tells the movie star that she likes his movies in order to
get him to have a positive attitude toward her. After subgoal relaxation, DAY-

DREAMER

believes with low realism (certainty) that Harrison Ford wants to

be romantically involved with her.
FAO
IIH OK
M-date-plan

fired

as plan

for #{0B.2345: (ACTIVE-GOAL
in Cx.29 sprouting Cx.30
IF
THEN

obj

(M-DATE

to have POS-ATTITUDE toward activity
and
self to have POS-ATTITUDE toward activity and self and
other to M-AGREE to activity and self and

#{CX.30:

(CX)} --> #{CX.31:

FA

ICICI II

IO

M-agree-plan

fired

and

(CX)}

as plan

for #{0B.2671: (ACTIVE-GOAL
in Cx.30 sprouting Cx.31

THEN

...)...)}

goal for self and
other to M-DATE
subgoals for other

other to ENABLE-FUTURE-VPROX
to wait until a later time

IF

actor

obj

(M-AGREE

actor

...)...)}

goal for self and
other to M-AGREE to something
subgoals for self to MTRANS to other that self
ACTIVE-GOAL for that something and
for other to MTRANS to self that other BELIEVE
for that something

#{CX.31:

(CX)} --> #{CX.32:

BELIEVE
ACTIVE-GOAL

(CX)}

FOI
IO AOR IK
Mtrans-plan2

fired

as plan

for #{0B.2704: (ACTIVE-GOAL
in Cx.31 sprouting Cx.32

obj

(MTRANS

IF

goal

state

to person

THEN

subgoal

FRO

OI

AO

to MTRANS

OK

mental

to be VPROX
I

that

person

actor

...)...)}

<!-- page: 336 -->
Goal #{0B.2714: (ACTIVE-GOAL obj (VPROX actor ...)...)} succeeds
Subgoals of #{0B.2704: (ACTIVE-GOAL obj (MTRANS actor ...)...)} completed
Perform

external

action

Perform action goal #{0B.2704: (ACTIVE-GOAL obj (MTRANS actor ...)...)
FAI
ICI IAC I
Goal #{0B.2704: (ACTIVE-GOAL obj (MTRANS actor ...)...)} succeeds
| I tell

him

I would

like

to have

dinner

with

him

inference

in Cx.32

at a restaurant.

actor

...)...)}

FEO
Believe-plani

IF
THEN

MTRANS
person

#{CX.32:
FEO

fired

mental state
BELIEVE self

to person
mental state

(CX)} --> #{CX.33:
OCI

III

Mtrans-plan2

fired

for #{0B.2709:
in Cx.32
IF
THEN

as

(CX)}

as plan

(ACTIVE-GOAL

sprouting

obj

(MTRANS

Cx.33

goal to MTRANS mental state to person
subgoal to be VPROX that person

FEO
IOC IORI IIOK
Goal #{0B.2758: (ACTIVE-GOAL obj (VPROX actor ...)...)} succeeds
Perform other action #{0B.2709: (ACTIVE-GOAL obj (MTRANS actor ...)...)
Enter concepts in #{CX.33: (CX)}
| Input: He declines.
FAC IC II I A ICIKk
Goal #{0B.2709: (ACTIVE-GOAL obj (MTRANS actor ...)...)} fails in #{CX.33:
SAAC aK
Believe-plan2
IF
THEN

MTRANS
person

fired

as

inference

(CX)}

in Cx.33

NOT mental state to person
BELIEVE self NOT mental state

He 2 A ee 2 2 og2 2 2 ie2 2 2 oieok

Goal #{0B.2671: (ACTIVE-GOAL obj (M-AGREE actor ...)...)} fails in #{CX.33:
Subgoal of #{0B.2345: (ACTIVE-GOAL obj (M-DATE actor ...)...)} failed
FOO
IOI III OK

(CX)}

Goal #{0B.2345:
(ACTIVE-GOAL obj (M-DATE actor
Subgoal of #{0B.1882:
(ACTIVE-GOAL obj (LOVERS

...)...)} fails
actor ...)...)}

in #{CX.33:
failed

(CX)}

FESO AIO
Goal #{0B.1882: (ACTIVE-GOAL

...)...)}

in #{CX.33:

(CX)}

Attempting

to backtrack

Top-level

goal #{0B.1882:

All

possibilities

obj

(ACTIVE-GOAL

Terminating planning
Leaf context #{CX.33: (CX)}
[0B.1882: (FG. (LOVERS actor
[NB.2518: (SG. (ACQUAINTED
(SG.

actor

obj

(LOVERS

actor

fails

...)...)}

fails

failed

FOO OCIS CII IOIICI
Goal #{0B.1882: (ACTIVE-GOAL obj
| I fail at going out with him.

[0B.2365:

(LOVERS

(LOVERS

actor

...)...)}

Me Movie-star1))]
actor Me Movie-star1))]

(ROMANTIC-INTEREST

obj

Movie-star1))]

fails

in #{CX.33:

(CX)}

<!-- page: 337 -->
[0B.2648: (SG. (BELIEVE actor Movie-star1
obj (ACTIVE-GOAL obj (LOVERS actor Me Movie-star1))))]
[OB.2622: (SG. (BELIEVE actor Movie-star1

obj (ROMANTIC-INTEREST
[O0B.2547:

(SG.

(BELIEVE

obj Me)))]

actor

Movie-stari

obj (ATTRACTIVE actor Me)))]
[OB.2617:
obj

(SG.

(BELIEVE

(POS-ATTITUDE

actor

([0B.2610: (SG. (BELIEVE
obj (BELIEVE actor Me

obj
(0B.2581:

obj

(SG.

(POS-ATTITUDE

Movie-star1

obj Me)))]

actor

Movie-star1

(POS-ATTITUDE

obj (MOVIES obj Movie-star1)))))]

(MTRANS

Me from Me to Movie-star1

actor

obj (MOVIES obj Movie-star1))))]

[0B.2577: (SG. (VPROX actor Movie-star1 Me))]
[OB.2643: (SG. (BELIEVE actor Movie-star1i
obj (NOT obj (LOVERS actor Movie-star1))))]
([0B.2807: (FG. (M-DATE actor Me Movie-star1))]
(OB.2341: (AG. (M-AGREE actor Me Movie-star1
obj (LOVERS actor Me Movie-stari)))]
Removing

motivating

Emotional

responses

| I feel

really

emotions

angry

at him.

In order to go on an M-DATE with the movie star, DAYDREAMER has to
get the movie star to agree to have dinner at a restaurant. She asks him if
he would like to have dinner, but he declines. Thus the subgoal for the movie
star to MTRANS that he would like to have dinner fails, resulting in failure
of the M-AGREE subgoal, in turn resulting in failure of the M-DATE subgoal,
finally resulting in the failure of the top-level LOVERS goal. The action of
Harrison—his negative MTRANS—is noted in the reality context as the cause
of the top-level goal fuilure. The LOVERS concern fails, its motivating interest
emotion is terminated, and an emotional response is generated. The resulting
negative emotion is directed toward the movie star, since he is the one who
performed the action which caused the personal goal failure. This is generated
as an anger emotion.
DAYDREAMER bluntly asks Harrison Ford if he would like to have din-

ner. However, Gibbs and R. A. G. Mueller (1988) have observed that people
prefer to preface such a request with an utterance designed to remove the greatest potential obstacle to achieving the request. Thus one might more likely
first say “What are you doing Friday night?”. In DAYDREAMER, obstacles
(or subgoals) involving the other person for which no plans are available are
merely hypothesized away (through subgoal relaxation); thus in the trace we
have Maybe he is not going out with anyone and Maybe he thinks I am cute.
Although detailed planning of conversational utterances is not performed by
DAYDREAMER, avoiding embarrassment resulting from a negative response to
a request could be accomplished via the REVERSAL daydreaming goal. Rules
would have to be added to infer failure of a social regard preservation goal upon
a negative response to a request for a date. If no plans were available to ensure a

<!-- page: 338 -->
positive response to the request (such as plans for verbally removing obstacles),
the system would simply not perform the request at all.?

#{CX.33:

(CX)} --> #{CX.34:

(CX)}

Concerns:

#{0B.1420:
#{0B.2412:
#{0B.1887:

(ACTIVE-GOAL
(ACTIVE-GOAL
(ACTIVE-GOAL

obj
obj
obj

(RATIONALIZATION...)...)} (0.97)
(REVERSAL...)...)} (0.98)
(ENTERTAINMENT...)...)} (0.6)

Switching

to new top-level

goal #{0B.1887:

#{CX.34:

(CX)} --> #{CX.35:

(CX)}

(ACTIVE-GOAL

obj

(ENTERTAINMENT)}

FAIR
II IAC K
Mtrans-movie-plan

for #{0B.1919:
in Cx.34
IF
THEN

sprouting

as plan

obj

(CX)}

(MTRANS

actor

...)...)}

Cx.35

goal to MTRANS a movie
subgoal to be AT location

#{CX.35:

Fact

fired

(ACTIVE-GOAL

-->

#{CX.36:

plan #{0B.286:

of movie

(CX)}

(AT actor

Nuart-theater

obj

...)} found

obj

...)

top-level-goal

...)}

FOICRI I IO K
Goal #{0B.2826: (ACTIVE-GOAL obj (AT actor ...)
Subgoals of #{0B.1919: (ACTIVE-GOAL obj (MTRANS

top-level-goal
actor ...)...)}

...)} succeeds
completed

FOSS
II Hi K
Goal #{0B.2819: (ACTIVE-GOAL
Instantiating

Perform

external

Perform

action goal #{0B.1919:

succeeds

action

FEO
IIA
II
Goal #{0B.1919: (ACTIVE-GOAL
| I watch

(AT actor

plan

a movie

at the

After the LOVERS
TERTAINMENT
Theater.

(ACTIVE-GOAL
obj

(MTRANS

obj

(MTRANS

actor

...)...)

actor

...)...)}

succeeds

Nuart.

concern is terminated, processing shifts back to the EN-

concern

and DAYDREAMER

watches

a movie

at the Nuart

SOOO
IO I IOI
Reversal-theme

fired

as

inference

in

Cx.36

IF
THEN

NEG-EMOTION resulting from a FAILED-GOAL
activate daydreaming goal for REVERSAL of failure

JRO

I I Iekeaieakeakcakakakakakak

Activate top-level goal #{0B.2851: (ACTIVE-GOAL obj
#{CX.36: (CX)} --> #{CX.37: (CX)}
FESO
IOIIIK OK
Rationalization-theme fired as inference in Cx.36
IF

NEG-EMOTION

of sufficient

strength

resulting

(REVERSAL...)...)

from

a FAILED-GOAL
THEN

activate

daydreaming

2DAYDREAMER

goal

for

RATIONALIZATION

deactivates a goal if all episodes recalled for achieving that goal have a

negative desirability rating.

<!-- page: 339 -->
## A.1 LOVERS1 of

EXPERIENCE

319

failure

FIO
IO I IK
Activate top-level goal #{0B.2861: (ACTIVE-GOAL
FIO
RI IOK
Revenge-theme fired as inference in Cx.7
IF
THEN

obj

(RATIONALIZATION...)...)

NEG-EMOTION toward person resulting from a FAILED-GOAL
activate daydreaming goal to gain REVENGE against
person

Activate

top-level

goal #{0B.1889:

(ACTIVE-GOAL

obj

(REVENGE

obj

...)...)

FEI IK
Roving-theme

IF
THEN

fired

as

inference

in Cx.7

NEG-EMOTION of sufficient
a FAILED-GOAL
activate daydreaming goal

strength
for

resulting

from

ROVING

FIO
IORI IO AIC ACK

Activate

top-level

goal #{0B.1901:

(ACTIVE-GOAL

obj

(ROVING))

SRI
IR IR
Recovery-theme

IF
THEN

fired

as

goal

Lovers-theme

as

THEN

in Cx.3

NEG-EMOTION resulting from a FAILED-GOAL
activate daydreaming goal for RECOVERY of failure

FEO
ICC
Activate top-level
FEO
I IIR

IF

inference

fired

#{0B.1662:

inference

(ACTIVE-GOAL

LOVERS

(RECOVERY...)...)

in Cx.36

self not LOVERS with anyone and
SEX or LOVE-GIVING or LOVE-RECEIVING
below threshold
activate goal for

obj

with

or COMPANIONSHIP

a person

FA
II IIR

Activate
| I want

top-level goal #{0B.2872: (ACTIVE-GOAL
to be going out with someone.
interested

in going

out

with

obj

(LOVERS

actor

...)...)

| I feel

really

#{CX.36:

(CX)} --> #{CX.39:

someone.

Concerns:
#{0B.2872:
#{0B.2861:
#{0B.2851:
#{0B.1420:
#{0B.2412:
#{0B.1887:
#{0B.1901:

(ACTIVE-GOAL
(ACTIVE-GOAL
(ACTIVE-GOAL
(ACTIVE-GOAL
(ACTIVE-GCAL
(ACTIVE-GOAL
(ACTIVE-GOAL

obj
obj
obj
obj
obj
obj
obj

(LOVERS actor ...)...)} (0.9)
(RATIONALIZATION...)...)} (1.0059812499999996)
(REVERSAL...)...)} (1.0159812499999996)
(RATIONALIZATION...)...)} (0.97)
(REVERSAL...)...)} (0.98)
(ENTERTAINMENT...)...)} (0.6)
(ROVING...)...)} (0.96)

#{0B.1889:
#{0B.1662:

(ACTIVE-GOAL
(ACTIVE-GOAL

obj
obj

(REVENGE...)...)} (0.99)
(RECOVERY...)...)} (0.99)

(CX)}

The negative emotion of anger resulting from the personal goal failure activates the following daydreaming goals: RATIONALIZATION (rationalize being

<!-- page: 340 -->
turned down), ROVING (shift attention from the rejection to more pleasant
thoughts), REVENGE (imagine getting even with the movie star), REVERSAL
(plan to prevent similar rejections in the future), and RECOVERY (imagine
future scenarios in which DAYDREAMER becomes romantically involved with
Harrison). Since the LOVERS goal is still not achieved and the associated need
states are not satisfied, a fresh LOVERS goal—not associated with Harrison
Ford—is again activated.
FEO A IR I A CR
Ptrans-plan fired as plan

for #{0B.1916:
in Cx.36

IF
THEN

(ACTIVE-GOAL

sprouting

obj

(PTRANS

actor

...)...)}

Cx.39

goal for person to PTRANS to a location
subgoal for person to KNOW that location

FAI
A a aK
Goal #{0B.2901: (ACTIVE-GOAL obj (KNOW actor ...)...)} succeeds
Subgoals of #{0B.1916: (ACTIVE-GOAL obj (PTRANS actor ...)...)}
Perform

external

Perform

action goal #{0B.1916:

SA
Goal

completed

action

a ACK CK
#{0B.1916: (ACTIVE-GOAL

(ACTIVE-GOAL
obj

(PTRANS

obj

(PTRANS

actor

...)...)

actor

...)...)}

succeeds

| I go home.

AE

Ea

At-plan2

IF
THEN

Aa I aK aK
fired

as

inference

in Cx.39

person PTRANS from one location
person AT new location and
no longer AT old location

to another

Retracting dependencies
Retract 0b.1943 in Cx.39:

(AT actor

Me obj Nuart-location)

Retract

in Cx.39:

0b.1940

(PTRANS actor Me from Home to ...)
Subgoals of #{0B.1897: (ACTIVE-GOAL obj (M-MOVIE actor ...)...)} completed
FEISS
IOI IO II II K
Goal #{0B.1897: (ACTIVE-GOAL obj (M-MOVIE actor ...)...)} succeeds
Assert

O0b.2943

in Cx.39:

(M-MOVIE actor Me)
FEI IO IG A IOI III
Entertainment-inf.

IF

M-MOVIE

THEN

ENTERTAINMENT

fired

as

inference

need

satisfied

in Cx.39

BEISICIO SOI I I IIR IK
Goal

#{0B.1887:

(ACTIVE-GOAL

obj

| I succeed

at being entertained.

Terminating

planning

for

top-level

Leaf context #{CX.39: (CX)}
[0B.1887: (SG. (ENTERTAINMENT

[0B.2942:

(ENTERTAINMENT...)...)}

succeeds

goal

strength

(SG. (M-MOVIE actor Me))]

(UPROC proc

’NEED-SATISFIED?)))]

<!-- page: 341 -->
## A.2 REVENGE1 DAYDREAM

[OB.1926:

(SG.

(AT actor

(0B.1939:

(SG.

(PTRANS

321

Nuart-theater

actor

obj Nuart-location))]

Me from Home

[0B.1935: (SG. (KNOW actor
(OB.2837: (SG. (MTRANS actor

to Nuart-location

obj Me))]

Me obj Nuart-location))]
Me from Nuart-theater to Me

obj (MOVIE)))]
[0B.2829: (SG. (AT actor Nuart-theater obj Nuart-location))]
[CB.2833: (SG. (AT actor Me obj Nuart-location))]
[0B.2907: (SG. (PTRANS actor Me from Nuart-location to Home obj Me))]
[OB.2903: (SG. (KNOW actor Me obj Home))]
Removing motivating
Emotional responses

| I feel

emotions

amused.

episode

Assess

scenario

(CX)}

in #{CX.39:

desirability

(ENTERTAINMENT...)...)

obj

(SUCCEEDED-GOAL

#{0B.1887:

Store

#{0B.1882:
(FAILED-GOAL obj (LOVERS actor ...)...)} (41.15)
#{0B.2403:
(FAILED-GOAL obj (BELIEVE actor ...)...)} (0.95)
#{0B.1887:
(SUCCEEDED-GOAL obj (ENTERTAINMENT...)...)} (0.6)
Scenario desirability = -1.4999999999999996

Activate
Activate
Activate
Activate
Activate
Activate

index #{ENTERTAINMENT-PLANi: (RULE subgoal (M-MOVIE actor ...)...)}
index #{0B.2563: (MOVIES obj Movie-star1)}
index #{NUART-THEATER: (THEATER name "the Nuart")}
index #{MOVIE-STAR1: (MALE-ACTOR first-name "Harrison"...)}
index #{HOME: (LOCATION name "home")}
index #{NUART-LOCATION: (LOCATION name "the Nuart")}

#{CX.39:

(CX)} --> #{CX.40:

(CX)}

Concerns:
#{0B.2872:
(ACTIVE-GOAL obj (LOVERS actor ...)...)} (0.9)
#{0B.2861:
(ACTIVE-GOAL obj (RATIONALIZATION...)...)} (1.0059812499999996)
#{0B.2851:
(ACTIVE-GOAL obj (REVERSAL...)...)} (1.0159812499999996)
#{0B.1420:
(ACTIVE-GOAL obj (RATIONALIZATION...)...)} (0.97)
#{0B.2412:
(ACTIVE-GOAL obj (REVERSAL...)...)} (0.98)
No more goals to run; switching to daydreaming mode

State

changes

from PERFORMANCE

to DAYDREAMING

DAYDREAMER successfully completes her plan for M-MOVIE. This replenishes the ENTERTAINMENT need state and thus the ENTERTAINMENT personal goal succeeds. A positive emotion is activated and the concern is terminated. The experience is stored in episodic memory, along with its evaluation
of -1.5. All in all, this was not a positive experience for DAYDREAMER.

## A.2 REVENGEI1 fired

THEN

obj

(REVENGE

as plan

for #{0B.1889: (ACTIVE-GOAL
in Cx.8 sprouting Cx.10
IF

(ACTIVE-GOAL

goal #{0B.1889:
(CX)}

Switching to new top-level
#{CX.8: (CX)} --> #{CX.10:
FAI
II IOI IC
Revenge-plani

Daydream

goal to gain REVENGE

obj

against

a failed

POS-RELATIONSHIP

subgoal

for person

to have

(REVENGE

obj

...)...)}

person

for

causing

self

goal
failure

of same

POS-RELATIONSHIP

obj ))}

<!-- page: 342 -->
#{CX.10:

(CX)} --> #{CX.11:

in Cx.10

sprouting

FROM DAYDREAMER

(CX)}

FESO
AI AK ACK
Failed-lovers-goal-plani fired as plan
for #{0B.1917: (ACTIVE-GOAL obj (BELIEVE

IF
THEN

TRACES

APPENDIX A. ANNOTATED

322

actor

...)...)}

Cx.11

goal for person to have FAILED-GOAL of LOVERS
subgoal for person to have ACTIVE-GOAL of LOVERS
with
then

person and
BELIEVE that

person

does

not

have

ACTIVE-GOAL

of LOVERS

#{CX.11:

(CX)} --> #{CX.12:

(CX)}

FEI
IICR I IK
Lovers-theme-plan

for #{0B.1924:
in Cx.11
IF
THEN

fired

as plan

(ACTIVE-GOAL

sprouting

obj (BELIEVE actor

goal for self ACTIVE-GOAL of LOVERS with
subgoals for ROMANTIC-INTEREST in person
not LOVERS with anyone

#{CX.12:

(CX)} --> #{CX.13:

FAI
IO IR AAC ACK
Romantic-interest-plan4

for #{0B.1938:

fired

(ACTIVE-GOAL

as plan

obj

sprouting

Cx.13

IF

goal

to have

POS-ATTITUDE

self

is STAR

subgoal

for

person

(BELIEVE

toward

to be greater

Belief-pers-attr2
in Cx.14

fired

obj

STAR

(BELIEVE

(CX)}

for #{0B.2007:

obj

IF
THEN

(ACTIVE-GOAL

sprouting

#{CX.16: (CX)} --> #{CX.17:
AES OA
I II IK

for #{0B.2012:
in Cx.16

fired

...)...)}

actor

...)...)}

(STAR actor

...)...)}

to be an ACTOR

(CX)}

as plan

(ACTIVE-GOAL

sprouting

actor

Cx.16

goal for self to be a STAR
subgoal for self to M-STUDY

M-study-plan

and

Cx.15

#{CX.15: (CX)} --> #{CX.16:
AEC SAS
IG I KK
Star-plan fired as plan
in Cx.15

...)...)}

as plan

(ACTIVE-GOAL

sprouting

actor

a person

#{CX.13: (CX)} --> #{CX.14: (CX)}
FE
IO IO IR aK
Goal #{0B.1950: (ACTIVE-GOAL obj (BELIEVE
#{CX.14: (CX)} --> #{CX.15: (CX)}
BES OSI
IOIKK
for #{0B.1959:

a person
and

(CX)}

in Cx.12

THEN

...)...)}

Cx.12

Cx.17

obj

(M-STUDY

actor

...)...)}

succeeds

<!-- page: 343 -->
Goal #{0B.2017: (ACTIVE-GOAL obj (RTRUE)
top-level-goal ...)} succeeds
Subgoals of #{0B.2012: (ACTIVE-GOAL obj (M-STUDY actor ...)...)} completed

IIIA
AIA AKI AA AAA ACK

Goal

#{0B.2012:

(ACTIVE-GOAL

obj

(M-STUDY

| I study to be an actor.
Subgoals of #{0B.2007: (ACTIVE-GOAL

obj

SA AAA AAA IAA IA AAA.

actor

...)...)}

(STAR actor

succeeds

...)...)}

Goal #{0B.2007: (ACTIVE-GOAL obj (STAR actor ...)...)}
| I am a movie star even more famous than he is.
Subgoals of #{0B.1959: (ACTIVE-GOAL obj (BELIEVE actor

completed

succeeds
...)...)}

completed

4A IAAI AIA HAA AAAI AK.
Goal
HAA

#{0B.1959:

KKK

(ACTIVE-GOAL

obj

(BELIEVE

actor

...)...)}

succeeds

HeHeHt i HeHee HeHeHeHe

Subgoals

of

#{0B.1969:

(ACTIVE-GOAL

FAA IA IAAI A AAA AHA
Goal #{0B.1969: (ACTIVE-GOAL

obj

obj

(BELIEVE

(BELIEVE

actor

actor

...)...)}

...)...)}

completed

succeeds

Personal goal outcome
Emotional responses

| I feel pleased.
| He is interested

#{CX.17:

in me.

(CX)} --> #{CX.18:

(CX)}

4A IA IASI AIA AA IA IAA
Not-lovers-plani
for

#{0B.1964:

in Cx.17

fired

sprouting

M-break-up-plan2
#{0B.2088:

in Cx.18

obj

(BELIEVE

actor

...)...)}

Cx.18

#{CK.18: (CX)} -->
4A AAI AAA AAA. AIA.
for

as plan

(ACTIVE-GOAL

#{CK.19:

fired

as plan

(ACTIVE-GOAL

sprouting

(CX)}

obj

(M-BREAK-UP...)...)}

Cx.19

HA AAAI IIIA ASA AIA
Goal #{0B.2093: (ACTIVE-GOAL obj (RTRUE)
top-level-goal ...)} succeeds
Subgoals of #{0B.2088: (ACTIVE-GOAL obj (M-BREAK-UP...)...)} completed
4A AH AAA AAA AH AAI
Goal #{0B.2088: (ACTIVE-GOAL obj (M-BREAK-UP...)...)} succeeds
| He breaks up with his girlfriend.
4H AA AAAI.
AI AAA AHe
Not-lovers-plani

fired

as

inference

in Cx.19

Subgoals of #{0B.1964: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} completed
HA AA IA AAAI AAAS AAI A A
Goal #{0B.1964: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds
Subgoals of #{0B.1982: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} completed
HAA A AA HAA AAAS AA IA A
Goal #{0B.1982: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds
| He wants to be going out with me.

#{CX.19:

(CX)} --> #{CX.20:

(CX)}

SH AIHA SAI SAA AA IIA A
Believe-plan2

for #{0B.1975:
in Cx.19

IF

fired

as plan

(ACTIVE-GOAL

sprouting

obj

(BELIEVE

actor

...)...)}

Cx.20

goal for person

to BELIEVE

self

NOT mental

state

<!-- page: 344 -->
THEN

subgoal

#{CX.20:

to MTRANS

NOT mental

(CX)} --> #{CX.21:

state

TRACES FROM DAYDREAMER

to person

(CX)}

FAO
I IR II
Mtrans-plan2 fired as plan

for #{0B.2142:
in Cx.20
IF
THEN

(ACTIVE-GOAL

sprouting

obj

(MTRANS actor

goal to MTRANS mental state to person
subgoal to be VPROX that person

#{CX.21:

(CX)} --> #{CX.22:

(CX)}

#{CX.21:
#{CX.21:

(CX)} --> #{CX.23:
(CX)} --> #{CX.24:

(CX)}
(CX)}

Candidates: #{0B.1889:
FE
ICRAOR CR IK
Vprox-plan2

fired

for #{0B.2147:
in Cx.21
IF
THEN

...)...)}

Cx.21

(ACTIVE-GOAL

obj

(REVENGE

obj

...)...)}

(1.2)

as plan

(ACTIVE-GOAL

sprouting

obj

(VPROX

actor

...)...)}

Cx.23

goal to be VPROX person
subgoal to M-PHONE person

#{CX.23:

(CX)} --> #{CX.25:

(CX)}

SE aK
M-phone-plani

fired

for #{0B.2160:
in Cx.23
IF
THEN

as plan

(ACTIVE-GOAL

sprouting

goal to M-PHONE
subgoal to KNOW

of #{0B.2160:

IF
THEN

fired

as

obj

actor

(KNOW actor

(ACTIVE-GOAL

FEO
OI IO III KK
Goal #{0B.2160: (ACTIVE-GOAL
| He calls me up.
FICS
IICR OK
Vprox-plan2

(M-PHONE

...)...)}

person
TELNO of person

FESO
IORI IC
Goal #{0B.2171: (ACTIVE-GOAL

Subgoals

obj

Cx.25

obj

inference

...)...)}

succeeds

obj

(M-PHONE

actor

...)...)}

(M-PHONE

actor

...)...)}

completed

succeeds

in Cx.25

M-PHONE person
VPROX person

Subgoals of #{0B.2147: (ACTIVE-GOAL obj (VPROX actor ...)...)} completed
FOCI
OI IO IOI AIK
Goal #{0B.2147: (ACTIVE-GOAL obj (VPROX actor ...)...)} succeeds
Subgoals of #{0B.2142: (ACTIVE-GOAL obj (MTRANS actor ...)...)} completed
AGI
FECA ISIC AIO
Goal #{0B.2142: (ACTIVE-GOAL obj (MTRANS actor ...)...)} succeeds
| I turn him down.
FESS IOI IOIIII OK
Believe-plan2
IF

MTRANS

fired

as

NOT mental

inference
state

in Cx.25

to person

<!-- page: 345 -->
## A.3 RATIONALIZATIONI THEN

person

BELIEVE

self

DAYDREAM

NOT mental

325

state

Subgoals

of #{0B.1975: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} completed
IO II IIR
FOI
Goal #{0B.1975: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds
Subgoals of #{0B.1989: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} completed
FICO IK
Goal #{0B.1989: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds
Subgoals of #{0B.1889: (ACTIVE-GOAL obj (REVENGE obj ...)...)} completed
FEC
CII K
Goal #{0B.1889: (ACTIVE-GOAL obj (REVENGE obj ...)...)} succeeds
| I get even with him.
Emotional

response.

| I feel

pleased.

Terminating planning for top-level
Removing motivating emotions

Store

episode

#{CX.7:

#{0B.1889:

goal

(SUCCEEDED-GOAL

(CX)} --> #{CX.26:

obj

(REVENGE...)...)

(CX)}

The table-turning strategy for REVENGE involves causing a failure for the
other person similar to the one which that person caused for DAYDREAMER.
When the failed goal is one to form a positive relationship with the other person,
REVENGE is achieved when the other person experiences the failure of a goal
to form a similar relationship with DAYDREAMER.
In order for the movie star to experience a failure of the goal to form a

LOVERS relationship with DAYDREAMER, he must (a) have such a goal in the
first place, and (b) be turned down by her. In order for a person to have a goal
to form a LOVERS relationship with a second person, the first person must
have ROMANTIC-INTEREST in the second person and not be in a LOVERS
relationship with someone else. This rule is applied via backward other planning.
ROMANTIC-INTEREST in turn requires a POS-ATTITUDE. A rule states that
a movie star has a POS-ATTITUDE toward a person if that person is an even
greater star. A low-plausibility plan to become a star is to study to be an
actor. Once the movie star is imagined to be interested in DAYDREAMER, she
imagines turning him down. This results in a positive emotion. The daydreamed
episode is then stored in episodic memory for future use (in REVENGE2 and

REVENGE3).

## A.3 Switching
#{CX.6:
#{CX.6:

RATIONALIZATION1
to new top-level

goal #{0B.1420:

Daydream

(ACTIVE-GOAL

(CX)}
(CX)}

(CX)} --> #{CX.7:
(CX)} --> #{CX.8:

FIO
ICO I ICC
Rationalization-plani

fired

as

for #{0B.1420: (ACTIVE-GOAL
in Cx.6 sprouting Cx.7

obj

IF

goal

to RATIONALIZE

plan

failure

(RATIONALIZATION...)...)}

obj

(RATIONALIZATION)}

<!-- page: 346 -->
THEN

subgoal

#{CX.7:
FEO

for

imagined

success

(CX)} --> #{CX.9:

to LEADTO

TRACES FROM DAYDREAMER

failure

(CX)}

IOI IOI ICR I IC

Leadto-plani

coded

plan

for #{0B.1441: (ACTIVE-GOAL
in Cx.7 sprouting Cx.9

obj

(LEADTO

goal for success to LEADTO
hypothesize success and
explore consequences

failure

IF
THEN

| What

fired

if I were

as

ante

...)...)}

going out with him?

One way to rationalize the failure of a goal is to imagine that had the goal
succeeded, it would have resulted in a equal or worse goal failure. One way of
generating such a LEADTO scenario is to assert the succeeded goal in an alternative past context, and then apply inferences in that context in order to evaluate
the consequences of that success. If negative consequences (goal failures) are
found, positive emotions will be generated since this is an alternative past. Positive emotions will offset the original negative emotion resulting from failure
of the LOVERS goal. (Of course, if positive consequences are found, negative
emotions

will instead result.

In the current version of DAYDREAMER,

there

is no means of directing the outcome one way or another when this particular
plan for LEADTO is employed.)
FOR IARI

I I IOK

Other-rulei

IF
THEN

fired

as

inference

LOVERS with a person
initiate forward other

in

Cx.9

planning

for

person

FESO IOI III
ICI IK
Acting-job-theme

fired

IF

actor

not

THEN

activate

JO

does

a a a aka kk a

Activate

goal

as

have

inference
an

to have

in Cx.9

ACTING-EMPLOY

an ACTING-EMPLOY

KK kk ok

top-level

goal #{0B.1464:

for #{MOVIE-STAR1:
(MALE-ACTOR
| He would need work.

(ACTIVE-GOAL

first-name

obj

(ACTING-EMPLOY...)...)}

"Harrison"...)

The first consequence of the hypothesized goal success is that forward other
planning is initiated for the movie star. This is performed whenever DAYDREAMER is in a LOVERS relationship with another person in order to monitor the mental state of the other person. Once forward other planning begins,
the following further consequence is generated: the movie star will have the goal
to act in a movie.
#{CX.9:

(CX)}

-->

#{CX.10:

(CX)}

<!-- page: 347 -->
## A.3 RATIONALIZATIONI Episodic

reminding

of #{EPISODE.1:

time

| I remember

the

FRI ISIOOI

IORIOK
fired

Episodic-rule.1

DAYDREAM

as

analogical

rule Episodic-rule.i...)}

Paramount

Pictures

in Cairo.

plan

(ACTING-EMPLOY...)...)}

for #{0B.1464: (ACTIVE-GOAL obj
in Cx.9 sprouting Cx.10
| He would have to be in Cairo.

#{CX.10:

(EPISODE

a job with

he had

327

(CX)}

(CX)} --> #{CX.11:

FACI IK
Rprox-plan

fired

for #{0B.1486:

sprouting

in Cx.10

IF
THEN

as plan

#{CX.11: (CX)} --> #{CX.12:
FAIS IOC
I KK
fired

actor

...)...)}

Cx.11

goal to be RPROX a city
subgoal to PTRANS1 to that

Ptransi-plan

(RPROX

obj

(ACTIVE-GOAL

city

(CX)}

as plan

for #{0B.1504: (ACTIVE-GOAL obj (PTRANS1 actor ...)...)}
in Cx.11 sprouting Cx.12
ACR AIC ICR
FRI
top-level-goal ...)} succeeds
Goal #{0B.1525: (ACTIVE-GOAL obj (RTRUE)
Subgoals of #{0B.1504: (ACTIVE-GOAL obj (PTRANS1 actor ...)...)} completed
RACK
FOR
Goal #{0B.1504: (ACTIVE-GOAL obj (PTRANS1 actor ...)...)} succeeds

| He would go to Cairo.
FA

IC

Rprox-plan
IF
THEN

I
fired

I KK
as

inference

in Cx.12

PTRANS1 from one city to another
RPROX new city and
no longer RPROX old city

FESO
I IORI IIR
Rprox-plan
IF
THEN

fired

as

inference

in Cx.12

PTRANS1 from one city to another
RPROX new city and
no longer RPROX old city

Forward other planning continues for the movie star: DAYDREAMER is reminded of a previous secondhand episode involving Harrison Ford—when he
had to go on location in Egypt to shoot a film. Through analogical planning, this episode is applied to the star’s current goal: he must be RPROX

(located in) Cairo to shoot another film. This in turn results in Harrison Ford’s

PTRANS1ing to Cairo.
FI
CIC IOC I IC
Lovers-p-goal fired
IF

LOVERS with
self is not

as

inference

person who
RPROX that

in Cx.12

is RPROX
city

one

city

and

<!-- page: 348 -->
THEN

activate

preservation

j Our relationship

would

goal

TRACES FROM DAYDREAMER

on LOVERS

be in trouble.

FEC
ICI II A

Goal #{0B.1486: (ACTIVE-GOAL obj (RPROX actor ...)...)} succeeds
Subgoals of #{0B.1637: (SUCCEEDED-GOAL obj (RPROX...)...)} completed
Subgoals of #{0B.1464: (ACTIVE-GOAL obj (ACTING-EMPLOY...)...)} completed
FEO
II III
Goal #{0B.1464: (ACTIVE-GOAL obj (ACTING-EMPLOY...)...)} succeeds

#{CX.12:
#{CX.13:

(CX)} --> #{CX.13:
(CX)} --> #{CX.14:

(CX)}
(CX)}

SOO
I I II KK
Rprox-plan

fired

for #{0B.1688:
in Cx.13
IF
THEN

as plan

(ACTIVE-GOAL

sprouting

obj (RPROX actor

goal to be RPROX a city
subgoal to PTRANS1 to that

#{CX.14:
FEO

(CX)} --> #{CX.15:
IKK

Ptransi-plan

for #{0B.1694:
in Cx.14

fired

city

(CX)}

as plan

(ACTIVE-GOAL

sprouting

...)...)}

Cx.14

obj

(PTRANS1

actor

...)...)}

Cx.15

BUA OII RK
Goal #{0B.1699: (ACTIVE-GOAL obj (RTRUE)
top-level-goal ...)} succeeds
Subgoals of #{0B.1694: (ACTIVE-GOAL obj (PTRANS1 actor ...)...)} completed
FEO
RICK
Goal #{0B.1694: (ACTIVE-GOAL obj (PTRANS1 actor ...)...)} succeeds
| I would go to Cairo.
FEISS
IIR IIHR
Rprox-plan

IF
THEN

fired

as

inference

in Cx.15

PTRANS1 from one city to another
RPROX new city and
no longer RPROX old city

The fact that the star is in Cairo and DAYDREAMER is in Los Angeles
results in the activation of a preservation goal on the LOVERS relationship.
Planning is then performed to undo the antecedents for this preservation goal—
in particular, the fact that DAYDREAMER is not RPROX the same city as the
star results in DAYDREAMER PTRANSIing to Cairo. (The other alternative
would is for the star to PTRANS1 back to Los Angeles—or not to go to Cairo

in the first place.)
FEO
IO IO II IOC
Job-failure fired as
IF

THEN

inference

in Cx.15

have EMPLOYMENT with organization
one city and
self is not RPROX that city
goal to have EMPLOYMENT fails

which

is RPROX

<!-- page: 349 -->
| I would

329

lose my job at May Company.

Personal goal outcome
Emotional responses

| I feel relieved.
FOO ICICI I ICICI IK

Goal

#{0B.1688:

(ACTIVE-GOAL

obj

(RPROX

actor

...)...)}

succeeds

However, since DAYDREAMER is no longer in Los Angeles, she loses her
job. Since this moderately important goal failure occurs in an alternative past,
it results in the positive emotion of relief. This positive emotion is diverted into
the original negative emotion, which loses some of its strength as a result. However, the strength of the negative emotion is still not below a certain threshold.
Therefore, another rationalization is sought.

## A.4 RATIONALIZATION2 Attempting

to backtrack

for

top-level

#{0B.1420:

(ACTIVE-GOAL

obj

(RATIONALIZATION...)...)

Backtracking

to next

context

obj

Rationalization-plan2

as plan

for #{0B.1420:
in Cx.6
IF
THEN

fired

(ACTIVE-GOAL

sprouting

goal

of #{CX.8:

for #{0B.i420: (ACTIVE-GOAL
FEC
ICI

obj

(CX)}

(RATIONALIZATION...)...)}

(RATIONALIZATION...)...)}

Cx.8

goal to RATIONALIZE
subgoal for failure

failure
to LEADTO

success

#{CX.8: (CX)} --> #{CX.16: (CX)}
Episodic reminding of #{EPISODE.5:

(EPISODE

rule

| I remember
| Irving led

the time my being turned down by
to a success by being turned down

| him

to succeeding

leading

Daydream

at going

out

with

Episodic-rule.4...)}

by
Chris.

Another method for rationalization is to imagine future scenarios in which
the goal failure leads to an equal or more important goal success. An episode for
achieving this goal is recalled: the time DAYDREAMER went to a bar after being
turned down by Irving and ended up meeting Chris. Thus through analogical
planning, DAYDREAMER imagines going to a bar and meeting someone:
FOCI
ICAI IK
Episodic-rule.4

fired

for #{0B.1448:

(ACTIVE-GOAL

in Cx.8

#{CX.16:

sprouting

as

analogical

obj

plan

(LEADTO

ante

...)...)}

Cx.16

(CX)} --> #{CX.17:

(CX)}

Apply existing analogical plan
FOIA IA
AI AAC
Episodic-rule.3 fired as analogical plan
for #{0B.1769: (ACTIVE-GOAL obj (LEADTO ante
in Cx.16 sprouting Cx.17

...)...)}

<!-- page: 350 -->
#{CX.17:
Apply

(CX)} --> #{CX.18:

existing

analogical

TRACES FROM DAYDREAMER

(CX)}
plan

FEI
IO IC II
Episodic-rule.2

fired

for #{0B.1776:

(ACTIVE-GOAL

as

analogical

in Cx.17 sprouting Cx.18
Apply existing analogical

obj

plan

(LOVERS actor

...)...)}

plan

FES
IS II KK
At-plan2

fired

as analogical

plan

for #{0B.1781:

(ACTIVE-GOAL

obj

in Cx.18
IF
THEN

sprouting

goal to be AT a location
subgoal to PTRANS to that

#{CX.19:

(CX)} --> #{CX.20:

FEO
I IC IKK
Ptrans-plan fired as plan
for #{0B.1789: (ACTIVE-GOAL
in Cx.19

IF
THEN

sprouting

(AT actor

...)

top-level-goal

...)}

Cx.19

location

(CX)}
obj

(PTRANS

actor

...)...)}

Cx.20

goal for person to PTRANS to a location
subgoal for person to KNOW that location

FEO

IOI

IK

Goal #{0B.1794: (ACTIVE-GOAL obj (KNOW actor ...)...)} succeeds
Subgoals of #{0B.1789: (ACTIVE-GOAL obj (PTRANS actor ...)...)}

FEO
II AIK
Goal #{0B.1789: (ACTIVE-GOAL
| I go to Mom’s.

obj

(PTRANS

actor

...)...)}

completed

succeeds

Personal goal outcome
Emotional responses

| I feel pleased.
| I am going out with him.

Generating an imaginary future scenario in which the DAYDREAMER meets
someone (Chris or someone similar) at a bar results in the imagined future
success of the LOVERS goal and corresponding positive emotion.
emotion is diverted into the original negative emotion.

## A.56 RATIONALIZATION3 Daydream

FEO
IOI III IK
Rationalization-plan3

for #{0B.1420:
in Cx.19
IF
THEN
2

2

fired

(ACTIVE-GOAL

sprouting

Cx.110

goal to RATIONALIZE
subgoal to MINIMIZE
ke ka

ake2 a a ieaiea oeae2k

as plan

obj

failure
failure

(RATIONALIZATION...)...)}

This positive

<!-- page: 351 -->
## A.5 RATIONALIZATION3 DAYDREAM Minimization-plan

as

coded

plan

for #{0B.5356: (ACTIVE-GOAL
in Cx.110 sprouting Cx.111

obj

(MINIMIZATION...)...)}

IF

goal for MINIMIZATION

of failure

THEN

negate

| Anyway,

and

I was

fired

331

justify
well

antecedents

dressed

of failure

because

I was

wearing

a necklace.

| I feel less embarrassed.
| Anyway,

I do not

think

much

of Harrison

Ford.

| I feel less embarrassed.
FEAR
IC IO IK
Rationalization-inf1

fired

as

inference

in Cx.ili

IF

NEG-EMOTION associated with failure less than a
certain strength or POS-EMOTION associated with
failure

THEN

RATIONALIZATION

of failure

| I rationalize being turned
FEI
CICK
Goal #{0B.1420: (ACTIVE-GOAL
Terminating

planning

for

down

by him.

obj

(RATIONALIZATION...)...)}

top-level

#{0B.1420: (SUCCEEDED-GOAL obj
Leaf context #{CX.21: (CX)}

succeeds

goal

(RATIONALIZATION...)...)}

(OB. 1420:
(SG.

(RATIONALIZATION

obj (FAILED-GOAL

obj

(LOVERS actor Me Movie-star1))))]

(0OB.1855: (SG. (LEADTO ante (FAILED-GOAL obj (LOVERS actor Me Movie-star1))
conseq (SUCCEEDED-GOAL)))]
([0B.1845: (SG. (LEADTO ante (FAILED-GOAL obj (LOVERS actor Me Movie-star1))
conseq (SUCCEEDED-GOAL obj (LOVERS actor Me Chris))))]
(OB.1820: (SG. (LOVERS actor Me Chris))]
[0B.1810: (SG. (AT actor Me obj Bari-loc))]
[0B.1800: (SG. (PTRANS actor Me from Home to Bari-loc obj Me))]
[0B.1796: (SG. (KNOW actor Me obj Bari-loc))]
([0B.1817: (SG. (AT actor Chris obj Bari-loc))]
Leaf context #{CX.15: (CX)}
(OB.1420:
(SG. (RATIONALIZATION obj (FAILED-GOAL obj (LOVERS actor Me Movie-star1))))]
([0B.1441: (AG. (LEADTO ante (SUCCEEDED-GOAL obj (LOVERS actor Me Movie-star1))
conseq (FAILED-GOAL)))]

Leaf

[0B.1452:

(SG. (RTRUE))]

context

#{CX.111:

(0B.1420:

(SG.

[0B.5356:

(AG.

(CX)}

(RATIONALIZATION

obj

(FAILED-GOAL

top-level-goal

...)))]

(MINIMIZATION

obj

obj

(FAILED-GOAL

obj
obj

(BELIEVE actor Movie-star1
(POS-ATTITUDE obj Me))
(BELIEVE

obj (POS-ATTITUDE
top-level-goal ...))]

[0B.5360:

actor

Movie-star1

obj Me))

(SG. (RTRUE))]

Removing motivating emotions of
#{0B.1420: (SUCCEEDED-GOAL obj (RATIONALIZATION...)...)
Store episode #{0B.1420: (SUCCEEDED-GOAL obj (RATIONALIZATION...)...)

<!-- page: 352 -->
DAYDREAMER attempts to rationalize her early embarrassment in front of
the movie star. The minimization strategy for rationalization involves finding
antecedents of the failure; if an antecedent is an attitude, the opposite attitude
is asserted, otherwise reasoning chains for the negation of the antecedent are
highlighted. In this case, an antecedent for embarrassment in front of someone
is that one has a positive attitude toward that person. This attitude is negated,
resulting in a reduction of the strength of the resulting embarrassment emotion.
Another antecedent is that DAYDREAMER was not well dressed. However, a
low realism inference was already made that DAYDREAMER was well dressed
because she was wearing a nice necklace. This antecedent is thus negated and
the strength of the resulting embarrassment further reduced.
Now the strength of the negative emotion is below the necessary threshold
and the RATIONALIZATION daydreaming goal succeeds. The various planning
episodes are stored in episodic memory for possible future use.

## A.6 ROVING1 Switching

to new top-level

#{CX.9:

(CX)}

-->

FOI

IO

IK

Roving-plani

fired

for #{0B.1901:
in Cx.9
IF
THEN

Daydream
goal #{0B.1901:

#{CX.27:

(CX)}

as

coded

plan

(ACTIVE-GOAL

obj

sprouting

(ROVING)

(ACTIVE-GOAL

obj

top-level-goal

(ROVING)

...)}

...)}

Cx.27

daydreaming goal for ROVING
recall pleasant episode

Episodic

reminding

| I remember

of #{EPISODE.1:

the time

Steve

told

(EPISODE
me

rule

he thought

Induced-rule.1i...)}

I was

wonderful

| at Gulliver’s.
Activate index #{INDUCED-RULE.1: (RULE subgoal (RSEQ obj (MTRANS...)
Activate index #{GULLIVERS: (RESTAURANT name "Gulliver’s")}
Activate index #{MARINA: (CITY name "the Marina")}
Activate index #{STEVE1: (MALE-PERSON first-name "Steve"...)}
| I feel pleased.

...)...)}

The plan for the ROVING daydreaming goal is to retrieve a random episode
associated with a strong positive emotion. Here a pleasant episode at Gulliver’s
restaurant, located in Marina del Rey, is recalled, and the associated positive
emotion is reactivated.
Episodic

reminding

! I remember

the

of #{EPISODE.2:

time

(EPISODE

rule

Induced-rule.2...)}

I had a job in the Marina.

Activate

index

#{INDUCED-RULE.2:

Activate

index

#{0B.1829:

(RULE subgoal

(RPROX

actor

...)

goal

...)}

(EMPLOYMENT)}

Index #{MARINA: (CITY name "the Marina")} already active
Activate index #{VENICE: (CITY name "Venice Beach")}
Index #{INDUCED-RULE.1: (RULE subgoal (RSEQ obj (MTRANS...) ...)...)}
Episodic reminding of #{EPISODE.3: (EPISODE rule Induced-rule.3...)}

fades

<!-- page: 353 -->
## A.7 RECOVERY2 DAYDREAM | I remember

the

time

Steve

and

333

I bought

sunglasses

in

| Venice Beach.
Activate index #{INDUCED-RULE.3: (RULE subgoal (RPROX actor ...)
goal ...)}
Index #{VENICE: (CITY name "Venice Beach")} already active
Subgoals of #{0B.1901: (ACTIVE-GOAL obj (ROVING)
top-level-goal ...)} completed
FIO
A IOI
Goal #{0B.1901: (ACTIVE-GOAL obj (ROVING)
top-level-goal ...)} succeeds
Terminating planning for top-level goal
#{0B.1901:
(SUCCEEDED-GOAL obj (ROVING...)...)}
Removing motivating emotions
#{CX.26: (CX)} --> #{CX.28:
(CX)}

Whenever an episode is retrieved via some of its indices, its remaining indices
are activated. (The number of indices required for the retrieval of an episode
depends on the episode. Some may be retrieved using only one index; others
require several indices to be active before they can be retrieved.) The Gulliver’s
episode is indexed under Marina del Rey and so this index is activated. This
index is sufficient to result in the recall of a job DAYDREAMER once had in
Marina del Rey. Venice, which is near Marina del Rey, is another index for
this second episode and so it is activated. The active indices of Venice and
Peter together result in the retrieval of an episode involving both. (Retrieval
of episodes involving more than one index is accomplished through a one-level
intersection search.)

## A.7 RECOVERY2 Switching to new top-level
FFA ITA AAA TOA AAA A
Recovery-plan

fired

for #{0B.1662:
in Cx.4

IF
THEN

as

goal #{0B.1662:

(ACTIVE-GOAL

obj

(RECOVERY...)...)}

plan

(ACTIVE-GOAL

sprouting

Daydream

obj

(RECOVERY...)...)}

Cx.5

goal for RECOVERY from a LOVERS failure
subgoal to ask out person again

| I have

to

ask

him

out.

In general, to achieve RECOVERY of a failed LOVERS goal, one must imagine ways of achieving that same LOVERS goal in the future. After LOVERS1,
DAYDREAMER considers possible ways to go out with the movie star in the
future. For simplicity, instead of generating an entire future plan for achieving
the LOVERS goal (which is demonstrated by other traces), only the problem of
contacting the movie star to ask him out is considered here.
FOIA IATA AOIATA HHA A A
Mtrans-plan2

fired

as plan

for #{0B.1711: (ACTIVE-GOAL
obj (MTRANS
in Cx.5 sprouting Cx.6

actor

...)...)}

<!-- page: 354 -->
IF
THEN

TRACES FROM DAYDREAMER

APPENDIX A. ANNOTATED

334

goal to MTRANS mental state to person
subgoal to be VPROX that person

FAI
I IC I
Vprox-plan2

fired

for #{0B.1716:
in Cx.6

IF
THEN

as plan

(ACTIVE-GOAL

sprouting

obj

(VPROX actor

goal to be VPROX person
subgoal to M-PHONE person

| I have to call him.
FEIO II ACK
M-phone-plani fired as plan
for #{0B.1729: (ACTIVE-GOAL obj
in Cx.8 sprouting Cx.9
IF
THEN

...)...)}

Cx.8

goal to M-PHONE
subgoal to KNOW

| I have

to know

his

(M-PHONE

actor

...)...)}

person
TELNO of person
telephone

number.

FOI
IR IAC
Know-plan2 fired as plan

for #{0B.1735:
in Cx.9
IF
THEN

(ACTIVE-GOAL

sprouting

obj

(KNOW actor

goal for person to KNOW TELNO
subgoal for someone to MTRANS

| He has

to tell

...)...)}

Cx.10

me his

telephone

of another
TELNO from

to that

person

number.

FEO I IIIIAK
Mtrans-plani

fired

as

plan

for #{0B.1741: (ACTIVE-GOAL
in Cx.10 sprouting Cx.11
IF
THEN

obj

(MTRANS

actor

...)...)}

goal for person2 to MTRANS info from object to personi
subgoals for object to KNOW info and
for personi to be VPROX person2 and
for person2 to be VPROX object and
for person2 to BELIEVE that personl has ACTIVE-GOAL
to KNOW info and
for person2 to have ACTIVE-GOAL for personi to KNOW
info

Attempting

to backtrack

for

top-level

goal

#{0B.1662: (ACTIVE-GOAL obj (RECOVERY...)...)
Top-level goal #{0B.1662: (ACTIVE-GOAL obj (RECOVERY...)...)}

All possibilities

failed for #{0B.1662:

(ACTIVE-GOAL

obj

fails

(RECOVERY...)...)

DAYDREAMER fails to generate a scenario of high realism in which she is
able to get in touch with the movie star. Therefore, DAYDREAMER attempts
to generate mutations of failed action goals which might enable her to get in
touch with the star:

<!-- page: 355 -->
## A.7 RECOVERY2 DAYDREAM
Action mutations for #{0B.1662:
Mutating action goal #{0B.1741:

(ACTIVE-GOAL
(ACTIVE-GOAL
telephone

his

else

someone

he tells

| Suppose

335

obj
obj

(RECOVERY...)...)}
(MTRANS actor ...)...)}

number.

Serendipity!! (daydreaming goal)
[0B.1989: (AG. (KNOW actor Me
obj (TELNO actor Movie-star1))) EPISODE.7]
[OB.2041: (AG. (MTRANS actor ?Person6 from ?Person6

to Me

obj (TELNO actor Movie-star1))) EPISODE.8]
[OB.2052: (AG. (KNOW actor ?Person6
obj (TELNO actor Movie-stari))) EPISODE.9]
[OB.2058: (AG. (MTRANS actor Movie-star1 from Movie-stari
obj (TELNO actor Movie-star1)))]
[0B.2035: (AG. (BELIEVE actor ?Person6
obj (ACTIVE-GOAL obj (KNOW actor Me
obj (TELNO actor Movie-star1)))))]
[0B.2028: (AG. (BELIEVE actor ?Person6
obj (BELIEVE actor Me
obj (ACTIVE-GOAL obj (KNOW actor Me

to ?Person6

obj (TELNO actor Movie-stari))))))]
Me ?Person6))]
?Person6 ?Person6)) ]

[0B.2025: (AG. (VPROX actor
[0B.2022: (AG. (VPROX actor
| What do you know!
existing

Apply

plan

analogical

FEC OCI IKK
Know-plan3
in Cx.26
IF
THEN

as

fired

for #{0B.2063:

analogical

(ACTIVE-GOAL

sprouting

plan

obj

(KNOW actor

goai for person to KNOW TELNO
subgoal for someone to MTRANS

| This

person

has

...)...)}

Cx.27

to tell

me

his

of another
TELNO from

telephone

to that

person

number.

The action “he tells me his telephone number” is mutated into “he tells
someone else his telephone number.” The serendipity mechanism detects that
this mutated action is applicable to the goal of finding out the star’s telephone
number: If the star tells someone else his number, this other person will know his
number, who can then tell it to DAYDREAMER. Next, the planning mechanism
fleshes out this possibility. In particular, a value for the variable which represents
the other person must be found. Initially, DAYDREAMER proceeds by analogy
to the episode produced as a suggestion by the serendipity mechanism:
Apply

existing

FE

Mtrans-plani

for #{0B.2090:
in Cx.27

IF
THEN

analogical

plan

IORI II CK
fired

as

analogical

(ACTIVE-GOAL

sprouting

obj

plan

(MTRANS

actor

...)...)}

Cx.28

goal for person2 to MTRANS info from object to person1
subgoals for object to KNOW info and
for personi to be VPROX person2 and
for person2 to be VPROX object and
for person2 to BELIEVE that personi has ACTIVE-GOAL

<!-- page: 356 -->
to KNOW info and
for person2 to have
info

ACTIVE-GOAL

existing

plan

Apply

analogical

for

personi

to KNOW

FEAR
I Ia A aKK
Know-plan2

fired

for #{0B.2095:
in Cx.28
IF
THEN

as analogical

(ACTIVE-GOAL

sprouting

goal for person to KNOW TELNO
subgoal for someone to MTRANS

| He has

to tell

bd (#{0B.1403:
ICRI KR

Mtrans-plani
in Cx.29

this

fired

for #{0B.2118:

...)...)}

his

of another
TELNO from

telephone

(KNOW actor

to that

person

number.

Movie-star1

obj

...)}

as plan

sprouting

obj

(MTRANS

actor

...)...)}

Cx.30

goal for person2 to MTRANS info from object to personi
subgoals for object to KNOW info and
for personi to be VPROX person2 and
for person2 to be VPROX object and
for person2 to BELIEVE that personi has ACTIVE-GOAL
to KNOW info and
for person2 to have ACTIVE-GOAL for personi to KNOW
info

IORI

Jiffy-rulei

fired

for #{0B.2187:
in Cx.30

R
as plan

(ACTIVE-GOAL

sprouting

another

subgoal

person

| He has

to want

(BELIEVE

actor

...)...)}

to have

ACTIVE-GOAL

to KNOW

TELN

person

for person

with

obj

Cx.31

goal for person
of

THEN

person

(ACTIVE-GOAL

FAI

IF

(KNOW actor

Cx.29

Non-empty
FEO

IF
THEN

plan

obj

to have

to be going

out

ACTIVE-GOAL

of LOVERS

with

person.

this

FEISS
IO IIA II AK
Lovers-theme-plan

for #{0B.2298:
in Cx.31

IF
THEN

fired

as plan

(ACTIVE-GOAL

sprouting

obj (BELIEVE actor

goal for self ACTIVE-GOAL of LOVERS with
subgoals for ROMANTIC-INTEREST in person
not LOVERS with anyone

| He has
| He has

...)...)}

Cx.32

to be interested in this person.
to believe that he is not going out

a person
and

with

anyone.

FOSS
IORI CK
Romantic-interest-plan

fired

for #{0B.2397: (ACTIVE-GOAL
in Cx.32 sprouting Cx.33

as plan

obj

(BELIEVE

actor

...)...)}

(PERSON6...))

<!-- page: 357 -->
## A.7 RECOVERY2 DAYDREAM IF
THEN

goal to have ROMANTIC-INTEREST in person
subgoal to have POS-ATTITUDE toward person
person to be ATTRACTIVE

| He has

to believe

that

this

person

337

and

is attractive.

In order for the star to tell someone else his number, he must have the goal for
the other person to know his number. The following rule is employed here: One
has the goal to tell someone else one’s number if one has the goal to go out with
that person. Other rules (such as having the goal to give someone one’s number
when one wants a job from that person) would lead to other daydreams. In
order for the star to have the goal to go out with this unknown person, he must
be interested in her. Thus he must have a positive attitude toward this person
and she must be attractive. A suitable value for the variable representing this
unknown person is therefore DAYDREAMER’s rich and attractive friend Karen:
Porte
ort tft ee te
Belief-pers-attr3

fired

as plan

for #{0B.2513: (ACTIVE-GOAL
in Cx.33 sprouting Cx.34

Fact plan #{0B.1606:

that

(BELIEVE

(ATTRACTIVE

SE
CK
Subgoals of #{0B.2640:
FEO
II ICR
| He believes

obj

obj Karen)}

(ACTIVE-GOAL

Karen

of #{0B.2742: (ACTIVE-GOAL
FEC
IO AIO
| He is interested in her.
FACIR II
#{0B.2645:

in Cx.37

(ACTIVE-GOAL

sprouting

obj

...)...)}

found

(BELIEVE

actor

...)...)}

completed

(BELIEVE

actor

...)...)}

completed

actor

...)...)}

is attractive.

Subgoals

for

actor

obj

obj

(BELIEVE

Cx.38

FESO
IO II I AK
M-break-up-plan2
for

#{0B.2842:

in Cx.38

fired

as plan

(ACTIVE-GOAL

sprouting

obj

(M-BREAK-UP...)...)}

Cx.39

SEI
III I I KK
Subgoals of #{0B.2842: (ACTIVE-GOAL obj (M-BREAK-UP...)...)} completed
FEI
I A IIR
| He breaks up with his girlfriend.
FESO
AOR AIK
Subgoals of #{0B.2869: (SUCCEEDED-GOAL obj (BELIEVE...)...)} completed
Subgoals of #{0B.2650: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} completed
Etetr
oreo rore Let
| He wants to be going out with her.
FESO IIA III IK
Jiffy-rule1

IF
THEN

person
person

fired

as

inference

in Cx.39

to have ACTIVE-GOAL of LOVERS with person
have ACTIVE-GOAL to KNOW TELNO of person

FORO
IO AC
I KO aK

<!-- page: 358 -->
Activate

| He wants

Subgoals
FES

her

(MALE-ACTOR

to know

her his

first-name

telephone

his

of #{0B.2674:
ICICI CK

| He tells

(ACTIVE-GOAL

goal #{0B.2884:

top-level

for #{MOVIE-STAR1:

(ACTIVE-GOAL

obj

(KNOW actor

...)...)}

"Harrison"...)

number.

obj

telephone

number.

inference

in Cx.43

(MTRANS

actor

...)...)}

completed

FAI
IIII IC
Know-plan3

fired

as

FAIR

ACK

Believe-plani

fired

IF
THEN

MTRANS
person

as

inference

mental state
BELIEVE self

A

to person
mental state

IO IK

Enable-future-vprox-plani
IF
THEN

in Cx.43

personi
personi
person2

fired

as

inference

in Cx.43

KNOW TELNO of person2
and
ENABLE-FUTURE-VPROX

Karen asks the movie star for his telephone number, and he gives it to her.
Now DAYDREAMER must get the telephone number from Karen:
FAO
IO IC IK
Subgoals of #{0B.3005: (SUCCEEDED-GOAL obj (KNOW actor ...)...)} completed
Subgoal relaxation, #{0B.2716: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds
FEO
I ROR ok
Believe-plani

fired

for #{0B.2722:
in Cx.44

IF
THEN

as plan

(ACTIVE-GOAL

sprouting

obj

(BELIEVE

actor

...)...)}

Cx.45

goal for person to BELIEVE self mental state
subgoal to MTRANS mental state to person

| I have

to tell

her

that

I want

to know

his

telephone

obj

(MTRANS

actor

...)...)}

JE
I II IKK
Mtrans-plan2

fired

for #{0B.3022:
in Cx.45
IF
THEN

sprouting

Cx.46

goal to MTRANS mental state to person
subgoal to be VPROX that person

FEISS

III II OK

Vprox-plan2

fired

for #{0B.3027:
in Cx.46
IF
THEN

as plan

(ACTIVE-GOAL

as plan

(ACTIVE-GOAL

sprouting

obj (VPROX actor

Cx.48

goal to be VPROX person
subgoal to M-PHONE person

RIOR I ROR I keaka aieakeakaeaieae2k

...)...)}

number.

<!-- page: 359 -->
## A.7 RECOVERY2 DAYDREAM M-phone-plani

fired

as plan

for #{0B.3040: (ACTIVE-GOAL
in Cx.48 sprouting Cx.49
IF

goal

THEN

subgoal

to M-PHONE

FESO
IO IC IC IK
Subgoals of #{0B.3040:
BESS III
IK
| I call her.
FEISS
III IK

IF
THEN

fired

obj

(M-PHONE

actor

...)...)}

(M-PHONE

actor

person

to KNOW TELNO

Vprox-plan2

339

as

of person

(ACTIVE-GOAL

inference

obj

...)...)}

completed

in Cx.49

M-PHONE person
VPROX person

FEI
IC
IK
Subgoals of #{0B.3072:
FCI
II IOK
Subgoals of #{0B.3022:
SEG
RIK
| I tell

her

that

(SUCCEEDED-GOAL

obj

(ACTIVE-GOAL

(MTRANS

actor

telephone

number.

I want

to know

obj

his

(VPROX...)...)}

completed

...)...)}

completed

FEI
I I
Believe-plani
IF
THEN

MTRANS
person

fired

as

inference

mental state
BELIEVE self

in Cx.49

to person
mental state

FEIG ICI I IKak
Subgoals of #{0B.3104:
FAO
II
ACR

(SUCCEEDED-GOAL

Vprox-reflexivity

as plan

fired

obj

(BELIEVE...)...)}

completed

for #{0B.2732: (ACTIVE-GOAL obj (VPROX actor ...)...)}
in Cx.49 sprouting Cx.50
FEI
IO II
Subgoals of #{0B.2732: (ACTIVE-GOAL obj (VPROX actor ...)...)}
FEC
IRI IIR

Subgoals

of #{0B.2702:

FE

IO

| She

(ACTIVE-GOAL

tells

me

his

telephone

FAI
IOC II
Know-plan3 fired as inference
FEI
IO II IIIIR
Believe-plani

IF
THEN

MTRANS
person

obj

(MTRANS

actor

IO IIR

fired

as

number.

in Cx.50

inference

mental state
BELIEVE self

in Cx.50

to person
mental state

FEC
II IIR
Enable-future-vprox-plani

IF
THEN

personi
personi

person2

fired

as

KNOW TELNO of person2
and
ENABLE-FUTURE-VPROX

inference

in Cx.50

...)...)}

completed

completed

<!-- page: 360 -->
Mtrans-acceptable-inf3
IF
THEN

fired

as

inference

other MTRANS anything to self
MTRANS-ACCEPTABLE between self
other

TRACES FROM DAYDREAMER

in Cx.50

and

DAYDREAMER calls Karen and gets the star’s number.
him and ask him out:
FEBS IAI
OI IIK
Subgoals of #{0B.3166:
Subgoals of #{0B.2693:
AEGIS AICOIOI
ieiecaliehim:.
FEC
OIA II IO K
Vprox-plan2
IF
THEN

fired

as

(SUCCEEDED-GOAL obj (KNOW actor
(ACTIVE-GOAL obj (M-PHONE actor

inference

completed
completed

in Cx.50

M-PHONE person
VPROX person

etter
ier err etS tet,
Subgoals of #{0B.3191:
Subgoals of #{0B.2683:
SESS
IAII IK
| I ask him out.
FEI IIR
Believe-plani
IF
THEN

...)...)}
...)...)}

Now she can call

MTRANS
person

Subgoals
FEC

fired

(SUCCEEDED-GOAL obj (VPROX...)...)} completed
(ACTIVE-GOAL obj (MTRANS actor ...)...)} completed

as

inference

mental state
BELIEVE self

of #{0B.1662:
IAI K

Terminating

planning

Leaf

#{CX.50:

context

in Cx.50

to person
mental state

(ACTIVE-GOAL

obj

for top-level

goal

(RECOVERY...)...)}

completed

(CX)}

(OB. 1662:

(SG. (RECOVERY obj (FAILED-GOAL obj (LOVERS actor Me Movie-stari))))]
(OB.3196: (SG. (MTRANS actor Me from Me to Movie-stari
obj (ACTIVE-GOAL obj (LOVERS actor Me Movie-star1))))]
(0B.3191: (SG. (VPROX actor Movie-stari Me))]
(OB.3172: (SG. (M-PHONE actor Me to Movie-star1))]
(OB.3166: (SG. (KNOW actor Me
obj (TELNO actor Movie-star1)))]
(0B.3125: (SG. (MTRANS actor Karen from Karen to Me
obj

(TELNO

actor

([0B.3005:

obj

(TELNO

Movie-star1)))]

(SG.

actor

(KNOW

actor

Karen

Movie-star1)))]

[OB
.2969:
(SG.

(MTRANS

actor Movie-star1

from Movie-stari

obj
obj

(TELNO actor Movie-star1)))]
(OB.2657: (SG. (KNOW actor
(TELNO actor Movie-star1)))]

Movie-star1

to Karen

<!-- page: 361 -->
## A.7 RECOVERY2 obj

DAYDREAM

([0B.2900:

(SG.

(BELIEVE

(ACTIVE-GOAL

obj

(KNOW actor

[0B.2876:

obj

(ACTIVE-GOAL

obj

obj

(LOVERS
(SG.

actor Movie-stari

(BELIEVE

actor

obj

Karen)))]

[0B.2759:

(SG.

(BELIEVE

(ATTRACTIVE

(POS-ATTITUDE

(SG.

(SG.

obj

(RICH actor

actor

Movie-star1

(ATTRACTIVE

(BELIEVE

obj

actor

Karen))]

Movie-stari

obj Karen)))]

[OB.2806:

(RICH actor

Karen))))]

Movie-stari

obj Karen)))]
(0B.2755:

obj

Movie-star1i

Karen

(ROMANTIC-INTEREST

([0B.2825:

obj

actor

obj (TELNO actor Movie-star1)))))]
(BELIEVE actor Movie-stari

(SG.

[0B.2831:
obj

341

(SG.

(BELIEVE

actor

Movie-star1

(BELIEVE

actor

Movie-stari

Movie-stari)))]
[0B.2820:

(SG.

Karen)))]

[OB.2816: (SG. (RICH actor Karen))]
[OB.2869: (SG. (BELIEVE actor Movie-star1

obj

(NOT obj

(LOVERS

actor

[0OB.2852:

[0B.2849:
[0B.2948:

obj

(BELIEVE

Movie-star1))))]

(SG.

(SG.

(RTRUE))]

(BELIEVE

actor

actor

Movie-star1))]

Movie-stari

actor Karen
obj (ACTIVE-GOAL

obj (KNOW actor Karen
(TELNO actor Movie-star1))))))]

obj
[OB.2927:

obj

(M-BREAK-UP

(SG.

(ACTIVE-GOAL

(SG.

obj

(MTRANS

actor

(KNOW actor

Karen

Karen

from

Karen

to Movie-star1i

obj (TELNO actor Movie-star1)))))]
[0B.2920: (SG. (VPROX actor Movie-stari Karen)
)]
(0OB.2923: (SG. (VPROX actor Karen Movie-star1))]
(OB.2965: (SG. (VPROX actor Movie-star1 Movie-star1))]

[OB.2962:
[0B.3013:

obj

obj

(SG.

(SG.

(ACTIVE-GOAL

obj

[0B.3104:

(SG.

(BELIEVE

actor
obj

(RTRUE))]

(BELIEVE

actor

Me

obj (TELNO actor
actor Karen

(BELIEVE

Movie-star1)))))]

Me
(ACTIVE-GOAL

obj
obj

Karen

(KNOW actor

obj

(TELNO

(0B.3081:

(SG.

(MTRANS

(ACTIVE-GOAL

obj

(KNOW

(KNOW

actor

actor

actor

obj

actor

Me

Movie-star1))))))]

Me from

Me to Karen

Me

(TELNO actor Movie-star1)))))]

(0B.3072: (SG. (VPROX actor Karen Me))]
[0B.3053: (SG. (M-PHONE actor Me to Karen))]
[0B.3048:

obj

[0B.3077:
[0B.3121:

(SG.
(SG.

(0B.3118:

Removing

Store

(SG.

(KNOW

actor

Me

(TELNO actor Karen) ))]

motivating

episode

(SG.

(VPROX
(VPROX

actor
actor

Me Karen))]
Karen Karen)
)]

(RTRUE))]

emotions

#{0B.1662:

(SUCCEEDED-GOAL

obj

(RECOVERY...)...)

<!-- page: 362 -->
FIO

RECOVERY3
I

III

Daydream

I IK

Entertainment-theme

fired

as

inference
threshold

IF

ENTERTAINMENT

need

below

THEN

activate

for

ENTERTAINMENT

goal

FAO
IIR II I CIC
Activate top-level goal #{0B.1835:
| I want to be entertained.
| I feel

interested

in being

in Cx.4

(ACTIVE-GOAL

goal #{0B.1835:

Entertainment-plan2

as plan

for #{0B.1835:
in Cx.4

fired

(ACTIVE-GOAL

sprouting

obj

(ENTERTAINMENT...)...)

entertained.

Switching to new top-level
SEO
II
IC

IF
THEN

TRACES FROM DAYDREAMER

APPENDIX A. ANNOTATED

342

obj

(ACTIVE-GOAL

obj

(ENTERTAINMENT)}

(ENTERTAINMENT...)...)}

Cx.7

goal for ENTERTAINMENT
subgoal to MTRANS mail

The level of satisfaction of DAYDREAMER’s need for entertainment has
again fallen below the threshold, and so an ENTERTAINMENT goal is activated. This time, DAYDREAMER selects the plan of getting and reading the
mail.
FEO
ORI II IK
Poss-plan2

fired

for #{0B.1857:
in Cx.7

as plan

(ACTIVE-GOAL

sprouting

IF

goal

THEN

subgoal

obj

(POSS actor

...)...)}

Cx.8

for person

to POSS

object

to GRAB object

FEI
I III IK
Grab-plan

fired

for #{0B.1880:
in Cx.8
IF
THEN

as plan

(ACTIVE-GOAL

sprouting

obj

(GRAB actor

...)...)}

Cx.9

goal to GRAB object
subgoal to be AT location

of object

Fact

plan #{0B.528: (AT actor Maili obj Outside)} found
JESS
GIOIAOIIII IK
Goal #{0B.1902: (ACTIVE-GOAL obj (AT actor ...)
top-level-goai

...)}

FEI OIC CIO IOIIO IO IK
At-plan2

fired

for #{0B.1909:
in Cx.10
IF
THEN

as plan

(ACTIVE-GOAL

sprouting

obj

(AT actor

Cx.11

goal to be AT a location
subgoal to PTRANS to that

location

...)

top-level-goal

...)}

succeeds

<!-- page: 363 -->
Ptrans-plan

fired

for #{0B.1938:
in Cx.11
IF
THEN

as plan

(ACTIVE-GOAL obj (PTRANS actor

sprouting

...)...)}

Cx.12

goal for person to PTRANS to a location
subgoal for person to KNOW that location

FE
IG aI
Goal #{0B.1943: (ACTIVE-GOAL obj (KNOW actor ...)...)} succeeds
Subgoals of #{0B.1938: (ACTIVE-GOAL obj (PTRANS actor ...)...)} completed
Current top-level goal waits #{0B.1835: (ACTIVE-GOAL obj (ENTERTAINMENT...)...)}
No more

goals

to run;

switching

to performance

Waking up top-level

goal #{0B.1835:

State

DAYDREAMING

changes

from

Subgoals

of #{0B.1938:

Perform

external

Believe-believe-inf

(ACTIVE-GOAL

fired

obj

as

SEA
I I aK
At-plan2 fired as inference

(ENTERTAINMENT...)...)}
...)...)}

obj

(PTRANS

actor

(PTRANS

actor

...)...)}

inference

completed

of #{0B.1909:

succeeds

in Cx.12

in Cx.12

person PTRANS from one location
person AT new location and
no longer AT old location

Subgoals

obj

to PERFORMANCE

action

SA
IG
IK
Goal #{0B.1938: (ACTIVE-GOAL
| I go outside.
FeO
a Ia Ik

IF
THEN

mode

(ACTIVE-GOAL

(ACTIVE-GOAL

to another

obj

(AT actor

...)

top-level-goal

...)}

completed

FA
RII K
Goal #{0B.1909: (ACTIVE-GOAL obj (AT actor ...)
top-level-goal ...)} succeeds
Subgoals of #{0B.1912: (ACTIVE-GOAL obj (GRAB actor ...)...)} completed
Perform

external

action

EAI
I IO IICI IK
Goal #{0B.1912: (ACTIVE-GOAL obj (GRAB actor
| I grab the mail.
JAI
I I I IK
Poss-plan2 fired as inference in Cx.12
SEA
II IO II
Vprox-plan3 fired as inference in Cx.12
IF
THEN

POSS and object
VPROX that object

FA

IRI

Poss-mail-inf

fired

as

| I have the UCLA Alumni
FEO
IO IIR III
Vprox-plan3 fired as
IF
THEN

POSS and
object
VPROX that

inference

inference

object

in Cx.12

directory.
in Cx.12

...)...)}

succeeds

<!-- page: 364 -->
| Input:

Carol

| Input:

Carol’s

Burnett

went

telephone

to UCLA.
number

Adding rule

#{INDUCED-RULE.1:

Serendipity!!

(daydreaming

is in the UCLA

(RULE subgoal

Alumni

(COLLEGE

directory.

actor

...)...)}

goal)

[0B.2035:

(AG. (MTRANS actor Me from Me to Movie-star1l
obj (ACTIVE-GOAL obj (LOVERS actor Me Movie-star1)))) EPISODE.4]
(0B.2124: (AG. (VPROX actor Movie-stari Me)) EPISODE.5]
[0B.2128: (AG. (M-PHONE actor Me to Movie-star1i)) EPISODE.6]
[0B.2132: (AG. (KNOW actor Me
obj (TELNO actor Movie-stari))) EPISODE.7]
[0B.2114: (AG. (MTRANS actor Me
from (ALUMNI-DIR
to Me

obj

obj

Movie-stari)))

(TELNO actor

([0B.2140:

obj

(TELNO

[0B.2146:

(AG.

(AG.

actor

EPISODE.8]

actor

(ALUMNI-DIR

Movie-stari)))

EPISODE.9]

(AG.
(AG.

(ALUMNI-DIR

([0B.2100:

(AG.

(BELIEVE

(0B.2094:

obj

(KNOW

(COLLEGE actor Movie-stari

([0B.2111:
[0B.2107:

obj

?Var.1731:ORGANIZATION)

(VPROX actor
(VPROX actor
obj

obj

?Var.1731:ORGANIZATION)

obj ?Var.1731:ORGANIZATION))]

Me Me))]
Me

?Var.1731:ORGANIZATION)
))]

(BELIEVE actor Me

actor Me
obj (ACTIVE-GOAL obj (KNOW actor Me
obj (TELNO actor Movie-star1))))))]
(AG.

(BELIEVE

(ACTIVE-GOAL

obj

actor

Me

(KNOW actor

Me

obj (TELNO actor Movie-star1)))))]
| What

do you know!

DAYDREAMER receives an Alumni directory from her college, UCLA, in the
mail. The program is then provided with world states as input: The directory
contains the telephone number of Carol Burnett, who happens also to have
gone to UCLA. A new rule is induced from this input: An Alumni directory
for a given college contains the telephone number of a person if that person
went to that college. The serendipity mechanism recognizes that this new rule
is applicable to the active goal of getting in touch with the movie star. In
particular, DAYDREAMER needs to obtain the Alumni directory of the college
the movie star attended. However, since DAYDREAMER is still in performance
mode, it must first complete the processing of its ENTERTAINMENT goal:
Subgoals of #{0B.1924: (ACTIVE-GOAL obj (POSS actor ...)...)} completed
FESO
IAG II AK
;
Goal #{0B.1924: (ACTIVE-GOAL obj (POSS actor ...)...)} succeeds
Fact plan #{0B.2021: (KNOW actor Alumni-dir1 obj ...)} found
FEISS
III IIR
Goal #{0B.1921: (ACTIVE-GOAL obj (KNOW actor ...)...)} succeeds
FOSS SIOIOI IIA IK
Mtrans-plani

for #{0B.2471:

fired

in Cx.17

sprouting

IF

for

goal

as plan

(ACTIVE-GOAL

obj

(MTRANS

actor

...)...)}

Cx.18

person2

to MTRANS

info

from

object

to person1

<!-- page: 365 -->
## A.8 RECOVERY3 DAYDREAM THEN

subgoals for object to KNOW info and
for personi to be VPROX person2 and
for person2 to be VPROX object and
for person2 to BELIEVE that personi has ACTIVE-GOAL
to KNOW info and
for person2 to have ACTIVE-GOAL for personi to KNOW
info

345

FEO IC

Goal #{0B.2492:

(ACTIVE-GOAL

Perform

action

external

obj

(VPROX actor

...)...)}

succeeds

obj

(MTRANS

...)...)}

succeeds

FAI
I II AGK

Goal #{0B.2650:

(ACTIVE-GOAL

actor

| I read her telephone number in the UCLA
FEI CICK
Know-plan fired as inference in Cx.23

Alumni

IF
THEN

to person

someone MTRANS info
person KNOW info

from

FEI
IC IIE
Enable-future-vprox-plani
IF
THEN

fired

as

personi
personi

KNOW
and

person2

ENABLE-FUTURE-VPROX

FC

TELNO

some

object

inference

directory.

in Cx.23

of person2

CAR

Entertainment-inf2

Subgoals

fired

of #{0B.1835:

as

inference

(ACTIVE-GOAL

in Cx.23

obj

(ENTERTAINMENT...)...)}

FEC IK
Goal #{0B.1835: (ACTIVE-GOAL obj (ENTERTAINMENT...)...)}
| I succeed at being entertained.
Terminating

planning

#{0B.1835:

(SUCCEEDED-GOAL

for

top-level

obj

completed

succeeds

goal

(ENTERTAINMENT...)...)}

Removing motivating emotions.
Emotional responses.

| I feel amused.
Store episode #{0B.1835:
No more

State

goals

to run;

changes

(SUCCEEDED-GOAL

obj

switching

to daydreaming

from PERFORMANCE

to DAYDREAMING

(ENTERTAINMENT...)...)
mode

Now that DAYDREAMER has completed its ENTERTAINMENT
shifts to RECOVERY and getting in touch with the star:
Switching
Apply

to new top-level

existing

analogical

goal #{0B.1810:

(ACTIVE-GOAL

plan

FEO
OCI IC IACK
Mtrans-plan2

for #{0B.2150:
in Cx.14

fired

as

analogical

(ACTIVE-GOAL

sprouting

obj

plan

(MTRANS

actor

Cx.25

IF
goal to MTRANS mental state to person
THEN subgoal to be VPROX that person

...)...)}

obj

goal, focus

(RECOVERY...)...)}

<!-- page: 366 -->
Apply

TRACES FROM DAYDREAMER

APPENDIX A. ANNOTATED

346

existing

analogical

plan

FEC
I IIR III
Vprox-plan2

fired

for #{0B.2742:
in Cx.25

as

analogical

(ACTIVE-GOAL

sprouting

plan

obj (VPROX actor

...)...)}

Cx.26

IF

goal to be VPROX

THEN

subgoal

Apply

existing

person

to M-PHONE

person

analogical

plan

AG
RIC IKK
M-phone-plani

fired

for #{0B.2747:
in Cx.26

as

analogical

(ACTIVE-GOAL

sprouting

obj

plan

(M-PHONE

actor

...)...)}

Cx.27

IF

goal to M-PHONE

person

THEN

subgoal

TELNO

Apply

existing

to KNOW

analogical

of person
plan

FAC I CR
Know-plan

fired

for #{0B.2753:
in Cx.27
IF
THEN

as

analogical

(ACTIVE-GOAL

sprouting

plan

obj

(KNOW actor

goal for person to KNOW info
subgoal for someone to MTRANS
to that person

| I have to read Harrison
Apply existing analogical

FEC

Ford’s
plan

info

from

telephone

some

object

number

in the

Alumni

IK

Mtrans-plani

fired

for #{0B.2759:
in Cx.28

...)...)}

Cx.28

as analogical

(ACTIVE-GOAL

sprouting

obj

plan

(MTRANS

actor

...)...)}

Cx.29

IF
THEN

goal for person2 to MTRANS info from object to personi
subgoals for object to KNOW info and
for personi to be VPROX person2 and
for person2 to be VPROX object and
for person2 to BELIEVE that personi has ACTIVE-GOAL
to KNOW info and
for person2 to have ACTIVE-GOAL for personi to KNOW
info

Apply

existing

analogical

plan

FISICA
III IK
Induced-rule.1 fired as analogical plan
for #{0B.2765: (ACTIVE-GOAL obj (KNOW actor
in Cx.29 sprouting Cx.30
FEO
IIOI ICICI IK
Inverse-college-plan

fired

for #{0B.2788: (ACTIVE-GOAL
in Cx.30 sprouting Cx.31
| I have

to know

where

...)...)}

as plan

obj

he went

(COLLEGE
to college.

actor

...)...)}

directory.

<!-- page: 367 -->
In order for DAYDREAMER to find out the star’s telephone number, she must
read his telephone number in the Alumni directory of the college he attended.

Therefore, she must first know what college he attended (if any).

Episodic

reminding

of #{EPISODE.3:

| I remember the time I knew where
| college by reading that she went

(EPISODE

rule Know-plan

Brooke Shields went to
to Princeton University

goal

...)}

in

| People magazine.

DAYDREAMER recalls an episode in which she was able to find out what
college Brooke Shields was attending. This plan is then applied by analogy:
FEO
ICR ACK
Know-plan

fired

as

analogical

for #{0B.2855: (ACTIVE-GOAL
in Cx.31 sprouting Cx.32
IF
THEN

plan

obj

(KNOW actor

goal for person to KNOW info
subgoal for someone to MTRANS
to that

| I have

info

...)...)}

from

some

object

person

to read

where

he went

Store episode #{0B.1810:

to college

(SUCCEEDED-GOAL

in People

obj

magazine.

(RECOVERY...)...)

This episode is then stored for possible future use in achieving

a LOVERS

goal with the movie star (or other movie stars).

## A.9 REVENGES3 Daydream

DAYDREAMER goes to Westward Ho to buy a newspaper, learns of an opening
at the Broadway, interviews for the job and is hired. After all personal goal
concerns have completed, DAYDREAMER switches to daydreaming mode and
generates a REVENGE daydream which was pending:
Switching

to new

top-level

goal

#{0B.1749: (ACTIVE-GOAL obj (REVENGE actor ...)...)}
Emotion #{0B.1768: (POS-EMOTION strength 0.14273477802205323...)}

below threshold.

Episodic reminding.
| I remember the time I got even with Harrison Ford for turning me down
| by studying to be an actor, being a star even more famous than he is,
| him calling me up, him asking me out, and me turning him down.

FAI
I IO
Revenge-plani

fired

as

analogical

for #{0B.1749: (ACTIVE-GOAL
in Cx.10 sprouting Cx.41
IF
THEN

obj

plan

(REVENGE

actor

...)...)}

goal to gain REVENGE against person for causing self
a failed POS-RELATIONSHIP goal
subgoal for person to have failure of same POS-RELATIONSHIP

<!-- page: 368 -->
its previous

recalls

DAYDREAMER

revenge

daydream

in response

to

A single rule from this daydream (Revenge-plan1) is applied to

LOVERS1.

the current situation. Other rules of the old daydream are not applicable to the
EMPLOYMENT situation; thus the remainder of the daydream is generated
through regular planning:
FESS

III

AIK

Employment-theme-plan fired
for #{0B.2925:
(ACTIVE-GOAL
in Cx.41 sprouting Cx.43

as plan
obj (BELIEVE

actor

...)...)}

actor

...)...)}

FOG III IK
Pos-att-employ-plani

for #{0B.2952:

fired

(ACTIVE-GOAL

as plan

obj

in Cx.43

sprouting

Cx.44

Subgoal

relaxation,

#{0B.2958:

| Say I am a powerful

(BELIEVE

(ACTIVE-GOAL

obj

(BELIEVE

actor))}

succeeds

executive.

FO III
IE IK
Goal #{0B.2958: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds
Subgoals of #{0B.2952: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} completed
FOO
IAI IK
Goal #{0B.2952: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds
Subgoals of #{0B.2925: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} completed
FEC
III IIR ACK
Goal #{0B.2925: (ACTIVE-GOAL obj (BELIEVE actor ...)...)} succeeds
FESO AIK
Mtrans-plan2 fired as analogical plan
for #{0B.2930:
(ACTIVE-GOAL obj (MTRANS
in Cx.45 sprouting Cx.46

IF
THEN

actor

...)...)}

goal to MTRANS mental state to person
subgoal to be VPROX that person

FOIA
II IK
Goal #{0B.2987: (ACTIVE-GOAL obj (VPROX actor ...)...)} succeeds
Subgoals of #{0B.2930: (ACTIVE-GOAL obj (MTRANS actor ...)...)} completed
FOIA III
I II K
Goal #{0B.2930: (ACTIVE-GOAL obj (MTRANS actor ...)...)} succeeds
| Agatha offers me a job.

## A.10 COMPUTER-SERENDIPITY
| Input: Computer.
Serendipity!! (personal goal)
| What do you know!
Episodic reminding of #{EPISODE.8}
| I remember the time Harold and I broke the ice by
| me being a member of the computer dating service, and by him
| being a member of the computer dating service. I knew his
| telephone number by the dating service employee
| telling me Harold’s telephone number.

<!-- page: 369 -->
DAYDREAMER has an active goal to initiate a LOVERS relationship with
someone when she happens to stare at an object in the room: a computer.
The serendipity mechanism, using this physical object as one index and the
active LOVERS goal as another, retrieves an episode which contains a plan for
achieving the LOVERS goal. In general, the episode which is retrieved need not
be directly related to, or indexed under, the LOVERS goal; rather, it is sufficient
for the episode to contain a rule which is applicable to any potential subgoal of
the LOVERS goal. An intersection search in the space of rule connections is
employed in order to recognize such a relationship, and unification is employed to
verify applicability of a path to the current concrete goal. The episode produced
by the serendipity mechanism is then applied through analogical planning:
Apply

existing

analogical

plan

FARSI
IO ISIC IK Kk
Lovers-plan

fired

as

analogical

plan

for #{0B.2038}
in Cx.25

IF
THEN

Apply

sprouting

Cx.26

goal for LOVERS with a person
subgoals for ACQUAINTED with person and
ROMANTIC-INTEREST in person and
person have ACTIVE-GOAL of LOVERS with self
self and
person M-DATE self and
person M-AGREE to LOVERS
existing

analogical

and

plan

FEO
IO IRI IAC
Acquainted-plan

fired

as

analogical

plan

for #{0B.2339}
in Cx.26

sprouting

Cx.27

IF
THEN

goal to be ACQUAINTED with
subgoal for M-CONVERSATION

Apply

existing

analogical

FAI
OO IOI IAC
M-conversation-plan

fired

person
with person

plan

as

analogical

plan

for #{0B.2367}
in Cx.27

sprouting

Cx.28

IF

goal for M-CONVERSATION between person1 and
person2
THEN subgoals for MTRANS-ACCEPTABLE between person1
and
person2 and
personi to MTRANS to person2 something and
person2 to MTRANS to personi something
Apply

existing

analogical

FAO
IIIT
Induced-rule.2 fired

for #{0B.2371}

plan

as analogical

plan

<!-- page: 370 -->
in Cx.28 sprouting Cx.29
| I have to be a member of the dating service.
| He has to be a member of the dating service.
FEI
IOI IO IK
Induced-rule.1 fired as analogical plan

for #{0B.2382}
in Cx.29
| I have

sprouting Cx.30
to pay the dating

service

employee.

TRACES FROM DAYDREAMER
