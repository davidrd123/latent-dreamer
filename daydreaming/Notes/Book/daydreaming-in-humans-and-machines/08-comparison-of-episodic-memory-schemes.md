# Chapter 8: Comparison of Episodic Memory Schemes

<!-- page: 215 -->

In the human stream of thought, thinking about one past experience or episode naturally leads one to think about another. In DAYDREAMER, associated with each episode is a collection of indices. An episode is retrieved when a sufficient number of its indices are currently active. Whenever an episode is retrieved, its remaining indices are made active. Therefore, retrieval of one episode may activate new indices which result in the retrieval of a second episode, which in turn may activate new indices resulting in the retrieval of a third episode, and so on, producing a stream of recalled episodes. Retrieval of a given episode may in fact require indices resulting from more than one previously retrieved episode. How are episodes stored? Given a collection of active indices, how are episodes which are stored under those indices retrieved? In this chapter, we examine three approaches which have been proposed in the past: discrimination nets, spreading activation, and connectionist nets. We then propose an alternative mechanism called intersection indexing.

## 8.1 Discrimination Nets

A discrimination net (Feigenbaum, 1963; Charniak, Riesbeck, & McDermott, 1980) is a tree structure whose branching nodes correspond to decisions, and whose leaves correspond to the final results of a series of such decisions. Discrimination nets were first used in the EPAM program (Feigenbaum, 1963; Feigenbaum & Simon, 1984) which models human learning of nonsense syllables, and have since been employed in a number of other artificial intelligence programs (Goldman, 1975; Kolodner, 1984). Discrimination nets may be used as a mechanism for episode retrieval in the following manner: Each branching node consists of the possible values of a given

<!-- page: 216 -->

emot = neg

emot = pos

obj = lamp obj = chair

ep1

obj = lamp obj = chair

ep2

ep3

ep4

Figure 8.1: Discrimination Net

index, and each leaf designates a particular episode. As an example—which we will use to compare alternative indexing mechanisms—suppose that episodes are indexed under emotions and objects, and that each index takes on two values: negative and positive, chair and lamp, respectively. Further suppose that there are four episodes, each indexed under one of the four possible combinations of indices and index values. A discrimination net which organizes these episodes for retrieval is shown in Figure 8.1. If one wishes to retrieve an episode given the indices and index values of positive emotion and chair, one starts at the root of the tree, then traverses the positive emotion branch, traverses the chair branch, and finally arrives at the node for eps. Such a discrimination net, however, does not facilitate retrieval when indices are presented in the wrong order (if, say, chair is presented before neg), when one or more indices are unavailable (if, say, only the index of neg is given), or both (if, say, only the index of chair is given). In such cases, traversal of the net cannot proceed beyond a certain point because an index is not available, or because the next available index does not appear on any of the branches at the current point. Humans, on the other hand, are able to retrieve episodes given partial sets of indices presented in various orders.

One solution to this problem is to employ a redundant discrimination net in which episodes are stored under several different index orders. A fully redundant discrimination net—an example of which is shown in Figure 8.2—enables a set of indices to be supplied in any possible order. If a partial set of indices is supplied, the traversal process will terminate at a nonleaf node. At this point, it is possible simply to retrieve all of the leaf episodes below this node. However, protocols (Kolodner, 1984; Reiser, 1983) and other evidence (M. D. Williams & Hollan, 1981) suggest that humans must still generate the remaining indices before recall of an episode can occur. Kolodner gives the following example of this process of elaboration: Suppose former Secretary of State Cyrus Vance is asked to recall any diplomatic meetings about the Camp David accords with Menachim Begin. Further suppose there are several such episodes in his memory indexed under different places—Israel, Belgium, and the United States.

<!-- page: 217 -->

emot=neg obj=chair |\ obj=lamp

obj=chair

ep1

obj=lamp

ep2

emot=neg

ep3

ep4

epi

ep3

ep2

ep4

Figure 8.2: Fully Redundant Discrimination Net

At first, without a place index no episodes might be recalled. However, he may infer the place in which an event might have taken place from the nationalities or countries of residence of the participants. In this case, Israel and the United States may be inferred as places, enabling the remaining branches to be traversed and the episodes recalled. (But note that the episode which took place in Belgium will not be recalled unless suitable indices for that episode are somehow generated.) Kolodner provides a collection of general and domain-specific strategies for elaboration in memory retrieval. These strategies are implemented in the CYRUS computer program which stores events in the life of former Secretaries of State Cyrus Vance and Edmund Muskie, and answers English questions about those events. Kolodner employs a redundant discrimination net in which each node is called an E-MOP and viewed as a generalization of all the leaf episodes below that node. Thus each E-MOP indexes individual episodes (leaf nodes connected

directly to the E-MOP) and other more specific E-MOPs (connected nonleaf nodes). These episodes and E-MOPs are indexed by their differences from each other (in terms of different possible indices and values). For example, in Figure 8.2, the E-MOP reached by traversing the emot = neg link from the root generalizes those episodes involving negative emotions—ep; and ep2—which are distinguished from each other by whether they involve a chair or a lamp. In CYRUS, E-MOPs are more than nodes: They are actually structures which

contain information useful in elaboration (such as default index values, causal relationships between E-MOPs, and other inferences). In addition, E-MOPs provide generalizations useful for disambiguation and filling in details in text

understanding (Lebowitz, 1980). While the number of nodes in a nonredundant discrimination net grows linearly with the number of stored episodes,’ a fully redundant discrimination 1Specifically, a nonredundant discrimination net requires (eb — 1)/(b— 1) nodes, where e is the number of episodes and 6 is the number of branches from each node (assumed to be the same for each node).

<!-- page: 218 -->

emot=neg

emot=pos

ep2

obj=chair

epi

obj=lamp

ep2

emot=pos

emot=neg

ep1

ep3

Figure 8.3: Redundant Discrimination Net with Delayed Expansion

net exhibits combinatorial growth. Kolodner copes with this potential problem in two ways: First, strategies are used to constrain the possible indices for indexing episodes and E-MOPs under a given E-MOP—the net is thus only partially redundant. Second, E-MOPs are created only when a new episode is to be added into the net at a point where an episode is already present. When a new E-MOP is created in this way, the two episodes are then indexed by their differences. Thus expansion of the net is delayed. For example, Figure 8.3 shows what the net looks like in this scheme before ep, has been added. Because of the unrealistic space requirements of redundant discrimination nets and further because we are not concerned with elaboration processes in the current work, such nets are not employed in DAYDREAMER for episode indexing and retrieval.

## 8.2 Spreading Activation Models

In general, spreading activation models of cognition (Quillian, 1968; Collins & Loftus, 1975; J. R. Anderson, 1983) consist of a processor and a long-term memory composed of a collection of nodes, each of which is connected to other nodes by bidirectional links.? Associated with each node is an analog level of activation, which may be viewed as modeling human short-term memory. The processor may access any node whose activation is above a certain threshold; such nodes are therefore said to be a part of working memory. The more active the node, the faster that node may be accessed (or the higher priority that node has to be processed). The activation of a node may thus be regarded as a heuristic for the relevance of that node to current processing (J. R. Anderson,

1983). A node is said to be retrieved from long-term memory when it is brought?The various spreading activation models differ in certain details from the one presented here, which is a combination of the model of J. R. Anderson (1983) and that of Langley and Neches (1981).

<!-- page: 219 -->

into working memory—when its activation rises above a certain level. Activation of a node is continually spread to linked nodes via the process of spreading activation. That is, every node with a nonzero activation continuously leaks some of its activation to connected nodes. The purpose of spreading activation is to increase the activation of nodes related to those currently active; presumably, if active nodes are relevant to processing, so are those nodes associated with active nodes. The activation spread from a node to a linked node is proportional to the numeric trace strength of the linked node relative to the sum of the trace strengths of all the linked nodes. In other words, activation spread to connected nodes is divided up among those nodes in proportion to their relative strengths. As a result, the more links a node has to other nodes, the less activation each of those linked nodes will receive from the node. This is called the fan effect (J. R. Anderson, 1983). Since activation is continually spread from one node to another, decay must be built into the mechanism in order to prevent the activation levels of nodes from forever rising. Decay will cause a node to leave working memory after a certain length of time (unless the activation of the node is replenished)— thus decay models the time-limited aspect of short-term memory. Continuous spreading of activation eventually leads to an even distribution of activation throughout the network; with the addition of decay, that activation tends toward zero. However, certain nodes, called source nodes, enable the introduction of new activation into the network. Source nodes may be created and added into working memory by the processor or through perception of the external environment. Miller (1956) found that the “channel capacity” of short-term memory is limited to seven—plus or minus two—meaningful chunks (Simon, 1974) of information. This limited capacity is modeled in spreading activation models by imposing a limit on the total amount of activation permitted in the system; this imposes a (variable) limit on the number of nodes which can be in working memory at any given time. How does working memory relate to subjective experience and consciousness? J. R. Anderson (1983) avoids any mention of consciousness, except in a note where he writes: “I do not intend to correlate active memory with what

one is currently conscious of” (p. 309). Ericsson and Simon (1984, pp. 221-223) assume that thoughts which may be verbalized (after suitable recoding) correspond to the contents of working memory. They thus imply that (objective) data structures in working memory are identical with the subject’s (subjective) focus of attention. However, they identify neither attention nor working memory with consciousness, since, as James (1890a, p. 285) has pointed out, attention picks out only some of the sensations which are a part of consciousness. Ericsson

and Simon (1984, p. 222) admit that they do not choose to identify working memory with consciousness because their concern is with information relevant in task-oriented situations. However, as argued in Chapter 10, the entire gamut of conscious thoughts (including, for example, feelings) is subject to verbaliza-

<!-- page: 220 -->

tion. Although Ericsson and Simon (1984) claim that “day-dreams and similar thoughts contain imagery that never is heeded directly” (p. 223), people are in fact able to describe such imagery to others (Varendonck, 1921; Klinger, 1971; Pope, 1978). Therefore, we can only ask the question: If these other compo-. nents of consciousness are not a part of working memory, what are they a part of? Spreading activation and network models of memory have been used by several researchers (Clark & Isen, 1982; Bower & Cohen, 1982) to explain emotiondependent memory recall effects: Emotional states are linked in memory to the experiences involving those states. When one is in a certain emotional state, spreading activation primes experiences connected to that emotional state, making it more likely for those experiences to be recalled. The basic architecture for connectionist models (Rumelhart, McClelland, & PDP Research Group, 1986; Waltz & Pollack, 1985) is similar to that for spreading activation as described here, with the addition of inhibitory links for spreading negative activation to connected nodes. Thus a node with a high activation level tends to reduce, rather than increase, the activation level of nodes connected by inhibitory links. An inhibitory link from one node to another indicates that the activation of one node is evidence that the other node should not be activated. Such links force the system to select one of the two nodes, but not

both. Rumelhart, Hinton, and McClelland (1986) present a generalized model for parallel distributed processing which includes many spreading activation and connectionist models as special cases. Continual flow of activation between connected nodes is computationally expensive in a serial system: The activation for every node in the network must

be calculated based on the activation of each of its neighboring nodes on every clock tick. Because of this problem, spreading activation programming tools such as the PRISM language (Langley & Neches, 1981) require the programmer to invoke spreading activation explicitly and to specify the starting nodes for the process. In PRISM, activation does not spread back along paths from which it originated and spreading terminates when a node is reached which has already had activation spread to it from another path (however, although spreading does not continue beyond such a node, the activation from multiple paths is in fact summed at such intersection nodes). If the “spreading to a depth” strategy is selected, the process also terminates when a certain depth is reached. If the “spreading to a limit” strategy is selected, spreading terminates when the activation level decays below a certain threshold. How may spreading activation be employed in retrieving episodes from memory? Figure 8.4 shows a network (loosely based on an example of J. R. Anderson, 1983, pp. 91-92) representing four actions (which are more specific versions of the example episodes we are using to compare various indexing mechanisms): failing at sitting down in a chair, failing to purchase a lamp, successfully purchasing a chair, and successfully turning on a lamp. For simplicity in this example, we assume that an episode consists of a single action performed on

<!-- page: 221 -->

## 8.2 SPREADING ACTIVATION MODELS chair (1.0)

<-chairs (.1)

201

act3 (.045 — success3) (.020) ——pos3

chair1 (.1)———act1 (.126)

| sit (.020)

(.018)

purchase (.057)

fail1 (.11)

neg (1.0) Kare (.1) —— fail2 (.09)

(.016)

pos4 (.002)

‘y

neg1 (.1) ——

——pos

success4 (.003)

act2 (.083)

turn-on (.001)

act4 (.004)

lamp2 (.036)

lamp (.033)

lamp4 (.003)

neg5 (.1)

Working

= Long-term

Memory

=: Memory

Figure 8.4: Network for Spreading Activation

behalf of a goal. In order to retrieve, say, episodes involving negative emotions and chairs, the nodes neg and chair are first activated, and then spreading activation is performed starting from those nodes. After this process completes,’ action, ends up with an activation of 0.126, actiong with 0.0830, actzong with 0.0450, and action, with 0.00428. A relatively high activation level is attained by action, since activation is spread to it from two different paths. That is, action, is a node at which the paths emanating from neg and chair intersect. The node we desire to retrieve is in fact action; since it is an episode involving negative emotions and chairs. However, another such intersection node is actiong, because this node involves negative emotions and purchasing, which in turn involves chairs via action3. Thus spreading activation may find episodes that are only indirectly related to the given retrieval indices. The threshold for adding items into working memory is set depending on how close of a match is desired for retrieval:

if the threshold is set to 0.1, action, is added to working

memory; if the threshold is 0.08, both action; and action2 are added. Thus spreading activation provides a promising mechanism for retrieval: For example, if it is desired to retrieve episodes similar to an episode which is currently active, one may simply spread activation from the current episode to 3The sample values shown in Figure 8.4 were calculated according to the “spreading to

a limit” strategy of PRISM (Langley & Neches, 1981) with all trace strengths equal to 1.0, spread decay equal to 0.9, and nodes other than neg and chair having an initial activation of

## 0.0 It was further assumed that relation or class nodes (such as neg, chair, purchase, and

so on) were connected to a total of 10 nodes (not shown).

<!-- page: 222 -->

related nodes. This works without regard to how these episodes are represented since a link between two nodes implies some relationship between those nodes in any representation scheme. The more relationships the current episode has in common with another episode, the more activation that other episode will receive. In effect, spreading activation is able to discover its own indices—it would appear that higher-level patterns can emerge out of the relationships already present in the network. In addition, the fan effect automatically assigns higher priority to more unique indices, since nodes which are less unique have more links to other nodes and thus are less likely to contribute to retrieval. Unfortunately, there are several problems with spreading activation: The first problem is that the activation levels of nodes along the path to a desired episode—and even those not along such a path—are often higher than the activation of the episode itself. In fact, unless intersection effects are significant, they must be higher. As a result, all of these nodes are retrieved in addition to the desired episode nodes. These extraneous nodes are difficult for most programs to handle. For example, suppose we would like to retrieve action2; we therefore set the working memory activation threshold to 0.08. In this case, not only are action, and actiong added to working memory, but so are negs, chairs, chair,, negi, fail,, neg, and fail. Many of these nodes are irrelevant; we only wanted to know about actions directly or indirectly involving negative emotions and chairs. One possible solution (although it is somewhat against the spirit of spreading activation) is to limit retrieval to certain classes of nodes, such as actions. A second problem is that as more nodes and links are added to the network, or as desired episodes becomes more distant from their indices, the activation level of a desired episode becomes lower and lower. This is because the fan effect divides the leaked activation of each node among all its connected nodes. The result is that the activation levels of retrieval indices are very large in comparison with the activation levels of retrieved episodes. A possible solution (which again seems against the spirit of the mechanism) is to bring the activation level of an episode up to a certain high level once it is retrieved. A third problem is that the difference in activation level—in a particular situation—between a desired episode and an undesired episode may be very slight. To an extent this effect occurs in our example: although action; is much more related to negative emotions and chairs than is actiong, their activation levels differ by a relatively small amount. Even so, after carefully setting the working memory threshold in order to retrieve the desired episode in one situation, the programmer may discover that this threshold does not retrieve desired episodes in other situations: the activation levels of episodes are in a completely different range. Finally, spreading activation mechanisms often present a temptation to try to tune the system manually by modifying trace strengths or the network itself to get a more intuitive result: Why, for example, does action end up with

a higher level of activation than action3?

After all, actions is closer to chair

<!-- page: 223 -->

than action is to neg. It is not because actions is involved in an intersection. Rather, it is because of the somewhat irrelevant fact that chairl is involved

in two actions (and thus its activation is divided in two) while neg only results from fatlg which is only associated with actionz (and thus in this case the activation is largely preserved). At this point, one may attempt to tune the network. However, this often degenerates into a never-ending process of tuning and retuning. Something is wrong if the proper operation of an artificial intelligence program depends on a very fine tuning of numbers by the programmer. Of course, if the system is able to tune itself, that is another matter entirely. Learning in connectionist models is in fact accomplished via automatic tuning of connection strengths (McClelland, Rumelhart, & Hinton, 1986). We consider these models in the next section. The rule intersection search employed for serendipity detection in DAYDREAMER does not suffer from the same problems as general spreading activation, since activation spreads only through rule interconnections, rather than through every concept link or relationship. Rule intersection is therefore a more “content-based” scheme (Reiser, 1983) for spreading activation. A redundant discrimination net trades space for fast retrieval time, while spreading activation trades time for a compact memory representation: Spreading activation is expensive in terms of processing time because the activation level of every node connected to retrieval indices up to a certain depth must be calculated. In contrast, with redundant discrimination nets it is merely necessary to perform a linear traversal of a number of levels of the tree corresponding to the number of indices (nonetheless, Kolodner’s [1984] retrieval algorithm exhibits a combinatorial increase with the number of indices because it considers all possible paths “in parallel” until an episode is retrieved). Because of the problems with general spreading activation discussed above, this mechanism is not employed in DAYDREAMER for episode retrieval.

## 8.3 Connectionism

McClelland and Rumelhart (1986) present a connectionist model of memory storage and retrieval. The essential features of this model are as follows: Given a collection of episodes encoded in terms of a set of microfeatures, the mechanism automatically finds salient features of those episodes. That is, it classifies the episodes into one or more schemata—central tendencies of similar sets of episodes. Although single episodes cannot be retrieved from the network, similar episodes which have been repeated several times can be retrieved. Thus episodes and schemata are not distinguished in the network—all episodes are effectively stored as schemata. Schemata are represented as a stable pattern of activation in the network. Learning of schemata is accomplished in an incremental fashion through alteration of the strengths of links when presented with a new episode. Retrieval is

<!-- page: 224 -->

viewed as reinstating the activation pattern of a prior schema given part of that schema.

A subset of the nodes of the network (those that are not hidden) are used for

the input and output of schema microfeatures. These external nodes are used to provide indices for retrieval (input) and to provide information retrieved about a schema (output). In order to perform a retrieval, appropriate index nodes are activated—any of the external nodes may be used. Retrieval then consists of reading the activation levels of the remaining external nodes. Thus retrieval consists of filling in the remaining portion of a schema given a part of that schema. How might we use such a mechanism in DAYDREAMER? One task is for one or more active or recent episodes to produce a reminding of another episode which is related to those episodes. Since a connectionist network can only represent one episode at a time (McClelland & Rumelhart, 1986, p. 176), there must be multiple copies of the same network; the number of copies will be determined by the maximum number of episodes we wish to allow to be active at the same time. The external nodes of each of these networks are then connected together in order to enable episodes active in source networks to produce a reminding of an episode having similar external microfeatures in a target network. The connections must be unidirectional—that is, the external nodes of the source networks must charge the external nodes of the target network without the external nodes of the target network affecting the external nodes of the source networks. Without unidirectional connection of external nodes, all networks would settle into the same episode. Furthermore, this directionality will have to change depending on which networks are currently source networks and which is the target. Each network will contain a collection of hidden nodes for representing episodes in a distributed fashion, and a set of external nodes for input and output. The external nodes will employ a local representation of the microfeatures of interest external to the module.* In particular, we will use the microfeatures of the example we have been using to compare various retrieval mechanisms. As shown in Figure 8.5, there will be one external node for each of the retrieval indices (negative emotion, positive emotion, chair, and lamp) and nodes for the information which we desire to retrieve, namely, the action performed in the episode. We then incorporate the relevant episodes into the network through use of a learning algorithm (see, for example, McClelland & Rumelhart, 1986, pp. 179-181). At this point, the connection strengths among the hidden nodes 4Local representations of external microfeatures—already determined to be significant by the programmer—are used in most of the example connectionist networks discussed by Rumelhart et al. (1986). One example (Rumelhart, Smolensky, McClelland, & Hinton, 1986, pp22-38) uses objects in a room as external microfeatures, another (Smolensky, 1986a, pp. 240250) uses the resistance, voltage, and current in parts of an electronic circuit. In fact, these examples even involve local representations of the network itself, constructed manually by the programmer. An exception is provided by the learning mechanism of Rumelhart and Zipser (1986), which discovers salient features starting from an arbitrary initial set of microfeatures.

<!-- page: 225 -->

neg

indices

os P;

chair

lamp

retrieved information

purchase sit

Figure 8.5: Connectionist Net for Episodes: First Attempt

encode the relevant episodes in a distributed fashion. If we now active various indices, the network will respond with the prototypical actions for those indices. Furthermore, we can activate actions and see what indices respond, or any other combination. However, there are several problems with the above: First, for actions we need to be able to represent the type of action (such as purchase and sit), and the slots—sometimes called roles and cases (such as the agent, object, destination, instrument, and so on)—of the action. How do we represent actions in terms of the activation levels of external nodes? How, in particular, do we implement slots? For example, if an object is active, how do we know which slot it fills of the active action? Slots may be implemented by using clusters of mutually inhibitory nodes (Rumelhart, Smolensky, McClelland, & Hinton, 1986, pp. 33-34). (This assumes whoever is reading the external nodes is able to interpret this output network properly.) Thus we might use a network such as that shown in Figure 8.6. The drawback of this, however, is that each entity (such as person1) must be replicated for each possible slot which that entity can fill. If there is only one action per episode, this may not be so bad. However, if there are several actions in each episode, then each action type and slot must. be repeated for each of the actions in the episode, as shown in Figure 8.7. All of these nodes, of course, have to be allocated in advance. Second, observe what is happening here: Instead of the representation emerging automatically within the network itself, the representation is being constructed manually by the programmer at the external nodes of the network, as shown in Figure 8.8. A major claim of connectionist models is that they can be used to find a set of salient features in input patterns already encoded in terms of some set of microfeatures (McClelland, Rumelhart, & Hinton, 1986; Rumelhart & Zipser, 1986). Unfortunately, it is difficult to find an appropriate set of microfeatures when the system has to deal with complex concepts such as emotions, goals, actions, beliefs, persons, objects, and causal relationships. For example, what set of microfeatures should be used to represent actions? How, for example, will the system distinguish between a sequence of two sim-

<!-- page: 226 -->

destination

PERSON2

Figure 8.6: Connectionist Net for Episodes: Second Attempt

ilar actions performed in one episode, and two similar actions which occur in different episodes? In the first case, one would like to retain two actions. In the second case, one would like to form a generalization. The connectionist model of memory presented by McClelland and Rumelhart (1986) would generalize in both cases. In order to avoid difficulties such as this, the connectionist designer is forced to examine such questions as: What is an episode? How are episodes represented? Should an episode be represented as a sequence of actions? Should the sequence of actions be ordered? Do episodes involve causal relations among those actions? What kinds of causal relations are there? These are exactly the kinds of problems which knowledge structures such as scripts, plans, and goals

(Schank & Abelson, 1977) are intended to address. Although connectionist models do not enable one to bypass the difficult problems of representation, they do provide a degree of automatic generalization once appropriate representations have been found. Nonetheless, it is important to recognize the limitations of this generalization mechanism: First, unlike mechanisms for explanation-based generalization (DeJong, 1981), connectionist learning procedures cannot distinguish relevant features from irrelevant features in a small number of trials. Consider a story about kidnapping which describes the kidnap victim as a woman whose hair is dark brown and whose father is rich. A connectionist model will treat each of these features equally. A human, on the other hand, knows that certain features are more relevant to the goals of

<!-- page: 227 -->

various

indices

action1

agent!

action2 ATRANS

@

agent2

Figure 8.7: Connectionist Net for Episodes: Third Attempt

SCRIPTS, PLANS, GOALS, etc.

Figure 8.8: Connectionist Net for Episodes: Fourth Attempt

<!-- page: 228 -->

the kidnapper and hence are more useful as components of a generalization. Second, since generalizations are emergent properties of the network rather than representational structures, connectionist mechanisms have difficulty in using those generalizations as a basis for the representation of further concepts (McClelland & Rumelhart, 1986, pp. 209-214). For example, one task in DAYDREAMER is as follows: When the system is reminded of an episode which is related to the current daydream, components of that episode may be incorporated into the daydream. A connectionist scheme has one method of combining episodes—this method might be described as interpolation. Daydreaming requires more interesting ways of composing episodes: For example, employing an episode as a subepisode of the current daydream, employing only parts of the episode in the current daydream, modifying parts of the episode before employing them in the current daydream, and so on.® Viewing episodes and schemata as emergent properties of a network is an intriguing idea, but—in its current formulation—of little use in the generation of complex behaviors such as daydreaming. At the very least, connectionist models provide a mechanism for episode retrieval similar to that provided by spreading activation. How do connectionist models differ from spreading activation models? Distributed connectionist models create new concepts by tuning the weights of the links in the network to create a new stable pattern of activation. However, only one concept can be represented at a time in such a scheme. This presents a serious difficulty since it is often necessary to relate two concepts and it is often necessary to have multiple instances of a given concept. One solution is to use a separate network for each concept instance (Hinton, McClelland, & Rumelhart, 1986; Hinton,

1981). McClelland (1986) presents a mechanism called connection information distribution which replicates modular networks on a demand basis. In local spreading activation schemes, a new concept is created simply by adding a node to the network and connecting it via appropriate links to the nodes to which it is related.

A local connectionist scheme which allows creation of new nodes,

however, is quite similar to spreading activation networks.

## 8.4 Intersection Indexing

Instead of employing a general spreading activation mechanism for indexing, we propose the use of what we call intersection indexing. This scheme employs a network consisting of a node for each index and a node for each episode. Each index node has a connection to those episode nodes indexed under that index. An intersection net for our example is shown in Figure 8.9.

A marking algorithm (Quillian, 1968; McCarthy et al., 1965, pp. 42-43) is employed for retrieval. Given a collection of retrieval indices, the connections from each index to each episode are traversed

and a mark count

>The application of episodes in daydreaming is discussed in Chapter 4.

associated

<!-- page: 229 -->

## 8.4 INTERSECTION INDEXING emot=neg

ep1

emot=pos

ep2

209

obj = chair

obj = lamp

ep3

ep4

Figure 8.9: Intersection Indexing Net

with the episode (initially zero) is incremented. Whenever the mark count of a given episode reaches a certain threshold specified by that episode, that episode is retrieved. The number of indices required for retrieval is determined on an episode by episode basis. If all indices are required for retrieval of a given episode, the threshold specified with that episode should be equal to the number of connections to that episode. Often, however, it is desired to retrieve an episode when some percentage of its indices are active. In DAYDREAMER, episodes are indexed in a redundant fashion so that retrieval is possible even if only some of a given episode’s indices are active. Index intersection is in the spirit of spreading activation, but without all its disadvantages. It is similar to the nets used in PANDEMONIUM (Selfridge, 1959). Because only one level of links is traversed, the retrieval algorithm is not expensive. The number of links traversed is equal to the total number of episodes connected to the given indices. Nor does intersection indexing have the prohibitive space requirements of a fully redundant discrimination net. At any time, DAYDREAMER maintains a short list of active indices. Whenever an episode is retrieved (using the currently active indices), the remaining indices of that episode are activated. Active indices and intersection indexing are illustrated in the daydream ROVINGI (see page 5). An episode at Gulliver’s

Restaurant (recalled directly by the ROVING daydreaming goal) activates the indices of Marina del Rey (the location of the restaurant) and Steve (a character in the episode). The index of Marina del Rey in turn leads to retrieval of an episode (whose reminding threshold is one index) involving a job located there. Retrieval of this episode in turn activates the index of Venice (a town along the bus route to the job). Now the combined active indices of Steve and Venice, resulting from two different episodes, lead to the retrieval of a third episode (whose reminding threshold is two indices) involving Steve at Venice Beach.

<!-- page: 230 -->

a

Lsa

a

ee

aS cca Gat has eat pal Be Baval Nig Dir aires: o 2 i

7 _ astcae

s Ue

\Craennt Aly en Agate eee ie he dlngtes Aer imont aa, Reed

Gexie na

wet

eteta a nas tie

al

fades

hat

7%

ii yi pica se.jee

iat 02 raetk

cia ested ae

Aas

a

A

;

S| oes at. eS dh

eae

ute

ot Gctie ines yg adtaetigi Yi ic

efine). Tatham

Aa a

3 aa rege Lara tie ah Lot

edi”

j

SHY hi

Mi Peat

wi} nbd he

oc]Sati1phag earot cil pat onaR S ri ‘!)Leni grillaidasre “eel a Mcr is | “fd: ot, ci Sa Aidee et”

Feu iter

yan» ‘4

tadashi are

isFaa Vag.¥ acli vf fee: ed Tae

et fis yas ania ss pay ANG} Laie oe‘Glait Poa ot if} rail ie HEas 36 we.

wove.

a Raker in A a. ut asa) f7 i POUT in

a!

ies

sia! % ore Pict ath it Tn Digits 0) oheo!l nui iy batt trap Pibengeniv's|

nt eigen aan ay

8) yeiaeY

Ago

hus ction

i!

reac

wi oad; Lown 7 syalePenge.erty

eaaiehasi ‘aves

diet S—

ie

adieoed

bath

| *

RS, yonicenne,. subd writs Srbeag, Bhd soupsandide

th tai ti Wet

hed ‘i ented

_A asking

etre? US i Rote PRN LAYi} %ching an tk ai ll 0wb anh! S gota att Aeon |at Poulweafy i. bien. palustre paoiier)

#

lo xahei od evlaVieon cms ol afnmiqe wildbee

rmArr oh pase. |

gil Be

vey Lert ip ea 4 Bg vay Lis Yel! on iy bs nae:

tslew

L

om.

as «.

-

et!

We Oi chayihy pees twice ay

4nd!

mele

Gy ee

SS

of, MACathy ai mya

‘

hel

re: 4

vil ret eval werrce

wt

and

Ls

» (leasemet’

pid Uri

a2

ee
