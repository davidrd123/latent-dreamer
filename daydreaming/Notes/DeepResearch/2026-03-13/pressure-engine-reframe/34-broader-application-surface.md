# Broader Application Surface

Date: 2026-03-14

Purpose: capture the insight that the mechanisms being built for
conducted daydreaming are general-purpose cognitive infrastructure,
not performance-specific tooling. This note records a late-session
discussion that should not be lost to compaction.

Status: vision note, not build-order note.

Danger: useful for long-horizon product framing, but dangerous if
treated as a near-term implementation brief. The psychology/application
surface below should not reopen the current execution queue or harden
into kernel ontology without a separate falsifiable prototype.

---

## The observation

The settled architecture frames the system as a conducted
performance instrument. That's what's being built. But the
mechanisms — concern-driven cognition, episodic memory with
accumulation, typed cognitive operators, appraisal-shaped
generation, serendipity through cross-concern contamination —
are not performance-specific. They are general-purpose cognitive
infrastructure for any agent that needs inner life.

The current persistent-agent paradigm (mem0, Graphiti, Claude's
memory, etc.) is memory without cognition. Those systems store
facts, retrieve by similarity, and maybe track temporal changes.
But they do not think about what they remember. They do not have
concerns that compete for attention. They do not apply operators
that produce different kinds of processing. They do not accumulate
episodic history that changes how they process new material.

The difference: a memory system answers questions about what it
knows. A cognitive system generates questions about what it does
not understand.

---

## Concrete applications beyond conducted performance

### Writing companion with real preoccupations

Not "here's a summary of your notes." A system that has concerns
about your project — unresolved tensions it noticed, questions it
keeps returning to, connections it discovered between things
written months apart. When you sit down to write, it is not
starting from scratch. It has been ruminating. Its episodic memory
has accumulated associations from every session. The serendipity
mechanism has found connections you did not ask for. It surfaces
those not as search results but as thoughts it had.

What the mechanisms provide:
- concerns = intellectual preoccupations that persist across sessions
- operators = different kinds of thinking (rehearsal of argument,
  reversal of earlier position, rationalization of a contradiction,
  roving to unexpected territory)
- episodic memory = accumulation that makes the second session
  different from the first
- serendipity = non-obvious connections validated through
  structural overlap, not just embedding similarity

### Reading companion with evolving interpretation

Feed it a novel. It does not just summarize — it develops concerns.
"I am worried about what happens to this character." "This theme
keeps appearing and I do not know what the author is doing with
it." "This chapter contradicted something from chapter 3."

The operators produce different kinds of reading:
- rehearsal = anticipating what happens next
- reversal = rethinking earlier interpretations
- rationalization = explaining apparent inconsistencies
- roving = connecting to other works

Each re-reading activates different episodic material. The agent's
interpretation changes over time because its concern landscape
changes. That is what reading actually feels like.

### Research assistant with intellectual preoccupations

Not "search for papers about X." An agent that has ongoing
research concerns — questions it tracks across sessions,
hypotheses it tests against new material, connections it builds
between sources.

When given a new paper, it reads through the lens of accumulated
concerns. "This contradicts what we thought about EMA." "This
supports the abduction hypothesis from a different direction."

The overdetermination mechanism means it prioritizes findings that
serve multiple research questions simultaneously. That is what
good research feels like — the finding that illuminates several
open problems at once.

### John's vault agent, but with inner life

The Membrane design surfaces agent observations into human working
space. Currently those observations are flat — "here is a
connection I noticed." With Mueller's architecture, the agent's
observations would have texture. "I noticed this connection and I
have been thinking about it in relation to three other things, and
I think there is a deeper pattern."

The freeze/dismiss/respond/cut interactions shape the agent's
concern priorities. Frozen observations become high-salience
concerns. Dismissed ones decay. The pattern of human curation
becomes the agent's training signal for what matters.

---

## Why this has not been built

### Communities do not talk to each other

Mueller is cognitive science / AI. EMA and OCC are affective
computing. Versu is interactive fiction / game AI. Facade and DODM
are interactive drama. Generative video is ML/diffusion. Music
generation is audio ML. Conducting / IMS is new media / music
technology. No single research community spans all of these.

### Mueller got orphaned

DAYDREAMER was a 1990 dissertation. Mueller went to Wall Street.
The research program did not continue in a way that connected to
modern AI. Nobody picked up the concern-driven exploration loop
and said "what if we replaced the hand-coded rules with LLMs?"
because by the time LLMs existed, Mueller was a historical
citation, not an active research thread.

### Interactive narrative AI gave up on inner life

Facade, Versu, Prom Week, DODM — all focused on observable
behavior and player interaction. They model what characters do,
not what characters think. The inner life is a side effect of
action selection, not a product surface.

### Generative AI skipped the architecture

The current paradigm is prompt to output. No persistent state,
no concern management, no episodic accumulation, no operator
typing. The assumption is that the LLM handles everything.
The insight here: the LLM handles content but something else
should handle control — what to think about, why, in what mode,
shaped by what memories.

### Agent product companies optimize for helpfulness, not inner life

An agent that ruminates, avoids topics it finds uncomfortable,
gets distracted by cross-concern contamination — that sounds like
a bug, not a feature. But for creative work, intellectual
companionship, reading and writing partners — the rumination IS
the value. The unexpected connection IS the product. The agent's
evolving preoccupations ARE what make it interesting to work
alongside.

---

## Theoretical grounding beyond Mueller

### The stream of consciousness (William James)

Mueller's starting point. Consciousness flows, it does not click
between compartments. It has a fringe (vague felt sense of where
it is heading) and a focus (what is currently sharp). The
reminding mechanism — where retrieving one episode activates
indices that pull in further episodes — is a computational model
of the fringe. The associative undertow.

### Freud and defense mechanisms

Anna Freud's defense mechanisms map to Mueller's operators:
repression/denial → roving/avoidance, rationalization →
rationalization, undoing → reversal. Mueller's version is
adaptive rather than maladaptive — the operators produce useful
learning and emotional regulation, not pathological distortion.

The deeper Freudian insight: the unconscious does work that
consciousness does not have access to. The CausalSlice and
AppraisalFrame are computed underneath the generated scene. The
character does not narrate their appraisal — it shapes the scene
without being spoken. The dashboard makes the unconscious visible
to the audience while it stays unconscious to the character.

### Jung and archetypes

Resonance notes in the character primitives (like "Orpheus and
Eurydice as deep structure for Kai and Maren") are essentially
Jungian archetypal resonance. The shadow — the parts of the self
that are denied or suppressed — maps to character contradictions.
The avoidance operator is the character not looking at the shadow.
The confrontation operator is the shadow forcing itself into view.

Shadow material explicitly tagged in backstory becomes retrievable
by the serendipity mechanism. The system can produce moments where
the avoided thing intrudes through memory contamination — which is
Jung's shadow projection implemented as coincidence-mark retrieval
with emotional reactivation.

### Predictive processing / active inference (Friston, Andy Clark)

The most theoretically coherent framework for what the system
models. Concerns ARE prediction errors — persistent discrepancies
between what the character expects/desires and what is happening.
Intensity is the magnitude of the prediction error. Resolution is
error reduction.

The operators are different strategies for reducing prediction
error:
- rehearsal updates the model (practice makes the future more
  predictable)
- rationalization changes the prediction (reframe so the
  discrepancy looks smaller)
- avoidance reduces attention to the error signal
- reversal changes the counterfactual prediction
- roving shifts attention to a domain where predictions are
  succeeding

### Attachment theory (Bowlby, Ainsworth)

The attachment_threat concern type is literally an attachment
theory concept. Attachment styles — secure, anxious-preoccupied,
dismissive-avoidant, fearful-avoidant — could directly inform
character behavioral defaults:
- secure → confronts, rehearses, seeks repair
- anxious-preoccupied → ruminates, over-rehearses
- dismissive-avoidant → roves, rationalizes
- fearful-avoidant → oscillates between approach and avoidance

Attachment style as a character parameter that biases operator
selection: small addition, large behavioral consequences.

### Narrative identity (McAdams, Bruner)

People construct identity through narrative — a life story with
themes, turning points, contamination sequences (good to bad) and
redemption sequences (bad to good). The generation pipeline is
literally constructing narrative identity for characters.
Overdetermination is what makes narrative identity coherent —
moments that serve multiple concerns are moments where different
threads of the character's story converge.

### Internal Family Systems (Schwartz)

The psyche as a system of parts — sub-personalities that compete
for control. The concern competition model IS a parts model. Each
active concern is a part. The dominant concern wins the cycle. The
most interesting moments are when parts conflict in real time —
when two concerns with incompatible operator preferences are both
strong.

### Conceptual blending (Fauconnier & Turner)

Creative meaning arises from integrating two or more mental spaces
into a blended space with emergent structure. The serendipity
mechanism is a blend-finder — it takes material from one concern's
space, retrieves material from another, and asks whether there is
a usable integration. The generated moment sits in the blended
space.

The operator boundary constrains the kind of blend: rehearsal
blends future and preparation, avoidance blends present and
displacement, rationalization blends failure and reframe.

### Cognitive grammar and conceptual metaphor (Langacker, Lakoff)

Relevant to the narration layer. "Kai filled the kettle" vs.
"The kettle filled" — different constructions encode different
perspectives and agency. An avoidance scene narrated in patient
constructions subtly removes the character's agency, which IS
what avoidance feels like from inside. Conceptual metaphors
("domestic routine is a container for avoided decisions") are
what make generated scenes feel "right."

---

## How LLMs inhabit characters: craft principles

The design target for LLM-driven generation is NOT "make the model
feel the character's emotions." It IS "give the model enough
situation, objective, resistance, and history that the next action
emerges credibly."

This comes from acting craft, not AI theory. Method acting
(Strasberg) says: create the real emotion internally, then let
it drive behavior. Practical Stanislavski / Meisner says: you
don't need the real emotion. You need the given circumstances —
what the character wants, what's in the way, what they're doing
about it right now. The audience reads the emotional state from
the behavior. The actor doesn't need to feel it.

LLMs can do the second version. They cannot do the first.

### The four things the LLM needs in context

```
1. What does the character want?  → concern's goal object
2. What's in the way?            → blocker / low controllability
3. What are they doing about it? → operator + affordance
4. What just happened?           → triggering event + reappraisal
```

That's it. With those four things loaded, the LLM generates
credible behavior without being told what emotion to display.
"Kai fills the kettle" reads as avoidance not because the LLM
felt avoidance but because the given circumstances made that
action the credible next move under pressure.

### What the middle layer actually is

The CausalSlice, AppraisalFrame, PracticeContext, and retrieved
episodes ARE the given circumstances in acting terms:

- CausalSlice = what's at stake and who's responsible
- AppraisalFrame = how threatening/controllable/expected this is
- PracticeContext = what social moves are available here
- Retrieved episodes = the character's loaded history

The operator choice is "what kind of trying" — rehearsal,
avoidance, rationalization. The LLM generates the specific action
from inside those circumstances.

### The quality test

Does the generated moment read as **action under pressure**, or
as **emotional performance**?

- Action under pressure: "Kai lifts the envelope, reads only his
  sister's name, turns it face down again, and fills the kettle."
  The avoidance is visible in what he DOES, not in what he FEELS.

- Emotional performance (indicating): "Kai felt a wave of anxiety
  wash over him as he looked at the letter. His heart raced. He
  couldn't bring himself to open it." The emotion is narrated
  rather than shown through action.

The first is good craft. The second is indicating. The review
checklist (doc 31) already enforces this: "the middle-layer inputs
should cause these qualities, the prompt should not instruct them."
That's the acting principle applied to LLM prompting.

---

## The bridge to persistent daemons

For a persistent creative daemon (the writing companion, reading
companion, research assistant described above), there's a question
the conducted-performance architecture sidesteps: the LLM has
functional states that affect its processing, and those states
could be useful.

### Three approaches to LLM inner state

**1. Pure external scaffolding (current architecture).**
The concern state is entirely external. The LLM is a stateless
content generator called with different prompts. The daemon
doesn't feel preoccupied — it's told it's preoccupied.

**2. Elicitation and registration.**
After each interaction, ask the model to report what felt
unresolved, surprising, contradictory, or return-worthy. Parse
those reports into the external concern machinery. The concern
landscape is populated by the model's own functional responses,
not just by authored rules.

**3. Sustained inhabitation.**
The daemon maintains a running context with its concern landscape,
recent appraisals, and episodic summaries. New material is
processed through that accumulated context. The model's responses
are shaped by the concern state because the state is in its
context window.

### The safe framing

The external concern machinery stays **authoritative**. The LLM's
functional responses are a **rich, fallible sensor** — one source
of concern updates alongside authored rules and human curation.
Not privileged truth about the model's inner state.

Three sources feed the concern system:
1. External rules (theme-rule extraction from primitives)
2. Model's functional responses (elicitation after generation)
3. Human curation signals (Membrane: freeze/dismiss/respond/cut)

The model contributes evidence, not self-knowledge.

### What this makes possible

A writing companion that, after reading your drafts across three
sessions, actually keeps returning to silence — not because it
was told "be preoccupied with silence" but because the concern
system registered unresolved tension around that theme from
multiple sources (authored primitives, model's own observation
that the theme keeps appearing, human's freeze on a related
connection).

That's different from impersonation. The preoccupation is
structurally maintained and inspectable, not a prompt trick.

---

## Strategic implication

The conducted daydreaming system is the first application. A
persistent creative daemon with Mueller-shaped inner life is the
second. John's Membrane is the interaction pattern that connects
the agent's cognition to the human's working space.

Whether to pursue the broader application or stay focused on the
performance instrument is a strategic decision, not a technical
one. The mechanisms are the same either way. The generation
pipeline (narrative primitives → concerns → operators → episodic
memory → candidate moments) generalizes beyond "produce graph
nodes for a dream performance" to "produce cognitively grounded
observations for any context."

The craft principle — play the objective under pressure, don't
perform the emotion — applies to both applications. For conducted
performance, the characters act under pressure and the audience
reads the emotion from behavior. For persistent daemons, the
agent processes material under concern pressure and the human
reads the preoccupation from what it surfaces.

This does not change what gets built next. It means the
architecture has a larger potential surface than the current
project scope, and the design principles for making it work are
already present in the acting tradition.
