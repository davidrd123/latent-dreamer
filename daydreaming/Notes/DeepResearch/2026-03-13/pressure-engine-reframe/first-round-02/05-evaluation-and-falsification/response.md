# Response

## Recommended experiment order

1. **Graffito `L3` pilot**
2. **City Routes full `L3` experiment**, only if the Graffito pilot wins
3. **`L1` critic microtest**
4. **`L2` refactor microtest**

That is the order the notes converge on. `13-execution-roadmap.md` explicitly puts Graffito `L3` first, then City Routes `L3`, then `L1`, then `L2`. The reason is simple: the next real falsifier is the watched run, not more theory work. `L2` is important, but the notes repeatedly say it is **not** the next falsifier. (`11-settled-architecture.md`, `12-prior-work-synthesis-against-settled-architecture.md`, `13-execution-roadmap.md`)

I’m treating **Graffito** as the **minimum `L3` experiment** and **City Routes** as the **fuller `L3` falsifier**. The notes separate those on purpose.

## L3 minimum experiment

Use the **Graffito Phase 1 pilot**, not City Routes. The Graffito checklist defines it as the smallest watched-run test that can kill the `L3` idea early. (`reading-list/11-graffito-phase-1-pilot-checklist.md`)

Minimum shape:

* Freeze the existing Graffito slice. Do **not** expand it to satisfy research curiosity.
* Add only the minimum graph structure:

  * explicit `edges`
  * `delta_tension`
  * `delta_energy`
  * optional `priority_tier`
* Give both arms the same shared traversal state:

  * current node
  * recent path
  * visit count
  * last seen cycle
  * situation activation
  * recent tension/energy trajectory
  * optional conductor bias
* Compare **2 arms only**:

  1. simple baseline walk
  2. Façade-shaped pilot scheduler
* Keep renderer and narration seams unchanged.
* Emit comparable trace YAML plus debug JSONL.

Do **not** add yet:

* DODM feature registry
* structural tension
* event homing
* full three-arm comparison
* any `L2` semantics inside `L3`

The question is narrow: **does a small stateful scheduler over the existing Graffito slice produce a more legible watched run than a naive walk or replayed trace?** (`reading-list/11-graffito-phase-1-pilot-checklist.md`, `reading-list/08-l3-experiment-1-synthesis.md`)

If that passes, then run the **City Routes** three-arm experiment on the larger substrate. That is where you test whether `L3` is more than dressed-up edge weighting. (`reading-list/12-city-routes-experiment-1-checklist.md`, `13-execution-roadmap.md`)

## L2 minimum experiment

Use **one existing benchmark only**. The settled architecture already gives the right minimal frame: represent the same decision process more explicitly, export the **same playback packet as before**, and judge whether traces get clearer without destabilizing behavior. (`11-settled-architecture.md`)

Minimum shape:

* Take one current benchmark.
* Re-express the decision process in explicit refactor state, at minimum:

  * concern state
  * branch/context state
  * memory/reminding state
  * trace/provenance state
  * commit record
* Add the **fast appraisal pass** after each imagined development. The prior-work note is explicit that appraisal is the first high-value `L2` upgrade and should come before deeper branch machinery. (`12-prior-work-synthesis-against-settled-architecture.md`, `reading-list/13-l2-refactor-synthesis.md`)
* Keep the stage-facing/export surface fixed.
* Run a before/after comparison on the **same benchmark**.
* Add one tiny perturbation test at a branch point:

  * change appraisal state, or
  * change a lightweight practice tag,
  * then check whether operator preference shifts in an intelligible way.

Do **not** build in this test:

* ATMS
* full social simulation
* conductor logic
* broader graph traversal changes
* a five-module grand refactor all at once

The minimum question is: **does explicit concern/appraisal/provenance make one benchmark easier to explain, with same or better outward behavior?** (`11-settled-architecture.md`, `reading-list/13-l2-refactor-synthesis.md`)

## L1 minimum experiment

Keep `L1` much smaller than the full seeded-fixture experiment at first. The synthesis note already defines the smallest useful test: **one authored graph region, one detected deficiency, a small set of local repair proposals, visible scoring, and one mutation fallback**. (`reading-list/14-l1-critic-test-synthesis.md`)

Minimum shape:

* Use **one authored graph region**
* Seed or detect **one computable deficiency**
* Restrict proposals to **small graph edits** only:

  * add node
  * add edge
  * split node
  * retag node
  * insert bridge node
  * add payoff hook
  * add alternate continuation
  * defer/reroute resolution
* Generate **2–5 candidate repairs**
* Score them visibly on the boring criteria:

  * deficiency coverage
  * local coherence
  * pressure relevance
  * cost/invasiveness
  * novelty as subordinate term
  * future utility
* Reject weak candidates immediately
* If nothing passes, mutate **one near-miss** and rescore
* Output:

  * one chosen recommendation
  * one mutated fallback
  * full proposal record

Inputs should stay narrow:

* local graph neighborhood
* active pressure tags projected from `L2`, if available
* authored constraints
* recent `L3` trouble signals, if available

Do **not** turn this into world generation. The notes are explicit that `L1` is a narrow critic/repair layer, not a free-running author. (`11-settled-architecture.md`, `reading-list/14-l1-critic-test-synthesis.md`)

## Success criteria

**`L3` minimum success**

* The Graffito pilot arm feels more intentional than the simple walk.
* Humans can describe **why** the run moved when it did.
* Returns to street/apartment feel timed, not accidental.
* The result is **not** explainable by a trivial two-rule heuristic.
* Renderer/narration seams stay intact.
  (`reading-list/11-graffito-phase-1-pilot-checklist.md`, `13-execution-roadmap.md`)

**`L3` full success after that**

* On City Routes, arm **B beats A**.
* Arm **C clearly beats B** on at least some combination of event approach, recall timing, structural tension, and leverage.
* Graph changes are absorbed mostly locally.
* Different conductor bias produces meaningful, legible differences.
  (`reading-list/12-city-routes-experiment-1-checklist.md`, `reading-list/08-l3-experiment-1-synthesis.md`)

**`L2` success**

* Same or better behavior on the same benchmark.
* No stage instability.
* Traces can now answer:

  * why this branch happened
  * what memory/pressure got activated
  * what changed after exploration
* A small appraisal/practice perturbation changes operator choice in a way a human can understand.
  (`11-settled-architecture.md`, `reading-list/13-l2-refactor-synthesis.md`)

**`L1` success**

* The target deficiency is detected stably.
* At least one local repair is clearly better than doing nothing.
* The visible critic helps choose a better repair than ad hoc suggestion.
* Mutation of a near-miss sometimes rescues a candidate more cheaply than restarting.
  (`reading-list/14-l1-critic-test-synthesis.md`)

If you scale `L1` up after that, then use the settled architecture’s stronger thresholds:

* 50% reduction in seeded defects
* more accepted proposals per review minute than baseline
* at least 3 accepted proposals needing only light edits
  (`11-settled-architecture.md`)

## Negative-result criteria

**`L3` real negative**

* Humans do **not** prefer the pilot arm in watched runs.
* People cannot tell why it moved.
* Priority tiers and target trajectory do no visible work.
* Behavior is reproducible by slightly better edge weighting.
* On City Routes, the full scheduler does **not** clearly beat the Façade baseline.

If that happens, stop expanding `L3`. The notes are explicit about this. Keep the shipping path narrow. (`reading-list/11-graffito-phase-1-pilot-checklist.md`, `reading-list/12-city-routes-experiment-1-checklist.md`, `11-settled-architecture.md`)

**`L2` real negative**

* You add explicit state and get no better explanation.
* The traces are still opaque.
* Operator choice does not become more intelligible under appraisal/practice changes.
* Outward behavior gets worse, or stage stability drops.
* The refactor mostly renames internals without improving inspectability.

That is a fail, not “partial progress.” (`11-settled-architecture.md`, `reading-list/13-l2-refactor-synthesis.md`)

**`L1` real negative**

* Proposals do not actually address the target deficiency.
* The system mostly produces generic filler.
* Novelty rises by weakening coherence.
* Edit burden is high enough that patching by hand is faster.
* One-shot structured prompting or ad hoc human patching works as well or better.
* Mutation/repair does not beat regenerate-from-scratch behavior.
  (`11-settled-architecture.md`, `reading-list/14-l1-critic-test-synthesis.md`)

## Baselines

**`L3` baselines**

* **Minimum Graffito baseline:** available-node walk with recency suppression and local continuity bias
* **Secondary manual baseline:** current trace-player / replayed authored trace / current graph-weighted traversal
* **Full City Routes baselines:**
  A. weighted-random baseline
  B. Façade baseline
  C. full scheduler
  (`reading-list/11-graffito-phase-1-pilot-checklist.md`, `reading-list/08-l3-experiment-1-synthesis.md`, `reading-list/12-city-routes-experiment-1-checklist.md`)

**`L2` baseline**

* Current kernel on the same benchmark
* Same playback/export packet
* Same benchmark fixture
* Same renderer/stage path

Anything else makes the comparison muddy. (`11-settled-architecture.md`)

**`L1` baselines**

* Ad hoc human patching on the same deficiency
* One-shot structured prompting with the **same model budget** and **same reviewer**
* Unconstrained generative suggestion as an anti-baseline

The notes explicitly say `L1` should be compared against ad hoc patching or unconstrained suggestion, and the settled architecture names one-shot structured prompting as the direct comparator. (`reading-list/14-l1-critic-test-synthesis.md`, `11-settled-architecture.md`)

## Bottom line

The notes already made the hard call: **`L3` lives or dies first on watched-run evidence**.

So the plan should be ruthless:

* Use **Graffito** as the smallest kill test.
* If it wins, run **City Routes** to see whether `L3` is actually buying leverage.
* Then test **`L1`** as a narrow local critic, not a world generator.
* Test **`L2`** last, and judge it by explanation/provenance gains under the same outward behavior, not by how much new ontology it introduces.

If Graffito fails, do not hide behind bigger graphs, richer features, or more `L2` theory. The notes are telling you to stop.
