# Research Results + Notes — For John

Date: 2026-03-12
From: David

Hey John — three things here. First, results from the deep research you
asked me to run based on our Graphiti/docking conversation. Second,
thoughts on the vault agent integration note you shared — the one where
Claude read the bidirectional coupling report against the Symbiotic
Vault. Third, some adjacent work that turned out to be relevant.

There's a full research package backing all of this up — it's in the
same folder as this memo, called `research-package-john-track.md`. If
you or your agent want to dig into the raw evidence, source links, and
platform-by-platform breakdowns, that's where it lives. This memo is
the highlights + my take.

---

## Part 1 — Deep Research Results

I derived research queries from our conversation and your Graphiti chat,
sent them to deep research, and got replies back for six threads. Five
are covered below. The sixth — when does file-first stop being enough,
and what should you build next — came back later and changed my
architecture thinking. I'm keeping it separate because I'd rather walk
through it with you than have it land as a document. The five threads
here stand on their own.

### Graphiti

I had them pressure-test the Graphiti thesis specifically — not just
the idea of knowledge graphs, but Graphiti's actual implementation.
GitHub issues, paper vs. docs, user reports, scale signals.

**Research found — what holds up:**
- It's a real temporal context-graph engine, not vaporware
- The bi-temporal edge model (4 timestamps, contradiction-based
  invalidation) is a genuine capability
- Neo4j is no longer a hard dependency — FalkorDB, Neptune, Kuzu also
  supported now

**Research found — what doesn't hold up as well as the pitch suggests:**
- **Entity resolution** — heuristic pipeline, no published
  precision/recall benchmarks. GitHub issues show duplicates and
  nondeterministic quality. Without developer-supplied `entity_types`,
  everything collapses to generic `Entity` labels.
- **Community detection** — paper says label propagation, docs say
  Leiden. `build_communities()` OOM-crashes at ~1,000 entities (1,210
  entities → 1,210 queries → process killed). "Frames as literal
  subgraphs" isn't production-ready yet.
- **Scale** — no published benchmarks. Operational signals: ~60 min to
  bulk-ingest 100 records with custom ontology, community rebuild OOMs.
  Zep themselves had to move vector search and BM25 out of the graph DB
  under real growth.
- **Docking as graph intersection** — not a built-in Graphiti
  capability. You'd build that on top.
- **At least one reported comparison** (cited in the research but
  primary source not pinned down) found mem0 beat Graphiti on efficiency
  without a statistically significant accuracy gap.

**My take:** Graphiti is real but rough, and it looks premature for a
creative vault at our current scale (<500 atoms). The rough filter from
the research: it earns its keep when entities recur across many
episodes + facts change over time + you repeatedly ask entity-centric
multi-hop questions where chronology matters. We probably don't meet
those conditions yet, but the crossover point is honestly unknown.

Not "never" — just "probably not yet." You may read the evidence
differently.

### PKM-to-LLM Docking

I had them survey what actually exists today for feeding an Obsidian
vault into LLM conversations.

**Research found:** The deployed path is file-first, not graph-first.
Claude Code's CLAUDE.md + scoped memory + MCP is the strongest current
docking pattern. Obsidian MCP servers exist but the ecosystem is
fragmented — bitbonsai/mcp-obsidian (~700 stars, active Mar 2026) is the
best alive general bridge; MarkusPfundstein/mcp-obsidian has the most
stars (~3k) but stale commits and user complaints. Gemini is strong for
curated packets (PDFs, exported slices) but has no live-vault coupling.
Cursor is unreliable for this.

**My take:** Usable docking exists but it looks like Markdown + scoped
memory + thin MCP bridges. The interesting open question isn't "how to
dock" anymore — it's "what structure to expose through the dock." That's
where your graph topology ideas start to matter.

### Graph Intersection for Learning

The individual pieces here all exist. What's novel is the specific
combination — and the research treats it well.

**Research found:** The concept exists as a composition of known pieces
— curriculum graphs, ontology alignment, LLM-based KG extraction,
mastery-aware path planning all exist separately. Pleskach 2023, Chen
2024, InstructKG 2026 are the closest ancestors. CZI/Learning Commons
has released an education KG with an MCP server for Claude — so
external teaching-graph docking into LLMs is happening. But nobody has
built the full stack from open-ended personal PKM → curriculum alignment
→ tutoring. The deployed platforms (Knewton, ALEKS, MATHia, OLI) all
use authoritative curriculum graphs with internal learner models — none
accept a user-owned PKM as the learner graph.

**My take:** The novel piece is specifically using a personal vault as
the learner graph that docks into an external teaching graph. Each
component exists separately — nobody has built the full stack from
open-ended PKM to curriculum alignment to tutoring. That's the live
research edge.

**Two refinements from the literature:**

1. The papers say "projection," "overlay," or "alignment" rather than
   "intersection." The learner graph works better as an overlay on an
   authoritative curriculum graph, not a second authority.

2. "Shortest path" is too topological. The stronger adaptive learning
   systems (Knewton, ALEKS, and newer KG-based planners) optimize for
   best feasible mastery path under constraints — prerequisites,
   forgetting, difficulty, time. "Lowest-cost mastery path" is the
   better frame.

### Temporal Knowledge Graphs

**Research found:** Temporal fact storage is real (Graphiti's 4-timestamp
edges, Neo4j temporal types, TerminusDB's git-for-data). Temporal
personal learner models barely exist. Math Academy is the strongest live
example of graph-shaped learner modeling, but it's platform-owned. No
deployed system infers evolving understanding by watching KG topology
changes — the ones that get closest use quiz performance, explicit
learner signals, and diagnostics.

A fifth thread came back specifically on **learner modeling without
explicit assessment** — can vault artifacts (journals, drafts, atoms)
serve as a passive mastery signal?

**Research found:** Artifact-based learner modeling works when artifacts
are structured enough or the environment is instrumented enough (revised
explanations, concept maps, instrumented process traces). But passive
PKM-as-mastery-graph is still under-validated. The strongest cases all
start from explicit diagnostic data or explicit learner signals, not
silent inference from note topology. Useful event types the research
suggests tracking: `asserted claim`, `revised claim`, `added support`,
`noted contradiction`, `returned after delay`, `successfully recalled`,
`failed recall`, `explicitly marked confusion/confidence`.

**My take:** The valuable temporal object isn't timestamped facts — it's
tracked change in commitments, salience, confidence, and neglected
regions, with provenance. That's a modeling question, not an
infrastructure question. You don't need Graphiti to start tracking that.
Append-only provenance, session logs, and commitment-state tracking get
you most of the way. The vault should be treated as an evidence stream,
not a self-certifying mastery graph — and low-friction explicit signals
matter more than hoping silent inference is good enough.

---

## Part 2 — On the Vault Agent Integration Note

You shared a note where Claude read the bidirectional coupling report
against the Symbiotic Vault. I'm engaging the note's content here, not
assuming it's your settled position — just working through what it
raises.

**The three concrete suggestions are good:**
- **Add `claim` to the atom `kind:` enum** — agreed, this is the most
  important move. Concepts stay; claims add the argumentative layer.
- **Natural language relationship descriptions** when `tend` creates
  links — smart, low-cost, preserves future optionality.
- **Epistemic relations during atomization** — richer without requiring
  structural changes.

**One thing the note was missing context on:** The report's
bidirectionality thesis. The note says "you're right not to care about
it the way the report does" — but the report was born from a specific
conversation about whether you could have both a research assistant that
builds a graph AND a writing environment where the graph is the
interface. Bidirectionality wasn't a peripheral finding in the report,
it was the central question.

The note's probably right that the vault doesn't need automated
real-time prose↔graph sync. But there's a deeper version of this
question that the vault's own design experiments are already wrestling
with: how does agent work flow back to influence human work? The
Membrane, the Drift, the Séance are all experiments in designed
bidirectional flow — lossy, mediated, curated. That's a different thing
from automated sync, and it seems worth keeping on the table even if
the engineering problem of real-time sync isn't relevant.

Not a big disagreement — more like the note answered the engineering
question and skipped the design question. Curious what you think.

---

## Part 3 — Adjacent Work That's Relevant

### The Atom Question — Claims Alongside Concepts

The vault agent note and the broader research agree here. The discourse
graph literature converges on a small typed substrate:

- Joel Chan (UMD): Questions, Claims, Evidence with typed rhetorical
  edges. Deployed Obsidian + Roam plugins with real users.
- Nanopublications: Assertion + provenance + publication context.
- eRST: Discourse relation graphs with 40+ signal types.

The lesson isn't "replace concepts with claims" — it's "add claims as a
layer alongside concepts." Concepts are entities. Claims have positions
that can be supported, opposed, refined. The vault agent note already
suggested this. The research backs it up.

### A Working Docking Pattern — SNT Project

**Context, not prescription:** I've been building something in a
different domain (supplemental needs trust legal research) that turned
out to be relevant. Sharing because the structural patterns might be
useful, not because the vault should adopt this wholesale. The pattern:

- **Markdown → SQLite compilation with typed edges.** Markdown stays
  source of truth. Python compiler builds a queryable index with claims,
  evidence chains, typed edges (`supportedBy`, `overriddenBy`,
  `exceptionApplies`). One-way compilation — no reverse propagation, so
  the bidirectional sync problem is sidestepped.

- **MCP server over the compiled graph.** Claude queries through typed
  tools: `get_claim(id)`, `trace_support(claim_id)`,
  `what_breaks_if(node_id)`, `list_conflicts()`, `search(query)`. This
  is PKM-to-LLM docking working end-to-end without Graphiti or Neo4j.

- **Claims with epistemic status.** `proven | provisional | open`.
  Proven requires verbatim sources in the vault.

The plumbing generalizes — Markdown → SQLite → MCP is domain-agnostic.
But the epistemology doesn't transfer directly to a creative vault.
Legal research has authoritative sources, falsifiable claims, concrete
"proven." Creative work has journal entries, aesthetic commitments,
productive contradictions.

### The Domain Gap

**My take — this is the part I think matters most for your vault.** The
SNT plumbing (Markdown → SQLite → MCP) generalizes. But if we adapt the
compilation pattern for creative work, the schema needs to be different.

The research stops here. What follows is my proposed adaptation — not
something the literature directly supports, but my attempt to fill the
gap it identifies:

**Status types for creative work might be:**
- `committed` — the writer has taken this position
- `exploring` — actively developing, not yet a commitment
- `tension` — deliberately maintained contradiction
- `dormant` — not currently active
- `composted` — neglected, possibly worth revisiting (the Compost
  experiment's territory)

**Edge types might be:**
- `resonatesWith` — felt connection, not argued
- `tensionWith` — productive contradiction
- `sourceOf` — provenance
- `developedFrom` — one idea growing out of another
- `complicates` — makes something harder without contradicting it

The SNT project's `what_breaks_if(node_id)` is powerful because
invalidation is concrete in legal research. The creative equivalent
might be `what_shifts_if(node_id)` — not "what breaks" but "what
changes character, what loses a productive tension."

The deeper question: at the creative end, is claim-evidence-chain still
the right shape, or does it force a falsificationist epistemology onto
a domain that doesn't work that way? The structure might need to be
**descriptive** (what happened, what connected, what the writer noticed)
rather than **argumentative** (what's proven, what supports what). Your
vault's atoms are already closer to descriptive. Adding a claim layer is
valuable, but creative claims might be lighter-weight commitments than
the legal model assumes.

---

## Where This Leaves Us

**Things to do now:**
1. Add `claim` to the atom `kind:` enum
2. Natural language relationship descriptions when `tend` links atoms
3. Consider prototyping a compiler: take a subset of atoms, compile to
   SQLite with creative-domain status/edge types, see if
   `what_shifts_if(node_id)` is useful

**Things to research next:**
4. Your graph intersection for learning idea is the strongest original
   contribution. Worth a focused deep research on: how to represent
   learner state as an overlay on a curriculum graph, with mastery
   signals beyond note presence
5. The sixth research thread (architecture strategy) — I want to walk
   through this one with you live. It addresses what to build and when.

**Things to wait on:**
6. Graphiti — revisit when flat-file matching fails in concrete ways
7. Temporal KG infrastructure — build provenance surfaces first, model
   commitment change, then decide

The full research package has all the evidence — platform comparisons,
specific GitHub issues, paper citations, deployed system details. Let me
know what you want to dig into.
