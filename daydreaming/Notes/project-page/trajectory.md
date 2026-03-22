# Project Trajectory: Where This Goes

Date: 2026-03-22

What we're reconstructing from Mueller, what we're extending, and
where we're headed that he didn't go. This is the outward-facing
version of the build sequence — not which functions to write next,
but what the system becomes and why each phase enables the next.

---

## Phase 1: Reconstruct Mueller's discipline

Mueller's 1990 DAYDREAMER had admission gates, retrieval discipline,
and a rule accessibility frontier that we initially collapsed when
building the Clojure kernel. Recovering these isn't nostalgia — it's
the foundation that makes everything else safe.

What this gives us:
- Episodes don't automatically become permanent memory. They have to
  earn durability through downstream success.
- Not all indices are equal. Content cues drive retrieval; provenance
  and metadata are queryable but don't inflate retrieval scores.
- Rules start inaccessible. Serendipity discovers them. Verification
  proves they work. Then the planner can use them. Creative discovery
  literally expands the system's repertoire.

Without this: the system develops grooves. Prior rationalizations
become easy to retrieve, get reused, become even easier. Good
performance degrades into self-reinforcing habit.

**Status:** First pass done. Admission tiers, cue zones, provenance
caps in code. Promotion logic and rule accessibility frontier next.

---

## Phase 2: Extend with LLM judgment and cross-session persistence

Mueller hard-coded everything — rule consequents, realism scores,
analogy aptness, rationalization content. He couldn't do otherwise
in 1990. We replace specific hard-coded decisions with LLM calls
that return typed results validated against schemas.

The rule stays in the connection graph. The content becomes
contextual. The system checks that the LLM's output matches
what the rule was supposed to accomplish (the denotational
contract), not just that it returned the right shape.

Mueller's system ran once. Ours persists across sessions. Episodes
from Tuesday change Thursday's retrieval. Rules created through
reversal in one session enter the connection graph for all future
sessions. The writeback loop (RuntimeThoughtBeatV1) generates
inner-life content and feeds residue back into memory. Proven:
traces diverge from baseline.

What this gives us:
- Richer content at every judgment point (realism assessment,
  analogy aptness, rationalization reframes, episode evaluation)
- Memory that compounds across sessions
- A system whose creative capacity grows through its own operation

**Status:** Writeback proven (gate 2 passed). Executor boundary
and first LLM evaluator pilot next.

---

## Phase 3: Grow the creative engine

Once the foundation is solid and the LLM boundary is real,
the system can do things Mueller's couldn't:

**Serendipity over a heterogeneous graph.** Mueller had ~135
rules in one domain. Our graph spans activation, planning,
evaluation, retrieval events, and critic signals. Serendipity
paths that cross phases — from "this emotional trigger" through
"that planning strategy" to "this evaluation criterion" — are
genuinely non-obvious. They connect things that weren't designed
to connect.

**Rules the system creates.** Mueller's REVERSAL already creates
new rules — counterfactual repair strategies. In our system,
those rules enter the connection graph and change what serendipity
can find. The LLM can also propose rules: a rationalization that
works could be generalized into a reusable reframe strategy,
stored as a RuleV1, and available for future analogical planning.

**The accessibility frontier as cognitive development.** The system
generates candidate rules → they start on the frontier →
serendipity discovers paths through them → verification proves
they work → the planner can now plan things it couldn't before.
Over months, the planner's repertoire grows through the system's
own creative operation. Two instances with different experiences
diverge into recognizably different cognitive profiles.

**Directed daydreaming.** Mueller studied undirected daydreaming.
We give the system a creative brief. The brief constrains the
concern space. Serendipity operates within those constraints but
finds non-obvious connections. Constrain the question, not the
surprise.

**Optimized write/read interface.** HOW you write memory matters
as much as how many times you write. The residue prompt can be
optimized against a metric: "does this residue change future
retrieval in useful ways?" The measurement exists (trace
divergence). The optimization (DSPy-style discrete search over
write strategies) is a tuning pass over the architecture.

---

## Phase 4: Perform

A conducted daydreaming instrument. A human performer (APC Mini
faders, pads) steers a system that has its own unfinished
business, its own emotional weather, its own serendipitous
discoveries. The performer can bias concern pressure, escalate
or suppress operators, introduce events — but the system's
cognitive machinery responds with its own dynamics.

What makes this different from "AI generates art":
- The system carries persistent inner state between sessions
- It has unfinished business that returns involuntarily
- Its creative capacity compounds — yesterday's reversal
  creates a rule that changes tomorrow's serendipity
- The performer collaborates with a partner that develops
  its own associative style
- The audience watches something that has genuine return
  pressure, not just generated prose

The evaluation criterion (from the 5 Pro research): the system
works when it seeds actual afterthought in the human — when the
audience keeps wondering about the character between sessions.
That means the machine's accumulated inner process has crossed
over into the viewer's own daydreaming.

---

## Why this order

Each phase earns the next:

- **Phase 1** makes accumulation safe (no grooves)
- **Phase 2** makes accumulation rich (LLM content + persistence)
- **Phase 3** makes accumulation compound (graph growth, rule
  creation, developmental divergence)
- **Phase 4** makes accumulation performable (conducted inner life)

You can't skip phases. Without Phase 1, Phase 2 produces grooves.
Without Phase 2, Phase 3 has no LLM content to crystallize.
Without Phase 3, Phase 4 is a static instrument. The sequence
is a ladder, not a menu.

---

## What's genuinely novel

The 5 Pro research identified the novelty line:

Not "LLM + memory" (others have that). Not "cognitive architecture
+ LLM" (SOAR/ACT-R integrations exist). Not "persistent agents"
(Ori-Mnemos, Mem0 exist).

The specific combination: **typed LLM judgment inside persistent
graph-structured cognition, where the graph owns the recursion
and the LLM provides evaluative content at structural decision
points, and the whole thing accumulates across sessions so that
the system's creative capacity grows through its own operation.**

The narrower claim is stronger: **endogenous concern economics
plus typed counterfactual processing, if and only if those
processes produce later behavior that strong assertion/event-memory
baselines cannot match.**

The falsification test: can yesterday's generated structure change
tomorrow's reachable solutions in a way a rolling summary and
vector retrieval cannot? If yes, this is something real. If no,
it's a beautifully organized prompt stack.
