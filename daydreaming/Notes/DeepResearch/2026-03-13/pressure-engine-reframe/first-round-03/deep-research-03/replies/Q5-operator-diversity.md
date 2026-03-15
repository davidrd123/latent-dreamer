Here’s the blunt read.

## Findings first

1. **The current prototype has no real repetition penalty.**
   In `score_operators()`, `repetition_penalty` is static per family (`rehearsal=0.05`, `rationalization=0.08`, `avoidance=0.12`). That is not a history-sensitive penalty. It is just a family prior with a misleading name. So nothing in the current scoring path pushes back when the same operator wins three steps in a row.

2. **Within-sequence operator choice is structurally stationary.**
   `reappraise_concerns()` mostly changes `base_intensity`. That only changes the shared `pressure` term, which lifts all operators together. The differentiating terms are the ones that matter for diversity:
   - `appraisal_fit`
   - `practice_fit`
   - `episodic_resonance`

   Those barely move through the current reappraisal path.

3. **`derive_practice_context()` is more locked than the high-level story suggests.**
   Once the fixture’s practice-bias rules fire and produce a unique winner, the function returns that practice immediately. So in the live code:
   - Kai stays in `evasion`
   - Rhea stays in `anticipated-confrontation`
   - Tessa stays in `confession`

   That means the affordance menu and the `practice_fit` row are effectively fixed inside a sequence unless you explicitly bypass this path.

4. **Generated-episode writeback compounds the lock.**
   `build_generated_episode()` writes accepted steps back into retrieval with inherited concern / target / situation / practice tags and highest recency. `retrieve_episodes()` then sorts by exact-match score and recency. So after 2-3 steps, the system is increasingly retrieving its own same-lane material. That keeps `episodic_resonance` pointing at the same family.

5. **So the real problem is upstream control, not prose, and not admission.**
   Q3 and the Kai curation packet already established this: the system can write. The bottleneck is that the same operator template keeps winning before the batch ever reaches admission.

6. **Because of that, pure operator fatigue is not enough on its own.**
   For Kai especially, static `practice_fit` plus same-lane retrieval resonance create a large enough anchor that a small fatigue term will not honestly widen the pool. If you want the next batch to be less narrow, you need a small exploration controller, not just a renamed penalty.

---

## MVP recommendation

**Add one explicit exploration policy upstream of prompt generation.**

Not a redesign. Not a solver. Not per-step operator hand-seeding.

One policy, two tiny levers:

1. **Sequence-local operator fatigue based on actual recent usage**
2. **Batch-sequence soft rotation toward plausible non-winning families**

That is the smallest change that can move both:
- **within a sequence**: stop the same family from winning forever by inertia
- **across a 5-sequence batch**: deliberately widen the pool before admission

### What to change now

#### A. Add actual operator history to the scoring path

Thread a small runtime object through:
- `run_middle_sequence()`
- `compile_candidate_set()`
- `run_arm_middle()`
- `score_operators()`

Something as small as:

```python
operator_history: list[str]
sequence_target_family: str | None
```

Then in `score_operators()` add a real history term:

```python
consecutive = consecutive_tail_count(operator_history, family)
history_penalty = min(0.12, 0.05 * consecutive)
```

Do **not** smuggle this into the existing `repetition_penalty` field unless you rename the trace output. Keep the old static prior separate from the new history penalty so the trace stays legible.

#### B. Add a soft exploration target per sequence

In `run_middle_batch()`, assign each sequence a target family:
- sequence 1: neutral, no target
- sequence 2: second-ranked family from the natural step-1 score ranking
- sequence 3: third-ranked family from that ranking
- sequence 4: neutral again
- sequence 5: whichever non-dominant family is still underrepresented in the pooled accepted set

Then in `score_operators()` add:

```python
exploration_bonus = 0.06 if family == sequence_target_family else 0.0
```

But gate it:

```python
only apply if best_total - family_total <= 0.08
```

That gate is the important part. It means:
- do not force a family that is clearly wrong
- do nudge a family that is already close enough to be believable

So this is **soft rotation**, not hard operator seeding.

#### C. Keep the policy visible in traces

Emit both terms into the score breakdown:
- `history_penalty`
- `exploration_bonus`
- `sequence_target_family`
- `operator_history_tail`

Otherwise you will not know whether a new family won because the world changed, because retrieval changed, or because the controller nudged it.

### Why it works

Because it attacks the actual failure mode in the current code.

Right now the system has three anchors keeping the same family on top:
- fixed `practice_type`
- fixed `practice_fit` row
- same-lane retrieval resonance

A small history penalty by itself can only shave a few hundredths off the winner. That might matter for Rhea or Tessa in some steps, but it is too weak to widen the Kai pool honestly.

The batch-sequence target does the missing job: it creates a few controlled opportunities for the second- and third-place families to surface **only when they are already within a plausible band**. That is enough for the next milestone, which is not “full emergent diversity,” but:

- in a 5-sequence batch,
- produce at least 1 neutral baseline sequence,
- and 1-2 credible alternate-family sequences,
- so admission has a wider pool to work with.

This stays inside the current architecture:
- same fixtures
- same seam
- same batch unit from Q1
- same validator
- same admission pass

It is partly artificial. That is fine. This is an exploration controller for the batch authoring phase.

### Candidate mechanisms, compared

#### 1. Operator fatigue only
- **Approx. code:** 25-35 LOC
- **Fixture schema:** none
- **Artificial or emergent:** artificial
- **Risk:** low to medium
- **Problem:** too weak on its own, especially for Kai

#### 2. Forced operator rotation across sequences
- **Approx. code:** 20-30 LOC
- **Fixture schema:** none
- **Artificial or emergent:** strongly artificial
- **Risk:** medium
- **Problem:** can produce obviously wrong openings if ungated

#### 3. Appraisal perturbation after sustained same-operator selection
- **Approx. code:** 40-60 LOC
- **Fixture schema:** none if heuristic
- **Artificial or emergent:** semi-emergent
- **Risk:** medium to high
- **Problem:** if the perturbation is not tied to coping outcome, it becomes another disguised rotation trick

#### 4. Practice-context evolution triggered by repeated failure
- **Approx. code:** 60-100 LOC
- **Fixture schema:** maybe none, maybe new runtime templates later
- **Artificial or emergent:** much more emergent
- **Risk:** medium
- **Problem:** this is closer to the right long-term answer, but it is not the cheapest honest patch

### Failure modes

1. **Fake diversity**
   The batch may show more families while the prose is still doing the same dramatic job. This is the main risk. Q10 evaluation matters here.

2. **Bad operator starts**
   If the exploration bonus is ungated, the controller will push the model into a family that is not actually supported by the appraisal / practice state.

3. **Kai still stays narrow**
   This is likely. Kai’s world is narrow enough that even a good exploration policy may mostly widen the pool from “all avoidance” to “mostly avoidance plus one credible alternate lane.” That is still a win.

4. **Prompt-label compliance instead of behavioral shift**
   Because `build_middle_prompt()` names the selected operator explicitly, the model may comply with the label more than with the changed situation logic. That is not a reason to skip the patch, but it means the next evaluation pass has to inspect behavior, not just labels.

5. **It does not solve stationarity at the source**
   Correct. This is an MVP. It widens the pool. It does not make diversity emergent.

---

## Higher-octane design

**Build a coping-failure critic that changes appraisal / practice state when the current operator fails to reduce pressure.**

This is the smallest principled replacement for the MVP.

Do not think of it as “more novelty.” Think of it as:

> sustained use of one coping strategy with no relief should change the next appraisal, which should change the next socially legible move.

That is much closer to Mueller + EMA than raw rotation.

### The mechanism

Add one tiny runtime structure, not a new architecture:

```python
coping_state = {
    "same_operator_streak": int,
    "same_practice_streak": int,
    "last_commit_types": [...],
    "dominant_relief_history": [...],
    "secondary_concern_gain_history": [...],
}
```

Then after each accepted step, compute whether the operator actually helped.

For example:
- if the same family was selected again
- and commit type was `salience` or `none`
- and dominant concern intensity did not drop
- and the secondary concern kept rising or staying live

then the current coping strategy failed.

When that failure condition holds for 2 consecutive steps, fire a **coping-failure critic**.

### What the critic does

Not a global rewrite. Just a few operator-specific state updates before the next scoring pass.

#### Avoidance failure
If `avoidance` repeats and does not reduce pressure:
- lower effective controllability a bit for “delay will help”
- raise the secondary concern, often `obligation`
- allow practice context to shift from pure `evasion` toward a threshold form
- slightly discount avoidance resonance from self-generated episodes

Interpretation: the character has learned, within the sequence, that delay is not working.

#### Rehearsal failure
If `rehearsal` repeats and nothing changes:
- reduce novelty and preparation utility
- increase threshold pressure
- make actual confrontation or confession relatively more legible

Interpretation: more scripting is no longer buying control.

#### Rationalization failure
If `rationalization` repeats and blame pressure stays high:
- increase praiseworthiness pressure rather than diffusing it
- allow practice shift from `confession` toward `abandoned_confession` or `evasion`
- increase the chance that obligation overtakes status protection

Interpretation: the self-explanation is no longer covering the damage.

### Why it works

Because this finally changes the operator-differentiating terms for structural reasons.

The current problem is not that pressure is too weak. It is that pressure is shared. The higher-octane design changes the things that actually separate families:
- appraisal dimensions
- practice context
- secondary-concern competition
- resonance weighting

That makes switching **earned**.

This is basically the smallest version of:
- EMA coping-strategy switching
- Mueller-style mode change after non-progress
- concern competition when a secondary pressure becomes harder to ignore
- a predictive-processing story where a failed error-reduction strategy gets replaced by a different one

You do not need to import that full theory stack into the code. You only need the local consequence:

**repeated same-family use without relief must change the next state in a family-specific way.**

### Failure modes

1. **Heuristic whiplash**
   If the failure trigger is too sensitive, the system will thrash between families.

2. **Unprincipled practice shifts**
   If practice context changes without clear trace logic, you will get apparent diversity with no believable social frame.

3. **Still blocked by retrieval self-priming**
   If generated episodes keep dominating retrieval, the critic will be working against a memory loop that keeps pulling back to the same family.

4. **Still blocked by shallow scene-state progression**
   If the world state is not moving, the best you may get is more plausible paraphrase switching, not real trajectory.

---

## Connection to other questions

### Q8 and Q13 matter immediately

This answer does **not** remove the retrieval-source problem.

If generated episodes keep winning the memory race, `episodic_resonance` keeps pointing back into the same operator lane. So:
- **MVP for Q5** can run before retrieval balancing
- but **higher-octane Q5** will be partly wasted unless Q8/Q13 source-balancing lands soon after

Bluntly: a coping-switch system fighting a self-echoing retrieval pool will still look sticky.

### Q12 is the structural follow-on

Q12 is right that static `practice_fit` is the third anchor. The MVP here does not solve that. The higher-octane design does, because it makes practice context evolvable when coping fails.

So the sequencing is:
1. add explicit exploration policy now
2. then replace it with coping-failure-driven practice evolution if the MVP proves useful

### Q7 matters for making the switches feel real

If later steps still rebuild from a mostly unchanged situation, operator diversity may read as stylistic variation rather than scene progression. That is not a reason to wait on Q7 first, but it is why the first success criterion for Q5 should be **wider batch pool**, not “fully convincing multi-step character development.”

### Q10 matters for evaluation honesty

If evaluation cannot distinguish a genuine rationalization beat from an avoidance beat wearing rationalization tokens, you will over-credit the MVP. So Q10 is the check on whether the diversity is real.

### Q1 and Q3 stay unchanged

Nothing here reopens batch admission. Q1’s batch unit still holds. Q3’s diagnosis still holds. The problem is upstream of admission, and this patch is upstream.

---

## What not to build yet

Do **not** build any of this yet:

- a solver or bandit controller over sequence generation
- full Mueller performance/daydreaming mode machinery
- a general predictive-processing controller
- new fixture schema for operator exploration
- per-step hard operator seeding in prompts
- embeddings-based diversity control
- an LLM judge to decide whether the switch was “real”
- a large practice state machine

Also do **not** pretend the MVP is more principled than it is. It is a controlled exploration patch. That is enough for the next batch.

---

## Practical next move

If I were ordering the work for the next week, I would do this:

1. add real operator history to the scorer
2. add gated sequence-target exploration in batch mode
3. expose both terms in trace output
4. run a new `Tessa` 5-sequence batch first
5. judge success by whether the pool contains believable non-dominant-family material before admission
6. only then decide whether the coping-failure critic is worth building

That is the cheapest honest path.
