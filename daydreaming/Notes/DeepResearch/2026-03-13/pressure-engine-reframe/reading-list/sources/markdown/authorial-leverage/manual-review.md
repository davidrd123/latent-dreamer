# Authorial Leverage Manual Review

Image-reviewed companion notes for layout-sensitive pages in `authorial-leverage/`.

This note stabilizes the core evaluation argument of the paper: not just whether drama management improves player experience, but whether it reduces authoring burden relative to equivalent trigger logic.

<!-- page: 1 -->
## Page 1

- Title: `Evaluating the Authorial Leverage of Drama Management`
- Authors: Sherol Chen, Mark J. Nelson, Michael Mateas.

Paper goal:
- Evaluate the value of a drama-management architecture from the author’s side, not just the player’s side.
- The paper defines `authorial leverage` as the power a tool gives an author to define a quality interactive experience relative to the tool’s authorial complexity.

Three evaluation criteria introduced on page 1:
1. Complexity of equivalent script-and-trigger logic
2. Ease of policy change when story elements change
3. Variability/branching of generated experiences at a given quality level

Pressure-engine relevance:
- This is the cleanest source for asking whether an L3 controller is worth its complexity rather than merely whether it can produce some better runs.

<!-- page: 2 -->
## Page 2

DODM recap as assumed by this paper:
- Plot points are important story events.
- Plot points have ordering constraints and annotations such as location or subplot membership.
- DM actions can cause, deny, un-deny, or hint toward plot points.
- An evaluation function rates complete plot-point sequences.

Decision-tree-equivalent method:
- Generate many traces of the DM acting in many story states.
- Learn a decision tree from `(partial story state -> DM action)` examples.
- Interpret the learned tree as equivalent trigger logic.
- Internal nodes correspond to trigger tests over flags/state.
- Leaves correspond to DM moves and thus to scripts.

This is the paper’s main methodological contribution:
- Instead of comparing DODM to a hand-written trigger system directly, it induces an approximate trigger equivalent and studies its size and behavior.

<!-- page: 3 -->
## Page 3

Testbed:
- The paper evaluates on `EMPath`, a Zelda-like adventure game built to test DODM in a more traditional game format.
- The world has up to 10 plot points and 32 DM actions, plus the no-op option.

Training setup:
- DODM is run to generate 2,500 drama-managed story traces.
- This yields about 22,000 training instances for the decision-tree learner.

Key qualitative result:
- Very small trees underfit.
- Very large trees overfit.
- A moderately pruned tree best matches the actual search-based DM policy.

Pressure-engine relevance:
- This is useful because it turns “equivalent trigger logic” into something measurable rather than rhetorical.

<!-- page: 4 -->
## Page 4

Figures 2-4:
- Figure 2 shows a highly pruned policy with 17 nodes.
- Figure 3 shows the best-performing induced policy with 70 nodes.
- Figure 4 zooms into one part of the 70-node tree and reveals the practical trigger complexity behind the policy.

The paper’s point from the zoomed view:
- Even for a small story world, the approximate trigger equivalent rapidly becomes awkward to hand-author.
- The equivalent logic involves conjunctions over plot-point history and DM-action history, not simple local triggers.

Policy change experiment:
- The authors create three EMPath variants with increasing world complexity.
- Table 1 (manually stabilized):
- `empath-small`: 10 plot points, 33 DM actions, 3 quests, max size 25
- `empath-med`: 14 plot points, 47 DM actions, 5 quests, max size 40
- `empath-large`: 18 plot points, 62 DM actions, 6 quests, max size 64

This experiment targets the second leverage criterion:
- If small authorial changes in the DM representation imply many edits in the trigger-logic equivalent, the DM offers leverage.

<!-- page: 5 -->
## Page 5

Figure 5:
- Approximate optimal decision-tree size rises sharply as story-world size increases.
- The paper’s reading is that going from small to medium to large would require many additional script-trigger edits in a hand-authored system.

Concrete example on the page:
- Adding new plot points and DM actions around `get_sword` changes the equivalent policy in a way that would require edits across multiple trigger conditions and outcomes.
- The point is not the specific sword example; it is that local world growth produces distributed policy growth in the equivalent trigger system.

Third leverage criterion: variability of stories
- The paper argues that leverage is not just about one high-quality experience.
- A system that always produces the same good story would still fail this criterion.
- Figure 6 shows the number of unique stories produced across tree sizes, with DODM supporting both high quality and substantial variation.

Pressure-engine relevance:
- This paper is a direct argument against evaluating L3 only by “best run quality”.
- The controller should be judged by both policy compactness and the breadth of experiences it can support.

<!-- page: 6 -->
## Page 6

Decision-tree policy caveat:
- Decision trees are only an approximate compiled form of the DM policy.
- Some constraints are implicit in DODM and not directly visible in a plain trigger representation.
- The authors note that a trigger system would need additional machinery to represent forbidden or disallowed action combinations.

Conclusions, manually stabilized:
- The paper argues that the central authorial question is the degree of authorial control that a drama manager provides relative to its complexity.
- For the tested cases, DODM appears to provide leverage:
- equivalent trigger logic becomes complex quickly
- policy changes scale poorly in trigger form as worlds get larger
- DODM can maintain quality while supporting a wider variety of experiences

Future directions suggested by the paper:
- evaluate additional systems
- refine the measures of authorial leverage
- use learned trigger-and-script systems as fast compiled surrogates for runtime policies
