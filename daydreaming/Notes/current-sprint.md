# Current Sprint

Last updated: 2026-03-22

---

## Previous Sprint: RuntimeThoughtBeatV1 (CLOSED)

**Objective:** Is cognition legible on stage, and does residue
writeback change the next trace?

**Result: both gates passed.**

### Gate 1 (stage legibility): passed

RuntimeThoughtBeatV1 produces inner-life prose that reads as a
character thinking. Haiku at ~3.3s/cycle, Sonnet at ~7.6s/cycle.
Hybrid routing (Haiku base, Sonnet escalation on reversal)
produces the best cost/quality tradeoff at ~57.8s for 12 cycles.

Known qualification: the LLM generates richer inner life than
the kernel's events strictly justify. The narration partly
outruns actual cognitive computation.

### Gate 2 (structural divergence with feedback): passed

All three feedback modes (Haiku, Sonnet, hybrid) diverge from
the no-feedback baseline starting at cycle 4. By cycles 8-12,
different models produce different node selections. The writeback
changes retrieval, which changes selection. The feedback loop is
structurally real, not cosmetic.

Key evidence:
- cycle 8: Haiku → n15_subway_self_scrutiny, Sonnet → n02_corridor_repeat
- cycle 10: Haiku → n05_peel_the_wall, Sonnet → n06_overhead_set_edge
- cycle 12: Haiku → n01_notice_seams, Sonnet → n06_overhead_set_edge

See `runtime-thought-feedback-comparison.md` for full results.

### Implementation

The feedback loop runs in Clojure inside the autonomous benchmark:
cycle completes → runtime thought packet built from snapshot →
LLM call (routed Haiku/Sonnet via `runtime_thought.clj`) →
residue normalized → new episode stored with indices →
recent-indices updated → Director feedback → next cycle.

Distilled residue writeback, not full prose. Matches the sprint
contract.

### Artifacts

- `out/puppet_knows_autonomous_feedback_haiku12_cognitive.html`
- `out/puppet_knows_autonomous_feedback_sonnet12_cognitive.html`
- `out/puppet_knows_autonomous_feedback_hybrid12_cognitive.html`
- `kernel/src/daydreamer/runtime_thought.clj`
- `daydreaming/Notes/runtime-thought-feedback-comparison.md`

### Secondary targets (closed)

**Provocation seam:** state/event provocations proven load-bearing
(ferry and Dara flip operators). Pure salience provocations not
load-bearing (architectural finding — pipeline is situation-first).
See provocation matrix results in `daydreaming/out/`.

**Lyria:** listen_trace.py exists. Mappings need interactive tuning
but connection works.

---

## Current Objective

**Option A is active:** rule connection graph + memory ecology
discipline.

The graph itself is no longer the only frontier. The immediate
risk is that family-plan output enters reusable memory too early
and too uniformly, creating grooves before the executor boundary
and path verifier are real.

### What is already true

- graphable trigger / activation / planning pairs exist
- cross-family bridges exist and execute at runtime
- provenance affects retrieval and roving seed selection
- autonomous conductor extraction is done
- external family-plan evaluator seam exists
- roving now runs through a typed effect program with
  kernel-applied effects instead of inline world mutation
- `roving-plan-dispatch` is now the first real `:clojure-fn`
  rule vertical slice: `execute-rule` dispatches it through a
  runtime executor binding, while effect application still stays
  kernel-owned and local
- `rationalization-plan-dispatch` now runs through the same
  `execute-rule` seam, so Step 2 has a second real vertical slice
  without moving effect application out of the kernel yet
- `reversal-plan-dispatch` now runs through the same
  `execute-rule` seam as a composite branch program, so all three
  family dispatch rules cross the executor boundary while branch/effect
  application still stays kernel-local

### Current subphase: memory ecology hardening

This pass is implementing the first membrane between "generated"
and "deserves future influence":

- family-plan episodes now carry `:trace` / `:provisional`
  admission status (`:archive-cold` maps to `:trace`)
- cue roles are separated in the episode substrate:
  content / reminding / provenance / support
- support and rule-path indices no longer enter the normal
  retrieval index
- reminding only reactivates reminding cues
- provenance bonus now requires content marks before it applies
  (stricter for imaginary / counterfactual material)
- same-family fallback for rationalization and reversal is gated
  on durable promotion
- rationalization stored-frame fallback no longer uses cheap
  `:serendipity? true` reopening
- cross-family reuse can now promote a family-plan episode from
  `:provisional -> :durable`, with a typed `:episode-promotion`
  fact asserted into the branch context
- family-plan reuse is now starting to move onto an explicit
  use/outcome substrate:
  `note-episode-use -> resolve-episode-use-outcome ->
  reconcile-episode-admission`
- `roving` now records typed `:episode-use` and `:episode-outcome`
  facts for the seed/reminded family-plan episodes it actually uses
- `rationalization` now records attributed use/outcome when a stored
  family-plan episode is actually selected as the frame source
- `reversal` now records attributed use/outcome when a stored
  family-plan episode is actually selected as the counterfactual source
- cross-family success now promotes through attributed use evidence
  (`:cross-family-use-success`) rather than raw retrieval alone
- promotion does not bypass the recent-episode anti-echo gate;
  a promoted episode becomes reusable only after recent eviction
- same-family reuse is now tracked, and repeated same-family reuse
  can flag `:same-family-loop` to suppress reentry
- world state now carries an inert `:rule-access` registry with
  `:accessible` / `:frontier` / `:quarantined` statuses derived
  from rule provenance deployment roles
- current authored family rules initialize as `:accessible`, so
  ordinary planning behavior is unchanged while the frontier
  scaffolding becomes real
- planning and serendipity now read different filtered graph views:
  ordinary roving seed ranking uses planning view, while reminding-side
  provenance scoring uses serendipity view
- durable promotion can now open frontier rules to `:accessible`,
  and hard-failure demotion can quarantine non-core rules touched by
  an episode's rule path
- external family evaluators can now return an explicit
  promotion decision; heuristic mode stays conservative, but
  evaluator-backed rationalization/reversal plans can enter as
  `:durable` without the old manual test hack
- evaluator-backed anti-residue flags now persist on episodes,
  assert typed `:episode-flag` facts into branch contexts, and
  can already suppress later retrieval (`:contradicted`,
  `:backfired`) or down-rank it (`:stale`)

The reviews now make the next two abstractions explicit:
- **Step 1:** promotion / anti-residue / accessibility should be
  driven by **episode use with attributed outcomes**, not by retrieval
  alone. The first real slices are now in code for roving cross-family
  reuse and the stored rationalization/reversal reopen paths, but
  broader coverage, richer outcome resolution beyond simple
  success/failure/backfire/contradiction, and active frontier-opening
  behavior beyond the current inert scaffolding are still missing. The
  evaluator is a gate or veto, not the sole authority.
- **Step 2:** the executor seam belongs in `rules.clj` as
  `execute-rule`, not as growing local effect machinery inside
  `goal_families.clj`.

The first honest Step 2 slices are now in code:
- `execute-rule` can resolve a `:clojure-fn` executor from a
  call-supplied executor registry
- `execute-rule` now also rejects malformed `:effects` at a narrow
  shape level: effect programs must be vectors of maps with keyword
  `:op`, and rule-declared `:effect-ops` are enforced
- `execute-rule` now also runs a call-supplied effect validator, so
  the current family runtime can reject malformed op payloads before
  any local kernel effect application happens
- `execute-rule` now also validates a rule-declared `:effect-schema`,
  so the family dispatch rules no longer declare only allowed op sets;
  they declare the expected ordered effect shape as part of the rule
  contract
- `rules.clj` now owns the generic effect-program reduction/threading
  scaffold through `apply-effects`; `goal_families.clj` still owns the
  concrete family op handlers
- `rules.clj` now also owns the first builtin effect handlers:
  `:context/sprout`, `:fact/assert`, `:facts/assert-many`,
  `:context/set-ordering`, and `:goal/set-next-context`
- `goal_families.clj` no longer applies family effects through one
  monolithic `case`; the local runtime is now an explicit op-handler
  registry dispatched by `rules/apply-effects`, and it now only keeps
  family-semantic handlers
- `goal_family_rules.clj` now marks
  `:goal-family/roving-plan-dispatch` and
  `:goal-family/rationalization-plan-dispatch` and
  `:goal-family/reversal-plan-dispatch` as `:clojure-fn`
- `goal_families.clj` now routes roving, rationalization, and reversal
  dispatch through that rule executor, while leaving
  `apply-family-effects` local
- the first op-specific payload checks are now live for the current
  family effect vocabulary (`:context/sprout`, `:fact/assert`,
  `:facts/assert-many`, `:episode/reminding`,
  `:episodes/*`, rationalization diversion/afterglow/log, reversal
  branch execution)
- reversal currently uses a named composite branch op rather than a
  generic shared effect runtime

### Next after this

- extend the new use/outcome substrate beyond roving's current
  cross-family slice so promotion, anti-residue, and rule
  accessibility are driven by attributed use records broadly
- start using the new `:rule-access` registry for non-core/future
  frontier rules, not just authored-core defaults
- widen outcome resolution beyond the current simple
  `:succeeded` / `:failed` split and let those outcomes
  drive stronger promotion/demotion decisions
- continue the declarative effect vocabulary / executor boundary
  by keeping all three family dispatch rules on `execute-rule` while
  using `:effect-schema` for declarative effect shape, keeping deeper
  payload validation honest, and continuing to pull the generic effect
  runtime into `rules.clj`
- widen anti-residue from evaluator annotations to stronger
  downstream demotion / contradiction detection
- strengthen consolidation policy beyond the current first-pass
  promotion + flag membrane
- only then build verified paths and generic `:clojure-fn`
  dispatch

### Deferred parallel fronts

- Option B (Graffito creative brief) remains valid but not active
- Option C (live instrument prototype) remains valid but not active

---

## Previous Sprints

**RuntimeThoughtBeatV1: passed (closed 2026-03-21)**
- Both gates met. Feedback loop diverges. Thought beats read
  as inner life. See above.

**supply_v1: practical pass (closed)**
- Tessa: clean pass (7 keepers / 4 reserves)
- Kai: narrow pass (7 keepers / 3 reserves)
- Artifacts: keeper bank, pack registry, closeout note
- See `supply-v1-closeout.md`

---

## Standing constraints

- Full runtime memory writeback remains cautious (self-echo risk).
  Current writeback is distilled residue only.
- Multi-situation generation (Q5/Q7/Q12) is later phase.
- Scope/video rendering deferred (GPUs scarce).
- Milestone-based discipline continues. Cheap levers before
  structural mechanisms. Doc 16 applies.
