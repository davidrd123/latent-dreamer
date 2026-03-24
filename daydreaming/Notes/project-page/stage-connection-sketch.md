# Stage Connection Sketch

Date: 2026-03-22

For the project page: how the kernel connects to the performance
surface. The kernel is built. The stage exists from earlier rounds.
The connection is the next phase. This sketches what it looks like
when they meet.

---

## The opening frame

A performance instrument where a human conductor steers a
character's inner life — what it worries about, what it avoids,
what it remembers — and the stage makes that inner life legible
through narration, music, and eventually video.

Round 02 built the stage: real-time video via Scope at 23 fps,
APC Mini MK2 as a physical control surface, Lyria RealTime for
audio, LoRA style morphing, depth conditioning.

This round built the mind: a Clojure kernel with persistent
concerns, episodic memory with admission discipline, a rule engine
mediating cognitive transitions, and a feedback loop where
generated thought changes future behavior.

The next phase connects them.

---

## What the connected flow looks like

```
Conductor (human)
  APC Mini faders bias concern pressure, mood, mode
    ↓
Kernel cycle fires
  → concern with strongest emotional pull is selected
  → family plan runs (rationalize, reverse, rehearse, rove)
  → episodes retrieved by coincidence-mark counting
  → plan result stored with admission discipline
    ↓
Thought beat projected
  → LLM receives kernel state as a compact packet
  → produces 2-3 sentences of inner-life prose
  → mood tags: ["guarded", "self-dividing", "pressurized"]
  → residue writes back into episodic memory
    ↓
Stage outputs (three channels)
  → Narration: inner monologue rendered from thought beat
  → Audio (Lyria RT):
      tension → held metallic hum
      reversal → harmonic shift, unstable resolution
      roving → open drift, looser rhythm
      rationalization → contained warmth, false comfort
  → Video (Scope, when GPUs available):
      emotional landscape rendered from kernel state
      situation activation as visual weight
      conductor bias visible as camera/lighting shift
    ↓
Conductor sees/hears the result
  → adjusts faders for next cycle
  → the performance is steering cognition, not prompts
```

---

## What exists vs. what's sketched

| Component | Status |
|-----------|--------|
| Kernel cycle with membrane | Built, tested (223 tests) |
| Thought beat projector | Built, feedback loop proven |
| Lyria RT integration | Designed, audio mapping spec exists |
| Narration bridge | Exists from earlier work |
| APC Mini → kernel bias | Designed, not yet wired to new kernel |
| Scope video rendering | Exists from Round 02, not connected |
| Conductor → kernel → stage loop | Not yet integrated |

---

## Diagram prompt: The Instrument

```
Render this as a polished technical diagram for creative
technologists:

THE INSTRUMENT — KERNEL TO STAGE

Top: "Conductor" — APC Mini faders + pads
  Arrow down labeled "bias: pressure, mood, mode"

Center: "Kernel" — circular flow:
  Select concern → Run family plan → Retrieve episodes →
  Store with admission → Project thought beat → Write residue
  (cycle repeats)

  Inside the kernel circle:
  - "Memory Membrane" label (small, underneath)
  - "Rule Graph" label (small, to the side)

Below kernel, three arrows fanning out:

Left arrow → "Narration"
  Inner monologue from thought beat

Center arrow → "Audio (Lyria RT)"
  Mood tags → musical parameters
  tension/reversal/roving/rationalization each mapped

Right arrow → "Video (Scope)"
  Emotional landscape from kernel state
  (labeled "when GPUs available")

Bottom: all three merge into "Stage"
  The audience sees/hears the character thinking

Style: Dark mode, neon accents, VJ aesthetic.
- Kernel: cyan border
- Conductor: amber
- Stage outputs: green
- Memory membrane: magenta accent
- Arrows: color-coded by channel
Keep it clean — restrained glows, not fizzy.
```

---

## Diagram prompt: What This Round Built

```
Render this as a polished technical diagram:

WHAT THIS ROUND BUILT

Left column: "Round 02 — The Stage"
  - Scope (real-time video, 23 fps)
  - APC Mini MK2 (physical control)
  - Lyria RT (audio from mood)
  - LoRA morphing, depth conditioning
  Label: "The performance surface exists"

Right column: "This Round — The Mind"
  - Mueller's control loop
  - Rule engine (RuleV1, connection graph)
  - Memory membrane (admission, promotion, anti-residue)
  - Thought beat projector + feedback loop
  - 19 mechanism cards from the book
  Label: "The cognitive substrate exists"

Center: arrow connecting both columns
  Label: "Next: connect the mind to the stage"

Below: "The instrument becomes real when the
character's inner pressure directly shapes what
you hear and see"

Style: Dark mode, two columns with a bridge.
- Stage column: cyan
- Mind column: magenta
- Bridge: amber glow
- Bottom text: subdued white
```

---

## For the page draft

The page should:

1. Open with the instrument, not the architecture
2. Show "this round" as the mind that feeds the stage
3. Sketch the connected flow honestly (designed, not built)
4. Include diagrams that show both rounds converging
5. Close with the same honest claim: foundation, not finished

The audience is creative technologists interested in real-time
performance. They care about what it sounds and looks like. The
kernel is why it has depth — but depth serves the performance,
not the other way around.
