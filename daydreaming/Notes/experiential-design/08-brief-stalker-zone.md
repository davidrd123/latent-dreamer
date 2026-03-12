# Creative Brief: The Zone Knows What You Want

Palette: `stalker_zone` (8x8 grid, ink-brush figures on washi paper,
Tarkovsky's Zone, indigo/overcast/golden hour, calligraphic style)

---

## `creative_brief.yaml`

```yaml
title: "The Zone Knows What You Want"

concept: >
  The Zone is the place where desire becomes legible. You entered it
  to reach the Room — the place that grants your deepest wish. But the
  Zone doesn't care about the wish you'd choose. It reads the wish you
  can't say. The landscape itself rearranges around what you actually
  want, not what you claim to want. The horror is not danger. The horror
  is that the Zone is honest about you. Every path it offers, every
  trap it sets, every room it opens is a sentence about what you desire
  and can't admit.

core_tensions:
  - the stated mission (reach the Room) versus the real mission (delay
    reaching the Room, because the Room will reveal what you actually
    want)
  - the Zone is dangerous but the danger is a mercy — it gives you a
    reason not to arrive
  - faith in the Zone requires admitting the Zone can read you, which
    means admitting there's something to read

interpretive_angles:
  - landscape as handwriting (the Zone's paths, puddles, overgrowth
    are calligraphy — the terrain is writing a sentence about whoever
    is walking through it)
  - traps as gifts (every obstacle the Zone places is protecting you
    from arriving too soon at the truth about yourself)
  - water as legibility (still water reflects; moving water obscures —
    the Zone uses water to control how much you can see of yourself)

obsessions:
  - the thrown bolt with its cloth tail (the only way to find the path
    is to throw something ahead and watch where the Zone accepts it)
  - the threshold that reconfigures (you passed through it already but
    looking back it's different — the Zone rewrote the door after you)
  - ink pooling in low places (the landscape's material — indigo ink —
    collects where desire is strongest)

charged_objects:
  - the metal bolt wrapped in cloth (the guide's tool — you throw it
    to ask the Zone a question, and where it lands is the answer)
  - the black rotary phone (sits in the ruins, connected to nothing —
    or connected to the Room, which is the same thing)
  - the rusted railway handcar (the vehicle that brought you to the
    Zone's edge, now overgrown — you can't go back the way you came)
  - the sand dunes inside the waiting room (the Zone has been exhaling
    into this building for years — desire accumulated as sediment)
```

## `style_extensions.yaml`

```yaml
brief_phrases:
  materials:
    - "ink-brush strokes dissolving into wet washi"
    - "pooled indigo ink in concrete depressions"
    - "rust-brown calligraphic lines on pale ground"
    - "torn green tissue paper as overgrowth"
    - "visible fiber texture of the paper ground"
  lighting:
    - "overcast diffuse blue (the Zone's default — no shadows, no hiding)"
    - "golden hour through gaps in ruin (the Zone offering warmth)"
    - "chiaroscuro in tunnels (the Zone forcing you to choose between
      light and dark)"
  textures:
    - "shattered concrete with rebar ink lines"
    - "paper-dust sand filling interior spaces"
    - "watercolor washes bleeding past their edges"

negative_space:
  - no clear skies (the Zone is always overcast or filtered — direct
    sun would make everything too legible too fast)
  - no technology that works (phones don't connect, trains don't run,
    lights flicker — the Zone has digested all mechanisms)
  - no hurrying (even when danger is present, the Zone's pace is
    meditative — rush and you miss what it's saying)

capstone_templates:
  rehearsal: >
    The scene feels like practicing how to ask for something
    you're not allowed to name.
  reversal: >
    The scene feels like discovering the path you took was the
    Zone's answer, not your choice.
  rationalization: >
    The scene feels like explaining to yourself why you haven't
    turned back yet.
  roving: >
    The scene feels like the Zone relaxing its attention on you
    for a moment — everything is just landscape, just beautiful.
  recovery: >
    The scene feels like finding a way forward that the Zone
    left open, knowing you'd take it.
  revenge: >
    The scene feels like throwing the bolt and refusing to follow
    where it lands.
  repercussions: >
    The scene feels like watching the Zone rearrange behind you
    because of what you just admitted.
```

## Situations (for `world.yaml`)

```yaml
situations:
  - id: s1_the_approach
    description: >
      The travelers are still outside the Zone, preparing to cross.
      Every preparation is also a delay. Every delay is also relief.
    ripeness: 0.5
    activation: 0.3
    indices:
      - threshold
      - preparation
      - delay
      - anticipation
      - neg_emotion
    # about: the tension between wanting to enter and wanting a reason
    # not to. Forward motion that keeps finding reasons to pause.

  - id: s2_the_path
    description: >
      Inside the Zone. The landscape responds to whoever walks through
      it. The guide throws bolts to find the safe path. But "safe" means
      "the path the Zone wants you on."
    ripeness: 0.6
    activation: 0.4
    indices:
      - navigation
      - trust
      - reading
      - ink
      - pos_emotion
    # about: the act of following — trusting the guide, trusting the
    # Zone, trusting yourself to interpret what the landscape is saying.
    # Pos_emotion because being inside the Zone is, despite everything,
    # where the Stalker feels alive.

  - id: s3_the_ruins
    description: >
      The Zone has swallowed buildings, vehicles, infrastructure.
      Everything human is being slowly digested. Sand fills waiting
      rooms. Vines cover tracks. The Zone is patient.
    ripeness: 0.4
    activation: 0.1
    indices:
      - ruin
      - sediment
      - patience
      - digestion
      - neg_emotion
    # about: what happens to human structures when desire has been
    # working on them for years. The ruins aren't dead — they're
    # composting into the Zone's vocabulary.

  - id: s4_the_room
    description: >
      The Room at the center. Nobody has entered it in this session.
      Its pull is gravitational. Its promise is that it will grant your
      deepest wish — the real one, not the one you'd choose.
    ripeness: 0.8
    activation: 0.0
    indices:
      - center
      - wish
      - honesty
      - fear
      - pos_emotion
    # about: the destination that nobody actually wants to reach.
    # Highest ripeness, zero activation — it's always pulling but
    # never visited. Pos_emotion because the Room's promise is
    # genuine even though it's terrifying.

  - id: s5_the_guide
    description: >
      The Stalker himself. His faith in the Zone is the only thing
      holding the group together. If he stops believing, there's no
      reason to be here. His belief is sincere and it might be wrong.
    ripeness: 0.5
    activation: 0.2
    indices:
      - faith
      - guide
      - sincerity
      - fragility
      - pos_emotion
    # about: the person whose belief makes the system work.
    # The Stalker is the Dreamer's avatar — his emotional relationship
    # to the Zone IS the emotional scheduling.
```

## How This Brief Differs from "The Puppet Knows"

| Dimension | Puppet Knows | Zone Knows |
|---|---|---|
| Core tension | Performing sincerity in a fake world | Avoiding honesty in a truthful landscape |
| Relationship to environment | The puppet sees through the set | The Zone sees through the traveler |
| Direction of awareness | Character → world (puppet notices artifice) | World → character (landscape reads desire) |
| Emotional register | Cynical acceptance → exhausted resolve | Reverent dread → delayed self-knowledge |
| Movement | Static (the puppet stands in doorways) | Forward (the path leads somewhere) |
| Dominant material | Clay, foam-core, felt (solid, graspable) | Ink, washi, watercolor (fluid, dissolving) |
| What the Director reads through | The gap between material and pretense | The gap between stated and actual desire |

The format works for a completely different tonal register. The same
fields (concept, tensions, angles, obsessions, charged objects, style
extensions, capstone templates, situations) produce a coherent brief
whether the palette is cynical-meta or spiritual-contemplative.
