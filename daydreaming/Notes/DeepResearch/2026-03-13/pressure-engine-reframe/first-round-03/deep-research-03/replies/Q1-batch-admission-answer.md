### Answer

#### Findings first

- The smallest viable batch pass is **not** a new subsystem. The current Python harness already has the right shape: per-step candidate bags and local reduction (`compile_candidate_set()`), multi-step accumulation (`run_middle_sequence()`), pooled batch mode (`run_middle_batch()`), and a first greedy pooled admission pass (`admit_candidate_pool()`).
- The missing work is mostly **policy and packaging**, not architecture: define the batch unit, make hard gates explicit, add an explicit overdetermination term at pooled admission, and emit a human curation packet that is small enough to review quickly.
- Batch admission should happen in **two reductions**:
  1. **local reduction per step**: 3-4 raw variants → 1 step winner  
  2. **pooled reduction per batch**: ~15 step winners → 6-8 shortlist nodes
- Human review should happen **after** greedy admission, not before. Reviewing all raw generations is wasted motion. Review the shortlist only.

#### Minimal architecture sketch

```text
fixture / character / active situation family
  → run 5 short sequences
  → each step generates 3-4 raw candidates
  → compile_candidate_set() picks 1 step winner
  → accepted-only writeback shapes later steps
  → pool ~15 accepted step winners
  → hard-gate invalid / contaminated / duplicate nodes
  → greedy pooled admission over the survivors
       reward:
         - coverage gain
         - distinctiveness
         - overdetermination
         - useful operator/practice spread
  → shortlist 6-8 nodes
  → human curation packet
  → keep / edit / reject
  → admitted graph nodes + archived provenance sidecars
```

This keeps the seam discipline intact:

- graph payload stays thin
- `CausalSlice`, `AppraisalFrame`, `PracticeContext`, retrieval details, and score breakdown stay in sidecars/traces
- only accepted nodes re-enter retrieval on the next step or next batch
- no solver, no embeddings, no kernel refactor, no `L3` rethink

#### 1. What the batch unit should be

Use a **character-pressure batch**, not a whole-world batch.

Smallest useful default:

- **one fixture / one character / one active situation family**
- **5 short sequences**
- **3 steps per sequence**
- **3-4 raw candidates per step**
- local compilation keeps **1 winner per step**
- pooled batch size becomes **up to 15 step winners**
- greedy admission keeps **6-8 shortlist nodes**

Why this unit:

- it matches the current harness shape
- it lands in the target range of roughly 15-25 candidates per batch
- it keeps duplicate detection meaningful
- it avoids mixing incompatible pressure regimes in one admission pass

Do **not** batch `Kai`, `Rhea`, and a future `Tessa` rationalization case together. Their duplicate relations are not meaningful across worlds.

#### 2. Hard gates

Hard gates should stay narrow and structural.

A candidate does not enter pooled admission unless all of these pass:

1. **Graph-valid projection**
   - `validate_graph_projection()` passes
   - required fields present
   - canonical vocabularies only
   - refs resolve mechanically

2. **Frozen seam compliance**
   - thin graph payload only
   - no appraisal / causal / practice internals leaking into the graph payload
   - provenance lives in the sidecar, not the graph node

3. **No cross-fixture contamination**
   - fail on foreign names / places / props from another benchmark world

4. **World-coherence for intended canonical nodes**
   - no contradiction with current canon for the active batch
   - no impossible state jump
   - no “magic resolution” that bypasses the fixture’s live situation

5. **No exact duplicates**
   - duplicate `node_id`
   - identical graph signature
   - exact text duplicate

6. **Structural semantic validity**
   - fixture-specific checks stay structural, not token-mirroring
   - example: “letter remains unopened,” “meeting has not begun,” “damage is not already repaired”

This is where `31-generation-experiment-review-checklist.md` matters: hard gates should test structure, not whether the candidate repeated expected surface tokens.

#### 3. Soft preferences

After hard gates, prefer candidates that improve the batch as a set.

Priority order:

1. **Coverage gain**
   - introduces new `setup_refs[]`
   - introduces new `payoff_refs[]`
   - adds a useful new path through the active situation

2. **Overdetermination**
   - serves more than one live pressure
   - or links a pressure node to an additional setup/payoff line
   - or gives traversal another way into already important material

3. **Distinctiveness**
   - not a paraphrase of already shortlisted nodes
   - not the same ritual beat with different nouns

4. **Operator / practice usefulness**
   - some spread is good
   - but do not force quotas when fit is bad
   - a batch full of avoidance should remain possible if avoidance is the real winner

5. **Curation keepability**
   - low edit burden
   - clear operator legibility
   - graph-useful residue
   - readable enough that a human would plausibly keep it

The current comparison memos already imply the right standard here: behavioral specificity, operator legibility, provenance fit, and graph usefulness beat token-level heuristic scoring.

#### 4. How near-duplicates should be handled

Use a simple **signature + overlap** policy. No embeddings yet.

Treat candidates as near-duplicates when they share:

- same `situation_id`
- same `selected_operator_family`
- same `origin_pressure_refs` or same dominant pressure
- same `practice_tags`
- same `option_effect`
- high text overlap or the same setup/payoff signature

Three cases:

**A. Same operator, same concern, same texture**
- keep only the highest-scoring exemplar
- reject the rest as duplicate supply

This is the common `Kai` failure mode: several kitchen-delay variants that all do the same job.

**B. Same operator, same concern, different texture**
- allow one second exemplar only if it changes:
  - setup/payoff coverage
  - commit type
  - or overdetermination value

Do not keep three stylistic variants of the same beat.

**C. Same concern, different operator**
- not a duplicate by default
- this contrast is often useful
- example: avoidance and rationalization around the same pressure may both deserve admission if they open different graph uses

Practical clustering key for now:

```text
cluster_key =
  situation_id
  + operator_family
  + sorted(origin_pressure_refs)
  + sorted(practice_tags)
  + option_effect
```

Then use simple token overlap / Jaccard-style text overlap as the texture check inside the cluster.

#### 5. How overdetermination should be scored across the batch

Overdetermination should be a **pooled admission reward**, not just a sidecar note.

A candidate gets extra value when it does more than one job:

- carries multiple live `pressure_tags`
- links a new setup to an already active payoff line, or vice versa
- creates an alternate ingress / egress around the same unresolved situation
- supports traversal density without being redundant

Smallest current-path scoring rule:

```text
pooled_score =
    0.30 * local_compiler_total
  + 0.20 * coverage_gain
  + 0.20 * distinctiveness
  + 0.15 * overdetermination_gain
  + 0.10 * operator_practice_gain
  + 0.05 * semantic_quality
```

Where:

- `local_compiler_total` = existing per-step compilation score
- `coverage_gain` = what this node adds that the admitted set does not already cover
- `distinctiveness` = distance from already admitted text / signature clusters
- `overdetermination_gain` = multiple pressures or multi-line closure value
- `operator_practice_gain` = small diversity bonus, never a quota override
- `semantic_quality` = structural semantic pass ratio

The key point: **compute this incrementally against the already admitted set**. Overdetermination is about package value, not isolated node quality.

#### 6. What the simplest greedy admission pass should look like

Use the current harness shape almost unchanged.

1. Run a batch with:
   - `run_middle_batch()`
   - `5 sequences`
   - `3 steps`
   - `3-4 candidates_per_step`

2. Keep the current local reduction:
   - `compile_candidate_set()` selects one winner per step

3. Pool only:
   - graph-valid
   - contamination-free
   - accepted step winners

4. Before admission, cluster obvious duplicates.

5. Run a **greedy pooled pass**:
   - score each remaining row against the current admitted set
   - admit the highest-scoring row
   - update coverage / distinctiveness / overdetermination state
   - rescore remaining rows
   - continue until:
     - `admit_max` reached, or
     - remaining rows add no meaningful gain

6. Default `admit_max`:
   - **6-8 nodes** from a pool of ~15

7. Emit:
   - shortlist
   - rejected list with reasons
   - coverage summary for the whole batch

That is enough before any solver exists.

#### 7. What the human curation packet should contain

The packet should be small, comparative, and sidecar-aware.

Recommended shape:

##### Batch summary header

- fixture / character / active situation
- sequence count, step count, candidates per step
- pool size
- shortlist size
- operator counts
- pressure/practice coverage
- setup/payoff coverage summary

##### One card per shortlisted node

For each shortlisted node, show:

- **candidate text**
- **graph projection summary**
  - `node_id`
  - `situation_id`
  - `option_effect`
  - `setup_refs`
  - `payoff_refs`
  - `pressure_tags`
  - `practice_tags`
- **provenance summary**
  - dominant concern
  - selected operator family
  - selected affordance tags
  - retrieved episode refs
  - commit type
- **admission score breakdown**
  - local compiler score
  - coverage gain
  - distinctiveness
  - overdetermination gain
- **duplicate cluster id**
- **why this survived**
  - one short sentence
- **curation action**
  - keep
  - keep with edit
  - reject

##### Rejected appendix

For rejected candidates, include only:

- `node_id`
- operator family
- rejection reason
  - duplicate
  - no coverage gain
  - weaker exemplar in same cluster
  - low semantic quality
  - canon issue

Do **not** dump full traces into the main packet. Link sidecar JSON only as appendix or drill-down. The main packet is for triage.

#### 8. Failure signals that justify a later soft-constraint admission compiler

Do not move to a solver because it sounds cleaner. Move only if the greedy pass demonstrably breaks.

Good failure signals:

1. **Humans repeatedly override the greedy shortlist for package-level reasons**
   - “these two medium candidates together are better than that one top scorer”
   - “we need this bridge node even though its local score is lower”

2. **Greedy admission leaves obvious setup/payoff holes**
   - locally strong nodes win
   - batch-level closure loses

3. **Duplicate suppression becomes brittle**
   - too many hand-tuned penalties
   - cluster rules start acting like hidden constraints

4. **Overdetermination requires coordinated subset choice**
   - not just row-by-row rewards
   - actual package optimization

5. **Batch size grows past the current regime**
   - more than ~25 pooled candidates
   - or more than ~8-10 intended admissions

6. **Curators cannot explain why the shortlist feels wrong except in global terms**
   - missing balance across active pressures
   - too many local maxima
   - not enough bridge nodes

That is the point to consider a soft-constraint admission compiler. Not earlier.

#### Concrete prototype order

1. **Freeze the batch unit**
   - 5 sequences
   - 3 steps
   - 3-4 candidates per step
   - `admit_max = 6-8`

2. **Keep the current local compiler**
   - do not reopen per-step generation yet

3. **Make pooled admission policy explicit**
   - hard gates
   - duplicate clustering
   - incremental scoring

4. **Add explicit overdetermination gain to pooled admission**
   - as a small scoring term, not a new subsystem

5. **Emit a real curation packet**
   - shortlist cards
   - rejected appendix
   - coverage summary

6. **Run human curation on the shortlist only**
   - keep / keep-with-edit / reject
   - record edit burden and reasons

7. **Use that review data as the next instrument**
   - keep-rate
   - edit burden
   - duplicate complaints
   - missing-coverage complaints

That is the smallest experiment that actually tests batch admission rather than just generating more text.

#### What not to build yet

Do **not** build any of this yet:

- embeddings for admission or duplicate detection
- a solver / ILP / MaxSAT admission layer
- a kernel refactor
- global cross-fixture batching
- aesthetic judge loops
- automatic prose scoring beyond the current structural smoke tests
- `L3`-aware admission logic beyond graph usefulness at the seam
- automatic canon repair search

Also do **not** change the current sequence mechanics yet:

- accepted-only writeback
- reappraisal path
- boundary detection
- per-step local reduction

Those are already good enough to support the next admission experiment.

