# Knowledge Coupling

Working space for developing ideas around bidirectional knowledge-text
coupling, PKM-to-LLM docking, and the structural primitives that make
both possible.

This is not integration (that's `../integration/`). This is where the
ideas themselves get worked through.

## Source Material

- `../DeepResearch/2026-03-11/bidirectional_knowledge_text_coupling.md`
  — the landscape report. Five convergences, the bidirectional sync gap,
  claims as atoms, LLMs as bridge technology.

- `../DeepResearch/2026-03-12/john/asks/threads_from_chat.md`
  — six deep research threads derived from John's Graphiti/docking chat.

- `../PreviousWork/outside/notes_team.md`
  — the original conversation notes with John.

## Threads To Develop

- **Claims vs. concepts** — the field converges on claims (QUE-CLM-EVD)
  as the fundamental atom. What does that mean for personal knowledge
  systems that currently use concept-notes? Migration path, tradeoffs,
  what you gain (argumentation, typed relations) vs. what you lose
  (simplicity, low-friction capture).

- **Docking protocols** — how a PKM supplies context to an LLM
  conversation happening elsewhere. What standards exist (MCP, discourse
  graph protocol, nanopublication infrastructure). What "situated
  inferencing" actually requires at the protocol level.

- **The sync question** — text-to-graph extraction is mature.
  Graph-to-text generation is advancing. Bidirectional sync is unsolved.
  Is lossy one-way extraction the right near-term bet? At what scale
  does inference-time matching break down?

- **Graphiti evaluation** — pressure-testing John's thesis. Where
  knowledge graphs genuinely outperform vector DBs and inference-time
  matching. Where the infrastructure overhead doesn't pay for itself.

- **Temporal knowledge** — tracking how understanding evolves over time.
  Graphiti's temporal features, spaced repetition via graph topology,
  knowledge decay detection.
