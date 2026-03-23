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
- the substrate can now promote a family-plan episode from
  `:provisional -> :durable`, with a typed `:episode-promotion`
  fact asserted into the branch context, but `roving` no longer treats
  reminded episodes as promotion evidence by itself
- family-plan reuse is now starting to move onto an explicit
  use/outcome substrate:
  `note-episode-use -> resolve-episode-use-outcome ->
  reconcile-episode-admission`
- `roving` now records typed retrieval-hit facts, but it no longer
  converts reminded family-plan episodes directly into use/outcome
  evidence; retrieval stays retrieval until a later vindication path
  exists
- `rationalization` now records attributed use when a stored
  family-plan episode is actually selected as the frame source; only
  negative branch-local evidence resolves that use immediately
- `reversal` now records attributed use when a stored
  family-plan episode is actually selected as the counterfactual
  source; only negative branch-local evidence resolves that use
  immediately
- repeated later cross-family success can now vindicate those pending
  same-family source uses without turning that vindication itself into
  new promotion evidence
- repeated cross-family success can now promote through attributed use
  evidence (`:cross-family-use-success`), but raw roving reminding no
  longer counts as that evidence by itself
- promotion does not bypass the recent-episode anti-echo gate;
  a promoted episode becomes reusable only after recent eviction
- same-family reuse is now tracked, and repeated same-family reuse
  can flag `:same-family-loop` to suppress reentry; one old
  cross-family success no longer grants lifetime immunity from that
  flag
- repeated failed same-family attributed use can now auto-flag
  `:stale` and demote a `:durable` episode back to `:provisional`
  without waiting for evaluator annotation; while `:stale` remains
  present, one later cross-family success does not re-promote it
  automatically
- repeated later cross-family success can now rehabilitate a `:stale`
  episode by clearing the stale flag and letting durable status be
  re-earned through evidence rather than evaluator judgment
- repeated later cross-family success can now also clear an active
  `:same-family-loop` flag, but only when that evidence is genuinely
  later than the loop-triggering use; once cleared, same-family reentry
  pressure now accumulates from that rehabilitation point rather than
  from lifetime totals
- world state now carries an inert `:rule-access` registry with
  `:accessible` / `:frontier` / `:quarantined` statuses derived
  from rule provenance deployment roles
- current authored family rules initialize as `:accessible`, so
  ordinary planning behavior is unchanged while the frontier
  scaffolding becomes real
- planning and serendipity now read different filtered graph views:
  ordinary roving seed ranking uses planning view, while reminding-side
  provenance scoring uses serendipity view
- `:goal-family/reversal-aftershock-to-rationalization` now starts as
  the first real `:authored-frontier` bridge rule instead of just
  metadata-marked scaffolding
- cross-family trigger emission now honors that access split in actual
  runtime behavior: the bridge can fire while frontier-visible to
  serendipity, and quarantining it now suppresses that handoff
- activation and planning dispatch now also honor the planner-visible
  access split in actual runtime behavior instead of iterating raw
  `activation-rules` / `planning-rules`; frontier/quarantined rules
  are no longer merely hidden in graph views
- durable promotion can now open frontier rules to `:accessible`,
  and hard-failure demotion can quarantine non-core rules touched by
  an episode's rule path
- later durable evidence can now reopen explicitly quarantined
  non-core rules to `:frontier`; quarantine is no longer purely
  one-way, and reopening now requires later durable evidence from a
  different episode than the one that quarantined the rule
- external family evaluators can now return an explicit
  promotion decision; heuristic mode stays conservative, and that
  evaluator output is now advisory/gating input rather than direct
  store-time admission authority
- family-plan episodes no longer enter as `:durable` from evaluator
  opinion alone; durable admission and frontier opening now require
  later use/outcome reconciliation
- durable eligibility for stored family-plan episodes is now keyed to
  a kernel-owned promotion basis (`:reframe-facts`,
  `:input-facts`, or `:none`) rather than evaluator-authored
  retention labels, so the evaluator can still request hot/cold
  storage policy without authoring structural promotion class
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
  evaluator is a gate or veto, not the sole authority. Review `13`
  sharpens the same guardrail from the higher level: growth should sit
  on top of typed cross-phase artifacts like use/outcome evidence and
  frontier admission, not bypass them. Review `14` sharpened the next
  hardening split inside that substrate: `retrieved` vs `used` vs
  `vindicated`, and `advisory` vs `admissible`. The substrate now has
  the first real later-vindication path for pending same-family source
  uses, and quarantine reopening is now real for explicitly
  quarantined non-core rules; broader vindication policy beyond the
  current source/stale/loop paths is still missing.
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
- `:effect-schema` is now closed at the top level for current
  `:clojure-fn` rules: undeclared effect keys fail at the executor
  boundary instead of drifting silently past the rule contract
- `rules.clj` now owns the generic effect-program reduction/threading
  scaffold through `apply-effects`; `goal_families.clj` still owns the
  concrete family op handlers
- `rules.clj` now also owns the first builtin effect handlers:
  `:context/sprout`, `:fact/assert`, `:facts/assert-many`,
  `:context/set-ordering`, and `:goal/set-next-context`
- builtin `:goal/set-next-context` now fails closed on unknown goals,
  and the family whole-program validator now rejects duplicate
  symbolic producers across `:ref` / `:result-key`
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

### Benchmark re-entry

`Puppet Knows` benchmark re-entry is now partially executed against the
hardened membrane:

- 12-cycle regression return: **clean but only partly informative**
- 50-cycle soak: **partial, improved after benchmark pressure retune**

Current read:

- the benchmark now runs cleanly under the Step 1 / Step 2 contract
- stored family-plan episodes remain `:provisional` at store time in
  the live family-plan path
- the benchmark does **not** yet exercise much of the new
  use/outcome/promotion substrate: no family-plan episode accumulated
  use history, promotion evidence, or anti-residue flags in the 12- or
  50-cycle returns
- a new `retrieved_episodes` channel now shows that the live 50-cycle
  run is seeing the world episode store on most cycles, including some
  provisional family-plan episodes, but those hits are still not being
  converted into source-use / promotion / flag movement
- rule-access gating is now live in activation, planning, and the
  first frontier bridge, but the current benchmark run does not yet
  produce meaningful quarantine/reopening movement
- the first benchmark-pressure retune broke the earlier rehearsal lock:
  a fresh 50-cycle rerun now stays mixed after cycle 13
  (`reversal 9`, `roving 6`, `rehearsal 13`, `repercussions 9`), and
  the late tail no longer locks into same-situation repercussions
- even after that retune, the soak is still more useful as a runtime /
  pressure read than as a deep memory-membrane read, because the run is
  not yet generating visible use-history / promotion / flag dynamics in
  the live benchmark world

Treat the benchmark result as:

- a **runtime/seam regression pass**
- a **partial but improving membrane integration read**
- not yet a decisive architecture verdict on long-run memory ecology

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
