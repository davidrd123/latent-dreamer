# The Prep Layer: Creative Briefs

The live system (Dreamer → Director → Stage) needs upstream material to
work from. The Dreamer needs situations and emotional dynamics. The
Director needs a creative brief and a style guide. This note defines
what a creative brief is, provides the format, and includes a prototype.

## The Gap This Fills

The architecture has two well-defined layers:

- **Dreamer** — *what* to obsess about (emotional scheduling, goal types,
  coincidence retrieval)
- **Director** — *what that looks like* (visual interpretation through a
  style guide)

What's missing is: **what is this piece about?** The thematic concept, the
creative through-line, the interpretive angle. That's not the Dreamer's
job (it handles emotional dynamics, not thematic meaning) and it's not
the Director's job (it translates emotional state into visual language,
but needs a lens to translate *through*).

The creative brief is that lens. It sits alongside the style guide in the
Director's input. The Dreamer does not see it — thematic meaning shapes
interpretation, not scheduling.

## What a Creative Brief Contains

Three artifacts that the live system consumes:

### 1. Creative Brief (`creative_brief.yaml`)

The thematic DNA of the session. Not a plot — a lens.

```yaml
title: "..."
concept: >
  One paragraph. The core idea that everything gets read through.
core_tensions:
  - 2-3 tensions that the concept generates. These are oppositions,
    not plot points.
interpretive_angles:
  - 2-3 ways the Director should read situations through this concept.
    Each one is a metaphorical mapping (X as Y).
obsessions:
  - 2-3 recurring images or motifs the Director should gravitate toward.
charged_objects:
  - 2-4 specific objects that carry thematic weight.
```

The Director receives this alongside the Dreamer state and the style
guide. It shapes *how* the Director interprets every emotional state.

### 2. Style Extensions (`style_extensions.yaml`)

Session-specific vocabulary that extends (not replaces) the base style
guide:

```yaml
brief_phrases:
  materials: [session-specific material vocabulary]
  lighting: [session-specific lighting vocabulary]
  textures: [session-specific texture vocabulary]
negative_space:
  - things to never generate (what breaks this brief's logic)
capstone_templates:
  rehearsal: "The scene feels like..."
  reversal: "The scene feels like..."
  roving: "The scene feels like..."
  # one per goal type
```

### 3. Situations (in `world.yaml`)

Situations shaped by the brief, with indexing cues the Dreamer can
schedule against:

```yaml
situations:
  - id: s1_example
    description: "One sentence. What's unresolved."
    ripeness: 0.0-1.0
    activation: 0.0-1.0
    indices: [situation-specific cues for coincidence retrieval]
```

## How the Brief Flows Through the Architecture

```
creative_brief.yaml ──────────────────────────┐
style_extensions.yaml ────────────────────────┐│
                                              ││
world.yaml ──→ Dreamer ──→ dreamer state ──→ Director ──→ Stage
               (does not      │               (sees brief,
                see brief)    │                style guide,
                              │                and dreamer
                              │                state)
                              │
                    situations, goal types,
                    activation, emotion
```

Swap the brief, get a different dream. Same Dreamer, same Director,
same Stage. The thematic DNA is a parameter, not a structural change.

## Prototype Brief: Escape from New York palette

This brief is written against the existing `escape_new_york.v2-layered`
palette (8x8 grid, clay/stop-motion puppet animation, John Carpenter
analog synth). It's a prototype — meant to show how the format works
so David can write his own.

### `creative_brief.yaml`

```yaml
title: "The Puppet Knows"

concept: >
  The puppet is aware of its own materiality. It knows it's made of
  clay. It knows the buildings are foam-core and the fires are LEDs.
  And it keeps going anyway — not because it believes the world is
  real, but because the mission still feels real even though nothing
  else does. The horror is not the prison island. The horror is
  performing sincerity inside a set.

core_tensions:
  - the mission is urgent but the world is fake
  - defiance requires belief, but belief requires forgetting what
    you're made of
  - escape is the stated goal, but what you're escaping from is the
    knowledge that there's nothing on the other side of the wall
    except more set

interpretive_angles:
  - surfaces as confessions (every material — clay, foam, felt —
    is a confession that this world was built, not found)
  - performance as survival (Snake keeps performing the hero because
    stopping means confronting the set)
  - light as stage direction (every spotlight is the system telling
    the puppet where to stand)

obsessions:
  - fingerprints in clay (proof of the maker's hand, proof of
    artifice)
  - the cigarette that never burns down (a prop pretending to be
    a habit)
  - the wall that is also the edge of the set (there is no
    "outside" — there's only backstage)

charged_objects:
  - the micro-explosives in Snake's neck (the one real thing in a
    fake world — real stakes in a cardboard body)
  - the reel-to-reel tape (the information that's supposedly worth
    all this — but it's a prop too)
  - the Cadillac's working headlights (the only light source that
    isn't a stage light — the one thing that illuminates without
    directing)
  - the wrestling ring (the most honest place — here, at least,
    everyone knows they're performing)
```

### `style_extensions.yaml`

```yaml
brief_phrases:
  materials:
    - "visible clay seams"
    - "foam-core edge showing white interior"
    - "felt fabric bunching at the joints"
    - "fingerprint ridges catching the light"
    - "painted cardboard peeling at the fold"
  lighting:
    - "theatrical spot from directly above (stage lighting, not natural)"
    - "LED trash-fire glow (warm but obviously artificial)"
    - "backstage spill (light from somewhere the camera shouldn't see)"
  textures:
    - "clay skin with thumbprint texture"
    - "matte-painted sky that doesn't move"
    - "broken glass that's real among props that aren't"

negative_space:
  - no photorealistic textures (everything should read as handmade)
  - no natural sunlight (all light is placed, all light is direction)
  - no moments where the puppet forgets it's a puppet (no unselfconscious
    action — even running should feel like a puppet running)

capstone_templates:
  rehearsal: >
    The scene feels like practicing a rescue that everyone knows
    is made of cardboard.
  reversal: >
    The scene feels like discovering that the wall is the same
    material on both sides.
  rationalization: >
    The scene feels like a puppet explaining why it chose to keep
    moving.
  roving: >
    The scene feels like staring at the painted sky and pretending
    it has depth.
  recovery: >
    The scene feels like straightening a bent limb and continuing
    the scene.
  revenge: >
    The scene feels like tearing a piece of the set apart with
    clay hands.
  repercussions: >
    The scene feels like watching the foam-core dominos fall
    and knowing someone built each one.
```

### Situations (for `world.yaml`)

```yaml
situations:
  - id: s1_seeing_through
    description: >
      Snake notices the seams. The foam-core edge. The fingerprint
      in the clay. He can't unsee it but he can't say it.
    ripeness: 0.7
    activation: 0.4
    indices:
      - perception
      - awareness
      - seam
      - fingerprint
      - neg_emotion
    # about: the moment of noticing artifice (cognitive, internal)
    # distinct from s3 (spatial) and s4 (social)

  - id: s2_the_mission
    description: >
      The mission is urgent. The timer is real. The explosives are
      real. But the mission is happening inside a model.
    ripeness: 0.5
    activation: 0.3
    indices:
      - urgency
      - timer
      - explosive
      - sincerity
      - pos_emotion
    # about: genuine stakes in a fake world (motivation, forward drive)
    # the anchor that pulls against all three others

  - id: s3_the_edge
    description: >
      The wall is also the edge of the set. Beyond it there is no
      geography, only backstage. Escape is stepping into nothing.
    ripeness: 0.4
    activation: 0.1
    indices:
      - edge
      - void
      - backstage
      - darkness
      - neg_emotion
    # about: spatial limits (boundary, containment, the unknown)
    # distinct from s1 (perceptual) — this is about where the
    # world literally ends, not about noticing it's built

  - id: s4_the_ring
    description: >
      The wrestling ring. The only honest place. Here, performance
      is the stated purpose. Everywhere else it's a secret.
    ripeness: 0.3
    activation: 0.0
    indices:
      - ritual
      - combat
      - crowd
      - honesty
      - pos_emotion
    # about: acknowledged performance (social, relational)
    # distinct from s1 (private noticing) — here everyone
    # knows it's a show, and that's what makes it real
```

### How This Brief Changes What the Director Produces

Same Dreamer state — obsessing about s1_seeing_through in REVERSAL mode, high
tension. Without this brief, the Director might show Snake finding a
different exit. With it:

> *A clay figure stands in a corridor of foam-core walls. One wall has
> been peeled back, revealing white corrugated interior. Behind it:
> another wall, same material. The figure reaches for the edge of the
> second wall. Extreme close-up on clay fingers finding the same white
> corrugation underneath. Theatrical spot from directly above. The scene
> feels like discovering that the wall is the same material on both
> sides.*

The brief doesn't change the Dreamer's emotional logic. It changes what
the Director *sees* when it looks at that emotional logic.

### Dreamer narration examples (by goal type, for this brief)

These are templates the Director can draw from:

- **REHEARSAL:** "I go through the door. I find the president. I get to
  the bridge. I keep saying it like it matters."
- **REVERSAL:** "What if I'd looked at the wall instead of through it."
- **RATIONALIZATION:** "It doesn't matter that it's cardboard. The
  explosives are real. That's enough."
- **ROVING:** "There was a sky once. Painted, but it had depth if you
  didn't look too hard."
- **RECOVERY:** "My arm bent wrong. I bent it back. The scene continues."
- **REVENGE:** "I'll tear the set down. Not because it frees me. Because
  it's honest."
- **REPERCUSSIONS:** "If I pull this wall down, the next one is the same.
  And the next."

## Scaling (Future — Phase 5+)

Hand-authoring briefs is the right approach for Phase 2-3. You need to
write 2-3 manually to discover what a good brief looks like.

Eventually, prep agents (LLMs with creative system prompts) could
generate brief candidates from seed material (a palette, a mood, a
concept fragment). Different agents with different sensibilities would
produce different thematic readings of the same palette. A human curates
the output — the agent drafts, the human directs.

This is not on the critical path. It's the scaling answer for after you
know what good material looks like.

## Quality Gate: The Capstone Diagnostic

The capstone templates are the best test of whether a concept is strong
enough to drive a Director. Write all seven (one per goal type). If they
all sound like the brief's voice — distinct from each other but
recognizably from the same thematic world — the concept works. If some
goal types produce forced or generic sentences, the concept is too narrow.
This was discovered empirically across three briefs (Puppet Knows, Zone
Knows, Ship Becomes): the same cognitive posture (REVERSAL, REHEARSAL,
etc.) should read completely differently through each thematic lens. If
it doesn't, the lens isn't refracting enough.

## Open Questions

1. **How much of the brief does the Director actually use?** Start small
   (concept + 2-3 tensions + charged objects). See how much the Director
   can absorb before the prompt gets noisy.

2. **Do the situations work for the scheduler?** The indices on each
   situation need to produce interesting coincidence retrieval. This is
   discovered by running them against the scheduler, not by reading them.

3. **How do you start a new brief?** What's the seed? A palette alone?
   A mood word? A single image? A question? The prototype above started
   from the palette's material vocabulary (clay, foam-core, felt) and
   asked "what if the puppet noticed?"
