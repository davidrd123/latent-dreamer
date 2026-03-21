# Planning Rule Application

## 1. Mechanism name

Planning rule application (regular plus the Chapter 7 analogical revision)

## 2. Source anchors

- Chapter 7, `7.4.5 Planning Rule Application` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):601
- Chapter 7, `7.5.4 Revised Planning Rule Application`, `7.5.5 Analogical Rule Application`, and `7.5.6 Subgoal Creation` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):748
- Chapter 2 compressed overview at [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):353
- Procedure dependencies in [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):56
- Appendix A examples: `REVENGE1`, `REVENGE3`, `RECOVERY2`, `RECOVERY3`
- Corresponding code: partial infrastructure in [context.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/context.clj), [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj); no general recovered planning-rule engine yet

## 3. Cognitive phenomenon (one line)

Goal decomposition: turn one active objective into a next layer of subgoals, while opportunistically reusing prior episodes when available.

## 4. Kernel status (one line)

Partial at best: the kernel has context sprouting and some family-specific planning scaffolds, but not Mueller's general planning-rule application loop with rule-graph gating, analogical continuation, and fallback to regular planning.

## 5. Loop shape

In the basic procedure, the mechanism iterates over active subgoals for the current concern that have no children. For each such subgoal it finds planning rules whose antecedent unifies with the subgoal objective, sprouts a new context for each rule application, and instantiates the consequent subgoals into that sprouted context.

After episodic memory is added, the loop changes:

1. choose each active leaf subgoal of the current concern
2. if that subgoal is already carrying an analogical episode and the associated rule still unifies, continue the existing analogical plan
3. otherwise, for each accessible planning rule whose antecedent unifies with the subgoal objective:
   - retrieve episodes indexed by that rule
   - if episodes are found, invoke reminding on each and then analogical rule application
   - otherwise, do regular subgoal creation

Important structural details:

- planning rules are not searched globally once the rule connection graph exists; only connected rules are considered
- subgoals are expanded left-to-right
- some planning-rule consequents are not plain subgoal lists but executable code that creates subgoals for daydreaming goals such as `RATIONALIZATION` and `REVERSAL`

State read:

- current concern
- active subgoals in current context
- accessible planning rules
- rule connection graph
- any analogical episode already attached to the subgoal

State written:

- sprouted contexts
- newly created subgoals
- analogical plan associations carried onto child subgoals
- retrieval and reminding side effects through called mechanisms

## 6. Judgment points

This mechanism contains several good hybrid-cut candidates:

1. **Applicability softness**: antecedent unification is rigid in Mueller. A modern system may want soft applicability judgments when exact pattern fit is too brittle.
2. **Episode usefulness**: once episodes are retrieved under a rule index, Mueller tries each structurally. A modern system may want to rank which retrieved episode is worth analogical application before committing work.
3. **Generated consequents for daydreaming goals**: Mueller already allows some consequents to be code that creates subgoals rather than a fixed declarative pattern. That is the clearest slot for an LLM-augmented consequent while keeping the rule itself structural.

## 7. Accumulation story

Planning rule application grows the current planning tree. It sprouts new contexts, creates new subgoals, and can carry analogical episodes deeper into the tree. Through its calls to episode retrieval and reminding, it also updates the recent-memory machinery that affects future retrieval and serendipity.

It does not itself store long-term memory, but it is the mechanism that turns long-term structures into new short-term planning structure.

## 8. Property to preserve

Planning must remain a graph-structured decomposition process over explicit goals, rules, and contexts, not a monolithic one-shot content generation step.

In particular, the system must preserve:

- explicit alternative branches
- traceable rule provenance
- the ability to say which rule or episode produced which subgoal

## 9. Upstream triggers / downstream triggers

Upstream:

- planner
- any analogical episode already associated with the current subgoal

Downstream:

- episode retrieval
- reminding
- analogical rule application
- subgoal creation

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: planning rule application is the branching engine of DAYDREAMER. It takes the current active leaf subgoals, finds applicable planning rules, and either expands them regularly or uses retrieved episodes to continue or begin analogical plans. It operates over explicit rule antecedents and consequents, explicit contexts, and explicit subgoal trees.

**Candidate hybrid cut**: keep branching, context management, rule-graph accessibility, and subgoal bookkeeping structural. The best hybrid points are: (a) soft applicability judgment when unification is too rigid, (b) ranking retrieved analogical episodes before expansion, and (c) rule-with-LLM-consequent for those daydreaming-goal rules whose consequents are already procedural rather than plain pattern instantiation.

## Interface shape (required)

**tentative schema**

Integration patterns:

- **Co-routine judgment** for soft applicability
- **LLM-as-evaluator** for episode ranking
- **Rule-with-LLM-consequent** for contextual daydreaming-rule outputs

Input:

```clojure
{:subgoal {:objective any
           :context-id keyword
           :concern-id keyword}
 :candidate-rules [{:id keyword
                    :antecedent any
                    :consequent any
                    :plausibility number}]
 :retrieved-episodes [{:id keyword
                       :rule keyword
                       :realism number
                       :desirability number
                       :summary string}]
 :current-concern {:id keyword
                   :goal-type keyword
                   :planning-type #{:real :imaginary}}
 :recent-state {:recent-indices [any]
                :recent-episodes [keyword]}}
```

Output:

```clojure
{:rule-ordering [keyword]
 :selected-episode keyword-or-nil
 :soft-applicability [{:rule-id keyword
                       :applicable? boolean
                       :confidence number
                       :reason string}]
 :generated-consequents [any]}
```
