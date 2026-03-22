# Modern Extension: Consolidation / Compression / Graph Hygiene

This is NOT a Mueller mechanism. Mueller's system ran for one session. This note addresses what happens when the system runs across sessions and accumulation becomes a liability as well as an asset.

Surfaced by outside architecture review. All four 5 Pro replies converged on this: "accumulation without consolidation is sludge."

## The problem

As the system accumulates episodes, rules, and graph connections:

- Episode count grows, retrieval noise rises
- New rules (from REVERSAL, from LLM-backed generation) increase graph density
- Denser graph means more candidate serendipity paths, most of which are garbage
- Branching factor blowup: candidate paths grow roughly as b^l where b is effective branching and l is path length
- Without pruning, the system rots into noise rather than sharpening into capability

## What needs to happen (design space, not settled)

**Episode consolidation:**
- Down-weight or archive episodes that are never retrieved
- Ori-style Q-value learning: episodes that prove useful when retrieved earn higher accessibility
- Merge or compress episodes that cover the same planning structure with minor variations
- Decay profiles by zone: identity-level material near-zero decay, knowledge-level moderate, operational-level fast (cf. Ori's three-zone architecture)

**Graph hygiene:**
- Monitor effective branching factor per rule node
- Flag rules whose schemas are too generic (connect to everything, useful for nothing)
- Potentially prune or quarantine LLM-generated rules that never participate in successful planning
- The connection graph must stay structurally derived — but the rule BASE can be curated

**Evaluation pressure:**
- Mechanism 18 (episode evaluation) stores realism and desirability. Those scores should influence later retrieval ordering (not just episode selection during planning)
- Rules created by REVERSAL should carry provenance metadata and be re-evaluated if the analogical plans they support consistently fail

## Property to preserve

Consolidation must not create usage-weighted grooves in the connection graph itself. The graph stays structurally derived from rule schemas. Consolidation operates on rules (which ones stay active) and episodes (which ones are accessible), not on graph edges.

## Relationship to Ori-Mnemos

Ori's three-zone decay, Q-value retrieval learning, and co-occurrence edge strengthening are the closest existing implementation of consolidation for a cognitive memory system. The kernel should borrow persistence patterns from Ori but not apply Hebbian learning to the rule connection graph. See design notes on mechanisms 11 and 13.
