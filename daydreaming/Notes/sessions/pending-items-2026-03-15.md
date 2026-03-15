# Pending Items — 2026-03-15

What was read or discussed but not yet acted on. Organized by
when it matters, not by when it was discovered.

---

## Immediate (before next build pass)

1. **End-to-end Gemini run with all four mechanisms active.**
   Concern inference + multi-step accumulation + candidate
   compilation (K=4) + boundary detection. Kai and Rhea.
   Codex is on this.

2. **practice_tags list comparison uses ordering-sensitive match.**
   Line ~1415 in harness. Should use set() to avoid false
   positives. Quick fix.

3. **Memory files need updating.** `project_pressure_engine_reframe.md`
   predates the authoring-time generation reframe and prototype
   results. Should reflect current state.

---

## Before scaling the generation pipeline

4. **Weighted abduction for interpretation layer.** The strongest
   missing mechanism for material supply. Sparse primitives →
   competing latent interpretations → top-k hypotheses feeding
   CausalSlice. 5 Pro designed `AbductiveHypothesis` object.
   See: `r3-3cand-imports-unpacking.md`, `r2-near-term-mech-design.md`

5. **Soft-constraint compiler for graph compilation.** "Which
   candidate moments survive together?" Hard constraints + soft
   objectives over candidate batches. Greedy reranker first,
   solver later if needed.
   See: `r3-3cand-imports-unpacking.md`

6. **POCL-lite causal-link sketcher.** Makes `setup_refs[]` /
   `payoff_refs[]` mechanically grounded rather than prompt-inferred.
   Requires, establishes, threatens per candidate.
   See: `r3-3cand-imports-unpacking.md`

---

## Before L2 kernel refactor

7. **L2 build order correction.** Source-miss scan says front-load
   Mueller harder than current synthesis: theme rules + recursive
   reminding + verified serendipity + surprise BEFORE practice tags
   and role gating. Capture in `13-l2-refactor-synthesis.md`.
   See: `source-lineage-deep.md` §build-order-implications

8. **Overdetermination as central scoring criterion.** Partially
   addressed by the overdetermination feature in arm C. But the
   source-miss scan says it should be even more central — the
   default quality signal, not a bonus term.
   See: `06-source-miss-scan/response.md`

---

## Before conductor build

9. **NIME mapping research.** Adding control dimensions can reduce
   affordance exploration. Consult before building the full APC
   mapping. 5 Pro conductor response already incorporated this.
   See: `r4-conductor-mapping.md`, `03-missing-lineage-scan/response.md`

10. **Wizard-of-Oz conductor test.** Designed in r4. Run before
    building full live mapping. Measures reachability, repeatability,
    causal feel, observer legibility.
    See: `r4-conductor-mapping.md` §3

11. **Tidal oscillators.** Hybrid but strongly autonomous. "The tides
    are weather. The performer plays against the weather." No
    per-tide knobs in v1.
    See: `r4-conductor-mapping.md` §4

---

## Before dashboard / narration work

12. **Two surfaces, not one.** Performance dashboard (85% current
    state, 15% residue) vs. authoring membrane (provenance + curation
    actions: freeze/dismiss/respond/cut). Don't merge them.
    See: `r5-dashboard.md`

13. **Narration companion rules.** Packet-first. Provenance through
    four derived hints only (emotional shading, threatened goal,
    memory resonance, operator tone). Raw sidecars never in the
    main voice. "Narrate structural turns, hide maintenance math."
    See: `r5-dashboard.md` §3-4

14. **Rule-level narration pruning.** Mueller's Appendix B has
    pruning hooks on individual rules, not coarse operator families.
    Carry this into the dashboard implementation.
    See: `12-mueller-appendix-b-extraction.md`

---

## Before City Routes becomes production content

15. **Event Segmentation Theory.** Consult before locking node
    granularity. "What makes one node a node?" Missing lineage scan
    flagged this.
    See: `03-missing-lineage-scan/response.md` §4

16. **Storylets/QBN as L3 comparison class.** The current graph
    nodes with preconditions and state-gated availability are close
    to storylet architecture. Worth asking: "is arm B basically a
    storylet engine with continuous scoring?" Not urgent but should
    inform City Routes interpretation.
    See: `03-missing-lineage-scan/response.md` §2

---

## Doc hygiene (do anytime)

17. ~~**Fix `05-stage-integration.md`.**~~ DONE. Status note
    added at top, performer section rewritten to match
    one-control-plane rule.

18. **Packet ownership audit.** One writer per runtime field.
    ~30 min exercise. Build a field-ownership table.
    See: `02-failure-mode-review/response.md` §3

19. **Stale Graffito wording.** Execution roadmap and L3 experiment
    synthesis still say "add edges and deltas" but those are done.

20. **Formal arm C ablation.** Arm C was built incrementally but no
    formal C1 (features only) vs C2 (+ structural tension) ablation
    exists.

---

## Bigger vision (don't lose)

21. **Broader application surface (doc 34).** Mueller-shaped inner
    life as general cognitive infrastructure. Writing companion,
    reading companion, research daemon, vault agent with
    preoccupations. The mechanisms generalize beyond conducted
    performance.

22. **Three-layer compiler stack from 5 Pro.** Abduction → causal
    scaffold → soft-constraint admission. This is the future
    material-supply architecture after the current four mechanisms
    are validated.

23. **Five non-Mueller imports (from broader discussion).** Attachment
    theory (styles as operator bias), predictive processing (concerns
    as prediction errors), event segmentation (node boundaries),
    conceptual blending (serendipity as blend-finding), Internal
    Family Systems (concern competition as parts model).

24. **John collaboration.** Questions-for-john.md exists.
    High-value evaluator for watched run and conductor mapping.
    Membrane design directly relevant to authoring curation UX.

---

## File pointers

Key docs that might not be in the session log:

- `first-round-02/` — all six 5 Pro round-2 responses
- `first-round-03/deep-research/` — source synthesis, Mueller
  synthesis, project synthesis, source lineage audit
- `first-round-03/deep-research-02/` — r2 mechanism design,
  r3 imports, r4 conductor, r5 dashboard
- `first-round-03/deep-research-further/` — 01-04, theoretical
  imports assessment
- `34-broader-application-surface.md`
- `35-operator-taxonomy-status.md`
- `31-generation-experiment-review-checklist.md`
- `explainers/explainer-conducted-daydreaming.md`
- John's `THE_MEMBRANE.md` and `DESIGN_CRITIQUE.md` in
  `team-repos/Symbiotic-Vault/`
