# System Mechanics — Detailed Reference

Date: 2026-03-22

Detailed explanation of each mechanism in the kernel: how episodic
memory works, how the connection graph works, how admission and
anti-residue work, how the executor boundary will work, and how
they connect. This is the reference version; system-walkthrough.md
is the teaching version.

Note: anti-residue detection thresholds mentioned below are design
targets from the 5 Pro/5 Thinking reviews, not yet settled as
implemented constants.

Note: the reminding cascade runs within a single retrieval call,
not spread across cycles. The FIFO carries forward between cycles,
but the cascade itself is bounded within one call.

---

## How episodic memory works (Mueller + our extensions)

Mueller's episodic memory is NOT embedding similarity. It's
coincidence-mark counting over explicit named indices.

An episode is stored under multiple indices. Each index is a
keyword — :honesty, :seeing_through, :reversal, whatever. When
retrieval happens, the kernel has a set of "currently active
indices." It walks the index links and counts how many of each
episode's indices are currently active. If the count reaches the
episode's threshold, the episode is retrieved.

```
Episode A: indices #{:honesty :performance :harbor}
           plan-threshold: 2

Active indices this cycle: [:honesty :performance :seeing_through]

Marks on Episode A: 2 (honesty hit, performance hit)
Threshold: 2
Result: RETRIEVED
```

### Mueller's discipline that we're reconstructing

Each index link has TWO boolean flags:
- `needed-for-plan?` — does this index count toward the planning
  threshold?
- `needed-for-reminding?` — does this index count toward the
  reminding threshold?

So an episode can have indices that help find it for planning but
not for reminding, or vice versa, or neither (pure metadata).

Mueller also had TWO SEPARATE THRESHOLDS per episode:
- `plan-threshold` — how many plan-eligible indices must match
- `reminding-threshold` — how many reminding-eligible indices must
  match

Different callers use different thresholds:
- Planning retrieval: uses plan-threshold
- Reminding cascade: uses reminding-threshold

### Our cue zone system reconstructs this as:

**Content cues:** drive retrieval (counted toward threshold), enter
the recent-indices FIFO, participate in reminding cascade.

**Provenance:** queryable ("which rules produced this?"), weak
tie-break only (not counted toward threshold), do NOT enter the
FIFO, do NOT trigger reminding.

**Support tags:** pure metadata, never counted toward anything.

---

## How the reminding cascade works

When an episode is retrieved, its content-zone indices become
active. Those active indices can retrieve MORE episodes. Those
episodes activate THEIR indices. And so on, bounded by the
recent-indices FIFO (max 6) and the recent-episodes list (max 4).

The cascade runs within a single retrieval call:

```
Active indices: [:honesty, :performance]
→ Episode A retrieved (matched 2 of 2)

Episode A has content indices: #{:honesty :performance :harbor}
→ :harbor pushed into recent-indices FIFO
→ check: does :harbor retrieve any more episodes?
→ Episode B has indices: #{:harbor :sister :old_routine}
  → :harbor matches, but B needs threshold 2
  → only 1 mark → NOT retrieved in this cascade

But :harbor stays in the FIFO, carrying forward to next cycle.
If :sister becomes active next cycle:
→ Episode B gets 2 marks (harbor + sister) → RETRIEVED
→ Episode B's :old_routine enters the FIFO
→ ...and the cascade continues
```

This is associative drift — one memory triggers another triggers
another, bounded by the FIFO sizes.

Critical discipline: only CONTENT-ZONE indices should enter the
recent-indices FIFO. If rule IDs or support tags enter the FIFO,
they crowd out content cues and the cascade drifts toward
structural echoes instead of semantic associations.

Mueller's post-storage reminding (`dd_epis.cl:550`): newly stored
episodes enter the cascade (their indices prime the FIFO) but with
serendipity explicitly suppressed (`no-serendipities? = t`). The
system doesn't "discover" its own just-stored episode.

---

## How the connection graph works

Rules connect when one rule's output can feed another rule's input.
The connection is computed from declared schemas, not runtime
behavior.

```
Rule A consequent-schema: [{:fact/type :goal-family-trigger
                            :goal-type '?goal-type
                            :failed-goal-id '?failed-goal-id}]

Rule B antecedent-schema: [{:fact/type :goal-family-trigger
                            :goal-type '?goal-type
                            :failed-goal-id '?failed-goal-id}]

→ Both have :fact/type :goal-family-trigger
→ Shared structural keys: #{:goal-type :failed-goal-id}
→ No conflicting constants
→ EDGE: Rule A → Rule B
```

The graph is computed by pairwise comparison of all rules' output
projections against all rules' input projections.

**What the graph is:** a candidate adjacency graph. "These rules
COULD connect structurally."

**What it's NOT:** a verified execution graph. Just because Rule
A's output type-matches Rule B's input doesn't mean Rule B is
actually fireable — it might need OTHER antecedents nobody
provides.

**Why it must stay static:** structurally derived from rule
schemas, not usage-weighted. A path never traversed is equally
findable as one used a hundred times. This is what makes
serendipity different from retrieval — it finds paths through
structural connections you didn't know existed.

---

## How the admission system works

The problem: without admission discipline, every family-plan
episode immediately becomes fully retrievable permanent memory.
Over time, the system develops grooves — it keeps finding its own
prior rationalizations and recycling them.

### Three tiers

**`:trace`** — stored for the record, NOT retrievable.
Like Mueller's `hidden? = t` (threshold = 100).
For: cold provenance, archived material.

**`:provisional`** — retrievable, but under stricter conditions.
Must have world-anchored content cues. Same-family provenance
alone can't rescue it.
For: newly generated family-plan episodes (default).

**`:durable`** — fully retrievable, can bias future planning.
Promoted only after downstream evidence.
For: episodes that EARNED their place.

### Promotion requires evidence

- Cross-family reuse (useful outside its own family)
- Successful downstream planning (contributed to non-failed plan)
- World confirmation (later facts aligned)
- NO contradiction or backfire signals

The evaluator is a gate, not the sole authority. The LLM evaluator
can veto promotion but cannot alone cause it. Observable kernel
evidence drives promotion.

### The unifying abstraction: episode use with attributed outcomes

From 5 Pro review 11b: promotion, anti-residue, and rule
accessibility are three views of the same evidence. Log: "episode X
was used in cycle Y, by family Z, for goal G." Later resolve: "that
use succeeded/failed/backfired." From that evidence stream,
everything else is a state transition.

---

## How anti-residue works (partially implemented)

Four flags, each with specific detection logic:

**`:backfired`** — episode was used and things got worse.
Detection: same concern retriggered same family within ~2 cycles
with no emotional improvement, or the supported goal terminated
`:failed`.

**`:stale`** — episode keeps being retrieved but never leads to
success. Detection: used ~3+ times, successful-use-count = 0, no
grounding in ~8+ cycles.

**`:contradicted`** — later facts contradicted stored content.
Detection: structural comparison of episode payload facts against
newly canonicalized facts.

**`:same-family-loop`** — episode only reused within its own
family, never cross-family. Detection: same-family-use-cycles >=
threshold, cross-family success count = 0.

Key design: detection happens at USE time and OUTCOME time, not at
retrieval time. Retrieval only reads flags. Use-logging writes
them.

---

## How rule accessibility works (planned, from Mueller)

Mueller's design: rules start inaccessible. A rule becomes
accessible only when it produces a stored, non-hidden episode.
Serendipity specifically searches INACCESSIBLE rules.

Three states: `:accessible` / `:frontier` / `:quarantined`

- `:accessible` — planner can use it
- `:frontier` — planner cannot, serendipity can target it
- `:quarantined` — neither (rule is suppressed)

Tracked in world state, NOT in RuleV1. The structural graph stays
the same for all three states. Accessibility is applied as a
caller-level filter:

- Planning: only `:accessible` rules
- Serendipity: `:accessible` + `:frontier` (targets `:frontier`)

Interaction with admission: provisional episodes do NOT open rules.
Only durable episodes open rules. Otherwise the groove returns
through the rule frontier.

---

## How the executor boundary will work (planned)

Current: plan bodies directly mutate the world.
Target: executors return declarative effect descriptions.

```clojure
;; Executor returns:
{:consequents [...]
 :effects [[:context/sprout {:parent :cx-5}]
           [:fact/assert {:context :cx-6 :fact reframe-fact}]
           [:emotion/replace {...}]]
 :episode-material {...}
 :confidence 0.7
 :reason "hidden blessing reframe"}

;; Kernel pipeline:
;; 1. validate-rule-result-shape!
;; 2. validate-consequents! against :consequent-schema
;; 3. validate-denotation! via :validation-fn
;; 4. apply effects to world
;; 5. stamp provenance (after admission, not before)
;; 6. decide admission tier
;; 7. store episode (outside executor)
```

The executor computes. The kernel decides. The executor never
touches the world directly.

---

## How it all connects

```
Rule fires
  → executor computes effects
  → kernel validates (shape, schema, denotation)
  → kernel applies effects to world
  → kernel logs episode-use on source episodes
  → kernel evaluates result (possibly LLM-backed)
  → kernel decides admission tier
  → kernel resolves use outcomes on source episodes
  → promotion/demotion/anti-residue flags updated
  → if durable: maybe open frontier rules
  → index zones enforced on stored episode
  → content cues enter recent-indices FIFO
  → serendipity suppressed on storage event itself
  → next cycle: retrieval sees the changed landscape
```

Every piece feeds the next. Episode-use attribution feeds
promotion. Promotion feeds rule accessibility. Anti-residue feeds
demotion. The executor boundary makes all effects inspectable.
The cue zones keep the FIFO clean. The static graph keeps
serendipity anti-groove.
