# Tanagra Manual Review

Image-reviewed companion notes for layout-sensitive pages in `Tanagra/`.

Use this note when the text layer is structurally unreliable. It does not replace `source.md`; it records safer reconstructions of the parts that matter most for later reasoning.

<!-- page: 1 -->
## Page 1

- This is a publisher wrapper page, not article body content.
- Safe treatment: omit from canonical content.
- It confirms the DOI, venue, and author list, but adds no substantive paper content.

<!-- page: 2 -->
## Page 2

- Title: `Tanagra: A Mixed-Initiative Level Design Tool`
- Authors: Gillian Smith, Jim Whitehead, Michael Mateas.
- Affiliation: Expressive Intelligence Studio, University of California, Santa Cruz.

Abstract, manually stabilized:
- Tanagra is a mixed-initiative tool for 2D platformer level design.
- The human can place exact geometry constraints and manipulate pacing.
- The computer continuously fills in the remaining geometry.
- The generator either guarantees playability or reports that no satisfying level exists.
- The paper covers the editor design, editing operations, and an expressivity evaluation.

Figure 1, manually reconstructed:
- Upper-left: main level canvas where geometry is drawn and regenerated.
- Lower-left: beat timeline used to manipulate pacing.
- Right side: editing controls for level operations.
- The core point is not just “generator + editor”; it is a coupled interface where the designer can constrain both spatial geometry and beat structure.

Key framing from the introduction:
- Level design is iterative and local edits can force broader downstream changes.
- Procedural support is valuable when it can regenerate partial designs and enforce playability.
- The paper explicitly rejects weak author control through global parameter tweaking alone.

<!-- page: 3 -->
## Page 3

This page is body content, not references.

The paper states three research questions:
1. What technical infrastructure can support a mixed-initiative level design tool?
2. What novel editing operations can such a tool provide?
3. How can the expressive range of such a generator be measured, and what is Tanagra's?

Related-work position:
- Prior procedural level generation mostly emphasizes fully automated replayability.
- Tanagra instead targets one level at a time, with designer-computer collaboration throughout the process.
- The key distinction is iterative co-creation, not one-shot generation followed by minor edits.

Pressure-engine relevance:
- This is a direct precedent for “human anchors, machine completion, fail-loud constraints”.
- The paper treats partial regeneration and local edits as first-class workflow operations.

<!-- page: 4 -->
## Page 4

Figure 2, geometry pattern vocabulary:
- Flat platform
- Jump to platform, example 1
- Jump to platform, example 2
- Enemy
- Spring
- Stomper

Section 3, representation:
- The level is represented as a set of beats plus geometry.
- Geometry patterns are tied to the player action expected during the associated beat.
- Geometry has entrance and exit points so consecutive beats can be connected coherently.

Section 3.1, beats:
- Beats are the underlying structural unit.
- A beat has an associated length or traversal time.
- Beats are ordered and can be split, resized, added, or removed.

Section 3.2, geometry:
- Running along a long platform
- Jumping to a second platform, with or without a gap
- Jumping to kill an enemy
- Jumping onto a spring
- Waiting before running underneath a stomper

Pressure-engine relevance:
- The important abstraction is “typed recurring pattern with explicit connection points”, not the platformer domain itself.
- This maps cleanly to authored anchors plus constrained completion.

<!-- page: 5 -->
## Page 5

Figures 3 and 4:
- Figure 3 shows the drawing canvas with a level created cooperatively by the human and the procedural generator.
- Figure 4 shows the beat timeline and a modified version of the same timeline after designer edits.

Section 4.2, beat-timeline edits:
- Remove beat
- Split beat
- Resize beat

Section 5, generator requirements:
1. Autonomously create levels when the designer does not provide input.
2. Respond to designer geometry edits by moving geometry.
3. Respond to designer beat edits by modifying beat timing and associated geometry.
4. Ensure that all generated levels remain playable.

Figure 5, system architecture:
- GUI
- Working Memory
- ABL
- Choco
- Geometry Pattern Library

Manual reconstruction of the architecture:
- GUI and ABL communicate through working memory.
- ABL selects behaviors/patterns and posts constraints.
- Choco solves spatial/physics constraints and returns a solution.
- The geometry pattern library supplies the candidate pattern vocabulary.

Figure 6, algorithm shape:
- There is a branch for a beat with no geometry and a branch for a beat that is modified.
- In both cases the process is: choose or update pattern state, post constraints, solve, and regenerate as needed.
- The important design choice is incremental recomputation around the modified beat rather than full restart.

<!-- page: 6 -->
## Page 6

Section 5.1, constraint solving:
- Choco is used to assign exact component positions.
- Constraints enforce playability by matching beat geometry across transitions and bounding movement distances.
- Gap width and height are treated as constrained variables.

Section 5.2, reactive planning:
- ABL chooses behaviors for beat management and geometry creation.
- Working memory holds both the level state and the designer's most recent edits.
- The system responds to user input through behavior selection rather than a single monolithic solve.

Sections 5.3 and 5.4:
- Beat management keeps beat length and predecessor/successor relationships consistent.
- Geometry creation assigns geometry to beats and modifies it when the beat changes.

Pressure-engine relevance:
- This is a strong precedent for using one controller to choose pattern/operation structure and another solver to satisfy hard constraints.
- The “brain chooses structure, solver places details” split is exactly the useful part to steal.

<!-- page: 7 -->
## Page 7

Section 5.5, search for a solution:
- If a geometry pattern becomes invalid after an edit, the system backtracks.
- Three recovery options are described:
- Relax positioning constraints on other geometry so newly chosen geometry can fit.
- Choose a different geometry pattern.
- Ignore a geometry conflict for the current beat and choose new geometry for adjacent beats, while marking the current pattern invalid for this beat.

Figure 7, use scenario:
- A and B show different generated completions for the same initial input.
- C shows a new platform drawn into the first beat, with regeneration affecting both the beginning and end of the level.
- D shows a beat-structure change: slower pacing in the first half and faster pacing in the second half.

Pressure-engine relevance:
- This is the clearest operational model for “regenerate locally under preserved anchors”.
- The system treats edit propagation as structured and limited, not unconstrained rewrite.

<!-- page: 8 -->
## Page 8

Section 7, generator expressivity:
- The paper compares generated levels against hand-authored reference levels.
- Two explicit metrics are used: linearity and leniency.

Section 7.1, linearity:
- Fit a line through the contour of the level.
- Score by summed deviation from that line.
- Higher score means more linear; lower score means less linear.

Section 7.2, leniency:
- Intended as a rough proxy for difficulty.
- Computed from geometry content such as gaps, enemies, stompers, and springs.
- Higher score means more lenient/easier; lower score means harsher/more difficult.

Figure 8:
- Shows extreme examples of the expressivity axes: highly linear vs highly non-linear, and highly lenient vs highly non-lenient.

Figure 9:
- Histogram of linearity scores
- Histogram of leniency scores
- Scatter plot combining both axes

Pressure-engine relevance:
- The reusable move here is visible metric space for proposal browsing, not the particular platformer metrics.
- This supports the “show alternatives with legible reasons” workflow.

<!-- page: 9 -->
## Page 9

Discussion and future work:
- The paper positions Tanagra as an initial mixed-initiative level design tool rather than a finished product.
- It proposes broader geometry vocabularies, richer designer-facing editing operations, and better evaluation metrics as future work.

For this project, the takeaways are narrower:
- Human-set anchors should be inviolable.
- Regeneration should be local and constraint-backed.
- “No valid completion exists” is a legitimate result.
- Expressive-range evaluation should expose the design space rather than collapse everything into one score.
