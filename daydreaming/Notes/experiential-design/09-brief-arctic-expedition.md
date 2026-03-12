# Creative Brief: The Ship Becomes the Place

Palette: `arctic_expedition` (8x8 grid, brass-and-tin ship, rock candy
ice, felt sailors in wool coats, resin sea, golden hour/overcast/aurora)

---

## `creative_brief.yaml`

```yaml
title: "The Ship Becomes the Place"

concept: >
  The ship was a vehicle. Then the ice closed and it became a home.
  Soon it will be a coffin. The expedition was about what's out there
  — the passage, the pole, the discovery. But the ice stopped all
  forward motion and now the expedition is about what's in here: the
  ship, the crew, the materials, the daily maintenance of something
  that isn't going anywhere. The tragedy is not being trapped. The
  tragedy is that the ship has become the most interesting place in
  the world precisely because you can't leave it.

core_tensions:
  - the ship was built to move but now it's the only stable thing
    in a landscape that shifts
  - the crew maintains the ship as if it will sail again, and this
    maintenance is both delusion and survival
  - the ice is destroying the ship and the ice is the most beautiful
    thing any of them have ever seen

interpretive_angles:
  - brass as stubbornness (every polished fitting is an act of
    defiance against the ice — you keep the ship bright because
    admitting decay means admitting you're staying)
  - ice as architecture (the pressure ridges, the floes, the bergs
    are buildings in a city that rearranges itself nightly — the
    crew lives inside a landscape that designs itself)
  - felt as warmth that isn't enough (wool coats, stitched tents,
    cotton batting insulation — every soft material is trying to
    hold heat against a force that doesn't care)

obsessions:
  - the brass chronometer (still running, still accurate — measuring
    time in a place where time has stopped mattering)
  - breath made visible (cotton-puff wisps on wire — every exhale
    is the body confessing that it's warm inside and cold outside)
  - the pressure ridge that grows overnight (the ice is building
    something, and the crew wakes up to find new architecture
    outside the porthole)

charged_objects:
  - the ship's bell (rung on the hour, every hour — the one sound
    the crew makes that the ice can't muffle)
  - the expedition journal (still being written in, still recording
    observations — the mission continues on paper even though it
    stopped on water)
  - the aurora overhead (cellophane strips on invisible threads —
    the only light that isn't the crew's, the only beauty that
    isn't made of ship materials)
  - the sledge dogs' harnesses (hanging empty on the rail — the dogs
    are alive but there's nowhere to go)
```

## `style_extensions.yaml`

```yaml
brief_phrases:
  materials:
    - "brass fittings dulled by frost then polished bright again"
    - "rock candy ice fracturing under pressure"
    - "crumpled aluminum foil painted matte white as snow"
    - "felt wool coats with visible stitching"
    - "cotton-puff breath on fine wire"
  lighting:
    - "golden hour through ice (warm light filtered through cold
      material — the sun is there but can't reach)"
    - "overcast diffuse pale blue (the default light of being stuck
      — flat, even, no drama, no shadows)"
    - "aurora neon green and violet glow on resin ice (the landscape
      lit from above by something nobody controls)"
    - "LED porthole warmth spilling onto ice (the ship's light
      claiming a small circle of the frozen world)"
  textures:
    - "translucent blue resin cracking under compression"
    - "hand-carved wave textures frozen still"
    - "cotton-thread rigging stiff with ice crystals"

negative_space:
  - no open water (the sea is solid — every surface is walkable,
    which means there's no boundary between ship and world)
  - no arrival (nobody reaches anything — the brief is about stasis,
    not discovery)
  - no warmth that lasts (every warm moment — golden hour, porthole
    glow, polished brass — is temporary and the cold always returns)

capstone_templates:
  rehearsal: >
    The scene feels like practicing a departure that requires
    water where there is only ice.
  reversal: >
    The scene feels like remembering open sea and not being sure
    if the memory is accurate.
  rationalization: >
    The scene feels like polishing the brass because someone
    should, and that's reason enough.
  roving: >
    The scene feels like watching the aurora and forgetting,
    briefly, that you can't leave.
  recovery: >
    The scene feels like the ice shifting and a lane of dark water
    appearing for one hour before closing again.
  revenge: >
    The scene feels like striking the ice with a brass fitting
    and knowing the ice doesn't notice.
  repercussions: >
    The scene feels like the pressure ridge growing another foot
    overnight and the hull groaning in response.
```

## Situations (for `world.yaml`)

```yaml
situations:
  - id: s1_the_ship
    description: >
      The ship is frozen in place. It creaks. The crew maintains it.
      Every repair is both practical and ritual — keeping the ship
      alive keeps them alive.
    ripeness: 0.7
    activation: 0.4
    indices:
      - ship
      - maintenance
      - brass
      - ritual
      - pos_emotion
    # about: the daily practice of caring for something that isn't
    # going anywhere. Pos_emotion because the maintenance itself is
    # sustaining — it gives the crew purpose.

  - id: s2_the_ice
    description: >
      The ice is a landscape, a force, an architect. It builds pressure
      ridges overnight. It cracks and reforms. It is beautiful and it
      is crushing the hull.
    ripeness: 0.6
    activation: 0.3
    indices:
      - ice
      - pressure
      - beauty
      - architecture
      - neg_emotion
    # about: the thing that trapped them is also the most extraordinary
    # thing they've ever witnessed. Neg_emotion because the beauty is
    # inseparable from the threat.

  - id: s3_the_wait
    description: >
      Time has changed shape. The chronometer still works but what it
      measures has become abstract. Days are marked by the bell. The
      journal fills with observations of nothing changing.
    ripeness: 0.4
    activation: 0.1
    indices:
      - time
      - measurement
      - journal
      - stillness
      - neg_emotion
    # about: the experience of endurance without progress. Distinct
    # from s1 (which has purpose through maintenance) — this is the
    # raw experience of time passing without forward motion.

  - id: s4_the_horizon
    description: >
      Sometimes the light does something extraordinary — aurora, golden
      hour through ice, a crack in the overcast. The crew stops and
      looks. For a moment the ice field is not a prison but a cathedral.
    ripeness: 0.3
    activation: 0.0
    indices:
      - light
      - aurora
      - horizon
      - wonder
      - pos_emotion
    # about: beauty that arrives uninvited. The only situation where
    # the crew isn't doing anything — they're just receiving. Lowest
    # ripeness because it can't be pursued, only encountered.

  - id: s5_the_hull
    description: >
      The pressure ridge is growing. The hull is groaning. Everyone
      hears it at night. Nobody mentions it at breakfast. The ship
      might hold. The ship might not.
    ripeness: 0.5
    activation: 0.2
    indices:
      - hull
      - pressure
      - collapse
      - silence
      - neg_emotion
    # about: the threat that everyone knows about and nobody discusses.
    # Shares 'pressure' index with s2_the_ice — coincidence retrieval
    # should link them, but s5 is about the ship's response to the
    # ice, not the ice itself.
```

## How This Brief Differs from the Other Two

| Dimension | Puppet Knows | Zone Knows | Ship Becomes |
|---|---|---|---|
| Core mode | Stasis by choice (keep performing) | Forward into danger (approaching the Room) | Stasis by force (ice won't let go) |
| Awareness | Character sees through world | World sees through character | Neither — both are just enduring |
| Movement | None (doorways, corridors) | Linear (approach, enter, arrive) | Frozen (the ship doesn't move) |
| Emotional center | Cynicism → acceptance | Dread → self-knowledge | Routine → beauty → dread |
| What's beautiful | Nothing (the set is ugly on purpose) | The Zone's honesty | The ice that's killing them |
| Dominant material | Clay, foam-core (dry, solid) | Ink, washi (fluid, dissolving) | Brass, ice, felt (cold, bright, soft) |
| Relationship to time | Timeless (no before/after) | Journey (approaching destination) | Stuck (time passes but nothing changes) |

## Format Observations (Across Three Briefs)

Three briefs against three very different palettes. The format holds.
The consistent structure — concept, tensions, angles, obsessions,
charged objects, style extensions, capstone templates, situations — works
across:

- **Cynical meta-awareness** (Puppet Knows)
- **Spiritual dread** (Zone Knows)
- **Material endurance** (Ship Becomes)

The capstone templates are the most revealing test. Each goal type
produces a recognizably different sentence across all three briefs,
which means the brief is actually doing its job: the same cognitive
posture (REVERSAL, REHEARSAL, etc.) reads completely differently through
each thematic lens.

The situations are where brief-specific tuning matters most. Index
overlap between situations should be low (distinct conceptual territory)
with 1-2 deliberate shared indices to enable coincidence retrieval across
situation boundaries (s2/s5 sharing `pressure` in this brief, s1/s4
sharing nothing but connected through the crew's relationship to beauty
and maintenance).
