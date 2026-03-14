Here’s the hard version: the canonical architecture is mostly coherent, but the downstream notes are already smuggling back the exact confusions the canonical split was supposed to remove.

## Findings

1. **Critical — `L2` is leaking runtime ontology across the graph membrane.**
   The canonical docs say the levels share control geometry “but not ontology,” put the graph “between the lanes,” and explicitly recommend “graph annotations over runtime ontology sharing.” But `13-l2-refactor-synthesis.md` defines the “minimal graph seam” as including `active concern refs` and `reminded episode refs`. Those are live `L2` internals, not graph-readable residue.
   **Why it matters:** if `L3` scores against live concern/memory refs, you have started collapsing `L2` into the graph instead of using the graph as the membrane. (`11-settled-architecture.md`; `12-prior-work-synthesis-against-settled-architecture.md`; `13-l2-refactor-synthesis.md`)

2. **Critical — the control plane is not actually settled; one doc still has two choosers.**
   `11-settled-architecture.md` says the conductor sits **above `L3` as a biasing authority**, with **one control plane**: conductor biases, scheduler scores, one decision, stage executes. `05-stage-integration.md` still says the APC performer can “override concern priorities,” “force operator selection,” “modulate stage parameters directly,” and that “the engine and the conductor are parallel control sources” with arbitration unresolved.
   **Why it matters:** that reintroduces the old “who is actually selecting?” ambiguity. If you leave this unresolved, any `L3` evaluation can be explained away as conductor-side puppeting. (`11-settled-architecture.md`; `05-stage-integration.md`)

3. **High — the graph/interface seam has the right role, but it is still underspecified as a contract.**
   The role is correct: `11-settled-architecture.md` and `12-prior-work-synthesis-against-settled-architecture.md` are right to make the graph the composition surface. But the field list is not frozen anywhere. `12-prior-work-synthesis-against-settled-architecture.md` proposes one minimum node metadata set; `08-l3-experiment-1-synthesis.md` adds another; `02-dodm-extraction.md` wants `motivated_by[]`; `04-suspenser-extraction.md` wants `resolution_path_count`, `option_effect`, `affected_situation_id`; `13-l2-refactor-synthesis.md` adds its own projection vocabulary.
   **Why it matters:** “graph membrane” is still a slogan, not a stable schema. The hardest open problem is correctly identified, but not operationalized. (`11-settled-architecture.md`; `12-prior-work-synthesis-against-settled-architecture.md`; `08-l3-experiment-1-synthesis.md`; `02-dodm-extraction.md`; `04-suspenser-extraction.md`; `13-l2-refactor-synthesis.md`)

4. **High — the full `L3` experiment still bundles too many deltas into arm C.**
   `08-l3-experiment-1-synthesis.md` gives the right staged order: Façade substrate first, then DODM-style feature registry, then Suspenser structural tension. But `12-city-routes-experiment-1-checklist.md` makes arm C = Façade + DODM feature sum + Suspenser structural tension + optional shallow lookahead.
   **Why it matters:** if C wins, you won’t know whether the gain came from named features, structural tension, or lookahead; if C loses, you won’t know which addition failed. That is bad falsification design. (`08-l3-experiment-1-synthesis.md`; `12-city-routes-experiment-1-checklist.md`)

5. **High — the `L1` critic test is drifting past the canonical “computable defects only” rule.**
   `11-settled-architecture.md` is blunt: if a pressure cannot be detected from typed fields in world state, it is not a real schedulable pressure. But `14-l1-critic-test-synthesis.md` introduces `under-motivated node`, `weak escalation`, and `premature closure` without defining detectors, and it shifts the proposal unit from typed world-state diffs to graph edits like `split node`, `retag node`, and `reroute resolution`.
   **Why it matters:** that is how a narrow critic quietly turns into fuzzy story-doctor prompting. (`11-settled-architecture.md`; `14-l1-critic-test-synthesis.md`)

6. **High — the `L2` refactor is missing Mueller’s actual control backbone while adding new surfaces.**
   `11-mueller-ch7-extraction.md` says three things are critical: theme-rule concern initiation, per-cycle emotion/need decay, and the distinction between **personal-goal** vs **daydreaming-goal** concerns because they write back differently. `13-l2-refactor-synthesis.md` adds five modules and several state objects, but its concern state omits concern kind/write-back semantics, and its build order does not explicitly preserve theme rules or cycle decay.
   **Why it matters:** you risk a cleaner architecture diagram that is less faithful to Mueller where the control logic actually lives. (`11-mueller-ch7-extraction.md`; `13-l2-refactor-synthesis.md`)

7. **Medium — the build order is mostly right, but the conductor question is being deferred too aggressively.**
   `13-execution-roadmap.md` is right to keep `L3` first and split Graffito pilot from City Routes full experiment. But `11-settled-architecture.md` also says to prototype the APC mapping **before building the scheduler**, because instrument feel constrains which intents are actually reachable. The roadmap demotes that to a cross-cutting question that should not displace the Graffito pilot.
   **Why it matters:** that is fine for an autonomous pilot, but not fine for the full `L3` claim, because “conductor expressivity” is one of the evaluation criteria. (`11-settled-architecture.md`; `13-execution-roadmap.md`; `11-graffito-phase-1-pilot-checklist.md`; `12-city-routes-experiment-1-checklist.md`)

## Must fix now

* Rewrite the `L2 -> graph` projection so it exports only graph-readable residue, not live `L2` refs. `origin_pressure_refs[]`, `setup_refs[]`, `payoff_refs[]`, `pressure_tags`, `practice_tags`, `event_commit_potential`, `contrast_tags` fit the canonical design. `active concern refs` and `reminded episode refs` do not. (`12-prior-work-synthesis-against-settled-architecture.md`; `13-l2-refactor-synthesis.md`)
* Either deprecate `05-stage-integration.md` as stale or rewrite its performer section to match the canonical one-control-plane rule. There must be one chooser. (`11-settled-architecture.md`; `05-stage-integration.md`)
* Freeze one canonical graph schema before more `L3` implementation. Right now the contract is split across half a dozen notes. (`11-settled-architecture.md`; `12-prior-work-synthesis-against-settled-architecture.md`; `08-l3-experiment-1-synthesis.md`)
* Split arm C of the City Routes experiment into ablations: C1 = DODM features only, C2 = + structural tension, C3 = + lookahead only if needed. (`08-l3-experiment-1-synthesis.md`; `12-city-routes-experiment-1-checklist.md`)
* Prune `L1` deficiency classes back to explicitly computable detectors, or demote the vague ones to human-review lints. (`11-settled-architecture.md`; `14-l1-critic-test-synthesis.md`)
* Amend the `L2` refactor note to carry forward Mueller’s concern-kind/write-back distinction, theme-rule initiation, and cycle decay explicitly. (`11-mueller-ch7-extraction.md`; `13-l2-refactor-synthesis.md`)

## Important but later

* Bring in Mueller Appendix B’s **rule-level narration pruning**. Right now the dashboard direction is right, but the key implication that pruning hooks live on individual rules/knowledge, not coarse operator families, is not carried through strongly enough. (`12-mueller-appendix-b-extraction.md`; `13-l2-refactor-synthesis.md`)
* Decide whether Façade’s `arc_drift` / cumulative error should become an explicit runtime diagnostic instead of remaining implicit in “trajectory fit.” (`01-facade-extraction.md`; `11-settled-architecture.md`; `08-l3-experiment-1-synthesis.md`)
* Update the Graffito pilot notes to match the current fixture state. The notes still describe empty authored edges; the selected YAML no longer does. That is doc drift, not an architectural problem. (`08-l3-experiment-1-synthesis.md`; `11-graffito-phase-1-pilot-checklist.md`; `graffito_v0_scenes_3_4.yaml`)
* The stronger Mueller serendipity mechanism is still only half-imported. The extraction note calls for rule-connection search plus path verification; the refactor note mostly reframes it as observability. (`11-mueller-ch7-extraction.md`; `13-l2-refactor-synthesis.md`)

## Solid as written

* The canonical lane split is good: `L1` narrow mixed-initiative authoring support, `L2` real character-pressure engine, `L3` traversal scheduler over authored material. That part is coherent. (`11-settled-architecture.md`; `12-prior-work-synthesis-against-settled-architecture.md`)
* The graph as membrane is the right role. The problem is underspecification, not wrong placement. (`11-settled-architecture.md`; `12-prior-work-synthesis-against-settled-architecture.md`)
* Deferring `ATMS` is correct. Both the prior-work synthesis and the ATMS extraction treat it as a later representation upgrade, not the next thing to build. (`12-prior-work-synthesis-against-settled-architecture.md`; `10-atms-extraction.md`)
* The `L3` pilot/full split is right: Graffito for scheduler-shape proof, City Routes for the real narrow-`L3` test. (`13-execution-roadmap.md`; `11-graffito-phase-1-pilot-checklist.md`; `12-city-routes-experiment-1-checklist.md`)
* The source-to-level mapping is mostly faithful: EMA/OCC/Versu to `L2`; Façade/DODM/Authorial Leverage/Suspenser to `L3`; Sentient Sketchbook/MINSTREL to `L1`. (`12-prior-work-synthesis-against-settled-architecture.md`)
* Keeping the stage seam thin with `traversal_intent` is right. The leak is in the stale support doc, not the canonical seam. (`11-settled-architecture.md`; `05-stage-integration.md`)

## Bottom line

1. **Are the `L1/L2/L3` boundaries coherent?**
   Canonically, yes. In downstream notes, not fully: `L2` leaks into the graph seam, and the stage integration note reintroduces chooser ambiguity. (`11-settled-architecture.md`; `13-l2-refactor-synthesis.md`; `05-stage-integration.md`)

2. **Does the graph/interface seam have the right role, or is it underspecified?**
   Right role, underspecified contract. The membrane idea is correct; the schema is not frozen. (`11-settled-architecture.md`; `12-prior-work-synthesis-against-settled-architecture.md`)

3. **Is the `L3` experiment scoped tightly enough to falsify the right thing?**
   Almost. The Graffito/City Routes split is right, but the City Routes C arm still bundles too much. (`13-execution-roadmap.md`; `08-l3-experiment-1-synthesis.md`; `12-city-routes-experiment-1-checklist.md`)

4. **Is the `L2` refactor minimal and well-shaped, or is it still trying to do too much?**
   Directionally right, but not minimal enough yet. It adds structure while dropping some of Mueller’s essential control distinctions. (`11-mueller-ch7-extraction.md`; `13-l2-refactor-synthesis.md`)

5. **Is the `L1` critic test narrow enough to be useful without drifting into free generation?**
   Close, but not yet. The proposal grammar is disciplined; the deficiency vocabulary is not. (`11-settled-architecture.md`; `14-l1-critic-test-synthesis.md`)

6. **Did the synthesis miss anything important from the source lineages?**
   Yes: Mueller’s concern-kind/write-back split, theme rules, cycle decay, stronger serendipity mechanism, and rule-level narration pruning are not carried through strongly enough. (`11-mueller-ch7-extraction.md`; `12-mueller-appendix-b-extraction.md`; `13-l2-refactor-synthesis.md`)

7. **Is the proposed build order correct?**
   Mostly yes: `L3` first is right. But fix the seam/control contradictions now, and insert a small conductor-mapping spike before the full `L3` claim, even if you keep the autonomous Graffito pilot first. (`11-settled-architecture.md`; `13-execution-roadmap.md`)

The short version: keep the canonical architecture, but stop the leaks now. The architecture is narrower and better than the downstream notes make it look.s
