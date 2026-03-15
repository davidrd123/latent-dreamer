Here’s the blunt read.

## 1. MVP recommendation

**Do hard slot reservation first.**
Specifically: in `retrieve_episodes()`, keep the current exact-match scoring, but enforce:

* **1 authored backstory slot minimum**
* **at most 1 generated slot** in the top-2 retrieved set
* keep current score ordering *within each source bucket*

So the retrieval result becomes:

1. best authored match
2. best remaining match from either authored or generated

If no generated episode clears the threshold, fine. If no authored episode clears the threshold, then and only then let generated episodes fill both slots.

That is the cheapest fix that actually addresses the current bug instead of cosmetically nudging it.

### Why not the other two as first move?

**Source-weighted retrieval**
This helps, but it is squishier. You’ll end up tuning constants against a deterministic structural bias. The current bias is not “generated is a bit too strong.” It is “generated gets the same tags as the active step and also gets `max_recency + 1`.” That means same-score generated episodes beat backstory by construction on the next step. A soft weight is the wrong first response to a hard bias.

**Recency decay**
This is second-best, not first-best. It helps once you already have some source balancing, but by itself it still lets step `t-1` generated material dominate step `t` if the decay is shallow, and if the decay is steep enough to matter immediately, you’ve basically reinvented a crude slot rule with worse semantics.

So the order is:

1. **hard slot reservation**
2. **then** add a small source weight or generated-recency decay if needed

Not the other way around.

---

## 2. Why it works

Because the current self-priming problem is structural, not statistical.

In the live harness:

* `run_middle_sequence()` appends accepted generated episodes into `episode_pool` for later steps.
* `build_generated_episode()` copies the current dominant concern / target / situation / practice into `retrieval_tags`.
* that same function assigns `recency_rank = max_recency + 1`.
* `retrieve_episodes()` then sorts by `(-score, -recency_rank, fixture_order)`.

That means a generated episode with the same exact-match score as backstory **wins by tie-break automatically** on the next step. This is not an emergent cognitive effect. It is a retrieval policy bug.

You can see why Kai collapses so easily. The authored backstory in the fixture already has strong same-target/same-concern matches, but generated episodes inherit those same keys *plus* newest recency. So after a couple of accepted steps, the retrieval pool starts citing the system’s own most recent move back to itself.

That is exactly the failure the earlier near-term design note was warning about when it said accepted-only writeback plus one-hop reminding was okay, but immediate self-echo was not the goal. And Q3 already called generated-episode self-priming a dangerous scaling risk, not a subtle maybe.

Hard slot reservation fixes the actual pathology:

* authored backstory stays in the loop every step
* generated material can still matter
* operator scoring still sees recent accepted material through the generated slot
* but generated material cannot crowd authored memory out of existence

That is the right near-term compromise for this harness.

### Concrete code shape

In `retrieve_episodes()`:

1. score everything exactly as now
2. split `eligible` into:

   * `authored = episode_kind != "generated"`
   * `generated = episode_kind == "generated"`
3. sort both lists the same way you do now
4. select:

   * first authored if available
   * then best remaining item overall, respecting `max_generated_slots = 1` unless authored is unavailable

This is a tiny change. It does not require fixture schema changes. It is honest to the current benchmark because it removes a structural bias rather than inventing fake diversity.

---

## 3. Failure modes

### Failure mode A: authored backstory becomes stale and the reservation feels fake

Yes, this can happen. Tessa is the better test here than Kai. If step 4 genuinely ought to be shaped more by a recent generated aftermath beat than by an older authored memory, a rigid reservation can over-correct.

The fix is not to abandon reservation. The fix is to make it **conditional**:

* authored gets 1 reserved slot **only if** at least one authored episode clears threshold
* if authored scores are weak or absent, generated can take both slots

That keeps the bias correction honest.

### Failure mode B: you preserve backstory contact but still get narrow operator paths

Also yes. Retrieval balancing alone will not solve Q5/Q12 stationarity. It only fixes one anchor. If `practice_fit` and `appraisal_fit` are still static, you can keep authored memory in the set and still get repeated operator winners.

That does **not** mean the retrieval fix failed. It means it addressed the retrieval bug and exposed the next bug.

### Failure mode C: the single generated slot becomes a same-step self-summary channel

Possible. If the generated slot is always just “what I did one minute ago,” you still get echo, only weaker echo.

That’s where recency decay comes in as the second move:

* generated episodes get full recency at `age = 1`
* then decay fast over step distance
* authored recency stays fixed from fixture

But do this **after** slot reservation, not instead of it.

### Failure mode D: reminder path and main retrieval diverge weirdly

Right now `retrieve_one_hop_reminder()` is already more source-balanced than main retrieval. In `run_middle_sequence()`, the reminder excludes generated episodes from the reminder candidates. That means the main retrieval and reminder path are operating under different source policies.

That’s not catastrophic, but it is ugly. Once you patch main retrieval, the asymmetry becomes less bad.

---

## 4. Higher-octane design

If the MVP works but still leaves the pool too narrow, the right next thing is **Mueller-style bounded reminding**, not unconstrained “more retrieval.”

The kernel lineage is clear on this:

* retrieval is coincidence-counting over indices
* recent episodes are suppressed
* recent indices are tracked in a FIFO
* retrieving one episode activates its *other* indices
* that can recursively surface more episodes
* recursion is bounded by recency structures

The important point is this: **Mueller’s system is not a free-for-all associative explosion.**
It is a bounded cascade with recency suppression. That suppression is exactly what prevents pathological reactivation loops.

### Smallest useful v1 reminding cascade for this project

Keep the current flat exact-match retrieval as the main channel. Add a **single extra cascade lane** with these rules:

#### A. Separate source classes

Each episode gets:

* `episode_kind = authored | generated`

You already have this in practice.

#### B. Cascade can start from either source, but recursive expansion is authored-first

Rules:

1. main retrieval returns the source-balanced top-2 set
2. each retrieved episode contributes indices for a reminder pass
3. **generated episodes may seed reminding, but may not recursively expand further generated episodes**
4. authored episodes may recursively expand authored episodes
5. generated episodes can enter the cascade again only after a step-distance delay, e.g. `age >= 2`

This keeps recent generated material informative without letting it build a hall of mirrors.

#### C. Add explicit recent suppression, not just ranking

The kernel model has `recent-episode?` and bounded recent lists for a reason.
Bring over the principle, not the whole Lisp port:

* maintain `recent_episode_ids` for the last 2–3 accepted generated episodes
* disallow those episodes from reminder recursion
* maybe allow them in main retrieval only, never in cascade

That is the simplest way to stop recursive self-echo.

#### D. Use independent indices, not cloned tags only

The current Python writeback copies concern / target / situation / practice tags from the dominant step. That is too lossy and too self-confirming.

For the richer cascade, generated episodes need a few extra indices drawn from accepted output, such as:

* operator family
* payoff/setup refs
* option_effect bucket
* maybe a coarse place or object cue

Not because “more features” is cool, but because Mueller-style reminding depends on **different active cues converging**, not the same four tags being replayed.

If you keep cloning only the dominant concern/practice tuple, a cascade will just amplify the echo chamber faster.

### What this higher-octane design buys you

If done right, it does three things:

1. preserves contact with authored backstory
2. allows recent generated material to matter
3. lets authored memory re-surface through associative paths rather than only exact tag overlap

That is closer to the project’s actual memory lineage than the current flat top-2 sorter.

---

## 5. Connection to other questions

### Q5 / Q12: operator diversity and practice stationarity

This is directly coupled. `episodic_resonance` is part of operator scoring. If retrieval self-primes on same-practice generated episodes, then operator winners get stickier. Fixing retrieval is one of the cheapest ways to loosen that anchor before you add exploration policy.

### Q7: scene-state progression

Also coupled. If generated episodes keep dominating retrieval, later steps can look “progressive” only because they are paraphrasing the system’s own prior outputs. That is fake progression. Source-balanced retrieval helps distinguish real scene evolution from recursive self-reference.

### Q9 / Q13: intervention ordering

This answer should heavily influence those.
**Retrieval balancing should happen before operator exploration policy.**

Reason: if the pool is structurally self-echoing, then operator rotation is just painting over a retrieval bug. You want to know what the pool looks like after backstory contact is restored *before* you add explicit exploration pressure.

So the ordering is:

1. patch retrieval source balance
2. run Tessa batch
3. inspect whether pool width improves
4. only then decide whether operator fatigue / rotation is needed

---

## 6. What not to build yet

Do **not** build a full kernel memory port right now.

Not yet:

* no full `episodic_memory.clj` port
* no recursive reminding tree with arbitrary depth
* no learned source weights
* no embeddings-based reminder graph
* no “memory controller” subsystem
* no fancy bandit policy over retrieval sources

That is wrong for the current state of the project.

The current harness has a plain bug: accepted generated episodes inherit the active tags and get unbeatable recency on the next step. Fix that first with one hard rule. Then check whether Tessa still goes narrow.

If Tessa stays narrow after source balancing, *then* you have evidence that the next bottleneck is operator/practice stationarity rather than retrieval-source collapse.

## Bottom line

**MVP:** reserve 1 of 2 retrieval slots for authored backstory whenever authored material clears threshold.

That is the right first move because the current self-priming problem is caused by a deterministic tie-break bug, not a lack of retrieval sophistication.

**Higher octane:** add a bounded Mueller-style reminding cascade with explicit recent suppression and authored-first recursive expansion. Generated material may seed the cascade, but it must not recursively amplify itself.

That gets you closer to DAYDREAMER’s memory logic without rebuilding the whole kernel in the middle of a prototype.

And no, a soft score multiplier is not the best first fix. It is weaker than the bug.
