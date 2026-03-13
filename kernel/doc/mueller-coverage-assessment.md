# Mueller DAYDREAMER Coverage Assessment

**Date:** 2026-03-13 (session 10)
**Kernel state:** 78 tests, 487 assertions, 3 benchmarks + autonomous trace

---

## Coverage by Layer

| Layer | Mueller | Kernel | Coverage | Gap |
|-------|---------|--------|----------|-----|
| **Contexts** | Hierarchical sprouting, GATE visibility, pseudo-sprouts | Sprouting, visible-facts, branch assertions | ~70% | No GATE pseudo-sprouts (read-only views), no ancestry-scoped visibility |
| **Goals** | Activation, competition, strength decay, next-context | Activation, competition, strength, next-context | ~85% | Mostly faithful |
| **Control** | Next-context selection, mode oscillation with strike counting | Goal selection, family dispatch | ~55% | No mode oscillation (performance ↔ daydreaming with strikes) |
| **Episodic memory** | Coincidence marks, reminding, serendipity threshold | Marks, reminding, serendipity-bias field | ~60% | No serendipity pipeline (rule intersection → threshold decrement) |
| **Rule engine** | Forward-chaining rules, theme rules, PRESERVATION, LEADTO | None | 0% | The fundamental gap |
| **Emotions** | Per-emotion per-goal, 0.95 decay, GC below 0.15, divert-emot-to-tlg | Per-situation metrics, bounded divert-emot bridge | ~25% | No per-emotion decay, no GC, no emotion-goal linkage |
| **REVERSAL** | sprout-alternative-past, reverse-leafs, reverse-alterns, reverse-undo-causes via PRESERVATION rules | sprout-alternative-past, reverse-leafs, reverse-undo-causes via stored facts | ~65% | Missing reverse-alterns, rule-derived undo-causes |
| **ROVING** | Theme rule trigger, episode seed, reminding cascade | Threshold trigger, episode seed, reminding | ~80% | Faithful (simplest family) |
| **RATIONALIZATION** | LEADTO/minimization chain derivation, emotional diversion | Stored frames with priority, bounded emotional diversion | ~40% | No LEADTO chain derivation |
| **RECOVERY** | Retry/repair failed goals | Not implemented | 0% | — |
| **REHEARSAL** | Practice anticipated future | Goal type exists, no family plan body | ~10% | — |
| **Serendipity** | Bidirectional BFS through rule network (max 5 hops), threshold decrement | Bias field only | ~5% | No rule intersection |
| **Mutations** | Plan exhaustion → action/permutation/substitution | Not implemented | 0% | — |

---

## Aggregate Estimate

- Core architecture (contexts, goals, control, episodic): **~65%**
- Goal families (3 of 5, each as bridges): **~40%**
- Rule engine: **0%**
- Emotional system: **~25%**
- Serendipity/mutation pipeline: **~5%**
- **Overall: ~40% of Mueller's full system**

---

## The Fundamental Gap: Rules

Mueller's DAYDREAMER is a forward-chaining rule system. Rules make everything
else work:

- **Theme rules** activate goal families from emotional state
- **PRESERVATION rules** derive what to undo in reverse-undo-causes
- **LEADTO rules** derive rationalization chains
- **Rule intersection** creates serendipity (two rules sharing a variable →
  unexpected connection)

Every "bridge" in the kernel is a hardcoded shortcut for what rules would
derive dynamically:

| Kernel Bridge | What Rules Would Do |
|---------------|---------------------|
| Stored `:failure-cause` facts | PRESERVATION rule synthesis at runtime |
| Stored `:rationalization-frame` facts with priority | LEADTO/minimization chain derivation |
| `serendipity-bias` scalar | Rule intersection BFS (max 5 hops) |
| `branch-fact->situation-deltas` tables | GATE visibility → rule firing → changed behavior |

The bridges produce Mueller-like *behavior*. They do not produce Mueller-like
*generativity*. A new scenario requires new hardcoded facts; Mueller's system
would derive them.

---

## What the Kernel Has That Mueller Didn't

- **Adapter pattern** — translating branch facts into conducted-system
  situation/node vocabulary. Mueller had no "situations" or "nodes."
- **Situation-level emotional landscape** — Mueller had per-emotion atoms,
  not a spatial field of situations with activation/ripeness/hope/threat.
- **Autonomous runner** — scores goal candidates from situation pressure,
  reads real fixture files (world.yaml, dream_graph.json).
- **Trace/visualizer infrastructure** — JSON trace export, HTML comparison
  reports, cognitive trace visualizer with animated playback.
- **Three benchmark fixtures** — Puppet Knows (REVERSAL), Arctic (ROVING),
  Stalker Zone (RATIONALIZATION) proving generalization.

---

## What This Means for Conducted Daydreaming

The kernel has proven the **architectural thesis**: goal competition,
counterfactual branching, and emotional diversion produce cognitive paths a
flat scheduler cannot express.

For the conducted daydreaming use case, the bridges may be **sufficient** —
authored scenarios provide the frames and facts that rules would otherwise
derive. The rule engine becomes necessary only for:

1. **Open-ended generalization** — scenarios nobody wrote frames for
2. **Serendipity** — unexpected connections between unrelated rules
3. **Full reverse-undo-causes** — deriving what to undo from the planning
   tree rather than stored facts

The missing 60% is mostly generativity machinery (rules, mutations,
serendipity) that matters for open-ended AI research but may not be required
for conducted performance where the creative brief provides the scenario
structure.

---

## Faithful / Bridge / Missing Summary

### Faithful
- sprout-alternative-past (dd_reversal.cl)
- reverse-leafs — INTENDS chain walk, strength < 0.5, multi-branch, objective retraction
- reversal-sprout-alternative
- ROVING theme rule (neg emotion > 0.04 from failed goal)
- ROVING plan body (sprout, episode seed, epmem-reminding)
- RATIONALIZATION activation (neg emotion > 0.7 + stored frame)

### Bridge (honest gap)
- reverse-undo-causes — stored :failure-cause facts, NOT Mueller's runtime
  PRESERVATION rule synthesis
- RATIONALIZATION plan — stored frames with priority, NOT Mueller's
  LEADTO/minimization chain derivation
- divert-emot-to-tlg — bounded emotional update (0.35 * frame-priority),
  not Mueller's full emotional redirection
- Branch-fact adapter — conducted-system invention, not Mueller
- Situation-level emotions — not Mueller's per-emotion-per-goal model

### Missing
- Rule engine (forward-chaining, theme, PRESERVATION, LEADTO)
- Rule intersection / serendipity pipeline
- GATE pseudo-sprouts / ancestry-scoped visibility
- reverse-alterns — walking activation→termination planning path
- RECOVERY family
- REHEARSAL family plan body
- Mode oscillation (performance ↔ daydreaming with strike counting)
- Per-emotion decay (0.95 factor) and garbage collection (< 0.15)
- Plan exhaustion → mutation pipeline (action/permutation/substitution)
