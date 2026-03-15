Findings first.

1. The seam is not too thin. The current projector is wrong.

2. The real failure point is the single-pass structured-output call in `run_arm_middle()`: `build_middle_prompt()` asks the model to do two jobs at once, write a good beat and compress it into seam fields. The prose comes out richer than the graph, then `validate_graph_projection()` waves it through because it only checks legality, not whether the projection preserved causal lineage or mixed pressure.

3. Kai step 1 and Rhea step 1 are the clean proof. Kai’s prose and sidecar carry both `attachment_threat` and `obligation`, but the graph exports only `attachment_threat`. Rhea’s prose and sidecar carry both `status_damage` and `obligation`, but the graph exports only `status_damage`. So the bug is not “the model can’t write mixed pressure.” The bug is “the projector collapses to dominant concern.”

4. This is not a matter of taste. All four current fixtures already define the intended richer seam:

   * Kai: `attachment_threat + obligation`
   * Maren: `obligation + attachment_threat`
   * Rhea: `status_damage + obligation`
   * Tessa: `status_damage + obligation`

   So one-tag projections are benchmark misses, not alternate valid readings.

5. The right next move is not full bidirectional sync. The broader graph/prose landscape says automated two-way propagation is still the missing piece, and the near-term viable pattern is extract/modify/regenerate, not magical live coherence. Graphologue is the clearest precedent there. 

## MVP recommendation: generate, then project

Bluntly: stop letting the scene-writing model choose the seam.

Split the current middle arm into three stages:

1. **Generate the scene text**
2. **Project seam fields**
3. **Validate against fixture + sidecar**

Your own architecture is already “propose → validate → admit,” so this is a local compiler split, not a redesign. 

### A. First pass: scene only

`run_arm_middle()` should first ask for just:

```json
{ "candidate_text": "..." }
```

No `graph_projection` in that call.

That removes the current compression pressure where the model writes a vivid beat and then immediately reaches for the shortest legal label set.

### B. Second pass: seam projection

Now feed the generated `candidate_text` into a separate projection pass.

But do **not** make that second pass “another freeform LLM guess.” Split the seam fields into two classes.

### B1. Deterministic fields: do not let the LLM decide these

For the current prototype, these should be derived mechanically from the sidecar / fixture state:

* `origin_pressure_refs`
* `pressure_tags`
* `practice_tags`
* `source_lane`
* `scope`
* `revisability`

Concretely:

* `origin_pressure_refs = source_concern_ids`
* `pressure_tags = concern_type(origin_pressure_refs)`
* `practice_tags = [practice_context.practice_type]`
* `source_lane = "L2"`
* `scope = "proposal"`
* `revisability = "ephemeral_candidate"`

That is the clean fix.

Why? Because `origin_pressure_refs[]` is supposed to be lineage, not salience. The seam contract already treats it as the stable cross-lane lineage strip. It is not “whichever pressure the prose foregrounded most.” It is “which pressures materially produced this node.” For the current four fixtures, that means dual-pressure projection by default.

So yes: for **Kai, Maren, Rhea, and Tessa as they currently exist**, single-pressure projection should fail validation unless there is an explicit suppression justification. Right now there is no such justification.

### B2. Extractive fields: let the model help, but bounded

These are the fields the second pass should still infer:

* `setup_refs`
* `payoff_refs`
* `option_effect`
* `delta_tension`
* `delta_energy`
* maybe `confidence`
* maybe `node_id` if you don’t want to slug it mechanically

Inputs to this pass should be:

* `candidate_text`
* allowed ref universe from the fixture
* active situation id
* active concern ids/types
* `practice_context`
* selected operator family

That is: **text + bounded sidecar + fixture universe**.

Not text alone.
Not full raw runtime dump.

Text alone will miss latent but active obligation pressure. Sidecar alone will overproject and ignore what the scene actually dramatized. The right shape is: sidecar supplies the legal lineage and context window; text supplies the local realization.

## Why this works

Because it fixes the actual asymmetry.

Right now the prose is the high-bandwidth object and the graph is the compressed afterthought. The second pass turns the graph into a compiler product instead of a trailing checkbox.

It also keeps the seam thin. You are **not** pushing `causal_slice`, `appraisal_frame`, or `practice_context` into the graph. You are just making the allowed seam residues less lossy.

And it makes later batch work actually meaningful. Right now batch admission cannot score real pressure coverage or overdetermination because the graph is lying by omission. Once `origin_pressure_refs[]` and `pressure_tags[]` are preserved properly, admission can finally distinguish “another Kai avoidance beat” from “a Kai beat carrying both attachment and duty in a graph-useful way.”

## Concrete validator patch

Add a second validator, something like:

```python
validate_graph_projection_against_sidecar(
    fixture,
    graph_projection,
    sidecar,
    candidate_text,
)
```

New rules:

1. `origin_pressure_refs` must match `source_concern_ids` for the current four fixtures.
2. `pressure_tags` must be mechanically derivable from `origin_pressure_refs`.
3. `practice_tags` must match `practice_context.practice_type`.
4. `setup_refs` and `payoff_refs` must be legal **and** temporally sane.
5. `option_effect` must agree with the scene’s actual function.

That last two are where the current outputs are weakest.

Example: Kai step 1 is graph-valid now, but its ref orientation is probably wrong. The pending harbor meeting is setup; the threshold departure is payoff. The worked trace already tells you that. The current output’s `setup_refs` / `payoff_refs` feel opportunistic because the model is guessing causal role from local salience, not from local structure.

## Failure modes of the MVP

1. **Overprojection from sidecar**
   If later worlds have many active concerns, copying all of them into lineage may become noisy. For the current four fixtures, though, it is correct.

2. **Second-pass ref guessing is still weak**
   You will fix pressure collapse immediately, but `setup_refs` / `payoff_refs` may still wobble because causal grounding is the hard part.

3. **Upstream state can still be wrong**
   If the sidecar is wrong, the seam will now preserve the wrong thing more faithfully.

4. **Two-pass latency / cost**
   Real, but this is authoring-time generation. Cost is not the bottleneck right now. Wrong graph residue is.

## Higher-octane design: POCL-lite causal sketcher

This is where you stop asking the model to guess setup/payoff refs at all.

Not a full planner.
Not a world solver.
A **local causal scaffold builder** between generation and validation.

Its job is narrow:

* what does this moment **require**
* what does it **establish**
* what does it **threaten**
* what `option_effect` follows from that

So the artifact looks like:

```yaml
CausalLinkSketch:
  candidate_id: beat_kai_kitchen_avoidance
  requires_refs:
    - ev_harbor_meeting_tonight
  establishes_refs:
    - sit_threshold_departure
  threatens_refs:
    - preserve_possible_repair
  inferred_option_effect: clarify
  pressure_lineage:
    - cc_attachment_threat
    - cc_obligation
```

This does **not** go into the graph.
It lives in sidecar / compiler state.

Then the graph gets:

* `setup_refs = requires_refs`
* `payoff_refs = establishes_refs`
* `option_effect = inferred_option_effect`

Pressure lineage stays deterministic from the sidecar.

That split is important:

* **pressure/origin/practice** are lineage/provenance questions
* **setup/payoff/option_effect** are causal-structure questions

Do not use one mechanism for both.

## How many schemas for v1?

20–40 is too much right now.

For the current four fixtures, **12 schemas is enough$_{75%}$**. Eight is bare minimum. Twelve gives you enough spread without turning into ontology theater.

Roughly:

### Kai

1. pending summons + ritual delay
2. charged shared place intrudes during avoidance
3. delay hardens toward departure threshold

### Maren / Rhea

4. threshold rehearsal before entry
5. first-line revision under anticipated confrontation
6. public sting / prior correction reactivates at threshold
7. preparation clarifies without crossing threshold

### Tessa

8. public harm already done, motive-defense starts
9. half-apology collapses into explanation
10. reply threshold after blame text

### Cross-fixture glue

11. charged place / object reactivates secondary concern
12. node sharpens a future threshold without committing it

Each schema needs only:

* `schema_id`
* `operator_family`
* `practice_type`
* `phase`
* `preconditions`
* `allowed_ref_bindings`
* `requires_refs`
* `establishes_refs`
* `threatens`
* `default_option_effect`

That’s it.

No full plan search.
No open-condition explosion.
No full temporal logic.

## How POCL-lite interacts with the MVP

Additive first. Then partial replacement.

* **Now**: two-pass projection fixes pressure collapse.
* **Next**: POCL-lite replaces LLM guessing for `setup_refs`, `payoff_refs`, `option_effect`.
* **Later**: those causal effects can become the substrate for Q7-style situation progression.

So no, POCL-lite should not replace the whole MVP. It should replace the weakest extractive subproblem inside it.

## Smallest honest prototype order

1. Split scene generation from seam projection.
2. Make `origin_pressure_refs`, `pressure_tags`, and `practice_tags` deterministic.
3. Re-run Kai step 1 and Rhea step 1. If they still export one tag, the patch is wrong.
4. Run the same path on Maren and Tessa. They should also preserve dual pressure.
5. Only then add POCL-lite for ref grounding.
6. Start with **3 schemas**:

   * Kai avoidance ritual
   * Rhea threshold rehearsal
   * Tessa aftermath rationalization
7. Compare ref quality against the current direct-prompted refs.

## What not to build yet

* no full bidirectional prose↔graph sync
* no full planner
* no solver
* no new seam ontology
* no runtime traversal changes
* no graph-to-prose regeneration loop

The seam contract is already good. The compiler feeding it is not.

Bottom line: **determinize lineage, extract only what is still genuinely ambiguous, then add a tiny causal sketcher for refs.** That gets you from graph-valid to graph-useful without reopening the architecture.
