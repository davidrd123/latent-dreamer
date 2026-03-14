# Chapter 4: Learning through Daydreaming

<!-- page: 105 -->

Why do humans spend time daydreaming about hypothetical past and future situations? There must be something that is gained. Humans must be retaining or learning something in the process of daydreaming. Daydreaming should result in similar benefits for computer programs: Instead of leaving a computer sitting idle when it has no task to perform, it should be daydreaming in order to improve the usefulness and efficiency of its future behavior.! Not every daydream need be useful. In fact, even if only 10 percent (or less) of all generated daydreams eventually prove to be of use, a daydreaming system still has an advantage over systems which merely sit idle when unoccupied with tasks. Previous researchers have addressed learning from real experiences (Lebowitz, 1980; DeJong, 1981; Schank, 1982; Carbonell, 1983; Kolodner, 1984). We address learning from imagined experiences or thought experiments: If a situation arises which is similar to one explored in a previous daydream, the program should be equipped to handle that situation better and more efficiently. For example, when the program daydreams about alternative actions which might have prevented a past failure, those actions may be recalled and applied _in case a similar situation comes up. Similarly, if possible future situations and responses to those situations are daydreamed and stored in memory, they may be used if those situations should arise. In this chapter, we address how DAYDREAMER learns—improves its future 1The idea of exploiting free processing time has been around in one form or another for years. In an early version of the UNIX operating system, a lowest-priority background process applied any idle CPU time toward the task of calculating the first million digits of the mathematical constant e (Ritchie & Thompson, 1974). The PARRY program (Colby, 1975), a computer simulation of a paranoid patient, continued processing while waiting for the next input of the psychiatrist by using its own output as input (K. M. Colby, personal communication, March, 1987).

<!-- page: 106 -->

behavior—through the exploration of hypothetical situations. We consider: (a) the generation of alternative past scenarios, (b) the generation of possible future scenarios, (c) the use of such scenarios in improving future behavior, (d) the formation and later recall of intentions to perform certain future actions. First, we present the mechanisms of analogical planning and episodic memory which provide the basic framework for learning in DAYDREAMER. Second, we explore what is meant by learning through daydreaming. Third, we discuss several daydreaming goals which initiate and guide learning activity. Fourth, we present strategies for daydream evaluation and selection—mechanisms for decision-making in daydreaming. Fifth, we discuss mechanisms for the formation and recall of intentions. Finally, we review related previous work in artificial intelligence.

## 4.1 Episodic Memory and Analogical Planning

In order for people to learn through daydreaming, this activity must somehow modify memory. Tulving (1972, 1983) proposed a distinction between two types of human memory—episodic and semantic.?, Episodic memory contains concrete, unique autobiographical memories, while semantic memory contains abstract, generalized world knowledge. DAYDREAMER contains an episodic memory which includes both real and imagined episodes. That is, daydreams generated in the past are stored in episodic memory along with real experiences (gained in performance mode or taken as input). In addition to personal experiences, “real” episodes include secondhand experiences which do not contain DAYDREAMER as a character. Currently, secondhand experiences are added to the program as hand-coded episodes. DAYDREAMER also contains a semantic memory which consists of its collection of planning and inference rules. Adding daydreams to episodic memory enables learning via the following sequence: (a) a daydream—hypothetical past or future scenario of potential use in the future—is first generated; (b) next, the daydream is evaluated and possibly stored in episodic memory; and (c) if the daydream is later retrieved from episodic memory, it may then be applied in generating new internal or external behavior. What evidence is there that humans store daydreams in episodic memory’ Just as real experiences may be forgotten (Linton, 1982), so may daydreams (Smirnov, 1973). (Of course, it is possible that the experience still exists somewhere in long-term memory and could be recalled if one were able to find the right retrieval index.) Nonetheless, humans often do remember their daydreams—sometimes in great detail: Subjects are able to recall and report daydreams seconds after they occur (Klinger, 1978) and days, months, and?Tulving (1972) introduced the term episodic memory; the term semantic memory had been used previously by Quillian (1968). See Tulving (1983, pp. 18-21) for a review of the history of the episodic-semantic distinction in psychology and philosophy.

<!-- page: 107 -->

years afterwards (J. L. Singer & Antrobus, 1963, 1972). Retrospective reports of daydreaming are often quite elaborate (McNeil, 1981; Varendonck, 1921). People sometimes confuse real and imaginary experiences (M. K. Johnson & Raye, 1981). People frequently construct future plans (J. L. Singer, 1975) or intentions (Lewin, 1926/1951) during daydreaming which are later carried out. Varendonck (1921) reports near-complete recall of his daydreamed plans for writing: “When I am composing letters I often afterwards write them almost exactly as I worded them in my phantasy” (p. 327). How are real experiences distinguished from daydreamed ones in episodic memory? Very subtle memory modifications may occur during daydreaming: Neisser’s (1982a) analysis of the testimony of John Dean shows that he often remembered conversations in terms of his own fantasy about how those conversations should have been, rather than how they actually were. M. K. Johnson and Raye (1981) propose that humans distinguish memories of real and imagined experiences based on differences in how those experiences are represented in memory as well as through general reasoning processes. They hypothesize and present empirical evidence that externally generated memories have a greater amount of spatial and temporal contextual information, sensory attributes, and semantic detail, while internally generated memories have associated with them a greater amount of information about cognitive operations. In DAYDREAMER, episodes are simply tagged as to whether they are real or imaginary.

### 4.1.1 Representation of Episodes

Real and imaginary episodes are represented as planning trees produced by the planner. Figure 4.1 shows the planning tree for the REVENGE] (see page 4) daydream. A planning tree consists of a tree of goals, with the root of the tree (e.g., REVENGE) as the top-level goal, and remaining nodes (e.g., STAR, MTRANS, VPROX, and so on) as various subgoals. Leaves of the tree (e.g., RTRUE, KNOW, and so on) are subgoals which did not require further planning because their objectives were already satisfied in the world model or contezt. _ RTRUE is an objective which is satisfied in any context. Actions are never

* leaves because all actions have preconditions in DAYDREAMER. Each nonleaf

goal (e.g., REVENGE) in the tree has a two-way connection to the rule (e.g., Revenge-Plan1) from which the division of that goal into subgoals was derived. Recall from Chapter 2 that planning rules consist of an antecedent pattern specifying a goal and a consequent pattern specifying one or more subgoals. A planning rule is applicable to a given active goal if its antecedent unifies with the objective of the goal. The objectives of the subgoals for the active goal are then obtained by instantiating the consequent pattern of the planning rule with the bindings obtained from the unification. An episode is actually represented as an entire context, which includes the planning tree as well as any states and inference chains not contained within the planning tree.

<!-- page: 108 -->

REVENGE (Harrison, LOVERS) yeild

APHIS

MTRANS(Harrison,Me,GOAL-LOVERS)

GOAL (Harrison,LOVERS)

Revenge-Plan1 FAILED-GOAL (Harrison, LOVERS)

Failed-Rel-Goal-Plan1

Lovers-Theme-Plan

BELIEVE(Harrison,NOT-LOVERS) ROMANTIC-INT(Harrison,Me)

M-BREAK-UP

BELIEVE(Harrison,STAR(Me))

RTRUE

GOAL(Harrison, LOVERS) VPROX

BELIEVE(HarrisonNOT-GOAL-LOVERS-ME)

M-PHONE

MTRANS(Me,Harrison,NOT-GOAL-LOVERS)

KNOW

STAR(Me)

v

VPROX

M-STUDY(Me,ACTOR)

y

RTRUE

Figure 4.1: Planning Tree for REVENGE1

Episodes arise in three ways in DAYDREAMER: through generation of a daydream, through participation in a performance mode experience (including external input), and by manual entry of a hand-coded episode. In the program, daydreams, performance mode experiences, and hand-coded episodes are all represented in the same fashion.

### 4.1.2 Storage of Episodes

The episodic memory of DAYDREAMER will contain many episodes. An episode will be useful to the current situation when that episode contains a plan for achieving a goal similar to one which is currently active. Thus in DAYDREAMER, a real or imagined episode ts indexed under the planning rule associated with the root goal of the episode. Since (a) an episode will be retrieved in

planning when its rule is selected, and (b) a rule is selected when the antecedent of that rule unifies with (matches) the objective of an active subgoal, episodes are effectively indexed under goal objective patterns. Every single nonleaf subgoal in a planning tree becomes the root of an episode. Thus a top-level planning tree actually becomes many episodes—one for the top-level goal and one for each nonleaf proper descendant of the toplevel goal. Effectively, then, episodes are indexed under goal objective patterns corresponding to each of the subgoals achieved in that episode. Figure 4.2 shows a portion of an episodic memory consisting of REVENGE1

(see page 4), REVENGE?

(see page 91), and REVENGES

(see page 11). Each

<!-- page: 109 -->

Revenge-Plan1

EE

REVENGE1 EPISODE CLeat ti,

q

REVENGE2 EPISODE aaa ea

REVENGE3 EPISODE a FAILED-GOAL(Harrison,LOVERS)

Mtrans-Plan2

REVENGE1.1 SUBEPISODE GOAL(Harrison,LOVERS)

REVENGE1.2 SUBEPISODE

REVENGE1.3 SUBEPISODE

©MTRANS(Harrison,Me,GOAL-LOVERS)

REVENGE1.21 SUBEPISODE eile

Figure 4.2: Indexing of Episodes by Rules

of these episodes is indexed under the Revenge-plan rule (and thus, in effect, under the REVENGE goal which is the antecedent of that rule). The top-level REVENGE goal is connected to three subgoals, which are the root goals of three sub-episodes, REVENGE1.1, REVENGE1.2, and REVENGE1.3. These sub-episodes are also indexed under rules; in the figure, only the index of REVENGE1.2, Mtrans-plan2, is shown. The MTRANS subgoal in turn has VPROX< as a subgoal, which is the root goal of the sub-episode REVENGE1.21. The entire planning tree, not shown in the figure, is broken down into subepisodes in this fashion. Other researchers have previously noted the importance of goals as memory indices: Reiser (1983) argues that episodes are indexed under causally relevant features; specifically, that episodes are indexed under goals and goal relation-

ships. Schank’s (1982) MOPs are indexed under goals.

### 4.1.3 Analogical Planning

Once imagined and real episodes are stored in episodic memory, how are they later retrieved and applied in an appropriate situation? Episodes, whether imaginary or real, are employed in generating new daydreams or external behavior through the process of analogical planning: An existing planning tree for achieving a particular goal, called the source goal, is used to guide the generation of a planning tree for achieving a new goal, called the target goal.

When planning to achieve a given subgoal, planning rules are first found which apply to that subgoal. A number of episodes are then retrieved using

<!-- page: 110 -->

Figure 4.3: Cases in Analogical Planning

those planning rules as indices. application.

One or more

episodes are then selected for

The regular planning algorithm involves repeatedly selecting an active subgoal and sprouting plans—activating further subgoals—for that subgoal, with each plan in its own separate context. When an episode is retrieved and selected, the entire planning tree is not employed at once. Instead, the tree serves as a suggestion to the planner about which planning rules to employ in sprouting plans for active subgoals. The following cases, shown in Figure 4.3, may occur during this process:

- Case 0: rule is applicable: A rule which was applicable to a subgoal in the source situation is applicable in the target situation. Therefore, it is applied. Case 1: rule is inapplicable: A rule which was applicable to a subgoal in the source situation is not be applicable in the target situation. Regular planning must be invoked in order to achieve this subgoal. Case 2: source tree bottoms out before target: In planning to achieve the target goal, a subgoal which was satisfied in the source situation (and is thus a leaf of the source episode) is not satisfied in the target situation. Regular planning must be invoked in order to construct a plan for achieving this subgoal in the target situation.

Case 3: target tree bottoms out before source: A subgoal which required planning in the source situation might already be satisfied in the target situation. In this case, no further planning for this subgoal need be performed. Portions of the source episode which do not apply to the current situation are therefore automatically repaired in the course of planning. Whenever regular planning is invoked above on a given subgoal, yet another episode may be found applicable to that subgoal; that is, analogical planning

<!-- page: 111 -->

may be invoked recursively. As a result, a newly constructed episode may be composed of multiple previous episodes and generic planning rules. Once an episode for achieving a goal is found, alternative plans for achieving each subgoal—such as those specified by other generic planning rules or episodes—are not employed since the purpose of employing the episode is to reduce the search through such possibilities. Only when a suggested rule proves inapplicable, or if the source episode bottoms out before the target episode, are other possibilities considered. Here are some specific examples of analogical planning in DAYDREAMER: Since both imaginary and real episodes may be employed in generating both imaginary and real episodes, there are four possibilities. We consider each in turn.

First, a previous daydream may be employed in generating a new daydream. For example, in LOVERS1, DAYDREAMER asks Harrison Ford out on a date and he turns her down. DAYDREAMER then produces REVENGE1. Later when DAYDREAMER is turned down by another movie star, the following daydream is generated by analogy to REVENGE1:

REVENGE2 | remember the time | got even with Harrison Ford for turning me down by studying to be an actor, being a star even more famous than he is, him calling me up, him asking me out, and me turning him down.

| study to be an actor. | am a star even more famous than Robert Redford is. He calls me up. He asks me out. | turn him down. | get even with him for turning me down. | feel pleased. In this case, no repairs or completions were necessary—the entire source episode, as previously shown in Figure 4.1, was employed verbatim (after mapping Harrison Ford to Robert Redford) in the target episode, as shown in Figure 4.4. P

In the daydream REVENGES (see page 11), however, repairs must be performed. As shown in Figure 4.5, the GOAL subgoal of the target episode cannot be achieved by the Lovers-theme-plan rule of the source episode (as shown previously in Figure 4.1). Since this rule is inapplicable in the target situation, regular planning is invoked to achieve this subgoal. The rule Employment-theme-plan is applied to the subgoal, followed by further rules. Except for this subtree, the remainder of the source episode is employed without further modification (other

than parameter substitution). Second, a previous real episode may be employed in generating a new daydream. Suppose DAYDREAMER now wishes to get in touch with Harrison Ford

to try and ask him out again (just in case he changes his mind!). Hand-coded episodes contain plans for finding out the movie star’s telephone number which are recalled and employed through analogical planning in RECOVERY1 (see

<!-- page: 112 -->

REVENGE (Robert, LOVERS) Revenge-Plan1

FAILED-GOAL(Robert, LOVERS)

MTRANS(Robert,Me,GOAL-LOVERS)

GOAL(Robert, LOVERS)

Failed-Rel-Goal-Plan1

Lovers-Theme-Plan

GOAL (Robert, OVERS)

BELIEVE(Robert,NOT-LOVERS)

ie

BELIEVE(Robert,NOT- iyLOVERS-ME)

= M- te

MTRANS(Me, Robert, cele-LOVERS)

ROMANTIC-INT(Robert,Me)

M-BREAK-UP

BELIEVE(Robert,STAR(Me))

RTRUE

VPROX

KNOW

STAR(Me)

M-STUDY(Me,ACTOR)

y

RTRUE

Figure 4.4: Planning Tree for REVENGE2

REVENGE (Agatha, EMPL) Revenge-Plan1

GOAL(Agatha,EMPL)

MTRANS (Agatha, Me,GOAL-EMPL)

j

:

FAILED-GOAL (Agatha, EMPL)

Failed-Rel-Goal-Plan1

POS-ATTITUDE(Agatha,EMPL)

Nepigeetlre tyi) ii

BELIEVE (Agatha, coreg EMPL-ME)

; BELIEVE(Agatha,EXECUTIVE(Me)) RRR

eee

ene

wenn anne

nnn

Repaired portion

M- sya KNOW

MTRANS(Me, Agatha, wih 4b VPROX

Figure 4.5: Planning Tree for REVENGE3

<!-- page: 113 -->

TARGET EPISODE (DAYDREAM)

KNOW(Ne,asia

RECOVERY

saeMe, TELNO(Rich))

MTRANS

kh

VPROX

meal TRW-File)

M-PHONE

KNOW(TRW-File it

M-risesTRW-Flle)

KNOW(Me, TELNO(Harrison))

MTRANS(Alex,Me, TELNO(Harrison)) KNOW(TRW-Flle, TELNO(Harrison))

WH

Wachee

M-LOGIN(Alex, TRW-File)

Figure 4.6: Analogical Planning in RECOVERY1 page 6). As shown in Figure 4.6, the source episode becomes a subtree of the

target episode (daydream). The daydream RATIONALIZATIONI (see page 4) provides another example of a (hand-coded) real episode applied to the generation of a daydream—in this case, however, only a small component of the daydream results from analogical application of the episode, as shown in Figure 4.7. The source episode bottoms out at the RPROX subgoal, and so regular planning is employed in order to expand this subgoal. Third, a previous imaginary episode may be employed in generating a realworld (performance mode) episode. For example, consider the following daydream:

REHEARSALI1 | go to his house. We go to a restaurant. We eat dinner. We go to the Fox International Theater. We watch a movie. We go to his house. We kiss....

This daydream is employed through analogical planning in the following performance mode experience:

LOVERS3 External action:

I go to Vicente Foods.

<!-- page: 114 -->

(Forward Other Planning) ACTING-EMPL(Harrison,Paramount)

ACTING-EMPL(Harrison,Paramount)

RPROX(Harrison,Cairo)

RPROX(Harrison,Cairo)

y Perrrttteetetree

Pe

Soe

Figure 4.7: Partial Tree for RATIONALIZATION1

Input: A guy asks you what time it is. What do you know!

External action: I tell him him at a restaurant.

I would

like to have

dinner

with

Input: He tells me his address.

External action:

I buy groceries.

I go home.

External shared action: We go to a restaurant. We go to his house. We kiss.

I eat.

We eat dinner.

Fourth, a previous real episode may be employed in generating another real episode. For example, after the successful performance mode experience of LOVERS3, DAYDREAMER could apply this episode by analogy the next time it meets someone. When the target situation is sufficiently different from the source situation, nontrivial repairs will be made to the source episode. However, if previous episodes are frequently employed in generating new daydreams without repair, then the program will generate what is basically the same daydreain over and over again. For example, REVENGE2 is a trivial variation on REVENGE1; future similar experiences will give rise to yet more similar daydreams. There are several ways in which DAYDREAMER attempts to cope with this problem

(even if recurrent daydreams do occur in humans [J. L. Singer, 1975, pp. 17-32]):

- Each episode associated with a given rule is moved to the end of the list of episodes associated with the rule whenever it is employed in generating

<!-- page: 115 -->

a daydream. When selecting episodes for application, episodes earlier in the list are given priority over episodes later in the list.

- A collection of strategies for creativity, including serendipity, mutation, and environmental input (discussed in Chapter 5), are employed to get the program out of a rut by forcing it to generate or notice new possibilities.

Furthermore, if a daydream generated by analogy to a previous daydream is stored again in episodic memory, there will be an even greater proliferation of almost identical episodes. For this reason, a daydream is not stored in memory if it was derived from a previous episode without any repairs. That is, a new daydream is not stored if it is isomorphic to a previous episode—if it consists of exactly the same tree of rules. Thus REVENGE? is not stored since it was derived wholly from REVENGE1 without repairs; however, REVENGES is stored since it was constructed through repair of REVENGE1. Whether or not repairs are performed is monitored during planning, so that a graph comparison does not have to be performed later on.

### 4.1.4 Issues for Theories of Episodic Memory

Any theory of episodic memory use must address the following questions:

- What information is provided by an episode which is not already available as a result of having found the appropriate knowledge structure for accessing that episode? For example, suppose that episodes are represented

in terms of scripts (Schank & Abelson, 1977). Scripts may then be used as memory structures for retrieval of an episode involving the same script as the current situation. However, what is the use of retrieving such an episode? Normally, one might wish to employ such an episode in making predictions about the current situation. However, in order to retrieve the episode, one must first determine what script the current situation is an instance of; having done this, the retrieved episode provides no information not already contained in its script. The only reasonable response is that episodes must somehow contain more information than is contained in their indices. It is important to specify exactly what that information is. Scripts were used as an example memory structure. In the offshoot of scripts called MOPs (Schank, 1982), episodes are in fact indexed by deviations of a scene of a script rather than by scripts themselves.

- What information is provided by the episode that is not available elsewhere’? For example, if episodes are merely combinations of existing rules from semantic memory, what is gained by accessing the episode? If episode events are fully explained in terms of existing rules, what new knowledge do those events provide? For example, in the explanation-based generalization of Mitchell, Kellar, and Kedar-Cabelli (1986), both the training example and the justified generalization—that is, a generalization of

<!-- page: 116 -->

the proof which demonstrates that the example is an instance of a given concept—contain no information that is not derivable from the existing concept definition and the domain theory. However, the justified generalization does satisfy a given criterion of operationality (Mostow, 1981)— that the generalization be useful for recognizing examples of the concept efficiently. The requirements that DAYDREAMER increase efficiency and improve control in closed-system learning (as described in the next section) are both examples of operationality criteria. How do we address these questions in the case of DAYDREAMER? Episodes indeed contain more information than is contained in their indices. In particular, episodes consist of instantiated planning trees enabling search to be reduced. Indices are a heuristic for relevance in DAYDREAMER. Episodes are retrieved when their indices are currently active. Once an episode is retrieved, it may be discarded based on evaluations of realism and desirability associated with the episode (discussed later in the chapter). If not, the episode will be applied in planning and it may or may not prove useful. The representation of episodes in DAYDREAMER is based to a large extent on semantic memory—episodes are represented as planning trees which refer to generic rules, which are in turn part of semantic memory. In Figure 4.2, for example, the rules Revenge-planl and Mtrans-plan2 are part of semantic memory and the remainder is part of episodic memory. In addition, however, some episodes contain information that is not present elsewhere in any form, that has no basis in terms of other knowledge. That is, episodes may contain causal relationships—inaccessible episodic rules—that are unknown outside that episode, and are thus not considered a part of semantic

memory.

## 4.2 Definition of Learning

What does it mean for DAYDREAMER to learn through daydreaming—a process carried out without interaction with the external world? There is a potential problem in claiming that a closed system is able to learn: Any desired state of the system (including processes and data) given the initial state of the system is reachable whether or not the system has “learned”—that is, reached some intermediate state between the initial and desired states. In other words, if you can learn something without external input, then you must really already know it. First of all, DAYDREAMER is not a closed system. In fact, it accepts the following sources of input which contribute to learning:

- Input of events and states in performance mode: This enables the program to gain new experiences automatically through interaction with the external environment. External input is an important source of feedback

<!-- page: 117 -->

in learning:

97

Daydreamed plans may be modified if they do not succeed

when tested in the real world.®

- Input of hand-coded episodes: Normally, hand-coded episodes are loaded when the program is started. However, there is no reason why such episodes cannot be added later. This is not a particularly interesting form of learning since it is not carried out automatically. Hand-coded episodes are all loaded during program initialization in the current version of DAYDREAMER. However, we might eventually wish to compare the behavior of DAYDREAMER given different initial sets of hand-coded episodes, or given new hand-coded episodes after the program has been running for a

while. RECOVERY1 (see page 6) is an example of a daydream generated through the application of a hand-coded episode.

- Input of random physical objects: This input is used to stir up a stagnant closed system by producing remindings (see Chapter 5). Examples are provided by the daydreams COMPUTER-SERENDIPITY and

LAMPSHADE-SERENDIPITY

(see page 14).

However, since the majority of the learning carried out by DAYDREAMER does not require external input, we must examine in detail what is meant by closed-system learning. Closed-system daydreaming modifies the state of the system in three ways which contribute to learning:

- Episode storage: Daydreams are stored in episodic memory for later recall. A recalled daydream is then employed through the process of analogical planning in generating new daydreams or external behavior.

- Rule creation: Inference and planning rules are constructed and saved for possible future use. In particular, new preservation goal inference and planning rules are derived by the REVERSAL daydreaming goal.

- Partial concern completion: A concern is activated, planned up to a point, and then halted until an appropriate time in the future. Such a concern is primed to employ fortuitous subgoal successes or serendipities which may occur later on.

In this section, we concentrate on the problem of what is meant by learning in the case of stored episodes, leaving a discussion of rule creation and partial concern completion to later sections in this chapter. 3DAYDREAMER also includes a simple mechanism for inducing new planning rules from a sequence of two input states assumed to form a causal chain; this is accomplished through variabilization (converting constants into variables). Although a potentially general mechanism, it is currently employed only in the specific situation of RECOVERY3 (see page 13).

<!-- page: 118 -->

Episodes for Improvement

THROUGH DAYDREAMING

of Future Search

Many daydreams stored as episodes are simply collections of existing generic rules. What new information, then, does such an episode contribute? What has actually been learned through storage of the episode? In generating a plan to achieve a given goal, the goal is broken down into subgoals, which in turn are further broken into subgvals, and so on. Multiple ways of breaking down each subgoal lead to alternative plans, which are evaluated according to their positive and negative consequences (i.e., personal goal successes and failures). If there are several ways of breaking down each subgoal, the search for a good solution can grow large—a combinatorial explosion results. After spending much time searching to solve a problem once, a traditional planner will start from scratch in solving a similar or identical problem. This is wasted effort; a planner should be able to generate a solution more quickly the second time. A person solving a problem similar to a previous one would probably recall the previous solution which would then enable the person to solve the new problem more easily. In DAYDREAMER, the analogical planning mechanism enables recall of a previous real or imagined planning episode which is then adapted to a new goal. Parts of the adapted plan may need to be repaired for the new goal, which is not identical to the previous one. Even so, much of the adapted plan may stand untouched. Thus, episodes enable learning by reducing future search in planning. Episodes are a source of knowledge for choosing among alternative plans for achieving a subgoal—in solving a problem, for each subgoal, the plan that worked best in a similar previously considered situation is chosen. That. is, daydreaming evaluates the consequences of alternative future plans of action, storing the best plans for possible future use. If the program can perform search in advance through daydreaming, search in the future will be reduced—provided that the program is good at anticipating future problems and in detecting when a problem is similar to a previously examined one. In the terminology of Bundy, Silver, and Plummer (1985), episodes repair control faults. Thus learning in a closed daydreaming system may be defined as follows: If a program is able to solve a given problem more quickly after daydreaming, the program may be sazd to have learned from that daydreaming. Alternatively, we may say that if, after daydreaming, a program is able to generate a better solution to a problem given a limited amount of time, the program has learned from that daydreaming. This form of learning is important because time constraints are imposed in most real-world situations: For example, quick responses are expected in human conversation. Whereas in daydreaming mode the program generates many possible plans, both fanciful and realistic, in performance. mode the program ummediately retrieves and employs the best previously daydreamed plan for the current situation. Thus learning enables the program to generate better solutions when there is no time for daydreaming in performance mode.

<!-- page: 119 -->

Given its initial collection of rules and episodes, DAYDREAMER may not always be able to achieve its personal goals in performance mode; for example, it fails in LOVERS, its first attempt to achieve a LOVERS goal. If the program learns, it may then be able to achieve a personal goal which it was previously unable to achieve. Learning also enables the program to improve at achieving its personal goals. Furthermore, learning enables the program to retain the ability to achieve its personal goals in the face of a changing environment. Thus, learning 1s demonstrated when, given one performance mode experience in which the program produces a certain behavior and fails, the program is later given a similar experience in response to which the program produces a different behavior and succeeds. Closed-system learning, then, enables the program to survive—achieve its personal goals in the simulated real world. Evolution may thus be considered a form of closed-system learning at the species level. However, unlike the process of evolution, learning in DAYDREAMER is not accomplished through random mutation and natural selection; rather, DAYDREAMER already has heuristics which direct it toward a fruitful evolution. (See, however, Lenat’s, 1983, hypothesis that evolution of higher animals and plants is not accomplished through random mutation and natural selection, but through plausible mutation and testing—that is, that mutation is directed by heuristic rules contained within DNA which, for example, coordinate simultaneous genetic mutations in

an advantageous manner.) The value of spinning a daydream lies partially in the fact that its value is not always readily apparent. Only by considering many possibly irrelevant, absurd, or unrealistic possibilities—which may take much time—are the benefits of daydreaming achieved. In a real-world situation, it may never occur to a person to try to find an overlooked possibility because it might not be apparent that such a possibility exists, and the person may never know for lack of time to go into a daydream to find out. However, in a particularly difficult situation in which all apparent alternatives have negative consequences, it may in fact pay to step back for a while and daydream. The following additional benefit is provided by episodes: Episodes enable reduction of search through the filling in of details (such as persons, animals, locations, and so on) of a daydream. For example, in generating an imaginary sequence of events taking place in a familiar grocery store, details such as the location of the aisles, vegetable section, and check-out lanes may be filled in based on a memory of that grocery store. In particular, planning rules leave certain entities (such as physical objects or locations) unspecified—this situation occurs when a rule consequent contains variables not present in the antecedent. Normally, general planning is required to find appropriate instances of these entities. However, if this planning has been performed once, it may not have to be performed a second time: Entities may be instantiated based on entities contained in a recalled episode. An example of this occurs in RATIONALIZATION1 (although in this case the episode was hand-coded rather than generated

<!-- page: 120 -->

through prior daydreaming): The recalled episode of Harrison Ford having a job contains the location of his job (Egypt) which is then employed in the current daydream. Another example is RECOVERY1. The application of previous episodes in daydreaming is also useful to further learning, since past experiences are often a predictor of future experiences. Specifically, (a) episodes provide initial daydream scenarios (for example, “what if there is an earthquake?”) and (b) episodes provide continuations of existing daydream scenarios (for example, “what if there is an earthquake while I am on

a date?”).

### 4.2.2 Episodes for Changing Knowledge Accessibility

If the possibilities inherent in a closed system are sufficiently numerous, that system can effectively be considered as a nonclosed system. In fact, if one draws the boundaries big enough, the distinction between closed and non-closed systems breaks down: Everything is a closed system. The important problem then becomes one of control: How and when are knowledge elements accessed, out of all those contained in the program? Although a program may zn principle contain the knowledge necessary to generate a given plan in a given situation, the control algorithm may be such that access to that knowledge is unlikely or even impossible in that situation. For example, as mentioned above, in performance mode only the plan with the highest evaluation at a given point is selected; Other plans are simply not explored. Closed-system learning therefore involves restructuring of knowledge so that access to it is possible in new situations.

DAYDREAMER restructures knowledge through (a) the storage of episodes, (b) creation of new rules, and (c) partial processing of a concern. A particular kind of knowledge restructuring which can occur through episode storage is discussed in this section. Rules in DAYDREAMER are divided into two classes—generic and episodic. Episodic rules are similar to generic rules, except that they are inaccessible to regular planning. A given episodic rule may only be accessed through analogical application of an episode which contains that rule. That is, an episodic rule may only be applied if an episode containing it is recalled. Episodic planning rules are initially contained within, and can only be applied through retrieval of, hand-coded episodes. However, if a daydream is generated which employs episodic rules borrowed from a hand-coded episode, the storage of this daydream may enable those rules to become accessible in new situations. In short, generation and storage of a daydream may make a previously inaccessible episodic rule accessible. In other words, in a certain situation, although the hand-coded episode may not be recalled, the new daydream 27s recalled, and an episodic rule previously inaccessible in this situation is now

accessible through this daydream.*

4If an episodic rule is employed often enough, does it become a generic rule?

Although

<!-- page: 121 -->

COMPUTER, CASH,

INDEXED ee

Acquainted-Plan

REHEARSAL-LOVERS

ACQUAINTED

iis

ape

wraans-acdtoras.e inace

PEN

MTRANS

ACOUAWTED

MTRANS

DAT-SERV-MBR

a

uhNohane ROMANTIC-INT

M-AGREE

GOAL

M-CONVERSATION

DAT-SERV-MBR

Ke

*

“ies

MTRANS-ACCEPTABLE

ATRANS

i4 DAT-SERV-MBR

Dee

MTRANS

ANS

—DAT-SRV-MBR

ATRANS AT

AT

‘a PTRANS

‘’

KNOW

Figure 4.8: Making Inaccessible Rules Accessible

An example of this is provided by COMPUTER-SERENDIPITY (see page 14). As shown in Figure 4.8, a hand-coded episode containing two other-

wise inaccessible episodic rules is retrieved through the conjunction of (a) the index of computer provided as environmental object input, and (b) serendipity with a active REHEARSAL concern for a LOVERS goal. A new daydream involving achievement of the LOVERS goal is then generated by analogy to this episode. The hand-coded episode is normally not retrievable, since it requires two or more of its indices to be active; thus the episodic rules it contains would normally not be accessed. However, these rules are now contained in the new daydream, which may be retrieved any time a LOVERS goal is pursued in the future.

### 4.2.3 Survival and Growth

Goals in humans are ultimately directed toward survival of the person, growth

(Maslow, 1954) of the person (e.g., toward self-actualization), and survival of the species.

Goals are thus tuned so that if they are continually satisfied the

person tends to survive, grow, and reproduce. Thus personal goals—the cognitive representation of needs—should correspond to the true physiological and such a change of status is not performed explicitly in the current version of DAYDREAMER, an episodic rule may be contained in so many diverse episodes that it is effectively accessible in all situations.

<!-- page: 122 -->

psychological needs of the person. In humans, it is clear that oxygen, water, and food are necessary for survival. However, the definition of survival in the case of a computer program such as DAYDREAMER is not as clear. In computer science terms, survival must refer to the ability of a system to perform its designated tasks. The designated tasks in DAYDREAMER are specified by its personal goals: forming and maintaining interpersonal relationships, keeping a job, and so on. Therefore, in DAYDREAMER, survival is defined as the ability to achieve, and continue to achieve, personal goals, while growth refers to the ability to improve at achieving personal goals over time, that is, to learn. The performance mode of DAYDREAMER enables us to evaluate the program’s success in achieving its personal goals given various external situations as input. Specifically, the success or failure of a personal goal is assessed according to whether, in performance mode, the system carries out certain actions, or receives certain actions or states as input, which are designated to achieve that goal. For example, ENTERTAINMENT is satisfied only if the system performs certain actions designated as having entertainment value (such as watching a movie). The MONEY goal is satisfied only if money is “given” to the system (via external input). The success or failure of a LOVERS, FRIENDS, or EMPLOYMENT goal is also dependent upon external input, for example, he dumps me, she offers me the job, and so on. The success of the SELF-ESTEEM and SOCIAL ESTEEM goals is generally assessed as a function of the success of each of the other goals. Thus there are objective criteria for the success and failure of personal goals. However, since these criteria refer in part to external input, there is a potential problem: A human could consistently defeat the computer by typing in negative responses. We therefore assume that the responses typed in are those of a “reasonable” person, that the responses present to the system a plausible external world, one which the system might encounter were it a human and not a computer system (since, after all, humans do not usually form true friendships with computers!). The “reasonableness” of the input responses can be varied in order to evaluate how the system is able to cope with different environments. The criteria for the success and failure of personal goals are contained within DAYDREAMER in the form of inferences and planning rules, some of which were discussed above (and others of which will be presented in the remainder of this chapter). Thus the success or failure of a given goal may be assessed simply by examining the appropriate data structure within DAYDREAMER. This, of course, assumes that the system does not cheat (for example, by altering its

MONEY

data structure to indicate a million dollars).

The DAYDREAMER

program is constructed in such a way that it does not generally cheat in this manner. However, in certain cases DAYDREAMER does deceive itself: For example, through the RATIONALIZATION daydreaming goal, the degree to which a goal is considered to have failed is reduced in order to reduce the negative impact on processing caused by the negative emotional state resulting from that failed goal. Thus RATIONALIZATION temporarily tricks the system

<!-- page: 123 -->

into thinking its situation is not as bad as it actually is, so that it can get on with the business of actually improving its situation: RATIONALIZATION brings optimism to the system. If employed to an excessive degree, such strategies can have disastrous effects: By daydreaming away all of its unsatisfied goals, the system would never achieve them in reality. A system which fools itself in this way cannot continue to function for long—although it may lead a blissful existence during its last few moments.

## 4.3 Daydreaming Goals for Learning

General, abstract planning for future tasks is impossible because of the combinatorial explosion of possible future tasks and world situations. Therefore, daydreaming goals are used to guide the exploration of concrete situations of potential use in the future. In this section, we discuss REVERSAL, RECOVERY,

REHEARSAL,

### 4.3.1 Reversal

and REPERCUSSIONS.

The REVERSAL daydreaming goal generates scenarios in which a past real failure is avoided or a future imagined failure is prevented.° This goal is activated in response to a negative emotion of sufficient strength resulting from a real or imagined personal goal failure: IF

THEN

NEG-EMOTION resulting from a FAILED-GOAL ACTIVE-GOAL for REVERSAL of failure

The REVERSAL daydreaming goal modifies memory in two ways (depending on which of several strategies for REVERSAL is employed): episodes in which the personal goal failure is avoided are stored in episodic memory, and new inference and planning rules are created and stored in semantic memory. Both memory modifications modify future behavior of the program: Episodes are recalled and applied in order to achieve similar future goals, and rules are applied directly.

The experience LOVERSI (see page 3), in which DAYDREAMER is turned down by Harrison Ford, contains two failures: the failure of a SOCIAL ESTEEM preservation goal and the failure of a LOVERS goal.® In the daydream 5It is also possible to daydream about alternative actions which might have prevented a goal success and resulted in a failure. We ignore these daydreams in the present DAYDREAMER for simplicity. Nonetheless, assessing the reasons for success is just as important as assessing

the reasons for failure—both enable learning (Heider, 1958, pp. 88-89). Daydreaming of ways success might have been prevented is another way of generating imagined failures for further

learning. That is, one may assess how one might have failed in a past experience in order to plan to avoid such possible mistakes in the future. 6]t also happens to be the case that the SOCIAL ESTEEM goal is a subgoal of the LOVERS goal.

<!-- page: 124 -->

REVERSAL] (see page 9), a REVERSAL of the SOCIAL ESTEEM goal leads to success of the LOVERS goal: DAYDREAMER imagines having worn nicer clothes. When DAYDREAMER is later confronted with a similar situation in LOVERS2 (see page 9), she puts on nicer clothes. Here are some other daydreams involving REVERSAL of the LOVERS goal:

REVERSAL2 | feel upset. What if | had told him | found out his girlfriend was cheating on him? He would have broken up with his girlfriend. | would have asked him out. He would have accepted. | feel regretful.

REVERSAL3 What if| had told him | liked his movies? He would have had a positive attitude toward me. | would have asked him out. He would have accepted. | feel regretful. The value of the REVERSAL daydreaming goal arises from the rule of thumb that if something has happened once, it may happen again (or if something is imagined, it is possible that it may happen in reality). This is especially true in a program such as DAYDREAMER which is perpetually concerned with a fixed

set of personal goals. REVERSAL is accomplished in different ways depending upon what caused the personal goal failure: If the personal goal failure was generated through an inference, or chain or inferences, as in REVERSAL1, the UNDO-CAUSES strategy is employed. Ifthe personal goal failure resulted from failure of other persons

to behave in the manner necessary for achievement of that goal, the strategies

of EXPAND-LEAVES, used in REVERSAL2, and EXPAND-ALTERNATIVES, used in REVERSAL3, are employed. Other possibilities are not handled by REVERSAL. We now discuss each strategy in turn. The UNDO-CAUSES strategy applies to the failure of a personal goal which was generated by an inference rule. For example, failure of SOCIAL ESTEEM results from a chain of inferences while planning for LOVERS in LOVERSI1, as shown in Figure 4.9. This strategy may only be applied if one of the leaves (ultimate sources), call it N, of the inference chain leading to the goal failure is the negation of some state. (In the two-valued logic of DAYDREAMER contexts—see Chapter 7 for details—the absence of the assertion of a given fact is equivalent to the assertion of the negation of that fact—the closed-world assumption.) In this case, there exists such an N:

(NOT (WELL-DRESSED Me)) If the negation of N is deemed

RPROX

a long-term state, examples of which are

(city of residence) and INSURED

(having insurance), a new top-level

<!-- page: 125 -->

PTRANS(Me,Home, Nuart) At-Plan AT(Harrison,Nuart)

MTRANS(Me,Harrison)

AT(Me,Nuart)

NOT(WELL-DRESSED(Me)) Neg-Attitude-Inf

RICH(Harrison) NEG-ATTITUDE(Harison,Me)

ROMANTIC-INTEREST(Me,Harrison) Social-Esteem-Failure

FAILED-GOAL(Me,POS-ATTITUDE(Harrison,Me))

Figure 4.9: Inference Chain for a Social Regard Failure

personal goal whose objective is the negation of N is created and activated. This enables DAYDREAMER to prevent future similar failures once and for all. Otherwise, if the negation of N is not a long-term state (as is the case for

WELL-DRESSED),

the UNDO-CAUSES

strategy creates (a) a new inference

rule which activates a preservation goal in appropriate circumstances, and (b) a new planning rule for achieving that preservation goal. The new preservation goal should be activated whenever it is possible that the personal goal may fail in the near future (for example, when DAYDREAMER has an active goal to

MTRANS

to a RICH person toward whom she has ROMANTIC-INTEREST).

Planning for this preservation goal should then attempt to prevent N (in our

example, not being WELL-DRESSED) WELL-DRESSED).

or to achieve the negation of N (being

Specifically, the antecedent of the new inference rule is given by the following: for each variabilized’ version VL of all the leaves besides N, either VL is true or a goal whose objective is VL is active. The consequent of the new inference rule is to create a new preservation goal with some unique identifier. The antecedent of the new planning rule is a preservation goal with the same unique identifier as above. The consequent is a variabilized version of the negation of N. In REVERSALI, new rules:

the UNDO-CAUSES

strategy creates the following two

7The operation of variabilization converts constants, such as persons and physical objects, into variables which can unify with (match) any instance of the major type from which they were derived.

<!-- page: 126 -->

person is RICH or ACTIVE-GOAL for person to be RICH and person is AT location or ACTIVE-GOAL for person to be AT location and self PTRANS to location or ACTIVE-GOAL for self to PTRANS to location and self MTRANS to person or ACTIVE-GOAL for self to MTRANS to person and ROMANTIC-INTEREST toward person or ACTIVE-GOAL for ROMANTIC-INTEREST toward person ACTIVE-GOAL PRESERVATION UID.1 IF THEN

ACTIVE-GOAL ACTIVE-GOAL

PRESERVATION UID.1 for self to beWELL-DRESSED

After these rules are added, the past failure episode is then replanned on behalf of the REVERSAL daydreaming goal starting from an appropriate context. This results in a REVERSAL scenario—REVERSALI in this case. These rules are then later employed in the performance mode experience LOVERS2. Within a concern, planning for a preservation goal has priority over planning for nonpreservation goals. Thus any actions necessary to achieve preservation goals will be taken before further planning for regular goals is performed. The remaining strategies for REVERSAL operate if the personal goal failure was caused by the failure of another person to perform actions necessary to achieve that goal; thus these strategies may be employed only on goal failures which result in performance mode. Performance mode experiences provide important feedback information which would otherwise be unavailable: whether a given plan worked or did not work when applied in the external world. Although one may not know exactly why a given plan failed, one may follow the strategy of continuing to use a plan which seemed to work or, if the plan did not seem to work, attempting to use a different plan. Both EXPAND-LEAVES and EXPAND-ALTERNATIVES are instances of such a strategy. The EXPAND-LEAVES strategy for REVERSAL finds succeeded leaf subgoals whose realism is below a certain threshold and reinitiates planning for

those subgoals (on behalf of the current REVERSAL daydreaming goal). A leaf subgoal is one for which no further planning was performed because it was considered satisfied at the time of planning. In REVERSAL2, the subgoal that the movie star is NOT in a LOVERS relationship with another woman is replanned. The EXPAND-ALTERNATIVES strategy for REVERSAL finds unexplored branch contexts (if any) generated in planning for the personal goal and reinitiating planning in those contexts (on behalf of the current REVERSAL daydreaming goal). REVERSAL3 demonstrates the use of an alternative plan for getting the movie star to have a POS-ATTITUDE toward the daydreamer. Both of these strategies may make use of information provided as input which justifies the action of the person which resulted in the personal goal failure. For example, Harrison Ford may explain that he does cannot go out with DAYDREAMER. because he has a girlfriend. Such input may be used

<!-- page: 127 -->

to reduce the search: In expanding leaves, only those leaf subgoals stated as reasons for the external action need be considered. In expanding alternatives, only alternative plans for subgoals stated as reasons for the external action need be considered. What is the emotional impact of the REVERSAL daydreaming goal? First we consider daydreams resulting from a real goal failure: An imagined past goal success resulting from an alternative action leads to a negative, rather than positive, emotion: regret. This occurs in each of the example daydreams above. Thus successful REVERSAL actually has a negative emotional impact— emotional state is sacrificed for the benefit of learning. If, instead, an imagined goal failure results from alternative actions, positive emotions of relief result. Thus in a situation in which there simply was no way of avoiding the past failure, REVERSAL functions as a form of rationalization—the negative emotional state associated with a past failure is reduced. In the case of imagined goal failures which result in emotions of worry, successful REVERSAL (generating means by which the imagined failure may be avoided and a success achieved) results in positive emotions, which reduce the original level of worry. However, unsuccessful attempts resulting in further imagined failures produce further worry emotions.

### 4.3.2 Rehearsal and Recovery

The purpose of the REHEARSAL and RECOVERY daydreaming goals is twofold: to discover problems in a future plan before it is actually carried out in performance mode, and to construct a plan in advance in order to improve efficiency. REHEARSAL is activated in response to a halted personal goal concern: subgoal to WAIT and corresponding top-level goal contains no variables and POS-EMOTION of sufficient strength connected to top-level goal

ACTIVE-GOAL

for REHEARSAL

of top-level goal

All personal goal concerns are halted just before they are about to perform an action and the program is in daydreaming mode. This insures that the program will get a chance to daydream about future tasks before performing them. REHEARSAL involves considering possible future completions of a concern and incorporating these plans into episodic memory for future use by that concern. For example, before the date in LOVERS3, DAYDREAMER daydreams about the date in REHEARSALI. REHEARSAL may lead to an imagined personal goal failure, which then activates a REVERSAL daydreaming goal, which in turn determines how to

avoid that failure. The daydream NEWSPAPER

(see page 7) is an example.

<!-- page: 128 -->

RECOVERY is activated in response to a negative emotion strength resulting from a personal goal failure: IF THEN

of sufficient

NEG-EMOTION resulting from a FAILED-GOAL ACTIVE-GOAL for RECOVERY of failure

RECOVERY involves the generation of future scenarios for achieving the same goal which failed. What is the emotional impact of REHEARSAL and RECOVERY? An imagined goal success in a future daydream results in the positive emotion of hope. Thus REHEARSAL and RECOVERY can serve to counteract negative emotions resulting from previous imagined failures. By telling oneself that one promises to achieve a goal, or that a goal will eventually be achieved, one tends to feel better (even if that goal is never in fact achieved or planned for—though this may lead to later problems). This is expressed in the proverb “pain is forgotten where gain follows.” Rapaport (1951) also noticed the emotional value of future daydreaming: “Daydreams often succeed in allaying, at least temporarily, the fears and urgings with which they deal. Their work of planning—by experimenting with possible solutions—seems to succeed at times in ‘binding’ mobile cathexes” (p. 463). Of course, an imagined goal failure in a future daydream results in the negative emotion of worry, which in turn activates a REVERSAL daydreaming goal to cope with that worry and prevent the imagined goal failure.

### 4.3.3 Repercussions The REPERCUSSIONS daydreaming goal involves exploring the consequences

of a hypothetical future situation. In particular, if an important goal failure or

success occurs for another person (or animal), a REPERCUSSIONS daydreaming goal is activated to consider what it would be like for that failure or success to occur to the daydreamer. If a hypothetical future situation leads to a personal goal failure, the REVERSAL daydreaming goal will be activated to find ways to avoid such a future failure.

In the daydream REPERCUSSIONS1

(see page 8), a REPERCUSSIONS

daydreaming goal is first activated which causes an earthquake in Los Angeles to be hypothesized. Rules fire which infer the collapse of the apartment and thus the failure of goals to stay alive and preserve possessions. These failures then result in the activation of two REVERSAL daydreaming goals. The first goal applies the UNDO-CAUSES strategy which creates rules causing DAYDREAMER to go underneath a doorway whenever an earthquake begins in the future (in the same city in which she lives). The second REVERSAL results in the activation of a new top-level personal goal: to get insurance in order to preserve her possessions. DAYDREAMER then tests the rules created in the first REVERSAL by generating another alternative scenario. This time, rules fire which infer a falling plant (hung above the doorway) and the failure of a goal not to be hurt. This in turn activates another REVERSAL goal which

<!-- page: 129 -->

initiates a new top-level goal to move the plant. The two top-level goals are later pursued in performance mode: DAYDREAMER moves the plant, and goes to the insurance company and purchases the insurance. Although it may be obvious that one can get hurt in an earthquake, it may not be obvious to watch out for a falling plant. Thus only through such a daydream is one able to learn to watch out for a falling plant in case of an earthquake.

## 4.4 Evaluation and Decision Making

In previous sections we have described how daydreams are produced, stored, and later recalled and applied through analogical planning in a new situation. This section addresses the issues of daydream evaluation and selection: After generating a multitude of daydream scenarios, how does DAYDREAMER decide which scenario to pursue in the future? How is such a decision fixed in memory? Might the program consider several alternative scenarios and not make a final decision among them? How are choices made among several previously daydreamed courses of action applicable in the current situation? When does DAYDREAMER abandon a daydream or back up and modify it because it is not turning out as desired? What different ways may daydreams be evaluated?

### 4.4.1 Daydream Evaluation Metrics

Humans often decide to perform a given daydreamed plan based on which “feels best” or which the person is most “comfortable” with. In DAYDREAMER, however, these sensibilities must be made concrete. We therefore present three daydream evaluation metrics: desirability, realism, and similarity. Suppose DAYDREAMER generates two alternative future daydreams in which she preserves her possessions in case of an earthquake in Los Angeles:

(a) move to New York, or (b) stay in Los Angeles and purchase insurance. The first daydream has the following negative consequence: She will lose her job in Los Angeles. Thus the first daydream has a lower desirability than the second. As a result, DAYDREAMER will select the second daydream rather than the first for execution in performance mode. While planning for one personal goal, side effects or consequences for other personal goals are detected as follows: Either a personal goal activation, success, or failure happens to be inferred (through application of an inference rule) or such a personal goal happens to result as a subgoal of the personal goal originally being planned. Therefore, the desirability of a final daydream scenario is calculated according to the sum of the importances of each of the personal goals activated or resolved some time after the activation of the top-level goal of the concern. Goal successes have positive importance while goal failures and active goals have negative importance. A negative desirability thus indicates an undesirable scenario while a positive value indicates a desirable one. The im-

<!-- page: 130 -->

portance of a goal is initially set according to an intrinsic importance assigned by the inference rule which activated that goal (which in turn is determined by the programmer). However, this initial importance may be modified through

processes such as rationalization (discussed in Chapter 3.) Suppose DAYDREAMER instead daydreams that she moves to New York and becomes a famous artist. This might have a higher desirability than the daydream of purchasing insurance in Los Angeles. However, it has a much lower realism. Although daydreams of low realism may be useful for further daydreaming (such as in generating rationalizations or in the generation of more realistic solutions through incremental modification), DAYDREAMER should not generate external behavior based on an unrealistic daydream—the sequence of actions taken by the program, when performed in the external world, would be unlikely to result in the daydreamed outcome. Therefore, DAYDREAMER decides whether to act on a given daydream based on both the desirability and realism of that daydream. In this case, staying in Los Angeles receives a higher overall evaluation based on both desirability and realism, and the program decides to stay in Los Angeles. On a scale from 0 to 1, the realism of a scenario is evaluated based on the plausibilities of the rules employed in generating that scenario. 1 indicates a completely realistic scenario while 0 indicates a completely unrealistic one. Methods for calculating scenario realisms are discussed by Shortliffe and

Buchanan (1975), Duda, P. E. Hart, and Nilsson (1976), Charniak (1983b), and Pearl (1982). Evaluations of desirability and realism are stored along with daydreams in episodic memory. Realisms, but not desirabilities, are also associated with subepisodes (episodes associated with subgoals). One might object to the absence of such metrics as cost and risk. However, such metrics are subsumed—albeit in a rudimentary manner—by the above metrics: The cost of executing a given plan is equivalent to the (negative) impact of that plan on personal goals: For example, if running to the store before it closes causes one to become fatigued, this cost is accounted for in a personal goal not to become fatigued. The risk of a given plan is also determined by negative impact on personal goals—provided that the scenario has a high realism. That is, if a plan is risky, it is likely that it will result in negative consequences: For example, since robbing a bank is risky, a realistic daydream about robbing a bank would include getting caught. If getting caught were overlooked by the daydream, it would then have a low realism. Thus cost and risk are taken into account by the desirability and realism metrics. Another metric, that of s¢milarity, is not applied when storing an episode, but rather when an episode is retrieved. While desirability and realism evaluate daydreams in isolation from any particular task, the similarity metric is an evaluation of a daydream in terms of a particular target situation—the similarity of the source and target goals is evaluated. The similarity of two goal objectives is determined through a structural comparison of those two objectives. Similarity may be determined according to the distance of the types of two objectives in

<!-- page: 131 -->

## 4.4 EVALUATION AND DECISION MAKING itt

the type hierarchy and recursively according to the similarity of the components of the two objectives; differences occurring deeper in the structure have a smaller weighting. For example, in the current program, the similarity of the REVENGE goal against Harrison Ford and the REVENGE goal against Robert Redford is 2.4. The similarity of the REVENGE goal against Harrison Ford and the REVENGE goal against the daydreamer’s boss is 2.1. These two goals are less similar than the previous two because one of the latter goals involves REVENGE for a failed LOVERS goal while the other of the latter goals involves REVENGE for a failed EMPLOYMENT goal. In the former case, both goals involved LOVERS goals. The similarity of the REVENGE goal against Harrison Ford and a FOOD goal is a large negative number. In the current version of DAYDREAMER, this similarity evaluation is sufficient to select, out of several alternative episodes, the episode most appropriate to the target goal. This metric, for example, will enable the program to select

REVENGE3

instead of REVENGE1

the next time an EMPLOYMENT

RE-

VENGE daydream is generated. However, as the number of indexed daydreams and episodes becomes large, similarity will have to be measured in terms of relevant features of the goal objective and of the enclosing situation. The program will have to determine such features automatically in order for it to learn effectively. For example, DAYDREAMER might build up a large collection of alternative plans for forming LOVERS relationships with people depending on the particular personality traits of the person. Given a new person, the best, most similar, plan would have to be selected using those traits as indices. Daydream evaluation is employed at several points in processing:

- During production: As a daydream or external plan is generated, an ongoing assessment of its realism is maintained. A planning branch is abandoned if (a) the current concern is a personal goal or learning daydreaming

goal and (b) the scenario realism falls below a certain threshold (higher for personal goals than for learning daydreaming goals). For example, in planning for a future date via REHEARSAL, if an earthquake is imagined to occur in the middle of the date, this scenario will be deemed unrealistic and abandoned.

- After production: After generation of a daydream or external plan, its desirability and realism are evaluated for later use in deciding whether or not to employ that episode.

- During application: When several previous daydreamed or real planning episodes are retrieved as potentially applicable to a given situation, those episodes are evaluated with respect to the current situation. That is, the similarity of the source and target goals is calculated.

<!-- page: 132 -->

|ordering? |ondering3 | (episode [|simi.|realism |desir.| ordering gg el ee PREVENGEL Nog3d [cut 16) b nu 3a [= 2 | 18[2eee PREVENG||ES Table 4.1: Ordering of Retrieved Episodes

### 4.4.2 Daydream Selection

Several previously daydreamed or real plans for a goal may be retrieved when planning to achieve that goal in a new daydream or performance mode experience. How does DAYDREAMER choose which of these episodes to employ? In DAYDREAMER, the ordering of episodes as candidates for potential use is calculated according to the stored desirabilities and realisms of those episodes, and the assessed similarity of those episodes to the current situation. This calculation is different depending on the type of concern. If the concern is a personal goal or a learning daydreaming goal, then the ordering is based on the product of desirability, realism, and similarity, provided that each is above a certain threshold (higher for personal goals than for learning daydreaming goals). Otherwise, the ordering is based on the similarity, provided it is above a certain threshold. For example, suppose DAYDREAMER is about to generate another REVENGE daydream after being fired from her job. Previous REVENGE daydreams, REVENGE]

and REVENGBS,

are recalled, and an ordering for these

episodes is determined as shown in Table 4.1. Since the current concern is a daydreaming goal, the ordering of these daydreams is based only upon the similarity, shown as ordering1. Therefore, REVENGES is attempted before REVENGE1. (REVENGE3 will succeed and so REVENGE1 will never be attempted.) If the current concern had instead been a learning daydreaming goal, the orderings would have been based upon the product of the three evaluations, shown as ordering2. Again, in this case, REVENGE3 would have been attempted before REVENGE1. However, if the current concern had been a personal goal (and thus a performance mode experience), the realism of both of these episodes would have been below the acceptable minimum level of realism for performance mode planning. Thus the ordering would have been 0 for both episodes and neither would have been employed in the generation of external behavior. If all the retrieved alternative daydreams for achieving a given goal have a low or negative desirability, this is an indication that the goal itself may not be a good idea. Therefore, whenever all daydreamed attempts to achieve an active goal have negative desirability, that active goal is deactivated. This enables the program to learn to avoid pursuing certain courses of action. For example, in the case of the movie star, it might be determined that dating movie stars always

<!-- page: 133 -->

turns out bad.

## 4.5 Intention Formation and Application

In daydreaming, one often makes mental notes, commonly called intentions, about what one will do in the future: One thinks something along the lines of “next time X happens, I will do Y” or “tomorrow I will do Z.” Intentions, once formed, do not remain in consciousness. Rather, an intention is reactivated only at some appropriate time in the future. We distinguish between persistent and one-shot intentions. A persistent intention is a generalized plan to be applied whenever a certain situation arises in the future: for example, to run for the nearest doorway in case of an earthquake, to check that the car lights are turned off before leaving the car, to call a store to make sure it is open before driving there, to dress up before going out alone, when meeting a new person to find out the telephone number of that person in order to be able to contact that person in the future, and so on. A one-shot intention, on the other hand, is a more or less specific plan which applies once and is then deactivated: for example, to go grocery shopping on the way home from work, to pick up a copy of a newspaper while shopping, to mail a letter, and so on. Our distinction between the recall of persistent and one-shot intentions is

similar, but not identical to, the distinction of Meacham and Leiman (1982) between habitual prospective remembering and episodic prospective remembering: Habitual remembering is more specific than recall of persistent intentions—it involves the recall of intentions as part of a routine, such as taking vitamins each morning at breakfast. Episodic remembering, however, is basically the same as the recall of a one-shot intention. It is sometimes difficult to distinguish the two classes: Planning a particular sentence for use in a particular job interview may be viewed as a one-shot intention since it is performed only once; and yet, if the person does not get

- the job and has to go to more interviews, this same sentence could be employed again and again. For the present purposes, any detailed future plan which has the potential of being applied more than once in the future is considered a persistent intention. In modeling intentions, we must consider both how intentions are formed and how they are later recalled and acted upon. That is, how is a particular plan generated and then how is a later situation recognized as appropriate for application of this plan? First we consider persistent intentions. We hypothesize that persistent intentions correspond to daydreamed episodes having high evaluations. That is, persistent intentions are formed though the generation, evaluation, and storage of daydreamed scenarios in episodic memory; such episodes are indexed under the goal of the scenario and other indices, such

<!-- page: 134 -->

as emotions, persons, and physical objects.8 Whenever a similar goal is activated

in the future (and other indices are active), this episode will be recalled and potentially applied. (If several episodes are recalled it will then be necessary to select from among these episodes.) Proper recall of a persistent intention then depends on proper indexing of the episode. We further hypothesize that one-shot intentions correspond to concerns in DAYDREAMER. In effect, each currently active concern constitutes an intention

to complete that concern.

A concern (and therefore a one-shot intention) is

initiated upon activation of a personal or daydreaming goal. Processing activity on behalf of a given concern is performed when that concern has the highest level of motivation, as provided by emotions. Once initiated, a concern persists until completion. This tendency of humans to complete a task once begun—even if interrupted—has been demonstrated in psychological experiments (Lewin, 1926/1951) and observed by other researchers (Mandler, 1975; Miller, Galanter, & Pribram, 1960). However, a concern may be interrupted in DAYDREAMER if a new concern is initiated with a higher level of motivation or if an existing concern acquires a higher level of motivation. An existing concern can acquire a higher level of motivation if, while performing another concern, something comes up which is relevant to that concern. This is detected through two mechanisms:

- Fortuitous subgoal success: While performing a personal goal concern, an active subgoal of another personal goal concern is accidentally achieved. A surprise emotion is generated and becomes an additional motivating emotion associated with the second concern.

- Serendipity: An input state, retrieved episode, or internally generated state accidentally suggests a solution to another concern. A surprise emotion is generated and associated with the second concern. See Chapter 5 for details. Sometimes an interrupted concern is resumed shortly thereafter—for example, when the concern is interrupted by a short concern such as (in humans) swatting a mosquito—while other times a concern is interrupted by a longer concern. In both cases, there is the potential that yet other concerns will be activated or that other concerns will acquire greater motivation, so that the interrupted concern will not resume execution immediately after completion of the interrupting concern. It is possible for the program to follow a series of interruptions from each concern it is working on, returning to interrupted concerns only when concerns complete or when interrupted concerns acquire 8However, humans often decide on a single, best daydream to perform in the future. This one daydream is the intention. In DAYDREAMER, a particular daydream could be designated

as such if it had a evaluation sufficiently higher than each of the other candidates. For the time being, however, all daydreams with successful evaluations are stored, and the daydream with the highest evaluation may be considered as the single intention since it is most likely to be employed in future performance mode planning.

<!-- page: 135 -->

higher motivation. There is no special mechanism for resumption of interrupted concerns, since when an interrupting concern completes, it is removed and the program continues processing with the most highly motivated concern at that time (which is often, but not always, the last interrupted concern). Concerns are also interrupted and placed into a nonrunable state if the program desires to perform an action and is currently in daydreaming mode or the program is in performance mode and that action contains variables. This distinction in our program between runable and nonrunable concerns accounts for results

discussed by Lewin (1926/1951): Resumption of an interrupted activity occurs even without external stimulus related to that activity (as in runable concerns), while unperformed intentions require appropriate external stimulus to activate (as in nonrunable concerns reactivated through fortuitous subgoal success and

serendipity). Humans, of course, may perform more sophisticated planning to interleave several concerns and achieve greater efficiency. For example, if one concern has purchasing a newspaper as a subgoal and another has purchasing groceries as a subgoal, the two subgoals may be accomplished in one, rather than two, trips to the store (which carries newspapers). DAYDREAMER is not generally capable of batching plans in this way, since planning is performed separately for each concern. However, while performing one concern, the program may realize that the current situation is relevant to another concern, thus achieving a similar batching effect in an opportunistic manner: In performing the concern to obtain groceries, the subgoal of being near a newspaper is achieved, thus reactivating the other concern to purchase a newspaper. Once the newspaper is obtained, the concern to obtain groceries may be resumed. Experiments (B. Hayes-Roth & F. Hayes-Roth, 1979) have indicated that human planning is often performed in such a manner, especially when the plans reach a certain level of complexity. In addition, although DAYDREAMER plans for each concern separately, one concern may involve more than one personal goal. In particular, the impact on personal goals other than the top-level goal is taken into account during planning. There are, of course, still difficulties with this approach: The program will

- go through the checkout line twice, failing to realize that it can buy the groceries and the newspaper at the same time. Also, once having purchased the newspaper, it may continue with that concern, which could take it out of the store to another location before having purchased the groceries. Avoiding such problems requires more advanced planning techniques (such as those discussed by Wilensky, 1983 and Sacerdoti, 1977) which are beyond the scope of the current work.

Lewin (1926/1951, pp.

97-98) argues that the use of associations cannot

account for the recall of an intention on an appropriate occasion since, for ex-

ample, dropping a letter into a mailbox when it is seen (S. Freud, 1901/1960, p. 152) should increase the force of association between the mailbox and the dropping of the letter, and yet once this intention is carried out, seeing a sec-

<!-- page: 136 -->

ond mailbox results in no forces directed toward mailing the letter.? Instead, he proposes that intentions result in the formation of a tension state called a “quasi-need” (similar to our emotionally-motivated concerns) to which there corresponds a set of situations and objects (said to have a “valence” for the quasi-need) enabling actions which satisfy the quasi-need. In DAYDREAMER, a “valence” for a concern is determined by whether a situation achieves a subgoal of that concern or suggests a solution to that concern through serendipity (see Chapter 5). Nonetheless, as J. L. Singer (1975) points out, intentions and unfinished business are often recalled in daydreaming via a chain of associations or remindings. In DAYDREAMER, persistent intentions (stored episodes) can be recalled in this fashion (see Chapter 8) and one-shot intentions (concerns) can be recalled or reactivated through an associational process in the space of rule connections. Why do we sometimes forget our intentions? A common phenomenon is walking into the kitchen or somewhere else and then wondering what one is doing there. Perhaps such forgetting is caused by daydreaming about things other than the current plan, which pushes that plan out of limited-capacity working memory (as suggested by J. R. Anderson, 1983, p. 130). S. Freud,

(1901/1960) discusses the forgetting of intentions caused by obscure motives (see also Lewin, 1926/1951). In DAYDREAMER, a persistent intention might be forgotten if an episode is improperly indexed; a one-shot intention might be forgotten if the serendipity mechanism is unable to find a relationship between the current situation and a concern when it should.

## 4.6 Related Work in Machine Learning

There has been much work in the past on learning by creating new inference, planning, or production rules and modifying existing ones. Bundy, Silver, and

Plummer (1985) and Dietterich and Michalski (1983), for example, review this work. Typical basic strategies for rule creation and modification include: (a) dropping conditions, (b) adding conditions, (c) converting constants into variables (variabilization), (d) converting variables into constants (instantiation), and (e) composing two rules. New rules are created in DAYDREAMER through the UNDO-CAUSES strategy for the REVERSAL daydreaming goal. (An additional input induction strategy, not developed extensively in the present work, is

used in DAYDREAMER to create a new planning rule through the variabilization of two consecutive input states in certain specialized situatious.) The UNDOCAUSES strategy is used when a personal goal failure results from the absence of some state in some situation. It creates a new rule which initiates plan° Although this may be true in most cases, informal observations suggest that one may sometimes become confused about whether a given intention has been carried out. Perhaps it is possible that daydreamed actions may be confused with real ones or that confusions among similar intentions may occur.

<!-- page: 137 -->

## 4.6 RELATED WORK IN MACHINE LEARNING

ssNG

ning to achieve that absent state in future similar situations. Sussman’s (1975) HACKER program used a similar strategy for correcting failures or “bugs” in a plan given the reason for the failure. DAYDREAMER primarily learns through the generation, evaluation, storage, retrieval, and application of daydreamed episodes. Although planning and inference rules are neither created nor modified by this process, new episodes improve the program’s ability to select from among alternative rules in any given situation. Rules which were previously inaccessible in a given situation may become accessible. Since daydreamed episodes are applied through a process of analogical planning, the remainder of this section is devoted to a review of previous work in this area. The analogical planning mechanism of DAYDREAMER is similar to Car-

bonell’s (1983) method for learning by analogy. Both methods employ the following steps to solve a new problem: (a) recall the solution to a previous similar problem, and (b) employ that solution to reduce search in generating a solution to the new problem. In particular, Carbonell starts with a “means-ends analysis” (Newell & Simon, 1972) framework for problem solving: The task is to find a sequence of operators which transform an initial state in the problem space into a final (goal) state; a difference function is used to compute the differences between one state and another; operators are indexed by the differences they reduce. In general, problem solving involves a repeated process of selecting operators which reduce the differences between the current state and the goal state (and appropriate recursion for subproblems resulting from operators with unsatisfied

preconditions). Problem solving by analogy is then carried out as follows: The initial and goal states and other constraints of the current problem are compared to those of previously solved problems, using the difference function as a similarity metric. In addition, the percentage of preconditions of those operators employed in the previous problem solution which are already satisfied in the new situation is calculated. The previous problem solution most similar to the new problem according to these metrics is then selected (or a number of previous solutions are selected). The previous solution is then transformed into a solution to the new problem as follows: A process of problem solving is carried out in a higher-level space of problem solutions called “T-space” (for transform space), with the previous solution as the initial state and a solution to the new problem as the goal state. This task is performed through a process of means-ends analysis whose available “T-operators” include: inserting an operator into the solution, deleting an operator,

reordering

operators,

substituting

operator parameters,

and

(a)

invoking means-ends analysis in the (lower-level) problem space in order to achieve an operator precondition satisfied in the previous situation but not in the new one and then (b) splicing the resulting subsequence into the solution. DAYDREAMER employs a subgoaling framework rather than a means-ends

<!-- page: 138 -->

analysis one. A previous plan, recalled primarily on the basis of the similarity of the previous goal to the new one (but according to other indices as well), funetions as a “suggestion” to the process of planning for the new goal. Nonetheless, the operations which DAYDREAMER performs as it generates a new plan are analogous to “T-operators”: (a) a subtree for achieving a subgoal satisfied in the original situation but not in the new one may be generated and spliced in, (b) a subtree for a subgoal satisfied in the new situation but not in the original may be deleted, (c) a subtree for achieving a subgoal may be generated and spliced in when the plan for that subgoal was applicable in the original situation but is not applicable in the new one, and (d) parameters may be substituted. Analogical planning in DAYDREAMER is also related to the “purposedirected analogy” mechanism of Kedar-Cabelli (1985). Given a goal concept (e.g., a hot cup), purpose of that concept (e.g., to enable one to drink hot liquids), a target example (e.g., a styrofoam cup), and domain knowledge, the mechanism (a) retrieves a known instance of the goal concept (e.g., a ceramic mug), (b) generates a proof that this instance satisfies the purpose of the goal concept (e.g., that a ceramic mug enables one to drink hot liquids), and (c) uses this proof to construct a proof that the target example satisfies the purpose of the goal concept (e.g., that a styrofoam cup also enables one to drink hot liquids). This last step is accomplished in a fashion similar both to analogical planning in DAYDREAMER and to Carbonell’s analogical problem solving (i.e., through incremental repair of the proof). A generalization may then be formed from the common features of the two proofs; this generalization provides a definition of the goal concept stated in terms of lower-level structural features (e.g., upward concavity, flat bottom, and so on) instead of higher-level functional ones (e.g., used to drink hot liquids). Kedar-Cabelli assumes that the source and target concept instances are given, and therefore does not address the problem of finding an analogy in the first place. In contrast, DAYDREAMER selects episodes related to the current situation automatically. Unlike the analogical planning mechanism of DAYDREAMER,

neither the scheme of Carbonell nor that of Kedar-Cabelli en-

ables incorrect subgoals in a tentative analogical plan to be themselves repaired through further (recursive) analogical planning. Carbonell, however, has suggested two related ideas: (a) two recalled solutions might be combined into one (p. 144), and (b) future problems in T-space might be solved by analogy to previous T-space problem solutions (p. 149). The mechanisms of Carbonell and Kedar-Cabelli are otherwise quite similar to our analogical planning mechanism. Analogical planning in DAYDREAMER is also similar to the chunking on goal hierarchies of Rosenbloom (1983) and the work on MACROPS in STRIPS

(Fikes, Hart, P. E., & Nilsson, 1972). However, unlike these mechanisms, our mechanism is embedded within a larger framework and set of strategies for learning from imagined past and fu-

ture scenarios:

(a) upon a failure in the external world, alternative past sce-

narios may be generated, evaluated (for example, according to the impact on

<!-- page: 139 -->

various personal goals of the program) and stored for possible future application by analogy, and (b) possible future situations and plans may be hypothesized, evaluated, and stored for possible future application by analogy. Analogical planning is thus used as the basic mechanism for applying previous imagined scenarios to the generation of new imagined scenarios, as well as to the generation of external actions in performance mode. In addition, this mechanism enables previous real episodes (hand-coded or formed during a performance mode experience) similar to a hypothesized scenario to be employed by analogy in generating possible continuations of that scenario. Furthermore, we consider the possibility and role of accidentally becoming reminded of a previous problem directly or indirectly applicable to a current problem (serendipity).

<!-- page: 140 -->

fsb

easter neste

4 pos:

-_

Sri

beer aves na Of

aicnakstintotmades hestenidegnat

ee me

| = ideng

iw

tat

itstabs

boaseal sepia Kia) wredeltvy

deurtaweraeOf aledaciieneta

» cap), puerj

ie a gee

esis?

otpeay?

ot i ral cade’

ip ini sebieves o lance 7

.

oi acca a pir?

hae ethos A

he -

oo is

»

Cp

Haggai’ ri, ‘oot =

eye a

Tew

ies

ef

a

pee be _

7

-

OS>:

ti

“en cet OW ttle. agedee

Pare

ae

wy eliasfr paaley ‘ank £

x a

L Sere

ive?

bl

her

rit

Terai

"ar tte

y

4

‘

Ji. ent

‘7

|

vi

cs

sh

>

sir

A.

wa

‘

—_—

Mae

OA

«

i

AR

Lael

tt

_

lw

bot

Dar

«

et

aun ald “ynee

ie

ln

ete mshi 7

at

y

ivelie

=a 2 Cuil

3.

‘i

«nD

i

“~

he

i

;

-

|

aay.

,

;

uel» Ad ay

a

Epepe ial Haeeyo potent Vey

beer gen art 7

(etnies writ ow ®

‘

ne

ohn

7

/

a

4 ‘ ‘

steht

eewrrio=

pip?

;

rian citi

;

’

——

}

T

LPP

’%

|

it

.

rm i sagt ve yriadell rt ee

im

a

e.4

abate

:

|

ae

Ty ding, cfr

Cyd

‘

pase

ae wrale

ofSarlavnet I

‘

eoby

_ -

ihe

Dae

iii

=tf

f

ee

ata

= arneinhevel Sects: “ -e

1

re

pe

} |

AY ¥

Ge i

ten

o

>,

Se

ovata

iw

i

beiy

pen brn

a sas Lee rap

|

a

ree

'

Aare

Vii i

on uel (aie

“

ur

be

—

ates

+?

eer

—

er

Golhfneuiy iideg hat

ants

>

pie

7

ma?

31).

aw

i

—_

wdrom

ioe

on

pee toa

eines.

Heme.ep

one

e%

be

bs

a0

quite, wel. eerie ping aril a «

int,-

and her

tig eonidesoneteil i

son——

-

4h extelilE-ionp ite

Soph

thatanct oF rhe etal SiC ‘6

aio nflatied

piespes is CA

tpg.

ar ee

thal“teh tet the Phat

eeramie

yadeened ta mete

it oe mil

: +

,

cavwlepe” MePornmem <P Kida Cnbili THR). te

5: 1

=

wie

wns ietx alo boberinee? auderiex 6 heer

ii Hed

-

Wi

maT FE eta putegig sical END

saab *. 9

ss —

sire

ag

me &

“

:

:

| ae"
