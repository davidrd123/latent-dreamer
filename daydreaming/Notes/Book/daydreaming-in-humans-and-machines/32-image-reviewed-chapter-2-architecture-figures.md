# Image-Reviewed Chapter 2 Figures

This note reconstructs the visual content of the Chapter 2 figure pages from the page images, not from the OCR text layer.

Source images:

- [page-054-054.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-054-054.jpg)
- [page-056-056.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-056-056.jpg)
- [page-057-057.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-057-057.jpg)
- [page-058-058.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-058-058.jpg)
- [page-060-060.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-060-060.jpg)
- [page-061-061.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-061-061.jpg)
- [page-062-062.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-062-062.jpg)
- [page-064-064.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-064-064.jpg)

## Figure 2.1: Needs with Subsumption States

PDF page: `54`

The diagram is a small decision flow:

- `Need state below threshold`
- test: `Subsumption state achieved?`
- if `Yes`: `Initiate goal to satisfy need state`
- if `No`: `Initiate goal to achieve subsumption state`

The visual point is that DAYDREAMER may have to satisfy prerequisite world states before it can satisfy the need directly.

## Figure 2.2: Interleaving of Concerns

PDF page: `56`

This figure is a timeline with labeled thought segments:

- `Daydream1 / Concern1` spans early thoughts
- `Daydream2 / Concern2` interrupts in the middle
- `Daydream3 / Concern1` resumes the original concern later

The horizontal time axis is the key visual element. The figure shows that a concern can be suspended, displaced by another concern, and then resumed.

## Figure 2.3: Meta-Planning / Universal Subgoaling

PDF page: `57`

The diagram places `GOAL1`, `GOAL2`, and `GOAL3` around a `PROBLEM SOLVER`, with a separate box labeled `Create subgoal / metagoal`.

Two goals are shown in a `tie/conflict` relation. The visual claim is that a generic problem-solver could respond to multi-goal conflict by creating an additional higher-level or coordinating goal.

## Figure 2.4: Emotion-Driven Planning

PDF page: `58`

This is the proposed alternative to Figure 2.3. It shows:

- `PROBLEM SOLVER -> EMOTION-DRIVEN CONTROL`
- `EMOTION-DRIVEN CONTROL` connected outward to multiple `EMOTIONS` / `GOAL` pairs

The figure matters because it makes emotions the routing mechanism for selecting among active concerns instead of invoking an explicit meta-planner.

## Figures 2.5-2.10: Emotions and Concerns

PDF pages: `60-62`

These figures are a sequence, not isolated diagrams.

`Figure 2.5: Emotions and Concerns 1`
- two concerns, each linked to one emotion
- both have dynamic importance equal to the emotion strength

`Figure 2.6: Emotions and Concerns 2`
- `concern1` acquires a second associated emotion
- the concern’s importance becomes the sum of the associated emotion strengths
- this makes `concern1` dominate `concern2`

`Figure 2.7: Emotions and Concerns 3`
- now `concern2` also acquires another emotion
- its summed importance overtakes `concern1`

`Figure 2.8: Emotions and Concerns 4`
- a new `concern3` appears with a single strong emotion
- the new concern outranks both earlier concerns

`Figure 2.9: Emotions and Concerns 5`
- a still newer `concern4` appears with even stronger associated emotion
- it becomes the next concern to process

`Figure 2.10: Emotions and Concerns`
- the same story is redrawn as a graph of `dynamic importance` over `time`
- the plotted sequence shows the control order moving from `concern1` to `concern2` to `concern3` to `concern4`

Taken together, these figures are the visual proof of the chapter’s main control claim: concern selection is driven by summed emotional motivation, and the active concern can shift whenever a newly associated emotion pushes another concern above the current one.

## Figure 2.11: DAYDREAMER Procedures

PDF page: `64`

This is the top-level process graph for the whole Chapter 2 architecture. The visible dependency structure is:

- `Emotion-driven control -> Planner`
- `Planner -> Rule application`
- `Planner -> Concern termination`
- `Rule application -> Inference rule application`
- `Rule application -> Planning rule application`
- `Inference rule application -> Concern initiation`
- `Planning rule application -> Episode retrieval`
- `Planning rule application -> Analogical rule application`
- `Planning rule application -> Subgoal creation`
- `Concern termination -> Episode storage`
- `Episode retrieval -> Reminding`
- `(Input) -> Serendipity recognition`
- `Mutation -> Serendipity recognition`
- `Reminding -> Serendipity recognition`

The diagram is important because it shows the architecture as a dependency graph rather than a simple pipeline. Serendipity, reminding, mutation, analogical application, and episode storage all sit off the main planner spine but feed back into the same control loop.
