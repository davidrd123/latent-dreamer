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

## What is now settled

**Episode consolidation / admission:**
- Family-plan episodes do not jump straight to "durable precedent."
- The active membrane is:
  - `:trace` — auditable only
  - `:provisional` — retrievable under stricter conditions
  - `:durable` — promoted only after downstream evidence
- Current implementation has the first pass of this:
  - `:archive-cold -> :trace`
  - other family-plan episodes default to `:provisional`
- The missing piece is promotion logic from `:provisional -> :durable`.

**Cue zone separation:**
- Content cues drive retrieval and reminding.
- Reminding cues are a subset of content cues; only these enter the FIFO.
- Provenance indices are queryable but do not count as cue overlap.
- Support tags are metadata only and do not enter the normal retrieval index.
- This is now implemented in the episode substrate.

**Reentry discipline:**
- Provenance only helps after content marks exist.
- Imaginary / counterfactual material has a stricter floor than ordinary remembered material.
- Same-family fallback for rationalization and reversal is gated on durable promotion.
- Cheap rationalization resurrection via `:serendipity? true` is now disabled.

**What remains open:**
- Anti-residue flags (`:backfired`, `:stale`, `:contradicted`, `:same-family-loop`)
- Promotion criteria to `:durable`
- Stronger source-type decay and cluster caps
- Graph hygiene for larger, less toy rule sets
- Re-evaluation of stored material after later contradiction or success

## Property to preserve

Consolidation must not create usage-weighted grooves in the connection graph itself. The graph stays structurally derived from rule schemas. Consolidation operates on rules (which ones stay active) and episodes (which ones are accessible), not on graph edges.

## Relationship to Ori-Mnemos

Ori's three-zone decay, Q-value retrieval learning, and co-occurrence edge strengthening are the closest existing implementation of consolidation for a cognitive memory system. The kernel should borrow persistence patterns from Ori but not apply Hebbian learning to the rule connection graph. See design notes on mechanisms 11 and 13.
