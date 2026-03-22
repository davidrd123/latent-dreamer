# Architectural Framing: Typed LLM Judgment Inside Persistent Graph-Structured Cognition

This document captures the point of view arrived at through the condensation work. It is not implementation spec — it is the framing that implementation should be evaluated against.

## The core claim

Mueller's DAYDREAMER is a cognitive architecture where 19 mechanisms operate on persistent data structures (rule connection graph, episodic memory, context tree, concern stack, emotional state). Each mechanism has a loop shape (structural, deterministic) and judgment points (where Mueller hard-coded decisions that could be contextual). The system accumulates — episodes grow, rules can be created, the connection graph gets richer, emotions decay but persist. That accumulation is what makes it different from stateless prompting.

The hybrid architecture keeps the structural loops in Clojure and places LLM calls at the judgment points inside those loops. The LLM provides content and evaluation. Clojure owns the recursion, the state changes, the backtracking, and the persistence.

## The distinction that matters: content vs. accumulation

An LLM can replicate the **output** of most Mueller mechanisms in a single forward pass. When it generates "Kai fills the kettle while avoiding eye contact with the letter," it is simultaneously doing emotional appraisal, social practice evaluation, scenario generation, and implicit episodic retrieval.

But generating output is not the same as changing persistent state. Mueller's mechanisms don't just produce text — they:

- Create typed emotion objects linked to specific goals, with decay rates, that drive future concern selection
- Store episodes as decomposable planning trees under explicit indices that participate in future coincidence-mark retrieval
- Activate daydreaming goals as persistent concerns that compete for processing time across cycles
- Track which rationalization strategy was used, whether it backfired, and how emotional strength changed — so future retrieval of that episode activates different affect
- Create new rules through REVERSAL that enter the connection graph and change future serendipity search space

These are state changes on persistent structure. The LLM cannot perform them because it has no persistent structure to change. The Clojure kernel provides that structure.

Reducing the mechanisms to "the LLM handles most of them holistically" is the central error to avoid. It confuses content generation with cognitive accumulation. That error collapses episodes to palette cells, emotions to scalars, and serendipity to fuzzy retrieval. The condensation exists to prevent it.

## The hybrid boundary is inside mechanisms, not between them

The cut is not "these mechanisms are structural, those are LLM." The cut is within individual mechanisms, at specific judgment points inside their execution:

- Mechanism 13 (serendipity): the rule-graph path search is structural. Whether the found path is *useful* is a judgment call.
- Mechanism 09 (analogical planning): the source-to-target tree transfer is structural. Whether the analogy is *apt* is a judgment call.
- Mechanism 17 (rationalization): which strategy fires is structural. What the *reframe content* actually says is a judgment call.
- Mechanism 18 (episode evaluation): the threshold/ordering logic is structural. What *realism score* to assign is a judgment call.

The dominant integration pattern is **LLM-as-evaluator** (10 of 19 mechanisms). Only 3 mechanisms need LLM content generation. The system is overwhelmingly a structural engine with evaluative LLM assistance, not an "LLM generates, Clojure bookkeeps" system.

## The rule schema enforces the boundary

A rule is a Clojure map:

- `:antecedent-schema` and `:consequent-schema` are structural, declared at rule creation, and participate in connection graph computation. The graph stays valid regardless of what executor runs.
- `:executor` can be `:instantiate` (Mueller-faithful pattern matching), `:clojure-fn` (procedural, Mueller's daydreaming-goal rules), or `:llm-backed` (the new thing).
- Clojure validates that LLM output conforms to `:consequent-schema` before writing it into state.

The rule lives in the connection graph, is indexed in episodes, participates in serendipity intersection search — and can invoke an LLM when it fires. The graph is the skeleton. The consequent is the flesh. Different flesh, same skeleton.

## Recursion stays in Clojure

Three mechanisms are recursive: the reminding cascade, analogical repair, and serendipity path verification. In all three:

- Clojure owns the recursion stack, branching, cycle prevention, and backtracking
- The LLM provides bounded evaluation at leaf nodes within each recursive step
- The LLM never decides what to retrieve next, how deep to recurse, or when to backtrack

This preserves provenance, cost control, and the ability to run without LLM calls entirely (Mueller-faithful fallback).

## The static connection graph is the creative substrate

The rule connection graph is computed from antecedent/consequent pattern overlap. It is structurally derived, not usage-weighted. A path that has never been traversed is equally findable as one traversed a hundred times.

This is the property that makes serendipity different from retrieval. Retrieval finds things similar to what you're looking for. Serendipity finds paths you weren't looking for through structural connections you didn't know existed.

If retrieval learning is added (e.g., usage-weighted episode ordering), it must apply to episode retrieval, not to the connection graph. The graph has no grooves. That is a feature.

## The four structural capabilities no existing system provides to LLMs

1. **Rule connection graph**: persistent, searchable graph of rule relationships. Enables serendipity intersection search — finding non-obvious paths between active concerns. No LLM agent framework provides this.

2. **Episodic memory with coincidence-mark indexing**: retrieval based on which specific indices converge, not embedding similarity. The system knows WHY something was retrieved (which indices matched), not just THAT it was retrieved. The reminding cascade needs that provenance.

3. **Reminding cascade**: bounded recursive retrieval where one episode's indices activate further episodes. Associative drift with structural propagation. Not one-shot similarity search.

4. **Context tree with immutable sprouting/backtracking**: explore hypothetical branches and abandon dead ends without contaminating reality. Safe imagination with explicit provenance.

These are the bones. The LLM is the flesh. Neither alone does what the combination does.

## What accumulates and why it matters

Across sessions, the system grows:

- Episodes get stored with planning tree structure, indices, realism/desirability scores
- New rules can be created (especially through REVERSAL) that enter the connection graph
- The connection graph gets richer, expanding the serendipity search space
- Emotional residue from past experiences biases future concern selection
- Retrieved episodes that prove useful could earn higher retrieval scores (Ori-style learning on episodes, not on the graph)

This is the claim that distinguishes the project from both stateless LLM agents and from simple RAG+memory systems: the system's creative capacity grows through its own operation. A serendipitous discovery becomes a stored episode, indexed under the rules that produced it, available for future analogical planning. A reversal creates a new rule that changes what paths serendipity can find later. The LLM contributes content that crystallizes into persistent structure.

## The condensation as reasoning surface

The 19 mechanism cards, two chain traces, cross-cut summary, and rule connection graph document exist so that architecture discussions can happen at the right resolution — not in 90K of chapter prose, not in 18K lines of T source, but in 1-2 page mechanism descriptions with typed interface shapes. Each card is grounded in Mueller (source anchors), connected to the kernel (status field), and oriented toward the hybrid architecture (judgment points, integration patterns, interface shapes).

The success criterion: point at any mechanism, trace through the chain, identify the interfaces, distinguish what Mueller built from what we propose. If a simplification is proposed, check it against the property-to-preserve field. If a mechanism is dismissed as "the LLM handles this," check whether the accumulation story survives that dismissal.
