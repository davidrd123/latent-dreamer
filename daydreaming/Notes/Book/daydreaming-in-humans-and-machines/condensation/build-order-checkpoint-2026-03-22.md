# Build Order Checkpoint — 2026-03-22

Supersedes the step sequence in `rule-engine-trajectory.md` based on the
2026-03-22 review stack from 5 Pro and 5 Thinking (`03` through `09`).

Status note: the first memory-ecology pass is now in code. This document
tracks the settled order and the remaining gaps, not just desiderata.

## What changed

The original trajectory assumed: generic dispatcher → provenance → graph query → runtime integration → more families → evaluation rules → serendipity.

The reviews found:
- The memory ecology has active groove risks (reviews 03, 06)
- The executor boundary needs a declarative effect contract before new executor kinds (reviews 04, 07)
- Verified paths should come after the memory and executor contracts are solid (reviews 05, 08)
- Review 09 turned the memory problem into a minimum patch set: cue roles,
  admission gating, provenance floors, and no cheap rationalization resurrection

## Settled build order

### 1. Memory ecology (ACTIVE — in progress)

Fix the core flaw: "generated because it fit the moment" ≈ "earned the right to bias future cognition."

Concrete targets:
- **Admission tiers**: `:trace` / `:provisional` / `:durable` on every family-plan episode. Default for rationalization and reversal is `:provisional`, not durable.
- **Cue zone separation**: content cues (drive retrieval + reminding), provenance (queryable, weak tie-break only), support tags (metadata, never counted toward threshold). Substrate-level enforcement, not just caller discipline.
- **Eliminate double-counting**: same structural evidence currently counted once as cue overlap AND again as provenance bonus.
- **Cap same-family provenance**: shared-rule/shared-edge bonus cannot rescue weak episodes by itself. Require at least one world-anchored content cue for reentry.
- **Fix FIFO crowding**: metadata tags can crowd out content cues in the recent-indices FIFO (max 6). Only content-zone indices should enter the FIFO.
- **Anti-residue flags**: `:backfired`, `:stale`, `:contradicted`, `:same-family-loop`. Retrieval down-ranks or excludes these.
- **Gate rationalization resurrection**: remove or gate `:serendipity? true` for stored-frame fallback. Pure family-internal resonance should not be enough to reopen a frame.

Why first: every cycle that stores a family-plan episode is building potentially self-reinforcing retrievability. The groove risks are active now.

Implemented in the first pass:
- `:trace` / `:provisional` admission status on family-plan episodes
- content / reminding / provenance / support cue roles persisted on episodes
- support and rule-path indices removed from the normal retrieval index
- reminding only activates reminding cues
- provenance requires content marks before it applies
  (`1` for ordinary episodes, `2` for imaginary / counterfactual)
- rationalization stored-frame fallback no longer gets cheap `:serendipity? true`
- same-family fallback for rationalization and reversal is gated on durable promotion
- the substrate can now promote `:provisional -> :durable`
- promotion emits a typed `:episode-promotion` fact into the branch context
- promotion still respects the recent-episode anti-echo gate
- repeated same-family reuse can now flag `:same-family-loop`
  and suppress same-family reentry; one earlier cross-family success
  no longer grants lifetime immunity from that loop signal
- external evaluator output can now annotate promotion eligibility
  through an explicit promotion decision; heuristic evaluation still
  defaults to `:stay-provisional`
- direct evaluator-only store-time durable admission has been removed;
  durable status and frontier opening now depend on later
  use/outcome reconciliation
- evaluator output can now attach `:backfired`, `:stale`, and
  `:contradicted` flags to stored episodes, and those flags are
  asserted as typed `:episode-flag` facts into branch contexts
- first attributed use/outcome slice is now in code:
  `note-episode-use`, `resolve-episode-use-outcome`,
  `record-promotion-evidence`, and `reconcile-episode-admission`
- `roving` now emits typed retrieval-hit facts, but it no longer
  treats reminded family-plan episodes as use/outcome evidence by
  itself; retrieval remains retrieval until a later vindication path
  exists
- stored rationalization-frame reopen and stored reversal-counterfactual
  reopen now also run through attributed use rather than the old
  same-family reuse wrapper; immediate resolution is now reserved for
  negative branch-local evidence, not plain successful reuse
- repeated later cross-family success can now vindicate those pending
  same-family source uses without counting that vindication as new
  promotion evidence
- repeated cross-family success now promotes via explicit use evidence
  (`:cross-family-use-success`); raw roving reminding no longer counts
  as that evidence by itself
- family-plan episodes now also carry a kernel-owned structural
  promotion basis (`:reframe-facts`, `:input-facts`, or `:none`);
  later durable promotion eligibility reads that basis instead of
  evaluator-authored `:retention-class` / `:keep-decision` labels
- repeated failed same-family attributed use can now auto-flag
  `:stale` and demote a previously durable episode back to
  `:provisional`; one later cross-family success does not re-promote it
  automatically
- repeated later cross-family success can now clear `:stale` and let
  durable admission be re-earned through evidence
- repeated later cross-family success can now also clear
  `:same-family-loop`, but only when that evidence is genuinely later
  than the loop-triggering use; once cleared, same-family pressure
  accumulates from that rehabilitation point rather than lifetime
  totals
- inert rule-access scaffolding is now in code:
  `world[:rule-access]` with `:accessible` / `:frontier` /
  `:quarantined`
- authored family rules initialize as `:accessible`, preserving current
  planner behavior while making the frontier state real
- graph views are now split above the structural graph:
  planning view filters to `:accessible`, serendipity view allows
  `:accessible ∪ :frontier`
- `:goal-family/reversal-aftershock-to-rationalization` now starts as
  the first real `:authored-frontier` bridge rule, not just a
  documented future case
- actual cross-family trigger emission now reads that access split
  instead of walking the raw bridge-rule list, so quarantining a
  frontier bridge suppresses the handoff in runtime behavior
- activation and planning dispatch now also read planner-visible rule
  sets rather than raw `activation-rules` / `planning-rules`, so
  frontier/quarantined state now gates those live family paths too
- durable episode promotion can open frontier rules to
  `:accessible`, and hard-failure demotion can quarantine non-core
  rules on the episode's rule path
- later durable evidence can now reopen explicitly quarantined
  non-core rules to `:frontier`, so quarantine is no longer purely
  one-way; reopening now requires later durable evidence from a
  different episode than the one that quarantined the rule

Still missing inside step 1:
- broader family coverage for episode-use logs with attributed outcomes
  (`used by which family, for which goal, with what later result`)
- broader structural evidence policy beyond the current kernel-owned
  family-plan promotion basis, still with the evaluator acting as
  gate/veto rather than sole authority
- cleaner evidence/authority semantics:
  `retrieved` vs `used` vs `vindicated`, and evaluator output as
  advisory/gating input rather than direct admission authority
- broader vindication policy beyond the first pending-same-family
  source-use path, plus fuller quarantine reopening / rehabilitation
- fuller frontier structure beyond the first live frontier bridge rule;
  most family activation/planning rules still default to authored-core
- source-type decay / stronger zone-specific consolidation
- explicit loop-risk metadata shaping retrieval
- stronger downstream flagging / demotion beyond evaluator annotation
- contradiction/backfire detection from later evidence instead of
  only evaluator judgment
- review 13 guardrail: keep growth grounded in typed cross-phase
  artifacts (use/outcome, evaluation, retrieval aftermath, frontier
  admission) rather than adding more family-wrapper rules or letting
  evaluators bypass the substrate
- review 13 sequencing guardrail: do not optimize write/read policy or
  DSPy-style residue prompts until the memory membrane is trustworthy;
  otherwise optimization will learn the current leaks instead of
  strengthening the architecture

### 2. Executor boundary with declarative effects

Define the contract that `:clojure-fn` and `:llm-backed` executors must satisfy.

Core principle: procedural computation inside the executor, declarative effect description at the boundary, kernel-owned application of those effects.

`RuleResultV1` evolves to:
```clojure
{:consequents [...]        ; schema-validated, graphable products
 :effects [...]            ; typed kernel ops (:context/sprout, :fact/assert, etc.)
 :summary {...}            ; trace-facing outcome
 :episode-material {...}   ; generic post-plan persistence input
 :confidence ...
 :reason ...}
```

Concrete targets:
- Define the effect vocabulary for the three family plans (roving, rationalization, reversal)
- Separate `:consequent-schema` (graph-visible products) from `:effect-schema` (allowed operational effects) from `:denotation` (intended state change)
- Handle symbolic refs for id allocation (executor can't allocate context IDs — kernel resolves refs during commit)
- Episode storage stays outside the executor boundary
- Provenance stamped after admission, not before

Why second: this is the architecture fix. New executor kinds (`:clojure-fn`, `:llm-backed`) should produce declarative effects from day one, not wrap hidden mutation.

Implemented first pass:
- `roving` now builds a typed effect program and applies it through a
  kernel-owned effect applier instead of mutating world inline
- `execute-rule` now supports call-supplied `:clojure-fn` executor
  bindings, so pure rule data can stay in the registry while runtime
  executor ownership lives at the family/kernel boundary
- `execute-rule` now performs minimal effect validation:
  `:effects` must be a vector of maps, each effect must declare a
  keyword `:op`, and rule-declared `:effect-ops` are enforced
- `execute-rule` now also runs a call-supplied effect validator so the
  current family runtime can reject malformed op payloads before local
  kernel application
- `execute-rule` now also validates a rule-declared `:effect-schema`,
  so `:clojure-fn` family dispatch rules declare their expected
  ordered effect shape rather than only an allowed op set; this check
  is now actually order-sensitive rather than treating the effect
  program as an unordered fact bag
- that `:effect-schema` check is now also closed at the top level for
  current family dispatch rules: undeclared effect keys fail at the
  executor boundary instead of drifting past the rule contract
- `rules.clj` now also owns the generic effect-program
  reduction/threading scaffold via `apply-effects`; family code still
  owns op semantics, symbolic refs, and world mutation handlers
- `rules.clj` now also owns the first builtin effect handlers:
  `:context/sprout`, `:fact/assert`, `:facts/assert-many`,
  `:context/set-ordering`, and `:goal/set-next-context`
- builtin `:goal/set-next-context` now fails closed on unknown goals,
  and the family whole-program validator now rejects duplicate
  symbolic producers across `:ref` / `:result-key`
- `goal_families.clj` now exposes those handlers as an explicit
  op-handler registry instead of a single monolithic dispatcher, and
  now only retains family-semantic handlers
- `:goal-family/roving-plan-dispatch` is now the first real
  `:clojure-fn` vertical slice: the rule dispatches through
  `execute-rule`, returns a typed effect program, and local kernel code
  still applies those effects
- `:goal-family/rationalization-plan-dispatch` now runs through the
  same seam, using a typed effect program for sprout/assert/diversion/
  afterglow/log while keeping effect application kernel-local
- `:goal-family/reversal-plan-dispatch` now runs through the same seam
  as a composite branch program, with local kernel code still applying
  the multi-branch reversal effect
- the first op-specific payload validation now exists for the current
  family effect vocabulary, so the Step 2 contract is no longer just
  “vector of ops” but “declared ops with validated payload shape”
- first effect ops are:
  `:context/sprout`, `:fact/assert`, `:episode/reminding`,
  `:episode/assert-retrieval-hits`, `:episodes/note-family-uses`,
  `:episodes/resolve-use-outcomes`, `:context/set-ordering`,
  `:goal/set-next-context`, `:mutation/log`, `:facts/assert-many`,
  `:rationalization/divert-emotion`,
  `:rationalization/assert-afterglow`,
  `:mutation/log-rationalization`, `:reversal/execute-branches`

Next seam from review 10:
- keep `instantiate-rule` as a compatibility wrapper
- keep effect application local while the effect contract tightens
- next move is not another family migration; it is using
  `:effect-schema` plus deeper payload validation to keep the contract
  honest while continuing to pull the generic effect runtime into
  `rules.clj`
- only then widen toward generic effect-schema validation and a shared
  effect runtime

### 3. First `:llm-backed` pilot as episode evaluator

Not generator. Evaluator.

After a rationalization or reversal family plan is produced, ask an LLM for:
- Realism score
- Desirability score
- Keep-hot vs archive recommendation

This gates promotion from `:provisional` to `:durable`. The evaluator is the mechanism that prevents the memory ecology from degrading.

Why this pilot:
- Exercises `:llm-backed` within RuleV1
- Creates the missing evaluative metadata the memory system needs
- Avoids granting the LLM control over branching, backtracking, or state mutation
- Mueller-faithful fallback: neutral score or skip

### 4. Verified paths in stages

Leave `bridge-paths` as candidate search. Add `verify-path` as a sibling layer.

Status lattice:
- `:candidate` — current `bridge-paths` (structural adjacency)
- `:projection-verified` — progressive binding propagation succeeds across hop projections
- `:episode-constructed` — same, plus concrete episode skeleton built
- `:grounded` — non-bridge antecedent obligations discharged against ambient facts
- `:sound-under-executors` — executor/denotation contracts checked

Three implementation stages:
- Stage 1: `verify-path` with progressive binding (`:projection-verified`). Debugging tool.
- Stage 2: Episode skeleton construction (`:episode-constructed`). Mueller-faithful.
- Stage 3: Grounding + executor checking (`:grounded`, `:sound`). After executor boundary is real.

Design note (from Draft-and-Prune, arxiv 2603.17233): add a
well-definedness gate early in verification. A path that's `:bound`
but has contradictory bindings at different hops, or ambiguous
bindings that admit multiple incompatible readings, should be
rejected before reaching `:supported`. Fail on contradiction and
ambiguity, not just on binding propagation failure. This is cheap
insurance against paths that unify syntactically but are
semantically incoherent.

Why after steps 1-3: verifying paths through a tiny corridor is honest but uninformative. The graph needs more heterogeneity (evaluation facts, retrieval events, critic signals) and the executor contract needs to be real before verification produces meaningful results.

### 5. Generic executor dispatch with `:clojure-fn`

Migrate family plan bodies to `:clojure-fn` executors that return declarative effects.

Order: roving first (simplest), rationalization second, reversal last (hardest — multi-branch, composite context surgery).

Why last: the executor contract must be real (step 2), the memory ecology must be safe (step 1), and the LLM evaluator must be gating promotion (step 3) before more writeback paths are added.

## The discipline chain (fully specified)

```
Rule fires
  → executor computes effects (no world mutation)
  → kernel validates consequents against :consequent-schema
  → kernel validates effects against :effect-schema
  → kernel applies effects to world
  → kernel evaluates the result (possibly :llm-backed)
  → kernel decides admission tier:
      :trace       → auditable, not retrievable for planning
      :provisional → retrievable under strict conditions
      :durable     → fully retrievable, promoted after downstream success
  → index zones enforced:
      content cues   → drive retrieval and reminding
      provenance     → queryable, weak tie-break only
      support tags   → never counted toward threshold
  → same-family provenance bonus capped
  → anti-residue flags checked
```

## What's done vs what's not

| Component | Status |
|-----------|--------|
| Rule validation + matcher + graph construction | Done |
| Graphable trigger/activation/planning rule pairs | Done |
| Provenance-weighted retrieval (bonus flipped to shorter=stronger) | Done |
| Cross-family bridges | Done |
| Cross-family reuse into roving | Done |
| Same-family reuse gated on durable promotion | Done |
| Conductor extraction | Done |
| Evaluation metadata fields on episodes | Done |
| **Admission tiers (trace/provisional/durable)** | **Second pass done** |
| **Cue zone separation (content/provenance/support)** | **First pass done** |
| **Double-counting elimination** | **First pass done** |
| **Same-family provenance cap** | **First pass done** |
| **Content-mark floor before provenance applies** | **Done** |
| **Rationalization no-cheap-resurrection gate** | **Done** |
| **Cross-family promotion to durable** | **Done** |
| **Evaluator gate/veto seam for promotion** | **First pass done** |
| **Same-family-loop anti-residue flag** | **First pass done** |
| **Other anti-residue flags** | **First pass done via evaluator seam** |
| **Effect vocabulary** | **First pass done on roving** |
| **:llm-backed evaluator pilot** | **First pass done (post-plan evaluator seam)** |
| **Verified paths** | **Not done** |
| **Generic :clojure-fn dispatch** | **Not done** |

## Source reviews

- `replies/archive-01-16/03-episode-loop-risks-5thinking.md` — groove risks, three-tier admission, anti-residue
- `replies/archive-01-16/04-executor-seam-5thinking.md` — declarative effects, three-channel RuleResultV1
- `replies/archive-01-16/05-verified-paths-5thinking.md` — status lattice, ambient ∪ derived witness
- `replies/archive-01-16/06-family-graph-rev-5pro.md` — double-counting, FIFO crowding, index zone separation
- `replies/archive-01-16/07-executor-seam-5pro.md` — symbolic refs, consequent-schema vs effect-schema
- `replies/archive-01-16/08-verified-paths-5pro.md` — progressive binding first, episode skeleton, code delta
- `replies/archive-01-16/09-episode-loop-risks-5pro.md` — minimum patch set for the memory membrane
- `replies/archive-01-16/10-executor-boundary-5pro.md` — `execute-rule`, RuleResultV1 validation, denotation contract
- `replies/archive-01-16/11a-promo-loose-5pro.md` — promotion needs structural + outcome evidence; evaluator is gate/veto
- `replies/archive-01-16/11b-promo-prompt-5pro.md` — episode use with attributed outcomes as the missing abstraction
