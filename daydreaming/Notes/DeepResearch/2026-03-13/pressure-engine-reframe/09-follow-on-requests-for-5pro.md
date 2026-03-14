# Follow-On Requests for GPT-5 Pro

This note stores the next round of GPT-5 Pro requests to send after the
initial pressure-engine review wave.

The goal is to avoid losing prompt wording while earlier replies are
still in flight.

---

## 1. Prior-work scan

Use this now that `08-prior-work-questions-for-5pro.md` has been
expanded.

```text
Please do a deep architectural scan of this note:

daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/08-prior-work-questions-for-5pro.md

Read the docs it points to as needed.

This is not a generic literature review. I want a selective, high-signal
scan of prior work that is directly useful for this project.

Please answer the questions in the doc directly and be decisive. In
particular, I care about:
- what existing bodies of work should ground L1, L2, and L3
- what representation patterns we should appropriate
- what evaluation frameworks we should steal
- what mixed-initiative workflow models are relevant
- what adjacent domains matter for the conducted-performance dimension
- what tempting prior work is actually a distraction

Please rank rather than just list. For each layer, give the top 2-3
bodies of work that matter now, what to steal from each, and what to
ignore for now.
```

---

## 2. Boundary and glossary adjudication

Use this after the first wave of replies has landed. This is the
follow-up that should force vocabulary and abstraction decisions instead
of another broad architectural essay.

```text
Please synthesize the answers to the earlier pressure-engine review
requests together with this note:

daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/08-prior-work-questions-for-5pro.md

Include the reply docs from the earlier requests as context, along with
the core reframe docs they reviewed.

I do not want another broad architectural essay. I want a cleaned-up
boundary and vocabulary decision that is narrow enough to build.

Please decide, explicitly, for each of these concepts whether it should
be:
- shared across levels
- split into level-specific variants
- renamed
- dropped

Concepts to adjudicate:
- Pressure / Concern
- Operator
- Context
- Proposal
- CommitRecord
- Adapter
- TraversalIntent
- DreamDirective

Please structure the answer like this:
1. Top-line architecture judgment after considering all prior answers
2. Shared abstractions vs level-specific abstractions
3. Revised glossary
4. Revised type/model sketch
5. Shipping-path vocabulary vs research-path vocabulary
6. Terms or abstractions to retire
7. One revised architecture diagram
```

---

## 3. Experiment design pass

Use this after the architecture and vocabulary questions have been
tightened enough that GPT-5 Pro can propose concrete tests rather than
speculative generalities.

```text
Please use the earlier pressure-engine review answers and the resulting
architecture/boundary decisions to design the smallest falsifiable
experiments for this project.

I do not want a roadmap deck. I want 2-4 concrete experiments that test
the real unknowns without destabilizing the watched-run shipping path.

Please include:
- one experiment for L1 world-building pressure loops
- one experiment for L3 traversal as more than heuristic scheduling
- one experiment that protects and clarifies the current shipping path

For each experiment, specify:
- the hypothesis
- the minimum input/state required
- the operators or mechanisms being tested
- the human role
- the output artifact
- the evaluation criteria
- the failure condition
- what existing seam/code/doc boundary it touches
- whether it belongs on the shipping path or the research path

Please end with a recommended order to run the experiments.
```

---

## Why these three

The three follow-on requests do different jobs:

- `08` widens the grounding and asks what existing work is worth
  appropriating.
- the boundary/glossary pass turns broad critique into architecture
  decisions
- the experiment pass turns those decisions into concrete falsification
  steps

That sequence should produce better output than sending another generic
"what do you think?" request.
