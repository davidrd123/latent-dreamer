# Beyond Mueller: Research Questions on the Eight Future Directions

You have the full repo context. See `project-page/beyond-mueller-detailed.md` for the numbered list of directions. These questions ask for outside knowledge, prior art, and concrete design guidance on each one. Do NOT summarize the repo — bring things we don't have.

---

## 1. Heterogeneous graph serendipity

Mueller's serendipity searched a graph of ~135 rules in one domain. Our graph will have activation rules, planning rules, evaluation rules, retrieval events, and critic signals — different semantic phases.

- What prior art exists for creative discovery across heterogeneous typed graphs? Not GraphRAG (that's retrieval). Systems where paths through semantically different node types produce genuine insight.
- Hofstadter's Copycat used fluid concepts and context-sensitive descriptors. Is there work that combines Copycat-style conceptual fluidity with a typed graph substrate? Or are those fundamentally incompatible approaches?
- What's the expected graph density where serendipity transitions from "too sparse to find anything" to "useful cross-phase bridges" to "graph soup where everything connects to everything"? Is there empirical or theoretical work on this transition?

## 2. Systematic LLM evaluator insertion

The condensation identifies 10 of 19 mechanisms with evaluator-pattern judgment points. We want to systematically insert LLM evaluators at these points.

- Is there prior art for systematic "evaluator insertion" into an existing cognitive architecture? ACT-R subsymbolic computations, SOAR operator evaluation, FAtiMA appraisal modules — have any of these been augmented with LLM evaluators, and what worked?
- What's the right granularity for evaluator calls? One per mechanism per cycle? One per candidate? Batched? What do SOAR+LLM and ACT-R+LLM projects report about evaluator call frequency and cost?
- The NL2GenSym paper used execution-grounded critic feedback for SOAR rule generation. How does their critic loop translate to our evaluator pattern? Their critic validates proposed rules; our evaluators score episodes and paths. Different objects, same shape?

## 3. Rules the system creates

Mueller's REVERSAL creates new planning rules. We extend this: LLM-proposed generalizations of successful strategies, stored as RuleV1 objects that enter the connection graph.

- Beyond NL2GenSym, what other work exists on LLM-generated symbolic rules that enter a persistent knowledge base? Not prompt-time chain-of-thought — durable rules that accumulate.
- What validation is needed before a generated rule enters a connection graph? NL2GenSym uses execution-grounded validation. Our architecture has schema + denotation + executor validation. Is that sufficient, or is execution testing also needed (fire the rule, check the outcome, then decide whether to keep it)?
- How do you prevent generated rules from degrading graph quality? Too many vague rules → graph soup. Too few → no growth. What's the curation/quality discipline for a growing rule base?

## 4. Directed daydreaming

Mueller studied undirected daydreaming. We want directed daydreaming under a creative brief — constrain the question, not the surprise.

- Is there any cognitive science or AI work on DIRECTED daydreaming specifically? Not just "goal-directed thought" (that's planning) — daydreaming with a loose constraint where the cognitive machinery (serendipity, reminding, rationalization, reversal) operates within a thematic frame.
- The "one-end anchored search" idea (concern fixes the top, reminding/mutation leave the bottom open) — does this have analogs in creative problem solving research? Janusian thinking, remote associates, constraint relaxation?
- How does the creative brief interact with the daydreaming goal families? Does the brief constrain which families activate (only rehearsal and reversal, not revenge)? Or does it constrain the situation space while letting all families operate?

## 5. DSPy-style optimization of write/read

The GradMem paper showed: how you write memory matters. We want to optimize the residue write prompt against trace divergence.

- Has DSPy been applied to memory systems specifically? Not retrieval-augmented generation (that's read-side) — optimizing the WRITE side: what gets stored, how it's indexed, what residue is carried forward.
- What's the right metric for write optimization? "Trace divergence" measures that something changed, not that the change was good. What quality metrics would distinguish useful divergence from random noise?
- GradMem uses a reconstruction loss (can you reconstruct the context from the memory?). What's the token-space analog? "Can the LLM reconstruct the salient aspects of the cycle from the stored residue alone"?

## 6. Developmental trajectory

Two instances with different histories should diverge. We want to measure that divergence.

- Is there work on measuring "cognitive style" in artificial agents? Not personality questionnaires (those measure self-report) — observable behavioral divergence from shared starting conditions. Different repair tendencies, different association patterns, different retrieval biases.
- How does human cognitive development work at the level of accumulated experience shaping future cognition? Not developmental psychology in the milestone sense — more like "expertise development" or "cognitive habit formation." What structures accumulate and how do they change processing?
- If two instances diverge, how do you tell the difference between meaningful style (history-shaped associative structure) and random drift (different random seeds led to different paths)? What's the control experiment?

## 7. Accessibility frontier as growth

Rules start inaccessible. Serendipity finds them. Verification proves them. The planner gets new capabilities.

- Mueller's frontier is closest to Soar's chunking — new rules emerge from subgoal resolution. How does Soar's chunking work in practice at scale? Does the chunked rule base grow productively or does it degrade?
- ACT-R's production compilation is another analog. What are the known failure modes of automatic rule compilation in cognitive architectures? Over-specialization? Interference? Applicability decay?
- Is there work on "staged rule admission" in any rule-based system — where rules go through a probationary period before becoming fully available? Not just learning rate / eligibility traces — explicit staging with evidence requirements.

## 8. Multi-model routing as cognitive economics

The kernel controls which model handles which judgment. Haiku for routine, Sonnet for reversal. This could become adaptive.

- Is there prior art for adaptive model routing in cognitive architecture contexts? Not just "route to the cheapest model that can handle it" (that's load balancing) — routing based on cognitive difficulty assessed by the architecture itself.
- The metacognition literature (Flavell, Nelson & Narens) distinguishes monitoring (assessing difficulty) from control (allocating resources). Our kernel does monitoring (which operator is active, what's the emotional pressure) and could do control (escalate model when verification is ambiguous). Is there AI work that uses this monitoring/control framework explicitly?
- What structural signals from the kernel would be good escalation triggers? Binding conflicts in path verification? High emotional pressure? Anti-residue flags on the active concern? What's the right feature set for a routing policy?
