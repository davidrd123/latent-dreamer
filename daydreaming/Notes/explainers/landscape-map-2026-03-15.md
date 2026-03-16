# The Landscape — March 15, 2026 (evening)

A map of where the project stands after a marathon session.

---

## What happened today

Started the day with a working generation prototype and four
benchmark fixtures. Ended with:

- Two bridge tests passing (Tessa + Kai — generated material
  survives L3 traversal, human read prefers generated prose)
- A supply pilot that practically passes (keeper yield proven)
- Temperature 0.2 discovered as a major missed lever (raised
  to 0.7, staging diversity immediately improved)
- A design space analysis placing the project in the
  believable-agent region, nearest to FAtiMA and Loyall/Hap
- The Director layer (541 lines of Clojure, partially tested)
  surfaced as a key missing piece in the dashboard
- A provocation seam experiment (FixtureDeltaV1) run — seam
  works mechanically, decision surfaces not yet wired
- The first inner-life trace viewer (watch_trace.py) —
  experiencing a traversal as a temporal sequence for the
  first time
- Eight nearest-neighbor source extractions (Loyall, ABL,
  FAtiMA, IPOCL, Sabre, MINSTREL, Generative Agents,
  Character is Destiny)
- Ten 5 Pro questions sent (D1-D10), four replies back
  (D3, D5, D6, D7) — all confirm the architecture holds,
  four middle contracts are missing

---

## The current plateau

**The generation pipeline works.** Four fixtures validated
across three operators. Batch generation, admission, keeper
banking, pack registry all operational. Bridge tests pass.
Supply pilot passes (Tessa clean, Kai narrow). Sonnet is the
default provider — produces better prose than Gemini, no quota
issues. Temperature 0.7 + consider-alternatives system prompt +
framing variation are load-bearing for keeper yield.

**The traversal scheduler works.** Graffito pilot, City Routes
three-arm comparison, conductor responsiveness, robustness sweep.
Generated material survives traversal without breaking anything.

**The provocation seam exists but isn't deep yet.** The overlay
interpreter applies FixtureDeltaV1 proposals correctly. The
downstream functions (concern inference, operator scoring) don't
consume the provoked state deeply enough yet. Codex1 is patching
the propagation.

**The first watch happened.** `watch_trace.py` formatted a
City Routes feature-arm trace into a per-cycle inner-life view:
tension bars, situation activation landscape, selection
competition, traversal intents, scene text. The dynamics are
legible — tension builds, situations wake and spread, shifts
feel motivated. But nobody has yet judged whether it *feels like
a mind at work* (Level 3).

---

## The project's unique position

From the design space analysis (Ask A):

> "Our foundation is tuned for one narrow prize: **legible inner
> drift under partial human steering.** If that prize is the one
> you actually care about, the architecture makes sense."

The differentiator: **Mueller gives us the cognitive process
between interactions — the daydreaming itself.** Rumination,
avoidance, rehearsal, rationalization when no one is watching.
FAtiMA does action selection during interaction. Generative
Agents does memory + reflection + planning as prompted summary.
We do the inner monologue that happens before the character
acts. No other architecture makes that process first-class,
inspectable, and steerable.

---

## The four-layer architecture

```
Conductor (human)         biases the Dreamer through faders
    ↓
Dreamer (L2/L3)           concerns, operators, memory,
                          pressure, traversal
    ↕ lossy feedback
Director (LLM)            interprets state, introduces events,
                          wakes dormant situations
    ↓
Stage                     inner-life view + audio + narration
                          + eventually video
```

Two lanes feed into this:

- **Authoring lane:** Prep → L1 orchestration → L2 generation
  → Provocation Generator → curation → graph compilation
- **Runtime lane:** authored graph → L3 traversal → Director
  feedback → adapter → Stage consumers

They meet at the authored graph. The Provocation Generator
(authoring-time world-event writer) and Director (runtime
interpretive perturbation) are separate agents with separate
contracts.

---

## Near peaks (reachable in the next few sessions)

### Level 3: does this feel like anything?

Step through a trace with `watch_trace.py --pause`. Or wire
Lyria RT to tension/energy and listen. This is the most
important unanswered question. Everything downstream is
conditional on it.

### Provocation propagation

Codex1 is patching the pipeline so provoked situation state
actually feeds concern inference and operator scoring. Once
that lands, rerun the Kai control and check whether the
provocations actually change what the character does.

### Graffito creative brief

Translate Mark's script + LoRA + creative direction into
`creative_brief.yaml` + `style_extensions.yaml`. This gates
the Director, which gates the runtime lane.

---

## Middle distance (needs real work)

### Counterfactual memory (D6)

Preserve one unrealized alternative per step as retrievable
lane-local memory. "Not enacted" and "not remembered" are
different. The dashboard needs a ghosted "almost happened"
channel. Early architectural correction, before full
watchable-runtime work.

### Pursuit threads (D3)

Lightweight `PursuitThreadV1` bridging L2 concerns (what
hurts) with L3 multi-situation coherence (what the character
is trying to do about it). Needed before full graph assembly.
Not a planner — a continuity hook and verifier handle.

### Checker layer (D5)

Reachability, dead branches, setup/payoff closure, orphan-
action audit. The propose → check → enhance shape for L1.
Needed before claiming multi-situation coherence.

### Richer viewer-facing projections (D5)

CyclePacketV1 + DashboardOverlayV1 as explicit contracts.
The dashboard should get projections, not raw state dumps.
Live execution state (what's currently active and why) beyond
what traces provide.

---

## Far mountains (real but distant)

### The conductor

APC Mini mapping, Wizard-of-Oz test, tidal oscillators.
The performance/daydreaming mode toggle. Five traversal
intents as directing vocabulary.

### Visual rendering through Scope

When GPUs are available. LoRA-based video from traversal.
The palette-cell → Director-generated prompt transition.

### L2 as live cognitive engine

The Clojure kernel running inside L3 at performance time.
Mueller's actual control loop with recursive reminding,
serendipity, concern competition. Research, not engineering.

### The persistent daemon (doc 34)

Mueller-shaped inner life as general cognitive infrastructure.
Writing companion, research daemon, vault agent with
preoccupations. Same mechanisms, different externalization.

---

## What we learned

### Lesson 1: milestone not mechanism (doc 16)

Check cheap levers (temperature, system prompt) before
building state machines. Set exit conditions before starting
diagnostic chains. The milestone is the success criterion.

### Lesson 2: the architecture holds

Eight nearest-neighbor systems analyzed. Four 5 Pro replies
drawing on all of them. None said "replace anything." All
said "four middle contracts are missing." The foundations are
right; the connective tissue between layers needs to be
explicit.

### Lesson 3: the first watchable thing is not video

It's the inner-life view + audio. Concern landscape shifting,
operators firing, memory surfacing, pressure building and
releasing. Then narration. Then Lyria RT. Video is downstream
and contingent on GPUs.

### Lesson 4: the unique thing is the daydreaming itself

Not the traversal (Facade does that). Not the appraisal
(FAtiMA does that). Not the memory (Generative Agents does
that). The inner cognitive process when the character is alone
with their concerns — the rumination, the rehearsal, the
avoidance ritual, the rationalization that rewrites what
happened. That's what no other system makes first-class.

---

## The weather

The risk is still trying to reach too many peaks at once.
The most important thing right now is looking at what the
system produces and forming a judgment. Does the City Routes
trace feel like a mind navigating a city under pressure?
Does the Kai avoidance feel like someone who can't open the
letter?

Everything else follows from whether those feel like something.
