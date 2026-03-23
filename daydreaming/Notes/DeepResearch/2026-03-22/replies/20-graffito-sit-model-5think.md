You’re asking two linked things: what the Graffito ontology should be, and what order of operations makes sense given the kernel’s actual state.

My view:

1. The 7-category Graffito brief is mostly right.
2. It is missing two categories that matter a lot here: **body/regulation state** and **cross-layer correspondence**.
3. You do **not** need a third cognitive layer for myth/magic. Mueller’s two-layer split is enough if situations carry layer/register metadata and typed counterpart links.
4. The first Graffito kernel run should be **small and psychologically sharp**, not world-complete.

Right now, your implemented Graffito work is still a scenes-3-4 graph/trace slice plus traversal pilot, while the clearest kernel benchmark path still reasons from scalar gauges like activation, ripeness, threat, hope, anger, grief, and waiting. So the useful move is not “invent the full Graffito ontology.” It is “add the minimum facts that let appraisal, reappraisal, and object-state changes stop being fake.”

## 1. Appraisal structure

Scherer’s CPM breaks appraisal into **relevance**, **implications**, **coping potential**, and **normative significance**, with subchecks like novelty, pleasantness, goal relevance, causal attribution, goal conduciveness, urgency, controllability, power/resources, and internal/external standards. Lazarus’s primary appraisal centers on **goal relevance**, **goal congruence/incongruence**, and **ego-involvement**; secondary appraisal adds coping and accountability questions. ([Penn State University][1])

That means your 7-category brief already covers a lot:

* present actors, relationship facts, role/obligation facts, and recent events help with **goal relevance**, **ego-involvement**, and **normative significance**
* exposure/surveillance helps with **threat**, **shame**, and social self-appraisal
* artifact-state helps with **coping potential**
* derived appraisal summary is the right place for scheduler-facing summaries

But it misses five things that should be explicit:

**First, novelty/expectedness.** “Recent event facts” is not enough. Tony’s near-miss and later cop-light pressure matter partly because they are sudden violations of prediction.

**Second, attribution.** Graffito cares a lot about who is responsible: Monk left, Grandma judges, cops converge, Tony blames himself. That is Lazarus/OCC territory, not just threat.

**Third, controllability and felt agency.** For Tony this is the hinge. Before flow: low control. During entrainment: rising control. After the can stabilizes: control high.

**Fourth, self-compatibility / norm compatibility.** Grandma’s question to Monk is exactly this category: is this mission real care, or ego? Scherer treats norm significance as a late appraisal stage for a reason. ([Penn State University][1])

**Fifth, bodily regulation state.** More on that below, but without it the whole “liability becomes asset” arc collapses into generic threat.

So I would revise the Graffito schema to:

* present actors
* relationship facts
* role/obligation facts
* exposure/observation facts
* artifact-state facts
* recent-event facts
* **body/regulation facts**
* **cross-layer correspondence facts**
* derived appraisal summary

That is the right shape$_{85%}$.

### What emotions matter most

OCC’s big split is event emotions, agent-evaluation emotions, and object emotions. For Graffito, the important ones are not the whole 22-type catalog. The live subset is:

* **fear / hope / relief / disappointment** for Tony’s immediate situation
* **admiration / reproach / shame / pride** for Monk and Grandma, and Tony’s self-read
* **anger / gratitude / remorse** as compound social emotions

That is much better than a flat set of threat/hope/anger/grief/waiting. In particular, **waiting is not an emotion**. It is a scheduler or readiness variable. Keep it, but do not pretend it sits at the same level as fear or shame. OCC also helps because Monk/Grandma material is heavy on **agent appraisal**, not just outcome appraisal. ([PMC][2])

### The “what is wrong with you is what is right with you” reversal

This is not a new stimulus. It is an **appraisal shift** over the same stimulus. Reappraisal research is useful here. Uusberg et al. frame reappraisal as changes in construal or in the goals used to evaluate the construal; in one 2023 study of 437 participants, 19% to 49% of changes in emotion were statistically mediated by shifts along appraisal dimensions. Lazarus’s threat-versus-challenge distinction also matters: the same event can be read as impending harm or as mastery opportunity depending on coping/readiness. ([PubMed][3])

For Graffito, the reversal should therefore be modeled as:

* sensory intensity stays high
* novelty may stay high
* social exposure may stay high
* **felt agency rises**
* **coping potential rises**
* **self-compatibility flips**
* **challenge appraisal replaces pure threat**

That gives you a principled route from overload to generative power.

## 2. Layered reality: baseline, myth, magic

Narrative AI does not hand you a turnkey model for this.

MINSTREL gives you cross-domain reminding and transform-recall-adapt, which is useful for authoring-time analogy, but not for runtime layered ontology. Versu models concurrent social practices inside one world. Sabre models author goals, agent intentions, wrong beliefs, and explanation constraints inside one world. None of them really do “same conflict in another register” as a first-class runtime object. ([AAAI][4])

The best tools here are from analogy and blending, not from interactive drama systems:

* **Structure-mapping** says analogies work by mapping **relations**, not surface attributes, and prefers systematic relational bundles over isolated traits.
* **Conceptual blending** gives you linked input spaces and emergent blended structure.
* **Material anchors** explain how physical objects stabilize those blends. ([Northwestern Scholars][5])

That is almost a direct description of Graffito:

* school ↔ castle
* Grandma ↔ Motherload
* sketchbook ↔ world-simplification device
* mural ↔ portal surface
* can ↔ embodied agency externalized

### Should Motherload and Grandma be the same entity?

No. They should be **distinct entities with a typed correspondence relation**.

Make them separate, linked by something like:

* counterpart-of
* avatar-of
* reframe-force-of
* shares-role-pattern-with

Same answer for school and castle.

Why separate is better:

* the two layers have different causal affordances
* you want one to be observable inside one register without collapsing the other
* return transitions need a mapping, not identity collapse
* Tony can misrecognize, partially recognize, or later integrate the relation

If you model them as the exact same entity, you lose too much structure. If you model them as unrelated, you lose the whole point. Distinct-plus-correspondence seems right$_{90%}$.

### Do you need a third Mueller layer?

Probably not.

Mueller’s personal-goal/daydreaming-goal split already handles “real situation” versus “imagined processing of situation.” Graffito’s myth/magic material looks like a **register of daydream content**, not a third control stratum. So I would keep the two-layer cognitive architecture and add two orthogonal fields:

* `reality_register`: baseline | myth | magic
* `counterpart_links`: typed mappings across registers

If later you need authoring-time myth compilation, that is a separate pipeline concern. It does not need to become a third runtime mind layer.

## 3. Sensory state and regulation

This is the place where your current flat pressure map is most obviously wrong.

ACT-R and Soar are useful only up to a point here. ACT-R gives you a crisp bottleneck story: buffers are module interfaces, and a buffer holds one chunk at a time. So ACT-R is good for modeling limited throughput and serial access pressure. Soar explicitly includes an appraisal-based emotion model, but neither architecture gives you a ready-made qualitative state machine for **overwhelmed → regulating → flowing → creating**. 

So do not wait for an off-the-shelf architecture answer. Build the state you need.

### The right simplification

I would not model Tony with “arousal.” That is too blunt. I would use four variables:

* **sensory load**
* **precision control / contextual filtering**
* **rhythmic entrainment**
* **felt agency**

The active-inference literature on autism is mixed, but it is useful here as a modeling hint: a 2023 computational paper argued that the better explanation was not generic volatility or uniformly high prediction-error precision, but **context-sensitive precision adjustment**. That gives you a nice abstraction for Tony without overclaiming clinical fidelity. ([PMC][6])

Then add a coarse phase label:

* overloaded
* bracing
* entraining
* flowing
* generative

This is simple, testable, and much closer to the script.

### Why rhythm and movement need to be causal, not decorative

Sensorimotor synchronization research defines SMS as aligning body rhythms to external rhythmic stimuli and treats it as part of adaptation and performance. Recent rhythm studies also connect entrainment to subjective emotion and to oscillatory mechanisms involved in regulation; a 2024 DMT review argues movement-based therapy works through mind-body coupling and emotion regulation, but note that the evidence base there is still thin, with only seven included studies. ([PMC][7])

So for Graffito:

* movement should modify **entrainment**
* entrainment should modify **precision control**
* precision control should modify **felt agency**
* felt agency should unlock can-state transitions

That is the actual mechanism.

### Flow

The 2022 computational theory of flow is useful because it is sharper than the folk challenge-skill story. It models flow in terms of **mutual information between means and desired ends**. That maps surprisingly well here. Tony’s “flow” is the moment when movement stops feeling like noise and starts having legible consequence in the world. ([PMC][8])

So I would not encode flow as “challenge ≈ skill.” I would encode it as:

* higher predictability of action → effect
* reduced conflict between intention and execution
* rising confidence that embodied action can transform the scene

That is much closer to the brief.

## 4. Objects and symbolic agency

Narrative AI has no standard formalism for “symbolic agency.” It has pieces.

Narrative planners such as IPOCL and Sabre are good at **state progression** through actions and effects. Versu is good at **affordances**, meaning what the current practice makes possible. Material-anchor theory is good at explaining why an object can stabilize a way of thinking or feeling. Put those together. ([auld.aaai.org][9])

So yes, add **person-object relations** as first-class structure. Absolutely.

Each Graffito object should have three kinds of state:

**1. Capability state**
What it can do now.

* Can: inert → unstable → responsive → world-making
* Mural: surface → latent portal → open portal
* Elephant: comfort object → active companion → guide

**2. Relational state**
How a character stands with it.

* trust
* inheritance
* dependence
* mastery
* fear
* identification

**3. Symbolic-anchor state**
What internal process it stabilizes.

* Can = embodied agency / sensory transformation
* Sketchbook = compression / regulation / 3D→2D legibility
* Elephant = co-regulation / safety / continuity of self
* Mural = boundary-crossing surface

That last one is not standard planning machinery. It is where Hutchins helps. The object is a **material anchor** for a cognitive transformation. ([ScienceDirect][10])

## 5. What the first Graffito kernel run should look like

Not all seven situations.

Do **four**:

1. Street overload
2. Apartment co-creation + Grandma’s challenge
3. Mural crisis
4. Motherload reframe

That gives you the minimum set needed for:

* reversal
* rehearsal
* rationalization
* roving

Space/emergence and final return can wait. Bedtime/legend transfer can be compressed into retrieved material rather than modeled as a full situation in v1.

### How many facts?

Five is too few. Fifty per situation is sludge.

I’d aim for **15 to 25 typed facts per situation**, plus **4 to 8 derived appraisal variables per active character**.

Roughly:

* 3 to 5 actor-presence / observation facts
* 4 to 6 relation / obligation facts
* 2 to 4 exposure / surveillance facts
* 3 to 5 artifact and person-object facts
* 2 to 4 recent-event facts
* 3 to 4 body/regulation facts
* 2 to 3 cross-layer correspondence facts

That is enough to produce actual reasoning without drowning the kernel.

### What “psychologically interesting” output should look like

Not “family = rationalization, therefore emit calm node.”

It should look like:

* **Rehearsal**: Tony practices alignment with Monk’s movement, not generic future planning. The branch should change readiness or felt agency even if no external event changes.
* **Reversal**: Tony imagines the near-miss or cop-light moment going differently, perhaps with Monk present earlier or Tony sensing the forward path instead of lateral scan.
* **Rationalization**: the system reframes crookedness from defect to distinctive power, or Monk’s mission from public ego to inherited calling, and then tests whether that actually lowers pressure.
* **Roving**: fantasy material appears, but it preserves unresolved real structure. Motherload-world should still carry Grandma/Monk/Tony tensions, not become random whimsy.

If the run cannot do those four things, the ontology is still too thin.

## Sequencing: semantics versus membrane

Do not fully Graffito-ize the kernel before the membrane settles.

Your promotion/frontier/anti-residue work is still active. So I would **not** build a full seven-situation Graffito knowledge model on top of a memory ecology that still risks confusing retrieval with use or frontier state with actual operative gating.

But I also would not wait for perfect membrane closure before touching Graffito.

Best sequence:

* prove minimal membrane behavior on a simpler fixture or a stripped Graffito subset
* run a **thin Graffito 4-situation benchmark**
* only then widen to all seven situations and richer mythic propagation

That sequencing seems right$_{80%}$.

## Bottom line

The right first Graffito situation model is:

* **fact-based, not scalar-first**
* **character-appraisal-centered**
* **body-state-aware**
* **cross-layer by correspondence, not identity**
* **object-rich in relational and symbolic terms**
* **small enough to test**

The 7-category brief already points the right way. Add **body/regulation facts** and **cross-layer correspondence facts**, make appraisal explicit, and keep the first run narrow.

That gets you from “family selection from pressure numbers” to something that can actually think about Tony, Monk, Grandma, the Can, and the shift from overwhelm to agency.

[1]: https://psu.pb.unizin.org/psych425/chapter/component-process-model-cpm/ "Component Process Model (CPM; Scherer, 2001) – Psychology of Human Emotion: An Open Access Textbook"
[2]: https://pmc.ncbi.nlm.nih.gov/articles/PMC4243519/?utm_source=chatgpt.com "Psychological Construction in the OCC Model of Emotion - PMC"
[3]: https://pubmed.ncbi.nlm.nih.gov/37161355/?utm_source=chatgpt.com "Reappraising reappraisal: an expanded view - PubMed"
[4]: https://cdn.aaai.org/Symposia/Spring/1993/SS-93-01/SS93-01-021.pdf "A Case-based Model of Creativity"
[5]: https://www.scholars.northwestern.edu/en/publications/structure-mapping-a-theoretical-framework-for-analogy/?utm_source=chatgpt.com "Structure-mapping: A theoretical framework for analogy - Northwestern Scholars"
[6]: https://pmc.ncbi.nlm.nih.gov/articles/PMC10529610/ "
            Testing predictive coding theories of autism spectrum disorder using models of active inference - PMC
        "
[7]: https://pmc.ncbi.nlm.nih.gov/articles/PMC11486764/ "
            The effect of rhythmic stimuli with spatial information on sensorimotor synchronization: an EEG and EMG study - PMC
        "
[8]: https://pmc.ncbi.nlm.nih.gov/articles/PMC9042870/ "
            A computational theory of the subjective experience of flow - PMC
        "
[9]: https://auld.aaai.org/Library/JAIR/Vol39/jair39-005.php?utm_source=chatgpt.com "Narrative Planning: Balancing Plot and Character"
[10]: https://www.sciencedirect.com/science/article/pii/S0378216605000597?utm_source=chatgpt.com "Material anchors for conceptual blends - ScienceDirect"
