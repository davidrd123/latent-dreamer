# Session Log: 2026-03-14/15 — Generation Pipeline Build

## Scope

Marathon session continuing the pressure engine reframe arc.
Started with reading list extractions, moved through L3 experiment
review, the authoring-time generation reframe, the L2 middle-layer
schema, and ended with a four-mechanism generation pipeline validated
on four benchmarks plus the first real batch/curation pass.

---

## 1. Reading List Extractions (12 papers + Mueller)

Created extraction notes for all sources in the reading list:

**L3 (traversal scheduler):**
- 01-facade-extraction.md — pipeline shape, scoring formula, priority tiers
- 02-dodm-extraction.md — declarative feature sum, intervention vocabulary
- 03-authorial-leverage-extraction.md — evaluation methodology
- 04-suspenser-extraction.md — structural tension (1/solutions)

**L2 (character inner life):**
- 05-ema-extraction.md — appraisal cycle, controllability, reappraisal
- 06-occ-extraction.md — emotion type taxonomy, intensity derivation
- 07-versu-extraction.md — social practices, affordance menus

**L1 (world-building):**
- 08-sentient-sketchbook-extraction.md — continuous evaluation, novelty
- 09-minstrel-extraction.md — transform-recall-adapt for dead ends

**Shared infrastructure:**
- 10-atms-extraction.md — assumption management, nogoods

**Mueller book:**
- 11-mueller-ch7-extraction.md — full procedural shape, serendipity,
  reminding, concern initiation, evaluation metrics
- 12-mueller-appendix-b-extraction.md — narration layer design

All extraction notes were reviewed by codex and corrected for
boundary violations:
- Versu: conductor mapping removed (conductor is above L3, not an L2 practice)
- OCC: interest/disgust added to revised hierarchy
- Sentient Sketchbook: novelty scoped to ranking term, not independent driver
- MINSTREL: scoped to L1 only, not L2 runtime
- EMA: controllability as one input among several, not sole dispatcher

---

## 2. Mueller Book Deep-Read

Read chapters 2, 3, 4, 6, 7, and 11 via agents. Key findings
beyond what the reading list covered:

- **Emotional feedback system (§3.4):** Rationalization permanently
  modifies episode emotional valence. Episodes retrieved later carry
  updated emotion. Mechanism for characters "getting over things."
- **Daydream evaluation metrics (§4.4):** Desirability × Realism ×
  Similarity ordering. Realism thresholds differ by goal type
  (personal > learning > emotional).
- **Fanciful planning (§6.5):** Plausible planning, subgoal relaxation,
  inference inhibition. Realism thresholds per goal type.
- **Full serendipity mechanism (§7.7):** Not a scalar bias but
  bidirectional intersection search in rule-connection graph + path
  verification. Five triggers. Mutation subordinate to serendipity.
- **Reminding (§7.5.3):** Recursive index expansion with emotion
  reactivation. Retrieved episodes activate their other indices,
  retrieving further episodes.
- **Seven daydreaming goals (§3.3):** Full taxonomy mapped against
  kernel operator set. RECOVERY and REHEARSAL missing as distinct
  operators. REPERCUSSIONS not implemented.

---

## 3. L3 Experiment Tracking

Reviewed the full L3 experiment arc:
- Graffito pilot: Facade shape validated, conductor works both directions
- City Routes arms A+B: Facade scales to 29 nodes / 6 situations
- City Routes arm C: DODM features add structural behavior, seed-robust
- Robustness sweep: 15/15 on threshold coverage
- Overdetermination scoring: e2 up to 15/15, e4 drops to 9/15
- Conductor: real but limited (2/3 seeds diverge with contrasting presets)
- Fixture vocabulary fixes: seam enums, ref namespace, option_effect

---

## 4. 5 Pro Round 2 Review

Read all six responses from first-round-02:
- 00: L1/L2/L3 boundary review — L2 leaking runtime ontology, stale
  conductor model in doc 05, graph schema underspecified
- 01: Graph interface seam audit — lane-collapse risk, missing lineage,
  unstable field vocabularies
- 02: Failure mode review — five failure modes with exposing tests
- 03: Missing lineage scan — storylets/QBN, claim-centered discourse,
  event segmentation, NIME conductor mapping
- 04: Alternative architecture — two-stage graph-centric stack as
  strongest competitor
- 05: Evaluation and falsification — experiment order, success/failure
  criteria, baselines

---

## 5. Authoring-Time Generation Reframe

Absorbed the morning conversation (first-round-03 chat) that shifted
the bottleneck from scheduler theory to material supply.

Key decisions:
- L1 flipped from evaluative (critic) to generative
- L1 becomes authoring-time orchestration over L2-style generation
- Critic demoted to stage 2 (after material exists)
- Output must compile to frozen graph seam
- Embeddings and aesthetic filtering deferred

Created doc 27 (authoring-time-generation-reframe.md) and updated
execution roadmap (doc 13).

---

## 6. L2 Middle-Layer Schema (5 Pro)

Integrated two rounds of 5 Pro schema feedback:

**First response:** Full schema with CausalSlice, AppraisalFrame,
EmotionVector, SocialPracticeInstance, Affordance, OperatorCandidate,
GeneratedNodeProvenance. Plus l2-step transition function.

**Second response (v1 contract):** Tightened for prototype:
- CausalSliceV1 (9 fields, short-lived, sidecar-only)
- PracticeContextV1 (4 fields, lightweight)
- Concrete reappraisal formula with per-commit-type rules
- Retrieval rule (exact-match, top 2, score >= 2)
- Prompt-conditioning rule (no EmotionVector in v1)
- Field split (graph / sidecar / runtime-only)
- Ablation failure criteria for each middle-layer object
- Canonical benchmark (Kai, unopened letter, harbor)

Created doc 28 (l2-schema-from-5pro.md) with v1 contract section.
Patched docs 13 and 27 for contradictions.

---

## 7. Worked Trace and Prototype Spec

**Worked trace (doc 29):** Full 10-step chain through v1 middle layer
for Kai benchmark. Each step adds distinct value:
- CausalSlice turns vague pressure into threatened goal + attribution
- AppraisalFrame gives low controllability that changes operator ranking
- PracticeContext prevents unearned confrontation moves
- Generated candidate compiles to graph seam
- Reappraisal updates concern intensities after commit

**Prototype spec (doc 30):** Two-arm comparison (flat baseline vs
middle-layer), five required ablations, 10-step implementation order.

---

## 8. Generation Prototype Implementation

### Harness built
- authoring_time_generation_prototype.py
- Stub, Gemini, and OpenAI providers
- Structured output via Pydantic schema for graph compliance
- Three ablations: no_causal_slice, high_controllability, swap_practice_context

### First Gemini runs
- Contract compliance initially failed (invented refs)
- Fixed with structured output + explicit allowed-ID blocks
- Behavioral prescription in Arm B prompt caught and removed
  (was contaminating no_causal_slice ablation)
- Created review checklist (doc 31) to prevent future contamination

### Cleaned comparison results
- Graph compliance: solved
- Operator control: proven (ablation flips stable)
- CausalSlice: directionally positive
- Comparison memo written (doc 32)

### Three-benchmark validation
- Kai (avoidance), Maren (rehearsal), Rhea (rehearsal)
- Ablation flips consistent across all fixtures
- Cross-fixture contamination fixed
- Cross-benchmark memo written (doc 33)

---

## 9. 5 Pro Mechanism Design (Round 3)

Five parallel requests sent and received:

**r2 (near-term mechanisms):** Four local mechanisms for the harness:
1. Scored rule-abduction for concern extraction (shadow mode first)
2. Accepted-only writeback + one-hop reminding for multi-step
3. Hard-filter + soft reranker for candidate compilation
4. Boundary detector + tiny temporal relations

**r3 (three candidate imports):** Abduction, soft-constraint
compilation, POCL-lite. Full compiler pipeline sketch with
AbductiveHypothesis and CausalLinkSketch objects.

**r4 (conductor mapping):** Don't map 9D state to APC Mini.
Use 8 faders (hold/release, escalation, recall, intensity, 4 lanes)
+ 8 pads. Derive tension/energy/etc internally. Wizard-of-Oz test
protocol with acceptance bar.

**r5 (dashboard):** Two surfaces, not one. Performance dashboard
(85% current state, 15% residue) + authoring membrane
(provenance-heavy, freeze/dismiss/respond/cut). Narration: packet-first,
provenance-derived hints only.

---

## 10. Four-Mechanism Build

### Concern inference from primitives
- Normalized fixture schemas (theme_rules, primitive_facts)
- infer_concerns_from_primitives() in shadow mode
- Shadow mode validated: dominant_match true on all three fixtures
- use_inferred_concerns switch added and validated
- Inferred path matches seeded path on all three fixtures

### Multi-step accumulation
- --sequence-steps N for multi-step generation
- Accepted nodes write back as retrievable episodes
- One-hop reminding from previous accepted episode
- Concern reappraisal between steps
- Validated on Gemini: Kai intensifies (0.87→1.0),
  Rhea softens (0.56→0.26)

### Candidate compilation
- --candidates-per-step K generates K candidates per step
- Hard filter (graph validity + contamination)
- Soft scoring (specificity, legibility, distinctiveness, coverage)
- Within-step sibling diversity for cold-start
- Validated on Gemini: compiler picks different candidates
  on later steps (1/4, 2/4, 4/4)

### Boundary detection
- New segment on: situation change, concern change, operator change,
  practice change, option_effect threshold crossing
- Temporal relations: during, after, rehearsal_for
- Trace-only, no graph seam changes
- Awaiting end-to-end validation

---

## 11. Additional Notes Created

- doc 13 (LLM roles in architecture) — where LLMs play a role,
  where excluded, five decision gates
- doc 31 (generation experiment review checklist) — seven-point
  contamination prevention
- doc 34 (broader application surface) — mechanisms generalize
  beyond conducted performance
- doc 35 (operator taxonomy status) — Mueller's seven, gaps,
  expansion candidates
- explainer-conducted-daydreaming.md — illustrated human-readable
  overview with ASCII diagrams

---

## 12. Cross-Project Connections

### John's Symbiotic Vault
- Membrane design (freeze/dismiss/respond/cut) maps to generation
  curation interface
- Design Critique diagnosis ("roommates who leave notes on the fridge")
  applies to generation pipeline's surfacing problem
- Memory note created linking the two projects

### Vault changes reviewed
- Protocol refinements (dev mode vs use mode)
- New trace skill (Obsidian canvas generator)
- Claim added to atom kind enum
- Design experiments reorganized into subfolder
- Tana comparison with research directions

---

## 13. Theoretical Grounding Discussion

Discussed connections to broader psychology/cognitive science:
- William James (stream of consciousness, fringe)
- Freud (defense mechanisms → operators)
- Jung (shadow → retrievable avoided material)
- Predictive processing (concerns as prediction errors)
- Attachment theory (styles as operator bias parameters)
- Narrative identity (overdetermination as coherence)
- Internal Family Systems (concern competition as parts model)
- Conceptual blending (serendipity as blend-finding)
- Cognitive linguistics (narration layer construction choices)

---

## 14. Source Syntheses Received

Three 5 Pro synthesis documents for human reading:
- source_synthesis_daydream_stack.md — all sources organized by
  what they do in the stack
- mueller-authoring-time-synthesis.md — Mueller for the generation
  pipeline specifically
- pressure-engine-reframe-synthesis.md — full project state for
  collaborator onboarding

Plus source lineage deep audit (source-lineage-deep.md) checking
extraction fidelity and phase calibration.

---

## 15. Quartet Validation and Batch Generation

### Tessa rationalization fixture
- Fourth fixture added: actual aftermath, status_damage, confession practice
- Required: aftermath CausalSlice, actual/low-control appraisal,
  family_bonus reaching operator scoring, confession resonance
- 5 Pro rationalization benchmark review (Q2, Q2a) identified four
  issues: expected_* contamination, close→ontic on rationalization,
  behavioral prompt leading, exemplar-token checker
- Validated on Gemini Pro: rationalization x3 on derived path

### Quartet regression
- All four fixtures validated on derived path:
  Kai (avoidance), Maren (rehearsal), Rhea (rehearsal), Tessa (rationalization)
- All graph-valid, contamination-clean

### Batch generation
- 5 parallel sequences × 5 steps × 4 candidates = ~100 candidates
- 25 pooled accepted nodes → 8 admitted
- Admission hardened: semantic gate ≥ 0.75, sequence diversity cap,
  duplicate detection

### Post-quartet hardening
- Kai batch curation packet written (doc 34-kai-batch-curation-packet):
  batch path earns its keep, but Kai remains narrow: strong avoidance
  texture, weak progression/closure
- Tessa hardening pass landed:
  - runtime_practice_templates now available for real runtime practice
    selection
  - aftermath status_damage no longer depends on expected_practice_context
  - rationalization operator description made definitional rather than
    behavioral-leading
  - rationalization close→clarify normalization when no outward
    irreversible act happened
  - Tessa semantic checks made more structural and less exemplar-token
    dependent
- One remaining cleanup after rerun: Tessa worked-trace calibration
  references likely need syncing to the new derived path

### Correctness fixes during this phase
- Policy reappraisal now follows v1 contract (bucket-shift dependent)
- Non-Kai derivation handles obligation and status_damage properly
- Commit type derives from accepted candidate, not fixture expectation
- Reappraisal targets only affected concerns (origin_pressure_refs)
- Calibration shortcuts removed as default path
- 10 unit tests covering these semantics

---

## 16. 5 Pro Quality and Bridge Reviews (Q1, Q3, Q4)

### Key worldview shift
Before: "generation works, scale it up"
After: "generation works locally, diversity/seam-richness/duplicate-function
are the real bottleneck, and the end-to-end proof is a patch test"

### Q1 (batch admission)
- Batch is policy on top of existing mechanisms, not a new subsystem
- Add pooled overdetermination_gain to admission scorer
- Curation packet as shortlist cards, not a dump

### Q3 (generation quality) — most important
- Read actual outputs. 3/6 keepable. Prose passes "action under pressure"
- Bottleneck is diversity, not sentence quality
- Template collapse: three kitchen-avoidance variants, three threshold-
  rehearsal variants. Locally strong, globally redundant.
- Graph-valid is not graph-useful: prose carries mixed pressure but
  graph projection collapses to dominant concern
- Five scaling risks: template collapse, false progression, pressure
  flattening, self-priming, greedy redundancy

### Q4 (end-to-end bridge)
- Next milestone: Kai micrograph patch test
- Hand-authored scaffold (6-8 nodes) + generated kitchen patch (4 nodes)
- Same scheduler, seeds, conductor
- Failure criterion: 4 distinct keepers from ~16 candidates

### Diversity depends on
- Varying retrieval across sequences (not same top-2 every time)
- Functional duplicate detection (operator + practice + option_effect +
  situation, not just text similarity)
- Richer graph projection (multiple pressure_tags when justified)
- More situation territory in the fixture
- Highest-leverage near-term fix: duplicate-function detection in admission

---

## 17. Acting Craft Insight

"Play the objective under pressure" (practical Stanislavski/Meisner)
is the right design target, not method acting. The middle layer
(CausalSlice, AppraisalFrame, PracticeContext) IS the "given
circumstances." The LLM generates action from inside those
circumstances. The audience reads emotional state from behavior.
Quality test: "action under pressure" vs "emotional performance."
Doc 34 expanded with this insight plus the daemon bridge.

---

## 18. Additional Artifacts

- Explainer: explainers/explainer-conducted-daydreaming.md
- Landscape map: explainers/landscape-map-2026-03-15.md
- Operator taxonomy: doc 35
- Review checklist: doc 31
- Broader application surface expanded: doc 34
- Acting feedback in memory
- Symbiotic-Vault changes reviewed

---

## Current State

### What's built and validated
- L3 traversal scheduler (Graffito + City Routes)
- Generation pipeline with four mechanisms:
  1. Concern inference from primitives (shadow-mode validated, switch added)
  2. Multi-step accumulation with writeback (Gemini validated)
  3. Candidate compilation with diversity scoring (Gemini validated)
  4. Boundary detection (trace-only)
- Four benchmark fixtures: Kai, Maren, Rhea, Tessa
- Quartet validated on derived path (no calibration shortcuts)
- Batch generation with hardened admission (Kai batch: 25→8)
- Kai batch curation packet proving shortlist review is now possible
- Graph compliance via structured output (Pydantic)
- 10 unit tests covering correctness semantics

### Codex queue
1. Rerun Tessa single-sequence after the latest hardening
2. Sync Tessa worked-trace calibration refs if the rerun holds
3. Tessa batch
4. Tessa curation packet
5. Admission patch (overdetermination_gain + duplicate-function detection)
6. Kai micrograph patch test (the real end-to-end milestone)

### What's deferred
- L2 kernel refactor
- L1 critic / repair (stage 2)
- ATMS assumption management
- Full social practice machinery
- Embedding-based retrieval
- Aesthetic evaluation
- Three-layer compiler (abduction → POCL → soft-constraint)
- Conductor physical prototype
- Visual rendering through Scope
- Music (Lyria)

---

## Opus Session: Late 2026-03-15 (continued from above)

### What was discussed

#### 5 Pro Responses Q1/Q3/Q4 Integration

All three replies read and integrated:

**Q1 (Batch Admission):** Two-stage reduction (per-step compile →
pooled admission). Batch unit = one character/one pressure regime.
Key steals: pooled overdetermination_gain, card-based curation
packet (shortlist first, thin rejected appendix).

**Q3 (Generation Quality Review):** Read actual Kai/Rhea output.
3 of 6 nodes keepable. Bottleneck is diversity, not sentence
quality. Operator-template collapse is the main risk ("three
kitchen-avoidance beats"). Graph-valid is not graph-useful (prose
carries mixed pressure but graph projection collapses to dominant
concern). Five scaling risks: operator-template collapse, false
confidence from traces, pressure flattening at seam, self-priming,
greedy admission admitting same-good-thing repeatedly.

**Q4 (End-to-End Bridge):** Patch test, not whole-world bakeoff.
Kai micrograph (10-12 nodes), hand-authored scaffold (6-8 nodes),
4 generated-curated nodes replacing one region. Compare H vs G
with same scheduler/seeds/conductor. Failure diagnosed at three
points: generation, admission, traversal.

#### Delta in worldview from Q1-Q4

Three shifts:
1. Problem is diversity at batch scale, not generation quality
2. Graph-valid is not graph-useful (seam thinning)
3. End-to-end proof should be a patch test, not whole-graph generation

#### Obstacles and Prospects Assessment

Five obstacles assessed:
1. **Diversity at batch scale** — good prospects, solvable with
   batch scoring + diversity penalty. No new architecture needed.
2. **Graph seam richness** — good prospects, mostly prompt
   engineering (generate-then-project pipeline).
3. **Patch test** — very good prospects contingent on diversity fix.
4. **Scene-state progression** — moderate, requires real
   implementation (active-situation carry-forward).
5. **Self-priming** — manageable with retrieval-source weighting.

#### Critical Correction from Other Opus

**Reappraisal stationarity is real.** The salience reappraisal
adds +0.10 to base_intensity, but base_intensity feeds the
operator-AGNOSTIC pressure term (0.35 weight, identical for all
operators). So intensifying the concern doesn't change the operator
winner. The differentiating terms (appraisal_fit, practice_fit,
episodic_resonance) don't change through reappraisal.

Multi-step sequences are structurally stationary in operator
selection unless an external event (option_effect change, situation
change) perturbs the appraisal inputs.

Fixes needed: operator rotation across batch sequences (cheapest),
operator fatigue penalty, or operator-specific reappraisal effects.

**Practice-fit is a third stationarity anchor.** practice_fit is
a static lookup table indexed on practice_type (which doesn't
change within a sequence). For Kai/evasion, practice_fit +
episodic_resonance together create a 0.182 gap favoring avoidance
— larger than any plausible fatigue penalty.

#### 5 Pro Question Bank Round 2 (Q5-Q14)

Expanded question bank drafted and committed:
- Q5: Operator diversity (stationarity fix)
- Q6: Graph seam richness (generate-then-project + POCL-lite)
- Q7: Scene-state progression (situation carry-forward)
- Q8: Retrieval self-priming (source balancing)
- Q9: Intervention ordering for Tessa milestone
- Q10: Evaluation infrastructure (semantic checks too noisy)
- Q11: Prompt architecture (JSON dump vs given circumstances)
- Q12: Practice-context stationarity (third anchor)
- Q13: Retrieval-source balancing vs batch-time exploration
- Q14: Patch-test bridge design

#### Deep Research Further (docs 03, 04)

**Doc 03 (Three-Layer Compiler Stack):**
Three layers, not competing approaches:
1. Weighted abduction (interpret: primitives → latent readings)
2. Partial-order causal-link planning (scaffold: earnedness)
3. Soft-constraint optimization (compile: which candidates together)
+ ATMS-style assumption memory as glue

Ranked by marginal value:
1. Soft-constraint optimization 87%
2. Weighted abduction 85%
3. Partial-order planning 78%
4. Assumption-set memory 75%

**Doc 04 (Broader Application Surface, Grounded):**
Four jobs, different theories for each:
- Seed inner life: Freud, Jung, narrative identity, IFS
- Update causally: attachment, appraisal, predictive processing
- Segment/store: event segmentation (Zacks)
- Realize in language: cognitive grammar, conceptual blending

Five highest-yield non-Mueller imports:
1. Attachment/control-system theory
2. Self-discrepancy + reappraisal
3. Event segmentation
4. Interpersonal circumplex
5. Conceptual blending

**Daemon vision confirmed:** Mueller-shaped inner life generalizes
to writing companions, research daemons, vault agents. "Memory
without cognition" is the current persistent-agent paradigm gap.

#### Dashboard Spec (r5-dashboard)

Two mode-specific surfaces, not one dashboard:
- **Performance dashboard** (85% current state, 15% stable residue)
  - Live band: node, intent, tension/energy, event state
  - Context band: recent path, revisit markers, situation strip
  - Inspector (collapsed): why this step, debug drill-down
- **Authoring membrane** (provenance + curation actions)
  - Candidate cards: surface + graph projection + provenance
  - Four actions: freeze / dismiss / respond / cut
- **Narration companion:** packet-first, provenance through
  derived hints only, raw sidecars never

Pruning rule: "If it changed the felt sequence, surface it. If it
only helped the scheduler arrive at the choice, keep it hidden."

#### Acting Insight: Play the Objective Under Pressure

From David's Santa Monica College acting training: method acting
is the wrong bar. "Play the objective under pressure" is the right
bar. LLMs can do that — give them the given circumstances deeply
enough that the right behavior emerges without instructing it.

The middle-layer objects map directly:
- CausalSlice = what's threatened and by whom
- AppraisalFrame = how bad, controllable, likely
- PracticeContext = what social moves are available
- Operator = mode of trying

"Play the objective under pressure" became the aesthetic criterion:
does the generated moment read as action under pressure, or as
emotional performance?

#### Autoresearch Pattern (from Karpathy)

Key steals for the experiment operating model:
1. Frozen evaluation harness
2. Single mutable surface per sweep
3. Run ledger (results.jsonl)
4. Simplicity bias ("removal is better than addition")
5. Accept/discard discipline

What NOT to steal: single-number optimization, fully autonomous
looping (eval harness not trustworthy enough yet), blind accept/
revert on structural metrics alone.

Simplicity criterion added to generation review checklist (item 8).
Experiment operating model doc created (reading-list/15).

#### Landscape Map

Documented in `explainers/landscape-map-2026-03-15.md`:
- Current plateau: working generation prototype, scaling to batches
- Near peaks: watched experience, curation workflow, narration
  from provenance
- Middle distance: connecting two lanes end-to-end, conductor,
  visual rendering
- Far mountains: three-layer compiler, L2 kernel refactor, music
- Other continent: persistent creative daemon (doc 34)

### What was committed

- 5 Pro question bank round 2 (Q5-Q14)
- Experiment operating model doc
- Simplicity criterion in review checklist
- Landscape map
- Trace viewer HTML
- Reappraisal targeting fix (salience commits)
- Multiple pending items marked DONE
- Memory files updated

### What's still open from this session

- Q5-Q14 ready to send to 5 Pro (question bank committed)
- Codex working on: Tessa batch, retrieval source balancing,
  operator rotation evaluation
- Reappraisal stationarity identified but not yet fixed in code
  (operator fatigue / rotation needed)
- Practice-fit stationarity identified but not yet addressed
- Dashboard spec exists but nothing built yet
- "Given circumstances" prompt format (Q11) not yet tested
- Kai micrograph patch test is the next real end-to-end milestone
