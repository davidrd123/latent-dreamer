# Q2 Rationalization Benchmark Review

## Scope and standard for what counts as rationalization

The benchmark is trying to validate that **rationalization** is being selected and expressed as a distinct operator family, not as a mislabeled form of avoidance or rehearsal.

In entity["people","Erik Mueller","daydreamer author"]’s DAYDREAMER framing, *rationalization* is a daydreaming goal whose function is to **modify the interpretation of a previous personal goal failure in order to reduce the resulting negative emotional state**. fileciteturn10file2 That definition implies three constraints that matter for your Tessa case:

- **Past-directed anchor:** there has already been a failure or damage (the “thing that happened” must be treated as done, not merely looming). fileciteturn10file2  
- **Interpretation shift, not disengagement:** the cognitive move is “what it meant / why it was necessary / why it is not as bad / why blame is distributed,” rather than “do something else so I do not have to engage.” fileciteturn10file2  
- **Affective function:** it is an affect-regulation move under pressure (reducing shame, distress, guilt, anger, etc.), often connected to cognitive-dissonance reduction (the project notes cite entity["people","Leon Festinger","cognitive dissonance"]). fileciteturn10file2  

Mueller also breaks rationalization into strategy types (mixed blessing, hidden blessing, external attribution, minimization). The key point for your benchmark is not covering all four, but ensuring the generated moment clearly instantiates *at least one* of them in a way that is behaviorally legible. fileciteturn10file2  

## Findings

The headline: **Tessa is directionally the right fixture for rationalization**, and the current prototype **does produce a derived-path operator selection that is not prospectively “rehearsal-shaped.”** But the benchmark is still *too easy to “pass” for the wrong reasons*, and the current evaluation gates are not yet strong enough to prove that rationalization is being exercised as a structurally distinct move.

### The fixture represents the right kind of rationalization case

Your Tessa setup (entity["fictional_character","Tessa","latent dreamer benchmark"] after a public credit-cut toast, with entity["fictional_character","Eli","latent dreamer benchmark"] texting immediately) matches Mueller’s rationalization conditions: the core injury is **already done**, and the immediate problem is **what the act “means” and how she can live inside that meaning**. fileciteturn10file2

Even if the system later allows rationalization about anticipated failures, Mueller explicitly treats rationalization as fundamentally aimed at reducing negative affect from failure interpretation. That is exactly what “the toast cannot be unsaid” is engineered to force. fileciteturn10file2  

### The prototype’s derived state for Tessa is not secretly rehearsal-shaped

From the run artifacts you provided, the derived Tessa state is consistent with “aftermath/actual failure” rather than “threshold/prospective rehearsal”:

- A causal interpretation that treats the failure as **actual** (not prospective) and frames the affected goal as credibility/standing with the collaborator.  
- An appraisal profile that is **low controllability** and **high self-blame pressure** (negative praiseworthiness), which should favor rationalization over rehearsal.  
- A practice context that is “confession/aftermath” rather than “anticipated confrontation/threshold,” which should make “explanation turning into exoneration” legible.

Conceptually, that aligns with the rationalization goal being affect-regulatory and post hoc. fileciteturn10file2  

So, on the narrow question “is the system structurally biased toward prospective/rehearsal interpretation,” the answer is: **not in the Tessa derived path.** It is deriving an aftermath-shaped state and selecting rationalization consistently.

### Rationalization is behaviorally distinct from avoidance in the best Tessa moments, but the proof is still weak

The strongest behavioral distinction you are aiming for:

- **Avoidance** (compare entity["fictional_character","Kai","latent dreamer benchmark"]): non-engagement as displacement activity, often ritualized, keeping hands busy so contact is deferred.  
- **Rehearsal** (compare entity["fictional_character","Rhea","latent dreamer benchmark"]): pre-speaking, trial phrasing, simulated objections before the encounter.  
- **Rationalization** (Tessa): *language-editing and motive-editing as the action*, where the character actively constructs a “necessary / fair / not-that-bad / not-my-fault” reason that dulls shame and stabilizes self-image.

Your selected Tessa outputs do show that last pattern: typing a justification and deleting an apology-word from the draft is a concrete action that reads like “motive as cover,” not like “do something else.” That is a real rationalization beat in the Mueller sense (reinterpretation to relieve negative affect). fileciteturn10file2  

But the benchmark does **not yet prove** that this is coming from the middle layer rather than from prompt-leading and permissive scoring.

### The benchmark currently “passes” even when the strongest rationalization predicate fails

Your run summary shows a consistent pattern: *rationalization wins*, but the semantic predicate **“rationalization reframes rather than resolves the damage” fails on every selected step**.

This is not just cosmetic. It means the current evaluation is in an awkward in-between state:

- The generation is *good enough* to be recognizable as rationalization to a reader.
- The checker is *not* recognizing the reframing when it is expressed with different surface language.
- The compiler/admission logic is therefore optimizing against a proxy that does not tightly correspond to the construct you care about.

Practically, this makes the benchmark too vulnerable to: “the scene is labeled rationalization and sort of feels like it, but we are not actually enforcing the structural difference we want.”

### Two structural confounds are making Tessa less diagnostic than it should be

First confound: **operator-semantic text in the prompt is still doing behavioral work.**

Your own experimental checklist warns that prompts should not contain behavioral prescriptions that substitute for middle-layer causation. In the Tessa prompt, the rationalization operator description includes an instruction-like clause (“show the excuse attaching itself to concrete behavior”). That is mild compared to the avoidance description’s stronger imperatives, but it is still on the wrong side of the warning: it tells the model *how to manifest the operator*, instead of letting the middle-layer inputs cause the manifestation.

This does not invalidate the benchmark. It does weaken the causal attribution you want: was the behavior shaped by appraisal/practice/retrieval, or by the operator-description text?

Second confound: **graph option_effect is free to contradict the intended rationalization geometry, and the validator accepts it.**

One of your selected Tessa steps returns `option_effect: close`. In the harness, `close` maps to an ontic commit type. That creates a segment boundary and changes reappraisal dynamics. For a rationalization-after-failure beat where “the apology is not sent” is an explicit constraint, a `close` effect is usually semantically wrong or at least ambiguous: it suggests resolution or closure at the graph level while the prose is doing reframing and deferral.

Because the validator does not reject this mismatch, the multi-step trace can drift into “it looks like rationalization, but the graph says we closed it,” which makes the benchmark less diagnostic and can produce misleading downstream behavior in reappraisal and segmentation.

## Smallest viable patch plan

This is intentionally narrow: no new architecture, no embeddings, no refactor of the whole loop. The goal is to make Tessa *actually* test rationalization on the derived path, and to make failures informative.

### Tighten the benchmark so it cannot “pass” with graph-level contradictions

Implement a fixture-sensitive validation rule for Tessa (or, more generally, for `operator_family == rationalization` under “apology not sent” constraints):

- **Disallow `option_effect: close`** when the fixture’s state constraints say the apology or reply has not been sent.
- Preferably restrict to `{clarify, none}` for rationalization beats in aftermath confession contexts.

This one change removes a major confound: you stop letting the model claim “ontic closure” while the scene is still in the rationalization reframing loop.

Why this is minimal: it does not change prompting, scoring, or operator selection. It only prevents contradictory graph projections from being admitted.

### Make the rationalization semantic check structural instead of exemplar-token based

Right now, the strict rationalization predicate fails because it is keyed to a very specific lexical signature. That violates your own principle that checkers should test structure, not surface tokens.

Minimal upgrade: keep it heuristic, but move from “these exact phrases” to “these structural cues”:

- Require an explicit **justification move** (one of: causal connective like “because/so/in order to,” a necessity framing like “had to/needed to/the only way,” or a disavowal framing like “it wasn’t about/not the same as”).  
- Require explicit **acknowledgment of the already-done harm** (reference to the toast/credit cut, or a generic “what I did / what I said” marker).
- Keep the non-resolution guard: no sent apology, no forgiveness/resolution language.

This is still heuristic, but it becomes much harder for the benchmark to pass by accidentally matching one magic word, and much harder to fail just because the model used “needed” instead of “had to.”

Crucially, do **not** feed these expectations back into the prompt. Keep them purely in evaluation and validation.

### Reduce prompt-level behavioral prescription in operator descriptions

Rewrite operator-family descriptions to be definitional rather than imperative, so the prompt does not “teach” the output form.

For rationalization, aim for something like:

- “Rationalization is reinterpretation of an already-happened harm or failure in a way that reduces negative affect and protects self-image.”

Avoid “show X” phrasing. Your checklist calls out exactly this failure mode.

This change makes the benchmark more causal: if the output still looks like rationalization, you can attribute more of that to the derived middle layer.

### Make confession practice participate in episodic resonance explicitly

In your operator scoring, episodic resonance treats anything other than evasion and anticipated confrontation as a generic “else” bucket. That means confession-tagged episodes are not being recognized as confession; they get a default resonance pattern.

Minimal patch: add an explicit `elif episode_practice == "confession": ...` branch that boosts rationalization resonance relative to avoidance and rehearsal in a controlled way.

This helps in two ways:

- It makes the rationalization win less dependent on blunt practice-fit constants or bonuses.
- It makes ablations more meaningful: removing retrieval should now have a cleaner, measurable effect on the rationalization preference.

### Add one rationalization-specific ablation that proves the appraisal story

You already have ablations for no causal slice, controllability, and practice swap. For rationalization specifically, you need one that targets its distinctive trigger: blame/self-evaluation.

Add an ablation that sets praiseworthiness from “high self-blame” to “low self-blame” while holding the rest fixed. The predicted effect:

- Rationalization should drop.
- Avoidance may rise (if controllability remains low) or rehearsal may rise (if controllability rises).

This ablation is the cleanest proof that you are not just selecting rationalization because “confession practice says so,” but because the appraisal frame is actually doing work.

## What not to change yet

Do not broaden this into new machinery. Specifically, do not:

- Add more operators or expand the taxonomy. A rationalization benchmark that is not yet diagnostic will not be fixed by adding reversal or roving.  
- Add embeddings or retrieval refactors. The immediate weakness is evaluation and validation, not recall quality.  
- Redesign the seam contract or add new graph fields. You can fix the Tessa benchmark with stricter validation on existing fields.  
- Turn the prompt into a rationalization tutorial. The more you prescribe behavior in prompt text, the less you are testing the middle layer.

The tight path is: **make the Tessa benchmark harder to “pass” accidentally, and make its failures interpretable**, before you scale batch generation or add more operator families.
