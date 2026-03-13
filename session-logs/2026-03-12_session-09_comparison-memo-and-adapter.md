# Session 09: Comparison Memo, Mueller Audit, and Branch-Fact Adapter

**Date:** 2026-03-12
**Focus:** Trace comparison analysis, Mueller context propagation audit, branch-fact adapter for situation/node derivation
**Agents:** Claude Opus (comparison memo, code review, orchestration), Codex (Mueller audit, adapter implementation, review fixes)

---

## What Happened

### 1. Comparison Memo

Claude read both benchmark traces (`puppet_knows_benchmark.json` and
`puppet_knows_semi_unscripted.json`) cycle by cycle and wrote a detailed
comparison memo at `kernel/doc/comparison-memo-session09.md`.

Key finding (pre-adapter): the semi-unscripted run produces real control-layer
divergence at cycle 10 (reversal at 0.74 beats rehearsal at 0.42), but the
divergence stops there. Situations, active indices, and chosen node are
identical across both runs because they're all fixture-provided. The reversal
branch's counterfactual facts (`:performance_is_admitted`, `:s4_the_ring`)
exist in cx-16 but have no downstream consumer.

### 2. Mueller Context Propagation Audit (Codex)

In parallel with the comparison memo, Codex audited Mueller's source to answer:
"how does a planning-context change propagate into changed later salience?"

Result at `kernel/doc/reference/mueller_context_propagation_audit.md`:

- Mueller has no separate "situation activation" mechanism to port
- Propagation is indirect: context-local facts → GATE visibility → rule firing
  → changed behavior
- The kernel's `:situations` map is a conducted-system invention, not Mueller
- The missing bridge is "derive situation/node salience from branch-visible
  facts," not "find Mueller's situation updater"

Sources checked: `dd_cntrl.cl` (control loop, next-context selection),
`gate_cx.cl` (fact visibility, sprout vs pseudo-sprout), `dd_reversal.cl`
(counterfactual input state assertion), `dd_rule1.cl` and `dd_kb.cl` (theme
rule firing from visible facts), `dd_epis.cl` (activation-context scoping of
episodic indexing).

### 3. Branch-Fact Adapter (Codex)

Codex built `puppet_knows_adapter.clj` — a bounded adapter that translates
REVERSAL branch-visible facts into Puppet Knows situation and node state.

The adapter:
1. Reads `visible-facts` from the reversal branch context
2. Filters to `:situation` and `:counterfactual` fact types
3. Maps bridge facts to situation metric deltas (`:s4_the_ring` → +0.24
   activation, +0.13 ripeness, +0.12 hope; `:performance_is_admitted` → +0.10
   activation, +0.07 ripeness, +0.09 hope)
4. Derives active indices from bridge facts (`:s4_the_ring` → ritual,
   sincerity, non_directed_light; `:performance_is_admitted` → performance,
   honesty)
5. Checks for honest-ring arrival (s4_the_ring + honesty/performance/ritual
   overlap) and derives node selection

Runner integration: `:cycle-adapter` hook in `run-scripted-cycle`, called after
family plans and before sprouts. Pure function `(fn [world goal-id script])` →
world.

Semi-unscripted cycle 10 now emits `chosen_node_id: n10_honest_ring` with
`situation_id: s4_the_ring`, `edge_kind: counterfactual_bridge`.

### 4. Code Review

Claude ran a code review on the adapter and runner hook. Three issues found:

1. **`fact-id` leaked goal/emotion IDs into trace** — fallback chain
   `(:fact/id fact) → (:goal-id fact) → (:emotion-id fact)` included
   non-bridge facts. Fixed: now filters by `#{:situation :counterfactual}`
   fact types.
2. **`derived-situation-id` sort-by fragile** — opaque composition chain with
   `ffirst` over MapEntry. Fixed: explicit destructuring in sort key function.
3. **Silent no-op when no branch context** — adapter returned `world`
   unchanged with no trace annotation. Fixed: now annotates trace with
   `:adapter_policy :no_branch_context`.

All three fixed by Codex. Final state: 63 tests, 319 assertions, 0 failures.

### 5. Trace Artifacts Regenerated

`bb puppet-knows-compare` regenerated all five output artifacts:
- `out/puppet_knows_benchmark.json` / `.html`
- `out/puppet_knows_semi_unscripted.json` / `.html`
- `out/puppet_knows_compare.html`

---

## Key Result

The semi-unscripted run now completes the full propagation chain:

```
failed goal + negative emotion
  → REVERSAL auto-activates (cycle 8)
  → reversal wins goal competition (cycle 10)
  → reverse-leafs finds weak leaf, derives counterfactual facts
  → sprouts alternative-past branch (cx-16)
  → adapter reads branch facts: performance_is_admitted + s4_the_ring
  → s4_the_ring activation jumps 0.15 → 0.49
  → s1_seeing_through activation drops 0.55 → 0.42
  → indices shift to performance/honesty/ritual/sincerity
  → retrieval matches n10_honest_ring
```

The kernel autonomously arrives at the same artistic destination (honest ring)
through a different cognitive path (reversal-driven counterfactual bridge
instead of fixture-scripted feedback-woke-ring).

---

## Files Changed

### New files
- `kernel/src/daydreamer/benchmarks/puppet_knows_adapter.clj` — branch-fact → situation/node adapter
- `kernel/test/daydreamer/benchmarks/puppet_knows_adapter_test.clj` — adapter tests
- `kernel/doc/comparison-memo-session09.md` — trace comparison analysis
- `kernel/doc/reference/mueller_context_propagation_audit.md` — Mueller source audit

### Modified files
- `kernel/src/daydreamer/runner.clj` — added `:cycle-adapter` hook (`apply-cycle-adapter`)
- `kernel/src/daydreamer/benchmarks/puppet_knows.clj` — cycle 10 semi-unscripted uses adapter instead of hardcoded node/indices
- `kernel/test/daydreamer/runner_test.clj` — adapter hook tests
- `kernel/test/daydreamer/benchmarks/puppet_knows_test.clj` — updated assertions for adapter output

---

## Design Decisions

1. **Adapter lives in `benchmarks/`, not kernel core** — the kernel doesn't
   know about "situations" or "nodes." Those are conducted-system concepts.
   The adapter is the translation layer between Mueller's branch-fact model
   and the Director's situation vocabulary.

2. **`:cycle-adapter` is a narrow hook, not a general plugin system** — the
   runner calls it with `(cycle-adapter world goal-id script)` after family
   plans and before sprouts. It returns bare `world`, not `[world value]`.
   This is intentionally minimal.

3. **Situation deltas are hardcoded in the adapter** — the delta tables
   (`branch-fact->situation-deltas`, `branch-fact->indices`) encode Puppet
   Knows-specific knowledge. A second fixture would need its own adapter or a
   generalized version.

4. **Adapter traces its own decisions** — `:adapter_policy`,
   `:adapter_branch_context`, `:adapter_visible_fact_ids`,
   `:adapter_selected_situation`, `:adapter_active_indices` all appear in the
   cycle trace for debuggability.

---

## Research Question Status

> Does Mueller's full machinery produce behaviors the Python engine can't reach?

**Stronger answer: yes.** The kernel now autonomously:
- Self-triggers REVERSAL from failed-goal + negative-emotion state
- Wins goal competition over scripted alternatives (rehearsal)
- Derives counterfactual facts from stored failure causes
- Propagates those facts into changed situation activation and node selection
- Arrives at the artistic destination (honest ring) through a cognitive path
  the Python engine cannot express

The Python engine's flat scheduler has no mechanism for "I failed, therefore
imagine an alternative past, therefore the ring situation becomes salient."

**Not yet proven:**
- Whether this generalizes beyond Puppet Knows (needs second fixture)
- Whether ROVING produces meaningful trace changes (fires but dormant)
- Whether cycles 8-9 can be further de-scripted
- Whether a fully unscripted run (no fixture situations at all) is feasible

---

## Stats

- **63 tests, 319 assertions, 0 failures**
- Source: ~2,750 lines (kernel src + adapter)
- Tests: ~1,900 lines

---

## Next Session Plan

1. **Second fixture (roving-heavy)** — test whether the architecture
   generalizes. ROVING is firing but not materially changing the benchmark
   trace. A roving-heavy scenario would test both the ROVING mechanism and
   whether the adapter pattern generalizes.
2. **De-script cycles 8-9** — optional: reduce fixture surface on existing
   benchmark. Lower priority than the second fixture.
3. **Consider fixture loader** — `world.yaml` / `dream_graph.json` parsing
   would let the kernel read real fixture files instead of hardcoded specs.
