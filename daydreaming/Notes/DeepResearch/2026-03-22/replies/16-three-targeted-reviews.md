Here’s the blunt read.

## Verdict

1. **The new effect runtime is the right base model**$_{80%}$, but the current ref/result discipline is still too loose to trust as a general kernel seam.

2. **The episode-admission / promotion / anti-residue / rule-access chain has good bones, but I would not trust it past the current 12-cycle Puppet Knows envelope**$_{35%}$ without tightening two or three specific leaks.

3. **The first `:llm-backed` evaluator should stay a post-plan critic in `family_evaluator.clj`, not become a generic `RuleV1` executor yet.** Its input should be a small typed packet; its output authority should be narrower than it is now.

The March architecture note still describes the system as **propose → validate → admit**, with the LLM on the proposing side and the validator/kernel owning admission and contradiction repair. That direction is correct.

---

## 1. Effect runtime: right model, wrong fallback semantics

### What is right

`rules/apply-effects` folding over `[world effect-state]` is the right reduction model.

Why:

* It keeps **state commit in kernel-owned code**, not inside executor closures.
* It lets `execute-rule` validate that effects are declared and typed before anything mutates.
* It gives you a clean split:

  * `rules.clj` owns generic reduction + truly generic ops
  * `goal_families.clj` owns family semantics and composite domain ops

That is exactly the seam you wanted in the March 22 direction: executors compute a transaction proposal, the kernel commits it. The architecture docs also point the same way: explicit validation, explicit contradiction repair, persistent canon, and LLM-as-component rather than LLM-as-world-mutator.

### What is wrong

The current runtime has one serious structural flaw:

**unresolved symbolic refs do not consistently fail closed.**

You’ve got this pattern:

* `resolve-effect-context-id` returns the symbolic ref itself if lookup misses
* `:fact/assert` is mostly safe because `cx/assert-fact` throws on unknown context
* but `:goal/set-next-context` and `:context/set-ordering` do **not** fail hard

So a typo or ordering bug can do one of two bad things:

* write `:branch-context` straight into a goal’s `:next-cx`
* synthesize a ghost context fragment under `[:contexts :branch-context ...]`

That is not a style nit. That is a real kernel-integrity bug.

### Mid-program results are also under-specified

`effect-state` currently works because it only carries two kinds of transient thing:

* `:context-refs`
* `:results`

That is enough for the current roving/rationalization/reversal programs.

It will stop being enough once you add any new created-id class:

* episode ids
* use ids
* event ids
* maybe dynamic branch ids

Right now `:results` is just a bag of maps indexed by keywords. There is no kernel-level contract for:

* who produces a given result key
* what shape it has
* whether a consumer is reading a missing key
* whether two ops write the same key by accident

So the model is correct, but the **IR is still weak**.

### Reversal shows the abstraction boundary

` :reversal/execute-branches` is already a mini-interpreter hidden inside one handler.

That means you have to choose one of two things:

* **Bless macro-ops** as first-class and keep them as composite kernel primitives, or
* make the generic runtime capable of nested/composed effect programs

Right now it is half-way. That is fine for this week, not for a stable seam.

### Minimum missing coverage for this seam

Before I’d widen the effect vocabulary, I’d want tests for:

* unresolved symbolic ref on **every** builtin op
* duplicate `:result-key`
* missing producer for `:from-*`
* branch-local result isolation inside composite ops
* non-context symbolic ids

---

## 2. Admission / promotion / anti-residue / rule-access chain: coherent, but too permeable

### What is genuinely good

These are the parts I would keep:

* explicit admission tiers: `:trace`, `:provisional`, `:durable`
* clear split between:

  * use attribution
  * outcome attribution
  * promotion evidence
  * admission reconciliation
* retrieval gates for:

  * recent episodes
  * contradicted/backfired
  * same-family loop
  * hot-cue aging
  * payload exemplar cap
* idempotent use-outcome resolution
* rule-access transitions derived from episode transitions, not some side registry fantasy

That is a real memory membrane, not hand-wavy prose.

### What I do **not** buy yet

#### A. The evaluator can durable-promote too early

This is the biggest problem.

Right now evaluator output can set `:promotion-decision :promote-durable`, which `family-plan-admission-status` turns into immediate `:durable`, and `store-family-plan-episode` can then open frontier rules at store time.

So the LLM can effectively say:

> this episode is durable now

and the kernel can then say:

> okay, open rule access

That bypasses the stronger membrane you built everywhere else, namely:
**use → outcome → evidence → reconciliation**.

If your design goal is “evaluator as gate/veto on earned promotion,” current code overshoots. It lets the evaluator be the promoter.

#### B. One cross-family success is enough

`qualifying-promotion-evidence` is basically “latest cross-family-use-success.”

That means one lucky reuse can make a payload exemplar durable.

That may be okay in a wide world. In a small repetitive world, it will over-promote.

#### C. Plain failure is too weak

Only `:backfired` and `:contradicted` create hard residue. Ordinary `:failed` mostly becomes stats.

So you can accumulate a class of episodes that are:

* not good enough to help
* not bad enough to get suppressed
* still hanging around as possible retrieval candidates or already-promoted durable material

That is how long-run systems get gummy.

#### D. Quarantine appears one-way

`reconcile-episode-rule-access` can quarantine non-core rules on hard failure.

I do **not** see a real reopening path from `:quarantined` back to `:frontier` or `:accessible`.

That may be intentional. If it is intentional, say so and test it as policy.
If it is not intentional, this will create permanent rule black holes in long runs.

#### E. Your payload-cluster cap is weaker than it looks

This one is subtle and important.

`payload-exemplar-cluster-cap` only works if semantically similar episodes land in the **same cluster**.

But your family-plan payload clusters include run-local goal ids. So repeated rationalization/reversal payloads across separate activations are likely landing in different clusters.

That means the cap is mostly deduping **within one activation lineage**, not across the conceptually same reframe/counterfactual over time.

So the dedupe membrane is partly fake.

#### F. Same-family-loop has an immunity loophole

Loop risk only fires when same-family reuse count crosses threshold **and** cross-family reuse count is zero.

So one legitimate cross-family success can immunize an episode against later same-family reentry pressure.

That is too generous.

#### G. The 12-cycle benchmark barely exercises rule-access dynamics end-to-end

The benchmark is useful, but it is not the hard case.

Why:

* all authored family rules are effectively accessible by default in the real family registry
* frontier opening/quarantine is mostly covered in unit tests with overridden deployment roles
* there is no 50-cycle or 200-cycle soak
* there is no small-world repetition test
* there is no evaluator-aggressive long run

So the chain is **structurally plausible**$_{70%}$ for the current window, but **not demonstrated** for long-run ecology.

### Highest-value missing coverage

If you only add four tests, make them these:

1. **100-cycle soak**, tiny world, evaluator on
2. **Payload-cluster dedupe across different goal ids**
3. **Quarantine recovery policy**, either explicit reopen or explicit permanence
4. **No evaluator-only rule opening**, unless you truly mean to allow it

---

## 3. Minimum safe input contract for the first `:llm-backed` evaluator

The most important point:

**This should not be the first real use of `rules.clj`’s generic `:llm-backed` executor.**
Keep it as a post-plan episode critic, where it already lives conceptually.

That matches your architecture docs and keeps blast radius small.

### Also: input contract is not enough

The real safety boundary is **output authority**.

You can give the model a perfect packet and still make the system unsafe if the model is allowed to decide too much.

### Minimum safe input packet

I’d make it something like:

```clj
{:schema-version 1
 :family :rationalization | :reversal | :roving
 :source-rule :goal-family/...
 :selection-policy :stored_rationalization_frame | ...
 :payload-summary {...}
 :emotion-summary {...}
 :reuse-summary {...}
 :heuristic-default {...}
 :kernel-caps {...}}
```

#### Required fields

* `:schema-version`
* `:family`
* `:source-rule`
* `:selection-policy`
* `:payload-summary`
* `:emotion-summary`
* `:heuristic-default`
* `:kernel-caps`

#### Payload summary should be **bounded and structural**

Not raw scene prose. Not full branch state. Not giant index dumps.

Examples:

* **roving**
  seed episode id, reminded count, maybe top content cue ids or types

* **rationalization**
  frame id, frame goal id, reframe fact ids/types, source episode id if reused

* **reversal**
  counterfactual source id, input fact ids/types, retracted fact ids/types, branch count

#### Emotion summary

Small before/after summary only:

* trigger emotion before/after
* new reframe emotion if present
* maybe delta magnitudes

#### Kernel caps

This is the key field.

The packet should explicitly tell the model what it is **allowed** to decide:

```clj
{:promotion-eligible? true|false
 :allow-archive-cold? true|false
 :allowed-flags [:stale :backfired :contradicted]
 :allowed-promotion-decisions [:stay-provisional :promote-durable]}
```

If `:promotion-eligible?` is false, then any model attempt to promote gets normalized away.

### What should be excluded

Do **not** send:

* full context contents
* full free-text scene descriptions
* raw world dumps
* fresh branch context ids
* run-local goal ids as semantic evidence
* whole retrieval/support index soup

The evaluator should judge **episode quality and reuse-worthiness**, not plan over your engine internals.

### Minimum safe output contract

For v1, I would narrow it to:

```clj
{:promotion-decision :stay-provisional | :promote-durable
 :anti-residue-flags []
 :evaluation-reasons [:...]}
```

Optionally:

```clj
:archive-recommendation true|false
```

### What the model should **not** control yet

I would **not** let the first evaluator directly set all of these:

* `:retention-class`
* `:keep-decision`
* `:promotion-decision`

all at once.

That is too much authority.

Safest v1:

* kernel keeps retention class / keep decision heuristics
* model can:

  * veto promotion
  * request hard flags
  * optionally request colder storage

That is a critic. Good.
What you have now is drifting toward a critic-plus-policy-engine. Too much.

### What `family_evaluator.clj` already gets right

Keep these:

* enum allowlists
* normalization to defaults
* capped reason list
* post-plan separation from generic rule execution

Those are good guardrails.

---

## Bottom line

* **Effect runtime:** right direction$_{80%}$, but unresolved symbolic refs currently fail open in places they absolutely should not.
* **Admission chain:** good design, weak long-run guarantees$_{35%}$ because evaluator promotion is too strong, dedupe is weaker than it looks, and quarantine looks irreversible.
* **LLM evaluator:** keep it a **post-plan typed critic**, shrink the packet, and narrow its authority. Do not let it become the thing that both judges and opens memory/rule access.

If I had to reduce this whole review to one sentence:

**Do not let a loosely supervised model output turn a provisional family-plan episode into durable memory plus opened rule access without downstream evidence.**

Look away from the screen for a minute and relax your shoulders.
