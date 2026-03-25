# Graffito: What The Next Cross-Family Pairing Should Be

You have the full repo context. Do NOT summarize the repo back to us.
Bring outside knowledge: name specific systems, papers, mechanisms, or
design patterns that bear directly on this question.

This prompt is intentionally narrow. The question is no longer whether
the membrane works, whether reappraisal works, or whether Graffito can
produce *a* cross-family promotion path. Those are now established. The
open design question is: **what should the next cross-family family
pairing be in the Graffito miniworld?**

## Current kernel state

The benchmark ladder has moved further than earlier prompts assumed:

- Assay A passed
- Fixed-chain promotion/access proof passed
- Assay B passed
- Graffito Slice 1 passed
- Graffito Slice 2 passed
- Graffito Slice 3 passed
- the first autonomous Graffito miniworld passed

Current live code:

- `kernel/src/daydreamer/benchmarks/graffito_miniworld.clj`
- `kernel/test/daydreamer/benchmarks/graffito_miniworld_test.clj`

Current miniworld shape:

- three situations:
  - street overload
  - apartment support / rehearsal
  - mural crisis
- Tony carries transient organism state:
  - `:sensory-load`
  - `:entrainment`
  - `:felt-agency`
  - `:perceived-control`
- reappraisal runs every cycle
- family execution updates Tony state and object phase
- learned capability is **not** stored as sticky Tony-state floats;
  durable change is supposed to route through the memory membrane

Current 20-cycle deterministic baseline:

- `:rationalization 10`
- `:reversal 6`
- `:rehearsal 4`
- `reappraisal-flips = 5`
- `challenge-mural-cycles = 8`
- `stored-episode-count = 16`
- `dynamic-source-candidate-cycles = 7`
- `dynamic-source-win-cycles = 7`
- `cross-family-source-candidate-cycles = 4`
- `cross-family-source-win-cycles = 4`
- `episodes-with-cross-family-use-history = 2`
- `episodes-with-promotion-history = 2`
- `durable-episode-count = 2`
- `frontier-bridge-cycles = 1`
- `rule-access-transition-count = 1`

What those numbers now mean:

- cycle 5 is a real frontier-path mural rationalization triggered by
  `:reversal_aftershock_rationalization_frame`
- cycles 6 and 9 reuse that frontier-path rationalization episode from
  apartment rehearsal
- at cycle 9 that episode promotes and opens
  `:goal-family/reversal-aftershock-to-rationalization` from
  `:frontier` to `:accessible`
- cycles 13 and 17 then reuse a second, non-frontier rationalization
  episode from apartment rehearsal
- so the miniworld now has **two promoted cross-family paths**, but
  they still share the same family pairing:
  `rationalization -> rehearsal`

## The actual question

The next honest step is **not** “more promoted episodes of the same
shape.” It is:

**What should the next cross-family family pairing be after
`rationalization -> rehearsal`, if we want the Graffito miniworld to
grow in a psychologically honest way without benchmark-local gimmicks?**

We want guidance on the *next consumer family* or bridge shape, not a
broad ontology pass.

## Constraints

Please reason under these explicit constraints:

- no new top-level family unless you think that is overwhelmingly
  necessary
- no broad ontology expansion first
- no LLM-backed evaluation first
- no “just make it bigger” answer
- no sticky learned-capability character floats
- durable change should still route through:
  stored episode -> later reuse -> outcome evidence -> promotion ->
  access change
- prefer the smallest benchmark-local or kernel-local move that tests a
  genuinely new pairing

## Candidate directions we want you to compare

Please compare at least these possibilities:

1. `rationalization -> reversal`
2. `reversal -> rehearsal`
3. `reversal -> rationalization` in a second distinct Graffito form,
   not just the current aftershock-to-mural frame
4. some other pairing you think is better, if you can justify it

For each candidate pairing, answer:

- Is it psychologically honest on Graffito material?
- What concrete stored source would be reused later?
- What later family would consume it?
- Would the resulting use more likely be:
  - same-family loop defense
  - cross-family success evidence
  - merely another benchmark-local trick
- Does it fit the current membrane semantics cleanly?
- Would it likely broaden the miniworld’s behavior, or just restate the
  same regulation story in another form?

## The deeper design question

We now have strong evidence for this mechanism:

- `rationalization` changes meaning
- `rehearsal` changes embodied control
- later reread changes family/operator selection

What *other* cross-family dependency in a psychologically rich creative
mind is the next most plausible analogue?

Examples of the kind of answer we want:

- a reversal episode can later serve as a rehearsal seed because it
  identifies the exact point of motor breakdown to practice against
- a rehearsal episode can later serve reversal because embodied control
  changes which counterfactual becomes available
- a rationalization episode can later serve reversal because a new
  meaning frame changes which “what if I had…” branch even makes sense

But do not be limited to those examples if you think a different bridge
is better.

## What I need

Please answer in this structure:

1. **Best next pairing**
   - name the family pairing
   - why it is the best next step after `rationalization -> rehearsal`

2. **Why not the main alternatives**
   - briefly reject or postpone the other leading options

3. **Minimum benchmark implementation**
   - what exact stored episode should exist first
   - what later family should consume it
   - what the trigger / compatibility condition should be
   - whether the change should be benchmark-local first or kernel-level
     immediately

4. **Success signature**
   - what counters / trace events should move if this pairing is
     working
   - what would count as a false positive

5. **Relevant prior art**
   - specific systems, papers, or literatures that support your choice

Please optimize for the next buildable step, not for breadth.
