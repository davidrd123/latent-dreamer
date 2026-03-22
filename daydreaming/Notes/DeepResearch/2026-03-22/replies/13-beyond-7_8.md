Here’s the clean read.

Q7 and Q8 are the same problem viewed from two sides.
Q7 asks: when does newly reachable structure become planner-usable?
Q8 asks: when is that decision hard enough to spend more compute?

Your repo is already pointed the right way. The structural graph is separate from dynamic accessibility, episode use/outcome is separate from raw retrieval, and routing already exists as a small hard-coded policy. The missing move is to treat **frontier opening** and **model escalation** as one monitor→control loop.

## 7. Accessibility frontier as growth

The prompt says Mueller’s frontier is “closest to Soar’s chunking.” That’s only half right.

**Soar chunking** compiles substate reasoning into new rules automatically when a substate produces a result, turning deliberate sequential reasoning into later parallel rule firings. It is automatic, and it fires whenever a result is produced in a substate. **ACT-R production compilation** is narrower: it combines two consecutively firing rules into one and specializes the new rule by substituting the retrieved content into the rule. So Soar is about compiling impasse resolution; ACT-R is about compressing frequent local sequences. Neither is the same as Mueller’s mechanism of changing *accessibility* by storing new episodes under easier cues. Mueller’s move is less “compile a macro” and more “reindex previously latent structure so future thought can reach it.” ([arXiv][1])

That distinction matters because **Mueller’s frontier is not just learning new rules**. It is learning new *routes to old rules*. In Chapter 4, inaccessible episodic rules become effectively usable when a new daydream gets indexed under broader cues; in Chapter 5, serendipity verifies a path, constructs an episode witness, and then future planning can use that path directly. So the closest Soar analog is not plain chunking. It is **chunking plus verification-based learning plus deferred reliance**. Old Soar work explicitly describes “verify advice, then build chunks during verification,” which is much closer to your `candidate path → verified path → stored witness → later direct use` story. ([Cantaloupe Image Server][2])

The literature also answers the “does chunking scale productively?” question with a blunt “sometimes.” Early Soar chunking had real **overgeneralization** problems: if the right result depends on process-state facts like “there is nothing left to do,” then the architecture can fail to include all relevant pre-impasse conditions, and the resulting chunks can become overgeneral or even less efficient than the original problem solving. Later Soar introduced **Explanation-Based Behavior Summarization (EBBS)** specifically to fix generality and correctness problems. The Soar manual gives a nice concrete example: an arithmetic agent that learned 1263 rules in Soar 9.4 learns only 8 much more general rules in Soar 9.6 under EBBS. That is the clearest answer to your “productive growth or degradation?” question: **without summarization discipline, degradation is real; with it, growth can be productive.** ([Cantaloupe Image Server][2])

ACT-R points in the same direction but with a different failure mode. Because production compilation specializes rules using retrieved content, the natural risk is not frontier discovery but **over-specialization and dead compiled rules**. ACT-R’s own tooling docs say production compilation can generate many new productions and that it is difficult to tell which ones are becoming generally useful and which are never used. That is basically your “graph soup” failure in a different costume. So ACT-R is useful as a warning about proliferation, but it is a weak model for your frontier mechanism. It is mostly a speedup mechanism, not a creative-access mechanism. ([ACT-R][3])

There is one especially relevant staged-admission analog in Soar, and it’s easy to miss. In the current architecture, chunking is not used when decisions are driven by numeric preferences, because correctness is no longer deterministic. Laird notes the plan is to add such chunks only when there is **sufficient accumulated experience** that they have a high probability of being correct. That is not your exact `:frontier → :accessible` design, but structurally it is the same idea: **candidate knowledge exists, but admission is deferred until evidence is strong enough.** That is the closest thing I found to explicit probationary rule admission inside a major cognitive architecture. ([arXiv][1])

Modern LLM-symbolic work reinforces this. **NL2GenSym** does not let LLM-proposed SOAR rules into the agent on style points. It immediately executes them inside Soar, uses execution-grounded feedback, and lets a critic iterate on them. On its Water Jug benchmark it reports over 86% rule-generation success and large decision-cycle reductions. So the pattern that actually works in 2025 is: **propose → execute → critique → keep**. That matches your denotation/executor/promotion stack, not naive direct rule admission. ([arXiv][4])

So my answer to Q7 is:

Mueller’s frontier should be implemented as **access restructuring with evidence-gated opening**, not as raw automatic compilation. Your current split already supports that better than Soar or ACT-R do out of the box:

* structural rule graph stays cheap and static
* `bridge-paths` stays candidate-only
* verified-path is second-pass witness construction
* episodes carry use/outcome history
* rule accessibility lives in a separate registry
* only durable, positively used evidence should open frontier rules
* hard failures should quarantine them

That is exactly why `rule-access-registry`, `planning-graph` vs `serendipity-graph`, `note-episode-use`, `resolve-episode-use-outcome`, and `reconcile-episode-rule-access` are the right substrate. The missing abstraction is still the one 11b identified: **episode use with attributed outcomes**. Once you have that, frontier opening becomes an ordinary state transition, not magic.

The right ladder is:

`candidate path → projection-verified → grounded/constructed → provisional episode witness → durable episode evidence → frontier rule opened`

and the right failure path is:

`opened rule → same-family loop/backfire/contradiction → quarantine`

That is cleaner than the classical architectures. My confidence on this framing is high$_{85%}$.

One nit: do **not** say Soar gives you staged frontier growth “for free.” It doesn’t. It gives you automatic compilation plus later fixes for generality. Your explicit `:frontier` tier is more deliberate than the standard Soar story.

## 8. Multi-model routing as cognitive economics

This part is genuinely beyond Mueller. He explicitly excluded metacognition. So don’t pretend this is faithful reconstruction. It is a post-Mueller extension.

The right theoretical frame is still the old one: **Nelson & Narens monitoring/control**. Monitoring estimates the state of ongoing cognition; control allocates effort, time, or strategy. Their retrieval discussion is especially relevant: a preliminary “feeling of knowing” judgment can decide whether to keep searching or terminate retrieval. That is almost a perfect analogue of your future “keep verifying this candidate path vs escalate vs abandon” decision. More recent metacognition work keeps the same split: monitoring receives input from the cognitive processes it influences, then evaluates candidate control options. A recent 2026 survey on artificial metacognition defines the AI version in almost your intended terms: systems that self-monitor and/or regulate resources, with object-level processes and meta-level processes clearly separated. ([UCI Social Sciences][5])

There is also direct architecture prior art. **FAtiMA Toolkit** explicitly describes a metacognitive model that affords different internal reasoners and affective components, plus an explicit dialogue structure for managing complex scenarios. That is not model routing, but it is a real example of an architecture that separates object-level social/emotional reasoning from meta-level selection and authorial control. So FAtiMA is a better ancestor for your Q8 than Mueller is. ([arXiv][6])

On the LLM side, routing has moved fast. **Route to Reason (RTR)** dynamically allocates both models and reasoning strategies under budget constraints, reporting higher accuracy than the best single model while cutting token use by over 60%. **BEST-Route** chooses both which model to use and how many responses to sample, with reported cost reductions up to 60% and less than 1% performance drop. A 2025 EMNLP paper formulates routing as a **budget-constrained contextual bandit** with online feedback instead of assuming you can label the best model for every query in advance. So the technical prior art for adaptive routing is already there. What is missing in that literature is your extra source of information: **internal cognitive state**. Those papers mostly route on input-query difficulty. Your kernel can route on process difficulty. ([arXiv][7])

That is the main opportunity. Your current `runtime_thought.clj` router is not cognitive economics yet. It is a bootstrap heuristic: fixed model, or family-based escalation plus branch events. Useful, but blunt. The benchmark packet already exposes much richer signals: `selected_goal`, `top_competing_goals`, `branch_events`, `emotional_state`, `retrieved_fragments`, `allowed_residue_indices`, and `previous_residue_summary`. And the rest of the kernel already computes even richer ones that are not yet in the routing packet: threshold slack, provenance dependence, retention flags, promotion transitions, rule-access transitions, and soon verified-path obligations.

So the right answer to Q8 is not “route to the expensive model for reversal.” It is:

**monitor internal ambiguity, risk, and stakes; then control model/strategy accordingly.**

The feature set I would actually use is:

**1. Verification ambiguity.**
This is the big one. If a candidate path has binding conflicts, many open obligations, or a large gap between projection-verification and grounded verification, you are in hard reasoning territory. That is where escalation belongs. This is exactly where 08’s verification lattice becomes useful. Cheap model for structural candidate search, stronger model for witness construction or denotational adjudication.

**2. Retrieval ambiguity.**
If the top two episodes are close, if retrieval only barely crossed threshold, if provenance bonus is doing most of the work, or if same-family capping is active, then the kernel is uncertain about what memory should govern the next move. That is a routing signal, not just a retrieval stat.

**3. Memory-risk signals.**
Any anti-residue flags on candidate source episodes, any pending durable/provisional decision, or any possible `:frontier ↔ :accessible ↔ :quarantined` transition should raise stakes immediately. Those are not cheap-output situations. A bad judgment there changes future cognition.

**4. Emotional pressure and competition.**
High trigger-emotion strength, steep pressure growth, and small margins between top competing goals all indicate a fragile control state. In your terms: when concern competition is close, routing errors change what the system thinks about next.

**5. Action criticality.**
Runtime-thought prose is low stakes. Family evaluator output is medium stakes. Path verification that could open a frontier rule, adjudicate contradiction, or support an event commit is high stakes. Route on that before you route on family.

**6. Budget/history signals.**
How often have you escalated recently? Did two cheaper passes disagree? Did the previous cycle already use the expensive model? Budget should shape control, but only after stakes and ambiguity.

That suggests a monitor vector like:

`[stakes, verification-ambiguity, retrieval-ambiguity, memory-risk, affective-pressure, budget]`

and a control output like:

`{model, reasoning_strategy, n_samples, max_tokens}`

Notice `reasoning_strategy` there. That matters. Modern routing papers are right to route not only the base model but also the **test-time reasoning policy**. Sometimes you want a bigger model. Sometimes you want the same model with more samples or a verifier pass. Those are different costs with different returns. ([arXiv][7])

So I’d make the first real routing policy **typed and lexicographic**, not learned:

1. If decision can change admission or rule access, escalate.
2. Else if verified-path ambiguity is high, escalate verifier, not narrator.
3. Else if runtime thought only, stay cheap unless branch events or affective pressure spike.
4. Else if evaluator output is near a decision boundary, use either a stronger model or multi-sample vote.
5. Else stay cheap.

Only after you log enough outcomes should you move to a contextual bandit or learned router. The 2025 routing literature is useful there, but hand-authored policy is the correct phase-1 move because your signals are architecture-specific and interpretable. ([ACL Anthology][8])

One more nit: I would not let the router see only surface packet fields forever. The architecture already has better signals than text-level difficulty. Add these to the monitoring packet when you build verified-paths:

* `verification_level`
* `verified_prefix_length / total_hops`
* `open_obligation_count`
* `binding_conflict_count`
* `retrieval_margin`
* `threshold_slack`
* `anti_residue_flag_count`
* `pending_rule_access_transition?`
* `pending_admission_transition?`

Those are the real escalation triggers. “Reversal family” is just a lossy proxy.

## Bottom line

For Q7:
**Soar chunking is an existence proof that automatic compilation can work, and a warning that it can also go bad.** The good version needs explanation-based summarization, not naive chunk accretion. ACT-R is a weaker analog because it mostly compresses local sequences and proliferates specialized rules. Your explicit `:frontier` registry plus episode-use evidence is cleaner than either. ([Soar][9])

For Q8:
**Model routing should be meta-level control over cognitive effort, not family-based load balancing.** The right theory is Nelson & Narens monitoring/control; the right architectural cousin is FAtiMA’s metacognitive layer; the right ML prior art is adaptive routing over models and reasoning strategies. But your distinctive advantage is that the kernel can monitor internal process state, not just prompt difficulty. ([UCI Social Sciences][5])

The concise design claim is:

**Frontier growth should be opened by verified episode evidence.
Routing should be driven by ambiguity and stakes in that verification/evidence loop.**

That seems right$_{90%}$.

Look away for 20 seconds. Unclench your shoulders.

[1]: https://arxiv.org/pdf/2205.03854 "https://arxiv.org/pdf/2205.03854"
[2]: https://iiif.library.cmu.edu/file/Newell_box00016_fld01185_doc0003/Newell_box00016_fld01185_doc0003.pdf "https://iiif.library.cmu.edu/file/Newell_box00016_fld01185_doc0003/Newell_box00016_fld01185_doc0003.pdf"
[3]: https://act-r.psy.cmu.edu/wordpress/wp-content/themes/ACT-R/workshops/2001/talks/pgss.pdf "https://act-r.psy.cmu.edu/wordpress/wp-content/themes/ACT-R/workshops/2001/talks/pgss.pdf"
[4]: https://arxiv.org/abs/2510.09355 "https://arxiv.org/abs/2510.09355"
[5]: https://sites.socsci.uci.edu/~lnarens/1990/Nelson%26Narens_Book_Chapter_1990.pdf "https://sites.socsci.uci.edu/~lnarens/1990/Nelson%26Narens_Book_Chapter_1990.pdf"
[6]: https://arxiv.org/pdf/2103.03020 "https://arxiv.org/pdf/2103.03020"
[7]: https://arxiv.org/abs/2505.19435 "https://arxiv.org/abs/2505.19435"
[8]: https://aclanthology.org/2025.findings-emnlp.1301.pdf "https://aclanthology.org/2025.findings-emnlp.1301.pdf"
[9]: https://soar.eecs.umich.edu/soar_manual/04_ProceduralKnowledgeLearning/ "https://soar.eecs.umich.edu/soar_manual/04_ProceduralKnowledgeLearning/"
