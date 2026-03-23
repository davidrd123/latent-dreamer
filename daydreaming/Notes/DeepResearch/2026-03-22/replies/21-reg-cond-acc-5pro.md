Here’s the build-order memo.

## Bottom line

1. **Put regulation in Tony-owned state, not in situation facts.**
2. **Add appraisal twice:** once before family activation, once right after family effects on the **same** situation.
3. **Do not add a new regulation family.** The missing mechanism is reappraisal, not taxonomy.
4. **The next benchmark is not a bigger ontology.** It is a same-cue before/after trace: same mural inputs, different Tony state, different appraisal, different family. This is the right next slice$_{90%}$.

---

## 1) Where regulation belongs in the current loop

**Strongest outside-knowledge conclusion:** regulation should enter **primarily through appraisal**, especially coping/control appraisal, with a smaller downstream effect on affordance gating and retrieval. EMA explicitly treats appraisal as operating over a person’s interpreted relation to the environment, and Scherer places control/power among the major appraisal criteria. Challenge-threat work says the same motivated situation changes as perceived resources are weighed against demands; later reviews also argue challenge and threat can coexist, which fits your `:threat-dominant` / `:challenge-dominant` labels better than a hard binary. ([Stacy Marsella][1])

That means the loop you want is:

`decay → update Tony state from scene inputs → appraise → activate family goals → select/run family → apply family state delta → reappraise same situation → then store/bridge/use memory`

For this repo, that implies two seams:

* **Before** `goal_families/activate-family-goals` in `control/run-cycle`:
  read visible facts + Tony’s carried state, derive appraisal and family-bias signals.
* **After** family effects settle in `goal_families/run-family-plan`:
  update Tony state, then rerun appraisal on the **same** live situation before the next activation race.

Your current rationalization afterglow is not enough. It changes trigger emotion and adds hope, but it does **not** recompute the same cue family.

### What regulation should change

In order of importance:

* **Appraisal itself:** yes, this is the main lever.
* **Emotion generation from appraisal:** yes, downstream of appraisal.
* **Family weighting:** yes, as a bias term on existing candidates.
* **Retrieval accessibility:** yes, a little, by changing active indices and cue coherence.
* **Emotion decay/persistence:** only as a consequence, not the main mechanism.

If you model the Graffito shift mainly as emotion decay, you get a sedation story. That is wrong. Tony’s arc is “same intensity, more usable control,” not “less feeling.”

### Coping-potential inside appraisal vs downstream modulator

Use a **hybrid**, but put most of the mass inside appraisal.

* **Inside appraisal:** threat/challenge balance, coping potential, self-compatibility.
* **Downstream gate:** whether some affordances are available now, especially the Can and mural operators.

If you put regulation **only downstream**, the cue still “means threat,” but the system mysteriously picks a calmer family. Fake.
If you put it **only inside appraisal**, you get the emotional flip but miss the fact that some operators should remain unavailable until control rises.

### Architecture precedents

If you want one precedent to actually port, **EMA** is the best fit.
If you want a second borrowed idea, take **PSI/MicroPsi** modulators like resolution level and selection threshold.
**ACT-R** and **Soar** are useful analogies, not the template. ACT-R is good for bottleneck thinking because buffers hold one chunk and one production fires at a time. Soar is good for operator-selection thinking because impasses create substates for evaluating operators. Neither one gives you the Graffito regulation arc directly. PSI/MicroPsi does better on modulators, and active inference gives the cleanest story for agency as beliefs about controlled behavior. ([Cognitive AI][2])

### What it implies for this repo right now

Two nit-picky points matter:

* **Do not direct-dispatch families from `:regulation-mode`.** No `if overloaded -> reversal`.
  Use appraisal-derived **bias** on the family candidates the kernel already knows how to activate.
* **Do not zero out negative affect after support.** Your current rationalization activation still wants a live negative concern. The benchmark should be `threat-dominant → challenge-dominant`, not `fear → none`.

Because current family goal strength is basically trigger emotion strength, you need a small extra term, something like:

```clj
family-strength
= trigger-emotion-strength
+ appraisal-bias(family, tony-state, situation-facts)
```

That is enough to let rising control/agency push `:rationalization` or `:rehearsal` above `:reversal` without inventing a second chooser.

### Smallest next implementable slice

Add one appraisal pass that outputs:

* appraisal summary, e.g. `:threat-dominant` / `:challenge-dominant`
* emotion updates
* family bias terms
* active retrieval indices

Do **not** globally lower retrieval thresholds. Your retrieval code already has marks, provenance bonuses, same-family caps, and retention. The honest way to let regulation affect retrieval is to change **which indices become active**, not to make all memory looser.

---

## 2) Minimum persistent Tony character-state

**Strongest outside-knowledge conclusion:** the minimum persistent state is still the four-variable set the repo has already converged on:

```clj
:character-state
{:tony {:sensory-load ...
        :entrainment ...
        :felt-agency ...
        :perceived-control ...}}
```

Keep `:overloaded | :bracing | :entraining | :flowing | :creating` as a **derived debug label**, not source of truth. PSI/MicroPsi’s modulators and active inference’s treatment of agency as beliefs about control both point in this direction. ([Cognitive AI][2])

### What persists vs what recomputes

**Persist with leaky decay across cycles and scene transitions:**

* `:sensory-load`
* `:entrainment`
* `:felt-agency`
* `:perceived-control`

**Recompute every cycle from current situation facts:**

* crowd / siren / light / tactile load
* rhythmic affordance
* co-regulator presence
* exposure / surveillance
* object affordances
* appraisal labels like `:threat-dominant` and `:challenge-dominant`

I would **not** add `:cue-coherence` or `:precision-control` yet$_{70%}$. They are real, but for the next benchmark they can be derived from the four variables plus current inputs. Add them only if a failing trace forces it.

### Tony only, or Tony + Monk + Grandma?

**Tony only in v1.**

Monk and Grandma should remain:

* scene inputs
* relationship facts
* obligation/pressure facts
* co-regulation affordances

Do **not** give them full persistent regulation state yet.

Tradeoff: you postpone reciprocal appraisal and richer multi-agent regulation. That is fine. The first mechanistic target is Tony’s appraisal flip. Multi-character state would triple the update surface and muddy the trace.

If you later need one extra notch, add a tiny authored field like `:monk-co-regulation-availability`, not a full Monk state machine.

### Decay vs accumulated capability

Separate these cleanly:

* **Transient regulation state:** one good apartment support cycle can change it.
* **Accumulated capability:** only repeated successful episodes change it.

That separation is backed by imagery work. A meta-analysis of post-injury mental imagery interventions found 10 studies and a strong positive trend for self-efficacy (`g = 0.99`) but only a small mobility effect (`g = 0.16`), with heterogeneity and low power. Separately, mastery imagery ability is associated with greater confidence, more frequent challenge appraisal, lower anxiety, lower perceived stress, and increased control/coping. In plain English: rehearsal can change “I can handle this” before it produces retained skill. ([UCLan - University of Central Lancashire][3])

So for this repo:

* one apartment support / rehearsal cycle should update **state**
* repeated succeeded support / rehearsal episodes should update **skill**

And you already have the right scaffold for that: the membrane. Use repeated succeeded episodes to promote a provisional regulation-support episode to durable, then write a durable capability or relation. Do **not** invent a separate learning subsystem now.

### When exactly to rerun appraisal

**After effect application and after all appraisal-relevant branch-local consequences settle, but before stored-episode side effects can influence the next choice.**

Concretely:

1. assert branch-local facts / object phase changes / relation changes
2. update Tony state
3. rerun appraisal on same situation
4. then store the episode / emit bridges / allow next cycle

If you rerun too early, you miss the new facts.
If you rerun too late, you confound reappraisal with memory side effects.

---

## 3) Minimum next Graffito representation

**Strongest outside-knowledge conclusion:** the smallest useful representation is **not** scene-level “regulation facts.” It is **scene-owned regulation inputs plus Tony-owned state**. For layered reality, keep counterpart entities distinct and link them by typed correspondence. SME supports correspondence as a role-structure mapping problem, not identity collapse, and GOLEM supports narrative entities as explicitly structured fictional elements. 

### Smallest useful schemas

#### A. Scene-owned regulation inputs

Use something like:

```clj
{:fact/type :sensorimotor-input
 :fact/id ...
 :channel :visual|:auditory|:proprioceptive|:rhythmic|:tactile|:social
 :effect :load|:entrain|:control-channel
 :magnitude 0.0-1.0
 :target :tony}
```

That is the corrected design.
The scene owns **inputs**. Tony owns **processing**.

#### B. Cross-layer correspondence

```clj
{:fact/type :cross-layer-correspondence
 :fact/id ...
 :source-id :grandma
 :target-id :motherload
 :relation :counterpart-of|:reframe-force-of|:capacity-correspondence
 :registers [:baseline :magic]}
```

No identity collapse.

#### C. Person-object relations

```clj
{:fact/type :person-object-relation
 :fact/id ...
 :person-id :tony
 :object-id :can
 :relation :lineage|:trust|:attunement|:regulation-tool|:permission|:symbolic-correspondence
 :strength 0.0-1.0}
```

This is what makes Graffito different from generic artifact-state.

### What should be facts vs object state vs relation structure

**Ordinary typed facts:**

* present actors
* exposure / surveillance
* recent events
* `:sensorimotor-input`
* `:cross-layer-correspondence`

**Dedicated object-state maps:**

* **Can**
* **Mural**

Those two actually change phase and gate later behavior.

Minimal object state:

```clj
:objects
{:can   {:phase :inert|:steadyable|:worldmaking
         :required-mode #{:entraining :flowing :creating}
         :available-affordances #{...}}
 :mural {:phase :surface|:threshold-surface|:portal-surface
         :gated-by {:object :can
                    :mode #{:entraining :flowing :creating}}}}
```

**Keep simple for now:**

* Sketchbook can remain a relation/fact
* Elephant can wait

Bluntly: **do not pull Elephant into the next kernel slice.** It is not needed for the first mechanistic proof.

### Smallest situation set

Still **street + apartment + mural**$_{85%}$.

Why:

* **street** seeds carried load
* **apartment** changes state through support / reframe / rehearsal
* **mural** tests same-cue reappraisal under pressure

If you want the absolute smallest benchmark, do:

1. mural baseline read
2. apartment support cycle
3. same mural reread

But the first story-honest miniworld should add the street overload slice.

### What it implies for the repo right now

Your next representation should add only these new things:

* Tony `:character-state`
* scene-level `:sensorimotor-input`
* `:person-object-relation`
* `:cross-layer-correspondence`
* object maps for **Can** and **Mural**

That is enough. More than that is authoring drag.

---

## 4) Rehearsal as regulation, not generic future planning

**Strongest outside-knowledge conclusion:** rehearsal in Graffito should mean **practicing a body-policy that makes action controllable under load**. In your Mueller-derived architecture, rehearsal is already a goal type for preparation; for Graffito, the prepared thing is not abstract future success, it is stable control in a high-load scene. 

The outside-knowledge fit is good. Mastery imagery is associated with confidence, challenge appraisal, and better coping; some work argues that imagery can alter control/coping more than it alters the stressor itself; and the informational theory of flow ties flow to tighter means-end coupling. Rhythm-and-movement interventions also show measurable self-regulation gains: the 2023 RAMSR clustered RCT involved 213 children across eight preschools, 16 to 20 sessions over eight weeks, and found significant post-intervention self-regulation gains. ([UBIRA ETheses][4])

So, in this repo, rehearsal should **not** write “Tony imagined success.”
It should write:

* `:entrainment ↑`
* `:felt-agency ↑`
* `:perceived-control ↑`
* `:sensory-load ↓` a bit, not to zero
* Can/Mural affordance availability maybe opens

It may also change retrieval by making supportive indices active.

### Clean mechanistic relation to later family behavior

The clean chain is:

`rehearsal/support → Tony state delta → same-situation reappraisal → changed family or changed operator`

Not:

`rehearsal → future success fact`

That second version is generic planning. It misses Graffito.

### What it implies for this repo right now

Use rehearsal to change the state variables that later appraisal reads.
Do **not** make rehearsal the direct chooser of later behavior.

One more nit-picky point: your code barely has an explicit operator layer. So the next benchmark should prove a **family flip**, not an operator flip. Operator flip comes after you have trace vocabulary for operators.

### Smallest next implementable slice

One apartment support/rehearsal operator or family result that:

* consumes `Monk present + rhythm available + can lineage/sketchbook support`
* writes a Tony state delta
* stores a provisional family-plan episode
* reruns mural appraisal with unchanged raw input ids

That is enough.

---

## 5) Exact mechanistic success signature

**Strongest outside-knowledge conclusion:** success is **not** “retrieval happened” and it is **not** “a family ran.” Success is that strongly overlapping raw cues now produce a different appraisal because Tony’s state changed, and that changed appraisal changes behavior. Challenge-threat theory is explicit that resource appraisal changes the state readout of the same performance pressure; the review literature also says challenge and threat can coexist, so a dominance label is the right simple benchmark. ([PMC][5])

### Primary Graffito example

Use this one first:

**Before support**

* raw mural cues: cop light, siren pulse, exposing street, portal wall, can lineage, interrupted mural
* Tony: low entrainment, low agency, low control
* appraisal: `:threat-dominant`
* selected family: `:reversal`

**After apartment support / rehearsal**

* **same raw cue ids**
* Tony: entrainment/agency/control up, load slightly lower or more manageable
* appraisal: `:challenge-dominant` or at least challenge-forward while threat remains live
* selected family: `:rationalization` or `:rehearsal`

That is the benchmark.
Not “different scene.”
Not “different cue set.”
Same cue family, different read.

### What the trace should show

You want these fields, explicitly:

1. `:raw-input-ids-before`
2. `:tony-state-before`
3. `:appraisal-before`
4. `:candidate-family-scores-before`
5. `:family-effect`

   * frame/operator id
   * state delta
   * family-plan episode id
6. `:raw-input-ids-after`
7. `:tony-state-after`
8. `:appraisal-after`
9. `:candidate-family-scores-after`
10. `:selected-family-after`

### Distinguish the three claims cleanly

**“Typed retrieval happened”**
You see an episode hit, overlap, provenance, effective marks.

**“Regulation changed appraisal”**
You see the **same** raw cue ids, a Tony-state delta, and a changed appraisal summary.

**“Changed appraisal altered behavior”**
You see a changed family score or changed selected family/operator downstream.

Do not collapse these into one blob.

### Regression assertion shape

The benchmark should look roughly like this:

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
```

That is enough for the next slice.

---

## Build order

1. **Add Tony state + appraisal pass**
   Before family activation. No new family.

2. **Add post-family state delta + same-situation reappraisal**
   This is the real missing mechanism.

3. **Run the mural-before / apartment-support / mural-after benchmark**
   Same raw cues, changed appraisal, changed family.

4. **Only after that add durable skill accumulation through the membrane**
   Repeated succeeded support/rehearsal episodes promote to durable capability.

5. **Only after that consider Elephant, Motherload-world expansion, or multi-character persistent state**

That order keeps the repo honest.

[1]: https://stacymarsella.org/publications/pdf/EMA_Dynamics.pdf#:~:text=L318%40P4%3A%202,or%20cope%20with%20these%0A%0AL329%40P4%3A%20consequences "EMA: A process model of appraisal dynamics"
[2]: https://cognitive-ai.com/publications/assets/MicroPsiArchitectureICCM03.pdf#:~:text=L284%40P2%3A%20Emotional%20Behaviour%20Script%20Space,Goal%20Creation%2FVerification%0A%0AL299%40P2%3A%20Technical%20Percept%20Receiver "Pre Test Excerpt"
[3]: https://knowledge.lancashire.ac.uk/id/eprint/17705/2/17705%20A-meta-analysis-of-mental-imagery-effects-on-post-injur_2018_Psychology-of-S.pdf#:~:text=L22%40P0%3A%20Objectives%3A%20A%20meta,psychological%20functioning%20in%20injured%20athletes "A meta-analysis of mental imagery effects on post-injury functional mobility, perceived pain, and self-efficacy"
[4]: https://etheses.bham.ac.uk/id/eprint/8879/1/Moller19MScbyRes.pdf#:~:text=L321%40P15%3A%20Beyond%20its%20association%20with,has%20not%20yet%20examined%20whether "Microsoft Word - CMoller Thesis Revisions 01.2019 Final.docx"
[5]: https://pmc.ncbi.nlm.nih.gov/articles/PMC11869992/?utm_source=chatgpt.com "The effects of challenge and threat states on performance ..."
