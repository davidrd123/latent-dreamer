The prompt is decent$_{75%}$, but it is miscalibrated in one big way: it briefs 5 Pro as if this is a mostly greenfield rule-engine design problem. It isn’t. It’s a migration/design problem inside a real kernel that already has strong persistent substrates, hand-coded family bridges, and benchmark-edge adapters.

That matters because the best advice changes a lot once you say the thing honestly.

## What the prompt gets right

It is asking the right class of questions:

* pattern language
* structural connection graph
* executor contract
* integration with contexts/goals/episodic memory
* serialization/versioning
* build order and testing
* “what will we regret later?”

That part is solid.

It is also anchored to the right companion doc. `kernel-rule-schema-and-execution-model.md` is the closest thing you have to an implementation brief. The prompt’s emphasis on `RuleV1`, `EdgeBasisV1`, structural graph derivation, and executor kinds lines up with that doc.

And the basic framing from the condensation set is right: keep the graph structural, keep recursion in Clojure, and use LLMs only at bounded judgment sites, not as the owner of the control loop.

## Where it is off

### 1. It is not honest enough about what the kernel is today

The prompt says “the architecture is settled” and then lists what exists. That is only true at the macro-architecture level.

At the kernel level, what exists is:

* a real immutable context tree (`context.clj`)
* real goal objects and control selection/backtracking (`goals.clj`, `control.clj`)
* real episodic retrieval plus reminding cascade (`episodic_memory.clj`)
* real trace/orchestration harness (`trace.clj`, `runner.clj`)
* real family-specific bridge logic in `goal_families.clj`
* real benchmark adapters at the edge (`puppet_knows_adapter.clj`, `puppet_knows_autonomous.clj`)

What does **not** exist is the main thing the rule substrate is meant to add:

* no generic planning-rule engine
* no generic inference fixpoint loop
* no generic analogical planner
* no general rule-connection graph
* no general serendipity path search / verification loop

That is not a small hole. That is the hole.

So the prompt should say that explicitly, otherwise 5 Pro may optimize for polish on abstractions instead of confronting the actual migration problem.

### 2. It undersells `goal_families.clj`

Right now the prompt describes `goal_families.clj` as “daydreaming goal family activation (partial — reversal, rationalization, roving).”

That description is too weak. The file is not just partial activation logic. It is the biggest concrete substitute for the missing generic rule layer. It already contains:

* activation-candidate logic
* stored failure-cause retrieval
* rationalization-frame retrieval
* reversal branch construction
* roving plan body
* rationalization plan body
* emotional diversion logic
* context sprouting / alternative-past operations

If 5 Pro doesn’t see that file, it will miss the main question: **what gets extracted into a generic rule substrate, and what stays procedural/family-specific for a while?**

### 3. It doesn’t force the internal-graph vs authored-graph distinction

This is the other big miss.

You have two different “graphs” in the surrounding docs:

1. the **kernel-internal rule connection graph** for planning / serendipity / mutation
2. the **shared authored graph/interface seam** from `21-graph-interface-contract.md`

Those are not the same object. If the prompt does not say this explicitly, you’re inviting bad advice. The March 13 architecture docs are already trying to keep L2 internal runtime ontology separate from the shared graph seam. The implementation prompt should inherit that discipline.

### 4. It asks about async `:llm-backed` execution too early

That question is fair in the abstract, but in *this repo* it is premature.

Right now the only real impure model edge is `director.clj`, and it is intentionally kept outside the pure kernel. That is a clue. First you need the structural rule substrate. Then you can argue about whether `:llm-backed` executors live inside the pure loop, at an orchestration boundary, or as a staged external judgment pass.

If you ask async/scheduling too early, 5 Pro may spend tokens designing concurrency around a thing you don’t even structurally have yet.

### 5. It omits the actual migration/testing surface

The prompt mentions benchmarks, but it should foreground `runner.clj`, `trace.clj`, and the Puppet Knows edge files.

That’s where the new substrate will have to prove itself.

The current system already has a trace vocabulary and benchmark style. Advice that ignores that and talks like this is a fresh engine build is lower value.

## What I would change in the prompt

### Replace the opening stance

Current vibe: “the architecture is settled; think deeply about how to build it.”

Better:

```md
The macro-architecture is settled enough to design the rule substrate, but the
current kernel is not yet a recovered generic DAYDREAMER rule engine. It is a
running Clojure kernel with strong persistent substrates (contexts, goals,
control, episodic retrieval, tracing), family-specific bridge code, and
benchmark-edge adapters. Treat this as a migration/design problem, not a
greenfield architecture exercise.

In particular, keep separate:
1. the kernel-internal rule connection graph for L2 cognition, and
2. the shared authored graph/interface seam used across lanes.
Do not conflate them.
```

### Rewrite “What exists in the kernel today”

Split it into **substantial substrates** and **still missing**.

Something like:

```md
## What exists in the kernel today

Substantial substrates:
- `context.clj` — immutable context tree with sprouting, pseudo-sprouting,
  copy, assert/retract, touched-fact bookkeeping
- `goals.clj` / `control.clj` — goal objects, real vs imaginary planning type,
  control selection, decay, backtracking, termination
- `episodic_memory.clj` — episode store, coincidence-mark retrieval,
  recent-index / recent-episode FIFOs, recursive reminding cascade
- `trace.clj` / `runner.clj` — orchestration and trace surface already used by
  benchmarks
- `goal_families.clj` — current family-specific substitute for missing generic
  rule machinery: activation candidates, stored failure-cause lookup,
  rationalization frames, reversal/roving/rationalization branch procedures
- `director.clj` — existing impure LLM edge, intentionally outside the pure
  kernel
- benchmark edge files (`puppet_knows_*`) — current adapter-mediated integration
  showing how branch facts, pressures, retrieval, and directed feedback interact

Still missing:
- generic rule base
- generic planning-rule application loop
- generic inference-rule fixpoint loop
- generic analogical planner
- general rule-connection graph construction / traversal
- general serendipity path search and verification
```

That version is much more truthful.

## Attachment set: what’s missing

The current attachment list is too prose-heavy and too polite.

### Must add

* `kernel/src/daydreamer/goal_families.clj`
* `kernel/src/daydreamer/context.clj`
* `kernel/src/daydreamer/control.clj`
* `kernel/src/daydreamer/episodic_memory.clj`
* `kernel/src/daydreamer/runner.clj`
* `kernel/src/daydreamer/trace.clj`
* `kernel/src/daydreamer/benchmarks/puppet_knows_autonomous.clj`
* `kernel/src/daydreamer/benchmarks/puppet_knows_adapter.clj`
* `daydreaming/Notes/DeepResearch/2026-03-13/review-request-5pro-arch/reply.md`
* `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/11-settled-architecture.md`
* `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/21-graph-interface-contract.md`
* `mechanisms/12-reminding-cascade.md`

### Why

Because right now the prompt gives 5 Pro a lot of design intent, but not enough evidence of the live codebase shape.

`goal_families.clj` is the main omission.
`21-graph-interface-contract.md` is the main architectural-discipline omission.
`review-request-5pro-arch/reply.md` is the main honesty omission.

### If token budget is tight

Drop one of the chain traces before dropping code.

## The question list: what’s missing

The existing questions are good, but they miss the repo-specific sharp edges.

Add these.

### 1. Migration question

> Given `goal_families.clj`, which abstractions should be extracted into the first generic rule substrate, and which behaviors should remain family-specific procedural code for a long time?

### 2. Graph-seam question

> How do we prevent the kernel-internal rule connection graph from being conflated with the shared authored graph/interface seam from `21-graph-interface-contract.md`?

### 3. Fact-shape question

> The current kernel stores context facts as plain Clojure data in `:all-obs`, `:add-obs`, `:remove-obs`, with `:touched-facts` already present. What fact representation should the rule engine target so it composes directly with `context.clj` rather than introducing a second state substrate?

### 4. Trace/provenance question

> How should rule applicability, firings, bindings, and path provenance surface in `trace.clj` so the existing benchmark style can test them?

### 5. Adapter-boundary question

> Some semantics currently live in benchmark adapters (`puppet_knows_adapter.clj`, `puppet_knows_autonomous.clj`). Which of those belong in the future generic rule substrate, and which should remain conducted-edge adapters?

### 6. LLM-boundary question

> Should the first `:llm-backed` executor live inside the pure kernel at all, or should it initially remain at an impure orchestration boundary like `director.clj` until the structural substrate is stable?

### 7. Cycle-placement question

> Where in the existing control cycle should generic rule application live relative to `run-cycle`, `run-goal-step`, and `maybe-activate-family-goals`?

That last one is especially important. The current prompt oddly doesn’t ask where the new machinery enters the already-existing control loop.

## My compressed answer to the prompt

If I were answering the prompt itself, I’d say this.

### Pattern language

Do **not** start with `core.logic`. Do **not** start with a big general unification framework.

Start with a small custom unifier over plain EDN/Clojure maps:

* closed-field equality
* `?vars` in values
* map subset matching
* vector/list exact shape where needed
* explicit bindings map
* projection helpers for indexing and edge construction

Why: your current kernel already speaks plain maps everywhere. A small custom matcher will fit `context.clj`, be easier to debug, serialize cleanly, and preserve provenance. You can swap in richer machinery later if it earns its keep.

### Graph construction

Start dumb.

Naive pairwise comparison is fine for the first pass if the rule set is tens or low hundreds. The key is not asymptotics yet. The key is **edge semantics** and **debuggability**.

Use projection signatures first:

* emitted fact/type
* antecedent fact/type
* goal-type
* concern-type
* explicit index projections

Build inverted indexes over those signatures, then derive edges. Bidirectional BFS is enough for early path search.

Do **not** learn edges from usage. Rank paths later if you want. Do not learn the graph itself.

### Integration with current kernel

The rule engine should target the existing substrates, not replace them.

Reads:

* visible facts from `context.clj`
* current goal/context pointers from `goals.clj` / `control.clj`
* episodes and indices from `episodic_memory.clj`

Writes:

* `cx/assert-fact`
* `cx/retract-fact`
* `goals/activate-top-level-goal`
* existing trace fields extended with rule provenance

If a design answer invents a separate world-state object model detached from `context.clj`, it is wrong.

### Executor dispatch

Use a simple `case` on `(:kind (:executor rule))`.

You do not need protocols or multimethods yet.

`multimethod` looks elegant and buys almost nothing here.

### `:llm-backed` executor

Demote this in v1.

First get:

1. fact shape
2. matcher
3. one tiny generic inference loop
4. trace/provenance hooks
5. one tiny connection graph

Then maybe add one `:llm-backed` rule.

And even then, the default failure mode should be:

* validate result
* on failure: log and skip or fall back
* no retry storm
* no fancy async scheduler

### Serialization and rule versioning

EDN. Immutable values. Stable logical rule IDs plus revision metadata.

Do not mutate rule meaning in place without leaving a revision trail. You will want replay/debug later.

### Build order

Your current build order is close, but I’d tighten it to:

1. decide fact shape that composes with `context.clj`
2. implement small custom matcher/unifier
3. implement tiny generic inference loop over visible facts + touched-fact scoping
4. extend `trace.clj` to record rule firings/bindings
5. hand-author tiny rule set
6. derive tiny connection graph
7. pilot one planning dispatch path
8. only then consider extracting pieces from `goal_families.clj`
9. only then consider `:llm-backed`

I would not start by importing Mueller’s KB wholesale. That’s a good way to hide structural mistakes under volume.

### Minimum useful test set

Not 1 rule. Not 200.

Something like 8-15 hand-authored rules is enough if they include:

* one 2-3 step inference chain
* one planning dispatch case
* one episode index emission/retrieval case
* one 3-hop rule-path in the connection graph
* one failure case with provenance visible in trace

### What you will regret later

These are the real landmines.

1. **Conflating the kernel rule graph with the authored graph seam**
2. **Overcommitting to `core.logic` before earning it**
3. **Failing to fix fact shape early**
4. **Skipping provenance in trace output**
5. **Putting usage-weighted learning into edge existence**
6. **Designing async LLM execution before the pure substrate exists**
7. **Ignoring `goal_families.clj` as migration evidence**
8. **Treating benchmark adapters as irrelevant instead of asking what should move inward**

## Bottom line

The prompt is worth using, but I would not send it as-is.

I’d make three hard changes before using it:

* make the current-kernel description much more honest
* attach `goal_families.clj` plus the adapter/trace files
* add explicit questions about migration, graph-seam separation, and control-loop placement

That would move it from “good abstract architecture prompt” to “actually calibrated to this repo.”
