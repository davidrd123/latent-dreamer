# Generative Agents - Extraction

Source: Park et al., "Generative Agents: Interactive Simulacra of
Human Behavior" (UIST 2023)

Source file: `sources/markdown/GenerativeAgents/source.md`

Purpose: extract what Generative Agents contributes relative to the
pressure-engine / daydreaming architecture, especially around memory
stream, retrieval, reflection, and planning; where it is genuinely
strong as an LLM-era baseline; where it is structurally shallower than
the current stack; and what to steal vs. avoid.

---

## What the paper actually describes

This is not a graph-first drama manager and not a deep cognitive
architecture. It is a clean early **LLM-plus-scaffolding sandbox
agent** design:

- agents live in a Sims-like toy town
- each agent starts from one paragraph of seed identity / relationships
- all reasoning is represented in natural language
- behavior is produced by combining memory retrieval, reflection, and
  hierarchical planning around an LLM

The core loop has four parts:

### 1. Memory stream

The agent stores an append-only stream of natural-language records:

- observations
- plans
- reflections

Each memory carries a creation time, last-access time, and an
importance score. The important move here is simple but real: the paper
treats long-horizon behavior as a memory-management problem, not as a
single big prompt.

### 2. Retrieval

At action time, the system does not dump the whole history into the
prompt. It scores memories by a weighted mix of:

- recency
- importance
- relevance to the current query

Relevance is embedding similarity. Importance is itself assigned by the
LLM on a poignancy scale. The top memories that fit the context window
condition the next action or answer.

### 3. Reflection

The paper's key upgrade over pure episodic memory is that agents do not
just store events; they periodically synthesize them into higher-level
claims. Reflection is triggered when recent memories cross an aggregate
importance threshold. The system:

1. looks at recent memory
2. asks what salient high-level questions can be asked
3. retrieves evidence relevant to those questions
4. generates abstract insights with citations back to source memories
5. stores those insights as new memories

So the memory stream becomes a mix of raw episodes and summarized
conclusions such as "Klaus is dedicated to his research."

### 4. Planning

The planning loop is hierarchical:

- generate a broad day plan
- decompose into hour-scale actions
- decompose again into 5-15 minute actions
- re-plan when observations warrant a reaction

Plans are themselves stored in memory, so future prompts can condition
on prior commitments. Dialogue is generated the same way: retrieve the
relationship context and current situation, then prompt for the next
utterance.

---

## Why it mattered as an LLM-era baseline

Generative Agents is still one of the cleanest statements of the
minimal architecture needed to get beyond stateless roleplay prompting.

### 1. It correctly identifies the real problem

The paper understands that believable long-run behavior is not mainly a
"write a better character prompt" problem. It is a problem of:

- retaining history
- surfacing the right history at the right time
- compressing history into reusable abstractions
- carrying commitments forward across time

That remains correct.

### 2. It gives a very legible four-part baseline

Memory, retrieval, reflection, and planning is a strong baseline split.
It is easy to explain, easy to instrument, and easy to ablate. That is
part of why the paper landed so hard: it made the architecture visible.

### 3. It proves that persistence plus planning changes behavior

The Valentine's Day party example is structurally modest, but it does
show something real. If agents can:

- remember invitations
- infer social implications
- make plans
- revise those plans when they meet others

then you get information diffusion and lightweight coordination without
hand-scripting the whole chain.

### 4. It actually runs ablations

This matters. The paper removes memory, planning, and reflection and
shows that behavior degrades. That makes it more useful than a lot of
"agent" work that gestures at architecture without showing which parts
carry the result.

### 5. It exposes the right failure surface

The paper's own failure modes are highly informative:

- retrieval misses
- partial retrieval that supports the wrong answer
- hallucinated embellishment on top of real memory
- instruction-tuned politeness and over-cooperation
- increasing drift as the memory/world description grows

That is a useful baseline failure profile for any LLM-native social
agent design.

---

## Where it aligns with the current architecture

Generative Agents does not match the pressure-engine directly, but it
does validate a few broad instincts that the current stack already has.

### 1. Memory should be first-class

The repo's architecture already treats episodic retrieval and reminding
as shared infrastructure. Generative Agents reinforces that this is not
optional garnish. If you want coherence across time, memory has to be a
first-class subsystem.

### 2. Retrieval is a control problem, not just storage

The paper's recency / importance / relevance triad is crude compared to
where this repo is heading, but it is the right category of move.
Memory quality depends on retrieval policy, not just on how much text
you save.

### 3. Raw episodes are not enough

The reflection layer makes the same high-level claim the repo now makes
in a stronger form: you need a middle layer between raw events and the
next action. The paper reaches for that with reflection memories. The
current architecture reaches for it with things like:

- `CharacterConcern`
- `CausalSliceV1`
- `AppraisalFrame`
- `PracticeContextV1`

The paper is weaker, but it is reacting to the same real problem.

### 4. Future commitments improve coherence

The planning layer is also directionally right. Agents need explicit
future commitments if they are not going to re-decide their lives from
scratch every five minutes.

---

## Where it is structurally weaker than the current architecture

This is where the paper stops being a blueprint and becomes a baseline.

### 1. It collapses too many roles into one natural-language loop

In the settled architecture, the split is explicit:

- `L1` authoring-time orchestration / critique
- `L2` character-level motivated exploration
- `L3` traversal scheduling over authored material

Generative Agents has no such membrane. It folds memory, inference,
planning, reaction, and world-state interpretation into one prompt
pipeline. That is fine for a demo sandbox. It is too collapsed for the
current architecture.

### 2. It has no graph membrane

The pressure-engine is graph-first in the shipping lane. The graph is
the shared cross-lane membrane, with stable residue and authored
structure separated from runtime state.

Generative Agents does the opposite:

- the natural-language memory stream is the effective source of truth
- plans and reflections live in the same pool as observations
- runtime summaries and higher-order inferences are not cleanly
  separated from authored facts

That is exactly what `21-graph-interface-contract.md` is trying to
avoid.

### 3. Its "reflection" is much shallower than the repo's middle layer

Reflection in this paper is basically prompted summarization plus light
inference over retrieved memories. It does not give you:

- explicit appraisal variables
- causal threat / advance structure
- controllability or likelihood buckets
- practice phase or role
- commit semantics
- operator families like rehearsal, rationalization, or avoidance

So while the paper correctly senses that raw episodes are insufficient,
its answer is still quite shallow relative to the current
`concern -> causal slice -> appraisal -> practice -> commit` stack.

### 4. Its planning is itinerary planning, not pressure management

The plans are mainly calendars and task decompositions. That is useful,
but it is not the same as:

- concern-driven hypothetical exploration
- event approach / commit legibility
- traversal diagnostics and intents
- authored setup/payoff management across a graph

The paper solves "what will I do this afternoon?" better than "what
dramatic pressure is ripening, being dodged, or converting into a real
choice?"

### 5. Retrieval is serviceable but structurally naive

The retrieval scoring is a good baseline, but it is still flat. It does
not understand:

- concern lineage
- practice type
- situation structure
- setup / payoff references
- branch outcomes
- event commitment
- reminding cascades or coincidence marks

It is a decent attentional heuristic. It is not yet architecture-shaped
retrieval.

### 6. Social life is broad but shallow

The Smallville demo gets diffusion, acquaintance formation, and
coordination. What it does not really get is hard dramatic structure:

- conflict that bites
- anti-coordination
- durable ambivalence
- blocked commitments
- social-practice escalation
- watchable inner pressure

The paper itself notes that the agents skew overly formal and overly
cooperative. That matters. The repo's current architecture is trying to
get at motivated pressure and practiced social structure, not just
plausible niceness.

### 7. It is expensive, fragile, and prompt-sensitive

The paper reports large runtime cost and latency. More importantly, the
whole stack is vulnerable to the usual freeform-LLM problems:

- hallucinated elaboration
- memory hacking
- summary drift
- norm misclassification
- prompt-style bias leaking into agent behavior

Those are not incidental bugs. They follow directly from making
untyped natural-language memory carry almost all semantic load.

---

## What to steal

### 1. Keep this as the baseline to beat

This is probably the cleanest "LLM + memory + retrieval + reflection +
planning" baseline in the literature. That makes it useful even where
the repo goes beyond it. If a proposed sidecar or runtime module cannot
beat this architecture on coherence or inspectability, it is not yet
interesting.

### 2. Use a human-legible memory trace, but as sidecar

The memory stream is worth stealing in one narrow form:

- append-only
- timestamped
- human-readable
- useful for dashboard narration, debugging, and provenance

But it should be a trace or sidecar, not canonical world state.

### 3. Keep multi-factor retrieval

Recency, salience, and semantic relevance should all matter. The exact
formula in the paper is too weak, but the principle is right: retrieval
should be a scored competition among candidate memories, not an
undisciplined dump.

### 4. Reflection should be scheduled and evidence-bearing

The paper is right that synthesis should happen periodically, not on
every token. It is also right to require evidence pointers. That
suggests a useful discipline for this repo's own summarization and
dashboard layers:

- schedule synthesis
- produce compact higher-level claims
- preserve source refs
- treat them as inspectable artifacts

### 5. Keep hierarchical plan objects where commitments matter

The recursive day-plan decomposition is too shallow for drama, but the
general idea is still useful. When the system forms future
commitments, those commitments should exist as explicit objects that can
be:

- remembered
- revised
- inspected
- compared against later behavior

### 6. Keep the ablation posture

The paper's most valuable research habit may be methodological. It does
not just propose modules; it removes them and shows what breaks. That
fits the repo's current operating discipline very well.

---

## What not to steal

### 1. Do not let natural-language memory become the canonical state

The current architecture is right to keep the graph seam thin and typed.
Observations, reflections, and plans should not all collapse into one
mutable text pile that silently becomes the world model.

### 2. Do not confuse reflection memories with real cognition

The reflection tree is useful, but it is not a substitute for explicit
appraisal, practice context, or commit semantics. Treating it as such
would flatten the middle layer back into prompted summary prose.

### 3. Do not collapse `L2` and `L3`

Generative Agents has one behavioral loop. The repo should keep its
stronger split:

- `L2` for motivated local cognition
- `L3` for traversal over authored material

The paper is a baseline below that split, not an argument against it.

### 4. Do not import its importance score as pressure semantics

LLM-rated poignancy is not the same thing as concern intensity,
appraisal, or event pressure. "Important" is too vague a scalar to do
the job the pressure-engine needs.

### 5. Do not mistake lightweight coordination for dramatic structure

Invitation spreading and party attendance are interesting demos. They
are not yet setup/payoff discipline, multi-situation dramatic
coherence, or watchable inner-life architecture.

### 6. Do not inherit the model's niceness bias

The paper's agents are notably formal, agreeable, and easily steered by
others. That is exactly the wrong baseline if the goal is pressure,
friction, rehearsal, evasion, and consequential social practice.

### 7. Do not accept the cost profile as normal

The paper's architecture is expensive because it keeps too much of the
semantic burden in prompt-time natural language. The current stack
should continue pushing typed state, stable residue, cached summaries,
and narrower prompt surfaces where possible.

---

## Bottom line

Generative Agents is best read as the cleanest early **LLM-native
baseline for persistent memory, retrieval, reflection, and lightweight
planning**. It is genuinely useful because it names the right four
problems and shows that solving them changes behavior.

But relative to the current pressure-engine architecture, it is still
pre-membrane and pre-middle-layer. It has no authored graph seam, no
real `L2` appraisal/practice/commit stack, and no `L3` traversal
discipline. Its strongest contribution is not a new top-level
architecture. Its strongest contribution is a clear baseline and a
warning: if you leave long-term coherence to stateless prompting, you
get nonsense; if you solve it only with an untyped memory stream, you
get something better, but still too shallow for the current goal.

