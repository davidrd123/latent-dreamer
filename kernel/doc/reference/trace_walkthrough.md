# Trace — Walkthrough

A walkthrough of `trace.clj` in the daydreamer-kernel. Assumes you
have read `clojure_for_schemers.md`, the goals/control walkthrough
(world-threading, opts maps, mode switching), and the episodic memory
walkthrough (retrieval, `mapv`, `keep`).

This document covers the observability layer that makes the kernel's
behavior visible and comparable with the Python engine's output. Unlike
the other kernel files, `trace.clj` has no Mueller equivalent -- it is
new to the sidecar. Its job is to record what happens each cycle and
to bridge the Clojure kernel's internal representation with the existing
Python HTML reporter (`daydream_trace_report.py`).

---

## The Core Design: Pure Trace, Impure Edges

Trace is **pure**. Every function in this file builds data structures.
No file I/O, no atoms, no side effects. Writing JSON to disk happens
at the boundary -- a REPL wrapper or CLI runner calls `reporter-log`,
serializes the result, and writes it out. The kernel never touches the
filesystem.

This follows the "pure kernel, impure edges" decision from the
guardrails doc. The trace module builds the EDN; the edge writes the
JSON. Testing the trace logic requires no mocking, no temp files, no
cleanup.

Two output shapes exist side by side:

- **Internal EDN** -- Clojure maps with keyword keys, used by the
  kernel itself. This is what `cycle-snapshot` produces and what
  `append-cycle` stores on the world.
- **Reporter JSON** -- string-keyed, snake_cased maps matching the
  schema expected by `daydream_trace_report.py`. This is what
  `reporter-cycle` and `reporter-log` produce. The kernel never uses
  this shape internally; it exists solely for Python compatibility.

The boundary between the two is explicit: `reporter-cycle` translates
one direction, and nothing translates back.

---

## Helper Functions

### `scalar->json` — Keyword/symbol to string

```clojure
(defn- scalar->json
  [value]
  (cond
    (keyword? value) (name value)
    (symbol? value) (name value)
    :else value))
```

Converts Clojure-specific scalar types to JSON-compatible strings.
`(name :daydreaming)` returns `"daydreaming"` -- it strips the colon.
Numbers, strings, booleans, and nil pass through unchanged.

The `defn-` (note the dash) makes this namespace-private. Same as
`^:private` metadata on a `def`.

### `json-value` — Recursive EDN to JSON conversion

```clojure
(defn- json-value
  [value]
  (cond
    (map? value)
    (into {}
          (map (fn [[k v]]
                 [(scalar->json k) (json-value v)]))
          value)

    (set? value)
    (->> value
         (map json-value)
         (sort-by str)
         vec)

    (sequential? value)
    (mapv json-value value)

    :else
    (scalar->json value)))
```

Walks an arbitrary EDN structure and produces a JSON-compatible
equivalent. The interesting patterns:

**`into {}` with a map transducer.** The three-argument form of `into`
is `(into to xform from)`. Here `xform` is `(map (fn [[k v]] ...))`,
which transforms each key-value pair. Each keyword key becomes a
string; each value is recursively converted. The result is poured into
a new map.

In Scheme you would write something like:

```scheme
(alist->hash-table
  (map (lambda (pair)
         (cons (scalar->json (car pair))
               (json-value (cdr pair))))
       (hash-table->alist m)))
```

The Clojure version is more direct because maps are already sequences
of `[k v]` pairs.

**Sets become sorted vectors.** JSON has no set type, so sets are
converted to vectors sorted by string representation. The sort makes
output deterministic -- tests can compare JSON without worrying about
set ordering.

**`mapv` for sequential values.** `mapv` is eager `map` that returns
a vector. Trace uses `mapv` throughout rather than `map` because the
results go into data structures, not lazy pipelines. A lazy seq inside
a map is a footgun -- it might not realize when you expect, and it
prints differently than a vector. `mapv` avoids both problems.

### `goal-summary` — Compact goal projection

```clojure
(defn goal-summary
  [world goal-id]
  (let [{:keys [id goal-type strength planning-type situation-id main-motiv]}
        (goal-or-throw world goal-id)]
    (cond-> {:id id
             :goal-type goal-type
             :strength strength}
      planning-type
      (assoc :planning-type planning-type)

      situation-id
      (assoc :situation-id situation-id)

      main-motiv
      (assoc :main-motiv main-motiv))))
```

Projects a full goal map into a smaller trace payload. Only the fields
relevant to trace output are kept.

**`cond->`** is the key pattern here. It threads a value through a
series of test/transform pairs: if the test is truthy, apply the
transform; otherwise skip it. Read it as "start with this map, then
conditionally add fields."

```clojure
(cond-> {:id id :goal-type goal-type :strength strength}
  planning-type                         ;; test: is planning-type non-nil?
  (assoc :planning-type planning-type)  ;; yes: add it to the map

  situation-id                          ;; test: is situation-id non-nil?
  (assoc :situation-id situation-id)    ;; yes: add it

  main-motiv                            ;; test: is main-motiv non-nil?
  (assoc :main-motiv main-motiv))       ;; yes: add it
```

In Scheme you would build the base alist and then conditionally append:

```scheme
(let ((base `((id . ,id) (goal-type . ,goal-type) (strength . ,strength))))
  (if planning-type
    (set! base (cons `(planning-type . ,planning-type) base)))
  ...)
```

The `cond->` version is declarative and pure -- no mutation, no
imperative `if` chain. The result is a map with 3 to 6 keys depending
on which optional fields the goal has.

### `context-depth` — Ancestry length

```clojure
(defn- context-depth
  [world context-id]
  (if (and context-id (contains? (:contexts world) context-id))
    (count (cx/ancestors world context-id))
    0))
```

Returns how deep a context is in the tree. The root context has depth
0 (no ancestors). A child of root has depth 1. This is included in
trace snapshots so the HTML reporter can visualize nesting.

The guard checks both that `context-id` is non-nil and that it
actually exists in the world. If either fails, depth is 0. This
defensive style avoids crashing on partially constructed states during
early development.

### `reporter-goal` — Goal to reporter shape

```clojure
(defn- reporter-goal
  [goal]
  {"id" (some-> (:id goal) scalar->json)
   "goal_type" (some-> (:goal-type goal) scalar->json)
   "strength" (:strength goal)
   "planning_type" (some-> (:planning-type goal) scalar->json)
   "situation_id" (some-> (:situation-id goal) scalar->json)})
```

Translates a goal summary into the reporter's expected shape: string
keys, snake_case names, string values for keywords.

**`some->`** is nil-safe threading. It works like `->` but
short-circuits if any step produces nil. `(some-> (:id goal)
scalar->json)` means "get `:id` from the goal; if it is nil, return
nil; otherwise pass it to `scalar->json`." Without `some->`, calling
`(scalar->json nil)` would return nil anyway (the `:else` branch in
`scalar->json`), but `some->` makes the nil-safety explicit and
would protect you if `scalar->json` ever changed to throw on nil.

In Scheme, the equivalent is a chain of `and`:

```scheme
(and (goal-id goal) (scalar->json (goal-id goal)))
```

`some->` is more concise and avoids evaluating the accessor twice.

---

## `cycle-snapshot` — Building one cycle's record

```clojure
(defn cycle-snapshot
  [world {:keys [goal-id selected-goal top-candidate-ids top-candidates
                 active-indices retrievals chosen-node-id selection
                 feedback-applied serendipity-bias situations context-id
                 sprouted active-plan backtrack-events mutations
                 terminations timestamp goal-selection]
          :or {active-indices []
               retrievals []
               active-plan []
               backtrack-events []
               mutations []
               situations {}
               goal-selection :highest_strength}}]
  (let [selected-goal (or selected-goal
                          (when goal-id
                            (goal-summary world goal-id)))
        context-id (or context-id
                       (:context-id selected-goal)
                       (when goal-id
                         (get-in world [:goals goal-id :next-cx]))
                       (:reality-lookahead world)
                       (:reality-context world))
        sprouted (or sprouted
                     (if (and context-id (contains? (:contexts world) context-id))
                       (->> (cx/children world context-id)
                            (sort-by str)
                            vec)
                       []))
        top-candidates (or top-candidates
                           (mapv #(goal-summary world %) top-candidate-ids))
        terminations (or terminations (:termination-events world) [])]
    {:cycle-num (:cycle world)
     :timestamp timestamp
     :mode (:mode world)
     ...}))
```

This is the largest function in the file. It takes the world state
plus an opts map of override fields, and produces the canonical cycle
snapshot shape from the guardrails doc.

### Multi-source `or` chains

The `let` block has a distinctive pattern: each field is derived from
a priority chain using `or`.

```clojure
context-id (or context-id
               (:context-id selected-goal)
               (when goal-id
                 (get-in world [:goals goal-id :next-cx]))
               (:reality-lookahead world)
               (:reality-context world))
```

Read this as a priority list:

1. Explicit override (caller passed `:context-id` in the opts map)
2. The selected goal's own context
3. The goal's `:next-cx` field (if a goal-id was provided)
4. The world's reality-lookahead
5. The world's reality-context

The first non-nil value wins. This is how the function works during
incremental development: early on, the caller can provide explicit
overrides for fields the kernel does not yet compute. As the kernel
matures and `run-cycle` starts populating goal and context fields,
the overrides become unnecessary and the `or` chain naturally falls
through to the computed values.

In Scheme you would write nested `or`:

```scheme
(or context-id
    (goal-context-id selected-goal)
    (and goal-id (world-goal-next-cx world goal-id))
    (world-reality-lookahead world)
    (world-reality-context world))
```

The Clojure version reads identically. The one difference is `when`
vs `and`: `(when goal-id (get-in ...))` returns nil if `goal-id` is
nil, because `when` with a falsy test returns nil. In Scheme, `and`
returns `#f`, which is still falsy so `or` skips it -- same behavior.

### The output map

The returned map is the canonical trace shape:

```clojure
{:cycle-num       7
 :timestamp       "2026-03-12T..."
 :mode            :daydreaming
 :goal-selection  :highest_strength
 :selected-goal   {:id :g-1 :goal-type :roving :strength 0.8}
 :top-candidates  [...]
 :context-id      :cx-12
 :context-depth   3
 :sprouted        [:cx-13 :cx-14]
 :active-plan     []
 :active-indices  []
 :retrievals      []
 :chosen-node-id  nil
 :selection       nil
 :feedback-applied nil
 :serendipity-bias nil
 :situations      {}
 :backtrack-events []
 :mutations       []
 :terminations    []}
```

Every field is present, even if nil or empty. This makes the shape
predictable -- downstream code can destructure without checking for
missing keys.

---

## `append-cycle` — Accumulating the trace

```clojure
(defn append-cycle
  [world snapshot-opts]
  (update world :trace (fnil conj []) (cycle-snapshot world snapshot-opts)))
```

A one-liner. Takes the world and an opts map, builds a snapshot via
`cycle-snapshot`, and conjs it onto the `:trace` vector in the world.

The `(fnil conj [])` pattern: if `:trace` does not exist yet on the
world (nil), treat it as `[]` before conj-ing. You saw this in
`store-episode` with `(fnil conj #{})`. Same idea, different initial
collection.

This function is **pure**. It returns a new world with one more
snapshot appended to `:trace`. No I/O. The caller (a REPL wrapper or
runner) is responsible for persisting the trace to disk if needed.

**Not yet wired into control.** `run-cycle` in `control.clj` does not
call `append-cycle` yet. The trace infrastructure exists; the
integration point is the next step. When it happens, `run-cycle` will
call `append-cycle` with the cycle's results and the world will
accumulate a complete trace as it runs.

---

## `reporter-cycle` — Translating to the Python reporter's shape

```clojure
(defn reporter-cycle
  [snapshot]
  {"cycle" (:cycle-num snapshot)
   "timestamp" (:timestamp snapshot)
   "goal_selection" (scalar->json (:goal-selection snapshot))
   "selected_goal" (reporter-goal (:selected-goal snapshot))
   "top_candidates" (mapv #(assoc (reporter-goal %)
                                  "reasons"
                                  (json-value (:reasons % [])))
                          (:top-candidates snapshot))
   "active_indices" (json-value (:active-indices snapshot))
   "retrieved" (mapv (fn [hit]
                       {"node_id" (or (some-> (:node-id hit) scalar->json)
                                      (some-> (:episode-id hit) scalar->json)
                                      "n/a")
                        "episode_id" (some-> (:episode-id hit) scalar->json)
                        "retrieval_score" (or (:retrieval-score hit)
                                              (:marks hit)
                                              0.0)
                        "overlap" (json-value (or (:overlap hit) []))
                        "threshold" (:threshold hit)})
                     (:retrievals snapshot))
   "chosen_node_id" (or (:chosen-node-id snapshot) "n/a")
   "selection" (json-value (:selection snapshot))
   "feedback_applied" (json-value (:feedback-applied snapshot))
   "serendipity_bias" (:serendipity-bias snapshot)
   "situations" (json-value (:situations snapshot))})
```

This is the bridge function. It takes an internal EDN snapshot and
produces the JSON-shaped map that `daydream_trace_report.py` expects.

Three transformations happen:

1. **Keys become snake_cased strings.** `:goal-selection` becomes
   `"goal_selection"`, `:selected-goal` becomes `"selected_goal"`.
   These are literal string keys in the output map, not computed --
   the function hand-writes each key to match the Python schema.

2. **Keywords become strings.** `scalar->json` and `json-value`
   handle the value conversion.

3. **Data is reshaped.** The `:retrievals` vector is restructured
   to match the Python reporter's `"retrieved"` array, with field
   names like `"node_id"`, `"retrieval_score"`, `"overlap"`. The
   `or` chains inside the retrieval map handle field aliasing --
   the kernel might store `:marks` but the reporter expects
   `"retrieval_score"`, so `(or (:retrieval-score hit) (:marks hit)
   0.0)` checks both names with a fallback to 0.0.

The output is a Clojure map with string keys. When the edge
serializes this with `clojure.data.json/write-str`, it produces
valid JSON that the Python reporter can consume directly.

---

## `reporter-log` — The top-level JSON envelope

```clojure
(defn reporter-log
  [world metadata]
  (let [metadata (json-value metadata)]
    (into {"cycles" (mapv reporter-cycle (:trace world))}
          (map (fn [key]
                 [key (or (get metadata key)
                          (get metadata (keyword key)))])
               reporter-top-level-keys))))
```

Wraps all cycle snapshots into the top-level JSON shape. The
`reporter-top-level-keys` vector at the top of the file defines the
expected fields: `"started_at"`, `"seed"`, `"world_path"`,
`"git_commit"`, various SHA256 hashes, etc.

The `into` expression starts with `{"cycles" [...]}` and then adds
metadata fields by iterating over `reporter-top-level-keys`. For
each key, it tries two lookups on the metadata map: the string key
first, then the keyword version. This handles the case where the
caller passes metadata with either string or keyword keys -- the
double lookup makes the function tolerant of both.

`mapv reporter-cycle` converts every internal snapshot in `:trace`
to the reporter shape. The result is the complete JSON document that
the Python reporter reads.

---

## `dreamer-state-packet` — The Director's view

```clojure
(defn dreamer-state-packet
  [snapshot]
  {"mode" (some-> (:mode snapshot) scalar->json)
   "goal_type" (some-> snapshot :selected-goal :goal-type scalar->json)
   "active_indices" (json-value (:active-indices snapshot))
   "retrieved_episodes" (mapv #(scalar->json (:episode-id %))
                              (:retrievals snapshot))
   "active_plan_chain" (json-value (:active-plan snapshot))
   "episode_cause" (:episode-cause snapshot)
   "trace_context_id" (some-> (:context-id snapshot) scalar->json)})
```

A reduced projection of a cycle snapshot, designed for the Director --
the LLM interpreter that translates dreamer state into visual prompts.
The Director does not need the full snapshot; it needs just enough
to know what mode the system is in, what kind of goal is active, and
what episodes were retrieved.

The `some->` chain on the second line is notable:

```clojure
(some-> snapshot :selected-goal :goal-type scalar->json)
```

This threads `snapshot` through three steps: get `:selected-goal`,
then get `:goal-type` from that, then convert to string. If any step
returns nil (no selected goal, or goal has no type), the whole chain
returns nil. Without `some->`, you would need nested nil checks:

```scheme
(and (selected-goal snapshot)
     (goal-type (selected-goal snapshot))
     (scalar->json (goal-type (selected-goal snapshot))))
```

---

## Connection to the Python Engine

`daydream_trace_report.py` already exists in the Python toolchain. It
reads a JSON trace log and generates an HTML report showing what
happened each cycle: which goal was selected, what episodes were
retrieved, what decisions were made.

The reporter adapter (`reporter-cycle`, `reporter-log`) means the
Clojure kernel can produce trace logs that this existing tool
understands. The output format is identical -- same field names, same
nesting structure. This enables:

- **Side-by-side comparison.** Run the same scenario through the
  Python engine and the Clojure kernel. Feed both trace logs to the
  HTML reporter. Compare the reports cycle by cycle to verify that
  the kernel matches the engine's behavior.

- **Incremental adoption.** The existing HTML reporter does not need
  to change. As the kernel matures, its trace logs become drop-in
  replacements for the Python engine's output.

- **Two consumers, one source of truth.** The internal EDN shape
  serves Clojure-side tools (REPL inspection, test assertions,
  future Clojure-native reporters). The reporter shape serves
  Python-side tools. Both are derived from the same cycle snapshot,
  so they can never disagree.

---

## Pattern Summary

| Pattern | Where | What it does |
|---|---|---|
| `cond->` | `goal-summary` | Conditionally assoc fields onto a map -- declarative optional keys |
| `some->` | `reporter-goal`, `dreamer-state-packet` | Nil-safe threading; short-circuits the chain if any step returns nil |
| `into {}` with map transducer | `json-value` | Transform key-value pairs and collect into a new map in one pass |
| `mapv` vs `map` | Throughout | Eager vector result; avoids lazy seqs in data structures |
| Multi-source `or` chains | `cycle-snapshot` | Derive a value from a priority list of sources; first non-nil wins |
| `fnil` | `append-cycle` | Nil-safe wrapper; treats missing `:trace` as `[]` |
| `defn-` | `scalar->json`, `json-value`, etc. | Namespace-private function (callers outside the ns cannot access it) |
| Literal string keys | `reporter-cycle`, `reporter-log` | Output maps use `"snake_case"` strings to match Python JSON schema |
