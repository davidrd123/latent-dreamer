# Current Sprint

Last updated: 2026-03-21

---

## Objective

**Is cognition legible on stage, and does residue writeback change
the next trace?**

Two gates, both required:

1. **Stage gate:** runtime thought beats read as a person thinking,
   not as a system report with better labels.
2. **Architecture gate:** the feedback loop produces a structurally
   different trace than running without feedback — evidence that
   persistent kernel state earns its keep beyond LLM prose style.

Gate 1 alone is too weak — a plain LLM rewrite could pass it. Gate 2
is what distinguishes this from memory-augmented generation.

## Context

The supply pilot passed. The bridge tests passed. Three threads
(thought beats, provocations, generation pipeline) converge into
one loop — see `37-plan-of-attack.md` for the strategic framing.
The most important unanswered question is no longer about material
supply or traversal dynamics.

## Primary Target

### RuntimeThoughtBeatV1 on Puppet Knows

Run the kernel's Mueller cognitive loop (`bb puppet-knows-autonomous`).
For each cycle, call an LLM with the kernel's state to produce a
runtime thought beat — the specific rationalization, reversal image,
rehearsal line, or roving drift that the character is experiencing.

The beat is:

```
RuntimeThoughtBeatV1:
  thought_beat_text    — 2-3 sentences of inner monologue
  mood_tags            — for Lyria / audio mapping
  residue_candidates   — tiny distilled sidecar for memory
  counterfactual_image — what the character is imagining (optional)
  spoken_line_fragment — rehearsed speech fragment (optional)
```

**Contract:**
- Kernel chooses the cognitive situation (concern, operator,
  retrieved material, context branch)
- Runtime projector LLM realizes it as inner-life prose
- Stage consumes the rendered beat (narration, audio, visualization)
- Kernel receives at most a tiny distilled residue, not full prose

**Status (2026-03-20):** RuntimeThoughtBeatV1 replay is working.
Haiku at ~3.3s/cycle (default), Sonnet at ~7.6s/cycle (escalation
for complex cycles). Operator routing produces visible quality
differences. The narration inhabits the cognitive events.

Known tension: the LLM generates richer inner life than the kernel's
events justify. The narration is partly disconnected from actual
cognitive computation. The feedback loop is the fix.

**Next step:** The feedback loop experiment. Feed the narration
back as a new episode. Run another cycle. Does retrieval change?
This tests whether accumulation matters — the D9 question in
miniature.

**Success condition:**
Both gates pass. Stage legibility AND structural divergence with
feedback. If only gate 1 passes, the kernel isn't earning its keep.

## Secondary Targets (parallel, not blocking)

### Provocation seam propagation
Codex1 is patching the generation pipeline so provoked situation
state feeds concern inference and operator scoring. Rerun Kai
control after that lands.

### Lyria parameter tuning
The Lyria → traversal connection works but the mappings need
interactive dialing in. BPM ranges, density curves, prompt
derivation quality.

### Project page
`scope-drd/notes/daydream/self_docs/project_page_v3/` has a draft
and diagram prompts. Diagrams need revision (data-driven, not
generic flowcharts). Page text is ready for editing.

## Previous Sprint Status

**supply_v1: practical pass (closed)**
- Tessa: clean pass (7 keepers / 4 reserves)
- Kai: narrow pass (7 keepers / 3 reserves)
- Artifacts: keeper bank, pack registry, closeout note
- See `supply-v1-closeout.md`

## Autonomy Contract

Same milestone-based discipline as the previous sprint.
Reporting at milestone boundaries only.

Escalation triggers:
- the RuntimeThoughtBeatV1 shape needs to change
- the kernel's output format is insufficient for conditioning
- Lyria or narration bridge integration blocks unexpectedly
- a 5 Pro reply materially changes the project framing

## What is NOT in this sprint

- Full runtime memory writeback (dangerous — self-echo risk)
- Multi-situation generation (Q5/Q7/Q12 — later phase)
- Scope/video rendering (GPUs scarce)
- L2 kernel refactor beyond what's needed for thought beats
- New supply/generation pipeline work
- Full provocation-to-generation authoring loop
