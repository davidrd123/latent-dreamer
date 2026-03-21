# Emotion-Driven Control

## 1. Mechanism name

Emotion-driven control

## 2. Source anchors

- Chapter 7, `7.4 Basic Procedures for Daydreaming` and `7.4.1 Emotion-Driven Control`, especially [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):483 and [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):487
- Chapter 2 discussion of concern competition and emotion-concern coupling at [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):180 and [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):188
- Figure dependencies in [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):13
- Appendix A examples where control shifts across concerns: `LOVERS1` ([20-image-reviewed-appendix-a-lovers1-batch-01.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/20-image-reviewed-appendix-a-lovers1-batch-01.md)), `REVENGE1` ([22-image-reviewed-appendix-a-revenge1.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/22-image-reviewed-appendix-a-revenge1.md))
- Corresponding code: [control.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/control.clj), [goals.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goals.clj)

## 3. Cognitive phenomenon (one line)

Mood- and interest-driven attentional arbitration: work on the concern that currently feels most pressing.

## 4. Kernel status (one line)

Partially implemented in [control.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/control.clj) and [goals.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goals.clj): the cycle, decay, goal selection, and mode oscillation exist, but Mueller's full concern/emotion ecology is still simplified.

## 5. Loop shape

This is the top scheduler. On startup, Mueller runs inference rule application once to get initial concerns off the ground. After that, each cycle:

1. decays needs and nonmotivating emotions
2. removes emotions below threshold
3. selects the concern with the highest emotional motivation
4. invokes the planner on that concern
5. repeats

Selection is mode-gated. In performance mode only personal-goal concerns are eligible. In daydreaming mode both personal-goal and daydreaming-goal concerns are eligible. If no concern is eligible in the current mode, the system flips modes, except that optional physical-object input can block the flip from daydreaming to performance.

State read:

- active concerns
- emotions associated with concerns
- current mode
- need strengths

State written:

- decayed need and emotion strengths
- removal of weak emotions
- current mode when a flip occurs
- the choice of which concern advances next

This loop does not itself plan, retrieve episodes, or mutate content. It is the narrow control skeleton that decides which already-existing concern gets the next unit of processing.

## 6. Judgment points

None inside Mueller's own loop. Given concern strengths and mode, the scheduler is deterministic.

The real judgment sits upstream in mechanisms that set or modify emotional strength. If those strengths become contextual rather than fixed, the control loop will feel different, but the loop itself does not need a soft decision.

## 7. Accumulation story

The loop updates persistent motivational state every cycle. Needs and nonmotivating emotions decay rather than resetting. Concerns remain in the pool until they terminate or lose enough motivation to be dropped. Because selection depends on decaying but persistent strengths, the system acquires temporal continuity: what happened earlier still biases what gets processed next.

This mechanism does not add long-term memory. Its accumulation is short- and medium-term control state.

## 8. Property to preserve

There must be one global arbitration point over multiple persistent concerns, driven by durable motivational state rather than by a fresh prompt-level choice on every step.

If that property is lost, the system stops feeling like one mind with competing unfinished business and becomes a sequence of unrelated local decisions.

## 9. Upstream triggers / downstream triggers

Upstream:

- concern initiation adds new concerns to the pool
- emotion generation changes concern motivation
- concern termination removes or resolves concerns

Downstream:

- planner
- mode switching between performance and daydreaming

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: DAYDREAMER repeatedly decays need and emotion strengths, selects the most highly motivated eligible concern, and runs the planner on it. Personal and daydreaming concerns share one scheduler, but eligibility depends on the current mode. Emotional motivation is the control currency that determines what receives thought next.

**Candidate hybrid cut**: Keep this mechanism structural. The hybrid cut should happen upstream in emotion generation or evaluation, not inside the scheduler. If a modern system wants richer prioritization, the natural move is to let an LLM help set or revise concern pressure and then feed the resulting typed strengths into this same deterministic loop.

## Interface shape (required)

**none** -- this mechanism is the structural consumer of motivational state, not a judgment call site.
