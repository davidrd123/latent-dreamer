# Prompt 26: Typed concern entry, epistemic retrieval indices, and live material selection

## Context

We have a Clojure cognitive kernel built from Mueller's 1990 DAYDREAMER
architecture. It has working concern competition (one attention economy,
emotion-driven selection), a memory membrane (admission tiers, anti-residue,
evidence-driven promotion), coincidence-mark retrieval over named indices,
branchable contexts, typed internal moves (families: rationalization,
reversal, rehearsal, roving), and a feedback loop where generated thought
changes future retrieval.

The kernel currently uses LLMs only at bounded edges: prose realization
from cycle state (runtime_thought.clj), small scene-level nudges
(director.clj), and post-hoc evaluation of family plans
(family_evaluator.clj). LLMs do not yet build the world model, drive
concern initiation, own retrieval strategy, or execute LLM-backed rules.

Mueller's original system spent ~46% of its code on representations and
hand-coded planning rules (135 rules, 3722 lines). That was the
common-sense layer. Our kernel has the cognitive architecture (~28% of
Mueller) but fills the common-sense gap with hand-authored benchmark
logic (the Graffito miniworld). The miniworld proves the kernel
mechanisms work but its content is entirely hand-coded scenery.

We are pivoting toward a persistent collaborative companion. The
working thesis is:

- **product**: issue-centered studio partner
- **internal design**: polyphonic daemon
- **mode**: conducted inner-life instrument (demoted from primary)

The kernel's concern competition and membrane are load-bearing and
transfer. The top-level object model (currently episodes only) and
concern entry points (currently failed goals + negative emotion only)
need to expand.

The first pivot experiment we are considering: keep the kernel's control
loop and memory exactly as they are, but let an LLM propose what enters
the concern economy. The kernel validates, competes, retrieves, and
membranes. The LLM supplies the content that currently comes from
hand-authored templates.

This is no longer a speculative "someday" seam. The current Graffito
assay has reached a readable plateau:

- the assay is honest enough to trust (projected affect is now derived
  and provenance-tagged)
- the reusable seams we expected have mostly landed
  (kernel rehearsal trigger/activation, generic post-effect hook,
  canonical episode lifecycle, lifecycle summaries, 20-cycle and
  100-cycle exports)
- the 100-cycle attractor stabilizes after cycle 17
- the late tail is dominated by one durable cross-family bridge
  (`:ep-12`)
- there is no late vindication, rehabilitation, demotion, or additional
  rule-access movement in that tail
- that 100-cycle attractor read is now a standard export artifact, not
  just a one-off probe

So the immediate branch question is not "should we add another
benchmark-local Graffito bridge?" We think the answer there is no. The
real question is whether the stabilized attractor is the right trigger
for the first bounded collaborator-side proposal experiment.

Key prior art we are already tracking: Joel Chan's discourse graphs
(questions/claims/evidence/sources), Synergi (mixed-initiative
synthesis), Graphologue (LLM responses as editable graphs), Clark &
Brennan (grounding/common ground), argumentative human-AI
decision-making, George Lewis / Voyager (nonhierarchical partnership
with independent internal process).

## Four questions

Before answering the three questions below, answer this explicitly:

Do you agree that the current state above is enough to justify moving
now to one bounded collaborator-side proposal seam? Or do you think one
more kernel-side use/outcome generalization pass is still the better
next step? Be concrete about the decision rule, not just the mechanism.

### 1. Designing the typed concern entry point

We want an LLM to read a chunk of real working material and propose
typed concern objects that a kernel can consume. Mueller had ~12 theme
rules for concern initiation. We want to replace those with one or a
small number of prompts.

Assume we are deliberately keeping this first experiment narrow:

- one typed proposal seam, not a broad collaborator rewrite
- strict kernel-side validation and normalization
- kernel remains the committer
- no prompt-per-rule replacement
- no full planning decomposition yet

Also answer this comparison directly: among

- typed issue / concern initiation
- contradiction / contestation proposal
- retrieval reformulation

which is the best first bounded proposal seam, and why?

What should the output schema look like? What fields does a concern
object need to carry so the kernel can run competition, retrieval, and
membrane on it? How do you distinguish genuine unresolved concerns from
an LLM's tendency to manufacture problems or overgenerate? What does
validation mean concretely at the kernel boundary — structural checks,
plausibility gates, deduplication against existing concerns, something
else? What prompt design patterns produce reliable typed output for
this kind of task?

Think about this from both directions: what the kernel needs to consume
(typed, indexed, scorable) and what the LLM can reliably produce (it
is good at noticing tension, contradiction, unfinished threads, and
aesthetic misfit; it is bad at consistent scoring and at knowing when
to stop).

Also answer these boundary questions explicitly:

- Should the first typed proposal enter the kernel as a real concern
  immediately, or as a provisional issue object that only creates
  concern pressure after validation?
- What is the smallest adapter from a typed proposal into the current
  kernel's existing machinery? For example: one proposal becomes a
  concern id, a small retrieval-index bundle, a plausibility score, and
  a provenance record. Is that too thin, or exactly right for v1?
- Should urgency / ripeness / interruptibility be part of the first
  concern schema, or should return-ticket timing stay out of scope for
  the first experiment?
- Should human uptake be part of the first validation/persistence story
  from day one, or is that a second-step extension after the initial
  typed concern path works?

### 2. Retrieval indices for epistemic objects

The kernel's retrieval works by coincidence-mark counting over explicit
named indices. Episodes currently carry retrieval indices like
`can_corresponds_to_sensory_capacity` or `light_jolt_floods_attention`.
Multiple independent index hits must converge for retrieval to fire.
This is a real differentiator over embedding-based retrieval for
surfacing surprising-but-grounded returns.

If the top-level objects expand from episodes to claims, questions,
hypotheses, contradictions, aesthetic bets, and commitments — what are
the indices? How do you do multi-signal convergence retrieval over
epistemic objects? What would discourse graph research suggest about
the right retrieval keys for question/claim/evidence structures? How
do you index a contradiction vs. a hypothesis vs. a commitment in a
way that lets coincidence-mark retrieval surface "these three
unrelated things are actually about the same tension"?

The constraint is: the indices need to be discrete named tokens, not
embeddings. The kernel's retrieval is combinatorial (count overlapping
index hits), not geometric (nearest neighbor). What naming conventions
or index taxonomies would make this work for epistemic objects the way
Mueller's situation-level indices worked for scene episodes?

Also answer the mixed-object question directly:

- if the first experiment introduces issue / concern objects alongside
  episodes rather than replacing them, how should retrieval work across
  both?
- should issue objects retrieve episodes, should episodes retrieve issue
  objects, or should both race through a shared bridge-index layer?
- what are the best candidate bridge indices for linking an unresolved
  issue to older episodes without collapsing the two object types into
  one?

### 3. What live material feeds the first experiment

The Graffito miniworld has hand-authored material. The collaborative
case needs real material. What is the smallest viable "live material"
source for a first typed-concern experiment?

Options we can see:

- an actual conversation transcript (e.g. a session like this one)
- Obsidian vault journal entries (we have a collaborator's vault with
  atoms, reflections, frames, and a daily journal)
- project notes and research sifts from this repo
- a code review or architecture discussion
- a research reading session with annotations

Each has different properties: density of unresolved concerns, natural
contradiction, richness of epistemic objects, how much context the LLM
needs to read before it can propose typed concerns.

What does the research on mixed-initiative sensemaking suggest about
what input material works best for surfacing genuine intellectual
tension? Which of these sources is most likely to produce the kind of
unresolved concerns, contradictions, and hypotheses that would exercise
concern competition and the membrane in a non-trivial way? What are
the failure modes — material too thin (LLM manufactures concerns),
material too dense (LLM drowns and produces generic summaries),
material too personal (concerns are relational, not epistemic)?

For each source type, what is the expected concern density, and what
is the risk profile?

Also rank the source types for this narrower decision:

- best source for the very first bounded typed-concern experiment
- best source for stress-testing contradiction detection
- best source for avoiding overfitting to our existing architecture
  language
- worst source to start from, and why

One concrete pressure test:

If the source is a conversation from this repo, there is a risk that
the LLM simply mirrors our own current framing and vocabulary.
If the source is a collaborator vault or journal, there is a risk that
the output becomes too relational or too diffuse to exercise epistemic
retrieval well.

How would you choose between those two failure modes for v1?

### 4. Smallest experiment that actually teaches us something

Assume we want the first collaborator-side proposal experiment to be
small enough to run now, but strong enough to falsify a bad direction.

What is the smallest end-to-end experiment you would actually run?

Please specify:

- exact source material type
- approximate input size / context window shape
- exact typed output packet
- exact observability / verifier packet:
  what counters, traces, and example records should exist so we can
  read `proposed -> validated -> admitted -> retrieved -> used ->
  outcome -> persisted/dropped` without reconstructing it by hand
- what kernel-side validation should do
- what retrieval should be allowed to do
- what persistence should and should not be allowed to do
- what success would look like after 1 session
- what success would look like after 3-5 sessions
- what failure would tell us the seam is wrong rather than merely
  under-tuned

## What I am looking for

Not a tutorial. Not a literature review. Concrete design answers that
I could start implementing. Schema sketches with field names. Prompt
fragments. Index taxonomy proposals. Specific failure modes with
mitigations. Rank the source types for the first experiment with
reasons.

Be blunt about what you think will not work. If coincidence-mark
retrieval over epistemic objects is a bad idea and embeddings are
actually better for this case, say so and say why. If the typed
concern entry point is going to fail because LLMs cannot reliably
produce the output format, say so and suggest what to do instead.

Two additional pressure tests:

**The membrane transfer question.** The membrane was designed for
family-plan episodes: provisional until cross-family use proves
them. For epistemic objects (claims, questions, contradictions),
what does "cross-family use" even mean? If a claim enters as
provisional, what later event should promote it? Human uptake?
Cross-concern retrieval? Survival without contradiction? The
membrane is one of the strongest things in the kernel, but its
evidence model may need different semantics for epistemic objects
vs cognitive episodes. Is the same promotion chain usable, or does
it need a parallel evidence channel?

**The overgeneration trap.** LLMs are good at noticing tension and
generating plausible-sounding concerns. The risk is not that the
LLM fails to produce output — it's that it produces TOO MUCH.
Ten concerns per conversation chunk, all plausible, most shallow.
The kernel's attention economy can only handle a few competing
concerns meaningfully. What is the right throttle? Hard limit on
proposals per chunk? Quality gate before entry? Or let the kernel's
own competition handle it — flood the economy and let the strongest
survive? What does the mixed-initiative sensemaking literature say
about the right proposal rate for human-AI collaboration?

**The mirroring / jargon trap.** If the source is from this repo or a
conversation like this one, the model may simply mirror our own
architecture vocabulary (`concern`, `membrane`, `retrieval
reformulation`, `rule access`) instead of surfacing the underlying
issue in the material. What is the best defense against that for v1?
Source-grounding spans? A ban on kernel jargon in proposal text?
Separate fields for `source_phrasing` vs `normalized_kernel_label`?
A verifier check that the concern is recoverable from the source
without architecture language?
