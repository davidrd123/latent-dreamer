# Conducted Daydreaming: An Illustrated Explainer

How a 1990 cognitive architecture meets modern generative AI
to produce a performed experience of inner life.

---

## What is this?

A system where a human performer steers a traversal engine through
authored dream material, producing real-time video, music, narration,
and a live view of character cognition. Think of it as conducting an
orchestra — except the orchestra is a mind, and the score is a graph
of psychologically charged moments.

```
 THE PERFORMER            THE ENGINE              THE OUTPUT
 ┌──────────┐     ┌─────────────────────┐     ┌──────────────┐
 │ APC Mini │────>│  Traversal Engine   │────>│  Video       │
 │ faders   │     │  (which moment now? │     │  Music       │
 │ pads     │     │   why? what next?)  │     │  Narration   │
 │          │     │                     │     │  Inner Life  │
 └──────────┘     └─────────────────────┘     │  Dashboard   │
                          │                    └──────────────┘
                          │
                  ┌───────┴───────┐
                  │ Authored      │
                  │ Dream Graph   │
                  │ (the "score") │
                  └───────────────┘
```

---

## The two big problems

### Problem 1: Traversal (solved)

Given a graph of authored moments, how does the engine choose what
to show, in what order, with what pacing?

**Answer:** A Facade-shaped scheduler with a DODM-style feature
registry. It scores candidate nodes against a desired trajectory,
filters by priority tiers, and soft-weights by recency, situation
activation, and conductor bias.

This is validated. Three comparison arms, three graph substrates,
seed-robust. The scheduler produces visibly more intentional
traversals than simpler alternatives.

### Problem 2: Material supply (current focus)

Where does the graph come from? Hand-authoring 200+ annotated nodes
is a wall. The engine needs material.

**Answer:** A generation pipeline that takes narrative primitives
(characters, contradictions, backstory, charged places) and produces
candidate moments through Mueller-style cognitive operators, with
episodic memory accumulation across passes. Humans curate.

This is validated at the single-step level. Three benchmark fixtures
show the pipeline produces graph-compilable material with operator
control that responds to appraisal and practice context.

---

## The architecture at a glance

```
           AUTHORING LANE                    SHIPPING LANE
           (research time)                   (performance time)

     Characters, World Rules               Authored Dream Graph
     Backstory, Contradictions           + visit history
              │                          + conductor bias
              v                                  │
     ┌─────────────────┐                         v
     │ L1: Authoring   │                ┌─────────────────┐
     │ Orchestration   │                │ L3: Traversal   │
     │                 │                │ Scheduler       │
     │ Stage 1:        │                │                 │
     │  Generation     │                │ Facade pipeline │
     │  from           │                │ + DODM features │
     │  primitives     │                │ + conductor     │
     │                 │                │   bias          │
     │ Stage 2:        │                └────────┬────────┘
     │  Critic /       │                         │
     │  repair         │                         v
     └────────┬────────┘                ┌─────────────────┐
              │                         │ Cycle Packet    │
              v                         │ (what to show)  │
     ┌─────────────────┐                └────────┬────────┘
     │ L2: Character   │                         │
     │ Exploration     │              ┌──────────┼──────────┐
     │ Engine          │              v          v          v
     │                 │          ┌───────┐ ┌────────┐ ┌───────┐
     │ Mueller's core: │          │ Inner │ │ Video  │ │ Music │
     │ concerns,       │          │ Life  │ │(Scope) │ │(Lyria)│
     │ operators,      │          │ Dash  │ │        │ │       │
     │ memory          │          └───────┘ └────────┘ └───────┘
     └────────┬────────┘
              │
              v
     ┌─────────────────┐
     │ Human Curation  │
     │ (keep / reject) │
     └────────┬────────┘
              │
              v
     ┌─────────────────┐
     │ Authored        │─ ─ ─ ─ ─> feeds the shipping lane
     │ Dream Graph     │
     └─────────────────┘
```

The graph sits between the lanes. L1/L2 write into it during
authoring. L3 reads from it during performance. The graph is the
membrane — it carries stable annotations, not live engine internals.

---

## What Mueller's DAYDREAMER provides

Erik Mueller built DAYDREAMER in 1990 — a computational model of
the stream of consciousness. The system is long gone, but the
*architecture* is exactly what this project needs.

### The control loop

```
 ┌─────────────────────────────────────────────────────┐
 │                                                     │
 │  1. What's pressing?  ─── Select dominant concern   │
 │         │                                           │
 │         v                                           │
 │  2. What does it mean? ── Build causal              │
 │         │                 interpretation             │
 │         v                                           │
 │  3. How bad is it?    ── Appraise                   │
 │         │                                           │
 │         v                                           │
 │  4. What can I do?    ── Check social context,      │
 │         │                 available moves            │
 │         v                                           │
 │  5. What kind of      ── Choose operator:           │
 │     thinking?             rehearse? avoid?           │
 │         │                 rationalize?               │
 │         v                                           │
 │  6. Generate moment   ── LLM behind typed boundary  │
 │         │                                           │
 │         v                                           │
 │  7. What changed?     ── Reappraise, update         │
 │         │                 concerns                   │
 │         │                                           │
 │         └────────────────── loop ────────────────────┘
```

### The seven operators (what kind of thinking?)

Each operator produces a *different kind* of inner moment:

| Operator | What it feels like | Example |
|----------|-------------------|---------|
| **Rehearsal** | Practicing what to say or do next | Maren mouths three different openings at the rehearsal-room door |
| **Avoidance** | Choosing not to engage with what demands engagement | Kai fills the kettle instead of opening the letter |
| **Rationalization** | Reframing a failure to reduce its sting | "If it truly mattered, she wouldn't have trusted paper to carry it" |
| **Reversal** | Imagining how things could have gone differently | What if I'd answered her call that night? |
| **Roving** | Mind sliding to something pleasant to escape pressure | A memory of laughing on the ferry as teenagers |
| **Recovery** | Planning a future attempt after a past failure | Next time I see her, I'll start with the truth |
| **Revenge** | Imagining retaliation or the other's failure | She'll realize what she lost when I don't show up |

These aren't just labels. They produce *structurally different*
scenes with different tension profiles, different energy signatures,
and different implications for what comes next.

### Episodic memory

The system remembers what it has already thought about.

```
 Pass 1: Kai rehearses what to say at the harbor.
          The rehearsal falls apart when Lucy's face intrudes.
          ───> stored as episode

 Pass 2: Rationalization fires on the same concern.
          Episodic memory retrieves the failed rehearsal.
          Now the rationalization has to work harder —
          it knows the rehearsal didn't hold.
          ───> stored as episode

 Pass 3: Avoidance fires.
          Memory retrieves BOTH the failed rehearsal
          AND the strained rationalization.
          The avoidance carries the weight of two
          failed attempts to face the thing.
```

Each pass is different because the character's cognitive history
has changed. That's what makes generated material feel like it
came from a mind, not a scene generator.

---

## The middle layer (what makes generation structured)

Between "this character has a concern" and "generate a scene,"
there are four steps that make the output psychologically specific:

```
 ┌─────────────────────────────────────────────────────────┐
 │                                                         │
 │  CHARACTER STATE                                        │
 │  "Kai has attachment_threat                             │
 │   toward his sister"                                    │
 │         │                                               │
 │         v                                               │
 │  CAUSAL SLICE                                           │
 │  "His loyalty goal is threatened                        │
 │   by his own intentional inaction.                      │
 │   Controllability is low. The deadline                  │
 │   is tonight."                                          │
 │         │                                               │
 │         v                                               │
 │  APPRAISAL                                              │
 │  "This is bad (-0.78), likely (0.82),                   │
 │   hard to control (0.28), and partly                    │
 │   his fault (-0.52 praiseworthiness)"                   │
 │         │                                               │
 │         v                                               │
 │  PRACTICE CONTEXT                                       │
 │  "He's in precontact evasion.                           │
 │   Available moves: delay contact,                       │
 │   ritual distraction, prepare excuse"                   │
 │         │                                               │
 │         v                                               │
 │  OPERATOR SCORING                                       │
 │  "Avoidance wins because: low control                   │
 │   + evasion practice + retrieved memory                 │
 │   of silence becoming the message"                      │
 │         │                                               │
 │         v                                               │
 │  GENERATION (LLM)                                       │
 │  "Kai lifts the envelope, reads only                    │
 │   his sister's name, turns it face                      │
 │   down again, and fills the kettle                      │
 │   as if the water can decide whether                    │
 │   the harbor still belongs to them."                    │
 │                                                         │
 └─────────────────────────────────────────────────────────┘
```

Each step narrows and sharpens the next. Remove the CausalSlice
and you lose "why this is threatening." Remove the AppraisalFrame
and you lose "why avoidance wins over rehearsal." Remove the
PracticeContext and you lose "what avoidance looks like in this
social situation."

### Where four sources contribute

```
 MUELLER ─── the control loop        "What runs next?"
 EMA    ─── appraisal dynamics       "Why did this pressure change?"
 OCC    ─── emotion semantics        "What kind of state is this?"
 VERSU  ─── situation structure      "What moves are available here?"
```

Not four competing theories. Four orthogonal answers to four
different questions.

---

## What LLMs change

Mueller built DAYDREAMER in Lisp with hand-coded rules. Every
scenario required explicit programming. The architecture was right
but the content machinery couldn't scale.

```
 MUELLER (1990)                    THIS PROJECT (2026)
 ┌────────────────┐               ┌────────────────┐
 │ Control loop   │──── kept ────>│ Control loop   │
 │ Concern mgmt   │──── kept ────>│ Concern mgmt   │
 │ Episodic memory│──── kept ────>│ Episodic memory│
 │ Operators      │──── kept ────>│ Operators      │
 │ Serendipity    │──── kept ────>│ Serendipity    │
 │                │               │                │
 │ Hand-coded     │               │ LLM-generated  │
 │ rules for      │── replaced ──>│ content behind │
 │ every scenario │               │ typed operator │
 │                │               │ boundaries     │
 │ Brittle index  │               │ Embedding +    │
 │ retrieval      │── upgraded ──>│ tag retrieval  │
 │                │               │                │
 │ Discrete       │               │ Continuous     │
 │ emotion labels │── upgraded ──>│ appraisal      │
 │                │               │ framework      │
 │ No common      │               │ LLM's implicit │
 │ sense          │── resolved ──>│ world knowledge│
 └────────────────┘               └────────────────┘
```

The architecture is Mueller's. The muscles are LLMs.

---

## The conductor

A human performer with an APC Mini mk2 steers the traversal
in real time. They don't script scenes — they shape pressure,
pacing, and focus.

```
 8 FADERS                          8 PADS
 ┌────────────────────────┐       ┌────────────────────┐
 │ F1  Hold ↔ Release     │       │ Threshold pulse    │
 │ F2  Escalation         │       │ Exchange pulse     │
 │ F3  Recall             │       │ Spectacle pulse    │
 │ F4  Intensity          │       │ Refuge pulse       │
 │ F5  Threshold lane     │       │ Hold latch         │
 │ F6  Exchange lane      │       │ Recall pulse       │
 │ F7  Spectacle lane     │       │ Commit confirm     │
 │ F8  Refuge lane        │       │ Commit veto        │
 └────────────────────────┘       └────────────────────┘
```

The five traversal intents emerge from combinations:

- **dwell** = hold up, intensity low-mid
- **release** = hold down, intensity down, maybe refuge lane up
- **recall** = recall fader or pulse
- **escalate** = escalation high + intensity high + a lane
- **shift** = change lane emphasis while hold is low

The performer doesn't select intents directly. They shape
the bias field, and the scheduler interprets it.

---

## The inner-life dashboard

The primary output for early runs. Not a debug view — a designed
surface showing what's happening inside the characters.

### Performance mode (watching a run)

```
 ┌─────────────────────────────────────────────────┐
 │ LIVE BAND                                       │
 │ Node: kai_letter_avoidance_001                  │
 │ Intent: dwell │ Tension: ██████░░ │ Energy: ███ │
 │ Event: approaching (harbor meeting tonight)     │
 ├─────────────────────────────────────────────────┤
 │ CONTEXT BAND                                    │
 │ Path: street → apartment → street(recall)       │
 │ Situation: kitchen / precontact evasion          │
 │ Return after: 4 cycles                          │
 ├─────────────────────────────────────────────────┤
 │ INSPECTOR (collapsed)                      [+]  │
 │ Why: low controllability + evasion practice...   │
 └─────────────────────────────────────────────────┘
```

### Authoring mode (curating candidates)

```
 ┌───────────────────────────────────────────────────┐
 │ CANDIDATE                                         │
 │                                                   │
 │ "Kai lifts the envelope, reads only his sister's  │
 │  name, turns it face down again..."               │
 │                                                   │
 │ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ │
 │ │ FREEZE  │ │ DISMISS │ │ RESPOND │ │   CUT   │ │
 │ └─────────┘ └─────────┘ └─────────┘ └─────────┘ │
 ├───────────────────────────────────────────────────┤
 │ GRAPH PROJECTION                                  │
 │ situation: sit_unopened_letter                     │
 │ delta_tension: +0.10 │ option_effect: clarify     │
 │ pressure: attachment_threat, obligation            │
 │ setup: ev_harbor_meeting_tonight                   │
 ├───────────────────────────────────────────────────┤
 │ PROVENANCE                                   [+]  │
 │ Concern: attachment_threat → CausalSlice →         │
 │ Appraisal (ctrl: 0.28) → Evasion → Avoidance     │
 └───────────────────────────────────────────────────┘
```

The narration companion speaks the inner life:
- "Holding here, pressure still rising."
- "Returning to the threshold after a long gap."
- "Release move, the route backs away."

Not: "The scheduler selected this because recall_value=0.41."

---

## What's proven vs. what's planned

| Component | Status |
|-----------|--------|
| L3 traversal scheduler | **Validated** — beats baseline, scales, ablations stable |
| Conductor surface | **Partially validated** — works on Graffito, limited on City Routes |
| Generation pipeline (single-step) | **Validated** — three benchmarks, operator control generalizes |
| Concern inference from primitives | **Shadow-mode validated** — recovers seeded concerns |
| Multi-step accumulation | **Designed, not built** |
| Candidate compilation | **Designed, not built** |
| Dashboard / narration | **Partially built** — cognitive visualizer exists |
| Conductor → APC Mini mapping | **Designed, not tested** |
| Rendered watched run | **Not done** — the art question |
| L2 kernel refactor | **Designed, deferred** |
| L1 critic / repair | **Designed, deferred** |

---

## The broader thesis

The mechanisms being built here — concern-driven cognition,
episodic memory with accumulation, typed operators, appraisal-
shaped generation, serendipity through cross-concern contamination —
are not performance-specific. They're general-purpose cognitive
infrastructure for any agent that needs inner life.

A memory system answers questions about what it knows.
A cognitive system generates questions about what it doesn't
understand.

The conducted daydreaming system is the first application.
A persistent creative daemon is the second.
