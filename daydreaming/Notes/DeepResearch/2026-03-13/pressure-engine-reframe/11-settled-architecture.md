# Settled Architecture

This document supersedes the working drafts in docs 01-10 and
incorporates the results of four GPT-5 Pro review rounds (03, 06,
07, 08) plus the synthesis (`first-round/03-06-07-arch-synthesis.md`).

It also reconnects to earlier brainstorm work from the first
Daydream hackathon (`PreviousIdeation/video-conductor/`), which
anticipated much of this architecture before the kernel existed.

The working drafts and 5 Pro responses remain as a read-only
research log. This document is the current canonical reference.

---

## The Architecture in One Sentence

**An authored-graph performance stack with a real L2 exploration
engine at the character level, a drama-management traversal
scheduler at L3, and a research-stage authoring assistant at L1 —
all sharing control geometry but not ontology.**

---

## Two Lanes

```
RESEARCH / AUTHORING LANE
=========================

Source Bible / World Sketch
        |
        v
L1 Authoring Critic/Expander             [research, narrow, curated]
(computable structural deficiencies
 over typed world state)
        |
        v
L2 Character Exploration Engine           [proven, real engine core]
(concern-driven hypothetical exploration,
 Mueller-derived control geometry)
        |
        v
Candidate scenes / events / variants
        |
        v
Human curation + authoring
        |
        v
Authored Dream Graph


SHIPPING / PERFORMANCE LANE
============================

Authored Dream Graph
   + traversal state
   + visit history
   + conductor bias
   + Director feedback
        |
        v
L3 Traversal Scheduler                   [drama management, not cognition]
(diagnostics + intents over
 authored material)
        |
        v
Adapter / Projection Layer
(graph + engine state → normalized packet)
        |
        v
Normalized Cycle Packet / DreamDirective
        |
        +--> Inner-Life Dashboard [PRIMARY for early runs]
        |      (narration companion + cognitive visualizer
        |       + system state — both a product and a
        |       feedback/evaluation tool)
        |
        +--> Renderer (visual prompts via Scope)
        +--> Music (parametric Lyria modulation)
        +--> Stage (Scope deterministic actuator)


SHARED INFRASTRUCTURE
=====================

- Episodic retrieval (coincidence-mark, reminding cascade)
- Pressure/signal scheduling (priority, decay, recency)
- Trace / export / provenance
- Explicit commit discipline
- World-state services
```

### What each lane owns

**Research/authoring lane:** L1 discovers structural gaps in the
world. L2 generates motivated character events. Human curates. The
graph is the crystallized output of authoring plus curation.

**Shipping/performance lane:** L3 schedules traversal over the
authored graph. The adapter projects engine state into the
normalized cycle packet. The inner-life dashboard, renderer, music,
and stage consume the packet. The conductor (human performer with
APC Mini) biases the scheduler.

**The inner-life dashboard is the primary output for early runs.**
The narration companion (inner monologue grounded in cognitive
state), the cognitive trace visualizer (situation landscape, goal
race, branch events, thought stream), and the system state display
are not debug tools — they are compelling to look at, useful for
feedback and evaluation, and may be one of the actual products of
the system. Even before rendered video is good enough to carry the
experience alone, watching the workings of motivated characters
with concerns, rehearsals, avoidance, and emotional dynamics is
interesting on its own. Video is a secondary channel until it
earns primacy through the watched run.

### Where the graph sits

Between the lanes. It is the artifact of authoring — hand-authored
for the shipping path (Graffito v0), potentially engine-assisted
on the research path later.

The graph is a human-authored, human-curated creative asset.
Engine-assisted authoring is a research direction that must prove
itself in A/B testing before displacing hand-authoring.

Three surfaces, not one:
- authored graph (human-curated)
- `L1` annotation/proposal sidecar (machine-proposed, human-gated)
- runtime playback packet (downstream of the adapter)

The minimum cross-lane field contract for that graph/interface seam is
frozen in `21-graph-interface-contract.md`.

---

## Level 2: Character Exploration Engine (Proven)

### What it is

Mueller-derived hypothetical exploration. Characters have concerns,
appraise events, fork hypothetical contexts, explore alternatives
(rehearsal, reversal, rationalization, roving), and commit results
as policy/salience/ontic updates. The kernel implements this:
5,800 lines of Clojure, 85 tests, three benchmarks.

### Pressure type: CharacterConcern

```
CharacterConcern {
  id
  owner_agent_id     // which character
  target_agent_id?   // directed at whom
  concern_type       // status_damage, attachment_threat,
                     //   retaliation_pressure, anticipation,
                     //   guilt, desire, curiosity,
                     //   unresolved_injury, obligation
  intensity          // 0.0–1.0
  urgency            // 0.0–1.0
  valence            // signed
  unresolved         // bool
  decay_rate
  blockers[]
}
```

### Mechanisms (all implemented or partially implemented)

- Context sprouting, pseudo-sprouting, fact visibility
- Goal competition (strength-based selection, decay)
- Mode oscillation (performance/daydreaming)
- Backtracking over context trees
- Episodic memory (coincidence-mark retrieval, reminding cascade)
- Goal families as operators (REVERSAL, ROVING, RATIONALIZATION)
- Emotional diversion
- Director feedback loop (Gemini-based, external perturbation)

### Commit types

`ontic | policy | salience | none`

### What to add (from prior work review)

- **Appraisal pass** (EMA, Marsella & Gratch): event → appraised
  consequences → concern intensity updates. Currently concern birth
  and intensity are somewhat ad-hoc in the kernel.
- **Social practice objects** (Versu, Evans & Short): recurring
  interaction templates like confrontation, evasion, confession,
  rehearsal, status repair. Currently these are implicit in
  benchmark adapters.
- **ATMS-style assumption management** (de Kleer): explicit
  assumption tokens, nogood tracking, support sets for hypothetical
  branches. Currently the kernel uses a simpler add-obs/remove-obs
  model.

### Key papers

- Mueller, DAYDREAMER (1990) — the source architecture
- Marsella & Gratch, EMA — appraisal dynamics
- Ortony, Clore, Collins / OCC — concern type taxonomy
- Evans & Short, Versu — social practices
- McCoy et al., Comme il Faut / Prom Week — reusable social rules

---

## Level 3: Traversal Scheduler (Drama Management)

### What it is

A traversal scheduler over authored graph material. Not a cognitive
engine. Not context-forking cognition. Selects, orders, and dwells
over existing nodes using diagnostics and intents.

**L3 is not the same engine as L1/L2.** It shares control signals
(priority, recency, memory) but not ontology (contexts, forks,
proposals). "What if we show this next?" is not the same kind of
fork as "what if this character retaliated?"

The best prior work grounding is **drama management** (Facade,
DODM), not story grammars or directing theory.

### State: TraversalState

```
TraversalState {
  current_node_id
  recent_path[]
  visit_count[node_id]
  last_seen_cycle[node_id]
  situation_activation[situation_id]
  recent_emotional_trajectory[]   // tension, energy, valence
  event_approach_count[event_id]
  current_hold_state
  conductor_bias                  // from APC Mini / performer
}
```

### Diagnostics: TraversalDiagnostic

Computed from traversal state. These are **critic signals**, not
concerns. They score sequence quality, they don't represent
unresolved psychological pressure.

- `overexposed_situation` — too many visits to one cluster
- `recall_due` — earlier material now has renewed relevance
- `tension_flatline` — trajectory has been flat too long
- `tension_spike` — unearned intensity jump
- `exhausted_node` — node visited too recently/often
- `event_proximity` — approaching a committable event
- `avoided_material` — high-affinity node being dodged

### Intents: TraversalIntent

Five controller verbs, not seven operators:

| Intent | What it does |
|--------|-------------|
| `dwell` | Stay here longer |
| `shift` | Move to another situation/cluster |
| `recall` | Return to earlier material |
| `escalate` | Move toward higher pressure / event proximity |
| `release` | Move toward lower pressure / relief |

Parameters on intents (not additional operators):
- `target` — which situation/node/cluster
- `contrast_mode` — whether shift is lateral or collision-seeking
- `avoidance_target` — whether escalate aims at avoided material

This means:
- `build` = `escalate`
- `confront` = targeted `escalate` with avoidance_target
- `juxtapose` = targeted `shift` with contrast_mode, or an
  authoring-time pattern, not a runtime primitive

### Output: TraversalDecision

```
TraversalDecision {
  selected_node_id
  traversal_intent
  transition           // how we moved there
  dwell                // how long to stay
  source_diagnostics[] // which diagnostics drove this
  reason               // human-readable provenance
}
```

### Commit types (L3-specific)

- `canon_event` — a traversal event becomes part of the realized run
- `traversal_bias` — the scheduler adjusts its own future tendencies
- `activation_shift` — situation/node salience changes
- `none` — option considered and rejected

Ordinary node selection is NOT a commit. It's a control decision.

### Ephemeral lookahead (not persistent contexts)

L3 may need shallow path simulation (2-3 step beam search, score
alternative continuations). But this is lookahead, not L2-style
context branching.

```
LookaheadPath {
  node_ids[]
  projected_trajectory
  score_breakdown
}
```

Do not call this `Context`. Do not attach `assumption_patch`.

### Stage seam

Keep the normalized cycle packet as-is. Add one new semantic field:

- `traversal_intent`: dwell | shift | recall | escalate | release

Optionally add:
- `target_situation_id`
- `event_commit_state`: none | approaching | eligible | committed

Do NOT export full diagnostic sets, pseudo-concerns, context trees,
or beam-search internals into the stage seam.

Keep `family` for backward compatibility. Do not overload it with
L3 semantics. `traversal_intent` is the L3 runtime vocabulary.

### Key papers

- Mateas & Stern, Facade — beat sequencing, drama management
- Nelson et al., DODM — declarative optimization-based drama mgmt
- Chen, Nelson, Mateas — authorial leverage evaluation
- Cheong & Young, Suspenser — audience-facing tension heuristic
- Farbood — musical tension model (for music layer coupling)

---

## Level 1: Authoring Critic/Expander (Research)

### What it is

A research-stage authoring assistant that detects and repairs a
narrow set of structural deficiencies in a typed world model. Not
an autonomous world-building engine. Not the same kind of engine
as L2.

The best prior work grounding is **mixed-initiative PCG** (Sentient
Sketchbook, Tanagra), not creative AI or narrative generation.

### Pressure type: ArtifactDeficiency

Only computable structural defects are schedulable. If a pressure
can't be detected from typed fields in the world state, it's not a
real pressure — it's prompt theater.

**Strong (v1 schedulable):**

| Deficiency | What it means | Detects from |
|-----------|--------------|-------------|
| `goal_without_resistance` | Desire with no blocker | goals, stakes, blockers |
| `conflict_without_response` | Threat with no response path | participants, capabilities |
| `setup_without_payoff` | Setup with no consequence | setup refs, payoff refs |
| `place_without_role` | Location with no dramatic function | affordances, linked events |
| `charged_object_without_function` | Important object with no effect | object-event links |
| `disconnected_subgraph` | Cluster weakly connected to rest | graph connectivity |

**Do not schedule:** `tonal_monotony`, `backstory_vacuum` — linter
warnings at most.

### Operators

| Operator | Responds to | Produces |
|----------|-----------|---------|
| `obstruct` | goal_without_resistance | blocker, cost, opponent |
| `route` | conflict_without_response | response path, ally, refuge |
| `concretize` | place_without_role | situation/event grounded in place |
| `couple` | disconnected_subgraph | relation via shared event/object |
| `consequence` | setup_without_payoff | payoff, fallout, escalation |

`historicize` is NOT a top-level operator. It produces decorative
lore. Use it only as a tactic inside `couple` or `concretize`.

### Evaluation discipline

**A proposal must be a typed world-state diff, not a prose fragment.**

If a proposal only adds "interesting descriptive detail" and does
not change typed state, score it zero.

Evaluation criteria:
- Target-pressure reduction (rerun detector before/after)
- Cross-pressure gain (serendipity: did it fix another defect?)
- Leverage (how many existing entities does the diff touch?)
- Response coverage delta
- Setup/payoff closure
- Connectivity delta
- Economy penalty (new entities with no links = bloat)

Three gates: (1) validity, (2) structural utility, (3) human
keepability. Pure structural optimization gives you tidy dead worlds.

### Human workflow

Engine proposes diffs, human curates world canon.

| Action | System response |
|--------|----------------|
| accept | apply diff, rerun detectors, reprioritize |
| reject | store diff + reason code, penalize retries |
| edit | human modifies diff, validator reruns |
| merge | combine compatible diffs |
| reweight | change what the loop cares about |
| freeze | remove entities from mutation space |

### Minimum world state schema

| Entity | Required fields |
|--------|----------------|
| Character | id, goals, stakes, fears, capabilities, relationship_ids |
| Place | id, affordances, access_constraints, tone_vector, linked_situations |
| Relationship | a, b, type, valence, tension |
| Situation | id, participants, place_id, stakes, blockers, response_paths, setup_ids, payoff_ids |
| Event/Affordance | id, preconditions, consequences, affected_entities |
| ChargedObject/Motif | id, carriers, linked_situations, function_tags |

### Visual world-building (future)

World-building may become multimodal. Visual anchors (character
portraits, location references, mood variants) serve simultaneously
as design reference, LoRA training data, prompting vocabulary, and
consistency anchors. The DMPOST toolchain (Gemini Vision MCP, After
Effects MCP) is available as an external sidecar for this.

`visual_ungrounded` (entity has text but no approved visual anchor)
is a trivially computable deficiency that fits the strong-pressure
tier.

See `10-dmpost-multimodal-worldbuilding-sidecar.md` for details.

### Key papers

- Liapis et al., Sentient Sketchbook — mixed-initiative proposals
- Smith et al., Tanagra — constraint-backed generation
- Turner, MINSTREL — transform-recall-adapt for dead-end memory

---

## The Conductor

### Relationship to L3

The conductor sits **above L3 as a biasing authority**, not as a
parallel scheduler fighting it.

**Default:** Engine chooses, conductor biases.

One control plane:
1. Conductor updates bias state
2. Scheduler scores candidates under that bias
3. Scheduler emits one decision
4. Stage executes

### Three modes

- **Autonomous:** engine only
- **Assisted:** conductor biases + commit confirm/veto
- **Forced:** conductor issues one-step intent, engine resolves
  specifics within that constraint

### What the conductor controls

- Situation boosts (faders → activation weights)
- Hold / release bias
- Escalation bias
- Recall bias
- Intensity envelope
- Event commit confirm / veto

### What the conductor does NOT control

- Direct node-by-node playlisting
- Separate stage commands bypassing the scheduler
- Low-level scene semantics

### The Conductor State Vector

From the earlier brainstorm work (`PreviousIdeation/video-conductor/
instrument-stack-architecture.md`), the conductor's authority is
expressed as a low-dimensional continuous state:

```
conductor_state = {
  energy:   float   // 0.0–1.0, overall intensity
  tension:  float   // 0.0–1.0, unresolved pressure
  density:  float   // 0.0–1.0, how much is happening
  clarity:  float   // 0.0–1.0, focus vs diffusion
  novelty:  float   // 0.0–1.0, surprise level
  tempo:    float   // events per unit time
  phase:    float   // 0.0–1.0, position in arc
  hold:     bool    // suspension active
  focus:    vector  // attention locus
}
```

These are computed from input signals across multiple timebases:

- **Fast loop** (ms): immediate gesture / fader position
- **Medium loop** (seconds): integrated energy, smoothed state
- **Slow loop** (minutes): tidal oscillators, arc position
- **Session loop**: what's remembered, what becomes motif

### Tidal oscillators

From the earlier brainstorm work: slow-timescale arcs that create
inevitability independent of moment-to-moment decisions.

```
Tide {
  name
  period_seconds     // e.g., 90s for tension
  amplitude
  phase
  coupling           // how conductor state nudges the tide
}
```

Tides advance naturally. The conductor can nudge them (energy
pushes the tension tide forward, stillness slows it). The L3
scheduler reads the tidal state as part of its diagnostics.

This gives the performance "breathing" — a sense of larger forces
even when the conductor is responding locally.

### Key prior work

- George Lewis, Voyager — autonomous co-performer, autonomy
  negotiation
- TidalCycles / Strudel — pattern-as-control, small live edits
  with large structural consequences
- IMS literature — layer separation, latency budgets, body-as-
  conductor thinking
- Soundpainting / Conduction (Butch Morris) — gestural conducting
  grammars

---

## Shared Infrastructure

### Episodic retrieval

Coincidence-mark threshold retrieval + reminding cascade. Shared
across all levels. The kernel's implementation is sound.

Addition from prior work review: retrieval should also filter on
role similarity, valence, relational pattern, and operator
compatibility — not just index intersection.

### Provenance

Every decision at every level records: who explored/selected, what
pressure/diagnostic drove it, what operator/intent was used, what
assumptions changed (L1/L2) or what alternatives were considered
(L3).

### Commit discipline

Hypothetical material does not silently mutate canon. Explicit gates
at every level:
- L1: human curation gate for world edits
- L2: ontic/policy/salience distinction
- L3: ordinary selection is NOT a commit; only event realization
  is a commit

### Representation patterns to steal

- **Event sourcing** for canon/session mutation history (append-only
  log, replay, snapshots, provenance)
- **ATMS** for L2 hypothetical branches (assumption tokens, nogoods,
  support sets, cheap context switching)
- **Blackboard architecture** for critic/selector/conductor
  arbitration if that arbitration grows

---

## Glossary

| Term | What it is |
|------|-----------|
| **ArtifactDeficiency** | Computable authored-world gap (L1) |
| **CharacterConcern** | Unresolved character-level motivational pressure (L2) |
| **TraversalDiagnostic** | Critic signal over sequence quality (L3) |
| **TraversalIntent** | Sequencing posture: dwell, shift, recall, escalate, release (L3) |
| **TraversalDecision** | L3 output: selected node + intent + transition + provenance |
| **Operator** | Exploratory state-space move (L1/L2 only) |
| **TraversalVerb** | Controller verb over authored material (L3 only) |
| **Adapter** | Translation layer between engine semantics and stage contracts |
| **Renderer** | Cycle packet → model-specific prompts and parameters |
| **SelectionRecord** | Why the scheduler chose this step (L3) |
| **CommitRecord** | Explicit state promotion — not ordinary traversal (L2, + L3 event commits) |
| **Proposal** | Structured output of L1/L2 exploratory operator |
| **AuthoringCandidate** | L1 world-edit proposal for human curation |
| **DreamDirective** | Serialized output for stage: engine block (provenance) + stage block (rendering) |
| **ConductorState** | Low-dimensional continuous authority vector from performer |
| **Tide** | Slow-timescale oscillator creating arc-level inevitability |

---

## Three Experiments (Ordered)

### 1. L3 scheduler test (shipping lane, first)

Use the Graffito v0 authored graph. Build a minimal traversal
scheduler with stored state (visit counts, recency, situation
activation, tension trajectory) and five intents (dwell, shift,
recall, escalate, release).

**Baseline:** Current graph-weighted traversal or trace-player.
**Measure:** Revisit gap distribution, overexposure rate, purposeless
loops, event approach before commit, human judgment of arc legibility.
**Win:** Humans can reliably tell it feels more intentional than
baseline. Returns happen with legible timing. Event approach feels
earned.
**Fail:** Behavior reproducible by slightly better edge weighting.
Humans don't prefer it. Then L3 is just a better heuristic, not a
new engine.

### 2. L1 authoring critic test (research lane, second)

Seed a typed world fixture (3 characters, 3 locations, 3 tensions,
4 events) with 4 intentional structural defects. Run 4 pressures
with 4 operators. Compare against one-shot structured prompting
(same model, same budget, same reviewer).

**Measure:** Hard defect count before/after, accepted proposals per
review minute, edit burden, number of proposals that yield usable
graph material.
**Win:** 50% reduction in seeded defects + more accepted proposals
per minute than baseline + at least 3 accepted proposals needing
only light edits.
**Fail:** Doesn't beat one-shot. Generic filler. High rewriting
cost. Structural scores improve but nobody wants the material.

### 3. L2 internal refactor test (research lane, third)

Take one existing benchmark. Represent the decision process as
CharacterConcern + operator + Context.assumption_patch + CommitRecord.
Export the same playback packet as before.

**Measure:** Trace provenance clarity, explanation quality, same or
better behavior with no stage instability.
**Win:** Cleaner traces, easier to explain why a branch happened.
**Fail:** Lots of new abstraction, no better behavior, no better
inspectability.

---

## What's Settled vs. Open

### Settled

- Two-lane architecture (research/authoring + shipping/performance)
- L2 is the real engine core (Mueller-derived, proven)
- L3 is a traversal scheduler, not a cognitive engine
- L1 is a research-stage authoring assistant, not autonomous
- Operators and adapters are separate categories
- The graph is a human-authored asset (shipping path)
- CommitRecord taxonomy is level-specific
- Stage seam stays thin (DreamDirective with traversal_intent)
- Conductor is a biasing authority, not a parallel chooser
- One control plane (conductor biases → scheduler scores → one
  decision → stage executes)
- Inner-life dashboard (narration + cognitive visualizer + system
  state) is the primary output for early runs, not a debug tool
- Video is secondary until it earns primacy through the watched run

### Open

**Falsification questions (experiments answer these):**

- Whether L3 pressure-shaped scheduling beats simpler traversal
  (experiment 1)
- Whether L1 deficiency-driven authoring beats structured prompting
  (experiment 2)
- Whether L2 refactor improves traces without breaking behavior
  (experiment 3)

**Hard architectural gaps (need theory, not just experiments):**

- **Cross-level pressure propagation.** This is the hardest
  unsolved problem and has been deferred too casually. When a
  character concern (B wants revenge) becomes authored material
  (a revenge scene) that becomes traversal material (a high-tension
  node), that chain IS the system working. If the levels can't
  talk, you have three interesting subsystems that don't compose.
  The graph is the obvious interface (L1/L2 write, L3 reads), but
  the question is whether anything richer than "put it in the graph
  and let L3 find it" is needed. At minimum: how do character-level
  concerns tag the graph nodes they produce, so L3 can score against
  them?

- **Conductor state vector vs. APC Mini.** Nine dimensions, eight
  faders. The mapping hasn't been validated. Some dimensions
  (energy, tension, hold) have natural physical expressions. Others
  (clarity, novelty) may not. Tidal oscillators add further
  complexity. Risk: theoretically elegant but physically unplayable.
  **Prototype the conductor mapping before building the scheduler**
  — the feel of the instrument constrains what intents are reachable
  in practice.

- **L1 pressures may require more structured input than exists.**
  The "strong" pressures (goal_without_resistance, setup_without_
  payoff, disconnected_subgraph) are computable IF the typed world
  state is rich enough. But populating that schema for even one
  world is significant authoring work. The risk is that L1's
  "computable" pressures require so much structured input that
  curation cost exceeds just writing the material. Start L1
  experiments with status-flag-level pressures (visual_ungrounded)
  rather than structural-analysis-level ones (setup_without_payoff).

**Practical questions:**

- How tidal oscillators interact with L3 diagnostics
- Whether multimodal world-building (visual anchors) is worth
  pursuing now or later
- What the prior work reading (Facade, EMA, Versu, etc.) changes
  about specific implementations
- The right evaluation framework (authorial leverage + expressive
  range + watched-output judgment)
- Whether the Graffito graph is rich enough to make Experiment 1
  legible

**Overall risk:** The architecture is ready but large. The
temptation will be to build infrastructure before running
experiments. Everything in this document that doesn't directly
help Experiment 1 (L3 scheduler over Graffito graph) is research-
track work that should stay in notes until that experiment returns
results. The watched run is still the next real falsifier.

---

## Document Lineage

This document supersedes:

- `01-reframe-summary.md` (multiple revisions)
- `02-state-model.md` (multiple revisions)
- `04-kernel-gap-analysis.md` (multiple revisions)

It incorporates results from:

- `first-round/03-questions.md` (5 Pro architectural review)
- `first-round/06-world-building-pressure.md` (5 Pro L1 review)
- `first-round/07-L3-traversal-questions.md` (5 Pro L3 review)
- `first-round/08-prior-work-questions.md` (5 Pro prior work scan)
- `first-round/03-06-07-arch-synthesis.md` (5 Pro synthesis)
- `PreviousIdeation/video-conductor/` (conductor brainstorm docs)
- `10-dmpost-multimodal-worldbuilding-sidecar.md` (DMPOST toolchain)

Supporting docs (unchanged, still valid):

- `05-stage-integration.md` (DreamDirective, scope-drd surfaces)
- `reading-list/00-reading-list.md` (prioritized prior work)
- `09-follow-on-requests-for-5pro.md` (pending request #3)
