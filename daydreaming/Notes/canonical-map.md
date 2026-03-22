# Canonical Map

The documents that matter right now, plus where everything else
lives. Read this when starting a session or switching tracks.

Last updated: 2026-03-22

---

## Control Plane (start here)

| Doc | What it does |
|-----|-------------|
| [dashboard.md](dashboard.md) | Project status, architecture overview, parallel tracks |
| [current-sprint.md](current-sprint.md) | Current objective, gates, immediate tasks |
| [executor / verifier protocol](executor-verifier-protocol.md) | Milestone-based autonomy with independent verification |
| this file | Where to find everything |

---

## Kernel Architecture (the center of gravity)

| Doc | What it covers |
|-----|---------------|
| [architectural-framing](Book/daydreaming-in-humans-and-machines/condensation/architectural-framing.md) | The hybrid architecture: LLM judgment at typed interfaces inside Clojure-owned structural loops |
| [build-order-checkpoint](Book/daydreaming-in-humans-and-machines/condensation/build-order-checkpoint-2026-03-22.md) | Current build order settled by 5 Pro/5 Thinking reviews: memory ecology → executor boundary → evaluator pilot → verified paths → generic dispatch |
| [rule-engine-trajectory](Book/daydreaming-in-humans-and-machines/condensation/rule-engine-trajectory.md) | Full arc from substrate to creative engine — what exists, what's next, the critical threshold |
| [kernel-rule-schema](Book/daydreaming-in-humans-and-machines/condensation/kernel-rule-schema-and-execution-model.md) | RuleV1 schema, connection graph, execution contract, worked examples |
| [mueller-to-kernel-mapping](Book/daydreaming-in-humans-and-machines/condensation/mueller-to-kernel-mapping.md) | What Mueller had, what we collapsed, what we've restored, what remains |
| [extension-consolidation](Book/daydreaming-in-humans-and-machines/condensation/extension-consolidation.md) | Memory ecology: admission tiers, cue zones, consolidation discipline |

---

## Mueller Condensation (reference)

| Doc | What it covers |
|-----|---------------|
| [condensation spec](Book/daydreaming-in-humans-and-machines/mueller-mechanism-condensation-spec.md) | 10-field format, 19-mechanism inventory, integration patterns |
| [cross-cut summary](Book/daydreaming-in-humans-and-machines/condensation/cross-cut-summary.md) | One-page table: all 19 mechanisms with kernel status, hybrid cut, property to preserve, evaluation ladder |
| [chain-trace-a](Book/daydreaming-in-humans-and-machines/condensation/chain-trace-a.md) | Goal-directed planning/analogy chain with judgment annotations |
| [chain-trace-b](Book/daydreaming-in-humans-and-machines/condensation/chain-trace-b.md) | Accident-driven reminding/serendipity chain with judgment annotations |
| [rule-connection-graph](Book/daydreaming-in-humans-and-machines/condensation/rule-connection-graph.md) | Shared substrate under planning, serendipity, mutation |
| [mechanisms/](Book/daydreaming-in-humans-and-machines/condensation/mechanisms/) | 19 individual mechanism cards (1-2 pages each) |

---

## Research (recent)

| Doc | What it covers |
|-----|---------------|
| [research replies](DeepResearch/2026-03-22/replies/) | Code reviews 03-11: groove risks, executor seam, verified paths, promotion/accessibility |
| [feedback comparison](runtime-thought-feedback-comparison.md) | Gate 2 result: writeback diverges traces (Haiku, Sonnet, hybrid) |
| [GradMem ref](DeepResearch/2026-03-21/refs/GradMem.md) | Gradient-based memory writes — relevance to residue optimization |
| [5 Pro prompts](Book/daydreaming-in-humans-and-machines/condensation/5pro-visioning-prompt-v2.md) | Visioning prompt (novelty, serendipity, provenance, emotion, accumulation) |

---

## Earlier Architecture (still valid, less central)

| Doc | What it covers |
|-----|---------------|
| [settled-architecture](DeepResearch/2026-03-13/pressure-engine-reframe/11-settled-architecture.md) | L1/L2/L3 three-layer architecture |
| [graph-interface-contract](DeepResearch/2026-03-13/pressure-engine-reframe/21-graph-interface-contract.md) | Thin graph seam (distinct from kernel-internal connection graph) |
| [broader application surface](DeepResearch/2026-03-13/pressure-engine-reframe/34-broader-application-surface.md) | Mueller-shaped cognitive infrastructure beyond performance |

---

## Operating Discipline

| Doc | What it covers |
|-----|---------------|
| [operating model](DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/15-experiment-operating-model.md) | Frozen harness, single mutable surface, run ledger |
| [lesson: milestone not mechanism](DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/16-lesson-milestone-not-mechanism.md) | Check cheap levers first, exit conditions |

---

## Kernel Code

| File | What it does |
|------|-------------|
| `kernel/src/daydreamer/rules.clj` | RuleV1 validation, matcher, unifier, connection graph, bridge paths, intervention delta |
| `kernel/src/daydreamer/episodic_memory.clj` | Episodes, threshold retrieval, reminding cascade, cue zones, admission tiers |
| `kernel/src/daydreamer/control.clj` | Mueller control loop: decay, goal selection, mode oscillation |
| `kernel/src/daydreamer/context.clj` | Immutable context tree: sprouting, backtracking, pseudo-sprouts |
| `kernel/src/daydreamer/goal_families.clj` | Family activation, plan bodies (being migrated to rule engine) |
| `kernel/src/daydreamer/goal_family_rules.clj` | RuleV1 definitions for family trigger/activation/planning rules |
| `kernel/src/daydreamer/runtime_thought.clj` | LLM thought beat generation + episodic writeback |
| `kernel/src/daydreamer/family_evaluator.clj` | Post-plan episode evaluation (retention class, keep decision) |
| `kernel/src/daydreamer/conductor.clj` | General session orchestration (extracted from Puppet Knows) |
| `kernel/src/daydreamer/director.clj` | Gemini-based conducted-daydreaming feedback |
| `kernel/src/daydreamer/fact_query.clj` | Shared fact predicates |
| `kernel/src/daydreamer/benchmarks/puppet_knows_autonomous.clj` | Primary benchmark: autonomous Puppet Knows with writeback |

---

## Mueller Source

| File | What it covers |
|------|---------------|
| `daydreamer/dd_epis.cl` | Episode storage, retrieval, reminding, hidden? flag, cue roles |
| `daydreamer/dd_ri.cl` | Serendipity, rule intersection, accessibility frontier |
| `daydreamer/dd_cntrl.cl` | Control loop, emotion-driven selection |
| `daydreamer/dd_rule1.cl` / `dd_rule2.cl` | Rule application, planning, analogical planning, ordering |
| `daydreamer/dd_mutation.cl` | Action mutation |
| `daydreamer/dd_kb.cl` | Knowledge base (rules, goals, representations) |
| `daydreamer/gate_cx.cl` | Context tree |

---

## Generation Pipeline (earlier phase, not active focus)

| File | What it does |
|------|-------------|
| `daydreaming/authoring_time_generation_prototype.py` | The generation pipeline (~3000 lines) |
| `fixtures/authoring_time_generation_*.yaml` | Benchmark fixtures (Kai, Tessa, Maren, Rhea) |

---

## Where everything else lives

| Directory | What's in it |
|-----------|-------------|
| `DeepResearch/2026-03-22/` | Current review cycle (code reviews, prompts, replies) |
| `DeepResearch/2026-03-21/` | Condensation work, GradMem, project sift |
| `DeepResearch/2026-03-13/` | Earlier architecture cycle (pressure-engine-reframe, reading list) |
| `Book/daydreaming-in-humans-and-machines/` | Mueller book: 15 OCR'd chapters + image-reviewed supplements + condensation |
| `experiential-design/` | Design narrative (experience, briefs, rendering, node schema) |
| `knowledge-coupling/` | John collaboration (queries, briefings, research package) |
| `reference-repos/` | External repos (Ori-Mnemos) |
| `team-repos/` | Collaborator repos |
| `out/` | Run artifacts (traces, cognitive viz, feedback comparisons) |
