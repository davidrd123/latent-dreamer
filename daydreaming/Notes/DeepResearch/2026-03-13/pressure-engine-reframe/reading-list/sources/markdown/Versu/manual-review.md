# Versu Manual Review

Image-reviewed companion notes for the structurally important pages in `Versu/`.

Because almost every page in this bundle is layout-sensitive, this note focuses on the parts most likely to matter later: social practices, affordances, the world model, the agent chooser, and the story-manager position relative to Façade.

<!-- page: 1 -->
## Page 1

- Title: `Versu: A Simulationist Storytelling System`
- Authors: Richard Evans and Emily Short.

Abstract, manually stabilized:
- `Versu` is a text-based simulationist interactive drama.
- It emphasizes replayability by letting the same episode be played from multiple perspectives and by allowing different characters to fill the same roles.
- The central coordination abstraction is the `social practice`, described as a successor to the Schankian script.
- Social practices are implemented as reactive joint plans that provide `affordances`.
- Practices do not directly control agents; they suggest actions.
- Actual choice remains with the individual agent via utility-based reactive action selection.

Figure 1, screenshot:
- The interface shows generated prose, portrait/state feedback for characters, and an explicit action menu.
- The menu structure reinforces the paper’s main UI decision: expose affordances instead of forcing the player through a broad natural-language parser.

<!-- page: 2 -->
## Page 2

Strong-autonomy framing:
- The paper contrasts strong-story systems, where a centralized DM decides everything, with strong-autonomy systems, where individual characters choose from their own beliefs and desires.
- `Façade` is placed in the middle: the DM chooses beats, while characters and joint behaviors control local realization.
- `Versu` places itself closer to strong autonomy:
- each character chooses its next action from its own beliefs and desires,
- the centralized DM rarely overrides character autonomy directly,
- the DM usually operates by suggestion or by tweaking desires.

Roles versus characters:
- `Versu` sharply separates story roles from the characters assigned to those roles.
- This is one of the paper’s primary replayability arguments.
- A scenario is authored once at the role level, then replayed with different character-role assignments.

Pressure-engine relevance:
- This is the clearest source in the list for “author role structure once, let cast assignment vary later.”

<!-- page: 3 -->
## Page 3

The paper explicitly answers three issues it sees in `Façade`:
1. Content-production speed and limited global agency.
2. Weak feedback about simulation state.
3. Interface friction from broad natural-language input being forced into a relatively small discourse-act set.

`Versu`’s three corresponding answers:
1. Use generated text and static images instead of 3D performance.
2. Expose simulation state more directly through UI feedback.
3. Replace parser-first interaction with explicit menus of currently available affordances.

Text as an expressive medium:
- The paper argues that text is not only cheaper than animated embodiment, but often more expressive for interiority and fine personality differences.
- The concrete claim is that modifiers such as reluctance, eagerness, swagger, and similar shades of style are cheap in text and expensive in animation.

<!-- page: 4 -->
## Page 4

Figure 2, manually reconstructed:
- Each character has an emotional portrait/state display.
- Clicking a portrait reveals why the character is in that state, including:
- the emotion,
- its target,
- and the event that produced it.

Feedback model:
- The UI exposes emotions, relationships, and currently active social practices so the player can learn the simulation’s own ontology.
- The paper frames this through the `Sim City effect`: the interface should help the player converge on the simulation’s model of the world without having to read a manual.

Architecture overview on this page:
- The simulation is built from two first-class object types: `agents` and `social practices`.
- A social practice is a recurring social situation.
- Practices coordinate through roles.
- Practices enumerate possible actions for participants but do not force execution.
- Agents still decide what to do.

<!-- page: 5 -->
## Page 5

Figure 3, architecture diagram, manually stabilized:
- Inputs:
- `Social Practice File`
- `World Initialization File`
- `Character File`
- Parsing / interpretation stages:
- `Social Practice Parser -> Social Practice Type`
- `Function Parser -> World Initialization Function`
- `Function Parser -> Character Initialization Function`
- Shared state:
- `Database`
- Runtime processes and outputs:
- `Social Practice Instantiator -> Social Practice Instances`
- `Action Instantiator -> Action Instances`
- `Character Instantiator -> Characters`
- `Decision Maker -> Chosen Action Instance -> Action Executor`

Figure-caption implication:
- The pictured flow is for an NPC making a decision.
- For player choice, the action instances are sent directly to the UI instead of going through the decision maker.

Concurrent-practice claim:
- Multiple social practices can exist at once.
- The example given is a dinner party with simultaneous meal behavior, conversation, flirtation, and response to an incident.
- An agent’s current options are the union of affordances from all practices the agent participates in.

Important distinction from finite-state machines:
- A practice can keep arbitrary persistent data.
- Performing an action can do much more than change a local state label; it can update relationships, create beliefs/desires, delete practices, and spawn new ones.

<!-- page: 6 -->
## Page 6

Authoring model:
- Building an episode involves three script classes:
- scripts for social practices,
- scripts for character initial state,
- a script for initial world state.
- These are authored in the domain-specific language `Praxis`.

World representation:
- The simulation state is a set of sentences in exclusion logic.
- The paper emphasizes three advantages of this representation:
- visibility,
- debuggability,
- serializability.

Runtime inspector:
- The authors explicitly value tooling that can reveal:
- what is true,
- all facts about an object/agent/process,
- instantiations of terms with free variables,
- why an action’s preconditions failed,
- what is causing a fact to become true.

DM status:
- The diagram does not treat the DM as a separate ontological category.
- Each episode’s DM is implemented as a special type of social practice.

Pressure-engine relevance:
- This is a strong precedent for representing world state in a form the control layer can inspect directly, rather than burying important state inside opaque objects.

<!-- page: 10 -->
## Page 10

Agent decision mechanism:
- The paper calls the chooser `utility-based reactive action selection`, not full planning.
- The key difference from many utility systems is that agents evaluate the actual short-term consequences of candidate actions, not a simplified proxy.
- The paper explicitly contrasts this with `The Sims`, where simplified consequence models can create pathological repeated failures.

Broad-not-deep reasoning:
- The system does not plan deeply.
- Instead it looks broadly at a rich set of short-term conditional consequences.
- The authors argue that this often captures benefits usually associated with longer-term planning.

Utility and desires:
- Utility is computed by summing satisfied desires.
- Wants are logical sentences over the same representational substrate as the world state.
- A character can therefore have highly specific desires, not just weights over a small trait vector.

Individuality:
- The paper’s personality argument is that there are effectively as many possible personalities as there are expressive desire sentences in the logic.

<!-- page: 14 -->
## Page 14

Story manager:
- `Versu` does not use one general-purpose DM for arbitrary combinatorial story construction.
- Each episode instead has its own story manager encoding the narrative goals of that episode.
- The story manager is described as a high-level director who dislikes micromanagement.

Operational role:
- It creates characters and initial social situations.
- It watches ongoing play.
- It occasionally intervenes by spawning new practices or tweaking goals.
- It generally leaves actual performance and moment-to-moment choice to autonomous characters.

Implementation detail:
- The story manager is itself a reactive process implemented as a social practice.

Interpretation:
- This is not “no DM.” It is an episode-specific, relatively light-touch DM that shapes the situation by manipulating the practice layer.

<!-- page: 15 -->
## Page 15

Bird’s-eye authoring of practices:
- Schankian scripts are criticized here for being role-perspectival.
- `Versu`’s social practices are authored from a bird’s-eye perspective and are agnostic about which particular character fills each role.
- The restaurant script is written once and incorporates both sides of the interaction.

DM and autonomy:
- In authorial-intent terms, the paper places `Versu` between hand-authored branching narrative and fully emergent narrative.
- Each episode has an episode-specific reactive DM that mostly watches and occasionally nudges.
- In character-autonomy terms, `Versu` is strongly simulationist:
- individual characters choose actions from their own beliefs and desires,
- DM overrides are rare,
- the DM more often creates practices or tweaks desires.
- The paper also states that the DM is modeled as an autonomous agent, which means that in some episodes the player can actually play the DM.

<!-- page: 16 -->
## Page 16

Figure 4, design-space table:
- The paper separates three questions that many systems collapse together:
- what entity controls decision making,
- what entity provides coordination between agents,
- what entity provides continuity over time.

Façade comparison:
- The paper argues that `Façade` effectively answers all three with the joint behavior / DM combination.
- `Versu` answers them separately:
- decision making: individual agent,
- coordination: social practice,
- continuity: social practice.

JB versus social practice:
- A social practice is presented as broader than a JB in two specific ways:
- it can coordinate PCs as well as NPCs,
- it does not seize decision-making authority from the individual agent.

Content-production comparison:
- The paper states that `Façade`’s roughly 20-minute scenario took more than three years to create.
- It contrasts this with a comparable `Versu` episode taking about two months, and a 45-60 minute ghost-story episode taking six months.
- It also gives a content-scale comparison:
- `Façade`: about 30 parameterized speech acts after 3+ years,
- `Versu`: more than 1000 parameterized actions after one year.

Prom Week comparison:
- `Prom Week` is described as organizing behavior around discrete speech acts.
- `Versu` instead embeds individual acts inside larger social practices that give them continuity and shared situational meaning.

Overall takeaway:
- The paper’s most reusable claim is that `social practice` is the intermediate coordination layer between a centralized authorial controller and purely local character action.
- That is the core conceptual move, more than the specific logic formalism or the text UI.
