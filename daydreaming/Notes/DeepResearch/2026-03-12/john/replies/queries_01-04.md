Here’s the clean local-only synthesis.

`launch-queries-john.md` says these four are **web research questions** and that the repo by itself cannot answer them. So the right move is not to pretend otherwise. The useful output is: what the local corpus already establishes, what is still just proposal-language, and what that implies for John’s track.

Two constraints from the dropped threads still matter because they shape all four queries.

First, the **sync tax** is real. `established-findings.md` and the 2026-03-11 landscape note say text→graph extraction is mature enough to deploy, but authoritative bidirectional sync is not. Any persistent KG inherits that problem.

Second, the local critique in `IMS+Daydream_-_Bidirectional_Sync_in_Writing.md` says the bottleneck is not “having a graph.” It is **boring typed substrate + provenance/scope/reversibility + salience/control**. That sharply limits how much credit to give Graphiti-style pitches.

## Query 2 — Graphiti specifically

**Established locally**

The strongest pro-Graphiti case in the repo is John’s pitch in `notes_team.md`: KGs give explicit topology instead of “soup of paragraphs”; frames become literal subgraphs; temporal change becomes queryable; docking becomes graph intersection; entity resolution cleans messy notes into typed concepts. That is the case for Graphiti.

But that same file is a **model-generated chat excerpt**, not independent validation. Treat it as proposal, not evidence.

The strongest constraint against Graphiti in the repo comes from the other direction:

* `established-findings.md` says persistent graphs create a sync problem nobody has solved.
* `convergence-map.md` explicitly argues that the flat-file, inference-time approach may be the structurally correct default for the current moment.
* `AGENT_PROTOCOL.md` is not theory. It is an operating example of that alternative: semantic matching over markdown atoms, no requirement that the human maintain links, provenance logged every time.
* The integration chat sharpens the critique further: a graph alone is a dead warehouse unless paired with salience and control.

One more correction: John’s pitch is mostly **concept-centric**. The repo’s strongest prior research says the field is converging on **claims / questions / evidence** as the durable atom, not generic concept nodes (`bidirectional_knowledge_text_coupling.md`, `established-findings.md`). So even if Graphiti is useful, a concept-only build is probably aimed one layer too low.

**Unvalidated locally**

The repo does **not** give you any of the evidence Query 2 actually asked for:

* no Graphiti benchmarks
* no GitHub issue summaries
* no production user reports
* no independent confirmation of Neo4j dependency details
* no evidence about community detection implementation
* no evidence about entity resolution quality in practice
* no scale thresholds at 100 / 10K / 100K nodes
* no comparisons to LlamaIndex property graphs, LangChain graph stores, or real flat-file baselines

So the repo does not support “Graphiti is superior.” Full stop.

**Answer for John’s track**

The local answer is: **Graphiti remains a plausible later enrichment layer, not a justified architectural default.**

Best local guess$_{80%}$: for the current workspace size, Graphiti overhead does **not** pay for itself. `convergence-map.md` literally frames the current vault as tiny and asks whether inference-time matching only breaks at 500 or 5,000 atoms. That means the threshold is still unknown, and the current scale is clearly below it.

What the local corpus does support is a narrower claim:

* KGs become interesting once you need **durable typed relations**, **explicit prerequisite traversal**, **historical change queries**, or **subgraph-level operations** that an LLM cannot reliably reconstruct on the fly.
* Even then, the hard part is still not the graph. It is trust: provenance, scope, reversibility, and sync.

So the repo does not validate John’s Graphiti thesis. It says: **maybe later, only under pressure, and probably over claims/questions/evidence rather than loose concepts.**

## Query 1 — PKM-to-LLM docking pipelines

**Established locally**

The repo is strongest here in a negative way: `launch-queries-john.md` and `established-findings.md` both say the **practical PKM-to-LLM pipeline side is still open**. The previous deep-research pass covered scholarly infrastructure, not the day-to-day toolchain.

What the repo does give you is a small set of **patterns**, not an ecosystem survey.

One pattern is the scholarly side:

* discourse graph protocol
* nanopublications
* ORKG

Those are real precedents in the local research, but they are not practical Obsidian/Claude/Cursor pipeline answers.

The second pattern is the repo’s own counter-design:

* `AGENT_PROTOCOL.md` defines docking as semantic reading over markdown atoms plus required provenance logging.
* `project_conducted_daydreaming.md` shows one concrete Claude-style memory artifact.
* There is **no repo-local `CLAUDE.md`**, which matters because Query 1 explicitly asked about that.
* `vault design possibilities.md` holds two practical leads: an Obsidian MCP / graph Reddit thread and a canvas-like AI UI thread. Those are leads only.
* `2026-03-09.md` and `notes_team.md` mention Peter D / Peter Danovich, Gemini, JSCL, JupyterLite, and LilPAIPer-adjacent work. Again: lead only, not validated implementation detail.

**Unvalidated locally**

The repo does not answer the practical questions Query 1 asked:

* no verified Obsidian MCP servers
* no GitHub stars or last-commit dates
* no Cursor context-loading details
* no Gemini document-grounding specifics
* no writeup of Peter Danovich’s textbook-to-Gemini pipeline
* no maturity assessment of deployed tools

Also, the identity link between “Peter D” in the journal and “Peter Danovich” in the John notes is plausible, not proven.

**Answer for John’s track**

The local answer is that **practical docking is still largely unanswered here**.

What is locally supported is a design stance:

* keep the PKM in markdown
* let the model dock at inference time by semantically reading relevant files
* log memory/provenance explicitly
* do not force the human to maintain a graph unless that graph starts paying rent immediately

That is not the market survey Query 1 wanted. It is the repo’s strongest current answer to the design problem.

So, for John’s track: **the strongest practical docking pattern in this corpus is flat-file semantic traversal, not an established MCP/KG stack.** Everything else in Query 1 is still a lead list.

## Query 3 — Graph intersection for personalized learning

**Established locally**

This is the cleanest statement of John’s novel idea:

* personal knowledge graph + objective/curriculum graph
* docking as graph intersection
* shortest path between what the learner knows and what the system wants to teach (`notes_team.md`, `threads_from_chat.md`)

The repo also gives you the motivation clearly:

* the vault as a **local, self-profiling** learning system
* resistance to externally harvested learner models
* “eccentric learning paths” that later reconcile with formal pathing
* critique of LilPAIPer-like systems that reflect the tutor’s structure rather than learning the student’s shape (`2026-03-09.md`, `writing_concept_vault-as-personalized-learning.md`)

The prior research changes the atomic picture, though. If the field is converging on **questions / claims / evidence** rather than concept nodes, then a learner model probably should not be “bag of concepts with edges.” It should look more like:

* what questions the learner has active
* what claims they currently accept / reject / hedge
* what evidence they trust
* where uncertainty and contradiction live

The integration chat makes the correction even harsher: shortest-path on a static graph is too crude because it ignores **salience** and **control**. In tutoring terms, it ignores what the learner is trying to do right now.

**Unvalidated locally**

The repo does not give you what Query 3 asked for:

* no ontology-alignment literature summary
* no OAEI / LogMap / AML evidence
* no Knewton / ALEKS / MATHia / OLI analysis
* no examples of teaching agents aligning against LLM-extracted learner graphs
* no deployed systems doing this in practice

So there is zero local empirical support for “graph intersection for personalized learning” as an existing method.

**Answer for John’s track**

The local answer is: **the idea is promising, but the current formulation is too naive.**

Best local reformulation$_{75%}$:

> not concept-graph intersection, but alignment between learner and curriculum over questions, claims, evidence, uncertainty, and current salience.

That is a better fit with the repo’s own research and critique layer.

So John’s mechanism survives, but only after correction:

* “frame as subgraph” is too static
* “shortest path” is too topological
* learner modeling needs provenance, scope, and temporal state
* the right v0 may be flat-file semantic alignment over extracted Q/C/E units, not a persistent learner KG

In other words: the repo supports the **problem statement** and the **design pressure**, not the graph-intersection solution as stated.

## Query 4 — Temporal knowledge graphs

**Established locally**

John’s pitch treats temporal awareness as the holy grail: track how facts and relationships change over time. That shows up in `notes_team.md` and in `convergence-map.md` as part of the Graphiti attraction.

But the repo’s actual knowledge on temporal KGs is thin. `launch-queries-john.md` itself says this remains open. `established-findings.md` says nothing specific beyond the general sync gap.

The one useful correction from the critique layer is that temporal value is probably **not** just timestamped nodes and edges. The better target is something like **version control for commitments**:

* what was believed when
* what changed
* what evidence caused the change
* what is currently active versus merely stored

That lines up with `IMS+Daydream_-_Bidirectional_Sync_in_Writing.md`.

Also, the repo already has simpler temporal mechanisms that are more concrete than any Graphiti evidence here:

* dated journals
* append-only `_memory/` traces
* project memory artifacts
* provenance logs required by `AGENT_PROTOCOL.md`

And there is a useful cross-domain design analogy in `project_conducted_daydreaming.md`: the daydream system separates **slow ripeness** from **fast activation**, with decay and explicit commits. That is not a temporal KG, but as a learner-model pattern it is more specified than anything Graphiti-specific in the repo.

**Unvalidated locally**

The repo does not answer Query 4’s external questions:

* no Graphiti temporal feature validation
* no TigerGraph / Neo4j / TerminusDB review
* no evidence on graph-based spaced repetition
* no knowledge decay detection systems
* no xAPI / Caliper linkage
* no deployed personal temporal KG products

**Answer for John’s track**

The local answer is: **temporal KG remains mostly aspiration in this corpus.**

What the repo does support is a sharper requirement:

> the valuable temporal object is not time-stamped facts; it is tracked change in commitments, salience, confidence, and neglected regions, with provenance.

That suggests a sequence:

1. first build append-only provenance and memory surfaces
2. then model fast-vs-slow state change
3. only then decide whether a temporal KG is warranted

So, locally, the right stance is: **treat temporal KG as an unproven upgrade path, not an existing capability you can assume.**

## Bottom line

For John’s track, the repo supports four hard conclusions.

**1. Graphiti is not validated here.**
The strongest Graphiti claims are proposal-language in `notes_team.md`. The strongest counter-evidence is the repo’s own working flat-file architecture plus the sync-gap research.

**2. Practical PKM docking is still mostly open.**
The local corpus gives patterns and leads, not an external tool survey. The strongest implemented pattern here is markdown + inference-time semantic matching + provenance logs.

**3. Personalized-learning docking survives only after reformulation.**
The promising object is not concept-graph shortest path. It is alignment over questions, claims, evidence, uncertainty, and current salience.

**4. Temporal KG is not answered locally.**
The repo’s sharper insight is “version control for commitments,” not “Graphiti already solves temporal learning.”

So the honest synthesis is this:

**John’s ideas are still live, but in this repo they are hypotheses under pressure, not findings.**
The strongest current architecture in the local material is still Track B’s bias: flat markdown, semantic traversal at inference time, strict provenance, and delayed commitment to persistent graph infrastructure until scale or task structure forces it.
