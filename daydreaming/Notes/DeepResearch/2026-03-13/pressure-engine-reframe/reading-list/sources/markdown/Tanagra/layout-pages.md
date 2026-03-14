# Tanagra

Layout-preserving `pdftotext -layout` extraction.

<!-- page: 1 -->
```text
                                                                                                                                    PDF Download
                                                                                                                                    1822348.1822376.pdf
                                                                                                                                    14 March 2026
                                                                                                                                    Total Citations: 88
                                                                                                                                    Total Downloads: 1021
    .
    .
        Latest updates: hps://dl.acm.org/doi/10.1145/1822348.1822376
                                                                                                                                    .
                                                                                                                                    .
                                                                                                                          Published: 19 June 2010
        .
        .
                                                                                                                          .
    RESEARCH-ARTICLE
                                                                                                                          .
                                                                                                                          Citation in BibTeX format
    Tanagra: a mixed-initiative level design tool
                                                                                                                          .
                                                                                                                          .
                                                                                                                          FDG '10: International Conference on the
                                                                                                                          Foundations of Digital Games
    GILLIAN SMITH, University of California, Santa Cruz, Santa Cruz, CA, United States                                    June 19 - 21, 2010
    .
                                                                                                                          California, Monterey
    JIM WHITEHEAD, University of California, Santa Cruz, Santa Cruz, CA, United States
                                                                                                                          .
                                                                                                                          .
    .
    MICHAEL MATEAS, University of California, Santa Cruz, Santa Cruz, CA, United States
    .
    .
    .
    Open Access Support provided by:
    .
    University of California, Santa Cruz
    .
                                                        FDG '10: Proceedings of the Fih International Conference on the Foundations of Digital Games (June 2010)
                                                                                                                           hps://doi.org/10.1145/1822348.1822376
                                                                                                                                              ISBN: 9781605589374
.
```

<!-- page: 2 -->
```text
                   Tanagra: A Mixed-Initiative Level Design Tool
                                         Gillian Smith, Jim Whitehead, Michael Mateas
                                                         Expressive Intelligence Studio
                                                       University of California, Santa Cruz
                                                             Santa Cruz, CA, USA
                                               {gsmith, ejw, michaelm}@soe.ucsc.edu
ABSTRACT
Tanagra is a prototype mixed-initiative design tool for 2D plat-
former level design, in which a human and computer can work to-
gether to produce a level. The human designer can place constraints
on a continuously running level generator, in the form of exact ge-
ometry placement and manipulation of the level’s pacing. The com-
puter then fills in the rest of the level with geometry that guarantees
playability, or informs the designer that there is no level that meets
their requirements. This paper presents the design of Tanagra, a
discussion of the editing operations it provides to the designer, and
an evaluation of the expressivity of its generator.
Categories and Subject Descriptors
I.2.1 [Artificial Intelligence]: Applications and Expert Systems
– Games. F.4.1. [Mathematical Logic and Formal Languages]                            Figure 1. The Tanagra intelligent level design tool. The large
Mathematical Logic – Logic and constraint programming. I.2.8.                        area in the upper left is where the level is created. Below that
[Artificial Intelligence] Problem Solving, Control Methods, and                      is a beat timeline, allowing manipulation of the pacing of the
Search – Plan execution, formation, and generation.                                  level. On the right are buttons for editing the level.
                                                                                     ers is especially important now that players, who tend to be inex-
General Terms                                                                        perienced designers, are increasingly being invited to create their
Design, Human Factors                                                                own content for games.
                                                                                     A mixed-initiative approach to level design, where content is cre-
Keywords                                                                             ated through iterative cycles between the human designer and
Games, level design, procedural content generation, AI-assisted                      procedural support, would make it possible for designers to take
design.                                                                              advantage of the benefits that procedural level generation can of-
                                                                                     fer: rapidly showing alternative designs, regenerating specified
1. INTRODUCTION                                                                      portions of a level, and enforcing the playability of any produced
Level design is a vital part of the game design process: levels are                  levels. Existing techniques in procedural level generation tend to
a “container for gameplay” [3], providing the player with a space                    offer little in terms of author guidance other than tweaking param-
to explore the game’s mechanics. Creating a good level is a time                     eters in probability distributions, which can lead to unexpected
consuming and iterative process: the level may start as a simple                     outcomes [20] and do not allow the author to exert enough control
sketch of the space, which is then filled in with specific geometry.                 over the final design. Consider, for example, our previous work
Designers will typically play the level themselves a number of                       in designer-guided level generation for 2D platformers [26]. We
times before showing it to anyone else, checking that it is playable,                found it was extremely challenging to set fine-grained constraints
engaging, and meets their expectations [4]. Making a change to                       such as prioritizing a segment of the control line or specifying an
a small section of a level, such as moving a single platform, can                    order for components to be introduced. It was also impossible to
have a wide impact and require much of the rest of the level to be                   edit the level once it had been created, which drastically reduced
modified as well. Techniques from procedural level generation of-                    the designer’s creative control.
fer many opportunities to ease this authoring burden, by having the                  This paper presents Tanagra, a prototype mixed-initiative level de-
computer design and modify that which a human would normally                         sign tool for 2D platformers (e.g. Super Mario World or Sonic the
painstakingly create. Providing authoring support to level design-                   Hedgehog). The underlying representation for levels in Tanagra is
                                                                                     beat-based, which provides a structure for creating levels with a
Permission to make digital or hard copies of all or part of this work for            well-defined rhythm [25]. Designers can interact with the tool in
personal or classroom use is granted without fee provided that copies are
                                                                                     two different ways: both by editing the gameplay experience of the
not made or distributed for profit or commercial advantage and that copies
bear this notice and the full citation on the first page. To copy otherwise,         level through altering the pacing of the beats, and by manipulat-
to republish, to post on servers or to redistribute to lists, requires prior         ing the geometry from which this gameplay emerges. Each beat
specific permission and/or a fee.                                                    specifies an interval of play time, during which the player must
                                                                                     take some action, such as running or jumping. For example, the
FDG 2010, June 19-21, Monterey, CA, USA                                              designer can draw a few platforms into the editor, and ask Tanagra
Copyright 2010 ACM 978-1-60558-937-4/10/06... $10.00                                 to generate geometry that will make the level playable but obey
```

<!-- page: 3 -->
```text
these geometry constraints. The designer could then decide that the             grained support for the designer in terms of both geometry and beat
beginning of the level should be more fast-paced than the end, and              editing tools.
address this by adding new beats to the beginning of the level for
                                                                                An increasingly common use for content generation is personalized
which either he or Tanagra can specify geometry. Tanagra is ca-
                                                                                content creation, either adapting to a player at runtime [2][8][10]
pable of producing a wide range of fully playable levels. A screen-
                                                                                [17][18] or learning player preferences offline [29]. The goal of
shot of a level created in Tanagra is shown in Figure 1. A video of
                                                                                Tanagra is to assist a human designer with the creation of an single
Tanagra in action is also available online1.
                                                                                level at design-time; interesting future work would be to incorpo-
Our work is guided by design principles for intelligent creativity              rate models of different styles of play to encourage designers to
and design support tools [12][14][16][21]. There has recently been              include more diversity in their levels.
a call for such intelligent design tools specifically in the domain of
                                                                                Work in author-guided level generation tends to place all of an au-
games [11][23]. We have been careful to ensure that Tanagra does
                                                                                thor’s control at the beginning of the process [9][26][30], and occa-
not push its own agenda on the designer by ensuring that decisions
                                                                                sionally allows editing after the level is complete [7]. For example,
made by the human cannot be overridden by the system, although
                                                                                the world builder for Civilization IV allows the scenario designer
it can augment human-placed geometry through the placement of
                                                                                to set certain terrain parameters ahead of time, such as the size of
additional level components. Tanagra provides expertise through
                                                                                land masses, distribution of water and land, and climate. After the
its ability to ensure that levels are always playable and by allowing
                                                                                generator creates the initial world, the designer can then modify the
the designer to directly manipulate the pacing of the level by edit-
                                                                                terrain according to his own desires. However, there is no way to
ing the underlying beat structure. It also works with the iterative
                                                                                request another map that respects the changes that the designer has
design process by supporting new decisions from the designer at
                                                                                made, or that only a part of the level be regenerated. The mixed-
any time during creation, and rapidly re-generating sections of a
                                                                                initiative nature of Tanagra means that the designer and computer
level as needed.
                                                                                can collaborate throughout the design process.
At the core of Tanagra is a novel procedural level generator that
is capable of accepting initial input from a designer and then con-             2.1.1 Platformer Generation Techniques
tinuously updating the level when further refinements are made.                 Platformers are well-suited to research in procedural level genera-
Our approach integrates a reactive planning language, ABL [15],                 tion due to their simple rules but surprisingly complex level design
with a constraint programming and solving library, Choco [5]. ABL               [25] and their lasting popularity. Infinite Mario Bros [19] and Spe-
chooses the level components that should be placed, while the con-              lunky [31] are two examples of released platformers where all lev-
straint solver determines their exact location.                                 els are procedurally generated. Both games’ generation techniques
This work addresses the following questions:                                    work by fitting together relatively large hand-authored sections of
                                                                                a level. These generation techniques work well for their specific
 1. What technical infrastructure can be used to support a mixed-               game, but could not be applied to other games in the same genre
    initiative level design tool?                                               without the significant design burden of authoring large chunks of
 2. What kinds of novel editing operations can a mixed-initiative               levels for the generator to use. Spelunky is especially dependent on
    level design tool provide?                                                  its game mechanics, since the player can use certain tools to modify
                                                                                the level and traverse otherwise impossible terrain, and the player’s
 3. How can we measure the expressive range of such a generator,                desire to conserve such tools means that many areas of the level
    and what is Tanagra’s?                                                      will deliberately go unexplored. The building blocks for our levels
The remainder of this paper discusses related work in both pro-                 are much smaller—a beat encompasses a single player action—and
cedural level generation and AI-supported design (Section 2), the               are extensible to different sets of geometry. Tanagra’s level genera-
implementation details of Tanagra (Sections 3 and 4), and evalu-                tion technique is built on our previous work in generating levels
ation of our system in terms of use scenarios and analysis of the               based on a rhythm that the player feels [26], which in turn is based
expressive range of the generator (Sections 5 and 6).                           on Compton & Mateas’s work in pattern-based level generation [6].
2. RELATED WORK                                                                 2.2 AI-Assisted Design Tools
                                                                                As a mixed-initiative design tool, Tanagra combines the power of
2.1 Procedural Level Generation                                                 procedural content generation with traditional design techniques to
Typically, procedural level generation is used to improve re-play-              improve the human designer’s experience. Lipp et al.’s [13] tool
ability in games by providing a new scenario each time the player               for procedurally-supported building modeling has similar goals to
starts a new game. In such games, the levels are completely gener-              Tanagra, although in the space of 3D modeling for non-interactive
ated by the computer, with no creative interplay between human                  structures rather than interactive spaces such as platformer levels.
designer and computer generator [1][19][22][31]. Much of the
design of levels is implicitly encoded in the generation algorithm              Other AI-supported design tools include Biped [24], which allows
itself, with designer input limited to tweaking parameters to ensure            designers to rapidly specify prototypes of their games and view
that all the levels that the generator can create are appropriate. This         play traces from both human and computer players. The system’s
parameter tweaking can be unintuitive, with small shifts leading to             strength is in allowing a designer to view situations in which their
radical changes in the produced content [20]. In contrast, Tanagra              game design “fails” by asking the system to show a playthrough
focuses on creating a single level at a time, while providing fine-             that produces a certain undesirable result. Tanagra does not sup-
                                                                                port any qualitative evaluation of the level, but does make sure that
1 http://users.soe.ucsc.edu/~gsmith/tanagra/fdgdemo.html                        levels are always playable.
```

<!-- page: 4 -->
```text
Flat Platform                                                                  3.2 Geometry
                                                                               Level components are built up into patterns based on the action the
Jump To Platform                                                               player should perform during the associated beat. These patterns
(example 1)                                                                    are:
                                                                                • Running along a long platform
Jump To Platform                                                                • Jumping to a second platform (with or without a gap)
(example 2)
                                                                                • Jumping to kill an enemy
                                                                                • Jumping onto a spring
Enemy
                                                                                • Waiting before running underneath a stomper
                                                                               Gaps can be of variable width, from zero to the maximum length
Spring                                                                         that the player can jump, and variable height, from the maximum
                                                                               height that the player can jump to its opposite value. Examples of
                                                                               each of these patterns are shown in Figure 2. Enemies, springs, and
                                                                               stompers are always the same size, but can have different positions
                                                                               along the platform.
Stomper                                                                        Geometry patterns also have a defined player entrance and exit
                                                                               point, which can be tied together by the geometry generator such
                                                                               that the exit point of geometry in beat n is equivalent to the en-
                                                                               trance point of geometry in beat n+1.
Figure 2. Example instantiations of the different geometry pat-                4. DESIGN ENVIRONMENT
terns used by Tanagra.                                                         Tanagra can give suggestions for level designs to the human de-
QuestBrowser [28] is a brainstorming tool for quest designers,                 signer, ensure that all levels it creates are playable, and supports
intended to show different potential solutions that a player might             editing the level at both the geometry layer and the beat layer to
think of, to better inform the design. Brainstorming is an important           modify pacing. When Tanagra starts, the designer can specify the
aspect to creativity support [27], which Tanagra fulfills by provid-           length of the beat timeline and number of beats in it, and they are
ing the ability to rapidly regenerate levels.                                  presented with an empty geometry canvas. A control panel on the
                                                                               right side of the design environment allows the designer to insert
                                                                               and remove beats, change the length of a beat, select geometry to be
3. LEVEL REPRESENTATION                                                        locked in place or moved in the canvas, and request a newly gener-
Tanagra represents levels as a set of beats, each of which contains
                                                                               ated level that respects any changes the human has made.
level components, collectively referred to as “geometry”. The sup-
ported level components are platforms, gaps, springs, enemies,
and stompers. A physics model that defines the maximum running                 4.1 The Geometry Canvas
speed and maximum jump height for the avatar guarantees that all               The geometry canvas is the place where both the human and com-
geometry placed into the level is playable and meets beat duration             puter draw geometry. The canvas, which is initially empty, is made
constraints. Tanagra focuses on structural properties of the level,            up of a grid of tiles that are 5 units wide and 5 units tall. This grid
displaying platforms and other level components as differently col-            serves two purposes: to reduce the search space for the constraint
ored rectangles.                                                               solver, and for ease of adding tile-based art assets at a later time.
                                                                               The designer can choose to draw platforms associated with specific
3.1 Beats                                                                      beats to guide the generator. Each beat has an area allotted to it that
As the building blocks of rhythms, beats are the underlying struc-             spans the maximum distance the player can cover in the length of
ture for Tanagra’s level generator. A beat’s primary role is to con-           the beat. Drawn platforms must fit into the area that is allotted to
strain the length of the geometry within it to the distance that can           the beat, based on its start position and length. Designers can as-
be traversed by the player in the duration of that beat, as calculated         sign geometry to as many beats as they wish before starting the
from the physics model. Beats are also a convenient way of subdi-              generator. Once the computer has created geometry, the designer
viding the space for the generation algorithm, as geometry for each            can select that geometry to be “glued” in place or moved around.
beat can be generated separately.                                              All designer-specified geometry is respected by the generator and
                                                                               cannot be moved, unless the designer chooses to “release” that ge-
Beats encapsulate a single player action, such as a jump, which
                                                                               ometry back to the generator. The following geometry canvas edit-
can occur at any time between the start and end times of the beat.
                                                                               ing operations are available to the designer:
Each beat has the following properties: a start time, an end time, a
collection of level components associated with the beat, and knowl-             • Draw platform: draw a platform associated with a beat.
edge of its preceding and following beats. At any time, the designer
                                                                                • Remove platform: delete geometry, either user-created or gener-
can add, remove, or modify a beat, which propagates any changes
                                                                                  ator-created, and request new geometry in its place.
down to the geometry contained within it.
                                                                                • Glue geometry: select geometry that the computer has created
```

<!-- page: 5 -->
```text
                                                                                                     Working Memory
                                                                                                    Updated Level
                                                                                                                                            Beat Structure
                                                                             Designer Input
                                                                                                                      Updated Level
Figure 3. Tanagra’s drawing canvas, containing a level created                                                                                               Constraints
by cooperation between the human designer and procedural                                      GUI                                     ABL                                  Choco
generator.                                                                                                                                                    Solution
                                                                                                                      Geometry
                                                                                                                    Pattern Library
Figure 4. Top: the timeline used in creating the level shown
above. Bottom: the original timeline that was presented to the
                                                                             Figure 5. Tanagra is made up of three components: the GUI, an
designer.
                                                                             ABL agent, and the Choco constraint solver. The GUI and ABL
   and have it be treated as user-created from now on.                       communicate through working memory.
 • Replace geometry: draw new geometry for a beat, replacing                 When a beat has no geometry:
   what was previously used for that beat.                                    1. Choose a geometry pattern
                                                                              2. If designer has specified a component for this beat:
An example of a simple level with mixed human- and computer-
                                                                                     Post additional constraints on the geometry pattern to reflect the
created geometry is shown in Figure 3. Note that human-created                       specified component
geometry is shaded darker than computer-created geometry.                         Else:
                                                                                     Add new components to geometry pattern
4.2 The Beat Timeline                                                         4. Post constraints to Choco solver for all chosen geometry
The beat timeline provides a mechanism for editing the pacing of              5. Post constraints to Choco for tying this beat’s geometry to
                                                                                  neighboring beats’ geometry
the level by inserting or removing beats and modifying their length.
                                                                              6. Run constraint solver. If solution:
Beat changes prompt the generator to make geometry changes, al-                      Move on to another beat
lowing pacing changes without the need to manipulate geometry.                    Else if there are still valid geometry patterns for this beat:
The following beat timeline editing operations are available to the                  Try generating for this beat again with a different pattern
designer:                                                                         Else:
                                                                                     Regenerate a neighboring beat with a different geometry pattern
 • Remove beat: remove the beat and its associated geometry by
   merging its two neighboring beats.                                        When a beat is modified:
                                                                              1. Make change to the beat requested by designer (add, remove,
 • Split beat: divide the selected beat in half, moving all of the
                                                                                 change length)
   existing geometry to one of these halves and generating new                2. Correct pointers to the beat’s next and previous beats as necessary
   geometry for the other half.                                               3. Post updated beat constraints to Choco solver
 • Resize beat: change the length of the beat, adjusting the next             4. Re-solve
   and previous beats to compensate for the length change.                   Figure 6. Algorithm for Tanagra’s level generator as applied to
Figure 4 shows the original timeline and a modified beat timeline.           a single beat.
                                                                             single beat is shown in Figure 6.
5. GENERATION TECHNIQUE                                                      Tanagra works quickly enough to permit rapid reaction to user
Tanagra’s level generator fulfills the following requirements:
                                                                             input. It will continuously generate geometry until a solution is
 1. Autonomously create levels in the absence of designer input.             found, and regenerate levels either in response to a change made
                                                                             by the designer, or on demand. We have found it useful to keep
 2. Respond to designer input in the form of placing and moving
                                                                             separate the choice of a geometry pattern (using ABL) from the
    geometry.
                                                                             instantiation of that pattern (using Choco), as the precise placement
 3. Respond to designer input in the form of modifying the beat              of level components is influenced by surrounding geometry. This
    timeline.                                                                means that the placement of components in one beat may be able to
 4. Ensure that all levels are playable.                                     change based on the placement of level components in a later beat,
                                                                             while still maintaining the same geometry pattern in both beats.
To meet these goals, Tanagra employs a reactive planning lan-                There are also many different configurations of component place-
guage, ABL [15], to respond to user input and choose the geom-               ment that meet the same geometry pattern. For example, a jump
etry that should be placed for each beat. A constraint programming           to a different platform could have a short initial platform and a
library, Choco [5], is used to specify and solve constraints on the          long later platform, or vice versa. To enumerate and search through
placement of different level components. A diagram of Tanagra’s              each allowed instantiation of a geometry pattern would be inef-
system architecture is shown in Figure 5. Pseudocode for how ABL             ficient and tedious; instead, the search can be split into two stages:
handles geometry generation and response to designer input for a             ABL searches at the pattern level, while Choco searches for a valid
```

<!-- page: 6 -->
```text
instantiation. The pattern abstraction also permits adding new kinds           designer are as local as possible. For example, extending the length
of design patterns easily, as instead of specifying all possible com-          of a beat should not result in an entirely different level, but just a
binations of geometry components, we can instead specify rules for             minor change to the affected beats. To accomplish this, Tanagra
the construction of the pattern.                                               sets additional constraints on the solver, that the value of a variable
                                                                               should be the same as the last time the solver was run. These con-
5.1 Constraint Solving                                                         straints are then relaxed if no solution can be found.
Choco is a Java library for constraint satisfaction problems. The
position and dimension for every level component are expressed as              5.2 Reactive Planning
Choco integer variables, where the domain for each variable is the             A Behavior Language (ABL) is a Java-based reactive planning
size of the canvas they are placed in. For example, a platform has             language created by Mateas & Stern for use in creating believable
6 constraint variables: startX, endX, startY, endY, width, and                 agents that can rapidly react to a changing world state [15]. ABL
height, and begins with the constraints:                                       defines agents as a set of behaviors (goals) that can be performed,
                                                                               where each of these behaviors can have a number of subgoals that
   startX + width = endX; startY + height = endY
                                                                               can operate either in sequence or in parallel. Behavior selection
While width and height are obviously not necessary for finding a               is determined by fulfillment of preconditions and the behavior’s
solution, we find it useful to include them both for ease in specify-          priority.
ing other constraints, and for pruning Choco’s search space: fre-
                                                                               The world state is communicated to the agent through Working
quently, the width of a platform is more constrained than its begin-
                                                                               Memory Elements (WMEs). Behaviors can request information
ning or end positions, and so a good place to start Choco’s search is
                                                                               from, update, or poll for changes in any WMEs that are registered
with one of these variables that has very few legal values.
                                                                               to working memory. These WMEs are also accessible to the appli-
Choco is responsible for ensuring that the level is playable through           cation; for example, each beat in Tanagra is stored as a BeatWME,
solving constraints for:                                                       that can be access by both ABL and the GUI.
 • Matching the end of one beat’s geometry with the beginning of               ABL behaviors specify how the system should react to user input,
   its following beat’s geometry                                               and rules for what geometry patterns can be chosen for beats. Its
                                                                               responsibilities are:
 • The length of the sum of all geometry components in a beat be-
   ing equal to the distance that should be covered in that amount              • Beat management: making sure that all beats have correctly as-
   of time                                                                        signed preceding and following beats, and that changes to beats
 • Individual geometry elements having an appropriate position                    result in changes to geometry.
   and dimension; for example, a gap should separate the ending                 • Creating geometry: if a beat has no geometry pattern associated
   and beginning points of two platforms by its width and height,                 with it, ABL will choose a pattern for the beat.
   and platforms should never overlap
                                                                                • Responding to user input: any change made to the level is de-
Consider the example of a gap separating two platforms. A gap has                 tected by the ABL agent and the appropriate response is given.
two constraint variables: width and height, and knows which
                                                                                • Posting constraints: aside from constraints internal to level
two platforms it is separating. Both width and height are con-
                                                                                  components, which are typically known when that component
strained by the distance the avatar can jump in the length of time
                                                                                  is created, ABL is responsible for posting all constraints to the
the gap encompasses, as determined from the physics model. For
                                                                                  Choco constraint solver.
two platforms, p1 and p2, and a gap, g:
                                                                               How ABL handles these responsibilities is described in the follow-
      p1.endX + g.width = p2.startX;
                                                                               ing sections.
      p1.endY + g.height = p2.startY;
Choco is called each time the generator places geometry for a beat;            5.3 Beat Management
typically, solutions are found within 5 milliseconds, on a 3GHz                ABL is responsible for maintaining the accuracy of the beat time-
Intel Core 2 Duo. It can take significantly longer to exhaustively             line, by making sure that each beat is pointing to the correct preced-
determine that there is no solution, so Tanagra stops itself from              ing and following beats, and that the length of the beat is correct.
searching after 500 milliseconds, since it is unlikely to find a solu-         This is accomplished by storing each beat as a BeatWME, thus
tion after that point. The solution space is pruned by requiring all           making it accessible to ABL. There are then managers for each ac-
position variables to be a multiple of 5, to match the grid represen-          tion that can be performed on a beat, and for ensuring that the beat
tation, anchoring the first geometry component placed in the grid to           knows about its neighbors. These managers constantly poll work-
have an x value of 0, and only placing geometry for a beat when a              ing memory, looking for BeatWMEs that have been modified or
neighboring beat already has geometry in it.                                   do not have pointers to their next or previous geometry assigned
                                                                               correctly.
To avoid always getting the same values for position and dimension
of components in an instantiation of a geometry pattern, Choco is
instructed to use a random value in the domain of the variable each            5.4 Geometry Creation
time it has to make a decision on variable assignment. Once the                There are three different kinds of geometry management that ABL
pattern has been instantiated, however, we want to keep the same               performs: assigning geometry to a beat with no geometry associ-
values for as long as possible, so that any changes made by the                ated with it, assigning geometry to a beat with user-created geom-
                                                                               etry associated with it, and modifying geometry when its associated
```

<!-- page: 7 -->
```text
beat has been modified.
If ABL encounters a BeatWME with no geometry associated with
it, it can choose to place any of the geometry patterns described
earlier. ABL creates all of the level components required for the
geometry pattern; for example, if “jump to another platform” was
chosen, it would create two Platforms and a Gap. Each of these lev-
el components is itself a WME. ABL adds these WMEs to working
memory and posts the appropriate constraints to the Choco physics
model. Finally, after all of the level components have been placed,
ABL calls on Choco to solve the constraint problem. If a solution is
found, the process is complete. If not, a different geometry pattern
is tried. If all geometry patterns fail, the generator backtracks, as
described below.
When the designer adds a platform to the geometry canvas, he adds
it to a specific beat. If ABL detects it is assigning a pattern to a beat
that already has a designer-specified platform, it will incorporate
this platform into each pattern that it attempts. If the designer at-
tempts to place geometry “illegally” (for example, by drawing a
platform on top of another one), Tanagra determines that no solu-
tion is available and waits for the designer to edit the level such that
a solution can be found.
When a beat is removed or has its length changed, all geometry as-
sociated with the affected beat is removed, and ABL will reassign
new geometry to the new beats. If the beat’s length was changed,
ABL will first attempt the geometry that had been previously as-
signed to it.
5.5 Searching for a Solution
There are many cases in which the geometry pattern selected for
a beat will be invalid. For example, consider a scenario in which
the designer has drawn two long platforms that are separated by a
beat. These platforms have different y values, so clearly the con-
necting geometry for the middle beat could not be another long
platform, as the endpoints would not line up. However, a different                Figure 7. A use scenario for Tanagra. A and B show different
geometry pattern, such as a jump to a different platform, may solve               levels generated for the same designer input. C shows a new
this scenario.                                                                    platform drawn into the first beat, and pacing changes at both
                                                                                  the beginning and end of the level.
There are many scenarios in which no geometry pattern for a given
beat can produce a playable level; in these cases, ABL must back-                 manipulating level pacing.
track and try a different geometry pattern for other beats, in order              The designer can provide initial guidance to Tanagra by placing
to find a correct combination of geometry patterns.                               initial platforms in the geometry canvas. In this example, we place
There are three ways in which ABL will backtrack for a given beat,                two platforms in the level: one at the mid-point, and one at the end.
in order:                                                                         The generator then fills in geometry for the rest of the level that
                                                                                  matches the initial specifications (Figure 7A).
 1. Relaxing absolute positioning constraints on other geometry in
    the level, which allows the rest of the level to “flow” around the            Tanagra can rapidly regenerate the level to show different varia-
    newly chosen geometry.                                                        tions that meet the same requirements. Figure 7B shows one such
                                                                                  alternative level.
 2. Choose a different geometry pattern, marking the currently se-
    lected pattern as invalid.                                                    Geometry for a beat can be replaced with user-drawn geometry at
                                                                                  any time, and Tanagra will regenerate surrounding geometry to ac-
 3. Ignore all chosen geometry for the current beat, and choose
                                                                                  commodate the change. In Figure 7C, the designer has decided to
    new geometry for either the next or previous beat. Mark the
                                                                                  move the beginning of the level lower down, by drawing a different
    pattern of currently chosen geometry as invalid regardless of
                                                                                  platform in the first beat.
    the geometry used for the current beat.
                                                                                  Finally, Tanagra supports editing the beat structure itself, in addi-
6. USE SCENARIO                                                                   tion to manipulating raw geometry. In Figure 7C, the designer has
This section presents a detailed use scenario, showcasing Tanagra’s               also slowed down the pacing of the first half of the level, and sig-
key abilities: auto-filling geometry, brainstorming level ideas, and              nificantly increased the pacing of the second half.
```

<!-- page: 8 -->
```text
                                                                               that range between highly linear and highly non-linear. Examples
                                                                               of levels that fall at the extremes of this scale are shown in Figure
                                                                               8A and Figure 8B. The linearity of a level is measured by perform-
                                                                               ing linear regression on the level, taking the center-points of each
                                                                               platform as a data point. We then score each level by taking the sum
                                                                               of the absolute values of the distance from each platform midpoint
                                                                               to its expected value on the line, and divide by the total number
                                                                               of points. Results are normalized to a [0, 1] scale, with 0 being
                                                                               highly linear and 1 being highly non-linear. A graph showing the
                                                                               distribution of linearity scores across 500 generated levels is shown
                                                                               in Figure 9A.
                                                                               Tanagra is slightly biased towards creating more linear levels; we
                                                                               hypothesize that this is because there are more instantiations of ge-
                                                                               ometry patterns that have no height variation. Flat platforms, en-
                                                                               emies, and stompers all appear on flat platforms, while gaps can
                                                                               have a positive, negative, or zero height.
                                                                               7.2 Leniency
                                                                               While we hesitate to classify the difficulty of Tanagra’s levels, as
                                                                               such a measure would be a) subjective, and b) highly dependent
                                                                               on the ordering of geometry, we can classify how “lenient” each
                                                                               geometry pattern is to the player. Clearly, a level made up of mostly
Figure 8. Examples of levels at the extrema of Tanagra’s ex-                   gaps and enemies is far less forgiving than a level with no gaps and
pressive range. (A) A highly linear level, score=0.0 (B) A highly              a number of long, flat platforms. To measure this, we define a leni-
non-linear level, score=1.0 (C) A highly lenient level, score =                ency score that is the weighted sum of chosen geometry patterns in
-0.8 (D) A highly non-lenient level, score=1.0                                 the level. Scores are as follows: flat platform: -2, jump to platform
                                                                               with no gap: -1, jump to platform with gap, enemies, or stompers:
7. GENERATOR EXPRESSIVITY                                                      1. The score is normalized for the number of beats in the level, with
As a level generator that must support designers, it is especially             a maximum value of 1 and a minimum value of -2. However, very
important that Tanagra can create a wide range of levels. While it             few levels score lower than -1. Figure 8C and Figure 8D shows
is easy to show that Tanagra can create a large quantity of levels,            examples of levels at the extremes of the leniency score. Notice that
we feel it is more interesting to examine the qualities of the levels          the lenient level has very few gaps or enemies, whereas the non-
that are produced, and compare how similar they are to each other.             lenient level has many enemies and stompers. A graph showing the
To this end, we have created two different metrics for comparing               distribution of leniency scores is shown in Figure 9B.
levels: the linearity of a level, and how lenient the level is towards
the player. We then apply these metrics to levels that are entirely            Again, Tanagra seems slightly biased towards one side of this
procedurally generated.                                                        range, creating more unforgiving levels than lenient ones. This is
                                                                               because there are more geometry patterns that contain a level com-
                                                                               ponent classified as unforgiving: enemies, stompers, and gaps can
7.1 Linearity                                                                  all occur quite frequently.
The first method for evaluating Tanagra’s expressivity is evaluat-
ing the “profile” of produced levels. We do this by fitting a line to
the produced geometry, and determining how well the geometry                   7.3 Expressive Range
fits that line. The goal here is not to determine what exactly the             With two different axes on which to compare generated levels, Tan-
line is, but rather to understand Tanagra’s ability to produce levels          agra’s expressive range is the distribution of levels within the rect-
Figure 9. Graphs showing the expressive range of Tanagra. The first shows the distribution of levels with specific linearity scores, the
second the distribution of levels with specific leniency scores, and the third shows the combined distribution of levels with linearity
on the x-axis and leniency on the y-axis.
```

<!-- page: 9 -->
```text
angle defined by these axes. Figure 9C shows a plot of each of the                  Game Artificial Intelligence. Technical Report WS-04-04, The AAAI Press,
generated levels within this range, where each dot corresponds to                   Menlo Park, CA.
a single generated level. Although heavily biased towards creating                  [11] Isla, D. 2009. Invited Talk: Next-Gen Content Creation for Next-Gen
                                                                                    AI. Fourth International Conference on the Foundations of Digital Games.
linear levels, Tanagra is clearly capable of generating levels with
                                                                                    Orlando, FL. April 26-30, 2009.
varying linearity and leniency, and there is no evident correlation                 [12] Lawson, B. and Loke, S.M. 1997. Computers, Words, and Pictures.
between these two measures.                                                         Design Studies, vol.18, no.2, pp171-183. 1997.
                                                                                    [13] Lipp, M., Wonka, P., and Wimmer, M. 2008. Interactive Visual Edit-
8. DISCUSSION AND FUTURE WORK                                                       ing of Grammars for Procedural Architecture. In Proc. of the Intl. Confer-
Tanagra is a mixed-initiative level design tool for 2D platformers                  ence on Computer Graphics and Interactive Techniques (SIGGRAPH ’08).
                                                                                    Los Angeles, CA. August 11-15, 2008.
that supports a human designer through procedurally generating                      [14] Lubart, T. 2005. How Can Computers be Partners in the Creative Pro-
new content on demand, verifying the playability of levels, and al-                 cess: Classification and Commentary on the Special Issue. International
lowing the designer to edit the pacing of the level without needing                 Journal of Man-Machine Studies, vol.63, no.4-5, pp365-369, 2005.
to manipulate geometry. This paper presented Tanagra’s design, as                   [15] Mateas, M. and Stern, A. 2002. A Behavior Language for Story-Based
well as examples of how it can be used and a method for measuring                   Believable Agents. IEEE Intelligent Systems, pp39-47. July/August, 2002.
the expressive range of the levels it can produce.                                  [16] Negroponte, N. 2003. Soft Architecture Machines. In The New Media
                                                                                    Reader, Noah Wardrip-Fruin and Nick Montfort, Eds. Chapter 23, pp353-
Tanagra is still in a prototype stage, and is itself part of a larger in-           366. The MIT Press, Cambridge, MA. 2003.
tended system that will support more editing operations and differ-                 [17] Nitsche, M., Ashmore, C., Hankinson, W., Fitzpatrick, R., Kelly,
ent views on the level. We envision a final system that uses Tanagra                J., and Margenau, K. 2006. Designing Procedural Game Spaces: A Case
as a subcomponent, with other components being a difficulty ana-                    Study. In Proceedings of FuturePlay 2006. London, Ontario. October 10
lyzer, and a way to easily view and edit entire level paths without                 – 12, 2006.
                                                                                    [18] Pedersen, C., Togelius, T., and Yannakakis, G. 2009. Modeling Player
needing to focus on fine-grained geometry details.                                  Experience in Super Mario Bros. In Proceedings of the IEEE Symposium
There is work to be done in easily expressing different geometry                    on Computational Intelligence and Games (CIG ’09). Milano, Italy. Sep-
patterns; for example, Sonic the Hedgehog has long spirals and                      tember 7 – 10, 2009.
                                                                                    [19] Persson, Marcus. 2008. Infinite Mario Bros! (Online Game). Last Ac-
loop-de-loops that are not currently supported by Tanagra. There                    cessed: December 2008. http://www.mojang.com/notch/mario
are also interesting issues to address in terms of an appropriate in-               [20] Regier, J. and Gresko, R.. 2009. “Random Asset Generation in Diablo
terface for a mixed-initiative level design tool. It can be difficult               3”, Invited Talk, UC Santa Cruz. October 30, 2009.
to determine the “correct” action to take in certain situations; for                [21] Resnick, M. et al. 2005. Design Principles for Tools to Support Cre-
example, when splitting a beat in half, how should the existing ge-                 ative Thinking. Technical Report: NSF Workshop Report on Creativity Sup-
ometry for that beat be divided up?                                                 port Tools. Washington, DC. 2005.
                                                                                    [22] Rogue Basin. Articles on Implementation Techniques [Online]. Last
More broadly, there is a need for a standard way to measure the ex-                 Accessed: July 2009. http://roguebasin.roguelikedevelopment.org/index.ph
pressive range of a procedural content generation system, whether                   p?title=Articles#Implementation
it is entirely autonomous or mixed-initiative. This paper presents a                [23] Satchell, C. 2009. Keynote: Evolution of the Medium – Positioning
step in the right direction, by classifying Tanagra’s range along two               for the Future of Gaming. Fourth International Conference on the Founda-
different measures of the level. We anticipate performing future                    tions of Digital Games. Orlando, FL. April 26-30, 2009.
                                                                                    [24] Smith, A.M., Nelson, M.J., and Mateas, M. 2009. Prototyping Games
work in defining new metrics for comparing levels.                                  with BIPED. In Proceedings of the Fifth Artificial Intelligence and Interac-
                                                                                    tive Digital Entertainment Conference (AIIDE ’09). Stanford, CA. October
REFERENCES                                                                          14-16, 2009.
[1] Bay 12 Games. 2006. Dwarf Fortress (PC Game).                                   [25] Smith, G., Cha, M., and Whitehead, J. 2008. A Framework for Analy-
[2] Booth, M. 2009. The AI Systems of Left 4 Dead. Keynote, Fifth Arti-             sis of 2D Platformer Levels. In Proceedings of the ACM SIGGRAPH Sand-
ficial Intelligence and Interactive Digital Entertainment Conference (AIIDE         box Symposium 2008. Los Angeles, CA. August 9 – 10, 2008.
’09). Stanford, CA. October 14 – 16, 2009.                                          [26] Smith, G., Treanor, M., Whitehead, J., and Mateas, M. 2009. Rhythm-
[3] Byrne, E. 2005. Game Level Design. Charles River Media.                         based Level Generation for 2D Platformers. In Proc. of the 4th Int’l Confer-
[4] Castillo, T. and Novak, J. 2008. Game Development Essentials: Game              ence on Foundations of Digital Games. Orlando, FL. April 26 – 30, 2009.
Level Design. Delmar Cengage Learning.                                              [27] Sternberg, Robert J. 1999. Enhancing Creativity, Handbook of Cre-
[5] Choco Team. 2008. Choco: an Open Source Java Constraint Program-                ativity. pp 401 – 402. Cambridge University Press.
ming Library. White Paper, CPAI08 Competition. http://www.emn.fr/z-                 [28] Sullivan, A., Mateas, M., and Wardrip-Fruin, N. 2009. QuestBrowser:
info/choco-solver/pdf/choco-presentation.pdf                                        Making Quests Playable with Computer-Assisted Design. In Proceedings
[6] Compton, K. and Mateas, M. 2006. Procedural Level Design for Plat-              of Digital Arts and Culture 2009 (DAC ’09). Irvine, CA. December 12-15,
form Games. In Proceedings of the Second Artificial Intelligence and Inter-         2009.
active Digital Entertainment Conference (AIIDE ’06). Stanford, CA. 2006.            [29] Togelius, J., De Nardi, R., and Lucas, S. 2007. Towards Automatic
[7] Firaxis Games. 2005. Sid Meier’s Civilization IV (PC Game).                     Personalised Content Creation for Racing Games. In Proceedings of the
[8] Hastings, E., Guha, R., and Stanley, K.O. 2009. Evolving Content in             IEEE Symposium on Computational Intelligence and Games (CIG ’09).
the Galactic Arms Race Video Game. In Proc. of the IEEE Symposium on                Honolulu, HI. 2007.
Computational Intelligence and Games (CIG ’09). Milano, Italy. Septem-              [30] Tutenel, T., Smelik, R., Bidarra, R., and Jan De Kraker, K. 2009. Us-
ber 7 – 10, 2009.                                                                   ing Semantics to Improve the Design of Game Worlds. In Proceedings of
[9] Hullett, K. and Mateas, M. 2009. Scenario Generation for Emergency              the Fifth Artificial Intelligence and Interactive Digital Entertainment Con-
Rescue Training Games. In Proceedings of the 4th International Confer-              ference (AIIDE ’09). Stanford, CA. October 14 – 16, 2009.
ence on Foundations of Digital Games. Orlando, FL. April 26 – 30, 2009.             [31] Yu, D. 2009. Spelunky (PC Game).
[10] Hunicke, R. and Chapman, V. 2004. AI for Dynamic Difficulty Ad-
justment in Games. Papers from the 2004 AAAI Workshop on Challenges in
```
