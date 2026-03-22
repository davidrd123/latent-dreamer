# Deep Research Prompt: Architecture Review of the Hybrid Cognitive Engine

## Instructions

This is an architecture-level thinking prompt. Not "what does this mean" (that's a separate prompt). Not "how do I write the code" (that's also separate). This is: **given what we're trying to build, are we making the right structural choices?**

Think about this as a systems architect reviewing a proposed design for a novel kind of system — one that combines a persistent symbolic cognitive architecture with LLM judgment calls. The design exists (attached). I need you to find the load-bearing joints, identify where the design might be wrong or incomplete, and think about architectural alternatives we might be missing.

## What we're building

A hybrid system where:
- A Clojure kernel maintains persistent cognitive structure (rule connection graph, episodic memory with index-based retrieval, reminding cascade, context tree, emotion-driven concern selection)
- LLMs provide content and evaluation at typed call sites within the kernel's structural loops
- The system accumulates across sessions — episodes grow, new rules can enter the connection graph, serendipity search space expands
- Rules are Clojure data (maps with `:antecedent-schema`, `:consequent-schema`, `:executor`) that live in a searchable graph AND can invoke LLMs when they fire

The attached documents describe:
- The 19 mechanisms extracted from Mueller's DAYDREAMER, each with loop shape, judgment points, accumulation story, and property to preserve
- The proposed rule schema (`RuleV1`), connection graph (`EdgeBasisV1`), execution contract (`RuleCallV1` / `RuleResultV1`)
- Two chain traces showing how mechanisms compose in goal-directed and accident-driven modes
- Worked examples: Kai's letter rule with `:instantiate` vs `:llm-backed` executors; hidden blessing rationalization as LLM-backed planning rule

## What I want you to think about

### 1. The consequent-schema / executor split

The core architectural choice: rules declare their structural shape (`:consequent-schema`) separately from how they produce output (`:executor`). The connection graph is computed from schemas. The LLM fills in content. Clojure validates output against the schema.

- Is this the right split? Are there cases where the schema can't adequately describe what a rule produces without constraining it too much?
- What happens when the LLM generates something that's valid against the schema but semantically wrong for the context? Schema validation catches type errors but not judgment errors. Is there a middle layer needed?
- Could the schema evolve at runtime (e.g., a rule that starts producing a new kind of consequent)? Should it? What does that do to graph stability?

### 2. The static connection graph as creative substrate

The graph is structurally derived from pattern overlap, not usage-weighted. Never-traversed paths are equally findable. This is the property that makes serendipity different from retrieval.

- Is "static and structurally derived" definitely right? Or is there a version of adaptive graph structure that preserves creative discovery while still learning from experience?
- What about graph maintenance as rules are added? REVERSAL creates new rules. Each new rule means new potential connections. At what point does incremental graph update become a problem?
- Mueller's graph was implicit (computed on demand during search). The proposed architecture makes it explicit (precomputed edges cached on rules). Which is better for the use case? What are the tradeoffs?

### 3. The recursion ownership model

Three recursive mechanisms (reminding cascade, analogical repair, serendipity verification) stay in Clojure. LLM calls appear only at bounded judgment points within the loops.

- Is this the right boundary? Are there cases where the LLM should have more control over the recursion — e.g., deciding to go deeper or stop early?
- What about latency? If each recursive step has an optional LLM call, and the cascade is 4-6 steps deep, that's potentially 4-6 serial LLM round-trips. How does the architecture handle the latency budget?
- Could the recursion be parallelized? E.g., evaluate multiple reminded episodes simultaneously rather than sequentially?

### 4. The relationship between rules, episodes, and the connection graph

Three different structures with three different lifecycles:
- Rules: mostly static, occasionally created by REVERSAL, connection graph computed from them
- Episodes: grow continuously, indexed under rules and surface features, retrieved by coincidence-mark counting
- Connection graph: derived from rules, used by serendipity and planning

- Are these the right boundaries between the three structures? Is there a tighter or looser coupling that would work better?
- Episodes index under rules (rule-rooted indices). Rules connect through pattern overlap. So an episode is indirectly connected to the graph through its rule indices. Is that indirection right, or should episodes have more direct graph presence?
- When a new rule is created, it enters the graph. When a new episode is stored, it gets indexed under rules. But does storing an episode ever change the graph? (In the current design: no. Should it?)

### 5. The four integration patterns

The condensation identifies four ways LLMs plug in:
- **Co-routine judgment**: Clojure asks a typed question, LLM returns typed answer
- **Rule-with-LLM-consequent**: rule fires structurally, consequent is generated
- **LLM-as-evaluator**: Clojure generates candidates, LLM scores/ranks
- **LLM-as-content-generator**: Clojure provides context, LLM generates scenario

- Are four patterns the right number? Are any of them actually the same pattern at different granularities?
- Is the dominant pattern (LLM-as-evaluator, 10/19 mechanisms) an architectural strength or a sign that the LLM isn't contributing enough? Could more mechanisms benefit from content generation?
- How should the system behave when LLM calls fail, time out, or return low-confidence results? The design says "degrade to Mueller-faithful fallback." Is that sufficient, or does graceful degradation need more nuance?

### 6. What's missing from the architecture?

The design covers rules, episodes, concerns, emotions, contexts, and the connection graph. What cognitive capabilities are NOT addressed that the system will eventually need?

- **Attention / salience beyond emotion-driven selection**: Mueller routes by emotional motivation. Is that sufficient for a richer system, or does attention need its own mechanism?
- **Temporal reasoning**: Mueller's context tree gives branching but not rich temporal structure. How does the system reason about "this happened before that" or "this is relevant now but won't be next week"?
- **Learning beyond episode storage and rule creation**: The system accumulates structure, but does it optimize? Does it get better at anything other than having more material?
- **Multi-agent or multi-perspective**: Mueller's system has one mind. What happens when the system needs to model other agents' mental states for social reasoning?

### 7. Comparison to existing architectures

How does this design compare to:
- **SOAR** with LLM integration (e.g., recent work on SOAR + GPT)
- **ACT-R** with LLM modules
- **Cognitive agent architectures** like CoALA (Cognitive Architectures for Language Agents)
- **LangGraph / LangChain agent frameworks** with persistent memory
- **Ori-Mnemos** style knowledge graphs with cognitive decay

Where is this design stronger? Where is it weaker? What have those systems learned that this design should absorb? What does this design do that none of them attempt?

### 8. The code-is-data question

Rules are Clojure maps. Episodes are Clojure maps. The connection graph is over Clojure maps. The LLM can return Clojure maps. Everything is the same substrate.

- Is this actually an advantage, or does it create a false sense of composability? Are there places where type boundaries (e.g., rules vs episodes vs emotions) should be harder rather than softer?
- If the LLM can return rules, what prevents it from creating degenerate graph structure — rules that connect to everything, rules that create cycles, rules whose schemas are too vague to be useful?
- Clojure's persistent data structures make immutability cheap. How important is immutability for this specific architecture? Could the same design work in a mutable-state language, or does immutability provide guarantees the architecture depends on?

## Attached context

1. architectural-framing.md
2. cross-cut-summary.md
3. chain-trace-a.md
4. chain-trace-b.md
5. kernel-rule-schema-and-execution-model.md
6. rule-connection-graph.md
7. mechanisms/05-planning-rule-application.md
8. mechanisms/09-analogical-rule-application.md
9. mechanisms/13-serendipity-recognition.md
10. mechanisms/12-reminding-cascade.md
