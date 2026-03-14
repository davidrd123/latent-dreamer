# Pressure Engine: State Model

## Design Principles

1. **Pressure is the primitive, not emotion.** Pressures are the
   *reason* exploration happens. Emotions are downstream signals.
   Each level has its own pressure type (see below).

2. **Every exploratory fork names its assumptions.** No implicit
   "what if." The assumption_patch on an L1/L2 Context is mandatory.
   L3 traversal does not use exploratory Contexts.

3. **Commit types are lane-specific.** L1/L2 use `ontic | policy |
   salience | none`. L3 uses `canon_event | traversal_bias |
   activation_shift | none`.

4. **Exploratory operators are pluggable; traversal verbs are
   separate.** The system doesn't hardcode Mueller's families, but it
   also should not pretend L3 controller verbs are the same interface
   as L1/L2 exploratory operators.

5. **Provenance is mandatory.** Every exploratory node records who was
   exploring, what pressure drove it, what operator generated it, and
   what assumptions changed. Every traversal decision records why this
   node was selected now.

6. **Related types, not one undifferentiated abstraction.** The three
   levels share structural fields (intensity, urgency, valence,
   unresolved, decay) but have distinct pressure types, detection
   logic, and evaluation criteria. Forcing them into one polymorphic
   type risks creating a mushy abstraction that sounds elegant but
   confuses the code.

---

## Core Entities

### Pressure Types (Related but Distinct)

The three levels share a common shape but have distinct types,
detection logic, and evaluation criteria. This section defines
the shared invariant fields and the level-specific types.

#### Shared invariant fields

Every pressure at every level has:

```
PressureBase {
  id
  owner_id           // who has this pressure
  source_ids[]       // what created it
  intensity          // 0.0–1.0, how much this demands attention
  urgency            // 0.0–1.0, how soon it needs resolution
  valence            // signed: positive (opportunity) or negative (deficit)
  unresolved         // bool
  decay_rate         // how fast it fades without attention
  supporting_ids[]   // other pressures this could co-resolve (overdetermination)
}
```

These fields are sufficient for pressure scheduling (prioritize,
decay, detect co-resolution). Everything else is level-specific.

#### Level 1: ArtifactDeficiency (world-building)

**Status:** Research hypothesis. These types are provisional. See
`06-world-building-pressure-questions-for-5pro.md` for the focused
question pack on whether these are computable.

```
ArtifactDeficiency extends PressureBase {
  target_ids[]       // which world elements are affected
  deficiency_type    // see table below
  detection_method   // how this is computed from structured world state
}
```

| Deficiency type | What it means | Detection requires |
|----------------|--------------|-------------------|
| `desire_without_obstacle` | Character wants something, nothing blocks it | Character.desires[], world.obstacles[] |
| `disconnected_elements` | Two elements exist with no relationship | Relationship graph connectivity |
| `empty_location` | Location has atmosphere but no dramatic role | Location.events[], Location.tensions[] |
| `setup_without_payoff` | Tension or setup exists with no consequence | Event.consequences[], tension.resolution_path |
| `tonal_monotony` | All material has similar emotional register | Aggregate valence/intensity distribution |
| `backstory_vacuum` | Character or location has no history | Character.backstory, Location.history |
| `conflict_without_response` | Conflict exists but no character can respond | Conflict.response_paths[] |

**Open question:** Are these computable from structured state, or
do they require LLM judgment to detect? If the latter, the pressure
loop collapses into "LLM judges its own output."

#### Level 2: CharacterConcern (character inner life)

**Status:** Proven. The kernel implements this domain. Mueller's
original types are well-grounded in cognitive science.

```
CharacterConcern extends PressureBase {
  owner_agent_id     // which character
  target_agent_id?   // directed at whom
  concern_type       // see table below
  blockers[]         // other concerns preventing resolution
}
```

| Concern type | What it means | Mueller equivalent |
|-------------|--------------|-------------------|
| `status_damage` | Agent's standing has been reduced | Preservation failure |
| `attachment_threat` | Important relationship at risk | Preservation failure |
| `retaliation_pressure` | Harm received, response demanded | Revenge goal |
| `anticipation` | Future encounter generates anxiety | Rehearsal goal |
| `guilt` | Agent caused harm, unresolved | Recovery goal |
| `desire` | Agent wants something not yet obtained | Achievement goal |
| `curiosity` | Unexplained situation demands attention | — |
| `unresolved_injury` | Past harm not addressed | Reversal goal |
| `obligation` | Promise or debt outstanding | Achievement goal |

#### Level 3: TraversalPressure (director)

**Status:** Open but narrowed. These types are dramaturgical
intuitions, not grounded in a studied theory the way Mueller's
families are grounded in cognitive science. The current claim is not
that L3 is the same exploratory engine as L1/L2. It is a related
controller problem over authored material.

```
TraversalPressure extends PressureBase {
  scope              // :local (current moment) | :arc (trajectory)
  traversal_history  // what's been shown, avoided, earned
  pressure_type      // see table below
}
```

| Pressure type | What it means | Detection requires |
|--------------|--------------|-------------------|
| `tension_deficit` | Sequence has been too comfortable | Running tension trajectory |
| `pacing_stall` | Lingered too long in one situation | Visit duration per situation |
| `emotional_debt` | Climax hasn't been earned | Arc analysis (setup/payoff tracking) |
| `avoidance` | Important material being sidestepped | Node affinity vs. visit history |
| `resonance_opportunity` | Two situations could collide now | Active situation co-analysis |
| `unearned_climax` | High intensity without buildup | Intensity trajectory slope |
| `exhausted_situation` | Situation has been fully explored | Visit count + novelty per situation |

### ExplorationContext (L1/L2 only)

An exploration branch. Only the exploratory lane uses Contexts.

```
Context {
  id
  level              // :world-building | :character
  owner_id           // who is exploring
  modality           // :canonical | :hypothetical | :memory | :forecast |
                     //   :rehearsal | :fantasy | :counterfactual
  parent_id?         // forked from what context (nil for root/canonical)
  base_state_id      // snapshot of world or character state at fork point
  focus_id           // what triggered this fork (event, node, discovery)
  dominant_pressure_id // the primary pressure driving this exploration
  supporting_pressure_ids[] // other pressures being served (overdetermination)
  operator           // which operator generated this context
  assumption_patch   // MANDATORY: what assumptions differ from parent
                     //   e.g., "what if B had replied instantly?"
  branch_depth       // how deep in the exploration tree
  status             // :active | :parked | :exhausted | :committed | :rejected
  timeout            // depth budget remaining
}
```

**Key invariant:** A Context with no `assumption_patch` is sludge.
Every fork must name what it changed.

**L3 note:** The traversal lane does not use exploration contexts. It
keeps traversal history, active pressures, and bias state instead.

### Proposal (L1/L2 only)

The output of an operator exploring inside a Context. Replaces
the ChatGPT conversation's "SceneNode" — we use "Proposal" because
at levels 1 and 2 it's not a scene yet.

```
Proposal {
  id
  context_id         // which context generated this
  level              // :world-building | :character
  operator           // which operator produced it

  // Structured content (NOT a text blob)
  event_semantics    // what happens, typed by level
                     //   L1: world-building discovery (new character trait,
                     //       relationship, conflict)
                     //   L2: character action/reaction/internal shift
  preconditions[]    // what must be true for this to work
  predicted_consequences[] // what follows from this
  emotional_effects[]     // how this changes pressure landscape

  // Retrieval
  retrieved_episode_ids[] // prior episodes that informed this

  // Scoring
  score_components   // intensity_relief, novelty, concern_coverage,
                     //   dramatic_value, serendipity_bonus

  // Render surface (separate from semantics)
  render_surface     // L1: world description fragment
                     //   L2: character behavior/dialogue sketch

  // Provenance (mandatory)
  source_pressure_id // what pressure drove this
  owner_id           // who is exploring
  assumption_patch   // inherited from context
}
```

**Key invariant:** `event_semantics` and `render_surface` are always
separate. You can reason about what happened without reading the
rendered output. You can render without knowing the full causal chain.

### TraversalDecision (L3 only)

L3 does not generate Proposals. It makes traversal decisions over
existing authored material.

```
TraversalDecision {
  id
  selected_node_id
  traversal_intent   // e.g., :build | :release | :shift | :recall
  transition         // how we moved there
  dwell              // how long to stay
  source_pressure_ids[] // which TraversalPressures drove this choice
  alternatives_considered[] // optional debug/ranking output
  reason             // human-readable provenance
}
```

### CommitRecord

Formalizes how exploration results feed back into state.

```
CommitRecord {
  level
  source_id          // proposal_id (L1/L2) | traversal_decision_id (L3)
  commit_type        // lane-specific enum
  delta              // what changed (typed by commit_type)
  reason             // human-readable provenance
}
```

**L1/L2 commit types:**

- **ontic** — world facts change. A character actually does something.
  An event is now canonical. Rare. Requires explicit gate (human
  curation at authoring time, or explicit "realize" at performance
  time).

- **policy** — agent readiness/priors change. A character is now *more
  likely* to do something, without having done it. Common output of
  rehearsal, rationalization, fantasy.

- **salience** — attention shifts. Something that was background is now
  foregrounded. An index that was dormant is now active. A situation
  that was cold is now warm. Most common exploratory commit type.

- **none** — the exploration was evaluated and rejected. Still recorded
  for provenance. "We considered this and decided no."

**L3 commit types:**

- **canon_event** — a traversal event becomes part of the realized run.
- **traversal_bias** — the controller is now more or less likely to
  favor a certain kind of move.
- **activation_shift** — situation or node salience changes without a
  canonical event firing.
- **none** — the option was considered and rejected.

**Key invariant:** Most exploratory branches produce `policy` or
`salience`, not `ontic`. Most traversal steps produce
`activation_shift` or `traversal_bias`, not new hypothetical state.

### Episode

Stored outcome of exploration, available for future retrieval.
Largely unchanged from the current kernel's episodic memory model.

```
Episode {
  id
  level              // which level produced this
  operator           // which operator
  pressure_id        // what pressure was being explored
  context_id         // resolution context
  proposal_id?       // if committed, which proposal
  desirability       // how well it resolved the concern
  indices[]          // retrieval keys (situation IDs, emotional tags,
                     //   character IDs, operator types, etc.)
  plan_threshold     // marks needed for retrieval
  reminding_threshold // marks needed for reminding cascade
}
```

Coincidence-mark retrieval is kept from Mueller. Multi-index
intersection is kept. Reminding cascade is kept. These are good
mechanisms.

**Addition from ChatGPT conversation:** Retrieval should also filter
on role similarity, valence similarity, relational pattern, operator
compatibility — not just index intersection. "Give me prior episodes
where status damage plus attraction threat led to useful recovery"
is a better query than "nearest episodes in index space."

---

## Operator and Controller Shapes

### Exploratory Operator Interface (L1/L2)

Each exploratory operator is a typed move inside a hypothetical state
space.

```
Operator {
  id                 // e.g., :complicate, :rehearse, :rationalize
  level              // :world-building | :character

  // Activation predicate
  activates?(pressure, world_state) -> bool

  // Priority scoring
  priority(pressure, world_state) -> float

  // Execution: fork context, generate proposals
  execute(pressure, context, world_state, episodes) -> [Proposal]

  // Post-hoc: check if proposals accidentally serve other pressures
  scan_serendipity(proposals, active_pressures) -> [updated Proposals]
}
```

### Level 1 Operators (World-Building)

| Operator | Activates when | Produces |
|----------|---------------|----------|
| `complicate` | A character desire has no obstacle | New constraint or obstacle |
| `ground` | An abstract tension has no concrete situation | Situated version of the tension |
| `connect` | Two elements exist without relationship | A relationship that creates pressure |
| `contrast` | Tonal uniformity detected | An element that breaks the pattern |
| `historicize` | An element has no backstory | A backstory that creates latent pressure |

### Level 2 Operators (Character)

| Operator | Activates when | Produces |
|----------|---------------|----------|
| `revenge/reversal` | Status damage + anger | Counterfactual or retaliatory action |
| `rehearse` | Anticipated encounter + anxiety | Practice script for next meeting |
| `rationalize` | Failure + self-blame | Reframing that reduces negative pressure |
| `recover` | Injury + desire for repair | Non-retaliatory repair path |
| `avoid` | High-stakes confrontation + fear | Deferral (creates mounting pressure) |
| `rove` | Sustained negative pressure + exhaustion | Relief through unrelated pleasant material |
| `confront` | Mounting avoidance pressure | Forced engagement with avoided thing |

### Traversal Controller Verbs (L3)

These are not exploratory operators. They are controller verbs over
authored material.

```
TraversalVerb {
  id                 // e.g., :build, :release, :shift
  activates?(pressure, traversal_state) -> bool
  score(options, traversal_state) -> ranked_options
  apply(selection, traversal_state) -> TraversalDecision
}
```

| Verb | Activates when | Produces |
|------|---------------|----------|
| `build` | Tension deficit or unearned climax | Selection that increases pressure |
| `release` | Sustained high intensity | Relief or decompression |
| `confront` | Avoidance pattern detected | Sequence toward avoided material |
| `shift` | Exhausted situation | Move to different situation |
| `recall` | Earlier material now has renewed relevance | Return to earlier node |
| `juxtapose` | Resonance opportunity between situations | Collision between adjacent nodes |
| `dwell` | Moment with unextracted weight | Extended stay on current node |

---

## State Shape (per level)

### Level 1 World State
```
{
  setting: {...}
  characters: {char_id: {desires, fears, backstory, relationships}}
  situations: {sit_id: {characters, tensions, location, status}}
  discoveries: [...]       // things the engine has found/proposed
  active_deficiencies: [...] // ArtifactDeficiency pressures
  episodes: [...]
  contexts: {...}
}
```

### Level 2 Character State (per character)
```
{
  character_id
  concerns: [...]          // CharacterConcern pressures
  readiness: {action: probability}  // policy deltas from rehearsal/fantasy
  expectations: {agent: expectation} // what they expect from others
  salience: {index: weight}         // what they're attending to
  episodes: [...]          // personal episodic memory
  contexts: {...}          // their exploration tree
}
```

### Level 3 Director State
```
{
  graph: {...}             // the dream graph (authored/generated)
  traversal_history: [...] // what's been shown
  active_pressures: [...]  // TraversalPressure instances
  situation_activation: {sit_id: weight}
  traversal_biases: {intent: weight}
  emotional_trajectory: [...] // running arc
  episodes: [...]          // directorial memory (what worked before)
}
```

---

## Open Design Questions

1. **How do pressures propagate between levels?** A character-level
   concern (B wants revenge) becomes director-level material (a
   revenge sequence could resolve the tension deficit). What's the
   interface? Is it event-based, query-based, or something else?

2. **How does the human curator interact with each level?** Full
   proposal review at L1/L2? Trajectory review at L3? What's the
   UX for steering the engine vs. letting it run?

3. **How do the two lanes interface without collapsing together?**
   When does L3 simply traverse authored material, and when can it
   request or expose character-level material from L2?

4. **Pressure lifecycle:** When is a pressure born (event appraisal)?
   When does it resolve? When does it intensify? What's the decay
   model? Does it differ by level?

5. **Minimum viable surface for Graffito v0:** We don't need all of
   this on day one. Which 3-4 L2 operators matter for traces, and
   which 3-4 L3 controller verbs are worth testing offline first?

6. **Is the split into three pressure types correct?** Should
   ArtifactDeficiency, CharacterConcern, and TraversalPressure share
   more structure? Less? Or should L3 be modeled as evaluation signals
   and biases rather than full pressures?

7. **Can L1 pressures actually be computed from structured state?**
   This is the central question for the world-building research
   direction. See `06-world-building-pressure-questions-for-5pro.md`
   for the full question pack.
