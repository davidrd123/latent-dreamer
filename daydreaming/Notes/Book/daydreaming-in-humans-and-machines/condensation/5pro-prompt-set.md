# 5 Pro Deep Research Prompt Set

Five prompts, each standalone. Run with the same attachment set (architectural-framing.md, cross-cut-summary.md, chain traces, rule schema with worked examples, connection graph, and mechanism cards 09/12/13/17). Each prompt is grounded in specific mechanisms and asks a question that's bigger than implementation but smaller than pure philosophy.

---

## Prompt 1: Novelty and Falsification

**The believer/skeptic pair in one prompt.**

Here is a system where an LLM generates content inside a persistent cognitive architecture. The Clojure kernel maintains a rule connection graph (structurally derived, not usage-weighted), episodic memory with coincidence-mark indexing, a reminding cascade that propagates individual indices, and an immutable context tree for hypothetical branching. The LLM provides judgment at typed call sites within those structural loops. The system accumulates — episodes grow, rules can be created, the connection graph gets richer, serendipity search space expands.

**Question 1a (believer):** Is this combination genuinely novel? Mueller had the cognitive architecture (1990). LLM agents have generation. Ori-Mnemos has persistent memory with cognitive decay. RLMs have recursive decomposition. Cognitive architectures (SOAR, ACT-R) have been integrated with LLMs. Where exactly is the novelty line? Is it:
- The specific combination (structural serendipity + LLM judgment + accumulation)?
- The code-is-data rule schema where rules live in a searchable graph AND invoke LLMs?
- The accumulation story where LLM-generated content crystallizes into persistent structure that changes future creative capacity?
- Something else?

**Question 1b (skeptic):** What evidence would convince a hard skeptic that this is more than narrative dressing around generation? What observable behaviors would distinguish this system from a well-prompted LLM with RAG? Specifically:
- What would a system with structural serendipity produce that a system with embedding-based retrieval would not?
- What would accumulation across sessions produce that re-prompting with a summary would not?
- What experiment or observation would falsify the claim that the persistent structure adds genuine creative capability?

The attached rule schema shows concrete typed interfaces. The worked examples (Kai's letter, hidden blessing rationalization) show the same rule with `:instantiate` and `:llm-backed` executors. The question is whether that architectural distinction produces a measurable difference in output, or whether it's engineering elegance without functional consequence.

---

## Prompt 2: Creativity Through Structural Serendipity

**What does graph-based creative discovery enable that nothing else does?**

Mueller's serendipity is not fuzzy retrieval or temperature-based randomness. It is: find a path through the rule connection graph from a rule associated with an active concern to a rule associated with something currently salient. The path exists structurally (rules connected through antecedent/consequent pattern overlap) but wasn't being looked for until a reminding cascade or mutation made a particular concept salient at the right moment. The path is then verified step by step through progressive unification.

The connection graph is static — structurally derived, not usage-weighted. A path never traversed is equally findable. This is the property that makes serendipity different from retrieval: it finds PATHS, not ITEMS.

**Questions:**
- What kinds of creative discovery does structural path-finding enable that associative retrieval (embedding similarity, spreading activation) does not? Give concrete examples of the kind of connection serendipity could find that retrieval could not.
- How does serendipity relate to existing creativity theories? Is it an implementation of Koestler's bisociation (connecting two previously unconnected frames)? Boden's exploratory creativity (searching a conceptual space)? Something distinct?
- What happens to serendipity at scale? Mueller had ~135 rules. With 1,000 or 10,000 rules, does the combinatorial explosion of possible paths make serendipity better (more connections to find) or worse (more noise)? Is there a scaling relationship between graph density, path length, and discovery quality?
- Directed vs. undirected: Mueller studied undirected daydreaming. This project gives the system a creative brief. The brief constrains the concern space, but serendipity needs freedom. What's the theory of directed daydreaming? How do you constrain enough to be relevant without killing the non-obvious discoveries?

The attached mechanism card 13 (serendipity recognition) and the rule connection graph document describe the mechanism precisely. The chain trace B shows how serendipity fits into the accident-driven execution chain.

---

## Prompt 3: Provenance, Self-Deception, and the Episodic Memory Problem

**What happens when a system accumulates its own imaginative output as memory?**

Mueller's system stores both real and imagined episodes. A rationalization generates an imagined scenario — "maybe the failure was actually a hidden blessing" — and if that scenario is stored, future retrieval will encounter it alongside real experiences. The reminding cascade doesn't distinguish provenance during propagation. The episode's indices activate regardless of whether the episode was real or imagined.

In the hybrid version, the LLM generates richer imagined content. A rationalization reframe, a revenge fantasy, a rehearsal of a future conversation — all get stored as typed episodes with planning tree structure, indices, realism/desirability scores. They enter episodic memory. They participate in future retrieval and analogical planning.

**Questions:**
- What's the relationship between creative productivity and epistemic reliability in a system that accumulates its own output? How do you build a mind that daydreams productively without believing its own fantasies?
- Mueller has a real-or-imagined flag and realism scores. Are those sufficient, or does the LLM's richer content generation make the provenance problem qualitatively harder?
- Does rationalization (mechanism 17) constitute a meaningful form of self-relation? The system generates a reframe of its own failure, stores it, and later retrieves it with reduced negative affect. Structurally, that's how human rationalization works. Is there something real happening here, or is it just structured output variation?
- What does it mean for a system to re-encounter its own past through reminding cascades? An episode stored three weeks ago surfaces because today's indices overlap. The associated emotions reactivate. The episode may trigger serendipity. This isn't lookup — it's re-encounter. What kind of relation to its own history does that create?

The attached mechanism cards for rationalization (17), reminding cascade (12), and episode evaluation (18) show the specific structural paths. The rule schema worked example B (hidden blessing) shows exactly how a rationalization gets generated, validated, and stored.

---

## Prompt 4: Emotion as Cognitive Infrastructure

**Reframing AI emotion from affect display to control architecture.**

Most AI emotion work is about generating or recognizing affect — sentiment in text, expression in faces, tone in speech. Mueller's emotions are none of that. They are typed routing infrastructure:

- Emotions drive concern selection (what gets thought about next)
- Specific emotion configurations activate specific daydreaming goal families (negative + directed → revenge; negative + failure → rationalization; positive + active goal → rehearsal)
- Emotions get stored with episodes and reactivated by the reminding cascade, so past emotional associations bias future retrieval
- Emotions decay but persist across cycles, creating temporal continuity

In the hybrid architecture, the LLM provides richer emotional appraisal at judgment points (mechanism 15: realism assessment, attribution, affect classification). But the structural role — typed motivational state that persists, decays, drives the control loop, and gets indexed in episodes — stays in Clojure.

**Questions:**
- Is there a paper-length argument that emotion in AI systems should be implemented as control infrastructure rather than output modulation? What would that argument look like, and what existing literature supports or contradicts it?
- Mueller's emotion types (Tables 3.1-3.2: hope, worry, relief, regret, anger, gratitude, etc.) are distinguished by structural features (toward-person, from-goal, to-goal, altern flag). These features determine which daydreaming goal activates. Is this a good model for how emotion connects to action tendency in modern affective science, or has the field moved past it?
- What's the relationship between Mueller's emotion-driven control and predictive processing / active inference frameworks? Is concern selection a form of precision-weighted prediction error minimization? Does the emotional feedback system (daydream about failure → generate negative emotion → that emotion activates rationalization → rationalization reduces the emotion) look like a prediction error minimization loop?
- In the hybrid version, the LLM provides richer affect classification. But is that actually useful? Mueller's structural emotion types (positive/negative × directed/undirected × altern/non-altern) are coarse but they drive the control loop effectively. Does richer affect classification improve the routing, or just produce more expressive text?

---

## Prompt 5: Code-Is-Data and the Accumulation Engine

**Why Clojure's code-is-data matters for persistent cognitive structure, and what accumulation across sessions actually means.**

The rule schema uses Clojure maps where `:antecedent-schema` and `:consequent-schema` are plain data (patterns with logic variables). The connection graph is computed from pattern overlap between these data structures. Rules can be created at runtime (especially through REVERSAL — counterfactual replay that produces new planning rules). New rules enter the connection graph and change what serendipity paths exist.

This means the system's creative capacity grows through its own operation:
- An episode gets stored → its indices participate in future retrieval
- A reversal creates a new rule → that rule enters the connection graph → new serendipity paths become possible
- A serendipitous discovery becomes a stored episode → indexed under the rules that produced it → available for future analogical planning

The LLM contributes content that crystallizes into persistent structure. Not just text in a database — typed planning trees, indexed episodes, searchable rules, traversable graph connections. The structure changes what the system can discover and reuse in future sessions.

**Questions:**
- What does compounding cognitive capacity look like over time? If the system runs for months, what does the connection graph look like? Does it develop something like cognitive style — preferred associative patterns, characteristic serendipity paths? If two instances run with different experiences, do they diverge into recognizably different creative profiles?
- What's the relationship between structural accumulation and what we call "development" in humans? Humans develop cognitive style, expertise, creative habits through accumulated experience. Is this a meaningful analog, or a surface resemblance?
- The code-is-data property means rules are inspectable, modifiable, and composable at runtime. A rule can be created by one mechanism (REVERSAL), stored by another (episode storage), found by another (serendipity), and applied by another (analogical planning). That compositional lifecycle is what makes accumulation compound rather than just grow. Is this the genuinely load-bearing architectural choice? If you had to strip the system to its essential properties, is "rules as persistent searchable data that accumulate and participate in graph search" the one that can't be lost?
- What's the minimum viable accumulation loop? Not the full 19-mechanism architecture — the smallest cycle that demonstrates compounding cognitive capacity. Is it: generate episode → store with indices → retrieve via coincidence → discover serendipitous connection → store new rule → richer graph → better future discovery? If so, what's the simplest implementation that proves this cycle works?

---

## Attachment list (same for all five prompts)

1. architectural-framing.md
2. cross-cut-summary.md
3. chain-trace-a.md
4. chain-trace-b.md
5. kernel-rule-schema-and-execution-model.md
6. rule-connection-graph.md
7. mechanisms/13-serendipity-recognition.md
8. mechanisms/09-analogical-rule-application.md
9. mechanisms/17-rationalization-strategies.md
10. mechanisms/12-reminding-cascade.md
