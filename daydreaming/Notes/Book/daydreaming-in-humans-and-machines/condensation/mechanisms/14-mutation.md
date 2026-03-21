# Mutation

## 1. Mechanism name

Mutation

## 2. Source anchors

- Chapter 5, `5.4 Action Mutation` at [05-everyday-creativity-in-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/05-everyday-creativity-in-daydreaming.md):196
- Chapter 5, `5.4.1 The Purpose of Action Mutation` and `5.4.2 Strategies for Action Mutation` at [05-everyday-creativity-in-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/05-everyday-creativity-in-daydreaming.md):200 and [05-everyday-creativity-in-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/05-everyday-creativity-in-daydreaming.md):220
- Chapter 7, `7.7.2 Action Mutation` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):926
- Chapter 2 compressed overview at [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):393
- Appendix A examples: `RECOVERY2`, `COMPUTER-SERENDIPITY`, `LAMPSHADE-SERENDIPITY`
- Corresponding code: no recovered generic mutation mechanism; only mutation-event tracing and family-specific branch instrumentation in [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj), [control.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/control.clj), and [trace.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/trace.clj)

## 3. Cognitive phenomenon (one line)

Fanciful possibility generation: deliberate off-path variation that breaks mental set and sometimes exposes a usable solution.

## 4. Kernel status (one line)

Not recovered as a general mechanism: the kernel records mutation-like events in traces, but it does not yet enumerate Mueller's action mutations and feed them through serendipity as a last-resort search expansion.

## 5. Loop shape

Input: a concern whose ordinary plans have failed.

Procedure:

1. walk descendant leaf contexts of the concern's root context
2. for each active goal in those contexts whose objective is an action, treat that action as a mutation seed
3. generate mutations of the action using three structural strategies:
   - permute objects
   - generalize one object into a typed variable constrained not to be the original
   - change action type and adjust fillers to the new action schema
4. for each mutated action, invoke serendipity recognition on the current concern and the mutation
5. keep a mutation only if serendipity finds and verifies a path that makes the mutation useful

Important structural details:

- mutation is explicitly nested over contexts, active action goals, and generated mutations
- it is a last-resort mechanism, used when normal search reduction has blocked discovery
- the top rule for the downstream serendipity search is derived from the parent goal of the mutated action

State read:

- concern root and descendant leaf contexts
- active action goals
- mutation operators
- current concern and parent goals

State written:

- no durable state from mutation alone
- successful mutations feed serendipity, which may create a new analogical plan and new episode structure

## 6. Judgment points

Mueller's mutation operators are structural, but two modern judgment points stand out:

1. **Mutation triage**: some generated mutations are formally legal but obviously useless or nonsensical; a contextual filter could rank or prune them before expensive downstream work.
2. **Richer mutation generation**: Mueller's operators are fixed and local. A modern system might generate additional contextual mutations, but only if those still attach to an explicit mutated action candidate rather than replacing the mechanism wholesale.

The accept/reject decision itself is intentionally delegated to serendipity recognition.

## 7. Accumulation story

Raw mutations do not persist. Their value is indirect: they expose candidate actions that normal planning would not have considered. When one of those candidates yields a verified serendipitous path, the result becomes a new analogical plan and eventually a stored episode. So mutation is one of the main ways the system expands the search frontier without immediately bloating long-term memory.

## 8. Property to preserve

The mechanism must preserve explicit off-path candidate generation over identifiable plan elements. It is not enough to ask for "a creative alternative"; the system has to know which action was mutated, how it was mutated, and which downstream discovery made it worth keeping.

## 9. Upstream triggers / downstream triggers

Upstream:

- failure of all ordinary plans for a concern
- blocked planning under heavy search reduction

Downstream:

- serendipity recognition
- analogical planning if a mutation yields a verified path
- later episode storage only if the resulting plan becomes a real episode

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: mutation is a bounded structural generator of arbitrary action variations. It exists to counter the search-reduction strategies that keep ordinary planning tractable but sometimes miss creative solutions. The generated mutations are not accepted directly; each one is passed into serendipity recognition, which determines whether the mutation can actually support a useful plan.

**Candidate hybrid cut**: keep the explicit mutation loop and the identity of each mutated action structural. The most promising hybrid use is an evaluator or supplementary generator that ranks or adds contextual mutations before serendipity checks them, while preserving the mutated-action record as a first-class object.

## Interface shape (required)

**tentative schema**

Integration patterns:

- **LLM-as-evaluator** for mutation triage
- **LLM-as-content-generator** for bounded supplemental mutations

Input:

```clojure
{:concern {:id keyword
           :goal any
           :context-id keyword}
 :seed-action {:fact any
               :parent-goal any
               :context-id keyword}
 :generated-mutations [{:mutation-id keyword
                        :operator keyword
                        :action any}]}
```

Output:

```clojure
{:mutation-rankings [{:mutation-id keyword
                      :keep? boolean
                      :score number
                      :reason string}]
 :supplemental-mutations [{:operator :llm
                           :action any
                           :reason string}]}
```
