# Sabre - Centralized Narrative Planning Extraction

Source: Stephen G. Ware and Cory Siler, "Sabre: A Narrative
Planner Supporting Intention and Deep Theory of Mind" (AIIDE
2021)

Source file: `sources/text/Sabre.txt`

Purpose: Extract what Sabre contributes to the pressure-engine
discussion: a concrete centralized planner with author utility,
character utility, nested beliefs, observation-triggered belief
updates, and explicit tests for whether a character action is
locally explainable. The point is not to import Sabre wholesale.
The point is to isolate what is useful for authoring-time planning,
validation, and state-update discipline.

---

## What the paper actually describes

Sabre is a forward-chaining state-space narrative planner. It is
explicitly a centralized "puppetmaster" system: one omniscient
planner chooses the whole action sequence to improve author
utility, but every action must still make sense for the characters
who perform it.

The paper's concrete machinery is:

- nominal and numeric fluents
- arbitrarily nested `believes(c, l)` literals
- one utility function for the author and one for each character
- actions with:
  - preconditions
  - conditional effects
  - consenting characters
  - observation functions
- triggers that fire automatically when their preconditions hold

A plan counts as a solution only if:

1. the action sequence is executable
2. author utility improves
3. every action is explained for all consenting characters
4. the sequence is non-redundant

The explanation criterion is the heart of the paper. A character
action is only allowed if, from that character's believed state,
there exists some continuation beginning with that action that the
character would expect to improve their own utility. This is how
Sabre keeps centralized plotting from simply forcing puppets to
act against their own interests.

That explanation test is coupled to a strong belief model:

- beliefs can be wrong
- beliefs can be arbitrarily nested
- observation changes beliefs
- surprise actions repair beliefs when a character witnesses
  something they thought was impossible
- triggers can update beliefs and world facts automatically after
  an action

So Sabre is not "plot planning with some annotations." It is a
formal attempt to combine:

- author-level plot steering
- character-level intention
- nested social cognition
- automatic consequence propagation

The evaluation is modest but informative. The paper benchmarks
search performance on several narrative-planning domains and runs
ablations for "intention only" vs. "belief only." The claim is
not that Sabre beats every prior planner. The claim is that belief
and intention together rule out bad plans that either ablation
would still accept.

The paper is also candid about its limits:

- characters must commit to specific beliefs; there is no
  uncertainty model
- deep theory of mind is expensive
- domain authoring remains heavy
- believable action is still richer than utility maximization
  alone

## The stealable core

### 1. Character action should be checked from the character's believed state

This is Sabre's strongest steal.

Current docs often talk about plausibility, motivation, or whether
a move "feels like" the character. Sabre gives a sharper test:
does the move make sense from what the character thinks is true,
not only from what the author knows is true?

That is especially useful for:

- deception
- concealment
- mistaken assumptions
- staggered revelation
- false confidence
- betrayal and miscoordination

For the pressure engine, the immediate value is not a full
planner. It is an **explanation critic** over generated multi-step
material.

### 2. Consenting characters are a clean ownership model

Sabre distinguishes participants from responsible actors. A victim
can be affected by an action without being a consenting
character. A trade can require both buyer and seller to consent.
This is a better action model than a flat participant list.

That matters for our situation work because many social moves have
different ownership structures:

- accusation: one driver, one target
- confession: one owner, one witness
- flirtation: one initiator, optional reciprocation
- deal-making: multiple consenting parties
- ambush: one consenting actor, unwilling target

This is a small representation detail, but it is a strong steal
for typed situation semantics.

### 3. Observation is first-class, not an afterthought

Sabre does not assume that world change and belief change are the
same thing. Each action defines who observes it, and characters
update beliefs only when they observe it or when an explicit
belief effect says they do.

This is directly relevant to the current "what changed and who
knows it?" seam. A situation move should not just emit prose and a
vague state delta. It should also have a disciplined answer to:

- what changed in the shared world
- who observed it
- what each character now believes
- which updates are immediate and mandatory

### 4. Triggers are a concrete model of mandatory consequences

Triggers let Sabre encode things that must happen when conditions
hold: perception updates, derived consequences, and automatic
world reactions.

This is useful because the current docs still have haze around
automatic aftermath. Sabre shows a principled split:

- chosen actions advance time
- mandatory consequences fire automatically
- some of those consequences are belief updates

That is a good fit for local situation machinery even if we never
adopt Sabre's full planning model.

### 5. Deep theory of mind can be represented structurally, not hand-waved

Sabre's nested `believes(c1, believes(c2, ...))`
representation is expensive, but it is honest about what
deception and anticipation require. If a story beat depends on "A
knows that B thinks C is safe," that should exist somewhere as
structure, not only as prompt wording.

The steal here is not "track arbitrary depth everywhere." The
steal is that **belief depth should be explicit when it matters**.

### 6. Author utility and character utility can coexist, but only in the right lane

Sabre's most provocative move is to separate:

- what the author wants the story to accomplish
- what each character would rationally pursue

That split is useful for us only if it stays upstream. It is a
good pattern for:

- authoring-time search
- planning-time validation
- multi-situation lookahead
- coherence critics

It is not a good default runtime psychology model.

### 7. Non-redundancy is part of explanation

Sabre rejects plans that contain unnecessary actions even if they
still eventually improve utility. That matters because characters
who wander through obviously pointless detours do not read as
believable just because a distant payoff exists.

This is a useful reminder for the current stack: multi-step
material needs a notion of unnecessary motion, not only local
plausibility.

## What maps to our architecture

| Sabre concept | Best fit in our architecture | Why it matters |
|---|---|---|
| Author utility | Authoring-time search or curation objective | Best used offline to rank candidate multi-situation paths or pack fragments |
| Character utility | L2 concern/policy plausibility check | Should stay a critic signal, not become the whole character model |
| `beta(c, s)` believed state | Explicit character belief sidecar | Most relevant for deception, surprise, rumor, concealment, and negotiation |
| Consenting characters | Ownership / buy-in set for a move | Clarifies who must have a reason for a joint action to happen |
| Observation function | Per-character noticing model | Helps separate world change from who noticed or inferred it |
| Triggers | Situation-local automatic updates | Good fit for mandatory aftermath, sensing, and carry-forward belief effects |
| Explanation test | Proposal critic or planner constraint | Rejects author-convenient moves that characters could not justify |
| Forward search over actions | Optional authoring-time planner | Could support multi-situation coherence checks, not runtime control |

The cleanest fit is **not** the runtime Director. The current
docs are already right that the runtime Director is not a
world-authoring puppetmaster.

If Sabre belongs anywhere, it belongs in a narrow upstream role
such as:

- a multi-step coherence checker over authored/generated
  candidates
- a planning sidecar for small deception-heavy situation clusters
- a verifier for whether a proposed situation progression can be
  jointly explained from the characters' belief states

That is a very different role from "replace L2 with Sabre" or
"let L3 plan the story centrally."

## What's genuinely new vs current docs

### 1. A precise formal answer to "why did this character do that?"

Current docs already care about motivated behavior. Sabre
contributes a stricter formulation: judge an action from the
character's believed state, and require some believable
utility-improving continuation behind it.

That is more concrete than "it seems plausible."

### 2. A strong representation for agency in joint actions

The consenting-character model is better than a vague notion of
"characters involved in the situation." It forces us to ask who
actually needs a reason.

That sharpens the design of offers, bargains, betrayals, rescues,
and collaborative concealment.

### 3. A real answer to the aftermath problem

The current reframe has an open seam around typed world/situation
change, local state, and carry-forward residue. Sabre contributes
a real technical pattern:

- chosen action
- observation-dependent belief update
- mandatory trigger aftermath

That is much closer to an executable semantics than the current
prose descriptions.

### 4. A concrete reason to isolate belief-sensitive scenes

The current docs already suspect that not every situation needs
the same machinery. Sabre makes the case more sharply: deep
theory of mind is valuable, but expensive and domain-heavy. That
pushes us toward selective use in exactly the scenes where
deception, secrecy, anticipation, and misread intent matter.

### 5. A model of centralized planning that is actually coherent

The repo already has an instinctive warning against centralized
world authorship at runtime. Sabre is useful because it shows what
such a system would actually need to do to be principled. That
makes the rejection more informed. We are not rejecting a
strawman. We are rejecting a specific, serious architecture
because it is the wrong center of gravity for this project.

## What to take

1. **Add an explanation critic keyed to believed state.**
   Generated or selected multi-step moves should be checkable as
   character-sensible, not only author-sensible.

2. **Give situation moves explicit ownership via consenting
   characters.**
   A move should specify who needs a reason, who is merely
   affected, and who only observes.

3. **Separate action choice from automatic aftermath.**
   Keep a distinct layer for mandatory consequences, especially
   sensing and belief propagation.

4. **Make observation explicit where secrecy matters.**
   For deception-heavy fixtures, track who saw what and what they
   now believe.

5. **Use deeper belief modeling selectively.**
   Reserve structured theory-of-mind machinery for rumor,
   bluffing, concealment, negotiation, and betrayal rather than
   forcing it into every interaction.

6. **If we need planning, keep it authoring-time and narrow.**
   Use it as a planner/validator over bounded situation clusters or
   pack candidates, not as the live runtime mind.

## What not to take

1. **Do not turn the runtime Director into Sabre.**
   The current architecture is correct to keep runtime directing
   separate from omniscient world authorship.

2. **Do not reduce character interiority to utility maximization.**
   Sabre itself admits believable behavior is richer than utility.
   Our L2 concern/appraisal layer should stay richer than a single
   scalar objective.

3. **Do not impose full ADL-style domain authoring on the main
   pipeline.**
   Sabre's expressiveness comes with serious author burden. That
   is acceptable for a narrow verifier; it is not acceptable as
   the default content authoring surface.

4. **Do not assume characters always hold crisp, exhaustive
   beliefs.**
   Sabre forbids uncertainty. That is a useful simplification for
   search, but too rigid as a general model of social cognition.

5. **Do not make deep theory of mind the baseline cost of every
   scene.**
   Most scenes do not need arbitrarily nested beliefs. Spend that
   cost only where it materially changes what can happen.

6. **Do not let centralized plot search displace authored
   situation craft.**
   Sabre is strongest at coherence under explicit constraints. It
   is weak as an answer to texture, watchability, voice, and the
   felt grain of a conducted inner life.

## Bottom line

Sabre is the clearest example in the reading list of a serious
centralized narrative planner that tries to satisfy both author
goals and character rationality under wrong beliefs. That makes
it valuable mainly as a **bounded planning and verification
reference**.

The steal is not the puppetmaster. The steal is the surrounding
discipline:

- actions need owners
- belief change needs semantics
- automatic aftermath should be explicit
- multi-step character behavior should be checked from what the
  character thinks is true
