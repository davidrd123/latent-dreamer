# Nearest Neighbors Reading List

Date: 2026-03-15

Source: 5 Pro Ask A reply — alternative foundations for
steerable cognitive character engines.

## Purpose

The original reading list (docs 01-12) covered the sources we
built on: Facade, DODM, EMA, OCC, Versu, Mueller, etc. This
list covers the sources we should have been citing — the nearest
neighbors in the design space that we arrived at independently.

Priority is: believable-agent lineage first (closest prior art),
then narrative planning (needed for multi-situation), then
everything else as reference.

---

## Priority 1: Believable agents / affective architectures

These are the closest alternative foundations for "computational
characters with legible inner life." If someone else were
building what we're building without our specific lineage, this
is where 5 Pro says they'd start.

### Loyall — Believable Agents: Building Interactive Personalities
- **What:** PhD thesis on interactive personality through the
  Oz/Hap architecture. The foundational work on what makes a
  computational character feel like a person.
- **Why it matters:** Directly addresses our core question —
  how to make inner life legible and interactive. May have
  solved problems we're re-deriving.
- **Status:** [x] Extraction exists (18-loyall-extraction.md)
- **URL:** https://www.cs.cmu.edu/Groups/oz/papers/CMU-CS-97-123.pdf

### Mateas & Stern — A Behavior Language for Story-Based Believable Agents (ABL)
- **What:** Reactive planning language for believable agents in
  interactive drama. Used in Facade.
- **Why it matters:** We already use Facade's scheduler shape.
  ABL is the character-behavior layer underneath it. We may be
  reinventing parts of this.
- **Status:** [x] Extraction exists (19-abl-extraction.md)
- **URL:** https://users.soe.ucsc.edu/~michaelm/publications/mateas-aaai-symp-aiide-2002.pdf

### FAtiMA Toolkit
- **What:** Modular socio-emotional agent stack. Appraisal →
  emotion → coping → social action, with explicit personality.
  Used in social robots and interactive narratives.
- **Why it matters:** Structurally close to our system (appraisal
  + coping + social practice). The key question: what does FAtiMA
  do that we don't, and vice versa? Our hypothesis: Mueller gives
  us the *between-interactions* cognitive process (daydreaming,
  rumination, avoidance) that FAtiMA doesn't focus on.
- **Status:** [x] Extraction exists (20-fatima-extraction.md)
- **URL:** https://arxiv.org/abs/2103.03020

---

## Priority 2: Narrative planning

Needed for multi-situation arc coherence. The current scheduler
is locally excellent but doesn't plan forward. If Q5/Q7/Q12
aren't enough for long-horizon arcs, this tradition has the
mechanisms.

### Riedl & Young — Narrative Planning: Balancing Plot and Character
- **What:** The pivot paper on planning that satisfies both plot
  coherence and character intentionality.
- **Why it matters:** Setup/payoff discipline, multi-situation
  coherence, explicit failed-plan reasoning. The Q6 POCL-lite
  sketcher is a nod toward this tradition.
- **Status:** [x] Extraction exists (21-narrative-planning-extraction.md)
- **URL:** https://faculty.cc.gatech.edu/~riedl/pubs/jair.pdf

### Sabre (2021)
- **What:** Centralized "puppetmaster" planner that constrains
  every action to make sense under each character's beliefs and
  intentions. Explicit author goals + character intentionality.
- **Why it matters:** Shows what "principled multi-situation
  coherence" looks like as an architecture. May inform graph
  compilation / the soft-constraint compiler.
- **Status:** [x] Extraction exists (22-sabre-extraction.md)
- **URL:** https://cdn.aaai.org/ojs/18896/18896-52-22662-1-2-20211004.pdf

### MINSTREL (Turner)
- **What:** Case-based analogical transformation for story
  generation. Already in our reading list but worth revisiting
  through the narrative-planning lens.
- **Why it matters:** Shows how to keep story generation from
  collapsing into flat planning — through analogical
  transformation rather than search.
- **Status:** [x] Extraction exists (09-minstrel-extraction.md; 23-minstrel-followup.md)
- **URL:** https://www.cal.cs.ucla.edu/tech-report/1992-reports/920057.pdf

---

## Priority 3: Active inference / predictive processing

Not actionable now but relevant for the daemon/companion vision
(doc 34). If the project leans toward persistent agents with
uncertainty and information-seeking, this is the substrate.

### pymdp + Friston et al.
- **What:** Python library for active inference in discrete state
  spaces. The practical entry point for building PP-based agents.
- **Why it matters:** "Concern" becomes persistent prediction
  error. "Attention" becomes precision allocation. Elegant but
  loses named authorable coping modes. Relevant for doc 34's
  research daemon.
- **Status:** [ ] Not yet read
- **URL:** https://arxiv.org/abs/2201.03904

### Mahault et al. 2026 — Empathy in active-inference agents
- **What:** Multi-agent active inference with perspective-taking
  and alignment.
- **Why it matters:** Pushes PP toward social cognition, which
  connects to multi-character conducted performance.
- **Status:** [ ] Not yet read
- **URL:** https://arxiv.org/abs/2602.20936

---

## Priority 4: LLM-as-cognitive-engine (know the competition)

### Generative Agents (Park et al.)
- **What:** Memory + reflection + planning over a simulated town.
  The canonical "LLM as the mind" paper.
- **Why it matters:** This is the dominant modern baseline. Our
  system's differentiator is explicit causal inner process vs.
  prompt-state that "masquerades as cognition without constraining
  anything durable" (5 Pro's assessment).
- **Status:** [x] Extraction exists (24-generative-agents-extraction.md)
- **URL:** https://arxiv.org/abs/2304.03442

### Character is Destiny + Thinking in Character
- **What:** Role-aware reasoning for LLM characters. Moves from
  style mimicry toward internal role-aware reasoning.
- **Why it matters:** Shows the field trying to solve the problem
  we're solving from the LLM-first direction. Where they end up
  may look like where we started.
- **Status:** [ ] Partial: Character is Destiny extracted
  (25-character-is-destiny-extraction.md); Thinking in Character
  still missing
- **URL:** https://arxiv.org/abs/2404.12138

---

## Priority 5: Reference (not urgent)

### Rao & Georgeff — BDI Agents: From Theory to Practice
- **What:** The classic BDI formulation. Beliefs, desires,
  intentions, plan libraries.
- **Why it matters:** "An excellent outer skeleton. A mediocre
  standalone explanation of inner life." Good to know what BDI
  gives and doesn't give.
- **Status:** [ ] Not yet read
- **URL:** https://cdn.aaai.org/ICMAS/1995/ICMAS95-042.pdf

### Orkin — Three States and a Plan (F.E.A.R.)
- **What:** GOAP in game AI. Planning without huge FSM spaghetti.
- **Why it matters:** Shows what embodied/game AI action selection
  looks like. Our system is further from this tradition than from
  the believable-agent tradition.
- **Status:** [ ] Not yet read
- **URL:** https://www.gamedevs.org/uploads/three-states-plan-ai-of-fear.pdf

### DreamerV3
- **What:** Learned world models + behavior improvement by
  imagining future scenarios.
- **Why it matters:** "If you cite only one modern 'this is what
  learned world-model control looks like' paper, cite DreamerV3."
  Relevant if RL-based tuning becomes a consideration.
- **Status:** [ ] Not yet read
- **URL:** https://arxiv.org/abs/1705.05363

---

## Key insight from 5 Pro

Our system's unique position in the design space is **the
daydreaming itself** — the cognitive process that happens when
the character is alone with their concerns. Not reaction selection
(FAtiMA), not plot planning (Sabre), not prompt coherence
(Generative Agents). The inner monologue of avoidance, the
rehearsal of what you'll say, the rationalization that rewrites
what you did. No other architecture makes that process first-class,
inspectable, and steerable.

The nearest comparison (FAtiMA) focuses on action selection
*during* social interaction. Our system focuses on the cognitive
process *between* interactions. That's the differentiator to
protect.
