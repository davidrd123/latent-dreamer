# Session 06 Log — 2026-03-12

## What We Did

### Creative Briefs (David authored, we reviewed)
- David wrote two new creative briefs: **08-brief-stalker-zone.md** ("The Zone Knows What You Want") and **09-brief-arctic-expedition.md** ("The Ship Becomes the Place")
- Three briefs now exist across three tonal registers: cynical meta-awareness (Puppet Knows), spiritual dread (Zone Knows), material endurance (Ship Becomes)
- David's key observations from writing them:
  - **Capstone templates are the best diagnostic** — if you can write 7 distinct capstone sentences in the brief's voice, the concept works. Added to 06-prep-layer.md as a formal quality gate.
  - **Index design is the real work** — situations are easy thematically, hard to index well. Arctic brief has deliberate s2/s5 `pressure` overlap for coincidence testing.
  - **Five situations > four** — arctic brief has natural fifth (hull threat), Puppet Knows could use s5_the_others (interpersonal).

### s5_the_others (drafted, not in fixture)
- Fifth Puppet Knows situation: the other puppets. Do they know? Recognition, mirroring, companion, isolation.
- `recognition` can coincidence-link with s1's `awareness`, `isolation` with s3's `darkness`
- Low ripeness (0.35), zero activation — stumbled into, not started from

### Phase 2 Stub Progress (Codex track, we observed/diagnosed)
- **6→7 handoff proven**: exhaustion + ROVING escape valve produces s1→s3 switch
- **9→10 revenge proven**: directed anger (anger@the_set) via minimal emotional episodes
- **s4 takeover proven**: primed congruence rule lets ring win after activation
- **Trace reporter built**: `daydream_trace_report.py` with benchmark milestone table (first_s3, first_revenge, first_n09, first_s4_rehearsal)
- Benchmark progression across four runs: feedback-stub → roving-handoff → episodes-18 → ring-congruence

### Phase 2 Proper: Director Prompt Spec (we wrote)
- Created **12-director-prompt-spec.md** — full spec for replacing hand-authored sidecar with live LLM Director
- **Director input**: trimmed DreamNode + full creative brief + selected style extensions (capstones, negative space)
- **Director output**: structured feedback echo (director_concepts, situation_boosts, valence_delta, surprise, emotional_episodes, interpretation_note)
- **Full prompt template** with system/user structure
- **Two worked examples**:
  - n07_backstage_headlights (absence inference — light that doesn't direct = honest light → s4 connection)
  - n08_inventory_dominos (hardest case — stored scenery → apparatus → anger@the_set)
- **Key output constraints**:
  - Novelty over echo (don't repeat Dreamer's indices or previous director:* FIFO)
  - Boost forward, not sideways (prefer dormant situations over active ones)
  - Canonical targets only (anger targets must match world fixture vocabulary)
  - Absence inference rule (notice what's NOT happening, not just what is)
- **Three-step calibration strategy**: replay sidecar nodes, run full benchmark, compare across briefs
- **Open questions**: model choice (Haiku vs Sonnet), context window (1-cycle memory?), temperature (start 0.7)

### Director Calibration (Codex built, we diagnosed)
- DirectorClient built using Gemini structured output (gemini-3.1-pro-preview, later gemini-3-flash-preview)
- Calibration harness: `daydream_director_calibrate.py` — multiple prompt variants, replay over 5 benchmark nodes, scorecard
- Best variant (operative_example_n08): 4/5 replay hits, first_s3=9
- **n07 was hardest** — all variants scored 0. Director sees the absence (unscripted_illumination, honest light) but routes boost back to s3/s1 instead of s4. Diagnosis: routing problem, not perception problem.
- After "boost forward" rule: Director now routes to dormant situations, but sidecar overlap score dropped (expected — it's doing something different now)
- After artifice→directed anger variant: Director produces anger episodes but with wrong target vocabulary (the_builder, the_apparatus instead of the_set). Revenge never fires.
- **Current state**: first_s3=9, first_n09=12 (via roving not revenge), first_revenge=None, first_s4_rehearsal=None
- **Next fix**: constrain/normalize Director anger targets to world-canonical IDs

### DAYDREAMER Parallel Track
- **10-daydreamer-parallel-track.md** written and revised — bounded Clojure research sidecar
- Key framing: generative vs descriptive hypothesis — does Mueller's full architecture produce behaviors the Python approximation can't reach?
- Wave 1 done, Wave 2 REVERSAL in progress
- REVERSAL leads Wave 2 (tests pseudo-sprouts directly — if it doesn't justify context machinery, strongest argument weakens)
- Oscillation called out as likely load-bearing mechanism
- episode_cause as causal-extraction problem over context tree
- New risk: richer traces may not improve the art

### Project State Overview (we wrote)
- Created **13-project-state.md** — system map, maturity map, evaluation ladder, decision dashboard, falsification criteria
- **Evaluation ladder**: Level 1 (scheduler dynamics) = Yes, Level 2 (Director changes trajectory) = Partially, Levels 3-5 (stage output, dreamlike sequence, brief differentiation) = Unknown
- **Key falsification**: Dreamer's emotional dynamics might be invisible in video. Creative briefs might not survive contact with video models. Director might be unnecessary.
- **Recommended next step**: one rough rendered run through Scope to answer Level 3

### Kernel Autonomous Dreaming (Codex built, we specified)
- David asked "when am I going to see the DAYDREAMER replication at work?"
- Identified gap: kernel runs scripted benchmarks, not autonomous dreaming
- Wrote task spec: `kernel/doc/tasks/autonomous-dreaming.md` — fixture-driven autonomous trace
- Codex built `puppet_knows_autonomous.clj` — loads shared fixture, derives goal pressure from situations, retrieves against graph, invokes real REVERSAL/ROVING
- **`bb dream`** now works — 12 cycles, readable terminal + HTML output
- Current behavior: starts s1 × rationalization, moves to s1 × reversal with branch sprouting, flips to s4 × rehearsal, reaches ring nodes
- Committed as c64905c
- Known issue: s1/s4 bipolar (s2_the_mission underrepresented) — tuning, not architecture

### Cognitive Trace Visualizer (spec reviewed)
- `kernel/doc/cognitive-trace-visualizer-spec.md` — animated visualization of kernel traces
- Five regions: Situation Landscape (colored circles), Goal Race (bar chart), Timeline + Mode Lanes (reality/daydreaming), Branch Tree (context ancestry), Thought Stream (natural language narration)
- Dark theme, standalone HTML/JS, reads trace JSON
- Directly addresses David's visibility problem
- Not built yet — David asked for session log before compacting

## Key Decisions Made

1. **Capstone diagnostic is the formal quality gate for briefs** — added to 06-prep-layer.md
2. **Creative track produces material, Codex debugs the scheduler** — clear track separation
3. **"Boost forward not sideways"** as Director output constraint — prefer dormant situations
4. **"Canonical targets only"** for Director anger episodes — match world fixture vocabulary
5. **Sidecar overlap score retired as primary metric** — milestone table is the real measure
6. **Kernel autonomous dreaming via adapter layer** — fixture-loading and goal activation at edge, not in pure kernel

## Files Created/Modified This Session

### Created
- `daydreaming/Notes/experiential-design/12-director-prompt-spec.md` — Director prompt spec
- `daydreaming/Notes/experiential-design/13-project-state.md` — project state overview
- `kernel/doc/tasks/autonomous-dreaming.md` — task spec for autonomous kernel
- `kernel/src/daydreamer/benchmarks/puppet_knows_autonomous.clj` — autonomous runner (Codex)
- `kernel/test/daydreamer/benchmarks/puppet_knows_autonomous_test.clj` — test (Codex)

### Modified
- `daydreaming/Notes/experiential-design/00-what-this-is.md` — index updated for 12, 13
- `daydreaming/Notes/experiential-design/06-prep-layer.md` — capstone diagnostic section added

### In scope-drd (Codex, separate repo)
- `daydream_engine.py` — primed congruence, fatigue escape, emotional episodes, Director feedback
- `daydream_trace_report.py` — benchmark milestones, combined feedback view
- `daydream_director.py` — DirectorClient, prompt variants, calibration
- `daydream_director_calibrate.py` — calibration harness
- `director_feedback.json` — Phase 2 feedback sidecar
- `daydream-trace-schema.md` — shared trace contract

## Current State of All Tracks

### Python Engine (Codex)
- Phase 2 stub: DONE (all four transition mechanisms proven)
- Phase 2 proper: IN PROGRESS (Director calibration — routing works, directed anger target vocabulary needs normalizing)
- Next: normalize Director targets → rerun benchmark → check if revenge fires

### Creative Track (us)
- Three briefs: DONE
- Director prompt spec: DONE
- s5_the_others: DRAFTED
- Capstone diagnostic: ADDED to 06
- Not blocking anything

### Clojure Kernel
- Wave 1: DONE
- Wave 2 REVERSAL: IN PROGRESS
- Autonomous dreaming: DONE (`bb dream` works)
- Next: cognitive trace visualizer (spec exists, not built)
- Known tuning: s1/s4 bipolar, s2 underrepresented

## What David Should Do Next

1. **Run `bb dream`** in kernel/ and look at the output — first time seeing Mueller's architecture make decisions
2. **Rebuild the server** — once Scope is up, render the sidecar benchmark through Scope for first visual evaluation
3. **Build cognitive trace visualizer** — the animated "watching a mind think" experience. Spec at `kernel/doc/cognitive-trace-visualizer-spec.md`. Could use frontend-design skill.

## Floating/Unresolved

- Two-GPU idea still floating (two video streams from same dreamer block)
- APC Mini fader as conductor between immersion and introspection
- Whether Director is necessary at all (could scheduler + palette be sufficient?)
- Whether Clojure kernel produces meaningfully richer traces than Python (generative vs descriptive)
- s2_the_mission underrepresented in autonomous kernel traces (goal activation tuning)
