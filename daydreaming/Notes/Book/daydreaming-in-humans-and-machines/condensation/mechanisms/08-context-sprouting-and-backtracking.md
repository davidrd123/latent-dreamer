# Context Sprouting and Backtracking

## 1. Mechanism name

Context sprouting / backtracking

## 2. Source anchors

- Chapter 7, `7.2.5 Contexts` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):217
- Chapter 7 planner discussion and Figure 7.2 at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):502 and [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):516
- Image-reviewed context-tree reconstruction in [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):28
- Figure 7.3 discussion of personal versus daydreaming goal contexts at [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):40
- Corresponding code: [context.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/context.clj), backtracking support in [control.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/control.clj)

## 3. Cognitive phenomenon (one line)

Maintain several hypothetical worlds at once, then retreat from dead ends without contaminating reality.

## 4. Kernel status (one line)

Substantially implemented in [context.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/context.clj), with backtracking support in [control.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/control.clj); this is one of the stronger recovered structural mechanisms.

## 5. Loop shape

Whenever a planning rule creates an alternative continuation, DAYDREAMER sprouts a new context from the current one. The child inherits the visible facts of the parent and then accumulates its own additions and retractions. Planning proceeds depth-first through this branching tree. When a branch fails because no applicable planning remains, the planner backtracks and selects another sprouted context.

There are two important variants:

- ordinary sprouts, which inherit the parent's visible world
- pseudo-sprouts, used for special constructions such as alternative pasts, which appear in the tree without inheriting content in the same way

State read:

- current context
- visible facts in the ancestry chain
- current branch ordering and timeout information

State written:

- new child contexts
- parent-child links
- asserted and retracted facts local to a branch
- the planner's next-context pointer when backtracking selects another branch

The mechanism is what makes DAYDREAMER's planning tree a tree of worlds rather than a single mutable state.

## 6. Judgment points

None in Mueller's as-built mechanism. Context management is structural.

The only plausible modern pressure point would be branch pruning policy, but Mueller treats that as planner control, not as a context-level judgment call.

## 7. Accumulation story

Contexts accumulate within a run as persistent branch-local world snapshots. They are not long-term episodic memory, but they are durable enough for multi-step planning, backtracking, and later episode formation. Personal-goal success can also roll a sprouted context forward into the new reality context, so branch-local state can become the new real world.

## 8. Property to preserve

Alternative plans must be able to evolve in isolated but ancestry-linked world states.

If this collapses into one mutable scratchpad, the system loses both safe imagination and explicit provenance for later episode storage.

## 9. Upstream triggers / downstream triggers

Upstream:

- planning rule application
- concern initiation for daydreaming goals
- serendipity verification when a new analogical plan is inserted

Downstream:

- planner depth-first traversal
- backtracking
- concern termination
- episode storage

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: contexts are the implementation substrate for hypothetical world states. Planning rules sprout new contexts, the planner explores them depth-first, and backtracking moves among them when branches fail. Personal-goal concerns march across reality contexts; daydreaming-goal concerns sprout downward from present or past reality contexts.

**Candidate hybrid cut**: keep this entirely structural. LLM help may influence which branch is worth expanding, but the branch substrate itself should remain a persistent context graph with explicit ancestry, child links, and branch-local facts.

## Interface shape (required)

**none** -- this mechanism is structural context management.
