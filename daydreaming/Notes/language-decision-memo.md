# Language Decision Memo: Python v0, Clojure v1?

This memo is not about taste or lineage. It is about sequencing risk.

The core question is not "which language is more elegant?" The core question is:

> What is the fastest path to a working daydream engine that can drive the
> existing Scope + Lyria stage, while leaving room to move the symbolic core
> into Clojure if the problem turns out to be genuinely Lisp-shaped?

## Short Answer

Recommended path:

1. Keep the **stage/runtime** in Python. That is not up for debate.
2. Build the first ugly **behavioral v0** in Python.
3. Keep the engine I/O pure data (`DreamNode`, graph, session log, world state).
4. Move the **offline engine core** to Clojure only if v0 proves the hard part is
   branching symbolic state, declarative validation, threshold retrieval, and rule authoring.

This is not punting the language decision. It is making the language decision
depend on measured friction instead of projection.

## What Is Already Fixed

These parts are already effectively Python-bound because of the existing system:

- Scope control surface and REST API
- APC Mini bridge
- palette generation/enrichment pipeline
- Lyria Python SDK integration
- any live renderer / traversal loop that talks to the stage in real time

So the real language question is only about the **offline engine**:

- world state / world bible representation
- validation rules
- daydream goal selection
- threshold retrieval / coincidence logic
- mutation / graph expansion
- graph construction

## Why Python First

Python is the right `v0` language for one reason:

It gets us into contact with the actual unknowns faster.

Those unknowns are not philosophical. They are:

- what a `situation` actually is
- what an `index` actually is
- how often goal types switch
- whether threshold retrieval actually feels different from semantic retrieval
- whether mutation is load-bearing
- what narration does in practice
- what a renderable node contract needs to contain

An ugly Python `v0` answers those quickly because it can plug straight into:

- existing palettes
- existing stage endpoints
- existing Lyria prompt/config conventions
- existing logging habits

If we do not answer those first, a Clojure implementation risks becoming a
beautiful encoding of an untested theory.

## Why Clojure Still Matters

The case for Clojure is real. Mueller's architecture is genuinely close to the
kinds of problems Clojure handles well:

- branching inherited state
- symbolic rule authoring
- immutable snapshots
- declarative queries
- coincidence counting over structured indices
- a plausible future bridge to ACL2-style formalization

If the engine's real difficulty turns out to be:

- "we need better prompt orchestration"

then Python is enough.

If the engine's real difficulty turns out to be:

- "we are fighting mutable bookkeeping, ad hoc validation, hand-rolled rule dispatch,
  and awkward branching state representation"

then Clojure starts to earn its cost.

## Why The Ugly v0 Comes First

The ugly `v0` is not a throwaway. It is the behavioral reference implementation.

Its job is to force answers to the operational questions the design docs keep
abstracting over. Specifically:

- what runs each cycle
- what gets stored
- what gets retrieved
- what counts as a coincidence
- what gets emitted to the stage
- what gets logged

Without that, "Clojure vs Python" is premature because we are choosing an
implementation language for interfaces that are not yet stable.

With that, we get a concrete port target if we later choose Clojure.

## Recommended Sequencing

### Phase A: Python behavioral v0

Build this in Python:

- one-palette episode store
- 3 situations
- 7 goal-type labels with simple trigger policy
- threshold retrieval
- simple mutation fallback
- node emission to the `DreamNode` contract
- caption narration templates
- graph/timeline + session log

Success condition:

- it produces believable `return / drift / hold / counterfactual jump`
- and can target the existing stage

### Phase B: Clojure spike

After `v0` exists, do a small, isolated Clojure spike on the part most likely to benefit:

- world bible in EDN
- minimal validator (`validate_node`, `apply_event`, `contradicts`)
- or threshold retrieval over indexed episodes

Time-box it tightly.

Success condition:

- Clojure feels materially better for the real engine problem,
- not just aesthetically truer to Mueller.

### Phase C: Decide engine split

Only then choose one of:

- stay Python for the full offline engine
- move just validation/world state to Clojure
- move the full offline engine to Clojure and keep stage/runtime in Python

## Decision Criteria For Switching To Clojure

Move the engine core to Clojure if most of these become true:

- Branching state and backtracking dominate engine complexity.
- Validation rules start looking like a rule system, not imperative glue.
- Threshold retrieval wants queryable structured indices, not ad hoc Python loops.
- Event semantics need persistent snapshots and clearer provenance.
- You want EDN/Datalog-level data modeling for the world bible.
- ACL2/formalization remains a serious direction rather than a nice idea.
- The Clojure spike feels faster to extend than the Python version in the exact problem area.

Do **not** move just because:

- Mueller wrote Lisp
- the architecture feels "supposed" to be Lisp
- Python looks less elegant on paper

## Decision Criteria For Staying In Python

Stay Python if most of these become true:

- The hard part is material extraction, prompt compilation, and stage orchestration.
- The symbolic layer remains small enough to express as plain data + functions.
- LLM proposal/validation dominates over handwritten rules.
- The graph is more like media coordination than theorem-prover-adjacent search.
- Clojure would add more learning and integration drag than modeling clarity.

## My Recommendation

Today, the right call is:

- `Python v0` for the first real engine
- `pure data boundaries` so the engine can be replaced
- `Clojure spike after v0`, not before

That gives you the fastest path to a working system without closing the door on
the more Lisp-shaped architecture if the measured engine complexity justifies it.
