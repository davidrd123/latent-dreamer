# Rendering Agents: Director, Cinematographer, Composer

Provisional. Written to capture the current design thinking and keep
iterating on it.

## The Problem This Solves

The previous docs conflated "the Director" into a single role that was
supposed to interpret the Dreamer's cognitive state AND produce
renderable prompts for Scope AND produce music for Lyria. That's three
different jobs with different skill profiles, different temperature
requirements, and different reference material.

The core question that forced this apart: **where does surprise enter
the pipeline?**

Mueller's original system produces surprise through rule intersection
(serendipity), plan mutation (permutation/substitution), and emotional
diversion. The rendering of the mental image is downstream — it's not
where surprise happens.

Our system replaces Mueller's mechanical surprise with an LLM's
interpretive imagination. But that interpretive wildness needs to happen
at exactly one point, with coherence maintained everywhere else.

The analogy that clarified it: think Wes Anderson or Kubrick. An
extremely consistent visual world within each piece, where surprise
comes from what happens inside that world, not from the world itself
changing. Dreams work the same way — you're in a recognizable place,
but things are wrong. The hallway is too long. Someone who shouldn't be
there is there. The setting stays coherent. The violations are specific
and meaningful.

## The Three-Agent Architecture

Each dream cycle produces three LLM calls. Each has a distinct job,
distinct reference material, and a distinct temperature profile.

### 1. Director — "What bends?"

The Director reads the Dreamer's cognitive state through the creative
brief and articulates the **delta** — what is different about this
moment, what has changed, what the dream logic is doing to the
established world.

**Inputs:**
- Dreamer state (goal_type, situation, tension, energy, emotion)
- Branch facts from the cognitive cycle (what fired, what woke up)
- The creative brief (full — concept, tensions, interpretive angles,
  obsessions, charged objects)
- Style extensions (capstone templates, negative space)
- Previous cycle's Director output (for continuity)

**Output:**
- A conceptual delta: what is this moment about, what has changed,
  what bends
- Novel concepts (for feedback into the Dreamer)
- Situation boosts (for feedback into the Dreamer)
- Valence shift (for feedback into the Dreamer)
- Surprise signal (for feedback into the Dreamer)
- Emotional episodes if warranted (rare)
- Interpretation note (logged for human review)

**Character:**
- High temperature
- Wild, interpretive, conceptual
- Reads through the brief's lens — not freelancing
- The Director's job is NOT to describe what things look like. It is
  to say what the dream logic is doing: "The seam in the puppet's chest
  is open. Light comes from inside. The ring is no longer hidden."

**What the Director is not:**
- Not a visual prompt writer
- Not a cinematographer
- Not a narrator
- Not a composer

The Director produces the **meaning** of the visual moment. The craft
layers downstream turn that meaning into renderable output.

### 2. Cinematographer — "How does it look?"

The Cinematographer takes the Director's conceptual delta and the base
visual world from the creative brief, and constructs the actual
text-to-video prompt that Scope/Wan can render well.

This is an LLM call, not a template. Constructing good prompts from
rubrics like "attention budget," "front-load the subject," "one
singular core idea," "negative prompts for this model's failure modes"
requires judgment. Templating it would produce mediocre prompts every
time.

**Inputs:**
- Director's conceptual delta (what bends)
- Base visual world from creative brief (materials, palette,
  composition, forbidden elements)
- Style guide phrase bank (approved vocabulary for this visual world)
- Prompt engineering rubrics from the team's strategy docs:
  - Strategy 2: Conceptual distance / attention budget
  - Strategy 2.1: OOD generalization
  - Strategy 3: Strategic simplification
  - Strategy 4.1: Thematic capstone
  - Strategy 4.2: Aesthetic control (cinematography & style)
  - Strategy 5: Material & motion translation
  - Strategy 6: Character consistency
  - Strategy 7: Directing performance
  - Strategy 8: Active props & tangible VFX
  - Strategy 9: Technical analogy
- Model-specific prompt patterns (from Wan spec or equivalent):
  - Pattern A: Establishing shot (setting-first) — for ROVING, drift
  - Pattern B: Interaction shot (character-first) — for REHEARSAL, REVENGE
  - Pattern C: Insert shot (object-first) — for hold states, obsession
- Goal-type-to-cinematography defaults (from doc 01 mapping table)
- Previous cycle's visual prompt (for continuity)

**Output:**
- `visual_prompt` — the actual text-to-video prompt for Scope
- `transition_type` — soft_cut, hard_cut, or dwell
- `dwell_time` — how long to stay on this node
- `scope_params` — any Scope-specific parameters (strength, guidance, etc.)

**Character:**
- Lower temperature than Director
- Disciplined, craft-focused
- Applies the rubrics with judgment, not mechanically
- Stays within the visual world established by the creative brief
- The Cinematographer's wildness comes from the Director's delta, not
  from its own imagination

**The key rule:** The Cinematographer should never invent visual
content that the Director didn't imply. Its job is to render the
Director's vision into a prompt that the model will actually produce
well. The creative leap is upstream. The craft is here.

### 3. Composer — "How does it sound?"

The Composer takes the Director's conceptual delta and the musical
world from the creative brief, and constructs the Lyria prompt and
configuration.

**Inputs:**
- Director's conceptual delta
- Musical palette from creative brief (genre, instruments, register,
  mood boundaries)
- Previous cycle's music state (for continuity)
- Goal-type-to-musical-register defaults (TBD — equivalent of the
  cinematography mapping but for music)

**Output:**
- `music_prompt` — layered prompt for Lyria (identity | instruments |
  production)
- `music_config` — density, brightness, guidance, BPM/scale if shifting

**Character:**
- Lower temperature than Director
- Works within the established musical world
- Shifts keys, textures, density — but within the genre/palette set
  by the brief

**Status: provisional.** The Lyria prompt engineering pipeline is not
yet well understood. The research exists but hasn't been formatted into
usable rubrics. For now, the Composer call can be as rough as the
current Lyria integration — crappy but functional — and improved as the
Lyria game gets debugged separately. This is a known gap, not a blocked
dependency.

## The Pipeline Per Cycle

```
Dreamer
  │
  │  cognitive state + branch facts
  │
  ▼
Director (LLM, high temp)
  │
  │  conceptual delta + feedback
  │  ┌──────────────────────────────────────────┐
  │  │ "The seam is open. Light from inside.    │
  │  │  The ring is no longer hidden.           │
  │  │  Moment of accidental honesty."          │
  │  └──────────────────────────────────────────┘
  │
  ├──────────────────┬──────────────────────────┐
  │                  │                          │
  ▼                  ▼                          ▼
Cinematographer    Composer               Dreamer feedback
(LLM, lower temp)  (LLM, lower temp)      (concepts, boosts,
  │                  │                      valence, surprise)
  │                  │
  ▼                  ▼
visual_prompt      music_prompt
+ transition       + music_config
+ dwell_time
  │                  │
  └────────┬─────────┘
           │
           ▼
     DreamNode emission
           │
           ▼
        Stage
   (Scope + Lyria)
```

Note: Cinematographer and Composer can run in parallel — they both
consume the Director's delta, neither depends on the other.

## What Each Layer Anchors

| Layer | Anchors | Changes during performance? |
|---|---|---|
| Creative brief | Visual world, materials, palette, forbidden elements, musical genre | No |
| Style guide / prompt rubrics | Prompt craft rules, model-specific vocabulary | No |
| Director | What bends, what the moment means | Yes — every cycle |
| Cinematographer | How the bend renders as a prompt | Yes — every cycle |
| Composer | How the bend sounds | Yes — every cycle |

The creative brief is the production design. It is what makes a
performance feel like **one piece** rather than a visual random walk.

## What This Replaces

This spec supersedes the single-Director rendering model described in
docs 01 and 03. Specifically:

- Doc 03 §Director outputs (`visual_prompt`, `music_prompt`,
  `music_config`, `dreamer_narration`, `director_narration`,
  `transition_type`, `dwell_time`): these outputs are now split across
  Director + Cinematographer + Composer.

- Doc 01 §Director's Toolkit and the goal-type-to-cinematography
  table: this material moves into the Cinematographer's system prompt,
  not the Director's.

- Doc 12 (Phase 2 Director spec): the Director's feedback schema
  (`director_concepts`, `situation_boosts`, `valence_delta`, `surprise`,
  `emotional_episodes`, `interpretation_note`) remains valid. The
  Director's output now includes both that feedback schema AND the
  conceptual delta that feeds the Cinematographer and Composer.

## What This Doesn't Change

- The Dreamer architecture (Python scheduler or Clojure kernel)
- The DreamNode contract (what the stage consumes)
- The trace schema (what evaluation inspects)
- The feedback loop (Director concepts → Dreamer active indices)
- The creative brief format
- The stable seams from doc 14

## Swappability

This separation gives clean swap points:

- Swap the Director's lens (Jodorowsky vs Lynch vs Paranoid) without
  touching the craft layers
- Swap the style guide (Akira vs noir vs Tarkovsky) without touching
  the Director
- Swap the model (Wan vs KREA vs future model) by updating the
  Cinematographer's rubrics, not the Director
- Swap Lyria for another music engine by updating the Composer, not
  the Director

Each swap changes one agent's system prompt. The others don't notice.

## Open Questions

1. **Director delta format.** The Director's conceptual delta is
   currently described loosely ("what bends"). It needs a more concrete
   schema — but not so rigid that it constrains the interpretive
   wildness. Probably: a short free-text description of the visual
   moment (2-4 sentences) plus structured fields for the feedback
   schema. The Cinematographer reads the free text.

2. **Latency budget.** Three sequential-then-parallel LLM calls per
   dream cycle. The Director call must complete before Cinematographer
   and Composer can start. If dream cycles are 4-8 seconds, the total
   LLM time needs to fit within that window. Model choice (Haiku for
   craft layers?) and whether the Director can overlap with the
   previous cycle's rendering both matter.

3. **Narration.** Narration is not assigned to any of the three agents
   yet. It could be a Director output (the Dreamer's inner monologue),
   a fourth agent, or template-based. Deferred — narration is Phase 2+
   and not blocking.

4. **Lyria rubrics.** The Composer needs the same quality of reference
   material that the Cinematographer has from the team's prompt
   engineering docs. That material doesn't exist yet in usable form.
   Known gap, to be addressed when Lyria debugging happens.

5. **How much of the Director's delta is structured vs free-text.**
   The Cinematographer needs to understand the delta, but it also needs
   creative room. Too structured = the Director is just filling in
   fields. Too free = the Cinematographer has to re-interpret. The
   right balance is probably: free-text conceptual description +
   structured metadata (goal_type, transition_hint, intensity).

6. **Continuity across cycles.** Each agent gets the previous cycle's
   output for continuity. But how much context? Full history = token
   bloat. Last cycle only = no arc awareness. Probably: last 2-3
   Director deltas, last visual prompt, last music state.
