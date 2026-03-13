# Session 08: Wave 2 — REVERSAL, ROVING, Activation Rules

**Date:** 2026-03-12
**Focus:** Wave 2 goal families, Mueller faithfulness audit, self-triggering activation rules, side-by-side trace comparison
**Agents:** Claude Opus (orchestration, code review, Mueller source audit, commits), Codex (implementation)

---

## What Happened

### 1. REVERSAL Primitive (Codex, reviewed by Claude)

Codex implemented the core REVERSAL machinery in `goal_families.clj`:

- **`sprout-alternative-past`** — the key function from Mueller's `dd_reversal.cl`. Copies an old planning context as a fresh root, pseudo-sprouts it under a new planning branch, GC's emotions and unrelated plans, rebinds surviving plan structure to the new top-level goal.
- **`gc-emotions`** — strips emotion facts and their dependency links from the alternative-past branch.
- **`gc-plans`** — removes planning structure owned by unrelated top-level goals.
- **`rebind-planning-facts`** — rewrites goal/intends facts to point at the new top-level goal, resets terminal statuses to `:runable`.
- **`reversal-sprout-alternative`** — REVERSAL-family wrapper that adds branch ordering and injects counterfactual input facts.
- **`context.clj` patch** — `copy-context` now handles `nil` parent, creating parentless root clones needed by REVERSAL.

### 2. Incremental Narrowing of Scripted Surface Area

Each Codex pass removed one layer of fixture dependency from the Puppet Knows benchmark:

1. **`118c262`** — REVERSAL primitive exists, benchmark exercises real branching, but fixture picks everything
2. **`c0f37eb`** — `reverse-leafs` discovery: kernel finds the failed context autonomously
3. **`d25f2e2`** — `reverse-undo-causes`: kernel derives counterfactual input facts from stored `:failure-cause` facts
4. **`3041d67`** — `reverse-leafs` corrected to walk INTENDS chains per Mueller (see below)

### 3. Mueller Source Audit

David flagged that we should be checking against Mueller's actual source, not working from memory. Claude read `dd_reversal.cl` and found three real divergences:

**Divergence 1 — `reverse-leafs` scope:** Mueller walks INTENDS chains from a specific failed goal (`get-leafs`), not the whole context tree. Codex's original version scanned all contexts. Fixed in `3041d67`.

**Divergence 2 — Multi-branch and objective retraction:** Mueller sprouts one branch per qualifying leaf (strength < 0.5), uses inverse-strength ordering (1.0 / strength), and retracts the leaf objective to force replanning. Codex's original picked one winner and didn't retract. Fixed in `3041d67`.

**Divergence 3 — `reverse-undo-causes` is rule synthesis:** Mueller synthesizes PRESERVATION rules at runtime — inference-only + plan-only rule pairs generated on the fly. Our implementation uses stored `:failure-cause` facts. This is an honest bridge that requires a rule engine to fix. Documented in commit messages and guardrails.

### 4. Guardrail Audit

Claude audited wave 2 code against `agent_guardrails.md` and `AGENTS.md`. Issues found:

1. ~~Duplicate `defn- selected-goal-ids`~~ — false positive (Claude's error)
2. **`:retract-facts` destructuring shadowed private fn** — fixed, now uses `retraction-facts` local binding
3. **`reachable-goal-ids` used `(subvec (vec frontier) 1)` in loop** — fixed, now uses persistent queue with `peek`/`pop`
4. **Namespace-qualified keys (`:fact/type`, `:fact/id`)** — documented in updated guardrails
5. **New data shapes undocumented** — `:failure-cause`, `:counterfactual` facts and `:alternative-past?`, `:generation-switches` context keys now in guardrails
6. **`set` alias vs `clojure.core/set`** — noted, not worth changing

### 5. ROVING Implementation

Claude read Mueller's ROVING source in `dd_kb.cl:2064-2101` before Codex implemented. Key finding: ROVING is surprisingly simple — it's a mood regulation mechanism, not a planning mechanism.

Mueller's ROVING:
- Theme rule: fires when negative emotion > 0.04 from a failed goal
- Plan body: sprout one context, pick a random pleasant episode from `*roving-episodes*`, call `epmem-reminding`, assert trivially-succeeded INTENDS

Codex implemented:
- `roving-activation-candidates` / `select-roving-trigger` — finds failed-goal / negative-emotion pairs linked by dependency facts
- `roving-plan` — sprouts side-channel context, seeds pleasant episode, runs existing reminding cascade from `episodic_memory.clj`
- Runner wired with `:roving-branch` script hook

Only divergence: deterministic episode selection (first from list) vs Mueller's `random-element`. Intentional for benchmark reproducibility.

### 6. Strategic Pivot: Depth Over Width

After REVERSAL and ROVING, David asked what to prioritize next. Claude recommended stopping new families and proving what we have:

> "We keep building mechanisms but haven't tested whether they produce different behavior than the Python engine."

The agreed plan:
1. Goal family activation rules (Theme rules) — make REVERSAL and ROVING self-triggering
2. Semi-unscripted Puppet Knows run — kernel decides when to fire families
3. Side-by-side trace comparison — scripted vs semi-unscripted in same reporter

Codex agreed and added the architectural refinement: activation rules belong in `goal_families.clj` as a clean pass called from `control.clj`, not buried as ad hoc conditionals.

### 7. Activation Rules and Semi-Unscripted Benchmark

Codex implemented `activate-family-goals` in `goal_families.clj`:
- Checks both REVERSAL and ROVING activation candidates
- Creates goals only if no duplicate exists for the same trigger
- Records activation events in trace
- Opt-in via `:auto-activate-family-goals?` on the world map
- Called from `control.clj` after decay, before goal competition

Semi-unscripted Puppet Knows (`run-semi-unscripted-benchmark`):
- Drops the fixture-scripted `:reversal` goal from goal specs
- Enables `:auto-activate-family-goals? true`
- Seeds roving episodes with pleasant indices
- Kernel self-activates REVERSAL and ROVING on cycle 8

### 8. Side-by-Side Trace Export

Codex generalized `export_trace.clj` to support `--benchmark scripted|semi-unscripted` and added `bb` tasks:
- `bb puppet-knows-trace` — scripted benchmark
- `bb puppet-knows-semi-trace` — semi-unscripted benchmark
- `bb puppet-knows-compare` — renders both traces in one comparison HTML

Key behavioral result:
- **Scripted:** repercussions → revenge → rehearsal (fixture decides everything)
- **Semi-unscripted:** repercussions → (kernel auto-activates reversal + roving) → revenge → reversal (kernel decides which families fire)

### 9. Git Commits (8 this session)

1. `118c262` — Wave 2 REVERSAL primitive and benchmark wiring
2. `c0f37eb` — reverse-leafs discovery
3. `d25f2e2` — reverse-undo-causes bridge
4. `3041d67` — Correct reverse-leafs to INTENDS chains per Mueller
5. `a97181b` — Guardrail fixes (shadowing, queue, docs)
6. `350d133` — ROVING goal family
7. `7e8e8a5` — Activation rules and semi-unscripted benchmark
8. `72bbf3d` — Side-by-side trace export and comparison artifacts

---

## Key Design Decisions Made

1. **Activation rules in `goal_families.clj`, not `control.clj`** — clean separation: families infer what should activate, control arbitrates via goal competition.

2. **Opt-in activation via `:auto-activate-family-goals?`** — scripted benchmark stays a stable baseline while semi-unscripted enables self-triggering.

3. **`reverse-undo-causes` is an honest bridge** — stored `:failure-cause` facts, not Mueller's runtime PRESERVATION rule synthesis. Documented as a gap requiring a rule engine.

4. **Depth over width** — stopped adding families after REVERSAL + ROVING to prove the architecture is generative before expanding.

5. **Deterministic ROVING** — first pleasant episode from list, not random. Reproducibility matters more than fidelity for benchmarks.

---

## What's Proven

- The core REVERSAL pipeline (sprout-alternative-past → gc-emotions → gc-plans → rebind) is Mueller-faithful
- `reverse-leafs` correctly walks INTENDS chains, filters by strength threshold, and retracts leaf objectives
- ROVING activates from failed-goal + negative-emotion pairs and runs the reminding cascade
- Family activation rules self-trigger REVERSAL and ROVING from world state
- Semi-unscripted benchmark produces a different trajectory than scripted (repercussions→revenge→reversal vs repercussions→revenge→rehearsal)
- 60 tests, 301 assertions, 0 failures
- Both traces export to JSON and render to HTML through the Python reporter
- Side-by-side comparison artifact exists at `out/puppet_knows_compare.html`

## What's NOT Proven Yet

- **Whether the semi-unscripted trajectory is "better"** — the kernel reaches REVERSAL at cycle 10 instead of rehearsal. Whether that's a meaningful behavioral difference or just a different sequence requires David's judgment.
- **`reverse-undo-causes` as runtime rule synthesis** — needs a rule engine the kernel doesn't have.
- **`reverse-alterns`** — walking activation→termination planning path for branch alternatives. Not implemented.
- **Fully unscripted run** — scripts still provide situation data, timestamps, termination. Only family activation is autonomous.
- **RATIONALIZATION, RECOVERY, REHEARSAL** — not started.
- **Fixture loader** — world.yaml / dream_graph.json parsing.

---

## Mueller Faithfulness Scorecard

### REVERSAL (`dd_reversal.cl`)

| Mechanism | Mueller | Kernel | Status |
|---|---|---|---|
| `sprout-alternative-past` | copy → pseudo-sprout → gc-emotions → gc-plans → rebind | Same pipeline | Faithful |
| `reverse-leafs` | Walk INTENDS chain, strength < 0.5, multi-branch, retract objective | Same logic | Faithful |
| `reversal-sprout-alternative` | Set ordering, assert goal, optionally assert INTENDS | Same + input facts | Faithful |
| `reverse-undo-causes` | Runtime PRESERVATION rule synthesis | Stored :failure-cause facts | Bridge |
| `reverse-alterns` | Walk activation→termination path, sprout at each branch point | Not implemented | Missing |

### ROVING (`dd_kb.cl:2064-2101`)

| Mechanism | Mueller | Kernel | Status |
|---|---|---|---|
| Theme rule | Neg emotion > 0.04 from failed goal | Same threshold + dependency check | Faithful |
| Plan body | Sprout, random episode, epmem-reminding, success INTENDS | Same (deterministic pick) | Faithful |

---

## Observations

### On the Mueller source audit

David's instinct to check against the actual source was exactly right. The `reverse-leafs` scope bug (scanning all contexts vs walking INTENDS chains) would have been a silent behavioral divergence that worked for the single-failure-context benchmark but failed on anything more complex. Reading `dd_reversal.cl` line by line caught it immediately.

### On the strategic pivot

The shift from "implement more families" to "prove what we have works" was the right call. We had been building width (more mechanisms) without ever testing depth (does the architecture produce emergent behavior). The activation rules and semi-unscripted benchmark answer the first-order question: yes, the kernel makes different decisions when allowed to self-trigger.

### On the comparison result

The semi-unscripted run reaching REVERSAL at cycle 10 instead of rehearsal is genuinely interesting. It means the emotional state in the seeded world (failed goal + negative emotion above threshold) is sufficient to trigger REVERSAL autonomously, and that REVERSAL's strength competes successfully against rehearsal in goal competition. This is a behavior the Python engine's flat scheduler cannot produce — it has no mechanism for "I failed, therefore I should imagine an alternative past."

### On what's left for the rule engine

`reverse-undo-causes` and `reverse-alterns` are the two remaining REVERSAL gaps. Both require infrastructure the kernel doesn't have: a rule engine that can synthesize planning rules at runtime. This is a significant architectural addition — not wave 2 scope. The stored-cause bridge is honest and sufficient for the research question as currently framed.

---

## Next Session Plan

1. **Analyze the comparison trace** — look at `out/puppet_knows_compare.html` and determine if the behavioral divergence is meaningful or trivial
2. **Reduce remaining scripted surface** — situations, timestamps, and termination are still fixture-provided. Can any of these be derived?
3. **Consider a second fixture** — Puppet Knows is one scenario. A second scenario would test whether the architecture generalizes.
4. **Session log and memory update** — keep the project memory current with wave 2 completion status
