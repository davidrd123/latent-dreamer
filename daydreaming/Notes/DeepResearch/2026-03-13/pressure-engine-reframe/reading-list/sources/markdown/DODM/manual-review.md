# DODM Manual Review

Image-reviewed companion notes for layout-sensitive pages in `DODM/`.

Use this note when the two-column text layer is structurally unreliable. It captures the authorial-modeling and control-policy ideas that are easiest to lose in raw extraction.

<!-- page: 1 -->
## Page 1

- Title: `Declarative Optimization-Based Drama Management in Interactive Fiction`
- Authors: Mark J. Nelson, Michael Mateas, David L. Roberts, Charles L. Isbell Jr.
- The page’s pull-quote gives the paper’s core claim: prior game-tree-search drama management does not perform well in general.

Core framing:
- A drama manager watches the story as it unfolds and reconfigures the world to better satisfy authorial goals.
- DODM projects possible future stories and chooses world interventions according to an author-specified evaluation function.
- DODM is presented as a generalization of search-based drama management rather than a commitment to one specific projection algorithm.

Figure 1:
- Uses `Façade` as the motivating example of a player interacting with story-bearing NPCs.
- The point is not the specific interface, but the need for coordination between local actions and larger narrative quality goals.

<!-- page: 2 -->
## Page 2

Figure 2, manually reconstructed:
- The concrete game world emits plot points to the drama manager.
- The drama manager chooses DM actions and sends them back to the game.
- Example: opening the puzzle box emits `open_puzzle_box`; the DM can respond with `temp_deny_get_amulet`.

Anchorhead setup:
- The paper applies DODM to a modified version of day two from `Anchorhead`.
- The story is reduced to two interleaved subplots, each of which can lead to an ending.
- Subplot 1 centers on the puzzle box, odd lens, observatory, and `see_evil_god`.
- Subplot 2 centers on the bum, William, the amulet, the sewer book, and `discover_book_in_sewer`.

Pressure-engine relevance:
- This is the exact abstraction we want for L3: the live world stays concrete, while the control layer reasons over a thinner plot-point space.

<!-- page: 3 -->
## Page 3

Plot-point modeling:
- The author abstracts story contents into discrete plot points that the DM should know about.
- Ordering constraints are not limited to a simple DAG; DODM allows arbitrary Boolean formulas over prior plot points.
- This matters because some story beats can be enabled by multiple alternative earlier discoveries.

Level-of-detail rule from the paper:
- Represent plot points the DM might want to cause, prevent, or otherwise modify.
- Represent plot points that significantly affect story quality and should be visible to the evaluation function.
- Omit the rest, because too many low-importance plot points make authorial evaluation noisy and increase optimization complexity.

Pressure-engine relevance:
- This is a strong precedent for a deliberately thin traversal state rather than a fully detailed world model at the control layer.

<!-- page: 4 -->
## Page 4

Figure 3:
- Shows 29 plot points for the modified second day of `Anchorhead`.
- The graph is explicitly AND-OR rather than a simple linear ordering.

Player model:
- The baseline player model is effectively random exploration over currently legal plot points.
- A second variant assumes the player sometimes follows hints emitted by the DM.
- The paper notes that the player model and the plot-point abstraction interact: with a near-random player, a fairly uniform level of detail works better.

Choosing DM actions:
- The paper emphasizes that DM actions must be plausible in-world.
- It is easier to design plausible DM actions around characters than around arbitrary world-state manipulations.

<!-- page: 5 -->
## Page 5

Five DM action types:
- permanent deniers
- temporary deniers
- causers
- hints
- game endings

Anchorhead action inventory:
- 4 permanent deniers
- 5 temporary deniers
- 5 corresponding reenablers
- 4 causers
- 10 hints
- 2 game endings
- Total: 30 DM actions

Important authoring judgment:
- Permanent deniers are powerful but risky because they irreversibly close off possibilities.
- Temporary deniers are preferred where possible because they can be undone later.

Feature toolbox for authorial goals:
- General features: `location flow`, `thought flow`, `motivation`
- Multiple-ending features: `plot mixing`, `plot homing`
- Metafeatures about DM behavior: `choices`, `manipulativity`

Manual stabilization of feature meanings:
- `location flow`: prefers spatial locality rather than constant wandering.
- `thought flow`: prefers short coherent sub-subplot runs from the player’s assumed mental continuity.
- `motivation`: rewards plot points that feel prepared by earlier plot points.
- `plot mixing`: early story should include multiple subplots.
- `plot homing`: later story should converge toward one subplot rather than keep oscillating.
- `choices`: preserves player freedom over the next plot point.
- `manipulativity`: penalizes suspiciously intrusive DM moves.

Pressure-engine relevance:
- This is the strongest reusable part of the paper. The scheduler is not guided by one scalar “quality” term; it is guided by a weighted feature toolbox encoding explicit authorial goals.

<!-- page: 6 -->
## Page 6

Evaluation methodology:
- Compare distributions of story scores with and without drama management.
- The unmanaged distribution is built from many random plots.
- The managed distribution is built from runs induced by the player model plus DM interventions.

Reported experiment sizes:
- unmanaged distributions: 10,000 samples each
- search-managed distributions: 100 simulated runs each
- reinforcement-learning-managed distributions: 2,000 simulated runs each

Search framing:
- SBDM uses a game-tree style projection after each plot point.
- In DODM’s random-player setting, minimizing nodes become averaging nodes.
- SAS and SAS+ are shallow sampling approximations to the full search.
- SAS+ allows temporarily denied plot points to appear in future samples because they may later be reenabled.

Why search breaks:
- The local search effectively chooses the action that optimizes the immediate average future under an assumption of no later coordinated DM action.
- This is a bad fit for paired interventions such as temporary denier plus reenabler, which only pay off when planned across multiple steps.

<!-- page: 7 -->
## Page 7

Figure 4 results:
- With SAS+ and a 2-second decision limit, drama management shifts the quality distribution to the right, but only modestly.
- Reported percentiles:
- player ignores hints: mean at the 64th percentile, median at the 59th
- player probabilistically follows hints: mean at the 64th percentile, median at the 63rd

Figure 5 result:
- When the authors give the DM synthetic actions that should make control easier, SAS+ still performs worse than using no drama management at all.
- Their conclusion is direct: the problem is not just weak action vocabulary; the shallow search itself is inadequate on this story.

Subplot-specific finding:
- Search works better on the `discover_book_in_sewer` ending than on `see_evil_god`.
- The likely reason given is that the latter relies heavily on temporary deniers and reenablers, which shallow search handles badly.

Pressure-engine relevance:
- This is the key warning. If a traversal policy depends on delayed paired interventions, shallow local search can look reasonable in toy domains and then fail once the story has real structure.

<!-- page: 8 -->
## Page 8

Reinforcement-learning alternative:
- The paper investigates offline temporal-difference learning to precompute a policy instead of projecting futures online.
- Training uses many simulated runs and a neural network function approximator for story-state values.

Important design choice:
- Training is done against an adversarial player rather than a random player, to expose weaknesses in the DM policy more aggressively.
- The resulting policy is then evaluated against the usual player models.

Figure 6:
- Repeats the subplot-specific distributions under reinforcement learning and shows clearer improvement than SAS+.

Figure 7:
- Compares standard TD training with a pseudoadversarial TD variant.
- The pseudoadversarial version reduces the large gap in the search results and produces the better policy of the two.

Pressure-engine relevance:
- The paper is effectively arguing for policy learning or precomputation when online shallow search cannot coordinate delayed authorial moves.

<!-- page: 9 -->
## Page 9

Table 1 summary:
- Search: mean `66.40`, median `57.62`
- Reinforcement learning: mean `72.21`, median `73.80`

Conclusions, manually stabilized:
- Declarative optimization remains attractive because the author specifies what should happen at a high level and leaves detailed maximization to the optimizer.
- In this domain, search-based drama management was too optimistic a framing.
- SAS+ can help in some cases but does not generalize robustly.
- Reinforcement learning is presented as the more promising alternative, especially for larger and more complex stories.

Future work:
- Better reinforcement-learning-based optimization
- More realistic player models
- Potential use of author-supplied coarse story maps or player likelihood annotations

<!-- page: 10 -->
## Page 10

- This page is author biography material, not article body content.
- Safe treatment: exclude from canonical reasoning about the paper’s methods and results.
