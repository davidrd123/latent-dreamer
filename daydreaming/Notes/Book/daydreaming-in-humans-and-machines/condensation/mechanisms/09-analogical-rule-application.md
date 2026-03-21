# Analogical Rule Application

## 1. Mechanism name

Analogical rule application

## 2. Source anchors

- Chapter 7, `7.5.5 Analogical Rule Application` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):759
- Chapter 7 revised planning loop at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):748
- Chapter 4, `4.1.3 Analogical Planning` at [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):61
- Analogical repair examples in [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):93 and [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):118
- Image-reviewed analogical figures in [34-image-reviewed-chapter-4-learning-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/34-image-reviewed-chapter-4-learning-figures.md):36 and [34-image-reviewed-chapter-4-learning-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/34-image-reviewed-chapter-4-learning-figures.md):47
- Appendix A examples: `REVENGE2`, `REVENGE3`, `RECOVERY1`, `RATIONALIZATION1`
- Corresponding code: no recovered general analogical planner in the kernel; closest ingredients are [episodic_memory.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/episodic_memory.clj) and context utilities in [context.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/context.clj)

## 3. Cognitive phenomenon (one line)

Structured reminding-by-use: a remembered prior plan suggests how to continue the current plan without searching from scratch.

## 4. Kernel status (one line)

Not recovered as a general mechanism: the kernel can retrieve episodes, but it does not yet perform Mueller's explicit source-to-target planning-tree transfer with binding augmentation and recursive repair.

## 5. Loop shape

Input: a context, a target subgoal, a retrieved episode, a planning rule, and initial bindings from rule-to-subgoal unification.

Procedure:

1. unify the abstract planning rule structure with the concrete goal and subgoals contained in the retrieved episode
2. use that unification to augment the current binding set with values supplied by the source episode
3. invoke subgoal creation using the target subgoal, the source episode, the planning rule, and the augmented bindings

What this means at the larger Chapter 4 level:

- the source episode acts as a suggestion for which rule to apply and how to instantiate it
- if the suggested subtree fits, it can be copied directly
- if a subtree does not fit or bottoms out early, regular planning is invoked to repair only the mismatched part
- analogical planning can recurse if repairs themselves find new applicable episodes

State read:

- target subgoal and current context
- retrieved source episode
- planning rule associated with that episode fragment
- current bindings

State written:

- augmented bindings
- a newly instantiated or partially copied target subtree
- repair flags that determine whether the resulting episode counts as new enough to store

## 6. Judgment points

This mechanism is strongly structural, but there are two plausible hybrid cuts:

1. **Aptness of source-to-target mapping**: Mueller relies on structural similarity and unification; a modern system may need contextual help deciding whether a source episode is actually the right analogy.
2. **Repair-vs-reject judgment**: when a suggested rule partially mismatches, deciding whether the mismatch is a tolerable repair or a sign that the analogy is poor may benefit from contextual evaluation.

The actual copying, binding propagation, and repair bookkeeping should stay structural.

## 7. Accumulation story

Analogical rule application converts stored episodes into new plan structure. When the resulting plan later succeeds or is stored with repairs, the system gains a new episode that can be indexed more accessibly than the old one. This is one of Mueller's main mechanisms for making previously inaccessible structure available in future situations.

So the mechanism does not just reuse memory; it grows memory by producing new repaired variants.

## 8. Property to preserve

Analogy must remain explicit transfer over planning structure with explicit repair points. The system has to know which source episode was used, which rule it suggested, which subtree transferred unchanged, and which parts had to be rebuilt.

Without that structure, analogical planning collapses into generic "this feels similar" retrieval and loses the prosthetic value Mueller is after.

## 9. Upstream triggers / downstream triggers

Upstream:

- planning rule application after episode retrieval
- reminding when a retrieved episode is reactivated and fed back into planning

Downstream:

- subgoal creation
- recursive analogical or regular planning for repaired subtrees
- later episode storage if the constructed target plan is retained

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: analogical rule application is a small but critical bridge procedure. It takes a retrieved episode and a currently applicable planning rule, uses the episode to fill in otherwise unbound variables in the current plan, and then hands control to subgoal creation. At the chapter level, this is the move that makes source-episode structure guide target planning while still allowing repair when parts do not fit.

**Candidate hybrid cut**: keep the structural transfer, explicit bindings, recursive repair, and provenance in Clojure. The best hybrid use is evaluative: help decide whether a retrieved episode is actually an apt analogy and whether a partial mismatch should be repaired or abandoned, but do not replace the planning-tree manipulation itself.

## Interface shape (required)

**tentative schema**

Integration patterns:

- **LLM-as-evaluator** for analogy aptness
- **Co-routine judgment** for repair-vs-reject decisions

Input:

```clojure
{:target-subgoal {:id keyword
                  :objective any
                  :context-id keyword}
 :source-episode {:id keyword
                  :root-goal any
                  :subtree any
                  :realism number
                  :desirability number}
 :planning-rule {:id keyword
                 :antecedent any
                 :consequent any}
 :bindings map}
```

Output:

```clojure
{:analogy-evaluation {:apt? boolean
                      :score number
                      :reason string}
 :repair-guidance {:repairable? boolean
                   :mismatch-sites [any]
                   :reason string}}
```
