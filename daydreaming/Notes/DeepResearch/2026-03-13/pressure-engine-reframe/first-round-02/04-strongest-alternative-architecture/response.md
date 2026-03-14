## Alternative architecture

The strongest competitor$_{75%}$ is a **graph-centric two-stage stack**:

```text
Source Bible / Canon / Constraints
        ↓
Unified Graph Constructor
        ↓
Executable Dream Graph
        ↓
Thin Traversal Policy
        ↓
Playback Packet → Renderer / Music / Stage
```

This is not “same engine everywhere.” It is almost the opposite.

The claim is that the current architecture stopped one step too early. The notes already demote **L1** to local graph repair and **L3** to graph traversal policy, while leaving **L2** as the only real exploratory engine. The clean alternative is to stop calling L1 and L3 separate architectural levels at all. The broader source map already contains this simpler stack explicitly: **Story Source → Daydream Engine → Graph → Traversal → Render**, with a **propose → validate → admit** loop upstream and **graph navigation during performance** downstream.   

### 1. Unified Graph Constructor

This is the only upstream engine.

Its core is still your current L2 thesis:

* concern-centered control
* fast appraisal
* reminding / serendipity
* practice-gated operator choice
* explicit commit discipline

But the constructor also absorbs what the current notes call L1:

* deficiency detection
* local graph repair
* mutation of near-miss proposals
* admission / rejection of candidate nodes and edges

So `14-l1-critic-test-synthesis.md` stops being a separate lane and becomes a **graph-admission policy** inside the constructor. Its “small graph edits” are not a sibling architecture. They are one way the constructor repairs or extends the graph it is building.

### 2. Graph as central executable artifact

In the current canonical architecture, the graph is both:

* the human-authored product
* the membrane between lanes

That is one abstraction too many.

In the alternative, the graph is just the central artifact:

* authored by humans
* expanded or repaired by the constructor
* traversed at runtime
* projected into the playback packet

That aligns more cleanly with `17-game-engine-architecture.md` and `21-graffito-v0-playback-contract.md`, where the graph already behaves like the product and the downstream seam is already thin.

### 3. Thin Traversal Policy, not a third “level”

What survives from current L3 is real, but smaller:

* recency / revisit state
* situation activation
* event approach
* weighted next-node choice
* conductor bias
* maybe shallow lookahead

What disappears is the idea that this needs to be a full separate architectural level with its own research identity.

`08-l3-experiment-1-synthesis.md` already narrows L3 to a Façade-shaped pipeline plus a DODM-style feature registry over candidate nodes. That is runtime policy over graph material. It is not a second engine. In the alternative architecture, that becomes the graph’s runtime policy layer, not “Level 3.”

### 4. Cross-level propagation becomes a non-problem

The current architecture treats “cross-level pressure propagation” as the hardest open composition problem.

In the alternative, that problem mostly goes away.

The constructor writes graph-readable residues directly:

* origin concern refs
* pressure tags
* setup / payoff refs
* event potential
* release potential
* practice tags
* recall hooks

Traversal reads those tags. No live cross-level bus. No membrane mysticism. Just annotated graph state.

---

## Comparison against current architecture

| Criterion                            | Current L1/L2/L3                                                                                                                                  | Alternative                                                                                                                                       | Judgment                  |
| ------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------- |
| **Clarity of boundaries**            | Clean research taxonomy, but composition is awkward: L1 is “narrow critic,” L3 is “narrow scheduler,” and the graph is both membrane and product. | Cleaner product story: one upstream constructor, one downstream traversal policy, one graph.                                                      | **Alternative wins**      |
| **Implementation tractability**      | Better staged build discipline. `11-settled-architecture.md` gives clean experiments: L3 first, L1 second, L2 refactor third.                     | Simpler end-state, but heavier integration earlier. You must make graph construction, validation, and annotation strong sooner.                   | **Current wins for now**  |
| **Authoring burden**                 | Lower near-term burden because L1 is optional and Graffito can stay hand-authored.                                                                | Higher burden earlier because the graph must carry richer annotations and the world bible must support constructor logic sooner.                  | **Current wins**          |
| **Explainability**                   | Better failure localization: “this was an L1 defect detector failure,” “this was an L3 traversal failure,” etc.                                   | Better high-level mental model for the product, but weaker debugging partitions.                                                                  | **Current wins slightly** |
| **Fit to cited source lineages**     | Best fit. It keeps mixed-initiative PCG, Mueller-style cognition, and drama management distinct, which is what the reading-list syntheses argue.  | It compresses Sentient Sketchbook/Tanagra and Façade/DODM into one graph-policy family. That is plausible, but it blurs real lineage differences. | **Current wins**          |
| **Usefulness for early experiments** | Strongest. It protects the watched-run falsifier and keeps Graffito / City Routes experiments small and legible.                                  | Good eventual architecture, worse early experimental discipline. Risk: building unification before proof.                                         | **Current wins clearly**  |

### Net read

The alternative is **product-cleaner**.
The current architecture is **experiment-cleaner**.

That is the real trade.

---

## Discriminating evidence or experiment

Run one experiment that asks the real question:

**Are L1 and L3 actually two domains, or just two timescales of one graph-policy system?**

### Best substrate

Use **City Routes**, not Graffito v0.

`08-l3-experiment-1-synthesis.md` already says Graffito v0 is fine for phase-1 traversal sanity checks but too thin for event homing, cross-situation mixing, and larger structural-tension questions. City Routes is the better discriminator.

### Two arms

**Arm A: current architecture**

1. Run the current **L1 critic** workflow on a graph region with seeded deficiencies.
2. Accept a few local repairs.
3. Run the current **L3 scheduler** with its own feature registry over the repaired graph.
4. Watch the run.

**Arm B: alternative architecture**

1. Run a **unified graph constructor**:

   * L2 concern-driven generation
   * integrated deficiency critics
   * local repair / mutation / admission inside one loop
2. Output one annotated executable graph.
3. Run only a **thin traversal policy** over those annotations.
4. Watch the run.

### What to measure

Do not measure elegance. Measure these:

* **feature-registry reuse**
  Can one annotation vocabulary serve both offline repair and runtime traversal, or do you end up inventing two different score languages anyway?

* **accepted graph improvement per review minute**
  Does the unified constructor reduce human patching work, or just hide it?

* **watched-run quality**
  Same playback contract, same renderer path, same conductor surface. Does the alternative match or beat the current architecture on legible return, escalation, recall, and event approach?

* **failure localization**
  When the run is bad, can you tell why quickly?

### Decision rule

* If **Arm B** works with one shared annotation / critic vocabulary, produces equal or better watched runs, and removes bespoke L1/L3 machinery, then the alternative wins.
* If **Arm B** quickly grows separate offline and runtime feature vocabularies, separate acceptance criteria, and separate human workflows, then the current decomposition is real and should stay.

That is the discriminator. Not theory. Not naming.

---

## Bottom line

The strongest competing architecture is:

**one real upstream engine that constructs and repairs an executable graph, plus one downstream traversal policy that navigates it.**

In other words:

* keep **L2**
* demote **L1** into constructor-side graph criticism
* demote **L3** into runtime graph policy
* stop pretending those demotions still deserve separate “levels”

I think this is the strongest competitor$_{75%}$ because it resolves the graph/membrane confusion, kills the cross-level propagation pseudo-problem, and matches the older graph-first stack already present in the source map. It is also close to the compact pipeline in the revised conducted-daydreaming architecture.  

I still would **not** switch to it now$_{80%}$. The current architecture is the better experimental discipline. It keeps the lineages honest, protects the watched-run falsifier, and avoids building a unified superstructure before you know whether the watched thing works at all.

So the sharp version is:

**Best competitor as product architecture: the two-stage graph-centric stack.**
**Best architecture right now for research and falsification: the current one.**
