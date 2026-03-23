Yes. The current kernel has a real self-reinforcement loop.

Not hypothetical, not subtle. It already stores family-plan residues in a form that later family planning can retrieve and reuse. The tests explicitly prove that stored rationalization episodes can later reopen rationalization frames, stored reversal episodes can later reopen reversal causes, and both can feed later roving. So the question is not whether writeback can bias future cognition. It can. The real question is whether the current damping is enough to stop that from hardening into grooves.

It is not enough.

Mueller absolutely wanted prior daydreams to affect later daydreams and behavior. He gives examples where a prior reversal changes later behavior, and where one revenge daydream later assists another. He also treats rationalization and roving as ways of modulating negative emotion after failure. So reuse itself is not the sin. The problem is that your current kernel admits generated branch material into reusable episodic memory too early and with too little discipline. 

The core flaw is this:

`run-family-plan()` → `store-family-plan-episode()` currently collapses four different things into one act:

1. a branch was generated
2. the branch was structurally valid
3. the branch was affectively useful
4. the branch deserves durable future influence

Only 1 and 2 are actually known at write time. But the episode gets stored in a way that already supports 3 and 4.

That is where the groove risk comes from.

## What the current loop is actually doing

At write time, family-plan episodes get:

* retrieval indices, stored as both `:plan? true` and `:reminding? true`
* support indices, stored as neither
* rule-path entries, also stored as support-only
* provenance that marks them as `{:source :family-plan, :family ...}`

Then later:

* `rationalization-frame-candidates()` can retrieve stored rationalization family-plan episodes as fallback frames
* `reverse-undo-cause-candidates()` can retrieve stored reversal family-plan episodes as fallback causes
* `roving-plan()` can remind over these episodes once they are reachable from a seed and structurally connected
* `retrieve-episodes()` can add provenance bonus via shared rule, shared edge, or graph bridge depth, so retrieval is not just cue overlap, it is also family-structure overlap

So the system is already doing a weak form of “family episodes bootstrap later family episodes”.

That is close in spirit to Mueller’s claim that imagined episodes should later influence future external behavior and future daydreaming. He explicitly asks how daydreams are stored and later applied in future daydreaming, and gives examples of prior imagined material shaping later cognition. 

But Mueller also had explicit evaluation and selection pressure over retrieved episodes, including realism, desirability, and ordering criteria. Your kernel mostly does not. The episode-evaluation note says those metrics should shape later reuse, but the current implementation mostly stores fields without using them as a general gating or decay mechanism. 

## Where the dangerous grooves form

### 1. Rationalization groove

This is the biggest risk.

Stored rationalization family-plan episodes carry reframe facts as retrieval indices. Later rationalization candidate search queries episodic memory using current visible fact ids, with `:threshold-key :plan-threshold`, `:serendipity? true`, and `:active-rule-path [:goal-family/rationalization-plan-dispatch]`. That means three things are helping the old frame come back:

* direct overlap on visible fact ids
* lowered threshold from serendipity
* structural provenance bonus from shared rationalization rule path

That is a recipe for “this old explanation is easy to think with, therefore it returns, therefore it becomes easier still.”

Worse, rationalization is not just stored as content. It is stored as emotionally successful content. In Mueller, rationalization is supposed to reduce negative emotion after failure. Your current tests show that stored rationalization episodes can later reopen frames and can also feed later roving. So the system can end up preferring prior affect-soothing reinterpretations because they are retrievable, not because they remain true or useful. 

That is the confirmation groove: “I have already built a frame that makes this kind of failure feel better, so that frame becomes the easiest one to retrieve next time.”

### 2. Reversal counterfactual groove

Stored reversal family-plan episodes are less permissive, but still risky.

Later reversal cause search can retrieve stored reversal episodes when their retracted fact ids overlap the current visible indices. That is more anchored than rationalization, because it asks for overlap with the old weak-leaf objective facts. Good. But once an imagined counterfactual repair is stored, it can return later as a candidate cause even if the original explicit `:failure-cause` fact is gone.

That means an imagined “if only X” can become a reusable causal prior.

Mueller does want imaginary past variations to support learning, including reversal after failure. But he also says such daydreaming can either teach a better course of action or reinforce the original course. In other words, he already names the failure mode. 

Your kernel currently has the reinforce-the-original-course side, but not enough machinery to tell when that reinforcement is bad.

### 3. Cross-family laundering through roving

Roving is supposed to be diversion. In Mueller, it shifts attention from charged failure material to pleasant or loosely associated material. The book’s early example does exactly that. 

But your current system allows stored rationalization and reversal family-plan episodes to feed later roving. That means prior self-generated reinterpretations and counterfactuals can come back through a “pleasant reminder” channel.

This matters because it launders family residue into seeming spontaneity.

The system can experience something like:

* failure
* rationalization branch
* stored rationalization episode
* later roving reminder of that episode
* re-entry of the same frame under the guise of associative drift

That is not neutral reminding. That is affective recirculation.

### 4. Provenance ratchet

You were careful not to usage-weight the rule connection graph itself. Good. The consolidation note is right that graph structure should stay structurally derived, not traversal-strengthened. That preserves serendipity and avoids Hebbian rutting in the graph. 

But you still have a ratchet elsewhere: provenance bonus.

A stored episode that shares rule path, edge path, or a short bridge with the active family plan gets extra marks. So once family-plan episodes exist inside the family-rule topology, future structurally related searches get free lift.

That is not graph learning, but it is still retrieval privilege.

The effect is especially strong for same-family reuse. Shared-rule gives +4, shared-edge +5, and even graph-bridge paths add enough bonus to bring low-mark episodes over threshold. That is exactly why your roving tests can recover one-cue family-plan episodes if rule provenance lines up.

So support-only rule indices are not harmless. They are not counted as direct reminding cues, but they still amplify retrieval through provenance.

### 5. No anti-memory

There is no strong negative residue.

No “this rationalization backfired.”
No “this counterfactual kept reopening but never improved planning.”
No “this frame became contradicted by later canon.”

So every reused family-plan episode gets to act like a precedent, and very few get explicitly demoted for being bad precedents.

That is the other half of groove formation.

## Is `:retrieval-indices` versus `:support-indices` sufficient?

No.

Necessary, yes. Sufficient, no.

It is doing real work. If everything were retrieval/reminding-eligible, you’d have a total mess. The split at least prevents rule ids and support tags from directly becoming ordinary coincidence cues.

But it fails for three reasons.

### First, the retrieval indices are still the dangerous part

For rationalization, the retrieval indices are basically the reframe facts themselves. For reversal, they are counterfactual fact ids plus retracted leaf fact ids. Those are exactly the things future candidate searches look for.

So the stored residue is not merely metadata about a branch. It is content-level cue material on the same plane as world facts.

That is too permissive.

### Second, support indices still matter indirectly

Support indices do not count as direct marks, but rule-path and edge-path still fuel provenance bonus. So the memory substrate still “sees” structural family similarity and rewards it.

Meaning: support-only is not non-causal. It is just second-order causal.

### Third, the memory substrate has no cue typing

The separation is only enforced by write-time convention and caller discipline. `episodic_memory.clj` does not know that some indices are “world evidence”, some are “family residue”, some are “rule scaffolding”, some are “affective traces”.

Everything is just an index.

That means the substrate cannot enforce rules like:

* at least one cue must come from current world evidence
* family-internal cues alone cannot reopen a frame
* same-family provenance bonus is capped
* counterfactual cues cannot dominate explicit reality cues

Without typed cue namespaces, the split is too blunt.

## What discipline is missing

The revised architecture doc already says generation should be propose → validate → admit, and that a Critic should watch for clustering, flat emotional trajectory, or getting stuck in one goal type. That is the right shape. But the current family-plan writeback path has no comparable admission filter for reusable episodic residue.

Right now you validate branch coherence, then admit it straight into durable reusable episodic memory.

That is too early.

You need at least three layers, even if they share one underlying store.

### 1. Branch trace

Everything generated by a family plan can be stored here.

Purpose: auditability, replay, introspection, maybe same-session reminding.

No assumption yet that it should bias future planning.

### 2. Provisional reusable residue

This is where a rationalization frame or reversal counterfactual can live after one use.

Purpose: weak suggestion source.
It can be retrieved, but under stricter conditions.

Not yet a durable precedent.

### 3. Consolidated durable episode

Only after outcome evidence.

Purpose: strong reusable memory.
Can legitimately bias later planning and reminding.

That is the missing distinction.

Your D6 memo was pointing exactly in this direction for counterfactuals: keep rich hypothetical material lane-local, preserve only filtered residue broadly, do not dump raw hypothetical branches into the durable shared layer by default. That logic applies here too, even inside the kernel. Unfiltered family-plan writeback is the kernel-local version of “dumping raw hypothetical branches into memory.”

## What concrete rules I’d add

### A. Add admission status

Every family-plan episode should get something like:

* `:admission-status :trace`
* `:admission-status :provisional`
* `:admission-status :durable`

Default for rationalization and reversal should be `:provisional`, not durable.

Roving can maybe be a bit looser, but even there I would be careful, because roving is now acting as a laundering channel.

### B. Type the cues

Do not keep raw flat indices only. Add namespaces or typed cue records:

* `:world/fact`
* `:family/reframe`
* `:family/counterfactual`
* `:goal/id`
* `:rule/id`
* `:affect/state`
* `:selection/policy`

Then enforce retrieval policy at the substrate level, not only in callers.

Most important rule:

A stored rationalization or reversal episode should not reopen as a frame/cause unless at least one current cue is a `:world/*` cue.

Pure family-internal resonance should not be enough.

### C. Remove cheap rationalization resurrection

Right now rationalization fallback is more permissive than reversal because it uses serendipity lowering.

I would either:

* remove `:serendipity? true` for stored-rationalization fallback entirely, or
* only allow serendipity lowering when there are already at least two independent world cues

Otherwise rationalization becomes the easiest self-reinforcing family by construction.

### D. Cap same-family provenance bonus

Shared-rule and shared-edge bonus are fine in general, but same-family reuse should not get to cross threshold on provenance alone.

So:

* if `episode.provenance.family == active-family`
* cap provenance bonus to less than 1 threshold unit, or require one extra non-family cue

Otherwise “I was generated by rationalization before” becomes a retrieval accelerator for future rationalization. That is exactly the groove.

### E. Add post-hoc evaluation before promotion

Promotion from provisional to durable should depend on downstream evidence, not mere retrieval.

For rationalization, good promotion signals might be:

* later reduced negative emotion without later contradiction
* improved external behavior, or improved recovery/rehearsal selection
* cross-family usefulness, not just same-family recurrence

Bad signals:

* repeated reuse only inside rationalization
* later contradiction by canon or explicit facts
* affect relief with no behavioral improvement
* strong clustering around one frame-id

For reversal:

* did the counterfactual support a later successful repair?
* was it independently corroborated by explicit `failure-cause` style evidence?
* did it broaden search, or keep reopening the same weak leaf?

If no, keep provisional or demote.

### F. Add anti-residue

You need explicit negative precedent:

* `:backfired`
* `:stale`
* `:contradicted`
* `:self-soothing-only`
* `:same-family-loop`

Then retrieval can down-rank or exclude those.

Without anti-residue, retrieval history only ever says “this was once thinkable”, which is not enough.

### G. Consolidate aggressively

The consolidation note is right: accumulation without consolidation becomes sludge. The same logic applies here, except the most dangerous sludge is emotionally self-referential sludge. 

I’d add a consolidation job that:

* archives or quarantines family-plan episodes never reused usefully
* decays accessibility for episodes reused only within the same family
* promotes episodes that bridge families productively or improve external planning
* merges near-duplicate rationalization frames
* keeps the graph structural, but curates episode accessibility aggressively

That gives you anti-groove pressure without corrupting the connection graph itself.

## My blunt bottom line

The current kernel is already doing something like this:

“Generated because it fit the moment” ≈ “earned the right to bias future cognition.”

That equivalence is wrong.

It is the heart of the problem.

The retrieval/support split helps, but it only reduces the most obvious contamination channel. It does not solve the deeper issue that family-plan content is being written back as reusable epistemic residue before the system knows whether it is actually a good precedent.

So the right fix is not “stop self-reuse”.
The right fix is:

* keep self-reuse,
* make it typed,
* make it provisional first,
* require world-anchored cues for reentry,
* cap same-family structural bonus,
* and only promote branch residue to durable memory after downstream success.

If you do not add that discipline, you will get rationalization grooves. Not maybe. You will.

Look away for a second, relax your shoulders.
