# Session 01 — Orientation & Assessment

**Date:** 2026-03-12
**Model:** Claude Opus 4.6 (Claude Code)
**Parallel conversation:** Also discussed with Codex (GPT-5.2 xhigh) — that session ran out of context mid-work.

---

## What This Project Is

Round 3 of the Daydream Interactive AI Video hackathon (2-week sprint). David is building a **narrative backbone / dream director** on top of a mature real-time video instrument built in rounds 1-2.

**The one-sentence version:** Adapt Erik Mueller's DAYDREAMER cognitive architecture into a system that autonomously drives an existing real-time text-to-video + AI music instrument, producing something that feels like being inside a coherent daydream.

---

## What Already Exists (scope-drd repo)

`/Users/daviddickinson/Projects/Lora/scope-drd/` — the round 1-2 codebase. **This is where the daydream engine should eventually live** (noted but not yet moved).

### The Stage (rendering + control)
- **KREA 14B pipeline** — real-time text-to-video at 24+ FPS, WebRTC streaming
- **APC Mini MK2** — 8×8 MIDI pad grid mapped to 42 palettes × 8 prompts each (336 scene descriptions total)
- **k-SAE faders** — 9 continuous latent-space controls via Top-K sparse autoencoder on UMT5-XXL embeddings (d=4096→16384, k=64)
- **Lyria RealTime** — Google's AI music generation, synchronized with visuals. Multi-prompt layering (identity + instruments + production), per-cell config (density, brightness, guidance, BPM). ~2s control latency.
- **Full REST API** — everything controllable via HTTP:
  - `PUT /api/v1/realtime/prompt` — set video prompt
  - `PUT /api/v1/realtime/ksae/apply` — k-SAE feature deltas
  - `POST /api/v1/realtime/soft-cut` / `hard-cut` — transitions
  - Lyria driven via command queue in `tools/lyria_session.py`
- **Control bus** — chunk-transactional event queue (deterministic ordering at chunk boundaries)

### Key Files
| Component | Path |
|-----------|------|
| FastAPI server | `src/scope/server/app.py` |
| APC Mini bridge | `tools/apc_mini_bridge.py` |
| Lyria session manager | `tools/lyria_session.py` |
| Prompt compiler | `src/scope/realtime/prompt_compiler.py` |
| World state | `src/scope/realtime/world_state.py` |
| Control bus | `src/scope/realtime/control_bus.py` |
| k-SAE application | `src/scope/core/controls/ksae_apply.py` |
| Palette generator | `src/scope/realtime/palette_generator.py` |
| Music enrichment | `scripts/enrich_palette_music.py` |
| Video CLI | `src/scope/cli/video_cli.py` |

### The Bridge Pattern
`apc_mini_bridge.py` is the template for how external logic drives the stage:
- External process holds state
- On events (pad press, fader move), it makes REST calls + Lyria command queue entries
- The stage responds at chunk boundaries
- **The daydream engine follows this exact same pattern** — it's a sibling tool that emits REST calls on a loop instead of responding to MIDI events.

### Palette Pipeline (3-stage)
1. **Generate** — `scripts/generate_palette.py` → Gemini creates 64 visual prompts from a creative brief
2. **Enrich** — `scripts/enrich_palette_music.py` → Gemini Flash adds music identity + 64 music prompts (two-hop: scoring identity → per-cell scoring)
3. **Perform** — `tools/apc_mini_bridge.py` → pad press dispatches visual prompt to Scope + music prompt to Lyria simultaneously

### Lyria Integration Details
- API: `google-genai>=1.62.0`, `v1alpha` API version, model `lyria-realtime-exp`
- Multi-prompt layering: 3 weighted prompts (identity + instruments + production), pipe-delimited in palette YAML
- Config params: BPM (requires reset_context), density, brightness, guidance (smooth), scale, mute_drums, mute_bass
- Known instrument vocabulary: ~70 confirmed-working tokens (303 Acid Bass through Woodwinds)
- ~2s control latency (chunk-based generation), 48kHz stereo PCM output
- BPM/scale changes choreographed: ramp down rhythm → reset_context → apply new config → ramp back up
- Full reference: `scope-drd/notes/architecture/final-push/beyond/proposals/001-apc-mini-mk2-bridge/phase-2-lyria-music/lyria-reference.md`

---

## What's in daydream-round-03 (this repo)

`/Users/daviddickinson/Projects/Lora/daydream-round-03/` — round 3 exploration and notes.

### Mueller's DAYDREAMER (reference)
`daydreamer/` — cloned from `github.com/eriktmueller/daydreamer`. 12,000 lines of Common Lisp (GPL-2.0). Key files:
- `dd_cntrl.cl` — the emotion-driven control loop (the core)
- `dd_kb.cl` — type hierarchy, initial facts, goal definitions, planning/inference rules
- `dd_epis.cl` — episodic memory with threshold-based retrieval + serendipity mechanism
- `dd_mutation.cl` — action permutation for getting unstuck
- `dd_reversal.cl` — counterfactual reasoning (imagining alternate outcomes)
- `dd_gen.cl` — English generation (NOT relevant — replaced by LLM + rendering)
- `gate_cx.cl` — context tree (branching world states, ancestor of counterfactual graph)
- `dd_night.cl` — night dreaming (NOT relevant for v1)

### Design Documents
`daydreaming/Notes/ProspectiveDesign/`

**v1 docs** (three companions — overcollapsed, mix three systems):
- `v1/conducted-daydreaming-architecture.md` — core pipeline, graph structure, rendering translators (~65KB)
- `v1/conducted-daydreaming-interaction.md` — world state, events, session persistence, experience design (~59KB)
- `v1/conducted-daydreaming-hardware.md` — biometrics (NPG Lite), signal mapping, formal verification (ACL2)

**v2 doc** (consolidated after GPT-5 critique — the best document):
- `v2/conducted-daydreaming-architecture-v2.md` — 4-layer architecture (World/Cognitive/Graph/Stage), explicit build sequence, deferred biometrics/vault/ACL2

**Chat transcripts:**
- `chats/2026-03-12_01-01-16_Claude_Chat_Building_computational_daydreaming_models_today.md` — initial exploration: Mueller → modern adaptation → concrete AV pipeline → vault integration → Lyria mapping. (~50K tokens, large)
- `chats/2026-03-12_01-01-48_Claude_Chat_Daydreamer_architecture_split_the_three_systems.md` — GPT-5 critique → Claude absorbs it → examines actual Mueller source → Clojure vs Python → MCP architecture → Claude as engine. (~94KB, large). **The key section where Web Opus examined Mueller's source code starts around line 382.**

**Research:**
- `Notes/DeepResearch/2026-03-11/bidirectional_knowledge_text_coupling.md` — deep report on discourse graphs, knowledge representation, bidirectional sync. Interesting but tangential to core build.

---

## Key Architectural Decisions (from design exploration)

### Settled
1. **Prep/performance split** — expensive generation offline, traversal at runtime (from Mueller's daydreaming/performance modes)
2. **Canonical world state vs ephemeral daydream state** — persistent consequences + revisitable counterfactuals with different rendering treatments
3. **Agency over attention, not action** — the participant conducts what the daydream dwells on, not what characters do
4. **Propose-validate-admit** — LLM generates, deterministic validator checks, graph admits (for constraint enforcement)
5. **Base product is fictional world** — autobiographical vault mode deferred
6. **Biometrics demoted from chooser to modulator** — deferred for v1 entirely

### The GPT-5 Critique (most important piece of external feedback)
Source: the second chat transcript, early section. Key points:
- "You are trying to build three systems at once" (fictional world, autobiographical vault, biometric instrument)
- "Your event model and your offline graph model are not fully reconciled" (post-event graph repair needed)
- "Biometric choice rule is wrong as stated" — arousal ≠ preference, demote to modulator
- "Too many overlapping state notions" — clean distinction: tides, ripeness, hold, conductor state, world state
- Split storage into 5 explicit layers: CanonicalWorldState, SourceBible, CounterfactualGraphPool, SessionLog, PersonalVault(deferred)
- Build sequence: Stage 0 (world bible) → 1 (hand-author graph, prove traversal) → 2 (world state engine) → 3 (engine via MCP/LLM) → 4 (session loop) → 5 (conductor input) → 6 (AV rendering)
- "85% confidence core architecture is worth pursuing. 20% confidence current merged sketch would produce coherent first build."

### What to Port from Mueller (highest value, from Web Opus analysis)
1. **Seven daydreaming goal types** — RATIONALIZATION, ROVING, REVENGE, REVERSAL, RECOVERY, REHEARSAL, REPERCUSSIONS — as first-class node property. Each triggered by specific emotional conditions. This is what makes it feel like daydreaming rather than slideshow.
2. **Emotion-driven priority scheduling** — multiple situations compete for daydream time based on emotional weight. System obsesses about the most charged thing, occasionally switches.
3. **Threshold-based serendipity retrieval** — not just embedding similarity. Counts how many independent active signals (tidal, thematic, character, emotion) point at each piece of source material. When count exceeds threshold, it surfaces regardless of embedding distance.
4. **Mutation mechanism** — when locally exhausted, systematically permute a scene element (swap character, place, situation type) rather than raising temperature.
5. **Critic-Selector** (from Minsky) — monitor generation quality (admission rate, diversity, emotional trajectory), switch strategies when stuck.

### The Lisp Question
David was "a little convinced" by the Web Opus chat that Lisp has properties Python can't replicate. The chat explored this in detail:
- Mueller's architecture is genuinely Lisp-shaped (context trees, pattern-matching rules, symbolic manipulation)
- Clojure specifically: persistent data structures (free context sprouting), Datascript (Datalog queries), core.logic (unification), macros (rule DSL), short path to ACL2
- The split architecture: Clojure engine + Python renderer communicating via data (JSON/EDN)
- But: David's Clojure is "familiar but not substantial", and learning Clojure + designing architecture + authoring world bible simultaneously is too many unknowns
- **Practical conclusion from that chat:** start with Python, possibly extract symbolic core to Clojure later. Or: build world bible as Clojure data literal first as a weekend test of whether it feels productive.
- **This question is unresolved.** David hasn't committed to a language for the engine.

---

## David's Stated Intent (from this session + Codex session)

Direct quotes / paraphrases:
- "My main interest is in adapting Daydreamer and figuring how to hook that into the process"
- "I want to start that now — I wanted to get that worked out a couple days ago"
- "Something that can plausibly dream coherently"
- "Potentially we're also going to maybe have narration... how does daydreaming hook into that?"
- "We gotta figure out what that base material is" — open to existing fiction/movie as starting point, extract a world bible from it
- "The palettes... it's not exactly high quality" — fine as rendering vocabulary but not as the narrative source material
- "Creating a detailed starting point of some sort, but that can be worked on over time — we could just have a shitty one to start"
- On the previous round's infrastructure: "We're going to be standing on top of what I've already built"

---

## What the Next Session Should Do

### Immediate: Read Mueller's Source + Write Adaptation Spec
The Codex session was in the middle of doing this when it ran out of context. It had read:
- `dd_cntrl.cl` (control loop)
- `dd_epis.cl` (episodic memory)
- `dd_mutation.cl` (mutation)
- Was searching for goal type definitions in `dd_kb.cl`

**The deliverable:** A concrete adaptation document that maps Mueller's actual code to the actual system. Not a design doc — a "here's the loop, here are the data structures, here's where LLM calls replace Lisp rules, here's what the first ugly version looks like" spec. Should include:
- The control loop (cycle by cycle)
- The 7 daydream goal types with their trigger conditions
- The data structures (situations, emotions, activations)
- Where the LLM calls go
- How the output maps to Scope REST API calls
- What a minimal world bible looks like
- How narration fits as a third output channel

### Also needed but secondary:
- **Move working home to scope-drd** — the engine code should live as `tools/daydream_engine.py` alongside `apc_mini_bridge.py`
- **Pick starting material** — grab an existing movie/fiction, have an LLM extract a minimal world bible (2 characters, 3 places, 3 situations, 6 events)
- **Decide Lisp/Python** — or just start in Python and see if it hurts

### Narration as third channel
100% agreed this would be powerful. Inner monologue whispered over video + music = you're inside a mind, not watching a screen. Technically simple (one more text output → TTS API). The daydream goal types would determine the *tone* of narration:
- REHEARSAL: "What if I said... no, that's not right..."
- REVERSAL: "If only I had..."
- RATIONALIZATION: "It wasn't that bad, really..."
- ROVING: "Remember that summer..."

---

## File Locations Reference

| What | Where |
|------|-------|
| This session log | `daydream-round-03/session-logs/2026-03-12_session-01_orientation.md` |
| Mueller source | `daydream-round-03/daydreamer/` |
| v2 architecture (best design doc) | `daydream-round-03/daydreaming/Notes/ProspectiveDesign/v2/conducted-daydreaming-architecture-v2.md` |
| Web Opus chat (Mueller source analysis) | `daydream-round-03/daydreaming/Notes/ProspectiveDesign/chats/2026-03-12_01-01-48_...md` (line ~382+) |
| Web Opus chat (initial exploration) | `daydream-round-03/daydreaming/Notes/ProspectiveDesign/chats/2026-03-12_01-01-16_...md` |
| Scope-drd (existing instrument) | `/Users/daviddickinson/Projects/Lora/scope-drd/` |
| APC Mini bridge (template for engine) | `scope-drd/tools/apc_mini_bridge.py` |
| Lyria session manager | `scope-drd/tools/lyria_session.py` |
| Lyria reference (comprehensive) | `scope-drd/notes/architecture/final-push/beyond/proposals/001-apc-mini-mk2-bridge/phase-2-lyria-music/lyria-reference.md` |
| Palette pipeline reference | same directory, `palette-pipeline-reference.md` |
| Round 2 "beyond" ideation | `scope-drd/notes/architecture/final-push/beyond/` (generative-steering, narrative-conducting, instrument-stack-architecture, conductor-landscape) |
| Palettes (42 × 8 prompts) | `scope-drd/content/palettes/` |
| Claude memory | `~/.claude/projects/-Users-daviddickinson-Projects-Lora-daydream-round-03/memory/` |
