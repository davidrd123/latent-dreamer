### D7. The provocation seam: what should the missing object actually be?

#### 1. Direct answer

Keep the current authoring-lane split, but name it more cleanly.
The architectural object should be **`SituationProvocationProposal`**.
The current harness/schema name **`FixtureDeltaV1`** is fine as its serialized `v1` form.
Those are not rivals. One is the concept, one is the current file format.

The minimum working seam is:

- **`SituationProvocationProposal` / `FixtureDeltaV1`** = external world or situation push proposed upstream of reaction generation
- **`CarryForwardStateV1`** = internal cross-situation residue
- **`SituationLocalStateV1`** = active local state for the currently generated situation
- **`ProvocationContextV1`** = reduced typed summary given to the provocation writer

Do **not** collapse those into one blob, and do **not** let the runtime Director write them.
The runtime Director stays a performance-time interpretive perturbation agent.
The Provocation Generator stays an authoring-time proposal writer.
That split is already the strongest current seam analysis and should be frozen.

The only thing the current split still lacks is **not** a full fourth seam object right now.
IPOCL is right that multi-situation coherence eventually needs a lightweight pursuit or commitment thread, but D7 is not the place to force a full `PursuitThread` into `v1`.
The practical move is:

- **now:** keep the current split and make it real
- **reserve:** an optional `pursuit_ref` or `motivation_ref` field in proposal metadata or sidecars
- **later:** promote that to `PursuitThreadV0` only if provocation-driven sequences start losing intentional continuity across situations

So the answer is: **the current split is the right minimum seam for the next experiment, but it should gain a reserved continuity hook, not a full fourth object yet.**

`SituationProvocationProposal` should look closest to an **authored event or situation proposal with explicit aftermath hooks**. It should **not** be a dialogue-state transition, **not** a raw appraisal trigger, and **not** a full planner action language.
Sabre is the right warning here: keep **world change**, **who notices it**, and **automatic aftermath** distinct instead of hiding all three inside one delta blob.

Minimum `v1` shape:

```yaml
SituationProvocationProposal:
  proposal_id: str
  base_situation_id: str
  provocation_type: external_event | knowledge_shift | pressure_shift | threshold_shift
  source_lane: human | L1
  source_agent: human | provocation_generator
  scope: proposal
  commit_semantics: ontic | policy | salience
  fact_deltas: []
  state_overrides: {}
  adds:
    events: []
    situations: []
    reference_markers: []
  updates:
    events: []
    situations: []
    reference_markers: []
  successor_situation_stub: null
  observation_scope: []
  automatic_aftermath: []
  pressure_tags: []
  source_ref: str
  confidence: low | medium | high
  revisability: fixed | editable | ephemeral_candidate
```

Important: **`FixtureDeltaV1` is the current harness name for this schema. `SituationProvocationProposal` is the architectural name.** Keep both levels explicit.

`ProvocationContextV1` should stay small and typed, more like FAtiMA meta-beliefs than a raw state dump.
Minimum useful fields:

- `active_situation_id`
- `dominant_concern_ids`
- `concern_intensities`
- `recent_operator_path`
- `carry_forward_tags`
- `boundary_count`
- `coverage_targets`
- optional `pursuit_ref?`
- optional `current_threshold_state`

Keep out:

- full `CausalSliceV1`
- full `AppraisalFrame`
- raw `EmotionVector`
- the full retrieval pool
- prompt scratch state
- graph internals
- long prose summaries pretending to be state

If the Provocation Generator sees the full raw state dump, the prompt gets brittle.
If it sees only the base fixture, the world pushes will be generic.
The reduced typed packet is the right middle.

One more boundary to freeze: a new provocation does **not** reset internal progression.
The next step should be:

1. apply accepted `FixtureDeltaV1` proposals to get an updated fixture view
2. compress prior local progression into `CarryForwardStateV1`
3. initialize the next `SituationLocalStateV1` from the updated fixture view plus that carry-forward residue

That is the `Q7` rule: **carry + remap**, not reset and not full carry.

#### 2. What to import

1. **From `Ask-B-01` and `Q7`: the state split and carry-remap rule.**
   External world change, cross-situation residue, and active local state must stay separate. The next situation is built from updated fixture view + carry-forward residue, not from prose rewrite.

2. **From `Ask-B-02`: the conceptual object and the proposal ledger discipline.**
   Use `SituationProvocationProposal` as the architecture name and keep proposals append-only until human curation compiles them into accepted fixture versions.

3. **From FAtiMA: the meta-belief seam pattern.**
   `ProvocationContextV1` should be a reduced typed packet that plugs foreign computation into the control loop without collapsing everything into one prompt blob.

4. **From Sabre: separate provocation, observation, and aftermath.**
   A world push should say what changed, who can notice or infer it, and what mandatory follow-ons exist. Do not hide all consequences in one generic delta field.

5. **From MINSTREL: propose/check/repair/enhance at the authoring layer.**
   The right ledger is human-gated and checker-backed. Proposals should be reviewable, mergeable, rejectable, and editable before they touch canon.

6. **From IPOCL: a reserved continuity hook, not a full planner.**
   Add `pursuit_ref` or `motivation_ref` as optional metadata so later coherence work has something to bind to. Do not import full frame-of-commitment machinery into the next experiment.

7. **From `Q6`: deterministic provenance wherever possible.**
   `source_lane`, `source_agent`, `scope`, `revisability`, and `source_ref` should be mechanically assigned. Do not let one-shot prose compression invent lineage.

#### 3. What to skip

- **Do not use one mushy “Director in two modes” concept.**
  Authoring-time provocation and runtime interpretation are different contracts.

- **Do not give the runtime Director world-mutation authority.**
  That is the wrong level and the wrong timebase.

- **Do not collapse internal carried state into `FixtureDeltaV1`.**
  If you do that, the delta becomes a junk drawer.

- **Do not force a full `PursuitThread` into `v1`.**
  The problem is real, but the next seam experiment does not need a full fourth object.

- **Do not import ABL’s active behavior tree or joint-behavior runtime here.**
  That belongs to later situation execution, not the upstream authoring seam.

- **Do not make deep theory-of-mind baseline.**
  Sabre’s belief-sensitive discipline is useful, but full nested-belief tracking is too heavy for this seam.

- **Do not replace the seam with an untyped memory stream.**
  Generative Agents is the right baseline to beat, not the architecture to copy here.

- **Do not rewrite whole situations in prose after every accepted step.**
  That is state drift disguised as progression.

- **Do not silently write proposal-time state into the graph membrane.**
  The graph stores authored structure and stable residue, not live authoring-time carry state.

#### 4. What changes our architecture

1. **Freeze the naming split.**
   `SituationProvocationProposal` is the architectural object.
   `FixtureDeltaV1` is the current serialized harness schema.

2. **Freeze provenance levels.**
   Separate `source_lane`, `source_agent`, and `scope`.
   Do not collapse those into one enum.

3. **Freeze the authoring integration sentence.**

   > The Provocation Generator writes `SituationProvocationProposal` / `FixtureDeltaV1`. The authoring loop applies accepted proposals to build an updated fixture view. The generation pipeline combines that view with `CarryForwardStateV1` and `SituationLocalStateV1` to generate reactions.

4. **Add `observation_scope` and `automatic_aftermath` as optional proposal subfields.**
   That is the smallest Sabre-shaped improvement that actually changes semantics.

5. **Reserve `pursuit_ref` in proposal metadata or sidecars.**
   Do not make it part of the shared graph membrane yet.

6. **Make the proposal ledger explicit.**
   The curation surface should support `accept`, `reject`, `edit`, `merge`, and `freeze`, not just one-shot apply-or-ignore behavior.

#### 5. Priority

**Before the next provocation experiment:**

1. freeze `SituationProvocationProposal` vs `FixtureDeltaV1`
2. implement `ProvocationContextV1`
3. implement real `CarryForwardStateV1` + `SituationLocalStateV1` carry-remap flow
4. add append-only proposal ledger + validator + human-gate operations

**After the first successful provocation experiment, but not before:**

5. add optional `observation_scope` and `automatic_aftermath`
6. add `pursuit_ref` only if multi-step continuity is visibly failing

**Smallest success condition:**

After 2-3 normal authoring steps, apply one hand-authored or generated provocation proposal and compare against a no-delta control.
The seam has earned its keep only if:

- operator ranking changes, **or** retrieval changes
- at least one admitted candidate is **structurally new because of the proposal**, not just phrased differently
- graph projections remain valid

If the delta only changes wording, the seam is fake.
