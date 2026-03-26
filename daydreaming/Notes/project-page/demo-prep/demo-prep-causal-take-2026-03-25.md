# Demo Prep — Causal Read Take (2026-03-25)

This is a companion note to `demo-prep-2026-03-25.md`.

The issue is not that the current demo scroll lacks information. It is
that it shows **state** more clearly than **causality**.

Meters going up and down are not enough. For a viewer to feel that the
Clojure machinery matters, they need one compact answer to:

> why did this reading change?

The video should not try to show all the machinery. It should show one
legible causal chain.

---

## The right split

Use the three existing pages for different jobs:

- `graffito_demo_scroll.html`
  - best for the emotional arc
  - strongest for image + prose + path-dependent change
- `graffito_miniworld_rendered.html`
  - best for engineer-facing mechanics
  - too much candidate detail for a short showcase clip
- `graffito_thought_stream.html`
  - best for production-surface feel
  - not the clearest place to explain causality

For the showcase video, the **demo scroll should stay primary**.

But it needs one small machinery layer that is more explanatory than
the current badges/meters.

---

## What the current scroll gets right

- image at top
- prose in the middle
- left-margin legend
- compact state strip
- flip badge

This is enough to make the result feel alive.

What it does **not** yet make clear:

- what triggered the winning family
- what that family did
- why Tony state changed
- why the mural re-read flipped

Right now the viewer can see "challenge" replacing "threat," but not
the causal ladder underneath it.

---

## Add one causal strip, not a full debug panel

Do **not** put the full candidate race or episode ids on the main card.
That turns the showcase into an engineer dashboard.

Instead add one compact row under the thought prose:

`PRESSURE -> FAMILY -> CHANGE -> RE-READ`

Example:

`Overload + low control -> Rehearsal -> entrainment/control rise -> mural reads as challenge`

Or for an earlier beat:

`Wrongness + overload -> Rationalization -> meaning reframed -> mural still reads as threat`

This is the missing link.

It lets the viewer understand:

- the family was selected for a reason
- the family had an effect
- the effect changed later perception

That is the actual claim.

---

## Page structure I would use

For each key card:

1. **Visual**
   - generated image

2. **Scene**
   - one short line of external situation

3. **Inner life**
   - prose excerpt

4. **Why it changed**
   - one causal strip:
     - pressure
     - winning family
     - effect on Tony
     - resulting appraisal

5. **State strip**
   - regulation badge
   - appraisal badge
   - small mini-meters

So the meters remain, but they stop carrying the whole explanatory load.

---

## What the causal strip should contain

Keep it to four fields max:

- **Pressure**
  - what was active enough to matter
  - examples:
    - `sensory overload`
    - `wrongness + overwhelm`
    - `aftershock from street`

- **Family**
  - one plain-language label plus family name if helpful
  - examples:
    - `Rehearsal (practice/regulation)`
    - `Rationalization (meaning shift)`
    - `Reversal (counterfactual repair)`

- **Effect**
  - what changed in Tony state or meaning
  - examples:
    - `entrainment and control rose`
    - `the mess got re-read as form`
    - `street shock knocked him back`

- **Re-read**
  - the outcome that matters
  - examples:
    - `mural: threat -> challenge`
    - `street still reads as overload`

This is enough.

---

## Which beats to show

Do not show six if the goal is clarity.
Show four:

1. **C01**
   - establish Tony as overloaded / bracing
   - show that the engine is selecting a family for a reason

2. **First mural threat**
   - establish the place reading badly

3. **First rehearsal flip**
   - the key proof beat
   - same place, different read

4. **Late hold**
   - show that the gain holds after setback

If a fifth beat is needed, use one setback card in between.

---

## Narration principle

The narration should not explain the whole architecture.
It should teach the causal grammar once, then let the cards carry it.

Suggested pattern:

1. one sentence at the start:
   - "The bars show Tony's regulation state. The causal strip shows why
     it changed."

2. for each card:
   - say the family in plain language
   - say what changed
   - say why the next reading differs

Example:

- "Tony starts overloaded. The engine picks rationalization, trying to
  make the scene feel bearable."
- "At the mural, the same pressure reads as threat."
- "After rehearsal, his entrainment and control rise. Nothing about the
  mural changed, but Tony did, so the mural now reads as challenge."
- "Later the street knocks him back, but the gain holds."

---

## What not to show in the main demo

Keep these out of the main card unless there is a separate appendix
shot:

- full candidate tables
- episode ids
- detailed retrieval indices
- promotion / durable / rule-access counters
- raw trace terms like `cross-family-source-win-cycles`

Those are real, but they are not the shortest path to comprehension.

---

## If one structural visual is included

Use only one:

- the divergence figure, or
- one tiny inset showing `same cues -> different reading`

If included, it should support the claim, not become the claim.

---

## Bottom line

The right showcase page is:

- story-first like `graffito_demo_scroll.html`
- with a compact causal strip borrowed from the logic of
  `graffito_miniworld_rendered.html`
- not a full debug page

The key thing to make visible is not "all the machinery."
It is:

**pressure selected a family, the family changed Tony, and that changed
the next reading.**
