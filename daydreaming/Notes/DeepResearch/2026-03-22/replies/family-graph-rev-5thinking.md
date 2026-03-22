Bluntly: the kernel is in a good local shape, but it is not yet a safe long-run cognitive ecology.

It already has enough structure to prove provenance-sensitive retrieval and authored cross-family carryover. It does **not** yet have enough negative feedback to stop memory sludge, and it does **not** yet have enough graph heterogeneity for serendipity in Mueller’s sense. Right now it is closer to a well-instrumented family-transition lattice than a creative rule field.

## 1. Episodic accumulation / writeback

The good news first.

The writeback shape is more disciplined than it looks. `store-family-plan-episode` writes hot retrieval indices and cold support/provenance indices separately. Rule ids in `:rule-path` are stored with `{:plan? false :reminding? false}`, so you are **not** directly turning every traversed rule into a reminding cue. That avoids the dumbest self-licking ice cream cone. Recent-episode descendant exclusion and short recent FIFOs also block immediate recursive loops. And the connection graph itself is still structural, not Hebbian, which preserves the anti-groove property you wanted.

But the long-run failure mode is still there.

### Where the groove risk actually lives

It is mostly in four places.

First, **all successful family plans become retrievable forever**. There is no forgetting, no archival, no “this worked once but keeps being useless now,” no realism/desirability gating. Episode count only rises.

Second, **provenance bonus is strong enough to convert weak cue evidence into retrieval**. Your tests explicitly demonstrate one-cue retrieval succeeding because of shared rule or graph bridge bonus. That is fine as a proof of mechanism. It is dangerous as a steady-state policy. Structural lineage is being allowed to stand in for actual cue convergence.

Third, **bridge depth is currently rewarded, not discounted**. In `provenance-bonus-info`, a graph bridge gets bonus `provenance-bonus * depth`. That means a 2-hop bridge beats a 1-hop overlap, and deeper bridges can beat shallower ones. That is backwards unless you have evidence that longer structural chains are better memories. You do not. In a sparse graph this is tolerable. In a denser graph it becomes a false-recall amplifier.

Fourth, **roving writes back transient state as durable retrieval surface**. Roving stores `:active-indices` after reminding cascade. That means the system can reify a momentary attentional cloud into a new episode signature. That is exactly how echo chambers form: not by explicit repetition, but by turning temporary co-activation into future access scaffolding.

Rationalization and reversal have a related problem, but a different flavor. They write back reframe facts and counterfactual/retracted facts, then later reuse those stored family-plan episodes as candidate frames or causes. So once the system has found one decent rationalization or reversal move, it now has a standing tendency to rediscover that same family of reinterpretation. That is useful at first. Without pruning, it becomes ideology.

### My verdict

Groove risk: yes$_{85%}$.

Echo-chamber risk: yes$_{80%}$.

Immediate runaway self-reinforcement: not yet$_{60%}$, because the graph is still tiny, rule ids are cold, and recent exclusion blocks the worst local loops.

So the memory system is not broken. It is **under-damped**.

### What consolidation discipline fits this exact writeback shape

Not generic “add decay.” A specific discipline that matches your storage scheme.

Treat stored material as three classes:

**Hot cues**
Things stored with `{:plan? true}` or `{:reminding? true}`. These should decay fastest, especially roving’s `:active-indices`. These are attentional residues, not identity.

**Cold provenance**
Rule ids, edge paths, family/support tags. These should remain queryable almost indefinitely, but they should not promote retrieval on their own beyond a weak tie-break.

**Payload exemplars**
Rationalization frames and reversal counterfactuals. These should not just accumulate. They should compete within a small equivalence class like `(family, failed-goal, cue-signature)` or `(family, frame-goal-id)` and only the best few remain hot.

That implies a pruning rule of this form:

* archive, do not delete
* keep provenance forever or near-forever
* aggressively demote hot retrieval eligibility for episodes that are repeatedly retrieved but not selected
* aggressively demote provenance-bonus-only recalls that fail to lead to use
* cap hot exemplars per local family/problem cluster

In other words, the thing to consolidate is **accessibility**, not existence.

You already have the right latent slots for this because episodes already carry `:realism`, `:desirability`, provenance, and thresholds. The missing piece is that those fields do no work yet.

The exact bad pattern to avoid is this:

1. rationalization episode is stored
2. later retrieved partly because of bridge bonus
3. selected again because it is retrieved
4. written back again with overlapping cues
5. family acquires a stylistic groove that feels like “character consistency” but is actually retrieval inertia

That is the failure you should assume unless you add evaluative demotion.

## 2. Is the 15-rule graph rich enough for serendipity?

No.

It is too sparse and too regular$_{90%}$.

Right now the graph is mostly this:

trigger → activation → plan-ready/request → dispatch
plus three authored cross-family bridges.

That is not “finding a neglected path in a structural substrate.” That is “walking a tiny explicitly-laid corridor.”

The current graph is useful for:

* provenance tracking
* testing bridge-path plumbing
* proving that stored family episodes can bias later selection
* showing that cross-family handoffs are structurally representable

It is **not** yet useful for meaningful serendipity.

### Why not

Because nearly all interesting paths are variants of the same macro-pattern, and nearly all edges are carried by a handful of generic fact types:

* `:goal-family-trigger`
* `:family-plan-ready`
* `:family-plan-request`
* `:family-affect-state`

That gives you connectivity, but not differentiated semantics.

The graph has low topological entropy. It is regular enough that most reachable paths are predictable from family design alone.

Said more harshly: your current “serendipity” is still mostly authored bridge traversal.

### The deeper issue

`rules.clj` connects rules whenever fact type matches and constant fields do not conflict. With a tiny registry, that is fine. With a bigger registry, generic fact types like `:family-affect-state` and `:goal-family-trigger` will overconnect fast.

So you are sitting between two bad states:

* now: too sparse for real serendipity
* later, if you add lots more rules naively: graph soup

That means the missing rules cannot just be “more family rules.” They need to add **new semantic phases** and **more specific typed intermediates**.

### What kinds of links are missing

The graph needs cross-phase heterogeneity. Right now it mostly has concern/affect/planning.

You need at least four additional kinds of typed nodes/rules if you want non-obvious paths:

**1. Evaluation facts**
Completed family plan → explicit evaluation facts like realism, desirability, similarity, keep/discard, caution, archive. This is the missing bridge from planning to future memory policy. The notes already point at this as central. Mueller also used evaluation during production, after production, and during later application of retrieved episodes. 

**2. Retrieval/reminding facts**
Right now reminding is procedural. If retrieval events never become graph-visible facts, other rules cannot reason over “this memory surfaced now” except through helper code. That blocks serendipity from operating over memory events themselves.

**3. Critic / selector facts**
You say you want Critic-Selector behavior in the broader architecture, with signals like clustering, flat emotional trajectory, or stuck goal type. Those need to exist as typed products, not just control-flow comments, if they are going to participate in graph search later.  episodes 

So the first pilot should be a judge, not an author.

Concretely, the narrowest good pilot is:

**after a rationalization or reversal family plan is produced, ask an LLM evaluator for realism / desirability / target-fit and a keep-hot vs archive recommendation**

That gives you three benefits at once:

1. it exercises `:llm-backed` inside RuleV1
2. it creates the missing evaluative metadata your memory system needs
3. it avoids granting the LLM first-class control over branching, backtracking, or state mutation

Only after that would I consider an LLM at:

* path usefulness ranking in serendipity search
* soft verification assistance
* hidden-blessing style content generation

The notes on serendipity already frame the good hybrid cut as evaluative, not replacing graph traversal. 

## Bottom line

My answer to your three questions is:

**(1)** The writeback shape is locally sane but globally unstable without consolidation. The main risk is not immediate runaway loops, it is provenance-assisted groove formation and rationalization fossilization.

**(2)** The 15-rule graph is not yet a real serendipity substrate. It is a tidy family-transition scaffold. You need evaluation, retrieval/reminding, critic/selector, and event/consequence links before non-obvious paths become likely.

**(3)** `:clojure-fn` is the right next step, starting with roving, but only if you admit an explicit effect contract. The first `:llm-backed` pilot should be episode evaluation, not generative plan production.

Priority order, if you want the shortest path to something that actually improves the kernel:

1. add evaluative pressure to stored episodes
2. stop bridge depth from acting like free magic
3. make memory/retrieval/evaluation events graph-visible
4. then move roving to `:clojure-fn`

Look away from the screen for a minute. Unclench your jaw and drop your shoulders.
