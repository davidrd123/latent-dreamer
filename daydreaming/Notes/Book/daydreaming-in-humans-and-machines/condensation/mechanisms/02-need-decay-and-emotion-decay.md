# Need Decay / Emotion Decay

## 1. Mechanism name

Need decay / emotion decay

## 2. Source anchors

- Chapter 7, `7.4.1 Emotion-Driven Control` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):485
- Chapter 3, `3.4 Emotional Feedback System` at [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):211
- Emotional feedback figures in [33-image-reviewed-chapter-3-emotion-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/33-image-reviewed-chapter-3-emotion-figures.md)
- Corresponding code: [control.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/control.clj) and Mueller's recovered `dd_cntrl.cl` factors as reflected there

## 3. Cognitive phenomenon (one line)

Affective fading without reset: needs and feelings linger across thought cycles but gradually lose force unless reinforced.

## 4. Kernel status (one line)

Implemented in [control.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/control.clj): needs decay at Mueller's recovered `0.98`, nonmotivating emotions decay at `0.95`, and weak emotions are garbage-collected below threshold.

## 5. Loop shape

This mechanism is the time-based maintenance pass inside control, distinct from the scheduler itself.

On each cycle:

1. multiply each need strength by the need-decay factor
2. multiply each nonmotivating emotion strength by the emotion-decay factor
3. leave motivating emotions alone so they continue to drive their concerns
4. remove emotions that fall below the garbage-collection threshold

State read:

- current need strengths
- current emotion strengths
- whether an emotion is currently motivating an active concern

State written:

- decayed need strengths
- decayed emotion strengths
- removal of weak emotions from the active emotional state

At the chapter level, decay counteracts the self-strengthening loops caused by emotion-congruent retrieval, emotion reactivation from episodes, and positive or negative daydreaming spirals.

## 6. Judgment points

None in Mueller's formulation. Decay is numeric maintenance, not a contextual decision.

## 7. Accumulation story

Decay is what makes motivation persistent without making it permanent. It gives the system temporal continuity across cycles while still allowing old pressures to fade if they are not refreshed by new outcomes, remindings, or active concerns.

This is not long-term memory accumulation. It is medium-term persistence control.

## 8. Property to preserve

Different kinds of state must have different persistence profiles. The system needs a way for motivations and emotions to outlast a single step, but not to remain indefinitely at full force. Without decay, emotional weather never clears; without persistence, the system has no temporal continuity.

## 9. Upstream triggers / downstream triggers

Upstream:

- existing needs and emotions from prior cycles
- reinforcing events such as episode reactivation, goal outcomes, and serendipity

Downstream:

- emotion-driven control through changed concern pressures
- activation or nonactivation of daydreaming goals
- forgetting of weak emotional residue

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: on each control cycle, needs and nonmotivating emotions decay, and emotions that become too weak are removed. This counterbalances the tendency of emotional state to reinforce itself through congruent retrieval and imagined outcomes.

**Candidate hybrid cut**: keep decay deterministic. A modern system might later stratify different persistence zones more explicitly, but that is still structural policy, not an LLM judgment problem.

## Interface shape (required)

**none**
