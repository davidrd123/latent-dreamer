# Conducted Daydreaming: Architecture for a Generative Stream of Thought

## Companion to: `generative-steering.md`, `instrument-stack-architecture.md`, `narrative-conducting.md`, `conductor-landscape.md`

---

## Part I: DAYDREAMER and the Computational Theory of Daydreaming

### 1.1 What Mueller Built (1983–1990)

Erik Mueller and Michael Dyer at UCLA built DAYDREAMER, a cognitive architecture implemented as 12,000 lines of Lisp that modeled the human stream of thought. The system was presented at IJCAI in 1985 and published as a book (*Daydreaming in Humans and Machines*, Ablex, 1990).

DAYDREAMER's thesis: daydreaming is not noise — it is a collection of cognitive functions essential to intelligent behavior:

- **Plan preparation and rehearsal** — mentally simulating future actions
- **Learning from failures and successes** — replaying outcomes to extract lessons
- **Support for processes of creativity** — associative juxtaposition of unrelated material
- **Emotion regulation** — processing emotional states through imagined scenarios
- **Motivation** — connecting current state to desired goals through imagined paths

The system operated in two modes: *daydreaming mode* (continuous generation until interrupted) and *performance mode* (demonstrating that it had learned from daydreaming). It took situational descriptions as input ("accidentally meeting a movie star," "being fired from a job") and produced actions and daydreams in English.

### 1.2 DAYDREAMER's Five Components

| Component | Function |
|-----------|----------|
| **Scenario Generator** | Produced imagined sequences using "relaxed planning" — planning with constraints loosened so that implausible but emotionally meaningful paths could be explored |
| **Episodic Memory** | A dynamic database of experiences indexed for retrieval. Daydreamed events were incorporated alongside real events, creating a self-modifying memory |
| **Personal Goals** | Health, food, sex, friendship, love, possessions, self-esteem, social esteem, enjoyment, achievement — drawn from Maslow's hierarchy |
| **Control Goals** | Strategies for *what to daydream about*, triggered by emotions: **rationalization** (reframing failure), **failure/success reversal** (imagining different outcomes), **revenge** (imagining retaliation), **preparation** (rehearsing future actions) |
| **Domain Knowledge** | Hand-coded representations of interpersonal relations and common everyday situations — the "world model" the scenario generator drew from |

### 1.3 What Mueller Got Right (and Neuroscience Confirmed)

Two decades after DAYDREAMER, neuroscience discovered the Default Mode Network (DMN) — a large-scale brain network active during rest, mind-wandering, and internally directed thought. The DMN's documented functions map almost exactly onto Mueller's postulated roles:

| Mueller's Postulated Function | DMN Research Finding |
|-------------------------------|---------------------|
| Plan preparation and rehearsal | DMN active during future simulation and prospection |
| Learning from failures/successes | DMN integrates episodic memory with self-referential processing |
| Support for creativity | Mind-wandering linked to creative insight; DMN facilitates novel connections between disparate ideas |
| Emotion regulation | DMN constructs and maintains the "internal narrative" central to sense of self and emotional processing |
| Motivation | DMN active during goal-directed internal thought, connecting past experience to future planning |

The DMN comprises multiple interacting subsystems — not a single process but a polyphonic network where memory retrieval, social cognition, future simulation, and self-referential processing operate simultaneously and influence each other. This polyphonic structure is critical for what follows.

### 1.4 What Mueller Could Not Build

DAYDREAMER hit five fundamental constraints, all now resolved:

**Constraint 1: Hand-coded domain knowledge.** The scenario generator could only produce scenarios involving entities and relations that the programmers had explicitly represented. The system's imagination was bounded by its vocabulary.

*Resolution:* Language models have internalized millions of scenarios, causal chains, emotional dynamics — not as discrete rules but as continuous distributions over possible continuations. The domain knowledge is the model's weights.

**Constraint 2: Discrete emotional labels.** Emotions were categories (anxiety, anger, joy) that triggered specific control goals via lookup. "Failure → negative emotion → revenge daydream OR rationalization daydream."

*Resolution:* Continuous affect representation. Instead of discrete labels, a vector of continuous variables (tension, energy, clarity, novelty, etc.) can modulate generation along multiple simultaneous dimensions.

**Constraint 3: Symbol-to-symbol episodic memory.** Retrieval required hand-coded index frames. Episodes were retrieved by exact feature match, not by associative resonance.

*Resolution:* Vector embedding stores enable retrieval by semantic similarity, emotional tone, structural pattern — all the associative dimensions that make human memory retrieval feel like "this reminds me of that" rather than "this matches that query."

**Constraint 4: No temporal substrate.** DAYDREAMER was event-driven: situational input → emotion → control goal → scenario. There was no continuous background process, no slow emotional weather, no involuntary return to unresolved concerns over time.

*Resolution:* Tidal oscillators — slow-timescale arcs that create gravitational pull independent of moment-to-moment content (detailed in Part II).

**Constraint 5: No multimodal output.** DAYDREAMER produced English text describing daydreams. Actual daydreaming is multimodal: visual scenes, auditory textures, felt senses, spatial experience.

*Resolution:* Real-time generative video and audio systems that accept text prompts as control interfaces, enabling the same scenario representation to drive visual and auditory rendering simultaneously.

---

## Part II: The Conducting Framework Applied to Daydreaming

### 2.1 The Core Analogy

The existing conductor architecture (documented in `instrument-stack-architecture.md`) was designed for steering generative systems through constraint and selection. It identifies five components:

1. **Score** — Prepared constraints (degrees of freedom, not fixed content)
2. **Conductor** — Low-dimensional continuous authority (pressure, not selection)
3. **Ensemble** — Distributed agents with budgets and contracts
4. **Stage** — Renderers (text, video, audio output)
5. **Log** — Preserved counterfactuals and state history

Daydreaming maps onto this architecture with startling precision:

| Instrument Stack Component | Daydreaming Equivalent |
|---------------------------|----------------------|
| **Score** | The vault — personal knowledge, memories, open questions, unresolved situations. Not a script but a space of possible material. |
| **Conductor** | Emotional state — continuous affect vector that modulates which material surfaces and how it's processed. Not choosing content but shaping energy, attention, mood. |
| **Ensemble** | Cognitive subsystems — memory retrieval, future simulation, social modeling, affect processing, creative association. Each operates semi-independently, responding to conductor pressure. |
| **Stage** | Multimodal experience — the felt, seen, heard quality of the daydream. In our system: video pipeline + Lyria RealTime audio. |
| **Log** | The daydream graph — branching record of what was imagined, what alternatives existed, where attention dwelled and where it moved. |

### 2.2 The Conductor State Vector as Affect Model

The conductor state vector from the instrument stack architecture doubles as the daydream's emotional modulation layer:

```
conductor_state = {
    energy: float,      # vividness and pace of scenario generation
    tension: float,     # unresolved goal pressure — what you keep returning to
    density: float,     # how much is happening simultaneously in the daydream
    clarity: float,     # abstract worry vs concrete rehearsal; vivid vs diffuse
    novelty: float,     # how far from established schemas the daydream wanders
    tempo: float,       # pacing — how fast scenes shift
    phase: float,       # position in emotional processing arc
    hold: bool,         # fixation — the daydream is stuck replaying this moment
    focus: vector,      # attention locus — what/who the daydream is about right now
}
```

These variables don't describe what the daydream is *about* — they describe *how it feels*. The content comes from the vault and the scenario generator. The conductor state shapes the character of generation and retrieval.

### 2.3 The Ensemble as Cognitive Subsystems

Following the multi-agent architecture from `instrument-stack-architecture.md`, each cognitive function in daydreaming becomes an agent with a contract and a budget:

**Memory Agent**
- Role: Retrieve resonant episodes from the vault
- Responds to: tension (retrieves charged material), clarity (concrete vs. abstract memories), novelty (familiar vs. forgotten material)
- Contract: must return material with real provenance in the vault; never fabricates memories
- Budget: increases when energy is low and clarity is high (reflective states)

**Planning Agent**
- Role: Project goal-relevant futures; simulate "what if" scenarios
- Responds to: tension (urgency of rehearsal), energy (vividness of simulation), phase (early = broad exploration, late = convergence)
- Contract: scenarios must be plausible given vault contents; must connect to actual goals
- Budget: increases during preparation control goal; decreases during low-energy states

**Social Agent**
- Role: Model others' responses; generate imagined conversations
- Responds to: tension (conflict modeling), clarity (specific person vs. abstract other), focus (who is being modeled)
- Contract: must maintain character consistency for modeled people (if vault contains information about them)
- Budget: increases when vault retrieval surfaces interpersonal material

**Affect Agent**
- Role: Generate emotional elaboration; amplify or soothe the emotional register
- Responds to: all conductor state variables (this agent is the most coupled)
- Contract: bounded by the emotional realism of the overall system — cannot produce emotional states without causes
- Budget: always nonzero (there is always emotional color in daydreaming)

**Association Agent**
- Role: Surface unexpected connections between disparate material; the serendipity engine
- Responds to: novelty (primary driver), density (more cross-connections at high density), hold (suppressed during fixation — fixation narrows association)
- Contract: connections must have structural basis (not random juxtaposition)
- Budget: increases when novelty is high and hold is false; suppressed during rumination

### 2.4 Tidal Oscillators as Emotional Weather

The critical addition to Mueller's architecture. Tides are slow-timescale arcs generated by unresolved vault items — open projects, pending decisions, unprocessed emotional events. They create involuntary gravitational pull on the daydream stream.

```python
class DaydreamTide:
    name: str               # "deadline-pressure", "unresolved-argument"
    source_note: str        # vault note that generates this tide
    tension_contribution: float  # how much tension this adds at peak
    period_seconds: float   # cycle length (60–300s typical)
    current_phase: float    # 0.0–1.0
    decay_rate: float       # resolved items decay; unresolved persist
    
    coupling: dict          # how it biases conductor state at peak
    # e.g. {"tension": 0.4, "clarity": -0.2, "hold": 0.3, 
    #        "retrieval_bias": "project-plan.md"}
```

Key behaviors:

- **Tidal cresting** — when a tide reaches peak phase, it biases memory retrieval toward its source material regardless of the daydream's current content. This is the "I keep thinking about that deadline" phenomenon.
- **Tidal interference** — when multiple tides align (unresolved argument + deadline pressure both peaking), the system enters high-tension states that may trigger hold (fixation/rumination).
- **Tidal decay** — resolved items lose pull strength. Writing a reflection in the vault about a processed experience reduces its tidal contribution. Completing a project eliminates its tide.
- **Tidal initialization** — the system scans the vault for items with `status: active`, `emotional_tone: unresolved`, or recent high-frequency access, and generates tides proportional to their stakes and recency.

---

## Part III: The Two-Phase Architecture

### 3.1 The Key Insight: Preparation vs Performance

The conducting framework's central distinction applies to daydreaming directly:

- **Preparation** = the daydreaming itself (slow, generative, branching, expensive)
- **Performance** = real-time traversal of the daydream's output (fast, navigational, lightweight)

The vault goes in. A prolonged generative process produces a **daydream graph** — a densely connected structure of scenes, moods, transitions, and associations. The real-time system then performs through that graph, rendering each node into video and audio simultaneously.

The daydream engine does NOT run during performance. It already ran. It produced a rich navigable structure. Performance is graph traversal, not generation.

### 3.2 Phase 1: Daydreaming (Offline, Extended, Generative)

```
VAULT ──► DAYDREAM ENGINE ──► DAYDREAM GRAPH
(input)    (extended LLM         (output: the score)
            generation,
            branching,
            tidal cycles,
            serendipity)
```

The engine runs for an extended period — minutes to hours — processing the vault through emotional filters and associative walks. It produces not a linear sequence but a graph where each node is a fully specified daydream moment and each edge is a possible transition.

#### The Daydream Engine Loop

```python
async def daydream_over_vault(
    vault: Vault, 
    config: DaydreamConfig
) -> DaydreamGraph:
    
    graph = DaydreamGraph()
    memory = VaultMemory(vault)
    tides = TidalField.from_vault(vault)
    conductor = ConductorState()
    
    for cycle in range(config.num_cycles):
        # 1. Update emotional substrate
        tides.update(dt=config.cycle_duration)
        conductor.update_from_tides(tides)
        
        # 2. Retrieve vault material shaped by current affect
        fragments = memory.retrieve(
            state=conductor,
            mode="dual",  # both embedding + graph walk
            n=5
        )
        
        # 3. Generate multiple possible next moments
        candidates = await scenario_generator.generate(
            conductor_state=conductor,
            recent_path=graph.recent_path(),
            vault_fragments=fragments,
            num_candidates=config.branching_factor,
        )
        
        # 4. ALL candidates become nodes (we don't discard branches)
        for candidate in candidates:
            node = graph.add_node(candidate)
            edges = compute_transitions(
                node, graph.existing_nodes, conductor
            )
            graph.add_edges(node, edges)
        
        # 5. Advance along one path for generation coherence
        #    but preserve all branches for later traversal
        chosen = select_for_continuation(candidates, conductor)
        graph.set_current(chosen)
        conductor.apply_delta(chosen.conductor_delta)
    
    return graph
```

#### The Scenario Generator Prompt

Each cycle, the LLM receives:

```
You are generating moments in a continuous daydream that draws 
on personal material from a knowledge vault.

Current emotional state:
  tension: {tension}, energy: {energy}, clarity: {clarity}
  novelty: {novelty}, phase: {phase}, hold: {hold}

Tidal pressures:
  {dominant_tide.name} at {dominant_tide.pull()} pull strength
  Source: "{dominant_tide.source_note}"

Recent daydream sequence (last 3 moments):
  [1] {recent_node_1.scene} — {recent_node_1.mood}
  [2] {recent_node_2.scene} — {recent_node_2.mood}
  [3] {recent_node_3.scene} — {recent_node_3.mood}

Retrieved vault fragments:
  [semantic] {fragment_1.content}
  [graph walk, 2 hops] {fragment_2.content}
  [tidal intrusion] {fragment_3.content}

Generate {branching_factor} possible next daydream moments.
Each moment should include:
  - scene (visual description, 1-2 sentences)
  - characters (who is present, can be abstract)
  - action (what is happening or changing)
  - mood (short phrase capturing emotional quality)
  - visual_texture (cinematographic quality)
  - audio_texture (sonic quality — instruments, ambience, silence)
  - conductor_delta (how this moment shifts tension/energy/clarity)
  - vault_resonance (which vault material this connects to)

The moments should span a range: at least one that continues 
the current trajectory, at least one that diverges, and at least 
one that responds to tidal pressure.
```

The LLM returns structured moments. Each becomes a graph node. The engine computes edges between the new nodes and existing nodes based on emotional proximity, thematic connection, and transition plausibility.

### 3.3 The Daydream Graph Structure

#### Node Specification

```yaml
node:
  id: string                    # unique identifier
  
  # Content
  scene: string                 # "standing at a window, rain on glass, distant city lights"
  characters: list[string]      # ["self", "unnamed_figure_in_doorway"]
  action: string                # "turning to speak but the words dissolving"
  
  # Affect
  mood: string                  # "tender incompleteness"
  tension: float                # snapshot of conductor state at generation
  energy: float
  clarity: float
  novelty: float
  
  # Texture (for renderers)
  visual_texture: string        # "soft focus, warm grain, Tarkovsky-like water reflections"
  audio_texture: string         # "solo cello, room tone, intermittent rain on metal"
  
  # Temporal
  dwell_time: float             # how long to stay on this node (seconds)
  phase: float                  # position in emotional arc
  
  # Provenance
  vault_sources: list[string]   # which vault notes seeded this moment
  generation_cycle: int         # when in the daydream process this was created
  
  # Edges (where you can go from here)
  edges: list[Edge]
```

#### Edge Specification

```yaml
edge:
  source: string                # origin node id
  target: string                # destination node id
  
  # Transition quality
  transition_type: enum         # dissolve | hard_cut | slow_morph | hold_release | intrusion
  conductor_delta: dict         # how traversing this edge shifts conductor state
  emotional_continuity: float   # 0.0–1.0, how smooth the affective transition is
  
  # Traversal conditions
  tension_range: [float, float] # edge is available when tension is in this range
  energy_range: [float, float]  # edge is available when energy is in this range
  tidal_affinity: string|null   # if set, edge strengthens when this tide crests
  
  # Labels (for conductor/performer)
  label: string                 # "resolution", "deepening", "lateral association", 
                                # "tidal intrusion", "scene shift"
```

#### Graph Properties

**High connectivity** — most nodes connect to many others, not just sequential neighbors. A moment of "standing at a window in the rain" is reachable from multiple emotional paths and leads to multiple continuations. This is not a branching tree; it is a dense graph.

**Attractor nodes** — certain nodes, seeded by the vault's most charged unresolved items, have high in-degree. The graph naturally wants to return to them. During performance, even when the conductor pushes away, edge weights keep pulling back. This is the structure of rumination and also of creative fixation — the thing you keep circling.

**Emotional continuity on edges** — transitions between distant emotional states require intermediate nodes. Large conductor deltas on edges are rare and flagged. The graph enforces that you can't jump from deep melancholy to manic energy without passing through *something*.

**Cycles** — the graph contains loops. You can return to a node you've already visited, but the conductor state is different, so the renderers produce different video and audio. Same scene, different emotional coloring. This is the "I keep thinking about this but each time it feels different" quality of real daydreaming.

**Clusters** — nodes naturally cluster around vault sources. The "work anxiety" cluster, the "creative excitement" cluster, the "unresolved conversation" cluster. Transitions between clusters are the graph's macro-structure; transitions within clusters are its micro-texture.

### 3.4 Phase 2: Performance (Real-Time, Navigational)

```
DAYDREAM GRAPH ──► CONDUCTOR ──► VIDEO + AUDIO
(the score)        (real-time     (renderers)
                    traversal)
```

The performance loop is lightweight — no LLM calls, no generation. Just graph traversal and rendering:

```python
async def perform(
    graph: DaydreamGraph, 
    video_pipe: VideoPipeline, 
    lyria_session: LyriaSession,
    tides: TidalField,
    input_source: ConductorInput  # gestures, autonomous, or hybrid
):
    conductor = ConductorState()
    current_node = graph.select_entry_point(conductor)
    log = PerformanceLog()
    
    while performing:
        # 1. Render current node
        video_frame = translate_to_video(current_node, conductor)
        await video_pipe.send(video_frame)
        
        lyria_update = translate_to_lyria(current_node, conductor)
        await lyria_session.update(lyria_update)
        
        # 2. Log the moment
        log.record(current_node, conductor)
        
        # 3. Dwell
        await asyncio.sleep(current_node.dwell_time)
        
        # 4. Update conductor from tides + input
        tides.update(dt=current_node.dwell_time)
        conductor.update_from_tides(tides)
        conductor.update_from_input(input_source)
        
        # 5. Select next node
        available_edges = graph.edges_from(current_node)
        filtered_edges = filter_by_conductor(available_edges, conductor)
        next_edge = select_edge(filtered_edges, conductor)
        
        # 6. Execute transition
        await execute_transition(
            source=current_node,
            target=graph.node(next_edge.target),
            transition=next_edge.transition_type,
            video_pipe=video_pipe,
            lyria_session=lyria_session,
            duration=transition_duration(next_edge)
        )
        
        # 7. Advance
        conductor.apply_delta(next_edge.conductor_delta)
        current_node = graph.node(next_edge.target)
    
    return log
```

#### Edge Selection During Performance

The conductor state determines which edges are available and which are preferred:

```python
def select_edge(
    edges: list[Edge], 
    conductor: ConductorState
) -> Edge:
    scores = []
    for edge in edges:
        score = 0.0
        
        # Emotional continuity preference
        score += edge.emotional_continuity * 0.3
        
        # Conductor state alignment
        if edge.tension_range[0] <= conductor.tension <= edge.tension_range[1]:
            score += 0.2
        
        # Tidal pull
        if edge.tidal_affinity:
            tide_pull = tides.pull_for(edge.tidal_affinity)
            score += tide_pull * 0.4  # tides are the strongest force
        
        # Novelty seeking vs. familiar return
        if conductor.novelty > 0.6:
            score += (1.0 - edge.emotional_continuity) * 0.2  # prefer surprise
        else:
            score += edge.emotional_continuity * 0.2  # prefer flow
        
        # Hold state — prefer edges that maintain current position
        if conductor.hold and edge.transition_type == "hold_release":
            score -= 0.3  # resist leaving fixation
        
        scores.append(score)
    
    # Softmax selection with temperature based on novelty
    temperature = 0.5 + conductor.novelty * 1.0
    return weighted_random_select(edges, scores, temperature)
```

This selection mechanism means the "performance" of the daydream is non-deterministic even over the same graph. Different tidal states, different conductor input, different temperature — different paths through the same material. The graph is the score; each performance is a unique realization.

---

## Part IV: The Vault as Episodic Memory

### 4.1 Vault Structure for Daydream Ingestion

An Obsidian-style vault of structured markdown provides three things Mueller had to build by hand:

**Link graph** — the `[[wikilinks]]` between notes are explicit associative connections built through actual use. A note about a project links to notes about people, which link to conversations, which link to ideas. This is an organic associative memory.

**Frontmatter metadata** — YAML frontmatter carries emotional and structural tags:

```yaml
---
type: experience | idea | conversation | project | reflection | question
status: active | resolved | dormant | unresolved
emotional_tone: exciting | anxious | peaceful | conflicted | unresolved | bittersweet
stakes: high | medium | low
people: [alice, bob]
themes: [autonomy, creative-block, belonging, deadline]
date: 2026-03-01
last_touched: 2026-03-08
---
```

**Temporal layering** — creation dates, modification dates, and access frequency provide salience signals. Recent notes and frequently-touched notes have higher daydream salience. Dormant notes that suddenly become relevant via tidal pull produce the serendipity of "I haven't thought about that in months."

### 4.2 Dual Retrieval: Embedding + Graph Walk

The vault memory system uses two complementary retrieval strategies:

```python
class VaultMemory:
    embeddings: VectorStore         # chunked vault, embedded
    graph: NoteGraph                # [[link]] structure
    frontmatter_index: dict         # queryable metadata
    
    def retrieve(
        self, 
        state: ConductorState, 
        mode: str = "dual",
        n: int = 5
    ) -> list[VaultFragment]:
        
        # Strategy blend based on daydream phase
        embedding_weight = 1.0 - state.phase  # early: semantic seed
        graph_weight = state.phase              # later: associative extension
        
        # --- Embedding retrieval ---
        # Query composed from current daydream content + mood
        query = compose_retrieval_query(state)
        semantic_hits = self.embeddings.search(
            query,
            n=n,
            filter=self.affect_filter(state)
        )
        
        # --- Graph walk retrieval ---
        if self.current_anchor:
            walk_hits = self.graph.walk(
                start=self.current_anchor,
                steps=int(1 + state.novelty * 3),  # 1–4 hops
                bias=self.walk_bias(state)
            )
        
        # --- Tidal override ---
        tidal = self.tidal_field.aggregate()
        forced = None
        if tidal["hold_probability"] > 0.7:
            forced = self.graph.get(tidal["retrieval_bias"])
            # Involuntary intrusion: the unresolved thing
            # surfaces regardless of current content
        
        return blend_and_rank(
            semantic_hits, walk_hits, forced,
            embedding_weight, graph_weight
        )
    
    def affect_filter(self, state: ConductorState) -> dict:
        """Bias retrieval toward notes matching current emotional register."""
        if state.tension > 0.7:
            return {"emotional_tone": ["unresolved", "anxious", "conflicted"]}
        if state.energy < 0.3 and state.clarity > 0.5:
            return {"emotional_tone": ["peaceful", "reflective"]}
        if state.novelty > 0.7:
            return {"type": ["idea", "question"]}
        return {}
    
    def walk_bias(self, state: ConductorState) -> dict:
        """How to choose among outgoing links during graph walk."""
        return {
            "prefer_unvisited": state.novelty,
            "prefer_high_stakes": state.tension,
            "prefer_recent": state.clarity,
            "prefer_old": 1.0 - state.clarity,
        }
```

**Mode 1 (embedding retrieval)** answers: "what vault material is *about* similar things to the current daydream state?"

**Mode 2 (graph walk)** answers: "what vault material is *connected to* the current anchor in ways shaped by the current affect?"

The first seeds; the second extends. Early in a daydream arc, embedding retrieval dominates. As the daydream develops, graph walking takes over, following association chains. When a tide crests, the tidal override injects material from the tide's source regardless of where the daydream is — the involuntary intrusion.

### 4.3 Tidal Initialization from Vault

The tidal field is derived from vault contents:

```python
class TidalField:
    tides: list[DaydreamTide]
    
    @classmethod
    def from_vault(cls, vault: Vault) -> "TidalField":
        tides = []
        for note in vault.notes:
            fm = note.frontmatter
            
            # Active unresolved items generate tides
            if fm.get("status") in ["active", "unresolved"]:
                stakes = {"high": 0.8, "medium": 0.4, "low": 0.15}
                tension = stakes.get(fm.get("stakes", "medium"), 0.3)
                
                # Recency amplifies
                days_since_touch = (now() - fm.get("last_touched", now())).days
                recency_factor = max(0.2, 1.0 - (days_since_touch / 30))
                
                # Emotional tone shapes the coupling
                tone = fm.get("emotional_tone", "neutral")
                coupling = TONE_COUPLING_MAP.get(tone, {})
                # e.g., "anxious" → {"tension": 0.5, "clarity": -0.3}
                # e.g., "exciting" → {"energy": 0.4, "novelty": 0.3}
                
                tides.append(DaydreamTide(
                    name=note.title,
                    source_note=note.path,
                    tension_contribution=tension * recency_factor,
                    period_seconds=90 + random.uniform(-20, 40),
                    current_phase=random.random(),
                    decay_rate=0.0,  # doesn't decay until resolved
                    coupling=coupling,
                ))
        
        return cls(tides=tides)
    
    def aggregate(self) -> dict:
        """Combined tidal influence on conductor state."""
        total_tension = sum(t.pull() for t in self.tides)
        dominant = max(self.tides, key=lambda t: t.pull()) if self.tides else None
        
        # Interference: when multiple tides align, tension spikes
        aligned_count = sum(1 for t in self.tides if t.pull() > 0.5 * t.tension_contribution)
        interference_bonus = 0.1 * max(0, aligned_count - 1)
        
        return {
            "tension_bias": min(1.0, total_tension + interference_bonus),
            "retrieval_bias": dominant.source_note if dominant else None,
            "hold_probability": total_tension + interference_bonus,
            "dominant_tide": dominant,
        }
```

---

## Part V: The Rendering Pipeline

### 5.1 The Shared Representation

Between the daydream graph and the two rendering backends sits a **modality-agnostic prompt state** — the DaydreamState stored in each graph node. Two translators convert this into modality-specific control signals.

```
Graph Node (DaydreamState)
    │
    ├──► Video Translator ──► Video Pipeline
    │     (prompt string,       (StreamDiffusion
    │      LoRA weights,         or equivalent)
    │      control signals)
    │
    └──► Audio Translator ──► Lyria RealTime
          (WeightedPrompts,     (WebSocket,
           bpm, density,         48kHz stereo)
           brightness, key)
```

### 5.2 Video Translation

```python
def translate_to_video(
    node: DaydreamNode, 
    conductor: ConductorState
) -> VideoPromptFrame:
    
    # Compose scene prompt
    prompt_parts = [node.scene, node.visual_texture]
    
    # Conductor state overlays
    if conductor.clarity < 0.3:
        prompt_parts.append("soft focus, dreamlike blur, impressionistic")
    if conductor.tension > 0.7:
        prompt_parts.append("dramatic lighting, high contrast, chiaroscuro")
    if conductor.hold:
        prompt_parts.append("frozen moment, time suspended, stillness")
    if conductor.energy < 0.2:
        prompt_parts.append("slow, meditative, minimal movement")
    if conductor.novelty > 0.7:
        prompt_parts.append("surreal, unexpected juxtaposition, uncanny")
    
    prompt = ", ".join(prompt_parts)
    
    # LoRA weight mapping
    lora_weights = {
        "dreamy_aesthetic": max(0, 1.0 - conductor.clarity),
        "cinematic_tension": conductor.tension,
        "warmth_intimacy": max(0, 0.5 - conductor.energy) * 2,
        "abstract_surreal": conductor.novelty * 0.8,
        # ... extend with your existing LoRA vocabulary
    }
    
    # Control signals
    control = {
        "motion_intensity": conductor.energy * conductor.tempo,
        "transition_speed": conductor.tempo if not conductor.hold else 0.0,
        "depth_variation": conductor.novelty * 0.5,
        "camera_stability": conductor.clarity,
    }
    
    return VideoPromptFrame(
        prompt=prompt,
        loras=lora_weights,
        control=control
    )
```

### 5.3 Audio Translation (Lyria RealTime)

Lyria RealTime accepts:
- **WeightedPrompts** — multiple text descriptors with relative weights, blended to steer the continuous audio stream
- **MusicGenerationConfig** — bpm, temperature, density, brightness, key, instrument group controls

The audio translator maps DaydreamState into these controls:

```python
async def translate_to_lyria(
    node: DaydreamNode, 
    conductor: ConductorState
) -> None:  # updates session directly
    
    prompts = []
    
    # Primary: the node's own audio texture
    prompts.append(WeightedPrompt(
        text=node.audio_texture,
        weight=0.6
    ))
    
    # Mood overlay
    mood_music = mood_to_music_description(node.mood, conductor)
    # Examples of mapping:
    #   "tender incompleteness" + tension=0.5 → 
    #     "gentle piano, unresolved chord, sustained strings, intimate"
    #   "exhilarated vertigo" + energy=0.9 →
    #     "driving percussion, ascending synth, bright brass, euphoric"
    #   "anxious rehearsal" + tension=0.8 →
    #     "minimal pulse, dissonant drone, sparse clicks, uneasy"
    prompts.append(WeightedPrompt(
        text=mood_music,
        weight=0.4
    ))
    
    # Novelty/strangeness layer
    if conductor.novelty > 0.5:
        prompts.append(WeightedPrompt(
            text="experimental textures, detuned, glitchy artifacts, unusual timbres",
            weight=(conductor.novelty - 0.5) * 0.8
        ))
    
    # Hold/fixation: reduce variation, sustain
    if conductor.hold:
        prompts.append(WeightedPrompt(
            text="sustained, unchanging, drone, frozen, loop",
            weight=0.5
        ))
    
    await session.set_weighted_prompts(prompts=prompts)
    
    # Continuous parameter mapping
    await session.set_music_generation_config(
        config=LiveMusicGenerationConfig(
            bpm=int(60 + conductor.tempo * 80),         # 60–140 bpm range
            temperature=0.5 + conductor.novelty * 1.0,   # 0.5–1.5
            density=conductor.energy,                     # sparse ↔ dense
            brightness=conductor.clarity,                 # dark ↔ bright
        )
    )
```

### 5.4 Transition Execution

When the performer traverses an edge, both renderers need coordinated transitions:

```python
async def execute_transition(
    source: DaydreamNode,
    target: DaydreamNode,
    transition: TransitionType,
    video_pipe: VideoPipeline,
    lyria_session: LyriaSession,
    duration: float
):
    if transition == TransitionType.DISSOLVE:
        # Gradual blend over duration
        steps = int(duration * 30)  # ~30 updates per second
        for i in range(steps):
            t = i / steps  # 0.0 → 1.0
            
            # Interpolated conductor state
            blended = ConductorState.lerp(source.conductor, target.conductor, t)
            
            # Video: cross-fade prompts and weights
            video_frame = translate_to_video(
                interpolate_nodes(source, target, t), blended
            )
            await video_pipe.send(video_frame)
            
            # Audio: gradually shift prompt weights
            # (Lyria handles its own internal smoothing in ~2s chunks)
            if i % 15 == 0:  # update Lyria every ~0.5s
                await translate_to_lyria(
                    interpolate_nodes(source, target, t), blended
                )
            
            await asyncio.sleep(duration / steps)
    
    elif transition == TransitionType.HARD_CUT:
        # Immediate switch — jarring, used for intrusions
        await video_pipe.send(translate_to_video(target, target.conductor))
        await translate_to_lyria(target, target.conductor)
    
    elif transition == TransitionType.HOLD_RELEASE:
        # Pause, then gradual emergence into new state
        await asyncio.sleep(duration * 0.4)  # silence/stillness
        await execute_transition(
            source, target, TransitionType.DISSOLVE,
            video_pipe, lyria_session, duration * 0.6
        )
    
    elif transition == TransitionType.INTRUSION:
        # Brief flash of target, return to source, then hard cut
        # Models the involuntary thought that interrupts
        await video_pipe.send(translate_to_video(target, target.conductor))
        await asyncio.sleep(0.3)
        await video_pipe.send(translate_to_video(source, source.conductor))
        await asyncio.sleep(0.7)
        await video_pipe.send(translate_to_video(target, target.conductor))
        await translate_to_lyria(target, target.conductor)
```

---

## Part VI: The Feedback Loop

### 6.1 Performance Log

Every performance produces a log — the record of which path was taken through the graph:

```yaml
performance_log:
  timestamp: 2026-03-08T22:15:00Z
  graph_id: "daydream_session_047"
  duration_minutes: 18
  
  # What tides were active
  active_tides:
    - name: "deadline-pressure"
      peak_moments: [3:42, 11:15, 16:30]
    - name: "unresolved-conversation"  
      peak_moments: [7:20, 14:55]
  
  # Path through the graph
  traversal:
    - node: "dm_012"
      entered_at: 0:00
      conductor_state: {tension: 0.3, energy: 0.4, clarity: 0.6, ...}
      
    - edge: "dm_012 → dm_017"
      transition: dissolve
      at: 0:24
      
    - node: "dm_017"
      entered_at: 0:24
      dwell_time: 18s
      conductor_state: {tension: 0.5, energy: 0.3, clarity: 0.4, ...}
    
    # ... full path
  
  # Derived analysis
  analysis:
    attractor_visits:
      "dm_089": 3  # returned to this node three times
      "dm_023": 2
    
    peak_tension_moment:
      node: "dm_067"
      tension: 0.92
      vault_source: "project-plan.md"
    
    novel_connections:
      - "dm_045 (seeded by research-notes.md) → dm_078 (seeded by old-journal.md)"
        # unexpected bridge between work and personal material
    
    emotional_arc:
      start: {tension: 0.3, energy: 0.4}
      peak: {tension: 0.92, energy: 0.7}
      end: {tension: 0.2, energy: 0.3}
      resolution: true
```

### 6.2 Writing Back to the Vault

The performance log — or a summary of its most interesting features — can be written back to the vault as a new note:

```yaml
---
type: daydream-log
date: 2026-03-08
duration_minutes: 18
source_graph: daydream_session_047
dominant_tides: [deadline-pressure, unresolved-conversation]
seeds: [project-plan.md, research-notes.md, old-journal.md]
status: processed
---

# Daydream Session — March 8, Evening

## Arc
Started in low-energy reflection, reviewing project status. 
Deadline tide pulled attention repeatedly toward demo readiness. 
Mid-session, the graph walked from project notes through to old 
journal entries about a similar moment of creative pressure two 
years ago — unexpected connection. Peak tension at the realization 
that the current project has the same structural risk as the old one. 
Resolution came through the planning agent's rehearsal of a 
simplified demo scope.

## Novel Connection
[[research-notes]] → [[old-journal-2024-spring]] 
The pattern of "trying to integrate too many components before 
testing the core loop" recurred. The old project failed because 
of this. The current project can learn from it.

## Attractor
Kept returning to the "integration layer" concept from 
[[conductor-landscape]]. This may be the actual center of the 
project, not the rendering pipeline.
```

This note enters the vault. It links to the source material it drew from. Next time the daydream engine runs, it can find *this note* as episodic material. The system daydreams about its own daydreaming. Previous patterns of attention, previous novel connections, previous resolutions — all become available as material for future daydreaming.

The vault accumulates a sediment of dream-logs. Over time, the system develops something like autobiographical continuity — not just facts and projects but a record of *how this mind wanders* through its own material.

---

## Part VII: System Architecture Summary

### 7.1 Full Stack

```
┌─────────────────────────────────────────────────────────────────┐
│                       VAULT (input)                              │
│                                                                  │
│  Structured markdown notes with [[links]], YAML frontmatter      │
│  Types: experiences, ideas, conversations, projects, reflections │
│  Emotional and structural metadata in frontmatter                │
│  Daydream logs from previous sessions (recursive)                │
│                                                                  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                 PHASE 1: DAYDREAM ENGINE (offline)                │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────┐      │
│  │ VaultMemory  │  │  TidalField  │  │ ConductorState    │      │
│  │              │  │              │  │                   │      │
│  │ • Embeddings │  │ • From vault │  │ • energy          │      │
│  │ • Note graph │  │   unresolved │  │ • tension         │      │
│  │ • Frontmatter│  │   items      │  │ • density         │      │
│  │ • Dual       │  │ • Slow arcs  │  │ • clarity         │      │
│  │   retrieval  │  │ • Cresting   │  │ • novelty         │      │
│  │              │  │   & decay    │  │ • tempo, phase    │      │
│  └──────┬───────┘  └──────┬───────┘  │ • hold, focus     │      │
│         │                 │          └─────────┬─────────┘      │
│         └────────┬────────┘                    │                │
│                  │                             │                │
│                  ▼                             │                │
│         ┌──────────────────┐                  │                │
│         │ Scenario         │◄─────────────────┘                │
│         │ Generator (LLM)  │                                   │
│         │                  │                                   │
│         │ Agents:          │                                   │
│         │ • Memory         │                                   │
│         │ • Planning       │                                   │
│         │ • Social         │                                   │
│         │ • Affect         │                                   │
│         │ • Association    │                                   │
│         └────────┬─────────┘                                   │
│                  │                                              │
│                  ▼                                              │
│         ┌──────────────────┐                                   │
│         │ DAYDREAM GRAPH   │                                   │
│         │                  │                                   │
│         │ Nodes: moments   │                                   │
│         │ Edges: transitions│                                  │
│         │ Attractors       │                                   │
│         │ Clusters         │                                   │
│         │ Cycles           │                                   │
│         └────────┬─────────┘                                   │
└──────────────────┼─────────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                 PHASE 2: PERFORMANCE (real-time)                  │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────┐      │
│  │ Graph        │  │ Conductor    │  │ Edge Selection    │      │
│  │ Traversal    │──│ State        │──│                   │      │
│  │              │  │ (live)       │  │ • Tidal pull      │      │
│  │ Current node │  │              │  │ • Affect match    │      │
│  │ Available    │  │ Updated by:  │  │ • Novelty seeking │      │
│  │ edges        │  │ • Tides      │  │ • Temperature     │      │
│  │              │  │ • Input      │  │                   │      │
│  └──────┬───────┘  └──────────────┘  └───────────────────┘      │
│         │                                                        │
│         ├────────────────────────────────┐                       │
│         ▼                                ▼                       │
│  ┌────────────────────┐  ┌────────────────────┐                 │
│  │ Video Translator   │  │ Audio Translator   │                 │
│  │                    │  │                    │                 │
│  │ DaydreamState →    │  │ DaydreamState →    │                 │
│  │ • Prompt string    │  │ • WeightedPrompts  │                 │
│  │ • LoRA weights     │  │ • bpm              │                 │
│  │ • Control signals  │  │ • density          │                 │
│  │                    │  │ • brightness       │                 │
│  └────────┬───────────┘  │ • temperature      │                 │
│           │              └────────┬───────────┘                 │
│           ▼                       ▼                              │
│  ┌────────────────────┐  ┌────────────────────┐                 │
│  │ Video Pipeline     │  │ Lyria RealTime     │                 │
│  │ (your existing     │  │ (WebSocket,        │                 │
│  │  infrastructure)   │  │  48kHz stereo)     │                 │
│  └────────────────────┘  └────────────────────┘                 │
│                                                                  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                     PERFORMANCE LOG                              │
│                                                                  │
│  Traversal path, conductor state timeline, attractor visits,     │
│  novel connections, emotional arc                                │
│                                                                  │
│  Optionally written back to vault as daydream-log note           │
│  (recursive: future daydreams can reference past daydreams)      │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 Temporal Structure

| Timescale | Phase 1 (Daydreaming) | Phase 2 (Performance) |
|-----------|----------------------|----------------------|
| **Slow (minutes)** | Tidal oscillators cycling; emotional arc of the entire daydream session | Tidal oscillators cycling; macro-structure of the performance |
| **Medium (5–15s)** | Scenario generator cycle; one LLM call producing branching candidates | Node dwell time; one position in the graph |
| **Fast (1–2s)** | N/A (batch process) | Lyria chunk boundary; prompt weight updates |
| **Frame (ms)** | N/A | Video frame rendering; LoRA weight interpolation |

### 7.3 Data Flow Summary

| Data | Format | Flows From | Flows To |
|------|--------|-----------|---------|
| Vault notes | Structured markdown + YAML frontmatter | User's Obsidian vault | VaultMemory (embedding + graph) |
| Tidal configuration | DaydreamTide objects | Vault frontmatter analysis | ConductorState, edge selection |
| Vault fragments | Text chunks with metadata | VaultMemory retrieval | Scenario generator LLM prompt |
| DaydreamState | Structured object (scene, mood, textures, affect) | Scenario generator output | Graph nodes |
| Daydream graph | Nodes + edges with metadata | Phase 1 output | Phase 2 input |
| Video prompts | String + LoRA weights + control signals | Video translator | Video pipeline |
| Audio prompts | WeightedPrompts + MusicGenerationConfig | Audio translator | Lyria RealTime WebSocket |
| Performance log | YAML/JSON | Phase 2 output | Vault (written back as new note) |

---

## Part VIII: Build Sequence

### Stage 1: Coherence Test (no LLM, no vault)

**Goal:** Verify that video and audio driven by the same conductor state feel like one experience.

- Hard-code a conductor state trajectory: a tension arc that rises and falls over 3 minutes
- Write a fixed sequence of 10 DaydreamState nodes (manually authored)
- Connect both translators to your video pipeline and a Lyria RealTime session
- Run the performance loop

**Test:** As tension rises, does the video darkening / intensifying happen *in the same emotional moment* as the music building? Does the hold state feel like a held breath in both modalities?

**Done when:** A viewer says "the music and the visuals go together" without being told they're driven by the same signal.

### Stage 2: Graph Traversal (no LLM, manual graph)

**Goal:** Verify that non-linear navigation through a graph feels like daydreaming rather than a slideshow.

- Hand-author a small graph (20–30 nodes, ~80 edges)
- Include attractor nodes, cycles, and variety of transition types
- Add tidal oscillators that bias edge selection
- Run autonomous performance (no human input, just tides + temperature)

**Test:** Does it feel like mind-wandering — returning to certain things, drifting unexpectedly, having a flow that's neither random nor predetermined?

**Done when:** The system produces a 10-minute performance that feels like it has an emotional arc without that arc being hard-coded.

### Stage 3: Vault Ingestion + Tidal Initialization

**Goal:** Connect the vault as the source of tidal configuration and seed material.

- Implement vault parsing: frontmatter extraction, link graph construction, embedding generation
- Implement TidalField.from_vault()
- Implement VaultMemory with dual retrieval
- Manually author graph nodes, but seeded by actual vault content

**Test:** Do the tides pull toward the right material? Does retrieval surface interesting fragments?

**Done when:** The tidal field reflects the vault's actual unresolved tensions, and retrieval produces fragments that feel associatively rich rather than keyword-matching.

### Stage 4: LLM Scenario Generation (the daydream engine)

**Goal:** Replace manually authored graph nodes with LLM-generated ones.

- Implement the scenario generator prompt template
- Implement the batch daydream loop (Phase 1)
- Generate a full daydream graph from vault material
- Run Phase 2 performance over the generated graph

**Test:** Are the generated nodes coherent, emotionally plausible, and varied? Does the graph have the right structural properties (high connectivity, attractor nodes, cycles)?

**Done when:** The system can take a vault, daydream over it for 30 minutes, and produce a graph that yields a compelling 10-minute audiovisual performance.

### Stage 5: Feedback Loop

**Goal:** Performance logs written back to vault; recursive daydreaming.

- Implement performance log generation
- Implement vault write-back as daydream-log notes
- Run successive daydream sessions over the growing vault
- Observe whether daydream logs influence subsequent daydreams

**Test:** Does the system reference its own previous daydreams? Does it build on novel connections discovered in earlier sessions?

**Done when:** The second daydream session over the same vault produces different and richer output than the first, because it has access to the first session's discoveries.

### Stage 6: Conductor Input (human in the loop)

**Goal:** Allow a human performer to influence graph traversal during Phase 2.

- Implement conductor input mapping (gesture, MIDI controller, keyboard — whatever your existing interface supports)
- Blend human input with tidal pressure for edge selection
- Expose conductor state to the performer (visualization)

**Test:** Does it feel like conducting — shaping the flow without choosing content?

**Done when:** A performer can navigate the same graph twice and produce meaningfully different emotional arcs.

---

## Part IX: Open Questions

### Architecture

- **Graph density** — what is the right ratio of edges to nodes? Too sparse and traversal is predetermined; too dense and selection becomes noise.
- **Node dwell time** — fixed per node, or dynamically computed from conductor state? Probably dynamic: high energy = shorter dwell, hold = extended dwell.
- **Transition coordination** — Lyria's ~2s response latency vs video's near-instant response. How to make transitions feel simultaneous despite different response times?
- **Graph size** — how many nodes/edges make a graph that sustains a 30-minute performance without repetition? Hypothesis: 200–500 nodes.

### Vault

- **Minimum vault size** — how much material does the vault need before the daydream engine produces interesting output? Can it work with 50 notes? 500?
- **Frontmatter schema** — what's the minimum metadata the vault needs? `status` and `emotional_tone` seem essential; `stakes` and `themes` are useful but optional.
- **Link density** — does the vault need to be heavily interlinked, or can the embedding layer compensate for sparse linking?
- **Privacy/scope** — if the vault contains personal material, how is the generated graph handled? Ephemeral by default? Stored with access controls?

### Rendering

- **Video prompt vocabulary** — what LoRA vocabulary does the existing pipeline support? The translator needs to map into what's actually available.
- **Lyria prompt effectiveness** — how well does Lyria respond to emotional/abstract prompts vs genre/instrument prompts? May need empirical testing of the mood→music mapping.
- **Cross-modal coherence metrics** — how do we evaluate whether video and audio feel unified? Probably subjective initially, potentially measurable via arousal/valence trajectory alignment.

### Daydream Quality

- **Branching factor** — how many candidates per cycle? More = richer graph but more LLM cost.
- **Scenario coherence over long sessions** — does the LLM maintain thematic coherence across hundreds of cycles? May need explicit memory of earlier nodes in the prompt.
- **Avoiding cliché** — the LLM may default to generic scenes. The vault fragments and style conditioning (from the brief/LoRA prompt engineering work) are the primary mitigation.
- **Emotional plausibility** — does the conductor delta suggested by the LLM actually produce believable emotional trajectories? May need post-hoc smoothing or validation.

---

## Appendix A: Relationship to Existing Project Documents

| Document | Contribution to This Architecture |
|----------|----------------------------------|
| `generative-steering.md` | The Authoring/DJing/Conducting distinction. Daydreaming as preparation; performance as conducting through prepared material. The multi-timescale temporal structure. |
| `instrument-stack-architecture.md` | The five-part architecture (Score/Conductor/Ensemble/Stage/Log). Conductor state vector. Agent contracts and budgets. Tidal oscillators. The Open Score Graph format. |
| `narrative-conducting.md` | The brief as score. Style seeding as orchestration. Selection criteria as aesthetic commitment. The clarify move. The Loom as instrument. |
| `conductor-landscape.md` | Existing technology mapping. Soundpainting grammar. Build order. The key insight: "the components exist; the integration philosophy doesn't." |
| `ims-exploration-notes.md` | IMS 5-layer architecture. Experience design principles ("learnable in 10s, expressive in 10min, deep in 10hr"). Playtest loop. Hardware/software boundaries. |

## Appendix B: Key References

**DAYDREAMER**
- Mueller, E.T. & Dyer, M.G. (1985). "Towards a computational theory of human daydreaming." *Proceedings of the Seventh Annual Conference of the Cognitive Science Society*, 120–129.
- Mueller, E.T. & Dyer, M.G. (1985). "Daydreaming in Humans and Computers." *Proceedings of IJCAI-85*.
- Mueller, E.T. (1990). *Daydreaming in Humans and Machines: A Computer Model of the Stream of Thought.* Norwood, NJ: Ablex.

**Default Mode Network**
- Menon, V. (2023). "20 years of the default mode network: a review and synthesis." *PNAS*.
- Smallwood, J. et al. (2017). "The role of the default mode network in component processes underlying the wandering mind." *Social Cognitive and Affective Neuroscience*.
- Andrews-Hanna, J.R. (2012). "The Brain's Default Network and its Adaptive Role in Internal Mentation." *The Neuroscientist*.
- Tong, D. et al. (2024). "Space wandering in the rodent default mode network." *PNAS*.

**Lyria RealTime**
- Google DeepMind Lyria RealTime: https://deepmind.google/models/lyria/lyria-realtime/
- Lyria RealTime API: https://ai.google.dev/gemini-api/docs/music-generation
- Magenta blog: https://magenta.withgoogle.com/lyria-realtime
