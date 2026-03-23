Bottom line:

1. **The regulation machinery should live at kernel level, not as situation metadata.**
2. **But the situation owns the inputs that drive it.**
3. **Do not make the phase label itself the primitive. Store continuous variables in character state and derive the phase label from them.**
4. **Tony’s central arc does not require a new top-level family first. It requires an explicit reappraisal pass that existing families can drive, especially RATIONALIZATION plus REHEARSAL, with REVERSAL as an optional exploratory aid.**

## 1) Regulation state machine: kernel-level or situation-level?

**Kernel-level.** More precisely: **character-level kernel state, updated from situation facts, and read by appraisal/control.**

That is the best fit to the literature. In Lazarus-style appraisal, an event is evaluated for relevance and then for coping potential/resources; those appraisals shape the resulting emotion, and coping/regulation strategies are then engaged to change either the person-environment relation or the felt emotion. In Scherer’s CPM and in EMA-style appraisal dynamics, appraisal is not a one-shot label stuck on a situation. It is a continuous process, and coping-related changes feed back into reappraisal. That makes Tony’s regulation variables properties of the **agent’s current state**, not properties of the scene. ([PMC][1])

The challenge/threat literature points the same way. Challenge and threat are not scene tags. They are dynamic states produced by evaluating **demands relative to perceived resources/control**, and they shift as those perceived resources shift. That is exactly why Tony can move from overwhelm to generative agency without the external stimulus becoming mild. ([PMC][2])

So I would place the Graffito regulation machinery like this:

* **Situation-level facts**: crowd density, noise/sensory intensity, observation pressure, rhythmic affordances, co-regulator presence, can-state, sketchbook availability, portal affordance, etc.
* **Kernel character state**: `sensory_load`, `entrainment`, `felt_agency`, `perceived_control`, maybe `self_coherence` or `self_compatibility`.
* **Derived label**: `overloaded | bracing | entraining | flowing | creating`
* **Appraisal output**: threat, challenge, shame, hope, curiosity, coping bias, etc.

So the regulation phase is **not** just another scalar like threat/hope sitting on the situation. It is closer to a cross-cycle organism state that the kernel should carry forward.

### What that means for scheduling

It should affect scheduling, but **not by becoming a raw family switch**.

Do **not** do:

`if mode == overloaded -> activate ROVING`

That is too stupid.

Do this instead:

1. Situation facts update regulation variables.
2. Appraisal reads situation facts + regulation variables.
3. Appraisal produces emotion deltas and coping/control deltas.
4. Family activation reads those outputs.

That keeps the architecture honest. Tony’s regulation state influences family activation because it changes appraisal, especially coping potential and challenge/threat balance, but it does not bypass appraisal and hard-dispatch families.

### The right primitive

Store the **continuous variables**, not the phase label, as source of truth.

So:

* `sensory_load`
* `entrainment`
* `felt_agency`
* `perceived_control`

Then derive the phase label for debugging, narration, and coarse gating.

Why? Because “bracing” and “entraining” are useful human labels, but they are not stable computational primitives. The real thing that matters is whether sensory demands are outrunning control, whether rhythmic coupling is rising, and whether the agent expects action to have coherent effect.

### Minimal design recommendation

For first implementation:

* Put the regulation vars in **character state**.
* Update them in the **appraisal pass** from typed situation facts.
* Let family activation consume them as **one input among several**.
* Keep the situation schema responsible for the environmental drivers only.

That is the clean answer.

## 2) Appraisal shift: does Graffito fit Mueller’s existing families?

Also bottom line:

**Do not add a new top-level family yet.**
Add an explicit **reappraisal mechanism** as a kernel process, and let existing families drive it in different ways.

The literature is clear that reappraisal means changing how one interprets a stimulus in order to change its affective impact. Ochsner and Gross describe it that way directly, and a 2023 study with **437 participants** found that **19% to 49%** of emotion change during reappraisal was statistically mediated by shifts along appraisal dimensions. That is not counterfactual world-editing. It is appraisal change over the same event. ([PMC][3])

That means Tony’s central arc is **not primarily REVERSAL**.

REVERSAL in Mueller is about alternative pasts, undoing causes, counterfactual branches. That can help Tony imagine overload moments differently, but it is not the core mechanism of “the same sensory intensity becomes the basis of art and power.”

The Graffito arc breaks down more cleanly like this:

### RATIONALIZATION handles the meaning shift

This is the closest existing family to:

* “maybe what is wrong with you is exactly what’s right with you”
* “crooked is not defect, it is signature”
* “sensory richness is not only vulnerability, it is also perceptual capacity”

That is classic meaning-level reframing. It changes self-compatibility, attribution, and norm significance.

### REHEARSAL handles the control shift

The other half of the arc is not semantic. It is embodied.

Tony learns:

* stop forcing
* entrain to rhythm
* move with Monk
* let action become coherent

That changes **coping potential**, **felt agency**, and **perceived control**. In appraisal terms, that is a secondary-appraisal shift. In challenge/threat terms, perceived resources start to meet demands. ([PMC][1])

So if you want the architecture to cash out Graffito, REHEARSAL cannot mean only “practice future dialogue.” It needs to include **embodied practice that raises control**.

### ROVING can help, but it is not the main arc

ROVING can provide a reset, distraction, or transition into a less overdetermined imaginative space. But by itself it does not explain the lasting shift from threat to challenge. It is relief, not transformation.

### REVERSAL is auxiliary here

REVERSAL is still useful for:

* counterfactual overload scenes
* “what if I had sensed the opening”
* “what if the same burst became a door”

But it is not the main family for the Graffito transformation.

## So what is the actual mechanism?

**Reappraisal should be a kernel-level pass, not a top-level family.**

That is the missing piece.

A good implementation story is:

1. Situation/event triggers appraisal.
2. Families operate.
3. Family outcomes modify appraisal-relevant state.
4. Kernel reruns appraisal on the **same live situation**.
5. Threat may become challenge, shame may become pride/curiosity, helplessness may become agency.

That gives you the right answer to your question:

* **Same stimulus, different appraisal** is not itself a family.
* It is a **reappraisal process** that families can cause.

### Concrete mapping for Graffito

* **RATIONALIZATION** changes meaning, attribution, self-compatibility.
* **REHEARSAL** changes coping potential, perceived control, felt agency.
* **ROVING** may temporarily reduce load or open imaginative space.
* **REVERSAL** explores counterfactual variants and can sometimes seed the later reappraisal.

So Tony’s core arc is best modeled as:

**RATIONALIZATION + REHEARSAL -> reappraisal shift**
not
**REVERSAL alone**

## What I’d implement

### For question 1

Put this in character/kernel state:

* `sensory_load`
* `entrainment`
* `felt_agency`
* `perceived_control`

Derive:

* `regulation_mode`

Have the appraisal step compute:

* threat/challenge balance
* shame/pride tilt
* coping bias

Then let family activation read that.

### For question 2

Do **not** add a new family yet. Instead:

* add explicit **reappraisal-after-family** logic
* let **RATIONALIZATION** update meaning variables
* let **REHEARSAL** update control/agency variables
* rerun appraisal on the current situation

If later you find that embodied regulation keeps doing work that REHEARSAL and RECOVERY cannot express cleanly, then add a **sub-operator** like `entrain` or `flow-induce` under REHEARSAL/RECOVERY before you touch the top-level family taxonomy.

That is the right order. New family now would be premature. The missing thing is the **cross-cutting reappraisal mechanism**.

Stretch your hands once before the next pass.

[1]: https://pmc.ncbi.nlm.nih.gov/articles/PMC2844958/?utm_source=chatgpt.com "Appraisals, emotions and emotion regulation: An integrative approach - PMC"
[2]: https://pmc.ncbi.nlm.nih.gov/articles/PMC6696903/?utm_source=chatgpt.com "Understanding Police Performance Under Stress: Insights From the Biopsychosocial Model of Challenge and Threat - PMC"
[3]: https://pmc.ncbi.nlm.nih.gov/articles/PMC4193464/?utm_source=chatgpt.com "Cognitive Reappraisal of Emotion: A Meta-Analysis of Human Neuroimaging Studies - PMC"
