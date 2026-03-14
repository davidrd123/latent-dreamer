# Questions for GPT-5 Pro

This note is the first question pack for deep external review of the
pressure-engine reframe.

The goal is not generic brainstorming. The goal is to pressure-test
whether the reframe is a real architectural improvement, where it is
overreaching, and how it should intersect with the already-working
mainline without destabilizing it.

---

## What to include with the request

At minimum, send GPT-5 Pro these docs:

- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/01-reframe-summary.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/02-state-model.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/04-kernel-gap-analysis.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/05-stage-integration.md`
- `daydreaming/Notes/experiential-design/17-game-engine-architecture.md`
- `daydreaming/Notes/experiential-design/21-graffito-v0-playback-contract.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/review-request-5pro-arch/reply.md`

Optional but useful:

- `daydreaming/Notes/experiential-design/14-operating-map.md`
- `daydreaming/Notes/experiential-design/18-graffito-vertical-slice.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/re-evaluation/dr/deep-research-report (4).md` — full deep research report on kernel↔scope-drd integration (source for doc 05)
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/06-world-building-pressure-questions-for-5pro.md` — focused follow-up pack on whether world-building can actually be pressure-driven in a computable way
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/07-l3-traversal-questions-for-5pro.md` — focused follow-up pack on whether Level 3 traversal is actually the same kind of engine as Levels 1 and 2
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/08-prior-work-questions-for-5pro.md` — focused follow-up pack on what existing theories, systems, and workflow patterns we should appropriate instead of reinventing

---

## Framing for GPT-5 Pro

Please treat this as an architectural review, not a code review.

Be direct. We want category errors, hidden contradictions, and
overreach called out plainly. If the reframe is good but too broad,
say so. If the right answer is "keep the mainline and narrow the
reframe," say so.

Assume the project has two simultaneous needs:

1. A **shipping/art track** that needs a watched run soon
2. A **research/cognition track** that wants a cleaner, more general
   pressure-driven architecture

We do not want an answer that optimizes one track by silently
breaking the other.

Treat `04-kernel-gap-analysis.md` as a working translation attempt of
the reframe onto the current kernel, not as settled architecture. Part
of the review is to test whether its proposed refactoring boundaries are
actually category-correct.

---

## Core Questions

### 1. Is the three-level reframe actually one architecture?

The reframe claims the same loop runs at three levels:

- world-building / ideation
- character inner life
- director traversal

Question:

Is that a real unification, or a false symmetry?

More specifically:

- Are levels 1 and 2 genuinely the same kind of hypothetical
  exploration engine?
- Is level 3 actually the same kind of engine, or is it a different
  thing entirely: dramaturgical scheduling over authored material?
- Should level 3 be modeled as "pressure-driven traversal" without
  claiming it is the same type of context-forking cognition as levels
  1 and 2?

We want a judgment here, not just "it depends."

### 2. Is `Concern` too overloaded as a cross-level primitive?

The state model makes `Concern` the primitive at all three levels:

- creative/world-building pressure
- character psychological pressure
- director dramaturgical pressure

Question:

Should this remain one abstraction, or should it split into related but
distinct types?

For example:

- `CreativePressure`
- `CharacterConcern`
- `TraversalPressure`

What minimal invariant fields should be shared across all levels, and
what fields should be level-specific?

We want to avoid a vague abstraction that sounds elegant but becomes
mushy in use.

### 3. Are operators and adapters being conflated?

The reframe currently proposes that benchmark-specific adapters become
operator plugins.

Question:

Is that category-correct?

Please distinguish clearly between:

- an **operator** that explores inside a state space
- an **adapter/projection layer** that translates outputs between
  levels or between the kernel and the stage contracts
- a **renderer** that turns selected material into model-ready prompt
  form

If these should stay separate, define the boundary cleanly.

### 4. What is the correct role of the dream graph after this reframe?

The current mainline says:

- pre-authored graph
- intelligent traversal
- one-shot renderer

The reframe pushes toward:

- compressed initial state
- engine-driven exploration
- graph as artifact of motivated exploration

Question:

What is the right synthesis?

More concretely:

- Should the graph remain primarily a human-authored, curated creative
  artifact?
- Should it become partially engine-generated during authoring?
- Is "the graph is not a hand-authored asset" too strong and likely to
  undercut the quality gate of the current game-engine model?

We want the most honest formulation, not the most ambitious-sounding one.

### 5. Is `CommitRecord` sufficient and correctly scoped?

The reframe formalizes:

- `ontic`
- `policy`
- `salience`
- `none`

Question:

Is this commit model correct across all three levels, or is it really a
better fit for some levels than others?

For example:

- At level 2, `policy` vs `ontic` makes obvious sense
- At level 3, is "policy" really the right term, or is a different
  dramaturgical commit vocabulary needed?

Please pressure-test whether one commit taxonomy should span all levels.

### 6. How should level 3 connect to the existing playback contract?

Note: `05-stage-integration.md` contains a concrete DreamDirective
schema and integration architecture derived from a deep research
report that examined both repos. Please evaluate that proposal as
part of answering this question.

The current stable seam for performance is the normalized cycle packet:

- node id
- family / goal_type
- tension
- energy
- dwell
- visual description
- visit history

The reframe proposes director-level operators like:

- `build`
- `release`
- `confront`
- `shift`
- `recall`
- `juxtapose`
- `dwell`

Question:

How should these director operators map into the existing playback
contract without muddying semantics?

For example:

- Is "family coloring" still the right output vocabulary?
- Should level 3 emit a new field like `traversal_intent` instead of
  overloading `family`?
- What is the smallest contract change that keeps the live/batch seam
  clean?

### 7. Does the reframe preserve enough of Mueller to still claim his value?

The reframe keeps:

- control geometry
- coincidence retrieval
- context/fork logic
- mode oscillation

It drops or generalizes:

- family taxonomy as primitive
- Mueller-specific domain assumptions
- much of the original content machinery

Question:

Is this still properly "Mueller-derived" in a meaningful sense, or does
it become a new architecture that merely borrows a few useful patterns?

We are not asking about branding. We are asking about conceptual honesty.

### 8. What should change now, and what should stay aspirational?

This is the most important practical question.

Given the current state of the project, which parts of the reframe
should affect the near-term mainline now, and which should remain
research framing only?

Please answer in terms of:

- change now
- defer until after a watched run
- likely wrong turn / do not do

Specifically pressure-test:

- introducing `Concern` into the live/mainline packet
- changing the graph authoring story
- turning adapters into plugins
- using the engine for authoring-time world exploration
- altering the Graffito slice plan

### 9. What is the smallest falsifiable experiment for this reframe?

We do not want a grand migration plan first.

Question:

What is the smallest concrete experiment that would tell us whether this
reframe is buying something real?

Possible examples:

- run level-3 traversal with operator vocabulary distinct from Mueller
  family labels
- use the engine to generate authoring candidates from compressed world
  state, then compare against hand-authored graph quality
- compare a concern-driven director packet against the current
  family/tension packet for one slice

We want the best falsification step, not a list of ten nice ideas.

### 10. If you were rewriting this architecture honestly, what would the diagram be?

Please give us the clearest revised architecture you actually believe
after reading the docs.

Not the architecture that flatters the current direction.
The architecture that is most defensible.

We want:

- the main layers
- the stable seams
- what each level owns
- what is shared vs not shared
- where operators live
- where adapters live
- where the graph sits
- where the renderer sits
- what the performer is steering

Note: `05-stage-integration.md` proposes a specific topology
(engine emits DreamDirective → REST → FrameProcessor) and a
specific contract split (engine block for provenance, stage block
for rendering). Please evaluate whether that split is correct, and
whether the DreamDirective schema is the right seam or whether a
different contract boundary would be cleaner.

If the right answer is "the current reframe is one layer too ambitious,"
say that explicitly and redraw it narrower.

---

## Preferred Answer Shape

Ask GPT-5 Pro to answer in this format:

1. **Top-line judgment** — what the reframe gets right, wrong, and
   overstated
2. **Category errors / hidden contradictions**
3. **Revised architecture** — the cleanest version it actually believes
4. **What to change now vs later**
5. **One falsifiable next experiment**

If useful, ask for one revised ASCII diagram and one revised glossary.

---

## Why this note exists

The danger right now is not "lack of ideas."
The danger is accidentally building a bigger conceptual umbrella than the
project can currently support, then translating that ambiguity into the
implementation.

This question pack is meant to force sharper boundaries before that
happens.
