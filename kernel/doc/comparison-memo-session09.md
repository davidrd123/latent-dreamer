# Comparison Memo: Scripted vs Semi-Unscripted Puppet Knows

**Date:** 2026-03-12 (session 09)
**Traces:** `out/puppet_knows_benchmark.json` vs `out/puppet_knows_semi_unscripted.json`
**Comparison artifact:** `out/puppet_knows_compare.html`

---

## Summary

**Update:** The propagation gap identified below has been closed by the
branch-fact adapter (`puppet_knows_adapter.clj`). The semi-unscripted run now
reaches `s4_the_ring` / `n10_honest_ring` at cycle 10 via derived situation
state — the kernel's reversal branch counterfactual facts drive the arrival
instead of fixture scripting. See "Post-Adapter Result" at the end.

### Original finding (pre-adapter)

Family/control divergence exists. Director-level arrival does not yet.

The semi-unscripted run self-activates REVERSAL and ROVING, and selects
reversal at cycle 10 instead of rehearsal. That is real control-layer
divergence — the kernel makes a different decision about *what to think about
next*. But the divergence does not propagate into changed situations, changed
active indices, or a changed chosen node. Cycle 10 stays in `s1_seeing_through`
with `chosen_node_id: n/a`. The scripted run arrives at `s4_the_ring` /
`n10_honest_ring`. The architecture is changing trajectory, but not yet
completing the artistic bridge.

---

## Cycle-by-Cycle Comparison

### Cycle 8

| Field | Scripted | Semi-Unscripted |
|-------|----------|-----------------|
| Selected goal | repercussions (g-3, 0.86) | repercussions (g-5, 0.86) |
| Activations | — | **reversal (g-10) + roving (g-12) auto-activated** |
| Reversal fires? | Yes (fixture `:reversal-branch`) | No (activation only, plan deferred) |
| Sprouted | cx-10 (reversal branch) | — |
| Chosen node | n08_inventory_dominos | **n/a** |
| Active indices | edge, void, backstage, stored_scenery, consequence, darkness | *identical* |
| Situations | 4 situations with activation/ripeness/emotion values | *identical* |

**What diverges:** The scripted run fires REVERSAL *during* cycle 8 via the
`:reversal-branch` script key, sprouting cx-10 immediately. The semi-unscripted
run auto-activates reversal and roving *goals* at cycle 8 but defers their
plan execution to when they win goal competition. The scripted run also
provides `chosen-node-id`; the semi-unscripted run provides no node.

### Cycle 9

| Field | Scripted | Semi-Unscripted |
|-------|----------|-----------------|
| Selected goal | revenge (g-7, 0.82) | revenge (g-7, 0.82) |
| Activations | — | — |
| Chosen node | n09_tear_the_set | **n/a** |
| Active indices | ritual, combat, honesty, crowd, seam, anger, performance | *identical* |
| Situations | 4 situations | *identical* |

**No divergence** in goal selection or situations. Both select revenge.
Divergence only in `chosen_node_id` (fixture-provided vs absent).

### Cycle 10 — the divergence point

| Field | Scripted | Semi-Unscripted |
|-------|----------|-----------------|
| **Selected goal** | **rehearsal (g-9, 0.42, real)** | **reversal (g-10, 0.74, imaginary)** |
| **Situation** | **s4_the_ring** | **s1_seeing_through** |
| **Chosen node** | **n10_honest_ring** | **n/a** |
| Selection policy | feedback_woke_ring | highest_strength |
| Edge kind | association | — |
| Reversal fires? | — | Yes (auto-family-plan, reverse-leafs) |
| Sprouted | — | cx-16 (reversal branch) |
| Top candidates | rehearsal (0.42) only | reversal (0.74), **roving (0.74)** |
| Active indices | ritual, honesty, crowd, performance, sincerity, non_directed_light | *identical* |
| Situations | 4 situations | *identical* |

**What diverges:** Goal selection. The scripted run has `rehearsal` at the top
because it was the only remaining unexecuted goal. The semi-unscripted run has
`reversal` (auto-activated at cycle 8, strength 0.74) competing against
`roving` (also 0.74) and beating `rehearsal` (0.42). Reversal wins and fires
its full branch pipeline: walks INTENDS chains, finds the weak leaf
(g_fixture_old_leaf, strength 0.32), retracts the leaf objective, derives
counterfactual facts (performance_is_admitted, s4_the_ring), and sprouts cx-16.

**What does NOT diverge:** Situations, active indices, and chosen node. All
three are fixture-provided in both runs. The semi-unscripted run simply has no
`chosen-node-id` because no fixture provides one.

---

## Why the Divergence Stops at the Control Layer

The reversal branch (cx-16) contains counterfactual facts — `performance_is_admitted`
and a situation fact for `s4_the_ring`. In the scripted run, these facts are the
*reason* the Director arrives at s4_the_ring / n10_honest_ring: the counterfactual
"what if performance were admitted?" changes the emotional landscape, waking the
ring situation and enabling the honest_ring node.

But in the semi-unscripted run, nothing reads the counterfactual facts to update
situation activation or index selection. The fixture still provides the same
situations and active indices regardless of which goal was selected. The
counterfactual facts exist in cx-16 but have no downstream consumer.

The propagation gap:

```
REVERSAL fires → sprouts cx-16 → counterfactual facts exist
    ↓
[MISSING: counterfactual facts → changed situation activation]
    ↓
[MISSING: changed situation activation → changed active indices]
    ↓
[MISSING: changed active indices → changed node selection]
    ↓
n10_honest_ring (or something the kernel derives)
```

Each step in this chain is currently fixture-provided in the scripted run:
- Situations are hardcoded per cycle script
- Active indices are hardcoded per cycle script
- Chosen node is hardcoded per cycle script

---

## ROVING Observation

ROVING auto-activates at cycle 8 alongside REVERSAL (same trigger: failed goal
g_fixture_old_failure + negative emotion e_fixture_apparatus_dread at 0.74). At
cycle 10, roving (g-12) appears as a top candidate at strength 0.74, tied with
reversal. Reversal wins the tiebreak.

Even if roving had won, its current implementation (sprout a side-channel
context, seed a pleasant episode, run reminding cascade) would face the same
propagation gap: the reminded episodes don't flow into changed situation
activation or index selection.

ROVING is not materially changing the benchmark trace. A second fixture, when
built, should probably be roving-heavy rather than another reversal case —
otherwise we'd mostly retest the same mechanism.

---

## What This Means for the Next Pass

The architecture works at the planning layer. The research question "does
Mueller's machinery produce different behavior?" has a partial yes: the kernel
makes a different goal selection. But the *artistic* consequence — arriving at
a different node, in a different situation, with different emotional color —
requires the propagation chain above.

The next bounded pass should answer: **how does a selected reversal branch's
counterfactual facts change the next visible situation/node?** This is not
about de-scripting timestamps or broad fixture plumbing. It's about the
specific bridge from counterfactual facts to situation activation.

Mueller's approach to this (which Codex audited in
`mueller_context_propagation_audit.md`) turns out to be indirect: Mueller has
no separate situation-activation mechanism. Instead, branch-local facts drive
rule firing, which drives goal activation and episodic retrieval. The kernel's
`:situations` map is a conducted-system invention, not a Mueller concept.

---

## Post-Adapter Result

The branch-fact adapter (`puppet_knows_adapter.clj`) closes the propagation
gap for cycle 10. The adapter:

1. Reads `visible-facts` from the REVERSAL branch context (cx-16)
2. Filters to `:situation` and `:counterfactual` fact types
3. Maps bridge facts (`:s4_the_ring`, `:performance_is_admitted`) to situation
   deltas and active indices
4. Derives node selection from the updated situation/index state

### Semi-unscripted cycle 10 now:

| Field | Before adapter | After adapter |
|-------|---------------|---------------|
| Selected goal | reversal (g-10, 0.74) | reversal (g-10, 0.74) |
| Situation | s1_seeing_through | **s4_the_ring** |
| Chosen node | n/a | **n10_honest_ring** |
| Active indices | (fixture) ritual, honesty, crowd, performance, sincerity, non_directed_light | **(derived)** ritual, sincerity, non_directed_light, performance, honesty |
| s4_the_ring activation | 0.15 (from cycle 9) | **0.49** (+0.34 from branch facts) |
| s1_seeing_through activation | 0.55 (from cycle 9) | **0.42** (-0.13 from branch facts) |
| Edge kind | — | **counterfactual_bridge** |
| Adapter policy | — | **branch_visible_facts** |

### What this proves

The semi-unscripted run now completes the full propagation chain:

```
failed goal + negative emotion → REVERSAL auto-activates (cycle 8)
    → reversal wins goal competition (cycle 10)
    → reverse-leafs finds weak leaf, derives counterfactual facts
    → sprouts alternative-past branch (cx-16)
    → adapter reads branch facts: performance_is_admitted + s4_the_ring
    → s4_the_ring activation jumps, s1_seeing_through drops
    → indices shift to performance/honesty/ritual/sincerity
    → retrieval matches n10_honest_ring
```

The kernel autonomously arrives at the same artistic destination (honest ring)
through a different cognitive path (reversal-driven counterfactual bridge
instead of fixture-scripted feedback-woke-ring).

### Remaining scripted surface

Cycles 8 and 9 are still fixture-driven for situations, active indices, and
chosen nodes. The adapter only fires on cycle 10. Timestamps and termination
are also fixture-provided at all cycles. Whether to de-script these or build a
second fixture is the next design decision.

### ROVING status

ROVING still fires but doesn't materially change the trace. A second fixture
should be roving-heavy to test whether the same adapter pattern generalizes.
