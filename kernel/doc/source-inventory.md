# Source Inventory: Mueller's DAYDREAMER → Clojure Sidecar

This is deliverable #0 for the parallel track (10-daydreamer-parallel-track.md).
It maps every file in `daydreamer/` to the sidecar modules defined in the spec,
with priority assignments for each implementation wave.

## Sidecar Modules (from spec)

| Module | Purpose |
|---|---|
| `context` | Persistent context graph (sprouting, backtracking, pseudo-sprouts) |
| `goals` | Goal objects, status, strength, planning-type |
| `episodic-memory` | Episode store, index-threshold retrieval, reminding |
| `control` | Top-level loop: decay, goal selection, mode oscillation, planning step |
| `goal-families` | Per-family activation rules and plan bodies |
| `trace` | Structured logs for comparison with Python engine |

---

## Critical Path Files (Wave 1)

These files contain the architectural mechanisms that distinguish DAYDREAMER
from the simplified Python engine. They are the kernel.

### `dd_cntrl.cl` — Top-level control loop

**703 lines. Maps to: `control`, `goals`**

| Function | What it does | Sidecar module | Notes |
|---|---|---|---|
| `daydreamer` | Entry point: init, run inferences, enter control loop | `control` | |
| `daydreamer-control0` | THE control loop: need-decay, emotion-decay, select most-motivated goal, advance one step | `control` | This is the function to port first |
| `daydreamer-control1` | Per-goal control: run one planning step, check success, set next context or backtrack | `control` | The planning-step-then-backtrack logic |
| `most-highly-motivated-goals` | Goal competition by strength. Filters by mode (performance skips imaginary goals) | `goals` | Simple but load-bearing |
| `set-state` / `performance-mode?` / `daydreaming-mode?` | Mode oscillation between `performance`, `daydreaming`, `suspended` | `control` | **Key missing mechanism** — the Python engine has no real oscillation |
| `need-decay` | Multiply all need strengths by 0.98 each cycle | `control` | Trivial |
| `emotion-decay` | Multiply non-motivating emotion strengths by 0.95; GC below 0.15 | `control` | Trivial, but the "motivating-emotion?" check matters |
| `motivating-emotion?` | An emotion is motivating if any goal depends on it | `goals` | Determines which emotions are exempt from decay |
| `activate-top-level-goal` | Create goal, set planning-type (real/imaginary), sprout backtrack-wall context, attach emotions | `goals`, `context` | The birth of a top-level goal |
| `terminate-top-level-goal` | Emotional response, episode storage, reality stabilization, context sprouting | `control`, `episodic-memory` | The death of a top-level goal — this is where episodes get stored |
| `backtrack-top-level-goal` | Walk up context tree from current to backtrack-wall, try alternate sprouts, invoke mutation if exhausted | `control`, `context` | Backtracking is the escape from dead-end planning |
| `emotional-response` | Create POS/NEG-EMOTION based on goal outcome, other-causes, and altern? status | `goals` | The emotional response to goal success/failure |
| `all-possibilities-failed` | Last resort: try action-mutations, then terminate with failure | `control` | Links to `dd_mutation.cl` |
| `prune-possibilities` | Filter and sort sprouted contexts by ordering, skip already-run and dd-goal sprouts | `context` | |

**Key slots on top-level goals** (documented in comments at line 629–653):
- `status`: runable, halted, waiting
- `planning-type`: real, imaginary
- `backtrack-wall`: (imaginary only) backtrack limit context
- `next-context`: (imaginary only) next context to run
- `activation-context`: where the goal was first activated
- `termination-context`: where the goal was terminated
- `main-motiv`: main motivation emotion
- `top-level-goal`: self-pointer (also on subgoals)

### `gate_cx.cl` — Context mechanism

**617 lines. Maps to: `context`**

| Function | What it does | Notes |
|---|---|---|
| `cx$sprout` | Create child context inheriting parent's all-obs, type-hashing, gen-switches, touched-facts, mutations-tried?, timeout (decremented), pseudo-sprout? | **The fundamental operation.** Every planning step sprouts. |
| `cx$pseudo-sprout-of` | Make an existing root context appear to be a child of another context, but don't inherit content. Sets `pseudo-sprout? t` | **Used by REVERSAL** to create alternative pasts. Critical mechanism. |
| `cx$assert` | Assert fact into context: add to all-obs, add-obs, type-hash, touched-facts, top-context | |
| `cx$retract` | Retract fact from context: handle both add-obs and inherited (remove-obs) cases | |
| `cx$true?` | Check if fact is true in context: walk ancestry chain checking add-obs/remove-obs | The truth-maintenance logic |
| `cx$retrieve` | Pattern-match against all facts in context via unification | Sidecar can use simpler matching |
| `cx$parent` / `cx$ancestors` / `cx$children` | Tree navigation | |
| `cx$leaf-descendants` / `cx$descendants` | Recursive tree walks | Used by backtracking and mutation |
| `cx$copy` | Copy a context's contents into a new sprout of a given parent | Used by `sprout-alternative-past` |
| `cx$hypothesize` | Sprout, retract some facts, assert others | Used by REPERCUSSIONS |
| Context-sensitive links (`ol-get`, `ol-set`, `ol-path`) | Links (INTENDS, DEPENDENCY) are context-local | Dependency tracking |

**Context data shape:**
- `parent` — single parent context (or nil)
- `ancestors` — list of all ancestors (parent first)
- `children` — list of child contexts
- `all-obs` — all facts visible in this context (inherited + added - removed)
- `add-obs` — facts added in this specific context
- `remove-obs` — facts removed in this specific context (that were inherited)
- `type-hashing` — alist of (type . obs) for fast typed retrieval
- `touched-facts` — facts that have been modified (for inference chaining)
- `pseudo-sprout?` — if true, this context doesn't inherit content from parent
- `mutations-tried?` — inherited from parent, set true after mutation attempt
- `timeout` — decremented each sprout; planning step skipped when ≤ 0
- `gen-switches` — generation options (tense, etc.)
- `ordering` — priority for sprout selection

### `dd_epis.cl` — Episodic memory

**879 lines. Maps to: `episodic-memory`**

| Function | What it does | Notes |
|---|---|---|
| `make-and-store-episode` | Create episode object, set children/descendants, set thresholds, call `epmem-store` | Episode creation |
| `epmem-store` | Store episode under an index. Increment plan-threshold and/or reminding-threshold | The threshold increment is how multi-index episodes require multiple marks to retrieve |
| `epmem-retrieve1` | **THE retrieval algorithm.** For each index: find episodes stored under it, mark them. Episode retrieves when marks ≥ threshold. Serendipity lowers threshold by 1. Skip recent episodes. | **Already approximated in Python** but the mark/threshold mechanism should be recovered faithfully |
| `mark-mark` / `mark-init` / `mark-unmark-all` | Mark management for retrieval passes | |
| `remindings` | Top-level reminding: retrieve against recent-indices + emotion-indices | |
| `epmem-reminding` | On retrieval: add to recent, activate other indices of the episode, reactivate emotions, run serendipities, recursively check for new remindings | **The cascade.** Retrieving one episode activates its indices, which may retrieve more. |
| `add-recent-index` | Bounded list (max 6), FIFO | |
| `add-recent` | Bounded list (max 4), FIFO, with descendant deduplication | |
| `recent-episode?` | Check if episode (or its descendants) is recent | |
| `episode-store-top-goal` | Store episode on goal termination: assess desirability, find misc indices, do housekeeping | |
| `try-analogical-plan` / `run-analogical-plan` | Use retrieved episode as template for planning | Analogical planning — not wave 1 |
| `ob$similarity` | Similarity metric for episode comparison | Not wave 1 |

**Episode data shape:**
- `rule` — the planning rule that produced this episode
- `goal` — the goal (succeeded/failed)
- `context` — the resolution context
- `realism` — strength of the outcome
- `desirability` — scenario desirability score
- `children` / `descendants` / `parent` — episode hierarchy
- `indexed-under` — list of indices
- `plan-threshold` — marks needed for plan-retrieval
- `reminding-threshold` — marks needed for reminding-retrieval

**Key constants:**
- `*recent-index-max-length*` = 6
- `*recent-ep-max-length*` = 4
- `*need-decay-factor*` = 0.98
- `*emotion-decay-factor*` = 0.95
- `*emotion-gc-threshold*` = 0.15

---

## Goal Family Files (Wave 2–3)

### `dd_kb.cl` — Knowledge base and goal family definitions

**~3300+ lines. Maps to: `goal-families`, `goals`**

This is a huge file. Only the architecturally relevant parts are inventoried here.

#### Goal family type hierarchy (lines 169–180)

```
DD-GOAL-OBJ
├── FANCIFUL-GOAL-OBJ (imaginary planning)
│   ├── RATIONALIZATION
│   ├── REVENGE
│   └── ROVING
└── REALISTIC-GOAL-OBJ (real or imaginary)
    ├── RECOVERY
    ├── REHEARSAL (+ SKIPINDEX)
    ├── REVERSAL (+ SKIPINDEX)
    └── REPERCUSSIONS (+ SKIPINDEX)
```

SKIPINDEX means the top-level goal itself is not used as an episode index
(the subgoal content is indexed instead).

#### Activation rules (Theme rules)

Each goal family has a Theme rule that fires as an inference to activate
the daydreaming goal. The activation conditions are:

| Family | Rule | Trigger | Threshold |
|---|---|---|---|
| ROVING | `Roving-Theme` (line 2064) | NEG-EMOTION from FAILED-GOAL | emotion strength > 0.7 |
| RATIONALIZATION | `Rationalization-Theme` (line 2140) | NEG-EMOTION from FAILED-GOAL | emotion strength > 0.7 |
| REVERSAL | `Reversal-Theme` (line 2731) | NEG-EMOTION from inferred TLG failure | emotion strength > 0.5, overall < 2.0 |
| RECOVERY | `Recovery-Theme` (line 3201) | NEG-EMOTION from FAILED-GOAL | emotion strength > 0.5, overall < 2.0 |
| REHEARSAL | `Rehearsal-Theme` (line 3242) | POS-EMOTION from waiting TLG with no vars | emotion strength > 0.5 |
| REPERCUSSIONS | `Repercussions-Theme1` (line 3286) | Domain-specific (earthquake) | hardcoded |
| REVENGE | (lines 2495+) | NEG-EMOTION directed at another person | domain-specific |

#### Plan rules (what each family does)

| Family | Rule | Mechanism | Sidecar priority |
|---|---|---|---|
| **ROVING** | `Roving-Plan1` (line 2076) | Coded plan: recall random pleasant episode via `epmem-reminding` | Wave 2 |
| **RATIONALIZATION** | `Rationalization-Plan1` (line 2165), `Rationalization-Inf1` (line 2177) | Find LEADTO success→failure, then find minimization to reduce emotion | Wave 2 |
| **REVERSAL** | `Reversal-Plan` (line 2779) | Coded plan: delegates to `do-reversal` → `reversal()` in dd_reversal.cl | **Wave 2 — first family** |
| **RECOVERY** | `Recovery-Theme` (line 3201) | Activates RECOVERY goal → plans to recover from failure | Wave 2 |
| **REHEARSAL** | `Rehearsal-Plan` (line 3264) | Simple: the goal IS the plan (rehearse by trying to achieve it) | Wave 2 |
| **REPERCUSSIONS** | `Repercussions-Plan1/Plan2` (lines 3297–3308) | HYPOTHESIZE state, then chain through consequences | Wave 3 |
| **REVENGE** | (lines 2510+, 2675+) | Domain-bound social scripts; needs generalization | Wave 3 |

#### Domain content (NOT needed for sidecar)

The rest of dd_kb.cl is Mueller's specific scenario content:
- Person definitions (Me, Movie-Star1, Guy1, etc.)
- Need initialization
- Phrase definitions for English generation
- Episode definitions (lovers, employment, etc.)
- Domain-specific inference rules

The sidecar should **not** port this content. It should define its own
situations using the creative brief format from 06-prep-layer.md.

### `dd_reversal.cl` — Reversal context operations

**435 lines. Maps to: `goal-families` (REVERSAL), `context`**

| Function | What it does | Notes |
|---|---|---|
| `reversal` | Entry: if failed-goal was from inferred TLG, call `reverse-undo-causes` | |
| `reverse-undo-causes` | **The main algorithm.** Find leaf causes of the failure. For each negated leaf cause: set up preservation rules, find backwards planning path, sprout alternative past context with input states. | The most complex function in the reversal module |
| `reverse-alterns` | Explore unchosen branches from original planning path | |
| `reverse-leafs` | Find leaf goals with low realism, sprout alternatives for them | Ordering inversely proportional to realism |
| `reversal-sprout-alternative` | Calls `sprout-alternative-past`, sets ordering, asserts INTENDS link | |
| `sprout-alternative-past` | **KEY MECHANISM.** Copy old context → create pseudo-sprout of new context → gc emotions → gc unrelated plans → reset goal ownership → fix activation contexts | This is the operation the Python engine cannot do |
| `gc-plans` / `gc-emotions` | Garbage collect planning structure/emotions from a context | Cleanup for alternative contexts |
| `add-emotions` | Merge emotions and their dependencies from one context to another | |

### `dd_mutation.cl` — Mutation machinery

**370 lines. Maps to: `control`, `goal-families`**

| Function | What it does | Notes |
|---|---|---|
| `action-mutations` | Top-level: iterate leaf contexts under backtrack-wall, try mutations on active action goals. Only tries once per TLG. | |
| `action-mutation` | Try a specific mutated action via serendipity mechanism | |
| `mutations` | Apply mutation pattern table to an action (unify + instantiate) | |
| `redo-plans-with-mutations?` | For leaf contexts: find mutation-plan-contexts and splice them in | |
| `inference-chain->plan-trc` | Convert inference chain from mutation context into planning trace | |

Mutation is the last-resort escape from exhausted planning. It fires only
after backtracking hits the wall. The sidecar should implement it in wave 1
as part of the control kernel (it's part of the exhaustion → escape logic
that the Python engine's `mutation` flag approximates).

---

## Planning Infrastructure Files (Wave 1, partial)

### `dd_rule1.cl` — Planning rules, part 1

**~1000+ lines. Maps to: `control`**

| Function | What it does | Notes |
|---|---|---|
| `run-rules` | Top-level: run inferences then plans for a goal in a context | The per-step planner |
| `run-inferences` | Fire inference rules against touched facts in context | |
| `run-plan` | Plan for a single goal: try episodes first (analogical), then rules | |
| `rule-fire` / `rule-fire-msg` | Fire a rule, create sprouted context, instantiate subgoals | |
| `activate-subgoal` / `instan-and-activate-subgoals` | Create and activate subgoals from rule firing | |

The sidecar needs the **control flow** from this file (run-rules → run-inferences
→ run-plans → fire → sprout) but NOT the full GATE theorem-proving
infrastructure. The planning step can be simplified to: select applicable
rule for current goal in current context, sprout child context with subgoals.

### `dd_rule2.cl` — Planning rules, part 2

**Continuation of dd_rule1.cl. Maps to: `control`**

- `run-plans` — plan execution loop, preservation goal priority, seq ordering
- Episode ordering/pruning for analogical planning

### `dd_ri.cl` — Serendipity mechanism

**~435 lines. Maps to: `episodic-memory`, `control`**

| Function | What it does | Notes |
|---|---|---|
| `run-serendipity` | Core: find rule intersections between bottom rules and existing active goals | Serendipity is what makes coincidence retrieval interesting |
| `serendipity-recognize-apply` | Recognize overlap between a new fact/episode and an existing goal, apply if found | |
| `entered-concept-serendipity` | Serendipity from environmental object input | Performance-mode mechanism |
| `run-object-serendipities` | Serendipity from perceived objects | |

Serendipity is how the system makes unexpected connections. It's retrieval
plus recognition: "this episode I just recalled has a rule that applies to
a goal I'm currently working on." The Python engine approximates this with
coincidence retrieval but doesn't have the rule-intersection mechanism.

---

## GATE Infrastructure (Not directly ported)

The GATE (General Actor Transaction Engine) object system underlies all of
Mueller's data representation. The Clojure sidecar replaces GATE entirely
with Clojure's native data structures.

| File | What it provides | Sidecar equivalent |
|---|---|---|
| `gate_main.cl` | Object system: ob$create, ob$get, ob$set, slot management | Clojure maps with keywords |
| `gate_cx.cl` | Context mechanism (already covered above) | `context` module |
| `gate_prove.cl` | Theorem proving, proof search | Not needed — simplified planning |
| `gate_unify.cl` | Unification (ob$unify, ob$unify-cx) | Clojure pattern matching (core.match or manual) |
| `gate_instan.cl` | Instantiation (ob$instantiate, ob$variabilize) | Data transformation functions |
| `gate_obs.cl` | Object observation/debugging | Clojure REPL + data inspection |
| `gate_compile.cl` | Rule compilation | Not needed |
| `gate_get.cl` | GATE accessors | Clojure keywords |
| `gate_macros.cl` | Macros | Not needed |
| `gate_ty.cl` | Type system (ty$create, ty$instance?) | Clojure multimethods or keyword dispatch |
| `gate_read_pr.cl` | Read/print | EDN |
| `gate_test.cl` | Tests | Reference for testing |
| `gate_utils.cl` | Utilities | As needed |

---

## Support Files (Not ported)

| File | What it provides | Why not ported |
|---|---|---|
| `dd.cl` | Entry point (loads files, calls `(daydreamer)`) | Project setup only |
| `dd_gen.cl` | English generation | Replaced by Director in conducted system |
| `dd_get.cl` | DD-specific accessors | Replaced by Clojure keywords |
| `dd_utils.cl` | Utilities | As needed |
| `dd_macros.cl` | Macros | Not needed |
| `dd_compile.cl` | Rule compilation | Not needed |
| `dd_night.cl` | DAYDREAMER* night dreaming mode | Separate extension, not wave 1-3 |
| `compat.cl` | Common Lisp compatibility layer | Not needed |
| `loop.cl` | Main loop wrapper | Not needed |
| `hello_world.cl` | Example/test | Reference only |

---

## Implementation Wave Mapping

### Wave 1: Kernel (context + control + retrieval)

| Sidecar module | Primary source files | Key functions to recover |
|---|---|---|
| `context` | `gate_cx.cl` | `cx$sprout`, `cx$pseudo-sprout-of`, `cx$assert`, `cx$retract`, `cx$true?`, tree navigation |
| `control` | `dd_cntrl.cl`, `dd_rule1.cl` (partial) | `daydreamer-control0`, `daydreamer-control1`, `backtrack-top-level-goal`, `set-state`, mode oscillation, `all-possibilities-failed` |
| `goals` | `dd_cntrl.cl`, `dd_kb.cl` (type defs only) | `activate-top-level-goal`, `terminate-top-level-goal`, `most-highly-motivated-goals`, goal slot schema |
| `episodic-memory` | `dd_epis.cl` | `epmem-retrieve1` (mark/threshold), `epmem-reminding` (cascade), `add-recent-index`, `add-recent`, episode storage |
| `trace` | (new) | Cycle snapshots, goal switches, context tree state, retrieval events |

### Wave 2: Revealing goal families

| Family | Primary source | Key functions | Why this order |
|---|---|---|---|
| **REVERSAL** | `dd_reversal.cl`, `dd_kb.cl` lines 2731–2790 | `reversal`, `sprout-alternative-past`, `reverse-undo-causes` | Tests context sprouting + pseudo-sprouts directly. **First family.** |
| **ROVING** | `dd_kb.cl` lines 2064–2101 | `Roving-Plan1`, `roving-plan1` | Tests recall and relief drift once the context kernel is in place. Simple coded plan: retrieve pleasant episode. |
| **RATIONALIZATION** | `dd_kb.cl` lines 2140–2200+ | `Rationalization-Theme`, `Rationalization-Inf1`, `divert-emot-to-tlg` | Tests reinterpretation logic: divert emotion strength from NEG to POS via planning |
| **RECOVERY** | `dd_kb.cl` lines 3201–3216 | `Recovery-Theme` | Tests realistic replanning after failure |
| **REHEARSAL** | `dd_kb.cl` lines 3242–3270 | `Rehearsal-Theme`, `Rehearsal-Plan` | Tests goal delegation: rehearse = try to achieve |

### Wave 3: Domain-sensitive families

| Family | Primary source | Notes |
|---|---|---|
| **REVENGE** | `dd_kb.cl` lines 2495–2690 | Heavily domain-bound (social scripts). Needs generalization. |
| **REPERCUSSIONS** | `dd_kb.cl` lines 3286–3310 | HYPOTHESIZE chaining. Domain-specific trigger (earthquake). |

---

## Cross-Reference: What the Python Engine Already Has

| Mueller mechanism | Python status | Source location |
|---|---|---|
| Goal competition by strength | Present (simplified) | `daydream_engine.py` goal scoring |
| Coincidence retrieval with thresholds | Present (simplified) | `daydream_engine.py` retrieval |
| Serendipity lowering threshold | Present | `daydream_engine.py` serendipity flag |
| Need/emotion decay | Present | `daydream_engine.py` decay loop |
| Seven goal types | Present (as labels) | `daydream_engine.py` goal types |
| Mutation as stuckness escape | Present (simplified) | `daydream_engine.py` mutation |
| Active index history | Present | `daydream_engine.py` recent indices |
| Director feedback injection | Present (scalar) | `daydream_engine.py:1517` |
| **Context sprouting/backtracking** | **Absent** | `gate_cx.cl` |
| **Pseudo-sprouts (alternative pasts)** | **Absent** | `gate_cx.cl`, `dd_reversal.cl` |
| **Performance/daydreaming oscillation** | **Absent** (mode flag only) | `dd_cntrl.cl` |
| **Multi-step planning chains** | **Absent** | `dd_rule1.cl`, `dd_rule2.cl` |
| **Emotional episodes as structured objects** | **Absent** (scalar only) | `dd_epis.cl` |
| **Goal family plan bodies** | **Absent** (posture labels only) | `dd_kb.cl`, `dd_reversal.cl` |
| **Termination semantics** | **Absent** | `dd_cntrl.cl` |
| **Episodic reminding cascade** | **Absent** | `dd_epis.cl` |

---

## Shared Benchmark

Both engines should be tested against the **Puppet Knows** fixture:
- Situations: `s1_seeing_through`, `s2_the_mission`, `s3_the_edge`, `s4_the_ring`
- World: `scope-drd/content/daydreams/puppet_knows/world.yaml`
- Graph: `scope-drd/content/daydreams/puppet_knows/dream_graph.json`
- Feedback: `scope-drd/content/daydreams/puppet_knows/director_feedback.json`
- Target transitions: cycle 6→7 (s1→s3 via ROVING) and cycle 9→10 (revenge-to-ring transfer)
- Visualization: Codex's `daydream_trace_report.py` (shared trace schema)

The sidecar should emit traces compatible with this reporter.
