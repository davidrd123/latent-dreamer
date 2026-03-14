# Arctic As Control Fixture

`Puppet Knows` is the artistic flagship. It proves that the system can
carry a distinctive, high-concept register.

It is not the clearest control case for understanding the machinery.

If the immediate question is:

> what setup makes it easiest to reason about what the Dreamer is doing,
> and whether the machinery itself is producing interesting dynamics?

the best next full fixture should be `Arctic Expedition`.

## Why Arctic Is Clearer

The brief is simpler in the right way.

- `Puppet Knows` is metafictional. The brief itself does a lot of cognitive
  work: fake world, admitted performance, backstage, the ring as honesty.
- `Arctic Expedition` is materially concrete. The tensions are immediately
  legible: trapped ship, beautiful ice, passing time, hull pressure,
  horizon relief.
- The emotional axes are easier to read:
  - `pressure / waiting / dread`
  - `maintenance / purpose`
  - `wonder / relief`

That makes it a better control case for the machinery:

- `REHEARSAL` has a clear job:
  practicing departures or maintenance routines that probably will not
  happen.
- `ROVING` has a clear job:
  escaping into aurora / horizon / wonder.
- `REPERCUSSIONS` has a clear job:
  listening to the hull groan and imagining collapse.
- `RATIONALIZATION` has a clear job:
  telling yourself the brass still matters because someone has to polish it.

If the system behaves well here, it is because the mechanics are working,
not because the brief itself is doing interpretive heavy lifting.

## What "Minimum Full Fixture" Means

For Arctic to be a real control fixture, it needs the same package shape as
`Puppet Knows`:

1. `creative_brief.yaml`
2. `style_extensions.yaml`
3. `world.yaml`
4. `dream_graph.json`

Right now only `Puppet Knows` has the full package in `scope-drd`.
This note defines the minimum viable Arctic package that is worth building.

## Package Goal

The first Arctic fixture should not try to be the most poetic version of
the brief. It should be optimized for:

- clear situation competition
- clear read on `ROVING`
- clear read on return pressure after wonder
- easy interpretation in the trace visualizers

In other words: it should be the easiest fixture to debug and trust.

## Recommended Situation Set

Keep the five situations from the brief. They are already balanced and
legible.

- `s1_the_ship`
  maintenance, brass, ritual, purpose
- `s2_the_ice`
  beauty, pressure, architecture, threat
- `s3_the_wait`
  time, measurement, journal, stillness
- `s4_the_horizon`
  aurora, light, wonder, relief
- `s5_the_hull`
  pressure, collapse, silence, dread

This is a good control set because each situation has a distinct emotional
job:

- `s1` keeps the crew functioning
- `s2` externalizes the sublime threat
- `s3` expresses stagnation
- `s4` provides the roving escape valve
- `s5` localizes danger into the ship itself

## Recommended Initial Balance

The first Arctic world should avoid a single dominant attractor like
`s1_seeing_through` in early Puppet Knows runs.

Recommended starting values:

```yaml
s1_the_ship:
  ripeness: 0.62
  activation: 0.28

s2_the_ice:
  ripeness: 0.58
  activation: 0.24

s3_the_wait:
  ripeness: 0.50
  activation: 0.18

s4_the_horizon:
  ripeness: 0.24
  activation: 0.00

s5_the_hull:
  ripeness: 0.56
  activation: 0.26
```

That gives:

- early pressure from `s1`, `s2`, and `s5`
- real but not overwhelming stagnation from `s3`
- a clearly dormant wonder-state in `s4`

## Deliberate Index Overlap

The fixture should use low overlap with only a few intentional bridges.

Keep these:

- `pressure`: shared by `s2_the_ice` and `s5_the_hull`
- `light`: shared indirectly by ship illumination and horizon phenomena
- `time`: shared by `s1_the_ship` and `s3_the_wait`

Avoid broad index blur. Arctic works as a control case because each
situation should remain conceptually crisp.

## Minimum Graph Shape

The first graph does not need to be large. It needs to be readable.

Target: `16-20` nodes, grouped into four small clusters:

1. `Ship maintenance cluster`
   - brass polishing
   - bell on the hour
   - journal entry
   - porthole warmth

2. `Ice / hull pressure cluster`
   - ridge outside the porthole
   - hull groan at night
   - pressure line climbing the hull
   - camp tools on the ice

3. `Wait / temporal drift cluster`
   - chronometer still running
   - breakfast silence
   - same corridor, different frost
   - deck routine repeated

4. `Horizon / aurora cluster`
   - aurora watch
   - overcast break
   - dark-water lane appears
   - distant ice cathedral

## Minimum Compatibility Set

Use the same compatibility statuses already proven useful in Puppet Knows:

- `present_compatible`
- `anticipated_future`
- `alternative_past`
- `alternative_present`
- `projected_consequence`

For Arctic, these should map cleanly:

- `REHEARSAL`
  maintenance / departure / rescue fantasies in `anticipated_future`
- `ROVING`
  horizon / aurora / relief in `alternative_present`
- `RATIONALIZATION`
  maintenance-as-purpose in `present_compatible`
- `REPERCUSSIONS`
  hull / ridge / collapse in `projected_consequence`
- `REVERSAL`
  remembered water, imagined thaw, or open-sea alternatives

## What Makes Arctic A Good Tire-Kicker

The control milestones are easy to understand:

1. pressure wins early
2. waiting accumulates
3. roving or wonder interrupts
4. the system returns to the trapped ship

If that shape appears, the machinery is readable.

If it does not appear, the problem is likely the machinery rather than the
brief.

## Recommended First Benchmark Milestones

For the first full Arctic fixture, the milestone table should be simpler
than Puppet Knows:

- `first_s5_hull`
- `first_roving`
- `first_s4_horizon`
- `first_return_to_ship_after_horizon`

Optional if a darker path emerges naturally:

- `first_collapse_projection`

These are easier to judge than Puppet Knows because they describe a very
readable emotional rhythm:

`pressure -> stasis -> wonder -> return`

## What To Build First

The minimum useful sequence is:

1. scaffold the fixture folder in `scope-drd`
2. write `world.yaml`
3. write `creative_brief.yaml`
4. write `style_extensions.yaml`
5. hand-author a `16-20` node `dream_graph.json`
6. run the existing Python and kernel machinery against it unchanged

If Arctic immediately produces a clearer trace than Puppet Knows, that tells
us something important:

- the machinery was probably better than the flagship fixture made it look

If Arctic is just as muddy, the scoring/retrieval logic needs attention.

## Recommendation

Use `Puppet Knows` as the artistic showcase.

Use `Arctic Expedition` as the control fixture for understanding and tuning
the system.
