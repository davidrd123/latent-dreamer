## 1. Top-line judgment

The right prior-work map is not “narrative AI” in general.

For **L1**, the best fit is **mixed-initiative procedural content generation** and co-creative design tools, because your problem is not “generate lore” but “propose world enrichments against explicit constraints, with legible reasons and human curation.” For **L2**, the right grounding is **computational appraisal plus social simulation**, because that gives you event→concern dynamics and interpersonal structure without pretending generic agent architectures are enough. For **L3**, the right grounding is **drama management and suspense/tension modeling**, because your performance lane is an authored-graph traversal problem, not a full hypothetical-world explorer. For the live conducted dimension, the useful analogs come from **interactive music systems, live coding, and autonomous improvisation**, not from screenwriting manuals. That lines up with your own current boundary discipline: graph-first shipping path, deterministic stage seam, explicit canon/counterfactual split, L2 as the only mature cognitive layer, and L3 as likely a scheduler over authored material rather than “the same engine again.”   

Blunt version: **steal from game AI and interactive music, not from generic story theory or knowledge-graph hype**. BDI, story grammars, GraphRAG, and ontology-heavy narratology are mostly the wrong center of gravity for the next phase. ([Jose M. Vidal][1])

---

## 2. Best bodies of work by architectural layer

### L1 world-building / authoring-time expansion

**1. Sentient Sketchbook + the mixed-initiative PCG line**
**Status:** **Bedrock**
This is the best prior work for your L1 question because it treats the system as a **proposal machine with visible evaluation**, not an oracle. Sentient Sketchbook combines real-time alternative generation, playability/quality analysis, and novelty search so the human can browse, reject, and refine suggestions. In the reported evaluation, the tool was tested with **five industry experts** in a small qualitative study; no strong inferential stats or effect sizes were reported, which is fine because the main thing to steal is the workflow shape, not the evidence strength. Steal now: surfaced alternatives, visible metrics, novelty-first early ideation, metric-guided refinement later, and explicit “this suggestion is different because…” explanations. Ignore for now: game-map-specific metrics and any temptation to treat novelty search as your world-building engine. ([Georgios N. Yannakakis][2])

**2. Tanagra**
**Status:** **Bedrock**
Tanagra matters because it gets the hard part right: the human sets anchors and constraints, the system fills around them while preserving invariants like playability, and if the constraints are impossible it fails loudly instead of hallucinating coherence. That is exactly the shape your L1 needs if it is to be more than “LLM vibes plus vibes-based validation.” Steal now: constraint-backed partial generation, authored anchors, validator-first proposal admission, and “no valid completion exists” as a legitimate output. Ignore for now: platformer-specific geometry vocabulary and rhythm/pacing as level-layout terms. ([ACM Digital Library][3])

**3. MINSTREL / transform-recall-adapt**
**Status:** **Optional input**
MINSTREL is not a full blueprint, but it is still the sharpest old result on **creative recall with transformation**. The useful part is not “rebuild symbolic story generation from the 1990s.” The useful part is: when the system is stuck, do not sample from scratch; retrieve a nearby case, change one structural thing, and test the result. That is a good model for L1 operators like `complicate`, `connect`, and `historicize`, and for dead-end-aware memory. Ignore the larger ambition of end-to-end automated story invention. ([AAAI][4])

**Verdict on L1 overall:** the strongest prior work here helps with **workflow, proposal structure, and constraint discipline**, not with a grand theory of “world-building pressure.” That is a warning. L1 is still a research track, not the mainline.

---

### L2 motivated characters / inner life / concern-driven cognition

**1. Computational appraisal: EMA + OCC, with Scherer as vocabulary support**
**Status:** **Bedrock**
This is the strongest thing to add around Mueller. EMA gives you the right dynamic move: appraisal is not a one-shot labeler but an ongoing process where interpretations, plans, and beliefs keep reappraising the same situation as state changes. OCC gives you a compact eliciting-condition vocabulary and intensity variables that are still useful as schema fields. Scherer helps with appraisal dimensions like relevance, implications for needs/goals, coping potential, and normative significance, but I would use CPM as vocabulary support, not as the main executable architecture. Steal now: event→appraisal→concern update rules, intensity variables, coping/readiness fields, and a typed distinction between harm, threat, blame, relief, obligation, etc. Ignore for now: full emotion taxonomies, physiological component modeling, and any attempt to “do all of appraisal theory.” ([Stacy Marsella][5])

**2. Versu’s social practices**
**Status:** **Bedrock**
Versu solves a problem your current L2 framing only partly addresses: characters do not just have private concerns, they inhabit **recurring social situations with norms, affordances, and role expectations**. Versu’s “social practice” model gives you a way to represent recurring interactional contexts that offer candidates without seizing the agent’s autonomy. Reactive joint plans coordinate a scene; utility-based reactive selection still lets each character decide. That is a clean way to structure situations like confrontation, avoidance, confession, rehearsal, public humiliation, reconciliation. Steal now: social practices as typed recurring situations, affordance menus conditioned on role and norm, and autonomy preserved at action selection. Ignore for now: rebuilding Versu’s full logic stack or trying to imitate its authoring scale. ([UK Computer Science][6])

**3. Comme il Faut / Prom Week**
**Status:** **Optional input, high value**
CiF and Prom Week matter because they treat social state as **reusable, recombinable mechanics**, which reduces authoring burden without flattening characters into utility bots. The load-bearing insight is that social norms and interaction patterns can be encoded once and reused across many situations. That is useful for your benchmark adapters problem too: instead of one-off bridge logic, you want reusable social-state transforms. Steal now: compact social-state variables, reusable interaction schemas, and authoring abstractions that trade bespoke scripting for composable social rules. Ignore for now: the specific teen-drama ontology and player-facing game mechanics. ([AAAI][7])

**Verdict on L2 overall:** add **appraisal for concern detection** and **social-practice modeling for interpersonal structure**. Do **not** add generic BDI as your main enrichment. BDI gives you nouns like belief/desire/intention, but it is weak on emotional appraisal and weak on the socially situated, pressure-driven behavior you actually care about. ([Jose M. Vidal][1])

---

### L3 traversal / directing / sequencing

**1. Drama management: Façade beat sequencing + DODM**
**Status:** **Bedrock**
This is the best prior work for L3, full stop. Façade’s beat sequencer explicitly scores candidate beats against authorial priorities and a desired story/tension arc, then does weighted selection among eligible high-priority candidates. DODM extends that with a feature toolbox for authorial goals like plot mixing, plot homing, and different endings. That is much closer to your actual problem than “story generation” or “directing theory.” Steal now: candidate-next-node preconditions, priority tiers, weighted selection inside tiers, explicit story-value or tension-value effects, and traversal features like “home back toward unresolved line X” or “mix in subplot Y before climax.” Ignore for now: dialogue management, player-conversation assumptions, and any idea that L3 should fork symbolic contexts the way L2 does. ([Baskin School of Engineering][8])

**2. Authorial leverage, plus human-evaluated drama-management results**
**Status:** **Bedrock**
The best thing this literature gives you is not more theory. It gives you a way to ask whether the scheduler is worth the trouble. “Authorial leverage” is the right frame: how much policy complexity are you saving, how much branching burden are you shifting, and how much quality are you getting relative to simpler trigger logic? The Anchorhead/C-DraGer line also matters because it actually evaluated drama management with humans; one study reports **22 participants** and found improved overall experience, especially for inexperienced players, though the open materials I checked do not report clean effect sizes. Steal now: leverage as an evaluation lens, not just outcome quality. Ignore for now: any assumption that your conducted piece needs a player model as rich as interactive fiction. ([AAAI][9])

**3. Suspense/tension modeling: Suspenser + music-tension models like Farbood**
**Status:** **Optional input**
Suspenser is useful because it operationalizes a piece of audience-facing shape: suspense depends on the protagonist’s perceived solution space, and you can score story states against desired suspense levels. Farbood-style musical tension modeling is useful because it gives you a temporal, measurable tension signal for the **music layer**, not because it gives you a full traversal engine. Steal now: explicit audience-facing tension as a modeled variable, plus rate-of-change terms for the music layer. Ignore for now: turning suspense planning into your main L3 engine, or assuming a music-tension model can ground sequence logic by itself. ([AAAI][10])

**Verdict on L3 overall:** treat it as **drama management over an authored graph**, with some tension/suspense heuristics and maybe a few montage-like verbs. Do not pretend it is the same kind of exploratory cognition as L2. Your own docs are already leaning that way, and they are right. 

---

### Evaluation frameworks to steal

**1. Authorial leverage**
This is the right evaluation frame for L3 and for mixed-initiative tooling generally. Ask not only “was the output better?” Ask “did this reduce authoring burden without collapsing quality?” That is the test for whether your traversal controller is doing real work beyond a weighted random walker or benchmark-specific glue. ([AAAI][9])

**2. Expressive range / quality-diversity style evaluation**
You need some version of this for both L1 and L3. Not because “quality-diversity” is trendy, but because you need to know whether the world-builder or scheduler is exploring meaningful different regions rather than producing near-duplicates. The ERaCA framing is useful here: evaluate generated artifacts against target aesthetic/functional dimensions, and inspect the spread, not just average score. Steal the habit of measuring range across axes like tension profile, situation recurrence, event approach patterns, counterfactual density, and musical posture. Ignore any attempt to reduce artistic success to one aggregate scalar. ([Max Kreminski][11])

**3. Workflow telemetry from Wordcraft and CoAuthor, plus CSI for human-side judgment**
Wordcraft is one of the few co-creative writing studies with decent experimental evidence. In a study with **25 hobbyist writers**, Wordcraft increased AI requests, acceptance of suggestions, and proportion of AI-generated text, with reported p-values including **p=0.003**, **p=0.0003**, **p=0.0068**, **p=0.0266**, **p=0.0134**, and **p=0.00668**; effect sizes were not reported in the lines I checked. CoAuthor is weaker as an evaluation theory but excellent as an instrumentation model: **63 writers**, **1,445 sessions**, every interaction logged for replay and analysis. CSI is a reasonable subjective co-creativity instrument if you need one, but telemetry plus watched-output judgment matters more here. ([3D Var][12])

**Practical read:** for your project, evaluation should be a **three-legged stool**:

1. structural range and policy metrics,
2. human curation/workflow telemetry,
3. watched-output judgment.

Anything with only one leg will lie to you.

---

### Representation patterns to appropriate

**1. Assumption-Based Truth Maintenance Systems (ATMS)**
**Status:** **Bedrock**
This is the cleanest off-the-shelf pattern for your `Context` / `assumption_patch` / hypothetical-branch problem. ATMS gives you assumptions, derived consequences, nogoods, cheap context switching, and efficient handling of multiple inconsistent possibilities without flattening them into one state. That is a much better fit than inventing your own context machinery from scratch or pretending a property graph alone solves it. Steal now: explicit assumption tokens, nogood tracking, support sets, and cheap recomputation across alternative branches. ([ResearchGate][13])

**2. Event sourcing**
**Status:** **Bedrock**
Your canon/session story is screaming for event sourcing. Store canonical state changes as an append-only event stream; derive current canon by replay; keep per-session logs as first-class artifacts. This fits your explicit commit discipline and canon/counterfactual split. Steal now: append-only canonical event log, replay, snapshots for performance, and provenance of how a world got here. Ignore any attempt to force hypothetical branches into the same structure; event sourcing is for canon, not for all imagined variants. ([martinfowler.com][14])

**3. Blackboard architecture**
**Status:** **Optional input**
Blackboard systems are useful because they separate **knowledge sources** from **control** and give you an agenda-based arbitration mechanism. That fits your critic/selector instincts and the fact that engine, conductor, narrator/music policies, and validator are not the same thing. Steal now: agenda-based control, multiple specialists writing to shared state, and explicit control policies over contributions. Ignore the idea that blackboard is a total architecture replacement. It is a control/arbitration pattern. ([AAAI Conference Proceedings][15])

---

### Mixed-initiative / co-creative workflow models

**1. Sentient Sketchbook/Tanagra-style interaction**
**Status:** **Bedrock**
This is still the best template: human anchors the design, system offers constrained alternatives, every suggestion is inspectable, and refusal is cheap. The lesson is not “mixed initiative is nice.” The lesson is that **proposal visibility and constraint transparency** are what keep the human from becoming a janitor for stochastic sludge. ([Georgios N. Yannakakis][2])

**2. Wordcraft**
**Status:** **Bedrock for interface lessons, not theory**
Wordcraft’s result is simple: structured co-writing UX beats raw chat for creative assistance. Users asked more, accepted more, and rated it as more helpful/collaborative than weaker baselines. For your project, the translation is obvious: do not surface L1/L2/L3 as a text chat with a giant prompt box. Surface typed proposals, local controls, alternatives, and provenance. ([3D Var][12])

**3. CoAuthor**
**Status:** **Optional input**
CoAuthor is less useful as a UI model than as an instrumentation pattern. It shows how to log every suggestion, revision, acceptance, rejection, and timing event so you can later analyze where the human actually found value. You need that once L1 proposals and L3 traversal interventions exist. ([Computer Science][16])

---

### Adjacent domains relevant to conducted live performance

**1. George Lewis’s Voyager**
**Status:** **Bedrock**
Voyager is the best precedent for the conducted dimension because it frames the machine as a **co-performer with its own agency**, not just a controllable instrument. That is the right abstraction for your engine/conductor relationship. The conductor should shape pressure, pacing, and emphasis, but the system should still have its own traversal logic. Steal now: autonomy negotiation, nonhierarchical interaction, and the refusal to collapse the system into “a fancy controller.” Ignore attempts to literalize free improvisation aesthetics into the core architecture. ([EA Music][17])

**2. TidalCycles / Strudel / live coding pattern systems**
**Status:** **Bedrock**
This matters because live coding has already solved one class of real-time performance control problem: how to make **small gestures produce legible structural changes** under time pressure. Pattern transformations, combinators, and live reparameterization are exactly the kind of conductor verbs your system needs more than low-level overrides. Steal now: pattern-as-control vocabulary, small live edits with large structural consequences, and readable real-time transformations. Ignore the temptation to replace your graph traversal with a full live-coding language. ([Zenodo][18])

**3. Interactive Music Systems (IMS)**
**Status:** **Bedrock**
IMS is useful because it gives you the right five-layer decomposition for the live system: sensing/input, mapping, time engine, output, experience design. Your own IMS note already points at the important part: the art lives in **mapping** and in the playtest loop, not in the hardware stack. It also gives you hard perceptual metrics: audio latency, visual latency, haptic latency, learnability, and “is it fun?” as a legitimate criterion. Steal now: layer separation, latency budgets, fast playtest loops, and body-as-conductor thinking. Ignore for now: hardware expansion and biometrics as primary control. Your own revised architecture already demotes those correctly.    

---

## 3. What to steal conceptually

1. **L1 is mixed-initiative design, not “creative cognition” yet.**
   Use pressure language only where it cashes out as computable deficiency detection plus operator-specific proposals. The rest is curation workflow. That is the honest framing.

2. **L2 needs two additions, not a new religion.**
   Add **appraisal** for concern birth/intensity/update and **social practices** for interpersonal situation structure. That upgrades Mueller without washing it out.

3. **L3 is a drama manager, not a symbolic daydreamer.**
   Use traversal pressures as scoring/evaluation variables over an authored graph. Do not force context forks and assumption patches into places where ranked candidate sequencing is the right model.

4. **Canon and hypotheticals need different storage semantics.**
   Event-sourced canon, ATMS-style hypothetical branches, blackboard-style control. Do not mush them together.

5. **The human should curate structured alternatives, not babysit a chatbot.**
   Every serious mixed-initiative result says the same thing in different words.

6. **For the live medium, music and conductability are first-class, not decoration.**
   The best adjacent work comes from systems that already wrestle with autonomy, timing, and expressive control in performance.

---

## 4. What to steal operationally

This is the part that should change code and docs.

**Now**

* Add an **appraisal pass** to L2 state updates. Event in, appraised consequences out, concern intensities updated.
* Add **social-practice objects**: recurring interaction templates like confrontation, evasion, confession, rehearsal, status repair, public embarrassment.
* Change L3 terminology from “same engine as L2” to **drama-management scheduler** in docs and traces. That alone will reduce abstraction sludge.
* Add **authorial leverage** as an explicit evaluation question for L3: does this controller reduce scripting burden versus benchmark-specific adapters or weighted-random traversal?

**Next**

* Prototype L1 as a **Sentient Sketchbook/Tanagra-style proposal tool**, not as an autonomous world-builder. Human anchors world facts, system suggests pressure-resolving enrichments with visible evaluation.
* Instrument proposal workflow like **CoAuthor**: log acceptance, rejection, edits, time-to-decision, and later reuse.
* Run at least one **Wordcraft-style structured UI test** for authoring proposals rather than defaulting to chat.

**Representation**

* Use **event sourcing** for canon/session mutation history.
* Use **ATMS-style assumption sets** for counterfactual branches and invalidated hypotheses.
* Use a **blackboard/agenda** for critic-selector and conductor/engine arbitration if that arbitration keeps growing.

**Performance**

* Take conductor controls from **pattern transformation / IMS mapping** thinking, not from a flat parameter soup.
* Evaluate live use with IMS metrics: latency, learnability, expressive range, and a blunt “is this actually fun / legible?” pass. 

---

## 5. What to ignore for now

**1. Generic BDI architectures**
Useful nouns. Wrong center of gravity. Too plan/commitment-centric, too weak on appraisal, too weak on social practices. Mostly a distraction right now. ([Jose M. Vidal][1])

**2. Story grammars and beat-sheet lore**
Good for post-hoc description. Weak as operational machinery for your scheduler. They will make the docs sound tidy while leaving the code unchanged. Mostly a distraction. ([ScienceDirect][19])

**3. GraphRAG and generic KG tooling**
Useful for corpus mining and note infrastructure. Not useful as the center of this architecture. They are one-way extraction-heavy and do not solve runtime concern dynamics, traversal scheduling, or live conductability. Mostly a distraction for the core engine. ([Microsoft][20])

**4. Ontology-heavy narratology / DH knowledge graphs**
GOLEM, CIDOC-style extensions, and related ontology work are interesting if you later need provenance-rich scholarship tooling or interoperable fictional-world archives. They are not what your near-term engine is missing. Postpone. 

**5. Full narrative planning / IPOCL as near-term center**
This is tempting because it sounds principled. But your shipping path is authored graph plus traversal, not full plot synthesis. Use drama management now. Revisit narrative planning only if you later decide to generate larger causal plot structures automatically. Postpone. ([Georgia Tech Faculty][21])

**6. End-to-end LLM story generation**
Wrong move. Mueller’s own 2022 update points the other way, and so do your current docs: process and state matter. You already know this. Keep knowing it. 

---

## 6. Short prioritized reading list

| Priority | Work                                                                      | Why this matters here                                                      | What to steal                                                                                                         |
| -------- | ------------------------------------------------------------------------- | -------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| 1        | **Mateas & Stern, Façade / beat sequencing papers**                       | Best grounding for L3 as drama management over authored material           | Candidate-node preconditions, priorities, weighted selection, tension-arc scoring ([Baskin School of Engineering][8]) |
| 2        | **Nelson et al., Declarative Optimization-Based Drama Management (DODM)** | Turns authorial goals into explicit traversal features                     | Plot homing/mixing, authorial goal features, scheduler evaluation knobs ([EIS][22])                                   |
| 3        | **Chen, Nelson, Mateas, Evaluating Authorial Leverage**                   | Best evaluation lens for whether L3 is worth it                            | Policy complexity, leverage vs trigger logic, branching burden metrics ([AAAI][9])                                    |
| 4        | **Marsella & Gratch, EMA**                                                | Best executable upgrade for L2 concern dynamics                            | Continuous appraisal, coping/readiness, event→concern updates ([Stacy Marsella][5])                                   |
| 5        | **Ortony, Clore, Collins / OCC line**                                     | Best compact eliciting-condition schema for L2                             | Concern types, intensity variables, event-based appraisal vocabulary ([IDSIA People][23])                             |
| 6        | **Evans & Short, Versu**                                                  | Best model for socially situated recurring interaction contexts            | Social practices, affordances, autonomy-preserving interaction structure ([UK Computer Science][6])                   |
| 7        | **McCoy et al., Comme il Faut / Prom Week papers**                        | Best reusable social-rule abstraction for authoring character interactions | Social-state variables, reusable interaction schemas, authoring burden reduction ([AAAI][7])                          |
| 8        | **Liapis et al., Sentient Sketchbook**                                    | Best L1 model for proposal+evaluation mixed initiative                     | Suggestion browsing, visible metrics, novelty early / refinement later ([Georgios N. Yannakakis][2])                  |
| 9        | **Smith, Whitehead, Mateas, Tanagra**                                     | Best validator-backed mixed-initiative generation pattern                  | Human anchors + constrained completion + fail-loud invariants ([ResearchGate][24])                                    |
| 10       | **Turner, MINSTREL**                                                      | Best memory-based creativity mechanism still worth stealing                | Transform-recall-adapt, mutation over resampling, case-based serendipity ([AAAI][4])                                  |
| 11       | **Cheong & Young, Suspenser**                                             | Best explicit audience-facing tension/suspense heuristic                   | Suspense scoring for traversal evaluation, not full engine replacement ([AAAI][10])                                   |
| 12       | **Farbood, musical tension model**                                        | Best grounding for music as inner-process modulation                       | Temporal tension features, pacing/music coupling ([BPB][25])                                                          |
| 13       | **de Kleer, ATMS**                                                        | Best representation pattern for hypothetical branches                      | Assumptions, nogoods, support sets, cheap context switching ([ResearchGate][13])                                      |
| 14       | **Fowler, Event Sourcing**                                                | Best representation pattern for canon/session mutation                     | Append-only canon log, replay, snapshots, provenance of change ([martinfowler.com][14])                               |
| 15       | **Yuan et al., Wordcraft**                                                | Best evidence-backed lesson for co-creative authoring UI                   | Structured controls beat raw chat; measure acceptance and usefulness ([3D Var][12])                                   |

### The shortest honest summary

* **Must-read now:** Façade, DODM, Authorial Leverage, EMA, Versu, Sentient Sketchbook, Tanagra, ATMS.
* **Useful after that:** CiF/Prom Week, Suspenser, Farbood, Event Sourcing, Wordcraft.
* **Read later only if the scope changes:** IPOCL, ontology-heavy narratology, GraphRAG/KG tooling, generic BDI.

The high-value move is not to widen the theory cone. It is to **narrow each layer onto the prior work that actually matches its job**.

[1]: https://jmvidal.cse.sc.edu/library/rao91a.pdf "https://jmvidal.cse.sc.edu/library/rao91a.pdf"
[2]: https://yannakakis.net/wp-content/uploads/2013/08/ALiapis_SentientSketchbook.pdf "https://yannakakis.net/wp-content/uploads/2013/08/ALiapis_SentientSketchbook.pdf"
[3]: https://dl.acm.org/doi/pdf/10.1145/1822348.1822376 "https://dl.acm.org/doi/pdf/10.1145/1822348.1822376"
[4]: https://cdn.aaai.org/Symposia/Spring/1993/SS-93-01/SS93-01-021.pdf "https://cdn.aaai.org/Symposia/Spring/1993/SS-93-01/SS93-01-021.pdf"
[5]: https://www.stacymarsella.org/publications/pdf/N_Emcsr_Marsella.pdf "https://www.stacymarsella.org/publications/pdf/N_Emcsr_Marsella.pdf"
[6]: https://cs.uky.edu/~sgware/reading/papers/evans2014versu.pdf "https://cs.uky.edu/~sgware/reading/papers/evans2014versu.pdf"
[7]: https://cdn.aaai.org/ojs/12454/12454-52-15982-1-2-20201228.pdf "https://cdn.aaai.org/ojs/12454/12454-52-15982-1-2-20201228.pdf"
[8]: https://users.soe.ucsc.edu/~michaelm/publications/mateas-gdc2003.pdf "https://users.soe.ucsc.edu/~michaelm/publications/mateas-gdc2003.pdf"
[9]: https://cdn.aaai.org/ojs/12377/12377-52-15905-1-2-20201228.pdf "https://cdn.aaai.org/ojs/12377/12377-52-15905-1-2-20201228.pdf"
[10]: https://cdn.aaai.org/AAAI/2006/AAAI06-331.pdf "https://cdn.aaai.org/AAAI/2006/AAAI06-331.pdf"
[11]: https://mkremins.github.io/publications/ERaCA_HAI-GEN2022.pdf "https://mkremins.github.io/publications/ERaCA_HAI-GEN2022.pdf"
[12]: https://3dvar.com/Yuan2022Wordcraft.pdf "https://3dvar.com/Yuan2022Wordcraft.pdf"
[13]: https://www.researchgate.net/publication/220546361_An_assumption-based_TMS "https://www.researchgate.net/publication/220546361_An_assumption-based_TMS"
[14]: https://martinfowler.com/eaaDev/EventSourcing.html "https://martinfowler.com/eaaDev/EventSourcing.html"
[15]: https://ojs.aaai.org/aimagazine/index.php/aimagazine/article/view/537 "https://ojs.aaai.org/aimagazine/index.php/aimagazine/article/view/537"
[16]: https://www-cs.stanford.edu/~minalee/pdf/chi2022-coauthor.pdf "https://www-cs.stanford.edu/~minalee/pdf/chi2022-coauthor.pdf"
[17]: https://eamusic.dartmouth.edu/~larry/algoCompClass/readings/george%20lewis/lewis.too_many_notes.pdf "https://eamusic.dartmouth.edu/~larry/algoCompClass/readings/george%20lewis/lewis.too_many_notes.pdf"
[18]: https://zenodo.org/record/849841/files/smc_2010_067.pdf "https://zenodo.org/record/849841/files/smc_2010_067.pdf"
[19]: https://www.sciencedirect.com/science/article/pii/0378216682900145 "https://www.sciencedirect.com/science/article/pii/0378216682900145"
[20]: https://www.microsoft.com/en-us/research/blog/graphrag-unlocking-llm-discovery-on-narrative-private-data/ "https://www.microsoft.com/en-us/research/blog/graphrag-unlocking-llm-discovery-on-narrative-private-data/"
[21]: https://faculty.cc.gatech.edu/~riedl/pubs/jair.pdf "https://faculty.cc.gatech.edu/~riedl/pubs/jair.pdf"
[22]: https://eis.ucsc.edu/papers/Nelson_etal_-_DODM_-_IEEE-CGA06.pdf "https://eis.ucsc.edu/papers/Nelson_etal_-_DODM_-_IEEE-CGA06.pdf"
[23]: https://people.idsia.ch/~steunebrink/Publications/KI09_OCC_revisited.pdf "https://people.idsia.ch/~steunebrink/Publications/KI09_OCC_revisited.pdf"
[24]: https://www.researchgate.net/publication/228339816_Tanagra_A_mixed-initiative_level_design_tool "https://www.researchgate.net/publication/228339816_Tanagra_A_mixed-initiative_level_design_tool"
[25]: https://bpb-us-e1.wpmucdn.com/wp.nyu.edu/dist/f/11865/files/2020/08/Farbood_2012_Musical_Tension.pdf?bid=11865 "https://bpb-us-e1.wpmucdn.com/wp.nyu.edu/dist/f/11865/files/2020/08/Farbood_2012_Musical_Tension.pdf?bid=11865"
