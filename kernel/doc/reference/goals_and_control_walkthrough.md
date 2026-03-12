# Goals and Control — Walkthrough

A walkthrough of `goals.clj` and `control.clj` in the daydreamer-kernel.
Assumes you have read `clojure_for_schemers.md` (syntax, maps, threading)
and the `context.clj` walkthrough (world-threading, sprouting).

This document covers the goal representation, emotion-driven goal
competition, and the control loop that oscillates between performance
and daydreaming modes. All of these recover mechanisms from Mueller's
`dd_cntrl.cl`.

---

## Real vs Imaginary Goals

The single most important distinction in DAYDREAMER's architecture.

**Real goals** are grounded in the current situation. They operate on
the existing context -- the world as it is. When you activate a real
goal, it starts planning from wherever the system currently stands.
Examples: RECOVERY (try to fix what went wrong), REHEARSAL (practice
achieving something you want).

**Imaginary goals** create a hypothetical sandbox. When activated, they
sprout a fresh planning context off the current one and do all their
work inside that branch. The sprout point becomes the "backtrack wall"
-- the boundary the planner cannot retreat past. Examples: REVERSAL
(rewrite the past), RATIONALIZATION (reinterpret a failure),
ROVING (wander through pleasant memories).

This distinction drives two architectural decisions:

1. **Goal activation** -- imaginary goals call `cx/sprout` to create
   their sandbox; real goals just point at the current context.
2. **Goal competition** -- in performance mode, imaginary goals are
   excluded from selection. The system only daydreams when there is
   nothing real to do.

The Python engine has goal-type labels but no actual planning contexts
behind them. This is the gap the kernel fills.

---

## goals.clj

### `create-goal` — Opts map with `:or` defaults

```clojure
(defn create-goal
  [{:keys [id goal-type planning-type status strength main-motiv
           activation-cx termination-cx next-cx backtrack-wall
           top-level-goal]
    :or {id :g-1
         planning-type :real
         status :runable
         strength 0.0}}]
  {:id id
   :goal-type goal-type
   ...
   :top-level-goal (or top-level-goal id)})
```

The function takes a single map argument and destructures it. The
`:or` clause provides defaults for keys not present in the input map.
This is Clojure's answer to keyword arguments with defaults.

In Scheme you would do something like:

```scheme
(define (create-goal . opts)
  (let ((id (or (assoc-ref opts 'id) 'g-1))
        (planning-type (or (assoc-ref opts 'planning-type) 'real))
        ...))
```

The Clojure version does the same thing declaratively in the parameter
list itself. The `:keys` vector names which keys to extract; the `:or`
map names which get defaults. Any key not mentioned in `:or` defaults
to `nil`.

Note the last line: `(or top-level-goal id)`. A top-level goal is its
own top-level-goal (self-pointer). Subgoals would point to their parent.
The `or` here means "use what was passed, or fall back to the goal's
own id."

### `activate-top-level-goal` — The birth of a goal

```clojure
(defn activate-top-level-goal
  [world context-id goal-spec]
  (let [planning-type (:planning-type goal-spec :real)
        [world goal-id] (next-goal-id world)
        [world activation-cx backtrack-wall next-cx]
        (if (= planning-type :imaginary)
          (let [[world sprout-id] (cx/sprout world context-id)]
            [world sprout-id sprout-id sprout-id])
          [world context-id nil nil])
        goal (create-goal (assoc goal-spec ...))]
    [(assoc-in world [:goals goal-id] goal) goal-id]))
```

The key pattern here is the conditional multi-value return. The `if`
expression returns a 4-element vector `[world activation-cx
backtrack-wall next-cx]` regardless of which branch runs. Both branches
produce the same shape; the caller destructures it the same way.

For imaginary goals, all three context pointers start at the sprout.
The sprout is simultaneously where planning begins (activation-cx),
how far backtracking can go (backtrack-wall), and where the next
planning step happens (next-cx). As planning progresses, next-cx
advances while backtrack-wall stays put.

For real goals, activation-cx is the context that was passed in, and
backtrack-wall and next-cx are both nil. Real goals don't maintain
their own planning frontier -- they operate directly in the shared
context.

### `motivating-emotion?` — `some` with `boolean`

```clojure
(defn motivating-emotion?
  [world emotion-id]
  (boolean
   (some #(= emotion-id (:main-motiv %))
         (vals (:goals world)))))
```

`some` walks a collection and returns the first truthy result of
applying the predicate, or `nil` if nothing matches. This is an
existence check: "does any goal in the world have this emotion as
its main motivation?"

The `boolean` wrapper coerces `some`'s return value. Without it, the
function would return `true` (the result of `=`) on match and `nil`
on no match. Clojure treats `nil` as falsy, so this usually doesn't
matter in conditionals, but `boolean` normalizes the return to
`true`/`false`. It is a stylistic choice -- it makes the function
contract explicit rather than relying on nil-punning.

Mueller context: this function determines which emotions are "load-
bearing." An emotion that motivates a goal is protected from decay.
An emotion nobody cares about fades away. This is how DAYDREAMER
implements the cognitive principle that emotions persist as long as
they are relevant.

### `most-highly-motivated-goals` — Thread-last pipeline

```clojure
(defn most-highly-motivated-goals
  [world]
  (let [eligible-goals (->> (vals (:goals world))
                            (filter (fn [{:keys [status planning-type]}]
                                      (and (= status :runable)
                                           (or (not= (:mode world) :performance)
                                               (not= planning-type :imaginary))))))
        highest-strength (reduce max 0.0 (map :strength eligible-goals))]
    (->> eligible-goals
         (filter #(= (:strength %) highest-strength))
         (map :id)
         (sort-by str)
         vec)))
```

Two `->>` pipelines. The first builds the candidate set; the second
selects winners from it.

The filter predicate in the first pipeline encodes the mode rule:
a goal is eligible if it is `:runable` AND either the system is not
in performance mode OR the goal is not imaginary. In performance mode,
imaginary goals are invisible to the selector.

`(reduce max 0.0 ...)` finds the maximum strength. The `0.0` seed
handles the empty case -- if there are no eligible goals, the max is
0.0 and the second filter will also produce nothing.

The final `(sort-by str)` and `vec` make the output deterministic.
When multiple goals tie at the same strength, they are returned in
keyword-alphabetical order as a vector. The tests rely on this
stability.

Mueller context: this is the goal competition mechanism from
`most-highly-motivated-goals` in `dd_cntrl.cl`. The strongest runnable
goal wins each cycle. Ties are possible and the control loop just
picks the first one. The mode filter is what makes performance mode
"serious" -- it ignores daydreams.

---

## control.clj

### Constants

```clojure
(def ^:private need-decay-factor 0.98)
(def ^:private emotion-decay-factor 0.95)
(def ^:private emotion-gc-threshold 0.15)
```

These are Mueller's original values from `dd_epis.cl`. `^:private` is
metadata that makes the var namespace-private (same effect as `defn-`
for functions). Needs decay slowly (2% per cycle). Emotions decay
faster (5% per cycle) and get garbage-collected when they fall below
0.15.

### `set-state` — Mode validation

```clojure
(defn set-state
  [world mode]
  (when-not (contains? #{:suspended :performance :daydreaming} mode)
    (throw (ex-info "Invalid control state"
                    {:mode mode})))
  (assoc world :mode mode))
```

A guard-then-assign pattern. `when-not` evaluates the body only when
the condition is false -- it's the "unless" of Clojure. The set literal
`#{:suspended :performance :daydreaming}` acts as a membership test
via `contains?`. If the mode is invalid, throw. Otherwise, return a
new world with the mode set.

Mueller context: the three modes map directly to Mueller's control
states. `:performance` means the system is doing real-world tasks.
`:daydreaming` means it has idle cycles and is exploring imaginary
goals. `:suspended` pauses everything.

### `need-decay`

```clojure
(defn need-decay
  [world]
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

`update` applies a function to one key in a map. Here it replaces
`:needs` with a transformed version of itself.

The transformation uses `into` with a transducer. `(into {} xform coll)`
pours `coll` through `xform` and collects the results into a map.
The `map` here is being used as a transducer (passed to `into` as the
second argument, before the collection). Each need entry `[need-id need]`
is mapped to `[need-id updated-need]`, and `into {}` reassembles the
map.

The `(or strength 0.0)` guards against nil strengths -- if a need
has no strength yet, treat it as zero rather than crashing on `(* 0.98 nil)`.

### `emotion-decay` — The `comp` transducer pattern

```clojure
(defn emotion-decay
  [world]
  (update world :emotions
          (fn [emotions]
            (into {}
                  (comp
                   (map (fn [[emotion-id emotion]]
                          [emotion-id
                           (if (goals/motivating-emotion? world emotion-id)
                             emotion
                             (update emotion :strength
                                     (fn [strength]
                                       (* emotion-decay-factor
                                          (or strength 0.0)))))]))
                   (remove (fn [[_ {:keys [strength]}]]
                             (< (or strength 0.0) emotion-gc-threshold))))
                  emotions))))
```

This is the most interesting function mechanically. It does two things
in a single pass over the emotions map:

1. **Decay**: multiply non-motivating emotion strengths by 0.95
2. **Garbage collect**: remove emotions that fell below 0.15

The `comp` combines two transducers into one pipeline. Data flows
through `map` first (decay), then through `remove` (GC). This is a
single traversal -- the map entry is decayed and then immediately
checked against the threshold, without building an intermediate
collection.

Transducer composition with `comp` reads in application order: `map`
runs first, `remove` runs second. This is the opposite of normal
function composition, where `(comp f g)` means "apply g then f." With
transducers the convention is reversed because transducers wrap each
other -- the leftmost transducer is the outermost wrapper, which
processes input first.

The `motivating-emotion?` check is the key Mueller mechanism. If an
emotion is the `main-motiv` of any active goal, it is exempt from
decay. This means emotions that drive active goals stay at full
strength until the goal terminates. Emotions without purpose fade.

This is how DAYDREAMER implements "emotions that matter are protected."
A negative emotion from a failed goal stays strong as long as a
REVERSAL or RATIONALIZATION goal is trying to address it. Once the
goal terminates (success or failure), the emotion loses its protection
and begins to decay. If no new goal picks it up, it eventually falls
below 0.15 and gets garbage-collected -- the system has moved on.

### `wake-waiting-real-goals` — Private helper

```clojure
(defn- wake-waiting-real-goals
  [world]
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

Flips all waiting real goals back to `:runable`. This only fires when
the system transitions from daydreaming back to performance mode (see
`run-cycle` below). The idea: real goals were put on hold while the
system daydreamed. Now that it is back in performance mode, they wake
up and compete again.

Imaginary goals are not woken. They stay waiting (or halted) because
they belong to the daydreaming world and will get their chance when
the system next enters daydreaming mode.

### `run-cycle` — The control loop

```clojure
(defn run-cycle
  [world]
  (let [world (-> world
                  need-decay
                  emotion-decay
                  (update :cycle (fnil inc 0)))
        candidates (goals/most-highly-motivated-goals world)]
    (cond
      (seq candidates)
      [world (first candidates)]

      (performance-mode? world)
      [(set-state world :daydreaming) nil]

      :else
      [(-> world
           wake-waiting-real-goals
           (set-state :performance))
       nil])))
```

This is a partial implementation of Mueller's `daydreamer-control0`.
Each call is one tick of the cognitive clock.

**Step 1: Decay and tick.**
Thread the world through `need-decay`, `emotion-decay`, and a cycle
counter increment. `(fnil inc 0)` means "increment the cycle counter,
and if it doesn't exist yet, start from 0." `fnil` wraps `inc` so
that `(inc nil)` becomes `(inc 0)` instead of throwing.

**Step 2: Select.**
Ask `most-highly-motivated-goals` for candidates. This already
respects the mode filter (performance mode excludes imaginary goals).

**Step 3: Dispatch.**
Three cases via `cond`:

- **Candidates exist**: return the world and the first candidate.
  `(seq candidates)` is the idiomatic Clojure emptiness check --
  `seq` returns `nil` on an empty collection, which is falsy.

- **No candidates, in performance mode**: nothing real to do. Switch
  to daydreaming mode. Return nil for the selected goal (no work
  this cycle, just a mode switch).

- **No candidates, in daydreaming mode** (the `:else` case): nothing
  imaginary to do either. Wake up any waiting real goals, switch back
  to performance mode. The next cycle will see them as candidates.

The return value is always `[world goal-id-or-nil]`, following the
same world-threading-with-value convention from `context.clj`.

---

## Mode Oscillation

The three `cond` branches in `run-cycle` implement the oscillation:

```
performance ──(no real goals)──> daydreaming
                                     │
daydreaming ──(no imaginary goals)──> wake real goals
                                      └──> performance
```

The system bounces between modes based on what goals are available.
Performance mode is the default -- it handles real goals. When nothing
real is pending, the system drifts into daydreaming mode, where
imaginary goals become eligible. When daydreaming also runs out of
candidates, it wakes any waiting real goals and flips back.

This is the oscillation the Python engine lacks. The Python engine has
a mode flag but no actual behavioral difference -- all goals compete
equally regardless of mode. The kernel enforces the constraint that
performance takes priority, and daydreaming only happens in the gaps.

---

## What `run-cycle` Does Not Yet Do

Mueller's `daydreamer-control0` does more after selecting a goal:

- Run one planning step (`daydreamer-control1`)
- Check for goal success or failure
- Backtrack if the planning step hit a dead end
- Invoke mutation as a last resort
- Store episodes on termination
- Trigger emotional responses and remindings

The current `run-cycle` handles decay, selection, and mode switching.
The planning-step and termination logic is the next layer to build.
The `[world selected-goal-id]` return is the hook point -- the caller
will eventually pass the selected goal to a planning step function.

---

## Pattern Summary

| Pattern | Where | What it does |
|---|---|---|
| Opts map with `:or` | `create-goal` | Keyword arguments with defaults, declared in the parameter list |
| `->>` collection pipeline | `most-highly-motivated-goals` | Filter, transform, collect in reading order |
| `some` + `boolean` | `motivating-emotion?` | Existence check with explicit boolean return |
| `comp` transducer | `emotion-decay` | Fuse map + remove into a single pass |
| `fnil` | `run-cycle` | Nil-safe increment for missing counter |
| Conditional multi-return | `activate-top-level-goal` | Both `if` branches produce the same vector shape |
| `seq` as emptiness test | `run-cycle` | Idiomatic nil-on-empty for `cond` dispatch |
