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
- cross-family reuse can now promote `:provisional -> :durable`
- promotion emits a typed `:episode-promotion` fact into the branch context
- promotion still respects the recent-episode anti-echo gate
- repeated same-family reuse can now flag `:same-family-loop`
  and suppress same-family reentry
- external evaluator output can now widen promotion beyond the
  cross-family reuse path through an explicit promotion decision;
  heuristic evaluation still defaults to `:stay-provisional`

Still missing inside step 1:
- remaining anti-residue flags (`:backfired`, `:stale`, `:contradicted`)
- source-type decay / stronger zone-specific consolidation
- explicit loop-risk metadata shaping retrieval

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
| **Evaluator-gated durable promotion** | **First pass done** |
| **Same-family-loop anti-residue flag** | **First pass done** |
| **Other anti-residue flags** | **Not done** |
| **Effect vocabulary** | **Not done** |
| **:llm-backed evaluator pilot** | **First pass done (post-plan evaluator seam)** |
| **Verified paths** | **Not done** |
| **Generic :clojure-fn dispatch** | **Not done** |

## Source reviews

- `replies/03-episode-loop-risks-5thinking.md` — groove risks, three-tier admission, anti-residue
- `replies/04-executor-seam-5thinking.md` — declarative effects, three-channel RuleResultV1
- `replies/05-verified-paths-5thinking.md` — status lattice, ambient ∪ derived witness
- `replies/06-family-graph-rev-5pro.md` — double-counting, FIFO crowding, index zone separation
- `replies/07-executor-seam-5pro.md` — symbolic refs, consequent-schema vs effect-schema
- `replies/08-verified-paths-5pro.md` — progressive binding first, episode skeleton, code delta
- `replies/09-episode-loop-risks-5pro.md` — minimum patch set for the memory membrane
