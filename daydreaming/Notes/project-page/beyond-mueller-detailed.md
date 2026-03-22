# Beyond Mueller — Detailed Reference

Date: 2026-03-22

The numbered specifics of what we're already doing that Mueller didn't,
and where we can keep going. This is the working reference version.
For the position-memo version, see `trajectory.md`.

---

## What we're already doing that Mueller didn't

### 1. LLM judgment at typed interfaces

Mueller hard-coded everything — rule consequents, realism scores,
analogy aptness, rationalization content. We replace specific
hard-coded decisions with LLM calls that return typed results
validated against schemas. The rule stays in the graph. The content
becomes contextual. Mueller couldn't do this because LLMs didn't
exist.

### 2. Cross-session accumulation

Mueller's system ran once. Ours persists. Episodes from Tuesday's
session change Thursday's retrieval. Rules created through reversal
in one session enter the connection graph for future sessions. This
is the "compounding creative capacity" claim — the system gets
richer through its own operation.

### 3. The denotational contract layer

Mueller's rules either instantiated patterns or ran Lisp procedures.
There was no middle layer that said "this rule is SUPPOSED to reduce
negative emotion on the failed goal." Our schema/denotation/executor
split lets us validate that an LLM-backed rule did what it was
supposed to, not just that it returned the right shape.

### 4. The writeback loop

Mueller narrated inner life but didn't feed narration back into
memory. Our RuntimeThoughtBeatV1 generates inner-life content AND
writes residue back as episodes. The writeback changes future
retrieval. Proven: traces diverge from baseline.

### 5. Memory ecology / consolidation

Mueller didn't need this because his system ran once. We need
admission tiers, anti-residue flags, promotion criteria, and
consolidation sweeps because our episodes accumulate and
self-reference across sessions.

---

## Where we can keep going — beyond Mueller

### 1. Serendipity over a heterogeneous graph

Mueller's connection graph had ~135 hand-authored
interpersonal-domain rules. Ours will have activation rules,
planning rules, evaluation rules, retrieval-event rules, critic
rules. Different semantic phases. Serendipity paths that span from
"this emotional trigger" through "that planning strategy" to "this
evaluation criterion" — cross-phase discovery that Mueller's
domain-specific graph couldn't do because it didn't have those
phases as typed facts.

### 2. LLM-as-evaluator everywhere the condensation identifies a judgment point

10 of 19 mechanisms have evaluator-pattern judgment points. Once the
executor boundary is real and the first evaluator pilot works, you
can systematically add evaluators: analogy aptness (mechanism 09),
path usefulness (mechanism 13), mutation triage (mechanism 14),
realism assessment (mechanism 15/18). Each one makes the system's
judgment more contextual without changing the structural loops.

### 3. Rules that the system creates, not just rules you author

Mueller's REVERSAL already creates new rules — counterfactual repair
strategies that enter the rule base. In our system, those rules enter
the connection graph and change what serendipity can find. But the
LLM could ALSO propose new rules — a rationalization that works could
be generalized into a reusable reframe strategy, stored as a RuleV1
with schemas, and available for future analogical planning. The
NL2GenSym work (from the 5 Pro review) shows this is viable if
execution-grounded validation is in the loop.

### 4. Directed daydreaming as a creative methodology

Mueller studied undirected daydreaming — the stream of consciousness.
We give the system a creative brief. The brief constrains the concern
space (which situations, which characters, which stakes). Serendipity
operates within those constraints but can still find non-obvious
connections. The 5 Pro prompt-set reply called this "constrain the
question, not the surprise" — one-end anchored search where the
concern fixes the top and reminding/mutation leave the bottom open.

Two versions:
- **Thin (implementable now):** Brief = situation configuration +
  initial episode set. Operators fire endogenously from emotion.
  The kernel's existing situation model already supports this.
  Graffito demo needs this version.
- **Thick (needs beyond-Mueller stack):** Brief also shapes
  serendipity anchoring, mutation constraints, operator priors,
  evaluation criteria. Needs the heterogeneous graph and
  verified paths to be meaningful.

### 5. DSPy-style optimization of the write/read interface

The GradMem insight: HOW you write memory matters as much as how many
times you write it. Right now the residue prompt is hand-authored.
DSPy could optimize it against a metric: "does this residue change
future retrieval in useful ways?" The write interface
(`residue_summary` + `residue_indices`) is already a DSPy signature.
The measurement (trace divergence) is already available. This is a
tuning pass over the architecture, not a new architecture.

### 6. The developmental trajectory

If the system runs for months with the accumulation + consolidation
discipline working, what happens? The connection graph gets richer
(new rules from reversal, new evaluation fact types). The episode
store develops characteristic patterns (which rationalizations stick,
which get flagged as stale). The emotion-driven routing develops
preferences (some concerns keep winning, others decay). Two instances
with different experiences should diverge into recognizably different
cognitive profiles. Not mystical "personality" — topological and
evaluative divergence. Measurable by: distribution over daydreaming
goal families, recurring motif neighborhoods, edge kinds used in
successful serendipities, repair families used most often.

### 7. The accessibility frontier as a creative growth mechanism

(Note: 5 Pro confirmed this may be a genuine contribution to the
cognitive architecture space. Neither Soar chunking nor ACT-R
production compilation has explicit staged admission with evidence
requirements. Our `:frontier / :accessible / :quarantined` registry
is architecturally novel, not a literature rename.)

Mueller had rules starting inaccessible and serendipity unlocking
them. In a cross-session system with LLM-generated rules, this
becomes a genuine growth mechanism: the system generates candidate
rules (through reversal, through LLM-proposed generalizations) →
they start on the frontier → serendipity discovers paths through
them → verification proves they work → they become accessible to
the planner → the system can now plan things it couldn't plan before.
That's cognitive development, not just memory growth.

### 8. The multi-model routing as cognitive economics

The hybrid routing (Haiku for most cycles, Sonnet for reversal) is
already a crude form of cognitive resource allocation — spend more
compute on the harder thinking. With the evaluation ladder, you
could make this adaptive: escalate to a more capable model when the
path verification is ambiguous, or when the anti-residue flags
suggest a difficult situation. The kernel controls the routing, not
the model. That's a form of metacognition — the system allocates its
own processing resources based on structural difficulty.

---

## The trajectory in one picture

```
RECONSTRUCT (Mueller baseline)
  admission gates, accessibility frontier, realism/desirability,
  proper cue discipline
        ↓
EXTEND (Mueller + LLM + persistence)  ← we are HERE
  typed LLM judgment, cross-session accumulation,
  denotational contracts, writeback loop,
  memory ecology / consolidation
        ↓
GROW (beyond Mueller)
  heterogeneous graph serendipity, systematic evaluators,
  LLM-proposed rules, directed daydreaming,
  DSPy optimization of write/read, developmental trajectory,
  accessibility frontier as growth mechanism
        ↓
PERFORM (the art)
  conducted daydreaming with a human performer,
  the system has its own unfinished business,
  creative collaboration where the partner develops
  its own associative style across sessions
```
