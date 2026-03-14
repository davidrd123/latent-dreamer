# Prior Work Synthesis Against Settled Architecture

This note updates the earlier architecture synthesis in
`first-round/03-06-07-arch-synthesis.md` after the canonical
settling in
`11-settled-architecture.md`.

The purpose is narrower than a literature review. It answers one
question:

**Given the settled two-lane architecture, what does the prior work
actually change now, what should be deferred, and what seams need to
be made explicit so the levels compose cleanly?**

---

## Top-line judgment

The settled architecture is broadly right.

The prior work does **not** collapse L1, L2, and L3 into one engine.
It sharpens the differences between them while confirming that they
can share a small control geometry:

- unresolved state
- prioritization
- retrieval / reminding
- explicit provenance
- explicit commit discipline

The strongest result of the prior work pass is:

**L2 should become more explicit and less ad-hoc. L3 should become
more legibly drama-manager-like. L1 should stay narrow, typed, and
mixed-initiative.**

That means:

- `EMA` and `OCC` mainly upgrade `L2`
- `Versu` mainly upgrades `L2` and clarifies one graph seam
- `Façade`, `DODM`, and `authorial leverage` mainly upgrade `L3`
- `Tanagra` and `Sentient Sketchbook` mainly constrain `L1`
- `ATMS` is useful, but not on the immediate critical path

The critical path remains what `11-settled-architecture.md` already
says it is:

**Experiment 1: minimal L3 scheduler over the Graffito graph, judged by
the watched run.**

---

## What prior work changes at each level

## L1: Authoring critic / expander

The settled doc is already correctly framed here: this is not an
autonomous story builder.

What the prior work adds:

- `Tanagra` confirms that mixed-initiative systems work best when
  they are local, fail-loud, and constraint-backed.
- `Sentient Sketchbook` supports proposal surfaces and human
  curation rather than hidden one-shot generation.
- `MINSTREL` is a useful reminder that memory-based recall/adapt can
  help when the system is stuck, but it does not justify broad
  autonomous authoring claims.

Practical consequence:

- L1 outputs should stay typed world-state diffs.
- Pressure types should remain limited to computable structural
  deficiencies.
- Human curation remains mandatory.
- The system should be judged by accepted-diff yield and edit burden,
  not by prose cleverness.

What should not change:

- Do not broaden L1 into “world dreaming.”
- Do not schedule soft aesthetic deficiencies as if they were hard
  structural pressures.
- Do not let L1 become the default authoring path before it beats
  hand-curated work in A/B tests.

---

## L2: Character exploration engine

This is where the prior work is most actionable.

The settled architecture already identifies the three missing pieces
correctly:

- appraisal pass
- social practice objects
- ATMS-style assumption management

The prior work clarifies their order.

### 1. Appraisal should come before deeper branch machinery

`EMA` and `OCC` jointly argue for a cleaner event-to-concern path.

What to steal:

- an explicit appraisal pass from event observation to appraised
  consequence
- appraised dimensions that update concern intensity in a principled
  way
- a cleaner concern vocabulary grounded in OCC-style consequence /
  action / object appraisal

Why this matters first:

- concern birth and intensity are currently acknowledged as ad-hoc
- better branch bookkeeping is less valuable if concern formation is
  still muddy

Minimal implication:

- add an appraisal stage that maps observed events into
  consequence/action/object appraisals
- update `CharacterConcern` intensity, urgency, valence, and
  unresolved status from those appraisals
- keep the output in the existing concern framework rather than
  importing EMA wholesale as a second ontology

### 2. Social practices should become explicit objects

`Versu` is the most useful non-Mueller source for the character level.

What to steal:

- recurring interaction templates as first-class social-practice
  objects
- role-based coordination without forcing a centralized chooser
- affordance surfaces that constrain what kinds of moves are legible
  in a situation

Good candidate practice objects for this project:

- confrontation
- evasion
- confession
- rehearsal
- status repair
- caretaking
- seduction / invitation
- avoidance ritual

Why this matters:

- many benchmark adapters already imply these structures
- making them explicit should improve both trace readability and
  authoring vocabulary

What not to steal directly:

- Versu’s full world ontology
- its strong commitment to a logic-first world representation
- its DM model as a direct template for L3

What matters here is the interaction-template layer, not the entire
simulator architecture.

### 3. ATMS is real but deferred

`ATMS` remains the right conceptual direction for hypothetical branch
management:

- assumption tokens
- support sets
- nogood tracking
- cheaper switching among branch states

But it is not the next thing to build.

Reason:

- the canonical architecture puts Experiment 1 first
- the next L2 improvements with highest leverage are appraisal and
  social practices
- current kernel branch handling is crude but usable enough for the
  near-term research track

Decision:

**Treat ATMS as a representation upgrade for later L2 refactor work,
not as an immediate architecture priority.**

---

## L3: Traversal scheduler

The settled architecture is also correctly framed here:

**L3 is drama management over authored material, not cognition.**

The prior work mainly sharpens what kind of drama management it should
be.

### Façade

What to steal:

- authored content plus scheduler annotations, not generated story
  ontology
- explicit distinction between local interaction machinery and global
  beat sequencing
- scoring against an intended value trajectory
- “bag of beats” logic for recombining authored material with
  partial-order constraints

What this supports in the settled doc:

- `TraversalDiagnostic`
- `TraversalIntent`
- shallow lookahead without persistent context trees
- target-trajectory reasoning over tension / energy / valence

What not to steal:

- discourse-act machinery as an L3 concept
- beat-goal ontology outside places where the graph already needs it

That is local performance architecture, not traversal vocabulary.

### DODM

What to steal:

- treating drama management as policy over authored plot material
- evaluating schedulers with feature-toolbox style diagnostics
- making authorial criteria explicit rather than intuitive

Most relevant DODM-style lessons for this project:

- sequence quality needs critic signals, not just weighted edges
- scheduler evaluation should include avoidance, homing, mixing, and
  manipulability-like concerns
- if a more complex scheduler does not visibly improve output, it has
  not earned its cost

This strongly supports the settled choice of:

- a small set of L3 diagnostics
- a small set of L3 intents
- experiment-first falsification

### Authorial leverage

This paper is the cleanest justification for L3 existing at all.

What to steal:

- ask whether the scheduler buys usable authorial power, not whether
  it is intellectually elegant
- compare against simpler trigger/tree baselines
- measure not just quality of single runs but controllability and
  variability across runs

Practical implication:

- the Graffito scheduler should be judged against a simple traversal
  baseline
- if humans cannot tell the difference in watched output, the
  scheduler should be demoted back to a heuristic

### Suspenser

What to steal:

- event approach and event proximity as explicit runtime notions
- audience-facing tension can depend on how a run moves toward or away
  from committable events

This supports the settled architecture’s optional fields:

- `event_commit_state`
- `event_proximity`

What not to do:

- do not turn L3 into a full suspense model
- use this as a heuristic lens for event approach, not as a new core
  ontology

### Farbood

Important, but not for the current scheduler experiment.

What it is good for:

- coupling musical tension to traversal shape once the traversal is
  already legible

What it should not do right now:

- drive scheduler design before the watched run proves the traversal
  layer is real

---

## Shared infrastructure implications

## Episodic retrieval stays shared

This remains one of the strongest reusable ideas across levels.

The settled doc is right to keep:

- coincidence-mark retrieval
- reminding cascade
- shared retrieval infrastructure

The prior work adds one practical caution:

- retrieval should eventually filter not only by index overlap but
  also by relational pattern, valence, role similarity, and operator
  compatibility

That is an improvement path, not a reason to redesign the shared
infrastructure.

## Provenance stays mandatory

The manual-review pass reinforced this.

Each useful prior-work system becomes easier to reason about when the
system can say:

- what drove this move
- what alternatives were considered
- what got committed
- what remained hypothetical

This is especially important because the inner-life dashboard is a
primary output, not a debug afterthought.

## Event sourcing remains a steal

This remains a good shared representation pattern for:

- canon mutation history
- session history
- replay
- trace export

This is compatible with the settled architecture and does not force
cross-level ontology collapse.

---

## The graph is the real cross-level interface

The settled architecture names cross-level pressure propagation as the
hardest open problem. The prior work suggests a minimal answer:

**Do not build a live cross-level pressure bus first. Enrich the graph
as the composition surface.**

That means L1/L2 write graph-facing structure that L3 can later read.

Minimum useful node metadata to add or preserve:

- `situation_id`
- `participants`
- `place_id`
- `stakes`
- `pressure_tags`
- `origin_pressure_refs[]`
- `setup_refs[]`
- `payoff_refs[]`
- `event_commit_potential`
- `release_potential`
- `contrast_tags`

This is enough to support several important things:

- L2-originated material can leave a legible residue in the graph
- L3 can score toward avoided or unresolved material without sharing
  L2 ontology directly
- recall and payoff closure become graph-readable instead of
  hand-waved

Strong recommendation:

- keep the graph as the explicit membrane between lanes
- prefer graph annotations over runtime ontology sharing
- only add richer cross-level machinery if graph annotations prove
  insufficient

---

## What should change now

These are the changes that prior work actually justifies now.

### 1. Start the canonical synthesis and implementation memo

This note is part of that.

The goal is not “what did the papers say?”

The goal is:

- what enters the settled design now
- what stays deferred
- what gets translated into graph metadata
- what becomes an experiment criterion

### 2. Keep Experiment 1 first

Prior work strengthens the case for `L3`; it does not move `L2` or
`L1` ahead of the watched run.

The next serious falsifier is still:

- minimal traversal scheduler
- over Graffito graph
- with five intents and explicit diagnostics
- judged against a simpler traversal baseline

### 3. Plan two concrete L2 upgrades behind it

Once Experiment 1 is running or once there is a clean slot for L2
work, the two highest-value upgrades are:

1. appraisal pass
2. explicit social-practice objects

`ATMS` comes after those unless branch-management pain becomes acute
enough to force reprioritization.

### 4. Keep the stage seam thin

Prior work does not justify exporting richer internals to the stage.

The settled seam remains correct:

- `traversal_intent`
- optional target / event-commit state
- no diagnostic dumps
- no context trees
- no pseudo-concerns leaking outward

---

## What should stay deferred

These are real, but they are not current critical-path work.

- `ATMS` implementation
- `Farbood`-driven music coupling
- broad `L1` structural-pressure authoring loop
- rich cross-level pressure propagation beyond graph annotation
- multimodal world-building as core architecture rather than sidecar
- full conductor-theory refinement before validating the APC mapping

---

## What to avoid

- Treating `ATMS` as required before the next useful experiment
- Letting prior-work enthusiasm broaden `L1` beyond typed mixed
  initiative
- Smuggling `L2` ontology into `L3`
- Smuggling `L3` internals into the stage seam
- Replacing graph annotations with a vague shared “pressure” story
- Building infra for hypothetical composition before the watched run
  says the traversal layer matters

---

## Immediate recommendations

1. Run with the settled architecture as written.
2. Treat this note as the prior-work implementation filter.
3. Keep `ATMS` explicitly deferred in the reading queue and in
   implementation priority.
4. When the synthesis note branches into implementation tasks, split
   them into:
   - `L3 now`
   - `graph annotation seam`
   - `L2 next`
   - `deferred research`

If the project stays disciplined on that split, the prior-work pass
adds clarity instead of architecture drift.
