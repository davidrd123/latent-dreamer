# Session 07: Kernel Implementation

**Date:** 2026-03-12
**Focus:** Clojure toolchain setup, wave 1 implementation, Puppet Knows benchmark
**Agents:** Claude Opus (orchestration, code review, explainer docs), Codex (implementation)

---

## What Happened

### 1. Project Restructure

- Renamed `daydream-round-03` → `latent-dreamer` (was a hackathon round label, needed a real name)
- Merged separate `daydreamer-kernel` GitHub repo into `kernel/` subfolder (was causing confusion as a separate repo; David's project is private so GPL boundary doesn't require repo separation)
- Updated all Claude memory files to reflect new paths
- Added `.claude/` to `.gitignore` (keeps personal memory/settings out of repo)

### 2. Clojure Toolchain Setup (from zero)

Installed the full stack via Homebrew:
- **Java** (OpenJDK 25 via Temurin)
- **Clojure CLI** 1.12.4
- **Babashka** 1.12.216 (task runner)
- **bbin** (Babashka package manager)
- **clj-kondo** (linter)
- **clojure-mcp-light** via bbin — three commands:
  - `clj-nrepl-eval` — CLI nREPL client for agents
  - `clj-paren-repair` — standalone delimiter fixer (for Codex)
  - `clj-paren-repair-claude-hook` — auto paren repair hook (for Claude Code)

**bbin gotcha discovered:** when a repo has multiple `:bbin/bin` entries, `bbin install` only installs the first one alphabetically. Need `--as <name> --main-opts '["-m" "namespace"]'` to install specific commands. Also: all three initially pointed to the same hook namespace — the `--as` flag doesn't automatically select the right `:main-opts` from `bb.edn`.

**bb.edn gotcha discovered:** task names must be **symbols** (`lint`, `format`), not **keywords** (`:lint`, `:format`). Babashka silently ignores keyword-named tasks.

### 3. Agent Guardrails & Support Docs

Created `kernel/doc/reference/agent_guardrails.md` — the contract for AI agents writing Clojure in this project:
- **Locked decisions:** plain maps (no records/protocols), pure kernel with impure edges only, persistent snapshots (no atoms in core), explicit data shapes
- **Data shapes:** context, goal, episode, cycle snapshot, world-state maps — all fully specified
- **First API contract:** 11 public functions for `daydreamer.context`
- **Coding rules:** adapted from expert advice (Ivan Willig's 250k LOC practitioner report + community style guide)
- **Testing strategy:** golden tests, property tests, `clojure.test`

Created `kernel/AGENTS.md` — operational rules for both Claude Code and Codex:
- REPL workflow with `clj-nrepl-eval` examples
- Verification commands (`bb check` = format + lint + test)
- Clojure idiom constraints
- Data model summary

Copied community Clojure style guide into `kernel/doc/reference/clojure_style_guide.md`.

### 4. Expert Advice Integration

David shared two rounds of expert advice on AI agents writing Clojure (from `daydreaming/Notes/PreviousWork/clojure/`). Key insights integrated:

1. **REPL access is the biggest lever** — agents write bad Clojure mainly because they lack runtime truth, not because of prompting
2. **Make conventions executable** — clj-kondo, cljfmt, bb tasks, not prose
3. **Make data shapes explicit** — most bad agent Clojure is "map shape wrong," not syntax wrong
4. **Teach idioms explicitly** — agents drift to imperative style without rules
5. **ClojureMCP Light** for REPL integration — bridges Claude Code/Codex to nREPL

### 5. Wave 1 Implementation (Codex, reviewed by Claude)

Codex implemented all 5 core modules in a single session. Claude reviewed, fixed bugs, and wrote explainer docs in parallel.

#### context.clj — Mueller's `gate_cx.cl`
- `create-context`, `sprout`, `pseudo-sprout`, `assert-fact`, `retract-fact`
- `visible-facts`, `fact-true?`, `ancestors`, `children`, `leaf-descendants`, `copy-context`
- Eager `all-obs` (copied from parent on sprout), `add-obs`/`remove-obs` overlay
- **Bugs caught by REPL:** extra paren, forward reference to `visible-facts`, shadowed `clojure.core/ancestors`, unused defs

#### goals.clj — Mueller's `dd_cntrl.cl` (goal representation)
- `create-goal`, `activate-top-level-goal`, `change-status`, `record-termination`
- `motivating-emotion?`, `most-highly-motivated-goals`
- Key design: imaginary goals sprout a fresh planning context on activation; real goals stay anchored

#### control.clj — Mueller's `dd_cntrl.cl` (control loop)
- **First pass:** `set-state`, mode predicates, `need-decay`, `emotion-decay`, `run-cycle`
- **Deepening pass:** `prune-possibilities`, `run-goal-step`, `backtrack-top-level-goal`, `terminate-top-level-goal`, `all-possibilities-failed`
- Mode oscillation: performance → daydreaming → performance
- Backtracking walks up context tree to backtrack wall
- Termination stabilizes reality by sprouting fresh context

#### episodic_memory.clj — Mueller's `dd_epis.cl`
- `create-episode`, `add-episode`, `store-episode`
- `retrieve-episodes` — coincidence counting via `frequencies`
- `remindings`, `episode-reminding` — full reminding cascade with `loop/recur`
- Serendipity lowers threshold by 1
- FIFO bounds on recent indices/episodes

#### trace.clj — new module (no Mueller equivalent)
- `cycle-snapshot`, `append-cycle`, `merge-latest-cycle`
- `reporter-cycle`, `reporter-log` — adapter for Python `daydream_trace_report.py`
- `dreamer-state-packet` — reduced Director-facing view
- Auto-wired through control.clj (every cycle records a trace snapshot)

#### runner.clj — orchestrated scripted sessions
- `initial-world`, `activate-goals`, `run-scripted-session`
- Threads through real control loop, enriches trace, emits reporter JSON
- Pure — no I/O in the kernel

#### puppet_knows.clj — benchmark
- Encodes the 8→10 bridge with 9→10 revenge handoff
- `bb puppet-knows-trace` emits reporter JSON to `out/puppet_knows_benchmark.json`
- Round-trips through Python reporter into HTML

### 6. Explainer Docs for David

Written in parallel as Codex delivered modules — David can read at his own pace:
1. `clojure_for_schemers.md` — Clojure syntax mapped to Scheme (square brackets, keywords, maps, threading macros, destructuring, `defn-`, `cond`, `fnil`, `reduce`, world-threading pattern, Scheme→Clojure reference table)
2. `goals_and_control_walkthrough.md` — real vs imaginary goals, mode oscillation, `comp` transducer, emotion protection
3. `episodic_memory_walkthrough.md` — coincidence counting, `frequencies`, reminding cascade, `loop/recur` as named `let`, threshold increment logic
4. `control_deepening_walkthrough.md` — backtracking with ASCII diagrams, `cond->`, multi-arity functions, termination/reality stabilization
5. `trace_walkthrough.md` — pure trace design, `some->`, reporter adapter, `mapv` vs `map`

### 7. Git Commits (5 this session)

1. `f7804ee` — Add Clojure kernel: wave 1 (30 files, 9,206 lines)
2. `2dcffb3` — Add session 06 research (21 files, 8,280 lines)
3. `7242c0e` — Wire trace recording through control loop
4. `a88fc79` — Add orchestrated runner
5. `64cd2ac` — Add Puppet Knows benchmark

---

## Key Design Decisions Made

1. **Plain maps over records/protocols** — inspectable, printable, serializable, diffable. Records are premature abstraction for a research kernel.

2. **Pure kernel, impure edges** — every function takes a world-state value and returns a new one. No atoms/refs/agents in core logic. Mutable state only at REPL wrappers and trace file output.

3. **Eager `all-obs`** — Mueller eagerly maintains the full visible fact set on every context. Could be lazy (walk ancestry) but eager matches Mueller and truth-checking is the hot path.

4. **`world` as first arg everywhere** — state monad without the monad. Every function takes the world, returns the world. Enables `->` threading and makes every intermediate state inspectable.

5. **`[world result]` return pattern** — when a function needs to return both the updated world and a value (like `sprout` returning the new context id), it returns a two-element vector. Destructured with `(let [[world id] (sprout world parent-id)] ...)`.

6. **Symbols not keywords for bb.edn tasks** — learned the hard way.

7. **bbin `--main-opts` required** — bbin's `--as` flag doesn't auto-select the right namespace from `:bbin/bin` entries.

---

## What's Proven

- The core state model works (context tree, goals, episodes, control loop)
- The trace/reporting contract works (Clojure kernel → JSON → Python reporter → HTML)
- The kernel can express the Puppet Knows benchmark cleanly
- 44 tests, 170 assertions, 0 failures
- Full Clojure toolchain operational (REPL, lint, format, test, paren repair)

## What's NOT Proven Yet

- **The benchmark is scripted, not emergent.** The 9→10 handoff is encoded in `puppet_knows.clj`, not produced by the kernel's own goal families and planning.
- **REVERSAL/ROVING/RATIONALIZATION plan bodies don't exist yet.** Wave 2.
- **Multi-step planning and subgoal creation don't exist yet.** Needs a rule engine.
- **Fixture-driven execution from world.yaml/dream_graph.json without scripting doesn't exist yet.**

---

## Observations & Thoughts

### On the Codex + Claude workflow

This session was the first real parallel agent workflow: Codex implementing, Claude reviewing and writing explainer docs simultaneously. It worked well because the division was clean — Codex owns implementation, Claude owns review/docs/tooling. The guardrails doc was the shared contract that kept both agents aligned.

### On Clojure agent quality

The expert advice was right: the three bugs in Codex's first pass were exactly the predicted failure modes:
1. Unbalanced parens (paren repair hook would have caught this)
2. Forward reference (Clojure's sequential compilation, unlike Common Lisp)
3. Shadowing core names (agents don't naturally avoid `ancestors`, `children`, etc.)

All caught instantly by the REPL + lint loop. Without `clj-nrepl-eval` and `clj-kondo`, these would have been discovered much later.

### On Mueller faithfulness

The kernel is structurally faithful to Mueller where it matters (context tree, sprouting semantics, coincidence counting, mode oscillation) and deliberately diverges where Clojure is better (persistent data structures instead of mutable lists, pure functions instead of setf, explicit world-threading instead of global state). The `rebase-subtree` in pseudo-sprout is slightly beyond what Mueller does (recursive vs single-level) but arguably more correct.

### On the "generative vs descriptive" question

Wave 2 is where the sidecar either earns its keep or doesn't. The research question: does Mueller's full machinery (REVERSAL's sprout-alternative-past, multi-step planning, backtracking) produce behaviors the Python engine's flat scheduler can't reach? The benchmark infrastructure is now in place to answer this — same fixture, same reporter, side-by-side comparison.

### On `touched-facts` inheritance

One thing to verify in wave 2: `sprout` copies the parent's `touched-facts` into the child. In Mueller, `touched-facts` drives inference chaining — facts that changed are candidates for rule firing. Copying them might cause spurious rule triggers. Need to check whether Mueller clears `touched-facts` on sprout or inherits them.

---

## Next Session Plan

1. **Wave 2: REVERSAL first** — implement `sprout-alternative-past` in `goal_families.clj`. This is the most important function in the whole system — it creates "what if" as a first-class planning object.
2. **Then ROVING** — pleasant episode recall, simpler than REVERSAL.
3. **Goal family activation rules** — when to fire each goal type.
4. **Fixture loader** — read world.yaml and dream_graph.json dynamically.
5. **Unscripted Puppet Knows run** — the real test of whether the architecture is generative.

The key question for wave 2: can REVERSAL drive the 9→10 handoff without scripting?
