# FAtiMA Toolkit — Nearest-Neighbor Extraction

Source: Mascarenhas et al., "FAtiMA Toolkit - Toward an
effective and accessible tool for the development of
intelligent virtual agents and social robots" (2021)

Source file: `sources/markdown/FAtiMA/source.md`

Purpose: Extract the toolkit and architecture ideas that are
actually relevant to the pressure-engine stack, especially for
L2 appraisal, pluggable reasoners, dialogue structure, and
authoring-time tooling.

---

## What FAtiMA Toolkit actually is

FAtiMA Toolkit is not a single character architecture in the
Mueller sense. It is a **componentized socio-emotional agent
toolkit** built around a central agent state and several optional
libraries: appraisal, decision-making, dialogue, social
reasoners, authoring tools, simulator, and API bridge.

The base loop is simple:

1. Events update beliefs and memory
2. Appraisal rules generate emotions
3. Decision rules choose candidate actions
4. Optional reasoners can participate through meta-beliefs
5. Dialogue and world editors define the authorable interaction
   surface

So the paper is less about a novel "mind" and more about a
**usable architecture seam** for combining affective appraisal,
rule-based authoring, and domain-specific algorithms.

---

## The stealable core

### 1. Appraisal as a portable component

FAtiMA keeps the emotional appraisal layer independent from any
single planning or decision algorithm. Events come in, appraisal
rules compute appraisal variables, and emotions are updated from
that result. The important move is architectural, not theoretical:
**appraisal is a reusable service, not a byproduct of one
particular planner**.

This is close to what our L2 refactor wants. Concern updates
should remain a typed layer with a stable contract, even if
different generators, critics, or search procedures sit on either
side of it.

### 2. Meta-beliefs as the seam for foreign reasoners

This is the most useful idea in the paper.

FAtiMA lets specialized algorithms appear inside the shared rule
system as **meta-beliefs**. That means an author can still write
appraisal and decision rules declaratively, while some conditions
or values are supplied by an external process such as:

- theory-of-mind lookup
- social-importance estimation
- search / planning
- Monte Carlo Tree Search in the game examples

Conceptually:

```text
typed rules
  read beliefs + meta-beliefs
meta-beliefs
  may call arbitrary domain reasoners
shared control layer
  stays legible even when internals differ
```

This is exactly the kind of seam we need when an L2/L1 control
loop should remain inspectable, but some subproblems benefit from
custom computation or model calls.

### 3. Hybrid dialogue structure

FAtiMA argues that dialogue should not be treated as just another
flat action in the agent model, but it also rejects a pure
dialogue-tree runtime where all control lives in the tree.

Its compromise is:

- centralized state graph for all possible dialogue moves
- agent logic decides which move to take
- dialogue graph defines reachability and transition structure
- appraisal / decision rules still determine behavior

That is useful because it separates:

- **conversation topology**
- from **agent choice inside that topology**

For us, the key value is not the specific dialogue editor. It is
the idea that multi-party conversational situations can have an
explicit authored state space without collapsing character
cognition into a dialogue tree.

### 4. Authoring tools with validation, not just editing

The integrated authoring tool matters because it includes:

- dialogue-state reachability checks
- end-state counting
- state graph visualization
- simulator-driven iterative testing
- live inspection of beliefs, emotions, memory, goals, and rule
  triggers

This is stronger than "a GUI for rules." It is an argument that
authoring-time systems for cognitive characters need
**verification surfaces**, not only edit surfaces.

That aligns with the project's broader direction around graph
validation, pack assembly verification, and visible provenance.

---

## What maps to our architecture

| FAtiMA concept | Our equivalent | Notes |
|---|---|---|
| Emotional Appraisal component | L2 concern/appraisal update layer | Best match. Keep typed updates stable while generators vary. |
| Meta-belief | Typed seam to external reasoner or model-backed helper | Good fit for search, retrieval, evaluators, or situation-specific calculators. |
| Rule-based decision surface | Operator activation / proposal gating | We should keep stronger typed boundaries than FAtiMA's general action rules. |
| Central dialogue state graph | Authored situation/practice state machine | Useful for conversation-heavy situations, not for all cognition. |
| Role Play Character state | Character packet: beliefs, memory, affective state | Broad conceptual match. |
| Autobiographical memory | Episodic history / retrieved episodes | Similar use, though our memory is more tightly tied to daydreaming and reminding. |
| Integrated authoring tool | Graffito/L1 authoring and validation tooling | The main reusable lesson is validation and simulator support. |

---

## What's genuinely new vs. current docs

### 1. A concrete pattern for mixing declarative control with arbitrary reasoners

The current docs already want typed control loops and selective
LLM use. FAtiMA adds a concrete pattern for doing that without
breaking inspectability: foreign computation appears through
named, typed meta-beliefs rather than replacing the control loop.

This is a stronger formulation of the same instinct behind:

- keeping L3 deterministic
- keeping L2 typed even if content generation varies
- allowing external evaluators or planners behind stable seams

### 2. Dialogue as authored topology plus agent choice

Current notes talk about social practices and situation structure,
but FAtiMA contributes a practical framing for conversation-heavy
situations: maintain a central state graph of possible dialogue
states while leaving local choice in the agent.

That is especially relevant for:

- interviews
- interrogations
- confessions
- confrontations
- any multi-party "who has the floor?" scene

### 3. Verification-oriented authoring tools

The paper is unusually explicit that accessibility is not only
"make rules editable." It includes reachability analysis,
simulator support, and live debugging against authored rules and
state.

That is directly useful for L1 and graph tooling. If we build
authoring interfaces for situations or graph fixtures, they
should expose unreachable states, dead branches, and rule-trigger
visibility.

### 4. Prospect-based emotions from goal-likelihood changes

FAtiMA generates a full set of prospect emotions (Hope, Fear,
Relief, Disappointment, Satisfaction, Fears-Confirmed) from
changes in goal likelihood, not just from event desirability.
Goals carry significance and likelihood values; when likelihood
shifts, the corresponding prospect emotion fires.

This is directly relevant to our concern/pressure dynamics.
A character who sees their goal becoming less likely does not
just feel "bad" — they feel specific prospect emotions that
should drive different coping responses. The current docs do
not yet distinguish event-triggered appraisal from
goal-likelihood-triggered appraisal, and FAtiMA shows why
that distinction matters.

### 5. Appraisal as a reusable service across domains

The paper reinforces that appraisal should be portable across
dialogue systems, games, and robots. That supports the idea that
our concern/update machinery should be reusable across:

- authoring-time generation
- conversation-heavy fixtures
- multi-situation runtime work later

---

## What to take

1. **Meta-belief seam for specialized computation.** Keep the main
   control geometry typed and legible, but let specific fields be
   supplied by search, retrieval, learned models, or other custom
   reasoners.

2. **Appraisal as an independent subsystem.** Event interpretation
   and affective update should not be hardwired to one planner or
   one prompting strategy.

3. **Hybrid conversation modeling.** For dialogue-heavy situations,
   author a centralized state graph while letting the character
   mind choose moves within it.

4. **Authoring tools that validate.** Reachability checks,
   simulator passes, state inspection, and rule-trigger visibility
   should be first-class in any L1/L2 authoring environment.

5. **Bridgeability as a design goal.** The Web API and library
   framing are reminders that cognitive components become much
   more usable when they can be embedded into other runtimes
   without framework lock-in.

---

## What not to take

- The full FAtiMA toolkit stack as our architecture backbone
- The generic action-rule engine as the primary mind model
- OCC completeness or the paper's full emotion catalog as a
  required ontology
- Dialogue-state graphs as the universal representation for all
  situations
- Robotics / embodiment integration concerns as a current
  priority
- The many optional side modules as immediate scope for this repo

The real steal is the **seam design**, not the whole toolkit.

---

## Bottom line

FAtiMA is the closest "practical toolkit architecture" neighbor
to what we are trying to do. Its most valuable contribution is
not a better theory of inner life than Mueller. It is a concrete
way to keep socio-emotional control **authorable, inspectable,
and extensible** while still allowing specialized algorithms to
participate.

If Mueller gives us the distinctive inner process, FAtiMA gives
us a useful reminder about the surrounding engineering: typed
appraisal surface, plug-in reasoner seams, structured dialogue
topologies, and authoring tools that make rule systems debuggable
by humans.
