### D6 response

#### 1. Direct answer

Yes, but not by flattening everything into the graph.

The current note's sentence "Our current implementation doesn't do this" is only half true. It is false for the legacy Mueller-shaped kernel and true for the newer authoring-time generation lane.

- In the kernel, hypothetical preservation already exists. Imaginary goals spawn fresh contexts in `goals.clj`; `goal_families.clj` copies and pseudo-sprouts alternative pasts for REVERSAL, and ROVING / RATIONALIZATION create side branches; `episodic_memory.clj` stores and reminds over prior episodes. That is already counterfactual retention.
- In the authoring-time generation prototype, accepted generated episodes feed back into sequence-local retrieval via `build_generated_episode()` and `run_middle_sequence()`, but rejected pool rows mostly die as admission artifacts. That lane does not yet preserve unrealized alternatives in a psychologically meaningful way.

So the right answer is:

1. Preserve unrealized alternatives as retrievable memory in lane-local state.
2. Preserve only a filtered subset of authoring-time near-misses.
3. Do not write raw hypothetical branches into the shared graph by default.

The state model already gives the right shape for this. `Context.modality` distinguishes `:rehearsal`, `:fantasy`, and `:counterfactual`. `CommitRecord.commit_type` already distinguishes world mutation from non-mutation. The missing rule is: `commit_type = none` should still be allowed to yield an `Episode`. "Not enacted" and "not remembered" are different.

The inner-life visualization does need an "almost happened / imagined" channel. Without it, the system collapses back toward traversal plus narration and gives up the main differentiator, namely showing the between-interaction cognition itself. But v1 should surface only the strongest active unrealized branch, not every discarded possibility.

The graph membrane remains intact if counterfactual material is treated as lane-local memory plus sidecar provenance, not graph canon. The authored graph may store only thin durable residue, such as stable refs or tags that a notable counterfactual sibling exists. Full context trees, live appraisal state, and reminder pools stay out of the shared graph.

Mueller is still the real pattern. Among the listed neighbors, MINSTREL is the best fit for authoring-time preservation of alternatives, Sabre is useful for belief-vs-world semantics, and ABL is useful for how to display active versus suspended execution state. Generative Agents is only a flat memory-stream baseline here.

#### 2. What to import

Minimum viable import:

1. Add an explicit counterfactual-memory admission rule.
   - L2/runtime: whenever an exploration branch with modality `:rehearsal`, `:fantasy`, or `:counterfactual` is abandoned, store an `Episode` even if world-state commit is `none`.
   - L1/authoring: preserve a rejected or superseded candidate only if it is structurally valid, dramatically distinct from the winner, and tied to an active concern/operator. Do not bank filler.

2. Extend `Episode` or add a thin sidecar with:
   - `modality`
   - `realization_status`: `imagined_only | abandoned | superseded | contradicted_by_canon`
   - `source_pressure_refs[]`
   - `source_operator`
   - `why_not_realized`
   - `sibling_of` or `superseded_by`

3. Feed preserved counterfactual episodes back into retrieval with their own indices.
   Retrieval should be able to surface "what this character almost did" alongside "what actually happened."

4. Add one dashboard channel for the strongest unrealized branch.
   Display it as ghosted, secondary, and explicitly modal. Use ABL-style inspectability for presentation, not ABL as the memory model.

5. Keep graph-visible residue thin.
   If a counterfactual needs to survive cross-run or cross-lane interpretation, write only a durable ref or tag through proposal/provenance channels. Do not dump branch trees into the graph.

This gives a clean two-tier model:
- lane-local counterfactual memory, automatic
- graph-visible counterfactual residue, curated and thin

#### 3. What to skip

- Do not preserve every rejected batch candidate from the generation lane. Most are prompt noise, not mind.
- Do not store live context trees, appraisal objects, reminder pools, or beam-search style alternatives in the shared graph. The graph contract explicitly forbids that.
- Do not treat authoring-time rejection as equivalent to character imagination. Some rejected candidates are tooling artifacts, not psychologically meaningful near-misses.
- Do not use a Generative Agents-style flat append-only memory stream as the main representation. It is too mushy for modality, provenance, and commit discipline.
- Do not make L3 traversal consume raw counterfactual trees. L3 should read thin residue, not lane-internal search state.

#### 4. What changes our architecture

This is a real architectural change, not a note-level wording fix.

1. The repo needs an explicit middle surface between "lane-local hypothetical branch" and "shared graph node":
   - `CounterfactualEpisodeV1` as an `Episode` extension or adjacent sidecar

2. The authoring-time generation lane needs filtered rejected-candidate retention.
   Right now it reuses accepted generated episodes for retrieval and mostly drops the rest. That is fine for supply, not fine for "watching a mind."

3. `CommitRecord` semantics need one clarification:
   - `none` means "not promoted into world canon"
   - it must not imply "discard the episode"

4. The dashboard becomes more than a narration surface.
   It needs to show:
   - current realized line
   - strongest active unrealized line
   - why the unrealized line stayed unrealized

5. The graph contract stays mostly the same.
   The only new graph-facing thing should be optional thin residue, probably via proposal refs or branch outcome tags, not a new branch ontology.

A minimal concrete split is:

```text
lane-local:
  Context tree
  appraisal
  reminder pools
  counterfactual episodes
  rejected / superseded candidate bank

shared graph:
  authored nodes
  stable tags
  proposal refs
  durable provenance only
```

#### 5. Priority

Needed before full watchable-runtime work, but not before the cheapest possible visual/audio run.

Order:

1. First, prove the watchable surface with a minimal ghost channel driven by existing kernel-side hypotheticals or a filtered authoring-time near-miss bank.
2. Next, add explicit `CounterfactualEpisodeV1` retention in the generation lane.
3. Only after that, decide whether any graph-level thin residue is worth adding.

So this is not "nice to have later." It is an early architectural correction. But the first implementation should be narrow:
- one preserved unrealized sibling per step or node
- explicit modality marker
- no raw context trees in the graph
- no attempt to preserve every rejected draft
