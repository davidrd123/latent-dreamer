# MINSTREL_Remixed

Layout-preserving `pdftotext -layout` extraction.

<!-- page: 1 -->
```text
                                Proceedings of the Sixth AAAI Conference on Artificial Intelligence and Interactive Digital Entertainment
                      Minstrel Remixed: Procedurally Generating Stories
                             Brandon Tearse, Noah Wardrip-Fruin, and Michael Mateas
                                                           Jack Baskin School of Engineering
                                                          University of California at Santa Cruz
                                                         1156 High Street, Santa Cruz, CA 95064
                                                         {batman, nwf, michaelm}@cs.ucsc.edu
                              Abstract                                                   questions and continuing to explore what can be done with
  We are recreating, investigating, and defining new uses for                             such a successful system from the past, we have discovered
  one of the most influential artificial intelligence projects of                          many interesting possible uses for Minstrel and a number
  the past 25 years: Scott Turner’s Minstrel, which is regarded                          of insights into how and why it works.
  as a landmark in both the story generation and
  computational creativity communities. We compare our new
  system, Minstrel Remixed, with the implementation of the                                                              Related Work
  original, and discuss the various additions made during our
  rational reconstruction which facilitate investigations into                           This section briefly outlines previous work done in the
  the inner workings of the system. In conclusion, we evaluate                           three principal areas relevant to this project: classical story
  the performance of Minstrel Remixed and determine that its                             generation systems, case based reasoning, and rational
  results are quite close to those of the original.                                      reconstruction.
                         Introduction                                                    Classical Story Generation. The first major story
While ongoing progress in digital entertainment                                          generation system, which preceded Minstrel and which
technology continues, commercial designers still largely                                 also received significant attention, is Tale-Spin (Meehan
eschew systems for procedural story generation, preferring                               1977). Like Minstrel, this system generates stories which
instead to generate content by hand. In the academic                                     satisfy user-submitted requirements. Tale-Spin creates
literature, projects such as (Appling & Riedl 2009, Roberts                              English stories by planning a method for the main
& Isbell 2009) continue to investigate ways to improve the                               character to achieve her or his goal, using inferences and
nuances of interactive storytelling while others attempt to                              rules to generate a large number of details about a story
create their own systems to investigate ways to use                                      (many of which do little contribute to an audience
knowledge from interactive narrative and story generation                                experience). This contrasts nicely with Minstrel, which
in new fields such as playable games (Drachen & Hitchens                                  performs no logical inferences and which performs all
et al. 2009, Sullivan, Mateas & Wardrip-Fruin 2009). As a                                actions from the point of view of an author, manipulating
complement to current research in the field, revisiting the                               all parts of the story in parallel.
landmark systems of the 1970s and 80s with modern                                         Along with Minstrel and Tale-Spin, the other widely-
computers and techniques may yield fruitful results. One                                 discussed early story generator is Universe (Lebowitz
such landmark is Minstrel, developed by Scott Turner for                                 1985) later reconstructed as WideRuled (Skorupski,
his Ph.D thesis (Turner 1993). Despite the fact that                                     Jayapalan, et al. 2007) which implement a Hierarchical
Minstrel saw no further testing or investigation beyond that                             Task Network planner to generate stories. These systems,
which Turner performed in his dissertation, it is still one of                           like Minstrel, generate stories from an author’s perspective.
the most acclaimed story generation systems to date.                                     Unlike Minstrel’s creative approach, however, they are
 Since a working copy of Minstrel does not exist to be                                  organized around immutable “plot fragments.”
investigated and the system was designed and implemented                                 Case Based Reasoning. Minstrel was developed as an
so long ago, Minstrel is an attractive candidate for rational                            approach to creativity for Case Based Reasoning (CBR)
reconstruction (see below) followed by thorough testing.                                 and its approach has been followed up in some past work,
 In the ongoing reconstruction of Minstrel we investigate                               some rather far afield. For example, TRAM-like operators
several topics, including: what authorial affordances                                    were applied to feature vector-based case representations in
Minstrel provides, what new representations might be                                     the real-time strategy game Wargus in multiple studies
needed to expand the system beyond strictly passive story                                (Weber & Mateas 2009, Aha 2005).
generation, and what new algorithms might be exploited to                                 A bit closer to the original Minstrel, multiple projects
favorably alter its performance. Through answering these                                 have been published in which Vladimir Propp’s work on
                                                                                         story fragment interchangeability (Propp 1968) is
Copyright © 2010, Association for the Advancement of Artificial                          leveraged to assist CBR systems to dynamically generate
Intelligence (www.aaai.org). All rights reserved.
```

<!-- page: 2 -->
```text
stories. One such such system was created to manage a                 pieced together from the story library, the results are
multiplayer game environment (Fairclough 2003) while                  adapted back to modify the current story.
another has the same goals as Minstrel but uses predefined
relevant knowledge to supplement its CBR instead of
applying transformations (Gervas 2007).
Rational Reconstruction. The normal goal of rational
reconstruction is to investigate the inner workings of a
system by altering some components of the system and
comparing the results to the original. Once accomplished,
arguments can then be made about the pieces that were
altered and a view of the potential of the underlying system
can be separated from the arbitrary programming that the                   Figure 1. Overview of Minstrel’s Components
original author implemented.
    A number of projects (Musen, Gennarj & Wong 1995,                 Story Graphs. Stories in MR are represented exactly like
Tate 1995) have successfully used rational reconstruction             those in the original Minstrel, as graphs with labeled
to better understand the fundamental concepts of the                  directed edges and a number of facts attached to each node.
original systems and to explore various improvements. It              There are four types of nodes: state, act, goal, and belief.
should also be noted that a partial reconstruction of                 Each of these can have values assigned to the following
Minstrel was performed (Peinado 2006) in which the                    fields: type, actor, object, value, to, and from. When
knowledge representation systems of Minstrel were                     combined with appropriate edges and a list of nouns, these
recreated in W3C’s OWL. While this did a good job of                  graphs have a one-to-one relationship with completely
proving that the knowledge representation can be                      realized stories. An example story graph can be seen in
successfully recast, without the rest of the system in place          Figure 2, which illustrates a very short story describing a
it is far from a complete reconstruction.                             princess who drinks a potion in order to injure herself.
                                                                      TRAMs. (Transform Recall Adapt Methods) perform all of
                  Minstrel Remixed                                    the fine-grained story graph edits in both Minstrel and
                                                                      Minstrel Remixed. TRAMs are small bundles of operations
The original Minstrel was created to explore how well                 designed to help recall useful information from the story
human creativity could be simulated in the space of story             library. Each TRAM takes a requirements graph as input
generation while taking advantage of the Case Based                   and performs a transformation to produce a new graph. It
Reasoning theories of the time (Turner, 1994). Our                    then performs a recall from the story library based on the
reconstruction, Minstrel Remixed (MR), is a useful but                new graph and attempts to adapt the matches back to the
necessarily imperfect recreation based on written                     original query through a reverse transformation. There are
descriptions of the original Minstrel—since neither the               many TRAMs in the library because each one has its own
source nor a working copy is available today. Our three               unique transformation and reverse transformation, giving
goals for MR differ from those of the original: to identify           the system flexibility. Just as in Turner’s original work, the
elements of the original which were crucial to its operation,         TRAM system in its simplest mode of operation is given a
to explore possible uses for the system outside of its                requirements graph which it uses to perform straight recall
original scope, and to provide a version of Minstrel to the           from the story library. But the creative power of the
research community for analysis and general use.                      TRAMs is their ability to transform the requirements graph
                                                                      into something different and then adapt the results back to
Architecture                                                          a useable story graph. This transformation and adaptation
                                                                      is done through one or more of a library of individual
Like Turner’s Minstrel, MR can be broadly broken down                 TRAMs. The most generic of these TRAMs is called
into two main components. The Transform Recall Adapt                  Generalize Constraint—which simply takes the
Method (TRAM) system is a case based reasoner which                   requirements graph and makes it more general by
modifies story details. Working above the TRAM system is               removing one of the constraints.
the Author Level Planning system which enforces                          An example of TRAMs in action is as follows: a graph is
constraints and improves stories as they are generated.               passed in requiring a knight, John, to die by the sword. By
   Both systems use story subgraphs as their primitives.              transforming this initial query using a TRAM called
Figure 1 shows the high level progression of requests that            Generalized Constraint, we might end up with a resulting
make up a single step of the story generation loop. First an          requirement in which a something dies by the sword. If this
Author Level Plan (ALP) notes that there is a chunk of the            then matched to a story about another knight, Frances who
story that is underspecified or in some way requires                   has a duel with an Ogre and kills it, then the TRAM system
attention (e.g., a character appears without an introduction,         could replace the Ogre with John the knight and return a
major events appear without dramatic support, etc.). This             story fragment about a duel between John and Frances in
causes a call to the TRAM system asking for a story detail            which John dies.
with some specific characteristics. Once that is found or
```

<!-- page: 3 -->
```text
 The creative power of TRAMs ultimately comes from                   generic PATs Generalized Story Templates (GSTs).
their ability to find cases in the story library which aren’t          Because all PATs can be translated into the more flexible
easily recognizable as applicable. In the original Minstrel,          GSTs with little or no work, this gives MR more overall
a search which returns no results will be transformed by a            flexibility in its story frameworks. GSTs have the same
random TRAM and then restarted. This often leads to a                 structure as a completed story graph but they are generally
random sequence of transformations being applied to the               full of holes or placeholder variables that need to be filled
search term before a result is located. In MR however, we             in. An example GST is shown in Figure 4, in which a
have enabled TRAMs to be chosen intelligently, oftentimes             person asks another person for help and successfully
resulting in fewer transformations being needed to get                solicits their aid.
search results and thus more similarity between the original
query and the eventual result. To illustrate the ability that
the TRAM approach has to create truly unexpected results,
we can look at other ways for John to die. Let’s say that the
first TRAM applied is Similar Outcomes Partial Change,
which changes the death requirement of John’s story to
some ambiguous change in health. Let’s also say that,
when this failed to match anything in the story library, the
Generalize Noun TRAM was triggered next, which
generalized John to a Generic person. These changes
allowed the requirements to match with the story fragment
from Figure 2 in which John matches to Princess Peach
who uses a potion to hurt (rather than kill) herself. Upon
adaptation back to the current requirements, the resulting
story is that John commits suicide, killing himself
dramatically with a potion (See Figure 3 for the whole
progression).
                                                                                  Figure 4. Extremely Simple GST
                                                                       Because the structure of the GST provides an outline
                                                                      that is filled in by the other systems in Minstrel Remixed,
                                                                      the quality of the GSTs is directly linked to the quality of
                                                                      the resulting stories. As the very first step in any story
        Figure 3. TRAM progression to suicide.                        creation in MR, any given requirements are used to select
                                                                      an appropriate GST, which is then installed into the story
 Although the original TRAM system functioned well                   graph for the rest of the system to work with.
and created interesting results for Minstrel to work with,            Author Level Plans. The Author Level Planning system
during our reconstruction we decided that the TRAM                    contains a number of Author Level Plans (ALPs) which
system was a ripe place to investigate possible changes. So           guide story construction at a high level. ALPs are
we included hooks in the TRAM system of Minstrel                      responsible for looking at the current state of their target
Remixed which enable programmers to change the manner                 (the current story graph) and planning modifications to it
in which TRAMs are selected and applied. Two selection                with the eventual goal of filling in the whole story graph in
algorithms which are discussed below in the rational                  a desirable manner. There are three classes of ALP which
reconstruction section of this paper have been included and           operate together: Story Producers, Story Checkers, and
can be selected from by toggling a single variable.                   Story Enhancers. Producers are the simplest form of ALP
Generalized Story Templates. Planning Advice Themes                   and operate by handing story subgraphs that have blank
(PATs) are parable based story templates which are used to            variables to the TRAM system to be filled out. The
start stories. The original Minstrel exclusively used PATs            Checkers are only slightly more complex in that they each
and could produce stories to illustrate themes such as “a             search over the story graph for very specific subgraphs and
bird in the hand is worth two in the bush.” In MR we have             add other ALPs to the queue in order to deal with the
opted for a more generalized target and thus have a wide              discovered subgraphs. Enhancers are used to add rich
array of templates, some of which do not describe adages.             characteristics to a story such as tragedy, suspense, and
Although they are the same structurally, we call these more           characterization. As such, enhancers will often add
```

<!-- page: 4 -->
```text
additional details to the story outside of its original bounds,         accidental side-effect of the princess wand. Although in
generally in the form of new nodes being added to the                   this instance MR was stopped before continuing on from
story graph.                                                            this point, additional enhancement could have been
                                                                        brought to bear to further embellish upon the completed
                                                                        story.
                                                                        Modernization. Although Minstrel Remixed attempts to
                                                                        stick to the original designs for Minstrel as much as
                                                                        possible, improvements have been added to make it easier
                                                                        to use in a modern setting. MR is coded in Scala which can
                                                                        operate as a functional language like Minstrel’s LISP
                                                                        variant but can compile down to Java source or Java byte
                                                                        code. Additionally, MR will read XML files into its story
                                                                        library, scripts, and GSTs. This makes it much easier to
                                                                        author new content, swap entire story libraries in and out,
                                                                        and exchange specific stories between instances. We also
                                                                        included an interactive text based shell and an output
                                                                        routine for story graphs which allows for visual
                                                                        representations to be generated as PDFs.
                                                                        Minstrel’s Rational Reconstruction
                                                                        Traditional rational reconstruction is performed in order to
                                                                        investigate the importance of various components,
                                                                        concepts, and algorithms to the functionality of a piece of
                                                                        software. Through studies of this sort we are able to learn
                                                                        what is crucial to the underlying operation of the software
                                                                        and what is merely an implementation detail. We have kept
                                                                        these goals in mind while building Minstrel Remixed.
        Figure 5. A Completely Generated Story
                                                                        Turner’s dissertation (Turner 1993) was used as a starting
                                                                        point for MR but in keeping with the goals of a rational
 Figure 5 shows the ALP System in action, starting with                reconstruction we have sought alterations that might give
an empty story graph in which person A asks person B for                some clues as to the full potential of the system. As a
help in becoming more healthy. The first ALP in the queue                result, during our reviewing of the code base we have
that activates in this case will be a Completeness Checker              supplemented the original functionality in a number of
which looks for question marks or undefined nouns (in this               areas to provide insights into what makes Minstrel unique.
case A and B). The Completeness Checker, upon finding                    While the focus of the project has been on a faithful
the empty variables in the story graph, will put the Story              recreation, in this section we describe the algorithm-level
Producer ALP in the queue with its target set to the nodes              changes and alternate applications of Minstrel that we are
in question. The Story Producer ALP targets ‘Act SIX’                   exploring. By rationally reconstructing Minstrel, we enable
with its question marked entry and pulls ‘Goal FIVE’ and                these explorations of Minstrel’s generative model.
‘State SEVEN’ in as references and passes the trio of nodes              In Minstrel the TRAMs are a crucial aspect of the
off to the TRAM system to get a matching story fragment.                functioning of the system. TRAMs function by being given
It gets back a story in which MrKnight uses a magical                   a requirement which they then transform and attempt to
Princess Wand on Roselyn to make her healthy. In cleaning               match. Minstrel implemented an index tree that spanned
up, the producer ALP changes all instances of A and B in                the story library in order to quickly retrieve only those
the story to match Roselyn and MrKnight and returns the                 cases which were relevant for matching. MR deviates from
story shown in figure 5. Once that is complete, a checker                the original functionality in that it uses the increased
ALP called Accidental Consequences runs over the story in               processing power of modern machines to find all subgraphs
search of acts which have outgoing intends edges but no                 in the story library which have the same types of nodes
outgoing accidents edges. This checker is designed to find               (i.e., State, Act, Goal, Belief) in the same order as the
places where Story Enhancers can be brought to bear to                  requirement graph and then attempts to match against them
make the story more interesting or detailed. In this case it’s          all. Additionally, where Minstrel only has one TRAM
trying to find good candidates for side effects of main plot             application strategy, a depth-limited depth-first search of
actions which can be either left alone to provide                       sequentially applied random transforms, MR was
background details or which can later be woven back into                implemented with a hook upon which any appropriate
the plot. It notes two such places and queues an                        algorithm can be mounted and toggled on or off. As can be
enhancement ALP which decides that the extra state, EL:2                seen in figure 6, Minstrel’s random TRAM application (zig
should be added to enhance the story by adding an                       zag arrows) is inefficient and often requires more
```

<!-- page: 5 -->
```text
transformation to return results than the optimum                                               Evaluation
transformation. Although transformations are directly
correlated with perceived creativity of results, more                   Since the original Minstrel is not available for comparison,
transformations also tends to lead to a higher likelihood of            the only evaluative steps possible are to look at the
incompatible returns, leading to nonsensical story                      examples included in Turner’s book and dissertation. When
fragments. In contrast to the random selection method, the              compared with these examples, MR appears to work as
currently implemented graph distance algorithm (the direct              intended. One example of a successful creative
line from origin to closest point on figure 6), is able to very          transformation and recall by the original Minstrel is given
efficiently ascertain which TRAMs would be required in                   in figure 2. It was later reproduced by MR. Many of
order to match the requirements graph to the graph in                   Turner’s other examples were tested and shown to work as
question. By giving a penalty value to each TRAM and                    well, including one example which has been often used to
applying this algorithm to all possible matches, MR is                  demonstrate one of the major flaws with Minstrel in which
provided with a sorted list of the closest matches (lowest              a knight attempts to curry favor with a princess by giving
penalties) from which it can choose and then perform the                her a hunk of meat (created by transforming a story about a
proscribed TRAMs to retrieve a solution. With some                      knight giving a hunk of meat to a dragon in order to
random weighting applied to prevent MR from always                      appease it). Although there are obvious reasons for this
selecting the closest match, this method approximates the               outcome, it demonstrates that MR shares its progenitor's
capabilities of the random method without deviating so far              limitation of not understanding any of the common sense
from ideal that nonsensical results are returned.                       of the worlds behind the stories that it tells. It’s reasonable
                                                                        to assume that both dragons and princesses like many of
      Noun Differences
                                                                        the same things (gold, gems, etc.) but there’s no way for
                                                                        MR to tell whether a gift for a dragon, horse, or hermit
                                                                        would be of any interest to a princess. There is obvious
                                                                        potential for work to be done in this area and we plan to
                                                                        investigate means of providing some domain-appropriate
                                                                        common sense knowledge MR as needed for projects in
                                                                        the future.
                                                                           Although we have no way of knowing how many story
                         Value Differences                              fragments were incorporated into the original, we do know
Figure 6. TRAM Matching. Minstrel’s original random                     that Minstrel’s story library was roughly equivalent to the
 TRAM selector (zig-zag) compared to graph distance                     content of a few short stories. MR now has forty stories of
                                                                        varying length (in two domains) along with eleven of the
            TRAM selector (slanted path).
                                                                        original twenty-four TRAMs. The current contents of the
                                                                        system have proved rich enough to procedurally generate
Interactivity. An interesting potential change between                  many viable story fragments from the original Arthurian
Minstrel and Minstrel Remixed is made possible by the                   domain that Turner used. For completeness’ sake, we have
dramatic improvement in processing power that has taken                 included a new domain of storytelling that revolves around
place in the past two decades. While MR has not been                    conspiracy theories. In this domain we had MR generate
tested with thousands of stories in its library to search over,         150 story fragments and of those, some (ten to twelve)
initial tests of story creation happen instantaneously rather           appear to be interestingly unexpected (e.g., assassination
than in tens of minutes (as was the case for Minstrel).                 by alien abduction) and only 7 appear nonsensical. While
Using the new speed inherent in MR, interactivity can be                we acknowledge that a fantastic domain such as conspiracy
achieved in realtime. Although the thrust of the current                theories lowers the bar for fragments being useful, these
work has been towards recreating a functioning version of               results indicate that MR is capable not only of reproducing
Minstrel, care was taken to enable new functionality in the             much of the original results of Minstrel but also of
ALP system such that all actions that are taken are                     generating stories in a completely novel domain.
recorded in a manner that enables the system to roll back
story creation to a specific point and continue construction
along a different path. We have successfully performed                                        Future Work
proof of concept tests in which a story has been generated,
rolled back to a specific point, modified by a user, and then             There are many future challenges and opportunities for
regenerated, incorporating the story prefix (the part that has           Minstrel Remixed. To begin with, problems due to MR’s
already happened) and the modification. The potential to                 inability to understand common sense in its domain (such
support interaction opens a number of research directions               as princesses being gifted meat) are unacceptable for many
that were previously closed to the Minstrel system.                     uses. We believe that integrating some form of knowledge
                                                                        base (such as ConceptNet (Liu & Singh 2004)) for use in
                                                                        the TRAMs might allow us to improve the results of the
                                                                        TRAMs without impacting the creative space too greatly.
```

<!-- page: 6 -->
```text
 Additionally, the story library for MR is currently small.           Gervas, P., Diaz-Agudo, B., and Hervas, R. Story plot
An authoring tool is currently in development, which                   generation based on cbr. Applications and Innovations in
allows users to rapidly create stories and fill MR’s library.           Intelligent Systems XII, 33–46.
By use of this tool, we hope to flesh out the Arthurian and             Lebowitz, M. 1985. Story-telling as planning and learning.
Conspiracy domains and enable other users to create and                Poetics, 14(6).
rapidly fill their own domain libraries as well. In addition
                                                                       Liu, H. and Singh, P. 2004. ConceptNet—a practical
to filling the story library, a secondary task that needs to be
                                                                       commonsense reasoning toolkit. BT Technology Journal,
accomplished is to implement a system by which MR can
                                                                       22(4):211–226.
output English text in a manner equivalent to that
employed by the original Minstrel. A prototype for this                Meehan, J. 1977. Tale-spin, an interactive program that
system is currently in production, but more work is needed             writes stories. In Proceedings of the Fifth International
before it will be helpful in translating MR’s graphs into              Joint Conference on Artificial Intelligence.
English.                                                               Musen, M., Gennari, J., and Wong, W. 1995.A        rational
 Aside from connecting to common sense reasoning,                     reconstruction of INTERNIST-I using PROTEGE-II. In
work to fill out the story library, and developing the ability          Proceedings of the Annual Symposium on Computer
to report stories in English, additional rational                      Application in Medical Care
reconstruction would still be fruitful. Although hooks were            Peinado, F., Gervas P. 2006. Minstrel reloaded: from the
embedded into the TRAM system to allow us to investigate               magic of lisp to the formal semantics of OWL. in
the effects of alternate searching algorithms, additional              Technologies for Interactive Digital Storytelling and
investigations could no doubt be made into the ALP                     Entertainment, 93–97.
system. Finally, we believe that there are many ways in
which MR could be made to be interactive.                              Propp, V. 1968. Morphology of the Folktale, trans.
                                                                       Laurence Scott (Austin: University of Texas Press).
                                                                       Roberts, D., Narayanan, H., and Isbell, C. 2009. Learning
                       Conclusion                                      to Influence Emotional Responses for Interactive
                                                                       Storytelling. In Proceedings of the 2009 AAAI Symposium
This paper has described Minstrel Remixed, a                           on Intelligent Narrative Technologies II.
reconstructed and working version of the original Minstrel
system created by Scott Turner. We have discussed a                    Skorupski, J., Jayapalan, L., Marquez, S., and Mateas, M.
number of improvements to the original design as well as               2007. Wide ruled: A friendly interface to author-goal based
some of the potential applications for Minstrel Remixed                story generation. LECTURE NOTES IN COMPUTER
which lie well outside of the originally intended uses of              SCIENCE, 4871:26.
Minstrel. We believe that future work on Minstrel Remixed              Sullivan, A., Mateas, M., and Wardrip-Fruin, N. 2009.
will provide interesting insights into the nature of the               QuestBrowser: Making Quests Playable with Computer-
creativity demonstrated by the TRAM system, the                        Assisted Design.
flexibility of the system as a whole, and the utility that              Tate, A. 1995. Integrating Constraint Management into an
Minstrel Remixed will have in Interactive Narrative                    AI Planner. Artificial Intelligence in Engineering, 9(3):
applications.                                                          221–228.
                                                                       Turner, S. (1993). MINSTREL: a computer model of
                  Acknowledgements                                     creativity and storytelling.
                                                                       Turner, S. (1994). The creative process: a computer model
This material is based upon work supported by the                      of storytelling and creativity.
National Science Foundation under Grant No. 0747522.
                                                                       W. Aha, D., Molineaux, M., and Ponsen, M. 2005.
                                                                       Learning to win: Case-based plan selection in a real-time
                       References                                      strategy game. Case-Based Reasoning Research and
                                                                       Development, 5–20.
Appling, D. and Riedl, M. 2009. Representations for                    Weber, B. and Mateas, M. 2009. Conceptual
Learning to Summarize Plots. In Proceedings of the 2009                Neighborhoods for Retrieval in Case-Based Reasoning. In
AAAI Symposium on Intelligent Narrative Technologies II.               Proceedings of the 8th International Conference on Case-
Drachen, A., Hitchens, M., Aylett, R., and Louchart, S.                Based Reasoning: Case-Based Reasoning Research and
2009. Modeling Game Master-based story facilitation in                 Development, 357.
multi-player Role-Playing Games. In Proceedings of the
2009 AAAI Symposium on Intelligent Narrative
Technologies II, 24–32.
Fairclough, C. and Cunningham, P. 2003. A multiplayer
case based story engine. In 4th International Conference
on Intelligent Games and Simulation, 41–46.
```
