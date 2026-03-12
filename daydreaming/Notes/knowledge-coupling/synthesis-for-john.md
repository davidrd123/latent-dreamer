# Deep Research Synthesis — Responding to John's Track

Date: 2026-03-12

This document synthesizes what came back from four deep research queries
targeting John's Graphiti/docking/learning thesis, cross-referenced with
a prior landscape report on bidirectional knowledge-text coupling (654
sources), the generative conversation that produced it, and a working
implementation in a different domain (SNT legal research).

The goal is honest engagement: where John's instincts are confirmed,
where they need refinement, and what the concrete path forward looks like.

---

## What the Research Confirms

**Your core instincts are right in several places.**

1. **Explicit topology matters.** The difference between "soup of
   paragraphs" and "network of concepts and relationships" is real.
   Vector DBs lose structural relationships that matter for learning,
   argumentation, and navigation. The entire discourse graph literature
   (Chan's QCE model, nanopublications, eRST) validates this.

2. **Temporal awareness is valuable.** Tracking how understanding
   evolves — what was believed when, what changed, what caused the
   change — is a real requirement. The research confirms this as
   important, and Graphiti's bi-temporal edge model (four timestamps,
   contradiction-based invalidation) is a genuine capability.

3. **Docking as alignment between knowledge systems is the right
   frame.** The idea of an external teaching agent arriving with its
   own knowledge graph and negotiating against a learner's graph is
   coherent and exists as a composition of known research pieces
   (Pleskach 2023, Chen 2024, InstructKG 2026). CZI/Learning Commons
   has already released an education KG with an MCP server for Claude —
   the ecosystem is moving toward machine-readable curriculum graphs
   docking into LLMs.

4. **PKM-to-LLM docking is a real unsolved category.** The practical
   tool ecosystem is fragmented — there is no settled standard for how
   a personal knowledge vault supplies structured context to an LLM
   session. This is a space where contributions matter.

---

## Where the Research Pushes Back

### Graphiti Specifically

The deep dive into Graphiti's actual implementation (GitHub issues, paper
vs. docs, user reports, operational signals) found significant gaps
between the pitch and the reality.

**Entity resolution** is heuristic, not magic. No published
precision/recall benchmarks. GitHub issues show duplicates and
nondeterministic quality. Without developer-supplied `entity_types`, all
entities collapse to generic `Entity` labels. "Automatic ontology
learning" is only partially true.

**Community detection** is unstable. Paper says label propagation, docs
say Leiden — implementation inconsistency. `build_communities()`
OOM-crashes at ~1,000 entities. This means "frames as literal subgraphs/
communities" is not production-ready.

**Scale is rough.** No published benchmarks at 100/10K/100K nodes.
Operational signals: ~60 minutes to bulk-ingest 100 records with custom
ontology, 10 messages in ~2 minutes, community rebuild OOMs. Zep itself
had to move vector search and BM25 out of the graph DB under real growth.
Default concurrency kept low to avoid LLM rate limits.

**The comparison set matters.** Microsoft GraphRAG is better for static
corpora. LlamaIndex PropertyGraphIndex is a lighter middle ground. A
2026 independent study found mem0 beat Graphiti on efficiency without a
statistically significant accuracy gap. Plain flat-file semantic matching
is still the right default for small-to-medium PKM.

**A rough filter** (from the deep research, not a settled threshold —
Graphiti doesn't publish the benchmark we'd need to be precise):
Graphiti starts to earn its keep when all three hold: (1) entities recur
across many episodes, (2) facts about them change over time, (3) you
repeatedly ask entity-centric multi-hop questions where chronology
matters. A personal creative vault with <500 atoms probably doesn't meet
these conditions yet, but the crossover point is genuinely unknown.

**This doesn't mean "graphs are bad."** It means Graphiti specifically
looks premature for this use case at this scale. A lighter compilation
layer may be the right near-term step — see Prior Art below for one
working approach.

### "Shortest Path" for Learning

One refinement from the adaptive learning literature: stronger systems optimize
for **best feasible mastery path under constraints** (prerequisites,
forgetting, difficulty, time, uncertainty), not minimum hops. Knewton's
remediation depth, ALEKS's outer fringe, and newer KG-based path
planners all fit this pattern. "Shortest path" should be translated to
"lowest-cost mastery path under constraints."

### The Atom Question

The Graphiti pitch is concept-centric: "AI," "LLMs," "ChatGPT" as
entities that get resolved and linked. That layer is real and useful —
entity resolution and concept linking are genuine capabilities. But the
broader research suggests there's a layer above concepts that does more
work: **claims, questions, and evidence** as typed atoms alongside
entities, not replacing them.

- Joel Chan (UMD): Questions, Claims, Evidence with typed rhetorical edges
- Nanopublications: Assertion + provenance + publication context
- eRST: Discourse relation graphs with 40+ signal types
- KSG: "Micro-ideas" from student discourse linked to synthesis nodes

The reusable lesson: use a small typed substrate, keep provenance and
discourse separate. Your vault agent already suggested adding `claim` to
the atom `kind:` enum — that's the right move. Concepts stay as entities;
claims add the argumentative layer that the Séance and Table experiments
need to push against something.

### Temporal KGs for Learning

Temporal fact storage exists and is real (Graphiti, Neo4j, TigerGraph,
TerminusDB). Temporal personal learner models barely do.

No deployed system infers a person's evolving understanding by watching
changes in their knowledge graph topology. The deployed systems that get
closest (Math Academy, ALEKS) use quiz performance, explicit learner
signals, and diagnostic questions — not graph topology drift alone.

The valuable temporal object is not timestamped facts. It is **tracked
change in commitments, salience, confidence, and neglected regions, with
provenance.** That's a modeling question, not an infrastructure question.

---

## Adjacent Prior Art — SNT Project

There's a working implementation in a different domain that de-risks
several of the patterns the research recommends. David built this for
supplemental needs trust legal research (SNT project) — different
subject matter, but the structural patterns transfer and show what's
achievable with lighter infrastructure than Graphiti:

**Markdown → SQLite compilation with typed edges.** Markdown stays source
of truth. A Python compiler builds a queryable SQLite index with claims,
claim_evidence, decision_points, tasks, and typed edges (`supportedBy`,
`overriddenBy`, `exceptionApplies`, `scopedTo`). Validations run on
every build. No reverse propagation needed — one-way compilation
sidesteps the bidirectional sync problem entirely.

**MCP server as docking mechanism.** Claude queries the compiled graph
through typed tools: `get_claim(id)`, `trace_support(claim_id)`,
`what_breaks_if(node_id)`, `list_conflicts()`, `search(query)`. This IS
PKM-to-LLM docking, already working. No Graphiti, no Neo4j, no external
graph infrastructure.

**Claims with epistemic status.** Each claim is atomic (one assertion)
with: numbered evidence items citing exact source sections with quotes,
explicit reasoning steps, `status: proven | provisional | open`. Proven
requires verbatim sources in the vault.

**Source cards with load-bearing analysis.** What/Why/How pattern: what
the source establishes, which claims cite it and why (including impact
analysis — "if this changes, Claims 4, 10, and 11 need re-evaluation"),
and operational guidance for action.

**Multi-model adversarial audit.** GPT 5 Pro adversarially audits
Claude's analysis. Working example: GPT caught that a claim was
overstated, the correction propagated through the KB. Each model's
output gets `status: untrusted_suggestions` until verified against the
axiom base.

**The degradation spectrum.** This architecture degrades gracefully from
formal proof (ACL2) through legal reasoning (SNT) toward creative
writing. The core pattern that transfers: source → claim → evidence
chain + invalidation triggers. What degrades: monotonicity, completeness,
the nature of "checking."

---

## What This Means for Your Ideas

Here's how the research reshapes the path forward.

### Docking: Closer Than We Thought

The practical docking question has a working shape. File-first +
CLAUDE.md/scoped memory + MCP is the strongest deployed pattern right
now, and the SNT project shows one version of it working end-to-end. The
interesting open question shifts from "how to dock" to "what structure to
expose through the dock" — which is where your ideas about graph topology
start to matter.

For the Symbiotic Vault specifically: if the vault compiled its atoms
into a SQLite index with typed edges, an MCP server could answer "what
supports this claim?" or "what would break if this source changed?"
without reading the entire vault. That's achievable now without Graphiti.

### Graph Intersection for Learning: The Live Research Edge

This is where a real contribution could be made. Nobody has built the
full stack from open-ended personal PKM → curriculum alignment →
tutoring. The research validates each piece separately:

- Authoritative curriculum graphs exist (Knewton, CZI/Learning Commons)
- Ontology alignment is mature (OAEI, LogMap, AML)
- LLM-based KG extraction from course materials works (Chen 2024,
  InstructKG 2026)
- Mastery-aware path planning exists (ALEKS, ALPN, newer KG-based
  planners)

What nobody has done: use a local personal knowledge system as the
learner graph that docks into an external teaching graph. That's the
exact move your architecture proposes. The reformulation that makes it
work:

1. Keep curriculum/domain graph authoritative and versioned
2. Treat the learner side as an **overlay/evidence graph**, not a second
   authoritative ontology
3. Build the learner graph over claims/questions/evidence, not just
   concepts
4. Infer mastery from stronger signals than note presence (assessments,
   self-ratings, successful recall, dialogue evidence, task performance)
5. Run path planning on curriculum graph with learner overlay as weights

### Graphiti: Probably Not Yet

The research doesn't rule Graphiti out — it says the evidence for
adopting it now is weak at current vault scale. The trigger to revisit:
when flat-file semantic matching fails in concrete, repeatable ways —
specifically when you need durable typed relations + prerequisite
traversal + historical change queries + subgraph operations that an LLM
cannot reliably reconstruct on the fly. We genuinely don't know where
that threshold is.

A lighter middle ground worth testing: compile vault atoms into a SQLite
index with typed edges (the pattern from the SNT project). That gives
you queryable structure and provenance without the Graphiti
infrastructure overhead, and it's a step toward Graphiti-scale tooling
if the need emerges.

### Temporal: Build Provenance First

The recommended sequence:
1. Build append-only provenance and memory surfaces (dated journals,
   session logs, agent protocol provenance — the vault already has the
   beginnings of this)
2. Model fast-vs-slow state change (what's ripening vs. what's active)
3. Only then decide whether a temporal KG is warranted

The insight: "version control for commitments" is more valuable than
"timestamped facts."

---

## Concrete Next Steps

1. **Vault agent suggestion — implement now:** Add `claim` to the atom
   `kind:` enum. The vault's atoms are concept-notes. Claims have
   positions that can be argued with, which is what the Séance and Table
   experiments need.

2. **Natural language relationship descriptions — implement now:** When
   `tend` creates a link, write a sentence explaining how the atoms
   relate. Costs nothing, preserves future optionality.

3. **Prototype the compiler pattern:** Take a subset of the vault's
   atoms and compile them into a SQLite index with typed edges. See if
   `what_breaks_if(node_id)` is useful for creative work. This tests the
   SNT pattern in the creative domain without committing to Graphiti.

4. **Research the learner overlay:** Your graph intersection idea is
   the strongest original contribution. The next deep research should
   target specifically: how to represent learner state as an overlay
   on an authoritative curriculum graph, using claims/questions/evidence
   rather than concept nodes, with mastery signals beyond note presence.

5. **Ask about Thread 6:** The "literary vs. infrastructure
   architectures" query — the strategic decision thread — was not
   returned from deep research. That question still needs answering:
   is this a writing tool that happens to have a graph, or a graph tool
   that happens to produce writing?
