# Thinking Through Bidirectionality

Working document — David thinking through the distinction that got
compressed during the research delivery sprint.

Date: 2026-03-12

---

## The Distinction

The vault agent's dismissal of bidirectionality conflated two different
things. The research's bidirectionality thesis was about both of them,
but the agent only saw the first:

### 1. Engineering Bidirectionality

Automated real-time sync between prose and a knowledge graph. Edit a
graph node, the prose updates. Edit the prose, the graph updates. No
production system does this. The engineering is genuinely unsolved —
extraction is lossy, regeneration destroys voice, round-tripping
accumulates errors.

The vault agent was right to dismiss this. The vault doesn't need it.
Markdown stays the source of truth. One-way compilation sidesteps the
whole problem.

### 2. Design Bidirectionality

How does agent work flow back to influence human work? How does the
human's current writing context shape what the agent surfaces? This is
not an engineering problem — it's a design problem about the *channel*
between two cognitive systems (human and agent) that see different
things.

The vault agent missed this entirely. But the vault's own design
experiments are all attempts to solve it:

- **The Membrane** — agent offerings appear in the margins of the
  writing surface. The writer can freeze, dismiss, respond, or cut
  them. Those interactions shape future surfacing. This is a designed
  bidirectional channel: agent → human (offerings) and human → agent
  (feedback on offerings).

- **The Drift** — discoveries surface during writing, not sitting in
  `_memory/`. The Drift needs a signal from the current writing context
  to know what to surface. Without that signal, it's just random
  retrieval.

- **The Séance** — frames write from their perspective, producing
  output the human might disagree with. The disagreement is generative.
  But frame outputs need to feed back into the atomic layer for frames
  to develop over time. Otherwise frames are stateless — they produce
  writing but don't learn from the response.

- **The Compost** — neglected material resurfaces. But what counts as
  "neglected" depends on what the human is currently attending to. The
  Compost needs a salience model to know what's been dormant relative
  to current work.

- **The Table** — frames convene around material. The arrangement
  depends on what's active. Active depends on what the human is doing.

Every one of these experiments requires a flow from human work → agent
context (so the agent knows what's relevant now) AND a flow from agent
work → human surface (so the agent's contributions are visible where
the writing happens). That's bidirectional coupling, mediated and lossy
by design.

---

## The Connection to Salience

The 5 Pro review identified a missing layer: salience / surfacing /
collaboration surface. This is not a coincidence. **The salience layer
IS the mechanism for design bidirectionality.**

The salience question — "what should the writing surface show right
now?" — requires two inputs:

1. **What the human is doing.** Current document, current section,
   recent edits, recent queries, what they've dismissed, what they've
   engaged with. This is the human → agent direction.

2. **What the agent knows.** Compiled structure, connections,
   contradictions, neglected regions, things that changed since last
   session, things that relate to the current work. This is the agent →
   human direction.

Without input #1, the agent surfaces random things (or everything).
Without input #2, the human has to ask for everything explicitly.
The Membrane is the interface where these two flows meet.

So the architecture question "what salience mechanism decides what the
writing surface should see right now?" is the same question as "how do
we build design bidirectionality?" — just phrased as engineering rather
than design.

---

## What the Research Actually Said

The landscape report's bidirectionality thesis wasn't about automated
sync. It was about *the loop*:

> The research assistant builds the graph during intake; the writing
> environment renders it navigable during composition; the writer's act
> of writing modifies the graph (new edges, revised weights, collapsed
> tensions).

That's three phases of a loop, not a real-time sync protocol. And the
third phase — "the writer's act of writing modifies the graph" — is
exactly what the Membrane, the Drift, and the Séance are reaching for.

The GPT 5.4 critique sharpened this with the salience requirement:

> A static graph is a dead warehouse. Global coherence in writing
> depends on at least three things: state (what's currently committed),
> salience (what is active right now), and control (what the writer is
> trying to do in this paragraph).

Without all three, the graph is a Notion wiki graveyard. This is why
the vault's `_memory/` directory feels inert — it has state (what the
agent recorded) but no salience (what's pressing) and no control (what
the writer is working on).

The Conducted Daydreaming connection: Mueller's DAYDREAMER has a
scheduler that IS a salience model — activation, decay, coincidence
retrieval, competing goal types. All mechanisms for answering "what's
pressing right now?" That's the same question.

---

## What Exists as Prior Art

The research found several patterns for designed bidirectional flow:

- **ARTIST** — argumentation graph alongside text. Students wrote 5.1
  arguments vs 3.2 with baseline, 60% structural improvement. The graph
  and the prose are side by side; the graph shapes the writing and the
  writing feeds the graph.

- **Graphologue** — extract-modify-regenerate cycle. Not real-time sync,
  but fast enough loops that the system feels responsive.

- **SNT source cards** — compiler output (which claims cite this source,
  what breaks if it changes) written back into source documentation.
  This is curated bidirectional flow: the graph's structure informs the
  human's understanding of each source.

- **SNT multi-model audit** — GPT challenges Claude's claims, human
  judges, KB corrects. Two AI systems and one human in a designed loop.
  Epistemic status tracks trust. This IS designed bidirectionality
  across systems.

- **Dreamer↔Director** — the Conducted Daydreaming architecture. Two
  systems with different surfaces influencing each other through a lossy
  channel. The Director doesn't have direct access to the Dreamer's
  internal state — it influences through goals and interrupts. The
  Dreamer doesn't have direct access to the Director's plan — it
  influences through reports and creative output.

---

## What's Actually Open

The distinction between engineering and design bidirectionality is clear.
The vault's experiments are all reaching for design bidirectionality.
The salience layer is the mechanism. So what's actually unresolved?

1. **The salience algorithm itself.** None of the vault's experiments
   specify how salience is computed. The Drift says "wander" — wander
   where? The Membrane says "surface offerings" — which ones? The
   Compost says "neglected material" — neglected relative to what?
   Activation/decay from DAYDREAMER is one model. Recency + relevance +
   surprise is another. Embedding similarity to current work context is
   the obvious baseline. But nobody in this thread has proposed a
   concrete mechanism.

2. **The feedback signal.** For the Membrane to learn what to surface,
   it needs signal from the human. Dismiss, engage, freeze, cut are
   interaction types. But what data do those produce? A dismiss is weak
   negative signal (maybe wrong time, not wrong content). An engage is
   strong positive. A freeze is "not now but keep it." How do those
   accumulate? Is there a decay? Does the signal transfer across
   sessions?

3. **The compilation target for creative work.** The A+ compiled layer
   gives the agent structured access to vault material. But which
   structure matters for salience? Typed queries ("what supports this?")
   are one use. But the salience layer might need different structure —
   recency, activation level, connection density, distance from current
   work. The compiler might need to produce salience-relevant metadata,
   not just query-relevant structure.

4. **Frame state.** The Séance produces frame output. Currently frames
   are stateless — they don't remember previous outputs or how the human
   responded. For frames to develop, they need to carry state: what has
   this frame said before? What did the human do with it? What changed
   in the frame's territory since last invocation? This is a design
   bidirectionality problem specific to frames.

5. **The unit of flow.** The SNT source cards flow at the source level.
   The Membrane flows at the offering level. The Séance flows at the
   frame level. The Drift flows at the connection level. Is there one
   right granularity, or is the right answer that different experiments
   use different units and we learn from that?

---

## Why This Matters for John

John's vault agent dismissed bidirectionality. The memo gently flags
this in Part 2. But the deeper point — that the vault's own design
experiments are all bidirectionality experiments, just not using that
word — is something worth developing for the live Thread 6 conversation.

If John is interested in the architecture question (and he clearly is),
the bidirectionality framing connects his graph-structure instincts to
the vault's creative experiments in a way that "Graphiti vs. not" never
did. The graph isn't the point. The flow between the graph and the
writing surface is the point. That's what makes the graph pay rent.

This might be the bridge between John's infrastructure instincts and the
vault's creative aspirations — not "should we use a graph database" but
"how does structure flow into writing and how does writing flow back
into structure?"
