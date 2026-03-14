# Response

## Findings

1. **The largest unresolved risk is still the graph seam.** If the graph cannot carry enough typed residue from L1/L2 for L3 to score, explain, and revisit material without peeking into hidden engine state or free-form prose, then the two-lane split is cosmetically neat but operationally fake. The canonical doc itself flags cross-level pressure propagation as the hardest unsolved problem, and the prior-work synthesis explicitly says to make the graph the membrane rather than invent a live shared pressure bus. (`11-settled-architecture.md`, “Open”; `12-prior-work-synthesis-against-settled-architecture.md`, “The graph is the real cross-level interface”; `13-l2-refactor-synthesis.md`, “Minimal Graph Seam from L2”; `08-l3-experiment-1-synthesis.md`, “Minimal graph annotation burden”)

2. **L3 has a very clear kill condition:** if it does not beat simpler traversers in watched output and leverage, it is not a real layer. The docs are unusually explicit here. If the Façade-shaped scheduler or the full feature registry cannot beat baseline traversal on intentionality, event approach, recall timing, resilience, and controlled variety, then the extra machinery is not justified. (`11-settled-architecture.md`, “Three Experiments”; `08-l3-experiment-1-synthesis.md`, “Experiment 1 question” and “If C does not clearly beat A and B…”; `11-graffito-phase-1-pilot-checklist.md`, “Failure signals”; `12-city-routes-experiment-1-checklist.md`, “Success condition”)

3. **The control plane is not actually clean end-to-end yet.** The canonical architecture says one control plane, with the conductor biasing L3. But `05-stage-integration.md` still gives the conductor parallel authority, direct stage modulation, and operator forcing. That is not wording drift. That is a live interface bug waiting to happen. (`11-settled-architecture.md`, “The Conductor”; `05-stage-integration.md`, “What the performer steers”)

4. **Even a semantically clean control plane can still fail as an instrument.** The canonical doc already says the APC mapping is unvalidated and may be physically unplayable. If the performer cannot reliably reach the scheduler’s intents, the live product claim fails even if the scheduler is good. (`11-settled-architecture.md`, “Open → Conductor state vector vs APC Mini”; `08-l3-experiment-1-synthesis.md`, “Evaluation frame from Authorial Leverage”; `12-city-routes-experiment-1-checklist.md`, “Authorial leverage”)

5. **The graph/content pipeline is load-bearing in a harsher way than the architecture admits.** It is not enough to have a graph. You need a graph whose nodes are individually watchable, whose metadata is good enough for traversal, and whose maintenance cost is lower than just hand-authoring around the system. Several selected docs say this outright from different angles. (`17-game-engine-architecture.md`, “Dream Graph (the product)” and “Quality gate”; `18-graffito-vertical-slice.md`, “What Needs to Be Built”; `11-settled-architecture.md`, “Open → L1 pressures may require more structured input than exists”; `14-l1-critic-test-synthesis.md`, “What L1 Should Actually Do”)

---

## Top 5 failure modes

### 1. The graph seam is too weak to compose the lanes

**What the failure is**
The graph does not carry enough stable, typed, graph-readable structure to let L3 operate on authored material while preserving provenance from L1/L2. In practice, people start cheating: reading prose, peeking into engine traces, or stuffing hidden ontology back into the scheduler. The graph membrane stops being real. (`11-settled-architecture.md`, “Open”; `12-prior-work-synthesis-against-settled-architecture.md`, “The graph is the real cross-level interface”; `13-l2-refactor-synthesis.md`, “Minimal Graph Seam from L2”)

**Which lane or interface it hits**
L1/L2 → graph, graph → L3, and the dashboard/provenance layer.

**Earliest symptom**
Feature scoring is impossible or mushy using only graph data. `pressure_tags` become vibes instead of discriminative tags. `setup_refs`, `payoff_refs`, `origin_pressure_refs`, and event metadata are missing or unused. The dashboard explains moves with post-hoc prose instead of actual provenance. (`08-l3-experiment-1-synthesis.md`, “Minimal graph annotation burden”; `12-prior-work-synthesis-against-settled-architecture.md`, “Minimum useful node metadata”)

**Cheapest test**
Take 10 nodes from City Routes. For each candidate node, force the scorer to compute `trajectory_fit`, `preparation_satisfied`, `event_homing`, `recall_value`, and a human-readable reason using only graph annotations plus traversal state. Ban access to L2 internals and raw brief prose. Count which features are undefined or ambiguous. (`08-l3-experiment-1-synthesis.md`, “Feature registry”; `12-city-routes-experiment-1-checklist.md`, “Implement the feature registry”)

**Best mitigation**
Freeze a minimal graph contract and linter now. Make `situation_id`, participants, place, stakes, `pressure_tags`, `origin_pressure_refs`, `setup_refs`, `payoff_refs`, event metadata, and release/contrast tags first-class and validated. Refuse to add a live shared pressure bus unless this graph-only contract demonstrably fails. (`12-prior-work-synthesis-against-settled-architecture.md`, “The graph is the real cross-level interface”; `13-l2-refactor-synthesis.md`, “Minimal Graph Seam from L2”)

---

### 2. L3 does not earn its cost

**What the failure is**
The scheduler is formally coherent but behaviorally thin. It produces runs that are not meaningfully better than weighted random plus recency suppression. Then L3 is a cost center, not a layer. (`11-settled-architecture.md`, “Three Experiments → 1. L3 scheduler test”; `08-l3-experiment-1-synthesis.md`, “Experiment 1 question”)

**Which lane or interface it hits**
Shipping lane core: authored graph → L3 traversal scheduler.

**Earliest symptom**
Priority tiers never matter, target trajectory barely changes movement, and the watched run looks reproducible by two simple heuristics. On the richer substrate, graph changes require broad retuning instead of local metadata edits. (`11-graffito-phase-1-pilot-checklist.md`, “Failure signals”; `12-city-routes-experiment-1-checklist.md`, “Change-resilience checks”)

**Cheapest test**
Do the exact staged falsifier the docs already prescribe: Graffito A/B for scheduler shape, then City Routes A/B/C for actual leverage. Use blind watched-run judgments plus the stated metrics: revisit gaps, overexposure, event approach legibility, recall quality, controlled variety, and resilience after a small graph edit. Do **not** treat Graffito alone as the answer to the full L3 thesis. (`13-execution-roadmap.md`, “Top-line sequence”; `08-l3-experiment-1-synthesis.md`, “Current graph suitability”; `11-graffito-phase-1-pilot-checklist.md`, “Why Graffito is only a pilot”; `18-city-routes-substrate-draft.md`, “Counts”)

**Best mitigation**
Keep only the smallest scheduler layer that wins. If B beats A but C does not beat B, keep the Façade baseline and stop there. Do not keep feature registry or structural-tension machinery on principle. (`08-l3-experiment-1-synthesis.md`, “Suggested implementation order”; `12-city-routes-experiment-1-checklist.md`, “Success condition”)

---

### 3. Runtime control semantics split across incompatible vocabularies

**What the failure is**
`TraversalIntent`, `family`, `goal_type`, L2 operators, conductor gestures, and stage commands all remain partially alive at runtime. Different consumers rely on different truth vocabularies. Provenance becomes untraceable. (`11-settled-architecture.md`, “Level 3” and “Stage seam”; `05-stage-integration.md`, “What the performer steers”; `21-graffito-v0-playback-contract.md`, “Normalized Cycle Packet”)

**Which lane or interface it hits**
Conductor ↔ L3, adapter/DreamDirective, renderer/narration/stage seam.

**Earliest symptom**
A cycle can be described in two incompatible ways. The renderer or narration layer quietly relies on `family`/`goal_type` for behavior. The conductor can bypass L3 with direct stage commands. Debugging a single run requires reading three schemas. (`19-graffito-node-schema.md`, “What Is NOT on the Node”; `21-graffito-v0-playback-contract.md`, “Consumer projections”; `05-stage-integration.md`, “The conductor’s inputs enter the same DreamDirective pipeline”)

**Cheapest test**
Run one end-to-end contract audit on a single cycle packet and one live pilot trace. Remove `family` from the runtime selection path and check what breaks. Also build a field-ownership table: every stage-visible field must have exactly one writer. If both conductor and scheduler can write the same thing, fail the test. (`11-settled-architecture.md`, “The Conductor”; `05-stage-integration.md`, “Reserved Control Keys”)

**Best mitigation**
Freeze one runtime vocabulary and one ownership model. In the shipping lane, `TraversalIntent` is the runtime control language. `family`/`goal_type` stay author-facing or debug-only for Graffito v0. The conductor writes bias state and commit confirm/veto, not direct stage parameters. Add schema tests so this stays true. (`11-settled-architecture.md`, “The Conductor” and “Stage seam”; `21-graffito-v0-playback-contract.md`, “Why This Seam Exists”)

---

### 4. The conductor mapping is physically unplayable

**What the failure is**
The control surface has more abstract dimensions than a performer can meaningfully steer. Even if the math is fine, the instrument feels bad. The performer cannot reliably induce the intended traversal behavior. (`11-settled-architecture.md`, “Open → Conductor state vector vs APC Mini”; `17-game-engine-architecture.md`, “The Performer's Instrument”)

**Which lane or interface it hits**
Human performer ↔ conductor bias ↔ L3.

**Earliest symptom**
Most controls are unused. A performer can maybe boost a situation and hit hold, but cannot intentionally cause `recall` vs `release` vs `shift` in repeatable ways. The mapping is learned as arbitrary control lore instead of embodied leverage. (`11-settled-architecture.md`, “What the conductor controls”; `08-l3-experiment-1-synthesis.md`, “Authorial Leverage”; `12-city-routes-experiment-1-checklist.md`, “controlled run diversity” and “conductor expressivity”)

**Cheapest test**
Before the full scheduler, use a mock traverser or Wizard-of-Oz harness and ask a performer to produce the five intents plus two arc shapes repeatedly. Measure reachability, repeatability, and subjective causal feel. This is cheaper than building the whole live stack and tells you more. (`11-settled-architecture.md`, “Open → Prototype the conductor mapping before building the scheduler”)

**Best mitigation**
Collapse the control surface to a few directly legible controls already endorsed by the canonical doc: situation boosts, hold/release bias, escalation bias, recall bias, intensity envelope, and event commit confirm/veto. Derive secondary dimensions internally. Defer tides and abstract dimensions until playability is proven. (`11-settled-architecture.md`, “What the conductor controls”)

---

### 5. The graph pipeline cannot supply enough good, annotated material

**What the failure is**
The runtime assumes a graph that is both creatively strong and structurally annotated. In practice you get one but not the other: good scenes with missing scheduler metadata, or tidy metadata attached to dull nodes. Then traversal quality plateaus because the substrate is bad. (`17-game-engine-architecture.md`, “Scale” and “Quality gate”; `23-brief-city-night-pursuit.md`, “Target Graph Shape”)

**Which lane or interface it hits**
Human authoring → graph asset, later L1 → graph, and any future maintenance loop.

**Earliest symptom**
Authors skip fields, or local edits trigger large retagging cascades. Runs feel like shuffled clips. Revisit logic exposes thin nodes fast. L1 defect detection is unstable because the typed world state is too sparse. (`11-settled-architecture.md`, “Open → L1 pressures may require more structured input than exists”; `12-city-routes-experiment-1-checklist.md`, “Change-resilience checks”; `18-graffito-vertical-slice.md`, “If the visual output is flat”)

**Cheapest test**
Time-box authoring one City Routes cluster with full required annotations, then do three checks: node watchability, metadata completeness, and change-impact after adding 2–3 nodes or altering one event. Measure author-minutes per usable node and metadata churn per change. (`18-city-routes-substrate-draft.md`, “Situation Clusters” and “Annotation Strategy”; `12-city-routes-experiment-1-checklist.md`, “Required substrate” and “Change-resilience checks”)

**Best mitigation**
Separate the gates. First, a node must be worth watching. Second, it must satisfy a minimal annotation contract. Shrink required metadata to what the scorer and critic actually consume; infer defaults for the rest. Keep L1 on a tiny set of strong, computable deficiencies until the typed world schema proves it pays rent. (`17-game-engine-architecture.md`, “Quality gate”; `14-l1-critic-test-synthesis.md`, “Recommended Deficiency Classes” and “Cheap rejection rules”)

---

## Cheapest exposing tests

1. **Graph-only score audit**: compute planned L3 features and reasons from graph annotations alone.
2. **Blind Graffito A/B, then City Routes A/B/C**: watched-run judgment plus mechanical metrics.
3. **Packet ownership audit**: one writer per runtime field, one control vocabulary.
4. **APC reachability session**: can a performer reliably elicit the intended intents?
5. **Author-minutes-per-usable-node test**: one cluster, one edit, measure watchability + annotation churn.

---

## Best mitigations

1. **Freeze and lint the graph membrane** before adding more theory.
2. **Keep only the smallest L3 layer that wins** against baseline.
3. **Lock runtime vocabulary and field ownership** across conductor, scheduler, adapter, and stage.
4. **Reduce conductor controls to directly legible biases** and derive the rest.
5. **Separate content quality gates from metadata gates** and cut mandatory schema aggressively.

---

## Bottom line

The architecture can still be a good direction. But only if three empirical claims survive contact with code:

* the graph seam is strong enough to compose the lanes,
* L3 visibly beats simpler traversal on the right substrate,
* the conductor is playable as an instrument rather than just spec text.

Miss those, and you do not have “a promising architecture with some rough edges.” You have a coherent document stack that hides the fact that the live system has no leverage.

Look away from the screen for a minute. Drop your shoulders.
