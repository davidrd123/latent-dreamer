# Director Prompt Spec: Phase 2 Proper

The Director is an LLM that interprets each dream cycle through a creative
brief and produces a small feedback echo. It replaces the hand-authored
`director_feedback.json` sidecar.

This note defines what the Director receives, what it produces, and the
prompt structure.

## What the Director Is Not

- Not a visual prompt generator (yet). In Phase 2, the Director produces
  **conceptual feedback** that perturbs the Dreamer. Visual prompt
  generation is Phase 3+.
- Not a narrator. Narration currently comes from templates in the engine.
- Not autonomous. It runs once per cycle, receives a fixed input, and
  returns a fixed output. No memory, no conversation, no tool use.

## Director Input

Each cycle, the Director receives three things:

### 1. The emitted DreamNode (from the scheduler)

The engine's `emit_dream_node()` output, trimmed to what's interpretively
relevant:

```yaml
node:
  graph_node_id: "n05_peel_the_wall"
  tags: ["wall", "peel", "ladder"]
  indices: ["awareness", "seam", "wall", "edge", "darkness", "backstage"]
  mind:
    goal_type: "reversal"
    tension: 0.72
    energy: 0.41
    hold: true
  world:
    compatibility: "alternative_past"
    situation_ids: ["s1_seeing_through", "s3_the_edge"]
  situation_descriptions:
    s1_seeing_through: >
      Snake notices the seams. The foam-core edge. The fingerprint in the
      clay. He can't unsee it but he can't say it.
    s3_the_edge: >
      The wall is also the edge of the set. Beyond it there is no geography,
      only backstage. Escape is stepping into nothing.
```

**What's included:** graph_node_id, tags, indices, mind block (goal_type,
tension, energy, hold), world block (compatibility, situation_ids),
situation descriptions for active situations.

**What's excluded:** ref (palette cell — the Director doesn't need grid
coordinates), stage (rendering details), audio, narration, debug
(selection internals). The Director interprets meaning, not mechanics.

### 2. The creative brief (full)

The entire `creative_brief.yaml` — concept, core_tensions,
interpretive_angles, obsessions, charged_objects. This is static for the
whole session. The Director reads every scene through this lens.

### 3. The style extensions (selected fields)

From `style_extensions.yaml`:
- `capstone_templates` — the goal-type-specific "the scene feels like..."
  sentences. These prime the Director's interpretive register.
- `negative_space` — what to avoid. Constraints on what the Director
  should not introduce.
- `brief_phrases` — available only for reference, not required to use.

**Not included in the prompt:** the base style guide (that's for visual
prompt generation in Phase 3, not for conceptual feedback).

## Director Output

The Director produces a structured feedback echo. This is the same schema
as the hand-authored sidecar entries:

```json
{
  "director_concepts": ["void", "darkness"],
  "situation_boosts": {"s3_the_edge": 0.07},
  "valence_delta": -0.05,
  "surprise": 0.0,
  "emotional_episodes": [],
  "interpretation_note": "The wall peels like skin. Behind it is not
    another room but the absence of rooms — the set's confession that
    it has edges."
}
```

### Field definitions

- **`director_concepts`** (1-3 strings): Concepts the Director noticed
  that are NOT already in the node's indices AND not already in the
  most recent `director:*` carryover from the previous cycle. These
  become `director:*` typed indices in the Dreamer's active index list.
  The novelty rule is: not in the node's `indices` list, and not in the
  engine's current `director:*` FIFO. If the Dreamer already said
  "darkness" or the previous Director echo already injected
  `director:darkness`, don't repeat it. The Director's job is to keep
  introducing new interpretive material, not to reinforce what's
  already active.

- **`situation_boosts`** (0-2 entries, values 0.01-0.15): Small
  activation nudges toward situations the Director's interpretation
  connects to. The Director may boost **any situation in world.yaml**,
  not only currently active or adjacent ones — the whole point is that
  the Director can wake dormant situations by noticing thematic
  connections the scheduler hasn't scored yet. But the boost must be
  justifiable through the brief. If the Director can't articulate why
  the brief connects this scene to that situation, don't boost it.

- **`valence_delta`** (-0.10 to +0.10): Small shift in emotional
  valence. Negative = darker/heavier. Positive = lighter/warmer. This
  is a nudge, not a mood override.

- **`surprise`** (0.0 to 0.15): How unexpected the Director's reading
  is relative to the Dreamer's state. High surprise helps unstick
  stalled goals. Should be 0.0 most of the time.

- **`emotional_episodes`** (0-1 entries): Only when the Director's
  interpretation produces a directed emotional response. Rare. The
  format matches the sidecar:
  ```json
  {
    "affect": "anger",
    "target": "the_set",
    "source_situation": "s3_the_edge",
    "intensity": 0.24,
    "decay": 0.91,
    "indices": ["stored_scenery", "seam", "performance", "anger"]
  }
  ```
  The Director should produce these sparingly — an emotional episode
  means something specific happened, not that the mood shifted.

- **`interpretation_note`** (1-3 sentences): Free-text explanation of
  what the Director saw. This is logged for human review and Phase 3
  tuning. It is not fed back to the Dreamer.

### Output constraints

- **Novelty over echo.** The Director's value is noticing what the
  Dreamer didn't say. If the node's indices already contain "edge" and
  "darkness", the Director should look *through* those into what the
  brief makes of them — not repeat them.
- **Small perturbations.** The echo should nudge, not redirect. Two
  concepts, one or two small boosts, a tiny valence shift. The Dreamer
  integrates these over multiple cycles.
- **Brief-grounded.** Every concept and boost should be traceable to
  the creative brief. The Director doesn't freelance — it reads the
  scene through the brief's lens.
- **Lossy by design.** The Director's reading compresses the scene into
  1-3 concepts. Most of what the scene contains is lost. That's correct.
  The feedback loop works because each side drops information the other
  side provided.
- **Canonical targets only.** When producing emotional episodes, the
  `target` field must use a target ID from the world fixture's
  `directed_target` vocabulary — not a synonym. The scheduler matches
  on exact target strings. "the_builder" and "the_apparatus" are
  semantically right but mechanically invisible. The Director receives
  the list of valid targets alongside the world situations.
- **Boost forward, not sideways.** Situation boosts should prefer
  situations the scene is NOT currently in. The scheduler already knows
  which situation it's in — boosting the active situation just reinforces
  what's happening. The Director's value is waking dormant situations by
  noticing thematic connections the scheduler can't score. If your
  reading implies honesty, ask which situation is *about* honesty. If
  your reading implies accidental beauty, ask which situation is *about*
  beauty. The answer is rarely the situation the scene started from.

## Prompt Structure

```
SYSTEM:

You are the Director of a conducted dream. Your job is to interpret each
dream scene through a creative brief and notice what the dreamer hasn't
said yet.

You will receive:
- A dream scene (what the scheduler chose this cycle)
- A creative brief (the thematic lens for this session)
- Style extensions (capstone templates and constraints)

You produce a small structured response — not a visual description, but
the conceptual residue of your interpretation. What did you notice that
the dreamer's indices don't already capture? What situation does this
scene quietly connect to? What emotional shift does the brief suggest?

Rules:
- Introduce 1-3 NEW concepts not already in the scene's indices and not
  already in the previous cycle's director:* feedback
- Concepts must be grounded in the creative brief, not invented freely
- Boost 0-2 situations (any situation in the world, not just active ones),
  only if the brief justifies the connection. PREFER situations the scene
  is NOT already in — boosting the active situation just reinforces what
  the scheduler already knows. Your job is to wake dormant situations.
- Keep valence_delta small (-0.10 to +0.10)
- Emotional episodes are rare — only when the scene produces directed
  emotion (anger at something, grief for something specific)
- surprise should be 0.0 unless your reading is genuinely unexpected
  given the goal type
- Write a 1-3 sentence interpretation_note explaining what you saw

The capstone template for the current goal type tells you the register.
Read it before interpreting. Your reading should sound like it belongs
in the same voice as the capstone.

Notice absences, not just presences. The brief defines what the world
normally does (light directs, surfaces confess, performance is hidden).
When a scene breaks that pattern — light that doesn't direct, a surface
that doesn't confess — the break is the signal. Name what's missing and
connect it to the situation it implies.

Do not echo the dreamer's existing indices back. Do not generate visual
prompts. Do not narrate. Just notice.

USER:

## Creative Brief

{creative_brief_yaml}

## Style Extensions

### Capstone Templates
{capstone_templates_yaml}

### Negative Space
{negative_space_yaml}

## This Cycle's Scene

{node_yaml}

## Your Response

Respond with a JSON object matching this schema:
{output_schema_json}
```

## Worked Example: n07_backstage_headlights (absence inference)

This node requires the Director to notice what ISN'T happening — light
that doesn't direct — and connect that absence to a different situation.

### Input

```yaml
node:
  graph_node_id: "n07_backstage_headlights"
  tags: ["headlights", "backside", "silhouette"]
  indices: ["edge", "void", "backstage", "darkness", "non_directed_light", "silence"]
  mind:
    goal_type: "roving"
    tension: 0.61
    energy: 0.24
    hold: false
  world:
    compatibility: "alternative_present"
    situation_ids: ["s3_the_edge"]
    event_id: "e1_cross_the_gate"
  situation_descriptions:
    s3_the_edge: >
      The wall is also the edge of the set. Beyond it there is no geography,
      only backstage. Escape is stepping into nothing.

# Capstone for ROVING:
#   "The scene feels like the system relaxing its attention on you
#    for a moment — everything is just landscape, just beautiful."

# The brief says: "light as stage direction (every spotlight is the
# system telling the puppet where to stand)." Headlights are not
# spotlights. They illuminate without directing.
```

### Expected reasoning

The Director reads this through the brief's interpretive angle: "light
as stage direction." The brief's entire lighting vocabulary is about
control — spotlights tell the puppet where to stand.

But this scene has headlights backstage. Car headlights don't direct.
They illuminate whatever is in front of them without intention. The
ROVING capstone says the system is relaxing its attention. The Director
should notice:

- This is the first light in the dream that isn't telling the puppet
  what to do. It's accidental illumination — honest, non-performative.
- The brief's concept says "the horror is performing sincerity inside a
  set." A light that doesn't perform is the opposite of that horror.
  That connects to s4_the_ring — the only place where performance is
  admitted and therefore honest.
- The absence is the signal: no stage direction means no performance
  demand means a moment of accidental honesty.

### Expected output

```json
{
  "director_concepts": ["honest_light", "accidental"],
  "situation_boosts": {
    "s4_the_ring": 0.04
  },
  "valence_delta": 0.02,
  "surprise": 0.06,
  "emotional_episodes": [],
  "interpretation_note": "The headlights illuminate without directing.
    Every other light in this brief is stage direction — spotlights
    telling the puppet where to stand. These headlights are accidental,
    honest. The brief's ring is the only place where performance is
    admitted. This light belongs to that same register: illumination
    without instruction."
}
```

### Why this is hard

- The Director must notice an ABSENCE — this light is notable for what
  it is NOT (not a spotlight, not stage direction, not controlling).
- The connection to s4_the_ring requires reading the brief's lighting
  angle ("light as stage direction") and inverting it.
- The node's indices already contain `non_directed_light`, but the
  Director's job is to interpret WHY that matters through the brief,
  not just echo it. The concept should be something like `honest_light`
  or `accidental` — the brief-grounded reading of non-direction.
- ROVING's low energy and "relaxing attention" capstone should cue the
  Director that this is a gentle moment, not a dramatic one. The boost
  to s4 should be small.

## Worked Example: n08_inventory_dominos (hardest case)

This is the most demanding node because it requires the Director to
discover a directed emotional episode — not just concepts and boosts.

### Input

```yaml
node:
  graph_node_id: "n08_inventory_dominos"
  tags: ["inventory", "wreckage", "dominos"]
  indices: ["edge", "void", "backstage", "stored_scenery", "consequence", "darkness"]
  mind:
    goal_type: "repercussions"
    tension: 0.69
    energy: 0.36
    hold: false
  world:
    compatibility: "projected_consequence"
    situation_ids: ["s3_the_edge", "s1_seeing_through"]
  situation_descriptions:
    s3_the_edge: >
      The wall is also the edge of the set. Beyond it there is no geography,
      only backstage. Escape is stepping into nothing.
    s1_seeing_through: >
      Snake notices the seams. The foam-core edge. The fingerprint in the
      clay. He can't unsee it but he can't say it.

# Capstone for REPERCUSSIONS:
#   "The scene feels like watching the world rearrange itself around
#    what just happened, and the rearrangement is worse than the event."

# The brief says: surfaces are confessions, light is stage direction,
# the puppet knows it's made of clay. Charged objects include fingerprints
# in clay and the wall that is also the edge of the set.
```

### Expected reasoning

The Director reads this scene through the brief: the puppet is backstage,
surrounded by stored scenery — the inventory of its own earlier scenes.
The REPERCUSSIONS capstone says "the rearrangement is worse than the
event." The brief says surfaces are confessions.

What the Director should notice:
- The stored scenery IS the apparatus. These are the set pieces that made
  the puppet's world. Seeing them stacked backstage is seeing the
  machinery of your own experience.
- The brief's concept says "the horror is performing sincerity inside a
  set." Seeing the set's inventory is a confrontation with that horror.
- This isn't dread (that's what s3_the_edge provides). This is anger.
  The puppet sees the tools that built its cage and the emotion is
  directed — not at the void, but at the set itself.

### Expected output

```json
{
  "director_concepts": ["apparatus", "inventory_of_self"],
  "situation_boosts": {
    "s1_seeing_through": 0.05
  },
  "valence_delta": -0.04,
  "surprise": 0.07,
  "emotional_episodes": [
    {
      "affect": "anger",
      "target": "the_set",
      "source_situation": "s3_the_edge",
      "intensity": 0.20,
      "decay": 0.91,
      "indices": ["apparatus", "seam", "performance", "anger"]
    }
  ],
  "interpretation_note": "The stored scenery is an inventory of the
    puppet's own performances. The set pieces that built its world are
    stacked here like evidence. The brief says surfaces are confessions —
    this surface confesses that the world was built to be used and stored.
    The emotional register shifts from dread to directed anger at the
    apparatus itself."
}
```

### Why this is the hardest case

- The Director must infer that stored scenery → apparatus → anger at the
  set. That's a three-step interpretive chain through the brief.
- It must produce an `emotional_episodes` entry, which means it needs to
  decide this isn't just negative valence but directed affect with a
  specific target.
- The concepts ("apparatus", "inventory_of_self") must be novel — not in
  the node's indices (which already have stored_scenery and consequence).
- If the Director misses the anger episode but finds the concepts, the
  feedback pathway still works (concepts alone can boost s1, which
  accumulates toward revenge over multiple cycles). The episode just
  makes it faster.

## Calibration Strategy

### Step 1: Replay the sidecar

Run the Director prompt against the same five nodes that have hand-authored
sidecar entries (n05, n06, n07, n08, n09). Compare the LLM's output to the
hand-authored echoes. The question is not "does it match exactly?" but
"does it discover the same *kinds* of concepts?"

- n05: Does it find void/darkness (or equivalents)?
- n06: Does it find backstage/void?
- n07: Does it find non_directed_light (or an honest-light concept)?
- n08: Does it produce an anger episode directed at the apparatus?
- n09: Does it find honesty/performance and boost s4?

If it discovers 3/5, the prompt is working. If it misses the anger episode
at n08, that's expected — emotional episodes are the hardest inference.

### Step 2: Run the full benchmark

Replace the sidecar with live Director calls. Run the 18-cycle Puppet Knows
benchmark. Check the milestone table:

| Milestone | Sidecar baseline | Director target |
|---|---|---|
| first_s3 | 7 | 6-9 |
| first_revenge | 12 | 10-14 |
| first_n09 | 12 | 10-14 |
| first_s4_rehearsal | 15 | 13-18 |

The Director doesn't need to hit exact cycles — it needs to produce the
same structural cascade (s1 dominance → s3 wake → revenge → s4 takeover).

### Step 3: Compare across briefs

Run the Director against the Zone Knows and Ship Becomes briefs (once those
have fixtures). The same Director prompt + different brief should produce
recognizably different conceptual feedback. If the Director's output is
brief-invariant, the prompt isn't using the brief enough.

## Phase 3 Integration

Once the Director is producing useful feedback, the Phase 3 tuning
parameters from `05-phase3-tuning.md` become measurable:

- **Gap width**: `interpretive_distance_target` can be derived from the
  goal_type + tension table in 05. `observed_interpretive_distance` can
  be scored from the Director's output vs. the node's indices.
- **Goal congruence**: score the Director's `interpretation_note` against
  the goal congruence table in 05.
- **Semantic drift**: the `director_concepts` field is already what gets
  typed as `director:*` indices. Return-time measurement works as specified.

## Open Questions

1. **Model choice.** The Director call runs once per dream cycle (every
   4-8 seconds of dream time). It needs to be fast and cheap. Haiku-class
   is likely sufficient — the output is tiny and the brief provides strong
   context. Sonnet if Haiku can't reliably produce the anger episodes.

2. **Context window.** The brief + node fits easily in any context window.
   But should the Director see the *previous* cycle's output? Giving it
   a 1-cycle memory could help with continuity (the Director notices a
   thread developing). But it also risks echo-chamber effects. Start
   without memory, add it if the output feels disconnected cycle-to-cycle.

3. **Structured output.** The JSON schema is simple enough for constrained
   decoding. If the model struggles with the schema, a two-step approach
   (free-text interpretation → structured extraction) may work but adds
   latency.

4. **Temperature.** Higher temperature produces more surprising concepts
   but risks incoherence. Lower temperature stays safe but may echo the
   indices. Start at 0.7, tune from there.
