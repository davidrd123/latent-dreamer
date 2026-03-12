# Session 03 — Experiential Design & Architecture

**Date:** 2026-03-12
**Model:** Claude Opus 4.6 (Claude Code)
**Parallel conversations:** Another Opus instance (creative track), Codex (mechanical review)

---

## What Happened This Session

Started from the focused-source-trace.md (Codex's Mueller analysis) and the companion docs (adaptation spec, stage contract, language memo, starter kit, home base, end-to-end flow). David pushed back on rushing to implementation — "you're pushing me to implement, but we gotta feel this part out." The session then became a creative exploration that produced a genuinely new architecture.

### Key Creative Moves (in order)

1. **Working backwards from the experience.** What does "feeling like dreaming" actually mean? Six visual phenomena: the switch, the return, the overlay, the narration intrusion, the dwelling, the acceleration.

2. **Performance/daydreaming as two visible modes.** Mueller's toggle repurposed: performance = full immersion in the scene, daydreaming = the engine's interior process becomes visible.

3. **The Dreamer + Director split.** The session's core contribution. Mueller's architecture (the Dreamer) handles emotional logic — what to obsess about, when to switch, why. A second agent (the Director) — Freud meets Jodorowsky — interprets that emotional state into wild visual language. The gap between what the Dreamer says and what the Director shows is where the art lives.

4. **The Director as mutation engine.** Mueller's mechanical mutation (swap one element) is replaced/complemented by the Director's imagination. Interpretation is inherently mutative — every Director output introduces novel elements that feed back as mutations.

5. **Bidirectional feedback.** The Director's output feeds back into the Dreamer as new indices, emotional shifts, situation activation, and surprise triggers. The dream mutates through its own rendering. Lossy feedback prevents runaway.

6. **Team's prompt engineering work as Director toolkit.** The existing style guides (Akira, Wan spec, shared patterns, numbered strategies) are exactly what the Director needs. Goal-type → cinematography mapping table created. Different style guides = different Directors.

7. **Three external reviews** (PCE/simulators lens, MR_IMS/instrument architecture, VC/control theory). Converged on: demote Director narration to text, defer competing lenses, move fast Phase 1→2. Unique contributions: director_concept index type, asymmetric Director context window, contradiction handling via goal congruence + activation trajectory, anchor rule for semantic drift.

8. **Dual-stream rendering.** Two GPUs consuming the same dreamer block with different rendering strategies. The gap becomes visible. APC Mini crossfades between literal and interpreted streams. State-driven assignment (dominant situation, competing hold, counterfactual, goal-type shift).

9. **Phase 3 tuning note.** Three open questions resolved into measurable parameters: gap width (two knobs: interpretive + render distance), contradiction handling (goal congruence + obsession phase), semantic drift (anchor rule + return time metric). Complete logging spec.

---

## Documents Created

All in `daydreaming/Notes/experiential-design/`:

| Doc | Lines | Purpose |
|---|---|---|
| `00-what-this-is.md` | ~50 | Index, two-track explanation |
| `01-the-experience.md` | ~780 | Creative vision: six phenomena, performance/daydreaming modes, Dreamer+Director, three voices, society of mind, bidirectional feedback, Director as mutation engine, Director's toolkit (team's prompt engineering), dual-stream rendering |
| `02-codex-v0-brief.md` | ~120 | Self-contained mechanical v0 brief for Codex |
| `03-architecture.md` | ~570 | Full system architecture with confidence table, two DreamNode versions (v0 + full vision), build phases as hypothesis tests with failure modes |
| `04-review-findings.md` | ~295 | Three external reviews consolidated, 11 design recommendations by phase, additional observations on asymmetry/conductor/mutation/offline-online |
| `05-phase3-tuning.md` | ~235 | Operational: gap width, contradiction handling, semantic drift as measurable parameters with logging spec |

---

## Architecture Summary (What We Designed)

**Not a Mueller port.** A new architecture that uses Mueller for the emotional scheduler and adds the Director layer.

```
World State → Dreamer (Mueller-derived) → Director(s) (LLM + style guide) → Stage
                  ▲                              │
                  └──────── lossy feedback ───────┘
```

**The stable seam:** The `dreamer` block (goal_type, situation, emotion, tension, energy, active_indices, retrieved_episodes). Everything above this seam can be built now. Everything below is under creative investigation.

### What comes from Mueller:
- Scheduler loop (decay, competition, selection)
- Seven goal types as cognitive postures
- Coincidence retrieval with threshold counting
- Performance/daydreaming oscillation
- Emotional and activation decay

### What's new (ours):
- Director interpretive layer (LLM + style guides from team's prompt engineering work)
- Bidirectional feedback (lossy, dimensionality-reducing)
- Director as primary mutation engine (complements Mueller's structural mutation)
- Three voice channels (Dreamer whisper primary, Director text secondary, imagery)
- Dual-stream rendering (two GPUs, gap made visible)
- Goal-type → cinematography mapping
- Phase 3 tuning framework (gap width, contradiction handling, semantic drift)

### Confidence levels:
- **High:** World State, Dreamer, stable seam, Stage (Scope+Lyria)
- **Medium:** Single Director with style guide
- **Low-Medium:** Director lens competition, narration (TTS)
- **Speculative:** Bidirectional feedback, three voice channels

---

## Key Design Decisions

### Settled this session:
1. The Dreamer + Director split as core architecture
2. The `dreamer` block as stable seam (rendering layer is swappable, scheduler is structural)
3. Team's prompt engineering toolkit maps directly to Director infrastructure
4. Build phases as hypothesis tests with graceful degradation
5. Phase 3 logging requirements (gap width, goal congruence, obsession phase, drift metrics)

### From reviews (all three agree):
6. Director narration demoted to text/subtitle, not audio
7. Competing lenses deferred to Phase 4+ (prove single Director first)
8. Phase 1 → Phase 2 should be fast (don't polish v0)

### From Phase 3 tuning work:
9. Gap width is two knobs (interpretive distance + render distance)
10. Contradiction handling via goal congruence + activation trajectory (rising resists, falling allows)
11. Anchor rule: director_concept indices only count when co-activated with grounded Dreamer index
12. Return time (cycles to situation-aligned node) as primary semantic drift metric

---

## What the Next Session Should Do

### Most valuable: Hand-compose a dream sequence
The experience doc explicitly calls for this. Manually pick palette cells, goal types, transitions, narration lines, and Director prompt sketches. See if the sequence on paper feels like dreaming. This is the creative track's forcing function. No code needed.

### Talk to the team
Bring the Dreamer + Director framing and the goal-type → cinematography mapping table. Ask about their latest style guides, automated prompt generation, model achievability limits. The Director's Toolkit section has five specific conversation starters.

### Let Codex build v0
The brief is ready (`02-codex-v0-brief.md`). Codex builds the Mueller-derived scheduler, Dreamer state emission, palette-cell traversal. David continues creative exploration in parallel.

### Dual-stream exploration
If multi-GPU is available, test two simultaneous streams (literal + interpreted) and see if the visible gap communicates without explanation.

---

## Git State

Repo initialized this session. Seven commits on `main`:

```
d9312e3 Add Phase 3 tuning note: feedback loop parameters and logging spec
c30b636 Add dual-stream rendering concept (two GPUs, two interpretations)
bb47042 Add review findings from three external Opus reads
c88c0e7 Expand review findings with additional observations
2efa449 Address Codex review: clarify schema relationship and stage readiness
e7c1ffb Add confidence levels, hypothesis framing, and graceful degradation
befb405 Initial commit: design exploration + Conducted Daydreaming architecture
```

Mueller's daydreamer added as git submodule (github.com/eriktmueller/daydreamer).

---

## File Locations Reference

| What | Where |
|------|-------|
| This session log | `session-logs/2026-03-12_session-03_experiential-design.md` |
| Experiential design docs | `daydreaming/Notes/experiential-design/` (6 files) |
| Mueller source | `daydreamer/` (git submodule) |
| Focused source trace | `daydreaming/Notes/focused-source-trace.md` |
| Stage contract (canonical v0) | `daydreaming/Notes/daydream-to-stage-contract.md` |
| End-to-end flow | `daydreaming/Notes/end-to-end-flow.md` |
| Companion docs | `daydreaming/Notes/` (home-base, language-decision-memo, mueller-adaptation-working-spec, round-03-starter-kit) |
| Team's prompt engineering | `ComfyPromptByAPI/WorkingSpace/davidrd/OutsideStruct/PromptWriting/` and `.../John/Prompting/` |
| Scope-drd (existing instrument) | `/Users/daviddickinson/Projects/Lora/scope-drd/` |
| Claude memory | `~/.claude/projects/-Users-daviddickinson-Projects-Lora-daydream-round-03/memory/` |
| Previous session logs | `session-logs/2026-03-12_session-01_orientation.md`, `session-logs/2026-03-12_session-02_source-trace.md` |
