# Authoring-Time Generation Prototype Comparison Memo

Date: 2026-03-14

Purpose: record what the cleaned Gemini runs actually established about the authoring-time generation prototype, and separate strong findings from noisy measurement.

## Runs examined

Primary runs:
- `daydreaming/out/authoring_time_generation/20260314-181521`
- `daydreaming/out/authoring_time_generation/20260314-181851`

These runs should be treated as the first interpretable comparison set because they come after:
- structured-output enforcement for graph-compilable fields
- richer shared framing for both arms
- removal of the leading behavioral instruction from the middle prompt
- explicit `no_causal_slice`, `high_controllability`, and `swap_practice_context` ablations

## What is now established

### 1. Graph compilation works

In the cleaned Gemini runs, all arms produced graph-valid outputs under the frozen seam contract.

This matters because the prototype claim is not just "generate good prose." It is "generate candidate moments that compile into the graph seam with thin residue and sidecar provenance."

Status: established.

### 2. The middle layer affects operator behavior

Across the cleaned runs:
- default middle arm selects `avoidance`
- `high_controllability` flips to `rehearsal`
- `swap_practice_context` flips to `rehearsal`

This is the clearest positive result in the whole prototype.

It shows that changes in interpreted situation structure alter operator choice in a stable, intelligible way. That is exactly what the middle layer was supposed to buy: not just better labels, but different generative control.

Status: established.

### 3. The comparison is no longer obviously contaminated by prompt leading

The earlier middle prompt included a direct behavioral prescription telling the model to make the selected operator visible in behavior. That instruction was removed.

After removal:
- the middle arm still produces avoidance-shaped scenes
- the ablation flips still hold
- the experiment is materially cleaner

This does not prove there are no remaining contamination risks, but it removes the most direct one.

Status: established.

### 4. The middle arm produces psychologically legible material under contract pressure

Representative middle-arm output remains specific and structurally aligned even after the leading instruction was removed. The generated scenes reliably:
- keep the letter unopened.
- keep the harbor psychologically active
- realize evasion through delay ritual rather than abstract mood reporting
- remain graph-compilable

Status: established.

## What is directionally positive but not fully settled

### 5. `CausalSlice` appears to contribute something real

The first clean post-leading run (`20260314-181521`) showed meaningful separation between `middle` and `middle (no_causal_slice)`:
- full middle passed all semantic checks
- `no_causal_slice` lost some quality signals while still selecting `avoidance`

That is the pattern we wanted:
- practice context and retrieval are enough to keep the broad operator family in place
- explicit causal interpretation improves the specificity and fit of the generated moment

However, the next rerun (`20260314-181851`) became noisier again because the token-based semantic checker is still imperfect. In that later run, `no_causal_slice` passed all checks, which means the current checker is no longer a reliable instrument for isolating the `CausalSlice` contribution.

So the honest claim is:
- `CausalSlice` contribution is directionally supported
- but not conclusively measured by the current automatic checker

Status: promising, not fully settled.

## What the current checker can and cannot tell us

The semantic checker was useful for fast iteration, especially while fixing obvious issues like:
- invented refs
- prompt contamination
- brittle unopened-letter detection

But it has reached its limit as an evaluation instrument.

Why:
- it still relies on surface textual proxies
- different valid delay rituals can evade the heuristic
- different valid unopened-letter phrasings still produce false negatives
- it cannot reliably separate baseline vs middle vs `no_causal_slice` quality at the level we now care about

Conclusion: the checker is now good enough for smoke tests, but not strong enough for the main claim.

## Current bottom line

The prototype has already passed its own core bar from `30-authoring-time-generation-prototype-spec.md` in the following sense:
- the system generates graph-valid candidate moments
- middle-layer state affects operator choice in stable ways
- ablations produce intelligible control shifts
- the cleaned prompt structure does not need an explicit behavioral instruction to make the operator show up in scene behavior

That is enough to treat the authoring-time generation direction as viable.

What is not yet fully proven is the stronger claim that `CausalSlice` specifically produces a robust, measurable quality lift over a coarser concern+situation path. The current evidence points that way, but the measurement layer is not yet strong enough to make that the headline result.

## Recommended next step

Do not spend more time tuning the token checker.

Instead, write the result up as:
- graph-valid generation under seam constraints: yes
- operator-control sensitivity to appraisal and practice: yes
- `CausalSlice` incremental value: plausible, needs better evaluation than the current heuristic checker

Then, if stronger evidence is desired, run a small human-judged comparison using the current best harness:
- baseline vs middle vs `no_causal_slice`
- 3-5 seeded runs
- rubric: behavioral specificity, operator legibility, provenance fit, and graph usefulness

## Suggested interpretation for the project

This result is strong enough to justify the architectural reframe in `27-authoring-time-generation-reframe.md`.

The bottleneck really is material supply, and the prototype demonstrates that structured authoring-time generation is a better bet than returning immediately to either:
- more scheduler theorizing
- or a broad, premature runtime `L2` refactor

That does not mean `L2` runtime work disappears. It means the authoring-time path has now earned the right to be the critical next lane.
