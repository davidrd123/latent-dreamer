Verdict:

* There was a real bug in the rationalization afterglow seam.
* The diff you showed fixes that bug in the built-in path.
* I do **not** see another live omission bug in the current top-level reversal bridges.
* The Step 2 effect-program contract is still weak. It validates effect **maps**, not effect **programs**.

## Primary seam: rationalization afterglow

### What was broken

`goal_family_rules.clj` says `:goal-family/rationalization-plan-dispatch` structurally emits **two** consequents: the payload and a `:family-affect-state` with `:affect :hope` / `:transition :reappraised`.

But `goal_families.clj` does not admit that second consequent directly. `plan-payloads-from-request-facts` throws away fact consequents and keeps only the primary payload plus `:effects`. So the **actual** world admission path is:

`rationalization-plan-dispatch-executor`
→ `rationalization-effect-program`
→ `apply-family-effects`
→ `handle-rationalization-assert-afterglow`
→ `rationalization-afterglow-fact`
→ `cx/assert-fact`

That split is the root problem.

### Why the bridged reversal → rationalization path failed pre-patch

In the normal rationalization path, `rationalization-diversion-preview` can find a dependency-linked negative emotion in `trigger-context-id`, so `handle-rationalization-assert-afterglow` had real diversion data and asserted the affect-state fact.

In the **bridged** path, `reversal-aftershock-to-rationalization-rule` does **not** require a dependency fact or a live negative emotion fact in the trigger context. It only needs reversal’s `:family-affect-state` plus a rationalization frame. So the executor could still **claim** an afterglow consequent, but `apply-rationalization-emotional-diversion` returned no diversion payload, and pre-patch `handle-rationalization-assert-afterglow` only looked at diversion output. Result: structural consequent present, runtime fact absent.

That was a real behavioral bug, not just a spec mismatch, because downstream `cross-family-trigger-facts` only scans asserted facts in contexts. No asserted `:family-affect-state` meant no later bridge from rationalization afterglow.

### What the patch fixes

The patch does the right thing:

* `rationalization-effect-program` now threads fallback trigger-emotion id/strength into `:rationalization/assert-afterglow`.
* `handle-rationalization-assert-afterglow` now uses those fallback values when diversion output is absent.
* The new test that checks the bridged reversal → rationalization branch now contains the affect-state fact is exactly the regression that was missing.

So:

* **If this diff is merged, the live bug is fixed.**
* **If it is not merged, the bug is still live.**

### What is still odd, but not broken

The runtime afterglow fact is asserted into the **sprouted branch context**, but the fact’s own `:context-id` still points at the **source** context, and the runtime adds `:branch-context-id` even though the structural consequent does not. That is weird, but it is operationally coherent:

* `cross-family-trigger-facts` scans visible facts in the branch, so it can see the fact.
* The bridge rules bind `?context-id` from the fact payload, not from storage location.
* So the system can store the fact in the branch while still treating it as “about” the source context.

I would call that a modeling asymmetry, not a bug.

### Residual risk in this seam

The patch fixes the built-in path, but it does **not** make the property invariant.

`family-effect-specs` now validates `:fallback-trigger-emotion-id` and `:fallback-trigger-emotion-strength` **if present**. They are still not required keys for `:rationalization/assert-afterglow`.

So a future malformed executor can reintroduce the same class of bug and still pass per-effect validation.

That matters. The bug was fixed in code flow, not in the contract.

### One more nit: semantic thinness remains

In the bridged reversal → rationalization path, the patch ensures the coarse `:family-affect-state` is asserted. But `apply-rationalization-emotional-diversion` still produces no actual hope-emotion fact and no trigger-emotion attenuation when no diversion source exists.

So after the patch, the branch can truthfully say “rationalization yielded hopeful afterglow” at the family-affect level, while still lacking the finer-grained emotion-fact updates that the normal rationalization path produces.

That is not the seam you asked about, but it is a real semantic asymmetry.

---

## Other cross-family affect-state bridges

### Reversal → roving

### Reversal → rationalization

I do **not** see a live top-level bug here.

Reason:

* `reversal-plan-dispatch-rule` has the same structural split: it declares a `:family-affect-state` consequent, but the runtime does not auto-admit it from the rule result.
* The actual runtime assertion happens later in `run-family-plan` via `reversal-aftershock-fact`.
* That function falls back to the goal’s stored trigger-emotion id/strength if it cannot re-derive them from the old context.
* If reversal has no executable branches, `reversal-plan-effects` returns `nil`, and `run-family-plan` returns `nil` rather than pretending the plan succeeded without aftershock.

So in the current **top-level** path, reversal bridges are aligned.

### But there is still a layering split

Lower-level helpers do not materialize the structurally claimed affect fact:

* `reversal-plan-effects` does not assert it.
* `reverse-leafs` does not assert it.
* only `run-family-plan` does.

So the reversal seam is still architecturally fragile. It just is not currently broken in the path your tests exercise.

That same structural/runtime split is the disease. Rationalization just happened to catch it first.

---

## Secondary seam: Step 2 effect-program contract

This is where the remaining trouble is.

### Bottom line

`rules/execute-rule` is pretty good at validating a single effect against its schema.
It is **not** good at validating an effect **program** as a dependency graph.

There is no real whole-program validation for:

* `:result-key` producer/consumer wiring
* `:from-*` dependencies
* duplicate `:result-key`s
* “producer must come before consumer”
* branch payload internal shape
* “effects must actually materialize the non-payload consequents the rule structurally declared”

That last one is the big one.

### Concrete current holes

| Hole                                                                         | Current behavior                                                                                                                                                                       | Severity |
| ---------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- |
| Dangling `:from-promotions` in `roving-effect-program`                       | `:mutation/log` consumes `:roving/promotions`, but no prior effect produces it. `handle-mutation-log` silently defaults promotions to `[]`.                                            | Medium   |
| Missing upstream `:from-reminding`                                           | `handle-episode-assert-retrieval-hits` will happily build retrieval-hit facts from `nil` reminder data, including a seed fact with `:episode-id nil`, instead of throwing.             | High     |
| Missing upstream `:from-diversion` in rationalization                        | Now masked by fallback trigger data. Good for the bridged path, bad for detecting wiring mistakes. A typo can silently turn “diverted afterglow” into “undeducted fallback afterglow”. | High     |
| Missing upstream `:from-uses` / `:from-reminding` in use-resolution handlers | Silent no-op / empty results, not fail-fast.                                                                                                                                           | Medium   |
| Duplicate `:result-key`                                                      | Later writes overwrite earlier ones in `effect-state`. No validator stops that.                                                                                                        | Medium   |
| Symbolic `:context-ref` dependency                                           | No static validation, but unresolved refs do fail fast at runtime through `resolve-effect-context-id`.                                                                                 | Lower    |

So the answer to “missing whole-program validation?” is **yes**.

Not everywhere is equally bad. Context-ref issues at least throw. Result-key issues often do not.

### Reversal branch payload validation is too shallow

This is another real contract gap.

For `:reversal/execute-branches`, validation currently only guarantees:

* `:branches` is a vector
* each entry is a map

That is nowhere near enough for what `handle-reversal-execute-branches` assumes. The handler expects branch maps to contain, functionally:

* `:old-context-id`
* `:leaf-goal-id`
* `:ordering`
* `:objective-facts`

Current built-in `reverse-leaf-branches` does emit those fields, so the shipped path is okay.

But the contract is still bad:

* missing `:old-context-id` will explode later
* missing `:ordering` can silently fall back to default context ordering
* missing `:objective-facts` can silently skip the weak-assumption retraction that reversal is supposed to perform

That last one is important. It means malformed executor output can preserve a branch while quietly dropping the core reversal operation.

### The main architectural gap

The system currently has two separate notions of correctness:

1. the rule’s structural consequents
2. the effect program that mutates the world

There is no generic proof that (2) actually materializes (1), especially for non-payload consequents.

That is exactly why the rationalization bug happened.

The patch fixes one instance. The generic gap remains.

---

## What is actually safe vs unsafe right now

### Safe enough in current built-in flows

* Normal rationalization afterglow path
* Bridged reversal → rationalization afterglow path, **with the diff merged**
* Reversal aftershock bridges in `run-family-plan`
* Unresolved symbolic context refs: these fail fast

### Not safe enough contractually

* Any future effect program that miswires `:result-key` / `:from-*`
* Any future executor that omits rationalization fallback fields
* Any future malformed reversal branch payload
* Any caller that uses lower-level helpers and assumes structural consequents were admitted automatically

---

## Tight summary

The patch is correct. It closes the live rationalization-afterglow hole.

But it does **not** solve the deeper issue: RuleV1 structurally claims facts that the runtime only materializes through separate, ad hoc effect handlers, and there is no whole-program validator tying those together.

That leaves you with:

* **primary seam fixed in the current path**
* **secondary contract still porous**

The most important remaining risk is not “some effect map has a wrong type.”
It is “the effect program is internally miswired and the system either no-ops or writes degraded state instead of throwing.”

Look away for a second. Relax your shoulders.
