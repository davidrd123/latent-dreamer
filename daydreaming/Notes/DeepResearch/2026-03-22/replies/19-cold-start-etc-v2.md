Answering prompt **19**, not 14.

## Bottom line

The cold-start fix is correct. Letting `:provisional` rationalization/reversal episodes enter stored-source races below authored sources is the minimum repair that makes promotion possible at all.

The bigger membrane design is also the right shape$_{85%}$. Mainstream cognitive architectures mostly do **not** solve your problem. ACT-R uses graded retrieval strength and compilation, Soar and ICARUS generally admit learned procedures immediately, and CLARION refines extracted rules after they already exist as actionable top-level knowledge. The nearest outside precedent to your design is a hybrid of CLARION-style refinement, Prodigy-style “keep the learned control knowledge only if it improves performance,” and Soar-style later forgetting of low-value learned rules. ([ACT-R][1])

Repo-grounded read: the current kernel is now doing the right thing at the seam. New family-plan episodes enter as `:trace` or `:provisional`, not `:durable`; provisional dynamic episodes can compete in rationalization/reversal source races with an authored-first penalty; rule access lives in a separate `:accessible / :frontier / :quarantined` registry; and the live Puppet Knows runs still look more like seam regressions than real membrane stress tests because authored sources keep dominating.

## 1. How other architectures handle cold start

**ACT-R** does not have an admission membrane. Declarative retrieval picks the matching chunk with the highest activation above threshold. Activation combines base-level activation, which reflects recency and frequency of use, with spreading activation from current context; the fan effect weakens spread as competitors multiply. ACT-R’s production compilation then composes sequential productions into a new production. That is graded availability, not probation. A newly learned structure becomes ordinary memory or ordinary procedure immediately; the controls are activation, interference, and utility, not trust tiers. ([ACT-R][1])

So ACT-R’s answer to “how do spurious chunks stay under control?” is basically: decay, contextual competition, and retrieval threshold. That is fine for tightly authored lab models. I would not trust it for a self-writing creative system that keeps generating new candidate residue.

**Soar** is closer to your case and still mostly more permissive than you want. Standard chunking learns new procedural rules from impasse-driven substate reasoning, creates chunks continuously as results are produced, and adds them to procedural memory immediately. EBBS was added later because raw chunking produced over-specialization and correctness problems; in the Soar manual’s arithmetic example, EBBS reduced learned rules from 1263 to 8 by learning more general rules with correctness guarantees. Modern Soar also tracks activation metadata for learned rules and can forget rarely used learned productions. Laird’s 2022 architecture paper adds one important caveat: for numeric-preference decisions, chunking is withheld until enough accumulated experience exists that the learned rule is likely to be correct. That is the closest Soar gets to a probationary frontier. ([Soar][2])

There is also an old warning here that matters for your project. A long-term Soar learning study found performance improved as learned productions were removed, peaking after roughly 90% of them had been deleted. That is not a weird corner case. It is the classic utility problem in symbolic learning. Your anti-sludge anxiety is warranted. ([ACT-R][3])

**CLARION** is the closest large architecture to your `:provisional` intuition, but it still doesn’t go as far as your membrane. CLARION’s bottom-up story is: implicit successful behavior can be extracted into explicit top-level rules; those extracted rules are then revised based on future world states, broadened if successful, narrowed if too general, and discarded if they fail badly enough. Independent Rule Learning also generates rule subsets and deletes those that prove ineffective. Explicit rules compete via utility once they exist. So CLARION has refinement and deletion, but not a separate “retrievable as trial source, not yet fully trusted” tier. ([HASS Homepages][4])

A side note from CLARION is actually useful for your scheduler: its goal-list formulation lets multiple goals compete by strength, with older goals weakening over time but able to reassert themselves if conditions change. That is a better analogy for your `activation + ripeness` split than a pure stack model. ([HASS Homepages][4])

**ICARUS** is simpler. When means-ends problem solving achieves a goal, ICARUS creates a new skill from the successful solution trace; when similar problems recur, it executes the learned skill reactively instead of solving again. That is immediate macro-operator formation from one successful trace, not staged admission. It works because ICARUS assumes comparatively clean task structure and success conditions. ([ICDST E-print Archive][5])

**Prodigy** is the strongest outside precedent for your membrane. It learned control rules from success and failure traces, **and** it kept statistics on learned rules, retaining only those whose inclusion improved problem solving over time. That is not your exact `:trace / :provisional / :durable` ladder, but it is unmistakably evidence-gated retention. Prodigy is also a warning: macro-operator learning sometimes degraded performance. So “more learned structure” can absolutely make a planner worse. ([Vanderbilt CDN Dev][6])

So the blunt answer to Q1 is:

* ACT-R: no gate, just activation economics.
* Soar: immediate admission, later repair via EBBS and forgetting.
* CLARION: extraction plus revision/deletion.
* ICARUS: one-success macro-skill learning.
* Prodigy: closest thing to usefulness-gated retention.

Your membrane is not a literature rename. It is a response to a noisier regime: self-generated symbolic residue in a persistent system.

## 2. Is the staged admission design well chosen?

Yes, mostly. The important part is the **state machine**, not the exact thresholds.

The `:trace → :provisional → :durable` ladder is the right abstraction$_{80%}$. `:trace` handles auditable residue. `:provisional` gives bootstrap reuse without letting fresh material steer the whole system. `:durable` marks material that has earned planner influence. That is better than ACT-R’s “everything is in DM immediately” and better than Soar/ICARUS’s “one successful derivation, now it is a rule.” It matches your actual failure mode: self-reinforcing reuse of content that merely fit once.

The **cross-family-use-success** criterion is a good **primary** signal because it measures exportability. It asks whether an episode helps outside the operator family that created it. That is a transfer test. For groove prevention, that is exactly what you want. A rationalization that only helps more rationalization is suspect; a reversal that later helps rehearsal or roving is more interesting.

But it is too narrow as the **only** positive witness. Cross-family success is a proxy for usefulness, not a proxy for truth or groundedness. I would add two more positive evidence types:

1. **world-confirmed downstream success**: the episode contributes to a later branch whose consequences line up with later facts or with a resolved concern;
2. **same-family success with external corroboration**: same-family reuse should still count a little when it demonstrably changes later appraisal or action and is not just looped self-soothing.

So I would keep cross-family success as the strongest signal, but not the only admissible positive evidence.

`threshold = 2` feels right for a live system$*{65%}$ and too strict as the sole promotion route for a 20-cycle toy world$*{80%}$. In the real system, two independent successes buy hysteresis and lower the chance that one lucky reuse creates permanent precedent. In a tiny benchmark, though, that threshold can make promotion a sampling artifact unless the world is deliberately engineered to force two opportunities. Your current repo notes already suggest the real bottleneck is not the threshold by itself; it is that authored sources still absorb most opportunities before dynamic reuse can accumulate evidence.

So I would **not** lower the global threshold yet. I would instead broaden the evidence menu and keep the staged benchmark criteria exactly as staged. Level 3 should include any membrane movement, not only durable promotion.

**Roving should be non-promotable by default, but “never promotable” is too rigid.** The current repo stance is correct for now: most roving episodes are attentional escape or pleasant surfacing, not reusable strategy. That matches how ACT-R-like priming or context cues behave, and it matches the idea that roving is not the place to store precedent. But later you will probably want a separate durable path for a subset of roving material, something like a `regulation-anchor` class: episodes that repeatedly reduce pressure across families without backfire or loop risk. That is not the same thing as a payload exemplar, so do not shoehorn it into the same lane.

The **`:same-family-loop` flag** is smart. I do not know a canonical major-architecture analog for exactly this mechanism. The nearest family resemblance is Soar’s forgetting of rarely used learned rules, ACT-R’s competition/interference, and older utility-problem work on filtering learned control knowledge. Your flag is more explicit because your failure mode is more explicit: “this episode keeps waking up only to help its own family talk to itself.”

**Demotion from `:durable` back to `:provisional` is unusual and correct.** It would be dangerous in classical symbolic architectures if learned rules were proofs of deterministic reasoning. Your episodes are not proofs. They are partly speculative, partly evaluative, partly generated. So reversible admission is the right move. The only hard requirement is hysteresis:

* promotion should require later evidence;
* demotion should fire on contradiction/backfire or repeated stale failure;
* re-promotion should require evidence **after** the demotion event, not lifetime totals.

That last part matters. Otherwise you get oscillation and fake rehabilitation.

One good analogy here comes from Soar RL. In Soar, numeric RL preferences influence operator selection only when other knowledge is insufficient. That is a good model for your evaluator too. Use the evaluator as a subordinate signal under uncertainty, not as the thing that decides truth. ([arXiv][7])

So the design verdict is:

* `:trace / :provisional / :durable`: keep.
* cross-family use: keep as strongest witness.
* add world-grounded success and weak same-family corroborated success.
* roving: default non-promotable; later add a distinct regulation path.
* demotion: keep, with hysteresis.
* evaluator: veto/triage only.

## 3. Is the 2-situation membrane benchmark strong?

Yes. It is much stronger than current Puppet Knows.

The key reason is simple: **it converts promotion from a theoretical possibility into a structurally forced opportunity**. Current Puppet Knows still has authored coverage in the places that matter most, so dynamic family-plan episodes can exist without ever winning consequential races. That means you are mostly testing runtime continuity and effect-seam safety. The proposed membrane fixture instead creates deliberately incomplete authored coverage plus shared structural compatibility, so the system must either reuse its own material or fail to bridge at all. That is the right test.

There is a standard evaluation idea nearby, though not a standard name I’d trust. Wray and Lebiere argue that architecture evaluation should not be reduced to raw task performance; it should include **taskability** on novel tasks in the same domain, plus **incrementality** and **knowledge utilization**. Your membrane fixture is exactly that kind of within-domain novelty test: can the system reuse what it learned, and can you show that the reused knowledge was actually utilized rather than merely present? ([AAAI][8])

So I’d describe your pattern as a **scaffolded transfer benchmark** or **forced cold-start transfer test**. “Asymmetric authored coverage” is a good local name, but the concept is: leave a gap that can only be filled by the system’s own prior products.

Your staged observables are good:

1. dynamic candidates appear,
2. use-history is recorded,
3. a membrane transition fires,
4. a promoted episode opens a frontier rule and that rule gets used.

That is the correct way to evaluate this. Final task score alone would be useless.

What I would add:

* **dynamic share of candidate races** over time, not just presence/absence;
* **dynamic win rate conditional on eligibility**, split by authored vs durable vs provisional;
* **pending-use resolution rate** and average time-to-resolution;
* **promotion precision**: promoted items later contribute again without immediate demotion;
* **frontier utilization**: opened rules that are later actually used, not just opened;
* **flag incidence by cause**: stale, same-family-loop, contradicted, backfired;
* **knowledge utilization** in Wray/Lebiere’s sense: of the knowledge that could have applied, what fraction actually shaped behavior. ([AAAI][8])

I would also add one negative diagnostic that your current plan underemphasizes: **why was a dynamic source excluded?** Log threshold miss, structural mismatch, same-family-loop block, stale block, contradicted block, authored outrank, or recent-episode gate. Otherwise you won’t know whether the fixture failed because the membrane is too strict or because the structural bridge never actually existed.

On cycle counts: **20 cycles is realistic for Level 1 and maybe Level 2** in a deliberately forced 2-situation world. It is **not** enough for stable promotion statistics unless the benchmark is very tightly engineered. For Level 3, I’d expect either:

* 20 cycles × many random seeds, or
* 50–100 cycles in a smaller number of seeds.

Single-run anecdotes will lie to you here. What you care about is the distribution of time-to-first-dynamic-win, time-to-first-use-resolution, and time-to-first-promotion/demotion.

So my recommendation is blunt: **fix the benchmark before changing the membrane again**. Current Puppet Knows is still the wrong judge.

## 4. How the situation model should evolve

The current flat situation map is a **scheduler stub**, not a situation model.

That is not a criticism of the first benchmark pass. It is the right cheap control surface for getting something to run. But if you want psychologically interesting daydreaming, you need the numbers to become **derived summaries** of a richer fact-space, not the primary representation.

Appraisal theory gives the right middle layer. Scherer’s component-process view organizes emotion around appraisal criteria such as novelty/expectedness, goal relevance, valence, agency/intentionality, control/power, and fairness. Lazarus/Smith make the other crucial point: coping is part of the loop. Problem-focused and emotion-focused coping feed back into later appraisal and later emotion. That means a “situation” should not just say “threat = 0.55.” It should say what happened, who it matters to, who caused it, what can be done, and whether the current coping path is changing later appraisal. ([Faculty of Psychology and Education][9])

So I’d move to a three-layer representation:

**1. Canonical fact-space.**
Entities, relationships, places, events, beliefs, hidden facts, commitments.

**2. Situation/appraisal layer.**
An unresolved concern anchored to facts, carrying:

* protagonist and target,
* triggering event or condition,
* active goals and blocked goals,
* appraisal fields: novelty, goal relevance, congruence, agency/blame, coping potential, norm/fairness,
* currently available coping modes,
* epistemic asymmetry: who knows what,
* relationship context.

**3. Derived control layer.**
Your current `activation`, `ripeness`, `anger`, `hope`, `threat`, `indices` remain, but they are computed from 1 and 2.

That is the middle ground you asked for. Not a giant ontology. Not a raw pressure gauge.

Two existing interactive-narrative systems tell you what should go in that middle layer.

**Versu** is the best template for socially rich situations. Its central abstraction is the **social practice**, a recurring social situation that coordinates agents without taking away their autonomy. Agents still select actions by utility over their desires. Versu also models **role evaluations** with remembered explanations: how well someone is performing as a father, husband, daughter, suitor, and so on, and why the evaluator thinks that. That is exactly the level your current pressure maps lack. It is not enough for Tony to have “threat 0.55.” Someone needs to evaluate Monk as father, artist, and danger; Grandma needs to hold explained judgments, not just affect weights. ([UK Computer Science][10])

**Façade** is the best template for short-horizon dramatic progression. Its beats carry preconditions, weights, priorities, and story-value effects, and the drama manager sequences beats to fit a tension arc. That is the right abstraction for event and scene progression above your daydream families. Use it for scene-scale commitment and causal progression, not for long-term memory admission. Also, Façade is a warning about content cost: roughly 2500 authored joint-dialog behaviors, 27 beats, and years of authoring for a ~20 minute experience. Do not copy that density into v1. ([Lorene Shyba's Collaborative Web Space][11])

**MINSTREL** is useful in a different place. Its TRAMs transform, recall, and adapt cases when ordinary problem solving fails. That is useful for reversal, mutation, and case-based scene invention. It is **not** a runtime situation model. Treat it as inspiration for creative operator design, not for core world state. ([AAAI][12])

If later you want a portable ontology layer for archival, analysis, or external interchange, GOLEM looks plausible: it explicitly models characters, relationships, events, settings, narrative, and inference. For runtime control, though, it is overkill. Use something that simple first. ([ResearchGate][13])

### Minimal Graffito structure

For Graffito, the kernel needs more than “street overwhelm” and “grandma’s apartment.” It needs a **small social-psychological world model**.

I would give Graffito at least these runtime structures:

**Character traits/regulation**

* Tony: sensory-load threshold, drawing as regulation, flow susceptibility, social misread risk.
* Monk: creative compulsion, embodied teaching skill, unreliability as caregiver.
* Grandma: protective authority, lineage knowledge, fear/pride conflict.

**Role evaluations with explanations**

* Tony’s view of Monk as father.
* Monk’s view of Tony as apprentice / son / inheritor.
* Grandma’s view of Monk as gifted and dangerous.
* Grandma’s view of Tony as vulnerable and chosen.
  These should store explanations, not just scores. Versu is right about that.

**Epistemic asymmetries**

* Who knows the Can’s history.
* Who knows the cost of the lineage.
* Who interprets Tony’s difference as disability, gift, danger, or all three.

**Social practices**

* street-overwhelm,
* apartment-grounding,
* embodied teaching/painting-in-flow,
* police-threat,
* initiation/transmission,
* ego-temptation vs purpose.
  These are recurring frames, not one-off scenes.

**Active concerns**

* control vs overload,
* belonging vs abandonment,
* calling vs safety,
* ego-recognition vs communal purpose.

**Event affordances**

* near miss,
* Grandma grounds,
* Monk demonstrates flow,
* Can activates,
* police interruption,
* Tony successfully uses art to regulate or connect.

**Derived pressures**
Only after the above do you compute `activation`, `ripeness`, `anger`, `hope`, `threat`, and retrieval indices.

That gives you a situation model small enough to run and rich enough to matter.

The direct answer to your Graffito question is: Tony’s neurodivergence should **not** live as a single tag. It should live as a causal cluster:

* high sensory load increases overwhelm,
* drawing and embodied flow reduce it,
* misunderstanding by others changes agency/blame appraisals,
* contact with Monk and the Can changes coping potential.

Likewise Monk should not be “absent father” as lore text. He should be a source of both skill transmission and instability. Grandma should not be generic warmth. She should be a character with explained role evaluations and opposed goals: protect Tony, preserve lineage, avoid repeating Monk’s damage.

That is what a Mueller-style system needs to daydream well about this material.

## What I would do next

1. **Keep the membrane.** Do not revert toward ACT-R/Soar-style immediate full admission.

2. **Broaden positive evidence.** Keep `cross-family-use-success` as the strongest witness, but add world-confirmed downstream success and weak same-family-with-external-corroboration.

3. **Do not touch the global threshold yet.** `2` is fine for the real system. Benchmark with better structure and more seeds first.

4. **Treat roving as “normally non-promotable,” not metaphysically impossible to durabilize.** Add a later `regulation-anchor` lane if the data demands it.

5. **Use the membrane fixture, not Puppet Knows, to judge memory ecology.** Puppet Knows is still mostly a seam test.

6. **Promote situations into appraisal frames over facts.** Keep the flat numbers as derived scheduler summaries.

That is the shortest path to getting signal instead of prettier sludge.

Look away from the screen for 20 seconds. Relax your shoulders.

[1]: https://act-r.psy.cmu.edu/actr7.x/reference-manual.pdf "https://act-r.psy.cmu.edu/actr7.x/reference-manual.pdf"
[2]: https://soar.eecs.umich.edu/soar_manual/04_ProceduralKnowledgeLearning/ "https://soar.eecs.umich.edu/soar_manual/04_ProceduralKnowledgeLearning/"
[3]: https://act-r.psy.cmu.edu/wordpress/wp-content/uploads/2012/12/626kennedyPaper.pdf "https://act-r.psy.cmu.edu/wordpress/wp-content/uploads/2012/12/626kennedyPaper.pdf"
[4]: https://homepages.hass.rpi.edu/~rsun/ra-tutorial.pdf "https://homepages.hass.rpi.edu/~rsun/ra-tutorial.pdf"
[5]: https://dl1.icdst.org/pdfs/files/8f12627480235d0c9136ca5e18fa8b8e.pdf "https://dl1.icdst.org/pdfs/files/8f12627480235d0c9136ca5e18fa8b8e.pdf"
[6]: https://cdn-dev.vanderbilt.edu/t2-my-dev/wp-content/uploads/sites/3191/2021/02/CognitiveArchitecture-copy.pdf "https://cdn-dev.vanderbilt.edu/t2-my-dev/wp-content/uploads/sites/3191/2021/02/CognitiveArchitecture-copy.pdf"
[7]: https://arxiv.org/pdf/2205.03854 "https://arxiv.org/pdf/2205.03854"
[8]: https://cdn.aaai.org/Workshops/2007/WS-07-04/WS07-04-015.pdf "https://cdn.aaai.org/Workshops/2007/WS-07-04/WS07-04-015.pdf"
[9]: https://ppw.kuleuven.be/okp/_pdf/Scherer2019TEPEA.pdf "https://ppw.kuleuven.be/okp/_pdf/Scherer2019TEPEA.pdf"
[10]: https://cs.uky.edu/~sgware/reading/papers/evans2014versu.pdf "https://cs.uky.edu/~sgware/reading/papers/evans2014versu.pdf"
[11]: https://lorishyba.pbworks.com/f/MateasStern_Facade.pdf "https://lorishyba.pbworks.com/f/MateasStern_Facade.pdf"
[12]: https://cdn.aaai.org/Symposia/Spring/1993/SS-93-01/SS93-01-021.pdf "https://cdn.aaai.org/Symposia/Spring/1993/SS-93-01/SS93-01-021.pdf"
[13]: https://www.researchgate.net/publication/396103969_The_GOLEM_Ontology_for_Narrative_and_Fiction "https://www.researchgate.net/publication/396103969_The_GOLEM_Ontology_for_Narrative_and_Fiction"
