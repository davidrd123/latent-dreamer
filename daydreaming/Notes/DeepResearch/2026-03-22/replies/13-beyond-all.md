Here’s the memo. I’m treating the prompt as eight design questions, not a repo summary.

The local ground truth is pretty clear. `beyond-mueller-detailed.md` is the canonical list of directions; `trajectory.md`, `viewpoint.md`, and `system-mechanics-detailed.md` define the actual claim; `mueller-to-kernel-mapping.md` and `build-order-checkpoint-2026-03-22.md` separate what Mueller had, what the kernel collapsed, and what is now back in code. On that basis: first-pass memory ecology, evaluator gating, attributed use/outcome logging, and rule-access scaffolding are real; general serendipity traversal, verified paths, strong realism/desirability pruning, cross-session developmental proof, and adaptive routing are not. The notes `arch-prompt.md`, `prompt-set.md`, `visioning-prompt-v2.md`, and `20-fatima-extraction.md` are useful syntheses, but they are not primary literature. I checked the strongest outside claims separately.

## 1. Heterogeneous graph serendipity

The best outside matches are not GraphRAG. They are literature-based discovery, semantic ideation networks, and conceptual blending. All three are about finding non-obvious bridges across differently typed knowledge fragments, usually by explicit search plus pruning. That means your move from Mueller’s homogeneous rule graph to a graph with activation rules, planning rules, evaluation rules, retrieval events, and critic signals is plausible$_{70%}$ as a real extension, not just repackaging. But Copycat/Metacat are still the hard objection: they say the hard part of creativity is often descriptor fluidity, i.e. conceptual slippage, not merely path-finding over a fixed ontology. So the clean synthesis is: keep the graph as the explicit, typed path substrate, but let descriptor vocabulary evolve at rule-creation time. Do not try to “solve” Copycat by making the graph itself usage-adaptive. That would kill the repo’s stated preserve-property for serendipity. ([ScienceDirect][1])

I did **not** find a convincing empirical paper that gives a sparse → useful → soup phase transition for creative typed graphs. The nearest literature mostly warns about combinatorial explosion and noisy bridging, not a clean threshold law. So for this project, the right control variables are operational: effective branching factor, candidate-path count per anchor pair, percentage of candidate paths that survive verification, shared-key specificity, and how often path ranking has to rescue structurally weak paths. Your local mechanism note’s “probably 2–4 hops” is a good engineering prior, but I would treat it as a hypothesis, not a literature-backed fact.

My recommendation: static structurally derived edges, learned ranking only, and separate graph views by semantic phase. In practice, you want a typed corridor first: activation → planning → evaluation, with maybe retrieval-event nodes added later. If that small corridor already explodes, the full heterogeneous graph will be mush.

## 2. Systematic LLM evaluator insertion

There is not much direct precedent for “take an existing cognitive architecture and systematically insert LLM evaluators at every judgment point.” The closest pattern is FAtiMA: appraisal is kept independent of any one reasoner, and specialized algorithms appear as meta-beliefs that the shared rule layer can read. That is a genuine architecture seam, not just tool use. LLM-ACTR is different: it injects ACT-R process knowledge into model adapters, which is much closer to moving cognition into the model than keeping evaluators at bounded interfaces. Prompt-enhanced ACT-R/Soar is even further away: that is LLM-assisted model authoring, not runtime evaluator insertion. NL2GenSym is the strongest shape match because it uses an execution-grounded generator-critic loop where rules are immediately executed and refined against grounded feedback. ([arXiv][2])

That suggests a clean evaluator taxonomy for your system. Use evaluators as one of three things: gate, rank, or diagnose. Gate = “is this promotable at all?” Rank = “which of these candidates is best?” Diagnose = “why did this fail or look brittle?” Do **not** let evaluators become free-form planners. Your local docs are already moving the right way here: `build-order-checkpoint-2026-03-22.md` has the evaluator as post-plan gating, not branch author. Keep that. The strongest analogue from current routing/tool-use work is that self-assessment only helps when calibration is decent; confidence by itself is cheap and often misleading. ([arXiv][3])

On granularity: one evaluator call per mechanism per cycle is probably wrong. One call per candidate is also usually wrong. The sweet spot is mechanism-level batching: let the kernel produce a small, typed candidate set, then have the evaluator rank or veto that set. Per-candidate calls only make sense when branch factor is tiny or the decision is especially high stakes, مثل promotion to durable memory or opening a frontier rule.

## 3. Rules the system creates

This direction has the clearest direct precedent: NL2GenSym. It shows that LLM-generated symbolic rules can enter a persistent symbolic system and become useful, but only because rule generation is wrapped in an execution-grounded generator-critic loop and immediate environment testing. That is the part people keep trying to skip. Your local `schema / denotation / executor` split is necessary, but it is not sufficient. A rule can be schema-valid and denotationally plausible and still be garbage in practice. You need at least four filters: schema validity, denotational validity, executable sandbox firing, and later downstream utility evidence. ([arXiv][4])

The cautionary literature from Soar and ACT-R points the same way. Soar chunking and ACT-R production compilation absolutely do turn successful traces into reusable rules. But they also create the classic utility problem: symbolic learning can degrade performance as the learned rule base grows, and pruning or forgetting helps. In Soar, long-term performance improved when a large fraction of learned productions was removed; in ACT-R, production compilation is explicitly a specialization mechanism that compresses sequences into task-specific rules, not an invitation to accumulate everything forever. ([Soar][5])

So the admission discipline for created rules should be harsh. New rules should start on a frontier/probation layer, carry explicit antecedent/consequent specificity measures, and face merge/quarantine/excision. A generated rule should need more than “the evaluator liked it.” It should need cross-context success or at least repeated sandbox success under varied bindings. If you don’t do that, graph growth becomes graph pollution.

## 4. Directed daydreaming

There is real adjacent literature here, but it usually talks about **deliberate** or **intentional** mind wandering, not “directed daydreaming.” The strongest result I found is a 2025 study using a large sample of 1309 adults with resting-state fMRI and creativity measures: deliberate mind wandering was positively associated with creative performance, while spontaneous mind wandering was not. That does not prove your architecture, but it does support the repo’s local phrase “constrain the question, not the surprise.” A creative brief that anchors the thought-space while leaving the endpoint open is much closer to deliberate MW/incubation than to explicit planning. ([ScienceDirect][6])

The broader creativity/incubation literature is messier than people like to pretend. Reviews and experiments still show context dependence: some incubation conditions help, some do not, and conscious reflection during incubation can outperform uncontrolled drifting. The dynamic-framework work on spontaneous thought is useful here because it treats internal thought as shaped by varying deliberate and automatic constraints, with meta-awareness changing the effective constraint structure. That is a good fit for your one-end-anchored-search idea. ([UCSB Psychology Labs][7])

So my answer is: the brief should constrain concern-space, motifs, and evaluation criteria, **not** the processing family inventory by default. Rehearsal, reversal, rationalization, roving, repercussions all remain useful machinery. The brief decides what unresolved material is “in bounds” and what outputs count as acceptable. You only hard-disable a family if its ontic behavior violates the brief. Otherwise you are quietly collapsing daydreaming back into planning.

## 5. DSPy-style optimization of write/read

I did **not** find convincing published work where DSPy itself is used specifically to optimize the **write side** of agent memory. The closest primary line is GradMem: writing is an objective, not just a formatting choice, and explicit loss-driven write operations outperform forward-only writers with the same memory size. A-MEM and Learn to Memorize push in a similar direction, but they are about adaptive memory architectures rather than DSPy-style signature optimization, and `Learn to Memorize` is a withdrawn ICLR 2026 submission, so I would not lean on it heavily. ([OpenReview][8])

That means your local instinct is right, but the metric in the prompt needs tightening. “Trace divergence” alone is not a quality metric. Garbage also causes divergence. The write objective should be signed. The most important measurements are: does the residue improve later analog transfer **without** replaying the original trace; does it improve retrieval hit usefulness; does it improve promotion precision; does it reduce stale/backfired memory; and does it preserve world-anchored cues rather than only stylistic residue. GradMem’s lesson is that write quality matters; your architecture’s lesson is that write quality must be evaluated at the level of later cognition, not just reconstruction.

The best token-space analogue to GradMem reconstruction is also not “can I reconstruct the prose.” That’s the wrong target. The right target is: can the model reconstruct the **kernel-relevant slice** of the cycle from the residue alone? Active concern, operator family, world-anchored cues, evaluation flags, and maybe the candidate next-action set. That is the representation you actually care about preserving.

Also, the long-memory benchmarks are a useful warning. LoCoMo was built to test very long-term conversational memory across long multi-session dialogues, and its original paper reported that long-context models and RAG improve memory QA but still lag human performance substantially. Mem0 then showed that structured persistent memory helps a lot on that benchmark, but its graph-memory variant was only modestly better than its base memory system. So: optimize the write/read interface, yes. But do not assume that “more graph” is where the gain will come from. ([arXiv][9])

## 6. Developmental trajectory

There is not yet a clean, standard literature on “cognitive style” for artificial agents in the sense you want. The best adjacent move is behavioral: treat style as a stable difference in observed policy under controlled contexts, not as self-description. The new AI Agent Behavioral Science line is useful because it explicitly argues that agent behavior emerges from the interaction of model, context, feedback, and environment, and that the right science is intervention-based, not just model-internal. That matches your repo’s developmental claim better than the personality-chatbot literature does. ([arXiv][10])

Two newer empirical lines matter. First, long-term multi-agent conversation work shows that diversity tends to degrade over time and that memory is often the most diversity-suppressing prompt component. Second, persona studies show a boundary condition: self-reports may remain stable while observed persona expression declines over long conversations. In other words, “stable style” is very easy to fake if you only look at declared identity, and much harder if you look at actual long-horizon behavior. 

So the control experiment is straightforward. Hold model, initial world, and task distribution fixed. Vary history. Cross histories and seeds. Then do intervention tests: remove or swap promoted episodes/rules and see whether the behavioral profile changes in the dimensions the architecture claims are causal. Meaningful style is history-sensitive, intervention-sensitive, and transfers to held-out but structurally similar situations. Random drift is not. Your local proposed metrics are already pretty good: daydream-family distributions, successful edge types, motif neighborhood recurrence, revisit entropy, promotion/demotion profile, and anti-residue frequencies.

## 7. Accessibility frontier as growth

This is the direction where the repo may actually be ahead of the literature. The analogies are real: Soar chunking learns new procedural knowledge from impasse-driven subgoals, and ACT-R production compilation learns new rules by collapsing sequential rule/declarative interactions into compiled productions. But neither tradition gives you a clean “generated rule enters probation, then graduates by evidence” framework. What they do give you is a warning: unconstrained rule growth causes utility problems, interference, and performance degradation. ([Soar][5])

I did not find a standard staged-admission framework in mainstream Soar or ACT-R papers. So your local `:frontier / :accessible / :quarantined` idea looks like an actual contribution, not a literature rename. The immediate design consequence is blunt: rules should not become planner-accessible just because they were generated, retrieved, or evaluator-approved. They should require durable evidence, ideally cross-context or cross-family evidence, before they move from frontier to accessible. Quarantine should not just be for syntax failures; it should also be for low-utility or repeatedly backfiring rules.

Locally, you already have first-pass scaffolding for this in `build-order-checkpoint-2026-03-22.md`. What is still missing is the hard part: accessibility derived from downstream evidence rather than from authored-core status or evaluator opinion. That is the difference between a decorative frontier and a real growth mechanism.

## 8. Multi-model routing as cognitive economics

The local claim here is modest and solid: `37-plan-of-attack.md` reports Haiku around 3.3 s/cycle and Sonnet around 7.6 s/cycle on the runtime thought-beat projector, with Sonnet doing better on operator-heavy or reversal-like cycles. That is hand-tuned routing evidence. It is **not** yet an adaptive routing policy. As a first result, that’s fine.

Outside work now gives you direct analogues. Route to Reason routes both model and reasoning strategy to get a better accuracy/compute tradeoff; one result it highlights is over 60% token reduction while beating the best single-model baseline. Uncertainty-based routing papers show that calibration matters more than raw confidence, and MeCo explicitly frames the decision to invoke external tools as a metacognitive self-assessment problem. RADAR pushes the same logic further by routing model size **and** reasoning budget jointly according to query difficulty. That is very close to what your kernel wants to do. ([arXiv][11])

Nelson–Narens is the right conceptual vocabulary: monitoring estimates competence/difficulty; control allocates resources. In your kernel, good monitoring features are not generic vibes. They are structural. Operator family, path length, branching factor, evaluator disagreement, contradiction/backfire flags, novelty score, ambiguous verification, maybe even whether the current candidate path required soft-binding rescue. Those are the right triggers for escalating from a cheap model to an expensive one, or from no-verification to heavy verification. ([UCI Social Sciences][12])

My recommendation is not to learn the router first. Write the policy down first. “Use cheap model for routine roving/retrieval narration; escalate on reversal, ambiguous verification, evaluator disagreement, or flagged residue.” Once you have telemetry on those triggers, then learn a routing policy. Before that, you are optimizing noise.

## My blunt build order

If the goal is highest-value movement, I would order the eight directions like this:

First: **2 + 7 together**. Evaluator insertion and accessibility frontier are really one control problem. You need good evidence channels before you let the system grow new durable structure.

Second: **1 on a tiny typed corridor**. Build heterogeneous serendipity on a deliberately narrow graph slice and instrument path explosion hard.

Third: **5**. Write/read optimization is not glamour work, but it determines whether persistence becomes signal or sludge.

Fourth: **8**. Adaptive routing only gets easier once you have evaluator outputs, anti-residue signals, and verification telemetry.

Fifth: **3**. Rule creation becomes much safer once frontier discipline and routing exist.

Sixth: **6**. Developmental measurement is downstream of the above. You cannot measure style cleanly before you have a stable growth regime.

Seventh: **4**. Directed daydreaming is mostly a control/evaluation policy layered on top of the engine. It is not the blocker.

The sharp version is this: the frontier, evaluator discipline, and memory write/read interface are the actual choke points. Everything else gets better or worse depending on whether those three are honest.

Look away from the screen for a minute. Unclench your jaw.

[1]: https://www.sciencedirect.com/science/article/pii/S1532046419300590?utm_source=chatgpt.com "A survey on literature based discovery approaches in biomedical domain - ScienceDirect"
[2]: https://arxiv.org/pdf/2103.03020 "https://arxiv.org/pdf/2103.03020"
[3]: https://arxiv.org/html/2502.04428v1 "https://arxiv.org/html/2502.04428v1"
[4]: https://arxiv.org/abs/2510.09355 "https://arxiv.org/abs/2510.09355"
[5]: https://soar.eecs.umich.edu/soar_manual/04_ProceduralKnowledgeLearning/ "https://soar.eecs.umich.edu/soar_manual/04_ProceduralKnowledgeLearning/"
[6]: https://www.sciencedirect.com/science/article/abs/pii/S1871187124002827 "https://www.sciencedirect.com/science/article/abs/pii/S1871187124002827"
[7]: https://labs.psych.ucsb.edu/schooler/jonathan/sites/labs.psych.ucsb.edu.schooler.jonathan/files/pubs/220._capturing_the_dynamics_of_creative_daydreaming.pdf "https://labs.psych.ucsb.edu/schooler/jonathan/sites/labs.psych.ucsb.edu.schooler.jonathan/files/pubs/220._capturing_the_dynamics_of_creative_daydreaming.pdf"
[8]: https://openreview.net/forum?id=Wdzhnmu5HR&referrer=%5Bthe+profile+of+Yuri+Kuratov%5D%28%2Fprofile%3Fid%3D~Yuri_Kuratov2%29 "https://openreview.net/forum?id=Wdzhnmu5HR&referrer=%5Bthe+profile+of+Yuri+Kuratov%5D%28%2Fprofile%3Fid%3D~Yuri_Kuratov2%29"
[9]: https://arxiv.org/abs/2402.17753 "https://arxiv.org/abs/2402.17753"
[10]: https://arxiv.org/abs/2506.06366 "https://arxiv.org/abs/2506.06366"
[11]: https://arxiv.org/abs/2505.19435 "https://arxiv.org/abs/2505.19435"
[12]: https://sites.socsci.uci.edu/~lnarens/1990/Nelson%26Narens_Book_Chapter_1990.pdf "https://sites.socsci.uci.edu/~lnarens/1990/Nelson%26Narens_Book_Chapter_1990.pdf"
