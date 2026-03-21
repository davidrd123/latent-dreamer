# Chapter 1: Introduction

<!-- page: 21 -->

The field of artificial intelligence is concerned with the construction of computer systems which exhibit intelligent behavior in order to augment and complement our own human intelligence, and to understand more about how the human mind works. Consider the human phenomenon of daydreaming: the spontaneous activity—carried out in a stream of thought—of recalling past experiences, imagining alternative courses that a past experience might have taken, and imagining possible future experiences. Why is daydreaming an interesting topic of study for artificial intelligence? On the face of it, daydreaming may seem like a human shortcoming, a useless distraction from a task being performed which would be undesirable in computer systems. But how can we ignore an aspect of human thought which appears so frequently in everyday life? For example:

- While driving home from work with the car radio on, you suddenly realize you have completely missed the last few minutes of the news report. Instead of listening, you have been mentally replaying an event of the day and altering it to how you would have liked it to come out. You think about what you might have said or done, but didn’t.

- An important job interview is coming up. You have trouble concentrating on your current task. Instead, you find yourself imagining the future event again and again. You go over what you and others will say or do in that situation, and you imagine things go—or don’t go—the way you would like them to go.

- You are reading a book and are startled by the noisy cuckoo clock. You realize that you have been lost in thought for minutes, following a chain of experiences and associations triggered by the sentence or word you read just before drifting off.

<!-- page: 22 -->

- You are sitting in a classroom and are bored with the material. You miss the question which the instructor has just asked you because, in your fantasy, you have been off driving in a convertible on the French Riviera, wearing a scarf, your hair waving in the wind.

- When things don’t go as planned, you make yourself feel better by imagining how if they had gone as planned, you might have been even worse off.

Most likely, some or all of the above experiences are familiar to the reader. Since daydreaming is such a pervasive aspect of human experience (Pope, 1978; J. L. Singer, 1975; Klinger, 1971), the following questions naturally arise: Why do we daydream? How is daydreaming useful to us? What are the advantages (and disadvantages) of daydreaming? If daydreaming serves a useful function in humans, then might daydreaming not also be useful in a computer system? Do we want our computers to daydream? Can a computer even be fully intelligent without being able to daydream?

## 1.1 The DAYDREAMER Program

This book presents a theory of human daydreaming implemented as a computer program called DAYDREAMER. This program models the daydreaming, and to a lesser extent the overt behavior, of a human in the domain of interpersonal relations and common everyday occurrences.

Ideally, DAYDREAMER would be a robot capable of moving around, performing actions, and communicating with humans and other robots in the real world; this would enable the program to interact with and learn from a fertile and often unpredictable environment just as human daydreamers do. However, in order to construct such a robot, difficult problems in robotics, vision, and speech understanding would have to be solved first, not to mention obtaining and constructing the necessary hardware. Since these problems are outside the scope of our work, DAYDREAMER is instead embedded within a simulated world which models the real world of humans.

As input, DAYDREAMER takes descriptions of events in this world. As output, DAYDREAMER describes both actions which it performs in this world as well as its internal “stream of thought,” sequences of events in imaginary past or future worlds, or daydreams. (A more precise definition of daydreaming will be provided later on.) Several sources of verbal reports of daydreaming, henceforth called protocols, have been tapped in the construction of our theory and program: (a) previously published protocols (Pope, 1978; Klinger, 1971; G. H. Green, 1923; Varendonck, 1921), (b) unpublished retrospective reports (see Ericsson & Simon, 1984) collected from subjects by McNeil (1981), and (c) protocols collected by the author from people via the methods of immediate retrospective

<!-- page: 23 -->

report, retrospective report of memorable or classic daydreams, and typing on a computer terminal concurrently with daydreaming.

DAYDREAMER was constructed in order to produce daydreams similar but not identical to the obtained protocols. The daydreams produced by the program result from its particular knowledge structures and processes, which were designed to correspond to a hypothetical female daydreamer in her twenties.

DAYDREAMER operates in two modes: daydreaming mode and performance mode. In daydreaming mode, the program continuously daydreams until it runs out of things to daydream about or is interrupted. In performance mode, the program accepts input states or actions and produces output actions.

When DAYDREAMER is first started, the daydreamer has a job and is not romantically attached. Below is a portion of the I/O actually produced by DAYDREAMER, showing:

1. input world states or actions (indicated by “Input” and displayed in a slanted type style)
2. output actions which modify the state of the world (indicated by “External Action” and displayed in a bold type style)
3. the internal “stream of thought” of the program (displayed in a sans serif type style)

Both input and output are in the form of English sentences.

```text
LOVERS1

| want to be going out with someone. | feel really interested in going out with someone.
| want to be entertained. | feel interested in being entertained.
| have to go see a movie.

External action: I go to the Nuart.
Input: Harrison Ford is at the Nuart.

What do you know! | have to have a conversation with him.

External action: I tell Harrison Ford I would like to know the time.

He does not think much of me because | am not well dressed.
| fail at him thinking highly of me. | feel really embarrassed.

External action: I introduce myself to him.
Input: He introduces himself to me.

He has to be interested in me. He cannot be going out with anyone.
Maybe he thinks | am cute.

External action: I tell him I like his movies.
```

<!-- page: 24 -->

```text
LOVERS1 (continued)

He thinks highly of me. He is interested in me. Maybe he is not going out with anyone.
Maybe he wants to be going out with me.

External action: I tell him I would like to have dinner with him at a restaurant.

Input: He declines.

| fail at going out with him. | feel really angry at him.

External action: I watch a movie at the Nuart.

| want to be going out with someone. | feel really interested in going out with someone.

External action: I go home.

| succeed at being entertained. | feel amused.
```

In the above experience, called `LOVERS1` for future reference, DAYDREAMER first activates two goals: one to form a romantic relationship, and another to satisfy a need for entertainment. She acts first on her entertainment goal by performing an external action: going to see a movie at the Nuart Theater. There she accidentally meets the actor Harrison Ford, whom she has always liked; his presence at the theater is provided as external input. Acting on behalf of her goal to form a relationship, she asks him if he knows the time. Now she feels embarrassed because she is not wearing her best clothes. In any case, she introduces herself to him and he introduces himself to her. She then tells him she enjoys his films and asks him if he would like to have dinner sometime. Unfortunately, he turns her down. Her goal to form a relationship thus fails, and she is angry at him. She then watches the movie, resulting in the success of her entertainment goal. After this experience, DAYDREAMER amuses herself with the following revenge daydream:

```text
REVENGE1

| study to be an actor. | am a movie star even more famous than he is.
| feel pleased. He is interested in me. He breaks up with his girlfriend.
He wants to be going out with me. He calls me up. | turn him down.
| get even with him. | feel pleased.
```

DAYDREAMER then tries to make herself feel better by generating the following rationalization daydream:

```text
RATIONALIZATION1

What if | were going out with him? He would need work. | remember
the time he had a job with Paramount Pictures in Cairo. He would go
to Cairo. Our relationship would be in trouble. | would go to Cairo. |
would lose my job at May Company. | feel relieved.
```

<!-- page: 25 -->

DAYDREAMER is relieved about being turned down by Harrison Ford because if she had ended up in a relationship with him, she would have lost her job. Another rationalization is then generated:

```text
RATIONALIZATION2

| remember the time my being turned down by Irving led to a success
by being turned down by him leading to succeeding at going out with
Chris. | go to Mom's. He goes to Mom's. | am going out with him. My
being turned down by Harrison Ford leads to me succeeding at going
out with Chris.
```

In the above daydream, DAYDREAMER imagines that as a result of being turned down by Harrison Ford, she goes to Mom’s Bar and meets someone else. Further rationalizations are produced:

```text
RATIONALIZATION3

Anyway, | was well dressed because | was wearing a necklace. | feel less
embarrassed. Anyway, | do not think much of Harrison Ford. | feel less
embarrassed. | rationalize being turned down by him.
```

In the above daydream, DAYDREAMER reduces her embarrassment about her clothes by (a) thinking about the nice necklace she was wearing, and (b) deciding she does not like Harrison Ford anyway.

```text
ROVING1

| remember the time Steve told me he thought | was wonderful at
Gulliver's. | feel pleased. | remember the time | had a job in the
Marina. | remember the time Steve and | bought sunglasses in Venice
Beach.
```

In this daydream, DAYDREAMER diverts attention from negative emotions associated with being turned down by Harrison Ford to a pleasant past experience. She is then reminded of two other related experiences. Further daydreams resulting from `LOVERS1` will be presented and discussed in the following section.

## 1.2 Research Objectives and Issues

The objective of the present work is to construct a model of daydreaming and related phenomena using the computer as a tool. We seek to apply the techniques of artificial intelligence to the problem, to flesh out these techniques in terms of particular knowledge structures and constructs, to extend these techniques where they fall short, and to develop new techniques. We seek to gain new psychological insights, to raise new psychological issues, to generate new

<!-- page: 26 -->

hypotheses, and to suggest possible experiments for testing these hypotheses. In sum, our goal is to investigate daydreaming from the standpoint of computer modeling, and in so doing to advance the fields of both artificial intelligence and psychology.

The fundamental hypothesis of our work is that daydreaming is useful for humans, in particular, that it serves the following important functions: (a) learning from imagined experiences, (b) creative problem solving, and (c) useful interaction with emotions. In this section, we discuss these functions, how they are useful in a computer system, and, specifically, how they are demonstrated in DAYDREAMER. We present the associated research problems addressed by the present work.

### 1.2.1 Learning from Imagined Experiences

Previous work in artificial intelligence has addressed learning from real experiences: DeJong (1981), Carbonell (1983), and Kolodner, Simpson, and Sycara-Cyranski (1985) have investigated how behavior can be improved as a result of previous experience; Schank (1982) and Dyer (1983a) have pointed out the importance of learning from failure experiences. But in order to learn from a given situation, why should one have to wait for that situation to come up in the real world? It should be possible to learn from imagined experiences in addition to real ones. In fact, humans frequently daydream about hypothetical future experiences (Pope, 1978; J. L. Singer, 1975, pp. 118-119, 1966; Klinger, 1971, pp. 47-48; Rapaport, 1951, pp. 718-719; G. H. Green, 1923, pp. 16, 180). We propose that this activity enables learning, or the useful modification of future behavior, in several ways.

First, daydreaming enables the formation of intentions to perform certain actions in the future. For example, in the following daydream, DAYDREAMER thinks of a way she could find out Harrison Ford’s unlisted telephone number in order to contact him again:

```text
RECOVERY1

| have to ask him out. | have to know his unlisted telephone number.
| remember the time Alex knew Rich's unlisted telephone number by
logging into the TRW credit database. | ask Alex to look up Harrison
Ford's unlisted telephone number in the TRW credit database.
```

The next time DAYDREAMER sees Alex, she will ask him to help her find out the actor’s telephone number.

Second, daydreaming enables decision-making by (a) exploring alternative plans for achieving a goal, and then (b) forming an intention to carry out the best such plan. McDougall (1923) proposes a similar function for imagination, of which he considers daydreaming to be one form: “The essential and primary function of imagination is to carry out the process of trial and error on the

<!-- page: 27 -->

imaginary plane, to depict each situation and the consequences of each step of action, before the action is accomplished or even begun” (p. 292).

Third, daydreaming enables preparation in the face of various contingencies through (a) the exploration of alternative situations that might arise in trying to achieve a goal, and (b) the formation of intentions to perform certain actions should those situations arise. For example, before a job interview one may daydream about possible questions of the interviewer and various responses to those questions.

Fourth, daydreaming, when one is otherwise unoccupied with a task, enables discovery of negative consequences of carrying out a future intention that might not otherwise have been recognized. Then, once a negative consequence of a future intention has been discovered, the future intention may be adjusted as appropriate, and tested through further daydreaming. Thus daydreaming provides an adaptive safety mechanism to avoid undesirable courses of action. For example:

```text
NEWSPAPER

| want to be entertained. | feel hopeful. | have to read the newspaper.
| have to grab the newspaper.
| go outside. | get wet. | feel displeased. | fail at keeping dry.

What if | put on a raincoat? | go outside. | grab the newspaper. | read
the newspaper. ...

| want to avoid getting wet.
External action: I put on a raincoat.
I go outside. I grab the newspaper. I read the newspaper.
```

DAYDREAMER has the intention to go outside to get the newspaper. She then imagines carrying out this intention. Since it is raining, she gets wet. As a result of this negative consequence, she adjusts her intention and tests it out through another daydream; she imagines putting on a raincoat before going outside.

Why is this learning? Before the daydream, DAYDREAMER knew only that (a) a plan to be at some location is to go to that location, and (b) one gets wet if one is at some location and not wearing a raincoat while it is raining at that location. After the daydream, the program also knows that a plan to be at some location while it is raining at that location is to put on a raincoat and then go to that location. In this case, the negative consequence of the plan happens to be obvious to most people, but not to the program. In other cases, however, the negative consequences may not be so obvious.

Note: the example of getting the newspaper while it is raining is borrowed from Wilensky (1983).

<!-- page: 28 -->

Fifth, by spending free time exploring possible future situations in the world, daydreaming enables the discovery of negative consequences of a situation that might not otherwise have been noticed. Consequently, daydreaming enables one to take steps to prevent those negative consequences well in advance of the future situation. Alternatively, daydreaming enables one to form the intention to take certain steps as soon as the future situation arises, in order to prevent those negative consequences from occurring. For example:

```text
REPERCUSSIONS1

Input: There is an earthquake in Mexico City.

What if there is an earthquake in Los Angeles? My apartment collapses.
My possessions are destroyed. | am killed. | feel very worried.
| have to go to the doorway. | have to get insurance.

What if there is an earthquake in Los Angeles? | go to the doorway. |
get hit by a falling plant. | get hurt. My possessions are destroyed. |
feel very worried.

| have to move the plant away from the doorway. | have to buy insurance
from some company.

External action: I go to State Farm. I pay Sally.
Input: Sally gives me the insurance.

| am insured. | feel very pleased.
```

DAYDREAMER imagines there is an earthquake, her apartment collapses, and she is killed. In order to prevent this negative consequence of an earthquake, she forms the intention to run to the doorway as soon as the earthquake begins. She imagines carrying out this intention in another daydream. This time, however, she is hit by a plant which is hung above the doorway. So she decides to move the plant well before a possible future earthquake. Without exploring the possibility of running to the doorway in a daydream, the need to move the plant might never have been recognized.

Some Olympic gymnasts and divers prepare for performances by mentally “running through” those performances in advance (Suinn, 1984). Thus, in humans, rehearsing future actions through daydreaming may increase the accessibility of planned actions and the skill with which they can be performed. J. R. Anderson (1983, pp. 172-173), however, suggests that short distractions, daydreams about something other than a task, may be useful to that task: he hypothesizes that bringing an item out of short-term memory and then back in again results in a strengthening of the long-term memory of that item; therefore, short distractions which remove an item from short-term memory and then restore that item are useful in increasing retention of facts.

Humans daydream not only about hypothetical future experiences, but about hypothetical past experiences as well (Klinger, 1971, p. 301; Neisser,

<!-- page: 29 -->

1982c, p. 14). After an experience, one often imagines alternative sequences of events which might have led to different outcomes. We propose that daydreaming about imaginary variations of a past experience enables learning as a result of that experience. By considering these alternative scenarios, one may learn better courses of action for use in future similar situations, or one may reinforce the original course of action. For example, after `LOVERS1`, DAYDREAMER generates the following daydream:

```text
REVERSAL1

| feel embarrassed. What if | had put on nicer clothes? | would have
gone to the Nuart Theater. | would have asked him out. He would have
accepted. | feel regretful.
```

From this daydream, DAYDREAMER learns to wear nicer clothes when going out, if she is looking to form a new relationship, as demonstrated in the following:

```text
LOVERS2

| want to be going out with someone. | feel hopeful. | have to be
acquainted with someone. | have to go to the Nuart Theater. | want
to avoid someone having a negative attitude toward me. | put on nicer
clothes. | go to the Nuart Theater.
Input: Robert Redford is at the Nuart Theater.

... External action: I ask him out.
Input: He turns you down. ...
```

It is therefore proposed that learning, rather than being a one-shot event in response to an external world experience, is an ongoing process carried out in part during daydreaming. An external experience triggers any number of daydreams from which one may learn and continue to learn if that experience is later reinterpreted in light of new experiences and knowledge.

**Daydream Production**

Are daydreams random? We assume not, at least not entirely, and therefore DAYDREAMER must deal with the following issues:

- The triggering of daydreams. What instigates a daydream in the first place?
- The generation of daydreams. Once triggered, how are the events of a daydream generated?
- The direction of daydreams. Since at any point in a daydream there may be many directions for the daydream to proceed, how is one of those directions selected?

<!-- page: 30 -->

**Representation of the Interpersonal Domain**

In order to produce daydreams, DAYDREAMER must be able to represent a number of concepts in the domain of interpersonal relations, for example: “going out with,” “breaks up with,” “turn him down,” “get even with,” and so on.

The program must have knowledge about what is required to initiate, maintain, and terminate relationships. For example, in `LOVERS1`, DAYDREAMER attempts to form a relationship with Harrison Ford through appropriate actions such as introducing herself to him, asking him out, and so on. In `RATIONALIZATION1`, DAYDREAMER recognizes that when the movie star leaves to shoot a film in Egypt, their relationship is “in trouble.” The program must be able to represent attitudes such as “interested in” and to assess and modify attitudes. For example, in `LOVERS1`, DAYDREAMER infers that the movie star does not “think much of” her; she later tells the star she likes his movies in order to get him to have a more positive attitude toward her.

**Goals and Needs**

Humans have many needs, from biological needs for oxygen, water, food, sex, and shelter, to higher-level needs, such as for self-esteem, companionship, friendship, love, and sometimes also for material goods, money, achievement, a career, and so on. Typically, when a need is unsatisfied, or when a satisfied state is threatened, a goal is activated to satisfy that need, or preserve the satisfied state. For example, in `LOVERS1`, DAYDREAMER activates goals to form a relationship and to be entertained. What collection of needs and goals should DAYDREAMER have? Is there a basic set of needs from which all other needs derive? The answer is not simple since even basic needs interact with each other. For example, satisfaction of the need for money may enable satisfaction of the shelter need; satisfaction of the need for friendship may contribute to satisfaction of the need for self-esteem.

**Episodic Memory**

Past personal and secondhand experiences are often the topic of daydreaming. In order for DAYDREAMER to daydream about such experiences, it will have to have something like a human episodic memory (Tulving, 1972). We must therefore address:

- The representation of episodes. The program must first be given an appropriate representation for previous experiences.
- The storage of episodes. The program should then be able to enter new experiences into memory, such as `LOVERS1`.

<!-- page: 31 -->

- The retrieval of episodes. The program should then be able to retrieve previous experiences appropriate to the current daydream or situation.
- The application of episodes. The program should then be able to make use of a retrieved episode, to incorporate the episode into the events of a daydream. In `RATIONALIZATION1`, for example, DAYDREAMER is reminded of a previous secondhand experience, the time Harrison Ford “had a job with Paramount Pictures in Cairo.” This experience is employed in the daydream being generated.

**Learning Through Daydreaming**

After a daydream, what remains in memory? How does what remains in memory modify subsequent behavior? We must address the following issues in machine learning via daydreaming:

- Influence of daydreaming on future external behavior. How are intentions formed in daydreaming later recalled and acted upon? How are daydreams stored in memory and later applied in generating external behavior? In `REVERSAL1`, for example, DAYDREAMER imagines she wore nicer clothes when going to the movies, and that this led to a success. Later, in `LOVERS2`, DAYDREAMER remembers to wear better clothes. How are daydreams evaluated and selected? How are realistic daydreams distinguished from unrealistic ones? For example, as a result of `RATIONALIZATION1`, should or will DAYDREAMER avoid movie stars in the future?
- Influence of daydreaming on future daydreaming. How are daydreams stored in memory and later applied in generating new daydreams? For example, suppose DAYDREAMER goes to work and her boss fires her. DAYDREAMER is then reminded of `REVENGE1`, which assists in the production of the following daydream:

```text
REVENGE3

| remember the time | got even with Harrison Ford for turning me
down by studying to be an actor, being a star even more famous than
he is, him calling me up, him asking me out, and me turning him
down. Say | am a powerful executive. Agatha offers me a job. |
turn down her offer. | get even at her for firing me. | feel pleased.
```

<!-- page: 32 -->

### 1.2.2 Creative Problem Solving

Consider the traditional paradigm for problem solving in the field of artificial intelligence: given a collection of operators, an initial state, and a description of a goal state, carry out a process of search in order to find a sequence of operators for transforming the initial state into a goal state. Programs based on this approach, such as GPS (Newell, Shaw, & Simon, 1957; Ernst & Newell, 1969) and STRIPS (Fikes & Nilsson, 1971), are given a problem to solve, perform the search in order to generate a solution or solutions, and then terminate. The generation of solutions to problems in human daydreaming, on the other hand, has three important properties which are not captured by this view of problem solving.

First, each time one daydreams about a problem, different information may be available that will enable a different and possibly better solution. That is, new solutions can be generated in light of new experiences. We propose that daydreaming occurs in the context of a memory which is constantly being updated, called a “dynamic memory” by Schank (1982). Each time a problem is examined, it may be moved one step closer to its solution; daydreaming thus enables the ongoing revision of previous solutions. For example, in `RECOVERY3`, the scheming to find out Harrison Ford’s telephone number is advanced by one step. Now DAYDREAMER needs to find out where the actor went to college. A classic example of how the production of creative products involves incremental revision is provided by Beethoven’s progressive modification of the first eight measures of the second movement of his *Third Symphony* (see, for example, Dallin, 1974, pp. 4-5).

Second, daydreaming often explores possibilities which are unrealistic or fanciful at first glance, but which can sometimes be incrementally modified into realistic, useful solutions to problems. In particular, scenarios are pursued in daydreaming which might not have otherwise been pursued either because of physical, social, cultural, or other constraints, or simply because their utility to some problem is not immediately evident. Humans daydream about possibilities such as: What would it be like if I were a laboratory mouse? What if I were on Mars? What if I found out that I was dreaming right now? What if I could fly? Daydreaming is thus a natural instance of such explicit techniques for improving creativity as “brainstorming” (Osborn, 1953) and “lateral thinking” (de Bono, 1970), which encourage one to generate wild ideas even if they seem unrelated to the problem being solved. DAYDREAMER, for example, produces the following daydream:

```text
RECOVERY2

| have to ask him out. | have to call him. | have to know his telephone
number. He has to tell me his telephone number. | have to know where
he lives. Suppose he tells someone else his telephone number. What do
you know! This person has to tell me his telephone number. He has to
```

<!-- page: 33 -->

```text
RECOVERY2 (continued)

tell this person his telephone number. He has to want to be going out
with this person. He has to believe that this person is attractive. He
believes that Karen is attractive. He is interested in her. He breaks up
with his girlfriend. He wants to be going out with her. He wants her
to know his telephone number. She tells him she would like to know
his telephone number. He tells her his telephone number. | have to
tell her that | want to know his telephone number. | call her. | tell her
that | want to know his telephone number. She tells me his telephone
number. | call him. | ask him out.
```

Here DAYDREAMER wishes to find out Harrison Ford’s telephone number. Instead of Harrison Ford telling DAYDREAMER his number, it is imagined that he tells someone else his number. Whether or not this possibility will help DAYDREAMER find out the actor’s telephone number is not clear at first. However, this possibility then proceeds to be worked into a potential solution: DAYDREAMER will get her attractive and rich, and happily married, friend Karen to ask Harrison Ford for his telephone number, and then give it to her.

Third, while daydreaming about one of several ongoing problems, one sometimes stumbles into a solution to another one of those problems. That is, serendipity, the accidental recognition and exploitation of relationships among problems, often occurs during, and as a result of, daydreaming. For example:

```text
RECOVERY3

| want to be entertained. | feel interested in being entertained.
External action: I go outside. I grab the mail.
| have the UCLA Alumni directory.
Input: Carol Burnett went to UCLA.
Input: Carol's telephone number is in the UCLA Alumni directory.
What do you know!
External action: I read her telephone number in the UCLA Alumni directory.
| succeed at being entertained. | feel amused. | have to read Harrison
Ford's telephone number in the Alumni directory. | have to know where
he went to college. | remember the time | knew where Brooke Shields
went to college by reading that she went to Princeton University in
People magazine. | have to read where he went to college in People
magazine. ...
```

DAYDREAMER receives an alumni directory from UCLA which happens to contain the number of Carol Burnett. Since DAYDREAMER still has an active goal to contact Harrison Ford, she realizes that this experience is applicable to finding out his telephone number: DAYDREAMER now has to obtain a copy of

<!-- page: 34 -->

the alumni directory from Harrison Ford’s college, if any. Even if in fact there is no such directory, this may lead to other ideas such as finding the number of his agent in the guild directory.

Sometimes a reminding and serendipity are simultaneously triggered by noticing a physical object in one’s environment, as in the following:

```text
COMPUTER-SERENDIPITY

Input: Computer.
What do you know! | remember the time Harold and | broke the ice by
me being a member of the computer dating service, and by him being a
member of the computer dating service. | knew his telephone number
by the dating service employee telling me Harold's telephone number. |
have to be a member of the dating service. He has to be a member of
the dating service. | have to pay the dating service employee.
```

```text
LAMPSHADE-SERENDIPITY

Input: Lampshade.
What do you know! | remember the time Karen thought highly of the
comedian because she thought he was funny. She thought he was funny
because he wore a lampshade.
... | have to wear a lampshade. ...
```

In each case, a physical object provided as input leads to recall of an episode related to a currently active goal of DAYDREAMER: to form a new relationship. The episode is then applied to the goal. Although either the goal or the physical object alone may be insufficient to retrieve an appropriate episode, the two in conjunction are sufficient. The solution resulting from such a combination is serendipitous because the retrieved episode is not directly related to the active problem, and was not previously known as a possible solution to that problem. For example, the episode retrieved in `LAMPSHADE-SERENDIPITY` does not involve a goal to form a relationship with someone. Rather, it involves the goal to be entertained at a nightclub. Nonetheless, it is recognized as applicable, albeit indirectly, to the goal to form a relationship with someone.

Despite the fundamental importance of creativity to human intelligence, this unique ability of humans has rarely been investigated by artificial intelligence researchers. This is understandable in light of the difficulty of the problem: creativity and the creative process are indeed quite complex. However, there are certain needless limitations of most present-day artificial intelligence programs which make creativity difficult or impossible: they are unable to consider bizarre possibilities and they are unable to exploit accidents.

Note: some notable exceptions are AM (Lenat, 1976), BACON (Langley, Bradshaw, & Simon, 1983), and TALE-SPIN (Meehan, 1976), considered in Chapter 5.

<!-- page: 35 -->

Several researchers (see, for example, Sacerdoti, 1974, 1977; Sussman, 1975) have addressed the incremental refinement of slightly incorrect or incomplete solutions. However, they have not addressed the generation and application of highly fanciful sequences of events such as those found in human daydreaming. Wilensky (1983) has proposed the use of “meta-planning” for dealing with multiple problems. Still, this mechanism does not address how a solution to one problem may accidentally suggest a solution to another problem.

**Modeling Everyday Creativity**

Our task here is not to account for the creative products of great artists, writers, scientists, and so on. Rather, we seek to address everyday creativity, so called for the following reasons: first, all humans are creative at some time or another (J. L. Adams, 1974; Wertheimer, 1945). As Polya (1945) puts it: “A great discovery solves a great problem but there is a grain of discovery in the solution of any problem.” (p. v) Second, the problems of interest in daydreaming are the everyday topics of interpersonal relations, employment, and the like (J. L. Singer, 1975). Third, creative solutions occur on a daily basis in daydreaming (J. L. Singer, 1981; Klinger, 1971, pp. 217-221; Wallas, 1926, pp. 104-105; Varendonck, 1921, pp. 213-215) and are thus not restricted to exceptional individuals. Of course, the mechanisms which we will propose for everyday creativity may also be involved in more profound forms of creativity.

The following research issues must be addressed:

- the generation of fanciful and realistic solutions
- the incremental modification of solutions
- the use of past experience in generating new solutions
- the recognition and exploitation of accidental relationships among problems
- the evaluation of solutions
- the storage of solutions
- the application of solutions in the real world

Poincaré (1908/1952) and others (Wallas, 1926) have discussed the following phenomenon in creativity: setting aside a problem for a period of time (“incubation”) seems to increase the chances for a later insight (“illumination”). How will our theory address this phenomenon?

<!-- page: 36 -->

**Perpetual Daydreaming**

Humans seem to be able to generate an endless series of novel daydreams. Although it has not reached this point so far, eventually we would like DAYDREAMER to be a sort of computational perpetual motion machine, able to daydream continuously as humans do. How much knowledge do we have to put into our program to get it to daydream endlessly or for a long time?

Of course, humans are constantly taking in new experiences which provide a new source of material for daydreaming. Even a human might run out of things to daydream about if not provided with input; subjects participating in sensory deprivation experiments report visual hallucinations (Bexton, Heron, & Scott, 1954) rather than normal daydreams. Will a daydreaming computer also require new input? If not provided with input, would the program eventually reach a limit point at which time it repeats the same daydreams over and over?

It is easy to construct a program which generates superficially different daydreams. One can imagine any number of schemes for generating daydreams which are trivial variations on previous daydreams, such as changing the names of all the characters in a previous daydream. However, constructing a program which continuously generates truly novel daydreams is very difficult. The daydreams of such a computer program must influence future daydreams in ways which are unpredictable to the programmer, since the only way for the programmer to predict an endless sequence of novel daydreams is by running the program itself.

Is it possible for any closed information-processing device to generate new and useful combinations forever? So far, of course, the universe has not exhausted its evolutionary possibilities. Daydreaming humans and computers have the benefit of outside input to enhance their daydreaming. But is it always a benefit? In what ways are creative processes aided or hindered by outside input?

### 1.2.3 Emotions

Previous artificial intelligence research in problem solving (see, for example, Fikes & Nilsson, 1971; Wilensky, 1983) has ignored the role of emotions. How might emotions be useful in a problem-solving program? Certainly it would be a disadvantage, say, for a theorem prover to become so angry about not being able to prove a theorem one way that it gave up trying other ways. But future intelligent problem-solving systems will not be directed toward single goals specified in advance by the user. Consider the example of a household robot: such a system may have a number of ongoing goals, such as keeping its users happy and safe, answering the door, answering the phone, cleaning the house, preparing meals, and so on. The system will have to activate goals on its own, in response to the environment. When several goals are active simultaneously, the system will have to decide which to work on first. Emotions are one mechanism for selecting from among multiple goals: (a) emotions determine the focus

<!-- page: 37 -->

of attention in processing, and (b) various events during processing in turn influence emotions. For example, when a problem-solving system anticipates a future failure, a negative emotion is produced. This negative emotion in turn motivates a goal to avoid the failure. This goal will compete for processing time with other goals motivated by other emotions. Emotions provide a partial alternative to other proposed mechanisms for coping with multiple goals, such as “universal subgoaling” (Laird, 1984) and “meta-planning” (Wilensky, 1983).

Previous computer models of human emotions have addressed the following: the emotional responses of a paranoid patient to a psychiatrist’s questions and the influence of emotional state on the patient’s subsequent verbal behavior (Colby, 1975); the emotional responses of a person to real-world situations and their influence on later behavior (Pfeifer, 1982); and the comprehension by a reader of the emotional reactions of story characters (Dyer, 1983b). These models all assume that a single emotional response results from an event in the world. However, in humans, an entire sequence of daydreams and corresponding emotions may result from an event, perhaps at last resting on a final emotion. One must therefore account not only for the relationship between emotions and events in the external world, but also between emotions and internal events or daydreams. Our work addresses how emotions result from imagined as well as real events.

Daydreaming interacts with emotions in a useful way: in humans, daydreaming is often concerned with the reduction or elimination of negative emotions resulting from a past failure (Varendonck, 1921, pp. 249-250; J. L. Singer, 1975; Izard, 1977, pp. 339, 398). Thus, daydreaming enables one to feel better. In effect, failures result in a form of cognitive dissonance (Festinger, 1957) which must somehow be reduced. DAYDREAMER models this human function of daydreaming: for example, in `RATIONALIZATION1` the failure is rationalized and the disappointment reduced through the generation of a daydream demonstrating that going out with Harrison Ford would be undesirable. In `REVENGE1`, DAYDREAMER achieves revenge against the movie star, resulting in a positive emotion. In `ROVING1`, DAYDREAMER shifts the topic to a more pleasant episode.

Of course, sometimes an increase, rather than a decrease, of negative emotion results from daydreaming about a failure. This is also modeled in DAYDREAMER: for example, in `REVERSAL1`, DAYDREAMER regrets not having worn nicer clothes.

**Modeling Emotions**

A potential problem arises in the computer modeling of emotions: emotions are subjective experiences felt by humans whereas computers are presumably not able to feel. An assumption of the present work is that it is possible, in principle, to model abstractly the interaction of emotions and external and internal behavior without actually getting the computer to feel, whatever that

<!-- page: 38 -->

might mean. (The philosophical issues involved here are discussed in greater detail in Chapter 10.)

Given this assumption, the following research issues must be addressed:

- Representation of emotions. How do we represent emotions such as the “embarrassed,” “angry,” and “pleased” which appear in the daydreams reproduced above?
- Initiation and modification of emotions in response to real and imagined (daydreamed) events. What rules govern the generation of emotional responses in daydreaming? In `RATIONALIZATION1`, for example, a positive emotion results from what would normally be considered a negative experience by DAYDREAMER, losing her job. Why is this?
- Influence of emotions on daydreaming and behavior. How do emotions, such as the “angry” emotion of `LOVERS1`, modify processing? If this emotion resulted in no modification of the behavior of DAYDREAMER, then there would be no reason to have it.
- Emotion regulation. What strategies do humans employ for generating daydreams such as `RATIONALIZATION1` which enable one to feel better? Why is it necessary to feel better in the first place, for humans, and possibly even for computers?

## 1.3 Foundations of DAYDREAMER

So far we have seen that daydreaming is a pervasive aspect of human experience and fundamentally related to human learning, creative problem solving, and emotions. But is not daydreaming then too all-encompassing, too difficult a research problem to undertake? In this section, we present (a) the simplifying assumptions which have been imposed in order to facilitate the difficult task of constructing a theory of daydreaming and associated computer program, and (b) a definition of the phenomenon of daydreaming to be addressed by our work.

### 1.3.1 Excluded Phenomena

Some daydreaming consists of an interior monologue (J. L. Singer, 1975) in which one comments to oneself on the current situation or attempts to solve a personal problem by silently talking to oneself. Although the output of DAYDREAMER is in English, our theory and program do not address verbal daydreaming. Rather, the program generates verbal protocols of nonverbal, conceptual, and emotional daydreaming. Modeling of the generation of protocols itself is not of primary concern to us in the present work. English output is employed to make the stream of thought easily accessible to the human observer of

<!-- page: 39 -->

the program. The English generator of DAYDREAMER functions merely as an output filter—it has no influence upon the content of daydreaming, unlike concurrent verbalization in humans which most likely does modify and slow down daydreaming (Pope, 1978). Instead of an English generator, DAYDREAMER might have employed an output module to produce graphical animations of its daydreams. For example, the representation for transfer of location (PTRANS) would result in a little person walking from one place to another on the screen; locations such as home, work, and the Nuart Theater would be represented graphically; and so on. Such output graphics would have had no impact on daydreaming. Our theory does not address mental imagery or the quasisensory experiences which are often a part of daydreaming (J. L. Singer & Antrobus, 1972). Is this simplification justified? We must distinguish the experience of imagery from imagery representations: The experience of imagery escapes objective definition as does any inherently subjective phenomenon (Dennett, 1978, pp. 174-189; Nagel, 1974). Imagery representations, however, may be defined as knowledge structures which enable operations such as three-dimensional rotation (Shepard & Metzler, 1971) or the inference of spatial relations among objects from English sentences (Waltz & Boggess, 1979). Imagery representations may be essential to certain kinds of daydreaming: an artist or architect, for example, has detailed

visual representations (Arnheim, 1974) which may enable daydreaming about artistic or architectural objects, just as a composer may have detailed musical representations (L. B. Meyer, 1956). Imagery representations do not appear essential in the domain of interest to DAYDREAMER, that of interpersonal relations. We choose to ignore the effects of the visualization of real or imaginary events—whatever those might turn out to be—and concentrate on the conceptual and abstract emotional content of daydreaming. Although the daydream protocols we have consulted in constructing our theory sometimes refer to visual images, imagery representations have not been necessary to account for such daydreams. Varendonck (1921) came to the conclusion that “the thought associations, which are rendered in words when we succeed in becoming conscious of our fancy, are the principal part of the phantasy, the visual images only the illustrations” (p. 74, emphasis removed). DAYDREAMER does not account in detail for daydreams (or external experiences) involving conversations. Nor does our theory account for daydreams involving arguments. Despite the inherently phenomenological nature of daydreaming, DAYDREAMER is not a detailed theory of the phenomenology of inner experience. Thus our theory does not account for the subjective feeling states of the stream of consciousness, including the altered state of consciousness which often accompanies daydreaming. This is a point of departure for addressing the larger problem: As we construct more and more detailed conceptual representations, we may find that we are increasingly able to account for diverse aspects of subjective experience. Another largely subjective aspect which is not addressed is

<!-- page: 40 -->

the distinction between directed, voluntary, intentional thought and undirected, involuntary, unintentional thought.

The theory does not address metacognition (Flavell, 1979) or the knowledge and thoughts people have about their own thoughts and thought processes. Although one may observe one’s daydreams and consciously develop new strategies for daydreaming, this phenomenon is not addressed in the current work. The theory does not account for development of daydreaming in childhood (J. L. Singer, 1975, pp. 123-148; Klinger, 1971; G. H. Green, 1923). However, we are concerned with learning through daydreaming. The theory does not account for nonconscious thoughts and the influence of nonconscious thoughts on the conscious stream of thought. For concreteness, DAYDREAMER is arbitrarily chosen to be a young heterosexual female. This makes little difference in the program since we make no attempt to model age, gender differences, or differences in sexual preference (although it does make a difference in the concrete details of the daydreams

which the program produces).

### 1.3.2 Definition of Daydreaming

In order to construct a computational theory of a particular phenomenon, it is necessary to have a precise definition of that phenomenon. People have different ideas about what the words and expressions “daydreaming,” “daydream,” “fantasy,” “reverie,” “stream of consciousness,” and “stream of thought” refer to. In this section, we examine previous attempts to define human daydreaming and related phenomena, and then we present our own definitions. (A detailed review of past work in daydreaming is provided in Chapter 9.) J. L. Singer (1975) describes daydreaming as a shift of attention from the external environment or task being performed toward an internal sequence of events, memories, or images of future events which have varying degrees of likelihood of occurring. Daydreaming often ends with an awakening sensation (Varendonck, 1921, pp. 154-165; G. H. Green, 1923, p. 25)—either as a result of some environmental stimulus or a sudden awareness of being lost in thought. However, as Klinger (1971) notes, daydreaming may also be freely intermixed with task-related thought—in such cases there is no clear distraction followed by daydreaming followed by awakening. In response to earlier conceptions of daydreaming as stimulus-independent and task-irrelevant thought (J. L. Singer, 1966; Antrobus, J. L. Singer, & Greenberg, 1966), Klinger (1978) points out that undirected thought—another property identified with daydreaming—may be stimulus-dependent and, conversely, directed thought may be stimulus-independent. Klinger therefore proposes that thought be characterized along the following four conceptually independent dimensions: undirected vs. directed, stimulus-independent vs. stimulus-dependent, fanciful vs. realistic, and integrated vs. degenerate.

<!-- page: 41 -->

Directedness refers to the degree to which thought is under deliberate, voluntary control. In directed thought, one has the impression that one is consciously steering the stream of thought, whereas in undirected thought the stream of thought seems to steer itself. Stimulus dependence refers to the degree to which thought is directly related to the external environment or current task situation. Realism refers to the likelihood or plausibility of depicted events or situations. Integrated vs. degenerate distinguishes regular thoughts from more bizarre, dreamlike, or nonsensical thoughts which sometimes intrude into waking thought. Degenerate thoughts are similar to the hypnagogic thoughts present at the onset of sleep (Maury, 1878; S. Freud, 1900/1965; Silberer, 1909/1951;

Foulkes, 1966). Although the classical daydream is undirected, stimulus-independent, fanciful, and integrated, daydreaming is capable of having any values along the above dimensions. It would serve no purpose to rule out, say, a directed, stimulus-dependent, and fanciful stream of thought, or an undirected, stimulus-dependent and realistic stream of thought, as instances of daydreaming. Daydreaming may be decomposed into segments consisting of a coherent

sequence of events or having distinct thematic content (Klinger, 1971).

Daydreaming shifts from segment to segment (Pope, 1978); segments vary in length and need not run to their natural completion—each segment transition is in effect a distraction from one segment to the other. Hereafter, when we speak of a daydream or a fantasy, we mean such a segment. Daydreaming, therefore, is not as homogeneous in content over time as some might think. Rather, daydreaming is similar to stream of consciousness thought (James, 1890a). However, the stream of consciousness is composed of a rich and varied collection of subjective phenomena including sensory perceptions, imagery, feelings, memories, drug-induced and meditative states, and so on (Ornstein, 1973; Tart, 1969; Pope & J. L. Singer, 1978b). For the purposes of the present work, we must limit our investigation to some well-defined subset of the contents of conscious thought. We choose a subset consisting of the following frequent components of daydreaming:

- emotional states

- recalled past events

- imagined variations on past events

- imagined future events

We rule out degenerate thoughts, imagery, and other consciousness phenomena from consideration (in accordance with the previous discussion). We also rule out metacognition. Since daydreaming is an event performed internally, an objective definition of daydreaming must refer to external behavior, in particular, to verbal protocols. (A detailed argument for this point is presented in Chapter 10.)

<!-- page: 42 -->

Accordingly, human daydreaming is defined as any sequence of thoughts reported in a verbal protocol, where thoughts are composed of self attitudes, goals, and emotions, beliefs about the thoughts of others, beliefs about world states and events, hypothetical past, present, or future thoughts of varying degrees of realism, and memories of past thoughts. This is a recursive definition: “Hypothetical thoughts,” for example, refers to hypothetical self attitudes, goals, and emotions, hypothetical beliefs about the thoughts of others, and hypothetical beliefs about world states and events; these cases correspond to daydreaming about imaginary past or future events. For simplicity, we rule out hypothetical hypothetical thoughts (i.e., hypothetical thoughts about hypothetical thoughts) and hypothetical memories (although such thoughts are possible in humans). However, memories of past hypothetical thoughts are not ruled out: This is the case of recalling prior imagined events. But memories of memories are also ruled out for simplicity. Our definition incorporates neither beliefs about self thought processes nor the notion of directed (vs. undirected) thought—two requirements for metacognition, not explored in the current work. Also, note that no mention is made of the dependence or independence of daydreaming upon current world states and events—the stimulus-dependence dimension of Klinger.

Daydreaming is restricted to verbal protocols for convenience only. We certainly would not wish to claim that a person or animal unable to communicate using language does not daydream. From other behaviors it may be possible to infer beliefs about world states and events, thoughts about hypothetical events, and so on. In fact, Griffin (1981) presents evidence which suggests that animals may daydream. He writes:

> [A] type of mental experience that may occur in non-human animals is belief that something has happened or will happen in the future.
>
> ...{A]nimals might experience thoughts roughly described in the following words: ...A cottontail rabbit, “If I run into this briar patch, that big, threatening animal won’t catch and hurt me.” ... (p. 15)

William James (quoted by Jung, 1916) writes:

> Our thought consists for the great part of a series of images, one of which produces the other; a sort of passive dream-state of which the higher animals are also capable. ... (p. 21, emphasis in original)

However, McDougall (1923) writes:

> Animals, if they are confronted by a problem, solve it, if at all, ambulando, in the course of action; they do not sit down and think out a plan.... Such planning, such purposive deliberation, would be the principal condition of the natural man’s superiority to the animals. (p. 208)

<!-- page: 43 -->

Although the phrase “stream of consciousness” is normally a broad expression which includes all the subjective phenomena of consciousness (and thus encompasses daydreaming as we have defined it), we define stream of consciousness thought here as one kind of daydreaming, in particular, as any daydreaming which contains only segments of relatively short duration. The term “scenario” is used to mean a sequence of thoughts as defined above. Some readers might use the word “daydreaming” to refer to a narrower or otherwise different range of mental phenomena. Why did we choose this word to describe the subject of our research? We might have instead called it, for example, “spontaneous fanciful and realistic past and future scenario exploration with emotional responses and recall of past experiences.” On the other hand, “daydreaming” is short, memorable, and has been used by psychologists to mean just the phenomenon we are interested in.

## 1.4 Daydream Protocol Analysis

How can we obtain data on the content of the stream of consciousness? Daydreaming is unlike most other human activities which artificial intelligence researchers attempt to model, since it is a behavior which is not visible externally. The fact that there is no apparent “I/O” while a person is daydreaming presents several problems for us: How do we know when a person is daydreaming? Given that a person is daydreaming, how can we find out what that person is daydreaming about? Under what circumstances are people able to remember their daydreams? How can we hope to record a phenomenon as varied as the stream of consciousness? In this section, we review methods for gathering data about and analyzing daydreaming which have been used in the past.

### 1.4.1 Retrospective Reports

Retrospective reports involve the description of a previous stream of thought as it is recalled from memory. Varendonck (1921) obtained many retrospective reports of his own daydreams. He would start from the end of a daydream and work backward—in a similar fashion to when two people attempt to discover how a particular topic was reached in a conversation:

> I try to retrace, step by step, all the ideas which have succeeded one another on the screen of my fore-consciousness, but not at random.
>
> Usually I start from the last link (which I at once write down) and try to recapitulate the last but one, and so on.... The whole process requires some practice.... (p. 29)

Foulkes (1985, p. 83) similarly observes that the last portion of a night dream is a more effective cue for retrieval of the remainder of the dream than other content cues.

<!-- page: 44 -->

Here is a portion of one of Varendonck’s retrospective daydream reports:

> Shall I get my book and read, or shall I continue to think about Miss X.? But what is the good of thinking about her? And what should I say to my children to explain why I prefer her to Miss Y., to whom they seem to incline? (Here my imagination reproduced before my mental eye an occurrence which took place the day before yesterday: Miss Y. is in my home with two members of her family; we see them out, and at the front door we all repeat the same remarks. This recollection is interrupted by the thought:) Miss X. looks younger than Miss Y. But the latter seems so attached to her father that I should have to detach her from him before she could really become attached to me. I might let her read some works on psycho-analysis. That might help. But how could I make her realize that I am not any more the simple teacher of fifteen years ago? For I remember that she once said in my presence that she would never marry an insignificant man. I could let her read one of my publications. I might start by sending her an order written on one of my visiting-cards that bears my academic title. By the way, I must not forget to send a card of congratulation to my friend V. on the occasion of his election as a member of the Flemish Academy. ... (Varendonck, 1921, p. 79, emphasis removed)

We assume that daydreaming consists of the continual addition (and removal) of items into short-term memory. A “memory trace” is a (possibly partial) series of snapshots of the state of short-term memory. Retrospective reports may be generated immediately, while the memory trace of the daydream is still in short-term memory, or later by retrieving the trace from long-term memory (M. K. Johnson & Raye, 1981; Ericsson & Simon, 1984, pp. 10-20). Retrieval of daydream traces from long-term memory, however, is subject to at least two problems. First, if the daydream is recalled it will be subject to the same kinds of confusions, distortions, and omissions which other memories are subject to (F. C. Bartlett, 1932; Neisser, 1967, 1982b; Loftus, 1975).

Second, it may be difficult to recall the daydream at all. Smirnov (1973) performed studies in which subjects were asked to report on their stream of thought during particular episodes (such as walking to the office) which had occurred an hour or two earlier. Most of the consciousness material which subjects were able to recall was directly related to the activity being performed at the time. However, subjects felt that other material had been forgotten. Why

might daydreams be forgotten? S. Freud (1900/1965) proposes that the forgetting of dreams—and presumably also daydreams—is caused by the resistance created by repression. A similar view is adopted by G. H. Green. Ericsson and Simon (1984, p. 151), however, propose the following account of Smirnov’s results: When the content of internal experience is unrelated to the task being performed, memory connections are not formed between the internal experience

<!-- page: 45 -->

events and the task events (especially when the task is a routine one, such as driving to work) and therefore the subject will be unable to recall those internal experiences when the task is used as a retrieval cue. They propose (p. 19) that retrospective reports be obtained immediately after a given thought stream, while it is still unnecessary to give specific retrieval cues—presumably, appropriate retrieval cues are already present in short-term memory. Varendonck (1921) similarly writes: “my memory is very deficient as regards my day-dreams, and for this reason I retrace and register them immediately if I think they may be useful.” (p. 214) (However, at other times [p. 327] Varendonck claims to have no trouble recalling daydreams.) One way of generating retrieval cues long after the fact is to ask subjects whether they can recall ever experiencing a particular daydream (for example, “have you ever imagined walking on the ceiling?”). One method of gathering retrospective reports, the 400-item Imaginal Processes

Inventory (IPI) of J. L. Singer and Antrobus (1972), operates in just this fashion.

### 1.4.2 Think-Aloud Protocols

Retrospective reports rely on memory and so are not always accurate: similar internal experiences may be confused. However, they do have the advantage of not interfering with the process itself. In the think-aloud protocols, first employed extensively by researchers interested in problem solving (Bloom & Broder, 1950), the subject verbalizes the stream of thought as it is taking place. Several investigators (Pope, 1978; Klinger, 1971; Antrobus & J. L. Singer, 1964) have used think-aloud protocols in examining spontaneous stream of consciousness thought. The experiments of Pope (1978) have suggested that think-aloud protocols may inhibit the subject or slow down and thus modify the stream of thought. Specifically, he found that the duration of thought segments relating to a particular theme or topic lasted about 30 seconds when thinking aloud, whereas when the subject was not thinking aloud, thought segments—which were indicated by pressing a button—lasted only about 5 or 6 seconds. Think-aloud protocols derive from the method of free association used by S.

Freud (1900/1965) in interpreting the night dreams of patients. In this method, patients were instructed to verbalize any thoughts coming to mind in connection with a given element in a dream, no matter how irrelevant, absurd, meaningless, or unpleasant to the patient. The purpose of free association was to uncover, and eventually remove, a pathological idea underlying the patient’s problems.

Reik (1948) gives a transcript of his own free associations, of which we reproduce a portion below in order to show the similarity between what has been called free association by psychoanalysts and what we are here calling daydreaming and stream of consciousness thought:

<!-- page: 46 -->

> What are my thoughts at this moment? I see the pussy willows on my bookcase ... a prehistoric vase ... spring, youth, old age ... regrets ... the books ... the Encyclopedia of Ethics and Religion ... the book I did not finish ... My eyes wander to the door ... A photograph of Arthur Schnitzler on the wall ... my son Arthur ... his future ... the lamp on the table ... What a patient had said about the lamp once when it was without a shade ... the table ... it was not there a few years ago ... my wife bought it ... I did not want to spend the money at first ... she bought it nevertheless ... (pp. 28-29, ellipses in original)

Reik does not specify exactly how this report was generated; it was either a “think-in-writing” protocol or a transcription of a think-aloud protocol recorded on a dictaphone. He does make it fairly clear that retrospective elaboration and modification (for the purposes of publication) were performed.

Rapaport (1951, p. 452) reports using the method of recording (presumably by writing down) the contents of his hypnagogic reveries continuously, interrupted only by temporarily dozing off.

### 1.4.3 Thought Sampling and Event Recording

One variation on the immediate retrospective report is the method of thought sampling used by Klinger (1978). In this method, subjects would carry a beeper with them during the day. When the beeper sounded at a random time, subjects would be asked to describe or rate their most recent thoughts. Descriptions would be provided in the form of a narrative retrospective report; subjects would also complete a questionnaire in order to rate their most recent thoughts along the following variables: duration of most recent thought segment, duration of next to most recent thought segment, vagueness versus specificity of imagery, amount of directed thought, amount of undirected thought, amount of detail in imagery, number of things that seemed to be going on simultaneously, visualness of imagery, auditoriness of imagery, attentiveness to environmental events, degree of recall of environmental events, degree to which imagery felt controllable, degree of trust in accuracy of memory of latest experience, usualness of latest experience, distortedness of latest experience, and time of life associated with experience. In the method of event recording discussed by Klinger, subjects are asked to report whenever a particular kind of event occurs in the stream of consciousness. Thus, for example, subjects can be asked to note whenever they have a daydream of revenge. Pope (1978) used a button-press method to indicate a new daydreaming topic.

<!-- page: 47 -->

## 1.5 Contents of the Book

The remainder of this book is organized as follows:

Chapters 2 through 7 present and discuss our computer model of human daydreaming. The major contributions of each chapter are as follows:

| Chapter | Artificial intelligence | Psychology |
| --- | --- | --- |
| Chapter 2 | Emotion-driven planning | Algorithms for modeling daydream production |
| Chapter 3 |  | Interaction of emotions and daydreaming taxonomy and strategies |
| Chapter 4 | Strategies for learning<br>Perpetual planning | Memory for daydreams |
| Chapter 5 | Serendipity-based planning and learning | Algorithms for transfer in problem solving |
| Chapter 6 | Representation of interpersonal planning knowledge |  |
| Chapter 7 | Detailed construction of program |  |

Chapter 8 presents a critical comparison of techniques for episode storage and retrieval, including discrimination nets, spreading activation, and connectionist nets.

Chapter 9 presents a comprehensive review of previous psychological work on daydreaming and the related phenomenon of night dreaming.

Chapter 10 examines some of the underpinnings, both philosophical and scientific, of the present work.

Chapter 11 presents some potential future applications of DAYDREAMER and daydreaming, suggestions for future work, and conclusions.

Appendix A presents annotated traces of the operation of DAYDREAMER.

Appendix B presents the generator used to convert program representations into English.

<!-- page: 48 -->
