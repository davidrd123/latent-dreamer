Blunt version: the kernel already has a better theory of obsession than most AI memory systems. It does not yet have a better theory of collaboration.

That is not a small gap. It is the product boundary.

## What persistent inner state should feel like from the human side

Not “it remembers my preferences.” More like:

**1. Return, not recap.**
When I come back, it should not dump a summary. It should resume with altered pressure. Some threads feel warmer, some cooler, some newly embarrassing, some newly plausible. “We left this unresolved, and meanwhile two adjacent things got entangled.”

**2. Unevenness.**
Everything should not be equally retrievable. The good part of your kernel is exactly this: `episodic_memory.clj` already treats memory as tiered, cued, suppressible, promotable, and loop-prone. That is the right phenomenology. Human inner life has hot spots, dead zones, taboos, stale stories, and material that only comes back under specific pressure.

**3. Accountable surprise.**
If it surfaces a strange connection, it should be able to say why now. Not chain-of-thought theater, just a structural path: “this came back because this claim, that scene, and this unresolved question converged.” Otherwise “serendipity” just reads as randomness with good prose.

**4. Relational memory, not just self-memory.**
The most important persistent state for a companion is often not inner. It is *between us*. Topics I welcome being pushed on; topics I want cooled; kinds of interruption I find useful; places where the system keeps misreading me; promises we made; questions we parked. Right now the kernel has endogenous state and world state. It barely has relational state$_{90%}$.

**5. State as operator, not adjective.**
“Guarded,” “curious,” “avoidant” are useless if they only color narration. Persistent state should change moves: whether it challenges, defers, analogizes, asks for evidence, rehearses, backs off, or reopens an abandoned branch.

**6. Joint unfinishedness.**
The feeling you want is not “the system has a soul.” It is “we have history.” The companion should carry unfinished business with me.

## What in the current kernel is genuinely load-bearing

I’d keep these.

**Concern competition.**
The single biggest good idea is the existence of one attention economy. `control.clj` choosing a most-motivated thing each cycle is the difference between cognition and tagged storage. If everything can be active at once, nothing has pressure.

**Branchable contexts.**
`context.clj` and the reversal sprouting machinery are real substrate, not ornament. A companion that cannot preserve incompatible possibilities is stuck with either amnesia or fake certainty. Counterfactual branching is useful far beyond fiction: drafts, decisions, social what-ifs, abandoned research paths.

**The membrane.**
The admission tiers, cue zones, same-family loop suppression, stale/backfired/contradicted flags, and promotion by later use in `episodic_memory.clj` are probably the best idea in the repo. Most AI memory stacks optimize retention. This one at least tries to model when memory should *not* get more authority. That is exactly what a long-lived daemon needs.

**Structural provenance.**
`rules.clj` keeping a structurally derived graph, separate from usage weighting, is load-bearing for novelty. If you usage-weight the graph itself, the system becomes a habit machine. Learn ordering on episodes, fine. Do not turn the substrate into reinforced grooves.

**Typed operators.**
Some notion of “what kind of thinking is happening” is necessary. The companion needs internal moves, not just retrieval plus generation.

## What looks inherited, not load-bearing

I would demote these.

**The family taxonomy as ontology.**
Roving, reversal, rationalization, rehearsal are useful *control labels*. I do not think they should become the companion’s main self-model$_{85%}$. They are too tied to Mueller’s fiction-facing, failure-facing psychology. Good debug surface, bad public ontology.

**Failure as the main source of concern.**
Mueller privileges failed goals plus negative emotion. That is too narrow for a collaborator. Real collaborative pressure also comes from curiosity, contradiction, promise, social debt, aesthetic pull, boredom, and “this feels important but I don’t know why.”

**Binary mode switching.**
Performance/daydreaming is fine for a staged character. A companion needs at least: listening, co-thinking, background incubation, repair, and drafting. Not five grand metaphysical modes. Just more than two.

**Episode as universal currency.**
This is a serious pressure point. Episodes are good for scenes, cases, rehearsals, remembered fragments. They are not the right atom for everything. A collaborator also needs first-class claims, questions, hypotheses, commitments, refusals, and contradictions. Your own knowledge-coupling notes push this hard: claims, not documents, are becoming the right structured unit, with provenance attached. 

**Prose residue writeback as default.**
`runtime_thought.clj` is clever and dangerous. Clever because it proves structural divergence. Dangerous because LLM-authored language can become self-fanfic. The safe default for companionship is often not “store another episode.” It is “write a typed delta”: confidence changed, contradiction noticed, commitment created, concern warmed, taboo flagged.

## The missing collaborative mechanisms

This is the actual gap. Not more family polish.

### 1. A claim/question/evidence layer

Right now the kernel is strongest at persistent preoccupation. A thinking partner also needs persistent *epistemic objects*. The discourse-graph notes are useful here: question/claim/evidence nodes with typed relations, provenance, and revision history are a much better basis for collaboration than episodes alone. The field is converging on granular, attributed assertions as the unit, and practical graph↔prose sync is still unsolved.  

Without this, the daemon can keep circling things, but it cannot cleanly say:

* this is the claim we now half-believe
* this is the question still open
* this is the evidence that moved it
* this is what now conflicts

### 2. Contradiction as a first-class concern source

Your own prompt on serendipity/concern economy points at this. The kernel mostly has goal-failure entry. It needs dissonance entry. “Something doesn’t fit” should create pressure without pretending it is just negative emotion from failed action.

This is where computational argumentation and belief revision belong. Not as academic garnish. As machinery.

### 3. Relational state

You need explicit state for:

* what the human wanted kept warm
* what they dismissed
* what kind of interruption they tolerate
* what the system overdoes
* where trust broke
* what was promised for later

This is the biggest missing mechanism$_{90%}$. A companion that lacks relational state will keep feeling like a clever internal monologue engine, not a partner.

### 4. Interaction repair

The current kernel has backtracking for plans. It does not have repair for conversation. That matters more. Most companion failures are not memory failures. They are grounding failures: wrong level, wrong timing, wrong interpretation, wrong initiative. Conversation analysis, common-ground tracking, and repair belong in the frame here.

### 5. Human uptake as promotion evidence

Your membrane currently promotes on later structural use/outcome. Good. Extend it. For a companion, the strongest evidence is often human uptake:

* the user followed the suggestion later
* copied the phrasing into a draft
* explicitly rejected it
* reopened it after delay
* marked it as “keep bothering me about this”

That is much better promotion signal than internal reuse alone.

### 6. Permission and ownership

A collaborator needs to distinguish:

* my private daemon speculation
* things safe to surface now
* things I think are yours
* things we have jointly ratified

Without ownership tags, persistence gets creepy fast.

## Three product identities you should not collapse

I think the repo is mixing these.

**A. Rumination daemon**
What keeps coming back? What gets avoided? What reenters under pressure?
The current kernel is already pretty good here.

**B. Epistemic collaborator**
What do we think, doubt, contest, owe, and revise together?
The current kernel is weak here.

**C. Counterfactual studio**
What branches remain live? What abandoned alternatives deserve reopening?
The current kernel is very good here, mostly because of contexts + reversal.

If you keep polishing A and C while telling yourself you’re building B, you will get a persuasive inner-life engine that still doesn’t help think with someone.

That is the category error to watch.

## Unusual uses that become possible once state is typed

Not memory. Not personalization. More interesting:

**Counterfactual continuity.**
A system that preserves abandoned project lines, rejected arguments, unrealized replies, and alternate plans, then reopens them when conditions change.

**Argument weather.**
A reading/research partner that tracks which claims are warming, cooling, undercut, or becoming entangled across months, instead of merely retrieving papers.

**Motif pressure.**
A creative partner that notices recurrence of image, rhythm, phrasing, or scene logic, and keeps a motif warm without forcing it into immediate output.

**Ritualized revisitation.**
A daemon that can hold “not now, but do not lose this,” then reintroduce it at the agreed intensity later.

**Relational calibration.**
A companion that learns not just facts about you, but how you want help under different postures: exploratory, overloaded, grieving, drafting, deciding.

## Adjacent traditions worth importing

Not to replace the kernel. To stop it from staying trapped inside character-architecture land.

**Discourse graphs, nanopublications, argumentation.**
These give you proper atoms for collaboration: questions, claims, evidence, support, opposition, provenance. The knowledge-coupling notes already show the convergence here.  

**Mixed-initiative sensemaking / CSCW.**
Chan, Kittur, Chang, and related systems matter because they treat the writing/reading workspace itself as cognitive substrate, not just prompt context. Same reason Tana and Graphologue matter. They are primitive, but they point toward edit loops where graph and prose co-shape each other.  

**Conversation analysis and grounding.**
This is missing almost entirely from the current framing. A companion is a participant in joint action, not just a thinking thing with memories.

**Predictive processing / active inference.**
Not because you need free-energy doctrine. Because it gives you better entry points for curiosity and dissonance: unresolved prediction, uncertainty, precision allocation, surprise.

**Attachment / defense / parts-based traditions.**
Use these as phenomenological design resources, not truth machines. They are good at naming recurring approach/avoid/reframe patterns that humans recognize instantly.

**IMS / conducting / embodied modulation.**
This one is easy to miss and worth keeping. The IMS notes frame gesture/haptics as an interface to AI, not just a controller. That matters because collaboration does not have to be fully propositional. A human may want to bias intensity, novelty, interruptibility, or revisit pressure without issuing instructions in words. 

## The cleanest reframe I can give you

Engineer the companion’s “inner life” as three interacting state systems:

* **Concern state**: what wants processing
* **Epistemic state**: what is believed, questioned, contradicted, evidenced
* **Relational state**: what is live between us

The current kernel has a strong start on the first, fragments of the second, and almost none of the third.

That means the next big move is probably not another family. It is giving the system objects worth collaborating over.

One sentence to pin over the repo:

**Do you want a system that keeps thinking on its own, or a system that helps us think together?**

Same substrate can support both. Not with the same state types.

Look away for ten seconds. Relax your shoulders.
