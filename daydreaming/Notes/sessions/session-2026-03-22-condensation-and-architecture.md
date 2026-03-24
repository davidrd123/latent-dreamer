# Session Log: 2026-03-22 — Condensation, Architecture, and Memory Ecology

Duration: extended session (many hours)
Agent: Claude Opus 4.6 (1M context)
Context used: ~631k/1000k tokens

## What we accomplished

This was a foundational architecture session. We went from "the book
has been condensed into mechanism cards" to "the build order is settled,
the memory ecology has a first pass, the executor boundary is designed,
the Mueller source code has been verified, and eight beyond-Mueller
directions have been researched."

### Phase 1: Condensation review and spec refinement

- Reviewed the condensation spec and all 19 mechanism cards
- Added integration patterns vocabulary (co-routine judgment,
  rule-with-LLM-consequent, LLM-as-evaluator, LLM-as-content-generator)
- Added fixed 19-mechanism inventory to the spec
- Added source hierarchy with divergence rule (book primary, code verification)
- Removed sync/async from chain traces (that's modern runtime design, not Mueller-faithful)
- Made interface shape required (none / unclear / tentative schema)
- Reviewed the pilot batch (8 mechanisms) in detail
- Codex completed the remaining 11 mechanisms
- Second-pass review: annotated chain traces with judgment points,
  tightened mechanism 16, analyzed goal_families.clj for migration

### Phase 2: Architecture framing

- Wrote architectural-framing.md (our point of view: content vs.
  accumulation, recursion ownership, static graph, four structural
  capabilities)
- Wrote the rule schema with three-layer split (schema / denotation /
  executor) — RuleV1, EdgeBasisV1, RuleCallV1, RuleResultV1
- Wrote two worked examples (Kai's letter, hidden blessing)
- Wrote the recursion ownership model (Clojure owns stack, LLM evaluates
  at leaf nodes)
- Discussion with web opus and codex refined the framing: "typed LLM
  judgment inside persistent graph-structured cognition"

### Phase 3: Research prompts and replies

- Wrote 5 Pro visioning prompt (6 questions: novelty, serendipity,
  provenance, emotion, accumulation, what kind of thing)
- Wrote 5 Pro architecture prompt (8 questions: schema/executor split,
  static graph, recursion, integration patterns, what's missing,
  comparison to SOAR/ACT-R/CoALA)
- Wrote 5 Pro implementation prompt (7 questions: pattern language,
  graph construction, kernel integration, executor dispatch, code-is-data,
  build order)
- Received and sifted 4 initial replies — key findings:
  - Denotational contract layer (schema / denotation / executor)
  - ACT-R buffer discipline (narrow interfaces, status signals)
  - Consolidation is mandatory ("accumulation without consolidation is sludge")
  - Copycat/descriptor-rigidity as open question on serendipity
  - Write-back transfer as falsification criterion
  - "Cumulative structured search with explicit affective control" as framing

### Phase 4: Writeback experiment

- Reviewed runtime_thought.clj and puppet_knows_autonomous.clj
- Traced the full writeback flow: packet → LLM call → normalize →
  create episode → store under indices → update FIFO → next cycle diverges
- Gate 2 passed: Haiku/Sonnet/Hybrid all diverge from baseline at cycles 4-12
- Different models produce different downstream node selections
- Applied GradMem insight: residue prompt now asks for what's NEW/UNRESOLVED,
  not a general summary

### Phase 5: Rule engine and deep build

- Reviewed codex 1's deep-build work: fact_query.clj, rules.clj (matcher,
  unifier, connection graph), goal_families.clj migration
- Reviewed codex 2's sprint work: runtime_thought.clj, writeback integration
- Added code comments throughout rules.clj, fact_query.clj, runtime_thought.clj
- Wrote rule-engine-trajectory.md (full arc from substrate to creative engine)
- Wrote goal-families-migration-analysis.md (three layers, four-phase extraction)
- Codex landed graphable trigger/activation/planning rule pairs
- Connection graph has real edges for the first time

### Phase 6: Code reviews and build order

- Received and synthesized 5 code reviews (03-08 from 5 Pro and 5 Thinking):
  - Episode loop risks / groove formation
  - Executor seam design (declarative effects)
  - Verified path lattice (candidate → bound → supported → constructed → sound)
  - Double-counting bug, FIFO crowding, index zone separation
  - Symbolic refs for id allocation, consequent-schema vs effect-schema
- Mueller source verification: confirmed 6 gates Mueller had that we collapsed
  (hidden?, needed-for-plan?/needed-for-reminding?, misc indices, two thresholds,
  accessible?, realism/desirability)
- Build order settled: memory ecology → executor boundary → LLM evaluator →
  verified paths → generic dispatch
- Wrote build-order-checkpoint-2026-03-22.md
- Updated extension-consolidation.md with settled and remaining items
- Wrote mueller-to-kernel-mapping.md

### Phase 7: Beyond-Mueller research

- Wrote beyond-mueller-detailed.md (5 current extensions + 8 future directions)
- Wrote position memo (trajectory.md — codex draft)
- Sent 8 beyond-Mueller research questions to 5 Pro
- Received and sifted 4 beyond-Mueller replies (Q1-3, Q4-6, Q7-8, all-8):
  - Ephemeral descriptor-slippage workspace (Copycat import)
  - Evaluator rollout order (pointwise default, funnel shape)
  - Eight-step induced rule admission ladder
  - Directed daydreaming: constrain question not surprise, thin/thick split
  - DSPy write optimization: 4 metric families, park until membrane solid
  - Developmental trajectory: style = history-caused bias beating seed drift
  - Accessibility frontier: genuinely novel contribution (not literature rename)
  - Multi-model routing: monitor internal ambiguity/stakes, not family label
- Wrote research-sift-memory-ecology-and-beyond.md (batch sift with admission
  status per finding)

### Phase 8: Code reviews on current code

- Review 15: 4 bugs in executor boundary (nil propagation, false swallowed,
  afterglow divergence, symbolic ref fail-open) + 3 contract gaps
- Review 16: 6 admission chain leaks (evaluator promotes too early,
  one cross-family success enough, plain failure too weak, quarantine one-way,
  cluster cap fake, loop immunity loophole) + evaluator authority narrowing

### Phase 9: Project housekeeping

- Updated canonical-map.md to kernel-first reality
- Updated dashboard.md to kernel-first reality
- Mapped reading list sources against current approach
- Captured Symbiotic-Vault / Membrane / Compost connections to kernel
- Codex corrected the vault mappings (atoms ≠ episodes, frames ≠ families,
  respond = typed event not world mutation)

## Key architectural decisions settled

1. Hybrid boundary is inside mechanisms, not between them
2. Rule schema: consequent-schema (graphable) / denotation (intended effect) /
   executor (runtime)
3. Connection graph stays structurally derived, not usage-weighted
4. Recursion stays in Clojure; LLM evaluates at leaf nodes
5. Dominant pattern is LLM-as-evaluator (10/19), not content-generator (3/19)
6. Every LLM-backed rule has Mueller-faithful fallback
7. Memory ecology: trace / provisional / durable admission tiers
8. Cue zones: content (retrieval + reminding), provenance (weak tie-break),
   support (metadata only)
9. Evaluator is gate/veto, not sole promotion authority
10. Static graph + adaptive retrieval (not adaptive graph)

## Key open questions

1. Promotion criteria: what downstream evidence promotes provisional → durable?
2. Anti-residue detection logic: how to detect backfire, stale, contradicted?
3. Rule accessibility frontier: how does it interact with admission tiers?
4. Descriptor rigidity: is the vocabulary fluid enough for genuine serendipity?
5. Graph density transition: where does the corridor become soup?
6. Directed daydreaming: thin version implementable now, thick needs infrastructure
7. Write optimization: DSPy pass parked until membrane solid

## Documents produced

| Document | Purpose |
|----------|---------|
| `condensation/architectural-framing.md` | Our point of view |
| `condensation/build-order-checkpoint-2026-03-22.md` | Settled build sequence |
| `condensation/rule-engine-trajectory.md` | Full arc from substrate to engine |
| `condensation/extension-consolidation.md` | Memory ecology discipline (updated) |
| `condensation/goal-families-migration-analysis.md` | Three layers, four phases |
| `condensation/mueller-to-kernel-mapping.md` | What Mueller had, what we collapsed |
| `condensation/kernel-rule-schema-and-execution-model.md` | RuleV1 with worked examples (updated) |
| `condensation/research-sifts/research-sift-memory-ecology-and-beyond.md` | Batch sift of all reviews |
| `condensation/5pro-visioning-prompt-v2.md` | Merged visioning prompt |
| `condensation/5pro-architecture-prompt.md` | Architecture review prompt |
| `condensation/5pro-implementation-prompt.md` | Implementation review prompt |
| `condensation/5pro-prompt-set.md` | Five-prompt set (superseded by v2) |
| `project-page/trajectory.md` | Position memo (codex draft) |
| `project-page/beyond-mueller-detailed.md` | Numbered future directions |
| `project-page/symbiotic-vault-kernel-connections.md` | Vault/kernel mappings |
| `Notes/canonical-map.md` | Updated to kernel-first (was pipeline-first) |
| `Notes/dashboard.md` | Updated to kernel-first (was pipeline-first) |
| `Notes/runtime-thought-feedback-comparison.md` | Gate 2 result |

## Code changes

| File | What changed |
|------|-------------|
| `kernel/src/daydreamer/rules.clj` | Added comments throughout |
| `kernel/src/daydreamer/fact_query.clj` | Added comments + section headers |
| `kernel/src/daydreamer/runtime_thought.clj` | Comments + GradMem residue prompt |
| `condensation/mechanisms/11-episode-retrieval.md` | Typed retrieval outcomes + adaptive retrieval note |
| `condensation/mechanisms/13-serendipity-recognition.md` | Copycat open question + property-to-preserve strengthened |
| `condensation/chain-trace-a.md` | Judgment point annotations + daydreaming-goal branch |
| `condensation/chain-trace-b.md` | Judgment point annotations |
| `condensation/cross-cut-summary.md` | Evaluation ladder (4 levels) |
| `mueller-mechanism-condensation-spec.md` | Integration patterns, inventory, source hierarchy |
| `.gitignore` | Added reference-repos/ |

## Memory updated

- `project_condensation_and_hybrid_architecture.md` — architecture decisions,
  build order, key gaps, Ori relationship, adaptation spec relationship

## Perspective for future sessions

This session was about establishing the intellectual foundation. The
condensation made Mueller's mechanisms legible. The architecture framing
made the hybrid Clojure/LLM boundary precise. The code reviews shaped
the build order. The Mueller source verification showed what we collapsed
and what we've restored.

The system is at a specific point: the rule engine substrate exists
(matcher, graph, trigger/activation/planning rules), the writeback loop
works (gate 2 passed), the memory ecology has a first pass (admission
tiers, cue zones, provenance caps), but the deeper discipline (promotion,
anti-residue, accessibility frontier, executor boundary) is still being
built.

The north star hasn't changed: can yesterday's generated structure change
tomorrow's reachable solutions in a way a rolling summary and vector
retrieval cannot? Every build step is evaluated against that criterion.

The product path is clearer than before: thin directed daydreaming
(brief = situation configuration) + Membrane gestures (freeze/dismiss/
respond/cut) = a conducted daydreaming surface. The thick version waits
for the beyond-Mueller infrastructure.

The genuinely novel contribution is the accessibility frontier — staged
rule admission with evidence-gated opening. Neither Soar nor ACT-R has
this as an explicit framework. The 5 Pro reviews confirmed this is a
real contribution, not a literature rename.

## Late-session additions (after main log)

### Reviews 15 and 16 — code-level bugs

Review 15 found 4 real bugs in the executor boundary code:
1. Reversal nil-result doesn't propagate through reverse-leafs
2. `false` from executor silently swallowed (only `nil` should mean no-result)
3. Rationalization afterglow divergence (executor computes one fact,
   assertion handler rebuilds a different one)
4. `resolve-effect-context-id` fails open (unresolved symbolic refs
   fall back to raw keyword, silently writing junk)

Plus 3 contract gaps: result-key wiring not validated across effect
program, effect ordering implicit/unchecked, reversal branch validation
too weak.

Review 16 found 6 admission chain leaks:
A. Evaluator can durable-promote too early (bypasses use→outcome→evidence chain)
B. One cross-family success is enough to promote (over-promotes in small worlds)
C. Plain failure too weak (gummy middle-tier episodes accumulate)
D. Quarantine appears one-way (no reopening path)
E. Payload-cluster cap weaker than it looks (run-local goal IDs split clusters)
F. Same-family-loop has immunity loophole (one cross-family success immunizes)

Plus: evaluator authority should be narrowed — critic only, not
critic-plus-policy-engine. `:kernel-caps` field should explicitly
limit what the model is allowed to decide.

One-sentence summary from review 16: "Do not let a loosely supervised
model output turn a provisional family-plan episode into durable memory
plus opened rule access without downstream evidence."

These are active fix targets for the codex, not research findings.

### Project page status

The v3 page draft lives in scope-drd, not latent-dreamer:
`/Users/daviddickinson/Projects/Lora/scope-drd/notes/daydream/self_docs/project_page_v3/page_draft.md`

It has absorbed most of the session's work. Codex correctly notes:
- Don't claim a 50-cycle soak result that doesn't exist yet
- Add honest status notes about rule-access not fully gating live
  activation and evaluator still having some negative authority
- Promote the draft into latent-dreamer as canonical source

This is polish work for a fresh session.

### Symbiotic-Vault connections (corrected)

The initial mappings were too literal. Codex corrected:
- Atoms ≠ episodes (atoms are conceptual notes, episodes are cognitive traces)
- Frames ≠ goal families (frames are reader-lenses, families are operators)
- Respond = typed conductor event, NOT raw world mutation
- Compost = diagnostic layer over neglected material, not anti-residue itself

The real connection: THE_MEMBRANE vocabulary (freeze/dismiss/respond/cut)
is the right human-facing control surface for a conducted cognitive engine.
The kernel provides the persistent machinery underneath. The conductor
shapes pressure and surfacing without becoming a freeform world mutator.

### Reading list mapping

The earlier reading list (25 sources + Mueller sections) was built for
the generation-pipeline architecture. Current status against kernel-first:
- EMA, OCC, ATMS, MINSTREL: directly absorbed
- Facade, DODM, Versu: still valid for performance lane, not current focus
- Loyall/ABL, FAtiMA: flagged gap — surface believability, interaction legibility
- Generative Agents: our baseline is now "assertion/event memory with
  consolidation + graph memory + long-context fallback" (citing Mem0)
- Our distinguishing claim is narrower than sometimes stated: not "memory"
  but "cumulative structured search with explicit affective control"

### What to read on restart

If compacted, read in this order:
1. `sessions/session-2026-03-22-condensation-and-architecture.md` (this file)
2. `condensation/build-order-checkpoint-2026-03-22.md` (settled build sequence)
3. `condensation/research-sifts/research-sift-memory-ecology-and-beyond.md` (all research findings)
4. `condensation/architectural-framing.md` (our point of view)
5. `Notes/dashboard.md` (current project status)
6. `Notes/canonical-map.md` (where everything lives)
