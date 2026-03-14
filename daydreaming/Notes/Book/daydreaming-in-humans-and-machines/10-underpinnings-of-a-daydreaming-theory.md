# Chapter 10: Underpinnings of a Daydreaming Theory

<!-- page: 253 -->

This chapter addresses the philosophical, scientific, and methodological issues involved in the construction of a computational theory of daydreaming. First, we examine the philosophical justifications and implications of our underlying assumption that daydreaming can, at least in principle, be captured in computational terms. We address the apparent paradox in constructing an objective computer program to capture a subjective phenomenon such as the stream of consciousness. Second, we address the problem of semantics in computer programs. We examine the sense in which a computer made out of electronic components can be said to exhibit the same cognitive processes or mental states as a human. Third, we address the scientific issues involved in our theory: (a) the objectives—in particular, the explanatory goals—of the theory, (b) the methodology employed in constructing the theory, and (c) the testing and evaluation of the theory.

## 10.1 Philosophical Underpinnings

What is the true nature of the human mind? How is the mind (that is, the notion of an entity which thinks and is conscious) related to the brain (that is, the physical organ)? What, in reality, are mental entities such as thoughts, sensations, emotions, desires, and beliefs? How do such mental entities relate to the physical world? What do we mean by a computational theory of an inherently subjective phenomenon such as the stream of consciousness? These questions are aspects of what is known in philosophy as the mind-body problem.

<!-- page: 254 -->

234.

CHAPTER

### 10.1.1 10. UNDERPINNINGS

OF A DAYDREAMING

Functionalist Approach to Mind-Body

THEORY

Problem

Theories which address the mind-body problem may be divided into two classes:! dualist theories and materialist theories. Dualist theories maintain that the mind is a nonphysical entity—that intelligence cannot be explained in

physical terms alone. That is, part (or all) of intelligence is provided by something outside of the physical realm. Materialist theories maintain that the mind can be explained in purely physical terms—that mental states and processes correspond in some way, however complex, to the states and operations of the brain. In the dualistic theory of René Descartes (Russell, 1945, p. 561), the mind

(or soul) exerts an influence on the body and this influence is located in the pineal gland (a pinecone-shaped body attached to the base of the brain thought by Descartes to be unique to humans but later found to occur in all vertebrates [Asimoy, 1963]). After the discovery by scientists of conservation of momentum, which would rule out a nonphysical influence on the physical world, his disciple Geulincx proposed (Russell, 1945, pp. 561-562) that the soul does not act on the body; rather, the body operates in exact synchronism with the mind so that it appears that there is an influence when in reality there is not. The problem with this theory is that if the body behaves according to rigid, physical laws, then the mind, operating simultaneously, would be characterizable in terms of those same laws (and thus dualism is lost). Dualistic theories, which place the mind in the nonphysical realm, contend that a purely physical explanation (and therefore a scientific explanation) of the mind is not possible. In seeking a scientific, objective (at least partial) explanation of the phenomenon of human daydreaming, we make the assumption that such an explanation is possible. It is logically possible for this assumption to be false—it may eventually turn out, as in the theory of Descartes, that the mind, a nonphysical entity, is in fact able to influence the body in the physical world (for example, via some as yet unknown form of energy exchange [Churchland, 1984, pp. 9-10]). Nonetheless, cognitive science and the neurosciences have already achieved a degree of explanatory success operating under the assumption that an objective characterization of mind is indeed possible (see Churchland, 1984, pp. 18-21 for a more detailed version of this argument). In a functionalist theory of mind—a special brand of materialist theory—the essential nature of a mental state is how that mental state is causally related to inputs (environmental stimuli), outputs (bodily responses), and other mental

states (Fodor, 1981; Dennett, 1978).

Mental states have a “life of their own”

independent of any particular physical realization. In principle, it does not matter whether mental states with a given set of causal properties are realized on a digital computer, in the brain, or in some alien biology. See, for example, Hofstadter’s (Hofstadter & Dennett, 1981, p. 376) discussion of how human 1The taxonomy presented here is based on those provided by Churchland (1984) and Fodor (1981).

<!-- page: 255 -->

mental processes might be simulated using a variety of media including toilet paper. In computer science terms, mental processes can be carried out on any device providing a suitable interpreter. An interpreter for a given programming language L is a program P’, written in a language L’, which carries out the operations (including generation of output) specified by a program P written in L, given P (and data) as input. Interpreters enable programs in a given language to be run on computers with different hardware architectures and instruction sets, provided that an interpreter for the language exists for the particular computer. Interpreters may be nested: An interpreter for one language may be run by another interpreter, which in turn is run by another interpreter, and so on, until eventually there is an interpreter which is able to execute on the computer in question. A computational theory of mind is a functionalist (and therefore materialist) theory. Computational theories are not specified in terms of neurophysiological processes, but in terms of an abstract computing device called the Turing machine (Turing, 1936).? Turing machines provide a hardware-independent language for describing mental processes in a functionalist theory of mind.* The modern digital computer may be characterized as a Turing machine.* Rather than specifying a computational theory directly in terms of a Turing machine, the theory is usually programmed on a computer running a high-level language

such as Lisp (Winston & Horn, 1981).

### 10.1.2 Problems with Theories of Subjective Phenomena

The stream of consciousness is an internal phenomenon experienced subjectively, from a particular point of view which is unique to an individual. How can there be a computational, materialist theory of a subjective phenomenon such as the 2Some authors (for example, Johnson-Laird,

1983, pp.

9-10) define functionalist theories

as those which characterize mental processes in terms of effective procedures (or algorithms) as distinguished from Turing machines. The Church-Turing thesis (which we adopt) states that the intuitive notion of an effective procedure is equivalent to a Turing machine. 3The Turing machine provides a deterministic model of human behavior. In order to admit indeterministic

quantum

mechanical

effects we would

have to add a true random

number

generator to our Turing machine. However, whether and how quantum effects at the subatomic level might cascade to the level of human behavior presents a very difficult problem (Dennett, 1984, pp. 77, 135-136). 4The digital computer is theoretically equivalent to the finite-state automaton (with a very large number of states) rather than the more powerful Turing machine with its infinite tape. Nonetheless, most researchers view computation in terms of Turing machines for (at least) the following reasons: First, the memory of modern computers is so large that for most purposes they can be considered as Turing machines (Lewis & Papadimitriou, 1981, p. 362). Second, the Turing machine is closer to the computer in its basic architecture than the finite-state automaton: A finite-state automaton is a large state-transition network and has no memory analogous

to the tape of a Turing machine

or the primary

and secondary

memories

of a

computer (consult Pylyshyn, 1984, pp. 69-74 for an elaboration of this point). However, see Kugel (1986) for an interesting proposal that thinking may be more than computing.

<!-- page: 256 -->

10.

UNDERPINNINGS

OF A DAYDREAMING

THEORY

stream of consciousness? First of all, what do we mean when we say that the stream of consciousness is “experienced subjectively”? Everyone is familiar with the feeling of consciousness: There seems to be an “I” at the center of everything which observes, experiences, and—to varying degrees—controls the events which make up consciousness. As Dennett (Hofstadter & Dennett, 1981, p. 6) has noted, we feel that the “I” is located roughly in back of our eyes and in between our ears. The “I” perceives the world through the senses, feels various emotional states, experiences the “I” feeling, thinks, imagines, decides to perform—and then performs—various actions in the world, and so on. However, this “I” feeling is sometimes lost—in unconscious states, in certain dream states, in certain daydreaming states (Watkins, 1976), or even when becoming very involved in a movie or novel. The subjective rate of consciousness is not always linear with real time. There is the common observation that “time flies when you’re having fun.” On the level of milliseconds, one is often surprised, upon looking at a wristwatch, by how long the first click of the second hand seems to take (if one starts looking at the watch at the very instant of a tick)—thereafter, the second hand resumes its normal subjective rate. We assume that other human beings have internal experiences similar to our own—that others, for example, have the “I” feeling. Others describe their experiences to us, and we can imagine—with varying degrees of apparent success— how it would feel to have those experiences. However, the exact subjective experience of another person is inaccessible to us (just as others do not have access to our subjective experiences). We will never know exactly how a particular experience felt to the other person. Even if someone describes a subjective experience which seems to coincide with one of our own subjective experiences, there is no guarantee that the experience is actually felt the same way. This problem is demonstrated in the classic philosophical conundrum of color perception: How does one person know that another person sees the same color when both look at a “red” object? It is possible that the second person subjectively perceives what the first person would call “blue,” but calls it “red” because that is the language convention which the second person has learned. Even if similar physical events occur in the brains of two persons when viewing a red object, this does not necessarily imply that the color is subjectively experienced in the same way. Nagel (1974) considers the problem of subjective experience in asking the reader to ponder “what it is like to be a bat.”° Bats perceive the world in a way entirely foreign to humans: Instead of normal vision or hearing, bats use echolocation (bouncing high frequency sounds off of objects in the environment to determine their distance, velocity, size, and shape—a kind of natural sonar). How can a human ever hope to know what it is like to be a bat? You can try to >The example of bat experience was used previously by Farrell (1950, pp. 183-184).

<!-- page: 257 -->

imagine being a mouselike animal with webbed wings who flies around at night to capture and eat insects; you “see” objects only using sonar; this is completely natural to you; and so forth. But to the degree that you are able to imagine this, you would only know what it would feel like for you to behave as a bat, not what it feels like to the bat to be a bat. The question of knowing what it is subjectively like to be another creature presents a difficult philosophical problem, since in order to know what it is like to be that creature, you would have to be that creature, in which case you would no longer be yourself, and hence it would no longer be you knowing what it is like to be that creature.

Narayanan (1983) asks “What is it like to be a machine?”

in the context

of an example involving a nuclear reactor controller which hypothesizes and prepares for potential future events—an instance of daydreaming. Narayanan proposes that it is necessary for one to take the subjective “viewpoint” of such a program in order to understand its operation. The common, intuitive notion of subjective experience is difficult if not impossible to define in objective terms.® We should not then expect a computational theory to account for subjective experience in this sense. If a computational theory were constructed which completely accounted for the behavioral impact of consciousness (ignoring for the present discussion what constitutes a “complete account”), it appears that the theory would be logically compatible with the absence of this concept of subjective experience. Put another way, it seems that a human or computer which had such subjective experiences would behave identically to one which did not. This makes one wonder what the role of these subjective experiences could possibly be. If the stream of consciousness is defined in terms of subjective experience, how can a computational theory be constructed to account fully for it—in all its subjective glory? Since pinning down the common notion of subjective experience is an insoluble (for the present, at any rate) philosophical problem, we must instead formulate a definition of the stream of consciousness which does not make use of this notion. Our definition must employ an objective, external frame of reference, rather than a subjective, internal one. The stream of consciousness must therefore be defined operationally in terms of the input-output behavior—verbal and otherwise—of an organism having

(the common

notion of) a stream of consciousness.

Since we have made the

materialist assumption that human behavior is capturable in computational terms, the stream of consciousness, redefined as input-output behavior, is also capturable in computational terms. At this point, the reader may well wonder if we have rendered our definition so unlike the intuitive conception of the stream of consciousness as to make it useless and irrelevant to those interested in this private, internal phenomenon. 6QOn the face of it, a dualist theory would seem to be more likely to account for subjective experience since in such a theory the “I” feeling could be located outside the physical world. However, as argued above, dualist theories fail to provide a satisfactory scientific account because such theories are not stated in objective terms.

<!-- page: 258 -->

10.

UNDERPINNINGS

OF A DAYDREAMING

THEORY

But the purpose of this definition is merely to ensure that the phenomenon under investigation is in principle capturable by a computational theory. In fact, such a theory—if complete—will have to account for a rich set of behavioral phenomena, including practically every aspect of the intuitive notion of the stream of consciousness—that is, every aspect except this elusive notion of subjective experience. Most, if not all, aspects of our common conception of the stream of consciousness—the contents of the stream—will be accounted for by the theory, since most such aspects have a causal effect on behavior. For example, when we feel a negative emotional state, we tend to behave more negatively. When we decide to perform an action in the future, we often do perform that action. We are capable of producing rich, detailed descriptions of our internal experiences. Our verbal behavior is thus influenced in a very complex way by what we refer to commonly as the stream of consciousness. Accounting for this behavior is a sufficiently—in fact, extremely—difficult problem without also having to explain the true nature of subjective experience (whatever that might be). The mere fact that the content of internal experiences can be described to others implies that those experiences are not epiphenomenal. If the experiences were epiphenomenal, they would not have a causal effect on verbal behavior. However, the subjective sensation of experience may indeed be epiphenomenal. We do not deny the existence or reality of this sensation. From an internal point of view, this sensation certainly seems to exist.

However,

we do assume

that

this subjective sensation has no causal effect on behavior. This is a reasonable assumption as argued above, since its falsehood would seem to imply influence on the brain from outside the objective, physical realm. If, possibly, the assumption is false, a stumbling block will eventually be reached. However, we are a long way from reaching such a stumbling block. In the meantime, we can investigate what appears to be a large component of our intuitive notion of the stream of consciousness which does have an effect on behavior. If we are eventually able to construct a complete theory of the behavioral impact of the stream of consciousness, which addresses all the important issues regarding our intuitive notion of the stream of consciousness except for the subjective sensation, we may decide that the question of subjective sensation is no longer an important question for us. Thus our theory of the stream of consciousness will attempt to account only for its objective, empirically observable aspects. That does not mean that we must ignore the many varied aspects of consciousness with which we are all familiar, since these aspects are observable in the descriptions we receive from people. DAYDREAMER is the computer program embodiment of a computational theory of the stream of consciousness as defined operationally in terms of the impact of the intuitive notion of the stream of consciousness on behavior: verbal and otherwise. Why focus on the stream of consciousness? How do we know that the stream of consciousness is a relevant object of study? Some psychologists (see, for example, Nisbett & Wilson, 1977; Watson, 1924) have criticized the use of

<!-- page: 259 -->

introspective reports of consciousness (James, 1890a; Titchener, 1912) in inferring cognitive processes. Although the subjective aspect of consciousness may have no causal effect of behavior, the contents of consciousness influences verbal behavior, and, furthermore, reflects the useful activities of creative problem solving, learning from imagined experiences, and emotion regulation (as argued in Chapter 1). In addition, Ericsson and Simon (1984, pp. 24-30, 48-61, 220) present extensive arguments—which need not be repeated here—that verbal reports of the stream of consciousness are not epiphenomenal but highly relevant to the study of cognitive processes and memory structures. If our theory is to account for the effect of the intuitive notion of stream of consciousness on behavior, our theory must include some construct, or architectural element, corresponding to the stream of consciousness. In fact, it does: the current planning context discussed in Chapter 7 is such an element.

## 10.2 The Problem

of Semantics

What is the nature of the meaning of concepts? How are concepts programmed in a computer

program?

How

can

a data structure

in a computer

program

be said to represent a concept? What do we mean when we say a computer program has semantics? What is semantics, anyway? How can we represent the subjective aspects of consciousness in a computer program? How could a mental state such as embarrassment be programmed into a computer?

### 10.2.1 Structure-Content Distinctions

A distinction is often made between claims regarding the content of a system and claims regarding the structure of the system. Newell (1982) distinguishes between the “knowledge level” and the “symbol level” (and several other lower levels). The knowledge level is a system which consists of a set of goals, actions, and a body of knowledge. The behavior of the knowledge level is specified by the “principle of rationality”: If the system has knowledge that an action will achieve one of its goals, that action is selected. (Knowledge may thus be viewed as whatever a system has that enables its behavior to be computed according to the principle of rationality.) However, this principle does not specify which action will actually be performed, for example, in the event that several actions are selected or if multiple, conflicting goals are present in a given situation. Thus the knowledge level provides only a partial specification of behavior. The remainder of the specification is given by the symbol level, which provides mechanisms for control, encoding of knowledge, memory storage and retrieval, and so on.

Pylyshyn (1984) distinguishes three levels: the “physical level” (or biological level), the “symbol level,” and the “semantic level.” These correspond respectively to the device, symbol, and knowledge levels of Newell. Symbol level generalizations are expressed in terms of the “functional architecture” of the

<!-- page: 260 -->

10.

UNDERPINNINGS

OF 4 DAYDREAMING

THEORY

system: A “virtual machine” for cognitive processes providing a set of primitive operations. Pylyshyn proposes the criterion of “cognitive penetrability” for determining whether a given function should be located within the symbol or semantic levels: If a given function is alterable in a semantically regular way through the use of mental representations such as goals and beliefs, then that function is cognitively penetrable and should be located in the semantic level.

### 10.2.2 Semantics of the Stream

of Consciousness

In order for DAYDREAMER to model the stream of consciousness, it must contain representations for the contents of the stream of consciousness. As we have noted above, the stream of consciousness contains a variety of phenomenological events. How can we represent events which are inherently subjective: valid only from a single point of view? Pekala and Levine (1981) have developed a methodology for assessing subjective experience. Subjects are asked to sit quietly with their eyes open for several minutes and then complete a 60-item questionnaire. Items are drawn from the following aspects of consciousness: body integrity, time, state of awareness, attention, volition, self-awareness, perception, positive affect, negative affect, imagery, internal dialogue, rationality, memory, meaning, and alertness. The subject rates each item along a 7-point scale, where extremes are provided by statements such as “I experienced no strong feelings of anger” and “I experi-

enced very strong feelings of anger” (p. 43). The questionnaire thus provides one representation for the phenomenology of consciousness. However, this representation is too broad for our purposes since it provides too low a level of detail (e.g., why is the subject angry, and at whom?), and does not measure the instantaneous change of consciousness over time (e.g., was the subject angry during the entire period, or merely for an instant?). Although stream of consciousness events are subjective, it is possible to make generalizations about them. It is reasonable to expect that human beings have similar subjective experiences. Such a set of generalizations is provided by human language. Through use of such generalizations it is possible to describe subjective experiences to others. Another possibility is to provide a “key” which maps the program’s representations to descriptions of what subjective experiences they involve: The key could state, for example, that a particular representational structure corresponds to the feeling of embarrassment in humans. This is in effect what the English generator of DAYDREAMER accomplishes. One might argue that this is begging the question—we have substituted one ill-defined description language for another. This problem of absolute meaning is not unique to the phenomenology problem, however. An identical problem occurs for any meaning representation scheme. No matter what syntax is used to implement a representation—whether it be node and links, strings over a finite alphabet, or positive integers—the meaning of that representation is completely dependent upon what interpretation is placed on the syntax. That is, syntac-

<!-- page: 261 -->

tically identical structures may be used to represent different concepts, just as syntactically different structures may be used to represent identical concepts. If we attempt to formalize the interpretation process, we are forced to invent yet another representation which itself is subject to interpretation. Thus we can never have an absolute representation whose meaning is specified objectively.

In denotational semantics (Stoy, 1977), for example, the meaning of a computer program in one language is specified in terms of another language—a metalanguage. The meaning, however, of the metalanguage is left unspecified. Usually the metalanguage consists of abstract mathematical concepts whose meaning is assumed to be well understood. That the same problem arises for both phenomenological and conceptual representations hints at the possibility that they are really instances of the same problem. In the case of music, L. B. Meyer (1956) argues that “there is no diametric opposition, no inseparable gulf, between the affective and the intellectual responses made to music” (p. 39).

### 10.2.3 The Functionalist Approach to Semantics

If representations can have no inherent, absolute meaning, then just how do they acquire meaning? In functionalist theories, representational entities exist at the semantic level (Newell, 1982; Pylyshyn, 1984), and correspond (at least to a first approximation) to various concepts from folk psychology such as beliefs, goals, emotions, and so on. A functionalist theory of mind specifies how these representational entities causally relate to input, output, and other representational entities. Thus a representational entity alone does not have semantics. Rather, a representational data structure acquires meaning only in conjunction with processes that operate on that representation and which have a causal relation with external objects, events, or concepts of other intelligent entities, such as humans. So, for example, in the knowledge level of Newell, goals, actions, and other knowledge elements have meaning only in combination with the “principle of rationality,” which relates the knowledge elements to processes carried out in the external world.

### 10.2.4 The Elusiveness of Concepts

Finding suitable representations for concepts, however, is a very difficult task. Concepts have an elusive, open-ended quality which makes complete and consistent characterization difficult. For any representation of a concept, a counterexample may be found which demonstrates the incompleteness or inconsistency of that concept. First, there is representational incompleteness. Suppose, for example, we wish to represent the concept of buying something. We might define buying as two instances of the ATRANS primitive in conceptual dependency (Schank,

<!-- page: 262 -->

10.

UNDERPINNINGS

OF

A DAYDREAMING

THEORY

1975), which refers to abstract transfer of possession of an object from one person to another. Buying thus defined consists of an ATRANS of money from the buyer to the seller and an ATRANS of the object being sold from the seller to the buyer. But this definition is obviously incomplete. What if a credit card of the credit card from is used for payment? In this case, there is an ATRANS the buyer to the seller, an ATRANS of the card back to the buyer, an ATRANS of a pen from the seller to the buyer, an ATRANS of the charge sheet, a signing of the charge sheet, an ATRANS back of the charge sheet and pen, and so on. Then there is an ATRANS of the charge sheet from the seller to a third party, the credit card company. Then there is an ATRANS of a bill from the credit card company to the buyer, followed by an ATRANS of a check from the buyer to the credit card company, and so on. What if the transaction takes place by mail order? What if the sold item is delivered to the seller’s home by another party? But these are mere details—in each of the above cases, money and the sold object are transferred in some way, and these facts are captured by the definition. However, a more serious form of incompleteness may arise. Suppose a buyer purchases an item by check from a mail order company. Two months later, the buyer receives the cancelled check, but the item still has not arrived. At this point, the seller is obligated to deliver the item or to refund the buyer’s money. We see now that the original definition of buying is but a component of a larger picture involving higher-level notions of oblzgation and implicit contracts. Note, however, that a mere ATRANS of money to someone does not automatically invoke such obligations—if the ATRANS were accidental, such as if money were found on the ground, there is no obligation to return the money to its original

owner (this is the case of “finder’s keepers losers weepers.”) Incompleteness of representations may continue indefinitely if one tries hard enough. Of course, this phenomenon is paralleled in humans who always continue to learn more about concepts. Schank (1986, pp. 1-24) argues that understanding of concepts may be characterized along a spectrum from “making sense” at one end, to “cognitive understanding” in the middle, to “complete empathy” at the other end (the domain of present work in artificial intelligence being between “making sense” and “cognitive understanding” on this scale [p.

9]). Crook (1983) contends that attribution of consciousness to another requires empathy, analogical sympathy, and concordance. Another problem is that of representational conflict. In this situation, two representations or rules are postulated which appear correct when considered in isolation from each other. However, when those representations are incorporated into a system, it is noticed that the two representations contradict each other in a certain situation. For example, suppose there is one rule which states that “if a person is friends with another person, the former person.will help the latter person,” and another rule which states that “if a person is angry at another person, the former person will not help the latter person.” These two rules conflict when a person is angry at a friend. In order to cope with a problem

<!-- page: 263 -->

_

243

such as this, one may order the rules so that one rule has priority over another, introduce further rules to resolve conflicts, modify the conditions of the rules (e.g., “if a person is friends with and not angry at another person sh) 08 otherwise rethink and reorganize the representations. Naturally occurring concepts are very rich entities. Computer programs will not display intelligence approaching that of humans unless they contain a collection of multifarious concepts. The importance of this has been recognized by researchers in narrative comprehension engaged in the construction of “in-depth” representational schemes (Dyer, 1983a). Representations derived initially from folk psychological concepts will have to be enriched. Our view is that if sufficiently complex representational systems are built, and augmented through the incorporation of both real and daydreamed experiences, the concepts contained in that system will exhibit the same elusive, vague character.

### 10.2.5 Connectionist Approaches to Semantics

The elusive quality of concepts has led some researchers toward a holistic approach to representation known as connectionism (Rumelhart et al., 1986; Hofstadter, 1983; J. A. Feldman & Ballard, 1982; Hinton & J. A. Anderson, 1981; Grossberg, 1976; Rosenblatt, 1962). In this approach, specific nodes in a representation do not refer to particular entities of the knowledge level. Rather, semantics is distributed among the nodes of the system in a “holographic” manner. It is argued (McClelland, Rumelhart, & Hinton, p. 12) that various proposed knowledge structures—such as scripts, frames, and schemata—are in fact approximate descriptions of the “emergent” (Dennett, 1978, p. 107; Neisser, 1963, p. 194) properties of an appropriate connectionist network. The holistic connectionist approach is characterized by the assumption that high-level conceptual representations can be generated automatically starting

from low-level (or even random {[Rumelhart & Zipser, 1986]) initial representations. Thus researchers who work in this paradigm are typically not very interested in the explicit construction of higher-level knowledge structures. Not _ all connectionist models are holistic (see, for example, Waltz & Pollack, 1985; Rumelhart & Norman, 1982). However, our emphasis in this section is on holistic connectionism, since it provides an alternative to knowledge-level representations. A connectionist system consists of a set of nodes connected by directional links (Rumelhart, Hinton, & McClelland, 1986). These links are of two types— excitatory and inhibitory. Each node has an activation level and each link has a strength. Activation spreads from a node to connected nodes. The amount of activation which is spread depends on the strength of the link from one node to the other. Positive activation is spread if the link is excitatory; negative activation is spread if the link is inhibitory. Short-term information is stored in the activation levels of nodes, while long-term information is stored in link strengths. Thus a connectionist system must learn through the alteration of the

<!-- page: 264 -->

10.

UNDERPINNINGS

OF

A DAYDREAMING

THEORY

strengths of connections between nodes. Some nodes, called input and output nodes, communicate with an external environment. The remaining nodes are called hidden nodes. In 1890, William James, inspired by physiological research of his day, outlined a similar connectionist model. This model incorporated a learning rule for modification of link strengths, and the ideas of inhibition and

spread of activation (James, 1890a, p. 567). Representation in a connectionist model may be local, distributed, or a combination of the two. In a local representation scheme, single nodes represent single concepts. In a distributed scheme, each concept is spread across many nodes and furthermore each node represents many concepts. In distributed representations “it is impossible to point to a particular place where the memory for a particular item is stored” (Hinton et al., 1986, p. 80). Emergent, holistic properties are possible in both local and distributed representational schemes. It is often difficult to distinguish clearly between distributed and local representations in a holistic connectionist system (Hinton et al., 1986, p. 85). Connectionist models take their inspiration from the physiology of the brain, which is estimated (in humans) to contain tens of billions of neurons. Like the nodes and links of connectionist models, neurons (nodes) have dendrites (links to a node) and an axon (the link from a node) connected by excitatory and inhibitory synapses. Neurons are quite slow (on the order of milliseconds) compared to the electronic circuits of computers (on the order of nanoseconds). J. A. Feldman and Ballard (1982) have therefore proposed the “100-step program” constraint—for those tasks which take about a second, a program should require only about that many steps of a parallel system. Connectionist models are materialist theories of cognition which can, in fact, be simulated by a Turing machine, albeit at a very slow rate. Researchers differ in their view of the correspondence between connectionist nodes and neurons. As Smolensky (1986b) points out, there are several possibilities: Connectionist models can be given either local or distributed neural interpretations, and either local or distributed conceptual interpretations. These mappings are independent. In a local neural interpretation of a model, for example, each node corresponds to a neuron. It is possible for such a model to have a local conceptual interpretation (in which each node and neuron represents a single concept) or a distributed conceptual interpretation (in which concepts are represented by a set of nodes or neurons). In a distributed neural interpretation of a model, each node corresponds to a set of neurons. If such a model has a local conceptual interpretation, each node represents one concept which is in turn represented by many neurons. If the model has a distributed conceptual interpretation, concepts are represented by a set of nodes, each of which is represented by a set of neurons. Some of the tasks which connectionist models are able to perform are as follows (Rumelhart & Zipser, 1986, p. 161):

- Auto association. The model is trained with a set of concepts. Given part

<!-- page: 265 -->

of a concept, the system will then retrieve the remainder of that concept.

- Pattern association. The model is trained to associate concepts. one concept the system will retrieve the associated concept.

Given

- Classification. The model is trained with a set of concepts and classifications for those concepts. Given a concept similar to those already seen, the system will return the best classification for that concept.

- Regularity detection. The model discovers salient features or central tendencies in a collection of given concepts. In each case above, the system is first trained. Later, tasks are accomplished by activating appropriate input-output nodes, waiting for the network to reach a stable pattern of activity, and then reading the results from the remaining input-output nodes. Most applications of connectionist networks may be viewed as instances of one or more of the above cases. For example, the storage and retrieval of information in human memory is modeled as a form of

auto association (McClelland & Rumelhart, 1986).” In another application of connectionist models, schema acquisition is modeled as regularity detection (Rumelhart, Smolensky, McClelland, & Hinton, 1986, pp. 22-38). Although the system exhibits what might be called schemata or prototypes for rooms in a house (kitchen, living room, bathroom, and so on),® it should be noted that these schemata are not as conceptually rich as,

say, restaurant scripts (Schank & Abelson, 1977). One difficult problem with holistic connectionist representations is as follows: Since concepts are represented as a stable pattern of activation of the nodes in the network, only one concept can be represented at a time (Rumelhart et al., 1986, p. 38). That is, one cannot have multiple copies of a concept. Therefore, it is difficult to represent even simple relationships among multiple concepts (Hinton et al., 1986, pp. 82-84, 105). One solution is to have a separate copy of the network for each concept, and “gateway” networks for relating those concepts (Hinton, 1981). First, this seems a bit costly to someone who

- is used to creating a new concept by allocating a few bytes of storage. Second, and more importantly, if this approach is adopted, many of the advantages originally claimed for connectionist models are lost: instead of semantic relationships emerging from the “microstructure” of the system, the semantic relationships 7See Chapter 8 for a critical discussion of the memory model of McClelland and Rumelhart i (1986). 8Tarnopolsky (1986) independently chose a similar example involving objects in a living room and bedroom for his simple connectionist model of “spontaneous thinking.” Currently, his model is able to produce thought streams such as: There are THINGs like the PICTURE which are SMALL. The THING which is SMALL may be a PICTURE. The THINGs such as a CURTAIN and a PIC-

TURE are in the LIVING ROOM.

(p. 18)

<!-- page: 266 -->

10.

UNDERPINNINGS

OF A DAYDREAMING

THEORY

are determined by the “macrostructure” of the system of modules as designed by the programmer. One might think that a “scrambling” function could be run on the result in order to gain back the potential emergent properties lost in the modularization process. However, emergent properties occur in both local (modularized) and distributed holistic connectionism schemes, so it may not be necessary to rescramble the result. Furthermore, the fact remains that the programmer is forced to make semantic-level design decisions. It would be difficult to construct a theory of daydreaming in the holistic, connectionist paradigm as currently conceived, for several reasons: First, knowledge structures such as schemata are important in the generation of daydreaming behavior; although connectionist models have recently been able to achieve certain aspects of schemata in an emergent fashion, it is difficult then to employ those schemata in an interesting way. Second, even operations which are staples in artificial intelligence programs, such as instantiation, become a major obstacle in a connectionist scheme. Most generally, we do not see why we should limit ourselves to what a given low-level mechanism may happen (or not happen) to be able to do. It seems to be difficult to implement instantiation without abandoning the holistic paradigm. Touretzky (1986) has developed an implementation of Lisp cons cell allocation in connectionist networks; such a mechanism could be used to implement instantiation. However, it must be stressed that this is classical

instantiation, which requires that the programmer specify the representation of concepts in advance. Thus the system cannot discover its own concepts as in the holistic paradigm. At present, the only advantages of redeveloping traditional artificial intelligence tools for connectionist networks are the “blurring” and fault tolerance properties provided by those networks. If a holistic, connectionist network could be made to discover its own instantiations and relationships among concepts, that would be interesting indeed. We do not deny the importance of emergent phenomena. It is our hope, for example, that the elusive property of concepts will arise once a sufficiently complex knowledge level has been constructed. However, any approach that seeks to achieve intelligence through emergent phenomena must give careful consideration to the following problem: If intelligent behavior is finally produced by the model, will we be satisfied that we understand how that behavior is achieved? Will the model serve as an explanation of the phenomenon? Will the explanation be at a level which is of use to us? Will we be able to explain or predict phenomena of interest? For example, will the model be useful in diagnosing and treating depression? Of course, in any artificial intelligence endeavor, lack of understanding of the behavior of a running program is a potential source of difficulties. Simply because one can “name” nodes does not mean one understands the operation of the system (McDermott, 1976; Hinton et al., 1986, p. 85)—the semantics of any computer program resides, not in any single node or data structure, but rather in the processes which operate on those data structures, and how a subset of

<!-- page: 267 -->

those data structures are eventually related to the external world. Although holistic connectionism presents several difficult problems, it may in fact be fruitful to investigate semantic connectionist models—those in which knowledge-level information is communicated between active processing elements in parallel (Charniak, 1983a; Small & Rieger, 1982; Minsky, 1977, 1981; Hewitt, 1977) or in which knowledge-level information is represented by nodes and links where processing is carried out by other mechanisms (J. R. Anderson, 1983). A knowledge-level connectionist theory of daydreaming is beyond the scope of the present work; however we do consider it an interesting topic for

future research.®

## 10.3 Scientific Underpinnings

What are the scientific objectives of our research? Why do we want to understand how the mind works? According to what methodology will the construction of the theory proceed? How do we go about constructing the theory? How do we test and evaluate the theory? This section addresses these issues.

### 10.3.1 Scientific Objectives and Methodology

In constructing a computational theory of daydreaming there are two primary objectives: an understanding of human daydreaming—a scientific objective— and the application of daydreaming in order to make computer systems more useful—an engineering objective. In this section, we consider the scientific objectives of the present research (applications are discussed in Chapter 11): What constitutes an understanding of a phenomenon? What constitutes a successful theory? Our theory must satisfy the following requirements: First, it must be stated in physical terms—that is, as a computer program. This ensures that no hidden or unknown operations are required in order to apply the theory. Second, the theory must be stated at a useful level of abstraction. That is, the theory should ' provide generalizations which are useful in making predictions (Pylyshyn, 1984,

eh? ‘T): We explore how a theory may satisfy these requirements, and how understanding of a phenomenon benefits from imposing these requirements on the theory, in the context of a detailed example—that of an electronic telephone switching system. The electronic switching system is contained in a sealed box which is plugged into a power outlet and connected by wires to four telephones. Most everyone is familiar with the operation of the telephone. After a bit of thought, one may propose an initial concrete theory of the telephone switching system. For simplicity, we will not actually describe the theory as a computer program; 2We outline a preliminary version of such a theory in Chapter 11.

<!-- page: 268 -->

OF

A DAYDREAMING

THEORY

however, a demon-based implementation (Dyer, 1983a) is possible directly from our English description. The theory is as follows: First of all, the system has an internal representation of the time-varying physical state of each of its phones, including the current hook state (on-hook or off-hook), current phone number being dialed, and instantaneous air pressure level (i.e., sound) at the phone’s transmitter. The system can also influence the physical state of each of its phones in the following ways: It can make the phone ring, and it can modify the air pressure level over time at the phone’s receiver. A bit of terminology will simplify the description of the remainder of the theory: When the system begins the continual ringing of a phone, we will say that the system starts ringing the phone. While this process is being performed we will say the phone is ringing. When this process terminates we will say that the system stops ringing the phone. When the system begins the continual modification of the instantaneous air pressure level of the receiver of a phone to produce the sound of a particular tone, such as a dial tone or audible ring tone, we will say that the system gives the phone that tone. While this process is being performed we will say that the phone has the tone. When this process terminates we will say that the system removes the tone. When the system begins the continual modification of the instantaneous air pressure level of the receiver of one phone in proportion to the sensed instantaneous air pressure level of the transmitter of another phone, and vice versa, we will say that the system connects those two phones. While this process is being performed we will say the the two phones are connected. When this process terminates we will say that the system disconnects the two phones. A few constraints which the system obeys are as follows: A phone may only have one tone at a time. A phone may be connected to at most one other phone. A phone may be in, at most, one of the following conditions at any given time: It may have a tone, be connected to another phone, or be ringing. The processes which the telephone switching system performs may then be

described as follows:1°

- When the hook state of a phone changes from on-hook to off-hook, the system gives that phone a dial tone.

- When a phone number is dialed by a phone that has a dial tone, the system removes the dial tone from this calling phone, gives the phone an audible ring tone, and starts ringing the phone corresponding to the dialed number—the called phone. The system notes the calling phone associated with the called phone. 10Tt is assumed that the multiple actions necessary to handle a given phone state change are carried out as an atomic (uninterruptable) action. That is, the system finishes handling one state change before it handles the next.

<!-- page: 269 -->

- When a phone is ringing and the hook state of that called phone changes from on-hook to off-hook, the system stops ringing the phone, removes the audible ring tone from the associated calling phone, and connects the calling and called phones.

- When two phones are connected and the hook state of one of the phones changes from off-hook to on-hook, the system disconnects the two phones.

One reason for stating a theory as a computer program is to ensure the concreteness of the theory. Constructing a program forces the theory designer to consider cases that might have otherwise been overlooked. Another reason is to facilitate testing of the theory. By executing the programmed theory on a computer, one can easily try out various example situations in order to:

- Find bugs in the program/theory. Cases which were not considered when constructing the program/theory are likely to be exposed by running the program. Incomplete and inconsistent aspects of the program/theory are

discovered when the program bombs.!! These problems may then be fixed.

- Discover differences in the behavior of the program/theory and the known behavior of the actual system. This leads to appropriate revisions of the theory /program.

- Suggest experiments to be carried out on the actual system. When a certain behavior is produced by the program in some situation, and the theory designer does not know how the actual system behaves in that situation, an experiment may be conducted in order to find out. If the prediction of the theory holds in reality, confidence in the theory is strengthened. Otherwise, the theory must be revised.

Our initial theory of the telephone switching system may have several faults: It may be internally inconsistent or incomplete, and it may not correspond to the actual behavior of the system. However, now that we have an explicit theory, we can begin testing the theory. We consider various possible sequences of hook states and dialed numbers. If problems are discovered, the theory will be modified in order to fix those problems. What if a number is dialed by a phone when that phone is connected to another phone? What happens in the program? Nothing. This behavior is consistent with the known behavior of the telephone switching system. What if the hook state of the phone associated with a ringing phone changes from offhook to on-hook? That is, what if there is no answer and the caller hangs up? The program does nothing. In this case, the behavior is inconsistent with the 11The branch of computer science sometimes known as verification is concerned with discovering such problems in a program automatically or semiautomatically. However, these techniques are not yet able to cope with programs as intricate as most artificial intelligence programs. See the criticisms of DeMillo, Lipton, and Perlis (1978) of this approach.

<!-- page: 270 -->

10.

UNDERPINNINGS

OF

A DAYDREAMING

THEORY

known behavior of the telephone switching system. Therefore, the program must be appropriately modified: In this situation, the system should stop ringing the called phone and remove the audible ring tone from the calling phone. What if a phone which has a dial tone goes on-hook before a number is dialed? The program must be modified to disconnect the dial tone. What if a dialed phone number corresponds to a phone that currently has a tone, is connected to another phone, or is ringing? That is, what if the called phone is busy? The program in this case will violate the constraint that a phone may not be ringing and connected to a phone at the same time—the program will bomb. Thus the program must be modified to check whether the called phone is busy, and if so, to give the calling phone a busy signal (and to remove the busy signal when the calling phone goes on-hook). Once the above modification has been performed, a particular special case of busy phones may be considered: What if a dialed phone number corresponds to the very same phone that dialed that number? That is, what happens when a phone calls itself? The theory predicts that the phone will be given a busy signal. In this case, however, one may not know what the actual behavior of the telephone switching system is. Consequently, an experiment must be carried out on the actual system. If a busy signal is in fact given to the calling phone, this prediction of the theory is shown to be correct. Otherwise, the theory must

be appropriately modified.}” Another case involving busy signals might be considered: If the caller hangs on the line after receiving a busy signal, will the system remove the busy signal, give the caller an audible ring tone, and start ringing the called phone when the called phone eventually becomes free? The theory predicts that this is not the case—the busy signal will remain until the caller hangs up. This turns out to be true when tried out on the actual system. Other situations cause the program to bomb: When a ringing phone is picked up, the system also gives that phone a dial tone, violating the constraint that a phone may not have a tone and be connected to another phone at the same tine. The program must be fixed to give a phone a dial tone only if it is not ringing. What happens if an unassigned number is dialed? The program again bombs in this case. The program must be fixed to give the calling phone a recording. Still other predictions of the theory may be found to be incorrect: For example, suppose when two phones are connected, one phone goes on-hook for an instant and then goes off-hook again. The program would disconnect the two phones, while most real telephone systems keep the phones connected. Instead of simply asserting that the telephone switching system somehow lets one “dial the number of a person and then talk to that person,” we constructed 12The behavior of actual phone systems varies: Some give a busy signal and some give a recording. Still others, such as my old local exchange in Brentwood, California (a Western Electric No. 1A Electronic Switching System), give a sequence of clicks; when one then hangs up, the phone rings.

<!-- page: 271 -->

an explicit, computational theory of the system. In the process, we discovered many unanticipated complexities. We were forced to ask and answer questions that we might have otherwise neglected. Although there are probably other problems with our theory of the telephone switching system, it now comes quite close to reality. The theory may be used to understand, explain, and predict the operation of the system. The theory presented above is a high-level theory (in terms of abstract states and processes) of the telephone switching system. This theory was constructed based on the external behavior of the system. Suppose, now, that we are allowed to take apart the sealed box containing the switching system. Now we can examine and catalog each and every electronic component and connection in that system—we can produce a complete low-level description of the system in terms of an electronic circuit diagram. The high-level theory may be related to the low-level description via a certain transformational sequence: If the circuit incorporates a traditional microprocessor and memory, the transformations are those of a compiler which converts the high-level language program into a machine language program for the microprocessor. Otherwise, the transformations must convert the high-level language program directly into a hard-wired circuit (see, for example, the proposal of

Mostow, 1983). The two levels have their own advantages and disadvantages. The lowlevel description is useful if we wish to construct another identical electronic switching system. However, constructing a modified version of the system would be extremely difficult, since one has only a low-level electronic description of the circuit and not a more abstract understanding of the functional modules or programs contained in the circuit (a more abstract understanding would in fact be a high-level theory). In contrast, the high-level theory facilitates modification of the system, provided that one has a way to implement that high-level theory. A high-level theory also has the advantage that it can be implemented in any medium for computation. The high-level theory is more useful for explanation. Suppose we are given the question, Why does a phone get a busy signal when the number of that same phone is dialed? Our high-level theory provides the following answer: Whenever one calls a number corresponding to a phone that is in use, one receives a busy signal. It does not matter if that phone happens to be the same phone one is calling from. The need for a busy signal derives from the constraint that a phone may not be connected to two things at once. In constrast, the low-level description (for a hard-wired logic circuit) would only give us an explanation something like: “Phone 4 is given a busy signal when phone 4 is dialed because when input lines 11 and 12 are both high, and pass through AND gate 62, the resulting high value is ANDed with input line 4 by gate 112, producing a high value which is sent to OR gate 19, resulting in a high value which is ANDed by gate 442 with a high value from flip-flop 45, producing a high value which is ANDed by gate 32 with a high value from AND gate 48 whose inputs are the

<!-- page: 272 -->

10.

UNDERPINNINGS

OF A DAYDREAMING

THEORY

outputs of flip-flops 12 and 13, resulting in a high value which sets flip-flop 21, whose high output is connected to driver 24, whose 12 volt output is in turn attached to relay 24 that connects phone 4 to the busy tone generator.” The high-level theory is also more useful for making predictions. For example, suppose one wishes to know what happens if two parties place a call to each other at the same time. Our high-level theory states that a busy signal is given to the caller whenever the called phone has a tone, is connected to another phone, or is ringing. Since both parties have a dial tone while dialing the other party, the answer is that both parties receive a busy signal. It is more difficult to answer this question using the low-level description: One would have to specify the appropriate time-varying input values (corresponding to the hook state changes and dialed numbers of the two phones of interest), simulate the operation of the circuit for those inputs, and then interpret the resulting output levels (which determine what connections the system makes). The simulation would then show that the system gives the two phones a busy signal. However, what is true of two particular phones may not be true of all pairs of phones. Therefore, the simulation would have to be carried out for every possible pair of phones. Furthermore, in general, the simulations would have to be carried out for every possible initial system and input state, and every possible sequence of state changes of the remaining inputs—those not associated with the two phones of interest. This adds up to an awful lot of simulations—one is probably better off just trying out the various cases on the actual system. The purpose of a theory is to provide generalizations which are useful in explaining and predicting the behavior of the system. The high-level theory provides relevant generalizations, whereas the low-level description does not. Of course, there may be certain phenomena which the high-level theory does not address, but which the low-level description does. For example, suppose one bit of the memory cell that holds the state information for a given phone is damaged. This may result in behavior that is difficult to characterize in terms of the high-level theory. Thus some information is lost in the transformation from the low-level description to the high-level theory. But the advantage of the high-level theory is that it provides generalizations which are useful in predicting most behaviors. Of course, the ultimate understanding may consist of high- and low-level theories, and a mapping between the two levels. Indeed, one may feel that one understands a system best if one understands the operation of the system at a high level (its logical specification), the operation of the system at a low level (its physical implementation), and the connection between the two. The case of human cognition, however, is more difficult. Constructing a complete low-level account of cognition faces the following obstacle: We cannot take apart the brain in order to analyze its operation the way we can take apart and analyze the electronic telephone switching system. It would destroy the pattern of interest to us. That is, there is no way to, say, insert a sensor at every neuron and synapse in order to gain a complete picture of the operation of the brain.

<!-- page: 273 -->

Using partial data or even complete data (if, perhaps, a way of scanning the brain with sufficient time and space resolution were somehow eventually realized), a low-level theory may be developed. However, in order for a lowlevel account to be useful, the constructs of the lower level must be related to the more familiar high-level constructs—this was demonstrated even in the simple case of the telephone switching system. In order for a low-level account of cognition to succeed as an explanatory theory, high-level abstractions will still have to be developed. The transformations which map the high-level theory of the telephone switching system to the low-level description of that same system were seen to be fairly difficult. The transformations involved in mapping a knowledgelevel theory to a neuroscientific theory are likely to be orders of magnitude more complex than this. (However, if materialism is true, there must be some mapping.) We emphasize a top-down approach toward the explanation of human behavior, while the neurosciences and connectionism emphasize a bottom-up approach. Using either approach, the task is extremely difficult. We start with concepts from folk psychology, such as emotions, goals, and beliefs, and processes which operate on these concepts. These concepts and processes are continually refined to provide progressively better accounts of human behavior. We hope that at each incremental modification of the theory, a useful level of explanation can be retained. The neurosciences and connectionism, however, attempt to bootstrap human behavior starting from the structure of the human brain and neurons or neuron-like units. But in the connectionist paradigm, for example, when a certain high-level behavior is produced, it is not always clear how that behavior is actually generated. Saying that the schema is an “emergent property” is not enough. How exactly does the schema emerge and why? Perhaps in the end neither approach will succeed at providing an explanation, or perhaps one will and the other will not. Probably, as Churchland (1984) points out, the end result will lie somewhere between a folk psychological explanation and a neuroscientific account. It will most likely involve the construction and conceptualization by humans of an entirely new, as yet unanticipated taxonomy—what may become the folk psychology of future generations. Perhaps one or both of the approaches will succeed in achieving truly intelligent behavior without retaining a useful explanation of that behavior. Even this, of course, would be a significant accomplishment.

### 10.3.2 Goals and Limits of Artificial Intelligence

Why attempt to understand the mind? There are those who would argue that explaining human intelligence is either impossible or, if possible, a misguided endeavor: By succeeding at a complete mechanistic explanation of the mind, all that is most unique and valued about our own humanity would be eliminated. First of all, such complete demystification is not probable in the near (or

<!-- page: 274 -->

10.

UNDERPINNINGS

OF

A DAYDREAMING

THEORY

even not-so-near) future. Furthermore, by pursuing the enterprise of artificial intelligence, our fascination with human intelligence and creativity will only be strengthened. Because of the near intractability of human creativity, artificial intelligence researchers will have no difficulty finding interesting problems to look at for plenty of years to come. Artificial intelligence can be thought of as another creative human endeavor—one which presents a difficult problem to solve, a fun exercise, a new approach to discovering the many ways in which humans are so amazing. What are the limits of artificial intelligence? Will our efforts, if continued over many years, eventually converge upon a complete theory of human intelligence? Will we reach a true thinking machine? Might we reach a limit point after which no more progress could be made? If functionalism is true, not only is it possible for a theory of intelligence to be constructed in terms of a computer program, but, in principle, a computer program could actually be intelligent. Obviously, DAYDREAMER is a long way from having a true, human stream of consciousness. Computer programs are not likely to approach the level of human intelligence for many years to come, since the processing mechanisms, knowledge, and experiences which humans have are so much more complex and rich than those which so far have been put into computer programs. Perhaps materialism is true and there will come a time when a fully human, intelligent computer program will be developed. However, even if materialism is true, we may not necessarily be able to construct such a program. It may be logically impossible for the mind to understand itself, since, as Johnson-Laird

(1983) states the paradox, perhaps “the mind must be more complicated than any theory proposed to explain it: the more complex the theory, the still more complex the mind that thought of it in the first place” (p. 1). There are several responses to this problem. Perhaps such a theory, while not constructible by a single person, may be achieved by many persons working together. Perhaps such a theory, while not understood by any single person, may be understood by a collection of persons. Sometimes it is argued that a mind understanding itself is impossible on the grounds that infinite storage would be required: If a complete model of the human mind were contained in a human mind, then this model would have to contain yet another complete model, which in turn would have to contain

another complete model, and so on ad infinitum.!> However, through econom-

ical processing and representation of recursion, this problem may be avoided: Instead of having a separate copy of the model embedded within each copy of the model, the top-level model need only refer to itself recursively and for recursive processes to be carried out on a demand basis; how to accomplish this is suggested by such programming language constructs as pointers and lazy evalu13But, as K. M. Colby (personal communication, February, 1987) points out, if the mind is infinite, it could contain itself as a subset (just as the integers contain the even integers as an isomorphic subgroup).

<!-- page: 275 -->

ation (Sussman & Steele, 1975). As Minsky (1968) suggests (but using different terminology), if an interpreter for a programming language is written in that same language (assuming, of course, that the interpreter is eventually executed by another hardware or software interpreter for that language with equivalent behavior), the interpreter can be run on itself to see how it would operate on an arbitrary program in that language (which itself could be the interpreter operating on yet another program in that language, and so on). There is no problem with infinite storage since there need only be one copy of the interpreter. In addition, several computer programs which can be said to introspect about their own operation—in a way more interesting than simple self-interpretation—have

been constructed (B. Smith, 1982; Doyle, 1980). Even if it were not logically impossible for the mind to understand itself, the mind might still turn out to be too complex for a person or group of people (even aided by partially intelligent computers!) to figure out. Perhaps the experimental methods which are available for examining the mind will prove insufficient. Physiological mechanisms interact with psychological mechanisms and contribute much to the experience of being human (see, for example, Bloch, 1985). Even if materialism is true, a truly human artificial intelligence (if we wanted to construct one) might have to be embedded within a biological system (or within a simulation of a biological system). Programs that seem human will be constructed way before those programs actually come close to achieving a level of intelligence comparable to that of humans. Indeed, the programs PARRY (Colby, 1973, 1975, 1981) and ELIZA (Weizenbaum, 1966) have already fooled some people, that is, passed weak versions of the Turing test (discussed in the following section). As our computer programs become more intelligent, we might find humans becoming even more clever in order to demonstrate their superiority over their mechanical counterparts. As R. E. Mueller (1963) puts it: Perhaps the human mind has only been creating in low gear up to row. If a computer was invented that outinvented man, imagine

how inventively man would try to outinvent the computer! (p. 152) Materialism may turn out to be false—the phenomenological aspect of consciousness may turn out to be inexplicable in purely physical terms. Perhaps after trying so hard to design a computer program with artificial consciousness, and achieving a device with equivalent behavior to a human with true consciousness, we may find out that true phenomenological experiences are of a nonphysical nature. Of course, if the behavior of the artificial system is identical to the natural one, this might not make any difference to us. Another possibility is that the behavior of the program will not be equivalent to human behavior in very subtle ways—ways that depend on the true nature of mental phenomena. For example, there might be something “not quite right” about the program’s descriptions of its internal experiences.

<!-- page: 276 -->

10.

UNDERPINNINGS OF

A DAYDREAMING

THEORY

As we construct closer and closer computer approximations to human intelligence, and as we run into difficulties, it may be very difficult to pinpoint the cause of these difficulties: We are not smart enough, the systems lack physiology, materialism is false, and so on. Even if it eventually turns out that materialism is false—that a full computational account of intelligence is not possible—we will have gained insight into those aspects of human cognition which are characterizable in these terms. We will also have better knowledge of which aspects of intelligence are capturable and which are not. If it were to turn out that no part of intelligence is capturable in computational terms, then we still will have gained insight into artificial intelligence. Whether or not all of our theoretical assumptions (such as materialism) turn out to be true, and whether or not we ever converge upon a complete explanation of human behavior, we will have uncovered a whole range of previously uninvestigated human cognitive phenomena and previously unasked questions. We will thus have learned much about human thought processes.

### 10.3.3 Theory Construction, Testing, and Evaluation

What is the methodology for the construction of our computational theory of daydreaming? How is such a theory tested? How do we evaluate such a theory? What constitutes a successful theory? In what senses might the processes posited by the theory be considered equivalent to human mental processes? Theory construction begins by examining one or more human daydream protocols. An initial theory is devised and implemented as a computer program which is able to exhibit the behavior of these protocols and other, similar, protocols. The theory is not developed in a vacuum. Rather, previous related theories from psychology, artificial intelligence, and philosophy are studied. Experiments from the psychological literature are consulted in choosing among alternative explanations of the same behavior. Because our objective is an explanatory theory of daydreaming at an appropriate level of abstraction, the theory is stated in terms of knowledge-level representations which correspond roughly to folk psychological notions such as emotions, goals, beliefs, attitudes, and so on. A rudimentary test of the theory occurs in its implementation as a computer program. At the very least, the fact that the theory can be implemented demonstrates that the theory is physically possible. Moreover, it ensures that the theory does not depend on any hidden or mysterious mechanisms. The theory is next tested by running the computer program. The program is presented with various input states and actions in performance mode, and the resulting external and daydreaming behaviors are observed. This exposes various problems—the program may crash or it may otherwise not exhibit the desired output behavior. Modifications are continually made to the initial theory and program in response to problems discovered from running the program. In order to increase the generality of the theory and program, additional hu-

<!-- page: 277 -->

man daydream protocols are considered and incorporated into the theory and program. Since everything must be made concrete in a computer program, there are certain aspects of the program which might not be considered a part of the

theory. As Colby (1981) writes: All the statements of a theory are intended to be taken as true, or as candidates for truth, whereas some statements in the programming language in the model, features necessary to get the model to run, are intended to be read as false, or at least of uncertain truth value.

(p. 516) That is, since a given theory must concentrate on some aspects of intelligence while overlooking others, those parts of the program which deal with issues not addressed by the theory must either accept input from a human, or be implemented in a simplistic fashion (analogous to the routines called “stubs” in software engineering [Kernighan & Plauger, 1976]). Indeed, one of the advantages of implementing a theory as a computer program is the discovery of such loose ends. At first, one may devise a quick, specific solution or “kludge” to enable the program to handle such a situation. However, upon closer examination of the situation, substantial theoretical issues may be revealed. One may then update the theory and program in order to handle that situation (and others) in a more general fashion. Alternatively, that situation may justifiably be deemed beyond the scope of the theory. A theory is best developed in conjunction with the writing of the program. Once the behavior of the program has achieved rough face validity—that is, the output more or less resembles the modeled daydreaming behavior—more

exacting tests may be employed: Turing (1950) proposed the “imitation game” as a test for computer intelligence: Suppose there are three persons—a woman, a man, and an interrogator of either sex. The interrogator is in a separate room from the woman and man, and communicates with them via a teletype connection. The task of the interrogator is to determine which of the two persons is the woman and which is the man. The man attempts to fool the interrogator into thinking he is the woman, and the woman attempts to convince the interrogator that she is the woman. Suppose now that a computer takes the place of the man. Turing contends that the computer may be considered intelligent if it is able to fool the interrogator as often as a man is able to. What is often called the Turing test is a modified version of this game: If an interrogator is allowed to converse freely with a computer program, and after some long length of time the interrogator is fooled into thinking the program is a human, that computer program is said to have passed the Turing test. The Turing test therefore ascribes importance to the input-output behavior of the human or computer program (rather than to other attributes). A Turing-like test may be used to evaluate a program which models some aspect of human intelligence: If the output of the program cannot be distinguished from the output of a human

<!-- page: 278 -->

10.

UNDERPINNINGS

OF

A DAYDREAMING

THEORY

engaged in the activity modeled by that program, the program passes the test. Other authors (Hofstadter & Dennett, 1981; Colby, 1981; Heiser, Colby, Faught, & Parkison, 1980) treat Turing-like tests in greater detail. How might a Turing-like test be applied to DAYDREAMER? One might argue that such a test is insufficient because daydreaming is an output-less activity—any program could pass such a test for daydreaming! As Weizenbaum (1974) pointed out in a letter to the Communications of the ACM, a program which responds to each input of an interrogator in the same way as an autistic patient—that is, not an iota—hardly constitutes an explanation of autism. However, daydreaming as defined here—as a verbal report of the stream of consciousness—is not an output-less activity. The daydreams produced by the current DAYDREAMER resemble those of a human daydreamer to a large degree; however, we suspect that the output of the program can be distinguished from human daydream protocols. This is due in part to the current English generator—perhaps if daydreams generated by a human and daydreams generated by the program were both edited for style by a human, the resulting daydreams would be indistinguishable. However, we have not yet attempted such an experiment.

### 10.3.4 Pylyshyn’s Strong Equivalence

Pylyshyn (1984) argues that if a computer program is to be taken literally as a model of human behavior, the program must generate behavior in the same way as a human. In other words, he argues that a Turing-like test is too weak a criterion for an explanatory theory—rather, up to some level of granularity, the processes carried out by the program and by the human should be equivalent. He proposes the notion of “strong equivalence.” Informally, two programs are strongly equivalent if they carry out the “same” algorithm. If two programs carry out the “same” algorithm, it should be possible to run both programs directly on the same functional architecture: Consider the random access machine, which has an infinite number of memory locations, accessible in a single step by address. Although Turing machines have the same computational power (are able to compute the same functions) as random access machines, there are certain algorithms—such as hashing—which can be directly executed on a random access machine, but not on a Turing machine. In order to run a hashing algorithm on a Turing machine, random access memory would have to be simulated. This can be accomplished by using the Turing machine’s tape to store the contents of memory and accessing locations by scanning the tape until the desired location is reached. But in the process of simulation, the computational complexity of the program (measure of space and time necessary for a computation as a function of the input) is increased—on a Turing machine the number of steps to retrieve an item is dependent upon the total number of stored items, whereas on a random access machine the number of steps is independent of the number of items.

<!-- page: 279 -->

Consequently, complexity is a way of defining what is meant by direct execution of a program on a given functional architecture—if the basic operations of a program are not realized by the functional architecture in a number of steps independent of the input, then, by definition, the program is not executed directly by that architecture. A program which employs random access as a basic operation may therefore not be executed directly on a Turing machine. Strong equivalence of two processes requires that both processes run directly on the same functional architecture. Of course, a major task in constructing a cognitive model is that of finding the unknown functional architecture of cognition. The use of complexity measures and reaction-time experiments using humans may assist in this process. The strongest requirement for equivalence might be phenomenological equivalence—that the behavior be experienced subjectively in the same way. We have already discussed the paradoxes involved in such a notion. There are several problems with Pylyshyn’s arguments: First, he argues that Turing (input-output) equivalence is too weak and proposes the notion of “strong equivalence” which makes use of complexity equivalence and reaction times. But Turing equivalence does not necessarily free a theory from accounting for reaction times: For example, reaction times may be reflected in verbal behavior simply by requesting that a subject count out loud while not verbalizing, or by reading elapsed time off a stopwatch before each verbalization. Even if we do not require reaction-time equivalence, there are many important features of the functional architecture which can be correct without satisfying the criteria for strong equivalence. There are significant aspects of the functional architecture serving to determine behavior which can implemented using different algorithms. For example, failing (rather than delaying) at recalling something because it has not recently been primed is typically explained at the level of functional architecture (e.g., in terms of a spreading activation mechanism). And yet, this mechanism could be implemented using very different algorithms

(for example, one on a Turing machine, one on a random access machine) not satisfying complexity equivalence. Thus even weak Turing equivalence is really not so weak. Second, Pylyshyn appears to require that primitive operations of the functional architecture have a time complexity independent of input. A problem with this is that the primitives of most cognitive science architectures, present and future, hardly have this property: Spreading activation, for example, might have a time complexity dependent upon the lengths of connections in memory. There is no a priori reason for the functional architecture of cognition not to be implemented using various levels of interpretation. Perhaps there is theoretical justification for this view; however, Pylyshyn does not address the basic problem that functional architectures with the same primitive operations can be, and often are, implemented in radically different physical systems with resulting radically different complexity properties. For example, a random access machine can be implemented directly in hardware, or it can be implemented

<!-- page: 280 -->

OF

A DAYDREAMING

THEORY

in microcode on a very fast Turing machine. If this requirement for primitive operations is enforced, then it is likely that the primitives of the functional architecture will not be very complex. When the primitives are thus constrained, all the ways of implementing them at the physical level will probably turn out to be the same algorithm. Thus this requirement has the side effect that two strongly equivalent programs will not simply be equivalent up to the level of the functional architecture, but since the functional architectures on which the programs are based are also implemented in the same way, the two programs will be equivalent all the way down to the physical level. This dilutes the notion of strong equivalence as an “abstract sameness” of algorithms only up to the symbol level. The problem is probably that degrees of “sameness” cannot be quantized. If, on the other hand, primitive operations are not required to have time complexity independent of input, then reaction time experiments no longer constrain the functional architecture; instead, they constrain the entire implementation down to the physical level. This destroys the utility of reaction time tests for strong equivalence, which is only equivalence up to the level of functional

architecture. However, reaction times could be used to test for a “stronger” equivalence requiring that the two programs execute the same algorithm all the way down to the physical level. Thus we have argued that either strong equivalence, as defined, ends up being all the way down to the physical level, or reaction times are useless in assessing strong equivalence. Third, Pylyshyn is not clear on the issues of: (a) how complexity equivalence serves to locate the symbol-semantics boundary (although it is clear how the criterion of cognitive penetrability accomplishes this, even if it is just a restatement of the definition of the symbol-semantics distinction [see also Johnson-Laird, 1983, p. 152]), (b) how finding an appropriate symbol-semantics boundary (via any method) implies there is strong equivalence, (c) whether the program which runs on the functional architecture is identical to the semantic level (although the answer to this is most probably yes, it is far from clear, and there are problems: it appears that a semantic level program could not contribute complexity effects, because any such effects would be the result of cognitively impenetrable operations; yet paradoxically, strong equivalence is defined as equivalence up to, but not including, the functional level; that is, the programs which run on the

functional level [semantical-level programs] must be equivalent; but this then

means that complexity equivalence is meaningless as a test for strong equiva-

lence), and (d) how the cognitive penetrability criterion ensures that the division of levels in the program corresponds to the division of levels in the person (this might be the case if the program has already been shown to be weakly equivalent to the person; however, if the program is weakly equivalent to the human, the levels are already correct).

<!-- page: 281 -->

Discussion

In the body of this book, we have specified our theory in English, with the major propositions typeset in italics. A detailed description of the DAYDREAMER computer program itself is given in Chapter 7. Our theory of daydreaming represents one possible way in which daydreaming behavior may be produced. How might we evaluate whether and to what degree this behavior is achieved in the same way in humans? Both the major propositions of the theory and the detailed algorithms may be tested empirically. Although conducting appropriate psychological experiments is beyond the scope and resources of the present work, it will be useful to conduct such experiments in the future. From one or more propositions of the theory, further consequences or side effects of the theory may be inferred. Nonobvious implications of the theory may also be discovered through observation of the behavior of the program— some consequences arise only when the dynamic behavior of a theory is explored in a running program. If predictions generated by the theory and program are validated experimentally, confidence in the theory is reinforced. This is the case whether or not the experimental evidence existed previously—provided that this evidence was not consulted in constructing the theory. Checking a theory against the data from which it was derived hardly serves to validate that theory (although it may serve as a check that we have not lost sight of the data). Rather, a theory must be tested through independent and indirect means. Whenever aspects of the theory have been derived from existing experimental evidence, relevant literature is cited in the text. If existing evidence for a given proposition of the theory is sufficiently conclusive, there is no need to test that proposition further. It may, however, be necessary to test that proposition in interaction with other propositions of the theory. In addition, there are potential problems in validating a theory through its ability to explain (new) behaviors after the fact—rather than predicting

behaviors before the fact. Rapaport (1960, p. 18, footnote) gives suggestions on the proper handling of such “postdiction” by a theory: It is necessary to make explicit what information is given in the behavioral data and what information can only be inferred (in a nonobvious manner) by postdiction from the given information. Predictions regarding computational complexity may be generated by the theory and program. One example is the algorithm for serendipity which is presented in Chapter 5. The way the program carries out a given task might then be shown to be “strongly equivalent” to how humans carry out a similar task—this would be accomplished by measuring the reaction times of humans in a certain set of situations and comparing these times with the reaction times of the program in those situations. However, if a prediction generated by the program turns out to be incorrect— sometimes this is obvious, as when the program crashes—there are two possi-

<!-- page: 282 -->

10.

UNDERPINNINGS

OF A DAYDREAMING

THEORY

bilities: Either the program does not correspond to the theory, or the program does correspond to the theory and the theory is (at least in part) incorrect. At this point, one must examine the theory and program and devise appropriate modifications. Testing the program and theory is thus important not only to validate the program and theory, but to suggest improvements in the program and theory. Another means of evaluating DAYDREAMER is in terms of the proposed functions of daydreaming discussed in Chapter 1. In previous chapters, we have already discussed how the eos achieves the functions of learning and creative problem solving. DAYDREAMER as presented in this book is the result of many revisions (E. T. Mueller & Dyer, 1985a; E. T. Mueller & Dyer, 1985b). The behavior of the program resembles that of a human daydreamer—to what degree remains a question for future research. DAYDREAMER appears to account for much of the daydreaming behavior of humans, although a number of aspects of the theory are still to be tested in the psychological laboratory. Most likely, DAYDREAMER will succeed at some future tests and fail at others. Considering the magnitude of the problem, we consider it to be an achievement simply to have constructed a plausible computational theory of daydream production. Although this theory may not represent the way that daydreams are generated, it represents one possible way that daydreams may be produced—for now this must suffice since, to our knowledge, no alternative computational theories of daydreaming have yet been proposed. Thus DAYDREAMER is really a point of departure for future theories of daydreaming. We expect this theory to be continually refined in the years to come.
