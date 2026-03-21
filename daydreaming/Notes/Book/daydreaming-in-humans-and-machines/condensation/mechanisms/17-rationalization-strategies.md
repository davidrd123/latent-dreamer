# Rationalization Strategies

## 1. Mechanism name

Rationalization strategies

## 2. Source anchors

- Chapter 3, `3.5 Rationalization` at [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):264
- Chapter 3 rationalization subsections at [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):280, [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):311, [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):326, and [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):336
- Emotional feedback discussion in [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):211
- Figure 3.3 in [33-image-reviewed-chapter-3-emotion-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/33-image-reviewed-chapter-3-emotion-figures.md)
- Chapter 7 note that some daydreaming-goal planning consequents are procedural code at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):617
- Corresponding code: partial bounded bridge in [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj)

## 3. Cognitive phenomenon (one line)

Cognitive dissonance reduction through scenario-building and reinterpretation.

## 4. Kernel status (one line)

Partial but narrow: [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj) has stored rationalization frames and emotion diversion, but not Mueller's full dynamic set of mixed blessing, hidden blessing, external attribution, and minimization strategies.

## 5. Loop shape

Input: an active `RATIONALIZATION` goal tied to a failed goal and a negative motivating emotion.

The strategy family includes four methods:

1. **Mixed blessing**
   - imagine the failed goal succeeding in an alternative past
   - try to derive a new failure from that imagined success
   - if the alternative still leads somewhere bad, relief reduces the original negative emotion

2. **Hidden blessing**
   - start from the real failure
   - derive a real or imagined success from it
   - if a positive outcome follows, the positive emotion offsets the original negative emotion

3. **External attribution**
   - use unsuccessful `REVERSAL` or other causal reinterpretation to conclude that the failure would have happened anyway or was caused externally
   - the failure is re-read as less self-caused

4. **Minimization**
   - inspect the antecedents that led to the failure emotion
   - try to negate or weaken those antecedents by generating a reasoning chain
   - if the negating chain has strength `s'`, weaken the original antecedent strength `s` by `s / (s + s')`

Across all four, positive or relief-type side-effect emotions are diverted back into the main motivating emotion of the rationalization concern, reducing the original negativity.

State read:

- failed goal and associated negative emotion
- inference chain or causal antecedents of the failure
- available rules and episodes for constructing reframe scenarios

State written:

- modified emotional strength on the original failure
- new imagined branches and their side-effect emotions
- reappraised emotional state attached to the episode or concern

## 6. Judgment points

This mechanism is saturated with judgment points:

1. **Strategy choice**: which rationalization style fits the current failure?
2. **Content generation**: what hidden blessing, mixed blessing, or minimizing chain is actually plausible here?
3. **Backfire evaluation**: some rationalizations intensify the negative emotion instead of reducing it.
4. **Belief-system coherence**: Mueller notes that minimization may create attitudes that should be rejected as dissonant, but his current implementation cannot do that.

## 7. Accumulation story

Rationalization changes more than the current mood. In Mueller's framing, it can reappraise an episode so that the next retrieval activates a less negative emotion. That makes it one of the clearest long-term emotion-modification mechanisms in the architecture, not just a momentary consolation.

## 8. Property to preserve

Rationalization must remain explicit reinterpretation over structured causes and outcomes, not a free-floating positivity layer. The system needs to know what failure is being re-read, by which strategy, and how the reappraisal changed emotional strength.

## 9. Upstream triggers / downstream triggers

Upstream:

- daydreaming goal activation for `RATIONALIZATION`
- failed-goal emotion and its causal chain

Downstream:

- emotion generation and diversion
- modified episode affect on later retrieval
- possible revenge, roving, or other emotional-goal competition depending on the new affective state

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: DAYDREAMER handles rationalization through several explicit scenario-building or reinterpretation strategies. Mixed blessing and hidden blessing generate new imagined consequences; external attribution reframes causation; minimization weakens antecedents of the failure emotion. Successful rationalizations reduce the strength of the original negative emotion, while failed ones can make it worse.

**Candidate hybrid cut**: keep goal/episode linkage, emotional-strength bookkeeping, and strategy identity structural. The natural hybrid move is to let an LLM generate or evaluate the actual reframe content, because the creative burden of finding a convincing hidden blessing or minimization chain is precisely where Mueller's rigid rules are thinnest.

## Interface shape (required)

**tentative schema**

Integration patterns:

- **LLM-as-content-generator** for reframing scenarios
- **LLM-as-evaluator** for plausibility and backfire risk

Input:

```clojure
{:failed-goal {:id keyword
               :objective any}
 :trigger-emotion {:affect keyword
                   :strength number}
 :strategy #{:mixed-blessing :hidden-blessing :external-attribution :minimization}
 :antecedents [any]
 :retrieved-episodes [any]}
```

Output:

```clojure
{:reframe {:strategy keyword
           :scenario any
           :negated-antecedents [any]
           :reason string}
 :emotion-delta {:new-strength number
                 :valence-shift number}
 :plausibility {:score number
                :backfire-risk number}}
```
