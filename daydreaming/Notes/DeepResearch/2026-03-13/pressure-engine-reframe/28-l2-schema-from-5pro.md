# L2 Schema from 5 Pro

Source: GPT-5 Pro response to L2 middle-layer review request,
2026-03-14.

Purpose: Freeze the concrete schema for the missing middle layer
between raw world state and operator selection. This is the
reference for `CausalSlice`, `AppraisalFrame`, `EmotionVector`,
`SocialPracticeInstance`, and the `l2-step` transition function.

Related:
- `11-settled-architecture.md`
- `21-graph-interface-contract.md`
- `27-authoring-time-generation-reframe.md`
- `reading-list/13-l2-refactor-synthesis.md`

---

## V1 contract

This section is the authoritative `v1` build contract.

Use this section for:

- the worked trace
- the prototype spec
- any first implementation pass

Treat the larger schema below as reference or later expansion.

### 1. Runtime object cut

Build these as the explicit `v1` middle layer:

- `CharacterConcern`
- `CausalSliceV1`
- `AppraisalFrame`
- `PracticeContextV1`
- derived `EmotionVector`

Do **not** build full `SocialPracticeInstance` in `v1`.

### 2. CausalSliceV1

`CausalSlice` is first-class in `v1`, but short-lived.

- it should be built explicitly
- inspected in traces
- attached to provenance sidecar for accepted candidates
- discarded after reappraisal
- never exported into the graph seam

Minimal `v1` form:

```clojure
CausalSliceV1
{:focal_situation_id kw
 :concern_id         uuid
 :target_ref         kw?
 :affected_goal      {:goal kw :sign #{:threatens :advances} :weight 0.0-1.0}
 :attribution        {:actor kw? :intentional? boolean?}
 :temporal_status    #{:prospective :actual}
 :likelihood_bucket  #{:low :medium :high}
 :self_options       [kw]
 :other_options      [kw]}
```

### 3. AppraisalFrame is authoritative

`AppraisalFrame` is the source of truth for per-step evaluation.

`EmotionVector` is derived from `AppraisalFrame` and should be used for:

- dashboard
- narration
- trace inspection

It should **not** be:

- durable peer runtime state
- prompt input in `v1`
- graph residue

### 4. PracticeContextV1

Use a light practice wrapper in `v1`, not a durable full practice
object.

```clojure
PracticeContextV1
{:practice_type   #{:evasion :anticipated-confrontation :confession}
 :role            kw
 :phase           #{:precontact :threshold :aftermath}
 :affordance_tags [kw]}
```

### 5. Canonical v1 operator families

The canonical `v1` families are:

- `rehearsal`
- `rationalization`
- `avoidance`

Do not add `reversal` or `roving` in the first prototype.

### 6. Prompt-conditioning rule

The generation prompt in `v1` should be built from:

- `CausalSliceV1`
- `AppraisalFrame`
- `PracticeContextV1`
- selected operator / affordance
- retrieved episode summaries

Do **not** condition the prompt on `EmotionVector` in `v1`.

### 7. Retrieval rule

Simple exact-match retrieval is enough for `v1`.

Retrieval keys, in priority order:

- `concern_type`
- `target_ref`
- `situation_id`
- `practice_type`

Scoring rule:

```text
score(ep) = number of exact key matches
keep top 2 episodes with score >= 2
```

Additional rules:

- `max_retrieved_episodes = 2`
- generated episodes do **not** re-enter retrieval immediately
- only accepted episodes re-enter retrieval
- accepted episodes re-enter on the next step or next batch

### 8. Reappraisal/update rule

First compute:

```text
I_app = clamp_[0,1](0.6 * |desirability|
                  + 0.25 * likelihood
                  + 0.15 * (1 - controllability))
```

Then apply:

```text
ontic:
  rebuild CausalSlice from changed world
  reappraise
  intensity := I_app
  if trigger condition no longer holds -> unresolved=false, intensity=0

policy:
  if controllability rose by one bucket -> intensity := clamp(old - 0.15)
  else if controllability fell by one bucket -> intensity := clamp(old + 0.05)
  else intensity unchanged

salience:
  if node intensifies focus on the concern -> intensity := clamp(old + 0.10)
  if node successfully diverts focus -> intensity := clamp(old - 0.05)

none:
  intensity := clamp(old * 0.95)
```

Constraint:

- only `ontic` may resolve a concern or change concern type in `v1`

### 9. Canonical field split

Graph node fields:

```text
node_id
node_type
situation_id
delta_tension
delta_energy
setup_refs[]
payoff_refs[]
option_effect
pressure_tags[]
practice_tags[]
origin_pressure_refs[]
branch_outcome_tags[]?
contrast_tags[]?
source_lane
scope
confidence
revisability
source_ref
```

Provenance sidecar fields:

```text
node_id
source_concern_ids[]
causal_slice
appraisal_frame
emotion_vector_topk
practice_context
selected_affordance_tags[]
operator_family
operator_score_breakdown
retrieved_episode_refs[]
retrieval_keys[]
commit_type
validator_result
prompt_version
```

Runtime-only fields:

```text
active_concern_stack
current context tree / assumption patch
current reminder pool
unselected affordances
full candidate operator matrix
transient CausalSlice builders
scratch prompt state
live conductor bias
live L3 diagnostics / lookahead
```

Explicit seam rule:

- do **not** put `appraisal_summary_tags[]` in the graph seam

### 10. Canonical v1 benchmark

Use this benchmark first:

- Kai has an unopened letter from his estranged sister asking him to
  meet tonight at the harbor
- three backstory episodes:
  - the last rupture
  - an older harbor memory
  - one episode of avoidance with consequences
- one active situation:
  - the unopened letter on the table

### 11. Ablation / failure criteria

Each middle-layer object must visibly earn its keep.

- `CausalSlice` fails if removing it and prompting directly from
  `CharacterConcern + situation` yields the same operator rankings and
  curation yield
- `AppraisalFrame` fails if changing controllability, attribution, or
  temporal status does not change operator scores or intensity updates
- `PracticeContext` fails if swapping `evasion` for
  `anticipated-confrontation` leaves affordances and generated nodes
  effectively unchanged
- `EmotionVector` fails if removing it from prompting changes nothing,
  which is why it should stay out of the prompt in `v1`
- provenance fails if accept/reject decisions are no faster or clearer
  with sidecar fields than with just operator label plus retrieved
  episodes

---

## 5 Pro verdict

Mostly correct. One omission, two corrections.

**The omission is `CausalSlice`.** EMA does not appraise raw world
state directly. It appraises a structured interpretation of the
situation: affected goals, probable outcomes, attribution, and
available interventions. Without that intermediate object,
"EMA-lite" turns back into hand-authored concern updates with
appraisal vocabulary sprinkled on top. EMA's whole point is that
appraisal variables are read off an interpretation structure,
not guessed ad hoc.

**Correction 1: `AppraisalFrame` should be authoritative;
`EmotionVector` should be derived.** OCC gives you an
inheritance-structured taxonomy of emotion types elicited by
appraisal conditions. Durable runtime state should be
`CharacterConcern` plus maybe active practices, not a persistent
bag of named emotions. Compute `EmotionVector` fresh from the
current appraisal, use it for narration/UI, then discard or
cache it as a view. Persisting it as peer state invites drift.

**Correction 2: the graph seam is only half-solved by
self-annotation.** The settled architecture is explicit that L3
shares control geometry with L2, not ontology. Nodes should carry
residue from L2 generation, but L3 should read a stable
projection of that residue, not raw appraisal frames or practice
internals. Otherwise you leak L2 runtime ontology into traversal
and the level boundary starts rotting.

---

## Full reference schema

### CharacterConcern (durable motivational state)

```clojure
CharacterConcern
{:id                 uuid
 :owner-agent-id     kw
 :target-agent-id    kw?
 :concern-type       #{:status_damage :attachment_threat :retaliation_pressure
                       :anticipation :guilt :desire :curiosity
                       :unresolved_injury :obligation}
 :base-intensity     0.0-1.0
 :urgency            0.0-1.0
 :valence            -1.0..1.0
 :unresolved?        boolean
 :decay-rate         0.0-1.0
 :blockers           [kw]
 :origin-event-ids   [kw]
 :last-appraisal-id  uuid?}
```

### CausalSlice (the missing middle object — what EMA actually reads)

```clojure
CausalSlice
{:id                 uuid
 :focal-event-id     kw?
 :concern-ids        [uuid]
 :affected-goals     [{:goal-id kw
                       :utility -1.0..1.0
                       :probability 0.0-1.0
                       :status #{:actual :prospective}
                       :beneficiary kw
                       :threatened? boolean}]
 :attribution        {:actor kw?
                      :intentional? boolean?
                      :foreseeable? boolean?}
 :interventions      {:self   [kw]
                      :others [kw]}
 :temporal-status    #{:past :present :future}
 :relation-frame     #{:self :other :shared}}
```

### AppraisalFrame (authoritative per-cycle evaluation)

```clojure
AppraisalFrame
{:id                 uuid
 :causal-slice-id    uuid
 :perspective-agent  kw
 :desirability       -1.0..1.0
 :likelihood         0.0-1.0
 :controllability    0.0-1.0
 :changeability      0.0-1.0
 :praiseworthiness   -1.0..1.0
 :appealingness      -1.0..1.0?   ;; mostly nil in v1
 :familiarity        0.0-1.0?     ;; mostly nil in v1
 :expectedness       0.0-1.0
 :temporal-status    #{:past :present :future}
 :realization-status #{:actual :prospective :confirmed :disconfirmed}}
```

### EmotionVector (derived view, not source of truth)

```clojure
EmotionVector
{:hope               0.0-1.0
 :fear               0.0-1.0
 :joy                0.0-1.0
 :distress           0.0-1.0
 :anger              0.0-1.0
 :remorse            0.0-1.0
 :shame              0.0-1.0
 :resentment         0.0-1.0
 :relief             0.0-1.0
 :disappointment     0.0-1.0
 :admiration         0.0-1.0
 :gratitude          0.0-1.0
 :arousal            0.0-1.0
 :surprise           0.0-1.0}
```

### SocialPracticeInstance (persistent social situation state)

Reference only. Do not build this whole object in `v1`; use
`PracticeContextV1` above.

```clojure
SocialPracticeInstance
{:id                 uuid
 :practice-type      #{:confrontation :evasion :confession
                       :status-repair :alliance}
 :phase              kw
 :roles              {kw kw}      ;; :accuser -> :kai, :accused -> :maren
 :norms              [kw]
 :persistent-data    map
 :spawned-by         uuid?
 :active?            boolean}
```

### Affordance

Reference only. In `v1`, keep affordances lightweight and aligned to the
three canonical families.

```clojure
Affordance
{:id                 uuid
 :practice-id        uuid
 :role               kw
 :label              kw
 :preconditions      [kw]
 :style-tags         [kw]         ;; :reluctant :cutting :deflecting
 :operator-bias      {:rehearsal 0.0
                      :reversal 0.0
                      :rationalization 0.0
                      :roving 0.0}
 :effects            {:policy-delta   map
                      :salience-delta map
                      :ontic-delta    map}}
```

### OperatorCandidate

Reference only. In `v1`, the family set should be narrowed to
`rehearsal`, `rationalization`, and `avoidance`.

```clojure
OperatorCandidate
{:family             #{:rehearsal :reversal :rationalization :roving}
 :score              number
 :score-breakdown    {:pressure            number
                      :appraisal-fit       number
                      :practice-fit        number
                      :episodic-resonance  number
                      :novelty-penalty     number}
 :bound-affordance-ids [uuid]}
```

### GeneratedNodeProvenance

```clojure
GeneratedNodeProvenance
{:source-concern-ids   [uuid]
 :dominant-concern     kw
 :appraisal-signature  {:desirability bucket
                        :likelihood bucket
                        :controllability bucket
                        :attribution kw
                        :temporal-status kw}
 :emotion-signature    {:top [(kw float) ...]}
 :practice-signature   {:type kw :phase kw :role kw}
 :family               kw
 :commit-type          #{:ontic :policy :salience :none}}
```

---

## Transition function

```clojure
(defn l2-step [world character memory]
  (let [concern   (select-dominant-concern world character memory)
        causal    (build-causal-slice world character concern)
        app       (ema-appraise causal)
        emo       (occ-project app)
        practices (activate-practices world character concern app)
        affs      (enumerate-affordances practices character)
        ops       (score-operators concern app emo affs memory)
        proposal  (generate-node world character concern app emo affs ops)
        valid?    (validate-node world proposal)
        proposal' (if valid? proposal (mutate-and-regenerate proposal))
        commit    (choose-commit proposal')
        world'    (apply-commit world commit)
        concerns' (reappraise world' (affected-concerns concern proposal' commit))]
    {:world world'
     :concerns concerns'
     :node (attach-provenance proposal' concern app emo practices ops commit)}))
```

---

## Operator scoring formula

```
score(family) = w_p · pressure
              + w_a · appraisal-fit
              + w_s · practice-fit
              + w_e · episodic-resonance
              - w_r · repetition-penalty
```

This matches the existing Mueller-derived control logic, the
settled architecture's operator layer, and the revised
authoring-time generation cycle.

---

## Recommended prototype scope

### Include in v1

- Concerns: `attachment_threat`, `retaliation_pressure`, `obligation`
- EMA vars: desirability, likelihood, controllability, attribution,
  temporal status
- OCC emotions: hope, fear, distress, anger, remorse, relief,
  disappointment
- Practices: evasion, anticipated-confrontation, confession
- Families: rehearsal, rationalization, avoidance

### Do NOT include in v1

- Full OCC runtime taxonomy
- Object-based emotions (love/hate/interest/disgust)
- Full Soar-like causal model
- Versu's full Praxis logic
- ATMS assumption management
- Multi-character social practice machinery
- Conductor integration

---

## Four-source decomposition

The 5 Pro endorsed this framing:

| Source | Role | What it provides |
|--------|------|-----------------|
| Mueller | Control loop | "What thought process runs next" |
| EMA | Dynamics | "Why this pressure changed" |
| OCC | Semantics | "What kind of emotional state this is" |
| Versu | Situation structure | "What move is socially available here" |

---

## Nitpicks from 5 Pro

1. **OCC does not give you "concern deltas."** It gives emotion
   categories and intensity structure. Concern updates are runtime
   design, driven by reappraisal after commit. Keep that distinction
   clean.

2. **Curiosity is not covered by OCC.** Treat it as an epistemic
   concern with its own intensity logic, probably tied to
   uncertainty, novelty, blocked inference, or information gain.

3. **Obligation and retaliation are composite.** Obligation is
   mixed hope/fear. Retaliation pressure is reproach + distress +
   available retaliatory affordance. The node generator should show
   mixed states, not collapse everything into one label.

---

## Next move (per 5 Pro)

The best next move is a worked trace, not more abstraction:

```
unopened letter
  → obligation + attachment_threat
  → CausalSliceV1
  → AppraisalFrame
  → PracticeContextV1
  → operator score breakdown
  → generated node provenance
  → derived EmotionVector
  → reappraisal after commit
```

That will show fast whether the middle layer is earning its keep
or just producing elegant paperwork.
