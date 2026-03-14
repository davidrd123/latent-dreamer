# LLM Roles in the Architecture

Purpose: Capture where LLMs play a role, where they're
deliberately excluded, and where they could add value as
the system matures. Not a design doc — a reference note
for keeping these decisions in mind during implementation.

---

## Available model tiers

| Model | Approx latency | Best for |
|-------|---------------|----------|
| Flash Lite | ~1-1.5s | Fast evaluation, structured analysis, comparison scoring |
| Flash | ~2-3s | Narration, appraisal, content generation with quality |
| Pro | ~5-10s | Deep reasoning, complex authoring proposals, evaluation |

Latency constraints are context-dependent:
- **Live performance cycle** — must fit within chunk boundary (~2-4s). Flash Lite or Flash only.
- **Batch/research mode** — any model. Latency is inconvenient, not blocking.
- **Authoring time** — any model. Human is reviewing anyway.

## Where AI models are already in the system

### Language-model-mediated roles

**Narration companion** (shipping lane, live)
Gemini Flash consuming the cycle packet and producing
inner-life narration text. Already partially working.
Grounded in cognitive state, not free-form generation.

**Director feedback** (L2, live)
Gemini providing external perturbation to the kernel:
situation boosts, conceptual associations, valence shifts,
serendipity bias. Implemented in `director.clj`. Functions
as a mechanism against closure over the authored graph.

**Visual world-building pipeline** (L1, authoring time)
Gemini Vision MCP in `daydream_vision/` for image generation,
analysis, comparison, and structured evaluation of visual
anchors. Already built and tested.

### Generative media model roles

**Rendering** (shipping lane, live)
Text-to-video via Scope. Diffusion model consumes the cycle
packet's visual prompt and produces rendered frames. Not an
LLM in the language sense — a generative media model.

**Music** (shipping lane, live)
Lyria parametric music generation. AI-generated audio
modulated by conductor state and cycle packet parameters.
Also a generative media model, not a language model.

### Evaluation/analysis roles

**Visual anchor evaluation** (L1, authoring time)
Gemini Flash Lite scoring generated images against structured
criteria (match, identityFit, paletteFit, constraintViolations).
Already in `daydream_vision/service.py`.

---

## Where the architecture explicitly plans for LLMs

### L2 operator content generation (research lane)
Doc 11's reframe: "We replace Mueller's content machinery
with LLMs behind typed operators." Mueller hand-coded
planning rules in Lisp. The system keeps his control
geometry (concern → operator → fork → evaluate → feedback)
but lets LLMs generate what happens *inside* the fork.

The operator boundary is typed; the content behind it is
LLM-generated. The LLM doesn't decide *which* operator to
run or *whether* to commit — those are deterministic control
decisions. The LLM generates *what* the operator produces.

### L1 authoring proposals (research lane)
The L1 authoring critic uses LLMs behind typed operators
to propose world enrichments. The constraint from doc 11:
proposals must be typed world-state diffs, not prose
fragments. If a proposal only adds "interesting descriptive
detail" and does not change typed state, score it zero.

---

## Where the architecture deliberately excludes LLMs

### The L3 scheduler (shipping lane, live)
The Facade-shaped scorer is deterministic. Priority tiers,
trajectory scoring, feature weights — all computable without
an LLM call.

Reasons:
- Runs on every cycle; latency matters
- Decisions must be inspectable via debug JSONL
- The whole point of the Facade/DODM design is that
  *authored annotations + deterministic scoring* can produce
  arc-shaped traversals without runtime generation
- If you can't explain why the scheduler chose this node
  by reading the score breakdown, the system is opaque

This is a firm boundary. Do not put an LLM in the
selection loop.

### Traversal state management (shipping lane, live)
Visit counts, recency, situation activation, tension/energy
tracking — all deterministic state updates. No LLM judgment
needed or wanted.

---

## Where LLMs could add value (not yet built)

### 1. Graph authoring assistance (L1 / authoring time)

**What:** An LLM takes a brief and proposes a draft graph
structure — nodes, edges, situation clusters, scheduler
annotations (delta_tension, delta_energy, setup_refs,
priority_tier).

**Why it's useful:** Hand-authoring graph fixtures is the
current bottleneck. The Graffito and City Routes YAML files
took real effort. An LLM could produce a first draft that a
human then reviews, corrects, and tunes.

**What to watch for:** The LLM needs to produce *traversable*
graphs, not just narratively interesting ones. Any LLM-generated
graph draft must compile to the frozen graph seam contract
(`21-graph-interface-contract.md`). The structural validation
suite (NetworkX tests for reachability, seam balance, edge
density, attractor nodes) should be the quality gate, not the
LLM's self-assessment. Schema drift from LLM-generated graphs
is a real risk — validate against the contract, not against
"does this look reasonable?"

**Relationship to existing work:** `brief_bridge.py` in
`daydream_vision/` already bridges from briefs to structured
output. The world defect linter (codex queue) would validate
the result. This is L1 applied to graph authoring itself.

### 1b. Graph annotation validation (L1 / authoring time)

**What:** An LLM reviews a human-authored graph fixture and
flags mismatches between annotations and content. "This node
claims delta_tension: 0.3 but its visual description is a
moment of calm — mismatch?"

**Why it's useful:** Different from the world defect linter
(which checks structural gaps). This checks whether the
scheduler annotations match the narrative content of each node.
Catches authoring errors before they reach the scheduler.

**What to watch for:** The LLM is judging content-annotation
alignment, not proposing new annotations. It flags for human
review, not auto-correction.

**Model tier:** Flash Lite is sufficient — this is structured
analysis of short text against numeric fields.

### 2. Richer narration from traversal provenance (shipping lane)

**What:** The narration companion gets not just the cycle
packet but also the scheduler's debug record — why this node
was chosen, what alternatives were considered, what the
tension target was.

**Why it's useful:** An LLM narrator that knows "the system
chose this node because tension was too high and it needed
release" can narrate that as inner life: "she needed to get
away from the noise." The scheduler's provenance becomes the
narration's source material.

**What to watch for:** The narration shouldn't expose the
scheduler's vocabulary directly ("the priority tier was 3").
It should translate structural decisions into character-level
language. Mueller's Appendix B already does this — different
templates for different cognitive modes.

**Relationship to existing work:** The narration layer spec
(doc 20) and Mueller Appendix B extraction (doc 12 in the
reading list) both address this. The debug JSONL already
contains the provenance; the missing piece is feeding it
to the narrator.

### 3. Event content generation at commit time (shipping lane)

**What:** When the L3 scheduler reaches a `canon_event`, the
content of that event is LLM-generated from the graph node's
metadata + traversal history + current conductor state.

**Why it's useful:** "The blackout siren" is a node ID; what
it *looks and sounds like* in this particular run, given what
came before, could be LLM-shaped. This is how the same event
node produces different experiences across performances.

**What to watch for:** Event generation must stay within the
node's authored constraints. The LLM elaborates, it doesn't
invent. If the event is "blackout siren," the generation
should vary the *texture* (how dark, how sudden, what's
visible) not the *semantics* (which event happens).

Specific bounds needed before implementing:
- Which graph fields are fixed (node_id, situation_id,
  event_type, delta_tension/energy) vs. variable (visual
  description detail, atmospheric modifiers, timing nuance)
- The LLM output must still compile to the cycle packet
  contract — it varies the prompt content, not the packet
  structure

**Model tier:** Flash for quality content generation within
the cycle budget. Flash Lite if latency is tight.

**Relationship to existing work:** The renderer already does
a version of this — the visual prompt is constructed from node
metadata and conductor state. This would make the renderer's
prompt construction LLM-assisted rather than template-based.

### 4. Structured evaluation of traversal quality (research lane)

**What:** An LLM watches two rendered runs (or reads two
traces) and provides structured judgment: "Run 2 felt more
intentional because returns happened at legible moments."

**Why it's useful:** Human judgment is the gold standard but
expensive. LLM judgment can be a fast screening pass —
especially useful during the robustness sweep where you're
comparing 15+ runs.

**What to watch for:** This is "LLM judges LLM output,"
which the 06 5 Pro response warns about. Mitigate by:
using structured evaluation criteria (not "rate this run"),
treating LLM judgment as supplement to human judgment (not
replacement), and checking whether LLM rankings correlate
with human rankings on a small calibration set.

**Relationship to existing work:** `daydream_vision/`'s
`pack-evaluate` already does structured LLM evaluation of
generated images. Same pattern, different artifact.

### 5. The appraisal pass (L2, research lane)

**What:** EMA's appraisal cycle — event in, concern deltas
out — optionally mediated by an LLM. Given a character's
state and a new event, the LLM assesses: "This event
threatens B's attachment goal, controllability is low,
intensity is high."

**Critical constraint: Mueller backbone first, LLM on top.**
The L2 refactor synthesis (`13-l2-refactor-synthesis.md`)
is explicit: preserve the Mueller control backbone (concern
kind, write-back distinction, theme-rule concern initiation,
cycle decay, serendipity mechanism) before adding LLM-
mediated appraisal. The LLM fills typed appraisal fields
that feed into the existing control loop — it does not
replace the control loop.

The wrong framing: "theme rules are ad-hoc, so let the
model decide what events mean." That makes L2 opaque.

The right framing: theme rules handle concern initiation
and the deterministic backbone. The LLM enriches appraisal
with contextual judgment (controllability, attribution,
intensity modulation) that rules alone can't provide.

**Why it's useful:** A real appraisal pass needs contextual
judgment about what an event *means* to a character — which
is a natural LLM task. But the judgment fills typed fields,
it doesn't make control decisions.

**Latency:** Flash Lite (~1.5s) may be fast enough for live
cycles if the output is tightly structured. Flash (~2-3s)
is acceptable for batch/research. Pro is authoring-time
only.

**What to watch for:**
- Output must be structured (concern type, intensity,
  controllability, attribution) not free-form
- EMA extraction (doc 05) says controllability should
  bias operator selection, not hard-dispatch it
- The appraisal pass produces inputs; the control loop
  uses them alongside situation context (Versu) and
  concern state
- Semantic drift gate: the LLM's appraisal should not
  silently shift what the kernel considers important
  across runs. Cache or seed for replayability in
  comparison experiments.

**Relationship to existing work:** EMA extraction (doc 05),
OCC extraction (doc 06), L2 refactor synthesis (doc 13),
and the kernel gap analysis (doc 04) all identify this as
the highest-value L2 addition.

---

## The governing principle

**LLMs generate content and evaluate meaning. Deterministic
systems handle control, scheduling, and state management.**

The boundary between them is the typed interface:
- Cycle packet (scheduler → renderer/narrator)
- Concern delta (appraisal → control loop)
- Structured proposal (operator → curation gate)
- Score breakdown (scorer → debug JSONL)

### Decision gates

The meaning/computation question is necessary but not sufficient.
Even if something needs contextual judgment, it may still need to
stay deterministic (or be tightly constrained) if any of these
apply:

1. **Live-cycle latency** — can the call fit within the chunk
   boundary? If not, it must be batch-only or pre-computed.
2. **Replayability** — must the same inputs produce the same
   trace? If yes, the LLM output must be cached or seeded
   deterministically.
3. **Graph contract compliance** — does the output write into
   the frozen graph seam (`21-graph-interface-contract.md`)?
   If yes, it must compile to that contract. No schema drift.
4. **Inspectability** — can a human read the debug JSONL and
   understand why this decision was made? If the LLM's reasoning
   is opaque, add structured output constraints.
5. **Semantic drift risk** — would accumulated LLM judgment
   shift the system's behavior in ways that are hard to detect
   or reverse? If yes, bound the LLM's authority (fill typed
   fields, don't make control decisions).

When in doubt: ask "does this need contextual judgment about
*meaning*?" If yes, check all five gates. If it passes, LLM.
If any gate fails, either keep it deterministic or add
constraints that satisfy the gate.

---

## Implementation priority

| Role | Lane | Model tier | When |
|------|------|-----------|------|
| Narration from provenance | Shipping | Flash | **Next** — wiring change, high impact |
| Graph authoring assistance | L1 / authoring | Flash or Pro | Soon — current bottleneck |
| Graph annotation validation | L1 / authoring | Flash Lite | Soon — catches authoring errors |
| Event content at commit time | Shipping | Flash | After narration layer solid |
| Structured evaluation | Research | Flash Lite | During robustness sweep |
| Appraisal pass | L2 research | Flash Lite (live) / Flash (batch) | During kernel refactor, Mueller backbone first |
