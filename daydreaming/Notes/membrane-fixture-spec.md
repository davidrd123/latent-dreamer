# Membrane Integration Fixture — Codex Task Spec

Date: 2026-03-22
Updated: 2026-03-22 (post-5-Pro sift, assay ladder reframe)

## Assay ladder

This fixture is **Assay A** in a benchmark ladder. It is not the
full membrane verdict.

```
Puppet Knows         runtime/seam regression (authored-dominant, stays)
Assay A (this)       L1/L2 + first flag movement (same-family-loop)
Fixed-chain test     direct promotion/access proof with clean evidence
Assay B (sibling)    cross-family-use-success -> durable -> frontier open
Graffito miniworld   richer world-model benchmark with typed fact-space
```

Each assay proves one thing. Do not bloat Assay A to cover later
assay territory. Assay A is now a frozen regression, not an active
promotion benchmark.

## Purpose

Assay A: prove dynamic stored-source entry, live pending-use
recording, and first defensive membrane movement (`:same-family-loop`
flag) in a live soak. Puppet Knows remains the runtime regression.
The fixed-chain test remains the separate proof that promotion and
frontier opening work when handed clean evidence directly.

## Why Puppet Knows can't do this

Puppet Knows has complete authored coverage:

- Reversal always selects `fc_admit_performance` (authored fixture cause)
- Rationalization always selects from `autonomous-rationalization-frames`
  (authored per-situation frames for s1 and s4)
- Roving pulls from `:roving-episodes` (seeded fixtures)

Dynamic family-plan episodes are stored but never retrieved because
authored sources always win the candidate race. The candidate-race
observability (commit 8dbe716) confirmed: zero dynamic candidates in
the race across 20 cycles.

## Design principle: deliberately incomplete authored coverage

The fixture has two situations. Each situation has authored coverage
for ONE family but not both. This forces the system to generate
dynamic material in one situation that becomes a candidate source
in the other situation's planning.

## Structural compatibility requirements

Dynamic reuse is not just index overlap. The stored-source paths in
`goal_families.clj` require structural compatibility:

- **Rationalization** stored-source needs matching `failed-goal-id`
  (`goal_families.clj:1062`). A stored rationalization episode from
  situation A can only be reused in situation B if both situations
  reference the same failed goal identity.
- **Reversal** stored-source needs matching goal identity plus
  retracted-fact overlap (`goal_families.clj:900`). A stored reversal
  episode from situation B can only be reused in situation A if the
  goal structure matches and the stored episode's
  `:reversal_leaf_retracted_facts` overlap fact ids visible in
  situation A's reversal context.

So the fixture must share goal structure across situations, not just
indices. In the current harness the shared cue is the fact
`:shared_stage_light`, but cue overlap alone is still not enough; the
candidate filter then checks structural compatibility too. Both must
be satisfied.

## Fixture definition

### Situations

```
situation A — "the rehearsal room"
  indices: [:mirror :voice :doubt :preparation :stage_light]
  initial state:
    activation: 0.55, ripeness: 0.6
    hope: 0.4, threat: 0.35, anger: 0.1, grief: 0.15

situation B — "the empty house"
  indices: [:silence :dust :memory :absence :stage_light]
  initial state:
    activation: 0.50, ripeness: 0.55
    hope: 0.2, threat: 0.25, anger: 0.3, grief: 0.4
```

Index overlap: `:stage_light` is shared. This provides the retrieval
coincidence bridge. But retrieval alone is not enough — the structural
compatibility checks below must also pass.

### Shared goal structure

Both situations reference a common top-level failed goal. This is
what makes cross-situation stored-source reuse structurally possible:

```
shared failed goal: :g_performance_failure
  - both situations have rationalization frames targeting this goal
  - both situations have reversal leaf goals under this goal
  - the failed-goal-id match is what lets a stored rationalization
    episode from situation A be a candidate source in situation B
```

For reversal compatibility, both situations also share a leaf
objective fact structure:

```
shared leaf objective: :performance_stays_hidden
  - reversal retracted-facts reference this
  - a stored reversal episode from situation B carries retracted-fact
    IDs that overlap fact ids visible in situation A's reversal
    context when reversal activates there
```

### Authored coverage — deliberately asymmetric

```
situation A has:
  - authored rationalization frame targeting :g_performance_failure
    (reframe-facts: [{:fact/type :rationalization :fact/id :doubt_is_preparation}])
  - NO authored reversal counterfactual cause

situation B has:
  - authored reversal counterfactual cause for :g_performance_failure
    (counterfactual-facts: [{:fact/type :counterfactual :fact/id :absence_is_filled}])
    with retracted-facts that overlap fact ids visible in situation A's
    reversal context
  - NO authored rationalization frame
```

So:

- Rationalization in situation A fires with the authored frame, stores
  a family-plan episode with `:reframe-facts` (promotion-eligible),
  targeting `:g_performance_failure`
- Rationalization in situation B has no authored frame — must use a
  dynamic stored episode from situation A (structurally compatible
  because same `failed-goal-id`) OR return nil. This fixture must
  explicitly suppress any generic authored fallback; omitting a map
  entry alone is not enough in the more autonomous benchmarks.
- Reversal in situation B fires with the authored counterfactual
- Reversal in situation A has no authored counterfactual — must use a
  dynamic stored episode from situation B (structurally compatible
  because same goal identity + retracted-fact overlap) OR fall back

### Concrete cue overlap used in the shipped harness

The current code-defined fixture makes the overlap explicit in visible
facts rather than via a scored graph:

```
Situation A visible facts include:
  - :shared_stage_light
  - :absence_is_filled
  - :performance_stays_hidden

Situation B visible facts include:
  - :shared_stage_light
  - :doubt_is_preparation
  - :performance_stays_hidden
```

This gives the first stored episodes two retrieval cues when they are
looked up later:

- rationalization A -> rationalization B:
  `:shared_stage_light` + `:doubt_is_preparation`
- reversal B -> reversal A:
  `:absence_is_filled` + `:performance_stays_hidden`

That is enough to clear the current content-threshold without changing
kernel retrieval policy.

### Reversal setup (seeded facts)

Same structure as `seed-reversal-setup` in puppet_knows.clj, but
with the shared goal structure:

Both situations share:
- Failed goal `:g_performance_failure` in the root context
- Leaf goal with objective fact `:performance_stays_hidden`

Situation B additionally has:
- The authored failure-cause `fc_absence_filled` with the
  counterfactual fact `:absence_is_filled`

Situation A has NO authored failure-cause. When reversal activates
for situation A, the only source candidates are dynamic stored
episodes from situation B's reversal runs — which are structurally
compatible because they share `:g_performance_failure` and their
retracted-facts overlap fact ids visible in situation A's reversal
context.

### Roving episodes (seeded)

Two pleasant episodes, one per situation:

```
ep-roving-a: stored under [:mirror, :preparation]
ep-roving-b: stored under [:silence, :memory]
```

These go into `:roving-episodes`.

### Rationalization frames

Only situation A gets an authored frame:

```
{:situation_a {:priority 0.78
               :reframe-facts [{:fact/type :rationalization
                                :fact/id :doubt_is_preparation}]}}
```

Situation B gets no authored frame at all in the shipped harness.
The deterministic benchmark calls `run-family-plan` directly, so there
is no autonomous authored fallback map to suppress here.

## Expected dynamics

### Phase 1 (cycles 1-2): authored sources seed the store

- Situation A rationalization fires with authored frame, stores
  episodes with `:reframe-facts` as `:provisional`, promotion-eligible
- Situation B reversal fires with authored counterfactual, stores
  episodes with `:input-facts` as `:provisional`, promotion-eligible
- Roving seeds exist in the world but are not the active proof path in
  Assay A

### Phase 2 (cycles 3-4): dynamic sources enter and win

- A rationalization or reversal cycle in the OTHER situation retrieves
  a stored episode from phase 1 via coincidence marks and the
  structural compatibility filters pass (shared
  `failed-goal-id`, retracted-fact overlap)
- In the current deterministic harness, the stored episode does win
  because the authored source is absent on that side
- If selected as source, `note-family-plan-episode-use` fires and
  the episode accumulates use-history. This is the first real
  milestone: **dynamic source selected, use recorded.**
- The use is recorded as `:pending` initially. Outcome resolution
  happens through the existing evidence chain, not automatically at
  selection time. For same-family stored-source reuse, this usually
  means Level 2 only: the use record exists, but it does not itself
  create promotion evidence unless the branch immediately
  contradicts or backfires.

### Phase 3 (cycles 5-8): first defensive pressure appears

- Same-family reuse can now repeat enough to trigger the first narrow
  defensive movement: `:same-family-loop`
- This is the current live Level 3 result for Assay A
- Promotion and frontier opening do NOT belong to this phase; they
  belong to Assay B

Note: Assay A is deterministic on purpose. It is no longer aiming to
discover promotion dynamics inside a 20-cycle autonomous soak.

## Success criteria — Assay A scope

### Level 1 — dynamic candidates visible (minimum pass)

The candidate-race trace shows at least one dynamic source candidate
in a rationalization or reversal race. This means the retrieval +
structural compatibility chain works end to end.

**Status: PASSED** (codex confirmed in live 4-cycle run)

### Level 2 — use-history recorded (strong pass)

At least one stored family-plan episode has `use-history` with one
or more records. This means `note-family-plan-episode-use` fired
in a live cycle, not just in the unit test.

**Status: PASSED** (pending same-family source-use recorded on both
stored episodes)

### Level 3 (narrow) — first flag movement

A `:same-family-loop` flag fires from live same-family reuse. This
demonstrates the membrane defending against grooves in a real soak.

**Status: PASSED** (8-cycle deterministic run trips the flag)

### Beyond Assay A — belongs in Assay B (future sibling fixture)

These are NOT Assay A targets. They belong in a separate fixture
designed to produce cross-family evidence:

- Promotion from `:provisional` to `:durable` via cross-family-use-success
- Frontier rule opening from promotion evidence
- Broader demotion (`:stale`, `:backfired`, `:contradicted`)
- Stale rehabilitation via later cross-family evidence
- Full loop: promote -> open frontier -> route through opened rule

**Status: PASSED in sibling fixture**

The shipped sibling benchmark now lives at:

```
kernel/src/daydreamer/benchmarks/membrane_assay_b.clj
kernel/test/daydreamer/benchmarks/membrane_assay_b_test.clj
```

Its deterministic positive path is:

1. authored reversal in situation B
2. dynamic reversal in situation A
3. bridged rationalization in situation A
4. first cross-family success probe in situation B
5. second cross-family success probe in situation B

That harness proves, live:

- a stored rationalization episode can inherit the real
  `:goal-family/reversal-aftershock-to-rationalization` frontier rule in
  its `:rule-path`
- later cross-family use success can be attributed twice through the
  normal episode-use / outcome machinery
- the episode promotes from `:provisional` to `:durable`
- the frontier bridge opens from `:frontier` to `:accessible`

### Exclusion-reason logging (from 5 Pro review)

When a dynamic source loses a candidate race, log WHY:
- Threshold miss
- Structural mismatch (failed-goal-id, retracted-fact overlap)
- Same-family-loop block
- Stale block
- Contradicted block
- Authored outrank
- Recent-episode gate

This is needed for both Assay A diagnostics and Assay B design.

## Implementation notes

### File structure

```
kernel/src/daydreamer/benchmarks/membrane_fixture.clj
kernel/test/daydreamer/benchmarks/membrane_fixture_test.clj
kernel/src/daydreamer/benchmarks/membrane_assay_b.clj
kernel/test/daydreamer/benchmarks/membrane_assay_b_test.clj
```

### Shape of the shipped harness

The current implementation is narrower than the original plan:

- code-defined, not YAML/JSON
- deterministic cycle pattern, not autonomous scoring
- two situations only
- explicit authored asymmetry
- direct `run-family-plan` calls
- trace summaries focused on candidate races and use records

That is intentional. Assay A is a live membrane regression, not a
mini creative benchmark.

### No kernel gate change needed

The stored-source retrieval paths in `goal_families.clj` already
allow provisional dynamic candidates with a lower rank (as of
commit f00400c). No kernel change is needed for this fixture to
exercise the dynamic reuse path.

### No implied Level 3 guarantee

The kernel already supports the full promotion/access chain when it is
handed clean cross-family evidence. This fixture does not need to
change that substrate. But the benchmark should not assume that
same-family dynamic source reuse will naturally produce Level 3/4
movement. Level 3 remains a stretch unless the live soak creates a
separate cross-family evidence path.

### Seed an authored-frontier rule

If frontier movement (Level 3/4 success) is a goal, the fixture
must explicitly seed at least one rule with `:authored-frontier`
deployment role. This rule should be structurally reachable through
the cross-situation bridge so that promotion evidence from the
dynamic reuse path can trigger frontier opening.

## Run protocol

```bash
cd kernel && bb check                    # regression first
# For focused probes, use a dedicated project nREPL you started yourself
# (or a discovered dedicated port via clj-nrepl-eval), not a shared REPL.
# In REPL:
(require '[daydreamer.benchmarks.membrane-fixture :as mf])
(mf/run-soak 20)                         # 20-cycle soak
# Inspect:
# - :use-history on stored episodes
# - :promotion-evidence on stored episodes
# - :admission-status transitions
# - :rule-access transitions
# - candidate-race traces
```

If `clj-nrepl-eval` is available in the workflow, prefer it for
focused probes over an ad hoc REPL transcript.

## What this does NOT need

- Runtime thought / LLM calls (pure kernel soak)
- Director feedback (no external conductor)
- Scope rendering
- HTML trace output (text summary is enough for first pass)
- More than 20 cycles
