# Session Log: 2026-03-14 — Pressure Engine Reframe

## Duration & Scope

Marathon session covering architectural reframe, four GPT-5 Pro
review rounds, Mueller book deep-dive, visual world-building
pipeline, and settled architecture document.

---

## 1. Starting Point

Compared three sources:
- The existing Clojure kernel (~5,800 lines, 85 tests, 40% Mueller
  coverage, three benchmarks)
- A ChatGPT (IMS+Daydream) deep-dive that proposed a "pressure-
  driven hypothetical expansion policy" from reading Mueller's
  original paper
- The 5 Pro architecture review (reply.md) from an earlier session

The ChatGPT conversation proposed:
- Concern/Context/SceneNode/CommitRecord as core entities
- A three-loop decomposition (canonical world / hypothetical
  expansion / performance)
- Operators (revenge, reversal, rehearsal, etc.) as typed moves,
  not labels
- Serendipity as post-hoc concern scan
- Ontic/policy/salience commit distinction

Key finding: the kernel already avoids most of ChatGPT's predicted
"wrong turns" (flat graph, text-blob nodes, generic generation,
hypotheticals mutating canon, pure semantic retrieval, unbounded
daydreaming).

## 2. The Reframe Conversation

### Director as Dreamer
Reframed: the Dreamer IS the Director. The director's concerns
are dramaturgical (pacing, tension, earned climax, avoidance),
not psychological. The operators become directing verbs (build,
release, confront, shift, recall, juxtapose, dwell).

### Three-Level Architecture (initial proposal)
Same loop at three levels:
- L1: World-building / ideation (creative pressures)
- L2: Character inner life (psychological pressures — Mueller's
  native domain)
- L3: Director traversal (dramaturgical pressures)

### Engine as Authoring Tool
The bottleneck is world richness, not engine sophistication.
Instead of hand-authoring 50+ nodes, use the engine to explore
from compressed initial state. Human curates. Graph emerges from
motivated exploration rather than being hand-authored.

### Characters with Intrinsic Motivation
Like The Sims but with genuine Daydreamer-style concerns, goals,
emotional reactions. Characters generate events because they want
things, not because the author scripted them.

## 3. Documents Created

### pressure-engine-reframe/ directory (10+ docs)

- `01-reframe-summary.md` — Full reframe (revised multiple times)
- `02-state-model.md` — Concern/Context/Proposal/CommitRecord
  schemas (revised to split pressure types by level)
- `03-questions-for-5pro.md` — Architectural review pack (10 Qs)
- `04-kernel-gap-analysis.md` — Module-by-module kernel mapping
  (revised: adapters ≠ operators)
- `05-stage-integration.md` — DreamDirective schema, scope-drd
  integration points, commit policy options, V1→V2→V3 roadmap
  (distilled from deep-research-report (4))
- `06-world-building-pressure-questions-for-5pro.md` — Focused
  L1 question pack (written by codex)
- `07-l3-traversal-questions-for-5pro.md` — Focused L3 question
  pack (written by codex)
- `08-prior-work-questions-for-5pro.md` — Prior work scan
  (enhanced with specific systems, conducted performance dimension)
- `09-follow-on-requests-for-5pro.md` — Three follow-on prompts:
  prior work → boundary adjudication → experiment design
- `10-dmpost-multimodal-worldbuilding-sidecar.md` — DMPOST
  toolchain as visual world-building sidecar (written by codex)
- `11-settled-architecture.md` — Canonical reference document
  superseding all working drafts

### reading-list/00-reading-list.md
Prioritized reading list with 15 external papers + 4 Mueller
book sections. Must-reads: Facade, DODM, Authorial Leverage,
EMA, Versu, Sentient Sketchbook, Tanagra, ATMS.

## 4. GPT-5 Pro Responses (4 rounds)

### 03 — Architectural Review (early draft)
**Verdict:** "Good upstream, wrong downstream."
- L1/L2 can share one pressure-driven engine
- L3 is NOT that engine — it's a traversal controller
- Seven category errors identified (false symmetry, Concern
  overloaded, operators/adapters conflated, "graph is not hand-
  authored" is wrong, CommitRecord doesn't fit all levels, don't
  overload family, hidden schedule contradiction)
- Proposed two-lane architecture (research/authoring + shipping/
  performance)
- Falsifiable experiment: tiny TraversalPressure controller with
  4 pressures, compare 3 sequences (authored baseline, weighted
  random, pressure controller)

### 06 — World-Building Pressures
**Verdict:** "Narrow yes, broad no."
- Viable as upstream authoring assistant / world-state linter
- NOT viable as general autonomous world-building engine
- 10 candidate pressure types ranked: 6 strong (goal_without_
  resistance, conflict_without_response, setup_without_payoff,
  place_without_role, charged_object_without_function,
  disconnected_subgraph), 1 maybe, 2 do-not-schedule
- Revised operator taxonomy: obstruct, route, concretize, couple,
  consequence (historicize is a trap — decorative lore)
- "A proposal must be a typed world-state diff, not a prose
  fragment. Score it zero if it only adds interesting detail."
- Minimum world state schema: 6 entity types with specific
  required fields (blockers, response_paths, setup/payoff links)
- Concrete experiment using City Night Pursuit brief with 4
  intentional defects, compare against one-shot prompting

### 07 — L3 Traversal
**Verdict:** "L3 is not the same engine as L1/L2."
- L3 "concerns" are NOT concerns — they're diagnostics computed
  from traversal history
- 7 operators collapse to 5 intents: dwell, shift, recall,
  escalate, release (build = escalate, confront = targeted
  escalate, juxtapose = targeted shift or authoring-time pattern)
- Conductor sits ABOVE L3 as biasing authority, not parallel
  chooser. One control plane: conductor biases → scheduler scores
  → one decision → stage executes
- Three modes: autonomous, assisted, forced
- Compare against current graph-weighted traverser (not random —
  random is a fake baseline)
- Ephemeral lookahead (LookaheadPath), NOT persistent contexts

### 03-06-07 Synthesis
**Architecture in one sentence:** "A graph-first performance stack
with a real L2 exploration engine and two research extensions."

Final glossary settled:
- ArtifactDeficiency (L1), CharacterConcern (L2), TraversalSignal/
  SequenceDebt (L3), TraversalIntent, TraversalDecision,
  SelectionRecord, AuthoringCandidate, TraversalVerb, Operator
  (L1/L2 only), Adapter (translator, not operator), CommitRecord
  (L2 + L3 event commits only)

Three ordered experiments:
1. L3 scheduler test (shipping lane, first)
2. L1 authoring critic test (research lane, second)
3. L2 internal refactor test (research lane, third)

### 08 — Prior Work Scan
**Verdict:** "Steal from game AI and interactive music, not from
generic story theory."

By level:
- L1: Mixed-initiative PCG (Sentient Sketchbook, Tanagra, MINSTREL)
- L2: Computational appraisal + social simulation (EMA, Versu,
  CiF/Prom Week) — NOT generic BDI
- L3: Drama management (Facade, DODM, Authorial Leverage) — NOT
  story grammars
- Conducted performance: Voyager, TidalCycles/Strudel, IMS

What to ignore: BDI, story grammars, GraphRAG, ontology-heavy
narratology, full narrative planning (IPOCL), end-to-end LLM
story generation

Representation patterns: ATMS for hypothetical branches, event
sourcing for canon, blackboard for arbitration

Evaluation: authorial leverage + expressive range + watched-output
judgment (three-legged stool)

## 5. Earlier Brainstorm Docs (PreviousIdeation/video-conductor/)

Rediscovered four docs from the first Daydream hackathon:
- conductor-landscape.md — integration map, conductor state vector,
  agent contracts, tidal oscillators, Soundpainting/Conduction refs
- narrative-conducting.md — authoring/DJing/conducting distinction,
  brief as score, phenomenology of selection, Loom as instrument
- generative-steering.md — mapping across video/text/audio, style
  seeding as orchestration, multi-timescale control
- instrument-stack-architecture.md — five-part architecture
  (Score/Conductor/Ensemble/Stage/Log), conductor state vector spec,
  budgets and contracts, implementation stages

Key contributions not in the 5 Pro responses:
- Multi-timescale structure (fast/medium/slow/session loops)
- Tidal oscillators (slow arcs creating inevitability)
- Conductor state vector (energy, tension, density, clarity,
  novelty, tempo, phase, hold, focus)
- "Preparation as the actual work" — prep/performance separation

These mapped directly to the settled architecture:
- Conductor state vector → APC Mini faders/knobs
- Tidal oscillators → L3 slow-loop autonomous behavior
- Prep/performance → two-lane model
- "Constraints tight enough to make interpretation meaningful" →
  authored graph + traversal scheduler

## 6. Mueller Book Deep-Dive

Revisited chapters 5 and 11 of "Daydreaming in Humans and
Machines" (1990) with fresh eyes after the 5 Pro reviews.

### Serendipity (Ch. 5, §5.3) — Much richer than implemented
Mueller has 6 types (not just scalar bias):
- Episode-driven, object-driven, rule-induction-driven,
  concern-activation-driven, input-state-driven, mutation-driven
- The kernel's serendipity-bias (0.0-0.35) is a placeholder for
  rule-intersection graph search through planning rules
- Codex2 correction: the taxonomy is broader than my initial
  4-type summary

### Overdetermination and DAYDREAMER* (Ch. 11, §11.3)
- Stream of consciousness from merged products of many concurrent
  nonconscious processes, not serial switching between concerns
- DAYDREAMER* proposes split/merge architecture for concurrent
  concern-processes with voting, merging, pruning
- Codex2 correction: this is an architecture proposal, not just
  "prefer overdetermined nodes" — significantly more ambitious
  than a scoring bonus

### Art and Music (Ch. 11, §11.1.5)
- Mueller directly anticipated the project: "DAYDREAMER could
  provide initial ideas for stories, animations, or live-action
  films"
- Artist as curator, program as art object, program as artist's
  apprentice or proxy
- Questions about computational art, authorship distribution,
  derivative works

### Action Mutation (Ch. 5, §5.4)
- Specifically counter-search-reduction, not general creativity
- Fires when search has narrowed too much and system is stuck
- Codex2 correction: targeted escape mechanism, not "be more
  creative" knob

All four added to reading list as M1-M4.

## 7. Visual World-Building Pipeline (Codex)

Codex built a complete visual world-building pipeline while the
architecture review was happening:

### Core pipeline
- `brief_bridge.py` — parses brief markdown, extracts YAML blocks,
  derives world pack
- `world_pack.py` — loads pack, runs anchors, evaluates candidates,
  reports status
- `manifest.py` — anchor lifecycle (candidate → review_pending →
  approved/rejected/deferred), history tracking, schema migration
- `service.py` — image generation (Gemini), analysis, comparison,
  segmentation (SAM3/Replicate)
- `anchor_spec.py` — structured anchor specifications with
  entity type, visual/negative constraints, consistency notes
- `cli.py` — full CLI with subcommands

### Commands
- `pack-from-brief` — brief.md → world pack YAML
- `pack-run` — generate images from pack, append to manifest
- `pack-status` — compare pack against manifest
- `pack-evaluate` — compare candidates with structured scoring
- `anchor` — generate candidate visual anchors
- `anchor-refine` — edit existing anchor with targeted prompt
- `review` — approve/reject/defer with reason codes
- `compare` — rank multiple candidates with rubric scoring
- `analyze` — multimodal media analysis
- `segment` — SAM3 segmentation
- `edit` — image generation/editing

### Key design decisions
- Structured evaluation criteria (match, identityFit, paletteFit,
  constraintViolations, keepOrRework) — not "does this look good?"
- Scoring rubric with explicit weights (brief/identity 40, palette/
  style 25, constraints 25, composition 10)
- Review reason codes (identity_mismatch, palette_mismatch,
  constraint_violation, composition_weak, low_quality, duplicate,
  needs_refinement, keep_for_later, off_brief)
- Skip-existing prevents regeneration waste
- Manifest as truth — all state tracked in one JSON file

### Tested against real briefs
- City Night Pursuit: 4 characters, 8 locations, 6 situation
  moodboards generated
- Arctic Expedition and Stalker Zone: situation-only packs
- Actual images generated (bridge_001.png, alley-night_001.png)

### Character enrichment (later pass)
- Pulls detail from situation descriptions and charged objects
- Role-specific defaults for thin characters
- Prompt shaping cleaned up
- Unit tests added (test_daydream_vision.py)

### Spot check assessment
- Architecture is sound and well-structured
- Manifest lifecycle is well-designed with proper state machine
- Evaluation uses structured criteria, not vibes
- Main gaps: comparison prompts could lean harder on constraints,
  pack-evaluate doesn't filter deferred/rejected candidates
- Human review remains the actual gate (correct v1 choice)

### Codex priority queue
1. Tests (done — added in character enrichment pass)
2. Strengthen compare prompt and structured output
3. Then broader character enrichment

## 8. DMPOST31 Exploration

Explored David's existing multimodal toolchain in
/Users/daviddickinson/Projects/LLM/DMPOST31:

### Three MCP servers
- Illustrator MCP (13 ops) — shapes, paths, text, styles,
  per-element export with manifest
- After Effects MCP (21 ops) — comps, layers, keyframes,
  expressions, effects, track mattes, export frames
- Gemini Vision MCP — nano_edit_image, nano_analyze_media

### Proven pipeline
Illustrator → per-element PNG + manifest → AE import → rigging →
export. Monty Python cutout proof of concept (Gilliam-style).

### What's reusable for latent-dreamer
- Gemini Vision MCP (immediate)
- Manifest/artifact tracking pattern
- Handle system (persistent identity across operations)
- Orchestrator pattern (coordinate multi-MCP calls)

### Multimodal world-building vision
Visual anchors serve simultaneously as: design reference, LoRA
training data, prompting vocabulary, consistency anchors. Pipeline:
text sketch → image generation → characters in locations → caption
→ train LoRA → reprompt with known vocabulary.

## 9. Settled Architecture (doc 11)

The canonical reference document. Supersedes docs 01-10.

### Architecture
Two lanes (research/authoring + shipping/performance) with shared
infrastructure (episodic retrieval, pressure scheduling, trace/
provenance, commit discipline).

### What's settled
- Two-lane architecture
- L2 is the real engine core (Mueller-derived, proven)
- L3 is a traversal scheduler (drama management, not cognition)
- L1 is a research-stage authoring assistant (narrow, falsifiable)
- Operators and adapters are separate categories
- Graph is human-authored (shipping) with engine-assisted research
- CommitRecord taxonomy is level-specific
- Stage seam stays thin (DreamDirective with traversal_intent)
- Conductor is biasing authority with one control plane
- Conductor state vector with multi-timescale structure + tides

### What's open
- Whether L3 scheduling beats simpler traversal (experiment 1)
- Whether L1 deficiency-driven authoring beats prompting (exp 2)
- Conductor state vector in practice for APC Mini
- Tidal oscillator interaction with L3 diagnostics
- Multimodal world-building priority
- Prior work reading's impact on implementations
- Cross-level pressure propagation
- Evaluation framework details

## 10. Memory Updates

### New memory files
- `project_pressure_engine_reframe.md` — reframe direction,
  two lanes, 5 Pro reviewed
- `project_multimodal_toolchain.md` — existing MCPs, future
  vision for visual world-building + LoRA training

### Updated
- `MEMORY.md` — added pointers to both new memory files

## 11. Key Decisions Made

1. **Two lanes, not one engine.** Research/authoring (L1/L2) and
   shipping/performance (L3 + stage). Shared control geometry
   but not ontology.

2. **L3 is drama management.** Not context-forking cognition.
   Diagnostics not concerns. Intents not operators. Facade/DODM
   not Mueller as the grounding.

3. **L1 stays narrow.** Computable structural deficiencies only.
   "Authoring critic/linter plus proposal workflow" until it
   beats one-shot prompting in A/B test.

4. **Adapters are translators, not operators.** The code already
   enforces this. The docs now match.

5. **Graph is hand-authored for shipping.** Engine-assisted
   authoring is research. "The graph is not a hand-authored
   asset" was wrong for the current project.

6. **Companion/inner-life view is potentially a primary product.**
   Not just a debug tool.

7. **Visual world-building is a real sidecar.** DMPOST toolchain
   is available. Pipeline works. But it's research lane, not
   shipping lane.

8. **Three ordered experiments.** L3 scheduler test first (shipping
   lane), L1 authoring critic second, L2 refactor third.

9. **Prior work grounding by level.** Game AI + interactive music,
   not generic story theory. Specific papers identified and
   prioritized.

10. **Keep the watched Graffito run on the critical path.** Nothing
    in the reframe blocks or delays it.

## 12. What's Next

### Immediate
- Codex: comparison prompt tightening, then broader improvements
- Codex: world defect linter (if assigned)
- David: can start visual world-building with the pipeline now
- David: can start the L3 scheduler experiment on existing graph

### Pending 5 Pro
- Follow-on request #3 (experiment design) — ready when needed
- Boundary/glossary adjudication — mostly answered by synthesis

### Reading
- Must-reads: Facade, DODM, EMA, Versu, Sentient Sketchbook,
  Tanagra, ATMS, plus Mueller §5.3/§11.3
- Priority: Facade (L3 grounding) and EMA (L2 appraisal upgrade)

### Shipping lane
- Graffito watched run remains the critical path
- authored graph → traversal → playback packet → renderer → stage
- L3 traversal_intent in trace/debug only (not in playback contract)

### Research lane
- L1 world-building experiment (after L3 scheduler test)
- L2 kernel refactor (after both experiments)
- Visual world-building pipeline (available, not blocking)

## 13. Late-Session Opus Review & Updates

### External Opus review of full doc sequence
An independent Opus read the full arc (docs 01-11, all 5 Pro
responses, synthesis, kernel gap analysis, DMPOST sidecar) and
provided four pushbacks:

1. **L1 pressures need richer input than exists.** Start with
   status-flag-level pressures (visual_ungrounded) not structural-
   analysis-level (setup_without_payoff) which requires rich
   event/consequence graphs.

2. **Cross-level propagation is the hardest unsolved problem.**
   Deferred too casually. When character concerns become authored
   material become traversal material — that chain IS the system.
   Need at least a theory of how character concerns tag graph
   nodes so L3 can score against them.

3. **Conductor state vector untested against APC Mini.** Nine
   dimensions, eight faders. Prototype the mapping BEFORE building
   the scheduler — feel constrains what intents are reachable.

4. **Narration/inner-life dashboard is undersold.** Should be
   primary output for early runs, not one of four consumers.

### Closing decision: inner-life dashboard as primary output
David confirmed: the inner-life view (narration companion +
cognitive trace visualizer + system state) is the primary output
for early runs. Not just text narration — the visual dashboard
showing the workings of the system is compelling on its own, useful
for feedback and evaluation, and potentially one of the actual
products. Video is secondary until the watched run proves it can
carry the experience.

Updated doc 11 (settled architecture):
- Promoted inner-life dashboard in shipping lane diagram
- Added to Settled section
- Sharpened Open section with all four Opus pushbacks
- Added overall risk note: "the temptation will be to build
  infrastructure before running experiments"

### Codex2 review of full session
Confirmed architecture is coherent. Corrected Mueller nuances:
- Serendipity taxonomy is broader (6 types, not 4)
- Overdetermination is a split/merge architecture, not a scoring
  bonus
- Action mutation is counter-search-reduction, not general
  creativity
Advised: keep L1 vocabulary soft until it beats a baseline.

### Codex1 final update
Completed character enrichment + tests + prompt shaping. Pipeline
fully tested against real briefs. Priority queue: comparison prompt
tightening next.
