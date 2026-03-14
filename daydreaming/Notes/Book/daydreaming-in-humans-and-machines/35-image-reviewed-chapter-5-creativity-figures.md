# Image-Reviewed Chapter 5 Figures

This note reconstructs the Chapter 5 figure pages from the page images, not from the OCR text layer.

Source images:

- [page-147-147.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-147-147.jpg)
- [page-149-149.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-149-149.jpg)
- [page-150-150.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-150-150.jpg)
- [page-159-159.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-159-159.jpg)
- [page-165-165.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-165-165.jpg)

## Figure 5.1: Serendipity

PDF page: `147`

This is a compact process sketch showing four triggers feeding into serendipity recognition:

- `Episode retrieved`
- `Concern activated`
- `Input received`
- `Mutation generated`

Those feed into:

- `Serendipity recognition`

which relies on:

- `Intersection search + path verification`

and, if successful, leads to:

- `Full planning`

The figure matters because it visually unifies the otherwise separate chapter cases of episode-driven, input-driven, and mutation-driven serendipity.

## Figure 5.2: Rule Intersection for RECOVERY3

PDF page: `149`

This diagram is a rule-graph path rather than a tree. It starts from a `TOP RULE` and a `BOTTOM RULE`, with labeled rules such as:

- `Mtrans-Plan2`
- `Vprox-Plan2`
- `M-Phone-Plan`
- `Know-Plan2`
- `Mtrans-Plan1`
- `Know-Plan3`

The right margin marks:

- `found path`
- `forward/backward connection`

The figure is important because it makes the serendipity search criterion visual: find a path through the rule-interconnection graph from a rule tied to one concern to a rule tied to another.

## Figure 5.3: Verification Episode for RECOVERY3

PDF page: `150`

This is the episode-level companion to Figure 5.2. It shows a descending chain:

- `MTRANS(Me,Harrison)`
- `VPROX(Harrison,Me)`
- `M-PHONE(Me,Harrison)`
- `KNOW(Me,TELNO(Harrison))`
- `MTRANS(Me,ALUMNI-DIR(Organization1),TELNO(Harrison))`
- `KNOW(ALUMNI-DIR(Organization1),TELNO(Harrison))`
- `COLLEGE(Harrison,?Organization1)`

The figure shows the actual episode fragment used to verify that the rule-path found in serendipity recognition can be grounded in a usable episode.

## Figure 5.4: ABSTRIPS vs. DAYDREAMER

PDF page: `159`

The figure contrasts two search philosophies.

`ABSTRIPS`
- starts from one `GOAL`
- applies `RELAXATION of operator preconditions`
- then performs `successive REFINEMENT`

`DAYDREAMER`
- starts from one `GOAL1`
- generates `FANCIFUL scenarios`
- applies `successive MUTATION`
- can then use `SERENDIPITY` to shift into multiple goals such as `GOAL1` and `GOAL2`

The visual contrast is the main argument: ABSTRIPS reduces search by abstraction, while DAYDREAMER deliberately expands and distorts the search space to discover hidden alternatives.

## Figure 5.5: Opportunistic Planning vs. Serendipity

PDF page: `165`

The left side, `Serendipity`, shows movement from a `SOURCE` world state to a `TARGET` through:

- `take const`
- `subgoal`
- `subst`

The right side, `Opportunistic planning`, shows a simpler local move:

- `world state -> equiv -> subgoal`

The figure’s point is that serendipity is not merely opportunistic noticing of local equivalence. It involves transfer from a separate source structure into the current target structure.
