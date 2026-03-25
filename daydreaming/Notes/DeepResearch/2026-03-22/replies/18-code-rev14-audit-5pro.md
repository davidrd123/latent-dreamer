I’m treating the “post-review-14 claims” as the ones now asserted in `build-order-checkpoint-2026-03-22.md` and `extension-consolidation.md`, since the selected context does not include a standalone review-14 note.

## Audit

1. **Store-time evaluator output no longer grants durable admission or opens frontier rules.**
   **Resolved.**
   The positive shortcut is gone. `family_evaluator.clj` still produces `:promotion-decision`, but `goal_families.clj::family-plan-admission-status` never returns `:durable`; it returns `:trace` for `:archive-cold`, otherwise `:provisional`. Then `goal_families.clj::store-family-plan-episode` only calls `rules/reconcile-episode-rule-access` when the admission status is already `:durable`. That never happens from evaluator output. The tests named `evaluator-promotion-decision-does-not-open-frontier-rules-at-store-time` and `hard-failure-family-plan-admission-does-not-open-frontier-rules-at-store-time` match the code.

2. **The evaluator now acts only as gate/veto, not as direct authority.**
   **Partially resolved.**
   Positive authority is gone. Negative authority is not. `goal_families.clj::maybe-apply-family-evaluator` blindly merges normalized evaluator output into the evaluation. That merged output can still:

   * force `:keep-decision :archive-cold`, which `family-plan-admission-status` turns into `:trace` immediately
   * attach `:backfired`, `:contradicted`, or `:stale` flags at storage time
   * in source-reuse paths, directly demote older episodes via `goal_families.clj::record-family-plan-source-episode-use` → `immediate-family-plan-source-outcome` → `episodic_memory.clj::resolve-episode-use-outcome` → `reconcile-episode-admission` → `rules/reconcile-episode-rule-access`

   So the evaluator cannot durabilize or open rules, but it can directly cold-store new material and directly demote old material. That is more than “gate/veto.”

3. **Retrieval/reminding no longer counts as use or promotion evidence.**
   **Resolved for the live roving path.**
   `goal_families.clj::roving-effect-program` emits `:episode/reminding` and `:episode/assert-retrieval-hits`, but not `:episodes/note-family-uses` or `:episodes/resolve-use-outcomes`. The live effect path therefore records retrieval hits only. The tests `roving-reminding-does-not-promote-retrieved-family-plan-episodes` and `stored-reversal-family-plan-episode-feeds-later-roving` reflect that.

4. **Same-family source reuse stays pending until later cross-family vindication.**
   **Resolved in the slices that are actually wired.**
   `goal_families.clj::record-family-plan-source-episode-use` records pending source-use for stored rationalization/reversal episodes. It only resolves immediately on hard negative evaluator flags. Otherwise it stays pending. Later vindication lives in `episodic_memory.clj::vindicate-pending-same-family-uses`, which only resolves those same-family uses after later qualifying cross-family evidence, and does not create fresh promotion evidence during vindication. The corresponding tests are present for rationalization and reversal reuse.

5. **Repeated failed same-family use can flag stale and demote; repeated later cross-family success can rehabilitate.**
   **Resolved as a first-pass substrate.**
   This is real code, not doc fiction:

   * `episodic_memory.clj::resolve-episode-use-outcome` flags `:backfired` / `:contradicted` immediately and flags `:stale` after repeated same-family failed use
   * `episodic_memory.clj::reconcile-episode-admission` demotes durable episodes on hard failure or stale use
   * stale rehabilitation is implemented via `stale-rehabilitation-ready?` and `clear-episode-flag`
   * retrieval accessibility respects those flags in `retention-accessibility-info` and `episode-accessibility-info`

6. **Promotion is now later evidence-driven rather than retrieval-driven or evaluator-driven.**
   **Partially resolved.**
   The negative half is resolved: raw retrieval does not promote, and evaluator output does not directly promote. The positive half is narrow:

   * promotion evidence comes from `episodic_memory.clj::record-promotion-evidence`
   * `eligible-for-promotion?` requires `:provisional`, `:payload-exemplar`, `:keep-exemplar`, no hard negative flags, and repeated `:cross-family-use-success`

   That is a real membrane. But in live code, the broader cross-family use/outcome path is barely wired. The promotion path mostly exists in the substrate and in tests, not across the whole running family pipeline.

7. **Broader family coverage for episode use/outcome is in place.**
   **Still open.**
   The substrate exists, but the live effect programs do not use it. `goal_families.clj` defines handlers for `:episodes/note-family-uses` and `:episodes/resolve-use-outcomes`, but none of `roving-effect-program`, `rationalization-effect-program`, or `reversal-effect-program` emits those ops. Current live attribution coverage is basically:

   * pending source-use for stored rationalization episodes
   * pending source-use for stored reversal episodes
   * immediate negative source-use outcomes when evaluator flags backfire/contradiction

   That is not “broader coverage.” It is a narrow slice.

8. **Rule-access frontier/quarantine is real and the planner/serendipity layers actually respect it.**
   **Partially resolved, with one serious hole.**
   The substrate is real:

   * registry: `rules.clj::rule-access-registry`, `sync-rule-access-registry`
   * graph views: `planning-graph`, `serendipity-graph`
   * transitions: `set-rule-access-status`, `reconcile-episode-rule-access`

   But the firing path does **not** actually consult those filtered views. `goal_families.clj::activation-candidates-from-trigger-facts`, `plan-request-facts-from-ready-facts`, and `cross-family-trigger-facts` iterate raw `activation-rules`, `planning-rules`, and `cross-family-rules`. So quarantined/frontier state affects graph views and provenance scoring, but not actual rule application. That is a real bug relative to the claimed architecture.

9. **The typed effect boundary with closed declarations exists.**
   **Partially resolved.**
   For the current family dispatch rules, yes:

   * closed effect shape: `goal_family_rules.clj` dispatch rules declare `:effect-schema`
   * runtime enforcement: `rules.clj::validate-effects!`, `validate-effect-schema!`, `validate-effect-program!`
   * op-specific payload checks: `goal_families.clj::validate-family-effect!`
   * whole-program producer checks: `validate-family-effect-program!`
   * effect runtime: `rules.clj::apply-effects`

   But the contract is not globally closed:

   * `:effect-schema` is still optional in `rules.clj::valid-rule?`
   * `:consequents` are still open-world; `validate-consequents!` checks required keys/pattern match, but does **not** reject undeclared extra keys
   * `RuleResultV1` does not require `:effects`
   * the live `:llm-backed` executor path is unimplemented

   So the membrane is solid for the three current family dispatch rules, not for RuleV1 in general.

10. **The first `:llm-backed` evaluator pilot exists in the kernel proper.**
    **Partially resolved at best.**
    There is an external evaluator seam in `family_evaluator.clj`, including live Anthropic calls and normalization. But the RuleV1 `:llm-backed` path does not exist yet: `rules.clj::invoke-llm-executor` still throws `"LLM-backed rule execution is not implemented yet"`. So the claim is only true if you define “pilot” as “external evaluator function hanging off `goal_families.clj`.” It is false if you mean a real RuleV1 `:llm-backed` executor.

11. **The denotational contract now catches schema-valid but semantically wrong outputs.**
    **Still open.**
    The substrate exists in `rules.clj::validate-denotation!`, but the current rule registry does not use it. `goal_family_rules.clj::denotation` sets `:validation-fn nil` everywhere. So today there is no active semantic validator between “schema-valid” and “cognitively valid.” That is exactly the hole that matters most before effectful `:llm-backed` rules.

## Is the admission/promotion discipline strict enough for an `:llm-backed` evaluator pilot?

**As currently wired, no.**
**As a narrow advisory or veto-only pilot, yes.**

Why “no” as currently wired:

* The evaluator bypasses RuleV1 entirely. It does not go through `rules.clj::execute-rule`, `:llm-backed`, `:denotation`, or any closed executor contract.
* The evaluator still has direct negative authority. It can set `:archive-cold` on the new episode, and in source-reuse paths it can directly drive `:backfired` / `:contradicted` outcomes on old episodes through `record-family-plan-source-episode-use`.
* Promotion evidence is real, but the live positive path is under-wired. The system is safe partly because it under-promotes, not because the full contract is enforced.
* Rule-access is not actually in the firing path, so even if evaluator-driven demotion quarantines a rule, current activation/dispatch code can still apply that rule.

That means the membrane is conservative by omission, not by complete enforcement.

A pilot is safe if you narrow it to this:

* evaluator produces advisory realism/desirability/reasoning
* evaluator may veto durable promotion
* evaluator may not emit hard semantic flags like `:contradicted` or `:backfired`
* evaluator may not directly choose `:archive-cold`
* evaluator output must go through a closed schema, ideally a RuleV1 path or equivalent

## Should `:effect-schema` stay optional?

**No. Make it required for every `:clojure-fn` and `:llm-backed` rule. Keep it optional only for pure `:instantiate` rules.**

I would make the rule this simple:

* `:instantiate` may omit `:effect-schema`
* `:clojure-fn` and `:llm-backed` must declare `:effect-schema`, possibly `[]`
* corresponding results must always include `:effects`, possibly `[]`

Why:

* Right now one future executor can silently fall back to “allowed op set only” and skip closed payload validation.
* The current family dispatch rules already prove the closed form is workable.
* An evaluator rule with no operational effects can declare `:effect-schema []`. That is not burden; it is clarity.

But this is not sufficient by itself. Before the evaluator pilot, you also need a **closed non-effect output contract**. Right now `:consequents` are open-world, and `denotation.validation-fn` is unused. So even with mandatory `:effect-schema`, a model can still return extra consequent keys or semantically bad but schema-matching outputs.

My concrete recommendation:

1. require `:effect-schema` for all non-`instantiate` rules
2. require `:effects` in all non-`instantiate` `RuleResultV1`s, with `[]` allowed
3. add closed consequent validation or a `:result-schema` for non-effect outputs
4. require non-nil denotation validators for every `:llm-backed` rule

## Where the end-to-end chain still relies on convention

This is the part that matters most.

**Activation → dispatch**
Still conventional. `activation-candidates-from-trigger-facts` discards the typed `:family-plan-ready` consequent via `primary-payload-consequent!`. `activate-family-goals` then writes a goal directly. Later, `roving-plan-ready-facts`, `rationalization-plan-ready-facts`, and `reversal-plan-ready-facts` reconstruct “ready” facts procedurally from goal/world state. So the structural rule chain exists on paper and in the graph, but not as a persisted enforced artifact chain.

**Rule-access → actual rule firing**
Still conventional. The filtered graph views exist, but actual rule application still iterates raw registries:

* `activation-candidates-from-trigger-facts`
* `plan-request-facts-from-ready-facts`
* `cross-family-trigger-facts`

So “quarantined” is not yet a real execution membrane.

**Dispatch result → episode storage**
Still conventional. `store-family-plan-episode` does not consume a validated `episode-material` contract from `RuleResultV1`. It reconstructs storage inputs from the ad hoc `family-plan` map. `aux-indices` from `execute-rule` are not the storage contract.

**Evaluator output → admission**
Still conventional. `maybe-apply-family-evaluator` merges model output into evaluation metadata with enum normalization, but no semantic cross-check. Inconsistent combinations can survive as dead metadata, and hard negative flags can have direct consequences.

**Use/outcome coverage → promotion**
Still conventional in breadth. The substrate is strict when invoked, but most live family paths do not emit use/outcome ops, so promotion depends on narrow hand-wired slices and test helpers.

**Consequent semantics**
Still conventional. `validate-denotation!` exists, but every current rule uses `validation-fn nil`. For `:llm-backed`, that is not good enough.

## Bottom line

The kernel has fixed the worst shortcut: **model output can no longer directly durabilize memory or open frontier rules at storage time.** Good.

But the architecture is **not** closed enough to claim the whole review-14 story is resolved. The two biggest remaining problems are:

1. **rule-access is not in the actual firing path**
2. **the evaluator still has direct negative authority and still bypasses RuleV1**

So the honest verdict is:

* the memory membrane is **safer**
* the effect membrane is **real for current family dispatch rules**
* the full enforcement chain is **not complete**
* a true RuleV1 `:llm-backed` evaluator pilot is **not ready without another tightening pass**
