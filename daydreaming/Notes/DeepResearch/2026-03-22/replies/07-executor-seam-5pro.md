Verdict: the future `:clojure-fn` seam should be **declarative over state changes**. Do **not** let executors take `world`, patch it privately, and return only a summary.

That would make the rule layer cosmetic.

Right now the mismatch is plain:

* `rules.clj` defines a rule runtime that thinks in terms of `RuleResultV1` with `:consequents`, `:confidence`, `:reason`, `:aux-indices`.
* `goal_families.clj` does the real work as direct world transformation: `roving-plan`, `rationalization-plan`, `reverse-leafs`/`reversal-sprout-alternative` thread `world` through `context`, `goals`, and `episodic-memory`.
* `run-family-plan` then does more shared persistence on top, especially `store-family-plan-episode`.
* `goal_families_test.clj` asserts not just emitted facts, but context topology, pseudo-sprout flags, retractions, emotion rewrites, reminding results, goal pointer changes, mutation logs, stored episodes, and composed provenance.

So the current plans are not “rules with consequents.” They are **cross-subsystem transactions**.

The right boundary seems correct$_{90%}$:

**executors may be procedural in how they compute a plan, but the kernel should remain the component that commits the state change.**

## Why summary-only world mutation is the wrong seam

Because it breaks the three things your docs say matter.

First, **kernel ownership**.
`context.clj`, `goals.clj`, and `episodic_memory.clj` are pure world transformers. That is already a transactional kernel shape. The docs in `kernel-rule-schema-and-execution-model.md` explicitly put recursion, validation, and persistence in Clojure, not in the executor. If a `:clojure-fn` executor just mutates `world`, you’ve bypassed that ownership model.

Second, **provenance**.
The review note in `2026-03-22/replies/code-review-3-core.md` is right: provenance has to be stamped on **admitted runtime outputs**, not on pre-runtime placeholders. If the executor mutates world internally, there is no canonical admission point. Provenance gets smeared across helper functions, or worse, attached to a sketch that does not match the real output. You already have this smell in `goal_family_rules.clj`: `rationalization-plan-dispatch-rule` and `reversal-plan-dispatch-rule` sketch `:family-affect-state` outputs, but the actual emitted facts are richer and are produced later in `goal_families.clj`.

Third, **validation**.
A summary-only return gives you nothing to validate except the summary text. But the plans need validation at several levels:

* fact-level: emitted facts conform to schema
* operation-level: context surgery is legal
* world-level: the resulting state respects invariants
* persistence-level: episode writeback is correctly indexed and provenanced

If the executor already changed the world, validation becomes post-hoc diff archaeology.

## What `RuleResultV1` should mean instead

`RuleResultV1` as currently documented is too small. Either extend it, or stop pretending the family plans fit it.

For these plan bodies, `RuleResultV1` should mean:

1. **actual emitted facts**
   Facts that become visible in the world and matter for graph/provenance/retrieval.

2. **typed state effects**
   A declarative transaction over kernel-owned subsystems.

3. **episode/writeback proposal**
   Enough structured information for common persistence to happen outside the executor.

4. **summary/explanation**
   Useful for traces and debugging, but not the authoritative contract.

In other words, something like:

```clojure
{:consequents [...]          ; actual emitted facts, schema-valid
 :effects    [...]           ; typed ops over contexts/goals/memory/trace
 :writeback  {...}           ; optional episode/storage proposal
 :confidence 1.0
 :reason "..."
 :aux-indices [...]
 :surface-summary "..."}
```

The crucial point: `:effects` should be **typed kernel operations**, not a giant raw diff and not an opaque `world'`.

That lets you preserve meaningful primitives such as:

* `:sprout-context`
* `:copy-context`
* `:pseudo-sprout`
* `:assert-fact`
* `:retract-fact`
* `:set-goal-next-context`
* `:episode-reminding`
* `:append-mutation-event`
* `:store-family-plan-episode`

That is the right granularity. It keeps Mueller’s coded-plan character, but under kernel control.

## How this applies to the three family plans

### ROVING

`roving-plan` currently:

* resolves a plan payload from structural request facts
* sprouts a context
* asserts success `:intends`
* runs `episodic/episode-reminding`
* sets ordering
* updates the goal’s `:next-cx`
* appends a mutation event

That should not become “executor mutates world, returns `{:sprouted-context-id ...}`”.

It should return a proposal roughly of the form:

* emitted facts: the actual asserted `:intends` fact, if you want it surfaced
* effects: sprout, assert, reminding, goal-pointer update, mutation log
* writeback: seed episode id, reminded episode ids, active indices, selection policy

The reminding cascade is the interesting case. It is algorithmic and recursive. Fine. Let the executor compute it procedurally. But the runtime should still own the commit of its results.

### RATIONALIZATION

`rationalization-plan` is even more obviously transactional:

* choose frame
* sprout context
* assert reframe facts
* rewrite trigger emotion
* create hope emotion
* emit `:family-affect-state`
* update `:emotions`
* update goal next-context
* append mutation event

Here `:consequents` should include the **actual emitted facts** that later reasoning can see, especially the emitted affect-state and any admitted emotion facts if you want them rule-visible.

But the emotion rewrite itself is not “just a consequent.” It is an operation with invariants. That belongs in `:effects`.

### REVERSAL

REVERSAL kills the idea that `RuleResultV1 = consequents`.

The real work is:

* select reversal target and counterfactual cause
* copy old context
* pseudo-sprout under new context
* mark alternative-past metadata
* garbage-collect emotions/plans
* rebind planning facts
* assert input facts
* retract weak leaf objectives
* set goal next-context
* emit aftershock fact
* later persist a family-plan episode

This is a transaction over context topology plus fact visibility plus later episodic persistence. A summary-only return would hide the whole mechanism that your tests actually care about.

So for REVERSAL, the executor result should name high-level effects like `:sprout-alternative-past` or `:reversal-branch`, not just “here is the new branch id”.

## Why this fits the current kernel better than direct mutation

Because `run-family-plan` already behaves like a proto-applier.

Look at the shape:

* family-specific function computes the meaningful plan result
* shared logic wraps it with selection metadata
* shared logic stores family-plan episodes
* shared logic carries rule provenance into stored episodes

That is not accidental. It is the kernel telling you where the boundary wants to be.

If you move to direct world-mutating executors, you either:

* duplicate all of that shared persistence/provenance logic inside each executor, or
* keep it outside, in which case the executor still has to return structured data describing what happened

The second option is just the declarative seam again.

## One nit that matters: ids

A real transactional seam needs to deal with allocated ids for sprouted contexts and episodes.

That means the clean contract is probably not “executor returns final concrete ids” unless the executor is allowed to reserve ids. Better is:

* executor returns symbolic refs / effect bindings
* applier resolves them during commit
* applied result gets concrete ids afterward

That is the only annoying part. It is still better than opaque mutation.

## What to do with `:consequent-schema`

Do not overload it to describe the whole plan.

For `:clojure-fn` plan bodies:

* `:consequent-schema` should describe **actual graph-visible emitted facts**
* a separate `:effect-schema` or equivalent should describe allowed operational effects
* `:denotation` should say what state change the rule is meant to accomplish

Right now `goal_family_rules.clj` mixes payload sketches with actual emitted facts. That is why the seam feels muddy.

## Bottom line

The clean answer is:

* **No**, executors should not mutate `world` and return only a summary.
* **Yes**, they may be procedural internally.
* The contract should be **declarative over typed state changes**, with the kernel applying them.
* `RuleResultV1` should become a **transaction proposal plus emitted facts plus writeback metadata**, not just `:consequents`.

That preserves:

* kernel ownership of state
* runtime validation
* canonical provenance stamping
* shared episode persistence
* future compatibility with `:llm-backed` rules

If you don’t do that, `:clojure-fn` becomes an escape hatch, and the rule engine stays a diagram rather than a runtime.

The next useful move is to define the effect vocabulary for the three existing family plans before touching generic executor dispatch. Look away from the screen for a moment and drop your shoulders.
