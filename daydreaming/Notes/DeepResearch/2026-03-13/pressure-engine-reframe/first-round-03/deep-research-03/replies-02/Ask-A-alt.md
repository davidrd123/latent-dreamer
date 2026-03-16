Below is a paste-ready Ask A draft. I kept it in generic design-space language and did not mirror the project’s internal labels back at itself.

---

# Ask A Draft: What kind of thing is this?

## 1. Design-space map

The cleanest way to map this space is not by genre labels but by axes. The useful axes are:

1. **State model explicitness**: prompt-only, latent state ↔ explicit, inspectable state with memory, appraisals, norms, commitments, and typed internal moves.
2. **Content source**: fully hand-authored ↔ mixed-initiative generation with human curation ↔ fully live generation.
3. **Runtime control**: direct user action selection ↔ human biasing/conducting ↔ autonomous selection.
4. **World openness**: fixed authored graph ↔ partially simulated world ↔ open-ended world/society simulation.
5. **Externalization**: hidden internal state ↔ text/dialogue only ↔ explicit observatory/dashboard ↔ audiovisual performance.
6. **Persistence horizon**: one-shot session ↔ session memory ↔ cross-session canon/world state.
7. **Character scope**: single character ↔ small ensemble ↔ society/ecosystem.
8. **Social structure**: generic goals/needs ↔ explicit roles, norms, and social practices.
9. **Canon policy**: one realized timeline only ↔ preserved counterfactuals and unrealized alternatives.
10. **Authoring membrane**: raw model output is runtime material ↔ generated material is draft material that must be curated into canon.

On that map, some well-known systems cluster pretty clearly. DAYDREAMER sits near the explicit, introspectable, single-character end: it is a model of inner processing more than outward performance. BDI systems also sit on the explicit side, but they are action-centric and commitment-centric rather than ruminative. Versu moves toward multi-character social simulation through explicit social practices. Façade moves toward authored interactive drama, where a runtime manager shapes the order and pressure of authored material. Storylet systems push farther toward authorial leverage and recombination, usually with much less built-in psychology.  ([AAAI][1])

A different cluster lives on the world-simulation side. The Sims-like corner emphasizes needs, autonomy, and object-mediated action selection across many agents. Dwarf Fortress pushes much farther toward open world and historical simulation, where whole-world emergence matters more than introspectable per-character thought. Generative Agents is a modern LLM-native neighbor: memory, reflection, planning, and social diffusion in a sandbox town. Digital puppetry sits almost opposite to that: very high human expressivity and embodiment, very low autonomous psychology. Conversational companion systems usually sit near language-first persistence, low inspectable control geometry, and high improvisational flexibility. ([yo252yo][2])

Where does your project sit? In a sparse part of the map: explicit inner-process model, mixed-initiative authoring, curated graph/runtime substrate, runtime sequencing over authored material, human conducting rather than direct action selection, multimodal output, and cross-session canon. That combination is unusual. It is closer to “conducted interactive drama with an explicit mind under the hood” than to games, companions, or pure performance tools. Your own notes also make one hard distinction that matters: authoring-time material generation and performance-time traversal are separate loops, not one giant live generator, and right now the evidence is stronger for traversal/material-supply than for audience-facing output (`27-authoring-time-generation-reframe.md`, `11-settled-architecture.md`, `13-project-state.md`, `dashboard.md`, `34-broader-application-surface.md`).

## 2. Nearby designs

A few one- or two-axis moves produce nearby but different things:

* **Drop conducting, keep explicit cognition and persistence** → a writing/reading/research companion with recurring preoccupations rather than a stage instrument.
* **Drop explicit cognition, keep traversal and conducting** → an adaptive performance sequencer or storylet show engine.
* **Replace curated graph material with open simulation** → a sandbox social world, closer to Sims, Generative Agents, or small-society simulation.
* **Keep the graph and authoring membrane, drop runtime performance** → a mixed-initiative narrative authoring tool.
* **Raise dramatic control, lower wandering/rumination** → an interactive drama engine closer to Façade.
* **Raise embodiment, lower introspection** → a co-performative puppetry or improvisation system.

The important point is that these are not cosmetic changes. They change what counts as success.

## 3. Blind spots

The usual design-space map misses several axes that matter here.

First, **the authoring membrane**. Whether generated material becomes runtime canon directly, becomes draft material for curation, or never becomes canon at all is not an implementation detail. It changes the whole category.

Second, **counterfactual policy**. Most systems either realize one path or discard the rest. Daydream-like systems care about unrealized alternatives. A system that preserves abandoned possibilities as traversable fantasy or memory is doing something categorically different.

Third, **inner-life observability**. In most character systems, the mind is hidden and behavior is the only surface. Here, the internal process itself may be part of the output. That puts the project near observatory tools and performance dashboards, not just narrative systems.

Fourth, **timescale factorization**. Many systems choose one loop: offline authoring, runtime selection, or persistent adaptation. Your design spans all three. That is rare, and it creates failure modes that simpler systems do not have.

Fifth, **evaluation target mismatch**. “Believable behavior,” “dramatic arc,” “felt inner life,” “authorial leverage,” and “companionship value” are not the same metric. A system can win one and fail the others.

Sixth, **multi-agent scaling**. Going from one troubled character to an ensemble is not linear. Versu, The Sims, Dwarf Fortress, and Generative Agents all show that once multiple agents interact, the dominant problems become norms, coordination, and combinatorics, not just richer internal state. ([UK Computer Science][3])

The research traditions closest to these blind spots are storylet authoring, social-practice simulation, improvisational AI, digital puppetry/performance animation, and sandbox agent societies. They are nearby, but they solve different problems. ([Emily Short's Interactive Storytelling][4])

## 4. Is “conducted cognitive character” a coherent category?

As a **design family**, yes$*{80%}$. As a **single product category**, no$*{60%}$.

The coherent center is this: systems where explicit internal pressures, memory, and coping/processing modes shape what surfaces next, while a human can bias but not fully script the flow, and the resulting inner process is externally legible in real time. That is a real family.

But the broader umbrella easily turns into mush. Performance instrument, NPC substrate, writing companion, and conversational companion are not one product. They optimize for different things:

* performance instruments optimize for felt arc, conductability, and legible output
* NPC substrates optimize for reactive action and systemic robustness
* writing companions optimize for intellectual return and cross-session accumulation
* companions optimize for conversation quality and attachment

The most focused version is a **single-character or small-ensemble performance system** with explicit inner state, curated runtime material, human conducting, and multimodal output. The most general version is a **persistent character-cognition substrate** with multiple front ends: stage, companion, NPC, authoring assistant. Those are related, but they are not the same near-term build.

So the honest answer is: coherent as a family, incoherent if pitched as one thing too early.

## 5. Alternative foundations

None of the alternatives is simply “better.” They optimize for different failures.

**Predictive processing / active inference.**
You gain one formal language for attention, uncertainty, action, and affect. Concerns can be reinterpreted as persistent prediction errors, and human conducting can be reinterpreted as precision-weighting or attentional bias. You lose authorability and legibility. Typed inner moves like rehearsal, rationalization, or avoidance become harder to preserve as writable creative operators. Better for adaptive embodied agents. Not clearly better for authored inner drama. ([Wiley Online Library][5])

**Narrative planning, Dramatica, MINSTREL-like planning.**
You gain stronger global arc control, setup/payoff closure, and causal coherence across longer spans. You lose the involuntary, obsessive, sideways feel of mind-wandering. These systems are good at plots; they are weaker at minds that circle, avoid, and intrude on themselves. Better if the goal is story construction. Worse if the goal is to externalize interior processing. ([AAAI][6])

**Reinforcement learning with intrinsic motivation.**
You gain adaptation in open environments and the possibility of discovering strategies you did not author. You lose inspectability, cheap authorial control, and a clean mapping from reward structure to felt psychology. This is attractive for embodied agents in rich sims. It is a bad fit for a near-term system that wants legible inner life and controllable aesthetics. ([arXiv][7])

**LLMs as the cognitive engine directly.**
You gain fluency, world knowledge, portability, and speed of prototyping. You lose stable control geometry, inspectability, and durable distinctions between different kinds of internal process. Generative Agents is the clearest current example: memory, reflection, planning, believable local behavior, but still closer to sandbox social simulation than to authored, conductor-shaped inner drama. Good prototype path. Weak final answer if the point is reusable mechanics rather than just plausible output. ([arXiv][8])

**Embodied simulation / robotics / game-AI emotion models.**
You gain sensorimotor grounding, physically legible stakes, and tighter coupling between action and consequence. You lose cheap literary richness, counterfactual memory work, and much of the coping/defense palette that makes inner life readable in prose-like terms. Better for robots, VR, and installation work. Not clearly better for a system whose target is “watching a mind work.” Digital puppetry is the neighboring case here: it excels at expressive real-time control, not autonomous reflective cognition. ([Disney Research Studios][9])

**Improvisation / Meisner-style reactive systems.**
You gain listening, turn-taking, co-performer feel, and live responsiveness. You lose persistence, accumulated pressure landscapes, and strong cross-session structure. This is probably best treated as a runtime augmentation layer, not a replacement for the underlying cognition. It is a good answer to “how does this listen live?”, not to “what keeps returning three sessions later?” ([AAAI Publications][10])

**BDI agents.**
You gain explicit beliefs, goals, intentions, commitments, and a practical framework for real-time action selection. You lose involuntary association, coping, emotional diversion, and the messy non-instrumental processing that makes daydreaming feel like daydreaming. BDI is strong for purposeful NPCs and assistants. It is weaker for troubled minds, obsession, self-protection, and associative return. Borrow the commitment discipline. Do not inherit the whole worldview unless the system’s real target turns out to be competent action rather than inner life. ([AAAI][1])

## Bottom line

The current approach buys something specific: **legible inner drift under partial human steering**. That is not what BDI is best at, not what narrative planning is best at, not what RL is best at, and not what pure LLM companions are best at.

So the right stance is not “our foundation is best.” It is: **our foundation is tuned for one narrow prize**. If that prize is the one you actually care about, the architecture makes sense. If the real prize is something else, one of the neighboring foundations is probably cleaner.

The mistake would be to blur those prizes together.

---

If you want, I can also turn this into a tighter “answer as if written by 5 Pro” version with less scaffolding and fewer caveats.

[1]: https://cdn.aaai.org/ICMAS/1995/ICMAS95-042.pdf "https://cdn.aaai.org/ICMAS/1995/ICMAS95-042.pdf"
[2]: https://yo252yo.com/old/ens/sims-rapport.pdf "https://yo252yo.com/old/ens/sims-rapport.pdf"
[3]: https://cs.uky.edu/~sgware/reading/papers/evans2014versu.pdf "https://cs.uky.edu/~sgware/reading/papers/evans2014versu.pdf"
[4]: https://emshort.blog/2019/11/29/storylets-you-want-them/ "https://emshort.blog/2019/11/29/storylets-you-want-them/"
[5]: https://onlinelibrary.wiley.com/doi/10.1111/tops.12704 "https://onlinelibrary.wiley.com/doi/10.1111/tops.12704"
[6]: https://cdn.aaai.org/ojs/12419/12419-52-15947-1-2-20201228.pdf "https://cdn.aaai.org/ojs/12419/12419-52-15947-1-2-20201228.pdf"
[7]: https://arxiv.org/pdf/2209.08890 "https://arxiv.org/pdf/2209.08890"
[8]: https://arxiv.org/abs/2304.03442 "https://arxiv.org/abs/2304.03442"
[9]: https://studios.disneyresearch.com/2018/11/08/puppetphone-puppeteering-virtual-characters-using-a-smartphone/ "https://studios.disneyresearch.com/2018/11/08/puppetphone-puppeteering-virtual-characters-using-a-smartphone/"
[10]: https://ojs.aaai.org/index.php/AIIDE/article/view/12926 "https://ojs.aaai.org/index.php/AIIDE/article/view/12926"
