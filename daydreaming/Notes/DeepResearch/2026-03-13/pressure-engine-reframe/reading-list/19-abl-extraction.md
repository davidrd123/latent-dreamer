# ABL — Believable-Agent Runtime Extraction

Source: Mateas & Stern, "A Behavior Language for Story-based
Believable Agents" (AAAI Spring Symposium, 2002)

Source file: `sources/markdown/ABL/source.md`

Purpose: Extract the parts of ABL that matter for the pressure
engine's missing **micro-runtime for situated multi-character
interaction**, not to import Oz/Façade wholesale.

---

## What the paper actually describes

ABL is not a drama manager and not a cognition theory. It is a
**reactive behavior language/runtime** for believable agents in an
interactive drama.

Its job is to let characters:

1. Pursue multiple goals concurrently
2. React immediately to changing world state
3. Coordinate with other characters on shared dramatic action
4. Expose enough runtime structure that authored meta-behaviors can
   inspect and redirect what is currently happening

In Façade terms, ABL is the **within-the-beat execution layer** under
the higher-level beat scheduler. That makes it relevant to us as a
possible model for the layer that would execute a social practice,
bridge interaction, or coordinated event once L2/L3 has decided that
it should happen.

---

## The stealable core

### 1. Behavior selection is guarded, not monolithic

ABL agents do not have one giant policy. They have many behaviors with
the same goal signature, and the runtime chooses among them using:

- signature match
- precondition satisfaction
- specificity
- fallback on failure

This is a practical pattern for "same motive, different realization."
Opening the door, avoiding a question, or making a conciliatory move
can each have multiple authored realizations selected from local state.

### 2. Start conditions and continue conditions are separate

ABL distinguishes:

- **precondition**: when a behavior is allowed to start
- **context condition**: what must remain true for it to keep running

This is more important than it sounds. A character may begin a move
under one mood or social setup, then abort it when the situation
changes. That is a different design problem from initial selection.

### 3. Runtime state is explicit and inspectable

Parallel expansion produces an **active behavior tree (ABT)**. This is
the live structure of what the agent is currently pursuing.

ABL then allows:

- reflection over the ABT
- failing/succeeding/suspending running behaviors
- modifying priorities/persistence on active subgoals

This gives authors a way to write *meta-behaviors* that respond to
what the character is currently doing, not just to external world
state.

### 4. Multi-character coordination is first-class

ABL's main extension over Hap is **joint behavior** support.

Joint behaviors give synchronized:

- entry
- exit
- suspension

across a named team of agents. The runtime coordinates this with a
negotiation protocol rather than assuming one agent can silently force
the others into the same state.

### 5. Shared team memory is used for local episodic coordination

During a joint behavior, ABL creates a named **team working memory**.
Subgoals can post completion records into that memory, and other
participants can wait on those records.

This is a lightweight way to coordinate timing and local sequence
without centralizing the whole interaction in one controller.

### 6. Synchronization is attachable to specific subgoals

ABL's `synchronize` annotation prevents duplicated joint-behavior
copies by forcing coordination at the intended subgoal site, not at an
arbitrary root.

That matters because coordinated interaction is usually not "all of us
enter one shared state forever." It is "we need to line up **here**,
then continue our own local threads."

---

## Useful concepts in ABL

| ABL concept | Why it matters |
|-------------|----------------|
| `precondition` | authored availability gate |
| `context_condition` | authored abort/continue rule |
| `specificity` | choose more exact realization when multiple behaviors fit |
| `success_test` | opportunistic completion when the world changes in your favor |
| persistent subgoal | keep trying while situation remains live |
| ABT | inspectable live execution state |
| reflection | authored meta-control over current behavior |
| named memory / team memory | shared local coordination state |
| joint behavior | synchronized multi-agent dramatic action |
| goal spawning | let long-running side goals outlive a local behavior |

---

## What maps to our architecture

| ABL concept | Pressure-engine equivalent | Notes |
|-------------|----------------------------|-------|
| Goal signature | Typed operator family or situation move | same pressure can realize in multiple ways |
| Behavior library | Authored realization library | situation-specific action patterns, not whole cognition |
| Preconditions | Operator/situation guards | concern + role + local world state |
| Context conditions | Abort/retarget rules | if the social moment shifts, stop forcing the old move |
| ABT | Active situation execution state | a missing concept in current docs |
| Reflection | Critic/monitor over active interaction | should be narrower than ABL's full reflective power |
| Joint behavior | Multi-character situation enactment | confession, confrontation, repair, rehearsal |
| Team memory | Situation-local shared trace | a local blackboard / completion log for the interaction |
| Goal spawning | Background carry-over thread | lets one concern keep simmering while another move occurs |

The cleanest fit is **between L2 and realized situation performance**:
L2 or L3 decides *what kind of move is happening*; an ABL-like runtime
would decide *how that move unfolds across one or more characters in
time*.

---

## What's genuinely new vs. current docs

### 1. A real execution substrate for social practices

Current notes are strong on:

- typed operators
- concerns
- social practices as reusable situation templates
- graph traversal and selection

But they are still thin on the **runtime mechanics of enacting a
situation once selected**. ABL is useful because it is concrete about
how ongoing, interruptible, multi-step, multi-character behavior is
represented and coordinated.

### 2. The precondition / context-condition split

Current architecture language tends to talk about whether a move is
available or not. ABL adds the crucial distinction between:

- why a move starts
- why it should continue

That is directly relevant to evasion, confession, confrontation, and
repair, where the character's willingness to continue can change
mid-stream.

### 3. Runtime reflection over active behavior

The current stack has traces, diagnostics, and post-hoc justification.
ABL adds the idea that the agent can inspect a structured model of what
it is **currently** doing and redirect it.

That is different from adding more static annotations. It is a live
control hook.

### 4. Decentralized coordination with explicit sync points

Current docs describe social practices and multi-character situations,
but not the mechanics by which two characters line up on the same local
interaction. ABL shows one answer: joint entry/exit plus subgoal-level
synchronization plus local shared memory.

### 5. Handlers as interrupt architecture

ABL organizes beat execution with:

- beat goals
- handlers for player interaction
- cross-beat/background behaviors

The exact Façade form is not ours, but the deeper idea is useful:
**foreground interaction and interrupt handling should be separate
authored structures**.

---

## What to take

1. **A lightweight active execution state for situations.**
   Not a full ABL clone, but some inspectable runtime object for "what
   this character or pair is currently trying to do."

2. **Separate start guards from continuation guards.**
   This should become a standard part of situation/operator design.

3. **Joint enactment for multi-character moves.**
   Confession, accusation, repair, invitation, and deflection all want
   synchronized entry points and explicit re-sync moments.

4. **Situation-local shared memory.**
   A local completion log or blackboard is a clean way to coordinate
   participants without centralizing all choice in one master script.

5. **Selective interrupt handlers.**
   The system should distinguish "continue the intended move" from
   "handle interruption, then resume or abandon."

6. **Meta-control hooks over active execution.**
   A narrow reflective layer could let a critic or situation monitor
   suspend, redirect, or downgrade an in-progress move when tension,
   role alignment, or legibility shifts.

---

## What NOT to take

- The full ABL language/runtime as a new core substrate
- Reflection powerful enough to arbitrarily rewrite all running state
- Façade's player-dialog assumptions
- Hand-authoring every interaction at beat-script scale
- The full distributed commit protocol unless the runtime is actually
  distributed
- Tight coupling to animation/NLP-specific execution details

---

## Minimal import for us

If we steal only the high-value part of ABL, it looks like this:

```text
SituationRuntime {
  active_moves[]
  local_shared_state
  interrupt_handlers[]
  continue_guards[]
  sync_points[]
}
```

Where:

- **L2/L3** choose the move or situation
- **SituationRuntime** executes it across time
- **narration/rendering** observe the resulting structured trace

That would give the architecture something it currently lacks:
an explicit answer to "what happens after a concern/operator/situation
has been selected, but before it becomes merely a finished trace or a
committed graph event?"

ABL is the strongest nearby source for that missing layer.
