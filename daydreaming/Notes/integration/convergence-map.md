# Convergence Map

Three parallel tracks are developing in this workspace. They share
vocabulary, structural concerns, and unsolved problems, but no document
has traced the connections until now. This note is that bridge.

Last updated: 2026-03-12

---

## The Three Tracks

### Track A: Conducted Daydreaming (the build)

A persistent audiovisual daydream system. An offline engine prepares a
branching scene graph around unresolved situations; a real-time runtime
traverses that graph into video (Scope) and audio (Lyria) while canonical
world state only changes at explicit event commits.

Key docs:
- `daydreaming/Notes/home-base.md`
- `daydreaming/Notes/experiential-design/03-architecture.md`
- `daydreaming/Notes/ProspectiveDesign/v2/conducted-daydreaming-architecture-v2.md`
- `daydreaming/Notes/focused-source-trace.md`

Core structural commitments:
- Mueller-derived emotional scheduler (7 competing goal types)
- Coincidence retrieval (threshold mark counting, not embedding similarity)
- Director layer (LLM interprets Dreamer state through a style guide)
- Lossy bidirectional feedback (Director output → concept extraction → Dreamer index shift)
- World state / daydream state firewall

### Track B: Symbiotic Vault (teammate — vault design)

An Obsidian vault designed for human-AI creative collaboration. Ideas are
the center. Agents maintain structure and surface connections. Human and
agent surfaces are separated but aspire toward symbiosis.

Key docs:
- `team-repos/Symbiotic-Vault/_system/VAULT_VISION.md`
- `team-repos/Symbiotic-Vault/_system/AGENT_PROTOCOL.md`
- `team-repos/Symbiotic-Vault/_system/_design/DESIGN_CRITIQUE.md`
- `team-repos/Symbiotic-Vault/_system/_design/THE_DRIFT.md`
- `team-repos/Symbiotic-Vault/_system/_design/THE_SEANCE.md`
- `team-repos/Symbiotic-Vault/_system/_design/THE_MEMBRANE.md`
- `team-repos/Symbiotic-Vault/_system/_design/THE_COMPOST.md`
- `team-repos/Symbiotic-Vault/_system/_design/THE_TABLE.md`

Core structural commitments:
- Atoms as single-concept notes (Zettelkasten-style)
- Frames as interpretive lenses (not just tags — perspectives with concerns and vocabulary)
- Provenance as first-class ("the trail is the treasure")
- Agent surfaces vs. human surfaces (separation with aspiration toward membrane)
- Inference-time semantic matching (no persistent graph — LLM reads flat files)

### Track C: Knowledge Infrastructure (teammate — Graphiti/docking)

PKM-to-LLM docking via knowledge graphs. Personal knowledge represented
as a graph (not a vector store), with frames as literal subgraphs, temporal
awareness, and docking as graph intersection with external knowledge sources.

Key docs:
- `daydreaming/Notes/PreviousWork/outside/notes_team.md`
- `daydreaming/Notes/DeepResearch/2026-03-12/john/asks/threads_from_chat.md`

Core proposals:
- Graphiti/Zep over Neo4j as the KG backbone
- Entity resolution to clean messy notes into typed concepts
- Temporal tracking of how relationships evolve
- Docking = graph intersection (external teaching agent queries your KG, finds the gap)
- The Double Graph Problem (Obsidian wikilinks vs. Graphiti semantic graph)

---

## The Deep Research That Bears On All Three

`daydreaming/Notes/DeepResearch/2026-03-11/bidirectional_knowledge_text_coupling.md`

Five convergences the field is reaching:

1. **Claims as atoms** — not documents, not concepts. Granular, attributed,
   evidence-linked assertions with typed rhetorical relations (supports,
   opposes, informs). Joel Chan's QUE-CLM-EVD model is the clearest version.

2. **Graphs over trees** — humanities knowledge has non-hierarchical,
   overlapping, multi-perspectival structure. eRST, discourse graphs,
   property graphs in DH all reflect this.

3. **Provenance as first-class** — nanopublications, ATR4CH, HiCo, GOLEM
   all treat attribution and interpretive context as essential structure.

4. **LLMs as bridge technology** — Discourse Grammar Parser, ARTIST,
   Graphologue all use LLMs to mediate between prose and structure,
   approaching interactivity.

5. **The bidirectional sync gap** — text-to-graph extraction is mature;
   graph-to-text generation is advancing; keeping both synchronized as
   edits occur is **fundamentally unsolved**. No production system does it.

---

## Where The Tracks Converge

### The Drift is the Dreamer's ROVING applied to knowledge

The vault's Drift experiment (aimless wandering through the atomic layer)
is structurally the same as the Dreamer's ROVING goal type (escape from
aversive pressure by pleasant drift through recalled episodes). Both:

- Start without a destination
- Follow associative links rather than systematic traversal
- Self-report their character (tails vs. norm / dense cluster vs. sparse)
- Produce narrative logs, not structured outputs
- Value the surprise of unexpected connections

The Drift even uses the same language as Mueller: "curiosity with no
destination." The difference is medium — the Dreamer drifts through a
scene graph toward video; the Drift wanders through atoms toward a
written log. The traversal logic could be shared.

### The Séance is asking the Director question for knowledge

The vault's Séance asks: can a frame, given enough accumulated reflections,
produce *writing* that has recognizable character? The Conducted Daydreaming
Director asks: can an LLM, given Dreamer state plus a style guide, produce
*imagery* that has recognizable sensibility?

Both are testing whether accumulated context + defined perspective =
something that resembles taste. Both require:

- A body of accumulated observations (frame reflections / Director memory)
- A defined perspective (frame definition / style guide + lens)
- A creative output that is more than summary (draft fragment / visual prompt)
- A feedback mechanism where the output changes what comes next

The Director has something the Séance currently lacks: the feedback loop.
The Director's novel concepts feed back into the Dreamer and shift future
traversal. The Séance is currently stateless between invocations — the
design critique flagged this. Wiring feedback from séance outputs back
into the atomic layer (new atoms, updated connections, sharpened frame
definitions) would make frames develop the way Director lenses develop.

### The Membrane is the feedback loop problem

The vault's Membrane aspires to: agent offerings appearing in the margins
of your working surface, with freeze/dismiss/respond/cut interactions
that shape future surfacing. This is the same problem as the Dreamer↔Director
feedback loop:

- Two systems with different surfaces (agent/human, Dreamer/Director)
- Lossy channel between them (the membrane is selectively permeable;
  the feedback reduces rich imagery to concept tokens + valence)
- The pattern of interaction shapes future behavior (curation signals;
  novel index injection)
- The engineering challenge is making the loop fast and transparent enough
  to feel like one system, not two loosely coupled artifacts

The deep research confirms this is the field's central unsolved problem
(bidirectional sync). The Dreamer↔Director loop sidesteps it by making
the feedback deliberately lossy — the Dreamer doesn't try to stay in sync
with the Director's full output, it just absorbs a compressed echo. The
Membrane could adopt the same strategy: don't try to sync the agent's
full understanding with the human's working surface. Surface compressed
offerings. Accept compressed responses. Let the lossy channel be the
feature, not the bug.

### The Compost is survivorship bias analysis — applicable everywhere

The vault's Compost reads neglected material as signal. This applies
directly to Conducted Daydreaming: which situations never got traversed?
Which dream graph regions are always bypassed? Which episodes never
surface through coincidence retrieval? The pattern of neglect in a dream
graph is diagnostic the same way it is in a knowledge vault.

It also applies to Track C: in a knowledge graph, which entities have
zero inbound edges? Which temporal relationships decayed without ever
being queried? The Compost is a general pattern for reading absence,
not a vault-specific tool.

### Claims vs. concepts changes the atomic layer for both A and B

The deep research's strongest finding: the field is converging on
**claims** (evidence-linked assertions with typed rhetorical relations)
as the fundamental atom, not concepts.

This challenges both:

- **Track B's atoms** — currently single-concept notes. If atoms were
  claims with supports/opposes/informs relations, frames could *argue*
  with them rather than just observe them. The Séance needs this to work.

- **Track A's episodes** — currently indexed by situation, goal_type,
  emotion, place, recency. If episodes also carried claim-like structure
  (what was asserted, what was contradicted, what evidence emerged),
  coincidence retrieval could match on rhetorical structure, not just
  thematic overlap.

### The bidirectional sync gap validates Track B's current approach

Track B (the vault) uses inference-time semantic matching — the agent
reads flat markdown files and understands them. No persistent graph.
Track C (Graphiti) proposes building an explicit knowledge graph.

The deep research shows that the persistent-graph approach creates a
sync problem nobody has solved. The vault's approach sidesteps this
entirely. The inference-time strategy isn't just simpler — it may be
structurally correct for the current moment.

The question for Track C becomes: at what scale does inference-time
matching break down and *require* a persistent graph? And if you build
the graph, can you treat it as a one-way enrichment layer (text → graph
extraction only, no reverse sync) rather than attempting bidirectional
propagation?

### The Table is a multi-agent arrangement problem

The vault's Table experiment (accumulate fragments, then have frames
argue for different orderings) has a future version: fragment advocates
who negotiate sequence. This is architecturally similar to Conducted
Daydreaming's competing goal types — multiple agents with different
priorities arguing for what comes next, with the collision producing
structure that no single agent could design from above.

The vault's aspirational "advocates" and the Dreamer's competing goal
types are both implementations of Society of Mind scheduling.

---

## Open Questions For Integration

1. **Should the vault's atoms become claims?** If so, what's the migration
   path? Can the atomize skill be updated to extract QUE-CLM-EVD triples
   from journal entries instead of single-concept notes?

2. **Can the Dreamer's traversal logic serve the Drift?** The coincidence
   retrieval mechanism (threshold mark counting, serendipity as threshold-1)
   could drive vault wandering. Is it worth extracting the traversal engine
   as a shared library?

3. **Should the Séance wire feedback?** The Director's feedback loop
   (novel concepts → index injection → shifted retrieval) could be applied
   to frame reflections. A séance produces a draft fragment; the fragment's
   novel concepts feed back into the atomic layer as new connections or
   updated frame state. Does this make frames develop over time?

4. **Is the Membrane actually the Dreamer↔Director feedback loop in a
   different medium?** If so, can the same lossy-channel architecture
   (compress to tokens + valence, accept compressed responses, let decay
   handle drift) work for human-agent surfacing?

5. **At what scale does inference-time matching need a persistent graph?**
   The vault currently has ~5 atoms and ~7 journal entries. At 500 atoms?
   5,000? Where does the LLM's context window become the bottleneck, and
   does Graphiti become necessary at that point?

6. **Can the Compost pattern generalize?** Neglected dream graph regions,
   neglected vault atoms, neglected KG entities — same analysis, different
   substrate. Worth building once?

---

## Status

This document is a living map. Update it as the three tracks develop.
The connections identified here are structural, not speculative — they
reflect shared mechanisms, shared problems, and shared unsolved questions
across projects that are being built in parallel.
