According to documents from March 15, 2026, here’s the blunt read.

I reviewed the actual Kai bundle `20260315-012749-315952` and Rhea bundle `20260315-012749-316071` at the prompt/response/sidecar/trace level, not just the memos. I also checked `pending-items-2026-03-15.md`, `30-authoring-time-generation-prototype-spec.md`, `31-generation-experiment-review-checklist.md`, `32-authoring-time-generation-comparison-memo.md`, and `34-broader-application-surface.md`.

## Findings first

1. **The prose is good enough to justify scaling to batch generation with human curation. It is not good enough for blind admission.**
   My estimate: **3–4 of the 6 selected nodes are keepable after light edit**$*{75%}$, but **only about 3 of 6 should survive as distinct graph material**$*{80%}$ because some are near-duplicates in dramatic function.
   My curation call on the actual six:

   * **Keep:** Kai step 1, Kai step 3, Rhea step 1
   * **Keep as alternate, not alongside the stronger twin:** Rhea step 3
   * **Cut / down-rank:** Kai step 2, Rhea step 2
     This matches the artifact summaries: all three Kai steps are graph-valid and pass the smoke checks; all three Rhea steps are graph-valid, but step 2 is the only one that fails the “rehearsal sharpens rather than resolves the choice” check. (Sources: Kai `315952/summary.md`; Rhea `316071/summary.md`.)

2. **The best thing here is not generic fluency. It is concrete, behavioral pressure.**
   Kai is the stronger writerly case. The beats have real physicalization: “squeak of the sponge,” “spice rack,” “polishing the kitchen faucet,” the harbor buoy leaking in through the window. That is not therapy narration. It is avoidance embodied as household ritual.
   Rhea is solid too: “heavy soundproof door,” “locked phone screen,” “chin up,” “cold wall,” “door handle.” Those are good threshold cues. But Rhea is narrower and more speech-centered. Kai feels like a body under pressure; Rhea sometimes feels like a well-executed rehearsal template. (Sources: Kai `315952/middle-step-01.response.txt`, `...02.response.txt`, `...03.response.txt`; Rhea `316071/middle-step-01.response.txt`, `...02.response.txt`, `...03.response.txt`.)

3. **The main quality break is repetition, not bad sentences.**
   Kai gives you three versions of the same beat: domestic delay ritual while refusing the letter. The object changes, the move does not. Rhea gives you three versions of the same beat: outside-the-door opening-line rehearsal. Again, the wording changes more than the dramatic function.
   This is the problem now. Not “can it write?” Yes, it can write. The problem is **material supply diversity**. The current per-step compiler is finding the strongest local version of a narrow operator template and then doing it again. (Sources: Kai `315952/sequence.trace.json`; Rhea `316071/sequence.trace.json`; `authoring_time_generation_prototype.py`, especially `score_candidate_for_compilation()` and `compile_candidate_set()`.)

4. **The operator does show up in behavior.**
   Kai’s nodes read as avoidance even if you hide the label and sidecar. He keeps finding things to do with his hands so he does not have to face the envelope.
   Rhea’s nodes read as rehearsal even if you hide the label. She keeps testing openings before crossing the threshold.
   So the answer to “is the operator only labeled correctly while the prose stays generic?” is **no**. The behavior is doing real work.
   The caveat: this is not a perfectly pure test, because `build_middle_prompt()` still gives a short operator-semantics paragraph. That is better than outright behavioral leading, and it passes the spirit of the checklist more than the old prompt did, but it still narrows the space. So the operator signal is real, but not fully disentangled from prompt scaffolding. (Sources: `31-generation-experiment-review-checklist.md`; `authoring_time_generation_prototype.py` `build_middle_prompt()`; Kai/Rhea response files.)

5. **These beats mostly pass the “action under pressure” test.**
   The criterion from `34-broader-application-surface.md` is the right one: does this read like action under pressure, or like emotional indicating?
   The current Kai outputs pass. They are doing-action scenes, not “Kai felt anxious” scenes.
   Rhea mostly passes too, but step 2 is the weakest because it drifts toward a more resolved, rhetorically flattened version of the rehearsal. The summary’s failed check is not noise. Human read agrees with it. (Sources: `34-broader-application-surface.md`; Rhea `316071/summary.md`; Rhea `316071/middle-step-02.response.txt`.)

6. **Multi-step accumulation is visible, but weaker than it looks at first glance.**
   There is real carry-forward of **memory**. Later prompts and traces explicitly include earlier accepted generated beats, and the retrieval trace shows those generated episodes re-entering retrieval on later steps. So the system is not just pretending to remember.
   But the stronger claim, “the sequence is really moving through new state,” is not yet true enough. The newer caveat file says this directly: multi-step state carry-forward is “not real yet”; later steps still rebuild from the first fixture situation rather than carrying the active situation forward.
   That is exactly how the prose reads:

   * **Kai** has some real escalation. Step 3 is stronger than step 1, and the sequence trace marks step 3 as a new segment because `option_effect` crosses into `close`.
   * **Rhea** does not really progress. All three steps stay in the same segment. Step 3 feels like a better re-roll of step 1, not the next state of the world.
     So the right answer is: **yes, step 3 is shaped by step 1, but mostly as iterative fixation and rewrite, not as genuine scene-state progression**. (Sources: Kai `315952/sequence.trace.json`; Rhea `316071/sequence.trace.json`; `pending-items-2026-03-15.md`.)

7. **Graph annotation is structurally valid but semantically thin.**
   This is the quiet weakness. The selected nodes validate under the seam. Fine. But “graph-valid” is not the same thing as “graph-useful.”
   In both Kai and Rhea, the selected graph projections usually collapse to the **dominant concern only**:

   * Kai prose clearly carries attachment pressure **and** obligation/deadline pressure, but the selected graph nodes often tag only `attachment_threat`.
   * Rhea prose clearly carries status damage **and** obligation/threshold pressure, but the selected graph nodes often tag only `status_damage`.
     The sidecars still remember both source concerns, so the richness exists upstream. The graph seam is where it gets thinned out.
     Same story with refs: `setup_refs` / `payoff_refs` are mechanically valid, but some feel opportunistic rather than causally earned. That means the graph is currently less informative than the prose+sidecar combination. That will matter later. (Sources: Kai `315952/middle-step-01.sidecar.json`, `...02.sidecar.json`, `...03.sidecar.json`, response files; Rhea `316071/middle-step-01.sidecar.json`, `...02.sidecar.json`, `...03.sidecar.json`, response files; fixture expectations in `authoring_time_generation_kai_letter_v1.yaml` and `authoring_time_generation_rhea_credit_meeting_v1.yaml`.)

8. **The current greedy compiler is enough for the next tiny step only if a human curator is downstream. It is not enough as the real admission policy.**
   The code’s scoring is local: specificity, semantic legibility, novelty against already accepted text, and ref coverage. There is **no batch-level notion** of:

   * “we already have two kitchen-avoidance beats”
   * “we are over-selecting one operator”
   * “this node adds no new pressure coverage”
   * “this set is low in overdetermination”
     The actual Kai and Rhea sequences prove the limitation. The compiler is good at picking the best local version of a beat. It is bad at knowing when the next best local beat is still the same beat.
     So: **good enough for small batch generation + human curation packet now**. **Not good enough for automatic graph admission or strong claims about material supply.** (Sources: `authoring_time_generation_prototype.py`; `r2-near-term-mech-design.md`; `r3-3cand-imports-unpacking.md`; Kai/Rhea sequence traces.)

## Fast curation rubric

Use five scores, 1–5 each. This is enough.

| Dimension                    | What to ask                                                                                                                    | Keep threshold |
| ---------------------------- | ------------------------------------------------------------------------------------------------------------------------------ | -------------- |
| **Behavioral legibility**    | Could I infer avoidance/rehearsal from the action alone, without labels?                                                       | 4+             |
| **Pressure specificity**     | Does the beat feel tied to *this* situation and history, not generic anxiety?                                                  | 4+             |
| **Delta from kept material** | Does it add a new move, object, causal turn, or pressure mix?                                                                  | 4+             |
| **Graph usefulness**         | Are `pressure_tags`, `origin_pressure_refs`, `setup_refs`, `payoff_refs`, `option_effect` actually useful for later traversal? | 3+             |
| **Rewrite cost**             | Could I keep it with light edit, or would I have to rebuild it?                                                                | 4+             |

Two hard reject rules:

1. **Same operator + same situation + same physical action class + no new causal turn = reject.**
2. **If the prose implies mixed pressure but the graph seam flattens it into one thin tag set, down-rank hard unless the scene is exceptional.**

Applied to the current six:

* **Kai 1:** strong keep
* **Kai 2:** reject as weaker duplicate of the same avoidance schema
* **Kai 3:** keep
* **Rhea 1:** keep
* **Rhea 2:** reject
* **Rhea 3:** keep only as an alternative to Rhea 1, not as an additional admitted node

## Most dangerous scaling risks

1. **Operator-template collapse.**
   Avoidance will become “household task while refusing object.” Rehearsal will become “mouth opening line outside threshold.” At batch scale you’ll get a pile of locally good paraphrases.

2. **False confidence from sequence traces.**
   Because memory writeback is partly real, the traces look more accumulated than the actual scene-state progression really is. If you don’t keep this distinction clear, you will overestimate how much trajectory you have.

3. **Pressure flattening at the graph seam.**
   If mixed pressure in the prose keeps collapsing to the dominant concern in `pressure_tags[]` / `origin_pressure_refs[]`, the later traversal layer will inherit a thinner graph than the generation layer actually produced.

4. **Generated-episode self-priming.**
   Once generated beats re-enter retrieval, they can crowd out authored backstory and produce an echo chamber of the system’s own favorite move. The current Rhea step 3 trace is already leaning in that direction.

5. **Human curation burden hidden by graph-validity.**
   “Valid JSON with resolvable refs” is cheap. “Worth admitting to a dream graph” is expensive. At batch scale, most labor will be duplicate suppression and deciding which variant actually changes the graph.

6. **Greedy local scoring will quietly poison the graph.**
   It will not admit junk. It will admit too many versions of the same good thing. That is a worse failure, because it looks like success for a while.

## What not to spend time on yet

* **Do not spend more time tuning the token/semantic checker.**
  `32-authoring-time-generation-comparison-memo.md` is right. It is now a smoke test, not the main evaluation instrument.

* **Do not spend more time polishing single-fixture prompts to get a prettier third Kai beat.**
  You already know the system can write a good avoidance beat and a good rehearsal beat. The bottleneck is diversity and admission.

* **Do not jump to embeddings, aesthetic filters, or a solver yet.**
  The current next question is still small-batch quality and curation packet design, not global optimization theater.

* **Do not reopen L3 or broader runtime architecture because these outputs are promising.**
  The problem exposed by these artifacts is upstream material supply, exactly as the reframe document said.

* **Do not treat “graph-valid” as “graph-ready.”**
  That would be the most expensive self-own in this whole phase.

Bottom line: **the generation lane has earned the right to scale**, but the evidence from Kai and Rhea says the next bottleneck is not prose fluency. It is **batch diversity, seam richness, and admission discipline**. The good news is that this is the right problem to have.

Look away for a moment. Relax your shoulders.
