# Canonical Map

The 15 documents that matter right now, plus where everything
else lives. Read this when starting a session or switching tracks.

Last updated: 2026-03-15

---

## Control Plane (start here)

| Doc | What it does |
|-----|-------------|
| [dashboard.md](dashboard.md) | Project status, phase map, parallel tracks, milestones |
| [current-sprint.md](current-sprint.md) | Current objective, frozen recipe, immediate tasks, decision gates |
| this file | Where to find everything |

---

## Architecture (read once, reference as needed)

| Doc | What it covers |
|-----|---------------|
| [settled-architecture](DeepResearch/2026-03-13/pressure-engine-reframe/11-settled-architecture.md) | L1/L2/L3 three-layer architecture, settled decisions |
| [graph-interface-contract](DeepResearch/2026-03-13/pressure-engine-reframe/21-graph-interface-contract.md) | Thin graph seam, field split, what goes in the graph vs. sidecar |
| [L2 schema](DeepResearch/2026-03-13/pressure-engine-reframe/28-l2-schema-from-5pro.md) | CausalSlice, AppraisalFrame, PracticeContext, reappraisal rules |
| [generation spec](DeepResearch/2026-03-13/pressure-engine-reframe/30-authoring-time-generation-prototype-spec.md) | Two-arm comparison, five ablations, implementation order |
| [authoring-time reframe](DeepResearch/2026-03-13/pressure-engine-reframe/27-authoring-time-generation-reframe.md) | Why material supply is the bottleneck, L1 as orchestration |

---

## Operating Discipline

| Doc | What it covers |
|-----|---------------|
| [operating model](DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/15-experiment-operating-model.md) | Frozen harness, single mutable surface, run ledger, Pareto discipline |
| [lesson: milestone not mechanism](DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/16-lesson-milestone-not-mechanism.md) | Check cheap levers first, exit conditions, don't over-isolate |

---

## Active Research

| Doc | What it covers |
|-----|---------------|
| [5 Pro question bank (round 2)](DeepResearch/2026-03-13/pressure-engine-reframe/first-round-03/deep-research-03/01-5pro-question-bank.md) | Q5-Q14, all answered, replies in replies/ subfolder |
| [broader application surface](DeepResearch/2026-03-13/pressure-engine-reframe/34-broader-application-surface.md) | Mueller-shaped cognitive infrastructure beyond performance |
| [operator taxonomy](DeepResearch/2026-03-13/pressure-engine-reframe/35-operator-taxonomy-status.md) | Mueller's seven operators, gaps, expansion candidates |

---

## Explainers (for onboarding or context recovery)

| Doc | What it covers |
|-----|---------------|
| [explainer](explainers/explainer-conducted-daydreaming.md) | Full illustrated overview with ASCII diagrams |
| [landscape map](explainers/landscape-map-2026-03-15.md) | Project state map (plateau, near peaks, far mountains) |

---

## Session State

| Doc | What it covers |
|-----|---------------|
| [session log](sessions/session-2026-03-15-generation-pipeline.md) | What happened in the marathon session |
| [pending items](sessions/pending-items-2026-03-15.md) | 29 items organized by when they matter |

---

## Code

| File | What it does |
|------|-------------|
| `daydreaming/authoring_time_generation_prototype.py` | The generation pipeline (~3000 lines) |
| `daydreaming/graffito_pilot.py` | L3 traversal scheduler |
| `daydreaming/city_routes_pilot.py` | L3 three-arm experiment |
| `daydreaming/keeper_bank.py` | Keeper bank for supply pilot |
| `tests/test_authoring_time_generation_prototype.py` | 17 tests for generation pipeline |

---

## Fixtures

| File | What it tests |
|------|-------------|
| `fixtures/authoring_time_generation_kai_letter_v1.yaml` | Avoidance benchmark |
| `fixtures/authoring_time_generation_tessa_toast_rationalization_v1.yaml` | Rationalization benchmark |
| `fixtures/authoring_time_generation_maren_opening_line_v1.yaml` | Rehearsal benchmark |
| `fixtures/authoring_time_generation_rhea_credit_meeting_v1.yaml` | Rehearsal benchmark |
| `fixtures/kai_patch_test_*.yaml` | Kai bridge test (hand + generated) |
| `fixtures/tessa_patch_test_*.yaml` | Tessa bridge test (hand + generated) |

---

## Where everything else lives

These are not daily-use docs. They're reference material
organized by topic. Don't read them unless you need them.

| Directory | What's in it | ~Files |
|-----------|-------------|--------|
| `DeepResearch/2026-03-13/pressure-engine-reframe/01-26` | L3 experiment cycle (Graffito, City Routes, traversal) | 26 |
| `DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/` | Source paper extractions (Facade, DODM, EMA, OCC, Versu, Mueller, etc.) | 16 |
| `DeepResearch/2026-03-13/pressure-engine-reframe/first-round-02/` | 5 Pro round 1 responses (architecture review, failure modes, lineage) | ~20 |
| `DeepResearch/2026-03-13/pressure-engine-reframe/first-round-03/` | 5 Pro round 2+3 (deep research, mechanism design, conductor, dashboard) | ~40 |
| `experiential-design/` | Design narrative (experience, briefs, rendering, node schema) | 23 |
| `knowledge-coupling/` | John collaboration (queries, briefings, research package) | 9 |
| `Book/` | Mueller's book, digitized chapters + page reviews | ~40 |
| `PreviousWork/` | Historical: MMOP, Akira Protocol, Kaiju, John brainstorming | 14 |
| `PreviousIdeation/` | Historical: early conductor/steering concepts | 4 |
| `ProspectiveDesign/` | Historical: early architecture sketches (superseded) | 6 |
| `session-logs/` | Old session logs (superseded by sessions/) | 2 |
| `out/authoring_time_generation/` | 83 generation run bundles | ~500+ |
| `out/kai_patch_test_20260315/` | Kai bridge test output | ~18 |
| `out/tessa_patch_test_20260315/` | Tessa bridge test output | ~18 |
