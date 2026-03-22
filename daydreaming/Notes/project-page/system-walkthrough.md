# System Walkthrough: The Kernel Cycle by Cycle

Date: 2026-03-22

A ground-up explanation of what the kernel does, where the groove
problem lives, and what each fix is for. Written for someone
encountering the system for the first time.

---

## The Big Picture: What Happens Each Cycle

The kernel runs a loop. Each cycle:

1. **DECAY** — emotions and needs lose a little strength
2. **SELECT** — pick the concern with the highest emotional motivation
3. **PLAN** — run the family plan for that concern (roving, rationalization, reversal)
4. **RETRIEVE** — find stored episodes whose indices overlap with what's active
5. **STORE** — write the plan's output as a new episode in memory
6. **THOUGHT** — (optional) call LLM to generate inner-life narration
7. **WRITEBACK** — store the LLM's residue as another episode

After 12 cycles, the system has generated maybe 6-8 new episodes
in memory. Each one carries indices that future retrieval can match
against. Each one carries provenance about which rules fired.

That's the accumulation story. Episodes grow. Future retrieval
changes. The system's behavior shifts because its memory shifted.

## Where Episodes Come From

Three sources:

**1. Hand-coded seed episodes** — loaded from fixtures at startup.
Things like "the last fight at the harbor" or "a pleasant memory
of the old routine." Starting material.

**2. Family-plan episodes** — generated during planning. When the
kernel runs a rationalization, reversal, or roving plan, the output
gets stored as a new episode:

- Rationalization: "the delay wasn't cowardice, it was prudence"
  → stored with indices like :honesty, :performance
- Reversal: "what if the wall had been open?"
  → stored with indices like :wall_was_open, :seeing_through
- Roving: the reminding cascade's associative drift
  → stored with whatever indices surfaced

**3. Runtime thought residue** — from the LLM. The thought beat
produces residue_indices (filtered from the allowed set), and
those become a new episode marked :realism :imaginary.

## What Retrieval Looks Like

Coincidence-mark counting:

```
Active indices this cycle: [:honesty, :performance, :seeing_through]

Episode A has indices: #{:honesty :performance :harbor}
  → 2 marks (honesty + performance hit)
  → threshold is 2 → RETRIEVED

Episode B has indices: #{:reversal :wall_was_open}
  → 0 marks (no overlap)
  → NOT retrieved

Episode C has indices: #{:honesty :rationalization :seeing_through}
  → 2 marks (honesty + seeing_through)
  → threshold is 2 → RETRIEVED
```

Simple counting. An episode is retrieved when enough of its indices
overlap with what's currently active.

On TOP of that, there's a provenance bonus. If the currently active
rule shares a rule-path or graph edge with a stored episode, the
episode gets extra marks. So a rationalization episode gets a bonus
when the system is currently running rationalization, because they
share rule provenance.

## The Groove Problem

Trace one groove forming:

**Cycle 1:**
- Concern: failed goal, negative emotion
- Family: RATIONALIZATION activates
- Plan: finds stored rationalization frame "the delay was prudence"
- Output: new episode stored with indices #{:honesty :performance}
- Provenance: {:rule-path [:rationalization-plan-dispatch]}

**Cycle 4:**
- Concern: same failed goal, emotion has decayed but still present
- Family: RATIONALIZATION activates again
- Retrieval: the Cycle 1 episode is found because:
  - Active indices overlap (honesty, performance) → 2 marks
  - Same-family provenance bonus adds more marks
  - Serendipity lowering reduces threshold by 1
- Plan: reuses the old frame → stores ANOTHER episode
- Now TWO rationalization episodes reinforcing each other

**Cycle 8:**
- Same thing, but THREE rationalization episodes
- Each one makes the next retrieval easier
- The system has developed a "rationalization groove"

That's confirmation-by-writeback: "I already built a frame that
makes this failure feel better, so that frame becomes the easiest
one to retrieve next time."

## The Five Specific Risks

**Risk 1: Rationalization groove** — old reframes become easy to
find → get reused → make themselves even easier to find.

**Risk 2: Reversal counterfactual groove** — stored "what if X"
counterfactuals return as candidate causes even after the original
failure evidence is gone. An imagined repair becomes a standing
causal prior.

**Risk 3: Cross-family laundering through roving** — roving is
supposed to be pleasant diversion. But stored rationalization and
reversal episodes can feed roving's reminding cascade. Prior
self-generated reinterpretations re-enter through a "pleasant
reminder" channel. Affective recirculation disguised as spontaneous
association.

**Risk 4: Double-counting** — the same structural information gets
counted twice. Once as cue overlap (indices match) and again as
provenance bonus (rule-paths match). Same evidence, two boosts.

**Risk 5: No anti-memory** — nothing ever gets flagged as "this
rationalization backfired" or "this counterfactual was contradicted
by what actually happened." Every reused episode acts as precedent.
Nothing gets demoted.

## What Mueller Already Had

These risks are partly a regression. Mueller's 1990 implementation
had explicit admission discipline that we collapsed:

**Hidden episodes** (`dd_epis.cl:43-50`): stored with threshold =
100 (unreachable). Auditable but not retrievable. Our `:trace` tier
reconstructs this.

**Per-index roles** (`dd_epis.cl:94-104`): every index link
specifies `needed-for-plan?` and `needed-for-reminding?`. Misc
indices (people, places, objects) and result emotions are stored
with `nil nil` — metadata only, never counted toward threshold.
Our cue zone separation reconstructs this.

**Two separate thresholds**: planning retrieval and reminding
retrieval use different thresholds. An episode can be easy to
find for reminding but hard to find for planning. We have the
fields but collapsed the cue roles into one flat set.

**Realism/desirability as pruning criteria** (`dd_epis.cl:372-430`,
`dd_rule2.cl:97-137`): computed before storage, used during later
retrieval ordering. Episodes below threshold are skipped. If all
retrieved episodes for a goal are low-desirability, the goal is
deactivated. We have the fields but don't compute or use them.

**Rule accessibility frontier** (`dd_epis.cl:49`, `dd_ri.cl:273`):
rules start inaccessible. A rule becomes accessible only when it
produces a stored non-hidden episode. Serendipity specifically
searches INACCESSIBLE rules — rules that exist in episodes but
haven't been used in planning yet. Finding and verifying a path
through an inaccessible rule is what promotes it. This is the
mechanism by which serendipity expands the planner's repertoire.
We don't have anything like it yet.

**Post-storage reminding with serendipity suppressed**
(`dd_epis.cl:550`): newly stored episodes enter the reminding
cascade (their indices prime the FIFO) but with serendipity
explicitly suppressed. The system doesn't "discover" its own
just-stored episode as a serendipitous finding.

## The Fixes — What Each One Does

### Fix 1: Admission Tiers

Right now every family-plan episode immediately becomes fully
retrievable. The fix adds a status field:

**`:trace`** — "this happened, we recorded it."
Auditable. Replayable. NOT retrievable for future planning.
Like a diary entry you wrote but don't re-read.
(Mueller's `hidden?` episodes.)

**`:provisional`** — "this might be useful, let's see."
Retrievable, but under stricter conditions. Must have at least
one world-anchored cue (a real fact, not just family-internal
resonance). Like a hypothesis you're willing to consider but
haven't confirmed.

**`:durable`** — "this earned its place through downstream success."
Fully retrievable. Can bias future planning and reminding.
Promoted only after evidence: reduced negative emotion without
contradiction, cross-family usefulness, improved external behavior.
Like a lesson you've validated through experience.

Default for rationalization and reversal output: `:provisional`.
They have to EARN durability.

### Fix 2: Cue Zone Separation

Right now all indices are equal. The fix separates them:

**Content cues:** `[:honesty, :performance, :seeing_through]`
- Drive retrieval (counted toward threshold)
- Enter the recent-indices FIFO (influence next cycle)
- Participate in reminding cascade

**Provenance:** `[:rationalization-plan-dispatch, :rule/hidden-blessing]`
- Queryable ("which rules produced this episode?")
- Weak tie-break in retrieval (not counted toward threshold)
- Do NOT enter the recent-indices FIFO
- Do NOT trigger reminding

**Support tags:** `[:family/rationalization, :admission/provisional]`
- Metadata only
- Never counted toward anything
- Filterable but not retrieval-active

(Mueller's `needed-for-plan?` / `needed-for-reminding?` with
explicit `nil nil` for misc indices.)

### Fix 3: Eliminate Double-Counting

Currently the same structural information gets counted as index
hits AND as provenance bonus. Fix: provenance is a tie-break, not
a threshold contributor. It reorders episodes that already crossed
threshold on content cues alone. It cannot push a sub-threshold
episode over the line.

### Fix 4: Cap Same-Family Provenance

If episode.provenance.family == active-family, cap the provenance
contribution. Require at least one non-family content cue. Pure
family-internal resonance is not enough to retrieve an episode.

### Fix 5: Anti-Residue

Add flags that retrieval can check:

- `:backfired` — this rationalization made things worse
- `:stale` — this counterfactual kept reopening but never improved
- `:contradicted` — later facts contradicted this frame
- `:same-family-loop` — retrieved/reused only within its own family

Retrieval down-ranks or excludes flagged episodes.

### Fix 6: Gate Rationalization Resurrection

Remove `:serendipity? true` from stored-frame fallback, or require
at least two independent world cues. Rationalization should not be
the easiest self-reinforcing family by construction.

## The Executor Boundary (Build Step 2)

Separate from the memory fixes. Currently, family plans directly
mutate the world map and return a summary. The problem: by the time
the kernel could validate or evaluate the result, the world has
already changed.

The fix: executors return a DESCRIPTION of what they want to do:

```clojure
{:consequents [{:fact/type :family-affect-state ...}]
 :effects [[:context/sprout {:parent context-id}]
           [:fact/assert {:context context-id :fact reframe-fact}]
           [:emotion/replace {...}]]
 :episode-material {...}}
```

The kernel applies those effects. The kernel can:
1. Validate consequents against the rule's schema
2. Check the denotation ("is this what the rule was supposed to do?")
3. Apply the effects to the world
4. Decide the admission tier for the resulting episode
5. Stamp provenance AFTER admission

The executor never touches the world directly. The kernel owns all
state changes.

## The LLM Evaluator (Build Step 3)

The first `:llm-backed` rule is an evaluator, not a generator.

After a rationalization or reversal plan completes, the kernel asks
an LLM: "given this character's situation and history, is this
rationalization plausible? Is it desirable?"

```clojure
{:realism 0.35
 :desirability 0.6
 :keep-recommendation :provisional
 :reason "reframe is internally coherent but relies on a distinction
          the character hasn't actually tested"}
```

That evaluation gates promotion from `:provisional` to `:durable`.
(Mueller's realism/desirability computation, modernized — he
hard-coded scores from rule plausibilities; we use contextual LLM
judgment with the same structural role.)

Safest LLM boundary because:
- The LLM doesn't generate content
- The LLM doesn't control state
- The LLM evaluates what the kernel already produced
- Fallback: neutral score, episode stays provisional

## Verified Paths (Build Step 4)

Currently `bridge-paths` finds candidate paths through the
connection graph: "A's output type matches B's input, and B's
output type matches C's input." But it doesn't mean the path
WORKS — B might need additional inputs nobody provides.

The verified path layer adds a second pass:

```
verify-path(graph, [A, B, C], starting-context)

Step 1: Can A fire in this context?
  → match A's antecedent against context facts → bindings

Step 2: Does A's output feed B's input?
  → unify A's consequent with B's antecedent → updated bindings
  → B still needs a :dependency fact (open obligation)

Step 3: Does B's output feed C's input?
  → unify B's consequent with C's antecedent → bindings
  → if conflict → path fails
  → if success → path verified
```

Verification status lattice:
- `:candidate` — current bridge-paths (type adjacency)
- `:bound` — bindings propagate consistently
- `:supported` — full antecedents satisfied (with ambient facts)
- `:constructed` — episode/planning-tree witness built
- `:validated` — executor/denotation contracts checked

Mueller's serendipity specifically targeted the **inaccessible
rule frontier** — rules that exist in episodes but haven't been
used in planning. Verification is what promotes an inaccessible
rule to accessible. That's how serendipity expands the planner's
repertoire — it doesn't just find a connection, it unlocks a rule.

## The Full Discipline Chain

```
Rule fires
  → executor computes effects (DOES NOT mutate world)
  → kernel validates consequents against schema
  → kernel validates effects against effect-schema
  → kernel applies effects to world
  → kernel calls LLM evaluator (realism, desirability)
  → kernel decides admission tier:
      :trace       — recorded, not retrievable
      :provisional — retrievable with world-anchored cues only
      :durable     — fully retrievable (promoted after success)
  → index zones enforced:
      content cues   → drive retrieval + reminding
      provenance     → weak tie-break only
      support tags   → metadata, never counted
  → same-family provenance capped
  → anti-residue flags checked
  → newly stored episode primes FIFO (content cues only)
  → serendipity suppressed on storage event itself
  → episode stored with correct tier and zones
```

Each step makes the next one safe. You can't add more writeback
paths until the memory ecology won't be corrupted by them. You
can't gate promotion until the executor returns inspectable
results. You can't verify paths meaningfully until the graph
has enough heterogeneity from evaluation facts and typed events.
