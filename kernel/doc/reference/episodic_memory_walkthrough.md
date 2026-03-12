# Episodic Memory — Walkthrough

A walkthrough of `episodic_memory.clj` in the daydreamer-kernel.
Assumes you have read `clojure_for_schemers.md` (syntax, maps, threading)
and the goals/control walkthrough (world-threading, opts maps, `fnil`).

This document covers Mueller's coincidence-counting retrieval mechanism,
the reminding cascade, and the FIFO recency structures. All of these
recover mechanisms from Mueller's `dd_epis.cl`.

---

## The Big Idea: Coincidence Counting

This is the most important concept in the file and probably the most
foreign if you are used to modern retrieval systems.

DAYDREAMER does **not** use embedding similarity, vector search, or
any continuous distance metric to retrieve memories. It uses a
discrete counting mechanism: an episode is stored under multiple
**indices** (symbolic tags like `:anger`, `:s1`, `:lost-job`). When
you query with a set of indices, the system counts how many of those
indices each episode appears under. That count is called the episode's
**marks**. The episode retrieves when marks >= its **threshold**.

The threshold is set at storage time. Each time you store an episode
under a new index with `plan? true`, its `plan-threshold` goes up by 1.
This means an episode stored under 3 plan-indices needs 3 matching cues
to retrieve. This is counter-intuitive but intentional -- richly indexed
episodes require richer cue overlap to trigger. A memory with many
associated themes does not become "easier" to recall. Instead, it
demands that more of those themes be simultaneously active before it
surfaces.

**Serendipity** lowers the threshold by 1, making retrieval slightly
easier. If an episode's threshold is 3 and serendipity is active, it
retrieves at 2 matching cues instead of 3. This models the cognitive
phenomenon of being in a receptive state where associations come more
freely.

This is the mechanism that produces "that reminds me of..." -- not a
nearest-neighbor search, but a coincidence of active cues crossing a
threshold.

---

## Episode Structure

```clojure
(defn create-episode
  [{:keys [id rule goal-id context-id realism desirability indices
           plan-threshold reminding-threshold children descendants]
    :or {id :ep-1
         indices #{}
         plan-threshold 0
         reminding-threshold 0
         children []}}]
  {:id id
   :rule rule
   :goal-id goal-id
   :context-id context-id
   :realism realism
   :desirability desirability
   :indices (set indices)
   :plan-threshold plan-threshold
   :reminding-threshold reminding-threshold
   :children (vec children)
   :descendants (vec (or descendants (cons id children)))})
```

Same opts-map-with-`:or` pattern from `create-goal`. An episode records
which rule produced it, which goal it belongs to, and the context it
was created in. The `:realism` and `:desirability` values carry the
emotional coloring of the episode.

The two threshold fields serve different retrieval contexts:
- **`plan-threshold`** -- used by the planner to find relevant past
  plans. Higher bar (you need many cues to match).
- **`reminding-threshold`** -- used by the reminding cascade. Can have
  a different (usually lower) bar than plan retrieval.

### Descendants

The `descendants` field is a vector containing the episode's own id
plus the descendants of any child episodes. If episode A has children
B and C, then A's descendants are `[A, B, C]` (plus whatever B and C
have as descendants).

```clojure
(defn add-episode
  [world episode-spec]
  (let [[world episode-id] (next-episode-id world)
        child-descendants (mapcat (fn [child-id]
                                    (get-in world [:episodes child-id :descendants]
                                            [child-id]))
                                  (:children episode-spec))
        episode (create-episode (assoc episode-spec
                                       :id episode-id
                                       :descendants (vec (distinct
                                                          (cons episode-id
                                                                child-descendants)))))]
    [(assoc-in world [:episodes episode-id] episode) episode-id]))
```

`mapcat` is flatMap -- it applies the function to each child-id and
concatenates the results into a single sequence. Here it collects all
descendant ids from all child episodes into one flat list. `distinct`
deduplicates (an episode might appear in multiple children's descendant
lists). `cons` prepends the episode's own id.

The descendants list matters because of recency exclusion: if a parent
episode is marked recent, all its descendants are also considered
recent and excluded from retrieval.

---

## Index Storage and Threshold Growth

```clojure
(defn store-episode
  [world episode-id index {:keys [plan? reminding?] :or {plan? false
                                                         reminding? false}}]
  (-> world
      (update-in [:episode-index index] (fnil conj #{}) episode-id)
      (update-in [:episodes episode-id :indices] (fnil conj #{}) index)
      (update-in [:episodes episode-id :plan-threshold]
                 (fnil (fn [threshold]
                         (if plan?
                           (inc threshold)
                           threshold))
                       0))
      (update-in [:episodes episode-id :reminding-threshold]
                 (fnil (fn [threshold]
                         (if reminding?
                           (inc threshold)
                           threshold))
                       0))))
```

`store-episode` does four things in one threading pipeline:

1. Adds the episode-id to the index's set in `episode-index` (the
   reverse lookup table: index -> set of episode-ids).
2. Adds the index to the episode's own `:indices` set.
3. If `plan?` is true, increments `plan-threshold` by 1.
4. If `reminding?` is true, increments `reminding-threshold` by 1.

The `(fnil conj #{})` pattern you saw in `context.clj` -- if the set
doesn't exist yet, start with an empty set, then `conj` into it.

The threshold increment is the key design point: the threshold grows
with the number of plan-relevant indices. If you store an episode
under `:s1` with `plan? true` and then under `:anger` with
`plan? true`, the plan-threshold is now 2. You need both `:s1` AND
`:anger` active to retrieve it for planning. Store it under a third
index with `plan? true` and the threshold becomes 3.

You can store an episode under an index without incrementing thresholds
(both flags false). This makes the episode findable under that index
but does not raise the bar for retrieval.

---

## Recency: Two FIFO Structures

Mueller's system excludes "recent" episodes from retrieval. You do not
want the system to be reminded of what just happened -- that defeats
the purpose of associative memory. Two bounded FIFO lists manage this.

### `add-recent-index`

```clojure
(defn add-recent-index
  [world index]
  (let [recent-indices (vec (:recent-indices world []))]
    (if (some #{index} recent-indices)
      world
      (assoc world :recent-indices
             (->> (conj recent-indices index)
                  (take-last recent-index-max-length)
                  vec)))))
```

Adds an index to the recent-indices list, skipping duplicates. The
`(some #{index} recent-indices)` idiom uses a set as a predicate -- a
set is a function that returns its argument if present, or nil. Here
we wrap the single index in a set `#{index}` and use `some` to check
membership. If the index is already recent, return world unchanged.

`take-last` keeps only the last N elements (bounded at
`recent-index-max-length`, which is 6). This is the FIFO eviction:
when a 7th index is added, the oldest one drops off. `take-last` works
from the end of the collection, so `(take-last 6 [a b c d e f g])`
gives `[b c d e f g]` -- the oldest element `a` is gone.

### `add-recent-episode`

```clojure
(defn add-recent-episode
  [world episode-id]
  (let [descendants (set (get-in world [:episodes episode-id :descendants]
                                 [episode-id]))
        recent-episodes (->> (:recent-episodes world [])
                             (remove descendants)
                             vec)]
    (assoc world :recent-episodes
           (->> (conj recent-episodes episode-id)
                (take-last recent-episode-max-length)
                vec))))
```

This is more interesting. When adding a new episode to the recent
list, any existing recent episode that is a **descendant** of the new
one gets removed first. The `descendants` set is used directly as the
predicate for `remove` -- since a set is a function, `(remove
descendants coll)` removes any element that the set returns truthy for.

Why? If episode A is a parent of episode B, and A becomes recent, then
B is already implicitly recent (it is part of A's story). Keeping B
explicitly in the list wastes a slot. This is descendant displacement.

### `recent-episode?`

```clojure
(defn recent-episode?
  [world episode-id]
  (boolean
   (some (fn [recent-id]
           (contains? (set (get-in world [:episodes recent-id :descendants] []))
                      episode-id))
         (:recent-episodes world))))
```

An episode is recent if it appears in any recent episode's descendants
list. This is the descendant-aware check: you are recent not just if
you are in the recent-episodes list yourself, but also if any of your
ancestors are. The `boolean` wrapper normalizes the return, same pattern
as `motivating-emotion?` in goals.clj.

---

## The Retrieval Algorithm

This is the core of the file. Lines 120-143.

```clojure
(defn retrieve-episodes
  [world indices {:keys [serendipity? threshold-key]
                  :or {serendipity? false
                       threshold-key :plan-threshold}}]
  (let [marks (frequencies
               (mapcat #(get-in world [:episode-index %] #{}) indices))]
    (->> marks
         (keep (fn [[episode-id mark-count]]
                 (let [episode (episode-or-throw world episode-id)
                       threshold (max 0
                                      (if serendipity?
                                        (dec (or (threshold-key episode) 0))
                                        (or (threshold-key episode) 0)))]
                   (when (and (not (recent-episode? world episode-id))
                              (>= mark-count threshold))
                     {:episode-id episode-id
                      :marks mark-count
                      :threshold threshold}))))
         (sort-by (juxt (comp - :marks) (comp str :episode-id)))
         vec)))
```

### Step 1: Count the marks

```clojure
(let [marks (frequencies
             (mapcat #(get-in world [:episode-index %] #{}) indices))]
```

For each query index, look up which episodes are stored under it
(`get-in world [:episode-index %]` returns a set of episode-ids, or
`#{}` if the index has no entries). `mapcat` flattens all those sets
into one sequence. If episode `:ep-1` is stored under both `:s1` and
`:anger`, and both `:s1` and `:anger` are in the query indices, then
`:ep-1` appears twice in the flat sequence.

`frequencies` is a built-in that counts occurrences:

```clojure
(frequencies [:ep-1 :ep-1 :ep-2])
;; => {:ep-1 2, :ep-2 1}
```

This is the mark-counting step. In Mueller's Common Lisp, this was done
with a mutable counter; here `frequencies` does it in one call. The
result is a map from episode-id to mark-count.

### Step 2: Filter by threshold

```clojure
(keep (fn [[episode-id mark-count]]
        (let [episode (episode-or-throw world episode-id)
              threshold (max 0
                             (if serendipity?
                               (dec (or (threshold-key episode) 0))
                               (or (threshold-key episode) 0)))]
          (when (and (not (recent-episode? world episode-id))
                     (>= mark-count threshold))
            {:episode-id episode-id
             :marks mark-count
             :threshold threshold}))))
```

`keep` is like a combined `map` + `filter`. It applies a function to
each element and keeps the non-nil results. Here: for each
`[episode-id mark-count]` pair from the marks map, compute the
effective threshold, and if the episode passes, return a hit map.
If it doesn't pass, `when` returns nil and `keep` drops it.

Compare with the alternative using `filter` + `map`:

```clojure
;; This would require computing threshold twice or introducing a let
(->> marks
     (filter (fn [[episode-id mark-count]]
               (let [threshold (compute-threshold ...)]
                 (and (not (recent-episode? ...))
                      (>= mark-count threshold)))))
     (map (fn [[episode-id mark-count]]
            {:episode-id episode-id ...})))
```

`keep` avoids the redundancy by fusing the test and the transformation.

The `threshold-key` parameter is a **keyword used as a function**. When
called as `(threshold-key episode)`, it looks up that key in the episode
map. The default is `:plan-threshold`; the reminding system passes
`:reminding-threshold`. This lets the same retrieval function serve
both contexts with different threshold bars.

### Step 3: Sort

```clojure
(sort-by (juxt (comp - :marks) (comp str :episode-id)))
```

`juxt` creates a function that applies multiple functions and returns
a vector of results. `(juxt f g)` returns `(fn [x] [(f x) (g x)])`.
Vectors compare lexicographically, so this sorts by marks descending
(the `(comp - :marks)` negates the count), then by episode-id string
as a tiebreaker.

In Scheme you would write a custom comparator:

```scheme
(sort results
  (lambda (a b)
    (or (> (marks a) (marks b))
        (and (= (marks a) (marks b))
             (string<? (symbol->string (id a))
                       (symbol->string (id b)))))))
```

The Clojure version is more declarative: specify the sort key, not the
comparison logic.

---

## The Reminding Cascade

This is Mueller's `epmem-reminding` -- the mechanism that turns indexed
retrieval into free association.

### `remindings` — Entry point

```clojure
(defn remindings
  ([world]
   (remindings world []))
  ([world extra-indices]
   (retrieve-episodes world
                      (concat (:recent-indices world []) extra-indices)
                      {:threshold-key :reminding-threshold})))
```

Multi-arity function (two bodies, one with one argument, one with two).
The single-arity version calls the two-arity with an empty extra list.

The query indices for reminding are the **recent-indices** -- whatever
indices have been activated recently -- plus any extra cues the caller
provides. It uses `:reminding-threshold` instead of `:plan-threshold`.

### `episode-reminding` — The cascade

```clojure
(defn episode-reminding
  [world episode-id]
  (if (recent-episode? world episode-id)
    [world []]
    (loop [world (reduce add-recent-index
                         (add-recent-episode world episode-id)
                         (:indices (episode-or-throw world episode-id)))
           pending (mapv :episode-id (remindings world))
           seen #{episode-id}
           reminded []]
      (if-let [next-episode-id (first pending)]
        (if (or (contains? seen next-episode-id)
                (recent-episode? world next-episode-id))
          (recur world (subvec pending 1) seen reminded)
          (let [world (reduce add-recent-index
                              (add-recent-episode world next-episode-id)
                              (:indices (episode-or-throw world next-episode-id)))
                new-pending (into (subvec pending 1)
                                  (map :episode-id (remindings world)))]
            (recur world
                   new-pending
                   (conj seen next-episode-id)
                   (conj reminded next-episode-id))))
        [world reminded]))))
```

This is the most complex function in the kernel so far, and the first
real use of `loop/recur`. Let's break it down.

### `loop/recur` — Clojure's tail-recursive loop

In Scheme, you would write a named `let`:

```scheme
(let cascade ((world world)
              (pending (initial-pending))
              (seen (set episode-id))
              (reminded '()))
  ;; body that calls (cascade new-world new-pending ...) to loop
  ...)
```

Clojure's `loop` is the same idea:

```clojure
(loop [world   initial-world
       pending initial-pending
       seen    #{episode-id}
       reminded []]
  ;; body that calls (recur new-world new-pending ...) to loop
  ...)
```

`loop` establishes bindings and a recursion point. `recur` jumps back
to that point with new values. It is guaranteed tail-recursive -- the
JVM does not have tail call optimization, so `recur` is how Clojure
avoids stack overflow in loops. Unlike Scheme, you cannot `recur` from
a non-tail position; the compiler will reject it.

Why `loop/recur` instead of `reduce` here? Because the collection being
iterated over **grows during iteration**. Each time we process a pending
episode, its remindings may add new episodes to the pending queue. You
cannot express this with `reduce` because `reduce` iterates a fixed
collection. The cascade is genuinely iterative -- a work queue that
expands as items are processed.

### The cascade algorithm

**Initial setup** (before the loop):

1. If the seed episode is already recent, bail out with `[world []]`.
2. Otherwise, mark it recent (`add-recent-episode`) and activate all
   its indices (`reduce add-recent-index` over its index set).
3. Query `remindings` to find what those newly activated indices
   retrieve. These become the initial `pending` queue.

**Each iteration:**

1. `if-let` binds `next-episode-id` to `(first pending)`. If `pending`
   is empty, `first` returns nil, `if-let` takes the else branch, and
   we return `[world reminded]`. This is the termination condition.

2. If the episode has been seen or is recent, skip it: `recur` with
   `(subvec pending 1)` (pop the front) and everything else unchanged.

3. Otherwise, process it:
   - Mark it recent and activate its indices (same as the seed setup).
   - Query `remindings` again -- the newly activated indices may now
     surface additional episodes.
   - Build a new pending queue: the old tail `(subvec pending 1)` plus
     the newly retrieved episode-ids via `into`.
   - `recur` with the episode added to both `seen` and `reminded`.

### `if-let`

```clojure
(if-let [next-episode-id (first pending)]
  ;; then: next-episode-id is bound and truthy
  ...
  ;; else: (first pending) was nil
  ...)
```

Binds a value and branches on it in one form. If the bound value is
truthy, the then-branch runs with the binding in scope. If falsy,
the else-branch runs (without the binding). This is more concise than:

```clojure
(let [next-episode-id (first pending)]
  (if next-episode-id
    ...
    ...))
```

### `subvec`

```clojure
(subvec pending 1)
```

Returns a subvector from index 1 to the end -- effectively "pop the
first element." This is O(1) because `subvec` shares the underlying
array with the original vector. By contrast, `(rest v)` on a vector
returns a **seq** (linked-list view), not a vector, which means
subsequent `into` and `first` calls would lose the O(1) indexed
access.

This matters here because `pending` is used in a tight loop --
repeatedly popping from the front and appending to the end. Keeping it
as a vector via `subvec` preserves efficient access on both ends.

### The cascade in action

Suppose the world has three episodes:

- ep-1: indices `#{:s1 :anger}`, reminding-threshold 1
- ep-2: indices `#{:anger :betrayal}`, reminding-threshold 1
- ep-3: indices `#{:betrayal :loss}`, reminding-threshold 1

You call `(episode-reminding world :ep-1)`.

1. ep-1 is marked recent. Its indices `:s1` and `:anger` are activated
   (added to recent-indices).
2. `remindings` queries with `[:s1 :anger]`. ep-2 has `:anger` and its
   threshold is 1, so it retrieves. ep-3 has neither `:s1` nor `:anger`,
   so it does not. Pending: `[:ep-2]`.
3. Process ep-2: mark it recent, activate `:anger` (already there) and
   `:betrayal` (new).
4. `remindings` queries with `[:s1 :anger :betrayal]`. ep-3 has
   `:betrayal` and its threshold is 1, so it retrieves. Pending:
   `[:ep-3]`.
5. Process ep-3: mark it recent, activate `:betrayal` (already there)
   and `:loss` (new).
6. `remindings` queries with all accumulated indices. Nothing new
   surfaces. Pending: `[]`.
7. Return `[world [:ep-2 :ep-3]]`.

One memory about anger surfaced a memory about betrayal, which surfaced
a memory about loss. This is the chain of free association that makes
DAYDREAMER's memory **associative** rather than just indexed.

---

## How This Connects to the Python Engine

The Python engine has a simplified version of coincidence retrieval --
it can count marks and check thresholds. But it does not implement the
full cascade. When the Python engine retrieves a memory, that retrieval
is a dead end; it does not activate the memory's indices to surface
further memories.

The cascade is what makes DAYDREAMER's memory system qualitatively
different from a lookup table. Retrieving one memory activates its
themes, which may surface other memories that share those themes. The
result is not "find the best match" but "follow the thread of
associations." This is the mechanism behind DAYDREAMER's daydreaming --
the system wanders from memory to memory along thematic connections,
just as human minds do during idle moments.

The kernel recovers this cascade from Mueller's `dd_epis.cl`, closing
the gap between the Python engine's flat retrieval and DAYDREAMER's
original associative memory.

---

## Pattern Summary

| Pattern | Where | What it does |
|---|---|---|
| `loop/recur` | `episode-reminding` | Tail-recursive loop over a growing work queue (Scheme's named `let`) |
| `if-let` | `episode-reminding` | Bind-and-test in one form; terminates loop when pending is empty |
| `subvec` | `episode-reminding` | O(1) vector slice, avoids seq conversion from `rest` |
| `frequencies` | `retrieve-episodes` | Counts marks -- occurrences of each episode-id across matched indices |
| `keep` | `retrieve-episodes` | Fused map + filter: apply fn, keep non-nil results |
| `juxt` + `comp` | `retrieve-episodes` | Multi-key sort: descending marks, then id as tiebreaker |
| `mapcat` | `add-episode` | flatMap: collect descendants from all children into one list |
| `take-last` | `add-recent-index/episode` | FIFO bounds: keep only the last N elements |
| Set-as-predicate | `add-recent-index`, `add-recent-episode` | Use `#{x}` or a set directly as a membership-testing function |
