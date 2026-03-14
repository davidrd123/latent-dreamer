# Mueller Appendix B — Narration Layer Extraction

Source: Mueller, "Daydreaming in Humans and Machines," Appendix B:
English Generation for Daydreaming

Source file: `Notes/Book/daydreaming-in-humans-and-machines/
14-appendix-b-english-generation-for-daydreaming.md`

Purpose: Extract the structured narration model for the
inner-life companion / dashboard.

---

## What Mueller built

DAYDREAMER already had a structured narration layer — not a
debug dump but a designed realization surface. The generator
converts internal concepts into English narration with:

1. Template-based generation over typed internal concepts
2. Belief-path-sensitive narration (whose perspective?)
3. Different language for different cognitive modes
4. Explicit pruning rules
5. Paragraph breaks on structural boundaries

---

## The generator interface

```
generate(con, bp, switches, context)
  con     — the concept to narrate
  bp      — current belief path (whose viewpoint)
  switches — tense, case, mode specifications
  context  — which context to generate in
```

**Belief path** is a list like `(Me)` or `(Harrison Me)` meaning
"Harrison as seen through my eyes." This controls:
- Pronoun selection
- Perspective framing
- Nested belief attribution

---

## Template structure

Each concept type has generational templates:

```
actor(con) want obj(con)+INF
```

This produces: "John would have wanted to go to the movies."

Templates compose:
- **Subject generation** with automatic pronoun handling and
  reflexive elision (won't say "John wanted John to go")
- **Verb morphology** with extensive tense/mood support
- **Recursive concept generation** for nested structures

---

## Mode-sensitive narration

Different cognitive modes produce different language:

| Mode | Language |
|------|---------|
| Normal assertion | Simple past/present |
| Alternative past context | Conditional present perfect: "I would have told him I would like to know the time" |
| Subgoal relaxation (daydreaming mode) | "Say he thought I was cute" |
| Subgoal relaxation (performance mode) | "Maybe he thought I was cute" |
| Initial hypothesis | "What if I were going out with him?" |
| Low-strength fact | "Possibly con" |
| Rationalization (minimization) | "Anyway, I was well dressed because I was wearing a necklace" |
| Reminding | "I remember the time [goal] by [subgoal1] and [subgoal2]..." |
| Surprise (serendipity) | "What do you know!" |

This is directly relevant to the companion's voice. The narration
layer shouldn't just describe what happened — it should reflect
*how the character is thinking about it*.

---

## Emotion narration

Emotions are generated as:

```
head(bp) feel [strength] emotion [toward person] [reason]
```

| Condition | Generated text |
|-----------|---------------|
| Positive + active goal | "I feel interested in being entertained" |
| Negative + failed goal | "I feel displeased about failing at being entertained" |
| Positive + succeeded entertainment | "I feel amused" |
| Directed anger | "I feel angry at Harrison" |
| Regret (alternative past + positive) | "I feel regretful" |
| Relief (alternative past + negative) | "I feel relieved" |
| Low strength | "I feel a bit worried" |
| High strength | "I feel really angry" |

Strength thresholds: < 0.3 → "a bit"; > 0.7 → "really";
between → no modifier.

---

## Pruning rules

Not every internal assertion becomes narration. Mueller has
explicit pruning:

1. **inf-no-gen / plan-no-gen flags** on rules — the rule
   author controls which inferences and subgoals get narrated.

2. **Activated goals** only narrated if: objective isn't already
   satisfied AND gen-advice flag permits.

3. **Goal outcomes** only narrated if: not a daydreaming goal
   AND gen-advice flag permits.

4. **Goal success** not narrated if: objective was already
   satisfied when goal activated.

5. **Top-level goals** only narrated if: not a daydreaming goal.

6. **Input states** always narrated (external events are always
   shown).

**Key insight:** The pruning is *authored per rule*, not computed
globally. The knowledge author decides, at rule-writing time,
which consequences are worth narrating. This gives fine-grained
control over narration density without a separate "what to show"
system.

---

## Structural narration boundaries

**New paragraph** on:
- Backtracking (abandoned a branch)
- Concern switching (attention shifted)

**Pronoun references** cleared on paragraph boundaries, forcing
re-introduction of characters. This prevents the narration from
becoming incomprehensible when the system jumps between concerns.

---

## Reminding narration

Retrieved episodes are narrated as:

```
"I remember the time [goal] by [subgoal1] and [subgoal2]..."
```

Each subgoal is recursively narrated. "I remember the time" is
only generated for the top goal of the episode. This creates
nested narrative recall that mirrors the character's associative
stream.

---

## What maps to the companion/dashboard

| Mueller concept | Dashboard equivalent | Notes |
|----------------|---------------------|-------|
| Belief path | Character perspective selector | Narrate from character A's viewpoint |
| Mode-sensitive language | Hypothetical vs. actual framing | "What if..." vs. "I decided to..." |
| Emotion narration | Concern/emotion state display | "I feel really worried about..." |
| Reminding narration | Associative recall display | "I remember when..." |
| Pruning rules | Narration density control | Not every internal step gets shown |
| Paragraph breaks | Structural segmentation | Visual breaks on concern shifts |
| Subgoal relaxation language | Fantasy/hypothetical marker | "Say he thought I was cute" |
| Strength modifiers | Intensity indication | "a bit" / "really" / unmarked |

---

## What to take for the narration layer

### Immediate

1. **Mode-sensitive framing.** The companion should use different
   language for: actual events, hypothetical exploration,
   alternative pasts, relaxed assumptions, and recalls. Not just
   "here's what happened" but "here's what the character is
   *imagining*."

2. **Authored pruning per operator.** Each L2 operator should
   specify which of its internal steps are worth narrating.
   Don't dump everything; don't hide everything.

3. **Emotion narration with strength and direction.** "I feel
   really angry at B" is more useful than "emotion: anger,
   target: B, strength: 0.8".

4. **Paragraph breaks on concern shifts.** When the character's
   attention moves between concerns, signal this visually. This
   is the narration equivalent of a scene break.

### Next

5. **Reminding narration.** When the character recalls an episode,
   narrate it as a memory: "I remember the time I tried to..."
   This makes the associative stream visible and narratable.

6. **Belief-path narration.** When the character is modeling
   another character's thinking, the companion should frame it:
   "Harrison probably thinks I'm..." (belief path: Harrison, Me)

7. **Hypothetical language.** When the character is exploring a
   counterfactual ("what if I had done X?"), the narration should
   use conditional language, not declarative.

### What this supports

Doc 01 says: "The companion/inner-life view is compelling on its
own." Mueller's Appendix B is evidence that this was always part
of the design — DAYDREAMER already narrated its own stream of
consciousness with mode-sensitive language, emotional framing,
and structural boundaries.

The narration layer is not a debug view. It is a designed
realization surface with its own authored quality. The companion
should be built with the same discipline.
