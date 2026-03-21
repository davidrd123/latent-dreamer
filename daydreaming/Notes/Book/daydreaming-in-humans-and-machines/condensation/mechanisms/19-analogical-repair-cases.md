# Analogical Repair Cases

## 1. Mechanism name

Analogical repair cases

## 2. Source anchors

- Chapter 4, `4.1.3 Analogical Planning` at [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):61
- Chapter 4 learning-goal sections at [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):244, [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):318, and [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):342
- Analogical examples in [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):93 and [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):118
- Image-reviewed Chapter 4 figures in [34-image-reviewed-chapter-4-learning-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/34-image-reviewed-chapter-4-learning-figures.md)
- Corresponding code: partial `REVERSAL` family recovery in [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj); the broader four-case family is not recovered

## 3. Cognitive phenomenon (one line)

Structured counterfactual and anticipatory repair: use prior plans plus targeted rebuilding to learn from failures and pre-explore future action.

## 4. Kernel status (one line)

Partial and uneven: the kernel has a meaningful first pass on `REVERSAL`, but `RECOVERY`, `REHEARSAL`, and `REPERCUSSIONS` are not yet recovered as general analogical repair/use families.

## 5. Loop shape

This card treats the inventory's "four repair cases" as the four higher-level learning uses of analogical planning named in Chapters 3 and 4:

1. **REVERSAL**
   - start from a real or imagined failure
   - generate an alternative past or preventable future variant
   - repair the failed plan using `UNDO-CAUSES`, `EXPAND-LEAVES`, or `EXPAND-ALTERNATIVES`

2. **RECOVERY**
   - start from a real failure
   - generate future scenarios that achieve the same goal later
   - use retrieved episodes analogically to construct recovery plans

3. **REHEARSAL**
   - start from an active personal goal not yet performed
   - generate future completions before acting
   - let imagined failures trigger later reversal or adjustment

4. **REPERCUSSIONS**
   - start from a hypothetical future situation
   - propagate its consequences to the self
   - let any resulting failures activate further repair goals such as `REVERSAL`

What these share:

- they all use analogical planning as a structural scaffold
- they all orient planning around a specific temporal or causal relation to a failure/success
- they all rely on targeted repair rather than generic unconstrained fantasy

State read:

- current failure or active goal situation
- retrieved episodes
- inference chains and branch contexts
- goal family and temporal orientation

State written:

- repaired or pre-explored planning trees
- new episodes
- sometimes new rules, especially under `REVERSAL`
- new emotions such as regret, relief, hope, or worry

## 6. Judgment points

The structural case identity is clear, but there are contextual choices inside it:

1. **Which branch or failed leaf to repair first**
2. **Whether a repaired scenario is genuinely useful enough to keep**
3. **How much departure from the source episode still counts as the same useful analogy**

## 7. Accumulation story

This family is one of the main engines of learning in Mueller. It produces repaired episodes, future rehearsal episodes, and sometimes new rules. These artifacts alter future retrieval and future planning. That is why the mechanism matters: it does not just simulate possibilities, it restructures future capability.

## 8. Property to preserve

The system must preserve explicit repair orientation. `REVERSAL`, `RECOVERY`, `REHEARSAL`, and `REPERCUSSIONS` are not just content themes; they define different typed relations between source problem, target scenario, and acceptable repair. Without those distinctions, analogical planning becomes generic brainstorming and loses Mueller's learning structure.

## 9. Upstream triggers / downstream triggers

Upstream:

- daydreaming goal activation
- failed goals, halted goals, or hypothetical situations
- retrieved analogical episodes

Downstream:

- analogical rule application
- episode evaluation and storage
- rule creation under `REVERSAL`
- new emotions and later concern competition

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: the learning daydreaming goals are not interchangeable. Each one orients analogical planning differently: reversing a failure, recovering later, rehearsing before action, or exploring repercussions. The repair work is explicit and strategy-bound, not just "generate another scenario." These mechanisms are how DAYDREAMER uses analogical planning to learn from both real and imagined experience.

**Candidate hybrid cut**: keep the family identity, temporal orientation, explicit branch/leaf repair, and any resulting rule or episode creation structural. The hybrid opportunity is in evaluating which repair target is most promising and whether the repaired scenario is worth keeping, not in replacing the strategy family with generic model improvisation.

## Interface shape (required)

**tentative schema**

Integration patterns:

- **LLM-as-evaluator** for repair-target choice and utility

Input:

```clojure
{:family #{:reversal :recovery :rehearsal :repercussions}
 :trigger {:goal any
           :failure any
           :context-id keyword}
 :candidate-episodes [any]
 :candidate-repairs [any]}
```

Output:

```clojure
{:repair-ordering [{:candidate any
                    :score number
                    :reason string}]
 :keep-repaired-scenario? boolean}
```
