# Phase 3 Tuning: Feedback Loop Parameters

Three open questions from the architecture reviews resolve into measurable
control parameters. None of them need to be decided on paper — they need
to be measured with good logging once the feedback loop is running.

This note defines what to measure and what the tuning knobs are.

## 1. Gap Width

**The question:** How far should the Director's interpretation depart from
the Dreamer's literal situation?

**Why it matters:** Too narrow and you get an illustrated audiobook. Too
wide and the imagery feels disconnected from the emotional content. The
right width is where the feedback produces Dreamer state changes that
wouldn't have been reached without the Director's intervention.

### Two separate knobs

Gap width is not one parameter. It's two:

- **Interpretive distance** — how far the Director departs from the
  Dreamer's literal situation. "Entrapment at the harbor" at low distance
  is a locked gate by the water. At high distance it's a figure pressing
  palms into breathing skin.

- **Render distance** — how far the prompt departs from what the model
  can reliably render. Capped by the model/LoRA's achievability limits
  (see Strategy 2: Conceptual Distance from the prompt writing patterns).

These are related but not identical. High interpretive distance with low
render distance is possible and often desirable: the Director reimagines
the harbor as a flooded cathedral but describes it in simple,
model-friendly terms.

### What drives interpretive distance

| Signal | Effect on gap | Why |
|---|---|---|
| **Tension** (high) | Wider | Intense emotion produces more abstract imagery |
| **Tension** (low) | Narrower | Mild states stay closer to literal content |
| **REHEARSAL, RECOVERY** | Narrower | These are practice and repair — you need to recognize what you're working with |
| **ROVING** | Wider | Escape — you want to be somewhere else entirely |
| **REVERSAL** | Wider | Counterfactual — the point is departure from what happened |
| **RATIONALIZATION** | Medium | Reframing, not departing — the original is still visible |
| **REVENGE** | Medium-wide | Directed, not abstract — wild but pointed |
| **REPERCUSSIONS** | Medium-wide | Cascading, extrapolating outward |
| **Obsession phase (rising)** | Narrower | The mind is locked on — less room for interpretive drift |
| **Obsession phase (falling)** | Wider | Grip loosening — more room for reinterpretation |

### What to log

- `interpretive_distance_target` — the gap width the Director was asked for
- Whether the feedback actually shifted what the Dreamer obsesses about
  (did it change goal type, activate a new situation, or just reinforce
  the current state?)
- If feedback only reinforces: gap is too narrow (Director is illustrating)
- If feedback produces nothing the Dreamer recognizes: gap is too wide
  (Director has departed entirely)

## 2. Contradiction Handling

**The question:** When the Director's interpretation contradicts the
Dreamer's goal type (REVENGE → forgiveness imagery), does the dream
resolve the emotion or intensify it?

**Answer: both, depending on obsession phase.**

### Activation trajectory, not activation level

Simple activation-gating (high activation resists, low allows) is
incomplete. The key signal is whether the obsession is building or
winding down:

- **Rising activation** (obsession building, current near or approaching
  peak): Contradictions register as surprise → intensification. The
  Dreamer can't be dreamed out of rising anger. Forgiveness imagery
  makes the Dreamer *more* angry. This is the obsessive phase.

- **Falling activation** (obsession peaked, decay working, Dreamer's
  grip loosening): Contradictions can actually shift valence. The
  forgiveness imagery lands differently when you're exhausted from the
  obsession. This is the resolution phase.

Implementation: compare current activation to peak activation for that
goal. `current / peak > threshold` → resist. `current / peak < threshold`
→ allow.

This matches how real emotional processing works in dreams. You don't
resolve anger in the first dream about it. You obsess, cycle, build
charge, and eventually the repetition wears it down. Then the dream can
offer a different perspective.

### Goal congruence, not just valence

Valence alone (positive/negative) is too crude for detecting contradiction.
A tender image can be revenge-congruent (tenderness toward what was lost
sharpens the grievance). A violent image can be recovery-congruent
(destroying the obstacle is how you continue).

The right signal is **goal congruence**: does the Director's reading
support or oppose the Dreamer's current cognitive posture?

| Dreamer posture | Congruent Director output | Contradictory Director output |
|---|---|---|
| REHEARSAL | Practicing, preparing, running through | Abandoning, giving up |
| REVENGE | Directed hostility, exposure, redress | Forgiveness, softening, letting go |
| REVERSAL | Alternative outcomes, different paths | Acceptance of what happened |
| RECOVERY | Continuing, repairing, finding a way | Dwelling on the loss, reopening the wound |
| ROVING | Pleasant, distant, elsewhere | Pulled back to the charged situation |
| RATIONALIZATION | Reframing, minimizing, justifying | Confronting the failure directly |
| REPERCUSSIONS | Consequences cascading outward | Containment, nothing-changes |

### What to log

- `goal_congruence` — does the Director's output align with or oppose
  the Dreamer's current posture?
- `obsession_phase` — rising, peaked, or falling (derived from
  `current_activation / peak_activation`)
- Whether contradictory feedback during falling phase actually shifts
  the Dreamer's goal type or situation focus
- Whether contradictory feedback during rising phase produces surprise
  boosts

## 3. Semantic Drift

**The question:** When Director-generated concepts chain through
coincidence retrieval ("membrane" → "origin" → "water" →
"dissolution"), is that dreamlike migration or incoherent noise?

**Answer: depends on return time.**

### Drift is only incoherent if the Dreamer can't recapture it

The Dreamer's situations (escape, bargain, betrayal) are gravitational
wells. Even if the Director drifts through novel conceptual territory,
the Dreamer's activation dynamics keep pulling the system back toward
charged situations. Drift happens *between returns* to obsessive centers.

The phenomenology we want is drift and return. The Director produces
drift (novel concepts, associative leaps). The Dreamer produces return
(activation-driven pull back to unresolved situations). Semantic drift
is only a problem if the Dreamer's gravitational pull is too weak to
recapture the system after the Director pushes it out.

### The anchor rule

Director-injected concepts (`director_concept` index type) should only
contribute to coincidence counting when **co-activated with at least one
grounded Dreamer index** (situation, place, emotion, or goal_type).

This means:
- "Membrane" alone cannot drive retrieval
- "Membrane" + "entrapment" can (the Director's concept meets the
  Dreamer's situation)
- "Membrane" + "sacred_threshold" cannot (two Director concepts
  reinforcing each other without Dreamer grounding)

This gives you drift without letting the Director hijack the memory
system. The Director can introduce novel associations, but only the
Dreamer can authorize them as retrieval-relevant.

### Return time is the metric

The right thing to measure isn't "how far did concepts drift" — it's
**how many cycles until the system returns to a situation-aligned node
after a Director-induced excursion.**

- Return within 3-5 cycles: drift is productive (enriches the dream
  with novel associations, then comes home)
- Return within 5-15 cycles: drift is extended but potentially
  interesting (the dream goes on a journey)
- Return takes 20+ cycles: drift has escaped the Dreamer's gravity.
  The system is wandering, not dreaming.

### If drift goes too far

The fix is straightforward: increase situation activation strength
relative to Director-injected index strength. Make the gravitational
wells deeper. The `director_concept` typed index already gives you the
knob — tune its weight in coincidence counting separately from situation
indices.

### What to log

- `director_concepts` — which novel concepts the Director introduced
  this cycle
- `cycles_off_axis` — how many consecutive cycles since the system was
  on a situation-aligned node
- `cycles_to_return` — after a Director-induced excursion, how many
  cycles until the system returns to a situation-aligned node
- Which Director concepts survive in the FIFO-6 over time (do they
  persist or get pushed out quickly?)
- Whether surviving Director concepts ever contribute to retrieval
  (do they actually participate in coincidence hits?)

## Summary: Phase 3 Logging Requirements

From day one of feedback loop work, every cycle should log:

```
{
  "cycle": 142,
  "dreamer_state": { ... },
  "director_output": { ... },

  // Gap width
  "interpretive_distance_target": 0.7,

  // Contradiction
  "goal_congruence": -0.3,
  "obsession_phase": "falling",
  "current_activation": 0.45,
  "peak_activation": 0.82,

  // Semantic drift
  "director_concepts": ["membrane", "breath"],
  "director_concepts_in_fifo": 2,
  "cycles_off_axis": 3,
  "grounded_indices_active": ["s1_escape", "neg_emotion"],

  // Feedback effects
  "feedback_shifted_goal_type": false,
  "feedback_activated_new_situation": null,
  "feedback_triggered_surprise": false
}
```

These fields make Phase 3's three hypotheses testable:
- Gap width is productive → feedback shifts Dreamer state
- Contradiction handling works → rising obsessions resist, falling
  obsessions resolve
- Semantic drift is bounded → return time stays under threshold
