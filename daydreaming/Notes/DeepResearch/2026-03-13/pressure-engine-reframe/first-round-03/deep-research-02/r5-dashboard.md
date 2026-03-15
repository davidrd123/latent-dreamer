You should not build one dashboard. You should build two mode-specific surfaces that share a small common spine.

The spine is the live cycle packet. The split is:

* **performance dashboard** for traversal-time runs
* **authoring membrane** for candidate-generation and curation
* **inspector layer** that can open richer provenance when a human wants to ask “why?”

That keeps the stage contract thin while still letting the dashboard be richer than the stage. It also matches the actual system split: traversal is graph navigation, not generation, and authoring is propose → validate → admit, not playback.  

## 1. Traversal mode: mostly current state, not node archaeology

My answer is: **roughly 85% current traversal state, 15% node history**, and that 15% should be stable residue, not full generation provenance.

The live dashboard should primarily show what the run is doing **now**:

* current node / scene label
* current situation or cluster
* tension, energy, hold
* traversal intent
* event approach / eligible / committed state
* short temporal context, like recent path, revisit gap, and whether this is a return, shift, escalation, or release

That is enough to make the run legible as a present-tense process. The performance lane is supposed to feel like “circles, avoids, approaches, drifts, commits,” not like a notebook of old authoring decisions. That is also the experiential standard the traversal work is aiming at: mind-wandering with structure, not slideshow plus debug dump.  

What should count as the 15% “history” layer in traversal mode?

Use **stable graph residue** only:

* pressure tags
* line / subplot identity
* setup / payoff markers
* canonical vs counterfactual
* maybe one compact “why this surfaced” sentence

Do **not** surface, by default:

* causal slices
* appraisal frames
* retrieved episode lists
* operator score breakdowns
* full candidate matrices
* raw diagnostics for every non-selected option
* lookahead / beam-search internals
* full debug JSON from the scheduler

Reason: those belong to lane-local reasoning and inspection, not the main performance view. The current code already points at the right separation: trace rows are small and live-facing; debug rows carry the selection/evaluation guts. Keep that split in the UI instead of flattening it. Traversal mode should consume the trace-like layer first and expose debug-like detail only on demand.

So the traversal dashboard should have three bands:

1. **Live band**: current node, traversal intent, tension/energy, event state
2. **Context band**: recent path, revisit marker, active situation strip
3. **Inspector** (collapsed by default): why this step, stable node metadata, optional debug drill-down

That gives you a watched run, not a forensic report.

## 2. Authoring mode: invert the emphasis, make it a membrane

Authoring-time generation is the opposite. Here the dashboard should be **provenance-heavy and action-heavy**.

This mode is not trying to make a live run legible. It is trying to help a human decide whether a candidate deserves to become authored material. The architecture here is explicitly propose → validate → admit, and the current generation pipeline already emits the right artifacts: candidate text, graph projection, validation result, and a sidecar with causal slice, appraisal frame, practice context, retrieved episodes, operator family, operator score breakdown, and emotion vector summary. 

So authoring mode should show, per candidate:

* the candidate moment itself
* the graph landing zone
* the validator result
* the provenance chain that made it appear

Concretely, a candidate card should have:

**A. Surface**

* candidate text
* short scene title
* keep / reject / compare affordances

**B. Graph projection**

* situation_id
* delta_tension / delta_energy
* setup_refs / payoff_refs
* option_effect
* pressure_tags / practice_tags
* origin refs
* readiness / validation status

**C. Provenance**

* dominant concern
* causal slice
* appraisal frame
* practice context
* retrieved episodes
* selected operator and score breakdown
* emotion vector as compact summary, not the main explanation

**D. Action membrane**

* freeze
* dismiss
* respond
* cut

Those four actions should mean something precise:

* **freeze**: pin this candidate, or its provenance bundle, into a holding area. “Not yet, don’t lose it.”
* **dismiss**: negative salience. Usually “wrong time” or “not useful,” not “globally forbidden.”
* **respond**: attach human feedback and spawn a revision path. This should preserve lineage.
* **cut**: promote or splice material into the authored graph. This is the strong action.

This is where your bidirectionality notes matter. The membrane is not automatic graph ↔ prose sync. That remains unsolved in the broader landscape. The useful part is the designed loop where the system surfaces candidates and the human’s response changes future surfacing. Mixed-initiative systems that work still rely on curation, not magic round-tripping.  

So authoring mode should behave more like a review instrument than a player. It should support side-by-side comparison of baseline vs middle-layer candidates, ablations, and revised descendants. This is also where the current prototype memo points: automated checks got you to the edge, but human judgment is still the deciding layer. 

## 3. Narration companion: packet first, provenance only through derived hints

The narration companion should **consume the normalized cycle packet as its contract**.

It may also consume **small derived summaries from provenance**, but it should **not** ingest raw sidecar material wholesale.

Reason one: the traversal layer is present-tense. The narration companion is supposed to sound like the inner voice of the current moment, not like a committee explaining how the node was authored.

Reason two: the richer provenance objects are not all display-grade. In the middle-layer design, `EmotionVector` is the one object that is naturally suited to dashboard / narration use. The rest, causal slice, appraisal frame, operator matrix, retrieval list, are inspectability objects. They are for provenance and review. If you feed them raw into narration, the voice will start narrating the system’s paperwork. That will sound fake fast.

So the rule should be:

### Narration gets by default

* current node / scene text
* current situation
* traversal intent
* tension / energy
* canonical vs counterfactual status
* optional live frame
* optional short recent-path context

### Narration may get, as distilled hints

* one emotional shading cue derived from EmotionVector
* one threatened-goal cue derived from causal slice
* one memory-resonance cue derived from retrieved episodes
* one operator-family tone cue if you want subtle voicing differences

### Narration should not get raw

* full causal slice object
* full appraisal frame
* operator score table
* raw retrieved episode list
* validation metadata
* whole sidecar JSON

So: **packet-first, provenance-derived enrichments second, raw provenance never**.

If you want the user to inspect the richer story behind the moment, put that in a secondary companion pane called something like **Why this scene exists**. Do not make the narrator speak it.

## 4. Mueller-style pruning for L3: narrate control turns, hide maintenance

Mueller’s lesson is not “show less text.” It is “show only what changes the experienced thought.”

For L3 traversal, the dashboard equivalent is:

### Always surface

* a change in traversal intent
* a move into a different situation / cluster
* a recall after a meaningful absence
* event state changes: approaching, eligible, committed
* strong tension / energy inflections
* canonical ↔ counterfactual crossings
* hold / release transitions
* conductor interventions that materially changed the route

### Usually hide

* raw weight tables
* loser candidates
* recency penalties
* activation maps
* feature scores
* manipulation / exhaustion / reuse penalties
* raw diagnostic vectors
* lookahead alternatives that were not chosen
* bookkeeping like visit_count tables unless summarized into a legible fact

The pruning test is simple:

**If it changed the felt sequence, surface it.
If it only helped the scheduler arrive at the choice, keep it hidden.**

That means the dashboard narration layer should produce compact structural utterances like:

* “Holding here, pressure still rising.”
* “Returning to the threshold after a long gap.”
* “Release move, the route backs away from the checkpoint.”
* “Event now eligible.”
* “Counterfactual territory.”

Not:

* “The scheduler selected this because recall_value=0.41 and overdetermination=0.73.”

Also copy Mueller’s structural segmentation idea. In L3, the equivalents of paragraph breaks are:

* intent shifts
* recall entries
* event commits
* situation / cluster crossings
* counterfactual crossings

That gives the dashboard a rhythm.

## The short spec

**Performance dashboard**

* primary output
* live packet first
* stable residue second
* inspector optional

**Authoring membrane**

* candidate + graph projection + provenance + actions
* freeze / dismiss / respond / cut are first-class
* comparison and lineage matter

**Narration companion**

* packet-first
* provenance only through derived cues
* raw sidecars stay out of the main voice

**Pruning rule**

* narrate structural turns
* hide maintenance math

That is the clean split. If you blur it, traversal turns into explainability sludge, and authoring turns into a passive gallery. Keep the lanes separate.
