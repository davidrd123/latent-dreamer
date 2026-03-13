# Established Findings

What we know about bidirectional knowledge-text coupling — from the
deep research, the generative conversation that produced it, the
cross-model critique, the vault agent's response, six deep research
threads pressure-testing John's track, and a GPT 5 Pro architecture
review. This document carries the reasoning, not just the conclusions.

Last updated: 2026-03-12 (post-5-Pro review)

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
- `../DeepResearch/2026-03-12/john/replies/query05/query05_integrated_answer.md` — learner modeling without explicit assessment
- `../DeepResearch/2026-03-12/john/replies/query06.md` — strategic architecture: file-first vs compiled vs graph-first
- `../DeepResearch/2026-03-12/john/replies/project_file_org.md` — GPT 5 Pro architecture review of the briefing

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

Initial deep research run: four web-facing queries (2026-03-12),
targeting John's Graphiti/docking/learning thesis. Raw replies in
`../DeepResearch/2026-03-12/john/replies/`. A separate local-only
synthesis (`queries_01-04.md`) ran the same prompt set against the
repo's own corpus as calibration — separating what was already implied
locally from what the external research added.

The initial batch returned queries 01-04. Two follow-up threads
(`query05` and `query06`) came back after the first integration pass
and are covered below, so the full six-thread agenda is now represented.

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
- bitbonsai/mcp-obsidian (~700 stars, active Mar 2026; marketed as
  "mcpvault") — best alive general bridge, but no published releases
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
Graphiti is usually too much." Graphiti is not the next move — not
permanently ruled out, but not justified until flat files fail in
concrete, repeatable ways. The SNT compiler pattern (Markdown → SQLite
with typed edges) is the lighter middle ground the comparison set
points toward.

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

### Learner Modeling Without Explicit Assessment (Query 05)

Can vault artifacts (journals, drafts, atoms) serve as a passive mastery
signal, without formal quizzes or self-ratings?

**Where artifact-based modeling works:** When artifacts are structured
enough or the environment is instrumented enough. Revised explanations,
concept maps, instrumented process traces (keystrokes, time on task,
revision patterns) all produce usable learning evidence. The strongest
cases all start from explicit diagnostic data or explicit learner
signals, not silent inference from note topology.

**Where it doesn't:** Passive PKM-as-mastery-graph is still under-
validated. No deployed system infers evolving understanding by watching
KG topology changes alone. The ones that get closest use quiz
performance, explicit learner signals, and diagnostics.

**Useful event types the research suggests tracking:**
- `asserted claim` — writer makes a new position
- `revised claim` — writer changes an existing position
- `added support` — new evidence for an existing claim
- `noted contradiction` — writer flags a tension
- `linked concepts` — writer makes an explicit conceptual bridge
- `returned after delay` — spaced repetition signal
- `successfully recalled` — active retrieval evidence
- `failed recall` — gap signal
- `abandoned thread` — line of thought goes dormant or gets dropped
- `explicitly marked confusion/confidence/uncertainty` — self-rating

**Key reframe:** The vault should be treated as an **evidence stream**,
not a self-certifying mastery graph. Note presence is not mastery
evidence. Low-friction explicit signals (self-ratings, confidence marks,
"I don't understand this yet" flags) matter more than hoping silent
inference from note topology is good enough.

### Strategic Architecture — File-First vs. Graph-First (Query 06)

The strongest thread. This is the one that answers the breakpoint
question directly.

**The core finding:** Do not jump from file-first to full graph-first.
The evidence supports a narrower move: stay file-first now, and when
you upgrade, upgrade first to a one-way compiled layer where Markdown
remains the source of truth and the compiled structure is rebuildable,
disposable, and query-oriented.

**What graph structure actually buys (from controlled comparisons):**
- Plain RAG does better on single-hop and detail-heavy questions
- Community-GraphRAG (Local) does better on multi-hop/reasoning questions
- Graph construction is lossy: ~65% entity coverage in tested benchmarks
- For summarization, integration is often only comparable to RAG
- GraphRAG's strongest win is global sensemaking over large corpora
  (~1M tokens), not general-purpose knowledge work

**Long context is not a silver bullet either:**
- Lost in the Middle: models degrade with relevant info buried in middle
- RULER: only half of tested models maintained performance at 32K tokens
- OP-RAG: answer quality follows an inverted-U with more retrieved chunks
- No clean "huge context window makes flat files enough forever" result

**The ecosystem is drifting toward hybrids:**
- Microsoft's GraphRAG local search combines graph structure with raw
  text chunks at query time
- LazyGraphRAG: 0.1% of full GraphRAG indexing cost, comparable quality,
  defers expensive structure to query time
- LlamaIndex PropertyGraphIndex: per-chunk entity/relation extraction,
  persistable, source text included at query
- CAG: for constrained corpora, preload and cache instead of retrieving
- Even Microsoft's own docs now read as internal critique of full eager
  graphing — static global search called "expensive and inefficient"

**The breakpoint rule:**

> Flat-file inference-time assembly stops being enough when the cost of
> repeatedly re-deriving structure exceeds the cost of maintaining a
> compiled artifact. Not before.

The breakpoint is NOT raw vault size. It is query type + reuse
frequency + whether typed operations pay rent in live work.

**Stay file-first (Architecture A) when:**
- Most questions are local, detail-heavy, frame-based, or exploratory
- Main pain is surfacing/cohabitation, not recall
- Ontology is still unstable
- You want Drift/Membrane/Compost/Table behaviors that rely on soft
  semantic movement and productive ambiguity

**Move to compiled layer (Architecture A+) when:**
- Typed questions recur that are annoying to recover from raw files
  ("what supports this?" "what contradicts?" "what changed?" "trace the
  dependency path")
- Same working set reused across ~10+ interaction turns (adjacent
  evidence: at ~100k tokens, persistent memory becomes cheaper after
  ~10 turns)
- Substrate is boring enough to compile (claims/questions/evidence, not
  vibes)

**Move to full graph-first only when:**
- Multi-hop/global reasoning is a core workload, not occasional
- The graph itself becomes a user-facing surface
- You're willing to own extraction quality, rebuilds, staleness,
  pruning, entity resolution, and cost

**Creative/exploratory evidence is thin.** No benchmark covers
Drift/Membrane/Table-style creative work on live PKM vaults. The best
targeted benchmark (GraphRAG-Bench) reports GraphRAG advantages on
harder reasoning/summarization but plain RAG wins fact retrieval, and
on novel datasets RAG is stronger on small-corpus creative generation.

**Still missing:** No controlled benchmark compares file-first / compiled
index / full graph on the same live PKM vault with creative tasks and
real editing churn.

### What This Changes (Queries 01-06)

Four things crystallize from the full set of replies:

**1. The infrastructure bet shifts.** Graphiti is not the next move.
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

**4. The breakpoint is now concrete.** Query 06 provides a falsifiable
rule: the trigger for moving from A to A+ is repeated typed-query
failure + repeated reuse, not "graphs seem smarter." The trigger for
moving from A+ to B is persistent agent-memory failure after A+ exists.
The whole ecosystem is drifting toward hybrids that defer structure to
query time.

---

## GPT 5 Pro Architecture Review (2026-03-12)

The briefing-for-review.md was sent to GPT 5 Pro for architecture sanity
check, with all six query replies, the research package, memo, and
established findings as supporting material. The review confirmed the
broad direction and sharpened the model in five ways.

### 1. Missing Layer: Salience / Surfacing / Collaboration

The four-layer decomposition (authoring, docking, agent memory, learner
overlay) is incomplete. The review identifies a missing layer — or
cross-cutting concern — for **salience, surfacing, and collaboration
surface.**

This is not a new finding in this document (see "The Salience
Requirement" section above, from the GPT 5.4 critique). But it got lost
in the architecture decomposition. The briefing proposed four backend
layers without making salience explicit. The review says: the
architecture is not complete until this is explicit. This is the layer
that makes the graph pay rent.

The vault's own docs confirm this: DESIGN_CRITIQUE.md names cohabitation
as the big failure; THE_MEMBRANE.md defines the missing surface;
convergence-map.md ties the core problem to feedback and salience.

**Updated decomposition:**
1. Human authoring substrate (vault) — file-first, permissive
2. Docking / query substrate (compiled layer + MCP) — A+ lives here
3. Salience / surfacing / collaboration surface — **the real unsolved
   problem**; what decides what the writing surface should see right now
4. Agent memory / planning substrate — separate system if needed later
5. Learner model / curriculum overlay — future branch

### 2. Layer 3 Was Conflated

The original "Agent memory / planning / execution substrate" bundled
three different things in one box. The review says: agent memory
(tracking recurring entities, changing facts) is different from agent
execution/planning (skills, task state, traces, protocols).

Split them conceptually even if you don't build two systems yet. The
briefing itself admitted execution was bundled "for now"; the review
says make the split explicit now. Nothing in the external evidence shows
that Graphiti/Cognee-class memory automatically solves planning or
execution.

### 3. The Ladder Is Not Linear

The briefing presented A → A+ → B → C as a straight progression. The
review corrects this: **C does not depend on B.**

The actual architecture graph is:
- A → A+ (the one justified move)
- Then branch: B if agent memory becomes a real failure
- Or branch: C if learner overlay becomes a real product need
- B and C are independent paths, not sequential steps

This matters because it means learner overlay work (mapping vault
concepts onto curriculum graphs, logging learning events) can proceed
without first building a Graphiti/Cognee agent-memory layer.

### 4. Product Stance: Writing Tool With a Graph

The review answers open question #7 directly: **this should be a writing
tool with a graph, not a graph tool that produces writing.**

Evidence: the vault's own design docs (DESIGN_CRITIQUE.md, THE_MEMBRANE,
THE_DRIFT, THE_SEANCE, THE_TABLE) all point toward a writing-centered
system whose value appears during work, not in a separate graph console.
The discourse-graph literature, ARTIST, Tana, and Query 06 all support
prose-first or outline-first interaction.

Once that stance is settled, the next design question sharpens: **what
salience mechanism decides what the writing surface should see right
now?**

### 5. Start Boring, Not Poetic

The briefing proposed creative-domain status types (`committed/exploring/
tension/dormant/composted`) and edge types (`resonatesWith/tensionWith/
sourceOf/developedFrom/complicates`). The review says: **too invented to
be the first compiler schema.**

The external evidence supports starting with boring primary atoms and
keeping richer interpretation as overlays. The discourse-graph research
is strong for argumentative writing, weaker for free-form ideation.
"Resonance" as a primary core type becomes a vibes machine.

**The concrete A+ build spec (from the review):**
1. Add `claim` as an atom kind, but do not migrate all atoms
2. Define a compilable subset only: explicit Questions, Claims,
   Evidence, Sources, plus provenance spans and revision events
3. Compile that subset one-way into SQLite
4. Keep the edge grammar tiny: `supports`, `opposes`, `informs`,
   `sourceOf`, `revises`
5. Expose only a few MCP operations: scoped search, trace support,
   trace conflict, what changed, what shifts if X changes
6. Do not ship a graph editor — surface results in a prose-adjacent
   sidebar/margin/backlink surface
7. Instrument everything: correction rate, acceptance/dismissal rate,
   time-to-answer, whether a surfaced item changed the draft, and
   whether users voluntarily reopen the compiled view

### 6. Stop Using Graphiti as the Straw Antagonist

The review notes the corpus is slightly overfit to "not Graphiti." The
negative conclusion is mostly right, but it's no longer the real design
center. The briefing, memo, and research package all spend substantial
energy rebutting the Graphiti pitch when the real choice is product
stance and minimal compiled structure.

Also: B-layer alternatives (Cognee, GraphMD, skills.md→KG, Jim White's
text2kg direction) are only named, not evaluated. If B matters later,
the next research pass should compare those directly rather than
continuing to orbit Graphiti.

### 7. Breakpoint Criteria (Concrete)

**A → A+ (move when 3 of 5 are true over 2-4 weeks):**
1. Support/contradiction/change/path questions recur weekly
2. Same note subset reread across ~10+ interactions
3. Answers to typed questions are slow/unstable because structure is
   re-derived each time
4. Minimal schema has stopped changing every few days
5. Users asking impact questions ("what shifts if...?") often enough
   to justify compiled traces

**A+ → B (move only when all hold after A+ exists):**
- Weekly re-resolution of same people/projects/facts across sessions
- Repeated need for temporal invalidation or change history not authored
  in the vault
- Visible cost from re-grounding the same agent context
- If the failures are still about authored support/contradiction, stay
  at A+

**A+ → C (move only when all four hold; B is not required):**
1. A stable external curriculum/domain graph exists
2. The vault can map some claim/concept subset onto it
3. Explicit learner events are being logged
4. There is an actual decision use case ("what should I learn next?")
   not just "it would be neat to model mastery"

### 8. Research Gaps Identified by the Review

- No same-corpus benchmark for A vs A+ vs B on live creative-vault work
- B-layer alternatives under-researched (Cognee, GraphMD, text2kg)
- Compiler schema for creative work under-evidenced — start boring
- Salience layer conceptually present, mechanically absent — "what
  should surface, when, and why?" is the design question, not "more
  structure?"
- SNT transfer claim needs direct testing on creative material
- Learner overlay remains a future research track, not a build track

---

## Current Working Position (Post-Review)

As of 2026-03-12, after six deep research threads and one external
architecture review, the working position is:

1. **Keep the vault file-first.** Markdown stays source of truth. The
   authoring substrate must preserve productive contradiction, low-
   friction capture, and the writer's voice.

2. **Build a narrow A+ compiled layer over a boring subset only.** Do
   not compile the whole vault. Target explicit Questions, Claims,
   Evidence, Sources with provenance. Tiny edge grammar. Few MCP
   operations. Instrument everything.

3. **Make salience/surfacing the real product problem.** The missing
   thing is not backend choice — it's what decides what the writing
   surface should see right now. The Membrane, the Drift, and the
   Séance are all experiments in this space. The compiled layer's value
   is measured by whether it makes surfacing better.

4. **This is a writing tool with a graph.** Not a graph tool that
   produces writing. Prose-first UI. No graph editor. Results surface
   in margins, backlinks, annotations — not a separate console.

5. **Treat agent memory and learner overlay as later branches, not the
   next step.** B (agent memory) is justified only when cross-session
   entity/state memory fails repeatedly after A+ exists. C (learner
   overlay) is justified only when an external curriculum graph exists
   and explicit learning events are being logged. They are independent
   branches, not sequential. Planning/execution traces stay lightweight
   until they prove they need their own substrate.

6. **Stop arguing about Graphiti.** The negative conclusion is right
   and no longer interesting. The design center is product stance and
   minimal compiled structure.

---

## Open Design Questions

1. **Should the vault's atoms become claims?**
   The field converges on claims (QUE-CLM-EVD) as the fundamental atom.
   The SNT project already implements this with proven/provisional/open
   status and evidence chains. If the Symbiotic Vault adopted claims-as-atoms,
   what's the migration path from concept-notes? What do you gain
   (argumentation, typed relations, falsifiability) vs. what do you lose
   (simplicity, low-friction capture, the current atom's openness)?
   **Update:** The 5 Pro review says add `claim` alongside concepts, do
   not migrate all atoms. The compiler targets the claim-bearing subset
   only.

2. **Can the SNT compiler pattern generalize?**
   Markdown → SQLite with typed edges works for legal research. Could the
   same pattern apply to an Obsidian vault with atoms and frames?
   **Update:** The review says the plumbing transfers (one-way compiler +
   MCP is domain-agnostic). The epistemology doesn't (creative work needs
   different status types and edge types). Start with a boring subset and
   test on 50 vault atoms before committing to a schema.

3. **What does "intentional ambiguity" look like in a claim-based vault?**
   Legal claims resolve to proven/provisional/open. Creative writing
   deliberately maintains productive contradictions. A fourth status —
   "deliberately unresolved" — might be needed, but that changes the
   epistemology. Still open. Not addressed by the review.

4. **Where does inference-time matching break down?**
   **Sharpened by Query 06 and the review.** The breakpoint is not vault
   size — it's query type + reuse frequency + rent-paying. Concrete
   criteria: 3 of 5 signals over 2-4 weeks (see breakpoint criteria
   in the 5 Pro review section above). The vault probably doesn't meet
   these conditions yet.

5. **What's the right feedback surface between graph and prose?**
   The SNT source cards show curated feedback. The Membrane aspires to
   contextual surfacing. Still open. **Sharpened:** The real question is
   now "what salience mechanism decides what the writing surface should
   see right now?" — not just granularity of feedback.

6. **How should the learner overlay graph work?**
   Query 03 establishes overlay framing. Query 05 says passive PKM-as-
   mastery-graph is under-validated — vault should be evidence stream,
   not self-certifying mastery graph. **Update:** C does not depend on B.
   Near-term learner work is event logging only: track `asserted claim`,
   `revised claim`, `added support`, `noted contradiction`, `linked
   concepts`, `returned after delay`, `successfully recalled`, `failed
   recall`, `abandoned thread`, confidence/confusion/uncertainty marks.

7. **Literary vs. infrastructure architectures (answered).**
   ~~Thread 6 from the original asks was not returned.~~ Thread 6 came
   back and the 5 Pro review answered the question: **this is a writing
   tool with a graph.** Not a graph tool that produces writing. Prose-
   first UI. No graph editor. The next question: what salience mechanism
   decides what the writing surface should see right now?

8. **What should the salience mechanism actually be?**
   (New, from 5 Pro review.) The vault's design docs identify salience
   as the actual unsolved problem. The Membrane, the Drift, and the
   convergence-map all point at it. The 5 Pro review makes it explicit
   as the missing architectural layer. No one in this thread has proposed
   a concrete salience algorithm. Research should shift from "more
   structure?" to "what should surface, when, and why?"

9. **Is the proposed creative-domain schema grounded or speculative?**
   (Carried from briefing.) The proposed status types (`committed/
   exploring/tension/dormant/composted`) and edge types (`resonatesWith/
   tensionWith/sourceOf/developedFrom/complicates`) were critiqued by the
   review as too invented for v1. Start boring. Validate with 3-5
   creative vault users or prototype on a small subset before adopting.
