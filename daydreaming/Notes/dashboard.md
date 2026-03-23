# Conducted Daydreaming — Project Dashboard

Last updated: 2026-03-22

---

## What This Is

A cognitive architecture for persistent inner life. A Clojure kernel
maintains structural cognition (rules, episodic memory, contexts,
concerns, emotions). LLMs provide content and evaluation at typed call
sites within the kernel's structural loops. The system accumulates
across sessions — episodes grow, rules can be created, the connection
graph gets richer, creative capacity compounds through operation.

The first creative brief is Graffito (Mark Friedberg's script). The
first benchmark is Puppet Knows. The first proof of accumulation:
runtime thought writeback changes the kernel's trace structurally
(gate 2 passed 2026-03-21).

## The Architecture

```
Conductor (human)         APC Mini faders + pads bias the Dreamer
    ↓
Kernel (Clojure)          Mueller's cognitive architecture:
                          concerns, operators, memory, pressure,
                          rule connection graph, episodic retrieval,
                          reminding cascade, context tree
    ↕
LLM edges                 runtime thought beats (inner-life content)
                          director feedback (scene-level events)
                          episode evaluation (realism/desirability)
    ↓
Stage                     narration + audio (Lyria RT) + visuals (Scope, deferred)
```

The kernel owns loops, recursion, state changes, and persistence.
LLMs provide judgment at bounded call sites. The dominant integration
pattern is LLM-as-evaluator (10 of 19 mechanisms), not
LLM-as-content-generator (only 3).

## What Success Looks Like

**The falsification criterion:** if a new rule or path discovered in
session 1 does not change reachable behavior in session 2 without
re-pasting the trace, we do not yet have anything beyond strong
memory-augmented generation.

**The evaluation ladder:**

1. Traceable rule firing with provenance — **Done**
2. Intervention sensitivity (delete a rule, behavior degrades) — **Done**
3. Bridge discovery (2-4 hop non-obvious paths) — **Candidate paths done, verification not yet**
4. Cross-session write-back transfer — **Not yet (north star)**

**Sprint gate 2 passed:** runtime thought writeback changes the live
kernel trace. Cycles 4-12 diverge from baseline across Haiku, Sonnet,
and hybrid routing. Different models produce different downstream
retrieval/selection behavior.

---

## Phase Map

```
DONE
  ├── Mueller condensation (19 mechanism cards, chain traces, cross-cut)
  ├── Rule engine substrate (RuleV1, matcher, unifier, connection graph)
  ├── Graphable family rules (trigger/activation/planning pairs)
  ├── Runtime thought writeback (gate 2 passed — traces diverge)
  ├── Memory ecology first pass (admission tiers, cue zones, provenance caps)
  ├── Provenance-weighted retrieval (shorter=stronger, same-family capped)
  ├── Conductor extraction (general session orchestration)
  └── Earlier: L3 scheduler, generation pipeline, supply pilot, bridge tests

CURRENT — three parallel lanes

Lane 1: Sprint (RuntimeThoughtBeatV1)             ← active project target
  ├── Writeback probe passed gate 2
  ├── Haiku/Sonnet/Hybrid comparison done
  └── Next: narration legibility, Lyria integration

Lane 2: Deep Build (kernel rule engine)            ← where the architecture lives
  ├── Memory ecology: promotion logic, anti-residue, accessibility frontier
  ├── Executor boundary: execute-rule in rules.clj, declarative effects
  ├── Puppet Knows re-entry: 12-cycle clean, 50-cycle partial but mixed after pressure retune
  ├── Membrane assays: Assay A (dynamic entry + loop flag), Assay B (live promotion/frontier open)
  ├── First :llm-backed pilot as episode evaluator
  └── See build-order-checkpoint-2026-03-22.md

Lane 3: Research                                   ← 5 Pro / 5 Thinking reviews
  ├── Code reviews 03-11 shaped the build order
  ├── Mueller-to-kernel mapping verified against source
  └── Ongoing prompts for promotion/accessibility/executor design

NEXT
  ├── Verified path layer (status lattice: candidate → projection-verified → episode-constructed)
  ├── Generic :clojure-fn executor dispatch
  ├── More graph density (evaluation facts, retrieval events, critic signals)
  └── First :llm-backed content generation rule

LATER
  ├── Full serendipity search (paths through heterogeneous graph)
  ├── Cross-session persistence layer
  ├── Scope rendering (when GPUs available)
  └── Show Mark Friedberg something
```

---

## Deep Build Order (settled 2026-03-22)

From 5 code reviews + Mueller source verification:

1. **Memory ecology** — ACTIVE. Promotion from :provisional to :durable.
   Anti-residue flags. Rule accessibility frontier. Episode-use attribution.
2. **Executor boundary** — declarative effects in rules.clj, kernel-owned
   application, schema + denotation validation.
3. **:llm-backed evaluator pilot** — post-production realism/desirability
   scoring. Gates promotion. Not generator.
4. **Verified paths** — progressive binding over candidate paths.
   Three stages: :bound → :supported → :constructed.
5. **Generic :clojure-fn dispatch** — roving first, rationalization second,
   reversal last.

See `condensation/build-order-checkpoint-2026-03-22.md` for full details.

---

## Kernel Status

### What exists (Clojure, tested)

| Component | Status | Tests |
|-----------|--------|-------|
| Control loop (decay, selection, mode) | Done | Yes |
| Context tree (sprout, backtrack, pseudo-sprout) | Done | Yes |
| Episodic memory (threshold retrieval, reminding) | Done | Yes |
| Goals and concerns | Done | Yes |
| RuleV1 validation + matcher + graph | Done | Yes |
| Graphable family rules (roving, rationalization) | Done | Yes |
| Provenance-weighted retrieval | Done | Yes |
| Cross-family bridges and self-reuse | Done | Yes |
| Admission tiers (trace/provisional) | Done (first pass) | Yes |
| Cue zone separation | Done (first pass) | Yes |
| Runtime thought + writeback | Done | Comparison run |
| Conductor (general session loop) | Done | Benchmark parity |
| Director (Gemini feedback) | Done | Puppet Knows |

### What's missing

| Component | Priority | Blocked by |
|-----------|----------|------------|
| Benchmark re-entry with visible membrane dynamics | High | Benchmark behavior still too narrow |
| execute-rule in rules.clj | Medium | Nothing |
| :llm-backed evaluator | Medium | Executor boundary |
| Verified paths | Lower | Graph heterogeneity |
| Generic :clojure-fn dispatch | Lower | Executor boundary |
| Inference fixpoint loop | Lower | Generic dispatch |
| Full serendipity search | Later | Verified paths + graph density |
| Analogical planning | Later | Planner + episodes |

---

## Earlier Work (still valid, less central)

### Generation Pipeline
- Four benchmark fixtures (Kai, Tessa, Maren, Rhea)
- Batch generation + admission + keeper bank
- Supply pilot passed (Tessa clean, Kai narrow)
- Bridge tests passed (traversal + human read)

### L3 Traversal
- Graffito pilot validated (feature arm beats baseline)
- City Routes three-arm comparison done
- DODM feature registry exists

### Performance Surface
- APC Mini → ScopeDRD → Scope pipeline designed
- Lyria RT integration designed
- Narration bridge exists

---

## Milestones

| Milestone | Status | Date |
|-----------|--------|------|
| L3 scheduler validated | Done | 2026-03-13 |
| Generation prototype validated | Done | 2026-03-14 |
| Bridge tests passed | Done | 2026-03-15 |
| Supply pilot passed | Done | 2026-03-15 |
| Mueller condensation complete | Done | 2026-03-20 |
| Rule engine substrate built | Done | 2026-03-21 |
| RuntimeThoughtBeatV1 writeback (gate 2) | Done | 2026-03-21 |
| Memory ecology first pass | Done | 2026-03-22 |
| Connection graph with real edges | Done | 2026-03-22 |
| Code reviews shaped build order | Done | 2026-03-22 |
| Mueller-to-kernel mapping verified | Done | 2026-03-22 |
| Promotion + anti-residue + accessibility | Substantially real | 2026-03-22 |
| Executor boundary (execute-rule) | Substantially real | 2026-03-22 |
| Puppet Knows benchmark re-entry | Partial | 2026-03-22 |
| Membrane fixture benchmark (Assay A: L1/L2 + first L3 flag) | Done | 2026-03-22 |
| Membrane positive-path benchmark (Assay B) | Done | 2026-03-22 |
| First :llm-backed evaluator | Not started | |
| Verified paths | Not started | |
| Cross-session write-back transfer (L4) | Not started | |

---

## Collaborators

| Who | Role | Status |
|-----|------|--------|
| Mark Friedberg | Graffito creative partner | Not yet shown anything |
| John | Evaluation, conductor mapping, membrane design | Questions prepared |
