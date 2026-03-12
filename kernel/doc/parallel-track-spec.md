# Parallel Track Spec: DAYDREAMER Resurrection

This note defines a bounded version of "path 3": build a source-grounded
DAYDREAMER exploration in Clojure, keep the Python engine as the production
instrument, and only feed recovered mechanisms back once they prove their value.

The point is not to do a nostalgic port. The point is to answer two precise
questions:

> Which parts of Mueller's original architecture are still missing from the
> conducted system, and which of those parts are worth recovering?
>
> What does Mueller's full architecture produce that the simplified Python
> engine cannot?

## Decision

The working decision is:

- Keep `scope-drd/tools/daydream_engine.py` as the production scheduler.
- Treat the Clojure DAYDREAMER work as an **offline research sidecar**.
- Optimize the sidecar for **source faithfulness** and **behavioral insight**,
  not stage integration.
- Do not attempt to replace the Python engine until the sidecar demonstrates a
  behavior we cannot get cleanly from the current system.

This is path 3, deliberately bounded.

It also clarifies the timeline split:

- **Path 2 matters for the hackathon timeline.**
- **Path 1 matters for the long personal project.**
- **Path 3 is how to start path 1 without blocking path 2.**

## Why This Track Exists

The current conducted architecture is intentionally not a Mueller port. It keeps
the short scheduler loop, goal competition, threshold retrieval, and mutation,
then inserts the Director as the visual imagination layer.

That was the right choice for Round 03. It got us to a working Dreamer seam
quickly.

But the current Python engine is still a **Mueller-derived scheduler**, not a
recovered DAYDREAMER. It does not yet model the parts of Mueller that are most
Lisp-shaped and most likely to change the system's feel:

- separate daydreaming vs reality context stores
- one-step planning over a branching context tree
- multi-step planning chains and subgoal decomposition
- explicit backtracking through sprouted alternatives
- pseudo-sprouted alternative past/future contexts
- emotional episodes as first-class causal objects
- substantive performance/daydreaming oscillation
- top-level goal termination semantics that write back into reality
- goal-family plan bodies that do more than pick a posture label

Those are exactly the parts a Clojure sidecar is good for.

This matters because the conducted system's Director can only interpret the
Dreamer block it receives. If the Dreamer can only say "goal_type=reversal,
obsessing about s1," the Director gets one kind of prompt. If the Dreamer can
say "reversal on s1 because a plan for s3 failed, which produced a
rationalization episode that pulled s1 back into focus," the Director gets a
qualitatively richer prompt.

## Why Path 3, Not Path 1 Or 2

### Not path 1 first: "faithful port as its own project"

A fully faithful port is too large as a first move. It risks months of work
before we learn whether the missing Mueller machinery actually produces
artistically useful differences.

### Not path 2 first: "replace the Python scheduler"

Replacing the current scheduler now would be premature. The Python engine is
already proving the Dreamer-to-stage contract, and the higher layers are still
under active creative investigation.

### Why path 3

Path 3 is the right shape because it is:

- **bounded**: no need to carry the live stage
- **source-grounded**: the original Lisp is already local in this repo
- **comparable**: it can be measured against the Python engine
- **non-blocking**: it does not stall Director or stage work
- **reversible**: if the recovered architecture is not useful, we keep the
  insight and discard the implementation

## What The Current Python Engine Already Covers

The point of the sidecar is not to redo what already exists.

### Already present in `daydream_engine.py`

- short repeated scheduler loop
- activation and emotion decay
- competition among the seven goal types
- threshold retrieval with serendipity lowering thresholds
- mutation as a structured stuckness escape
- active-index history and recency pressure
- a stable `DreamNode`/session-log boundary
- Director feedback indexes anchored to grounded Dreamer cues

### Still absent or only approximated

- real planning vs imaginary planning as separate context stores
- branching context trees with parent/ancestor lineage
- pseudo-sprouts for alternative past exploration
- one-step planning with explicit backtracking over sprouted contexts
- multi-step planning chains with subgoal generation and failure
- emotional episodes represented as structured objects instead of scalar mood
  fields
- a real performance/daydreaming oscillation; the Python engine has a `mode`
  flag, but not Mueller's full alternation between real-goal handling and
  daydream-goal handling
- termination semantics that update memory and reality differently depending on
  planning type
- narrow goal-family plan bodies recovered from Mueller's actual rules
- the older planning-layer distinction between "what to think about" and
  "how this branch of thought unfolds"

That absence is the exact scope of the parallel exploration.

## Source-Grounded Claims To Preserve

The sidecar should preserve the parts of Mueller that are concrete in code, not
just the parts that sound good in summary.

### 1. Scheduler and mode split

From `dd_cntrl.cl`:

- the top-level loop decays needs and emotions, selects the most highly
  motivated runnable goals, and advances exactly one planning step
- the system explicitly oscillates between `performance` and `daydreaming`
- the scheduler compares goal strengths, not raw emotion values
- the oscillation matters because the system returns from daydreaming to
  world-facing processing rather than remaining in one permanent dream mode

### 2. Context-based planning

From `dd_cntrl.cl`, `dd_rule1.cl`, `dd_rule2.cl`, and `gate_cx.cl`:

- planning advances in **contexts**
- rules sprout child contexts rather than mutating one global state
- contexts inherit parent state but keep their own local additions/removals
- backtracking means selecting a different surviving sprout, not merely scoring
  a different node
- top-level thinking is a planning chain, not only a scheduler decision

### 3. Pseudo-sprouted alternative pasts

From `gate_cx.cl` and `dd_reversal.cl`:

- an alternative past is not just a tagged node
- it is a copied context, attached to the current branch as a
  **pseudo-sprout**, with cleaned emotions and planning structure
- this is a distinctive mechanism and should be recovered explicitly

### 4. Episodic retrieval as threshold coincidence counting

From `dd_epis.cl`:

- episodes are stored under multiple indices
- retrieval increments marks per matched index
- retrieval occurs when marks reach threshold
- serendipity effectively lowers threshold by one
- recent episodes are excluded

This part is already present in the Python engine, but the sidecar should
recover it in a source-faithful form, not only as a behavioral approximation.

### 5. Emotional episodes as first-class objects

From `dd_epis.cl` and the top-level goal termination path:

- episodes are not only "emotion level at time t"
- they are structured traces of what failed or succeeded, what context it
  happened in, and what indices it should later reactivate under
- this is part of what can give the Director richer material than a flat
  `goal_type + situation + tension` packet

### 6. Mutation as explicit structure-changing search

From `dd_mutation.cl`:

- mutation is attempted when ordinary planning exhausts possibilities
- mutation is tried against leaf contexts under the backtrack wall
- mutation is checked through the serendipity mechanism
- mutation is not generic randomness

### 7. Goal families as plan skeletons

From `dd_kb.cl`:

- `ROVING` is not generic wandering; it is pleasant recall after aversive
  pressure
- `RATIONALIZATION` is not just excuse-making; it is typed failure
  reinterpretation with specific causal flips
- `REVERSAL` is not generic counterfactuality; it is alternative past/future
  exploration over copied contexts
- `RECOVERY`, `REHEARSAL`, and `REPERCUSSIONS` have concrete planning
  postures, not just mood labels
- `REVENGE` is real in the source, but its exact social scripts are heavily
  domain-bound and should be generalized carefully

## Non-Goals

This track should stay narrow. It should explicitly not attempt all of the
following in its first passes:

- live stage control
- direct Scope or Lyria integration
- replacing the Python engine in performance
- full GATE theorem proving
- full English generation
- porting every original rule file before behavior is tested
- recreating Mueller's entire social domain as-is
- mixing Director/brief/style-guide logic into the Clojure core

If it starts doing those things early, the track is losing discipline.

## Research Questions

The sidecar exists to answer these questions:

1. Does explicit context sprouting and backtracking produce traces that feel
   meaningfully different from weighted node traversal?
2. Does Mueller's reality/daydream split matter to the resulting dynamics, or
   is the current Python approximation enough?
3. Do planning chains and emotional episodes produce a richer Dreamer block for
   the Director than the current scheduler does?
4. Which goal families genuinely require plan bodies, and which can remain
   scheduler postures?
5. Does Clojure materially improve the representation of branching inherited
   state, or does it simply make the architecture feel more "correct"?
6. Which recovered mechanisms, if any, improve the conducted instrument when
   translated back into the Python engine?

The underlying hypothesis behind all six questions is this:

- if Mueller's architecture is **descriptive**, then the current Python engine
  may already approximate the important dynamics with simpler machinery
- if Mueller's architecture is **generative**, then the full
  context/planning/termination system may produce behaviors the approximation
  cannot reach cleanly

## Architectural Stance

The sidecar is not a second runtime. It is a second **engine core**.

### Production side

- Python
- `DreamNode` emission
- stage integration
- Director feedback loop
- palettes, prompts, and performance infrastructure

### Research side

- Clojure
- source-faithful control and planning kernel
- trace generation
- behavioral comparison harness
- optional later adapter into the Dreamer seam

## Proposed Clojure System

The sidecar should be implemented as a small offline system with explicit,
serializable data. EDN is the default representation.

One of the main reasons to do this in Clojure is that every cognitive cycle can
be represented as a pure transformation from one immutable snapshot to the
next. That makes replay, diffing, and "time-travel" inspection part of the
architecture rather than a debugging afterthought.

The spreading-activation side of the system fits this well: each activation
cycle can be modeled as a pure function over an immutable state map, yielding a
new snapshot rather than mutating a live network in place.

### Core modules

#### `context`

Persistent context graph with:

- `:id`
- `:parent`
- `:ancestors`
- `:children`
- `:pseudo-sprout?`
- `:timeout`
- `:mutations-tried?`
- `:touched-facts`
- `:all-obs`
- `:ordering`
- `:gen-switches`

This is the center of gravity of the resurrection.

#### `goals`

Goal objects with at least:

- `:id`
- `:goal-type`
- `:planning-type` (`:real` or `:imaginary`)
- `:status` (`:runable`, `:halted`, `:waiting`, etc.)
- `:strength`
- `:main-motiv`
- `:activation-context`
- `:termination-context`
- `:next-context`
- `:backtrack-wall`

#### `episodic-memory`

Episode store with:

- episode records
- emotional episode records with cause/resolution/context links
- index store
- plan and reminding thresholds
- mark/unmark pass
- recent episode exclusion
- recent index list

#### `control`

Top-level loop implementing:

- need decay
- emotion decay
- goal selection by strength
- performance/daydreaming toggle
- one-step planning
- top-level goal termination
- backtracking
- mutation invocation after exhaustion

#### `goal-families`

A small set of goal-specific rule bodies. These should be handwritten and
source-grounded, not inferred from broad summaries.

#### `trace`

Structured logs for comparison with the Python engine, including:

- cycle number
- full immutable snapshot id
- selected top-level goal
- current mode
- current context id and depth
- sprouted children
- current planning chain / active subgoals
- retrieval hits and thresholds
- backtrack events
- mutation attempts
- termination events

The trace layer also needs a derived-causality pass. It is not enough to dump
events. It must be able to walk context ancestry and reconstruction history in
order to answer questions like:

- which plan body failed?
- in which context did the failure terminate?
- which retrieved episode or reminding event reactivated the current concern?
- which causal chain should be surfaced to the Director seam?

## Data Shape

The sidecar should prefer plain data to opaque objects. The exact schema can
evolve, but the shape should stay close to this:

```clojure
{:engine
 {:mode :daydreaming
  :reality-context :cx/12
  :reality-lookahead :cx/19
  :top-level-goals [:goal/1 :goal/2]
  :recent-indices [:emotion/neg :goal/reversal]
  :recent-episodes [:ep/14]}

 :contexts
 {:cx/12 {:parent nil
          :ancestors []
          :children [:cx/19]
          :pseudo-sprout? false
          :timeout nil
          :mutations-tried? false
          :ordering 1.0}
  :cx/19 {:parent :cx/12
          :ancestors [:cx/12]
          :children []
          :pseudo-sprout? false
          :timeout 3
          :mutations-tried? false
          :ordering 0.8}}

 :goals
 {:goal/1 {:goal-type :rationalization
           :planning-type :imaginary
           :status :runable
           :strength 0.78
           :next-context :cx/19
           :backtrack-wall :cx/12}}

 :episodes
 {:ep/14 {:kind :emotional-episode
          :trigger-goal :goal/7
          :resolved-goal :goal/9
          :termination-context :cx/18
          :indices [:emotion/neg :goal/reversal :person/x]
          :summary {:failed-goal :goal/7
                    :reframed-as :goal/9}}}}
```

The point is not aesthetic purity. The point is inspectability.

## Implementation Order

Do not implement all seven goal families at once.

### Wave 1: kernel before repertoire

Implement first:

- context sprouting
- backtracking
- episode indexing and threshold retrieval
- top-level goal loop
- performance/daydreaming oscillation
- termination semantics

Without this, the project is just rebuilding the Python engine in Clojure.

The oscillation item is especially important. It is not bookkeeping. It may be
the missing mechanism that explains why a concern can return changed rather than
simply recur in another posture.

### Wave 2: the most revealing goal families

Start with the families most diagnostic of the missing architecture:

1. `REVERSAL`
2. `ROVING`
3. `RATIONALIZATION`
4. `RECOVERY`
5. `REHEARSAL`

Reason:

- `REVERSAL` tests the architecturally distinctive mechanism first:
  alternative-past and alternative-present exploration through copied
  pseudo-sprouted contexts
- `ROVING` then tests recall and relief drift
- `RATIONALIZATION` tests reinterpretation logic
- `RECOVERY` and `REHEARSAL` test realistic planning rather than pure fantasy

If `REVERSAL` does not justify the context machinery, the strongest argument for
the resurrection weakens immediately.

### Wave 3: the domain-sensitive families

Add later:

- `REVENGE`
- `REPERCUSSIONS`

These are useful, but more likely to demand domain adaptation rather than pure
architectural recovery.

## Comparison Harness

The sidecar needs a disciplined comparison path to the Python engine.

### Shared benchmark fixture

The primary comparison target should be the same Puppet Knows fixture already
used by the Python engine and the hand-composed sequence:

- `scope-drd/content/daydreams/puppet_knows/world.yaml`
- `scope-drd/content/daydreams/puppet_knows/dream_graph.json`
- `scope-drd/content/daydreams/puppet_knows/director_feedback.json`
- `daydreaming/Notes/experiential-design/07-dream-sequence-01.md`

The point is to run both systems against the same charged situations
(`s1_seeing_through` through `s4_the_ring`) and compare traces on the same
material, not on abstract toy worlds.

At minimum, the comparison harness should support:

- same starting world state
- same cycle count window
- same seed when stochasticity is involved
- side-by-side trace export for Python and Clojure runs
- optional comparison against the hand-composed 12-cycle sequence as a
  qualitative target

### Shared trace schema

The Clojure sidecar should not invent an unrelated report format. It should be
able to export a JSON adapter that targets the existing Python reporter in
`scope-drd/tools/daydream_trace_report.py`.

That means the comparison-friendly export should preserve the fields the current
reporter already expects at top level:

- `started_at`
- `seed`
- `world_path`
- `graph_path`
- `feedback_path`
- `palette_path`
- `git_commit`
- `engine_path`
- `engine_sha256`
- `world_sha256`
- `graph_sha256`
- `feedback_sha256`
- `palette_sha256`
- `graph_nodes`
- `graph_edges`
- `cycles`

And, within each cycle, at minimum:

- `cycle`
- `timestamp`
- `goal_selection`
- `selected_goal`
- `top_candidates`
- `active_indices`
- `retrieved`
- `chosen_node_id`
- `selection`
- `feedback_applied`
- `serendipity_bias`
- `situations`

The sidecar can keep richer internal EDN traces, but it should provide a stable
export adapter to this schema so that both engines can be compared with the
same reporting tooling.

### Comparison target

Do **not** compare on literal scene output first.

Compare on:

- switching behavior
- return/drift dynamics
- recurrence of charged material
- oscillation behavior: whether returns come back changed after a
  performance-facing interval rather than only through continuous dreaming
- planning-chain depth and failure structure
- quality of causal material exposed to the Director seam
- backtracking frequency
- mutation usage
- context depth and branch survival
- termination patterns

### Explicit benchmark case: cycle 9 -> 10

The first cross-engine benchmark should be the Puppet Knows transition from
cycle 9 to cycle 10 in `07-dream-sequence-01.md`:

- cycle 9: `s1_seeing_through × REVENGE`
- cycle 10: `s4_the_ring × REHEARSAL`

This is the strongest local test because it concentrates several important
claims in one transition:

- a charged return to `s1`
- a revenge posture directed at the apparatus rather than a person
- a Director-mediated ring setting that wakes `s4`
- an unexpected arrival at `s4_the_ring`
- a changed rehearsal posture in the "honest" place

If the sidecar cannot produce a meaningfully different causal account of this
transition than the Python engine, that is evidence against the strongest case
for the resurrection track.

### Shared metrics

Use the same measurement mindset as the conducted tuning notes:

- return time to a charged center
- persistence of an obsession before switching
- rate of productive drift vs incoherent wandering
- proportion of retrievals driven by grounded cues vs weaker associative cues
- frequency and depth of multi-step plan decomposition
- whether performance/daydreaming oscillation produces "return with residue"
  rather than simple alternation
- whether the sidecar can explain "why this obsession now" in structured terms

The sidecar does not need Director metrics yet, but it should produce logs that
can later be evaluated with the same discipline.

### Downstream evaluation

Trace richness alone is not enough.

The comparison harness should eventually include a Director-facing check:

- given the same brief and style guide, does the richer Dreamer block actually
  change the prompts or imagery in a useful way?
- do Director outputs improve when supplied with causal chains and episode
  structure, or are goal type, situation, and tension already sufficient?

This is the main guard against mistaking structurally richer traces for better
art.

### Minimal-before-full episode test

The Python engine should be allowed to attempt **minimal emotional episodes**
first: lightweight causal packets attached to failures, returns, and retrievals.

The Clojure sidecar's job is not merely to say "episodes should be richer." Its
job is to show whether the full Mueller-style machinery produces meaningfully
different behavior on the same failure case.

That comparison should be run on the same benchmark transition, especially the
Puppet Knows 9 -> 10 move. If minimal episodes in Python explain that move just
as well and drive equally useful Director outputs, the argument for porting
full machinery weakens.

## Interface With The Conducted System

At first, the interface is **analysis only**.

### Phase 1 interface

The Clojure system writes:

- EDN trace
- JSON trace
- per-cycle summaries
- retrieval and branch diagnostics

No stage contract yet.

### Phase 2 interface

If the sidecar starts producing useful results, add a thin projection layer that
emits a reduced Dreamer-state packet:

```json
{
  "mode": "daydreaming",
  "goal_type": "reversal",
  "active_indices": ["situation:...", "goal_type:reversal"],
  "retrieved_episodes": ["ep_14", "ep_27"],
  "active_plan_chain": ["goal_7", "goal_9"],
  "episode_cause": "plan failure on s3 reactivated s1 through rationalization",
  "trace_context_id": "cx_19"
}
```

This is enough to compare with the existing seam without forcing full stage
integration.

The `episode_cause` field must not be treated as a hand-written note. It should
be generated by a dedicated extraction pass over the trace/context tree:

1. identify the active top-level concern at the current cycle
2. walk backward to the relevant failure or success termination point
3. identify the plan body and subgoal chain involved
4. identify the retrieval or reminding event that pulled the next concern
   forward
5. compress that chain into a Director-facing causal summary

That extraction problem is one of the core deliverables of the sidecar.

### Phase 3 interface

Only after the above is useful should we consider an adapter that emits
`DreamNode`-compatible directives.

That adapter should be thin. The sidecar should not absorb stage concerns.

## Success Criteria

The project is successful if it produces at least one of these:

1. A source-faithful context/planning kernel that reveals behaviors the Python
   scheduler currently cannot express.
2. A clear negative result: the missing Mueller machinery is historically
   interesting but not worth carrying into the conducted system.
3. A small set of precise mechanisms that can be ported back to Python without
   replacing the production architecture.
4. A richer Dreamer-state packet that gives the Director narrative structure,
   not just posture labels and activation values.

The best outcome is not "everything moves to Clojure." The best outcome is
"we now know exactly which Mueller mechanisms are load-bearing."

## Kill Criteria

This track should be paused if any of the following become true:

- the work expands into a full GATE recreation before producing comparative
  traces
- the domain mismatch dominates the architectural questions
- the sidecar cannot show any behavior that is meaningfully different from the
  current Python engine
- the project starts competing with Director/stage work for near-term delivery

Path 3 is only healthy while it remains a sidecar.

## Risks

### Scope explosion

GATE and DAYDREAMER are bigger than the scheduler summary makes them look.

Mitigation:

- recover kernel mechanisms first
- add goal families in waves
- require trace output early

### Domain mismatch

Mueller's original rules are strongly interpersonal and socially specific.

Mitigation:

- preserve architectural mechanisms before preserving scenario content
- generalize domain-bound plan bodies only after they are understood

### False faithfulness

A port can feel Lisp-shaped while still losing the original control logic.

Mitigation:

- anchor every subsystem to local source files
- write invariants before extending creatively

### Licensing and distribution

The original DAYDREAMER code in this repo is GPL-licensed.

Mitigation:

- treat the sidecar as source-derived work
- keep licensing decisions explicit before external distribution

### Richer traces may not improve the art

The sidecar may produce beautiful causal traces and still fail to improve what
the Director does with them.

Mitigation:

- include Director-facing evaluation in the comparison harness
- test against the same brief and fixture material, not only trace metrics
- treat "better internal explanation" and "better stage outcome" as separate
  success tests

## Immediate Deliverables

The next concrete outputs for this track should be:

0. A Mueller source inventory mapping exact Lisp files and functions to sidecar
   modules, along with what each source fragment is meant to test.
1. A minimal Clojure kernel spec covering contexts, top-level goals, retrieval,
   and termination.
2. A shared trace schema and export adapter targeting
   `scope-drd/tools/daydream_trace_report.py`.
3. A concrete Puppet Knows benchmark definition using the shared
   `world.yaml` and `dream_graph.json` fixture files, with the cycle 9 -> 10
   transition called out explicitly.
4. A Python baseline pass for minimal emotional episodes on the same fixture.
5. A first implementation pass of the kernel with no stage integration.

## Recommendation

Proceed with the resurrection track only as a **research kernel**.

Do not ask it to be the live Dreamer yet.

The correct discipline is:

- recover the context machinery
- recover planning chains and emotional episode structure
- recover a few goal-family bodies
- compare traces
- extract what matters

That is the version of DAYDREAMER resurrection most likely to teach us
something real without derailing the conducted instrument.
