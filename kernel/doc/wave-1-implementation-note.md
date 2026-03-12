# Wave 1 Implementation Note

This note fixes the coding contract for wave 1 of the Clojure sidecar.

It is intentionally short. The purpose is to remove ambiguity before writing
`daydreamer.context`, not to reopen architecture design.

Use alongside:

- `doc/parallel-track-spec.md`
- `doc/source-inventory.md`
- `daydream-round-03/daydreamer/gate_cx.cl`
- `daydream-round-03/daydreamer/dd_cntrl.cl`

## Locked Decisions

These decisions are fixed for wave 1 unless implementation pressure proves they
are wrong.

### Namespace

- Use `daydreamer.*`, not `daydreamer-kernel.*`.

### Representation

- Use plain maps, not records or protocols.
- Use immutable kernel state snapshots.
- Keep atoms/refs out of core logic.
- If a REPL helper wants mutable convenience later, put it at the edge.

### Development style

- Data-first functions.
- Small namespaces.
- Tests before framework.
- Review idiom after the first real module (`context`), not before every
  helper.

## Wave 1 Scope

Wave 1 is the kernel only:

1. `daydreamer.context`
2. `daydreamer.goals`
3. `daydreamer.control`
4. `daydreamer.episodic-memory`
5. `daydreamer.trace`

The first real implementation target is `daydreamer.context`.

Wave 1 does **not** include:

- full GATE unification
- full rule engine
- English generation
- live runtime integration
- goal-family implementations beyond whatever minimal scaffolding `control`
  needs to compile

## Kernel State Shape

Wave 1 functions should operate on a single immutable kernel-state map.

```clojure
{:engine
 {:mode :daydreaming
  :next-context-number 1
  :next-goal-number 1
  :next-episode-number 1}

 :contexts
 {:cx/1 {...}
  :cx/2 {...}}

 :goals {}
 :episodes {}
 :trace []}
```

Notes:

- The `context` module owns `:contexts` and `:engine/:next-context-number`.
- Later modules may extend `:engine`, but should not change this ownership.
- Prefer deterministic ids like `:cx/1`, `:cx/2`, etc.

## Context Representation

Each context is a plain map.

```clojure
{:context/id :cx/1
 :parent nil
 :ancestors []
 :children []
 :pseudo-sprout? false
 :timeout nil
 :mutations-tried? false
 :ordering 1.0
 :gen-switches nil
 :touched-facts #{}
 :all-facts #{}
 :added-facts #{}
 :removed-facts #{}
 :type-index {}}
```

Field meanings:

- `:parent`: parent context id or `nil`
- `:ancestors`: nearest parent first, matching Mueller
- `:children`: child ids in creation order
- `:pseudo-sprout?`: true when attached as a pseudo-sprout
- `:timeout`: inherited from parent and decremented on sprout when numeric
- `:mutations-tried?`: inherited flag
- `:ordering`: branch selection priority
- `:gen-switches`: carried through for later use
- `:touched-facts`: facts touched in this context since last inference pass
- `:all-facts`: effective visible fact set
- `:added-facts`: facts added locally
- `:removed-facts`: inherited facts removed locally
- `:type-index`: map of `fact-type -> #{facts}`

## Fact Assumptions

Wave 1 uses the simplest possible fact contract.

Facts are plain maps with at least:

```clojure
{:fact/type :active-goal
 :fact/id :goal/7
 ...}
```

Rules:

- Fact identity is value equality for wave 1.
- `:fact/type` is mandatory for anything stored in a context.
- If later modules need richer object identity, that can be added without
  changing the context API.

This is enough to recover `cx$assert`, `cx$retract`, typed retrieval, and tree
visibility.

## Context API Contract

This is the first API to lock before implementation.

### Construction

```clojure
(empty-state)
=> initial kernel-state

(create-root-context state)
=> [state' context-id]

(create-root-context state {:ordering 0.8 :timeout 4})
=> [state' context-id]
```

### Tree operations

```clojure
(sprout state parent-id)
=> [state' child-id]

(sprout state parent-id {:ordering 0.6})
=> [state' child-id]

(pseudo-sprout state parent-id)
=> [state' child-id]

(pseudo-sprout state parent-id {:ordering 0.4})
=> [state' child-id]
```

Semantics:

- `sprout` copies visible content and inherited metadata from the parent.
- `pseudo-sprout` attaches a new context as a child but does **not** inherit
  the parent's visible content.
- Both update the parent's `:children`.
- Both update `:ancestors`.
- `sprout` decrements numeric timeout from the parent.
- `pseudo-sprout` starts with empty fact content unless explicit seed content is
  later added.

### Fact operations

```clojure
(assert-fact state context-id fact)
=> state'

(retract-fact state context-id fact)
=> state'

(context-facts state context-id)
=> #{...}

(context-facts-of-type state context-id :active-goal)
=> #{...}

(fact-visible? state context-id fact)
=> true | false
```

Semantics:

- Asserting a new fact adds it to `:all-facts`, `:added-facts`, `:type-index`,
  and `:touched-facts`.
- Asserting a fact already visible in the context is a no-op.
- Retracting a locally added fact removes it from `:added-facts` and
  `:all-facts`.
- Retracting an inherited visible fact adds it to `:removed-facts` and removes
  it from `:all-facts`.
- Retracting a fact not visible in the context is a no-op.

### Navigation

```clojure
(parent-id state context-id)
=> context-id | nil

(ancestor-ids state context-id)
=> [...]

(child-ids state context-id)
=> [...]

(leaf-descendant-ids state context-id)
=> [...]

(descendant-ids state context-id)
=> [...]
```

### Introspection

```clojure
(context state context-id)
=> context-map
```

Keep the API small. If a helper is not needed by tests or the next module, do
not add it yet.

## Initial Implementation Order Inside `daydreamer.context`

Implement in this order:

1. `empty-state`
2. `create-root-context`
3. `context`
4. `sprout`
5. `pseudo-sprout`
6. `parent-id` / `ancestor-ids` / `child-ids`
7. `assert-fact`
8. `retract-fact`
9. `context-facts`
10. `context-facts-of-type`
11. `fact-visible?`
12. `descendant-ids` / `leaf-descendant-ids`

This order lets tests grow from structure to behavior.

## Required Test Cases For `context`

These tests should exist before moving to `control`.

### 1. Root creation

- creates `:cx/1`
- has no parent or ancestors
- has empty fact sets

### 2. Sprout inheritance

- child gets parent in `:parent`
- child gets parent in `:ancestors`
- parent gets child in `:children`
- child inherits visible facts
- child inherits `:mutations-tried?`, `:gen-switches`, and decremented numeric
  `:timeout`

### 3. Pseudo-sprout behavior

- child is attached to parent structurally
- child has `:pseudo-sprout? true`
- child has parent/ancestor links
- child does **not** inherit fact content

### 4. Assert visibility

- asserting a fact makes it visible
- asserting same fact twice is a no-op
- type index updates correctly

### 5. Retract local vs inherited

- retracting locally added fact removes it cleanly
- retracting inherited fact records local removal without touching ancestor

### 6. Tree navigation

- `descendant-ids` walks all descendants
- `leaf-descendant-ids` returns only leaves

## Relation To Mueller Source

The source anchors for this first module are:

- `gate_cx.cl`
  - `cx$sprout`
  - `cx$pseudo-sprout-of`
  - `cx$assert`
  - `cx$retract`
  - `cx$parent`
  - `cx$ancestors`
  - `cx$children`
  - `cx$leaf-descendants`
  - `cx$descendants`

The first correctness goal is not "idiomatic in the abstract." It is:

- structurally faithful to Mueller
- inspectable as Clojure data
- small enough to support the next module cleanly

## What Happens Next

Once `daydreamer.context` and its tests are in place:

1. review naming and idiom once
2. freeze the context API unless something truly breaks
3. move to `daydreamer.control`

If `control` immediately exposes a missing context helper, add it then.
Otherwise keep the module minimal.
