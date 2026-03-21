# Emotion Generation

## 1. Mechanism name

Emotion generation

## 2. Source anchors

- Chapter 3, `3.1 Emotional Responses in Daydreaming` at [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):17
- Chapter 3, `3.2 Emotion Representation` at [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):63
- Chapter 3, `3.4 Emotional Feedback System` at [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):211
- Chapter 7 notes in `Rule Application`, `Inference Rule Application`, and `Concern Termination` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):572, [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):619, and [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):547
- Emotional tables in [17-image-reviewed-tables.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/17-image-reviewed-tables.md)
- Corresponding code: no recovered general emotion-generation engine; partial emotion creation/diversion appears in [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj), [control.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/control.clj), and [director.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/director.clj)

## 3. Cognitive phenomenon (one line)

Appraisal becomes motive: goal outcomes, imagined or real, are converted into typed emotional pressure that changes what the mind does next.

## 4. Kernel status (one line)

Partial and fragmented: the kernel can store and decay emotions and create some family-specific hope/surprise-like effects, but it does not yet recover Mueller's general rule for generating emotions from real and imagined goal outcomes with alternative-past sign inversion, realism scaling, and typed labels.

## 5. Loop shape

Input: a goal outcome or other triggering event.

Core generation rule from Chapter 3:

1. determine whether the triggering outcome is a success or failure
2. determine whether the scenario is real, imagined future, or imagined alternative past
3. assess the relevant goal importance and scenario realism
4. compute emotion sign:
   - same sign as the goal outcome unless the scenario is an alternative past
   - for alternative pasts, invert the sign
5. compute strength proportional to realism times goal importance
6. assign representational features such as:
   - toward person or not
   - altern flag
   - active goal link
   - from-goal / to-goal links
7. attach the emotion to the appropriate concern or episode

Special cases named in Chapter 3:

- hope and worry for imagined future outcomes tied to active goals
- relief and regret for alternative past outcomes
- anger, humiliation, gratitude, and similar directed emotions when another person is implicated
- surprise upon fortuitous subgoal success or serendipitous discovery

State read:

- goal outcome and goal type
- scenario realism
- dynamic goal importance
- alternative-past flag
- causal role of another person if present

State written:

- new emotion objects
- links from emotions to concerns, goals, persons, and episodes
- changed concern motivation

## 6. Judgment points

This mechanism contains real appraisal judgments:

1. **Realism assessment**: Mueller needs a realism value before emotion strength can be set.
2. **Directedness/attribution**: deciding whether an emotion should be toward another person depends on causal interpretation.
3. **Specific affect naming**: the broad positive/negative sign is structural, but selecting a richer affect class can be contextual.

## 7. Accumulation story

Generated emotions persist across cycles, decay over time, motivate concerns, get attached to episodes, and are later reactivated by reminding. This is one of the main cross-cycle bridges in the architecture: an outcome now becomes a bias on later thought.

## 8. Property to preserve

Emotions must remain explicit typed state linked to goals, persons, and scenarios. The architecture depends on more than raw sentiment or valence; it needs structured motivational objects that can activate concerns, index episodes, decay, and be reactivated later.

## 9. Upstream triggers / downstream triggers

Upstream:

- inferred or directly observed goal outcomes
- imagined successes and failures during planning
- fortuitous subgoal success
- serendipity
- concern termination

Downstream:

- concern motivation
- daydreaming goal activation
- episode indexing and later emotional reactivation
- emotion decay and emotional feedback loops

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: emotions are generated from goal outcomes and related events according to goal importance, scenario realism, whether the scenario is an alternative past, and whether another person is implicated. The resulting emotion is represented as a structured data object whose fields affect later control, episode retrieval, and daydreaming-goal activation.

**Candidate hybrid cut**: keep the typed emotion object, concern linkage, and sign/strength bookkeeping structural. The most plausible hybrid cuts are appraisal steps: realism estimation, attribution of responsibility, and richer affect classification.

## Interface shape (required)

**tentative schema**

Integration patterns:

- **LLM-as-evaluator** for realism and attribution
- **Co-routine judgment** for richer affect classification

Input:

```clojure
{:event {:type #{:goal-success :goal-failure :serendipity :fortuitous-success}
         :goal any
         :scenario-type #{:real :imagined-future :alternative-past}
         :realism number
         :importance number
         :caused-by any}}
```

Output:

```clojure
{:emotion {:valence #{:positive :negative}
           :affect keyword
           :strength number
           :toward any
           :altern? boolean
           :from-goal any
           :to-goal any}
 :reason string}
```
