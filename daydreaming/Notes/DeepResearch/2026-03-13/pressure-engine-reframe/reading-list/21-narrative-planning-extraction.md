# Narrative Planning / IPOCL - L3 Extraction

Source: Riedl & Young, "Narrative Planning: Balancing Plot and
Character" (JAIR, 2010)

Source file: `sources/text/NarrativePlanning.txt`

Purpose: Extract what IPOCL contributes to the multi-situation
architecture, especially around plot coherence, character
intentionality, author goals, and the missing bridge between L2
motivation and L3 sequence control.

---

## What the paper actually describes

This is not a beat scheduler like Facade and not a simulationist
social world like Versu. It is a **centralized fabula planner**:
given an initial world state, an author-specified outcome, and a
library of symbolic actions, generate a story-world action plan
that is:

1. causally coherent as a plot
2. believable because characters appear intentional

The core claim is that ordinary planners can satisfy the author's
outcome while still producing nonsense character behavior, because
they treat every action as instrumental to the outcome instead of
asking whether a reader can infer why a character is doing it.

IPOCL extends partial-order causal-link planning with explicit
machinery for character intentionality:

- **Author goals**: the outcome the human author wants the story to
  reach
- **Character goals**: the local goals characters appear to pursue
  inside the story
- **Frames of commitment**: an interval of actions performed by one
  character in pursuit of one goal
- **Motivating steps**: actions/events that make it reasonable for a
  character to adopt that goal
- **Happenings**: events that do not need to be intentional
- **Intentional completeness**: every non-happening action must
  belong to at least one frame of commitment

Algorithmically, IPOCL adds new flaw types on top of POCL:

- open motivation flaws
- intent flaws
- intentional threat flaws

So the planner is not just solving open preconditions and causal
threats. It is also solving the question: "why is this character
doing this, and what larger pursuit is this step part of?"

The paper's evaluation matters. It is not just a design essay. The
authors compare IPOCL-generated stories to conventional POCL stories
and find suggestive evidence that readers are better able to answer
"why" questions about character actions when the story was generated
with IPOCL's intentionality machinery. (The "poor" question-answer
result reached p < 0.05; the "good" question-answer result reached
p < 0.0585, which the authors call "strongly suggestive" but which
does not clear conventional significance.)

---

## The stealable core

The paper's real contribution is not "use a planner." The useful
steal is the representation of **plot coherence and character
intentionality as separate but coupled constraints**.

### 1. Decouple author outcome from character motivation

The author may want:

- the king dead
- the princess imprisoned
- a marriage to happen
- a betrayal to land

But the characters should not all look like they are secretly
colluding to realize that authored outcome. IPOCL's strongest idea
is that author goals and character goals must be represented
separately.

This matters directly to us. A watched run can have authored
destination pressure, event-commit pressure, or conductor-shaped arc
pressure without implying that any given character consciously wants
that same outcome.

### 2. Represent pursuit threads explicitly

A frame of commitment is basically a tracked answer to:

- what goal is this character pursuing?
- what caused that pursuit to start?
- which actions belong to that pursuit?
- what action would count as achieving it?

This is the paper's most transferable structure. Our current docs
have concerns, situation activation, traversal diagnostics, and
event proximity, but they do not yet have an explicit runtime or
authoring-time object for a **character pursuit thread spanning
multiple selected situations**.

### 3. Motivation is part of coherence, not optional garnish

IPOCL does not just say that characters should have goals. It says
the audience should be able to infer when and why a goal was formed.
That is what motivating steps are for.

This is stronger than "the behavior kind of seems plausible." It
demands an explicit causal/motivational link between:

- something that happened in the world
- a character adopting a goal
- later actions in service of that goal

### 4. Believability needs an audit, not just vibes

IPOCL gives a concrete completeness test: if an action is not a
happening, it should belong to some intentional pursuit. In our
terms, this is an **orphan-action check**. If a selected node shows
a character doing something important and there is no recoverable
answer to "what are they trying to do here?", the sequence is
structurally weak even if it is locally vivid.

### 5. Inter-character motivation delegation ("contracted out")

IPOCL supports a mechanism where one character's motivating action
causes another character to form an intention that serves the first
character's goals. Character A can perform an action whose effect
makes it reasonable for Character B to adopt a new goal, and B's
subsequent pursuit of that goal may be part of A's larger plan.

This is relevant for multi-character coordination: persuasion,
manipulation, delegation, and alliance-building all have this shape.
One character's pursuit thread can depend on and incorporate another
character's separate, independently motivated actions.

### 6. Global plot reasoning still matters

The paper is firmly in the deliberative camp: it wants the system to
reason over the whole fabula so the resulting sequence is globally
causal, not merely locally reactive. That is the tradition's real
value for the current architecture. It protects against the failure
mode where each local step is motivated but the larger run has no
legible throughline.

---

## What this tradition gives that the current stack lacks

The current architecture in `11-settled-architecture.md` already has
an important split:

- L2 handles motivated local cognition
- L3 handles traversal over authored material

That split is still right. IPOCL does **not** argue for collapsing
L2 and L3 back into one planner. What it adds is a missing middle
representation.

### 1. A run-level model of intentional throughlines

Current docs have:

- `CharacterConcern`
- social-practice direction from Versu
- `TraversalDiagnostic`
- `TraversalIntent`
- event proximity / committable events

What they do not yet have is a compact object that says:

- character X is currently pursuing Y
- pursuit Y was activated by Z
- these selected nodes/situations are in service of Y
- this would count as payoff, blockage, abandonment, or reversal

Without that, L2 can be psychologically rich and L3 can be
sequentially competent, but the architecture still lacks an explicit
handle for multi-situation intentional continuity.

### 2. A clean distinction between authored arc pressure and
character desire

Doc 11 gives us conductor bias, traversal intents, and event commit
state. IPOCL sharpens the missing distinction:

- the run can be steering toward an authored outcome
- the characters inside the run may be steering toward different,
  partial, temporary, or conflicting goals

That distinction becomes important as soon as we want the scheduler
to produce plot coherence without making characters feel like
puppets of the traversal system.

### 3. Explicit goal-formation moments

Our current docs talk well about concern dynamics and traversal
diagnostics, but are lighter on the exact question of **goal
adoption provenance**. IPOCL says that if a pursuit matters, the run
should usually show or be able to reconstruct the event that made
that pursuit legible.

That is especially relevant for:

- Q5-style setup/payoff structure
- Q7-style multi-situation coherence
- Q12-style event approach / commit legibility

### 4. An intentionality validator for selected material

The paper implies a practical verifier question for our authored
graph and traversal runs:

- for each important character action or selected node, is it
  intentional?
- if yes, what pursuit thread is it part of?
- if no, is it explicitly a happening, accident, reflex, or external
  imposition?

That validator does not currently exist in the docs as a first-class
contract.

### 5. Plot coherence as something stronger than local scheduler fit

Facade gave us a selection pipeline and target trajectory logic.
IPOCL adds a different missing piece: **whole-story causal and
motivational legibility**. Current L3 docs are strong on sequence
control but weaker on representing why a sequence of individually
reasonable node picks adds up to a coherent plotline.

---

## What maps to our architecture

| IPOCL concept | Our architecture | Use |
|---------------|------------------|-----|
| Author goal / outcome | Run-level authored arc pressure, event-commit targets, or authoring-time desired end state | Keep distinct from character motives |
| Character goal | L2 concern crystallized into a concrete pursuit, or situation-level aim | Temporary, local, revisable |
| Frame of commitment | Missing `PursuitThread` / `IntentThread` object | Tracks multi-situation continuity |
| Motivating step | Appraisal-triggering event, social-practice turn, memory reminder, or traversal selection that births a pursuit | Make commitment provenance explicit |
| Happenings | Accidents, involuntary reactions, outside shocks, or forced events | Mark as non-intentional instead of pretending every beat is chosen |
| Intentional threat | Conflicting simultaneous pursuits for one character | Diagnostic on thread layer |
| Orphan action | Selected node with no intelligible pursuit membership | Coherence failure or authoring warning |
| Global fabula planning | Authoring-time verifier, bounded lookahead, or offline coherence audit | Not shipping runtime control |

The key point is that IPOCL maps less to the current L3 scheduler
itself than to a missing layer between:

- L2 local motive formation
- L3 selection/traversal
- authoring-time coherence verification

---

## What's genuinely new vs. current docs

### 1. The explicit `author_goal != character_goal` contract

The architecture already implies this, but IPOCL makes it
structural. That is new. Right now the docs describe separate layers
but do not yet freeze a representation that prevents authorial arc
pressure from leaking into character motive semantics.

### 2. A first-class pursuit-thread object

Current docs have pressures, concerns, diagnostics, and intent
verbs. IPOCL contributes a concrete answer to the missing unit of
cross-situation coherence: a character-specific interval of pursuit
with origin, supporting actions, and terminal condition.

### 3. Motivation links as required structure

Setup/payoff exists in the L1 deficiency language, but that is still
artifact-level structural critique. IPOCL adds a specific kind of
setup/payoff: the setup that makes a character's later intention
readable.

### 4. Intentional completeness as a verifier criterion

This is not in the current docs. We do not yet have a rule like:
"every selected non-accidental character-significant action should
belong to some recoverable pursuit thread."

### 5. A stronger answer to the multi-situation problem

Current docs have situation activation and event approach, which are
good traversal controls. IPOCL adds the missing question: when we
move between situations, are those moves jointly serving legible
character pursuits and authored plot outcomes, or are they only
locally good picks?

---

## What to take

### 1. Add a lightweight pursuit-thread object

Not full IPOCL. Just enough structure to track intentional
continuity.

```
PursuitThread {
  id
  owner_agent_id
  goal_condition
  motivating_event_id?
  supporting_node_ids[]
  supporting_situation_ids[]
  status            // active | blocked | abandoned | achieved
  payoff_event_id?
  conflict_with_thread_ids[]
}
```

This can live in runtime state, authoring-side annotations, or both.
Its purpose is to make multi-situation intentional continuity
inspectable.

### 2. Add `happening` / `non-intentional` as explicit graph or event
annotation

Some things really are accidents, compulsions, shocks, or external
impositions. Mark them. Do not force fake motive explanations onto
everything.

### 3. Add motivation provenance to important pursuit changes

When a pursuit thread begins, the system should be able to point to:

- the appraisal event
- the memory reminder
- the social-practice turn
- the revelation
- the conductor-confirmed event commit

that made the pursuit legible.

### 4. Add an authoring/verifier pass for orphan actions

For authored graph material and for sampled runs, ask:

- which character-significant nodes are intentional?
- what pursuit thread are they in?
- what earlier node or state motivated that thread?

This is a strong candidate for a future L1/L3 sidecar tool.

### 5. Use IPOCL ideas for bounded coherence checking, not for full
runtime generation

The paper's best use for us is as:

- an offline run auditor
- a graph-annotation discipline
- a shallow lookahead scoring signal
- a way to evaluate whether an upcoming event commit has enough
  motivational support

That is much more plausible than making shipping L3 into a symbolic
story planner.

---

## What not to take

### 1. Do not replace the authored-graph stack with a full fabula
planner

IPOCL generates the story itself from symbolic action schemata. Our
shipping architecture is explicitly graph-first. That boundary
should stay.

### 2. Do not make L3 centrally choose every character action

IPOCL is a centralized planner over all characters. Our system
already has a better split:

- L2 owns local motivated behavior
- L3 owns traversal over authored material

The steal is the pursuit-thread representation, not the totalizing
control model.

### 3. Do not import the paper's success-only assumption

One of IPOCL's biggest limitations is that each frame of commitment
ends in successful goal achievement. That is bad for us. We need:

- blocked attempts
- failed intentions
- abandoned pursuits
- reversals
- anti-payoffs

Those are central to dramatic pressure.

### 4. Do not adopt the full symbolic search burden

IPOCL is expensive, heuristic-hungry, and not practical as a live
runtime control loop. The paper itself reports severe search cost.
That is a warning, not an invitation.

### 5. Do not confuse plot proof with lived texture

IPOCL is strong on causal/motivational legibility, weak on the
texture that comes from appraisal dynamics, mood, ambiguity,
hesitation, and situated social affordances. We still need EMA,
Versu-style practices, and the Mueller line for that.

---

## Bottom line for this architecture

Narrative planning is not the new base architecture. But it does
identify a real missing piece in the current stack.

Facade gave us a strong model for **how to pick the next authored
unit**.

Versu gave us a strong model for **how social situations structure
local affordances**.

IPOCL gives the missing model for **how a run can maintain legible
character pursuit and authored plot coherence across multiple
situations without collapsing character desire into author desire**.

So the right steal is not "build a symbolic story planner." The
right steal is:

1. explicit pursuit-thread representation
2. explicit motivation provenance
3. orphan-action / intentionality verification
4. a stronger distinction between authored outcome pressure and
   character motive

That is the part of the tradition the current docs do not yet fully
cash out, and it is directly relevant to the multi-situation phase.
