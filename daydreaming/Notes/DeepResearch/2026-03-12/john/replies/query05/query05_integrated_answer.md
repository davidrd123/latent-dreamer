# Query 5 — Learner modeling without explicit assessment

Integrated synthesis for the `knowledge-coupling` track.

## Bottom line

There is real evidence that learner artifacts can support learner modeling, but the strongest validated cases are not free-form PKM vaults. They are revised explanations, reflective writing, concept maps, and instrumented process traces inside designed learning environments.

So Query 5 comes back **yes in narrow pieces, no in the strong repo-specific form**.

## What the repo already establishes

- The real question is not temporal storage. It is what counts as evidence that a learner knows, half-knows, forgot, or restructured something.
- The strongest local target is tracked change in commitments, confidence, salience, and unresolved tensions with provenance.
- The relevant learner artifacts in this workspace are journals, project notes, drafts, atoms, frames, and memory traces.
- The repo does not yet justify treating those artifacts as a reliable passive mastery model.

## What Round 1 already established

- `query04.md`: temporal KG infrastructure exists, but temporal learner modeling for a personal vault mostly does not.
- `query04.md`: event and evidence logging matters more than raw topology drift.
- `query03.md`: the learner model is usually an overlay on an authoritative curriculum graph, not a personal PKM graph standing in for mastery.
- `bidirectional_knowledge_text_coupling.md`: KSG, discourse graphs, ARTIST, and related systems show that LLM-mediated extraction from discourse and writing is feasible, but do not solve free-form personal-vault mastery inference.

## What fresh research adds

### Deployed / at least operational

- Math Academy is the strongest live graph-aware learner model found. It overlays student answers on a knowledge graph, computes a knowledge profile, uses diagnostics to estimate a knowledge frontier, and lets repetitions trickle through the graph.
- CourseMapper PKG work is transparent and scrutable, but it relies on explicit learner markings like `Did Not Understand`, `Understood`, and `New`.
- A 2025 Scientific Reports English-learning system deployed as a WeChat Mini Program combines graph propagation, forgetting, and reinforcement learning over real interaction logs. It is operational, but still begins from explicit diagnostic and interaction data.
- Consumer note systems like RemNote support spaced repetition and graph-like note structures, but the strongest scheduler evidence is still FSRS-style per-card memory modeling, not graph-driven mastery inference.

### Published prototypes with evaluation

- ENA is a serious method for modeling connections among coded elements over time, and it has spread beyond discourse to other educational artifacts.
- Reflective-writing studies show ENA can distinguish stronger and weaker reflective patterns across multiple writing tasks over time.
- Knowledge-integration studies show that revised explanations after adaptive NLP dialogues can reveal conceptual change, and that revision behavior itself predicts later gains.
- Writing-process analytics show that keystroke logs, self-regulated learning transitions, and time allocation patterns can predict output quality and differ between stronger and weaker performers.
- Concept-map studies show that structural artifacts can be analyzed as graphs over time, and automated comparison against reference maps can detect missing concepts and misconceptions.

### Still thin / proposal territory

- Personal PKM artifacts as a passive learner model.
- ENA applied to something like a free-form Obsidian vault.
- Graph-topology-only knowledge-decay detection from notes.
- A strong proof that journals + drafts + links alone can substitute for explicit assessment or explicit learner signals.

## Net answer for John’s track

The right conclusion is not “graphs solve learner modeling.” The right conclusion is:

1. **Artifact-based learner modeling is plausible** when the artifacts are structured enough or the environment is instrumented enough.
2. **Passive PKM-as-mastery-graph is still under-validated**.
3. **The learner model should remain an overlay**, not an authority.
4. **Provenance and event logging are the core move**.

## Recommended repo move

Treat the vault as an evidence stream, not a self-certifying mastery graph.

Useful event types:
- asserted claim
- revised claim
- added support
- noted contradiction
- linked concepts
- returned after delay
- successfully recalled
- failed recall
- abandoned thread
- explicitly marked confusion / confidence / uncertainty

Then maintain a lightweight learner overlay with:
- claim/question/evidence units
- confidence
- salience
- provenance
- revision history

Use explicit low-friction signals where ambiguity matters. Do not pretend silent inference is already good enough.
