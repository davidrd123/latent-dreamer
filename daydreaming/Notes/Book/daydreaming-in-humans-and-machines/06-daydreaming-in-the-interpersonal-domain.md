# Chapter 6: Daydreaming in the Interpersonal Domain

<!-- page: 171 -->

In order to daydream in a certain content area, DAYDREAMER must have an in-depth representation of that area. People daydream about a variety of things from everyday concerns to technical matters to unlikely science-fiction-like adventures. Some of the more common topics of daydreaming are human relationships, sexual activities, good fortune, career achievement, heroic feats, hostility,

and guilt (J. L. Singer & Antrobus, 1972). We have singled out the natural and frequent daydream topics of interpersonal relations (J. L. Singer & McCraven, 1961)+ and employment for detailed representation in DAYDREAMER.

Dyer (1983a) and Schank and Abelson (1977) have previously addressed the representation of interpersonal relationships (such as friends, lovers, and so on) for use in story understanding. Although the representations for understanding and planning overlap to some degree (Wilensky, 1983), the emphasis is different: Previous research in story understanding has been concerned with tracking existing relationships between story characters in order to generate expectations for connecting up the events of a story. In contrast, we address how an individual takes actions in order to initiate, maintain, and terminate relationships

with others. We also provide an implementation of Heider’s (1958) analysis of attitudes—positive or negative evaluative judgments toward a person, object, or idea—which are important in interpersonal relations. In this chapter, we first review the representation of simple states, mental states, and actions. Second, we discuss extensions to the basic planner required for planning in the interpersonal domain. Third, we discuss the representation 1 Although the domain of interpersonal relations might be taken to include erotic fantasies, which are a common aspect of daydreaming (Stoller, 1979; Hariton & J. L. Singer, 1974; Friday, 1973; J. L. Singer & Antrobus, 1972), this level of representation is beyond the scope of the present work.

<!-- page: 172 -->

152 CHAPTER 6. DAYDREAMING

of attitudes.

IN THE INTERPERSONAL DOMAIN

Fourth, we discuss the representation of interpersonal relation-

ships. Fifth, we present some strategies for generating fanciful scenarios in the interpersonal domain. It should be pointed out that the representation elaborated here presents a somewhat simplified and idealized view; this is also true of other current artificial intelligence representations of natural phenomena. Some readers may find our representations too stereotypical to capture the richness of what is probably the— most complicated, varied, pleasurable, and difficult area of human experience: human relationships.? However, the purpose of constructing representations is in fact to capture certain generalizations—stereotypes—which humans have, both in order to construct theories of human cognition and in order to construct intelligent computer programs. The representations provided here are a first approximation to these stereotypes.

## 6.1 Basic States and Actions The basic representation in DAYDREAMER

for states and actions in the world

is based on Schank’s (1975; Schank & Abelson, 1977) conceptual dependency (CD) representation. In this section, we review CD and present extensions to CD employed in DAYDREAMER.

### 6.1.1 Conceptual Dependency Representation

CD was originally developed to enable computer programs

meanings of English sentences (Schank, 1975).

to represent the

There are two types of CD

conceptualizations (often referred to simply as CDs): actions and states. An action CD consists of a primitive action, an actor (the animal performing the action), an object (the animal or physical object on which the action is being performed), a direction of the action (including origin and destination), and an optional instrument for the action. A state CD consists of a state, a current value of that state, and an object (which is in the given state with the given value). CD conceptualizations are connected via causal links (dependencies). Some primitive actions of CD (Schank & Abelson, 1977) are:

ATRANS The abstract transfer of possession, by an animal, of a physical object from one animal to another; an abstraction of giving, taking, buying, selling, and so on. MTRANS The mental transfer of information, by an animal, from an animal or physical object to an animal; an abstraction of talking, reading, gesturing, and so on.?Quinn (1981) presents some alternatives to the goal subsumption model of relationships (Schank & Abelson, 1977) adopted here.

<!-- page: 173 -->

The physical transfer from one location to another, by an animal or physical object, of an animal or physical object; an abstraction of walking, driving a car, riding a bicycle, and so on.

### 6.1.2 Conceptual Dependency in DAYDREAMER DAYDREAMER employs a modified conceptual dependency representation for

states and actions. The functional behavior of representations in the program is specified by planning and inference rules such as the following: IF THEN

ACTIVE-GOAL ACTIVE-GOAL

for person to be AT location for person to PTRANS to location

This rule states that in order to achieve the state of being AT a particular location, one employs the PTRANS action to go to that location; conversely, if one PTRANSes from one location to another, one is then AT the new location

and no longer AT the original location. follows: IF THEN

ACTIVE-GOAL ACTIVE-GOAL

PTRANS

is further broken down as

for person to PTRANS to location for person to KNOW location

That is, in order to PTRANS to a particular location, one must KNOW where that location is. In DAYDREAMER, mental states of persons are called beliefs. A belief consists of an actor—the person whose mental state is described, and an object— the mental state. The mental state may itself be the mental state of another person—yet another belief. One typical use of beliefs is the representation of mental states of others which refer to the mental states of the self—such as Harrison Ford believing that DAYDREAMER has the goal to go out with him. Most often, beliefs are used to represent the attitudes and goals of others.

## 6.2 Other Planning

Daydreaming in the interpersonal domain often involves other planning— monitoring and attempting to modify the mental states (attitudes, goals, and so on) of other persons. We distinguish between forward and backward other planning. Forward other planning involves running the planning mechanism from the viewpoint of another person. This enables the program to determine in a given situation what inferences that person might make, what beliefs that person might form, what goals that person might initiate, what actions that person 3A belief of another person is actually a belief of DAYDREAMER about the belief of another person. However, since all representations in the program are assumed to be beliefs of the daydreamer, this extra level is ignored in the discussion for simplicity.

<!-- page: 174 -->

154 CHAPTER

6. DAYDREAMING

IN THE INTERPERSONAL

DOMAIN

might take on behalf of those goals, and so on. That is, forward other planning provides a simulation of the behavior of another person. For example, when

DAYDREAMER imagines she is going out with Harrison Ford in RATIONALIZATION1, DAYDREAMER predicts that the movie star will have the goal to work and thus will travel to Egypt to shoot a film. Backward other planning involves attempting to achieve some mental state in another person. For example, in LOVERS1, DAYDREAMER has a goal to go out with the movie star; in order to achieve this goal she must plan for the star to have the goal to go out with her. In order to perform (both forward and backward) other planning, DAYDREAMER must have a model of the inference rules, planning rules, and initial mental states of other persons. The general assumption is made that the behavior of others is similar to the behavior of DAYDREAMER. This enables an economical representation of rules for both personal and other planning. However, rules may be added and deleted from this basic set for particular persons or classes of persons. Thus, for example, movie stars may have a different set of goals than DAYDREAMER. The mental states of other persons are maintained in the same planning contexts as those containing self mental states; any mental state of another person

is marked as a belief of that person. In order to perform forward other planning for a person, the planning algorithm described in Chapter 7 is employed. How-

ever, this algorithm (a) only retrieves beliefs of the other person and (b) marks as beliefs of the person any newly asserted subgoals or facts. The algorithm is sufficiently general to handle the simulation by another person of another

person (including the self), the simulation by another person of the simulation by yet another person of yet another person, and so on. However, such nested planning is avoided in DAYDREAMER since it rapidly leads to a deep regress. Backward other planning is employed in the following situation: Suppose a goal exists whose objective is some mental state of another person. The program has two ways of achieving this goal: Either it can employ a rule stated in terms of another person having the given mental state, or it can employ a rule stated in terms of the self having the given mental state. The former is attempted first, and then the latter is attempted. In the latter case—backward other planning—the rule is applied as if the other person were the self.

## 6.3 Attitudes

An attitude is an evaluation or generalized judgment made by a person of a physical object, another person, or an idea. Attitudes are important in the interpersonal domain, since the behavior of people is guided by their attitudes. If, for example, a person has a negative attitude toward another person, then the person is less likely to assist the other person in achieving that person’s goals. It is insufficient, however, merely to model the impact of attitudes on the

<!-- page: 175 -->

behavior of a person. In addition, it is necessary to model how a person models the attitudes of others, and how this modeling in turn affects that person’s behavior: We are often concerned with the attitudes of other persons toward us and frequently behave in order to manipulate those attitudes (Goffman, 1959).

In this section, we address: (a) the representation of attitudes, (b) the formation of attitudes, (c) the monitoring of the attitudes of others, and (d) the modification of the attitudes of others.

### 6.3.1 Basic Attitude Representation The representation of attitudes in DAYDREAMER

is based on previous work by

Heider (1958) (who calls them “sentiments”). While emotions (such as anger directed toward an object) have a short duration, attitudes are stable dispositions toward objects. Attitudes may be classified into positive and negative attitudes. A POS-ATTITUDE toward something means liking that something; a NEG-ATTITUDE means disliking it. There is a special positive attitude called

ROMANTIC-INTEREST® as well. A taxonomy of attitudes has been constructed by Schank, Wilensky, Carbonell, Kolodner, and Hendler (1978). However, their emphasis is on the inferences which can be made on the basis of attitudes in the comprehension of stories, rather than on the relationship of attitudes to behavior. As a result, the set of attitudes which they propose is too large for our purposes—a sufficiently difficult problem is presented by only positive, negative, and romantic attitudes. Attitudes are mental states. Because all mental states are those of the daydreamer, an attitude by itself represents a self mental state. An attitude of another person is represented as a belief containing an attitude. However, for simplicity, we use “person has a POS-ATTITUDE toward an object” to mean “a BELIEVE of person that POS-ATTITUDE toward object.”

### 6.3.2 Assessing, Forming, and Modifying Attitudes

In social psychology, there are a number of similar cognitive balance theories which contend that the attitudes which a person holds tend to proceed toward

balance: Festinger’s (1957) theory of “cognitive dissonance,” Heider’s (1958) “preference for balanced states,” the “principle of congruity” of Osgood and Tannenbaum (1955), as well as other related theories. Heider (1958, pp. 174217) discusses various cases of this principle, some of which are encoded as rules in DAYDREAMER. Each rule has several functions: The rule may be employed

(a) as an inference rule in the formation of the attitudes of DAYDREAMER, (b) as a planning rule for DAYDREAMER to achieve certain self attitudes, (c)

4This attitude might have been called the LOVE attitude, but since DAYDREAMER concentrates on modeling the initial phase of a relationship, the term more appropriate to this stage is employed.

<!-- page: 176 -->

156 CHAPTER 6. DAYDREAMING

IN THE INTERPERSONAL DOMAIN

as an inference rule for the monitoring of the attitudes of others, and (d) as a planning rule for the modification of the attitudes of others. According to Heider (1958), a person will tend to like another person having similar beliefs and attitudes. Thus in DAYDREAMER we have the rule that people tend to like those who like the same people and things they do: IF THEN

ACTIVE-GOAL to have POS-ATTITUDE toward person ACTIVE-GOAL for person to have POS-ATTITUDE toward something that self has POS-ATTITUDE toward

Thus, for example, if one likes Indian food, one tends to like other people who also like Indian food. Since people tend to like themselves and aspects of themselves, the above rule also implies that one person will tend to like a second person who likes the first person or an aspect of the first person. Another consequence of the above rule is that if one person likes a second person, the first person tends to believe that the second person likes the first person. Another principle is that a person will tend to like a similar person and

dislike a dissimilar person (Heider, 1958): “Birds of a feather flock together.” That is, one will form a positive attitude toward others with similar personality traits (e.g., social class, appearance) and a negative attitude toward those with dissimilar personality traits. An instance of this principle is provided by the following DAYDREAMER rule which states that well-dressed rich people do not like others who are not well-dressed: person is RICH and self is AT same location as person and

self not WELL-DRESSED person has NEG-ATTITUDE

toward self

How is the attitude of ROMANTIC-INTEREST generated and modified? In DAYDREAMER we will simply assume that a certain level of this attitude may be generated if one has a positive attitude toward the other person, and one finds the other person attractive in appearance; we assume the ROMANTICINTEREST attitude may be strengthened if the constituent positive attitude and attractiveness are strengthened, as well as through continued success of a LOVERS relationship. We have the following rule:

THEN

ACTIVE-GOAL person ACTIVE-GOAL and

to have ROMANTIC-INTEREST to have POS-ATTITUDE

in

toward person

person to be ATTRACTIVE

In order to get another person to have a particular personal goal, one must achieve the antecedents of the rule which activates that goal. For example, the following rule states that a goal to be LOVERS with someone is initiated if one

has ROMANTIC-INTEREST with someone else:

in that person and one is not currently LOVERS

<!-- page: 177 -->

## 6.3 ATTITUDES THEN

ACTIVE-GOAL of LOVERS with ACTIVE-GOAL and ACTIVE- GOAL

157 for self to have ACTIVE-GOAL person for ROMANTIC-INTEREST in person for not LOVERS

with anyone

Although this rule is stated in terms of the self rather than another person, it can still be applied to another person through backward other planning.

### 6.3.3 Attitudes and Meeting People

In order for DAYDREAMER to form a FRIENDS or LOVERS relationship, she must meet people. In this section, we investigate how one person approaches another when the two are not already acquainted. That is, we explore the situations in which is it acceptable for one person to initiate communication with another, and how acceptable “opening lines” are produced. The ACQUAINTED relationship in DAYDREAMER models the fact that two people know each other. This relationship has the inference that whenever the two persons meet, each will recognize the other and say hello to the other. Being acquainted with someone is a prerequisite to forming a more important FRIENDS or LOVERS relationship with the person. In DAYDREAMER, two become acquainted when they have participated in an M-CONVERSATION:

IF THEN

ACTIVE-GOAL ACTIVE-GOAL

to be ACQUAINTED with person for M-CONVERSATION with person

The M- prefix is from Schank (1982) and refers to a complex action made up of more primitive actions. Thus, an M-CONVERSATION consists of a series of exchanges, where each exchange is one or more MTRANSes by the first person followed by one or more MTRANSes by the second person. If two persons are not already ACQUAINTED, there are certain constraints on the first MTRANS which initiates the conversation—the MTRANS from the first person to the second person must be acceptable to the second person, which means:

- the second person does not form a negative attitude toward the first person

- the second person does not believe that others (or society) would form a negative attitude toward the second person if the second person failed to behave as if the second person had a negative attitude toward the first

person. That is, it is acceptable to initiate communication with someone if neither oneself nor the other person is embarrassed as a result. The acceptability criteria are encapsulated under a state called MTRANSACCEPTABLE which may or may not exist between two individuals in a given

<!-- page: 178 -->

6. DAYDREAMING

158 CHAPTER

IN THE INTERPERSONAL

DOMAIN

situation. A conversation is then achieved if MTRANS-ACCEPTABLE of the two persons and if each person introduces oneself to the other:°

THEN

is true

ACTIVE-GOAL for M-CONVERSATION between person1 and person2 ACTIVE-GOAL for MTRANS-ACCEPTABLE between personl and person2 and ACTIVE-GOAL for personl to MTRANS INTRODUCTION to person2 and person2 to MTRANS INTRODUCTION to person1

However, if one person MTRANSes to another without the state MTRANSACCEPTABLE, the other will form a negative attitude toward the first: not MTRANS-ACCEPTABLE between personl and person2 and person! MTRANS anything to person2 person2 has NEG-ATTITUDE toward person1l

What, then, are the criteria for MTRANS-ACCEPTABLE?

That is, what

determines whether the second person forms a negative attitude (or believes that such an attitude should be formed) toward the first person? Such attitude formation is sensitive to the particular goal context in which the communication occurs, as well as the particular existing attitudes and traits of the persons involved. In general, a negative attitude will not be formed (and the communication will be acceptable) if the communication is performed as a subgoal of some goal situation which the two people have (or are contrived to have) in common. DAYDREAMER contains various planning and inferences rules for MTRANSACCEPTABLE. For example, a request for a small favor, such as the time, is an acceptable initial communication:

THEN

ACTIVE-GOAL for MTRANS-ACCEPTABLE self and person ACTIVE-GOAL for self to MTRANS to person that self has ACTIVE-GOAL to KNOW the time

between

In DAYDREAMER, communication is also acceptable with another person if (a) one is already acquainted with that person, (b) that person has already spoken to the self, (c) that person already has the goal to be acquainted with

the self (as is inferred in the situation of a party), and (d) if the other person smiles at the self. °The subgoal for MTRANS-ACCEPTABLE is not strictly (causally) necessary for achievement of the goal. It is introduced to prevent certain negative consequences. That is, although a conversation really requires only the exchanges themselves, if an initial communication by one person is deemed unacceptable by another, that party will form a negative attitude toward the first person. Therefore an additional subgoal is added to the rule to pre-

vent formation of such a negative attitude. When actions to prevent negative consequences are not already compiled into a rule, the REVERSAL daydreaming goal may be used to create new rules in which such actions are compiled in; see Chapter 4.

<!-- page: 179 -->

Interpersonal Relationships

There

are

DREAMER:

three

interpersonal

FRIENDS,

relationships

LOVERS,

currently

and EMPLOYMENT.

represented

in DAY-

Two persons may go

through a sequence of relationships over time: For example, they may start out as FRIENDS, which is terminated by the two becoming LOVERS, which then is terminated by the two becoming FRIENDS, and so on. In general, there may be more than one relationship between two persons—for example, the same two persons could participate in both a FRIENDS and an EMPLOYMENT relationship. However, not all combinations are possible. In our scheme, LOVERS subsumes FRIENDS so both relationships should not be present between two people at a given time.

### 6.4.1 Lovers

The LOVERS relationship consists of the following phases: nance, and termination.

initiation, mainte-

Once a personal goal to form a LOVERS relationship with someone is activated, this goal may be achieved by: (a) being acquainted with a person, (b) being romantically interested in the person, (c) the person having the goal to

be in the relationship, (d) going on a date with the person, and (e) agreeing to initiate the relationship. These steps are expressed as the following planning rule in DAYDREAMER: ACTIVE-GOAL

for LOVERS

ACTIVE-GOAL ACTIVE-GOAL and ACTIVE-GOAL

for ACQUAINTED with person and for ROMANTIC-INTEREST in person

of LOVERS

with person

for person to have ACTIVE-GOAL with self and

ACTIVE-GOAL

for M-DATE

and ACTIVE-GOAL to LOVERS

with self and person

for self and person to M-AGREE

It would be possible to introduce a loop into the planning: the M-DATEs would continue until the two parties agreed to form the more permanent Since DAYLOVERS relationship (or decided they were not interested). DREAMER does not represent a great variety or degree of detail of the activities performed on a date, a loop would just add more repetition to the traces. Therefore, only one M-DATE is performed before initiating a LOVERS relationship in the current system. and ROMANTICWe have discussed planning for ACQUAINTED that a person will states rule Another section. previous the INTEREST in the first person is if person another with relationship a have the goal to initiate is not currently person first the and person second the in romantically interested in a relationship:

<!-- page: 180 -->

160 CHAPTER 6. DAYDREAMING

IN THE INTERPERSONAL

IF

ACTIVE-GOAL

THEN

of LOVERS with person ACTIVE-GOAL for ROMANTIC-INTEREST and

ACTIVE-GOAL

DOMAIN

for self to have ACTIVE-GOAL

for not LOVERS

in person

with anyone

This rule, although stated in terms of the self, is employed through the backward other planning mechanism to plan for a person other than DAYDREAMER.

An M-DATE activity

toward

in DAYDREAMER

consists of:

which

have

both

parties

(a) agreeing to perform an

a positive

attitude

(such as

M-

RESTAURANT or M-MOVIE), (b) enabling future MTRANSes (in order to arrange meeting time and location), (c) at a later time going to the other person’s home, (e) performing the activity (including going to an appropriate location to perform the activity), (f) going back to the other person’s home, and (g) kissing each other. This is expressed as the following rule: ACTIVE-GOAL ACTIVE-GOAL

for M-DATE with self and person for self and person to M-AGREE to M-RESTAURANT with self and person and ACTIVE-GOAL for self and person to ENABLE-FUTURE-VPROX and ACTIVE-GOAL for it to beFRIDAY-NIGHT and ACTIVE-GOAL for self to be AT location of person and ACTIVE-GOAL for M-RESTAURANT with self and person and ACTIVE-GOALE for self and person to be AT initial location of person and ACTIVE-GOAL for self and person to M-KISS and ACTIVE-GOAL for self to be AT initial location of self

An agreement to perform an activity or initiate a state for the mutual benefit of the parties results when the parties MTRANS the fact that they have the same goal to each other:

THEN

ACTIVE-GOAL for self and person to M-AGREE to thing ACTIVE-GOAL for self to MTRANS to person that self has ACTIVE-GOAL for that something and ACTIVE-GOAL for person to MTRANS to self

that person have ACTIVE-GOAL

for thing

The LOVERS relationship consists of the following conditions and inferences: (a) each party supports various goals of the other, (b) each party maintains a ROMANTIC-INTEREST attitude toward the other, and (c) each party is not in a LOVERS relationship with another (and especially must not engage in M-SEX with another).

<!-- page: 181 -->

Violations of requirements activate preservation goals on the relationship. For example, the following rule is employed in RATIONALIZATION to generate 2 preservation goal on the relationship when Harrison Ford leaves Los Angeles. IF

LOVERS with person who is RPRO®X eM is not RPROX city

THEN

ACTIVE-P-GOAL

of LOVERS

city and

with person

If the requirements of the relationship are violated sufficiently, one or both parties may decide to terminate the relationship: ACTIVE-GOAL for FAILED-GOAL of POS-RELATIONSHIP with person ACTIVE-GOAL for person to MTRANS to self that person has ACTIVE-GOAL not to be in POS-RELATIONSHIP with slf

THEN

This rule applies to any positive relationship (i.e., FRIENDS, LOVERS, and

EMPLOYMENT).

### 6.4.2 Friends

A PRIENDS relationship is achieved whenever two people are acquainted and they have positive attitudes toward each other:

THEN

### 6.4.3 ACTIVE-GOAL ACTIVE-GOAL

and ACTIVE-GOAL and ACTIVE-GOAL toward self

for FRIENDS with person for self and person to be ACQUAINTED to have POS-ATTITUDE

toward person

for person to have POS-ATTITUDE

Employment

The EMPLOYMENT relationship is initiated in the following manner: (a) an employer has an opening, (b) an employer wants a particular employee for the

job, (c) the employee wants the job, and (d) the employer and employee agree to the job. These steps are expressed in terms of the following rule: IF THEN

ACTIVE-GOAL

for EMPLOYMENT

with person

ACTIVE-GOAL for person to have ACTIVE-GOAL of employing someone and ACTIVE-GOAL for person to have ACTIVE-GOAL of employing self and

ACTIVE-GOAL for self and person to M-AGREE of self with person to EMPLOYMENT

<!-- page: 182 -->

162 CHAPTER 6. DAYDREAMING

IN THE INTERPERSONAL

DOMAIN

One can quit and one can be fired; this is accomplished via the rules given above for termination of LOVERS. In addition, one’s job fails if one is not living in the same region as the city in which one is employed (this rule is employed in RATIONALIZATION1 to generate failure of employment upon following Harrison Ford to Cairo): have EMPLOYMENT with organization which is RPROX city and self is not RPROX city FAILED-GOAL of EMPLOYMENT with organization

## 6.5 Strategies for Fanciful Planning

Consider the following hypothetical (but typical) daydream, given by S. Freud (1908/1962), of a poor orphan boy on his way to see an employer where he might find a job:

He [daydreams that he] is given a job, finds favour with his new employer, makes himself indispensable in the business, is taken into his employer’s family, marries the charming young daughter of the house, and then himself becomes a director of the business, first as his employer’s partner and then as his successor. (p. 148) In this daydream, the goals most important to the orphan—to be employed, to have a family, to be married, to be the director of a business—are achieved without considering all of the necessary planning steps and obstacles. He does not imagine knocking on the employer’s door, the opening of the door, talking

with the employer about a job, being offered (or refused) a job, accepting the job, performing the activities of the job each day for many days, and so on. Furthermore, he ignores the likely behavior of the employer, family, and daughter as well as his own probable limitations. Planning in daydreaming relazes constraints: A constraint relaxed in the above daydream is the probable behavior of other people. Another constraint often relaxed in daydreaming is the identity or attributes of the self. One might imagine being a famous movie star (as in REVENGE1), an Olympic athlete, or even a physical object or animal. Often physical constraints are relaxed in daydreaming: One might imagine being able to fly, being invisible, being able to slide underneath doors, and so on. Finally, social and cultural constraints may be relaxed as when one imagines yelling “fire!” in a crowded movie theater. In one daydream, one might imagine walking around in Paris without considering the plane trip required to get there. But after seeing a news report on terrorism, one might daydream about the plane trip itself. How does DAYDREAMER, determine when to relax a constraint and when to omit details? Three strategies are employed in DAYDREAMER for the generation of fanciful daydream scenarios in the interpersonal domain: plausible planning, subgoal relaxation, and inference inhibition. We consider each of these methods in turn.

<!-- page: 183 -->

Plausible Planning

Daydream scenarios which are not completely realistic may be generated through the use of plausible planning and inference rules (Shortliffe & Buchanan, 1975). Associated with each rule is a number from 0 to 1 which roughly specifies how plausible that inference or planning rule is. A somewhat fanciful daydream scenario results whenever an inference or planning rule is employed whose plausibility is less than 1. For example, DAYDREAMER employs the following plausible planning rule in generating REVENGE1: IF THEN

ACTIVE-GOAL ACTIVE-GOAL

for self to be STAR for self to M-STUDY

to be an

ACTOR

This rule, which states that one may become a star by studying to be an actor, is assigned a plausibility of 0.4. The realism of a scenario, also measured on a scale from 0 to 1, is calculated based upon the plausibilities of the planning and inference rules employed in generating that scenario. The particular values are not important. What is important is that (a) rules not guaranteed to succeed

may be employed in planning, (b) some rules are more likely to succeed than others, and (c) numbers can be used to provide a rough measure of scenario realism. For any given subgoal, there may be several applicable plausible planning rules. How does DAYDREAMER decide which rules to employ and in what order to attempt each of these rules? When planning for personal goals and learning daydreaming goalsk—REVERSAL, REHEARSAL, RECOVERY, and REPERCUSSIONS—rules are attempted in order of decreasing plausibility. When planning for emotional daydreaming goals—RATIONALIZATION, REVENGE, and ROVING—rules are attempted in random order. Furthermore, when planning for personal goals and learning daydreaming goals, a rule is only selected if its application would result in a scenario whose realism is above a certain level. This level is higher for personal goals than for learning daydreaming goals. Such a restriction is not imposed on emotional daydreaming goals. Thus personal goals result in the most realistic scenarios, learning daydreaming goals result in moderately realistic scenarios, while emotional daydreaming goals may result in fairly unrealistic scenarios. Assessment of the realism of a daydream scenario is important since daydreams are potential plans for future action. If a particular daydream contains unrealistic world events in response to actions of the daydreamer, it should not be employed in the world; in performance mode it does not matter what the imagined positive consequences of the daydream are if those consequences are derived from an unrealistic scenario. If DAYDREAMER failed to make realism assessments, it would act on its unrealistic daydreams and be disappointed with the results—that is, its personal goals would fail.

<!-- page: 184 -->

164 CHAPTER 6. DAYDREAMING

### 6.5.2 IN THE INTERPERSONAL DOMAIN

Subgoal Relaxation

In addition, fanciful daydream scenarios may be generated by hypothesizing the success of an unsatisfied subgoal without generating a plan—even a plauszble plan—to achieve that subgoal. Such subgoal relaxation is performed in the following situations:

- All plans to achieve a subgoal have failed and the objective of the subgoal is either: a mental state (e.g., goal, attitude, belief) of another or a self attribute (e.g., appearance, social class). Examples occur in LOVERS1, in which DAYDREAMER assumes Harrison Ford is interested in her and thinks she is attractive.

- A daydreaming goal strategy (implemented as rules) explicitly specifies that the success of a given subgoal is to be hypothesized. Here the purpose of a daydream, as specified by the current daydreaming goal, determines which subgoal success to hypothesize. For example, one plan for the RATIONALIZATION of a past goal failure involves hypothesizing the success of that saine goal, without worrying about how that goal might have been achieved. Subgoal relaxation, by itself, is similar to the relaxation of operator preconditions employed in ABSTRIPS (Sacerdoti, 1974). See Chapter 5 for a review of ABSTRIPS in the full context of DAYDREAMER.

### 6.5.3 Inference Inhibition

In addition, fanciful daydream scenarios may be generated by failing to infer negative consequences of a situation. In particular, social or cultural consequences (such as being embarrassed or being thrown in jail) are ignored whenever a special daydreaming goal® is invoked to explore a socially or culturally unacceptable goal (such as causing chaos or stealing). For example: I’m sitting on the deck of Pacific Princess with lots of people swimming and sun-bathing not far away from me. A waiter comes around passing out desserts. Then someone gives a signal and everybody

starts throwing food all over the place. (McNeil, 1981) I imagine myself as the notorious bank robber who is wanted by the law everywhere. I’m entering a majestic bank full of security guards. I still carry out my plan and achieve my goal despite... the security

network. (McNeil, 1981) Daydreams produced using inference inhibition have low realism values, so that they are not acted upon in performance mode. Alternatively, inferences ®This daydreaming goal is not yet implemented in the current version of DAYDREAMER.

<!-- page: 185 -->

can be turned on during generation of socially unacceptable actions; although the realism of such a scenario may then be high, the desirability evaluation of the scenario will be low because of its negative consequences. Thus such a daydream will also not be acted upon in performance mode. Inference inhibition probably occurs in humans only after a person has learned which actions are socially unacceptable through daydreaming without inference inhibition. Thereafter, the generation of such daydreams simply serves as a form of self-entertainment (J. L. Singer, 1975). Generation of negative consequences would reduce the entertainment value of such daydreams.

<!-- page: 186 -->

&

7

ie vetoes Prete 0d) i.

‘onri)

Pi

-

a

Sak pe

sade Put

:i) ai> vied er a

:

:

:

1

ets

,

N

P|

ier nee in ar ha‘Veg ten swibben

: 1 _- Very a iest dsatenils stdat fyaaaarnat ie ad aly ‘. yieeyirasl rea thaw bn aySts

tra

oh)

eS as tee, Bern

ates ben, ute io ashe, Japaringa38; nett ll ' v

7

<a & @in@aainune-< ey.. be are af

)

—_ ax,

—

;

|

edarinan\

:

:

in "aes

.

OMic

ij vee!

athal,

aa

-

up raL wen

> “BF

tye

ny

4

off

=>

of

¢

Pay

L) Cachine |

t het

ee

@

erie the plemmg goal Ges

al

i Wierctne

Toe T ~ parted »

:

7 Migs + Tl a

eprriid

Ge Miled) epeiealy

(eS her ext

“yi pei

thes bin

2p

io

jurw

jriady FG

e

at

t

thr

—

“

. eae

f

¢iut

id mish

—

Al

Nw

‘

$ 45

‘

Shia

el

lov oakHanae " « 7 gecitie

ma

Ad by

-

-

ss

=«@

J

ad

—

oT) Vv?

‘

t

"

:

<

——

:

f C8 eT

Wikest

:

is

es:

‘

i

mes

7

1

ie

4

=

a

7

:; noix fe)

‘ ee

ime

el ¢

iu }

r

4

ie

yd

ta

; rie

vom

ine

128

z

i

@ an

i

a

}

ral

(rt ph

yeh

i@

"

&

he

G

net

;

i}

| Oe GOR

y Pah

4 ie

“~

AA

7

>»

F

‘

i

ald

ca

vi

is il

Chaba

ey

itedtwee

Ti. Aeris

—

n*

| Absa ide

On aT)

oe

a >=

buy dou fiay reid, Uf 4 vegwnsed

)

-

eee

————

<

=

7

7

-

,

-

@

a

e

=

'

_

ores stady Rchne

;

=

-

'

a

——

:

|

f

=a.4eir

—_

;

>

:

btelli ha, Jr

in “re

a

at

: a

ate

ih

——

ge

-

Lhe

4

WOerent

ad

_

te

; satan!

ae

Frey,

(npeesintend

cmzot;

\erraric iy =e

a

7

a

i wnteon moa 7. iT

wai

4

Loy

a

Kiurlien Sued by eee

erm

OREAUER

heh Os

be

iy
