I read the repo chain first. The repo already had the conjecture and the constraints:

`notes_team.md` gives John’s concrete picture: an external teaching agent arrives with its own KG, queries the learner KG, and finds the route from current knowledge to target knowledge. `threads_from_chat.md` and `launch-queries-john.md` turn that into a research question. `bidirectional_knowledge_text_coupling.md`, `established-findings.md`, and `convergence-map.md` add the warning: persistent graphs buy structure, but they also buy sync pain. `writing_concept_vault-as-personalized-learning.md` sets the design bar: local, learner-shaped, eccentric path first, formal path second.

Everything about OAEI/AML/LogMap, Knewton, ALEKS, Carnegie Learning, OLI, and recent 2023-2026 KG+LLM education papers required external search. The repo did not already answer Query 3.

Method: I checked recent papers and project pages on arXiv, CEUR, ERIC/ED, PMC, OAEI, and official docs from Wiley/Knewton, ALEKS, Carnegie Learning, OLI, CZI/Learning Commons, and IES.

## Verdict

John’s idea is coherent. It already exists as a **composition of known pieces**: an authoritative curriculum/domain graph, a learner model, an alignment layer, and a planner that chooses the next concept or resource. What I did **not** find is a mainstream deployed system that takes an open-ended personal PKM graph, aligns it with an external curriculum graph, and tutors over that intersection. Deployed systems usually keep the curriculum graph authoritative and represent the learner as an overlay, mastery state, or internal per-student model derived from assessments and interactions. ([CMU School of Computer Science][1])

The first nitpick: “graph intersection” is mostly your repo’s language, not the literature’s. The papers closest to John’s idea talk about **projection**, **integration**, **overlay**, or **alignment**. Pleskach et al. model the student’s acquired-knowledge graph as a subset of the general discipline graph, compare the two to identify gaps, and project the student graph onto the discipline graph to restore missing nodes and links and determine the already-completed part of the learning trajectory. Chen et al. do the same shape with newer tooling: textbook target KG plus student-dialogue KG, integrated to reveal activated versus non-activated knowledge and remaining gaps. ([CEUR Workshop Proceedings][2])

## What exists in the literature

There are direct hits, but they are mostly course-centric rather than PKM-centric. Pleskach’s 2023 paper is the clearest structural ancestor to John’s picture: student KG, discipline KG, comparison, missing-node recovery, and trajectory formation. Chen et al. 2024 push this into LLM territory by extracting KGs from both course materials and student discussions. Yu-Hsiang Chen et al. 2025 build personalized learning-trajectory graphs from course materials plus exam data. Abu-Rasheed et al. 2025 build linked **domain, curriculum, and user models** with LLM-assisted KG completion for personalized higher-ed recommendations. InstructKG 2026 automatically constructs instructor-aligned prerequisite and part-of graphs from course materials, explicitly to support gap diagnosis and targeted intervention. ([CEUR Workshop Proceedings][2])

At the schema layer, John’s docking problem is just ontology matching. OAEI is the standing evaluation framework for ontology alignment. LogMap and AgreementMakerLight are mature matchers; LogMap emphasizes scalability plus reasoning/repair, and AML has strong OAEI performance across years. There is also recent work using an LLM as an oracle inside LogMap-style alignment. But this only solves **concept/schema correspondence**. It does not solve learner diagnosis, mastery inference, or tutoring policy. Education-specific precedent exists here too: the older SAMUEL/SONIA/INGRID work already handled student models coming from systems with different ontologies and used semi-automatic ontology mapping to establish equivalences between concepts across domains. Competency ontologies such as COMP2 also include explicit cross-framework alignment hooks (`alignsTo`) and use them for competency-based personalization. ([Ontology Alignment Evaluation Initiative][3])

A 2024 systematic review is useful here because it cuts through the hype. It found KGs already being used for personalized learning, curriculum design, concept mapping, and educational recommendation, but it also flagged the obvious blockers: lack of standardized educational ontologies, interoperability problems, sparse/incomplete data, scalability, semantic heterogeneity, real-time updates, and weak evaluation. That lines up almost perfectly with your repo’s Track B vs Track C tension. ([PMC][4])

## What the big platforms actually do

Knewton/Wiley is close on the **curriculum** side. Its platform uses a knowledge graph of learning objectives and prerequisite relations. Recommendation scope can be defined by prerequisite hops, and remediation can walk backward through the graph when a student struggles. But the learner model is internal to the platform. The docs expose goals, progress, and recommendations, not a separate user-owned learner KG. So this is explicit curriculum graph + adaptive student model, not personal-KG docking. ([Knewton][5])

ALEKS is even more explicit about the learner model, but it is not a graph-alignment system. It uses Knowledge Space Theory to infer a student’s feasible knowledge state and “outer fringe,” meaning what the student is ready to learn next. That is rigorous and battle-tested, but it is a combinatorial knowledge-state model, not alignment between a learner-authored KG and an external ontology. ([Aleks][6])

Carnegie Learning’s MATHia is skill-tracing, not graph intersection. It uses step-level student behavior to infer what the learner understands, tracks skill proficiency probabilistically, and reports mastery skill by skill. OLI is similar in spirit: learning objectives / skills tagged into the course, then a statistical learning model and dashboard that estimate learning levels and identify where students are struggling. Both are adaptive and useful. Neither is doing John’s “teacher agent docks its KG into your personal KG” move. ([Carnegie Learning][7])

One current infrastructure move is worth noticing: Learning Commons/CZI has released an education Knowledge Graph that links standards, smaller learning components, and prerequisite-like pathways, plus an MCP server connecting that graph to Claude. That is not learner-side docking, but it **is** external teaching-graph docking. So the ecosystem is moving toward machine-readable curriculum graphs that can be attached to LLMs. It is just not yet doing the full learner-owned graph piece. ([Chan Zuckerberg Initiative][8])

## Are people doing this with LLM-extracted KGs?

Yes, but mostly on the course side, not the PKM side. Chen 2024 extracts both target and student KGs with LLMs. Yu-Hsiang Chen 2025 uses LLMs to turn materials into structured learning-trajectory graphs. Abu-Rasheed 2025 uses LLM-assisted KG completion for curriculum/domain/user models. InstructKG 2026 uses an LLM to infer prerequisite and compositional dependencies from lecture documents. An IES-funded 2025 project, SkillTree, is even closer to your direction: it proposes LLM-linked custom KGs that visualize a student’s knowledge, skills, and interests in real time. There is also a conceptual precedent for your learner-owned/local stance: Ilkou’s 2022 PKG proposal argues for small, user-centric personal knowledge graphs in e-learning for personalization, explainability, and privacy. But that is still a proposal, not evidence that the full docking stack works in production. ([ERIC][9])

## The “shortest path” part is slightly wrong

This is the second nitpick that matters. “Find the shortest path between current knowledge and target knowledge” is a clean graph metaphor, but it is not usually the right pedagogical objective. Some systems do literally enumerate candidate paths and choose the shortest, and some recent recommenders use Dijkstra or A* as subroutines. But the stronger systems optimize for **best feasible path**, not minimum hops: mastery, prerequisites, difficulty, forgetting, time, uncertainty, and sometimes reinforcement-learning reward. Knewton’s remediation depth, ALEKS’s outer fringe, ALPN’s knowledge-tracing + RL loop, and newer KG-based path planners all fit that pattern. So John’s “shortest path” should probably be translated into “lowest-cost mastery path under constraints.” ([PMC][10])

## What this implies for Track C

The graph bet looks plausible$_{85%}$. The specific **personal vault as learner graph** bet is where the literature gets thin.

So the near-term architecture I’d trust is:

1. Keep the external curriculum/domain graph authoritative and versioned.
2. Treat the learner side as an **overlay/evidence graph**, not a second authoritative ontology.
3. Use ontology alignment to map vault concepts/claims onto canonical curriculum IDs.
4. Infer mastery from stronger signals than note presence alone: assessments, explicit self-ratings, repeated successful recall, dialogue evidence, task performance.
5. Run path planning on the curriculum graph, with the learner overlay supplying weights and constraints.

That respects your repo’s sync warning and preserves the learner-centered/local stance. The vault then tells you what this person notices, values, and already connects. The curriculum graph tells you what prerequisites and formal dependencies remain. The intersection is useful. Letting the vault itself stand in for prerequisite mastery is the part that still looks under-validated. ([PMC][4])

Bottom line: Query 3 comes back **yes in pieces, no as a standard product**. The literature validates curriculum graphs, learner overlays, ontology matching, and mastery-aware path planning. It does **not** yet strongly validate the exact move your repo cares about most: using a local personal knowledge system as the learner graph that docks into an external teaching graph. That is the live research edge. ([CEUR Workshop Proceedings][2])

Next useful step would be to turn this into a decision memo for the repo: what stays flat-file, what becomes graph, and what evidence is allowed to update mastery.

[1]: https://www.cs.cmu.edu/~aleven/Papers/2016/Aleven_etal_Handbook2017_AdaptiveLearningTechnologies.pdf "https://www.cs.cmu.edu/~aleven/Papers/2016/Aleven_etal_Handbook2017_AdaptiveLearningTechnologies.pdf"
[2]: https://ceur-ws.org/Vol-3646/Paper_11.pdf "https://ceur-ws.org/Vol-3646/Paper_11.pdf"
[3]: https://oaei.ontologymatching.org/ "https://oaei.ontologymatching.org/"
[4]: https://pmc.ncbi.nlm.nih.gov/articles/PMC10847940/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC10847940/"
[5]: https://dev.knewton.com/implementation/api-overview/ "https://dev.knewton.com/implementation/api-overview/"
[6]: https://www.aleks.com/about_aleks/research_behind "https://www.aleks.com/about_aleks/research_behind"
[7]: https://www.carnegielearning.com/blog/mathia-ai "https://www.carnegielearning.com/blog/mathia-ai"
[8]: https://chanzuckerberg.com/blog/scaling-proven-learning-practices/ "https://chanzuckerberg.com/blog/scaling-proven-learning-practices/"
[9]: https://files.eric.ed.gov/fulltext/ED665562.pdf "https://files.eric.ed.gov/fulltext/ED665562.pdf"
[10]: https://pmc.ncbi.nlm.nih.gov/articles/PMC9748379/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC9748379/"
