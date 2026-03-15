**Findings**

The first bridge experiment should not ask whether the current generator can already author a whole traversal graph. That is the wrong falsifier. The generation lane is proven at the candidate-moment level. The traversal lane is proven on authored graphs. The real gap is narrower: can a small set of generated, human-admitted nodes live inside a graph slice that L3 can traverse without flattening, looping stupidly, or losing the pressure/event contour?

So the smallest honest experiment is a **patch test**, not a whole-world bakeoff. Use one generation-native benchmark world, not City Routes or Graffito. Keep the graph seam fixed. Keep the scheduler fixed. Swap one tiny hand-authored region for a generated-curated region and see whether traversal still works. If you try to jump straight to “fully generated graph versus City Routes,” failure tells you nothing.

Use **Kai** for the first pass. It is the canonical benchmark, it already has two explicit situations and one pending event, and it is the easiest place to build a tiny graph without inventing new ontology. The representative multi-step trace also shows why this should be a patch test: the current sequence machinery can carry forward accepted episodes, but it is not yet reliable enough to treat as whole-graph authoring. Asking it for a full traversal substrate right now is premature.

Keep conductor out of the first bridge. Keep post-event regeneration out too. Keep rendering out. The first question is only: **can generated node material survive admission and support meaningful traversal?**

## 1. Smallest end-to-end substrate

Build a **Kai micrograph** with **10 to 12 total nodes** across the two existing situations:

* `sit_unopened_letter`
* `sit_threshold_departure`

Include the existing pending event as a graph reference target, but do **not** require the event to fire in this first bridge. Event approach is enough.

Make the graph out of two parts:

**Fixed scaffold, hand-authored:** 6 to 8 nodes.
These provide the start node, one or two return nodes, the threshold spine, and the event-approach region. They also carry the edges and the normal L3 traversal annotations.

**Replaceable patch:** 4 nodes.
These sit in the precontact kitchen region. In the control graph they are hand-authored. In the test graph they are generated then curated from the current authoring-time prototype.

That gives you two graph versions:

* **H:** full hand-authored micrograph
* **G:** same micrograph, same scaffold, same edges outside the patch, but the 4 patch nodes are generated-curated

Everything except the patch stays fixed.

## 2. How many generated nodes are enough

**Four admitted generated nodes** is enough for the first falsifier.

Less than four is too brittle. If one node is bad, the result collapses. More than six is wasted effort at this stage.

The right batch size is:

* generate **about 16 candidates**
* admit **exactly 4**

That is large enough to expose duplicate collapse and curation burden, but still small enough to review by hand.

If you cannot get **4 distinct keepers out of ~16 candidates** without major rewriting, the bridge has already failed. You do not need traversal to tell you that.

## 3. Curation and admission rules

Use the current seam and current prototype outputs. Do not invent a new admission layer.

**Hard gates:**

A candidate is rejected immediately if it fails any of these:

* graph projection is invalid
* refs do not resolve
* cross-fixture contamination appears
* canon contradiction appears
* it is a near-duplicate of an already admitted node
* it requires more than light copyediting to be usable

That last one matters. If the curator has to rewrite the beat, you are measuring human authoring, not generated supply.

**Patch-level keep rules:**

The admitted set of 4 should satisfy all of these:

* at least **1 forward-driving node** whose payoff points toward `sit_threshold_departure` or the pending event approach
* at least **1 local-loop node** that keeps material in the precontact region
* at least **1 intensifier** with clearly positive tension pressure
* at least **1 decompressor or hesitation node** with a distinct energy/tension signature

Those are not new ontology. They can be read off the existing fields: `delta_tension`, `delta_energy`, `payoff_refs`, `option_effect`, `pressure_tags`.

At admission time, the human is allowed to add only the L3-facing fields the traversal harness already expects, such as:

* `availability_test`
* `priority_tier`
* `line_id`
* `subplot_id`
* `importance`
* `resolution_path_count`
* `event_commit_potential`
* edges

That is not a seam change. That is normal graph authoring on top of the generated node projection.

## 4. Traversal comparison

Run the **current feature arm** as the primary traversal test. That is the L3 layer with the strongest evidence behind it.

Use:

* same start node
* same neutral conductor
* same cycle budget
* same seeds

A good minimum is:

* **12 cycles**
* **3 seeds**: use the same seed family you have already used elsewhere so the comparison stays legible

That yields **6 primary runs**:

* H graph × 3 seeds
* G graph × 3 seeds

If the result is ambiguous, add the simpler scheduler as a secondary check. Do **not** add conductor presets to the first bridge. Conductor is not the question here.

The right comparison is not “which graph is prettier?” It is:

* does the generated patch get selected?
* does traversal still move from precontact material into threshold material?
* does the graph still support returns and nontrivial revisit gaps?
* does it avoid pathological early looping?

## 5. Success and failure conditions

**Success** looks like this:

On the generated-curated graph G, under the feature arm:

* both situations are visited in **at least 2 of 3 seeds**
* the traversal reaches the threshold or event-approach region in **at least 2 of 3 seeds**
* the run does **not** collapse into a tight 2-node loop before cycle 8
* generated patch nodes are actually selected, not just present as dead leaves
* a blind reviewer reading the text playlist can still recover a coherent precontact-to-threshold arc

It does **not** need to match the hand-authored control exactly. It only needs to be close enough that the generated region behaves like usable graph material rather than decorative residue.

**Failure** looks like one of three things:

1. **Generation failure**
   You do not get 4 distinct keepers out of ~16 candidates without heavy rewrite.

2. **Admission failure**
   Individual candidates are decent, but the patch cannot be assembled into a structurally usable region without hand-authoring away the problem.

3. **Traversal failure**
   The patch looks fine on paper, but once inserted, L3 either avoids it, over-repeats it, or cannot leave it cleanly.

That three-way split is the point of the experiment.

## 6. What makes the result informative rather than just a demo

Three things.

First, the comparison is **role-matched**, not vibe-matched. The hand-authored control patch should occupy the same structural slot as the generated patch: same situation, same rough forward/backward role, same graph size.

Second, only **one region changes**. Same world, same scaffold, same scheduler, same seeds. If the outcome changes, you know why.

Third, the experiment stops before extra confounds appear. No conductor expressivity question. No post-event graph repair question. No video quality question. No “whole graph generation” fantasy. Just generate, admit, traverse.

That is what makes the answer falsifiable.

## 7. What to instrument

Instrument all three stages separately.

**Generation stage**

For every candidate, keep:

* candidate text
* graph projection
* provenance sidecar
* semantic checks
* operator family
* retrieval set
* candidate compilation score

This tells you whether the generator is actually supplying distinct material.

**Admission stage**

For every accept/reject decision, keep:

* reject reason
* copyedit burden
* duplicate grouping
* structural role in the patch
* any L3-specific annotations the curator added

This tells you whether failure is supply or assembly.

**Traversal stage**

Use the existing traversal artifacts:

* trace rows
* debug rows
* selected node sequence
* `traversal_intent`
* feature breakdown
* situation activation
* event-approach counts

Add a tiny patch-specific summary:

* patch visitation count
* first pathological loop cycle
* threshold reach yes/no
* revisit gap to patch nodes

With those logs, failure attribution is clean:

* bad candidates = generation
* good candidates but unusable patch = admission
* usable patch but bad run = traversal integration

## What not to change yet

Do not widen to City Routes. Do not ask the generator to author a whole graph. Do not build a solver for admission. Do not vary conductor. Do not require video. Do not reopen the seam.

The smallest honest bridge is:

**Kai micrograph → generate 16 candidates → admit 4 nodes into one replaceable patch → traverse H vs G under the same L3 feature arm.**

If that passes, then you have earned the next experiment. If it fails, the failure will be local and useful.
