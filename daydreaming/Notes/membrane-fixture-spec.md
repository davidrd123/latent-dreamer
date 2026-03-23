# Membrane Integration Fixture — Codex Task Spec

Date: 2026-03-22

## Purpose

A minimal 2-situation autonomous benchmark that forces the membrane
state machine (promotion / demotion / flags / frontier opening) to
exercise in a live soak. Puppet Knows remains the runtime regression;
this fixture is the membrane integration test.

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
indices. The shared `:stage_light` index creates a retrieval bridge
(coincidence marks), but the candidate filter then checks structural
compatibility. Both must be satisfied.

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
    (counterfactual-facts: [{:fact/type :counterfactual :fact/id :absence_is_filled}
                            {:fact/type :situation :fact/id :situation_a}])
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
  explicitly suppress the usual autonomous generic fallback; omitting
  a map entry alone is not enough because Puppet Knows currently
  synthesizes a fallback frame with `:reframe-facts`.
- Reversal in situation B fires with the authored counterfactual
- Reversal in situation A has no authored counterfactual — must use a
  dynamic stored episode from situation B (structurally compatible
  because same goal identity + retracted-fact overlap) OR fall back

### Fixture graph nodes (minimal)

8 nodes total, 4 per situation. Each node has indices, a goal-type
compatibility, threshold, and situation-id.

```
Situation A nodes:
  ma01 — roving, indices: [:mirror :voice :preparation], threshold: 2
  ma02 — rationalization, indices: [:doubt :preparation :stage_light], threshold: 2
  ma03 — reversal, indices: [:mirror :doubt :voice :stage_light], threshold: 2
  ma04 — rehearsal, indices: [:preparation :voice :stage_light], threshold: 2

Situation B nodes:
  mb01 — roving, indices: [:silence :dust :memory], threshold: 2
  mb02 — rationalization, indices: [:absence :memory :stage_light], threshold: 2
  mb03 — reversal, indices: [:silence :absence :dust :stage_light], threshold: 2
  mb04 — repercussions, indices: [:dust :memory :absence], threshold: 2
```

### Edges (enough for scoring, minimal)

```
ma01 → ma02, weight 0.5
ma02 → ma03, weight 0.4
ma03 → ma04, weight 0.3
mb01 → mb02, weight 0.5
mb02 → mb03, weight 0.4
mb03 → mb04, weight 0.3
ma02 → mb02, weight 0.3  (cross-situation bridge via rationalization)
mb03 → ma03, weight 0.3  (cross-situation bridge via reversal)
```

### Reversal setup (seeded facts)

Same structure as `seed-reversal-setup` in puppet_knows.clj, but
with the shared goal structure:

Both situations share:
- Failed goal `:g_performance_failure` in the root context
- Leaf goal with objective fact `:performance_stays_hidden`

Situation B additionally has:
- The authored failure-cause `fc_absence_filled` with the
  counterfactual facts above, plus retracted-facts that overlap
  fact ids visible in situation A's reversal context
- A second failure-cause with lower priority (fallback)

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

Situation B must explicitly suppress the usual generic fallback.
If the fixture simply omits the authored-frame entry, Puppet
Knows-style code will still synthesize a fallback frame with
`:reframe-facts`, which is promotion-eligible. So this benchmark
needs an explicit `:no-authored-frame` path for situation B. The
system must eventually use a stored rationalization episode from
situation A for situation B to produce promotable material.

## Expected dynamics

### Phase 1 (cycles 1-8): authored sources dominate, episodes accumulate

- Situation A rationalization fires with authored frame, stores
  episodes with `:reframe-facts` as `:provisional`, promotion-eligible
- Situation B reversal fires with authored counterfactual, stores
  episodes with `:input-facts` as `:provisional`, promotion-eligible
- Roving fires for both situations using fixture seeds
- Dynamic episodes are stored but do not yet appear in candidate races

### Phase 2 (cycles 8-15): dynamic sources enter the candidate race

- A rationalization or reversal cycle in the OTHER situation retrieves
  a stored episode from phase 1 via coincidence marks (`:stage_light`
  bridge) AND the structural compatibility filters pass (shared
  `failed-goal-id`, retracted-fact overlap)
- The stored episode appears in the candidate race (trace shows
  "dynamic" origin). It may or may not win against authored sources.
- If selected as source, `note-family-plan-episode-use` fires and
  the episode accumulates use-history. This is the first real
  milestone: **dynamic source selected, use recorded.**
- The use is recorded as `:pending` initially. Outcome resolution
  happens through the existing evidence chain, not automatically at
  selection time. For same-family stored-source reuse, this usually
  means Level 2 only: the use record exists, but it does not itself
  create promotion evidence unless the branch immediately
  contradicts or backfires.

### Phase 3 (cycles 15-20): evidence accumulates, membrane may move

- Level 3 is only possible if the fixture also produces a real
  cross-family evidence path in addition to the same-family stored
  source races above. Same-family stored-source reuse alone does not
  promote. The most plausible bridge is roving or another family path
  retrieving a stored rationalization/reversal episode and resolving
  it as `:cross-family-use-success`.
- If the soak does produce repeated cross-family success evidence
  (threshold: 2), `reconcile-episode-admission` can promote from
  `:provisional` to `:durable`
- That promotion may then open a seeded authored-frontier rule
- Anti-residue flags may fire if same-family reuse exceeds threshold
- Same-family-loop or stale flags would demonstrate the membrane
  defending against grooves

Note: promotion is not guaranteed in 20 cycles. The fixture is
designed so the structural conditions for dynamic source competition
exist, but whether the pressure model also routes enough real
cross-family reuse to hit the promotion threshold depends on the
actual dynamics. The success criteria below reflect this.

## Success criteria (staged)

### Level 1 — dynamic candidates visible (minimum pass)

The candidate-race trace shows at least one dynamic source candidate
in a rationalization or reversal race. This means the retrieval +
structural compatibility chain works end to end.

### Level 2 — use-history recorded (strong pass)

At least one stored family-plan episode has `use-history` with one
or more records. This means `note-family-plan-episode-use` fired
in a live cycle, not just in the unit test.

### Level 3 — membrane state moves (full pass)

Any of:
- A promotion from `:provisional` to `:durable` fires
- A demotion fires (`:stale`, `:backfired`, `:contradicted`)
- A `:same-family-loop` flag fires
- A frontier rule changes status

### Level 4 — full loop (stretch)

- An episode promotes, that promotion opens a frontier rule, and a
  later cycle routes through the newly opened rule
- This is unlikely in 20 cycles but would close the full loop

## Implementation notes

### File structure

```
kernel/src/daydreamer/benchmarks/membrane_fixture.clj
kernel/test/daydreamer/benchmarks/membrane_fixture_test.clj
```

### Reuse from puppet_knows_autonomous.clj

Most of the autonomous cycle machinery is reusable:

- `run-autonomous-cycle` (the core loop)
- `compute-goal-candidates`, `sync-goals`, `apply-family-plan`
- `retrieve-node-hits`, `choose-node`, `score-node`
- `compute-active-indices`, `advance-situations`
- `summary-line`, `runtime-thought-packet`, `director-cycle-input`
- All the pressure/decay/saturation logic

What's different:

- The fixture is defined in code (not loaded from YAML/JSON)
- Only 2 situations, 8 nodes, 8 edges
- Authored coverage is deliberately incomplete
- The reversal setup is for situation B only
- The rationalization frames map is situation A only

### Copy a minimal slice, don't extract

`puppet_knows_autonomous.clj` is 1100+ LOC mixing fixture loading,
scoring, director/runtime-thought shaping, and benchmark I/O.
Extracting a shared namespace is a real refactor. Copy the minimal
subset needed (cycle machinery, scoring, pressure/decay) and leave
the shared extraction for the decomposition pass.

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
cd kernel && bb nrepl                    # start canonical nREPL on 7888
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
