# Current Synthesis

Date: 2026-03-15

Purpose: a top-level synthesis for the `pressure-engine-reframe`
subtree after the generation sprint, bridge tests, supply pilot,
and deep-research-03 replies.

This is not a dashboard, not a sprint contract, and not a replacement
for the canonical architecture docs. It records the current read on:

- what is settled
- what is proven
- what changed unexpectedly
- what the real open seam is now

Related:
- `11-settled-architecture.md`
- `21-graph-interface-contract.md`
- `27-authoring-time-generation-reframe.md`
- `30-authoring-time-generation-prototype-spec.md`
- `34-broader-application-surface.md`
- `35-operator-taxonomy-status.md`
- `reading-list/15-experiment-operating-model.md`
- `reading-list/16-lesson-milestone-not-mechanism.md`
- `first-round-03/deep-research-03/replies-02/Ask-A.md`
- `first-round-03/deep-research-03/replies-02/Ask-B-01.md`
- `first-round-03/deep-research-03/replies-02/Ask-B-02.md`

---

## Bottom line

The project is now past "does the middle layer exist?" and into
"what are the real bottlenecks to usable material and watched
experience?"

Two things are now true at the same time:

1. The authoring-time generation lane is real.
   The pipeline can produce keeper-quality nodes, those nodes can
   survive traversal, and `supply_v1` is good enough to count as a
   practical pass.

2. The watched-runtime lane is still under-proven.
   The system can traverse and emit state, but the question
   "does this feel like a mind at work?" is still open.

The architecture is therefore best understood as **two connected
fronts**, not one finished stack:

- **authoring/material-supply lane**
- **runtime/watchable-experience lane**

They meet at the authored graph, but they are not yet fully unified in
code.

---

## What is settled

### 1. The graph remains the membrane

The authored graph stays thin, curated, and cross-lane.

- the graph is not raw runtime state
- the graph is not a fixture dump
- the graph is not a prompt transcript

`21-graph-interface-contract.md` still stands.

### 2. `L3` is the shipping traversal layer

`L3` is drama management over authored material.
It is not the same thing as `L2` cognition.

The City Routes and Graffito work changed sequencing, not this
architectural claim.

### 3. The generation lane is an authoring-time orchestration lane

`27-authoring-time-generation-reframe.md` was correct.
The critical bottleneck was material supply, not more `L3` theory.

In practice this means:

- `L1` is authoring-time orchestration and curation flow
- `L2` in this lane is cognition-shaped reaction generation
- the output is candidate graph material plus provenance sidecar

### 4. Fixtures are task views, not canon

The benchmark fixtures are not the world.
They are compiled authoring/test views over world material.

This matters for both architecture and future schema cleanup.

### 5. Operating discipline is now explicit

The research loop now has a real operating model:

- diagnostic vs round runs
- frozen surfaces for comparability
- run ledger
- Pareto discipline
- milestone-based verification instead of executor self-report

`reading-list/15-experiment-operating-model.md`,
`reading-list/16-lesson-milestone-not-mechanism.md`, and the repo-level
executor/verifier protocol now govern this more honestly than ad hoc
reruns did.

---

## What is proven

### 1. The generation pipeline works in the narrow but important sense

The current prototype in
`daydreaming/authoring_time_generation_prototype.py` can:

- condition on concerns, appraisal, practice, retrieval, and operator
- generate batches
- admit shortlisted nodes
- log runs and curation judgments
- produce pack-ready material

The live provider path is Anthropic Sonnet, not Gemini.

### 2. Bridge survivability is proven across two fixtures

Both patch tests passed in the narrow sense:

- Tessa / rationalization
- Kai / avoidance

Generated-curated nodes can be inserted into live micrographs without
breaking traversal, and human read preferred generated prose in both
tests.

What this proves:

- generated nodes are not dead leaves
- seam-compatible generated material is real
- the graph slot can hold generated-curated prose

What it does not prove:

- prose-sensitive traversal
- full graph assembly
- multi-situation progression

### 3. `supply_v1` is a practical pass

The supply pilot is good enough to stop asking "can this work?"

Current honest read:

- `Tessa`: clean pass
- `Kai`: narrow pass

This is enough to move from bridge proof toward scaling, banking,
pack assembly, and next-phase authoring questions.

Important: the proven configuration includes temperature 0.7,
authored framing rotation, and the consider-alternatives system
prompt. These are not optional polish — they are load-bearing
for keeper yield. Running the pipeline at the old temperature 0.2
without framing will reproduce the old narrow results.

### 4. Cheap levers mattered

The system spent a while searching for deeper mechanism answers before
fully testing the cheapest ones.

What actually changed the Tessa results most:

- temperature increase
- lighter system-prompt stance
- framing variation
- authored-first memory ordering

The lesson from `reading-list/16-lesson-milestone-not-mechanism.md`
is real:

- cheap levers first for creative spread
- structural work early for honesty and measurement

Both were needed.

---

## What changed unexpectedly

### 1. The seam problem was partly self-inflicted

`Q6` showed that the runtime already knew more provenance than the
projection preserved.

The deterministic lineage fix was a cheap, high-value correction:

- provenance should come from runtime state where possible
- practice should come from runtime state where possible
- not every graph-thin result was a creativity failure

This was a measurement and seam-honesty win, not just a diversity fix.

### 2. Prompt anchoring was stronger than expected

The prompt was leaking ranked internal ontology too directly.
That encouraged dominant-concern collapse and repetitive staging.

`Q11` was right about the diagnosis:

- raw state dump is the wrong prompt surface
- compiled given circumstances are better

But the A/B also showed that prompt honesty and prompt diversity are
not the same thing.

### 3. `Q11` was necessary but not sufficient

`circumstances_v1` improved prompt honesty.
It did not widen the Tessa shortlist enough by itself.

The next prompt bottleneck appears to be:

- world/situation framing
- candidate-level variation
- not just raw JSON vs compiled view

### 4. Within-operator diversity matters as much as operator diversity

For Tessa, all-rationalization may be correct.
The real question became:

- are these different dramatic jobs within rationalization
- or just paraphrases of one beat

This was an important correction to how the batch results were being
read.

---

## Design space placement

The Ask-A design space analysis places this project in the
**believable-agent / affective-agent** region — closest to
FAtiMA (Dias et al.) and Oz/Hap (Loyall), not to LLM-character
companies or game AI. The control primitive is concern-driven
operator selection modulated by appraisal, memory, and social
practice.

The Mueller foundation provides named, authorable coping modes
that are uniquely valuable for a conducted instrument where the
performer needs to understand and steer what the character is
doing. Active inference and RL foundations would give stronger
surprise/adaptation but at the cost of inspectable, nameable
cognitive moves.

The project's unique differentiator in this space: **Mueller gives
us the cognitive process between interactions — the daydreaming
itself.** FAtiMA and most affective architectures focus on action
selection during social interaction. This system focuses on
rumination, avoidance, rehearsal, rationalization — the inner
monologue when no one is watching. No other architecture makes
that process first-class, inspectable, and steerable. That is the
thing to protect and not accidentally optimize away.

The nearest risk identified: LLM generation could undermine the
structured middle layer by producing good material with just
prompting and temperature. The current answer is that the middle
layer provides controllability and interpretability that pure
LLM cognition does not — but this remains an honest tension,
not a settled question.

See `reading-list/17-nearest-neighbors-reading-list.md` for the
full comparison (Loyall, ABL, FAtiMA extractions now exist).

---

## The architecture read now

### Authoring lane

Best current description:

- Prep / world inputs
- `L1` orchestration, admission, curation, keeper banking
- `L2`-style authoring-time generation
- graph compilation / pack assembly

### Runtime lane

Best current description:

- authored graph
- `L3` traversal scheduler
- runtime Director feedback
- adapter / normalized packet
- dashboard / narration / audio / stage consumers

### Important clarification

The current runtime Director is **not** a true world-event author.

It is better described as:

- interpretive perturbation
- situation activation nudge
- feedback echo

That is a real implemented component.
It is not yet the missing world-update mechanism.

---

## The real open seam

The major unresolved architecture question is now:

**What is the typed object between world/situation change and
reaction generation?**

`Ask-B-02.md` is the clearest answer so far:

- the missing object is a typed provocation proposal
- it should sit upstream of generation
- it should be a proposal sidecar, not silent canon mutation

`Ask-B-01.md` sharpens the state discipline around that seam:

- external world change should stay separate from internal carried
  state
- local situation state should stay separate from cross-situation
  residue

The likely next-phase split is:

- external provocation proposal
- carry-forward internal state
- active-situation local state

That is the current best answer to the haze around "who updates the
world?"

### Working vocabulary to freeze for the next phase

The strongest concrete takeaway from `Ask-B-01.md` is that the
terminology should stop drifting.

Use:

- **Provocation Generator** for the authoring-time world/situation
  proposal writer
- **Director** for the runtime interpretive perturbation agent

Do not use one mushy "Director in two modes" phrase for both.

### Working state split to freeze for the next phase

The current `DerivedSituationState` idea is overloaded.
The cleaner working split is:

- **`FixtureDeltaV1`**
  external world-authoring change or situation/event proposal
- **`CarryForwardStateV1`**
  cross-situation internal residue that survives the boundary
- **`SituationLocalStateV1`**
  active-situation local state that is remapped on the next boundary

The important behavioral rule is:

- not reset
- not full carry
- **carry + remap**

That is the clearest current answer to what should happen when a new
situation arrives.

### Reduced context for provocations

The Provocation Generator should not see raw internal state dumps.
It probably does need a reduced progression summary.

The working object here is:

- **`ProvocationContextV1`**

Enough to make the push specific.
Not enough to couple the prompt to every internal implementation
detail.

### What should not happen

Do not:

- pretend the runtime Director already fills this role
- collapse internal progression into fixture deltas
- mutate canon silently during generation experiments

### Cheapest next architecture experiment

After the current supply closeout:

- hand-author 2-3 provocation sidecars
- overlay one into a Kai or Tessa run before `build_causal_slice()`
- compare against no-delta control

That tests the missing seam without expanding the runtime Director or
reopening the whole architecture.

---

## The watched-runtime correction

The first genuinely watchable thing is probably not video.

The more honest first watchable target is:

- inner-life surface
- narration companion
- audio response (Lyria Real Time — viable now, no GPU needed)
- traversal / selection legibility

Video remains downstream and contingent (GPUs scarce).

This does not invalidate the supply lane.
It means the whole project should be understood as having two distinct
evaluation questions:

1. can we produce enough good graph material
2. does watching/listening to the runtime feel like anything

The first is now partly answered.
The second is still open.

### Where we sit on the evaluation ladder

From `experiential-design/13-project-state.md`:

1. Scheduler dynamics work? — **Yes**
2. Director changes trajectory? — **Partially**
3. Stage output feels like anything? — **Unknown**
4. 12-cycle run feels dreamlike? — **Unknown**
5. Different briefs produce different dreams? — **Unknown**

We are at Level 2-3. **Level 3 is the most important unanswered
question.** Everything downstream is conditional on it. The cheapest
way to test it is: take an existing traversal trace, wire it to
inner-life visualization + Lyria RT, and watch/listen for 2-3
minutes.

---

## What not to do with this note

Do not use this note as:

- a sprint plan
- a replacement for `11-settled-architecture.md`
- a replacement for `current-sprint.md`
- a substitute for reading the actual Ask A / Ask B replies when
  implementing the next seam

This note is a compression layer.

Use it when you need to recover:

- the current project shape
- the current strongest claims
- the current honest uncertainty

---

## Canonical references from here

If you only read a few docs after this one, read:

1. `11-settled-architecture.md`
2. `21-graph-interface-contract.md`
3. `27-authoring-time-generation-reframe.md`
4. `30-authoring-time-generation-prototype-spec.md`
5. `reading-list/15-experiment-operating-model.md`
6. `reading-list/16-lesson-milestone-not-mechanism.md`
7. `first-round-03/deep-research-03/replies-02/Ask-B-02.md`

If you want the adjacent conceptual placement:

8. `34-broader-application-surface.md`
9. `first-round-03/deep-research-03/replies-02/Ask-A.md`

If you are implementing the next seam:

10. `first-round-03/deep-research-03/replies-02/Ask-B-01.md`

If you want the nearest-neighbor comparison:

11. `reading-list/17-nearest-neighbors-reading-list.md`
