Got it. No repo recap.

My current read: the architecture is directionally right$_{70%}$, but the `consequent-schema/executor` split is one abstraction short, and the working-memory story is too permissive.

## 1) What the Soar + LLM work actually learned

The strongest Soar-adjacent results do **not** hand the runtime loop to the model. They use the model as an unreliable knowledge source, then let the architecture analyze, repair, select, and validate. Kirk et al.’s STARS explicitly says prompt engineering alone is insufficient for situationally grounded task knowledge; their agent broadens the response space and then evaluates, repairs, and selects candidate LLM responses, reaching 77–94% one-shot task completion without user oversight and 100% with simple human confirmation/disconfirmation. Wray et al. do something similar one level up: the LLM maps natural language into a **semi-formal problem-space specification**, after which the cognitive system uses ordinary search and learning. ([arXiv][1])

The negative evidence is just as useful. Wu et al.’s prompt-enhanced ACT-R/Soar modeling paper found Bard produced ACT-R code with no ACT-R syntax at all, ChatGPT still made architecture-specific syntax mistakes, hallucinated a nonexistent “Soar Toolkit,” and needed human debugging rationales plus manual fixes for things like buffer use and motor commands. In other words, “sounds right” is cheap; “obeys architecture invariants” is not. ([ACS IST][2])

The positive rule-synthesis result, NL2GenSym, reinforces the same point. It only works because proposed rules are **immediately executed inside Soar** and then iteratively refined with execution-grounded critic feedback. That got them above 86% rule-generation success on their benchmark. So the lesson is not “LLMs can write symbolic rules.” The lesson is “LLMs can propose symbolic rules if execution is inside the loop and failure is first-class.” ([arXiv][3])

There is another important Soar-side lesson from Jones & Wray’s constraint-compliance work: once norms, soft constraints, hard constraints, partial observability, and online learning matter, **metacognitive judgment** becomes necessary. That pushes against any architecture that assumes typed outputs alone are enough. Typed outputs are not the same thing as constraint-compliant behavior. ([arXiv][4])

So the transfer is blunt: use the model for proposal, disambiguation, and repair. Do **not** let it directly own state mutation or rule creation unless execution-grounded validation is attached.

## 2) What ACT-R’s buffer system got right that this design misses

ACT-R got one thing very right: working memory is an **interface discipline**, not a dump of whatever state might be useful. Buffers are the interface between procedural memory and modules; each buffer holds one chunk; only one production fires per cycle. The modules can run in parallel/asynchronously, but the architecture forces a narrow attentional bottleneck. That buys you explicit focus, explicit bandwidth limits, and clean request/result semantics. ([ACT-R][5])

What your design seems to miss is the value of that bottleneck. Right now the judgment sites look too much like wide function calls over a lot of state. ACT-R’s lesson is that “available to the architecture” and “visible to this decision right now” are not the same thing. If every evaluator sees the whole concern/context/episode state, you lose attentional structure and you make it too easy for the model to short-circuit the architecture.

A second thing ACT-R gets right is **status-bearing module interfaces**. The procedural system does not peek inside a module. It sees buffer contents and status. Your design needs the analogue of busy / failed / no-result / stale / partial, not just input and output payloads. Otherwise latency and failure vanish from cognition and become invisible engineering details.

A third thing is the “recent echo” story. Thomson et al.’s buffer-decay extension shows why recently cleared items should still bias retrieval for a while. That is a more general version of your recent-index / recent-episode idea. The point is not just FIFO recency. It is that context should decay, not disappear. ([ACT-R][6])

That said, you should **not** copy ACT-R literally. Laird’s ACT-R vs Soar comparison makes clear that ACT-R’s fixed buffer set becomes a liability for more complex AI agents and pushes too much intermediate structure into declarative memory. So the right move is to steal the **narrow-interface principle**, not the exact ACT-R buffer inventory. ([Cognitive Systems Conferences][7])

## 3) CoALA compared to your architecture

CoALA is stronger than your current design in one respect: it gives a cleaner decomposition of a language agent into **working vs long-term memory**, **external vs internal actions**, and a repeated **observation → proposal/evaluation → selection/execution → learning** cycle. That is useful, and your architecture could borrow its action-space vocabulary directly. ([arXiv][8])

But CoALA is weaker where you actually need commitment. It is a **conceptual framework** for organizing language agents, not a mechanized architecture with invariants. It explicitly positions the LLM as the core component of a larger architecture. That is fine for tool-using agents. It is weak for a system whose whole point is persistent symbolic accumulation, graph correctness, recursive verification ownership, and provenance-preserving state change. CoALA does not tell you how to keep a learned graph coherent, how to govern schema evolution, or how to stop recursive evaluators from dissolving back into generic prompting. ([arXiv][8])

So: CoALA is stronger as a **taxonomy**. Your design is stronger where it insists that there is a persistent substrate whose shape matters. If you adopt CoALA too literally, you will probably loosen the architecture too much.

## 4) What knowledge-graph systems already ran into, and your connection graph will too

KG people have already paid tuition on this.

First, **incremental update pain**. Hofer et al.’s 2024 survey says KG construction is still largely batch-like, incremental updates are underexplored, and manual intervention still limits scalability. That maps directly to your rule-graph maintenance problem. Once rules are added online, cached edges and derived indices go stale fast. ([Database Group Leipzig][9])

Second, **quality and conflict pain**. The 2025 GraphRAG survey flags knowledge quality, knowledge conflict, and efficiency as central limitations. The specific problems are the ones you should expect: inconsistencies, redundancies, missing information, contradictory statements, source-reliability disputes, and the need for uncertainty modeling rather than binary truth labels. A connection graph with LLM-authored or LLM-extended rules will hit the same thing, just with “rule paths” instead of “facts.” ([arXiv][10])

Third, **traversal cost**. That same survey points to scalable subgraph matching, graph traversal, and reasoning as deployment bottlenecks. Your graph is smaller than a web KG, but the same shape of problem appears as branching-factor blowup and path-search cost once rules get even moderately generic. ([arXiv][10])

Fourth, **retrieval is not reasoning**. Zarrinkia et al.’s March 2026 Graph-RAG paper is the cleanest warning shot: in their evaluation, 77–91% of questions had the gold answer inside retrieved graph context, yet accuracy was only 35–78%, and 73–84% of errors were reasoning failures. So a graph that surfaces the right path does not solve the downstream reasoning problem. That matters a lot for your serendipity and verification story. ([arXiv][11])

Fifth, **schema/ontology evolution is its own hard problem**. Shimizu and Hitzler’s 2025 KGOE paper says ontology modeling, extension, modification, alignment, and entity disambiguation still resist automation at reasonable quality and scale, and still need expert labor. If you let rule schemas evolve freely at runtime, you are not “just adding rules.” You are doing ontology engineering. ([KASTLE Lab][12])

## 5) The consequent-schema / executor split: good idea, wrong decomposition

There is prior art for this split. The closest matches are **semantic attachments** and **PDDLStream**.

Dornhege et al.’s semantic-attachment work extends PDDL so planners can call external procedures for predicate evaluation, effects, and durations. PDDLStream does something similar with black-box samplers under a declarative specification, plus search algorithms that balance exploration and exploitation. So yes, there is a real tradition behind “declarative skeleton + opaque executor.” ([AAAI][13])

But the prior art is stricter than your split. Semantic attachments only preserve conditional soundness/completeness when the external procedures **terminate**, are **deterministic for identical inputs/states**, and crucially **do not secretly choose among multiple outcomes**. Dornhege et al. explicitly say effect applicators should not contain a mechanism for making choices between different outcomes. That is the sharpest outside challenge to your architecture I can make. If your `:llm-backed` executor is allowed to choose which materially different consequent to produce, then you have violated the exact condition under which this style of architecture keeps its guarantees. ([AAAI][13])

The other failure mode from that literature is cost. External calls are expensive, often used ad hoc, and early symbolic commitment can fail on lower-level execution details. Dornhege’s planning paper says precomputing all possibly relevant low-level facts is too time and memory consuming, while direct lower-level checking must be pulled into the planner carefully to avoid repeated execution failures and replanning. HTN semantic attachments say the same thing in another form: external calls are computationally expensive, so planners often fall back to specialized ad hoc tricks. ([AAAI][13])

Companion gives the language-side version of the same lesson. Their symbolic NLU stays because the representations are discrete and inspectable. BERT helps with disambiguation and plausibility filtering, and analogy + BERT raises precision from 45.7% to 71.4%, while BERT alone gets only 20.8%. Also, their text-simplification experiments exposed the exact bug your split is vulnerable to: fluent output that omits a crucial fact. Their example is an LLM simplification of a legal case that dropped the fact that police had a search warrant. That is not a type error. It is a **denotational failure**. ([Qualitative Reasoning Group][14])

So my verdict is direct: the split is plausible, but it is **one layer short**. You do not just need:

* schema
* executor

You need:

* **graph/interface schema**
* **denotational contract** for intended state change
* **executor**

Without that middle layer, you will admit lots of outputs that are schema-valid and cognitively wrong.

## Bottom line

The outside literature pushes in one direction:

* narrow the interfaces like ACT-R buffers
* treat LLMs like unreliable knowledge sources, not trusted runtime authors
* add execution-grounded validation like STARS / NL2GenSym / semantic attachments
* assume graph maintenance, conflict resolution, and ontology drift will become real problems
* and most importantly, split `schema/executor` into **schema / denotation / executor**

If you only change one thing, change that last part. It is the cleanest fix.

[1]: https://arxiv.org/abs/2306.06770 "https://arxiv.org/abs/2306.06770"
[2]: https://acs.ist.psu.edu/papers/wuSRLip.pdf "https://acs.ist.psu.edu/papers/wuSRLip.pdf"
[3]: https://arxiv.org/abs/2510.09355 "https://arxiv.org/abs/2510.09355"
[4]: https://arxiv.org/pdf/2405.12862 "https://arxiv.org/pdf/2405.12862"
[5]: https://act-r.psy.cmu.edu/wordpress/wp-content/uploads/2014/11/unit1.pdf "https://act-r.psy.cmu.edu/wordpress/wp-content/uploads/2014/11/unit1.pdf"
[6]: https://act-r.psy.cmu.edu/wordpress/wp-content/uploads/2015/09/CogSci-2014-Final-Extending-the-Influence-of-Contextual-Information-in-ACT-R-using-Buffer-Decay.pdf "https://act-r.psy.cmu.edu/wordpress/wp-content/uploads/2015/09/CogSci-2014-Final-Extending-the-Influence-of-Contextual-Information-in-ACT-R-using-Buffer-Decay.pdf"
[7]: https://advancesincognitivesystems.github.io/acs2021/data/ACS-21_paper_6.pdf "https://advancesincognitivesystems.github.io/acs2021/data/ACS-21_paper_6.pdf"
[8]: https://arxiv.org/abs/2309.02427 "https://arxiv.org/abs/2309.02427"
[9]: https://dbs.uni-leipzig.de/files/research/publications/2024-8/pdf/information-15-00509-with-cover.pdf "https://dbs.uni-leipzig.de/files/research/publications/2024-8/pdf/information-15-00509-with-cover.pdf"
[10]: https://arxiv.org/pdf/2501.13958 "https://arxiv.org/pdf/2501.13958"
[11]: https://arxiv.org/abs/2603.14045 "https://arxiv.org/abs/2603.14045"
[12]: https://kastle-lab.github.io/assets/publications/2024-LLMs4KGOE.pdf "https://kastle-lab.github.io/assets/publications/2024-LLMs4KGOE.pdf"
[13]: https://cdn.aaai.org/ocs/ws/ws0614/2056-8460-1-PB.pdf "https://cdn.aaai.org/ocs/ws/ws0614/2056-8460-1-PB.pdf"
[14]: https://www.qrg.northwestern.edu/papers/Files/QRG_Dist_Files/QRG_2023/nakos_forbus_fss23_aaai_camera_ready.pdf "https://www.qrg.northwestern.edu/papers/Files/QRG_Dist_Files/QRG_2023/nakos_forbus_fss23_aaai_camera_ready.pdf"
