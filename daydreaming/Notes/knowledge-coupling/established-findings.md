# Established Findings

What we know about bidirectional knowledge-text coupling — from the
deep research, the generative conversation that produced it, the
cross-model critique, and the vault agent's response. This document
carries the reasoning, not just the conclusions.

Last updated: 2026-03-12

Sources:
- `../DeepResearch/2026-03-11/bidirectional_knowledge_text_coupling.md`
  — the landscape report (654 sources, 8m45s)
- `../DeepResearch/2026-03-12/chats/2026-03-12_15-27-18_Claude_Chat_Exploring_layered_approaches_to_project_challenges.md`
  — the generative conversation (Claude + GPT 5.4 Thinking critique)
- `john-text/vault-agent-response-to-bidirectional-research.txt`
  — John's vault agent reading the research against the Symbiotic Vault
- `../DeepResearch/2026-03-12/john/replies/query01.md` — PKM-to-LLM docking landscape
- `../DeepResearch/2026-03-12/john/replies/query02.md` — Graphiti pressure test
- `../DeepResearch/2026-03-12/john/replies/query03.md` — graph intersection for learning
- `../DeepResearch/2026-03-12/john/replies/query04.md` — temporal knowledge graphs
- `../DeepResearch/2026-03-12/john/replies/queries_01-04.md` — local synthesis calibration

---

## The Generative Question

The deep research didn't start from an academic interest in knowledge
graphs. It started from a practical question about writing:

> "Is it wrong to want both?" — a research assistant that builds a
> structured graph AND a writing environment where the graph is the
> primary interface?

The answer was no — they're two phases of the same loop. The research
assistant builds the graph during intake; the writing environment
renders it navigable during composition; the writer's act of writing
modifies the graph (new edges, revised weights, collapsed tensions).
The hard part isn't either half — it's keeping the graph and the prose
as two views of one object.

The concrete vision that emerged:

> "You're writing a paragraph, you make a claim, the environment
> quietly surfaces 'this connects to [source node X] via
> [transformation Y]' or flags 'nothing in your graph supports this
> yet.' Not as a popup. More like margin annotations that are always
> there, updated live. And when you write something that resolves a
> tension edge, the graph updates."

This is the Symbiotic Vault's Membrane experiment, described before
the Membrane was designed. It is also structurally the same as the
Conducted Daydreaming Dreamer↔Director feedback loop — two systems
with different surfaces influencing each other through a lossy channel.

**Bidirectionality is not a peripheral finding. It is the entire
thesis.** Any engagement with this research that dismisses
bidirectionality is missing what question the research was answering.

---

## Writing Has Layers (Not All Need Protection)

The conversation decomposed writing assistance into four layers:

| Layer | What | AI Role | Controversy |
|---|---|---|---|
| 0 | Research / intake | Great already | Low |
| 1 | Structure / architecture | Useful sparring partner | Low (if framed as outlining) |
| 2 | Sentence-level craft | **Corrosive if generated for you** | High — this is the load-bearing layer |
| 3 | Revision / editing | Good at this | Low |

The territory where AI assistance destroys the work is Layer 2, plus
the *discovery* part of Layer 1 (where you figure out what you think
by writing it). The rest is infrastructure.

Implication for tool design: the graph and the surfacing system operate
at Layers 0, 1, and 3. They must never touch Layer 2 — the sentence,
the voice, the rhythm. The Membrane's "margin annotations" respect
this: they surface connections and flag gaps without touching the prose.

---

## The Four Update Channels (from GPT 5.4 Thinking Critique)

GPT's critique decomposed "prose-to-graph" into four separable channels
that should NOT all hit the same graph layer:

1. **Referential updates** — entity, coreference, disambiguation.
   "He" = Slothrop, "the rocket" = 00000.

2. **Commitment updates** — what the text is now committed to, denied,
   presupposing, or leaving open.

3. **Rhetorical updates** — contrast, concession, emphasis, framing,
   foreground/background.

4. **Epistemic/stylistic updates** — hedge, confidence, evidential
   status, narrator stance, irony risk.

Why this matters: "Maybe X happened" is not just a weak assertion node.
It's a commitment with specific modal status, possibly attributed to a
speaker, possibly only live within a local discourse frame. Collapsing
these channels makes the system brittle fast.

A sentence often doesn't add a world-fact. It adds one of: a
hypothesis, a narration-level framing move, a local comparison, a
reminder, a viewpoint attribution, a temporary planning commitment by
the writer. Those are different object types.

---

## The Salience Requirement

GPT's critique identified the biggest omission in the original framing:

> "The graph is the medium through which local actions achieve global
> coherence" is good, but incomplete. It's missing the scheduler.

A static graph is a dead warehouse. Global coherence in writing
depends on at least three things:

- **State** — what's currently committed
- **Salience** — what is active right now
- **Control** — what the writer is trying to do in this paragraph

Without salience and control, the graph becomes a Notion wiki graveyard.
The revised meta-pattern:

> "The structured model is the memory substrate through which local
> actions can inherit global constraints, but only when coupled to a
> salience model and a control model."

This is why plain knowledge graphs feel dead. They store relations but
don't model what is currently pressing, unresolved, or instrumentally
relevant.

Connection to Conducted Daydreaming: Mueller's scheduler IS a salience
model. Activation, decay, coincidence retrieval, competing goal types
— all mechanisms for answering "what's pressing right now?" The vault's
atomic layer has no equivalent. The Drift is an attempt to add one.
The Membrane is another. Without bidirectional flow, there's no
mechanism for the graph to know what's pressing.

---

## Intentional Ambiguity

An open design question from the generative conversation:

> What happens when the writer disagrees with the graph? Not "corrects
> an extraction error" but actively wants to maintain a contradiction,
> because the contradiction is productive, or because they haven't
> decided yet, or because the tension is the point of the essay.

The system needs a way to say "I see this tension, I'm keeping it
deliberately" that's different from "I haven't noticed this tension."
Annotated intentional ambiguity is a first-class design requirement
for literary/humanities use.

Connection to vault frames: frames that see different things in the
same material are maintaining productive contradictions. The Séance
asks a frame to *write* from its perspective, which means taking a
position the writer might disagree with. That disagreement is
generative, not a bug.

---

## Research Findings — What the Field Has Built

### Small Typed Substrates (Not "Claims Win")

GPT's correction to the research: the report doesn't show broad
convergence on "the claim" as the atom. It shows convergence on a few
claim-centered local schemas in specific subcommunities:

- **Chan (UMD):** Questions, Claims, Evidence (QCE) with typed
  rhetorical edges. Deployed Obsidian + Roam plugins with real users.
  Discourse Grammar Parser converts informal prose to formal relations.
- **Nanopublications:** Assertion + provenance + publication context.
  Most mature. ARPHA Writing Tool does inline export from manuscripts.
- **eRST:** Discourse relation graphs (not trees). 40+ signal types.
  200K+ token annotated corpus across 12 genres.
- **KSG:** "Micro-ideas" from student discourse linked to "Synthesis
  Nodes" with epistemic relations.

The reusable lesson is NOT "claim wins." It is: **use a small typed
substrate, and keep provenance and discourse separate.**

### Boring Atoms, Rich Overlays

GPT's architecture recommendation (80% confidence):

- **Primary graph:** claims, entities, relations, conflicts, supports
- **Secondary layer:** resonance, motif echo, structural analogy,
  thematic pressure

Do not start with "resonance" as a primary atom — it becomes a vibes
machine. Do not start with giant humanities ontologies — too heavy.
Keep high-level interpretation provisional.

### Provenance Is Non-Negotiable

Every graph update must carry at minimum:
- **Source span** — what exact text triggered it
- **Scope** — global fact, local section, quoted speech, hypothetical,
  outline note
- **Attribution** — narrator, character, author note, cited source
- **Confidence** — system confidence, not just writer confidence
- **Reversibility** — can the writer undo or reclassify it quickly

Without this, the graph becomes haunted — stuff appears with unclear
origin and unclear ontological status. That kills trust.

> "Provenance is not a nice-to-have. It is the difference between an
> instrument and a parasite."

### The Bidirectional Sync Gap

No production system maintains automatic real-time bidirectional sync
between prose and a knowledge graph. The specific engineering problem
of "edit a graph node and have the prose update automatically" is
unsolved.

What gets closest:
- **ARTIST:** Argumentation graph alongside text. 5.1 arguments vs 3.2
  with baseline. 60% structural improvement in student writing.
- **Graphologue:** Extract-modify-regenerate cycle. Partial feedback.
- **ORKG Reviews:** Narrative Markdown with inline structured references.
  Human-curated sync.

Most viable near-term path: **LLM-mediated extract-modify-regenerate
cycle** — not continuous automated sync, but fast enough loops that
the system feels responsive.

### The Evaluation Path Is Tractable

GPT decomposed evaluation into iterable metrics:

- **Intrinsic:** precision/recall of extraction, commitment
  classification accuracy, contradiction detection, provenance
  correctness, user correction rate
- **Interaction:** time to resolve inconsistency, useful tensions
  surfaced, interruption burden, acceptance vs dismissal rate
- **Outcome:** coherence ratings by blinded readers, citation coverage,
  revision efficiency, self-reported cognitive load

The far metric ("did this help the writer produce better work?") is
hard. The near metrics are iterable today.

### The Beachhead Domain

All three voices (Claude, GPT, research) converged: start with
**argumentative research writing from source packets** — not fiction,
not Pynchon. Specifically: dissertation chapter drafting, review essays
from annotated sources, archival argument construction. This is where
Chan has users, ARTIST has positive results, nanopublications have
infrastructure, and eRST has training data.

### The Composability Prize

The individual writing support use case is the beachhead, but the
network effects from composable structured arguments could be the
actual prize. Your evidence graph connecting to my evidence graph.
Historians interfacing at the claim level rather than the citation
level. Nanopublications and Chan's "decentralized discourse graph"
protocol both point at this. Nobody's built it yet. This connects
directly to John's "docking as graph intersection" idea.

---

## Engagement with the Vault Agent Response

Source: `john-text/vault-agent-response-to-bidirectional-research.txt`

The vault agent read the research report (without the generative
conversation) and produced a competent local analysis. Here's what
it got right and what it got wrong.

### Missing Context: Who Was the Agent Responding To?

The message opens with "This is a useful check. Let me work through
your specific flags..." — so it's responding to John, who fed the
research report into the vault agent and raised specific concerns.
**We don't have John's input — only the response.** But the agent's
phrasing "You're right not to care about it the way the report does"
indicates John explicitly expressed that bidirectionality wasn't
relevant to the vault. The agent is *agreeing* with John's stated
position, not just inferring it.

This matters because the dismissal is now a two-party decision:
John decided the vault doesn't need to care about bidirectionality,
and the agent reinforced that reading. Neither had access to the
generative conversation that produced the research, where
bidirectional coupling was the central question — not a peripheral
finding. And neither connected the research's bidirectionality
problem to the vault's own Membrane, Drift, and Séance experiments,
which are all attempts to solve designed bidirectional flow.

**Action:** Ask John for his original prompt/flags so we have the
full exchange and can understand what specifically he was dismissing.

### What the Vault Agent Got Right

**Add `claim` to the atom `kind:` enum.** Correct and overdue. The
vault's atoms are concept-notes. Claims have positions that can be
argued with, which is what the Séance and the Table need to work.
A concept is "highlights and hides." A claim is "asking an LLM what
a framing conceals is epistemic literacy." The second one gives a
frame something to push against.

**Natural language relationship descriptions.** Smart middle path
between untyped wikilinks and formal edge types. When tend creates
a link, write a sentence explaining *how* the atoms relate — not just
a bare `[[wikilink]]` but "This connects to [[highlights-and-hides]]
because it's a specific instance of the general method." Readable now,
parseable later, costs nothing structurally. Preserves future
optionality for Graphiti without committing to infrastructure.

**Epistemic relations during atomization.** When atomize creates an
atom from a journal entry, note in the atom's prose: "This claim
emerged as a challenge to [[existing-atom]]." Richer atomization
without structural changes.

**PARA mapping.** The vault maps to PARA with better creative-work
semantics. Frames as Areas (ongoing concerns) is stronger than PARA's
maintenance-oriented Areas for ideation work.

**Three things the vault is already doing right:** provenance as
first-class, graphs over trees (flat atom pool with wikilinks), LLMs
as bridge technology (the entire collaboration model).

### What the Vault Agent Got Wrong

**The dismissal of bidirectionality.** The agent said: "You're right
not to care about it the way the report does. The report is obsessed
with the unsolved problem of real-time sync. Your vault doesn't have
that problem because the human writes prose, the agent maintains the
graph, and they're loosely coupled by design."

This conflates two things:

1. The specific *engineering* problem of automated real-time prose↔graph
   sync — which genuinely isn't what the vault needs.
2. The *design* problem of how agent work flows back to influence human
   work and vice versa — which is the vault's central open question.

The vault's own DESIGN_CRITIQUE.md identifies this as "the deepest
tension":

> "We drew such a clean line between human and agent surfaces that
> they're barely cohabiting — roommates who leave notes on the fridge."

The Membrane experiment is specifically designed to solve this — agent
offerings in the margins, with freeze/dismiss/respond/cut interactions
shaping future surfacing. That IS bidirectional coupling, mediated and
lossy by design. The Drift needs it (discoveries must surface during
writing, not sit in `_memory/`). The Séance needs it (frame outputs
must feed back into the atomic layer for frames to develop). The
design critique flagged stateless frames as a problem.

The agent made a correct narrow claim (the vault doesn't need automated
real-time sync) and used it to dismiss a broader design concern that
the vault's own documents identify as the central unsolved problem.

**Missing: the four update channels.** The vault agent treated
bidirectionality as one monolithic thing. GPT's decomposition into
referential, commitment, rhetorical, and epistemic/stylistic channels
suggests that some directions of flow are more tractable than others.
The vault could start with commitment tracking (what has the human's
writing settled or left open?) without attempting full discourse
analysis.

**Missing: the salience layer.** The vault's atomic layer has no
mechanism for "what's pressing right now." Atoms just sit there. The
Drift is an attempt to add salience through wandering. The Membrane
would add it through contextual surfacing. Without bidirectional flow,
neither mechanism works — there's no signal from the human's current
work to drive what gets surfaced.

**Missing: intentional ambiguity.** The vault agent's three suggested
changes (add `claim`, natural language relations, claim-aware table)
are all good but don't address the question of productive
contradictions. Frames that see different things in the same material
need a way to maintain disagreement as a feature, not resolve it.

**Missing: the composability dimension.** The vault agent assessed
bidirectionality purely as a single-practitioner question. But when
David is integrating toward teammates — when John's Graphiti graph
needs to dock with the vault's atomic layer — the asymmetry that
"works for a single creative practitioner" may not hold. The docking
question IS a bidirectionality question across systems.

### Net Assessment

The vault agent's three concrete suggestions (add `claim`, natural
language relations, claim-aware table) should be implemented. They're
good on their own merits.

But "bidirectionality doesn't matter for us" is the wrong takeaway.
The right takeaway: **automated** bidirectional sync is unsolved and
probably not what the vault needs. But **designed** bidirectional flow
— lossy, mediated, curated — is exactly what the Membrane, the Drift,
the Séance, and the Compost are all reaching for. The research gives
patterns for how to build it:

- Graphologue's extract-modify-regenerate cycle
- ARTIST's side-by-side graph+prose view (5.1 vs 3.2 arguments)
- The Dreamer↔Director lossy feedback channel
- Chan's Discourse Grammar Parser for prose→structure
- The "graph must earn the right to exist by paying rent in live
  cognition" design constraint

---

## The Converged v1 Architecture

All three voices (Claude, GPT, research) converged on:

1. **Primary types:** Question, Claim, Evidence, Source
2. **Mandatory metadata:** source span, attribution, scope, confidence,
   revision log
3. **Secondary overlays:** discourse relation labels, epistemic status
4. **Primary UI:** outline/prose editor (Tana-like)
5. **Bridge actions:** select span → create node; click node → highlight
   source text; graph changes produce suggested prose patches, not
   direct rewrites
6. **Salience layer:** what's pressing, unresolved, instrumentally
   relevant — coupled to current writing context
7. **Evaluation:** correction rate, acceptance rate, time-to-resolve-
   tension, whether users keep the graph alive after drafting

This is specific enough to build. The beachhead is argumentative
research writing from source packets.

---

## Prior Art: SNT Research as Working Implementation

Source: `/Users/daviddickinson/Documents/Obsidian/SD_Coord/David_SNT/SNT_Research/`

David has already built a working version of the converged v1
architecture in a different domain (supplemental needs trust legal
research). The SNT project implements nearly everything the research
and cross-model critique recommend, applied to legal reasoning rather
than academic writing.

### What's Already Built

**Conventions document** (`_conventions.md`): Modeled on ACL2-style
proof-carrying reasoning — axioms (sources) → theorems (claims) →
proofs (grounding chains). A fresh session can re-derive any conclusion
from the axiom base alone.

**Note types:**
- `type: source` — immutable axiom base. Verbatim when feasible.
  `source_mode: verbatim | excerpt | summary` tracks fidelity.
- `type: analysis` — claims about the specific situation, grounded in
  sources. `status: proven | provisional | open`. `proven` requires
  verbatim sources in the vault.
- `type: reference` — general knowledge derived from sources, not
  specific claims.

**Claim schema:** Each claim is atomic (one assertion) with:
- Numbered evidence items citing exact source sections with quotes
- Explicit reasoning steps (even when trivial: "Directly follows")
- Inline `> [!question]` callouts near blocking claims
- Bottom work queue collecting all open questions with claim IDs

**Compiled graph** (`snt_kg_schema.md`): Markdown stays source of
truth. A Python compiler builds a SQLite index with:
- `claims`, `claim_evidence`, `decision_points`, `tasks`, `edges`
- Typed edges: `supportedBy`, `overriddenBy`, `exceptionApplies`,
  `scopedTo(jurisdiction, date, program)`
- Validations on every build (every claim has citations, no uncited
  inference steps, all wikilinks resolve)

**MCP server** over SQLite:
- `get_claim(id)` — text, status, evidence chain, source links
- `trace_support(claim_id)` — full dependency tree
- `what_breaks_if(node_id)` — everything downstream of a source/fact
- `list_conflicts()` — contradictory or flagged evidence
- `search(query)` — FTS with filtering

**This IS PKM-to-LLM docking, already implemented.** Claude queries
structured knowledge through typed tools rather than reading raw files.
The MCP server is the docking mechanism.

### The Degradation Spectrum

The `acl2_kg_connection.md` document theorizes how proof-carrying
degrades across domains:

| Domain | Source quality | Proof style | Checker |
|---|---|---|---|
| ACL2 (formal) | Machine-verified | Formal, monotonic | Logic/prover |
| Legal (SNT) | Authoritative text, versioned | Conditional, defeasible | Auditability |
| Forums (Unreal) | Mixed quality, reputation | Anecdotal, fragile | Reproduction |
| Creative writing | ? | ? | ? |

The core that transfers across ALL domains: **source → claim →
evidence chain + invalidation triggers**. What degrades: monotonicity,
completeness, stability, the nature of "checking."

The creative writing domain is one step further down the spectrum.
Claims in a creative vault don't have clear authority hierarchy or
falsification conditions. But they still have: sources (journal
entries, readings), positions (what the writer believes/argues),
evidence (passages, examples), and invalidation conditions (what
would change the writer's mind, what new information would undercut
the claim).

### Source Cards: The What/Why/How Pattern

The `Meta/SourceCards/` directory contains enriched cards for key
source documents, using Jim White's tripartite summarization adapted
to legal research:

- **What** — the rule + key sections (what does this source establish?)
- **Why** — which claims cite it, what decisions it informs, derived
  from KG edges + manual annotation. Includes **load-bearing status**:
  "This is the single most cited POMS section in the KB. If SSA's
  interpretation of A.3.c changes, Claims 4, 10, and 11 all need
  re-evaluation."
- **How** — operational guidance: what to cite, what to ask the
  attorney, what to argue. "Cite A.3.c when challenging the 'just
  redirect payment to the trust' approach."

This is exactly what the Symbiotic Vault's `atomize` skill tries to
do, but more disciplined. The source card doesn't just say "this
connects to these atoms." It says *how* it connects (cited by claim
C04 for *this specific reason*), what would change if it changed
(impact analysis), and what you should *do* with it (operational
guidance).

The source cards also create a **designed feedback loop**: the KG
compiler builds edges from claims→sources, and those edges are
written back into source card markdown as the "Why" section. That's
a curated form of bidirectional flow — not automated sync, but
human-mediated enrichment where the graph's structure informs the
documentation of each source.

### Multi-Model Adversarial Audit

The SNT project uses GPT 5 Pro to adversarially audit Claude's
analysis. Session 2 documents a concrete example:

- Claude built Claims 10-12 evaluating Jay's plan
- GPT 5 Pro caught that Claim 11 ("dual penalty") was overstated —
  SI 01150.121A.1 creates a carve-out for assets already counted as
  resources
- The correction was integrated: Claim 11 downgraded from "dual
  penalty proven" to "excess resources proven / transfer penalty
  provisional"

This is a working bidirectional loop between different AI systems,
mediated by the human. The models challenge each other's claims, the
human judges, and the KB gets corrected. Each model's output is marked
with epistemic status: chat sessions get `status: untrusted_suggestions`
until their findings are verified against the axiom base.

### Session Logs as Provenance

Every session log captures: what was done, design decisions (with
explicit reasoning for each), files created/modified/deleted, open
questions carried forward, and exact state for next session ("read
these 7 files in this order to resume"). This IS "the trail is the
treasure" — the session logs are the provenance record for the
entire KB's evolution.

The session logs also carry **state for resumption after compaction**.
When context hits ~135k/200k tokens and compacts, the session log
ensures nothing is lost. A fresh session reads the log and can
reconstruct full context from the vault's state. This solves a
problem the Symbiotic Vault's `_memory/` directory is also trying
to solve — but more explicitly.

### The Outbox Pattern

The `Outbox/` directory contains drafts of messages to real people
(attorney questions with POMS citations, message to Jay about holding
claims, message to Jim White about proof-carrying parallels). These
are actionable outputs grounded in the KB's claims — the knowledge
graph producing artifacts for human consumption and action.

The attorney questions are particularly instructive: each question
cites specific POMS sections and claim IDs, so the attorney can
trace the reasoning. "If you're relying on carrier assignment to
the existing SNT, how do you get around A.3.c?" — that's a question
generated by the graph's structure, not by general concern.

### What This Teaches About Knowledge Coupling

1. **The conventions document is the model for carrying reasoning.**
   Every finding should have a source, every inference should be
   explicit, status should be declared. This is what the original
   version of established-findings.md was missing — conclusions
   without the reasoning that produced them. The SNT conventions
   are operational — a fresh session can re-derive any conclusion
   from the axiom base alone.

2. **One-way compilation sidesteps bidirectional sync.** Markdown
   stays the source of truth. A compiler builds a queryable index.
   No reverse propagation needed. This is the viable near-term
   approach the research recommends. But source cards show that
   *curated* feedback (compiler output informing the documentation
   of sources) IS a form of designed bidirectional flow.

3. **The vault agent's "natural language relationship descriptions"
   are a step backward from what's already built.** The SNT project
   has typed edges (`supportedBy`, `overriddenBy`, `exceptionApplies`,
   `scopedTo`) in a queryable graph, plus source cards with
   load-bearing analysis and impact tracing. The vault agent proposed
   writing relationship descriptions in prose as a compromise. David
   has already gone further in another project.

4. **The MCP server pattern IS docking.** If the Symbiotic Vault
   compiled its atoms into a SQLite index with typed edges, an MCP
   server could answer "what supports this claim?" or "what would
   break if this source changed?" without reading the entire vault.
   This is exactly the PKM-to-LLM docking that John's chat notes
   were asking about — and it's already built and working.

5. **Multi-model adversarial audit is a form of bidirectional
   coupling.** Two AI systems challenging each other's claims,
   with the human judging and the KB correcting — this IS the
   "designed bidirectional flow" that the vault's Membrane aspires
   to. It's lossy, mediated, curated. And it works: GPT caught a
   real error in Claude's analysis, and the correction propagated
   through the KB.

6. **Session logs solve the compaction/resumption problem.** The
   vault's `_memory/` directory tries to solve this for agent
   activity. The SNT session logs do it more explicitly: exact file
   lists for resumption, design decisions with reasoning, open
   questions as work queue. The pattern generalizes.

7. **The outbox pattern shows knowledge → action.** The graph
   doesn't just store relationships — it generates actionable
   outputs (attorney questions with POMS citations, messages to
   collaborators). The question "If you're relying on carrier
   assignment, how do you get around A.3.c?" is generated by graph
   structure. This is what "paying rent in live cognition" looks
   like in practice.

8. **The degradation spectrum maps to creative writing.** Legal
   research (SNT) sits between formal proof (ACL2) and creative
   writing on the spectrum. The pattern that transfers: source →
   claim → evidence chain + invalidation triggers. In creative
   writing, claims are weaker (positions, arguments, aesthetic
   commitments), evidence is weaker (passages, examples, resonances),
   and invalidation is subjective (what would change the writer's
   mind). But the structure holds. The Symbiotic Vault's atoms
   could adopt this structure without adopting the formality.

---

## Commercial Landscape

- **Tana:** Closest to "writing environment as graph editor." Every
  note/bullet is a graph node. Supertags type notes as #claim,
  #question, #source. Outline editor IS the graph editor.
- **Elicit:** Research Agents (Dec 2025). Structured internal
  representations, user interacts via prompts/reports.
- **Scite.ai:** 1.6B+ citations classified as supporting, contrasting,
  or mentioning.
- **InfraNodus:** Text as networks, topical clusters, gap detection.

None offer argument maps or discourse graphs navigable while composing.

---

## Digital Humanities

- **ATR4CH:** LLM-based knowledge extraction for cultural heritage.
  Handles scholarly disagreement. 0.96-0.99 F1 metadata, 0.65-0.75
  F1 hypotheses. Error-budget intuition: automate entity/evidence/
  provenance extraction aggressively; treat interpretation and
  hypothesis as provisional proposals with easy reversal.
- **Pelagios Network:** LLMs + Linked Open Data workshop (Sep 2025).
- **GOLEM:** Narratology + CIDOC-CRM + DOLCE. Pluralistic
  interpretations, fictional entities.
- **Actor-Network Theory:** Maps to graph DBs but no system fully
  realizes Latourian principles computationally. The enrollment/
  ally-accumulation model (how a claim gains strength by connecting
  to more evidence) is still our contribution if we want it.

---

## Deep Research Replies — Pressure-Testing John's Track

Four queries sent to deep research (2026-03-12), targeting John's Graphiti/
docking/learning thesis. Raw replies in `../DeepResearch/2026-03-12/john/
replies/`. A separate local-only synthesis (`queries_01-04.md`) ran the
same queries against the repo's own corpus as calibration — separating
what was already implied locally from what the external research added.

Threads 5 (temporal KGs for learning specifically) and 6 (literary vs.
infrastructure architectures — the strategic decision thread) were not
returned. Thread 6 was flagged as high priority. That gap means the
strategic framing question is still unanswered.

### Docking Landscape (Query 01)

The deployed path today is **file-first, not graph-first**. The external
research confirmed what the repo's architecture already implied.

**Claude Code as docking mechanism.** CLAUDE.md scoping (user, project,
managed-policy), `.claude/rules/` for path-scoped instructions, auto
memory as plain Markdown, MCP as transport. The reply positions this as
the strongest current PKM-to-LLM docking implementation. This validates
the repo's "files are primary, semantic matching at inference time" stance
as state of the art, not as a workaround.

**Obsidian MCP servers — fragmented, no winner:**
- bitbonsai/mcpvault (~700 stars, active Mar 2026) — best alive general
  bridge, but no published releases
- MarkusPfundstein/mcp-obsidian (~3k stars) — highest adoption, stale
  commits (Apr 2025), maintenance risk
- Piotr1215/mcp-obsidian (16 stars) — most philosophically aligned with
  file-first stance (direct filesystem, no Obsidian dependency)
- aaronsb/obsidian-semantic-mcp — archived Sep 2025, skip

**Gemini:** Strong for curated packets (PDFs, exported slices, URL
context, caching). No live-vault coupling. "Here's a textbook, reason
over it" — not "live inside my vault."

**Cursor:** Right nouns (rules, MCP, @Docs), unreliable execution.
Users report flaky doc indexing, global-not-project scoping. Not
recommended as memory authority.

**Peter Danovich:** Unverifiable. No public writeup, repo, or talk found.
Treat as unconfirmed hearsay.

**Net finding:** Usable docking exists now, but it looks like Markdown +
scoped memory + thin bridges, not a settled PKM operating system. The
SNT project's MCP-over-SQLite pattern is ahead of the deployed ecosystem.

### Graphiti Under Pressure (Query 02)

This is the sharpest reply. It goes point-by-point through John's claims
with specific evidence.

**Entity resolution: overstated.** Heuristic pipeline (extract, embed
into 1024-d space, cosine + full-text candidate search, LLM prompt to
judge duplicates). No published precision/recall benchmark. GitHub issues
show duplicates and nondeterminism. Without developer-supplied
`entity_types`, all entities collapse to generic `Entity` labels.

**Temporal evolution: real but narrower than claimed.** Bi-temporal fact
management with four timestamps on edges (`created_at`, `valid_at`,
`invalid_at`, `expired_at`). Automatic invalidation on contradiction.
But: node attributes are destructively upserted, not versioned. This is
temporal fact management, not automatic learner mastery modeling.

**Communities/frames as subgraphs: implementation inconsistent.** Paper
says label propagation, docs say Leiden with label-propagation-inspired
incremental updates. `build_communities()` OOM-crashes at ~1k entities
(1,210 entities → 1,210 queries → process killed). Do not build meaning
on community nodes until this stabilizes.

**Docking as graph intersection: not a built-in Graphiti capability.**
Would need to be built on top.

**Scale: no published benchmarks.** Operational signals: ~60 min to
bulk-ingest 100 records with custom ontology, 10 messages ~2 min,
community rebuild OOMs. Zep itself had to move vector search and BM25
out of the graph DB at scale. Default concurrency kept low
(`SEMAPHORE_LIMIT=10`) to avoid LLM 429 errors.

**Neo4j dependency: partly stale.** Graphiti now also supports FalkorDB,
Amazon Neptune + OpenSearch, and Kuzu. Neo4j is the default center of
gravity, no longer a hard lock.

**Comparison set:**
- Microsoft GraphRAG — better for static corpora and landscape summaries.
  75% of indexing cost is graph extraction. No temporal invalidation.
- LlamaIndex PropertyGraphIndex — lighter middle ground. Schema-guided
  or free-form extraction, pluggable graph/vector stores. No bi-temporal
  invalidation or community maintenance.
- LangChain graph stores — glue, not a coherent system.
- Plain flat-file semantic matching — still the right default for
  small-to-medium PKM.
- mem0 — a 2026 independent study found mem0 beat Graphiti on efficiency
  without a statistically significant accuracy gap.

**The filter for when Graphiti earns its keep:** All three must be true
simultaneously: (1) entities recur across many episodes, (2) facts about
them change over time, (3) you repeatedly ask entity-centric multi-hop
questions where chronology matters. The vault does not currently meet
these conditions.

**Bottom line:** "If the job is 'help an LLM read my Obsidian vault,'
Graphiti is usually too much." Graphiti is off the table for now — not
permanently, but until flat files fail in concrete, repeatable ways.
The SNT compiler pattern (Markdown → SQLite with typed edges) is the
lighter middle ground the comparison set points toward.

### Graph Intersection for Learning (Query 03)

John's most original idea. The external research treats it respectfully:
**coherent, exists as a composition of known pieces, but nobody has built
the full stack from open-ended PKM → curriculum alignment → tutoring.**

**Literature terminology correction:** The papers say "projection,"
"integration," "overlay," or "alignment" — not "intersection." Closest
ancestors: Pleskach 2023 (student KG vs. discipline KG, gap detection),
Chen 2024 (LLM-extracted KGs from both course materials and student
dialogue), InstructKG 2026 (prerequisite/part-of graphs from course
materials for gap diagnosis).

**Deployed platforms (Knewton, ALEKS, MATHia, OLI):** All use
authoritative curriculum graphs with internal learner models. None accept
user-owned PKM as the learner graph. The learner model is always
platform-internal.

**CZI/Learning Commons signal:** Released an education KG with MCP
server for Claude — external teaching-graph docking into an LLM exists.
Learner-side docking does not yet.

**"Shortest path" correction:** Pedagogically wrong. Stronger systems
optimize for best feasible mastery path under constraints (prerequisites,
forgetting, difficulty, time, uncertainty), not minimum hops. John's
"shortest path" should be translated to "lowest-cost mastery path under
constraints."

**The reformulation that survives:** Not concept-graph intersection, but
alignment over questions, claims, evidence, uncertainty, and current
salience. The learner graph should be an overlay/evidence graph, not a
second authoritative ontology. Mastery should be inferred from stronger
signals than note presence alone: assessments, explicit self-ratings,
repeated successful recall, dialogue evidence, task performance.

**Near-term architecture:**
1. Keep curriculum/domain graph authoritative and versioned
2. Treat learner side as overlay/evidence graph
3. Use ontology alignment to map vault concepts/claims onto canonical IDs
4. Infer mastery from stronger signals than note presence
5. Run path planning on curriculum graph with learner overlay as weights

**Verdict:** "Yes in pieces, no as a standard product." Using a local
PKM as the learner graph that docks into an external teaching graph is
the live research edge. This is where a contribution could be made.

### Temporal Knowledge Graphs (Query 04)

"John's 'temporal awareness is the Holy Grail' line is half right."

**What exists, by system:**
- **Graphiti/Zep:** Real temporal fact graph (4 timestamps on edges,
  invalidation on contradiction). But node attributes destructively
  upserted, not versioned. OSS still sharp-edged.
- **TigerGraph:** Strong for large event streams. You model time yourself.
  Not a ready-made learner system.
- **Neo4j:** Temporal value types, versioning guidance. You own the
  semantics. No automatic invalidation. Old GraphAware TimeTree retired.
- **TerminusDB:** Git-for-data (revision history, time travel, branches,
  diffs). Database-history time, not semantic-validity time.

**Personal learning side: very thin.** Math Academy is the strongest
live graph-shaped learner model (curriculum KG + spaced repetition), but
platform-owned. Recall/RemNote are partial. Research prototypes exist
(UMAP 2024, LAK 2025) but not deployed products.

**The key reframe:** The valuable temporal object is not timestamped
facts. It is **tracked change in commitments, salience, confidence, and
neglected regions, with provenance.** This is a modeling question, not
an infrastructure question. The SNT project already partially answers it:
claims with proven/provisional/open status, session logs as provenance,
evidence chains tracking what changed and why.

**Recommended sequence:**
1. Build append-only provenance and memory surfaces (the repo already
   has dated journals, `_memory/` traces, session logs, AGENT_PROTOCOL
   provenance requirements)
2. Model fast-vs-slow state change (Conducted Daydreaming already
   separates slow ripeness from fast activation, with decay and commits)
3. Only then decide whether a temporal KG is warranted

**Bottom line:** Temporal KGs exist; temporal personal learner models
barely do. "They buy you history. They do not buy you pedagogy."

### Local Synthesis — Calibration Layer

The combined local synthesis (`queries_01-04.md`) ran the same four
queries against the repo's own corpus to separate what was already
implied from what the external research added. Four hard conclusions:

1. **Graphiti is not validated here.** Strongest claims are proposal-
   language from `notes_team.md`. Strongest counter-evidence is the
   working flat-file architecture + sync-gap research.
2. **Practical PKM docking is still mostly open.** Strongest implemented
   pattern is markdown + inference-time semantic matching + provenance
   logs. The SNT MCP server is ahead of the field.
3. **Personalized-learning docking survives only after reformulation.**
   Not concept-graph shortest path, but alignment over questions/claims/
   evidence/uncertainty/salience.
4. **Temporal KG is not answered locally.** Sharper insight is "version
   control for commitments."

**"John's ideas are still live, but in this repo they are hypotheses
under pressure, not findings."** The strongest current architecture in
the local material is flat markdown, semantic traversal at inference
time, strict provenance, and delayed commitment to persistent graph
infrastructure until scale or task structure forces it.

### What This Changes

Three things crystallize from the replies:

**1. The infrastructure bet shifts.** Graphiti is off the table for now.
The SNT compiler pattern (Markdown → SQLite with typed edges + MCP
server) becomes the reference architecture for docking. It's lighter
than Graphiti, already working, and the external research validates it
as the right approach at current scale.

**2. The docking question is largely answered.** File-first + scoped
memory + MCP is the practical state of the art. The open question isn't
"how to dock" but "what structure to expose through the dock" — which
is the claims-vs-concepts question.

**3. John's most original contribution survives but needs reformulation.**
Graph intersection for personalized learning is the live research edge.
The conversation with John should center on: the problems are real, the
instinct about graph structure is right, but the atom should be claims/
questions/evidence not concepts, the path planning should be mastery-
aware not shortest-path, and the learner graph should be an overlay not
a parallel authority.

---

## Open Design Questions

1. **Should the vault's atoms become claims?**
   The field converges on claims (QUE-CLM-EVD) as the fundamental atom.
   The SNT project already implements this with proven/provisional/open
   status and evidence chains. If the Symbiotic Vault adopted claims-as-atoms,
   what's the migration path from concept-notes? What do you gain
   (argumentation, typed relations, falsifiability) vs. what do you lose
   (simplicity, low-friction capture, the current atom's openness)?

2. **Can the SNT compiler pattern generalize?**
   Markdown → SQLite with typed edges works for legal research. Could the
   same pattern apply to an Obsidian vault with atoms and frames? The
   vault's `atomize` skill already extracts structure — a compiler that
   builds a queryable index from that structure would give the vault an
   MCP-dockable graph without requiring Graphiti or Neo4j. What changes
   in the schema when the domain shifts from legal to creative?

3. **What does "intentional ambiguity" look like in a claim-based vault?**
   Legal claims resolve to proven/provisional/open. Creative writing
   deliberately maintains productive contradictions. A fourth status —
   "deliberately unresolved" — might be needed, but that changes the
   epistemology: the graph isn't converging toward truth, it's holding
   tension. How does that interact with typed edges like `supports` and
   `opposes`? Can a claim simultaneously support AND oppose another
   claim without the graph treating it as incoherent?

4. **Where does inference-time matching break down?**
   The current approach (LLM reads flat files, no persistent graph) works
   at SNT scale. At what vault size or query complexity does it fail?
   This determines whether a compiled graph is a nice-to-have or a
   necessity. The Graphiti reply (Query 02) says the crossover is when
   entities recur across many episodes + facts change over time + you
   need multi-hop entity-centric queries where chronology matters. The
   vault doesn't meet these conditions yet. Rough guess from the reply:
   100 nodes no, 10K maybe, 100K only if core infrastructure. But Graphiti
   itself doesn't publish the benchmark — these are indirect signals.

5. **What's the right feedback surface between graph and prose?**
   The SNT source cards show curated feedback — compiler output informing
   source documentation. The Symbiotic Vault's Membrane aspires to a
   richer surface. The Dreamer↔Director loop is lossy by design. What's
   the right granularity for creative work — per-session, per-claim,
   per-atom?

6. **How should the learner overlay graph work?**
   Query 03 establishes that the learner graph should be an overlay on
   an authoritative curriculum graph, not a parallel authority. But what
   does "overlay" mean concretely for a vault? Which signals count as
   evidence of mastery beyond note presence? How does the vault's claim
   status (proven/provisional/open) map to learning states? Can the SNT
   evidence-chain pattern adapt to track learning rather than legal proof?

7. **Literary vs. infrastructure architectures (unanswered).**
   Thread 6 from the original asks was not returned. Is this a writing
   tool that happens to have a graph, or a graph tool that happens to
   produce writing? The answer shapes everything: UI priorities, what
   gets compiled, what stays prose, where the Membrane sits. Still needs
   external research or a decision memo.
