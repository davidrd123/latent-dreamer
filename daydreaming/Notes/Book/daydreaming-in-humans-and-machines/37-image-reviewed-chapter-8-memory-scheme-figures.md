# Image-Reviewed Chapter 8 Figures

This note reconstructs the Chapter 8 figure pages from the page images, not from the OCR text layer.

Source images:

- [page-216-216.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-216-216.jpg)
- [page-217-217.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-217-217.jpg)
- [page-218-218.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-218-218.jpg)
- [page-221-221.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-221-221.jpg)
- [page-225-225.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-225-225.jpg)
- [page-226-226.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-226-226.jpg)
- [page-227-227.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-227-227.jpg)
- [page-229-229.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-229-229.jpg)

## Figures 8.1-8.3: Discrimination Nets

PDF pages: `216-218`

`Figure 8.1: Discrimination Net`
- a simple tree indexed first by emotion polarity, then by object
- leaf episodes `ep1` to `ep4` sit under combinations like `emot=neg / obj=chair`

`Figure 8.2: Fully Redundant Discrimination Net`
- the same episodes are reachable through multiple index orders
- branches explicitly duplicate combinations like `emot=neg`, `obj=chair`, `obj=lamp`, `emot=pos`

`Figure 8.3: Redundant Discrimination Net with Delayed Expansion`
- keeps some redundancy but delays full branching until needed
- visually shows a compromise between retrieval flexibility and combinatorial explosion

These three figures together illustrate the tradeoff the chapter cares about: ease of partial-cue retrieval versus storage cost.

## Figure 8.4: Network for Spreading Activation

PDF page: `221`

This figure is a weighted network with a dotted divider between `Working Memory` and `Long-term Memory`.

Active indices like `chair(1.0)` and `neg(1.0)` spread activation through intermediate nodes such as:

- `act1`
- `purchase`
- `success`
- `fail`
- `lamp`

The figure matters because it shows retrieval as graded propagation through a network rather than traversal of a rigid tree.

## Figures 8.5-8.8: Connectionist Nets for Episodes

PDF pages: `225-227`

`Figure 8.5: Connectionist Net for Episodes: First Attempt`
- a single opaque `Net` receives raw indices and outputs retrieved information like `purchase` and `sit`
- this is the simplest distributed-memory sketch

`Figure 8.6: Second Attempt`
- action roles are explicitly separated
- input groups include `various indices`, `action`, `agent`, and `destination`
- role fillers such as `MTRANS`, `ATRANS`, and `PERSON1` / `PERSON2` are attached

`Figure 8.7: Third Attempt`
- generalizes the same idea across multiple action slots such as `action1`, `agent1`, `action2`, `agent2`
- the diagram makes sequence representation more explicit

`Figure 8.8: Fourth Attempt`
- replaces role-specific symbolic channels with a dense structured feature block labeled `SCRIPTS, PLANS, GOALS, etc.`
- this figure visualizes the move toward higher-level structured encodings as network input

The sequence of figures is important because the chapter’s critique depends on the visible escalation in representational complexity.

## Figure 8.9: Intersection Indexing Net

PDF page: `229`

This figure is a flat bipartite-style network:

- top nodes are active indices such as `emot=neg`, `emot=pos`, `obj=chair`, `obj=lamp`
- bottom nodes are episodes `ep1` to `ep4`
- an episode is retrieved when enough of its connected indices are active

The figure captures the compromise strategy advocated for DAYDREAMER: cheaper than fully redundant discrimination nets, but still able to exploit overlap among active indices.
