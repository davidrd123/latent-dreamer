Blunt version: the matcher is mostly fine. The graph criterion is the shaky part. The provenance bonus is backwards for retrieval. And the executor story is still mostly a design doc, not a runtime contract.

## 1. Matcher correctness in `rules.clj`

I think `rules/unify-node` and `rules/match-antecedent-schema` are basically correct for the contract the current tests encode.

`rules_test/match-rule-binds-shared-variables-across-facts` and `...respects-prebound-variables...` are good evidence that you have:

* subset matching for maps
* repeated-variable consistency
* prebound-variable consistency
* depth-first backtracking over distinct facts

So the core search is not where I’d spend panic budget.

The actual issues are the silent edges around that contract.

1. **Late failure for unbound consequent vars**

   * Symbol refs: `rules/valid-rule?`, `rules/instantiate-template`, `rules/instantiate-rule`
   * Problem: `valid-rule?` does not check that every logic var in `:consequent-schema` and `:index-projections` is bound by the antecedent or by expected initial bindings.
   * Risk: a rule can validate cleanly at load time and then explode only when it fires.
   * Missing test: a rule with consequent `'?x` not present in the antecedent should be rejected early, or at least explicitly tested as a runtime failure.

2. **Duplicate-match explosion**

   * Symbol ref: `rules/match-antecedent-schema`
   * Problem: the search returns all permutations of matching facts. If you have duplicate facts, or two antecedent slots that are symmetric, you can get duplicate binding-equivalent matches.
   * Risk: unstable behavior anywhere you later do `first` on matches, plus combinatorial blow-up.
   * Missing test: two identical matching facts, or two symmetric antecedent patterns, and assert whether duplicate logical matches are acceptable.

3. **“One antecedent slot = one distinct fact” is hardcoded**

   * Symbol refs: `rules/match-antecedent-schema`, `rules/remove-fact-at`
   * Problem: once a fact is used for one antecedent slot, it cannot satisfy another.
   * Risk: probably fine for your current fact model, but it is a real semantic choice, not an incidental implementation detail.
   * Missing test: explicitly assert that the same fact may not satisfy two slots.

4. **Vector semantics are much stricter than map semantics**

   * Symbol ref: `rules/unify-node`
   * Problem: vectors require exact type and exact length. Maps are partial/subset matches; vectors are not.
   * Risk: list vs vector, optional tail elements, or “contains these reasons” style data will silently fail.
   * Missing test: nested vector patterns with repeated vars, and list-vs-vector mismatch.

5. **Nested-map matching is better than your graph derivation**

   * Symbol ref: `rules/unify-node`
   * Important point: nested maps and repeated vars inside them should work fine in the matcher.
   * But that strength does **not** carry into `projection-basis`. More on that below.

So: the backtracking matcher itself looks sound$_{85%}$ for the intended narrow model. The bad news is that the graph layer is much weaker than the matcher and will misrepresent what the matcher can actually do.

## 2. Connection-graph construction

This is the weakest part.

Your current criterion is effectively:

> same `:fact/type` + no conflicting constant fields

That is okay as a **candidate adjacency heuristic** for the current tiny, flat, hand-authored family rules. `goal_families_test/activation-rules-register-cleanly...` and `...planning-rules-register-cleanly...` prove that.

It is not yet an honest general “structural connection graph” in the sense the docs claim.

1. **It over-connects because zero shared keys still produce an edge**

   * Symbol refs: `rules/compatible-projection-bases?`, `rules/connection-edge-basis`
   * You already compute `:shared-keys`, but you do not require it to be non-empty.
   * Example:

     * from: `{:fact/type :goal :goal-id ?g}`
     * to: `{:fact/type :goal :status :failed}`
     * Current result: edge exists, `shared-keys #{}`.
   * That is too loose. Sharing only `:fact/type` is not “overlap” in the sense of the design docs.
   * Risk: once the rule set broadens, the graph becomes a same-type cloud.
   * Missing test: assert that same-type / zero-shared-key pairs do **not** connect, if that’s the intended invariant.

2. **It under-connects on nested structure**

   * Symbol refs: `rules/projection-basis`, `rules/compatible-projection-bases?`
   * `projection-basis` only inspects top-level values for logic vars. Nested maps and vectors are treated as opaque constants.
   * Example:

     * from: `{:fact/type :rel :participants {:from ?x :to ?y}}`
     * to: `{:fact/type :rel :participants {:from ?a :to ?b}}`
   * Matcher verdict: structurally compatible.
   * Graph verdict today: probably **not** compatible, because the nested maps are compared as literal constants and `?x` ≠ `?a`.
   * Risk: the graph under-approximates the matcher once schemas stop being flat.
   * Missing test: nested antecedent/consequent schemas with renamed vars should still connect.

3. **Multiple edges between the same rule pair are ambiguous**

   * Symbol refs: `rules/connection-edges`, `rules/explain-path`
   * `connection-edges` can emit multiple edges for the same `from-rule`/`to-rule` pair if multiple projections match.
   * `explain-path` then picks the first edge whose `:to-rule` matches. It ignores projection identity.
   * Risk: path explanations become arbitrary as soon as you have multi-edge pairs.
   * Missing test: a pair of rules with two distinct compatible projection pairs, and assert which edge `explain-path` is supposed to report.

4. **`EdgeBasisV1 :bindings` is dead**

   * Symbol refs: `rules/connection-edge-basis`, `rules/bridge-paths`, `rules/explain-path`
   * The docs say edges should support progressive unification and step-by-step verification.
   * Current code always stores `:bindings {}`.
   * So the graph knows “these two projections don’t obviously clash,” but it does not know how variables map across the edge.
   * Risk: path search is topology over rule ids, not verified structural flow.

5. **You are treating partial-support edges as if they were traversable transitions**

   * Symbol refs: `rules/connection-edge-basis`, `rules/bridge-paths`, `episodic-memory/provenance-bonus-info`
   * This is the big cross-file issue.
   * An edge exists if **one consequent projection** is compatible with **one antecedent projection**.
   * That does **not** mean the target rule is actually fireable. It may still require other antecedents not produced anywhere on the path.
   * The test suite already shows this. `rules_test/build-connection-graph-creates-structural-edges-only-for-compatible-rules` creates `:test/source -> :test/target` even though `:test/target` also requires an emotion fact not supplied by `:test/source`.
   * That is fine for candidate search.
   * It is **not** fine if later code uses `bridge-paths` as evidence of real structural continuity. `episodic_memory` currently does exactly that.

My bottom line here: the current graph is acceptable as a **candidate graph**. It is overstated as a structural traversal graph.

## 3. Provenance-weighted retrieval

The current policy is deliberate. I still think it is wrong for retrieval.

`episodic_memory_test/retrieve-episodes-prefers-deeper-graph-bridges` explicitly locks in:

* deeper bridge → larger bonus
* deeper bridge outranks shallower bridge

That is not an accident. It is a design choice. I think it is the wrong one.

1. **You are rewarding remoteness over closeness**

   * Symbol refs: `episodic-memory/best-bridge-candidate`, `episodic-memory/provenance-bonus-info`
   * Shared edge = `1.0`
   * Shared rule = `1.0`
   * 2-hop bridge = `2.0`
   * 3-hop bridge = `3.0`
   * So an inferred 3-hop relation beats a literal shared edge.
   * That is backwards for relevance.

2. **Depth here is not evidence strength**

   * Symbol refs: `episodic-memory/bridge-candidates`, `.../best-bridge-candidate`
   * Depth is partly a function of:

     * graph size
     * graph permissiveness
     * how long a stored `:rule-path` is
     * which start point inside `active-rule-path` and `episode-rule-path` happens to win
   * So longer provenance histories can get bigger bonuses by construction.

3. **Because the graph already over-connects, deeper paths are where false positives accumulate**

   * This compounds the previous issue.
   * The farther out you go, the less honest the bridge becomes under the current edge criterion.

4. **Tie-breaks are arbitrary**

   * Symbol ref: `episodic-memory/best-bridge-candidate`
   * After depth, you sort by printed rule-path and then stringified direction.
   * That is not semantic ranking. It is deterministic noise.

I’d separate two things you currently collapse:

* **relevance/confidence**: should favor shared-edge, shared-rule, and shorter verified bridges
* **serendipity/novelty**: can favor deeper or less obvious bridges

Right now you use depth as positive retrieval strength. I’d do the opposite:

* direct overlap strongest
* 1-hop bridge weaker
* 2-hop weaker
* 3-hop+ maybe novelty signal only, not core retrieval bonus

Otherwise you will retrieve the weirdly connected thing instead of the actually close thing.

Missing tests:

* direct shared-rule vs 2-hop bridge
* shared-edge vs 3-hop bridge
* same episode with both a shallow and deep bridge
* whether `:active-to-episode` should outrank `:episode-to-active`

## 4. Caching at `goal_families/ranked-roving-episode-ids`

The immediate rebuild is cheap now and wrong-layer later.

1. **Do not cache inside `ranked-roving-episode-ids`**

   * Symbol ref: `goal-families/ranked-roving-episode-ids`
   * The graph is not a property of roving ranking. It is a property of the rule registry.
   * Correct ownership is: rule registry or caller computes graph once, retrieval/ranking consumes it.

2. **A naive global cache would be wrong**

   * Why: your tests and code use dynamic rule sets, e.g. `with-redefs` on `cross-family-rules`, rule removal via `intervention-delta`, and future hot-reload/editor workflows.
   * If you memoize a single global `family-rules` graph, you will silently score episodes against stale structure.

3. **Correct cache key = structural rule-set signature**

   * At minimum: rule id + antecedent schema + consequent schema.
   * Full-rule-value memoization is okay too, just noisier.
   * Better still: carry an explicit immutable registry object like `{::rules ..., ::graph ..., ::version ...}`.

4. **The next bottleneck is not graph build, it is repeated path search**

   * Call chain: `ranked-roving-episode-ids` → `episodic/episode-provenance-info` → `bridge-candidates` → `rules/bridge-paths`
   * As episodes grow, repeated bridge search per candidate will dominate.
   * So the real scaling story is:

     * cache graph per registry
     * precompute reachable/best-bridge info per active rule path for the retrieval batch

5. **Hardcoding `family-rules` here is itself a future bug**

   * Symbol ref: `goal-families/ranked-roving-episode-ids`
   * Today this works because all structural provenance you care about is in `family-rules`.
   * Tomorrow the engine will have a wider rule base.
   * This function should accept a graph or a provenance scorer, not call `family-rules` directly.

Missing tests:

* cache invalidation when `cross-family-rules` changes
* same retrieval batch should reuse one graph snapshot
* old episode provenance against new registry version, if cross-session rule evolution matters

## 5. Future executor readiness

This is the biggest design-doc / code gap.

The docs describe:

`RuleCallV1 -> executor -> RuleResultV1 -> schema validation -> persistence`

The code does not do that yet. It does:

`bindings -> instantiate template -> take first consequent`

That difference matters.

1. **There is no generic dispatch/validation boundary**

   * Symbol refs: `rules/valid-rule?`, `rules/instantiate-rule`
   * `:clojure-fn` and `:llm-backed` are accepted by validation, but there is no runtime machinery for them.
   * `:denotation` and `:validation-fn` are currently inert.
   * Risk: once non-instantiate executors exist, schema and denotation can drift from actual outputs with no enforcement.

2. **`goal_families` is instantiate-biased all over**

   * Symbol refs:

     * `goal-families/emit-rule-fact`
     * `goal-families/instantiate-derived-fact`
     * `goal-families/plan-request-facts-from-ready-facts`
     * `goal-families/activation-candidates-from-trigger-facts`
     * `goal-families/plan-payloads-from-request-facts`
   * All of these assume:

     * antecedent bindings are enough
     * the rule can be executed locally with `instantiate-rule`
     * the relevant output is `(first :consequents)`
   * That will not generalize to `:clojure-fn` or `:llm-backed`.

3. **Provenance is being attached to template placeholders, not validated runtime outputs**

   * Symbol refs: `goal-families/emit-rule-fact`, `.../instantiate-derived-fact`, `episodic-memory/create-episode`
   * For future executors, provenance has to be stamped on the admitted `RuleResultV1` consequents after validation.
   * Otherwise stored `:rule-path` and `:edge-path` will describe intended rule flow, not actual outputs.

4. **Your procedural plan rules already show the mismatch**

   * Symbol refs:

     * `goal-families/rationalization-plan-dispatch-rule`
     * `goal-families/rationalization-plan`
     * `goal-families/reversal-plan-dispatch-rule`
     * `goal-families/reversal-aftershock-fact`
   * Example: `rationalization-plan-dispatch-rule` declares a `:family-affect-state` consequent, but `rationalization-plan` later computes and asserts a richer affect-state fact procedurally.
   * So even now, the schema is partly interface sketch, partly actual emitted fact.
   * Decide which it is. Right now it is muddled.

5. **`RuleResultV1` fields are mostly ignored**

   * Symbol ref: `rules/instantiate-rule`
   * `:confidence`, `:reason`, and `:aux-indices` come back from `instantiate-rule`.
   * The selected integration code does not really consume them.
   * That means the “shared runtime contract” in the docs is not actually the contract yet.

6. **Edge provenance shape is not canonical**

   * Symbol refs:

     * `goal-families/extend-rule-provenance`
     * `rules/explain-path`
     * `episodic-memory/comparable-edge`
   * `goal_families` edge provenance is `{from-rule to-rule fact-type edge-kind}`.
   * `rules/explain-path` edges are `{from-rule to-rule from-projection to-projection shared-keys edge-kind}`.
   * `episodic_memory` only knows the first shape.
   * Future generic dispatch needs one canonical edge provenance format.

7. **Minor but real: `:graph-cache` is required even though the docs say derived**

   * Symbol refs: `rules/required-rule-keys`, `rules/derive-graph-cache`
   * This is the wrong ownership boundary.
   * Callers should not need to hand-author empty `:graph-cache {}` maps for valid rules.

## What I’d fix first

1. **Decide what the graph means**

   * candidate adjacency graph, or verified structural path graph
   * right now the docs describe the second, the code implements the first

2. **Stop rewarding deeper bridges as stronger retrieval evidence**

   * keep depth as a serendipity signal if you want
   * do not let it outrank direct overlap

3. **Tighten edge construction**

   * at minimum, no edge when `shared-keys` is empty
   * then add tests for nested structures and multi-edge pairs

4. **Move graph ownership out of `ranked-roving-episode-ids`**

   * inject registry/graph
   * cache by structural rule-set signature

5. **Build the actual executor boundary before adding `:clojure-fn` or `:llm-backed`**

   * generic dispatch
   * result validation against schema
   * provenance stamped after admission, not before

If you want the shortest summary possible: **the matcher is decent, the graph is too permissive, the bridge bonus is upside down, and the executor abstraction is still mostly aspirational.**
