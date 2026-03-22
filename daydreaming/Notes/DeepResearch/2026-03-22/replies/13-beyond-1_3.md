Here’s the outside-knowledge answer to Q1–3, tied to your live kernel.

The short thesis is:

1. keep the `RuleV1` graph structural and typed
2. put fluid interpretation and evaluation in layers above it
3. admit induced rules through a probation system, not straight into planning

If you blur those three, you get graph soup.

## 1. Heterogeneous typed-graph serendipity

There is real prior art for useful discovery over heterogeneous typed graphs. It mostly comes from biomedical reasoning, scholarly synthesis, and design-by-analogy, not from “creative cognition” systems in the narrow sense. ARAX does graph-based biomedical reasoning by scoring result subgraphs over a standardized interface, and SPOKE integrates 21 node types and 55 edge types from dozens of sources to support emergent, multi-source discovery. In design, knowledge-graph systems for analogy retrieval use ontologies, semantic matching, and explicit analogical value models to find distant but still feasible source-target bridges. Joel Chan’s discourse-graph work is the closest thing to a prose-side analogue: questions, claims, and evidence are typed nodes linked by rhetorical relations, and the system treats those typed links as the substrate for synthesis rather than as retrieval metadata. ([OUP Academic][1]) ([OUP Academic][2]) ([Cambridge University Press & Assessment][3]) ([arXiv][4])

That maps cleanly onto your current kernel state. Right now you have candidate adjacency from schema overlap, retrieval-side provenance bonuses, and a small wrapper-heavy family graph. That is enough to prove plumbing, not enough to produce interesting serendipity. The outside literature says the missing ingredient is not “more edges” in the abstract. It is typed cross-phase heterogeneity with explicit path semantics. In your terms, that means rules around retrieval requests/results, episode evaluation, episode use/outcome, aftermath, and maybe explicit verification witnesses, not just more family trigger/request/dispatch wrappers.

On Copycat: no, fluid concepts and typed graphs are not fundamentally incompatible. Copycat’s point was that similarity and descriptor choice are context-dependent, with a dynamic Slipnet of concepts whose proximities shift during reasoning, plus a workspace and coderack that propose local interpretations. That is a complaint about frozen descriptors, not about explicit structure. The compatible decomposition for your repo is: static structural graph for candidate adjacency, plus an ephemeral “slippage” layer that can propose temporary descriptor rewrites, soft type aliases, or bridge facts before path search. What you should not do is make the permanent graph itself fluid or usage-weighted. That would destroy the property you care about, namely that neglected structural paths remain discoverable. ([Gwern][5]) ([Gwern][5]) ([Gwern][5])

So the right Copycat import is not “replace typed rules with fluid descriptors.” It is “add a pre-search reinterpretation workspace.” Concretely, for your kernel, that suggests an ephemeral object like:

```clojure
{:descriptor-bridge/id ...
 :source-fact-type :family-affect-state
 :target-fact-type :emotion-shift
 :rewrite {:affect :hope -> :positive-reappraisal}
 :support [:current-context ... :retrieved-episode ...]
 :status :ephemeral}
```

This object should be queryable during path search and verification, but not admitted into `build-connection-graph` unless it later survives repeated use and explicit validation.

On the graph-density question, do not look for one magic phase transition. That is the wrong abstraction. Your graph is heterogeneous and query-conditioned, so raw edge density is almost useless. HIN and meta-path work treat schema-level path types as the real unit, and recent long-range heterogeneous search work treats combinatorial blow-up as the central problem. Explainable path-reasoning work also keeps converging on short paths, typically 2 to 3 hops, because they are cheaper to search and still legible as explanations. That lines up with your repo’s 2–4 hop corridor. ([VLDB][6]) ([VLDB][6]) ([NeurIPS][7]) ([ScienceDirect][8])

So the practical answer is:

* do not target a scalar “good density”
* target bounded typed branching under admissible meta-paths
* measure candidate-path count, verified-path yield, and useful-path yield for sampled cross-phase queries

For your kernel, I’d use this heuristic$_{70%}$:

* **too sparse**: median sampled cross-phase query pair has 0 to 1 candidate 2–4 hop paths
* **promising**: median has 3 to 20 candidates, and at least ~10% survive verification
* **soup**: median has 50+ candidates, with <5% verification or usefulness yield

That is not a theorem. It is an engineering guardrail.

Concrete design guidance for Q1:

1. **Do not touch `build-connection-graph` semantics.** Keep it structural.
2. **Add cross-phase rule classes before adding more family wrappers.** Retrieval cue/result, episode evaluation, episode use/outcome, anti-residue, concern aftermath.
3. **Add an ephemeral descriptor-slippage layer.** Copycat import goes here, not into the permanent graph.
4. **Use `:rule-access` aggressively.** Planning graph stays sparse; serendipity graph can see frontier rules.
5. **Track schema-level health metrics.** Typed out-degree, sampled 2–4 hop path counts, verified-path yield, useful-path yield, and redundancy of meta-paths.
6. **Keep explanation path-based.** If a bridge cannot be named as a concise typed path, it is probably not a good bridge.

## 2. Systematic LLM evaluator insertion

There is surprisingly little direct prior art for “systematic evaluator insertion into an existing cognitive architecture.” Most ACT-R/Soar/LLM work is looser than what you want. One line of work uses LLMs to translate between ACT-R and Soar models. Another, LLM-ACTR, injects ACT-R trace-derived information into an LLM to get more human-like decisions. CogRec builds a bidirectional bridge where Soar state conditions prompt LLM generation and symbolic rules are extracted back from LLM outputs. There are also appraisal-style architectures where an LLM explicitly computes emotion/appraisal state. All of that is adjacent, but it is not the same as your “bounded judgment site inside a structural loop” design. In that sense, your architecture is cleaner than most of the literature, not behind it. ([SBP Brims][9])

The best outside taxonomy here comes from the LLM-as-judge literature: pointwise, pairwise, listwise, plus single-model, multi-model, and human-AI configurations. The practical takeaway is simple. Pointwise is cheap and scalable. Pairwise is useful for tie-breaking but grows badly with candidate count. Multi-model and human-AI setups can improve coverage, but they cost more and are harder to coordinate. ([arXiv][10]) ([arXiv][10])

That gives you a concrete evaluator policy:

* **pointwise by default**
* **pairwise only after structural narrowing**
* **listwise only offline or for tiny k**
* **multi-model only for high-value bottlenecks**

Per-cycle global judges are wrong$_{90%}$. They are expensive and vague. Your kernel already has the right instinct: evaluate at specific bounded objects.

For the current codebase, the rollout should be:

1. **post-plan episode evaluator**
   This is already the right first seam. The object is bounded, blast radius is low, and the result plugs directly into retention/admission metadata.

2. **path-usefulness evaluator**
   After candidate `bridge-paths`, before expensive full verification. This keeps judge calls off the raw graph fanout.

3. **analogy aptness / repair-vs-reject evaluator**
   Only after you have a real analogical transfer object, not before.

4. **rationalization backfire evaluator**
   Judge the generated reframe, not the whole family activation.

5. **mutation triage evaluator**
   Only on top-N mutations after structural mutation generation exists.

That order matches your repo’s live build order. It also avoids the current failure mode where the evaluator risks becoming a generic authority on memory policy. In your current first pass, evaluator output can still directly yield `:durable`. I would treat that as temporary and wrong$_{85%}$. The evaluator should gate or veto promotion. Durable promotion should come from downstream evidence in `note-episode-use` / `resolve-episode-use-outcome`, with the evaluator as one signal, not the sovereign.

Granularity wise, use a funnel:

* structural retrieval / matching reduces to top-k
* cheap pointwise judge scores each candidate
* only ambiguous or high-impact cases escalate to a stronger model or pairwise comparison

CogRouter’s monitoring/control framing is relevant here: adaptive cognitive depth can reduce token use while improving outcomes. That is close to what you want for evaluator routing. ([arXiv][11])

On NL2GenSym, the mapping is strong. Their loop is generator → immediate execution in Soar → reflective critic → revised rule, with >86% task success reported. For your architecture, the exact same shape applies, but the judged object can be an episode, a candidate path, or a candidate rule. The key is that the critic should see symbolic traces, denotation checks, effect programs, or sandbox outcomes, not just free text. “Execution-grounded” in your repo should mean:

* `RuleResultV1` shape validated
* effect ops validated
* denotation checked
* sandbox execution or branch replay run
* later use/outcome evidence logged

Then the evaluator judges that packet. That is much stronger than asking the model to bless raw prose. ([arXiv][12])

## 3. Rules the system creates

Yes, there is prior art beyond NL2GenSym.

GRAIL learns action symbols and PDDL-style rules, then validates them in simulation. LeSR samples KB subgraphs, has an LLM propose rules, then refines them with a rule reasoner to suppress hallucinated or weak rules. RIMRULE distills compact rules from failure traces, consolidates them with an MDL objective favoring generality and conciseness, and stores them in both natural-language and structured symbolic form for later reuse. SymAgent treats a KG as a dynamic environment and extracts symbolic rules to guide planning and discover missing facts. Across ontology/rule-engineering work, the repeated result is boring but important: LLMs are good at proposing candidates, bad at respecting ontological constraints on their own, and they need external validation plus expert or symbolic correction. ([OpenReview][13]) ([arXiv][14]) ([arXiv][15]) ([arXiv][16]) ([Semantic Web Journal][17])

That means your current `RuleV1` discipline, schema + denotation + executor, is necessary but not sufficient.

You asked if schema + denotation + executor validation is enough before graph admission. No. You also need execution testing. The good admission ladder for induced rules is:

1. **schema validity**
   `validate-rule!`, antecedent/consequent shape, allowed executor/effect ops.

2. **denotation validity**
   `:validation-fn`, declared failure modes, intended-effect coherence.

3. **sandbox executability**
   Effect program resolves symbolic refs, no illegal mutations, no impossible context surgery.

4. **behavioral tests**
   Positive and negative contexts. The rule should fire where intended and stay silent elsewhere.

5. **support across distinct traces**
   Not one lucky episode. Multiple independent supporting use/outcome records.

6. **novelty / subsumption / compression check**
   Reject rules that are just verbose duplicates of an existing accessible rule. Favor short rules that compress repeated successful traces. This is where RIMRULE’s MDL lesson matters.

7. **frontier admission only**
   Generated rules enter with `:deployment-role :frontier`, never directly `:accessible`.

8. **promotion by downstream evidence**
   Only rules implicated in later durable, non-flagged episodes become planner-visible.

This is exactly what your current `:rule-access` machinery wants to be. Use it.

So the quality discipline for a growing rule base should be:

* **frontier by default**
* **accessible by evidence**
* **quarantine on contradiction/backfire**
* **periodic frontier garbage collection**
* **duplicate/subsumption pruning**
* **compression bias**

A concrete extension to `RuleV1` that fits your repo:

```clojure
:provenance {:kernel-status :induced
             :deployment-role :frontier
             :induction-method :llm-generalization
             :supporting-episode-ids [...]
             :supporting-use-ids [...]
             :parent-rule-path [...]
             :validation-artifacts {:sandbox-pass-rate 0.83
                                    :negative-tests 12
                                    :novelty-score 0.41}}
```

That gives you something to reconcile with `reconcile-episode-rule-access` later.

The first induced rules in this repo should **not** be broad planning rules. That is too much blast radius. Start with narrow bridge rules or evaluator-side rules extracted from repeated use/outcome patterns. Example: if multiple durable episodes show the same cross-family transition with similar factual support and similar positive outcomes, induce a narrow bridge rule between those fact types. That is safer than inducing a new family planner.

If you want one sentence version of Q3: make the system create rules the same way you want it to create memories, namely through staged admission backed by attributed downstream success, not by one impressive generation.

## Bottom line

For your live kernel, the next high-value moves are:

* add cross-phase typed rule classes before expanding family wrappers
* keep evaluators local, pointwise, and downstream of structural narrowing
* treat evaluator output as a gate, not the source of durable truth
* admit generated rules to `:frontier`, never straight to `:accessible`
* add a Copycat-like ephemeral descriptor workspace, not fluid permanent edges

That stack seems right$_{80%}$.

Look away for a moment. Relax your shoulders.

[1]: https://academic.oup.com/bioinformatics/article/39/3/btad082/7031241 "https://academic.oup.com/bioinformatics/article/39/3/btad082/7031241"
[2]: https://academic.oup.com/bioinformatics/article/39/2/btad080/7033465 "https://academic.oup.com/bioinformatics/article/39/2/btad080/7033465"
[3]: https://www.cambridge.org/core/journals/ai-edam/article/knowledge-graphassisted-designbyanalogy-promoting-product-innovation-through-structured-analogical-knowledge-retrieval/9A6F1AFD04B956722EAFB62DEDC9ECFD "https://www.cambridge.org/core/journals/ai-edam/article/knowledge-graphassisted-designbyanalogy-promoting-product-innovation-through-structured-analogical-knowledge-retrieval/9A6F1AFD04B956722EAFB62DEDC9ECFD"
[4]: https://arxiv.org/abs/2407.20666 "https://arxiv.org/abs/2407.20666"
[5]: https://gwern.net/doc/ai/1990-mitchell.pdf "https://gwern.net/doc/ai/1990-mitchell.pdf"
[6]: https://www.vldb.org/pvldb/vol15/p3807-sun.pdf "https://www.vldb.org/pvldb/vol15/p3807-sun.pdf"
[7]: https://neurips.cc/virtual/2024/poster/94053 "https://neurips.cc/virtual/2024/poster/94053"
[8]: https://www.sciencedirect.com/science/article/pii/S1110016825012050 "https://www.sciencedirect.com/science/article/pii/S1110016825012050"
[9]: https://sbp-brims.org/2025/papers/working-papers/2025_SBP-BRiMS_paper_26.pdf "https://sbp-brims.org/2025/papers/working-papers/2025_SBP-BRiMS_paper_26.pdf"
[10]: https://arxiv.org/html/2412.05579v2 "https://arxiv.org/html/2412.05579v2"
[11]: https://arxiv.org/html/2602.12662v1 "https://arxiv.org/html/2602.12662v1"
[12]: https://arxiv.org/abs/2510.09355 "https://arxiv.org/abs/2510.09355"
[13]: https://openreview.net/forum?id=oyXoGJQlUf "https://openreview.net/forum?id=oyXoGJQlUf"
[14]: https://arxiv.org/abs/2501.01246 "https://arxiv.org/abs/2501.01246"
[15]: https://arxiv.org/abs/2601.00086 "https://arxiv.org/abs/2601.00086"
[16]: https://arxiv.org/abs/2502.03283 "https://arxiv.org/abs/2502.03283"
[17]: https://www.semantic-web-journal.net/system/files/swj4001.pdf "https://www.semantic-web-journal.net/system/files/swj4001.pdf"
