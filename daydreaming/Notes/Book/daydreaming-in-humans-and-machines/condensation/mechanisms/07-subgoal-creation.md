# Subgoal Creation

## 1. Mechanism name

Subgoal creation

## 2. Source anchors

- Chapter 7, `7.5.6 Subgoal Creation` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):766
- Chapter 7 revised planning loop at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):748
- Chapter 7 note on procedural consequents for daydreaming goals at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):617
- Chapter 2 overview of planning decomposition at [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):41
- Corresponding code: partial structural support in [context.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/context.clj); no general recovered subgoal-creation procedure yet

## 3. Cognitive phenomenon (one line)

Commit a chosen decomposition: once a way forward is selected, thought expands into explicit next things to achieve.

## 4. Kernel status (one line)

Partial infrastructure only: [context.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/context.clj) can sprout and manage planning branches, but the kernel does not yet recover Mueller's general subgoal-creation step with instantiated child goals, subepisode carry-down, and cross-plan variable propagation.

## 5. Loop shape

Input: a context, a parent subgoal, an optional episode, a planning rule, and bindings.

Procedure:

1. sprout a fresh child context from the given context
2. for each subgoal pattern in the planning-rule consequent:
   - instantiate it with the current bindings
   - create it as a child subgoal of the parent in the new context
   - if analogical planning is active, attach the matching subepisode from the source episode to that new subgoal
3. ensure any variable value supplied for one instance of the parent subgoal is propagated to matching variables elsewhere in the current plan

Important structural details:

- episodic planning rules are not directly eligible here; they can only enter through analogical episodes
- subgoal creation is the handoff where a chosen rule becomes explicit plan structure in a new branch
- some rules do not merely list subgoals but execute code that creates the appropriate subgoals, especially for daydreaming-goal planning

State read:

- current context and parent subgoal
- planning rule consequent
- current bindings
- optional source episode and its subepisode structure

State written:

- a sprouted child context
- instantiated child subgoals and their parent-child links
- subepisode associations on child goals
- propagated bindings across the current plan

## 6. Judgment points

This mechanism is almost entirely structural. The main hybrid pressure point is already present in Mueller:

1. **Procedural consequent generation**: some daydreaming-goal rules use code instead of fixed declarative consequents. That is the clearest slot for a rule-with-LLM-consequent pattern while keeping the rule itself structural.

## 7. Accumulation story

Subgoal creation grows the planning tree and the context tree. It is one of the main places where abstract rule knowledge becomes concrete branch structure. If the branch later succeeds and is stored, these created subgoals become part of an episode that can be reused in the future.

## 8. Property to preserve

Plan growth must remain explicit and inspectable. The system needs real subgoal nodes, real parent-child structure, and real branch contexts, not just a generated paragraph describing what might happen next.

## 9. Upstream triggers / downstream triggers

Upstream:

- planning rule application
- analogical rule application

Downstream:

- context sprouting / backtracking
- later rule application over the new child goals
- eventual episode storage if the branch resolves

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: subgoal creation is the mechanical decomposition step. A new context is sprouted, child subgoals are instantiated from the chosen rule, and any matching subepisodes are carried down to support further analogical planning. It is the place where planning rules become actual branch structure.

**Candidate hybrid cut**: keep context sprouting, child-goal creation, subepisode carry-down, and variable propagation structural. The only plausible hybrid extension is on those rules whose consequents are already procedural rather than purely declarative.

## Interface shape (required)

**tentative schema**

Integration patterns:

- **Rule-with-LLM-consequent** for procedural daydreaming-goal consequents

Input:

```clojure
{:context {:id keyword}
 :parent-subgoal {:id keyword
                  :objective any}
 :planning-rule {:id keyword
                 :consequent any}
 :bindings map
 :source-episode {:id keyword
                  :subepisodes [any]}}
```

Output:

```clojure
{:child-context-id keyword
 :created-subgoals [{:objective any
                     :subepisode any}]
 :generated-consequent any}
```
