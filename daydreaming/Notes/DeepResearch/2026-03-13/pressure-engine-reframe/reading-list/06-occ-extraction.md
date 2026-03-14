# OCC Model (Revisited) — L2 Extraction

Source: Steunebrink, Dastani, Meyer, "The OCC Model Revisited"
(KI 2009)

Source file: `sources/markdown/OCC/source.md`

Purpose: Extract the emotion type taxonomy for L2
CharacterConcern types and intensity variables.

---

## What the OCC model is

OCC (Ortony, Clore, Collins 1988) is the standard computational
emotion model. It classifies 22 emotion types based on their
**eliciting conditions** — what kind of appraisal produces each
emotion. The "Revisited" paper cleans up ambiguities in the
original using an inheritance-based view.

The model has three branches:

1. **Consequences of events** → pleased/displeased
2. **Actions of agents** → approving/disapproving
3. **Aspects of objects** → liking/disliking

---

## The 22 emotion types (cleaned up)

### Event-based (consequences)

| Type | Eliciting condition |
|------|-------------------|
| Joy | Pleased about an actual desirable consequence |
| Distress | Displeased about an actual undesirable consequence |
| Hope | Pleased about a prospective desirable consequence |
| Fear | Displeased about a prospective undesirable consequence |
| Satisfaction | Joy about confirmation of previously hoped-for |
| Fears-confirmed | Distress about confirmation of previously feared |
| Relief | Joy about disconfirmation of previously feared |
| Disappointment | Distress about disconfirmation of previously hoped-for |
| Happy-for | Joy about consequence desirable for someone else |
| Pity | Distress about consequence undesirable for someone else |
| Gloating | Joy about consequence undesirable for someone else |
| Resentment | Distress about consequence desirable for someone else |

### Agent-based (actions)

| Type | Eliciting condition |
|------|-------------------|
| Pride | Approving of one's own action |
| Shame | Disapproving of one's own action |
| Admiration | Approving of someone else's action |
| Reproach | Disapproving of someone else's action |

### Compound (event + agent)

| Type | Eliciting condition |
|------|-------------------|
| Gratification | Pride + joy about related consequence |
| Remorse | Shame + distress about related consequence |
| Gratitude | Admiration + joy about related consequence |
| Anger | Reproach + distress about related consequence |

### Object-based

| Type | Eliciting condition |
|------|-------------------|
| Love | Liking a familiar aspect of an object |
| Hate | Disliking a familiar aspect of an object |
| Interest | Liking an unfamiliar aspect of an object |
| Disgust | Disliking an unfamiliar aspect of an object |

Note: Interest/disgust is an addition from the "Revisited" paper,
differentiating on familiarity. The original OCC had only love/hate
under liking/disliking with no further specialization. This
cleanup is one of the main contributions of the revised hierarchy.

---

## Intensity variables

Each emotion type has intensity variables that modulate how
strong the emotion is:

- **Desirability** (how much the consequence matters)
- **Likelihood** (for prospective emotions: hope/fear)
- **Praiseworthiness/blameworthiness** (for agent-based emotions)
- **Appealingness** (for object-based emotions)
- **Familiarity** (for love/hate vs. interest/disgust)

Global variables (arousal, unexpectedness) affect all types.

---

## What maps to L2 CharacterConcern

Doc 11 defines nine concern types:

```
status_damage, attachment_threat, retaliation_pressure,
anticipation, guilt, desire, curiosity, unresolved_injury,
obligation
```

OCC gives a principled basis for these:

| Kernel concern | OCC mapping |
|---------------|-------------|
| `status_damage` | Distress + Shame (self-caused) or Reproach (other-caused) |
| `attachment_threat` | Fear (prospective loss of valued relationship) |
| `retaliation_pressure` | Anger (reproach + distress about blameworthy action) |
| `anticipation` | Fear or Hope (prospective consequence) |
| `guilt` | Remorse (shame + distress about self-caused harm) |
| `desire` | Hope (prospective desirable consequence) |
| `curiosity` | No direct OCC mapping — OCC is about valenced reactions, not information-seeking |
| `unresolved_injury` | Distress + Resentment (unresolved past harm) |
| `obligation` | Hope + Fear (prospective consequence of fulfilling/failing) |

### What this clarifies

1. **Concern type is not emotion type.** A concern is an
   ongoing motivational state; an emotion is a momentary
   reaction. A single concern can produce multiple emotions
   over time (fear → anger → relief as situation evolves).

2. **Intensity variables give computable intensity.** Instead
   of hand-setting concern intensity, derive it from
   OCC variables: desirability of the threatened/desired
   state × likelihood × unexpectedness.

3. **Compound emotions are richer than single concerns.**
   Anger = reproach + distress. Gratitude = admiration + joy.
   The kernel currently treats these as atomic concern types,
   but they're actually compounds that could decompose.

---

## What to take

1. **Intensity derivation from structural variables.** Don't
   hand-set concern intensity. Compute it from desirability ×
   likelihood × attribution factors.

2. **The prospective/actual distinction.** Hope and fear are
   about prospective events; joy and distress are about actual
   events. This maps to the kernel's rehearsal (prospective)
   vs. reversal (actual) distinction.

3. **Compound emotion structure.** Anger = reproach + distress.
   This gives richer concern dynamics — a character who
   experiences reproach without distress feels disapproval, not
   anger. Both components must be present.

4. **The fortunes-of-others emotions** (happy-for, pity,
   gloating, resentment). These are interpersonal and relevant
   to character dynamics. A character can feel resentment
   (distress about a good thing happening to a rival) without
   any direct harm.

What NOT to take:

- The full 22-type taxonomy as runtime state (too many types
  for current scale)
- Object-based emotions (love/hate/interest/disgust) in the first pass
- The "revisited" inheritance hierarchy as literal code structure
- Any attempt to make the taxonomy exhaustive before testing
