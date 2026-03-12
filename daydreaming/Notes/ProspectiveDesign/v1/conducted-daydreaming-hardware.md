# Conducted Daydreaming: Hardware, Biofeedback, and Formal Verification

## Companion to: `conducted-daydreaming-architecture.md`, `conducted-daydreaming-interaction.md`

---

## Part I: The Biometric Conducting Interface

### 1.1 The Core Loop

The conducted daydreaming system uses physiological signals as conductor input. The body becomes the baton:

```
Body state → Sensor → Feature extraction → Conductor state delta →
    Daydream response → Altered experience → Altered body state → ...
```

This is simultaneously a conducting interface, a biofeedback system, and — across sessions — a training mechanism. The user's nervous system learns to modulate the daydream by modulating itself. The daydream and the body co-regulate.

### 1.2 Three Interaction Layers

Biometric input is one of three simultaneous conducting layers, all feeding the same conductor state:

| Layer | Consciousness | Timescale | Signals |
|-------|--------------|-----------|---------|
| **Biometric** | Unconscious, involuntary | Fast (ms–seconds) | Heart rate, HRV, muscle tension, brain rhythms |
| **Ambient gesture** | Semiconscious | Medium (seconds) | Posture, breath rate, stillness, lean |
| **Explicit choice** | Conscious, deliberate | Slow (minutes) | Choices at event nodes, blink gestures |

The weighting means: tides provide the emotional weather (slow, structural, unavoidable), biometric provides moment-to-moment responsiveness (the body's reaction shapes the texture), and ambient/explicit gestures provide optional conscious steering. The experienced user discovers they can lean in to go deeper, lean back to withdraw — but even a first-time user who does nothing is already conducting through their heartbeat.

### 1.3 Signal → Conductor Mapping

| Signal | Source | Feature | Conductor Variable | Mapping Logic |
|--------|--------|---------|-------------------|---------------|
| ECG | Heart | Heart rate trend (30s window) | `energy` | Rising HR relative to personal baseline = rising energy |
| ECG | Heart | HRV (RMSSD) | `tension` | Low HRV = high stress/engagement = high tension |
| ECG | Heart | Respiratory sinus arrhythmia coherence | `clarity` | High coherence (~6 breaths/min resonance) = focused, receptive |
| EMG | Forearm | RMS amplitude vs baseline | `hold` (binary) | Above threshold = physically gripped = daydream fixates |
| EMG | Jaw/frontalis | RMS amplitude vs baseline | `tension` (secondary) | Jaw clench / brow furrow = stress confirmation |
| EEG | Frontal (Fp1/Fp2) | Alpha/theta ratio | `absorption_depth` | Theta > alpha = hypnagogic edge = deep daydream state |
| EEG | Frontal | Beta power (12-30 Hz) | `focus` | High beta = focused attention; low beta = diffuse wandering |
| IMU | Body | Movement magnitude | `clarity` | Stillness = focused attention; fidgeting = diffuse |
| IMU | Body | Forward lean angle | `approach` | Leaning in = engagement = daydream can go deeper |
| EOG | Eyes | Blink detection | Explicit gesture | Double-blink = "I want a choice point here" |

**Critical design principle:** All mappings use change relative to personal baseline, not absolute values. Everyone's resting heart rate differs. What matters: did it spike? Is HRV dropping? Is muscle tension rising? Deltas map to emotional engagement. Baselines are established during a 60-second calibration at session start.

---

## Part II: Hardware Platform — Neuro PlayGround Lite

### 2.1 Why NPG Lite

The Neuro PlayGround Lite (Upside Down Labs) is a multichannel Feather-form-factor wireless biosignal acquisition board. It collapses the entire sensor tier of the conducted daydreaming system into a single $240 purchase.

**Source:** https://www.crowdsupply.com/upside-down-labs/neuro-playground-lite

**Key specs:**

| Feature | Detail |
|---------|--------|
| MCU | ESP32-C6 (RISC-V), 4 MB Flash |
| Channels | 3 on-board, 6 with VibZ+ playmate |
| Modalities | ECG, EMG, EOG, EEG (all via BioAmp analog front-end) |
| ADC | 12-bit SAR |
| Wireless | Wi-Fi 6, BLE 5, Thread, Zigbee |
| Power | LiPo battery with on-board charger, USB-C |
| Expansion | Feather form factor, Playmate boards, QWIIC I2C |
| Software | Chords-Python, Chords-Web, Chords LSL Connector (all open source) |
| Haptic feedback | Vibration motor + buzzer on VibZ/VibZ+ playmates |

**What it replaces:** The project docs originally specced a custom nRF52840-based sensor pod with discrete analog front-end (AD8232 for ECG, INA128 for EMG). The NPG Lite eliminates weeks of hardware bring-up. The BioAmp front-end handles instrumentation amplification, filtering, and gain — the hard analog problems that are genuinely difficult to get right from scratch.

### 2.2 Recommended Configuration: Beast Pack + Accessories

| Item | Price | Purpose |
|------|-------|---------|
| **Beast Pack** | $240 | 6 channels (VibZ+ playmate), case, battery, 13 snap cables, 8 alligator cables, 24 gel electrodes |
| **BioAmp Accessory Bundle** | $43 | Dry electrode bands (ECG, EMG, EEG) for quick-setup sessions + skin prep kit |
| **Baby Gel Electrodes (100 pack)** | $20 | Clean signal during development/calibration |
| **Snap Cables (10 pack)** | $20 | Extra cables for flexible electrode placement |
| **Total** | **$323** | + $8 US shipping |

**Why Beast over Ninja:** The Ninja Pack provides 3 channels. With 3 channels, every session configuration is a compromise — you sacrifice ECG for better EEG, or lose EMG to fit both. With 6 channels, you run everything simultaneously:

```
Channel 1: ECG (chest)           → HR, HRV → tension, energy
Channel 2: EMG forearm            → grip/tension → hold state
Channel 3: EMG jaw or frontalis   → clench/furrow → stress confirmation
Channel 4: EEG (Fp1)             → alpha/theta/beta → absorption depth
Channel 5: EEG (Fp2 or reference) → bilateral or reference
Channel 6: EOG or spare           → blink detection → deliberate gesture
```

During development, simultaneous recording of all modalities lets you compare and correlate — discovering which signals actually matter for conducting before committing to a final channel allocation.

**Why dry electrode bands:** The BioAmp Accessory Bundle includes dry electrode bands for ECG (chest), EMG (arm), and EEG (forehead). Dry electrodes give lower signal quality than gel but require no skin preparation. For the daydream experience, this matters enormously — the "learnable in 10 seconds" principle requires: sit down, strap on, start. Gel electrodes for development and calibration (when you want the cleanest possible signal), dry bands for actual sessions (when you want the fastest possible setup).

### 2.3 Channel Allocation for Conducting

The 6-channel allocation strategy, from most to least critical:

**Channels 1-2: ECG + EMG forearm (Tier 1 — essential)**

These two signals close the fundamental biofeedback loop. ECG gives you heart rate and HRV continuously. EMG gives you the hold state — when the user is physically gripped, the daydream fixates.

The hold state is the single most compelling conducting gesture in the system. It requires zero training, is involuntary and honest, and maps directly to a meaningful daydream behavior. When something in the daydream has you physically tense, the daydream cannot move past it. It only releases when you release. Your body is holding the daydream in place.

**Channel 3: EMG jaw or frontalis (Tier 1.5 — valuable)**

Secondary tension confirmation. Jaw clenching and brow furrowing are reliable stress indicators that complement HRV. If ECG says "rising tension" and jaw EMG confirms, the conductor state can be more confident. If they disagree (high HR from excitement rather than stress), the system can distinguish arousal valence.

**Channels 4-5: EEG frontal (Tier 3 — experimental)**

Alpha/theta ratio for absorption depth. Beta power for focused vs. diffuse attention. This is where 12-bit ADC limitations are most relevant (see Part III). Start with channels 4-5 recording but not feeding the conductor state. Log everything. Once you have enough data to evaluate whether the EEG features are reliable enough to be useful, integrate them.

**Channel 6: EOG or spare (Tier 3 — optional)**

EOG blink detection enables a deliberate conducting gesture: double-blink to request a choice point. The NPG Lite firmware already includes blink detection (`BCI-Blink-Serial`, `Blinky-Keys-BLE`). This is invisible to observers, requires no hand movement, and doesn't break the dreamlike state.

Alternatively, keep channel 6 as a spare for experimentation — different electrode placements, different signal types, comparing frontal vs. temporal EEG, etc.

### 2.4 The Two-Device Architecture (Future)

The NPG Lite handles biosignal acquisition. For posture/movement and precision haptic feedback, a companion nRF52840 pod may still be valuable:

```
NPG Lite (ESP32-C6)               nRF52840 Pod
├── ECG ch1 ──► BLE/WiFi ──┐      ├── IMU ──► BLE ──────┐
├── EMG ch2 ──► BLE/WiFi ──┤      ├── Haptic ◄── BLE ───┤
├── EMG ch3 ──► BLE/WiFi ──┤      └─────────────────────┤
├── EEG ch4 ──► BLE/WiFi ──┤                             │
├── EEG ch5 ──► BLE/WiFi ──┤                             │
├── EOG ch6 ──► BLE/WiFi ──┤                             │
└───────────────────────────┤                             │
                            ▼                             ▼
                     LAPTOP: BiometricProcessor
                     ├── HR + HRV from ECG → tension, energy
                     ├── RMS from EMG → hold state
                     ├── Alpha/theta from EEG → absorption depth
                     ├── Tilt/stillness from IMU → clarity, approach
                     └──► ConductorState → graph traversal → renderers

                     Haptic feedback ◄── emotional beats from traversal
```

The NPG Lite's VibZ+ playmate has a vibration motor, which provides basic haptic feedback (pulse on tide crest, buzz at choice point). For more nuanced haptic conducting — the daydream *touching you back* with subtle, expressive vibration patterns — a custom DRV2605L-driven linear resonant actuator on the nRF52840 pod would be the upgrade path.

This is a post-sprint consideration. NPG Lite alone is sufficient for the biometric conducting MVP.

---

## Part III: ADC Resolution — The 12-bit Question

### 3.1 The Core Issue: Dynamic Range

A 12-bit ADC provides 4,096 discrete voltage levels. A 24-bit ADC provides 16,777,216. This determines what signals you can resolve.

The biosignals span a huge amplitude range:

| Signal | Typical Amplitude | Frequency Range |
|--------|------------------|----------------|
| ECG | 1–3 mV | 0.05–100 Hz |
| EMG | 50 µV – 5 mV | 20–500 Hz |
| EEG | 10–100 µV | 0.5–100 Hz |
| EOG | 50–3500 µV | DC–100 Hz |

EEG is the problem. At 10-100 microvolts, it is the smallest signal. Meanwhile, the same electrodes pick up facial muscle EMG (orders of magnitude larger), mains hum (millivolts), electrode contact noise, and motion artifacts. The signal of interest is buried under noise 100-1000x larger.

### 3.2 How the NPG Lite Compensates

The board is not just an ADC — the BioAmp analog front-end does substantial processing before digitization:

```
Electrode → Instrumentation Amp (high CMRR, rejects common-mode noise)
         → Bandpass Filter (removes DC offset + high-frequency noise)
         → Gain Stage (amplifies signal of interest)
         → 12-bit ADC
```

The gain stage is key. For EEG measurement at ~1000x gain, a 10 µV signal becomes 10 mV at the ADC — well within 12-bit resolution. The tradeoff: high gain means large artifacts (blinks, movement, electrode shifts) saturate the amplifier. The ADC clips until the artifact passes. A 24-bit system can use lower gain and still resolve the signal, clipping much less often.

### 3.3 Impact by Modality

**ECG at 12-bit: Excellent.** ECG is a relatively large signal (millivolts). R-peak detection for heart rate is robust even with noisy data because the QRS complex has a distinctive morphology. HRV computation from R-peak timing is essentially unaffected by ADC resolution — you're measuring *when* peaks occur, not their precise amplitude. The Pan-Tompkins algorithm works fine on 12-bit data.

**EMG at 12-bit: Excellent for this use case.** The hold state needs envelope detection — is the muscle tense or relaxed, and roughly how much? Rectify, smooth, threshold. The binary `hold` signal ("is RMS above baseline by more than 1.5x?") is robust even with noise.

**EEG at 12-bit: Adequate for coarse features, limited for subtle ones.**

For **frequency band power** (alpha, theta, beta): FFT over ~4-second windows, computing power in specific bands, smoothed over 10-30 seconds. The 12-bit quantization noise adds a noise floor to the spectrum, but for coarse questions like "is alpha power higher or lower than beta power?" it is usually not the limiting factor. Electrode contact quality, muscle artifact, and eye movement contamination are all larger sources of error.

For **alpha/theta crossover** (detecting the transition into hypnagogic states): This requires detecting the moment theta power begins to exceed alpha power. When both are near the noise floor, the crossover point can be obscured by quantization noise. With 24-bit you'd see a clean crossover; with 12-bit it may look like noisy toggling. This is the specific feature most affected by ADC resolution.

### 3.4 Real-World Signal Quality Ranking

The sources of signal degradation you will actually encounter, from most to least impactful:

1. **Electrode contact** — dry electrodes move, sweat changes impedance, hair interferes. This dwarfs all other noise sources. Good electrode prep can improve signal quality more than upgrading from 12-bit to 24-bit ADC.

2. **Muscle artifact** — frontalis (forehead) EMG contaminates frontal EEG. When the user furrows their brow, the "EEG" is mostly EMG. This is a fundamental problem with frontal-only placement regardless of ADC resolution.

3. **Eye movement / blink artifact** — massive voltage excursions that blow out the signal for hundreds of milliseconds. The NPG Lite's EOG detection firmware exists partly because blinks are so visible in frontal EEG that you might as well use them as a feature.

4. **Mains hum (50/60 Hz)** — the firmware has notch filtering for this.

5. **ADC quantization noise** — real, measurable, but rarely the binding constraint on whether feature extraction works or not.

### 3.5 The Practical Bottom Line

12-bit is a real limitation but not the binding limitation. ECG and EMG will be excellent. EEG will be adequate for coarse state classification (alpha/theta ratio, beta power) and questionable for subtle state transitions (precise crossover detection).

For the daydream conductor, EEG features map to continuous, temporally-smoothed variables — depth of absorption, focused vs. diffuse attention. These are inherently fuzzy quantities averaged over 10-30 seconds. Temporal smoothing papers over a lot of quantization noise.

**If you hit the ceiling** — EEG-derived conductor variables too noisy, alpha/theta mapping unreliable — the upgrade path is an ADS1299-based board (the industry standard EEG analog front-end, 24-bit, 8 channels). Different price point ($500+), different complexity. Start with NPG Lite. Discover what features actually matter. Upgrade the hardware only if the features you need are limited by ADC resolution rather than by electrode contact or artifact rejection.

### 3.6 The Logging Imperative

The most useful early investment: log everything. Raw signals, extracted features, conductor state, graph traversal decisions. When tuning the mapping, you need to answer: "Was the feature extraction the bottleneck, or was it the mapping from features to conductor state?" If features are clean but the mapping is wrong, better hardware won't help. If features are noisy and you can identify quantization noise in the spectrum, then the ADC matters.

The LSL (Lab Streaming Layer) path supports this directly — every stream timestamped to a shared clock. Raw ECG, extracted HR, conductor tension, graph node ID, all aligned on a single timeline.

---

## Part IV: DIY Biofeedback

### 4.1 The Conducted Daydream as Biofeedback System

Traditional biofeedback: measure a physiological signal, process it, present it back to the user as a number or simple visualization. The user learns to modulate the signal.

The conducted daydream is biofeedback where the feedback IS the daydream. You don't watch a number go up. You watch the daydream respond to your internal state. As you relax and HRV increases, the music opens up, the visuals soften. As you enter alpha/theta crossover, the rendering becomes more dreamlike — which reinforces the state, which deepens the crossover, which makes the rendering more dreamlike. Virtuous feedback loop where the reward is the experience, not a metric.

This addresses the biofeedback literature's biggest problem: adherence. People stop doing biofeedback because it's boring. A system where the feedback is an immersive audiovisual experience that gets more beautiful and personally meaningful as you achieve the target physiological state — that's something people would want to do repeatedly.

### 4.2 Established Biofeedback Modalities Accessible via NPG Lite

**HRV Biofeedback (ECG — Tier 1)**

The most accessible and best-evidenced modality. Measure beat-to-beat heart rate intervals, compute HRV in real-time, feed back. The user learns to increase HRV through slow breathing (~6 breaths/min, the resonance frequency of the baroreflex). High HRV correlates with parasympathetic activation — rest, recovery, emotional regulation.

This is the basis of HeartMath and similar commercial systems. With NPG Lite you have research-grade ECG and Chords-Python for the data stream. The core implementation is ~50 lines of R-peak detection and inter-beat interval computation.

```python
class HRVBiofeedback:
    """Minimal HRV biofeedback — standalone useful tool."""

    def __init__(self, chords_stream):
        self.stream = chords_stream
        self.rr_intervals = []

    def detect_r_peaks(self, ecg_buffer):
        """Simple threshold-based R-peak detection.
        Replace with Pan-Tompkins for production."""
        # derivative → square → integrate → threshold
        # with refractory period (~200ms minimum RR interval)
        pass

    def compute_rmssd(self, window_seconds=30):
        """RMSSD: standard short-term HRV metric.
        Root mean square of successive RR differences."""
        if len(self.rr_intervals) < 10:
            return None
        diffs = [
            abs(self.rr_intervals[i] - self.rr_intervals[i-1])
            for i in range(1, len(self.rr_intervals))
        ]
        return sqrt(mean([d**2 for d in diffs]))

    def coherence_score(self):
        """Respiratory sinus arrhythmia (RSA) coherence.
        High when breathing is slow and rhythmic (~6/min).
        This is what HeartMath measures."""
        # FFT of RR interval time series
        # Peak power in 0.04-0.15 Hz band = high coherence
        # Ratio of peak power to total power = coherence score
        pass
```

HRV coherence maps naturally to `clarity` — high coherence = clear, focused, receptive attention. Low coherence = scattered, anxious, ungrounded.

**EMG Biofeedback (EMG — Tier 1)**

Place electrodes on frontalis (forehead), trapezius (shoulders), or masseter (jaw). Feed back the tension level. People carry chronic tension they're completely unaware of. Seeing or feeling their own muscle tension in real-time teaches them to release it.

Clinically used for headache, TMJ, anxiety, and chronic pain.

In the daydream system, the feedback is automatic: high muscle tension triggers the `hold` state, which fixates the daydream on charged material. Releasing the tension releases the daydream. The user implicitly learns tension awareness through the experience of the daydream responding to their body.

**EEG Neurofeedback (EEG — Tier 3)**

The deep end. Established protocols and their relevance to daydream conducting:

**Alpha training** — reward increases in alpha power (8-12 Hz) at posterior sites. Alpha is associated with relaxed alertness. The user learns to enter a calm-focused state. Clinical uses: anxiety, ADHD, peak performance.

**Alpha/theta training** — reward the crossover point where theta (4-8 Hz) begins to exceed alpha. This is the hypnagogic edge — the boundary between waking and sleep where imagery becomes vivid and unbidden. Clinical uses: PTSD, addiction, creativity enhancement. This maps directly onto the daydream system: training the user to enter a state where daydreaming is neurologically facilitated. The system responds by deepening the dreamlike quality of the rendering, which reinforces the state. The biofeedback loop and the conducting loop become the same thing.

**Beta training** — reward sustained beta (12-30 Hz) for focused attention. The NPG Lite firmware already includes beta power extraction (`Serial-FFT`, `BLE-BCI-Server-Toggle`).

**SMR training** — reward sensorimotor rhythm (12-15 Hz) over the motor cortex. Associated with calm physical stillness with mental alertness.

### 4.3 Biofeedback Quality and the 12-bit Boundary

HRV biofeedback: **fully viable** at 12-bit. HR and HRV are timing features (when do R-peaks occur?), not amplitude features. Quantization doesn't affect peak timing.

EMG biofeedback: **fully viable** at 12-bit. Envelope detection (RMS) is a coarse amplitude feature. More than adequate resolution.

Alpha/theta neurofeedback: **viable with caveats** at 12-bit. Coarse band power ratios work. Precise crossover detection may be noisy. Temporal smoothing (10-30 second windows) helps. Electrode contact and muscle artifact are bigger concerns than ADC resolution.

Beta neurofeedback: **viable** at 12-bit. Beta power is a relatively large-bandwidth feature. The existing NPG firmware already demonstrates beta power detection working on this hardware.

### 4.4 The Deeper Implication

The session-over-session world state adds a dimension to biofeedback that doesn't exist in traditional clinical practice. Your biofeedback performance literally shapes the narrative. Sessions where you achieve deep relaxation produce different daydream traversals than sessions where you're tense. The world accumulates differently depending on your physiological state.

A session where the user enters alpha/theta crossover allows the daydream to drift into more associative, more creative territory — which means the daydream graph traverses more unexpected edges, which means more novel connections surface, which means the daydream log records more interesting material, which means the next session has richer source material.

A session where the user is tense and unable to relax keeps the daydream close to attractor nodes — charged, anxious, repetitive. The world doesn't advance. The situations ripen further. The next session starts with higher tidal pressure.

Your nervous system is not just conducting the daydream — it's authoring the story.

---

## Part V: Software Integration Path

### 5.1 Data Path: NPG Lite → Conductor State

Two approaches, depending on whether logging is prioritized:

**Path A: Direct — Chords-Python → BiometricProcessor**

```
NPG Lite ──► BLE ──► Chords-Python ──► BiometricProcessor
                                             │
                                             ▼
                                       ConductorState
```

Simplest. Chords-Python gives you raw sample data. You compute features in your own code. The daydream performance runtime is already Python async, so `chords_ingestion` becomes another asyncio task alongside video and Lyria tasks. No middleware.

**Path B: LSL — Chords LSL Connector → pylsl → BiometricProcessor**

```
NPG Lite ──► BLE/WiFi ──► Chords LSL Connector ──► LSL stream
                                                        │
                                                        ▼
                                                  pylsl inlet
                                                        │
                                                        ▼
                                                 BiometricProcessor
                                                        │
                                                        ▼
                                                  ConductorState
                                                  
All streams → LSL recording → synchronized offline analysis
```

More infrastructure, but: synchronized timestamps across all streams, standard recording format, ability to replay sessions offline. Since the logging imperative is established — and the key diagnostic question is "was feature extraction or mapping the bottleneck?" — LSL gives you aligned multi-stream logging for free.

**Recommendation:** Start with Path A for the first evening spike (get something working fast). Move to Path B before serious parameter tuning begins.

### 5.2 Feature Extraction Pipeline

```python
class BiometricProcessor:
    """Receives NPG Lite data via Chords-Python or LSL.
    Computes features. Outputs conductor state deltas."""

    def __init__(self):
        self.hr_baseline = None
        self.hrv_baseline = None
        self.emg_baseline = None
        self.calibrated = False

    async def calibrate(self, stream, duration=60):
        """60 seconds of quiet rest to establish personal baselines."""
        ecg_buffer = []
        emg_buffer = []
        async for sample in stream.read(duration):
            ecg_buffer.append(sample.channels[0])  # ECG on ch1
            emg_buffer.append(sample.channels[1])   # EMG on ch2

        # HR baseline from R-peak detection
        peaks = pan_tompkins(ecg_buffer, sample_rate=250)
        rr_intervals = diff(peaks) / 250.0  # seconds
        self.hr_baseline = 60.0 / median(rr_intervals)
        self.hrv_baseline = compute_rmssd(rr_intervals)

        # EMG baseline from RMS
        self.emg_baseline = rms(emg_buffer)
        self.calibrated = True

    async def stream_conductor_deltas(self, stream):
        """Continuous stream of conductor state influences."""
        ecg_window = deque(maxlen=250 * 30)   # 30s ECG buffer
        emg_window = deque(maxlen=250 * 2)     # 2s EMG buffer
        eeg_window = deque(maxlen=250 * 4)     # 4s EEG buffer

        async for sample in stream.read():
            ecg_window.append(sample.channels[0])
            emg_window.append(sample.channels[1])
            if len(sample.channels) > 3:
                eeg_window.append(sample.channels[3])

            # --- ECG features (every heartbeat, ~1 Hz) ---
            hr, hrv = self.extract_hr_hrv(ecg_window)

            # --- EMG features (continuous, ~20 Hz update) ---
            emg_rms = rms(list(emg_window))

            # --- EEG features (every 4s window) ---
            eeg_features = None
            if len(eeg_window) >= 250 * 4:
                eeg_features = self.extract_eeg_bands(eeg_window)

            yield {
                "tension": self.hrv_to_tension(hrv),
                "energy": self.hr_trend_to_energy(hr),
                "hold": emg_rms > self.emg_baseline * 1.5,
                "clarity": self.coherence_to_clarity(ecg_window),
                "absorption": (
                    self.alpha_theta_to_absorption(eeg_features)
                    if eeg_features else None
                ),
                "timestamp": sample.timestamp,
            }

    def hrv_to_tension(self, hrv):
        """Low HRV = high tension. Normalized to personal baseline."""
        if self.hrv_baseline is None or hrv is None:
            return 0.5
        return clamp(1.0 - (hrv / self.hrv_baseline), 0.0, 1.0)

    def hr_trend_to_energy(self, hr):
        """Rising HR over 30s window = rising energy."""
        self.hr_history.append(hr)
        if len(self.hr_history) < 30:
            return 0.5
        slope = linear_regression_slope(self.hr_history[-30:])
        return clamp(0.5 + slope * 10, 0.0, 1.0)

    def extract_eeg_bands(self, eeg_buffer):
        """FFT → band power for alpha, theta, beta."""
        spectrum = fft(list(eeg_buffer), sample_rate=250)
        return {
            "theta": band_power(spectrum, 4, 8),
            "alpha": band_power(spectrum, 8, 12),
            "beta": band_power(spectrum, 12, 30),
        }

    def alpha_theta_to_absorption(self, bands):
        """Theta/alpha ratio. High = hypnagogic, deep daydream."""
        if bands["alpha"] == 0:
            return 0.5
        ratio = bands["theta"] / bands["alpha"]
        return clamp(ratio / 2.0, 0.0, 1.0)  # normalize ~0-2 range
```

### 5.3 Integration with Performance Runtime

The BiometricProcessor becomes one of several async tasks in the performance loop:

```python
async def perform_with_biometric(
    graph: DaydreamGraph,
    video_pipe: VideoPipeline,
    lyria_session: LyriaSession,
    npg_stream: ChordsStream,  # NPG Lite via Chords-Python
    tides: TidalField,
):
    processor = BiometricProcessor()
    conductor = ConductorState()

    # Calibrate during pre-session quiet
    await processor.calibrate(npg_stream, duration=60)

    async with asyncio.TaskGroup() as tg:
        # Task 1: Biometric ingestion → conductor updates
        tg.create_task(biometric_loop(processor, npg_stream, conductor))

        # Task 2: Graph traversal → node selection
        tg.create_task(traversal_loop(graph, conductor, tides))

        # Task 3: Video rendering
        tg.create_task(video_loop(graph, conductor, video_pipe))

        # Task 4: Audio rendering (Lyria)
        tg.create_task(audio_loop(graph, conductor, lyria_session))

        # Task 5: Logging
        tg.create_task(log_loop(conductor, graph, processor))
```

### 5.4 Haptic Feedback Channel

The VibZ+ playmate's vibration motor provides basic haptic feedback. Events in the daydream can pulse back to the body:

| Daydream Event | Haptic Response |
|---------------|----------------|
| Tide cresting | Gentle sustained pulse, intensity proportional to tide pull |
| Choice point approaching | Subtle rhythm change (from steady to intermittent) |
| Event node triggered / state mutation | Single sharp pulse — something happened |
| Hold state entering | Motor mirrors breathing rhythm — slow throb |
| Resolution / tension release | Fade-out pulse — relaxation cue |

This closes the loop bidirectionally: the body shapes the daydream, and the daydream shapes the body (through haptic cues that influence breathing, tension, attention).

---

## Part VI: Formal Verification Horizon — LLM Guardrails via Theorem Proving

### 6.1 The Problem: Probabilistic Generators Need Deterministic Guardrails

The daydream engine's scenario generator is an LLM. It is probabilistic, creative, and unconstrained. It does not *know* that a character can't reference information gated behind an event that hasn't occurred. It will sometimes hallucinate knowledge, break character constraints, or propose impossible state transitions.

The standard mitigation is prompt engineering — include rules in the prompt and hope the model follows them. This works most of the time but fails probabilistically, which is exactly the failure mode you cannot tolerate in a persistent world where state mutations are irreversible.

### 6.2 The Architecture: Propose → Validate → Admit

```
SCENARIO GENERATOR (LLM — probabilistic, creative)
        │
        │ proposed nodes (may violate invariants)
        ▼
FORMAL VALIDATOR (deterministic, proven properties)
        │
        │ reject → regenerate with violation noted in prompt
        │
        │ admit
        ▼
DAYDREAM GRAPH (guaranteed consistent)
```

Every node the scenario generator proposes passes through formal checking before admission. Every event mutation is verified against invariants before application. The LLM proposes; the verifier disposes.

### 6.3 The Interface Boundary

The world state engine exposes a small, stable API. The implementation behind this interface can evolve from a Python dict to a rule engine to a formally verified module without any other system component changing.

```python
class WorldStateEngine:
    """The boundary. Implementation changes; interface does not."""

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
        Checks: character knowledge gates, behavioral constraints,
        world rules, temporal consistency."""

    def contradicts(self, node: DaydreamNode) -> bool:
        """Does this node imply state that doesn't hold?
        Used for canonical vs counterfactual rendering."""

    def situation_ripeness(self, situation_id: str) -> float:
        """Current ripeness of this situation."""

    def available_events(self, conductor: ConductorState) -> list[Event]:
        """Which events could fire right now?"""
```

### 6.4 Three Implementation Tiers

**Tier 1: Sprint version (Python dict + asserts)**

Good enough for prototyping. World state is a nested dict. Validation is imperative checks.

```python
class DictWorldState(WorldStateEngine):
    def __init__(self, path: str):
        self.state = yaml.safe_load(open(path))

    def validate_node(self, node):
        violations = []
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

World rules expressed declaratively. Queryable. Composable. The validator returns proof traces: *why* was this node rejected?

```lisp
;; Knowledge rules
(rule can-know (?character ?fact)
  (event-completed ?event)
  (event-grants ?event ?character ?fact))

;; Character constraint rules
(rule behavior-violation (?character ?action ?node)
  (node-implies-action ?node ?character ?action)
  (character-will-not ?character ?action))

;; World physics rules
(rule conversation-must-not-fully-resolve (?node)
  (node-type ?node conversation)
  (node-resolves-all-tension ?node))

;; Validation: admissible iff no violation rule fires
(query admissible (?node)
  (not (knowledge-violation ?node _))
  (not (behavior-violation _ _ ?node))
  (not (world-physics-violation ?node)))
```

This doesn't need ACL2 — a small Datalog or Lisp DSL suffices. But it's shaped like something ACL2 could reason about, making the transition to Tier 3 a formalization of existing rules rather than a rewrite.

**Tier 3: Formally verified properties (ACL2)**

Prove that the world state machine, given Tier 2 rules, satisfies global invariants for all reachable states.

### 6.5 Invariant Catalog

**Safety properties (bad things never happen):**

```
no-knowledge-without-source:
  For all characters C and facts F,
  if (knows C F) then there exists an event E such that
  (event-completed E) and (event-grants E C F).
  "No character knows something without a reason."

character-consistency:
  For all nodes N admitted to the graph,
  no behavioral constraint of any character referenced
  in N is violated by N.
  "Characters always act in-character."

tide-boundedness:
  For all reachable states S,
  the sum of all active tide tension contributions ≤ 1.0.
  "The emotional system can't overflow."
```

**Liveness properties (good things eventually happen):**

```
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
```

**Consistency properties:**

```
resolution-kills-tide:
  If situation.status = resolved then tide-active = false.
  "Resolved situations don't haunt."

counterfactual-detection-completeness:
  For all nodes N and world states W,
  if N implies any state variable differing from W,
  then contradicts(N, W) = true.
  "Every memory/fantasy is correctly identified."
```

### 6.6 Why ACL2 Specifically

ACL2 is well-suited because:

**The state space is finite and well-structured.** World state is a nested record with enumerated fields. Characters, knowledge facts, and events are all finite catalogs. This is exactly the kind of state machine ACL2 reasons about.

**The properties are first-order.** All invariants above are expressible in first-order logic.

**The verification is compositional.** Prove the knowledge system consistent, the tide system bounded, the event system precondition-preserving — then compose.

**Counterexamples are meaningful.** If ACL2 finds a sequence of events producing a state where a character knows something without a source — that's a real bug in the world bible, a narrative inconsistency, not a theoretical concern.

### 6.7 The Collaboration Interface

For a collaborator working in ACL2:

**Receives from the system:**
- World state schema (YAML structure → record type)
- Event catalog (preconditions + mutations)
- World rules (Tier 2 logic rules → axioms)
- Character constraints (behavioral invariants)
- Tide system specification (oscillator parameters, coupling rules)

**Produces:**
- Proofs that invariants hold for all reachable states
- Counterexamples when invariants are violated (= world bible bugs)
- Optionally: verified validator function callable from Python

**Does not need to verify:**
- The daydream engine (probabilistic by design; the validator catches mistakes)
- The graph structure (validated on node admission)
- The rendering pipeline (aesthetic, not logical)
- The conductor state (continuous, bounded by clamp)
- The biometric input (physical signals, not logical propositions)

The scope is tight: the world state machine and its transitions.

### 6.8 Integration Path

**Sprint (weeks 1-2):**
- Build `DictWorldState` — Python dict, imperative validation
- Write invariants as comments in code and prose in world bible
- Log violations from scenario generator (how often does the LLM break constraints?)

**Post-sprint (month 1-2):**
- Implement Tier 2 rule engine
- Express rules as logic, validator returns proof traces
- Share rule set with ACL2 collaborator as formalization target

**Formal verification (month 3+):**
- ACL2 formalization of world state machine
- Prove safety and liveness properties
- Counterexamples → world bible corrections
- Optionally: generate verified validator from ACL2 spec

### 6.9 The General Pattern

The architecture — probabilistic generator → formal validator → guaranteed-consistent output — is applicable beyond this system. It is a general pattern for any application where LLMs produce structured output that must satisfy invariants: agent workflows with preconditions, code generation with type checking, planning systems with physical constraints, game AI with world rules.

The conducted daydreaming system is a concrete, bounded, aesthetically motivated instance. The world is small enough to verify. The stakes (narrative consistency across persistent sessions) are high enough to justify the effort. And the failure mode (a character references knowledge they shouldn't have) is immediately obvious to any viewer, making verification practically meaningful.

---

## Appendix A: Recommended Purchase Order

| Item | Price | Notes |
|------|-------|-------|
| NPG Lite — Beast Pack | $240 | 6ch, VibZ+ playmate, case, battery, cables, electrodes |
| BioAmp Accessory Bundle | $43 | Dry electrode bands (ECG/EMG/EEG), skin prep kit |
| Baby Gel Electrodes (100 pack) | $20 | Clean signal during dev/calibration |
| Snap Cables (10 pack) | $20 | Flexible electrode placement |
| **Subtotal** | **$323** | |
| US Shipping | $8 | |
| **Total** | **$331** | |

Source: https://www.crowdsupply.com/upside-down-labs/neuro-playground-lite

## Appendix B: Firmware Quick Reference

Key NPG Lite firmware for the conducted daydreaming use case:

| Firmware | Use |
|----------|-----|
| `BLE-Server` | Stream raw multi-channel data over BLE — primary data path |
| `NPG-LITE-BLE` | Chords-compatible BLE firmware — use with Chords-Python |
| `NPG-LITE-WIFI` | Chords-compatible WiFi firmware — lower latency option |
| `Serial-FFT` | On-device FFT and band power — useful for EEG feature prototyping |
| `BCI-Blink-Serial` | Blink detection — for EOG-based conducting gestures |
| `BLE-BCI-Server-Toggle` | Beta power triggers over BLE — for focus detection |
| `Gyro-Motion-Detection` | MPU6050 accelerometer streaming — if IMU is connected |

## Appendix C: Relationship to Project Documents

| Document | Contribution |
|----------|-------------|
| `conducted-daydreaming-architecture.md` | Core two-phase pipeline, graph structure, vault memory, rendering translators. This companion specifies the hardware that feeds the conductor state. |
| `conducted-daydreaming-interaction.md` | World state, event nodes, choice points, session persistence, experience design. This companion specifies the physical interface and formal verification layer. |
| `instrument-stack-architecture.md` | Conductor state vector definition. This companion maps biological signals onto those variables. |
| `ims-exploration-notes.md` | IMS 5-layer architecture, "learnable in 10s" design principle, latency targets. The NPG Lite + Chords stack implements Layer 1 of this architecture. |
| `rust-embedded-futures-research-notes.md` | Embassy async on nRF52840. Relevant if the two-device architecture (NPG Lite + nRF52840 IMU/haptic pod) is pursued. Not needed for the biometric conducting MVP. |
| `conductor-landscape.md` | Gesture capture and mapping. Wekinator for ML-based gesture→control training — applicable to biometric→conductor mapping if hand-tuned rules prove insufficient. |
