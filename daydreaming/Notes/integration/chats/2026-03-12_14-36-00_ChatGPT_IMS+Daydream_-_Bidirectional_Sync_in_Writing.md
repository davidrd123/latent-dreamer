# IMS+Daydream - Bidirectional Sync in Writing

Source: [ChatGPT](https://chatgpt.com/g/g-p-698bffa582bc8191b96df7026d88b76e/c/69b2327f-7688-8323-bad9-ba684606abad)
Extracted: 2026-03-12T21:36:00.864Z
🚀 Exported with [Save my Chatbot](https://save.hugocolin.com)!

---

## User

Thoughts on this? 
'''
**The bidirectional sync, concretely:**

The graph-to-prose direction is easier. You have structured material, you surface it contextually while someone writes. Retrieval-augmented writing. People are already building this (Elicit, Afforai, various academic tools). The quality varies but the architecture is understood.

The prose-to-graph direction is where it gets interesting and mostly unsolved. When a writer produces a sentence, several things happen simultaneously that *should* update the graph:

- A new claim enters the world (node)
- It implicitly asserts relationships to existing material (edges)
- It *chooses* among the tensions in the graph by resolving them one way rather than another (edge deletion or weight change)
- It may introduce new tensions the writer hasn't noticed yet
- The *way* it's phrased carries information about the writer's epistemic state — hedged language means low confidence, definitive language means the writer is treating something as settled

An agent that can parse all of that from fresh prose and update the graph accordingly is doing something like... real-time discourse analysis with ontology maintenance. Not impossible — it's within current capability for each individual operation — but the integration is the challenge. You'd need something running continuously in the background, not batch-processing.

The closest existing analogy is how IDEs work for code. You type, the language server parses incrementally, the symbol table updates, red squiggles appear, autocomplete adjusts. The "graph" for code is the AST + type information + dependency graph, and it stays synchronized with the text because there's a formal grammar. For prose, there's no grammar in that sense, so you need an LLM doing the parse, which means it's probabilistic, which means sometimes it'll be wrong, which means you need a UI that lets the writer correct the graph without breaking flow.

**The larger picture:**

Here's how I see the threads connecting across our conversations:

There's a recurring pattern where humans build understanding by maintaining a *structured working model* of some domain, and the current tools for that are either (a) all in your head, which doesn't scale, or (b) externalized into flat text, which loses structure, or (c) externalized into formal representations, which lose nuance.

The Pynchon case is one instance. The historian case is another. But it's the same thing in:

- **Your Daydream project** — you and your teammate are building a vault of source material that an agent navigates. The vault is a proto-graph. The knowledge graph extraction your teammate is doing is literally the "build the structured representation" half. The video generation pipeline is the "produce output from the representation" half. The missing piece is the bidirectional part — the output informing the graph.

- **The morphogenetic RLM architecture** we designed — the constraint graph with soft weights, where sub-agents read local neighborhoods and emit proposed updates. That *is* this tool, applied to AI self-regulation rather than human writing. Same pattern: structured representation ↔ generated output, bidirectionally coupled.

- **Latour's fact-construction** — a finding becomes a fact through accumulating network connections. The graph of allies, inscriptions, instruments. What we called "sheafification as fact-construction" — local observations gluing into global coherence. The historian's evidence graph is a miniature version of Latourian fact-construction. Each edge you add is an enrollment.

- **The "moment vs. whole" problem** from the RLM conversation — current AI is good at generating the local moment but can't maintain coherence across the whole. The graph *is* the representation of the whole that makes local generation coherent. It's what lets you write paragraph 47 in a way that's consistent with paragraph 3, because the graph holds what paragraph 3 committed you to.

So the meta-pattern is: **the graph is the medium through which local actions achieve global coherence.** And the missing tool, across all these domains, is the bidirectional coupling between rich unstructured output (prose, video, agent actions) and the structured representation.

Now — why doesn't this exist yet? Three reasons, I think:

**1. The atoms are genuinely unclear.** You flagged this. In STEM, the atoms are entities-and-relationships and people have converged on triples. For humanities/creative work, we listed four candidate atoms (inscriptions, transformations, resonances, tensions). But those aren't stabilized. Nobody's built a system around them and iterated on whether they're the right decomposition. They might be wrong. Maybe you need "commitments" as a separate atom. Maybe "resonance" and "tension" are both instances of a more general "structural correspondence with valence" atom. The type system for the graph is an open research question.

**2. The evaluation problem.** How do you know the graph is good? For code, you run it — does it compile, does it pass tests. For a knowledge graph in STEM, you check factual accuracy. For a writing-support graph, the quality metric is something like "did this help the writer produce better work," which is expensive and slow to evaluate. So nobody can iterate quickly on the design.

**3. The expertise mismatch.** The people who know about knowledge representation (KR/ontology folks, SPARQL people) mostly don't write literary prose. The people who write literary prose mostly don't think in graphs. The people who could bridge this (digital humanities, computational rhetoric) are a small community without much engineering firepower. And the AI-tools startups are chasing the mass market, where "help me write an email faster" pays and "help me maintain a Latourian evidence network for my historical monograph" doesn't.

The thing that might change this: if the atoms stabilize enough that you can build a prototype for *one* domain (say, historical research specifically) and demonstrate that the bidirectional coupling produces measurably better work, that creates a proof of concept that generalizes. The Pynchon use case is romantic but hard to evaluate. A historian producing a paper with and without the tool is more tractable.
'''

## ChatGPT

Mostly right. A few parts are sharp, a few parts are blurry.

The strongest claim is this:

> the missing tool is the bidirectional coupling between rich unstructured output and the structured representation

Yes. That is the real thing. Not “knowledge graph for writers,” which is too vague. The problem is maintaining commitments across scales while preserving the local freedom of prose generation.

But I’d tighten the model in a few places.

## 1. “Prose-to-graph” is not one problem

Right now it’s bundled into one giant box: parse prose, update ontology, infer tensions, infer epistemics, maintain graph. That is too monolithic.

There are at least four separable update channels:

**Referential updates**
entity/coreference/disambiguation
“he” = Slothrop, “the rocket” = 00000, etc.

**Commitment updates**
what the text is now committed to, denied, presupposing, or leaving open

**Rhetorical updates**
contrast, concession, emphasis, framing, foreground/background

**Epistemic/stylistic updates**
hedge, confidence, evidential status, narrator stance, irony risk

Those should not all hit the same graph layer.

If you collapse them, the system becomes brittle fast. “Maybe X happened” is not just a weak assertion node. It is a commitment with a specific modal status, maybe attributed to a speaker, maybe only live within a local discourse frame.

So I think your “new claim enters the world (node)” line is too crude. Often a sentence does **not** add a world-fact. It adds one of:

a hypothesis

a narration-level framing move

a local comparison

a reminder

a viewpoint attribution

a temporary planning commitment by the writer

Those are different object types.

## 2. The code/IDE analogy is good, but only halfway

You’re right that the language server analogy is the closest modern UX analogy. But prose is not “code without a grammar.” That understates the problem.

Code editors stay synchronized because they have:

formal syntax

stable denotation

executable semantics

hard failure conditions

Prose lacks all four, or has them only weakly.

The closer analogy is not an IDE. It is something like:

IDE

argument map

citation manager

outliner

theorem assistant

collaborative whiteboard

all mashed together, with soft constraints instead of hard ones.

That matters because the UI can’t just be “red squiggles for graph inconsistencies.” Most of the time the system should surface **tension gradients**, not errors.

Example:

paragraph 3 suggests Character A distrusts B

paragraph 47 depicts intimate cooperation

this is not necessarily a contradiction

but it may be an unresolved transition that needs bridging

That is not a type error. It is a narrative pressure point.

## 3. “The graph is the medium through which local actions achieve global coherence” is good, but incomplete

It’s missing the scheduler.

A static graph is not enough. Global coherence in writing depends on at least three things:

**state**: what’s currently committed

**salience**: what is active right now

**control**: what the writer is trying to do in this paragraph

Without salience and control, the graph becomes a dead warehouse.

So I’d revise the meta-pattern to:

> the structured model is the memory substrate through which local actions can inherit global constraints, but only when coupled to a salience model and a control model.

That’s why plain knowledge graphs feel dead. They store relations, but they do not model what is currently pressing, unresolved, or instrumentally relevant.

This is also why your RLM connection is real. The important object is not a graph by itself. It is a graph under continual reweighting by current goals, local context, and unresolved tensions.

## 4. Your “atoms are unclear” section is exactly right, but I think you’re one level off

I don’t think the first successful system will start by solving the “right atom type system” in the abstract.

It’ll probably start by cheating.

Meaning: pick one domain and define a lopsided ontology that is obviously incomplete but operational.

For historians, the initial atoms might be:

source

claim

evidence

actor

event

date/time span

place

relation

contradiction

uncertainty

open question

That is pedestrian. But it’s enough to build a working loop.

For fiction/literary analysis, maybe:

entity

scene

motif

relation

tension

inference

unresolved question

thematic resonance

viewpoint attribution

Again, ugly but usable.

I would not start with “inscriptions, transformations, resonances, tensions” as the primary data model. That sounds cleverer than it is operational. “Resonance” especially is dangerous because it encourages the graph to become a vibes machine.

My guess$_{75%}$: the winning design has **boring primary atoms** and **richer derived overlays**.

So:

primary graph: claims, entities, relations, conflicts, supports

secondary layer: resonance, motif echo, structural analogy, thematic pressure

If you reverse that, the thing becomes untestable.

## 5. The biggest omission is provenance and scope

This is the hole I’d fix first.

Any real prose-to-graph sync needs every update to carry at least:

**source span**: what exact text triggered it

**scope**: global fact, local section, quoted speech, hypothetical, outline note, etc.

**attribution**: narrator, character, author note, cited source

**confidence**: system confidence, not just writer confidence

**reversibility**: can the writer undo or reclassify it quickly

Without that, the graph becomes haunted. Stuff appears in it with unclear origin and unclear ontological status.

That kills trust.

I’d state this much more strongly: provenance is not a nice-to-have. It is the difference between an instrument and a parasite.

## 6. The evaluation problem is real, but not as bad as you make it sound

You say evaluation is hard because the metric is “did this help the writer produce better work.” True, but that’s the far metric. There are nearer ones.

You can decompose evaluation into:

### Intrinsic metrics

precision/recall of entity and relation extraction

commitment classification accuracy

contradiction detection quality

provenance correctness

user correction rate

### Interaction metrics

time to resolve inconsistency

number of useful surfaced tensions

interruption burden

acceptance vs dismissal rate for suggestions

### Outcome metrics

coherence ratings by blinded readers

citation coverage

revision efficiency

self-reported cognitive load

That is enough to iterate.

So I’d change “nobody can iterate quickly” to “people haven’t chosen the right intermediate metrics.”

## 7. The expertise-mismatch diagnosis is correct, but there’s another reason this doesn’t exist

You missed the economic/UI failure mode.

Most tools in this space fail because they force the writer to become a database janitor.

That is fatal.

A writer will tolerate correction, lightweight tagging, and occasional graph surgery. They will not tolerate constant ontology gardening while drafting.

So the key design constraint is:

> the graph must earn the right to exist by paying rent in live cognition

Meaning it needs to help right away with things like:

“you’ve introduced a new unresolved contradiction here”

“this paragraph implicitly settles Q3”

“this claim currently rests on a single weak source”

“this sentence conflicts with how you framed the narrator’s knowledge earlier”

“this motif links to three prior scenes you may want active”

If it can’t do that, it becomes just another pane of guilt.

## 8. The historian use case is better than the Pynchon use case, yes

Agreed. Because historians have:

external sources

citation structure

explicit claims

falsifiability gradients

clearer success criteria

That makes it the right first beachhead.

I’d narrow even further. Not “historian” in general. More like:

**argumentative research writing from source packets**

For example:

dissertation chapter drafting

review essay from annotated sources

archival argument construction

That gives you a much tighter loop than “creative writing” or “literary scholarship” broadly.

## 9. What I’d rewrite in your current framing

This sentence:

> An agent that can parse all of that from fresh prose and update the graph accordingly is doing something like... real-time discourse analysis with ontology maintenance.

I’d replace with:

> The core task is incremental commitment tracking over prose, with entity resolution, scoped attribution, and reversible structural updates to an external memory.

That’s uglier, but more exact.

And this:

> The atoms are genuinely unclear.

I’d replace with:

> The correct atomization is unknown, but first systems can succeed with crude domain-specific atoms if provenance, scope, and reversibility are handled well.

That’s more actionable.

## 10. Net assessment

The piece is good because it identifies the actual missing object: not generation, not retrieval, but synchronization between expressive output and structured commitments.

What’s still weak:

it over-unifies several distinct parsing/update tasks

it gives the graph too much credit and salience/control too little

it treats “atoms” as the first bottleneck when trust/provenance may be more important

it doesn’t distinguish world-facts from discourse acts sharply enough

My strongest guess about the right architecture:

**boring typed substrate**

**continuous probabilistic update proposals**

**strict provenance/scoping**

**salience layer over the graph**

**writer-facing UI that surfaces pressures, not formal errors**

**human correction as first-class, low-friction operation**

That seems right$_{80%}$.

And one more thing: the real comparison class may be less “knowledge graph for prose” and more **version control for commitments**. The graph is the data structure, but the user value may come from seeing what a paragraph *changed* in the space of commitments.

That’s a sharper product idea.

I also see your IMS exploration notes loaded in the workspace, though they’re orthogonal to this thread. ims-exploration-notes

Look away from the screen for ten seconds and drop your shoulders.

## User

Anything to use from this research 
'''
# When graphs meet prose: the state of bidirectional knowledge-text coupling

**The claim — not the document, not the concept — is emerging as the fundamental atom of structured scholarly knowledge**, and the period from 2023 to 2026 has seen significant convergence across discourse graphs, computational argumentation, digital humanities ontologies, and LLM-powered extraction tools toward systems that treat writing and graph-editing as two views of the same object. True bidirectional synchronization between prose and structured representations remains an unsolved frontier problem, but several research programs and commercial tools are narrowing the gap from both sides. This report maps the landscape across six overlapping areas, highlighting the most consequential work and identifying where the field's structural primitives stand for non-STEM domains.

---

## Joel Chan's discourse graph program sets the pace

The most sustained and coherent research agenda on coupling structured knowledge with prose writing comes from Joel Chan's group at the University of Maryland. Their 2024 paper "Steps Towards an Infrastructure for Scholarly Synthesis" distills three years of research-through-design deploying discourse graph plugins among Roam Research users. [arXiv](https://arxiv.org/abs/2407.20666) The core model represents knowledge as **questions, claims, and evidence** (QUE, CLM, EVD) linked by typed rhetorical relationships (supports, opposes, informs), [Zettelkasten Forum](https://forum.zettelkasten.de/discussion/2509/discourse-graph-and-zettelkasten) drawing on earlier formalisms like SWAN, micropublications, and nanopublications. [Gitbook](https://oasis-lab.gitbook.io/roamresearch-discourse-graph-extension/fundamentals/what-is-a-discourse-graph) [Scalingsynthesis](https://scalingsynthesis.com/q-what-is-a-decentralized-discourse-graph/)

The most interesting component for bidirectional coupling is the **Discourse Grammar Parser**, which interprets informal prose outlines into formal discourse graph relations. Writers compose naturally in their hypertext notebooks; the parser recognizes patterns in their text that correspond to formal discourse relations and populates the graph automatically. A complementary "Node Menu" lets users formalize informal notes into typed discourse nodes without leaving their writing flow. [The Moonlight](https://www.themoonlight.io/en/review/steps-towards-an-infrastructure-for-scholarly-synthesis) This represents one of the closest approximations to "prose as a view on structured data" currently deployed in the wild.

The ecosystem has grown beyond a single-tool prototype. Discourse graph plugins now exist for both Roam Research and Obsidian, [Discoursegraphs](https://discoursegraphs.com/) and the project's conceptualization as a client-agnostic protocol — analogous to Git for knowledge synthesis — enables decentralized push-pull storage of discourse graphs across platforms. [Discoursegraphs](https://discoursegraphs.com/) Funding from Protocol Labs and Metagov supports ongoing development, with community events running through 2025. [Discoursegraphs](https://discoursegraphs.com/) Related work from this group includes a UIST 2024 paper on "Patterns of Hypertext-Augmented Sensemaking" (studying 23 scholars' workflows), [ACM Digital Library](https://dl.acm.org/doi/fullHtml/10.1145/3654777.3676338) [Joelchan](https://joelchan.me/publications/) Creativity & Cognition 2025 papers on immersive sensemaking environments, and the Context24 shared task on contextualizing scientific claims at the Scholarly Document Processing workshop. [Joelchan](https://joelchan.me/publications/)

At Carnegie Mellon and the Allen Institute for AI, Aniket Kittur and Joseph Chee Chang pursue a complementary agenda. Synergi (UIST 2023) is a mixed-initiative system that uses citation graphs and LLMs to expand and structure research threads that scholars iterate on [arXiv](https://arxiv.org/abs/2308.07517) — a form of coupled structured-and-prose synthesis. Selenite (CHI 2024) scaffolds online sensemaking by having LLMs produce structured overviews of options and criteria that guide subsequent reading. [arXiv](https://arxiv.org/abs/2310.02161) The Semantic Reader Project, described in a 2024 Communications of the ACM article [Skywork](https://skywork.ai/blog/semantic-scholar-review-2025/) with 45+ co-authors, creates AI-augmented reading interfaces [MIT DSpace](https://dspace.mit.edu/handle/1721.1/157322) (CiteSee, ScholarPhi, CiteRead) that overlay structured metadata onto prose documents [Semantic Scholar](https://www.semanticscholar.org/product/semantic-reader) — bridging the reading side of the equation.

---

## Structural primitives beyond the triple: what atoms fit humanities knowledge

The question of what the right "atoms" are for non-STEM knowledge representation has produced several competing but partially compatible answers in this period.

**Enhanced Rhetorical Structure Theory (eRST)**, published by Amir Zeldes and colleagues in *Computational Linguistics* in early 2025, represents the most significant advance in discourse-level structural primitives. Classical RST constrained discourse relations to tree structures; eRST extends this to **discourse relation graphs** that accommodate tree-breaking, non-projective, and concurrent relations. [MIT Press](https://direct.mit.edu/coli/article/51/1/23/124464/eRST-A-Signaled-Graph-Theory-of-Discourse) It introduces over 40 signal types that provide explainable rationales for each discourse relation, organized in a hierarchical taxonomy. [Gucorpling](https://gucorpling.org/erst/) A freely available corpus (GUM, 200K+ tokens across 12 genres) supports the framework. [Åbo Akademi University](https://research.abo.fi/en/publications/erst-a-signaled-graph-theory-of-discourse-relations-and-organizat) [MIT Press](https://direct.mit.edu/coli/article/51/1/23/124464/eRST-A-Signaled-Graph-Theory-of-Discourse) For knowledge representation in humanities, eRST matters because it provides a formal, empirically grounded vocabulary for how text segments relate to each other — beyond simple hierarchies — while remaining backwards-compatible with decades of RST-annotated resources.

In computational narratology, the **GOLEM Ontology** (Pannach et al., 2024–2025, published in MDPI *Humanities*) extends CIDOC-CRM and LRMoo into the narratology domain using the DOLCE foundational ontology. GOLEM models narrative structure, character dynamics, and fictional worlds while supporting provenance tracking and pluralistic interpretations. [ResearchGate](https://www.researchgate.net/publication/305476241_Steps_Towards_a_Formal_Ontology_of_Narratives_Based_on_Narratology) It explicitly engages with Meinong's theory of objects for representing fictional entities via an "as-if-about-only" construct [MDPI](https://www.mdpi.com/2076-0787/14/10/193) — bridging phenomenological concerns with computational formalism. A related survey on "Narrative Theory-Driven LLM Methods" (arXiv, 2025) maps narratological theories from Genette, Todorov, and cognitive narratology onto LLM capabilities, establishing connections between traditional literary-critical primitives and computational methods.

**Argumentation schemes** continue to evolve as knowledge representation primitives. Walton's scheme framework has been implemented as a knowledge base for multi-agent systems (Ferreira et al., ICEIS 2024), [SciTePress](https://www.scitepress.org/Papers/2024/125478/125478.pdf) while KR 2024 featured extensive work on formal argumentation including counterfactual explanations, [Kr](https://proceedings.kr.org/2024/2/) preferential reasoning in ASPIC+, and a position paper on "Contestable AI Needs Computational Argumentation." [Kr](https://proceedings.kr.org/2024/) A comprehensive 2025 survey on LLMs in argument mining documents the shift from supervised pipelines to prompt-driven, retrieval-augmented argumentation analysis. [arXiv](https://arxiv.org/abs/2506.16383) [arXiv](https://arxiv.org/html/2506.16383v6)

For scholarly communication specifically, **nanopublications** offer perhaps the most mature atomic primitive. Tobias Kuhn's group at VU Amsterdam continues developing the nanopublication infrastructure [PeerJ](https://peerj.com/articles/cs-78/) — each nanopublication is a small RDF graph consisting of assertion, provenance, and publication information, treated as an independent citable unit. [PubMed Central +2](https://pmc.ncbi.nlm.nih.gov/articles/PMC10280262/) The 2023 field study by Bucur, Kuhn, and Ceolin demonstrated the entire scientific review process (submissions, reviews, responses, decisions) represented as interlinked nanopublications. The ARPHA Writing Tool now allows biodiversity researchers to highlight and export key findings from manuscripts as structured nanopublications inline — a concrete dual-view implementation. [Blog](https://blog.pensoft.net/2023/05/17/how-it-works-nanopublications-linked-to-articles-in-rio-journal/) [Blog](https://blog.pensoft.net/tag/nanopublications/) An IRCDL 2025 paper proposes extending nanopublications with a fourth "knowledge provenance" component for assertions derived from bodies of evidence rather than single sources, directly connecting to discourse graph concepts. [ResearchGate](https://www.researchgate.net/publication/235892537_Broadening_the_Scope_of_Nanopublications) [CEUR-WS.org](https://ceur-ws.org/Vol-3937/paper10.pdf)

A notable 2025 ArXiv paper introduces **Knowledge Synthesis Graphs** (KSGs), which use LLMs to extract "Micro-ideas" from collaborative student discourse, link them to "Synthesis Nodes" from course readings, and assign epistemic relations. [arXiv](https://arxiv.org/html/2602.06194) This represents a direct implementation of LLM-mediated bidirectional coupling between structured knowledge graphs and prose annotation, informed by both discourse graph and argumentation theory.

---

## The bidirectional sync gap: why text-to-graph works but the loop doesn't close

The central finding of this research landscape is an **asymmetry**: text-to-graph extraction has become mature and commercially viable, while graph-to-text generation is advancing rapidly through LLMs, but **keeping the two synchronized as edits occur remains fundamentally unsolved**. No production system maintains automatic, real-time bidirectional sync between a prose document and a knowledge graph where edits to either propagate to the other.

Microsoft's **GraphRAG** (open-sourced July 2024) exemplifies the state of the art on the extraction side. It uses LLMs to extract entities, relationships, and claims from text corpora, applies community detection via the Leiden algorithm, and builds hierarchical summaries for retrieval. [GitHub](https://microsoft.github.io/graphrag/) [Neo4j](https://neo4j.com/blog/developer/llm-knowledge-graph-builder-release/) But GraphRAG is a one-way pipeline — it does not regenerate text from graph modifications or propagate graph edits back to source documents. Neo4j's LLM Knowledge Graph Builder (launched June 2024, major 2025 update) similarly operates text-to-graph with provenance links but no reverse channel. [Neo4j](https://neo4j.com/labs/genai-ecosystem/llm-graph-builder/) [Medium](https://medium.com/neo4j/llm-knowledge-graph-builder-first-release-of-2025-532828c4ba76) Pan et al.'s landmark survey "Unifying Large Language Models and Knowledge Graphs: A Roadmap" (2023–2024) articulates a "Synergized LLMs+KGs" framework where both play equal roles in bidirectional reasoning, but most implementations remain prototypes. [Openkg](https://lkm2024.openkg.org/) [arXiv](https://arxiv.org/abs/2306.08302)

The **Open Research Knowledge Graph (ORKG)** from TIB Hannover comes closest to bidirectional coupling in the scientific domain. Its Reviews feature lets researchers write narrative Markdown alongside inline references to structured ORKG content types — comparisons, contribution data, auto-generated visualizations — all within the same authoring interface. [Teodora Petkova](https://www.teodorapetkova.com/taming-academic-text-the-open-research-knowledge-graph/) AI-assisted curation using SciBERT and LLMs (GPT-3.5, Llama 2, Mistral) suggests properties for new contributions, while a five-stage human-machine hybrid workflow (SciMantify, 2025) supports annotation. [Emergent Mind](https://www.emergentmind.com/topics/open-research-knowledge-graph-orkg) But the sync relies heavily on human curation rather than automated propagation.

**ARTIST** (Huber and Niklaus, INLG 2025), a writing support system from the University of St. Gallen, [ACL Anthology](https://aclanthology.org/2025.inlg-demos.4.pdf) gets surprisingly close to the dual-view ideal. It analyzes argumentative text and simultaneously displays an interactive argumentation graph showing claims, major claims, and premises, plus an RST parse tree. Users click on graph subgraphs to highlight corresponding text passages. [aclanthology](https://aclanthology.org/2025.inlg-demos.4.pdf) Students using ARTIST wrote essays with **5.1 arguments on average versus 3.2 with baseline tools**, and produced more persuasive writing. [aclanthology](https://aclanthology.org/2025.inlg-demos.4.pdf) However, bidirectionality appears limited — the graph updates from text, but it's unclear whether graph edits propagate back to prose.

**Graphologue** (UIST 2023) suggests one architectural path forward: it converts LLM text responses into interactive node-link diagrams in real-time (97.2% F-score for entity extraction with GPT-4), and users can modify diagrams and request new text through those modifications, creating a partial feedback loop. This LLM-mediated extract-modify-regenerate cycle may be the most viable near-term path to practical bidirectional coupling.

---

## Commercial research tools are building structured representations, but not graphs writers can edit

The commercial research tool space has evolved significantly since 2023, though the structural representations these tools maintain are primarily **tabular and agent-mediated rather than graph-based and user-navigable**.

**Elicit** (5M+ users, [Scrollwell](https://scrollwell.com/guide/tools/elicit-ai-research-tool-review-new-features-2025/) $22M Series A) has moved furthest beyond simple retrieval. Its systematic review workflow automates screening and data extraction across up to 20,000 data points. [Scrollwell](https://scrollwell.com/guide/tools/elicit-ai-research-tool-review-new-features-2025/) Research Reports (February 2025) auto-generate customizable briefs [Elicit](https://support.elicit.com/en/articles/1475137) following PRISMA structure from up to 80 papers. [Elicit](https://elicit.com/) [Deakin University](https://deakin.libguides.com/AI-Evaluations/Elicit) Most significantly, **Research Agents** (December 2025) represent a shift to agentic AI that conducts multi-step investigations across complex topics, [Scrollwell](https://scrollwell.com/guide/tools/elicit-ai-research-tool-review-new-features-2025/) building temporary structured models of research domains. These agents maintain structured internal representations of research landscapes, but users interact with them through prompts and reports rather than navigating or editing the underlying structure directly.

**Consensus** (8M+ users, 220M+ papers) launched its Scholar Agent in 2025 — a multi-agent system with distinct Planning, Search, Reading, and Analysis agents built on GPT-5. Its Research Gaps Heat Map visualizes areas with limited research, and its Results Timeline shows chronological evolution. [Consensus](https://help.consensus.app/en/articles/11954907-consensus-product-changelog) **Scite.ai** (acquired by Research Solutions) offers the richest structured citation representation: **1.6B+ citations classified as supporting, contrasting, or mentioning** across 280M+ sources, [Scite](https://scite.ai/) [Scite](https://scite.ai/reports/scite-a-smart-citation-index-keppkgL5) with the Scite Index providing a next-generation ranking metric based on citation context rather than volume. [Scite](https://scite.ai/rankings)

**Tana** stands out among knowledge management tools as the closest to "writing environment as graph editor." Every note and bullet is a node in a knowledge graph; supertags turn unstructured notes into typed objects [Sika](https://www.superbcrew.com/tana-connects-your-notes-tasks-and-ideas-into-one-ai-powered-workspace/) (#claim, #question, #source). [Tool Finder](https://toolfinder.com/alternatives/logseq) The outline editor *is* the graph editor — information entered in one view automatically appears in filtered views. [Tana](https://tana.inc/knowledge-graph) **InfraNodus** takes a complementary approach, representing text as networks to identify topical clusters, structural gaps, and trends, with a GPT-powered chatbot that reasons over the graph structure. [Infranodus](https://infranodus.com)

**ResearchRabbit** and **Litmaps** merged in October 2025, combining citation-network mapping with semantic search. [Effortless Academic](https://effortlessacademic.com/litmaps-vs-researchrabbit-vs-connected-papers-the-best-literature-review-tool-in-2025/) [Substack](https://aarontay.substack.com/p/researchrabbits-2025-revamp-iterative) **Undermind** (Y Combinator-backed) [PubMed Central](https://pmc.ncbi.nlm.nih.gov/articles/PMC12352444/) [Y Combinator](https://www.ycombinator.com/companies/undermind) organizes papers into "scientific domain-expert models" — structured, query-specific knowledge models enabling expert-level reasoning. [Undermind](https://www.undermind.ai/enterprise) **Iris.ai** raised €7.64M for enterprise R&D workflows [Tech.eu](https://tech.eu/2024/05/29/iris-ai-raises-7-64m-to-enhance-its-ai-engine-to-accelerate-scientific-research/) with agentic RAG capabilities. [Tracxn](https://tracxn.com/d/companies/iris.ai/__0r5tCqeOMRId5QnOcyRoFicLN93c-lTaOMSCwFTcESc) None of these tools, however, offer explicit argument maps or discourse graphs that writers can navigate and build upon while composing prose.

---

## Digital humanities converges on LLM-ontology integration

The intersection of knowledge graphs and LLMs for humanities scholarship has produced its first systematic methodologies. **ATR4CH** (Schimmenti et al., arXiv 2025) provides a five-step methodology for LLM-based knowledge extraction from cultural heritage texts, combining annotation models with CIDOC-CRM, HiCo, and PROV-O ontologies. [arXiv](https://arxiv.org/abs/2511.10354) [arXiv](https://arxiv.org/pdf/2511.10354) Applied to authenticity debates about disputed artifacts, it achieved 0.96–0.99 F1 for metadata extraction and 0.65–0.75 F1 for hypothesis extraction. [arXiv](https://arxiv.org/abs/2511.10354) Crucially, it treats annotation, knowledge extraction, and ontology alignment as interdependent processes [arXiv](https://arxiv.org/pdf/2511.10354) and explicitly handles scholarly disagreement and multi-perspectival interpretive structures.

The **Pelagios Network** held a landmark Lorentz Center workshop in September 2025 ("Enriching Digital Heritage with LLMs and Linked Open Data"), representing one of the first concrete efforts within a major DH infrastructure project to integrate LLMs with Linked Open Data workflows. [GitHub](https://github.com/pelagios/llm-lod-enriching-heritage) A GitHub repository of Python recipes for combining LLMs with LOD for cultural heritage metadata enrichment resulted. [Hcommons](https://linkedpasts.hcommons.org/2025/11/09/linked-pasts-11-programme/) The **Semantic Digital Humanities (SemDH) workshops** at ESWC 2024 and 2025 have established a regular venue for this work, [Semdh](https://semdh.github.io/) with papers on prosopographical knowledge graphs for Irish history, semantic representation of Egyptian demonology, and the Viewsari KG of Vasari's *Lives*. [CEUR-WS.org](https://ceur-ws.org/Vol-3724/)

The **LODinG initiative** (presented at LREC-COLING 2024) explores Linked O

## ChatGPT

I’m pulling out the parts that are actually reusable versus the parts that are just “interesting landscape.” The key filter is: does it help with atoms, sync mechanics, UI, or evaluation?

## User

Integrate with Opus here
'''
You're right, I jumped straight to responding to the critique without actually unpacking what the research turned up. Let me do that.

The most important finding, to me, is that **Joel Chan's discourse graph program is much further along than I expected.** The QCE atoms (questions, claims, evidence) with typed rhetorical edges aren't just a paper — they're deployed plugins for Roam and Obsidian with actual users. And the Discourse Grammar Parser that converts informal prose outlines into formal graph relations is a partial implementation of exactly the prose→graph direction we were calling unsolved. It's not real-time continuous parsing, but it's not nothing. Chan's group has also been studying actual scholar workflows (23 scholars in the UIST 2024 study), which means there's empirical data on how people actually use these things rather than just how we imagine they would.

The thing I didn't know about and find most interesting is **eRST** — Enhanced Rhetorical Structure Theory (Zeldes et al., *Computational Linguistics* 2025). Classical RST forces discourse relations into trees. eRST allows graphs — tree-breaking, non-projective, concurrent relations. That's exactly the structural limitation we identified: humanities knowledge has overlapping, multi-perspectival structure that trees can't capture. And there's a 200K+ token annotated corpus across 12 genres. This is the kind of thing that could serve as the formal backbone for GPT's "rhetorical update channel" — it's not speculative, it's got annotated data you can train against.

**Nanopublications** I knew about vaguely but the ARPHA Writing Tool detail is new and significant: biodiversity researchers can highlight findings in manuscripts and export them as structured nanopublications *inline, while writing*. That's a real bidirectional interface in production, even if it's narrow domain. The proposed extension to a fourth "knowledge provenance" component for assertions derived from bodies of evidence (not single sources) is directly relevant — it's the difference between "Smith 2019 found X" and "the weight of evidence suggests X based on [Smith 2019, Jones 2020, the absence of contradicting findings]."

**ARTIST** (INLG 2025) is the sleeper hit in the report. Students using the tool that shows an argumentation graph alongside their text wrote essays with 5.1 arguments vs 3.2 with baseline tools. That's a 60% increase. It's a small study (I'd want to know N, and it's probably small), but it's *actual evidence* that a dual-view interface improves output quality. Not just "writers liked it" but measurable structural improvement in the writing. If that replicates, it's the strongest empirical support for the whole program.

**What surprised me by its absence:**

Nobody seems to be doing the Latour-informed version of this. The cultural heritage KG work (ATR4CH) handles scholarly disagreement and multi-perspectival interpretation, which is Latourian in spirit, but nobody is explicitly modeling the enrollment/ally-accumulation process — how a claim gains strength by being connected to more and better evidence nodes, instruments, institutional contexts. That's still our contribution if we want it$_{65\%}$.

The **graph-to-text direction** is genuinely underdeveloped. Microsoft GraphRAG, Neo4j's builder — they're all one-way extraction pipelines. The closest thing to closing the loop is Graphologue's extract-modify-regenerate cycle, which is clunky (you extract, the user edits the graph, then you regenerate text). Nobody has continuous propagation.

**What this changes about our thinking:**

I think the biggest implication is that we don't need to invent the atoms. Chan's QCE + eRST's discourse relations + nanopublication-style provenance structures give you a workable type system that's already been iterated on empirically. The open problem isn't "what are the primitives" — it's "how do you keep them synchronized with live prose in a way that pays rent." Which is exactly where GPT's critique landed too, from a different direction.

The second implication: the **beachhead domain should probably be argumentative academic writing**, not fiction, not Pynchon. That's where Chan's tools already have users, ARTIST has positive results, nanopublications have infrastructure, and eRST has training data. The fictional/literary version is harder and less well-supported by existing work. It could come later, once the basic sync machinery exists.

Third: **Tana is worth looking at seriously** as an existing substrate. Their "every node is a graph node, supertags make typed objects" model is closer to "writing environment as graph editor" than anything else commercial. If someone were to build a discourse-graph-aware writing tool, building it *on* Tana or something with Tana's architecture might save years vs. building from scratch.

What's your read? Anything in the report that jumped out differently for you, or threads you want me to pull harder on?

## ChatGPT

Yes. The main integration move is to take Opus's correction seriously: **do not treat "the graph" as one thing**. Separate the boring, durable substrate from the richer interpretive overlays. Here’s a merged version that folds that in.

You're right, I jumped straight to responding to the critique without actually unpacking what the research turned up. Let me do that.

The biggest update is that **Joel Chan's discourse graph program is further along than I expected, and it sharpens where the real bottleneck is**. The QCE atoms, questions, claims, evidence, with typed rhetorical edges, are not just a theory paper. They exist as deployed plugins for Roam and Obsidian with actual users. The Discourse Grammar Parser, which turns informal prose outlines into formal discourse graph relations, is already a partial implementation of the prose → graph direction we were treating as mostly unsolved. It is not continuous real-time sync, but it is a real bridge between natural writing and structured representation. Chan's group has also studied actual scholar workflows, including the 23-scholar UIST 2024 study, so this is not pure armchair design.

What that changes is that I no longer think the first problem is "invent the atoms from scratch." **There is already a workable boring substrate**: questions, claims, evidence, plus typed relations. That is enough to build on. The harder problem is keeping those structures synchronized with live prose in a way that is trustworthy, low-friction, and actually useful while writing.

The other major thing I did not know about is **eRST**. That feels important, but in a slightly different way than I first thought. Classical RST forces discourse relations into trees; eRST allows graphs, including tree-breaking, non-projective, and concurrent relations. That matters because it gives a formal, empirically grounded vocabulary for discourse organization that can represent overlapping structure rather than flattening it into a single hierarchy. There is also a substantial annotated corpus behind it. But I now think eRST is probably **not the primary atom layer**. It looks more like the right formalism for a **rhetorical/discourse overlay** on top of a simpler substrate. In other words: QCE for the durable object layer, eRST for the way spans of prose relate rhetorically.

That distinction feels important. A sentence often does **not** add a new world-fact. It may hedge, contrast, reframe, attribute, concede, or temporarily entertain a possibility. So a prose-to-graph system probably needs at least separate update channels for:

referential updates, who/what this is about

commitment updates, what is now asserted, denied, or left open

rhetorical updates, contrast, support, elaboration, concession

epistemic updates, certainty, attribution, evidential status

That is the part I was compressing too much before. The problem is not just "extract claims." It is **incremental commitment tracking with scope and attribution**.

**Nanopublications** now look more important to me for the same reason. What stands out is not just that they treat assertions as small citable units, but that they bundle assertions with provenance and publication metadata, and that tools like ARPHA let researchers highlight manuscript findings inline and export them as nanopublications while writing. That is a real dual-view interface in production. The proposed move toward a fourth "knowledge provenance" component is especially relevant, because it captures the difference between a claim tied to a single source and a claim that emerges from a body of evidence. That feels very close to what we'd need if we want the graph to represent not just isolated assertions but **why** the writer is treating something as established.

So one thing Opus's critique helped crystallize is this: **provenance is not a side detail, it is the trust layer**. Every graph update would need source span, attribution, scope, confidence, and easy reversibility. Without that, the graph becomes haunted. Things appear in it, but the writer cannot tell where they came from or what ontological status they have.

**ARTIST** is probably the strongest empirical support in the report. A tool that shows an argument graph alongside prose, and lets users move between them, led students to write essays with more arguments on average than baseline tools. I still want sample size and replication before leaning too hard on it, but it is actual behavioral evidence that a dual-view interface can improve structural quality, not just user satisfaction. That matters a lot. It suggests the graph does not need to be fully automatic to pay rent. It just needs to surface useful structure while the writer is working.

What I now take from ARTIST, Tana, and Chan together is a UI principle: **the writer should stay in prose or outline mode by default**. The graph should not be a separate kingdom that the user has to manually maintain. The right model looks more like lightweight formalization, highlight a span, type it as claim/evidence/question, see corresponding graph neighborhood, surface tensions, jump back to text. Tana is relevant here because it already treats outline items as graph nodes with type-like supertags. That may be closer to the correct substrate than a bespoke graph editor from scratch.

The part of the landscape that still looks genuinely underdeveloped is the **reverse channel**. GraphRAG, Neo4j's builder, most KG extraction tools, they are one-way text → graph pipelines. Graphologue gets closer with extract → modify diagram → regenerate text, but that is still a mediated loop, not continuous synchronization. So I think the asymmetry still stands: extraction has gotten good enough to deploy, graph-conditioned generation is advancing, but **authoritative shared-state sync** between prose and graph is still missing.

That shifts the frontier. I do not think the core open question is "what are the primitives?" anymore, at least not for a first system. It is more like:

**How do you maintain a shared commitment model across prose and graph, with provenance, salience, and reversibility, without turning the writer into a database janitor?**

That also clarifies the domain question. The beachhead probably should be **argumentative academic writing**, not fiction and not Pynchon. That is where the atoms are clearer, the evaluation is more tractable, the existing tools and corpora are strongest, and the graph can pay rent immediately by helping with claims, evidence, contradictions, open questions, and citation-backed structure. Fiction still seems interesting, but as a second domain after the sync machinery exists.

One thing that still feels absent from the literature is the more explicitly **Latourian** version of the problem. There is work on disagreement, provenance, and multi-perspectival interpretation, especially in cultural heritage KGs, but not much that directly models how a claim gains force by accumulating allies, sources, instruments, institutions, replications, absence of contradiction, and so on. Nanopublication-style provenance and discourse graphs get partway there, but the explicit "claim strength as networked enrollment" layer still seems underbuilt. That may still be a real contribution.

So my current synthesis would be:

**Do not invent exotic atoms for v1.** Start with QCE plus source/provenance.

**Treat discourse and rhetoric as overlays, not the substrate.** eRST is useful here.

**Treat provenance, scope, and reversibility as first-class.**

**Assume the graph alone is not enough.** You also need salience and control, meaning what is currently active and what the writer is trying to do.

**Target argumentative research writing first.**

**Design the interface so the graph earns its existence in live cognition.**

That is a different emphasis than where I started. The novelty is no longer "maybe there could be a graph for prose." That part already exists in fragments. The novelty is building a system where prose and graph are **two synchronized views of commitments**, and where the synchronization is good enough that writers trust it and keep using it.

That version is tighter and more correct. The biggest gain is replacing "we need the right atoms" with "we need a trustworthy sync architecture over a boring typed substrate."

Become a little more present for a second. Unclench your jaw.

