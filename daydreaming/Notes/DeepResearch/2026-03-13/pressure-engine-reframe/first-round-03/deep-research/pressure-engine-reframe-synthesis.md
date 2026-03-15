# Pressure-Engine Reframe: Current Synthesis

## Status and scope

This note is for a collaborator who has not followed the full research log.
It synthesizes the selected March 2026 pressure-engine-reframe documents
into one readable account of the current state.

It separates three things that the log often interleaved:

1. what is **architecturally settled**
2. what has **experimental support**
3. what is a **planned direction rather than a demonstrated result**

The authority here is the selected pressure-engine-reframe notes, especially
`11-settled-architecture.md`, `21-graph-interface-contract.md`,
`05-stage-integration.md`, `27-authoring-time-generation-reframe.md`,
`28-l2-schema-from-5pro.md`, the source-lineage syntheses, and the March 14
pilot / City Routes experiment notes.

---

## 1. Executive summary

The pressure-engine-reframe arc has reached a stable architectural position,
but not a finished product position.

The stable core is this:

- There are **two lanes**:
  - a **research/authoring lane** that discovers, generates, critiques, and
    curates material
  - a **shipping/performance lane** that traverses authored material during a
    run
- There are **three levels** with distinct jobs:
  - **L1** is a narrow authoring critic / expander in the research lane
  - **L2** is the real Mueller-derived character exploration engine
  - **L3** is a traversal scheduler over authored graph material, not a
    cognitive engine
- The **authored graph** sits between the lanes as the membrane. It stores
  authored structure plus stable cross-lane residue, but not live runtime
  internals.
- The **conductor** is above `L3` as a biasing authority, not a second chooser.
  There is one control plane: conductor bias → scheduler choice → stage
  execution.
- The **inner-life dashboard** remains the primary early output. Finished video
  is secondary until watched runs earn that priority.

The experimental picture is narrower than the architecture picture.

What the March experiments support:

- A **Façade-shaped scheduler** beats simpler traversal on a pilot graph.
- On a richer graph, **graph structure matters as much as scheduler logic**.
- A small **DODM-style feature registry** adds real structural behavior beyond
  a Façade-only arm, especially around threshold recovery and event approach.
- **Conductor influence exists**, but only in a limited and asymmetric way on
  the current City Routes substrate.

What they do **not** yet support:

- a mature performance surface
- a fully validated conducted experience
- proof that trace-level wins survive watched playback
- proof that cross-level pressure propagation is solved
- proof that the upstream material-supply problem is solved

That last point is why the sequencing changed.

Earlier, the main open question looked like `L3` scheduler theory. After the
Graffito and City Routes passes, the bottleneck is now judged to be **material
supply**: how to go from narrative primitives to enough graph-ready material
without hand-authoring every node. So the next critical-path experiment is now
**authoring-time generation**, with the `L1` critic becoming a second-stage
repair layer rather than the first upstream move.

A compact status map:

| Topic | Current status |
|---|---|
| Two-lane / three-level architecture | Settled |
| Graph membrane and field contract | Settled |
| One control plane and `DreamDirective` seam | Settled |
| `L2` as engine core, `L3` as scheduler, `L1` as narrow authoring support | Settled |
| Narrow `L3` scheduler thesis | Provisionally supported |
| DODM-style feature layer on City Routes | Supported on current substrate |
| Conductor expressivity | Real but limited / asymmetric |
| Authoring-time generation as next critical path | Settled as execution order |
| `L1` critic effectiveness in the new order | Untested |
| `L2` middle-layer schema | Conceptually frozen, implementation not yet proven |
| Watched-run validation | Still open |

---

## 2. What is settled architecturally

### 2.1 The overall shape

The canonical architecture is still the one in `11-settled-architecture.md`:

```text
RESEARCH / AUTHORING LANE

Source Bible / World Sketch
    ↓
L1 Authoring Critic / Expander
    ↓
L2 Character Exploration Engine
    ↓
Candidate scenes / events / variants
    ↓
Human curation + authoring
    ↓
Authored Dream Graph


SHIPPING / PERFORMANCE LANE

Authored Dream Graph
  + traversal state
  + conductor bias
  + director feedback
    ↓
L3 Traversal Scheduler
    ↓
Adapter / Projection Layer
    ↓
DreamDirective / normalized cycle packet
    ↓
Dashboard + renderer + music + stage
```

The key boundary claims are firm:

- `L2` is the **real engine core**.
- `L3` is **drama management over authored material**, not cognition.
- `L1` is **research-stage authoring support**, not autonomous worldbuilding.
- The graph is not an accidental byproduct. It is the explicit membrane between
  lanes.

### 2.2 The three levels

#### `L1` — authoring critic / expander

`L1` schedules **ArtifactDeficiency** objects: computable structural defects in
an authored world or graph. It is narrow on purpose. Its output is typed local
repairs, proposals, and evaluations, not free-running world invention.

Canonical examples include:

- goal without resistance
- conflict without response
- setup without payoff
- place without role
- disconnected subgraph

The important current clarification is that this broader `L1` conception still
stands architecturally, but the **first upstream experiment has moved earlier**:
before a critic can critique enough material, the project needs a way to produce
that material in the first place.

#### `L2` — character exploration engine

`L2` remains the live pressure engine. Its job is narrower and more important
than the rest of the stack:

- maintain active concerns
- appraise developments
- retrieve and reactivate episodes
- choose locally available exploratory operators
- preserve alternate contexts and traces
- render an intelligible inner-life stream

This is the Mueller-derived level. It is where concern selection, reminding,
serendipity, and inner-life dynamics live.

#### `L3` — traversal scheduler

`L3` schedules traversal over already-authored graph material. It does not fork
Mueller contexts, does not generate new cognition, and does not become a second
engine.

Its state is about sequence quality and traversal history:

- current node
- recent path
- visit counts / recency
- situation activation
- event-approach state
- conductor bias

Its diagnostics are not concerns; they are critic signals over sequence quality:

- overexposure
- recall due
- tension flatline / spike
- exhausted node
- event proximity
- avoided material

Its exposed runtime vocabulary is the five traversal intents:

- `dwell`
- `shift`
- `recall`
- `escalate`
- `release`

One important nuance from later review: these five verbs are good **seam
vocabulary** and good human-readable scheduler output, but they should not be
mistaken for the scheduler's deep internal ontology. Internally, the scheduler
wants a pipeline plus a feature registry, not a little intent metaphysics.

### 2.3 The graph membrane is frozen

`21-graph-interface-contract.md` is the current seam contract.

The graph stores three kinds of things:

1. **authored graph structure**
2. **stable graph-readable residue** that other lanes can read
3. **proposal / provenance records** that must survive beyond one cycle

What **must** live in the graph:

- node / edge identity
- traversal annotations such as availability, priority, `delta_tension`,
  `delta_energy`
- event-facing fields such as setup/payoff refs, event ids, commit potential,
  option effect, resolution-path count
- stable cross-lane tags such as `pressure_tags[]`, `practice_tags[]`,
  `contrast_tags[]`, `origin_pressure_refs[]`
- `L1` deficiency annotations, proposal refs, evaluation summaries
- provenance fields such as source lane, scope, revisability, source ref

What must **stay out** of the graph:

- live `L2` concern stacks
- reminded episode refs from the current cycle
- appraisal objects
- emotion stacks
- context trees / assumption patches
- live `L3` diagnostics
- lookahead paths
- raw conductor state

This is one of the strongest settled decisions in the whole arc. The graph is a
stable membrane, not a giant shared blackboard of current runtime state.

### 2.4 Stage integration is settled enough

`05-stage-integration.md` survives the reframe with one important clarification:
where it conflicts with the canonical control story, the canonical story wins.
That leaves a stable playback contract:

- the stage is a **deterministic actuator**
- the upstream engine / scheduler emits a **`DreamDirective`**
- the `DreamDirective` has an **engine block** for provenance and a **stage
  block** for the actual stage controls
- the stage only needs to understand the stage block
- commit semantics stay entirely upstream; the stage renders whatever it is told

This also fixes the conductor role:

- the conductor does **not** issue independent stage commands that bypass the
  scheduler
- the conductor biases the upstream chooser
- the scheduler emits one decision
- the stage executes that decision

### 2.5 The dashboard has first priority

A recurring source of confusion in the log was whether the dashboard was a debug
surface or a product surface. The settled answer is explicit:

- the inner-life dashboard is not a debug afterthought
- it is the primary early output
- narration, cognitive visualization, and system state are part of the watched
  experience

This also ties back to Mueller Appendix B: the narration layer has real lineage.
It should not collapse into raw trace inspection.

### 2.6 The strongest alternative was considered and not adopted

The strongest competitor architecture in the notes is a graph-centric two-stage
stack:

```text
Source Bible → Unified Graph Constructor → Executable Graph → Thin Traversal Policy → Render
```

That alternative is acknowledged as the strongest **product-clean** competitor.
It reduces the graph/membrane confusion and makes cross-level propagation look
like less of a metaphysical problem.

But the selected docs do **not** switch to it. They keep the current three-level
split because it is better for present research discipline:

- clearer failure localization
- cleaner staged experiments
- better fit to the source lineages
- better falsification architecture right now

So the graph-centric alternative remains a background tension, not the active
architecture.

---

## 3. What has been proven experimentally

The experimental story is about `L3`, because `L3` is where the March work was
actually executed.

The right way to summarize it is not “the scheduler is solved.” It is:

- some claims are now supported on specific substrates
- some claims are still only provisional
- some questions have moved downstream into playback and conducting

### 3.1 Graffito pilot: the scheduler shape is real on a pilot substrate

The Graffito runs were deliberately narrow.

#### Run 1

The first Graffito pilot compared:

- a simple baseline walk
- a Façade-shaped pilot scheduler over the same graph

The baseline stayed entirely inside `s1`, used 8 unique nodes across 18 cycles,
and made 0 cross-situation moves.

The pilot scheduler used 10 unique nodes, split time between `s1` and `s2`,
made 5 cross-situation moves, and produced a more legible release / escalation
rhythm.

That is the first experimental bar the project needed: the scheduler did
something legible on the same authored material that the simple walk did not.

#### Run 2

The second Graffito pass widened the crossing seam and exposed more target-arc
knobs. The updated pilot:

- used 9 unique nodes over 18 cycles
- split 11 cycles in `s1` and 7 in `s2`
- made 6 cross-situation moves
- no longer collapsed into the earlier `camera -> monk -> drone` micro-loop

The real lesson of Graffito is not just “the scheduler works.” It is also:

- graph authoring matters a lot at this scale
- seam-node priorities can distort traversal strongly
- Graffito is a real pilot, but too small to prove the full `L3` thesis

That later point matters. The Graffito substrate can prove that tiered
trajectory-aware traversal is better than naive walking. It cannot carry the
full burden of event homing, cross-situation mixing, structural tension, or
resilience under graph changes.

### 3.2 City Routes A/B: the richer substrate revealed graph and scheduler separately

The first City Routes A/B pass did two things:

1. it proved the harness carried over to a larger graph without structural code
   changes
2. it exposed that the first problem on the richer graph belonged largely to the
   **graph**, not the scheduler

Initially, both arms overused a strong `s2 -> s5 -> s6` spine and underused
`s3_false_refuge` and `s4_public_spectacle`.

The fix was graph-side:

- reroute one early detour exit into spectacle
- reroute exchange aftermath into refuge
- soften a couple of escalation auto-wins
- restore a second attractor

After tuning, the kept City Routes A/B result looked like this:

- baseline still followed a more literal route spine
- scheduler arm visited spectacle and refuge, stopped collapsing into the
  threshold / exchange loop, and used exchange as a middle-to-late structural
  move rather than a basin it could not leave

This is why the City Routes notes matter. They separated two claims that are
very easy to confuse:

- scheduler design matters
- graph structure matters

Both turned out to be true.

### 3.3 Arm C: the feature registry started earning its complexity

Arm C adds the first DODM-style feature registry to the shared traversal
harness.

The important result is that the **first** arm-C run technically worked but
failed the real bar: it produced the same path as arm B, because the older local
soft-weight multipliers still dominated.

A narrow correction fixed that:

- keep the feature score primary in arm C
- demote the old local multiplier into a gentler soft-bias term

After that correction, arm C stopped being “better debug for the same path” and
became a genuinely different traversal.

The final kept arm-C pass:

- used 16 unique nodes
- reintroduced `s5_the_threshold` and made it central
- touched `e1_train_missed`, `e2_blackout_siren`, `e4_bridge_lockdown`, and
  `e3_wrong_handoff`
- used spectacle as setup toward threshold material rather than only as color

This is the first strong support for the claim that a DODM-style feature layer
adds something beyond a Façade-only traversal arm.

The strongest experimental reading is narrow and defensible:

- the feature registry changes the route for structural reasons that match the
  intended feature meanings
- threshold recovery and event approach are not just explanatory decorations
- the extra scoring layer has started to earn its complexity

The caution is also explicit:

- arm C still showed event-heavy reuse, especially around `e4`
- this is enough to support the feature layer on the current substrate, not to
  call the experiment globally finished forever

### 3.4 Robustness: arm C held up across seeds better than arm B

The robustness sweep is where the feature layer moves from “interesting single
run” to “enough to matter.”

Across 15 City Routes cases:

- arm B visited threshold in `9/15`
- arm C visited threshold in `15/15`
- arm B averaged `5.6` situations visited
- arm C averaged `6.0`
- only `3/15` cases produced identical arm-B / arm-C paths
- in `6/15` cases, arm C improved threshold recovery and event structure in the
  cases where arm B underperformed

The interpretation note is careful and right:

- the feature registry is robust enough to matter across seeds
- this is not yet conductor validation
- the `release_moves` metric was too blunt to tell a useful story here

So the defended claim is specific:

- the DODM-style layer consistently helps recover threshold material and event
  structure on the City Routes graph
- especially in cases where the Façade-only arm underperforms

### 3.5 Conductor tests: real, but limited and asymmetric

The conductor story is the most tempting thing to overstate.

#### First targeted pass

The first targeted conductor sweep produced **0/3** divergent seeds.

The later interpretation makes clear why that does **not** mean the conductor
hooks are broken:

- the tested presets mostly reinforced the route that arm C already preferred
- conductor bias only entered as a bounded addition to feature score
- so the test was not structurally adversarial enough

That is a failure of expressivity under those presets, not a proof of impossible
conducting.

#### Contrasting pass

The later contrasting-conductor sweep is the real result to keep.

Across seeds `7`, `11`, and `19`:

- seeds with distinct preset paths: `2/3`
- average distinct paths per seed: `1.67`
- the presets that moved structure were mainly `threshold_drive` and
  `exchange_fast`
- `spectacle_hold` and `refuge_hold` remained much closer to neutral

The interpretation note is exact and worth preserving:

- conductor influence is **real**
- it is **moderate but still limited**
- it is **asymmetric**

In other words, the scheduler can now be bent, but not yet broadly or elegantly
enough to count as a mature performance surface.

### 3.6 What the experiments prove, and what they do not

What is now supported:

- Façade-shaped traversal beats naive traversal on a pilot graph
- graph tuning and scheduler tuning are separable and both matter
- DODM-style features add real structural behavior on a richer graph
- conductor bias can alter traversal under some contrasting presets

What remains unproven:

- whether watched playback preserves the trace-level wins
- whether conductor influence is strong and legible enough in the real
  performance stack
- whether the scheduler generalizes beyond the current substrates without major
  graph or annotation work
- whether `L3` is already the right performance surface rather than just a good
  trace generator

This is exactly why the later notes say: stop tuning the scheduler in circles.
The next questions have moved either **upstream** into material generation or
**downstream** into watched playback.

---

## 4. What the source lineages contribute

The source program is unusually disciplined. The reading list is explicit that it
is not a prestige bibliography. Every source is there because it contributes a
specific mechanism, evaluation frame, or warning.

### 4.1 `L3` lineage: drama management, not cognition

The four-source `L3` lineage is now clean.

#### Façade

Façade contributes the scheduler **pipeline shape**:

```text
available nodes
  → priority tiers
  → score against target trajectory
  → soft weighting
  → select next node
```

It also contributes several practical points:

- per-node availability and effect annotations are mandatory if trajectory
  control is real
- priority tiers are a hard filter, not just another soft score
- cumulative arc drift is a useful diagnostic
- the scheduler is an editor over authored fragments, not a generator

This is the ancestry of the current Façade-shaped traversal arm.

#### DODM

DODM contributes the **feature registry** and the idea that authorial quality
should be a weighted sum of named features rather than one opaque formula.

For this project, that means:

- preparation / earnedness can be graph-readable
- mixing, homing, recall value, exhaustion, and manipulation can be named terms
- shallow lookahead is permissible, but long-horizon planning is not required
- candidate-set shaping through promote / suppress / release is a real part of
  scheduler design

That is the ancestry of arm C.

#### Authorial Leverage

This paper contributes the right **evaluation question**.

It says the scheduler should not be judged only on whether a run “felt good.”
It should also be judged on whether it buys real leverage over simpler
alternatives:

- equivalent policy complexity
- resilience when the graph changes
- controlled variety across runs
- quality plus variability, not one without the other

The selected notes repeatedly use this as the real rubric for whether `L3`
deserves to exist.

#### Suspenser

Suspenser contributes one particular feature family rather than an entire
scheduler architecture:

- tension can be understood structurally as narrowing or widening of perceived
  resolution paths
- event proximity and option-closing / option-opening material matter
- structural tension is different from recent trajectory shape

This is why later `L3` notes split “tension” into at least two kinds:
trajectory tension and structural tension.

### 4.2 `L1` lineage: narrow mixed initiative, not free-running invention

#### Sentient Sketchbook (and Tanagra by implication)

This cluster contributes the workflow model:

- continuous visible evaluation
- feasibility before quality
- suggestions that mutate the current artifact rather than generate from scratch
- cheap rejection
- novelty as a diversity term, not a free-standing pressure

The important effect on `L1` is restraint. The critic should be visible,
structural, local, and cheap to say no to.

#### MINSTREL

MINSTREL contributes the transform-recall-adapt logic for dead-end recovery:

- when a candidate is close but wrong, mutate it
- do not regenerate blindly
- keep mutation paired with a specific repair logic
- gate against boredom / repetition

This is why later `L1` notes want mutation and repair after fast rejection,
not an unconstrained “be more creative” engine.

#### Result for `L1`

The settled and reviewed position is that `L1` should stay:

- narrow
- typed
- mixed-initiative
- local in its edits
- judged by accepted-diff yield and edit burden

The broader typed world-model ambitions in the canonical architecture are still
there, but the source-faithful v1 is the narrower critic / repair surface.

### 4.3 `L2` lineage: Mueller at the center, with targeted imports

#### Mueller

Mueller is still the kernel.

The selected Mueller extractions contribute the most project-specific machinery
in the whole arc:

- the simple concern-centered control loop
- theme-rule concern initiation
- distinction between personal-goal and daydreaming-goal concerns
- contexts as backtracking + trace retention
- recursive reminding with index expansion and emotion reactivation
- serendipity as explicit connection search plus verification
- mutable episode valence after rationalization
- a designed narration layer with mode-sensitive language and pruning

This is also where the overdetermination thread enters: Mueller's later work
says a good product is multiply caused and serves several concerns at once.

#### EMA

EMA contributes the explicit **appraisal pass**:

- appraisal reads a structured causal interpretation, not raw world state
- appraisal is fast and automatic
- dynamics come from reappraisal as interpretation changes
- controllability and changeability bias coping, but do not hard-dispatch it

This is the main source for making `L2` less ad hoc about concern birth and
intensity.

#### OCC

OCC contributes the emotion semantics and intensity structure:

- concern type is not emotion type
- emotion intensity should be derived from appraisal variables
- prospective vs actual distinctions matter
- compound emotions like anger and remorse have structure

This is why later `L2` notes insist that durable runtime state should not become
just a bag of emotion labels.

#### Versu

Versu contributes social-practice structure:

- recurring social situations as typed practice objects
- role-based affordance gating
- multiple practices can run concurrently
- situations suggest; characters still decide

This is the strongest source for making operator choice socially situated.

#### ATMS

ATMS contributes a real upgrade path for hypothetical branch management:

- assumption tokens
- labels on facts
- nogoods
- cheap context switching

But the current docs are consistent that ATMS is **deferred**. It is not on the
present critical path.

### 4.4 Source-lineage corrections that matter

Two later review notes sharpened the synthesis without overturning it.

#### Overdetermination should be a criterion, not just provenance

One of the clearest corrections is that the architecture was sometimes treating
cross-level composition as metadata. The stronger source-backed claim is that
products should be evaluated for how many pressures they serve at once.

That has not yet become a fully frozen graph schema or complete playback
criterion, but it is now an explicit methodological thread.

#### Keep `L1` narrow

The broader world-engine picture of `L1` should not be mistaken for the actual
v1 source-backed workflow. The narrow graph critic remains the truer immediate
lineage.

#### Keep `L3` intents as outward vocabulary

The five intents are useful as seam language. They should not become the inner
mechanics of the scheduler.

#### Keep Mueller-specific memory machinery central in `L2`

A repeated warning is that EMA, OCC, and Versu must not bury the things that
make this line actually DAYDREAMER-like:

- theme rules
- recursive reminding
- emotion reactivation
- verified serendipity
- mutable remembered valence

#### Treat narration as designed realization, not polished debugging

Mueller Appendix B is not a quaint extra. It is a source-backed argument that
inner-life narration belongs close to rules, episodes, and cognitive mode. The
current architecture acknowledges this, but the implementation still remains to
be done.

### 4.5 What the reading program explicitly excludes

The reading list is also clear about what is *not* the center of gravity here:

- generic BDI
- story grammars / beat-sheet theory
- GraphRAG / generic KG tooling
- ontology-heavy narratology as immediate runtime design
- end-to-end LLM story generation

That exclusion list matters because it explains why the architecture looks the
way it does. It is deliberately trying to stay close to drama management,
Mueller-style cognition, mixed-initiative PCG, and explicit graph seams.

---

## 5. What the authoring-time generation reframe changed

This is the biggest sequencing change in the arc.

### 5.1 What changed

The architecture did **not** flip from three levels to some new worldview.
What changed was the diagnosis of the bottleneck.

After the Graffito and City Routes scheduler passes, the later notes conclude:

- the narrow `L3` thesis is now supported enough for current purposes
- conductor influence is real but limited
- the next blocker is not more scheduler theory
- the next blocker is **graph material supply**

That is the point of `27-authoring-time-generation-reframe.md` and the revised
`13-execution-roadmap.md`.

### 5.2 The new bottleneck: `0 → usable graph material`

The problem is now stated plainly:

- how do you get from narrative primitives to enough graph-ready material
- without hand-authoring every node
- while preserving the frozen graph seam

This reframes the upstream work. The first question is no longer “can a narrow
critic improve an existing graph?” It is “how do we generate enough good graph
material at all?”

### 5.3 `L1` becomes two-stage

The selected docs now split `L1` into two stages.

#### Stage 1: generation

Goal:

- produce candidate moments from narrative primitives

Mechanism:

- authoring-time use of `L2`-style machinery
- retrieval
- operator-driven generation
- provenance
- human curation
- compilation into graph-native fields

#### Stage 2: critic / repair

Goal:

- identify and repair structural defects once enough material exists

Mechanism:

- deficiency detection
- proposal generation
- visible evaluation
- mutation / repair of near-misses

So the `L1` critic is not thrown away. It is moved to **after** material exists.

### 5.4 The new upstream experiment

The current critical-path experiment is now the authoring-time generation
prototype.

Its constraints are deliberately narrow:

- one character
- three authored backstory episodes
- one active unresolved situation
- three operators
- simple tag-based retrieval first
- human-readable provenance
- output that compiles to the frozen graph seam
- flat-prompt baseline for comparison

The important input is not “seed nodes.” It is a smaller set of narrative
primitives:

- characters
- contradictions
- relationships
- backstory events
- places with emotional charge
- active situations
- world rules / emotional physics
- resonance notes

The engine is then supposed to generate candidate moments from collisions among:

- active concerns
- retrieved episodes
- operator choice
- place / situation context

### 5.5 Compile-to-graph discipline is the whole point

The prototype only counts as a success if accepted candidates land cleanly in the
existing graph membrane.

Each accepted candidate should compile into graph-native fields like:

- `situation_id`
- `delta_tension`
- `delta_energy`
- `pressure_tags[]`
- `origin_pressure_refs[]`
- `setup_refs[]`
- `payoff_refs[]`

That is why the reframe matters. It is not asking for pretty generated scenes in
the abstract. It is asking whether Mueller-shaped, retrieval-aware,
operator-driven generation produces **usable graph material** better than flat
prompting.

### 5.6 What counts as success or failure

Success means:

- operator-driven generation beats the flat baseline in material quality
- retrieval changes the generated material in an observable way
- accepted candidates compile cleanly to the graph contract
- curation yield is high enough to make graph growth practical

Failure means:

- operator labels do not add specificity or motivation
- retrieval adds ceremony but not useful difference
- output stays fluent but graph-useless
- curation burden remains close to hand-authoring

This is a strong and healthy reframing because it makes the next test about the
actual bottleneck rather than continuing to elaborate a scheduler that has
already answered its narrowest architectural question.

---

## 6. What the `L2` middle layer now is

The current `L2` work is not yet a finished refactor. But conceptually it is now
much sharper than it was earlier in the log.

### 6.1 The core claim

`L2` is the live character-pressure engine.

It is not:

- a drama manager
- a global planner
- a world-building system
- the conductor
- the graph itself

Its job is to maintain and update motivated inner processing at the character
level.

### 6.2 The main correction: `CausalSlice`

The largest conceptual correction from the later review is that the middle layer
was missing a real object between raw world state and appraisal.

That missing object is **`CausalSlice`**.

EMA does not appraise raw world state directly. It appraises a structured
interpretation of the situation:

- what goals are affected
- with what utility and probability
- whether the issue is actual or prospective
- who is responsible
- what interventions are available to self or others
- whether the relation frame is self, other, or shared

Without `CausalSlice`, “EMA-lite” would collapse back into hand-authored concern
updates with appraisal vocabulary sprinkled on top.

### 6.3 The authoritative object is `AppraisalFrame`

The second major correction is about what should count as source-of-truth runtime
state.

The frozen position now is:

- **`AppraisalFrame` is authoritative per-cycle evaluation**
- **`EmotionVector` is derived**

That means durable runtime state should be things like:

- `CharacterConcern`
- active social practices
- maybe branch / trace state

But not a persistent bag of named emotions drifting independently over time.

Emotions are still very important. They are just a **derived view** from current
appraisal, used for narration, UI, and interpretation.

### 6.4 The current schema

A good way to understand the middle layer is by object role and persistence:

| Object | Role | Persistence |
|---|---|---|
| `CharacterConcern` | durable motivational pressure | durable |
| `CausalSlice` | structured interpretation of the current situation | ephemeral per step |
| `AppraisalFrame` | authoritative appraisal of that slice | ephemeral / cacheable |
| `EmotionVector` | derived emotional view for narration and UI | derived |
| `SocialPracticeInstance` | persistent local social-situation state | semi-durable |
| `Affordance` | role- and practice-bound locally available moves | ephemeral / enumerated |
| `OperatorCandidate` | scored exploration option | ephemeral |
| `GeneratedNodeProvenance` | stable graph-safe residue of the generation process | graph-writeable |

This is the key flow:

```text
dominant concern
  → CausalSlice
  → AppraisalFrame
  → EmotionVector (derived)
  → activate practices
  → enumerate affordances
  → score operator candidates
  → generate proposal / node
  → validate / mutate if needed
  → choose commit
  → apply commit
  → reappraise affected concerns
```

### 6.5 How operator choice is supposed to work now

The current scoring picture is multi-factor, not a lookup table.

Operator candidates are supposed to be scored by a weighted combination of:

- pressure
- appraisal fit
- practice fit
- episodic resonance
- repetition / novelty penalties

That is important because it preserves the architecture's main constraint:

- appraisal biases operator choice
- it does not deterministically dispatch one family
- local social affordances still matter
- memory still matters

### 6.6 What is in scope for v1

The frozen v1 scope in `28-l2-schema-from-5pro.md` is modest.

Include:

- concerns like `attachment_threat`, `retaliation_pressure`, `obligation`
- EMA variables like desirability, likelihood, controllability, attribution,
  temporal status
- OCC emotions like hope, fear, distress, anger, remorse, relief,
  disappointment
- practices like confrontation, evasion, confession
- families like rehearsal, rationalization, avoidance

Do not include yet:

- full OCC runtime taxonomy
- object-based emotions
- full Soar-like causal plan models
- full Versu logic
- ATMS assumption management
- multi-character social-practice machinery
- conductor integration

### 6.7 What must remain central from Mueller

One of the most useful later warnings is that the middle-layer refactor must not
turn into “Mueller plus some appraisal jargon.”

The source-faithful `L2` still has to keep Mueller's specific machinery central:

- theme-rule concern initiation
- recursive reminding
- emotion reactivation on retrieval
- serendipity as explicit connection recognition plus verification
- mutable episode emotion after rationalization
- narration with mode-sensitive realization and pruning

In other words: EMA, OCC, and Versu sharpen `L2`; they do not replace its core.

### 6.8 What is still untested

This middle layer is conceptually much cleaner than it was a week earlier, but
it is still mostly a **frozen direction**, not an experimental result.

What is not yet proven:

- that the new middle objects actually improve generated behavior
- that the added schema produces cleaner or more persuasive traces
- that practice gating makes branches more legible in use
- that the dashboard realization becomes better rather than more ceremonial

The next move the schema note itself recommends is a worked trace. That is a
sign of where the confidence actually is: the abstractions are now specific
enough to test, but not yet tested.

---

## 7. What comes next

The current roadmap is the one in `13-execution-roadmap.md`, updated by the
authoring-time generation reframe.

### 7.1 The new order

The new sequence is:

1. **Authoring-time generation prototype**
2. **Conductor mapping spike**
3. **Watched-run / performance-lane validation** using existing `L3`
4. **`L1` critic / repair pass**
5. **`L2` internal refactor**

This is a change from the earlier order implied in the settled-architecture doc.
That earlier order put:

- `L3` first
- `L1` critic second
- `L2` refactor third

The later notes do not overthrow the architecture. They say the experimental
order has changed because the bottleneck changed.

### 7.2 Why this order changed

The reasons are explicit:

- `L3` has now answered enough of its narrow architectural question
- the current bottleneck is graph material supply
- conductor mapping still needs a physical playability test
- watched output still matters, but it no longer blocks the next upstream
  experiment
- deeper `L2` cleanup should follow proof, not lead it

### 7.3 The immediate next move

The roadmap is unusually specific about the immediate prototype.

Do this, in order:

1. define one character with contradictions
2. author three backstory episodes plus one active unresolved situation
3. choose three operators
4. implement simple tag-based retrieval
5. run flat baseline vs operator-driven generation
6. require graph-compilable output fields on every accepted candidate
7. compare curation yield and usable graph material

That is the actual next critical path from the selected docs.

### 7.4 What the later steps are for

#### Conductor mapping spike

This is no longer a broad theory question. It is a physical instrument question:

- can a small set of controls reliably produce intended biases?
- do multiple dimensions collapse together on the hardware?
- does the instrument feel playable rather than elegant?

#### Watched-run validation

This is the next shipping-lane falsifier:

- render representative City Routes arms
- inspect whether setup/payoff, threshold recovery, and conductor influence are
  visible in playback rather than only trace analysis

#### `L1` critic / repair

This becomes useful once material exists. Then it can be judged properly against
one-shot structured prompting on defect reduction, edit burden, and accepted
proposal yield.

#### `L2` internal refactor

This is later because it should follow proof. Its target is better provenance,
appraisal clarity, concern lifecycle, and reminder / serendipity visibility
without changing the stage-facing packet.

---

## 8. What remains open or risky

The selected docs are explicit that the architecture is settled while several of
the hardest problems remain open.

### 8.1 Cross-level pressure propagation is still not solved

This is named in the canonical architecture as the hardest unsolved problem, and
that remains true.

The current answer is intentionally conservative:

- use the graph as the membrane
- let `L1` / `L2` write stable residue such as pressure tags, origin refs,
  practice tags, setup/payoff links, branch-outcome tags
- let `L3` read those tags
- do **not** build a live cross-level pressure bus yet

That is a useful practical answer, but it is not the same thing as fully solving
cross-level composition.

The overdetermination thread sharpens this. The project increasingly treats
multi-pressure coverage as a real criterion, but it has **not** yet frozen a
full schema or evaluation discipline for that across the entire stack.

### 8.2 The conductor is not yet a mature performance surface

The defended claim after City Routes is:

- conductor influence exists
- it is moderate
- it is asymmetric

That is not a mature instrument.

Open questions remain about:

- how much leverage conductor bias really has against a strong feature layer
- whether the APC mapping is physically playable
- whether trace-level conductor effects are visible in watched playback

### 8.3 Trace wins still need watched-run confirmation

This is probably the single biggest downstream risk.

The scheduler experiments show that certain arms behave better by trace metrics
and by human interpretation of trace structure. But the project still needs to
know whether those differences survive the actual rendering stack.

If watched playback flattens the differences, then some of the trace-level wins
will have been technically real but artistically irrelevant.

### 8.4 Material supply is still only a diagnosis, not a solved mechanism

The reframe is strong because it names the bottleneck clearly. But it does not
yet solve it.

The authoring-time generation prototype is still ahead. So the following are
still open:

- whether operator-driven generation beats flat prompting
- whether retrieval materially changes generation quality
- whether generated moments compile cleanly to the graph seam
- whether curation yield becomes practical

### 8.5 `L2` now has a schema, but not yet a worked proof of value

The middle layer is probably the cleanest conceptual part of the whole stack
right now. But it remains largely a frozen design.

Open risks include:

- the new objects could improve clarity but not behavior
- the extra schema could become elegant paperwork rather than leverage
- EMA / OCC / Versu imports could overshadow Mueller-specific strengths if not
  implemented carefully

### 8.6 The graph annotation burden is real

Several notes raise a practical risk that the graph may require more structured
input than is convenient to author.

This is especially relevant in two places:

- `L3`, where preparation, event homing, option effect, and structural tension
  need usable node metadata
- `L1`, where strong deficiency classes only exist if the graph or world schema
  is rich enough to detect them

The project's current answer is to keep the seam minimal and test on narrow
fixtures first.

### 8.7 The narration layer is source-backed but still underspecified in code

The dashboard priority is settled, and the Mueller Appendix B lineage is strong.
But the actual implementation question remains open:

- how much narration belongs at rule level?
- how are pruning hints attached?
- how are concern shifts and reminding surfaced without raw dumpiness?

This is not a theoretical gap. It is an implementation gap.

### 8.8 ATMS remains deferred for good reason

ATMS is still regarded as the principled upgrade path for branch management, but
it remains deferred.

That is not indecision. It is a priority judgment:

- current branch machinery is crude but sufficient for near-term work
- appraisal and social-practice structure have higher leverage first
- the watched run and the generation prototype matter more than a more elegant
  hypothetical-branch data structure right now

### 8.9 The strongest alternative remains a useful pressure test

The graph-centric two-stage architecture remains the strongest alternative in
the notes, and that is useful. It keeps pressure on the current design to justify
its extra level boundaries.

The current answer is still:

- the alternative may be cleaner as a product story
- the current split is better for research and falsification right now

That tension is healthy. It does not currently change the plan.

---

## 9. Bottom line

The pressure-engine-reframe arc is now in a clearer state than the raw log makes
it seem.

What is settled:

- the two-lane / three-level architecture
- `L2` as the engine core
- `L3` as traversal scheduling rather than cognition
- `L1` as narrow research-stage authoring support
- the graph membrane and its field contract
- the one-control-plane story for conductor and stage
- the inner-life dashboard as the primary early output

What is experimentally supported:

- the scheduler shape on a pilot substrate
- the value of graph tuning on a richer substrate
- the value of a DODM-style feature layer on City Routes
- a limited, asymmetric form of conductor influence

What changed most recently:

- the project's bottleneck diagnosis
- the next experiment is now authoring-time generation from narrative
  primitives, not more abstract scheduler work and not the `L1` critic first

What is still open:

- watched-run validation
- mature conductor playability
- the full upstream material-supply problem
- cross-level pressure propagation / overdetermination as a completed mechanism
- actual implementation proof of the `L2` middle layer

So the right summary is not “the architecture is done” and not “everything is
still speculative.”

It is this:

**The architecture is now stable enough to work against. The `L3` line has
cleared enough bar to stop dominating the roadmap. The next real falsifier is
upstream: can authoring-time, Mueller-shaped generation produce graph-native
material better than flat prompting, while preserving the graph seam and the
character-pressure logic that the rest of the stack depends on?**
