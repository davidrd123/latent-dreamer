Title: Latent Dreamer — David Dickinson

URL Source: https://app.daydream.live/creators/ddickinson/latent-dreamer

---

A cognitive engine that gives a language model capabilities it
doesn't natively have: persistent concerns, episodic memory with
planning structure, retrieval biased by emotional state, and
involuntary daydreaming between interactions.

The LLM inhabits the character. The engine gives it a mind.

---

## What It Is

This is not a memory layer bolted onto a chatbot. It is a hybrid
cognitive architecture built from Erik Mueller's *Daydreaming in
Humans and Machines* (1990), adapted for language models and live
performance.

The architecture has four layers:

```
World           canon, constraints, motifs, authored situations
    |
Cognitive       Mueller's daydreaming loop in a Clojure kernel:
                concerns, operators, memory, pressure, traversal
    |
Graph           typed rules, connection graph, structural paths,
                rule accessibility frontier
    |
Stage           narration, audio, visualization, live control
```

The offline engine works across World, Cognitive, and Graph. Live
performance works across Graph and Stage. The stage layer already
exists from earlier rounds: Scope for real-time video, APC Mini MK2
for conduction, and Lyria RealTime for audio. This round builds the
mind that feeds that stage.

The result is runtime prose caused by persistent cognitive state,
not just generated from a prompt. When the system writes residue
back into memory, it changes what the character retrieves and does
next.

---

## The Core Claim

Language models can perform a character fluently, but they do not
have durable emotional pressure, self-built episodic memory, or a
mind that keeps working when nobody is talking to it.

This project gives them those missing capabilities with typed
cognitive machinery:

- concern-driven control
- episodic retrieval over explicit named indices
- family operators: roving, rationalization, reversal, rehearsal,
  recovery
- context sprouting and planning structure
- writeback from generated thought into future cognition
- a memory membrane that prevents self-reinforcing grooves

The field often asks: "how do we make LLMs plan reliably?"
This project is asking a different question: "how do we give LLMs
inner life?"

---

## How It Works

Each kernel cycle runs a bounded loop:

1. Decay emotional and motivational state.
2. Select the concern with the strongest current pull.
3. Run a family plan for that concern.
4. Retrieve episodes whose explicit indices overlap with what is
   currently active.
5. Store the plan result as a new episode with admission state.
6. Optionally project the state into an inner-life thought beat.
7. Write the thought residue back into memory.

Episodes are not embedding blobs. They are structured records with
explicit indices, typed cue zones, provenance, and admission state.
Retrieval is coincidence-mark counting over named cues — Mueller's
original design, reconstructed from his Lisp source code and
extended with a memory membrane.

The membrane prevents the system from developing grooves —
self-reinforcing loops where prior rationalizations make future
rationalization easier, or prior counterfactuals become standing
causal priors. Episodes enter as provisional and must earn
durability through attributed downstream evidence: cross-family
usefulness, not just self-retrieval.

The newer substrate is rules-as-data. Rule behavior is being moved
out of opaque function bodies into typed `RuleV1` data with three
layers:

- schema: antecedents and consequents (graphable)
- denotation: what state change the rule is meant to achieve
- executor: how the result is computed (instantiate, Clojure
  function, or LLM-backed)

Once behavior becomes data, the system can:

- derive a connection graph from rule schemas
- store rule-path provenance inside episodes
- traverse structural paths that have never been executed before
- gate which rules the planner can see vs. which serendipity can
  discover

That is the build path toward serendipity, mutation, and genuine
cross-session accumulation.

---

## Where It Came From

Mueller's book describes a complete cognitive architecture for
mind-wandering and daydreaming: emotion-driven control, concern
initiation, planning, retrieval, reminding, rationalization,
reversal, serendipity, mutation, and episode evaluation.

A key correction during this project came from reading Mueller's
original Lisp source code alongside the book. Mueller's core
contribution is not just character behavior. It is the substrate:

- episodes as decomposable planning trees, not loose narratives
- explicit retrieval indices with per-index eligibility flags
- two separate thresholds (planning vs. reminding)
- hidden episodes that are stored but never retrievable
- a rule accessibility frontier where serendipity specifically
  searches rules the planner doesn't know about yet
- recursive reminding cascades with serendipity suppressed on
  newly stored episodes
- realism and desirability scores that prune and order retrieved
  candidates

The project condensed the full book into 19 typed mechanism cards.
That work, combined with the source code audit, produced the
current hybrid architecture:

- Clojure owns the persistent data structures and recursive loops
- LLMs supply bounded judgment and content generation inside those
  loops
- rules stay graphable, indexable, and inspectable even when an
  executor calls an LLM

Mueller's mechanisms are not being treated as an expert system to
reproduce literally. They are prosthetics that give language models
capabilities they otherwise do not have. Several of his admission
disciplines — per-index cue roles, hidden episodes, the
accessibility frontier — were collapsed during early development
and have since been reconstructed from the original source.

---

## What Exists Now

- A Clojure kernel running Mueller's control loop: episodic memory
  with coincidence-mark retrieval, immutable context tree with
  sprouting, goal competition and decay, five daydreaming families
- A rule engine with `RuleV1` schema, custom unifier/matcher,
  connection graph construction, cross-family bridges, and
  graphable activation / plan dispatch for three families
- A typed executor boundary: family plans return declarative
  effects, the kernel validates and applies them
- A memory membrane: admission tiers (trace / provisional /
  durable), typed cue zones (content / provenance / support),
  anti-residue flags, episode-use attribution with outcome
  resolution, evidence-driven promotion, and a rule accessibility
  frontier. Rule-access gates cross-family bridge dispatch; gating
  of activation and planning dispatch is partially wired. The
  evaluator can no longer grant durable admission at store time
  but retains some negative structural authority that is being
  narrowed
- Provenance-weighted retrieval where structural history shapes
  what gets remembered, with same-family bonus capped and
  double-counting eliminated
- A runtime thought projector that turns kernel state into
  inner-life prose
- A feedback loop where thought residue writes back into memory
  and changes later retrieval (tested: cycles 4-12 diverge from
  baseline)
- A general session conductor (not benchmark-specific)
- An authoring-time generation pipeline that produces keeper-
  quality material supply
- Stage infrastructure from earlier rounds: live visuals, control
  surface conduction, and audio response
- 19 mechanism cards condensed from Mueller's book, with five
  rounds of external architecture review absorbed into the build

A 12-cycle benchmark regression passes cleanly with the membrane
active. A 50-cycle soak completes without membrane failures, but
the benchmark fixture drifts into rehearsal-dominant behavior after
cycle 13, so promotion and vindication dynamics are under-exercised.
That is a fixture pressure issue, not a membrane problem — the
membrane is safe but not yet deeply tested under diverse cognitive
activity.

223 tests. 1107 assertions. All passing.

---

## What's Proven

- The kernel can drive language-model output that reads as inner
  life rather than a system report.
- That generated thought can feed back into memory and change later
  cognition. Cycles diverge from the no-feedback baseline.
- Different models produce different downstream retrieval and node
  selection. Model quality changes what the character thinks about
  next.
- The rule engine mediates real structural transitions in
  activation and planning.
- The memory membrane prevents store-time over-promotion, gates
  durable admission on repeated cross-family evidence, and
  suppresses self-reinforcing family loops.
- Rule accessibility gates cross-family bridge dispatch. Gating
  of activation and planning paths is partially wired.
- The generation pipeline can produce material that survives
  traversal rather than collapsing at runtime.

---

## What's Not Proven Yet

- The creative engine is not built. The connection graph exists,
  but serendipity traversal, analogical repair, and mutation do
  not.
- Cross-session accumulation has not passed the hard falsification
  test: a rule discovered in one session changing reachable
  behavior in a later session without replaying the trace.
- The first real `:llm-backed` rule (a post-production episode
  evaluator) is designed but not yet implemented in the kernel.
- The system has not been shown as a full staged run with
  narration, audio, and visuals working together.
- Multi-situation authored worlds and full graph assembly are still
  ahead.

This is a foundation, not a finished instrument.

---

## Why Rules As Data Matters

The early kernel got the behavior right, but too much of that
behavior lived inside procedural family functions. That makes the
system closed. It can act, but it cannot inspect, connect, or grow
its own behavior structurally.

Moving behaviors into rules-as-data changes three things:

1. The connection graph can see possible links between mechanisms
   whether or not they have fired together yet.
2. Episodes can carry structural provenance about which rule paths
   produced them, and retrieval can use that history.
3. The architecture becomes open to ideas from other cognitive
   systems — ACT-R's buffer discipline, SOAR's chunking,
   Copycat's fluid concepts — because they can be expressed in
   the same substrate.

This migration is what turns the kernel from a convincing
simulation into a substrate that could actually compound.

---

## What It Enables

### A performance instrument with an inner life

A human performer steers pressure, mood, and mode while the
character's cognitive machinery keeps working. The stage becomes a
legible display of thought: narration, music, and later video all
reflecting the same persistent internal state.

### Prosthetic inner life for language models

Between interactions, the model can rehearse, rationalize, avoid,
roam, and remember. You come back and the character is different
because it has actually been through something computationally.

### Accumulation across sessions

If the architecture works, the system gets more capable by
operating. Episodes, rules, and graph structure become resources
for future cognition rather than logs to archive. The memory
membrane ensures that accumulation is earned, not self-reinforcing.

### Other applications

The same substrate could power:

- a writing companion that ruminates between sessions
- a research daemon that accumulates hypotheses and surprising
  links
- a reading companion that develops suspicions and remembers what
  puzzled it
- persistent characters or NPCs with real between-scene cognition

---

## The Honest Claim

Latent Dreamer is an attempt to build a symbolic cognitive
prosthesis for language models: a system with durable pressure,
structured memory, typed mechanisms, and inspectable behavior.

It is already enough to produce convincing inner-life dynamics in
miniature and to prevent the memory ecology from degrading under
accumulation. It is not yet enough to claim the full creative
engine.

The bet is explicit: persistent typed cognitive structure, owned by
an engine and augmented by bounded LLM judgment, will accumulate in
ways no prompt-only system can match.

If later sessions do not become structurally different because of
what earlier sessions discovered, the architecture does not earn its
keep.

---

GitHub: [github.com/davidrd123/latent-dreamer](https://github.com/davidrd123/latent-dreamer)
