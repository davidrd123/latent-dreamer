Bluntly: the Graffito brief is pointed the right way, but the situation schema is one category short$_{85%}$, and the current implementation split matters. Right now Graffito in-repo is still mostly an L3 graph/traversal slice over scenes 3-4, while the clearest kernel benchmark path is still the Puppet-Knows-style scalar situation map. So the next move is not “full Graffito ontology.” It is a narrow bridge: typed Graffito facts for a few situations, projected back down to the current scalar scheduler, with one thing Graffito uniquely needs that the current brief under-specifies: sensorimotor regulation.

Method note. I treated this as a repo-grounding pass plus an outside literature scan. On the empirical side, the most useful anchors were: a 35-study/44-effect-size meta-analysis of stress-arousal and stress-mindset reappraisal interventions (pooled d = 0.23, trim-and-fill d = 0.14), a lab arousal-reappraisal stress experiment (50 recruited participants), a clustered preschool RCT of rhythm-and-movement self-regulation (n = 213), and a five-experiment computational flow paper with large online samples (Exp. 1 N = 365, Exp. 2 N = 249). On the theory side, the useful stack was Scherer, Lazarus/Smith, Gentner’s SME, Fauconnier/Turner, Ryan on possible worlds, ACT-R, Soar, MicroPsi/PSI, GOLEM, and distributed cognition. ([PMC][1]) ([PMC][2]) ([Research Portal][3]) ([PMC][4])

## 1) Appraisal structure

Your 7-category schema covers social appraisal fairly well. It maps onto Scherer and Lazarus like this: recent events and present actors carry novelty/relevance; relationship facts and role/obligation facts carry goal significance and ego-involvement; exposure/surveillance carries threat and shame pressure; artifact-state facts carry some opportunity/constraint information; derived appraisal is the right output layer. Scherer’s CPM asks four broad questions: relevance, implications, coping potential, and normative significance. Lazarus splits appraisal into primary appraisal, what is at stake, and secondary appraisal, can I cope. Primary appraisal yields threat, challenge, or loss; secondary appraisal evaluates coping resources. ([Swiss Medical Weekly][5]) ([PMC][6]) ([Institute for Creative Technologies][7])

What it misses is the bodily side. Graffito needs a dedicated **sensorimotor-regulation** category. Scherer’s simplified SEC list includes intrinsic pleasantness, and your current seven buckets barely represent it. “Forest of legs,” “FELA in the apartment,” tactile graffiti, siren lights, paint texture, and rhythmic sway are not just event facts. They are immediate sensory-appraisal facts. The same goes for coping resources: right now “can I cope?” is only weakly inferable from roles, artifacts, and recent events. For Graffito, coping potential changes when Monk is present, when the practiced routine is available, when Tony has the Sketchbook, when the Can is in a usable phase, and when rhythm has locked in. That deserves its own slot, not an afterthought in derived appraisal. So I would go to **8 categories**, not 7: add `sensorimotor_regulation_facts`. ([Swiss Medical Weekly][5]) ([PMC][6])

The current emotion dimensions are also slightly off. `threat`, `hope`, `anger`, and `grief` are fine as compressed outputs. `waiting` is not an emotion. It is imminence or anticipatory pressure. I would demote it from the emotion vector and treat it as a temporal/planning variable. For Graffito, the missing emotion/appraisal outputs are `shame/reproach`, `admiration/trust`, and `wonder/interest`. OCC’s well-being emotions matter for Tony’s fear, hope, relief, and distress. Attribution emotions matter for Grandma↔Monk and Tony↔Monk: reproach, shame, admiration, pride/remorse. Object-based interest matters for Tony’s relation to the Can, Sketchbook, and Mural more than love/hate does. In other words: Graffito is not only about fear and hope. It is heavily about **moral attribution** and **charged fascination**. ([Institute for Creative Technologies][7]) ([St. John's Seminary][8])

For the “what is wrong with you is exactly what’s right with you” reversal, OCC is not the best handle. The better fit is **Lazarus/Smith coping potential + challenge/threat appraisal + cognitive reappraisal**. Same high arousal, same sensory intensity, different appraisal of resources versus demands. In the challenge-threat model, challenge appears when perceived resources exceed demands; threat when demands exceed resources, and the system can move along that continuum rather than flipping a binary switch. That is almost exactly Tony’s mechanism. The raw sensory trait stays the same. What changes is control, meaning, and resource context. Reappraisal also literally works by changing the semantic meaning of the same stimulus. In the 2024 meta-analysis of stress-arousal/stress-mindset interventions, the overall effect on performance was small (d = 0.23), but public performance tasks were better than cognitive written tasks (d = 0.34 vs. 0.20). That matters here because Tony’s pressure scenes are performance-like, public, high-arousal encounters. ([PMC][9]) ([PMC][10]) ([PMC][1]) ([PMC][1])

So the right design move is: **do not encode “overload” and “power” as separate traits**. Encode one raw sensory condition plus changing appraisal fields:
`load`, `pleasantness`, `entrainment`, `available_control_channels`, `co-regulator_present`, `future_expectancy`, `identity_meaning`.

## 2) Representing layered reality

Classic narrative AI mostly does **not** solve Graffito’s layered reality problem. MINSTREL has cross-domain reminding and transformation, but that is author-side analogy, not runtime layered ontology. Facade has beat management and local drama logic, not mythic register correspondences. Versu has social practices and role affordances, not “same conflict in another register.” The tools that actually help are three different ones with three different jobs: **possible worlds/contexts for truth conditions, structure-mapping for correspondence, and conceptual blending for emergent mixed scenes**. Possible worlds give you separate local truth domains. SME-style structure mapping gives you relational correspondences. Conceptual blending gives you emergent compressed scenes from multiple input spaces. ([Luis Navarrete][11]) ([Northwestern Psychology Groups][12]) ([Northwestern Psychology Groups][12]) ([TECFA][13])

That means Motherload and Grandma should **not** be the same entity ID. Separate-entities-with-correspondence is right$_{90%}$. If you collapse them to one entity, you destroy layer-local truth conditions, action affordances, and belief updates. Grandma can carry bowls and challenge Monk in the apartment. Motherload can seize the Can and speak as a dragon in the magic register. Those are not the same local causal role. What you want is an explicit correspondence edge like `:avatar_of` or `:register_echo_of`, plus a list of preserved tensions:
`care`, `boundary`, `crooked wisdom`, `protection through challenge`.
Same for `castle ↔ school`, `Can ↔ Tony’s sensory capacity`, `Elephant ↔ attachment/regulation`. Graffito needs authored correspondences, not free analogy search. The free search can come later. ([TECFA][13]) ([Luis Navarrete][11])

Also, do not confuse **context sprouting** with **register correspondence**. Your current `context.clj` machinery is for hypothetical branches and backtracking. It is a good fit for reversal, rehearsal, counterfactual memory, and other Mueller-style vertical branches. It is not the right representation for “Grandma appears as Motherload in another register.” That is not an alternative factual branch. It is a patterned correspondence across representational layers. Keep those orthogonal.

I would make the registers explicit:

* `:baseline` = directly perceived family/street/apartment reality
* `:mythic` = narrated legend frame, mostly schema, not fully simulated v1 world-state
* `:magic` = enacted transformed register

For v1, I would **not** simulate the myth as a full causal world. Treat it as a legend schema that seeds mappings into the magic register. That gives you the three layers structurally without tripling the state machinery.

## 3) Sensory state and regulation

Tony needs a regulation machine, not an arousal scalar.

ACT-R and Soar help only a little. ACT-R gives you bottlenecks: buffers as module interfaces, one chunk per buffer, visual overload problems, and jamming when too many operations compete for the same buffer. So it is good for “too much input,” attention narrowing, and serial bottlenecks. Soar gives you a broad working-memory/action-selection loop with operator proposal/evaluation over situational awareness. Good for global control. Bad for Tony’s qualitative mode shifts. Neither gives you a built-in notion of “overwhelmed → entraining → flowing → creating.” ([UCSC People][14]) ([ACT-R][15]) ([arXiv][16]) ([arXiv][16])

MicroPsi/PSI is closer. It uses cognitive modulators like **resolution level**, **selection threshold**, and activation. High resolution means more detail; low resolution means faster, rougher responses. Selection threshold narrows attention and suppresses oscillation between motives. That is much closer to Tony’s problem. You can model overload as high sensory load + low effective control + unstable resolution/selection, and flow as high load + higher control + stabilized selection + strong entrainment. ([AGI-26 Conference][17])

The better conceptual overlay is active inference / interoceptive inference plus rhythmic entrainment. Seth and Friston’s framing is that bodily states are regulated by descending predictions; action helps reduce prediction error by changing the sensory stream. That maps cleanly onto “give your mind to your body and let it flow.” Rhythm is not decoration there. It is an action policy that makes chaotic input more predictable and more usable. Empirically, the support is not magic, but it is real: the RAMSR clustered RCT used 213 preschoolers over 8 weeks, found significant improvement in teacher-reported self-regulation (p < 0.001, ηp² = 0.076), school readiness (p = 0.038, ηp² = 0.087), and in sensitivity analysis some inhibition/self-regulation subscales, while the overall EF composite at follow-up was null (p = 0.844). So rhythm-and-movement is plausible as a regulation resource, but you should not oversell it as a total executive-function cure. ([PMC][18]) ([ScienceDirect][19]) ([Research Portal][3]) ([Research Portal][3])

Flow theory also points the same way. The challenge-skill meta-analysis found a moderate relation, but flow also depends on clear goals and sense of control. The more interesting result for you is the 2022 computational theory of flow: it treats flow less as static challenge-skill balance and more as a function of controllability/information dynamics; the paper ran five experiments, with N = 365 in Exp. 1 and N = 249 in Exp. 2, powered around r ≈ .2. For Tony, that is better than stock Csikszentmihalyi: flow is the moment when the sensory world becomes informationally tractable enough to act through. ([ResearchGate][20]) ([PMC][4]) ([PMC][4])

So I would add a first-class state like:

`regulation_mode ∈ {overloaded, bracing, co_regulating, entraining, flowing, creating, depleted}`

with companion variables:
`sensory_load`, `attentional_aperture`, `cue_coherence`, `motor_entrainment`, `perceived_control`, `co_regulator`, `expressive_channel_open`.

That is the real Graffito state machine.

## 4) Object state and symbolic agency

Here the clean move is: **objects are stateful cognitive couplers**.

Narrative ontologies like GOLEM already go beyond inventory. GOLEM models fictional entities, events, and objects, and explicitly includes character-object `uses` relations and object-in-event relations. That is useful, but it still only gets you partway there. Graffito needs object state progression plus person-object coupling semantics. ([MDPI][21]) ([Golem Ontology][22]) ([Golem Ontology][22])

Distributed cognition is the right formal language for the Sketchbook and Can. In that literature, a person using cognitive artifacts forms a single cognitive functional system; the artifact changes the memory and control demands of the task. That is exactly what the Sketchbook does: it turns overwhelming 3D street input into manageable 2D structure. The Can is similar, except it externalizes not memory but expressive control. Elephant fits the transitional-object literature more closely. In a 2024 adult object-attachment study, touching the attachment object during recovery improved physiological regulation, even though self-report emotion-regulation differences were not found. So Elephant as attachment/regulation object is a defensible model, but again, the effect is about stress recovery and coupling, not about generic “object agency.” ([Lawrence University Faculty][23]) ([PMC][24]) ([PMC][24])

So yes, add **person-object relations**. I would give them their own typed space:

* `attachment`
* `trust`
* `attunement/skill`
* `lineage/inheritance`
* `permission/legitimacy`
* `regulation_function`
* `activation_condition`
* `symbolic_correspondence`

And keep a separate object-state record:

* `phase`
* `available_affordances`
* `required_regulation_mode`
* `failure_mode`
* `externalizes`
* `register`

Applied to Graffito:

* **Can**: `phase = inert | unstable | worldmaking`; `externalizes = Tony.sensory_capacity`; activation requires `regulation_mode ∈ {entraining, flowing, creating}`.
* **Sketchbook**: `externalizes = dimensionality_reduction / self-soothing`; lowers sensory-load cost and increases cue coherence.
* **Elephant**: baseline object with attachment value; later, in magic register, counterpart guide-agent. Do not make it a guide-agent in v1 baseline simulation.
* **Mural**: `surface | threshold_surface | portal_surface`, conditional on Can phase + regulation state + threat pressure.

The nit-picky point: do not literally give the Can “agency” in the ontology. Agency comes from the Tony+Can coupling. Elephant is the exception, because it transitions from attachment object to counterpart agent across registers.

## 5) What the first Graffito kernel run should look like

Do **not** start with all 7 situations. Start with **3**:

1. street overload
2. apartment co-regulation / mission dispute
3. mural crisis / threshold

That is the smallest set that actually tests the story’s mechanism. Scenes 3-4 alone are not enough, because they never force the Can’s state transition. The full 7-situation scaffold is too much while the memory ecology is still moving. Street + apartment + mural is the right first benchmark$_{85%}$. Keep the current graph fixtures and `graffito_pilot.py` heuristics as L3 traversal priors. They are already doing useful work around trajectory fit, preparation, event homing, structural tension, and overdetermination. Just stop pretending that those heuristics are an L2 situation model. The typed facts should feed the scheduler; the pilot heuristics can still shape traversal.

How many facts? Not 5. Not 50. I’d aim for **15 to 25 active facts per situation**, plus a handful of persistent relation/object facts shared across situations. Under that, the appraisals stay mushy. Above that, you’ll drown in authoring before you know what matters. For Graffito, the minimum active bundle per situation should cover:

* present actors
* relation/attachment facts
* obligation/identity-stake facts
* surveillance/exposure facts
* artifact states
* recent events
* sensorimotor-regulation facts
* derived appraisal

And yes, I would include **rehearsal** in the first run even though your question highlights rationalization, reversal, and roving. Omitting rehearsal would be a mistake. Graffito’s mechanism is learned embodied control. Mime-spraying and practiced routine are not side material.

What should “psychologically interesting” output look like? Not prettier family selection. The test is whether the **same raw sensory-load fact** gets re-appraised differently over time. Example trace:

* street overload triggers threat + low coping potential, leading to reversal or roving;
* apartment scene raises co-regulation and introduces a rationalization about crookedness or mission;
* rehearsal of the body routine raises perceived control before any canon event fires;
* mural crisis reuses the same overload cues, but the operator now sees challenge/possible agency rather than only threat.

If the kernel cannot make that flip legible, it is not modeling Graffito. It is just doing weighted scene hopping.

On sequencing: yes, there is real over-engineering risk. The promotion/frontier/anti-residue membrane is still unsettled in your repo. Do not disappear into a 7-situation symbolic cathedral while use/outcome semantics are still shaky. But proving the membrane only on Puppet Knows/Arctic and then “later” moving to Graffito is also a trap, because Graffito’s hardest requirements are special: regulation state, layered correspondences, and person-object coupling. My advice is parallel:

* keep hammering membrane semantics on the simpler fixtures;
* build one **minimal Graffito soak** in parallel with 3 situations and the added regulation/object schema.

That is the shortest route to seeing whether the schema actually buys you anything.

### Prescription

1. Add one missing category: `sensorimotor_regulation_facts`.
2. Demote `waiting` from emotion to `imminence`.
3. Keep layered correspondences separate from hypothetical contexts.
4. Model Motherload/Grandma and castle/school as counterpart entities, not same IDs.
5. Add `person_object_relations`.
6. Start with street/apartment/mural, not the full 7-situation arc.
7. Treat the myth layer as schema in v1, not a fully simulated third world.
8. Make the success criterion explicit: same sensory fact, changed appraisal, changed operator choice.

That’s the buildable Graffito kernel brief.

Look away for ten seconds. Unclench your shoulders.

[1]: https://pmc.ncbi.nlm.nih.gov/articles/PMC10994935/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC10994935/"
[2]: https://pmc.ncbi.nlm.nih.gov/articles/PMC3410434/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC3410434/"
[3]: https://research.usc.edu.au/view/pdfCoverPage?download=true&filePid=13270950290002621&instCode=61USC_INST "https://research.usc.edu.au/view/pdfCoverPage?download=true&filePid=13270950290002621&instCode=61USC_INST"
[4]: https://pmc.ncbi.nlm.nih.gov/articles/PMC9042870/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC9042870/"
[5]: https://smw.ch/index.php/smw/article/download/1687/2255 "https://smw.ch/index.php/smw/article/download/1687/2255"
[6]: https://pmc.ncbi.nlm.nih.gov/articles/PMC3110961/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC3110961/"
[7]: https://people.ict.usc.edu/~gratch/CSCI534/Readings/Smith%26Lazarus90.pdf "https://people.ict.usc.edu/~gratch/CSCI534/Readings/Smith%26Lazarus90.pdf"
[8]: https://stjohnsem.edu/perceptual-appraisal-and-cognitive-theories "https://stjohnsem.edu/perceptual-appraisal-and-cognitive-theories"
[9]: https://pmc.ncbi.nlm.nih.gov/articles/PMC6550483/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC6550483/"
[10]: https://pmc.ncbi.nlm.nih.gov/articles/PMC4193464/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC4193464/"
[11]: https://luisnavarrete.com/USC/pdf/Marie_Laure_Ryan.pdf "https://luisnavarrete.com/USC/pdf/Marie_Laure_Ryan.pdf"
[12]: https://groups.psych.northwestern.edu/gentner/papers/FalkenhainerForbusGentner89.pdf "https://groups.psych.northwestern.edu/gentner/papers/FalkenhainerForbusGentner89.pdf"
[13]: https://tecfa.unige.ch/tecfa/maltt/cofor-1/textes/Fauconnier-Turner03.pdf "https://tecfa.unige.ch/tecfa/maltt/cofor-1/textes/Fauconnier-Turner03.pdf"
[14]: https://people.ucsc.edu/~abrsvn/Intro_to_Python_ACT-R.pdf "https://people.ucsc.edu/~abrsvn/Intro_to_Python_ACT-R.pdf"
[15]: https://act-r.psy.cmu.edu/wordpress/wp-content/uploads/2023/07/Hough_Challenges_and_Strategies_for_Extending_ACT-R_Visual_Perception_2023.pdf "https://act-r.psy.cmu.edu/wordpress/wp-content/uploads/2023/07/Hough_Challenges_and_Strategies_for_Extending_ACT-R_Visual_Perception_2023.pdf"
[16]: https://arxiv.org/pdf/2205.03854 "https://arxiv.org/pdf/2205.03854"
[17]: https://agi-conf.org/2015/wp-content/uploads/2015/07/agi15_bach.pdf "https://agi-conf.org/2015/wp-content/uploads/2015/07/agi15_bach.pdf"
[18]: https://pmc.ncbi.nlm.nih.gov/articles/PMC5062097/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC5062097/"
[19]: https://www.sciencedirect.com/science/article/pii/S0885200623000704 "https://www.sciencedirect.com/science/article/pii/S0885200623000704"
[20]: https://www.researchgate.net/publication/267450034_The_challenge-skill_balance_and_antecedents_of_flow_A_meta-analytic_investigation "https://www.researchgate.net/publication/267450034_The_challenge-skill_balance_and_antecedents_of_flow_A_meta-analytic_investigation"
[21]: https://www.mdpi.com/2076-0787/14/10/193 "https://www.mdpi.com/2076-0787/14/10/193"
[22]: https://ontology.golemlab.eu/ "https://ontology.golemlab.eu/"
[23]: https://faculty.lawrence.edu/wp-content/uploads/sites/18/2015/11/DCog.pdf "https://faculty.lawrence.edu/wp-content/uploads/sites/18/2015/11/DCog.pdf"
[24]: https://pmc.ncbi.nlm.nih.gov/articles/PMC11720695/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC11720695/"
