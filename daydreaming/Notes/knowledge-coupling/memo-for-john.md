# Research Results — For John

Date: 2026-03-12
From: Claude (David's Opus instance, latent-dreamer repo)

This memo grounds the deep research results against what's already in
the Symbiotic Vault. The full evidence trail — platform comparisons,
GitHub issues, paper citations — is in `research-package-john-track.md`.

---

## What Your Vault Already Gets Right

Before getting into what the research found, it's worth being explicit
about what the vault's current design already has in place — because
a lot of it is validated by the external evidence.

**File-first is the right default.** VAULT_DESIGN.md already positions
graph databases as an extension point ("when the atomic layer is dense
enough that file-based traversal becomes slow"). The research confirms
this is state of the art, not a workaround. Claude Code's own
architecture (CLAUDE.md + scoped memory + MCP) is the strongest
deployed docking pattern, and it's file-first.

**The human/agent surface split is sound at the file level.**
AGENT_PROTOCOL.md's rule that the agent never writes into human
surfaces, and that the human never has to link, is architecturally
correct. The research does not suggest collapsing those surfaces. The
real gap is contextual surfacing — getting agent work to appear during
human work without turning the writing surface into an agent-owned one.

**Semantic matching over atoms is the right retrieval model at current
scale.** The conversational collaboration model — user brings content,
agent matches semantically against `_atoms/`, produces grounded
response — is what the literature calls inference-time assembly. It
works until typed queries and reuse economics force a compiled layer.

**The atom schema is a good starting substrate.** The six kinds
(`concept | method | question | person | reference | event`) with
status progression (`seed → developing → stable → archived`) and traced
provenance (`source:` field) already give the vault a small typed
substrate with provenance. What it does not yet have — and what both
the literature and the vault agent note point toward — is an explicit
`claim` kind.

**Frames as lenses, not topic filters, is a genuine design
contribution.** The research on discourse graphs and knowledge-text
coupling doesn't give a clean off-the-shelf analog for this. Frames
that produce distinct readings of the same atomic layer — writing-
practice seeing craft where agentic-art sees process — are richer than
what most PKM-to-LLM tooling currently exposes.

**The Design Critique diagnosed the real problem.** DESIGN_CRITIQUE.md
identifies cohabitation as the biggest failure: "roommates who leave
notes on the fridge." The agent's work is hidden in `_memory/`. The
external architecture review we ran independently reached the same
conclusion: the missing thing is not backend infrastructure, it's
salience and surfacing.

**The Membrane is the right next build target.** THE_MEMBRANE.md
already names what the architecture review calls the missing layer:
a surfacing algorithm that reads the atomic layer, the current context,
and selects what to offer — with freeze/dismiss/respond/cut
interactions that shape future surfacing. The research confirms this is
where the graph pays rent.

---

## What the Research Adds

Six deep research threads ran against the Graphiti/docking/learning
questions from our conversation. All six are covered below.

### Graphiti

Pressure-tested the Graphiti thesis specifically — not just knowledge
graphs in general, but Graphiti's actual implementation.

**What holds up:**
- Real temporal context-graph engine, not vaporware
- Bi-temporal edge model (4 timestamps, contradiction-based
  invalidation) is a genuine capability
- Neo4j no longer a hard dependency (FalkorDB, Neptune, Kuzu supported)

**What doesn't hold up as well as the pitch suggests:**
- **Entity resolution** — heuristic pipeline, no published
  precision/recall benchmarks. GitHub issues show duplicates and
  nondeterminism. Without developer-supplied `entity_types`,
  everything collapses to generic `Entity` labels.
- **Community detection** — `build_communities()` OOM-crashes at ~1,000
  entities. "Frames as literal subgraphs" isn't production-ready.
- **Scale** — no published benchmarks. ~60 min to bulk-ingest 100
  records with custom ontology. Zep themselves had to move vector
  search and BM25 out of the graph DB under real growth.
- **Docking as graph intersection** — not a built-in capability. Would
  need to be built on top.
- At least one reported comparison found mem0 beat Graphiti on
  efficiency without a statistically significant accuracy gap.

**How this lands against the vault:** VAULT_DESIGN.md already treats
graph databases as a later extension point. The research supports that
instinct. The filter for when Graphiti earns its keep: entities recur
across many episodes + facts change over time + you repeatedly ask
entity-centric multi-hop questions where chronology matters. The vault
doesn't meet these conditions yet — the atom layer is still being
seeded. Not "never" — just not the next move.

### PKM-to-LLM Docking

Surveyed what actually exists for feeding an Obsidian vault into LLM
conversations.

**Research found:** The deployed path is file-first, not graph-first.
Obsidian MCP servers exist but the ecosystem is fragmented —
bitbonsai/mcp-obsidian (~700 stars, active Mar 2026) is the best alive
general bridge. Gemini is strong for curated packets but has no
live-vault coupling. Cursor is unreliable.

**How this lands against the vault:** The conversational collaboration
model in AGENT_PROTOCOL.md (semantic matching over atoms, grounded
response, wikilinked trace to `_memory/`) is already ahead of what most
Obsidian MCP setups offer. The open question isn't "how to dock" — it's
"what structure to expose through the dock." That's where the atom
schema and frame definitions start to matter for external agents.

### Graph Intersection for Learning

The individual pieces all exist — curriculum graphs, ontology alignment,
LLM-based KG extraction, mastery-aware path planning. The novel piece
is specifically what the vault is reaching for: **using a personal vault
as the learner graph that docks into an external teaching graph.**
Nobody has built that full stack from open-ended PKM → curriculum
alignment → tutoring.

The `teaching-learning` frame already asks the right questions:
"who is the learner, and what do they already know?" The inbox item
`vault-as-personalized-learning` captures the vision: "eccentric
learning paths that reconcile with formal pathing where necessary —
locally owned, self-profiling."

**Two refinements from the literature:**

1. The papers say "projection," "overlay," or "alignment" rather than
   "intersection." The learner graph works better as an **overlay** on
   an authoritative curriculum graph, not a second authority. The vault
   provides evidence of learning state; the curriculum graph provides
   canonical structure.

2. "Shortest path" is too topological. Stronger adaptive systems
   optimize for best feasible mastery path under constraints —
   prerequisites, forgetting, difficulty, time. "Lowest-cost mastery
   path under constraints" is the better frame. (This is also why the
   traveling salesman concept doesn't quite fit — TSP assumes fixed
   nodes and a shortest route. Learning paths have constraints that
   make it a different kind of optimization.)

**How this lands against the vault:** The vault's topology — which atoms
exist, which frames are active, what connections tend has discovered —
could serve as the learner evidence stream. But note topology alone
isn't mastery evidence. The research suggests tracking explicit events:
`asserted claim`, `revised claim`, `added support`, `noted
contradiction`, `returned after delay`, `successfully recalled`,
`explicitly marked confusion/confidence`. The vault should be treated as
an evidence stream, not a self-certifying mastery graph.

CZI/Learning Commons has released an education KG with an MCP server
for Claude — external teaching-graph docking into LLMs is already
happening on the curriculum side. Learner-side docking is the open
frontier.

### Temporal Knowledge Graphs

**Research found:** Temporal fact storage is real (Graphiti's 4-timestamp
edges, Neo4j temporal types, TerminusDB's git-for-data). Temporal
personal learner models barely exist. Math Academy is the strongest live
example of graph-shaped learner modeling, but it's platform-owned.

**How this lands against the vault:** The vault already has the right
temporal primitives: dated journals, `_memory/` traces, atom status
progression (`seed → developing → stable → archived`), session
provenance. The valuable temporal object isn't timestamped facts — it's
tracked change in commitments, salience, confidence, and neglected
regions. That's what the Compost experiment reaches for (reading what
was neglected as its own coherent text). You don't need Graphiti to
start tracking this. Append-only provenance and commitment-state
tracking get most of the way.

### Learner Modeling Without Explicit Assessment

Can vault artifacts (journals, drafts, atoms) serve as a passive
mastery signal?

**Research found:** Artifact-based learner modeling works when artifacts
are structured enough or the environment is instrumented enough. But
passive PKM-as-mastery-graph is under-validated. No deployed system
infers evolving understanding by watching note topology alone.

**How this lands against the vault:** The atom schema already has status
progression, provenance, and frame membership — that's more structure
than most PKM systems. But the gap identified by the research is real:
the vault currently doesn't distinguish between "I wrote about X" and
"I understand X." The `teaching-learning` frame's concern with
assessment ("the distinction between what is taught and how it is
assessed") is exactly the right question. Low-friction explicit signals
— self-ratings, confidence marks, "I don't understand this yet" flags
— would matter more than hoping silent inference from atom topology is
good enough.

### When Does File-First Stop Being Enough?

This was the sixth thread and arguably the strongest. It directly
addresses the architecture question: when should the vault move from
flat-file inference-time assembly to something more structured?

**The breakpoint rule:**

> Flat-file inference-time assembly stops being enough when the cost of
> repeatedly re-deriving structure exceeds the cost of maintaining a
> compiled artifact. Not before.

The breakpoint is **not** raw vault size. It's query type + reuse
frequency + whether typed operations pay rent in live work.

**What graph structure actually buys (from controlled comparisons):**
- Plain RAG does better on single-hop and detail-heavy questions
- Graph-based retrieval does better on multi-hop/reasoning questions
- Graph construction is lossy: ~65% entity coverage in tested benchmarks
- For summarization, graph + RAG is often only comparable to RAG alone
- GraphRAG's strongest win is global sensemaking over large corpora
  (~1M tokens) — not general-purpose knowledge work

**The ecosystem is drifting toward hybrids:**
- Microsoft's LazyGraphRAG: 0.1% of full GraphRAG indexing cost,
  comparable quality, defers expensive structure to query time
- LlamaIndex PropertyGraphIndex: per-chunk extraction, persistable,
  source text included at query
- Even Microsoft's own docs now read as internal critique of full eager
  graphing — static global search called "expensive and inefficient"

**How this lands against the vault:** VAULT_DESIGN.md already says
graph databases are for "when the atomic layer is dense enough that
file-based traversal becomes slow." The research sharpens that: the
trigger isn't density alone, it's when you keep asking typed questions
that are annoying to recover from raw files — "what supports this
claim?" "what contradicts?" "what changed?" "trace the dependency
path" — and the same working set gets reused across many interactions.

The vault doesn't meet those conditions yet. The atom layer is still
being seeded. Semantic matching over atoms is still the right model.

**When to consider a compiled layer (3 of 5 over 2-4 weeks):**
1. Support/contradiction/change/path questions recur weekly
2. Same note subset reread across ~10+ interactions
3. Answers to typed questions are slow because structure is re-derived
   each time
4. Minimal schema has stopped changing every few days
5. Impact questions ("what shifts if...?") frequent enough to justify
   compiled traces

**When to consider graph infrastructure (all must hold):**
- Multi-hop/global reasoning is a core workload, not occasional
- The graph itself becomes a user-facing surface
- You're willing to own extraction quality, rebuilds, staleness,
  pruning, entity resolution, and cost
- The graph is demonstrably paying rent in live work

**Creative/exploratory evidence is thin.** No benchmark covers
Drift/Membrane/Table-style creative work on live PKM vaults. The
research found benchmarks for QA, summarization, code, and structured
domains — not for writerly work on live Obsidian vaults. This gap
means the breakpoint criteria above are adjacent evidence, not direct
proof.

**One more finding from the architecture review:** After this thread
came back, we ran an external architecture review. It confirmed the
direction and added one important correction: **this should be a
writing tool with a graph, not a graph tool that produces writing.**
The compiled layer serves the writing surface — it's not a product
in itself. Results surface in margins, backlinks, annotations. No
graph editor. The Membrane is the interface; the compiled layer is
plumbing behind it.

---

## On the Vault Agent Integration Note

The vault agent note that came back from the bidirectional coupling
report made three concrete suggestions. All three are good:

- **Add `claim` to the atom `kind:` enum.** The current six kinds
  (`concept | method | question | person | reference | event`) cover
  entities well. Claims add the argumentative layer — positions that
  can be supported, opposed, refined. The discourse-graph literature
  converges on this: claims alongside concepts, not replacing them.

- **Natural language relationship descriptions when `tend` creates
  links.** Smart middle path. When tend discovers a connection, write a
  sentence explaining *how* the atoms relate — not just a bare
  `[[wikilink]]`. Readable now, parseable later, costs nothing.

- **Epistemic relations during atomization.** When atomize creates an
  atom from a journal entry, note in the prose: "This claim emerged as
  a challenge to [[existing-atom]]." Richer atomization without
  structural changes.

**One thing the note was missing context on:** The bidirectional
coupling question. The note dismissed it as "the vault doesn't need
real-time sync" — and that's correct for automated prose↔graph sync.
But there's a deeper version: how does agent work flow back to
influence human work? The Membrane, the Drift, the Séance are all
experiments in designed bidirectional flow — lossy, mediated, curated.
DESIGN_CRITIQUE.md names this as "the deepest tension": the
cohabitation principle asks for symbiosis while the surface rule
enforces separation. The note answered the engineering question and
skipped the design question that the vault's own experiments are
wrestling with.

Not a big disagreement — more like the note's framing was narrower
than the vault's own design ambitions.

---

## What's Adjacent: A Working Docking Pattern

**Context, not prescription.** David has been building something in a
different domain (supplemental needs trust legal research) that turned
out to be structurally relevant. The pattern:

- **Markdown → SQLite compilation with typed edges.** Markdown stays
  source of truth. A compiler builds a queryable index with claims,
  evidence chains, typed edges (`supportedBy`, `overriddenBy`,
  `exceptionApplies`). One-way compilation — no reverse propagation.

- **MCP server over the compiled graph.** Claude queries through typed
  tools: `get_claim(id)`, `trace_support(claim_id)`,
  `what_breaks_if(node_id)`, `list_conflicts()`, `search(query)`.

- **Claims with epistemic status.** `proven | provisional | open`.

The plumbing generalizes — Markdown → SQLite → MCP is domain-agnostic.
But the epistemology doesn't transfer directly to a creative vault.
Legal research has authoritative sources, falsifiable claims, concrete
"proven." Creative work has journal entries, aesthetic commitments,
productive contradictions.

The research stops here. What follows is a proposed adaptation — not
something the literature directly supports, but an attempt to fill the
gap it identifies:

If the compilation pattern were adapted for the vault, the schema would
need to be different. The vault's existing `seed → developing → stable
→ archived` progression is one axis. A claim-specific axis might add:
`committed` (position taken), `exploring` (not yet a commitment),
`tension` (deliberately maintained contradiction). The edge types would
be softer: `resonatesWith`, `tensionWith`, `sourceOf`, `developedFrom`,
`complicates`. And the SNT project's `what_breaks_if(node_id)` would
become something like `what_shifts_if(node_id)` — not "what breaks" but
"what changes character."

Whether that's the right schema is genuinely open. The vault's atoms
are closer to descriptive than argumentative, and adding a claim layer
should preserve that character.

---

## Where This Leaves Things

**What the vault already has right:**
- File-first architecture with graph as extension point
- Human/agent surface separation with semantic matching
- Atom schema with provenance and status progression
- Frames as lenses producing distinct readings
- Self-diagnosis of the cohabitation/surfacing problem
- The Membrane as the right next build target

**What the research validates:**
- File-first is state of the art, not a compromise
- Graphiti is premature at current vault scale
- The learning-graph vision is genuinely novel and survives
  reformulation
- Temporal value comes from tracking commitment change, not
  timestamped facts — the Compost experiment is on the right track
- The breakpoint for moving beyond file-first is query type + reuse,
  not vault size — and the vault isn't there yet
- This should be a writing tool with a graph, not a graph tool that
  produces writing

**What the research challenges or adds:**
- Add `claim` alongside the existing atom kinds
- The learner graph should be an overlay on a curriculum graph, not a
  parallel authority
- Passive note topology isn't mastery evidence — explicit learning
  signals matter
- "Lowest-cost mastery path under constraints" is a better frame than
  shortest-path or traveling-salesman optimization
- The vault should be treated as evidence stream, not self-certifying
  mastery graph
- When typed queries do start recurring, the right move is a one-way
  compiled layer (Markdown → index → MCP), not a graph database
  migration

**Things to do now:**
1. Add `claim` to the atom `kind:` enum
2. Natural language relationship descriptions when tend links atoms
3. Start logging learning-relevant events (assertions, revisions,
   contradictions, returns after delay, confidence/confusion marks) as
   groundwork for the learner overlay

**Things to watch for (the breakpoint signals):**
4. Typed questions (support/contradiction/change/path) recurring weekly
5. Same note subset reread across ~10+ interactions
6. Impact questions ("what shifts if...?") becoming frequent
7. Schema stabilizing enough to compile against

When 3 of those 4 hold over a few weeks, a compiled docking layer
becomes worth prototyping. Until then, the current architecture is
the right one.

The full research package has all the evidence. Let us know what you
want to dig into.

---

## Sources and Provenance

Each section of this memo draws on specific deep research queries and
external sources. The raw research replies are in
`daydreaming/Notes/DeepResearch/2026-03-12/john/replies/`. The prior
landscape report (`bidirectional_knowledge_text_coupling.md`, 654
sources) and the generative conversation that produced it are also in
the repo.

### What Your Vault Already Gets Right

- **Vault files referenced:** `VAULT_DESIGN.md`, `AGENT_PROTOCOL.md`,
  `DESIGN_CRITIQUE.md`, `THE_MEMBRANE.md` (under
  `team-repos/Symbiotic-Vault/_system/` and `_system/_design/`)
- **External validation:** Claude Code memory architecture
  ([docs](https://code.claude.com/docs/en/memory)); MCP transport
  ([docs](https://code.claude.com/docs/en/mcp))

### Graphiti

- **Local source:** `query02.md`
- **Key external sources:**
  - Graphiti repo and paper — https://github.com/getzep/graphiti;
    https://arxiv.org/html/2501.13956v1
  - Graphiti backend/config docs —
    https://help.getzep.com/graphiti/configuration/neo-4-j-configuration
  - `build_communities()` OOM —
    https://github.com/getzep/graphiti/issues/836
  - Entity resolution / generic `Entity` collapse concern —
    https://github.com/getzep/graphiti/issues/992
  - Zep managed platform — https://help.getzep.com/facts
  - FalkorDB, Amazon Neptune + OpenSearch, Kuzu as alternative backends

### PKM-to-LLM Docking

- **Local source:** `query01.md`
- **Key external sources:**
  - Claude Code architecture — https://code.claude.com/docs/en/memory
  - bitbonsai/mcpvault — https://github.com/bitbonsai/mcp-obsidian
    (~700 stars, active Mar 2026)
  - MarkusPfundstein/mcp-obsidian — https://github.com/MarkusPfundstein/mcp-obsidian
    (~3k stars, stale)
  - Gemini URL Context — https://ai.google.dev/gemini-api/docs/url-context
  - Cursor rules/MCP — https://cursor.com/docs/rules

### Graph Intersection for Learning

- **Local source:** `query03.md`
- **Key external sources:**
  - Pleskach et al. 2023 — student KG vs. discipline KG comparison —
    https://ceur-ws.org/Vol-3646/Paper_11.pdf
  - Chen et al. 2024 — textbook target KG + student-dialogue KG —
    https://files.eric.ed.gov/fulltext/ED665357.pdf
  - Abu-Rasheed et al. 2025 — linked domain/curriculum/user models with
    LLM-assisted KG completion — https://arxiv.org/abs/2501.12300
  - Yu-Hsiang Chen et al. 2025 — personalized learning-trajectory
    graphs — https://arxiv.org/abs/2504.11481
  - InstructKG 2026 — instructor-aligned prerequisite graphs —
    https://arxiv.org/abs/2602.17111
  - CZI/Learning Commons — education KG + MCP server for Claude —
    https://chanzuckerberg.com/blog/scaling-proven-learning-practices/
  - Math Academy — https://www.mathacademy.com/how-our-ai-works
  - ALEKS — https://www.aleks.com/about_aleks/research_behind
  - Carnegie Learning MATHia —
    https://www.carnegielearning.com/blog/mathia-ai
  - Ontology alignment: OAEI (https://oaei.ontologymatching.org/),
    LogMap, AgreementMakerLight
  - 2024 systematic review (KGs in education) —
    https://pmc.ncbi.nlm.nih.gov/articles/PMC10847940/

### Temporal Knowledge Graphs

- **Local source:** `query04.md`
- **Key external sources:**
  - Graphiti bi-temporal model — https://arxiv.org/html/2501.13956v1
  - TigerGraph temporal hierarchies —
    https://www.tigergraph.com/blog/utilizing-multi-edge-for-temporal-search-in-a-sales-agent-hierarchy/
  - Neo4j temporal types —
    https://neo4j.com/docs/getting-started/data-modeling/versioning/
  - TerminusDB (git-for-data) — https://terminusdb.org/docs/terminusdb-explanation/
  - Math Academy knowledge frontier model —
    https://www.mathacademy.com/how-our-ai-works
  - xAPI, Caliper (https://www.1edtech.org/standards/caliper), ADL TLA
    (https://www.adlnet.gov/guides/tla/service-definitions/)

### Learner Modeling Without Explicit Assessment

- **Local source:** `query05/` (two files: `chat_reply.md`,
  `query05_integrated_answer.md`)
- **Key external sources:**
  - Epistemic Network Analysis (ENA) —
    https://www.epistemicnetwork.org/
  - Stealth assessment — https://files.eric.ed.gov/fulltext/ED612156.pdf
  - FSRS scheduler —
    https://github.com/open-spaced-repetition/fsrs4anki/wiki/The-Algorithm
  - CourseMapper PKG (UMAP 2024) —
    https://www.uni-due.de/imperia/md/content/soco/alatrash_ukde24_final.pdf
  - Concept-map topology study (359 maps) —
    https://pmc.ncbi.nlm.nih.gov/articles/PMC9939863/
  - Writing-process keystroke study —
    https://www.sciencedirect.com/science/article/pii/S1060374324000201
  - NLP adaptive dialog study (162 students) —
    https://www.mdpi.com/2227-7102/15/2/207
  - Oxford chapter on pedagogically informed NLP —
    https://academic.oup.com/book/58946/chapter/492996783

### When Does File-First Stop Being Enough?

- **Local source:** `query06.md`
- **Key external sources:**
  - RAG vs. GraphRAG comparison — https://arxiv.org/html/2502.11371v1
  - Microsoft GraphRAG — https://arxiv.org/html/2404.16130v2;
    https://microsoft.github.io/graphrag/
  - LazyGraphRAG — https://www.microsoft.com/en-us/research/blog/lazygraphrag-setting-a-new-standard-for-quality-and-cost/
  - "Lost in the Middle" — https://arxiv.org/html/2307.03172v1
  - RULER benchmark — https://openreview.net/forum?id=kIoBbc76Sy
  - OP-RAG / "In Defense of RAG" —
    https://arxiv.org/html/2409.01666v1
  - EMNLP 2024 Self-Route (LC vs. RAG) —
    https://aclanthology.org/2024.emnlp-industry.66/
  - GraphRAG-Bench — https://arxiv.org/html/2506.05690v3
  - March 2026 cost study (long-context vs. structured memory) —
    https://arxiv.org/abs/2603.04814
  - CAG (Cache-Augmented Generation) —
    https://openreview.net/pdf?id=EOG15VvlY4
  - LlamaIndex PropertyGraphIndex —
    https://developers.llamaindex.ai/python/framework/module_guides/indexing/lpg_index_guide/

### Architecture Review (5 Pro)

- **Local source:** `project_file_org.md`
- **Review model:** GPT 5 Pro via AI Studio
- **Reviewed:** `briefing-for-review.md` plus all supporting documents

### Vault Agent Integration Note

- **Source:** Bidirectional coupling report returned from vault agent
  (in John's vault context)
- **Cross-referenced against:** `DESIGN_CRITIQUE.md`,
  `THE_MEMBRANE.md`, discourse graph literature (Joel Chan QCE,
  nanopublications)

### Adjacent Prior Art (SNT)

- **Source:** David's working implementation in `latent-dreamer` repo
  (supplemental needs trust legal research)
- **Relevant external patterns:** ARTIST argumentation system,
  Graphologue extract-modify-regenerate, nanopublication infrastructure
