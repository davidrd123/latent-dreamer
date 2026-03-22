My take: do **not** collapse candidate paths and verified paths into one thing.

Right now your graph layer is doing exactly what it should: a cheap structural overapproximation. In the code you pasted, `build-connection-graph` derives adjacency from schema overlap, `reachable-paths` walks that adjacency, `bridge-paths` filters it, and `explain-path` annotates it. None of that is verification. Good. Keep it that way.

## What “verified” should mean

A **candidate path** means: “these rules could connect structurally.”

A **verified path** should mean something stronger and concrete:

> there exists **one consistent binding environment** carried across the whole rule path, starting from a concrete goal/fact context, such that each hop’s selected emitted structure can instantiate the next hop’s selected required structure, **and** the verifier has built the corresponding concrete subgoal/episode spine while walking the path.

That is the witness you’re currently missing.

I’d make the levels explicit:

* `:candidate` — current `bridge-paths`
* `:projection-verified` — progressive binding propagation succeeds across the chosen hop projections
* `:episode-constructed` — same, plus a concrete episode/planning-tree spine was built
* `:grounded` — non-bridge antecedent obligations are also discharged against current facts / support context / planner
* `:sound-under-executors` — same, plus executor/denotation checks passed for procedural or LLM-backed rules

If you call the first stronger layer merely “verified”, fine, but then admit it is **path-verified**, not globally executable.

## First pass: progressive binding propagation or full downstream satisfiability?

First pass should be **progressive binding propagation**, not full downstream satisfiability of every antecedent in every downstream rule.

Requiring full downstream satisfiability now would be the wrong cut for three reasons:

1. It turns verification into a much bigger planning problem.
2. Your current RuleV1 graph substrate does not encode enough to prove full executability cheaply.
3. Mueller’s own mechanism was not “prove the whole downstream tree is already satisfiable”; it was “verify applicability of this path to the current concrete goal by progressive unification,” and then repair missing parts through regular planning when needed.  

So the first-pass verifier should check:

* a concrete start goal / fact context
* a running bindings map
* for each hop, the concretized producer projection unifies with the consumer projection under those bindings
* binding conflicts kill the path
* uninstantiable bridge subgoals kill the path

What it should **not** require in v1 is proving that every extra antecedent fact in the consumer rule is already satisfied. Those should be carried as **open obligations**.

That said, don’t lie to yourself: with multi-fact antecedents, projection-level verification alone is weaker than executability. So represent the missing obligations explicitly.

## Mueller-faithful verification should build an episode/planning tree

Yes. If you want Mueller-faithful verification, it should also **construct a concrete episode/planning tree while walking the path**.

If you skip episode construction, you have not rebuilt Mueller’s verifier. You have built a stronger path typechecker.

The selected Chapter 5/7 excerpts and `ri-path->episode` / `ri-path->episode1` are pretty clear about this: verification is not just checking compatibility; it is progressive unification **while constructing** the concrete episode. The analogical-planning chapter also makes clear that a retrieved source tree is a suggestion that gets repaired when parts do not apply.  

So I’d make episode construction part of the verifier contract, even if the first version builds only a **skeleton**:

* instantiated rule path
* instantiated connecting subgoals / facts
* chosen subgoal index per hop
* bindings introduced at each hop
* open obligations not yet discharged

## How to represent partial verification

Do **not** reduce it to one float. That will hide the only information you actually need.

Use a structured result, something like:

```clojure
{:rule-path [...]
 :verification-level :projection-verified
 :verified-prefix-length 3
 :total-hops 5
 :bindings {...}
 :hop-results
 [{:hop 0
   :from-rule ...
   :to-rule ...
   :status :ok
   :bindings-delta {...}
   :open-obligations [...]}
  {:hop 1
   :status :binding-conflict
   :blocked-on '?frame-id}]
 :episode-skeleton [...]
 :open-obligations [...]
 :failure-reason nil}
```

If you need ranking, use a **lexicographic score**, not a fake scalar:

1. verification level
2. verified prefix length / total length
3. number of open obligations
4. number of bound variables
5. path depth / desirability heuristics

That preserves diagnosis.

## What prior art actually matters

### 1. Mueller / DAYDREAMER

This is the main target behavior. Candidate search first, then verify applicability to the current concrete goal by unification, with analogical repair when source material only partially applies.  

### 2. Semantic attachments

Relevant, but only at the **executor boundary**.

The point from Dornhege et al. is sharp: planner-level conditional soundness/completeness survives only under assumptions like termination, determinism for identical parameters/states, and no hidden choice among multiple materially different outcomes by effect applicators. If those conditions fail, a structurally verified symbolic path does **not** buy you planner guarantees. ([AAAI][1])

That maps directly to your `:clojure-fn` / `:llm-backed` future:

* schema verification says the path shape fits
* denotational verification says the intended state change actually happened
* executor verification says the external procedure stayed within the contract

If the executor can secretly choose among different consequents, you no longer have semantic-attachment-style guarantees. That is exactly why your schema / denotation / executor split matters. ([AI University of Basel][2])

### 3. PDDLStream

Relevant as an architecture precedent, not as the definition of “verified path”.

It matters because it keeps a **declarative skeleton** while treating specialized procedures as black boxes, reducing problems to finite planning subproblems and balancing exploration of new candidate plans with exploitation of existing ones. ([arXiv][3])

That is useful for how you stage verification and optimistic search. It is not the core precedent for Mueller-style path verification.

### 4. Mostly irrelevant prior art

GraphRAG, ORKG, Tana, Graphologue, discourse-graph authoring systems: mostly orthogonal. They are about prose↔graph coupling, authoring, or retrieval surfaces, not executable symbolic path verification. Useful elsewhere in your project, not here.  

## Smallest conceptual/code delta from candidate to verified

This is the high-value move:

### Do not change `bridge-paths`.

Leave it candidate-only.

Why:

* `episodic_memory.clj` is using it as a cheap provenance bonus
* the current tests in `rules_test.clj`, `episodic_memory_test.clj`, and `goal_families_test.clj` assume that cheap structural meaning
* making it context-sensitive and expensive would muddle two different layers

### Add one new layer on top

1. `bridge-paths`
   returns candidate rule paths exactly as now

2. `explain-path`
   stays structural, or gains tiny hop metadata like projection indices

3. `verify-path`
   new function: `(verify-path graph rule-path verification-context) -> PathVerificationV1`

4. optionally `verified-bridge-paths`
   wrapper = `bridge-paths` + `verify-path` + filter/rank

That is the smallest honest delta.

### What `verify-path` should reuse

You already have the pieces:

* `logic-var?`
* `unify-node`
* `match-fact-pattern`
* `instantiate-template`

The verifier can walk the `explain-path` hops, unify `from-projection` with `to-projection` under running bindings, instantiate the next concrete bridge object, and accumulate an episode skeleton.

### One tiny graph metadata fix

I’d add `:from-projection-idx` and `:to-projection-idx` to `connection-edge-basis`.

Right now `from-projection` / `to-projection` are stored, but not which schema position they came from. If a rule has multiple same-shaped projections, your verifier/episode builder can get ambiguous. That is a real nit, not decorative.

## Bottom line

The clean model is:

* **graph** = structural possibility space
* **candidate path** = graph path
* **verified path** = graph path + binding witness + episode spine
* **grounded path** = verified path + antecedent obligations discharged
* **sound path** = grounded path + executor/denotation contract satisfied

So the immediate move is not “make `bridge-paths` smarter.”
It is “add `verify-path` as a second pass and keep the current graph cheap.”

That preserves the current provenance/ranking uses, matches Mueller much better, and gives you a real place to hang future denotational and executor checks.

[1]: https://cdn.aaai.org/ocs/ws/ws0614/2056-8460-1-PB.pdf "Integrating Task and Motion Planning Using Semantic Attachments"
[2]: https://ai.dmi.unibas.ch/papers/dornhege-et-al-icaps2009.pdf "ICAPS09.dvi"
[3]: https://arxiv.org/abs/1802.08705#:~:text=,world%20robotic%20tasks "[1802.08705] PDDLStream: Integrating Symbolic Planners and Blackbox Samplers via Optimistic Adaptive Planning"
