# Pressure Engine Reframe: Summary

## Origin

This reframe emerged from a conversation on 2026-03-13 comparing:

1. **The existing kernel** — a bottom-up Clojure implementation of
   Mueller's DAYDREAMER (~40% faithful, 5,815 lines, 85 tests passing),
   with three working benchmarks and benchmark-specific adapters.

2. **A ChatGPT (IMS+Daydream) deep-dive** — a clean-room architecture
   derived from reading Mueller's original paper, proposing a
   "pressure-driven hypothetical expansion policy" with explicit
   Concern/Context/SceneNode/CommitRecord schema.

3. **The 5 Pro architecture review** — an honest assessment that the
   architecture holds but the art question ("does the watched thing
   feel like a mind doing something?") remains unproven.

The reframe synthesizes all three into a new architectural direction.

---

## The Core Reframe

### What we were building

"Mueller's DAYDREAMER, simplified for conducted visual dreaming."

Mueller's specific families (REVERSAL, ROVING, RATIONALIZATION) as the
primitive taxonomy. A pre-authored dream graph traversed by those
families. The kernel as a cognitive sidecar producing Mueller-style
traces.

### What we should be building

**A pressure-driven exploration engine that uses Mueller's control
geometry but not his label set.**

The primitive is **pressure** — unresolved concerns that demand
exploration. The families are **pluggable operators**, not a fixed
taxonomy. Mueller proved the loop works. Our contribution is the
operators and the domains they explore.

### The exploratory loop Mueller actually proves

```
Pressure → Operator → Fork → Evaluate → Feedback
```

1. Something unresolved exists (a pressure with intensity, urgency,
   valence)
2. That pressure activates an operator (a typed exploration move)
3. The operator forks a hypothetical context with an explicit
   assumption patch ("what if X?")
4. Generation/evaluation happens inside that fork
5. Results feed back — not as world facts, but as policy, salience,
   and readiness changes
6. Post-hoc scan: did this accidentally resolve some other pressure?
   (serendipity)

This loop shape is Mueller's. It applies directly to the exploratory
lane (L1/L2). Everything else is pluggable.

L3 only partially inherits this shape:

```
TraversalPressure → Controller Verb → Score Options → Select → Update State
```

That is related control geometry, not the same context-forking machine.
Trying to force L3 into the same `Context` / `assumption_patch` /
`Proposal` mold creates category errors.

### What "shared control geometry" means and does not mean

This is **shared control geometry across two related lanes** — not one
engine running identically everywhere.

Different levels have:
- Different state spaces (possible worlds vs. character states vs.
  authored sequences)
- Different pressure types (artifact deficiencies vs. psychological
  concerns vs. dramaturgical pressures)
- Different operators (world-building moves vs. cognitive strategies
  vs. directing moves)
- Different evaluation criteria (world richness vs. emotional
  resolution vs. dramatic arc)

The shared elements are:
- Pressure scheduling (detect what's unresolved, prioritize)
- Episodic memory (remember what was tried, retrieve by coincidence)
- Trace/export and provenance discipline
- Evaluation/feedback loops

Shared only across the exploratory lane (L1/L2):
- Context forking (hypothetical exploration with named assumptions)
- Serendipity detection (post-hoc scan for multi-pressure resolution)
- Backtracking (abandon flat branches, try different operators)
- Proposal generation and commit discipline

Traversal-lane specific (L3):
- Scoring and selecting existing material
- Visit-history and pacing state
- Traversal-bias updates instead of exploratory commits

The claim is **not** "this is one engine." The claim is: the two lanes
share control geometry and some infrastructure, but only L1/L2 use the
full exploratory fork/evaluate machinery.

---

## Three Levels, Different Maturity

The shared pattern applies at three levels. These are at very
different stages of confidence.

- **Level 2 (character inner life)** — proven. The kernel implements
  this. 85 tests pass. Three benchmarks demonstrate it. This is
  Mueller's native domain.
- **Level 3 (director traversal)** — plausible but structurally
  different. Mostly selection/scheduling over existing material, not
  generative exploration. May share pressure/evaluation language
  without being the same kind of engine. Open question.
- **Level 1 (world-building)** — research direction. Exciting but
  unproven. Requires computable pressures, defensible operators, and
  evaluable proposals. See `06-world-building-pressure-questions-
  for-5pro.md` for the focused question pack on this.

### Level 1: World-Building / Ideation (Research Direction)

**Status:** Unproven. The pressures, operators, and evaluation
criteria are provisional. This level is a research hypothesis, not
settled architecture.

**When:** Pre-production. Before there's a graph or a script.

**State space:** Possible worlds — settings, characters, backstories,
relationships, latent conflicts.

**Input:** Compressed initial state — a setting sketch, 3-4 characters
with desires and backstories, a few seed tensions. Maybe a page total.

**Concerns** (creative possibilities):
- Unexplored implications of a character's desire
- Contradictions between characters that haven't been surfaced
- Gaps in the world (what's missing that would ground this?)
- Tonal imbalances (too dark, too safe, too uniform)

**Operators** (ideation moves):
- `complicate` — add a constraint or obstacle to an existing desire
- `ground` — make an abstract tension concrete and situated
- `connect` — find a relationship between two elements that weren't
  linked
- `contrast` — introduce something that throws existing material into
  relief
- `historicize` — give something a backstory that creates latent
  pressure

**Output:** A richer world with motivated characters and latent
conflicts. Possibly: a set of situations with affiliated characters,
desires, and tensions.

**Human role:** Curator. Select what's interesting, discard what's flat,
steer the exploration toward the world you want to inhabit.

### Level 2: Character Inner Life (Proven)

**Status:** This is the kernel's native domain. Working code,
passing tests, three benchmarks.

**When:** After world-building. Characters exist with desires and
relationships.

**State space:** Possible actions, reactions, internal states for each
character given their concerns and the current world state.

**Input:** Character profile + active concerns + current world state +
triggering events.

**Concerns** (character motivations — closest to Mueller's original):
- Status damage, attachment threat, retaliation pressure
- Anticipatory next-encounter pressure
- Guilt, obligation, desire, curiosity
- Unresolved injuries, debts, promises

**Operators** (psychological moves — Mueller's families generalized):
- `revenge/reversal` — undo or retaliate against a harm
- `rehearse` — practice an anticipated encounter
- `rationalize` — reframe a failure to reduce pressure
- `recover` — find a non-retaliatory repair path
- `avoid` — defer confrontation (creates mounting pressure)
- `rove` — seek relief in unrelated pleasant material
- `confront` — face what's being avoided (high risk, high payoff)

**Output:** Motivated behavior — what characters want to do, fear,
avoid, rehearse. Characters generate events because they *want things*,
not because the author scripted "and then B does X."

**Human role:** Curator. Review character actions for plausibility and
dramatic interest. Veto implausible moves. Encourage surprising ones.

### Level 3: Director Traversal (Open Question)

**Status:** Plausible but structurally different from L1/L2. This
level is primarily selection/scheduling over existing material, not
generative hypothetical exploration. It may share pressure vocabulary
and evaluation language with L1/L2 without being the same kind of
engine.

The key distinction: L1 and L2 are **generative** — they explore
possibility spaces and produce new material. L3 is **selective** —
it traverses existing material and chooses what to show. A fork at
L1 ("what if this character had a different backstory?") creates new
state. A fork at L3 ("what if we showed this node next?") evaluates
an existing option. That's the difference between a playwright and
a film editor.

The 5 Pro architectural review (see `first-round/03-questions.md`)
resolved this: **L3 is not the same engine as L1/L2.** It is a
traversal controller — related, sharing some infrastructure
(episodic retrieval, pressure vocabulary, trace/export), but not
context-forking cognition. If you have to invent fake
`assumption_patch` values to keep the abstraction uniform, the
abstraction has already failed.

**When:** Performance time (live or batch). Authored material exists.
Characters have generated events and conflicts.

**State space:** Possible sequences through the authored material —
which node next, given the emotional trajectory so far.

**Input:** Dream graph + current emotional state + dramatic history
(what's been shown, what's been avoided, what's been earned).

**Pressures** (dramaturgical):
- Tension deficit — things have been too comfortable
- Pacing stall — we've lingered too long in one situation
- Emotional debt — a climax hasn't been earned yet
- Avoidance — something important is being sidestepped
- Resonance opportunity — two situations could collide meaningfully
- Unearned climax — intensity without buildup

**Operators** (directing moves — provisional, not grounded in a
studied dramaturgical theory the way Mueller's families are grounded
in cognitive science):
- `build` — increase pressure toward an unresolved tension
- `release` — give the audience relief after sustained intensity
- `confront` — force the sequence toward what's been avoided
- `shift` — move to a different situation (current one exhausted)
- `recall` — bring back earlier material (it means something
  different now)
- `juxtapose` — place two things next to each other for collision
- `dwell` — stay with a moment longer than expected (let it breathe)

**Output:** Traversal path — which node, which family coloring, how
long, what transition.

**Human role (batch):** Review the sequence. Does it feel like
something? Does the director's logic track?

**Human role (live):** The APC Mini performer is a parallel controller,
steering pacing, tension, emphasis, and overrides in real time. The
live conductor should not be described as "just another instance of the
same engine" until that arbitration model is actually proven.

---

## Two Lanes, Not One Engine

The 5 Pro architectural review confirmed: this is not one engine
at three levels. It is **two related architectures with shared
control geometry**, organized as two lanes.

```
RESEARCH / AUTHORING LANE
=========================

Source Bible / World Sketch
        |
        v
L1 Creative Pressure Engine
(world gaps, contradictions, tonal imbalance)
        |
        v
L2 Character Pressure Engine
(injury, desire, avoidance, rehearsal, retaliation)
        |
        v
Candidate scenes / events / variants
        |
        v
Human curation + authoring
        |
        v
Authored Graph


SHIPPING / PERFORMANCE LANE
============================

Authored Graph
   + traversal state
   + visit history
   + Director feedback
        |
        v
Traversal Controller
(selection / recall / shift / dwell / release)
        |
        v
Adapter / Projection Layer
(graph + state -> normalized playback packet)
        |
        v
Normalized Playback Packet
        |
        +--> Renderer
        +--> Narration
        +--> Stage / Music / Control


SHARED INFRASTRUCTURE
=====================

- episodic retrieval
- validation
- trace / export
- pressure vocabulary (shared base fields)
- world-state services
```

### What each lane owns

**Research/authoring lane:** L1 discovers world material worth
having. L2 generates motivated scene/event candidates. Human
curates. Graph is the crystallized output.

**Shipping/performance lane:** Traversal controller selects from
the authored graph. Adapter projects engine state into the
normalized playback packet. Renderer, narration, and stage consume
the packet.

### Where the graph sits

The graph sits **between the lanes**. It is the artifact of
authoring (hand-authored or engine-assisted) that performance-time
traversal operates on.

**Current state:** The graph is hand-authored. That is the shipping
path (Graffito v0).

**Research direction:** The graph could become partially engine-
generated during authoring — candidates proposed by L1/L2, curated
by a human, frozen for performance. This is unproven and should not
displace hand-authoring until it demonstrates clear value.

### Where operators live

Operators exist at all levels but are **not the same class**:

- **L1/L2 operators** are exploratory and branch-generating
  (fork context, generate hypotheticals, evaluate, commit)
- **L3 operators** are controller verbs over traversal state
  (select, recall, shift, dwell, release)

Same word, different category family. Sibling interfaces, not one
interface with wishful type erasure.

### Where adapters live

Adapters live **between** engine semantics and graph/stage
semantics. They are translators, not exploratory operators. The
code already separates them (`puppet_knows_adapter.clj` etc.).
The docs should stop pretending otherwise.

### Where the performer sits

The performer steers pressure, pacing, situation emphasis, music,
and maybe family override — but not low-level scene semantics.
That matches the conducted model: steer the traversal, do not
rewrite the graph live.

---

## What Changes from the Current Architecture

### What stays

- **The game engine model** (doc 17): pre-authored graph + intelligent
  traversal + one-shot renderer. This is still right for performance
  time.
- **The playback contract** (doc 21): normalized cycle packets consumed
  by renderer and narration. Still the right seam.
- **The kernel's context mechanism**: sprouting, pseudo-sprouts, fact
  visibility, backtracking. This is the fork/evaluate machinery and
  it's sound.
- **The kernel's goal competition**: strength-based selection, decay,
  mode oscillation. This is the pressure scheduling and it works.
- **Episodic memory with coincidence retrieval**: still the right
  retrieval model.
- **The Director feedback loop**: still the mechanism against closure
  over the authored graph.
- **Batch-first approach**: still the right path to live performance.
- **Graffito as first testbed**: still the right first collaborator
  slice.

### What stays (addition)

- **The companion/inner-life view is compelling on its own.** The
  narration companion showing what's happening inside characters is
  not just a debug tool — it may be one of the actual products of
  the system. The pressure toward "make it immediately watchable as
  video" should not erase this. Even before rendered visuals are
  compelling, the inner-life view of motivated characters with
  concerns and goals is interesting to watch.

### What changes

- **Mueller's families are no longer the primitive taxonomy.** They're
  one possible operator set (useful at level 2). The system supports
  pluggable operators at each level.
- **Pressure replaces emotion as the driving abstraction.** Three
  level-specific types (CreativePressure, CharacterConcern,
  TraversalPressure) with shared base fields (intensity, urgency,
  valence, unresolved). "Concern" is reserved for character-level use.
- **The CommitRecord formalizes commit semantics, but with level-
  specific enums.** L1/L2: `ontic | policy | salience | none`.
  L3: `canon_event | traversal_bias | activation_shift | none`.
- **The assumption_patch on contexts becomes mandatory** (L1/L2
  only — L3 does not use context forking).
- **The kernel's role expands on the research track.** Not just a
  performance-time sidecar. Also an authoring-time exploration engine
  (levels 1 and 2). But this is research, not the shipping path.
- **Operators and adapters remain separate.** Adapters translate
  between engine semantics and graph/stage semantics. They are not
  operators. The code already enforces this separation.
- **L3 gets an optional `traversal_intent` field** in trace/debug
  first, not in the playback contract. Experiment there before
  changing the runtime seam.

---

## Relationship to Mueller

Mueller proved the loop works for motivated exploration of hypothetical
state spaces. That is the thing worth stealing.

Mueller's specific families (REVERSAL, ROVING, RATIONALIZATION,
RECOVERY, REHEARSAL, REVENGE, REPERCUSSIONS) are one operator set
for one domain (psychological daydreaming). We generalize:

| Mueller | Our system |
|---------|-----------|
| Emotion triggers goal | Pressure activates operator |
| Goal type (family) | Operator (pluggable, level-specific) |
| Sprouted context | Forked hypothetical with assumption_patch (L1/L2) |
| Forward-chaining rules | LLM generation behind typed operator boundary |
| Episode storage/retrieval | Episode storage/retrieval (kept, shared) |
| Serendipity via rule intersection | Serendipity via post-hoc pressure scan |
| Mode oscillation | Mode oscillation (kept, generalized) |
| Single cognitive loop | Two lanes: exploration engine (L1/L2) + traversal controller (L3) |

We keep the control geometry for L1/L2. We adapt it (pressure
vocabulary, episodic retrieval) for L3 without forcing context-
forking onto traversal. We replace Mueller's content machinery with
LLMs behind typed operators.

---

## Review Status

### Completed reviews

- `first-round/03-questions.md` — 5 Pro architectural review.
  Verdict: "good upstream, wrong downstream." Confirmed two-lane
  split, named seven category errors, proposed falsifiable L3
  experiment.

### Pending reviews

- `06-world-building-pressure-questions-for-5pro.md` — is L1
  world-building actually pressure-driven in a computable way?
- `07-l3-traversal-questions-for-5pro.md` — what is L3 actually,
  and what should it share with L1/L2?
- `08-prior-work-questions-for-5pro.md` — what existing research
  should ground each level?

### Supporting docs

- `04-kernel-gap-analysis.md` — kernel module mapping (needs update
  to remove adapter→operator conflation)
- `05-stage-integration.md` — concrete stage integration
  architecture
