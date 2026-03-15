Yes. A handful look genuinely additive rather than redundant.

If I had to pick the strongest five right now, they’d be:

1. **partial-order / causal-link planning**$_{80%}$
2. **abductive inference**$_{75%}$
3. **JTMS / dependency-directed backtracking**$_{75%}$
4. **structure-mapping analogy**$_{70%}$
5. **event segmentation + situation-model theory**$_{70%}$

Then there are a few second-tier imports that are still good:

6. **script / MOP / TOP memory**$_{65%}$
7. **Gross-style emotion regulation + implementation intentions**$_{65%}$
8. **TOTE / control-theoretic self-regulation**$_{60%}$
9. **qualitative reasoning / qualitative process theory**$_{55%}$
10. **rhetorical structure / discourse planning for the dashboard**$_{50%}$

Here’s why I think those matter, and where they’d fit.

## 1. Partial-order / causal-link planning

This is probably the cleanest missing classic-AI import.

In plain language, instead of planning as one serial chain of steps, you represent a plan as:

* actions
* causal links
* open preconditions
* threats to those links
* only as much ordering as you actually need

So you get "A must happen before B because B needs what A establishes," but unrelated things stay unordered.

Why this fits your project:

* Your **graph seam** already wants setup, payoff, preparation, event approach, and "earnedness."
* DODM’s `motivated_by` and your `setup_refs[]` / `payoff_refs[]` are basically begging for explicit causal-link structure.
* For **authoring-time generation**, this is better than pure serial Mueller-style branch growth when you need to produce graph-compilable material.

What it would buy:

* cleaner distinction between **prepared** and **unprepared** events
* explicit "what is still open?" state for L1 and L3
* better event-homing in L3
* more legible provenance on why a node belongs where it does

Where it belongs:

* mostly **L1 authoring-time generation**
* possibly the **graph compiler**
* maybe shallow L3 lookahead

Where it does **not** belong:

* not as the main runtime L2 engine
* not as a replacement for concerns and operators

So: **use it to shape material**, not to replace Mueller.

## 2. Abductive inference

This is the other big one.

In plain language, abduction is inference to the best explanation. You observe sparse clues and infer the hidden causes or latent structure that would make them make sense.

That matters because a lot of your stack currently assumes structure is already there. But often it won’t be. You’ll have:

* a charged place
* a fragment of backstory
* one awkward gesture
* one unresolved letter
* one contradiction between two characters

and you need to infer:

* what concern this probably activates
* what blocker is implied
* what offscreen event likely happened
* what practice is now active
* what hidden causal slice should feed appraisal

This is exactly where abduction helps.

Why it fits:

* **CausalSlice** in your frozen L2 schema is a perfect consumer of abductive structure.
* **Authoring-time generation** from narrative primitives needs something that can turn sparse material into plausible latent conflict.
* It also gives L1 a better path from "world sketch" to "candidate moments" than flat prompting.

What it would buy:

* inferred blockers
* inferred offscreen causes
* better concern birth from sparse stimuli
* better proposal generation from incomplete material
* stronger graph annotations derived from weak source inputs

Where it belongs:

* **L1 generation**
* **L2 causal-slice construction**
* maybe graph enrichment / sidecar inference

What not to do:

* do not build a giant logicist abductive theorem prover first
* keep it narrow and weighted
* think "best few explanations," not "exhaustive reasoning"

A lightweight weighted-abduction layer could be extremely good here.

## 3. JTMS / dependency-directed backtracking

You already have ATMS in view. I think there is a smaller, nearer thing worth stealing first.

ATMS is the grand version. **JTMS plus dependency-directed backtracking** is the more immediate practical move.

In plain language: when a branch fails, don’t just back up one step and try siblings. Record **why** it failed, then jump back to the assumption or choice that actually caused the contradiction.

This is old AI, but it is still useful.

Why it fits:

* Mueller backtracking is real, but crude.
* Your L2 branching will get more expensive as appraisal and practices make branches richer.
* L1 proposal mutation also wants failure reasons, not just "rejected."

What it would buy:

* smarter branch pruning
* explicit failure causes
* less blind retry
* a good bridge toward ATMS later
* better provenance for the inner-life dashboard

Where it belongs:

* **L2 branch management**
* **L1 proposal repair**
* maybe graph-authoring search if you start doing constrained completion

What to keep from it:

* conflict sets
* culprit assumptions
* dependency-directed jump-back

What to leave:

* full ATMS label algebra until you actually need it

So I’d say: **don’t jump straight to full ATMS**. Add failure-directed backtracking first.

## 4. Structure-mapping analogy

Mueller already gives you serendipity and analogical planning. But there’s still a gap.

Mueller’s memory story is great at reminding and opportunistic reuse. It is weaker at **explicit relational analogy**.

Structure-mapping, in the Gentner sense, is the idea that analogy should privilege **relational correspondence**, not surface feature overlap.

That matters because a lot of your future good material will look like:

* "this political dinner conflict has the same relational shape as a courtroom cross-examination"
* "this unresolved family duty has the same geometry as a military chain-of-command failure"
* "this flirtation + avoidance + witness triangle has the same role structure as a predator-prey-feint pattern"

That is not nearest-neighbor retrieval. It is relational transfer.

Why it fits:

* upgrades **Mueller-style serendipity**
* helps **authoring-time generation** from primitives
* gives L1 a way to make non-obvious but still structured proposals
* could enrich graph tags with deeper correspondences

What it would buy:

* better cross-domain retrieval
* more interesting but still coherent scene generation
* stronger "overdetermined" material, because one structure can satisfy multiple pressures

Where it belongs:

* mostly **L1**
* maybe as an auxiliary scorer in **L2 serendipity / reminding**
* maybe in graph-side recall and contrast logic

What not to do:

* do not make the runtime depend on a full heavy analogy engine
* use it as a **retrieval and proposal bias**, not as the whole cognition layer

This one feels especially aligned with your project’s ambitions.

## 5. Event segmentation and situation-model theory

This is from psych / cog sci more than old AI, but it is extremely relevant.

In plain language, humans naturally chunk ongoing activity into events when the active model stops predicting well. Also, people track situations along a few dimensions like:

* time
* space
* causation
* protagonist / social focus
* intention or goal state

That matters because your system needs to decide:

* when a generated stream becomes a **node**
* what makes a boundary feel real
* what metadata should travel with a node
* when the dashboard should visibly segment thought

You already have some of this implicitly in concern switches, backtracking, and graph nodes. This literature would make it cleaner.

What it would buy:

* principled node boundaries
* better `delta_tension` and `delta_energy` semantics
* better recall indices
* better narration segmentation
* better graph compilation from L1/L2 material

Where it belongs:

* **graph seam**
* **dashboard segmentation**
* **authoring-time generation compiler**
* maybe retrieval indexing

What not to do:

* do not drown in discourse-theory formalism
* keep the payoff practical: event boundaries, situation fields, segmentation rules

I think this is one of the most underrated additions for you.

## 6. Script / MOP / TOP memory

Versu gives you **social practices**, which is good. But practices are more about local social affordances. They do not fully solve **diachronic expectation structure**.

Schank-style scripts, MOPs, TOPs, and related case-based memory structures are worth a look again, even if old-school story grammars as a whole are not.

Why this still matters:

* L2 needs local situation structure
* L3 needs traversal over authored nodes
* but the system also needs reusable memory for **event-sequence patterns**

Examples:

* arrival → awkward greeting → evasion → disclosure → rupture
* invitation → delay → substitute offer → status test → acceptance/refusal
* obligation reminder → attentional dodge → guilt spike → rationalization → later repair

That is not exactly a practice, and not exactly a graph node. It is a **typed temporal pattern**.

What it would buy:

* better compilation of generated material into reusable graph motifs
* better setup/payoff memory
* better reminding from partial temporal matches
* better event-homing and arc shaping

Where it belongs:

* between **L1 generation** and the **graph seam**
* maybe also in episodic-memory indexing

What not to do:

* do not import rigid scriptism as the whole story model
* use scripts as **reusable motif memory**, not as a prison

## 7. Gross emotion-regulation process model and implementation intentions

EMA gives you appraisal. Mueller gives you typed daydream operators. But there’s another psych import that could sharpen the action side.

The **Gross process model of emotion regulation** distinguishes things like:

* situation selection
* situation modification
* attentional deployment
* cognitive change
* response modulation

That is useful because your current operators already roughly span this space, but not explicitly.

Rough mapping:

* rehearsal / avoidance → situation selection or preparation
* reversal → situation modification / counterfactual repair
* roving → attentional deployment
* rationalization → cognitive change
* some reactive discharge layer, if you ever add one → response modulation

Then pair that with **implementation intentions**:

* "If X happens, do Y"

That’s excellent for policy commits.

Why it fits:

* your `ontic | policy | salience | none` commit system wants a cleaner theory of what a policy commit is
* authoring-time generation wants durable "next time" rules
* it helps bridge rehearsal into later real behavior

What it would buy:

* more principled operator family coverage
* better policy-object generation
* clearer distinction between temporary coping and durable learning
* sharper behavior handoff from imagined material to later action

Where it belongs:

* **L2 commits**
* **policy residue at the graph seam**
* maybe some L1-generated "habit / plan" proposals

What not to do:

* do not replace Mueller families with therapy-taxonomy jargon
* just use it to sharpen the mapping from inner process to durable policy

## 8. TOTE and control-theoretic self-regulation

This is older than Russell and Norvig, but it fits your project well.

TOTE is Test-Operate-Test-Exit. More generally, comparator-style control theory says an agent is trying to reduce discrepancies between current state and target state.

Why it matters:

* concerns are basically **persistent discrepancies**
* urgency is partly derivative of expected error growth
* release is not just low valence, it is **error reduction**
* conductor shaping also looks like target tracking over tension, density, clarity, novelty

This could clean up several parts of the stack:

* why concerns persist
* when they decay
* when a commit counts as real relief
* how arc-shaping works at L3
* how conductor bias interacts with target trajectories

What it would buy:

* more coherent concern dynamics
* cleaner urgency math
* a better story for release and closure
* possible shared vocabulary across L2 and L3 without collapsing ontology

Where it belongs:

* mainly **concern dynamics**
* maybe **traversal diagnostics**
* maybe conductor-state interpretation

What not to do:

* do not rewrite the whole project as cybernetics fanfic
* use it as a quantitative discipline, not a metaphysics

## 9. Qualitative reasoning and qualitative process theory

This is another classic AI area that could matter more than people think.

In plain language, qualitative reasoning models causal tendencies and regime changes without requiring precise numerical simulation. It asks things like:

* if this pressure increases and this buffer decreases, what kind of transition becomes likely?
* if this status structure is destabilized, what consequences tend to follow?
* what causal processes become active when a threshold is crossed?

Why it fits:

* your world has **emotional physics**, not just dialogue
* `repercussions` wants exactly this sort of consequence propagation
* authoring-time generation from primitives needs causal scaffolding that is richer than vibes but cheaper than full simulation

What it would buy:

* better consequence chains from sparse world rules
* better charged-object and charged-place reasoning
* better "if this then what tends to follow?" machinery
* stronger L1 generation from high-level world constraints

Where it belongs:

* mostly **L1 generation**
* maybe `repercussions`-style L2 exploration
* graph enrichment

What not to do:

* do not build a big physical simulator unless your domain really needs one
* use qualitative process fragments as narrative-causal scaffolds

## 10. Rhetorical structure / discourse planning for the inner-life dashboard

This one is less foundational, but still good.

Mueller already gives you a narration layer. What you still might want is a clearer planner for **how narration is organized**, especially when the dashboard is showing:

* actual events
* hypothetical branches
* recalls
* operator explanations
* concern shifts
* causal justification

Rhetorical-structure-style thinking helps decide whether the dashboard is currently:

* elaborating
* contrasting
* motivating
* explaining
* foreshadowing
* summarizing

Why it fits:

* the dashboard is a product surface, not a dump
* TTS and watchability care about rhetorical organization
* it could let narration stay compact without becoming cryptic

Where it belongs:

* strictly the **dashboard / realization layer**

What not to do:

* do not let discourse planning metastasize into a full architecture
* keep it downstream

# One way to rank these by likely payoff

## Highest-payoff next imports

**Partial-order planning**, **abduction**, and **dependency-directed backtracking** feel most useful$_{75%}$.

They directly attack real bottlenecks:

* graph material supply
* causal-slice construction
* branch failure and retry cost

## Best conceptual upgrade to Mueller

**Structure-mapping analogy** feels strongest$_{70%}$.

It gives you a more powerful notion of serendipity and retrieval than plain local reminding.

## Best psych upgrade for system legibility

**Event segmentation** and **implementation intentions** feel strong$_{65%}$.

They help define nodes, transitions, policy commits, and narration.

## Most elegant but easiest to overbuild

**Control theory** and **qualitative reasoning** feel useful$_{55%}$, but easier to turn into abstraction theater if not kept concrete.

# My blunt take

The project has already covered a lot of the obvious lineage. The missing gains are no longer in finding another grand architecture paper. They’re in adding **specific missing mechanisms**:

* a better way to represent partial causal preparation
* a better way to infer hidden structure from sparse inputs
* a better way to fail intelligently
* a better way to map across domains
* a better way to decide where one event ends and another begins

If you want, I can next turn this into a **ranked shortlist of 5 concrete prototype candidates**, each phrased as:

* what to prototype
* where it plugs into L1/L2/L3
* what success would look like
* what to ignore so it doesn’t sprawl
