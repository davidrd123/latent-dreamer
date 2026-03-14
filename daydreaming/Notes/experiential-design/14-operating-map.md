# Operating Map: The Terrain Going Forward

Written as a stable reference note, not a brainstorm.

This is the map of the project as it actually exists now, after the Clojure
kernel, three benchmarks, the cognitive trace visualizer, and the Mueller
coverage assessment.

The question this note answers is:

> What are the actual tracks now, what is on the critical path, what can run in
> parallel, and what does "integrate the kernel with the Director" actually
> mean?

---

## 1. The System We Actually Have

There are now **five distinct layers**:

1. **Prep layer**
   - Creative brief
   - World fixture
   - Dream graph
   - Style extensions

2. **Dreamer layer**
   - **Python scheduler**: the current conducted-system engine
   - **Clojure kernel**: the Mueller-informed sidecar/research engine

3. **Director layer**
   - LLM interpretation of emitted dream state
   - Returns small structured feedback

4. **Stage layer**
   - Scope/Lyria/APC Mini runtime
   - Consumes `DreamNode`-shaped output and feedback consequences
   - **Inner process visibility** — the cognitive state is part of
     the performance, not just the visual output (see doc 01:
     performance/daydreaming mode toggle). Four implementation
     layers, simplest to richest:
     1. Companion dashboard (trace timeline synced to output)
     2. Text overlay (family/tension/situation — diagnostic)
     3. Inner monologue (whispered TTS narrating cognitive state)
     4. Parametric music (music IS the inner process — felt, not read)
   - Music is the primary audience channel. Dashboard is the
     performer's score.

5. **Evaluation layer**
   - Session logs
   - Benchmark traces
   - Comparison reports
   - Cognitive trace visualizer

The most important thing to keep in mind:

- The project is no longer "one engine."
- It is a **stack with replaceable parts**.
- The main seam is the Dreamer -> Director boundary.

---

## 2. The Stable Seams

These are the contracts that should stay stable while internal machinery
changes.

### A. DreamNode / scene emission

Defined in `daydream-to-stage-contract.md`.

This is what the stage ultimately needs:

- a node id
- mind block
- world block
- stage block
- audio block
- narration block

This contract should not depend on whether the Dreamer is Python or Clojure.

### B. Director-facing Dreamer packet

Defined in `kernel/src/daydreamer/trace.clj` as `dreamer-state-packet`,
and expanded in `12-director-prompt-spec.md`.

This is the Dreamer's reduced conceptual output:

- `mode`
- `goal_type`
- `active_indices`
- `retrieved_episodes`
- `active_plan_chain`
- `episode_cause`
- `trace_context_id`

This is the cleanest seam for swapping Dreamers.

### C. Director feedback schema

Defined in `12-director-prompt-spec.md`.

The Director returns:

- `director_concepts`
- `situation_boosts`
- `valence_delta`
- `surprise`
- optional `emotional_episodes`
- `interpretation_note`

This schema should also stay stable while the Dreamer changes.

### D. Trace schema

Defined in `kernel/src/daydreamer/trace.clj`.

This is the evaluation surface:

- `branch_events`
- `emotion_shifts`
- `emotional_state`
- activations
- selections
- retrievals
- situations

This is how we inspect cognition without needing the stage every time.

---

## 3. The Two Main Dreamer Tracks

There are now two real Dreamer tracks, and they are not the same project.

### Track A: Conducted mainline

Goal:

- produce compelling dream trajectories for the actual conducted system

Current options:

- keep the Python scheduler as the main Dreamer
- or swap the Clojure kernel into the Director seam if it proves better

What matters here:

- does it improve the Director's material?
- does it improve the rendered dream?
- does it make better art?

This is the **shipping / performance** track.

### Track B: Mueller fidelity

Goal:

- recover the missing generativity of DAYDREAMER

Current state:

- contexts/goals/ROVING are fairly strong
- REVERSAL is partly faithful plus one honest bridge
- RATIONALIZATION is still a bridge
- the rule engine is still missing

What matters here:

- theme rules
- `PRESERVATION`
- `LEADTO`
- serendipity via rule interaction
- fuller control/emotion fidelity

This is the **research / cognition** track.

These tracks overlap, but they are not identical.

---

## 4. What "Integrate the Kernel with the Director" Means

This does **not** mean:

- replace Scope
- replace the stage runtime
- rebuild the whole system around Clojure
- make the Director understand Mueller internals

It means something much smaller:

1. Run the Clojure kernel for a cycle.
2. Project its state into the Director-facing packet.
3. Feed that packet into the existing Director prompt.
4. Receive normal Director feedback.
5. Apply that feedback to the next kernel cycle.

So the first integration pass is:

**kernel -> Director -> feedback -> next kernel cycle**

not:

**kernel -> Director -> stage rewrite -> full product migration**

That is why this can happen without destabilizing the rest of the system.

---

## 5. What Can Safely Run In Parallel

The answer is: quite a lot, if the seams above are respected.

### Safe to do in parallel

- live Director integration using the current kernel
- Mueller-fidelity rule-engine experiments
- more trace/visualizer evaluation
- benchmark-specific adapters
- stage/runtime experimentation

### Safe if gated behind separate runners/config

- alternate control loops
- alternate activation policies
- experimental rule engines
- more faithful emotion decay/GC
- GATE refinements

### Not safe to churn casually

- DreamNode contract
- Director input schema
- Director feedback schema
- session/trace top-level export shape

If those four churn constantly, everything else becomes hard to evaluate.

---

## 6. The Actual Critical Path

If the question is "what gets us to a convincing conducted dream fastest?",
the critical path is:

1. **Dreamer -> Director integration**
2. **Watch the result**
3. **Judge whether the Director materially improves the dream**
4. **Only then decide whether Python or Clojure is the better mainline Dreamer**

In other words:

- the critical path is **evaluation through the live loop**
- not more mechanism-building in isolation

This is where the visualizer now helps:

- traces tell us whether cognition changed
- the live loop tells us whether that change matters artistically

---

## 7. The Biggest Open Question

The biggest open question is no longer:

- "Can the kernel produce richer traces?"

That has basically been answered yes.

The biggest open question is:

- **Does the richer cognitive path produce better Director output and better
  dream experience?**

That is the decision point between:

- "the bridges are sufficient for conducted daydreaming"

and

- "the missing Mueller machinery is necessary even for the art."

---

## 8. What The Coverage Assessment Changes

`kernel/doc/mueller-coverage-assessment.md` makes one thing very clear:

- the kernel is already enough to test the artistic architecture
- the main missing 60 percent is mostly **generativity machinery**

That means:

- if the priority is the conducted system, the rule engine is not automatically
  critical-path work
- if the priority is Mueller fidelity, the rule engine is the central task

So this is the clean split:

### If the priority is the art

- integrate kernel with Director
- evaluate real runs
- keep bridges if they are sufficient

### If the priority is fidelity

- build a minimal rule engine
- replace stored bridges with derived mechanisms
- push toward real generativity

Both are valid. They just answer different questions.

---

## 9. Recommended Priority Order

### Mainline priority

1. Do one live kernel -> Director replay on a real fixture.
2. Compare that against the Python Dreamer path.
3. Decide which Dreamer is currently the better source of Director material.
4. Keep the stronger one on the conducted-system path.

### Parallel priority

1. Scope a **tiny** rule-engine slice.
2. Use it first for `reverse-undo-causes`.
3. If it works, extend it to rationalization frame derivation.

### Lower priority for now

- more new families
- more benchmarks
- generic adapter abstraction
- polishing bridges that are already sufficient for evaluation

---

## 10. The Practical Rule Going Forward

When deciding what to do next, ask:

1. Does this improve the live Dreamer -> Director -> feedback loop?
2. If not, does it reduce one of the explicit Mueller fidelity gaps?
3. If not, it is probably not a priority right now.

That is the terrain.

The simplest working mental model is:

- **Mainline track:** make the dream better
- **Parallel track:** make the cognition truer
- **Shared discipline:** do not break the seams that let those run side by side

