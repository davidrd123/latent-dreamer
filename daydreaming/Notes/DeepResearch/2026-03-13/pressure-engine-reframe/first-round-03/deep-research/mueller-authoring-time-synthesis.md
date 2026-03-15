# Mueller for Authoring-Time Generation

## What DAYDREAMER contributes, in current project terms

The useful part of Erik Mueller's DAYDREAMER is not the 1980s knowledge engineering stack. It is the control and memory architecture.

Mueller's system says: a character does not produce inner material by sampling random scene ideas. It produces inner material because unresolved concerns compete for attention, each concern recruits a characteristic kind of thinking, retrieved episodes change what comes next, and occasional cross-concern connections create surprising but usable new lines. That is exactly the shape your current authoring-time generation problem needs.

In current project vocabulary, the core translation is:

`narrative primitives -> CharacterConcern -> CausalSlice -> AppraisalFrame -> operator choice -> retrieval/reminding -> candidate moment -> validation -> graph compilation`

The reframe doc already points in this direction: the bottleneck is no longer L3 traversal theory, but material supply from narrative primitives into graph-compilable candidate moments. Mueller matters because he gives a non-hand-wavy answer to what should happen in the middle. The engine should not just prompt for scenes. It should run a pressure-driven inner loop that generates scenes of a particular cognitive type, with provenance, memory trace, and evaluable relevance.

That said, Mueller is also badly outdated in exactly the places you would expect: hand-coded common sense, brittle retrieval, crude affect representation, tiny domain coverage, and no aesthetic filter. So the right move is not "recreate DAYDREAMER." It is: keep the skeleton, replace the content machinery.

---

## 1. Control loop: what runs next, and why

Mueller's top-level answer is simple: concerns are initiated by inference or theme rules, the system selects the concern with the highest emotional motivation, runs local planning on that concern, then repeats. The whole stream of thought emerges from repeated reselection, not from a master narrator or worldbuilder.

Two details matter.

First, concern initiation is rule-driven, not ambient. A concern appears because some condition in the current context triggers it. Second, concerns are not all the same kind. Personal-goal concerns try to change reality. Daydreaming-goal concerns branch away from reality into hypothetical contexts and only write back limited residue. That split is load-bearing.

For the current pipeline, the translation is:

- `CharacterConcern` is the durable motivational state.
- `CausalSlice` is the interpreted local situation, not raw world state.
- `AppraisalFrame` is the authoritative per-cycle evaluation.
- `EmotionVector` is a derived view for biasing operator choice and narration.
- `OperatorCandidate` is the modern analogue of a daydreaming-goal move.

That is cleaner than Mueller, because it inserts an appraisal layer between state and emotion, but it preserves his actual achievement: generation is pressure-shaped. A candidate moment exists because something is pressing, not because the prompt needed variety.

Why this matters for authoring-time generation: it prevents flat scene inflation. If you start from narrative primitives alone, an LLM will happily produce plausible but generic material. If you start from a dominant concern plus an appraisal of what is threatened, desired, blocked, or blameworthy, the scene arrives already typed: rehearsal, avoidance, retaliation, reframe, repercussion, and so on.

What is outdated here:

- Mueller's emotion model is too scalar and too category-bound.
- His domain rules are too hand-authored.
- His context mechanism is weak for reasoning across branches.

What still matters:

- concern-centered scheduling
- explicit concern initiation rules
- separation between reality-writing concerns and hypothetical branching concerns
- per-cycle decay and reselection

For v1, that means: generate from `CharacterConcern` plus `AppraisalFrame`, not from whole-world free prompting.

---

## 2. Operator phenomenology: the seven kinds of daydreaming

Mueller's seven daydreaming goals are the real payload. They are not just a taxonomy. They are operator phenomenology: different kinds of inner material produced for different affective and temporal reasons.

### Rehearsal

Trigger: upcoming situation or active goal.

Function: practice what to do next.

Phenomenology: relatively clear, forward-looking, instrumentally grounded.

Current use: high-clarity candidate moments around active situations. In schema terms, rehearsal should score high when controllability is moderate, likelihood is uncertain, and there are usable affordances in the current social practice.

### Reversal

Trigger: failure, regret, or worry about failure.

Function: imagine how the failure could have been prevented.

Phenomenology: alternative past or failure-avoidance branch.

Current use: counterfactual repair nodes, especially useful for setup/payoff discovery and for discovering policy-like residues. Reversal is one of the best sources of graph-usable material because it naturally exposes blocked dependencies and missing preparations.

### Rationalization

Trigger: negative emotion from failure.

Function: reduce charge by reframing the failure.

Phenomenology: minimization, hidden blessing, external attribution, "maybe it was good this happened".

Current use: salience or policy shifts rather than direct ontic change. Rationalization nodes are especially valuable for companion voice and for updating stored episode valence.

### Roving

Trigger: negative emotion that the system cannot or does not want to resolve directly.

Function: attentional shift to unrelated or more pleasant material.

Phenomenology: distraction, drift, avoidance.

Current use: low-clarity avoidance nodes. In the current schema this may collapse into an `avoidance` family, but that is only acceptable if the output still keeps the roving feel rather than turning all avoidance into generic withdrawal.

### Revenge

Trigger: directed negative emotion toward another agent.

Function: imagine retaliation or the other's failure.

Phenomenology: targeted hostility, prestige fantasies, role reversal.

Current use: a subtype of retaliation pressure. This should probably remain typed in provenance even if later grouped under a broader operator family, because it carries a distinct target structure and emotional signature.

### Recovery

Trigger: past failure that remains active.

Function: imagine future success at the failed goal.

Phenomenology: repair, return, eventual success.

Current use: often best treated as a forward-looking repair operator sitting between rehearsal and reversal. Recovery moments are especially useful for generating payoff candidates after humiliation, loss, missed contact, or prior collapse.

### Repercussions

Trigger: hypothetical future threat or major possible event.

Function: explore downstream consequences.

Phenomenology: contingency spreading, anticipatory chain reaction.

Current use: pressure-propagation nodes. This is valuable for graph growth because one hypothetical perturbation can generate many later candidate moments and event seeds.

The current repo vocabulary should not erase these distinctions. Even if v1 collapses some families operationally, every generated node should still record the Mueller-type provenance. Otherwise you lose exactly the thing Mueller gives you: candidate moments that differ not just in content but in mental mode.

A good practical rule is: every candidate moment gets a `family` or operator tag that says what kind of daydreaming produced it, plus a provenance object that records the dominant concern, source concerns, appraisal signature, and top emotion signature.

---

## 3. Episodic memory and recursive reminding

Mueller's episodic memory is not a scrapbook. It is part of generation control.

Episodes are stored from both real and imagined experience. They are indexed under goals, emotions, people, objects, and other features. Retrieval is not enough by itself. What matters is what happens after retrieval:

1. the episode enters the recent pool
2. its associated emotions reactivate
3. its other indices become active
4. those indices retrieve further episodes
5. reminding recurses
6. each recalled episode is checked for serendipitous applicability

That is the mechanism by which a second pass through a concern differs from the first. The system is no longer reasoning from the same local state. The active memory field has changed.

This matters a lot for the authoring-time pipeline. If generation is just `concern + prompt`, each operator fires in isolation. If generation includes reminding, then candidate moments accumulate pressure trace and autobiographical texture. The engine starts producing "this reminds me of that other time" material, which is exactly the kind of connective tissue that makes later graph traversal feel like mind-wandering instead of playlist switching.

Current translation:

- `retrieve_by_embedding(query)` handles semantic similarity.
- `retrieve_by_threshold(active_indices)` preserves Mueller's coincidence logic.
- `reminded episode refs` and active indices stay inside runtime trace, not exported raw into the graph seam.
- only stable residue gets exported, such as `origin_pressure_refs`, `pressure_tags`, or provenance signatures.

What is outdated:

- exact frame-match retrieval is brittle
- hand-coded episodic rules are too sparse

What should survive:

- thresholded coincidence retrieval
- recursive index expansion
- emotion reactivation on retrieval
- the idea that reminding is itself a generator of new candidates

This is also where candidate-moment scoring starts. A moment with strong episodic resonance, especially if it reactivates old charge and links multiple indices, should usually score above a merely fluent scene with no memory pressure behind it.

---

## 4. Serendipity: not "random idea generation," but verified cross-connection

Mueller is unusually concrete here. Serendipity is not a vibe. It is a procedure.

The system takes something newly salient - an input state, object, retrieved episode, or mutated action - and asks whether it connects to an active concern through the rule-connection graph. It searches for connecting paths, verifies the path by binding propagation, and only then treats the result as a usable cross-connection. On success it produces surprise and boosts the concern's motivation.

Two things follow.

First, mutation is subordinate to serendipity, not a separate creativity engine. A mutated action only matters if it can be shown to connect back into concern space.

Second, "interesting" is not enough. The connection has to be operable.

Current translation:

- the modern equivalent of the rule-connection graph is not just an embedding space; it is the overlap between active concerns, appraisal signatures, retrieved material, affordances, and world constraints.
- a good modern serendipity detector should combine soft retrieval with hard validation.
- a candidate should count as serendipitous when it is both non-obvious and structurally admissible.

In schema terms, this means `source_concern_ids`, `appraisal_signature`, `practice_signature`, and retrieved source material should all be able to jointly justify the candidate. In scoring terms, this suggests a feature like `episodic_resonance` or `verified unexpected fit`, rather than generic novelty.

This matters for graph material supply because serendipity is one of the few principled ways to get moments that are both surprising and narratively anchored. Without it, novelty drifts into tone-consistent filler. With it, the graph gets the sort of candidate that feels like "of course that memory would intrude here, even though I didn't see it coming."

---

## 5. What LLMs now resolve that Mueller could not

Mueller's architecture was right in shape and wrong in substrate.

The five biggest bottlenecks are clear.

### 1. Rules as imagination ceiling

Mueller could only daydream about things he had explicit rules for. LLMs blow this ceiling off. They can fill an operator with specific, situated content drawn from broad common-sense and narrative knowledge.

### 2. Brittle retrieval

Mueller's index matching was clever but narrow. Embeddings let you retrieve by semantic, emotional, and structural likeness. This makes reminding much richer.

### 3. Discrete emotion labels

Mueller's emotion handling is serviceable but crude. The current middle layer can do better: `AppraisalFrame` first, derived `EmotionVector` second. This handles ambivalence and mixed states far better than direct goal-type lookup.

### 4. Common-sense knowledge gap

Mueller had to hand-author a tiny interpersonal world. LLMs bring large amounts of implicit social, sensory, and causal knowledge.

### 5. No aesthetic evaluator

Mueller could assess desirability, realism, and similarity, but not whether a moment is actually worth keeping. LLMs and humans together can do first-pass aesthetic filtering.

But LLMs do **not** replace the architecture.

They do not solve:

- constraint enforcement
- world-state consistency
- concern scheduling
- operator typing
- provenance tracking
- overdetermination scoring
- graph compilation discipline

So the right summary is:

- LLMs replace Mueller's content bottlenecks.
- They do not replace his control loop or memory logic.

That is why the current reframe is right to make this an authoring-time orchestration layer over L2-style machinery instead of a pure scene-prompt pipeline. Narrative primitives should go into a pressure-and-memory engine, not directly into scene generation.

---

## 6. Overdetermination and DAYDREAMER*

Mueller's Chapter 11 is easy to underrate. It contains the forward-looking part.

He argues that single-cause thought is too weak. Many thoughts and daydream elements are multiply caused. A good stream element may satisfy several concerns at once. He then sketches DAYDREAMER*, a parallel split/merge architecture in which many processes run at once, split into alternatives, and merge when they discover shared substructure.

You do **not** need to build DAYDREAMER* literally.

You probably **do** want its scoring lesson.

For the current project, overdetermination is the best explanation for why some candidate moments feel rich and others feel flat. A strong candidate does more than one job:

- it serves more than one concern
- it carries memory charge from more than one prior episode
- it sets up and pays off multiple lines
- it registers several appraisal dimensions at once
- it can plausibly be compiled into stable graph residue

This maps cleanly onto `GeneratedNodeProvenance`:

- `source_concern_ids`
- `dominant_concern`
- `appraisal_signature`
- `emotion_signature`
- `practice_signature`
- `family`

A useful scoring principle is: reward candidate moments whose provenance shows genuine multi-source pressure, not just one loud concern.

In plain language, the engine should prefer moments that feel inevitable from several directions at once.

That is Mueller's overdetermination idea, modernized.

---

## 7. Appendix B: narration layer, not debug dump

Appendix B proves that Mueller was not only building an internal engine. He built a realization surface.

The generator tracks:

- concept type
- belief path or perspective
- tense, case, and mode switches
- context

And it does several things your current companion/dashboard layer still needs:

### Mode-sensitive language

Actual event, hypothetical future, alternative past, relaxed assumption, and remembered episode are phrased differently. This is crucial. The system should not narrate all generated material in one flat declarative voice.

### Belief-path sensitivity

Mueller tracks whose viewpoint the sentence belongs to. That matters for pronouns, but more importantly for nested social modeling. Current equivalent: character perspective selector plus explicit belief framing in narration.

### Emotion realization

Mueller renders emotion with direction, strength, and reason. That is far better than surfacing raw vectors to the user.

### Pruning

Not every internal assertion becomes output. Knowledge authors mark what should be narratable. That is the right lesson for the current system too. The companion view should be authored and selective, not a cognition dump.

### Structural boundaries

New paragraph on concern shift or backtracking. That is a small but important design decision. It makes switching legible.

### Memory narration

Reminding is rendered as recollection, not as ordinary present action.

For the current project, Appendix B should influence both the companion layer and the render prompts. A node tagged as rehearsal, reversal, memory, or counterfactual should not just look different in the graph. It should sound and read differently.

What is outdated:

- template-based English generation as the main realization engine

What still matters:

- mode tagging
- perspective control
- selective narration
- explicit distinction between actual, imagined, remembered, and counterfactual material

---

## What to port, what to modernize, what to drop

### Port almost unchanged

- concern-centered control
- typed daydreaming operators
- recursive reminding
- serendipity as validated cross-connection
- overdetermination as a quality criterion
- narration as designed realization surface

### Port, but modernize hard

- emotion handling: use appraisal-driven state, not bare labels
- episodic retrieval: embeddings plus coincidence thresholds
- social reasoning: lightweight practice/affordance model instead of huge hand-coded interpersonal rule books
- context handling: graph plus validation layer, not pure sprouted contexts
- evaluation: desirability/realism/similarity plus aesthetic filtering and provenance-based scoring

### Drop or demote

- hand-coded commonsense as the primary content source
- exact-match episodic retrieval by itself
- unconstrained free generation
- exhaustive export of raw cognition into the graph seam

---

## Minimal operational guidance for the current prototype

If you want the shortest path from this synthesis to implementation, it is:

1. Start from narrative primitives, not seed nodes.
2. Materialize `CharacterConcern` and `AppraisalFrame` explicitly.
3. Give every candidate moment a Mueller operator label.
4. Mix embedding retrieval with threshold-based reminding.
5. Score candidates for pressure fit, realism/plausibility, episodic resonance, and overdetermination.
6. Validate before graph admission.
7. Export only stable graph-readable residue.

That is the non-bullshit version of "Mueller for authoring-time generation."

He does **not** give you the modern content engine.

He **does** give you the missing middle layer between narrative primitives and graph-compilable candidate moments.

---

## Sources used

Primary Mueller sources:
- `01-introduction.md`
- `02-architecture-of-daydreamer.md`
- `03-emotions-and-daydreaming.md`
- `04-learning-through-daydreaming.md`
- `05-everyday-creativity-in-daydreaming.md`
- `06-daydreaming-in-the-interpersonal-domain.md`
- `07-implementation-of-daydreamer.md`
- `08-comparison-of-episodic-memory-schemes.md`
- `11-future-work-and-conclusions.md`
- `14-appendix-b-english-generation-for-daydreaming.md`

Bridge notes:
- `11-mueller-ch7-extraction.md`
- `12-mueller-appendix-b-extraction.md`
- `13-l2-refactor-synthesis.md`

Current project framing:
- `27-authoring-time-generation-reframe.md`
- `28-l2-schema-from-5pro.md`
- `2026-03-14_15-58-35_Claude_Chat_Pressure_engine_reframe_discussion.md` (selected slice)
