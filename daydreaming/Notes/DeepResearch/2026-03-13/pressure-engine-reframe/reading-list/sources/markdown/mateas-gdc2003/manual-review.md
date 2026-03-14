# mateas-gdc2003 Manual Review

Image-reviewed companion notes for the layout-sensitive and architecturally important pages in `mateas-gdc2003/`.

Use this note when `source.md` is structurally thin around figures, tables, or sequencing mechanics. It does not replace the canonical extraction; it stabilizes the parts that matter most for later reasoning.

<!-- page: 1 -->
## Page 1

- Title: `Façade: An Experiment in Building a Fully-Realized Interactive Drama`
- Authors: Michael Mateas and Andrew Stern.
- Core framing from the opening pages:
- `Façade` aims for an interactive drama that feels open-ended moment to moment while still being shaped into a coherent, paced dramatic experience.
- The paper positions drama management as the missing layer between freeform player action and usable story structure.

<!-- page: 3 -->
## Page 3

- The paper describes `Façade` as a replayable one-act experience, roughly 20 minutes long.
- The target experience is a continuously playable scene without explicit menu branches or obvious branch-point prompts.
- The design goal is not pure branching plot and not pure sandbox simulation; it is a managed middle ground where local interaction stays fluid and global sequencing stays dramatic.

<!-- page: 8 -->
## Page 8

Figure 2, manually reconstructed:
- Top-left: drama manager, explicitly labeled as sequencing beats.
- Inside the drama-manager box: a `bag of beats`, a `selected beat`, and a `desired value arc`.
- Top-right: story memory, including `current values`, `previous action time`, and a record of beat activity not part of the active beat.
- Bottom-left: story world containing `Player`, `Trip`, and `Grace`.
- Bottom-right: natural language processing, with a pipeline from `surface text -> discourse acts -> reactions`.
- The figure makes the architecture split explicit:
- local character behavior lives in the story world and ABL behaviors,
- language interpretation maps player expression into discourse acts and reactions,
- drama management sits above both and chooses beats against a desired story-value trajectory.

Pressure-engine relevance:
- This is the cleanest early diagram of a layered controller stack: local simulation, interpretation, memory/state tracking, and a beat scheduler with an explicit target arc.

<!-- page: 9 -->
## Page 9

ABL notes stabilized from the image-reviewed page:
- ABL uses an `active behavior tree (ABT)` to keep track of active goals, subgoals, and behaviors.
- It combines sequential and parallel behaviors with propagation of success/failure through the tree.
- The language emphasizes synchronized joint behaviors, which is how Grace and Trip can perform coordinated dramatic action without hand-synchronizing everything externally.

Feature list called out on this page:
- working-memory elements (`WMEs`)
- preconditions and context conditions
- primitive actions (`acts`)
- mental acts
- subgoals and spawngoals
- joint behaviors
- priorities and persistence
- explicit success/failure requirements
- atomic behaviors
- specificity-based conflict resolution
- numeric success thresholds for parallel behavior
- sensors and demons
- meta-ABL for reflecting on and altering active behaviors

Why this matters:
- The local execution substrate is not just a scripting language. It is the mechanism that lets authored beat content stay interruptible and resumable when player action arrives.

<!-- page: 10 -->
## Page 10

API between ABL and the 3D story world:
- ABL behaviors query the world for object/player state and send action requests back to the world.
- The page lists world-query calls such as `GetObjectPosition`, `GetObjectRotation`, `GetObjectState`, and related object-state accessors.
- It also lists world-action calls such as `DoGestureAnimation`, `DoFullExpressionMoodAnimation`, `SetObjectToHold`, `PlaySoundEffect`, and event-notification hooks for cues and typed dialog.

Player-agent role:
- The player is not represented as an acting autonomous story-world agent.
- Instead, the player agent senses the human player’s movement, gaze, and interaction patterns and turns those into higher-level signals for Grace, Trip, and the drama manager.
- The paper explicitly mentions compound sensing such as significant movement around the room and sustained looking at objects.

Pressure-engine relevance:
- The player interface layer already does semantic compression before the scheduler sees anything. That is a useful precedent for treating raw player activity as evidence for state updates rather than as direct commands.

<!-- page: 11 -->
## Page 11

Beat structure:
- Only one story beat is active at a time.
- A beat provides Grace and Trip with a context-specific set of ABL behaviors that defines the local simulation envelope for that portion of the drama.

The five beat-goal types are stated explicitly:
- `transition-in`: characters express intentions for the beat.
- `body`: characters pose the dramatic question or situation to the player.
- `local/global mix-in`: reactions to player action before the beat resolves.
- `wait-with-timeout`: wait for player response.
- `transition-out`: final reaction to the player’s action or inaction within the beat.

Performance details:
- Beat goals are implemented as coordinated joint behaviors for Grace and Trip.
- The page lists the usual subcomponents of a performance beat goal:
- staging and movement,
- dialog line delivery,
- gaze targets,
- arm gestures,
- facial-expression fragments,
- head/face gestures,
- small emphasis motions timed against dialog.

Interpretation:
- The important abstraction is that a beat is not a single scene script. It is a bundle of coordinated, partially interruptible goal fragments with explicit slots for player intervention.

<!-- page: 12 -->
## Page 12

Table 1, discourse acts:
- The player’s typed dialog, gestures, and some movement patterns are mapped into a controlled discourse-act vocabulary.
- The table includes ordinary social acts (`agree`, `disagree`, `thank`, `apologize`, `greet`, `goodbye`), affective moves (`flirt`, `comfort`, `physicallyFavor`, `criticize`, `praise`), meta-conversational moves (`judgment`, `suggestion`, `misc-locution`, `nonAnswer`-style behavior), and a few safety-net categories such as `dontUnderstand` and `wanderAway`.

Context mechanism:
- Every beat is authored to respond to any discourse act at any time.
- The current beat activates multiple contexts:
- typically one local beat-specific context,
- plus one or more reusable global contexts.
- The local context carries the beat-specific dramatic meaning.
- The global contexts provide broad fallback responses and recurring motifs.

Why this matters:
- The system gets robustness by collapsing many possible player expressions into a smaller discourse-act layer, then using context to recover local meaning.

<!-- page: 13 -->
## Page 13

Two-phase NLP:
- Phase I maps surface text into discourse acts using permissive template rules.
- The paper says these rules intentionally err on the side of permissiveness so that many inputs, including ungrammatical ones, still land somewhere useful.
- Phase II maps the discourse act to a reaction for the current beat context.

Interaction handlers:
- Once a reaction is chosen, the beat’s interaction handlers abort the current beat goal and insert a new high-priority beat goal into the ABT.
- Early in a beat, this is usually a `mix-in` reaction that responds without resolving the dramatic situation.
- Later in a beat, the inserted reaction may be a `transition-out` that resolves the beat.
- After the inserted reaction completes, the previously interrupted beat goal can resume with alternate/repeat dialog if needed.

Global state effects:
- Mix-ins and transition-outs can carry side effects on story state.
- The paper gives concrete examples:
- praising Grace can shift player affinity toward Grace,
- referring to infidelity can raise story tension.
- Those side effects feed future beat selection and can even force the current beat to abort.

Pressure-engine relevance:
- This is a direct example of local reaction fragments updating global control variables, with the scheduler consuming those variables later.

<!-- page: 14 -->
## Page 14

Drama-management framing:
- The paper distinguishes `local agency` from `global agency`.
- Beat goals and interaction handlers provide local agency.
- The drama manager, implemented here as the beat sequencer, provides global agency by choosing the next beat from interaction history and current story state.

`Bag of beats` model:
- The authors argue that this works particularly well for open-ended, character-oriented “kitchen sink” drama.
- The scenario is intentionally designed so that only a subset of topics, secrets, and head games need to occur in any single run.
- The point is not to preserve one fixed canonical ordering; it is to preserve coherence under many acceptable orderings.

Beat Sequencing Language, author annotations:
- `precondition { ... }`
- `weight`
- `weigh_test`
- `priority`
- `priority_test`
- `effects <story value changes>`

Interpretation:
- This is an author-facing scheduling language, not a planner that invents content. Authors supply beat fragments plus annotation hooks; the system scores and chooses among them.

<!-- page: 15 -->
## Page 15

Beat sequencing decision, stated as a six-step process:
1. Initialize beat-specific state needed for selection.
2. Evaluate preconditions for all unused beats to form the satisfied set.
3. Evaluate priority tests and keep only the highest-priority tier.
4. Score those beats against the desired story arc using their effects.
5. Multiply score by weight.
6. Randomly select from the weighted scored set.

Figure 3, manually stabilized:
- The figure is an Aristotelian tension arc over beat time.
- It rises unevenly, with local climbs and dips, toward a late climax and then falls sharply after the climax.
- The beat scorer tries to choose the next beat whose tension effects best match the near-term slope of that target arc.

Failure mode described here:
- If the currently available candidate beats do not let the system approximate the desired arc, cumulative error grows.
- If there are no candidate beats at all, beat management fails outright.
- The paper treats both conditions as authoring feedback: the beat library is insufficient and must be revised.

<!-- page: 16 -->
## Page 16

Table 1, scoring terms for step 4:
- `Tcur`: current tension value.
- `numBeats`: number of beats remaining in the current linear arc segment.
- `Ttarg`: target tension for that arc segment.
- `slope_target = (Ttarg - Tcur) / numBeats`.
- `deltaTbeat`: the candidate beat’s tension change from its effects annotation.
- Candidate beat score decreases as the gap between `slope_target` and `deltaTbeat` grows.

Early-observations framing:
- The paper immediately treats the architecture as something to evaluate in terms of agency and pacing, not just implementation novelty.
- The chosen metrics are still rough, but the important point is explicit: if the scheduler cannot keep tension on track without collapsing player responsiveness, the architecture is not succeeding.

Overall takeaway:
- `Façade`’s most reusable idea is the separation between:
- local reactive performance machinery,
- semantic compression of player action into discourse acts,
- and a beat scheduler that scores authored content against a target dramatic arc.
