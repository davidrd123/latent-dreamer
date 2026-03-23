# Rule Engine Trajectory: From Substrate to Creative Engine

This document explains the full arc of the kernel rule engine — where it started, what exists now, what the next steps are, and where it's headed. It connects the condensation work (the "what Mueller built" layer) to the implementation (the "what the kernel does" layer) to the project goals (the "why this matters" layer).

Status note, 2026-03-22: the original step sequence in this document is now
historical. The live ordering is maintained in
`build-order-checkpoint-2026-03-22.md`. The important update is that memory
ecology now comes before verified paths and before generic executor dispatch.
This file remains useful for the long arc; use the checkpoint for current
ordering.

## Where we started

Before this work, the kernel had:

- `control.clj` — the top-level cycle: decay, goal selection, mode oscillation
- `context.clj` — immutable context tree with sprouting and backtracking
- `episodic_memory.clj` — episodes stored under indices, threshold retrieval, reminding cascade
- `goals.clj` — goal objects with status, strength, planning type
- `goal_families.clj` — 1068 lines of hand-coded activation logic, plan bodies, and support infrastructure

The gap: `goal_families.clj` was openly a substitute for the missing rule engine. Its own docstrings said things like "this is an honest bridge for the missing rule engine" and "this approximates Mueller's Theme rules as a pure pre-competition pass." It hard-coded which families activate, how they activate, and what their plans do — all as procedural Clojure, not as searchable data.

The LLM edge was `director.clj` — one coarse Gemini call per scene that returned situation boosts and valence deltas. No typed interfaces, no judgment sites within mechanisms, no structural feedback into episodic memory.

## What the condensation revealed

The condensation extracted 19 mechanisms from Mueller's book and classified each one by:

- **Loop shape** — what stays deterministic in Clojure
- **Judgment points** — where an LLM could plug in
- **Accumulation story** — what persists and changes future behavior
- **Property to preserve** — what can't be lost when modernizing

The key architectural finding: the hybrid boundary is inside mechanisms, not between them. A rule can live in a persistent graph, be searchable, participate in serendipity intersection search — and invoke an LLM when it fires.

This led to the `RuleV1` schema with three layers:

- `:consequent-schema` — structural shape, graphable, declared up front
- `:denotation` — intended state change (catches schema-valid but cognitively wrong output)
- `:executor` — runtime behavior (`:instantiate`, `:clojure-fn`, or `:llm-backed`)

And the connection graph: edges derived from antecedent/consequent pattern overlap, structurally derived, not usage-weighted. A path never traversed is equally findable as one used a hundred times. This is the creative substrate.

## What exists now in the kernel

### New files

- `fact_query.clj` — shared fact predicates extracted from `goal_families.clj`
- `rules.clj` — the RuleV1 substrate:
  - Rule validation (all required fields including `:denotation`)
  - Logic variables (`?var`)
  - Custom unifier over plain maps (subset matching, variable binding)
  - Multi-pattern antecedent matching with cross-fact binding propagation
  - Template instantiation for consequent schemas
  - Connection graph construction (projection basis derivation, pairwise edge computation)
  - Rule instantiation for `:instantiate` executors
- `runtime_thought.clj` — impure LLM edge for thought beat generation and episodic writeback

### Changes to existing files

- `goal_families.clj` — refactored to use `fact-query` predicates and RuleV1 matching. Three family pairs are now graphable:
  - Activation layer: roving trigger/activation, rationalization trigger/activation
  - Planning layer: roving plan-request/plan-dispatch
  - Reversal activation uses the RuleV1 matcher
- `puppet_knows_autonomous.clj` — wired the runtime thought feedback loop into the benchmark cycle

### Test coverage

95 tests, 590 assertions, all passing. Tests cover rule validation, instantiation, multi-pattern matching with shared variables, graph cache derivation, connection graph construction with compatibility filtering, and the graphable trigger/activation/planning rule pairs.

### The connection graph

The kernel now has real graph edges:

```
:goal-family/roving-trigger         → :goal-family/roving-activation
:goal-family/rationalization-trigger → :goal-family/rationalization-activation
:goal-family/roving-plan-request    → :goal-family/roving-plan-dispatch
```

These are honest edges — they mediate real structural transitions inside the kernel, not just derived metadata. The activation edges span "should this family activate?" and the planning edge spans "what should the plan do?"

### The writeback experiment

The runtime thought feedback loop is working. `runtime_thought.clj:apply-feedback` creates a kernel episode from the LLM's beat residue (with `:realism :imaginary`, indices filtered against allowed set), stores it via `add-episode` + `store-episode` + `add-recent-index`. The comparison proved structural trace divergence: cycles 4-12 differ from no-feedback baseline across Haiku, Sonnet, and hybrid routing.

## What the rules span now

```
Facts in context
     │
     ▼
Trigger rules ──→ {:fact/type :goal-family-trigger}
                         │
                         ▼
              Activation rules ──→ Concern created
                                        │
                                        ▼
              Plan-request rules ──→ {:fact/type :family-plan-request}
                                        │
                                        ▼
              Plan-dispatch rules ──→ Plan body executes
```

Two lifecycle phases: activation and planning. The concern is created by rules and its plan is initiated by rules. The plan body itself is still procedural Clojure (sprout context, invoke reminding, assert facts).

## The build sequence going forward

The current order is:

1. Memory ecology
2. Executor boundary with declarative effects
3. LLM-backed evaluator pilot
4. Verified paths
5. Generic executor dispatch

### Step 1: Memory ecology

This is now the first active step, not a later cleanup. The kernel already has
real structural reuse, so the immediate risk is groove formation:
generated family-plan material becoming durable planner input too cheaply.

The current pass introduces:
- admission tiers (`:trace`, `:provisional`, later `:durable`)
- cue-zone separation (content / reminding / provenance / support)
- provenance floors before imagined material can reenter retrieval
- same-family fallback gated on promotion
- first durable-promotion substrate in place, with typed
  `:episode-promotion` facts asserted into branch contexts; raw roving
  reminding no longer counts as promotion evidence by itself

The remaining gap inside Step 1 is not "promotion exists or not."
The direct evaluator-only store-time promotion path is now gone.
The remaining issue is whether evaluator output stays gate/veto
rather than policy authority, and whether anti-residue signals can
demote or suppress loops cleanly.

The newer review stack sharpens this again: the missing substrate is
**episode use with attributed outcomes**. Promotion, anti-residue, and
later rule accessibility should all be downstream of:
- an episode being used
- by a named family / goal / context
- with a later resolved outcome

That is cleaner than treating promotion, demotion, and accessibility
as three separate mechanisms.

This is what prevents the graph from becoming a self-soothing loop machine.

The next honest landing after that substrate is an explicit dynamic
frontier:
- `world[:rule-access]`
- authored-core rules start `:accessible`
- planning reads an `:accessible` graph view
- serendipity reads an `:accessible ∪ :frontier` view
- durable promotion can open frontier rules
- hard-failure demotion can quarantine non-core rules
- `:goal-family/reversal-aftershock-to-rationalization` is now the
  first live `:authored-frontier` bridge, and cross-family trigger
  emission reads the serendipity-filtered access view instead of raw
  bridge lists

That keeps `build-connection-graph` structural while finally restoring
Mueller's missing "planner frontier" as state rather than graph essence.

### Step 2: Executor boundary with declarative effects

Only after the memory membrane exists does it make sense to make executors more
powerful. The next architectural step is an effect vocabulary:
- graph-visible consequents
- typed kernel effects
- summary / episode material returned from the executor
- kernel-owned application and persistence

This is the point where `:clojure-fn` stops being "opaque world mutation with a
pretty wrapper" and becomes a real contract.

The current `roving` effect program in `goal_families.clj` is now best read as
the first local experiment, not the final seam. Review 10 makes the next move
clear: the executor boundary belongs in `rules.clj` as `execute-rule`, with:
- runtime shape validation
- consequent-schema validation
- denotational validation
- `instantiate-rule` retained only as a compatibility wrapper

Only after that seam is real should more family plans be extracted.

Status update, 2026-03-22 late:
- `execute-rule` is now real in `rules.clj`
- `instantiate-rule` remains as a compatibility wrapper
- `execute-rule` now includes minimal effect-program validation
  (`:effects` vector, map entries, keyword `:op`, allowed op set)
- `execute-rule` now also runs a call-supplied effect validator so the
  current family runtime can reject malformed payloads before local
  effect application
- `execute-rule` now also validates rule-declared `:effect-schema`
  vectors, so the current `:clojure-fn` family rules declare an
  expected effect shape instead of only an allowed op list
- the current family dispatch `:effect-schema` checks are now closed
  at the top level, so undeclared effect keys fail at the boundary
  instead of drifting past the rule contract
- `rules.clj` now also owns the generic effect-program reduction and
  state-threading scaffold via `apply-effects`; the concrete family
  op handlers still live in `goal_families.clj`
- `rules.clj` now also owns the first builtin effect handlers for
  context/fact/goal mutation, while `goal_families.clj` keeps only the
  family-semantic handlers
- builtin `:goal/set-next-context` now fails closed on unknown goals,
  and the family whole-program validator now rejects duplicate
  symbolic producers across `:ref` / `:result-key`
- `goal_families.clj` now exposes those handlers as an explicit
  op-handler registry instead of a monolithic local dispatcher
- `:goal-family/roving-plan-dispatch` is now the first actual
  `:clojure-fn` vertical slice, but effect application still stays local
  in `goal_families.clj`
- `:goal-family/rationalization-plan-dispatch` is now the second actual
  `:clojure-fn` vertical slice, still with local kernel effect
  application
- `:goal-family/reversal-plan-dispatch` is now the third actual
  `:clojure-fn` vertical slice, using a composite branch effect while
  local kernel code still applies the mutation
- the current Step 2 pressure point is no longer family extraction; it
  is continuing to move the generic effect runtime into `rules.clj`
  now that both declarative `:effect-schema` validation and op payload
  validation are live at the `execute-rule` boundary

### Step 3: LLM-backed evaluator pilot

The first safe `:llm-backed` rule is evaluator-side, not generator-side.
Post-plan evaluation is the promotion gate from `:provisional` toward
`:durable`, not a way to let the LLM mutate kernel state directly.

### Step 4: Verified paths

`bridge-paths` stays candidate-only. Verification becomes a sibling layer after
the memory and executor contracts are more honest. The target sequence is:
- projection-verified
- episode-constructed
- grounded / sound-under-executors

This is the point where candidate adjacency becomes a context-sensitive witness.

### Step 5: Generic executor dispatch

Once the effect vocabulary is real, family plans can migrate to generic
dispatch. Order still matters:
- roving first
- rationalization second
- reversal last

That is when the rule engine stops being a structurally rich substrate and
starts owning behavior end to end.

## The critical threshold

The question is not "how many rules do we have?" It's "does the graph have enough cross-phase density that serendipity finds non-obvious paths?"

With only activation rules, the graph is a set of isolated trigger/consumer pairs. No interesting paths.

With activation + planning rules, paths can span from "why did this family activate?" to "what did its plan do?" Still mostly within-family.

With activation + planning + evaluation rules, paths can span from "this trigger happened" through "this plan ran" to "this outcome was scored this way." Cross-phase connections start appearing.

With activation + planning + evaluation + emotional routing rules, the graph connects emotional responses to planning strategies to evaluation criteria. A serendipity search that finds a path from a shame-trigger rule through a rationalization planning rule to a recovery evaluation rule has discovered something non-obvious about the relationship between shame, rationalization, and recovery.

That's the threshold. Not a number of rules — a density of cross-phase connections.

## How this connects to the evaluation ladder

| Level | What it tests | When it's testable |
|-------|--------------|-------------------|
| Level 1: Traceable firing | Rules fire with explicit provenance | Now (matcher + validation exist) |
| Level 2: Intervention sensitivity | Deleting a rule degrades downstream behavior | After Step 2 (provenance in episodes) |
| Level 3: Bridge discovery | System finds a 2-4 hop path that retrieval wouldn't | After Step 7 (serendipity search) |
| Level 4: Cross-session write-back | Rule/episode from session 1 changes behavior in session 2 without re-pasting trace | After Steps 2 + 7 + persistence layer |

The north star is Level 4. But the build sequence gets you testable milestones at each step.

## How this connects to the writeback experiment

The writeback experiment (gate 2) proved that accumulation matters within a session — residue episodes change later retrieval and selection. The rule engine trajectory extends that to structural accumulation:

- Today: LLM residue → episode with indices → changes next cycle's retrieval
- After provenance: LLM residue + rule provenance → episode indexed under the rule that produced it → retrieval returns episodes that share structural provenance, not just index overlap
- After serendipity: LLM residue + rule provenance + graph search → episode's rule indices participate in serendipity path discovery → the system doesn't just remember what it thought, it discovers new connections because of what it thought

That's the compounding creative capacity: each cycle doesn't just add a memory, it potentially adds a new path through the connection graph.

## The "remaining 60%" in concrete terms

The codex assessment was "35-40% of the rule engine is built." Here's what the remaining 60% looks like:

| Component | Status | Step |
|-----------|--------|------|
| Rule validation | Done | — |
| Pattern matching / unification | Done | — |
| Template instantiation | Done | — |
| Connection graph construction | Done | — |
| Graphable trigger facts | Done (roving, rationalization) | — |
| Graphable plan-request facts | Done (roving) | — |
| Generic activation dispatcher | Not started | Step 1 |
| Generic plan dispatcher | Not started | Step 1 |
| Rule/edge provenance in traces | Not started | Step 2 |
| Rule/edge provenance in episodes | Not started | Step 2 |
| Graph query (outgoing, paths, explanations) | Not started | Step 3 |
| Runtime integration of provenance | Not started | Step 4 |
| Remaining family rule pairs | Not started | Step 5 |
| Episode evaluation rules | Not started | Step 6 |
| `:llm-backed` executor | Not started | Step 6 |
| Serendipity path search | Not started | Step 7 |
| Serendipity path verification | Not started | Step 7 |
| Analogical rule application through graph | Not started | Step 7+ |

## What this is NOT

- This is not a rewrite of the kernel. The existing control loop, context tree, episodic memory, and reminding cascade stay. The rule engine gives them a structural substrate to operate over.
- This is not a port of Mueller's Lisp. It's a hybrid architecture where Mueller's structural patterns persist and LLMs provide content at typed judgment sites.
- This is not an academic exercise. The writeback experiment proved that accumulation changes behavior. The rule engine is the machinery that makes accumulation compound rather than just grow.
- This is not blocking the current sprint. RuntimeThoughtBeatV1 is the active project target. The rule engine is a parallel deep-build track that enriches what the sprint produces.
