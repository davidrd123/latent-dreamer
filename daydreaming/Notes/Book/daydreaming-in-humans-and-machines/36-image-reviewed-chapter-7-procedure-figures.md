# Image-Reviewed Chapter 7 Figures

This note reconstructs the Chapter 7 figure pages from the page images, not from the OCR text layer.

Source images:

- [page-198-198.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-198-198.jpg)
- [page-200-200.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-200-200.jpg)
- [page-206-206.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-206-206.jpg)
- [page-207-207.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-207-207.jpg)
- [page-212-212.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-212-212.jpg)

## Figure 7.1: Basic Procedure Dependencies

PDF page: `198`

This is the implementation-level version of the Chapter 2 architecture graph:

- `Emotion-driven control -> Planner`
- `Planner -> Rule application`
- `Planner -> Concern termination`
- `Rule application -> Inference rule application`
- `Rule application -> Planning rule application`
- `Inference rule application -> Concern initiation`

The figure serves as the minimal dependency skeleton before episodic memory and serendipity are added.

## Figure 7.2: Tree of Planning Contexts

PDF page: `200`

The figure is a context tree labeled `CX.1`, `CX.2`, `CX.3`, `CX.4`, `CX.5`, with one box marked `CURRENT`.

Different rule applications produce different sprouted contexts. The diagram makes the control problem visual:

- planning does not proceed in one linear world-state
- the planner must manage a branching tree of imagined contexts
- backtracking is implemented by moving among those sprouted contexts

## Figure 7.3: Personal and Daydreaming Goal Contexts

PDF page: `206`

This diagram distinguishes horizontal and vertical growth:

- a horizontal arrow across the top is labeled `personal goal concerns`
- a vertical relation downward is labeled `daydreaming goal concerns`

Named contexts like `CX71`, `CX75`, `CX78`, `CX710`, `CX73`, `CX74`, and `CX79` show how:

- personal-goal contexts proceed across reality contexts
- daydreaming-goal contexts can sprout downward off present or past reality contexts

The figure is useful because it turns the prose distinction between personal concerns and daydreaming concerns into a spatial one.

## Figure 7.4: Revised Procedure Dependencies

PDF page: `207`

This extends Figure 7.1 by adding episodic memory and analogy:

- `Planning rule application -> Episode retrieval`
- `Planning rule application -> Analogical rule application`
- `Planning rule application -> Subgoal creation`
- `Concern termination -> Episode storage`
- `Episode retrieval -> Reminding`

The figure shows the implementation consequence of adding analogical planning: the planner no longer depends only on local rules.

## Figure 7.5: Further Revised Procedure Dependencies

PDF page: `212`

This is the Chapter 7 culmination. It adds the serendipity machinery:

- `(Input) -> Serendipity recognition`
- `Reminding -> Serendipity recognition`
- `Mutation -> Serendipity recognition`
- `Serendipity recognition -> Concern initiation`

Visually, the figure integrates ordinary planning, episodic reminding, input-triggered recognition, and mutation-triggered recognition into one dependency network.
