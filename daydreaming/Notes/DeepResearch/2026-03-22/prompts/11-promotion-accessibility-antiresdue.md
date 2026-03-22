# Memory Ecology: Promotion, Rule Accessibility, and Anti-Residue Detection

You have the full repo context. Do NOT summarize it back. Bring concrete design proposals grounded in what the code actually does now.

Three connected questions about the memory ecology layer. They interact — answer them together.

## Context

The kernel now has `:trace` and `:provisional` admission tiers on family-plan episodes. Cue zones separate content cues (drive retrieval) from provenance (weak tie-break) from support (metadata only). Same-family provenance is capped. Cheap rationalization resurrection is gated. See the recent changes in `episodic_memory.clj`, `goal_families.clj`, and `build-order-checkpoint-2026-03-22.md`.

What's missing: the promotion path from `:provisional` to `:durable`, the rule accessibility frontier from Mueller, and the anti-residue detection logic.

## Question 1: Promotion criteria

The kernel stores family-plan episodes as `:provisional` by default. What should trigger promotion to `:durable`?

Mueller didn't have this — his episodes were either hidden (threshold=100) or fully active. We need promotion because our system accumulates across sessions and self-reuses, which Mueller's didn't.

Concrete design questions:
- What downstream evidence signals should trigger promotion? Options include: cross-family reuse (the episode was useful outside its own family), reduced negative emotion without later contradiction, improved external planning, LLM evaluator endorsement.
- Should promotion be automatic (triggered by observable kernel state) or require the LLM evaluator?
- What's the minimum viable gate that prevents grooves without being so strict nothing ever promotes?
- Should promotion be reversible? Can a `:durable` episode be demoted back to `:provisional` if later evidence contradicts it?
- Look at the current evaluation metadata (`family_evaluator.clj`, retention classes, keep-decision fields) and propose how promotion would use what already exists.

## Question 2: Rule accessibility frontier

Mueller's rules start inaccessible (`accessible? = nil`). A rule becomes `accessible? = t` only when it produces a stored, non-hidden episode (`dd_epis.cl:49`). Serendipity specifically searches INACCESSIBLE rules — `inaccessible-planning-rules` (`dd_ri.cl:273`) walks an episode's descendants and collects rules that haven't been accessed through planning yet. Finding and verifying a path through an inaccessible rule promotes it.

This creates a frontier between "known to the planner" and "discoverable by serendipity." It's the mechanism by which creative discovery expands the planner's repertoire.

Concrete design questions:
- How should this map into `RuleV1`? Should `:accessible?` be a field on the rule, or tracked separately in a registry?
- Should `build-connection-graph` respect accessibility (only connect accessible rules for planning, but allow inaccessible rules for serendipity search)? Or should accessibility be a caller-level filter?
- What's the interaction with admission tiers? Does a `:provisional` episode promote its rule to accessible, or only `:durable`? If `:provisional`, the groove risk returns through the rule frontier. If `:durable`, rules might stay inaccessible too long.
- Our rules are currently authored, not induced from episodes. Mueller's rules came from episode storage + rule induction. How does the accessibility frontier work when the initial rule set is hand-authored? Do authored rules start accessible or inaccessible?
- Look at `rules.clj` (graph construction, bridge-paths, reachable-paths) and `goal_family_rules.clj` and propose where accessibility should be enforced.

## Question 3: Anti-residue detection

The reviews called for flags: `:backfired`, `:stale`, `:contradicted`, `:same-family-loop`. These should cause retrieval to down-rank or exclude flagged episodes.

The detection logic is the hard part. Concrete design questions:
- **`:backfired`** — how do you detect that a rationalization made things worse? Is it: "the reframe was applied but negative emotion increased on the next cycle"? "The frame was reused but the concern didn't terminate successfully"? What observable kernel state constitutes backfire?
- **`:stale`** — how do you detect that a reversal counterfactual kept reopening but never improved planning? Is it: "this episode was retrieved N times but never led to a successful plan"? What's the threshold?
- **`:contradicted`** — how do you detect that later facts contradicted a stored frame? Is this structural (a new fact with the same `:fact/id` but different content was asserted in reality context)? Or does it require the LLM evaluator?
- **`:same-family-loop`** — this one seems most mechanical: "this episode was retrieved and reused only within its own family, never cross-family, across N cycles." What's N? Is retrieval count sufficient, or do you need to check whether the retrieval led to actual use?
- Should detection be checked at retrieval time (expensive but current) or as a batch consolidation pass between sessions (cheaper, delayed)?
- Look at the current retrieval code (`episodic_memory.clj`), the evaluation metadata, and the trace structure and propose where detection hooks would go.

## What I need

Not abstract principles — the reviews already gave those. I need concrete function signatures, data shapes, and detection logic that can be implemented in the current kernel. Think about what the code actually looks like in `episodic_memory.clj`, `goal_families.clj`, `family_evaluator.clj`, and `rules.clj` and propose changes that compose with what exists.
