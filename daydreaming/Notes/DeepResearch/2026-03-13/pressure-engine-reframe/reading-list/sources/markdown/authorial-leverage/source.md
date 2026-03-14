# authorial-leverage

<!-- page: 1 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: title/authors/abstract page._

```text
                               Proceedings of the Fifth Artificial Intelligence for Interactive Digital Entertainment Conference
               Evaluating the Authorial Leverage of Drama Management
                                    Sherol Chen,1 Mark J. Nelson,1,2 Michael Mateas1
                    1                                                                            2
                   Expressive Intelligence Studio                                                    School of Interactive Computing
                 University of California, Santa Cruz                                                Georgia Institute of Technology
                 {sherol, michaelm}@soe.ucsc.edu                                                        mnelson@cc.gatech.edu
                              Abstract                                              Authorial leverage is the power a tool gives an author to
   A drama manager (DM) monitors an interactive experience,                      define a quality interactive experience in line with their
   such as a computer game, and intervenes to shape the global                   goals, relative to the tool’s authorial complexity. It has
   experience so that it satisfies the author’s expressive goals                 been pointed out that the “burden of authoring high quality
   without decreasing a player’s interactive agency. Most re-                    dramatic experiences should not be increased because of
   search on drama management has proposed AI architectures                      the use of a drama manager” [Roberts & Isbell, 2008], but
   and provided abstract evaluations of their effectiveness; a                   determining whether that is the case depends on determin-
   smaller body of work has also evaluated the effect of drama                   ing both the complexity of an authoring approach and the
   management on player experience. Little attention has been                    gains it provides.
   paid, however, to evaluating the authorial leverage provided
                                                                                    Previous work has studied how experience quality can
   by a drama-management architecture: determining, for a
   given architecture, the additional non-linear story complex-                  be improved by DODM [Weyhrauch, 1997]. This does not
   ity a drama manager affords over traditional scripting meth-                  directly imply that DODM provides an authorial benefit,
   ods. In this paper, we propose three criteria for evaluating                  however. To do that, there needs to be some reason to be-
   the authorial leverage of a DM: 1) the script-and-trigger                     lieve that traditional authoring methods could not have
   complexity of the DM story policy; 2) the degree of policy                    achieved the same results, or that they would have required
   change given changes to story elements; and 3) the average                    considerably more effort to do so.
   story branching factor for DM policies versus script-and-                        A way to get at that comparison is to look at the set of
   trigger policies for stories of equivalent quality. We apply                  traditional trigger-logic rules that would be equivalent to
   these criteria to declarative optimization-based drama man-                   what a drama manager is doing. We propose three criteria
   agement (DODM) by using decision tree learning to capture
                                                                                 for evaluating the authorial leverage of drama managers in
   equivalent trigger logic, and show that DODM does in fact
   provide authorial leverage.                                                   this manner: equivalent script-and-trigger complexity of
                                                                                 their policies, policy change complexity, and average
                                                                                 branching factor of their policies. We present preliminary
                          Introduction                                           work applying these metrics to declarative optimization-
                                                                                 based drama management (DODM), by examining the
Technology can expand the possibilities of narrative both                        equivalent trigger-logic for a drama-manager policy as
for those who experience and those who tell stories, in par-                     captured by a decision-tree learner.
ticular by making narrative be interactive. Authoring inter-
active narratives, however, has proven quite challenging in
practice. Narrative in games, although sharing some quali-                                             Drama Management
ties with non-interactive storytelling, delivers a highly in-
teractive experience, which requires new ways of ap-                             In this work, we focus on DODM, an approach to drama
proaching authoring. Traditional approaches to authoring                         management based on plot points, DM actions, and an
interactive stories in games involve a scripted and heavily                      evaluation function [Weyhrauch, 1997].
linear process, and extending this process to large stories                         Plot points are important events that can occur in an ex-
with complicated interactivity is difficult. Drama managers                      perience. Different sequences of plot points define differ-
provide an alternative approach, by allowing the author to                       ent player trajectories through games or story worlds. Ex-
assume a system that knows something at run-time about                           amples of plot points include a player gaining story infor-
how to manage the story. Such approaches, however, are                           mation or acquiring an important object. The plot points
difficult to evaluate from the perspective of authors look-                      are annotated with ordering constraints that capture the
ing for reasons to use a drama manager rather than tradi-                        physical limitations of the world, such as events in a
tional authoring approaches.                                                     locked room not being possible until the player gets the
                                                                                 key. Plot points are also annotated with information such
                                                                                 as the plot point’s location or the subplot/quest it is part of.
Copyright © 2009, Association for the Advancement of Artificial Intelli-            The evaluation function, given a total sequence of plot
gence (www.aaai.org). All rights reserved.                                       points that occurred in the world, returns a “goodness”
```

<!-- page: 2 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: tabular spacing heuristic._

```text
measure for that sequence. This evaluation is a specific,         results could have been achieved just as easily using tradi-
author-specified function that captures story or experience       tional trigger-logic authoring techniques.
goodness for a specific world. While an author can create
custom story features, the DODM framework provides a              Script-and-trigger authoring and DM equivalents
set of additive features that are commonly useful in defin-
ing evaluation functions [e.g. Weyhrauch, 1997; Nelson &          Traditionally, interactive story experiences are authored
Mateas, 2005].                                                    with sets of scripts and triggers: the author specifies par-
                                                                  ticular events or world states that trigger scripts, which
   DM actions are actions the DM can take to intervene in
the unfolding experience. Actions can cause specific plot         then perform some sequence of actions in response. This
                                                                  typically involves keeping track of various state flags such
points to happen, provide hints that make it more likely a
                                                                  as which items a player possesses, which NPCs they have
plot point will happen, deny a plot point so it cannot hap-
pen, or un-deny a previously denied plot point.                   talked to, etc., and conditionally triggering actions based
                                                                  on these state flags.
   When DODM is connected to a concrete game world,
                                                                     One way to understand the operations of a DM is to gen-
the world informs the DM when the player has caused a
plot point to happen. The DM then decides whether to take         erate script-and-trigger logic that encodes a policy equiva-
any actions, and tells the world to carry out that action.        lent to the DM’s. We do that by generating a large set of
                                                                  traces of the DM operating on a number of different sto-
   Given this model, the DM’s job is to choose actions (or
no action at all) after the occurrence of every plot point so     ries, and then using a decision-tree learner to summarize
                                                                  the DM’s operation. The internal nodes in the learned deci-
as to maximize the future goodness of the complete story.
                                                                  sion tree, which split on state values, correspond to the
This optimization is performed using game-tree search in
the space of plot points and DM actions, using expectimax         tests that exist in triggers; the leaves, which are DM
                                                                  moves, then correspond to scripts to execute. A particular
to backup story evaluations from complete sequences.
                                                                  path from the root node to a leaf defines a script to execute
                                                                  given the conjunction of the set of triggers along the path.
Related Work
Most related work on drama management is in proposing             Evaluating DM via equivalents
AI architectures with abstract evaluations of their effec-
tiveness. A few projects have also been evaluated through         We propose looking at the equivalent trigger-logic formu-
                                                                  lations of DODM policies to establish the authorial lever-
user tests and simulations: U-Director [Mott & Lester,
                                                                  age of DODM from three perspectives.
2006], PaSSAGE [Thue, et al. 2007], Anchorhead [Nelson
& Mateas. 2005], and EMPath [Sullivan, Chen, & Mateas,               Complexity of script-and-trigger equivalents. First, if
2008]. U-Director was evaluated for run-time performance          the script-and-trigger equivalent of a DM policy is unrea-
                                                                  sonably complex, then scripts-and-triggers is an infeasible
as well quality of the experience with simulated users. The
PaSSAGE project was evaluated by 90 players to evaluate           way of authoring that policy. We can determine the small-
                                                                  est decision tree that achieves performance reasonably
the impact of its drama management model on player ex-
                                                                  close to the drama manager, and qualitatively consider
perience. Anchorhead, the first storyworld using DODM
created since Weyhrauch’s dissertation, evaluated DODM            whether it would be reasonable to hand-author it. Alter-
                                                                  nately, we can start with a reasonable hand-authored policy
using simulated players. EMPath, the first fully-playable
                                                                  for a small story world, and see how the complexity of
real-time game built using DODM was evaluated using
both simulated and real players, to verify that DODM can          required new additions scales as we add additional events
indeed improve the quality of the player experience over          and locations in the story world.
                                                                     Ease of policy change. Second, if experiences can be
the un-drama-managed case. Note that all these evaluation
approaches focus on player experience, not on the authora-        tuned and altered easily by changing some DM parameters
                                                                  (e.g. the author decides the experience should be faster
bility of the drama management approaches. In this paper
                                                                  paced), and the equivalent changes in trigger-logic would
we propose criteria for evaluating authorial leverage, and
apply this criteria to DODM.                                      require many complicated edits throughout the system, DM
                                                                  adds authorial leverage. DODM in particular uses a num-
                                                                  ber of numerical values/weights/probabilities to define
          Measuring Authorial Leverage                            experience goals, which can be changed to re-weight crite-
                                                                  ria in decisions throughout the story. Drama managers can
The evaluation of DODM thus far has shown that it can             also allow for changes such as adding or removing story
improve the quality of the experience using simulated             goals in a planning formalism. If simple changes at those
players [Weyhrauch, 1997; Nelson et al, 2006; Nelson &            levels of authorship result in a significantly different script-
Mateas, 2008] and real players [Sullivan, Chen, & Mateas,         and-trigger-equivalent policy, DM effectively allows an
2008], and has established that the evaluation function can       author to re-script the original from a compact representa-
correspond with expert evaluations of experience quality          tion, or to easily create a set of variations on a given ex-
[Weyhrauch, 1997]. None of this establishes the usefulness        perience.
of DODM for authors, however, if similarly impressive                Variability of experiences. The first two leverage met-
                                                                  rics are based on the relationship between the amount of
```

<!-- page: 3 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: tabular spacing heuristic._

```text
work and the quality of the work’s outcome. This third              script-and-trigger system; or alternately to see what level
measure of leverage is necessary to ensure that there is a          of script-and-trigger complexity is needed to achieve per-
variety of diverse experiences in addition to stories of great      formance similar to the drama manager. The following
quality. It is necessary to consider frequency of variability       subsections will evaluate the authorial leverage of a drama
because high quality stories are easily hand authored, al-          manager by the three performance measures discussed in
though difficult to author in large numbers. An AI system           this paper.
that guides the player on the same high quality experience
every time could, according to the first two metrics, yield         Script and Trigger Equivalents for EMPath
significant leverage, but would not offer significant lever-
                                                                    We performed our preliminary evaluations on EMPath, a
age according to this third metric. In the subsequent sec-
                                                                    Zelda-like adventure game [Sullivan, Chen, & Mateas,
tions we will demonstrate how using DODM simultane-
ously leads to higher quality and significant variation.            2008] that was developed to test DODM in a traditional
                                                                    game genre. It is set in a 25-room dungeon and has, at
                                                                    most, 10 plot points that can possibly occur. In addition to
     Implementing Decision Trees with DODM                          the game, there are 32 DM actions that DODM may
                                                                    choose to employ at various points in the story (33 DM
We induced decision trees from example drama-managed                actions when counting the choice to do nothing).
story traces using the J48 algorithm implemented in Weka,              We ran DODM in this world to generate 2500 drama-
a machine-learning software package.1 Each drama-                   managed story traces, producing 22,000 instances of train-
manager decision is made in the context of a partially              ing data from which to induce a decision tree. To vary
completed story, so the training data is a set of (partial-         pruning, we varied the maximum terminal node size (num-
story, dm-action) pairs, generated by running the expecti-          ber of examples captured by a terminal node), with a larger
max search-based drama manager to generate thousands of             terminal node size resulting in smaller trees (less splitting
examples of its actions. Partial stories (the independent           on data).
variable) are represented by a set of boolean flags indicat-
ing whether each plot point and DM action has happened
thus far in this story, and, for each pair of plot points a and                                               Story Quality Histogram
b, whether a preceded b if both happened.                                              0.5
   The tree that results can be interpreted as a script-and-                          0.45
                                                                                       0.4
trigger system. Each interior node, which splits on one of                            0.35                                                            50 tree
the boolean attributes, is a test of a flag. The path from a                                                                                          175 tree
                                                                        Occurrances
                                                                                       0.3
                                                                                                                                                      200 tree
root node to a leaf passes through a number of such flag                              0.25                                                            300 tree
                                                                                                                                                      1000 tree
                                                                                       0.2
tests, and their conjunction is the trigger that activates the                        0.15
                                                                                                                                                      null
                                                                                                                                                      search
script, the leaf node that indicates which DM action to                                0.1
take. The tree format consolidates common tests to pro-                               0.05
                                                                                        0
duce a compact (and inducible from data) representation of                                   0.25   0.35   0.45     0.55      0.65      0.75   0.85
the total set of trigger conditions and the scripts they trig-                                                     Quality
ger. A given story produces a number of partial story in-
stances, since each step of the story is a decision point for       Figure 1. Story quality histogram of search, null, and decision
the drama manager.                                                  tree policies.
   The tree induced from the drama-management traces can
be used as a drama-management policy, specifying the DM                The histogram above shows the performance of the
action (script) to take in the story states where action is         drama manager in the EMPath story world, compared to
needed (triggers). Decision trees of various sizes can be           the performance of a null policy (which always takes no
induced by varying the pruning parameters: a low degree             DM actions – this is the un-drama-managed experience)
of pruning will effectively memorize the training exam-             and a number of trees at various levels of pruning. The tree
ples, while a high degree of pruning results in a small tree        sizes in the legend refer to the maximum terminal node
exhibiting more generalization (and thus more error) across         sizes of the different trees. It is apparent that the perform-
the training examples.                                              ance of the smallest trees (greatest pruning), such as the
   Any of the policies—the actual DM policy or any of the           one labeled 1000, performs only slightly better than the
decision trees—can be run with a simulated player to gen-           null policy, whereas the best match with the search-based
erate a histogram of how frequently experiences of various          policy (the actual DM policy) is found at moderately low
qualities occur. More successful drama management will              levels of pruning (the “200” tree). In addition, the least-
increase the proportion of highly rated experiences and             pruned trees (e.g. 50) overfit to the particular runs in the
decrease that of lower-rated experiences.                           training set, as we’d expect, resulting in worse generaliza-
   Varying the degree of pruning allows us to see how               tion on the test set, and thus do not capture the DM policy
much performance is sacrificed by limiting to a simple              well either.
1
    http://www.cs.waikato.ac.nz/ml/weka/
```

<!-- page: 4 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: tabular spacing heuristic._

```text
  Figures 2 and 3 show the highly pruned (1000) policy         used; and the plot point give_flute has happened; all con-
with 17 nodes, and the best performing (200) policy with       joined with any tests further up the tree; then take the DM
70 nodes.                                                      action temp_deny_info_use_wax. This is specifying a se-
                                                               ries of exclusion tests (in which case other plot points
                                                               would be appropriate), followed by a choice of what to do
                                                               if all of them pass, depending on whether the flute has been
                                                               given yet. Hundreds of these sorts of rules get automati-
                                                               cally generated; while they could all be authored by hand
                                                               in principle, the fact that even in such a small story world it
                                                               requires a tree of this size to reasonably approximate the
                                                               DM’s performance gives some indication of the infeasibil-
                                                               ity of doing so.
                                                               Ease of Expanding the EMPath world
Figure 2. The poorly evaluating decision tree (17 nodes).      As a way of testing the ease of policy change (the second
                                                               authorial leverage criterion), we created three versions of
                                                               EMPath with increasing world complexity. Table 1
                                                               summarizes the three game variants.
                                                                                  # plot points   # DM actions   # quests   map size
                                                                   empath-small        10              33           3          25
                                                                   empath-med          14              47           5          64
                                                                   empath-large        18              62           6          64
                                                               Table 1. Game policy variations.
Figure 3. The highest evaluating decision tree (70 nodes).     Each story variant is used to create its own decision-tree
                                                               training data, by producing 1000 stories each. The training
Although this zoomed-out view gives only a general idea        data is built from the partial stories from each 1000-story
of the policies, the policy in Figure 3 is already clearly     set. Story worlds that were bigger had larger data sets as a
quite complex for such a small story world, while the more     result (8780, 12594, and 16437 respectively).
reasonable policy in Figure 2 doesn’t perform well.               Recall that the second authorial leverage criterion is ease
   Figure 4 gives a zoomed-in view of part of the best-        of policy change. Using DODM, to incorporate the logic
performing tree, showing some of the equivalent script-        for the new subquests into the game, all the author has to
and-trigger logic that it captures.                            do is provide the DM with the new plot points and DM
                                                               actions, and include the larger world map in the player
                                                               model (see [Sullivan, Chen and Mateas 2009] for details on
                                                               the player model). To change the policy for the script-and-
                                                               trigger-equivalent trees, the author would have to manually
                                                               add and delete trigger conditions to account for the new
                                                               content. Given our EMPath variants and the induced script-
                                                               and-trigger equivalent logic, we need a way of comparing
                                                               the differences between trees in order to measure the ease
                                                               (or difficulty) of changing one tree into another. As a sim-
                                                               ple of measure of this, we find a decision tree that best fits
                                                               the search-based DODM performance for each EMPath
                                                               variant (using the same techniques as described above),
                                                               and compare the sizes of the trees. If the sizes of the trees
                                                               vary significantly between EMPath variants, then there
Figure 4. Zoomed in view of the 200 pruned tree.               would be significant authorial difficulty in manually creat-
                                                               ing new script-and-trigger logic for each variant. Note that,
One trace through this segment specifies the following         even if the trees are the same size, there could be signifi-
rule. If info_key_guard_BEFORE_get_key is false (i.e.          cant differences between trees, differences that would best
either info_key_guard or get_key plot points haven’t hap-      be captured with some version of edit distance. But tree
pened, or the info_key_guard plot point happened second);      size gives us a first approximation of this difference.
and the DM action temp_deny_info_use_wax has not been             Figure 5 graphs the node size of the best-fitting decision
used; and the DM action temp_deny_wax has not been             tree for each of the variants. There is a significant increase
```

<!-- page: 5 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: tabular spacing heuristic._

```text
             in the node size of the decision tree from empath-small to             player is likely to find this action (how rail-roaded the ac-
             empath-med and from med to large. Tree sizes grow sig-                 tion might make them feel). The value of 0.9 (1.0 is maxi-
             nificantly from empath-small to empath-large, meaning                  mum) indicates that this is a strongly manipulative action.
             that, to expand the game from empath-small to empath-                     In addition to defining plot points and drama manager
             large, the author would have to make hundreds of edits to              actions, the author also defines an evaluation function, ex-
             the script-and-trigger-equivalent logic.                               pressed as a linear weighted sum of evaluation features. An
                                                                                    example of an evaluation feature is one that scores how
                               Approximate Decision-Tree Sizes                      motivated the events in a plot point sequence are, that is,
                     800                                                            how often, for each plot point in the sequence, its moti-
Decision Tree Size
                     700
                                                                                    vated_by plot points happen earlier in the sequence. The
                     600
                                                                                    author can tune the relative importance of the different
                     500
                     400
                                                                                    features by adjusting the weights associated with each fea-
                     300
                                                                                    ture. Adjusting the weights of the evaluation features de-
                     200                                                            termines characteristics for the overall quality metric used
                     100                                                            to evaluate the story. So, even without adding any addi-
                      0                                                             tional plot points or DM moves, the author can adjust the
                                 empath-small      empath-med      empath-large
                                                                                    experience purely by changing evaluation features or ad-
                                                story world size
                                                                                    justing weights. Thus, another way to measure ease of pol-
             Figure 5. Approximated complexity for the most optimal decision        icy change would be to learn decision trees for several dif-
             tree policy.                                                           ferent weightings and evaluation feature combinations, and
                                                                                    measure how different these trees are from each other. In
                To determine whether, using the second criterion,                   this paper, we only address policy change associated with
             DODM provides authorial leverage, we need to compare                   adding new plot points and DM moves.
             these hundreds of edits with the authoring work required
             using DODM. To include 8 additional plot points and 29                 Variability of Stories for EMPath
             drama manager actions, the author must describe each plot
             point and action to the DM. Plot points and DM actions are             The final measure for authorial leverage is in the variety of
                                                                                    quality experiences. The simplest way to measure variety
             defined by a list of attribute/value pairs.
                                                                                    is to sum up the total of unique stories. Figure 6 shows the
                Consider the get_sword plot point as an example of
             one of the 8 new plot points added to expand from empath-              histogram for number of unique stories (out of 50,000
                                                                                    simulated player runs) in the empath-small story world for
             small to empath-large.
                           • get_sword
                                                                                    trees of decreasing size, where the leftmost tree is the best
                           • QUEST = sword                                          fitting tree. The first thing to note is that the tree that best
                           • MOTIVATED_BY = {info_sword, info_loc_sword}            matches the DODM policy, the 137 tree, still produces
                           • COORD = 6 0                                            over 6000 unique stories (unique sequences of plot points).
             The quest attribute describes which quest the plot point               Thus, DODM is not forcing a small number of stories to
             is part of, the motivated_by attribute describes the list              always occur. Second, note that as we move towards
             of plot points that should motivate, for the player, this plot         smaller trees (increased generalization), the number of
             point happening, while the coord attribute stores the ini-             unique stories grows (more than 14000 in the smallest
             tial map location at which this plot point will occur (initial         tree). But we know from Figure 1 that smaller trees result
             location of the sword, which can potentially be moved                  in worse story-quality histograms. Thus, the higher script-
             around by drama management actions). When evaluating                   and-trigger complexity of the larger tree (the DM-
             the quality of potential future sequences of plot points, the          equivalent tree) is producing an increase in story quality
             evaluation function will use the attribute values to deter-            while still supporting a wide-variety of experiences.
             mine the quality of a particular sequence; for example, the
             evaluation function would decrease the rating of a se-
             quence in which info_sword and info_loc_sword                              16000
             don’t happen before get_sword, because the player ac-                      14000
             quiring the sword is not motivated in that sequence.                       12000
                                                                                        10000
                Now consider give_player_sword, one of the 29                            8000
             drama management actions added to expand empath_small                       6000
             to empath_large.                                                            4000
                                                                                         2000
                           •    give_player_sword                                           0
                           •    CAUSES = get_sword                                              13           11     8     7     6     6     5     4     3
                           •    MANIPULATION = 0.9                                                 7   tre      1 t 7 tre 1 tre 7 tre 1 tre 1 tre 9 tre 3 tre
                                                                                                             e s re e s e si e si e si e s i e s i e s i e si
             This DM action can force the plot point get_sword to                                               ize    ize ze     ze    ze    ze    ze    ze  ze
             happen by making an NPC walk up and give the sword to
             the player (with appropriate dialog from the NPC). The
             manipulation attribute indicates how manipulative the                  Figure 6. Histogram for unique stories according to tree size.
```

<!-- page: 6 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: tabular spacing heuristic._

```text
Decision-tree policy issues                                        investigating the performance measures, and making use of
Although decision trees are a nice way of automatically            the learned script-and-trigger systems. The evaluation
                                                                   measures will need to be applied to other story systems in
capturing the DM policy in a way that can be interpreted as
                                                                   several story worlds, and ideally, would also compare
a script-and-trigger system, there are a few difficulties with
the policies they produce. The generalization that takes           DODM to other drama-management approaches using a
                                                                   similar evaluation of authorial leverage. The three ap-
place in decision-tree induction can produce choices of
                                                                   proaches we took to evaluate DODM can be further re-
actions that would not be permitted in a particular state.
Since the decision tree learner doesn’t have access to inter-      fined; for instance, performing a more rigorous statistical
nal constraints used by DODM, it may make unsafe gener-            analysis or using the average branching factor to measure
                                                                   story variation. In addition to evaluating the authorial lev-
alizations. Two instances where the decision trees pro-
duced invalid choices of DM action are: 1) taking causer           erage of drama management, the script-and-trigger systems
                                                                   demonstrated that decision tree policies were drastically
DM actions that cause plot points which have already hap-
                                                                   faster at run time, although building the trees may take
pened; and 2) not knowing that denier actions for critical
plot points must be reenabled eventually.                          days to preprocess. Future work should examine how these
                                                                   learned script-and-trigger policies can be used at runtime
   These are in effect uncaptured additional complexities in
                                                                   as a “compiled” version of the optimization-based drama
a correct DM policy that a script-and-trigger system would
need to deal with. An improvement to the decision-tree             manager.
induction that might capture them would be to produce a
number of negative examples of such disallowed choices
of DM actions, and use a decision-tree induction algorithm
                                                                                            References
that allows negative class examples.                               Magerko, B. 2007. A comparative analysis of story representa-
                                                                     tions for interactive narrative systems. Proceedings of AIIDE
                                                                     2007.
           Conclusions and future work                             Mott, B. W. and Lester, J.C. 2006. U-director: a decision-
                                                                     theoretic narrative planning architecture for storytelling envi-
We proposed that a major open issue in the evaluation of
                                                                     ronments. Proceedings of AAMAS 2006.
drama managers is their authorial leverage: the degree of
                                                                   Nelson, M.J. and Mateas, M. 2005. Search-based drama man-
authorial control they provide over an interactive experi-
                                                                     agement in the interactive fiction Anchorhead. Proceedings of
ence as compared to the complexity of the authoring in-
                                                                     AIIDE 2005.
volved. Since authoring drama-manager-like interaction in
                                                                   Nelson, M.J. and Mateas, M. 2008 Another look at search-based
stories is commonly done via scripts and triggers, we pro-
                                                                     drama management. Proceedings of AAAI 2008.
posed that one way to evaluate the authorial leverage a
                                                                   Nelson, M.J. and Roberts, D.L. and Isbell Jr, C.L. and Mateas, M.
drama manager gives is to use decision trees to induce and
                                                                     2006. Reinforcement learning for declarative optimization-
examine a script-and-trigger equivalent form of a drama
                                                                     based drama management. Proceedings of AAMAS 2006.
manager’s policy. We proposed three criteria with which to
                                                                   Roberts, D.L. and Isbell, C.L. 2008. A survey and qualitative
do the comparison: 1) examine the complexity of the in-
                                                                     analysis of recent advances in drama management. Interna-
duced script-and-trigger representation; 2) consider the
                                                                     tional Transactions on Systems Science and Applications 4(2).
ease with which stories can be rebalanced or changed by
                                                                   Sullivan, A., Chen, S., and Mateas, M. From Abstraction to Real-
changing DM parameters versus editing scripts and trig-
                                                                     ity: Integrating Drama Management into a Playable Game Ex-
gers (in this paper, the changes studied involve scaling
                                                                     perience. In Proceeding of the AAAI 2009 Spring Symposium
storyworlds); and 3) examine the variability of stories pro-
                                                                     on Interactive Narrative Technologies II, AAAI Press, 2009.
duced by a script-and-trigger system and a DM policy, e.g.
                                                                   Sullivan, A., Chen, S., and Mateas, M. 2008. Integrating drama
the implied branching factor of the experience.
                                                                     management into an adventure game. Proceedings of AIIDE
   We presented results in inducing a script-and-trigger
                                                                     2008.
equivalent form of a DODM policy in a Zelda-like world,
                                                                   Thue, D., Bulitko, B., Spetch, M., and Wasylishen, E. 2007.
EMPath, and evaluated it by our first proposed criterion,
                                                                     Learning Player Preferences to Inform Delayed Authoring. In
showing that the resulting policies are quite complex to
                                                                     Proceedings of the AAAI 2007 Fall Symposium on Intelligent
hand-author even in this small domain. Secondly, we
                                                                     Narrative Technologies.
showed three versions of EMPath that vary in size, and
                                                                   Weyhrauch, P. 1997. Guiding Interactive Drama. PhD
measured how the decision tree equivalents scaled with
                                                                     dissertation, Carnegie Mellon University.
these changes. This showed that adding a few plot points to
the story world had drastic increases in decision tree com-
plexities. Finally, we showed that using DODM leads to
simultaneously higher quality and lots of variation, by ex-
amining the variety and frequency of unique stories in con-
junction with their story-quality evaluations.
   Three primary directions that future work should take
are: evaluating other systems, developing further ways of
```
