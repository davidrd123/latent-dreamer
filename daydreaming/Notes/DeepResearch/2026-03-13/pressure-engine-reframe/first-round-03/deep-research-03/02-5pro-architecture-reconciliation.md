# 5 Pro: Design Space + Architecture Reconciliation

Date: 2026-03-15

## How this works

This is a prompt for 5 Pro (ChatGPT 5.4 Pro) in the ChatGPT
interface. The document contains two asks (A and B) meant to be
sent as separate RepoPrompt requests against this file.

- **Ask A** and **Ask B** can go in parallel (separate chats or
  separate turns). A is design-space; B is project-specific.
  They don't depend on each other.
- **Ask C** goes after both A and B replies are in hand. It
  references both.

Save replies under `deep-research-03/replies/`.

---

# Ask A: What is this thing?

## Context for Ask A

We are building something like a **cognitive character engine**.
Computational characters with genuine inner lives — concerns that
compete, memories that surface unbidden, coping strategies that
fail and shift — that can be steered by a human and externalized
through multiple modalities (audio, narration, visualization,
eventually video).

What we've built so far:

- **Cognitive substrate** derived from Mueller's DAYDREAMER (1990):
  concern-driven cognition, seven daydreaming operators (avoidance,
  rehearsal, rationalization, reversal, recovery, roving,
  repercussions), episodic memory with coincidence-mark retrieval,
  recursive reminding, reappraisal after each cognitive step.

- **Appraisal semantics** from EMA (Gratch & Marsella) and OCC
  (Ortony, Clore, Collins): emotions as appraisal patterns over
  desirability, controllability, likelihood, praiseworthiness.

- **Social practices** from Versu (Evans & Short): characters
  operate within socially legible frames (evasion, confession,
  anticipated confrontation) that constrain which moves are
  available.

- **Traversal scheduler** shaped by Facade (Mateas & Stern):
  precondition → priority tier → score → weight → select. The
  scheduler walks a pre-authored dream graph. A human performer
  steers traversal through a physical control surface (MIDI
  controller with faders and pads).

- **LLM generation pipeline** that produces graph nodes at
  authoring time, conditioned on the cognitive substrate. The
  LLM writes character moments ("play the objective under
  pressure") while the cognitive layer determines which operator
  fires, which concerns are active, and what the character is
  trying to do.

- **A Director agent** (LLM-based) that reads the cognitive state
  through a creative brief and can introduce novel concepts, wake
  dormant situations, and produce external events that provoke
  character reactions.

The first creative application is a conducted performance piece
(Graffito), but the broader ambition includes writing companions,
research daemons with preoccupations, and persistent cognitive
characters that can be placed in any situation.

## Questions for Ask A

### A1. Map the design space

What are the axes along which cognitive character engines can
vary? Where do existing systems sit in this space?

Some dimensions we've identified:
- Cognitive fidelity (shallow state vs. deep cognitive model)
- Authoring burden (fully hand-authored vs. fully generated)
- Human control (spectator vs. conductor vs. co-author vs. player)
- Externalization modality (text, audio, dashboard, video, game)
- Temporal mode (real-time performance vs. offline authoring vs.
  persistent daemon)
- Character scope (single character vs. multi-character vs.
  character ecosystem)
- World dynamics (static situations vs. event-driven vs.
  fully simulated)

What other dimensions matter? Where do existing systems
(DAYDREAMER, Versu, Facade, The Sims, Dwarf Fortress — deepest
emergent character simulation in games, interactive fiction
engines, AI companions, digital puppetry, BDI agent frameworks)
sit?

### A2. Locate our project

Given what we've built, where are we in the space? What's the
neighborhood — what nearby designs would we get by changing
one or two dimensions?

### A3. What are we not seeing?

Are there dimensions of this design space we haven't considered?
Are there existing systems or research traditions we should know
about that occupy nearby positions?

### A4. Is "conducted cognitive character" a coherent category?

Or is it trying to be too many things at once? What would the
most focused version look like? What would the most general
version look like?

### A5. What are fundamentally different approaches to the same goal?

We arrived at our current approach through a specific path:
Mueller gave us cognitive control geometry, EMA/OCC gave us
appraisal semantics, Versu gave us social practices, Facade
gave us traversal scheduling. That's one way in.

But if someone else were building "computational characters
with genuine inner lives that can be steered and externalized,"
what other foundations might they choose? What would the system
look like if you started from:

- **Predictive processing / active inference** instead of
  Mueller's concern-driven model?
- **Narrative planning** (MINSTREL, Dramatica) instead of
  graph traversal?
- **Reinforcement learning with intrinsic motivation** instead
  of rule-based operator selection?
- **LLMs as the cognitive engine directly** — concerns and
  operators as prompt state rather than as scored formulas?
- **Embodied simulation** (robotics / game AI emotion models)
  instead of literary cognition?
- **Improvisation / Meisner-style reactive systems** instead of
  goal-driven daydreaming?
- **BDI agent architectures** (belief-desire-intention) — the
  dominant paradigm in autonomous agents. Characters as BDI
  agents with goals, plans, and belief revision. This is what
  most "AI character" companies actually build.

For each alternative foundation: what would you gain that we
currently lack? What would you lose that we currently have?
Is any of them clearly better for the "conducted cognitive
character" use case, or do they trade off in ways that depend
on priorities?

We are not looking to abandon our current approach. We want
to understand what it buys us and what it costs us relative
to the alternatives — so we can make informed decisions about
where to borrow ideas and where to stay the course.

## Context to include for Ask A

Light context only — 5 Pro doesn't need our codebase for this.

- This document (the Ask A section)
- `pressure-engine-reframe/34-broader-application-surface.md`
  (the broader vision — shows the ambition beyond performance)
- Summary of the evaluation ladder (from experiential-design/
  13-project-state.md): "We have 5 levels. Level 1: scheduler
  dynamics work (yes). Level 2: Director changes trajectory
  (partially). Level 3: output feels like anything (unknown).
  Level 4: 12-cycle run feels dreamlike (unknown). Level 5:
  different briefs produce different dreams (unknown). We are
  at Level 2-3."

## Preferred answer format for Ask A

**Important:** Do not reference our specific codebase vocabulary
(L1/L2/L3, Dreamer/Director/Stage) in the design space map.
Use generic terms. We'll map our specifics in Ask B. We want
to see the space, not our project reflected back at us.

1. **Design space map** — axes, existing systems placed on them,
   our project located
2. **Nearby designs** — what we'd get by changing 1-2 dimensions
3. **Blind spots** — dimensions or traditions we're not considering
4. **Is this a coherent category?** — honest assessment
5. **Alternative foundations** — for each: what you'd gain,
   what you'd lose, whether it's clearly better or a tradeoff

---

# Ask B: How do our pieces fit together?

Can be sent in parallel with Ask A. If Ask A's reply is
available, reference it — but Ask B is self-contained.

## Background for Ask B

The project has two vocabulary sets for the same architecture,
developed in different phases:

**Pressure-engine-reframe** (generation pipeline work) uses
**L1 / L2 / L3**:
- L1: authoring-time orchestration over L2 generation
- L2: Mueller-derived character exploration engine
  (CausalSlice, AppraisalFrame, operators, memory)
- L3: Facade-shaped traversal scheduler

**Experiential-design** (performance/rendering work) uses
**Prep / Dreamer / Director / Stage**:
- Prep: creative brief + style extensions + world
- Dreamer: internal cognition + traversal
- Director: LLM interpretive agent (introduces events,
  wakes dormant situations, provides external provocation)
- Stage: narration + audio + visuals

These are compatible but never mapped onto each other. Some
things one vocabulary names, the other doesn't (the Director
has no L-number; L1 has no Prep/Dreamer/Director/Stage role).

Important disambiguation: the "Director" in our codebase is
currently a specific thing — an LLM interpretive feedback agent
that reads cognitive state through a creative brief and
introduces novel concepts, situation boosts, and rare emotional
episodes. It is **not yet** a robust world-event author.

When discussing whether the Director should also generate world
events at authoring time, distinguish clearly between:
- the **current implemented Director** (interpretive perturbation)
- a **possible authoring-time provocation generator** (world events)
- **hand-authored world/event design** (human creative work)

## Tensions for Ask B

### B1. Vocabulary reconciliation

Map L1/L2/L3 onto Prep/Dreamer/Director/Stage on a 2-axis grid:
- authoring time vs. performance time
- cognition vs. traversal vs. interpretation vs. stage

Where do they overlap, where do they diverge, where is there
a genuine gap (something one vocabulary names that the other
doesn't)?

### B2. The Director at authoring time vs. performance time

The Director was designed as a **performance-time** agent.
But the generation pipeline produces character reactions to
fixed situations, and nothing currently generates the world
events that change situations. The Director is the natural
source of world events.

If the Director operates at both timescales:
- At authoring time: Director + pipeline iterate to produce
  a full graph (Director provides provocations, pipeline
  provides reactions)
- At performance time: Director introduces events that weren't
  pre-authored

How do these two modes relate? Is the authoring-time Director
the same agent with the same brief, or a different configuration?
What does the authoring loop look like concretely?

**The key concrete question:** What is the interface object
between "Director generates a world event" and "pipeline
generates a character reaction"? Is it a new situation added
to the fixture? An event node in the graph? A modification to
the existing situation description? The pipeline currently
reads from `fixture["situations"]` — what does the Director
write to?

For the interface object, specify:
- **name** — what is it called?
- **who writes it** — Director, human, pipeline, or some combination?
- **who reads it** — pipeline, traversal scheduler, both?
- **mutability** — append-only, mutable, or compiled?
- **where it lives** — fixture YAML, a world-state artifact,
  a runtime packet, or something new?

### B3. Generation pipeline ↔ Director integration

The generation pipeline was built without the Director in mind.
The Director was built without the generation pipeline in mind.

How should they integrate?
- Does the Director generate events that become new situations
  for the pipeline?
- Is there a shared state object both systems read/write?
- What is the authoring-time loop that produces a full
  multi-situation graph from their combined operation?

### B4. What provides world updates?

Mueller's DAYDREAMER has internal dynamics but its reality
context is updated externally. In our system, three candidates
for world-event source:

- **The Director** — designed for this, has a creative brief
- **Hand authoring** — the human writes situations and events
- **The pipeline's DerivedSituationState (Q7)** — accumulates
  effects from accepted steps

These aren't mutually exclusive. When does each one apply?
What is the relationship between them?

### B5. Should the creative brief and the generation fixture unify?

The Director needs `creative_brief.yaml` (concept, core_tensions,
interpretive_angles, obsessions, charged_objects).

The pipeline needs fixture YAML (characters, situations, concerns,
backstory episodes, practice contexts, benchmark step).

Three options:
- **Merge** into one artifact that both systems read
- **Keep separate** because they serve different layers
- **Shared world definition with layer-specific views** — one
  `world.yaml` that both read, each extracting what it needs

Which is right, and why?

### B6. Visualization contract

The first output modality is an inner-life visualization (not
video — GPUs are scarce). The Dreamer emits cycle packets. The
Director emits feedback. The scheduler emits selection decisions.

What data does the visualization layer need to subscribe to?
What is the minimal contract between the cognitive engine and
a visualization surface? This is an architectural question
(what flows where), not an aesthetic question (what it looks
like).

### B7. Internal dynamics as the reusable core

The broader vision extends cognitive characters to writing
companions, research daemons, game NPCs. Is the current
architecture building toward general-purpose cognitive
characters, or is it specific to conducted performance? What
would need to change to make the internal dynamics portable
across externalization modes?

## Questions for Ask B

1. **2-axis vocabulary map** — place everything on the grid

2. **Articulate 2-3 coherent versions** of how the Director,
   generation pipeline, and authoring loop relate. For each:
   - What does the authoring-time loop look like?
   - What does the performance-time loop look like?
   - What minimal shared state/contracts does it require?
   - What breaks if you pick this version?
   - Which version best matches the current codebase?
   - Which version should govern the next phase of work?

3. **Brief ↔ fixture unification** — merge, separate, or
   shared world definition with layer-specific views?

4. **Visualization contract** — what data does an inner-life
   visualization need to subscribe to? Minimal contract between
   cognitive engine and visualization surface.

5. **Identify the real design choices** — where different
   versions require genuinely different decisions, not just
   vocabulary alignment.

6. **Suggest what to test first** — given what exists now:
   - working generation pipeline (four fixtures, batch + admission)
   - validated narrow bridge tests (Tessa + Kai patch tests pass)
   - supply_v1 practical pass (keeper yield proven, edit cost measured)
   - Director still underintegrated with the authoring loop
   - L3 scheduler validated (Graffito pilot, City Routes three-arm)
   What is the cheapest experiment that would resolve the most
   important tension? The likely candidate is a minimal
   Director-provocation → fixture-delta → generation-pass loop.

7. **What NOT to decide yet** — explicit scope boundary.

## Context to include for Ask B

Heavier context — 5 Pro needs our specific docs:

Essential (paste in full):
- This document (the Ask B section)
- `pressure-engine-reframe/11-settled-architecture.md`
- `experiential-design/12-director-prompt-spec.md`
- `Notes/dashboard.md` (current version)

Summarize or excerpt:
- `pressure-engine-reframe/27-authoring-time-generation-reframe.md`
  (what the pipeline does and why)
- `experiential-design/17-game-engine-architecture.md`
  (the rendering architecture, relevant sections)
- `experiential-design/13-project-state.md`
  (evaluation ladder and falsification criteria only)

Include if context window allows:
- `Notes/current-sprint.md`
- `experiential-design/06-prep-layer.md` (brief format)
- `Notes/focused-source-trace.md` (Mueller source)

## Preferred answer format for Ask B

1. **2-axis vocabulary map** — one table or diagram
2. **2-3 coherent architecture versions** — with tradeoffs,
   minimal contracts, codebase match assessment
3. **Brief ↔ fixture** — merge / separate / shared-with-views,
   with reasoning
4. **Visualization contract** — minimal data subscription spec
5. **Real design choices** — what you actually have to decide
6. **Cheapest experiment** — what to try first
7. **Scope boundary** — what not to decide yet

---

# Ask C: What should we be thinking about that we aren't?

Send this after reading Ask A and Ask B replies. Reference both.

## Purpose

Asks A and B mapped the design space and reconciled our specific
architecture. Ask C steps back further. We want 5 Pro to think
about the project's trajectory, risks, and blind spots at a
level above the implementation.

We arrived at our current approach through a specific research
path (Mueller → EMA/OCC → Versu → Facade) and a specific
creative context (conducted performance for Graffito). That path
shaped what we see and what we don't. Ask C is about what we
might not be seeing.

## Questions for Ask C

### C1. What is the strongest version of this project?

If you were advising someone building a cognitive character
engine, and they had what we have (a working cognitive substrate,
a generation pipeline, a traversal scheduler, a partially-tested
Director, a physical control surface, and a creative brief from
a filmmaker), what would you tell them the strongest version of
this looks like?

Not what we've planned — what the *strongest possible outcome*
looks like given the ingredients. Where is the real leverage?
What is the thing this system can do that nothing else can?

### C2. What are the biggest risks to the vision?

Not implementation risks (those we can manage). Conceptual risks:

- Is there a reason this class of system hasn't been built
  before that we're not seeing?
- Are there fundamental tensions between the different goals
  (conducted performance vs. general cognitive characters vs.
  material supply scaling) that will force a choice we haven't
  acknowledged?
- Given your assessment of alternative foundations in Ask A,
  is there a specific limitation of the Mueller model that
  becomes critical as the project scales beyond single-situation
  generation into multi-situation arcs, Director-driven world
  events, and persistent cognitive characters?
- Could the LLM generation layer undermine the cognitive
  architecture by making the structured middle layer irrelevant?
  (If the LLM can produce good material with just a prompt and
  temperature, why maintain CausalSlice and operator scoring?)

### C3. Where is this project most likely to get stuck?

Not "what's the next hard problem" — we have a queue for that.
Where is the project most likely to plateau in a way that's hard
to diagnose? What would a failure mode look like that we'd
mistake for slow progress?

### C4. What would falsify the whole direction?

The experiential-design docs name three falsification criteria:
1. Dreamer emotional dynamics invisible in any output modality
2. Creative briefs don't survive contact with rendering
3. Director is unnecessary

Are these the right criteria? Are there others? What experiment
would most efficiently test whether the direction is sound?

### C5. What should we be reading or studying that we aren't?

Given where the project sits in the design space (from Ask A),
what research traditions, existing systems, or theoretical
frameworks are we not drawing on that we should be? Not more
papers to add to the pile — specific things that would change
how we think about the project if we engaged with them.

## Context to include for Ask C

- This document (the Ask C section)
- Ask A reply
- Ask B reply
- `Notes/dashboard.md` (for the blind spots appendix)
- `pressure-engine-reframe/34-broader-application-surface.md`

## Preferred answer format for Ask C

Do not repeat Ask A or Ask B. This is not a synthesis of those
answers — it is a separate, harder question about what we might
be wrong about.

1. **Strongest version** — what this could be at its best,
   in one paragraph
2. **3 non-obvious conceptual risks** — each with:
   - an observable failure signature (what would it look like
     if this risk were materializing?)
   - the cheapest experiment that would expose it
3. **Plateau risks** — where we'd get stuck in a way that looks
   like slow progress instead of a wrong turn. Name the specific
   plateau, not a generic "things might be hard."
4. **Falsification** — are our three criteria the right ones?
   What's missing? What single experiment would most efficiently
   test whether the direction is sound?
5. **What to read** — 3-5 specific recommendations with one
   sentence each on why it would change how we think, not just
   add to what we know
