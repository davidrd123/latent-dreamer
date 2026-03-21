# Episode Retrieval

## 1. Mechanism name

Episode retrieval (index-threshold)

## 2. Source anchors

- Chapter 7, `7.5.2 Episode Retrieval` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):708
- Chapter 2 condensed description at [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):360
- Chapter 4 discussion of rule-rooted episode indexing at [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):48
- Figure 4.2 reconstruction in [34-image-reviewed-chapter-4-learning-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/34-image-reviewed-chapter-4-learning-figures.md):26
- Appendix A examples: `REVENGE3`, `RECOVERY3`, `COMPUTER-SERENDIPITY`
- Corresponding code: [episodic_memory.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/episodic_memory.clj)

## 3. Cognitive phenomenon (one line)

Recall an experience when enough of its cues coincide with what is currently active.

## 4. Kernel status (one line)

Substantially implemented in [episodic_memory.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/episodic_memory.clj): thresholded coincidence counting, recent-episode exclusion, and threshold variants already exist.

## 5. Loop shape

Input: a list of indices.

Procedure:

1. for each requested index, intern it against the global unique-index list using equality or unification
2. for each episode linked from a matched index, increment that episode's transient mark count
3. when an episode's mark count reaches the number of marks required for retrieval, place it on the retrieved list
4. after the scan, reset marks on all marked episodes
5. return the retrieved episodes

This is not nearest-neighbor retrieval in the modern embedding sense. It is explicit coincidence counting over named indices. Retrieval is gated by an episode-specific threshold, so one index alone may be insufficient even if it matches.

State read:

- requested indices
- global unique-index list
- index-to-episode links
- per-episode retrieval threshold

State written:

- transient mark counts, then mark reset
- retrieved episode list returned to the caller

Mueller treats the marks as temporary retrieval bookkeeping, not as durable memory.

## 6. Judgment points

None inside Mueller's retrieval loop. It is a deterministic threshold-counting procedure.

The hybrid question starts only after retrieval, when the system decides which retrieved episode is apt, realistic, or worth expanding.

## 7. Accumulation story

Strictly speaking, this mechanism does not add durable memory. Marks are reset before return. Its long-term importance is indirect: because the retrieved episodes it returns feed reminding, analogical planning, and serendipity, it determines which accumulated experiences become active in the present.

So the accumulation story here is not "it writes memory" but "it is the gate through which accumulated memory becomes usable again."

One plausible modernization is usage-weighted retrieval learning: episodes that prove useful when retrieved become easier to retrieve next time. Mueller's emotion-indexed retrieval already provides a natural analog, since high-emotional-charge episodes keep getting reactivated through mood-congruent retrieval and effectively maintain activation through use. Formalizing that as retrieval learning would change episode accessibility over time without changing the core threshold-counting loop.

That learning pressure should apply only to episode retrieval ordering, not to the rule connection graph. The connection graph must remain structurally derived so that serendipity can find novel paths regardless of prior traversal frequency. See mechanism 13 (serendipity recognition), property to preserve.

## 8. Property to preserve

Retrieval must preserve which explicit indices matched, not just that an episode was globally similar.

That provenance matters because later mechanisms reactivate individual indices, propagate them through reminding, and search the rule graph from specific touched structures.

## 9. Upstream triggers / downstream triggers

Upstream:

- planning rule application
- reminding cascade
- object-driven and input-driven serendipity entry points

Downstream:

- analogical rule application
- reminding cascade
- serendipity recognition when retrieved episodes become salient

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: episode retrieval increments marks on episodes linked from each interned index and retrieves an episode only when its mark count reaches its required threshold. The mechanism is explicit, symbolic, and provenance-preserving. It is designed to work over both rule indices and surface-feature indices.

**Candidate hybrid cut**: keep the threshold-counting core structural. If a modern system adds embedding retrieval, it should be treated as an auxiliary cue generator or reranker, not as a replacement for the explicit matched-index structure. The thing to protect is not Mueller's exact data structure but the explicit overlap/provenance signal.

## Interface shape (required)

**none** -- this mechanism is structural retrieval.
