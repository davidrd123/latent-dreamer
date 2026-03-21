# Daydreaming Goal Activation

## 1. Mechanism name

Daydreaming goal activation

## 2. Source anchors

- Chapter 3, `3.3 Daydreaming Goals` at [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):151
- Chapter 3, `3.4 Emotional Feedback System` at [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):211
- Table 3.3 and Figure 3.1 in [17-image-reviewed-tables.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/17-image-reviewed-tables.md) and [33-image-reviewed-chapter-3-emotion-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/33-image-reviewed-chapter-3-emotion-figures.md)
- Chapter 4 learning-goal activations at [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):244, [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):318, and [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):342
- Chapter 7, `7.3.4 Rules for Concern Initiation` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):438
- Corresponding code: partial family activation in [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj)

## 3. Cognitive phenomenon (one line)

Affect-to-strategy conversion: certain feelings and heuristics automatically launch characteristic styles of daydreaming.

## 4. Kernel status (one line)

Partial: [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj) activates `:reversal`, `:rationalization`, and `:roving` from current world state, but the full DAYDREAMER family activation set and generic theme-rule machinery are not yet recovered.

## 5. Loop shape

Input: current emotional state plus a few nonemotional heuristics.

Procedure:

1. inspect active emotions and current concern state
2. when a triggering condition of sufficient strength is present, instantiate a daydreaming-goal theme rule
3. create a new top-level daydreaming goal and associated concern
4. attach the triggering emotion as the motivating emotion of that concern
5. sprout an imaginary planning context for the new concern

Canonical activations from Chapter 3:

- `RATIONALIZATION`, `ROVING`, `REVERSAL`, and `RECOVERY` from negative emotion
- `REVENGE` from directed negative emotion
- `REHEARSAL` from positive interest attached to an active personal goal
- `REPERCUSSIONS` from heuristics rather than an emotion trigger

State read:

- active emotions and their strengths
- whether an emotion is directed toward someone
- active personal goals and their status
- heuristic triggers for repercussions

State written:

- new daydreaming-goal concerns
- motivating-emotion links
- sprouted imaginary contexts for those concerns

## 6. Judgment points

Six of the seven canonical activation families are structurally specified enough to stay deterministic: negative or directed-negative emotion activates `RATIONALIZATION`, `ROVING`, `REVERSAL`, `RECOVERY`, and `REVENGE`; positive interest attached to an active personal goal activates `REHEARSAL`.

The only weakly specified pressure point is:

1. **Repercussions heuristics**: Chapter 3 says `REPERCUSSIONS` is activated by a collection of heuristics rather than by emotions, but those heuristics are not given at the same procedural precision as the emotion-triggered families.

## 7. Accumulation story

Activation turns emotional residue into new work. Once a daydreaming goal is activated, it becomes a persistent concern with its own motivational pressure and context lineage. That concern can then generate episodes, new emotions, and even new rules, depending on the family.

## 8. Property to preserve

The system must preserve explicit strategy families between emotion and planning. Negative or positive affect should not jump directly to unconstrained generation; it should activate typed daydreaming goals that determine what kind of scenario-building is now appropriate.

## 9. Upstream triggers / downstream triggers

Upstream:

- emotion generation
- current active personal goals
- heuristics for hypothetical future situations

Downstream:

- concern initiation
- emotion-driven control through the new concern's motivation
- planning under a family-specific strategy

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: daydreaming goals are a fixed set of top-level heuristic strategies. They are activated by specific emotional conditions or heuristics, and the triggering emotion becomes the motivating force for the resulting concern. Emotional and learning daydreaming goals differ by function, but both enter the same concern economy once activated.

**Candidate hybrid cut**: keep family activation structural and typed. The only place a modern system may need contextual judgment is around vague heuristic activations such as `REPERCUSSIONS`, but even there the output should still be a concrete daydreaming-goal family rather than an unconstrained prompt.

## Interface shape (required)

**unclear**

Six of the seven family activations do not need an interface here; they are already structural threshold rules. The unresolved part is `REPERCUSSIONS` alone, whose heuristics are underspecified in the book.

If modernized, the likely interface would be a narrow evaluator over a hypothetical situation that returns whether `:repercussions` should activate and why, but the source material does not yet pin down that input shape tightly enough to treat it as stable.
