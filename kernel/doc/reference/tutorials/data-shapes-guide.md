# Data Shapes Guide

Everything in the kernel is a plain Clojure map. No classes, no
objects, no hidden state. If you can read a map, you can read the
entire system.

This guide shows what each piece of data looks like, what it means,
and how pieces connect to each other.

---

## The world

The world is the single map that holds everything. Every function
takes it in and returns a new version of it. Nothing is mutated.

```clojure
{:contexts   {...}       ; the planning tree
 :goals      {...}       ; active concerns
 :emotions   {...}       ; current emotional state
 :needs      {...}       ; background drives
 :episodes   {...}       ; long-term memory
 :episode-index {...}    ; index -> set of episode ids
 :mode       :daydreaming  ; or :performance
 :cycle      7           ; increments each tick
 :trace      [...]       ; log of what happened
 :id-counter 42          ; for generating unique ids
 :recent-indices [...]   ; FIFO of recently active cues (max 6)
 :recent-episodes [...]} ; FIFO of recently seen episodes (max 4)
```

Each value inside the world is itself a map (or a vector, or a
set). The rest of this guide shows what those inner maps look like.

---

## Contexts (the planning tree)

A context is one hypothetical branch the system is exploring.
Contexts form a tree: each has a parent, and may have children.

```clojure
;; One context:
{:id         :cx-5
 :parent-id  :cx-2       ; who sprouted this branch (nil for root)
 :children   #{:cx-8 :cx-9}  ; branches sprouted from here
 :ancestors  [:cx-2 :cx-1]   ; path back to root

 ;; Facts visible in this branch:
 :all-obs    #{{:fact/type :situation :fact/id :s1_seeing_through}
               {:fact/type :emotion :emotion-id :e-shame :strength 0.7}
               {:fact/type :goal :goal-id :g-1 :status :failed}}
 :add-obs    #{{:fact/type :emotion :emotion-id :e-shame :strength 0.7}}
 :remove-obs #{}

 ;; Branch metadata:
 :ordering        0.85    ; priority when choosing which branch to explore
 :rules-run?      false   ; has this branch been processed this cycle?
 :pseudo-sprout?  false   ; true = attached to parent without inheriting facts
 :alternative-past? false ; true = a reversal "what if" branch
 :timeout         10}     ; cycles before auto-backtrack
```

**How facts work:** `:all-obs` is everything visible from this
branch (inherited from parent + added here - removed here).
`:add-obs` is what was newly asserted in THIS branch.
`:remove-obs` is what was retracted. Facts are plain maps with
a `:fact/type` key.

**The tree:** root context has `:parent-id nil`. Each `sprout`
creates a child context that inherits the parent's facts. The
system explores branches, backtracks when stuck, and tries
siblings.

---

## Goals (active concerns)

A goal is something the system is trying to do or process. Goals
compete for attention based on emotional strength.

```clojure
;; One goal:
{:id              :g-1
 :goal-type       :reversal    ; which family: :reversal, :roving,
                                ;   :rationalization, :rehearsal, :recovery
 :planning-type   :imaginary   ; :real (external) or :imaginary (daydream)
 :status          :runable     ; :runable, :waiting, :halted, :failed,
                                ;   :succeeded, :terminated
 :strength        0.7          ; how strongly this competes (from emotion)

 ;; What drives this goal:
 :main-motiv      :e-shame     ; the emotion powering this concern

 ;; Where planning happens:
 :activation-cx   :cx-3        ; context where the goal was born
 :next-cx         :cx-5        ; current planning branch
 :backtrack-wall  :cx-3        ; can't backtrack past here
 :termination-cx  nil          ; set when the goal ends

 ;; For family-specific tracking:
 :trigger-context-id    :cx-2
 :trigger-failed-goal-id :g-old
 :trigger-emotion-id    :e-shame
 :situation-id          :s1_seeing_through}
```

**Goal lifecycle:** A goal starts `:runable`, gets selected by the
control loop (highest `:strength` wins), runs one planning step,
and eventually terminates as `:succeeded`, `:failed`, or
`:terminated`.

**Connection to emotions:** `:main-motiv` points to an emotion ID.
As long as this goal is active, that emotion does not decay.
That's why shame stays strong while reversal is running.

---

## Emotions

Emotions are the pressure that drives everything. They decay each
cycle unless they're motivating an active goal.

```clojure
;; The emotions map:
{:e-shame {:strength 0.7
           :valence :negative    ; :positive or :negative
           :affect :shame        ; specific flavor
           :situation-id :s1_seeing_through}

 :e-hope  {:strength 0.3
           :valence :positive
           :affect :hope
           :goal-id :g-2         ; which goal created this emotion
           :source-emotion-id :e-shame  ; if derived from another
           :frame-id :rf-zone-mercy}}   ; if created by rationalization
```

**Decay:** Non-motivating emotions lose 5% per cycle. Below 0.15
strength, they're garbage collected (removed entirely). Motivating
emotions (linked to active goals via `:main-motiv`) keep their
strength.

**Created by families:** Rationalization creates hope emotions.
Reversal can create relief. Roving can reactivate old emotions
through reminding.

---

## Needs

Background drives that decay very slowly. Less important than
emotions for understanding the system, but they exist.

```clojure
{:n-belonging {:strength 0.6}
 :n-esteem    {:strength 0.4}}
```

Decay factor 0.98 per cycle (vs 0.95 for emotions). These fade
slowly and represent long-term motivational background.

---

## Episodes (long-term memory)

An episode is a stored experience, real or imagined. This is what
retrieval searches over.

```clojure
;; One episode:
{:id                  :ep-7
 :rule                :rationalization-plan  ; what created it
 :goal-id             :g-1
 :context-id          :cx-5

 ;; Evaluation (how good/real was this experience):
 :realism             0.7       ; how plausible (0-1)
 :desirability        0.5       ; how desirable (can be negative)

 ;; Retrieval mechanics:
 :indices             #{:honesty :performance :seeing_through}
 :plan-threshold      2         ; need 2 matching indices for planning retrieval
 :reminding-threshold 2         ; need 2 for reminding retrieval

 ;; Tree structure (episodes can have sub-episodes):
 :children            [:ep-8]
 :descendants         [:ep-7 :ep-8]

 ;; Newer fields from the membrane work:
 :admission-status    :provisional  ; :trace, :provisional, or :durable
 :evaluation          {:realism :plausible
                       :desirability :positive
                       :retention-class :payload-exemplar
                       :keep-decision :keep-exemplar}
 :rule-path           [:goal-family/rationalization-plan-dispatch]
 :edge-path           []
 :provenance          {:source :family-plan
                       :family :rationalization}
 :promotion-evidence  []         ; records of cross-family success
 :use-history         []         ; records of when/how this episode was used
 :outcome-stats       {:use-count 0
                       :successful-use-count 0
                       :same-family-use-count 0}
 :anti-residue-flags  #{}}       ; :stale, :backfired, :contradicted,
                                  ;   :same-family-loop
```

**How retrieval works:** The kernel has a set of "currently active
indices" (keywords like `:honesty`, `:performance`). It walks the
`:episode-index` to find episodes that share those indices. If an
episode's matching count reaches its threshold, it's retrieved.

**The membrane fields** (admission-status, promotion-evidence,
use-history, anti-residue-flags) are the newer work that prevents
self-reinforcing grooves. See the memory membrane diagram for how
these interact.

---

## Episode index

A reverse lookup from index keywords to episode IDs. This is how
retrieval finds episodes quickly.

```clojure
{:honesty       #{:ep-7 :ep-12}    ; these episodes are indexed under :honesty
 :performance   #{:ep-7}
 :seeing_through #{:ep-7 :ep-3}
 :harbor        #{:ep-3}}
```

When the active indices are `[:honesty :performance]`, the kernel
looks up both keys, counts how many times each episode appears,
and retrieves episodes that cross their threshold.

---

## Facts

Facts are plain maps that live inside contexts (in `:all-obs`,
`:add-obs`, `:remove-obs`). They're tagged by `:fact/type`.

```clojure
;; A situation:
{:fact/type :situation
 :fact/id   :s1_seeing_through}

;; An emotion:
{:fact/type :emotion
 :emotion-id :e-shame
 :strength 0.7
 :valence :negative}

;; A goal:
{:fact/type :goal
 :goal-id :g-1
 :top-level-goal :g-1
 :status :failed
 :activation-context :cx-3}

;; A dependency (links emotion to goal):
{:fact/type :dependency
 :from-id :e-shame
 :to-id :g-1}

;; A failure cause (for reversal):
{:fact/type :failure-cause
 :fact/id :fc_wall_closed
 :goal-id :g-1
 :priority 0.9
 :counterfactual-facts [{:fact/type :counterfactual
                          :fact/id :wall_was_open}]}

;; A rationalization frame:
{:fact/type :rationalization-frame
 :fact/id :rf-zone-mercy
 :goal-id :g-1
 :priority 0.91
 :reframe-facts [{:fact/type :rationalization
                   :fact/id :zone_is_mercy}
                  {:fact/type :rationalization
                   :fact/id :delay_is_faith}]}

;; An intends relation (goal decomposition):
{:fact/type :intends
 :from-goal-id :g-1
 :to-goal-id :g-1-sub
 :top-level-goal :g-1}
```

**Pattern:** Every fact has `:fact/type`. The rule matcher uses
this to filter quickly before attempting full unification. Most
facts also have either `:fact/id` or a type-specific ID key
(`:goal-id`, `:emotion-id`).

---

## Rules (RuleV1)

Rules are the newer layer. They're data that describes cognitive
behavior, making it searchable and graphable.

```clojure
{:id                :goal-family/roving-trigger
 :rule-kind         :inference       ; or :planning
 :mueller-mode      :inference-only  ; or :plan-only, :both

 ;; What must match for this rule to fire:
 :antecedent-schema [{:fact/type :goal
                       :goal-id '?failed-goal-id
                       :status :failed}
                      {:fact/type :emotion
                       :emotion-id '?emotion-id
                       :strength '?emotion-strength}
                      {:fact/type :dependency
                       :from-id '?emotion-id
                       :to-id '?failed-goal-id}]

 ;; What the rule produces:
 :consequent-schema [{:fact/type :goal-family-trigger
                       :goal-type :roving
                       :failed-goal-id '?failed-goal-id
                       :emotion-id '?emotion-id}]

 :plausibility      0.04    ; threshold / confidence

 ;; What state change this rule is supposed to accomplish:
 :denotation        {:intended-effect :activate-roving-daydream-goal
                     :failure-modes [:emotion-below-threshold]
                     :validation-fn nil}

 ;; How the result is computed:
 :executor          {:kind :instantiate  ; or :clojure-fn, :llm-backed
                     :spec {}}

 ;; For connection graph construction:
 :graph-cache       {:out-edge-bases [...]
                     :in-edge-bases [...]}

 ;; Where this rule came from:
 :provenance        {:book-anchors [:theme-roving]
                     :kernel-status :partial
                     :deployment-role :authored-core}}
```

**Logic variables:** The `'?something` symbols are logic variables.
When the matcher finds facts that fit the antecedent patterns, it
binds these variables (e.g., `'?failed-goal-id` binds to `:g-1`).
Those bindings then fill in the consequent schema to produce the
output.

**Three-layer split:** `:antecedent-schema` / `:consequent-schema`
is what the connection graph sees. `:denotation` is what the rule
is supposed to accomplish. `:executor` is how it actually runs.
The graph stays structural even when the executor calls an LLM.

---

## The trace

A vector of cycle snapshots. Each entry records what happened in
one tick.

```clojure
[{:cycle 1
  :mode :daydreaming
  :selected-goal {:id :g-1 :goal-type :rationalization :strength 0.7}
  :context-id :cx-5
  :retrievals [{:episode-id :ep-3 :marks 2 :threshold 2}]
  :activations [{:goal-id :g-1 :goal-type :rationalization
                  :emotion-id :e-shame :emotion-strength 0.7}]
  :mutations []
  :backtrack-events []
  :terminations []
  :selection {:policy :highest_strength}}

 {:cycle 2
  :mode :daydreaming
  :selected-goal {:id :g-2 :goal-type :reversal :strength 0.65}
  ...}]
```

This is what the cognitive visualizer and the runtime thought
projector read from.

---

## How the pieces connect

```
world
 ├── :contexts ──── tree of hypothetical branches
 │                   each contains :all-obs (set of facts)
 │                   facts have :fact/type tags
 │
 ├── :goals ──────── active concerns competing for attention
 │                   each points to an emotion (:main-motiv)
 │                   and a context (:next-cx)
 │
 ├── :emotions ───── pressure values that decay each cycle
 │                   motivating emotions don't decay
 │
 ├── :episodes ───── long-term memory
 │                   indexed by keywords for retrieval
 │                   newer ones carry admission state and provenance
 │
 ├── :episode-index  reverse lookup: keyword -> episode ids
 │
 ├── :mode ────────── :performance or :daydreaming
 │                    oscillates when one mode runs out of goals
 │
 └── :trace ──────── what happened each cycle (for viz/export)
```

**The flow each cycle:**

1. Emotions drive goal selection (strongest emotion's goal wins)
2. The winning goal's family plan runs in its context
3. The plan sprouts new contexts, asserts/retracts facts
4. Results get stored as episodes with indices
5. Those indices change what future retrieval finds
6. Changed retrieval changes which goal wins next

Everything is a map. Maps contain maps. Maps point to other maps
by keyword ID. The world map is the root. Follow the keywords to
trace any connection.
