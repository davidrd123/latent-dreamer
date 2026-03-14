# The Game Engine Model

Supersedes doc 16 (rendering agents). Written to capture the architecture
that emerged from pushing on "what does the Director actually produce?"
and landing on a simpler, more honest answer.

**Scope:** This doc replaces multi-agent rendering, not live cognitive
feedback. The Director feedback loop from doc 12 is a cognitive loop
that feeds indices and activation back into the Dreamer. That loop is
unchanged. What's replaced is the idea that three LLM agents improvise
visual and musical content every cycle.

## What Changed From Doc 16

Doc 16 described three LLM agents improvising every cycle: Director
(interpret), Cinematographer (render visual), Composer (render music).
That model was overbuilt. It came from film production thinking — each
role as a creative agent with its own imagination.

The clearer framing is a game engine. Pre-compute the world. Traverse it
with emotional dynamics. Render down state to prompts.

The three-agent model collapsed because:

1. The "Director's delta" was already visual ("light comes from inside,
   the ring is no longer hidden") — the line between interpretation and
   cinematography was blurry in practice.
2. The Cinematographer was really a prompt engineer, not an auteur. The
   creative leap was always upstream.
3. Music doesn't need to be "composed" from the Director's
   interpretation. Music IS the emotional modulation layer — it responds
   to the Dreamer's cognitive state directly, not to the visual content.

## The Architecture

```
                    PREP TIME
                    ─────────
Creative brief ──→ Authoring Director (LLM, wild, curated)
                         │
                         ▼
                    Dream Graph
                    (nodes with rich visual descriptions,
                     emotional valences, tags, indices,
                     situation affiliations, connections)


                 PERFORMANCE TIME
                 ────────────────

  Dream Graph          Dreamer               Performer
  (pre-authored)    (Mueller dynamics)      (APC Mini faders)
       │                   │                      │
       │  node             │  family              │  nudge tension,
       │  description      │  tension             │  boost situation,
       │                   │  emotion             │  shift music params
       │                   │  situation            │
       │                   │                      │
       ▼                   ▼                      ▼
  ┌─────────┐    ┌──────────────────┐    ┌──────────────┐
  │Renderer │    │ Music modulation │    │ Post-effects  │
  │(LLM)    │    │ (parametric)     │    │ (deterministic│
  │         │    │                  │    │  color cast   │
  │node desc│    │base prompt       │    │  by family)   │
  │→ Scope  │    │+ Dreamer state   │    │              │
  │  prompt │    │→ Lyria params    │    │              │
  └────┬────┘    └───────┬──────────┘    └──────┬───────┘
       │                 │                      │
       ▼                 ▼                      ▼
     Scope             Lyria               Scope params
   (visual)           (music)            (color, contrast)
       │                 │                      │
       └─────────────────┴──────────────────────┘
                         │
                         ▼
                       Stage
```

### 1. Dream Graph (the product)

The dream graph is the primary creative artifact. Each node contains:

- Visual scene description (rich, authored, curated)
- Situation affiliation (which situation this node lives in)
- Emotional valences (hope, threat, anger, grief, dread)
- Tags / indices (for retrieval by the Dreamer)
- Connections to other nodes (traversal edges)
- Musical tags (optional — situation-level mood, not per-node scoring)

The graph is authored with LLM help (the "authoring Director" — offline,
high temperature, wild interpretation of the creative brief) but curated
by the human. The authoring process is where the creative imagination
lives. It is not a performance-time process.

**Scale.** The graph is organized as clusters (see "Resolved: Families
and Visual Content" below). A brief with 4-5 situations, ~4-5 base
nodes per situation, plus REVERSAL/REVENGE variant nodes per key
scene, produces 40-50 nodes minimum for a fixture that doesn't feel
repetitive across a 50-cycle performance. The authoring pipeline isn't
just "highest-value tooling" — it is load-bearing infrastructure
without which the architecture can't function. If the graph is thin,
the output is thin. No live agent rescues a bland node.

**Quality gate.** Every node should pass the test: "would I want to
watch 5-8 seconds of this?" during authoring, before it enters the
performance loop. In the three-agent model, a live Director could
compensate for thin nodes by interpreting on the fly. In the game
engine model, the node description IS the creative material. The
brief + authoring pipeline must produce nodes that are individually
compelling, not just structurally correct.

The creative brief is the production design. It constrains the visual
world: materials, palette, composition rules, forbidden elements, musical
genre. Everything in the graph is traceable to the brief.

### 2. Dreamer (the game logic)

The Dreamer traverses the graph using Mueller's cognitive dynamics:

- Goal competition (which situation wins activation)
- Family activation (REVERSAL, ROVING, RATIONALIZATION, etc.)
- Emotional diversion (dread drops, hope rises)
- Counterfactual branching (what-if paths)
- Episodic memory (reminding, coincidence)

The Dreamer produces game state: which node, which family, what
emotion, what tension. The cognitive family determines the *posture*.
But the Dreamer doesn't produce visual content.

**Note:** Today the Dreamer doesn't directly pick nodes. An
adapter/selection layer sits between the Dreamer's cognitive output
(goal competition, branch facts, active indices) and node arrival.
The adapter reads the Dreamer's state, applies benchmark-specific
delta tables, derives which situation becomes salient, and retrieves
the matching node. This seam — Dreamer state → adapter → node
selection — is load-bearing and should stay explicit even as the
pipeline matures. "The Dreamer picks the next node" is the
aspirational description; the adapter is the current mechanism.

Either the Python scheduler or the Clojure kernel can serve as the
Dreamer. The rendering pipeline doesn't care which — it consumes
the same state.

### 3. Renderer (one LLM call per cycle)

The renderer takes the current node's pre-authored visual description
and constructs a text-to-video prompt that the model (Scope/Wan) will
produce well.

**Inputs:**
- Node's visual scene description (from the dream graph)
- Dreamer state: goal_type, family, tension, energy
- Prompt engineering rubrics from the team's strategy docs
- Model-specific prompt patterns (Wan spec, etc.)
- Previous cycle's visual prompt (for transition continuity)

**Output:**
- `visual_prompt` — text-to-video prompt for Scope
- `transition_type` — soft_cut, hard_cut, or dwell
- `dwell_time` — how long to stay on this node

**Character:**
- Lower temperature. Craft, not improvisation.
- The renderer does not invent visual content. It renders the node's
  authored description into a prompt that the model will execute well.
- The skill is prompt engineering: attention budget, front-loading,
  strategic simplification, model-specific vocabulary.
- "Model whisperer," not "Cinematographer."

**Revisitation.** A 50-cycle performance with 40-50 nodes means
nodes get revisited. On the second (or third) visit, the visual
prompt is the same node description rendered again — but the music
and color cast are different (different emotional context). The
renderer may also adjust atmospheric details on revisitation:
"same corridor but the frost is thicker," "same stage but the
lights are dimmer." This is a small amount of visual modulation
motivated by traversal history, not by LLM improvisation. The
renderer should receive `visit_count` and/or `last_seen_cycle` to
enable this.

**Why still an LLM call:** Applying prompt craft rubrics requires
judgment — which element to front-load, how to simplify for the
model's attention budget, what negative prompts to add. Revisitation
modulation adds another reason — adjusting atmospheric details based
on visit context is judgment work. This is the kind of thing an LLM
does well and templates do badly.

### 4. Music (parametric modulation, not composition)

Music is the emotional modulation layer. The same visual scene feels
completely different depending on the score. This is where the dream
*feels* like something — calm image with rising dread underneath is
dream logic. The music carries what the visuals don't show.

**Base prompt:** Set from the creative brief's musical palette at
performance start. Genre, instruments, identity, production style.
Stays constant across the performance. This is the musical equivalent
of the visual production design.

**Modulation parameters:** Driven by the Dreamer's emotional state
each cycle. Lyria prompts are composable — the base stays and
parameters shift on top:

- Key / mode (major ↔ minor, modal shifts)
- Density (sparse ↔ dense)
- Brightness (dark ↔ bright)
- Tempo (slower ↔ faster)
- Consonance / dissonance

**Trajectory, not snapshot.** The family→music mapping below is a
starting point, but reading only the current family will feel
mechanical after three cycles. The modulation should accumulate:
"we've been in REVERSAL for three cycles and hope just started
rising" is a different musical moment than "REVERSAL just fired."

Concrete mechanism: the music modulation function receives a sliding
window of the last N cycles' emotional states (not just the current
cycle). The parametric mapping includes rate-of-change terms:
"tension has been rising for 4 cycles" produces different music than
"tension just jumped." The Dreamer's trace already captures this —
`emotion_shifts` and `emotional_state` per cycle. The modulation
should consume a window, not a point.

**Family defaults (starting point):**

| Family | Musical posture |
|--------|----------------|
| REVERSAL | Shift minor, density up, brightness down, dissonance rises |
| ROVING | Shift major, density down, brightness up, space opens |
| RATIONALIZATION | Tension resolves, something settles, consonance |
| REHEARSAL | Repetition with variation, building intensity |
| REVENGE | Low register, rhythmic urgency, compressed dynamics |

**The performer's faders** can touch these same parameters directly.
You hear the Dreamer's emotion, but you can push against it or lean
into it from the APC Mini. The faders are the most intuitive when
they map to what you hear.

**LLM call:** Mostly not needed per cycle. Parametric modulation of
the base prompt may be sufficient. An LLM call may help when something
bigger shifts (new situation dominates, major family change) — but
this can run on a slower cadence (every 2-3 cycles) or be triggered
by threshold changes rather than running every cycle.

### 5. Post-effects (deterministic, no LLM)

Color cast by cognitive family, applied to the rendered video.
Deterministic — no LLM call, just Scope parameter adjustments.

| Family | Color cast |
|--------|-----------|
| REVERSAL | Cool shift (blue-indigo) |
| ROVING | Warm shift (amber-gold) |
| RATIONALIZATION | Soft warm (teal-settling) |
| REHEARSAL | Neutral with slight desaturation |
| REVENGE | High contrast, slight red push |

These give the performer instant visual feedback about what the
Dreamer is doing — you see the color shift before you understand
the content change. A beat to react.

### 6. Cognitive Feedback (doc 12 schema — live, cognitive only)

The existing Director feedback schema from doc 12 still applies
and is unchanged by this doc:

- `director_concepts` (1-3 novel concepts)
- `situation_boosts` (0-2 dormant situations)
- `valence_delta` (-0.10 to +0.10)
- `surprise` (0.0 unless genuinely unexpected)
- `emotional_episodes` (rare)

This feeds back into the Dreamer's cognitive state — indices,
activation, situation boosts. NOT into the visual content. The
feedback loop is purely cognitive.

**This is the part doc 17 does NOT replace.** The game engine model
replaces multi-agent *rendering*. The cognitive feedback loop is a
different system — it nudges the Dreamer's traversal, not the visual
output. The renderer doesn't feed back. It just reflects whatever
cognitive state the Dreamer is in.

**Cadence:** Start at full cadence (every cycle) and measure whether
backing off hurts. Don't pre-optimize by reducing cadence before
seeing what the feedback actually does to traversal quality. The
doc 12 worked examples (n07 backstage headlights → "honest_light"
→ s4 wakes up) were the most compelling demonstrations of the
system — those were feedback-driven moments where the Director
noticed something the Dreamer's indices didn't capture. That
capability should be preserved, not demoted by default.

**Evaluation ladder.** The right way to test this:

1. First watchable run: graph + Dreamer + renderer + parametric
   music. No feedback loop. Answer: does the stage output feel
   like anything? (doc 13 Level 3)
2. Second run: add feedback at full cadence. Answer: does the
   Director materially improve the dream's traversal?
3. Then decide cadence based on evidence, not theory.

## The Performer's Instrument

The APC Mini faders map to:

**Dreamer handles:**
- Tension (global emotional pressure)
- Energy (activity level)
- Situation boost (push a specific situation's activation)
- Family override (force a specific cognitive family)

**Music handles:**
- Density
- Brightness
- Key / mode shift
- Tempo

**Visual handles (stretch goal):**
- Scope strength / guidance
- Post-effect intensity

The performer is conducting the Dreamer primarily. The Dreamer's
state flows into the music parametrically and into the visuals
through the renderer. Direct music handles let the performer also
express independently of the Dreamer when the moment calls for it.

## What This Replaces

- **Doc 16 three-agent model.** Collapsed to one renderer + parametric
  music. The "Director" is a prep-time authoring tool, not a
  performance-time rendering agent.
- **Doc 03 single-Director rendering.** The Director no longer produces
  `visual_prompt` or `music_prompt`. The renderer and parametric music
  pipeline handle those.
- **Doc 01 goal-type-to-cinematography table.** Still valid reference
  material, but lives in the renderer's system prompt, not a
  Director's.

**What this does NOT replace:**

- **Doc 12 Director feedback schema.** The cognitive feedback loop
  (concepts, boosts, valence, surprise, emotional episodes) feeds back
  into the Dreamer's cognitive state. This is a cognitive loop, not a
  rendering loop. It is unchanged and should start at full cadence.

## What This Doesn't Change

- The Dreamer architecture (Python scheduler or Clojure kernel)
- The DreamNode contract (what the stage consumes)
- The trace schema (what evaluation inspects)
- The creative brief format
- The Director feedback schema (doc 12)
- The stable seams from doc 14

## Why This Is Simpler

| | Doc 16 | This doc |
|---|---|---|
| LLM calls per cycle | 3 (sequential + parallel) | 1 (renderer) + optional feedback |
| Music | LLM Composer every cycle | Parametric modulation of base prompt |
| Visual modulation | Director delta + Cinematographer | Node description + renderer craft |
| Emotional expression | Spread across all three agents | Music carries it |
| Creative imagination | Live every cycle (Director) | Prep-time (authoring Director) |
| Performer's instrument | Unclear — talk to which agent? | Faders → Dreamer + music params |
| Coherence guarantee | Director must maintain continuity | Graph IS the continuity |
| Latency budget | Director must finish before others start | One LLM call, music is parametric |

## Where Surprise Comes From

Not from live LLM improvisation. From:

1. **The Dreamer's path.** Which nodes surface, when, in what emotional
   context. Mueller's cognitive dynamics — goal competition, family
   activation, counterfactual branching — are the surprise engine.
2. **The performer.** Pushing against or into the Dreamer's state.
   Forcing a family. Boosting a dormant situation.
3. **Music-visual dissociation.** Calm image with rising dread. Hostile
   scene with settling warmth. The music carries what the visuals
   don't show. This is dream logic made audible.
4. **The graph's richness.** A well-authored graph with 50+ nodes,
   multiple situations, charged objects, and emotional valences has
   combinatorial depth. The same node arrived at via REVERSAL feels
   different from the same node arrived at via ROVING — not because
   the visual changes, but because the music and color cast change,
   and because of what came before.

Mueller's original DAYDREAMER doesn't improvise mental images — it
retrieves and recombines pre-stored structures. The dream feels dreamy
because of *which* structures surface and *when*, not because the
imagery is generated fresh. The Dreamer's cognitive dynamics ARE the
surprise. The rendering should be reliable.

## Resolved: Families and Visual Content

Previously open question #5, now a decision:

**Whether visual content changes depends on the family.**

| Family | Visual content changes? | Graph implication |
|--------|------------------------|-------------------|
| ROVING | Yes — different situation entirely | Different node. Dreamer traverses to a new situation's node. |
| REVERSAL | Yes — counterfactual version of scene | Different node. Author variant nodes ("same corridor, but the ring is visible"). |
| REVENGE | Usually yes — acting on/against the scene | Different node. The visual action is different content. |
| RATIONALIZATION | No — same scene, different emotional posture | Same node. Music + color cast carry the shift. |
| REHEARSAL | No — same scene, repeated with variation | Same node. Music carries the repetition/building quality. |

This means the graph is organized as **clusters**: a base scene node
plus 1-2 family-specific variant nodes for families that change
visual content (REVERSAL, REVENGE). The authoring pipeline needs to
account for variants — a brief with 4 situations and 5 base scenes
per situation might have 30-40 total nodes including variants.

Families that change posture but not content (RATIONALIZATION,
REHEARSAL) don't need variant nodes. The renderer renders the base
node faithfully; music and post-effects carry the emotional shift.

## Open Questions

1. **Authoring pipeline.** How does creative brief → dream graph work?
   This is where the "Director as wild interpreter" lives — offline,
   slow, curated. What tooling does this need? This is probably the
   highest-value thing to build. The cluster structure (base nodes +
   family variants) shapes the authoring workflow.

2. **Lyria mid-stream modulation (hard dependency).** The parametric
   music modulation assumes Lyria can shift key, density, brightness
   mid-stream without regenerating from scratch. This needs validation
   against Lyria's actual capabilities before building around it. If
   Lyria can't do smooth parametric shifts, alternatives:
   - Pre-authored musical stems per family that crossfade
   - Lyria prompt rewriting on cognitive transitions (not every cycle,
     but on family changes or threshold emotional shifts)
   - Hybrid: parametric where Lyria supports it, stem crossfade where
     it doesn't

3. **Renderer grounding.** How much of the node's visual description
   does the renderer use verbatim vs paraphrase for the model? If the
   node descriptions are authored with LLM help during prep (and
   therefore already well-written), the renderer's job might be mostly
   model-specific formatting: aspect ratio, negative prompts, style
   tokens, attention budget compliance. Worth testing whether a light
   LLM call or even deterministic formatting is sufficient when the
   source descriptions are good.

4. **How many nodes is enough?** With family variants, a 4-situation
   brief might produce 30-40 nodes. Is that enough for varied
   performances, or does it become repetitive? The graph's richness
   determines the ceiling. The authoring pipeline question (#1)
   is load-bearing.
