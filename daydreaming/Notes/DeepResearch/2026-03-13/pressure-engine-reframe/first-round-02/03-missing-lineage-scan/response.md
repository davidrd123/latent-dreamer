# Response

## Findings

* The notes are already strong on **within-level** lineage. The thin spots are elsewhere: what the graph seam *is*, how knowledge change is represented, what counts as an event boundary, and whether the conductor is a playable instrument rather than a bag of controls.
* None of the missing lineages below argue for collapsing `L1`/`L2`/`L3`. Three mostly **sharpen** the current split. One could change `L3` experiment design immediately. One could change later build order around conductor work.
* The two highest-leverage additions seem to me: **claim/provenance atoms at the graph seam**$*{85%}$ and a **storylet/QBN comparison arm for `L3`**$*{80%}$.

## Candidate lineages

### 1. Claim-centered discourse graphs, nanopublications, and contestable argumentation

* **Core mechanism or idea:** make the graph’s atomic unit a typed **claim** with explicit evidence / support / opposition links and provenance, rather than loose tags or summaries. Let prose and graph be two views over the same object.
* **Bears on:** graph/interface seam, dashboard/surfacing, and `L1`.
* **Supports / sharpens / threatens:** **sharpens** the current direction.
* **Read priority:** **must-read now**.
* **Concrete question it helps answer:** *What exactly is the minimal object that `L1`/`L2` write and `L3`/the dashboard read: a tag bundle, or a typed claim with provenance and justification?*

Why it matters relative to your notes: the canonical architecture says the **graph is the membrane** and that the **dashboard is a primary output**, but the current seam is still mostly refs, tags, and summaries. That is too loose. Chan’s discourse-graph program argues for local, shareable graphs of claims, evidence, and rhetorical relations; nanopublications make assertion and provenance separate graph objects; Graphologue shows a practical text↔diagram interaction loop; contestability work argues explanations should be revisable, not static. That combination would make the seam inspectable without collapsing ontologies across levels. Tags are not enough. ([arXiv][1])

### 2. Storylets / quality-based narrative systems

* **Core mechanism or idea:** authored, modular narrative units with **preconditions**, **content**, and **effects**, selected by changing world state or “qualities.”
* **Bears on:** `L3`, graph/interface seam, and some `L1` authoring.
* **Supports / sharpens / threatens:** **sharpens** the current direction, but also **mildly threatens** it by offering a simpler competitor to the current `L3` framing.
* **Read priority:** **must-read now**.
* **Concrete question it helps answer:** *Are Graffito and City Routes really storylet libraries with better scoring, and if so, what extra machinery beyond storylet gating actually earns its keep?*

This is the most immediate missing comparison class for `L3`. Your current scheduler vocabulary, availability tests, priority tiers, event approach, and authored-node traversal already sit very close to storylet/QBN design. Failbetter’s QBN defines storylets as little bundles of interactive story whose availability is controlled by changeable state; Kreminski’s survey maps the design space; Drama Llama shows a hybrid storylet + LLM architecture with natural-language triggers. The evidence for Drama Llama is still small, a six-author preliminary study, so do not overread it. But architecturally it is exactly the question your current notes are not asking: maybe the right baseline is not only weighted-random vs Façade, but **weighted-random vs storylet/QBN vs Façade**. ([Failbetter Games][2])

### 3. Dynamic epistemic logic and epistemic planning

* **Core mechanism or idea:** represent actions/events as changing not only world facts but also **who knows what**, **who believes what**, and what each agent considers possible.
* **Bears on:** `L2`, graph/interface seam, validator/world-state services.
* **Supports / sharpens / threatens:** **sharpens** the current direction, but can **threaten** it by exploding authoring/state burden if pulled in too early.
* **Read priority:** **later**.
* **Concrete question it helps answer:** *When an event commits, what changes in private knowledge, misinformation, secrecy, and nested beliefs, not just in ontic state?*

Your notes already care about fact visibility, knowledge gates, hypothetical contexts, and counterfactual traces, but they do not yet have a real semantics for knowledge change. DEL/epistemic planning is the right lineage for confession, concealment, misunderstanding, bluffing, and “A knows that B doesn’t know.” The good news is that it fits the problem directly. The bad news is that the Elsinore case study is a warning label: belief-update bugs were real, DEL was a plausible fit, and it still did **not** significantly reduce authoring burden. Read it because it tells you both what you gain and why it should stay off the critical path until the worlds actually need it. ([arXiv][3])

### 4. Event Segmentation Theory and event cognition

* **Core mechanism or idea:** people parse continuous activity into discrete events; boundaries are driven by prediction error / context change and strongly shape memory and retrieval.
* **Bears on:** `L3`, dashboard/surfacing, graph authoring, and parts of `L2` reminding.
* **Supports / sharpens / threatens:** **sharpens** the current direction.
* **Read priority:** **later**.
* **Concrete question it helps answer:** *What makes one node a node? What should trigger a branch marker, a paragraph break, recall, or event-commit eligibility?*

Right now the notes assume nodes, dwell, event proximity, recall value, branch shifts, and narration breaks, but they do not yet say what a boundary *is*. Event Segmentation Theory says segmentation is an ongoing part of perception and that it scaffolds later memory and learning; later reviews connect boundaries to working-memory updates, prediction error, and contextual change. That matters because if node granularity is arbitrary, then `event_homing`, `recall_value`, dashboard paragraphing, and even “event commit” are partly arbitrary too. I would not block Graffito on this, but I would not author City Routes or overdesign the visualizer without it. ([PMC][4])

### 5. NIME / IMS mapping, appropriation, and instrument-design research

* **Core mechanism or idea:** the **mapping layer is the instrument**. Expressivity comes from learnable mappings, good latency, appropriability, and iteration with players, not from maximizing control dimensions.
* **Bears on:** dashboard/surfacing, conductor design, stage integration.
* **Supports / sharpens / threatens:** **sharpens** the current direction, and **mildly threatens** the current build order around conductor work.
* **Read priority:** **later**.
* **Concrete question it helps answer:** *Can a human actually appropriate this conductor surface, or is the state vector overparameterized and unplayable?*

This is the missing lineage for the conductor. Your own IMS note already frames systems as sensing → mapping → engine → feedback → experience design, and treats mapping as “where the art lives.” NIME work adds two hard lessons. First, in a 10-musician study, adding a second control dimension reduced affordance exploration rather than increasing it. Second, Morreale and McPherson surveyed 97 NIME instruments and got 70 author responses; long-term uptake was low, and scenario design, participatory design, and fast prototyping mattered a lot. So: an APC Mini mapping is not a config file. It is instrument design. If you ignore that, you can build a nice control theory toy that nobody can really play.   ([NIME][5])

## Must-read now

* **Claim-centered discourse graphs / provenance.** This is the one that most directly addresses the graph membrane, which your own notes identify as the key compositional problem.
* **Storylets / QBN.** This is the one that most directly pressures `L3` experiment design right now.

## Later

* **Dynamic epistemic logic / epistemic planning.** Bring it in when the worlds care about secrecy, misinformation, and asymmetric knowledge in a serious way.
* **Event Segmentation Theory.** Bring it in before City Routes authoring and before locking down dashboard/event-boundary semantics.
* **NIME / IMS mapping and appropriation.** Bring it in before any serious conductor build, not before the Graffito pilot.

## Probably skip

A tempting near-miss is **full computational narratology ontology**: GOLEM-style fabula/sjuzhet/focalization/discourse-time machinery. It is real work, and it could matter later for plural interpretations or narrator structure, but right now it is mostly a bloat vector. Your notes are right to resist importing full discourse ontology into Experiment 1. 

## Bottom line

I would not blow up the roadmap. I would make **two** surgical changes now:

1. **Strengthen the graph membrane** from loose tags into typed claim/provenance records.
2. **Add a storylet/QBN comparison arm** to the `L3` experiments, or at least make the node schema explicit enough that you can tell whether you are reinventing storylets.

Then keep the rest disciplined:

* **Do not** move DEL ahead of the Graffito pilot.
* **Do not** drag in full narratology ontology.
* **Do** run a small NIME-style conductor spike before serious controller work, because the conductor may fail as an instrument even if the scheduler works.

That is the shortest path I see to reducing architecture drift while actually improving the odds that the next watched run teaches you something real.

[1]: https://arxiv.org/abs/2407.20666 "https://arxiv.org/abs/2407.20666"
[2]: https://www.failbettergames.com/news/storynexus-developer-diary-2-fewer-spreadsheets-less-swearing "https://www.failbettergames.com/news/storynexus-developer-diary-2-fewer-spreadsheets-less-swearing"
[3]: https://arxiv.org/pdf/1703.02192 "https://arxiv.org/pdf/1703.02192"
[4]: https://pmc.ncbi.nlm.nih.gov/articles/PMC3314399/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC3314399/"
[5]: https://www.nime.org/proceedings/2014/nime2014_409.pdf "https://www.nime.org/proceedings/2014/nime2014_409.pdf"
