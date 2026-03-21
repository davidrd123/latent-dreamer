# Rule Connection Graph

Supplementary note. This is not one of the 19 mechanism cards because it is a shared structural substrate rather than a standalone procedure. It is the main data-structure gap underneath [05-planning-rule-application.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/05-planning-rule-application.md), [13-serendipity-recognition.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/13-serendipity-recognition.md), and [14-mutation.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/14-mutation.md).

## Source anchors

- Chapter 5, serendipity / rule-intersection discussion in [05-everyday-creativity-in-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/05-everyday-creativity-in-daydreaming.md):95 and [05-everyday-creativity-in-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/05-everyday-creativity-in-daydreaming.md):129
- Chapter 7 revised planning / serendipity procedures in [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):748 and [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):886
- Figure dependencies in [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):56 and [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):70

## What it is

The rule connection graph is a graph over rules, not over episodes. A connection exists when one rule's antecedent or consequent structure overlaps another rule's structure closely enough that one can lead into the other during planning or serendipity search.

At a minimum, the graph supports three kinds of use:

- **Planning accessibility**: [05-planning-rule-application.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/05-planning-rule-application.md) does not search all rules globally once the graph exists; it restricts attention to connected rules.
- **Serendipity path search**: [13-serendipity-recognition.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/13-serendipity-recognition.md) searches from a concern-linked top rule to a salient-source-linked bottom rule through this graph.
- **Mutation follow-through**: [14-mutation.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/14-mutation.md) depends on serendipity using the mutated action's parent-goal rule as a starting point in the same graph.

## Construction and query shape

Construction is structural:

1. treat each rule as a node
2. compare antecedent and consequent patterns for overlap or unifiable transition points
3. create traversable edges where one rule can plausibly feed another

Query is also structural:

1. choose a start rule from the current concern or parent goal
2. choose a target rule from a salient concept, episode, or mutated action
3. traverse explicit paths
4. verify any candidate path through progressive unification and plan construction

## Property to preserve

The graph must remain **structurally derived and traversable**, not merely a learned similarity cloud.

That means preserving:

- explicit rule nodes and explicit edges
- the ability to say which path was found
- the ability to verify that path step by step
- equal findability for never-before-traversed structural paths

If adaptive learning is added, it should operate on episode retrieval ordering or path ranking, not on the existence of graph edges themselves. Otherwise the system stops discovering neglected structural paths and starts following reinforced habits.

## Kernel status

Not yet recovered as a general kernel substrate. Current kernel mechanisms can store episodes and manage contexts, but they do not yet expose Mueller's general rule-graph construction and traversal layer.

## Why it matters

This is the shared backbone missing under several of the most distinctive mechanisms. Without it, the kernel has memory and scheduling but not the full creative engine: planning stays too local, serendipity has no path space to search, and mutation cannot turn odd variants into reusable discoveries.
