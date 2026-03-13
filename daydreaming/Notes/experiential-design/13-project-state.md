# Project State: Where We Are and How to Question It

Written for David, not for the system. This is the map you asked for.

## System Map

```
┌─────────────────────────────────────────────────────────────┐
│                      PREP (offline)                         │
│                                                             │
│  Creative Brief ──── "The Puppet Knows"                     │
│  Style Guide ─────── materials, lighting, negative space    │
│  World ───────────── 4 situations, 5 places, 2 events      │
│  Dream Graph ─────── 20 nodes, 28 edges                    │
│                                                             │
│  You author this. It's the DNA of a specific dream piece.   │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                   DREAMER (Python scheduler)                │
│                                                             │
│  Every ~5 seconds:                                          │
│    decay → pick goal type → find matching node → emit       │
│                                                             │
│  Output: "s1_seeing_through, rationalization, tension 0.66" │
│                                                             │
│  Status: MECHANICALLY PROVEN. Produces obsession, drift,    │
│          return. Not artistically evaluated yet.            │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                   DIRECTOR (LLM, currently Gemini)          │
│                                                             │
│  Reads the Dreamer's output through the creative brief.     │
│  Produces a small echo: "I noticed void behind the wall."   │
│  Echo feeds back into the Dreamer as new indices.           │
│                                                             │
│  Output: 1-3 concepts, small situation boosts, maybe anger  │
│                                                             │
│  Status: PARTIALLY PROVEN. Can wake dormant situations.     │
│          Can't yet reliably produce directed anger →         │
│          revenge → ring takeover cascade.                   │
└──────────┬─────────────────────────────┬────────────────────┘
           │                             │
           │ feedback                    │ (Phase 3+: visual
           │ (proven)                    │  prompts from here)
           ▼                             ▼
     back to Dreamer              ┌──────────────────────┐
                                  │   STAGE (Scope+Lyria) │
                                  │                      │
                                  │   Scope: real-time   │
                                  │   video via REST API │
                                  │                      │
                                  │   Lyria: real-time   │
                                  │   music via API      │
                                  │                      │
                                  │   APC Mini: faders   │
                                  │   for live control   │
                                  │                      │
                                  │   Status: EXISTS     │
                                  │   from rounds 1-2.   │
                                  │   Adapter wired but  │
                                  │   not artistically   │
                                  │   evaluated.         │
                                  └──────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│             CLOJURE KERNEL (research sidecar)               │
│                                                             │
│  A source-faithful Mueller reconstruction.                  │
│  Produces traces for comparison against the Python engine.  │
│  Not connected to the stage. Not on the critical path.      │
│                                                             │
│  Status: RESEARCH. Wave 1 done, REVERSAL in progress.      │
│  Answers: does Mueller's full architecture produce          │
│  behaviors the Python approximation can't reach?            │
└─────────────────────────────────────────────────────────────┘
```

## Maturity Map

| Component | Status | What's proven | What's not |
|---|---|---|---|
| **Prep layer** | Done | Brief format works across 3 tonal registers. Capstone diagnostic is a reliable quality gate. | Only Puppet Knows has a fixture (world.yaml + dream_graph.json). |
| **Python Dreamer** | Mechanically proven | Obsession/drift/return dynamics. Four switching mechanisms (exhaustion, feedback, directed anger, congruence). All work with hand-authored echoes. | Hasn't been evaluated artistically — traces look right but nobody has watched the dream. |
| **Director** | Partially proven | Can wake dormant situations. Can introduce novel concepts. Can produce directed anger (with prompt guidance). | Can't reliably produce the full cascade autonomously. Target vocabulary drift. Prompt calibration ongoing. |
| **Stage adapter** | Exists | Wired from engine to Scope/Lyria REST. | Never run as a watched performance. No artistic evaluation. |
| **Clojure kernel** | Research | Wave 1 kernel runs. Puppet Knows benchmark end-to-end. REVERSAL primitive started. | No comparison against Python traces yet. No evidence yet that fuller Mueller machinery improves anything. |
| **"Interesting dream"** | Unproven | — | Nobody has watched a dream and said "yes, this is worth doing." |

## Where To Look Now

If you want to inspect the project rather than only talk about it,
start here:

- **Project map:** this note. It is the top-level summary of what is
  real, what is speculative, and what would falsify the direction.
- **Current feedback-loop view:** `/tmp/daydream_feedback_loop_compare.html`
  shows cycle-by-cycle Dreamer -> Director -> feedback -> next-cycle
  state for the latest offline runs.
- **How to run it live:** `scope-drd/RUNNING.md` has the current engine
  -> Scope/Lyria runtime commands.

## Evaluation Ladder

These are in order. Each one builds on the previous. You can't
meaningfully answer the later questions without answering the earlier
ones first.

### Level 1: Does the scheduler produce dream-shaped dynamics?
**Answer: Yes.** The 18-cycle traces show obsession (s1 dominates early),
drift (s3 wakes through exhaustion), return (s1 comes back changed).
This is proven through benchmark milestones and the trace reporter.

### Level 2: Does the Director change the trajectory?
**Answer: Partially.** With hand-authored echoes: yes, the full cascade
works (s1→s3→revenge→s4). With a live LLM Director: it can wake s3 and
reach n09, but revenge doesn't fire yet (target vocabulary problem,
being fixed). The Director demonstrably changes what happens — the
trajectory with Director feedback is different from without it.

### Level 3: Does the stage output feel like anything?
**Answer: Unknown.** The adapter exists but nobody has watched the
output and made a judgment. This is the most important unanswered
question. Everything below this line is speculative until someone
watches a dream.

### Level 4: Does a 12-cycle run feel dreamlike rather than slideshow?
**Answer: Unknown.** Depends on whether the transitions between nodes
feel like movement-of-mind or like jump-cuts. The dwell times (4-8s),
transition types (soft cuts, fades, holds), and the palette's visual
vocabulary all matter here. Can only be answered by watching.

### Level 5: Do different briefs produce meaningfully different dreams?
**Answer: Unknown.** Three briefs exist but only Puppet Knows has a
fixture. The hypothesis is strong (same scheduler + different brief =
different dream) but untested with actual video output.

## Decision Dashboard

### "Keep going" signals
- Stage output feels like something (Level 3 passes)
- Director feedback visibly changes the trajectory in a way that makes
  the dream more interesting than scheduler-alone
- Different briefs produce recognizably different dreams

### "Pivot Director design" signals
- Stage output is interesting but the Director's contributions feel
  generic or unhelpful
- The scheduler alone produces good enough trajectories and the
  Director is adding noise, not signal
- The prompt calibration never converges — the LLM can't reliably
  discover the concepts the hand-authored sidecar had

### "Drop Clojure" signals
- The Python engine's traces are already rich enough for the Director
- Mueller's full architecture doesn't produce meaningfully different
  dynamics (the generative-vs-descriptive question resolves as
  "descriptive")
- The Clojure work starts competing with stage integration for time

### "Wire stage sooner" signals
- You're here right now. You can't evaluate direction without seeing
  output. Everything else is architecture on faith.

### "This is not artistically paying off" signals
- The dreams feel like slideshows even with good transitions
- The emotional trajectory reads in the trace but doesn't read on screen
- The gap between Dreamer state and visual output is too wide — the
  viewer can't feel what the system is doing
- Different briefs produce output that feels the same

## What Would Falsify the Direction

The strongest version of "this might not work" is:

**The Dreamer's emotional dynamics might be invisible in video.** The
scheduler produces obsession, drift, return, directed anger — but those
are internal states. If the Director can't translate them into visual
differences that a viewer can feel, the whole architecture is an
elaborate engine producing output that looks like random clips.

The test is simple: watch a 12-cycle dream. Can you feel the obsession
building? Can you feel the drift when it comes? Can you feel the
surprise when the ring arrives? If you can, the architecture works. If
you can't, no amount of benchmark tuning will fix it.

**The creative briefs might not survive contact with video.** "The
puppet knows it's made of clay" is a strong concept on paper. It
produces good capstone sentences and interesting scheduler dynamics.
But when that becomes "a clay figure standing in a foam-core doorway,
rendered by KREA 14B," does the concept still land? Or does the
video model flatten everything into "puppet animation" regardless of
the brief?

**The Director might be unnecessary.** The scheduler alone produces
interesting traces. If the palette's visual vocabulary is rich enough,
maybe the node → palette cell → Scope prompt path is sufficient
without an LLM in the middle. The Director adds latency, cost, and
complexity. Its value proposition is "the dream is more interesting
with it than without it." That has to be demonstrated, not assumed.

## Recommended Next Step

One rough rendered run. Pick the best 18-cycle trace you have from the
hand-authored / sidecar benchmark (the `ring-congruence` run where all
four milestones land), render it through Scope with the palette, and
watch it. This is not the current best live-Director run; it is the
cleanest proof that the architecture can carry the full cascade when it
is fed the right echoes. Even without the Director producing visual
prompts — just the scheduler's node selection driving palette cell
choices.

That answers Level 3 before anything else. If it doesn't feel like
anything, the architecture needs to change. If it does, everything
you've built is confirmed and the Director's job is to make it better.
