Good draft. The main correction is structural, not cosmetic:

**You’re missing the believable-agent / affective-agent lineage.**

If the question is “what else could be the base architecture for steerable characters with inner life?”, then Oz/Hap/ABL, FAtiMA, ALMA, and MicroPsi are closer substitutes than RL, utility AI, or generic game simulation. Loyall’s *Believable Agents* thesis is explicitly about interactive personalities; ABL is a reactive planning language for story-based believable agents; FAtiMA Toolkit is a modular socio-emotional agent stack; ALMA integrates emotions, moods, and personality; and MicroPsi/PSI treats emotion, motivation, and cognition as one architecture rather than bolt-ons. That row belongs in Ask A. ([CMU School of Computer Science][1])

I wrote a fuller pasteable addendum here: [Ask A addendum — deeper alternative foundations](sandbox:/mnt/data/ask_a_alternative_foundations_deeper.md)

Here’s the tighter version I’d actually inject into your Ask A draft.

## A better comparison rule

Don’t compare foundations by “symbolic vs neural.” Compare them by **control primitive**:

* predictive processing / active inference → minimize expected free energy
* narrative planning → satisfy author goals through causally coherent intentional plans
* RL + intrinsic motivation → optimize reward / novelty / skill diversity
* LLM-direct roleplay → maintain coherence through prompt-state, memory retrieval, and reflection
* believable / affective agents → run reactive behaviors modulated by appraisal, emotion, coping, and personality
* embodied / game AI → choose affordance-grounded actions under real-time constraints
* improv-first systems → accept offers, justify, and keep scene energy alive
* BDI → deliberate over beliefs, desires, intentions, and plan libraries

That framing gets you from vague “approach families” to actual architecture.

## 1. Predictive processing / active inference

A system built on active inference would treat a character as a **generative model with preferences, beliefs, uncertainty, and precision weights**. “Concern” becomes persistent prediction error or preference violation. “Attention” becomes precision allocation. “Mood” becomes long-timescale bias over inference and policy selection. “Action” and “thinking” are both ways of reducing expected free energy. The practical entry point is `pymdp`; newer multi-agent work explicitly models self/other beliefs and strategic interaction; the 2026 empathy paper pushes this toward perspective-taking and alignment. ([arXiv][2])

Architecturally, this gives you a character engine that is very good at **uncertainty, surprise, information-seeking, and social inference**. It is much less naturally good at **named, authorable coping modes**. You can derive things like avoidance or rehearsal, but they stop being first-class operators and become emergent policy patterns. That is elegant and annoying at the same time. It’s strongest for persistent daemons, embodied agents, or systems where perception and inference matter continuously. It is weaker when you want artistically legible discrete modes that compile cleanly into curated beat libraries. ([arXiv][3])

**Papers/systems to know:** Friston et al. 2017, `pymdp` 2022, Hesp et al. on valence/emotion in deep active inference, Ruiz-Serra et al. on factorised multi-agent active inference, Mahault et al. 2026 on empathy in active-inference agents. ([arXiv][2])

## 2. Narrative planning / story reasoning

A system built here treats story generation as **search over causal plans** with explicit author goals and character intentionality. Characters look agentic locally, but there is usually a centralized planning layer making sure the whole thing lands somewhere. IPOCL is the classic pivot: the planner must satisfy both plot coherence and character intentionality. Glaive pushes that into state-space planning with intentionality and conflict. Sabre goes further: it is an explicit centralized “puppetmaster” planner that still constrains every action to make sense under each character’s beliefs and intentions. ([Georgia Tech Faculty][4])

If you started here, the architecture would look like: authored world state + action schemas + character goals/beliefs + author goals → planner → intentional scene graph / branching story graph. Inner life exists, but largely as **planning constraints and explanatory structure**, not as a stream of cognition. The payoff is strong setup/payoff discipline, multi-situation coherence, explicit failed-plan reasoning, and good authoring-time compilation. The loss is that obsession, involuntary return, defensive cognition, and reminding cascades are not native. You can add them, but they are not the chassis. ([Georgia Tech Faculty][4])

**Papers/systems to know:** MINSTREL, MEXICA, Riedl & Young 2010, Glaive 2014, Sabre 2021, and the 2024 “The Story So Far on Narrative Planning” review. MINSTREL and MEXICA matter because they show two different ways to keep story generation from collapsing into flat planning: case-based analogical transformation in MINSTREL, and engagement/reflection in MEXICA. ([UCLA Calendar][5])

## 3. RL + intrinsic motivation + world models

A system built this way says: stop hand-designing the cognitive update laws; learn a policy. Curiosity, surprise, novelty, or skill diversity become intrinsic rewards. Pathak et al. operationalize curiosity as prediction error; Burda et al. show curiosity-driven learning can work at scale even with no extrinsic reward in many game domains; DIAYN learns diverse skills without external reward; DreamerV3 learns world models and improves behavior by imagining future scenarios. ([arXiv][6])

Architecturally, that means environment state + policy + intrinsic objective + maybe a learned world model. If you want “character,” you either shape the reward heavily or learn skills and map them post hoc to character-like behaviors. This can work, but it changes the authoring language: now you are secretly writing reward functions, curriculum, and observation spaces. The gain is adaptation, reusable skills, and possible tuning from performer/audience feedback. The loss is inspectability. If your value proposition is “watchable inner process,” this is a bad primary substrate and a plausible secondary optimization layer. ([arXiv][6])

**Papers/systems to know:** Pathak 2017, Burda 2018, DIAYN 2019, DreamerV3 2025. If you cite only one modern “this is what learned world-model control looks like” paper, cite DreamerV3. ([arXiv][6])

## 4. LLM as the cognitive engine directly

This is the dominant modern baseline, and you should treat it seriously, not as a strawman. The architecture here is usually: natural-language memory store + reflection summaries + retrieval + role prompt + planning loop. *Generative Agents* is the obvious canonical paper: memory, reflection, and planning over a simulated town. The role-playing surveys from 2024 map the design space cleanly. Newer work is trying to fix the exact failure modes you care about: role drift, shallow reasoning, inconsistent persona, and weak decision fidelity. “Thinking in Character” explicitly adds role-aware reasoning; RAIDEN-R1 and Character-R1 move role consistency into RL with verifiable rewards; OpenCharacter aims at customizable role-playing models rather than one-off personas. ([arXiv][7])

If you started here, the architecture would look like: role profile + memory + retrieval + reflection + maybe tool calls. The model is asked to *be* the mind. This gives you enormous speed and breadth. It is also the easiest place to fake structure. Prompt-state can masquerade as cognition without constraining anything durable. The result can look psychologically rich while being operationally mushy. That’s the central risk. This foundation is best when you need fast prototyping, broad domain transfer, or strong surface realization. It is bad when the differentiator is explicit inner process that is inspectable and causally real. ([arXiv][7])

**Papers/systems to know:** Generative Agents, the 2024 role-play surveys, Character is Destiny, OpenCharacter, Thinking in Character, RAIDEN-R1, Character-R1. Those last three matter because they show the field trying to move from “style mimicry” to something closer to internal role-aware reasoning. ([arXiv][7])

## 5. Believable agents / affective architectures

This is the missing row, and probably the most important addition to Ask A. Oz/Hap/Loyall starts from **interactive personality**. ABL turns that into a reactive planning language for believable story agents. FAtiMA turns appraisal, emotion, coping, and dialogue structure into a modular toolkit for socio-emotional agents and social robots. ALMA explicitly layers emotions, moods, and personality across timescales. MicroPsi/PSI integrates cognition, motivation, and emotion in one architecture. Emotional BDI reviews show how this tradition overlaps with but is not reducible to plain BDI. ([CMU School of Computer Science][1])

If you started here, the architecture would look like: perception/appraisal → affective state → coping/behavior selection → social action, with explicit personality and often explicit dialogue control. This gets you much closer to “legible character” than pure BDI or game AI. It is weaker on long-horizon plot compilation than narrative planning and weaker on open-domain language generation than LLM-first systems. But it is a very plausible alternative foundation for exactly the thing you’re trying to build. In other words: if someone else were building computational characters with inner life and they were *not* using your current lineage, this is where I’d expect them to start. ([CMU School of Computer Science][1])

## 6. BDI

Plain BDI is often overstated in discussions like this. It is a clean architecture for **goal-directed deliberation**, not a full psychology. Rao & Georgeff give the classic formulation; AgentSpeak(L) operationalizes it; Jason and Jadex make it practical. This is the right substrate when you want explicit beliefs, commitments, plans, and multi-agent coordination. It is not the right substrate if you want rumination, defense, intrusive memory, or affective distortion to be native. Those become add-ons. ([AAAI][8])

If you built the system on BDI, it would look like: belief base + event handling + desires/goals + intention stack + plan library + plan repair. You’d get crisp semantics and good ensemble coordination. You’d also get characters that default toward competence and rationality. To get “dreamlike” texture, you’d need to inject appraisal, emotional state, memory bias, coping style, or explicit non-rational attentional loops. Emotional BDI work exists, but the very existence of that subfield tells you the core model is not enough by itself. ([AAAI][8])

So the honest answer is: BDI is an excellent **outer skeleton**. It is a mediocre standalone explanation of inner life.

## 7. Embodied simulation / game AI

This family starts from the body in a world: sensors, affordances, action selection, reactivity, and robustness. GOAP in *F.E.A.R.* is the classic example of “planning inside game AI without writing huge FSM spaghetti.” Behavior Trees are the canonical modern structure because they are modular and reactive, and game/robotics surveys make clear why they spread so widely. Yannakakis & Togelius’s *Artificial Intelligence and Games* is the broad reference if you want the whole landscape. ([GameDevs][9])

If you built from this foundation, the system would look like: world state + affordances + action selectors + utility or BT or planner. It would be good at real-time interactivity and embodied plausibility. It would be bad at reflection unless you add another layer. Memory here is usually instrumental, not haunting. That’s not a minor difference. It changes what kind of character you get. ([GameDevs][9])

## 8. Improvisation-first systems

This family starts from theatre craft, not cognition. *Robot Improv* is the old signal here: believable agents through drama rather than explicit emotion models. *Improvised Theatre Alongside Artificial Intelligences* and *Improbotics* make the live-performance version concrete. The 2024 co-creative improv paper is the current LLM-era update and is useful because it foregrounds the actual failure modes: interface, timing, context relevance, and audience expectations. ([ResearchGate][10])

Architecturally, this gives you a system optimized for **presence, offer-taking, and liveness**. It is often strong locally and weak globally. It keeps the scene alive but does not naturally accumulate obsession or long-range closure unless you bolt on memory and pressure machinery. That makes it a very good adapter for performance feel and a bad sole substrate for persistent preoccupation. ([AAAI Open Access

][11])

## What I’d actually change in your Ask A answer

Add one sentence like this:

> The nearest omitted alternative foundation is the believable-agent / affective-agent lineage: Oz/Hap, ABL, FAtiMA, ALMA, and MicroPsi. These systems are closer substitutes for “characters with legible inner life” than RL or generic game-AI stacks because their control primitives are appraisal, reactive planning, coping, personality, and socio-emotional state rather than reward optimization or embodied action selection alone. ([CMU School of Computer Science][1])

And then deepen section 5 by describing **what the system would actually look like** under each foundation, not just gain/loss bullets. The three that matter most to expand are exactly the three you flagged:

* **active inference** → character as generative model with preferences, uncertainty, and precision
* **BDI** → character as deliberative planner with goals, commitments, and repair
* **LLM-direct** → character as prompt-conditioned memory/reflection loop with role-aware reasoning tricks layered on top

## If I were choosing a reading shortlist

Not “all the papers,” just the load-bearing ones:

1. Loyall, *Believable Agents: Building Interactive Personalities* ([CMU School of Computer Science][1])
2. Mateas & Stern, *A Behavior Language for Story-Based Believable Agents* ([Baskin School of Engineering][12])
3. FAtiMA Toolkit paper ([arXiv][13])
4. Riedl & Young, *Narrative Planning: Balancing Plot and Character* ([Georgia Tech Faculty][4])
5. Sabre 2021 ([AAAI][14])
6. Friston et al. / `pymdp` for active inference ([arXiv][3])
7. Generative Agents ([arXiv][7])
8. Character is Destiny + Thinking in Character ([arXiv][15])
9. Rao & Georgeff, *BDI Agents: From Theory to Practice* ([AAAI][8])
10. Orkin, *Three States and a Plan* ([GameDevs][9])

One extra connection: for the **performance-instrument half**, don’t ask the cognitive-architecture literature to solve mapping, liveness, or learnability. That is an IMS / performance-HCI problem with its own layer stack and playtest criteria. Keep that lane separate in your head. 

And for the **research/writing-companion half**, there is a separate neighboring substrate that is not character-centric at all: claim-/question-/evidence-centered discourse graph systems. If the project leans harder into scholarly or reflective daemons, that literature matters as much as character architecture does. 

Look away from the screen for ten seconds. Relax your shoulders.

[1]: https://www.cs.cmu.edu/Groups/oz/papers/CMU-CS-97-123.pdf?utm_source=chatgpt.com "Believable Agents: Building Interactive Personalities"
[2]: https://arxiv.org/abs/2602.20936?utm_source=chatgpt.com "Empathy Modeling in Active Inference Agents for Perspective-Taking and Alignment"
[3]: https://arxiv.org/abs/2201.03904?utm_source=chatgpt.com "pymdp: A Python library for active inference in discrete state spaces"
[4]: https://faculty.cc.gatech.edu/~riedl/pubs/jair.pdf?utm_source=chatgpt.com "Narrative Planning: Balancing Plot and Character"
[5]: https://www.cal.cs.ucla.edu/tech-report/1992-reports/920057.pdf?utm_source=chatgpt.com "MINSTREL: A Computer Model of Creativity and Storytelling"
[6]: https://arxiv.org/abs/1705.05363?utm_source=chatgpt.com "Curiosity-driven Exploration by Self-supervised Prediction"
[7]: https://arxiv.org/abs/2304.03442?utm_source=chatgpt.com "Generative Agents: Interactive Simulacra of Human Behavior"
[8]: https://cdn.aaai.org/ICMAS/1995/ICMAS95-042.pdf?utm_source=chatgpt.com "BDI Agents: From Theory to Practice"
[9]: https://www.gamedevs.org/uploads/three-states-plan-ai-of-fear.pdf?utm_source=chatgpt.com "Three States and a Plan: The A.I. of F.E.A.R."
[10]: https://www.researchgate.net/publication/3847670_Robot_improv_Using_drama_to_create_believable_agents?utm_source=chatgpt.com "Robot improv: Using drama to create believable agents"
[11]: https://ojs.aaai.org/index.php/AIIDE/article/view/12926?utm_source=chatgpt.com "Improvised Theatre Alongside Artificial Intelligences"
[12]: https://users.soe.ucsc.edu/~michaelm/publications/mateas-aaai-symp-aiide-2002.pdf?utm_source=chatgpt.com "A Behavior Language for Story-based Believable Agents"
[13]: https://arxiv.org/abs/2103.03020?utm_source=chatgpt.com "FAtiMA Toolkit -- Toward an effective and accessible tool for the development of intelligent virtual agents and social robots"
[14]: https://cdn.aaai.org/ojs/18896/18896-52-22662-1-2-20211004.pdf?utm_source=chatgpt.com "Sabre: A Narrative Planner Supporting Intention and Deep ..."
[15]: https://arxiv.org/abs/2404.12138?utm_source=chatgpt.com "Character is Destiny: Can Role-Playing Language Agents ..."
