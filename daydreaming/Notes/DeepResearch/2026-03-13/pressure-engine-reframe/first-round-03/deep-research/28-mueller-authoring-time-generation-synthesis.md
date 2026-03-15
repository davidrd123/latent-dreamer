# Mueller for Authoring-Time Generation

Purpose: translate Mueller's *Daydreaming in Humans and Machines* into a concrete mechanism set for the authoring-time generation pipeline described in `27-authoring-time-generation-reframe.md`.

This is not a general DAYDREAMER summary.
It is a mechanism-first synthesis for one specific problem:

- start from authored narrative primitives
- generate candidate moments before curation
- keep the lane split from `11-settled-architecture.md`
- compile accepted material into the graph membrane defined in `21-graph-interface-contract.md`

The key correction is simple: Mueller is useful here **not** as a vibe source and **not** as a shipping traversal engine. He is useful as an authoring-time pressure engine that turns structured unresolved material into candidate scenes, counterfactuals, recalls, and reframings.

## Thesis

For this project, Mueller should be repurposed as a six-step authoring-time loop:

1. compile narrative primitives into typed concern seeds
2. appraise and prioritize those concerns
3. run operator families over them
4. retrieve and recombine episodes across passes
5. score outputs for realism, usefulness, and overdetermination
6. hand the ranked candidates to human curation, then compile only stable residue into the graph

That keeps the architecture honest:

- `L2`-style machinery does the motivated generation
- human curation decides what becomes authored material
- the shared graph stores stable annotations, not live runtime ontology
- `L3` remains traversal over curated graph material, not cognition

## 1. Narrative primitives -> concern extraction

### 1. What it is

Take authored primitives such as:

- characters
- contradictions
- relationships
- backstory episodes
- charged places
- unresolved situations
- world rules
- motifs and resonance notes

and compile them into **typed concern seeds**.

A concern seed is not yet a graph node. It is a structured statement that some character, in some situation, is under pressure about something.

Minimum shape:

```clojure
{:concern-id ...
 :owner-agent-id ...
 :target-agent-id ...
 :concern-kind ...
 :goal-object ...
 :emotion-vector ...
 :intensity ...
 :urgency ...
 :practice-tags [...]
 :origin-pressure-refs [...]
 :source-episode-refs [...]} 
```

This is the authoring-time answer to `27-authoring-time-generation-reframe.md`'s question about how to go from primitives to usable graph material. The engine should not start from pre-authored nodes. It should start from compiled pressures.

A practical mapping:

| Primitive | Compile into |
|---|---|
| blocked desire / contradiction | goal-centered concern |
| asymmetrical relationship | attachment / status / retaliation concern |
| charged place | practice tags + retrieval indices |
| backstory event | episode seed |
| world rule | validation constraint + operator gate |
| motif / resonance note | retrieval tags + realization guidance |

### 2. Where it lives in Mueller

- Ch. 2 §§2.2-2.5: personal goals, concerns, and emotion-driven control
- Ch. 3 §§3.2-3.4: emotion representation and daydreaming-goal activation
- Ch. 6 §§6.3-6.4: attitudes, relationships, and interpersonal goal structures
- Ch. 7 §7.3.4 and §7.4.7: theme rules and concern initiation
- Figure notes in `32-image-reviewed-chapter-2-architecture-figures.md`: concern competition and control loop

The sharp source-level point is that concern birth is **rule-driven**. Mueller's themes are not loose “pressure exists” heuristics. They are explicit activation rules from world conditions into top-level concerns.

### 3. What it buys the pipeline

- Candidate generation starts from actual pressure, not generic scene prompting.
- The engine can answer “why did this scene appear?” with a concrete trigger.
- Every accepted node can inherit `origin_pressure_refs[]` cheaply.
- The resulting graph has a better chance of being traversable because the material was born from unresolved structure, not prompt drift.

### 4. Minimum viable implementation now

Implement a tiny compiler from authored primitives to concern seeds.

For the narrow prototype from `27-authoring-time-generation-reframe.md`:

- one character
- three backstory episodes
- one active unresolved situation
- three operators
- simple tag-based retrieval first

Do this with **theme-like trigger rules**, not embeddings.

Example:

```clojure
IF  character wants X
AND blocker Y is present
AND situation S is active
THEN create concern seed
     {:concern-kind :anticipation
      :goal-object X
      :origin-pressure-refs [S Y]}
```

Then run a light appraisal pass on the concern seed to set:

- typed emotion
- intensity
- controllability / changeability
- practice tags

Use EMA/OCC/Versu imports here only as light interpreters around Mueller, not replacements for him.

### 5. What current project notes still miss

The current notes correctly say “start from narrative primitives,” but they still under-specify the **compilation step**. They talk about pressures and situations, but not yet about explicit theme-rule concern birth.

That is a real miss.

Without concern extraction rules, authoring-time generation collapses back into a prompt pipeline with better nouns.

---

## 2. Appraisal, typed emotion, and concern competition

### 1. What it is

Once concern seeds exist, the engine needs a control law for deciding what to work on next.

Mueller's answer is still the right one:

- appraise what the current state means for the character
- represent the result as typed emotional pressure
- select the most strongly motivated concern
- run one unit of generation on that concern
- update affect and continue

This is the part of Mueller that gives you rumination, fixation, switching, and re-entry instead of evenly distributed coverage.

### 2. Where it lives in Mueller

- Ch. 2 §2.4 and §2.5: emotion-driven planning and procedures
- Ch. 3 §§3.1-3.4: emotional responses, emotion representation, daydreaming goals
- Ch. 7 §7.4.1: top-level control loop, decay, and mode switching
- `33-image-reviewed-chapter-3-emotion-figures.md`: goal taxonomy and emotional feedback system
- `11-mueller-ch7-extraction.md`: explicit note on per-cycle decay and motivating vs non-motivating emotions

### 3. What it buys the pipeline

- The generator gets a real scheduling principle.
- It can dwell, worry, avoid, return, and get interrupted for a reason.
- Successive candidate moments are ordered by character pressure rather than by author convenience.
- It gives a clean place to use EMA/OCC imports without flattening Mueller.

### 4. Minimum viable implementation now

Use a small typed emotion vector around each concern:

```clojure
{:emotion-type :fear|:hope|:distress|:anger|:remorse|...
 :intensity 0.0-1.0
 :valence ...
 :controllability ...
 :changeability ...
 :surprise ...}
```

Recommended control loop:

1. appraise all active concern seeds
2. derive typed emotion + intensity
3. sum motivating pressures per concern
4. decay non-dominant residual pressures each cycle
5. select the concern with highest current motivation
6. run one operator step

Do not overbuild compound-emotion theory in v1.
An OCC-lite vocabulary is enough.
Do not hard-dispatch operators from appraisal either.
Appraisal biases choice. It does not replace operator selection.

### 5. What current project notes still miss

`13-l2-refactor-synthesis.md` gets the surrounding imports mostly right, but the current project notes still sand down two Mueller specifics that matter:

1. **per-cycle decay discipline**
2. **motivation as concern competition, not just static pressure weights**

If you skip those, you lose the distinctive mind-wandering / obsession rhythm.

---

## 3. Operator families as generation control

### 1. What it is

Mueller's daydreaming goals are the authoring-time generator's operator families.
They are not merely labels for output scenes.
They are the procedures that decide what kind of hypothetical work to do.

The useful set is:

- `REHEARSAL`
- `REVERSAL`
- `RECOVERY`
- `RATIONALIZATION`
- `ROVING`
- `REVENGE`
- `REPERCUSSIONS`

For authoring-time material supply, these are generation operators over concern seeds.

### 2. Where it lives in Mueller

- Ch. 3 §§3.3-3.7: daydreaming goals, rationalization, revenge, roving
- Ch. 4 §4.3: reversal, recovery, rehearsal, repercussions
- Ch. 6 §6.5: plausible planning, subgoal relaxation, inference inhibition
- Ch. 7 planning procedures: operator execution in context

### 3. What it buys the pipeline

This is where Mueller stops being generic retrieval and starts being useful.

Operator families produce **different kinds of candidate moments**:

| Operator | What it tends to produce |
|---|---|
| `REHEARSAL` | prospective, preparatory scenes |
| `REVERSAL` | counterfactual past scenes and “what should have happened instead” |
| `RECOVERY` | renewed future attempts after failure |
| `RATIONALIZATION` | reframe / consolation / hidden-blessing scenes |
| `ROVING` | diversionary side paths |
| `REVENGE` | retaliatory or displaced-pressure scenes |
| `REPERCUSSIONS` | consequence-expansion scenes |

That is almost exactly what the authoring-time pipeline needs: not generic continuations, but **functionally different scene candidates**.

### 4. Minimum viable implementation now

For the first prototype, do **three** operator families only.

Recommended trio:

- `REHEARSAL`
- `REVERSAL`
- `RATIONALIZATION`

Why these three:

- `REHEARSAL` gives direct next-step material
- `REVERSAL` gives counterfactual variety and missed-opportunity structure
- `RATIONALIZATION` gives interiority and emotional reframe

If the prototype needs more outward action than interior reframe, swap `RATIONALIZATION` for `RECOVERY`.

Also import one Versu idea here:

- operators are not globally available
- they are gated by lightweight `practice_tags` and roles

So the system should not ask “can I use reversal?” in the abstract.
It should ask “is reversal legible in this practice, for this role, in this situation?”

### 5. What current project notes still miss

The notes repeatedly say “operator-driven generation,” but they are still too soft about **which operators** and **what each one is for**.

That vagueness is expensive.

Without explicit family semantics, the generation pipeline will drift toward “scene prompting with memory attached.”

Mueller's operator families are the control backbone. Treat them that way.

---

## 4. Context branching and write-back discipline

### 1. What it is

Mueller's contexts are not just implementation plumbing.
They do two jobs:

- branch hypothetical alternatives
- retain abandoned alternatives for future use

And Mueller's concern split matters just as much:

- **personal-goal concerns** write into reality
- **daydreaming-goal concerns** explore isolated hypothetical contexts and write back only limited residue

For authoring-time generation, this distinction gives you a clean modern translation:

- generation branches are hypothetical
- they do not silently mutate canon
- only curated, admitted results become graph material

### 2. Where it lives in Mueller

- Ch. 2 §§2.3-2.5: multiple goal planning and procedures
- Ch. 4 §§4.1-4.2: planning trees, episodes, and learning from imagined scenarios
- Ch. 7 §7.4.2 and §7.4.6: context trees and personal-vs-daydreaming concern write-back
- `11-mueller-ch7-extraction.md`: explicit statement that personal and daydreaming concerns differ by how they write back

### 3. What it buys the pipeline

- Multiple incompatible candidate moments can coexist during authoring-time generation.
- The system can retain near-miss alternatives instead of discarding them.
- Human curation gets real options rather than a single generated path.
- The graph membrane stays clean because runtime branch state is not the same thing as accepted authored structure.

### 4. Minimum viable implementation now

Represent each authoring-time generation pass as an immutable branch context.

```clojure
{:context-id ...
 :parent-context-id ...
 :active-concern ...
 :assumptions ...
 :generated-candidates [...]
 :evaluation ...}
```

Policy:

- generation contexts never mutate canon directly
- accepted outputs are exported as **proposal records**
- only after human accept + validator admit does anything become graph material

Keep the graph contract strict.
Export only stable residue such as:

- `pressure_tags[]`
- `practice_tags[]`
- `contrast_tags[]`
- `origin_pressure_refs[]`
- `branch_outcome_tags[]`

Do **not** export:

- active concern refs
- reminded episode refs
- context trees
- full appraisal objects

### 5. What current project notes still miss

The current architecture docs know the graph is a membrane, but the implementation notes still occasionally flirt with leaking runtime ontology into it.

That is the wrong move.

The generation pipeline should be rich in live internal structure.
The graph should only receive stable residue after curation.

---

## 5. Episodic memory across passes

### 1. What it is

Mueller's memory system is not a passive archive.
It is an active store of reusable planning fragments that can be retrieved, reactivated, modified, and learned from.

For this pipeline, episodic memory should span **multiple authoring passes**, not just one run.

Three useful strata:

1. authored backstory episodes
2. generated candidate episodes
3. accepted / curated episodes

Each stratum serves different work, but all should be retrievable.

### 2. Where it lives in Mueller

- Ch. 4 §§4.1-4.2: episode representation, storage, analogical planning, intention
- Ch. 4 figures 4.1-4.8: planning trees, subepisodes, accessible vs inaccessible rules
- Ch. 5 §5.3.1: serendipity-based learning
- Ch. 7 §7.5: episode storage, retrieval, analogical rule application, subgoal creation
- Ch. 7 §7.5.3: reminding and recursive index expansion

### 3. What it buys the pipeline

- Later passes can build on earlier material instead of restarting from zero.
- Backstory and prior candidate moments can recur with real structure.
- Retrieval produces more than similarity search. It produces associative activation chains.
- Accepted graph material can feed future generation without collapsing the graph and the runtime into one object.

### 4. Minimum viable implementation now

Represent episodes as structured records, not just text chunks:

```clojure
{:episode-id ...
 :kind :authored|:generated|:accepted
 :operator-family ...
 :participants [...]
 :place ...
 :concern-tags [...]
 :emotion-summary ...
 :practice-tags [...]
 :indices [...]
 :source-refs [...]
 :payload ...}
```

Retrieval policy for v1:

- tag-based retrieval first
- embeddings second
- coincidence threshold over independent indices
- bounded reminding recursion

When an episode is retrieved:

1. reactivate its emotion summary
2. add its other indices to the active pool
3. permit one or two levels of recursive reminding
4. run serendipity checks against the current concern

Do **not** wait for a full memory architecture before doing this.
The minimum version is already useful.

### 5. What current project notes still miss

This is another sharp miss.

The notes already talk about retrieval, but they still underuse:

- recursive reminding
- emotion reactivation on retrieval
- episode reuse across authoring passes
- serendipity-based learning from accepted candidates

If retrieval remains “vector search + prompt stuffing,” you do not really have Mueller here.

---

## 6. Serendipity and mutation

### 1. What it is

Mueller's serendipity is not random inspiration.
It is a structural discovery procedure:

- find a path from an active concern to some otherwise inaccessible material
- verify that the path is actually usable
- then incorporate the result as a plan or episode

Mutation is subordinate.
It perturbs failed actions or scenarios, then uses serendipity recognition to see whether the perturbation is actually useful.

### 2. Where it lives in Mueller

- Ch. 5 §§5.3-5.5: serendipity, serendipity-based learning, action mutation, incubation/insight
- Figures 5.1-5.3: intersection search and path verification
- Ch. 7 §7.7: serendipity recognition and action mutation procedures
- `11-mueller-ch7-extraction.md`: exact trigger list and algorithm shape

### 3. What it buys the pipeline

- backstory fragments can become newly relevant in non-obvious ways
- distant place/object material can generate usable candidate moments
- the engine can escape local repetition without dissolving into noise
- “happy accidents” become reusable assets after they are found once

This is exactly the part the current material-supply problem needs.

### 4. Minimum viable implementation now

Do not start with a giant rule graph.
Start with a typed relation graph over:

- concern tags
- participants
- place tags
- object tags
- operator-family tags
- authored episode tags

Then run a shallow intersection search.

Pseudo-shape:

```clojure
(active-concern -> top-rule-like tags)
(candidate-episode/material -> bottom-rule-like tags)
(intersection search up to depth 3)
(path verification by slot/binding compatibility)
```

If the path verifies:

- surface the candidate
- attach surprise / novelty pressure
- store the result as a generated episode for later passes

Mutation for v1 should be simple and typed:

- swap actor
- swap place
- generalize object / relation
- invert social stance
- rebind to adjacent practice

Then rerun validation and relevance checks.

### 5. What current project notes still miss

The current notes still describe serendipity too much like “retrieval with a weird bonus.”
That is weaker than the source.

Mueller's serendipity is structural search plus verification.
Mutation is not temperature.
It is controlled perturbation whose value must still be proved.

---

## 7. Evaluation and overdetermination scoring

### 1. What it is

Mueller gives three explicit evaluation dimensions:

- **desirability**
- **realism**
- **similarity**

The current project also badly needs a fourth one from Ch. 11:

- **overdetermination**

That means: the best candidate moment should not just “work.”
It should serve multiple unresolved pressures at once.

### 2. Where it lives in Mueller

- Ch. 4 §4.4: evaluation metrics and selection rules
- Ch. 5 §5.1 and §5.4: creativity, realism, and mutation filtering
- Ch. 6 §6.5: plausible planning, subgoal relaxation, and inference inhibition
- Ch. 11 §11.3: overdetermination as multiply caused behavior
- `06-source-miss-scan/response.md`: explicit warning that overdetermination is underused in current notes

### 3. What it buys the pipeline

- Candidate ranking stops rewarding vivid but isolated scenes.
- Human curation sees which moments actually pay rent.
- The engine can prefer moments that advance more than one unresolved line.
- The resulting graph becomes denser in the good way: more setup/payoff reuse, less decorative sprawl.

### 4. Minimum viable implementation now

Use a visible scorecard for each candidate:

```clojure
{:plausibility ...
 :desirability ...
 :similarity-to-active-situation ...
 :overdetermination ...
 :leverage ...
 :notes ...}
```

Suggested ranking policy:

1. hard floor on plausibility / validity
2. hard floor on local relevance
3. then rank by

```text
base = plausibility * similarity * desirability
rank = base * (1 + weighted_overdetermination)
```

Where `weighted_overdetermination` is computed from how many active unresolved lines the candidate advances, weighted by their current pressure.

Keep it explicit.
Do not bury it in generic provenance.

### 5. What current project notes still miss

This is the biggest miss.

The notes talk about provenance and pressure tags, but not yet about **multi-pressure coverage as a selection criterion**.

Tracking that a node came from two pressures is not enough.
That fact has to affect ranking.

Otherwise the engine will keep producing singly motivated moments that look fine and compose badly.

---

## 8. Human curation and compilation into an annotated traversable graph

### 1. What it is

Mueller does not have a human curation stage because he is modeling a single mind.
This project does.

That means there must be a modern insertion point between hypothetical generation and graph admission:

- engine proposes
- human curates
- validator admits
- graph stores stable residue

Human curation is not a concession here.
It is the architecture.

### 2. Where it lives in Mueller

There is no one-to-one stage called “human curation” in Mueller.
The nearest source analogs are:

- Ch. 4 §§4.4-4.5: evaluation, selection, intention formation
- Ch. 7: separation of live processing state from stored episodes

The modern pipeline adds human curation **at the exact seam where Mueller would otherwise store an episode for future use**.

### 3. What it buys the pipeline

- Keeps `L1/L2/L3` boundaries intact
- Prevents live runtime ontology from contaminating the graph
- Lets the author keep or reject material based on world taste, not just local structural score
- Produces a graph that `L3` can traverse without depending on live concern stacks or context trees

### 4. Minimum viable implementation now

Use a strict `propose -> validate -> admit` flow.

Each candidate moment that survives curation should compile to graph-native fields such as:

- `situation_id`
- `delta_tension`
- `delta_energy`
- `pressure_tags[]`
- `origin_pressure_refs[]`
- `setup_refs[]`
- `payoff_refs[]`
- `practice_tags[]`
- `contrast_tags[]`
- `event_commit_potential?`
- provenance fields: `source_lane`, `scope`, `source_ref`, `revisability`

Mapping rule:

| Runtime structure | What survives into graph |
|---|---|
| live concern | `pressure_tags[]`, `origin_pressure_refs[]` |
| live appraisal | summary tags only |
| active practice / role | `practice_tags[]` |
| operator family | `branch_outcome_tags[]` / contrast cues |
| branch evaluation | tension/energy deltas, commit potential |
| episode provenance | `setup_refs[]`, `payoff_refs[]`, `source_ref` |

And again, do **not** put these in the graph:

- `active_concern_refs`
- `reminded_episode_refs`
- full appraisal objects
- context trees
- lookahead paths

### 5. What current project notes still miss

The notes are already correct that the graph is the membrane.
What they still need is a more explicit **compilation rubric** from Mueller-style runtime structure into graph-readable residue.

Right now that transform is still too implicit.

---

## Minimal prototype sequence

If you want the narrowest honest prototype that actually tests Mueller-at-authoring-time, build this:

1. **Primitive compiler**
   - one character
   - one unresolved situation
   - three authored backstory episodes
   - theme-rule concern extraction

2. **Concern scheduler**
   - OCC-lite emotion typing
   - EMA-lite appraisal bias
   - Mueller-style concern competition + decay

3. **Three operator families**
   - rehearsal
   - reversal
   - rationalization

4. **Episode store**
   - authored + generated + accepted episodes
   - tag retrieval + threshold retrieval
   - bounded recursive reminding

5. **Serendipity pass**
   - shallow typed relation-graph intersection search
   - path verification
   - controlled mutation as fallback

6. **Scoring + curation queue**
   - plausibility
   - desirability
   - similarity
   - overdetermination

7. **Graph compiler**
   - stable residue only
   - no live concern/runtime leakage

That is enough to test the actual claim:

> Mueller-shaped generation produces better candidate moments from narrative primitives than flat scene prompting does.

## Bottom line

The right reuse of Mueller here is narrow and concrete:

- **theme rules** to turn primitives into concern seeds
- **emotion-driven competition** to decide what to work on next
- **operator families** to control what kind of hypothetical material gets generated
- **episodic memory + reminding** to make successive passes accumulative
- **serendipity + mutation** to escape obvious local continuations
- **explicit evaluation + overdetermination** to rank moments that actually compose
- **human curation + graph compilation** to keep the membrane clean

The main thing the current notes still miss is not more reading.
It is sharper import of the strongest mechanisms they already have:

- concern birth must be rule-driven
- retrieval must be recursive and emotionally reactivating
- serendipity must be structural and verified
- overdetermination must change ranking, not just provenance
- graph compilation must export stable residue only

That is the mechanism set that makes Mueller useful for authoring-time generation instead of merely inspirational.
