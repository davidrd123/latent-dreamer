[Editable markdown](sandbox:/mnt/data/D3-narrative-planning-section.md)

### D3. Narrative planning (IPOCL + Sabre) vs. our traversal + graph compilation

**Direct answer**

Do not turn this stack into IPOCL or Sabre. The settled architecture is right: `L2` owns local motivated cognition, `L3` owns traversal over authored graph material, and the graph is the membrane. The current pilots and `graffito_pilot.py` are already proving a graph-native local scorer with recency/activation/tension features and no forward plan. Keep that center of gravity. Narrative planning should enter as a missing middle representation and a verifier layer, not as a new runtime sovereign.

* **Frames of commitment:** Yes. A lightweight `PursuitThread` is the right bridge. Concerns tell us what hurts; pursuits tell us what the character is trying to do across multiple situations. That is the missing representation between L2 local concern dynamics and L3 multi-situation coherence already banked in `36-current-synthesis.md` and `reading-list/21-narrative-planning-extraction.md`.
* **Belief vs truth:** Do not globally swap authorial truth for believed state. Keep authored truth as the base world model. The current generator resolves the active situation from authored fixture truth via `resolve_situation()` and builds `CausalSlice` from that. That should remain the default. Add a selective belief sidecar only when the beat depends on concealment, mistaken inference, or theory-of-mind. For Kai's unopened-letter benchmark, truth-conditioned generation is good enough for pre-contact avoidance. It only becomes belief-sensitive if the scene is about what Kai imagines the letter contains, rather than the fact that he fears engaging it.
* **Action vs aftermath:** This is a real gap. Right now generation mostly rebuilds from authored fixture state, and Q7 is right that situation-state carry-forward is weak. Add explicit ownership, observation, and automatic aftermath semantics to the typed move/provocation layer. Tessa rationalizing should usually mutate carried-forward local state or self-belief, not canon, unless the beat includes an outward act that someone can observe.
* **Intentional completeness:** Not needed for patch tests. Those tests are proving slot survivability, not multi-situation coherence. It becomes needed for full graph assembly and watched runs as a verifier: every non-accidental, character-significant node should either belong to an active/blocked/abandoned pursuit or be explicitly marked as a happening.
* **Failed or abandoned pursuits:** Reject IPOCL's success-only assumption. In this project, blocked, failed, and abandoned pursuits are not edge cases. They are the pressure engine working.

**What to import**

1. **A lane-local `PursuitThread` object.** Keep it small.

   ```text
   PursuitThread {
     id
     owner_agent_id
     motivating_ref          // event, memory reminder, provocation, or selected node
     goal_condition
     supporting_node_ids[]
     supporting_situation_ids[]
     status                  // active | blocked | abandoned | achieved
     termination_reason?
   }
   ```

   This is the IPOCL steal that actually matters. It gives L3 and the dashboard a handle on multi-situation continuity without turning L3 into a planner.

2. **Motivation provenance.** When a pursuit starts, record what made it legible: motivating event, reminder, revelation, or selected node. This is the missing answer to “why is this character doing this now?” and it fits the existing provenance discipline.

3. **A selective `BeliefEnvelope` or believed-circumstances sidecar.** Not a repo-wide belief calculus. Use it only for secrecy, deception, bluffing, mistaken identity, unopened messages, and other scenes where action legibility depends on what the character thinks is true. Per Q11, compile it into the prompt view when needed; do not dump raw belief JSON into prompts.

4. **Action / aftermath split with observation semantics.** Sabre is right here. A chosen move should be separate from mandatory consequences. Minimum import:

   * responsible or consenting characters
   * observers
   * automatic triggers or aftermath
   * belief-update targets

   This should live in the provocation / delta / local-state machinery, not in `L3`.

5. **An intentionality verifier.** Use IPOCL's intentional completeness as an audit, not a planner requirement. For full graph assembly, add an orphan-action check: if a node shows a character-significant act, the system should recover the pursuit it serves, or mark it as a happening / accident / compulsion. Q6's generate-then-project discipline is the right place to attach this.

6. **Blocked / abandoned outcome residue.** Represent failure explicitly in the pursuit thread, and export only stable residue when needed via existing graph-compatible fields such as `branch_outcome_tags[]`, `setup_refs[]`, `payoff_refs[]`, and `origin_pressure_refs[]`. Do not invent a second live ontology in the graph.

**What to skip**

* **Do not replace `L3` with a centralized planner.** `11-settled-architecture.md` and `12-prior-work-synthesis-against-settled-architecture.md` already settled this correctly. The current pilots are proving something real with graph-native local scoring. Do not throw that away for puppetmaster search.
* **Do not turn the runtime `Director` into Sabre.** `36-current-synthesis.md` is explicit that the runtime `Director` is interpretive perturbation, not world authorship.
* **Do not make full belief-state planning the default cost of every scene.** Most scenes do not need nested beliefs. Use belief sidecars only where hidden knowledge changes what counts as legible action.
* **Do not push live pursuit state, live beliefs, or planner internals into the shared graph.** `21-graph-interface-contract.md` is right. The graph should carry stable residue, not lane-local runtime machinery.
* **Do not use intentional completeness as prompt-time ceremony for narrow patch tests.** It will add bookkeeping and not buy much until sequences actually span situations.
* **Do not inherit IPOCL's success-only frame.** Success closure is the wrong model for a pressure engine built around avoidance, stalled repair, missed contact, and failed approach.

**What changes our architecture**

1. **New middle representation between L2 and L3:** add a lane-local `PursuitThread` ledger. This is the actual architectural bridge between concern-level cognition and multi-situation coherence.
2. **Selective belief / observation layer:** add a small `BeliefEnvelope` plus `ObservationTrigger` or aftermath semantics to the typed provocation / local-state seam. This is a new sidecar layer, not a graph rewrite.
3. **Verifier pass expansion:** add two new critics:

   * believed-state explanation critic: “could this action make sense from what the character believes?”
   * orphan-action / intentionality critic: “what pursuit is this act serving?”
4. **Carry-forward state gets sharper:** Q7 already shows that situation-state carry-forward is weak. Sabre-style aftermath should land as lane-local derived state updates, so accepted beats change what the next step actually sees.
5. **Possible later graph residue, but only after proof:** if pursuit support needs to survive across lanes, prefer existing fields first. `origin_pressure_refs[]`, `setup_refs[]`, `payoff_refs[]`, and `branch_outcome_tags[]` already cover most of the stable residue. Do not expand the graph contract until the sidecars prove a missing field is genuinely needed.

**Priority**

* **Not needed before the narrow patch tests.** Patch tests are proving graph-slot survivability and human preference, not multi-situation intentional coherence.
* **Needed before full graph assembly.** The first import to schedule is the lane-local `PursuitThread` plus orphan-action validation. That is the minimum honest move before claiming multi-situation coherence.
* **Needed before stronger watched-run evaluation.** Add action/aftermath and observation semantics before expecting Tessa / Maren / Rhea style sequences to feel causally progressive rather than iterative rewrite.
* **Belief sidecars are selective and later.** Bring them in when a fixture actually depends on hidden content, mistaken inference, or theory-of-mind. Kai's unopened letter does not force this yet; a later “Kai imagines what the letter says” variant would.
* **Full IPOCL / Sabre planning is a later concern that may never be needed.** The right steal is representation and verification discipline, not a new runtime sovereign.
