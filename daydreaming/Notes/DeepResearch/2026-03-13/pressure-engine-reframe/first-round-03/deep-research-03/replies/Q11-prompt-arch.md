## Findings first

1. **`build_middle_prompt()` is split-brained.**
   It starts in the right mode - character, situation, memories, selected move - then ruins it with `REFERENCE STRUCTURED STATE` JSON. That makes the model do schema parsing right before scene writing. Wrong interface.

2. **The docs already specify the right prompt surface.**
   `34-broader-application-surface.md` says the model needs four things: what the character wants, what is in the way, what they are doing about it, and what just happened. `31-generation-experiment-review-checklist.md` says not to prescribe behavior. Some current operator text is still borderline because it says things like “show X as action,” which is prompt contamination, even if milder than before.

3. **Do not remove structure from the system. Remove raw structure from the prompt.**
   Keep `CausalSlice`, `AppraisalFrame`, `PracticeContext`, retrieval, and scoring as runtime/sidecar objects, exactly as `28-l2-schema-from-5pro.md`, `27-authoring-time-generation-reframe.md`, and `29-worked-trace-kai-unopened-letter.md` imply. But compile them into an actor-readable prompt view before generation. Raw JSON in the prompt is like handing the actor your ORM models.

4. **The current prompt is probably anchoring dominant-concern collapse.**
   It explicitly says “Dominant concern:” and then dumps the selected concern JSON. If you later complain that `pressure_tags[]` flatten to the dominant concern, that is partly your own fault. The prompt surface is telling the model what the center of gravity is.

5. **Expected gains are real but bounded.**
   Replacing the JSON dump with compiled given circumstances should improve action-under-pressure$*{80%}$ and operator distinctiveness$*{70%}$ because it gives the model a usable dramatic frame instead of a data blob. It may improve graph projection richness a bit$_{35%}$, especially if you render multiple active pressures, but it will **not** solve seam flattening by itself. Q6 still exists. `Q3-gen-quality-curation-rev.md` already says the bigger weaknesses are thin graph projection and weak scene-state progression, not total prose failure.

---

## MVP recommendation

**Build a prompt compiler, not a prettier prompt template.**

Concretely:

### 1. Add a `PromptViewV1` compile step

Do not keep extending `build_middle_prompt()` with more positional args. Add a small compiled view object between runtime state and prompt text.

```python
@dataclass
class PromptViewV1:
    situation_now: str
    pressure_mix: list[str]
    want: str
    obstacle: str
    current_move: str
    memory_pressure: list[str]
    immediate_constraint: str | None = None
```

Compile it from existing state:

* `CausalSlice.affected_goal` → `want`
* `AppraisalFrame` buckets → `obstacle` / urgency / control read
* `operator_family + PracticeContext + affordance_tags` → `current_move`
* retrieved episodes → `memory_pressure`
* all active concerns, ranked → `pressure_mix`
* `temporal_status / realization_status` → `immediate_constraint`

That is fully consistent with the current architecture. The middle layer stays structured; the prompt becomes a compiled surface.

### 2. Replace the JSON block with a given-circumstances block

The current `WHAT THE SYSTEM HAS DETERMINED` plus `REFERENCE STRUCTURED STATE` should become something like this:

```text
THE GIVEN CIRCUMSTANCES

Situation now:
Kai is alone in the kitchen. His sister's letter asking for a harbor meeting tonight is still unopened on the table.

Pressures in the room:
Attachment threat is strongest. Obligation is second. If he keeps delaying, silence may become the answer.

What Kai wants:
To keep open the possibility of repair without having to face the first true thing yet.

What is in the way:
The demand is real and near. Once he engages, he loses control of what the conversation becomes.

What move he is making:
Avoidance through delay-contact and ritual-distraction.

What memory is pressing on this moment:
- Last time silence itself became the message.
- The harbor already holds the last rupture.
```

Then the existing output schema can stay exactly as it is for the MVP.

### 3. Render multiple active pressures, not just the dominant one

This is the smallest prompt-level fix for thin projection. Right now `build_middle_prompt()` effectively tells the model: one concern matters, everything else is background noise. Pass the active concern stack, or at least the top 2 concerns, into the prompt compiler.

Not as JSON. As ranked pressure language:

* “Attachment threat is strongest.”
* “Obligation is present underneath it.”

That should make prose and projection less one-note without changing the seam.

### 4. Shrink operator semantics to one descriptive line

`build_operator_behavior_description()` should not lecture the model about what the output should look like. Keep the move, lose the directing notes.

Bad:

* “Show non-engagement as action, not absence.”

Better:

* “Current move: avoidance. He is delaying contact by keeping his hands busy with ordinary tasks.”

That is circumstance, not instruction.

### 5. Keep the output contract fixed for the experiment

Do **not** combine this with Q6’s generate-then-project pass yet. That would muddy the test. Keep the same output schema, same validation, same model, same fixtures. Only change the prompt surface.

---

## Why it works

Because the model is already good at writing from circumstances. It is worse at translating a mini state machine into embodied behavior in the same breath.

The current JSON-heavy prompt forces a useless detour:

1. parse runtime objects
2. infer the dramatic frame
3. write the beat
4. fill graph fields

The MVP precomputes step 2 for it. That is the whole point of having a middle layer in the first place.

It also matches the repo’s own discipline:

* thin graph seam
* rich sidecar / trace
* prompt conditioning as a separate compilation step

Right now you are treating sidecar state as prompt text. That is sloppy. Sidecar is for inspection. Prompt is for action.

`32-authoring-time-generation-comparison-memo.md` already established that removing overt behavioral leading did **not** break operator control. Good. That means the JSON dump is not the secret sauce. You can cut it without panicking.

---

## Smallest viable patch / experiment plan

1. **Add a `--prompt-style` switch**
   `json_dump` vs `given_circumstances`

2. **Implement a deterministic renderer first**
   No second LLM pass. No fancy rewrite layer. Just compile:

   * goal → plain English
   * controllability/likelihood → plain-English obstacle
   * operator/practice → plain-English current move
   * retrieval → one-line memory residues

3. **Run three fixtures**

   * Kai for avoidance
   * Rhea or Maren for rehearsal
   * Tessa for rationalization

4. **Hold everything else constant**

   * same provider/model
   * same candidates-per-step
   * same validation
   * same scoring path

5. **Judge on four things**

   * blind operator guess from prose
   * action-under-pressure vs indicating
   * pairwise repetition across selected nodes
   * projection richness: especially average `pressure_tags[]` count and whether secondary pressure survives

6. **Use human read first**
   Do not let `make_semantic_checks()` be the headline metric here. `Q3-gen-quality-curation-rev.md` already showed that the bottleneck is dramatic function and seam richness, not keyword smoke tests.

---

## Failure modes

1. **You overcompress the state.**
   If the compiler strips out temporal status, blame, or control, rehearsal and rationalization will blur together.

2. **The renderer becomes generic.**
   If every prompt says some version of “X wants Y but Z is in the way,” you’ll get a new template collapse instead of a better one. The compiler needs terse but specific phrasing.

3. **You keep too much raw structure.**
   If the new prompt still contains mini-JSON disguised as bullets, nothing meaningful changes.

4. **You overclaim graph gains.**
   Even with a better prompt, simultaneous generation+projection will still bias toward dominant-concern filling. If projection gets only slightly richer, that is normal.

5. **You forget that Kai already mostly works.**
   The biggest upside is not “suddenly good prose.” Kai already gives you decent behavioral pressure. The real value is cleaner operator texture on rehearsal/rationalization fixtures and less samey later-step output.

---

## Higher-octane design: reactive prompt

The MVP fixes prompt surface. The next step fixes **adjacent-beat continuity**.

### Recommendation

For step `N > 1`, add a runtime-only **reactive residue** object and feed that into the next prompt.

Something like:

```python
@dataclass
class PromptResidueV1:
    what_just_happened: str
    what_got_sharper: str
    what_still_has_not_happened: str
```

Then the next prompt gets:

```text
What just happened:
He turned the letter face down again and disappeared into kettle ritual.

What got sharper:
Delay now feels more deliberate, not accidental.

What still has not happened:
The letter is still unopened. No reply has been sent.
```

This is the smallest principled reactive design.

### Where it should come from

From **three inputs together**, not just prior prose:

1. accepted scene text from step `N-1`
2. updated concern intensities / selected commit effect
3. current retrieved memory

That matters because prior prose alone gives you continuity texture, but not interpretive update. Updated concerns alone give you numbers, but not lived momentum.

### How to implement it

Cheapest good version:

* after accepting a step, derive `PromptResidueV1`
* store it only in trace/runtime, **not** in the graph seam
* pass it into the next step’s prompt compiler

You can build it either:

* deterministically from `candidate_text + graph_projection + updated_concerns`, if you can get something usable
* or with one cheap summarization pass, if deterministic extraction is too brittle

I would try deterministic first for MVP and allow one cheap summarizer later only if the residue reads dead.

### Does this replace explicit scene-state carry-forward?

**No.**
It helps local continuity$_{75%}$, but it is not a state model.

Reactive prompting answers:

* what just happened in dramatic terms

It does **not** answer:

* what facts in the world changed
* what is now canonically true
* what later validation should use
* what preconditions/effects accumulate over multiple steps

So Q7 still stands. Reactive prompting is continuity glue, not situation calculus.

---

## Connection to adjacent questions

### Q7: scene-state progression

Reactive prompting should come **before** full situation-state machinery if the goal is a cheap gain in continuity. But it does not eliminate the need for state carry-forward. It just makes the next beat feel like it remembers the last one.

### Q8: retrieval self-priming

Be careful here. `build_generated_episode()` already writes accepted prose back into episodic memory. If you also dump the full prior accepted text into the next prompt, you will amplify self-echo.

So: **do not include full prior scene text verbatim.**
Use a distilled reactive residue for adjacency, and let retrieval handle longer-horizon memory.

### Q6: graph seam richness

Prompt rewrite may modestly help secondary pressure survive because the prompt surface no longer screams “dominant concern only.” But real seam-rich projection probably still wants generate-then-project later. Do not conflate the two experiments.

---

## What not to change yet

1. **Do not change the graph seam.**
   `21-graph-interface-contract.md` is not the problem here.

2. **Do not mix this with a two-pass projection experiment.**
   That is Q6. Keep this test clean.

3. **Do not hand-author circumstance blocks per node.**
   The whole point is compiling them from existing middle-layer state.

4. **Do not build a full situation calculus yet.**
   Prompt continuity and world-state logic are different layers.

5. **Do not put `EmotionVector` into the prompt.**
   `28-l2-schema-from-5pro.md` was right to keep it out.

6. **Do not keep the JSON dump “just in case.”**
   That defeats the experiment.

## Bottom line

The right move is a **compiled prompt view**:

* rich runtime state upstream
* actor-readable circumstances in the prompt
* unchanged seam on output

Then, if that works, add a **reactive residue** layer for sequence steps.

That is narrow, testable, and it attacks the actual flaw in `build_middle_prompt()` instead of decorating it.

Look away for a second. Unclench your jaw.
