# Architecture Sanity Check — For External Review

Date: 2026-03-12

This document is a high-level summary of a research and design process,
packaged for critical review. The goal: decompose the problem, identify
where we might be stuck in a local minimum, and recommend the minimum
system that preserves a path to agent memory, learning overlays, and
execution without prematurely graphifying the creative vault.

---

## The Core Question

> How do we give a creative vault enough compiled structure to support
> LLM docking, agent memory, and future learner overlays without
> collapsing the vault itself into prematurely rigid graph
> infrastructure?

This is not "should we use Graphiti?" That question got over-indexed
because of a specific social path (see How We Got Here). The real
question is an architecture decomposition.

---

## The Problem Is Actually Four Problems

These are being conflated in the current conversation. They do not
require the same data model or the same tool.

### 1. Human Authoring Substrate
The creative vault itself: notes, atoms, frames, drafts, ambiguity,
live writing. Must preserve productive contradiction, low-friction
capture, and the writer's voice. The Symbiotic Vault's experiments
(Membrane, Drift, Séance, Compost, Table) all live here.

### 2. Docking / Query Substrate
How an LLM gets structured access to vault material. Needs to expose
enough structure for typed queries ("what supports this?" "what
changed?" "what would shift if X changed?") without requiring the
human to maintain a graph. Currently solved by inference-time file
reading. Could be upgraded to compiled index + MCP.

### 3. Agent Memory / Planning Substrate
Where the agent tracks recurring entities, changing facts, and
multi-hop relationships over time. This is where Graphiti/Cognee-class
systems might actually belong — not as the vault itself, but as a
separate memory layer the agent maintains.

For now, **execution** lives here too: skills, protocols, task state,
and planning/execution traces are treated as part of the agent layer
unless they later need to be split into their own substrate.

### 4. Learner Model / Curriculum Overlay
If the personalized learning idea becomes real. An overlay on an
authoritative curriculum graph, not a replacement for it. The vault
provides evidence of learning state, but it is not itself a mastery
model.

**Key insight: the vault, agent memory, and learner graph should
probably not be the same graph.**

---

## What Must the System Actually Do?

Success criteria, not tool preferences:

1. **Preserve creative ambiguity for the human author.** Atoms can be
   half-formed. Frames can disagree. Contradictions can be productive.
   The system must not force premature resolution.

2. **Expose enough structure for LLM docking and querying.** Typed
   questions like "what supports this claim?" or "what would shift if
   this source changed?" should be answerable without reading the entire
   vault.

3. **Support agent memory/planning without forcing the vault to become
   the memory system.** The agent needs to track entities, commitments,
   and changes. That doesn't mean the vault's Markdown needs to become
   a graph database.

4. **Preserve a path to learner overlays without pretending the vault
   itself is mastery.** Note presence is not mastery evidence. The
   system should track learning signals (assessments, self-ratings,
   revision behavior, successful recall) as an overlay, not assume the
   vault's topology equals understanding.

5. **Pay rent in live creative work.** Any structure that doesn't
   visibly help the writer during actual writing sessions is overhead.
   "The graph must earn the right to exist by paying rent in live
   cognition."

---

## How We Got Here

### The Social Path to Graphiti

1. **Jim White** mentioned Graphiti and Cognee in a Discord thread about
   agent memory and text2kg (March 10, 2026). He also flagged that
   Zettelkasten-style systems lack inference and execution. He pointed
   to Cognee (Neo4j-based, Vercel AI SDK integration), GraphMD
   (markdown-based executable knowledge graphs), and skills.md→KG
   compilation.

2. **John** picked up the Graphiti lead and had a model generate an
   enthusiastic analysis ("Using Graphiti is a brilliant choice"). The
   model framed Graphiti as solving entity resolution, temporal
   evolution, community detection (frames as subgraphs), and docking
   via graph intersection. John asked David to run deep research.

3. **David** independently ran a landscape report on bidirectional
   knowledge-text coupling (654 sources). Found: field converges on
   claims/questions/evidence as atoms, bidirectional sync is unsolved,
   inference-time matching may be the right near-term approach.

4. John fed that report to his vault agent. Agent produced good concrete
   suggestions (add `claim` to atom types) but dismissed bidirectionality
   without knowing it was the central thesis.

5. David ran deep research on five threads targeting John's thesis.
   Results summarized below.

6. A sixth thread — the strategic architecture question (file-first vs.
   graph-first, when to transition) — came back with the strongest
   evidence base. See Breakpoint Evidence.

### The Local Minimum Risk

Graphiti got over-indexed because Jim mentioned it, John ran with it,
and the conversation narrowed around one tool. That's a classic
anchoring effect. The instinct behind it (wanting explicit structure,
temporal awareness, docking capability) is sound. The mechanism
(Graphiti as the foundational bet) was anchored too early.

---

## Research Findings (Summary)

Six deep research threads, plus a prior landscape report. Full evidence
in the research package.

**Docking landscape:** File-first is state of the art. Claude Code's
CLAUDE.md + scoped memory + MCP is the strongest deployed pattern.
Obsidian MCP ecosystem fragmented, no winner.

**Graphiti pressure test:** Real but rough. Entity resolution heuristic
with no published benchmarks. Community detection OOMs at ~1k entities.
No scale benchmarks. mem0 competitive. Premature at current vault scale.

**Graph intersection for learning:** Each piece exists separately
(curriculum graphs, ontology alignment, LLM-based extraction, mastery-
aware path planning). Nobody has built the full stack from personal
PKM → curriculum alignment → tutoring. Genuinely novel contribution
opportunity. Refinements: learner graph as overlay, mastery-path not
shortest-path.

**Temporal KGs:** Infrastructure exists. Personal learner models don't.
Valuable temporal object is tracked change in commitments, not
timestamped facts. Build provenance first, temporal KG later.

**Learner modeling without assessment:** Artifact-based modeling works
when structured or instrumented enough. Passive PKM-as-mastery-graph
under-validated. Vault should be evidence stream, not self-certifying
mastery graph.

**Strategic architecture (Thread 6):** The strongest thread. Key
findings:
- Plain RAG beats graphs on single-hop/detail questions
- GraphRAG wins on multi-hop/reasoning and global sensemaking
- Graph construction is lossy (~65% entity coverage)
- Even Microsoft is moving toward hybrids (LazyGraphRAG: 0.1% of full
  GraphRAG indexing cost, comparable quality)
- Creative/exploratory evidence is thin — no benchmark covers this
  domain
- The breakpoint is NOT raw vault size — it's query type + reuse
  frequency + whether typed operations pay rent

### The Breakpoint Rule (from Thread 6)

> Flat-file assembly stops being enough when the cost of repeatedly
> re-deriving structure exceeds the cost of maintaining a compiled
> artifact. Not before.

**Stay file-first when:**
- Most questions are local, detail-heavy, frame-based, or exploratory
- Main pain is surfacing/cohabitation, not recall
- Ontology is still unstable
- You want Drift/Membrane/Compost/Table behaviors that rely on soft
  semantic movement and productive ambiguity

**Move to compiled layer when:**
- You keep asking typed questions that are annoying to recover from raw
  files ("what supports this?" "what contradicts?" "what changed?"
  "trace the dependency path")
- Same working set reused repeatedly (adjacent evidence: at ~100k
  tokens, persistent memory becomes cheaper after ~10 interaction turns)
- Substrate is boring enough to compile (claims/questions/evidence, not
  vibes)

**Move to full graph-first only when:**
- Multi-hop/global reasoning is a core workload, not occasional
- The graph itself becomes a user-facing surface
- You're willing to own extraction quality, rebuilds, staleness,
  pruning, entity resolution, and cost

---

## Adjacent Prior Art: SNT Project

David has a working implementation of the **docking/query substrate**
(layer 2) in legal research. The pattern:

- Markdown → SQLite compilation with typed edges (`supportedBy`,
  `overriddenBy`, `exceptionApplies`)
- MCP server: `get_claim(id)`, `trace_support()`,
  `what_breaks_if(node_id)`, `list_conflicts()`, `search()`
- Claims with epistemic status: `proven | provisional | open`
- One-way compilation — Markdown stays source of truth

**What transfers:** The plumbing (Markdown → index → MCP). Proof that a
compiled derivative layer can pay rent.

**What doesn't transfer:** The epistemology. Legal research has
authoritative sources, falsifiable claims, concrete "proven." Creative
work has journal entries, aesthetic commitments, productive
contradictions. Status types and edge types need rethinking for creative
domains.

---

## Candidate Architectures

To stay close to Query 06's terminology, this briefing uses:
`A = file-first only`, `A+ = compiled docking layer`, `B = A+ plus a
separate agent-memory layer`, `C = B plus learner overlay`.

This is slightly finer-grained than Query 06: it splits the heavier
"upgrade past A+" territory into a separate agent-memory move (B) and a
learner-overlay move (C).

The important move is that the first upgrade is **A+**, not a
graph-first vault.

### Architecture A: File-First + Inference-Time Only

Vault stays as Markdown files. LLM reads them at query time with
semantic matching. CLAUDE.md + scoped memory + rules provide context.
No persistent compiled structure.

**Solves:** Preserves ambiguity. Zero maintenance overhead. Ontology can
evolve freely. Already works at current scale.

**Breaks:** Can't answer typed multi-hop questions efficiently. No
persistent agent memory. Re-derives structure on every query. Probably
fails at scale (but threshold unknown).

### Architecture A+: File-First + Compiled Docking Layer + MCP

Vault stays as Markdown. A compiler builds a disposable, rebuildable
index (SQLite or similar) with typed structure. MCP server exposes
typed queries. The index is derivative, not authoritative.

**Solves:** Typed queries become cheap. Docking works end-to-end.
Markdown stays source of truth. Compilation can be incremental.
Validated by SNT prior art (in legal domain) and by the hybrid trend
in the literature (LazyGraphRAG, LlamaIndex PropertyGraphIndex).

**Breaks:** Doesn't provide agent memory (agent still starts fresh each
session unless separately solved). Doesn't provide learner modeling.
Compilation schema must be designed for creative work (not imported from
legal). May over-structure material that benefits from remaining loose.

### Architecture B: File-First + Compiled Docking + Separate Agent Memory

Architecture B adds a separate agent-memory system (Graphiti/Cognee
class) that the agent maintains. The vault feeds the memory system
through the compiler; the memory system tracks entities, commitments,
and changes across sessions; the agent queries both the vault (via MCP)
and its own memory.

**Solves:** Agent has persistent memory and planning context. Recurring
entities and changing facts are tracked. Multi-hop/temporal queries
become possible. The vault itself doesn't have to become the graph.

**Breaks:** Two systems to maintain. Agent memory quality depends on
extraction quality (Graphiti's entity resolution is heuristic and rough).
Risk of the agent memory diverging from vault truth. Adds infrastructure
complexity. Only justified when agent-memory queries are a frequent
workload.

### Architecture C: A+ or B + Learner Overlay

Any of the above plus a separate curriculum/learner overlay for
personalized learning. Authoritative curriculum graph from external
source (e.g., CZI/Learning Commons education KG). Vault provides
evidence of learning state. Ontology alignment maps vault concepts
onto canonical curriculum IDs.

**Solves:** The graph-intersection-for-learning vision. Genuinely novel.

**Breaks:** Requires an authoritative external curriculum graph.
Requires mastery signals beyond note presence. The most speculative
layer — no production system does this yet.

---

## Uninvestigated Leads

1. **Cognee** — Jim mentioned alongside Graphiti. Has Vercel AI SDK
   integration (connects to infrastructure we're already using). Could
   be a better fit for the agent-memory layer. Not researched.

2. **GraphMD** — Markdown-based executable knowledge graphs. May be
   more manifesto than tool, but the concept (executable structure
   living in Markdown) is architecturally interesting for the compiled
   layer.

3. **Jim White's text2kg / skills.md→KG direction** — Agent memory
   dogfooding, ACL2-to-KG, the gap between Zettelkasten and
   inference/execution. We haven't integrated this thread beyond noting
   where Graphiti came from.

4. **LazyGraphRAG** — Microsoft's 0.1% indexing cost alternative to
   full GraphRAG. Defers expensive structure to query time. Potentially
   relevant as a compiled-layer strategy.

---

## Key Uncertainties and Falsification Tests

| Bet | What would falsify it | How to test |
|---|---|---|
| File-first is enough for now | Repeated failure on typed multi-hop queries at current scale | Log query failures for 2 weeks; count how many require structure the LLM can't re-derive |
| Compiled layer (A+) will help creative work | Compiled index doesn't get used, or typed queries aren't what the writer actually needs | Prototype compiler on subset of atoms; measure usage over 10 sessions |
| Agent memory (B) should be separate from vault | Agent memory diverges from vault truth, or memory queries aren't frequent enough to justify overhead | Run Cognee/Graphiti on agent conversation logs for 2 weeks; measure query hit rate |
| Learner overlay (C) is novel and viable | Literature search finds deployed systems doing PKM→curriculum docking | Broader literature search using "overlay," "projection," "alignment" terms |
| Creative-domain status/edge types are right | Practitioners don't use these categories; they impose structure writers resist | Interview/observe 3-5 creative vault users; compare proposed types to actual practice |
| SNT compilation pattern transfers | Creative atoms are too fluid/ambiguous to compile usefully | Attempt compilation on 50 vault atoms; assess whether output is useful or forced |

---

## Questions for the Reviewer

1. **Decomposition check:** Are the four layers (authoring, docking,
   agent memory, learner overlay) the right decomposition, or are we
   missing a layer or conflating two that should be separate?

2. **Architecture recommendation:** Given the evidence, which
   architecture (A, A+, B, or C) should be built first? Is A+ the right
   next step, or should we jump to B?

3. **Agent memory placement:** Is a separate agent-memory system
   (Graphiti/Cognee class) the right home for persistent entity
   tracking, or should that be folded into the compiled docking layer?

4. **Domain gap:** Are we understating or overstating how much the SNT
   compilation pattern transfers to creative work? Is the proposed
   creative schema (`committed/exploring/tension/dormant/composted`,
   `resonatesWith/tensionWith/sourceOf/developedFrom/complicates`)
   grounded or speculative?

5. **Cognee vs. Graphiti:** Should we research Cognee before committing
   to any agent-memory layer decision? It has a Vercel AI SDK
   integration, which connects to infrastructure already in use.

6. **The creative evidence gap:** Thread 6 found no benchmark covering
   creative/exploratory tasks on live PKM vaults. Is there a way to
   design a lightweight test that would tell us whether compiled
   structure helps creative work, or are we in "just build it and see"
   territory?

7. **What's the single most important question we're not asking?**
