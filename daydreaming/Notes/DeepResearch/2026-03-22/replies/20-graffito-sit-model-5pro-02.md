Here’s the clean answer.

## 1. Where the regulation state machine lives

It should live at **L2 character-state level**, not as situation metadata, and not as a free-floating top-level kernel knob.

More precisely: regulation is part of the character’s **appraised relation to the environment**. Lazarus/Smith put coping potential and future expectancy inside secondary appraisal, not outside it. Scherer’s CPM likewise treats coping potential as one of the main appraisal objectives. EMA makes the same move computationally: appraisal runs over the agent’s current causal interpretation of the person–environment relation, and coping changes that interpretation, which then gets re-appraised. 

So the right split is:

`situation facts + relation facts + artifact facts + body/rhythm cues`
→ `appraisal pass`
→ `character regulation state`
→ `concern strengths + coping bias`
→ `family activation`

That means **the situation supplies inputs**, but the resulting regulation mode belongs to the character. Same street, same siren, same mural, same Can: Tony can be `:overloaded` while Monk is `:entraining` or `:creating`. If you store the mode on the situation, you erase that asymmetry and make regulation look like weather. That is wrong.

So I’d put something like this in L2 state, beside concerns/emotions/readiness, not beside global `:mode` in `control.clj`:

```clj
:regulation
{:mode :overloaded | :bracing | :entraining | :flowing | :creating | :depleted
 :sensory-load 0.0-1.0
 :cue-coherence 0.0-1.0
 :motor-entrainment 0.0-1.0
 :perceived-control 0.0-1.0
 :felt-agency 0.0-1.0
 :co-regulator #{:Monk :Grandma}
 :expressive-channel-open? true/false}
```

And then make the appraisal pass update it:

```clj
(appraise event character-state situation-facts)
=> {:concern-deltas [...]
    :regulation-delta {...}}
```

The scheduler should read it, but **indirectly**. Do not let `:regulation/mode` become a second global chooser that competes with emotional motivation. Mueller’s control loop is still emotion-driven. The better move is: regulation modulates concern intensity and coping bias, which then modulate family candidate strength. That preserves the architecture instead of smearing it. EMA points exactly this way: appraisal yields an affective summary, and coping acts as a gatekeeper over what kind of response is sanctioned next. ([Institute for Creative Technologies][1])

So, in one sentence:

**kernel-level, yes — but only as per-character L2 appraisal state; not as scene metadata, and not as a new top-level control variable.**

Concrete consequence for Graffito:

* `:overloaded + low perceived-control + high threat` should strengthen ROVING / REVERSAL candidates.
* `:bracing or :entraining + rising perceived-control + imminent task` should strengthen REHEARSAL.
* `:flowing + expressive-channel-open?` should reduce threat appraisal and increase action readiness.

That is the right place to wire it.

## 2. Does Tony’s appraisal shift fit existing Mueller families?

**No new family is needed, but a new mechanism is.**

The mechanism is **explicit reappraisal**.

You should not try to cram the whole arc into one family label. The families are coping/daydreaming goal types. The shift from “same stimulus, terrifying” to “same stimulus, usable” is an **appraisal update** over unchanged raw input. Challenge/threat work in exactly that space: the same performance situation can read as challenge or threat depending on appraisals of resources, control, and demands. Lazarus-style secondary appraisal and later challenge-threat work both say the split turns on coping/resource appraisal, not on the stimulus changing. ([PMC][2])

So for Graffito:

* **REVERSAL** is not the main fit. REVERSAL imagines a different past or different avoided outcome. That is useful for “what if the cab hadn’t nearly hit me?” or “what if I could turn this corner differently?” But it is not the main mechanism of “the same overload becomes power.”
* **RATIONALIZATION** is the closest fit for the **meaning shift**: “crookedness is not defect but gift,” “what’s wrong is what’s right.” That is a reinterpretation of significance.
* **REHEARSAL** is the closest fit for the **control shift**: practicing the routine, entraining to rhythm, learning the body-policy that makes the Can usable.

The central turn is produced by **RATIONALIZATION + REHEARSAL changing later appraisal**, not by REVERSAL. EMA again is the clean model: coping changes the causal interpretation, and appraisal updates automatically from the changed interpretation. ([Institute for Creative Technologies][1])

So I’d implement it like this:

1. **RATIONALIZATION family** can change appraisal-relevant meaning variables:

   * self-concept threat ↓
   * shame/defect framing ↓
   * positive significance of trait ↑

2. **REHEARSAL family** can change appraisal-relevant control variables:

   * motor entrainment ↑
   * perceived control ↑
   * future expectancy ↑
   * expressive channel openness ↑

3. After either one commits, run **reappraisal** on the same active situation.

4. That reappraisal can flip:

   * `threat → challenge`
   * `panic → determination`
   * `overload + low agency → overload + usable agency`

Scherer explicitly allows sharp emotional shifts from relatively small changes in coping potential/control, and the challenge-threat literature says the same thing in a more performance-oriented idiom. ([PMC][3])

So the design answer is:

**Do not add a new top-level family yet. Add an appraisal/reappraisal layer around the existing families.**

If you skip that layer, you’ll end up abusing RATIONALIZATION to do everything:
semantic reframe, bodily entrainment, control acquisition, and moment-to-moment reinterpretation. That’s bad design. It hides four distinct moves under one label.

The clean decomposition is:

* **RATIONALIZATION** = reinterpret meaning
* **REHEARSAL** = build embodied control
* **REVERSAL** = explore alternative avoided outcomes
* **REAPPRAISAL mechanism** = recompute appraisal on the same stimulus after those changes

That’s the architecture I’d bet on$_{90%}$.

Bottom line: Tony’s arc does **not** demand a new family taxonomy. It **does** demand that families stop being the only mental machinery in town. You need an explicit appraisal pass, a regulation state in L2 character state, and automatic reappraisal after coping. That is the missing piece.

[1]: https://people.ict.usc.edu/~gratch/CSCI534/Readings/COGSYS-RS-EMOTION-2008-6.pdf "EMA: A process model of appraisal dynamics"
[2]: https://pmc.ncbi.nlm.nih.gov/articles/PMC6550483/ "
            Capitalizing on Appraisal Processes to Improve Affective Responses to Social Stress - PMC
        "
[3]: https://pmc.ncbi.nlm.nih.gov/articles/PMC2781886/ "
            Emotions are emergent processes: they require a dynamic computational architecture - PMC
        "
