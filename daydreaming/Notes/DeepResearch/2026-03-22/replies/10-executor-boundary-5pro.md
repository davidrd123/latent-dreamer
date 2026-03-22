The next step is smaller than it looks:

Add one real runtime boundary in `rules.clj`, route `goal_families.clj` through it, and leave real `:clojure-fn` / `:llm-backed` behavior for later. Right now `goal_families.clj` is treating `rules/instantiate-rule` as if it were the rule engine. That is the wrong seam. The seam should be:

`match rule → build RuleCallV1 → dispatch executor → validate RuleResultV1 → stamp provenance on admitted outputs`

That also matches your own broader `propose → validate → admit` pattern. The executor proposes. Validation admits. Persistence and provenance happen after that, not before. 

## 1. What the generic dispatch function should look like

Call it `execute-rule`, not `instantiate-rule`. `instantiate-rule` is the compatibility wrapper.

You want two layers:

```clojure
(defn execute-rule
  ([rule bindings]
   (execute-rule rule {:bindings bindings}))
  ([rule {:keys [bindings] :as call}]
   (let [rule (validate-rule! rule)
         call (-> call
                  (update :bindings #(or % {}))
                  (assoc :rule-id (:id rule)))
         raw-result (dispatch-executor rule call)]
     (-> raw-result
         (validate-rule-result-shape! rule call)
         (validate-consequents! rule call)
         (validate-denotation! rule call)))))

(defn- dispatch-executor
  [rule call]
  (case (get-in rule [:executor :kind])
    :instantiate (instantiate-executor-result rule call)
    :clojure-fn  (invoke-clojure-executor rule call)
    :llm-backed  (invoke-llm-executor rule call)))
```

And then keep this public wrapper for backward compatibility:

```clojure
(defn instantiate-rule
  [rule bindings]
  (when-not (= :instantiate (get-in rule [:executor :kind]))
    (throw (ex-info "instantiate-rule only supports :instantiate executors"
                    {:rule-id (:id rule)
                     :executor-kind (get-in rule [:executor :kind])})))
  (execute-rule rule {:bindings bindings}))
```

That buys you three things immediately:

1. `rules_test.clj` keeps passing unchanged, because `instantiate-rule` still returns the current shape.
2. `goal_families.clj` can stop knowing which executor kind a rule uses.
3. You get one place to insert runtime validation before any output is admitted.

Also add one ergonomic helper, because `goal_families.clj` currently does `match-rule` and then hand-runs instantiation everywhere:

```clojure
(defn matched-rule-applications
  ([rule facts]
   (matched-rule-applications rule facts {} {}))
  ([rule facts initial-bindings call-base]
   (mapv (fn [{:keys [bindings matched-facts]}]
           (let [call (merge call-base
                             {:rule-id (:id rule)
                              :bindings bindings
                              :matched-facts matched-facts})]
             {:bindings bindings
              :matched-facts matched-facts
              :call call
              :result (execute-rule rule call)}))
         (match-rule rule facts initial-bindings))))
```

That is the honest replacement for the current `match-rule` + `instantiate-rule` pairing in `goal_families.clj`.

## 2. How `RuleResultV1` should be validated against `:consequent-schema`

Be strict now. Loose validation will come back and bite you the moment an executor is not pure template fill.

### Shape validation

Add `validate-rule-result-shape!` with these checks:

* result is a map
* required keys are present: `:consequents`, `:confidence`, `:reason`, `:aux-indices`, `:surface-summary`
* `:consequents` is a vector
* `:confidence` is numeric and in `[0.0, 1.0]`
* `:reason` is a string
* `:aux-indices` is a vector
* `:surface-summary` is `nil` or string

Do not overcomplicate it.

### Consequent validation

This is the main missing piece.

Use the existing matcher. You already have it. The trick is: validate with the original bindings as `initial-bindings`, otherwise a future executor can emit schema-shaped garbage that silently rebinds variables.

This is the core check:

```clojure
(defn- validate-consequents!
  [rule call {:keys [consequents] :as result}]
  (let [schema (:consequent-schema rule)
        bindings (:bindings call)]
    (when (not= (count schema) (count consequents))
      (throw (ex-info "RuleResultV1 consequent count mismatch"
                      {:rule-id (:id rule)
                       :expected-count (count schema)
                       :actual-count (count consequents)
                       :schema schema
                       :consequents consequents})))
    (when-not (seq (match-antecedent-schema schema consequents bindings))
      (throw (ex-info "RuleResultV1 consequents do not satisfy consequent-schema"
                      {:rule-id (:id rule)
                       :bindings bindings
                       :schema schema
                       :consequents consequents})))
    result))
```

That does two necessary things:

* it enforces full coverage of the declared schema
* it enforces binding consistency with the antecedent match

The second one matters more than people think. Without initial bindings, an executor can emit `:goal-id :wrong-goal`, still fit the schema, and pass.

### Why count equality is the right rule now

Use exact count equality for `:consequents` vs `:consequent-schema`.

Do not invent variable-cardinality consequents yet. None of the current rules need them. Current authored rules are fixed-arity. Current tests assume fixed-arity. Keep it simple.

Subset matching at the map level is already enough flexibility for richer future consequents. A `:clojure-fn` rule can emit a richer fact map and still satisfy a lean schema pattern.

### Aux indices

I would only shape-check `:aux-indices` in this step.

You are not consuming them yet. Do not create a second overengineered validator for a field with no callers. Later, if you start using `:index-projections :emit` operationally, validate them the same way.

## 3. What a real `:denotation/:validation-fn` contract should be

Right now `:denotation` is mostly descriptive metadata. Make it real, but keep it narrow.

### What it is

A pure postcondition checker over `{rule, call, result}`.

Not a schema validator. That already happened.

Not a persistence hook.

Not a world mutator.

Its job is to catch schema-valid but semantically wrong outputs.

### Contract

I would make `:validation-fn` return a vector of failure keywords, where:

* `[]` means success
* non-empty vector means denotational failure
* every returned keyword must be declared in `[:denotation :failure-modes]`

Example:

```clojure
{:denotation
 {:intended-effect :dispatch-rationalization-plan-request
  :failure-modes [:missing-family-plan-request
                  :missing-hope-transition
                  :binding-drift]
  :validation-fn
  (fn [{:keys [call result]}]
    (let [consequents (:consequents result)
          affect-fact (some #(when (= :family-affect-state (:fact/type %)) %) consequents)
          trigger-strength (get-in call [:bindings '?trigger-emotion-strength])]
      (cond-> []
        (nil? affect-fact)
        (conj :missing-hope-transition)

        (and affect-fact
             (not= :hope (:affect affect-fact)))
        (conj :missing-hope-transition)

        (and affect-fact
             (not= trigger-strength
                   (:trigger-emotion-strength affect-fact)))
        (conj :binding-drift))))}}
```

And the runtime side:

```clojure
(defn- validate-denotation!
  [rule call result]
  (if-let [vf (get-in rule [:denotation :validation-fn])]
    (let [failures (vec (vf {:rule rule :call call :result result}))
          allowed  (set (get-in rule [:denotation :failure-modes]))]
      (when-not (every? allowed failures)
        (throw (ex-info "validation-fn returned undeclared failure mode(s)"
                        {:rule-id (:id rule)
                         :returned failures
                         :allowed allowed})))
      (when (seq failures)
        (throw (ex-info "RuleResultV1 failed denotational validation"
                        {:rule-id (:id rule)
                         :failures failures
                         :call call
                         :result result})))
      result)
    result))
```

### Why this contract is good

It matches the data you already have in `goal_family_rules.clj`.

It gives you named failure modes instead of boolean mush.

It separates structural failures from semantic failures:

* structural failures belong to `rules.clj`
* semantic failures belong to the rule’s denotation

That is clean.

### What to validate in `valid-rule?`

Strengthen `valid-rule?` now:

* `:denotation` must contain `:intended-effect`, `:failure-modes`, `:validation-fn`
* `:failure-modes` must be a vector of keywords
* `:validation-fn` must be `nil` or `ifn?`

At the moment, `valid-rule?` only checks that `:denotation` is a map. That is too loose.

## 4. What must change in `goal_families.clj`

The short version:

`goal_families.clj` should never call `rules/instantiate-rule` directly again.

The choke points are obvious:

* `emit-rule-fact`
* `instantiate-derived-fact`
* `activation-candidates-from-trigger-facts`
* `plan-request-facts-from-ready-facts`
* `plan-payloads-from-request-facts`

Replace the first two helpers. Everything else gets simpler automatically.

### Replace these helpers

Current problem:

* they call `rules/instantiate-rule`
* they grab `(first :consequents)`
* they stamp provenance immediately
* there is no runtime validation step

Replace them with wrappers around `rules/execute-rule`.

Something like:

```clojure
(defn- single-consequent!
  [rule result]
  (let [consequents (:consequents result)]
    (when-not (= 1 (count consequents))
      (throw (ex-info "Expected exactly one consequent"
                      {:rule-id (:id rule)
                       :consequents consequents})))
    (first consequents)))

(defn- execute-seeded-consequent
  [rule bindings]
  (->> (rules/execute-rule rule {:bindings bindings})
       (single-consequent! rule)
       (assoc :rule-provenance (seed-rule-provenance rule))))

(defn- execute-derived-consequent
  [producer-provenance rule fact-type bindings]
  (->> (rules/execute-rule rule {:bindings bindings})
       (single-consequent! rule)
       (assoc :rule-provenance
              (extend-rule-provenance producer-provenance
                                      rule
                                      fact-type))))
```

Then:

* `emit-rule-fact` becomes `execute-seeded-consequent`
* `instantiate-derived-fact` becomes `execute-derived-consequent`

### Stop selecting dispatch payloads by position

This is important.

`plan-payloads-from-request-facts` currently depends on `(first :consequents)`. That is bad. It is brittle, and it hides the fact that `rationalization-plan-dispatch-rule` and `reversal-plan-dispatch-rule` declare multiple consequents.

In the current authored rules, the payload is the unique consequent without `:fact/type`. So use that explicitly as the temporary selector:

```clojure
(defn- primary-payload-consequent!
  [rule result]
  (let [payloads (filter #(not (contains? % :fact/type))
                         (:consequents result))]
    (when-not (= 1 (count payloads))
      (throw (ex-info "Expected exactly one primary payload consequent"
                      {:rule-id (:id rule)
                       :consequents (:consequents result)})))
    (first payloads)))
```

That keeps current behavior and stops depending on vector order.

It is still a temporary hack, but it is a better hack.

### Keep provenance local for now

Do not move `seed-rule-provenance` / `extend-rule-provenance` into `rules.clj` in this patch.

Reason: `episodic_memory.clj` and its tests currently assume the skinny edge shape:

```clojure
{:from-rule ...
 :to-rule ...
 :fact-type ...
 :edge-kind ...}
```

If you try to “improve” provenance now by stuffing in the richer edge objects from `rules/explain-path`, you will create churn for zero gain. `episodic_memory` normalizes only the skinny shape, and some tests compare stored `:edge-path` exactly.

Land the executor boundary first. Leave provenance shape alone.

### Keep `run-family-plan` hard-coded for one more step

Do not confuse two migrations:

1. executor dispatch boundary
2. generic family-plan dispatch

You need (1) first.

If you replace the `case` in `run-family-plan` with a map and still have `goal_families.clj` directly instantiating rule results, you have not solved the actual coupling.

So for this patch:

* keep `run-family-plan` as-is
* migrate the rule consumers to `execute-rule`
* only later make plan-dispatch rules own the procedural plan body

## 5. The awkward truth about current multi-consequent dispatch rules

`rationalization-plan-dispatch-rule` and `reversal-plan-dispatch-rule` are currently half real output, half interface sketch.

They declare `:family-affect-state` in `:consequent-schema`, but `goal_families.clj` does not actually admit those instantiated facts from the rule result. The procedural plan code recomputes richer affect-state facts later and asserts those.

That is not fatal, but it means this:

* `execute-rule` should validate the full multi-consequent result, because that is what the rule declares
* `goal_families.clj` should still only consume the primary payload consequent for now
* when you migrate those plan bodies behind real `:clojure-fn` executors, you need to either:

  * actually return and admit both consequents through the executor boundary, or
  * split the rule into payload emission and affect-state emission

Do not pretend the current authored schema and the current admitted outputs are perfectly aligned. They are not.

## 6. Migration path that keeps existing tests passing

This is the path I would use.

### Step A. Add the dispatch layer in `rules.clj`

Add:

* `validate-rule-result-shape!`
* `validate-consequents!`
* `validate-denotation!`
* `dispatch-executor`
* `execute-rule`

Rename current `instantiate-rule` implementation to something private like `instantiate-executor-result`, and keep public `instantiate-rule` as a compatibility wrapper.

Result:

* `rules_test.clj` keeps passing unchanged

### Step B. Strengthen rule validation, but only where it is already true

Tighten `valid-rule?` for denotation shape.

Do **not** change authored rules yet if they already satisfy the stricter shape. From what you pasted, `goal_family_rules.clj` already does.

### Step C. Migrate `goal_families.clj` helpers

Change only the choke-point helpers:

* `emit-rule-fact`
* `instantiate-derived-fact`

Make them call `rules/execute-rule`.

Then update:

* `activation-candidates-from-trigger-facts`
* `plan-request-facts-from-ready-facts`
* `plan-payloads-from-request-facts`

to consume `execute-rule` results via helper selectors.

Result:

* all current `goal_families_test.clj` expectations can stay unchanged
* provenance shape stays unchanged
* `episodic_memory_test.clj` stays unchanged

### Step D. Add regression tests, do not rewrite old ones

Add a few new tests.

In `rules_test.clj`:

* `execute-rule-dispatches-instantiate-and-matches-current-output`
* `execute-rule-rejects-consequent-count-mismatch`
* `execute-rule-rejects-binding-drift`
* `execute-rule-runs-denotation-validation-fn`

In `goal_families_test.clj`:

* one regression test that proves high-level family logic no longer depends on direct calls to public `rules/instantiate-rule`

The clean way is:

```clojure
(with-redefs [daydreamer.rules/instantiate-rule
              (fn [& _]
                (throw (ex-info "should not be called" {})))]
  ;; family activation / request / payload path still works
  ...)
```

That test only works if `execute-rule` uses its own private instantiate executor, not the public compatibility wrapper. That is exactly what you want.

### Step E. Only after that, generic family-plan dispatch

Once `goal_families.clj` is executor-agnostic, then make the next move:

* generic plan-dispatch rule selection
* still backed by the existing procedural plan bodies
* later, real `:clojure-fn` executors

Do not do both in one patch.

## 7. What not to do

Three things:

1. **Do not make `execute-rule` persist facts or stamp provenance.**
   Validation must happen first. Admission comes later. Same pattern as your higher-level validator design. 

2. **Do not touch `:edge-path` shape yet.**
   Retrieval and tests are locked to the skinny edge maps.

3. **Do not force current world-mutating family plan bodies into `RuleResultV1` yet.**
   `RuleResultV1` is fine for pure consequence emission. It is not yet a faithful carrier for `[world updated-payload]` style procedures like `roving-plan`, `rationalization-plan`, and `reverse-leafs`.

That last point matters. Your next step is the executor boundary for the current pure instantiate slice. It is not the full migration of family plans into `:clojure-fn`. Not yet.

## Bottom line

The right next move is:

* add `rules/execute-rule`
* validate `RuleResultV1` structurally and denotationally
* keep `instantiate-rule` as a compatibility shim
* route `goal_families.clj` through `execute-rule`
* keep provenance format and `run-family-plan` case dispatch unchanged for now

That gets you a real boundary without breaking the test suite. After that, you can introduce the first real `:clojure-fn` rule without the rest of the kernel caring how the result was produced.

Look away from the screen for a moment. Unclench your jaw. Drop your shoulders.
