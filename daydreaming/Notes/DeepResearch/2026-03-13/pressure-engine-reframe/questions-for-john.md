# Questions / Discussion Topics for John

This is not a question pack to send cold. It's preparation for
verbal conversation — topics to think through enough that David
can explain them clearly and get John's practiced reactions.

John's background: head of virtual production for major films.
He knows real-time creative control, directing instincts, what
makes sequences feel intentional, and how control surfaces map
to creative decisions.

---

## Context to explain first

Before any of the questions make sense, John needs to understand
three things:

### 1. What the system does

We have a cognitive engine (inspired by a 1990 AI system called
DAYDREAMER) that drives traversal through a pre-authored dream
graph. Characters in the graph have emotional states and motivated
inner lives. The engine decides what to show and when, based on
dramatic pressures — not random, not scripted, but pressure-driven.

The output is real-time text-to-video (Scope) plus an inner-life
dashboard showing what's happening inside the characters and why
the system is making the choices it's making.

A human performer conducts the dream live with an APC Mini —
shaping pacing, tension, emphasis, and overrides without choosing
content directly.

### 2. The directing thesis

The system's traversal scheduler is essentially an **automated
director** making sequencing decisions over authored material.
We modeled it on drama management research (Facade, a system
from the interactive drama community) rather than on story theory
or screenwriting structure.

The scheduler has five moves:
- **dwell** — stay here longer
- **shift** — move to a different situation
- **recall** — return to earlier material
- **escalate** — move toward higher pressure
- **release** — move toward relief

The question is whether these five verbs capture real directing
instincts or are just plausible abstractions.

### 3. The conductor model

The human performer doesn't choose content — they shape how the
system's own intelligence unfolds. Like conducting an orchestra:
the score (authored graph) exists, the musicians (engine) know
how to play, the conductor shapes interpretation.

The performer controls:
- Situation emphasis (which emotional threads to foreground)
- Pacing (hold longer, move faster)
- Tension bias (push toward confrontation or relief)
- Recall (bring back earlier material)
- Event commitment (approve or veto canonical changes)

---

## Topics for discussion

### A. Do the five traversal intents feel real?

When you're directing or supervising a sequence, do you recognize
these as actual moves you make?

- **dwell** — holding on a moment because it hasn't been fully
  extracted yet
- **shift** — cutting away because this thread is exhausted or
  needs to breathe
- **recall** — returning to something from earlier because it now
  means something different
- **escalate** — pushing toward the thing the sequence has been
  building toward
- **release** — letting the audience breathe after sustained
  intensity

Are there moves you make that aren't captured here? Is there a
move that's more fundamental than these five? Are any of these
actually the same move in practice?

The academic work we drew from (Facade) uses a different vocabulary:
priority tiers, candidate scoring, feature weights. Our five intents
are our translation of that into directing language. Does the
translation hold?

### B. What makes a sequence feel intentional vs. random?

This is the core question for the first experiment. We're going to
compare a pressure-driven scheduler against a simpler weighted
traversal over the same authored graph. The test is whether a
human viewer can tell the difference — whether one feels like
"a mind moving through charged material" and the other feels like
a slideshow.

From your experience: what are the telltale signs that sequencing
is intentional? What makes you feel like there's a directing
intelligence at work? Is it:
- Timing of returns?
- Earned escalation (setup before payoff)?
- Contrast between consecutive moments?
- The feeling that something is being avoided?
- The feeling that the system "remembers" what it showed earlier?

### C. The inner-life dashboard

We have a companion view that shows the system's cognitive state:
what characters are concerned about, what they're rehearsing or
avoiding, why the system chose this node, what it's fixating on.

For early versions (before the video rendering is good enough to
carry the experience alone), this inner-life view might actually
be the primary output — the thing that's interesting to watch.

Does that resonate? In VP, is there an analog — a view of the
system's decision-making that's compelling to watch even when the
final render isn't ready? Does the "making-of" view have value
as a product, not just as a debug tool?

### D. Conductor control surfaces

We have an APC Mini mk2: 8 faders, a grid of pads, some knobs.
The proposed conductor state has about 9 dimensions: energy,
tension, density, clarity, novelty, tempo, phase, hold, focus.

From your VP experience with control surfaces: what feels playable
vs. what's theoretically nice but physically unworkable? How many
independent dimensions can a performer actually manage in real
time? Which of these dimensions map naturally to faders vs. buttons
vs. something else?

The bigger question: in VP, when you have a complex system with
many parameters, how do you decide what the operator actually
touches? What's the design process for going from "the system can
do 50 things" to "the performer controls 6 things"?

### E. Visual world-building

We've been building a pipeline that generates visual anchors
(character portraits, location references, mood variants) from
text descriptions, using Gemini for generation and evaluation.

The pipeline works but the output is "AI-generated images." From
your production experience: at what point in the process do you
care about visual quality? Is there a stage where rough visual
reference is more useful than polished reference because it leaves
room for interpretation? What makes a visual reference actually
useful for a production team vs. just pretty?

### F. What would you want to see in the watched run?

When we have the first Graffito pilot output (a 12-18 cycle
traversal through authored dream material, with the inner-life
dashboard alongside), what would you be looking for? What would
make you say "this is worth pursuing" vs. "this is just a tech
demo"?

Is there a specific thing you'd want to see the system do that
would convince you it's not just a glorified playlist?

---

## Topics to defer

These are real questions but too architectural for a first
conversation:

- How character-level concerns should propagate into graph
  annotations (cross-level pressure propagation)
- Whether the world-building critic should use a pressure loop
  or simpler structured prompting
- The specific DAYDREAMER mechanisms (reversal, rationalization,
  serendipity) and whether they matter for the directing problem
- Whether tidal oscillators (slow autonomous arcs) would improve
  the conducted experience

These can come up if John is interested, but they need the
foundational context from the topics above first.

---

## How to use this doc

1. Read through the "context to explain" section until you can
   explain it cold without referencing the doc
2. Pick 2-3 topics from A-F that feel most natural for a
   conversation with John
3. Show him the cognitive visualizer and/or a narration companion
   trace if possible — concrete demos ground the conversation
4. His reactions to what he sees are more valuable than his
   responses to abstract questions
5. Take notes on what surprises him, what he pushes back on,
   and what analogies he reaches for from his VP experience
