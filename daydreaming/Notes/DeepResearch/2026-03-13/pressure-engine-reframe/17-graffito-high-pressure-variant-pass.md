# Graffito High-Pressure Variant Pass

Date: 2026-03-14

## Purpose

Test whether the Graffito pilot could support a more expressive
positive-bias conductor mode if the graph gained one apartment-side
high-pressure alternative.

The change was intentionally small:

- add one new `s2` variant that carries unresolved street charge into
  the apartment
- rerun the same neutral / high / early-release preset trio

## Graph Change

Added:

- `g_s2_n03v_tony_still_spun_up`

This is a variant of `g_s2_n03_tony_bursts_in` with:

- `delta_tension: 0.18`
- `delta_energy: 0.20`
- initial `priority_tier: 1`, then retuned down to `0`

New edges allow it to appear as:

- an apartment-side continuation from `camera_enters_window`
- a carryover from `monk_painting`
- a hotter alternative to the base `tony_bursts_in`

It can resolve toward:

- `grandma_enters`
- `dance_together`
- `eyes_searching`

## Result

The addition worked, but it also showed the limit of the Graffito
substrate.

### What improved

- `high` is no longer identical to `neutral`
- the graph now supports an `s2` state that is not purely a release
  basin
- cross-situation traversal can stay charged across the apartment
  boundary instead of immediately calming down

### What the rerun showed

All three presets reached for the new variant:

- neutral: `2` visits
- high: `2` visits
- early release: `2` visits

So the new node is not just a positive-bias affordance.
It became a broadly attractive structural option.

### Preset differences after the change

#### Neutral

- `8` unique nodes
- `11` cycles in `s1`
- `7` cycles in `s2`
- ended at max tension (`1.0`)

#### High

- `8` unique nodes
- `9` cycles in `s1`
- `9` cycles in `s2`
- diverged from neutral
- ended lower (`0.6`) and stayed in apartment material longer

#### Early Release

- `10` unique nodes
- `8` cycles in `s1`
- `10` cycles in `s2`
- still clearly the most release-oriented profile
- ended near floor tension (`0.1`)

## Interpretation

This is enough to answer the main question:

- yes, the conductor surface is real in both directions
- yes, a hotter `s2` option changes the traversal space
- but the Graffito slice is now close to saturation as a conductor
  expressivity test

The new variant gave the scheduler somewhere to go, but because the
slice is still tiny, that one added node quickly becomes important for
every preset rather than only for `high`.

That is not a bug in the scheduler.
It is a sign that the graph is too small for much finer discrimination.

## Takeaway

The Graffito pilot has likely reached its natural endpoint.

It has now shown:

- baseline vs scheduler separation
- release vs escalation shifts
- one real conductor-sensitive divergence
- limited two-sided conductor expressivity once the graph gains one hot
  apartment-side option

What it cannot show cleanly anymore is richer positive-bias range
without the graph becoming overdetermined by one extra node.

## Recommended Next Move

Stop pushing Graffito wider.

Use the current result as sufficient pilot evidence and move the next
real scheduler test to City Routes / City Night Pursuit, where the graph
has enough room for conductor range to be expressed without a single new
node dominating the whole system.
