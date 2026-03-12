# Conducted Daydreaming: Architecture Specification

## Status: Working Draft — March 2026

---

## Part I: Definition

Conducted Daydreaming is a persistent audiovisual narrative medium.

An authored fictional world enters an offline engine. The engine produces a graph of emotionally coherent scenes organized around unresolved situations. A live performance traverses that graph, rendering each node as video and audio. The world advances only when committed events occur. Scenes that contradict current canon remain traversable with distinct rendering treatment, preserving the phenomenology of memory, fantasy, and counterfactual imagination.

The participant does not control actions directly. They conduct attention, pressure, and timing.

Conducted Daydreaming is not an action-selection game. It is a persistent narrative system in which attention and timing shape traversal, and committed events alter canon. It shares structure with games (persistent state, participation-driven consequences) but differs in interface (no action menus, no dialogue trees, no text) and in the quality of agency (attention over what to dwell on, not selection of what to do).

---

## Part II: System Layers

The architecture comprises four runtime layers. Each has distinct responsibilities, state, and interfaces.

```
┌──────────────────────────────────────────────┐
│  STAGE LAYER                                  │
│  Traversal, rendering, commit input, logging  │
├──────────────────────────────────────────────┤
│  GRAPH LAYER                                  │
│  Nodes, edges, status tags, repair            │
├──────────────────────────────────────────────┤
│  COGNITIVE LAYER                              │
│  Situation selection, goal types, retrieval,  │
│  Critic-Selector, generation orchestration    │
├──────────────────────────────────────────────┤
│  WORLD LAYER                                  │
│  Canon, constraints, ripeness, events,        │
│  validation                                   │
└──────────────────────────────────────────────┘
```

The offline engine operates across the World, Cognitive, and Graph layers. Live performance operates across the Graph and Stage layers. The World layer is consulted during performance only at event commitment.

---

## Part III: The Pipeline

```
STORY SOURCE → ENGINE (offline) → GRAPH → TRAVERSAL (live) → RENDER
```

The base product is: authored world in, navigable daydream out. No body sensors. No biometric input.

### Deferred modes (not abandoned)

- **Autobiographical vault mode:** Personal notes as source material instead of fictional world bible. Already validated by users running manual versions of this workflow with Claude.
- **Biometric conducting mode:** Physiological signals modulating traversal. Demoted from chooser to modulator — physiology biases traversal but does not make irreversible choices unilaterally.
- **Sensor hardware:** nRF52840-based wearable with BLE transport. Architecture explored in companion embedded systems documents.

---

## Part IV: World Layer

### 4.1 Story Source (World Bible)

The input is an authored fictional world with three distinct concerns:

**Canon** — mutable facts about current state. Characters, their knowledge states, locations, relationships, wounds. Places and conditions. Situation ripeness values.

**Constraints** — invariant rules. Character behavioral rules, world physics (operationalized, not just lore: "weather is emotional" means weather state biases retrieval and rendering parameters), knowledge gates (who can know what and through which events).

**Motifs** — aesthetic and thematic generation guidance. Color palettes, recurring images, sonic textures, emotional registers per place, LoRA prompt vocabulary.

### 4.2 Storage (Five Distinct Stores)

| Store | Contents | Mutability |
|-------|----------|------------|
| **CanonicalWorldState** | Facts true now | Event-mutable, persists across sessions |
| **SourceBible** | Character sheets, place sheets, world rules, motifs, constraints | Authored, rarely mutated |
| **CounterfactualGraphPool** | Scene graph with status tags | Generated per engine run, repaired after events |
| **SessionLog** | Traversal trace, events, attractors, state trajectory | Append-only per session |
| **PersonalVault** (deferred) | Real notes, journals — autobiographical mode only | User-managed |

### 4.3 Situation Model: Ripeness and Activation

Each unresolved situation carries two values:

**Ripeness** is long-horizon canonical pressure. It increases with time and escalation across sessions. It decreases only through committed events or explicit state transitions authored in the world model. Daydreaming about a situation does not reduce its ripeness. You cannot un-ripen a situation by worrying about it.

**Activation** is short-horizon attentional salience within a session. It spikes when the engine generates material about the situation, decays naturally over generation cycles, and can spike again on retrieval hits or associative connections. Activation drives moment-to-moment generation selection.

```
ripeness:   slow, canonical, cross-session, resolves only via events
activation: fast, session-local, decays after daydreaming, drives selection
```

When activation is high and ripeness is high, the engine is locked onto charged material. When activation decays but ripeness remains, the system drifts away temporarily but will return — the gravitational pull persists even when momentary attention has moved on.

Autonomous event triggering occurs when ripeness exceeds a threshold (e.g., 0.85). Below that threshold, events require participant commitment.

### 4.4 Events

An event is a named state transition with:
- Preconditions (queryable against canon)
- Mutations (applied to canon on commitment)
- Knowledge grants (which characters learn what)
- Situation effects (which situations resolve, which spawn)

Events are irreversible in the canonical timeline.

### 4.5 Validation

The World layer exposes a deterministic validation interface:

```
query(path) → current value
character_knows(character, fact) → boolean
validate_event(event) → (valid, violations)
apply_event(event) → new state
validate_node(node) → (valid, violations)
contradicts(node) → boolean
situation_ripeness(situation_id) → float
situation_activation(situation_id) → float
available_events() → list of fireable events
```

**Three implementation tiers:**

- **Tier 1 (sprint):** Map/dict + predicate checks. Good enough for prototyping.
- **Tier 2 (rule engine):** Datascript/Datalog. Declarative, queryable, composable. Can explain "why was this node rejected?"
- **Tier 3 (formally verified):** ACL2. Proves invariants hold for all reachable states. A collaborator working in ACL2 exists. Clojure-to-ACL2 translation is short (both Lisps, same data shapes).

**Safety properties (provable at Tier 2-3):**

- **No knowledge without source:** If a character knows a fact, a granting event has occurred.
- **No illegal events:** All committed events had satisfied preconditions at commit time.
- **Pressure boundedness:** Sum of active situation ripeness values cannot overflow.
- **Continuation guarantee:** From any reachable canonical state, the engine can produce at least one admissible atmospheric, reflective, or transitional node.
- **Counterfactual soundness:** If a node is marked canon-compatible, it does not violate declared invariants or known facts.

Stronger properties (counterfactual detection completeness, full narrative liveness) are aspirational targets, not initial proof goals.

---

## Part V: Cognitive Layer

### 5.1 Lineage: Mueller's DAYDREAMER

Erik Mueller's DAYDREAMER (UCLA, 1983–1988, 12,000 lines of Common Lisp, source at `github.com/eriktmueller/daydreamer`) is the direct architectural ancestor.

Mueller's central contribution: the emotion-driven control loop as a complete system. Emotions drive what the system thinks about. The results of thinking change the emotions. The loop produces something that reads as a stream of consciousness.

The adaptation from Mueller requires three structural clarifications:

**The control loop** is simpler than the original architecture docs suggested. Mueller's `daydreamer-control0`: decay needs, decay emotions, find most motivated goal, run one planning step. No continuous conductor vector. A priority queue ordered by emotional motivation with one step per cycle for the winner. The stream of thought emerges from interleaving.

**The context tree** (`gate_cx.cl`) is the ancestor of the counterfactual graph. Each context inherits from its parent. The tree of contexts is the preserved record of explored and abandoned possibilities. But Mueller's tree is a planning decomposition tree. The adaptation to a navigable scene graph is real structural work.

**The mode switch** between daydreaming and performance is clean in Mueller. Same control loop, same emotion system, same memory. Only goal eligibility changes. The conducted daydreaming system makes a more radical split (different code in offline vs live phases) for latency reasons.

### 5.2 Daydreaming Goal Types

The highest-value import from Mueller. Each type is triggered by specific emotional conditions and determines the *kind* of scene the engine generates:

| Goal Type | Trigger | Generation Strategy |
|-----------|---------|-------------------|
| **RATIONALIZATION** | High negative activation from failure | Reframe the failure, reduce emotional charge |
| **ROVING** | High negative activation (alternative) | Shift to pleasant or unrelated material |
| **REVENGE** | Negative activation directed at specific character | Imagine the other's failure or comeuppance |
| **REVERSAL** | Negative activation from failure | Imagine alternate past where failure was avoided |
| **RECOVERY** | Past failure, ongoing ripeness | Imagine future success at previously failed goal |
| **REHEARSAL** | Upcoming situation, high ripeness | Practice/simulate what to do next |
| **REPERCUSSIONS** | Hypothetical future threat | Explore consequences, plan for contingencies |

Daydream goal type is a first-class node property. The engine does not generate "a scene near the harbor" generically — it generates a rehearsal scene, or a reversal scene, or a rationalization scene. Edge selection during traversal is biased by which goal type is currently most emotionally motivated.

### 5.3 Retrieval

Two complementary mechanisms:

**Embedding retrieval:** Semantic similarity between current generation context and source material. Standard vector search. Answers: "what material is *about* similar things?"

**Threshold-based coincidence retrieval:** Tracks how many independent active signals (situation activation, emotional tone, character reference, thematic tag, recent traversal) point at each piece of source material. When the count exceeds a threshold, the material surfaces regardless of embedding distance. This is Mueller's serendipity mechanism: multiple independent reasons converging on the same fragment. Start with threshold of 3 indices, tune empirically.

### 5.4 Mutation

When the graph is locally exhausted (recent nodes clustering semantically, diversity metrics dropping), the engine systematically permutes a scene element rather than raising temperature. Same setting, different character. Same emotional register, different place. Same characters, different situation. More principled than random high-temperature sampling, preserves more structural coherence. Adapted from Mueller's `dd_mutation.cl`.

### 5.5 Critic-Selector (from Minsky)

Mueller handles stuckness through backtracking. Insufficient for a system generating hundreds of nodes. Minsky's Critic-Selector mechanism addresses this.

A **Critic** monitors the engine's output:
- Node admission/rejection rate
- Semantic diversity of recent N nodes
- Emotional trajectory (moving or flat)
- Daydream goal type duration (stuck in one type)
- Activation patterns (everything decaying toward zero = system going flat)

A **Selector** switches strategy when a Critic fires:
- Change daydream goal type
- Invoke mutation
- Force situation focus shift
- Spike activation on dormant situation

Every Critic firing and Selector action is logged in the generation trace.

### 5.6 Conductor State (Leveled)

Not a flat vector. Three levels with different timescales:

**Fast / reactive (per-node, continuous):**
- `tension` — current emotional charge
- `energy` — vividness and pace
- `hold` — fixation state (stuck on current material)

**Medium / deliberative (per-situation, per-session):**
- `situation_activations` — session-local attention weights
- `daydream_goal_type` — current active generation strategy
- `novelty` — distance from established patterns

**Slow / canonical (cross-session):**
- `situation_ripeness` — long-horizon pressure per situation
- `session_history` — what happened in previous sessions
- `pattern_recognition` — recurring avoidance/attraction (Critic input)

Derived values (not independently specified):
- `tempo` ← energy
- `density` ← tension × novelty
- `clarity` ← daydream goal type (rehearsal = high, roving = low)

### 5.7 The Generation Cycle

Each cycle of the offline engine:

1. Read situation ripeness and activation values
2. Select daydream goal type based on dominant activation and Mueller's trigger rules
3. Retrieve source material (embedding + threshold coincidence)
4. Generate a scene node of the selected type
5. Validate against world state constraints — reject and retry if invalid
6. Admit to graph, connect edges
7. Tag node with compatibility status
8. Update activation (slight decay on daydreamed situation, possible spike on associated situations)
9. Run Critic check — if output quality is dropping, Selector switches strategy
10. Write trace entry
11. Repeat

### 5.8 Generation Trace

Every cycle writes a trace entry:

```
{
  cycle_id,
  timestamp,
  dominant_situation,
  situation_activations_snapshot,
  selected_goal_type,
  retrieval_hits: [embedding_results, threshold_results],
  critic_state: {diversity_score, admission_rate, trajectory_slope},
  selector_action: null | {type, reason},
  proposed_node_id,
  validation_result: admitted | {rejected, violations},
  final_node_id: id | null
}
```

Required for debugging, tuning, and understanding why the graph has the shape it has.

---

## Part VI: Graph Layer

### 6.1 Node Schema

```
{
  node_id,
  node_kind:           atmospheric | reflective | transitional |
                       event_candidate | aftermath,
  daydream_goal_type:  rationalization | roving | revenge | reversal |
                       recovery | rehearsal | repercussions,
  situation_ids:       [which situations this node relates to],
  characters:          [present or referenced],
  place,
  assumed_facts:       [world state this node implies],
  emotional_signature: {tension, energy, valence},
  compatibility_status: present_compatible | anticipated_future |
                        alternative_past | alternative_present |
                        projected_consequence | stale_after_event |
                        incoherent_unreachable,
  render_brief:        {visual description, color mood, focus, motion},
  sound_brief:         {emotional register, texture, instrumentation hints},
  event_payload:       null | {event_id, preconditions, mutations},
  provenance:          {source_fragments, retrieval_mode, goal_type_at_generation},
  dwell_hint:          suggested seconds (modulated by conductor state)
}
```

### 6.2 Edge Schema

```
{
  from,
  to,
  edge_kind:            continuation | association | escalation |
                        counterfactual_jump | aftermath | relief |
                        return,
  weight:               float (modulated during traversal),
  situation_alignment:  [which situations this transition serves],
  transition_style:     smooth | cut | dissolve | intrusion,
  justification:        why this edge exists (thematic, emotional, spatial, associative)
}
```

### 6.3 Compatibility Status

Not all non-canonical nodes are the same:

| Status | Meaning | Rendering Treatment |
|--------|---------|-------------------|
| `present_compatible` | Consistent with current canon | Full presence: sharp focus, direct audio |
| `anticipated_future` | Plausible upcoming scenario | Slight softness, forward-leaning energy |
| `alternative_past` | What if something had gone differently | Shifted color grade, more reverb |
| `alternative_present` | Canon-incompatible present imagination | Softer focus, dreamlike quality |
| `projected_consequence` | Imagined result of potential event | Tentative rendering, unstable edges |
| `stale_after_event` | Was canon-compatible, now contradicted by committed event | Marked shift to memory register |
| `incoherent_unreachable` | Nonsensical given current canon | Not traversable, edges severed |

### 6.4 Post-Event Graph Repair

After an event commits during traversal:

1. Re-evaluate all nodes via `contradicts()` against new canon
2. Nodes now contradicting canon → retag as `stale_after_event` (still traversable, memory rendering)
3. Nodes that are incoherent (not just counterfactual but nonsensical) → mark `incoherent_unreachable`, sever or heavily penalize edges
4. Inject post-event traversal energy: bias edge weights toward aftermath nodes, consequence exploration
5. Between sessions, engine regenerates graph from updated canon

For v1: simple re-run of `contradicts()`, flip tags, penalize edges. No live regeneration.

---

## Part VII: Stage Layer

### 7.1 Traversal

Live performance is graph navigation, not generation. At each step:

- Current node is rendering (video + Lyria RealTime audio)
- Edge weights are modulated by situation activations and conductor state
- On dwell expiry or transition trigger, select next node by weighted edge sampling
- Attractor nodes (high connectivity, emotionally charged) pull traversal across multiple hops
- Hold state freezes traversal on current node

Traversal quality target: neither random nor predetermined. Situation activation creates gravitational bias. Temperature creates variation. Attractors create return patterns. The result circles, avoids, approaches, drifts, and eventually commits.

### 7.2 Rendering

Node compatibility status drives rendering treatment (see 6.3 table). Video pipeline receives `render_brief`. Lyria RealTime receives `sound_brief`. Both driven by the same emotional signature.

### 7.3 Event Commitment (v1 Mechanic)

For the base product without biometric input:

**Repeated approach + explicit confirmation.**

1. Traversal reaches an event-candidate node
2. Node renders with event-candidate markers (visual weight, increased presence)
3. If the participant has approached this event candidate multiple times in the session (threshold: 3 visits), a commit prompt becomes available
4. Commit requires a single explicit input (button press, sustained gesture, or equivalent)
5. On commit: `apply_event()` mutates canon, post-event graph repair runs, session continues in new world state

This preserves conducting-by-attention (repeated approach reflects dwelling/obsession) while keeping irreversible state changes legible and testable. Pure implicit commit is deferred to the biometric mode.

### 7.4 Session Loop

```
SESSION 1:
  Canon: letter unopened, day 3
  Engine generates graph offline
  Performance: traverse graph, event candidates approached but not committed
  Result: letter still unopened; ripeness advances; activation patterns logged

SESSION 2:
  Canon: letter unopened, day 4 (ripeness higher)
  Engine generates new graph from updated canon + session history
  Performance: stronger pull toward letter nodes; event committed (letter opened)
  Post-event repair: stale nodes retagged, aftermath explored
  Result: canon mutated; new situations active

SESSION 3:
  Canon: letter opened; new situation landscape
  Pre-opening nodes now render as alternative_past
  New activation patterns from new situations
  Different emotional territory
```

---

## Part VIII: Orchestration

### 8.1 The Generation Loop

The offline generation loop requires:
- Access to world state (queries, validation)
- Retrieval over source material (embedding + threshold)
- Deterministic validation
- Graph storage and statistics
- Situation ripeness and activation management
- Explicit logging of all decisions

The architectural unit is the loop plus tool contract. The specific model serving as planner/generator is replaceable.

### 8.2 Prototype Implementation via MCP

For prototyping, Claude can execute the offline generation loop by following an explicit control policy encoded in a system prompt and calling MCP tools for state, retrieval, validation, and graph writes. Claude is the first orchestrator implementation because it supports rich instruction following and MCP integration. The system prompt encodes Mueller's control architecture; the MCP servers provide persistent state.

MCP tool groups:

**World State:**
- `query_world_state(path)` → current value
- `character_knows(character, fact)` → boolean
- `validate_event(event)` → valid + violations
- `apply_event(event)` → updated state
- `situation_ripeness(situation_id)` → float
- `situation_activation(situation_id)` → float
- `available_events()` → fireable events list

**Graph:**
- `add_node(scene_spec)` → node_id
- `add_edge(from, to, weight, kind, justification)` → edge_id
- `update_node_status(node_id, new_status)`
- `get_recent_nodes(n)` → last n for context
- `graph_stats()` → size, diversity, connectivity, admission rate

**Retrieval:**
- `retrieve_by_embedding(query, n)` → fragments
- `retrieve_by_threshold(active_indices)` → serendipitous hits
- `get_active_indices()` → currently primed signals

**Situation:**
- `get_all_situations()` → ripeness + activation for each
- `decay_activations(dt)` → updated activations
- `spike_activation(situation_id, delta)` → manual or Selector-driven
- `get_dominant_situation()` → highest activation

### 8.3 Language Choices

**MCP servers: Clojure (preferred) or Python**

Clojure advantages: persistent data structures (context tree without defensive copying), Datascript (Datalog over immutable state), core.logic (unification), EDN (world bible format), macros (rule DSL), short translation path to ACL2.

Python is viable for Tier 1 prototyping. The MCP interface is identical regardless of server language.

**Traversal and rendering: Python.** Video generation, Lyria RealTime, diffusion inference are Python-ecosystem tools. Communicates with the graph through data, not function calls.

**Graph interchange format: data.** JSON, EDN, or Transit. Nodes, edges, tags, metadata. Engine produces; renderer consumes; session logs flow back.

### 8.4 Mueller Source Code Mapping

| Mueller File | Contents | Conducted Daydreaming Equivalent |
|-------------|---------|--------------------------------|
| `dd_kb.cl` types | Object type hierarchy | Datascript schema / node+edge schemas |
| `dd_kb.cl` initial-facts | Starting world state | World bible seed data |
| `dd_kb.cl` define-rule | Planning/inference rules | Validation rules + generation strategy |
| `dd_cntrl.cl` control loop | Emotion-driven scheduling | System prompt control policy |
| `dd_cntrl.cl` emotion-decay | Weight decay | Activation decay in Situation Server |
| `dd_cntrl.cl` most-motivated | Priority selection | Daydream goal type selection |
| `gate_cx.cl` context tree | Branching world states | Persistent data structures (Clojure) |
| `dd_epis.cl` threshold retrieval | Multi-index episode recall | Threshold retrieval in Retrieval Server |
| `dd_epis.cl` serendipity | Lowered-threshold recall | Coincidence-count layer |
| `dd_mutation.cl` | Action permutation | Scene element mutation |
| `dd_reversal.cl` (concept) | Imagining alternate outcomes | `alternative_past` status + reversal goal type |
| `dd_gen.cl` | English generation | **Not ported** — replaced by LLM + rendering |
| `dd_night.cl` | Night dreaming | **Not ported** |
| `gate_unify.cl` | Custom unification | **Not ported** — use Datascript/core.logic |

### 8.5 Mueller 2022 Assessment

Mueller's January 2022 update paper identifies two paths for modernization: fine-tune an LLM on daydream corpora (insufficient — realistic daydream structure requires explicit goal management, emotional regulation, and type selection), or build explicit representations and processes with an LLM as the generation component (his recommendation, our approach).

Mueller spent his post-DAYDREAMER career on commonsense reasoning, identifying it as the main bottleneck. LLMs resolve the commonsense knowledge gap. The conducted daydreaming system provides the processes Mueller's update paper calls for: goal management, emotional control, memory retrieval, and learning. The propose-validate-admit architecture addresses the constraint enforcement question Mueller raises but does not resolve.

---

## Part IX: Build Sequence

### Stage 0: Author the World Bible

Two characters, three places, three unresolved situations, six events. Structured data. This is the test fixture.

### Stage 1: Hand-Author Graph, Prove Traversal

20-30 nodes, ~80 edges, hand-authored. Attractor nodes, cycles, varied transition types. Situation activation oscillators bias edge selection. Autonomous traversal (no human input, just activations + temperature).

**Test:** Does it feel like mind-wandering — returning, drifting, neither random nor predetermined?
**Done when:** 10-minute performance with an emotional arc that was not hard-coded.

### Stage 2: World State Engine + Validation

Build as MCP server. Wire up `validate_node()`, `contradicts()`, `apply_event()`. Test post-event retagging against hand-authored graph.

### Stage 3: Engine via MCP

System prompt encodes Mueller's control loop. Generator produces nodes by calling MCP servers. Generate a graph from the world bible. Compare quality against hand-authored graph.

**Key test:** Does the engine produce a graph with the quality of involuntary return — not random, not predetermined, with cognitive posture from the daydream goal types?

### Stage 4: Session Loop

Save updated canon after performance. Re-run engine. Verify second session reflects first session's events.

### Stage 5: Conductor Input

Manual conductor input (sliders, keyboard, MIDI). Blend with situation activations for edge selection. Expose state to performer.

**Test:** Does it feel like conducting — shaping flow without choosing content?

### Stage 6: Audio-Visual Rendering

Video generation pipeline + Lyria RealTime. Node emotional signature drives both. Test cross-modal coherence.

### Deferred

- Autobiographical vault mode
- Biometric modulation (not choice)
- ACL2 formal verification (Tier 3)
- Recursive daydream-log writeback
- Implicit event commitment via sustained biometric signal

---

## Part X: Experience Design

### 10.1 The 10/10/10 Framework

**10 seconds:** The daydream starts. No instructions needed. Situation pressures shape what surfaces.

**10 minutes:** Structure becomes felt. The daydream circles charged material. Patterns emerge. With conductor input: the participant discovers they can lean into intensity or pull back.

**10 hours (across sessions):** The world has changed. Choices were made — some deliberate, some through accumulated attention. The participant knows these characters. Between sessions, they find themselves thinking about it. The computational daydream has seeded an actual daydream.

### 10.2 Endgame Criterion

The medium is working when the participant involuntarily revisits the material in their own mind between sessions.

---

## Part XI: Open Questions

### Architecture
- Graph density: ratio of edges to nodes. Too sparse = predetermined, too dense = noise.
- Graph size for 30-minute performance. Hypothesis: 200-500 nodes.
- MCP turn management: how to run extended generation loops. Options: Claude Code agent loop, batched "generate N more" sessions, lightweight automation harness.

### Engine
- Goal type switching frequency. Too stable = monotone, too volatile = incoherent.
- Serendipity threshold tuning. Start at 3 indices, adjust empirically.
- Critic sensitivity. Too sensitive = thrashing, too insensitive = ruts.
- Ripeness escalation rates per situation type. Linear? Exponential? Situation-specific?

### World Bible
- Minimum viable size. Can the engine produce interesting output from 6 events, 2 characters?
- Constraint granularity. "Kai avoids confrontation" vs "Kai changes the subject when emotional topics arise directly, but can address them obliquely through shared activity."

### Rendering (downstream, not blocking engine work)
- Video prompt vocabulary supported by existing LoRA pipeline.
- Lyria responsiveness to emotional/abstract prompts vs genre/instrument prompts.
- Cross-modal coherence evaluation method.

---

## Appendix A: Document Lineage

| Source Document | Disposition |
|----------------|------------|
| `conducted-daydreaming-architecture.md` | Core architecture absorbed here. Vault-as-personal-memory applies to deferred autobiographical mode only. |
| `conducted-daydreaming-interaction.md` | World state, events, session loop absorbed. Biometric sections deferred. Kai/Maren walkthrough remains valid as example content. |
| `conducted-daydreaming-hardware.md` | Entirely deferred. ACL2 material relevant at Tier 3 validation. |
| `instrument-stack-architecture.md` | Conductor state vector, agent contracts remain foundational. Leveled conductor state here supersedes flat vector. |
| `narrative-conducting.md` | Selection-as-conducting, counterfactual preservation remain valid. |
| `generative-steering.md` | Authoring/DJing/Conducting distinction, prep/performance split remain foundational. |
| `conductor-landscape.md` | Technology mapping, gesture capture relevant at Stage 5+. |
| `ims-exploration-notes.md` | 5-layer architecture, 10/10/10, playtest methodology relevant throughout. |

## Appendix B: References

**DAYDREAMER**
- Mueller, E.T. (1990). *Daydreaming in Humans and Machines.* Norwood, NJ: Ablex.
- Mueller, E.T. (2022). "An Update on DAYDREAMER."
- Source: `github.com/eriktmueller/daydreamer` (Common Lisp, GPL-2.0)

**Minsky**
- Minsky, M. (1986). *The Society of Mind.*
- Minsky, M. (2006). *The Emotion Machine.* Key imports: Critic-Selector mechanism, six-level cognitive hierarchy, emotions as mode controllers.

**Default Mode Network**
- Menon, V. (2023). "20 years of the default mode network." *PNAS*.

**Model Context Protocol**
- `modelcontextprotocol.io` — specification.
- Adopted by OpenAI, Google, Microsoft, Salesforce. Donated to Linux Foundation, early 2026.
