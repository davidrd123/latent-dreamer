# Traversal-First Sandbox Candidates

This note is for the problem Graffito exposed in live runtime:

> the engine can traverse, but the current world is too small and too
> clustered to show much movement, turnover, or action.

Graffito should stay a control slice.
It should not also be the traversal stress test.

The right next world is not "the richest source material."
It is the source material that gives the scheduler the most room to move.

## What The Runtime Actually Rewards

The current Python Dreamer already rewards:

- distinct `place` changes
- distinct `situation` changes
- different `goal_type` families
- event turnover
- local edges that provide multiple exits from a hot cluster
- node indices that are differentiated enough to retrieve different material

That means the best sandbox is one with:

- `5-7` places
- `4-6` situations
- `3-5` events
- `25-40` nodes
- multiple cross-situation drift paths
- at least one refuge zone and at least one spectacle zone

## Source Strategy

### Best: Movie-Shaped Source, Loosely Adapted

The ideal source is already organized around:

- set-piece geography
- scene changes
- thresholds
- pursuit / regroup / escape / confrontation
- charged objects that move between spaces

Use the source as scaffolding, not as something to stay faithful to.
The runtime wants a world skeleton, not literary loyalty.

### Good: Comic / Storyboard / Graphic-Novel Logic

These are often even better than books because they already think in:

- visual thresholds
- strong places
- charged objects
- movement beats
- spatial contrast

### Worse: Books

Books are not forbidden. They are just a worse starting point for this
specific test.

The engine does not need interior prose. It needs:

- places
- situations
- events
- recurring objects
- reasons to move

Many novels can be adapted into that, but they do not arrive pre-broken
into those units.

### Also Good: Original Brief Built From Movie Logic

This may be the best compromise.
Take a recognizable source pattern, then write an original world around it
so nothing is constrained by fidelity.

## Candidate A: Urban Pursuit

### Working Title

`The City Routes Around You`

### Source Family

Think: night pursuit films, runaway-mission films, city odyssey films,
warriors-through-neighborhoods logic.

Not one specific movie. More like a skeleton built from:

- train platforms
- rooftops
- bodegas / markets
- clubs / underpasses
- bridge checkpoints
- river edge / warehouse / safehouse

### Why It Is Strong

This is the most traversal-friendly option.

It naturally gives you:

- lots of place changes
- immediate motion
- repeated threshold crossings
- strong event turnover
- natural contrast between threat, concealment, spectacle, and refuge

It also gives the stage the best shot at feeling kinetic fast.

### Concept

A courier is moving something across the city at night.
The package matters, but what matters more is that every district reads the
courier differently. In one neighborhood he is prey, in another he is a
messenger, in another he is a trespasser, in another he is somebody's last
chance. The city is not just the background. It is routing him.

### Core Tensions

- urgency versus disorientation
- the mission says "get there" but the city keeps converting forward motion
  into detours
- every refuge is temporary and every spectacle can become exposure

### Suggested Places

- elevated platform
- alley market
- rooftop maze
- underpass / tunnel
- bridge checkpoint
- club interior
- warehouse river edge
- borrowed apartment / kitchen refuge

### Suggested Situations

- `s1_the_run`
  immediate pursuit, keeping momentum
- `s2_the_detour`
  route disruption, city rerouting the mission
- `s3_false_refuge`
  a temporary interior where safety is never stable
- `s4_public_spectacle`
  crowd / noise / performance / exposure
- `s5_the_threshold`
  bridge, gate, train edge, checkpoint, the place you must cross
- `s6_the_exchange`
  the package changes meaning or hands

### Suggested Events

- train doors close before the courier reaches them
- bridge locks down
- package swap / mistaken handoff
- blackout or siren event changes the whole route
- rooftop slip / near-fall

### Why The Engine Will Like It

- `place` can change almost every `1-3` cycles
- `roving`, `rehearsal`, `repercussions`, `recovery`, and `revenge` all have
  obvious jobs
- events can redistribute the whole run without requiring abstract inference
- the same city can be revisited from different emotional angles

### Main Risk

It can become generic "cool chase footage" unless the brief has one clean
through-line.

The fix is to make the package or message psychologically charged, not just
plot-important.

## Candidate B: Heist Spiral

### Working Title

`The Plan Keeps Leaking`

### Source Family

Think: procedural heist films, infiltration stories, jobs that go sideways,
crew-fracture stories.

### Why It Is Strong

This is the cleanest fit for the scheduler's goal families.

- `REHEARSAL` has a natural job: running the plan again
- `REVERSAL` has a natural job: alternate entries, alternate escapes
- `REPERCUSSIONS` has a natural job: what failed, who got exposed, what it
  costs now
- `RECOVERY` has a natural job: regrouping and salvaging the job

### Concept

A crew has memorized the job so thoroughly that they start living inside
versions of it before the vault is ever opened. Once the job slips, the
planned route does not disappear. It keeps haunting every alternative. The
heist is no longer a break-in. It is a pattern no one can stop running.

### Core Tensions

- precision versus improvisation
- group coordination versus private panic
- the plan promises control, but every contingency reveals how little
  control anyone had

### Suggested Places

- planning room
- service corridor
- stairwell
- vault / inner chamber
- getaway car
- diner / regroup point
- river tunnel / maintenance route
- safe house

### Suggested Situations

- `s1_the_plan`
  calm precision, route rehearsal, tool preparation
- `s2_the_entry`
  threshold crossing, stealth, compression
- `s3_the_slip`
  small failure that changes the run
- `s4_the_split`
  the crew fragments physically or psychologically
- `s5_the_aftermath`
  cost accounting, exposure, missing object, missing person
- `s6_the_do_over`
  impossible fantasy of re-running the job cleanly

### Suggested Events

- alarm chirp or camera pan catches movement
- tool failure / lock mismatch
- crew member vanishes or defects
- stash item is wrong or missing
- getaway route closes

### Why The Engine Will Like It

- the same architecture can be revisited with different goal families
- local edges can be very strong without becoming repetitive
- high event density with clear causal consequences
- excellent for measuring when `repercussions` actually pays off

### Main Risk

It may become too procedural and too legible.
That is good for debugging, but it can feel less dreamlike unless the
charged object / betrayal logic is strong.

## Candidate C: Mythic Night Journey

### Working Title

`The Night Market Remembers Your Name`

### Source Family

Think: dream-fairytale, night carnival, bathhouse / labyrinth / market /
shoreline logic.

### Why It Is Strong

This gives you the widest tonal range:

- enchantment
- dread
- bargain
- refuge
- pursuit
- return

It is best if you want the engine to feel less like "plot traversal" and
more like moving through changing symbolic zones.

### Concept

A traveler enters a night market where every district trades in something
intangible: names, debts, voices, shadows, future days. Movement through the
market feels voluntary until it becomes clear the market already knows what
the traveler came to lose. Every zone is seductive. Every bargain changes
what the next zone can see.

### Core Tensions

- wonder versus cost
- invitation versus entrapment
- self-transformation versus self-loss

### Suggested Places

- ferry landing
- lantern street market
- bathhouse / inn refuge
- carnival ring
- narrow forest path
- tower archive
- shoreline / tide flats
- gate of return

### Suggested Situations

- `s1_the_invitation`
  arrival, fascination, crossing into the system
- `s2_the_bargain`
  exchange, naming, accepting terms
- `s3_the_maze`
  movement through zones that are beautiful and disorienting
- `s4_the_refuge`
  temporary care, food, warmth, false safety
- `s5_the_hunt`
  something wants the traveler or what the traveler carries
- `s6_the_return`
  exit becomes possible but at a cost

### Suggested Events

- name token exchanged or lost
- mask or garment changes identity
- gate opens only once
- tide reversal / market flood
- feast / parade turns predatory

### Why The Engine Will Like It

- very strong `roving` and `reversal`
- lots of place mutation
- repeated returns can feel transformed without literal realism
- charged objects can carry the run

### Main Risk

This can drift into beautiful vagueness.
If the indices are not authored sharply, the engine may feel atmospheric but
not decisive.

## Recommendation

Build Candidate A first.

Not because it is the most profound brief, but because it is the best
tire-kicker for the current runtime.

Why:

- it is the most naturally kinetic
- it will show place mutation clearly in Scope
- it gives the scheduler many valid exits from any hot node cluster
- it makes repetition failures obvious
- it does not depend on subtle interpretive logic to feel alive

Candidate B is the best second fixture if the goal is to debug family logic.
Candidate C is the best later fixture if the goal is to prove symbolic range.

## Concrete Recommendation

If we want a traversal-first sandbox fast, author an original city-night
world with movie logic instead of adapting Graffito further.

Suggested package target:

- `6` places
- `5` situations
- `4` named events
- `28-32` nodes
- one charged object that changes meaning over the run
- at least two refuge nodes and two spectacle nodes
- at least three cross-situation drift edges

## Next Note

If this direction is right, the next artifact should be:

`23-brief-city-night-pursuit.md`

That note should define:

- `creative_brief.yaml`
- `style_extensions.yaml`
- recommended `world.yaml`
- target graph shape
- milestone trace expectations
