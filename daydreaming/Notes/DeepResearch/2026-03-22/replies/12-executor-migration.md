Blunt answer: do **not** try to make these three migrations coincide in one abstraction jump.

Land them as three layers:

1. `execute-rule` for the **pure RuleV1 instantiate path**
2. episode **use/outcome attribution** behind the existing family hooks
3. rule-access **registry + filtered graph views** as inert scaffolding

If you try to make roving the first real `:clojure-fn` executor **while** redefining promotion **and** adding frontier semantics, you’ll tangle three state machines and lose the test suite.

## 1. Reconcile local roving effects with `execute-rule`

### The clean answer

`roving-plan-effects` is the **prototype** of the later `:effects` channel. It is **not** the thing to move into `rules.clj` first.

So:

* **Yes, later** it should become the body of a `:clojure-fn` executor for `:goal-family/roving-plan-dispatch`.
* **No, not now.**
  `execute-rule` v1 should handle the current pure instantiate slice only.

That means the first useful `execute-rule` is:

* dispatch on `[:executor :kind]`
* actually implement only `:instantiate`
* validate result shape / consequent count / binding consistency
* optionally run `:denotation/:validation-fn`
* return a result map
* **do not mutate world**
* **do not apply effects**

That is useful immediately because `goal_families.clj` is still calling `rules/instantiate-rule` directly at the choke points, and `rules_test.clj` only knows about `instantiate-rule` today. Keep `instantiate-rule` as a compatibility wrapper so those tests stay green.

### What changes in `goal_families.clj` first

Change the pure rule-consumption choke points only:

* `emit-rule-fact`
* `instantiate-derived-fact`
* `activation-candidates-from-trigger-facts`
* `plan-request-facts-from-ready-facts`
* `plan-payloads-from-request-facts`

Those should stop calling `rules/instantiate-rule` directly and call `rules/execute-rule` instead.

One nit that matters: `plan-payloads-from-request-facts` should stop taking `(first :consequents)`. That is already wrong in spirit because `rationalization-plan-dispatch-rule` and `reversal-plan-dispatch-rule` declare multi-consequent outputs. Add a helper that selects the unique **payload** consequent, i.e. the consequent without `:fact/type`. Fix that in the same patch.

### What **not** to do in `execute-rule` v1

Do **not** make it effect-aware yet.

Reason: the roving effect vocabulary is still local and rationalization/reversal do not share it yet. Worse, the local effect runtime depends on symbolic refs like `:branch-context`, effect-state threading, and kernel-owned ops over context/goals/episodes. You have not stabilized that contract.

So the minimum good version is:

* `execute-rule` returns validated pure results
* `apply-family-effects` stays in `goal_families.clj`
* `roving-plan-effects` stays where it is

Long-run, the seam should still be “executor proposes typed effects, kernel applies them,” i.e. propose → validate → admit, not opaque executor-owned world mutation. That matches the broader architecture notes. 

### Revised migration path

The old A→E path changes to this:

**A.** Add `execute-rule` in `rules.clj`, but only for `:instantiate`. Keep `instantiate-rule` as wrapper.
**B.** Route the five choke points in `goal_families.clj` through `execute-rule`.
**C.** Add `primary-payload-consequent!` so plan payload extraction stops depending on vector order.
**D.** Leave `roving-plan-effects` and `apply-family-effects` local.
**E.** Only after that, make `roving-plan-dispatch` the first `:clojure-fn` vertical slice.

So the answer to “does the roving effects code become the body of a `:clojure-fn` executor?” is:

* **architecturally yes**
* **migration-order no**

## 2. Exact hook points for `note-episode-use` and `resolve-episode-use-outcome`

### First principle

`note-episode-use` should fire on **actual branch use**, not on candidate retrieval.

Candidate retrieval is too early. In the current code, candidate lists are noisy:

* `rationalization-frame-candidates` may retrieve stored episodes that lose to explicit frames
* `reverse-undo-cause-candidates` may retrieve stored episodes that lose to explicit `:failure-cause`
* roving retrieval may surface episodes that are retrieved but not actually relied on in the final branch logic

If you count candidates as uses, your stats become garbage.

So the split should be:

* **retrieval** = maybe surfaced
* **use** = actually chosen into branch execution
* **outcome** = later resolved as helped / failed / contradicted / grounded

### Exact call sites

#### Roving

Hook point: the existing roving effect path, not candidate selection.

Today the concrete place is the `apply-family-effect` handling behind:

* `:episode/reminding`
* `:episodes/note-family-reuse`
* `:episodes/promote-cross-family`

That is the right seam because effect-state already contains the **actual** used episode ids:

* seed episode = `:episode-id`
* reminded episodes = `:reminded-episode-ids`
* branch context exists

So replace the meaning of `:episodes/note-family-reuse` first, not the op name yet.

In the intermediate green state:

* keep the op name `:episodes/note-family-reuse` so `roving-plan-effects-builds-typed-program` keeps passing
* change its handler to call `note-episode-use`

Record:

* seed episode with `:use-role :seed`
* reminded episodes with `:use-role :reminded`

This is actual use, not hypothetical use.

#### Rationalization

Hook point: where the stored episode is actually selected, which is already explicit.

Today that is the direct call in `rationalization-plan` gated by:

* `selection-policy == :stored_rationalization_episode`
* `(:episode-id frame)`

That is the correct place. Replace that call with `note-episode-use`.

Do it **after** the branch is sprouted and the reframe facts are asserted, because by then the stored episode has genuinely shaped the branch.

Use role should be something like:

* `:use-role :frame-source`

#### Reversal

Hook point: where the stored counterfactual source is actually used, not when it was retrieved.

Today that is the direct call in the reversal branch of `run-family-plan`, gated by:

* `primary-branch`
* `(:counterfactual-policy reversal-target) == :stored_reversal_episode`
* `(:counterfactual-cause-id reversal-target)`

That is the correct place. Replace that call with `note-episode-use`.

Use role:

* `:use-role :counterfactual-source`

### Where `resolve-episode-use-outcome` belongs

Immediate hook point: **after** `maybe-apply-family-evaluator` and **after** `store-family-plan-episode`, inside `run-family-plan`.

That is the first place where you actually know enough:

* evaluator output
* branch context id
* created family-plan episode id
* family/result payload
* emotion shifts / affect state
* whether anti-residue flags were emitted
* whether cross-family promotion facts were produced

So yes, the current family-evaluator timing is the **right immediate hook**.

But it is **not** the full long-run hook.

You need two-stage outcome resolution:

1. **Immediate resolution** in `run-family-plan`
   Uses evaluator output + local branch result.
2. **Delayed resolution** later
   For downstream success, contradiction, backfire, world confirmation.

So `resolve-episode-use-outcome` should be idempotent and reopenable. The first call happens in `run-family-plan`; later calls refine the same use record.

### Exact refactor for `promote-cross-family-retrieval-episodes`

Right now it does both bookkeeping and policy. Split it.

New shape:

* `record-cross-family-use`
* `reconcile-episode-admission`

Then keep `promote-cross-family-retrieval-episodes` as a **compatibility wrapper** for one migration step.

So the handler for current op `:episodes/promote-cross-family` becomes:

1. record cross-family use evidence
2. call `reconcile-episode-admission`
3. emit the same `promotion-facts` / `promoted-episode-ids` shape as today

That keeps these tests green:

* cross-family roving promotion tests
* `stored-rationalization-family-plan-episode-feeds-later-roving`
* `stored-reversal-family-plan-episode-feeds-later-roving`

### One more nit: do **not** flip initial admission yet

`family-plan-admission-status` currently lets evaluator `:promotion-decision :promote-durable` create immediate `:durable`.

Long-run, that is wrong. `:durable` should come from evidence later.

But do **not** change that in the same patch set.

Why: current tests depend on it, especially the stored rationalization/reversal reopen tests that use `mock-family-evaluator` to get immediate durable episodes.

So the safe order is:

* add `note-episode-use`
* add `resolve-episode-use-outcome`
* add `record-cross-family-use`
* add `reconcile-episode-admission`
* keep `family-plan-admission-status` behavior for now as compatibility mode
* only later remove immediate evaluator-driven durable

If you change that too early, you’ll break the reopen tests and confuse whether the regression came from attribution, admission, or frontier logic.

## 3. Where the rule-accessibility frontier hooks in

### Keep `rules.clj` structural

Do **not** put accessibility into `RuleV1`.

Do **not** make `build-connection-graph`, `reachable-paths`, or `bridge-paths` context-sensitive.

Those functions should stay structural. That is their job. The graph remains the candidate substrate.

Accessibility is dynamic state.

### Add filtered graph views above the structural graph

The split should be:

* `structural-graph` = raw `rules/build-connection-graph`
* `planning-graph` = structural graph filtered to `:accessible`
* `serendipity-graph` = structural graph filtered to `:accessible ∪ :frontier`

### Exact current callers

#### `ranked-roving-episode-ids`

This should use **planning view**.

Reason: it is ordinary current-plan seed ranking, not speculative frontier discovery. It should not use frontier-only rules to bias normal planning.

#### `roving-plan-effects`

The connection graph passed into `:episode/reminding` retrieval opts should use **serendipity view**.

Reason: provenance bonus here is acting as retrieval-side bridge search. That is the place where “reachable through episode-linked structure, but not regular planning” makes sense.

#### `episodic_memory/retrieve-episodes` and `episode-provenance-info`

Do **not** teach `episodic_memory.clj` about rule-access directly in phase 1.

Keep it caller-driven:

* caller passes a graph
* episodic memory uses that graph

That keeps `episodic_memory_test.clj` green because those tests already pass explicit structural graphs.

#### `rationalization-frame-candidates` and `reverse-undo-cause-candidates`

Leave them alone initially.

Today they do not pass `connection-graph` at all. If you later give them one, it should be **planning view**, not serendipity view, because these are ordinary same-family fallback paths, not frontier discovery.

### Should the registry own rules, graph, and accessibility?

Yes, eventually.

The wrong thing in current code is rebuilding `build-connection-graph (family-rules)` inline in two places:

* `ranked-roving-episode-ids`
* `roving-plan-effects`

So the next step is a registry/cache helper, probably world-owned eventually.

Long-run ownership should be:

* rules
* structural graph
* dynamic `:rule-access`

But the first landing does **not** need full world mutation.

Intermediate green step:

* add helpers like `family-structural-graph`, `planning-graph`, `serendipity-graph`
* for now, all three return the same structural graph
* swap the two inline callers to those helpers

That fixes the wrong-layer caching problem without changing behavior.

### Should frontier wait for promotion logic?

The **scaffolding** does not need to wait.

The **state transitions** do.

So:

* land `:rule-access` registry now if you want
* initialize authored family rules as `:accessible`
* have no frontier rules yet
* `planning-graph == serendipity-graph == structural-graph` initially

That is safe.

What must wait is this rule:

* provisional episodes do **not** open rules to `:accessible`

So real frontier motion should wait until `reconcile-episode-admission` exists and durable promotion is no longer a fake evaluator shortcut.

## Order of changes that keeps tests green

Here is the order I’d actually use.

### 1. Add `execute-rule` in `rules.clj`

Implement:

* `execute-rule`
* private instantiate executor helper
* result/consequent validation
* keep `instantiate-rule` public and unchanged

Add new tests for `execute-rule`. Leave `rules_test.clj` existing assertions untouched.

### 2. Route `goal_families.clj` pure rule helpers through `execute-rule`

Change only:

* `emit-rule-fact`
* `instantiate-derived-fact`
* `activation-candidates-from-trigger-facts`
* `plan-request-facts-from-ready-facts`
* `plan-payloads-from-request-facts`

Also add `primary-payload-consequent!`.

This should keep:

* activation tests
* planning request/dispatch tests
* provenance tests

green.

### 3. Add graph-view helpers and replace inline graph construction

Add:

* `family-structural-graph`
* `planning-graph`
* `serendipity-graph`

Initially all return the same structural graph.

Swap only:

* `ranked-roving-episode-ids`
* `roving-plan-effects`

No behavior change. Tests stay green.

### 4. Add `note-episode-use` as a richer wrapper

In `episodic_memory.clj`:

* add `note-episode-use`
* keep `note-episode-reuse` as compatibility wrapper

Then change call sites:

* roving effect handler behind `:episodes/note-family-reuse`
* rationalization direct stored-frame use hook
* reversal direct stored-counterfactual use hook

Do **not** rename ops yet. That preserves `roving-plan-effects-builds-typed-program`.

### 5. Split cross-family bookkeeping from policy, behind the same op

Add:

* `record-cross-family-use`
* `reconcile-episode-admission`

Reimplement `promote-cross-family-retrieval-episodes` as compatibility wrapper over those.

That preserves the current roving promotion result shape, so the cross-family promotion tests keep passing.

### 6. Add `resolve-episode-use-outcome` after storage in `run-family-plan`

Immediate hook:

* after `maybe-apply-family-evaluator`
* after `store-family-plan-episode`

Initially make it record-only or low-impact. Do not change admission policy yet.

### 7. Separate PR: remove immediate evaluator-driven durable

Only now change `family-plan-admission-status` / initial admission semantics.

This is the PR that will require updating tests that currently assume:

* mock evaluator can make rationalization durable immediately
* mock evaluator can make reversal durable immediately

Do **not** combine this with steps 1–6.

### 8. Separate PR: first real `:clojure-fn` executor

Make `roving-plan-dispatch` the first vertical slice.

At that point:

* `roving-plan-effects` becomes executor body or helper called by it
* generic effect applier can move out of `goal_families.clj`
* rationalization/reversal stay procedural until their effect/result contracts are real

## Bottom line

The short answer to the whole prompt is:

* **`execute-rule` first, but instantiate-only**
* **episode use on actual branch use, not retrieval**
* **frontier as registry scaffolding first, not active rule-opening logic**
* **do not change initial durable semantics in the same migration**

That is the path most likely to stay green while actually reducing architectural debt.

Look away for 20 seconds. Relax your shoulders.
