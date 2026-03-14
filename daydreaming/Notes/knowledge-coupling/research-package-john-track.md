# Deep Research Synthesis — Knowledge Coupling Track

Date: 2026-03-12

Six deep research threads targeting the Graphiti/docking/learning thesis,
cross-referenced with a prior landscape report on bidirectional
knowledge-text coupling (654 sources), the generative conversation that
produced it, and a working implementation in a different domain (SNT
legal research). The raw deep research replies are available separately
if needed.

This is structured as a **research package**, not just a position paper:
headline takeaways first, then the underlying material from each thread.
The idea is that you can inspect the evidence directly and disagree with
the takeaways if you read it differently.

Thread 5 (learner modeling without explicit assessment) came back
separately and is incorporated into the temporal section. Thread 6
(when does file-first stop being enough, what to build next) came back
later and is integrated below. Full raw evidence is still available
separately if useful.

---

## Headline Takeaways

1. **The deployed docking path today is file-first, not graph-first.**
   Claude Code's CLAUDE.md + scoped memory + MCP is the strongest
   working pattern. The Obsidian MCP ecosystem is fragmented with no
   clear winner.

2. **Graphiti looks premature at current vault scale.** Significant gaps
   between pitch and reality — entity resolution has no published
   benchmarks, community detection OOMs at ~1k entities, no scale
   benchmarks. Probably not yet, but the crossover point is unknown.

3. **Graph intersection for learning is the live research edge.** Each
   piece exists separately; the novel part is specifically using a
   personal vault as the learner graph that docks into an external
   teaching graph. Nobody has built that full stack.

4. **Temporal KGs buy you history, not pedagogy.** Temporal fact storage
   exists. Temporal personal learner models barely do. The valuable
   object is tracked change in commitments, not timestamped facts.

5. **The breakpoint is query type + reuse, not vault size.** If
   file-first stops being enough, the next justified move is a compiled
   layer, not full graph-first infrastructure.

6. **The domain gap matters.** The SNT prior art validates the plumbing
   (Markdown → SQLite → MCP) but its epistemology (proven/provisional/
   open, evidence chains, falsification) doesn't transfer cleanly to
   raw creative work. The creative vault needs different status types
   and different edge semantics.

---

## Query 01 — PKM-to-LLM Docking Landscape

*Source: deep research query on PKM-to-LLM docking pipelines.*

### Claude Code as Docking Mechanism

The research positions Claude Code as the strongest current
implementation of PKM-to-LLM docking:

- **CLAUDE.md** files at user, project, and managed-policy scopes.
  Ancestor files load automatically; subdirectory files lazy-load when
  Claude enters that part of the tree. `@path/to/import` lets one
  memory file pull in others.
- **`.claude/rules/`** for path-scoped instructions.
- **Auto memory** as plain Markdown under `~/.claude/projects/.../memory/`.
  First 200 lines of `MEMORY.md` loaded at session start.
- **MCP** as transport — Anthropic positions MCP as the way Claude
  connects to external tools and data sources.

The practical pattern: keep the vault on disk, use CLAUDE.md and rules
to teach the agent how to read it, add MCP only for operations the model
should not fake.

This validates the "files are primary, semantic matching at inference
time" approach as state of the art, not a workaround.

### Obsidian MCP Servers — Fragmented Ecosystem

Two families: servers through the Local REST API plugin, and servers
reading the filesystem directly. No settled winner.

| Server | Stars | Last Active | Notes |
|---|---|---|---|
| bitbonsai/mcp-obsidian | ~700 | Mar 2026 | Best alive general bridge (markets as "mcpvault"). No published releases. |
| MarkusPfundstein/mcp-obsidian | ~3k | Apr 2025 | Highest adoption, stale commits. Users report schema compat, macOS build, cert, timeout issues on larger vaults. Maintenance risk. |
| cyanheads/obsidian-mcp-server | ~397 | Jun 2025 | Broad CRUD/search/frontmatter/tag surface. Local REST API path. |
| Piotr1215/mcp-obsidian | ~16 | — | Direct filesystem access, no Obsidian dependency. Most aligned with file-first philosophy. Less mature socially. |
| aaronsb/obsidian-semantic-mcp | — | Archived Sep 2025 | Skip. |

### Cursor

Right nouns (project rules via `.cursor/rules`, MCP, `@Docs`), but
users report `@Docs` are globally scoped not project-scoped, and doc
indexing/retrieval has been unreliable through 2025-2026. Not
recommended as memory authority. Fine as editor with contextual assists.

### Gemini

Three strong mechanisms: URL Context for fetching/analyzing URLs, native
document understanding for PDFs/files, Files API + context caching for
large repeated corpora. Good for "here's a textbook, reason over it."
No native Obsidian coupling — no first-class "live inside my vault"
equivalent. Practical pattern is exported-context and session-grounding.

### Net Finding

(Peter Danovich was mentioned as a reference but couldn't be verified —
no public writeup, repo, or talk found. Treat as unconfirmed.)


Usable docking exists now but looks like **Markdown + scoped memory +
thin bridges**, not a settled PKM operating system. The interesting open
question shifts from "how to dock" to "what structure to expose through
the dock."

---

## Query 02 — Graphiti Deep Dive

*Source: deep research query pressure-testing Graphiti specifically.*

### What Graphiti Actually Is

A temporal context-graph engine for agent memory. Ingests episodes
(`text`, `message`, `json`), extracts entities and facts, keeps
provenance back to source episode, stores temporal metadata on edges,
optionally builds community summaries, retrieves with hybrid semantic +
keyword + graph-aware search.

Neo4j is no longer a hard dependency — now also supports FalkorDB,
Amazon Neptune + OpenSearch, and Kuzu. Neo4j remains the default center
of gravity.

### Point-by-Point Against the Original Pitch

**"Graphiti is an agent memory/KG layer, not an orchestrator."**
Correct. That's exactly how the docs and paper position it.

**"Entity resolution cleans messy notes into typed concepts."**
Overstated. Heuristic pipeline: extract entities → embed names into
1024-d space → cosine + full-text candidate search → LLM prompt to
judge duplicates. No published precision/recall benchmark. GitHub issues
show duplicates and nondeterministic quality. Without developer-supplied
`entity_types`, all entities collapse to generic `Entity` labels even
though edges get richer free-form relation types. "Automatic ontology
learning" is only partially true.

**"Temporal evolution is the killer feature."**
Real but narrower than claimed. Bi-temporal model: four timestamps on
edges (`created_at`, `valid_at`, `invalid_at`, `expired_at`).
Invalidates older contradictory edges when new temporally-overlapping
facts arrive. But: node attributes are destructively upserted, not
historically versioned (open Jan 2026 issue). This is temporal fact
management, not automatic learner mastery modeling.

**"Frames become literal subgraphs/communities."**
Partly true, loosely. Communities exist as generic clusters of connected
entities with summaries, not semantically privileged "frames."
Implementation inconsistency: paper says label propagation, current docs
say Leiden with label-propagation-inspired incremental updates.
`build_communities()` OOM-crashes at ~1,000 entities (1,210 entities →
1,210 queries → process killed). Do not build user-facing meaning on
community nodes until this stabilizes.

**"Docking as graph intersection."**
Not a built-in Graphiti capability. Would need to be built on top.

### Scale Signals

Graphiti does not publish benchmarks at 100/10K/100K nodes. Operational
signals only:

- Default concurrency low (`SEMAPHORE_LIMIT=10`) to avoid LLM 429s
- `add_episode_bulk` faster but explicitly skips edge invalidation
- Feb 2026 issue: ~60 min to bulk-ingest 100 records with custom ontology
- Older issue: 10 messages ~2 min
- Zep's own scale write-up: under real growth, had to move vector search
  and BM25 out of the graph DB because the graph DB was doing too much
- Zep (managed) claims sub-200ms; Graphiti (self-hosted) performance
  "depends on your setup" — don't let managed platform numbers bleed
  into OSS expectations

### Failure Modes

- Works best with structured-output LLM services; other services may
  produce incorrect schemas and ingestion failures
- GitHub: searches returning no results under OpenAI-compatible setups
  despite edges existing, FalkorDB namespace/search bugs, confusion
  around mandatory `group_id`s, requests for no-LLM ingestion mode
- Reddit: loose nodes, skipped relationships, repeated context,
  repeated reattachment of company/name/role facts. Also: one production
  user said Zep worked well after early performance issues. Another
  cited scaling, schema changes, cloud costs as pain.

### Comparison Set

**Microsoft GraphRAG:** Hierarchical graph over mostly static corpora.
Chunk → extract graph/claims → detect communities → generate reports.
~75% of indexing cost is graph extraction. Better than Graphiti for
large static collections and "summarize the landscape" questions. Worse
for changing facts and explicit temporal invalidation. FastGraphRAG
variant swaps LLM extraction for noun-phrase/co-occurrence heuristics —
cheaper but noisier.

**LlamaIndex PropertyGraphIndex:** Lighter, more modular middle ground.
Configurable `kg_extractors` per chunk, schema-guided or free-form
extraction, `PropertyGraphStore` abstraction (in-memory or external
graph/vector stores). No bi-temporal invalidation or community
maintenance found in docs.

**LangChain graph stores:** Mostly glue. `LLMGraphTransformer` converts
docs to graph docs, `Neo4jGraph` wraps the driver,
`GraphCypherQAChain` does NL→Cypher, `graph-retriever` adds
metadata-based traversal over vector stores. Good if you know the exact
graph behavior you want. Not a coherent memory system.

**Plain flat-file semantic matching:** Still the right default for
small-to-medium PKM. Removes graph backends, LLM entity-resolution
prompts, community rebuilds, namespace plumbing. You lose validity
windows, automatic contradiction invalidation, and reusable graph
neighborhoods. That trade is usually good until you have repeated
failures that are specifically temporal or multi-hop.

**mem0:** At least one reported comparison (cited in the raw research
reply but primary source not pinned down) found mem0 beat Graphiti on
efficiency without a statistically significant accuracy gap.

### Assessment

A rough filter (not a settled threshold — Graphiti doesn't publish the
benchmarks we'd need): Graphiti starts to earn its keep when all three
hold: (1) entities recur across many episodes, (2) facts change over
time, (3) you repeatedly ask entity-centric multi-hop questions where
chronology matters. The current creative-vault use case probably doesn't
meet these conditions yet, but the crossover point is genuinely unknown.

Graphiti looks premature for this use case at this scale. Not "never" —
the trigger to revisit is when flat-file semantic matching fails in
concrete, repeatable ways.

---

## Query 03 — Graph Intersection for Personalized Learning

*Source: deep research query on graph intersection for personalized learning.*

### Literature Landscape

The papers say "projection," "integration," "overlay," or "alignment" —
not "intersection." Closest ancestors:

- **Pleskach 2023:** Student KG as subset of discipline KG. Compare the
  two to identify gaps. Project student graph onto discipline graph to
  restore missing nodes/links and determine completed trajectory.
- **Chen 2024:** Textbook target KG + student-dialogue KG, integrated to
  reveal activated vs. non-activated knowledge and remaining gaps.
  LLM-extracted KGs on both sides.
- **Yu-Hsiang Chen 2025:** Personalized learning-trajectory graphs from
  course materials + exam data.
- **Abu-Rasheed 2025:** Linked domain, curriculum, and user models with
  LLM-assisted KG completion for personalized higher-ed recommendations.
- **InstructKG 2026:** Automatically constructs instructor-aligned
  prerequisite and part-of graphs from course materials for gap
  diagnosis and targeted intervention.

At the schema layer, the docking problem is ontology matching. OAEI is
the standing evaluation framework. LogMap emphasizes scalability +
reasoning/repair. AgreementMakerLight has strong OAEI performance.
Recent work uses LLMs as an oracle inside LogMap-style alignment. But
this solves concept/schema correspondence only — not learner diagnosis,
mastery inference, or tutoring policy.

A 2024 systematic review found KGs already used for personalized
learning, curriculum design, concept mapping, and educational
recommendation, but flagged: lack of standardized educational
ontologies, interoperability problems, sparse data, scalability,
semantic heterogeneity, real-time updates, weak evaluation.

### Deployed Platforms

**Knewton/Wiley:** Knowledge graph of learning objectives and
prerequisite relations. Recommendation scope defined by prerequisite
hops. Remediation walks backward through graph when student struggles.
But: learner model is internal to platform. Docs expose goals, progress,
recommendations — not a separate user-owned learner KG. Explicit
curriculum graph + adaptive student model, not personal-KG docking.

**ALEKS:** Knowledge Space Theory. Infers student's feasible knowledge
state and "outer fringe" (what student is ready to learn next).
Rigorous and battle-tested. Combinatorial knowledge-state model, not
graph alignment between learner-authored KG and external ontology.

**Carnegie Learning MATHia:** Skill-tracing. Step-level student behavior
→ inferred understanding, probabilistic skill proficiency tracking,
mastery skill by skill. Not graph intersection.

**OLI (Open Learning Initiative):** Learning objectives/skills tagged
into course, statistical learning model + dashboard estimating learning
levels. Not graph intersection.

**CZI/Learning Commons:** Released an education Knowledge Graph linking
standards, smaller learning components, and prerequisite-like pathways,
plus an MCP server connecting that graph to Claude. This IS external
teaching-graph docking. Learner-side docking does not yet exist.

**Math Academy:** Curriculum KG + spaced repetition, estimates
"knowledge frontier." Strongest live graph-shaped learner model, but
platform-owned.

### "Shortest Path" Correction

One refinement from the literature: "find the shortest path between
current knowledge and target knowledge" is a clean graph metaphor, but
not usually the right pedagogical objective. Stronger systems optimize
for **best feasible mastery path under constraints**: prerequisites,
forgetting, difficulty, time, uncertainty, sometimes RL reward.
Knewton's remediation depth, ALEKS's outer fringe, and newer
KG-based planners with knowledge-tracing + RL all fit this pattern. "Shortest path"
translates better as "lowest-cost mastery path under constraints."

### Verdict

**Yes in pieces, no as a standard product.** The literature validates
curriculum graphs, learner overlays, ontology matching, and
mastery-aware path planning separately. It does not yet validate the
exact move of using a local personal knowledge system as the learner
graph that docks into an external teaching graph. That is the live
research edge — this is where a contribution could be made.

Near-term architecture the research supports:
1. Keep curriculum/domain graph authoritative and versioned
2. Treat learner side as overlay/evidence graph, not a second authority
3. Use ontology alignment to map vault concepts/claims onto canonical IDs
4. Infer mastery from stronger signals than note presence (assessments,
   self-ratings, successful recall, dialogue evidence, task performance)
5. Run path planning on curriculum graph with learner overlay as weights

---

## Query 04 — Temporal Knowledge Graphs

*Source: deep research query on temporal knowledge graph systems.*

### System-by-System

**Graphiti/Zep:** Closest to a real temporal fact graph. Facts on edges
with four timestamps (`created_at`, `valid_at`, `invalid_at`,
`expired_at`). Contradiction-based invalidation. Chronological batch
ingestion. But: node attributes destructively upserted not versioned
(open Jan 2026 issue). Other open issues: datetime mismatches with Kuzu,
broken `valid_at` search-filter query generation, Neo4j backend
regression, MCP-server blank disconnected nodes. Active demand for
"bring your own extraction" mode. Real temporal capability, not boring
mature infrastructure.

**TigerGraph:** Strong for large event streams and time-sensitive
traversals. Temporal hierarchies with timestamped relationships and
multi-edge discriminators. Sub-millisecond traversal on multi-hop paths,
~50M events/day. `TemporalPyGTransform` for temporal subgraph sequences.
But: you model time yourself with schema choices. Not a ready-made
learner system.

**Neo4j:** Built-in temporal value types in Cypher, official versioning
guidance (`validFrom`/`validTo`), Bloom temporal filtering/styling, APOC
temporal utilities. No automatic fact invalidation or first-class
temporal KG layer. Old GraphAware TimeTree plugin retired, framework
discontinued 2021. You design the time model yourself.

**TerminusDB:** Git-for-data. Immutable history, branches, merges,
diffs, commit graphs, time travel to chosen commit. Active as of late
2025. Good for "what did the KB look like at commit X?" Does NOT mean
"when did this fact become true?" unless you model it yourself.
Database-history time, not semantic-validity time.

### Personal Learning Side — Very Thin

No deployed local-first vault infers a person's evolving understanding
by watching changes in knowledge graph topology. Partial approximations:

- **Recall:** Knowledge graph + question generation + spaced repetition +
  knowledge stages + time filters in graph view
- **RemNote:** Linked knowledge base + graph view + per-card spaced
  repetition
- **Math Academy:** Curriculum KG, estimates student's "knowledge
  frontier," tracks per-topic spaced-repetition stability. Strongest
  live example, but platform-owned.

Memory models in Recall/RemNote are mostly question/card performance,
not topology-change-based mastery inference.

2024-2026 research prototypes:
- 2024 UMAP paper: transparent learner models from PKGs in CourseMapper
  (learners mark "Did Not Understand" concepts)
- 2025 English-learning system: KG + graph propagation + forgetting,
  deployed in WeChat Mini Program
- 2025 KG-based personal learning path planning with learner-mastery
  estimates
- LAK 2025: KGs + LLMs tracking learning trajectories

Real research, not yet a settled product category.

### xAPI / Caliper

Standards for collecting learning-activity data, not temporal KGs
themselves. Useful as the evidence layer: how to log learner events.
Don't solve learner-state inference or graph-based forgetting.

### Learner Modeling Without Explicit Assessment (Query 05)

Raw reply available separately if useful.

A fifth thread came back targeting the specific question: can vault
artifacts (journals, drafts, atoms) serve as a passive mastery signal?

**Artifact-based learner modeling is plausible** when artifacts are
structured enough or the environment is instrumented enough. The
strongest validated cases are revised explanations, reflective writing,
concept maps, and instrumented process traces inside designed learning
environments — not free-form PKM vaults.

Deployed/operational examples:
- Math Academy: overlays student answers on a knowledge graph, computes
  knowledge profile, estimates frontier, trickles repetitions through
  graph
- CourseMapper PKG: transparent and scrutable, but relies on explicit
  learner markings (`Did Not Understand`, `Understood`, `New`)
- 2025 English-learning system (WeChat Mini Program): graph propagation
  + forgetting + RL over real interaction logs — still begins from
  explicit diagnostic and interaction data
- RemNote: spaced repetition + graph-like notes, but strongest scheduler
  evidence is FSRS-style per-card memory modeling, not graph-driven
  mastery inference

Published prototypes with evaluation:
- ENA (Epistemic Network Analysis): models connections among coded
  elements over time, distinguishes stronger/weaker reflective patterns
- Knowledge-integration studies: revised explanations after adaptive NLP
  dialogues reveal conceptual change; revision behavior predicts gains
- Writing-process analytics: keystroke logs and SRL transitions predict
  output quality
- Concept-map studies: structural artifacts analyzed as graphs over
  time, automated comparison against reference maps detects
  misconceptions

**Still thin / proposal territory:** Personal PKM as passive learner
model. ENA on free-form Obsidian vaults. Graph-topology-only
knowledge-decay detection. Proof that journals + drafts + links alone
substitute for explicit assessment.

**The vault as evidence stream, not self-certifying mastery graph.**
Useful event types to track: `asserted claim`, `revised claim`,
`added support`, `noted contradiction`, `linked concepts`,
`returned after delay`, `successfully recalled`, `failed recall`,
`abandoned thread`, `explicitly marked confusion/confidence/uncertainty`.

Lightweight learner overlay: claim/question/evidence units + confidence
+ salience + provenance + revision history. Use explicit low-friction
signals where ambiguity matters. Do not pretend silent inference is
already good enough.

### Key Reframe

The valuable temporal object is not timestamped facts. It is **tracked
change in commitments, salience, confidence, and neglected regions, with
provenance.** That's a modeling question, not an infrastructure question.

Recommended sequence:
1. Build append-only provenance and memory surfaces (dated journals,
   session logs, agent protocol provenance — the vault already has
   beginnings of this)
2. Model fast-vs-slow state change (what's ripening vs. what's active)
3. Only then decide whether a temporal KG is warranted

"Version control for commitments" is more valuable than "timestamped
facts."

---

## Query 06 — When Does File-First Stop Being Enough?

*Source: deep research query on the breakpoint from flat-file
inference-time assembly to a compiled layer or graph-first
infrastructure. The final subsection explicitly marks what came from the
later architecture review rather than the raw thread itself.*

Raw reply available separately if useful.

This thread asked the strategic question the earlier queries left open:
when does file-first stop being enough, and what should the next move
actually be?

**Breakpoint rule:** Flat-file inference-time assembly stops being
enough when the cost of repeatedly re-deriving structure exceeds the
cost of maintaining a compiled artifact. The breakpoint is **not** raw
vault size by itself. It is a mix of query type, reuse frequency, and
whether typed operations are paying rent in live work.

**What graph structure actually buys (from controlled comparisons):**
- Plain RAG tends to do better on single-hop and detail-heavy questions
- Graph-based retrieval tends to do better on multi-hop and reasoning
  questions
- Graph construction is lossy; tested benchmarks only recover about
  two-thirds of answer entities in the graph
- For summarization, graph + RAG is often only comparable to RAG alone
- GraphRAG's strongest win is global sensemaking over large corpora, not
  general-purpose PKM

**Where the ecosystem is moving:** toward hybrids, not full eager
graph-first systems.
- Microsoft LazyGraphRAG pushes expensive structure to query time and
  cuts indexing cost dramatically
- LlamaIndex PropertyGraphIndex combines extracted entities/relations
  with source text at query time
- Even Microsoft's own GraphRAG materials now read like a critique of
  static, expensive global graphing

The recurring lesson is the same one the SNT project already
demonstrates locally: structure pays when you reuse it often enough, not
because graphs are inherently smarter.

**What this means for the vault:** the trigger is not "the vault got
large." The trigger is when the work repeatedly demands typed questions
that are annoying to recover from raw files:
- what supports this claim
- what contradicts this
- what changed
- what shifts if this changes
- trace the dependency path

If those questions recur on the same working set often enough, the next
justified move is a **one-way compiled layer**: Markdown remains source
of truth, compiler builds a disposable retrieval artifact, MCP exposes
typed queries. That is the A+ move. It is not yet graph-first.

**When a compiled layer is justified (3 of 5 over 2-4 weeks):**
1. Support/contradiction/change/path questions recur weekly
2. The same note subset gets reread across roughly 10+ interactions
3. Answers are slow because structure is re-derived each time
4. The minimal schema has stopped changing every few days
5. Impact questions ("what shifts if...?") happen often enough to
   justify compiled traces

**When heavier graph infrastructure is justified (all must hold):**
- Multi-hop or global reasoning is a core workload, not an occasional
  one
- The graph itself becomes a user-facing surface, not only background
  plumbing
- You are willing to own extraction quality, rebuilds, staleness,
  pruning, entity resolution, and cost
- The graph is demonstrably paying rent in live work

**Creative/exploratory evidence remains thin.** The benchmarks cover QA,
summarization, code, and structured domains. They do not benchmark
Drift/Membrane/Table-style creative work on live Obsidian vaults. So the
breakpoint rule above is adjacent evidence, not direct proof.

**Architecture-review addendum (not part of the raw thread):** After
this thread came back, a later architecture review sharpened one product
stance: this should be a **writing tool with a graph**, not a graph tool
that produces writing. The missing layer is salience, surfacing, and
control. The compiled layer serves the writing surface; it is not the
product in itself.

---

## The Atom Question — Claims Alongside Concepts

The Graphiti pitch is concept-centric: "AI," "LLMs," "ChatGPT" as
entities that get resolved and linked. That layer is real and useful —
entity resolution and concept linking are genuine capabilities.

The broader research suggests there's a layer above concepts that does
more work: **claims, questions, and evidence** as typed atoms alongside
entities, not replacing them.

- **Joel Chan (UMD):** Questions, Claims, Evidence (QCE) with typed
  rhetorical edges. Deployed Obsidian + Roam plugins with real users.
  Discourse Grammar Parser converts informal prose to formal relations.
- **Nanopublications:** Assertion + provenance + publication context.
  Most mature infrastructure. ARPHA Writing Tool does inline export.
- **eRST:** Discourse relation graphs (not trees). 40+ signal types.
  200K+ token annotated corpus across 12 genres.
- **KSG:** "Micro-ideas" from student discourse linked to synthesis
  nodes with epistemic relations.

The reusable lesson: use a small typed substrate, keep provenance and
discourse separate. The vault agent already suggested adding `claim` to
the atom `kind:` enum — concepts stay as entities; claims add the
argumentative layer that experiments like the Séance and Table need to
push against.

---

## Adjacent Prior Art — SNT Project

There's a working implementation in a different domain that de-risks
several patterns the research recommends. David built this for
supplemental needs trust legal research — different subject matter, and
not proof that the vault should inherit the same schema, but some
structural patterns clearly transfer.

### What's Built

**Markdown → SQLite compilation with typed edges.** Markdown stays
source of truth. Python compiler builds queryable SQLite index:
`claims`, `claim_evidence`, `decision_points`, `tasks`, `edges`. Typed
edges: `supportedBy`, `overriddenBy`, `exceptionApplies`,
`scopedTo(jurisdiction, date, program)`. Validations on every build
(every claim has citations, no uncited inference steps, all wikilinks
resolve). One-way compilation — no reverse propagation.

**MCP server over SQLite.** Claude queries the compiled graph through
typed tools: `get_claim(id)`, `trace_support(claim_id)`,
`what_breaks_if(node_id)`, `list_conflicts()`, `search(query)`. This is
one working end-to-end PKM-to-LLM docking pattern. No Graphiti, no
Neo4j.

**Claims with epistemic status.** Each claim is atomic (one assertion)
with: numbered evidence items citing exact source sections with quotes,
explicit reasoning steps, `status: proven | provisional | open`. Proven
requires verbatim sources in the vault.

**Source cards.** What/Why/How pattern: what the source establishes,
which claims cite it and why (including impact analysis — "if this
changes, Claims 4, 10, and 11 need re-evaluation"), and operational
guidance. The KG compiler builds edges from claims→sources, and those
edges are written back into source card markdown as the "Why" section —
a curated form of bidirectional flow.

**Multi-model adversarial audit.** GPT 5 Pro audits Claude's analysis.
Concrete example: GPT caught an overstated claim, correction propagated
through the KB. Each model's output gets `status: untrusted_suggestions`
until verified against the axiom base.

### The Degradation Spectrum

The architecture degrades from formal proof (ACL2) through legal
reasoning (SNT) toward creative writing:

| Domain | Source quality | Proof style | Checker |
|---|---|---|---|
| ACL2 (formal) | Machine-verified | Formal, monotonic | Logic/prover |
| Legal (SNT) | Authoritative text | Conditional, defeasible | Auditability |
| Forums | Mixed quality | Anecdotal, fragile | Reproduction |
| Creative writing | ? | ? | ? |

The core that transfers across all domains: **source → claim → evidence
chain + invalidation triggers.** What degrades: monotonicity,
completeness, stability, the nature of "checking."

---

## The Domain Gap — Where the Prior Art Stops Transferring

This is the section that matters most for the vault.

The SNT project works because of specific domain properties:

- Sources have authority (POMS sections, statutes, regulations)
- Claims have falsification conditions ("this is wrong if SSA
  interprets A.3.c differently")
- "Proven" means something concrete (verbatim authoritative source in
  the vault)
- Evidence chains have clear directionality (this section says X,
  therefore Y)
- The compiler can validate because the rules are crisp

A creative vault doing raw notes and ideation has almost none of these:

- Sources are journal entries, readings, conversations, vibes — no
  authority hierarchy
- "Proven" doesn't mean anything for an aesthetic commitment or a
  half-formed idea
- Evidence is "I read this and it resonated" or "this connects to
  something I can't articulate yet," not "POMS A.3.c says..."
- Entity resolution might actively *destroy* useful ambiguity — merging
  related-but-distinct concepts kills distinctions the writer may be
  deliberately maintaining
- The Drift and Séance experiments are specifically about finding signal
  in creative mess, not about proving claims

### What Transfers (the plumbing)

The **Markdown → SQLite → MCP server** pipeline is domain-agnostic. It
doesn't care whether the atoms are legal claims or creative fragments.
The compilation step, the typed-tool interface, the one-way flow — all
of that works regardless of what's inside the atoms.

### What Doesn't Transfer (the epistemology)

The **claim schema** needs serious rethinking for creative work.

The research stops here. What follows is David's proposed adaptation —
not something the literature directly supports, but an attempt to fill
the gap it identifies:

Status types might become:
- `committed` — the writer has taken this position (not "proven," just
  chosen)
- `exploring` — actively being developed, not yet a commitment
- `tension` — deliberately maintained contradiction, productive
  ambiguity (the "intentional ambiguity" requirement)
- `dormant` — not currently active, might return
- `composted` — the Compost experiment's territory: neglected,
  possibly worth revisiting

Edge types might become:
- `resonatesWith` — felt connection, not argued
- `tensionWith` — productive contradiction
- `sourceOf` — where did this come from
- `developedFrom` — one idea growing out of another
- `complicates` — makes something harder without contradicting it

The SNT's `what_breaks_if(node_id)` is powerful in legal research
because invalidation is concrete. In creative work, the equivalent might
be `what_shifts_if(node_id)` — not "what breaks" but "what changes
character, what loses a productive tension, what becomes less
interesting."

### The Deeper Question

The degradation spectrum raises a real design question: at the creative
writing end, is the claim-evidence-chain pattern still the right shape,
or does it force a falsificationist epistemology onto a domain that
doesn't work that way?

The SNT conventions say "a fresh session can re-derive any conclusion
from the axiom base alone." In creative work, re-derivation may be
impossible — the value of some ideas is that they emerged from a
specific state of mind, a specific juxtaposition, a specific moment of
not-knowing. The trail matters more than the conclusion.

This doesn't mean structure is useless for creative work. It means the
structure might need to be **descriptive** (what happened, what
connected, what the writer noticed) rather than **argumentative** (what's
proven, what supports what, what invalidates what). The vault's atoms
are already closer to descriptive than argumentative. The research says
adding a claim layer is valuable — but for creative vaults, claims might
be lighter-weight commitments than the SNT model assumes.

---

## What This Changes

Here's how the research reshapes the path forward.

### Docking: Closer Than We Thought

The practical docking question has a workable current shape. File-first
plus scoped memory plus MCP is the strongest deployed pattern. The interesting
open question is "what structure to expose through the dock" — which is
where ideas about graph topology start to matter.

For the vault specifically: if the vault compiled its atoms into a
SQLite index with typed edges, an MCP server could answer "what connects
to this atom?" or "what's been dormant?" without reading the entire
vault. That's achievable now without Graphiti.

### Graph Intersection for Learning: The Live Research Edge

Nobody has built the full stack from open-ended personal PKM → curriculum
alignment → tutoring. The reformulation that makes it work:

1. Keep curriculum/domain graph authoritative and versioned
2. Treat learner side as overlay/evidence graph, not a second authority
3. Build the learner graph over claims/questions/evidence, not just
   concepts
4. Infer mastery from stronger signals than note presence
5. Run path planning on curriculum graph with learner overlay as weights

This is where a real contribution could be made.

### Graphiti: Probably Not Yet

The trigger to revisit: when flat-file semantic matching fails in
concrete, repeatable ways — specifically when you need durable typed
relations + prerequisite traversal + historical change queries +
subgraph operations that an LLM cannot reliably reconstruct on the fly.
We genuinely don't know where that threshold is.

A lighter middle ground worth testing: compile vault atoms into a SQLite
index with typed edges. That gives queryable structure and provenance
without the Graphiti overhead, and it's a step toward heavier tooling
if the need emerges.

### Temporal: Build Provenance First

1. Build append-only provenance and memory surfaces (the vault already
   has beginnings of this)
2. Model fast-vs-slow state change (what's ripening vs. what's active)
3. Only then decide whether a temporal KG is warranted

---

## Concrete Next Steps

1. **Add `claim` to the atom `kind:` enum.** Concepts stay as entities;
   claims add an argumentative layer. The vault agent already suggested
   this.

2. **Natural language relationship descriptions.** When `tend` creates a
   link, write a sentence explaining how the atoms relate. Costs nothing,
   preserves future optionality.

3. **Prototype the compiler pattern for creative work.** Take a subset
   of atoms, compile into SQLite with typed edges. But use creative-
   domain status types (`committed/exploring/tension/dormant/composted`)
   and edge types (`resonatesWith/tensionWith/sourceOf/developedFrom/
   complicates`) rather than importing the legal schema directly. See if
   `what_shifts_if(node_id)` is useful.

4. **Research the learner overlay.** The novel piece — personal vault as
   learner graph docking into a teaching graph — needs focused research
   next. Target: how to
   represent learner state as an overlay on an authoritative curriculum
   graph, using claims/questions/evidence, with mastery signals beyond
   note presence.

5. **Adopt the breakpoint rule explicitly.** Stay file-first until
   support/contradiction/change/path questions recur enough that
   maintaining a compiled artifact is cheaper than re-deriving
   structure from raw files. When that happens, move first to a one-way
   compiled layer; only consider graph-first infrastructure if it
   clearly pays rent in live work.

---

## Open Questions

- What does "mastery" even mean in a creative vault? Note presence is
  too weak. Quiz performance doesn't apply. What signals indicate that
  a writer has genuinely internalized a concept vs. merely recorded it?
- Can the vault's Drift experiment serve as a salience mechanism — the
  graph's way of knowing "what's pressing right now"?
- How does the Membrane (selective agent↔human surface) interact with
  the compiler pattern? Does compiled structure make Membrane surfacing
  better, or does it over-determine what gets surfaced?
- What concrete query/reuse profile would justify a compiled layer for
  this vault? The research says the breakpoint is not raw size alone.
  Can we design a test?

---

## Sources and References

Raw deep research replies are in
`daydreaming/Notes/DeepResearch/2026-03-12/john/replies/` — six query
files plus one architecture review. The prior landscape report
(`bidirectional_knowledge_text_coupling.md`, 654 sources) and its
generative conversation are also in the repo.

### Local Source Files

| Section | Source file(s) |
|---|---|
| Query 01 — PKM docking | `query01.md` |
| Query 02 — Graphiti | `query02.md` |
| Query 03 — Graph intersection | `query03.md` |
| Query 04 — Temporal KGs | `query04.md` |
| Query 05 — Learner modeling | `query05/chat_reply.md`, `query05/query05_integrated_answer.md` |
| Query 06 — File-first breakpoint | `query06.md` |
| Architecture review | `project_file_org.md` |

### Papers and Studies

- Graphiti paper — arXiv:2501.13956v1 —
  https://arxiv.org/html/2501.13956v1
- RAG vs. GraphRAG comparison — arXiv:2502.11371v1 —
  https://arxiv.org/html/2502.11371v1
- Microsoft GraphRAG — arXiv:2404.16130v2 —
  https://arxiv.org/html/2404.16130v2
- "Lost in the Middle" — arXiv:2307.03172v1 —
  https://arxiv.org/html/2307.03172v1
- OP-RAG / "In Defense of RAG" — arXiv:2409.01666v1 —
  https://arxiv.org/html/2409.01666v1
- GraphRAG-Bench — arXiv:2506.05690v3 —
  https://arxiv.org/html/2506.05690v3
- Long-context vs. structured memory cost — arXiv:2603.04814 —
  https://arxiv.org/abs/2603.04814
- EMNLP 2024 Self-Route (LC vs. RAG) —
  https://aclanthology.org/2024.emnlp-industry.66/
- RULER benchmark —
  https://openreview.net/forum?id=kIoBbc76Sy
- CAG (Cache-Augmented Generation) —
  https://openreview.net/pdf?id=EOG15VvlY4
- Pleskach et al. 2023 — student KG vs. discipline KG —
  https://ceur-ws.org/Vol-3646/Paper_11.pdf
- 2024 systematic review (KGs in education) —
  https://pmc.ncbi.nlm.nih.gov/articles/PMC10847940/
- Aleven et al. 2017 — Adaptive Learning Technologies chapter —
  https://www.cs.cmu.edu/~aleven/Papers/2016/Aleven_etal_Handbook2017_AdaptiveLearningTechnologies.pdf
- Abu-Rasheed et al. 2025 — linked domain/curriculum/user models with
  LLM-assisted KG completion — https://arxiv.org/abs/2501.12300
- Chen et al. 2024 — textbook target KG + student-dialogue KG —
  https://files.eric.ed.gov/fulltext/ED665357.pdf
- Yu-Hsiang Chen et al. 2025 — personalized learning-trajectory graphs —
  https://arxiv.org/abs/2504.11481
- InstructKG 2026 — instructor-aligned prerequisite graphs —
  https://arxiv.org/abs/2602.17111
- Ilkou 2022 — PKG for e-learning —
  https://files.eric.ed.gov/fulltext/ED665562.pdf (SkillTree context)
- CourseMapper PKG (UMAP 2024) —
  https://www.uni-due.de/imperia/md/content/soco/alatrash_ukde24_final.pdf
- Epistemic Network Analysis (ENA) —
  https://www.epistemicnetwork.org/
- 2024 ENA reflective-writing study —
  https://etasr.com/index.php/ETASR/article/download/7274/3645
- 2025 ENA scoping review in science education —
  https://link.springer.com/article/10.1007/s10956-024-10193-x
- 2025 NLP adaptive dialog (162 students) —
  https://www.mdpi.com/2227-7102/15/2/207
- Oxford chapter on pedagogically informed NLP —
  https://academic.oup.com/book/58946/chapter/492996783
- 2024 keystroke-logging writing study —
  https://www.sciencedirect.com/science/article/pii/S1060374324000201
- Concept-map topology study (359 maps, 36 students) —
  https://pmc.ncbi.nlm.nih.gov/articles/PMC9939863/
- 2025 English-learning system (WeChat Mini Program, 200 learners) —
  https://www.researchgate.net/publication/396169307
- Stealth assessment —
  https://files.eric.ed.gov/fulltext/ED612156.pdf
- KG-based path planners —
  https://pmc.ncbi.nlm.nih.gov/articles/PMC9748379/

### Systems and Platforms

- **Graphiti** (Zep) — https://github.com/getzep/graphiti
- **Graphiti backend/config docs** —
  https://help.getzep.com/graphiti/configuration/neo-4-j-configuration
- **Zep managed platform** — https://help.getzep.com/facts
- **Graphiti community OOM issue** —
  https://github.com/getzep/graphiti/issues/836
- **Graphiti generic `Entity` / ontology issue** —
  https://github.com/getzep/graphiti/issues/992
- **Microsoft GraphRAG** — https://microsoft.github.io/graphrag/
- **LazyGraphRAG** — https://www.microsoft.com/en-us/research/blog/lazygraphrag-setting-a-new-standard-for-quality-and-cost/
- **LlamaIndex PropertyGraphIndex** —
  https://developers.llamaindex.ai/python/framework/module_guides/indexing/lpg_index_guide/
- **LangChain graph stores** —
  https://blog.langchain.com/enhancing-rag-based-applications-accuracy-by-constructing-and-leveraging-knowledge-graphs/
- **Claude Code** — https://code.claude.com/docs/en/memory
- **Neo4j versioning** —
  https://neo4j.com/docs/getting-started/data-modeling/versioning/
- **TigerGraph temporal** —
  https://www.tigergraph.com/blog/utilizing-multi-edge-for-temporal-search-in-a-sales-agent-hierarchy/
- **TerminusDB** — https://terminusdb.org/docs/terminusdb-explanation/
- **Math Academy** — https://www.mathacademy.com/how-our-ai-works
- **ALEKS** — https://www.aleks.com/about_aleks/research_behind
- **Carnegie Learning MATHia** —
  https://www.carnegielearning.com/blog/mathia-ai
- **CZI/Learning Commons** —
  https://chanzuckerberg.com/blog/scaling-proven-learning-practices/
- **Recall** — https://docs.getrecall.ai/getting-started/6-review-content
- **FSRS scheduler** —
  https://github.com/open-spaced-repetition/fsrs4anki/wiki/The-Algorithm
- **Gemini URL Context** —
  https://ai.google.dev/gemini-api/docs/url-context

### Obsidian MCP Servers

- bitbonsai/mcpvault — https://github.com/bitbonsai/mcp-obsidian
  (~700 stars, active Mar 2026)
- MarkusPfundstein/mcp-obsidian —
  https://github.com/MarkusPfundstein/mcp-obsidian (~3k stars, stale)
- cyanheads/obsidian-mcp-server —
  https://github.com/cyanheads/obsidian-mcp-server (~397 stars)
- Piotr1215/mcp-obsidian —
  https://github.com/Piotr1215/mcp-obsidian (direct filesystem access)

### Standards and Frameworks

- **MCP** (Model Context Protocol) — https://code.claude.com/docs/en/mcp
- **xAPI** — learning activity stream standard
- **Caliper** (1EdTech) —
  https://www.1edtech.org/standards/caliper
- **ADL TLA** — https://www.adlnet.gov/guides/tla/service-definitions/
- **OAEI** (Ontology Alignment Evaluation Initiative) —
  https://oaei.ontologymatching.org/

### Graphiti GitHub Issues Referenced

- #836 — `build_communities()` OOM at ~1k entities —
  https://github.com/getzep/graphiti/issues/836
- #992 — scale/efficiency comparison —
  https://github.com/getzep/graphiti/issues/992
- #1166 — node attributes destructively upserted —
  https://github.com/getzep/graphiti/issues/1166
