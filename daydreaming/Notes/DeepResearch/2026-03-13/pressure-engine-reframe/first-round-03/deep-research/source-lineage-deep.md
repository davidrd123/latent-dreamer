I audited the set in two passes:

1. Fidelity to the paired source slice, where that slice is actually present in the prompt.
2. Calibration against the current project anchor, specifically `11-settled-architecture.md` (two-lane system, L2 as proven core, L3 as traversal/drama management not cognition, L1 as narrow research-stage authoring) and `27-authoring-time-generation-reframe.md` (material supply is now the bottleneck, generation-first L1 prototype comes before richer L3 work, and full social-practice / assumption-management / kernel-refactor work is explicitly not phase-1).

For `03-authorial-leverage-extraction.md` and `04-suspenser-extraction.md`, the paired source text is not in the selected context, so I am not pretending to have a real source-fidelity audit there. I only judge internal plausibility and calibration.

## 01. Façade extraction

Source slice: `mateas-gdc2003/source.md`, lines 1-94.
Extraction note: `01-facade-extraction.md`, full note.

1. **Major: it underweights the paper's own warning that content authoring, not scheduler cleverness, is the real limiter.**
   The source is explicit that Façade's architecture only gets you halfway. The rest is hand-authoring behavior, and even after years of work the result is still a short, content-bounded experience with uncertain global agency. The extraction gets the beat-selection pipeline right, but it does not foreground this warning hard enough. That matters because `27-authoring-time-generation-reframe.md` now makes material supply the critical bottleneck, and Façade's source actually points in the same direction.

2. **Moderate: it over-imports project-specific scheduler doctrine beyond the source.**
   Things like "intents are an interpretation of score rather than an input," "conductor as arc-shape authority," and the full node-side contract are reasonable project moves, but they are not findings from the paper. They are extrapolations. The note usually signals that, but not always sharply enough.

3. **Low: the core mechanism extraction is strong.**
   The bag-of-beats framing, precondition/priority/score/weight pipeline, explicit target tension arc, cumulative error tracking, and "editor not generator" stance are all faithful to the source.

4. **Calibration:**
   Against `11-settled-architecture.md`, this note is basically on target: Façade is the right prior-work anchor for L3 as drama management rather than cognition. Against `27-authoring-time-generation-reframe.md`, the note is phase-stale. Its Experiment 1 framing made sense before the reframe. After the reframe, the mechanism is still relevant, but not first on the build path.

## 02. DODM extraction

Source slice: `DODM/source.md`, lines 1-54.
Extraction note: `02-dodm-extraction.md`, full note.

1. **Major: it underplays the paper's negative result about search.**
   The source is not just "declarative feature scoring is good." It is also "search does not generalize cleanly, can perform weakly, and in some cases can do worse than no drama management." The extraction keeps the declarative-feature lesson but softens the force of the paper's warning about online search. That is too gentle.

2. **Major: it imports feature vocabulary not clearly present in the paired source slice.**
   The extraction's "standard features" list goes beyond what is actually in the included article slice. Some of that may come from Weyhrauch or adjacent work, but this task is about the paired source. On strict fidelity, that is an overreach.

3. **Moderate: it flattens the action vocabulary a bit too much.**
   The source distinguishes permanent deniers, temporary deniers, reenablers, causers, hints, and endings. The extraction's four-action abstraction is defensible for project use, but it is not a literal readout of the paired source.

4. **Low: the main steal is correctly identified.**
   The note is right that DODM's real contribution over Façade is declarative evaluation via weighted features and author-level annotation, not a new ontology of cognition.

5. **Calibration:**
   Against `11-settled-architecture.md`, the note is aligned in spirit. L3 wants drama-management priors, and DODM is one of them. Against `27-authoring-time-generation-reframe.md`, the note again gives L3 too much near-term centrality. The safe, phase-correct take is "named feature vocabulary and maybe richer node annotations later," not "richer scheduler search now."

## 03. Authorial Leverage extraction

Paired source text not included in selected context.
Extraction note: `03-authorial-leverage-extraction.md`, full note.

1. **Source-incomplete:**
   I cannot honestly score source fidelity here. No claims about "got right" or "over-imported" should be treated as verified against the source.

2. **Moderate calibration issue: it is too L3-only after the reframe.**
   The note applies leverage almost entirely to the L3 scheduler question. After `27-authoring-time-generation-reframe.md`, the same leverage frame belongs at least as much on the L1 generation prototype: operator-driven generation vs flat prompt baseline, curation yield, and whether the machinery buys anything over simpler authoring-time prompting.

3. **Low: the evaluation lens itself looks right for this project.**
   `11-settled-architecture.md` is full of falsification language, and this note's "is the machinery worth building compared to simpler scripting?" frame fits that very well.

## 04. Suspenser extraction

Paired source text not included in selected context.
Extraction note: `04-suspenser-extraction.md`, full note.

1. **Source-incomplete:**
   Same limitation as 03. I cannot pretend to audit fidelity without the source slice.

2. **Low to moderate calibration issue: it still adds annotation burden in the wrong phase.**
   Even though the note explicitly demotes Suspenser to a later refinement, its proposed `resolution_path_count` and `option_effect` annotations still add authored structure at exactly the moment when `27-authoring-time-generation-reframe.md` says material supply and compile-to-graph yield matter more than richer traversal scoring.

3. **Low: phase judgment is better than most of the L3 notes.**
   It does not try to make Suspenser a first build. That is good. Relative to the rest of the L3 set, this one is already partly self-disciplined.

## 05. EMA extraction

Source slice: `EMA/source.md`, lines 1-150.
Extraction note: `05-ema-extraction.md`, full note.

1. **Major: it over-extends EMA into a direct operator-dispatch story that the source does not give you.**
   EMA gives appraisal variables, a causal interpretation, and coping strategy preferences. It does not tell you that controllability maps to this exact Mueller family, or that changeability should dispatch to that exact operator set. That is project synthesis, not source extraction.

2. **Major calibration issue: it aims EMA mainly at an L2 runtime refactor, but the reframe makes it immediately useful somewhere else.**
   `11-settled-architecture.md` already explicitly wants an EMA-style appraisal pass. But `27-authoring-time-generation-reframe.md` makes the immediate need an explicit `AppraisalFrame` inside the authoring-time generation prototype. The extraction does not foreground that enough. It talks like EMA is mostly for later kernel cleanup, when in fact the reframe makes a narrowed appraisal layer phase-1 relevant.

3. **Moderate: it slightly underweights EMA's strongest theoretical claim.**
   The source's main move is not just "appraisal variables exist." It is "appraisal is a single fast automatic process, and dynamics come from changes in the interpreted world model." The note mentions this, but its recommendations lean more toward coping taxonomy and operator biasing.

4. **Low: the base read is good.**
   The appraisal loop, causal interpretation, variable set, coping strategies, and reappraisal logic are all well captured.

5. **Calibration:**
   Architecturally good, phase targeting not sharp enough. The right current steal is a narrow appraisal pass / `AppraisalFrame`, not a broad runtime coping redesign.

## 06. OCC extraction

Source slice: `OCC/source.md`, lines 1-34.
Extraction note: `06-occ-extraction.md`, full note.

1. **Moderate: the concern-type mapping is an import, not a source finding.**
   The source gives a cleaned-up emotion taxonomy and inheritance logic. It does not validate the mapping from nine project-specific concern types to those emotion categories. The note partly signals this, but the mapping still reads more definite than the source warrants.

2. **Low: the taxonomy extraction is mostly faithful.**
   The note correctly captures prospective vs actual distinctions, the compound emotions, the fortunes-of-others emotions, and the interest/disgust clarification from the revised hierarchy.

3. **Low: it avoids the obvious trap of trying to runtime-implement all 22 types immediately.**
   Its "don't take the full taxonomy as runtime state" guidance is sane.

4. **Calibration:**
   Good enough. `11-settled-architecture.md` does not explicitly elevate OCC the way it elevates EMA, Versu, and ATMS. `27-authoring-time-generation-reframe.md` only needs explicit appraisal structure, not a giant live taxonomy. The extraction's restraint is appropriate. I would only tighten one thing: OCC should be treated mainly as an appraisal-output vocabulary, not a proposed new kernel state model.

## 07. Versu extraction

Source slice: `Versu/source.md`, lines 1-104.
Extraction note: `07-versu-extraction.md`, full note.

1. **Major calibration issue: it is architecturally right and phase-wrong.**
   `11-settled-architecture.md` explicitly says the project wants social-practice objects from Versu. So the extraction is not wrong to emphasize them. But `27-authoring-time-generation-reframe.md` explicitly says full `SocialPracticeInstance` is not required in phase 1 and that only a lightweight `PracticeContext` is required. The note still reads like a case for building social practices as a typed L2 runtime subsystem now.

2. **Moderate: it underweights the source's interpractice communication model.**
   One of Versu's most important ideas is that beliefs, emotions, evaluations, and relationship states are the medium through which practices affect one another. The extraction focuses on practices and affordances more than on that shared core model.

3. **Moderate: it turns Versu into "operators activate from concern × situation" a little too quickly.**
   That is a sensible project adaptation, but it is not what the source directly says. Versu is about affordance menus, utility-scored action choice, and stateful social situations. The operator-family mapping is your project's translation layer.

4. **Low: the main practice model is captured well.**
   Role-agnostic practices, concurrent practice composition, practices suggesting rather than forcing action, and lifecycle/state progression are all faithful.

5. **Calibration:**
   Against `11-settled-architecture.md`, strong fit. Against `27-authoring-time-generation-reframe.md`, it should have been demoted to "lightweight PracticeContext for the prototype now, full social-practice machinery later if warranted."

## 08. Sentient Sketchbook extraction

Source slice: `SentientSketchbook/source.md`, lines 1-40 and following pages through user study/discussion.
Extraction note: `08-sentient-sketchbook-extraction.md`, full note.

1. **Major calibration issue: the note still makes dashboard-first / critic-first feel primary.**
   That fit the older L1 story in `11-settled-architecture.md`. It does not fit the new priority in `27-authoring-time-generation-reframe.md`, which explicitly says generation-first, critic/repair second. The note's "build a deficiency dashboard first" is phase-stale.

2. **Moderate: it underweights the actual empirical lesson from the user study.**
   The source does not just say "novelty and objective suggestions both matter." It gives a sharper picture: novelty search helped when users wanted inspiration or large changes, while objective-optimized suggestions were more useful in late-stage refinement. The extraction partly reflects this, but it turns the paper a bit too much into a general thesis about novelty ranking and visible metrics.

3. **Moderate: some doctrine is imported from the current architecture rather than read from the source.**
   "Novelty without structural justification is not schedulable pressure" is a good project doctrine. It is not the paper's doctrine.

4. **Low: the workflow extraction itself is good.**
   Continuous evaluation, feasibility before quality, starting from the current artifact, cheap rejection, and mixed objective/novelty proposals are all well captured.

5. **Calibration:**
   Good architectural fit for L1 mixed-initiative work in general. Wrong immediate emphasis after the reframe. The live part now is "generation from primitives with human curation," not "deficiency dashboard before proposal engine."

## 09. MINSTREL extraction

Source slice: `MINSTREL/source.md`, lines 1-60.
Extraction note: `09-minstrel-extraction.md`, full note.

1. **Moderate: it narrows TRAMs too much to post-rejection mutation.**
   In the source, transform-recall-adapt is the general creativity mechanism that starts when ordinary case-based problem solving fails. That is broader than "a human rejected a proposal, now mutate it." The note's framing is useful, but narrower than the source.

2. **Moderate miss: it underweights the importance of trying ordinary retrieval first.**
   In MINSTREL, standard problem solving is itself a TRAM and is always tried before creativity heuristics. That matters for the current project because `27-authoring-time-generation-reframe.md` wants a flat-prompt baseline and simple tag-based retrieval before adding more machinery.

3. **Low: the core creativity mechanism is accurately extracted.**
   Failure-driven creativity, paired transform/recover logic, imaginative memory, and boredom/novelty gating are all well represented.

4. **Calibration:**
   This one is actually pretty good for the current phase. `27-authoring-time-generation-reframe.md` is generation-first, and MINSTREL is an authoring-time generation precedent. The main missing calibration is that the note does not explicitly ask whether transformed outputs compile cleanly into the frozen graph seam, which the reframe makes a hard pass/fail condition.

## 10. ATMS extraction

Source slice: `ATMS/source.md`, lines 1-164.
Extraction note: `10-atms-extraction.md`, full note.

1. **Moderate: it underweights the source's solver-interface point.**
   The source is not only about cheap context switching and nogoods. It is also about moving the boundary between the problem solver and the maintenance system so the solver stops doing the wrong kind of control work. The extraction mostly treats ATMS as a data-structure upgrade.

2. **Low: the representation story is faithful.**
   Assumptions, environments, labels, nogoods, simultaneous contexts, and the reason ATMS beats single-state TMSs are all captured correctly.

3. **Low: it does not oversell full ATMS as immediate.**
   That is important, and the note gets it right.

4. **Calibration:**
   Strong. `11-settled-architecture.md` explicitly names ATMS-style assumption management as a desired L2 addition. `27-authoring-time-generation-reframe.md` explicitly says assumption management is not required in phase 1. The note mostly respects both facts.

## 11. Mueller Chapter 7 extraction

Source slice: `07-implementation-of-daydreamer.md`, lines 1-35, 213-248, 437-1041.
Extraction note: `11-mueller-ch7-extraction.md`, full note.

1. **Major fidelity issue: it is not a pure Ch. 7 extraction.**
   The note imports evaluation metrics from Ch. 4 and emotional feedback material from Ch. 3. Those additions are probably useful, but they are not supported by the paired source slice itself. On strict audit terms, that is the biggest fidelity problem in the set.

2. **Moderate: it slightly truncates daydreaming-goal side effects.**
   The source does distinguish personal-goal and daydreaming-goal concerns cleanly, and the note captures the big distinction. But the source also says daydreaming-goal concerns have other side effects including emotional-state modification and episodic-memory storage. The extraction leans too heavily on "goals + outcomes" as the write-back summary.

3. **Moderate calibration issue: it still reads like a broad kernel-refactor agenda.**
   `11-settled-architecture.md` makes this source central because L2 is the proven engine core. But `27-authoring-time-generation-reframe.md` explicitly says full kernel refactor is not required for the next prototype. The extraction's "what to take" list is right in direction but too large in immediate scope.

4. **Low: the procedural extraction is otherwise excellent.**
   The control loop, theme-rule initiation, context/backtracking structure, recursive reminding, serendipity intersection search, and mode distinctions are all captured with unusually high precision.

5. **Calibration:**
   Strong architectural fit, over-eager phase fit. The current move is to pick the narrow Mueller pieces needed for the authoring-time generation prototype, not to turn the note into a mandate for full kernel renovation.

## 12. Mueller Appendix B extraction

Source slice: `14-appendix-b-english-generation-for-daydreaming.md`, lines 1-92, 346-415, 486-686.
Extraction note: `12-mueller-appendix-b-extraction.md`, full note.

1. **Low to moderate: it slightly underweights the typed-template mechanics.**
   Appendix B is not only about inner-monologue flavor. It is also about a typed realization system with recursive templates, subject-generation rules, and inherited generation behavior. The note leans more on mode-sensitive voice than on that underlying machinery.

2. **Low: the mode-sensitive narration and pruning extraction is very good.**
   Belief-path perspective, hypothetical vs actual phrasing, low-strength qualifiers, rationalization/reminding language, paragraph breaks on structural boundaries, and rule-level pruning are all faithfully captured.

3. **Low: project mapping is disciplined here.**
   The note does not try to turn Appendix B into a giant NLG agenda. It mostly uses it to clarify what a narration companion should and should not do.

4. **Calibration:**
   Good. `11-settled-architecture.md` explicitly says the inner-life dashboard and narration companion are a primary early output, not a debug tool. That makes this note unusually well calibrated. `27-authoring-time-generation-reframe.md` does not make narration the bottleneck, so the correct use is thin realization support, not a rewrite of the generator. The note mostly respects that.

## Three cross-cutting mistakes or omissions across the set

1. **The set repeatedly underweights the sources' own warnings about content and authoring burden.**
   This is most obvious in the L3 notes. Façade and DODM are both useful, but both also warn that control machinery is only as good as the authored material it has to work with. After `27-authoring-time-generation-reframe.md`, this should have been the dominant calibration fact. It isn't.

2. **Too many notes slide from source extraction into project prescription without marking the boundary sharply enough.**
   This shows up as conductor mappings in Façade/DODM, operator-family dispatch in EMA/Versu, concern mapping in OCC, and broader architecture contracts in several notes. Some of that synthesis is good. But the notes too often present it with extraction-like confidence.

3. **The set does not apply the current phase gate hard enough.**
   `11-settled-architecture.md` says many imports are desirable. `27-authoring-time-generation-reframe.md` then says very clearly which of those are *not* required in phase 1: full social-practice machinery, assumption management, full kernel refactor, conductor integration, broad scheduler enrichment. Too many notes remain architecture-correct but build-order-wrong. The recurring missing question should have been: "Does this help the current generation-first prototype, and does it compile cleanly to the frozen graph seam?"
