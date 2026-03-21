# Episode Storage

## 1. Mechanism name

Episode storage

## 2. Source anchors

- Chapter 7, `7.5.1 Episode Storage` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):696
- Chapter 4 background on indexing episodes under rules and subgoals at [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):48
- Figure 4.2 reconstruction in [34-image-reviewed-chapter-4-learning-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/34-image-reviewed-chapter-4-learning-figures.md):26
- Figure 7.4 dependency `Concern termination -> Episode storage` in [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):56
- Appendix A examples where successful daydreams later reappear analogically: `REVENGE1`, `REVENGE3`, `COMPUTER-SERENDIPITY`
- Corresponding code: [episodic_memory.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/episodic_memory.clj)

## 3. Cognitive phenomenon (one line)

Turn a completed real or imagined experience into reusable memory indexed for later recall.

## 4. Kernel status (one line)

Partially implemented in [episodic_memory.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/episodic_memory.clj): index linkage exists, but Mueller's automatic subtree decomposition, context-derived surface indexing, and concern-termination write path are not all yet recovered.

## 5. Loop shape

Given an episode and an index:

1. intern the index against a global list of unique indices, using equality or unification
2. if no equivalent index exists, add the new index to that global list
3. create a link from the interned index to the episode

Invocation sites:

- after top-level success, store the whole planning tree and also the subtrees rooted at each descendant subgoal
- during initialization, store hand-coded episodes under programmer-supplied indices

Storage is not one episode to one index. The same planning experience is decomposed into reusable fragments and linked under both rule indices and surface-feature indices such as persons, objects, and locations. Realism and desirability are stored along with the episodes.

State read:

- completed planning tree
- rule roots and descendant subgoals
- surface features of the planning context
- global unique-index list

State written:

- episode objects
- index links
- global unique-index list
- stored realism and desirability scores

## 6. Judgment points

The storage mechanics are structural, but two judgment-adjacent places exist:

1. deriving additional non-rule indices from the planning context can be more or less literal
2. realism and desirability are attached at storage time, but those evaluations come from another mechanism and may be contextualized in a hybrid design

## 7. Accumulation story

This is one of the main long-term accumulation mechanisms. Episodic memory grows. The unique-index list grows. The same experience becomes retrievable later both as a whole episode and as smaller subepisodes rooted under descendant goals. Because the stored object carries evaluation data, later planning is shaped not only by what happened before but also by how the system judged it.

## 8. Property to preserve

Stored experience must remain decomposable, multiply indexed, and reusable as a planning fragment, not just as a narrative blob.

That is the property that lets later retrieval feed analogical planning and serendipity rather than just produce reminiscence.

## 9. Upstream triggers / downstream triggers

Upstream:

- concern termination
- program initialization
- evaluation metrics that supply realism and desirability

Downstream:

- episode retrieval
- reminding cascade
- analogical rule application
- serendipity recognition when newly stored material becomes available

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: DAYDREAMER stores completed real and imagined planning trees under interned indices. It stores not only the top-level episode but also subepisodes rooted at descendant subgoals. The indexing scheme mixes rule-rooted retrieval with surface-feature retrieval, and realism/desirability are saved with the episode.

**Candidate hybrid cut**: keep storage structural. The hybrid opportunities are not in the link creation itself but in index generation and evaluation attachment. A modern system may let an LLM propose extra semantic indices or richer summaries, but those should still be stored as explicit searchable fields under a persistent episode object.

## Interface shape (required)

**tentative schema**

Integration pattern: **LLM-as-content-generator** for optional extra semantic indexing

Input:

```clojure
{:episode {:id keyword
           :planning-tree any
           :context-summary string
           :entities [any]
           :rule-roots [keyword]}
 :evaluation {:realism number
              :desirability number}}
```

Output:

```clojure
{:extra-indices [any]
 :episode-summary string
 :salient-entities [any]}
```

If the system sticks to Mueller's literal indexing only, this collapses back to `none`.
