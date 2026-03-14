# Prior Work Questions for GPT-5 Pro

This note is a focused question pack for a broader problem behind the
pressure-engine reframe:

> What relevant prior work already exists that we could appropriate,
> adapt, or at least learn from, so we stop inventing every abstraction
> from scratch?

The point is not to ask for a random literature review.
The point is to identify existing bodies of work that could give us:

- better grounding
- better vocabularies
- better operator taxonomies
- better evaluation criteria
- better workflow models
- better implementation patterns

---

## Why this note exists

Right now a lot of the architecture is being improvised from a mix of:

- Mueller / DAYDREAMER
- project-specific experience design
- game-design instincts
- stage/runtime constraints
- whatever seems plausible in conversation

That is productive up to a point.

But there are at least three risks:

1. We reinvent something that already exists in a stronger form.
2. We use weak vocabulary where a mature field already has better terms.
3. We overgeneralize from Mueller when another adjacent body of work
   would actually fit part of the problem better.

This note is meant to surface reusable intellectual infrastructure.

### What we already know about (do not waste time re-explaining)

- Mueller's DAYDREAMER (1990) — we have the book, implemented ~40%
  of the system in Clojure, and understand the control geometry.
- The Sims needs model — we know it exists and have referenced it.
- Basic narrative structure (Aristotle, Freytag, three-act) — we
  know the shapes. We need operational models, not the shapes.

### What we suspect exists but haven't identified

- Formal theories of directing moves or editorial sequencing
- Computable models of narrative tension/resolution (beyond
  intuitive beat sheets)
- Evaluated co-creative authoring systems with real feedback loops
- Agent architectures specifically designed for emergent narrative
  (beyond general BDI)
- Tension/resolution models from music theory that could apply to
  pacing and conducted performance

### What level of appropriation we're asking about

For each body of work, tell us which of these we should steal:

- **Theory** — the conceptual framework changes how we think
- **Vocabulary** — better names for things we're already building
- **Data structures** — representation patterns we should borrow
- **Algorithms** — specific mechanisms we should implement
- **Evaluation criteria** — how to know if our output is good
- **Failure modes** — known pitfalls from their experience
- **Nothing right now** — interesting but not actionable yet

---

## What to include with the request

At minimum, send GPT-5 Pro these docs:

- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/01-reframe-summary.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/02-state-model.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/06-world-building-pressure-questions-for-5pro.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/07-l3-traversal-questions-for-5pro.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/05-stage-integration.md`
- `daydreaming/Notes/experiential-design/17-game-engine-architecture.md`
- `daydreaming/Notes/experiential-design/14-operating-map.md`

Optional but useful:

- `daydreaming/Notes/experiential-design/20-narration-layer.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/re-evaluation/dr/deep-research-report (4).md`
- `daydreaming/Notes/DeepResearch/2026-03-13/re-evaluation/dr/deep-research-report (5).md`

---

## Framing for GPT-5 Pro

Please do not answer with a broad, undifferentiated literature survey.

We want a highly selective scan of prior work that is directly relevant
to the architecture under discussion.

Be specific about:

- what part of the architecture a body of work informs
- whether it is genuinely useful or only superficially similar
- whether it provides theory, vocabulary, evaluation criteria,
  implementation ideas, or just inspiration
- whether it should influence the shipping path now or remain background
  research

We would rather get 6-10 highly relevant bodies of work than 30 shallow
mentions.

If something is famous but not actually useful here, say so.

---

## Core Questions

### 1. What bodies of work are most relevant to each level?

Please identify the strongest prior work for:

- L1 world-building / authoring-time expansion
- L2 motivated characters / inner life / concern-driven cognition
- L3 traversal / directing / sequencing
- stage/control integration
- human-in-the-loop co-creative workflow

For each, say:

- what the body of work is
- what problem it solves
- which part of our architecture it actually maps to
- whether it is bedrock, optional input, or mostly a distraction

### 2. What should ground the world-building level?

For the world-building pressure problem, what existing work is most
useful?

Possible candidates:

- interactive narrative authoring systems
- procedural content generation (WaveFunctionCollapse, constraint
  propagation for level design, etc.)
- game world design frameworks (Dwarf Fortress's history generation,
  Caves of Qud's procedural worldbuilding)
- constraint-based story/world generation
- co-creative writing systems (Wordcraft, etc.)
- planning-based creativity systems (MINSTREL's transform-recall-
  adapt methods)
- mixed-initiative design tools (Tanagra, Sentient Sketchbook)

Question:

What should we actually look at here, and why?

Please distinguish between:

- work that helps define computable world-building pressures
  (how do you detect "this world is thin here"?)
- work that helps define operators
  (what moves does the system make to enrich the world?)
- work that helps define evaluation / curation workflows
  (how do you know a proposal improved the world?)

### 3. What should ground the L2 character level besides Mueller?

Mueller is clearly central, but he may not be sufficient by himself.

Question:

What adjacent work should ground or enrich the character level?

Possible areas:

- appraisal theories of emotion (Scherer's component process model,
  Lazarus, Smith & Ellsworth — these define how agents evaluate
  events against concerns, which is exactly our pressure detection)
- OCC emotion model (Ortony, Clore, Collins 1988 — the standard
  computational emotion model, Mueller draws from this lineage)
- BDI architecture (Bratman, Rao & Georgeff — Belief-Desire-
  Intention, the mainstream AI agent model)
- social simulation (Prom Week, Versu / Richard Evans — agents
  with social practices and dispositions)
- memory models (spreading activation, dual-process theory)
- narrative character intentionality (Riedl & Young's IPOCL/
  Fabulist — planning that respects character believability)

Please tell us which of these are genuinely useful and which are likely
to overcomplicate the system without helping. We are especially
interested in whether Scherer's appraisal theory could provide a
better "concern detection" model than what we currently have.

### 4. What should ground the L3 traversal level?

This is the most ambiguous level. Our L3 operators (build, release,
confront, shift, recall, juxtapose, dwell) are dramaturgical
intuitions, not grounded in studied theory.

Possible bodies of work:

- story grammars (Rumelhart 1975, Mandler & Johnson 1977)
- computational narrative planning (IPOCL, Fabulist)
- drama management (Façade / Mateas & Stern — real-time dramatic
  arc management with a drama manager that monitors values and
  intervenes)
- dramaturgical theory (Aristotle's Poetics, but operationalized)
- directing pedagogy (is there a formal theory of directing moves?)
- editing theory (Walter Murch's "In the Blink of an Eye")
- montage theory (Eisenstein, Kuleshov — juxtaposition as meaning)
- beat-sheet / screenwriting traditions (McKee's "story values" —
  a scene works when it turns a value from positive to negative
  or vice versa)
- tension/resolution models from music theory (Lerdahl's GTTM,
  Narmour's implication-realization — mathematical models of
  tension that could ground pacing and the conducted performance)

Question:

Which of these should actually ground the L3 operator and state model,
and for what subproblems?

Please distinguish between:

- causal/dramatic structure (why scenes connect causally)
- sequencing and pacing (when to move, hold, accelerate, breathe)
- juxtaposition and recall (why placing two things next to each
  other creates meaning)
- audience-facing shape vs character-facing logic (the director
  serves the audience, not the characters)
- tension models that could apply to both music and visual
  sequencing (relevant because this is a conducted performance
  with parametric music via Lyria)

### 5. Are there existing evaluation frameworks we should steal?

A huge gap across the architecture is evaluation:

- did a world-building proposal actually improve the world?
- did a character-level exploration actually reduce or transform a
  concern?
- did a traversal sequence actually become more compelling?

Question:

What existing frameworks give us useful evaluation concepts or metrics?

These could come from:

- computational creativity
- narrative intelligence
- game AI
- procedural generation research
- dramaturgical analysis

We are especially interested in frameworks that are structured enough to
be operationalized, not just critical vocabulary.

### 6. Are there representation patterns we should appropriate?

Across the reframe we are inventing data structures like:

- `Concern`
- `Context`
- `Proposal`
- `CommitRecord`
- `DreamDirective`

Question:

What existing representation patterns should we borrow instead of
inventing ad hoc?

Examples:

- planning state / partial-order planning structures
- blackboard architectures
- behavior trees
- production-rule systems
- graph + annotation hybrids
- event-sourcing / provenance systems
- cognitive architectures with episodic memory

We do not need to adopt these wholesale, but we want to know what they
could teach us.

### 7. Are there co-creative workflow models we should look at?

Part of the project is not full automation. It is an interactive
human-machine workflow.

Question:

What prior work is most relevant to:

- human curation of generated proposals
- interactive world-building
- author steering of intrinsic-drive systems
- mixed-initiative creative tools

Please focus on work that helps define:

- where the human intervenes
- how proposals are surfaced
- how rejection/edit/accept cycles work
- how the system remembers human preference signals

### 8. Are there adjacent domains outside narrative we should steal from?

Some relevant work may come from outside obvious story research.

This project has a dimension most narrative AI systems lack: it is
a **conducted live performance instrument**. A human performer uses
an APC Mini controller to steer the system in real time. The output
includes parametric music (Lyria) and real-time text-to-video (Scope).
This is closer to live improvised music than to offline story
generation.

Question:

Are there adjacent domains that could provide stronger abstractions than
straight narrative theory?

Possible domains:

- game design and procedural generation
- simulation and agent-based modeling
- planning and scheduling
- recommender systems / retrieval systems
- improvisation systems (George Lewis's Voyager — an autonomous
  improvising system that responds to a human musician; live coding
  systems like Tidal/Strudel)
- music sequencing / composition systems (generative music,
  algorithmic composition, constraint-based music generation)
- tension/resolution in music (Lerdahl's Generative Theory of
  Tonal Music, Narmour's implication-realization — these are
  mathematical models of expectation and tension that might ground
  the pacing and energy dimensions of traversal)
- cybernetic art (Gordon Pask, Roy Ascott — responsive systems
  with feedback loops and participant interaction)
- HCI for mixed-initiative tools

If so, say what concept we should steal and where it would fit.

We are especially interested in work from live performance systems
where an autonomous system and a human performer must negotiate
control in real time — that is exactly the conducted dimension of
this project.

### 9. What prior work is tempting but probably a distraction?

This is just as important.

Question:

What bodies of work are likely to be seductive but wrong for this
project right now?

For example:

- too abstract to operationalize
- too heavyweight for the current phase
- optimized for full story generation rather than bounded world-building
  or traversal
- likely to produce taxonomy bloat without helping

Please say what to ignore or postpone.

### 10. If you were building a "reading list with purpose," what would it be?

Please give a short, prioritized list of the most relevant prior work
to study next.

For each item:

- what it is (title, author, year)
- which architectural hole it helps with (L1, L2, L3, conducted
  performance, evaluation, co-creative workflow)
- whether it is must-read, useful, or optional
- what concrete question it might help answer
- what level of appropriation it enables (theory, vocabulary, data
  structures, algorithms, evaluation criteria — see the list above)

We do not want a prestige list.
We want a "this actually helps us build or think better" list.

Cap it at 10-15 items. If something is frequently cited but not
actually helpful for THIS project, leave it off and say why in Q9.

---

## Preferred answer shape

Ask GPT-5 Pro to answer in this format:

1. **Top-line judgment** — where prior work could most improve the
   architecture
2. **Best bodies of work by architectural layer**
3. **What to steal conceptually**
4. **What to steal operationally**
5. **What to ignore for now**
6. **Short prioritized reading list**

If useful, also ask for a compact table like:

| Body of work | Architectural hole | What to steal | Why it matters now |

---

## Why this note matters

The project is strongest when it stops pretending every abstraction must
be invented locally.

If there is already:

- a better vocabulary for L3
- a better model of mixed-initiative authoring
- a better way to think about world incompleteness pressures
- a better representation for provenance or evaluation

then finding that is part of the work.

That is not avoiding original thinking.
That is making the original thinking less sloppy.
