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
- Current implementation now has the first two passes of this:
  - `:archive-cold -> :trace`
  - other family-plan episodes default to `:provisional`
  - cross-family reuse can promote `:provisional -> :durable`
  - `:episode-promotion` facts make those promotions graph-visible
  - promotion still obeys the recent-episode anti-echo gate
- external evaluator output can now promote selected family-plan
  episodes to `:durable`
- external evaluator output can now also attach anti-residue flags
  (`:backfired`, `:stale`, `:contradicted`) to stored episodes, and
  those flags are asserted as typed `:episode-flag` facts
- first attributed-use slice is now in code:
  `note-episode-use`, `resolve-episode-use-outcome`, and
  `reconcile-episode-admission`
- `roving` now emits typed `:episode-use` and `:episode-outcome`
  facts and reconciles admission from those use records
- The missing piece is broader evaluator discipline, later demotion,
  and downstream evidence that can set or clear these flags.
  The newest review pair sharpens this further:
  promotion should require both structural evidence and attributed
  outcome evidence. The evaluator can veto or permit promotion, but
  should not be the sole authority.

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
- episode use with attributed outcomes as the unifying substrate for:
  promotion, anti-residue, and rule accessibility
- Using the new `:same-family-loop` flag as a broader consolidation signal
- Stronger evaluator-gated promotion criteria and later demotion
- Stronger downstream contradiction / backfire detection
- Stronger source-type decay and cluster caps
- Graph hygiene for larger, less toy rule sets
- Re-evaluation of stored material after later contradiction or success

## Current design clarification

The missing abstraction is not promotion by itself, nor anti-residue,
nor accessibility. It is **episode use with attributed outcomes**:

- episode X was used by family Y
- for goal G
- in cycle C
- with later outcome O

Once that evidence exists cleanly, promotion to `:durable`,
anti-residue flags, and future rule accessibility become ordinary
state transitions rather than ad hoc retrieval heuristics.

The kernel now has the first real version of that substrate, but only
on the roving cross-family path. The next move is to extend it beyond
that slice and let richer outcomes than simple success/failure drive
promotion, demotion, and accessibility.

## Property to preserve

Consolidation must not create usage-weighted grooves in the connection graph itself. The graph stays structurally derived from rule schemas. Consolidation operates on rules (which ones stay active) and episodes (which ones are accessible), not on graph edges.

## Relationship to Ori-Mnemos

Ori's three-zone decay, Q-value retrieval learning, and co-occurrence edge strengthening are the closest existing implementation of consolidation for a cognitive memory system. The kernel should borrow persistence patterns from Ori but not apply Hebbian learning to the rule connection graph. See design notes on mechanisms 11 and 13.
