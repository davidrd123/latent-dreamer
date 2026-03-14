# Reading List: Prior Work for the Pressure Engine

Source: `first-round/08-prior-work-questions.md` (GPT-5 Pro response)

This is not a prestige list. Every item is here because it directly
informs something we're building.

---

## Must-Read Now

### 1. Facade / Beat Sequencing — Mateas & Stern

**Grounds:** L3 traversal scheduler (drama management)
**What to steal:** Candidate-node preconditions, priority tiers,
weighted selection inside tiers, tension-arc scoring, "home back
toward unresolved line X"
**PDF:** https://users.soe.ucsc.edu/~michaelm/publications/mateas-gdc2003.pdf

### 2. Declarative Optimization-Based Drama Management (DODM) — Nelson et al.

**Grounds:** L3 traversal features and authorial goals
**What to steal:** Plot homing/mixing, authorial goal features,
scheduler evaluation knobs, explicit traversal policy
**PDF:** https://eis.ucsc.edu/papers/Nelson_etal_-_DODM_-_IEEE-CGA06.pdf

### 3. Evaluating Authorial Leverage — Chen, Nelson, Mateas

**Grounds:** L3 evaluation ("is this controller worth the trouble?")
**What to steal:** Policy complexity vs trigger logic, leverage as
evaluation frame, branching burden metrics
**PDF:** https://cdn.aaai.org/ojs/12377/12377-52-15905-1-2-20201228.pdf

### 4. EMA (Emotion and Adaptation) — Marsella & Gratch

**Grounds:** L2 character concern dynamics (appraisal)
**What to steal:** Continuous appraisal loop (not one-shot labeling),
coping/readiness fields, event→concern intensity updates
**PDF:** https://www.stacymarsella.org/publications/pdf/N_Emcsr_Marsella.pdf

### 5. OCC Model — Ortony, Clore, Collins

**Grounds:** L2 concern type taxonomy
**What to steal:** Compact eliciting-condition schema, intensity
variables, event-based appraisal vocabulary (harm, threat, blame,
relief, obligation)
**PDF:** https://people.idsia.ch/~steunebrink/Publications/KI09_OCC_revisited.pdf
(This is an OCC-revisited paper; the original is the 1988 book
"The Cognitive Structure of Emotions")

### 6. Versu — Evans & Short

**Grounds:** L2 socially situated character interactions
**What to steal:** Social practices as typed recurring situations
(confrontation, avoidance, confession), affordance menus conditioned
on role/norm, autonomy preserved at action selection
**PDF:** https://cs.uky.edu/~sgware/reading/papers/evans2014versu.pdf

### 7. Sentient Sketchbook — Liapis et al.

**Grounds:** L1 mixed-initiative world-building workflow
**What to steal:** Proposal browsing with visible metrics, novelty-
first early ideation, metric-guided refinement later, "this
suggestion is different because..." explanations
**PDF:** https://yannakakis.net/wp-content/uploads/2013/08/ALiapis_SentientSketchbook.pdf

### 8. Tanagra — Smith, Whitehead, Mateas

**Grounds:** L1 constraint-backed generation with human anchors
**What to steal:** Human anchors + constrained completion,
validator-first proposal admission, "no valid completion exists"
as legitimate output, fail-loud invariants
**PDF:** https://dl.acm.org/doi/pdf/10.1145/1822348.1822376
**Alt:** https://www.researchgate.net/publication/228339816_Tanagra_A_mixed-initiative_level_design_tool

---

## Useful After That

### 9. Comme il Faut / Prom Week — McCoy et al.

**Grounds:** L2 reusable social-state mechanics
**What to steal:** Social-state variables, reusable interaction
schemas, composable social rules, authoring burden reduction
**PDF:** https://cdn.aaai.org/ojs/12454/12454-52-15982-1-2-20201228.pdf

### 10. MINSTREL — Turner

**Grounds:** L1 memory-based creativity / transform-recall-adapt
**What to steal:** When stuck, retrieve nearby case + change one
structural thing + test result. Good model for dead-end-aware memory.
**PDF:** https://cdn.aaai.org/Symposia/Spring/1993/SS-93-01/SS93-01-021.pdf

### 11. Suspenser — Cheong & Young

**Grounds:** L3 audience-facing tension/suspense heuristic
**What to steal:** Suspense scoring based on protagonist's perceived
solution space, scoring story states against desired suspense levels
**PDF:** https://cdn.aaai.org/AAAI/2006/AAAI06-331.pdf

### 12. Musical Tension Model — Farbood

**Grounds:** Music layer, pacing, conducted performance
**What to steal:** Temporal tension features, rate-of-change terms,
pacing/music coupling
**PDF:** https://bpb-us-e1.wpmucdn.com/wp.nyu.edu/dist/f/11865/files/2020/08/Farbood_2012_Musical_Tension.pdf

### 13. ATMS (Assumption-Based Truth Maintenance) — de Kleer

**Grounds:** L2 representation for hypothetical branches / contexts
**What to steal:** Explicit assumption tokens, nogood tracking,
support sets, cheap context switching across alternatives
**PDF:** https://www.researchgate.net/publication/220546361_An_assumption-based_TMS

### 14. Event Sourcing — Fowler

**Grounds:** Canon/session mutation history
**What to steal:** Append-only canonical event log, replay,
snapshots, provenance of how a world got here
**URL:** https://martinfowler.com/eaaDev/EventSourcing.html

### 15. Wordcraft — Yuan et al.

**Grounds:** Co-creative authoring UI evidence
**What to steal:** Structured controls beat raw chat for creative
assistance; measure acceptance and usefulness
**PDF:** https://3dvar.com/Yuan2022Wordcraft.pdf

---

## From the Source: Mueller's Own Book

These are specific sections of Mueller's original book that are
directly relevant to mechanisms the kernel hasn't fully implemented.
More detailed than any external reading and closer to the project's
actual lineage.

### M1. Serendipity Mechanisms — Mueller Ch. 5, §5.3

**Grounds:** L2 serendipity (currently a scalar bias in the kernel)
**What to steal:** Four types of serendipity, not one scalar:
- Episode-driven (§5.3.3): retrieved episode for one concern
  applies to another
- Object-driven (§5.3.4): encountered object has unexpected
  relevance to another concern
- Input-state-driven (§5.3.5): external input useful for active
  concern
- Rule intersection (§5.3.2): graph search through planning rules
  from one concern to another — this is the mechanism that produces
  non-obvious connections

The kernel's `serendipity-bias: 0.0-0.35` is a placeholder for
rule-intersection search. The ATMS recommendation (#13) supports
the cheap context switching this search needs.

Also relevant for L1: the `couple` operator's search for connections
between disconnected world elements is essentially Mueller's
serendipity applied to world structure.

**Location:** `Notes/Book/daydreaming-in-humans-and-machines/05-everyday-creativity-in-daydreaming.md`

### M2. Overdetermination and DAYDREAMER* — Mueller Ch. 11, §11.3

**Grounds:** Multi-concern resolution, L3 scoring, architecture
**What to steal:** The argument that the stream of consciousness
results from merged products of many concurrent processes — not
serial switching between concerns. A daydream is "good" precisely
when it serves multiple concerns simultaneously.

Implications:
- Overdetermination should be a first-class scoring criterion at
  L3, not just a bonus. The best traversal decision serves the most
  diagnostics simultaneously.
- The L1 evaluation formula's `cross_pressure_gain` should be more
  central than a bonus term.
- DAYDREAMER* proposes parallel concern processing with merging at
  intersection points — conceptually close to what the pressure
  engine's post-hoc serendipity scan does, but as the default mode
  rather than an afterthought.

**Location:** `Notes/Book/daydreaming-in-humans-and-machines/11-future-work-and-conclusions.md`

### M3. Art and Music — Mueller Ch. 11, §11.1.5

**Grounds:** The project's artistic ambition, authorship questions
**What to steal:** Mueller directly anticipated this project:
"DAYDREAMER could provide initial ideas for stories, animations,
or live-action films, and once begun, suggest continuations,
modifications, or further possibilities." The artist-as-curator
model, the program as art object, the tension between distribution
and homogeneity, whether understanding the mechanism reduces
apparent creativity.

Connects to the narrative-conducting doc's authorship questions
and the conducted performance dimension.

**Location:** `Notes/Book/daydreaming-in-humans-and-machines/11-future-work-and-conclusions.md`

### M4. Action Mutation — Mueller Ch. 5, §5.4

**Grounds:** L1 operators, dead-end recovery, variation generation
**What to steal:** When a planning branch exhausts its options,
mutation of action objectives produces novel alternatives. Mutation
strategies include substitution (different action type for same
goal), permutation (reorder subgoals), and relaxation (weaken
constraints). The kernel has 0% mutation implementation.

Relevant for L1's `obstruct` and `consequence` operators when
initial proposals are rejected — mutate the proposal rather than
generate from scratch.

**Location:** `Notes/Book/daydreaming-in-humans-and-machines/05-everyday-creativity-in-daydreaming.md`

### M5. Kernel Procedures, Contexts, and Reminding — Mueller Ch. 7

**Grounds:** L2 kernel refactor, shared retrieval, ATMS seam
**What to steal:** The actual procedural shape of DAYDREAMER:
- contexts are for backtracking and trace retention, not only
  hypothetical branching
- concern initiation is rule-driven (`theme` rules), with
  motivating emotions attached at creation time
- personal-goal concerns and daydreaming-goal concerns modify
  reality differently
- retrieval uses intersection-style marking
- reminding reactivates emotions, recursively expands recent
  indices, and then invokes serendipity recognition
- Mueller explicitly notes the weakness of pure contexts for
  reasoning across multiple worlds, which strengthens the later
  ATMS direction

**Location:** `Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md`

### M6. English Generation / Narration Layer — Mueller Appendix B

**Grounds:** Inner-life dashboard, narration companion, trace
legibility
**What to steal:** DAYDREAMER already had a structured narration
layer:
- template-based realization over internal concepts
- belief-path-sensitive narration
- different language for hypothetical past, relaxed alternatives,
  and surprise
- explicit generation patterns for goals, emotions, attitudes,
  beliefs, and remindings
- pruning rules so not every internal assertion becomes narration
- paragraph breaks on backtracking and concern shifts

This is directly relevant to the claim that the dashboard is a real
output surface, not just a debug view.

**Location:** `Notes/Book/daydreaming-in-humans-and-machines/14-appendix-b-english-generation-for-daydreaming.md`

---

## Read Later (only if scope changes)

- **IPOCL / Fabulist** (Riedl & Young) — full narrative planning.
  Only if you later generate causal plot structures automatically.
  https://faculty.cc.gatech.edu/~riedl/pubs/jair.pdf

- **CoAuthor** (Lee et al.) — instrumentation pattern for co-writing.
  Good logging model, less useful as theory.
  https://www-cs.stanford.edu/~minalee/pdf/chi2022-coauthor.pdf

- **George Lewis, Voyager** — autonomous co-performer.
  Right abstraction for conductor/engine relationship.
  https://eamusic.dartmouth.edu/~larry/algoCompClass/readings/george%20lewis/lewis.too_many_notes.pdf

- **ERaCA / Expressive Range** (Kreminski) — quality-diversity
  evaluation for generated content.
  https://mkremins.github.io/publications/ERaCA_HAI-GEN2022.pdf

---

## What to Ignore

- **Generic BDI** — useful nouns, wrong center of gravity
- **Story grammars / beat sheets** — post-hoc description, not
  operational machinery
- **GraphRAG / KG tooling** — corpus mining, not runtime dynamics
- **Ontology-heavy narratology** — interesting later, not now
- **End-to-end LLM story generation** — wrong move entirely

---

## Status Tracking

| # | Paper | Read? | Notes file? |
|---|-------|-------|-------------|
| 1 | Facade | yes | `01-facade-extraction.md` |
| 2 | DODM | yes | `02-dodm-extraction.md` |
| 3 | Authorial Leverage | yes | `03-authorial-leverage-extraction.md` |
| 4 | EMA | yes | `05-ema-extraction.md` |
| 5 | OCC | yes | `06-occ-extraction.md` |
| 6 | Versu | yes | `07-versu-extraction.md` |
| 7 | Sentient Sketchbook | yes | `08-sentient-sketchbook-extraction.md` |
| 8 | Tanagra | — | (covered by Sentient Sketchbook; same pattern) |
| 9 | CiF / Prom Week | — | (deferred; social rules, L2 second pass) |
| 10 | MINSTREL | yes | `09-minstrel-extraction.md` |
| 11 | Suspenser | yes | `04-suspenser-extraction.md` |
| 12 | Farbood tension | — | (deferred; music layer, not L3 scheduler) |
| 13 | ATMS | yes | `10-atms-extraction.md` |
| 14 | Event Sourcing | — | (pattern known; no extraction needed) |
| 15 | Wordcraft | — | (deferred; co-creative UI, not architecture) |
| M1 | Mueller §5.3 Serendipity | yes | (book section read directly) |
| M2 | Mueller §11.3 Overdetermination | yes | (book section read directly) |
| M3 | Mueller §11.1.5 Art and Music | yes | (book section read directly) |
| M4 | Mueller §5.4 Action Mutation | yes | (book section read directly) |
| M5 | Mueller Ch. 7 Kernel Procedures | yes | `11-mueller-ch7-extraction.md` |
| M6 | Mueller Appendix B Generation | yes | `12-mueller-appendix-b-extraction.md` |
