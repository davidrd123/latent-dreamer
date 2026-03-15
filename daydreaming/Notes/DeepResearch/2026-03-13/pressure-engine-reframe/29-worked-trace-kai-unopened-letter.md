# Worked Trace: Kai and the Unopened Letter

Purpose: force one benchmark scenario through the `v1` middle-layer
contract before any prototype code is written.

This note is a coherence test for:

- `CausalSliceV1`
- `AppraisalFrame`
- `PracticeContextV1`
- operator scoring
- graph-compilable output
- post-commit reappraisal

Related:
- `11-settled-architecture.md`
- `21-graph-interface-contract.md`
- `27-authoring-time-generation-reframe.md`
- `28-l2-schema-from-5pro.md`
- `reading-list/13-l2-refactor-synthesis.md`

---

## Benchmark scenario

Kai has an unopened letter from his estranged sister asking him to meet
that night at the harbor.

The letter is on the kitchen table.
Kai has not opened it yet.

This is the smallest scenario that still tests:

- dual concerns
- memory retrieval
- causal interpretation
- appraisal
- practice activation
- operator choice
- graph-compilable generation

---

## Authored inputs

### Character seed

Kai:

- wants repair, but only if he does not have to say the first true thing
- fears being trapped in a conversation he cannot control
- defaults to delay rituals when attachment pressure spikes

### Backstory episodes

`ep_last_rupture`

- Kai and his sister fought at the harbor six months ago
- she said, "if you are going to stay silent, do it honestly"
- retrieval tags:
  - `concern_type = attachment_threat`
  - `target_ref = sister`
  - `practice_type = anticipated-confrontation`

`ep_old_harbor_memory`

- as teenagers, they used to wait at the harbor after family fights and
  pretend the ferries made leaving easy
- retrieval tags:
  - `concern_type = attachment_threat`
  - `target_ref = sister`
  - `situation_id = harbor`

`ep_avoidance_consequence`

- Kai once ignored her call on an important night and learned too late
  that the silence itself had become the message
- retrieval tags:
  - `concern_type = attachment_threat`
  - `target_ref = sister`
  - `practice_type = evasion`

### Active situation

`sit_unopened_letter`

- the unopened letter remains on the table
- the letter requests a harbor meeting tonight
- no reply has yet been sent

### Active concerns at step start

`cc_attachment_threat`

- intensity: `0.72`
- unresolved: `true`
- target: `sister`

`cc_obligation`

- intensity: `0.64`
- unresolved: `true`
- target: `sister`

Dominant concern for this step: `attachment_threat`.

---

## Step 1: CausalSliceV1

`CausalSliceV1` is the first place where raw situation becomes a
structured interpretation.

```clojure
{:focal_situation_id :sit_unopened_letter
 :concern_id         :cc_attachment_threat
 :target_ref         :sister
 :affected_goal      {:goal :preserve-possible-repair
                      :sign :threatens
                      :weight 0.85}
 :attribution        {:actor :kai
                      :intentional? true}
 :temporal_status    :prospective
 :likelihood_bucket  :high
 :self_options       [:open-letter :delay-contact]
 :other_options      [:sister-withdraws]}
```

What this adds:

- the threatened thing is now explicit
- blame is now explicit
- the option space is now explicit
- temporal orientation is now explicit

Without this step, the system only knows "Kai has attachment pressure."

---

## Step 2: AppraisalFrame

The appraisal reads the `CausalSlice`, not the raw situation.

```clojure
{:causal-slice-id   :cs_001
 :perspective-agent :kai
 :desirability      -0.78
 :likelihood        0.82
 :controllability   0.28
 :changeability     0.34
 :praiseworthiness  -0.52
 :expectedness      0.76
 :temporal-status   :prospective
 :realization-status :prospective}
```

Read:

- bad if faced directly
- likely to matter soon
- hard to control once engaged
- partly self-caused

What this adds:

- now operator choice can respond to low controllability rather than
  just high pressure
- later reappraisal has a concrete object to compare against

---

## Step 3: Derived EmotionVector

This is a view for dashboard and narration only.
It is not durable state and not prompt input in `v1`.

```clojure
{:fear           0.62
 :distress       0.46
 :remorse        0.39
 :disappointment 0.18}
```

Dashboard read:

- fear dominates
- distress is secondary
- remorse is present because attribution is partly self-directed

What this adds:

- readable inner-life output
- no new control authority

---

## Step 4: PracticeContextV1

Given the appraisal profile, the active practice is not yet a direct
encounter.
It is precontact evasion.

```clojure
{:practice_type   :evasion
 :role            :evader
 :phase           :precontact
 :affordance_tags [:delay-contact :ritual-distraction :prepare-excuse]}
```

What this adds:

- socially available next moves become narrower
- the system stops treating every operator as equally legible

Without this step, the generator can jump straight to confrontation
without earning it.

---

## Step 5: Retrieval pass

The `v1` retrieval rule is exact-match only.

Keys used in order:

- `concern_type = attachment_threat`
- `target_ref = sister`
- `situation_id = sit_unopened_letter`
- `practice_type = evasion`

Scoring rule:

```text
score(ep) = number of exact key matches
keep top 2 episodes with score >= 2
```

Retrieved episodes:

`ep_avoidance_consequence`

- matches:
  - `concern_type`
  - `target_ref`
  - `practice_type`
- score: `3`

`ep_last_rupture`

- matches:
  - `concern_type`
  - `target_ref`
- score: `2`

Not retrieved:

`ep_old_harbor_memory`

- matches only:
  - `concern_type`
  - `target_ref`
- `harbor` is a `place_id`, not the active `situation_id`
- under strict `v1` retrieval semantics, it does not receive a
  `situation_id` match here
- tie-break favors `ep_last_rupture` on recency

What retrieval adds:

- the step is now shaped by Kai's known habit of making silence do the
  talking
- the generator has concrete history, not just abstract pressure

---

## Step 6: Operator scoring

Canonical `v1` families:

- `rehearsal`
- `rationalization`
- `avoidance`

Trace weights for this worked example:

```text
score = 0.35 * pressure
      + 0.30 * appraisal_fit
      + 0.20 * practice_fit
      + 0.20 * episodic_resonance
      - 0.10 * repetition_penalty
```

Shared pressure term from dominant concern intensity:

- `pressure = 0.72`

### Rehearsal

```clojure
{:pressure           0.72
 :appraisal_fit      0.51
 :practice_fit       0.35
 :episodic_resonance 0.48
 :repetition_penalty 0.05
 :total              0.57}
```

Interpretation:

- plausible because the meeting is prospective
- weak because evasion practice does not strongly support open
  preparation

### Rationalization

```clojure
{:pressure           0.72
 :appraisal_fit      0.66
 :practice_fit       0.58
 :episodic_resonance 0.55
 :repetition_penalty 0.08
 :total              0.68}
```

Interpretation:

- fits self-attribution and the need to soften blame
- still secondary because the immediate affordance set is more about
  delay than excuse

### Avoidance

```clojure
{:pressure           0.72
 :appraisal_fit      0.82
 :practice_fit       0.90
 :episodic_resonance 0.62
 :repetition_penalty 0.12
 :total              0.80}
```

Interpretation:

- low controllability and high likelihood make non-engagement attractive
- practice context strongly supports delay
- retrieved history confirms avoidance as Kai's habitual move
- repetition penalty keeps it from winning by default, but not enough to
  dislodge it

Selected family: `avoidance`

Selected affordance tags:

- `:delay-contact`
- `:ritual-distraction`

---

## Step 7: Generated candidate node

Prompt inputs for `v1` generation:

- `CausalSliceV1`
- `AppraisalFrame`
- `PracticeContextV1`
- selected operator family = `avoidance`
- selected affordance tags
- short summaries of the two retrieved episodes

Generated node candidate:

> Kai lifts the envelope, reads only his sister's name, turns it face
> down again, and fills the kettle as if the water can decide whether
> the harbor still belongs to them.

Why this is better than a flat mood prompt:

- it contains the delay ritual
- it shows non-engagement as action, not just feeling
- it is specific to Kai's history with harbor and silence

---

## Step 8: Graph-compilable output

The candidate is only useful if it compiles to the seam.

Graph node fields:

```yaml
node_id: kai_letter_avoidance_001
node_type: beat
situation_id: sit_unopened_letter
delta_tension: 0.10
delta_energy: -0.06
setup_refs:
  - ev_harbor_meeting_tonight
payoff_refs:
  - sit_threshold_departure
option_effect: clarify
pressure_tags:
  - attachment_threat
  - obligation
practice_tags:
  - evasion
origin_pressure_refs:
  - cc_attachment_threat
  - cc_obligation
branch_outcome_tags:
  - delayed_contact
contrast_tags:
  - domestic_ritual_vs_urgent_summons
source_lane: l2_generation
source_lane: L2
scope: proposal
confidence: medium
revisability: ephemeral_candidate
source_ref: worked-trace-29
```

Why `option_effect = clarify`:

- the node does not resolve anything
- but it sharpens what the unresolved choice is

The sentence itself is a reference exemplar, not a literal test oracle.
The testable output here is:

- operator choice
- graph projection validity
- provenance completeness
- semantic fit to the benchmark

---

## Step 9: Provenance sidecar

The rich middle-layer state stays here, not in the graph.

```yaml
node_id: kai_letter_avoidance_001
source_concern_ids:
  - cc_attachment_threat
  - cc_obligation
causal_slice:
  focal_situation_id: sit_unopened_letter
  concern_id: cc_attachment_threat
  target_ref: sister
  affected_goal:
    goal: preserve-possible-repair
    sign: threatens
    weight: 0.85
  attribution:
    actor: kai
    intentional: true
  temporal_status: prospective
  likelihood_bucket: high
  self_options:
    - open-letter
    - delay-contact
  other_options:
    - sister-withdraws
appraisal_frame:
  desirability: -0.78
  likelihood: 0.82
  controllability: 0.28
  changeability: 0.34
  praiseworthiness: -0.52
  expectedness: 0.76
  temporal_status: prospective
  realization_status: prospective
emotion_vector_topk:
  fear: 0.62
  distress: 0.46
  remorse: 0.39
practice_context:
  practice_type: evasion
  role: evader
  phase: precontact
  affordance_tags:
    - delay-contact
    - ritual-distraction
    - prepare-excuse
selected_affordance_tags:
  - delay-contact
  - ritual-distraction
operator_family: avoidance
operator_score_breakdown:
  pressure: 0.72
  appraisal_fit: 0.82
  practice_fit: 0.90
  episodic_resonance: 0.62
  repetition_penalty: 0.12
  total: 0.80
retrieved_episode_refs:
  - ep_avoidance_consequence
  - ep_last_rupture
retrieval_keys:
  - concern_type
  - target_ref
  - practice_type
commit_type: salience
validator_result: pass
prompt_version: v1-middle-layer
```

---

## Step 10: Commit and reappraisal

Chosen commit type: `salience`

Reason:

- the node changes focus, not world fact
- it intensifies the concern stream without resolving it

Per the `v1` rule, `salience` does not resolve concerns.
It only changes intensity.

### Updated concern intensities

`cc_attachment_threat`

- old: `0.72`
- effect: the node intensifies focus on the threatened relationship
- new: `0.82`

`cc_obligation`

- old: `0.64`
- effect: the delay ritual buys brief relief without resolving duty
- new: `0.59`

Result:

- attachment pressure rises
- obligation dips slightly
- the next step is more likely to stay in evasion or tip into
  rehearsal, not clean confrontation

---

## Why each layer earns its keep

`CausalSliceV1`

- turns vague pressure into a threatened goal with attribution and
  option space

`AppraisalFrame`

- turns that interpretation into low-controllability, high-likelihood
  structure that meaningfully changes operator ranking

`PracticeContextV1`

- prevents socially unearned moves and supplies legible affordances

Derived `EmotionVector`

- gives the dashboard a readable inner-life summary without taking over
  control logic

Provenance sidecar

- keeps the graph seam thin while still making accept/reject reasoning
  inspectable

---

## Ablation read

If `CausalSlice` is removed:

- the scenario collapses to "Kai is anxious about the letter"
- operator ranking becomes much harder to justify

If `AppraisalFrame` is removed:

- low controllability never becomes explicit
- `avoidance` loses its strongest explanation

If `PracticeContext` is removed:

- the generator can jump too easily to confrontation or confession

If `EmotionVector` is removed from prompting:

- nothing important should change in `v1`
- that is the intended result

If provenance sidecar is removed:

- the node may still read well
- but it becomes much harder to inspect why it was accepted

---

## Bottom line

This chain appears to earn its keep.

The strongest evidence is that each step narrows and sharpens the next
one:

- concern becomes structured threat
- structured threat becomes appraisal profile
- appraisal profile becomes social practice
- practice plus retrieval makes `avoidance` win for reasons that are
  inspectable
- the generated node compiles cleanly to the graph seam

That is enough to justify writing the prototype spec next.
