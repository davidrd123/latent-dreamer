# control.clj — Annotated Tutorial

Read this on your iPad. Each function is explained with the data
shapes flowing through it.

---

## The Big Picture

This file is Mueller's main control loop. Every cycle of inner life
runs through here. The loop is:

```
decay needs → decay emotions → bump cycle counter →
maybe activate family goals → pick the strongest goal →
advance one planning step → record what happened
```

The entire system's state lives in one big map called `world`. Every
function takes `world` in and returns a new `world` out. Nothing is
mutated. You're always building a new version of the world.

---

## The world map (what flows through everything)

```clojure
;; This is the shape of `world`. Every function here reads from
;; and writes to this same map.

{:contexts  {;; The planning tree. Each context is a hypothetical
             ;; branch the system is exploring.
             :cx-1 {:id :cx-1
                     :parent-id nil        ; root has no parent
                     :add-obs #{...}       ; facts asserted here
                     :remove-obs #{...}    ; facts retracted here
                     :rules-run? false     ; has this branch been processed?
                     :ordering 1.0         ; priority for backtracking
                     :timeout nil}         ; cycles before auto-backtrack
             :cx-2 {:id :cx-2
                     :parent-id :cx-1      ; child of cx-1
                     ...}}

 :goals     {;; Active concerns. Each goal has a type, strength,
             ;; and a pointer to its current planning context.
             :g-1 {:id :g-1
                    :goal-type :reversal   ; which family
                    :planning-type :imaginary  ; real or imaginary
                    :strength 0.7          ; emotional motivation
                    :status :runable       ; :runable :waiting :halted :failed :succeeded
                    :main-motiv :e-shame   ; the emotion driving this
                    :next-cx :cx-2}        ; where planning is happening
             :g-2 {:id :g-2
                    :goal-type :roving
                    :strength 0.3
                    ...}}

 :emotions  {;; Current emotional state. Strength decays each cycle
             ;; unless the emotion is motivating an active goal.
             :e-shame {:strength 0.7
                        :valence :negative
                        :affect :shame}
             :e-hope  {:strength 0.3
                        :valence :positive
                        :affect :hope}}

 :needs     {;; Background drives. Decay slowly (0.98/cycle).
             :n-belonging {:strength 0.6}
             :n-esteem    {:strength 0.4}}

 :episodes  {...}  ; episodic memory (see episodic_memory.clj)
 :mode      :daydreaming  ; or :performance
 :cycle     0              ; increments each tick
 :trace     []             ; log of what happened each cycle

 ;; Event accumulators (reset each cycle by prepare-cycle-events)
 :retrieval-events []
 :backtrack-events []
 :activation-events []
 :mutation-events []
 :termination-events []}
```

---

## Constants

```clojure
(def ^:private need-decay-factor 0.98)
;; Needs lose 2% per cycle. Slow fade.
;; After 50 cycles: 0.98^50 = 0.36 of original.

(def ^:private emotion-decay-factor 0.95)
;; Emotions lose 5% per cycle. Faster fade.
;; After 20 cycles: 0.95^20 = 0.36 of original.
;; BUT: emotions that motivate an active goal don't decay.

(def ^:private emotion-gc-threshold 0.15)
;; Below this strength, the emotion is removed entirely.
;; Prevents the map from filling with near-zero ghosts.
```

---

## maybe-activate-family-goals

```clojure
(defn- maybe-activate-family-goals
  [world]
  (if (:auto-activate-family-goals? world)
    (families/activate-family-goals world)
    world))
```

**What it does:** If the world has `:auto-activate-family-goals?`
set to true, scan for failed goals with negative emotions and
activate daydreaming families (rationalization, reversal, roving).
Otherwise return world unchanged.

**Shape in:** `world` map
**Shape out:** `world` map, possibly with new goals added to `:goals`
and new events in `:activation-events`

The `defn-` (with the dash) means this is private to the namespace.
Can't be called from outside.

---

## set-state

```clojure
(defn set-state [world mode]
  (when-not (contains? #{:suspended :performance :daydreaming} mode)
    (throw (ex-info "Invalid control state" {:mode mode})))
  (assoc world :mode mode))
```

**What it does:** Change the world's mode. Only three values allowed.
Throws an error if you try anything else.

**`assoc`** means "return a new map with this key set to this value."
The original `world` is unchanged (immutable data).

**`#{:suspended :performance :daydreaming}`** is a set literal.
`contains?` checks membership.

---

## need-decay

```clojure
(defn need-decay [world]
  (update world :needs
          (fn [needs]
            (into {}
                  (map (fn [[need-id need]]
                         [need-id
                          (update need :strength
                                  (fn [strength]
                                    (* need-decay-factor
                                       (or strength 0.0))))]))
                  needs))))
```

**What it does:** Walk every need, multiply its strength by 0.98.

**Reading the pattern:**

`(update world :needs (fn [needs] ...))` means: take world's
`:needs` value, transform it with this function, put the result back.

`needs` coming in looks like:
```clojure
{:n-belonging {:strength 0.6}
 :n-esteem    {:strength 0.4}}
```

`(into {} (map ...) needs)` iterates over the map as pairs:
```clojure
;; Each pair is [key value]:
[:n-belonging {:strength 0.6}]
[:n-esteem    {:strength 0.4}]
```

The `map` function transforms each pair. The `[need-id need]`
destructures the pair into its key and value. Then:
```clojure
(update need :strength (fn [strength] (* 0.98 (or strength 0.0))))
```
Multiplies the strength by 0.98. The `(or strength 0.0)` handles
the case where strength might be nil.

**Shape out:** same structure, smaller numbers:
```clojure
{:n-belonging {:strength 0.588}   ; was 0.6
 :n-esteem    {:strength 0.392}}  ; was 0.4
```

---

## emotion-decay

```clojure
(defn emotion-decay [world]
  (update world :emotions
          (fn [emotions]
            (into {}
                  (comp
                   (map ...)     ; step 1: decay non-motivating emotions
                   (remove ...)) ; step 2: garbage collect weak ones
                  emotions))))
```

**Same pattern as need-decay but with two steps composed together.**

**Step 1 (map):** For each emotion, check if it's motivating a goal.
If yes, leave it alone. If no, multiply strength by 0.95.

```clojure
;; Before:
{:e-shame {:strength 0.7 :valence :negative}   ; motivating :g-1
 :e-hope  {:strength 0.3 :valence :positive}   ; not motivating anything
 :e-fear  {:strength 0.1 :valence :negative}}  ; not motivating anything

;; After step 1:
{:e-shame {:strength 0.7 ...}    ; UNCHANGED (motivating a goal)
 :e-hope  {:strength 0.285 ...}  ; decayed: 0.3 * 0.95
 :e-fear  {:strength 0.095 ...}} ; decayed: 0.1 * 0.95
```

**Step 2 (remove):** Drop any emotion below 0.15.

```clojure
;; After step 2:
{:e-shame {:strength 0.7 ...}    ; kept (above threshold)
 :e-hope  {:strength 0.285 ...}  ; kept (above threshold)
 ;; :e-fear is GONE (0.095 < 0.15)
```

**`(comp step1 step2)`** composes two transformations into one pass.
In Clojure transducer composition, they run in declaration order:
step1 first, then step2.

**Why this matters:** Shame stays strong because it's driving
reversal. Hope fades slowly. Fear disappears. Next cycle, the goal
selection will be even more dominated by shame, because hope is
weaker and fear is gone. This is how pressure dynamics evolve.

---

## wake-waiting-real-goals

```clojure
(defn- wake-waiting-real-goals [world]
  (update world :goals
          (fn [goal-map]
            (into {}
                  (map (fn [[goal-id goal]]
                         [goal-id
                          (if (and (= (:status goal) :waiting)
                                   (= (:planning-type goal) :real))
                            (assoc goal :status :runable)
                            goal)]))
                  goal-map))))
```

**What it does:** Find goals that are `:waiting` and `:real` (not
imaginary daydreams), and wake them up to `:runable`.

This happens when the system switches from daydreaming mode back
to performance mode. Real-world goals that were waiting get a
chance to run again.

---

## prepare-cycle-events

```clojure
(defn- prepare-cycle-events [world]
  (assoc world
         :retrieval-events []
         :backtrack-events []
         :activation-events []
         :mutation-events []
         :termination-events []))
```

**What it does:** Reset all event accumulators to empty vectors.
Each cycle starts with a clean slate. As the cycle runs, events
get `conj`'d onto these vectors. At the end, they're recorded
in the trace.

---

## prune-possibilities

```clojure
(defn prune-possibilities [world context-ids]
  (->> context-ids
       (map #(get-in world [:contexts %]))
       (remove nil?)
       (remove (fn [{:keys [rules-run? dd-goal-sprout?]}]
                 (or rules-run? dd-goal-sprout?)))
       (sort-by (juxt (comp - :ordering) (comp str :id)))
       (map :id)
       vec))
```

**What it does:** Given a list of context IDs (hypothetical
branches), filter down to the ones worth exploring.

**The `->>` threading macro** pipes data through each step:

```clojure
;; Start: [:cx-3 :cx-4 :cx-5]

;; Step 1: look up each context in the world
;; → [{:id :cx-3 :rules-run? true :ordering 0.8}
;;    {:id :cx-4 :rules-run? false :ordering 0.9}
;;    {:id :cx-5 :rules-run? false :ordering 0.7 :dd-goal-sprout? true}]

;; Step 2: remove nils (safety)

;; Step 3: remove already-processed or dedicated sprouts
;; → [{:id :cx-4 :ordering 0.9}]
;;   (cx-3 already ran, cx-5 is a dedicated sprout)

;; Step 4: sort by ordering (highest first), break ties by id
;; → [{:id :cx-4 :ordering 0.9}]

;; Step 5: extract just the ids
;; → [:cx-4]

;; Step 6: make it a vector
;; → [:cx-4]
```

**`(juxt f g)`** makes a function that returns `[(f x) (g x)]`.
Used for multi-key sorting. `(comp - :ordering)` negates the
ordering so higher values sort first.

---

## terminate-top-level-goal

```clojure
(defn terminate-top-level-goal
  ([world goal-id resolution-cx]
   (terminate-top-level-goal world goal-id resolution-cx {}))
  ([world goal-id resolution-cx {:keys [status result-fact]
                                 :or {status :terminated}}]
   ...))
```

**What it does:** End a goal's life. Mark it as terminated (or
failed or succeeded). Optionally assert a result fact into the
reality context. If reality exists, sprout a new stable branch.

**Multi-arity:** The two argument lists mean you can call it with
3 or 4 args. The 3-arg version defaults to `{}` for the options.

**`{:keys [status result-fact] :or {status :terminated}}`** is
destructuring with defaults. Pull `status` and `result-fact` out
of the options map. If `status` is missing, default to `:terminated`.

**`cond->`** is conditional threading. Only applies the next form
if the test is truthy:
```clojure
(cond-> world
  result-fact                    ; if result-fact is not nil...
  (cx/assert-fact ... result-fact))  ; ...then assert it
```
If `result-fact` is nil, the assertion is skipped.

**`(fnil conj [])`** means: if the value is nil, treat it as `[]`
before applying `conj`. So `(update m :events (fnil conj []) x)`
always works even if `:events` doesn't exist yet.

---

## backtrack-top-level-goal

```clojure
(defn backtrack-top-level-goal
  ([world goal-id current-cx]
   (backtrack-top-level-goal world goal-id current-cx :exhausted))
  ([world goal-id current-cx reason]
   (loop [world world
          next-cx current-cx]
     ...)))
```

**What it does:** The current planning branch is stuck. Walk up the
context tree looking for an unexplored sibling branch to try.

**`loop/recur`** is Clojure's explicit iteration. The `loop` sets
up bindings (like a `let`), and `recur` jumps back to the loop
head with new values. No stack growth.

**The algorithm:**
```
Start at current context.
Is this the backtrack wall (the root of this goal's planning)?
  YES → all possibilities failed. Terminate the goal.
  NO  → Go up to parent. Check siblings.
         Any unexplored siblings?
           YES → Pick the best one. Continue planning there.
           NO  → Recur: go up another level.
```

**Shape out:** `[world next-context-id]` where next-context-id
is either the new branch to explore or nil (goal failed).

---

## run-goal-step

```clojure
(defn run-goal-step [world goal-id]
  (let [current-cx (goals/get-next-context world goal-id)
        timeout (get-in world [:contexts current-cx :timeout])
        world (assoc-in world [:contexts current-cx :rules-run?] true)]
    (cond
      (and (number? timeout) (<= timeout 0))
      (backtrack-top-level-goal world goal-id current-cx :timeout)

      (= :fired-halt ...)
      ...

      (contains? #{:runable :halted} ...)
      ...

      :else
      ...)))
```

**What it does:** After planning has run in a context, decide what
to do next. Four cases:

1. **Timeout expired** → backtrack
2. **Goal fired a halt signal** → stop, mark halted
3. **Goal is runnable** → look for child branches to explore,
   or backtrack if there are none
4. **Anything else** → do nothing (goal is already done)

**`cond`** is Clojure's multi-branch conditional. Each pair is
`test expression`. First truthy test wins.

---

## run-cycle — THE MAIN FUNCTION

```clojure
(defn run-cycle [world]
  (let [world (-> world
                  prepare-cycle-events   ; reset event accumulators
                  need-decay             ; needs * 0.98
                  emotion-decay          ; emotions * 0.95, gc < 0.15
                  (update :cycle (fnil inc 0))  ; bump cycle counter
                  maybe-activate-family-goals)  ; check for new daydream goals
        candidates (goals/most-highly-motivated-goals world)]
    (cond
      (seq candidates)        ; case 1: there are runnable goals
      ...

      (performance-mode? world)  ; case 2: no goals in performance mode
      ...                        ;         → switch to daydreaming

      :else                   ; case 3: no goals in daydreaming mode
      ...)))                  ;         → switch to performance
```

**What it does:** One tick of inner life.

**The `->` threading macro** pipes `world` through five
transformations in order. Each one takes world in and returns
world out:

```clojure
;; Read this as:
;; 1. Start with world
;; 2. Reset event accumulators
;; 3. Decay needs
;; 4. Decay emotions
;; 5. Increment cycle counter
;; 6. Maybe activate family goals
;;
;; The result of step 6 is bound to `world`.
```

**Then three cases:**

**Case 1: Goals exist.** Pick the strongest. Record it in the trace.
Return `[world goal-id]`.

**Case 2: No goals, in performance mode.** Nothing real to do.
Switch to daydreaming mode (maybe imaginary concerns will activate).
Return `[world nil]`.

**Case 3: No goals, in daydreaming mode.** Wake any waiting real
goals and switch to performance mode. Return `[world nil]`.

**This is Mueller's mode oscillation.** The system bounces between
performance (dealing with real-world goals) and daydreaming (running
imaginary concerns like rationalization and reversal). When one mode
runs out of goals, it switches to the other.

**Shape out:** `[world selected-goal-id]` where goal-id is the
concern that won this cycle, or nil if nothing was selected.

---

## How this connects to the rest of the system

The benchmark or conductor calls `run-cycle` each tick. If a goal
is selected, it then calls `run-goal-step` to advance planning.
The family plans (rationalization, reversal, roving) are invoked
through `goal-families/activate-family-goals` during the cycle,
and their plan bodies run through the executor boundary.

The trace recorded each cycle is what the cognitive visualizer
and the runtime thought projector read to produce inner-life
prose and the situation landscape display.
