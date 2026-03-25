Verdict: the patch fixes the actual seam you were aiming at.

Before this patch, `:goal-family/rationalization-plan-dispatch` could truthfully emit a structural `:family-affect-state` consequent while the runtime world still got no such fact. That happened on the bridged `reversal-aftershock -> rationalization` path because the structural side already had a fallback to the goal’s stored trigger fields, but `handle-rationalization-assert-afterglow` only looked at `:rationalization/diversion`. When `select-trigger-emotion` failed, diversion was empty, `rationalization-afterglow-fact` returned nil, and `cross-family-trigger-facts` had nothing to consume. The new fallback fields close that gap. That fix looks real$_{95%}$.

The key thing you fixed is this mismatch:

* structural path: `rationalization-plan-dispatch-executor` always had an afterglow consequent, even with no live diversion
* runtime path: `handle-rationalization-assert-afterglow` used to require live diversion data
* bridge consumer: `cross-family-trigger-facts` only reads asserted context facts with `:rule-provenance`, not executor consequents

So yes: on the bridged reversal→rationalization path, the patch makes the promised `:family-affect-state` actually exist at runtime.

The new test is the right test. The older `rationalization-plan-dispatch-emits-afterglow-consequent-with-diverted-strength` only proved executor shape. It did not prove world mutation. The added `cx/fact-true?` assertion on the bridged path is the missing coverage.

There is one regression risk in the patch, and it’s not hypothetical.

`rationalization-effect-program` now always emits `:fallback-trigger-emotion-id`, and `validate-family-effect!` will reject it if it is nil. But `rationalization-plan-ready-facts` derives that id only from goal metadata (`:trigger-emotion-id` or `:main-motiv`). `run-family-plan` does not let the caller pass it explicitly. So a manually-created rationalization goal with no stored motivator, but with a perfectly valid live dependency-linked emotion in the trigger context, used to be able to run because the runtime diversion lookup could recover the emotion. After this patch, that same case fails effect validation before execution. That regression looks real$_{85%}$.

Why that matters: you’ve silently tightened the API contract from “live context can rescue missing goal trigger metadata” to “goal trigger metadata must already be present.” Automatic activations are fine, because `activate-family-goals` stores the trigger fields. Manual entrypoints are where this bites.

There’s also a remaining asymmetry nearby, not introduced by this patch:

`reversal-plan-dispatch-executor` still has the same structural/runtime split. It emits a structural affect proxy, but the operational reversal aftershock fact is not produced by the rule effect program. It gets synthesized later in `run-family-plan` via `reversal-aftershock-fact`. So the invariant “dispatch promised affect-state ⇒ runtime world contains affect-state” is still not a general property of the rule/effect layer. It’s family-specific glue. That matters because the reversal bridge rules only work if callers go through `run-family-plan`, not merely `reversal-plan-effects` or `reverse-leafs`.

One more nit:

On the fallback path, the runtime world now gets a hopeful `:family-affect-state` even when no actual emotion diversion happened and the branch still has empty `:emotion-shifts` / `:emotional-state`. That is probably the right bridge contract. But it means `:family-affect-state` is now a summary fact, not proof that local emotion facts were mutated. If any later code starts treating it as proof of local emotional state change, that code will be wrong. I’d call this a semantics caveat, not a current bug.

Net:

* The targeted bridged afterglow seam is fixed.
* The patch does not appear to break the existing cross-family consumers that read runtime affect-state facts.
* It does introduce a new validation-time failure mode for manually-created rationalization goals missing stored trigger-emotion metadata.
* The broader “structural affect facts are always operationally materialized” story is still uneven across families.

So: good patch, real fix, but not clean enough to call the whole affect-state contract solved.
