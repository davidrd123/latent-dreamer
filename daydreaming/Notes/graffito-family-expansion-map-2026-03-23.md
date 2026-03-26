# Graffito Family Expansion Map

Last updated: 2026-03-23

Grounded in:
- `daydreaming/Notes/current-sprint.md`
- `kernel/src/daydreamer/goal_families.clj`
- `kernel/src/daydreamer/goal_family_rules.clj`
- `kernel/src/daydreamer/benchmarks/graffito_miniworld.clj`
- `daydreaming/Notes/DeepResearch/2026-03-22/replies/22-graffito-next-cross-family.md`

Purpose: separate two questions that keep getting blurred together:

1. What is the **next cross-family pairing** to test in the current
   Graffito miniworld?
2. What is the **next family** worth expanding onto Graffito material?

Those are related, but they are not the same decision.

---

## Current live position

The current Graffito branch has a real producer -> consumer ->
membrane chain centered on:

- `:rationalization`
- `:reversal`
- `:rehearsal`

The important update is:

- rehearsal is no longer only benchmark-external glue
- but it is still benchmark-shaped in content

Relevant kernel pieces:
- `goal_families.clj` now has
  `rehearsal-plan-ready-facts`
- `goal_families.clj` now has
  `rehearsal-plan-effects`
- `run-family-plan` now has a minimal `:rehearsal` branch

So the live question is no longer "can rehearsal participate at all?"
It can.

---

## The next pairing

### Best next pairing: `reversal -> rehearsal`

This is still the smallest honest next producer -> consumer expansion.

Why:
- it adds a new source family, not just another rationalization source
- it stays inside the current Graffito regulation / control arc
- it can use already-stored reversal episodes
- rehearsal is already the current consumer family that turns stored
  source material into embodied-control movement

The strongest design detail from review 22 is worth keeping:

- reversal episodes should expose a compact
  `:repair-target` / `:breakdown-surface`
- rehearsal should consume them by matched repair need, not loose fact
  overlap alone

That makes the bridge specific enough to mean something.

### Why this is the next pairing

Because it teaches the miniworld something new while staying close to
the implemented kernel.

What it proves:
- not only meaning repair -> control shift
- but also failure diagnosis -> control shift

---

## The next family

### Most interesting next family on Graffito: `:repercussions`

If the question is not "what pairing next?" but "what new family would
actually broaden Graffito beyond coping/regulation?", the answer is
probably `:repercussions`.

Why:
- it moves from coping with overload/failure into imaginative
  consequence exploration
- it asks "what would happen if the Can works?" or "what if Tony really
  steps through this opening?" instead of only
  "what went wrong?" / "how do I steady myself?"
- it would make the miniworld feel less like repair and more like
  creative projection

Why it is not next:
- Mueller left the trigger heuristics underspecified
- it is not currently a real kernel family path
- it needs more design work than the next pairing does

So:
- `reversal -> rehearsal` is the next pairing
- `:repercussions` is the next family worth opening up after that

---

## Other families

### `:recovery`

Plausible on Graffito, but not the next easiest move.

Why:
- it needs a real "different path forward" plan body
- the activation logic is broad, but Mueller's realization library is
  narrow and domain-specific
- in current code it is basically name/status only

So recovery is possible, but it is not the best immediate Graffito
family expansion.

### `:roving`

Already live, but not where the current membrane story wants to grow.

Why:
- roving is intentionally not the main durable-promotion lane in
  current policy
- pushing Graffito outward through roving right now risks flattening the
  overload -> agency arc into generic escape/surface drift

Roving may matter more later as a regulation anchor or fantasy-side
carrier, but it is not the cleanest next expansion.

---

## Practical ranking

If the question is:

### "What should we build next inside the current Graffito miniworld?"

1. `reversal -> rehearsal`

### "What family should broaden Graffito after that?"

1. `:repercussions`
2. `:recovery`
3. broader roving lanes later

---

## Short version

The narrow answer is:

- next pairing = `reversal -> rehearsal`

The broader answer is:

- next family = `:repercussions`

Those are different answers because the next pairing should be the
smallest honest move, while the next family should be the move that
actually widens the imaginative range of the Graffito world.
