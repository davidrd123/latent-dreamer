# Architecture: Conducted Daydreaming

This is the system we think we're building. It draws from Mueller's
DAYDREAMER but is not a port of it. The key departure is the Director layer
— an interpretive agent that sits between the emotional scheduler and the
stage, producing rich visual mutations that feed back into the dreaming
process.

This document describes the full vision. The build sequence (at the bottom)
is designed so that each phase tests a specific hypothesis, and the system
still works at the previous phase's level if a hypothesis fails.

## What's Decided vs. What's Under Investigation

| Layer | Confidence | Status |
|---|---|---|
| **World State** (situations, ripeness, activation, canon) | High | Decided. Stable across all versions. |
| **Dreamer** (Mueller-derived scheduler, goal types, coincidence retrieval, decay) | High | Decided. Can be built now. The v0 spike tests whether the scheduler produces interesting traversal. |
| **Dreamer state → DreamNode interface** | High | The stable seam. Everything the Dreamer outputs (goal_type, situation, emotion, tension, energy, active_indices, retrieved_episodes) is solid regardless of what interprets it. Everything above this seam can be built now. Everything below is under creative investigation. |
| **Director** (single LLM interpreter with style guide) | Medium | Strong hypothesis. The Dreamer + Director split solves the fundamental problem (Mueller is psychologically interesting but visually flat). Needs empirical test: does the Director's interpretation actually look good on stage? |
| **Director lens competition** (multiple competing interpretive agents) | Low-Medium | Interesting idea. Untested. Depends on whether single-Director output already feels dreamlike or needs more register variation. |
| **Bidirectional feedback** (Director output feeds back into Dreamer) | Speculative | The most exciting and most uncertain part. Could produce beautiful self-mutating dreams. Could produce noise or runaway instability. Phase 3 tests this specifically. |
| **Three voice channels** (Dreamer whisper + Director monologue + imagery) | Speculative | Two TTS streams competing with Lyria music could be overwhelming. Mixing is a major creative challenge. Needs stage testing. |
| **Stage: Scope + Lyria** (video + music) | High | Already built (rounds 1-2). |
| **Stage: Narration** (captions, TTS voices) | Low-Medium | Captions are straightforward. Single TTS voice is Phase 2+. Dual TTS (Dreamer + Director) is Phase 4+, aspirational. |

**The key implication:** The v0 spike should not silently commit to palette
lookup as imagination, single-director architecture, no feedback loop, or
narration as fixed inner monologue. It should prove scheduler behavior only.
If the full Director vision fails, the system still works as a
Mueller-scheduler driving palette cells. If the feedback loop fails, the
system still works as a one-way Dreamer → Director → Stage pipeline.

## The System at a Glance

```
                    ┌──────────────────────────┐
                    │       WORLD STATE         │
                    │  characters, places,      │
                    │  situations (ripeness +   │
                    │  activation), canon facts │
                    │  only changes via event   │
                    │  commit                   │
                    └─────────┬────────▲────────┘
                              │        │
                    unresolved │        │ event commit
                    situations │        │ (rare, explicit)
                              │        │
                              ▼        │
┌─────────────────────────────────────────────────────────┐
│                       DREAMER                           │
│  (Mueller-derived emotional scheduler)                  │
│                                                         │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────┐  │
│  │  Situation   │  │  Goal-Type   │  │  Coincidence  │  │
│  │  Activation  │  │  Competition │  │  Retrieval    │  │
│  │  + Decay     │  │  (7 types)   │  │  (threshold)  │  │
│  └─────────────┘  └──────────────┘  └───────────────┘  │
│                                                         │
│  outputs: goal_type, situation, emotion, tension,       │
│           energy, active_indices, retrieved_episodes     │
│                                                         │
│           ┌───────────────────────────────┐              │
│           │  FEEDBACK INTAKE              │              │
│           │  new indices, emotional       │              │
│           │  shifts, situation activation,│              │
│           │  surprise triggers            │◄─────────┐  │
│           └───────────────────────────────┘           │  │
└─────────────────────┬────────────────────────────────┘  │
                      │                                    │
              dreamer state                                │
                      │                                    │
                      ▼                                    │
┌─────────────────────────────────────────────────────┐   │
│                    DIRECTOR(S)                       │   │
│  (interpretive agent + style guide)                  │   │
│                                                      │   │
│  ┌──────────────────────────────────────────────┐   │   │
│  │  Competing Lenses (Society of Mind)           │   │   │
│  │                                               │   │   │
│  │  ┌───────────┐ ┌───────────┐ ┌────────────┐  │   │   │
│  │  │ Freudian  │ │Jodorowsky │ │ Paranoid   │  │   │   │
│  │  │ lens      │ │ lens      │ │ lens       │  │   │   │
│  │  │ strength: │ │ strength: │ │ strength:  │  │   │   │
│  │  │ 0.4       │ │ 0.7 ◄──── │ │ 0.2        │  │   │   │
│  │  └───────────┘ └───────────┘ └────────────┘  │   │   │
│  │         winner drives imagery                 │   │   │
│  └──────────────────────────────────────────────┘   │   │
│                                                      │   │
│  ┌──────────────────────────────────────────────┐   │   │
│  │  Prompt Construction                          │   │   │
│  │  (style guide + prompt engineering rules)     │   │   │
│  │                                               │   │   │
│  │  dreamer state                                │   │   │
│  │    + winning lens                             │   │   │
│  │    + style guide phrase bank                  │   │   │
│  │    + attention budget / achievability rules    │   │   │
│  │    + goal-type → cinematography mapping       │   │   │
│  │    → structured prompt for Scope              │   │   │
│  └──────────────────────────────────────────────┘   │   │
│                                                      │   │
│  outputs:                                            │   │
│    visual_prompt (for Scope)                         │   │
│    music_prompt (for Lyria)                          │   │
│    dreamer_narration (inner monologue)               │   │
│    director_narration (artistic process, optional)   │   │
│    novel_concepts (for feedback) ─────────────────────┘  │
│    emotional_valence (for feedback)                       │
│    transition_type                                        │
│    dwell_time                                             │
└─────────────────────┬────────────────────────────────────┘
                      │
              DreamNode
                      │
                      ▼
┌──────────────────────────────────────────────────────┐
│                      STAGE                            │
│  (existing Scope + Lyria + narration)                 │
│                                                       │
│  Scope:  PUT /api/v1/realtime/prompt                  │
│          POST /api/v1/realtime/soft-cut | hard-cut    │
│          POST /api/v1/realtime/parameters             │
│                                                       │
│  Lyria:  set_weighted_prompts()                       │
│          set_music_generation_config()                 │
│          reset_context() (on sharp emotional shift)   │
│                                                       │
│  Narration:                                           │
│    channel 1: dreamer voice (whispered, intimate)     │
│    channel 2: director voice (urgent, commanding)     │
│    channel 3: captions (text overlay)                 │
│                                                       │
│  Mix controlled by performance/daydreaming mode       │
│  + manual override (APC Mini faders)                  │
└──────────────────────────────────────────────────────┘
```

## What Each Layer Does

### World State

Small and canonical. A handful of characters, places, situations. Each
situation has:

- **ripeness** — slow, cross-session. How close to mattering.
- **activation** — fast, within-session. How present right now.

World state only changes through explicit event commits. The Dreamer can
obsess about it, the Director can wildly reimagine it, but neither can
silently alter canon. This firewall between imagination and fact is
load-bearing.

### Dreamer

The emotional scheduler. Derived from Mueller, but adapted:

**What we keep from Mueller:**

- **Competing goal-type selection.** Multiple goal types activate from the
  same situation. They compete by strength. The scheduler picks the
  strongest, ties broken randomly. This produces obsessive return and
  involuntary switching.

- **The seven goal types as cognitive postures.** Not just moods — control
  policies that determine what kind of thinking happens:

  | Type | Posture | Visual/sonic character |
  |---|---|---|
  | REHEARSAL | practicing a pending thing | static medium shot, character-first, tension held |
  | REVERSAL | exploring different outcomes | same location altered, weather/time shift |
  | RATIONALIZATION | reframing failure | softer light, withdrawal, quiet justification |
  | ROVING | drifting to something pleasant | wide tracking shot, setting-first, atmospheric |
  | RECOVERY | finding how to continue | gentler motion, warmer light, easing |
  | REVENGE | directed grievance | low angle, push-in, aggressive, two-stage FX |
  | REPERCUSSIONS | projecting consequences | wide shot, cascading change, chain reaction |

- **Coincidence retrieval.** Episodes indexed under multiple cues. When enough
  independent cues converge (marks >= threshold), an episode surfaces.
  Serendipity lowers threshold by one. This is associative recall — "enough
  independent reasons converge" — not semantic similarity.

- **Emotional and activation decay.** Activation decays ~0.95/cycle, emotions
  decay similarly. This creates temporal dynamics: a hot situation cools if
  nothing re-stimulates it. Decay is what makes the system drift.

- **Performance/daydreaming oscillation.** When daydreaming work dries up,
  switch toward performance. When performance work dries up, switch toward
  daydreaming. Bidirectional toggle.

**What we change from Mueller:**

- **Triggers are situation-based, not interpersonal.** Mueller's triggers
  depend on failed relationships and directed anger at specific people. Our
  triggers depend on spatial/atmospheric situations: entrapment, bargaining,
  betrayal, transformation. The goal types are the same; the activation
  conditions are adapted.

- **Mutation is handled by the Director, not by mechanical permutation.**
  Mueller's `action-mutations` swaps one action element and checks serendipity.
  The Director's interpretive imagination produces richer mutations as a
  byproduct of interpretation. Mueller-style mutation stays as a fallback
  for the no-LLM v0.

- **No GATE theorem proving.** Mueller's planning rules (`run-rules`,
  unification, pattern matching) are replaced by the Director's LLM-based
  interpretation. The Dreamer doesn't plan — it schedules and retrieves.
  The Director imagines.

### Director(s)

The interpretive layer. This is where the LLM lives. It is not a planner
and not a retriever — it is an **artist with a specific sensibility** who
watches the Dreamer's emotional state and interprets it into visual and
sonic language.

**Inputs:**
- Dreamer state: goal_type, situation, emotion, tension, energy
- Retrieved episodes (from coincidence retrieval)
- Active Director lens (from lens competition)
- Style guide (phrase bank, templates, achievability rules, negative space)
- World material (characters, places, canon facts)
- Recent Director outputs (for continuity / variation tracking)

**Outputs:**
- `visual_prompt` — structured prompt for Scope, following style guide
  templates and prompt engineering rules
- `music_prompt` — layered prompt for Lyria (identity | instruments |
  production)
- `music_config` — density, brightness, guidance, BPM/scale if shifting
- `dreamer_narration` — inner monologue text (intimate, first-person)
- `director_narration` — artistic process text (urgent, commanding) —
  optional, only in introspective mode
- `transition_type` — soft_cut, hard_cut, or dwell (hold)
- `dwell_time` — how long to stay on this node
- `novel_concepts` — new concepts/objects/relationships introduced by the
  interpretation (for feedback into Dreamer)
- `emotional_valence` — the emotional character of what was produced
  (for feedback into Dreamer)

**Lens competition:**

Multiple Director lenses can be active simultaneously. Each has:
- Activation strength (decays over time, boosted by relevant Dreamer state)
- A style guide / interpretive bias
- Affinity for certain situations or goal types

The strongest lens drives the current interpretation. Lens switching
produces the "register shift" that makes dreams feel dreamlike — one moment
mythic, the next paranoid, the next tender.

Lens activation could be driven by:
- Situation content (surveillance → Paranoid lens activates)
- Goal type (REVENGE → more aggressive lenses)
- Coincidence (a retrieved episode happens to resonate with a particular
  lens's vocabulary)
- Randomness / decay (the current lens weakens, another takes over)

**Prompt construction:**

The Director uses the team's existing prompt engineering infrastructure:

1. Select prompt pattern based on goal type:
   - Establishing (setting-first) for ROVING, environmental scenes
   - Interaction (character-first) for REHEARSAL, REVENGE, interpersonal
   - Insert (object-first) for HOLD states, obsessive focus

2. Apply attention budget rules:
   - High tension → push one element far OOD, simplify everything else
   - Low tension → stay closer to training distribution, more detail allowed

3. Construct prompt from style guide phrase bank:
   - Materials, surfaces, lighting from the active lens's vocabulary
   - Camera and framing from goal-type mapping
   - One singular core idea
   - Thematic capstone encoding the Dreamer's emotional state

4. Generate narration:
   - Dreamer voice: goal-type templates or LLM inner monologue
   - Director voice (optional): the artistic interpretation process

### Feedback Loop

The Director's output feeds back into the Dreamer. This is what makes the
system capable of surprising itself.

**What feeds back:**

| Director output | Dreamer intake | Effect |
|---|---|---|
| Novel concepts (wall-as-mother, flooded cathedral) | New active indices | Triggers coincidence retrieval on unexpected episodes |
| Emotional valence of imagery (unexpectedly tender/violent) | Emotional state shift | Changes goal-type selection on next cycle |
| Situation resonance (Director's interpretation echoes a latent situation) | Situation activation boost | Wakes up a situation that wasn't currently active |
| Unexpected output (Director surprises even itself) | Surprise trigger (Mueller: 0.25 strength boost) | Unhalts stalled goals, re-energizes decayed situations |

**The feedback is lossy by design.** The Director's rich visual interpretation
is reduced to a handful of concepts, a valence, and a resonance check.
That lossy reduction is what produces dream-like mutation. The Dreamer
doesn't get the full image — it gets an emotional/conceptual echo of it.
And that echo shifts its state in ways that produce new imagery on the
next cycle.

**Damping prevents runaway feedback.** The feedback loop could go unstable:
Director pushes wild → triggers emotional pressure → Director pushes wilder.
Mueller's decay factors are the natural damping mechanism. Every cycle:

- Novel indices from the Director decay in the active-index queue (FIFO,
  max 6 — old indices get pushed out)
- Emotional shifts from feedback decay at ~0.95/cycle
- Situation activation boosts decay at ~0.95/cycle

This means feedback only persists if the Director *keeps reinforcing it*.
A one-off wild interpretation decays naturally. A persistent resonance
(the Director keeps producing imagery that activates the same latent
situation) builds up and becomes a real shift in the dream. That's the
right behavior — dreams don't change direction on a single stray image,
but they do gradually migrate when the imagery keeps pointing somewhere
new.

### Stage

Scope (video) and Lyria (music) already exist from rounds 1-2. Narration
is new: captions are straightforward, TTS voice integration is not yet
built. Two-voice TTS (Dreamer + Director) is aspirational — it depends on
Phase 4 proving that the Director's voice adds value.

The canonical runtime contract for stage integration is
`daydream-to-stage-contract.md`, not this document. This document
describes the speculative target. The contract describes what's buildable
now.

**Three output channels (at different readiness levels):**

1. **Video** (Scope) — exists, REST API ready
2. **Music** (Lyria) — exists, command queue ready
3. **Narration** — captions (straightforward), single TTS voice (Phase 2+),
   dual TTS voices (Phase 4+, aspirational)

**Channel mixing is a performance parameter:**

| Mode | Video | Music | Dreamer voice | Director voice |
|---|---|---|---|---|
| Performance (immersive) | full | full | silent or sparse | silent |
| Light daydreaming | full | full | occasional whisper | silent |
| Deep daydreaming | full | full | present | leaks in |
| Full introspection | full | shifts | present | present |

Mixing could be:
- Automatic (driven by activation levels and goal-type intensity)
- Manual (APC Mini faders)
- Both (automatic baseline with manual override)

## The DreamNode (Two Versions)

**Schema relationship:** The canonical v0 DreamNode contract is in
`daydream-to-stage-contract.md`. That document defines the buildable
seam: `ref` (palette cell), `mind`, `world`, `stage`, `audio`,
`narration`. The schemas below show how the DreamNode *could* evolve
as later phases prove out, but they do not replace the stable contract.
The `dreamer` block shown here is an internal scheduler interface —
it feeds whatever rendering strategy is active (palette lookup in v0,
Director in v1+).

The DreamNode schema grows as phases prove out. The stable core is the
`dreamer` block — that's the interface the v0 spike produces. Everything
else accretes as later phases succeed.

### Phase 1: Scheduler Output (internal — what the Dreamer produces)

This is the scheduler's internal payload, **not** a stage-ready DreamNode.
It uses `dreamer` (the scheduler's own vocabulary) rather than the
contract's `mind`/`world`/`audio` fields. A translation step maps this
to the canonical DreamNode schema defined in `daydream-to-stage-contract.md`
before anything hits the stage.

The field differences from the contract are intentional:

| This doc (scheduler internal) | Contract (`daydream-to-stage-contract.md`) | Why |
|---|---|---|
| `dreamer` | `mind` | Scheduler thinks in its own terms; `mind` is the stage-facing subset |
| `ref` | `ref` | Same — palette cell reference |
| `transition_in` | `transition` | Contract uses `transition` with `kind` field |
| (no `world` block) | `world` | Contract carries `present_compatible`, `situation_ids`; scheduler has these in `dreamer` |
| (no `audio` block) | `audio` | Contract carries music prompt/config; in v0 these come from palette, not scheduler |

```json
{
  "id": "n_007",
  "timestamp": "2026-03-13T02:14:33Z",
  "dwell_s": 5.0,

  "dreamer": {
    "situation_id": "s1_escape",
    "goal_type": "reversal",
    "tension": 0.8,
    "energy": 0.5,
    "emotion": {"valence": -0.6, "arousal": 0.7},
    "retrieved_episodes": ["ep_harbor_01", "ep_wall_03"],
    "active_indices": ["entrapment", "wall", "night", "neg_emotion"]
  },

  "ref": {
    "palette_id": "escape_new_york",
    "row": 2,
    "col": 5
  },

  "narration": {
    "text": "What if I hadn't gone through that door?",
    "mode": "caption"
  },

  "stage": {
    "transition_in": {"kind": "soft_cut", "chunks": 4},
    "scope_params": {}
  }
}
```

The `dreamer` block is the stable seam — it carries the full scheduler
state regardless of what renders it. In v0, a thin translator maps
`dreamer` fields to the contract's `mind`/`world` fields and fills
`audio` from the palette cell. In later phases, the Director replaces
that translator entirely.

### Full-Vision DreamNode (Phase 4+ — if Director + feedback + lenses prove out)

```json
{
  "id": "n_042",
  "timestamp": "2026-03-13T02:14:33Z",
  "dwell_s": 5.0,

  "dreamer": {
    "situation_id": "s1_escape",
    "goal_type": "reversal",
    "tension": 0.8,
    "energy": 0.5,
    "emotion": {"valence": -0.6, "arousal": 0.7},
    "retrieved_episodes": ["ep_harbor_01", "ep_wall_03"],
    "active_indices": ["entrapment", "wall", "night", "neg_emotion"]
  },

  "director": {
    "lens": "jodorowsky",
    "lens_strength": 0.7,
    "visual_prompt": "A figure in a white robe presses both palms against a wall of translucent skin, the surface rippling like breath. Behind the wall, golden light pulses in slow rhythm. Extreme close-up on the hands sinking slightly into the membrane. Practical underlighting from below casts long vertical shadows. The scene feels like a sacred threshold that refuses to open.",
    "music_prompt": "identity=Ritualistic ambient drone, sacred and suffocating | instruments=Tibetan Singing Bowls, Pipe Organ, Sub Bass | production=Deep reverb cathedral space, very slow builds",
    "music_config": {"density": 0.2, "brightness": 0.3, "guidance": 4.0},
    "novel_concepts": ["membrane", "breath", "sacred_threshold", "golden_light"],
    "emotional_valence": -0.4,
    "interpretation_note": "Entrapment reimagined as proximity to the sacred — the wall is not a barrier but a living boundary. The reversal is not escaping but realizing the wall breathes."
  },

  "narration": {
    "dreamer_voice": "What if the wall was never meant to keep me out.",
    "director_voice": "No — the wall breathes. Make it breathe. The hands sink in. Hold there.",
    "mode": "deep_daydream"
  },

  "stage": {
    "transition_in": {"kind": "soft_cut", "chunks": 6},
    "scope_params": {}
  },

  "feedback": {
    "new_indices": ["membrane", "breath", "sacred_threshold"],
    "emotion_shift": {"valence": +0.2, "arousal": -0.1},
    "situation_boost": {"s4_origin": 0.15},
    "surprise": false
  }
}
```

Note: the `dreamer` block is identical in both versions. That's the point.
The stable seam doesn't change. What changes is what interprets it.

## What Comes From Where

| Component | Source | Status |
|---|---|---|
| Scheduler loop (decay, competition, selection) | Mueller `dd_cntrl.cl` | Adapt to Python |
| Seven goal types as cognitive postures | Mueller `dd_kb.cl` | Keep types, adapt triggers |
| Coincidence retrieval with threshold | Mueller `dd_epis.cl` | Port directly |
| Performance/daydreaming oscillation | Mueller `dd_cntrl.cl` | Port directly |
| Emotional and activation decay | Mueller `dd_cntrl.cl` | Port directly |
| Director interpretive layer | **New (ours)** | LLM + style guides |
| Director lens competition | **New (ours)** | Mueller-like dynamics |
| Bidirectional feedback loop | **New (ours)** | Lossy concept extraction |
| Three-voice narration | **New (ours)** | TTS + captions |
| Prompt engineering toolkit | **Team's existing work** | Style guides, strategies |
| Stage (Scope + Lyria) | **Rounds 1-2** | Already built |
| Mutation (structural permutation) | Mueller `dd_mutation.cl` | v0 fallback only |
| Mutation (imaginative) | **Director layer** | Primary mechanism |
| GATE theorem proving | Mueller | **Not ported** |
| English generation | Mueller `dd_gen.cl` | **Not ported** |
| Planning rules | Mueller `dd_kb.cl` | **Replaced by Director** |

## Build Sequence

Each phase tests a specific hypothesis. If a phase fails, the system still
works at the previous phase's level. The architecture is designed to
degrade gracefully — each layer is additive, not load-bearing for the
layers below it.

### Phase 1: Dreamer v0 (no LLM, no Director)

Codex can build this now, in parallel with creative exploration.

- Mueller-derived scheduler in Python
- Palette cells as episodes
- Coincidence retrieval with threshold
- Goal-type selection with decay
- DreamNode emission referencing palette cells (v0 schema)
- Template narration
- Outputs to stdout/file (no live stage connection yet)

**Hypothesis:** The Mueller-derived scheduler produces interesting traversal
patterns — obsessive return, drift, switching, hold.

**Success looks like:** Reading the session log and saying "yes, this feels
like a mind circling, returning, drifting, getting stuck and unstuck."

**If it fails:** The scheduler alone isn't enough. But the `dreamer` block
interface is still valid — we just need a different or richer decision layer
on top of it. The spike still did its job by producing readable logs.

**What this phase is NOT allowed to decide:** That palette lookup is the
final rendering strategy. That narration is fixed templates. That the
DreamNode schema is complete. The v0 `ref` block (palette cell) is
explicitly a placeholder for the Director.

### Phase 2: Director v0 (single lens, single LLM call)

Add one Director with one style guide.

- Takes Dreamer state (`dreamer` block), produces a Scope prompt
- Uses team's prompt engineering rules
- Generates Dreamer narration (inner monologue)
- No feedback loop yet — pipeline only (Dreamer → Director → Stage)

**Hypothesis:** An LLM interpreting the Dreamer's emotional state through
a style guide produces more visually interesting output than palette lookup,
and the gap between Dreamer logic and Director imagery feels dreamlike
rather than disconnected.

**Success looks like:** The Director's prompts render well on Scope, and
the combination of Dreamer narration + Director imagery feels like being
inside a mind — not like an illustrated audiobook.

**If it fails:** Palette lookup works fine. The Dreamer is still the right
scheduling layer. We just drive the stage more directly.

### Phase 3: Feedback loop

Wire the Director's output back into the Dreamer.

- Extract novel concepts from Director output
- Feed back as active indices in coincidence retrieval
- Shift Dreamer emotional state from Director's output valence
- Check for latent situation activation
- Apply Mueller-derived decay as damping

**Hypothesis:** The bidirectional feedback produces interesting dream
mutation — the dream evolves through its own rendering in ways that
surprise and feel organic.

**Success looks like:** The system produces sequences where the Director's
interpretation visibly shifts what the Dreamer obsesses about next, and
those shifts feel like how dreams actually mutate.

**If it fails:** The one-way pipeline (Dreamer → Director → Stage) still
works. The Director still interprets. The feedback just doesn't add enough
value to justify the complexity and instability risk. The system is still
interesting without it.

### Phase 4: Multiple lenses + Director voice

Add competing Director lenses with Mueller-like dynamics.

- Multiple style guides active simultaneously
- Lens strength, decay, competition
- Director's own narration channel (the "artistic process" voice)
- Channel mixing (performance ↔ daydreaming modes)

**Hypothesis:** Switching between interpretive frames (mythic → paranoid →
tender) itself feels dreamlike, and the Director's own voice adds a layer
of meaning.

**Success looks like:** The audience can feel the register shifts, and
they contribute to the dream feeling rather than feeling like glitches.

**If it fails:** Single-Director is enough. One consistent interpretive
voice is simpler and may be more coherent.

### Phase 5: Live stage integration

Connect to actual Scope + Lyria + TTS.

- DreamNode → Scope REST API calls
- DreamNode → Lyria weighted prompts + config
- Narration → TTS pipeline
- APC Mini integration for manual mode/mix control

**Hypothesis:** The system works as a live instrument, not just a log
generator. The APC Mini can meaningfully conduct the dream.

### Phase 6: World authoring + event system

- Authored world bibles (from existing fiction or original)
- Event candidates, repeated approach, explicit commitment
- Post-event graph repair
- Cross-session persistence

**Hypothesis:** The dream accumulates meaning across sessions. Events
matter. The world changes.

## What This Architecture Is Not

- **Not a Mueller port.** Mueller provides the emotional scheduler and
  retrieval mechanisms. Everything above the Dreamer layer is new.

- **Not a narrative engine.** It doesn't tell stories. It produces
  emotionally motivated, visually interpreted, semi-autonomous dream
  experiences. The "plot" (if any) emerges from the feedback loop, not
  from a story planner.

- **Not a slideshow.** The combination of motivated switching (Dreamer),
  wild interpretation (Director), and lossy feedback produces sequences
  that are associative and surprising, not linear or random.

- **Not a chatbot.** The LLM in the Director layer is not conversing.
  It's interpreting emotional state into visual language, constrained by
  a style guide and achievability rules. Its output is structured prompts,
  not prose.

- **Not final.** This document describes the full vision. The build
  sequence is designed so that each phase can fail and the system still
  works at the previous phase's level. The Dreamer layer is decided. The
  Director layer is a strong hypothesis. The feedback loop is speculative.
  The competing lenses are aspirational. Confidence levels are stated
  explicitly in the table at the top.
