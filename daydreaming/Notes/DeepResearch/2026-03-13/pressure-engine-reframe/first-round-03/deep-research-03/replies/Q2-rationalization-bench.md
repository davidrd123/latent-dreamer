Findings first

1. **Tessa is the right rationalization case.**
   The fixture is aftermath, not threshold. The damage already happened, controllability is low, blame pressure is high, and the live move is reinterpretation under pressure, not entry preparation. That is rationalization terrain, not rehearsal terrain.

2. **The upstream derived path is actually rationalization, not secretly rehearsal.**
   From the code you pasted:

   * `build_causal_slice()` puts Tessa’s `status_damage` case in `temporal_status = actual` when `event_over` is true.
   * `derive_appraisal_frame()` then drives controllability down and praiseworthiness strongly negative.
   * `derive_practice_context()` resolves to `confession`.
   * `retrieve_episodes()` pulls confession-adjacent episodes, especially the half-apology / explanation material.
   * `score_operators()` gives `confession` a strong rationalization practice fit and adds an “actual damage already done” bonus to rationalization appraisal fit.
   * `test_tessa_derived_path_prefers_rationalization()` is checking the right thing and passes.

   So no, the current control path is not still structurally biased toward prospective rehearsal. Upstream, it’s doing the right operator selection.

3. **But the benchmark is not clean end-to-end.**
   I’d call it a real rationalization benchmark at the operator-selection layer$*{85%}$, but only a clean end-to-end rationalization benchmark$*{45%}$.

   The dirt is in four places:

   **A. Runtime derivation leaks benchmark expectations.**
   `build_causal_slice()` for the Tessa aftermath path reads `fixture["benchmark_step"]["expected_practice_context"]` to get affordance tags. `resolve_practice_context_from_fixture()` also reads `expected_practice_context` / `alternate_practice_context`. That is contamination. “expected” fields are supposed to validate the run, not help construct it.

   **B. The prompt leaks operator behavior and blurs rationalization with avoidance.**
   `build_operator_behavior_description("rationalization")` says rationalization makes “delay, retreat, or self-protection feel justified.” That’s a bad description. “Delay” and “retreat” drag in avoidance semantics. Rationalization should mean: *reinterpret an already-occurred failure so it feels less blameworthy or less damaging*. Not “retreat, but with an excuse.”

   **C. The semantic checker is too tokenish and is missing obvious rationalization.**
   The Tessa checker keys off phrases like `had to`, `legible`, `fairness`, `as if`, `almost excuses`. In the selected Tessa run, rationalization wins all three steps, but the summary shows `rationalization reframes rather than resolves the damage = False` every time. That means the evaluator is worse than the prose. The prose is already doing rationalization-as-action:

   * typing a justification,
   * deleting `sorry`,
   * replacing fault with motive,
   * treating “project momentum” or “clean narrative” as cover.

   **D. The graph/commit semantics are wrong for rationalization.**
   This is the biggest bug. Step 2 returns `option_effect: close`, and `choose_commit_type()` maps that to `ontic`. But nothing ontic happened. Tessa did not repair anything, confess anything, or change the world. She just edited the interpretation of what she did. That should usually be `clarify → salience`, maybe `none` in some cases, but not `close → ontic`. Right now the sequence trace is pretending rationalization created closure. It didn’t.

4. **Behaviorally, Tessa is already distinct from Kai and Rhea.**
   This part is good.

   * **Rationalization (Tessa):** stays on the wound and edits its meaning. Drafts the message, deletes apology language, swaps fault for motive, turns “I hurt him” into “I had to keep the story clean.”
   * **Avoidance (Kai):** displaces engagement into another activity. Kettle, scrubbing, turning away, not opening the letter.
   * **Rehearsal (Rhea):** prepares for future contact. Mouths openings, tries phrasing, braces for the first sentence.

   Tessa’s selected outputs are much closer to the first than the other two. So the prose lane is not hopelessly confused. The confusion is mostly in prompt wording, evaluator design, and commit mapping.

5. **The fixture itself does not need replacement.**
   Tessa is the right case:

   * actual, not prospective
   * damage already done
   * low control over the past
   * high self-blame pressure
   * confession/aftermath social frame
   * reframing as the live move

   That part is correct.

Smallest viable patch plan

1. **Stop reading `expected_*` during runtime.**
   This is patch zero. Without it, the benchmark is contaminated.

   Smallest change:

   * Move runtime practice templates out of `benchmark_step.expected_practice_context`.
   * Add a runtime-only authored section, e.g. `practice_templates` or `practice_contexts`.
   * Make `build_causal_slice()` and `resolve_practice_context_from_fixture()` read those runtime fields.
   * Keep `expected_*` only for post-run checks.

   You do **not** need a new abstraction layer. Just separate “runtime authoring inputs” from “expected validation outputs.”

2. **Fix the rationalization operator description.**
   Rewrite `build_operator_behavior_description("rationalization")`.

   Current meaning is muddy. Use something like:

   > Rationalization means reinterpreting an already-occurred failure so it feels less blameworthy, less damaging, or more justified. The character stays in contact with the failure, but edits its meaning.

   Remove:

   * “delay”
   * “retreat”
   * anything that makes it sound like avoidance

   Also remove anything too scene-prescriptive. The checklist is right: explain what the operator *is*, not what the scene *must visibly contain*.

3. **Make the Tessa checker structural, not lexical.**
   Keep it cheap. No LLM judge needed yet.

   Replace the current token trap with checks like:

   * the damaging event is still unresolved
   * a self-exonerating or blame-softening explanation is being composed / revised / rehearsed
   * the explanation competes with apology or acknowledgment
   * no actual resolution occurs: no send, no completed apology, no repaired relationship, no world mutation

   You can still keep lexical cues, but they should be broad families:

   * explanation/minimization language
   * deletion/backspacing of apology
   * “clean narrative” / “project momentum” / “for the room” / “had to simplify” / “protect”
     not one exemplar phrase.

4. **Make commit derivation operator-aware.**
   Smallest patch:

   * After generation, before `choose_commit_type()`, validate whether the proposed `option_effect` is compatible with `operator_family`.
   * For `rationalization`, if there is no outward irreversible act, disallow `option_effect = close`.
   * Normalize it to `clarify` or reject/regenerate once.

   This alone fixes the worst misread in step 2.

5. **Tighten affordance conditioning.**
   For rationalization, condition on the selected affordances only, not the whole visible practice set.
   In Tessa that means something like:

   * `justify-simplification`
   * `minimize-harm-framing`
     and only include `draft-half-apology` when it is actually selected.

   Right now the prompt gives the model a bit too much room to drift.

6. **Tiny scoring cleanup, not a rewrite.**
   `score_operators()` mostly works. Leave the main formula alone.
   Only patch:

   * add an explicit `confession` episodic-resonance branch instead of letting it fall through the generic `else`
   * optionally add a small guardrail so `actual + confession + high blame` cannot lose to avoidance unless practice context is explicitly swapped

   Do not touch weights unless one of the ablations proves you need to.

7. **Add three narrow tests.**

   * A Tessa-style rationalization output with “delete sorry / justify motive” passes the new structural checker.
   * A Kai-style avoidance output fails the rationalization checker.
   * A rationalization scene with no outward irreversible act cannot survive as `option_effect = close` / `commit_type = ontic`.

What a good ablation looks like

Best ablation:

**Hold the world fixed. Flip only the temporal/practice structure.**

Same Tessa/Eli credit-omission domain, same target, same social pressure, but:

* change `temporal_status` from `actual` to `prospective`
* change `practice_type` from `confession` to `anticipated-confrontation`

Expected result:

* rationalization should drop
* rehearsal should rise
* the prose should become first-sentence prep, not self-exonerating reinterpretation

If rationalization still wins there, then yes, the benchmark is fake.

Second ablation, also useful:

* keep `actual`
* swap `confession` to `evasion`

Expected result:

* avoidance should rise
* prose should become put-it-away / leave / answer-later behavior, not explanation-editing

What not to change yet

1. **Do not replace the fixture.**
   Tessa is the right benchmark case.

2. **Do not rewrite the scoring model wholesale.**
   Operator selection is already mostly right. The dirt is downstream.

3. **Do not add embeddings, a judge model, or a solver.**
   This is not a retrieval sophistication problem.

4. **Do not expand the operator taxonomy.**
   Rationalization, rehearsal, avoidance are enough for this fix.

5. **Do not refactor the graph seam.**
   An operator-aware validator on `option_effect` is enough. You do not need new seam fields.

6. **Do not broaden this into an L2 redesign.**
   The current appraisal cut is good enough for this benchmark. The real fixes are:

   * expectation leakage
   * operator description
   * structural semantic checks
   * commit validation

Bottom line

**Tessa is a genuine rationalization case, and the prototype is already selecting rationalization for the right upstream reasons.**
What is still broken is the benchmark hygiene: runtime expectation leakage, avoidance-ish prompt wording, token-based evaluation, and a commit mapper that mistakes interpretive reframing for ontic closure.

Fix those four things and you’ll have a real rationalization benchmark, not just a rationalization-flavored win.
