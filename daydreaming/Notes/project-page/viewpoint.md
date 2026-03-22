# Project Page — Viewpoint Draft

Date: 2026-03-21

This is the honest starting point for the project page. Not a demo
page. A research/foundation page: what it is, where it came from,
what it enables.

---

## What it is

A cognitive engine that gives a language model capabilities it
doesn't natively have: persistent concerns, episodic memory with
planning structure, retrieval biased by emotional state, involuntary
daydreaming goal activation. The LLM inhabits the character. The
engine gives it a mind.

The architecture has four layers:

```
World           canon, constraints, motifs, authored situations
    |
Cognitive       Mueller's daydreaming loop in a Clojure kernel —
                concerns, operators, memory, pressure, traversal
    |
Graph           nodes, edges, status tags, connection graph
    |
Stage           narration + audio (Lyria RT) + visuals (Scope)
```

The offline engine works across World, Cognitive, and Graph. Live
performance works across Graph and Stage. The stage layer already
exists from earlier work — real-time video via Scope, APC Mini MK2
hardware controller, Lyria RealTime integration. This repo builds
the dream/narrative layer that feeds it.

The engine runs Mueller's daydreaming cycle: a character worries,
rehearses conversations, rationalizes failures, reverses past
decisions, drifts to pleasant memories under pressure. Each of
those strategies is a typed mechanism with explicit triggers,
structural preconditions, and observable consequences. The LLM
provides contextual judgment at bounded sites inside those loops.
The engine owns the loops.

The result is runtime prose — inner-life narration — that is
caused by persistent cognitive state, not just generated from a
prompt. When the engine writes residue back into memory, it
changes what the character retrieves and does next.

## Where it came from

Erik Mueller's 1990 book *Daydreaming in Humans and Machines*
described a complete cognitive architecture for daydreaming: 19
mechanisms covering emotion-driven control, concern initiation,
planning, retrieval, reminding, serendipity, rationalization,
reversal, and episode evaluation. Mueller hand-coded 135 rules in
a Lisp system that ran once per session.

The project had been building from Mueller's domain application
(interpersonal daydreaming, Chapters 3 and 6) rather than his
core engine. The condensation corrected that: Chapters 4 and 5 —
learning through daydreaming, everyday creativity, serendipity,
mutation — are the actual engine. Daydreaming IS learning. Episodes
are planning trees, not narratives. Reversal, rehearsal,
rationalization generate hypothetical episodes that get stored and
reduce future search time.

We condensed the full book into 19 typed mechanism cards, each
specifying the loop shape, the interface boundaries, the
properties a modern implementation must preserve, and where LLM
judgment can enter without breaking the structural guarantees.

The hybrid architecture that emerged: Clojure owns the persistent
data structures (rules, episodes, contexts, concerns) and the
recursive control loops (reminding cascade, analogical repair,
serendipity verification). LLMs supply contextual judgment and
content generation at bounded sites inside those loops. Rules are
plain data — searchable in a connection graph, indexable in
episodes, traversable for serendipity — but their consequents can
invoke an LLM when they fire.

Mueller's mechanisms aren't an expert system to implement. They're
prosthetics that give an LLM capabilities it doesn't natively
have — persistent emotions, episodic memory with planning
structure, retrieval biased by emotional state, involuntary
daydreaming goal activation.

The rule connection graph is derived from rule schemas, not from
runtime behavior. That means untraversed paths remain discoverable.
Serendipity is structural path search plus verification, not
embedding similarity.

Outside architecture review (5 Pro) sharpened the design: added a
denotational contract layer to the rule schema (catches valid-but-
wrong LLM output), identified consolidation as a post-Mueller
extension for multi-session growth, flagged descriptor rigidity
(Copycat/Hofstadter) as the deepest open challenge for
serendipity, and gave the falsification criterion: if a rule from
session 1 doesn't change reachable behavior in session 2 without
re-pasting the trace, you don't have a new cognitive substrate —
just better memory.

## What it enables

### A performance instrument with an inner life

A human performer steers the cognitive engine via faders and pads.
The engine's concerns, operators, memory, and pressure respond to
the performer's input and produce dynamics worth watching, hearing,
and eventually rendering as video. The inner life is legible on
stage through narration, audio (Lyria RT), and visualization.

The first creative brief is Graffito (Mark Friedberg's script).
Mark hasn't seen anything yet.

### Prosthetic inner life for language models

The LLM has no mind of its own. The engine gives it one. Between
interactions, the character daydreams — rehearses, rationalizes,
avoids, roves. You come back, and it's different. The dashboard
shows what happened while you were gone. This is not a memory
layer bolted onto a chat model. It is typed cognitive machinery
that shapes what the model can think next.

### Accumulation across sessions

The engine's episodic memory, rule base, and connection graph grow
through operation. A rule discovered by reversal enters the graph
and becomes available for future serendipity search. An episode
stored with explicit indices becomes retrievable by later
reminding. The system's creative capacity compounds — if the
architecture works.

### Future directions

The memory interface itself is an optimization target. GradMem-
style selective compression could improve what gets stored. RLM-
style recursive navigation could let the LLM actively query the
episode store rather than receiving a fixed packet. DSPy could
optimize the write/read prompts against retrieval quality. The
kernel's structural boundaries (Clojure-owned indices, coincidence-
mark retrieval, recursion depth bounds) are the invariants that
any optimization respects.

## What exists now

- Clojure kernel running Mueller's control loop (~40% of the full
  system): episodic memory with coincidence-mark retrieval,
  immutable context tree with sprouting, goal competition and
  decay, five daydreaming families (reversal, roving,
  rationalization, recovery, rehearsal)
- Rule engine with RuleV1 schema (three-layer split: schema /
  denotation / executor), custom unifier/matcher, connection
  graph construction, graphable trigger/consumer rule pairs,
  path search and explanation
- Runtime thought beat projector: LLM realizes each kernel cycle
  as 2-3 sentences of inner-life prose, conditioned on the
  kernel's structural state
- Feedback loop: thought beat residue writes back into episodic
  memory, changing retrieval on subsequent cycles (tested,
  divergence confirmed cycles 4-12)
- L3 traversal scheduler validated on two benchmarks (Graffito,
  City Routes)
- Generation pipeline for authored material supply (keeper bank,
  bridge tests passed)
- Stage layer from Round 02: real-time video via Scope, APC Mini
  MK2 controller, Lyria RealTime, palette pipeline
- 19 mechanism cards condensed from Mueller's book with hybrid
  integration patterns
- Architectural framing, evaluation ladder (4 levels), outside
  review absorbed
- Mueller's book fully digitized (15 chapters + appendices as
  markdown, PDF, image review audit)

## What's proven

- The kernel can drive LLM narration that reads as inner life,
  not system reports
- That narration feeds back and structurally changes subsequent
  cognition (cycles 4-12 diverge from no-feedback baseline)
- Different models produce different downstream retrieval and
  node selection — the quality of what the LLM says changes what
  the character thinks about next
- The rule engine mediates real structural transitions in the
  kernel (activation and planning layers)
- Generated material survives traversal (bridge tests passed for
  two fixtures)

## What's not proven

- The rule connection graph has nodes and edges but no serendipity
  traversal yet (Chain B — the accident engine — is ~5% recovered)
- Cross-session accumulation (the Level 4 falsification test)
- Nobody has watched a full run with audio and narration together
- Multi-situation generation hasn't been attempted
- The Director (runtime perturbation agent) has reliability issues
- Graffito hasn't been assembled as a full graph
- Mark Friedberg hasn't seen anything
- Consolidation/compression for multi-session growth

## The honest claim

This is a foundation, not a finished instrument. The architecture
is designed, partially built, and partially tested. The feedback
loop works in miniature. The rule engine mediates real structural
transitions but doesn't yet own behavior. The creative engine —
serendipity, bridge discovery, cross-session compounding — is
designed and has a clear build path, but is not built.

The bet is that persistent typed cognitive structure, owned by a
symbolic engine and augmented by LLM judgment at bounded sites,
produces behavior that accumulates in ways no prompt-only system
can match. The falsification criterion is explicit: if it doesn't,
the architecture doesn't earn its keep.
