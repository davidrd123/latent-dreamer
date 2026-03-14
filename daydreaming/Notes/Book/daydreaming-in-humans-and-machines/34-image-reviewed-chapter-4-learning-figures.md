# Image-Reviewed Chapter 4 Figures

This note reconstructs the Chapter 4 figure pages from the page images, not from the OCR text layer.

Source images:

- [page-108-108.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-108-108.jpg)
- [page-109-109.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-109-109.jpg)
- [page-110-110.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-110-110.jpg)
- [page-112-112.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-112-112.jpg)
- [page-113-113.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-113-113.jpg)
- [page-114-114.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-114-114.jpg)
- [page-121-121.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-121-121.jpg)
- [page-125-125.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/figure-pages/page-125-125.jpg)

## Figures 4.1-4.5: Planning Trees and Episode Indexing

PDF pages: `108-112`

`Figure 4.1: Planning Tree for REVENGE1`
- a full planning tree rooted in `REVENGE(Harrison,LOVERS)`
- left branch builds `GOAL(Harrison,LOVERS)`
- middle branch includes actions like `MTRANS`, `VPROX`, `M-PHONE`, and `KNOW`
- right branch builds `FAILED-GOAL(Harrison,LOVERS)`

`Figure 4.2: Indexing of Episodes by Rules`
- the same material is reorganized as episode fragments indexed under rules
- `REVENGE1`, `REVENGE2`, and `REVENGE3` are shown as episode / superepisode nodes
- the figure’s point is that retrieval indexes sit over reusable rule-rooted fragments, not only over whole monolithic trees

`Figure 4.3: Cases in Analogical Planning`
- a source tree and a target tree are shown side by side
- numbered cases mark what happens when the source and target diverge
- the chapter text under the figure names the key cases: applicable rule, inapplicable rule, source bottoms out before target, and target bottoms out before source

`Figure 4.4: Planning Tree for REVENGE2`
- visibly similar to Figure 4.1 but with `Robert` instead of `Harrison`
- shows reuse of the revenge structure under a new binding

`Figure 4.5: Planning Tree for REVENGE3`
- switches from the `LOVERS` domain to an employment-style domain rooted in `REVENGE(Agatha,EMPL)`
- a dotted box marks the repaired portion of the source tree
- this makes the analogical-repair story visual: some substructure transfers intact while other substructure is replaced

Taken together, these figures explain why episodic memory is stored under rules and subgoals. That representation makes partial reuse and repair possible.

## Figure 4.6: Analogical Planning in RECOVERY1

PDF page: `113`

The figure explicitly contrasts:

- `SOURCE HAND-CODED EPISODE`
- `TARGET EPISODE (DAYDREAM)`

The source side involves a known telephone number in a `TRW-File`. The target side is a recovery chain involving:

- `RECOVERY`
- `MTRANS`
- `VPROX`
- `M-PHONE`
- `KNOW(Me,TELNO(Harrison))`

The big diagonal arrow is the point: a hand-coded source episode becomes a subtree inside a generated daydream target.

## Figure 4.7: Partial Tree for RATIONALIZATION1

PDF page: `114`

Again the figure compares:

- `SOURCE HAND-CODED EPISODE`
- `TARGET EPISODE (DAYDREAM) (Forward Other Planning)`

Both sides include Harrison, Paramount, and Cairo. The target side has a dotted repair box around a generated `PTRANS(Harrison,Cairo)` portion.

This shows a different analogical effect from Figure 4.6: the imported episode is only partial and regular planning has to expand or repair the missing part.

## Figure 4.8: Making Inaccessible Rules Accessible

PDF page: `121`

This figure shows a hand-coded episode on the left, with an inaccessible rule path that requires multiple indices, and a new daydream on the right, indexed under `LOVERS`.

The arrow from the left episode into the right daydream shows the mechanism:

- a daydream generated from a retrieved episode can expose previously inaccessible rules
- once the new daydream exists under a more accessible index, those rules become easier to retrieve in the future

This is why the figure matters to the chapter’s learning story: daydreaming is not only producing episodes, it is reorganizing accessibility.

## Figure 4.9: Inference Chain for a Social Regard Failure

PDF page: `125`

This is a compact causal graph. Inputs such as:

- `AT(Harrison,Nuart)`
- `PTRANS(Me,Home,Nuart)`
- `MTRANS(Me,Harrison)`
- `NOT(WELL-DRESSED(Me))`
- `RICH(Harrison)`

feed into:

- `NEG-ATTITUDE(Harrison,Me)`
- `ROMANTIC-INTEREST(Me,Harrison)`
- `FAILED-GOAL(Me,POS-ATTITUDE(Harrison,Me))`

The figure shows why a preservation or reversal rule can be learned: the social-regard failure is not atomic, it has a structured chain of antecedents that can be targeted later.
