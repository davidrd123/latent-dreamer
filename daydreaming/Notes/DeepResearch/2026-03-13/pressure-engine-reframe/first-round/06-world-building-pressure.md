## Top-line judgment

Narrow yes, broad no.

World-building can be pressure-driven only for a small class of **structural deficiencies over a typed world model**. In that narrow form, it is genuinely computable. Outside that zone, it collapses into dressed-up structured prompting.

So the honest call is:

* **Viable$_{70%}$ as an upstream authoring assistant / world-state linter-plus-expander**
* **Not viable$_{20%}$ as a general autonomous world-building engine**
* **Should stay off the current shipping path for now**

The docs already point this way. `01-reframe-summary.md` labels Level 1 world-building a **research direction**. `02-state-model.md` labels `ArtifactDeficiency` a **research hypothesis** and raises the exact failure mode: if the pressures are not computable from structured state, the loop becomes “LLM judges its own output.” The shipping docs are stricter: `17-game-engine-architecture.md` says the graph is the **primary creative artifact** and the authoring pipeline is load-bearing; `18-graffito-vertical-slice.md` makes the first proof a hand-authored slice; `21-graffito-v0-playback-contract.md` freezes the downstream seam; `14-operating-map.md`, `deep-research-report (5).md`, `00-comparison-of-results-4-and-5.md`, and `reply.md` all separate the shipping/art track from the research/cognition track.

So: keep L1 as upstream research. Do not let it rewrite the graph-first mainline until it wins a hard A/B test.

---

## 1. Is world-building actually a pressure-loop problem?

**Answer:** only partly.

It is **not** the same kind of loop as Level 2 character cognition. Treating it as fully unified with Mueller-style inner-life dynamics is a category error. L2 has a real grounding story: agents have concerns, appraise events, rehearse, rationalize, avoid, retaliate. L1 does not have that grounding. It has **authorial deficiencies in a constructed artifact**.

So the right model for L1 is not “character-like cognition for worlds.” It is:

> **deficiency-driven search and repair over an explicitly typed world state**

That means the shared machinery is limited.

**Shared with L2**

* prioritizing unresolved things
* explicit fork/proposal/evaluate loop
* provenance
* memory of prior attempts
* backtracking / mutation
* human gating

**Not actually shared**

* psychologically grounded appraisal
* endogenous “what hurts” semantics
* clean success criteria from agent motivation
* any strong claim that “the same engine” is running unchanged

So yes, there is a real pressure-loop architecture here, but only if you narrow “pressure” to **detectable structural defects**. If you try to include theme, taste, vibe, subtext, or “interestingness” as first-class pressures, the whole thing reverts to prompt theater.

A better code name than `ArtifactDeficiency` would be `WorldDefect` or `StructuralDeficiency`. “Concern” is too anthropomorphic here.

---

## 2. What counts as a world-building pressure, concretely?

Here are **10 candidate pressure types**. Only the first **6-7** are good enough for v1 scheduling.

| Pressure type                       | Verdict                    | What it means                                                                               | Detect from structured state                                                  | Main failure modes                                                                           |
| ----------------------------------- | -------------------------- | ------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------- |
| **goal_without_resistance**         | **Strong**                 | A character or situation has a mission/desire with no blocker, cost, opponent, or threshold | character goals/mission, stakes, blockers, event preconditions                | FP: intentionally calm slice. FN: obstacle exists only in prose                              |
| **conflict_without_response**       | **Strong**                 | A threat or tension exists, but no actor/place/object/event can respond to it               | situation stakes/threat, participants, character capabilities, response paths | FP: deliberate helplessness. FN: response path implicit                                      |
| **setup_without_payoff**            | **Strong**                 | A setup, charged object, or early tension has no payoff or fallout path                     | setup refs, payoff refs, object-event links, event consequences               | FP: long-horizon setup. FN: payoff hidden in prose                                           |
| **place_without_role**              | **Strong**                 | A place has identity/atmosphere but no dramatic function                                    | place affordances, linked situations, linked events, access constraints       | FP: pure-atmosphere place by design. FN: role implied but untyped                            |
| **charged_object_without_function** | **Strong**                 | An object is marked important but does not affect conflict, route, or event                 | charged objects, object links to situations/events/characters                 | FP: object reserved for later. FN: function only textual                                     |
| **disconnected_subgraph**           | **Strong**                 | A character/place/situation cluster is weakly connected to the rest of the world            | graph connectivity across entities, situations, objects, places               | FP: intentional enclave. FN: hidden semantic linkage                                         |
| **single_path_bottleneck**          | **Medium**                 | An important situation has either zero response paths or exactly one brittle path           | resolution paths, capability map, access constraints, event preconditions     | FP: linear story by choice                                                                   |
| **motif_orphaning**                 | **Medium**                 | A motif/obsession is named in the brief but has no carriers or recurrence sites             | motif tags on places/events/objects/situations                                | FP: motif intentionally latent                                                               |
| **tonal_monotony**                  | **Weak**                   | Coarse tone distribution is too uniform across situations/places                            | coarse tone vectors per situation/place                                       | FP: monotone atmosphere may be correct. FN: semantic sameness can hide under numeric variety |
| **backstory_vacuum**                | **Weak / do not schedule** | A character/place lacks history hooks                                                       | backstory fields/hooks                                                        | FP is huge. Missing backstory is often fine                                                  |

### Hard recommendation

Use these as **v1 schedulable pressures**:

* `goal_without_resistance`
* `conflict_without_response`
* `setup_without_payoff`
* `place_without_role`
* `charged_object_without_function`
* `disconnected_subgraph`

Maybe include:

* `single_path_bottleneck`

Do **not** schedule on:

* `tonal_monotony`
* `backstory_vacuum`

Those are linter warnings at most.

---

## 3. What minimal world state is needed?

The concrete anchor here is `23-brief-city-night-pursuit.md`. It already has the right shape: characters, places, events, situations, charged objects, and coarse pressure-like fields. But it is **not enough yet** for the strong pressures above. To make the good pressures computable, you need a few extra structural fields.

### Minimum schema

| Entity                    | Minimum fields                                                                                                                                    | Why they are required                             |
| ------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------- |
| **Character**             | `id`, `goals/missions`, `stakes`, `fears/avoidances`, `capabilities`, `relationship_ids`, optional `history_hooks`                                | needed for resistance and response detection      |
| **Place**                 | `id`, `affordances`, `access_constraints`, `tone_vector`, `linked_situation_ids`, `linked_event_ids`                                              | needed for place role and routing                 |
| **Relationship**          | `a`, `b`, `type`, `valence`, `tension`, optional `history_hook`                                                                                   | needed for coupling and plausible response paths  |
| **Situation**             | `id`, `participants`, `place_id`, `stakes`, `blockers`, `response_paths`, `ripeness`, `escalation_rate`, `tone_vector`, `setup_ids`, `payoff_ids` | this is the load-bearing unit                     |
| **Event / Affordance**    | `id`, `preconditions`, `consequences`, `affected_entities`, `resolves_situations`, `creates_situations`                                           | needed for payoff, route, and consequence scoring |
| **ChargedObject / Motif** | `id`, `carriers`, `linked_situations`, `linked_events`, `function_tags`, `salience`                                                               | needed for object-function and motif pressures    |

### What `23` already gives you

* characters
* places
* events
* situations
* coarse emotional fields
* charged objects / obsessions / brief-level motifs

### What `23` is still missing for a real pressure loop

* explicit `blockers`
* explicit `response_paths`
* explicit `setup_ids` / `payoff_ids`
* explicit object-to-event / object-to-situation links
* explicit capability or legitimacy map for responders

Without those fields, the strong pressures are not computable. Then the model has to infer structure from prose, which is exactly the collapse you are trying to avoid.

So the minimum world model is **not** “rich lore.” It is a **typed constraint graph** over characters, places, situations, events, and charged objects.

---

## 4. What should the operator taxonomy be?

The current list in `02-state-model.md` is plausible, but too fuzzy. `complicate`, `ground`, `connect`, `contrast`, `historicize` sounds good and would get mushy fast.

A defensible v1 set is this:

| Operator          | Responds to                                                             | Reads                                               | Produces                                                            | Keep?     |
| ----------------- | ----------------------------------------------------------------------- | --------------------------------------------------- | ------------------------------------------------------------------- | --------- |
| **obstruct**      | goal_without_resistance                                                 | goals, stakes, blockers, access constraints         | a blocker, cost, opponent, or threshold                             | **Yes**   |
| **route**         | conflict_without_response, single_path_bottleneck                       | capabilities, relationships, places, preconditions  | a credible response path, ally, refuge, workaround, or access route | **Yes**   |
| **concretize**    | place_without_role, abstract tension                                    | places, situations, motifs, affordances             | a situation or event grounded in a place                            | **Yes**   |
| **couple**        | disconnected_subgraph, charged_object_without_function, motif_orphaning | connectivity graph, objects, motifs, relations      | a typed relation via shared event/object/history                    | **Yes**   |
| **consequence**   | setup_without_payoff, charged_object_without_function                   | setups, events, consequences, objects               | payoff, fallout, escalation, or irreversible turn                   | **Yes**   |
| **differentiate** | tonal_monotony, pattern overuse                                         | tone vectors, place reuse, actor reuse, motif reuse | a contrast in register, place, or social geometry                   | **Maybe** |

### What to do with the current verbs

* `complicate` should split into **obstruct** and **consequence**
* `ground` should become **concretize**
* `connect` should become **couple**
* `contrast` should become **differentiate**
* `historicize` should **not** be top-level in v1

`historicize` is the trap. It too easily produces decorative lore. Use it only as a tactic inside `couple` or `concretize`, where a past event creates present leverage. Backstory that does not change current pressure is filler.

---

## 5. How should world-building proposals be evaluated?

This is the make-or-break point. If evaluation is fuzzy, the whole architecture is fake.

The rule is simple:

> **A proposal must be a typed world-state diff, not a prose fragment.**

If the proposal cannot be evaluated as a structural delta, it is not part of the pressure loop. It is just generated text.

### Evaluation criteria

| Criterion                     | How to score it                                                             | Why it matters                               |
| ----------------------------- | --------------------------------------------------------------------------- | -------------------------------------------- |
| **target-pressure reduction** | rerun the triggered detector before/after; did the specific defect shrink?  | proves the proposal addressed its stated job |
| **cross-pressure gain**       | rerun all detectors; did it also fix another defect?                        | real serendipity signal                      |
| **leverage**                  | how many existing entities/situations/events does the diff touch or enable? | prevents isolated lore blobs                 |
| **response coverage delta**   | did the number of valid response paths increase?                            | catches real dramatic utility                |
| **setup/payoff closure**      | did the number of linked setups with payoff/fallout increase?               | prevents dead setups                         |
| **connectivity delta**        | did the world graph become less fragmented in a useful way?                 | prevents disconnected subworlds              |
| **constraint satisfaction**   | hard pass/fail against invariants and world rules                           | no contradiction drift                       |
| **economy penalty**           | penalize new entities/details that add no links, paths, or stakes           | blocks content bloat                         |
| **contrast contribution**     | only for `differentiate`: did coarse distribution actually diversify?       | keeps contrast from becoming hand-waving     |

### The scoring model

A proposal should pass three gates:

1. **Validity**: no hard world-rule violations
2. **Structural utility**: it measurably improves at least one hard metric
3. **Human keepability**: a human wants to keep, edit, or merge it into the world

That third gate matters. Pure structural optimization will give you tidy dead worlds.

A simple first-pass score would be:

[
\text{score} =
3(\Delta \text{target pressure})

* 2(\Delta \text{response coverage})
* 2(\Delta \text{setup/payoff closure})
* 1(\Delta \text{connectivity})
* 1(\text{cross-pressure gain})

- 3(\text{constraint violations})
- 1(\text{gratuitous additions})
  ]

You do **not** need this exact formula. You **do** need the discipline behind it.

### Hard rule

If a proposal only adds “interesting descriptive detail” and does not change typed state, score it zero.

That is how you avoid “the LLM liked its own idea.”

---

## 6. What does memory and serendipity mean in world-building?

It means something narrower than in Mueller, but still useful.

### What counts as an episode

An authoring-time episode is:

* the active pressure set
* the operator used
* the proposal diff
* the evaluation result
* the human disposition: accept / reject / edit / merge
* the failure reason if rejected

So an episode is not “a scene.” It is **an attempted structural repair**.

### Retrieval keys

Store these indices:

* pressure types served
* operator used
* entities touched
* situations touched
* places touched
* objects/motifs touched
* tone bucket
* failure reason
* human outcome

### Dead ends

Dead ends must be remembered. Otherwise the engine will rediscover the same generic move forever.

Store rejected proposals with explicit reason codes:

* violated constraint
* too generic
* added detail without leverage
* duplicated existing structure
* wrong tone for brief
* required too much human rewriting

Then penalize reuse of the same pressure-operator-pattern when the same failure reason repeats.

### Serendipity

Post-hoc scan **is** useful here, but only for structural pressures.

Example:

* a proposal aimed at `charged_object_without_function`
* also resolves `disconnected_subgraph`
* and adds a `response_path`

That is a real multi-pressure win.

For weak pressures like `tonal_monotony`, post-hoc scan becomes mush. Do not build your architecture around that.

---

## 7. Where should human curation sit?

At the commit gate. Always.

The interesting model is not autonomy. It is **engine proposes diffs, human curates world canon**.

### Recommended human workflow

| Human action                          | What the system does next                                |
| ------------------------------------- | -------------------------------------------------------- |
| **accept**                            | apply diff, rerun detectors, reprioritize pressures      |
| **reject**                            | store diff + reason code, penalize similar retries       |
| **edit**                              | human modifies typed diff, validator reruns, then commit |
| **merge**                             | combine compatible diffs into one world update           |
| **reweight pressures**                | change what the loop cares about for this brief          |
| **freeze entities/places/situations** | remove them from mutation space to stop churn            |

### Where the human should sit in the loop

1. **Author seed world**
2. **Set constraints and freezes**
3. **Let engine produce ranked diffs**
4. **Curate in batches**
5. **Commit accepted diffs**
6. **Only then generate graph material from updated world**

Do **not** make the human read raw prose paragraphs to discover what changed. Show the structural diff first, optional prose gloss second.

If curation effort is equal to direct authorship, the architecture lost.

---

## 8. How should this relate to the graph-first mainline?

Very simply:

**It should remain a parallel research track.**

The current shipping path in `17`, `18`, and `21` is:

* human-authored graph
* traversal
* deterministic playback packet
* deterministic stage seam

That is the right path right now.

### Direct answers

* **Should engine-assisted world-building be a parallel research track only?**
  **Yes.**

* **Should it produce candidate graph material for human curation?**
  **Yes, eventually. That is the best plausible role.**

* **Should it replace hand-authoring of graph material?**
  **No evidence. Not now. Maybe never.**

* **What is too early to change?**

  * the graph-first mainline
  * the Graffito slice plan
  * the playback contract
  * the deterministic stage seam
  * the claim that the graph is a curated creative artifact

The selected docs are blunt about sequencing. `17-game-engine-architecture.md` makes node quality a human authoring problem. `18-graffito-vertical-slice.md` makes the first proof a hand-authored slice. `deep-research-report (5).md` and `00-comparison-of-results-4-and-5.md` say minimal graph/traversal discipline should come before broader generation claims. `reply.md` says the honest claim today is narrower than some docs imply.

So keep it upstream, offline, and optional.

---

## 9. What is the smallest falsifiable experiment?

This needs a real baseline. Otherwise you learn nothing.

### Fixture

Use a **reduced version of `23-brief-city-night-pursuit.md`** because it already has the right world shape.

#### Seed world

* characters: `courier`, `hostess`, `runner`
* places: `platform`, `apartment`, `bridge`
* situations: `s1_the_run`, `s3_false_refuge`, `s5_the_threshold`
* charged objects: `silver package case`, `apartment key on red string`
* events: `e1_train_missed`, `e4_bridge_lockdown`

Then add the missing structural fields:

* blockers
* response paths
* setup/payoff links
* object-event links
* capabilities

### Intentional defects

Seed the fixture with **4 explicit holes**:

1. `place_without_role` on `apartment`
2. `setup_without_payoff` on `apartment key`
3. `conflict_without_response` on `s5_the_threshold`
4. `disconnected_subgraph` between `hostess` and main route logic

### Pressure set

Use only 4 pressures:

* `place_without_role`
* `setup_without_payoff`
* `conflict_without_response`
* `disconnected_subgraph`

### Operator set

Use only 4 operators:

* `concretize`
* `consequence`
* `route`
* `couple`

### Procedure

#### Baseline A

One-shot structured prompting:

* “Given this typed seed world, propose 12 world-building improvements.”

#### System B

Pressure loop:

* detect strongest defect
* choose operator
* propose typed diff
* validate
* rerun detectors
* repeat for 12 proposals max

Same model, same token budget, same human reviewer.

### Evaluation output

Measure:

* hard defect count before/after
* accepted proposals count
* accepted proposals per human review minute
* edit burden per accepted proposal
* number of accepted proposals that obviously yield graph-worthy scene material

### Human review criteria

For each proposal:

1. keep / reject / edit / merge
2. “Does this create usable world material?”
3. “Does this fit the brief?”
4. “Would I rather have gotten this from the engine than authored it directly?”

### Win condition

Call it a win only if System B beats Baseline A on both:

* **structural improvement**: at least 50% reduction in seeded hard defects
* **human utility**: more accepted proposals per minute, with at least 3 accepted proposals needing only light edits

### Failure condition

It fails if:

* it does not beat the one-shot baseline
* most accepted ideas are generic filler
* human rewriting cost is high
* structural scores improve but nobody wants the material

If it fails, say it failed. Then it was prompt theater.

---

## 10. If this works, what is the honest claim?

The strongest honest claim is:

> **a pressure-driven authoring assistant for typed fictional worlds**

A slightly blunter version:

> **a structured repair loop over LLM proposals for underdetermined worlds**

Not:

* an intrinsic-drive world-building engine
* a generalized Mueller-style exploration architecture
* autonomous world-building
* a replacement for authored graph creation

If it works well, it still remains **upstream authoring support**, not the core shipping medium.

---

## Strongest honest claim

**This is not a general world-building engine. It is a research-stage authoring assistant that can detect and repair a narrow set of structural deficiencies in a typed world model.**

That is the strongest claim the selected docs can support.

---

## What should change now vs stay research-only

### Change now

* adopt a tighter typed world fixture schema, closer to `23`, with explicit blockers, response paths, setup/payoff links, and object links
* treat world-building proposals as **typed diffs**, never prose blobs
* build an offline **world-defect linter** for the hard pressures only

### Research-only

* iterative pressure scheduling
* operator loop for L1
* memory of rejected authoring diffs
* post-hoc serendipity scan
* A/B evaluation against one-shot structured prompting

### Do not do now

* do **not** let L1 touch the shipping graph-first path
* do **not** change the Graffito slice plan
* do **not** modify the playback contract
* do **not** import fuzzy pressures like `tonal_monotony` or `backstory_vacuum` into the scheduler
* do **not** let the same LLM generate and judge without structural external checks
* do **not** claim L1 is the same kind of engine as L2

The clean boundary is:

* **shipping path**: authored graph → traversal → playback packet → deterministic stage
* **research path**: typed world fixture → defect detection → proposal diffs → human curation

Keep those separate until the research path proves it buys something real.
