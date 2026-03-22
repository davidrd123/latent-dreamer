## Findings

1. **The evaluator currently has more authority than your docs say it should.**
   Right now, evaluator output can directly produce `:durable` admission via `family-plan-admission-status`, and `store-family-plan-episode` will immediately run `reconcile-episode-rule-access` on that durable episode. That means a live evaluator can upgrade a fresh family-plan episode into durable precedent and open frontier rules with no attributed downstream success at all. Your docs say the opposite: promotion should require structural evidence plus outcome evidence, and the evaluator should be gate/veto, not sole authority (`build-order-checkpoint-2026-03-22.md`, `extension-consolidation.md`; `goal_families.clj` / `family-plan-admission-status`, `store-family-plan-episode`).
   That is the main architectural mismatch. Until this changes, the evaluator pilot is not just “scoring memory.” It is a direct authority path into memory and rule access.

2. **The frontier exists in state, but not yet in runtime behavior.**
   `rules.clj` has a real access registry plus `planning-graph` and `serendipity-graph`, but the family runtime still enumerates raw rule registries in places that matter. `cross-family-trigger-facts` walks `cross-family-rules` directly. Activation and planning paths also use raw rule lists. So `:frontier` / `:quarantined` currently affect graph views and provenance scoring, not actual rule firing (`rules.clj` / `planning-graph`, `serendipity-graph`; `goal_families.clj` / `cross-family-trigger-facts`, `activation-rules`, `planning-rules`).
   So promotion→rule-access is only half-correct. The state machine is there. The enforcement path is not.
   **First real frontier rule:** do not use a dispatch rule or a base activation rule. Those are core repertoire. The first frontier rule should be an optional bridge with narrow preconditions and clear failure semantics. Among the current rules, `:goal-family/reversal-aftershock-to-rationalization` is the cleanest candidate (`goal_family_rules.clj` / `reversal-aftershock-to-rationalization-rule`). The system still works with it closed, and opening it actually expands repertoire.

3. **The use/outcome substrate still confuses retrieval with use, then calls that evidence.**
   On the roving path, `note-family-plan-episode-uses` records a use for the seed episode *and every reminded episode*. Then `resolve-family-plan-use-outcomes` marks all cross-family uses `:succeeded` and all same-family uses `:failed`, purely from family identity. That is not attributed later outcome. That is “retrieved while another family ran” (`goal_families.clj` / `note-family-plan-episode-uses`, `resolve-family-plan-use-outcomes`; `episodic_memory.clj` / `note-episode-use`, `resolve-episode-use-outcome`).
   This is the sharpest evidence pathology in the current Step 1 substrate. A reminded episode can become durable because it was surfaced, not because it materially shaped a later branch.
   The rationalization/reversal source-episode path is better, because the stored frame/counterfactual actually shapes the new branch. But even there, outcome is still inferred immediately from the new branch’s evaluation, which conflates “new branch looked good” with “source episode deserved credit” (`goal_families.clj` / `record-family-plan-source-episode-use`, `immediate-family-plan-use-outcome`).

4. **Your outcome lattice is too small, and the meaning of “success” changes across paths.**
   Valid use outcomes are only `:succeeded`, `:failed`, `:backfired`, `:contradicted` (`episodic_memory.clj` / `valid-episode-use-outcomes`). There is no neutral, stale, abandoned, insufficient-evidence, or merely-considered state. Yet the evaluator taxonomy already has `:stale`, and the docs explicitly want longer-run consolidation based on attributed outcomes (`extension-consolidation.md`; `build-order-checkpoint-2026-03-22.md`).
   Worse, “success” is inconsistent. Roving uses family inequality as success. Source-episode reuse for rationalization/reversal can succeed on same-family reuse if the new plan evaluates well (`goal_families.clj` / `resolve-family-plan-use-outcomes`, `immediate-family-plan-use-outcome`).
   Longer runs will turn this into garbage stats.

5. **`:same-family-loop` should be relative, not lifetime-absolute.**
   Current logic flags loop risk when same-family use count crosses a fixed threshold before any cross-family use has happened. One cross-family use at any time disables future triggering forever. Once flagged, the flag persists and suppresses same-family reentry in retrieval (`episodic_memory.clj` / `same-family-loop-threshold`, `note-episode-use`, `retention-accessibility-info`).
   That is wrong for longer runs.
   It should be relative to a recent window and recent corroboration, not absolute lifetime totals. You want something like: “recent same-family reuse without external corroboration or downstream success is high,” not “this episode once had two same-family uses in 2026.”

6. **The executor boundary is honest enough for a narrow evaluator pilot, and not honest enough for a generic `:llm-backed` rule.**
   `execute-rule` does real work now: result-shape validation, consequent validation, denotational validation, and a minimal effect-op allowlist (`rules.clj` / `execute-rule`, `validate-rule-result-shape!`, `validate-effects!`, `validate-consequents!`, `validate-denotation!`). That is enough for a post-plan evaluator *if* the evaluator returns a sealed, non-mutating packet and the kernel stays sole owner of storage, admission, and access transitions.
   But the generic `:llm-backed` seam is still fake. `invoke-llm-executor` is unimplemented. Effect validation only checks op names, not per-op schema or symbolic refs. Effect application is still bespoke in `apply-family-effect`. And the docs have already moved past the current `RuleResultV1`: they talk about `:effects`, `:summary`, and `:episode-material`, while `rules.clj` still validates the older core result shape and only optionally accepts `:effects` (`kernel-rule-schema-and-execution-model.md`; `build-order-checkpoint-2026-03-22.md`; `rules.clj`).
   So the split is: **current evaluator seam, yes$_{80%}$**. **generic `:llm-backed` executor, no$_{90%}$**.

7. **The minimum missing validator is semantic, not syntactic.**
   `family_evaluator.clj` already does the right syntactic things: JSON-only parse, enum normalization, allowlists, fallback to defaults (`family_evaluator.clj` / `parse-json-text`, `normalize-family-evaluation`, `build-family-evaluator`). Keep that pattern.
   What is missing before LLM output should “cross the boundary” is kernel-side admissibility:

   * the model must not emit `:effects`, ids, or direct state mutations;
   * `:promote-durable` must be treated as a request, not a transition;
   * hard flags like `:backfired` / `:contradicted` should not by themselves open demotion/access transitions unless kernel evidence agrees;
   * fallback/normalization events should be logged, not silently absorbed.

   The minimum safe rule is: **LLM proposes triage, kernel decides transitions.**

8. **The future evaluator is currently looking at the wrong packet.**
   `build-family-evaluation-packet` sends family, selection, episode payload, emotion shifts/state, retrieval indices, support indices, and a trimmed result map. `build-user-message` also includes the default kernel evaluation and an output example (`family_evaluator.clj` / `build-family-evaluation-packet`, `build-user-message`).
   That packet is okay for branch-local flavor judgment. It is not okay for gate/veto on durability, anti-residue, or access. It is missing:

   * which source episodes were merely retrieved vs materially used,
   * source episode admission status / prior flags / prior use history,
   * actual downstream evidence window,
   * contradiction/backfire evidence after the branch,
   * which rule-access transition would follow from the decision.

   It also includes low-level tokens like raw indices, which are weak evidence for an evaluator.
   The evaluator should see a typed evidence packet, not an implementation packet.

9. **You are not storing enough evaluator provenance for post-mortem debugging.**
   `call-family-evaluator` attaches provider/model, but `normalize-family-evaluation` drops them, and the stored evaluation fact does not preserve raw response or model/version (`family_evaluator.clj` / `call-family-evaluator`, `normalize-family-evaluation`; `goal_families.clj` / `family-plan-evaluation-fact`).
   Once evaluator output can influence durability or future rule access, you will want exact model/version/prompt provenance. Otherwise the audit trail is mush.

---

## Open questions / assumptions

1. I am assuming `:durable` means “earned the right to bias future planning,” not merely “worth keeping around.” If you mean the weaker thing, Finding 1 softens a bit, but then your docs are using “durable” ambiguously.

2. I am assuming frontier is meant to gate actual firing, not only graph visibility/provenance scoring. If frontier is currently intended as descriptive state only, Finding 2 becomes design debt rather than runtime bug.

3. I am assuming you eventually want two distinct evaluative objects:

   * evaluation of the **new family-plan episode**, and
   * evaluation of the **source-episode use outcome**.
     The current code partly fuses these. That fusion is one source of bad evidence.

4. I am assuming same-family loop risk should be measured per recent regime, probably per family-pair or payload-cluster, not per episode lifetime. That seems aligned with the longer-run consolidation docs.

---

## Brief synthesis

Step 1 is real. Admission tiers, cue zones, anti-residue flags, use history, and rule-access state are not hand-wavy anymore. Step 2 is partly real. `execute-rule` is a real validator for pure executors and local declarative-effect programs.

The remaining fake seam is the handoff from **evidence** to **authority**. Right now:

* “use” partly means retrieval,
* “outcome” partly means family inequality or immediate evaluator impression,
* “durable” can still be evaluator-issued,
* “frontier” still does not gate rule firing.

So the next move is not more family migration, and not a generic `:llm-backed` executor. The next move is to harden the transition semantics:
**retrieved vs used vs vindicated**, **advisory vs admissible**, **descriptive frontier vs operative frontier**. Once those are clean, the evaluator pilot becomes straightforward.

Look away from the screen for ten seconds and unclench your shoulders.
