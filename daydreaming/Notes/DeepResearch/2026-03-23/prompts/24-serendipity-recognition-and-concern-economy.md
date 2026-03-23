# Serendipity Recognition and the Broader Concern Economy

You have the full repo context. This prompt mixes deep code reasoning
(read the actual source) with outside knowledge (name systems, papers,
mechanisms we should know about).

## Why this matters now

The kernel has a working inner-life substrate: persistent character
state, memory membrane with promotion/demotion, typed situation
facts, reappraisal, cross-family bridges, frontier opening. All
proven in benchmarks.

But the broader vision requires something the kernel can't yet do:
**find connections it wasn't looking for.** The current system
retrieves episodes when a family plan asks for them. It never
independently notices that two concerns are structurally connected
through material it already has.

That's serendipity — Mueller's Chapter 5 mechanism. And without it,
the system can accumulate and regulate but it can't cross-pollinate.
The vault/daemon vision (a thinking partner that holds intellectual,
practical, interpersonal, and emotional concerns simultaneously
and lets them fertilize each other through shared memory) depends
on this mechanism more than anything else.

The connection graph substrate already exists. This prompt asks:
what's the minimum viable recognition step, and how does the concern
economy need to broaden to support it?

## Current kernel state (read these files)

- `kernel/src/daydreamer/rules.clj` — connection graph
  (`build-connection-graph`, `connection-edges`, `bridge-paths`,
  `serendipity-graph`). Already finds 2-4 hop candidate paths
  between rules. Already filters by accessibility (`:accessible` +
  `:frontier` visible to serendipity).

- `kernel/src/daydreamer/episodic_memory.clj` — coincidence-mark
  retrieval (`retrieve-episodes`), reminding cascade, provenance
  tracking. Episodes carry `:rule-path` and `:edge-path` provenance.

- `kernel/src/daydreamer/control.clj` — emotion-driven control loop,
  mode oscillation (performance/daydreaming), goal selection.

- `kernel/src/daydreamer/goal_families.clj` — family plan execution.
  Each family plan carries rule provenance. Stored episodes carry
  the rules that produced them.

- `kernel/src/daydreamer/goals.clj` — goal/concern management.

- Mueller's serendipity mechanism is condensed in
  `daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/mechanisms/13-serendipity-recognition.md`

## Question 1: Minimum viable serendipity recognition

Mueller's serendipity works like this: during reminding, an episode
is retrieved. That episode's rule provenance is checked against
the connection graph. If there's a path from the episode's rules
to an active concern's rules, serendipity fires — surprise emotion
is generated and attached to the concern, promoting it.

The kernel already has:
- `bridge-paths` finding 2-4 hop candidate connections
- Episodes with `:rule-path` provenance
- Active concerns (goals with emotional pressure)
- `serendipity-graph` filtering for frontier+accessible visibility

What's missing is the RECOGNITION function: "given a retrieved
episode and the set of active concerns, is there a structural path
through the connection graph that connects them?"

Questions:

- What is the minimum viable implementation of this recognition
  step? Not the full Mueller pipeline with verification and
  analogical planning — just "this episode connects to that concern
  through these rules."

- Should recognition fire during every retrieval, or only during
  specific phases (e.g., roving, or a dedicated "browsing" phase)?
  Mueller fires it during reminding specifically. What's the right
  trigger in our architecture?

- When recognition fires, what should happen? Mueller generates
  surprise emotion and attaches it to the concern. Is that enough,
  or should it also create a typed fact (`:serendipity-connection`)
  that later families can reason about?

- How should recognition interact with the regulation state? The
  design note in our sift says: tighten the serendipity threshold
  when overloaded, loosen when flowing. Concretely, what does
  "tighten/loosen" mean — fewer hops allowed? Higher coincidence
  mark requirement? Fewer concerns checked?

- What's the interaction with the membrane? Should serendipitous
  connections through provisional episodes count differently than
  connections through durable episodes?

- Are there computational implementations of serendipity-like
  recognition in other systems? Not just theoretical descriptions.
  Copycat's slipnet, MINSTREL's TRAMs, Hofstadter's Fluid Concepts
  — what actually ran and what can we learn from their
  implementations?

## Question 2: The broader concern economy

Mueller's concerns are all triggered by goal outcomes — something
succeeded or failed, emotions arise, daydreaming families activate.
The web chat identified three entry points for concerns:

1. **Something happened to my goals** — Mueller's territory.
2. **Something is interesting** — curiosity, salience, novelty. No
   goal failed. You just noticed something.
3. **Something doesn't fit** — cognitive dissonance. Two beliefs
   conflict. New information doesn't slot in.

Only entry point 1 exists in the kernel. The vault/daemon vision
needs all three.

Questions:

- How should curiosity-driven concerns enter the kernel's concern
  economy? What does a concern look like that wasn't triggered by
  goal failure? What emotional valence does it carry? How does it
  compete for scheduler attention against failure-driven concerns?

- How should dissonance-driven concerns work? When two stored
  episodes contradict each other, or a new fact contradicts an
  existing belief, should that automatically create a concern? What
  priority does it get?

- Mueller's REPERCUSSIONS is the closest existing family to
  intellectual exploration — "consequences of a hypothetical
  situation," activated by unspecified heuristics. Could
  REPERCUSSIONS be the home for curiosity-driven and
  dissonance-driven concerns if it were developed? Or do those
  concerns need their own families?

- The web chat's self-correction was: the gap is engine (serendipity,
  mutation, analogical planning), not families. The families are
  configuration on top of the engine. Is that right? Or does
  broadening the concern economy specifically require new family
  types that Mueller's seven can't express even with broader goals?

- In ACT-R, Soar, CLARION, or other architectures: how do
  curiosity-driven and dissonance-driven processing enter the
  system? Is there a standard mechanism, or does each architecture
  handle it differently?

- For the Symbiotic Vault / Membrane interaction pattern
  specifically: the user encounters a note, the system should
  "notice" and check whether it connects to anything. That's
  environment-input → retrieval → serendipity recognition. What's
  the minimum pipeline? Is it literally:
  1. receive external cue
  2. convert to indices
  3. call `retrieve-episodes`
  4. for each hit, check `bridge-paths` to active concerns
  5. if path found, fire surprise + promote concern
  — or is there a subtlety we're missing?

## Question 3: What connects serendipity to what we already have

The kernel has typed rules, a connection graph with real edges,
episodic retrieval with provenance, a memory membrane, and a
regulation state machine. Serendipity recognition should USE all
of these, not exist alongside them.

- How should the connection graph's current edge types
  (projection-based structural compatibility) relate to serendipity
  path quality? A path through 2 hops is more trustworthy than 4?
  Should edge compatibility scores weight the recognition?

- Should serendipity recognition be a kernel-level process (like
  reappraisal) that runs after retrieval on every cycle? Or should
  it be a family-level operation that only fires when the system
  has capacity (regulation mode = flowing)?

- The membrane currently gates on cross-family use evidence.
  Should serendipitous connection count as a different kind of
  evidence? "This episode was serendipitously connected to a
  concern through a 3-hop path" is not the same as "this episode
  was reused by a different family." Should it contribute to
  promotion? To frontier opening?

- Mueller says serendipity specifically searches INACCESSIBLE rules
  — the frontier. That's how creative discovery expands the
  planner's repertoire. We already have the frontier registry. How
  does serendipity recognition interact with frontier opening?
  Should finding a path through a frontier rule be the trigger for
  opening it?

## Output preference

For Question 1: give the smallest implementable recognition
function. Actual algorithm, not prose. What are the inputs, what
are the outputs, what's the decision criterion.

For Question 2: be honest about which parts need new architecture
vs which parts are configuration on the existing engine. Don't
oversell "just broaden the goal vocabulary" if it actually requires
structural changes.

For Question 3: ground everything in the actual code. The connection
graph, the retrieval system, the membrane, and the regulation state
all exist. Show how serendipity recognition would wire into them.
