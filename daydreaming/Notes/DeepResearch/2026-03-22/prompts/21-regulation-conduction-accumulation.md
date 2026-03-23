# Regulation × Graffito Situation Model × Mechanistic Success Criteria

You have the full repo context. Do NOT summarize what the documents
say. Bring outside knowledge: name specific systems, papers, results,
and mechanisms that bear on these questions.

This prompt is intentionally narrower than the earlier broad
"regulation / conduction / accumulation" framing. The immediate need is
the next Graffito kernel slice, not performed interaction design or
very long-run personality drift.

## Current kernel state

Recent benchmark work now establishes:

- Assay A passed: live dynamic stored-source entry, pending source-use
  recording, first `:same-family-loop` movement
- Fixed-chain proof passed: repeated cross-family evidence can promote
  a provisional episode to `:durable` and open a frontier rule
- Assay B passed: that positive path now also works in a live
  deterministic benchmark
- A first Graffito microfixture passed:
  - Grandma's apartment as a typed rationalization context
  - night mural crisis as a typed reversal context
  - a mural-context retrieval probe can see the stored apartment
    rationalization episode through shared typed facts

Current Graffito kernel code is in:

- `kernel/src/daydreamer/benchmarks/graffito_microfixture.clj`

What it currently includes:

- typed fact categories for:
  - `:present-actor`
  - `:relationship`
  - `:role-obligation`
  - `:artifact-state`
  - `:exposure`
  - `:recent-event`
  - `:appraisal`
- honest family-plan storage for rationalization and reversal
- typed retrieval over shared facts

A second Graffito slice also exists:

- `kernel/src/daydreamer/benchmarks/graffito_regulation_slice.clj`
- adds sensorimotor regulation facts, cross-layer correspondence,
  person-object relations, and a first regulation/appraisal/operator
  contrast across street and apartment contexts

What it does NOT yet include:

- persistent character-state carried across situations (regulation
  vars are currently encoded as context facts, not as kernel-level
  Tony state that persists across situation transitions)
- explicit reappraisal-after-family on the same situation (families
  emit a `:reappraised` marker but don't yet update persistent
  character state and rerun appraisal)
- a miniworld-level benchmark with more than two deterministic slices

Recent 5 Pro / 5 Thinking work already pushed us in a direction:

- add sensorimotor regulation as first-class state
- keep layered correspondence separate from context sprouting
- treat Motherload / Grandma, castle / school, etc. as distinct
  entities linked by typed correspondence, not one identity
- use Graffito to move from scalar-first scheduling toward typed fact
  situations projected down to the current scheduler

So the next question is no longer "what is the full Graffito ontology?"
It is: what is the minimum next slice that makes the Graffito kernel
benchmark psychologically honest while staying mechanistic and
buildable?

## Question 1: Where regulation belongs in the existing loop

We now have two interacting structures:

1. Mueller-style emotional control already in the kernel:
   emotion generation, decay, concern activation, and family selection
   from weighted pressure.

2. A proposed Graffito regulation machine:
   `overloaded -> bracing -> entraining -> flowing -> creating`
   with companion variables such as sensory load, precision control,
   rhythmic entrainment, felt agency, cue coherence, and available
   control channels.

The concrete design question is:

**Where should regulation enter the current loop so that the same raw
Graffito cue can later be appraised differently and produce a different
family or operator choice?**

Please answer with outside-knowledge grounding, not repo summary.

Specific sub-questions:

- Should regulation primarily change:
  - event appraisal itself
  - emotion generation from appraisal
  - emotion decay / persistence
  - retrieval accessibility / threshold
  - family weighting / family availability
  - some combination of the above

- In appraisal theory terms: should regulation be modeled as part of
  coping-potential appraisal, or as a separate modulator downstream of
  appraisal? What different behaviors would those two choices produce?

- In architectures like ACT-R, Soar, PSI / MicroPsi, active inference,
  or related models: what are the closest precedents for this kind of
  regulation-sensitive change in processing? Which mechanism is most
  transferable here?

- For Graffito specifically: if the same sensory situation remains
  intense, but entrainment and felt agency rise, what should change
  computationally?
  - threat -> challenge?
  - more retrieval access?
  - a shift from reversal / repercussions toward rehearsal /
    rationalization?
  - different operator choice inside the same family?

- Is it more psychologically plausible to model the shift as:
  - suppression of previously generated negative emotion
  - changed appraisal of the same event
  - changed weighting of already-generated emotion
  - or some hybrid

### Sub-question 1.5: Character-state ownership and persistence

A recent 5 Thinking reply corrected our design: regulation variables
should live at kernel/character level, not as situation facts. The
situation provides inputs (crowd density, rhythmic affordances,
co-regulator presence). The character carries the processing
(sensory_load, entrainment, felt_agency, perceived_control).

- What is the minimum persistent `:character-state` for Tony? Which
  variables must persist across situation transitions, and which
  should be recomputed from the current situation each cycle?

- Should Grandma and Monk also carry regulation/appraisal state in
  v1, or only Tony? If yes, what subset? If no, what are the
  tradeoffs of single-character-state vs multi-character-state?

- What should decay across cycles (transient regulation) vs remain
  as a learned shift (accumulated capability)? Is there a principled
  distinction from the appraisal or motor-learning literatures?

- At what point in the cycle should the kernel rerun appraisal:
  immediately after family execution, after effect application,
  after episode storage, or after all branch-local consequences
  settle? What are the tradeoffs?

## Question 2: Minimum next representation for Graffito

The current microfixture proves typed facts can drive activation,
family planning, and retrieval. But it still lacks the pieces the
earlier Graffito replies called out most sharply.

Please give the **minimum buildable representation** for the next
Graffito kernel slice, not a full ontology.

The likely new elements are:

- `sensorimotor_regulation_facts`
- `cross_layer_correspondence_facts`
- `person_object_relations`
- richer object-state progression for:
  - Can
  - Sketchbook
  - Elephant
  - Mural

Specific sub-questions:

- What is the smallest useful typed schema for
  `sensorimotor_regulation_facts`?
  Please prefer a compact set of fields over a huge ontology.

- What is the smallest useful schema for
  `cross_layer_correspondence_facts`?
  We want explicit counterpart structure without turning myth/magic
  into a full third simulated world yet.

- What should be encoded as ordinary typed facts versus dedicated
  object-state or relation structures?
  For example, should the Can's phase be a fact, a relation, an object
  map, or a hybrid?

- What is the minimum person-object relation vocabulary that would make
  Graffito meaningfully different from ordinary artifact-state facts?
  We are especially interested in:
  - inheritance / lineage
  - regulation function
  - skill / attunement
  - legitimacy / permission
  - symbolic correspondence

- Please propose the smallest situation set for the next benchmark.
  Earlier replies suggested `street + apartment + mural` or a nearby
  variant. Is that still the right first mechanistic run?

## Question 3: Rehearsal as regulation, not generic future planning

The current Graffito microfixture uses rationalization, reversal, and
typed retrieval. It does not yet include rehearsal.

But the earlier Graffito replies strongly implied that rehearsal is
central here because learned embodied routine and movement-to-control
are core to the story.

So:

- What should rehearsal mean in Graffito if we want it to be about
  practiced regulation and embodied control, not just generic imagined
  success?

- What should rehearsal change in the kernel state?
  Examples:
  - cue coherence
  - felt agency
  - entrainment
  - retrieval weighting
  - object affordance availability
  - appraisal fields

- What is the cleanest mechanistic relation between rehearsal and later
  family behavior?
  For example:
  - rehearsal changes later appraisal of the same overload cue
  - rehearsal changes which reversal counterfactual is selected
  - rehearsal changes whether the Can or mural affordance can activate

- Are there cognitive-architecture, motor-learning, flow, or
  sensorimotor-control precedents that would help distinguish
  rehearsal-as-regulation from rehearsal-as-generic-planning?

- How should the kernel distinguish short-horizon regulation
  improvement (transient state change from one rehearsal cycle) from
  longer-horizon learned skill (accumulated capability that persists
  across sessions)? Should rehearsal write only transient
  character-state changes, or also durable episode evidence? When
  does repeated rehearsal become an accumulated capability rather
  than a momentary state shift?

## Question 4: Exact mechanistic success signature

We do NOT want an aesthetic criterion here like "is it interesting?"
We want a stronger mechanistic benchmark target for the next Graffito
slice.

Please define the clearest **trace-level success criterion** for a
Graffito kernel benchmark that goes beyond the current microfixture.

The key desired shape is:

- same or strongly overlapping sensory / situational cue family
- later changed appraisal
- later changed family choice or operator choice
- with provenance clear enough to inspect in a trace

Specific asks:

- Give one or two concrete Graffito examples:
  - same raw cue, different later appraisal
  - same raw cue, different later family/operator choice

- Say what exact trace evidence should be visible if the kernel is
  succeeding mechanically.

- Distinguish clearly between:
  - "typed retrieval happened"
  - "regulation changed appraisal"
  - "the changed appraisal altered behavior"

- If helpful, suggest one benchmark assertion shape that could be
  encoded in a future regression test.

## Output preference

Please answer as a build-order memo, not a literature dump.

For each of the four questions:

1. give the strongest outside-knowledge conclusion
2. say what it implies for this repo right now
3. name the smallest next implementable slice

Please prioritize:

- buildable guidance over completeness
- mechanisms over aesthetics
- concrete distinctions over broad metaphors

We do NOT need, in this round:

- a full conducted-performance interaction analysis
- APC control vocabulary design
- very long-run accumulation / style / personality divergence analysis

Those may matter later, but they are not the next Graffito kernel
decision.
