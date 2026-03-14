# Creative Brief: The City Routes Around You

Palette: `escape_new_york.v2-layered` (8x8 grid, handmade night-city
miniatures, sodium-vapor glow, clay-and-cardboard street infrastructure,
analog-synth pursuit energy)

This brief is not trying to be the most profound flagship piece.
It is a traversal-first sandbox built to stress the current runtime:

- more places
- more thresholds
- more rerouting
- more event turnover
- more obvious movement on stage

It should reuse the visual strengths already proven in the
`escape_new_york.v2-layered` palette while dropping the meta-theatrical
burden of `The Puppet Knows`.

---

## `creative_brief.yaml`

```yaml
title: "The City Routes Around You"

concept: >
  A courier is carrying something small but decisive across the city at
  night. The package matters, but what matters more is that every district
  reads the courier differently. In one neighborhood he is prey, in another
  he is a messenger, in another he is an intruder, in another he is the one
  person who might still arrive in time. The city is not just the background
  of the mission. It is an active routing intelligence. Every siren, train
  closure, crowd surge, locked gate, rooftop shortcut, and borrowed kitchen
  table changes what the package means and where the courier can go next.
  The drama is not just "get across town." The drama is that the route keeps
  rewriting the mission while pretending to help.

core_tensions:
  - urgency versus disorientation (the courier must keep moving, but the
    city keeps converting straight lines into detours)
  - exposure versus shelter (the places that hide you also slow you down,
    and the places that move you forward also reveal you)
  - carrying the package versus becoming the package (what is being moved
    starts to feel less important than who is now readable because of it)

interpretive_angles:
  - the city as switchboard (bridges, platforms, alleys, and rooftops are
    routing logic made physical — the city keeps deciding what connects to
    what)
  - light as jurisdiction (different neighborhoods claim the courier through
    different light signatures: train fluorescents, kitchen tungsten, club
    strobes, bridge sodium)
  - crowds as weather (the crowd is never neutral background; it is wind,
    current, concealment, drag, and sudden exposure)

obsessions:
  - the silver package case (small enough to carry under one arm, bright
    enough to catch every light source that wants to identify it)
  - the last train doors sliding shut (the city sealing one route and
    forcing another)
  - the borrowed kitchen table (the only horizontal surface in the whole
    run where the package can be set down and looked at)

charged_objects:
  - the silver package case (the thing everyone thinks they want, though it
    may only be a key that tells the route where to tighten)
  - the dead pager that sometimes lights anyway (instructions arrive late,
    partial, or from the wrong sender)
  - the bridge wristband (proof that the courier belonged to another route
    earlier in the night)
  - the apartment key on red string (temporary refuge made portable, but
    only once)
```

## `style_extensions.yaml`

```yaml
brief_phrases:
  materials:
    - "wet asphalt built from glossy black painted board"
    - "chain-link fence made from bent wire and silver thread"
    - "fly-posters layered like torn paper sediment on brick flats"
    - "subway tiles painted by hand, uneven and nicotine-stained"
    - "rooftop gravel as crushed charcoal and card dust"
  lighting:
    - "sodium-vapor bridge orange (everything feels claimed and exposed)"
    - "subway fluorescent flicker (institutional, greenish, impatient)"
    - "club strobe and neon bleed (movement broken into fragments)"
    - "borrowed kitchen tungsten pool (the one pocket of domestic steadiness)"
    - "headlight sweep across wet walls (the route briefly illuminated by someone else)"
  textures:
    - "rain sheen on cardboard pavement"
    - "spray-paint mist caught in backlight"
    - "poster-glue wrinkles and bubbled paper edges"
    - "steam vent haze crossing the frame in strips"

negative_space:
  - no open daylight (this world should feel entirely nocturnal and route-dependent)
  - no clean corporate interiors (every space should feel borrowed, improvised, claimed, or worn)
  - no empty establishing shots that stop the run cold (even calm scenes should imply route pressure)

capstone_templates:
  rehearsal: >
    The scene feels like running the route again in your head while your
    body is already late.
  reversal: >
    The scene feels like the shortcut revealing why it was closed in the
    first place.
  rationalization: >
    The scene feels like telling yourself this detour was always the plan.
  roving: >
    The scene feels like slipping into another block's rhythm and borrowing
    its anonymity for a minute.
  recovery: >
    The scene feels like finding one open line through the city after three
    good ones collapsed.
  revenge: >
    The scene feels like shoving back against a route that keeps trying to
    choose for you.
  repercussions: >
    The scene feels like realizing one missed transfer changed the whole
    city's opinion of where you belong.
```

## Recommended `world.yaml`

This should be the first traversal-first fixture that is optimized for
runtime behavior rather than symbolic purity.

```yaml
title: "The City Routes Around You"
source_palette: "/Users/daviddickinson/Projects/Lora/scope-drd/content/palettes/escape_new_york.v2-layered.palette.yaml"
creative_brief_path: "./creative_brief.yaml"
style_extensions_path: "./style_extensions.yaml"
brief_ref: "/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/experiential-design/23-brief-city-night-pursuit.md"

characters:
  - id: courier
    name: The Courier
  - id: runner
    name: The Runner
  - id: hostess
    name: The Hostess
  - id: crowd
    name: The Crowd

places:
  - id: platform
    description: "The elevated train platform where missed timing becomes route destiny."
  - id: market
    description: "The alley market, crowded enough to hide you and trap you at the same time."
  - id: rooftop
    description: "The rooftop maze where the city becomes ladders, tar, fences, and air gaps."
  - id: tunnel
    description: "The underpass and service tunnels where sound carries farther than sight."
  - id: club
    description: "The public interior where spectacle and concealment temporarily become the same thing."
  - id: bridge
    description: "The checkpoint crossing where every route narrows into inspection."
  - id: apartment
    description: "The borrowed kitchen refuge where the package can be set down exactly once."
  - id: warehouse
    description: "The river-edge warehouse where the run stops being transit and becomes exchange."

events:
  - id: e1_train_missed
    description: "The last train doors close before the courier reaches them."
  - id: e2_blackout_siren
    description: "A sudden blackout and siren run reroute the whole neighborhood."
  - id: e3_wrong_handoff
    description: "The package is nearly handed to the wrong person under the wrong light."
  - id: e4_bridge_lockdown
    description: "The bridge checkpoint seals and forces a rooftop/tunnel workaround."

situations:
  - id: s1_the_run
    description: >
      The courier is in motion and must stay in motion. Timing is the only
      thing that still feels objective. Every pause risks losing the route.
    ripeness: 0.66
    activation: 0.34
    place: platform
    anger: 0.08
    grief: 0.06
    hope: 0.58
    threat: 0.42
    waiting: 0.74
    inferred: false
    external: true
    indices:
      - movement
      - urgency
      - timing
      - train
      - mission
      - route
      - emotion:pos

  - id: s2_the_detour
    description: >
      The clean route has failed. The city now offers side paths: market
      cuts, tunnels, fence hops, roof gaps. Progress continues, but no
      longer cleanly.
    ripeness: 0.58
    activation: 0.26
    place: market
    anger: 0.10
    grief: 0.10
    hope: 0.30
    threat: 0.48
    waiting: 0.22
    inferred: true
    external: true
    indices:
      - detour
      - reroute
      - crowd
      - alley
      - workaround
      - friction
      - emotion:neg

  - id: s3_false_refuge
    description: >
      There is a temporary interior where the package can be set down, a
      drink can be poured, a map can be unfolded, and the route can be
      reconsidered. It is real refuge, but only briefly.
    ripeness: 0.48
    activation: 0.14
    place: apartment
    anger: 0.04
    grief: 0.12
    hope: 0.64
    threat: 0.18
    waiting: 0.40
    inferred: false
    external: false
    indices:
      - refuge
      - kitchen
      - table
      - borrowed
      - breath
      - map
      - emotion:pos

  - id: s4_public_spectacle
    description: >
      The city becomes loud, rhythmic, and watchful. Clubs, crowds, passing
      headlights, amplified music, and bodies in motion create temporary
      invisibility that can flip into exposure without warning.
    ripeness: 0.44
    activation: 0.12
    place: club
    anger: 0.10
    grief: 0.04
    hope: 0.38
    threat: 0.36
    waiting: 0.20
    inferred: false
    external: true
    indices:
      - crowd
      - music
      - spectacle
      - exposure
      - light
      - pulse
      - emotion:pos

  - id: s5_the_threshold
    description: >
      Every route narrows eventually: bridge checkpoint, locked gate,
      platform edge, fence line, roof gap. The city asks for proof right at
      the point of crossing.
    ripeness: 0.62
    activation: 0.22
    place: bridge
    anger: 0.12
    grief: 0.08
    hope: 0.22
    threat: 0.64
    waiting: 0.18
    inferred: true
    external: true
    indices:
      - threshold
      - bridge
      - checkpoint
      - gate
      - crossing
      - inspection
      - emotion:neg

  - id: s6_the_exchange
    description: >
      The run stops being just movement and becomes a question of transfer:
      who receives the package, who faked the route, and whether the object
      being carried is even the real thing.
    ripeness: 0.40
    activation: 0.06
    place: warehouse
    anger: 0.16
    grief: 0.10
    hope: 0.26
    threat: 0.44
    waiting: 0.12
    inferred: true
    external: false
    indices:
      - exchange
      - package
      - handoff
      - mistake
      - identity
      - river
      - emotion:neg
```

## Target Graph Shape

This fixture should be bigger than Graffito on purpose.

### Package target

- `28-32` nodes
- `6-8` places
- `6` situations
- `4` named events
- `30-40` edges

### Recommended node clusters

1. `run / platform cluster` (`5` nodes)
   - missed doors
   - platform sprint
   - fluorescent waiters
   - track-edge hesitation
   - train leaving reflection

2. `market / detour cluster` (`5` nodes)
   - alley squeeze
   - produce-cart bottleneck
   - tarp corridor
   - wrong turn through steam
   - fence-jump setup

3. `rooftop / threshold cluster` (`5` nodes)
   - ladder climb
   - roof gap
   - sodium bridge view
   - wire fence scrape
   - checkpoint seen from above

4. `club / spectacle cluster` (`4-5` nodes)
   - strobe crowd swallow
   - back-room cut-through
   - stage-light freeze
   - bathroom mirror recalibration
   - side-door exit

5. `apartment / false refuge cluster` (`4-5` nodes)
   - kitchen table set-down
   - sink water / breath return
   - map spread under tungsten
   - red-string key on hook
   - window watch

6. `warehouse / exchange cluster` (`5-6` nodes)
   - river-edge loading bay
   - wrong receiver silhouette
   - silver case under headlight sweep
   - almost-handoff
   - corrected transfer
   - aftermath exit path

### Edge design rules

- every cluster should have at least one strong internal continuity edge
- every cluster should have at least one exit into another situation
- `s3_false_refuge` must have at least two exits:
  - back into `s1_the_run`
  - forward into `s5_the_threshold` or `s6_the_exchange`
- `s4_public_spectacle` should support both concealment and exposure
- no single event should monopolize more than one cluster

## Milestone Trace Expectations

For an `18`-cycle run, the first benchmark should look something like:

1. `s1_the_run` establishes the route pressure early
2. an event forces `s2_the_detour` by around cycle `4-6`
3. `s3_false_refuge` appears by around cycle `6-9`
4. `s5_the_threshold` becomes unavoidable by around cycle `8-12`
5. `s6_the_exchange` appears late, not necessarily as the ending
6. at least one return from refuge back into motion feels costly

### Mechanical acceptance bars

- at least `4` places visited in `18` cycles
- at least `8-10` unique nodes visited
- no single node dominating more than `3` cycles
- at least `2` named events appearing
- at least one cross-situation return after refuge

### What failure looks like

- the run stays trapped in one location cluster
- `s3_false_refuge` never appears
- the bridge/checkpoint never exerts pressure
- the exchange fires too early and collapses the route
- the city feels like shuffled clips instead of route logic

## Why This Is The Right Next Fixture

Compared with `Graffito`, this world gives the scheduler:

- more places to mutate through
- more obvious event-driven rerouting
- clearer reasons for `roving`, `repercussions`, and `recovery`
- a refuge zone that actually costs something
- a threshold zone that can repeatedly tighten the route

Compared with `The Puppet Knows`, this world keeps the palette's city-night
strengths while removing the interpretive burden of metafiction.

The question here is not "is this the deepest brief?"
It is:

> does the current runtime produce interesting movement-of-mind when the
> world actually has enough room to move?

This is the brief that should answer that.
