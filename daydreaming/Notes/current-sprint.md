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
- promotion does not bypass the recent-episode anti-echo gate;
  a promoted episode becomes reusable only after recent eviction
- same-family reuse is now tracked, and repeated same-family reuse
  can flag `:same-family-loop` to suppress reentry
- external family evaluators can now return an explicit
  promotion decision; heuristic mode stays conservative, but
  evaluator-backed rationalization/reversal plans can enter as
  `:durable` without the old manual test hack

### Next after this

- define the declarative effect vocabulary / executor boundary
- add the remaining anti-residue flags (`:backfired`, `:stale`,
  `:contradicted`) and stronger consolidation policy
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
