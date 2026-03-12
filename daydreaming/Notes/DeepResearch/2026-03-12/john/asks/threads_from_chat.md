# Deep Research Threads — From John's Chat Notes

Source: `daydreaming/Notes/PreviousWork/outside/notes_team.md`
Date: 2026-03-12

David's notes from a conversation with John about PKM-to-LLM docking,
plus John's response pivoting toward Graphiti/knowledge graphs. The
top half is the original conversation notes; the bottom half is John
moving toward concrete infrastructure.

---

## Context Summary

**David's framing:** How does a PKM specifically supply context to an
LLM conversation happening elsewhere (situated inferencing)? Standards
already exist. Peter Danovich built a textbook into Gemini. The dream:
a Socratic exchange where your personal knowledge docks with an
objective knowledge base and the LLM finds the ideal path for you.

**John's pivot:** Vector DBs treat your vault as "soup of paragraphs."
Knowledge graphs treat it as a network of concepts and relationships.
Frames become literal subgraphs. Temporal awareness tracks how
relationships evolve. "Docking" becomes graph intersection — an
external teaching agent arrives with its own KG, queries yours, finds
the shortest path between what you know and what it wants to teach.
Entity resolution handles messy-notes-to-clean-concepts automatically.
The Double Graph Problem: Obsidian's wikilink graph vs. Graphiti's
semantic graph — keeping them in sync is hard.

**Underlying tension:** The Symbiotic Vault design (`_design/` docs)
went literary — frames as interpretive lenses, the seance, the drift.
John went infrastructural — Neo4j, graph intersection, entity
resolution. They solve the same problems from opposite angles.

---

## Thread 1: PKM-to-LLM Docking Standards

> What existing standards, protocols, or published approaches exist for
> connecting personal knowledge management systems (Obsidian, Notion,
> Roam) to LLM inference? Include MCP servers, RAG pipelines, knowledge
> graph integrations, and any academic work on "situated context" for
> conversational AI. What did Peter Danovich's textbook-to-Gemini
> project actually do and what were the results?

**Why this matters:** This is the most directly stated need from the
original conversation — "already standards for this." Before building
anything, know what exists.

**Priority:** High — foundational for everything else.

---

## Thread 2: Knowledge Graphs vs. Vector DBs for Personal Knowledge

> Compare Graphiti/Zep, Neo4j-backed knowledge graphs, and vector
> database approaches (ChromaDB, Pinecone) for representing personal
> knowledge bases in agentic systems. What are the tradeoffs for:
> semantic search, relationship traversal, temporal evolution tracking,
> entity resolution, and subgraph/community detection? Include
> benchmarks, case studies, and failure modes.

**Why this matters:** John made a strong claim that KGs are superior
to vector DBs for this use case. Research should pressure-test that
claim with evidence.

**Priority:** High — determines infrastructure direction.

---

## Thread 3: Graph Merging as a Docking Mechanism

> What research exists on merging or intersecting knowledge graphs from
> different sources — specifically, aligning a personal knowledge graph
> with a curriculum or domain ontology? Include work on ontology
> alignment, graph intersection algorithms, and any applications to
> personalized learning or Socratic tutoring systems. How does
> Graphiti's entity resolution compare to dedicated ontology matching
> tools?

**Why this matters:** This is the most novel idea in John's response —
docking as graph intersection. An external teaching agent arrives with
its own KG and finds the gap between what you know and what it wants
to teach. Worth knowing if anyone has actually done it.

**Priority:** Medium-high — novel and differentiating if it works.

---

## Thread 4: The Double Graph Problem

> In systems where both a human-maintained link graph (like Obsidian
> wikilinks) and an AI-maintained semantic graph coexist, what
> approaches exist for synchronization? What happens when they diverge?
> Are there Obsidian plugins or similar tools that bridge explicit user
> links with inferred semantic relationships? Include the "backlink
> injection" pattern (agent writes wikilinks into markdown files).

**Why this matters:** This is a concrete engineering problem that
likely has existing solutions or known failure modes. Obsidian's graph
is visible to the user; Graphiti's graph is invisible. They will
diverge. How do you handle that?

**Priority:** Medium — practical engineering, needs answers before
building.

---

## Thread 5: Temporal Knowledge Graphs for Learning

> What systems track the evolution of a learner's understanding over
> time using knowledge graphs? Include Graphiti's temporal features,
> spaced repetition systems that use graph structure, and any research
> on "knowledge decay detection" or "concept mastery tracking" via
> graph topology changes.

**Why this matters:** John called temporal awareness "the Holy Grail"
for learning — the ability to track not just what you know but how
your understanding evolved. Worth knowing what actually exists vs.
what's aspirational.

**Priority:** Medium — important for the learning use case
specifically.

---

## Thread 6: Literary vs. Infrastructure Architectures

> Compare agent architectures that use explicit knowledge graph
> infrastructure (Neo4j, Graphiti, entity extraction pipelines) with
> approaches that rely on LLM semantic understanding at inference time
> (no persistent graph, context assembled per-query from flat files).
> What are the tradeoffs in maintenance burden, accuracy, emergent
> behavior, and creative applications? When does the infrastructure
> overhead pay for itself?

**Why this matters:** This is the strategic question underneath all
the tactical ones. The Symbiotic Vault design relies on inference-time
semantic matching (the agent reads flat markdown files and understands
them). John proposes building explicit graph infrastructure. These are
fundamentally different bets. When does the infrastructure overhead
pay for itself, and when does it just add complexity without improving
outcomes?

**Priority:** High — this is the decision point. Threads 2-5 feed
into this question.

---

## Recommended Launch Order

1. **Thread 1** (docking standards) — foundational, scopes the field
2. **Thread 2** (KG vs vector) — validates or challenges John's thesis
3. **Thread 6** (literary vs infrastructure) — the strategic question
4. **Thread 3** (graph merging) — the novel mechanism
5. **Thread 4** (double graph) — engineering reality check
6. **Thread 5** (temporal KG) — specific to learning applications
