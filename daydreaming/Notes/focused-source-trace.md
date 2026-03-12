# Focused Mueller Source Trace

This note exists to pin the adaptation to the actual DAYDREAMER source, not to
chat summaries about the source.

It is intentionally narrower than the longer architecture notes. The goal is to
answer three questions:

1. What did Mueller concretely implement?
2. Which parts should Round 03 preserve on purpose?
3. Which parts are too domain-bound to port literally?

Use this alongside:

- `daydreaming/Notes/mueller-adaptation-working-spec.md`
- `daydreaming/Notes/daydream-to-stage-contract.md`
- `daydreaming/Notes/language-decision-memo.md`

Primary source files:

- `daydreamer/dd_cntrl.cl`
- `daydreamer/dd_kb.cl`
- `daydreamer/dd_epis.cl`
- `daydreamer/dd_ri.cl`
- `daydreamer/dd_mutation.cl`
- `daydreamer/gate_cx.cl`

## 1. What Web Opus Mostly Got Right

The broad map from the March 12 chat is directionally correct:

- The top-level scheduler really is a short loop, not a huge conductor system.
- The seven daydream goal families really are the core control mechanism.
- Episodic retrieval really is threshold coincidence counting, not embedding
  similarity.
- Mutation really is structural, not "raise temperature."
- Contexts really are planning branches, not a finished scene graph.

The useful correction is not "the chat was wrong." The correction is that the
chat was still too coarse in a few places, especially around exact triggers,
serendipity, and how narrow some of the concrete plan bodies are.

## 2. Scheduler And State Machine

### 2.1 System modes are simple and explicit

`dd_cntrl.cl` uses three states:

- `suspended`
- `performance`
- `daydreaming`

That matters because Mueller is not running one giant blended mode. The system
switches between real-goal handling and imaginary-goal handling.

The toggle is bidirectional. When daydreaming runs out of imaginary work, the
system switches back toward `performance`; when performance work dries up, it
switches toward `daydreaming`. This is an oscillating scheduler, not a one-way
"enter dream mode" flag.

### 2.2 The main loop is a repeated one-step scheduler

`daydreamer-control0` is the core loop:

- decay needs
- decay emotions
- collect the most highly motivated runnable top-level goals
- if there are none, flip between `performance` and `daydreaming`
- otherwise pick one candidate and run exactly one planning step

Important nuance: scheduling is driven by goal `strength`. The code does not
require a separate conductor vector to decide what to think about next. If
multiple goals tie for strongest, the system breaks the tie randomly.

More precisely: emotions matter because they feed top-level goals through
dependency links and motivators, but the scheduler itself compares goal
strengths, not raw emotions.

### 2.3 One iteration advances one context step

`daydreamer-control1` does not build a whole fantasy in one call. It:

- gets the next context
- runs rules in that context
- checks success / failure
- prunes possibilities
- backtracks if needed
- sets the next context to visit

That "one step at a time" structure is part of the feel. Stream-of-consciousness
comes from repeated interleaving, not from generating a whole scene tree at
once.

### 2.4 Daydreaming and reality use different context pointers

`get-next-context` and `set-next-context` distinguish:

- real planning, which runs against `*reality-lookahead*` / `*reality*`
- daydream planning, which stores `next-context` on the top-level daydream goal

This is the concrete mechanism behind the state split. Mueller is not just
labeling scenes as real or imagined; he is actually routing planning through
different context stores.

### 2.5 Goal termination updates memory and reality

`terminate-top-level-goal` does several things at once:

- removes motivators
- stores an episode
- if the goal was not imaginary, asserts outcomes into reality
- sprouts a fresh reality context

This is one of the clearest places where "daydreaming" and "world change" are
kept distinct in code.

## 3. The Seven Goal Families, Traced More Precisely

The biggest correction to the earlier chat is that the goal families are not
just mood labels. They are specific activation patterns plus plan skeletons.

### 3.0 Type hierarchy matters

The hierarchy in `dd_kb.cl` is part of the design:

- `DD-GOAL-OBJ`
- `FANCIFUL-GOAL-OBJ`
- `REALISTIC-GOAL-OBJ`
- `SKIPINDEX`

The concrete goal types are then arranged as:

- fanciful: `RATIONALIZATION`, `REVENGE`, `ROVING`
- realistic: `RECOVERY`, `REHEARSAL`, `REVERSAL`, `REPERCUSSIONS`
- `SKIPINDEX`: `REHEARSAL`, `REVERSAL`, `REPERCUSSIONS`

This is not just ontology polish.

- The fanciful/realistic split is Mueller's distinction between pure fantasy and
  imaginings that could feed real action.
- `SKIPINDEX` means some daydream goals do not index their own top-level episode
  directly. They lean on the indexed subgoal/episode structure instead.

### 3.1 ROVING

Trigger:

- a `FAILED-GOAL`
- a dependent negative emotion
- `greater-rat-activate?`, which checks emotion strength `> 0.7`
- supplemental negative emotion strength `0.04`

Plan shape:

- `Roving-Plan1` calls `roving-plan1`
- that plan recalls a pleasant episode

Interpretation:

- ROVING is not generic wandering.
- In source, it is a relief move after failure: strong negative feeling pushes
  the system toward pleasant recall.

Porting implication:

- Preserve the "escape from aversive pressure by pleasant drift" logic.
- Do not preserve the exact episode machinery literally.

### 3.2 RATIONALIZATION

Activation trigger:

- a `FAILED-GOAL`
- a dependent negative emotion
- `greater-rat-activate?`, again emotion strength `> 0.7`
- supplemental negative emotion strength `0.06`

Completion / activation-of-body condition:

- once negative emotion drops below `0.3`, or a positive emotion becomes linked
  to that failed goal, `Rationalization-Inf1` allows the rationalization itself
  to take hold

Plan variants:

- `Rationalization-Plan1`: imagine success leading to failure
- `Rationalization-Plan2`: imagine failure leading to success
- `Rationalization-Plan3`: minimization for inferred top-level failures

Interpretation:

- Rationalization is broader than "make excuses."
- It is a failure reinterpretation mechanism: flip the causal story, or make the
  failure seem smaller.

Porting implication:

- Preserve rationalization as a typed reinterpretation mode.
- Replace the specific `LEADTO` and `MINIMIZATION` logic with modern node
  templates or LLM-assisted counterfactual framing.

### 3.3 REVENGE

Trigger:

- a `FAILED-GOAL`
- a dependent negative emotion directed toward another person
- supplemental negative emotion strength `0.05`

Plan variants in source:

- mirror the relationship failure back onto the other person
- hurt the other person
- damage social esteem or standing

Interpretation:

- The source version is strongly interpersonal and grievance-driven.
- The activation rule is general enough to keep.
- The plan library is heavily domain-bound and often melodramatic.

Porting implication:

- Preserve directed grievance as a real mode.
- Generalize revenge into adversarial redress, social one-upsmanship, sabotage,
  exposure, or punitive fantasy rather than inheriting Mueller's exact scripts.

### 3.4 REVERSAL

Trigger:

- a `FAILED-GOAL`
- specifically an inferred top-level failed goal
- a dependent negative emotion
- `greater-reversal-activate?`, emotion strength `> 0.5`
- `emot-less-learn-thresh?`, meaning overall emotional state is not too extreme
- supplemental negative emotion strength `0.03`

Plan shape:

- generic coded `Reversal-Plan` calls `do-reversal`
- later example rules show domain-specific reversal bodies such as undoing the
  causes of academic failure

Interpretation:

- REVERSAL is not raw revenge and not mere comfort.
- It is "repair the outcome by exploring alternative causal routes."
- The emotion cap matters: the system only does this when it is upset but still
  able to reason.

Porting implication:

- Preserve reversal as structured alternative-outcome search.
- Reinterpret it as a counterfactual repair mode over your world material.

### 3.5 RECOVERY

Trigger:

- a `FAILED-GOAL`
- a dependent negative emotion
- `greater-recovery-activate?`, emotion strength `> 0.5`
- `emot-less-learn-thresh?`, again requiring emotional state not to be too high
- supplemental negative emotion strength `0.02`

Plan shape in source:

- the concrete plan body is narrow and domain-specific, especially around
  relationship repair

Interpretation:

- The activation logic is broad.
- The realization library is narrow.
- Recovery is "resume or repair after failure," not "generate a happy scene."

Porting implication:

- Keep recovery as a distinct mode from reversal.
- Reversal changes the counterfactual outcome.
- Recovery keeps the failure but looks for how life continues or repairs around
  it.

### 3.6 REHEARSAL

Trigger:

- there is an active `WAIT` subgoal
- the containing top-level goal is concrete enough to contain no variables
- a positive emotion is linked to that top-level goal
- `greater-rehearsal-activate?`, emotion strength `> 0.5`
- `emot-less-learn-thresh?`
- supplemental positive emotion strength `0.01`

Plan shape:

- `Rehearsal-Plan` simply re-activates the goal object for imaginative
  achievement

Interpretation:

- This is much more specific than "practice future possibilities."
- It is "while blocked or waiting on a concrete goal that still feels positive,
  mentally run through it."

Porting implication:

- Preserve the blocked-but-still-invested shape.
- In Round 03 terms, rehearsal should often happen near unresolved situations
  that are not yet ripe for commitment.

### 3.7 The families are deliberately overlapping

Mueller is not using an exclusive state machine where a failure enters one
daydream mode.

The same failed goal can activate several competing daydream goals at once:

- `RATIONALIZATION`
- `REVENGE` if anger is directed
- `ROVING`
- `REVERSAL`
- `RECOVERY`

The supplemental emotion strengths create a default priority ordering among those
competing goals:

- rationalization `0.06`
- revenge `0.05`
- roving `0.04`
- reversal `0.03`
- recovery `0.02`
- rehearsal `0.01`

This is a strong design clue. Mueller's system is built around simultaneous
pressure from multiple daydream modes, with the scheduler choosing among them by
strength.

### 3.8 REPERCUSSIONS

Trigger in source:

- effectively hardcoded around an earthquake example

Plan shape:

- hypothesize a state
- then recurse through the rest of a list of downstream states

Interpretation:

- The concept is valuable: project consequences outward from an event.
- The implementation in source is not a reusable general mechanism.

Porting implication:

- Do not "port the rule."
- Port the mode: aftermath projection, cascade imagination, and chain reaction
  traversal.

## 4. Episodic Retrieval Is More Specific Than "Search Memory"

`dd_epis.cl` confirms the strong claim from the earlier chat: retrieval is a
coincidence counter.

### 4.1 Retrieval counts converging marks

`epmem-retrieve1`:

- activates multiple indices
- increments marks on episodes that share those indices
- retrieves when `marks >= threshold`

This is not cosine similarity. The system is checking how many independent cues
currently converge on the same remembered episode.

### 4.2 Episodes carry separate planning and reminding thresholds

When episodes are stored, Mueller maintains:

- `plan-threshold`
- `reminding-threshold`

Those grow independently as an episode is indexed under more cues. So retrieval
already distinguishes "use this while solving something" from "this suddenly
comes back to mind."

### 4.3 Serendipity lowers the threshold by one

When serendipity is enabled, retrieval uses `threshold - 1`.

That sounds simple, but it matters. Mueller treats lucky coincidence as "almost
enough reasons became enough."

### 4.4 Reminder bias is small and local

The retrieval system also keeps:

- only a short recent-index list, capped at `6`
- only a short recent-episode list, capped at `4`
- emotion indexing that collapses to `POS-EMOTION` or `NEG-EMOTION`

The point is not rich semantic memory. The point is a lightweight, constantly
shifting reminder field.

### 4.5 Reminding can feed serendipity

`epmem-reminding` can reactivate an episode, add its indices back into recency,
run serendipity, and then run reminding again.

That creates a small loop where reminders create new analogical opportunities.

Porting implication:

- Preserve mark counting, thresholds, recency, and simple valence tags.
- Do not default to embedding retrieval as the primary memory mechanism.
- If embeddings are used later, use them as an auxiliary recall source, not as
  the core Mueller-like reminding system.

## 5. Serendipity Is More Than Threshold Lowering

This is where the March 12 chat was most incomplete.

### 5.1 There are two serendipity mechanisms

The earlier chat was most incomplete here.

1. Threshold serendipity in `dd_epis.cl`
   Lower retrieval threshold by one when a coincidence is almost strong enough.

2. Rule-intersection serendipity in `dd_ri.cl`
   Search for a rule path that structurally connects current concerns to newly
   surfaced material.

Both matter, and they are not interchangeable.

### 5.2 Serendipity runs against top-level goals

`run-serendipity` and `run-top-level-goal-serendipity` do not merely tweak
retrieval. They inspect active top-level goals, especially:

- daydreaming goals with a unique subgoal
- certain halted personal goals with variables

### 5.3 It intersects rule paths

`serendipity-recognize-apply`:

- intersects top rules and bottom rules
- condenses the resulting paths
- turns them into an episode-like structure
- creates new analogical plans from the result
- only treats constructed plans as usable if they already occur in known episodes

The search is not unbounded. The source sets:

- `*max-ri-depth* = 5`
- `*max-paths-found* = 3`

The resulting paths are then sorted differently depending on use:

- for action mutations, prefer shorter paths
- otherwise, prefer longer paths

This is much closer to analogical replanning than to "sample something a little
weirder."

### 5.4 Surprise is tracked on the new plan

The new analogical plans are not silent substitutions. `surprise` creates a
surprise emotion, diverts it into the top-level goal, and unhalts the task if
it had been halted. Serendipity is therefore also a motivational event.

Porting implication:

- Preserve serendipity as a structural re-route when multiple cues nearly align.
- In Round 03, that probably becomes: coincidence retrieval plus a constrained
  recombination step that emits a new candidate node or edge and marks it as
  surprising.

## 6. Mutation Is Coupled To Failed Action Planning

`dd_mutation.cl` is narrower and more disciplined than the phrase "mutation" can
make it sound.

Mutation is only attempted from `all-possibilities-failed` in `dd_cntrl.cl`, and
only when all of the following hold:

- the system is in `daydreaming` mode
- the top-level goal has `planning-type = imaginary`
- `action-mutations` succeeds

- `action-mutations` runs only once per top-level goal
- it walks leaf contexts
- it mutates action goals
- `action-mutation` checks whether serendipity can make the mutated action work

Interpretation:

- Mutation is not free exploration.
- Mutation is a recovery move when an action path is blocked.
- It is tightly coupled to the serendipity machinery in a real pipeline:
  mutate action, then test the mutated action through serendipitous connection.

Porting implication:

- Preserve mutation as a fallback for local stuckness.
- In Round 03 that likely means mutating one scene element, relation, role, or
  tactic when a node sequence stalls, then checking whether the mutated variant
  coheres with current situation cues.

## 7. Contexts Are Planning Workspaces, Not The Final Product Graph

`gate_cx.cl` makes this very explicit.

`cx$sprout` copies forward:

- parent / ancestor structure
- current asserted objects
- touched-fact bookkeeping
- mutation bookkeeping
- timeout-related metadata

That is a planning workspace tree. It preserves "how this possibility was
explored," not "what the audience should traverse."

Porting implication:

- Do not confuse Mueller's context tree with the final dream graph.
- The adaptation needs an explicit translation step from planning workspace to
  emitted `DreamNode` / `DreamEdge` structures.

## 8. What To Preserve Literally Vs Reinterpret

### 8.1 Preserve as literally as possible

- Short-cycle scheduling over competing top-level goals
- Goal-family-driven daydream types
- Coincidence-count retrieval with recency and valence
- Structured mutation as stuckness handling
- Separate planning contexts for real and imaginary processing

### 8.2 Reinterpret, not port literally

- Domain-specific plan bodies in `dd_kb.cl`
- Earthquake-specific `REPERCUSSIONS`
- Relationship melodrama used as the default content domain
- The exact GATE rule language and theorem-proving machinery

### 8.3 Additions that are ours, not Mueller's

- `DreamNode` / `DreamEdge` emission for Scope + Lyria
- Optional narration and caption tracks
- Admission / validation around generated nodes
- Any critic-selector or quality-monitor layer

Those may be good ideas, but they should be documented as new layers rather than
quietly attributed back to Mueller.

## 9. Round 03 Build Consequences

If we stay honest to the source, the first engine does not need to solve
everything.

It needs to prove five behaviors:

1. unresolved situations can activate distinct daydream modes
2. the scheduler can obsess and switch based on changing goal strength
3. recall happens through coincidence, not just nearest-neighbor similarity
4. stuck sequences can mutate structurally
5. counterfactual planning can emit nodes for the stage without corrupting canon

That is why the current recommendation still stands:

- do the first behavioral engine in Python
- keep engine outputs as pure data
- preserve Mueller's control logic where it is still the load-bearing part
- leave Clojure open for a later engine rewrite if the symbolic layer proves to
  be the actual bottleneck

One additional source-grounded consequence: do not treat REHEARSAL,
RATIONALIZATION, REVERSAL, and RECOVERY as narrative labels that an LLM picks
freely. In Mueller they are competing control policies with distinct triggers.

## 10. Immediate Documentation Follow-On

The next useful note after this one is not another architecture rewrite. It is a
small adaptation table:

- Mueller trigger
- Round 03 state signal
- candidate node strategy
- validator rule
- stage emission form

That would turn the source trace into an implementation checklist.
