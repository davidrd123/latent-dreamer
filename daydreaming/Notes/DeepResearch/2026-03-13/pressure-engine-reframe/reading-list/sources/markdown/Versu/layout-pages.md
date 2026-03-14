# Versu

Layout-preserving `pdftotext -layout` extraction.

<!-- page: 1 -->
```text
IEEE TRANSACTIONS ON COMPUTATIONAL INTELLIGENCE AND AI IN GAMES, VOL. 6, NO. 2, JUNE 2014                                                            113
              Versu—A Simulationist Storytelling System
                                                            Richard Evans and Emily Short
   Abstract—Versu is a text-based simulationist interactive drama.
Because it uses autonomous agents, the drama is highly replayable:
you can play the same story from multiple perspectives, or assign
different characters to the various roles. The architecture relies on
the notion of a social practice to achieve coordination between the
independent autonomous agents. A social practice describes a re-
curring social situation, and is a successor to the Schankian script.
Social practices are implemented as reactive joint plans, providing
affordances to the agents who participate in them. The practices
never control the agents directly; they merely provide suggestions.
It is always the individual agent who decides what to do, using
utility-based reactive action selection.
  Index Terms—Exclusion logic, interactive drama, multiagent
simulation, script, social practice.
                             I. INTRODUCTION
V      ERSU is a text-based simulationist interactive drama. It is
       available now for iPad (and soon for other devices). The
player starts by choosing an episode. At the time of writing,
the Versu platform contained episodes from three genres: we
have various episodes from a Jane-Austen-esque Regency Eng-
land, some episodes from a modern office comedy, and episodes                        Fig. 1. Screenshot of Versu in action.
from a lighthearted fantasy world. Once the player has chosen
an episode, and selected which character she wants to play, the
game starts.                                                                               “The first course is ready laid out on the table, with
   The player is presented with text and static images describing                       salmon at one end and a dish of turbot at the other, together
the current situation (see Fig. 1). There are two buttons: “act”                        with a dish of macaroni, muffin pudding, and larded sweet-
and “more.” If she presses “more,” the nonplayer characters                             breads.
(NPCs) will make decisions autonomously. She can keep                                      Lucy is at the Quinn dinner table, with their other guests.
pressing “more” if she just wants to sit back and watch the
                                                                                          The meal has only just begun, but a curious sort of un-
situation unfold. At any time, she can interrupt the autonomous
                                                                                        ease hangs over the room; a sense of apprehension whose
action and interject by pressing the “act” button. This brings
                                                                                        source she cannot identify.
up a large array of choices. She chooses an action, the NPCs
respond autonomously, and play continues.                                                  Mrs. Quinn gulps her wine down enthusiastically.
   Versu is a platform for interactive drama (focusing especially                         Mr. Quinn oversees the serving of a turtle soup from the
on lighthearted comedy of manners) and supports multiple                                great tureen in front of him.”
styles and genres of fiction. In this game-play example, from
our Regency England setting, the naive young debutante Lucy                          At this point, Lucy has several options to participate in the
and the rakish poet Brown are both dining with the Quinn                             dinner, by sipping her wine or having something to eat. She
family.                                                                              also has interpersonal options, including the ability to check out
                                                                                     other people and see whether they seem to be attracted to her.
                                                                                     She chooses to look at Brown.
   Manuscript received October 05, 2012; revised May 06, 2013; accepted
                                                                                           “Lucy silently studies Brown.
October 08, 2013. Date of publication October 25, 2013; date of current
version June 12, 2014.
                                                                                          She concludes that he cannot be wholly indifferent to her
   The authors are with Linden Lab, San Francisco, CA 94111 USA (e-mail:
richardprideauxevans@gmail.com).                                                        charms.”
   Color versions of one or more of the figures in this paper are available online
at http://ieeexplore.ieee.org.                                                       Encouraged by what she has seen, she will now give him a flir-
   Digital Object Identifier 10.1109/TCIAIG.2013.2287297                             tatious sort of look.
                         1943-068X © 2013 IEEE. Personal use is permitted, but republication/redistribution requires IEEE permission.
                             See http://www.ieee.org/publications_standards/publications/rights/index.html for more information.
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 2 -->
```text
114                                                 IEEE TRANSACTIONS ON COMPUTATIONAL INTELLIGENCE AND AI IN GAMES, VOL. 6, NO. 2, JUNE 2014
    “Lucy gives Brown a demure yet intriguing look under                      Versu is an agent-driven simulation in which each of the NPCs
  her lashes.                                                                 makes decisions independently.
                                                                                 When building an interactive narrative system, one of the fun-
    Brown smiles at Lucy in a way that conveys a strong
                                                                              damental design decisions is whether the individual agents or a
  encouragement.”
                                                                              centralized drama manager (DM) gets to decide what happens
Success! Lucy has established an initial relationship with                    [21]. At one end of the spectrum, a strong story system is one in
Brown.                                                                        which the DM makes all the decisions. The NPCs have no indi-
                                                                              vidual autonomy; they are just puppets of the DM. At the other
      “Brown rises from the table, taking his glass of wine.
                                                                              end of the spectrum is the strong autonomy system, in which
      Brown (to Lucy): You are truly a Muse!                                  the NPCs make decisions based on their own individual pref-
                                                                              erences, unaware of authorial narrative goals. Façade occupies
    Brown: I apologize, but I must go: I feel a sonnet coming
                                                                              the middle ground on this axis: a centralized DM chooses the
  on.
                                                                              next beat, but the individual agents and JBs have some limited
     He gives a flourishing parting bow. He swaggers out of                   control over how the beat is played out.
  the dining room on his way to the study.”                                      Versu, by contrast, takes the strong autonomy approach. Each
                                                                              character chooses his next action based on his own individual
Now other characters react to Brown’s departure.
                                                                              beliefs and desires. There is a centralized DM, but it is rare
     “Mr. Quinn: Perhaps Brown is sickening for some-                         indeed for the DM to override the characters’ autonomy and
  thing.”                                                                     force them do something. Instead, the DM typically operates at a
                                                                              higher level—by providing suggestions, or tweaking the desires
Now Lucy can choose how to interpret Brown’s abrupt exit.
                                                                              of the participants.
She could be dismayed by his poor manners, or intrigued by
                                                                                 Why did we choose a strong autonomy approach in Versu?
his decision to go write a sonnet. We select for her to be sad that
                                                                              There were two main reasons. First, a true simulation provides
he has left.
                                                                              much more opportunity for replayability. In Façade, many of the
     “Lucy watches Brown go with a disappointed expres-                       behaviors are hard-coded to the particular characters who are in-
  sion in her eyes.                                                           volved in them. In Façade, there is no general making cocktails
                                                                              activity. Instead, there is the particular activity of Trip making
      Frank expresses a similar concern over Brown’s health.”
                                                                              cocktails. Because the scripts and characters are entangled to-
Lucy’s sadness about Brown going means that her eating op-                    gether, it would be hugely nontrivial to replace a character in
tions become centered on expressing unhappiness.                              Façade with another (replacing Trip with Captain Kirk, say). It
                                                                              would involve rewriting most of the behaviors.
      “Lucy picks joylessly at the dish of buttered macaroni.”
                                                                                 In Versu, the social practices are authored to be agnostic about
In addition, because everyone is talking about Brown’s ill health             which characters are assigned to which roles. This means that
and strange behavior, she has the conversational option to de-                we can assign various different characters to the roles, and ev-
fend him by mentioning a positive evaluation she has made                     erything just works. In a Versu-authored version of the Façade
about him.                                                                    situation, you could play Grace, or Trip, or the guest. You could
                                                                              assign different characters to the various roles, and see what
      “She defiantly says that Brown is so handsome.”
                                                                              happens.
Because Lucy has expressed admiration for Brown, and Frank                       The simulation makes a clear distinction between roles in a
is the jealous type, we trigger another one of the situations avail-          story and the characters playing those roles. A romance might
able in this dining scene. Frank challenges Lucy about her crush.             have a hero, a heroine, a friend, and a jealous rival. The player
                                                                              is free to assign various characters to those roles. She could
    “Frank (to Lucy): Oh, enough! Pray spare us more of
                                                                              make Mr. Darcy the hero, and Elizabeth Bennett the heroine;
  your calf-eyes.
                                                                              or, she could make Mr. Collins the hero, and Miss Bates the
    Frank (to Lucy): Do you truly think that a man like                       heroine. The episode will play out very differently depending
  Brown would be so captivated by a girl like you as to marry                 on which characters are playing which roles. If there are roles
  you? Do you not realize that he has his choice of London?                   and           characters to play those roles, we have
  And he has hardly lived as a monk, I may add!”                              permutations.
                                                                                 Relatedly, the scripts in Façade assume that the player is al-
           II. A SIMULATIONIST INTERACTIVE DRAMA                              ways playing the guest, and the hosts are always played by
   Versu is an interactive drama. It is an improvisational play,              NPCs. If you wanted to rework it so that you could play Trip
rather than an interactive story. The player is encouraged to                 or Grace, this would involve a major rewrite. In Versu, because
perform her character, to improvise within the dramatic situa-                the social practices are authored to be character agnostic, you
tion that she has been thrown into. The smallest comment, the                 can play the same story from multiple perspectives. You can try
slightest look, even not saying something—these moment-to-                    out the job interview from the perspective of the interviewee,
moment actions are noticed by the other participants and ampli-               or the interviewer. You can even play it as the DM. An episode
fied. In this respect, Versu resembles Façade. But while Façade               with two roles could be played by two humans in multiplayer,
uses an architecture built around beats and joint behaviors (JBs),            one human playing either role, or both roles played by the AI.
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 3 -->
```text
EVANS AND SHORT: VERSU—A SIMULATIONIST STORYTELLING SYSTEM                                                                                          115
More generally, an episode with roles has       permutations of                 • Feedback. We added various visualizations to the user in-
player-NPC assignments. The first reason, then, that we com-                        terface (UI) so the player can see at a glance the state of
mitted to a simulationist architecture is because we wanted to                      the simulation.
maximize replayability.                                                         • Interface. We replaced the parser with a simpler menu in-
   The second reason for choosing strong autonomy is that a                         terface. The affordances provided by each social practice
simulationist architecture allows the player more control over                      are displayed explicitly to the player. She acts by clicking
the outcome. A simulated system has clear rules which the                           on a button.
player can learn and internalize. Once she has confidence that               We will go through each of these three decisions in turn. But
she has understood the internal mechanisms, she can use these                first, a point of clarification: it may look like each of these re-
to anticipate the consequences of future action, and plan how                sponses to issues in Façade are just simplifications that allowed
to achieve her goals. A nonsimulated system risks being just                 us to avoid the hard problems that Mateas and Stern were brave
a series of arbitrary puzzles, in which the player is forced to              enough to tackle head on. But these decisions were not just a
guess the changing whims of the designer. A simulation, by                   cop-out—they were pursued in order to allow us to be more
contrast, uses the same models repeatedly. The player can build              ambitious in our simulational goals. We will return to this point
up confidence that she understands the underlying system—and                 repeatedly below.
increased understanding can yield increased control.
                                                                             A. Text Output
                                                                                Façade wholeheartedly embraced the “holodeck” vision of
III. ADDRESSING THE DESIGN QUESTIONS RAISED BY FAÇADE
                                                                             interactive drama in which character behavior is rendered real-
   Some of the crucial initial design decisions in Versu were                istically in multiple modalities: 3-D characters, parallel anima-
made by looking hard at what Façade achieved. We focused on                  tion, recorded speech. This, of course, is one of the main rea-
Façade in particular because it is such a substantial achieve-               sons why it took so long to add a new behavior: it is time con-
ment. In [8], when evaluating the successes and failures of                  suming getting the animations blending correctly and achieving
Façade, Mateas and Stern mention three outstanding issues in                 synchronization with other actors.
particular.                                                                     Now there is nothing wrong with realistic rendering of char-
   • The speed of content production and global agency. Be-                  acters, but neither is it necessary. We agree with Salen and Zim-
     cause of the intricate animation overlaying and parallel                merman [26] that the level of immersion does not necessarily
     behaviors which needed to be authored for most actions,                 increase with extra levels of realism. A text output can be just
     adding a new piece of content to Façade was a time-con-                 as immersive as a 3-D animated environment. To think other-
     suming task. In the end, after three plus years in develop-             wise is to be seduced by what Salen and Zimmerman call the
     ment, they only had time to author 27 beats. The amount of              “immersive fallacy.” Versu does not use fancy 3-D animation
     global agency (the ability for the player to affect the overall         or voice actors; the output is dynamically generated text and
     arc of the story) is limited by the amount of content, so in            static images. Text output certainly makes it quicker to produce
     the end, the player did not have as much ability to affect              behavior (later, we will describe how we managed to produce
     the outcome of the story as the authors had initially hoped.            an order of magnitude more behaviors than Façade in a shorter
   • Feedback. Façade involved three social “head games,”                    time frame). But that is not its only advantage. It is not just that
     played one after the other (an affinity game, a hot-button              text is cheaper than 3-D animation; it is also more expressive.
     game, and a therapy game). By design, the state of each                    1) Text as an Expressive Medium—Interiority: Before we
     game was not communicated directly (via numbers of                      started developing Versu, when working on The Sims 3, we
     spreadsheets or sliders), but indirectly by gesture and tone            came across a revealing situation which highlighted the advan-
     of voice. (The authors wanted to maintain the sense that                tages of text output for revealing interiority. There was a chron-
     this was a drama rather than a computer game.) But this                 ically shy Sim who was hosting a party. Some of the guests
     design decision made it very hard for the player to tell the            had rung the door, and were waiting to be ushered in. The shy
     state of the simulation.                                                Sim was sitting on the couch, deciding what to do. Debug-
   • Interface. In Façade, the player can type any text she                  ging his internal state, we could see that he was conflicted: the
     wishes. But the parser will attempt to shoehorn all the                 norms of social propriety dictated that you should answer the
     player’s sentences into one of 30 parametrized discourse                door when invited guests come over. But his own chronic shy-
     acts. Unfortunately, the player’s utterance cannot always               ness gave him a strong countervailing reason for not answering
     be fitted into one of these 30 specific actions, and even if it         the door: he very much wanted to be alone. Within the deci-
     could, the parser often cannot see how it could. The player             sion-making system that we were using, the Sim had a hard
     can feel like she is fighting the parser, rather than using it          choice between answering the door and refusing to do so. But
     effortlessly as a tool to communicate with.                             neither of these options captured what the Sim wanted to ex-
We tried to make sure that Versu had good answers to the three               press: what should have happened is that the Sim answered the
issues which Mateas and Stern identified.                                    door reluctantly. Here, we express his internal conflict through
   • The speed of content production. To speed up content                    an adverbial modifier.
     production, we eschewed Façade’s 3-D procedurally an-                      Now, in a 3-D game with animated polygonal characters,
     imated characters for (procedurally generated) text and                 adding an adverbial modifier to an action is a hugely expensive
     static images.                                                          process: we would need a separate animation for answering the
     Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 4 -->
```text
116                                                      IEEE TRANSACTIONS ON COMPUTATIONAL INTELLIGENCE AND AI IN GAMES, VOL. 6, NO. 2, JUNE 2014
door reluctantly, and we would also need a separate walk cycle
for reluctant walking. Given a large set of animated actions, each
adverbial modifier we add would require a prohibitively large
number of additional animations. But in a text game, adverbial
modifiers are much cheaper: we can modify a verb by simply
appending an adverb to the sentence. Adverbial modifiers are
useful in many ways: we can use them to express internal state,
to express the reason for action, and also to express individual
personality.
   2) Text as an Expressive Medium—Individuality: We wanted
each character in Versu to have a unique personality and we
wanted their individuality to be expressed throughout their
actions. Text output made it feasible for each character to have
a unique text override for many actions. For example, in most
3-D games, each character uses the same generic walk cycle.
But in Versu, each character has an individual way of walking:
Brown swaggers, Frank Quinn walks ponderously, George
Wickham strides, Lady Catherine hobbles, while the pug dog
waddles.
B. Feedback
   When designing ways to help the player understand the social
simulation, we were guided by Wardrip-Fruin’s concept of the
                                                                                     Fig. 2. Each character’s emotional state is displayed along with explanatory
Sim City effect [34]. When playing a game which simulates                            text.
some aspect of experience that the player is already familiar
with, the player starts by using her own model of how it works.
But the simulation will inevitably diverge from reality in various                   the affordances around the practices which initiated them. For
ways. If things go badly, the divergence between the player’s                        example, if the player’s character is in the middle of a dinner
understanding of the phenomenon and the simulation’s model                           party, and Brown has just made a rude remark, there will be two
of the phenomenon will prevent the player from understanding                         social practices running concurrently: the dinner party (pro-
or manipulating the system. The Sim City effect occurs when                          viding affordances to eat, drink, etc.) and the current conver-
the user interface helps the player to transition from her original                  sation (providing affordances to disapprove of Brown, forgive
model of how the thing actually works to how the simulation                          him, etc.). The affordances are arranged in categories, grouped
models it. If this works properly, the player ends up with an                        by the social practice that instantiated them, so that the player
accurate model of how the simulation models the phenomenon,                          begins to understand the underlying simulation state. The text
without having had to read a manual or a textbook.                                   for each social practice is carefully worded to display its cur-
   Our simulation is based on fine-grained emotional states, re-                     rent state.
lationships, and social practices. We made sure that the user in-
terface exposed these to the player transparently.1
   To help the player understand the characters’ moods and re-                                         IV. ARCHITECTURE OVERVIEW
lationships, we added a portrait of each character at the bottom                        Our simulation is built up out of two types of objects: agents
of the screen (see Fig. 2). Each character has various emotional                     and social practices (see Fig. 3).
states he can be in (based on Ekman’s typology [4]), and each                           A social practice describes a type of recurring social situation.
character has a different portrait for each emotional state. When                    Some social practices (e.g., a conversation, a meal, a game) only
the player clicks on a character portrait, she sees why that                         exist for a short time, while others (e.g., a family, the moral
character is in that particular mood: each character remembers                       community) can last much longer.
who the emotion is directed toward (e.g., I am annoyed with                             A practice coordinates agents via the roles they are playing.
Brown), and the event which prompted the emotional change                            For example, a greeting practice sees the two participants under
(e.g., Brown’s insult).                                                              the descriptions of greeter and recipient.
   Our simulation is unusual in that there are multiple indepen-                        The main function of the social practice is to describe the
dent social practices running concurrently (this is described in                     actions the agents can do in that situation. A greeting practice,
detail below). To help the player understand the state of the                        for example, tells the greeter how he can greet the recipient. It
various social practices that are currently in play, we organized                    also tells the recipient the various ways she can respond.
                                                                                        The practice provides the agent with a set of suggested ac-
   1A danger with exposing the simulation internals is that the experience starts
                                                                                     tions, but it is up to the agent himself to decide which action
to seem less like a drama and more like a computer program. We worked hard
to make sure that the emotions, relationship states, and social practices were ex-   to perform, using utility-based reactive action selection. This is
posed in a way that kept the player immersed within the world we were creating.      described in Section IX.
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 5 -->
```text
EVANS AND SHORT: VERSU—A SIMULATIONIST STORYTELLING SYSTEM                                                                                                           117
Fig. 3. The architecture of the simulator. Data is placed in rectangles, and processes in ovals. This diagram shows the information flow when an NPC makes a
decision. The same architecture is used for player choice—except the Action Instances are sent directly to the user-interface, rather than to the Decision Maker.
   In Versu, we allow multiple practices to exist concurrently.                   trary persistent data,2 while the only memory a state machine
During a dinner party, for example, there will be multiple prac-                  has is the state it is in.3 Second, the only possible effect of a fi-
tices operating at once:                                                          nite-state machine’s action is transitioning from one state to an-
   • eating and drinking (and commenting on the meal);                            other. A Versu action can do much more than change the state
   • the conversation about politics;                                             of the practice: performing an action can result in any sentence
   • the rising flirtation between Frank and Lucy;                                being added to the world database. The results of adding new
   • responding to the fact that Mr. Quinn has spilled the soup.                  sentences can be that relationships are updated, new beliefs or
Each of these practices provides multiple affordances. The                        desires are formed, old practices are deleted, or new practices
agent’s set of options is the union of the affordances from each                  are spawned.
of the practices he is participating in.
                                                                                    2The whist game, for example, stores which cards have been assigned to which
   Some practices are organized into states, so that they can pro-
                                                                                  players, which suit is trumps, whose turn it is to play, and the score. See [10] for an
vide different affordances in different situations. But a social                  early description of how practices need their own memory to “keep score.”
practice is significantly more powerful than a finite-state ma-                       3Because the finite-state machine’s memory is limited by the number of states,
chine, in two main ways. First, each practice can store arbi-                     it is not Turing complete.
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 6 -->
```text
118                                                        IEEE TRANSACTIONS ON COMPUTATIONAL INTELLIGENCE AND AI IN GAMES, VOL. 6, NO. 2, JUNE 2014
                         V. THE ARCHITECTURE                                           or pointers, as traditionally conceived. Representing the world
                                                                                       state as a set of sentences has a number of advantages.
    In this diagram, boxes represent data and ovals represent pro-
                                                                                          • Visibility. The entire world state is completely open to in-
cedures which operate on that data.4
                                                                                             spection. Nothing is hidden. If you want to know, for ex-
    We will start from the top of the diagram and work downward.
                                                                                             ample, if there is anyone in the current simulation state who
Creating an episode involves three types of scripts:
                                                                                             is in the same room as someone they dislike, you just need
   • scripts defining the social practices that can be instantiated
                                                                                             to form the query. There is no need to ever write code to
       during the episode;
                                                                                             access the state of the world because the entire state of the
   • scripts defining the initial state of the characters that may
                                                                                             world is represented in a uniform manner and is already
       be participating in the episode;
                                                                                             open to view.
   • a script defining the initial world state.
                                                                                          • Debuggability. You can place logical breakpoints to detect
All scripts are authored in a high-level domain-specific-lan-
                                                                                             which practice is responsible for making a fact true. This
guage designed specifically for this simulation: Praxis.5
                                                                                             is much more powerful than traditional code breakpoints
    When the episode starts, we execute the world initialization
                                                                                             or data breakpoints.
function, and then execute the character initialization function
                                                                                          • Serializability. Because the world state is represented in a
for each character who is selected to participate in the episode.
                                                                                             uniform manner, it is trivial to serialize and deserialize the
At this point, the database contains the initial world state.
                                                                                             world state.
    Social practices can be parameterized by arguments, so they
                                                                                       The main advantage is visibility. We found, in previous simu-
can be multiply instantiated with different values for the argu-
                                                                                       lations we have worked on, that the main factor which makes it
ments. Each practice type specifies a set of actions together with
                                                                                       hard to improve the quality of the AI is the difficulty in seeing
their preconditions. The actions available to a character at any
                                                                                       the entire simulation state. Bugs lurk in the darkness. In this
given moment are determined by all the actions of the currently
                                                                                       architecture, where the world is a set of sentences, nothing is
instantiated practices whose preconditions are satisfied.
                                                                                       hidden.
    Agents score each available action (see Section IX) and then
                                                                                          The simulator comes with a runtime inspector, which gives
execute the highest scoring action. Actions may modify the
                                                                                       developers complete access to the internal state of the simula-
database and/or generate text.
                                                                                       tion. The inspector allows them to:
    The diagram is almost symmetric, with social practices on the
                                                                                          • find out what is true;
left-hand side, and characters on the right-hand side. The reason
                                                                                          • print all facts about an object, an agent, or a process;
it is not entirely symmetric is that there are multiple social prac-
                                                                                          • find all instantiations of a term with free variables (e.g.,
tice instances for each social practice type (e.g., there may be
                                                                                             find me everybody who Brown has annoyed);
two instances of the whist game practice type occurring simul-
                                                                                          • find out why an action’s preconditions have failed;
taneously in two different rooms), but there is only one character
                                                                                          • find out what is causing a fact to become true.
instance for each character file.
    The diagram does not call out the DM explicitly. This is be-
cause each episode’s DM is implemented as a special type of                            B. Sentences, Practices, Agents, and Affordances
social practice. A DM is not a new type of entity: it is just a par-
                                                                                          The world is a set of sentences in a formal logic. Sentences
ticular type of practice.6
                                                                                       which contain the distinguished process keyword make prac-
                                                                                       tices active. Sentences containing the distinguished agent
               VI. HOW WE REPRESENT THE WORLD                                          keyword makes agents active. These practices provide affor-
                                                                                       dances to the agents. When an agent chooses to perform one
A. The World Is Everything That Is the Case                                            of these affordances, the world state is changed: sentences are
                                                                                       added and removed from the database. The database changes
   In the Praxis system, the simulation state is entirely deter-                       can change the set of available practices, and the loop begins
mined by a set of sentences in a modal logic.7 The set of sen-                         again. In summary:
tences is the complete simulation state. There are no objects,                            • sentences being true make practices and agents active;
  4Sometimes if a procedure is used in multiple ways (e.g., the function inter-           • practices propose affordances to agents;
preter which sometimes interprets a world initialization function and sometimes           • performing affordances updates sentences in the database
a character initialization function), that procedure is drawn twice, to make it
easier to see the flow of data through the system.                                          (which might mean that different practices are now avail-
   5Praxis contains a number of decisions which are logically independent:                  able, providing different affordances).
1), the decision to model social practices as first-class objects; 2) the use of a
strongly typed logic-formalism to model simulational state; and 3) the use of
exclusion logic as the logic of choice.                                                                     VII. EXCLUSION LOGIC
  6The DM is described in Section XI.
   7A modal logic, in the broad modern sense, is a logic which contains non-              The world state is defined as a set of sentences in a logic called
truth-functional operators for talking about relational structures (see [1, p. xi]).   exclusion logic [5]. This modal logic is particularly well suited
Previously, the term “modal logic” was restricted to logics which treat the stan-
dard modalities of necessity, possibility, knowledge, belief, time, deontic modal-     for modeling simulation state in general, and social practices in
ities, etc.                                                                            particular.
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 7 -->
```text
EVANS AND SHORT: VERSU—A SIMULATIONIST STORYTELLING SYSTEM                                                                                                     119
  Given a set of symbols, the literals8 in exclusion logic are                         A related advantage of exclusion logic is that we get a
defined as all expressions of type in                                               form of automatic currying [32] which simplifies queries. If,
                                                                                    for example, Brown is married to Elizabeth, then we might
                                                                                    have “brown.married.elizabeth” in the database. In exclusion
                                                                                    logic, if we want to find out whether Brown is married, we can
We use the “!” and “.” operators to build up trees of expressions.                  query the subterm directly; we just ask if “brown.married.” In
For example:                                                                        traditional predicate logic, if married is a two-place predicate,
   • brown.sex!male: Brown is male;                                                 then we need to fill in the extra argument place with a free
   • brown.class!upper: Brown is upper class;                                       variable. We would need to find out if there exists an      such
   • process.dinner.dining_room.participant.brown: Brown is                         that “married(brown, X).” This is more inefficient as well as
     one of the people having dinner in the dining room;                            being more verbose.
   • process.dinner.dining_room.participant.lucy: Lucy is one
     of the people having dinner in the dining room.                                B. Automatic Removal of Invalid Data
Asserting that        is claiming both and one of the ways in                          The exclusion operator supports the automatic cleanup of
which is . Saying that            , by contrast, is to say that is                  data which is no longer referenced.10 For example, a social prac-
the only way in which is the case. The semantics of exclusion                       tice might have two states and . State might have two
logic are described in [5].                                                         pieces of information and . State might have three pieces
   The two things that distinguish exclusion logic from tradi-                      of information , , and . Being in state would be repre-
tional predicate logic are:                                                         sented as
   • the ability to directly represent tree structures;
   • the exclusion operator.
A. Exclusion Logic Literals Represent Tree Structures                               State    would be represented as
   Consider the following facts about Brown:
   • brown.sex!male;
   • brown.class!upper;                                                             Now, if we are in state (because the statement          is true),
   • brown.in!dining_room;                                                          and we switch to state (by inserting      into the database), all
   • brown.relationship.lucy.evaluation.attractive!40;                              the local data from state are automatically removed from the
   • brown.relationship.lucy.evaluation.humour!20.                                  database according to the update rules for exclusion logic.
This is a declarative representation of a tree structure, imple-
mented as a trie [9]. A group of shared literals has a shared                       C. Simple Postconditions
prefix (in this case, “brown”), and the subtree can be referred                        When expressing the preconditions and postconditions of an
to directly by its prefix. The subtree can be removed in one                        action, STRIPS11 has to explicitly describe the propositions that
fell swoop by deleting its associated prefix. For example, we                       are removed when an action is performed:
can remove all of the facts about Brown by deleting the term
“brown.”9 A prefix (referring to a subtree) is the Praxis equiva-                   action move(A, X, Y)
lent of an object in an object-oriented programming language.                         preconditions
                                                                                        at(A, X)
   The structure of literals allows us to express the lifetime of                     postconditions
data in a natural way. If we wish a piece of data to exist for                          add at(A, Y)
just the lifetime of an object , then we make the prefix of be                          remove at(A, X).
the prefix of . For example, if we want the beliefs of an agent
to exist just as long as the agent, then we place the beliefs inside                STRIPS finesses the frame problem [12] by using the
the agent:                                                                          closed-world assumption: anything that is not explicitly
                                                                                    specified as changing is assumed to stay the same. But there is
mr_collins.beliefs.clergymen_should_marry                                           still residual awkwardness here: we need to explicitly state that
                                                                                    when moves from to , is no longer at . It might seem
Or if we want the state of a game to exist just as long as the                      obvious to us that if is now at , he is no longer at ; but
game itself, then we place the state inside the social practice for                 we need to explicitly tell the system this. As Orkin writes [19]:
the game:
                                                                                        “It may seem strange that we have to delete one assign-
process.whist.data.whose_move!brown                                                   ment and then add another, rather than simply changing the
                                                                                      value. STRIPS needs to do this, because there is nothing in
   8The database (representing the current world state) is just a collection of
ground literals. But the queries that can be expressed in the Praxis language are
                                                                                      formal logic to constrain a variable to only one value.”
all propositions of type , using the familiar connectives
                                                                                    Exclusion logic is a formal logic designed to express directly
                                                                                    the natural idea that certain variables can only have one value.
  9Compare this with prolog, where it is much harder to remove all sentences
                                                                                      10This is a form of simplified belief revision, or garbage collection.
containing a particular symbol. You can remove all predicates of a certain arity,
but you would have to separately remove the various different groups of predi-        11These comments apply equally to the planning domain definition language
cates of different arities.                                                         (PDDL).
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 8 -->
```text
120                                                  IEEE TRANSACTIONS ON COMPUTATIONAL INTELLIGENCE AND AI IN GAMES, VOL. 6, NO. 2, JUNE 2014
The interpretation of the exclusion operator means we do not                                           VIII. SOCIAL PRACTICES
need to specify the facts that are no longer true:
                                                                               A. Regulative Versus Constitutive Views of Social Practice
action move(A, X, Y)                                                              At the heart of the technical architecture is a commitment to a
  preconditions
    A.at!X                                                                     constitutive view of social practices. We will explain what this
  postconditions                                                               means by contrasting it with the alternative regulative view of
    add A.at!Y
                                                                               social practices.
When using exclusion logic, the postconditions are shorter and                    1) The Regulative View of Social Practice: Imagine a group
less error prone. The “!” operator makes it clear that something               of agents, each with his own set of goals and available actions.
can only be at one place at a time.                                            At any decision point, an agent chooses the action which best
                                                                               satisfies his goals (or expected goal satisfaction, once we take
D. The Exclusion Operator Helps the Author                                     probabilities into account).
Specify Her Intent                                                                In this individualist picture, a social practice is just a set of
                                                                               restrictions on available actions which allows us to collectively
   The semantics of the exclusion operator remove various
error-prone bookkeeping tasks from the implementer. But                        increase our utility. For example, the driving-on-the-left prac-
perhaps the exclusion operator’s main benefit is that it allows                tice restricts our actions (we can no longer drive on the right).
the Praxis scriptwriter to specify her intent more precisely.                  We are prepared to accept this limitation on our freedom be-
When we specify that, for example12:                                           cause it lowers the probability of a collision.
                                                                                  2) The Constitutive View of Social Practice: According to
A(agent).sex!G(gender).                                                        the regulative picture, agents could act before they participated
                                                                               in practices. They already had goals, and already understood
We are saying that an agent has exactly one gender. This ex-
                                                                               what actions were available to them. These were already given.
clusion information is available to the type checker, which will
                                                                               All the social practices do is allow us to solve various coordi-
rule out any piece of code which suggests that an agent can have
                                                                               nation problems.
multiple genders.
   Some modern logic-programming languages are starting to                        The constitutive view (first articulated explicitly by Rawls
add the ability to specify uniqueness properties of predicates                 [20])15 rejects this assumption. According to the constitutive
[31], but they treat uniqueness properties as metalinguistic pred-             view, the action is only available within the practice. A nice ex-
icates. Praxis is the first language to treat exclusion as a first-            ample from Rawls’ original paper is the game of Chess: you
class element of the language.                                                 can move a carved piece of ivory from one square to another,
                                                                               but you can only move your pawn to King 4 if you are playing
E. Support Tools for Praxis                                                    Chess. Again, we can utter a series of noises which sounds like
                                                                               “Ay doo,” but this only constitutes a marriage vow in the con-
  A domain-specific language needs a number of support tools
                                                                               text of a wedding ceremony. The action is only available in the
before it becomes really usable for production work. Praxis
comes with a number of tools13 to make the scriptwriter’s life                 practice.
easier.                                                                           One way to see the need for a constitutive view of practice
  • A type-checking system to find errors early. Praxis is                     is to consider the vast array of actions we could possibly do
     strongly, but implicitly typed14: the author does not have                now. Sitting here right now, we could lend the stranger on our
     to specify the types of all variables; instead, the system                left 10 pounds; we could tell the other stranger on our right that
     will infer the types of all variables and complain if a                   Paris is the capital of France; we could ring up our spouse and
     consistent assignment cannot be found.                                    enumerate the prime numbers.
  • An inspector for giving the author runtime access to the                      With an infinite number of actions available to us, why are we
     precise state of the simulation.                                          not overwhelmed with choice? How do we ever find the time to
  • A playback facility. The game stores the exact set of ac-                  make a decision?
     tions chosen by the player during a game, and writes them                    In the constitutive view of practice, the affordances are al-
     to a file. This allows the author to reproduce exactly a pre-             ways embedded in the practices. The agent does not see the ac-
     vious playthrough. This playback feature required that we                 tion as available unless he is already participating in the practice
     kept the simulator fully deterministic at all times.                      which makes it visible. The agent is not overwhelmed by an infi-
  • A stress-test tool which runs hundreds of instances of the
                                                                               nite number of choices because he only sees the affordances that
     game simultaneously, with all characters controlled by the
                                                                               are provided by the social practices he is in. It is this constitutive
     computer. By running multiple instances of the game at
                                                                               model of social practices which lies at the heart of our simula-
     high speed, we are able to find bugs and anomalies quickly.
                                                                               tion. In our implementation, we take this idea literally: every
  12This is a typing judgement in Praxis, saying that a variable    (of type   affordance is contained within a practice and is only available
agent) has a unique sex (of type gender).
  13When designing the inspection tools, we were heavily indebted to the so-
                                                                               if that practice is instantiated.
phisticated authoring tools in Inform 7.                                          15But this view has a long history and can certainly be traced back to Wittgen-
  14Compare ML and Haskell.                                                    stein [29] and Heidegger [3], and arguably to Hegel [2] and beyond.
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 9 -->
```text
EVANS AND SHORT: VERSU—A SIMULATIONIST STORYTELLING SYSTEM                                                                                              121
B. Role Agnosticism                                                            • quantified preconditions (both universal and existential
                                                                                 quantifiers can be nested arbitrarily);
   Tomasello [33] has argued that one of the critical differences
                                                                               • expression evaluation (the ability to perform numeric
between the great apes and humans is that, although both apes
                                                                                 calculations);
and humans can participate in practices, only humans are able
                                                                               • domain axioms (the ability to define new predicates and
to effortlessly switch roles. As soon as a human infant (as young
                                                                                 relations in terms of existing relations);
as two) participates in a practice, she adopts a bird’s eye view of
                                                                               • conditional effects in postconditions.
the practice, which means she is able, after participating once,
to suggest we play again, with roles reversed. Other creatures               D. Respecting the Normative
do not exhibit the ability to play a practice from multiple roles.
A great ape can participate in many sorts of practice, but he is                Many of the practices we authored had their own individual
locked in to the role which he played. He cannot see the practice            sense of the normative. During a conversation, you should re-
from a bird’s eye perspective. This is a distinguishing feature of           spond when spoken to, you should respect the salient topics, etc.
humans.                                                                      [25], [30]. During a meal, you should be polite about the food,
   One of our big goals in Versu was that each practice would                etc. But the player still has a choice; she can always violate the
be role agnostic: it would be authored without knowing which                 norms if she wants to. The major requirements for modeling the
roles were being played by NPCs and which by human players.                  normative were that:
                                                                                • NPCs understand what they should and should not do;
C. Representing Social Practices                                                • NPCs, if left to their own devices, should respect the norms
                                                                                   (unless they have some particularly acute personality de-
   A social practice is a hierarchical collection of affordances,                  viation which overrides their urge to respect the social
providing various options to its participants (who are character-                  mores);
ized solely in terms of the roles they are playing).                            • but the player should be free to violate these norms at any
   A social practice is represented with the keyword process.                      time;
Processes are specified with declarations, for example:                         • if the player violates a norm, the others should notice and
                                                                                   respond accordingly.
process.greet.X(agent).Y(agent)                                              To get NPCs to respect norms, we add postconditions to norm-
  action “Greet”
    preconditions                                                            violating actions to mark that a norm has been violated. We sim-
      // They must be co-located                                             ilarly add postconditions to actions which should be performed
      X.in!L and Y.in!L                                                      to mark that a requirement has been respected. We give most16
    postconditions
      text “[X] says ‘Hi’ to [Y obj]”                                        agents strong desires to respect the social norms. When deciding
end                                                                          what to do, the agents will see the consequences of their actions.
                                                                             If they see a norm-violation consequence, that will be a major
Here the term associated with the process is:                                disincentive.
                                                                                Although NPCs typically do not violate norms, the player is
process.greet.X.Y.
                                                                             allowed to do whatever she likes. If the player does step outside
The processes can then be instantiated any number of times by                the bounds of propriety, the NPCs should notice and respond ac-
adding sentences to the knowledge base. For example, if we add               cordingly. Getting drunk, insulting the wine, refusing to answer
the following assertion:                                                     when spoken to, all these norm violations are only fun to play
                                                                             if they are noticed. When a norm violation occurs, the practice
process.greet.jack.jill                                                      which kept track of the norm spawns a subpractice whose job
                                                                             is to mark that a violation has occurred. This responsive prac-
then the process will be active with substitutions [jack/X, jill/Y].         tice will provide options to the others: disapproving, forgiving,
If, furthermore, Jack and Jill are in the same location:                     getting angry, and even (in extreme circumstances) evicting the
                                                                             character altogether.
jack.in!hill
jill.in!hill.
                                                                                                            IX. AGENTS
Now the preconditions of the action are satisfied and the greet
action will be available to Jack on Jill. If we added another term:          A. Autonomy
                                                                                When an agent is deciding between various possible actions,
process.greet.jill.jack                                                      he looks at the consequences of each action, and chooses the
                                                                             action which best satisfies his desires. He uses forward chaining,
then another instance of the process will be active with substi-
                                                                             rather than goal-directed backward chaining.17
tutions [jill/X, jack/Y], and the greet action will be available
to Jill on Jack.                                                               16The DM will occasionally lower these desires for certain agents (e.g., our
   The language for describing actions has a number of the fea-              rakish poet, Brown) when it wants them to behave outlandishly for dramatic
                                                                             purposes.
tures described in PDDL [14]:                                                  17SHOP [17] uses a similar approach: “Since SHOP always knows the com-
  • disjunctive preconditions;                                               plete world-state at each step of the planning process, it can use considerably
  • negation in preconditions (using negation as failure);                   more expressivity in its domain representation than most planners.”
     Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 10 -->
```text
122                                                        IEEE TRANSACTIONS ON COMPUTATIONAL INTELLIGENCE AND AI IN GAMES, VOL. 6, NO. 2, JUNE 2014
   This is a form of utility-based reactive action–selection [11],                     cards the whist player can play, the decision maker computes
[23], rather than a full-blown planner. But it is an unusual utility-                  the various features of a move (whether it counts as winning
based method in that it is highly responsive to the fine details of                    the trick, whether it counts as throwing away a card, trumping,
the simulation.                                                                        getting rid of a suit, etc.). These conditional effects determine
   In most systems which use utility-based decision making, the                        the score of playing the card. Using such simple appraisals, and
agent’s estimation of the consequences of the action is much                           giving the NPCs suitably weighted desires to perform actions
simpler than the actual consequences. For example, in The Sims,                        which satisfy these appraisals, is all that is needed for the NPCs
when a Sim decides to go to the toilet, the actual consequences                        to automatically play a strong game. There is no need for any
of the action are:                                                                     separate sui-generis whist-playing decision procedure.
   • routing into the bathroom;                                                           Our short-term planner elegantly handles large dynamic sets
   • if there is someone already in the room, he expresses frus-                       of goals, allowing characters to select actions that advance mul-
      tration, and exits;                                                              tiple goals simultaneously.
   • otherwise, he locks the door, sits down, and relieves his                            Utility is computed by summing the satisfied desires. A want
      bladder motive.                                                                  is a desire to make a sentence true, and that sentence can be any
These are the actual consequences. But the estimated conse-                            sentence of exclusion logic, to any level of complexity. So, for
quences are much simpler. When considering going to the toilet,                        example, Brown (our rakish poet) likes annoying upper class
all he sees about the future is:                                                       men. He wants it to be the case that there exists an other such
   • he will relieve his bladder motive.                                               that:
This discrepancy (between the actual and estimated conse-
quences) creates all sorts of issues. One particularly aggravating                     Other.sex!male and
                                                                                       Other.class!upper and
problem is that the estimated consequences miss all the condi-                         is_displeased_with.Other.brown.
tional effects: effects which may or may not happen depending
on various other aspects of the simulation state. For example,                         Each want comes with an associated utility modifier. This want
whether the going to the toilet will be successful depends on                          is tagged with a utility modifier of 20. Every separate instantia-
whether there is somebody already in the bathroom. But the                             tion of this desire gives an additional 20 score. If, for example,
Sim does not consider this when planning: he thinks the action                         Brown can see that one remark would simultaneously annoy
will always be successful. If there is, in fact, someone else in                       three upper class men, then that remark would score three times
the bathroom, the Sim will attempt to use the toilet, but he will                      higher than a remark that just annoyed one.
be thwarted by the other person. Then, he will try to choose
another action; his bladder motive will still be unsatisfied, and                      B. Individuality
he will attempt to use the toilet again, etc. This behavior can
                                                                                          When attempting to model individual personalities, one
repeat indefinitely.18 Now there are various kludges we can put
                                                                                       common method is to implement a small finite set of person-
in to avoid this particular problem. But the best way to fix this
                                                                                       ality traits, and define a character as a combination of these
general class of problems is to address the root cause: instead
                                                                                       orthogonal traits [6]. We wanted a more expressive system, in
of using a simplified estimation of the consequences, compute
                                                                                       which there were an infinite number of personalities—as many
the actual consequences for decision making.
                                                                                       personalities as there are sentences in a language.
   The NPCs in Versu look at the actual consequences of an
                                                                                          The fact that the planner looks at the entire simulation state
action when deciding what to do. When considering an action,
                                                                                       means that the range of things that agents can desire is very
they actually execute the results of the action, rather than some
                                                                                       wide. Some examples of individual desires are:
crude approximation. Then, they evaluate this future world state
                                                                                          • The doctor is sexist: he wants      leader!       sex!male;
with respect to their desires. Then, we undo the consequences
                                                                                          • Brown enjoys annoying upper class men: he wants
of the world state.19
                                                                                                   is_displeased_with brown          sex!male     class!
   This sort of decision making is broad rather than deep. It does
                                                                                             upper;
not look at the long-term consequences of an action, but at all
                                                                                          • Peter does not like to be alone: he hates it when
the short-term consequences. By looking at a broad range of fea-
                                                                                             peter.in!            character        peter not in! ;
tures, it is able to make decisions which would typically only be
                                                                                       The expressive range of the logic is what allows us to specify
available to long-term planners. For example, the NPCs are able
                                                                                       such fine-grained personalities.
to play a strong game of whist. When considering the various
   18The Sims is not an isolated example. FEAR contains exactly the same dis-          C. Relationships
crepancy between the planner’s understanding of the action effects and the ac-
tual game-play effects. In [19], notice the two separate fields in the action class:      We use role evaluation (based on Sacks’ membership catego-
m_effects for the planner’s understanding of the effects, and the ActivateAc-          rization devices [24]) to model many different sorts of relation-
tion() function for the actual game-play effects.
   19It is only because we are able to undo actions that this approach is work-
                                                                                       ships described.
able. The language instruction primitives of the Praxis language were designed            At any moment, we are simultaneously participating in many
to be efficiently undoable. If we could not undo an action, the only way we could      different practices. In Pride and Prejudice, Darcy, for example,
compute the actual consequences would be by making a copy of the entire sim-           is simultaneously a member of the gentry, a friend of Mr. Bin-
ulation state, performing the action in that copy, and then throwing it away. But
copying the entire simulation state is prohibitively expensive when we are con-        gley, a potential suitor. For each role he is playing, one crucial
sidering so many actions for so many agents.                                           question is how well he is playing that role. Different people
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 11 -->
```text
EVANS AND SHORT: VERSU—A SIMULATIONIST STORYTELLING SYSTEM                                                                                             123
have different evaluations of how well someone is playing a                             • their effusiveness in looking after somebody depends on
role.                                                                                     their evaluation (e.g., the Bingley sisters are less effusive in
   This concept, role evaluation, is at the heart of the relation-                        their concern for Jane because they do not rate her highly).
ship model. Just as agents can be participating in multiple social                      1) Updating Role Evaluations: Characters can acquire eval-
practices concurrently, just so an agent can have multiple con-                     uations in three ways:
current views about another agent.                                                      • they can start with the evaluation hard coded into them;
   Some of the role evaluations we use for our Jane Austen                              • they can acquire the evaluation when they interpret one of
episodes include:                                                                         the other’s actions;
  • how well bred someone is;                                                           • they can hear someone else’s evaluation and decide to be-
  • how properly he is behaving;                                                          lieve it.
  • how attractive someone is (evaluating someone as a poten-                       Characters acquire new evaluations of others when they see
      tial romantic partner);                                                       them performing actions, and interpret those actions in a par-
  • how generous, intelligent, amusing, etc., he is;                                ticular light. These interpretations are themselves actions which
  • how well they are performing their familial role (father,                       the characters decide to do: they have a choice how to interpret
      mother, daughter, etc.);                                                      others’ actions.
  • whether they are a good husband or wife.                                            2) Relationship States: Relationship evaluations are multiple
In Pride and Prejudice, characters evaluate each other ac-                          and asymmetric: may judge according to multiple different
cording to their success at these various roles:                                    roles, and ’s views on may not be the same as ’s views on
  • the Bingley sisters find Elizabeth lacking in style, taste,                       .
      beauty;                                                                           But as well as these multiple views, we also model a
  • they judge Jane to have low family connections;                                 single symmetric notion: the public relationship state between
  • Mrs. Bennet judges Charlotte to be plain.                                       the characters. This is the official long-term stance between
Characters remember the reason for these evaluations20:                             the characters: whether they are friends, lovers, siblings, or
  • Mr. Bingley is a good suitor because he was so affable at                       enemies.
      the dance;
  • Mr. Darcy is a bad suitor because he was rather rude at the                     D. Reactions
      dance;
  • Jane is a bad catch because her family is so badly                                 In The Sims, there is a curious design asymmetry. The player
      connected.                                                                    chooses how to act, but not how to react:
Characters make these evaluations public when prompted (or                             • the player chooses which action her Sim performs;
unpromptedly):                                                                         • but when another character does something to the player’s
  • Mrs. Bennet says Darcy is a bad match for Elizabeth on                                Sim, the player does not get to choose how to react; her
      account of his rudeness;                                                            Sim reacts automatically.
  • the Bingley sisters say Jane is a bad match for Mr. Bingley                     In Versu, we wanted to restore the symmetry, allowing the
      on account of her low connections.                                            player to choose how to react as well as how to act.
These public evaluations can be communicated from one char-                            For example, when Lucy recounts her art lessons from an at-
acter to another:                                                                   tractive teacher, there are many ways of interpreting her remark.
  • Mr. Wickham tells Elizabeth that Darcy is dishonorable                          A character who admires Lucy may jealously resent the impli-
      and she believes him.                                                         cation that she was attracted to someone else. One attuned to
Sometimes, people disagree about their evaluations of a                             issues of status may realize that Lucy has been sent to an expen-
character:                                                                          sive private seminary, and respond by congratulating her on her
  • Elizabeth and Jane disagree about whether Mr. Wickham’s                         superior education—or irritably tell her not to brag about her-
      dark hints mean that Darcy is blameworthy.                                    self. Another might simply proceed with a conversation about
These evaluations affect the characters’ autonomous behavior:                       schooling, art, or romance, riffing on the topics brought up by
  • characters will be invited over if they are evaluated suffi-                    Lucy’s remark.
      ciently highly;                                                                  We choose how to perceive the world, and we are responsible
  • characters will propose if they evaluate the other as a suit-                   for how we perceive it. One of our guiding design goals was
      able match;                                                                   that these interpretations should themselves be interpretable by
  • characters will display gratitude for doing favors (e.g., the                   subsequent interpretations. So if we decide that Lucy was being
      wife and daughters are grateful to Mr. Bennet for paying                      improper to talk about her art teacher in that way, our decision to
      Mr. Bingley a visit).                                                         perceive her action that way is itself evaluable by others. They
These evaluations also affect the tone of their autonomous                          may, for instance, decide that I am being prudish. And this in-
behavior:                                                                           terpretation of my interpretation is itself evaluable, and so on.21
  • Miss Bennet is not gracious when Mrs. Bennet apologizes                            Reactivity is implemented as a type of social practice. When
      because she is not valued highly;                                             an action is performed, a social practice is spawned. This re-
                                                                                    acting practice proposes various different sorts of responses to
   20The evaluations are stored inside the agent in terms of the form Agent.rela-
tionship.Evaluated.role.Role!Value!Explanation.                                       21This is something that Sacks [24] has emphasized.
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 12 -->
```text
124                                                 IEEE TRANSACTIONS ON COMPUTATIONAL INTELLIGENCE AND AI IN GAMES, VOL. 6, NO. 2, JUNE 2014
the initiating action. When a response is performed, this re-                 take another mistress, or focus on improving his poetry? The
sponse is just another action, which can itself spawn subsequent              author designs the character arc to give the character choices
reactive practices, and so on. Each of these responsive practices             about what he wants to be, not just what he wants to do.
is short-lived and destroys itself if anything more recent hap-                  The character arc also specifies a variety of possible epilogs
pens, to prevent agents reacting to “old news.”                               for the character. Each epilog describes the end state of the game
                                                                              from the perspective of that character’s defining life choices. If
E. Emotions                                                                   Brown decides to distract himself from his malaise by taking
   We used a fine-grained set of emotional states, based on                   another mistress, this could end up with him ditching her, or with
Ekman’s work [4]. An advantage of a text representation was                   him deciding—to his own great surprise—that he will remain
that it was easy to express the various emotional states. It would            committed to her after all. The character arc has its own sense of
be much harder to express the fine-grained distinction between,               drama, separate from the story: it is here that the character may
for example, being embarrassed and humiliated if we had to                    achieve self-awareness, sink into despair, or transform himself.
show it in a 3-D face. In order for an emotional state to be                     True character transformation comes about in the moments
intelligible to another, the character should be able to explain              where a character decides to set aside an old want, adopt a new
it. To do this, the character remembers who the emotion is                    one, or act in contravention of his own desires. This ability—the
directed toward (e.g., I am annoyed with Brown), and the event                ability to choose an affordance that is not what the character
which prompted the emotional change (e.g., Brown’s insult).                   simulation would prefer—is not available to NPCs; it is only
   An agent changes emotional state when performing an ac-                    available to the player. The natural result of this is that the char-
tion. Reactions, in particular, are a rich source of emotional state          acters controlled by humans have opportunities for change and
changes.                                                                      development, while the characters controlled by NPCs will not
   Our representation of agents’ emotional states is simple and               exercise those opportunities; instead, they will continue to play
straightforward: the agent has only one emotional state at a time,            supporting roles.
and any new emotional state always overrides the previous one.                   The mixing of character arcs with episode story management
The agent also remembers his previous emotional state, so that                also produces a productive interference when it comes to the
we can have autonomous decisions based on mood switching.                     meaning of the stories as they are experienced and interpreted by
An agent, for example, may not like to laugh when he is already               the player. For instance, the story manager of the murder mys-
in a bad mood.                                                                tery might dictate that the characters can identify and confront
                                                                              the murderer and then choose either to turn him in to the law or
F. Beliefs                                                                    to help conceal his guilt. These might be the only possibilities
                                                                              recognized by the episode structure. However, different motiva-
   The world state is shared among the agents. We do not, for                 tions might manifest themselves as a result of the character arcs.
memory reasons, give each agent his own separate representa-                  For instance, if the character who discovers the murderer’s guilt
tion of the world. Instead, we give them all access to the one                is betrothed to the murderer’s son, this presents a motivational
authoritative world model. This means, of course, that misun-                 question to the player: Should she push to convict out of a love
derstandings, etc., cannot be implemented fully.                              of justice, knowing that this will swamp the family in scandal,
   For specific cases where we want false beliefs, or factual dis-            lead to the end of her engagement, and cause a disappointing
agreements, we store individual beliefs for that specific issue.              end to her personal story arc?
So, for example, early on in the ghost story, when there are var-                Such dilemmas as these are not explicitly modeled by Versu;
ious spooky (but inconclusive) happenings, the characters can                 rather, they fall out of the creative interference between charac-
disagree about whether these events are caused by a ghost or                  ters who have personal motivations and desires for the outcome,
there is some more scientific explanation. Another example is                 and narrative cruxes that force dramatic change.
that, typically, interpersonal evaluations are shared and acces-
sible to all. But if we want some people to know—and others to
be unaware—that Frank loves Lucy, then we store this as a be-                 X. THE CORE MODEL OF BELIEFS, EMOTIONS, RELATIONSHIPS,
lief, and gate certain actions on whether the actor believes that                              AND EVALUATIONS
    loves , rather than on whether it is true that loves .
                                                                              A. Interpractice Communication via the Core Model
G. Character Arcs
                                                                                 Social practices typically track internally one or several vari-
   The most complex part of a character description file is the               ables pertinent to that practice: for instance, a dinner practice
character arc: a story arc for that individual character, describing          might keep track of how many guests had been served wine, or
how his objectives and emotions change over time. This arc ref-               which course it was; a whist practice might track which agents
erences only facts about that individual character, so it can be              were playing in the game, what cards they had been dealt, and
brought into any story in which the character is placed. Our phi-             which suit was trumps.
landering poet, Brown, for example, might choose to take an-                     This kind of internal information, however, does not allow the
other mistress, but once he has seduced her, we might want him                different practices to communicate with and affect one another.
to start to feel bored and trapped once again. If a social practice           Instead, the agent information described above—consisting of
provides choices about external low-level actions, the character              relationship states, beliefs, emotions, and evaluations—serves
arc represents internal high-level choices: Does Brown want to                to communicate between practices.
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 13 -->
```text
EVANS AND SHORT: VERSU—A SIMULATIONIST STORYTELLING SYSTEM                                                                                              125
   To be effective, that is, to cause repercussions for other char-            • Changes to long-term qualities should be strongly marked.
acters and changes within the story, a character’s actions within                If a player becomes someone’s friend, flirt, enemy, etc.,
Versu need to change one or more of these core model elements.                   that event should entail several moves of interaction and
   Changes to emotional states might last for a few turns and                    be marked out clearly by the user interface. (Currently,
might affect the way the character is described doing things (and                making a friend, a flirt, or an enemy is an achievement
her appearance on screen), but a character will experience many                  which is strongly noted when it occurs.) These events
emotions in the course of a game. Unless a given mood affects                    are key to the story and should be noted as significant
one of the longer term decisions, its unlikely to determine how                  accomplishments.
the story ends.                                                                • Relationship state changes should require active choice
   By contrast, character evaluations of others play into rela-                  from both characters. It should never be possible for an
tionship state decisions. A character who evaluates her flirt as                 NPC unilaterally to change his or her relationship to the
cruel or bumbling will have the opportunity to begin a “breaking                 player, though the NPC might offer the player an unavoid-
up” practice, which will end the romantic relationship state be-                 able choice, either prong of which will have a significant
tween these characters and produce a negative “rejected flirt”                   state-changing outcome (e.g., “marry me or we will break
relationship state instead. It does not matter to the core model                 up”).
how the character’s flirt made himself unacceptable: it might be
that he clumsily spilled his wine at dinner, or stepped on her foot
                                                                             C. Using the Core Model With Autonomous Agents
during a dance, or said something inconsiderate to her mother.
It might be that he committed one large transgression or a series               The use of autonomous agents also introduces an additional
of smaller ones. Regardless of the originating practice, when the            design goal into the system. As explored above, each agent
character’s evaluation of him becomes too negative, the oppor-               may have arbitrarily complex preferences. Lucy likes to eval-
tunity to break up will present itself.                                      uate other characters positively. Miss Bates likes to be in a good
   Relationship states and character self-evaluations are the                mood. Mr. Quinn likes to have a bad opinion of Frank Quinn.
most significant and lasting part of the core model.                         However, these preferences can only produce significantly dif-
   During play, a relationship state may function to make par-               ferent behavior between agents if the range of available affor-
ticular actions available: for instance, the practice allowing a             dances, and the effect of those affordances, is sufficiently great.
couple alone together to kiss will become functional only if the             Otherwise, agents will be comparatively scoring just a handful
pair are in a romantic relationship state.                                   of options, and they are unlikely to produce distinct behavior,
   In addition, each character in the drama begins with certain              even if their formulas for scoring differ widely. This produced
relationship or self-belief goals. For instance:                             an additional design goal:
   • Miss Bates begins wishing to be friends with someone;                     • individual actions should ideally produce change to mul-
   • the poet Brown, who has a chip on his shoulder about his                     tiple qualities or types of quality.
      illegitimacy, may have a desire to form an inimical rela-              This is especially easy to demonstrate with respect to conver-
      tionship with an upper class man;                                      sation. A given line of conversation may accomplish any of the
   • Lucy starts out hoping either to find a protector or else to            following tasks:
      become more confident in herself.                                        • shift the speaker to a new belief about the world;
   Succeeding or failing to achieve these goals affects story                  • communicate a belief;
outcomes, as each story concludes by narrating both how the                    • shift the speaker to a new emotion;
extrinsic episode ended (Was the murderer identified? Was he                   • communicate one or more emotions;
tried?) and how the character arc went for the player character                • shift the speaker to a new evaluation of another character;
(Did Elizabeth conclude the story engaged to Mr. Darcy?).                      • communicate an evaluation of another character;
                                                                               • shift the speaker to a new self-evaluation;
B. Using the Core Model to Promote Player Agency                               • communicate the self-evaluation of the speaker;
   Most of the player’s agency in the game, therefore, comes                   • count as an atomic action to which others may react.
from using the affordances made available by the social prac-                For example, Mr. Collins may speak about how Lady Catherine,
tices to affect the core model in some fashion. In order to max-             his patroness, has complimented the sermons he preached at her
imize this sense of agency, we identified the following design               parish. This quip22 is marked to do each of the following things:
goals.                                                                         • communicate that Mr. Collins has a positive self-evalua-
   • Player actions should be rewarded either with information                    tion about intelligence, inviting listeners to accept or reject
      about the world state or changes to the world state.                        this view of Mr. Collins;
   • Changes to short-term qualities should happen frequently.                 • communicate that Mr. Collins admires Lady Catherine’s
   • Changes to short-term qualities should lead to a chance                      status and patronage, inviting listeners to accept or reject
      for the player to back down or persist in an attempt. For                   this view of Lady Catherine;
      instance, if the player’s character has evaluated another as             • shift Mr. Collins to the “pleased” emotional state, because
      somewhat attractive, this should lead to opportunities to                   he enjoys dwelling on the compliments he has received.
      either flirt with or ignore the object of affection, allowing             22Dialog is authored in units called quips, which are combinations of a text
      the player to indicate whether it is really her intention to           template together with a collection of possible effects on social and emotional
      try to develop a relationship there.                                   state.
     Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 14 -->
```text
126                                                 IEEE TRANSACTIONS ON COMPUTATIONAL INTELLIGENCE AND AI IN GAMES, VOL. 6, NO. 2, JUNE 2014
Likewise, Lucy might say, “Oh no! I’m afraid there must be a                  music, whist. But it does not mind which particular activity, so
ghost here!” when she has not previously believed in ghosts.                  it spawns different practices on different occasions, leading to
That quip would have the following effects:                                   significantly different runthroughs.
   • shift to a belief in ghosts, changing Lucy’s mentality on                   Our story managers are significantly less ambitious than some
     this point for the remainder of the game;                                systems [22]. They do not plan ahead, anticipating the narrative
   • communicate a belief in ghosts, allowing other characters                consequences of various dramatic moves, scoring each move
     to accept this belief or rebut it;                                       according to narratological criteria. Instead, our story managers
   • shift to a fearful emotion;                                              are reactive processes, handcrafted for each episode. This gives
   • communicate a fearful emotion, allowing other characters                 the author strong control over the outcome and quality of the
     to offer Lucy comfort;                                                   story, at the expense of emergence at the narrative level.
so that it would be possible for other characters to respond by
reassuring her, telling her that she is superstitious to believe such               XII. RELATED THEORY: COMPUTATIONAL MODELS
a thing, or agreeing that there probably is a ghost.                                             OF SOCIAL PRACTICE
   Coding speech and other social actions in this way allows                  A. Schank and Abelson’s Scripts
agent-driven characters to make a more nuanced use of their
various wants. It also produces texts in which characters appear                 Schank and Abelson’s work on scripts [27], [28] has been
to react to both surface and subtext, as in this final example:               hugely influential. They were one of the first in the AI commu-
Mrs. Elton might say, “What a handsome parlour you have; it is                nity to articulate the important idea that an individual action is
almost as graciously appointed as my sister’s!” This quip would               not intelligible on its own: its intelligibility comes from the so-
accomplish each of the following:                                             cial practice in which it is embedded. They used the term script
   • communicate a moderately positive status view of the                     to describe a computer model of a routine social practice: eating
     person addressed;                                                        at a restaurant, traveling on a bus, visiting a museum. A script
   • communicate a superior status view of her own family;                    is a state graph containing a distinguished path which is marked
   • constitute a “be complimentary” action.                                  as “normal.” (For example, in the restaurant script, the normal
Some characters might respond to the “compliment” by                          path involves the customer ordering, eating the meal, and then
thanking Mrs. Elton; others might reply by being self-depre-                  paying for it.)
cating about their status or by putting down Mrs. Elton’s family                 The script describes coordination of multiple actors: charac-
in order to correct her self-evaluation that they disagreed with.             ters were assigned to roles and the script understood which ac-
                                                                              tions were expected for each role in each state. The script also
                                                                              achieves continuity over time: an individual agent’s sequence of
                     XI. THE STORY MANAGER                                    actions over time is intelligible as a sequence of causally linked
                                                                              actions as the script travels through various states.
   Unlike some other systems [22], Versu does not have a gen-
                                                                                 Schank and Abelson’s theory accommodates the important
eral-purpose DM which dynamically combines story elements
                                                                              idea that multiple scripts can be running concurrently, and can
to produce a wide variety of different stories. Instead, each indi-
                                                                              interfere with each other. One example they give is:
vidual episode has its own individual story manager, which en-
codes the author’s understanding of the narrative goals for that                   “John was eating in a dining car. The train stopped short.
particular episode. A particular story (say, a murder mystery)                   John’s soup spilled.”
has certain key recognizable moments (the victim makes him-
                                                                              Here, the eating script and the being-on-a-train script are run-
self unpopular, the victim is killed, the body is discovered), and
                                                                              ning concurrently. A problem in the train script then causes an
the story manager is responsible for making sure these events
                                                                              interference in the eating script.
happen at the right moment.
                                                                                 Schank and Abelson’s work on scripts was a major source of
   The story manager for an episode is a high-level director who
                                                                              inspiration to us. But our model of social practice is different
does not like to micromanage. Given a stock of characters and a
                                                                              in a number of ways. First, Schank and Abelson used scripts as
set of social practices, all our story manager likes to do is initiate
                                                                              a way of understanding natural language stories, while we use
practices, watch their progress, and insert occasional changes.
                                                                              social practices as a way of guiding autonomous behavior in an
It leaves the performance of those practices, and the individual
                                                                              interactive system. Second, Schank and Abelson use so-called
decisions, to the individual autonomous NPCs.
                                                                              “scruffy” methods to model social practices (conceptual depen-
   The story manager is a reactive process (itself implemented
                                                                              dency theory is a graph-based representation without a formal
as a social practice). It starts by creating characters and placing
                                                                              semantic), while we model the entire simulation state declar-
them in initial social situations. Once these characters have
                                                                              atively using a modal logic. (In Versu, we tackle “scruffy”
been given some interesting goals, it often leaves those au-
                                                                              research problems with “neat” methods.) Third, a Schankian
tonomous characters to their own devices for some time, before
                                                                              script describes a social practice from a particular perspective:
the next intervention. The story manager watches what is going
                                                                              from the viewpoint of one distinguished role. Schank and
on, spawning new social practices, and tweaking individual
                                                                              Abelson are explicit about this [27, p. 210]:
goals, to keep things moving.
   For example, in the murder mystery, the story manager                            “A script must be written from one particular role’s point
wants, after the meal has finished, for the people to gather to-                 of view. A customer sees a restaurant one way; a cook sees
gether to perform some sort of group activity: reading together,                 it another way.”
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 15 -->
```text
EVANS AND SHORT: VERSU—A SIMULATIONIST STORYTELLING SYSTEM                                                                                                127
Again, [28 p. 152]:                                                            2) Virtual character autonomy. Does each individual char-
                                                                                   acter make up his own mind, or is each character merely a
    “A script takes the point of view of one of these players,
                                                                                   puppet controlled by a centralized DM?
  and it often changes when it is viewed from another
                                                                             In terms of authorial intent, Versu is somewhere in the middle
  player’s point of view.”
                                                                             between a manually authored choose-your-own adventure, on
In Versu, by contrast, a social practice is authored from a bird’s           the one hand, and an automatically generated emergent narra-
eye perspective: the domain-specific language supports an au-                tive, on the other hand. Each episode in Versu comes with its
thoring style in which practices are agnostic about which par-               own episode-specific DM: a reactive agent which mostly sits
ticular character is playing which role. In Versu, a restaurant              back and watches, occasionally intervening to push the narra-
script is written once and incorporates both the customer’s and              tive forward. In this respect, Versu is rather like Façade: for each
the cook’s perspectives.                                                     episode, there is a specific situation which has been carefully au-
                                                                             thored, but the exact path taken through the narrative landscape,
B. Moses and Tenenholtz’s Work on Normative Systems                          the how and the why, is up to the player to determine.23
                                                                                In terms of character autonomy, Versu is strongly simula-
   Moses and Tenenholtz [16] have also developed a computa-                  tionist. Each character chooses his next action based on his own
tional model of social practices. They define a normative system             individual beliefs and desires. It is very rare indeed for the DM
as a set of restrictions on available actions such that, when these          to override the characters’ autonomy and force them to do some-
restrictions are respected, there is a guarantee that a certain de-          thing. Instead, the DM typically operates at a higher level, by
sirable condition obtains. For example, on a busy road, the de-              creating new social practices, or tweaking the desires of the
sirable condition might be that no cars hit each other, and the              participants.
restriction might be that all cars drive on the right-hand side of              Unusually, the DM is also modeled as an autonomous agent,
the road. Part of the power of their work is that, using a type              and chooses which (metalevel) action to do based on her own
of modal propositional logic, they can prove that certain norms              (metalevel) desires. This means that, in some episodes, the
guarantee certain desirable properties.                                      player can actually be the DM.
   Their approach is related to ours in that they use formal logic
to describe social systems. But there is one fundamental differ-
                                                                             A. Comparisons With Façade
ence: they see social systems as restrictive rather than constitu-
tive. Imagine an agent who already has a set of available actions.              1) Playing the Same Scene From Multiple Perspec-
A normative system, in their sense, provides a restriction on the            tives: Whereas Façade chose a middle ground between
set of actions. The restriction on the agent’s freedom is offset             the story-driven and agent-driven approaches, Versu is much
by the (provable) desirable properties of everyone obeying that              more heavily agent driven and simulationist. At the heart of
restriction: forcing me to drive on the right is a restriction on            Versu’s simulation of social practices is a distinction between
my ability to drive on the left, but the guarantee that I can drive          the roles in the social practice and the characters who are
without collision offsets that restriction. Versu, by contrast, uses         assigned to those roles. Because the player can play different
a constitutive model of social practice in which social practices            roles in a situation, and can assign many different permutations
make new actions available: placing a card down on the table                 of characters to the roles, a Versu situation has much more
only counts as trumping with the Jack of Spades within the con-              variation and replayability than the Façade scenario. If we
text of the whist game in which it is embedded.                              were to implement a version of the Façade scenario in Versu,
                                                                             the player would not be constrained to playing the guest—she
            XIII. RELATED INTERACTIVE SYSTEMS                                could also play Trip or Grace. Further, in a Versu version of
                                                                             Façade, the player could assign different characters to the roles.
   Starting with Tale Spin [15], there have been many attempts               The player could assign, say, Mr. Darcy to play the male host,
to generate narrative using a simulationist agent-driven archi-              and Elizabeth Bennett to play the female host, and see how
tecture. The recurring problem with these attempts has been that             differently it plays out.
the generated stories lacked narrative coherence. It has proven                 2) The Difference Between JBs and Social Practices: A
very hard for an author to achieve any sort of narrative con-                story-driven interactive narrative lets a single DM determine
trol by fiddling with the parameters of individual agents. When              what happens next. (This provides continuity at the cost of
building Versu, we were hoping to find a spot in design space                emergence.) At the opposite extreme, an agent-driven interac-
that has the generativity of simulation, while also having a sat-            tive narrative lets each individual agent determine what he will
isfying degree of narrative coherence and author ability.                    do next. (This provides emergence at the cost of continuity.)
   In a recent paper [21], Riedl and Bulitko provided a clear                One of the most striking architectural ideas in Façade is the use
taxonomy for describing design choices in interactive narrative              of JBs which coordinate a group of agents and are intermediate
systems. The following are the two fundamental questions that                between individual agents (on the one hand) and a single
they considered.                                                             DM (on the other hand). A JB can express synchronization
  1) Authorial intent. To what extent does the human author’s                between its participants and can enforce continuity between
      storytelling intent constrain the narrative? How much of                  23Versu’s DM is rather less ambitious. Façade models a sense of rising tension
      the story is decided in advance by the author, and how                 and chooses the next beat by finding the one which most closely matches the
      much is generated by the player and computer during play?              intended current tension. We do not do this.
     Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 16 -->
```text
128                                                      IEEE TRANSACTIONS ON COMPUTATIONAL INTELLIGENCE AND AI IN GAMES, VOL. 6, NO. 2, JUNE 2014
individual behaviors. But a JB, as Mateas and Stern use it, is
more restrictive than a social practice in that:
   • a JB is a way of coordinating NPCs—there is no equiva-
      lent of a JB for coordinating PCs—or for coordinating a
      mixture of PCs and NPCs;
   • when an NPC is deciding whether to enter a JB, this de-
      cision is not based on his individual personality or de-                       Fig. 4. Different ways of handling three aspects of character autonomy.
      sires. Deciding whether to join in depends only on whether
      the NPC can participate (whether he has an individual be-
      havior which matches the specification of the JB), and not                     (Praxis) was a very high-level declarative language for creating
      on whether he wants to.24                                                      content. A high-level declarative language can express behavior
A social practice is like a JB in that it is responsible for coordi-                 more compactly than a procedural language.25
nation and continuity. But it is more general in two ways. First,
a JB coordinates NPCs only, while a social practice can coordi-                      B. Comparisons With Prom Week
nate both NPCs and PCs together. Second, a social practice does                         “Prom Week” [13] is a social simulation of high-school stu-
not wrest control from the individual agent, instead it always re-                   dent social life developed by a group at the University of Cal-
spects the individual agents’ autonomy. It provides suggestions,                     ifornia San Diego (UCSD, La Jolla, CA, USA).26 At a high
but leaves it up to the individual agent what to do. In Versu, the                   level, Prom Week has a lot in common with Versu: they are both
social practice is a coordinating entity which is intermediate be-                   aiming to provide interesting individual characters in dramatic
tween individual agents and a DM, but this coordinating entity                       situations. But at the ontological level, there are some funda-
does not wrest control from the individual agents. In Versu, it is                   mental differences.
always the individual agents who decide what to do.                                     Activity in Prom Week involves a sequence of discrete speech
   What this discussion shows is that the broad question of vir-                     acts. In Versu, individual actions are coordinated by social prac-
tual character autonomy turns out, on closer inspection, to be                       tices which provide meaning to sequences of actions. For ex-
divided into three separate questions.                                               ample, a game of whist involves a sequence of actions by mul-
   • What sort of entity controls decision making?                                   tiple participants. These actions make sense as a whole because
   • What sort of entity provides coordination between agents?                       they are all contributing to the one unifying practice: the game.
   • What sort of entity provides continuity over time between                          A concrete example is as follows: suppose one character sug-
      the actions of a single agent?                                                 gests to some others that they retire to the drawing room to
Façade answers all three questions in the same way: the JB                           listen to some music. Now in Prom Week, this request would
provides coordination, continuity, and is also responsible for                       be an individual speech act: others would accept or reject this
making decisions. In Versu, there are separate answers to these                      proposal, and then the speech act would be over. But in Versu,
questions: the social practice provides coordination and conti-                      this suggestion is part of a larger social practice: the group de-
nuity, while the individual agent makes the decisions.                               ciding what they should do next. This larger practice involves
   3) The Cost of Content Production: The scenario in Façade                         the group as a whole achieving consensus (or failing to achieve
takes about 20 min for the player to complete. This episode                          consensus) on what they should do. Others may agree with the
took three plus years to create. A comparable 20-min length                          proposal to listen to music, or they may suggest an alternative
episode in Versu takes about two months to create. Versu also                        pastime (reading, dancing, whist). Others may take sides, at-
contains longer episodes. The ghost story, which takes 45–60                         tempt to dominate, or back down. In Versu, the simple request
min to complete, took six months to produce.                                         speech act is embedded within a larger practice which gives it in-
   In terms of the number of behaviors, Façade has 30 parame-                        telligibility and provides continuity. In Prom Week, by contrast,
trized speech acts created during the three plus years in devel-                     behavior is just a sequence of isolated and unrelated speech acts.
opment. Versu, by contrast, has more than 1000 parameterized
actions, authored in one year in development.                                        C. Situating Versu in Design Space
   We attribute our faster content production time to two main                          We conclude this section by relating Versu to other games in
factors. First, using text output rather than 3-D animated                           terms of the three separable aspects of character autonomy (see
characters saved us a lot of production time. Second, the                            Fig. 4).
domain-specific language in which we authored behaviors                                 • What sort of entity controls decision making?
   24See [7, p. 75]: “If all agents respond with intention to enter messages, this
                                                                                        • What sort of entity provides coordination between agents?
signals that all agents in the team have found appropriate behaviors in their           • What sort of entity provides continuity over time between
local behavior libraries.” The only way in ABL for an individual agent’s per-              the actions of a single agent?
sonal preferences to affect the decision to join in is if that preference is added      In a choose your own adventure (CYOA), there is one entity
as an explicit precondition: “Note that the preconditions can also be used to
add personality-specific tests as to whether the Woggle feels like playing follow    (the static preauthored story graph, functioning as a nonreactive
the leader” [7, p. 78]. As Mateas later acknowledges, “A possible objection            25Façade is authored in ABL, a procedural domain-specific language built on
to pushing coordination into a believable agent language is that coordination
should be personality-specific; by providing a generic coordination solution in      top of Java.
the language, the language is taking away this degree of authorial control” [7,        26We were advisors on this project, and have had many fruitful discussions
p. 226].                                                                             with the developers over the years.
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 17 -->
```text
EVANS AND SHORT: VERSU—A SIMULATIONIST STORYTELLING SYSTEM                                                                                           129
DM) which decides what happens next, and provides coordina-                        • beliefs about others’ beliefs, e.g., “Mr. Quinn believes that
tion between agents and continuity between action.                                   Lucy believes that Mrs. Quinn is the murderer.”
   In The Sims, the individual agent decides what to do next. Co-
ordination between agents is limited to individual speech acts.27                C. Limitations of Our Representation of Social Practices
In The Sims, there is no continuity between actions over time.                      1) Simplifying Assumptions of the Social Model: Our model
   Prom Week is similar in that decisions about what to do are                   of social practices simplifies in two ways. First, the agents have
always made by the individual agent. Coordination between two                    a shared understanding of the state of the social practices. It
agents lasts for the duration of an individual speech act and in-                is not possible in this model for two agents to have divergent
volves an initiating sentence followed by a response. Again,                     understandings of the state of the situation (for example, dis-
there is no continuity between actions over time.                                agreeing about whose move it is in a game of Chess). Instead of
   Façade uses a DM to decide what happens next. It uses JBs,                    modeling each individual agent’s beliefs about the state of the
described above, to achieve coordination between agents, and                     practice, we just model the practice once, and give agents access
continuity over time.                                                            to it. Second, even if we did give each agent his own individual
   Versu uses the social practice to achieve coordination and                    model of the practice, so that they could diverge, there would
continuity, but always lets the individual agents decide what to                 still be a shared understanding of the practice: both agents would
do.                                                                              agree, for example, that greeting is something you do when you
             XIV. LIMITATIONS AND FURTHER WORK                                   are sufficiently well acquainted. A deeper model of practices
                                                                                 would allow individual agents to have their own interpretations
A. Evaluation                                                                    of the practice.
   Versu is out now on the App Store for iPad, and soon for                         2) Multiple Concurrent Practices Complexify Authoring:
other devices. Although we have not attempted to evaluate the                    One of the striking things about the architecture is that it allows
system through controlled experiments, user feedback has been                    multiple practices to exist concurrently. Most of the time, there
very positive. At the time of writing, we have a 5/5 rating on the               are many practices running at once, each providing various
U.K. store and a 4/5 rating on the U.S. store.                                   options to the agents. It is because there are so many practices
   The press coverage has also been positive. Polygon wrote that                 that the player has such a wide range of options at any time.
Versu “looked quite simple at first, but became more extraordi-                     But this complexity comes at a price: the fact that there are
nary by the moment.” BookRiot described Versu as “a remark-                      multiple concurrent practices complicates the tuning and debug-
able set of storytelling tools.” Rock Paper Shotgun wrote “The                   ging of the scenes. Each action from each practice needs to be
simplicity with which it all appears betrays just how complex a                  scored against all the other actions from all the other concurrent
social AI project this really is ( ) The potential for this within               practices.
text adventures and interactive fiction seems madly enormous.”                      Allowing multiple concurrent practices generates another,
New Scientist wrote that Versu “captures the nuances of social                   deeper authoring problem. Sometimes something happens that
interaction in a way not seen before.”                                           is so obviously important that all the other things that are going
                                                                                 on should be forgotten, for the moment. For example, in our
B. Limitations of the Agent Model                                                murder-mystery episode, when the dead body is discovered,
   When using a utility-based decision maker, tweaking the var-                  there may be many other things that were going on: there may
ious desires (there are already over 300 desires in the system) so               be a budding flirtation between two of the guests, some of
that the preferred action scores higher is a difficult and time-con-             the characters may have become drunk, or violated one of the
suming tuning problem. The inspector helps authors understand                    norms of Regency England, and may be getting told off. But
autonomy tuning problems, by showing the scores of each ac-                      when the body is discovered, the affordances from these other
tion—and the reasons why the action gets the score it does—but                   practices should be suppressed. The seriousness of the situation
even with this tool, it is a difficult and unrewarding problem.                  should mean jokes and flirtations are not even considered. We
   Another limitation with the system is the way agents’ beliefs                 currently use a rather simple mechanism for suppressing the
are expressed. To simplify the implementation, all beliefs are                   affordances from other practices: a dominating practice is one
represented as sentences involving a two-place predicate and                     which, when active, suppresses the affordances from any other
a pair of constants. We can represent Mrs. Quinn’s belief that                   practices. But this dominating mechanism is too broad and
Lucy is compromised by Frank Quinn, or Darcy’s belief that                       crude for the case in hand: there are some other practices which
the ghost was killed in the study, or Elizabeth Bennett’s belief                 should coexist with the discovery of the body, for example,
that she should not marry Mr. Collins, but we cannot represent:                  weeping at the loss of a loved one. What we really want (but
   • beliefs involving universal quantifiers, e.g., “everyone has                do not know quite how to implement) is Heidegger’s idea of
      become insane”;                                                            a public mood [3], which opens up a range of possibilities
   • beliefs involving existential quantifiers, e.g., “the murderer              and closes off others. In our example, when the dead body
      is one of the guests”;                                                     is discovered, this should create a public mood of shock, and
  27Performing a speech act spawns an invisible object that lasts only for the
                                                                                 this mood should reveal various practices (grieving, examining
duration of the act, and coordinates the animations and responses of the two     the body, wondering who could have done such a thing) while
participants.                                                                    obscuring others (drinking, joking, flirtation).
      Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```

<!-- page: 18 -->
```text
130                                                        IEEE TRANSACTIONS ON COMPUTATIONAL INTELLIGENCE AND AI IN GAMES, VOL. 6, NO. 2, JUNE 2014
D. User-Generated Content                                                                [11] P. Maes, “How to do the right thing,” Connection Sci. J., vol. 1, pp.
                                                                                              291–323, 1989.
   Once she is up to speed with Praxis, a writer can produce a                           [12] J. McCarthy and P. Hayes, “Some philosophical problems from the
20-min episode (with a rich variety of end states and the ability                             standpoint of artificial intelligence,” Mach. Intell., vol. 4, pp. 463–502,
                                                                                              1969.
to play from multiple perspectives) in one to two months. Al-                            [13] J. McCoy, M. Mateas, and N. Wardrip-Fruin, “Comme il Faut: A
though this is a significant increase in content-production speed                             system for simulating social games between autonomous characters,”
over Façade, for example, the speed of content production is still                            in Proc. 8th Digit. Art Culture Conf., 2009, pp. 1–8.
                                                                                         [14] D. McDermott, “PDDL—The planning domain definition language
an issue for us.                                                                              (version 1.2),” Yale Center for Computational Vision and Control,
   In order to make content production possible in a reasonable                               New Haven, CT, USA, 1998.
amount of time, we have built, on top of the Praxis language,                            [15] J. R. Meehan, “Tale-spin, an interactive program that writes stories,”
                                                                                              in Proc. 5th Int. Joint Conf. Artif. Intell., 1977, vol. 1, pp. 91–98.
an authoring tool called Prompter. Graham Nelson (the author                             [16] Y. Moses and M. Tenenholtz, “On computational aspects of artificial
of the Inform language for interactive fiction [18]) has assisted                             social systems,” in Proc 11th DAI Workshop, 1992, pp. 108–131.
                                                                                         [17] D. Nau, Y. Cao, A. Lotem, and H. Muoz-Avila, “SHOP: Simple hier-
in the design and implementation of Prompter, which allows                                    archical ordered planner,” in Proc. Int. Joint Conf. Artif. Intell., 1999,
writers rapidly to create scenes and dialog in a format that re-                              pp. 968–973.
sembles a play script. The script is marked up with additional                           [18] G. Nelson and E. Short, “The Inform 7 Manual,” [Online]. Available:
                                                                                              http://inform7.com/learn/man/index.html
text, indicating the emotional and evaluative effects of a given                         [19] J. Orkin, “Three States and a Plan: The AI of FEAR,” 2006.
piece of dialog or action. The Prompter software then converts                           [20] J. Rawls, “Two concepts of rules,” Philosoph. Rev., vol. LXIV, pp.
the script into raw Praxis.                                                                   3–32, 1955.
                                                                                         [21] M. Riedl and V. Bulitko, “Interactive narrative: An intelligent systems
   Prompter-generated episodes can also include other, pure-                                  approach,” AI Mag., vol. 34, no. 1, 2013.
Praxis files at need, which makes it possible to coordinate gen-                         [22] D. L. Roberts and C. L. Isbell, “A survey and qualitative analysis of re-
                                                                                              cent advances in drama management,” Int. Trans. Syst. Sci. Appl., Spe-
erated data with newly invented social practices, props, and                                  cial Issue on Agent Based Systems for Human Learning, pp. 179–204,
behavior.                                                                                     2008.
   The existence of Prompter has significantly sped up our in-                           [23] J. Rosenblatt, “Maximising expected utility for behaviour arbitration,”
                                                                                              in Proc. Austral. Joint Conf. Artif. Intell., 1996, pp. 96–108.
ternal writing process, making it possible to create a substan-                          [24] H. Sacks, Lectures on Conversation. Norwell, MA, USA: Kluwer,
tially branching 20-min episode in less than a week, rather than                              1989.
in one to two months, as was formerly the case.                                          [25] H. Sacks, E. Schlegoff, and G. Jefferson, “A simplest systematics for
                                                                                              the organization of turn-taking for conversation,” Language, vol. 50,
   Our next steps include sharing Prompter with beta users and                                pp. 696–735, 1974.
developing it further as a front end for Versu development, and                          [26] K. Salen and E. Zimmerman, Rules of Play. Cambridge, MA, USA:
                                                                                              MIT Press, 2003.
releasing it as part of the eventual SDK. The intention is that                          [27] R. Schank and R. Abelson, Scripts, Plans, Goals and Understanding:
skilled and dedicated programmers will be able to add new                                     An Inquiry Into Human Knowledge Structures , ser. Artificial Intelli-
Praxis modules, but that people primarily interested in a more                                gence. New York, NY, USA: Psychology Press, 1977.
                                                                                         [28] R. Schank and R. Abelson, “Scripts, plans, knowledge,” in Proc. Int.
writerly experience with Versu will be able to use Prompter                                   Joint Conf. Artif. Intell., 1975, vol. 1, pp. 151–157.
exclusively and still create compelling new stories.                                     [29] T. R. Schatzki, Social Practices: A Wittgensteinian Approach to
                                                                                              Human Activity and the Social. Cambridge, U.K.: Cambridge Univ.
                             ACKNOWLEDGMENT                                                   Press, 1996.
                                                                                         [30] E. Short, “NPC conversation systems,” in IF Theory Reader.
   The authors would like to thank T. Barnet-Lamb, C. Gingold,                                Norman, OK, USA: Transcript, 2011.
I. Holmes, I. Horswill, M. Mateas, and G. Nelson for feedback                            [31] Z. Somogyi, F. Henderson, and T. Conway, “The execution algorithm
                                                                                              of mercury: An efficient purely declarative logic programming lan-
and guidance throughout the project.                                                          guage,” J. Logic Programm., vol. 29, pp. 17–64, 1996.
                                                                                         [32] C. Strachey, “Fundamental concepts in programming languages,”
                                  REFERENCES                                                  Higher-Order Symbolic Comput., vol. 13, pp. 11–49, 2000.
       [1] P. Blackburn, M. de Rijke, and Y. Venema, Modal Logic. Cambridge,             [33] M. Tomasello, Origins of Human Communication. Cambridge, MA,
           U.K.: Cambridge Univ. Press, 2002.                                                 USA: MIT Press, 2008.
       [2] R. Brandom, Making It Explicit. Cambridge, MA, USA: Harvard                   [34] N. Wardrip-Fruin, Expressive Processing. Cambridge, MA, USA:
           Univ. Press, 1998.                                                                 MIT Press, 2007.
       [3] H. Drefyus, Being-in-the-World. Cambridge, MA, USA: MIT Press,
           1990.                                                                      Richard Evans studied philosophy at Cambridge University, Cambridge, U.K.,
       [4] P. Ekman, “Basic emotions,” 1990.                                          and artificial intelligence at Edinburgh University, Edinburgh, U.K.
       [5] R. Evans, Introducing Exclusion Logic as a Deontic Logic. Deontic            He was the CEO of Little Text People and is now a Senior Architect at Linden
           Logic in Computer Science. New York, NY, USA: Springer-Verlag,             Lab, San Francisco, CA, USA. He is the technical architect for Versu. He has
           2010.                                                                      been working on multiagent simulations for 20 years. He was the AI Lead En-
       [6] R. Evans, “Representing personality traits as conditionals,” in Proc.      gineer on Black&White and The Sims 3.
           Artif. Intell. Simul. Behav., 2008, pp. 64–82.
       [7] M. Mateas, “Interactive drama, art, artificial intelligence,” Ph.D. dis-
           sertation, Schl. Comput. Sci., Carnegie Mellon Univ., Pittsburgh, PA,      Emily Short studied classics and physics at Swarthmore College, Swarthmore,
           USA, 2002.                                                                 PA, USA.
       [8] M. Mateas and A. Stern, “Writing Façade: A case study in procedural           She was the Chief Textual Officer at Little Text People and is now a Creative
           authorship,” in Second Person, Role-Playing Story Games Playable           Director at Linden Lab, San Francisco, CA, USA. She is the creative director
           Media. Cambridge, MA, USA: MIT Press, 2007, pp. 183–208.                   for Versu. She specializes in interactive narrative, especially dialog models. She
       [9] D. Knuth, Digital Searching. The Art of Computer Programming               is the author of over a dozen works of interactive fiction, including Galatea and
           Volume 3: Sorting and Searching, 2nd ed. Reading, MA, USA:                 Alabaster, which focus on conversation as the main form of interaction, and
           Addison-Wesley, 1997.                                                      Mystery House Possessed, a commissioned project with dynamically managed
      [10] D. Lewis, “Scorekeeping in a language game,” J. Philosoph. Logic, p.       narrative. She is also part of the team behind Inform 7, a natural-language pro-
           379, 1979.                                                                 gramming language for creating interactive fiction.
        Authorized licensed use limited to: UNIVERSITY OF KENTUCKY. Downloaded on June 08,2020 at 17:02:58 UTC from IEEE Xplore. Restrictions apply.
```
