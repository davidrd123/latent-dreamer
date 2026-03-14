## 1. Executive Verdict

* **The briefing is directionally right on backend evolution and wrong on the main bottleneck.**

  * **Local evidence:** `DESIGN_CRITIQUE.md` says the current failure is cohabitation, with agent work hidden in `_memory/`; `THE_MEMBRANE.md` defines the missing interaction surface; `THE_DRIFT.md`, `THE_SEANCE.md`, and `THE_TABLE.md` all care about salience, arrangement, and feedback more than storage.
  * **External evidence:** `query06.md` says the present win condition is not “more graph,” but moving from file-first to a one-way compiled layer only when typed queries and reuse justify it.
  * **Architectural inference:** The right review outcome is not “A until B.” It is “keep A as authoring truth, build A+ as a narrow derivative backend, and add an explicit salience/surfacing layer the briefing currently omits.”

* **The next move should be A+, not B or C.**

  * **Local evidence:** `AGENT_PROTOCOL.md` and `VAULT_DESIGN.md` already implement A; `established-findings.md` and `research-package-john-track.md` treat the SNT pattern as the strongest local prior art for compiled retrieval without graphifying the source vault.
  * **External evidence:** `query01.md` says deployed docking is file-first; `query06.md` says move next to a one-way compiled retrieval artifact; `query03.md` and `query05_integrated_answer.md` say learner state should be an overlay, not the vault itself.
  * **Architectural inference:** B is a later branch for persistent agent-memory failures. C is a later branch for curriculum-aligned learner overlays. Neither is the next build.

* **The corpus is slightly overfit to “not Graphiti.” The negative conclusion is mostly right, but it is not the real design center anymore.**

  * **Local evidence:** `briefing-for-review.md`, `memo-for-john.md`, and `research-package-john-track.md` all spend substantial energy rebutting the `notes_team.md` Graphiti pitch; the briefing itself admits the conversation was socially anchored there.
  * **External evidence:** `query02.md` does support “not now” for Graphiti: rough entity resolution, unstable community features, no decisive scale benchmark, unclear payoff at current vault scale.
  * **Architectural inference:** Stop using Graphiti as the straw antagonist. The real choice is product stance and minimal compiled structure, not continued argument about one backend.

## 2. What Holds Up

* **Keeping the vault file-first and human-writable is correct.**

  * **Local evidence:** `VAULT_VISION.md`, `AGENT_PROTOCOL.md`, and `VAULT_DESIGN.md` consistently make Markdown the human surface and forbid requiring the human to maintain graph structure.
  * **External evidence:** `query01.md` says current deployed docking is Markdown + scoped memory + MCP, not graph-first PKM.
  * **Architectural inference:** The source of truth should remain the vault, not a persistent extracted graph.

* **A one-way compiled derivative layer is the strongest next structural move.**

  * **Local evidence:** `established-findings.md` points to the SNT compiler/MCP pattern as working local prior art; `briefing-for-review.md` correctly identifies typed queries as the thing A cannot answer cheaply.
  * **External evidence:** `query06.md` says the first justified upgrade is a rebuildable compiled layer, not full graph-first infrastructure; the same file shows the wider ecosystem drifting toward hybrids.
  * **Architectural inference:** A+ is not a compromise position. It is the evidence-backed next move.

* **“Claims alongside concepts,” not “claims replace concepts,” is the right correction.**

  * **Local evidence:** the vault-agent response proposes adding `claim` to the atom kind rather than replacing the concept layer; `established-findings.md` makes the same correction.
  * **External evidence:** the stored discourse-graph and nanopublication material supports a small typed substrate, but not universal convergence on claim-only modeling.
  * **Architectural inference:** The compiler should target a claim-bearing subset, not try to coerce the whole vault into claim nodes.

* **Treating learner state as an overlay is right.**

  * **Local evidence:** `writing_concept_vault-as-personalized-learning.md` is aspirational, not a proof that vault topology equals mastery.
  * **External evidence:** `query03.md` says authoritative curriculum graph + learner overlay is the live pattern; `query05_integrated_answer.md` says passive PKM-as-mastery is under-validated.
  * **Architectural inference:** Do not use note presence, links, or graph centrality as mastery by default.

* **The new briefing is an improvement over the deleted `synthesis-for-john.md`.**

  * **Local evidence:** the diff shows the old document was a rebuttal memo; the new briefing decomposes the problem into substrates and candidate architectures.
  * **External evidence:** none needed.
  * **Architectural inference:** The current document is reviewable as architecture. The previous one mostly was not.

## 3. What Seems Wrong or Underspecified

* **The briefing is missing the layer the repo itself says is load-bearing: salience/surfacing/control.**

  * **Local evidence:** `DESIGN_CRITIQUE.md` names cohabitation as the big failure; `THE_MEMBRANE.md` defines the missing surface; `convergence-map.md` ties the core problem to feedback and salience; the exported critique chat explicitly says graph without state/salience/control is a dead warehouse.
  * **External evidence:** `query06.md` says the main pain at current stage is surfacing/cohabitation, not recall.
  * **Architectural inference:** The four-layer model is incomplete. A fifth layer, or a cross-cutting explicit layer, is needed: collaboration/salience/surfacing.

* **Layer 3 is conflated. “Agent memory / planning / execution” is too many things in one box.**

  * **Local evidence:** `briefing-for-review.md` itself says execution is bundled there “for now”; `AGENT_PROTOCOL.md` shows skills, task state, provenance, and memory already behave differently.
  * **External evidence:** `query02.md` and `query04.md` discuss memory systems, not execution systems; nothing in the stored external evidence shows that Graphiti/Cognee-class memory automatically solves planning/execution.
  * **Architectural inference:** Split this conceptually into at least “agent memory” and “agent execution/planning traces,” even if you do not build two systems yet.

* **The A → A+ → B → C ladder is too linear. C does not obviously depend on B.**

  * **Local evidence:** the briefing defines C as “B plus learner overlay.”
  * **External evidence:** `query03.md` and `query05_integrated_answer.md` support learner overlay on top of curriculum graph + learner evidence. They do not require a Graphiti-style agent-memory layer first.
  * **Architectural inference:** The actual graph is A → A+, then branch: B if agent memory becomes a problem, C if learner overlay becomes a real product need.

* **The SNT transfer argument is fair on plumbing and overstated if read as creative-work validation.**

  * **Local evidence:** `research-package-john-track.md` and `memo-for-john.md` do state that the plumbing transfers but the epistemology does not; still, they lean heavily on SNT as practical justification.
  * **External evidence:** the stored external evidence supports compiled derivatives in general, not SNT-style creative usefulness.
  * **Architectural inference:** SNT proves “one-way compiler + MCP can work.” It does not prove “creative vault users will want `what_shifts_if` queries” or accept the same discipline.

* **The proposed creative-domain schema is too invented to be the first compiler schema.**

  * **Local evidence:** the briefing proposes statuses like `committed/exploring/tension/dormant/composted` and edges like `resonatesWith/complicates`.
  * **External evidence:** the stored critique material repeatedly says start with boring primary atoms and keep richer interpretation as overlays; the bidirectional-coupling research supports Q/C/E + provenance and discourse overlays, not “resonance” as a primary core type.
  * **Architectural inference:** Do not start A+ with a poetic ontology. Start with a narrower boring subset and derive the richer layer later.

* **The corpus is over-indexing on anti-Graphiti and underweighting other B-layer candidates.**

  * **Local evidence:** Cognee, GraphMD, skills.md→KG, and Jim White’s text2kg direction are listed only as “uninvestigated leads,” despite the briefing claiming B may eventually solve inference/execution gaps.
  * **External evidence:** none in the selected corpus.
  * **Architectural inference:** You cannot center those alternatives yet, but the current document should admit that B-backend choice is under-researched, not just “Graphiti probably no.”

## 4. Layer Review

* **Layer 1: Human Authoring Substrate — correct.**

  * **Local evidence:** `VAULT_VISION.md`, `AGENT_PROTOCOL.md`, `THE_DRIFT.md`, `THE_SEANCE.md`, `THE_TABLE.md`, and `THE_COMPOST.md` all depend on ambiguity, low-friction capture, and human prose staying primary.
  * **External evidence:** the layered-assistance argument in `established-findings.md` says the writer’s sentence-level craft must remain protected.
  * **Architectural inference:** Keep this layer flat-file and permissive. Do not compile it wholesale.

* **Layer 2: Docking / Query Substrate — correct.**

  * **Local evidence:** `AGENT_PROTOCOL.md` already does inference-time semantic traversal; the briefing correctly identifies typed queries as the pressure that A cannot serve well.
  * **External evidence:** `query01.md` and `query06.md` support file-first docking plus thin bridges and compiled retrieval artifacts.
  * **Architectural inference:** This is the right place for A+.

* **Layer 3: Agent Memory / Planning Substrate — partly wrong because it is conflated.**

  * **Local evidence:** the briefing itself bundles memory, planning, and execution; no local doc shows they share one data model cleanly.
  * **External evidence:** `query02.md` and `query04.md` only speak to memory-like graph systems, not execution architecture.
  * **Architectural inference:** Keep the layer, but split the concept. Treat “agent memory” as a candidate system. Treat planning/execution as instrumentation and protocol until proven otherwise.

* **Layer 4: Learner Model / Curriculum Overlay — correct, but premature.**

  * **Local evidence:** `writing_concept_vault-as-personalized-learning.md` defines the aspiration but not a workable metric of mastery.
  * **External evidence:** `query03.md` and `query05_integrated_answer.md` support overlay framing and reject silent mastery inference from PKM alone.
  * **Architectural inference:** Keep it as a future branch, not an active architecture layer.

* **Missing Layer: Salience / Surfacing / Collaboration Surface — essential.**

  * **Local evidence:** `DESIGN_CRITIQUE.md` and `THE_MEMBRANE.md` make this the actual unsolved problem; `convergence-map.md` says the missing issue is feedback/salience, not just structure.
  * **External evidence:** the critique stored in the integration chat says state/salience/control are required for any graph-backed writing system to feel alive.
  * **Architectural inference:** The architecture is not complete until this is explicit. This is the layer that makes the graph pay rent.

## 5. Architecture Recommendation

* **Architecture A: keep it as the canonical authoring/storage model, but stop pretending it is sufficient.**

  * **Local evidence:** A is already the actual vault design in `AGENT_PROTOCOL.md` and `VAULT_DESIGN.md`.
  * **External evidence:** `query01.md` says this is also the current deployed docking pattern.
  * **Architectural inference:** A is the right foundation and the wrong endpoint. It preserves the source vault. It does not solve typed retrieval or cohabitation by itself.

* **Architecture A+: build this next.**

  * **Local evidence:** the briefing correctly identifies typed questions as the A failure mode; SNT is the strongest local proof that one-way compilation + MCP is feasible.
  * **External evidence:** `query06.md` explicitly recommends a one-way compiled layer as the next move; `established-findings.md` converges on a boring typed substrate with provenance.
  * **Architectural inference:** A+ is the right next structural move, but only if it compiles a narrow, boring subset rather than the whole vault.

* **The minimum thing to build is this, exactly.**

  * **Local evidence:** `established-findings.md` recommends Q/C/E/Source + provenance as v1; the vault-agent response says add `claim`; `DESIGN_CRITIQUE.md` says any backend must surface in live work.
  * **External evidence:** the stored discourse-graph, ARTIST, and query06 material support prose-first UI, small typed substrate, provenance, and hybrid retrieval.
  * **Architectural inference:**

    1. Add `claim` as an atom kind, but do not migrate all atoms.
    2. Define a **compilable subset** only: explicit Questions, Claims, Evidence, Sources, plus provenance spans and revision events.
    3. Compile that subset one-way into SQLite.
    4. Keep the edge grammar tiny: `supports`, `opposes`, `informs`, `sourceOf`, `revises`.
    5. Expose only a few MCP operations: scoped search, trace support, trace conflict, what changed, what shifts if X changes.
    6. Do **not** ship a graph editor. Surface results in a prose-adjacent sidebar/margin/backlink surface.
    7. Instrument everything: correction rate, acceptance/dismissal rate, time-to-answer, whether a surfaced item changed the draft, and whether users voluntarily reopen the compiled view.

* **Architecture B: do not build now. Run a research spike later if A+ leaves a real memory hole.**

  * **Local evidence:** no selected local doc shows persistent agent-memory failure as the current bottleneck; the main local complaint is cohabitation.
  * **External evidence:** `query02.md` says Graphiti is rough and probably premature; `query04.md` says temporal infrastructure does not buy pedagogy.
  * **Architectural inference:** B becomes justified only when cross-session entity/state memory becomes a recurring failure after A+ exists. Until then, B is a research track, not the next build.

* **Architecture C: do not build. Start event logging only.**

  * **Local evidence:** the learner aspiration is real, but the vault has no validated mastery signal yet.
  * **External evidence:** `query03.md` and `query05_integrated_answer.md` say learner overlay needs authoritative curriculum graph plus explicit evidence, not note topology.
  * **Architectural inference:** The only current C-adjacent work worth doing is cheap groundwork: append-only event logging for asserted claim, revised claim, support added, contradiction noted, confidence/confusion marked, recall success/failure if that use case appears.

## 6. Breakpoint Criteria

* **A → A+ transition:**

  * **Local evidence:** the briefing and `established-findings.md` keep returning to typed questions; `DESIGN_CRITIQUE.md` says hidden agent work is a problem.
  * **External evidence:** `query06.md` says the breakpoint is query type + reuse + typed operations, not vault size.
  * **Architectural inference:** Move from A to A+ when, over a 2–4 week window, at least three of these are true:

    1. support/contradiction/change/path questions recur weekly;
    2. the same note subset is reread across roughly ten or more interactions;
    3. answers to typed questions are slow or unstable because structure is re-derived each time;
    4. a minimal schema has stopped changing every few days;
    5. users are asking impact questions like “what shifts if this changes?” often enough to justify compiled traces.

* **A+ → B transition:**

  * **Local evidence:** nothing local yet proves this need.
  * **External evidence:** `query02.md` says graph memory pays when recurring entities, changing facts, and chronology-sensitive multi-hop queries all converge.
  * **Architectural inference:** Move from A+ to B only when persistent agent-memory failures remain after A+ exists. Concrete signals: weekly re-resolution of the same people/projects/facts across sessions, repeated need for temporal invalidation or change history that is not authored in the vault, and visible cost from re-grounding the same agent context. If the failures are still about authored support/contradiction, stay at A+.

* **A+ → C transition; B is not required.**

  * **Local evidence:** the briefing currently assumes C comes after B; nothing else in the corpus really requires that.
  * **External evidence:** `query03.md` and `query05_integrated_answer.md` support learner overlay on top of curriculum graph + learner evidence stream.
  * **Architectural inference:** Start C only when all four are true:

    1. a stable external curriculum/domain graph exists;
    2. the vault can map some claim/concept subset onto it;
    3. explicit learner events are being logged;
    4. there is an actual decision use case, meaning “what should I learn next?” not just “it would be neat to model mastery.”

## 7. Missing Question

* **The missing question is still: is this a writing tool with a graph, or a graph tool that produces writing? The corpus has not actually answered it, and it needs to.**

  * **Local evidence:** the original Thread 6 in `threads_from_chat.md` asks exactly this; `DESIGN_CRITIQUE.md`, `THE_MEMBRANE.md`, `THE_DRIFT.md`, `THE_SEANCE.md`, and `THE_TABLE.md` all point toward a writing-centered system whose value appears during work, not in a separate graph console.
  * **External evidence:** the stored discourse-graph, ARTIST, Tana, and query06 material all point toward prose-first or outline-first interaction, not graph-first creative workflow.
  * **Architectural inference:** The answer should be made explicit now: **this should be a writing tool with a graph**, not a graph tool that happens to emit prose. Once that is settled, the next missing design question becomes sharper: **what salience mechanism decides what the writing surface should see right now?**

## 8. Research Gaps

* **There is no same-corpus benchmark for A vs A+ vs B on live creative-vault work.**

  * **Local evidence:** `query06.md` says this explicitly; the briefing also acknowledges the gap.
  * **External evidence:** the stored benchmarks are about QA, summarization, code, or structured domains, not Membrane/Drift/Table-style work.
  * **Architectural inference:** The next empirical work should be a tiny internal benchmark, not another general GraphRAG survey.

* **B-layer alternatives are under-researched.**

  * **Local evidence:** Cognee, GraphMD, skills.md→KG, and Jim White’s text2kg direction are only named, not evaluated.
  * **External evidence:** none in the selected corpus.
  * **Architectural inference:** If B matters later, the next research pass should compare those directly rather than continuing to orbit Graphiti.

* **The compiler schema for creative work is still under-evidenced.**

  * **Local evidence:** the creative status and edge taxonomy in the briefing is proposed, not validated; `established-findings.md` warns against moving too quickly to rich primary ontologies.
  * **External evidence:** the stored discourse-graph evidence is strong for argumentative writing, weaker for free-form ideation.
  * **Architectural inference:** Start with a claim-bearing subset. Do not assume the whole creative vault wants the same grammar.

* **The salience layer is conceptually present and mechanically absent.**

  * **Local evidence:** multiple docs say this is the actual problem; none specify how salience is computed.
  * **External evidence:** the critique chat insists on state/salience/control, but does not hand you an algorithm.
  * **Architectural inference:** Research should shift from “more structure?” to “what should surface, when, and why?”

* **The SNT transfer claim needs direct testing in creative work.**

  * **Local evidence:** only summaries of the SNT project are in scope, not the underlying system.
  * **External evidence:** external evidence supports hybrids in general, not this exact translation.
  * **Architectural inference:** Run a prototype on a small creative subset before treating SNT as more than plumbing precedent.

* **Learner overlay remains a future research track, not a build track.**

  * **Local evidence:** the aspiration is present, but the vault has no accepted mastery proxy.
  * **External evidence:** `query05_integrated_answer.md` says passive PKM mastery inference is still thin.
  * **Architectural inference:** The only justified near-term learner work is event logging and mapping discipline, not pedagogy claims.
