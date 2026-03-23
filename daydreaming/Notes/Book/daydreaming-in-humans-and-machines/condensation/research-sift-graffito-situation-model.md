# Sift: Prompt 20 — Graffito Situation Model

Three replies (5pro, 5think, 5think-02). High convergence on schema
and objects. The third reply sharpens WHERE regulation lives and HOW
it interacts with families — the most architecturally important
correction.

## 1. Schema revision — settled

The 7-category kernel brief (`24-graffito-kernel-brief.md`) is mostly
right but needs two additions. Both replies converge independently.

**Add: sensorimotor regulation facts (category 8)**

Tony's overwhelm → flow → creation arc cannot be represented without
dedicated body/regulation state. This is the most important gap in the
current schema. Without it, the "what is wrong with you is exactly
what's right with you" reversal collapses into generic threat
reduction.

**Add: cross-layer correspondence facts (category 9)**

Motherload ↔ Grandma, castle ↔ school, Can ↔ Tony's sensory capacity.
These need typed correspondence links (`:counterpart_of`,
`:avatar_of`, `:reframe_force_of`), not identity collapse and not
free analogy search. Authored correspondences first; free search later.

Revised 9-category schema:

1. Present actors
2. Relationship facts
3. Role / obligation facts
4. Exposure / surveillance facts
5. Artifact-state facts
6. Recent event facts
7. Derived appraisal summary
8. **Sensorimotor regulation facts** (new)
9. **Cross-layer correspondence facts** (new)

## 2. Regulation state machine — settled

Both replies say: do not model Tony with "arousal" as a scalar.
Model a qualitative state machine:

```
overloaded → bracing → entraining → flowing → creating
```

(5pro adds `co_regulating` and `depleted` as additional states)

Companion variables:

- `sensory_load` — raw intensity of input
- `precision_control` / `attentional_aperture` — ability to filter
- `rhythmic_entrainment` — body-rhythm alignment
- `felt_agency` — perceived control over action → effect

The causal chain (both replies converge):

```
movement → entrainment → precision control → felt agency → Can unlock
```

This IS the Graffito mechanism. Not metaphorical. The Can's activation
condition should be explicitly gated: only works when
`regulation_mode ∈ {entraining, flowing, creating}`.

### Correction (5think-02): regulation lives at character level, not situation level

The first two replies designed the state machine but were ambiguous
about where it lives. The third reply is explicit and correct:

**Situation facts provide inputs** (crowd density, noise/sensory
intensity, rhythmic affordances, co-regulator presence, can-state,
sketchbook availability). These are properties of the scene.

**Character state carries the processing** (`sensory_load`,
`entrainment`, `felt_agency`, `perceived_control`). These are
properties of Tony's current organism state, updated from situation
facts but persisting across situation transitions.

**The phase label is derived**, not stored as the primitive. Store
the continuous variables; derive `regulation_mode` from them. "Bracing"
and "entraining" are useful human labels but not stable computational
primitives.

This matters because: Tony's entrainment from the apartment scene
carries into the mural crisis. If regulation were situation-level
metadata, it would reset on situation transition.

### Additional detail (5pro-02): co-regulator set and indirection

**Co-regulator as explicit field.** Character state should carry
`co-regulator #{:Monk :Grandma}` — who is currently available as a
regulation partner. Tony's regulation mode depends on WHO is present.
Monk enables entrainment through embodied teaching. Grandma enables
grounding through boundary and challenge. This should be a set on
character state, not implicit in situation facts.

**`expressive-channel-open?`** as a boolean companion to regulation
mode. The Can doesn't just need Tony to be entraining; the expressive
channel must be open (a function of entrainment + agency + co-regulator
context).

**The indirection principle.** Regulation should NOT become a second
global chooser competing with Mueller's emotion-driven control loop.
Instead: regulation modulates concern intensity and coping bias →
those modulate family candidate strength → family selection stays
emotion-driven. This preserves the architecture instead of smearing
it. (5pro-02, explicit: "Do not let `:regulation/mode` become a
second global chooser that competes with emotional motivation.")

### For autonomous selection: the family strength bias term (5pro prompt-21)

The deterministic slices hand-pick which family runs. For the
autonomous miniworld, regulation needs to affect family competition
through appraisal. Concrete formula:

```
family-strength = trigger-emotion-strength
                + appraisal-bias(family, tony-state, situation-facts)
```

This is how rising entrainment/agency tips the balance from reversal
toward rationalization/rehearsal without bypassing the emotion-driven
architecture. Without this term, autonomous selection can't produce
the reappraisal flip through dynamics — it would need scripted cycles.

Three companion rules for the miniworld:

1. **Don't zero out negative affect after support.** Challenge and
   threat coexist. The mural after support should read
   challenge-dominant, not fear-free. Preserve live negative emotion
   even after the appraisal flips.

2. **Regulation affects retrieval by changing active indices, not by
   lowering thresholds.** When entrainment rises, different cues
   become active (flow-related, practice-related), changing what
   episodes are retrieved. Don't make all memory looser.

3. **Add candidate-family-scores to the miniworld trace.** The
   current slices show which family was selected. The miniworld needs
   competing scores visible so we can see WHY one family won — the
   "parking lot laps" observability.

### Correction (5think-02, confirmed 5pro-02): no new family — add reappraisal as kernel process

The Graffito arc is NOT best modeled as a new "regulation" family.
It is RATIONALIZATION (meaning shift) + REHEARSAL (control shift) →
reappraisal.

The missing piece is a **cross-cutting kernel process**:

1. Situation facts update regulation variables in character state
2. Appraisal reads situation facts + character regulation state
3. Appraisal produces emotion deltas and coping/control assessment
4. Family activates (driven by those emotions)
5. Family outcome modifies appraisal-relevant character state:
   - RATIONALIZATION changes meaning, attribution, self-compatibility
   - REHEARSAL changes coping potential, felt agency, perceived control
6. **Kernel re-runs appraisal on the SAME situation**
7. Threat may become challenge, shame may become pride/curiosity

This is the right model for "what is wrong with you is exactly what's
right with you." Same stimulus, different appraisal, different family
selection — driven by what the earlier family execution changed in
character state.

Design rule: do NOT hard-dispatch families from regulation mode
(`if overloaded → activate ROVING`). Let regulation affect family
selection through appraisal, not by bypassing it.

**Warning (5pro-02): do not abuse RATIONALIZATION.** If the
reappraisal layer is skipped, RATIONALIZATION becomes the dumping
ground for semantic reframe, bodily entrainment, control acquisition,
AND moment-to-moment reinterpretation — four distinct moves under one
label. The clean decomposition is:

- RATIONALIZATION = reinterpret meaning
- REHEARSAL = build embodied control
- REVERSAL = explore alternative avoided outcomes
- REAPPRAISAL mechanism = recompute appraisal after those changes

## 3. Reappraisal model — settled

Both replies agree: the "liability becomes asset" reversal is NOT a
new stimulus. It is the **same sensory intensity** with changed
appraisal fields:

- Sensory load stays high
- Felt agency rises
- Coping potential rises
- Self-compatibility flips (defect → gift)
- Challenge replaces threat

Design rule: **do not encode "overload" and "power" as separate
traits.** Encode one raw sensory condition plus changing appraisal
fields. The kernel should make the flip visible in the trace.

Success criterion for first Graffito run: same overload cues produce
threat in street context and challenge/agency in mural context after
co-regulation. If the kernel can't make that flip legible, it's not
modeling Graffito. (5pro, explicit)

## 4. Layered reality — settled

Both agree at ~90%:

- Motherload and Grandma: **separate entities with typed
  correspondence**. Not same ID.
- Castle and school: same treatment.
- **No third cognitive layer needed.** Mueller's two-layer split is
  enough. Add `reality_register` (baseline / myth / magic) as
  metadata on situations.
- Myth layer is a **legend schema** in v1, not a fully simulated
  third world.
- **Context sprouting ≠ register correspondence.** Keep orthogonal.
  Sprouting is for hypothetical branches (reversal, rehearsal).
  Register correspondence is a structural mapping across layers.

## 5. Objects as stateful cognitive couplers — settled

Both replies agree: add **person-object relations** as first-class
structure with three dimensions:

**Capability state** — what the object can do now:
- Can: inert → unstable → world-making
- Mural: surface → latent portal → open portal
- Elephant: comfort object → companion → guide
- Sketchbook: always active (2D compression / regulation)

**Relational state** — how a character stands with it:
- trust, inheritance, dependence, mastery, fear, identification

**Symbolic-anchor state** — what internal process it stabilizes:
- Can = embodied agency / sensory transformation
- Sketchbook = dimensionality reduction / self-soothing
- Elephant = co-regulation / safety / continuity of self
- Mural = boundary-crossing surface

Design note (5pro): do not give the Can "agency" in the ontology.
Agency comes from the Tony+Can coupling. Elephant is the exception
(transitions from object to counterpart agent across registers).

## 6. Emotion dimensions — settled revision

Both replies agree: the current set (threat, hope, anger, grief,
waiting) is too narrow and slightly wrong.

**`waiting` is not an emotion.** Demote to imminence / scheduler
variable. Keep it in the scheduler, not the appraisal vector.

**Add for Graffito:**
- shame / reproach (Grandma → Monk, Tony → self)
- admiration / trust (Tony → Monk)
- pride / remorse (Monk ↔ Grandma)
- wonder / interest (Tony ↔ Can, Tony ↔ magic world)

Graffito is heavy on **moral attribution** (OCC agent-evaluation
emotions), not just threat/hope. The Tony/Monk/Grandma triangle is
driven by admiration, reproach, shame, and pride more than by fear.

## 7. First Graffito run scope — settled

**3-4 situations**, not 7:
- Street overload
- Apartment co-regulation + Grandma's challenge
- Mural crisis
- (optionally) Motherload reframe

**15-25 typed facts per situation**, broken down:
- 3-5 actor-presence / observation
- 4-6 relation / obligation
- 2-4 exposure / surveillance
- 3-5 artifact + person-object
- 2-4 recent event
- 3-4 body / regulation
- 2-3 cross-layer correspondence

**Include rehearsal** in the first run. Graffito's mechanism is
learned embodied control. Mime-spraying, practiced routine, and
body-rhythm alignment matter.

**What "psychologically interesting" output looks like:**
- Rehearsal: Tony practices alignment with Monk's movement, changing
  readiness/agency even without external events
- Reversal: Tony imagines the near-miss going differently, with
  changed agency or earlier flow
- Rationalization: the system reframes crookedness from defect to
  power, or Monk's mission from ego to calling, and tests whether
  that actually lowers pressure
- Roving: fantasy material appears but preserves unresolved real
  structure (Motherload world carries Grandma/Monk/Tony tensions)

## 8. What to add to the shipped microfixture — revised after 5think-02

Codex already landed apartment + mural with typed rationalization /
reversal facts. The 5think-02 correction changes what comes next.

The next slice is NOT "more fact categories." It is:
**character state + reappraisal pass, benchmarked on the same
situation before and after a family execution.**

### Immediate next slice (codex target)

1. **Add minimal Tony character state** to the world map:
   `sensory_load`, `entrainment`, `felt_agency`, `perceived_control`.
   These persist across cycles and situation transitions.
2. **Derive `regulation_mode`** from the continuous vars (for
   debugging and coarse gating, not as the primitive).
3. **Let one family outcome update character state** — e.g.,
   REHEARSAL in the apartment raises `entrainment` and `felt_agency`.
4. **Rerun appraisal on the same mural situation** and prove the
   shift: same sensory facts, but with higher `felt_agency` the
   appraisal now produces challenge instead of threat, changing
   family selection.

That is the minimum test of "same cue, different appraisal, different
behavior." If it passes, the typed fact-space plus character state
plus reappraisal pass are buying something the pressure numbers
couldn't.

### After that slice passes

5. **Can phase state** — inert / unstable / world-making, gated on
   `regulation_mode ∈ {entraining, flowing, creating}`
6. **Person-object relations** — Tony ↔ Can (trust, lineage,
   activation condition), Tony ↔ Sketchbook (regulation dependence)
7. **Cross-layer correspondence stubs** — typed links (Grandma ↔
   Motherload counterpart-of)
8. **Street situation** — needed to test the full arc (overload →
   co-regulation → crisis → agency)

## 8.5. Transient regulation vs accumulated capability — the membrane bridge

(From 5think reply to prompt 21. Architecturally significant — ties
the membrane work to the Graffito regulation work.)

**Transient regulation** decays over cycles:
- sensory_load, entrainment, felt_agency, perceived_control
- These live in character state and shift within a session
- One rehearsal cycle produces a momentary state improvement

**Accumulated capability** should NOT be sticky Tony-state floats.
It should go through the **existing membrane**:

1. Rehearsal or support episodes get stored as `:provisional`
2. Repeated successful cross-family reuse promotes them to `:durable`
3. Promotion opens frontier rules or lowers retrieval thresholds
4. Only later does accumulated capability crystallize into explicit
   learned skill facts

This means the membrane (Assays A and B) isn't separate infrastructure
from Graffito's developmental arc. It IS the mechanism by which
transient regulation improvements become durable capabilities:

```
rehearsal changes Tony state (transient)
  → episode stored
  → later reuse across situations/families
  → promotion through membrane
  → frontier rule opens (durable)
  → Tony can now plan things he couldn't before
```

Design rule: do NOT use one variable for both transient regulation
and learned capability. The right place for accumulation is the
episodic memory promotion chain, not a vague "agency trait" on
character state.

### When to rerun appraisal

After effect application and Tony-state update, once branch-local
consequences settle, but BEFORE the next family activation pass.
Not after episode storage — otherwise you blur immediate regulation
(state change) with later memory reuse (retrieval of stored episode).

```
family executes
  → branch facts asserted / Tony state updated
  → reappraise live situation      ← HERE
  → then store / reconcile episode / promotion
```

### Rehearsal implementation (first pass)

Do not build a full rehearsal planner yet. Build one authored
regulation routine:

```
{:fact/type :regulation-routine
 :fact/id :monk_pata_pata_routine
 :preconditions #{:monk_present :rhythm_available}
 :state-delta {:entrainment +0.4
               :felt-agency +0.3
               :perceived-control +0.25}}
```

Let a minimal rehearsal executor assert the routine, apply the
Tony-state delta, and store an episode with overlap to later mural
cues. Enough to prove rehearsal-as-regulation without a big search
space.

### Object state vs facts (settled)

Use **dedicated object maps** for phaseful objects:

```
:objects
{:can   {:phase :inert | :unstable | :world-making
         :requires-mode #{:entraining :flowing :creating}}
 :mural {:phase :surface | :threshold | :portal}}
```

Use **typed facts** for relations and meanings:
- "Tony trusts the Can as lineage" → relation fact
- "Can is currently :unstable" → object state map

For the next slice, only Can and Mural need phase logic.

## 9. Key outside references to keep

| Reference | What it gives us |
|---|---|
| Scherer CPM | Appraisal structure: relevance, implications, coping, norms |
| Lazarus/Smith | Challenge vs threat appraisal, coping potential, reappraisal |
| OCC | Agent-evaluation emotions (shame, reproach, admiration, pride) |
| MicroPsi/PSI | Resolution level + selection threshold as cognitive modulators |
| Gentner SME | Structure-mapping for cross-layer correspondence |
| Fauconnier/Turner | Conceptual blending for emergent mixed scenes |
| Active inference / Seth | Regulation as descending prediction + action policy |
| Hutchins / distributed cognition | Objects as material anchors for cognitive transformation |
| 2022 computational flow theory | Flow as mutual information between means and ends |
| RAMSR preschool RCT (n=213) | Rhythm-and-movement improves self-regulation (p<0.001) |

## 10. Future engine connections (from web chat sift, 2026-03-23)

### Regulation-modulated serendipity threshold

When serendipity is built, Tony's regulation state should modulate
the serendipity threshold:

- `overloaded` → tight threshold (only forced connections, emergency
  coping). Mueller's coercive loop territory.
- `flowing` / `creating` → loose threshold (browsing attention,
  wider associative reach). The "attractive" curiosity loop.

This is the principled bridge from the regulation state machine we
already have to the serendipity engine that hasn't been built yet.
Same variable, different ends of the spectrum produce different
cognitive modes. Serendipity's surprise emotion is the mechanism
that converts the attractive loop (low-pressure noticing) into the
coercive loop (can't stop thinking about it).

### REPERCUSSIONS as future family priority

Mueller's most underspecified family is also his most general:
hypothesis-driven simulation ("what would happen if X?"), activated
by heuristics he never specified, the only family not triggered by
emotions. Natural home for intellectual exploration. Not the next
implementation seam, but the next family to develop after rehearsal
is more real.

### Engine gap > family gap

The self-correction from the web chat is worth preserving: the
missing thing for broader cognitive modes (intellectual exploration,
vault brainstorming, curiosity-driven musing) is mostly engine
(serendipity, mutation, analogical planning), not new families.
Mueller's existing families are broader than they look if the goal
vocabulary is broader. REHEARSAL is already intellectual exploration
if "achieve understanding of X" is a valid concern.

## 11. Parked

- Full 7-situation Graffito scaffold — wait until 3-4 situations work
- GOLEM ontology for archival — overkill for runtime v1
- Free analogy search across registers — authored correspondences
  first
- Myth layer as fully simulated world — legend schema is enough for v1
- Facade-style beat gating — relevant but heavy authoring cost
