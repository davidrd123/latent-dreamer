Bottom line: the Step 2 seam validates effect **entries**, not effect **programs**$_{96%}$.

`rules/execute-rule` + `validate-family-effect!` do a decent job on local shape: bad op names, missing required keys, wrong effect count/order, malformed handler returns, unresolved context refs that are actually unresolved. But there is no pass that turns the `:effects` vector into a dependency graph over `:result-key`, `:from-*`, and symbolic refs. That gap is already visible in current code.

## Concrete findings

### 1) The shipped roving program has a dangling `:from-promotions` dependency

This is a current bug, not a hypothetical one$_{99%}$.

In `goal_families.clj`:

* `roving-effect-program` emits `:mutation/log` with `:from-promotions :roving/promotions`.
* No earlier roving effect emits `:result-key :roving/promotions`.
* `handle-mutation-log` then does:

  ```clj
  (get-in effect-state [:results (:from-promotions effect)]
          {:promoted-episode-ids []})
  ```

  so the missing upstream dependency is silently replaced with an empty vector.

Worse, `roving-plan` also reads `:roving/episode-uses` and `:roving/promotions` from `effect-state` with empty defaults, even though the current roving effect program never emits `:episodes/note-family-uses` or `:episodes/resolve-use-outcomes`.

So the current roving effect vector is dataflow-invalid, but the runtime masks that invalidity.

### 2) `:effect-schema` is not a closed contract, and the live runtime has already drifted past the rule data

This is the biggest contract leak$_{98%}$.

In `rules.clj`, `validate-effect-schema!` relies on `match-fact-pattern`, and map matching is subset-based. Extra keys on an effect map are allowed.

That means `goal_family_rules.clj` is **not** the full source of truth for effect contracts. Current examples:

* Roving adds `:from-promotions` on `:mutation/log`, but the rule’s `:effect-schema` does not declare it.
* The rationalization fallback patch adds `:fallback-trigger-emotion-id` and `:fallback-trigger-emotion-strength` to `:rationalization/assert-afterglow`, but the rule `:effect-schema` still does not declare them.
* Reversal injects `:input-facts` into `:reversal/execute-branches` after rule execution, but that field is not in the rule `:effect-schema` either.

So the patch fixed the afterglow symptom, but it fixed it by adding runtime-only fields outside the declared RuleV1 contract.

If you later build tooling around `goal_family_rules.clj` as “the contract”, that tooling will be wrong.

### 3) `:from-*` dependencies are not validated as producer/consumer edges, and several handlers degrade silently

This is broad and real.

There is no check that:

* every `:from-reminding`, `:from-uses`, `:from-diversion`, `:from-promotions`
  points to a produced `:result-key`,
* the producer comes earlier in the effect vector,
* the producer is unique.

Concrete cases in `goal_families.clj`:

* `handle-episode-assert-retrieval-hits` destructures the upstream reminder result directly. If `:from-reminding` is missing/wrong, it can build retrieval-hit facts from nil-ish data instead of failing.
* `handle-note-family-uses` on missing `:from-reminding` collapses into empty use facts.
* `handle-resolve-use-outcomes` on missing `:from-uses` collapses into empty outcomes/promotions.
* `handle-rationalization-assert-afterglow` on missing `:from-diversion` either:

  * synthesizes from fallback fields, or
  * produces `{:affect-state-fact nil}`
    with no exception.
* `handle-mutation-log-rationalization` defaults missing diversion to `{}` and logs an empty `:emotion-diversion`.
* `handle-mutation-log` defaults missing promotions to `[]`; missing reminding data yields nil-ish logged fields.

That is the pattern: missing upstream dataflow often becomes empty output or partial nonsense, not a hard failure.

### 4) Reversal branch validation is too shallow; nested branch payloads are mostly trusted

This is the weakest part of the Step 2 contract surface$_{97%}$.

`family-effect-specs` validates `:reversal/execute-branches` with:

* required top-level keys like `:old-context-id`, `:new-context-id`, `:branches`, `:result-key`
* and `:branches` only as `vector-of-maps?`

That is not enough. `handle-reversal-execute-branches` assumes each branch map has meaningful nested fields like:

* `:old-context-id`
* `:ordering`
* `:objective-facts`
* `:leaf-goal-id`

What happens today:

* Missing `:old-context-id` usually throws later via `copy-context` / unknown context. Good.
* Missing `:ordering` silently falls back to copied/default ordering.
* Missing `:leaf-goal-id` silently degrades later metadata.
* Missing `:objective-facts` means no retractions happen.
* If `:objective-facts` are present but not actually visible in the copied context, `cx/retract-fact` no-ops, but the branch result still reports:

  ```clj
  :retracted-fact-ids (mapv fact-id (:objective-facts branch))
  ```

  and `reversal-sprout-alternative` still logs those requested retractions.

So reversal can claim “I retracted the weak assumption” even when nothing was removed. That is bad state reporting.

### 5) `goal/set-next-context` can fail silently

In `rules.clj`, `handle-goal-set-next-context` does:

```clj
(if (contains? (:goals world) (:goal-id effect))
  (goals/set-next-context world (:goal-id effect) context-id)
  world)
```

If a malformed effect has a stale or nonexistent `:goal-id`, the world is left unchanged and no exception is raised.

That means a plan can:

* sprout a branch,
* assert facts into it,
* log mutation events,

while the goal still points somewhere else.

Current happy paths likely avoid this because `goal-id` is bound from real request facts. But the contract absolutely allows silent drift here.

### 6) Symbolic refs and result keys have no whole-program namespace/uniqueness discipline

I did **not** find a current duplicate `:result-key` or duplicate `:ref` in the shipped effect programs. I did find a current never-produced result dependency (`:roving/promotions`).

Still, the runtime allows the following:

* duplicate `:result-key` producers → later write overwrites earlier result in `effect-state[:results ...]`
* duplicate `:ref` producers → later sprout overwrites earlier symbolic ref in `effect-state[:context-refs ...]`
* consumer-before-producer → only noticed at execution time, if at all
* a typo that matches an actual context id can silently resolve, because `resolve-effect-context-id` accepts either a symbolic ref or a real context id

So the ref space is open and ambiguous. The current programs happen to behave because executor code is disciplined, not because the runtime enforces the invariant.

### 7) There is no semantic backstop from denotational validation

In `goal_family_rules.clj`, the local `denotation` helper always sets `:validation-fn nil`, and the family rules use that.

So `execute-rule` has no semantic postcondition checks like:

* “did this `:from-*` dependency resolve?”
* “did reversal actually retract something?”
* “did afterglow produce a non-nil affect-state fact?”
* “did `goal/set-next-context` actually move the goal?”

That is why the system stops at local shape validation.

## What is already solid

To be fair: the local contract is pretty good.

The current runtime already rejects:

* undeclared ops,
* non-keyword ops,
* bad effect counts,
* wrong op order vs schema,
* malformed handler returns,
* unresolved context refs when they are truly unresolved,
* missing required per-effect keys caught by `validate-family-effect!`.

So this is not sloppy code. It just stops one layer too early.

## Missing tests that would pin this down

These are the tests I’d add next, in order:

1. **Reject dangling result dependencies**

   * Build the current roving effect vector and assert failure because `:from-promotions :roving/promotions` has no producer.
   * Right now that would expose a real current bug.

2. **Reject duplicate `:result-key` and duplicate `:ref`**

   * Two effects with the same `:result-key` should fail before execution.
   * Two `:context/sprout` effects with the same `:ref` should fail before execution.

3. **Reject missing upstream `:from-*`**

   * `:episode/assert-retrieval-hits` without a produced reminder result should throw, not emit nil-ish facts.
   * Same for `:episodes/note-family-uses`, `:episodes/resolve-use-outcomes`, `:rationalization/assert-afterglow`, `:mutation/log`, `:mutation/log-rationalization`.

4. **Reject reversal branches with bad nested shape**

   * Missing `:objective-facts`
   * Missing `:ordering`
   * Missing `:leaf-goal-id`
   * Non-map or non-fact contents inside `:objective-facts`

5. **Detect “claimed retraction but no actual retraction”**

   * Run reversal with branch objective facts not visible in the copied context.
   * Assert failure, or at minimum assert that reported/logged retractions equal actual removals.
   * Right now they do not.

6. **Fail on absent goal in `goal/set-next-context`**

   * A nonexistent goal id should throw.
   * Silent no-op is the wrong behavior for this seam.

7. **Catch schema drift**

   * Add a test that current effect output has no undeclared extra keys relative to rule `:effect-schema`, or explicitly bless that openness.
   * Right now the rationalization fallback patch, roving `:from-promotions`, and reversal `:input-facts` all bypass the rule data contract.

## Net assessment

The Step 2 contract is good at **“is each effect entry shaped correctly?”**
It is weak at **“does this vector of effects form a valid, self-consistent program?”**$_{95%}$.

The most important concrete problems are:

1. the **current dangling roving promotions dependency**,
2. the fact that **rule schemas are already stale relative to runtime behavior**, and
3. the **reversal branch nested payload gap**, where no-op retractions can still be reported as if they happened.

Those are the places I’d treat as load-bearing before trusting this seam further.
