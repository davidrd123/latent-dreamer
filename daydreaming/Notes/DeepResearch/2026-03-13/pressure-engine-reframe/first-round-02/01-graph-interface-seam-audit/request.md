# Graph / Interface Seam Audit

## Purpose

Review the current architecture specifically through the lens of the
shared graph/interface seam.

## Recommended attachments

Primary:
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/11-settled-architecture.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/12-prior-work-synthesis-against-settled-architecture.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/08-l3-experiment-1-synthesis.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/13-l2-refactor-synthesis.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/14-l1-critic-test-synthesis.md`

Secondary if you want source-grounding:
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/05-ema-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/06-occ-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/07-versu-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/11-mueller-ch7-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/12-mueller-appendix-b-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/01-facade-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/02-dodm-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/03-authorial-leverage-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/04-suspenser-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/08-sentient-sketchbook-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/09-minstrel-extraction.md`

## Prompt

```text
Review the current architecture specifically through the lens of the shared graph/interface seam.

Treat `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/11-settled-architecture.md` as the canonical architecture unless you find a concrete contradiction. Do not redesign the system. I want a minimal viable seam, not a rich ontology.

Tell me:
- what absolutely must be represented in the graph for `L1`, `L2`, and `L3` to interoperate
- what should explicitly NOT be pushed into the graph
- where provenance, scope, confidence, and revisability need to be tracked
- where the current notes appear to over-serialize internal state
- where they appear to under-specify the shared interface
- what the minimum viable seam is for early experiments

Context:
- `L1` is a narrow authoring critic/repair layer
- `L2` is the live character-pressure engine
- `L3` is drama-management traversal over authored graph material
- the graph is the interface between lanes
- the goal is buildability and falsifiability, not conceptual completeness

Output format:
- `Findings` ordered by severity with citations to the supplied docs
- `Minimum viable seam`
- `Do not put this in the graph`
- `Must fix now`
- `Later refinements`
- `Bottom line`

Ground everything in the supplied notes. Avoid generic knowledge-graph advice.
```
