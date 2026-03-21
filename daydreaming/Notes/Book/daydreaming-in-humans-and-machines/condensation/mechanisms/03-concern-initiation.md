# Concern Initiation

## 1. Mechanism name

Concern initiation

## 2. Source anchors

- Chapter 7, `7.4.6 Inference Rule Application` and `7.4.7 Concern Initiation` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):623 and [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):661
- Chapter 2 overview of how new concerns inherit or create motivating emotions at [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):188 and [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):345
- Dependency figures in [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):13 and [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):70
- Appendix A examples: `REVENGE1`, `RATIONALIZATION1`, `ROVING1`, `COMPUTER-SERENDIPITY`
- Corresponding code: [goals.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goals.clj), [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj), [runner.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/runner.clj)

## 3. Cognitive phenomenon (one line)

The moment an inferred goal becomes its own stream of activity, with motivation attached and a context to run in.

## 4. Kernel status (one line)

Partially implemented: [goals.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goals.clj) can activate top-level goals and assign contexts, and [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj) activates some family-specific goals, but Mueller's general inference-rule-driven concern initiation and emotion binding are not yet recovered as one explicit mechanism.

## 5. Loop shape

This mechanism is triggered when inference rule application instantiates a new top-level goal.

Procedure:

1. create a new concern
2. inspect the emotion specification carried by the triggering inference rule
3. if that emotion is an already existing emotion, associate it with the new concern
4. otherwise instantiate a new emotion from the rule and bindings, then associate that new emotion with the concern
5. if the top-level goal is personal, attach the concern to the current reality context and assert the goal there
6. otherwise sprout a new context from reality, attach the concern to that new context, and assert the top-level goal there

State read:

- the inferred top-level goal
- the triggering inference rule
- rule bindings
- the current reality context
- any existing emotion referenced by the rule

State written:

- a new concern object
- a new or linked motivating emotion
- context linkage for the concern
- assertion of the top-level goal in the appropriate context

The central split is between personal goals, which stay tied to reality, and daydreaming goals, which begin in a sprouted hypothetical context.

## 6. Judgment points

Mueller's procedure is mostly structural, but one pressure point is obvious: when a rule specifies a motivating emotion, the strength is either inherited from an existing emotion or taken from a fixed intrinsic importance. That is a hard-coded motivation policy.

Possible modern judgment point:

- contextualize the initial motivating emotion for the new concern instead of taking a fixed programmer-set strength

This would be an evaluation step layered onto an otherwise structural concern creation path.

## 7. Accumulation story

This mechanism adds a new persistent concern to the system. It also adds or reuses a motivating emotion, and it anchors the concern to either reality or a new imaginary context. That changes future control cycles immediately, because the new concern now competes for processing time.

Unlike prompting alone, the new goal does not vanish after one response. It becomes a schedulable object with state, motivation, and a context pointer.

## 8. Property to preserve

A newly inferred goal must become an explicit, persistent concern object tied both to motivation and to a specific context of execution.

Without that property, inferred goals become disposable text instead of durable unfinished business.

## 9. Upstream triggers / downstream triggers

Upstream:

- inference rule application
- serendipity recognition when a verified serendipitous path creates a new plan or concern

Downstream:

- emotion-driven control
- planner
- later emotion generation and concern termination

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: concern initiation is the bridge from inference to control. A rule does not merely assert that a goal exists; it creates a new concern, associates one or more motivating emotions, and places the goal into reality or into a freshly sprouted imaginary context depending on whether the goal is personal or daydreaming.

**Candidate hybrid cut**: keep concern creation, context assignment, and concern registration structural. The likely hybrid cut is only in assigning the motivating emotion when the rule does not point to an already-existing one. That would be a narrow evaluation call, not a free-form generation step.

## Interface shape (required)

**tentative schema**

Integration pattern: **LLM-as-evaluator**

Input:

```clojure
{:inferred-goal {:goal-type keyword
                 :planning-type #{:real :imaginary}
                 :objective any}
 :triggering-rule {:id keyword
                   :emotion-template any}
 :bindings map
 :current-concern {:id keyword
                   :goal-type keyword
                   :strength number}
 :reality-context {:id keyword}}
```

Output:

```clojure
{:motivation {:mode #{:reuse-existing :create-new}
              :emotion-type keyword
              :strength number
              :justification string}}
```

If the system keeps Mueller's fixed intrinsic importances, this field collapses back to `none`.
