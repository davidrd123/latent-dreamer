## Q12. Practice-context stationarity: the third anchor

### Draft answer

#### 1. MVP recommendation

Do **not** treat raw practice-fit decay as the real fix. Use a tiny
**practice-template transition layer**.

That is the smallest defensible change.

Concretely:

- keep the current `v1` practice types:
  - `evasion`
  - `anticipated-confrontation`
  - `confession`
- do **not** add a full `SocialPracticeInstance`
- add **2-3 authored runtime templates per fixture** using the seam the
  code already has in `runtime_practice_templates()`
- add **2 transition guards** that run inside the sequence loop:
  1. **stagnation transition**
     - same operator family wins for 2 accepted steps in a row
     - recent `option_effect` values are only `none` or `clarify`
     - dominant concern intensity does not materially drop
     - no controllability bucket shift
  2. **advancement transition**
     - `active_situation_id` changes
     - or the accepted node produces `option_effect = open|close`
- compute `practice_fit` from the **current runtime template/state**,
  not just from coarse `practice_type`

If you want the truly cheapest smoke test before this, add a temporary
practice-fit decay multiplier and verify that stationarity weakens. But
that is a **diagnostic ablation**, not the design.

This is closest to the question-bank option of **practice-type evolution
rules**, but in the narrow form that fits the current architecture:
**template evolution within `PracticeContextV1`, not a new ontology**.

Bluntly: the current problem is not that the practice weights are too
strong in the abstract. It is that the system has no representation of
**a practice failing in a particular way over time**.

Mueller did **not** have explicit social-practice transitions. That part
is an extension. The Mueller-compatible part is outcome-driven strategy
switching: if one coping line keeps not helping, the system should stop
behaving as though nothing changed. The state representation is
Versu-derived; the trigger logic is Mueller-compatible.

#### 2. Why it works

The current runtime locks practice too early and then never revisits it.

In the code path today:

- `summarize_practice_biases()` turns fixture rules into fixed
  `practice_votes` / `family_votes`
- `derive_practice_context()` usually resolves from those votes before
  the controllability fallback matters
- `run_middle_sequence()` updates concern intensities and retrieval pool
- but it does **not** evolve practice state
- `score_operators()` then reads a static `practice_fit_table` keyed only
  by `practice_context["practice_type"]`

That is why practice becomes the third anchor.

Kai is the cleanest proof.

At step 1, the sidecar practice context is:

- `practice_type = evasion`
- `phase = precontact`
- `role = evader`
- affordances = `delay-contact`, `ritual-distraction`, `prepare-excuse`

At step 5, it is still the same object. Same practice type. Same phase.
Same role. Same affordance menu.

The step-1 and step-5 traces also keep the same practice bias summary:

- `practice_votes = {"evasion": 2.0}`
- `family_votes = {"avoidance": 2.0}`

So even while `run_middle_sequence()` is writing back concerns and
feeding generated episodes back into retrieval, the practice lane itself
never moves. The sequence trace stays in the same segment and the same
operator family keeps winning.

That is exactly the wrong behavior if the point of practice is to model
socially legible changing options rather than just provide one more
fixed prior.

A tiny transition layer works because it changes practice only when the
**use of the current practice has become legibly stale**.

That is the missing bit.

Notably, I would **not** derive `practice_fit` directly from retrieval.
That looks elegant and is wrong.

Why wrong:

- `episodic_resonance` already responds to retrieved episode
  `practice_type`
- after Q8, retrieval should remain a memory signal, not become the
  whole practice signal
- if retrieval also determines `practice_fit`, you double-count the same
  evidence and make the practice lane hostage to retrieval noise
- in the current code, that would make self-priming even more dangerous,
  not less

So retrieval may be a **soft vote** in a transition guard, but it should
not be the primary source of practice fit.

#### 3. Failure modes

1. **Fake diversity by timer**

   If the rule is just “same operator twice, therefore switch practice,”
   you will manufacture incoherent transitions. Kai does not become
   confrontation-ready merely because he polished three objects instead
   of two.

2. **No effect because the trigger is too strict**

   If you require a full situation change before practice can move, then
   nothing changes until Q7 is already solved. That misses the point.
   Stagnation itself has to be enough to change the practice posture.

3. **Double-counting retrieval**

   If retrieved episode mix directly sets `practice_fit`, the retrieval
   fix from Q8 and the practice fix from Q12 become the same mechanism in
   two places. That is bad design.

4. **Ontology creep**

   If you add ten new named practice types like `exhausted_evasion`,
   `shame_confession`, `half-entry`, `defensive_threshold`, you have
   quietly reopened the architecture the `v1` contract explicitly cut.

5. **Hallucinated advancement before Q7**

   Without real scene-state carry-forward, you cannot honestly claim that
   the practice has advanced because the scene advanced. Pre-Q7, the
   honest transitions are mostly:
   - stall
   - backslide
   - pressure redistribution

6. **Graph seam leakage**

   If you expose every internal practice substate to the graph seam now,
   you will repeat the same mistake already noted elsewhere: rich L2
   internals leaking into a seam that is supposed to stay stable.

#### 4. Higher-octane design

The principled version is a **minimal practice state machine**. Not full
Versu. Not concurrent practices. Just enough state to let a sequence
change its social posture.

The right cut for `v1.5` is:

- **2-3 runtime states per active fixture**
- **2 authored transition classes per state**
  - a **stall/backslide** transition
  - an **advance/threshold** transition
- keep the scoring formula the same shape
- replace the current one-key practice lookup with a
  **state-aware lookup**

You can do this with fixture-local template ids while leaving
`PracticeContextV1` itself intact.

##### Recommended generic clusters

| Cluster | State 1 | State 2 | State 3 |
|---|---|---|---|
| Evasion | fresh evasion | exhausted evasion | confrontation pull |
| Anticipated confrontation | preparation | threshold stall | entry commitment / backslide |
| Confession | initial confession | rationalized confession | abandoned confession / sharpened acknowledgment |

That is enough. You do not need more.

##### Minimal fixture mapping

| Fixture | Start template | Stall transition | Advance / threshold transition |
|---|---|---|---|
| **Kai** | `evasion_fresh` | after 2 avoidance wins with `option_effect ∈ {none, clarify}` and no meaningful relief → `evasion_exhausted` | if departure threshold becomes active, or obligation regains parity, or a real state advance lands → `confrontation_pull` |
| **Maren** | `preparation` | after 2 rehearsal wins with no entry and no reduction in exposure pressure → `threshold_stall` | if door/entry state advances → `entry_commitment`; if stall repeats without entry → `backslide_to_evasion` |
| **Rhea** | `threshold_rehearsal` | after repeated rehearsal outside Studio B with no meeting start → `defensive_threshold` | if still stalling and delay signals rise → `backslide_to_evasion`; if inside-room state advances → `offer_repair_pull` |
| **Tessa** | `initial_confession` | after one rationalization step with no reply sent → `rationalized_confession`; repeated again → `abandoned_confession` | if reply threshold activates or explicit acknowledgment becomes live → `sharpened_acknowledgment` |

The Tessa fixture is the easiest place to pilot this because it already
has explicit `runtime_practice_templates` for `confession` and `evasion`.
Rhea is the next cleanest because the fixture already encodes both the
script-before-speaking bias and the silence-can-backfire alternate lane.
Kai proves the stall problem, but Tessa and Rhea are better first
implementation surfaces.

##### How many transitions per practice type?

For `v1.5`, **two authored exits per active state are enough**:

1. **stall/backslide**
   - repeated same-family win
   - `option_effect` stays in `none|clarify`
   - no material concern relief
   - no state advance

2. **advance/threshold**
   - `active_situation_id` changes
   - or `option_effect = open|close`
   - or a real event precondition becomes newly active

That keeps each fixture at roughly:

- 3 states
- 2-4 transitions total

Anything bigger is premature.

##### How transitions interact with operator scoring

Do **not** add a second separate “practice-history bonus” term. That
would just create another anchor.

Instead, keep the formula shape and change only how `practice_fit` is
looked up.

Current shape:

```text
score = pressure
      + appraisal_fit
      + practice_fit(practice_type)
      + episodic_resonance
      - repetition_penalty
```

Recommended `v1.5` shape:

```text
score = pressure
      + appraisal_fit
      + practice_fit(runtime_practice_template)
      + episodic_resonance
      - repetition_penalty
```

If you want the smallest migration path, make it:

```text
practice_fit = base_fit(practice_type) + state_delta(template_id)
```

That is the right bridge.

- **MVP**: keep the current static lookup as a base prior and add small
  state deltas
- **higher-octane**: replace the type-only lookup entirely with a
  state-aware table

That answers the “additive or replace?” question cleanly:

- additive for the first cheap version
- replacement once the state machine is real

#### 5. Connection to other questions

**Q5 operator stationarity**

Q5 and Q12 are not the same problem.

- Q5 fixes repeated **family selection**
- Q12 fixes repeated **social framing**

If you only do Q5, you will force new operators inside an unchanged
practice lane. That yields diversity on paper and nonsense in the scene.

**Q7 scene-state progression**

Q7 makes the advance transitions honest. Without Q7, only stall and
backslide transitions are fully grounded. With Q7, `active_situation_id`
and world-state deltas become proper triggers for phase change.

**Q8 retrieval-source balancing**

Q8 should feed this only weakly. Retrieval can provide evidence that an
adjacent social move is now thinkable, but it should not define the
practice state directly. Otherwise retrieval and practice become the
same mechanism twice.

**Q11 prompt architecture**

This matters more than it looks. A stateful practice template gives the
prompt a better “what the character is doing about it” block. That is
how you get distinct prose texture without adding more JSON sludge.

**Q9 intervention ordering**

This is not first in line. Retrieval balancing and the basic operator
exploration fix still come first. But once those land, practice
stationarity is the next place the system can quietly re-freeze.

#### 6. What NOT to build yet

- do **not** build full `SocialPracticeInstance`
- do **not** import Versu concurrent-practice machinery
- do **not** invent a broad new ontology of micro-practices
- do **not** let retrieval define `practice_fit`
- do **not** add a second history bonus term on top of practice fit
- do **not** expose practice substates into the graph seam yet
- do **not** build a learned transition model
- do **not** solve this with prompt wording alone

The correct near-term move is much smaller:

- fixture-local runtime templates
- 2 transition guards
- state-aware practice fit
- no architecture reopening

That is enough to answer whether practice was actually the third anchor
or whether it only looked that way because Q5/Q7/Q8 were still missing.
