# Episode Evaluation

## 1. Mechanism name

Episode evaluation

## 2. Source anchors

- Chapter 4, `4.4.1 Daydream Evaluation Metrics` at [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):356
- Chapter 4, `4.4.2 Daydream Selection` at [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):387
- Table 4.1 ordering example at [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):378 and [17-image-reviewed-tables.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/17-image-reviewed-tables.md)
- Chapter 7 episode-storage note at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):696
- Chapter 3 realism discussion at [03-emotions-and-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/03-emotions-and-daydreaming.md):57
- Corresponding code: metadata fields in [episodic_memory.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/episodic_memory.clj) and provisional ranking scaffolds in [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj)

## 3. Cognitive phenomenon (one line)

Private appraisal: deciding which imagined or remembered scenarios feel plausible enough and desirable enough to keep, pursue, or ignore.

## 4. Kernel status (one line)

Partial data support only: [episodic_memory.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/episodic_memory.clj) stores realism/desirability fields, but the kernel does not yet recover Mueller's general realism, desirability, similarity, thresholding, and goal-deactivation logic.

## 5. Loop shape

Episode evaluation happens at three different moments:

1. **During production**: maintain a running realism estimate for the current branch; if realism falls below the threshold for the concern type, abandon the branch.
2. **After production**: compute final desirability and realism for the completed daydream or external plan and store them with the episode.
3. **During application**: when multiple episodes are retrieved for a current target goal, compute similarity between source and target, combine similarity with stored realism and desirability, and order candidate episodes according to concern type.

Metric definitions:

- desirability = sum of importances of personal goals activated or resolved after the concern begins, with successes positive and failures/active goals negative
- realism = a value on `[0,1]` based on the plausibilities of the rules used in the scenario
- similarity = structural comparison between source and target goal objectives

Selection details:

- personal-goal and learning-goal concerns use the product of desirability, realism, and similarity, gated by thresholds
- daydreaming-goal concerns use similarity alone, gated by threshold
- if all retrieved daydream attempts for a goal are low or negative in desirability, the active goal is deactivated

State read:

- planning tree and used-rule plausibilities
- goal importances and outcomes
- current concern type
- retrieved episode metadata
- target-goal structure

State written:

- realism and desirability attached to stored episodes
- candidate episode ordering during retrieval/application
- branch abandonment during planning
- possible deactivation of the current goal

## 6. Judgment points

This is one of the strongest hybrid-cut mechanisms in the whole architecture:

1. **Realism assessment**: Mueller computes realism from rule plausibilities, but contextual plausibility is an obvious modern LLM-evaluator slot.
2. **Desirability assessment**: the bookkeeping is structural, but the meaning of outcomes for multiple interacting goals can be more nuanced than fixed programmer-assigned importances.
3. **Similarity assessment**: Mueller uses structural goal comparison; a modern system may want a richer context-sensitive similarity judgment while preserving explicit reasons.
4. **Goal deactivation**: deciding that a goal is simply a bad idea may benefit from a richer review of retrieved alternatives than a pure numeric cutoff.

## 7. Accumulation story

Episode evaluation is the mechanism that turns raw daydream output into reusable memory policy. Once realism and desirability are stored with an episode, future retrieval and analogical planning are shaped by those values. The system is not just remembering what happened; it is remembering how good and how plausible the episode was.

That accumulated evaluative layer is also what lets the system stop repeating bad plans and eventually deactivate bad courses of action.

## 8. Property to preserve

Evaluation must remain explicit, inspectable metadata attached to episodes and branches. Even if the scoring becomes more contextual, the system still needs reusable stored judgments rather than opaque one-shot rankings. Future selection depends on knowing why an episode was favored or discarded.

## 9. Upstream triggers / downstream triggers

Upstream:

- branch expansion during planning
- completed daydream or performance-mode episodes
- episode retrieval during analogical planning

Downstream:

- branch pruning
- episode storage metadata
- retrieved-episode ordering
- goal deactivation when all alternatives look bad

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: episode evaluation combines three explicit metrics. Realism and desirability are stored with episodes after generation; similarity is computed when an episode is considered for a current target. The ordering of retrieved episodes depends on concern type, and very unrealistic or undesirable candidates may never be used. Evaluation also operates online: low-realism branches can be abandoned before a scenario completes.

**Candidate hybrid cut**: keep thresholds, ordering logic, stored metadata, and goal-deactivation consequences structural. But this is one of the clearest places for LLM evaluation: realism, desirability, and target-situation similarity are all judgments Mueller had to hard-code numerically and which a modern system may assess more contextually, provided the results are still stored as explicit fields.

## Interface shape (required)

**tentative schema**

Integration patterns:

- **LLM-as-evaluator** for realism, desirability, and similarity

Input:

```clojure
{:episode {:id keyword
           :planning-tree any
           :used-rules [{:id keyword :plausibility number}]
           :goal-outcomes [any]
           :stored-realism number
           :stored-desirability number}
 :target-goal {:objective any
               :concern-type keyword}
 :evaluation-stage #{:during-production :after-production :during-application}}
```

Output:

```clojure
{:realism {:score number
           :reason string}
 :desirability {:score number
                :reason string}
 :similarity {:score number
              :reason string}
 :abandon-branch? boolean
 :deactivate-goal? boolean}
```
