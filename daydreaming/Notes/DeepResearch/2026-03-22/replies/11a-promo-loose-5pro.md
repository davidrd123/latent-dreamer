My read: the missing piece is not “more evaluator,” it is a stricter state machine.

Right now you have:

* a decent **admission membrane**,
* one real **promotion path**,
* and one real **anti-residue detector**.

You do **not** yet have:

* a general durable-promotion contract,
* a rule-accessibility frontier,
* or outcome-grounded derivation rules for `:backfired`, `:stale`, `:contradicted`.

That is the right next cut.

## 1. Promotion from `:provisional` → `:durable`

Do not make promotion a free-floating score. Use **hard gates first, ranking second**.

### Promotion should require two kinds of evidence

A provisional family-plan episode becomes durable only if it has both:

1. **structural usefulness**
2. **outcome evidence**

If you skip (2), you’re back to “it fit once, therefore it gets to bias the future.”

### A. Structural usefulness

Acceptable durable-promotion triggers:

* **cross-family reuse** by a different family, already partly implemented via roving
* **successful downstream reuse** inside a later cycle, where the episode is cited as an input and the later plan does not fail
* **world confirmation**, where later canonical facts/events line up with a nontrivial part of the episode payload
* **novel coverage**, where the episode opens a rule/path/family combination that was previously inaccessible and later proves reusable

Not acceptable by itself:

* same-family reopenings
* evaluator saying “keep-exemplar”
* provenance overlap
* reminding alone
* repeated retrieval without downstream success

### B. Outcome evidence

You need a kernel-observable witness that the reuse was good.

The cleanest witnesses are:

* `:family-plan-outcome` fact
* `:downstream-success` fact
* `:world-confirmation` fact
* `:world-contradiction` fact
* `:reuse-outcome` fact

You do not have these yet. You need them.

The current trace already records enough raw material to derive some of this later:

* `selection`
* `rule-provenance`
* `retrievals`
* `emotion-shifts`
* `emotional-state`
* `branch-events`

So the missing thing is not observability from scratch. It is **typed outcome emission**.

### Promotion rule by family

This should be family-specific.

#### RATIONALIZATION

Be strict.

Promote only if:

* reused cross-family **or** reused later with a non-failed downstream plan,
* and no contradiction/backfire signal,
* and not dominated by same-family reuse.

I would require at least one of:

* cross-family reuse count ≥ 1
* successful downstream-use count ≥ 1
* world-confirmation count ≥ 1

And I would **forbid promotion** when:

* same-family reuse count > cross-family reuse count
* `:contradicted` or `:backfired`
* the only evidence is same-family reentry

Reason: rationalization is the most groove-prone family.

#### REVERSAL

Slightly looser than rationalization.

Promote if:

* the counterfactual cause helps a later successful repair/recovery/rehearsal,
* or gets reused cross-family without contradiction,
* and does not backfire.

Reversal is still dangerous, but there is a legitimate “learned repair case” interpretation here that rationalization mostly lacks.

#### ROVING

Also stricter than current intuition.

A pleasant cue episode should not become durable just because it was pleasant once.
Promote roving only if it becomes broadly useful:

* reused across families/situations,
* repeatedly retrieved without staleness,
* or attached to later beneficial transitions.

Otherwise keep it provisional hot-cue material.

### Suggested durable gate

I’d make promotion require all of these:

```clojure
{:eligible? (and
             (= :provisional (:admission-status episode))
             (not-any? #{:backfired :contradicted}
                       (:anti-residue-flags episode))
             (not (recent-episode? ...))
             structural-witness?
             outcome-witness?)}
```

Where:

```clojure
structural-witness? :=
  (or (>= cross-family-reuse-count 1)
      (>= successful-downstream-use-count 1)
      (>= world-confirmation-count 1))

outcome-witness? :=
  (and (zero? contradiction-count)
       (zero? backfire-count)
       (not same-family-dominated?))
```

Then use evaluator output only as a **tie-break / veto**, not as sufficient evidence:

* evaluator can veto promotion
* evaluator cannot alone cause promotion

That dependency matters because your executor boundary is not real yet. You do not yet have validated effect semantics, only instantiated payloads and family procedures. So an LLM evaluator cannot be trusted as the sole promotion authority yet. That sequencing is exactly right in your build order docs: memory membrane first, executor boundary next, evaluator widening after that. 

### The simplest non-wrong next step

Before any fancy scoring:

* keep current cross-family promotion
* add `:successful-downstream-use`
* add `:world-confirmation`
* refuse promotion on same-family reuse alone

That gets you 80% of the value.

---

## 2. Mapping Mueller’s rule accessibility frontier onto `RuleV1` / graph

Mueller’s point is not “some graph edges are hidden.”
It is:

* some **rules** are not available to regular planning,
* those rules become usable only through recalled episodes,
* and storage of a new daydream can make a previously inaccessible episodic rule accessible in new situations.  

So do **not** stuff accessibility into `RuleV1` source data.
And do **not** mutate the connection graph.

That would be the wrong abstraction.

### Keep three layers separate

#### 1. Structural graph

This is `build-connection-graph` / `bridge-paths`.

It stays:

* global
* structural
* candidate-only
* independent of current accessibility

Do not touch it.

`bridge-paths` is already overloaded enough by retrieval provenance. Do not make it context-sensitive too.

#### 2. Rule accessibility state

This belongs in **world state**, not in authored rule data.

Something like:

```clojure
:rule-access
{rule-id
 {:access-class        #{:generic :episodic}
  :frontier-status     #{:inaccessible
                         :analogically-accessible
                         :planning-accessible}
  :supporting-episode-ids [...]
  :promotion-support-count n
  :last-status-change-cycle c}}
```

#### 3. Episode-carried access

Episodes are the carrier that expose inaccessible rules.

That is already latent in your substrate because episodes store `:rule-path`.

So the mapping is:

* `RuleV1` stays structural
* `episode.rule-path` carries potential access-support
* `world[:rule-access]` stores current frontier status

### Concrete mapping

#### Generic kernel-authored rules

Start as `:planning-accessible`.

Examples:

* current extracted family trigger / activation / request / dispatch rules

They are authored substrate, not episodic discoveries.

#### Episodic rules / induced rules / future learned rules

Start as `:inaccessible`.

They become:

* `:analogically-accessible` when supported by a recalled episode
* `:planning-accessible` only after durable support

That mirrors Mueller much better than a binary `accessible?` on rules.

### What changes status

#### `:inaccessible` → `:analogically-accessible`

When:

* an episode containing the rule is retrieved and used,
* but the supporting episode is still only provisional or narrowly contextual.

This means:

* the rule can be used **through that episode**
* not yet as a regular planner candidate

#### `:analogically-accessible` → `:planning-accessible`

When:

* one or more **durable** episodes support the rule,
* and those episodes have non-bad outcome history,
* and, once the executor boundary is real, the rule has at least one postcondition-validated execution trail

That last dependency matters. Until you have `execute-rule` / effect validation, do not over-promote learned rules into general planning accessibility.

### What should emit accessibility changes

Not `rules.clj`.

This belongs at the admission/promotion membrane.

Concretely:

* when a provisional episode is promoted to durable,
* inspect its `:rule-path`,
* update `world[:rule-access]`,
* emit `:rule-accessibility-change` facts.

Example fact shape:

```clojure
{:fact/type :rule-accessibility-change
 :rule-id :some/episodic-rule
 :from-status :analogically-accessible
 :to-status :planning-accessible
 :supporting-episode-id :ep-42
 :reason :durable-episode-support
 :cycle 17}
```

### Important negative rule

Do **not** let same-family rationalization residue unlock rule accessibility.

Otherwise you turn coping loops into frontier expansion.

A rule should only move toward `:planning-accessible` via:

* cross-family durable support,
* successful downstream reuse,
* or world confirmation.

### Relation to serendipity

Mueller explicitly ties serendipity to inaccessible rules and inaccessible episodes. The point of serendipity is partly to surface solutions that regular planning could not reach because the relevant rule was inaccessible. 

In your substrate, that means:

* serendipity search still walks the **full structural graph**
* but verification / planning application filters by current **rule-access**
* inaccessible rules can still appear in candidate paths
* they just cannot be used by ordinary planning unless surfaced through an episode

That preserves the frontier without corrupting the graph.

### Verified paths stay deferred

This is separate from `verify-path`.

Accessibility answers:

* “may this rule participate in regular planning right now?”

Verified paths answer:

* “does this candidate path have a concrete binding witness?”

Do not merge them.
Do not wait for verified paths to add accessibility.

---

## 3. Concrete detection rules for anti-residue flags

You already have one real derivation rule:

* `:same-family-loop`

The others exist only as accessibility consequences, not as emitted diagnoses.

That’s the gap.

### A. `:same-family-loop`

Current logic is roughly right:

* same-family reuse count reaches threshold
* cross-family reuse count is still zero
* then flag and block same-family reentry

Keep it, but tighten the semantics:

Flag `:same-family-loop` if either:

1. `same-family-reuse-count ≥ 2` and `cross-family-reuse-count = 0`
2. or repeated same-family reuse happens within a short window and no external/world confirmation exists
3. or same-family reuse keeps reopening the same frame/cause cluster

For rationalization, cluster by:

* `(family, failed-goal-id, frame-id)`

For reversal, cluster by:

* `(family, failed-goal-id, cause-id)`
  or
* `(family, failed-goal-id, counterfactual-fact-set)`

That catches not just “same family twice,” but “same story twice.”

### B. `:stale`

Do not define stale as “old.”
Define stale as:

> aged without useful reuse.

Kernel-observable rule:

Flag `:stale` when all of these hold:

* episode age exceeds class-specific TTL
* `cross-family-reuse-count = 0`
* `successful-downstream-use-count = 0`
* `world-confirmation-count = 0`
* no nontrivial retrieval above threshold in the last K cycles

TTL should be class-specific:

* `:hot-cues`: 4 cycles is already consistent with your current decay
* `:payload-exemplar`: much longer, maybe 8–12 cycles
* `:cold-provenance`: usually archive, not durable anyway

Operationally:

* first stale state: down-rank
* persistent stale state: archive/quarantine candidate

### C. `:contradicted`

This one should be hard and explicit.

Flag `:contradicted` when a later canonical fact/event negates or invalidates a meaningful payload element of the episode.

Examples:

#### Rationalization

A later canonical event/fact says the reframe was wrong.

Example shape:

* stored reframe says `:seam_is_honesty`
* later world evidence says `:seam_is_deception`
* contradiction registry marks them incompatible
* flag episode `:contradicted`

#### Reversal

A later explicit failure cause or world fact negates the stored counterfactual cause.

Example:

* stored reversal payload assumes `:wall_was_open`
* later canonical evidence says `:wall_was_locked`
* contradiction registry marks conflict
* flag `:contradicted`

#### General

A stored episode implies character knowledge/state that `contradicts()` now rejects at the world boundary.

You already have the world-side pattern in the broader architecture:
`validate_node()` and `contradicts()` as deterministic admissibility checks. 

So the right cut is:

* add a shallow contradiction checker for episode payloads against current canon
* emit `:world-contradiction` fact
* derive `:contradicted`

This should block retrieval entirely, not merely down-rank.

### D. `:backfired`

This is the one people hand-wave. Don’t.

“Backfired” is **not** “felt bad.”
It is:

> reusing this episode made planning/outcome worse.

So you need a post-reuse outcome witness.

Flag `:backfired` when an episode is reused and one of these happens within the next N cycles:

1. the target family plan fails
2. the unresolved failed-goal pressure increases relative to pre-reuse baseline
3. negative trigger emotion rebounds upward after reuse
4. the system immediately reopens the same failed-goal/family cluster with stronger pressure
5. a canonical event outcome worsens the relevant situation after reuse

You can derive this from typed outcome facts, not raw vibes.

Suggested emitted fact:

```clojure
{:fact/type :reuse-outcome
 :episode-id :ep-42
 :source-family :rationalization
 :target-family :rationalization
 :failed-goal-id :g-9
 :pre-use-emotion-strength 0.72
 :post-window-emotion-strength 0.84
 :pre-use-situation-weight 0.61
 :post-window-situation-weight 0.78
 :plan-outcome :failed
 :result :backfired}
```

Then `flag-episode` on `:result :backfired`.

Until you emit something like this, `:backfired` will stay hand-wavy.

### Detection summary table

| Flag                | Trigger                                                    | Effect                            |
| ------------------- | ---------------------------------------------------------- | --------------------------------- |
| `:same-family-loop` | repeated same-family reuse with zero/weak external utility | block same-family reentry         |
| `:stale`            | age + no useful reuse + no confirmation                    | down-rank, then archive candidate |
| `:contradicted`     | later canonical fact/event invalidates payload             | block retrieval entirely          |
| `:backfired`        | reuse followed by failed/worsened downstream outcome       | block retrieval entirely          |

---

## The state machine I’d use

```clojure
:trace
  -> stored, auditable, never planning-retrievable

:provisional
  -> retrievable under strict membrane
  -> can accumulate reuse / contradiction / outcome history
  -> cannot unlock same-family fallback by itself

:durable
  -> granted only by structural usefulness + outcome evidence
  -> can support same-family fallback
  -> can contribute to rule accessibility frontier

:durable + :stale
  -> decayed / archive candidate

:any + :contradicted
  -> quarantined

:any + :backfired
  -> quarantined
```

And for rules:

```clojure
:inaccessible
  -> only episode-mediated access

:analogically-accessible
  -> usable through recalled supporting episodes

:planning-accessible
  -> durable frontier member
```

---

## What I would do next, in order

1. **Add outcome facts** after family-plan reuse
   Without this, `:backfired` and evaluator-gated promotion are mush.

2. **Add `world[:rule-access]`** as dynamic frontier state
   Not in `RuleV1`, not in graph edges.

3. **Add contradiction checking for payloads against canon**
   Start shallow and typed.

4. **Widen promotion beyond cross-family roving**
   But only after 1–3.

5. **Do not touch `bridge-paths`**
   Keep it cheap, structural, candidate-only.

That last one matters. Verified paths are still deferred. The right architecture is still:

* structural graph now,
* executor boundary next,
* verified path layer later.

Do not mix them.

If you want, I can turn this into a concrete design note with proposed fact schemas and state-transition tables keyed to the current namespaces.
