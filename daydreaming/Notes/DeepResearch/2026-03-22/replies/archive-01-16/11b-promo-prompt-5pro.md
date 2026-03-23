Here’s the design I’d ship.

Three blunt points first:

1. **Durable is not an evaluator output.** It is a later derived state from evidence.
2. **Rule accessibility does not belong inside `RuleV1`.** The graph stays structural. Accessibility is dynamic world state.
3. **Anti-residue should not be inferred on the retrieval hot path.** Retrieval reads flags. Use, outcome, and commit-time passes write them.

That split fits the current kernel instead of fighting it.

## 1. Promotion from `:provisional` to `:durable`

### The right state machine

Keep `store-family-plan-episode` exactly where it is in the pipeline, but stop treating it as the place where durable is decided.

Use this admission lattice:

```clojure
:trace < :provisional < :durable
```

and make it **non-monotone** in practice:

```clojure
:trace -> :provisional
:provisional -> :durable
:durable -> :provisional
:provisional -> :trace
:durable -> :trace   ; only for hard contradiction / archive decision
```

So yes: **promotion should be reversible**. Not because the system should flap constantly, but because “durable” means “currently trusted reusable precedent”, not “historically once promoted”.

Right now `family-plan-admission-status` is really **initial-admission-status**. Rename it. `:durable` should never be emitted there.

### Use the existing evaluation taxonomy as a lane selector

You already have a good taxonomy in `family_evaluator.clj`. Use it.

* `:keep-decision :archive-cold` or `:retention-class :cold-provenance`
  → `:trace`
* `:keep-decision :keep-hot` or `:retention-class :hot-cues`
  → `:provisional`, but **not a durable candidate**
* `:keep-decision :keep-exemplar` and `:retention-class :payload-exemplar`
  → `:provisional`, and **eligible for durable promotion**

That gives you a clean doctrine:

* **hot cues** stay hot, then fade
* **payload exemplars** are the only things that can become durable knowledge
* **cold provenance** stays auditable but inert

This also means most **roving** family-plan episodes should never become durable. That is correct. Roving is mostly attentional surfacing, not reusable knowledge.

### Add one explicit evidence layer

Do not make `promote-cross-family-retrieval-episodes` decide policy directly. That function name bakes in the wrong abstraction. Split it.

Add episode-local evidence:

```clojure
{:promotion-evidence
 [{:cycle 12
   :type :cross-family-use-success
   :source-family :rationalization
   :target-family :roving
   :branch-context-id :cx-17
   :source-rule :goal-family/rationalization-plan-dispatch
   :target-rule :goal-family/roving-plan-dispatch}
  {:cycle 14
   :type :grounded-by-explicit-frame
   :goal-id :g-failed
   :frame-id :rf-zone-mercy}
  {:cycle 15
   :type :evaluator-endorsement
   :evaluation-source :llm-backed
   :realism :plausible
   :desirability :positive
   :retention-class :payload-exemplar
   :keep-decision :keep-exemplar}]}
```

and outcome stats:

```clojure
{:outcome-stats
 {:successful-use-count 1
  :failed-use-count 0
  :backfire-count 0
  :grounding-count 1
  :contradiction-count 0
  :last-success-cycle 12
  :last-grounding-cycle 14
  :last-contradiction-cycle nil}}
```

### Family-specific promotion policy

This should be explicit, not implicit.

| family             | durable gate                                                                                                                                                  |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `:roving`          | almost never. Only if an external evaluator overrides it into `:payload-exemplar`, or it repeatedly scaffolds later successful planning across sessions       |
| `:reversal`        | `:payload-exemplar` + at least one real success signal: cross-family use success, later successful repair, or explicit grounding by authored `:failure-cause` |
| `:rationalization` | `:payload-exemplar` + evaluator endorsement + one externalizing signal: cross-family use success or explicit grounding by authored `:rationalization-frame`   |

That asymmetry matters. **Rationalization must be stricter than reversal.** Reversal can represent genuine repair learning. Rationalization is self-soothing by default.

### Automatic vs evaluator-gated

Do both, but not symmetrically.

* **Automatic kernel evidence** should drive most of the state machine.
* **LLM evaluator** should be one input, not the sole authority.
* For **rationalization**, evaluator endorsement should be mandatory.
* For **reversal**, evaluator endorsement is useful but not mandatory in MVP.
* For **roving**, evaluator should mostly keep things provisional.

So the answer is: **promotion should be automatic from observable evidence, with the evaluator acting as a gate or veto on the risky families**.

### Minimum viable gate

This is the smallest thing that prevents grooves without freezing the whole system:

```clojure
(defn durable-candidate?
  [episode]
  (and (= :provisional (:admission-status episode))
       (= :payload-exemplar (:retention-class episode))
       (= :keep-exemplar (:keep-decision episode))
       (empty? (set/intersection #{:backfired :contradicted}
                                 (set (:anti-residue-flags episode))))))

(defn eligible-for-promotion?
  [episode]
  (case (get-in episode [:provenance :family])
    :rationalization
    (and (durable-candidate? episode)
         (some #(= :evaluator-endorsement (:type %)) (:promotion-evidence episode))
         (some #{:cross-family-use-success :grounded-by-explicit-frame}
               (map :type (:promotion-evidence episode))))

    :reversal
    (and (durable-candidate? episode)
         (some #{:cross-family-use-success
                 :downstream-repair-success
                 :grounded-by-explicit-cause}
               (map :type (:promotion-evidence episode))))

    :roving
    false

    false))
```

That is enough to start.

### The functions to add

```clojure
(defn record-promotion-evidence
  [world episode-id evidence]
  ...)

(defn demote-episode
  [world episode-id demotion]
  ...)

(defn reconcile-episode-admission
  [world episode-id]
  ;; promote or demote based on current evidence + flags
  ...)
```

and a data-producing fact for graphability:

```clojure
{:fact/type :episode-outcome
 :episode-id :ep-12
 :branch-context-id :cx-17
 :family :roving
 :goal-id :g-4
 :use-role :reminded
 :outcome :succeeded
 :source-rule :goal-family/roving-plan-dispatch}
```

That last fact is not fluff. Review 06 is right that the graph is missing retrieval/evaluation/aftermath phases. This adds them.

### Where it hooks into current code

* `store-family-plan-episode`
  writes the new episode as `:trace` or `:provisional`, never `:durable`
* `roving-plan`, `rationalization-plan`, `reversal-plan`
  record **episode use**, not just reuse
* `run-family-plan`
  after evaluator output and episode storage, resolves pending episode uses into `:episode-outcome` and `record-promotion-evidence`
* `reconcile-episode-admission`
  runs on the source episodes that were used, and optionally on the newly created episode

Also: `promote-cross-family-retrieval-episodes` should become two things:

```clojure
(record-cross-family-use ...)
(reconcile-episode-admission ...)
```

Right now it is doing bookkeeping and policy in one place. That is the wrong seam.

---

## 2. Mueller-style rule accessibility frontier

### Do not put `:accessible?` on the rule

Track accessibility separately.

Reason: `RuleV1` in `rules.clj` is structural. `build-connection-graph`, `reachable-paths`, and `bridge-paths` are all derived from schemas only. That is correct. Accessibility is **usage state**, not rule essence.

So the right split is:

* **Rule**: static structural object
* **Rule registry**: dynamic planning/discovery state

### Add a dynamic rule registry to world state

```clojure
{:rule-access
 {:goal-family/roving-plan-dispatch
  {:status :accessible
   :source :authored-core
   :opened-cycle 0
   :opened-by nil
   :history []}

  :episode/induced-rule-17
  {:status :frontier
   :source :induced
   :opened-cycle nil
   :opened-by nil
   :history []}}}
```

Use a three-state lattice:

* `:accessible` = planner can use it
* `:frontier` = planner cannot use it, serendipity can target it
* `:quarantined` = neither planner nor serendipity should use it

That is better than a boolean.

### Static defaults belong in metadata, not state

You do need a static declaration of how a rule enters the world. Put that in optional metadata, not as a required `RuleV1` key.

For example inside `:provenance`:

```clojure
:provenance
{:book-anchors [...]
 :kernel-status :partial
 :deployment-role :core}   ;; or :frontier or :induced
```

`valid-rule?` already tolerates extra keys, so this does not require a substrate rewrite.

### Should `build-connection-graph` respect accessibility?

No.

Keep `build-connection-graph` purely structural.

Then apply accessibility as a **caller-level filter**:

* planning view = only `:accessible`
* serendipity view = `:accessible ∪ :frontier`
* serendipity target set = only `:frontier`

That preserves the property in the March docs: the graph is structural, not usage-weighted.

Also, you already have the right helper shape in `rules.clj`:

* `remove-rules`
* `graph-without-rules`

So the near-term implementation is easy:

```clojure
(defn planning-graph
  [world graph]
  (rules/graph-without-rules
   graph
   (inaccessible-rule-ids world)))

(defn serendipity-graph
  [world graph]
  (rules/graph-without-rules
   graph
   (quarantined-rule-ids world)))
```

Later, add `:allowed-rule-ids` to `reachable-paths` / `bridge-paths` if rebuilding filtered graphs becomes annoying.

### How accessibility interacts with admission tiers

This is the key point: **provisional episodes should not open rules**.

Otherwise you reintroduce the groove through the rule frontier instead of through episodic retrieval.

Use this rule:

* `:provisional` episode using a frontier rule
  → records **frontier evidence**
* `:durable` episode using a frontier rule
  → can open the rule to `:accessible`

So:

```clojure
(defn maybe-open-rule-frontier
  [world rule-id {:keys [episode-id branch-context-id reason]}]
  ...)
```

and the opening fact:

```clojure
{:fact/type :rule-access-change
 :rule-id :episode/induced-rule-17
 :from-status :frontier
 :to-status :accessible
 :episode-id :ep-42
 :branch-context-id :cx-19
 :reason :durable-episode-opened-rule}
```

That is Mueller-faithful in spirit. Stored, reusable episode material is what opens the planner’s frontier.

### What about authored rules?

The initial hand-authored family rules in `goal_family_rules.clj` should start **accessible**.

Do not romanticize the frontier so hard that you break the planner.

The frontier is useful first for:

* induced rules
* optional authored experimental rules
* future retrieval/result/evaluation rules that you want serendipity to discover before the planner relies on them

So I’d use:

* `:authored-core` → `:accessible`
* `:authored-frontier` → `:frontier`
* `:induced` → `:frontier`

### The right serendipity rule

Mueller’s `inaccessible-planning-rules` is not “all inaccessible rules in the graph”. It is the set of inaccessible rules touched by episodes. The kernel analogue should be:

* current concern path begins in accessible planner rules
* bottom targets come from **episode-linked frontier rules**
* path search runs over `accessible ∪ frontier`
* path verification that yields a durable episode can open the target rule

That is the actual frontier. Not a global bag of hidden rules.

---

## 3. Anti-residue detection

### General doctrine

Do not make retrieval infer residue. Retrieval is weak evidence.

Use a two-layer model:

* **online event-driven updates** for `:same-family-loop`, `:backfired`, explicit `:contradicted`, and positive grounding
* **batch consolidation pass** for `:stale` and for demotion decisions that depend on accumulated evidence

Retrieval-time code should only do this:

* consult flags
* apply family-aware blocking/downranking
* maybe surface a fact that a retrieval happened

It should not decide new residue states.

### Add explicit use records

Right now `note-episode-reuse` is too thin. It tracks family reuse counts, but not role, outcome, or goal.

Generalize it to:

```clojure
(defn note-episode-use
  [world episode-id
   {:keys [reason
           source-family
           target-family
           use-role
           goal-id
           failed-goal-id
           branch-context-id
           source-rule
           target-rule
           trigger-emotion-before]}]
  ...)
```

Store:

```clojure
{:use-history
 [{:cycle 12
   :reason :family-plan-use
   :source-family :rationalization
   :target-family :roving
   :use-role :reminded
   :goal-id :g-4
   :failed-goal-id :g-failed
   :branch-context-id :cx-17
   :trigger-emotion-before 0.82}]}
```

Then resolve it later:

```clojure
(defn resolve-episode-use-outcome
  [world episode-id
   {:keys [goal-status
           trigger-emotion-after
           evaluation
           grounded?
           contradicted?]}]
  ...)
```

That is the missing seam. Promotion and residue both want the same evidence.

### Per-flag detection

| flag                | detect from                                       | threshold                  | effect                                |
| ------------------- | ------------------------------------------------- | -------------------------- | ------------------------------------- |
| `:same-family-loop` | actual use events only                            | family-specific            | block same-family retrieval           |
| `:backfired`        | use outcome + later retrigger/outcome             | family-specific            | suppress retrieval, demote if durable |
| `:stale`            | consolidation over accumulated uses/outcomes      | 1 sweep rule               | downrank, maybe demote                |
| `:contradicted`     | explicit conflict with canonical / authored facts | one explicit contradiction | suppress retrieval immediately        |

#### `:same-family-loop`

Current code already has the right idea, but it is slightly too blunt.

Keep the logic “actual reuse, not retrieval”, but change the threshold to be family-specific and cycle-aware:

```clojure
(def same-family-loop-threshold
  {:rationalization 2
   :reversal 3})
```

and require **distinct cycles**.

So flag when:

* target family == source family
* same-family-use-cycles ≥ threshold
* cross-family-success-count = 0
* grounding-count = 0

That is more honest than raw count increments.

#### `:backfired`

Do not define backfire as “negative emotion increased immediately in the same branch”. That is too local.

Define it as **this episode was used, and the system got worse on the same concern shortly afterward**.

For current kernel data, I’d use three concrete signals:

1. target family-plan evaluation came back `:keep-decision :archive-cold` or `:desirability :negative`
2. same `failed-goal-id` retriggered the same family within `N=2` cycles with no meaningful drop in negative emotion
3. the supported goal later terminated `:failed` or `:terminated` without intervening success

That is implementable.

For rationalization, one strong backfire is enough. For reversal, require two.

```clojure
(defn backfire?
  [{:keys [family evaluation same-goal-retrigger? goal-status
           trigger-emotion-before trigger-emotion-after]}]
  (case family
    :rationalization
    (or (= :archive-cold (:keep-decision evaluation))
        (= :negative (:desirability evaluation))
        (and same-goal-retrigger?
             (>= (or trigger-emotion-after 0.0)
                 (- (or trigger-emotion-before 0.0) 0.05)))
        (#{:failed :terminated} goal-status))

    :reversal
    (or (= :archive-cold (:keep-decision evaluation))
        (#{:failed :terminated} goal-status))

    false))
```

That `0.05` epsilon is there so tiny relief does not count as success.

#### `:stale`

Do not use retrieval count alone in MVP. You do not yet emit generic retrieval-hit facts across all families.

For MVP, use **use without success**:

* `:payload-exemplar`
* used at least 3 times
* `successful-use-count = 0`
* `grounding-count = 0`
* no success in the last 8 cycles

Then flag `:stale`.

For full version, add retrieval/use ratio once retrieval-hit facts are generic.

So the sweep looks like:

```clojure
(defn stale?
  [world episode]
  (let [{:keys [successful-use-count grounding-count last-success-cycle]} (:outcome-stats episode)
        use-count (count (:use-history episode))
        age (- (:cycle world) (or last-success-cycle 0))]
    (and (= :payload-exemplar (:retention-class episode))
         (>= use-count 3)
         (zero? successful-use-count)
         (zero? grounding-count)
         (>= age 8))))
```

Important nit: **do not use `:stale` for ordinary roving hot cues**. You already have hot-cue age decay. That is enough.

#### `:contradicted`

Do not make the LLM the primary contradiction detector. That would be soft nonsense.

Use structural contradiction where you can, and explicit contradiction marking where you cannot.

The kernel needs a fact-type-specific comparator:

```clojure
(defmulti fact-contradicts?
  (fn [left right] [(:fact/type left) (:fact/type right)]))
```

Then run it against episode payload facts and newly canonicalized facts after event commit or authored fact assertion.

For MVP, I would only implement contradiction in two cases:

1. **reversal**: stored counterfactual payload conflicts with later explicit `:failure-cause` or canonical fact for the same goal
2. **rationalization**: stored reframe payload conflicts with later explicit `:rationalization-frame` or canonical fact for the same goal

If no structural comparator exists for a fact type, do **not** infer contradiction from absence. Leave it alone or let an offline evaluator suggest `:contradiction-risk` without directly flagging.

One explicit contradiction should be enough to flag:

```clojure
{:fact/type :episode-contradiction
 :episode-id :ep-12
 :branch-context-id :cx-22
 :goal-id :g-failed
 :reason :explicit-frame-conflict
 :contradicting-fact-id :rf-zone-mercy-2}
```

### Where detection hooks go

Not in retrieval.

Use these hooks:

**Online**

* `roving-plan`, `rationalization-plan`, `reversal-plan`
  call `note-episode-use`
* `run-family-plan` after evaluator output
  calls `resolve-episode-use-outcome`
* canonical event commit / authored fact assertion
  calls `detect-episode-contradictions`
* `note-episode-use`
  maintains same-family-loop stats

**Batch**

* `consolidate-episodes` at session end or every `N` cycles
  applies `:stale`, persistent loop demotion, and maybe archive moves

### The functions to add

```clojure
(defn note-episode-use
  [world episode-id use]
  ...)

(defn resolve-episode-use-outcome
  [world episode-id outcome]
  ...)

(defn detect-episode-contradictions
  [world changed-facts]
  ...)

(defn consolidate-episodes
  [world {:keys [max-stale-cycles]}]
  ...)
```

### Retrieval-time effect already composes

This part is good already.

`retention-accessibility-info` already does the right shape:

* `:contradicted` and `:backfired` suppress retrieval
* `:stale` downranks
* `:same-family-loop` blocks only same-family reentry

So the retrieval substrate is mostly fine. What is missing is **feeding it real evidence**.

---

## 4. End-to-end data flow

This is the full loop I’d aim for:

```text
family plan produced
  -> initial admission = :trace or :provisional
  -> episode-evaluation fact asserted
  -> reused source episodes get episode-use facts
  -> resulting plan gets evaluated
  -> episode-outcome facts asserted for the source episodes
  -> promotion evidence recorded
  -> anti-residue checks run
  -> reconcile-episode-admission
      -> maybe episode-promotion fact
      -> maybe episode-demotion fact
      -> maybe rule-access-change fact
  -> between sessions: consolidate-episodes sweep
```

That unifies all three questions:

* **promotion** comes from use/outcome evidence
* **rule accessibility** opens from durable reusable material
* **anti-residue** is the negative side of the same evidence stream

---

## 5. Minimal patch order

Not code, just the order I’d implement:

1. **Generalize `note-episode-reuse` into `note-episode-use`**
   Add role, goal, branch-context, pending outcome.
2. **Add `resolve-episode-use-outcome`**
   Feed current evaluator result into source-episode evidence.
3. **Replace direct cross-family promotion with `record-promotion-evidence` + `reconcile-episode-admission`**
   Keep current behavior behind that seam until stricter policy lands.
4. **Add `demote-episode`**
   Symmetric with `promote-episode`.
5. **Add `:rule-access` registry**
   Leave `RuleV1` structural.
6. **Add `consolidate-episodes` batch sweep**
   `:stale`, persistent loops, demotion.
7. **Only then** add fact-type contradiction comparators and rule frontier opening.

That gets you the architecture without pretending the kernel already has outcome attribution it doesn’t have.

Bottom line: the missing abstraction is not “promotion criteria” or “accessibility” separately. It is **episode use with attributed outcomes**. Once you log that cleanly, durable promotion, rule opening, backfire, stale, and contradiction all become ordinary state transitions instead of ad hoc patches.

Look away for 20 seconds. Relax your shoulders.
