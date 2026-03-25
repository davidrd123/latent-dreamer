# Learning Curriculum

Purpose: a practical order for learning this repo without drowning in
benchmark detail or scattered notes.

Last updated: 2026-03-24

---

## The order

### 1. Learn the control plane first

Read these before reading code:

- `current-sprint.md`
- `steering-balance-2026-03-24.md`
- `dashboard.md`
- `canonical-map.md`

Goal: know what is active, what is strategic pressure, what is parked,
and which artifacts are authoritative.

### 2. Learn the core control substrate

Read these next:

- `kernel/src/daydreamer/control.clj`
- `kernel/src/daydreamer/rules.clj`
- `kernel/src/daydreamer/goal_family_rules.clj`
- `kernel/src/daydreamer/goal_families.clj`

Goal: understand concern competition, family activation, plan execution,
and where the rule engine stops versus where family logic still owns the
behavior.

### 3. Learn the memory membrane in depth

Read:

- `kernel/src/daydreamer/episodic_memory.clj`
- `kernel/src/daydreamer/trace.clj`

Goal: understand the repo's real differentiator:

- retrieval by discrete named indices
- use / outcome tracking
- promotion and durability
- rule-access movement
- lifecycle visibility in traces

### 4. Then read one benchmark end to end

Read:

- `kernel/src/daydreamer/benchmarks/graffito_miniworld.clj`

Read it as an assay of the kernel, not as the kernel itself.

Question to keep asking:

> is this reusable substrate, or benchmark-local scenery?

### 5. Then learn the LLM edges

Read:

- `kernel/src/daydreamer/runtime_thought.clj`
- `kernel/src/daydreamer/director.clj`
- `kernel/src/daydreamer/family_evaluator.clj`

Goal: understand what the repo is and is not currently outsourcing to
LLMs.

---

## What to understand deeply first

If depth is limited, go deepest on:

1. `episodic_memory.clj` — the membrane is the piece most likely to
   need adaptation for epistemic objects. Understand admission tiers,
   promotion evidence, anti-residue flags, cue zone separation, and
   what "cross-family use" concretely means. Also understand the
   retrieval path: how coincidence-mark counting works over named
   indices, what makes something retrievable, how cue zones separate
   content from provenance.

2. `control.clj` — specifically the concern entry surface: how concerns
   enter, how `emotion-decay` works, what `most-highly-motivated-goals`
   computes. This is where the LLM proposer plugs in.

3. `goal_families.clj` — focus on the activation layer (how candidates
   are found and scored), not the plan body implementations. The
   activation layer is the part that generalizes; the plan bodies are
   family-specific.

4. `graffito_miniworld.clj` — read as an illustration of the full flow
   from "something wants attention" through family execution to "an
   episode gets stored with these indices." Trace one cycle end to end.
   The question at every step: where could an LLM proposer enter, and
   where does the kernel take over?

That order gives the right mental model for the pivot direction:
membrane first (what persists and why), then competition (what gets
attention), then activation (how content enters), then the full flow.

---

## What not to do first

Do not start by trying to understand:

- every benchmark
- every note
- every Python script
- every LLM-facing path

Do not mistake benchmark-local world logic for reusable kernel
mechanism.

---

## The current learning question

The important distinction to keep in mind while learning is:

- **control substrate**: kernel mechanisms that should generalize
- **knowledge-entry layer**: content-entry seams that may later be LLM-
  proposed
- **benchmark scenery**: Graffito-specific world math and ecology

If a piece of code feels overly specific, the right question is usually
not "why is this bad?" but "which layer is this?"

### Pivot-readiness questions to carry while reading

As you learn each piece, also ask:

- **Membrane**: what changes if the objects are claims/questions instead
  of episodes? What does promotion evidence mean for an epistemic
  object? Does cross-family use still make sense, or does it need a
  parallel evidence channel (e.g. human uptake)?
- **Competition**: what changes if concern sources expand beyond failed
  goals + negative emotion to include contradiction, curiosity, social
  debt, aesthetic pull?
- **Retrieval**: what are the indices for epistemic objects? Can
  coincidence-mark counting work on claims the way it works on scene
  episodes?
- **Activation**: the Graffito `compute-candidates` is hand-authored.
  What would an LLM-proposed concern need to look like to enter the
  same competition path?

These questions are not tasks. They are a lens on the current code that
builds toward the first pivot experiment.
