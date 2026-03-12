# Clojure for Schemers — Patterns in daydreamer-kernel

A guide to reading the kernel code, aimed at someone with Scheme/Lisp
background who hasn't written Clojure. All examples are drawn from
`context.clj` so you can follow along.

---

## Square Brackets: Three Jobs

In Scheme, everything is parens. Clojure uses square brackets for three
things, which can be confusing at first.

### 1. Function parameters

```clojure
(defn sprout [world parent-id]    ;; two params
  ...)
```

Scheme equivalent: `(define (sprout world parent-id) ...)`

### 2. Binding forms (let, loop)

```clojure
(let [x 1                         ;; bind x to 1
      y (+ x 2)]                  ;; bind y to 3
  y)
```

Pairs of name/value, read left to right. Scheme equivalent:
`(let ((x 1) (y (+ x 2))) y)`

### 3. Literal vectors

```clojure
[1 2 3]                           ;; a vector (indexed, not a linked list)
```

Vectors are like arrays with O(1) lookup. Clojure lists `'(1 2 3)` exist
but vectors are the default collection.

### Destructuring

The same vector syntax works on the left side of a `let` to unpack:

```clojure
(let [[world child-id] (next-context-id world)]
  ;; world = first element, child-id = second
  ...)
```

Python equivalent: `world, child_id = next_context_id(world)`

Map destructuring uses curly braces:

```clojure
(let [{:keys [children parent-id]} (context-or-throw world cx-id)]
  ;; children = (:children the-map)
  ;; parent-id = (:parent-id the-map)
  ...)
```

This is used heavily in `sprout` (line 66) to pull fields out of the
parent context.

---

## Keywords

`:foo` is a keyword — a self-evaluating interned symbol. Used as:

- **Map keys:** `{:name "Alice" :age 30}`
- **Enum values:** `:runable`, `:halted`, `:waiting`
- **Identifiers:** `:cx-1`, `:cx-2` (our context ids)

Keywords are also functions that look themselves up in maps:

```clojure
(:name {:name "Alice" :age 30})   ;; => "Alice"
```

Scheme has symbols (`'foo`) but not keywords as a separate type.

---

## Maps (the core data structure)

Clojure maps are persistent (immutable, structural sharing). Every context,
goal, and episode in the kernel is a plain map.

```clojure
{:id :cx-1
 :parent-id nil
 :children #{}
 :all-obs #{:sky :grass}}
```

### Reading from maps

```clojure
(get m :foo)           ;; explicit lookup
(:foo m)               ;; keyword-as-function (idiomatic)
(get-in m [:a :b :c])  ;; nested lookup: m[:a][:b][:c]
```

### "Mutating" maps (returns new map, original unchanged)

```clojure
(assoc m :foo 42)                    ;; set a key
(dissoc m :foo)                      ;; remove a key
(update m :count inc)                ;; apply fn to value
(assoc-in m [:contexts :cx-1 :timeout] 9)   ;; nested set
(update-in m [:contexts :cx-1 :children] conj :cx-2)  ;; nested update
```

These all return **new maps**. The original is untouched. This is how the
kernel stays pure — every function takes a world map and returns a new one.

---

## Sets

```clojure
#{}                    ;; empty set
#{:a :b :c}            ;; set literal
(conj #{:a} :b)        ;; => #{:a :b}
(disj #{:a :b} :a)     ;; => #{:b}
(contains? #{:a} :a)   ;; => true
```

Used for `all-obs`, `add-obs`, `remove-obs`, `children`, `touched-facts`.

---

## Threading Macros

These are Clojure's pipeline operators. They rewrite nested calls into
a readable chain.

### `->` (thread-first)

Inserts the result as the **first argument** of each form:

```clojure
(-> world
    (assoc-in [:contexts child-id] child)
    (update-in [:contexts parent-id :children] conj child-id))
```

Expands to:

```clojure
(update-in
  (assoc-in world [:contexts child-id] child)
  [:contexts parent-id :children]
  conj child-id)
```

This is the main pattern in the kernel. Every state transformation reads
top-to-bottom: "start with world, then set the child, then add child to
parent's children."

### `->>` (thread-last)

Same idea but inserts as the **last argument**. Used with collection
functions:

```clojure
(->> [1 2 3 4 5]
     (filter odd?)      ;; => (1 3 5)
     (map inc)           ;; => (2 4 6)
     (reduce +))         ;; => 12
```

---

## `defn` vs `defn-`

```clojure
(defn sprout ...)          ;; public function
(defn- context-or-throw ...)  ;; private function (namespace-local)
```

`defn-` is shorthand for `(defn ^:private ...)`. Private functions can't
be called from other namespaces.

In context.clj: `context-or-throw`, `next-context-id`, `descendant-ids*`,
and `rebase-subtree` are all private. Everything else is the public API.

---

## `cond` — Multi-branch conditional

```clojure
(cond
  (not (contains? all-obs fact))   ;; test 1
  world                            ;; result 1

  (contains? add-obs fact)         ;; test 2
  (-> world ...)                   ;; result 2

  :else                            ;; default (`:else` is truthy)
  (-> world ...))                  ;; default result
```

Scheme equivalent: `(cond ((not ...) world) ((contains? ...) ...) (else ...))`

The Clojure version uses `:else` (a keyword, which is truthy) instead of
Scheme's `else`.

---

## `fnil` — nil-safe function wrapper

```clojure
(fnil conj #{})
```

Creates a version of `conj` that replaces a `nil` first argument with `#{}`.
So `((fnil conj #{}) nil :a)` => `#{:a}` instead of throwing.

Used in the kernel when a map key might not exist yet:

```clojure
(update-in world [:contexts cx-id :children] (fnil conj #{}) child-id)
;;          if :children is nil, treat it as #{}, then conj child-id
```

---

## `reduce` — The workhorse

```clojure
(reduce
  (fn [acc child-id]                ;; accumulator, current element
    (into acc (leaf-descendants world child-id)))
  #{}                               ;; initial accumulator
  children)                         ;; collection to reduce over
```

Same as Scheme's `fold-left` / `foldl`. The kernel uses it for tree walks
(collecting descendants) and for applying multiple assertions.

`into` pours one collection into another: `(into #{1} #{2 3})` => `#{1 2 3}`.

---

## `ex-info` / `throw` — Structured errors

```clojure
(throw (ex-info "Unknown context"
                {:context-id cx-id}))
```

`ex-info` creates an exception with a message and a **data map**. This is
Clojure's convention — attach structured data to errors so callers can
inspect them programmatically, not just parse strings.

---

## The World-Threading Pattern

This is the architectural pattern of the whole kernel, not a Clojure
built-in:

```clojure
(let [root (cx/create-context)
      world {:contexts {(:id root) root} :id-counter 1}
      world (cx/assert-fact world :cx-1 :sky-is-blue)
      [world child-id] (cx/sprout world :cx-1)
      world (cx/retract-fact world child-id :sky-is-blue)]
  world)
```

Notice `world` is rebound on every line. Each function takes the current
world state and returns a new one. This is like a state monad but explicit —
you can see exactly where state flows.

When a function also needs to return a value (like `sprout` returning the
new id), it returns a vector `[world value]` and the caller destructures it:

```clojure
(let [[world child-id] (cx/sprout world :cx-1)]
  ...)
```

This pattern means:
- Every intermediate state is inspectable (just print `world`)
- No hidden mutation anywhere
- You can diff two world states to see exactly what changed
- Tracing is trivial — snapshot world before and after each cycle

---

## Namespace Mechanics

```clojure
(ns daydreamer.context
  "Docstring..."
  (:refer-clojure :exclude [ancestors])  ;; don't import clojure.core/ancestors
  )
```

- `ns` declares the namespace. Must match the file path:
  `daydreamer.context` → `src/daydreamer/context.clj`
- `:refer-clojure :exclude` prevents shadowing warnings when you define
  a function with the same name as a core function
- `(:require [daydreamer.context :as cx])` in other files imports the
  namespace with alias `cx`, so `cx/sprout` calls `daydreamer.context/sprout`

---

## `declare` — Forward references

```clojure
(declare visible-facts)
```

Clojure compiles top-to-bottom. Unlike Common Lisp, you can't call a
function before it's defined. `declare` creates a placeholder so
`assert-fact` (line 108) can reference `visible-facts` (line 139).

Scheme doesn't need this because `define` creates bindings at load time,
not sequentially. This is one of the most common agent mistakes in Clojure.

---

## `comment` — REPL scratch pad

```clojure
(comment
  (create-context))
```

The `comment` form is never executed. It's a convention for leaving
REPL-testable examples in the source. You can select the forms inside
and evaluate them in your editor. Think of it as inline documentation
that's also runnable.

---

## Quick Reference: Scheme → Clojure

| Scheme | Clojure | Notes |
|--------|---------|-------|
| `(define (f x) ...)` | `(defn f [x] ...)` | square bracket params |
| `(let ((x 1)) ...)` | `(let [x 1] ...)` | flat pairs, not nested |
| `(lambda (x) ...)` | `(fn [x] ...)` or `#(... % ...)` | `%` = first arg in shorthand |
| `'(1 2 3)` | `[1 2 3]` | vectors are default |
| `(car xs)` | `(first xs)` | |
| `(cdr xs)` | `(rest xs)` | |
| `(cons x xs)` | `(cons x xs)` | same, but returns a seq not a pair |
| `(set! x 5)` | — | no mutation in core |
| `(begin ...)` | `(do ...)` | |
| `(display x)` | `(println x)` | |
| `#t` / `#f` | `true` / `false` | also: `nil` is falsy |
| hash tables | `{:k v}` | persistent, immutable |
| `fold-left` | `reduce` | |
| `(eq? a b)` | `(= a b)` | value equality by default |
