Blunt answer: do **not** let `:clojure-fn` executors mutate `world` opaquely and return only a summary.

But also, do **not** force these plan bodies into a fake “facts only” contract.

The right seam is:

**procedural computation inside the executor, declarative effect description at the boundary, kernel-owned application of those effects**

That seems right$_{90%}$.

The reason is simple. Your current family plans are not “emit a few consequents” rules. They are little state-transition programs over the kernel’s owned subsystems:

* `context` owns sprouting, pseudo-sprouting, copying, assertion, retraction
* `goals` owns next-context pointers
* `episodic_memory` owns episode persistence and reminding
* outer family-plan machinery owns later trace and episode writeback

That already matches the broader architecture you wrote down elsewhere: propose → validate → admit, with persistent state owned by explicit servers and graph/state passed around as data, not hidden inside opaque calls.  

So the question is not “should executors be procedural?” Yes, obviously. Mueller’s original coded plans were procedural. `roving-plan1`, `reversal-sprout-alternative`, `reverse-leafs`, `minimization-plan` all directly did operational work and returned sprouted contexts. The question is **where that operational work becomes official kernel state**.

That boundary should be **outside** the executor.

## What `RuleResultV1` should mean

`RuleResultV1` should mean:

> the complete, admission-ready description of one rule firing

Not “the executor already changed the world.”

For `:instantiate`, that is basically the current shape.

For `:clojure-fn`, it needs to grow into three channels:

1. **Structural products**
   These are the graphable, provenance-carrying products that must actually conform to `:consequent-schema`.

   Example:

   * a real `:family-affect-state` fact for rationalization or reversal, if that is what later bridge rules consume
   * not a sketch, not “something like this happened”, the actual fact

2. **Operational effects**
   An ordered, typed effect program over kernel subsystems.

   Not raw final-world diff, and not hidden mutation.

   Think:

   * `:context/sprout`
   * `:context/pseudo-sprout`
   * `:context/copy`
   * `:fact/assert`
   * `:fact/retract`
   * `:goal/set-next-context`
   * `:emotion/replace`
   * `:episode/reminding`
   * `:trace/add-mutation-event`

   These can be primitive or composite. For example, `sprout-alternative-past` can stay a named composite op if you want. It just cannot be invisible.

3. **Post-apply summary material**
   Typed summary for trace and generic persistence.

   Example:

   * `:selection`
   * `:sprouted-context-ids`
   * `:emotion-shifts`
   * `:emotional-state`
   * `:retrieval-indices`
   * `:support-indices`
   * `:episode-payload`

This is the missing middle. Summary-only is too weak. Facts-only is too narrow.

## Why summary-only is the wrong contract

Because it destroys the exact things you say you care about.

First, **validation**. Your architecture is explicit that the system should work by propose → validate → admit, with state changes checked against constraints and invariants before they become canonical.  If an executor mutates `world` internally and returns a summary afterward, validation becomes post hoc archaeology.

Second, **provenance**. Right now the instantiate-biased bridge stamps provenance onto derived facts early. That is barely acceptable for `:instantiate`. It is wrong for complex executors. For `:clojure-fn`, provenance should be attached **after** the runtime validates and applies the actual admitted effects, not before. Your research note on structured knowledge is also pushing toward provenance as first-class, not an afterthought. 

Third, **kernel ownership**. The kernel docs and surrounding architecture treat Clojure/persistent state as the owner of long-lived world structure, with graph/state represented explicitly as data.  Opaque mutation inside executors collapses that ownership boundary.

Fourth, **replay, intervention, and ACL2 trajectory**. If you ever want rule deletion, intervention sensitivity, replayable traces, or translation of state semantics toward verification, you need an explicit transition record. Hidden mutation and a pretty summary kills that path. Your architecture explicitly keeps an ACL2 horizon open for reachable-state invariants. 

## Why final-world-diff is also the wrong contract

A raw diff is better than summary-only, but still wrong.

It loses semantic structure.

`episode-reminding` is not “some recent episode slots changed.” It is a specific kernel operation with retrieval semantics, provenance implications, and cycle-control behavior.

`sprout-alternative-past` is not “some context maps differ.” It is a very specific reversal mechanism: copy old context, pseudo-sprout under new branch, clear emotional residue, prune unrelated plans, rebind ownership. That is a named cognitive move, not just a state diff.

So the right unit is **typed state-change intent**, not opaque mutation and not raw diff.

## What this means for the three family plans

### ROVING

Today it does:

* resolve the selected pleasant episode from structural request/dispatch payload
* sprout a context
* assert success-intends
* run reminding cascade
* update goal next-context
* record mutation info
* later, outside the plan, store a family-plan episode

That should become:

* structural products: probably none, unless you want a typed plan-result fact
* effects:

  * `:context/sprout`
  * `:fact/assert` for success intention
  * `:episode/reminding` with seed episode and active provenance
  * `:goal/set-next-context`
  * `:trace/add-mutation-event`
* summary material:

  * seed episode id
  * reminded episode ids
  * active indices
  * branch context id
  * retrieval/support indices for generic episode storage

### RATIONALIZATION

Today it does:

* pick a stored frame
* sprout a branch
* assert reframe facts
* rewrite trigger emotion
* create hope emotion
* create an afterglow `:family-affect-state`
* update goal next-context
* record mutation info
* later, generic episode storage

That should become:

* structural products:

  * the actual `:family-affect-state` fact, if downstream bridge rules consume it
* effects:

  * `:context/sprout`
  * `:fact/assert` reframe facts
  * `:emotion/replace` trigger emotion
  * `:fact/assert` hope emotion
  * `:fact/assert` success intention
  * `:goal/set-next-context`
  * `:trace/add-mutation-event`
* summary material:

  * frame id
  * reframe fact ids
  * emotion shifts
  * hope situation
  * branch context id
  * episode payload for generic storage

One nitpick here: if `rationalization-plan-dispatch-rule` claims a `:family-affect-state` consequent, the executor/runtime needs to produce **that exact product**. Right now the schema is halfway interface sketch, halfway runtime claim. Pick one. I’d make it a real admitted product.

### REVERSAL

This is the hardest one, because the “plan body” is already spread across target resolution, leaf selection, counterfactual cause lookup, alternative-past sprouting, weak-objective retraction, and later aftershock emission.

That is exactly why summary-only is unacceptable here.

REVERSAL needs an effect language that can express:

* a multi-branch result
* symbolic references to created branch contexts
* parent-context and child-context writes
* composite context surgery ops

I would let it return something like:

* structural products:

  * actual reversal aftershock `:family-affect-state`, if you keep that as a bridgeable fact
* effects:

  * `:reversal/sprout-alternative-past` or an explicit sequence of

    * `:context/copy`
    * `:context/pseudo-sprout`
    * `:context/gc-emotions`
    * `:context/gc-plans`
    * `:planning/rebind-goal-ownership`
  * `:fact/assert` input counterfactual facts
  * `:fact/retract` weak-leaf objectives
  * `:goal/set-next-context`
  * `:trace/add-mutation-event`
* summary material:

  * branch ids
  * selected leaf ids
  * retracted fact ids
  * counterfactual source id
  * retrieval/support indices
  * episode payload

Again, the generic family-plan episode should still be stored **after** these effects are applied, not inside the executor.

## Where episode storage belongs

Keep `store-family-plan-episode` **outside** the executor boundary.

That is the correct separation already visible in current code.

Why?

Because family-plan episode storage is generic kernel persistence, not family-specific cognition. It is the “write this successful plan into episodic memory with structural provenance” step. The executor should return the material needed for that step, but not directly call `add-episode` / `store-episode`.

Same for trace writeback. Same for activation-event logging.

That gives you a clean layering:

1. structural rule routing
2. executor computes effect program
3. runtime validates and applies effects
4. runtime stamps provenance
5. runtime performs generic persistence and trace writeback

## The boundary I would recommend

So, concretely:

* **Executors may be procedural in computation**
* **Executors should not directly mutate `world` as their public contract**
* **`RuleResultV1` should be declarative over state changes**
* **The runtime should apply those changes through owned subsystem operations**
* **Summary-only returns belong one layer up, after application, as reporting artifacts**

If I were naming it cleanly, I’d probably evolve toward:

```clojure
{:consequents [...]
 :effects [...]
 :summary {...}
 :episode-material {...}
 :confidence ...
 :reason ...
 :surface-summary ...}
```

Where:

* `:consequents` = schema-validated, graphable products
* `:effects` = typed kernel ops
* `:summary` = trace-facing outcome
* `:episode-material` = generic post-plan persistence input

That lets `:instantiate`, `:clojure-fn`, and later `:llm-backed` share one real contract.

## The shortest version

`RuleResultV1` should not mean “the executor did stuff.”

It should mean “here is the validated set of products and requested state transitions produced by this rule firing.”

That keeps:

* the graph structural
* provenance attached to admitted outputs
* validation meaningful
* kernel ownership intact
* episode storage generic
* Mueller-style procedural planning available inside Clojure

Hidden `world` mutation plus summary is the easy path, but it just preserves today’s manual seam under a new name. It does not give you a real executor boundary.

Look away from the screen for a moment and drop your shoulders.
