# Research Sift: Memory Ecology, Executor Boundary, and Beyond-Mueller Directions

Batch: 2026-03-22 review stack (reviews 03-13)

---

## Scope

Prompts and replies covered:

| ID | Topic | Source |
|----|-------|--------|
| 03 | Episode loop risks | `replies/03-episode-loop-risks-5thinking.md` |
| 04 | Executor seam | `replies/04-executor-seam-5thinking.md` |
| 05 | Verified paths | `replies/05-verified-paths-5thinking.md` |
| 06 | Family graph rev | `replies/06-family-graph-rev-5pro.md` |
| 07 | Executor seam | `replies/07-executor-seam-5pro.md` |
| 08 | Verified paths | `replies/08-verified-paths-5pro.md` |
| 09 | Episode loop risks | `replies/09-episode-loop-risks-5pro.md` (if exists) |
| 10 | Executor boundary | `replies/10-executor-boundary-5pro.md` |
| 11a | Promotion (loose) | `replies/11a-promo-loose-5pro.md` |
| 11b | Promotion (prompted) | `replies/11b-promo-prompt-5pro.md` |
| 13a | Beyond Mueller Q1-3 | `replies/13-beyond-1_3.md` |
| 13b | Beyond Mueller Q4-6 | `replies/13-beyond-4_6.md` |
| 13c | Beyond Mueller Q7-8 | `replies/13-beyond-7_8.md` |
| 13d | Beyond Mueller all 8 | `replies/13-beyond-all.md` |

Also incorporates: Mueller source verification (`dd_epis.cl`, `dd_ri.cl`, `dd_rule2.cl`).

---

## Settled Findings

### Memory ecology

**Three-tier admission** — settled
- `:trace` / `:provisional` / `:durable`
- Default for rationalization and reversal is `:provisional`
- Roving hot-cues almost never promote to `:durable`
- First pass implemented in code

**Cue zone separation** — settled
- Content cues drive retrieval + reminding + FIFO
- Provenance is queryable, weak tie-break, not threshold-counted
- Support tags are metadata only
- First pass implemented in code

**Provenance requires content marks** — settled
- Provenance bonus only applies after content marks exist
- Imaginary/counterfactual material has stricter floor (2 marks vs 1)
- Implemented

**Bridge bonus direction** — settled
- Shorter = stronger for retrieval relevance
- Implemented (flipped from earlier deeper=stronger)

**Cheap rationalization resurrection gated** — settled
- `:serendipity? true` removed for stored-frame fallback
- Implemented

**Same-family provenance cap** — settled
- Same-family fallback gated on durable promotion
- Implemented

**Double-counting eliminated** — settled
- Cue overlap and provenance bonus no longer both count same evidence
- Implemented

### Executor boundary

**`execute-rule` in `rules.clj`** — settled
- Three-stage validation: shape → consequent schema → denotation
- `dispatch-executor` with `case` on `:kind`
- `instantiate-rule` becomes compatibility wrapper
- Five-step migration (A-E) keeps tests green
- Reviews 04, 07, 10 converge

**Three-channel RuleResultV1** — settled
- `:consequents` (schema-validated, graphable)
- `:effects` (typed kernel ops)
- `:summary` / `:episode-material` (trace + persistence)
- Executor does not mutate world

**Denotation validation** — settled
- `:validation-fn` returns vector of failure keywords
- Every keyword must be declared in `:failure-modes`
- Structural failures in `rules.clj`, semantic failures in denotation

### Verified paths

**Status lattice** — settled
- `:candidate` → `:projection-verified` → `:episode-constructed` → `:grounded` → `:sound-under-executors`
- First pass is progressive binding propagation
- Mueller-faithful version builds episode skeleton while walking
- `bridge-paths` stays candidate-only; `verify-path` is a sibling layer

**Partial verification is structured, not scalar** — settled
- Per-hop status, bindings delta, open obligations, verified prefix length
- Lexicographic ranking preserves diagnosis

### Mueller regression

**Six gates Mueller had that we collapsed** — settled
- `hidden?` → threshold=100 (our `:trace`)
- `needed-for-plan?` / `needed-for-reminding?` per index (our cue zones)
- Misc indices as `nil nil` (our support zone)
- Two separate thresholds (plan vs reminding)
- `accessible?` toggle on rules
- Realism/desirability as ordering criteria

### Build order

**Five-step sequence** — settled
1. Memory ecology (active)
2. Executor boundary
3. `:llm-backed` evaluator pilot
4. Verified paths
5. Generic `:clojure-fn` dispatch

---

## Provisional Findings

### Promotion criteria (from 11a, 11b)

**Promotion requires structural usefulness + outcome evidence** — provisional
- Cross-family reuse, downstream success, or world confirmation
- Evaluator is a gate/veto, not sole authority
- Rationalization strictest, reversal slightly looser, roving almost never
- `eligible-for-promotion?` function shape proposed but not implemented

**Episode-use attribution as the unifying abstraction** — provisional
- `note-episode-use` + `resolve-episode-use-outcome` + `reconcile-episode-admission`
- "The missing abstraction is episode use with attributed outcomes"
- Proposed but not implemented

**Promotion is reversible** — provisional
- `:durable` → `:provisional` on contradiction
- `:provisional` → `:trace` on persistent failure
- Non-monotone lattice

### Anti-residue detection (from 11a, 11b)

**`:same-family-loop`** — provisional
- Detect: same-family-use-cycles ≥ threshold (2 for rationalization, 3 for reversal), cross-family success = 0
- Mechanical, based on use records

**`:backfired`** — provisional
- Detect: same concern retriggered same family within 2 cycles with no emotion improvement, or supported goal terminated `:failed`
- One backfire sufficient for rationalization, two for reversal

**`:stale`** — provisional
- Detect: used 3+ times, successful-use = 0, grounding = 0, no success in 8+ cycles
- Batch consolidation, not retrieval-time

**`:contradicted`** — provisional
- Detect: structural comparison against newly canonicalized facts
- Needs fact-type-specific comparator (`defmulti fact-contradicts?`)
- One explicit contradiction sufficient

### Rule accessibility frontier (from 11a, 11b, Mueller verification)

**Three-state lattice** — provisional
- `:accessible` / `:frontier` / `:quarantined`
- Tracked in world state (`:rule-access` registry), NOT in `RuleV1`
- `build-connection-graph` stays structural; accessibility is caller-level filter
- `planning-graph` / `serendipity-graph` helpers filter the structural graph

**Provisional episodes do NOT open rules** — provisional
- Only `:durable` episodes promote rules from `:frontier` to `:accessible`
- Authored core rules start `:accessible`
- Induced/experimental rules start `:frontier`

### Heterogeneous graph serendipity (from 13)

**Ephemeral descriptor-slippage workspace** — provisional
- Copycat import: temporary descriptor rewrites queryable during path search
- Not admitted into permanent graph unless validated
- Resolves Copycat/descriptor-rigidity tension

**Cross-phase rule classes needed** — provisional
- Retrieval cue/result, episode evaluation, episode use/outcome, anti-residue, concern aftermath
- These add semantic phases that make serendipity non-trivial

**Density guardrail** — provisional
- Too sparse: 0-1 candidates per cross-phase query
- Promising: 3-20 candidates, 10%+ verification yield
- Soup: 50+ candidates, <5% yield

### Evaluator insertion (from 13)

**Pointwise by default, pairwise after structural narrowing** — provisional
- Don't use global per-cycle judges
- Evaluate bounded objects
- Funnel: structural narrowing → cheap pointwise → ambiguous escalate

**Evaluator rollout order** — provisional
1. Post-plan episode evaluator
2. Path-usefulness evaluator
3. Analogy aptness evaluator
4. Rationalization backfire evaluator
5. Mutation triage evaluator

### Induced rule admission (from 13)

**Eight-step admission ladder** — provisional
1. Schema validity
2. Denotation validity
3. Sandbox executability
4. Behavioral tests
5. Multi-trace support
6. Novelty/subsumption/compression (MDL)
7. Frontier admission only
8. Promotion by downstream evidence

**Start with narrow bridge rules** — provisional
- Induce from repeated cross-family transitions, not broad planning rules
- Lower blast radius

### Directed daydreaming (from 13 Q4, replies 4-6 and all-8)

**Brief constrains concern-space and admissibility, not operator family** — provisional
- Brief should strongly constrain: which concerns dominate, which episodes are in play, which scenes are admissible
- Brief should weakly constrain: family priors, mutation temperature, serendipity thresholds
- Brief should NOT hard-select the operator at every step
- "Brief constrains concern/situation space" = daydream-like. "Operator label directly controls generation" = structured prompting.
- Mueller explicitly excluded directed vs. undirected from his theory. This is our extension.
- Outside support: fMRI study showing goal-directed simulation couples default+control networks. Incubation study (N=200, β=0.38) showing mind wandering during incubation predicts creative improvement. N=1309 study: deliberate mind wandering positively associated with creativity.

**Thin/thick split** — settled as framing
- **Thin version (implementable now):** Brief = situation configuration + initial episode set. Kernel's existing situation model already supports this. Operators fire endogenously from emotion. This is a control surface, not a new mechanism. Graffito demo needs this.
- **Thick version (needs beyond-Mueller stack):** Brief also shapes serendipity search anchoring, mutation constraints, operator priors, evaluation criteria, admissibility filters. Needs heterogeneous graph + verified paths + write optimization.
- The thin version should land early as a product surface. The thick version waits for infrastructure. Don't let architectural purity delay the product path.

### DSPy write optimization (from 13 Q5, replies 4-6 and all-8)

**Optimize later usefulness + hygiene, NOT trace divergence** — provisional
- Four metric families: write coverage/reconstruction, retrieval quality (retrieve→chosen→success ratios), epistemic hygiene (penalize contradiction/backfire/stale/loop), efficiency/compression
- Token-space reconstruction target: kernel-relevant state (concern, family, world anchors, evaluation flags), not prose
- No published DSPy work on write-side memory optimization specifically
- Mem0 warning: graph-memory added only ~2% over base memory. Don't fetishize graph at expense of write/read discipline.
- GradMem gives the right slogan ("writing should be evaluated, not one-shot dumped"), not the right literal method
- **Sequencing: implementation stays parked until the membrane is stronger.** A DSPy pass on a leaky ecology learns to exploit the leaks.

### Developmental trajectory (from 13 Q6, replies 4-6 and all-8)

**Style = stable history-caused retrieval/control bias that transfers to held-out probes and beats seed drift** — parked
- This is a repo extension, not Mueller reconstruction
- The control experiment: multiple replicas, same start, controlled seeds, divergent histories, identical held-out probes. Compare within-condition and between-condition variance.
- Seed noise is real — divergence alone proves nothing. Cheap. Meaningful divergence is hard.
- Human expertise is compressive, not additive (~66 days to automaticity). Development = different control surfaces from compressed experience.
- Concrete metrics: family-choice distribution + KL divergence, cue-type usage, same-family vs cross-family reuse, promotion/demotion rates, serendipity path topology, motif concentration, response profile on identical probes
- "If the system just remembers more, that is drift, not development."

### Accessibility frontier as growth (from 13 Q7, replies 7-8 and all-8)

**This direction may be a genuine contribution to the cognitive architecture space** — settled as assessment
- 5 Pro confirmed: "your `:frontier / :accessible / :quarantined` idea looks like an actual contribution, not a literature rename"
- Neither Soar chunking nor ACT-R production compilation has explicit staged admission with evidence requirements
- Soar has proto-staged-admission (deferred numeric-preference chunks) but it's not a designed framework
- This is rare — the project is contributing something architecturally novel, not just recombining existing ideas

**Mueller's frontier is access restructuring, not Soar-style automatic compilation** — provisional
- Mueller's mechanism: reindex latent structure so future thought can reach it. Closer to "chunking + verification-based learning + deferred reliance" than plain chunking.
- Soar warning: without explanation-based summarization, chunking degrades. Soar 9.4 learned 1263 rules; Soar 9.6 with EBBS learned 8 general rules for the same task. Discipline matters.
- ACT-R warning: production compilation proliferates specialized rules. Risk is over-specialization and dead compiled rules, not frontier discovery.
- Soar has proto-staged-admission: chunks from numeric-preference decisions are deferred until "sufficient accumulated experience." Closest lit match to our `:frontier → :accessible`.
- Our `:frontier / :accessible / :quarantined` registry is a more explicit staged-admission layer than the default Soar/ACT-R mechanisms, not a claim of overall architectural superiority
- The ladder: candidate path → verified → provisional episode witness → durable episode evidence → frontier rule opened. Failure: opened rule → loop/backfire/contradiction → quarantine.
- "The missing abstraction is still episode use with attributed outcomes."

### Multi-model routing as cognitive economics (from 13 Q8, replies 7-8 and all-8)

**Routing principle: route on internal ambiguity and stakes, not family label** — provisional
- Nelson & Narens monitoring/control is the right framework: monitoring estimates state, control allocates resources
- FAtiMA is the architectural ancestor (metacognitive layer), not Mueller
- Our distinctive advantage: kernel can monitor PROCESS state, not just input difficulty
- Six monitoring features: verification ambiguity, retrieval ambiguity, memory-risk signals, emotional pressure/competition, action criticality, budget/history
- Monitor vector: `[stakes, verification-ambiguity, retrieval-ambiguity, memory-risk, affective-pressure, budget]`
- Control output: `{model, reasoning_strategy, n_samples, max_tokens}`
- "Reversal family" is a lossy proxy. Real triggers: verification_level, open_obligation_count, binding_conflict_count, retrieval_margin, threshold_slack, anti_residue_flag_count, pending transitions.

**Routing mechanism: hand-authored policy first, learned router later** — provisional
- Phase 1: typed lexicographic policy (hand-authored)
- Phase 2: learned router from telemetry (contextual bandit is the leading candidate, not a settled commitment)
- Route to Reason and BEST-Route support the general direction, but the mechanism should remain subordinate to kernel-owned monitoring signals

---

## Deferred / Speculative

### Ephemeral descriptor-slippage workspace (from 13 Q1)
- Copycat import: temporary descriptor rewrites queryable during path search
- Not admitted into permanent graph unless validated
- Concrete Clojure shape proposed
- **Status: parked** — interesting but requires heterogeneous graph first

### DSPy implementation (from 13 Q5)
- The write interface is already a DSPy signature
- But: don't optimize until membrane is solid
- **Status: parked** — deferred until memory ecology fully stable

### Developmental measurement harness (from 13 Q6)
- Clone-style longitudinal protocol designed
- Metrics identified (family KL divergence, motif concentration, etc.)
- **Status: parked** — research program, not implementable until accumulation is running

### All-8 reply build order for beyond-Mueller directions
- Proposed: 2+7 (evaluators + accessibility) → 1 tiny corridor → 5 write optimization → 8 routing → 3 rule creation → 6 developmental measurement → 4 directed daydreaming
- **Status: provisional** — good prioritization, but depends on current build order completing first

### Multi-model routing as metacognition (Q8)
- Kernel controls routing based on structural difficulty
- Monitoring/control framework from metacognition literature
- Awaiting Q7-8 reply

---

## Doc Updates

Which durable docs should absorb which findings:

| Finding | Target doc | Status |
|---------|-----------|--------|
| Three-tier admission | `extension-consolidation.md` | Done |
| Cue zone separation | `extension-consolidation.md` | Done |
| Build order | `build-order-checkpoint-2026-03-22.md` | Done |
| Mueller regression gates | `mueller-to-kernel-mapping.md` | Done |
| `execute-rule` spec | `kernel-rule-schema-and-execution-model.md` | Pending (review 10 gives exact spec) |
| Verified path lattice | New doc or append to `rule-connection-graph.md` | Pending |
| Promotion criteria | `extension-consolidation.md` | Pending |
| Anti-residue detection | `extension-consolidation.md` | Pending |
| Rule accessibility frontier | New doc or append to `rule-connection-graph.md` | Pending |
| Descriptor-slippage workspace | Mechanism 13 open question or new doc | Parked |
| Evaluator rollout order | `build-order-checkpoint` | Pending |
| Induced rule ladder | `build-order-checkpoint` or `rule-engine-trajectory.md` | Parked |
| Density guardrail | `rule-connection-graph.md` | Parked |
| Directed daydreaming framing | `beyond-mueller-detailed.md` Q4 | Provisional |
| DSPy write metrics (4 families) | `beyond-mueller-detailed.md` Q5 | Parked |
| Developmental control experiment | `beyond-mueller-detailed.md` Q6 | Parked |
| Soar chunking warnings + EBBS | `rule-engine-trajectory.md` | Provisional |
| Accessibility ladder (candidate→verified→durable→opened) | `rule-connection-graph.md` or `build-order-checkpoint` | Provisional |
| Routing monitor vector + lexicographic policy | New doc or `build-order-checkpoint` | Provisional |
