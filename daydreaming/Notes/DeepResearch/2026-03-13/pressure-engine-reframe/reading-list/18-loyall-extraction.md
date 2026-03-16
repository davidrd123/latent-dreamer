# Loyall / Hap — Believable-Agent Extraction

Source: A. Bryan Loyall, "Believable Agents: Building
Interactive Personalities" (CMU PhD thesis, 1997)

Source file: `sources/markdown/Loyall/source.md`

Purpose: Extract what Loyall actually contributes to the current
architecture: the **believability target**, the concrete **Hap
control semantics** under the Oz lineage, and the design
warnings about modularity, generality, and rationality.

---

## What the thesis actually is

This is the foundational Oz-line statement of the believable-agent
problem. It makes three main contributions:

1. a requirements analysis for believable agents
2. Hap, a behavior language/runtime for expressing personality in
   real time (including the argument that emotion, sensing, action,
   and language should live in one expressive fabric rather than in
   isolated modules)
3. natural language generation for believable agents

So the value here is not "copy this 1997 implementation." The
value is that Loyall is explicit about **what kind of agent is
being built** and **what runtime properties are required** if the
result is supposed to feel like a particular person instead of a
generic solver.

---

## The actual target

Loyall is unusually clear that believable agents are **not** the
same thing as:

- realistic simulation
- intelligent task completion
- rational action selection

The target is a character with legible inner life. The requirement
set is the strongest steal in the thesis:

- personality
- emotion
- self-motivation
- change over time
- social relationships
- consistency of expression
- the "illusion of life"

That last bucket includes the properties that matter most for the
runtime:

- appearance of goals
- concurrent pursuit of goals
- parallel action
- reactivity and responsiveness
- situated behavior
- exist in a social context
- visible resource bounds
- broad capability
- well-integrated behavior

This is directly relevant to the project because it gives a better
evaluation frame than "did the system choose a good action?" The
real question is: **did it behave like this character, in this
situation, under these pressures?**

---

## What Hap actually is

Hap is a behavior runtime, not a planner over an explicit world
model.

The main objects are:

- **goals**: atomic things the character is trying to do
- **behaviors**: authored ways of pursuing a goal
- **primitive actions**: physical or mental actions
- **active behavior tree (ABT)**: the live runtime structure of
  what the agent is currently pursuing

At runtime, Hap expands the ABT greedily. A goal can have multiple
behaviors. The runtime chooses among them using authored control
knowledge such as:

- preconditions
- specificity
- priorities
- conflicts
- success/failure propagation
- reactive annotations

This matters because it is a concrete answer to "how do you make a
character behave like a person rather than follow one monolithic
policy?" The answer is: **authored variation plus live arbitration
over multiple active goals**.

---

## The stealable core

### 1. Multiple authored behaviors for the same goal

Hap assumes that the same motive can be realized in different
ways. A character may amuse himself, avoid embarrassment, answer a
door, or calm someone down through several different authored
behaviors.

Selection among those behaviors is not framed as optimal planning.
It is framed as **situation-conditioned personality expression**.

That maps well to the current operator idea. For one concern or
situation, we want several character-shaped realizations, not one
"best" move.

### 2. Situated variation is first-class

Preconditions and specificity let a behavior depend on the current
situation. The same goal can produce different action depending on
where the character is, what is happening, and what else is
already active.

This is important because the project is moving toward
situation-conditioned operator menus and social practices. Loyall
gives the control-level argument for why that conditioning matters:
behavior is not just "concern kind -> action," but "concern kind x
situation x active state -> one of several authored moves."

### 3. Concurrent goal pursuit is part of believability

Hap does not treat "one goal at a time" as sufficient. It pursues
multiple goals concurrently and lets action/resource conflicts,
priorities, and author-specified conflicts determine what can mix.

That is highly relevant to the pressure engine. A compelling
character is not flatly executing one objective. They are juggling
social obligation, resentment, attachment, shame, curiosity, and
self-protection at once.

### 4. Reactivity is built into the behavior language

Loyall emphasizes that believable agents must respond quickly to
events in the world. Hap provides demons, reactive annotations,
parallelism, and other structures so interruption is native to the
runtime rather than bolted on later.

This is a useful precedent for handling:

- interruption
- abandonment
- mid-course redirection
- opportunistic completion

The current architecture needs more of this than a simple
"operator selected -> trace committed" story.

### 5. The runtime should look resource-bounded

Hap explicitly treats visible limitation as part of believability.
It has physical resource conflicts and a single processing
bottleneck. The system cannot do everything at once.

This is useful conceptually even if we do not copy the exact
mechanism. Characters should have attention limits, interruption
costs, and tradeoffs, because unbounded competence flattens
personality.

### 6. Unified expressive fabric

One of Loyall's strongest claims is that action, sensing, emotion,
and language should be expressible in the same behavior fabric.
This is not just an implementation preference. It is an authoring
claim: if the seams are too hard, the author loses the ability to
write personality-specific local rules.

This is the part of the thesis that most directly supports the
current instinct to keep typed control geometry shared across
operators, appraisal, and narration, rather than letting them
drift into isolated systems that only exchange summaries.

---

## Emotion in the architecture

The emotion chapter is worth stealing for architectural shape, not
for its exact inventory.

The useful points are:

- emotions arise from goal success/failure and inferred causes
- emotions decay over time
- multiple emotions can be summarized into current style/state
- emotional state influences how the character acts, not just what
  facial expression gets shown

That is very close to the project's appraisal/coping needs.
Emotion should alter:

- what the character notices
- what they attempt
- what they avoid
- how they narrate and justify

Loyall is helpful here because he treats emotion as **control
state**, not cosmetic output.

---

## The design warnings

The later chapters are unusually relevant to this repo because
they name several traps directly.

### 1. Generality is not the top goal

Loyall argues that believable agents are about the unique, not the
general. In ordinary CS, generality is usually a virtue. Here it
can be a way to sand off the very thing that makes a character
specific.

That is a useful warning any time the architecture starts drifting
toward elegant but generic abstractions that no longer preserve
voice, bias, or habit.

### 2. Modularity can work against expression

He is explicit that tight modular boundaries can block
personality-specific authoring. If the author cannot easily mix
action, language, emotional state, and perception inside one local
piece of behavior, the system becomes cleaner but less expressive.

This does **not** mean "write one giant monolith." It means the
shared seams must stay rich enough for cross-cutting personality
logic.

### 3. Irrationality is a requirement, not noise

Loyall says outright that architectures built around rationality
are a bad fit for believable agents. Characters are often
compelling precisely because they are irrational.

For this project that is a strong defense of keeping mechanisms
like rehearsal, rationalization, avoidance, blame distortion,
and self-protective re-interpretation (our terms, not Loyall's)
as first-class architecture rather than failure cases around a
planner. Loyall's argument is general — characters should be
allowed to be irrational — and our specific cognitive-defense
inventory is one way to cash that out.

---

## What maps to our architecture

| Loyall concept | Current equivalent | Why it matters |
|---|---|---|
| Believability requirements | Pressure-engine goal of legible inner life | Better evaluation target than generic agent competence |
| Goal with multiple behaviors | Typed operator family plus authored realizations | Same pressure should admit several character-shaped moves |
| Preconditions + specificity | Situation/role-conditioned gating | Realizes "same concern, different move here" |
| Priorities + conflicts | Concern salience, interruption rules, scheduler pressure | Controls what steals attention and what cannot coexist |
| Concurrent goal pursuit | Multiple active concerns / simultaneous pulls | Matches the core pressure-engine picture better than single-objective control |
| Reactive annotations / demons | Interrupt handlers, continuation guards, live monitors | Needed for responsive social interaction |
| ABT | Missing active execution state | Current docs are stronger on selection than on enacted runtime state |
| Unified action-emotion-language fabric | Shared typed control geometry across L2 + narration + situation layer | Protects cross-cutting character logic |
| Emotion as control state | Appraisal + coping + narration bias | Emotion should change choice, not just expression |

---

## What's genuinely new vs. current docs

### 1. A stronger requirements frame

The project already argues for steerable inner life, but Loyall
names the runtime properties that make that plausible:
concurrency, reactivity, visible limits, integrated capability,
and consistency of expression.

### 2. Concrete semantics for authored character variation

The current docs often discuss operators and situations at the
design level. Loyall gives a runtime-level vocabulary for how
personality stays real:

- multiple behaviors per goal
- situation-conditioned selection
- explicit conflicts
- explicit interruption
- emotional style bias

### 3. The clearest anti-rationality argument in the lineage

This is important because later refactor work will naturally push
toward cleaner decision machinery. Loyall is the best reminder
that "cleaner" can mean "less characterful" if irrationality gets
treated as error.

### 4. A warning against over-clean seams

The repo now has several lanes: kernel, narration, conductor,
evaluation, situation structure. Loyall's warning is that if those
lanes only communicate through thin summaries, personality gets
flattened.

---

## What to take

1. **Use believability requirements as architecture constraints.**
   Concurrency, responsiveness, visible limits, and integrated
   behavior should be considered load-bearing, not polish.

2. **Preserve multiple authored realizations for the same
   pressure.** Same concern does not mean same move.

3. **Make interruption and continuation rules explicit.**
   Characters should be able to abort, redirect, or opportunistically
   finish a move.

4. **Treat emotional state as control state.** Appraisal should
   affect operator gating, blame assignment, continuation, and
   narration style.

5. **Allow authored irrationality on purpose.** The runtime should
   support biased and self-protective behavior without treating it
   as broken planning.

6. **Keep seams rich enough for cross-cutting expression.**
   Typed boundaries are good; dead boundaries are not.

---

## What not to take

- The full Hap / RAL implementation
- The 1997 body-control and animation assumptions
- The hand-built NLG grammar path as our main language layer
- The thesis's exact emotion model
- A return to one monolithic runtime with no typed seams

The real steal is not the old implementation. It is the control
philosophy plus the concrete runtime semantics for making a
character seem like a particular person.
