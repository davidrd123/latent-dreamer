# Plan of Attack

Date: 2026-03-19

From the "Prosthetic Inner Life" reframe. The LLM inhabits the
role. The kernel gives it a mind. The world evolves around it.

---

## The convergence

Three threads that looked separate are actually one system:

1. **RuntimeThoughtBeatV1** — the kernel runs the cognitive
   loop, an LLM renders each cycle as inner-life prose
2. **The provocation seam** — typed world events that change
   the situation the character is in
3. **The generation pipeline** — produces character moments
   conditioned on cognitive state

They converge at: **the kernel runs continuously, the LLM
inhabits the character at each cycle, and the world evolves
through provocations that the character responds to.**

That's not three separate projects. It's one loop:

```
Kernel fires operator + retrieves memory
    ↓
LLM generates inner-life beat (RuntimeThoughtBeatV1)
    ↓
Beat feeds: narration, audio, visualization
    ↓
Tiny residue feeds back into kernel memory
    ↓
Provocation arrives (world event or Director perturbation)
    ↓
Kernel situation updates (carry + remap)
    ↓
Next cycle — different operator may win because
the world changed and the character remembers
```

## The build sequence

### Phase 1: The mind speaks (next)

**Target:** Puppet Knows cognitive trace with inner-life prose.

- Run `bb puppet-knows-autonomous` to produce a 12-cycle
  cognitive trace
- For each cycle, call Sonnet with the kernel's state:
  - active concern
  - selected operator
  - retrieved material
  - context branch
  - prior beat residue
- Produce `RuntimeThoughtBeatV1`:
  - thought_beat_text (2-3 sentences)
  - mood_tags (for Lyria)
  - residue_candidates (tiny, for memory)
  - external_action (if performance mode)
  - daydream_content (if daydreaming mode)
- Render through the cognitive HTML + narration bridge
- Wire to Lyria RT

**Success:** The output reads as a person thinking, not as a
system report.

**What exists:** kernel runs, Lyria connected, narration bridge
exists, cognitive HTML renderer exists. Missing: the LLM call
per cycle and the RuntimeThoughtBeatV1 object.

### Phase 2: The world pushes back

**Target:** Provocations change what the character does.

- The provocation seam is already partially built
  (FixtureDeltaV1 / overlay interpreter)
- Need: propagation into concern inference and operator scoring
  (codex1 is patching this)
- Need: ProvocationContextV1 so the Provocation Generator
  sees enough internal state to push intelligently
- Need: carry + remap when situations change

**Success:** A provocation ("Eli sends a second text") changes
which operator wins and produces a structurally different
inner-life beat.

**What exists:** overlay interpreter, three-way state split
design. Missing: propagation to decision surfaces, carry+remap
implementation.

### Phase 3: The loop runs continuously

**Target:** Indefinite traversal with accumulating memory and
world evolution.

- Kernel cycles continuously (daydream_engine.py already does
  this with --interval-s)
- RuntimeThoughtBeat at each cycle
- Provocations arrive at intervals (hand-authored initially,
  Provocation Generator later)
- Memory accumulates across cycles (with writeback discipline
  from Q8 — tiny residue only, not full prose)
- Conductor bias from APC Mini
- Audio + narration + visualization update in real time

**Success:** You watch for 5 minutes and the character's
thinking evolves — different concerns surface, different
operators fire, memories from earlier cycles influence later
ones, world events provoke genuine responses.

### Phase 4: The conversation

**Target:** The "prosthetic inner life" demo.

You talk to the character. It responds from inside accumulated
cognitive state. You leave. Between sessions, it daydreams —
rehearses, rationalizes, avoids, roves. You come back, and
it's different. The dashboard shows what happened while you
were gone.

This is the persistent daemon from doc 34, made concrete.
The kernel runs between sessions. The LLM inhabits the
character when you're there. The inner life is continuous.

### Phase 5: Graffito

**Target:** Mark Friedberg's creative brief, with generated
material, traversed live, rendered through Scope.

- Graffito creative brief authored (creative_brief.yaml +
  style_extensions.yaml)
- Full Graffito graph assembled from scaffold + generated
  patches + provocation-driven material
- Scope rendering when GPUs available
- APC Mini conducting the performance
- Mark watches and reacts

---

## What doesn't change

- The graph remains the membrane
- The kernel runs Mueller's loop
- L3 is the traversal scheduler
- The Director is runtime perturbation, not world authorship
- The Provocation Generator is authoring-time, typed, curated
- Fixtures are task views, not canon
- Memory writeback is disciplined (tiny residue, not full prose)

## What shifts

- **The primary product is inner-life prose, not graph nodes.**
  The generation pipeline proved material supply. Now the goal
  is the runtime experience — hearing the character think.
- **The provocation seam is part of the same loop, not a
  separate project.** World events and inner-life generation
  are two halves of one cycle.
- **The first demo is a conversation, not a video.** The
  persistent daemon is closer than the conducted performance
  because it doesn't need video, conductor, or Scope.
- **"Prosthetic Inner Life for Language Models" is the hook.**
  Not "cognitive character engine" (too abstract). Not "legible
  inner drift" (too niche). The LLM has no mind of its own.
  We give it one.

## What's immediately actionable

1. Write `runtime_thought_beat.py` — the LLM call per kernel
   cycle that produces RuntimeThoughtBeatV1
2. Wire it to the Puppet Knows autonomous trace
3. Wire the output to the existing cognitive HTML renderer
4. Wire to Lyria RT
5. Watch/listen and judge: does this feel like a mind?

That's the Level 3 test. Everything else follows from the
answer.
