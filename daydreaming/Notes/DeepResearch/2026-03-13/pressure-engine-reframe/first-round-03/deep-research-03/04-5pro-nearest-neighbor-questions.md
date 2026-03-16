# 5 Pro: Nearest-Neighbor Source Analysis

Date: 2026-03-15

## How this works

Same as previous question banks: paste into 5 Pro (ChatGPT 5.4 Pro)
along with the referenced source extractions. RepoPrompt enriches
the request with relevant repo context. Save replies under
`deep-research-03/replies-02/`.

## Purpose

We recently identified our nearest neighbors in the design space
(Ask A reply) and produced extractions from six key sources:

- **18** — Loyall / Hap (believable agents)
- **19** — ABL (behavior language for interactive drama)
- **20** — FAtiMA Toolkit (socio-emotional agents)
- **21** — IPOCL / narrative planning (plot + character coherence)
- **22** — Sabre (centralized narrative planning with beliefs)
- **23** — MINSTREL follow-up (author-level creative control)

Plus two more landing (likely active inference and generative
agents). These are the systems closest to what we're building.

The extractions identify mechanisms and open questions. Now we
need 5 Pro to think harder about what these sources actually
mean for our architecture — not just "what can we import?" but
"what do they reveal about what we're doing right, what we're
doing wrong, and what we're missing?"

## Shared context

Our system's unique position (from Ask A + synthesis):

> Mueller gives us the cognitive process *between* interactions —
> rumination, avoidance, rehearsal, rationalization. FAtiMA and
> most affective architectures focus on action selection *during*
> social interaction. Our differentiator is the inner monologue
> when no one is watching.

Current architecture (from 36-current-synthesis.md):

- Authoring lane: Prep → L1 orchestration → L2 generation →
  graph compilation
- Runtime lane: authored graph → L3 traversal → Director
  feedback → adapter → Stage
- Missing seam: typed provocation proposal between world
  changes and reaction generation
- State split: FixtureDeltaV1 (external) / CarryForwardStateV1
  (internal cross-situation) / SituationLocalStateV1 (local)

---

## Ask D: What do our nearest neighbors tell us about our architecture?

### D1. Loyall/Hap + ABL vs. our operator/practice layer

Loyall's believable agents and ABL's behavior language are the
closest prior art for "characters with personality that feel
real in real time." Our system uses Mueller's operators +
Versu's social practices for a similar role.

Key questions:

- Loyall insists that **authored irrationality** (avoidance,
  self-protective reinterpretation) must be first-class, not
  error cases. Our operators already do this. But Loyall also
  insists on **concurrent goal pursuit with explicit conflicts**
  and **interrupt/continuation rules**. We don't have either.
  How much does that matter for our use case?

- ABL distinguishes **start conditions** from **continue
  conditions** for behaviors. Our operators have selection
  scoring but no explicit "when should this operator stop
  mid-execution?" rule. Is that a gap, or is it irrelevant
  because our operators produce single beats rather than
  extended runtime behaviors?

- ABL's **Active Behavior Tree** is an inspectable runtime
  execution state. Our system has trace output and sidecars
  but no equivalent of "here is what the character is currently
  executing and why." Does the inner-life visualization need
  something like an ABT, or is our current trace/sidecar
  approach sufficient?

- Loyall warns that **modularity can work against expression**
  — clean separation of concerns can prevent the kind of
  cross-cutting personality logic that makes characters feel
  real. Is our CausalSlice → AppraisalFrame → PracticeContext
  → operator pipeline too modular? Does the sequential
  decomposition lose something that a more holistic approach
  would preserve?

### D2. FAtiMA vs. our middle layer

FAtiMA is structurally closest to what we've built (appraisal →
emotion → coping → social action with explicit personality).

Key questions:

- FAtiMA uses **meta-beliefs** as a typed seam for foreign
  reasoners (search, retrieval, MCTS, theory-of-mind lookup).
  Our system has no equivalent — everything flows through the
  sequential CausalSlice → AppraisalFrame pipeline. Should we
  adopt a meta-belief seam for the Provocation Generator's
  reduced context? For retrieval? For external knowledge?

- FAtiMA has **integrated authoring tools with validation**
  (reachability checks, state visualization, simulator-driven
  testing). Our authoring tooling is batch generation + keeper
  bank + pack registry. What validation surfaces are we
  missing? Should the generation pipeline check graph
  reachability, dead branches, or rule-trigger coverage?

- FAtiMA separates **dialogue-state topology** from **agent
  choice within that topology**. Our system has situations
  (topology) and operators (choice) but no equivalent of a
  dialogue-state graph for conversation-level structure. For
  multi-character Graffito scenes with dialogue, does this
  matter?

- The key differentiator claim: **Mueller gives us the
  between-interactions cognitive process; FAtiMA gives
  action selection during interaction.** Is this actually true
  when you look at the implementations? Does FAtiMA have any
  mechanism for rumination, rehearsal, or avoidance when the
  character is not engaged in social action? Or is our claim
  genuinely novel?

### D3. Narrative planning (IPOCL + Sabre) vs. our traversal + graph compilation

Our L3 scheduler is locally excellent (Facade-shaped beat
selection) but doesn't plan forward. IPOCL and Sabre show what
principled multi-situation coherence looks like.

Key questions:

- IPOCL introduces **frames of commitment** — tracked answers
  to "what goal is this character pursuing, what caused it,
  which actions belong to it." Our system has concerns +
  operators but no explicit pursuit-thread object. Is a
  lightweight pursuit thread the right bridge between L2
  local concerns and L3 multi-situation coherence?

- Sabre judges character actions from the **character's
  believed state**, not world truth. Our generation pipeline
  conditions on the fixture's world state, which is authorial
  truth. Should generated material be conditioned on what the
  character *believes* rather than what *is*? For Kai, does
  it matter whether the letter actually contains what he fears,
  or only that he fears it?

- Sabre separates **action choice** from **automatic
  aftermath** (mandatory consequences that fire on
  observation). Our system has reappraisal after each step
  but no explicit observation/trigger mechanism. When Tessa
  rationalizes, does the world change? Does anyone observe it?
  Is the lack of an observation model a gap?

- IPOCL's **intentional completeness** rule: every non-
  accidental action belongs to at least one frame of
  commitment. Our generation pipeline produces beats that
  serve operators, but there's no validation that the beats
  form intentionally coherent sequences across situations.
  Is this needed for the patch test? For full graph assembly?

- The extraction identifies that IPOCL assumes frames end in
  success. But **blocked/failed/abandoned pursuits** are
  critical to our system's pressure dynamics. How do you
  represent pursuit failure in an IPOCL-like framework
  without breaking the planner?

### D4. MINSTREL's authoring control loop vs. our L1

MINSTREL's author-level control (propose → check → enhance)
with typed mutation heuristics is relevant to L1's orchestration
role.

Key questions:

- MINSTREL uses a three-part control shape: **Story Producers**
  (fill gaps), **Story Checkers** (find inconsistencies),
  **Story Enhancers** (add enrichment). Our L1 currently has
  generation + admission. Should L1 have an explicit checker
  phase that finds inconsistencies in the generated material
  before admission? An enhancer phase that adds enrichment
  (cross-references, setup/payoff links) after admission?

- MINSTREL's 24+ **typed mutation heuristics** (TRAM operators)
  are paired with their own reverse adaptation logic. Our
  generation pipeline treats the LLM as one big operator.
  Would smaller, typed generation moves (each with its own
  repair/adaptation path) produce better material than
  monolithic LLM generation?

- MINSTREL's search policy **materially changes output
  quality** — random selection is a poor default. Our
  candidate compilation currently uses soft scoring
  (specificity, legibility, distinctiveness, coverage). Is
  this scoring analogous to MINSTREL's search policy? Should
  it be more deliberate about which "authorial move" each
  candidate is attempting?

### D5. Cross-cutting: what are we actually missing?

Looking across all six sources:

- **Runtime execution state**: Loyall/ABL have explicit ABTs.
  FAtiMA has centralized state. Our system has traces/sidecars.
  Is there a gap between "we log what happened" and "we
  maintain a live inspectable execution state"? Does the
  inner-life visualization need the latter?

- **Belief and observation discipline**: Sabre has explicit
  observation functions and belief revision. Our system treats
  the world as authorial truth. For multi-character scenes,
  is this sustainable? When does theory-of-mind reasoning
  become load-bearing?

- **Pursuit continuity across situations**: IPOCL has frames of
  commitment. ABL has joint behaviors. Our system has concerns
  that carry intensity across situations but no explicit
  "this character is pursuing X across multiple scenes" object.
  Is the concern the right carrier for this, or do we need
  something new?

- **Verification and validation**: FAtiMA has authoring-time
  validation (reachability, dead branches). MINSTREL has
  checkers as first-class control. Our system validates graph
  projections but doesn't validate graph-level coherence
  (are all setup_refs eventually paid off? are there dead
  branches? is every situation reachable?). Is this a gap
  that matters now, or only at full graph scale?

### D6. Counterfactual preservation: should unrealized alternatives be traversable?

Mueller's DAYDREAMER explicitly preserves unrealized scenarios
as fantasy/daydreaming contexts — rehearsals that weren't enacted,
revenge fantasies that were imagined but not pursued, rationalizations
that were started and dropped. Most character systems only track
what actually happened. A system that preserves abandoned
possibilities as traversable memory is doing something
categorically different.

Our current implementation doesn't do this. Generated material
is either admitted to the graph or rejected. There's no
"what the character imagined but didn't do" layer.

Key questions:

- Should rejected/abandoned generation candidates be preserved
  as counterfactual memory that can influence future retrieval
  and operator selection? (e.g., "Tessa drafted a real apology
  in her head but the rationalization won — that abandoned
  apology is still in her episodic memory")

- Does the inner-life visualization need a counterfactual
  channel? Not just "what is happening" but "what almost
  happened" or "what the character is imagining happening"?
  Mueller's original system makes this distinction between
  reality context and fantasy/daydreaming context. Is that
  distinction load-bearing for the "watching a mind" experience?

- How does counterfactual preservation interact with the
  authoring membrane? If rejected candidates become
  counterfactual memory, the line between "curated canon"
  and "character inner life" gets blurry. Is that desirable
  or dangerous?

- Which of our nearest neighbors has the best model for
  preserving and using unrealized alternatives? MINSTREL
  has story alternatives. Sabre has character belief states
  that may differ from world truth. ABL has behaviors that
  were considered but not executed. Is any of these the right
  pattern?

---

### D7. The provocation seam: what should the missing object actually be?

Across these neighbors, several systems have a typed boundary
between internal progression and world-facing change:

- ABL has behavior activation / continuation structure
- FAtiMA has meta-beliefs and foreign-reasoner seams
- IPOCL / Sabre have explicit action and commitment structure
- MINSTREL has typed authorial intervention and checking

Our current working split is:

- `FixtureDeltaV1` = external world/situation change proposal
- `CarryForwardStateV1` = internal cross-situation residue
- `SituationLocalStateV1` = active-situation local state
- `ProvocationContextV1` = reduced internal summary given to the
  Provocation Generator

Key questions:

- Is this three-object split the right minimum seam, or are we
  still missing a fourth object such as a lightweight pursuit /
  commitment thread?

- Should `FixtureDeltaV1` look more like:
  - an authored event schema
  - a planner action / aftermath pair
  - a dialogue-state transition
  - an appraisal trigger
  - or something else entirely?

- What absolutely must be in `ProvocationContextV1` for
  non-generic world pushes, and what must stay out to avoid
  brittle prompt coupling?

- Which nearest neighbor gives the best pattern for
  **human-gated proposal ledgers** rather than autonomous world
  mutation?

- What is the smallest success condition for this seam? In
  other words: what would count as evidence that a typed
  provocation object is doing real architectural work rather
  than just adding another layer of bookkeeping?

---

## Context to include (paste into chat)

Essential:
- This document
- `reading-list/18-loyall-extraction.md`
- `reading-list/19-abl-extraction.md`
- `reading-list/20-fatima-extraction.md`
- `reading-list/21-narrative-planning-extraction.md`
- `reading-list/22-sabre-extraction.md`
- `reading-list/23-minstrel-followup.md`
- `36-current-synthesis.md` (current project state)

If context allows:
- `reading-list/24-generative-agents-extraction.md`
- `reading-list/25-character-is-destiny-extraction.md`
- `11-settled-architecture.md`
- `reading-list/17-nearest-neighbors-reading-list.md`
- `deep-research-03/replies-02/Ask-B-01.md`
- `deep-research-03/replies-02/Ask-B-02.md`

## Preferred answer format

For each question section (D1-D7):

1. **Direct answer** — what we should actually do, not just
   what's interesting
2. **What to import** — specific mechanisms worth adopting,
   with the minimum viable version
3. **What to skip** — mechanisms that are interesting but wrong
   for our use case, with why
4. **What changes our architecture** — if adopting this
   mechanism would require a structural change, name it
5. **Priority** — is this needed before the next phase
   (provocation experiment + watchable run), or is it a
   later concern?
