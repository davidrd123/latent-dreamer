# Source-Miss Scan

## Purpose

Check whether the current synthesis underused, over-imported, or flattened
important tensions in the source lineages already read.

## Recommended attachments

Primary:
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/12-prior-work-synthesis-against-settled-architecture.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/08-l3-experiment-1-synthesis.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/13-l2-refactor-synthesis.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/14-l1-critic-test-synthesis.md`

Secondary source-basis docs:
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/01-facade-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/02-dodm-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/03-authorial-leverage-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/04-suspenser-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/05-ema-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/06-occ-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/07-versu-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/08-sentient-sketchbook-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/09-minstrel-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/11-mueller-ch7-extraction.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/12-mueller-appendix-b-extraction.md`

## Prompt

```text
Using the supplied extraction notes and synthesis notes, tell me whether the architecture is leaving value on the table from the source lineages already read.

I want:
- ideas from the existing source set that were underused
- ideas that were imported too aggressively
- important tensions between sources that the synthesis flattened over
- source-derived constraints that should probably shape the build order

Context:
- the architecture itself is in `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/11-settled-architecture.md`
- the higher-level synthesis is in `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/12-prior-work-synthesis-against-settled-architecture.md`
- lane-specific syntheses exist for `L3`, `L2`, and `L1`
- the goal is not to widen the reading list, but to check fidelity to what has already been read

Output format:
- `Findings` ordered by severity with citations to both synthesis docs and source-basis docs
- `Underused source ideas`
- `Over-imported ideas`
- `Flattened tensions`
- `Build-order implications`
- `Bottom line`

Stay close to the supplied source materials. Do not default to suggesting new papers unless a gap is impossible to describe otherwise.
```
