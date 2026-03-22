# Deep Research Prompt: Implementation Strategy for the Rule Engine and Connection Graph

## Instructions

This is a practical thinking prompt. The architecture is settled (see attached framing). I need you to think deeply about HOW to build the first layers — the rule engine and connection graph in Clojure — with attention to the specific properties that must hold, the abstractions that will pay off, and the pitfalls that come from building persistent cognitive infrastructure in a functional language.

Think about this as a senior architect reviewing a design before implementation begins. The design exists (see rule schema document). What I need is: where will this be hard, what will we wish we'd thought about earlier, and what's the smartest order to build it in.

## What exists in the kernel today

The Clojure kernel (`kernel/src/daydreamer/`) currently has:

- `context.clj` — immutable context tree with sprouting, backtracking, pseudo-sprouts. Substantially complete. Based on Mueller's `gate_cx.cl`.
- `goals.clj` — goal representation with status, strength, planning-type, context pointers. Based on `dd_cntrl.cl`.
- `control.clj` — top-level control loop: decay, mode oscillation, goal selection. Based on `dd_cntrl.cl`.
- `episodic_memory.clj` — episode store with index-threshold retrieval, reminding cascade, recent-index/recent-episode FIFOs. Based on `dd_epis.cl`.
- `goal_families.clj` — daydreaming goal family activation (partial — reversal, rationalization, roving).
- `director.clj` — impure LLM edge calling Gemini API for conducted-daydreaming feedback.
- `trace.clj`, `runner.clj` — tracing and execution infrastructure.
- Benchmarks: puppet_knows (with autonomous variant), arctic_expedition, stalker_zone.

Mueller's original source is also available: 18,262 lines of T/Common Lisp across 28 `.cl` files.

## What needs to be built

The proposed `RuleV1` schema, connection graph construction, and executor dispatch model are in the attached rule schema document. The key data structures:

```clojure
RuleV1
{:id keyword
 :rule-kind #{:inference :planning}
 :antecedent-schema [pattern]
 :consequent-schema [pattern]
 :plausibility number
 :index-projections {:match [index-projection] :emit [index-projection]}
 :executor {:kind #{:instantiate :clojure-fn :llm-backed} :spec any}
 :graph-cache {:out-edge-bases [edge-basis] :in-edge-bases [edge-basis]}}

EdgeBasisV1
{:from-rule keyword :to-rule keyword
 :from-projection pattern :to-projection pattern
 :bindings map
 :edge-kind #{:state-transition :goal-decomposition :emotion-trigger :repair-step}}

RuleCallV1 / RuleResultV1 — typed execution contract
```

The connection graph is computed from schema overlap (not runtime behavior). Executor dispatch goes: find rules structurally → bind antecedents → build RuleCallV1 → dispatch on executor kind → validate result against consequent-schema → write into state.

## What I want you to think about

### 1. The pattern language

The rule schema needs patterns with logic variables (`?target`, `?goal`, etc.) for antecedents and consequents. Mueller used GATE's unification system. We need something lighter. Questions:

- What's the right Clojure pattern language for this? Plain maps with keyword-prefixed logic variables? core.unify? core.logic? Something custom?
- The pattern language needs to support: structural equality for closed fields, variable binding for open fields, and projection functions for indexing and graph connection computation.
- Mueller's unification is asymmetric (one structure can be a substructure of the other). Do we need that, or is symmetric unification sufficient for the connection graph?
- How do we keep the pattern language simple enough to start but extensible enough for the full mechanism set?

### 2. Connection graph construction

The graph is computed from antecedent/consequent schema overlap across all rules. Questions:

- What's the right algorithm? Naive pairwise comparison is O(n²) in rule count. Is that acceptable for hundreds of rules? Thousands?
- Should the graph be eagerly computed (rebuild on any rule change) or lazily maintained (incremental updates when rules are added)?
- Mueller's connection graph supported bidirectional search (serendipity searches from top rule to bottom rule). What data structure supports efficient bidirectional path queries?
- The graph must be rebuildable from schemas alone (the `:graph-cache` field is derived). What's the right invalidation strategy?

### 3. Integration with existing kernel

The kernel's `episodic_memory.clj` already has index-threshold retrieval and reminding cascades. The rule engine needs to interoperate:

- Episode storage indexes episodes under rules. How does `RuleV1` connect to the existing episode index structure?
- The control loop in `control.clj` selects concerns and advances planning one step. How does rule-based planning integrate with the existing cycle?
- Goal families in `goal_families.clj` currently hard-code some activation logic. How do inference rules subsume or complement that?
- The world state is currently an immutable map passed through functions. Rules need to read and write facts in contexts. How does that compose with the existing context machinery?

### 4. The executor dispatch model

Three executor kinds: `:instantiate`, `:clojure-fn`, `:llm-backed`. Questions:

- What does the dispatch look like in practice? A multimethod? A protocol? A simple `case` on `:kind`?
- For `:llm-backed` executors, the LLM call is asynchronous and potentially slow. How does that interact with the synchronous control loop? Does the loop block? Does it defer and continue with other concerns?
- Schema validation of LLM output against `:consequent-schema` — what happens on validation failure? Retry? Fall back to `:instantiate`? Log and skip?
- The `:clojure-fn` executor needs to return `RuleResultV1`. How do we make it easy to write these functions without boilerplate?

### 5. Code-is-data considerations

Rules are Clojure maps. The LLM can potentially return rules as output. Questions:

- If REVERSAL creates a new rule at runtime, how does the connection graph update? Is this an incremental graph update or a full rebuild?
- If the LLM returns a new rule, how much validation is needed before it enters the graph? Schema validation of the rule itself? Checking that its patterns are well-formed? Testing that it doesn't create degenerate graph structure?
- What's the serialization story? Rules need to persist across sessions. EDN? Transit? How does the rule base get saved and loaded?
- How do we handle rule versioning? If a rule is modified (e.g., plausibility updated after episode evaluation), is it a new rule or a mutation?

### 6. Build order and testing strategy

The proposed order is: schema → graph → instantiate/clojure-fn executors → one LLM pilot → one recursive mechanism.

- Is that the right order, or should graph construction and executor dispatch be developed in parallel?
- What's the right testing strategy? The kernel already has benchmark scenarios. Can the rule engine be tested against Mueller's known traces (Appendix A)?
- What's the minimum number of rules needed to exercise the connection graph meaningfully?
- Should we seed the initial rule base from Mueller's `dd_kb.cl` (3,874 lines of knowledge base), or start with a small hand-authored set?

### 7. What will we wish we'd thought about earlier?

This is the most important question. Given the architecture, the existing kernel, and the goal of persistent cognitive structure that accumulates across sessions — what design decisions made now will be hard to change later? What abstractions will we regret? What will bite us at scale that seems fine with 20 rules?

## Attached context

1. architectural-framing.md
2. cross-cut-summary.md
3. chain-trace-a.md
4. chain-trace-b.md
5. kernel-rule-schema-and-execution-model.md
6. rule-connection-graph.md
7. mechanisms/05-planning-rule-application.md
8. mechanisms/06-inference-rule-application.md
9. mechanisms/13-serendipity-recognition.md
10. mechanisms/09-analogical-rule-application.md
