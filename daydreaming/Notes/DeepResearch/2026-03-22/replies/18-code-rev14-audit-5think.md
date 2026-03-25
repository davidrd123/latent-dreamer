I do not have the review-14 memo itself in the provided context. So I’m treating the “post-review-14 claims” as the claims that appear to have landed into the checkpoint docs plus the current code/tests.

The headline: a lot of the memory membrane work is real, the effect boundary is real enough to be useful, but the biggest claimed thing that is still not actually enforced is live rule accessibility. Right now rule-access is mostly a metadata/view layer, not an execution gate.

## Audit of the post-review-14 claims

| Finding                                                                                                | Status                   | Why                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| ------------------------------------------------------------------------------------------------------ | ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1. Store-time durable admission is gone; evaluator cannot directly promote or open rules at store time | **Resolved**             | `goal_families.clj` makes this strict. `family-plan-admission-status` only returns `:trace` or `:provisional`. `store-family-plan-episode` uses that status, and its initial `reconcile-episode-rule-access` path only runs on already-durable episodes, which normal family-plan storage never produces. `family_evaluator.clj` can still emit `:promotion-decision :promote-durable`, but that field is ignored for initial admission.                                                                                                                     |
| 2. Promotion now runs through attributed use/outcome evidence                                          | **Resolved**             | `episodic_memory.clj` has the real substrate now: `note-episode-use` → `resolve-episode-use-outcome` → `record-promotion-evidence` → `eligible-for-promotion?` → `reconcile-episode-admission`. Promotion comes from repeated `:cross-family-use-success`, not from store-time evaluator opinion.                                                                                                                                                                                                                                                            |
| 3. Same-family source reuse stays pending until later cross-family vindication                         | **Resolved**             | `goal_families.clj` `record-family-plan-source-episode-use` records pending same-family use for rationalization/reversal source episodes. `episodic_memory.clj` `vindicate-pending-same-family-uses` only resolves those after later qualifying cross-family evidence, and only if that later evidence comes after the pending use. That is the right direction.                                                                                                                                                                                             |
| 4. Backfire / contradiction / stale logic can demote, suppress retrieval, and later rehabilitate       | **Resolved, first pass** | `resolve-episode-use-outcome` can flag `:backfired`, `:contradicted`, and `:stale`; `reconcile-episode-admission` demotes durable episodes on hard failure or stale use; `stale-rehabilitation-ready?` and the stale-clearing path let later repeated cross-family success rehabilitate the episode.                                                                                                                                                                                                                                                         |
| 5. Promotion requires structural evidence plus outcome evidence                                        | **Partially resolved**   | Outcome evidence is enforced. Structural evidence is not. `eligible-for-promotion?` requires `:payload-exemplar` and `:keep-exemplar`, but those are labels, and `maybe-apply-family-evaluator` + `family_evaluator/normalize-family-evaluation` let the evaluator override those labels. So the outcome side is real; the “structural” side is still partly conventional.                                                                                                                                                                                   |
| 6. Dynamic rule-access layer exists above the structural graph                                         | **Partially resolved**   | `rules.clj` has the full registry/view machinery: `rule-access-registry`, `planning-graph`, `serendipity-graph`, `set-rule-access-status`, `reconcile-episode-rule-access`. Tests cover frontier opening, quarantine, and reopening.                                                                                                                                                                                                                                                                                                                         |
| 7. Rule-access actually gates live activation and planning                                             | **Still open**           | This is the big miss. `goal_families.clj` activation and dispatch paths use raw `activation-rules` / `planning-rules` directly. `activation-candidates-from-trigger-facts`, `plan-payloads-from-request-facts`, and `run-family-plan` do not consult `planning-graph`, `serendipity-graph`, or `rule-access-info` before matching/executing rules. So a quarantined or frontier rule can still fire in the live family pipeline.                                                                                                                             |
| 8. Typed effect boundary with closed effect schemas exists                                             | **Partially resolved**   | For current family dispatch rules, yes. `goal_family_rules.clj` declares `:effect-schema` and `:effect-ops`; `rules.clj` `execute-rule`, `validate-effects!`, and `validate-effect-schema!` enforce allowed ops, order, and closed top-level keys when a schema is present. `goal_families.clj` adds op-specific payload validation with `validate-family-effect!` and whole-program checks with `validate-family-effect-program!`. But `:effect-schema` is still optional globally, so the substrate does not force this discipline on all effectful rules. |
| 9. The denotation layer catches schema-valid but cognitively wrong outputs                             | **Still open**           | The machinery exists in `rules.clj` via `validate-denotation!`, but the current family rules do not use it. In `goal_family_rules.clj`, `denotation` always sets `:validation-fn nil`. So semantic validation beyond shape is mostly absent.                                                                                                                                                                                                                                                                                                                 |
| 10. There is a real `:llm-backed` kernel pilot                                                         | **Still open**           | Not in RuleV1. `rules.clj` `invoke-llm-executor` still throws “not implemented yet”. The current evaluator pilot lives in `family_evaluator.clj` as an external HTTP seam, not as a RuleV1 `:llm-backed` executor.                                                                                                                                                                                                                                                                                                                                           |
| 11. Frontier behavior is live, not just test scaffolding                                               | **Still open**           | In `goal_family_rules.clj`, `rule-provenance` defaults deployment-role to `:authored-core`, and nearly all rules use that default. So the runtime registry starts with basically everything accessible unless tests override provenance. The frontier/quarantine logic is real, but the live rule set does not yet encode much frontier structure.                                                                                                                                                                                                           |
| 12. The full chain is genericized, not family-local                                                    | **Still open**           | Storage/admission/use wiring is still manual in `goal_families.clj`. `run-family-plan`, `store-family-plan-episode`, and `record-family-plan-source-episode-use` do a lot of family-specific work that the docs want to be substrate-level. A new family can still bypass the discipline by omission.                                                                                                                                                                                                                                                        |

## What is actually solid now

These are the parts I’d treat as genuinely landed:

* Store-time durable admission is blocked.
* Repeated cross-family success is the current promotion path.
* Same-family source reuse no longer self-promotes.
* Stale / contradicted / backfired episodes can be suppressed and demoted.
* The effect boundary for the current family dispatch rules is substantially better than before.
* Builtin effect application fails closed in the places that matter most right now, especially `:goal/set-next-context`.

## What is only half true

Two checkpoint stories are only half true in the current code.

### “Promotion needs structural + outcome evidence”

The outcome side is real. The structural side is not yet hard.

Right now the structural gate is:

* episode is `:provisional`
* `:retention-class == :payload-exemplar`
* `:keep-decision == :keep-exemplar`
* no hard anti-residue flags

The problem is that those labels are still evaluator-overridable. So the model can indirectly decide which episodes are even eligible for later durable promotion. That is too much authority if the claim is “evaluator is only a gate/veto”.

### “Rule-access is now layered above the structural graph”

It is layered above the graph as data. It is not layered above execution.

That difference matters. Right now the live pipeline still does:

* raw trigger scanning
* raw activation rule application
* raw request/dispatch matching

without checking whether the rule is accessible, frontier, or quarantined.

So the strongest mismatch between story and code is this:

**rule-access currently shapes graph views and provenance scoring, not actual rule firing.**

## Is the current promotion/admission discipline strict enough for an `:llm-backed` evaluator pilot?

For a **narrow external evaluator pilot**, yes, with constraints$_{70%}$.

For a **true in-kernel RuleV1 `:llm-backed` pilot**, no$_{25%}$.

Why the narrow external pilot is acceptable:

* the evaluator cannot directly create durable admission
* it cannot directly open frontier rules
* the broader architecture already wants a propose → validate → admit loop and explicit process around the model rather than free-form authority 

Why the current contract is still too loose for a real `:llm-backed` kernel rule:

* the evaluator can still directly set `:retention-class`
* it can directly set `:keep-decision`
* it can directly set `:anti-residue-flags`
* denotational validation is mostly absent
* `invoke-llm-executor` is not implemented, so the kernel path you’d actually want to trust does not exist yet

My read is:

* **safe pilot**: evaluator returns realism/desirability and maybe advisory veto signals
* **unsafe pilot**: evaluator returns authoritative retention labels and truth-valued anti-residue flags

If you keep the current evaluator output contract, the model still has too much structural authority.

## Should `:effect-schema` stay optional?

Not in the current loose form.

My recommendation:

* **Keep it optional for pure `:instantiate` rules that emit no effects.**
* **Make it required, closed, and order-sensitive for every rule that can emit `:effects`, especially every `:clojure-fn` and future `:llm-backed` rule.**

So I would not make `:effect-schema` globally mandatory across all RuleV1 objects. That would add noise to simple structural rules. But I would absolutely make it mandatory across the executor/effect boundary.

Right now the family dispatch rules already behave that way. The substrate does not.

Before any real `:llm-backed` executor pilot, I would require:

1. declared `:effect-schema` for every effectful rule
2. non-nil denotation validator for every `:llm-backed` rule
3. static validation of symbolic producers/consumers, including `:context-ref`, not just `:from-*` result keys
4. a closed domain schema for any non-effectful LLM result, not just the generic RuleResultV1 shell

## Where the full chain still relies on convention instead of enforcement

### Activation

`goal_families.clj` builds activation candidates from raw rules. No live access check.

### Dispatch

`plan-payloads-from-request-facts` uses raw `planning-rules`, not filtered accessible rules.

### Effect execution

Current family dispatch rules are fairly strict, but only because family code passes `family-rule-call-base` with validators. A new family can skip that.

### Episode storage

`run-family-plan` and `store-family-plan-episode` hand-assemble storage behavior. There is no generic substrate-level `:episode-material` contract enforcing this.

### Admission

Admission ignores `:promotion-decision`, which is good. But it still trusts evaluator-controlled `:retention-class`, `:keep-decision`, and flags.

### Use / outcome

Attribution is only wired for the currently implemented paths. New families can forget to record use, forget to resolve outcome, or invent different local conventions.

### Promotion

Promotion evidence is unified around repeated cross-family success, which is good. But eligibility is still label-based, not independently structural.

### Rule-access reconciliation

This only happens in the places that currently remember to call it. It is not yet a guaranteed substrate transition attached to every admission change.

## Bottom line

The kernel is good enough to support another round of disciplined iteration.

It is **not** in the state the docs imply if you read those docs as saying “frontier/quarantine now genuinely controls live cognition” or “structural evidence for promotion is no longer evaluator-defined”.

The single biggest thing I’d fix next is:

**make rule-access actually gate activation and dispatch.**

The second biggest is:

**stop letting the evaluator author retention-class / keep-decision as if those were structural facts.**

If those two are fixed, the rest of the current architecture gets a lot more honest very quickly.

Look away from the screen for a moment. Drop your shoulders. Unclench your jaw.
