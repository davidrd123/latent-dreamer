# Authoring-Time Generation: Deep Explainer

How the prototype turns a character description into graph-ready
candidate moments, step by step.

Source: `daydreaming/authoring_time_generation_prototype.py`
Fixture: `daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml`

---

## The Big Picture

```
                    THE TWO ARMS
                    ============

    ARM A (baseline)              ARM B (middle layer)
    ================              ====================

    Character seed                Character seed
         |                             |
         |                     +----- MIDDLE LAYER -----+
         |                     |                        |
         |                     | 1. Concern Extraction  |
         |                     | 2. CausalSlice         |
         |                     | 3. Appraisal           |
         |                     | 4. EmotionVector       |
         |                     | 5. PracticeContext     |
         |                     | 6. Episodic Retrieval  |
         |                     | 7. Operator Scoring    |
         |                     |                        |
         |                     +------------------------+
         |                             |
         v                             v
    "Write a moment"          "Write a moment shaped by
     about this                these specific pressures,
     character"                this appraisal, these
                               memories, this operator"
         |                             |
         v                             v
    +----------+              +----------+
    |   LLM    |              |   LLM    |
    +----------+              +----------+
         |                             |
         v                             v
    Generic scene              Specific, grounded scene
    about a guy                born from pressure
    and a letter               and social context
         |                             |
         v                             v
    +-----------------+       +-----------------+
    | Graph Compile   |       | Graph Compile   |
    | + Validate      |       | + Validate      |
    +-----------------+       +-----------------+
         |                             |
         v                             v
    Graph node                 Graph node
    (if it passes)             + Provenance Sidecar
```

The question the prototype answers: **does the middle layer
produce better candidates than going straight to the LLM?**

---

## What "Primitives" Actually Are

Primitives are not raw text. They are compressed authorial
observations — the smallest facts about a character's situation
that an author would recognize as "charged."

```
                PRIMITIVE FACTS (Kai)
                =====================

   +----------------------------------+
   | incoming_contact_from_           |  Someone important
   |   estranged_target               |  reached out
   +----------------------------------+
   | prior_rupture_unresolved         |  The relationship
   |                                  |  is already damaged
   +----------------------------------+
   | contact_requests_action          |  A response is
   |                                  |  required
   +----------------------------------+
   | deadline_is_near                 |  Time pressure
   |                                  |  exists
   +----------------------------------+
   | known_avoidance_history          |  This character
   |                                  |  avoids things
   +----------------------------------+
   | same_target_reappears            |  The same charged
   |                                  |  person is back
   +----------------------------------+

These are NOT:
  - raw language tokens
  - full symbolic world state
  - a finished interpretation
  - scene descriptions

They ARE:
  - enough authored structure to support
    interpretation by theme rules
```

---

## Step 1: Concern Extraction

### What it does

Turns primitive facts into typed pressures through theme rules.

```
         THEME RULES
         ===========

  Rule 1:
  IF  incoming_contact_from_estranged_target
  AND prior_rupture_unresolved
  ────────────────────────────────
  THEN  concern_type: attachment_threat
        target_ref: sister

  Rule 2:
  IF  contact_requests_action
  AND deadline_is_near
  ────────────────────────────────
  THEN  concern_type: obligation
        target_ref: sister
```

### What the output means

```
    CONCERN: cc_attachment_threat
    =============================
    concern_type:     attachment_threat
    target_ref:       sister
    base_intensity:   0.73
    source_rule_ids:  [rule_attachment]
    source_episodes:  [ep_last_rupture,
                       ep_avoidance_consequence]
    unresolved:       true

    ┌─────────────────────────────────────┐
    │ This is NOT "Kai feels anxious"     │
    │                                     │
    │ This IS "the sister relationship    │
    │ is live as a danger/opportunity,    │
    │ repair is possible therefore        │
    │ threatening, the current moment     │
    │ should be shaped by that pressure"  │
    └─────────────────────────────────────┘
```

### Practice bias rules (separate from concerns)

```
         PRACTICE BIAS RULES
         ====================

  IF  known_avoidance_history
  AND same_target_reappears
  ────────────────────────────────
  THEN  practice_bias: evasion
        family_bonus: avoidance

  ┌─────────────────────────────────────┐
  │ This is NOT another concern.        │
  │                                     │
  │ This is a social-behavioral         │
  │ tendency: given this kind of        │
  │ pressure, how does THIS character   │
  │ tend to handle it?                  │
  │                                     │
  │ Concern = what the problem IS       │
  │ Practice = how the character        │
  │            HANDLES that kind of     │
  │            problem                  │
  └─────────────────────────────────────┘
```

---

## Step 2: CausalSlice

### What it does

Takes the dominant concern and interprets the situation
structurally. This is the first real semantic jump.

```
           BEFORE CausalSlice          AFTER CausalSlice
           ==================          =================

    "Kai has attachment                "Kai's goal of preserving
     pressure about                    possible repair with his
     his sister"                       sister is THREATENED.

                                       He thinks his OWN ACTION
                                       matters (attribution).

                                       The danger is AHEAD, not
                                       completed (prospective).

                                       He has a small action menu:
                                         - open the letter
                                         - delay contact

                                       His sister could also
                                       withdraw, making it worse."
```

### The data structure

```
    CausalSliceV1
    =============

    focal_situation ──► sit_unopened_letter
                        │
    concern ───────────►│ cc_attachment_threat
                        │
    target ────────────►│ sister
                        │
    affected_goal ─────►│ preserve-possible-repair
         sign ─────────►│   THREATENS (weight: 0.85)
                        │
    attribution ───────►│ actor: kai
                        │   intentional: true
                        │
    temporal ──────────►│ PROSPECTIVE (not yet happened)
                        │
    likelihood ────────►│ HIGH
                        │
    self_options ──────►│ [open-letter, delay-contact]
    other_options ─────►│ [sister-withdraws]
```

### Why this matters

Without CausalSlice, the system only knows "Kai has attachment
pressure." With it, the system knows:

- What is threatened (possible repair)
- Who is responsible (Kai himself)
- What the options are (open or delay)
- What the timeline is (prospective — still time to act)

The ablation test proves this: remove CausalSlice, and the
scenario collapses to "Kai is anxious about the letter."
Operator ranking becomes much harder to justify.

---

## Step 3: Appraisal

### What it does

Reads the CausalSlice (not the raw situation) and evaluates it
on EMA-style dimensions.

```
                    APPRAISAL FRAME
                    ===============

    ──────────────────────────────────────────────
    desirability      -0.78  ████████░░  BAD
    ──────────────────────────────────────────────
    likelihood         0.82  ████████░░  PRESSING
    ──────────────────────────────────────────────
    controllability    0.28  ███░░░░░░░  LOW CONTROL
    ──────────────────────────────────────────────
    changeability      0.34  ███░░░░░░░  HARD TO ALTER
    ──────────────────────────────────────────────
    praiseworthiness  -0.52  █████░░░░░  SELF-BLAME
    ──────────────────────────────────────────────
    expectedness       0.76  ████████░░  NOT A SURPRISE
    ──────────────────────────────────────────────

    Human-readable summary:
    "Bad if faced directly. Likely to matter soon.
     Hard to control once engaged. Partly self-caused."
```

### How appraisal drives operator choice

```
                    CONTROLLABILITY
                    ===============

    HIGH control ──► action-directed operators
                       rehearsal ("practice the encounter")
                       confrontation ("face it directly")

    LOW control ───► emotion-directed operators
                       avoidance ("redirect away")
                       rationalization ("reframe it")
                       roving ("think about something else")

    ┌─────────────────────────────────────────────┐
    │ Kai's controllability = 0.28 (LOW)          │
    │                                             │
    │ This biases toward avoidance/rationalization │
    │ and AWAY from rehearsal/confrontation.       │
    │                                             │
    │ Maren's controllability = 0.58 (MEDIUM)     │
    │ → biases toward rehearsal                   │
    │                                             │
    │ That's why they end up on different          │
    │ operators from the same pipeline.           │
    └─────────────────────────────────────────────┘
```

### The ablation proof

```
    Ablation: high_controllability (0.68 instead of 0.28)
    Result:   operator flips from AVOIDANCE to REHEARSAL

    ┌─────────────────────────────────────────────┐
    │ This proves the appraisal frame is doing    │
    │ real work. Change one structural variable    │
    │ and the whole pipeline produces a different  │
    │ kind of moment.                             │
    └─────────────────────────────────────────────┘
```

---

## Step 4: EmotionVector (Derived View)

```
    Derived from AppraisalFrame
    ===========================

    fear:            0.62  ██████░░░░  ← likelihood + low control
    distress:        0.46  █████░░░░░  ← desirability + low control
    remorse:         0.39  ████░░░░░░  ← self-blame (praiseworthiness)
    disappointment:  0.18  ██░░░░░░░░  ← desirability alone

    ┌─────────────────────────────────────────────┐
    │ THIS IS A VIEW, NOT A CONTROL INPUT.        │
    │                                             │
    │ The engine does NOT choose because           │
    │ "fear = 0.62"                                │
    │                                             │
    │ The engine chooses because the situation     │
    │ is appraised as bad, likely, low-control,    │
    │ blame-tinged.                               │
    │                                             │
    │ Emotions are then a READABLE SUMMARY         │
    │ of that state — for the dashboard,           │
    │ for narration, for the audience.             │
    │                                             │
    │ Removing EmotionVector from the prompt        │
    │ should change NOTHING in v1.                │
    │ That is the intended result.                │
    └─────────────────────────────────────────────┘
```

---

## Step 5: PracticeContext

### What it means

Not "setting." Not "location." The social micro-situation — what
kind of interaction is happening, what role the character plays,
what moves are legible.

```
                THREE CHARACTERS, THREE PRACTICES
                =================================

    KAI                  MAREN                RHEA
    ===                  =====                ====

    practice:            practice:            practice:
    EVASION              ANTICIPATED-         ANTICIPATED-
                         CONFRONTATION        CONFRONTATION

    role:                role:                role:
    evader               approacher           approacher

    phase:               phase:               phase:
    precontact           precontact           precontact

    affordances:         affordances:         affordances:
    - delay-contact      - draft-opening-     - draft-opening-
    - ritual-              line                 line
      distraction        - brace-for-         - brace-for-
    - prepare-excuse       accusation           accusation


    ┌─────────────────────────────────────────────┐
    │ Practice gives the LLM an ACTION GRAMMAR.   │
    │                                             │
    │ Kai's good scene looks like BUSY            │
    │ NON-ENGAGEMENT (filling a kettle,           │
    │ turning the letter face down).              │
    │                                             │
    │ Maren's good scene looks like THRESHOLD     │
    │ REHEARSAL (practicing an opening line,       │
    │ imagining the first words).                 │
    │                                             │
    │ Without PracticeContext, the LLM can jump    │
    │ straight to confrontation without earning    │
    │ it. The practice constrains what's socially │
    │ available RIGHT NOW.                        │
    └─────────────────────────────────────────────┘
```

### The ablation proof

```
    Ablation: swap_practice_context
             (evasion → anticipated-confrontation)
    Result:   operator flips from AVOIDANCE to REHEARSAL

    Same concern. Same appraisal. Different practice.
    Different operator. Different scene.
```

---

## Step 6: Episodic Retrieval

### How it works

Mueller's coincidence-mark retrieval. Not embeddings. Exact
key matching over typed fields.

```
                RETRIEVAL KEYS
                ==============

    concern_type ──► attachment_threat
    target_ref ────► sister
    situation_id ──► sit_unopened_letter
    practice_type ─► evasion


                SCORING EPISODES
                ================

    ep_avoidance_consequence
    ├── concern_type: attachment_threat  ✓
    ├── target_ref: sister              ✓
    └── practice_type: evasion          ✓
    SCORE: 3  ←── RETRIEVED

    ep_last_rupture
    ├── concern_type: attachment_threat  ✓
    └── target_ref: sister              ✓
    SCORE: 2  ←── RETRIEVED

    ep_old_harbor_memory
    ├── concern_type: attachment_threat  ✓
    └── target_ref: sister              ✓
    SCORE: 2  ←── but filtered by stricter tie-break


    ┌─────────────────────────────────────────────┐
    │ Retrieved episodes are NOT "similar          │
    │ memories." They are memories that MATCH      │
    │ THE CURRENT PROBLEM GEOMETRY.               │
    │                                             │
    │ ep_avoidance_consequence scores highest      │
    │ because it shares the same KIND of pressure, │
    │ the same TARGET, and the same HANDLING       │
    │ PATTERN. It's not "similar vibes" — it's    │
    │ "same structural problem shape."            │
    └─────────────────────────────────────────────┘
```

### What retrieval adds to generation

The LLM now has concrete history:

- "Kai once ignored her call on an important night and learned
  too late that the silence itself had become the message"
- "They fought at the harbor six months ago. She said, 'if you
  are going to stay silent, do it honestly.'"

That's why the generated scene references the harbor and silence
as charged material — not because the prompt said "mention the
harbor," but because retrieved episodes carry that history.

---

## Step 7: Operator Scoring

### The formula

```
    score = 0.35 * pressure
          + 0.30 * appraisal_fit
          + 0.20 * practice_fit
          + 0.20 * episodic_resonance
          - 0.10 * repetition_penalty
```

### Kai's scores

```
              PRESSURE  APPRAISAL  PRACTICE  MEMORY  -REPEAT  TOTAL
              ========  =========  ========  ======  =======  =====

  REHEARSAL     0.72      0.51      0.35     0.48    -0.05    0.57
                 │         │         │        │
                 │         │         │        └─ some episodes match
                 │         │         └─ evasion doesn't love rehearsal
                 │         └─ low control weakens this
                 └─ pressure is strong for everything

  RATIONAL.     0.72      0.66      0.58     0.55    -0.08    0.68
                 │         │         │        │
                 │         │         │        └─ avoidance episodes support
                 │         │         └─ evasion likes rationalization
                 │         └─ self-blame + low control help
                 └─ same pressure

  AVOIDANCE     0.72      0.82      0.90     0.62    -0.12    0.80  ← WINS
                 │         │         │        │
                 │         │         │        └─ evasion episodes are strong
                 │         │         └─ evasion practice STRONGLY favors this
                 │         └─ low control + high likelihood = best fit
                 └─ same pressure


    ┌─────────────────────────────────────────────┐
    │ AVOIDANCE wins because:                     │
    │                                             │
    │ 1. Low controllability makes non-engagement │
    │    the appraised-best response              │
    │ 2. Evasion practice strongly supports it    │
    │ 3. Retrieved history confirms this is Kai's │
    │    habitual move                            │
    │ 4. Even with repetition penalty, it wins    │
    │                                             │
    │ This is NOT "the system was told to pick    │
    │ avoidance." The middle layer DERIVED it     │
    │ from the character's situation, appraisal,  │
    │ social context, and history.                │
    └─────────────────────────────────────────────┘
```

---

## Step 8: LLM Generation (Behind the Operator Boundary)

### What the LLM receives

```
    ┌─────────────────────────────────────────────┐
    │                LLM PROMPT                    │
    │                                             │
    │  Character: Kai (wants repair, fears loss   │
    │  of control, defaults to delay rituals)     │
    │                                             │
    │  Situation: unopened letter requesting       │
    │  harbor meeting tonight                     │
    │                                             │
    │  CausalSlice: possible repair threatened,   │
    │  self-attributed, prospective, low control  │
    │                                             │
    │  Appraisal: bad, likely, hard to manage     │
    │                                             │
    │  Practice: evasion / evader / precontact    │
    │  Affordances: delay, ritual-distraction     │
    │                                             │
    │  Operator: AVOIDANCE                        │
    │  (non-engagement as action, not absence)    │
    │                                             │
    │  Retrieved memories:                        │
    │  - "silence itself became the message"      │
    │  - "if you stay silent, do it honestly"     │
    │                                             │
    │  Generate: one graph-compilable moment      │
    └─────────────────────────────────────────────┘

    ┌─────────────────────────────────────────────┐
    │        WHAT THE LLM SUPPLIES                │
    │                                             │
    │  - Surface realization                      │
    │  - Scene texture                            │
    │  - Concrete action details                  │
    │                                             │
    │        WHAT THE MIDDLE LAYER SUPPLIES       │
    │                                             │
    │  - WHY this moment exists                   │
    │  - WHAT KIND of moment it should be         │
    │  - WHAT it is allowed to do structurally    │
    └─────────────────────────────────────────────┘
```

### The generated output

> "Kai lifts the envelope, reads only his sister's name, turns
> it face down again, and fills the kettle as if the water can
> decide whether the harbor still belongs to them."

```
    ┌─────────────────────────────────────────────┐
    │ Why this is better than flat prompting:      │
    │                                             │
    │ ✓ Contains the delay ritual                  │
    │   (filling the kettle = ritual-distraction)  │
    │                                             │
    │ ✓ Shows non-engagement as ACTION             │
    │   (turns it face down = active avoidance)    │
    │                                             │
    │ ✓ Specific to Kai's history                  │
    │   (harbor reference from retrieved episode)  │
    │                                             │
    │ ✓ Psychologically legible                    │
    │   (the avoidance is visible and motivated)   │
    │                                             │
    │ ✓ Not generic                                │
    │   (not "Kai felt anxious about the letter")  │
    └─────────────────────────────────────────────┘
```

---

## Step 9: Graph Compilation

### What the output must include

The generated moment is only useful if it compiles to the
frozen graph seam contract.

```
    GRAPH NODE                    WHAT IT MEANS
    ==========                    =============

    node_id:                      stable identifier
      kai_letter_avoidance_001

    situation_id:                 which cluster it belongs to
      sit_unopened_letter

    delta_tension: 0.10           slightly increases tension
    delta_energy: -0.06           slightly decreases energy

    setup_refs:                   what this PREPARES
      - ev_harbor_meeting         (the meeting is set up
        _tonight                   but not yet faced)

    payoff_refs:                  what this PAYS OFF
      - sit_threshold_            (later departure scene)
        departure

    option_effect: clarify        doesn't resolve, but
                                  SHARPENS what the choice is

    pressure_tags:                which concerns it serves
      - attachment_threat
      - obligation

    origin_pressure_refs:         provenance back to concerns
      - cc_attachment_threat
      - cc_obligation

    source_lane: l2_generation    where it came from
    confidence: medium            how sure
    revisability: high            can be edited later
```

```
    ┌─────────────────────────────────────────────┐
    │ The graph gets STABLE RESIDUE.              │
    │                                             │
    │ It does NOT get:                            │
    │ - the causal slice                          │
    │ - the appraisal frame                       │
    │ - the operator scores                       │
    │ - the retrieved episodes                    │
    │ - the emotion vector                        │
    │                                             │
    │ That stuff lives in the SIDECAR.            │
    │ The graph stays THIN.                       │
    │ L3 can traverse this node without knowing   │
    │ anything about the middle layer.            │
    └─────────────────────────────────────────────┘
```

---

## Step 10: Provenance Sidecar

```
    SIDECAR (lives beside the graph node, not inside it)
    =======

    ┌──────────────────────────────────────────┐
    │ source_concern_ids:                      │
    │   - cc_attachment_threat                 │
    │   - cc_obligation                        │
    │                                          │
    │ causal_slice:                            │
    │   goal: preserve-possible-repair         │
    │   sign: threatens                        │
    │   attribution: kai, intentional          │
    │   temporal: prospective                  │
    │   options: [open-letter, delay-contact]  │
    │                                          │
    │ appraisal_frame:                         │
    │   desirability: -0.78                    │
    │   controllability: 0.28                  │
    │   likelihood: 0.82                       │
    │                                          │
    │ practice_context:                        │
    │   type: evasion                          │
    │   role: evader                           │
    │   affordances: [delay, distraction]      │
    │                                          │
    │ operator: avoidance                      │
    │ operator_score: 0.80                     │
    │                                          │
    │ retrieved_episodes:                      │
    │   - ep_avoidance_consequence             │
    │   - ep_last_rupture                      │
    │                                          │
    │ commit_type: salience                    │
    │ (focus shifted, nothing resolved)        │
    └──────────────────────────────────────────┘

    PURPOSE: if a human asks "why did the engine
    produce THIS moment?" — the sidecar has the
    complete interpretive chain.

    The GRAPH doesn't need this to be traversable.
    The DASHBOARD can read it for inspection.
    The AUTHORING MEMBRANE shows it during curation.
```

---

## Step 11: Reappraisal and Accumulation

### After the moment is committed

```
    BEFORE                        AFTER
    ======                        =====

    cc_attachment_threat           cc_attachment_threat
    intensity: 0.72       ──►     intensity: 0.82  ↑
                                  (avoidance sharpened
                                   the pressure)

    cc_obligation                  cc_obligation
    intensity: 0.64       ──►     intensity: 0.59  ↓
                                  (delay ritual bought
                                   brief relief)


    ┌─────────────────────────────────────────────┐
    │ The NEXT step is now different because:      │
    │                                             │
    │ - attachment_threat is STRONGER              │
    │ - obligation is slightly weaker              │
    │ - the generated episode is now in the        │
    │   retrieval pool for future steps           │
    │ - the practice may evolve                   │
    │                                             │
    │ This is how you go from one moment to a     │
    │ SEQUENCE of motivated moments that build    │
    │ on each other.                              │
    └─────────────────────────────────────────────┘
```

### Multi-step accumulation (the newest work)

```
    Step 1: Kai avoids (fills kettle, turns letter down)
         │
         ├── attachment_threat intensifies
         ├── generated episode enters retrieval pool
         │
    Step 2: Kai rationalizes (tells himself she'd understand)
         │
         ├── obligation pressure drops slightly
         ├── new episode enters pool
         │
    Step 3: Kai recalls harbor (memory intrudes)
         │
         ├── attachment_threat spikes
         ├── serendipity: harbor memory connects to current letter
         │
    Step 4: Kai rehearses opening the letter...
         │
         └── practice shifts from evasion to
             anticipated-confrontation

    ┌─────────────────────────────────────────────┐
    │ Each step FEEDS THE NEXT.                   │
    │                                             │
    │ The graph gets 4 candidate nodes.           │
    │ Each one has pressure provenance.           │
    │ Together they form a motivated arc,         │
    │ not a random collection.                    │
    │                                             │
    │ A human curates which survive.              │
    │ L3 traverses whatever survives.             │
    └─────────────────────────────────────────────┘
```

---

## The Full Pipeline at a Glance

```
    AUTHORED PRIMITIVES
    (character seed, backstory, situations)
                │
                ▼
    ┌──────────────────────┐
    │  CONCERN EXTRACTION  │  Theme rules fire on
    │  (infer_concerns_    │  primitive facts
    │   from_primitives)   │
    └──────────┬───────────┘
               │
               ▼
    ┌──────────────────────┐
    │    CAUSAL SLICE      │  Interpret what's
    │  (build_causal_      │  threatened, by whom,
    │   slice)             │  with what options
    └──────────┬───────────┘
               │
               ▼
    ┌──────────────────────┐
    │     APPRAISAL        │  Evaluate: how bad,
    │  (derive_appraisal_  │  how likely, how
    │   frame)             │  controllable?
    └──────────┬───────────┘
               │
               ├──► EMOTION VECTOR (derived view for dashboard)
               │
               ▼
    ┌──────────────────────┐
    │  PRACTICE CONTEXT    │  What social micro-
    │  (derive_practice_   │  situation? What moves
    │   context)           │  are legible?
    └──────────┬───────────┘
               │
               ▼
    ┌──────────────────────┐
    │  EPISODIC RETRIEVAL  │  Which memories match
    │  (retrieve_          │  this problem geometry?
    │   episodes)          │
    └──────────┬───────────┘
               │
               ▼
    ┌──────────────────────┐
    │  OPERATOR SCORING    │  Which cognitive family
    │  (score_operators)   │  best fits pressure +
    │                      │  appraisal + practice
    │                      │  + memory?
    └──────────┬───────────┘
               │
               ▼
    ┌──────────────────────┐
    │    LLM GENERATION    │  Content behind a typed
    │  (build_middle_      │  operator boundary.
    │   prompt → LLM)      │  LLM supplies texture,
    │                      │  middle layer supplies
    │                      │  control.
    └──────────┬───────────┘
               │
               ├──► GRAPH NODE (thin, traversable by L3)
               │
               ├──► PROVENANCE SIDECAR (rich, for inspection)
               │
               ▼
    ┌──────────────────────┐
    │    REAPPRAISAL       │  Concern intensities
    │  (reappraise_        │  update. Generated
    │   concerns)          │  episode enters memory.
    │                      │  Next step begins.
    └──────────┬───────────┘
               │
               ▼
         NEXT CYCLE...


    ┌─────────────────────────────────────────────┐
    │ THE GOVERNING PRINCIPLE:                    │
    │                                             │
    │ LLMs generate CONTENT and evaluate MEANING. │
    │ Deterministic systems handle CONTROL,       │
    │ SCHEDULING, and STATE MANAGEMENT.           │
    │                                             │
    │ The boundary is the TYPED INTERFACE:         │
    │ concern deltas, appraisal frames,            │
    │ operator selection, graph-compilable output. │
    └─────────────────────────────────────────────┘
```

---

## What's Still Toy

```
    WHAT THE PROTOTYPE PROVES        WHAT IT DOESN'T YET DO
    =========================        ======================

    ✓ Middle layer can be            ✗ No weighted abduction
      made executable                  (sparse primitives →
                                       competing interpretations)
    ✓ Concern inference
      matches hand-seeded            ✗ No partial-order causal
                                       scaffolding (earnedness
    ✓ Ablations produce                as structural property)
      intelligible operator
      flips                          ✗ No soft-constraint
                                       compiler (which candidates
    ✓ Graph compilation                survive together?)
      works under contract
                                     ✗ Only 3 operator families
    ✓ Reappraisal produces
      motivated sequences            ✗ Retrieval is exact-key,
                                       not rich reminding
    ✓ No prompt contamination
      needed to make it work         ✗ Semantic evaluation is
                                       heuristic string matching

    ┌─────────────────────────────────────────────┐
    │ It's an existence proof, not the full stack. │
    │                                             │
    │ The full stack (from 03_reply.md) would add: │
    │ - abductive interpretation layer            │
    │ - causal scaffold builder                   │
    │ - soft-constraint graph compiler            │
    │ - assumption-set memory (ATMS-style)        │
    │                                             │
    │ Those are future layers, not current code.  │
    └─────────────────────────────────────────────┘
```
