Here’s the blunt read.

You currently have a good **proof of structural provenance**, a mediocre **long-run memory policy**, and a graph that proves **plumbing**, not yet **serendipity**.

## 1) Episodic accumulation/writeback

**Verdict:** short-run sound$*{70%}$, long-run groove-prone$*{85%}$.

What is solid:

* `episodic_memory.clj` has real brakes. Thresholded coincidence retrieval, recent-episode exclusion, descendant exclusion, and bounded FIFOs mean this does **not** look like an infinite-loop machine.
* `goal_families.clj` reuses stored rationalization and reversal episodes conservatively. Explicit frames/causes outrank stored episodes in `rationalization-frame-candidates` and `reverse-undo-cause-candidates`, which is the main thing stopping memory from steamrolling present evidence.
* The tests already prove the intended reuse paths, especially the cross-family ones.

What is not solid:

* `store-family-plan-episode` writes **retrieval indices**, **support indices**, and **rule IDs** into the same `:indices` set.
* `episode-reminding` then pushes **the whole episode index set** into `:recent-indices`.
* So support/provenance tags are not passive metadata. They become active reminding cues.

That creates a real echo-chamber path:

* a roving family-plan episode stores tags like `:family/roving`, `:pleasant_episode_seed`, `:goal-family/roving-plan-request`, `:goal-family/roving-plan-dispatch`
* another roving family-plan episode shares several of those tags
* the second episode can then hit threshold from **structural overlap alone**, before content overlap does much work
* on top of that, `retrieve-episodes` adds a shared-rule provenance bonus

So the same information is effectively counted twice:

1. as cue overlap through stored rule/support indices
2. as provenance bonus through shared `:rule-path` / graph bridge

That is the main groove source.

There is a second, nastier nit:

* `:indices` is a **set**
* `episode-reminding` reduces over that set into a FIFO of max length 6

So once an episode has more than six indices, which cues survive into `:recent-indices` is semantically arbitrary. In practice that means metadata tags can crowd out content cues, or vice versa, depending on set iteration order. That is brittle.

So the failure mode is **not** “runaway recursion.” The recursion is bounded.
The failure mode is **memory ecology**: same-family and same-path material gets preferentially re-primed because administrative tags have become live cues.

### What consolidation/pruning discipline fits this exact shape

Not graph-edge learning. Episode accessibility discipline.

The right discipline for *this* writeback shape is:

**1. Three zones, hard separated**

* **Content cues**: retrieval/reminding eligible
* **Structural provenance**: searchable, explainable, never a reminding cue
* **Support/admin tags**: analytics/trace only

Your code already *conceptually* distinguishes these. It just does not enforce the distinction operationally, because everything lands in `:indices`.

**2. Cluster by rule-path + selection-policy + core cue set**
Family-plan episodes are formulaic. Many of them will be near-duplicates at the level that matters for retrieval. Keep a small hot set per cluster, archive the rest. Do not let every roving or rationalization episode remain equally live forever.

**3. Decay generated episodes by usefulness, not age alone**
You already have the right hooks:

* `:provenance`
* `:selection`
* `:rule-path`
* later reuse in rationalization/reversal candidate selection

Track:

* retrieved count
* chosen count
* downstream success count

Episodes with high retrieve / low choose ratios should lose reminding eligibility first. That is the exact sludge pattern your current shape will produce.

**4. Decay rates by source**

* explicit authored frames/causes: slow decay or none
* family-plan episodes: medium/fast decay
* runtime-thought residue episodes: fastest decay

That last one matters because `runtime_thought.clj` uses the same broad writeback pattern, so once you enable it heavily, the saturation problem gets worse.

## 2) The 15-rule family graph

**Verdict:** too sparse$_{90%}$ for meaningful serendipity.

Right now the graph is basically three short pipelines plus a couple of hand-authored affect bridges:

* roving trigger → activation → plan-request → plan-dispatch
* rationalization trigger → activation → plan-request → plan-dispatch
* reversal trigger → activation → plan-request → plan-dispatch

Bridges:

* rationalization plan-dispatch → afterglow-to-roving → roving activation
* reversal plan-dispatch → aftershock-to-roving → roving activation
* reversal plan-dispatch → aftershock-to-rationalization → rationalization activation

That is enough to prove:

* graph construction works
* provenance can matter for retrieval
* cross-family bridges can exist

The tests show exactly that.

It is **not** enough for “serendipity” in the Mueller sense.

Why not:

**1. The topology is regular**
This is mostly a small DAG of wrapper facts:

* `:goal-family-trigger`
* `:family-plan-ready`
* `:family-plan-request`
* `:family-affect-state`

That is execution choreography. It is not rich cognitive content.

**2. The graph is missing whole phases**
There is no graphable layer for:

* retrieval requests/results
* episode evaluation
* success/failure aftermath
* concern initiation
* surprise / serendipity verification
* goal deactivation

So paths do not cross phases. They mostly continue the same phase.

**3. Roving is mostly a sink**
It receives cross-family traffic, but graph-side it does not send much back out. The actual return path is through episodic memory side effects, not through explicit rule connections.

**4. Edge semantics are thin**
Most family edges collapse to `:state-transition`. So even when you explain a path, the explanation is structurally correct but cognitively flat.

### What is missing before non-obvious paths become real

Not “more rules” in the abstract. More **cross-phase heterogeneity**.

You need at least these additional rule classes:

**A. Retrieval/result rules**
Make memory graphable:

* active concern/state → retrieval cue fact
* retrieval cue fact → retrieved-episode fact
* retrieved-episode fact → analogical support / reminder / candidate plan fact

Right now memory is a side channel with provenance bonuses. Until retrieval itself is in the graph, serendipity is half blind.

**B. Evaluation rules**
Plan-dispatch should not be terminal. You need:

* completed episode/plan → evaluation fact
* evaluation fact → memory promotion / suppression / goal deactivation / later applicability

This is the cleanest missing phase.

**C. Outcome and aftermath rules**
You need explicit edges for:

* success
* failure
* unresolved continuation
* blocked repair
* reopened threat
* reduced negative emotion

Otherwise dispatch nodes dead-end.

**D. The missing families**
The supported-family set includes `:recovery` and `:rehearsal`, but the graph does not. Those matter because they add future-oriented and repair-oriented structure. `:repercussions` would help too. Without them, the graph cannot make many non-obvious turns.

**E. Family-agnostic domain facts**
This is the big one.

Non-obvious paths will not come from more family wrappers. They come from shared domain-level products like:

* `:imagined-situation`
* `:emotion-shift`
* `:counterfactual-cause`
* `:repair-step`
* `:concern-trigger`
* `:episode-evaluation`

If rationalization, rehearsal, and recovery all emit some of the same fact types, then you start getting actual bridge structure instead of hand-authored family handoffs.

So: the current graph is a good **migration scaffold**. It is not yet a creative substrate.

## 3) `:instantiate` now, `:clojure-fn` next, and the first `:llm-backed` pilot

**Verdict:** the plan-dispatch seam supports `:clojure-fn` reasonably cleanly$_{75%}$, but the runtime contract is not wired yet.

What is already clean:

* `RuleV1` already declares `:clojure-fn` and `:llm-backed`
* the graphable seam is there
* plan-ready → plan-request → plan-dispatch is already a bounded handoff
* provenance plumbing is already in place
* family plan bodies already look like executor bodies in practice: they take a bounded payload, mutate world state, and return a structured result map

So architecturally, yes: this is the right direction.

What is not clean yet:

* `rules.clj` only actually implements `instantiate-rule`
* `RuleResultV1` is a **fact-emission** contract
* the current family plan bodies are **world-transforming** procedures returning ad hoc maps

That mismatch matters.

Right now, `:clojure-fn` is a declared executor kind, not a real shared runtime contract. If you flipped a plan-dispatch rule to `:clojure-fn` today, the engine still would not know how to call it generically and reconcile its world mutation + result envelope.

So the honest read is:

* **yes** at the plan-dispatch seam
* **no** as a generic executor substrate, yet

Also, there is still duplication: `goal_family_rules.clj` is the graph registry, while `goal_families.clj` still owns the real execution path. That is workable for now, but it is not a clean single source of truth.

### Which `:clojure-fn` should go first

`roving-plan-dispatch`.

It is the simplest:

* one sprout
* one seed episode
* one reminding cascade
* minimal world surgery

`rationalization-plan-dispatch` is next.
`reversal-plan-dispatch` is last, because it does the most structural damage: copy, pseudo-sprout, rebind, retract, multi-branch.

### Which narrow `:llm-backed` pilot should go first

**Episode evaluation.**
Not path search. Not plan generation. Not frame selection.

More specifically: **a post-production evaluator on newly stored family-plan episodes**.

Why this first:

* bounded input
* bounded output
* low blast radius
* clean fallback
* directly addresses the current retention hole
* adds the missing evaluation phase to the graph

This should emit explicit evaluation metadata for a just-produced episode:

* realism
* desirability
* maybe one coarse `keep-live?` / `archive-only?` judgment

If you want the narrowest possible cut, start with:

* `keep-live?`
* score
* reason

Then later expand to the full realism/desirability/similarity trio.

Why this beats other candidates:

* **better than plan generation**: does not put an opaque model inside the bookkeeping loop
* **better than path usefulness**: your graph is too sparse, so path ranking is premature
* **better than rationalization/reversal candidate selection**: those are execution-critical and currently conservative in a good way
* **better than roving seed choice**: current issue is retention and echo, not lack of expressive seed choice

And it has a Mueller-faithful fallback:

* neutral score
* or skip evaluation entirely and keep current behavior

That is the right first `:llm-backed` move.

## Bottom line

1. **Memory**: bounded, but structurally self-reinforcing because metadata indices have become live cues.
2. **Graph**: good enough to prove provenance-sensitive reuse, not good enough for real serendipity.
3. **Migration**: `:clojure-fn` is ready at the plan-dispatch seam; the first `:llm-backed` rule should be post-production episode evaluation.

The single sharpest issue in the current implementation is this:

> rule-path/support metadata is stored as ordinary episode indices, and ordinary episode indices are what drive reminding.

That is where the groove lives.
