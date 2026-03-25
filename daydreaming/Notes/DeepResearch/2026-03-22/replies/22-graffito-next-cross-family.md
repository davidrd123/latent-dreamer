## 1. Best next pairing

**Best next pairing: `reversal → rehearsal`$_{85%}$, specifically `mural reversal → apartment rehearsal`.**

That is the next honest move after the currently proven `rationalization → rehearsal` path. You already showed **meaning repair → embodied control**. The next thing to prove is **failure diagnosis → embodied control**.

Why this one:

* it gives you a **new source family**, not just another rationalization producer
* it uses pieces you already have: reversal already stores family-plan episodes; rehearsal already exists as a live consumer surface in the miniworld
* it matches the Graffito material: Tony’s near-miss / cop-light aftershock is not dead content, it is the thing Monk later teaches him to metabolize into rhythm and control

Psychologically, this is the clean chain: Tony replays the breakdown, isolates where control snapped, then practices there. Counterfactual work says these thoughts are tied to goal-directed action and can provide “blueprints for future action”; they shape later intentions **when the later intention matches the content of the counterfactual**. Mental practice literature says rehearsal is literally cognitive rehearsal of movement, and case-based planners like CHEF use causal explanations of failure to retrieve repair strategies later. That is almost a direct spec for this bridge. 

## 2. Why not the main alternatives

**`rationalization → reversal`** is the tempting answer. I think it’s wrong for now.

Reason: rationalization mostly changes *meaning* and self-compatibility. Reversal needs a *causal fault model*. If you wire rationalization in now, you are mostly teaching reversal which branch to prefer, not proving a new source-consumer dependency. That may become useful later, once reversal has several real operator variants to choose among. Right now it is mostly a weighting hack.

**`reversal → rationalization` in a second form** is worth doing, but not next.

You already have a live extracted bridge of that family type. A second Graffito instance would broaden coverage, but it would not answer the actual question here, which is whether the miniworld can sustain a **new** consumer-family dependency.

**Any pairing with rehearsal as the source** should wait.

Right now rehearsal is benchmark-local and does not yet store reusable family-plan episodes. So `rehearsal → reversal` or `rehearsal → rationalization` first requires changing rehearsal into a real source-producing family. That is a bigger architectural step than this prompt is asking for.

I would also **postpone roving pairings**. Graffito’s fantasy layer is not generic pleasant distraction. If you benchmark roving now, you risk rewarding escape-surface instead of the overload→agency mechanism.

## 3. Minimum benchmark implementation

Use a **stored mural reversal episode** first, not a street one. Do not fight the code. Your current rehearsal-source probe already keys off mural-visible indices, so mural reversal is the smallest buildable source.

The source episode should not be any old reversal. It should be a **diagnostic reversal** with one extra compact field that tells rehearsal what to repair. Not a broad ontology pass. One field.

Something like:

```clj
{:source-family :reversal
 :rule-path [:goal-family/reversal-plan-dispatch]
 :breakdown-surface #{:light_jolt_floods_attention
                      :siren_pulse_hits_body
                      :noise_fragments_precision}
 :repair-target :precision_under_pulse}
```

Then let **apartment rehearsal** consume reversal episodes only when all four hold:

1. `source-family = :reversal`
2. the source overlaps the current mural cue family
3. `:repair-target` matches the authored routine’s purpose
4. apartment preconditions are live (`:monk_counts_a_holdable_beat`, `:sketchbook_offers_small_surface`, `:repeated_stroke_returns_precision`, `:monk_co_regulates_tony_with_rhythm`)

That content match is the important nit. Counterfactuals change later behavior when the intended behavior matches the counterfactual content, and mental-simulation effects are more reliable for effective/superior simulations than for generic ones. So the bridge should be **matched repair target**, not generic shared-fact overlap. ([PMC][1])

This should be **benchmark-local first**, inside `graffito_miniworld.clj`, not kernel-level immediately. The current extracted cross-family registry has no rehearsal-targeted bridge rule, and your existing `rationalization → rehearsal` path is already bespoke. Keep this one local until it proves itself. If it works, *then* extract a frontier rule like `reversal-breakdown-to-rehearsal`.

## 4. Success signature

If this pairing is real, three things should move.

First, **source diversity in rehearsal races**. Reversal-sourced candidates should become visible in the rehearsal candidate race, and at least one should win for a reason better than “shared a few facts.”

Second, **membrane evidence**. You should see a use record with `source-family :reversal`, `target-family :rehearsal`, repeated `:succeeded` outcomes, and then a durable promoted reversal episode.

Third, **downstream reread change**. After reversal-sourced rehearsal, the later mural pass should shift in a trace-visible way: same mural cue family, new rehearsal source, then changed appraisal or changed later family/operator choice.

The counters I’d add:

* `reversal_source_candidate_cycles`
* `reversal_source_win_cycles`
* `reversal_to_rehearsal_promotion_count`
* `challenge_mural_cycles_after_reversal_sourced_rehearsal`

False positives:

* reversal candidates appear but never beat rationalization sources
* reversal wins only because of loose overlap, not matched `repair-target`
* promotion happens but later mural appraisal and choice do not change
* the trace still only lives off rationalization sources and you just renamed the path

## 5. Relevant prior art

Three stacks matter.

**Counterfactual theory.** Epstude and Roese frame counterfactuals as tied to goal-directed cognition and future action; Smallman and Roese show the effect on later intentions is **content-specific**. That is why `reversal → rehearsal` should require a matched repair target, not just family overlap. 

**Mental rehearsal / motor imagery.** Motor imagery is cognitive rehearsal of movement, and broader meta-analytic work shows mental simulation can change behavior across many domains, with more reliable effects for effective/superior simulations. That supports rehearsal as a targeted control routine, not generic imagined success. ([PMC][2])

**Case-based plan repair.** CHEF repairs failures by building a causal explanation, using it to retrieve repair strategies, and storing the repaired result indexed by the problems it avoids. Derivational replay work sharpens that further: a failure reason specifies when replay will fail again, and retrieval is redirected toward a repaired case. That is the membrane analogue of `reversal → rehearsal`: remembered failure explanation becomes later repair guidance. ([AAAI][3])

**Verdict:** pick `reversal → rehearsal`. It is the smallest new pairing that is story-honest, membrane-clean, and different enough from the current `rationalization → rehearsal` path to actually teach you something.

[1]: https://pmc.ncbi.nlm.nih.gov/articles/PMC2717727/ "
            Counterfactual Thinking Facilitates Behavioral Intentions - PMC
        "
[2]: https://pmc.ncbi.nlm.nih.gov/articles/PMC2561070/ "
            Mental Practice With Motor Imagery: Evidence for Motor Recovery and Cortical Reorganization After Stroke - PMC
        "
[3]: https://cdn.aaai.org/AAAI/1986/AAAI86-044.pdf "1986 - CHEF: A Model of Case-Based Planning"
