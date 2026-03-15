Yes. Deeper take: those three are not three alternative architectures. They are three **different levels of the same stack**.

* **Weighted abduction** answers: *what is probably going on here?*
* **Partial-order causal-link planning** answers: *what would need to be true for this moment to be earned?*
* **Soft-constraint optimization** answers: *which set of moments actually belongs in the graph together?*

That decomposition feels right$_{80%}$. It is much cleaner than trying to make one giant mechanism do interpretation, local construction, and global compilation at once.

## 1. Weighted abduction: the interpretation layer

Classically, abduction is “inference to the best explanation.” In the Hobbs/Stickel line, the machinery gets more specific: observations can be explained by assumable hypotheses with costs, those costs get pushed backward through weighted Horn clauses, and factoring lets shared explanations get cheaper by exploiting redundancy. That is the part worth stealing. ([ACL Anthology][1])

### Why it matters here

Your authoring-time pipeline starts from **narrative primitives**, not from already-clean symbolic states. So the first hard problem is not generation. It is **latent situation construction**.

You have things like:

* unopened letter
* brother avoiding eye contact
* one previous betrayal episode
* rent due tomorrow
* mother in hospital
* apartment kitchen at 2 a.m.

That is not yet a `CausalSlice`. It is not even one problem. It is a cloud of weak cues.

Weighted abduction is the right way to turn that cloud into a few competing latent interpretations, for example:

* **H₁**: obligation pressure + shame + evasion practice
* **H₂**: attachment threat + hidden bad news + avoidance
* **H₃**: financial blocker + family-duty conflict + delayed disclosure

That is exactly the missing bridge from primitives to `CharacterConcern` and `CausalSlice`.

### The key modification I’d make

Classic weighted abduction optimizes for **best explanation**. Your system should optimize for **best explanation that also has downstream generative yield**.

That is a big difference.

The most parsimonious explanation is not always the one you want. Sometimes the slightly less parsimonious explanation is better because it opens:

* stronger operator paths
* better episodic retrieval
* more overdetermined candidate moments
* cleaner graph-compilable residue

So I would not use a pure “minimum assumability cost” objective. I would use something more like:

**score(explanation)**
= explanatory adequacy
− assumption cost
− genericity penalty

* operator yield
* episodic resonance
* graph yield

That is no longer textbook abduction. Good. Textbook abduction is not trying to author graph material.

### What the data structure should look like

Not full FOL. That would be a mistake.

Use a **typed hypothesis graph** with maybe 5 or 6 families of latent assumption:

* latent event
* latent relationship state
* latent belief / misbelief
* latent blocker
* latent practice or frame
* latent trait / pressure disposition

Then give each family different priors and different downstream effects.

For example:

* latent blocker assumptions should strongly affect rehearsal/reversal generation
* latent misbelief assumptions should affect rationalization/revenge/reveal moments
* latent practice assumptions should constrain affordances
* latent relationship-state assumptions should affect overdetermination and payoff potential

### Failure modes

Weighted abduction can go wrong in predictable ways:

1. **Generic explanations dominate.**
   “This is stressful” explains everything and gives you nothing.

2. **Kitchen-sink explanations proliferate.**
   You pay a small cost for every hidden cause, then the system explains everything with ten vague causes.

3. **One canonical explanation crushes ambiguity.**
   That is bad for generation. You want an explanation frontier, not a monoculture.

4. **It hallucinates social microstructure.**
   It infers a lot because the ontology lets it, not because the evidence warrants it.

So I’d insist on three guardrails:

* keep **top-k explanations**, not just best-1
* impose a **genericity penalty**
* include a **yield term**, so the system prefers explanations that lead to distinct downstream material rather than just semantic cleanliness

### My current view

This is probably the highest-value addition for **material supply**$_{85%}$.

Not because abduction is glamorous. Because right now your biggest missing mechanism is still: **how do sparse narrative primitives become structured pressure?**

## 2. Partial-order causal-link planning: the local scaffolding layer

In classic least-commitment planning, you do not commit to a total order too early. SNLP-style partial-order planning represents a plan with causal links, safety conditions, and only as much ordering as is necessary. A causal link says one step establishes a proposition needed by another step. Threats are steps that could break that link, and safety conditions resolve the threat by constraining order. That is the part to import. ([AAAI][2])

### Why it matters here

Your graph contract already wants:

* setup refs
* payoff refs
* pressure provenance
* “earnedness”
* event approach / event homing
* missing support visibility

Those are basically causal-link questions.

If a candidate moment is “she finally opens the letter on the bus,” then the system should be able to say:

* what made that possible
* what had to already be true
* what would threaten it
* what consequences it opens

Without that, you are relying on local prose plausibility and after-the-fact LLM judgments about whether something feels prepared.

That is weak.

### The important shift

Do **not** use partial-order planning as full classical action planning for the whole story world.

Use it as a **small causal scaffold builder around candidate moments**.

That means operators are not low-level STRIPS actions like MOVE, PICKUP, OPEN-DOOR. They are event-level or social-level schemas such as:

* reveal
* delay
* deflect
* confess
* confront
* withdraw
* promise
* break routine
* test loyalty
* reroute obligation
* perform competence
* fail to answer
* choose silence
* open letter
* miss chance
* accept help
* refuse help

Each schema has:

* entry conditions
* effects on belief / obligation / attachment / status / knowledge / location
* maybe practice tags
* maybe tension and energy deltas

Then causal-link planning can do two very good things.

### Forward use

Given a concern and an operator candidate, it can build a **minimal partial scaffold**:

* what supports the operator
* what it could enable next
* what it threatens
* what remains open

This is how you get graph-native residue almost for free.

### Reverse use

This is the more interesting use.

Take a candidate payoff-like moment and ask: **what preconditions are still unsupported?**

That immediately yields setup candidates.

So partial-order planning becomes a setup/payoff diagnosis tool, not just a generator.

That is a big deal. It means “earnedness” is no longer just an evaluator judgment. It becomes a structural property.

### What to keep tiny

You do not need a deep planner here. I would cap it hard.

Maybe:

* 1 to 3 causal steps out from the focal moment
* 20 to 40 operator schemas
* only a handful of state dimensions
* unresolved supports allowed as placeholders

That last point matters. For authoring-time generation, you often want to know that a support is missing without solving it yet.

### Failure modes

1. **Operator schema explosion.**
   You end up hand-authoring an entire story grammar.

2. **False precision.**
   The planner looks rigorous but is really just encoding shaky guesses in symbolic form.

3. **Search sprawl.**
   It starts trying to solve full scenes or whole arcs.

4. **Ontology leak.**
   The internal operator library starts dictating the graph seam too directly.

So the discipline is: **use POP to shape local causal residue, not to replace concern-centered generation**.

### My current view

This is probably the highest-value addition for **earnedness, setup/payoff coherence, and graph-compilable support structure**$_{78%}$.

It is not the first thing I’d build. But once abduction starts producing structured pressures, this becomes the clean next step.

## 3. Soft-constraint optimization: the global compilation layer

In valued or weighted CSP, the point is to handle over-constrained problems by separating what must be satisfied from what should be satisfied when possible, then minimizing violations or costs across the whole solution. Schiex, Fargier, and Verfaillie frame exactly that contrast between hard constraints and softer preferences/costs. 

### Why it matters here

Local candidate scoring is not enough.

Suppose you generate 40 plausible candidate moments. Even with good local scores, you still need to choose:

* which ones enter the graph
* which ones coexist
* which ones are mutually exclusive
* which ones need setup support
* which ones are too redundant
* how concern coverage gets balanced
* how tonal spread gets maintained
* which unresolved payoffs are tolerable

That is a **global optimization** problem. Not a ranking problem.

And right now that is probably under-specified in your architecture.

### What it should optimize

I would not obsess over the exact solver family yet. The architectural idea matters more than whether the backend is WCSP, MaxSAT, MILP, or CP.

The key is to explicitly represent:

#### Hard constraints

* impossible world-state contradictions
* impossible co-presence
* impossible role assignments
* temporal impossibilities
* lane exclusivity if you have it
* forbidden graph shapes
* required support for certain payoff classes

#### Soft constraints

* concern coverage
* overdetermination
* payoff density
* novelty spread
* operator-family diversity
* charged-place reuse without overuse
* avoidance of near-duplicates
* tension curve smoothness
* aesthetic priors from human curation
* compression, meaning not too many nodes doing the same job

Then the compiler does not ask “what is the highest local score?”
It asks “what is the best **set**?”

That is a different problem, and it is the right one.

### Why this is more than just engineering hygiene

This layer is where you finally stop pretending that global structure will emerge from local LLM scores.

It often won’t.

Soft constraints are your way of saying:

* yes, this moment is strong
* but not if it duplicates that one
* and not if it creates an unsupported payoff cluster
* and not if it leaves one major concern invisible
* and not if it creates tonal monoculture

That is the compiler you actually need.

### One important nuance

I would not overcommit to “WCSP” as the one formalism.

The **concept** is soft-constraint optimization.
The **implementation** should be whatever matches your decision variables.

If most variables are binary selection/link/order choices, then a Boolean or integer formulation may be cleaner than full weighted CSP.

So I’d treat solver choice as secondary. The high-level move is what matters:

**separate hard consistency from weighted preference, then solve globally.**

### Failure modes

1. **Weight-tuning hell.**
   You end up hand-tuning 25 coefficients forever.

2. **Sterile consistency.**
   The solver learns to prefer safe but dead material because contradiction penalties dominate value.

3. **Hidden ontology bugs.**
   Bad modeling choices get mistaken for objective quality.

4. **Opaque decisions.**
   The compiler picks a set, but you cannot explain why.

So I’d insist on two things from day one:

* per-candidate contribution reports
* weight fitting from curator data, sooner rather than later

A soft-constraint compiler with arbitrary weights is just a fancy prejudice engine.

### My current view

This is probably the highest-value addition for **actual graph quality**$_{87%}$.

Not because it is sexy, but because without it you still have no principled answer to “which candidate moments survive together?”

## How the three fit together

This is the real point.

I would think of them as:

### A. Interpret

Use weighted abduction to infer a few high-yield latent readings of sparse primitives.

### B. Scaffold

Use partial-order causal-link planning to build a small causal shell around candidate moments from those readings.

### C. Compile

Use soft constraints to choose the best mutually compatible set of scaffolded candidates for the graph.

That is a coherent stack.

In your vocabulary:

**primitives**
→ abductive `CausalSlice` candidates
→ concern / appraisal / operator firing
→ candidate moments with causal supports and threats
→ soft-constraint graph compilation

That is much stronger than:

**primitives**
→ LLM scene ideas
→ local scoring
→ manual cleanup

## The extra thing that keeps coming up

The extra concept I would add, as glue, is **assumption-set tracking / nogood memory**, basically a lightweight ATMS idea.

de Kleer’s ATMS point is that reasoning should manipulate **assumption sets**, not just conclusions. That makes inconsistent alternatives manageable, context switching cheap, and avoids a lot of blind backtracking. ([ScienceDirect][3])

Why this matters here:

* each abductive explanation is an assumption set
* each partial causal scaffold depends on assumptions
* each failed graph compilation reveals incompatible assumption combinations

So when the compiler rejects a combination, you do **not** want to just retry blindly. You want to record a reusable nogood like:

* {hidden affair, dutiful-son self-image, public confession at dinner} is incompatible
* {financial desperation, quiet avoidance, no one reads the letter} leaves too many unresolved supports
* {revenge reading, remorse reading} cannot both dominate the same node cluster

That is the glue between the top three.

Not full ATMS first. Just:

* explicit assumption tags
* failed-set memory
* backjump to culprit assumptions, not last action

That feels genuinely additive$_{75%}$.

## One more practical thought

There is a fourth thing, less elegant but necessary: **weight fitting / preference learning** for the soft-constraint layer.

Otherwise this happens:

* abduction has hand-tuned priors
* causal scaffolding has hand-tuned operator utilities
* soft constraints have hand-tuned weights

and then you have three different hand-tuned preference systems drifting apart.

So I would plan early for:

* curator accept/reject data
* pairwise comparisons
* maybe simple learned reweighting over soft objectives

Not because that is philosophically beautiful, but because otherwise the compile layer will become taste fossilized as constants.

## My updated ranking

For your project, right now:

1. **soft-constraint optimization** for graph compilation quality$_{87%}$
2. **weighted abduction** for material supply from primitives$_{85%}$
3. **partial-order causal-link planning** for earnedness and support structure$_{78%}$
4. **assumption-set / nogood memory** as glue$_{75%}$

That ranking is by likely marginal value, not elegance.

If I ranked by conceptual cleanliness, I’d put abduction first.
If I ranked by immediate graph output quality, I’d put soft constraints first.

## What I’d build first

A very concrete sequence:

1. **Abductive CausalSlice builder**

   * Input: primitive bundle
   * Output: top-3 latent readings, each with explicit assumptions

2. **Event-level operator schema library**

   * 20 to 40 schemas
   * Preconditions/effects over a tiny state vocabulary

3. **Local causal scaffold builder**

   * Given a reading and focal operator, produce supports, threats, open conditions

4. **Soft-constraint compiler**

   * Select subset + links + ordering from candidate pool
   * Expose why each selected/rejected candidate moved

5. **Nogood memory**

   * Record rejected assumption combinations
   * Reuse them in future abductive and compile passes

That feels like the highest-value route$_{80%}$.

The next useful move is to sketch the exact typed predicate inventory for the abductive layer, because that choice will determine whether the rest becomes crisp or mushy.

[1]: https://aclanthology.org/P88-1012.pdf "Interpretation as Abduction"
[2]: https://cdn.aaai.org/AAAI/1991/AAAI91-099.pdf "1991-Systematic Nonlinear Planning"
[3]: https://www.sciencedirect.com/science/article/pii/0004370286900809 "An assumption-based TMS - ScienceDirect"
