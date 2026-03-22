# Rule Engine Trajectory: From Substrate to Creative Engine

This document explains the full arc of the kernel rule engine — where it started, what exists now, what the next steps are, and where it's headed. It connects the condensation work (the "what Mueller built" layer) to the implementation (the "what the kernel does" layer) to the project goals (the "why this matters" layer).

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

### Step 1: Generic family-plan dispatcher

**What**: Replace the hand-written `case` dispatch in `goal_families.clj` with a generic dispatcher that consumes `:family-plan-request` facts and selects the matching plan-dispatch rule.

**Why**: After this, the kernel doesn't know family names at the dispatch level. It knows rules and trigger facts. This is the transition from "substrate with examples" to "engine that owns behavior."

**What it proves**: The rule engine can select among competing rules based on structural matching, not hard-coded branching.

### Step 2: Rule/edge provenance in traces and episodes

**What**: When a rule fires, record which rule it was, which bindings matched, and which graph edge was traversed. Store that provenance in the trace (for debugging) and in the episode (for later retrieval).

**Why**: This is the bridge to "episodes indexed by rule/path use." Without it, episodes are stored but the memory system doesn't know which rules produced them. With it:

- Retrieval can find episodes because a specific rule was involved
- The evaluation ladder's Level 2 (intervention sensitivity) becomes directly testable: delete a rule, check whether episodes that depended on it stop being retrievable
- The accumulation story starts compounding: rule fires → episode stored under that rule → later retrieval finds episode via rule's indices → analogical planning reuses the episode's structure

**What it proves**: The system remembers not just what happened but why — which rules produced which outcomes.

### Step 3: Graph query layer

**What**: Expose simple graph operations:

- `outgoing-neighbors` — what rules can fire after this one?
- `reachable-paths` — what paths exist up to depth N from this rule?
- `path-explanation` — why does this path exist (which projections matched)?

**Why**: First direct use of the connection graph as search space rather than metadata. Not serendipity yet — just the ability to ask "what connects to what and why." Makes the graph inspectable and testable before creative search depends on it.

**What it proves**: The graph is meaningful — paths through it correspond to real structural chains, not just superficial type overlap.

### Step 4: Runtime integration

**What**: Fold rule/edge provenance into the watchable artifacts (cognitive viz, trace exports, thought beat packets). Make the "why" visible in the stage-facing output.

**Why**: Keeps the deep-build work tied to perception. A beautiful substrate that nobody can see is an engineering indulgence. The audience (and the conductor) need to see which rules fired, which paths were traversed, and how past episodes influenced the current thought.

### Step 5: More families through the rule engine

**What**: Rationalization plan-request/dispatch. Reversal plan-request/dispatch. Eventually recovery, rehearsal, repercussions.

**Why**: Each family that moves into the rule engine adds edges to the connection graph. More edges across different lifecycle phases means richer serendipity search space. A rationalization trigger connecting through a planning rule to a recovery dispatch creates exactly the kind of cross-family path Mueller's serendipity was designed to find.

### Step 6: Episode evaluation through rules

**What**: Express realism/desirability scoring as rules. A completed plan gets evaluated by rules whose antecedents match the plan structure and whose consequents produce scored metadata.

**Why**: This is where LLM-as-evaluator becomes natural. The rule fires structurally (the plan completed), but realism assessment is a judgment call. The `:llm-backed` executor provides contextual scoring while the rule remains graphable and the scores get stored as explicit episode metadata.

**What it proves**: The three-layer split (schema / denotation / executor) works in practice for LLM-backed rules. The first real test of the hybrid architecture.

### Step 7: Serendipity path search

**What**: Given a current concern and a salient concept/episode, search the connection graph for paths of length 2-4 between a concern-linked rule and a salient-source-linked rule. Verify candidate paths by progressive unification.

**Why**: This is the payoff. This is the mechanism that makes the system genuinely creative rather than merely organized. A path that has never been traversed connects an active concern to something accidentally salient. The path is verified, turned into a new plan, stored as an episode, and the system's creative capacity has grown.

**What it proves**: The strong claim — persistent typed structure changes later reachable thought paths, not just later phrasing.

**Why it can't come earlier**: Serendipity over a graph with 3 edges is trivial and proves nothing. The graph needs enough density across different lifecycle phases (activation, planning, evaluation, emotional routing) that paths through it are genuinely non-obvious. Steps 1-6 build that density.

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
