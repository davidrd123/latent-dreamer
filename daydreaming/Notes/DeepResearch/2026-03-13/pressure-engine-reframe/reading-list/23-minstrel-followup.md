# MINSTREL — Follow-Up from Turner Book + Remixed

Source: Scott R. Turner, *The Creative Process: A Computer Model
of Storytelling and Creativity* (1994)

Source: Tearse, Wardrip-Fruin, Mateas, "Minstrel Remixed:
Procedurally Generating Stories"

Source files:

- `sources/text/CreativeProcess.txt`
- `sources/text/MINSTREL_Remixed.txt`

Prior note: `09-minstrel-extraction.md`

Purpose: capture what the full Turner book and the Minstrel
Remixed reconstruction add beyond the short paper. This note is
about the larger authoring architecture, the fuller TRAM ecology,
the integrated search-plus-adapt claim, and the evaluation /
robustness story. It is not a rehash of the basic TRAM loop already
covered in `09`.

---

## What the added sources contribute

`09-minstrel-extraction.md` pulled out the local creativity pattern:
paired transform-recall-adapt operators, imaginative memory, and a
novelty gate. The added sources show that this is only the inner
mechanism.

The full book adds the missing outer frame:

- MINSTREL is an **author-level storyteller**, not just a case-based
  improviser
- story generation is organized around an agenda of authorial goals
- the TRAM library is much larger, much more uneven, and much more
  diagnostic than the short paper suggests
- Turner evaluates not just outputs, but failure modes, heuristic
  usage, graceful degradation, and what breaks when creativity is
  removed

`Minstrel Remixed` adds a useful reconstruction layer:

- it makes the architecture explicit as **ALP layer over TRAM layer**
- it recasts stories as explicit graphs and partial templates
- it surfaces search policy as a variable, rather than treating
  random TRAM selection as essential
- it usefully re-demonstrates both the power and the obvious limits
  of the approach

So the delta beyond `09` is: MINSTREL is not mainly "three clever
TRAM examples." It is a broader L1-style authoring system whose
creativity operators sit inside a higher-level agenda, repair loop,
and assessment story.

---

## What's genuinely new beyond 09

### 1. The real control loop is author-level planning

The book is explicit that storytelling in MINSTREL is driven by
**author-level goals**, not by one monolithic plot planner and not by
TRAMs alone.

Turner identifies four classes of author-level goals:

1. thematic goals
2. drama goals
3. consistency goals
4. presentation goals

These are managed by an agenda-based planner with priorities,
subgoals, and re-queuing of failed goals. An author-level plan can:

- add scenes to the current story
- add new author-level goals
- repair damage created by earlier goals
- opportunistically retry failed work after the story state changes

This is a major addition beyond `09`. The short paper can read like
TRAMs are the system. The book makes clear that TRAMs are only the
creative problem-solving substrate used inside a larger authorial
control loop.

`Minstrel Remixed` makes the same split even clearer. It describes:

- **Story Producers** for filling underspecified subgraphs
- **Story Checkers** for finding missing support or inconsistencies
- **Story Enhancers** for adding suspense, tragedy, characterization,
  and other enrichments

That is close to an actionable L1 decomposition: generation,
checking/repair, and enrichment should be distinct control roles even
if they share representations.

### 2. The TRAM story is much richer than the three demo heuristics

The book's TRAM library has **twenty-four** heuristics, not just the
few famous examples from the paper. Turner groups them into rough
functional families:

- relaxation
- generalization
- substitution of a similar sub-part
- planning-knowledge heuristics
- other domain- or representation-specific moves

This matters because it changes the lesson. The useful pattern is not
"copy Generalize-Constraint, Similar-Outcomes, and Cross-Domain
Reminding." The useful pattern is: maintain a **heterogeneous library
of small, typed, local mutation heuristics** with different scopes and
failure profiles.

Two details here feel especially important:

1. many of the heavily used TRAMs are relatively mundane
   heuristics, not spectacular analogies
2. Turner treats planning knowledge itself as a source of creative
   mutation, not just analogy or generalization

That is a better fit for this repo than a romantic "creativity =
wild analogy" reading. In L1, many of the highest-value mutations may
be boring local repairs, focus shifts, role generalizations, and
constraint weakenings rather than dramatic cross-domain leaps.

### 3. The abandoned TRAMs are as useful as the successful ones

One of the most valuable additions in the book is the analysis of
TRAMs Turner implemented and then abandoned. These are strong design
warnings.

- `Ignore-Neighbors` failed because it made a large transformation
  without a matching adaptation story, so it retrieved unusable junk.
- `Switch-Focus` failed because a syntactic reframing did not open up
  substantively new memory; it often also fought the higher-level
  authorial focus.
- `Remove-Slot-Constraint` failed because random constraint removal
  discarded important structure as often as irrelevant structure.

This sharpens the lesson from `09`. It is not enough to "mutate a
problem." The mutation must:

- preserve the part of the problem that actually matters
- expose useful adjacent memory
- come with a believable way to adapt the returned material back

That is a strong L1 rule for this repo: avoid broad "rewrite the
idea" moves unless the system also knows what must be preserved and
how to repair the result.

### 4. Turner is really arguing for integrated search and adaptation

The book states the main theoretical claim more clearly than the
paper: the core creative process is **integrated search and
adaptation**.

That matters because it rejects two bad decompositions:

- search first, then hope some generic adaptation stage can repair
  whatever was found
- adapt first, then do retrieval against the already-mutated result
  with no memory of what changed

MINSTREL's answer is to bundle each problem transformation with its
own reverse adaptation. That is what keeps search tractable and keeps
the adaptation problem from exploding.

The book also adds a crucial limitation that `09` does not really
surface: at the **author level**, MINSTREL's plans are opaque Lisp
blocks. Because those author-level plans are not themselves
adaptable, MINSTREL's creativity at that level is restricted to
**nonadaptive creativity** such as recalling generalized author-level
plans that can be reused without modification.

This is one of the strongest L1 takeaways in the whole source set:
if higher-level authoring operators are opaque code blobs or opaque
prompt blobs, they can be reused but not creatively adapted. If we
want higher-level authoring creativity, the operator representation
itself must expose typed structure that can be transformed.

### 5. Search policy is not incidental

`Minstrel Remixed` makes explicit something only implicit in the older
material: **TRAM selection policy materially changes output quality**.

Original MINSTREL uses random, depth-limited application of TRAMs.
MR adds a graph-distance / penalty-based selector that tries to find
closer matches with fewer transformations. The point is not that
graph distance is the final answer. The point is that "which
transform sequence do we try?" is a first-class design question.

The practical consequence is straightforward:

- more transformations often means more novelty
- but also more drift and more nonsense
- so the system needs some way to rank or bias transforms rather than
  relying on undirected wandering

For this repo, that is a clean L1 implication: if we build a mutation
library, we should probably also build a cost / distance / penalty
story over it.

### 6. The evaluation story is broader and better than "did it tell a story?"

The book's evaluation chapters are more useful than I expected. Turner
argues that the right unit of evaluation is not raw story count, but
the number and type of **author-level goals solved**. He then uses the
system as a testbed:

- adding a new theme and seeing what generalizes
- measuring re-queued goals
- charting TRAM usage frequency
- examining nested TRAM use
- removing TRAMs and studying degradation
- cataloging common failure modes

The resulting picture is nuanced:

- creativity solves a minority of goals directly but often unlocks
  key decisions that many later goals depend on
- many failures are local rather than global
- performance often degrades gracefully when TRAMs are lost because
  goals are localized and alternate heuristics can substitute
- but some single heuristics are genuinely critical

`Minstrel Remixed` adds a contemporary confirmation of this shape:

- it reproduces classic examples
- it shows that better TRAM selection can reduce nonsense
- it still inherits obvious common-sense failures
- it demonstrates some transfer into a new domain, but with a low bar
  for acceptability

This is exactly the kind of evaluation posture L1 needs: not "did we
get one impressive output?" but "which operator classes carry the
system, where does drift enter, how local are failures, and what
happens when key operators are removed?"

---

## L1 implications for this repo

### 1. L1 probably needs explicit author-level goal classes

The strongest steal is not the full MINSTREL agenda machinery, but
the idea that authoring work decomposes into different goal classes
that should not all be flattened together.

Rough translation:

- thematic goals -> pressure / theme / experiential target
- drama goals -> local enrichment, support, contrast, foreshadowing,
  intensification
- consistency goals -> causal, social, and affective repair
- presentation goals -> packaging / ordering / expression

That suggests L1 should not be a single undifferentiated operator
pool. It should at least distinguish:

- proposal operators
- repair / checker operators
- enhancer operators

### 2. Represent authoring operators so they can be adapted

Turner's author-level limitation is directly relevant. If our
higher-level operators are only executable procedures or prompt
templates, we will have the same problem MINSTREL had with opaque
ALPs: reuse without much creative adaptation.

So if we want L1 creativity above the local mutation layer, operator
specs should preserve typed internal structure:

- target pattern
- preserved constraints
- relaxed constraints
- expected repair obligations
- adaptation hooks

### 3. Add repair as a first-class phase, not an afterthought

Consistency goals are not decorative in MINSTREL. They are the reason
thematic and dramatic moves do not simply leave wreckage behind.

This maps very cleanly to the pressure-engine work. L1 should expect
interesting proposals to create inconsistencies, and should maintain a
separate family of repair operators for:

- causal support
- social plausibility
- emotional follow-through
- missing setup / missing consequences

### 4. Prefer small typed mutations over broad rewrites

The full book reinforces that creativity works through many small,
controlled transformations. Broad unguided rewrites either fail to
adapt or destroy the important parts of the problem.

For this repo that argues for:

- local transforms
- explicit preservation rules
- ranked mutation costs
- selective chaining when single-step repair fails

not for one-shot wholesale regeneration of a large candidate.

### 5. Evaluate the operator ecology, not just the outputs

MINSTREL's evaluation chapters imply a better L1 scorecard:

- which goal classes are being solved
- which operators are frequently useful
- which operators are rare but critical
- where nonsense enters
- how often an operator can substitute for a missing one
- whether failures stay localized or cascade

That is higher signal than counting outputs or subjective "interesting
enough" judgments alone.

### 6. Keep MINSTREL on the L1 side

Nothing in the fuller sources changes the basic architectural
boundary from `09`. MINSTREL remains an **authoring-time precedent**,
not a runtime cognition model. The richer author-level planning story
makes it more useful for L1, not less.

The thing to borrow is authorial search, mutation, repair, and
assessment. The thing not to borrow is a claim that characters
themselves should think by running TRAM chains at runtime.

---

## What to take

1. The split between high-level author goals and low-level mutation
   heuristics.

2. A three-part L1 control shape: propose, check/repair, enhance.

3. A heterogeneous library of small, typed mutation operators rather
   than one generic "creative rewrite" move.

4. Explicit pairing of each transformation with its adaptation /
   repair logic.

5. Some notion of ranked or penalized transform selection, rather
   than random mutation order.

6. Evaluation by goal coverage, operator usage, nonsense rate, and
   graceful degradation under operator loss.

7. The warning that higher-level authoring plans must be structurally
   legible if we ever want them to be creatively transformed.

---

## What not to take

1. PATs as the main top-level contract. The repo does not want to be
   a parable generator organized around moral exempla.

2. Full symbolic story-graph machinery as a required first
   implementation. The representation lesson is "typed partial
   structure matters," not "rebuild MINSTREL's graph formalism."

3. Random TRAM search. MR is right that this is a poor default if we
   care about drift and nonsense.

4. Opaque author-level plans. Turner shows why this caps higher-level
   creativity.

5. MINSTREL's common-sense blindness. The classic failure — a knight
   gives raw meat to a princess after analogical transfer from a story
   about appeasing a troll (Turner's original) or a dragon (Minstrel
   Remixed's retelling) — is not a cute quirk; it is a direct warning
   about uncontrolled transfer.

6. The claim that the current MINSTREL setup is a general theory of
   storytelling. Even Turner is cautious about how far the particular
   goal classes and plans generalize.

7. Importing TRAM-style creativity into runtime character cognition.
   Keep it on the authoring side.

---

## Bottom line

The added sources upgrade MINSTREL from "useful mutation heuristic"
to "credible precedent for an L1 authoring architecture." The main
steal is not the old suicide examples. It is the combination of:

- explicit author-level goal classes
- many small creativity heuristics with repair-aware adaptation
- a checker / repair layer that protects coherence
- evaluation focused on operator ecology and graceful degradation

The main warning is equally important: broad transformations,
undirected search, opaque higher-level plans, and weak common-sense
constraints all produce nonsense very quickly.
