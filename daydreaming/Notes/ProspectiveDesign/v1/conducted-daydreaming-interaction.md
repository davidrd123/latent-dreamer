# Conducted Daydreaming: Interaction, World State, and the Body in the Loop

## Companion to: `conducted-daydreaming-architecture.md`

---

## Part I: World State and Narrative Persistence

### 1.1 The Problem of Accumulation

Without persistent world state, the system produces mood pieces — beautiful, ephemeral, and ultimately forgettable. Each session is self-contained. Nothing carries forward. There is no reason to return.

With persistent world state, sessions accumulate. The letter gets opened or it doesn't. If it gets opened, the world is different. The next daydream session starts from a new place. Situations resolve and spawn new situations. Characters gain knowledge, accumulate wounds, process experiences. The daydream has consequences.

This is what makes a game a game: consequences persist. But the daydream medium is not a game — you don't have agency over *actions*, you have agency over *attention*. You don't decide what Kai does; you decide what the daydream dwells on, and that dwelling shapes which events eventually occur.

### 1.2 Two Layers of State

The critical distinction: what *happened* vs what the daydream is *exploring*.

```
WORLD STATE (canonical, persistent, accumulates across sessions)
│
│  "The letter has been opened. Kai knows what it says.
│   Maren is at the harbor. It's the third day of rain."
│
▼
DAYDREAM STATE (ephemeral, branching, can contradict world state)
│
│  "Kai is standing at the table. The letter is unopened.
│   What if it said something different?"
│
▼
RENDERED EXPERIENCE (video + audio)
```

World state is the game layer — persistent, monotonically advancing, mutated by events. Daydream state is the mind-wandering layer — ephemeral, branching, free to revisit and contradict canonical reality.

Both render to video and audio. But they render differently. A scene that matches world state renders with clarity and presence — sharp focus, full color, direct audio. A scene that contradicts world state (counterfactual, memory, fantasy) renders with markers of unreality — softer focus, shifted color grade, more reverb, attenuated presence. The viewer can *feel* the difference between "this is happening" and "this is being imagined" without being told.

### 1.3 What World State Tracks

Borrowing from game design but tuned for emotional narrative rather than spatial simulation:

```yaml
world_state:
  # --- Temporal ---
  day: 3
  time_of_day: evening
  weather: rain           # affects place rendering + audio atmosphere
  season: late_autumn

  # --- Characters ---
  characters:
    kai:
      location: apartment
      emotional_state:
        surface: "performing calm"
        actual: "spiraling anxiety"
      knowledge:            # what this character knows (gates events)
        - "the letter exists"
        - "maren sent it"
        # NOT "what the letter says" — gated by an event
      wounds:               # unprocessed emotional events
        - id: "the_argument"
          severity: high
          processing: 0.3   # 0.0 = raw, 1.0 = integrated
      desires:
        - "to know what the letter says"
        - "to never know what the letter says"
        # contradictions ARE the state, not a bug

    maren:
      location: harbor
      emotional_state:
        surface: "focused on work"
        actual: "waiting for a response that isn't coming"
      knowledge:
        - "sent the letter three days ago"
        - "kai hasn't responded"

  # --- Relationships ---
  relationships:
    kai_maren:
      status: strained
      unspoken:
        - "the thing that happened at the harbor last month"
        - "what the letter actually means"
      tension: 0.8
      last_interaction: "two weeks ago, brief, performatively casual"

  # --- Objects ---
  objects:
    the_letter:
      status: unopened       # KEY STATE VARIABLE
      location: kitchen_table
      visible_to: [kai]
      emotional_charge: 0.9
      days_present: 3

  # --- Places ---
  places:
    apartment:
      mood: claustrophobic
      charged_objects: [the_letter]
      recent_events: ["kai pacing at 3am", "rain on windows"]
    harbor:
      mood: liminal
      charged_associations: [maren, departure, the_argument]

  # --- Situations (generate tides) ---
  situations:
    the_letter:
      status: unresolved
      blocking_action: "kai opening the letter"
      stakes: high
      days_unresolved: 3
      escalation_rate: 0.1    # tension increases each session-day
      ripeness: 0.7           # 0.0 = dormant, 1.0 = inevitable
    the_argument:
      status: unresolved
      blocking_action: "either character acknowledging it"
      stakes: high
      days_unresolved: 31
      ripeness: 0.5
```

### 1.4 Events as State Mutations

Certain graph nodes are marked as **events** — traversing them during performance mutates world state. Most nodes are pure daydream: explorative, non-canonical, consequence-free. Event nodes are where the world changes.

```yaml
node:
  id: "dm_089"
  scene: "Kai's hand on the envelope. A pause. Tearing it open."
  type: event               # this node mutates world state

  state_mutations:
    - path: "objects.the_letter.status"
      from: "unopened"
      to: "opened"

    - path: "characters.kai.knowledge"
      add: "what_the_letter_says"

    - path: "situations.the_letter.status"
      from: "unresolved"
      to: "resolved"         # this tide dies

    - path: "characters.kai.wounds"
      add:
        id: "the_letter_contents"
        severity: high
        processing: 0.0

    # New situation opens as old one closes
    - path: "situations"
      add:
        id: "response_to_letter"
        status: unresolved
        blocking_action: "kai deciding what to do"
        stakes: high
        ripeness: 0.0
```

When traversed during performance, the world state updates permanently. The letter tide dies. A new tide spawns. The next session operates in a world where the letter has been read.

### 1.5 Counterfactual Rendering

After an event has occurred, the daydream graph still contains pre-event nodes. The daydream can revisit the moment before the letter was opened. But it renders differently now — the video translator checks each node against current world state:

```python
def translate_to_video(node, conductor, world_state):
    prompt_parts = [node.scene, node.visual_texture]

    # Does this node imply a world state that no longer holds?
    if node_contradicts_world_state(node, world_state):
        # COUNTERFACTUAL rendering — memory, fantasy, what-if
        prompt_parts.append(
            "soft focus, desaturated, memory grain, "
            "dreamlike, slightly overexposed, nostalgic"
        )
        rendering_mode = "counterfactual"
    else:
        # CANONICAL rendering — present, vivid, real
        rendering_mode = "canonical"

    # Audio translator also applies counterfactual treatment:
    # more reverb, less presence, ambient rather than direct
    # Lyria: lower brightness, lower density, more temperature

    return VideoPromptFrame(
        prompt=", ".join(prompt_parts),
        rendering_mode=rendering_mode,
        # ... loras, control signals
    )
```

The viewer sees: Kai opens the letter (vivid, present, sharp). Then the daydream drifts back to the moment before — same kitchen, same table, but soft and ghostly. What if I hadn't opened it? What if it said something else? The world state tracks what's real. The daydream explores what's possible. The rendering distinguishes between them.

### 1.6 Situation Ripeness and Inevitability

The most important world-state mechanic: situations have a **ripeness** that increases over time and across sessions.

```python
class Situation:
    ripeness: float         # 0.0 = dormant, 1.0 = inevitable

    def update(self, dt, world_state):
        # Ripeness increases with time and escalation
        self.ripeness += self.escalation_rate * dt

        # External events can spike ripeness
        # (another character doing something related)

        # Daydreaming about it slows ripeness slightly
        # but cannot reverse it — you can't un-ripen
        # a situation by worrying, only by resolving it

    def available_for_event(self, conductor_override=False):
        if conductor_override:
            return self.ripeness > 0.3   # low bar for deliberate choice
        else:
            return self.ripeness > 0.85  # high bar for autonomous trigger
```

Below the autonomous threshold, only the user can trigger an event (through a choice point). Above the threshold, the event triggers itself — the tidal pressure is too strong, and the daydream goes there whether you guide it or not. You can delay but not prevent.

This creates a fundamental dynamic: the longer you avoid something, the more inevitable it becomes. The tension builds across sessions. Eventually, something has to give.

### 1.7 The Session Loop

```
SESSION 1:
  World state: letter unopened, day 3
  Daydream: explores anxiety about the letter,
            circles the harbor, imagines Maren
  Performance: traverses graph, does NOT hit event node
  Result: letter still unopened, but
          - escalation_rate advances ripeness
          - kai's wound processing from "the_argument" advances
            slightly (daydreaming as partial integration)
          - new associations discovered, logged

SESSION 2:
  World state: letter unopened, day 4 (ripeness now 0.7)
  Daydream: tension higher, more tidal pull toward letter,
            harder to avoid letter-adjacent nodes
  Performance: traverses graph, HITS event node — letter opened
  State mutation: letter.status = opened, new situation spawns
  Result: world permanently changed

SESSION 3:
  World state: letter opened, day 5, new situation active
  Daydream: processes letter contents, revisits pre-opening
            moments as counterfactuals (rendered differently),
            new tides from new situation, different emotional weather
  Performance: entirely different landscape than sessions 1-2

SESSION N:
  Situations have resolved and spawned.
  Some characters have new knowledge, new wounds.
  Old wounds have been partially processed through daydreaming.
  The world has moved. The system remembers everything.
```

---

## Part II: Source Material and Emotional Coherence

### 2.1 What Makes Daydreaming Coherent

When humans daydream, coherence comes from three things:

**Characters with inner contradictions.** A character who wants two incompatible things is a perpetual tide generator. They never resolve, so the daydream keeps returning to them. A fully consistent character is emotionally inert. The best characters for this system are the ones where "what do they really want?" has a different answer depending on the scene.

**A world with emotional physics.** Not rules about magic or technology — rules about what kinds of things happen to people here. In a noir world, trust is always betrayed. In Tarkovsky, memory and place are the same thing. In this system, emotional physics constrain the scenario generator the same way LoRA weights constrain the visual generator — they shape texture and tendency without determining content.

**Unresolved situations with stakes.** These are the tides. A character who needs to make a choice but hasn't. A relationship where something was said that can't be unsaid. Each unresolved situation generates gravitational pull, and the daydream keeps circling back.

### 2.2 World Bible as Vault

The vault for conducted daydreaming is not a knowledge base — it is a world bible. The note types:

**Character sheets** — linked directly to LoRA identifiers so the video pipeline knows these faces:

```yaml
---
type: character
name: Kai
lora_id: kai_v3
emotional_core: "wants connection but can't stop performing"
contradictions:
  - "genuinely kind but instrumentally manipulative"
  - "craves solitude but panics when alone"
relationships:
  - target: Maren
    quality: "unresolved attraction complicated by a betrayal
              neither acknowledges"
  - target: the_city
    quality: "hates it here but can't leave"
visual_signature: "always slightly too still, eyes that track"
behavioral_constraints:
  will_not: ["directly confront emotions", "ask for what they want",
             "admit vulnerability openly"]
  will: ["perform ease", "intellectualize", "find reasons to delay",
         "go to the harbor 'coincidentally'"]
status: active
emotional_tone: conflicted
stakes: high
---
```

**Place sheets** — environments with emotional charge and rendering specs:

```yaml
---
type: place
name: the_harbor
visual_texture: "grey water, industrial cranes, fog, sodium lights"
audio_texture: "foghorn, chain clinking, distant engine, water on concrete"
emotional_charge: "liminal — between departure and arrival, never either"
characters_associated: [Kai, Maren]
---
```

**World rules** — the emotional physics:

```yaml
---
type: world_rule
domain: emotional_physics
---

In this world:
- Memory is unreliable but emotionally true
- Places remember what happened in them (rooms hold moods)
- Conversations never quite resolve; something is always left unsaid
- Weather is emotional, not meteorological
- Time is non-linear — scenes from different periods can coexist
```

**Situations** — tide generators:

```yaml
---
type: situation
status: unresolved
emotional_tone: anxious
stakes: high
characters: [Kai, Maren]
---

The letter Maren sent but Kai hasn't opened. Three days on the
kitchen table. Every scene in the apartment includes it in
peripheral vision.
```

**Resonance notes** — mythological or fictional parallels providing deep structure:

```yaml
---
type: resonance
source: "Orpheus and Eurydice"
mapping: "Looking back destroys what you love. Kai's need to verify
          Maren's feelings is exactly what will drive Maren away."
characters: [Kai, Maren]
emotional_structure: "the act of checking kills the thing being checked"
---
```

### 2.3 Types of Source Material

Different source material produces different daydream qualities:

**LoRA-trained characters and aesthetics** — the strongest option because the video pipeline already knows these faces and environments. The vault describes characters in terms the system can render. Visual coherence is guaranteed by the LoRA; emotional coherence comes from the character sheets.

**Mythological and archetypal material** — myths are already daydream-shaped. They are collections of charged images and situations with deep emotional resonance rather than tight plots. Orpheus looking back. Icarus falling. Persephone's dual citizenship. These function as emotional attractors — nodes with high in-degree that the imagination has been graph-walking for millennia. Loaded as resonance notes, they provide the deep emotional logic the scenario generator follows.

**Fiction with strong emotional physics** — certain fiction is pre-structured for this. Material where the *situations* are more memorable than the plot:

- Tarkovsky (Stalker, Solaris, Mirror) — every scene is a charged emotional space
- Borges — labyrinths, doubles, infinite libraries — conceptual spaces with emotional weight
- Wong Kar-wai — desire, missed connections, time — the same beats revisited from different angles
- Sebald — memory, walking, photographs, history in landscapes
- Lynch — the uncanny, doubles, the ordinary turned strange

What these share: strong aesthetic identity, characters as emotional functions, situations that resist resolution.

**Genre physics** — noir, horror, romance, fairy tale each have reliable emotional physics that constrain the scenario generator. "In this world, trust is always betrayed" is a world rule that makes every relationship scene charged regardless of specific content.

### 2.4 The Blending Principle

The sweet spot is not adaptation but **resonance**. You don't load the vault with "the plot of Stalker." You load it with the emotional *situation* from Stalker — the idea of a zone where desire becomes literal, where getting what you actually want rather than what you say you want is terrifying. Then the engine daydreams through that with your characters, your aesthetics, your LoRA-trained visual world.

The recognizable element is the emotional physics, not the narrative. Viewers feel "this reminds me of something" without naming it — which is exactly what a daydream does. It draws on everything you've absorbed and recombines it in a way that feels both familiar and unprecedented.

Vault notes can explicitly layer: "the emotional situation of Orpheus mapped onto Kai and Maren" — the myth provides deep structure, the characters provide specificity, the LoRAs provide visual realization.

---

## Part III: Three Interaction Layers

### 3.1 The Spectrum of Conductor Input

The conducting interface operates across a spectrum from unconscious to deliberate:

```
UNCONSCIOUS ◄─────────────────────────────────────► DELIBERATE

Biometric signals    Ambient gesture     Explicit choice
(heart rate, EMG,    (posture, breath,   (selection at
 skin conductance)    stillness, lean)    event nodes)

Continuous           Periodic            Sparse
Fast (ms)            Medium (seconds)    Slow (minutes)
Involuntary          Semiconscious       Conscious
```

All three layers feed the same conductor state simultaneously. The system blends them with appropriate weighting — biometric provides continuous background modulation, ambient gesture provides medium-timescale steering, explicit choice provides high-stakes course correction at critical moments.

### 3.2 Layer 1: Biometric (Unconscious, Continuous, Fast)

ECG provides heart rate and heart rate variability (HRV). EMG provides muscle tension. EDA (galvanic skin response) provides arousal. These are the body's real-time emotional telemetry.

```python
class BiometricInput:
    def read(self) -> dict:
        return {
            "heart_rate": float,        # bpm
            "hrv": float,               # heart rate variability (ms)
            "muscle_tension": float,    # EMG amplitude (forearm or jaw)
            "arousal_trend": float,     # derived: rising/falling/stable
        }

    def map_to_conductor(self, bio: dict) -> dict:
        # HRV is the gold signal:
        # low HRV = stress/engagement, high HRV = relaxation/openness
        tension_bias = inverse_normalize(
            bio["hrv"],
            self.config.hrv_relaxed,
            self.config.hrv_stressed
        )

        # Heart rate TREND, not absolute (people differ)
        # Rising HR relative to personal baseline = arousal
        energy_bias = normalize_trend(
            bio["heart_rate"],
            self.config.hr_baseline,
            window_seconds=30
        )

        # Muscle tension → hold/fixation
        # Tense body = the daydream has you gripped
        hold_bias = bio["muscle_tension"] > self.config.tension_threshold

        return {
            "tension": tension_bias,
            "energy": energy_bias,
            "hold": hold_bias,
        }
```

**Critical design principle:** Use change relative to baseline, not absolute values. Everyone's resting heart rate is different. What matters: did it just spike? Is HRV dropping? Is muscle tension rising? Deltas map to emotional engagement with current content.

**The biometric feedback loop:** The daydream approaches charged material → heart rate rises → system detects rising arousal → conductor tension increases → daydream leans further in → heart rate rises more. Or: relaxation → HRV increases → tension drops → daydream softens → relaxation deepens. The daydream and the body co-regulate. This is not metaphorical conducting — the autonomic nervous system is the baton.

**EMG specifically:** Uniquely useful for the **hold state**. When people are emotionally gripped, muscles tense — jaw, shoulders, hands. EMG detects this directly:

- Elevated EMG → `hold = true` → daydream fixates, replays, can't move on
- EMG releases → `hold = false` → daydream can drift
- EMG spike → flag this node as emotionally significant in the log

The daydream literally cannot move past something that has you physically tense. It only releases when you release. This conducting gesture requires zero training — it is involuntary, honest, and directly meaningful.

### 3.3 Layer 2: Ambient Gesture (Semiconscious, Periodic, Medium)

The middle ground — not explicit choice, but readable body signals. Leaning forward or back. Breathing rhythm. Stillness vs fidgeting. Posture via IMU.

```python
class AmbientInput:
    def map_to_conductor(self, signals: dict) -> dict:
        return {
            # Breath rate → tempo coupling
            # Slow breathing = slow daydream pace
            "tempo": normalize(
                signals["breath_rate"], 
                bpm_low=8, bpm_high=20
            ),

            # Stillness → clarity
            # Very still = focused attention = high clarity
            # Fidgeting = diffuse attention = low clarity
            "clarity": inverse_normalize(
                signals["movement_magnitude"],
                self.config.still_threshold,
                self.config.fidget_threshold
            ),

            # Postural lean → approach/avoidance
            # Leaning in = engagement = daydream can go deeper
            # Leaning back = withdrawal = daydream should soften
            "approach": normalize(
                signals["forward_lean"], 
                degrees_back=-10, degrees_forward=10
            ),
        }
```

This layer is semiconscious: you might not *decide* to lean forward, but you could if you wanted to. It gives experienced users a way to steer without breaking the dreamlike state with explicit decisions.

### 3.4 Layer 3: Explicit Choice (Conscious, Sparse, Slow)

Choice points surface **only at event nodes** — the nodes that mutate world state. Everything else is ambient traversal driven by tides and biometric/gesture input. The system drifts, the body conducts the mood, and then the daydream arrives at a moment where something could actually change — and a choice surfaces.

**The key design decision:** choice presentation must be daydream-shaped, not menu-shaped.

```
NOT THIS:
  ┌─────────────────────────────┐
  │  A) Open the letter          │
  │  B) Leave the apartment      │
  │  C) Call Maren               │
  └─────────────────────────────┘

THIS:
  The video slows. The audio thins to near-silence.

  Three possible futures emerge simultaneously —
  superimposed, ghostly, each at partial opacity:

  [left of frame]   Kai's hand reaching for the envelope
  [center]          The apartment door, open, rain beyond
  [right of frame]  A phone screen, Maren's name, thumb hovering

  Your attention — gaze, lean, heart rate response —
  settles on one. It solidifies. The others dissolve.
  The daydream continues.
```

The choice is presented *as a daydream* — as a moment of indecision where multiple possible futures are simultaneously imagined, and one crystallizes. This is phenomenologically accurate to how decisions feel during actual mind-wandering. You don't see a menu; you see possible futures flickering, and one becomes real.

### 3.5 Biometric Choice Weighting

At choice points, the biometric layer influences which option is selected — not overriding conscious choice, but weighting it:

```python
async def present_choice_point(
    options: list[DaydreamNode],
    video_pipe: VideoPipeline,
    lyria_session: LyriaSession,
    biometric: BiometricInput,
    timeout: float = 8.0
) -> DaydreamNode:

    # Render all options simultaneously, ghosted/superimposed
    composite = render_choice_composite(options)
    await video_pipe.send(composite)

    # Monitor biometric response to each option
    responses = {opt.id: [] for opt in options}

    start = time.time()
    while time.time() - start < timeout:
        bio = biometric.read()

        # Cycle visual emphasis slowly across options
        # (each option gets ~2s of slight prominence)
        emphasized = cycle_emphasis(options, time.time() - start)

        # Record biometric response during this emphasis
        responses[emphasized.id].append({
            "hr": bio["heart_rate"],
            "hrv": bio["hrv"],
            "tension": bio["muscle_tension"],
        })

        # Check for explicit selection
        # (deliberate gesture, sustained gaze lock, button press)
        explicit = check_explicit_input()
        if explicit:
            return explicit

        await asyncio.sleep(0.1)

    # No explicit choice within timeout —
    # select by strongest biometric arousal response
    arousal_scores = {}
    for opt_id, readings in responses.items():
        arousal_scores[opt_id] = compute_arousal_delta(
            readings, baseline
        )

    # The option that produced the strongest body response wins
    chosen_id = max(arousal_scores, key=arousal_scores.get)

    # Visual: chosen option solidifies, others dissolve
    await transition_choice_to_selected(
        options, chosen_id, video_pipe, lyria_session
    )

    return next(o for o in options if o.id == chosen_id)
```

When the three possible futures appear, the system monitors physiological response to each. The one producing the strongest arousal gets subtle visual and audio emphasis — slightly more vivid, slightly more present. The user might still choose differently. But the default, if they let their body decide, follows the strongest physiological response.

This is conducting at its purest. You are not deciding with your conscious mind. You are letting your body's response to imagined futures determine which future becomes real. This is what daydreaming actually does — you don't choose which daydream to have. Your emotional system guides you toward the one you need.

---

## Part IV: The Sensor Architecture

### 4.1 Hardware Mapping

Drawing on the embedded systems work documented in `rust-embedded-futures-research-notes.md` and the IMS framework from `ims-exploration-notes.md`:

```
IMS 5-Layer Architecture Applied:

Layer 1 (Sensing):   ECG + EMG + IMU on nRF52840 (BLE)
Layer 2 (Mapping):   Biometric → conductor state deltas (laptop-side)
Layer 3 (Engine):    Daydream graph traversal + edge selection
Layer 4 (Output):    Video pipeline + Lyria RealTime
Layer 5 (Experience): Sit down, sensors on, daydream finds you
```

### 4.2 Sensor Tiers

**Tier 1 — Minimum Viable Biometric Loop (essential):**

| Sensor | Signal | Conductor Mapping | Hardware |
|--------|--------|-------------------|----------|
| ECG (single-lead) | Heart rate + HRV | tension (via HRV), energy (via HR trend) | AD8232 or MAX30003 breakout → nRF52840 |
| EMG (single-channel) | Muscle tension (forearm or jaw) | hold state, emotional grip | MyoWare 2.0 or INA128 amplifier → nRF52840 |

These two signals close the fundamental feedback loop: the daydream detects your emotional engagement and responds to it. ECG gives arousal/stress/engagement continuously. EMG gives the hold state — when something has you physically gripped.

**Tier 2 — Enriched Mapping (valuable):**

| Sensor | Signal | Conductor Mapping |
|--------|--------|-------------------|
| EDA / GSR | Skin conductance (arousal) | Slower but more reliable arousal signal than HR; confirms/contradicts HR readings |
| Respiration (chest strap or ECG-derived) | Breath rate | tempo coupling — the daydream breathes with you |
| IMU (from existing embedded work) | Posture, stillness, lean | clarity (stillness), approach/avoid (lean) |

**Tier 3 — Aspirational:**

| Sensor | Signal | Conductor Mapping |
|--------|--------|-------------------|
| Eye tracking | Gaze position, fixation duration | attention focus, choice point selection (which option draws you) |
| EEG (consumer-grade) | Alpha/theta ratio | depth of absorption → how dreamlike the rendering should be |

### 4.3 The Wearable Pod

A single nRF52840-based sensor pod doing ECG + EMG + IMU over BLE:

```
┌──────────────────────────────────┐
│  SENSOR POD (nRF52840 + BLE)     │
│                                  │
│  ECG ──► ADC ──┐                 │
│  EMG ──► ADC ──┤                 │
│  IMU ──► I2C ──┤──► BLE GATT ──►│──► Laptop
│                │    service       │
│  Embassy async │                 │
│  tasks:        │                 │
│   ecg_task     │                 │
│   emg_task     │                 │
│   imu_task     │                 │
│   ble_task     │                 │
│   health_task  │                 │
└──────────────────────────────────┘
```

This maps directly to the async task skeleton from the embedded notes (Section 14):

```
input_task   → ecg_task, emg_task (ADC sampling → Signal, latest-value)
sensor_task  → imu_task (I2C → Signal)
app_task     → feature extraction (HR from ECG, RMS from EMG, tilt from IMU)
output_task  → ble_task (GATT characteristics updated at ~20Hz)
health_task  → watchdog + connection state + battery monitoring
```

The IMS latency targets apply: sensor → BLE → laptop processing should be under 30ms for biometric conducting to feel responsive. BLE MIDI-style latency (~10-20ms) is sufficient since we're reading emotional trends, not triggering musical events.

### 4.4 Laptop-Side Processing

```python
class BiometricProcessor:
    """Receives BLE data, computes features, outputs conductor deltas."""

    def __init__(self, ble_connection):
        self.ble = ble_connection
        self.hr_baseline = None       # established in first 60s
        self.hrv_baseline = None
        self.emg_baseline = None

    async def calibrate(self, duration=60):
        """Establish personal baselines during quiet rest."""
        readings = []
        async for sample in self.ble.stream(duration):
            readings.append(sample)
        self.hr_baseline = median([r.heart_rate for r in readings])
        self.hrv_baseline = median([r.hrv for r in readings])
        self.emg_baseline = median([r.emg_rms for r in readings])

    async def stream_conductor_deltas(self):
        """Continuous stream of conductor state influences."""
        async for sample in self.ble.stream():
            yield {
                "tension": self._hrv_to_tension(sample.hrv),
                "energy": self._hr_trend_to_energy(sample.heart_rate),
                "hold": sample.emg_rms > self.emg_baseline * 1.5,
                "tempo": self._breath_to_tempo(sample.breath_rate),
                "clarity": self._stillness_to_clarity(sample.imu_magnitude),
                "timestamp": sample.timestamp,
            }

    def _hrv_to_tension(self, hrv):
        """Low HRV = high tension. Normalized against personal baseline."""
        if self.hrv_baseline is None:
            return 0.5
        return clamp(1.0 - (hrv / self.hrv_baseline), 0.0, 1.0)

    def _hr_trend_to_energy(self, hr):
        """Rising HR = rising energy. 30-second sliding window."""
        self.hr_window.append(hr)
        if len(self.hr_window) < 30:
            return 0.5
        trend = linear_regression_slope(self.hr_window[-30:])
        return clamp(0.5 + trend * 10, 0.0, 1.0)
```

---

## Part V: Temporal Flow of a Performance

### 5.1 Full Timeline Example

```
PRE-SESSION (30-60 seconds):
  Sensors connect. Biometric calibration.
  60 seconds of quiet: baseline HR, HRV, EMG established.
  Screen dark. Audio: room tone only.

0:00  PERFORMANCE BEGINS
  Tides active. Graph entry point selected based on
  world state (letter unopened, day 4, evening, rain).
  First node: the apartment, seen from outside, rain.
  Audio: rain on glass, distant city, low drone.

  Biometric: baseline. System reads: relaxed, open.
  Conductor: tension=0.3, energy=0.4, clarity=0.6

0:00-3:00  AMBIENT TRAVERSAL
  Tidal pull toward the letter (ripeness 0.7) shapes
  edge selection. Graph drifts toward apartment interior.
  Biometric: steady, moderate engagement.
  Experience: dreamlike flow, scenes shifting slowly.
  The letter appears in peripheral vision of several nodes.

3:15  CHARGED MATERIAL APPROACH
  Graph reaches a node close to the letter.
  Biometric: heart rate rising (+8 bpm from baseline),
             HRV dropping.
  System: tension_bias increasing from biometric input.
  Video: camera slowly closing in on the apartment.
  Audio: drone intensifies, rain becomes rhythmic.

3:30  BIOMETRIC FEEDBACK LOOP ENGAGES
  Rising tension → daydream leans in → more tension.
  Video: tight on the kitchen, the table, the envelope.
  Audio: sparse piano enters, tense intervals.

3:45  EMG THRESHOLD CROSSED → HOLD ACTIVATES
  User's jaw or hands have tensed.
  System: hold = true. Daydream fixates.
  Video: nearly still. The envelope. Kai's hand nearby.
  Audio: sustained chord, breath-like pulse.
  The system cannot move forward until the body releases.

3:55  EMG RELEASES → HOLD BREAKS
  User physically relaxes (conscious or unconscious).
  Graph traversal resumes.

4:00  EVENT NODE REACHED — CHOICE POINT
  Situation ripeness: 0.7 (below autonomous threshold
  of 0.85, so requires user participation).

  Three futures rendered simultaneously, ghosted:
    [left]   Kai's hand reaching for envelope
    [center] Apartment door, open, rain beyond
    [right]  Phone screen, Maren's name

  System cycles visual emphasis across options,
  monitoring biometric response to each.

  Over 6 seconds, strongest HR/HRV response is
  to option [left] — the letter.
  Option [left] gains subtle visual prominence.

  User either:
    (a) Makes no explicit choice → body selects [left]
    (b) Deliberately chooses an option via gesture/lean/gaze

4:08  CHOICE RESOLVED → STATE MUTATION
  Kai opens the letter.
  World state updates: letter.status = opened.
  Letter tide dies. New situation spawns.

  Video: chosen future solidifies, others dissolve.
  Hard transition to vivid present-tense rendering.
  Audio: shift — tension releases into something
         more complex. New harmonic territory.

4:08-8:00  AMBIENT TRAVERSAL (new world state)
  The emotional landscape has shifted.
  Revisiting pre-event nodes → counterfactual rendering.
  New tides beginning their slow oscillation.
  Biometric: processing the event, HR gradually settling.

8:00-12:00  POST-EVENT DAYDREAMING
  The daydream explores consequences, imagines Maren's
  response, revisits the argument, processes.
  Some nodes show Kai at the harbor — possibly
  seeking Maren, possibly avoiding.

12:00  SESSION ENDS
  Fade to dark. Audio dissolves to room tone.
  World state saved.
  Performance log recorded.
  
  Next session: world state persists. The letter
  is open. It cannot be unopened. But the daydream
  can — and will — revisit the moment before.
```

### 5.2 Conductor State Integration

During performance, the conductor state is a blend of three sources:

```python
class ConductorState:
    def update(
        self,
        tides: TidalField,
        biometric: dict,
        ambient: dict,
        dt: float
    ):
        # Tidal contribution (slow, structural)
        tidal = tides.aggregate()
        self.tension += tidal["tension_bias"] * 0.3 * dt
        # ... other tidal couplings

        # Biometric contribution (fast, involuntary)
        self.tension = lerp(self.tension, biometric["tension"], 0.4)
        self.energy = lerp(self.energy, biometric["energy"], 0.3)
        self.hold = biometric["hold"]  # binary, immediate

        # Ambient gesture contribution (medium, semiconscious)
        self.tempo = lerp(self.tempo, ambient.get("tempo", self.tempo), 0.2)
        self.clarity = lerp(
            self.clarity, ambient.get("clarity", self.clarity), 0.2
        )

        # Clamp all values
        self.tension = clamp(self.tension, 0.0, 1.0)
        self.energy = clamp(self.energy, 0.0, 1.0)
        # ... etc

        # Natural decay toward baseline when no strong signal
        self.tension *= (1.0 - 0.05 * dt)  # slow drift toward 0
        self.energy *= (1.0 - 0.03 * dt)
```

The weighting means: tides provide the emotional weather (you can't override the slow build toward the letter), biometric provides the moment-to-moment responsiveness (your body's response shapes the texture), and ambient gestures provide optional conscious steering (lean in to go deeper, lean back to withdraw).

---

## Part VI: Experience Design

### 6.1 The 10/10/10 Framework

Following the IMS design principle: learnable in 10 seconds, expressive in 10 minutes, deep in 10 hours.

**10 seconds:** Sit down. Sensors on. The daydream starts. You don't need to do anything — it flows. Your body is already conducting even if you don't know it. The feedback loop is active from the first heartbeat.

**10 minutes:** You feel the feedback. The daydream leans into what grips you. When you tense, it holds. When you relax, it drifts. A choice point surfaces and you feel yourself drawn. You realize your body is steering this. You start to recognize the characters, the world, the unresolved tensions.

**10 hours (across multiple sessions):** The world has changed because of choices made — some conscious, some your body made for you. You know these characters. You have opinions about what should happen. You find yourself thinking about it between sessions. You are daydreaming about the daydream.

### 6.2 The Endgame

When the system produces material that the user involuntarily revisits in their own mind between sessions — when you catch yourself wondering whether Kai should have opened the letter, or imagining what Maren will do next — then the medium is working. The computational daydream has seeded an actual daydream. The loop has jumped from silicon to flesh.

### 6.3 What This Medium Is

It is not a game — you don't have agency over actions. You have agency over attention.

It is not a film — the sequence isn't fixed and it responds to your body.

It is not ambient video — things happen and persist across sessions.

It is not a visual novel — there are no dialogue trees, no text, no menus.

It is closest to what it would feel like to *inhabit someone else's daydream* as they process their life — following the stream of thought as it circles, avoids, confronts, fantasizes, and eventually moves the world forward. The conducting interface means you're not just observing — you're *participating in the emotional processing*, with your body as the instrument.

---

## Appendix A: Relationship to Companion Documents

| Document | Contribution |
|----------|-------------|
| `conducted-daydreaming-architecture.md` | Core architecture: daydream engine, graph structure, vault memory, rendering pipeline, build sequence. This companion extends it with world state, interaction design, and biometric conducting. |
| `instrument-stack-architecture.md` | Conductor state vector, agent contracts, tidal oscillators. All reused here with biometric input added to the conductor update loop. |
| `ims-exploration-notes.md` | IMS 5-layer architecture applied to the sensor pod. Latency targets, hardware choices (nRF52840), async task skeleton, playtest methodology. |
| `rust-embedded-futures-research-notes.md` | Embedded async architecture for the sensor wearable. BLE GATT services via trouble crate. Power and latency considerations. |
| `conductor-landscape.md` | Gesture capture and mapping. VMC/OSC transport. Wekinator for ML-based gesture→control training — applicable to biometric→conductor mapping. |
| `narrative-conducting.md` | Selection as conducting. The phenomenology of choice. The tree as preserved counterfactuals — directly applicable to the daydream graph and world state's canonical/counterfactual distinction. |
| `generative-steering.md` | The core Authoring/DJing/Conducting distinction. Preparation as the actual work. The convergence of Loom and Video Conductor — extended here to include biometric conducting and world-state persistence. |

---

## Appendix B: Formal Verification Horizon — LLM Guardrails via Theorem Proving

### B.1 The Problem: Probabilistic Generators Need Deterministic Guardrails

The daydream engine's scenario generator is an LLM. It is probabilistic, creative, and unconstrained. It does not *know* that Kai can't reference the letter's contents before the event that opens it. It does not enforce that a character's behavioral constraints ("Kai will not directly confront emotions") are respected. It will sometimes hallucinate knowledge, break character, or propose state transitions that are impossible given the current world.

This is the general problem of LLM agents: they are powerful generators but unreliable reasoners about constraints. The standard mitigation is prompt engineering — include the rules in the prompt and hope the model follows them. This works most of the time but fails probabilistically, which is exactly the failure mode you can't tolerate in a persistent world where mutations are irreversible.

The architectural response: a **formal validator** between the LLM and the graph.

```
SCENARIO GENERATOR (LLM — probabilistic, creative)
        │
        │ proposed nodes (may violate invariants)
        ▼
FORMAL VALIDATOR (deterministic, proven properties)
        │
        │ reject ──► regenerate with violation noted in prompt
        │
        │ admit
        ▼
DAYDREAM GRAPH (guaranteed consistent)
```

Every node the scenario generator proposes passes through formal checking before admission. Every event mutation is verified against invariants before application. The LLM proposes; the verifier disposes.

### B.2 The Interface Boundary

The world state engine exposes a small, stable API. The implementation behind this interface can evolve from a Python dict to a rule engine to a formally verified module without any other system component changing.

```python
class WorldStateEngine:
    """The boundary. Implementation changes;
    interface does not."""

    def query(self, path: str) -> Any:
        """Current value of a state variable."""

    def character_knows(self, character: str, fact: str) -> bool:
        """Does this character have this knowledge?"""

    def validate_event(self, event: Event) -> tuple[bool, str]:
        """Can this event fire? Returns (valid, reason)."""

    def apply_event(self, event: Event) -> WorldState:
        """Apply mutation. Irreversible in canonical timeline."""

    def validate_node(self, node: DaydreamNode) -> tuple[bool, list[str]]:
        """Does this proposed node respect world state?
        Returns (valid, list_of_violations).
        
        Checks:
        - Character knowledge: does this node imply a character
          knows something they haven't learned?
        - Character behavior: does this node violate a
          behavioral constraint from the character sheet?
        - World rules: does this node violate the emotional
          physics of the world?
        - Temporal consistency: does this node imply a time
          or state that contradicts the current world?
        """

    def contradicts(self, node: DaydreamNode) -> bool:
        """Does this node imply state that doesn't hold?
        Used for canonical vs counterfactual rendering."""

    def situation_ripeness(self, situation_id: str) -> float:
        """Current ripeness of this situation."""

    def available_events(self, conductor: ConductorState) -> list[Event]:
        """Which events could fire right now?"""
```

### B.3 Three Implementation Tiers

**Tier 1: Sprint version (Python dict + asserts)**

Good enough for prototyping. World state is a nested dict. Validation is imperative checks. Catches obvious violations but can't prove anything about the system as a whole.

```python
class DictWorldState(WorldStateEngine):
    def __init__(self, path: str):
        self.state = yaml.safe_load(open(path))

    def validate_node(self, node):
        violations = []
        # Check: does this node reference knowledge the
        # character doesn't have?
        for char, facts in node.implied_knowledge.items():
            for fact in facts:
                if not self.character_knows(char, fact):
                    violations.append(
                        f"{char} cannot know '{fact}' — "
                        f"no granting event has occurred"
                    )
        return (len(violations) == 0, violations)
```

**Tier 2: Rule engine (Datalog / embedded Lisp DSL)**

World rules expressed declaratively. Queryable. Composable. The system can answer "why can't this node be admitted?" with a proof trace, not just a boolean. This is where the collaboration gets interesting.

```lisp
;; Knowledge rules
(rule can-know (?character ?fact)
  (event-completed ?event)
  (event-grants ?event ?character ?fact))

(rule knows (?character ?fact)
  (can-know ?character ?fact)
  (not (forgotten ?character ?fact)))

;; Character constraint rules
(rule behavior-violation (?character ?action ?node)
  (node-implies-action ?node ?character ?action)
  (character-will-not ?character ?action))

;; World physics rules
(rule conversation-must-not-fully-resolve (?node)
  (node-type ?node conversation)
  (node-resolves-all-tension ?node)
  ;; violation: the world rules say conversations
  ;; never quite resolve
  )

;; Validation: a node is admissible iff no rule fires
;; against it
(query admissible (?node)
  (not (knowledge-violation ?node _))
  (not (behavior-violation _ _ ?node))
  (not (world-physics-violation ?node)))
```

This doesn't need to be ACL2 — a small Datalog or a Lisp DSL is sufficient. But it's *shaped like* something ACL2 could reason about, which means the transition to Tier 3 is a formalization of existing rules, not a rewrite.

**Tier 3: Formally verified properties (ACL2)**

Prove that the world state machine, given the rules from Tier 2, satisfies global invariants for all reachable states — not just the current one.

Target properties:

```
SAFETY PROPERTIES (bad things never happen):

  no-knowledge-without-source:
    For all characters C and facts F,
    if (knows C F) then there exists an event E such that
    (event-completed E) and (event-grants E C F).
    
    "No character can know something without a reason."

  no-impossible-events:
    For all events E in the completed-events set,
    at the time E was applied, all preconditions of E
    were satisfied by the then-current state.
    
    "No event was applied illegally."

  character-consistency:
    For all nodes N admitted to the graph,
    no behavioral constraint of any character referenced
    in N is violated by N.
    
    "Characters always act in-character."

  tide-boundedness:
    For all reachable states S,
    the sum of all active tide tension contributions
    in S is ≤ 1.0.
    
    "The emotional system can't overflow."

LIVENESS PROPERTIES (good things eventually happen):

  situation-eventual-ripeness:
    For all situations with escalation_rate > 0,
    there exists a finite number of sessions after which
    ripeness ≥ 1.0.
    
    "Nothing stays unresolved forever."

  narrative-continuity:
    For all reachable states S,
    there exists at least one event whose preconditions
    are satisfiable from S.
    
    "The narrative can always continue — no dead ends."

CONSISTENCY PROPERTIES:

  resolution-kills-tide:
    For all situations, if status = resolved then
    tide-active = false.
    
    "Resolved situations don't haunt."

  counterfactual-detection-completeness:
    For all nodes N and world states W,
    if N implies any state variable that differs from W,
    then contradicts(N, W) = true.
    
    "Every memory/fantasy is correctly identified for
    counterfactual rendering."
```

### B.4 What ACL2 Specifically Offers

ACL2 is well-suited to this because:

**The state space is finite and well-structured.** World state is a nested record with enumerated fields. Characters are finite. Knowledge facts are finite. Events are a finite catalog. This is exactly the kind of system ACL2 reasons about — a state machine with defined transitions and invariants.

**The properties are first-order.** The invariants above are expressible in first-order logic. No higher-order reasoning needed. ACL2's sweet spot.

**The verification is compositional.** You can prove properties of subsystems independently: prove the knowledge system is consistent, prove the tide system is bounded, prove the event system preserves preconditions. Then compose.

**The failure modes are meaningful.** If ACL2 finds a counterexample — a sequence of events that produces a state where a character knows something without a source — that's a real bug in the world bible, not a theoretical concern. The counterexample is literally a narrative inconsistency.

### B.5 The Collaboration Interface

For a collaborator working in ACL2:

**What they receive from the system:**
- World state schema (the YAML structure, formalized as a record type)
- Event catalog (all defined events with preconditions and mutations)
- World rules (the Tier 2 logic rules, which become axioms)
- Character constraints (behavioral invariants per character)
- Tide system specification (oscillator parameters, coupling rules)

**What they produce:**
- Proofs that the invariant catalog holds for all reachable states
- Counterexamples when invariants are violated (these are bugs in the world bible)
- Verified validator function that can be called from the Python system (possibly via a foreign function interface, or by generating Python code from the verified spec)

**What doesn't need formal verification:**
- The daydream engine (the LLM is probabilistic by design; the validator catches its mistakes)
- The graph structure (nodes are validated on admission; the graph itself is just storage)
- The rendering pipeline (aesthetic, not logical)
- The conductor state (continuous, not discrete; bounded by clamp, not by proof)
- The biometric input (physical signals, not logical propositions)

The scope is tight: the world state machine and its transitions. Everything else is either probabilistic (and filtered) or continuous (and clamped).

### B.6 Integration Path

**Sprint (weeks 1-2):**
- Build `DictWorldState` — Python dict, imperative validation
- Write invariants as comments in the code and prose in the world bible
- Collect violations from the scenario generator (log them; how often does the LLM break constraints?)

**Post-sprint (month 1-2):**
- Implement Tier 2 rule engine — Datalog or Lisp DSL
- Express world rules, character constraints, knowledge gates as logic
- Validator returns proof traces (why was this node rejected?)
- Share the rule set with ACL2 collaborator as a formalization target

**Formal verification (month 3+):**
- ACL2 formalization of world state machine
- Prove safety and liveness properties
- Counterexamples feed back as world bible corrections
- Optionally: generate a verified validator from the ACL2 spec that replaces the Python implementation behind the same interface

**The key principle:** the interface boundary (`WorldStateEngine`) is designed now. The implementation evolves. The LLM generates freely; the validator constrains deterministically; the formal proofs guarantee that the constraints are sufficient. Each tier makes the guardrails stronger without changing anything else in the system.

### B.7 The General Pattern

This architecture — probabilistic generator → formal validator → guaranteed-consistent output — is applicable beyond this system. It's a general pattern for any application where LLMs produce structured output that must satisfy invariants:

- Agent workflows where actions have preconditions
- Code generation where the output must type-check
- Planning systems where plans must be physically realizable
- Game AI where NPCs must respect world rules
- Any system where "the LLM usually gets it right" isn't good enough

The conducted daydreaming system is a concrete, bounded, aesthetically motivated instance of this pattern. The world is small enough to verify. The stakes (narrative consistency) are high enough to justify the effort. And the failure mode (Kai references the unopened letter's contents) is immediately obvious to any viewer, making verification practically meaningful rather than theoretically satisfying.

The ACL2 collaboration isn't overkill — it's a well-scoped application of formal methods to a real problem (LLM guardrails) in a domain (interactive narrative) where the state space is tractable and the properties are meaningful. It just doesn't need to be in the first sprint.
