### D10. Sequencing: what belongs when?

Given the current project state (supply pilot passed, provocation
experiment next, watchable-runtime work pending), sequence the
imports from D1-D8.

## D10 answer

### Direct answer

Do **not** pull the whole nearest-neighbor stack forward at once.
Right now the project has one immediate architecture test: prove that a
human-gated typed provocation object can produce **structurally new,
keepable material** when combined with carried-forward internal state.
Anything that does not change that test design, its reduced context, or
its evaluation should stay out.

So the order is:

1. **Before the provocation-delta experiment:** import only seam-shaping
   ideas — FAtiMA's typed foreign-reasoner seam, retrieval discipline
   from the LLM-first baselines, and Loyall's irrationality guardrail.
2. **After that experiment passes, before full graph assembly:** import
   coherence and validation machinery — IPOCL pursuit threads,
   MINSTREL-style checker/enhancer control, selective Sabre critics,
   and graph-level validation/eval.
3. **When watchable-runtime work is actually underway:** import runtime
   execution-state machinery — ABL/Loyall-style active execution state,
   continuation guards, and any counterfactual-view surface.
4. **Only on failure:** deeper belief modeling, dialogue topology,
   full joint-behavior machinery, large typed mutation libraries, or
   reflection-heavy LLM scaffolding.

That is the concrete sequence. Anything else is premature parallelism.

### Phase 0 — before the provocation-delta experiment

These are the only imports that should change the next experiment.
Everything here either shapes `ProvocationContextV1`, shapes the delta
schema, or makes the no-delta vs delta comparison interpretable.

| Import source | Minimum viable import | Why it belongs now | Do **not** import yet |
|---|---|---|---|
| **D7 seam question itself** | Freeze the Ask-B split: `FixtureDeltaV1` for external proposal, `CarryForwardStateV1` for cross-situation residue, `SituationLocalStateV1` for active local state, `ProvocationContextV1` for reduced summary. Keep proposals append-only and human-gated. | This is the actual experiment. Without the split, the provocation test collapses into prompt mush. | Do not give the runtime Director world-authoring authority. Do not collapse delta + carry state into one blob. |
| **FAtiMA** | Import the **meta-belief seam** pattern, cut down to named typed fields in `ProvocationContextV1` and retrieval/context sidecars. Treat retrieval outputs, coverage targets, and carry-forward summaries as explicit foreign-computation fields, not raw state dump prose. | `36-current-synthesis.md` already identifies this as the right pattern for Provocation Generator integration. It directly affects the next seam design. | Do not import FAtiMA's full toolkit, full dialogue graph machinery, or broad action-rule engine. |
| **Generative Agents** | Import **multi-factor retrieval discipline** and a human-legible sidecar for what evidence packet was used. Use a scored packet, not a flat context dump. | The immediate risk is generic provocations. A reduced context needs explicit retrieval competition, not just more prompt text. | Do not import reflection memories, freeform agent planning, or natural-language memory as canonical state. |
| **Character is Destiny** | Import **description-guided retrieval** for scenario-specific evidence packets and log retrieval failure separately from final output failure. | The Provocation Generator needs a compact dossier + scenario packet so pushes are specific to Kai/Tessa rather than generic conflict prompts. | Do not build the full benchmark yet. Do not turn this into a runtime action architecture. |
| **Loyall/Hap** | Import one **design guardrail**: irrationality stays first-class. In practice, the delta generator and validator should preserve avoidance, defensiveness, and self-protective reinterpretation as allowed outcomes rather than "repairing" them into clean confrontation. | The provocation loop will otherwise cheat by writing neat world pushes that bypass the very inner process the system is trying to protect. | Do not import concurrency, ABT, or full runtime behavior semantics yet. |

#### Concrete next experiment after these imports

1. Hand-author 2-3 provocation proposals on one existing fixture.
2. Build `ProvocationContextV1` from actual carried-forward state, not
   from raw fixture prose.
3. Give the Provocation Generator a compact dossier + retrieved evidence
   packet.
4. Compare `no delta` vs `typed delta` on:
   - operator ranking change
   - retrieval set change
   - whether at least one admitted candidate is structurally new rather
     than a paraphrase
   - keep rate / edit burden

If that test fails, stop. Do **not** pull in IPOCL, Sabre, ABL, or
MINSTREL to rescue a seam that has not earned itself.

### Phase 1 — after the provocation-delta experiment, before full graph assembly

Only if the delta experiment produces structurally new, keepable
material should the project pull in the next layer. This phase is about
**multi-situation coherence and graph assembly quality**, not runtime
showmanship.

| Import source | Minimum viable import | Why it belongs in this phase | Do **not** import yet |
|---|---|---|---|
| **IPOCL / narrative planning** | Add a lightweight **`PursuitThread`** object with motivating event, supporting nodes/situations, owner, and status (`active / blocked / abandoned / achieved`). | `36-current-synthesis.md` already banks this for the later multi-situation phase. It is the right bridge once the system moves from one provoked step to assembled multi-situation material. | Do not build a full symbolic planner or make L3 into IPOCL. |
| **Sabre** | Add a narrow **explanation critic** for belief-sensitive sequences, plus explicit ownership and observer fields on important moves. Use it selectively for deception, concealment, rumor, and staggered revelation. | Once full graph assembly starts, "does this make sense from what the character thinks is true?" becomes a real verifier question. It is not needed for the first seam proof. | Do not import deep nested beliefs globally. Do not turn runtime directing into centralized puppetmaster planning. |
| **MINSTREL** | Add an explicit **propose → check/repair → enhance** L1 loop. Minimum version: checker pass for inconsistency/orphan-setup/dead-branch issues, enhancer pass for setup/payoff closure and cross-link enrichment after admission. | After the seam works, the next risk is debris: graph growth without coherence. MINSTREL is strongest here, on the authoring side. | Do not build a 24-TRAM library yet. Do not fragment generation into a huge mutation ecology before the checker layer proves necessary. |
| **FAtiMA** | Add authoring-time **validation surfaces**: reachability, dead branches, state/rule visibility, and maybe rule-trigger coverage if graph assembly gets larger. | This becomes valuable once there is enough graph material for graph-level failure to exist. It is overkill before seam proof. | Do not import dialogue-state topology unless dialogue scenes specifically become the bottleneck. |
| **Character is Destiny** | Build a lightweight **decision-point / prefix-time evaluator** for major later choices in the assembled graph. Track when a later choice becomes inferable, and whether failure is retrieval, motive recovery, or final-choice mapping. | Once graph assembly exists, authored-choice legibility becomes measurable instead of hand-wavy. | Do not build a massive benchmark harness first. One evaluator over a few key choices is enough. |
| **D6 counterfactual preservation** | Preserve rejected or abandoned candidates as a **sidecar ledger** with provenance (`rejected_candidate`, `abandoned_rehearsal`, `superseded_variant`), but keep them non-canonical and non-traversable by default. | This is the safest time to start learning whether abandoned alternatives are actually useful, without blurring the graph membrane during the first seam proof. | Do not yet let rejected material directly drive runtime retrieval or traversal. |

#### The concrete order inside Phase 1

1. `PursuitThread` sidecar
2. checker/repair pass
3. selective Sabre explanation critic on belief-sensitive material
4. graph-level validation + reachability checks
5. decision-point / prefix-time evaluator
6. counterfactual sidecar ledger

That order matters. Pursuit continuity and checker passes make graph
assembly legible. The evaluator and counterfactual ledger come after
there is enough material to measure.

### Phase 2 — wait until watchable-runtime work is actually underway

This phase starts only when the project is doing the work described in
`experiential-design/13-project-state.md`, `17-game-engine-architecture.md`,
`20-narration-layer.md`, and `21-graffito-v0-playback-contract.md`:
joined playback packets, narration/dashboard legibility, and judged
watched runs. Do **not** import these early just because they are
interesting.

| Import source | Minimum viable import | Why it waits for watchable runtime | What problem it is supposed to solve |
|---|---|---|---|
| **ABL** | A lightweight **`SituationRuntime`** / active execution state with start guards, continue guards, interrupt handlers, sync points, and local shared state. | This is execution-state machinery. It does not help prove the provocation seam. It matters only once the watched run says: "I can see what got selected, but I cannot tell what is currently being attempted." | Gives the inner-life surface a live answer to "what is unfolding right now?" |
| **Loyall + ABL** | Add **continuation / interruption semantics** and a minimal inspectable active state for enacted moves. | These are runtime legibility imports, not authoring-seam imports. | Prevents the watched output from reading as discrete beats with no ongoing attempt structure. |
| **Generative Agents** | Add a human-legible **memory/reflection sidecar** only for narration/dashboard use. Scheduled synthesis with evidence refs is acceptable here. | The narration layer and dashboard may need compact higher-level summaries, but that is a viewer-facing problem, not the next seam problem. | Makes the system easier to watch without flattening canonical state into text. |
| **D6 counterfactual preservation** | Add a **counterfactual channel** to narration/dashboard only if watched runs show that seeing only the realized path makes the mind unreadable. | The viewer-facing question — "what almost happened?" — belongs to the watchable-runtime phase, not the seam phase. | Makes fantasy/rehearsal/abandoned alternatives visible as part of the product, not just the authoring ledger. |

The watchable-runtime phase is not a license to dump raw ontology onto the
screen. The playback packet still stays thin. If these imports happen,
they should project into joined viewer surfaces, not leak lane-local
internals into the graph seam.

### Phase 3 — only import when a specific failure appears

These are real ideas, but they should stay out of the codebase until a
specific observed failure names them.

| Import source | Failure signature that would justify it | Import then |
|---|---|---|
| **Sabre deep belief modeling** | Deception, concealment, rumor, or staggered-revelation scenes repeatedly fail because characters act on author truth instead of believed truth. | Add explicit observation semantics and selective deeper belief state for those fixtures only. |
| **FAtiMA dialogue-state topology** | Multi-character dialogue scenes become incoherent or unreachable, and the issue is clearly conversational topology rather than character motivation. | Add an authored dialogue-state graph for those scenes only. |
| **ABL joint behaviors / team memory** | Two or more characters need tight synchronized enactment and the simpler runtime state cannot coordinate them. | Add joint-entry/exit and local shared-memory coordination. |
| **Loyall concurrency / resource-conflict machinery** | Watched runs show characters feel too serial or too cleanly single-minded, and that flatness is traceable to lack of concurrent goal pressure. | Add explicit concurrent goal handling and resource/conflict rules. |
| **MINSTREL typed mutation ecology** | Checker + enhancer + ordinary generation still yield repetitive filler, and the failure is clearly lack of controlled transformation rather than weak prompts or poor retrieval. | Add a small typed mutation library with cost/penalty selection. |
| **Generative Agents reflection/planning stack** | Retrieval packets repeatedly miss obvious higher-level abstractions, and authored sidecars are not enough. | Add scheduled reflection as a sidecar artifact, not canonical state. |
| **Character is Destiny gold-motivation upper-bound eval** | Choice-legibility failures remain ambiguous even after retrieval logging and prefix-time analysis. | Add explicit motive-recovery upper-bound tests to separate retrieval failure from reasoning failure. |

### What to keep out entirely until evidence says otherwise

- full Sabre-style centralized planning
- full IPOCL generation of the story itself
- full ABL runtime as a new substrate
- full FAtiMA toolkit adoption
- full TRAM/operator ecology from MINSTREL
- natural-language memory stream as canonical state
- giving the runtime Director authoring-time world-mutation powers

### One-line rule

**Seam first, coherence second, watchability third, rescue machinery only
on observed failure.**

That is the sequence that matches the actual project state instead of an
imagined all-fronts advance.

---

## Context to include (paste into chat)

Essential:
- This document
- `reading-list/18-loyall-extraction.md`
- `reading-list/19-abl-extraction.md`
- `reading-list/20-fatima-extraction.md`
- `reading-list/21-narrative-planning-extraction.md`
- `reading-list/22-sabre-extraction.md`
- `reading-list/23-minstrel-followup.md`
- `36-current-synthesis.md` (current project state)

If context allows:
- `reading-list/24-generative-agents-extraction.md`
- `reading-list/25-character-is-destiny-extraction.md`
- `11-settled-architecture.md`
- `reading-list/17-nearest-neighbors-reading-list.md`
- `deep-research-03/replies-02/Ask-B-01.md`
- `deep-research-03/replies-02/Ask-B-02.md`

## Preferred answer format

For each question section (D1-D10):

1. **Direct answer** — what we should actually do, not just
   what's interesting
2. **What to import** — specific mechanisms worth adopting,
   with the minimum viable version
3. **What to skip** — mechanisms that are interesting but wrong
   for our use case, with why
4. **What changes our architecture** — if adopting this
   mechanism would require a structural change, name it
5. **Priority** — is this needed before the next phase
   (provocation experiment + watchable run), or is it a
   later concern?
