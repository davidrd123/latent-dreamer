# Architecture Review Request for 5 Pro

## Context

This is a request for high-level architectural review of the
Conducted Daydreaming project. We've been building fast across
multiple tracks and want an outside perspective on whether the
overall approach holds together.

The project builds a real-time conducted dream instrument. A
cognitive engine (inspired by Robert Mueller's 1990 DAYDREAMER
system) drives scene selection through a pre-authored dream graph.
Visual output goes through Scope (a real-time text-to-video
system). Music will be parametric via Lyria. A performer conducts
live with an APC Mini controller.

RepoPrompt will include the codebase. The key docs for context:

- `daydreaming/Notes/experiential-design/01-the-experience.md` —
  the foundational experience vision
- `daydreaming/Notes/experiential-design/14-operating-map.md` —
  five layers, four stable seams, two Dreamer tracks
- `daydreaming/Notes/experiential-design/17-game-engine-architecture.md` —
  the game engine model (major architectural reframe)
- `daydreaming/Notes/experiential-design/18-graffito-vertical-slice.md` —
  the concrete next deliverable (scenes 3-4 of a real film)
- `daydreaming/Notes/experiential-design/19-graffito-node-schema.md` —
  the frozen node schema for dream graphs
- `daydreaming/Notes/experiential-design/20-narration-layer.md` —
  inner voice narration via Flash
- `kernel/doc/mueller-coverage-assessment.md` — what we've
  implemented vs. what Mueller's original DAYDREAMER had
- `kernel/src/daydreamer/` — the Clojure kernel (Mueller dynamics)
- `daydreaming/Notes/experiential-design/12-director-prompt-spec.md` —
  the Director feedback loop spec

---

## What we're asking

We have a lot of balls in the air and it's been chaotic. We want
your honest assessment across seven axes. For each, we've stated
what we decided and why — tell us if you think the decision is
right, wrong, or incomplete.

---

### 1. Mueller fidelity vs. pragmatic use

**What we did:** We simplified Mueller significantly. The Clojure
kernel implements three families (REVERSAL, ROVING, RATIONALIZATION)
out of Mueller's seven goal types. Several mechanisms use "bridges"
(stored heuristics) rather than derived generative machinery. There
is no rule engine. The Mueller coverage assessment shows roughly
40% faithful implementation, 30% bridged, 30% absent.

**Why:** Doc 17 argues that Mueller's key insight is *which*
structures surface and *when*, not content generation. The cognitive
dynamics (goal competition, emotional diversion, serendipity) are
what make dreaming feel like dreaming. The generative machinery
(rule engine, mutation, planning) was needed in 1990 because
Mueller's output was literal plan steps. We have LLMs for
generation now.

**The question:** Is this a productive simplification, or are we
keeping Mueller's vocabulary while losing the machinery that made
DAYDREAMER interesting? Specifically: does the missing rule engine
matter for producing compelling dream trajectories, or is the
activation/competition/diversion dynamics sufficient?

---

### 2. The game engine model (doc 17)

**What we did:** Pre-authored dream graph (50+ nodes, authored
offline) + Mueller traversal (the Dreamer decides which node to
visit) + one-shot renderer (one LLM call per cycle: node
description + Dreamer state → visual prompt). This replaces an
earlier three-agent model where multiple LLMs collaborated per
cycle.

**The insight:** Mueller's DAYDREAMER retrieves pre-stored episodic
structures, it doesn't generate them. The dreaming is in the
traversal pattern, not the content. So: author rich content offline
(with LLM help, curated by human), let Mueller dynamics drive the
walk, render what you land on.

**The question:** Does this architecture preserve what matters about
DAYDREAMER's dynamics, or does it reduce the Dreamer to a glorified
playlist shuffler? What distinguishes Mueller-driven traversal from
simpler alternatives (weighted random, Markov chain, hand-authored
sequence)? Is the pre-authored graph the right abstraction, or
should the system be generating/mutating nodes at runtime?

---

### 3. Graffito as first testbed

**What we did:** Chose Graffito (a real animated film by Mark
Friedberg) as the first vertical slice. It has a trained LoRA,
codified prompt engineering rubrics (9 strategy docs), a clip
library with director's selects, and an active collaborator whose
reaction is the evaluation.

**The trade-off:** Graffito has the richest source material but the
narrowest visual range (90 seconds of training footage, one visual
world). The LoRA collapses diverse prompts toward similar output.
Alternative: use recognizable IP (Escape from New York, kaiju) with
the base model for broader visual diversity.

**The question:** Is Graffito the right first test, or would a
broader visual palette better demonstrate whether dream-driven
traversal produces output that *feels like anything*? Does the
LoRA's narrowness make it harder or easier to evaluate whether the
cognitive dynamics matter?

---

### 4. The narration layer

**What we did:** Built a narration bridge (doc 20) that pipes
engine output through Gemini Flash to produce inner monologue —
1-3 sentences per cycle, grounded in cognitive state + scene text,
displayed on a companion page alongside the visual output.

**The argument:** Visuals alone are a hallucinatory montage. The
narration makes the dreaming process legible — the audience
perceives *why* scenes are surfacing, not just *what* they see.
This is the doc 01 experience: oscillation between immersion in
the dream and meta-awareness of the dreaming process.

**The hierarchy we landed on:** Cognitive state is the primary
input to the inner voice. Scene text is secondary. A Scope
screenshot (what actually rendered) is optional enrichment. The
dreamer doesn't watch itself dream — it knows why it's here.

**The question:** Is text narration the right channel for
externalizing interiority? Does it compete with or complement
the visual experience? Would parametric music alone (doc 17's
family → key/density/brightness mapping) be sufficient to make
the cognitive dynamics felt? Or is the narration solving a
problem that better visuals would solve on their own?

---

### 5. Priority ordering

**Current stack:**
1. Authoring pipeline (brief → dream graph) — how to produce
   rich 50+ node graphs efficiently
2. Renderer (node description + Dreamer state → Scope prompt)
3. Narration layer (inner voice companion)
4. Parametric music (Dreamer state → Lyria modulation)
5. Director feedback loop (built, working, demoted to secondary)
6. Kernel fidelity (rule engine, missing families)

**The question:** Are we building in the right order? Should
narration be this early (before we've even seen rendered output)?
Should the Director feedback loop be deprioritized given that
doc 17 demotes it to a secondary nudge? Is there something we're
not building that we should be?

---

### 6. Architectural complexity

**What we have:**
- Five layers (prep, dreamer, renderer/director, stage, evaluation)
- Four stable seams (DreamNode, Director packet, feedback schema,
  trace schema)
- Two Dreamer tracks (conducted mainline vs. Mueller fidelity)
- Two repos (latent-dreamer for spec + kernel, scope-drd for
  engine + stage)
- Kernel in Clojure (babashka), engine in Python, companion in HTML
- Multiple output formats (traces, visualizer, narration companion)

**The question:** Is this the right amount of structure for the
stage of the project, or is the architecture ahead of the art?
What would you simplify? Are the stable seams in the right places?
Is the two-repo split helping or hurting?

---

### 7. The "conducted" dimension

**The endgame:** Live performance with APC Mini. The performer
conducts the dream in real-time — faders control the crossfade
between situations, knobs modulate tension/energy, buttons trigger
mode switches between performance and daydreaming.

**What we're building:** Everything so far is batch/offline. The
Graffito vertical slice (doc 18) is explicitly "not a live
performance." The trace player (Option B) hand-authors a sequence
and renders it as a batch.

**The question:** Is the batch-first approach the right path to
live performance, or are we building assumptions into the offline
architecture that will need to be ripped out? What architectural
decisions should we be making now to keep the live path open?

---

## What we're NOT asking

- We're not asking for code review or implementation feedback.
- We're not asking you to evaluate the Clojure kernel's test suite.
- We're not asking about prompt engineering specifics.

We want your honest, high-level architectural assessment. If you
think something is fundamentally misguided, say so. If you think
something is a distraction, say so. If you think we're missing an
obvious approach, say so.

Be direct. We'd rather hear "this is wrong" than "this is
interesting but have you considered..."
