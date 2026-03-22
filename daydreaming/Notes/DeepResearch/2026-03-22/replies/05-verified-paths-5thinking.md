You’re one layer away, and it is not another graph algorithm. It is a witness layer.

Right now `bridge-paths` gives you candidate adjacency paths: rule sequences justified by schema overlap. That is useful, and you should keep it. But `verified` has to mean something stronger and state-relative.

## 1. What “verified” should mean

A path should count as **verified only relative to a seed state/context**, not as a property of the graph alone.

My recommended definition:

A path is verified iff there exists an **anchored witness trace** such that:

1. one binding environment is propagated through the whole path,
2. each rule’s **full antecedent** is satisfiable at its step,
3. at least one antecedent fact at step `i+1` is the **concrete fact emitted by step `i`** along the chosen edge,
4. the rule’s declared consequents can be materialized for that witness,
5. any opaque executor steps are either deferred explicitly or validated against denotation.

That is the key distinction:

* **candidate path** = “these schemas could connect”
* **verified path** = “in this state, with these bindings and supports, this chain actually has a witness”

So “verified” is not graph-global. It is query-time and context-sensitive.

## 2. Progressive binding only, or full downstream antecedents?

Not just binding propagation.

If you only propagate bindings through the shared edge projections, you have not really verified much. You have shown that one projected consequent can unify with one projected antecedent. That is stronger than current candidate adjacency, but it is still not an executable chain.

Planning validation literature is unambiguous about the stronger notion: a plan is executable only when each step’s preconditions hold in the current state, yielding a trace; a plan is valid when that executable trace reaches a goal. ([ResearchGate][1])

So I’d split this cleanly:

* **`:bound`** or **`:coherent`**: progressive binding propagation across the edge interfaces only
* **`:verified`**: full downstream antecedents satisfied at each step, under a bounded witness store

That bounded witness store should be:

* ambient seed facts from the active context / goal / episode
* facts emitted by earlier verified steps
* optional bottom-goal or episode support facts

It should **not** require that every antecedent be produced strictly by the previous rule. Your current family rules already show why. For example, candidate bridges into `rationalization-plan-request` structurally exist even though fields like `goal-id`, `context-id`, and `ordering` are not emitted by the preceding `family-plan-ready` slice. In `goal_families.clj`, those are supplied from ambient goal/context machinery, not from pure path-local emission. So the right test is:

> full antecedents must be satisfiable from `ambient ∪ derived`, with an anchor to the carried path fact.

Not:

> every antecedent literal must be path-self-generated.

That second criterion is too strict and would reject paths your architecture intentionally wants to allow.

## 3. Should Mueller-faithful verification also construct a concrete episode / planning tree?

Yes.

If you want to say “Mueller-faithful verification,” then the answer is plainly yes. In DAYDREAMER, verification is not just checking. It is **progressive unification while constructing the episode/planning tree**. The constructed episode is the witness.

So I’d separate two milestones:

### First shippable verifier

Build a **witness trace**:

* step-by-step bindings
* matched antecedent facts
* emitted consequent facts
* failure point or success
* maybe a light episode skeleton

### Mueller-faithful verifier

Build the **concrete planning tree / episode** while walking:

* instantiated goals/subgoals
* subgoal slot chosen at each hop
* propagated bindings
* resulting episode object suitable for later analogical use

The first is enough to upgrade from “candidate” to “actually supported in context.”
The second is what turns verification into **new reusable cognitive structure**, which is what Mueller cared about.

So: first pass can stop short of full episode construction, but the faithful destination includes it.

## 4. How to represent partial verification

Do not collapse partial verification to one float too early. That loses the exact thing you need for debugging and later evaluator work.

Use a status lattice like this:

* `:candidate`
  structural adjacency only

* `:bound`
  edge-level bindings propagate consistently

* `:supported`
  full antecedents satisfied at every verified step

* `:constructed`
  supported, plus episode/planning-tree witness built

* `:validated`
  constructed, plus all opaque executor obligations checked

And represent partial results as a trace, not just a score:

```clojure
{:status :partial
 :rule-path [...]
 :verified-prefix-length 3
 :failed-at-rule :goal-family/rationalization-plan-request
 :failure-kind :missing-ambient-support
 :bindings {...}
 :step-trace [...]
 :open-obligations [...]
 :episode-skeleton ...}
```

If you need ranking, use a tuple or lexicographic score, not a fake single confidence:

* verified prefix length
* satisfied antecedent ratio
* deferred obligation count
* opaque executor count
* constructed node count

That gives you something sortable without pretending uncertainty is one number.

## 5. Prior art that is actually relevant

Most “graph reasoning” prior art is not the right comparison. The relevant material is narrower.

### a) Semantic attachments

This matters once path verification touches `:clojure-fn` or `:llm-backed` executors.

The lesson from Dornhege et al. is sharp: soundness/completeness arguments only go through under conditions like termination, deterministic behavior for identical inputs/states, and no hidden choice among multiple outcomes. They say effect applicators should not make choices between different outcomes, and conditional soundness/completeness is only discussed under those assumptions. ([AAAI][2])

That maps directly onto your verifier:

* `:instantiate` rules can be proof-like
* procedural or LLM-backed rules are **not** proof-like by default
* they need either deferred-obligation status or denotational validators

So semantic attachments do **not** justify calling an LLM-backed hop “verified” just because it returned schema-valid output. They justify the opposite: be stricter.

### b) PDDLStream

This is relevant for the split between declarative structure and black-box witness generation. PDDLStream keeps the declarative skeleton, treats implementations as black boxes, reduces problems to a sequence of finite symbolic problems, and explicitly balances exploring candidate plans vs exploiting promising ones. ([ICAPS 2020][3])

The right lesson is:

* black-box procedures can help propose bindings/witnesses
* the planner still owns the search and verification envelope

That is much closer to your needs than generic “neuro-symbolic” talk.

### c) HTN semantic attachments

This is closer than classical semantic attachments because it explicitly uses semantic attachments with **semi-coroutines** and backtracking, linking external procedures to custom unifications during decomposition. ([OpenReview][4])

That is a real analogue for your recursive verifier:

* suspend on an external obligation
* resume with bindings / support
* backtrack if it fails

### d) Plan validation

VAL is relevant because it pins down what “valid” means operationally: executable trace first, goal satisfaction second. Each action’s preconditions must hold in the state where it fires, and an executable plan induces a trace. ([ResearchGate][1])

That is why I’m pushing you toward **state-relative witness traces** rather than path-plausibility.

### e) What is not especially relevant

* GraphRAG
* embedding retrieval
* generic path ranking over KGs
* argument-map UI work

Those are useful elsewhere in your project, but not for “did this rule path actually verify?”

## 6. Smallest conceptual and code delta from candidate to verified

The smallest delta is **not** changing graph construction. It is adding a second phase over the existing candidate paths.

### Keep unchanged

* `build-connection-graph`
* `reachable-paths`
* `bridge-paths` as candidate search
* current use of candidate bridges in `episodic_memory` provenance bonus

That consumer only needs candidate proximity. Don’t break it.

### Add

A sibling layer:

* `verify-path`
* `verified-bridge-paths`

Conceptually:

```clojure
(->> (bridge-paths graph start target opts)
     (map #(verify-path graph % seed-state verify-opts))
     ...)
```

That is the clean cut.

### One small but important fix

A verified path cannot be only a `:rule-path`.

It needs the **specific edge instances** used. Right now `explain-path` reconstructs edges from just rule IDs and grabs the first outgoing edge matching `to-rule`. If there are multiple compatible edges between the same two rules, that is under-specified. For candidate provenance this is usually fine. For verification it is not.

So the smallest real delta is:

1. make path search carry an `:edge-path`, not just a `:rule-path`
2. run verification over that concrete edge path

You already have most of the pieces:

* `from-projection`
* `to-projection`
* `match-rule`
* `instantiate-rule`

You do **not** need to redesign the graph first.

### Verification walk

At each step:

1. take the chosen incoming edge
2. instantiate the prior step’s emitted fact
3. unify that fact against the next rule’s relevant antecedent projection
4. match the **full next antecedent** against `ambient ∪ derived`, respecting propagated bindings
5. instantiate consequents
6. add emitted facts to the derived witness store
7. optionally extend an episode skeleton / planning tree

That is it.

### What not to do

Do not redefine `bridge-paths` to suddenly mean “verified.” That will tangle two distinct layers and break your current provenance use.

Instead:

* `bridge-paths` = candidate structural bridges
* `verified-bridge-paths` = state-relative witness-backed bridges

## 7. My actual recommendation

Ship this in three honest stages:

### Stage 1

Add **`:bound`** verification only.
Cheap, useful, and good for debugging edge interfaces.

### Stage 2

Add **`:supported`** verification.
This is the first layer I’d be willing to call “verified” in normal kernel language.

### Stage 3

Add **`:constructed`** verification.
That is the Mueller-faithful version and the one that should feed analogical-plan insertion.

If you try to jump straight from current `bridge-paths` to full Mueller episode construction in one move, you’ll build too much at once and blur the semantics. If you stop at binding propagation and call it verified, you’ll be lying to yourself.

The clean line is:

* candidate graph stays structural
* verification is query-time, state-relative, and witness-bearing
* Mueller-faithful verification constructs the episode while proving the path

That is the right seam.

[1]: https://www.researchgate.net/publication/4114491_VAL_Automatic_plan_validation_continuous_effects_and_mixed_initiative_planning_using_PDDL "(PDF) VAL: Automatic plan validation, continuous effects and mixed initiative planning using PDDL"
[2]: https://cdn.aaai.org/ocs/ws/ws0614/2056-8460-1-PB.pdf "Integrating Task and Motion Planning Using Semantic Attachments"
[3]: https://icaps20.icaps-conference.org/paper186.html "PDDLStream: Integrating Symbolic Planners and Blackbox Samplers via Optimistic Adaptive Planning – ICAPS 2020"
[4]: https://openreview.net/forum?id=BkxAIrBZtE "HTN Planning with Semantic Attachments | OpenReview"
