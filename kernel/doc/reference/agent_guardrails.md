# Agent Guardrails & Development Support

Snapshot of decisions, conventions, and tooling for AI agents working on
`daydreamer-kernel`. This document is the source of truth — copy relevant
sections into `CLAUDE.md` or `AGENTS.md` at the repo root when ready.

---

## Stack

- **JVM Clojure 1.12** (deps.edn, no Leiningen)
- **Babashka** for task runner (`bb.edn` — to be created)
- **clj-kondo** for linting (`.clj-kondo/config.edn` — to be created)
- **cljfmt** for formatting (defaults follow community style guide)
- **cognitect test-runner** (already in deps.edn `:test` alias)
- No ClojureScript, no frontend, no web server

## Core Representation Decisions

These are **locked for wave 1**. Do not introduce records, protocols,
multimethods, or dynamic vars without explicit approval.

### Plain maps, not records

Everything is a plain Clojure map. Records and protocols are premature
abstraction for a research kernel. Plain maps are inspectable, printable,
serializable, and diffable.

### Pure kernel, impure edges

The kernel (context, goals, episodic-memory, control) is **pure functions
operating on immutable values**. No atoms, refs, or agents in core logic.

Mutable state lives only at the boundary:
- REPL wrappers (atom holding current world state)
- Trace emission (side-effecting JSON write)
- Session logging

Each cognitive cycle takes a world-state value and returns a new world-state
value. This is the sidecar's primary advantage: every cycle is an inspectable
snapshot.

### Persistent snapshots

Clojure's persistent data structures are the mutation model. "Mutating" a
context means producing a new map that shares structure with the old one.
No defensive copying needed.

---

## Data Shapes

These are the core map shapes. All keys are unqualified keywords for wave 1.
Namespace-qualified keys may come later if schema tooling warrants it.

### Context

Source: `gate_cx.cl` — `cx$sprout`, `cx$pseudo-sprout-of`

```clojure
{:id              :cx-1        ; unique keyword
 :parent-id       nil          ; parent context id, or nil for root
 :children        #{}          ; set of child context ids
 :ancestors       []           ; vec of ancestor ids, parent-first
 :all-obs         #{}          ; all facts visible (inherited + added - removed)
 :add-obs         #{}          ; facts added in this context
 :remove-obs      #{}          ; facts removed from inherited set
 :pseudo-sprout?  false        ; true = child of parent but doesn't inherit content
 :mutations-tried? false       ; set true after mutation attempt
 :timeout         10           ; decremented each sprout; skip when <= 0
 :ordering        0            ; priority for sprout selection during backtracking
 :touched-facts   #{}}         ; facts modified in this context (for inference chaining)
```

**Design note:** `all-obs` is eagerly maintained (copied from parent on sprout,
updated on assert/retract), matching Mueller's approach. Lazy derivation via
ancestry walk is a possible optimization but not for wave 1.

### Goal

Source: `dd_cntrl.cl` — slots documented at lines 629–653

```clojure
{:id               :g-1
 :goal-type        :reversal    ; :roving, :rationalization, :recovery,
                                ; :rehearsal, :revenge, :repercussions
 :planning-type    :imaginary   ; :real or :imaginary
 :status           :runable     ; :runable, :halted, :waiting
 :strength         0.8          ; competition value (decays)
 :main-motiv       :neg-e-1     ; id of main motivation emotion
 :activation-cx    :cx-3        ; context where goal was activated
 :termination-cx   nil          ; context where goal terminated (nil while active)
 :next-cx          nil          ; next context to execute (imaginary only)
 :backtrack-wall   :cx-3        ; backtrack limit context (imaginary only)
 :top-level-goal   :g-1}        ; self-ref for top-level, parent-ref for subgoals
```

### Episode

Source: `dd_epis.cl` — `make-episode`, `epmem-store`

```clojure
{:id                  :ep-1
 :rule                :reversal-rule-1   ; rule that created this episode
 :goal-id             :g-1               ; goal this episode is about
 :context-id          :cx-5              ; context where episode was generated
 :realism             0.7                ; how realistic (nil = unrated)
 :desirability        0.5                ; how desirable (nil = unrated)
 :indices             #{:anger :betrayal :s4}  ; retrieval index set
 :plan-threshold      2                  ; marks needed for plan retrieval
 :reminding-threshold 2                  ; marks needed for reminding
 :children            []                 ; child episode ids
 :descendants         []}                ; all descendant episode ids
```

**Note:** marks are transient per retrieval pass, not stored on the episode.

### Cycle Snapshot (Trace)

New to the sidecar — no Mueller equivalent. Must be compatible with
Codex's `daydream_trace_report.py` JSON schema.

```clojure
{:cycle-num       7
 :mode            :daydreaming     ; :performance or :daydreaming
 :selected-goal   {:id :g-1 :goal-type :roving :strength 0.8}
 :context-id      :cx-12
 :context-depth   3
 :sprouted        [:cx-13 :cx-14]
 :active-plan     []
 :retrievals      [{:episode-id :ep-3 :marks 2 :threshold 2}]
 :backtrack-events []
 :mutations       []
 :terminations    []}
```

### World State (top-level value threaded through cycles)

```clojure
{:contexts       {}    ; id -> context map
 :goals          {}    ; id -> goal map
 :episodes       {}    ; id -> episode map
 :emotions       {}    ; id -> emotion map (TBD wave 1)
 :mode           :daydreaming
 :cycle          0
 :trace          []    ; vec of cycle snapshots
 :id-counter     0}    ; monotonic id source
```

---

## First API Contract (Context Module)

These are the public functions for `daydreamer.context`. Lock this before
writing implementation.

```clojure
;; Creation
(create-context)                    ; -> context map (root, no parent)
(sprout world parent-id)            ; -> [world new-context-id]
(pseudo-sprout world child-id parent-id) ; -> world (re-parents child under parent)

;; Fact management
(assert-fact world cx-id fact)      ; -> world
(retract-fact world cx-id fact)     ; -> world

;; Queries
(visible-facts world cx-id)         ; -> set of all facts visible in context
(fact-true? world cx-id fact)       ; -> boolean
(leaf-descendants world cx-id)      ; -> set of leaf context ids
(ancestors world cx-id)             ; -> vec of ancestor context ids
(children world cx-id)              ; -> set of child context ids

;; Tree operations
(copy-context world source-id target-parent-id)  ; -> [world new-cx-id]
                                    ; Used by sprout-alternative-past
```

**Naming convention:** functions take `world` (the full world-state map) as
first arg and return updated `world` (or `[world result]` when also producing
a value). This enables threading with `->`.

---

## Agent Coding Rules

### Run after every change

```
bb check   # = bb format && bb lint && bb test
```

(Once `bb.edn` is set up. Until then: `clj -M:test`)

### Constraints

- **Preserve public APIs** unless explicitly told to change them.
- **Prefer functions to macros.** Do not introduce macros unless asked.
- **Do not introduce** protocols, multimethods, records, or dynamic vars
  unless asked.
- **Prefer pure functions and data transformation.**
- **Prefer opts maps** over more than 3 positional args.
- **One namespace per file.** File path matches namespace.
- **Prefer `:require` with aliases.** Never use `:use`.
- **Standard aliases:** `str`, `set`, `io`, `edn`, `walk`, `s`, `pp`, `sh`.
- **For every changed public function,** add a test or a `(comment ...)` REPL
  example with real sample data.
- **Minimal diffs.** Don't reformat or restructure code you didn't change.
- **Ask for patches, not rewrites.** Preserve existing structure.
- **No single-segment namespaces.** All namespaces start with `daydreamer.`
- **Destructure** instead of `nth` or `get` chains.
- **Threading macros** for straight pipelines, not for showing off.
- **No `def` inside functions.** Local bindings use `let`.
- **Don't shadow `clojure.core`** names.
- **No useless anonymous wrappers** like `#(even? %)` when `even?` works.
- **Prefer higher-order functions** (`map`, `filter`, `reduce`) over manual
  `loop/recur` for collection transforms. Use `loop/recur` for stateful
  iteration or when the problem genuinely needs it.

### Testing

- **Golden tests** for `cx$sprout` and `cx$pseudo-sprout-of` behavior,
  derived from Mueller source examples.
- **Property: sprouting preserves parent facts.** A sprouted context must
  see all parent facts.
- **Property: pseudo-sprout does NOT inherit.** A pseudo-sprouted context
  must NOT see parent facts.
- **Property: assert/retract roundtrip.** Assert then retract returns to
  original state.
- Test files live in `test/daydreamer/` mirroring `src/daydreamer/`.
- Use `clojure.test` — no external test frameworks.

---

## Tooling To Set Up

These don't exist yet. Create them before or alongside the first
implementation pass.

### `bb.edn`

```clojure
{:tasks
 {:format {:doc "Format all source files"
           :task (shell "clojure -M:cljfmt fix src test")}
  :lint   {:doc "Lint with clj-kondo"
           :task (shell "clj-kondo --lint src test")}
  :test   {:doc "Run all tests"
           :task (clojure "-M:test")}
  :check  {:doc "Format, lint, and test"
           :depends [:format :lint :test]}}}
```

(Exact shape will depend on how cljfmt is added to deps.edn.)

### `.clj-kondo/config.edn`

```clojure
{:output {:exclude-files []}
 :linters {:unused-namespace {:level :warning}
           :unused-referred-var {:level :warning}
           :unresolved-symbol {:level :error}
           :missing-else-branch {:level :off}}}
```

### Formatting

Use cljfmt defaults (which follow the community style guide already
in `doc/reference/clojure_style_guide.md`).

---

## Reference Material

| What | Where |
|------|-------|
| Community Clojure style guide | `doc/reference/clojure_style_guide.md` |
| Mueller source inventory | `doc/source-inventory.md` |
| Parallel track spec | `doc/parallel-track-spec.md` |
| Mueller source (Common Lisp) | `daydreamer/` in `daydream-round-03` repo |
| Python engine | `scope-drd/tools/daydream_engine.py` |
| Trace reporter | `scope-drd/tools/daydream_trace_report.py` |
| Puppet Knows fixture | `scope-drd/content/daydreams/puppet_knows/` |

---

## What This Document Is Not

This is a snapshot of current decisions and conventions. It is not:
- Architecture documentation (see `doc/parallel-track-spec.md`)
- Source analysis (see `doc/source-inventory.md`)
- A tutorial on Mueller's DAYDREAMER

Update this document when decisions change. When stable, promote relevant
sections to `CLAUDE.md` at the repo root.
