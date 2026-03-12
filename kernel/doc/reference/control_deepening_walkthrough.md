# Control Deepening — Walkthrough

A walkthrough of the new functions added to `control.clj` in the
deepening pass. Assumes you have read `clojure_for_schemers.md`,
`goals_and_control_walkthrough.md` (decay, mode switching, goal
selection), and `episodic_memory_walkthrough.md` (loop/recur, if-let).

The first pass gave us the outer shell: decay needs and emotions,
select the strongest goal, oscillate between performance and
daydreaming modes. This pass adds the inner machinery: what happens
*after* a goal is selected. The system can now advance through a
context tree, backtrack when stuck, and terminate goals.

---

## The Context Tree — A Mental Model

Before diving into the functions, you need the picture they operate on.
When a goal activates, it gets a planning context. As the planner
explores options (in a future wave), it sprouts child contexts --
branches representing alternative plans. The result is a tree:

```
    backtrack-wall (activation-cx)
    ├── cx-A  (rules-run? true, explored)
    ├── cx-B  ← next-cx (current frontier)
    │   ├── cx-B1
    │   └── cx-B2
    └── cx-C  (dd-goal-sprout? true, reserved)
```

The goal's `next-cx` pointer marks where the planner last worked.
The `backtrack-wall` marks how far up backtracking is allowed to go --
for imaginary goals, this is the sprout point; the goal cannot retreat
past its own sandbox boundary.

All five new functions manipulate this tree structure.

---

## `prune-possibilities` — Filtering Candidate Branches

### Mueller context

When the planner or backtracker needs to pick a branch, not every
child context is eligible. Some have already been explored (rules
already fired), and some are reserved for daydream-goal sprouting
(a special slot the system keeps open for future imaginary goals).
Mueller's original code filters these out before branch selection.

### The code

```clojure
(defn prune-possibilities
  [world context-ids]
  (->> context-ids
       (map #(get-in world [:contexts %]))
       (remove nil?)
       (remove (fn [{:keys [rules-run? dd-goal-sprout?]}]
                 (or rules-run? dd-goal-sprout?)))
       (sort-by (juxt (comp - :ordering) (comp str :id)))
       (map :id)
       vec))
```

A thread-last pipeline, same shape as `most-highly-motivated-goals`
from the previous walkthrough. The steps:

1. Look up each context-id in the world map. Drop any that do not
   exist (`remove nil?`).
2. Remove contexts where `:rules-run?` is true (already explored) or
   `:dd-goal-sprout?` is true (reserved for daydream goals).
3. Sort by `:ordering` descending (highest priority first), then by
   id as a string tiebreaker for determinism.
4. Extract the ids and return a vector.

The `(juxt (comp - :ordering) (comp str :id))` sort key is the same
pattern from `retrieve-episodes` in episodic memory. `juxt` builds a
comparison vector; `(comp - :ordering)` negates the ordering value
to get descending sort.

This function is the gatekeeper. Both `backtrack-top-level-goal` and
`run-goal-step` call it to decide which branches are still live.

---

## `terminate-top-level-goal` — Goal Resolution

### Mueller context

When a goal succeeds, fails, or is otherwise resolved, the system
records the outcome and cleans up. Mueller's `terminate-top-level-goal`
in `dd_cntrl.cl` does several things: stamps the goal's status,
records the resolution context, optionally asserts a result fact into
reality, and sprouts a fresh reality context (the "stabilization step"
-- reality has changed, so the system needs a new planning frontier
that reflects the change).

### The code

```clojure
(defn terminate-top-level-goal
  ([world goal-id resolution-cx]
   (terminate-top-level-goal world goal-id resolution-cx {}))
  ([world goal-id resolution-cx {:keys [status result-fact]
                                 :or {status :terminated}}]
   (let [goal (get-in world [:goals goal-id])
         world (cond-> world
                 result-fact
                 (cx/assert-fact (or (:reality-context world)
                                      resolution-cx)
                                  result-fact))
         world (-> world
                   (assoc-in [:goals goal-id :status] status)
                   (assoc-in [:goals goal-id :termination-cx] resolution-cx)
                   (update :termination-events
                           (fnil conj [])
                           {:goal-id goal-id
                            :status status
                            :resolution-cx resolution-cx
                            :planning-type (:planning-type goal)}))]
     (if-let [reality-cx (:reality-context world)]
       (let [[world new-reality-cx] (cx/sprout world reality-cx)]
         (-> world
             (assoc :reality-context new-reality-cx)
             (assoc :reality-lookahead new-reality-cx)))
       world))))
```

### Multi-arity function definition

```clojure
(defn terminate-top-level-goal
  ([world goal-id resolution-cx]
   (terminate-top-level-goal world goal-id resolution-cx {}))
  ([world goal-id resolution-cx {:keys [status result-fact]
                                 :or {status :terminated}}]
   ...))
```

In Scheme, you would handle optional arguments with rest args or
case-lambda:

```scheme
(define terminate-top-level-goal
  (case-lambda
    ((world goal-id resolution-cx)
     (terminate-top-level-goal world goal-id resolution-cx '()))
    ((world goal-id resolution-cx opts)
     ...)))
```

Clojure uses multiple arities inside one `defn`. The 3-argument body
delegates to the 4-argument body with an empty opts map `{}`. This
lets callers omit the options when they just want the default
`:terminated` status.

### `cond->` — Conditional threading

```clojure
(cond-> world
  result-fact
  (cx/assert-fact (or (:reality-context world)
                       resolution-cx)
                   result-fact))
```

`cond->` is like `->` (thread-first) but each step has a predicate
guard. The value is threaded through the form *only when the predicate
is truthy*. If `result-fact` is nil, the `cx/assert-fact` step is
skipped entirely and `world` passes through unchanged.

This is cleaner than the alternative:

```clojure
(let [world (if result-fact
              (cx/assert-fact world ... result-fact)
              world)]
  ...)
```

`cond->` scales well when you have multiple optional steps. You can
chain several predicate/form pairs and each is independently guarded.
Note that in `cond->`, the predicate is just tested for truthiness --
it is *not* threaded. Only the value (here `world`) flows through.

### `if-let` for reality stabilization

```clojure
(if-let [reality-cx (:reality-context world)]
  (let [[world new-reality-cx] (cx/sprout world reality-cx)]
    (-> world
        (assoc :reality-context new-reality-cx)
        (assoc :reality-lookahead new-reality-cx)))
  world)
```

You saw `if-let` in the episodic memory cascade. Same pattern here:
if the world has a `:reality-context`, bind it and sprout a fresh
child from it. This is the stabilization step -- after a goal
terminates, reality may have changed (facts were asserted), so the
system advances its reality frontier. If there is no reality context
(early bootstrapping, or testing), just return the world unchanged.

### The termination-events log

```clojure
(update :termination-events
        (fnil conj [])
        {:goal-id goal-id
         :status status
         :resolution-cx resolution-cx
         :planning-type (:planning-type goal)})
```

An append-only event log. `(fnil conj [])` means "conj onto the
vector, and if it does not exist yet, start with `[]`." This is
the same `fnil` pattern from the cycle counter in `run-cycle`, but
with a vector instead of a number. The log lets downstream code
(episode storage, emotional responses) react to goal termination
without polling.

---

## `all-possibilities-failed` — Dead End

### Mueller context

When backtracking has exhausted every branch all the way back to the
backtrack wall, there is nothing left to try. The goal has failed.

### The code

```clojure
(defn all-possibilities-failed
  [world goal-id backtrack-wall]
  (terminate-top-level-goal world goal-id backtrack-wall {:status :failed}))
```

A one-liner that delegates to `terminate-top-level-goal` with
`:status :failed`. The resolution context is the backtrack wall
itself -- the goal failed at its own boundary.

Simple, but important as a named concept. Backtracking calls this
when it hits the wall, which keeps the backtracking logic clean --
it does not need to know how termination works internally.

---

## `backtrack-top-level-goal` — Walking Up the Tree

### Mueller context

When the planner reaches a dead end in the current context (no rules
fired, or all rules led to contradictions), it backtracks. Mueller's
`backtrack-top-level-goal` walks up the context tree toward the
backtrack wall, looking for an unexplored branch at each level. If it
finds one, it redirects the goal's `next-cx` to that branch. If it
reaches the wall without finding anything, all possibilities have
been exhausted.

### The algorithm in pictures

Starting position -- the planner is stuck at cx-B2:

```
    backtrack-wall
    ├── cx-A  (rules-run? true)
    ├── cx-B  (rules-run? true)
    │   ├── cx-B1  (rules-run? true)
    │   └── cx-B2  ← stuck here, rules-run? just set
    └── cx-C
```

**Step 1**: Look at cx-B2's parent (cx-B). Prune cx-B's children.
cx-B1 is rules-run, cx-B2 is rules-run. No surviving sprouts.
Move up.

```
    backtrack-wall
    ├── cx-A  (rules-run? true)
    ├── cx-B  ← now looking here
    │   ├── cx-B1  (pruned)
    │   └── cx-B2  (pruned)
    └── cx-C
```

**Step 2**: Look at cx-B's parent (backtrack-wall). Prune
backtrack-wall's children. cx-A is rules-run, cx-B is rules-run,
cx-C is still alive. Select cx-C.

```
    backtrack-wall
    ├── cx-A  (pruned)
    ├── cx-B  (pruned)
    │   ├── cx-B1  (pruned)
    │   └── cx-B2  (pruned)
    └── cx-C  ← next-cx redirected here
```

If cx-C had also been pruned, we would be at the backtrack wall with
no surviving children. That triggers `all-possibilities-failed`.

### The code

```clojure
(defn backtrack-top-level-goal
  [world goal-id current-cx]
  (loop [world world
         next-cx current-cx]
    (let [backtrack-wall (goals/get-backtrack-wall world goal-id)]
      (if (= backtrack-wall next-cx)
        [(all-possibilities-failed world goal-id backtrack-wall) nil]
        (let [parent-cx (get-in world [:contexts next-cx :parent-id])
              sprouts (prune-possibilities world (cx/children world parent-cx))]
          (if (seq sprouts)
            (let [selected-cx (first sprouts)]
              [(goals/set-next-context world goal-id selected-cx) selected-cx])
            (recur world parent-cx)))))))
```

### `loop/recur` for backtracking

This is the second use of `loop/recur` in the kernel, after the
episodic memory cascade. The pattern is similar -- a work loop where
the iteration variable changes shape on each step -- but the purpose
is different. The cascade grows a queue of pending episodes. The
backtracker walks up a fixed tree structure.

In Scheme, this would be a named let:

```scheme
(let walk ((world world) (next-cx current-cx))
  (let ((wall (get-backtrack-wall world goal-id)))
    (if (eq? wall next-cx)
      (list (all-possibilities-failed world goal-id wall) #f)
      (let* ((parent (context-parent world next-cx))
             (sprouts (prune-possibilities world (children world parent))))
        (if (pair? sprouts)
          (let ((selected (car sprouts)))
            (list (set-next-context world goal-id selected) selected))
          (walk world parent))))))
```

The structure is identical. Clojure's `loop/recur` replaces Scheme's
named `let` with explicit `recur` at the tail position.

Notice that `backtrack-wall` is re-fetched inside the loop body
rather than bound once before the loop. This is defensive -- in
theory the wall could shift if future code modifies goals during
backtracking. In practice it is constant today, but the pattern
keeps the function robust.

### Return shape

The function returns `[world next-context-id]`. On success,
`next-context-id` is the selected surviving sprout. On total failure,
it is `nil` (the goal has been terminated).

---

## `run-goal-step` — The Per-Goal Control Step

### Mueller context

This is Mueller's `daydreamer-control1`. After `run-cycle` (which is
`daydreamer-control0`) selects a goal, `run-goal-step` advances that
goal by one step. In the full system, the planner would have already
run in the goal's current context, producing child contexts. This
function inspects the result and decides what happens next: advance
into a child sprout, backtrack, halt, or do nothing.

### The code

```clojure
(defn run-goal-step
  [world goal-id]
  (let [current-cx (goals/get-next-context world goal-id)
        timeout (get-in world [:contexts current-cx :timeout])
        world (assoc-in world [:contexts current-cx :rules-run?] true)]
    (cond
      (and (number? timeout) (<= timeout 0))
      (backtrack-top-level-goal world goal-id current-cx)

      (= :fired-halt (get-in world [:goals goal-id :status]))
      [(goals/change-status world goal-id :halted) nil]

      (contains? #{:runable :halted} (get-in world [:goals goal-id :status]))
      (let [sprouts (prune-possibilities world (cx/children world current-cx))]
        (if (seq sprouts)
          (let [selected-cx (first sprouts)]
            [(goals/set-next-context world goal-id selected-cx) selected-cx])
          (backtrack-top-level-goal world goal-id current-cx)))

      :else
      [world nil])))
```

### Setup

The first three lines establish the situation:

1. Get the goal's current context pointer (`next-cx`).
2. Check if that context has a timeout.
3. Mark the context as explored (`rules-run? true`). This is done
   *immediately* -- before any branching logic. The planner has
   already had its chance; now the control step records that fact.

### The four-way dispatch

The `cond` has four branches, evaluated in order:

**Timeout expired** — `(and (number? timeout) (<= timeout 0))`.
If the context has a numeric timeout that has reached zero or below,
the planner ran out of time. Backtrack. The `(number? timeout)` guard
handles the case where timeout is nil (no timeout set) -- nil is not
a number, so the branch is skipped.

**Fired-halt** — `(= :fired-halt ...)`. Some rules produce a halt
signal (they decided the goal should stop but not fail). Normalize
the status from the transient `:fired-halt` to the stable `:halted`
and return nil for the next context. The goal pauses.

**Runable or halted** — `(contains? #{:runable :halted} ...)`. The
normal case. Look for child sprouts in the current context. If any
survive pruning, select the first one and advance. If none exist,
backtrack. This is where the planner's output meets the control
loop -- child contexts are the planner's proposals, and the control
loop picks among them.

**Else** — the goal is in some other status (terminated, failed,
waiting). Do nothing; return the world unchanged.

### The `contains?` set-membership pattern

```clojure
(contains? #{:runable :halted} (get-in world [:goals goal-id :status]))
```

A set literal as a membership test. This is the same pattern from
`set-state`'s mode validation. It reads as "is the status one of
`:runable` or `:halted`?"

---

## The Complete Control Flow

Here is how the pieces fit together:

```
run-cycle (daydreamer-control0)
│
├── decay needs, emotions
├── increment cycle counter
├── select strongest runnable goal
│   └── mode filter: performance excludes imaginary
│
├── goal selected?
│   ├── yes → return [world goal-id]
│   │         (caller passes goal-id to run-goal-step)
│   ├── no, performance mode → switch to daydreaming
│   └── no, daydreaming mode → wake real goals, switch to performance
│
run-goal-step (daydreamer-control1)
│
├── mark current context as rules-run
├── timeout expired? → backtrack
├── fired-halt? → halt goal
├── runable/halted?
│   ├── child sprouts exist? → advance to first sprout
│   └── no sprouts? → backtrack
└── else → do nothing
│
backtrack-top-level-goal
│
├── at backtrack wall? → all-possibilities-failed → terminate :failed
└── walk to parent
    ├── parent has surviving sprouts? → select first, redirect next-cx
    └── no sprouts? → recur (walk to grandparent)
│
terminate-top-level-goal
│
├── optionally assert result-fact into reality
├── record status and termination-cx on goal
├── append to termination-events log
└── if reality-context exists → sprout fresh reality (stabilization)
```

The caller (eventually the top-level REPL or agent loop) alternates:

```clojure
(let [[world goal-id] (ctrl/run-cycle world)]
  (if goal-id
    (let [[world _next-cx] (ctrl/run-goal-step world goal-id)]
      ;; next iteration
      world)
    world))
```

`run-cycle` picks the goal. `run-goal-step` advances it one step.
The world flows through both, accumulating changes.

---

## What Is Still Missing

The control loop is the skeleton. The muscle is the planner -- the
rule engine that actually fills in child contexts. Here is what the
current code assumes but does not yet implement:

**Rule execution.** Before `run-goal-step` is called, something
needs to have run rules in the goal's current context, producing
child contexts that represent alternative plans. `run-goal-step`
just picks among them or backtracks. The rule engine is wave 2+.

**Subgoal creation.** A rule might decide that achieving the current
goal requires achieving a sub-goal first. This creates a new goal
whose `top-level-goal` points back to the parent. The goal hierarchy
is supported by the data model (`create-goal` has a `top-level-goal`
field) but no code creates subgoals yet.

**Inference.** Some rules do not produce alternative plans but instead
derive new facts (forward chaining). The inference engine would
assert these facts into the current context and potentially trigger
further rules. This is the reasoning half of the planner.

**Episode storage on termination.** When a goal terminates, its
planning trace should be stored as an episode in episodic memory.
The `termination-events` log is the hook point -- downstream code
can watch it and call `add-episode` / `store-episode`. Not wired yet.

**Emotional responses.** Goal termination should trigger emotional
reactions (satisfaction on success, frustration on failure). These
emotions would be asserted into the world and could motivate new
goals (e.g., a RATIONALIZATION goal after a failure). The emotion
system exists (decay, GC, motivating-emotion?) but nothing generates
new emotions from goal outcomes.

The control loop is designed to work correctly even with these pieces
missing. You can call `run-goal-step` on a goal whose context has no
children, and it will backtrack cleanly. This lets us test the control
logic in isolation before the planner exists.

---

## Pattern Summary

| Pattern | Where | What it does |
|---|---|---|
| `cond->` | `terminate-top-level-goal` | Conditional threading: apply steps only when predicates are true |
| Multi-arity `defn` | `terminate-top-level-goal` | 3-arg and 4-arg bodies in one function; short arity delegates to long |
| `if-let` | `terminate-top-level-goal` | Bind-and-branch for optional reality stabilization |
| `loop/recur` | `backtrack-top-level-goal` | Walk up the context tree; second use in kernel after episodic cascade |
| `fnil conj []` | `terminate-top-level-goal` | Nil-safe append to a vector that may not exist yet |
| Set-as-membership-test | `run-goal-step` | `(contains? #{:runable :halted} status)` for multi-value check |
| `juxt` sort key | `prune-possibilities` | Multi-field sort: ordering descending, then id for determinism |
