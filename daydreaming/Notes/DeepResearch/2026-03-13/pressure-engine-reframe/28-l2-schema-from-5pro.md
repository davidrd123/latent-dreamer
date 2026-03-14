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

## Schema

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
- Practices: confrontation, evasion, confession
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
  → CausalSlice
  → AppraisalFrame
  → EmotionVector
  → evasion practice affordances
  → operator score breakdown
  → generated node provenance
  → reappraisal after commit
```

That will show fast whether the middle layer is earning its keep
or just producing elegant paperwork.
