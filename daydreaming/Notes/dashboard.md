# Conducted Daydreaming — Project Dashboard

Last updated: 2026-03-15

---

## What This Is

A performance instrument. A human performer steers a cognitive
traversal engine over authored dream-graph material. The engine
has an inner life — concerns, operators, memory, pressure — that
responds to the performer's input and produces dynamics worth
watching, hearing, and eventually rendering as video.

The first creative brief is Graffito (Mark Friedberg's script).
Mark's reaction to a rendered run is the primary external evaluation.

## The Four Layers

```
Conductor (human)         APC Mini faders + pads bias the Dreamer
    ↓
Dreamer (L2/L3)           internal cognition — concerns, operators,
                          memory, pressure, traversal
    ↕ lossy feedback
Director (LLM)            interprets state, introduces events,
                          rearranges the world, wakes dormant situations
    ↓
Stage                     narration + audio (Lyria RT) + visuals (Scope, deferred)
```

The generation pipeline we've been building is **material supply**
for the graph. The Director is the **runtime agent** that makes
traversal interesting by introducing world changes. These are
different things at different timescales:

- Generation pipeline: authoring time, produces graph nodes in advance
- Director: performance time, introduces events during traversal

## What Success Looks Like

The cognitive dynamics are interesting to watch. The performer's
input visibly shapes the traversal. The Director introduces events
that provoke genuine reactions. Narration and audio make the inner
life legible. Eventually, video externalizes all of it.

The evaluation ladder (from experiential-design/13-project-state.md):

1. Scheduler dynamics work? — **Yes** (Graffito + City Routes)
2. Director changes trajectory? — **Partially** (Clojure impl exists,
   target vocab drift unresolved)
3. Stage output feels like anything? — **Unknown** — nobody has watched
4. 12-cycle run feels dreamlike? — **Unknown**
5. Different briefs produce different dreams? — **Unknown**

We are at Level 2-3. The most important near-term question is
**Level 3**: does this feel like anything when you watch/hear it?

## Falsification Criteria

Three scenarios that would change the whole direction
(from experiential-design/13-project-state.md):

1. Dreamer emotional dynamics invisible in any output modality
2. Creative briefs don't survive contact with rendering
3. Director is unnecessary (traversal alone is interesting enough)

None of these has been tested.

---

## Phase Map

```
DONE
  ├── L3 scheduler validated (Graffito + City Routes)
  └── Single-situation patch test (Tessa + Kai)
      Pipeline → batch → admission → curation → traversal

CURRENT — two parallel fronts

Front A: Inner-life watchable experience    ← THE FIRST THING TO WATCH
  ├── Director reliability (fix target vocab drift)
  │   (kernel/src/daydreamer/director.clj — 541 lines, exists)
  ├── Inner-life visualization (beyond trace_viewer.html)
  │   concern landscape, operator firing, pressure shifts
  ├── Lyria Real Time responding to Dreamer state
  │   (no GPU needed, audio is viable now)
  ├── Narration from provenance (narration_bridge.py exists)
  └── Conductor input wired (even just keyboard/fader)

Front B: Material supply scaling
  ├── Supply pilot: repeatable keeper yield
  ├── Curation workflow / keeper bank
  └── Measured curator edit cost

NEXT
  ├── Multi-situation generation (Q5/Q7/Q12 banked, needed here)
  ├── Full Graffito graph assembly
  ├── Director ↔ Dreamer feedback loop tuned
  └── First watchable run (inner life + audio + narration)

LATER
  ├── Scope rendering (when GPUs available)
  ├── Full Graffito video rendering
  └── Show Mark Friedberg something

DEEP BUILD
  └── L2 live cognitive engine (Clojure kernel, research)
```

## Parallel Tracks (revised)

```
Front A: Inner-life experience              Front B: Material supply
─────────────────────────────               ────────────────────────
Director reliability ─────┐                 Supply pilot (codex, now)
                          │                     │
Inner-life visualization ─┤                 Multi-situation generation
                          │                 (needs Q5/Q7/Q12)
Lyria Real Time ──────────┤                     │
                          │                 Graffito scaffold authoring
Narration ────────────────┤                 (human, can start now)
                          │                     │
Conductor input ──────────┤                 Curation tooling ─► Authoring
                          │                 membrane (⚠ blocking risk)
                          │                     │
                          ▼                     ▼
              First watchable run ◄──── Full Graffito graph
              (inner life + audio)      (from Front B)
                     │
                     ▼
              Scope rendering (when GPUs available)
                     │
                     ▼
              Show Mark something

Track 5: L2 kernel (Clojure, independent, deep build, research)
  └── Not blocking first watchable run, but makes it much richer
```

Front A can produce a watchable experience with existing hand-authored
graphs — it doesn't need generated content. Front B makes the graphs
richer and cheaper to produce. They converge at "first watchable run."

Note: Front A is probably **more informative sooner** — it tells you
whether the dynamics are interesting, which is the question that
matters before scaling material supply.

---

Note on the banked structural work (Q5/Q7/Q12): these were correctly
deferred for the patch test (doc 16). But they are required for
multi-situation generation, where the character must transition
across situations with coherent arc. Cheap levers produced
within-situation diversity. Cross-situation coherence needs the
structural mechanisms.

---

## Authoring Lane (L1 + L2 conditioning)

### Generation Pipeline
- [x] Four benchmark fixtures (Kai, Maren, Rhea, Tessa)
- [x] Batch generation + pooled admission
- [x] Deterministic multi-pressure projection (Q6)
- [x] Retrieval self-priming fix (Q8)
- [x] Structural evaluator split (Q10 minimal)
- [x] Prompt compiler exists (Q11, behind --prompt-style flag)
- [x] Situation framing variation (3 Tessa framings, round-robin rotation)
- [x] Temperature 0.7 + system prompt diversity (doc 16: cheapest lever,
      largest single impact on staging variety — discovered late)
- [x] Run ledger + round declarations
- [x] Experiment operating model (doc 15)
- [ ] Supply pilot: repeatable keeper yield
- [ ] Supply pilot: 2 distinct patch packs per fixture
- [ ] Supply pilot: measured curator edit cost

### Bridge Tests
- [x] Tessa patch test passes (traversal + human read)
- [x] Kai patch test passes (traversal + human read)
- [ ] Second fixture pair (Maren or Rhea) — confirmation, not blocking

### Curation at Scale
- [ ] Authoring membrane prototype (freeze/dismiss/respond/cut)
- [ ] John's Membrane design integration
- [ ] Keeper bank with structured verdicts
- [ ] Shortlist-card curation packet format

Note: curation at scale is the bottleneck between "pipeline works"
and "we have a graph." The authoring membrane is the highest-value
surface after the supply pilot proves there is material to manage.

### Graph Assembly
- [ ] Graffito scaffold authored (situations, events, topology)
- [ ] Full character graph (30-60 nodes, multi-situation)
- [ ] Graph compilation: which candidates survive together
      (soft-constraint compiler — research, not just engineering)
- [ ] Multi-character graph with cross-character pressure
- [ ] Graffito graph assembled from generated + authored material

### Multi-Situation Generation (requires banked mechanisms)
- [ ] Q5 operator exploration across situations
- [ ] Q7 DerivedSituationState for cross-situation carry-forward
- [ ] Q12 practice transitions (evasion → confrontation pull)
- [ ] Cross-situation coherence validation

---

## Director (LLM runtime agent)

### Exists
- [x] Director spec (experiential-design/12-director-prompt-spec.md, 506 lines)
- [x] Clojure implementation (kernel/src/daydreamer/director.clj, 541 lines)
- [x] Partially tested on Puppet Knows benchmark
- [x] Can wake dormant situations
- [x] Can introduce novel concepts

### Not yet reliable
- [ ] Target vocabulary drift (current blocker)
- [ ] Full cascade: novel concept → situation wake → event → reaction
- [ ] Reliable directed emotional episodes (external events that provoke)
- [ ] Lossy bidirectional feedback (Director output shifts future traversal)

### Not yet started
- [ ] Director ↔ generation pipeline integration
      (Director introduces events; pipeline has authored the reactions)
- [ ] Director response to conductor input
- [ ] Director operating on generated (not just hand-authored) graphs

---

## Cognitive Engine (L2)

### L2 as generation conditioning (validated)

Python objects in the generation prototype. Conditions prompts
at authoring time. Not a running engine.

- [x] CausalSlice, AppraisalFrame, PracticeContext
- [x] Operator scoring (avoidance, rehearsal, rationalization)
- [x] Concern inference from primitives
- [x] Reappraisal mechanics (salience, ontic, policy)
- [x] Episodic retrieval with source balancing
- [x] Boundary detection

### L2 as live cognitive engine (not built)

The Clojure kernel in kernel/. A fundamentally different thing:
concerns compete in real time, operators fire based on live state,
episodic memory updates during traversal, reappraisal responds to
what the performer does. This is research, not engineering.

- [ ] Mueller control loop as runtime engine
- [ ] Recursive reminding + serendipity
- [ ] Concern competition (live, not batch)
- [ ] Coincidence-mark retrieval
- [ ] Performance-time L2 inside L3
- [ ] Response to conductor input in real time

### Designed but not built (authoring-time mechanisms)
- [ ] Q6 two-pass generate-then-project
- [ ] Q6 POCL-lite causal sketcher (12 schemas)

---

## Traversal Engine (L3)

### Validated
- [x] Graffito pilot (feature arm beats baseline)
- [x] City Routes three-arm comparison
- [x] DODM feature registry
- [x] Conductor surface (faders affect traversal)
- [x] Robustness sweep across seeds
- [x] Generated material survives traversal (patch tests)

### Not yet started
- [ ] Prose-sensitive traversal features
- [ ] Content-aware H vs. G discrimination
- [ ] Full Graffito graph traversal (not just micrographs)

---

## Performance Lane

### Conductor (design research, not just engineering)
- [ ] APC Mini physical mapping (8 faders + 8 pads)
- [ ] Wizard-of-Oz conductor test (designed in 5 Pro r4)
- [ ] Tidal oscillators (autonomous weather layer)
- [ ] NIME mapping research applied
- [ ] Performance/daydreaming mode toggle (the performer's primary
      creative decision — named in experiential-design/01 but undesigned)
- [ ] Five traversal intents as directing vocabulary
      (dwell, shift, recall, escalate, release — proposed for John
       validation, never confirmed)

### Inner-Life Visualization
- [x] Trace viewer exists (trace_viewer.html)
- [ ] Concern landscape view (pressure shifts visible in real time)
- [ ] Operator firing visualization
- [ ] Memory surfacing visualization
- [ ] Something that reads as "watching a mind" not "reading JSON"

### Audio (viable now, no GPU needed)
- [ ] Lyria Real Time responding to Dreamer state
- [ ] Mood-responsive generation from traversal state
- [ ] Music as the first external expression of inner dynamics

### Narration (craft problem, not just engineering)
- [x] Narration bridge exists (narration_bridge.py in scope-drd)
- [x] Companion page design (dark bg, Cormorant Garamond, badges)
- [ ] Packet-first narration (4 derived hints only)
- [ ] Rule-level narration pruning (Mueller Appendix B)
- [ ] Turning structure into language that doesn't sound like
      a system readout
- [ ] TTS pipeline (Flash narration → TTS → audio)

### Video Rendering (deferred — GPUs scarce)
- [ ] Scope pipeline (APC Mini → ScopeDRD → Scope)
- [ ] LoRA-based video generation from traversal
- [ ] Graffito LoRA integration
- [ ] Real-time rendering from graph state
- [ ] Palette-cell → Director-generated prompt transition

---

## Infrastructure

### Operating Model
- [x] Experiment operating model (doc 15)
- [x] Lesson: milestone not mechanism (doc 16)
- [x] Run ledger (results.jsonl)
- [x] Round declarations
- [x] Diagnostic vs. round run discipline

### Evaluation
- [x] Structural hard checks
- [x] Batch diversity metrics
- [ ] Operator-signature soft scoring (cue families)
- [ ] Embedding-based discriminator (post-hoc)
- [ ] Calibrated semantic evaluation
- [ ] Six phenomenological qualities tested against output
      (drift/return, repetition/variation, involuntary intrusion,
       obsessive charge, surprise, gradual stakes — from end-to-end-flow.md)

### Toolchain
- [x] After Effects MCP
- [x] Google image edit MCP
- [x] Segmentation MCP
- [ ] Visual world-building → LoRA training pipeline

---

## Collaborators

| Who | Role | What they need | Status |
|-----|------|---------------|--------|
| Mark Friedberg | Graffito creative partner | A rendered run to react to | Not yet shown anything |
| John | Evaluation, conductor mapping, membrane design | Graffito pilot output + directing questions | Questions prepared, conversation status unclear |

---

## Key Documents

| Doc | Location |
|-----|----------|
| **Control plane** | |
| Dashboard | Notes/dashboard.md |
| Current sprint | Notes/current-sprint.md |
| Canonical map | Notes/canonical-map.md |
| **Architecture** | |
| Settled architecture | pressure-engine-reframe/11-settled-architecture.md |
| Graph contract | pressure-engine-reframe/21-graph-interface-contract.md |
| L2 schema | pressure-engine-reframe/28-l2-schema-from-5pro.md |
| Generation spec | pressure-engine-reframe/30-authoring-time-generation-prototype-spec.md |
| Authoring-time reframe | pressure-engine-reframe/27-authoring-time-generation-reframe.md |
| **Experiential design** | |
| The experience | experiential-design/01-the-experience.md |
| Game engine architecture | experiential-design/17-game-engine-architecture.md |
| Graffito vertical slice | experiential-design/18-graffito-vertical-slice.md |
| Project state + eval ladder | experiential-design/13-project-state.md |
| Operating map | experiential-design/14-operating-map.md |
| Director spec | experiential-design/12-director-prompt-spec.md |
| Narration layer | experiential-design/20-narration-layer.md |
| Playback contract | experiential-design/21-graffito-v0-playback-contract.md |
| **Operating discipline** | |
| Operating model | reading-list/15-experiment-operating-model.md |
| Lesson | reading-list/16-lesson-milestone-not-mechanism.md |
| **Research** | |
| 5 Pro question bank | first-round-03/deep-research-03/01-5pro-question-bank.md |
| 5 Pro replies | first-round-03/deep-research-03/replies/ (Q1-Q14) |
| Broader vision | pressure-engine-reframe/34-broader-application-surface.md |
| Operator taxonomy | pressure-engine-reframe/35-operator-taxonomy-status.md |
| **Source material** | |
| Mueller source trace | Notes/focused-source-trace.md |
| End-to-end flow | Notes/end-to-end-flow.md |
| Mueller book | Notes/Book/daydreaming-in-humans-and-machines/ |

---

## Milestones

Execution = current sprint or lane call. Verification = independent
artifact check status.

| Milestone | Execution | Verification | Date |
|-----------|-----------|--------------|------|
| L3 scheduler validated (Graffito pilot) | Done | Verified | 2026-03-13 |
| L3 three-arm comparison (City Routes) | Done | Verified | 2026-03-14 |
| Generation prototype: single-step validation | Done | Verified | 2026-03-14 |
| Generation prototype: batch + admission | Done | Verified | 2026-03-15 |
| Bridge test: Tessa patch | Done | Verified | 2026-03-15 |
| Bridge test: Kai patch | Done | Verified | 2026-03-15 |
| Supply pilot: keeper yield | Tessa pass / Kai narrow pass | Verified | 2026-03-15 |
| Supply pilot: 2 patch packs per fixture + edit cost gate | In progress | Partial | |
| Director: reliable full cascade | Not started | Pending | |
| Inner-life visualization | Not started | Pending | |
| Lyria RT responding to Dreamer state | Not started | Pending | |
| First watchable run (inner life + audio) | Not started | Pending | |
| Curation workflow / authoring membrane | Not started | Pending | |
| Multi-situation generation (Q5/Q7/Q12) | Not started | Pending | |
| Full Graffito graph assembled | Not started | Pending | |
| Conductor Wizard-of-Oz test | Not started | Pending | |
| Show Mark Friedberg a rendered run | Not started | Pending | |
| L2 live cognitive engine | Not started | Pending | |

---

## Appendix: Blind Spots from Deep Scrub

Things that were planned or designed but dropped off tracking.
Not all of these need action now — but they shouldn't be invisible.

### Creative / Experiential

- **Watching a dream has never happened.** Nobody has watched a full
  traversal run and judged whether it feels dreamlike. The recommended
  step (18-cycle Puppet Knows through Scope) was skipped when focus
  shifted to generation. Level 3 of the evaluation ladder is unanswered.

- **Dream sequence benchmark.** experiential-design/07-dream-sequence-01.md
  is a hand-composed 12-cycle dream through Puppet Knows that shows what
  "a good dream" looks like. Has the current system ever been compared
  against this benchmark? Has anyone asked "does the system produce this?"

- **Arctic Expedition control fixture.** Repeatedly designated as the
  best fixture for understanding the machinery (docs 09, 15). Fully
  specified with milestone rubric. Never built. Biggest gap between
  "what was planned" and "what exists."

- **Heist Spiral and Mythic Night Journey.** Two complete world designs
  in experiential-design/22 ready to become fixtures with no additional
  design work. Dormant.

- **Capstone diagnostic.** Quality gate for creative briefs: write all
  seven capstone sentences, check they sound like the same thematic world
  but each is goal-type-distinct. Validated practice, never formalized.

- **Dual-stream rendering.** Two-GPU concept (Dreamer/Director gap
  visible as split screen) from experiential-design/01. Fully articulated,
  never mentioned again.

### Performance / Conductor

- **Performance/daydreaming mode toggle.** Called "the performer's primary
  creative decision" in experiential-design/04. No design, no implementation,
  no tracking.

- **Five traversal intents.** Directing vocabulary (dwell, shift, recall,
  escalate, release) proposed for John's validation. Never confirmed.

- **Phase 3 logging schema.** Detailed metrics for the Director feedback
  loop (interpretive_distance, goal_congruence, obsession_phase,
  cycles_to_return) from experiential-design/05. None implemented.

- **Harness knobs.** Four tuning parameters flagged in doc 14 (situation
  activation decay, revisit suppression, target trajectory shape,
  randomness temperature). Whether exposed is untracked.

### Architecture / Integration

- **Playback join.** experiential-design/21 says "the next implementation
  seam is the playback join that produces normalized cycle packets."
  This has a defined order of operations but is not tracked.

- **Palette-cell → Director-generated prompt transition.** The current
  system uses palette cell references for rendering. The architecture
  expects the Director to replace that lookup with generated prompts.
  Never formalized as a milestone.

- **Mueller adaptation table.** The mapping from "Mueller trigger →
  Round 03 state signal → candidate node strategy → validator rule →
  stage emission form" was called for in focused-source-trace.md. Never
  written. Would be valuable for verifying fidelity.

- **Compost pattern.** Which graph regions are always bypassed? Which
  episodes never surface? The pattern of neglect as diagnostic — proposed
  in convergence-map.md, never implemented.

### Evaluation

- **Six phenomenological qualities.** Drift/return, repetition/variation,
  involuntary intrusion, obsessive charge, surprise, gradual stakes.
  From end-to-end-flow.md. Never tested against system output.

- **v0 acceptance criteria.** Five observable behaviors from
  focused-source-trace.md that the scheduler was supposed to produce.
  Whether the current implementation exhibits all five is unverified.

- **Node quality gate.** "Would I want to watch 5-8 seconds of this?"
  From experiential-design/19. Never formalized in the batch generation
  workflow.
