My recommendation: **do not map the flat 9D conductor state to the APC Mini.** That is the wrong abstraction boundary.
For v1, map a **small bias surface over L3**:

* one bipolar **hold ↔ release** axis
* one **escalation** axis
* one **recall** axis
* one **intensity envelope** axis
* four **situation-lane boosts**

That is the smallest surface that can still produce all five traversal intents while staying playable. It also matches the later revised architecture move to a leveled control model where several of the original variables are derived rather than performer-facing, and it matches the IMS note that the mapping layer is the instrument and should be judged by playability, not dimensional richness.  

The reason is blunt: your own experiments say **abstract global curve controls are weak on the richer graph**, while **local lane biases can move traversal**. `16-graffito-trajectory-presets.md` says `early_release` was legible while `sustained_high` was not. `22-city-routes-robustness-interpretation.md` says the global conductor presets barely moved the feature arm. `24-city-routes-targeted-conductor-interpretation.md` says the first targeted pass reinforced the default route and entered too low in the stack. `26-city-routes-contrasting-conductor-interpretation.md` says `threshold_drive` and `exchange_fast` bent the route, while `spectacle_hold` and `refuge_hold` mostly did not. So the APC should privilege **release/hold** and **graph-local focus**, not separate “tension” and “energy” sliders.

## 1) Minimum mapping for the five intents

### Faders 1–8

| Fader | Control                | What it really biases                                      |
| ----- | ---------------------- | ---------------------------------------------------------- |
| 1     | **Hold ↔ Release**     | `dwell` vs `release` tendency                              |
| 2     | **Escalation**         | preference for higher-pressure / event-approach candidates |
| 3     | **Recall**             | return-to-earlier-material tendency                        |
| 4     | **Intensity envelope** | macro strength of the current bias field                   |
| 5     | **Threshold lane**     | `s5_the_threshold` + `e4_bridge_lockdown`                  |
| 6     | **Exchange lane**      | `s6_the_exchange` + `e3_wrong_handoff`                     |
| 7     | **Spectacle lane**     | `s4_public_spectacle` + `e2_blackout_siren`                |
| 8     | **Refuge lane**        | `s3_false_refuge` / shelter material                       |

That is the **physical** v1 surface.
But the **minimum live subset** for the first test is actually only **6 load-bearing controls**:

* Hold ↔ Release
* Escalation
* Recall
* Intensity
* Threshold lane
* Exchange lane

Spectacle and Refuge should be present, but I would treat them as provisional because City Routes only showed strong conductor leverage on the threshold and exchange lanes.

### Pads

Use one row of 8 pads for discrete actions:

1. Threshold focus pulse
2. Exchange focus pulse
3. Spectacle focus pulse
4. Refuge focus pulse
5. Hold latch
6. Recall pulse
7. Commit confirm
8. Commit veto / neutral reset

Everything else on the APC can stay unused in v1.

### How the five intents emerge

This is the important part: **do not give all five intents separate controls**.

* **dwell** = Hold/Release pushed toward hold, lane stable, intensity low-mid
* **release** = Hold/Release pushed toward release, intensity down, often refuge lane up
* **recall** = Recall high or Recall pulse, intensity low-mid
* **escalate** = Escalation high + Intensity high + usually Threshold or Exchange lane
* **shift** = change lane emphasis while Hold is low

So `shift` is **not** a dedicated knob. It emerges from **moving focus from one structural lane to another**. That is better than a “shift fader” because the graph already tells you what the meaningful alternatives are.

### Why this surface, specifically

Because it matches both the architecture and the harness:

* the canonical control surface was already narrower than the 9D vector: situation boosts, hold/release bias, escalation bias, recall bias, intensity envelope
* `graffito_pilot.choose_feature()` already gives local situation/event targeting a concrete path into scoring
* the City Routes graph has strong semantically coherent lanes, especially **Threshold** (`s5`/`e4`) and **Exchange** (`s6`/`e3`)

So on the APC, **focus should mean lane selection**, not an abstract free-floating vector.

## 2) Which of the 9 dimensions should be direct, derived, or dropped

The clean answer is:

### Direct in v1

* **hold**
* **focus**, but only as **discrete situation-lane focus**, not a continuous vector

### Derived in v1

* **tension**
* **energy**
* **density**
* **clarity**
* **novelty**
* **tempo**
* **phase**

### Dropped as independent performer-facing controls

* separate **tension** fader
* separate **energy** fader
* separate **density** fader
* separate **clarity** fader
* separate **novelty** fader
* separate **tempo** fader
* separate **phase** fader
* continuous **focus vector**

That is not a contradiction. It means the internal state can stay rich, but the **instrument should stay small**.

A good v1 derivation scheme is:

* **tension** ← escalation bias + active lane pressure + event proximity
* **energy** ← intensity envelope + recent gesture rate
* **density** ← tension × novelty
* **clarity** ← inferred from active mode, recall/rehearsal higher, roving/shift lower
* **tempo** ← energy
* **phase** ← traversal/session arc
* **novelty** ← lane-change rate / contrast seeking

That is almost exactly where the revised architecture ended up: tempo, density, phase, and clarity are already treated as things to derive, not specify independently. 

The direct takeaway is:

> **A dedicated tension fader is probably a mistake.**
> Your own City Routes sweep already told you that global tension/energy curves barely move the richer substrate.

So the performer-facing replacement is **Intensity Envelope**, not separate Tension and Energy.

## 3) What the Wizard-of-Oz conductor test should look like

Do this **before** building the full mapping.

### Rule 0

Use the APC as a **scheduler-bias controller only**.
Do **not** let it drive Scope/Lyria directly during the test, or you are testing DJ-style stage control, not conductor mapping.

The current `daydream-to-stage-contract.md` reality is useful for hardware/MIDI transport, but it is the wrong control semantics for this question.

### Setup

Run two substrates:

* **Graffito**: because `early_release` already produced a visible contour change
* **City Routes**: because it is the richer graph where local lane bias matters

The Wizard gets a dashboard with:

* current node
* current situation
* current candidate next nodes
* candidate situations/events
* current fader/pad states
* a simple written mapping sheet translating controls into bias changes

The wizard should **not improvise tastefully**. The whole point is to test the surface, not the wizard.

### Tasks

Ask the performer to produce, on command:

1. **dwell**
2. **shift**
3. **recall**
4. **escalate**
5. **release**

Then ask for two longer arc shapes:

6. **build → release**
7. **avoidance / refuge → forced threshold**

Use the graphs that already matter:

* Graffito task: can the performer reliably induce the early-release contour?
* City Routes task A: can they bias toward **Threshold**
* City Routes task B: can they bias toward **Exchange**
* optional later task: can they make Spectacle or Refuge legible at all?

### Measurements

Measure four things:

* **Reachability**: can the intended intent be produced within 1–2 scheduler cycles?
* **Repeatability**: does the same gesture family work 3 times in a row?
* **Causal feel**: does the performer report “I did that” rather than “I fished around and something happened”?
* **Observer legibility**: can a blind observer tell which intent/arc was being attempted from the resulting trace/watch run?

Use IMS-style criteria too:

* **time to first meaningful control move** should be short
* there should be at least **3 clearly distinct expressive gestures**
* the system should feel responsive enough that the controller reads as an instrument, not a settings panel.  

### Acceptance bar

I would use this:

* **dwell**, **release**, **escalate**: ≥80% reachability
* **shift**, **recall**: ≥70% reachability
* at least **4 of 5 intents** are distinguishable by an outside observer above chance
* performer says the surface feels like **influencing flow, not operating menus**
* any control unused in the first session gets removed

The failure review was right: the cheapest exposing test is exactly this kind of Wizard-of-Oz harness measuring reachability, repeatability, and subjective causal feel.

## 4) Should tides be conductor-controlled, autonomous, or hybrid?

**Hybrid, but strongly autonomous.**

More precisely:

* **base tides / situation weights should run autonomously**
* the conductor should **nudge** them
* the conductor should **not** get one fader per tide in v1

Why:

1. the canonical architecture already wanted tides to be slow background forces that the scheduler reads
2. the revised architecture is already drifting from explicit tides toward **situation weights**
3. the mapping problem is already hard enough without adding another layer of slow controls
4. your own NIME/IMS warning says over-dimensionalized mappings hurt exploration and appropriability

So for v1:

* autonomous system keeps advancing situation weights / tides
* performer biases the **foreground** through hold/release, escalation, recall, intensity, and lane focus
* one optional later meta-control could be added: **Yield ↔ Resist**, meaning “how much should autonomous pressure dominate vs live bias”

But **no per-tide knobs now**.

The simplest useful mental model is:

> **The tides are weather. The performer plays against the weather.**

That also fits the revised architecture’s move to situation weights as the medium-timescale pressure field, with only a few fast controls exposed directly. 

## Bottom line

If I had to freeze an APC Mini mk2 v1 today, I’d do this:

* **F1** hold ↔ release
* **F2** escalation
* **F3** recall
* **F4** intensity
* **F5** threshold
* **F6** exchange
* **F7** spectacle
* **F8** refuge

Pads:

* threshold / exchange / spectacle / refuge focus
* hold latch
* recall pulse
* commit confirm
* commit veto/reset

And I would **not** expose separate tension, energy, density, clarity, novelty, tempo, or phase controls.

That is the minimum viable instrument because it lines up with what your scheduler can actually be bent to do right now, and it does not ask the performer to steer abstractions the graph is not yet responsive to.

Look away for 20 seconds. Unclench your jaw.
