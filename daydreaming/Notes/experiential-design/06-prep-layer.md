# The Prep Layer: Offline Creative Generation

The live system (Dreamer → Director → Stage) needs upstream material to
work from. The Dreamer needs situations, emotional dynamics, a world. The
Director needs a creative brief and a style guide. Right now that material
is hand-authored. This note is about making it generative.

## The Gap This Fills

The architecture has two well-defined layers:

- **Dreamer** — *what* to obsess about (emotional scheduling, goal types,
  coincidence retrieval)
- **Director** — *what that looks like* (visual interpretation through a
  style guide)

What's missing is: **what is this piece about?** The thematic concept, the
creative through-line, the interpretive angle. "Boundaries becoming
permeable." "The impossibility of return." "Power expressed through
architecture." That's not the Dreamer's job (it handles emotional
dynamics, not thematic meaning) and it's not the Director's job (it
translates emotional state into visual language, but needs a lens to
translate *through*).

This is a third layer, and it's upstream of both. It's prep, not
performance.

## The Film Production Analogy

In film, pre-production is where most creative decisions happen:

| Film role | What they produce | Daydream equivalent |
|---|---|---|
| Writer / showrunner | Concept, thematic through-line, core tensions | Creative brief |
| Production designer | Visual world rules, material language, color logic | Style guide extensions |
| Location scout | Places that embody the concept | Situation-to-palette mappings |
| Costume / prop designer | Object language, charged artifacts | Charged objects, canon tokens |
| Casting | Who the characters are, how they relate | Character dynamics, relational tensions |

Then the director and DP *execute* from that prep during the shoot. They
don't reinvent the concept on set — they interpret it in the moment.

The daydream system works the same way. Prep happens offline. Performance
happens live. The creative personality lives in the prep, not the
performance.

## What the Prep Layer Produces

Three artifacts that the live system consumes:

### 1. Creative Brief (`creative_brief.yaml`)

The thematic DNA of the session. Not a plot — a lens.

```yaml
title: "Permeable Boundaries"
concept: >
  Every wall is almost passable. Every barrier breathes.
  The horror is not being trapped — it's being almost free.
core_tensions:
  - inside/outside are not stable categories
  - safety requires enclosure, but enclosure suffocates
  - permeability is both liberation and violation
interpretive_angles:
  - walls as membranes (biological, not architectural)
  - doors as wounds (openings that shouldn't exist)
  - light as pressure (what pushes through)
obsessions:
  - skin as architecture
  - breathing as structural failure
  - thresholds that resist crossing
charged_objects:
  - the slot in the concrete wall
  - the golden dust that solidifies
  - the gate that opens inward only
```

The Director receives this alongside the Dreamer state and the style guide.
It shapes *how* the Director interprets every emotional state. ENTRAPMENT
through "permeable boundaries" produces different imagery than ENTRAPMENT
through "the impossibility of return."

### 2. World Material (`world.yaml`)

Situations, characters, places — already defined in the architecture. The
prep layer generates these rather than requiring hand-authoring.

```yaml
situations:
  - id: s1_almost_through
    description: "The wall gives slightly. Not enough."
    ripeness: 0.6
    activation: 0.3
    brief_alignment: "permeable boundaries"
  - id: s2_offered_exit
    description: "Someone opens a door. The air on the other side smells wrong."
    ripeness: 0.4
    activation: 0.1
    brief_alignment: "permeability as violation"
```

The `brief_alignment` field connects each situation to the creative brief's
thematic concepts. This is what makes the Dreamer's scheduling feel
thematically coherent even though the Dreamer itself doesn't know what the
piece is "about."

### 3. Style Guide Extensions (`style_extensions.yaml`)

The base style guide (phrase bank, cinematography rules, achievability
constraints) is stable across sessions — it's tuned to the model/LoRA. But
the creative brief can extend it with session-specific vocabulary:

```yaml
brief_phrases:
  materials: ["translucent membrane", "breathing concrete", "wet plaster"]
  lighting: ["light leaking through skin", "pressure-glow", "golden seepage"]
  textures: ["surface tension", "almost-solid air", "permeable stone"]
negative_space:
  - no clean glass (too literal for permeability)
  - no locked doors with visible locks (the barrier is organic, not mechanical)
capstone_templates:
  rehearsal: "The scene feels like pressing against something that almost yields."
  reversal: "The scene feels like discovering the wall was never solid."
  roving: "The scene feels like drifting through a space with no edges."
```

These extend (not replace) the base style guide. The Director draws from
both the base vocabulary and the brief-specific vocabulary.

## Prep Agents

The prep layer is where creative personality lives. Different prep agents
produce different creative briefs from the same seed material.

### What a prep agent is

An LLM with a strong system prompt that encodes a creative sensibility.
It receives seed material (a palette, a mood, a world fragment, an
existing situation set) and produces a creative brief, world material,
and style extensions.

### Example prep agents

| Agent | Sensibility | What it produces |
|---|---|---|
| **The Jodorowsky** | Ritual, body, sacred geometry, alchemical transformation | Briefs about transformation through ordeal, boundaries as sacred thresholds |
| **The Tarkovsky** | Memory, water, rooms that breathe, overcast light | Briefs about the impossibility of return, spaces that remember |
| **The Paranoid** | Surveillance, hidden systems, institutional control | Briefs about visible/invisible power, architecture as authority |
| **The Maternal** | Enclosure, origin, separation, the body as first home | Briefs about permeable boundaries, inside/outside instability |
| **The Grief** | Loss, absence, objects that outlast their owners | Briefs about what remains, charged emptiness, rooms after departure |

These are not competing Directors. They're competing *writers*. Each one
produces a complete creative brief that a single Director then executes
from. Different prep agent = different thematic DNA = different dream
session from the same live architecture.

### Personal obsessions

The interesting part of prep agents is that they can have **fixations**.
A grief-inflected agent doesn't just generate sad situations — it
reads *everything* through loss. An escape palette becomes about what
was left behind, not about getting out. A betrayal situation becomes
about the absence where trust used to be.

These fixations are encoded in the prep agent's system prompt and they
bleed into every artifact it produces: the creative brief's core tensions,
the situations' emotional angles, the style extensions' vocabulary, the
charged objects. The entire session has a coherent thematic personality
because the prep agent that generated its material had one.

## How This Connects to the Architecture

```
PREP LAYER (offline, generative, can take time)
  seed material ──→ prep agent(s) ──→ creative_brief.yaml
  (palette,          with creative       world.yaml
   mood,             personality         style_extensions.yaml
   world fragment,
   obsession)

                         ↓

LIVE SYSTEM (real-time, the existing architecture)
  ┌─────────────────────────────────────────────┐
  │ Dreamer                                      │
  │   consumes: world.yaml (situations,          │
  │     characters, places)                      │
  │   does not see: creative_brief               │
  │   produces: dreamer state                    │
  └──────────────────┬──────────────────────────┘
                     │
                     ▼
  ┌─────────────────────────────────────────────┐
  │ Director                                     │
  │   consumes: dreamer state                    │
  │           + creative_brief.yaml              │
  │           + style_guide + style_extensions   │
  │           + world.yaml                       │
  │   produces: DreamNode                        │
  └──────────────────┬──────────────────────────┘
                     │
                     ▼
                   Stage
```

Key point: **the Dreamer does not see the creative brief.** The Dreamer
schedules emotions based on situations, goal types, and activation
dynamics. It doesn't know that the piece is "about" permeable boundaries.
That thematic meaning is the Director's lens — it shapes interpretation,
not scheduling.

This means the same Dreamer behavior (obsessing about s1_almost_through
in REVERSAL mode) produces completely different visual output depending on
which creative brief the Director is working from. Swap the brief, get a
different dream. The live system doesn't change.

## Scaling

The prep layer is the scalability answer. Instead of hand-authoring every
dream session's thematic material:

1. **Seed** — pick a palette, a mood, or a world fragment
2. **Generate** — run one or more prep agents against the seed
3. **Curate** — review the output, keep what's interesting, discard
   or edit what isn't
4. **Load** — feed the curated brief + world + style extensions into
   the live system
5. **Perform** — the live system runs from the prep material

Step 3 (curation) is important. Prep agents generate candidates, not
finished material. The human reviews the creative brief and decides
whether the thematic concept is interesting, whether the situations
carve the palette in productive ways, whether the charged objects feel
charged. This is creative direction, not automation.

Over time, the curation gets lighter as you learn which prep agents
produce material you trust. But the human stays in the loop on
"what is this piece about."

## Relationship to Build Phases

The prep layer is upstream of the build sequence in `03-architecture.md`:

- **Phase 1** (v0 spike): Hand-authored world material. No prep layer
  needed yet.
- **Phase 2** (single Director): Hand-authored creative brief. This is
  where you discover what a good brief looks like by writing a few
  manually.
- **Phase 3** (feedback loop): Same hand-authored briefs. The feedback
  loop is tested against known thematic material.
- **Phase 5+** (live stage, world authoring): This is where prep agents
  become valuable. You want to generate material for multiple sessions,
  multiple palettes, multiple thematic angles. Hand-authoring doesn't
  scale. Prep agents do.

The prep layer is not on the critical path for proving the core
architecture. It's on the critical path for making the system *productive*
— able to generate interesting dream sessions without requiring a human
to hand-write every creative brief.

## Open Questions

1. **How much of the creative brief does the Director actually use?** A
   brief with 3 core tensions, 5 interpretive angles, and 8 obsessions
   might be too much for a single LLM context window to meaningfully
   integrate. The right brief size is an empirical question — start
   small (concept + 2-3 tensions + a handful of charged objects) and
   see how much the Director can absorb.

2. **Can prep agents generate situations that the Dreamer's coincidence
   retrieval actually works with?** The situations need rich indexing
   cues for the scheduler to produce interesting traversal. Prep agents
   need to understand what makes a good index structure, not just what
   makes a good narrative concept.

3. **How do you seed a prep agent?** A palette alone might not be enough
   — the agent needs something to react to. A mood word? A single
   image? A piece of music? An existing situation that it extends? The
   right seed format is a creative question.

4. **Multiple prep agents per session?** You could run two prep agents
   against the same palette and blend their outputs — one situation
   set from the Paranoid agent, one from the Maternal agent. That
   creates thematic tension in the material itself (the world has
   paranoid elements and maternal elements, and the Dreamer's
   scheduling determines which dominate). Interesting but adds
   authoring complexity.
