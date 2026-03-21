# Concern Termination

## 1. Mechanism name

Concern termination

## 2. Source anchors

- Chapter 7, `7.4.3 Concern Termination` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):547
- Chapter 2 compressed overview at [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):300
- Procedure dependencies in [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):56
- Appendix A examples: `LOVERS1`, `FOOD1`, `REVENGE1`
- Corresponding code: partial concern cleanup in [control.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/control.clj) and goal bookkeeping in [goals.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goals.clj)

## 3. Cognitive phenomenon (one line)

Closure: when a line of thought or activity succeeds or fails, it leaves an emotional residue, updates reality, and then stops occupying attention.

## 4. Kernel status (one line)

Partial: [control.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/control.clj) can terminate top-level goals and record status, but Mueller's full concern-termination bundle still lacks the coupled response-emotion creation, garbage collection semantics, and episode-storage handoff.

## 5. Loop shape

Input: a concern plus a termination status.

Procedure:

1. destroy emotions associated only with the concern
2. create a new positive or negative response emotion whose strength equals the dynamic importance of the concern's top-level goal
3. if the concern succeeded, assert the top-level objective in the reality context
4. if it failed, mark the top-level goal failed
5. garbage-collect any remaining planning structure for the concern
6. destroy the concern itself

State read:

- the concern and its top-level goal
- concern-linked emotions
- the concern's planning structure and current/root contexts
- reality context

State written:

- response emotion
- reality-context assertion on success
- failed-goal status on failure
- removal of concern-local planning residue
- removal of the concern from the active concern set

Concern termination also triggers episode storage in the revised Chapter 7 dependency graph, so it is the point where a completed or failed run becomes long-term episodic material.

## 6. Judgment points

None in the strong sense. Mueller treats concern termination as bookkeeping plus fixed emotional outcome. The richer judgment question of exactly what emotion should result from a goal outcome belongs in the separate emotion-generation mechanism, not here.

## 7. Accumulation story

Concern termination is one of the main conversion points from active processing into durable aftermath. It removes a concern from the runnable set, but in doing so it can:

- change the persistent emotional state
- change the reality context
- trigger episode storage of the finished planning structure

So the concern disappears, but its outcome becomes part of the world model and memory.

## 8. Property to preserve

Termination must remain an explicit transition from active concern state into durable aftermath. Success or failure cannot just vanish into text generation; the system has to know that a concern ended, why it ended, and what residue it leaves behind.

## 9. Upstream triggers / downstream triggers

Upstream:

- planner when a top-level goal succeeds
- planner when all alternatives are exhausted and the concern fails

Downstream:

- episode storage
- emotion generation / emotional state update
- removal from the active concern pool, which changes later control selection

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: concern termination is a fixed cleanup-and-outcome procedure. It removes the concern's local emotional and planning residue, creates a response emotion keyed to goal success or failure, updates reality when appropriate, and then destroys the concern. It is also the point from which episode storage is invoked once episodic memory is added.

**Candidate hybrid cut**: keep concern termination structural. If emotion labeling later becomes richer, that should be handled in emotion generation, not by turning concern termination itself into an opaque judgment call.

## Interface shape (required)

**none**
