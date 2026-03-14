# Evaluation and Falsification Plan

## Purpose

Design the minimum set of tests needed to falsify or justify the current
architecture.

## Recommended attachments

Primary:
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/11-settled-architecture.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/12-prior-work-synthesis-against-settled-architecture.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/08-l3-experiment-1-synthesis.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/13-l2-refactor-synthesis.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/14-l1-critic-test-synthesis.md`

## Prompt

```text
Design an evaluation and falsification plan for the current architecture.

I want:
- the minimum experiment for `L3`
- the minimum experiment for the `L2` refactor
- the minimum experiment for the `L1` critic test
- what should count as success
- what should count as a real negative result
- what manual or simple baselines each experiment should be compared against

Bias toward short, falsifiable tests rather than ambitious benchmarks.

Context:
- `L3` is an authored-graph traversal scheduler experiment
- `L2` is a refactor of the character-pressure kernel
- `L1` is a narrow authoring critic/repair layer
- the system should be built in an order that reveals failure early

Output format:
- `Recommended experiment order`
- `L3 minimum experiment`
- `L2 minimum experiment`
- `L1 minimum experiment`
- `Success criteria`
- `Negative-result criteria`
- `Baselines`
- `Bottom line`

Ground the plan in the supplied notes, not in generic evaluation lore.
```
