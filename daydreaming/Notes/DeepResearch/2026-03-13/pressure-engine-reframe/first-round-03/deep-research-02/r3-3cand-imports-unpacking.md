## 1. Top-line ranking

1. **Weighted abduction / inverse planning**
2. **Soft-constraint optimization**
3. **Partial-order causal-link planning**

That ranking seems right$_{85%}$ for *this* architecture, not in general.

Why:

* Your current generation MVP already has a decent middle: `CausalSliceV1 -> AppraisalFrame -> PracticeContextV1 -> retrieval -> operator scoring -> candidate -> local graph validation`. What it still fakes is the step *before* that: sparse primitives -> latent concern / blocker / threatened goal / practice bias. That is exactly the hole weighted abduction fills. Right now that step is partly hand-specified in the fixture and mostly stubbed in the harness. `(27-authoring-time-generation-reframe.md; 28-l2-schema-from-5pro.md; 30-authoring-time-generation-prototype-spec.md; authoring_time_generation_prototype.py)`

* The next missing layer after that is not “better prose.” It is **admission**: given a bag of locally plausible candidates, which subset deserves to become graph material? The current harness has local validation, but no global subset compiler. Soft constraints fill a real missing layer. `(30-authoring-time-generation-prototype-spec.md; 02b_reply.md; authoring_time_generation_prototype.py)`

* POCL is useful, but third. It improves `setup_refs` / `payoff_refs`, earnedness, and local causal prep. It does **not** solve the first missing inference layer or the missing admission compiler. `(21-graph-interface-contract.md; 30-authoring-time-generation-prototype-spec.md)`

Blunt version: if you only steal one next, steal **weighted abduction**. If you get the second steal soon after, make it **soft-constraint admission**. POCL-lite is the third pass, and it should stay lite.

Also: **all three are authoring-time / compiler-time imports. None belong in shipping runtime.** Runtime already has its job: traverse graph-native residues. Do not smuggle a second mind into L3. `(11-settled-architecture.md; 21-graph-interface-contract.md; 22-city-routes-robustness-interpretation.md; 26-city-routes-contrasting-conductor-interpretation.md)`

## 2. Best steal from weighted abduction

The best steal is **not** “abduction” as a grand AI banner. It is a **top-k typed latent-explanation builder** for narrative primitives.

What it should do:

Given a sparse bundle like:

* character contradiction
* target relation
* charged place
* active situation
* a few backstory episodes

infer a small number of candidate explanations like:

* dominant concern
* threatened goal
* blocker
* likely attribution
* likely practice type
* likely operator bias

For Kai, the useful abductive move is not “generate a scene.” It is:

* “this letter reactivates `attachment_threat`”
* “the threatened thing is possible repair”
* “the blocker is loss of control once contact begins”
* “the available practice is `evasion`”
* “the likely immediate operator is `avoidance` or `rationalization`”

That is the missing `0 -> CausalSlice` stage in your current chain. `(29-worked-trace-kai-unopened-letter.md; 30-authoring-time-generation-prototype-spec.md; authoring_time_generation_kai_letter_v1.yaml)`

**Minimum viable version**

A tiny object like this is enough:

```yaml
AbductiveHypothesis:
  hypothesis_id: h1
  dominant_concern_type: attachment_threat
  target_ref: sister
  threatened_goal: preserve-possible-repair
  blocker: first_true_thing_triggers_loss_of_control
  attribution:
    actor: kai
    intentional: true
  likely_practice_type: evasion
  likely_operator_bias:
    avoidance: 0.8
    rationalization: 0.6
    rehearsal: 0.4
  evidence_refs:
    - ep_last_rupture
    - ep_avoidance_consequence
    - sit_unopened_letter
  score: 0.84
```

Do **top 3 hypotheses**, not one. Score them by:

* primitive coverage
* contradiction penalties
* fit to explicit theme-rule triggers
* downstream fit to operator/practice expectations

Use explicit slots and a tiny scorer. No theorem prover. No “general abductive reasoning framework.”

**Where it lives**

This lives at **primitive -> concern/blocker inference**, before `build_causal_slice()` in the current harness. In concrete terms, it should replace or precede the current hand-seeded `dominant_concern_id` and rigid concern extraction path. `(authoring_time_generation_prototype.py; authoring_time_generation_kai_letter_v1.yaml)`

**Authoring-time vs runtime**

Authoring-time only. Keep it out of runtime.

The runtime graph scheduler should see the residue of this inference, not rerun it.

**Relation to the graph seam**

Abduction should **not** write graph nodes directly.

It should produce sidecar-level inputs that later compile into:

* `pressure_tags[]`
* `practice_tags[]`
* `origin_pressure_refs[]`

If you keep `supporting_pressure_refs[]`, treat them as provisional compiler/runtime convenience, not canonical cross-lane ontology. The frozen seam still names `origin_pressure_refs[]` as the stable lineage strip. `(21-graph-interface-contract.md; 01-graph-interface-seam-audit/response.md; city_routes_experiment_1_v0.yaml)`

**Smallest falsifiable prototype**

Use the Kai fixture.

* Remove hand-seeded active concerns from the first run.
* Feed only character seed, backstory episodes, target relation, and active situation.
* Generate top-3 abductive hypotheses.
* Pass the best one into the existing middle-layer chain.
* Compare against:

  * current hand-seeded arm B
  * flat prompting arm A

Measure:

* dominant concern correctness
* selected practice correctness
* selected operator correctness
* graph-valid candidate rate
* keep/reject rate under human curation

It passes if the top-1 or top-2 hypothesis recreates the expected `attachment_threat -> evasion -> avoidance` chain often enough to beat the flat baseline and get close to the hand-seeded reference. `(29-worked-trace-kai-unopened-letter.md; 30-authoring-time-generation-prototype-spec.md)`

**What not to build yet**

* full abductive logic programming
* a world-scale hidden-state model
* full Bayesian inverse planning
* free-form inference over raw prose without typed slots
* runtime abduction

If you can’t write the hypothesis object on one screen, you’re already overbuilding.

## 3. Best steal from soft-constraint optimization

The best steal here is **an admission compiler**.

Not “MaxSAT” as a vibe. Not solver worship. A compiler.

Your current harness can produce one locally valid candidate. It cannot answer the real authoring question: from 12-20 plausible candidates, which subset should become graph material? That is why this is second. `(30-authoring-time-generation-prototype-spec.md; authoring_time_generation_prototype.py; 02b_reply.md)`

**Minimum viable version**

Take a small batch of locally valid candidates and select a subset under:

### Hard constraints

* all refs resolve mechanically
* no contradiction with current canon
* no illegal `option_effect`
* no impossible simultaneous event ordering
* no duplicate near-identical nodes
* if a candidate claims payoff pressure, required setup exists already or is admitted in the same batch

### Soft objectives

* maximize pressure coverage
* maximize overdetermination
* maximize setup/payoff closure
* maximize situation spread
* maintain some operator/practice variety
* minimize duplicates and edit burden
* maybe maintain a sane mix of `open / clarify / close / none`

That is enough.

For batch sizes ≤ 18, brute force or a tiny ILP is fine. You do **not** need a branded solver on day one.

**Where it lives**

This lives at **graph compilation/admission**, after local candidate generation and local validation, before human curation and graph write.

Second-wave use: near-miss repair. But first use is subset selection, not repair.

**Authoring-time vs runtime**

Compiler-time only. Absolutely not runtime.

Do not put a solver in traversal. The runtime already consumes the graph via features like setup satisfaction, structural tension, and overdetermination. `(graffito_pilot.py; reading-list/08-l3-experiment-1-synthesis.md)`

**Relation to the graph seam**

This is where overdetermination stops being decorative provenance and starts affecting what gets admitted. That is a direct correction your own source notes already make. `(06-source-miss-scan/response.md; 28-mueller-authoring-time-generation-synthesis.md)`

This compiler should explicitly reward candidates that:

* touch multiple unresolved pressures
* close or prepare multiple lines
* improve the graph as a scheduler substrate

That matters because the shipping runtime already reads graph-native fields like:

* `setup_refs[]`
* `payoff_refs[]`
* `option_effect`
* `pressure_tags[]`
* `origin_pressure_refs[]`
* provisional `supporting_pressure_refs[]`

via `_setup_match_ratio`, `_structural_tension_score`, and `_overdetermination_score()`. So admission is not just “writer taste”; it determines whether the runtime has anything worth traversing. `(graffito_pilot.py; city_routes_experiment_1_v0.yaml)`

**Smallest falsifiable prototype**

Take one fixture and generate a batch, not one node.

Compare three selectors:

1. greedy top-k by local score
2. soft-constraint subset selector
3. human pick from same batch

Measure:

* keep/reject yield
* edit burden
* setup/payoff closure
* pressure coverage
* duplicate rate
* average overdetermination score
* downstream scheduler readiness on the admitted graph slice

It passes if selector (2) beats greedy (1) on closure and keep-rate without increasing review burden. If it doesn’t, drop the solver rhetoric and keep a simpler scorer.

**What not to build yet**

* whole-world optimization
* online re-solving during traversal
* solver-generated prose
* global canon repair
* full automatic “minimal edit” repair search as phase 1

Start with subset choice. That is the missing mechanism.

## 4. Best steal from partial-order causal-link planning

The best steal is **POCL-lite as a causal-link sketcher**, not a planner.

You do not need UCPOP with different nouns. You need a pass that can say:

* what this candidate *requires*
* what this candidate *establishes*
* what it *threatens*
* which refs should become `setup_refs`
* which refs should become `payoff_refs`

That is the right size. `(21-graph-interface-contract.md; 30-authoring-time-generation-prototype-spec.md)`

**Minimum viable version**

For each candidate, produce something like:

```yaml
CausalLinkSketch:
  candidate_id: kai_letter_avoidance_001
  requires_refs:
    - ev_harbor_meeting_tonight
  establishes_refs:
    - sit_threshold_departure
  threatens_refs:
    - possible_repair_if_delay_hardens
  order_constraints:
    - ev_harbor_meeting_tonight before kai_letter_avoidance_001
    - kai_letter_avoidance_001 before sit_threshold_departure
  inferred_option_effect: clarify
```

Then compile:

* `requires_refs -> setup_refs[]`
* `establishes_refs -> payoff_refs[]`
* `inferred_option_effect -> option_effect`

This pass can also emit “missing support” errors for repair or rejection.

**Where it lives**

This lives between **candidate generation** and **graph validation/admission**.

It can also serve candidate repair by telling you why a node is graph-useless:

* missing setup
* unsupported payoff
* impossible order
* threatens already-admitted closure

That is a good validator. It is not a story planner.

**Authoring-time vs runtime**

Authoring-time only.

Runtime should see only the compiled residues:

* `setup_refs[]`
* `payoff_refs[]`
* `option_effect`
* maybe `event_commit_potential`

No runtime planner. No runtime open-condition search.

**Relation to the graph seam**

This is the import most directly tied to the seam.

Its job is to make `setup_refs[]` and `payoff_refs[]` **mechanically resolvable**, which your seam docs already insist on. It also helps stabilize `option_effect` so the runtime feature layer is not guessing from legacy vocabularies. `(21-graph-interface-contract.md; 01-graph-interface-seam-audit/response.md; graffito_pilot.py)`

It also helps with overdetermination, but indirectly: if one candidate establishes or clarifies several open lines, that becomes visible to the admission compiler.

**Smallest falsifiable prototype**

Keep the candidate prose generation exactly as-is.

Then compare two ways of getting `setup_refs[]` / `payoff_refs[]`:

1. direct prompting
2. separate POCL-lite causal-link sketching pass over the candidate plus fixed reference universe

Measure:

* resolvable-ref rate
* hallucinated-ref rate
* human “earnedness” score
* clarity of `option_effect`
* downstream validation pass rate

If POCL-lite doesn’t materially improve ref quality, do not romanticize it.

**What not to build yet**

* full partial-order search to story end
* generic action schema libraries
* plan-space search over the whole world bible
* runtime planner
* deep threat resolution machinery

You need better graph compilation, not a classical planning research program.

## 5. Prototype order

Here is the order I’d actually build:

1. **Add weighted abduction in front of the current harness.**
   Replace hand-seeded concern selection with top-k typed latent hypotheses.

2. **Batch generation, not single-candidate generation.**
   Generate a small candidate bag across top abductive hypotheses and top operator families.

3. **Run POCL-lite on each candidate.**
   This is where `setup_refs` / `payoff_refs` stop being mostly vibes.

4. **Run local validation.**
   Keep the current graph projection validator, extended with causal-link checks.

5. **Run soft-constraint admission over the valid bag.**
   Choose the subset that actually improves the graph.

6. **Human curation on the selected subset only.**

That ordering is different from the strategic ranking on purpose.

* **Strategic ranking** by marginal value: abduction > soft constraints > POCL.
* **Execution order inside the compiler**: abduction -> generation -> POCL-lite -> soft constraints.

That is not a contradiction. The solver needs good per-candidate structure, so POCL-lite physically runs before admission. But soft constraints still matter more as an architectural steal because they fill an entire missing layer.

## 6. Minimal architecture sketch

```text
narrative primitives
  ↓
abductive hypothesis builder            [new]
  ↓ top-k hypotheses
CausalSliceV1 / AppraisalFrame /
PracticeContextV1 / retrieval /
operator scoring                        [existing]
  ↓
candidate batch generation              [extend current harness]
  ↓
POCL-lite causal-link sketcher          [new]
  ↓
local graph validation                  [existing + extend]
  ↓
soft-constraint admission compiler      [new]
  ↓
human curation
  ↓
authored graph
  ↓
runtime traversal scheduler             [unchanged]
```

Mapped onto the current harness:

* **before** `build_causal_slice()`
  add `abduce_hypotheses(primitives)`

* **around** `run_arm_middle()`
  generate a bag, not one candidate

* **before** `validate_graph_projection()`
  add `compile_causal_links(candidate, allowed_refs)`

* **after** local validations
  add `select_admissible_subset(candidates)`

Outputs to the graph seam should stay thin:

* `setup_refs[]`
* `payoff_refs[]`
* `pressure_tags[]`
* `practice_tags[]`
* `origin_pressure_refs[]`
* maybe provisional `supporting_pressure_refs[]`
* `option_effect`
* the existing provenance fields

Keep these out of the graph:

* abductive hypothesis objects
* full `CausalSlice`
* full `AppraisalFrame`
* open conditions
* threat sets
* solver state

Those stay in sidecars, traces, or compiler artifacts. `(21-graph-interface-contract.md; 28-l2-schema-from-5pro.md)`

One more nitpick: if `supporting_pressure_refs[]` remains in use, treat it as a **compiler/runtime convenience field**, not settled cross-lane canon. The frozen seam still says the stable lineage strip is `origin_pressure_refs[]`. Don’t let field drift outrun the contract. `(21-graph-interface-contract.md; 01-graph-interface-seam-audit/response.md)`

## 7. Red flags / likely overbuild traps

* **Do not put any of these in runtime.**
  Runtime reads graph residue. That is the contract.

* **Do not build “abduction” as a general reasoning system.**
  Build a typed top-k hypothesis generator.

* **Do not let soft constraints become a whole-world theorem.**
  Start with small batch subset selection.

* **Do not build a full planner.**
  Build a causal-link sketcher that makes `setup_refs` / `payoff_refs` real.

* **Do not let `supporting_pressure_refs[]` become an ontology leak.**
  The seam is already strict for a reason.

* **Do not reward provenance without rewarding overdetermination.**
  A node serving two live pressures should outrank a node serving one. If that never changes admission, you missed the point. `(06-source-miss-scan/response.md; 28-mueller-authoring-time-generation-synthesis.md)`

* **Do not use solver complexity to hide weak candidate generation.**
  If the batch is junk, the solver just picks the best junk.

* **Do not let POCL invent refs outside the fixed universe.**
  `setup_refs[]` / `payoff_refs[]` must resolve against `events`, `situations`, or `reference_markers`. That rule exists to stop hand-wavy earnedness. `(30-authoring-time-generation-prototype-spec.md; 21-graph-interface-contract.md)`

* **Do not reopen L3 because these imports enrich graph tags.**
  The scheduler thesis has already earned provisional support. Upstream compiler work should feed it, not restart it. `(22-city-routes-robustness-interpretation.md; 26-city-routes-contrasting-conductor-interpretation.md)`

Shortest honest summary:

* **First steal:** weighted abduction for sparse primitive -> latent pressure inference
* **Second steal:** soft-constraint admission for candidate bag -> graph-worthy subset
* **Third steal:** POCL-lite for `setup_refs` / `payoff_refs` and earnedness

Everything else is abstraction theater until those three are doing real work.

Look away for a moment. Unclench your jaw. Drop your shoulders.
