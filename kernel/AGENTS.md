# Clojure Agent Rules — daydreamer-kernel

## Stack

JVM Clojure 1.12 · deps.edn · cognitect test-runner · Babashka tasks · nREPL on port 7888

## Before You Code

- Read `doc/reference/agent_guardrails.md` — locked data shapes, API contracts, testing strategy.
- Read `doc/reference/clojure_style_guide.md` for idiom questions.
- Start nREPL: `cd kernel && bb nrepl` (or `clojure -M:nrepl`).

## REPL Workflow

Use `clj-nrepl-eval` to evaluate forms against the running nREPL before committing to code:

```bash
# Evaluate a form
clj-nrepl-eval -p 7888 '(+ 1 2)'

# Load a namespace
clj-nrepl-eval -p 7888 '(require (quote [daydreamer.context :as cx]))'

# Inspect a var
clj-nrepl-eval -p 7888 '(doc daydreamer.context/sprout)'

# Test a function against sample data
clj-nrepl-eval -p 7888 '(daydreamer.context/create-context)'

# Reset session before a fresh load
clj-nrepl-eval -p 7888 --reset-session '(require (quote [daydreamer.context :as cx]) :reload)'
```

Always inspect before you write. Use `doc`, `source`, `dir`, and `apropos` via the REPL
to verify APIs exist and have the expected arities before calling them.

## Verification

Run after every change:

```bash
cd kernel && bb check   # = format + lint + test
```

Or individually:

```bash
bb format   # cljfmt
bb lint     # clj-kondo
bb test     # cognitect test-runner
```

Prefer targeted checks while iterating. Full `bb check` at the end.

## Constraints

- **Plain maps only.** No records, protocols, multimethods, or dynamic vars unless explicitly asked.
- **Pure kernel, impure edges.** No atoms/refs/agents in core logic. Mutable state lives only in REPL wrappers.
- **Preserve public APIs** unless explicitly told to change them.
- **Prefer functions to macros.** Do not introduce macros unless asked.
- **Prefer higher-order functions** (`map`, `filter`, `reduce`, `update-in`) over `loop/recur` for collection transforms.
- **Prefer destructuring** over `nth`, `first`/`second` ladders, or `get` chains.
- **Prefer `->` / `->>`** for linear pipelines. Don't use threading to show off.
- **Prefer opts maps** over more than 3 positional args.
- **One namespace per file.** File path matches namespace (`daydreamer.context` → `src/daydreamer/context.clj`).
- **Prefer `:require` with aliases.** Never use `:use`.
- **Standard aliases:** `str`, `set`, `io`, `edn`, `walk`, `s`, `pp`, `sh`.
- **Never `def` inside a function.** Use `let`.
- **Don't shadow `clojure.core` names.**
- **No useless anonymous wrappers** — `even?` not `#(even? %)`.
- **Minimal diffs.** Don't reformat or restructure code you didn't change.

## Data Model

All state flows through a single `world` map:

```clojure
{:contexts  {}    ; id -> context map
 :goals     {}    ; id -> goal map
 :episodes  {}    ; id -> episode map
 :emotions  {}    ; id -> emotion map
 :mode      :daydreaming
 :cycle     0
 :trace     []
 :id-counter 0}
```

Functions take `world` as first arg and return updated `world` (or `[world result]`).
This enables threading: `(-> world (assert-fact cx-id fact) (sprout parent-id))`.

See `doc/reference/agent_guardrails.md` for full context/goal/episode map shapes.

## Testing

- Tests live in `test/daydreamer/` mirroring `src/daydreamer/`.
- Use `clojure.test` — no external frameworks.
- For every changed public function, add a test or a `(comment ...)` REPL example.
- Golden tests for Mueller-equivalent behavior (sprouting, pseudo-sprouts, retrieval).

## Paren Repair (Claude Code only)

Claude Code hooks automatically fix unbalanced delimiters and run cljfmt
on Write/Edit. This is transparent — no action needed from the agent.

For Codex/Gemini: run edited files through `clj-paren-repair` before final
verification, then use `bb format` to normalize formatting.
