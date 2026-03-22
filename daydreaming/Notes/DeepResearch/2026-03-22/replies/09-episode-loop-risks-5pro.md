Yes. The loop is real.

The current kernel is storing outputs of its own coping families as future inputs to those same coping families. That creates a positive-feedback controller with weak outside grounding.

My read:

* rationalization groove risk$_{85%}$
* reversal groove risk$_{70%}$
* support/retrieval split being sufficient$_{5%}$

### What is happening

`run-family-plan()` writes family-plan episodes through `store-family-plan-episode()`. Later:

* `rationalization-frame-candidates()` can retrieve old `:family :rationalization` family-plan episodes and use them as fallback frames.
* `reverse-undo-cause-candidates()` can retrieve old `:family :reversal` family-plan episodes and use them as fallback causes.
* `roving-plan()` can pull those same episodes back in through `episode-reminding()`.
* `retrieve-episodes()` can boost weak matches with provenance bonuses.

So a rationalization can become evidence for later rationalization. A reversal can become evidence for later reversal. That is confirmation-by-writeback.

The tests make this explicit, not hypothetical:

* stored rationalization episodes can reopen a frame later
* stored reversal episodes can reopen a counterfactual cause later
* stored rationalization/reversal episodes can feed later roving
* stored roving family-plan episodes are retrievable by structure alone

So the behavior is designed in, not an accident.

### Where the self-reinforcement comes from

There are four separate mechanisms reinforcing each other.

**1. Family-plan episodes are born easy to retrieve.**
`family-plan-retrieval-indices` truncates to at most 3 distinct cues. That means family-plan episodes have small thresholds. Then `retrieve-episodes()` adds provenance bonus on top. So one real cue plus structural overlap can be enough.

In other words:

[
\text{effective marks} = \text{raw cue hits} + \text{provenance bonus}
]

and family-plan thresholds are often only 2 or 3.

**2. Rationalization gets an extra discount.**
`rationalization-frame-candidates()` uses `:serendipity? true`, which lowers threshold by 1. That makes the most epistemically dangerous family also the easiest one to resurrect.

That is backwards.

**3. Rule/provenance is helping twice.**
`store-family-plan-episode()` stores rule-path ids as ordinary episode indices, and `retrieve-episodes()` also separately grants provenance bonus from `:rule-path`/`:edge-path`.

So rule structure is getting counted:

* once as index hits
* again as provenance bonus

That is double counting.

**4. Reminding reactivates all indices, not just reminding cues.**
This is the substrate bug.

The `:retrieval-indices` vs `:support-indices` split is only used when writing thresholds. After storage, both end up in the same flat `:indices` set and the same global `:episode-index`. Then `episode-reminding()` reactivates **all** episode indices.

So support is not retrieval-inert. It is only threshold-neutral.

That means support cues and rule ids leak into the reminding ecology.

### One nit that matters

Support leakage is **not** the main reason same-family rationalization/reversal reopen today.

Those same-family lookups query visible fact ids, not support ids. So the reopen is already possible even if support cues were perfectly separated. The direct culprit there is:

* payload-derived content cues
* low thresholds
* provenance bonus
* and for rationalization, serendipity lowering

Support leakage makes the broader system worse by letting family-plan residue spread through reminding and cross-family reuse. It is the multiplier, not the first cause.

### Why this is worse for rationalization than reversal

`REVERSAL` is at least trying to learn a repair case. It can still groove into a false causal story, but there is a legitimate learning use there.

`RATIONALIZATION` is more dangerous because its job is affect regulation. If you let a soothing reframe become durable planner input with no promotion gate, the system learns whichever story once reduced tension.

That is not memory. That is self-soothing sediment.

### Is the current retrieval/support split sufficient?

No.

It is necessary bookkeeping, but it is not a real membrane.

Right now it does **not** give you:

* typed cue namespaces
* reminding restricted to reminding cues
* support-only metadata
* provenance-only metadata
* protection against rule-id leakage
* protection against same-family self-reinforcement

So the split fails at the one thing you actually need: keeping operational retrieval separate from descriptive residue.

### What discipline you need

You need both **admission** and **consolidation**.

#### 1. Admission: not every family-plan episode should become global episodic memory

Default state for family-plan episodes should be:

* `:source-kind :imagined-family-plan`
* `:admission-state :ephemeral`

Ephemeral means:

* usable for local/session introspection
* not eligible for same-family planning fallback
* not part of normal global reminding

Promotion to durable reusable memory should require at least one of:

* later external corroboration
* later real success downstream
* repeated cross-family usefulness
* novelty over existing promoted cases
* low contradiction count

For rationalization I would be stricter:

* no promotion without explicit external grounding or repeated successful downstream use

For reversal I’d allow promotion a bit earlier:

* but only when the counterfactual cause later helps produce a successful repair, not merely because it matched itself again

#### 2. Consolidation: cap near-duplicates and coping-clusters

Cluster family-plan episodes by the thing they are trying to say.

For rationalization:

* `(family, failed-goal-id, frame-id)`

For reversal:

* `(family, failed-goal-id, counterfactual-fact-set)`
  or `(family, failed-goal-id, cause-id)` if that is stable

Then:

* keep top 1–3 promoted episodes per cluster
* archive or compress the rest
* decay accessibility of episodes that are reused only inside the same family
* raise accessibility only from externally validated reuse, not self-retrieval count

The metric that should matter is not “how often did this episode fire again?”
It is “did this episode help anything outside its own family loop?”

### The minimum patch set

If you want the fastest non-handwavy fix, do this:

1. **Persist cue roles explicitly.**
   Store something like:

   ```clojure
   {:cue-indices {:plan #{...}
                  :reminding #{...}
                  :support #{...}
                  :provenance #{...}}}
   ```

   Do not collapse them into one flat retrieval space.

2. **Stop indexing support and rule-path ids into the normal episode index.**
   Keep them as metadata, or in a separate admin/debug index.

3. **Make `episode-reminding()` activate only `:reminding` cues.**
   Never `:support`. Never rule ids.

4. **Require content marks before provenance bonus can matter.**
   Provenance should rerank plausible matches, not conjure them from nothing.
   I’d require:

   * at least 1 raw content cue for normal episodes
   * at least 2 for `:source-kind :imagined-family-plan`

5. **Remove `:serendipity? true` from rationalization fallback** until admission/evaluation exists.

6. **Gate same-family fallback on promotion.**
   `rationalization-frame-candidates()` and `reverse-undo-cause-candidates()` should only accept stored family-plan episodes with `:admission-state :promoted`.

7. **Track loop-risk metadata.**
   Add fields like:

   * `:same-family-reuse-count`
   * `:cross-family-reuse-count`
   * `:external-grounding-score`
   * `:contradiction-count`
   * `:last-success-at`
   * `:promotion-reasons`

### The higher-level architecture answer

Your newer March 2026 architecture notes are already pointing in the right direction: keep storage layers separate, keep a generated counterfactual pool distinct from canonical state, and treat retrieval as coincidence counting over independent signals with a Critic/Selector watching for ruts. The kernel path here is violating that direction by dumping rich family-plan residue straight into durable reusable episodic memory instead of keeping most of it in a thinner, more controlled layer.  

### Bottom line

The system currently has no reliable distinction between:

* “this was an imagined coping move”
* “this proved useful later”
* “this should now count as reusable knowledge”

Until you add that distinction, writeback will bias the system toward its own prior coping narratives.

That is not a small issue. It changes the character of the whole memory system.

Look away for 20 seconds. Unclench your jaw.
