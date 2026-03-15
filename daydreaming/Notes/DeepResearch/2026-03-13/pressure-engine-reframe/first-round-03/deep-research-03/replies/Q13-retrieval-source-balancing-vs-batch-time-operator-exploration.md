# Q13. Retrieval-source balancing vs. batch-time operator exploration

## 1. MVP recommendation

Make **retrieval-source balancing** the next change, not batch-time operator exploration.

The specific MVP is:

**Reserve 1 of the 2 retrieval slots for authored backstory whenever an authored episode clears the existing `min_score`, and fill the remaining slot by current best-overall ranking.**

That is the smallest honest upstream fix.

Why this one first:

- In the current harness, the pool narrows **inside each sequence** before pooled admission ever starts.
- `run_middle_sequence()` appends accepted generated episodes back into the sequence-local `episode_pool`.
- `build_generated_episode()` gives those generated episodes inherited `concern_type` / `target_ref` / `situation_id` / `practice_type` tags plus `recency_rank = max_recency + 1`.
- `retrieve_episodes()` then sorts by `(-score, -recency_rank, fixture_order)`.
- So same-score generated memory beats authored backstory **by construction** on the next step.

That is the self-priming seam. It is upstream of `admit_candidate_pool()`.

Admission is already doing real work. The tests and Q1/Q3 state now already cover:

- semantic-gate hard rejection
- `duplicate_function_signature`
- overdetermination reward
- sequence-diversity pressure

So the next gain is not another admission rule. It is widening the candidate pool **before** pooled admission starts throwing out near-duplicates.

### Concrete patch

Patch `retrieve_episodes()` only. No fixture schema change required.

Pseudo-shape:

```python
authored = [row for row in eligible if row["episode_kind"] != "generated"]
generated = [row for row in eligible if row["episode_kind"] == "generated"]

ranked_authored = sort_current_way(authored)
ranked_all = sort_current_way(eligible)

retrieved = []
if ranked_authored:
    retrieved.append(ranked_authored[0])

for row in ranked_all:
    if row["episode_id"] not in {r["episode_id"] for r in retrieved}:
        retrieved.append(row)
    if len(retrieved) == max_episodes:
        break
```

Estimated change:

- `retrieve_episodes()` patch: ~20–30 LOC
- trace/debug additions: ~5–10 LOC
- tests: ~20 LOC

No new fixture fields. No new architecture.

### Ranking the candidate interventions

| Rank | Intervention | Concrete change | Benchmark effect | What metric says it worked |
|---|---|---:|---|---|
| 1 | **1-of-2 authored retrieval slot** | `retrieve_episodes()` only | Removes a structural self-echo bias, with mild distortion of the frozen v1 tie-break | authored episode present in retrieved set on most steps; more unique pre-admission function signatures |
| 2 | **Authored-over-generated tie-break on equal score** | `retrieve_episodes()` sort key only | Least distorting, but weaker than slot reservation | authored retrieval share rises, but may still collapse later |
| 3 | **Generated recency decay** | `build_generated_episode()` + `retrieve_episodes()` | More principled than hard slot reservation; moderate complexity | generated memories still contribute, but do not dominate after one step |
| 4 | **Alternating retrieval bias across sequences** | `run_middle_batch()` threads per-sequence retrieval policy | Acceptable batch exploration, because it perturbs memory source rather than forcing operator labels | operator / pressure / practice spread improves across the pooled winners |
| 5 | **Dynamic operator fatigue within sequence** | sequence state + `score_operators()` | Partly artificial; under current score geometry it may do little or force bad switches | operator entropy rises **without** semantic quality drop |
| 6 | **Forced operator seeding / rotation** | `run_middle_batch()` override or strong prior | Highest distortion; mostly gives diversity by fiat | almost anything can make the histogram look better, so keep this diagnostic-only |

The key comparison is simple:

- **retrieval-source balancing** changes the upstream memory context that feeds `episodic_resonance` and sometimes practice pressure
- **operator exploration** changes the winner selection more directly, but under the current score geometry that is usually a fight against the wrong terms

So retrieval balancing is the right first move.

## 2. Why it works

The present bottleneck is not that the admission layer fails to recognize duplicates. It is that the **sequence generator keeps manufacturing near-duplicates from a narrowed reminder pool**.

That is exactly what the live evidence says:

- Q3: the main break is repetition, not bad sentences; the compiler picks the best local version of a narrow beat, then does it again.
- Q3: generated-episode self-priming is already visible as an echo-chamber risk.
- `34-kai-batch-curation-packet.md`: the batch is mechanically successful but narrow, with stable avoidance geometry, weak progression, no pressure diversity, no practice diversity, and little contrast inside the admitted set.

And the code path matches that diagnosis:

1. `run_middle_sequence()` starts with authored backstory.
2. A step winner is accepted.
3. `build_generated_episode()` writes back a generated episode tagged from the current dominant concern / practice.
4. On the next step, `retrieve_episodes()` rewards exact tag match and highest recency.
5. The retrieved set tilts toward self-generated material.
6. `score_operators()` then sees similar `episodic_resonance` again.
7. The same operator wins again.

That means retrieval balancing is not just “memory hygiene.” It changes one of the few operator-differentiating terms that actually moves.

Why operator exploration is the worse first lever:

- `pressure` is operator-agnostic.
- salience reappraisal mainly changes `base_intensity`, so it lifts all operators together.
- `repetition_penalty` is static per family, not accrued from actual usage.
- the shared-context note for Round 2 already says that in Kai the `practice_fit + episodic_resonance` gap favoring avoidance is about **0.182**. A mild fatigue penalty will not flip that. A strong one will fake it.

So if you add fatigue before fixing retrieval, you get one of two bad outcomes:

- the penalty is small, and nothing changes
- the penalty is large, and the operator switches for bookkeeping reasons rather than because the sequence state changed

That is exactly the kind of artificial diversity you do **not** want.

### What metric says the MVP worked

Use pre-admission metrics, not admitted-set cosmetics.

Track these in `batch.trace.json`:

1. **retrieved source mix**
   - fraction of steps where at least one retrieved episode is authored backstory
   - fraction of steps where both retrieved episodes are generated

2. **pre-admission function diversity**
   - count unique `batch_function_signature(...)` across pooled accepted rows

3. **operator family spread in pooled step winners**
   - entropy or just raw counts

4. **pressure/practice spread in pooled rows**
   - unique `pressure_tags`
   - unique `practice_tags`

5. **semantic quality stability**
   - do not accept “more operator spread” if semantic hard failures increase or human keep-rate drops

The honest success condition is:

> more pre-admission graph-function spread, with no noticeable drop in semantic pass rate or human keepability.

## 3. Failure modes

### Retrieval-source balancing failure modes

1. **Backstory dead weight**
   - If the authored memory is genuinely stale, the reserved slot can drag old material into later steps.
   - Fix: only reserve authored material when it already clears the existing `min_score`.

2. **Thin-fixture overcorrection**
   - On a world as narrow as Kai, preserving backstory may still just resurface the same emotional lane.
   - That is fine. It tells you the fixture is narrow, not that admission failed.

3. **False diversity by source, not function**
   - You can widen retrieval source mix without widening graph function.
   - That is why the metric cannot just be “more authored retrieval.” It has to include unique function signatures and human curation outcomes.

4. **Same backstory episode every time**
   - Hard slot reservation may keep selecting the same authored episode repeatedly.
   - If that happens, move to authored tie-break + generated recency decay rather than staying with a blunt quota forever.

### Batch-time operator exploration failure modes

1. **Histogram theater**
   - Forced rotation can make the operator counts look better while the prose still performs the same dramatic work.

2. **Incoherent switches**
   - If rehearsal suddenly wins in a sequence whose retrieval and practice context still scream avoidance, the node may be distinct in label only.

3. **Semantic quality drop**
   - Exploration that broadens families but lowers pass rate or increases rewrite burden is not helping.

4. **Benchmark distortion**
   - The more directly you force the operator, the less you are testing whether the middle layer is actually doing selection work.

That is why “forced operator seeding” should be kept as a **diagnostic arm**, not the next default batch policy.

## 4. Higher-octane design

If simple source balancing plus a light exploration nudge still leaves the pool narrow, the next thing worth building is **not** a bandit and **not** a solver.

Build a **small batch controller** that chooses the next sequence policy from current pool coverage.

### Smallest principled controller worth building

Add a `sequence_policy` chosen inside `run_middle_batch()` before each call to `run_middle_sequence()`.

Possible policies:

- `neutral`
- `authored_anchor`
- `generated_permissive`
- `undercovered_family_nudge`

Controller state, updated after each finished sequence:

- operator counts from pooled accepted rows
- pressure/practice coverage from pooled accepted rows
- retrieved source mix
- duplicate-function rate using the existing `batch_function_signature(...)`

Controller rule sketch:

```python
if generated_retrieval_share > 0.6:
    next_policy = "authored_anchor"
elif undercovered_operator_exists and semantic_quality_ok:
    next_policy = "undercovered_family_nudge"
else:
    next_policy = "neutral"
```

And `undercovered_family_nudge` should be small, not a forced seed. Think:

- add a tiny family bonus in `score_operators()` for an under-covered family
- only for the first step of a sequence
- only if current batch coverage is skewed
- never override validation / semantic quality

That is the smallest controller worth building because it:

- reuses existing batch statistics
- keeps exploration at the **sequence policy** level, not per-node fiat
- stays upstream of admission
- does not require new fixture ontology

Estimated complexity:

- `run_middle_batch()` controller state + policy choice: ~40–60 LOC
- `run_middle_sequence()` threading policy: ~15–25 LOC
- `retrieve_episodes()` / `score_operators()` support: ~20–30 LOC
- trace/tests: ~30 LOC

### What I would not build as the “higher octane” move yet

Not yet:

- no Thompson sampling / UCB bandit
- no learned reward model
- no sequence-level MCTS
- no cross-fixture batch controller
- no embedding-based novelty controller

You do not have enough repeated human-curation signal yet to justify any of that.

## 5. Connection to other questions

This answer connects tightly to four nearby questions.

### Q8 — generated-episode self-priming

Q13’s MVP is basically the direct operational answer to Q8.

Q8 asks how to stop generated memory from crowding out authored backstory. Q13 asks which pre-admission lever actually widens the pool now. The answer is the same: fix retrieval first.

### Q5 — operator stationarity

Q5 is real, but it is downstream of Q8 in the current code.

If you do operator exploration before retrieval balancing, you are fighting a lane that is already stabilized by reminder source, `practice_fit`, and `episodic_resonance`.

So:

- retrieval balancing first
- only then a mild exploration nudge if the pool is still narrow

### Q12 — practice-context stationarity

If Tessa still stays narrow **after** retrieval balancing, the next likely blocker is Q12, not admission.

That is: static `practice_fit` may still be pinning the same family even with healthier retrieval.

So if retrieval balancing improves source mix but not function spread, do **not** jump straight to hard operator rotation. Look at practice-context evolution next.

### Q9 — intervention ordering

Q13 implies a very clear ordering for the next real batch:

1. retrieval-source balancing
2. rerun batch on Tessa
3. inspect pre-admission pool diversity
4. only if still narrow, add light sequence-level exploration

That is the correct order because it tests the least distorting upstream fix first.

## 6. What NOT to build yet

Do **not** build any of this yet:

- forced operator quotas across accepted shortlist
- sequence-by-sequence hard operator rotation as default behavior
- a bandit controller
- embedding retrieval to replace exact-match retrieval
- new admission heuristics before rerunning the batch
- cross-fixture batching
- any broader architecture rewrite

Also do not confuse two different goals:

- **widening the pool honestly**
- **making the operator histogram look prettier**

The first is the job. The second is bait.

## Bottom line

**Make retrieval-source balancing the next patch.**

The code says the collapse starts in `retrieve_episodes()` + generated writeback, not in pooled admission. The memos say the live problem is narrow material supply, self-echo, and all-avoidance batches, not a failure to reject duplicates. And the score geometry says a light fatigue term will usually do nothing, while a strong one will fake the result.

So the correct order is:

1. patch retrieval to keep authored backstory alive
2. rerun Tessa batch
3. measure pre-admission function spread
4. only then consider a light sequence-level exploration policy

That is the cheapest change that actually tests the right thing.
