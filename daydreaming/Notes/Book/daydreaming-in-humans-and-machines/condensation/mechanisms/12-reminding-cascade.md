# Reminding Cascade

## 1. Mechanism name

Reminding cascade

## 2. Source anchors

- Chapter 7, `7.5.3 Reminding` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):726
- Chapter 2 compressed description at [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):364
- Dependency edges in [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):70
- Chapter 5 discussion of episode-driven and object-driven serendipity at [05-everyday-creativity-in-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/05-everyday-creativity-in-daydreaming.md):97 and [05-everyday-creativity-in-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/05-everyday-creativity-in-daydreaming.md):168
- Appendix A examples: `ROVING1`, `COMPUTER-SERENDIPITY`
- Corresponding code: [episodic_memory.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/episodic_memory.clj)

## 3. Cognitive phenomenon (one line)

Associative drift: one recalled episode lights up more cues, which recall more episodes, which can suddenly make a new plan visible.

## 4. Kernel status (one line)

Substantially implemented in [episodic_memory.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/episodic_memory.clj): recent-index and recent-episode FIFOs, recursive reminding, and cycle-avoidance are already present.

## 5. Loop shape

Mueller maintains two short global recency structures:

- recent indices (currently at most 6)
- recent episodes (currently at most 4)

Given a retrieved episode:

1. if it is already recent, exit
2. add it to recent episodes, dropping the oldest if necessary
3. reactivate emotions associated with the episode
4. for each index of the episode not already in recent indices:
   - add the index to recent indices
   - invoke episode retrieval on that index
   - recursively invoke reminding on each newly retrieved episode
5. unless this reminding itself came from object-driven serendipity, invoke serendipity recognition on the episode for all concerns

This procedure turns one retrieval into a bounded associative stream rather than a single recall event.

State read:

- the seed episode and its indices
- recent indices
- recent episodes
- emotions associated with the episode

State written:

- updated recent-episode FIFO
- updated recent-index FIFO
- reactivated emotions
- recursively retrieved additional episodes

## 6. Judgment points

Mueller's cascade is mostly structural. The main hybrid pressure point is not "which episode do we retrieve?" but "which reminded episode is actually relevant enough to keep pushing into current cognition?" Mueller handles that by strict recency bounds and exact indices.

Possible modern judgment point:

- evaluate whether a newly reminded episode should meaningfully redirect the current concern instead of merely being present in the associative stream

## 7. Accumulation story

This mechanism writes short-horizon associative state. The recent-index and recent-episode lists persist across the immediate stream of thought, so a retrieval changes what will be recalled next. It also reactivates emotions and hands episodes to serendipity recognition, which can create new plans and new concerns. That makes reminding one of the main bridges from stored memory to newly generated behavior.

Unlike a one-shot prompt, the cascade changes the next retrieval opportunity every time it runs.

## 8. Property to preserve

The system must preserve explicit propagation of individual matched indices through a bounded recency structure.

That is what makes reminding a cascade rather than a one-off similarity lookup.

## 9. Upstream triggers / downstream triggers

Upstream:

- episode retrieval
- object-driven input that seeds recent indices

Downstream:

- episode retrieval on new indices
- emotion reactivation
- serendipity recognition

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: reminding activates the other indices of a just-retrieved episode, recursively retrieves further episodes using those indices, reactivates emotions associated with those episodes, and invokes serendipity recognition on the resulting salient episode stream. It is a bounded associative chain, not an unconstrained spread.

**Candidate hybrid cut**: keep the cascade structure and explicit recency stores structural. The most plausible hybrid cut is only after an episode has been reminded: use an evaluator to decide whether a reminded episode deserves stronger attentional promotion, richer summarization, or downstream scenario generation. Do not replace explicit index propagation with opaque similarity alone.

## Interface shape (required)

**tentative schema**

Integration pattern: **LLM-as-evaluator**

Input:

```clojure
{:seed-episode {:id keyword
                :summary string
                :indices [any]
                :emotions [any]}
 :reminded-episode {:id keyword
                    :summary string
                    :indices [any]
                    :emotions [any]}
 :current-concerns [{:id keyword
                     :goal-type keyword
                     :strength number}]
 :recent-state {:recent-indices [any]
                :recent-episodes [keyword]}}
```

Output:

```clojure
{:promote? boolean
 :relevance number
 :reason string}
```

If the system keeps Mueller's strict structural cascade only, this field collapses back to `none`.
