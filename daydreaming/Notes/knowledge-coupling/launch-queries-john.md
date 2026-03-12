# Deep Research Queries — For John's Track

Date: 2026-03-12

Four queries refined from the original six threads in
`../DeepResearch/2026-03-12/john/asks/threads_from_chat.md`.

Two original threads dropped (Double Graph Problem — already answered
by bidirectional coupling research; Literary vs Infrastructure — an
integration question, not a research question). Two reshaped to avoid
re-covering ground the existing research already covers.

See `established-findings.md` for what we already know from prior research.

**Note on query format:** These queries are intended for web-based deep
research tools (e.g., Gemini Deep Research, Perplexity Pro). They ask
about external tools, papers, products, and ecosystems — not about our
codebase. The answers require web search, GitHub repos, product docs,
academic papers, and user reports. Nothing in our repo can answer these.

---

## Query 1: PKM-to-LLM Docking Pipelines (practical)

> **This is a web research question — search the internet, GitHub,
> product documentation, and community forums.**
>
> What tools, MCP servers, and pipelines currently exist for feeding an
> Obsidian or Markdown-based personal knowledge vault into LLM
> conversations as live context? Search for: Obsidian MCP servers on
> GitHub, Claude Code's CLAUDE.md and memory systems (Anthropic docs),
> Cursor's context loading documentation, Gemini's document grounding
> API, and any open-source projects that treat a PKM as a structured
> context source rather than a RAG corpus. Search for Peter Danovich's
> work building a textbook into Gemini — what did he build, what were
> the practical results, and is there a writeup or repo? Focus on
> what's deployed and usable today, not academic prototypes. Include
> GitHub stars, last commit dates, and maturity indicators where
> available.

**Why:** The existing research covered the scholarly/academic side
(discourse graph protocol, nanopublications, ORKG). It did NOT cover
the practical tool side — what's actually pluggable today between an
Obsidian vault and an LLM session.

**What we already know:** See established-findings.md §Docking.

---

## Query 2: Graphiti Specifically

> **This is a web research question — search GitHub, product docs, user
> forums, benchmarks, and technical blog posts.**
>
> Deep dive on Graphiti (by Zep, https://github.com/getzep/graphiti).
> What does it actually do vs. what does its marketing claim? Search
> for: architecture and Neo4j dependency, entity resolution quality
> (user reports, not just docs), temporal tracking capabilities in
> practice, community detection implementation, performance benchmarks
> at different scales (100 nodes vs 10K vs 100K). Search for
> comparisons and user experiences: Graphiti vs. Microsoft GraphRAG
> (https://github.com/microsoft/graphrag), vs. LlamaIndex property
> graph index, vs. LangChain's graph stores, vs. plain inference-time
> semantic matching where an LLM reads flat files with no persistent
> graph. Search Reddit, Discord, Hacker News, and GitHub issues for
> honest assessments from users who have deployed Graphiti in
> production. What's the smallest project size where the graph overhead
> pays for itself? What are the failure modes and scaling limits?

**Why:** John made a strong claim that Graphiti/KGs are superior to
vector DBs for personal knowledge. This needs pressure-testing with
real-world evidence. Our existing research showed that inference-time
matching (no persistent graph) sidesteps the bidirectional sync problem
entirely. Need to know where the graph overhead actually pays off.

**What we already know:** See established-findings.md §Sync Gap. The
research established that text-to-graph extraction is mature but
bidirectional sync is unsolved. GraphRAG is one-way. Neo4j's LLM KG
Builder is one-way. This query should find where one-way extraction
still provides enough value to justify the infrastructure.

---

## Query 3: Graph Intersection for Personalized Learning

> **This is a web research question — search academic papers (Google
> Scholar, Semantic Scholar, ArXiv), education technology platforms, and
> research group pages.**
>
> What research or implementations exist where a personal knowledge
> graph is intersected or aligned with an external domain ontology or
> curriculum graph to find learning gaps? Search for: ontology alignment
> algorithms applied to education (OAEI benchmarks, LogMap, AML),
> graph intersection techniques for adaptive learning, any systems
> where a "teaching agent" arrives with its own knowledge graph and
> queries a learner's graph to find the shortest path between current
> knowledge and target knowledge. Search for how existing adaptive
> learning platforms handle this — Knewton (now Wiley), ALEKS
> (McGraw-Hill), Carnegie Learning's MATHia, and Open Learning
> Initiative. Is anyone doing this specifically with LLM-extracted
> knowledge graphs rather than hand-authored curriculum ontologies?
> Search for recent papers (2023-2026) combining knowledge graphs +
> LLMs + personalized learning.

**Why:** This is John's most novel idea — docking as graph
intersection. Our existing research didn't touch it. Needs independent
validation from the academic and edtech literature.

**What we already know:** Nothing specific. The bidirectional coupling
research mentions ontology alignment in passing but doesn't cover the
personalized-learning application.

---

## Query 4: Temporal Knowledge Graphs — What Actually Exists

> **This is a web research question — search product documentation,
> GitHub repos, academic papers, and user experience reports.**
>
> What temporal knowledge graph systems are actually deployed and usable
> (not just proposed in papers)? Search for: Graphiti's temporal
> features (search GitHub issues and docs for how they work in practice,
> not just marketing), TigerGraph's temporal capabilities, Neo4j
> temporal extensions and plugins, TerminusDB's versioning model. For
> personal knowledge specifically: search for systems that track how a
> person's understanding of a concept evolved over time by observing
> changes in their knowledge graph. Search for: spaced repetition
> systems that use graph structure (not just flashcard scheduling),
> knowledge decay detection via graph topology changes, connections to
> learning analytics platforms (xAPI, Caliper). Search academic
> literature (2023-2026) for "temporal knowledge graph" + "learning" or
> "personal knowledge." Separate clearly what is deployed and usable
> from what is roadmap or vaporware.

**Why:** John called temporal awareness "the Holy Grail" for tracking
how understanding evolves. Our existing research didn't cover temporal
KGs. Need to know what actually exists vs. what's aspirational so we
don't build toward a capability that's already available or, conversely,
assume something exists that doesn't.

**What we already know:** Nothing specific to temporal KGs.

---

## Launch Priority

1. **Query 2** (Graphiti) — highest leverage, determines whether
   John's core infrastructure bet is sound
2. **Query 1** (docking pipelines) — practical, immediately actionable
3. **Query 3** (graph intersection) — validates John's most original idea
4. **Query 4** (temporal KG) — important but less urgent
