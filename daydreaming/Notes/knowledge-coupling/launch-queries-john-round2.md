# Deep Research Queries — Round 2

Date: 2026-03-12

Two queries picking up Thread 5 and Thread 6 from
`../DeepResearch/2026-03-12/john/asks/threads_from_chat.md`.

Round 1 (`launch-queries-john.md`) sent four queries. Replies came back
for all four (see `../DeepResearch/2026-03-12/john/replies/`). These two
queries build on what Round 1 established and target what it left open.

See `established-findings.md` for the full prior knowledge base.

**Note on query format:** Same as Round 1 — these are intended for
web-based deep research tools. They ask about external research, tools,
and evidence. Nothing in our repo can answer these.

---

## What Round 1 Established (context for both queries)

- **File-first docking is state of the art** for current-scale PKM use.
  Claude Code's CLAUDE.md + memory + MCP is the strongest practical
  implementation. (Query 1 reply)
- **Graphiti overhead doesn't pay for small-to-medium vaults.** The
  crossover is roughly: recurring entities + changing facts + repeated
  multi-hop queries, all three at once. Below that, flat-file semantic
  matching wins. (Query 2 reply)
- **Graph intersection for personalized learning exists as a composition
  of known pieces** (curriculum graph + learner overlay + ontology
  alignment + path planning) **but not as a deployed product.** The
  literature says "overlay/projection," not "intersection." Shortest
  path is pedagogically wrong — best feasible mastery path under
  constraints is the real target. (Query 3 reply)
- **Temporal KG infrastructure exists** (Graphiti, Neo4j, TerminusDB)
  **but temporal learner models barely do.** The reply found Recall,
  RemNote, Math Academy, and a handful of 2024–2026 prototypes. The
  key correction: the valuable temporal object is tracked change in
  commitments/salience/confidence, not timestamped edges. (Query 4 reply)

---

## Query 5: Learner Modeling Without Explicit Assessment

Thread 5 from the original asks. Query 4 covered the infrastructure side
(what temporal KG systems exist). This query targets what Query 4 flagged
as thin: the learning-specific side.

> **This is a web research question — search academic papers (Google
> Scholar, Semantic Scholar, ArXiv, ACM DL, ERIC), learning analytics
> platforms, and open-source projects.**
>
> What methods exist for inferring a learner's evolving understanding
> of a topic from their **own knowledge artifacts** (notes, writings,
> linked concepts, revised claims) rather than from explicit assessments
> (quizzes, tests, flashcard recall)? Search for:
>
> - Systems that detect **conceptual change** by observing how a
>   learner's notes, links, or written arguments evolve over time.
>   Include work on "knowledge tracing from writing," "epistemic
>   network analysis," and "learning analytics from student artifacts."
>
> - **Epistemic Network Analysis (ENA)** specifically — Shaffer/Ruis
>   at UW-Madison. How does ENA model the co-occurrence of epistemic
>   elements in discourse, and has anyone applied it to personal
>   knowledge artifacts (not just classroom transcripts)?
>
> - Graph-topology methods for detecting **knowledge gaps, decay, or
>   restructuring** — e.g., changes in centrality, new cluster formation,
>   bridge nodes appearing/disappearing, subgraph density changes.
>   Include network science applied to learning (not just social
>   network analysis).
>
> - **Spaced repetition systems that use graph structure** to schedule
>   review — not just per-card forgetting curves but systems where
>   prerequisite relations, concept dependencies, or graph distance
>   influence what gets reviewed and when. Include FSRS, any graph-aware
>   extensions, and any academic work on "graph-based scheduling."
>
> - Systems that infer mastery from **activity patterns** rather than
>   test performance — e.g., time spent, revision frequency, link
>   creation patterns, writing quality changes. Include learning
>   analytics work on "unobtrusive assessment" and "stealth assessment."
>
> Separate clearly: (a) methods that are deployed and validated,
> (b) published prototypes with evaluation results, and (c) proposals
> without empirical evidence.

**Why:** The Round 1 replies converged on a key claim: the hard part
of temporal learner modeling is not storing time on edges — it's
defining what counts as evidence that a learner knows, half-knows,
forgot, or restructured something. This query asks what the field
has actually tried beyond explicit assessment.

**What we already know:** Query 4 found that deployed systems (Math
Academy, ALEKS, MATHia) rely on quiz/test performance, not artifact
analysis. The UMAP 2024 paper lets learners self-mark "Did Not
Understand." The WeChat Mini Program 2025 uses graph propagation +
forgetting. The established-findings doc says the converged v1
architecture needs evaluation metrics including "correction rate,
acceptance rate, time-to-resolve-tension, whether users keep the
graph alive after drafting." None of this tells us whether artifact-
based mastery inference actually works.

**What we specifically do NOT need:** More information about Graphiti's
temporal features, Neo4j, TigerGraph, or TerminusDB (covered in
Query 4). More information about spaced repetition flashcard scheduling
in isolation (well-known). More information about knowledge tracing
from test scores (BKT, DKT, etc. — not the question).

---

## Query 6: When Does Inference-Time Assembly Break?

Thread 6 from the original asks. Dropped in Round 1 as "an integration
question, not a research question." Now that Round 1 replies establish
that flat-file inference-time matching is the practical default, the
question sharpens: **what makes it stop working, and what does the
transition architecture look like?**

> **This is a web research question — search academic papers, technical
> blog posts, production experience reports, and benchmark studies.**
>
> Compare two architectures for giving an LLM access to a personal or
> project knowledge base:
>
> **Architecture A — Inference-time assembly:** No persistent graph or
> index beyond the files themselves. The LLM reads relevant flat files
> (Markdown, text) at query time, relying on its own semantic
> understanding to find connections, detect contradictions, and maintain
> coherence. Context is assembled per-query from file search, glob
> patterns, or lightweight metadata (frontmatter, filenames). This is
> what Claude Code does with CLAUDE.md + memory + file reads.
>
> **Architecture B — Precomputed structure:** A persistent knowledge
> graph, property graph, or structured index is maintained alongside
> the files. Entity extraction, relationship typing, and possibly
> community detection run as batch or incremental processes. The LLM
> queries the graph for retrieval, path-finding, or gap detection
> rather than reading raw files.
>
> Search for:
>
> - **Empirical comparisons** of RAG-over-flat-files vs. GraphRAG or
>   structured-retrieval approaches. Include Microsoft's GraphRAG
>   benchmarks, any published A/B tests, and production experience
>   reports. What specific query types does graph structure help with?
>   (Multi-hop reasoning, contradiction detection, temporal queries,
>   "what would break if X changed?")
>
> - **Scale thresholds** where inference-time assembly degrades. At what
>   corpus size does an LLM reading flat files start missing connections
>   it would catch with a precomputed graph? Search for evidence about
>   context window utilization, retrieval recall at different corpus
>   sizes, and "lost in the middle" effects.
>
> - **Hybrid architectures** where flat files remain the source of truth
>   but a compiled index/graph serves as a retrieval accelerator. The
>   SNT project in our repo already does this (Markdown → SQLite
>   compiler → MCP server). What similar patterns exist in the wild?
>   Include any "compiled knowledge base" or "knowledge base as build
>   artifact" approaches.
>
> - **Maintenance burden** evidence. What do teams report about the
>   ongoing cost of maintaining a knowledge graph vs. just keeping
>   files organized? Include drift/staleness problems, entity
>   resolution maintenance, and the cost of keeping extracted structure
>   in sync with evolving source files.
>
> - **Creative and exploratory applications** specifically. Most
>   GraphRAG benchmarks use factual Q&A or summarization tasks. Has
>   anyone compared structured vs. unstructured retrieval for creative
>   work, ideation, writing assistance, or exploratory research? What
>   about serendipitous discovery — does precomputed structure help or
>   hinder finding unexpected connections?
>
> Focus on evidence, not advocacy. The graph-vs-flat debate has a lot
> of marketing noise. Prioritize published benchmarks, controlled
> comparisons, and production experience reports over blog posts and
> vendor claims.

**Why:** Every Round 1 reply landed on the same default: flat files
win for now. But none provided a sharp answer to "when does 'for now'
end?" The repo needs a decision framework, not a vibe. Specifically:
the SNT project already implements Architecture B (compiled graph
over flat files). The Symbiotic Vault currently uses Architecture A.
John proposes moving to Architecture B with Graphiti. The question is
not "which is better" but "what evidence should trigger the
transition, and what does the intermediate step look like?"

**What we already know:** Query 2 said Graphiti starts earning its keep
when recurring entities + changing facts + multi-hop queries converge.
Query 1 said the deployed ecosystem backs file-first. The established-
findings doc says one-way compilation sidesteps bidirectional sync. The
SNT project demonstrates the hybrid pattern (Markdown source of truth →
compiled SQLite → MCP). But none of this is backed by external
benchmarks or scale evidence — it's inference from system architecture.

**What we specifically do NOT need:** More Graphiti feature analysis
(covered in Query 2). More Obsidian MCP server inventory (covered in
Query 1). More ontology alignment literature (covered in Query 3).
General RAG tutorial content. Vendor marketing for graph databases.

---

## Launch Priority

1. **Query 6** (inference-time vs. precomputed) — this is the strategic
   decision the repo is currently deferring. Evidence here directly
   informs whether to keep deferring or start building.
2. **Query 5** (learner modeling from artifacts) — important for the
   personalized-learning track but less urgent than the architectural
   question.
