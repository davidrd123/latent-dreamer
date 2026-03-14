# Failure-Mode Review

## Purpose

Assume the current architecture is internally coherent and identify the
main ways it could still fail as a build direction.

## Recommended attachments

Primary:
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/11-settled-architecture.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/12-prior-work-synthesis-against-settled-architecture.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/08-l3-experiment-1-synthesis.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/13-l2-refactor-synthesis.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/reading-list/14-l1-critic-test-synthesis.md`

## Prompt

```text
Assume the current architecture is internally coherent. Your task is to identify the top 5 failure modes that could still make it a bad build direction.

For each failure mode, provide:
- what the failure is
- which lane or interface it hits
- the earliest symptom we would observe
- the cheapest test that would expose it
- the best mitigation that does not require redesigning the whole architecture

Focus on real engineering and representation failures, not generic project-risk advice.

Context:
- `L1` is a narrow authoring critic/repair layer
- `L2` is the live character-pressure engine
- `L3` is drama-management traversal over authored graph material
- the graph is the interface between lanes
- the current goal is a buildable sequence of experiments, not a grand unified architecture

Output format:
- `Findings` ordered by severity with citations to the supplied docs
- `Top 5 failure modes`
- `Cheapest exposing tests`
- `Best mitigations`
- `Bottom line`

Do not propose a replacement architecture unless a mitigation is impossible without one.
```
