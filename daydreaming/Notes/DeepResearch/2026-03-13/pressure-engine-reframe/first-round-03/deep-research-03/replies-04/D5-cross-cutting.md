### D5. Cross-cutting: what are we actually missing?

**1. Direct answer**

We are missing four contracts, not a replacement architecture.

First, we are missing a **live lane-local execution state** that can be
projected into the inner-life surface. The current trace/sidecar story
is good for provenance, but it is not the same thing as a maintained
runtime object saying what is currently active, what can continue, what
can be interrupted, and why. The repo already half-knows this: the
current `dreamer-state-packet` is a reduced Director-facing packet, not
an audience-facing mind model. Keep it thin. Add a lane-local execution
state and richer viewer-facing projections instead of pretending logs
are enough.

Second, we are missing a **cross-situation carrier for intentional
continuity**. `CharacterConcern` is pressure. It is not enough to
represent “Kai is still trying to avoid / rehearse / repair this across
three situations.” For that, the system needs a lightweight
`PursuitThread` object that survives situation boundaries through
`CarryForwardStateV1` and makes multi-situation intent legible without
collapsing back into a full planner.

Third, we are missing **selective belief / observation / aftermath
discipline**. The current authorial-truth view is fine for narrow,
single-character fixtures. It will break as soon as scenes depend on
secrecy, staggered revelation, bluffing, rumor, or who actually noticed
what. This does **not** mean importing full Sabre-style nested beliefs
everywhere. It means adding explicit ownership, observation, and
automatic-aftermath hooks in the scenes where wrong beliefs matter.

Fourth, we are missing a real **checker layer**. The pipeline now
validates graph-compilability. It still lacks strong graph/run-level
checks: reachability, dead branches, setup/payoff closure,
intentional-completeness/orphan-action audit, and “does this action
make sense from what the character believes?” critic passes. That is the
missing propose → check/repair → enhance shape across L1 and L3.

So the cross-cutting answer is: the stack is not missing a better base
mind. It is missing the middle contracts that make the current split
watchable, belief-aware where needed, and auditable across situations.

**2. What to import**

- **From Loyall + ABL:** start guards vs continue guards,
  interrupt/continuation rules, and a minimal live execution object.
  Minimum viable version:

  ```text
  SituationRuntimeV1 {
    active_moves[]
    continue_guards[]
    interrupt_handlers[]
    sync_points[]
    local_shared_state
  }
  ```

  For early single-character runs, `sync_points[]` and
  `local_shared_state` can stay mostly empty. The point is to stop
  pretending “selected node” and “finished trace row” are the whole
  runtime.

- **From IPOCL:** a lightweight `PursuitThreadV1` plus motivation
  provenance. Minimum viable version:

  ```text
  PursuitThreadV1 {
    id
    owner_agent_id
    goal_condition
    motivating_event_id?
    supporting_node_ids[]
    supporting_situation_ids[]
    status            // active | blocked | abandoned | achieved
    blocked_by[]
    payoff_event_id?
  }
  ```

  This belongs in `CarryForwardStateV1`, not in the shared graph. Add an
  orphan-action check: every character-significant non-accidental node
  should either belong to a recoverable pursuit thread or be explicitly
  marked as a happening / forced event / reflex.

- **From Sabre:** action ownership plus observation/aftermath semantics,
  not the whole planner. Minimum viable fields on events/situations:

  ```text
  ActionOwnershipV1 {
    consenting_characters[]
    affected_characters[]
    observers[]
    automatic_aftermath[]
  }
  ```

  This is enough to stop collapsing “world truth” and “who knows what”
  in deception- or secrecy-heavy scenes.

- **From FAtiMA + MINSTREL:** checker/validation surfaces. Minimum
  viable version is an offline `graph_audit` / `run_audit` suite that
  reports:
  - unreachable nodes / dead branches
  - unresolved `setup_refs[]` / `payoff_refs[]`
  - event commits with no motivational support
  - pursuit-thread coverage
  - explanation-from-believed-state failures
  - missing trigger / aftermath coverage in belief-sensitive scenes

- **From the repo's own Ask-B direction:** keep the current thin
  Director-facing packet, but add viewer-facing projections rather than
  raw state dumps. The right family is already visible:
  `cycle_packet`, `selection_record`, `director_feedback`, and
  `state_snapshot`, with optional `SituationRuntimeV1` summaries under
  the dashboard.

**3. What to skip**

- Do **not** replace the graph-first stack with a full ABL runtime or a
  Sabre-style centralized puppetmaster.
- Do **not** dump full ABTs, context trees, appraisal objects, or live
  concern stacks into the shared graph. That violates the membrane and
  will rot.
- Do **not** adopt full nested theory-of-mind as the baseline cost of
  every scene. Use belief structure only where secrecy, deception, or
  misread intent are load-bearing.
- Do **not** turn pursuit continuity into full IPOCL planning or accept
  IPOCL's success-only bias. Blocked, failed, and abandoned pursuits are
  the point here.
- Do **not** universalize dialogue-state graphs. They belong only in
  conversation-heavy situations if the current social-practice layer
  proves too weak.
- Do **not** let checker work balloon into a grand formal system before
  the watchable-runtime test. Basic audits first.

**4. What changes our architecture**

1. `CarryForwardStateV1` stops being just loose residue and gains
   explicit `active_pursuit_threads[]`.
2. `SituationLocalStateV1` gains a runtime execution slice: active move,
   continue guards, interrupt state, local observation log, and optional
   shared-state/sync fields for multi-character scenes.
3. The runtime/view split becomes explicit:
   - keep the thin Director-facing packet
   - add viewer-facing projections (`cycle_packet`,
     `selection_record`, `director_feedback`, `state_snapshot`)
   - optionally project `SituationRuntimeV1` into dashboard-friendly
     summaries
4. Event/situation schemas gain small ownership / observation /
   aftermath fields for scenes where wrong beliefs matter.
5. L1 stops being only generation + admission and gets explicit
   checker/repair passes before full graph assembly.

The graph contract should stay thin. At most, add stable residue such as
an explicit `happening` / `non_intentional` marker or sidecar-level
motivation provenance. Full pursuit state, live execution state, and raw
belief traces remain lane-local.

**5. Priority**

- **Before the next watchable-runtime pass:** add the richer
  runtime/view projections. The inner-life dashboard cannot be the
  primary product surface if the only exported mind state is a reduced
  Director packet.
- **Before or alongside the provocation-delta experiment:** add
  `PursuitThreadV1` to `CarryForwardStateV1`. Otherwise “carry + remap”
  stays pressure-only and multi-situation intentional continuity remains
  implicit.
- **Before full graph assembly / multi-situation scaling:** add the
  basic checker suite (reachability, setup/payoff closure, orphan
  actions, pursuit coverage).
- **Only when scenes actually depend on secrecy / misbelief:** add the
  selective observation/belief layer. Important, yes. Universal, no.
- **Later:** joint-behavior/sync machinery and dialogue-topology work,
  once the current watchable surface and provocation seam have proved
  they earn their complexity.

Looking across all six sources:

- **Runtime execution state**: Loyall/ABL have explicit ABTs.
  FAtiMA has centralized state. Our system has traces/sidecars.
  Is there a gap between "we log what happened" and "we
  maintain a live inspectable execution state"? Does the
  inner-life visualization need the latter?

- **Belief and observation discipline**: Sabre has explicit
  observation functions and belief revision. Our system treats
  the world as authorial truth. For multi-character scenes,
  is this sustainable? When does theory-of-mind reasoning
  become load-bearing?

- **Pursuit continuity across situations**: IPOCL has frames of
  commitment. ABL has joint behaviors. Our system has concerns
  that carry intensity across situations but no explicit
  "this character is pursuing X across multiple scenes" object.
  Is the concern the right carrier for this, or do we need
  something new?

- **Verification and validation**: FAtiMA has authoring-time
  validation (reachability, dead branches). MINSTREL has
  checkers as first-class control. Our system validates graph
  projections but doesn't validate graph-level coherence
  (are all setup_refs eventually paid off? are there dead
  branches? is every situation reachable?). Is this a gap
  that matters now, or only at full graph scale?
