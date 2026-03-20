# Plan of Attack

Date: 2026-03-19

From the "Prosthetic Inner Life" reframe. The LLM inhabits the
role. The kernel gives it a mind. The world evolves around it.

---

## The convergence

Three threads that looked separate are actually one system:

1. **RuntimeThoughtBeatV1** — the kernel runs the cognitive
   loop, an LLM renders each cycle as inner-life prose
2. **The provocation seam** — typed world events that change
   the situation the character is in
3. **The generation pipeline** — produces character moments
   conditioned on cognitive state

They converge at: **the kernel runs continuously, the LLM
inhabits the character at each cycle, and the world evolves
through provocations that the character responds to.**

That's not three separate projects. It's one loop:

```
Kernel fires operator + retrieves memory
    ↓
LLM generates inner-life beat (RuntimeThoughtBeatV1)
    ↓
Beat feeds: narration, audio, visualization
    ↓
Tiny residue feeds back into kernel memory
    ↓
Provocation arrives (world event or Director perturbation)
    ↓
Kernel situation updates (carry + remap)
    ↓
Next cycle — different operator may win because
the world changed and the character remembers
```

## The build sequence

### Phase 1: The mind speaks (next)

**Target:** Puppet Knows cognitive trace with inner-life prose.

- Run `bb puppet-knows-autonomous` to produce a 12-cycle
  cognitive trace
- For each cycle, call Sonnet with the kernel's state:
  - active concern
  - selected operator
  - retrieved material
  - context branch
  - prior beat residue
- Produce `RuntimeThoughtBeatV1`:
  - thought_beat_text (2-3 sentences)
  - mood_tags (for Lyria)
  - residue_candidates (tiny, for memory)
  - external_action (if performance mode)
  - daydream_content (if daydreaming mode)
- Render through the cognitive HTML + narration bridge
- Wire to Lyria RT

**Success:** The output reads as a person thinking, not as a
system report.

**What exists:** kernel runs, Lyria connected, narration bridge
exists, cognitive HTML renderer exists. Missing: the LLM call
per cycle and the RuntimeThoughtBeatV1 object.

### Phase 2: The world pushes back

**Target:** Provocations change what the character does.

- The provocation seam is already partially built
  (FixtureDeltaV1 / overlay interpreter)
- Need: propagation into concern inference and operator scoring
  (codex1 is patching this)
- Need: ProvocationContextV1 so the Provocation Generator
  sees enough internal state to push intelligently
- Need: carry + remap when situations change

**Success:** A provocation ("Eli sends a second text") changes
which operator wins and produces a structurally different
inner-life beat.

**What exists:** overlay interpreter, three-way state split
design. Missing: propagation to decision surfaces, carry+remap
implementation.

### Phase 3: The loop runs continuously

**Target:** Indefinite traversal with accumulating memory and
world evolution.

- Kernel cycles continuously (daydream_engine.py already does
  this with --interval-s)
- RuntimeThoughtBeat at each cycle
- Provocations arrive at intervals (hand-authored initially,
  Provocation Generator later)
- Memory accumulates across cycles (with writeback discipline
  from Q8 — tiny residue only, not full prose)
- Conductor bias from APC Mini
- Audio + narration + visualization update in real time

**Success:** You watch for 5 minutes and the character's
thinking evolves — different concerns surface, different
operators fire, memories from earlier cycles influence later
ones, world events provoke genuine responses.

### Phase 4: The conversation

**Target:** The "prosthetic inner life" demo.

You talk to the character. It responds from inside accumulated
cognitive state. You leave. Between sessions, it daydreams —
rehearses, rationalizes, avoids, roves. You come back, and
it's different. The dashboard shows what happened while you
were gone.

This is the persistent daemon from doc 34, made concrete.
The kernel runs between sessions. The LLM inhabits the
character when you're there. The inner life is continuous.

### Phase 5: Graffito

**Target:** Mark Friedberg's creative brief, with generated
material, traversed live, rendered through Scope.

- Graffito creative brief authored (creative_brief.yaml +
  style_extensions.yaml)
- Full Graffito graph assembled from scaffold + generated
  patches + provocation-driven material
- Scope rendering when GPUs available
- APC Mini conducting the performance
- Mark watches and reacts

---

## Phase 1 status update (2026-03-20)

**RuntimeThoughtBeatV1 is working.** Codex ran the first
runtime comparison on Puppet Knows:

- Haiku: ~3.3s/cycle — usable as default runtime projector
- Sonnet: ~7.6s/cycle — better operator realization, use for
  complex cycles (operator changes, context sprouts, reversals)
- Quality: Haiku stays coherent. Sonnet performs the cognitive
  work — "what if the fingerprint was always meant to be seen"
  is a reversal the kernel triggered and the LLM inhabited.

**The narration is ahead of the machinery.** The LLM generates
richer inner life than the kernel's events justify. "The coin
with no face on either side" is prose the kernel didn't compute.
This is the right tension — the LLM adds texture, but the
narration is partly disconnected from the actual cognitive
computation. The feedback loop is the fix.

---

## The deeper architecture (from Ch. 7 analysis)

### The LLM/symbolic split is now precise

| LLMs provide | Symbolic system provides |
|---|---|
| Domain knowledge (replaces hand-coded rules) | Rule connection graph (searchable) |
| Contextual judgment | Context sprouting + backtracking |
| Content generation | Coincidence-mark retrieval |
| Evaluation | Reminding cascade (recursive) |
| Flexible rule consequents | Serendipity intersection search |
| | Persistent accumulating state |

### Hybrid rules: the novel contribution

Clojure rules that are data (searchable in the graph, indexable
in episodes, traversable for serendipity) but whose consequents
can call an LLM for contextual judgment when they fire.

The graph structure is preserved. The flexibility is added. The
judgments accumulate as episodes. "Not 'LLM with tools' and not
'expert system with better rules.' Rules that are data, that
are searchable, that accumulate in episodes, AND that can invoke
contextual judgment when they execute."

Mueller hand-coded 135 rules. The LLM can propose new rules as
typed Clojure structures. The system validates, adds to the rule
graph, recomputes connections. Now the rule is available for
planning, retrieval, serendipity — forever. The LLM generated
it once. The system uses it across sessions.

### Accretive, not disposable

This is NOT the RLM "programs as intermediate representation"
pattern (where generated code is disposable, solving one query).
This is **persistent evolving structures that grow through the
system's own operation**:

- Episodes accumulate (real and imagined)
- Rules grow (LLM-proposed, validated, added to graph)
- The rule connection graph gets richer
- Contextual judgments from past rule firings are stored
  in episodes and shape future analogical retrieval
- Clojure's immutable persistent data structures make this
  safe (versioned, branchable, no corruption)

The LLM is the wellspring. The Clojure structures are the
riverbed that accumulates.

---

## The key experiment: does accumulation matter?

The honest risk (D9, still unanswered): "This is a beautiful
architecture for a system that might not need to exist." The
value is in what's *maintained* across sessions versus what's
*possible* in one session.

**Cheapest test:** Feed the LLM narration back as a new episode
into the kernel. Run another cycle. See if retrieval changes.
One cycle of accumulation. If the trace is different with
feedback than without, the accretive machinery earns its keep.

This is more targeted than "wire to more things." It directly
tests whether the kernel contributes something beyond "fancy
prompt generator."

---

## What doesn't change

- The graph remains the membrane
- The kernel runs Mueller's loop
- L3 is the traversal scheduler
- The Director is runtime perturbation, not world authorship
- The Provocation Generator is authoring-time, typed, curated
- Fixtures are task views, not canon
- Memory writeback is disciplined (tiny residue, not full prose)

## What shifts

- **The primary product is inner-life prose, not graph nodes.**
  The generation pipeline proved material supply. Now the goal
  is the runtime experience — hearing the character think.
- **The provocation seam is part of the same loop, not a
  separate project.** World events and inner-life generation
  are two halves of one cycle.
- **The first demo is a conversation, not a video.** The
  persistent daemon is closer than the conducted performance
  because it doesn't need video, conductor, or Scope.
- **"Prosthetic Inner Life for Language Models" is the hook.**
  The LLM has no mind of its own. We give it one.
- **Hybrid rules are the deep technical contribution.**
  Searchable graph structure + LLM contextual judgment in
  the same rule. Accumulation of situated judgments as episodes.
  Clojure code-is-data makes this natural, not a hack.

## What's immediately actionable

1. ~~Write `runtime_thought_beat_replay.py`~~ — DONE. Codex
   built it. Haiku/Sonnet comparison complete.
2. ~~Wire to Puppet Knows autonomous trace~~ — DONE.
3. Wire the output to the existing cognitive HTML renderer
   (codex working on this)
4. Wire to Lyria RT
5. **Run the feedback loop test:** narration → new episode →
   different retrieval? This is the accumulation experiment.
6. Watch/listen and judge: does this feel like a mind?

The Level 3 test is becoming real. The feedback loop test
(step 5) is the D9 experiment in miniature.
