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
- `daydreaming/Notes/supply-v1-closeout.md`
- `daydreaming/out/authoring_time_generation/keeper_bank_supply_v1.jsonl`
- `daydreaming/tools/keeper_bank.py`
- `daydreaming/tools/pack_registry.py`
- `daydreaming/fixtures/patch_pack_registry_supply_v1.yaml`

---

## Bottom line

The project is now past "does the middle layer exist?" and into
"what are the real bottlenecks to usable material and watchable
experience?"

Two things are now true at the same time:

1. The authoring-time generation lane is real.
   The pipeline can produce keeper-quality nodes, those nodes can
   survive traversal, and `supply_v1` is good enough to count as a
   practical pass.

2. The watchable-runtime lane is still under-proven.
   The system can traverse and emit state, but the question
   "does this feel like a mind at work?" is still open.

The architecture is therefore best understood as **two connected
fronts**, not one finished stack:

- **authoring/material-supply lane**
- **runtime/watchable-runtime lane**

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

Important caveat: these patch fixtures held traversal-facing structure
constant between `H` and `G` on purpose. The current traversal feature
arm does not score prose, so `H/G` traversal traces were identical. This
is a **slot survivability** proof plus human quality judgment, not
prose-sensitive scheduling.

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

Concrete artifacts:

- `daydreaming/out/authoring_time_generation/keeper_bank_supply_v1.jsonl`
- `daydreaming/fixtures/patch_pack_registry_supply_v1.yaml`
- `daydreaming/tools/keeper_bank.py`
- `daydreaming/tools/pack_registry.py`
- `daydreaming/Notes/supply-v1-closeout.md`

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

The crispest framing (from Ask-A-alt):

> "Our foundation is tuned for one narrow prize: **legible inner
> drift under partial human steering.** If that prize is the one
> you actually care about, the architecture makes sense. If the
> real prize is something else, one of the neighboring foundations
> is probably cleaner. The mistake would be to blur those prizes
> together."

The nearest risk identified: LLM generation could undermine the
structured middle layer by producing good material with just
prompting and temperature. The current answer is that the middle
layer provides controllability and interpretability that pure
LLM cognition does not — but this remains an honest tension,
not a settled question. D9 (falsification) is out to 5 Pro
specifically to test this claim.

See `reading-list/17-nearest-neighbors-reading-list.md` for the
full comparison. Extractions now exist for Loyall (18), ABL (19),
FAtiMA (20), narrative planning/IPOCL (21), Sabre (22),
MINSTREL follow-up (23), Generative Agents (24), and
Character is Destiny (25).

Key steals from the nearest-neighbor analysis:

- **FAtiMA's meta-belief seam** — foreign computation participates
  through named typed fields, not by replacing the control loop.
  Right pattern for Provocation Generator integration.
- **Loyall's irrationality-as-requirement** — avoidance,
  defensiveness, and self-protective reinterpretation are features,
  not error cases around a planner. Design guardrail for L2 refactor.
- **IPOCL's pursuit-thread object** — tracks multi-situation
  intentional continuity (goal, motivating event, supporting nodes,
  status). The missing representation between L2 concerns and L3
  multi-situation arc coherence. Banked for Q5/Q7/Q12 phase.
- **Sabre as explanation critic** — can each character action be
  justified from the character's believed state? Useful as a
  verification layer over multi-step material.
- **Generative Agents as baseline to beat** — pre-membrane,
  pre-middle-layer. Main value is clarifying what the structured
  cognitive layer buys vs. pure LLM memory/reflection.
- **Character is Destiny's gold-motivation upper bound** — apparent
  character inconsistency usually masks retrieval failure, not
  reasoning failure. Validates retrieval-focused work (Q8).

### Counterfactual preservation — answered (D6)

Mueller's DAYDREAMER maintains unrealized scenarios as traversable
memory. 5 Pro's answer: **yes, preserve unrealized alternatives,
but narrowly.** One preserved sibling per step as a
`CounterfactualEpisodeV1` with explicit `modality` and
`realization_status`. Feed it back into retrieval so the character
can remember what they almost did. The dashboard needs a ghosted
"strongest unrealized line" channel — but only the strongest, not
every discard.

Key design rule: `commit_type = none` should still yield an
Episode. "Not enacted" ≠ "not remembered."

Priority: early architectural correction. Before full watchable
runtime work, but not before the cheapest visual/audio run. The
kernel already has hypothetical preservation; the generation lane
doesn't yet.

See `replies-04/D6-counterfactual-preservation.md`.

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

### Continuity hook now looks likely

The newer nearest-neighbor replies sharpen one thing beyond the earlier
Ask B answers:

- concerns are pressure
- they are probably **not enough** by themselves to carry
  multi-situation intentional continuity

The most likely next small object here is a lightweight
**`PursuitThreadV1`** or reserved `pursuit_ref`, kept lane-local at
first.

That should be treated as:

- a continuity hook
- a verifier/audit handle
- not a reason to replace `L3` with a planner

It belongs after the provocation seam is real, but before claiming
serious multi-situation coherence.

### Belief / observation should stay selective

The narrative-planning replies also sharpen the boundary here:

- keep authored truth as base world state
- add selective belief / observation / automatic-aftermath semantics
  only where secrecy, concealment, or mistaken inference matter

Do **not** make full belief-state reasoning the new baseline cost of
every scene.

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

### Cheapest next experiments (priority order)

**1. RuntimeThoughtBeatV1 on Puppet Knows** (highest priority)

- run the kernel's `bb puppet-knows-autonomous` to produce a
  cognitive trace
- for each cycle, call an LLM with the kernel's state (concern,
  operator, retrieved material, context branch)
- produce `RuntimeThoughtBeatV1` per cycle
- render through the narration bridge / cognitive HTML
- judge: does this read as a person thinking?

This is the Level 3 test in its most honest form.

**2. Provocation seam propagation** (still valuable)

After the current supply closeout:

- patch the generation pipeline so provoked situation state feeds
  concern inference and operator scoring (codex1 is on this)
- hand-author 2-3 provocation sidecars
- overlay one into a Kai or Tessa run
- compare against no-delta control

Success condition: at least one admitted candidate should be
structurally new because of the delta, not just a paraphrase.

---

## The watchable-runtime correction

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

The newest replies sharpen what that second question likely needs:

- viewer-facing runtime projections richer than the current thin
  Director-facing packet
- explicit execution/selection state that is live, not just logged
- probably one ghosted "strongest unrealized line" or counterfactual
  channel, kept lane-local rather than dumped into the graph

### The deepest gap: runtime content, not just runtime dynamics

The Clojure kernel runs Mueller's cognitive loop: operators fire,
contexts sprout, memories retrieve, concerns compete. The Python
L3 scheduler scores candidates against tension, energy, and
conductor bias. Both produce dynamics. Neither produces the
**specific thought** — the particular rationalization, the
particular reversal image, the particular rehearsal line.

Mueller's DAYDREAMER generates that content inside the cognitive
loop. When RATIONALIZATION fires, it constructs "if I were dating
him he'd go to Cairo, I'd lose my job" from the retrieved episode
and the concern. Our kernel fires the operator and selects a
pre-authored node tagged for rationalization.

The gap is not narration styling. It is a missing runtime object:

**`RuntimeThoughtBeatV1`**

```
RuntimeThoughtBeatV1:
  thought_beat_text    — 2-3 sentences of inner monologue
  mood_tags            — for Lyria / audio mapping
  residue_candidates   — tiny distilled sidecar for memory
  counterfactual_image — what the character is imagining (optional)
  spoken_line_fragment — rehearsed speech fragment (optional)
```

Produced by: one bounded LLM call per kernel cycle, conditioned
on the kernel's cognitive state (concern, operator, retrieved
material, context branch, immediate prior residue).

Consumed by: narration bridge, Lyria audio, inner-life
visualization.

Written back to kernel: **only a tiny distilled residue**, not
the full prose. Full writeback risks self-echo, drift, and memory
pollution (the same Q8 self-priming problem, now at runtime).

The contract:
- kernel chooses the cognitive situation
- runtime projector LLM realizes it as inner-life prose
- stage consumes the rendered beat
- kernel receives at most a distilled residue object

This is probably the most important thing to build next. It is
what turns "watching infrastructure" into "watching a mind."

The test: run the Puppet Knows cognitive trace with each cycle
producing "Snake stares at the brushstrokes and thinks..." instead
of "RATIONALIZATION activates — seam is honesty." If the output
reads as a person thinking, the two-lane architecture works with
a runtime projection layer.

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

## Pending and potentially consequential

Ten questions (D1-D10) are out to 5 Pro via
`04-5pro-nearest-neighbor-questions.md`. Most will enrich
understanding without changing direction. Four could change
the architecture:

### D7 (provocation seam) — answered
Confirmed the four-object split. Pursuit thread is a reserved
hook (`pursuit_ref`), not a v1 object. Two new optional fields
from Sabre: `observation_scope` (who notices?) and
`automatic_aftermath` (mandatory consequences). Add after the
first successful provocation experiment, not before.
See `replies-04/D7-provocation-seam-answer.md`.

### D9 (falsification)
Could reframe the middle layer's role. If the answer is
"the middle layer's value is primarily steerability and
provenance, not generation quality," the cognitive layer
becomes infrastructure for the instrument (dashboard,
conductor), not for the generation pipeline. That's a
clarifying reframe, not a rejection — but it changes what
we invest in.

### D6 (counterfactual preservation) — answered
Yes, preserve one unrealized sibling per step as lane-local
`CounterfactualEpisodeV1`. Not in the graph. Not every discard.
Early architectural correction, not v1 blocker. The kernel
already has hypothetical preservation; the generation lane needs
to catch up. See `replies-04/D6-counterfactual-preservation.md`.

### D10 (sequencing)
Will govern what gets built next. Which imports belong
before the provocation experiment, which after, which
should wait for the watchable-runtime front.

D6 and D7 are now answered. Both confirmed rather than
disrupted the current direction. D9 (falsification) and
D10 (sequencing) are still pending — D9 is the most likely
to shift the ground if it comes back saying the middle
layer's value is primarily steerability rather than
generation quality.

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
12. `reading-list/18-loyall-extraction.md` (believability requirements)
13. `reading-list/20-fatima-extraction.md` (meta-belief seam pattern)
14. `reading-list/21-narrative-planning-extraction.md` (pursuit threads)

If you want the D-question replies (provocation seam + counterfactual):

15. `first-round-03/deep-research-03/replies-04/D6-counterfactual-preservation.md`
16. `first-round-03/deep-research-03/replies-04/D7-provocation-seam-answer.md`

If you want the pending deep-research questions:

17. `first-round-03/deep-research-03/04-5pro-nearest-neighbor-questions.md`
