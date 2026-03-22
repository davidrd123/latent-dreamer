# Kernel Rule Schema and Execution Model

Purpose: bridge the condensation set into a buildable kernel design.

This note is a parallel deep-build document. It does not change the current sprint target in [current-sprint.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/current-sprint.md), which remains `RuntimeThoughtBeatV1`. It translates the condensation's main architectural findings into a proposed persistent rule schema, a graph-construction rule, an execution contract for LLM-backed rules, and a recursion-ownership model that keeps control in Clojure.

Related:

- [cross-cut-summary.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/cross-cut-summary.md)
- [rule-connection-graph.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/rule-connection-graph.md)
- [05-planning-rule-application.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/05-planning-rule-application.md)
- [09-analogical-rule-application.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/09-analogical-rule-application.md)
- [12-reminding-cascade.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/12-reminding-cascade.md)
- [13-serendipity-recognition.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/13-serendipity-recognition.md)
- [17-rationalization-strategies.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/17-rationalization-strategies.md)
- [21-graph-interface-contract.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/21-graph-interface-contract.md)
- [28-l2-schema-from-5pro.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/28-l2-schema-from-5pro.md)

## Core design constraints

1. The rule graph must remain structural.

The executor may be declarative instantiation, Clojure code, or an LLM-backed call. The graph cannot depend on opaque runtime behavior. It needs declared schema-level structure so that connections between rules remain computable and serendipity search remains traversable.

2. Recursion stays in Clojure.

Reminding, analogical repair, and serendipity verification are recursive control structures. Clojure owns the stack, branching, and backtracking. LLM calls appear only at bounded judgment sites within those loops.

3. Persistent kernel structures are not the same thing as the shared authored graph seam.

The persistent rule base, rule connection graph, episode store, and concern machinery belong to the kernel's own long-lived cognitive state. They should not be confused with the thinner authored graph/interface seam defined in [21-graph-interface-contract.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/21-graph-interface-contract.md), which is intentionally smaller and more cross-lane-stable.

4. LLM-backed rules must degrade cleanly.

Every LLM-backed rule should have a Mueller-faithful fallback path: rigid matching, fixed instantiation, or skip. That makes the hybrid system testable and keeps the structural substrate usable without the model.

## Proposed persistent rule schema

The key split is between the graphable structure of a rule and the runtime executor that realizes it.

```clojure
RuleV1
{:id keyword
 :rule-kind #{:inference :planning}
 :mueller-mode #{:plan-only :inference-only :both}

 ;; Structural, graphable, persistent
 :antecedent-schema [pattern]
 :consequent-schema [pattern]
 :plausibility number
 :index-projections {:match [index-projection]
                     :emit [index-projection]}

 ;; Denotational contract — what state change this rule is supposed to accomplish
 :denotation {:intended-effect keyword-or-description
              :failure-modes [keyword]
              :validation-fn fn-or-nil}

 ;; Runtime behavior
 :executor {:kind #{:instantiate :clojure-fn :llm-backed}
            :spec any}

 ;; Derived / rebuildable cache
 :graph-cache {:out-edge-bases [edge-basis]
               :in-edge-bases  [edge-basis]}

 ;; Provenance
 :provenance {:book-anchors [source-anchor]
              :kernel-status #{:recovered :partial :proposed}
              :notes string?}}
```

Design intent by field:

- `:antecedent-schema` is what must match for the rule to become applicable.
- `:consequent-schema` is the declared shape of what the rule produces.
- `:denotation` is what state change the rule is supposed to accomplish. This is the layer between "what shape does the output have" and "how is the output produced." Without it, an LLM can return something schema-valid that doesn't do what the rule is for. For `:instantiate` rules, the denotation IS the schema. For `:llm-backed` rules, it's the contract the validator checks against. Surfaced by outside architecture review (semantic attachment literature, Dornhege et al.).
- `:executor` is how the rule produces it at runtime.
- `:index-projections` make retrieval and storage explicit rather than implicit.
- `:graph-cache` is derived. It can be rebuilt from the schemas and should not become the source of truth.

The three-layer discipline is:

- `:consequent-schema` must stay structural and declared up front. The graph is computed from it.
- `:denotation` specifies the intended effect and what would count as a bad but schema-valid result.
- `:executor` may be declarative, procedural, or LLM-backed.

That means the graph can remain valid even when some rules generate contextual content, and the denotational contract catches LLM outputs that are formally correct but cognitively wrong.

### Pattern conventions

The exact pattern language can stay simple at first: plain Clojure data with logic-variable-style symbols such as `?target`, `?goal`, and `?context`.

```clojure
{:fact/type :concern-trigger
 :concern-type :attachment-threat
 :target-ref ?target
 :affected-goal :preserve-possible-repair}
```

The kernel does not need a full logic-programming substrate to start. It only needs:

- structural equality for closed fields
- variable binding for open fields
- projection functions for indexing and graph connection

## Connection graph computation

The rule connection graph is computed from schemas, not from runtime outputs.

```clojure
EdgeBasisV1
{:from-rule keyword
 :to-rule keyword
 :from-projection pattern
 :to-projection pattern
 :bindings map
 :edge-kind #{:state-transition :goal-decomposition :emotion-trigger :repair-step}}
```

### Build algorithm

1. Normalize each rule's `:antecedent-schema` and `:consequent-schema` into comparable projections.
2. For every rule pair `A -> B`, test whether a projection from `A`'s consequent unifies with a projection from `B`'s antecedent.
3. If yes, create an `EdgeBasisV1`.
4. Cache outgoing and incoming edge bases on the two rules.

### Non-negotiable property

The graph stays structurally derived and traversable. Adaptive learning may rank paths or re-rank episode retrieval, but it should not create or delete graph edges based on usage frequency. That would turn serendipity into groove-following retrieval and damage the creative substrate.

## Execution contract for rules

All rule executors should satisfy one shared runtime contract.

```clojure
RuleCallV1
{:rule-id keyword
 :bindings map
 :active-context {:id keyword :facts [any]}
 :active-concern {:id keyword :goal-type keyword}
 :retrieved-episodes [{:id keyword :summary string}]
 :recent-state {:recent-indices [any]
                :recent-episodes [keyword]}}
```

```clojure
RuleResultV1
{:consequents [any]
 :confidence number
 :reason string
 :aux-indices [index-projection]
 :surface-summary string?}
```

### Dispatch model

1. Clojure finds candidate rules structurally.
2. Clojure binds the rule antecedent against local facts / goals / context.
3. Clojure builds a `RuleCallV1`.
4. Clojure dispatches on `(:kind (:executor rule))`.
5. The executor returns a `RuleResultV1`.
6. Clojure validates that `:consequents` conform to `:consequent-schema`.
7. Clojure writes the result into the current mechanism:
   - assert facts
   - create subgoals
   - create emotions
   - attach analogical guidance
   - emit explicit indices for later episode storage

### Executor kinds

`{:kind :instantiate}`

- Mueller-faithful default
- instantiate `:consequent-schema` directly from bindings

`{:kind :clojure-fn}`

- for explicit procedural consequents already present in Mueller
- function still returns `RuleResultV1`

`{:kind :llm-backed}`

- for bounded judgment or generation inside an otherwise structural loop
- model receives a typed `RuleCallV1`
- model returns typed data
- Clojure validates and owns all persistence

## Recursion ownership model

The recursive control structures stay in Clojure. The LLM appears only at leaf-level judgment points.

### 1. Reminding cascade

Structural loop:

1. retrieve episodes from explicit indices
2. add their indices to the recent-index FIFO
3. retrieve more episodes
4. avoid cycles with recent-episode tracking

Possible LLM call sites:

- should this reminded episode get attentional promotion?
- should this reminded episode be summarized or surfaced to narration?

What the LLM does not own:

- which episode is retrieved next
- cascade depth
- cycle prevention

### 2. Analogical repair

Structural loop:

1. copy or align a source subtree against a target goal
2. detect mismatch sites
3. choose the next repair target
4. recurse into repair, possibly with another retrieved episode

Possible LLM call sites:

- is this analogy apt enough to continue?
- is this mismatch repairable or should the analogy be abandoned?

What the LLM does not own:

- subtree transfer
- binding propagation
- recursive repair stack

### 3. Serendipity path verification

Structural loop:

1. search candidate rule paths through the connection graph
2. walk each path
3. progressively unify and construct a concrete partial plan
4. accept or reject the path

Possible LLM call sites:

- is this partial match semantically acceptable even if literal unification is weak?
- is this verified path useful enough to promote?

What the LLM does not own:

- graph traversal
- path bookkeeping
- backtracking across alternative paths

## Worked example A: Kai's unopened letter rule

This example is adapted from [authoring_time_generation_kai_letter_v1.yaml](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml), especially the benchmark theme rule `rule_unanswered_repair_bid` at lines 168-179 and the resulting concern instance at lines 203-213.

### Proposed rule object

```clojure
{:id :rule/unanswered-repair-bid
 :rule-kind :inference
 :mueller-mode :inference-only
 :antecedent-schema
 [{:fact/type :primitive
   :value :incoming_contact_from_estranged_target
   :target-ref ?target}
  {:fact/type :primitive
   :value :prior_rupture_unresolved
   :target-ref ?target}]
 :consequent-schema
 [{:fact/type :concern-trigger
   :concern-type :attachment-threat
   :target-ref ?target
   :affected-goal :preserve-possible-repair
   :temporal-status :prospective}]
 :plausibility 0.82
 :index-projections {:match [:incoming-contact-from-estranged-target
                             :prior-rupture-unresolved]
                     :emit [:concern/attachment-threat
                            :goal/preserve-possible-repair]}
 :executor {:kind :instantiate}
 :provenance {:book-anchors [{:fixture :kai-letter
                              :source-rule :rule_unanswered_repair_bid}]
              :kernel-status :proposed}}
```

### Why this rule is graphable

The graph does not care that the runtime target is Kai and the concrete target ref is `:sister`. It cares that this rule produces a `:concern-trigger` of type `:attachment-threat` aimed at some target and affecting a repair-related goal.

That means the rule can connect to downstream rules whose antecedents consume:

- `:concern-trigger` with `:concern-type :attachment-threat`
- repair-oriented affect or obligation rules keyed off the same target
- retrieval/indexing rules that turn concern-trigger structure into explicit episode cues

The executor can vary. The schema-level connective tissue cannot.

### Runtime behavior with `:instantiate`

Given the Kai fixture facts:

- `incoming_contact_from_estranged_target`
- `prior_rupture_unresolved`
- `?target = :sister`

Clojure instantiates:

```clojure
{:consequents
 [{:fact/type :concern-trigger
   :concern-type :attachment-threat
   :target-ref :sister
   :affected-goal :preserve-possible-repair
   :temporal-status :prospective}]
 :confidence 0.82
 :reason "direct-instantiation"}
```

Concern initiation then turns that trigger into an explicit persistent concern like the fixture's `cc_attachment_threat`. That concern later feeds retrieval over episodes such as `ep_last_rupture` and `ep_old_harbor_memory`, and any later stored episode can carry indices such as:

```clojure
[:rule/rule-unanswered-repair-bid
 :concern/attachment-threat
 :target/sister
 :goal/preserve-possible-repair
 :situation/sit-unopened-letter]
```

### Runtime behavior with `:llm-backed`

The same rule could be upgraded for softer interpretation without changing its graph role:

```clojure
(assoc rule
       :executor {:kind :llm-backed
                  :spec {:prompt :concern-trigger-v1
                         :temperature 0.2}})
```

Now Clojure still finds the rule structurally, but the model receives a bounded call:

```clojure
{:rule-id :rule/unanswered-repair-bid
 :bindings {'?target :sister}
 :active-context {:facts [{:fact/type :primitive
                           :value :incoming_contact_from_estranged_target}
                          {:fact/type :primitive
                           :value :prior_rupture_unresolved}]}
 :retrieved-episodes [{:id :ep_last_rupture
                       :summary "fight at the harbor six months ago"}]}
```

The model must still return the declared shape:

```clojure
{:consequents
 [{:fact/type :concern-trigger
   :concern-type :attachment-threat
   :target-ref :sister
   :affected-goal :preserve-possible-repair
   :temporal-status :prospective}]
 :confidence 0.88
 :reason "new contact from estranged sister threatens a possible repair bid"}
```

Clojure validates this against `:consequent-schema` before creating or updating the concern.

The gain is contextual judgment. The discipline is that the rule still emits structural concern-trigger data and therefore still participates in graph connection, episode indexing, and later retrieval.

## Worked example B: hidden blessing as an LLM-backed planning rule

This is a smaller example drawn from [17-rationalization-strategies.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/17-rationalization-strategies.md). It shows where an LLM-backed consequent is useful without making the rule graph opaque.

```clojure
{:id :rule/hidden-blessing
 :rule-kind :planning
 :mueller-mode :plan-only
 :antecedent-schema
 [{:fact/type :daydreaming-goal
   :goal-type :rationalization
   :strategy :hidden-blessing
   :failed-goal ?goal}
  {:fact/type :emotion
   :valence :negative
   :about-goal ?goal}]
 :consequent-schema
 [{:fact/type :imagined-situation
   :strategy :hidden-blessing
   :failed-goal ?goal
   :positive-outcome ?outcome}
  {:fact/type :emotion-shift
   :failed-goal ?goal
   :direction :reduce-negative}]
 :plausibility 0.45
 :index-projections {:match [:goal/rationalization
                             :strategy/hidden-blessing]
                     :emit [:imagined-situation
                            :emotion-shift/reduce-negative]}
 :executor {:kind :llm-backed
            :spec {:prompt :hidden-blessing-v1
                   :temperature 0.4}}}
```

This rule is graphable because the consequent still declares:

- that it produces an imagined situation
- that the situation is a hidden-blessing reframe
- that it yields an emotion-reduction effect

Other rules can connect to those declared products. The model only supplies the contextual content of `?outcome` and the concrete imagined scenario.

### Runtime contract

Clojure chooses the strategy structurally. The LLM does not decide whether the system is rationalizing or which strategy family is active.

Clojure asks:

```clojure
{:failed-goal {:id :g1 :objective :be-forgiven}
 :trigger-emotion {:affect :distress :strength 0.63}
 :strategy :hidden-blessing
 :retrieved-episodes [{:id :ep_last_rupture
                       :summary "the last direct talk ended badly"}]}
```

The model returns:

```clojure
{:consequents
 [{:fact/type :imagined-situation
   :strategy :hidden-blessing
   :failed-goal :g1
   :positive-outcome :forced-honesty-later}
  {:fact/type :emotion-shift
   :failed-goal :g1
   :direction :reduce-negative}]
 :confidence 0.61
 :reason "the failed contact now may preserve the chance for a less defensive future meeting"}
```

Clojure validates, stores the branch facts, and later lets episode evaluation decide whether this reframe is worth keeping.

## What should be built first

The condensation suggests a practical order:

1. `RuleV1` plus schema validation
2. derived rule-connection-graph construction
3. executor dispatch with `:instantiate` and `:clojure-fn`
4. `:llm-backed` executor for a narrow pilot rule
5. judgment-site integration inside one recursive mechanism, not all three at once

The most conservative pilot is:

- inference or planning remains structural
- one narrow LLM-backed rule is added
- the output is validated into explicit consequence facts
- the result is stored with explicit indices

That is enough to test the core claim: typed LLM judgment inside persistent graph-structured cognition.
