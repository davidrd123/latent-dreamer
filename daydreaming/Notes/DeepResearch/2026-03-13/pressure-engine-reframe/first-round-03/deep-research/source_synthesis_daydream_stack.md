# Source Synthesis for the Daydream Stack

This document regroups the prior-work notes by what the reader actually needs to understand the project. The canonical split still comes from `11-settled-architecture.md`: `L3` is a traversal scheduler over authored graph material, `L2` is the character exploration engine, `L1` is a research-stage authoring assistant, the graph seam is the membrane between them, and the inner-life dashboard is the main early output rather than a debug view. `12-prior-work-synthesis-against-settled-architecture.md` sharpens the same point: the prior work does not collapse the layers into one grand engine. It makes the boundaries cleaner. `27-authoring-time-generation-reframe.md` then changes the sequencing: once `L3` is credible, the bottleneck is not scheduler cleverness but graph material supply, so authoring-time generation matters more than a critic-only `L1` story. `28-l2-schema-from-5pro.md` freezes the middle layer: Mueller provides control, EMA provides appraisal dynamics, OCC provides emotion semantics, and Versu provides situation structure.

That is the frame for everything below. The question is not “what did each paper say?” The question is “what job does each source do in this stack, what did the project keep, and what did it leave on the floor?”

## 1. `L3` traversal scheduler lineage: Façade → DODM → Authorial Leverage → Suspenser

### Façade: the scheduler as editor over authored material

Façade is the clearest ancestor for `L3` because it is not trying to simulate character cognition and it is not trying to generate a world from scratch. It is an editor over a bag of authored beats. That is the right plain-language description for why it matters here. The system chooses from authored fragments, using state and author annotations, to make a run feel shaped rather than random.

The load-bearing idea is the selection pipeline. Façade says the scheduler should not be one flat score over everything. It should move through stages: what is available now, what gets hard-promoted into the highest priority tier, what best matches the desired trajectory, what gets soft weighting, and then what is chosen. That is almost exactly the missing skeleton for `L3` in this project. It gives the traversal scheduler a clean structure for separating “cannot happen now,” “should happen soon,” and “would make sense next.”

The second idea is that the scheduler is tracking an explicit arc, not just local freshness. Façade’s tension scorer is primitive by current standards, but the project does not need the old Aristotelian curve itself. It needs the lesson that traversal should be judged against some target shape, whether that shape comes from authored run mode, the conductor, or slow arc-state in the traversal layer.

This connects cleanly to the settled architecture. `L3` already exists to schedule traversal over authored graph material, not to think like a character. Façade supplies the operational form of that sentence. It says what the scheduler does between authored units.

What the project kept: the staged pipeline, per-node availability and effect annotations, hard priority tiers before soft weighting, and the idea that `L3` is an editor over authored material rather than a cognition engine. What it left behind: the NLP/discourse layer, the beat-internal behavior machinery, the player model, and the old ABL-specific implementation. Those are not traversal concerns here.

### DODM: turn the scorer into a named feature registry

If Façade gives `L3` its skeleton, DODM gives it a cleaner scoring language. In plain terms, DODM is Façade after someone noticed that one hand-built tension formula is not a usable long-term authoring interface. It replaces one scorer with a weighted sum of named features.

That is the load-bearing import. `L3` should not be a pile of opaque heuristics. It should have an explicit feature registry that says what the scheduler cares about: trajectory fit, whether a node has been prepared, whether traversal is mixing or over-fixating on one situation, whether it is homing toward a committable event, whether it is paying off earlier setup, whether it is exhausting material, and so on. This is the point where “traversal diagnostics” become more than warning labels. DODM turns them into a score surface.

The second useful import is lightweight intervention vocabulary. DODM distinguishes between selecting the next thing and shaping what is even eligible. That maps well to this project’s conductor and traversal logic. The scheduler can promote, suppress, or re-enable graph material without mutating canon. That is much cleaner than pretending every decision is only a rank-ordering problem.

DODM connects Façade to the rest of the architecture by making the graph seam more explicit. Once you score by named features, authored graph nodes need stable annotations that can be read by traversal without importing `L2` internals. That is exactly the discipline `11-settled-architecture.md` and `12-prior-work-synthesis-against-settled-architecture.md` keep pushing: `L3` reads graph-readable residue, not raw concerns, appraisals, or context trees.

What the project kept: a weighted feature sum, local graph annotations like `motivated_by`, `setup_refs`, `payoff_refs`, line or situation identity, event approach metadata, and the possibility of shallow lookahead. What it deliberately did not inherit: full expectimax to story end, a fake player model, heavyweight DM interventions, or a planning ontology that would turn `L3` into a second `L2`.

### Authorial Leverage: this is the test, not the runtime

Authorial Leverage matters because it asks the right rude question: is this scheduler actually earning its keep, or is it just a dressed-up set of simple rules? In plain language, this paper is not a design source. It is a bullshit detector.

Its load-bearing ideas are three evaluation criteria. First, equivalent policy complexity: how ugly would the hand-authored trigger logic have to be to mimic the scheduler’s behavior? Second, resilience to graph changes: does the same scheduler absorb new material with local annotation changes, or does it require constant retuning? Third, variety under quality: does it produce multiple distinct good runs instead of one fixed “best” path or random mush?

This connects directly to Façade and DODM because it tells you how to judge whether they are worth implementing in this project’s setting. It also connects to the conductor. Here the “author” is not only the graph author. It is also the live conductor. A traversal layer has leverage if simple conductor bias can produce legible differences in runs without forcing low-level playlisting.

What the project kept: the evaluation frame for Experiment 1, especially equivalent-policy complexity, graph-change resilience, and controlled variety in watched runs. What it left behind: the full decision-tree methodology as a required tool. For this scale, the important thing is the question, not the exact evaluation apparatus.

### Suspenser: tension is not only a slope, it is a shrinking option space

Suspenser contributes one very specific thing. It is not another scheduler architecture. It is a model of suspense that says tension rises when the protagonist’s perceived solution space narrows.

That matters because the project’s current traversal language already has trajectory-level tension: flatlines, spikes, escalation, release. Suspenser adds a second axis. A run can be flat in terms of immediate intensity and still become tighter because options are silently closing. That is useful. It gives `L3` a structural notion of tension rather than only a temporal one.

The load-bearing idea is simple: nodes can be tagged by whether they open options, close options, clarify options, or do none of those. Situations can carry an authored or lightly derived sense of how many plausible resolution paths are still alive. That gives the scheduler a way to distinguish “quiet because nothing is happening” from “quiet because the trap is closing.”

This connects backward to DODM because structural tension can become one more named feature in the traversal score. It connects forward to the conductor because “escalate” no longer only means “pick a hotter node.” It can also mean “close off routes.”

What the project kept: the idea of structural tension as a second feature family, with lightweight graph annotations like `resolution_path_count` and `option_effect`. What it left behind: full planning-based solution enumeration, fabula/sjuzhet formalism, and any move that would force `L3` into real search over causal plan spaces.

### What this lineage made `L3`

Taken together, these four sources say something crisp. `L3` is not a cognition layer and not a world modeler. It is a Façade-shaped traversal pipeline, scored by a small DODM-style feature registry, judged by Authorial Leverage, and sharpened by Suspenser’s structural-tension term. That is why the settled architecture insists that `L3` shares control geometry with the other layers but not ontology. The conductor biases the scheduler. The scheduler scores graph material. The graph seam stays thin.

## 2. `L2` character cognition lineage: Mueller → EMA → OCC → Versu

### Mueller: the base control loop and the shape of inner processing

Mueller is still the base layer because he answers the question the other sources mostly do not: what does a daydreaming mind actually do from moment to moment? In plain language, DAYDREAMER is a concern-driven machine that repeatedly picks what feels most pressing, explores one step of that line of thought, updates emotion and memory, and then does it again.

The first load-bearing idea is the concern-centered control loop. The system is not a global planner. It is not a drama manager. It is not a chatty narrator choosing whatever sounds interesting. It is a loop over active concerns, driven by emotional motivation, with explicit mode switching between performance and daydreaming. This is the core of `L2` in the settled architecture: the live character-pressure engine.

The second idea is that the daydreaming goal families are not loose themes. They are typed operators with distinct triggers and distinct write-back semantics. Rehearsal, reversal, rationalization, roving, revenge, recovery, and repercussions are different modes of inner work. The focused source trace is useful here because it cuts away some later hand-waving: these are not just labels an LLM gets to pick because they sound dramatic. In Mueller, they are competing control policies with different activation conditions.

The third idea is the memory architecture. DAYDREAMER does not primarily retrieve by nearest-neighbor similarity. It uses coincidence-mark retrieval over intersecting indices, plus reminding, recursive index expansion, emotion reactivation, and serendipity recognition. That is one of the few places where Mueller is still sharper than many modern “memory” stories.

This is where the connection to the other `L2` sources matters. Mueller gives the control loop, the concern/write-back distinction, contexts for hypothetical branching, reminding, serendipity, and mutation. But he leaves concern birth and intensity too loose for current use, the emotion vocabulary too coarse, and the social-situation layer too implicit. EMA, OCC, and Versu each fix one of those holes.

What the project kept: concern-centered control, theme-rule concern initiation, the split between personal-goal and daydreaming-goal concerns, context branching for hypothetical exploration, recursive reminding, serendipity as validated cross-connection, and mutation as a fallback when local exploration is stuck. What it deliberately did not inherit: the old interpersonal domain, the exact GATE representation language, the full 1980s rule base, the source’s English generator as the core realization layer, and the idea that `L2` should directly become the traversal scheduler.

### EMA: appraisal is the missing middle, not a replacement loop

EMA matters because Mueller’s loop is good at deciding what to think about, but much looser about how an observed or imagined development changes concern state. In plain language, EMA says emotions are not labels you slap onto events. They arise from a fast, repeated appraisal pass over the agent’s current interpretation of what the event means.

The load-bearing import is the appraisal pass itself. An `L2` step should not jump straight from event to concern mutation by hand-authored vibes. It should build a structured interpretation of the situation, then read appraisal variables from that structure: desirability, likelihood, controllability, changeability, attribution, temporal status, expectedness. That is why `28-l2-schema-from-5pro.md` insists on `CausalSlice` as the missing middle object. Without it, “EMA-lite” becomes decorative vocabulary over ad hoc updates.

The second import is reappraisal. In EMA, coping changes the interpretation, and that triggers another appraisal. That fits `L2` extremely well. Rehearsal, rationalization, avoidance, confrontation, or confession are not just outputs. They are interventions that change what the character thinks is possible, responsible, likely, or threatening.

EMA connects to Mueller by tightening the event-to-concern path without taking over the whole engine. It answers “why did this pressure change?” while Mueller still answers “what process runs next?” It also prepares the ground for OCC, because appraisal variables are what generate emotion semantics.

What the project kept: an explicit appraisal pass, controllability and changeability as one input to operator selection, and reappraisal after exploration or commit. What it left behind: the full Soar implementation, the heavy decision-theoretic plan representation, and any temptation to treat appraisal as a second standalone engine.

### OCC: the concern vocabulary needs emotion semantics, but not a zoo

OCC matters because once appraisal exists, the system needs a cleaner account of what kind of emotional state has been appraised. In plain language, OCC is a taxonomy of emotion types based on what elicited them: consequences of events, actions of agents, and aspects of objects.

The load-bearing idea for this project is not “import all 22 emotion types and call it a day.” It is the structural distinction between kinds of emotion and the intensity variables that drive them. Anger is not just “high negative valence.” It has a shape: distress plus blame or reproach. Fear and hope are prospective, not actual. Relief and disappointment are about confirmation and disconfirmation. That matters because the project’s concern vocabulary should stop pretending every pressure is the same scalar.

The second import is intensity structure. Concern intensity should be derived from desirability, likelihood, attribution, and related appraisal variables. It should not be hand-set whenever the system wants more drama.

OCC connects EMA to the rest of the `L2` stack. EMA produces appraisal structure. OCC tells you what emotional pattern that structure implies. Then Versu constrains which socially legible moves exist in response.

What the project kept: typed emotion semantics as a derived view, compound-emotion structure where needed, and intensity factors tied to appraisal. What it deliberately did not inherit: the full taxonomy as durable runtime state, the full object-based branch in v1, or the idea that emotion labels themselves should be the main persistent state. `28-l2-schema-from-5pro.md` is explicit here: `AppraisalFrame` is authoritative, `EmotionVector` is derived, and persistent runtime state should still center on concerns and practices. Also, OCC does not solve curiosity, so curiosity stays an epistemic concern with its own logic.

### Versu: social practices are the missing situation structure

Versu matters because Mueller plus EMA plus OCC still leaves a hole: what moves are actually legible in a particular social situation? In plain language, Versu models recurring social situations as practices with roles, phases, norms, affordances, and practice-specific state.

That is the load-bearing idea. A character with retaliation pressure does not have the same possible moves at a dinner table, in a private confession, and in an evasion ritual. Concerns alone are too abstract. Social practices make the action surface local, role-bound, and phase-sensitive.

The second import is the idea that practices suggest but do not force. A confrontation practice, confession practice, or status-repair practice gives a menu of possible moves. The character still scores them using concern state, appraisal fit, memory resonance, and operator preference.

Versu connects orthogonally to the rest of `L2`. Mueller gives control. EMA gives appraisal dynamics. OCC gives emotional semantics. Versu gives situation structure. That is exactly the four-way decomposition frozen in `28-l2-schema-from-5pro.md`.

What the project kept: social practices as typed runtime objects, role-conditioned affordance menus, practice lifecycles, and the idea that narration can draw adverbial texture from competing pulls inside a practice. What it left behind: Versu’s full world logic, the Praxis language, the menu-based player interface, the Regency content, and the design choice that the drama manager is itself a practice. In this project, the conductor sits above `L3`, not inside `L2` as a special practice.

### What this lineage made `L2`

The resulting picture is clean if you state it bluntly. Mueller is still the core. EMA, OCC, and Versu are not replacements. They are orthogonal upgrades. Mueller tells the engine what kind of inner process runs next. EMA tells it how the situation is being appraised right now. OCC tells it what emotional pattern that appraisal implies. Versu tells it which moves are socially available in the current situation. That is why the current schema uses `CharacterConcern`, `CausalSlice`, `AppraisalFrame`, `EmotionVector`, `SocialPracticeInstance`, `Affordance`, and `OperatorCandidate` instead of trying to flatten everything into one grab bag of “state.”

## 3. `L1` authoring assistance lineage: Sentient Sketchbook → MINSTREL, then the shift to authoring-time generation

### Sentient Sketchbook: mixed-initiative authoring is visible evaluation plus cheap suggestion browsing

Sentient Sketchbook matters because it gives a practical model for what a useful authoring assistant looks like when it is not trying to replace the author. In plain language, it is a level-design tool where the human sketches, the system continuously evaluates structural properties, proposes alternatives, and lets rejection stay cheap.

The first load-bearing idea is visible, continuous evaluation. The tool is useful even before the generative suggestions get clever, because the author can see what is structurally weak right now. That maps directly to `L1` as a narrow critic over typed world state or graph material.

The second idea is feasibility before polish. Suggestions that violate hard constraints should not enter the beauty contest. They should fail fast. That matches the settled architecture’s validity-first stance and the insistence that `L1` work over computable deficiencies rather than “make it more interesting” prompt theater.

The third idea is that suggestions mutate the current artifact rather than replace it wholesale. That fits the graph seam well. `L1` proposals should be local typed diffs, not fresh worlds.

Sentient Sketchbook connects to MINSTREL because once you have visible evaluation and local proposal units, the next question is what to do when a proposal almost works but not quite.

What the project kept: continuous visible evaluation, local proposal generation, novelty as a diversity term among deficiency-grounded candidates, and cheap rejection. What it left behind: the genetic algorithm details, game-map-specific metrics, and any idea that `L1` should start by freely inventing worlds rather than editing structured ones.

### MINSTREL: when stuck, transform-recall-adapt instead of regenerating blindly

MINSTREL matters because it gives a better answer to dead ends than “sample again.” In plain language, it is a case-based creativity system that changes the problem slightly, recalls something that works for the changed problem, then adapts the result back.

The load-bearing idea is the transform-recall-adapt pattern itself. That is exactly what `L1` needs when an operator proposal is close but wrong. Do not throw away the proposal and start from zero. Relax or redirect one structural element, retrieve a nearby pattern, then adapt back.

The second import is boredom control. If the system keeps proposing the same shape of fix, the author stops paying attention. So novelty is not the main objective, but it is a necessary gate.

MINSTREL connects naturally to Sentient Sketchbook. Sketchbook gives the mixed-initiative loop and evaluation surface. MINSTREL gives the repair move when the first suggestion set fails.

What the project kept: transform-recall-adapt as a repair pattern for rejected proposals, novelty gates on authoring suggestions, and the preference for mutating near-miss proposals over brute-force restart. What it deliberately did not inherit: the King Arthur domain, full recursive imaginative memory as a first-pass implementation, or any move that would import TRAM-style creativity directly into `L2` runtime. That is authoring-time behavior, not character cognition.

### Why this is no longer only a critic story

If the architecture had stayed where the earlier `L1` notes began, Sentient Sketchbook plus MINSTREL would mostly justify a narrow critic-and-repair layer over an already-authored graph. That was the earlier plan. `27-authoring-time-generation-reframe.md` changes the sequencing.

The bottleneck is now material supply. The problem is not only “how do we critique the graph?” It is “how do we get enough graph-compilable material without hand-authoring every node?” That changes `L1` from critic-first to generation-first.

The key move is that `L1` becomes authoring-time orchestration over `L2`-style machinery. Narrative primitives go in: characters, contradictions, charged places, unresolved situations, backstory events, world rules, resonance notes. The system generates candidate moments using concern pressure, reminding, operator choice, and place or situation context. Humans curate. Accepted moments compile to the graph seam. Only after that does the critic/repair pass matter.

This does not make `L1` an autonomous worldbuilder. The project very deliberately refuses that move. The graph remains human-curated. The important shift is just that graph material supply happens earlier than critic-only structural repair.

What the project kept from the older `L1` lineage: typed deficiencies, visible evaluation, local proposals, cheap rejection, and mutation-based repair. What it added from the reframe: a two-stage `L1`, with authoring-time generation first and critic/repair second. What it still left behind: unconstrained world dreaming, autonomous lore expansion, and prose-only suggestion systems that cannot compile to the graph seam.

## 4. Representation patterns: `ATMS` for hypothetical branching, Event Sourcing for canon/session history

### ATMS: the right answer to Mueller’s context problem, but not the next thing to build

ATMS matters because Mueller himself exposes the weakness of his context machinery: it is good for branching and backtracking, but awkward for reasoning across multiple hypothetical worlds at once. In plain language, an Assumption-Based Truth Maintenance System tracks which assumptions support which beliefs, across many simultaneous hypothetical contexts, while also tracking combinations that are known to be inconsistent.

The load-bearing import is not abstract elegance. It is cheap context comparison and better failure memory. Labels tell you which branches support a fact. Nogoods tell you which assumption bundles are dead on arrival. Support sets give provenance.

That connects directly to `L2`. If the engine is going to do richer hypothetical branching, compare branches, avoid re-exploring bad combinations, and keep provenance on why something was believed in a branch, ATMS is the right conceptual direction.

But the project is right not to put it on the immediate critical path. The current sources are clear on this. Appraisal and social practices are higher-leverage next steps. `ATMS` is a representation upgrade for later `L2` work, not a prerequisite for the current trajectory.

What the project kept: ATMS as the conceptual target for richer hypothetical-state management, plus the more modest immediate steals of assumption tracking, nogoods, labels on key facts, and stronger provenance. What it left behind for now: a full ATMS implementation and any attempt to push ATMS-style branching into `L3`, which explicitly does not use `L2`-style context forking.

### Event Sourcing: canon and session history need replayable provenance

Event Sourcing shows up in the notes as a reusable systems pattern rather than a paper the project is trying to imitate stylistically. In plain language, it means treating state change as an append-only stream of events, with replay and snapshots available when needed.

That matters here because the project has persistent canon, session-by-session consequences, and an explicit need for provenance. If a committed event changes the world, you want a durable record of that mutation, not just the latest mutable map and a vague memory that “something happened last session.” It also fits the graph seam discipline. Canon is not the graph, and traversal is not canon mutation. Event Sourcing keeps that split legible.

It connects to the other layers by making replay and explanation easier. `L1` proposals can be audited. `L2` commits can be distinguished from hypothetical exploration. `L3` can keep selection history and event-commit history separate.

What the project kept: append-only canon/session mutation history, replayability, snapshots, and provenance as shared infrastructure. What it left behind: any temptation to collapse the live world state, the authored graph, and the session log into one universal log-shaped ontology. Event Sourcing is a pattern for history and replay, not a replacement for the world state engine or graph seam.

## 5. What Mueller’s own book adds beyond the external papers

The external papers sharpen the architecture, but Mueller’s own book still contributes several pieces that nobody else quite replaces.

### Serendipity: not “random good idea generation,” but validated cross-connection

Mueller’s serendipity mechanism is stronger than the way the term is usually used now. In plain language, serendipity is not just a lucky retrieval. It is a detected connection between something newly active and an already-pressing concern, followed by a check that the connection is actually usable.

That is the load-bearing point. The book gives a real procedure: multiple triggers can launch serendipity, including input states, retrieved episodes, newly activated concerns, and mutations. The system searches for a connecting path in the rule graph, verifies it, constructs a usable episode or plan from it, and then boosts motivation through surprise.

This matters because it reframes how the project should think about novelty. Novelty is not “raise the temperature.” It is “find a structurally valid bridge that was not previously accessible.” That is exactly why `11-mueller-ch7-extraction.md` and `focused-source-trace.md` keep insisting that mutation is subordinate to serendipity, not a separate magic creativity sauce.

What the project kept: coincidence-based retrieval, lowered thresholds for near-hits, validated cross-connection rather than random divergence, and surprise as a real change in motivational state. What it left behind: the exact rule-graph implementation in old Lisp and the assumption that this needs to be ported literally before anything useful can happen.

### Overdetermination: the best material serves more than one pressure at once

Mueller’s discussion of overdetermination in Chapter 11 is one of the most useful high-level ideas in the whole source base. In plain language, a good piece of mental content often exists because it is doing several jobs at once. It is not only revenge, or only recovery, or only rehearsal. It is one scene or one thought that partially satisfies several pressures.

That matters across the whole stack. In `L2`, it explains why some imagined scenes feel more compelling than others. In `L1`, it supports “cross-pressure gain” as a strong criterion for accepted proposals. In `L3`, it suggests that event approach is stronger when the selected node advances several unresolved lines instead of only one.

What the project kept: overdetermination as a scoring intuition across layers, especially for graph material that touches multiple pressures, tags, or payoff lines at once. What it left behind for now: DAYDREAMER* as a full parallel-process merging architecture. The idea survives. The specific machinery is deferred.

### Episodic memory and reminding: retrieval is a stream, not a lookup table

Mueller’s episodic-memory chapters add two things that many current memory stories flatten away. First, retrieval uses intersection indexing and coincidence counts, not just generic spreading activation. Second, retrieval is not the end of the story. Reminding recursively activates more indices, recalls more episodes, and reactivates the emotions attached to them.

That is load-bearing because it makes memory active in the loop. A reminded episode changes the present character state. It is not just evidence for later scoring. That is why the settled architecture keeps “episodic retrieval” and “reminding cascade” in shared infrastructure.

This connects to authoring-time generation too. If generated or authored episodes can be reactivated by converging cues, then the system naturally produces material with lineage, return, and emotional texture. It also explains why the project is cautious about embedding-first memory. Embeddings may help, but they should not erase the coincidence-count reminding stream that gives Mueller’s system its characteristic feel.

What the project kept: intersection-style retrieval, recent-index pools, reminding cascades, emotion reactivation on recall, and the distinction between retrieval for planning and retrieval for reminding. What it left behind: general spreading activation as the main memory model and the idea that semantic similarity alone is enough.

### Narration layer: the inner-life dashboard should be a designed surface, not a dump

Appendix B matters because it proves Mueller was not only building an inference engine. He was also building a realization layer that knew the difference between actual events, hypothetical alternatives, relaxed assumptions, memory, and surprise.

The load-bearing idea is mode-sensitive narration with pruning. Different kinds of inner process should sound different. A remembered episode is not phrased like a current action. A reversal is not phrased like a factual assertion. A weakly held possibility is not phrased like a settled belief. On top of that, not every internal assertion should be shown. Rule-level pruning and paragraph breaks on concern switches are part of the design, not cleanup.

This connects directly to the inner-life dashboard in `11-settled-architecture.md`. The dashboard is not there to expose raw runtime objects. It is a designed output surface. Mueller already solved that problem once in text. The modern project just has more possible realization channels.

What the project kept: mode-sensitive framing, reminding narration, emotional phrasing with intensity and direction, rule-level pruning as a better control point than blanket operator-level filtering, and structural segmentation on concern shifts. What it left behind: the exact template engine as a core subsystem. Modern realization can use different tools, but it should preserve the discipline.

### Artistic and authorship stance: the machine is a creative instrument, not only a solver

Chapter 11 matters because Mueller did not treat daydreaming as only a cognitive-science curiosity. He explicitly argued that a system like this could matter for creative writing, animation, film, art, and music, and he took authorship questions seriously.

The load-bearing idea here is the artist-as-curator stance. The system can be apprentice, collaborator, or proxy depending on how it is used, but the human’s role does not disappear. Mueller also asks the right question about mechanism transparency: does understanding how the work is generated destroy its value? His answer is basically no. Understanding after the fact is easier than thinking of the thing in the first place.

That connects tightly to this project’s conducted format, graph seam, and authoring-time generation story. The human authors the world, curates generated material, conducts traversal, and experiences the inner-life dashboard. The system is not replacing authorship. It is changing where authorship sits.

What the project kept: the stance that daydreaming machinery can be used to generate story, animation, and art material; the artist-as-curator framing; and the refusal to treat explicit mechanism as artistically disqualifying. What it left behind: the source book’s narrower text-only output assumptions and its original 1980s domain.

## Conclusion

The cleanest way to summarize the whole prior-work cluster is this:

- `L3` comes from drama management, not cognition. Façade gives the pipeline, DODM gives the feature language, Authorial Leverage gives the evaluation frame, Suspenser gives one useful tension feature.
- `L2` stays Mueller-shaped. EMA, OCC, and Versu do not replace Mueller. They make him less ad hoc by adding appraisal, emotion semantics, and social-practice structure.
- `L1` started as critic-and-repair, justified by Sentient Sketchbook and MINSTREL, but the current bottleneck has shifted it toward authoring-time generation first and critic/repair second.
- `ATMS` and Event Sourcing are not new layers. They are reusable representation patterns for hypothetical-state management and canon/session history.
- Mueller’s own book still carries some of the strongest ideas in the stack: serendipity as validated connection, overdetermination, reminding as active memory, narration as a designed output layer, and a clear artistic stance.

The result is not one grand synthetic theory. It is a disciplined stack. `L3` schedules traversal over authored graph material. `L2` runs character-level inner processing with concerns and operators. `L1` helps produce and repair graph-compilable material at authoring time. The graph seam keeps those levels from rotting into each other. The conductor biases traversal rather than micromanaging content. The inner-life dashboard makes the system legible. That is the actual inheritance story.
