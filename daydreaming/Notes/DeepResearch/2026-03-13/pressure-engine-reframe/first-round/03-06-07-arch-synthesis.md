1. Top-line judgment

The reframe is useful, but it is too broad by one layer$_{85%}$. It correctly identifies what is worth keeping from Mueller: pressure scheduling, hypothetical exploration, retrieval/reminding, provenance, backtracking, and explicit commit discipline. It overclaims when it implies that world-building, character cognition, and live traversal are one engine with one public abstraction set. (`01-reframe-summary.md`; `02-state-model.md`; `kernel/doc/mueller-coverage-assessment.md`)

The honest architecture today is narrower:

* **Shipping/mainline:** authored world or authored graph → traversal/scheduling → renderer/stage.
* **Real engine core:** L2 character-level hypothetical exploration.
* **Research hypothesis 1:** L1 world-building as a narrow, computable authoring loop.
* **Research hypothesis 2:** L3 smarter traversal using pressure-shaped sequencing signals.

That means the reframe buys you a **cleaner theory of the upstream decision layer**, not a license to rewrite the mainline as one polymorphic pressure machine. The graph-first stack is still the defensible shipping posture, and the watched run is still the next real falsifier. (`home-base.md`; `14-operating-map.md`; `17-game-engine-architecture.md`; `18-graffito-vertical-slice.md`; `13-project-state.md`; `00-comparison-of-results-4-and-5.md`)

A stronger honest claim than the current one is:

**an authored-graph performance architecture with Mueller-derived exploratory control at the character level, plus pressure-guided authoring and traversal research around it.**

2. What is actually one architecture vs what is not

What is actually one architecture:

There is **one stack** and **one shared control discipline**.

The stack is:

```text
authored/curated material
    → selection / exploration logic
    → normalized runtime packet
    → renderer / music / narration
    → deterministic stage
```

The shared control discipline is:

```text
unresolved state
    → prioritize
    → explore or rank
    → remember what happened
    → record provenance
    → commit only through an explicit gate
```

That shared discipline is real. It is visible in the graph-first specs, the kernel work, and the stage-integration docs. (`ProspectiveDesign/v2/conducted-daydreaming-architecture-v2.md`; `14-operating-map.md`; `05-stage-integration.md`; `deep-research-report (4).md`)

What is **not** one architecture:

**L2 is a real exploration engine.**
This is the strongest part of the reframe. Character concerns, hypothetical forks, rehearsal, reversal, rationalization, retrieval, and policy/salience updates belong together. The kernel is incomplete relative to full DAYDREAMER, but it is still the one place where “pressure → operator → fork → evaluate → feedback” is natively grounded. (`01-reframe-summary.md`; `02-state-model.md`; `04-kernel-gap-analysis.md`; `kernel/doc/mueller-coverage-assessment.md`)

**L1 is not yet that engine.**
L1 is a **researchy authoring loop**, not a proven cognitive architecture. It can become useful if you restrict it to a few computable deficiencies over structured world state. It should not currently be described as the same kind of engine as L2. Right now it is closer to a structured critic-plus-expander for authorship. (`01-reframe-summary.md`; `06-world-building-pressure-questions-for-5pro.md`; `deep-research-report (5).md`)

**L3 is mostly not that engine.**
L3 is primarily a **traversal scheduler over authored material**. It may use pressure-shaped signals, memory, and provenance, but it is not the same thing as context-forking cognition. “What if we show this next?” is not the same kind of fork as “what if this character retaliated?” Treating them as equivalent muddies the whole design. (`01-reframe-summary.md`; `07-l3-traversal-questions-for-5pro.md`; `17-game-engine-architecture.md`; `21-graffito-v0-playback-contract.md`)

So the corrected picture is:

```text
                RESEARCH / AUTHORING SIDE
seed world
   → L1 authoring critic/expander   [optional, narrow, curated]
   → curated world + situations
   → L2 character exploration engine [real engine core]
   → candidate events / beats / indices
   → human curation
   → authored dream graph

                SHIPPING / PERFORMANCE SIDE
authored dream graph + traversal state + conductor bias
   → L3 traversal scheduler
   → normalized cycle packet / DreamDirective
   → renderer / music / narration
   → deterministic stage
```

That is the architecture I’d defend. It keeps the real shared parts and drops the false symmetry.

3. Shared abstractions vs level-specific abstractions

Keep the shared layer **small**. The reframe starts to go bad when it tries to make the shared layer too expressive.

Shared across levels:

**A. Priority signal base**
You can keep a tiny common base for “something unresolved that competes for attention.” It should carry only scheduling fields: identity, source/provenance, intensity, urgency, unresolved flag, decay, maybe polarity/valence. That is enough for ranking and logging. It is not enough to justify one public `Concern` type. (`02-state-model.md`)

**B. Episodic retrieval / reminding**
Multi-index retrieval, reminding cascades, and post-hoc serendipity scans are plausibly shared. This is one of the strongest reusable ideas in the kernel and the reframe. (`02-state-model.md`; `04-kernel-gap-analysis.md`; `kernel/doc/mueller-coverage-assessment.md`)

**C. Provenance**
“Who explored what, under which pressure, using which move, with which assumptions?” That should be everywhere. This is a real upgrade in the reframe. Mandatory provenance is good. (`02-state-model.md`; `05-stage-integration.md`)

**D. Explicit commit gate**
Hypothetical material should not silently mutate canon. This is already a repo-wide stable idea and the reframe sharpens it. (`ProspectiveDesign/v2/conducted-daydreaming-architecture-v2.md`; `daydream-to-stage-contract.md`; `05-stage-integration.md`)

Now the level-specific split:

**L1: world-building / authoring**

Use **`ArtifactDeficiency`** or similar, not generic `Concern`. Only a few pressure types look computable enough to be worth building now:

* `desire_without_obstacle`
* `disconnected_elements`
* `setup_without_payoff`
* `conflict_without_response`

These can be checked against structured characters, relationships, tensions, and event paths. Good.

These are weak or too schema-hungry right now:

* `tonal_monotony`
* `backstory_vacuum`
* `empty_location`

They are not useless. They are just bad first-class engine pressures unless you have much richer state and evaluation. Otherwise the loop becomes “LLM scores its own ideas.” (`02-state-model.md`; `06-world-building-pressure-questions-for-5pro.md`)

L1 outputs should be **`AuthoringCandidate`** or **`WorldEditCandidate`**, not generic `Proposal`. Human curation is mandatory here.

**L2: character inner life**

This is where the full set belongs:

* `CharacterConcern`
* `Context`
* `assumption_patch`
* `Proposal`
* `CommitRecord`

This is the level where context trees, hypothetical forks, policy vs ontic distinction, and operator-triggered exploration actually cohere. Keep the reframe strongest here. (`01-reframe-summary.md`; `02-state-model.md`; `04-kernel-gap-analysis.md`)

The `CommitRecord` taxonomy of `ontic | policy | salience | none` is good here. It maps well to character rehearsal, rationalization, reversal, and actual event realization.

**L3: traversal / directing**

L3 should split harder.

Do **not** force full L2 abstractions onto it. L3 needs:

* **`TraversalState`**: current node, visit counts, last-seen history, situation activation, recent tension/energy trajectory, exhausted situations, conductor bias
* **`TraversalSignal`** or **`SequenceDebt`**: pacing stall, tension deficit, avoidance, recall opportunity, overused situation, unearned jump
* **`TraversalIntent`**: dwell, shift, recall, intensify/build, release
* **`SelectionCandidate`**
* **`SelectionRecord`**

That is enough for runtime sequencing.

`Context`, `branch_depth`, and `assumption_patch` at L3 are mostly over-modeling. They make sense only for **offline lookahead**, beam search, or auditioning alternatives. They do not belong in the main runtime story. (`07-l3-traversal-questions-for-5pro.md`; `17-game-engine-architecture.md`; `21-graffito-v0-playback-contract.md`)

Also: most L3 “pressures” are not concerns in the L2 sense. They are **critic signals over sequence quality**. Calling them concerns blurs a useful distinction.

One more split matters a lot:

**Operator vs Adapter vs Renderer**

This needs to be explicit.

* **Operator**: a move inside a state space, or a ranking policy over candidates
* **Adapter**: translation layer between representations or levels
* **Renderer**: turns cycle packet into model-ready prompts/params

The current benchmark adapters are **adapters**. They convert kernel-side facts into situation deltas, node retrieval, or stage-facing output. They are not operators. Rebranding them as operators is category-wrong. It hides the seam that is currently doing real work. (`17-game-engine-architecture.md`; `review-request-5pro-arch/reply.md`)

4. Shipping path vs research path

The shipping path is already well specified. Keep it.

**Shipping path**

* hand-authored graph or trace-first slice
* thin traversal scheduler or trace player
* normalized cycle packet
* renderer / stage
* watched run
* then compare richer decision layers against that baseline

That is the posture in `home-base.md`, `14-operating-map.md`, `17-game-engine-architecture.md`, `18-graffito-vertical-slice.md`, `21-graffito-v0-playback-contract.md`, and `13-project-state.md`. It is the right posture. Do not destabilize it. The watched run is still the next serious question. (`13-project-state.md`)

`Report 5` should keep dominating **what gets built first**. `Report 4` should keep dominating **how the thing eventually talks to `scope-drd`**. That comparison note got the sequencing right. (`00-comparison-of-results-4-and-5.md`; `deep-research-report (4).md`; `deep-research-report (5).md`)

The stage seam is also basically right:

* keep `DreamDirective`
* keep the stage as deterministic actuator
* keep engine provenance upstream
* do not leak engine theory into `scope-drd`

That part of the reframe is solid. (`05-stage-integration.md`; `deep-research-report (4).md`)

**Research path**

The research path has three threads, and they should stay separate:

**L2 thread:**
Clean up the kernel/refactor around typed L2 abstractions. This is the only place where the reframe currently has enough grounding to justify real architectural work.

**L1 thread:**
Run narrow authoring experiments with a few computable deficiencies. Treat this as authoring research, not as near-term mainline architecture.

**L3 thread:**
Build a better traversal scheduler over fixed authored graphs. Do not treat this as proof that one multi-level engine exists.

Conductor relationship:

The conductor should not become “another engine.” The clean model is:

* engine owns autonomous next-step proposal
* conductor biases situation weights / pacing / dwell / allowed intents
* conductor can hard-override for performance
* event commit stays explicit

One control source should own a parameter at a time. If engine and performer both push the same control dimension simultaneously, you get mush. (`05-stage-integration.md`; `daydream-to-stage-contract.md`)

5. Recommended vocabulary corrections

Here is the cleanup I’d actually make.

**A. Replace public `Concern` with typed names**

* umbrella/internal: `PrioritySignal` or `PressureBase`
* L1: `ArtifactDeficiency`
* L2: `CharacterConcern`
* L3: `TraversalSignal` or `SequenceDebt`

`TraversalPressure` is the wrong name. Most of those are sequence critics, deficits, or opportunities.

**B. Keep `Proposal` for L1/L2 only**

* L1: `AuthoringCandidate`
* L2: `Proposal`
* L3: `SelectionCandidate`

At L3 you are usually selecting or ranking existing material, not proposing a new hypothetical in the same sense.

**C. Restrict `CommitRecord`**

* L2: keep `CommitRecord`
* L1: use `CurationDecision` or `WorldEditDecision`
* L3: use `SelectionRecord`
* reserve actual `CommitRecord` at L3 for real event realization only

Ordinary next-node choices are not commits in the same sense as ontic/policy/salience updates.

**D. Split runtime `family` from L3 sequencing vocabulary**

* keep `family` only as legacy provenance or L2-origin metadata
* add `traversal_intent` for L3
* optionally add `transition_policy`
* optionally add `recall_target` when recall is active

Do not overload `family` to mean L3 sequencing. That confuses everyone. (`21-graffito-v0-playback-contract.md`; `05-stage-integration.md`)

**E. Keep `operator` internal; expose `traversal_intent` at the seam**
The stage does not need your internal theory. It needs a small runtime vocabulary.

Minimal useful L3-to-stage packet:

* `node_id`
* `primary_situation_id`
* `traversal_intent`
* `tension`
* `energy`
* `dwell_s`
* `transition_policy`
* optional `recall_target`

That is enough for renderer, narration, music, and stage without dragging `Concern`, `Context`, or `CommitRecord` into the actuator boundary.

**F. Keep `Adapter` as `Adapter`**
Do not rename it. It is doing projection/translation work.

Mini glossary:

* **ArtifactDeficiency**: a computable authored-world gap
* **CharacterConcern**: an unresolved character-level motivational pressure
* **TraversalSignal**: a runtime sequence deficit or opportunity used to rank next moves
* **TraversalIntent**: the sequencing posture taken now, such as dwell, shift, recall, intensify, release
* **Operator**: a state-space move or ranking policy
* **Adapter**: a translation layer across levels/contracts
* **Renderer**: cycle packet → model-specific prompts and parameters
* **SelectionRecord**: log of why the scheduler chose this next step
* **CommitRecord**: explicit state promotion, not ordinary traversal choice

6. Minimal falsifiable experiments

Do these in this order.

**1. L3 scheduler test. This is the first one.**

Use the fixed Graffito scenes 3-4 slice from `18-graffito-vertical-slice.md`.

State:

* current node
* `visit_count`
* `last_seen_cycle`
* `primary_situation_id`
* running tension trajectory
* underplayed/overplayed situation flags

Intents:

* `dwell`
* `shift`
* `recall`
* `intensify`
* `release`

Baseline:

* current trace-player or weighted-random traversal over the same authored graph

Success:

* the scheduler yields visibly clearer return, avoidance, release, and recall patterns than the baseline
* viewers can tell there is sequence intelligence
* Mark notices better arc-shaping, not just different clips

Failure:

* output is not distinguishable from weighted random
* most “intents” collapse into trivial next-node heuristics
* adding L3 theory does not improve the watched run

This is the cleanest falsifier for whether L3 deserves to be more than a simple traverser.

**2. L1 narrow authoring critic test.**

Seed:

* 3 characters
* 3 locations
* 3 tensions
* 4 event affordances

Pressures:

* `desire_without_obstacle`
* `disconnected_elements`
* `setup_without_payoff`
* `conflict_without_response`

Operators:

* `complicate`
* `connect`
* `ground`

Baseline:

* one-shot direct prompting from the same seed

Measure:

* acceptance rate after human curation
* edit burden per accepted candidate
* number of accepted candidates that later become usable graph nodes

Success:

* higher usable-candidate yield per minute than direct prompting
* lower curation burden than writing the same material from scratch
* accepted candidates improve world connectivity, conflict paths, or payoff structure

Failure:

* curation burden equals or exceeds direct authorship
* outputs accumulate detail without improving structure
* pressure detection is too fuzzy and the loop self-justifies garbage

This tells you whether L1 is a real authoring architecture or dressed-up prompting.

**3. L2 internal refactor-with-stable-output test.**

Take one existing benchmark path or one small authored slice.

Internal change:

* represent the decision process as `CharacterConcern + operator + Context.assumption_patch + CommitRecord`

External contract:

* export the same current playback packet / stage packet as before

Success:

* cleaner trace provenance
* easier explanation of why a branch happened
* same or better behavior with no stage instability

Failure:

* lots of new abstraction, no better behavior, no better inspectability

This is the right test for whether the reframe buys something immediate at the only level that is already grounded.

7. What to change now, what to defer, and what to avoid

**Change now**

* Rewrite the top-line claim in the docs. Say plainly that the project is a **graph-first performance stack with a real L2 exploration engine and two research extensions**. (`14-operating-map.md`; `review-request-5pro-arch/reply.md`)
* Split `Concern` now. Do not keep one public cross-level `Concern`.
* Keep `Operator`, `Adapter`, and `Renderer` separate now.
* Keep `DreamDirective` and the thin stage seam. That part is right.
* For L3, if you need a new runtime field, add **`traversal_intent`**. Do not overload `family`.
* Keep the Graffito slice and watched run on the critical path.
* Keep the kernel on the research track, with L2 as the real refactor target.

**Defer**

* L1 world-building engine beyond a narrow critic loop
* automated graph generation replacing human-authored graph material
* full cross-level propagation logic
* multi-level runner architecture
* L3 contexts and assumption patches outside offline audition/lookahead
* per-character stage streams
* stage snapshot mapping for most contexts
* broader conductor arbitration policy beyond simple ownership and hard override

**Avoid**

* putting `Concern` into the live/mainline packet now
* changing the graph authoring story now
* calling adapters “operator plugins”
* treating L3 as the same kind of context-forking cognition as L2
* changing the Graffito slice plan to serve speculative abstractions
* letting `scope-drd` absorb engine theory
* claiming “Mueller rebuilt”
* building an L1 loop where the LLM both invents and judges world-building proposals with no hard structure

Direct answer on the user’s specific pressure points:

* **introducing `Concern` into the live/mainline packet:** no
* **changing the graph authoring story now:** no
* **turning adapters into plugins/operators:** no
* **using the engine for authoring-time world exploration:** later, narrowly, offline, curated
* **altering the Graffito slice plan:** no

If I had to compress the whole judgment into one sentence:

**Keep the graph-first mainline, treat L2 as the real pressure-engine core, downgrade L1 and L3 from “same engine” to research layers with different semantics, and do not let speculative abstraction leak into the stage seam before the watched run.**

Look away for ten seconds and drop your shoulders.
