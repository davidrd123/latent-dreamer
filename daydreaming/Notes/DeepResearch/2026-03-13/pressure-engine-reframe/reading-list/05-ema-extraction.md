# EMA (Emotion and Adaptation) — L2 Extraction

Source: Marsella & Gratch, "EMA: A Computational Model of
Appraisal Dynamics" (Agent Construction and Emotions, 2006)

Source file: `sources/markdown/EMA/source.md`

Purpose: Extract the appraisal model for upgrading L2
concern birth, intensity, and coping in the kernel.

---

## What EMA actually is

EMA is a computational model of the appraisal cycle: how agents
interpret events in terms of their relationship to the
environment, and how that interpretation drives emotions and
coping responses. Built on top of the Soar cognitive
architecture.

The key theoretical stance: **appraisal is always fast and
automatic.** Dynamics arise not from multiple appraisal
processes (fast vs. slow) but from the underlying perceptual and
inferential processes that update the agent's interpretation.
Appraisal just reads that interpretation — the dynamics come
from the interpretation changing.

---

## The appraisal cycle

```
Event perceived
  → updates causal interpretation (agent's model of
    agent-environment relationship)
  → appraisal variables computed automatically from
    interpretation structure
  → emotion instances generated (type + intensity)
  → coping strategies proposed
  → coping modifies causal interpretation
  → reappraisal (automatic, because interpretation changed)
  → cycle continues
```

This is the thing the kernel is missing. Currently concern
birth and intensity are somewhat ad-hoc. EMA says: make
appraisal a structured pass that reads from a causal
interpretation, not a hand-coded mapping.

---

## The causal interpretation

EMA's central representation is a **causal interpretation** —
the agent's current model of their relationship to the
environment, represented as a decision-theoretic plan structure
augmented with beliefs and intentions.

```
States (ovals): beliefs about the world
  - each has probability (0.0-1.0)
  - each has utility (positive or negative)

Actions (rectangles): things that can happen
  - preconditions (states that must hold)
  - effects (states that change)
  - linked by establishment and threat relations

Goals: high-utility states the agent wants to achieve/maintain
```

Appraisal variables are derived from the **structure** of this
representation, not from domain-specific rules.

---

## Appraisal variables (domain-independent)

| Variable | How it's derived |
|----------|-----------------|
| Perspective | Whose viewpoint is being taken |
| Desirability | Utility of event's effects (positive = advances goals, negative = threatens goals) |
| Likelihood | Probability of the outcome in the plan structure |
| Causal attribution | Who executed the action? Did they intend/foresee consequences? |
| Temporal status | Past, present, or future? |
| Controllability | Can the agent's own actions affect the outcome? |
| Changeability | Can some other agent affect the outcome? |

These map to emotion types via OCC's structural scheme
(see `06-occ-extraction.md`).

---

## Coping strategies

When an emotion is negative, EMA proposes coping strategies
to modify the causal interpretation. This is the second
half of the cycle — emotions don't just happen, they drive
responses that change the interpretation, which triggers
reappraisal.

| Strategy | What it does | When used |
|----------|-------------|-----------|
| Action | Select an action for execution | Control is high |
| Planning | Form an intention to act (can improve mood even before acting) | Control is high |
| Seek support | Ask someone in control for help | Changeability is high |
| Procrastination | Wait for external change | Changeability is high |
| Positive reinterpretation | Increase utility of a positive side-effect | Control and changeability are low |
| Acceptance | Drop a threatened intention | Control and changeability are low |
| Denial | Lower probability of undesirable outcome | Control and changeability are low |
| Mental disengagement | Lower utility of desired state | Control and changeability are low |
| Shift blame | Move responsibility to another agent | Attribution-focused |
| Seek/suppress information | Form intention to monitor or ignore | Information-focused |

Selection priority: problem-directed (if controllable) >
procrastination (if changeable) > emotion-focused (if neither).

---

## What maps to L2

| EMA concept | L2 equivalent | Current kernel status |
|------------|---------------|---------------------|
| Causal interpretation | Character's model of world + relationships + goals | Partially: goals, emotions, world state. Missing: explicit causal/plan structure |
| Appraisal pass | Event → concern creation/intensity update | **Missing.** Currently ad-hoc concern birth |
| Desirability | Concern intensity based on goal relevance | Partially: goal strength competition |
| Controllability | Whether character can act on the concern | **Missing.** Would change operator selection |
| Coping strategies | Mueller's families (rehearsal, reversal, rationalization, roving) | Present, but not connected to appraisal |
| Reappraisal | Concern intensity update after coping | **Missing.** Concerns don't update after exploration |

---

## What's genuinely new vs. current kernel

### 1. Structured appraisal pass

The kernel creates concerns somewhat arbitrarily. EMA says:
run every event through a domain-independent appraisal pass
that reads the character's causal interpretation and derives
concern type + intensity from structural properties.

**Concrete change:** Add `appraise(event, character_state) →
[ConcernDelta]` as an explicit step in the cycle.

### 2. Controllability as one input to operator selection

EMA's coping strategy selection is based on whether the
situation is controllable, changeable, or neither. This
provides a useful *heuristic bias* for operator activation:

- **Controllable** → biases toward action-directed operators
  (reversal, confront)
- **Changeable** → biases toward anticipatory operators
  (rehearse, avoid)
- **Neither** → biases toward emotion-focused operators
  (rationalize, rove)

But this should NOT be treated as a direct lookup table from
appraisal result to Mueller family. Operator activation also
depends on situation/practice context (per Versu — what
affordances the current social situation offers) and on concern
type. Controllability is one input among several, not a sole
dispatcher.

### 3. Coping changes the interpretation, triggering reappraisal

Currently: a family plan runs, produces a result, and that's it.
EMA says: the result of exploration should change the character's
model of the situation, which re-runs appraisal, which may
intensify, resolve, or transform the original concern.

**Concrete change:** After a family plan commits (policy/salience),
re-run appraisal on affected concerns. Concerns can intensify,
diminish, transform type, or resolve.

### 4. Planning as emotion regulation (not just action)

EMA's insight: forming an intention to act (planning/rehearsal)
can reduce negative emotion *even before the action is executed*.
This is exactly what Mueller's rehearsal family does — and EMA
gives it a theoretical basis. The character feels better because
controllability increased, even though nothing changed in the
world.

---

## What to take

1. **The appraisal pass as an explicit kernel step.** Event in →
   appraisal variables out → concern deltas. Domain-independent
   structural derivation from character state.

2. **Controllability/changeability as one operator selection
   input.** Use appraisal-derived coping bias alongside concern
   type and situation context (per Versu) to inform which
   operators activate. Not a direct dispatcher — one input
   among several.

3. **Reappraisal after exploration.** Concerns update after
   coping, not just after external events.

4. **The coping strategy taxonomy** as a principled basis for
   which operators activate under which conditions.

What NOT to take:

- The full Soar-based implementation
- The decision-theoretic plan representation (too heavy for
  current kernel — simpler concern/goal structure is fine)
- The detailed facial expression / physiological modeling
- Any attempt to "do all of appraisal theory"
