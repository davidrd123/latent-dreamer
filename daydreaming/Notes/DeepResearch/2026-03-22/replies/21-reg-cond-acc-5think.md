**Graffito regulation memo**

Bottom line: the next slice should prove one thing and one thing only. **A family execution changes Tony’s persistent regulation state, the same mural inputs are reappraised, and that rereading changes the later family or operator choice.** Do not add a new global regulation family. Do not keep regulation as scene-local facts. Do not let regulation hard-switch the scheduler. Appraisal theory, challenge/threat work, and EMA all point the same way: coping potential and control belong inside appraisal, and reappraisal is a rereading of the same event, not a second chooser layered on top of it. ([Stacy Marsella][1])

## 1. Where regulation should enter the current loop

### Strongest outside-knowledge conclusion

**Regulation should enter as per-character state that shapes appraisal, not as a direct family selector.**

The causal order should be:

```text
situation facts
→ update Tony state
→ appraise situation through Tony state
→ emit emotion / concern deltas
→ activate family goals
→ execute family
→ apply Tony-state delta
→ reappraise same situation
→ next activation / selection
```

The primary locus is **appraisal**, especially coping potential, perceived control, and self-compatibility. That is where the “same cue, different reading” lives. Challenge/threat work defines challenge as demands being met or exceeded by perceived resources, and threat as demands exceeding perceived resources. Reappraisal research says emotion change during reappraisal is substantially mediated by appraisal shifts themselves, not just output damping. EMA’s computational claim is also the right one here: appraisal is recurrent over the agent’s interpreted relation to the environment, and the dynamics come from changes in that interpretation. ([PMC][2])

So the answer to the sub-question “what should regulation change?” is:

* **first:** event appraisal
* **second:** emotion generation and persistence
* **third:** retrieval accessibility and family weighting, but indirectly through appraisal outputs
* **not first:** direct family availability switching

Pure suppression is the wrong model. If Tony is still reading the same mural cue as unmanageable threat, but you just numerically damp fear, the kernel is lying about what changed. The core Graffito move is a changed reading of the same intensity, followed by changed emotion and changed behavior. ([PMC][3])

### What that implies for this repo right now

From the pasted code, the current loop has a hole in exactly the right place:

* `control/run-cycle` decays, optionally calls `activate-family-goals`, then selects the strongest goal.
* there is **no explicit appraisal pass**
* there is **no persistent Tony state**
* current “reappraisal” is thin: rationalization can reduce a linked negative emotion and inject hope, then emit `:family-affect-state`, but the kernel does not actually reread the same situation through updated Tony state

That means the next insertion point is not “a new regulation family.” It is a **small appraisal projection stage** before `activate-family-goals`, plus a **post-family reappraisal stage** after family effects settle.

Concretely, keep the current `:family-affect-state` bridge facts. They are useful. But stop treating them as enough. They should become **side-effects of a real reread**, not substitutes for one.

### Smallest next implementable slice

Add two tiny stages, not a new planner:

1. `project-tony-state-from-situation`
2. `project-appraisal-from-situation+tony-state`

Run them:

* once before family activation
* once after family execution updates Tony state

That is enough to let the same mural facts produce a different appraisal summary on the second pass.

### Sub-question 1.5: Character-state ownership and persistence

#### Minimum persistent `:character-state`

For v1, Tony only:

```clj
:character-state
{:tony {:sensory-load ...
        :entrainment ...
        :felt-agency ...
        :perceived-control ...}}
```

That is the minimum. I would **derive** `:regulation-mode` from those four values for debugging only.

What should persist:

* `:sensory-load`
* `:entrainment`
* `:felt-agency`
* `:perceived-control`

What should be recomputed from the current situation each cycle:

* raw sensory hits
* crowd / surveillance / exposure
* rhythmic affordances
* co-regulator presence
* object phase / affordance visibility

I would **not** add `:cue-coherence` yet as stored state. It is useful, but you can derive it for now from input structure plus entrainment/control.

#### Tony only, or Tony + Grandma + Monk?

**Tony only in v1.**

That is the right simplification. Grandma and Monk matter immediately, but as **situation inputs and support affordances**, not as full parallel regulation models. Multi-character appraisal state buys you dyadic regulation and conflict modeling, but it also explodes bookkeeping, trace volume, and selection ambiguity. You do not need that to prove the Graffito mechanism.

So:

* Tony gets persistent state
* Monk and Grandma stay as typed presence / relation / co-regulation facts
* later, if you want real dyadic coupling, add a reduced `:character-state` for them

#### What decays, what accumulates?

Do **not** use one variable for both transient regulation and learned capability.

Transient, decays over cycles:

* sensory load
* entrainment
* felt agency
* perceived control

Accumulated, slower and should not be stored in the same map:

* learned rhythm routine
* Can attunement
* Sketchbook regulation skill
* self-compatibility prior (“crookedness can be used”)

The cleanest place for accumulation in this repo is **not** a sticky Tony-state float. It is the existing memory membrane:

* rehearsal or support episodes get stored
* repeated successful reuse promotes them
* promotion opens frontier rules or lower-threshold retrieval
* only later do you crystallize that into explicit learned capability facts

That matches the membrane assays much better than inventing a vague “agency trait.”

#### When to rerun appraisal?

**After effect application and Tony-state update, once branch-local consequences settle, but before the next family activation pass.**

Not after episode storage as the main trigger. Immediate reread should come from the changed state and changed local facts, not from self-retrieval of the just-written episode. Otherwise you blur immediate regulation with later memory reuse.

So the order should be:

```text
family executes
→ branch facts asserted / Tony state updated
→ reappraise live situation
→ then store / reconcile episode / promotion
```

## 2. Minimum next Graffito representation

### Strongest outside-knowledge conclusion

You do **not** need a full ontology next. You need one persistent state surface and three compact representational additions:

1. persistent Tony state
2. raw sensorimotor input / affordance facts
3. person-object relations
4. cross-layer correspondence facts
5. minimal phase state for Can and Mural

The right first situation set is still **street overload → apartment support → mural reread**.

### What that implies for this repo right now

The current microfixture already proves:

* typed facts can activate families
* authored rationalization / reversal anchors work
* retrieval over shared typed facts works

So the next representational step should be biased toward **what the current kernel can already use**:

* indexable typed facts
* a very small persistent world map addition
* one projection function
* one reread test

Do **not** expand into a mythic third world yet. Do **not** build a giant object ontology. Do **not** collapse Grandma and Motherload into one entity.

### Smallest next implementable slice

#### 2.1 Tony state on the world map

```clj
:character-state
{:tony {:sensory-load ...
        :entrainment ...
        :felt-agency ...
        :perceived-control ...}}
```

#### 2.2 Smallest useful schema for regulation-relevant scene facts

The test spec you pasted is right to keep the mural’s regulation-relevant content as **raw inputs**, not stored regulation labels. So I would not actually use `:sensorimotor-regulation` as the main scene primitive. I would use:

```clj
{:fact/type :sensorimotor-input
 :fact/id ...
 :modality :visual | :auditory | :social | :motor
 :polarity :load | :support
 :targets [:sensory-load :entrainment :felt-agency :perceived-control]
 :magnitude 0.0-1.0}
```

Examples:

* `:light_jolt_floods_attention`
* `:siren_pulse_hits_body`
* `:noise_fragments_precision`
* `:monk_moves_tony_into_rhythm`
* `:sketchbook_compresses_the_scene`

That is enough. The projection function turns these into Tony deltas.

#### 2.3 Smallest useful schema for cross-layer correspondence

```clj
{:fact/type :cross-layer-correspondence
 :fact/id ...
 :from-id ...
 :to-id ...
 :relation :counterpart-of | :echo-of | :reframe-force-of
 :registers [:baseline :magic]
 :shared-tensions [:protection :boundary :crooked-wisdom]}
```

That keeps counterpart structure explicit without pretending the myth/magic layer is a full independent simulated world yet.

#### 2.4 Facts vs object-state maps

Use **ordinary typed facts** for relations and meanings.
Use **dedicated object maps** for phaseful objects.

That means:

* “Tony trusts the Can as lineage” is a relation fact
* “Can is currently `:unstable`” is object state
* “Mural is `:surface` vs `:threshold` vs `:portal`” is object state

Minimal object map:

```clj
:objects
{:can   {:phase :inert | :unstable | :world-making
         :requires-mode #{:entraining :flowing :creating}}
 :mural {:phase :surface | :threshold | :portal}
 :sketchbook {:phase :available}
 :elephant {:phase :comfort-object}}
```

For the next slice, only **Can** and **Mural** really need phase logic. Sketchbook can stay a fact-plus-relation object. Elephant can wait.

#### 2.5 Minimum person-object relation vocabulary

This is the minimum set that actually makes Graffito different from generic artifact-state:

* `:lineage`
* `:regulates-with`
* `:attuned-to`
* `:legitimized-by`
* `:corresponds-to`

That gives you:

* Tony ↔ Can: lineage, attunement, correspondence
* Tony ↔ Sketchbook: regulates-with
* Monk ↔ Can: legitimized-by / lineage
* Tony ↔ Mural: threshold relation

#### 2.6 Smallest benchmark situation set

Still:

1. **street-overload**
2. **apartment-support**
3. **mural-crisis**

That is the first honest mechanistic run.

Street gives you the original overload.
Apartment changes Tony.
Mural proves same-cue reread.

Motherload can wait.

## 3. Rehearsal as regulation, not generic future planning

### Strongest outside-knowledge conclusion

For Graffito, **rehearsal should mean embodied control rehearsal**.

Not “Tony imagines succeeding later.”
Not “Tony plans future dialogue.”
Not generic future planning.

It should mean:

* practicing a regulation routine
* tightening the mapping between action and effect
* increasing entrainment and felt agency
* making the Can or mural affordance computationally usable

The closest external precedent is not classical planner rehearsal. It is the overlap between motor imagery / mental practice, rhythmic entrainment, and flow. Mental practice is reliably better than doing nothing for performance, even if weaker than physical practice; the flow literature’s strongest computational proposal treats flow as a tighter means-to-ends coupling; and rhythm-and-movement interventions show small but real regulation-related benefits, which is enough to justify entrainment as a first-class variable here. ([PMC][4])

### What that implies for this repo right now

Do **not** overload rationalization to do embodied control.

Keep the split clean:

* **RATIONALIZATION** changes meaning
* **REHEARSAL** changes control
* **REAPPRAISAL** rereads the same situation after either change

That means rehearsal should directly change:

* `:entrainment`
* `:felt-agency`
* `:perceived-control`

Optionally, second-order:

* cue coherence
* object affordance availability
* later retrieval weighting through stored episodes

The cleanest behavioral consequence is still:
**rehearsal changes later appraisal of the same overload cue.**

That is the first thing to prove.

Second-order consequences can come later:

* different reversal counterfactual selected
* different rationalization frame selected
* Can affordance unlocked

### Smallest next implementable slice

Do not build a full rehearsal planner yet. Build **one authored rehearsal routine**.

Something like:

```clj
{:fact/type :regulation-routine
 :fact/id :monk_pata_pata_routine
 :preconditions #{:monk_present :rhythm_available}
 :state-delta {:entrainment +...
               :felt-agency +...
               :perceived-control +...}}
```

Then let a minimal rehearsal executor:

1. assert the routine in apartment support
2. apply the Tony-state delta
3. store an episode with overlap to later mural cues

That is enough to prove rehearsal-as-regulation without building a big search space.

### Short-horizon improvement vs learned skill

The right split is:

* **one rehearsal cycle:** transient Tony-state improvement
* **repeated successful rehearsal cycles across contexts:** promoted durable episode, then maybe later explicit capability

So repeated rehearsal becomes accumulated capability when:

* the rehearsal episode is reused successfully across more than one relevant situation
* admission / promotion moves it from provisional toward durable
* a frontier rule or learned capability fact becomes available

That is exactly where your membrane machinery should enter. Use it.

## 4. Exact mechanistic success signature

### Strongest outside-knowledge conclusion

A good trace should show **three distinct things**:

1. **typed retrieval happened**
2. **regulation changed appraisal**
3. **the changed appraisal changed behavior**

Those are not the same claim.

Reappraisal literature says the same distressing event can be re-read through changed appraisal dimensions, and challenge/threat work says that as perceived resources and control rise, a demanding situation can flip from threat-like to challenge-like without the raw demand disappearing. ([PubMed][5])

### What that implies for this repo right now

The intended `graffito_regulation_slice_test` is already pointing at the right target shape:

* same `raw-input-ids`
* different Tony state after apartment support
* different appraisal mode at the mural
* different selected family at the mural

That is the correct benchmark. The missing source file just means it is a design target, not shipped behavior.

### Two concrete Graffito examples

#### Example A: same raw cue, different later appraisal, different family

**Before support**

* raw cue family:

  * `:light_jolt_floods_attention`
  * `:siren_pulse_hits_body`
  * `:noise_fragments_precision`
  * `:cop_light_closing`
* Tony state: low entrainment, low agency, low control
* appraisal: `:threat-dominant`
* selected family: `:reversal`

**After apartment support / rehearsal**

* same raw cue ids
* Tony state: higher entrainment, higher agency, higher control
* appraisal: `:challenge-dominant`
* selected family: `:rationalization` or `:rehearsal` follow-on
* selected operator/frame: `:rf_same_light_can_be_held`

That is the first benchmark you want.

#### Example B: same raw cue family, same family class, different operator

Later, after repeated rehearsal, you should be able to show:

* same mural cue family still activates a control-oriented response family
* but the selected operator changes from an avoid/undo move to a hold/shape/channel move

That second benchmark can wait. Example A is enough for now.

### Exact trace evidence you should see

A succeeding trace should visibly include:

* `:raw-input-ids` before and after, unchanged
* Tony state before and after
* derived `:regulation-mode` before and after
* appraisal projection before and after
* selected family before and after
* selected frame / cause / operator before and after
* provenance for the support or rehearsal episode that contributed
* stored episode ids and admission status

If those are not all visible, you will not be able to tell whether the kernel actually changed reading or just changed surface output.

### Distinguish the three claims clearly

**Typed retrieval happened**

* evidence: retrieval hit exists
* overlap / provenance / episode id visible

**Regulation changed appraisal**

* evidence: same raw inputs, different Tony state, different appraisal projection

**Changed appraisal altered behavior**

* evidence: different family or operator actually selected and executed

Do not collapse those.

### One benchmark assertion shape

This is the regression shape I would target next:

```clj
(is (= (:raw-input-ids before)
       (:raw-input-ids after)))

(is (= :threat-dominant
       (:appraisal-mode before)))
(is (= :challenge-dominant
       (:appraisal-mode after)))

(is (= :reversal
       (:selected-family before)))
(is (= :rationalization
       (:selected-family after)))

(is (> (get-in after [:tony-state :entrainment])
       (get-in before [:tony-state :entrainment])))
(is (> (get-in after [:tony-state :felt-agency])
       (get-in before [:tony-state :felt-agency])))

(is (= support-episode-id
       (:source-episode-id after)))
```

That is mechanistic enough. It proves rereading, not just retrieval.

## Build order

1. **Add Tony persistent state** with the four variables only.
2. **Add appraisal projection** from situation facts + Tony state.
3. **Add one post-family reappraisal step** after local effects settle.
4. **Add one authored rehearsal/support routine** in the apartment.
5. **Prove the mural reread** with identical raw inputs and changed family choice.
6. **Only then** extend accumulation, multi-character state, or richer object phases.

That is the shortest path that is both psychologically honest and buildable.

[1]: https://stacymarsella.org/publications/abstracts/MarsellaCSR09-abstract.html?utm_source=chatgpt.com "<a href=\"http://www.stacymarsella.org/publications/pdf/EMA_Dynamics.pdf\">EMA: A process model of appraisal dynamics</a>"
[2]: https://pmc.ncbi.nlm.nih.gov/articles/PMC6614335/ "
            Challenge and Threat: A Critical Review of the Literature and an Alternative Conceptualization - PMC
        "
[3]: https://pmc.ncbi.nlm.nih.gov/articles/PMC4193464/?utm_source=chatgpt.com "Cognitive Reappraisal of Emotion: A Meta-Analysis of Human Neuroimaging Studies - PMC"
[4]: https://pmc.ncbi.nlm.nih.gov/articles/PMC3141540/ "
            Best practice for motor imagery: a systematic literature review on motor imagery training elements in five different disciplines - PMC
        "
[5]: https://pubmed.ncbi.nlm.nih.gov/36745065/ "Appraisal shifts during reappraisal - PubMed"
