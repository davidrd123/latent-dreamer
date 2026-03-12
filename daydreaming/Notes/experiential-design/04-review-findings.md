# Review Findings: Three External Reads of the Architecture

Three Web Opus instances with different project contexts and system prompts
read the four experiential-design documents. This captures what they
converged on, what was uniquely valuable from each, and the concrete design
recommendations that emerged.

Reviewers:
- **PCE** — Simulators/prediction-orthogonality lens
- **MR_IMS** — Instrument/mapping/sensor architecture lens
- **VC** — Control theory / dynamical systems lens (Proteus v3.0)

## Where All Three Converge

### Director narration should not be an audio channel

All three flag the three-voice narration (Dreamer whisper + Director
monologue + Lyria music) as too much simultaneous information.

- PCE: Director voice works as text (subtitles), not audio — reading and
  listening use different cognitive channels.
- MR_IMS: Director voice risks feeling "performatively creative" rather
  than dreamlike. Dreams don't have a visible artist. Dreamer whisper
  alone, used sparingly (~every 30-60s), is stronger.
- VC: Implied by bandwidth analysis — the feedback bottleneck is already
  doing the work of making the Director's influence felt.

**Recommendation:** Demote Director narration from audio channel to
text/subtitle mode. Treat it as a demonstration or debugging aid, not a
production channel. The Dreamer's whisper is the real narration voice.

### Competing lenses (Phase 4) are premature

- PCE: A single Director with shifting Dreamer state may already produce
  enough register variation without explicit lens competition.
- MR_IMS: Adding a second Mueller-like scheduling problem (7 goal types
  × N lenses) before proving the first is too much combinatorial space
  with no intuition for what "good" looks like.
- VC: Lens competition is formally the same mechanism as goal competition
  but functionally different — lenses are aesthetically distinct, not
  operationally distinct. Requires different tuning.

**Recommendation:** Prove single-Director output in Phase 2 before deciding
whether lens competition adds value. It may never be needed. If it is
needed, weight randomness and stochastic drift much higher than
content-driven switching — dreams shift aesthetic register because the
interpretive stance drifts, not because the content demands a new lens.

### Phase 1 → Phase 2 should be fast

- PCE: "The risk isn't that the v0 commits to the wrong rendering strategy
  — it's that a working v0 creates momentum that makes Phase 2 feel like
  a luxury rather than the actual test."
- MR_IMS: The real question can't be answered until Phase 2 runs on stage.
- VC: The transition from Phase 2 to Phase 3 is the real architectural
  cliff, not Phase 1 to Phase 2.

**Recommendation:** Treat Phase 1 success as permission to move fast into
Phase 2. Don't polish the v0. The v0 proves the scheduler; the Director
proves the system.

## Unique Contributions by Reviewer

### PCE: Theoretical Framing

**The feedback loop breaks prediction orthogonality.**

In Janus's Simulators paper, the simulator is time-invariant — it governs
but doesn't change. The feedback loop breaks that: the Dreamer (simulation
rule) mutates in response to what the Director (simulacra) rendered. The
act of imagining changes what you're imagining about. This is a system
where the rendering is part of the state transition function, not
downstream of it.

The lossy compression in the feedback (visual prompt → handful of concept
tokens + valence) is what prevents this from being a fixed point or a
runaway oscillator. It's a controlled hallucination in the precise sense:
the feedback is real but degraded, so the system drifts rather than locks
or explodes.

**The gap as mutual information.**

The gap between what the Dreamer says and what the Director shows is the
mutual information between two different compression schemes applied to
the same emotional state. The Dreamer compresses the situation into a goal
type + narration line. The Director compresses it through a style guide,
attention budget, and LoRA training distribution. The dream *is* the
interference pattern between them.

Gap width is probably the most important tunable parameter in the whole
system. Too narrow = illustrated audiobook. Too wide = incoherent. The
right width is where the feedback produces state changes the Dreamer's own
logic would not have reached without the Director's intervention.

**Coincidence retrieval as hash collision.**

Mueller's approach is fundamentally different from embedding similarity.
An episode surfaces when enough independent indices co-activate above a
threshold. That's closer to a hash collision than nearest-neighbor lookup.
The consequence: the system can produce genuine surprises from structural
coincidence, not semantic proximity.

**Concrete recommendation: Director-injected index type.**

When the Director's novel concepts become active indices, they enter a
system designed for coincidence counting. Mueller's original indices are
typed (situation, goal_type, emotion, place, recency). Director-generated
concepts ("membrane," "sacred_threshold") need their own type —
`director_concept` — that participates in coincidence counting but with
its own weight or threshold contribution. Otherwise:
- Too many novel indices → flooding (Director dominates retrieval)
- Too few → drowning (Director concepts never push past threshold)

### MR_IMS: Structural Warnings

**Semantic drift is a different failure mode from amplitude runaway.**

Decay at 0.95/cycle prevents amplitude runaway. But within 10 cycles,
"membrane" → "origin" → "water" → "dissolution" could leave you in
completely different conceptual territory with no legible connection to
where you started. This might be fine (dreamlike migration) or might be
bad (incoherent noise).

The FIFO-6 on active indices is the real damping mechanism for semantic
drift, not the decay factor. The FIFO naturally pushes out old concepts
as new ones enter. Worth instrumenting carefully in Phase 3: track how
many Director-originated indices survive in the active queue over time,
and whether the conceptual territory stays legible.

**Graph authoring must happen simultaneously with scheduler development.**

The v0 brief says Codex builds the scheduler and someone hand-authors the
dream graph. But the quality of traversal depends entirely on how the
nodes are indexed (situation, goal_type, emotion, place, recency cues).
The authoring bottleneck is that each of 20-30 nodes needs a rich,
consistent set of indexing cues. Author the graph and tune the scheduler
together, not sequentially.

**The IMS architecture isomorphism.**

The daydream system is essentially the IMS instrument-stack architecture
viewed from the opposite end:

| Daydream System | IMS Architecture |
|---|---|
| Dreamer (emotional state machine) | Sensor/input layer |
| Director (artistic interpretation) | Mapping layer |
| Stage (Scope + Lyria) | Output layer |
| Feedback loop | Bidirectional coupling |
| APC Mini | Conductor interface |

Building the same system twice at different scales — one on a microcontroller,
one on an LLM + video pipeline. Worth noting because patterns that work at
one scale may transfer.

### VC: Control Theory and the Hardest Question

**Lossy feedback as dimensionality reduction bottleneck.**

The feedback isn't just "lossy by design." It's a specific dynamical
structure: the Director receives high-dimensional state (dreamer block),
produces high-dimensional output (visual prompt + music + narration), and
feeds back a low-dimensional summary (novel_concepts, valence, situation
boost). That dimensionality reduction at the feedback junction prevents
mode-locking. If feedback were high-fidelity, you'd get either runaway
amplification or rigid entrainment. The narrow channel means the Dreamer
"hears" the Director through a fog — exactly how dreams seem to work
phenomenologically.

**Concrete recommendation: Director context window design.**

The Director's memory of its own recent outputs is underspecified and
matters a lot. Too little memory → slideshows (no slow-build coherence).
Too much → loops (LLM falls into comfortable patterns).

Recommended: asymmetric context window.
- `novel_concepts` from the last N cycles (cheap, structured, thematic
  memory — lets the Director maintain conceptual threads)
- Full `visual_prompt` from only the last 1-2 cycles (prevents stylistic
  entrainment — the Director can't just rephrase what it just said)

**The contradiction question (the hardest one).**

What happens when the Director's interpretation contradicts the Dreamer's
goal type?

Example: Dreamer is in REVENGE mode (high tension, directed grievance).
The Jodorowsky lens wins and interprets this as a ritual of forgiveness.
Sacred geometry. Golden light. Positive valence feeds back.

Two possible behaviors:
1. **Dreams-as-therapy:** The Director resolves the anger through
   reinterpretation. The revenge goal decays because the valence shifted.
   The dream processes the emotion and moves on.
2. **Dreams-as-intensification:** The contradiction registers as surprise
   (Mueller: 0.25 strength boost). The revenge goal intensifies. The
   Director's "forgiveness" imagery makes the Dreamer angrier.

Both produce interesting behavior, but they're different systems.

Suggested resolution: **gate by activation strength.**
- High-activation goals resist Director-induced valence shifts (strong
  feelings can't be dreamed away easily)
- Low-activation goals are susceptible (mild grievances can be dissolved
  by beautiful imagery)

This creates a natural dynamic where the system can process low-level
emotional noise through reinterpretation, but high-intensity situations
persist and obsess regardless of what the Director does. That matches
how real dreaming works — you can't dream your way out of genuine trauma,
but a slight annoyance might dissolve in pleasant imagery.

**Lens competition tuning (if ever built).**

Goal-type switches should be motivation-driven (situation content triggers
them). Lens switches should be more stochastic, more susceptible to
serendipity, less driven by content. Dreams don't switch aesthetic register
because the content demands it — they switch because the mind's
interpretive stance drifts. Weight randomness and decay much higher in
lens competition than in goal competition.

## Additional Observations (from the conversation that produced these reviews)

### The Dreamer is not swappable the way the Director is

The synthesis of the three reviews concluded that "the architecture is good
because of where it draws the lines, not because of what's on either side."
That's half right. The seam design is good — the `dreamer` block as stable
interface means the Director can be swapped, replaced, or removed without
touching the scheduler. But the reverse is not true. Mueller's specific
scheduling dynamics — coincidence retrieval, competing goal types, decay,
the performance/daydreaming oscillation — are what make the traversal feel
like a mind rather than a random walk or a playlist. You could swap in a
different Director (or no Director). You cannot casually swap the Dreamer
without losing the core phenomenology.

The architecture is asymmetric: the rendering layer is a swappable
hypothesis, but the emotional scheduler is a structural commitment.

### Performance/daydreaming as a conductor question

None of the three reviews engaged with the APC Mini fader between
immersion and introspection as a live performance instrument. The
MR_IMS reviewer noted the IMS isomorphism (Dreamer = sensor, Director =
mapping, Stage = output, APC Mini = conductor) but didn't follow through
on what it means.

The performance/daydreaming toggle is not just a mixing parameter. It's
the performer's primary creative decision: when does the audience see
the mind versus when are they just inside the dream? That decision is
real-time, musical, responsive to what's happening on stage. It's what
makes this a performed piece rather than a screensaver. The APC Mini
doesn't just control volume levels — it controls the phenomenological
mode of the whole system.

This deserves explicit design attention: what does the fader actually
control? Narration density? Interior-state visualization opacity? The
balance between palette-literal and Director-wild imagery? Probably all
of these, mapped to a single physical gesture, with the details tuned
by ear during rehearsal.

### Mueller's mutation complements the Director — it doesn't just fall back

The architecture doc frames Mueller's structural mutation (`dd_mutation.cl`)
as "v0 fallback only," replaced by the Director's imaginative mutations.
That undersells it. The Director produces associative, aesthetic mutations
— a wall becomes a breathing membrane, a harbor becomes a flooded
cathedral. Mueller's mutation produces structural mutations — swap the
character, swap the place, swap the action type. These are different
operations that complement each other.

When the Director is running, it's the primary source of novelty. But
the Director can get stuck too — LLMs have their own attractors and
comfortable patterns. Mueller's mechanical mutation (try a different
situation, a different place) can break the Director out of a rut the
same way it breaks the Dreamer out of one. Worth keeping as a real
mechanism, not just a legacy fallback.

### The offline/online split is a deliberate departure from Mueller

Mueller's DAYDREAMER doesn't have a "prep" phase and a "play" phase.
It has one loop that alternates between performance and daydreaming.
The current architecture's split — offline graph generation, then
real-time traversal — is closer to how a film score works (compose it,
then perform it). Mueller is more like jazz (the composition is the
performance).

Both are valid, but they produce different phenomenology. The offline
graph gives control and reliability. A live Mueller-like loop gives
emergent switching and surprise. Eventually the system probably wants
both: an offline graph as the base score, with a live Mueller-like
loop that can deviate from it during performance. That's a Phase 5+
question, but worth naming now so the architecture doesn't accidentally
close the door on it.

## Consolidated Design Recommendations

These are concrete enough to inform implementation. Ordered by when they
become relevant in the build sequence.

### Phase 1 (v0 spike)

1. **Author dream graph simultaneously with scheduler.** Don't hand-author
   20-30 nodes and then point the scheduler at them. Build both together
   so index structure and traversal logic inform each other.

2. **Don't polish v0.** Use Phase 1 success as permission to move fast
   into Phase 2. The scheduler alone is necessary but not sufficient.

### Phase 2 (single Director)

3. **Director context window: asymmetric.** Novel_concepts from last N
   cycles, full visual_prompt from last 1-2 only.

4. **Dreamer narration: sparse whisper only.** One line every 30-60
   seconds, not continuous. No Director audio voice.

5. **Evaluate single-Director register variation** before considering
   competing lenses. The shifting Dreamer state may already produce
   enough aesthetic variety.

### Phase 3 (feedback loop)

6. **Director-injected indices need their own type.** `director_concept`
   with its own weight or threshold contribution in coincidence counting.
   Prevents flooding and drowning.

7. **Gate feedback valence shifts by activation strength.** High-activation
   goals resist Director-induced shifts. Low-activation goals are
   susceptible. This creates dreams-as-therapy for mild emotions and
   dreams-as-intensification for strong ones.

8. **Instrument the FIFO-6.** Track how many Director-originated indices
   survive in the active queue over time. Track conceptual territory
   drift. The FIFO is the real semantic damping mechanism.

9. **Monitor semantic drift separately from amplitude.** Decay handles
   amplitude. FIFO handles semantic drift. Both need logging.

### Phase 4+ (if reached)

10. **Lens competition tuning: weight stochasticity high.** Lens switches
    should be drift-driven, not content-driven. Higher randomness, higher
    decay, more susceptibility to serendipity than goal-type competition.

11. **Director narration as text, not audio.** If ever surfaced, use
    subtitles or text overlay. Reading and listening use different
    cognitive channels — don't compete with Lyria for the audio channel.
