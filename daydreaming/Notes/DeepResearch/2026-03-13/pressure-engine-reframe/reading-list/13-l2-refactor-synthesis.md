# L2 Refactor Synthesis

Purpose: translate the `L2` source cluster into a concrete refactor
direction aligned with `11-settled-architecture.md`.

Sources folded here:

- `05-ema-extraction.md`
- `06-occ-extraction.md`
- `07-versu-extraction.md`
- `11-mueller-ch7-extraction.md`
- `12-mueller-appendix-b-extraction.md`

---

## Core Claim

`L2` is the live character-pressure engine.

It is not:

- a drama manager
- a global planner
- a knowledge graph reasoner
- a world-building system
- the conductor

Its job is narrower and more important:

- maintain active concerns
- appraise imagined developments
- retrieve and reactivate relevant episodes
- choose locally available exploratory operators
- preserve alternate contexts and traces
- surface an intelligible inner-life stream

In the settled architecture, `L2` is the engine core. `L3` traverses
authored graph material. `L1` critiques or expands authored structure.
The graph is the interface between them, not the thing `L2` collapses
into.

---

## What Stays from Mueller

Mueller still provides the kernel.

### 1. Concern-centered control stays

The core loop remains:

1. initialize or reactivate concerns
2. select the concern with the strongest emotional motivation
3. run local exploration
4. update affective state and traces
5. repeat

This is still the right answer to "what is pressing right now?" for
`L2`.

### 2. Contexts stay

Contexts are not optional bookkeeping. They do two jobs:

- support hypothetical branch exploration and backtracking
- preserve abandoned or partial lines as future traces and episodes

This is the right near-term substrate for alternate local worlds.
`ATMS` remains a later representational upgrade, not a prerequisite for
the refactor.

### 3. Reminding stays and should become explicit

Reminding is not just retrieval. It is associative reactivation:

- retrieve a relevant episode
- reactivate associated emotions
- add its indices back into the recent pool
- allow recursive reminding
- run serendipity recognition over what now co-occurs

This deserves to be a named subsystem in the refactor, not an implicit
side effect.

### 4. Serendipity stays, but in its stronger form

Serendipity is not "random good idea generation." It is:

- intersection search over active structures
- path verification that a discovered connection is actually usable

Action mutation is subordinate to this. Mutate only when needed, and
treat the mutation as a candidate that still has to be validated by
serendipity recognition.

### 5. English generation stays as a realization layer

Mueller's Appendix B matters because it proves that the internal stream
can be rendered in a disciplined way:

- template-driven realization
- hypothetical and remembered forms
- belief-path-sensitive narration
- pruning
- paragraph breaks on branch or concern shifts

This should inform the dashboard and trace narration layer, not the core
search loop itself.

---

## What EMA Adds

`EMA` is the cleanest upgrade to Mueller's affective loop.

### 1. Add an explicit appraisal pass after each imagined development

Each local step in `L2` should be followed by a fast appraisal update.
This gives the kernel a uniform way to answer:

- what just changed for this character?
- is that good or bad?
- how expected was it?
- how controllable or changeable does it seem?

### 2. Use appraisal to bias coping families, not to hard-dispatch them

The right import from `EMA` is:

- appraisal variables shape operator preference
- they do not deterministically map to one operator family

So the refactor should avoid:

- `if uncontrollable => use escape family`

and prefer:

- uncontrollable or low-changeability raises the weight of some coping
  families and lowers others
- available operators still depend on practice, local context, and
  concern state

### 3. Reappraisal must be continuous

`L2` should not appraise once per concern. It should reappraise as the
branch evolves. This is the main benefit of bringing `EMA` in: inner
life becomes dynamically updated rather than front-loaded.

---

## What OCC Adds

`OCC` supplies the emotion vocabulary and intensity structure that
Mueller leaves comparatively loose.

### 1. Give emotions stable typed labels

The refactor should stop treating emotion as a mostly undifferentiated
motivation scalar. It should carry at least:

- emotion type
- target or object
- intensity

The revised hierarchy is useful because it clarifies category structure
and adds the `interest/disgust` branch that helps with novelty and
familiarity-sensitive reactions.

### 2. Make intensity derivation less arbitrary

Intensity should be derived from appraisals and local modifiers rather
than assigned ad hoc. The exact formula can stay simple in v1, but it
should be traceable to factors like:

- desirability
- likelihood or certainty
- effort or investment
- realization or immediacy

### 3. Treat compound emotions as deferred

The hierarchy is worth adopting now. Full compound-emotion composition is
not needed for the first refactor.

---

## What Versu Adds

`Versu` is the strongest source for upgrading operator availability.

### 1. Social practices should gate what can happen next

The key idea is not "simulate all of Versu." It is:

- local social context determines what actions are legible
- roles within that context determine who can do what
- affordances should be practice-bound, not globally available

This is the best fix for the current risk that `L2` operators feel too
abstract or too context-free.

### 2. Practices belong in `L2`, not in `L3` or the conductor

Practices are not:

- graph traversal policy
- conductor logic
- authored graph ontology

They are local social situation state inside `L2` that shapes
exploration.

### 3. Start with lightweight practice tags

The first refactor does not need full `Versu` machinery. It needs:

- active practice tag or small set of tags
- local roles
- operator gating based on those tags and roles

That is enough to make appraisal and exploration socially legible
without importing a full practice engine.

---

## Recommended `L2` State Model

The refactor should make these state objects explicit:

### 1. Concern state

- concern id
- target
- current priority
- dominant motivation
- active or dormant status

### 2. Appraisal state

- current appraised situation
- emotion type
- emotion intensity
- controllability
- changeability
- expectation or surprise

### 3. Practice/context state

- active practice tags
- active roles
- branch context id
- recent local events

### 4. Memory activation state

- reminded episode refs
- recent indices
- currently reactivated material

### 5. Trace/narration state

- branch summary
- why this branch was pursued
- what changed emotionally
- what was reminded

---

## Minimal Refactor Shape

The refactor can be understood as five modules.

### 1. Concern Controller

Owns concern activation, prioritization, dormancy, and selection.

### 2. Appraisal Engine

Runs after each local imagined development and updates emotion state.

### 3. Practice-Gated Operator Selector

Chooses among locally available exploratory operators based on:

- concern state
- appraisal state
- active practice
- role availability

### 4. Reminding and Serendipity Layer

Handles episode retrieval, associative reactivation, intersection
recognition, and validated mutation.

### 5. Realization Layer

Produces dashboard-facing narration and trace summaries from internal
state without dumping every internal assertion.

---

## Minimal Graph Seam from `L2`

`L2` should write a narrow, inspectable projection into the shared
graph/interface layer. Not its whole internals.

The first useful projection is:

- active concern refs
- dominant emotion type and intensity
- appraisal summary tags
- active practice tags
- reminded episode refs
- branch outcome or pressure shift tags

This gives `L3` and the dashboard something legible without making the
graph pretend to be the whole `L2` machine.

---

## Non-Goals for the Refactor

Do not let the `L2` refactor expand into these:

- full social simulation
- conductor logic
- authored graph traversal
- full truth maintenance
- full compound-emotion modeling
- deterministic appraisal-to-operator lookup
- narration as exhaustive debug logging

Those either belong elsewhere or can wait.

---

## Practical Build Order

### Step 1. Re-state the Mueller kernel in explicit state objects

Do this before adding new theory. Make concerns, contexts, reminding,
and traces first-class.

### Step 2. Insert the appraisal layer

Add `EMA`-style fast reappraisal after each imagined development and use
it to update typed `OCC` emotion state.

### Step 3. Gate operators by practice

Add lightweight `Versu` practice tags and roles so operator selection is
socially situated.

### Step 4. Make reminding and serendipity observable

Expose what was reminded, why, and what verified cross-connection was
found.

### Step 5. Build the inner-life dashboard on top

Use the Appendix B lesson: render selected internal state cleanly and
economically, with real narration choices rather than raw dumps.

---

## What the Refactor Should Prove

If this refactor is working, three things should become true:

1. `L2` branches look emotionally and socially legible, not just
   procedurally generated.
2. Operator choice changes in understandable ways when appraisal state
   or practice state changes.
3. The dashboard can explain why a character is pursuing a line, what
   memory or pressure got activated, and what changed after exploration.

That is enough to justify the refactor. It does not need to solve all of
character cognition in one pass.
