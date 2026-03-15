According to documents from March 15, 2026, the order is:

0. **Do the Tessa hygiene fixes first**. I’m not counting these in the four-way ranking, but they are prerequisite: stop reading `expected_*` at runtime, fix the rationalization `close → ontic` misclassification, and clean up the rationalization prompt wording. Otherwise the Tessa batch is a dirty signal.
1. **Retrieval source-balancing**
2. **Richer graph projection**
3. **Scene-state carry-forward**
4. **Operator exploration policy**

If you only make **one** ranked change now, make it **retrieval source-balancing**.
If you can make **two**, do **retrieval source-balancing + richer graph projection** before the first serious Tessa batch.

## 1. MVP recommendation

**Make retrieval source-balancing the next change.**

Smallest honest patch:

* In `retrieve_episodes()`, stop letting same-score generated episodes automatically beat authored backstory through `-recency_rank`.
* Reserve **at least 1 of 2 retrieval slots for authored backstory** when an authored episode clears threshold.
* Or, if you want the cheaper variant, keep the current scoring and change the tie-break so authored beats generated on equal score.

Why this one first:

* The self-priming bias is structural in the current harness. `build_generated_episode()` writes accepted generations back with inherited concern/situation/practice tags and `max_recency + 1`, then `retrieve_episodes()` sorts by `(-score, -recency_rank, fixture_order)`. That means a generated episode with the same score beats authored backstory by construction.
* `run_middle_batch()` and `admit_candidate_pool()` can suppress duplicates, but they cannot manufacture upstream diversity. Q1 already established that admission is the right shape now; the bottleneck is earlier.
* Q3 and the Kai curation packet both say the problem is not sentence quality. It is narrow material supply and echoing function. Fix the echo chamber first.

**Ranking by short-term leverage**

| Rank | Intervention                | Expected short-term leverage  |
| ---- | --------------------------- | ----------------------------- |
| 1    | Retrieval source-balancing  | highest                       |
| 2    | Richer graph projection     | very high                     |
| 3    | Scene-state carry-forward   | medium                        |
| 4    | Operator exploration policy | low now, high later if needed |

## 2. Why it works

### 1) Retrieval source-balancing

**Failure addressed:** generated-episode self-priming.

Current failure mode:

* Accepted generated beats re-enter memory tagged as the same concern/situation/practice family that produced them.
* After 2–3 steps, retrieval can become “my own previous outputs plus one reminder,” which is exactly how you get the same dramatic move in new nouns.

**Observable batch change if it works:**

* Later-step retrieved sets keep contact with authored backstory instead of collapsing into self-echo.
* `selected_retrieved_episode_ids` in traces keep showing authored episodes past step 2.
* Operator score breakdowns move a bit more because episodic resonance is no longer being fed only by the model’s own last move.
* Tessa should show more real competition between “public omission,” “half-apology turning into explanation,” and “silence hardening story,” instead of just recursively amplifying whichever one fired first.

**Why this is honest, not cosmetic:**

* It removes a structural bias already introduced by the harness.
* It does **not** force diversity. It restores contact with authored material the benchmark was supposed to test.

### 2) Richer graph projection

**Failure addressed:** graph-valid but graph-thin output.

Current failure mode:

* The prose and sidecar carry mixed pressure.
* The graph seam often collapses that to the dominant concern only because `validate_graph_projection()` checks only structural validity, not semantic richness.
* So the system may already be generating a useful Tessa node in prose, but the graph residue lies about it.

This matters a lot for Tessa:

* The fixture’s intended shape is not single-pressure. It wants `status_damage + obligation`.
* If the graph keeps flattening that to one tag, your batch looks thinner than it really is, and admission under-rewards overdetermination.

**Observable batch change if it works:**

* Tessa nodes start carrying both `status_damage` and `obligation` when the prose and sidecar support both.
* `origin_pressure_refs[]` stop collapsing to one concern.
* Setup/payoff refs become more graph-useful instead of merely resolvable.
* Shortlists get better because `overdetermination_gain` and pressure coverage are finally seeing the real node.

**Smallest honest code change:**

* Add a **projection pass after scene generation**: scene text + sidecar → graph projection.
* Or, if you want the even cheaper version, add a validator/rewrite rule that refuses or normalizes projections that flatten obviously multi-concern scenes.

This should be pulled forward **before** the Tessa batch, not blocked on later results. Otherwise you are measuring a broken seam.

### 3) Scene-state carry-forward

**Failure addressed:** fake progression.

Current failure mode:

* `run_middle_sequence()` now forwards `current_active_situation_id`, but the docs are right: scene-state progression is still only partially real.
* Later steps still mostly regenerate from fixture-authored state plus remembered text, not from a principled updated situation state.
* That is why step 3 often reads like “a better re-roll of step 1” instead of “the next state of the world.”

**Observable batch change if it works:**

* Later steps actually change what is available to do.
* More boundary transitions become real rather than rhetorical.
* You should see more movement in `option_effect`, more threshold/aftermath shape, more meaningful `payoff_refs`, and fewer same-situation same-action-class repeats.

**Why it ranks third instead of first:**

* It is more code than retrieval balancing.
* It is not the cleanest next test for Tessa, because Tessa’s immediate problem is not “can the world update?” It is “can we get a useful non-flattened batch without self-echo?”
* For the **first Kai patch test**, the authored scaffold is supposed to carry the larger traversal spine. You do not need fully real state progression before that first patch.

### 4) Operator exploration policy

**Failure addressed:** operator stationarity.

Current failure mode:

* `reappraise_concerns()` mostly changes `base_intensity`.
* In `score_operators()`, that mostly shifts the operator-agnostic pressure term.
* `practice_fit` and much of `episodic_resonance` stay anchored, so the same operator can keep winning.

That is real. But it is **not** the next thing to touch.

**Why it ranks last now:**

* On Tessa, rationalization is supposed to dominate a lot of the time. If you add exploration now, you risk manufacturing “diversity” by distorting the benchmark.
* You would not know whether a broader batch came from better memory contact and better seam preservation, or from an exploration tax pasted on top.

**When it becomes correct:**

* After retrieval balancing + richer projection.
* If Tessa still gives you a narrow shortlist with low graph-functional spread, then add a **light** exploration policy.
* And even then, prefer gentle cross-sequence exploration over aggressive within-sequence forced rotation.

## 3. Failure modes

### Retrieval source-balancing

Bad version:

* Hard-clamp authored episodes so strongly that recent generated material never matters.
* Then you get artificial “backstory reverence” instead of real memory dynamics.

Good guardrail:

* Reserve one authored slot only when authored material clears threshold and is still relevant.
* Let the second slot remain fully competitive.

### Richer graph projection

Bad version:

* Blindly copy all sidecar concerns into the graph.
* That gives you fake overdetermination: richer tags without richer scene function.

Good guardrail:

* Projection should be constrained by **scene text + sidecar**, not sidecar alone.
* If the scene doesn’t actually realize the secondary pressure, don’t tag it.

### Scene-state carry-forward

Bad version:

* Append shallow prose like “the choice is sharper now” without changing affordances or causal inputs.
* That produces fake motion.

Good guardrail:

* Carry forward only state that can affect next-step interpretation: threshold reached, response drafted, apology unsent but edited, object handled, confrontation deferred, etc.

### Operator exploration

Bad version:

* Quotas, forced rotation, or fatigue so strong that the system emits the wrong family for the world.
* On Tessa that would be especially bad. A “diverse” Tessa batch full of forced rehearsal is junk.

Good guardrail:

* Use this only as a later controller when the pool remains narrow after debiasing retrieval and preserving seam richness.

## 4. Higher-octane design

If the MVP pair (`retrieval balancing + richer projection`) is not enough, the smallest principled next step is:

* **Source-aware retrieval controller**

  * track authored vs generated proportion across steps
  * penalize self-echo only when it starts dominating
* **Generate-then-project compiler**

  * prose first, graph projection second
  * overdetermination judged from scene text + sidecar, not from the same one-shot JSON call
* **Light exploration only across sequences**

  * not “force operator B on step 3”
  * more like “start one sequence from a slightly different retrieval bias or fatigue prior”

That stays inside the current architecture. It is not a redesign.

## 5. Connection to the other questions

* **Q8** is upstream of this answer. Retrieval self-priming is structural, so retrieval balancing comes before anything that tries to diversify downstream.
* **Q6** is also upstream. Seam richness is not a polish pass. It changes what the batch is *for*.
* **Q7** explains why scene-state carry-forward is real work, but not the first work.
* **Q5 / Q12** explain why operator exploration is tempting. Resist that temptation until the more honest fixes land.
* **Q14** matters for sequencing: the first Kai bridge is a **patch test** with fixed scaffold and a small generated patch. That is why scene-state carry-forward is not yet blocking.

## 6. What NOT to build yet

Do **not** build any of this now:

* no solver / soft-constraint admission redesign
* no embedding-retrieval overhaul
* no full situation calculus / STRIPS-lite state machine
* no operator quotas
* no L3 redesign
* no conductor work
* no bigger Kai batch campaign before Tessa

## Practical sequencing answer

Here is the concrete order of work I’d use.

### Before the first useful Tessa batch

1. **Tessa hygiene fixes**
   Not ranked, but mandatory. Otherwise the batch result is contaminated.

2. **Retrieval source-balancing**
   Yes, **before any operator exploration**.

3. **Richer graph projection**
   Yes, **pull this forward now**. Do not wait for later batch results. Otherwise you will under-measure graph-functional diversity.

4. **Run the Tessa batch**

### Only if Tessa is still narrow after that

5. **Add light operator exploration**

   * cross-sequence first
   * not forced per-step rotation

So the explicit answer is:

* **Retrieval balancing before operator exploration?**
  **Yes. Absolutely.**

* **Operator exploration needed now or only if Tessa remains narrow?**
  **Only if Tessa remains narrow after retrieval balancing + richer projection.**

* **Seam-rich projection blocked on batch results or pulled forward first?**
  **Pulled forward first.**

### Before the first Kai micrograph patch test

6. **Run the Kai patch test with the improved retrieval + projection stack**

   * same scaffold
   * same scheduler
   * generated patch only

* **Does scene-state carry-forward matter before the first patch test?**
  **No, not as a blocker.**
  For the first patch test, the authored scaffold should carry the threshold/event spine. The generated patch only has to be good enough to supply 4 role-distinct precontact nodes.

### Right after the first patch test, or earlier only if the dry run fails

7. **Scene-state carry-forward**

   * If Kai cannot yield even one forward-driving patch node without fake repetition, move this up.
   * Otherwise, do it **after** the first patch test.

So the clean order is:

**Tessa hygiene → retrieval balancing → richer graph projection → Tessa batch → (if still narrow) light operator exploration → Kai patch test → scene-state carry-forward**

That is the least-churn path that actually answers the next two milestones.

Look away for a moment. Unclench your jaw.
