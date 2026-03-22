I see four real bugs and three contract gaps.

The good news first: the core seam in `rules.clj` is doing real work now. `execute-rule` + `validate-consequents!` + effect-op allowlisting is a real upgrade, and the old falsey-initial-state bug in `apply-effects` is already fixed in the current file. I would not spend time re-litigating that.

The bad news: composition above the seam is still leaky.

## Real bugs

### 1. REVERSAL’s new “no result” semantics do not compose upward

You intentionally made `reversal-plan-dispatch-executor` return `nil` when there are no executable branches. `matched-rule-applications` drops that. `reversal-plan-effects` then returns `nil`. That part is coherent.

`reverse-leafs` is not.

It destructures `{:keys [effects]}` from `(reversal-plan-effects ...)` and immediately calls `apply-family-effects` on `effects`. If `reversal-plan-effects` returned `nil`, `effects` is `nil`, and `apply-effects` throws because it requires a vector.

So the seam says “nil means veto/no executable plan”, but the next layer still behaves like “I always get an effect vector”.

That means the tested no-result path only works at the `reversal-plan-effects` boundary. `reverse-leafs` and `run-family-plan` still crash on the exact case you added nil-semantics for.

Fix: make `reverse-leafs` explicitly propagate the nil case, probably as `[world []]` or `[world nil]`, and make `run-family-plan` handle it.

### 2. `false` from an executor is silently swallowed as “no result”

`execute-rule` uses `(when result ...)`. `matched-rule-applications` also uses `when result`.

In Clojure, that means both `nil` and `false` disappear.

Only `nil` should mean “no result”. `false` is almost certainly an executor bug, and right now it bypasses all validation and vanishes.

That is a real silent regression vector.

Fix: treat only `nil` as no-result. If `false` comes back, throw. Better: use an explicit sentinel like `::no-result` and stop overloading `nil`.

### 3. Bridged RATIONALIZATION can fail to assert afterglow even though the structural rule says it emitted one

This is the nastiest one.

The dispatch rule for rationalization structurally claims a `:family-affect-state` consequent. The executor also manufactures an afterglow proxy fact, with a fallback path when `rationalization-diversion-preview` is nil.

But the runtime does **not** assert that proxy fact.

Instead, `handle-rationalization-assert-afterglow` rebuilds a fresh fact from the `:rationalization/diversion` result. If diversion data is missing, it returns `nil` and asserts nothing.

Now look at your bridged reversal → rationalization path. In the test setup, you deliberately retract the dependency fact to force the bridge path. That makes `select-trigger-emotion` / `rationalization-diversion-preview` go nil. The executor still returns a fallback afterglow proxy. The effect runtime likely asserts **no** afterglow fact.

So the structural rule and executor say “afterglow exists”, but the world state says “no afterglow”.

That will break later `rationalization-afterglow-to-roving` bridging on the bridged path.

You already have the exact test setup that should expose this. You just never assert `:affect-state-fact` is non-nil there.

Fix: single source of truth. Either:

* assert the exact proxy fact the executor already computed, or
* put the same fallback logic into `handle-rationalization-assert-afterglow`.

Right now you have preview logic in one place and assertion logic in another. They diverged.

### 4. `resolve-effect-context-id` can silently corrupt world state

This function is too cute:

* if a symbolic `:context-ref` resolves, good
* otherwise it falls back to the raw keyword

Because real context ids and symbolic refs are both keywords, typos and misordering are ambiguous.

That becomes dangerous because some handlers throw on bad contexts, but others do not:

* `handle-context-set-ordering` can happily `assoc-in` a bogus `[:contexts :branch-context ...]`
* `handle-goal-set-next-context` can store `:branch-context` as a goal’s `next-cx`
* logging handlers can record garbage and look fine

So an unresolved ref is not guaranteed to fail fast. Sometimes it silently writes junk.

Fix: `:context-ref` should only mean symbolic ref. If you want literal context ids, use a separate field. At minimum, if a ref does not resolve and is not an existing context in `world[:contexts]`, throw.

## Contract gaps that will become bugs

### 5. `:from-*` result-key wiring is not validated, and many bad cases fail silently

You validate each effect map in isolation. You do **not** validate the program.

That means these regressions are currently possible:

* bad `:from-reminding` in `:episode/assert-retrieval-hits` → asserts retrieval-hit facts with `episode-id nil`
* bad `:from-reminding` in `:episodes/note-family-uses` → silently produces zero uses
* bad `:from-uses` in `:episodes/resolve-use-outcomes` → silently produces zero promotions
* bad `:from-diversion` in `:rationalization/assert-afterglow` → silently produces nil afterglow
* logging effects happily record partial nil data

This is exactly the kind of regression that a seam is supposed to block.

Fix: add a preflight validator over the **whole effect vector**. Track produced `:result-key`s and `:ref`s, and reject any consumer that points to a missing or forward-declared producer.

### 6. Effect ordering is implicit but unchecked

Allowlisting is not a contract.

Right now a roving executor could emit a legal-but-wrong effect program like:

* `:goal/set-next-context`
* then `:context/sprout`

and it would pass validation, because each effect shape is individually legal.

Same for omitting `:episode/reminding` entirely, or resolving use outcomes before note-family-uses.

You need either:

* an exact effect-program schema per dispatch rule, or
* at least partial-order constraints.

Otherwise the seam only catches malformed maps, not malformed programs.

### 7. `:reversal/execute-branches` branch validation is too weak

You currently validate only “vector of maps”.

That is nowhere near enough for what the handler assumes.

Per-branch you should validate at least:

* `:old-context-id` keyword
* `:leaf-goal-id` keyword
* `:ordering` number
* `:objective-facts` vector of maps, if present

Right now some bad branches will throw late, and others will “work” while emitting garbage `:retracted-fact-ids`.

## Structural graph vs runtime: currently not faithful

This is not just philosophy. It affects provenance and graph analysis.

Right now:

* RATIONALIZATION and REVERSAL dispatch rules structurally claim `:family-affect-state` consequents
* but runtime assertion of those facts happens elsewhere, reconstructed later
* meanwhile ROVING emits retrieval-hit / episode-use / episode-outcome / episode-promotion facts that the structural rule graph does not model at all

So the structural graph both:

* overclaims direct dispatch emission for afterglow/aftershock
* underclaims important runtime-emitted facts for roving reuse/promotion

That means `build-connection-graph`, provenance expectations, and intervention-style analysis are not describing literal runtime emissions. They are describing a cleaned-up structural story.

That’s fine if you say so explicitly. It is not fine if you treat the graph as an execution-faithful account.

## Tests I’d add immediately

1. `run-family-plan` reversal path returns nil/empty cleanly when dispatch returns no result.
2. `execute-rule` rejects `false` executor results.
3. Bridged rationalization still asserts a non-nil afterglow fact when diversion preview is unavailable.
4. Misspelled or forward `:context-ref` throws before any world update.
5. Bad `:from-reminding` / `:from-diversion` keys fail validation instead of degrading silently.
6. Malformed reversal branch maps fail in `reversal-plan-effects`, not inside execution.

If I were patching in order:

1. Guard `reverse-leafs` / `run-family-plan` for reversal nil-results.
2. Make `false` illegal in `execute-rule`.
3. Kill raw `:context-ref` fallback.
4. Add whole-program effect validation.
5. Unify afterglow/aftershock so the fact the graph claims is the fact the runtime actually asserts.

Right now the seam is useful, but it still lets the worst class of bug through: “program is structurally plausible, runtime quietly did something else.”
