Here’s the blunt memo.

The main read is:

1. Most classic cognitive architectures admit learned structure too early for a persistent creative system like yours.
2. Your `:trace / :provisional / :durable` membrane is the right correction, and the combo with anti-residue plus rule frontier looks plausibly novel$_{75%}$ in this exact form.
3. The 2-situation membrane fixture is a good assay, not a verdict.
4. The current flat pressure map is not a situation model. It is an appraisal cache with the world model amputated.

## 1. Cold start in other architectures

**Soar** solves cold start by learning chunks from actual problem-solving substates. When a substate produces results, Soar backtraces through the rules that produced them, saves the relevant superstate conditions, and creates learned rules from those saved structures. Newer Soar replaced older chunking with EBBS because the old mechanism had real generality/correctness problems; the manual’s arithmetic example drops from 1263 learned rules to 8 after EBBS. There is one narrow hint of staged admission: chunking is not used for decisions driven by numeric preferences, and Laird notes plans to add such chunks only when enough experience suggests they will be correct. But that is a special-case caution, not a general probationary membrane. ([arXiv][1])

**ACT-R** is even less like your membrane. Production compilation combines two productions that fire in sequence into one, specializing the rule by substituting retrieval results and merging the steps. On the retrieval side, ACT-R uses chunk activation; on the action-selection side, it uses production utilities. So ACT-R’s gate is subsymbolic strength and conflict resolution, not an explicit “store provisionally, then promote after later evidence” registry. New productions are learned from traces that actually fired, but once compiled they are part of procedural memory, not held behind a frontier. ([ACT-R][2])

**CLARION** is the closest mainstream cousin. It has a real staged story, but the stages are **implicit ↔ explicit**, not **provisional ↔ durable**. The top level supports bottom-up learning, top-down learning, and independent hypothesis testing; a successful action chosen implicitly at the bottom level can generate grounded explicit conceptual structures at the top. CLARION can learn with no a priori explicit knowledge, but it can also assimilate externally provided rules, plans, or categories when they exist. That is a genuine cold-start bridge. Still, once explicit knowledge is extracted, CLARION does not give you your kind of later demotion/quarantine membrane. ([HASS Homepages][3])

**ICARUS** also stages learning, but through problem solving and skill acquisition. It separates conceptual memory from skill memory; beliefs are instances of generalized concepts, and skills are stored in long-term skill memory. Means-ends analysis lets the system solve novel problems when no stored skill applies, and later work explicitly adds storage of new skills from successful problem-solving traces. Again, that is “derive from successful trace, then store,” not “store weakly, watch outcomes, then graduate or demote.” ([AAAI][4])

The punchline is simple: **Soar, ACT-R, CLARION, and ICARUS all stage learning somehow, but none of them really give you your full membrane**. They stage *derivation*, *compilation*, or *implicit-to-explicit extraction*. Your repo is staging **admission and later trust**, which is a different problem.

## 2. Narrative systems mostly dodged this problem

**MINSTREL** represents stories as graphs with state, act, goal, and belief nodes, then uses TRAMs to transform, recall, and adapt story subgraphs from memory. Above that sits Author Level Planning, which checks underspecified regions and fills them with retrieved/adapted material. That is excellent for transformable narrative memory, but it is still a case-adaptation system, not a probationary memory membrane. The retrieved/adapted fragment is used because the author-level planner asked for it, not because it passed a later evidence-based promotion test. ([AAAI][5])

**Versu** solves rich social situations by separating **roles** from **characters**, encoding **wants/desires** over the simulation state, and coordinating interaction through **social practices** authored from a bird’s-eye perspective. Social practices provide coordination and continuity, but individual agents still decide what to do based on their own desires. That is extremely relevant to your situation-model question, but not to admission. Versu’s richness comes from authored social ontology and agent desires, not from learned episodic material slowly earning planner access. ([UK Computer Science][6])

**Façade** is the opposite extreme. It gets psychological richness through a tightly authored beat architecture. Beat selection uses preconditions, priorities, weights, and comparison to a desired tension arc. For a ~20 minute experience, Mateas and Stern report ~2500 joint-dialog-behaviors across 27 beats, with only about 15 beats seen in one playthrough. That gives you a useful warning: psychologically rich scenarios need far more internal structure than a flat pressure gauge, but Façade bought that richness with heavy authoring rather than a long-run memory ecology. ([Baskin School of Engineering][7])

So the right comparison is:

* **MINSTREL** gives you transformable story memory.
* **Versu** gives you roles, desires, and social practices.
* **Façade** gives you beat-conditioned dramatic progression.
* **Your repo** is trying to add a long-run trust membrane on top of all that.

That is a real move beyond the old systems, not a literature rename$_{80%}$.

## 3. Assessment of the repo’s staged admission design

The repo is now in the right regime.

At the kernel level, the cold-start seam is exactly where it should be: stored-source races in rationalization and reversal admit authored sources first, dynamic durable sources second, and dynamic provisional sources third as explicit lower-ranked trials. Store time no longer durabilizes new family-plan episodes. Promotion happens later through use/outcome logging, promotion evidence, admission reconciliation, and then rule-access reconciliation. That is the right chain.

Why this is good:

* It keeps the **structural graph** static and moves trust into **episode/rule access state**.
* It allows **trial use** of provisional material without letting one lucky fit become precedent.
* It treats promotion, demotion, anti-residue, and frontier opening as one evidence pipeline, which is the cleanest local abstraction in the repo.

What is still weak:

**First, the positive evidence channel is too narrow.** Right now the live strong positive signal is basically repeated `:cross-family-use-success`. That is a good signal, but not enough. In a psychologically rich system, some genuinely useful material will be vindicated by later world confirmation, event success, conductor-driven reuse, or stable same-family success under changed conditions. If you only trust cross-family reuse, you will under-promote good material.

**Second, the ranking policy is conservative to the point of starvation.** Authored candidates are rank 0. Dynamic durable are rank 1. Dynamic provisional are rank 2. That is sane for regression safety, but it means cold start depends heavily on *holes in authored coverage*. In authored-rich fixtures, provisional sources may never win enough races to accumulate evidence. So a pass/fail result can tell you more about fixture scarcity than membrane quality.

**Third, the design is stronger than the benchmark evidence.** Your own sprint note says the 12-cycle and 50-cycle Puppet Knows runs are clean but still authored-source dominated, with little or no live use-history/promotion/flag movement. That means the fixed-chain proof is ahead of the live benchmark. Say that plainly. Do not oversell the soak.

My verdict: **the design is right$*{85%}$, the live evidence is still thin$*{40%}$**.

## 4. Critique of the proposed 2-situation membrane benchmark

The fixture is well targeted. It fixes the exact failure of Puppet Knows: complete authored coverage prevents dynamic reuse from ever mattering. The asymmetric authored coverage plus shared structural compatibility constraints is the correct way to force the kernel to prove real dynamic reuse instead of fake retrieval.

That said, it has four problems.

**1. It is a membrane smoke test, not a general benchmark.**
Two situations, one shared bridge cue, one shared failed-goal identity, one shared leaf objective. That is fine for proving the chain can fire. It is not enough to tell you whether the system handles richer analogy, partial mismatch, or false friends.

**2. It probably overestimates generalization.**
If both situations literally share the same failed-goal identity and compatible retracted-fact structure, then you are testing near-transfer, not the kind of oblique transfer that a creative daydreamer actually needs. Good first assay. Weak final assay.

**3. It bundles too many things into one pass condition.**
Cue overlap, structural compatibility, candidate ranking, attributed use, delayed vindication, promotion, and frontier movement are all mixed together. When it fails, diagnosis will be muddy.

**4. Its stretch criteria are not honest enough for 20 cycles.**
The spec itself admits Level 3/4 may not happen unless a separate cross-family evidence path emerges. Good. Then treat Level 1/2 as the true target, and make Level 3/4 a separate longer-run condition.

The fix is not to discard it. The fix is to make it the **first assay in a benchmark ladder**:

* **Assay A:** 20-cycle smoke test, dynamic candidate visibility and selection.
* **Assay B:** 100-cycle soak, promotion/demotion/frontier movement.
* **Assay C:** negative controls, incompatible goals, false bridge, complete-authored-coverage baseline.
* **Assay D:** ablation comparison against no membrane, and against no provisional trials.

The most important missing comparison is this one:

1. **Immediate durable** (bad old world)
2. **Current membrane**
3. **Current membrane + forced provisional exploration**
4. **Current membrane + broader evidence types**

Without that matrix, you cannot tell whether failures come from the membrane or from starvation.

## 5. Question 4: richer situation models

This part is easier than it looks.

**The flat map should survive, but only as a cache.**
Do not throw away `:activation`, `:ripeness`, `:threat`, `:hope`, etc. Those are useful scheduler variables. But they should be **derived** from a richer situation representation, not authored as the source of truth.

The current map is not a situation model. It is a **derived appraisal summary**.

The right middle ground is:

### Layer 1: canonical fact space

This is the real world model. Characters, beliefs, locations, relationships, recent events, obligations, symbolic objects, surveillance risks, injuries, promises, lineages.

### Layer 2: situation slice

A situation is a **queryable subgraph** over that world model:

* who is involved
* what unresolved conflict is active
* what facts are salient
* which beliefs are asymmetric
* which causal pivots matter
* which events are pending
* which values or norms are in conflict

### Layer 3: appraisal cache

Per character, derive a small vector:

* relevance / importance
* valence
* blame / credit
* control / coping potential
* expectedness
* exposure risk
* attachment threat
* pride / shame
* action pull / avoidance pull

### Layer 4: scheduler pressures

From that appraisal cache, derive the fast variables the kernel already uses:

* `:activation`
* `:ripeness`
* `:threat`
* `:hope`
* maybe also `:shame`, `:exposure`, `:flow-pull`, `:attachment-ache`

This is exactly the direction your broader conducted-daydreaming draft already points: canon, constraints, motifs, and a WorldStateEngine with `character_knows`, `validate_event`, `apply_event`, and `contradicts`. The kernel’s situation layer should converge upward into that model, not remain a separate scalar toy. 

Appraisal theory is the right guide for what the richer layer must contain. EMA argues that a computational emotion model must represent events and their consequences, future implications, blame and responsibility, power and coping potential, and coping strategies that can change either the world or the agent’s own beliefs, desires, intentions, and plans. It also argues that appraisal dynamics track changes in the person-environment relationship over time, not just one-shot labels. OCC and FAtiMA make this more implementable by giving you explicit eliciting conditions and a socio-emotional agent toolkit with dialogue structure. ([Stacy Marsella][8])

So the compact answer is:

> **A situation should carry facts, beliefs, relationships, causal pivots, and coping affordances.
> Pressure numbers should be derived from them.**

## 6. What MINSTREL, Versu, and Façade teach your situation model

**MINSTREL** says: represent situations in transformable story graphs with at least `state`, `act`, `goal`, and `belief` nodes. That is the right minimum if you want retrieval/mutation/reversal to operate on something richer than numbers. ([AAAI][5])

**Versu** says: separate **roles** from **characters**, and represent **social practices** explicitly. A situation should not be only “Tony feels threat 0.47.” It should also know whether the active practice is a lesson, a rebuke, a trespass, a performance, a hiding move, or an initiation. Social practices provide continuity and coordination; individual characters still act from their own desires. That is a near-perfect fit for psychologically charged scenes. ([UK Computer Science][6])

**Façade** says: authorial tension matters. You need beat-like structure with preconditions, priorities, and tension effects. Not because you want a centralized drama manager, but because psychologically interesting situations need **phase structure**. Some moves escalate. Some reveal. Some repair. Some abort. The scalar pressure gauge is too thin to encode that alone. ([Baskin School of Engineering][7])

So the right synthesis for your kernel is:

* **MINSTREL** for transformable situation graphs.
* **Versu** for roles, desires, practices, and social continuity.
* **Façade** for beat gating and escalation structure.
* **EMA / OCC / FAtiMA** for appraisal and coping state.

## 7. A concrete middle-ground schema

Do this, not a full heavyweight simulation:

```clj
{:situation/id :seeing-through
 :participants [:tony :grandma]
 :roles {:child :tony
         :guardian :grandma
         :hidden-lineage-bearer :tony}
 :salient-facts [...]
 :private-beliefs
 {:tony [...]
  :grandma [...]
  :monk [...]}
 :relationship-state [...]
 :pending-events [...]
 :counterfactual-pivots [...]
 :active-practices [...]
 :motifs [...]
 :appraisal-cache
 {:tony {...}
  :grandma {...}
  :monk {...}}
 :derived-pressure
 {:activation 0.61
  :ripeness 0.73
  :threat 0.44
  :hope 0.52
  :shame 0.68
  :exposure 0.31
  :flow-pull 0.77}}
```

That is enough. You do **not** need a full theorem prover per situation. You need maybe **8 to 15 typed facts**, **2 to 4 belief asymmetries**, **1 to 3 pending events**, and **4 to 7 derived appraisal scalars**.

## 8. Graffito: what the four situations should carry

For Graffito, the kernel should daydream about **lineage, body-knowledge, absence, danger, ego, and recognition**. Anger/hope/threat alone will miss the point.

### `:seeing-through`

Core conflict: **Is Tony broken, gifted, or both? Who sees him correctly?**

Carry:

* Tony’s self-model: “everything comes out crooked.”
* Tony’s coping fact: drawing regulates him.
* Grandma’s counter-belief: the crookedness is the gift.
* early lineage evidence: Tony as Scribe-correspondence, not yet fully owned.
* judgment/exposure facts: who has seen Tony fail, who has seen Tony flow.
* value conflict in Grandma: protect Tony vs honor lineage.

Derived appraisals:

* Tony: shame, curiosity, relief-through-art, fear-of-being-seen-wrong.
* Grandma: pride, fear, protective control, recognition certainty.

Families this should drive:

* rationalization
* rehearsal
* roving

### `:mission`

Core conflict: **Is the Magic Can a calling, a burden, or a trap?**

Carry:

* lineage chain: Grandma → Monk → Tony.
* object affordance: Magic Can only works in embodied flow.
* Monk’s teaching fact: “give your mind to your body and let it flow.”
* Tony’s desire for mastery and calm.
* Grandma’s fear that the same inheritance consumes whoever bears it.
* concrete opportunity windows: when Tony could create, where, under what conditions.

Derived appraisals:

* purpose
* obligation
* excitement
* fear-of-cost
* control/capability

Families:

* rehearsal
* recovery
* repercussions

### `:edge`

Core conflict: **Can art happen without repeating Monk’s loss?**

Carry:

* event history: arrest, instability, absence.
* surveillance/authority model: police, visibility, being watched.
* Tony’s mixed model of Monk: admiration + abandonment.
* Grandma’s mixed model of Monk: understanding + anger/fear.
* concrete risk factors: location exposure, authority proximity, time pressure.
* causal pivots: what exactly led to Monk’s loss.

Derived appraisals:

* threat
* blame
* low control
* attachment-loss
* recurrence risk

Families:

* reversal
* repercussions
* possibly revenge, if authority is personified strongly enough

### `:ring`

I’m reading this as the **recognition / ego / legacy loop** unless the script anchors it to a more literal object or locale.

Core conflict: **Is the mark for ego, belonging, healing, or purpose?**

Carry:

* Grandma/Motherload rebuke of vanity.
* public-name / wall / signature facts.
* Tony’s desire to be seen.
* Monk’s compulsion to create even when it costs him.
* lineage semantics: art as transpersonal inheritance vs self-branding.
* norm conflict: authentic expression vs narcissistic inscription.

Derived appraisals:

* pride
* shame
* authenticity strain
* belonging hunger
* ego temptation

Families:

* rationalization
* reversal
* repercussions

## 9. Graffito-specific facts a Mueller-style system actually needs

Not generic “character state.” These:

* **belief asymmetries**
  Tony thinks difference = defect; Grandma thinks difference = gift; Monk thinks body already knows.

* **ambivalent relationships**
  Tony → Monk = admiration + resentment.
  Grandma → Monk = pride + fear + anger.
  Grandma → Tony = protectiveness + recognition of calling.

* **causal wound history**
  What did art cost Monk? Arrest? absence? instability? humiliation?
  Without this, reversal/repercussions are empty.

* **embodied skill facts**
  Flow is bodily, not propositional. Represent skill, calm-through-drawing, spray/body coupling, freeze/unfreeze triggers.

* **symbolic object lineage**
  Magic Can is not just an item. It is inheritance, affordance, and test.

* **dual-identity / cross-world correspondence**
  Tony ↔ Scribe, Grandma ↔ Motherload.
  This belongs in contexts/role-correspondence facts, not buried in prompt text.

* **surveillance geometry**
  Who can see whom, where, and with what consequence.
  “Threat” without observability structure is useless here.

* **norm/value conflicts**
  protect child vs honor calling; express self vs erase ego; create vs remain safe.

That is enough for interesting daydreaming. More than that comes later.

## Bottom line

Your admission membrane is pointed the right way. The cold-start comparison to Soar/ACT-R/CLARION/ICARUS strengthens the case rather than weakening it: those systems stage learning, but they generally do not stage **trust** the way you need for a persistent creative memory ecology. The 2-situation fixture is worth building, but it should be treated as a first assay. And for Question 4, the answer is unambiguous:

**Pressure numbers should become derived scheduler state.
The real situation model should be a small typed fact-space with beliefs, relationships, causal pivots, coping affordances, and role/practice structure.**

The next benchmark I’d build is a **4-situation Graffito miniworld** with two true bridges, one false bridge, and one hard negative. That would test the membrane and the richer situation model together.

Look away from the screen for 20 seconds. Relax your shoulders.

[1]: https://arxiv.org/pdf/2205.03854?utm_source=chatgpt.com "Microsoft Word - Soar Introduction 5-7-2022.docx"
[2]: https://act-r.psy.cmu.edu/wordpress/wp-content/themes/ACT-R/workshops/2001/talks/pgss.pdf "https://act-r.psy.cmu.edu/wordpress/wp-content/themes/ACT-R/workshops/2001/talks/pgss.pdf"
[3]: https://homepages.hass.rpi.edu/rsun/folder-files/clarion-intro-slides.pdf "https://homepages.hass.rpi.edu/rsun/folder-files/clarion-intro-slides.pdf"
[4]: https://aaai.org/Papers/AAAI/2006/AAAI06-231.pdf "https://aaai.org/Papers/AAAI/2006/AAAI06-231.pdf"
[5]: https://cdn.aaai.org/ojs/12419/12419-52-15947-1-2-20201228.pdf "https://cdn.aaai.org/ojs/12419/12419-52-15947-1-2-20201228.pdf"
[6]: https://cs.uky.edu/~sgware/reading/papers/evans2014versu.pdf "https://cs.uky.edu/~sgware/reading/papers/evans2014versu.pdf"
[7]: https://users.soe.ucsc.edu/~michaelm/publications/mateas-gdc2003.pdf "https://users.soe.ucsc.edu/~michaelm/publications/mateas-gdc2003.pdf"
[8]: https://stacymarsella.org/publications/pdf/EMA_Dynamics.pdf "https://stacymarsella.org/publications/pdf/EMA_Dynamics.pdf"
