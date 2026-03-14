# Response

The graph is the membrane. The stage packet is downstream. The main seam problems are boundary leakage and one missing strip of lineage, not lack of ontology. `(11-settled-architecture.md, “The Architecture in One Sentence,” “Where the graph sits,” “Stage seam”; 12-prior-work-synthesis-against-settled-architecture.md, “The graph is the real cross-level interface”)`

## Findings ordered by severity with citations

1. **Critical: the real failure mode is lane-collapse.**
   The canonical docs are explicit that the system shares control geometry, not ontology; `L3` is a traversal scheduler, not cognition; and the stage seam must stay thin. Older seam-writing drifts back toward a shared meta-model by serializing cross-level `PressureBase`, `TraversalPressure`, `Context`, `Proposal`, and `CommitRecord` as if they were seam objects. `05-stage-integration.md` only avoids direct contradiction because it says the `engine` block is for logging/debugging/replay and the `stage` block is the only part Scope must understand. If that fence slips, the graph and stage contract get polluted by internal theory. `(11-settled-architecture.md, “The Architecture in One Sentence,” “Level 3: Traversal Scheduler,” “Stage seam”; 12-prior-work-synthesis-against-settled-architecture.md, “Top-line judgment,” “The graph is the real cross-level interface”; 02-state-model.md, “Core Entities,” “ExplorationContext,” “TraversalDecision”; 05-stage-integration.md, “DreamDirective Schema”)`

2. **High: the actual missing seam is cross-lane lineage from `L1`/`L2` into authored nodes.**
   `11-settled-architecture.md` names cross-level pressure propagation as the hardest open problem and says the graph is the obvious interface. `12-prior-work-synthesis-against-settled-architecture.md` narrows the answer: enrich the graph, do not build a live pressure bus. `13-l2-refactor-synthesis.md` makes the `L2 -> graph` projection very small. But the fixtures mostly stop at `L3`-ready scheduler tags. City Routes has `pressure_tags`, `setup_refs`, `payoff_refs`, `event_commit_potential`, and the rest of the scheduler surface, but it does **not** carry explicit `origin_pressure_refs[]` / `derived_from_concerns[]` or equivalent `L2` lineage. So `L3` can schedule authored material, but it still cannot read which nodes are residues of which character pressures except through hand-authored theme tags. `(11-settled-architecture.md, “Open,” especially “Cross-level pressure propagation”; 12-prior-work-synthesis-against-settled-architecture.md, “The graph is the real cross-level interface”; reading-list/13-l2-refactor-synthesis.md, “Minimal Graph Seam from L2”; daydreaming/fixtures/city_routes_experiment_1_v0.yaml)`

3. **High: two current scheduler fields are not seam-stable enough yet.**
   The `L3` notes want `setup_refs[]` / `payoff_refs[]` to support earnedness and `option_effect` to support structural tension. In City Routes, `setup_refs` and `payoff_refs` mix true ids (`e1_train_missed`) with undeclared symbolic tokens (`route_rewritten`, `dead_pager_glow`, `silver_case_visibility`, etc.). That makes `preparation_satisfied` weaker than the notes imply, because the refs are not in a clearly declared namespace. Also, the agreed `option_effect` vocabulary in the L3/Suspenser notes is small (`close | open | clarify | none`), but the fixture currently uses a much richer uncontrolled set (`reroute`, `escalate`, `conceal`, `suspect`, `commit`, `release`, etc.). That is a concrete seam mismatch, not a stylistic quibble. `(reading-list/08-l3-experiment-1-synthesis.md, “Structural tension from Suspenser,” “Minimal graph annotation burden”; reading-list/02-dodm-extraction.md, “MOTIVATION feature,” “Node-side annotations”; reading-list/04-suspenser-extraction.md, “Node-side annotations”; daydreaming/fixtures/city_routes_experiment_1_v0.yaml)`

4. **Medium: `L1` should not write “the graph”; it should write an annotation/proposal sidecar next to it.**
   The critic-test doc is unambiguous: `L1` reads a limited graph projection plus optional pressure/trouble tags, and writes deficiency annotations, proposal records, evaluation breakdowns, accepted recommendations, and mutation lineage. Whether accepted proposals are auto-applied is explicitly separate. That matches the canonical claim that the graph is a human-authored, human-curated creative asset. So the shared seam needs a small proposal/annotation surface adjacent to the graph, not richer node ontology. `(reading-list/14-l1-critic-test-synthesis.md, “What the Critic Should See,” “Minimal L1 -> Graph Write Surface,” “Proposal Record”; 11-settled-architecture.md, “Where the graph sits,” “Level 1: Authoring Critic/Expander”)`

5. **Medium: the stage seam is mostly right already, but the docs need a harder fence between graph/interface and playback/runtime.**
   `21-graffito-v0-playback-contract.md` is doing the correct thing: graph fixture + trace fixture -> normalized cycle packet -> consumer-specific projection. `17-game-engine-architecture.md` also insists the adapter/selection layer between Dreamer state and node arrival is load-bearing. So the stage should consume a joined runtime packet, not parse the graph or engine theory directly. That means per-cycle `family`, `goal_type`, `visit_count`, and `last_seen_cycle` belong in the playback join, not the authored graph. Authored `variant_family` on nodes is fine; runtime `family` is not the same thing. `(daydreaming/Notes/experiential-design/21-graffito-v0-playback-contract.md, “Why This Seam Exists,” “Join Rules,” “Normalized Cycle Packet,” “Consumer Projections”; daydreaming/Notes/experiential-design/17-game-engine-architecture.md, “The Dreamer (the game logic)” note on the adapter/selection layer; 05-stage-integration.md, “DreamDirective Schema”)`

6. **Low: some roadmap/L3 notes are stale relative to the fixtures, and that can waste work.**
   `reading-list/08-l3-experiment-1-synthesis.md` and `13-execution-roadmap.md` still talk as if Graffito needs edges and `delta_tension` / `delta_energy` added, but the current Graffito fixture already has explicit edges plus `delta_tension`, `delta_energy`, and `priority_tier`. The remaining Graffito gap is not edges/deltas. It is the thinner scheduler/readability surface compared with City Routes. Use the fixture, not the stale sentence. `(reading-list/08-l3-experiment-1-synthesis.md, “Current graph suitability”; 13-execution-roadmap.md, “Graffito L3 Pilot,” “Immediate next move”; daydreaming/fixtures/graffito_v0_scenes_3_4.yaml)`

## Minimum viable seam

1. **Core authored graph node/edge surface**
   Keep the shared graph small and graph-native:
   `id`, `scene_ref`, `place_id`, `situation_id` / `situation_ids`, `line_id` / `subplot_id` when used, `characters`, `objects`, `visual_description`, `tags`, and explicit `edges`.
   For `L3`, add only the scheduler-readable fields already converging in City Routes: `availability_test`, `priority_tier`, `delta_tension`, `delta_energy`, `setup_refs[]`, `payoff_refs[]`, `pressure_tags[]`, `event_id`, `event_commit_potential`, `option_effect`, `importance`, `resolution_path_count`. `(reading-list/08-l3-experiment-1-synthesis.md, “Minimal graph annotation burden”; reading-list/01-facade-extraction.md, “Minimal L3 contract implied by Facade”; reading-list/02-dodm-extraction.md, “Minimal L3 contract implied by DODM”; daydreaming/fixtures/city_routes_experiment_1_v0.yaml)`

2. **One thin cross-lane lineage strip**
   For material that comes from `L1`/`L2` and survives curation, add one stable lineage field: pick **either** `origin_pressure_refs[]` **or** `derived_from_concerns[]` and standardize on it. Pair that with lightweight `pressure_tags[]`. That is the minimum needed for cross-lane composition. Everything richer from `L2` can stay out for now. `(12-prior-work-synthesis-against-settled-architecture.md, “The graph is the real cross-level interface”; reading-list/13-l2-refactor-synthesis.md, “Minimal Graph Seam from L2”)`

3. **`L1` annotation/proposal sidecar**
   Store `deficiency annotations`, `proposal records`, `evaluation breakdowns`, `accepted recommendation`, and `mutation lineage` adjacent to the graph. This is part of the seam, but it is not node schema. `(reading-list/14-l1-critic-test-synthesis.md, “Proposal Record,” “Minimal L1 -> Graph Write Surface”)`

4. **Adapter/runtime packet downstream of the graph**
   Project graph + traversal state into the playback packet: `node_id`, `family` / `goal_type` or `traversal_intent`, `tension`, `energy`, `primary_situation_id`, `dwell_s`, `visit_count`, `last_seen_cycle`, plus renderer/narration-facing fields. Keep the stage block thin. `(11-settled-architecture.md, “Stage seam”; 21-graffito-v0-playback-contract.md, “Normalized Cycle Packet”; 05-stage-integration.md, “DreamDirective Schema”)`

## Do not put this in the graph

* `context_id`, `assumption_patch`, context trees, branch depth/status, ATMS support sets, or anything else from `L2` hypothetical bookkeeping. `(11-settled-architecture.md, “Ephemeral lookahead,” “Stage seam”; 02-state-model.md, “ExplorationContext”)`
* Full `CharacterConcern`, `TraversalPressure`, appraisal objects, reminding cascades, or social-practice machinery. If later needed, project summary tags only. `(reading-list/13-l2-refactor-synthesis.md, “Recommended L2 State Model,” “Minimal Graph Seam from L2”; reading-list/07-versu-extraction.md, “What maps to L2,” “What to take”)`
* `L3` diagnostics, candidate sets, score breakdowns, beam-search paths, manipulation penalties. Those are runtime/debug, not graph schema. `(11-settled-architecture.md, “Diagnostics,” “Ephemeral lookahead,” “Stage seam”; reading-list/02-dodm-extraction.md, “Feature registry”)`
* Stage control payloads: prompts, seeds, `_rcp_*`, cache resets, soft transitions, negative prompts. Those belong in adapter/stage packets only. `(05-stage-integration.md, “Scope-DRD Integration Points,” “Reserved Control Keys,” “DreamDirective Schema”)`
* Per-cycle playback join fields like `visit_count`, `last_seen_cycle`, and runtime `family` / `goal_type`. Authored `variant_family` is okay; runtime family selection is not a graph field. `(21-graffito-v0-playback-contract.md, “Derived fields,” “Normalized Cycle Packet”; daydreaming/fixtures/graffito_v0_scenes_3_4.yaml)`

## Must fix now

* **Freeze the boundary names.**
  State plainly that there are three surfaces, not one: authored graph, `L1` annotation/proposal sidecar, and runtime playback/stage packet. Treat `DreamDirective.engine` as provenance only, not stage contract. `(11-settled-architecture.md, “Where the graph sits,” “Stage seam”; 05-stage-integration.md, “DreamDirective Schema”)`

* **Add one explicit lineage field for `L1`/`L2` origin.**
  Pick `origin_pressure_refs[]` or `derived_from_concerns[]`. Do not keep both names floating around. `(12-prior-work-synthesis-against-settled-architecture.md, “The graph is the real cross-level interface”)`

* **Make `setup_refs[]` / `payoff_refs[]` mechanically resolvable.**
  Declare the ref namespace. Right now City Routes mixes ids and free symbolic tokens. That weakens the DODM-style earnedness feature. `(reading-list/08-l3-experiment-1-synthesis.md, “Feature registry”; reading-list/02-dodm-extraction.md, “MOTIVATION feature”; daydreaming/fixtures/city_routes_experiment_1_v0.yaml)`

* **Canonicalize `option_effect`.**
  Either use the small agreed Suspenser vocabulary or define an explicit mapping from the current richer verbs to that small set. Right now the fixture and the notes disagree. `(reading-list/04-suspenser-extraction.md, “Node-side annotations”; reading-list/08-l3-experiment-1-synthesis.md, “Structural tension from Suspenser”; daydreaming/fixtures/city_routes_experiment_1_v0.yaml)`

* **Update stale Graffito notes.**
  The fixture already has edges and deltas. Stop pointing effort there and point it at the actual missing pieces. `(13-execution-roadmap.md, “Immediate next move”; reading-list/08-l3-experiment-1-synthesis.md, “Current graph suitability”; daydreaming/fixtures/graffito_v0_scenes_3_4.yaml)`

## Later refinements

* From `L2`, add `appraisal_summary_tags`, `dominant_emotion_type/intensity`, `active_practice_tags`, `reminded_episode_refs`, and `branch outcome / pressure shift tags` only after the small lineage strip is in place. `(reading-list/13-l2-refactor-synthesis.md, “Minimal Graph Seam from L2”)`
* Add `contrast_tags` and `release_potential` only if `L3` actually needs them in scoring. They are suggested in the prior-work synthesis, but they are not required to stabilize the minimum seam. `(12-prior-work-synthesis-against-settled-architecture.md, “The graph is the real cross-level interface”)`
* Feed recent `L3` traversal trouble signals into `L1` critic input through the sidecar annotations, not by bloating node schema. `(reading-list/14-l1-critic-test-synthesis.md, “What the Critic Should See”)`
* If City Routes graduates from pure `L3` substrate to playback/narration, harmonize render-facing emotional fields then. Graffito currently has `valences`; City Routes currently does not. That is a later adapter/render issue, not a shared graph seam blocker. `(21-graffito-v0-playback-contract.md, “Required fields”; daydreaming/fixtures/graffito_v0_scenes_3_4.yaml; daydreaming/fixtures/city_routes_experiment_1_v0.yaml)`

## Bottom line

You do **not** need a shared cross-level ontology. You need:

* a small authored graph schema that `L3` can read,
* one thin lineage strip so `L1`/`L2` residues survive curation,
* an `L1` annotation/proposal sidecar,
* and a downstream adapter packet that keeps stage consumers away from engine internals.

Right now the canonical docs already point there. The two real gaps are missing lineage from `L1`/`L2` into nodes and unstable field vocabularies in the current `L3` fixture. Fix those, and the seam is viable without getting fancy. `(11-settled-architecture.md, “Where the graph sits,” “Open”; 12-prior-work-synthesis-against-settled-architecture.md, “The graph is the real cross-level interface”; reading-list/13-l2-refactor-synthesis.md, “Minimal Graph Seam from L2”; reading-list/14-l1-critic-test-synthesis.md, “Minimal L1 -> Graph Write Surface”; daydreaming/fixtures/city_routes_experiment_1_v0.yaml)`

Look away for a moment. Unclench your jaw. Drop your shoulders.
