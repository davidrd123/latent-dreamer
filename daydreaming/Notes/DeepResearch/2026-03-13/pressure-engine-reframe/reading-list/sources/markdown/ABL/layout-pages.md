# ABL

Layout-preserving `pdftotext -layout` extraction.

<!-- page: 1 -->
```text
               A Behavior Language for Story-based Believable Agents
                   Michael Mateas                                                          Andrew Stern
                Carnegie Mellon University                                                 InteractiveStory.net
                   5000 Forbes Avenue                                                  andrew@interactivestory.net
                   Pittsburgh, PA 15213                                                  www.interactivestory.net
                  michaelm@cs.cmu.edu
                www.cs.cmu.edu/~michaelm
                          Abstract
  ABL is a reactive planning language, based on the Oz
  Project language Hap, designed specifically for authoring                                ABL overview
  believable agents - characters which express rich                  ABL (A Behavior Language, pronounced “able”) is based
  personality, and which, in our case, play roles in an
  interactive, dramatic story world. Here we give a brief
                                                                     on the Oz Project (Bates 1992) believable agent language
  overview of the language Hap and discuss the new features          Hap developed by A. B. Loyall (Loyall 1997, Bates,
  in ABL, focusing on ABL’s support for multi-character              Loyall and Reilly 1992, Loyall and Bates 1991). The
  coordination. We also describe the ABL idioms we are               ABL compiler is written in Java and targets Java; the
  using to organize character behaviors in the context of an         generated Java code is supported by the ABL runtime
  interactive drama.                                                 system.
                                                                       ABL modifies Hap in a number of ways, changing the
                                                                     syntax (making it more Java-like), generalizing the
                      Introduction                                   mechanisms by which an ABL agent connects to a
                                                                     sensory-motor system, and, most significantly, adding
Façade is a serious attempt to move beyond traditional
                                                                     new constructs to the language, including language
branching or hyper-linked narrative, to create a fully-
                                                                     support for multi-agent coordination in the carrying out of
realized interactive drama - a dramatically interesting
                                                                     dramatic action. This section provides an overview of the
virtual world inhabited by computer-controlled characters,
                                                                     ABL language and discusses some of the ways in which
within which the user (hereafter referred to as the player)
                                                                     ABL modifies or extends Hap. The discussion of joint
experiences a story from a first person perspective
                                                                     behaviors, the mechanism for multi-agent coordination,
(Mateas and Stern 2002, Mateas and Stern 2000). The
                                                                     occurs in its own section below.
complete, real-time 3D, one-act interactive drama will be
available in a free public release at the end of 2002. In the
story, Grace and Trip, a married couple in their early               Hap Semantics
thirties, has invited the player over for drinks.                    Since ABL builds on top of Hap, here we briefly describe
Unbeknownst to the player, their marriage is in serious              the organization and semantics of a Hap program by
trouble, and in fact, tonight is the night that all their            walking through a series of examples. All examples use
troubles are going to come to the surface. Whether and               the ABL syntax.
how their marriage falls apart, and the state of the player’s          Hap/ABL programs are organized as collections of
relationship with Grace and Trip at the end of the story,            behaviors. In sequential behaviors, the steps of the
depends on how the player interacts in the world. The                behavior are accomplished serially. As each step is
player interacts by navigating in the world, manipulating            executed, it either succeeds or fails; step success makes
objects, and, most significantly, through natural language           the next step available for execution. If any step fails, it
dialog. This project raises a number of interesting AI               causes the enclosing behavior to fail. An example
research issues, including drama management for                      sequential behavior is shown below.
coordinating plot-level interactivity, broad but shallow             sequential behavior AnswerTheDoor() {
support for natural language understanding and discourse               WME w;
management, and autonomous believable agents in the                    with success_test { w = (KnockWME) } wait;
                                                                       act sigh();
context of interactive story worlds. This paper focuses on             subgoal OpenDoor();
the last issue, describing the custom believable agent                 subgoal GreetGuest();
language developed for this project, and the idioms                    mental_act { deleteWME(w); }
                                                                     }
developed within this language for organizing character
behaviors.                                                              In this sequential behavior, an agent waits for someone
                                                                     to knock on a door, sighs, then opens the door and greets
                                                                     the guest. This behavior demonstrates the four basic step
                                                                     types,     namely     wait,     act,     subgoal,      and
Appeared in: Ken Forbus and Magy El-Nasr Seif (Eds.), Working notes of Artificial Intelligence and Interactive Entertainment. AAAI
Spring Symposium Series. Menlo Park, CA: AAAI Press. 2002.
```

<!-- page: 2 -->
```text
mental_act. Wait steps are never chosen for execution;              specificity 1;
                                                                    // Default behavior - walk to door and open
a naked wait step in a sequential behavior would block the          . . .
behavior from executing past the wait. However, when            }
combined with a success test, a wait step can be used to        In this example there are two sequential behaviors
make a demon which waits for a condition to become              OpenDoor(), either of which could potentially be used
true. Success tests are continuously monitored conditions       to satisfy the goal OpenDoor(). The first behavior
which, when they become true, cause their associated step       opens the door by yelling for the guest to come in and
to immediately succeed. Though in this example the              waiting for them to open the door. The second behavior
success test is associated with a wait step to make a           (details elided) opens the door by walking to the door and
demon, it can be associated with any step type.                 opening it. When AnswerTheDoor() pursues the
   Success tests, as well as other tests which will be          subgoal OpenDoor(), Hap/ABL determines, based on
described shortly, perform their test against the agent’s       signature matching, that there are two behaviors which
working memory. A working memory contains a number              could possibly open the door. The precondition of both
of working memory elements (WMEs) which hold                    behaviors is executed. In the event that only one of the
information. WMEs are like classes in an object oriented        preconditions is satisfied, that behavior is chosen as the
language; every WME has a type plus some number of              method to use to accomplish the subgoal. In the event that
typed fields which can take on values. As described later       both preconditions are satisfied, the behavior with the
on in the paper, WMEs are also the mechanism by which           highest specificity is chosen. If there are multiple satisfied
an agent becomes aware of sensed information. In this           behaviors with highest specificity, one is chosen at
example, the success test is looking for WMEs of type           random. In this example, the first OpenDoor() behavior
KnockWME, which presumably is placed in the agent’s             is chosen if the lazy agent is too far from the door to walk
working memory when someone knocks on a door. Since             there (“too far” is arbitrarily represented as a distance >
there are no field constraints in the test, the test succeeds   “100”).
as soon as a KnockWME appears.                                     The precondition demonstrates the testing of the fields
   An act step tells the agent’s body (sensory-motor            of a WME. The :: operator assigns the value of the
system) to perform an action. For graphical environments        named WME field on the left of the operator to the
such as Façade, physical acts will ultimately be translated     variable on the right.1 This can be used both to grab
into calls to the animation engine, though the details of       values from working memory which are then used in the
this translation are hidden from the Hap/ABL program. In        body of the behavior, and to chain constraints through the
this example, the act makes the body sigh. Note that            WME test.
physical acts can fail - if the sensory-motor system               The last example demonstrates parallel behaviors and
determines that it is unable to carry out the action, the       context conditions.
corresponding act step fails, causing the enclosing
behavior to fail.                                               parallel behavior
                                                                YellAndWaitForGuestToEnter(int doorID) {
   Subgoal steps establish goals that must be                     precondition { (CurrentTimeWME t :: startT) }
accomplished in order to accomplish the behavior. The             context_condition {
                                                                    (CurrentTimeWME t <= startT + 10000) }
pursuit of a subgoal within a behavior recursively results        number_needed_for_success 1;
in the selection of a behavior to accomplish the subgoal.
   Mental acts are used to perform bits of pure                     with success_test {
                                                                      (DoorOpenWME door == doorID) } wait;
computation, such as mathematical computations or                   with (persistent) subgoal YellForGuest(doorID);
modifications to working memory. In the final step of the       }
example, the mental_act deletes the KnockWME (making            In a parallel behavior, the steps are pursued
a call to a method defined on ABL agent), since the             simultaneously.
knocking has now been dealt with. In ABL, mental acts           YellAndWaitForGuestToEnter(int)
are written in Java.                                            simultaneously yells “come in” towards the door (the door
   The next example demonstrates how Hap/ABL selects            specified by the integer parameter) and waits to actually
a behavior to accomplish a subgoal through signature            see the door open. The persistent modifier on the
matching and precondition satisfaction.                         YellForGuest(int) subgoal makes the subgoal be
sequential behavior OpenDoor() {                                repeatedly pursued, regardless of whether the subgoal
  precondition {                                                succeeds or fails (one would imagine that the behavior
    (KnockWME doorID :: door)
    (PosWME spriteID == door pos :: doorPos)                    that does the yelling always succeeds). The
    (PosWME spriteID == me pos :: myPos)                        number_needed_for_success annotation (only
    (Util.computeDistance(doorPos, myPos) > 100)
  }                                                             usable on parallel behaviors) specifies that only one step
  specificity 2;
  // Too far to walk, yell for knocker to come in               1
  subgoal YellAndWaitForGuestToEnter(doorID);                    In ABL, a locally-scoped appropriately typed variable is
}                                                               automatically declared if it is assigned to in a WME test
sequential behavior OpenDoor() {                                and has not been previously explicitly declared.
  precondition { (KnockWME doorID :: door) }
```

<!-- page: 3 -->
```text
has to succeed in order for the behavior to succeed. In this     multiple WMEs atomically), though they should be
case, that one step would be the demon step waiting for          used sparingly, as a time-consuming atomic behavior
the door to actually open. The context condition is a            could impair reactivity.
continuously monitored condition which must remain true        • Reflection. ABL gives behaviors reflective access to
during the execution of a behavior. If the context               the current state of the ABT, supporting the authoring
condition fails during execution, then the behavior              of meta-behaviors which match on patterns in the ABT
immediately fails. In this example, the context condition        and dynamically modify other running behaviors.
tests the current time, measured in milliseconds, against        Supported ABT modifications include succeeding,
the time at which the behavior started. If after 10 seconds      failing or suspending a goal or behavior, and modifying
the door hasn’t yet opened (the guest isn’t coming in),          the annotations of a subgoal step, such as changing the
then the context condition will cause the behavior to fail.      persistence or priority. Safe reflection is provided by
   As failure propagates upwards through the subgoal             wrapping all ABT nodes in special WMEs. Pattern
chain, it will cause the first OpenDoor() behavior to            matching on ABT state is then accomplished through
fail, and eventually reach the OpenDoor() subgoal in             normal WME tests. A behavior can only touch the ABT
AnswerTheDoor(). The subgoal will then note that                 through the reflection API provided on these wrapper
there is another OpenDoor() behavior which has not               WMEs.
been tried yet and whose precondition is satisfied; this
behavior will be chosen in an attempt to satisfy the           • Multiple named memories. Working memories can be
subgoal. So if the guest doesn’t enter when the agent yells      given a public name, which then, through the name, are
for awhile, the agent will then walk over to the door and        available to all ABL agents. Any WME test can
open it.                                                         simultaneously reference multiple memories (the
   Finally, note that parallel behaviors introduce multiple      default memory is the agent’s private memory). Named
lines of expansion into a Hap/ABL program.                       memories are used by the joint behavior mechanisms
Consequently, the current execution state of the program         (see below) for the construction of team memories. In
is represented by a tree, the active behavior tree (ABT),        Façade, named memories are also useful for giving
where the leaves of the tree constitute the current set of       agents access to a global story memory.
executable steps.                                              • Goal spawning. In addition to subgoaling, which roots
   These examples give a sense for the Hap semantics             the selected behavior at the subgoal step of the parent
which ABL reimplements and extends. There are many               behavior, a behavior can spawn a goal, which roots the
other features of Hap (also implemented in ABL) which it         subgoal elsewhere in the tree (the default is the root
is not possible to re-describe here, including how multiple      collection behavior). Unlike a normal subgoal, the
lines of expansion mix (based on priority, blocking on           success or failure of a spawned goal does not effect the
physical acts, and a preference for pursing the current line     success or failure of the behavior which spawned the
of expansion), declaration of behavior and step conflicts        goal (though it will effect the success or failure of the
(and the resulting concept of suspended steps and                behavior where it is rooted). The spawned goal
behaviors), and numerous annotations which modify the            continues to be held after the behavior which spawned
default semantics of failure and success propagation. The        the goal goes away (succeeds or fails). Goal spawning
definitive reference on Hap is of course Loyall’s                is useful for starting a behavior which should continue
dissertation (Loyall 1997).                                      past the end (and during the suspension) of the
                                                                 spawning parent.
ABL Extensions
ABL extends Hap in a number of ways, including:                                     Beat Idioms
 • Generalizing the mechanisms for connecting to the
   sensory-motor system. The ABL runtime provides              Developing a believable agent language such as ABL
   abstract superclasses for sensors and actions. To           involves simultaneously defining and implementing
   connect an ABL program to a new sensory-motor               language constructs which support the authoring of
   system (e.g. animation engine, robot), the author           expressive behavior, and the exploration of idioms for
   merely defines specific sensors and actions as concrete     expressive behavior using the language. In Façade,
   subclasses of the abstract sensor and action classes.       character behavior is organized around the dramatic beat,
   ABL also includes additional language constructs for        in the theory of dramatic writing the smallest unit of
   binding sensors to WMEs. ABL then takes                     dramatic action (see for example McKee 1997). This
   responsibility for calling the sensors appropriately        section describes the ABL idioms used in authoring beat
   when bound WMEs are referenced in working memory            behaviors.
   tests.                                                         Beat behaviors are divided into three categories: beat
                                                               goals, handlers, and cross-beat behaviors. A greeting
• Atomic behaviors. Atomic behaviors prevent other             beat, in which Trip greets the player at the door, will
   active behaviors from mixing in. Atomic behaviors are
   useful for atomically updating state (e.g. updating
```

<!-- page: 4 -->
```text
provide examples of these three behaviors categories and      or lower priority handlers must wait for a higher priority
                                          1
the relationships between the categories.                     handler to finish before handling the nested interaction.
   In the greeting beat, Trip wants to initially greet the    Generally handlers are persistent; when a handler finishes
player (“Hey! So glad you could make it. Thanks for           responding to an interaction, it should “reset” and be
coming over man.”), yell for Grace (“Grace, come on out!      ready to deal with another interaction in the same
Our guest is here.”), and invite the player in (“Come on      category. In general handlers are higher priority than beat
in, don’t be shy”). These are the three beat goals of the     goals so that if an interaction occurs in the middle of the
greeting beat and should be accomplished sequentially.        beat goal, the handler will “wake up” and interrupt it.
   Of course, during this greeting, the player will engage       In general, handlers are meta-behaviors, that is, they
in various actions which should be handled in the context     make use of reflection to directly modify the ABT state.
of the greeting. These interactions take the form of          When a handler triggers, it fails the current beat goal,
physical movement, object manipulation, and natural           potentially succeeds other beat goals, possibly pursues a
language text typed by the player. At the beat behavior       beat goal within the handler (effectively reordering beat
level, player text is captured by WMEs representing the       goals), and engages in its own bit of handler specific
meaning of the text as a discourse act.2 Handlers are         behavior. In some cases the handler specific behavior may
demons responsible for handling player interaction. For       entail mapping the recognized action to a different
the purposes of this example, assume that the greeting        recognized action, which will then trigger a different
beat wants to handle the cases of the player greeting Trip,   corresponding handler. Below is a simplified version of
the player referring to Grace, and the player preemptively    handlerDAReferTo_grace().
walking into the apartment before she has been invited        sequential behavior handlerDAReferTo_grace() {
in.3 The code below starts the handlers and begins the          with (success_test { (DAReferToWME topicID ==
sequence of beat goals.                                                                eTopic_grace) } ) wait;
                                                                with (ignore_failure) subgoal
parallel behavior StartTheBeat() {                                handlerDAReferTo_grace_Body();
  with (priority 1)                                             subgoal DAReferTo_grace_Cleanup();
    subgoal StartTheHandlers();                               }
  subgoal BeatGoals();
}                                                             // by mentioning Grace, we will say "Grace? uh
                                                              // yeah" and then yell for Grace but only if we
parallel behavior StartTheHandlers() {                        // aren't currently doing bgYellForGrace!
  with (persistent, priority 20)                              sequential behavior handlerDAReferTo_grace_Body()
    subgoal handlerDAGreet();                                 {
  with (persistent, priority 15)                                precondition {
    subgoal handlerDAReferTo_grace();                             (GoalStepWME signature == "bgYellForGrace()"
  with (priority 10, ignore_failure)                                           isExecuting == false) }
    subgoal handlerPreInviteAptMove();                          subgoal handlerDA_InterruptWith(
}                                                                 eTripScript_graceuhyeah,
                                                                  eFullExpression_blank);
sequential behavior BeatGoals() {                               subgoal handlerDAReferTo_grace_Body2();
  with (persistent when_fails)                                }
    bgOpenDoorAndGreetPlayer();
  with (persistent when_fails) bgYellForGrace();              // we aren't currently doing yellForGrace, and if
  with (persistent when_fails) bgInviteIntoApt();             // we haven't completed yellForGrace, then do it
}                                                             sequential behavior
                                                                handlerDAReferTo_grace_Body2() {
   Handlers are started in various priority tiers               // Goal still exists in the ABT so it hasn't
                                                                // been completed
corresponding to the relative importance of handling that       precondition {
interaction. Priorities are used to resolve cases where           (GoalStepWME signature == "bgYellForGrace()")
another player interaction happens in the middle of             }
                                                                specificity 2;
handling the previous player interaction, or when               subgoal SetBeatGoalSatisfied(
simultaneous player interactions occur. A higher priority          "bgYellForGrace()", true);
                                                                with (persistent when_fails)
handler can interrupt a lower priority handler, while same         subgoal bgYellForGrace();
                                                              }
1
  To simplify the discussion, the example will focus on a
beat in which only a single character interacts with the      // otherwise we must have already completed
                                                              // yellForGrace, so say "She's coming, I don't
player. See the next section for a discussion of ABL’s        // know where she's hiding"
support for multi-agent coordination.                         sequential behavior
2                                                               handlerDAReferTo_grace_Body2() {
  For translating surface text into formally represented        specificity 1;
discourse acts, Façade employs a custom rule language for       subgoal handlerDA_InterruptWith(
                                                                  etripScript_shescomingidontknow,
specifying templates and discourse chaining rules. The            eFullExpression_smallSmile);
discourse rule compiler targets Jess, a CLIPS-like forward-   }
chaining rule language (available at                             When the player refers to Grace (perhaps saying, “I’m
http://herzberg.ca.sandia.gov/jess/).                         looking forward to meeting Grace”, or “Where is Grace”,
3
  The real greeting beat employs ~50 handlers.                or “Hi Grace”) this handler is triggered. The handler body
```

<!-- page: 5 -->
```text
behavior uses reflection to test if the beat goal to yell for    accomplish joint story goals. In order to resolve the
Grace is currently not executing. If it is executing (e.g.       tension between local and global control of characters, we
Trip was in the middle of yelling for Grace when the             proposed organizing behaviors around the dramatic beat.
player said “Where’s Grace”), the body precondition fails,       In order to facilitate the coordination of multiple
causing the handler to fail, which then restarts because of      characters, we proposed extending the semantics of Hap,
the persistence annotation, leaving Trip ready to handle         in a manner analogous to the STEAM multi-agent
another reference to Grace. Effectively Trip ignores             coordination framework (Tambe 1997). This section
references to Grace if he’s in the middle of yelling for         describes joint behaviors, ABL’s support for multi-agent
Grace. Otherwise, Trip interrupts whatever he is saying          coordination.
with “Oh, yeah…”. handlerDAInterruptWith uses                       The driving design goal of joint behaviors is to
reflection to fail the currently executing beat goal, thus, as   combine the rich semantics for individual expressive
the name implies, interrupting the beat goal. When the           behavior offered by Hap with support for the automatic
handler is finished, the persistent_when_fails                   synchronization of behavior across multiple agents.
annotation will causes any handler-failed beat goals to
restart. After saying “Oh yeah…” Trip either performs the        Joint Behaviors
yell for Grace beat goal within the handler (and succeeds
it out of the BeatGoals behavior) or, if yell for Grace          In ABL, the basic unit of coordination is the joint
has already happened, says “She’s coming. I don’t know           behavior. When a behavior is marked as joint, ABL
where she’s hiding.” This handler demonstrates how               enforces synchronized entry and exit into the behavior.
player interaction can cause beat goals to be interrupted,       Part of the specification for an “offer the player a drink”
effectively reordered, and responded to in a way                 behavior from Façade is shown below. To simplify the
dependent on what has happened in the beat so far.               discussion, the example leaves out the specification of
   The final category of beat behaviors are the cross-beat       how player activity would modify the performance of this
behaviors. These are behaviors that cross beat goal and          beat. This will be used as the guiding behavior
handler boundaries. An example beat goal behavior is the         specification in the joint behavior examples provided in
staging behavior which an agent uses to move to certain          this paper.
dramatically significant positions (e.g. close or far              (At the beginning of the behavior, Trip starts walking
conversation position with the player or another agent,            to the bar. If he gets to the bar before the end of the
into position to pickup or manipulate another object, etc.).       behavior, he stands behind it while delivering lines.)
A staging request to move to close conversation position           Trip: A beer? Glass of wine? (Grace smiles at
with the player might be initiated by the first beat goal in       player. Short pause)
a beat. The staging goal is spawned to another part of the         Trip: You know I make a mean martini. (Grace
ABT. After the first beat goal completes its behavior,             glances at Trip with slight frown partway into line.
other beat goals and handlers can happen as the agent              At the end of line, rolls her eyes at the ceiling.)
continues to walk towards the requested staging point. Of          Grace: (shaking her head, smiling) Tch, Trip just
course at any time during a cross-beat behavior, beat              bought these fancy new cocktail shakers. He’s
goals and handlers can use reflection to find out what             always looking for a chance to show them off. (If
cross-beat behaviors are currently happening and succeed           Trip is still walking to the bar, he stops at “shakers”.
or fail them if the cross-beat behaviors are inappropriate         At “shakers” Trip looks at Grace and frowns slightly.
for the current beat goal’s or handler’s situation.                At the end of the line he looks back at player and
                                                                   smiles. If he was still on the way to the bar, he
                                                                   resumes walking to the bar).
              Support for Joint Action
                                                                 In order to perform this coordinated activity, the first
In (Mateas and Stern 2000) we argued that much work in           thing that Grace and Trip must do is synchronize on
believable agents is organized around the principle of           offering a drink, so that they both know they are working
strong autonomy, and that, for story-based believable            together to offer the drink. Grace and Trip both have the
agents, this assumption of strong autonomy is                    following behavior definition in their respective behavior
problematic. An agent organized around the notion of             libraries.
strong autonomy chooses its next action based on local
                                                                 joint sequential behavior OfferDrink() {
perception of its environment plus internal state                  team Grace, Trip;
corresponding to the goals and possibly the emotional              // The steps of Grace’s and Trip’s OfferDrink()
state of the agent. All decision making is organized               // behaviors differ.
                                                                 }
around the accomplishment of the individual, private
goals of the agent. But believable agents in a story must        The declaration of a behavior as joint tells ABL that entry
also participate in story events, which requires making          into and exit from the behavior must be coordinated with
decisions based on global story state (the entire past           team members, in this case Grace and Trip. Entry into a
history of interaction considered as a story) and tightly        behavior occurs when the behavior is chosen to satisfy a
coordinating the activity of multiple agents so as to            subgoal. Exit from the behavior occurs when the behavior
```

<!-- page: 6 -->
```text
succeeds, fails, or is suspended. Synchronization is           negotiation protocol provides authorial “hooks” for
achieved by means of a three-phase commit protocol:            attaching transition behaviors to joint behavior entry and
1. The initiating agent broadcasts an intention (to enter,     exit. Sengers, in her analysis of the Luxo Jr. short by
   succeed, fail or suspend) to the team.                      Pixar, identified behavior transitions as a major means by
                                                               which narrative flow is communicated (Sengers 1998a).
2. All agents receiving an intention respond by, in the        Animators actively communicate changes in the behavior
   case of an entry intention, signaling their own intention   state of their characters (e.g. the change from playing to
   to enter or a rejection of entry, or in the case of exit    resting) by having the characters engage in short
   signaling their intention own intention to succeed, fail,   transitional behaviors that communicate why the behavior
   or suspend.                                                 change is happening. Sengers’ architectural extensions to
3. When an agent receives intentions from all team             Hap provided support for authoring individual transition
   members, it sends a ready message. When ready               behaviors (Sengers 1998a, Sengers 1998b). However, she
   messages from all agents have been received, the agent      also noted that animators make use of coordinated multi-
   performs the appropriate entry into or exit from the        character transitions to communicate changes in multi-
   behavior.1                                                  character behavioral state, but did not provide
   Imagine that Trip pursues an OfferDrink() subgoal           architectural support for this in her system. By exposing
and picks the joint OfferDrink() behavior to                   the negotiation protocol to the agent programmer, ABL
accomplish the subgoal. After the behavior has been            can support the authoring of behaviors which
chosen, but before it is added to the ABT, Trip negotiates     communicate transitions in multi-agent behavior state.
entry with his teammate Grace. On receipt of the
intention-to-enter OfferDrink(), Grace checks if she           Posting Actions and Step Synchronization
has a joint behavior OfferDrink() with a satisfied
precondition. If she does, she signals her intention-to-       In addition to synchronizing on behavior entry and exit,
enter. Trip and Grace then exchange ready-messages and         ABL provides other mechanisms for synchronizing
enter the behavior. In Trip’s case the behavior is rooted      agents, namely support for posting information to a team
normally in the ABT at the subgoal which initiated             working memory, and the ability to synchronize the steps
behavior selection, while in Grace the spawned subgoal         of sequential behaviors. Below are the two
and corresponding joint behavior are rooted at the             OfferDrink() behaviors for Trip and Grace.
collection behavior at the root of the ABT.2 If Grace          Trip’s behavior:
didn’t have a satisfied joint OfferDrink() behavior,
                                                               joint sequential behavior OfferDrink() {
she would send a reject message to Trip, which would             team Trip, Grace;
cause Trip’s OfferDrink() subgoal to fail, with all the
normal effects of failure propagation (perhaps causing             with (post-to OfferDrinkMemory)
                                                                     // Individual behavior for initial offer
Trip to instead choose a lower specificity individual                subgoal iInitialDrinkOffer();
OfferDrink() behavior). Note that during the                       subgoal iLookAtPlayerAndWait(0.5);
                                                                   with (synchronize) subgoal jSuggestMartini();
negotiation protocol, the agents continue to pursue other
lines of expansion in their ABT’s; if the protocol takes           // react to Grace’s line about fancy shakers
awhile to negotiate, behavior continues along these other          with (synchronize) subgoal
                                                                     jFancyCocktailShakers();
lines.                                                         }
   The negotiation protocol may seem overly complex. In
the case that all the team members are on the same             Grace’s behavior:
machine (the case for Façade), one can assume that             joint sequential behavior OfferDrink() {
negotiation will be very fast and no messages will be lost.      team Trip, Grace;
In this case agents only need to exchange a pair of                // wait for Trip to say first line
messages for behavior entry, while the initiator only needs        with (success_test { OfferDrinkMemory
                                                                     (CompletedGoalWME name == iInitialDrinkOffer
to send a single message for behavior exit. However, this                              status == SUCCEEDED)})
simplified protocol would break in the distributed case              wait;
where team member’s messages may be lost, or in cases              subgoal iLookAtPlayerAndWait(0.5);
where an agent might disappear unexpectedly (e.g. a                // react to Martini suggestion
game where agents can be killed) in the middle of the              with (synchronize) subgoal jSuggestMartini();
                                                                   with (synchronize) subgoal
negotiation. More interestingly, the more complex                    jFancyCocktailShakers();
                                                               }
1
  Appropriate timeouts handle the case of non-responding       For readability, the subgoals have been named with an
agents who fail to send appropriate intention or ready         initial “i” if only an individual behavior is available to
messages.                                                      satisfy the subgoal, and named with an initial “j” if only a
2
  A collection behavior is a variety of parallel behavior in   joint behavior is available.
which every step need only be attempted for the behavior          Whenever a joint behavior is entered, the ABL runtime
to succeed.                                                    automatically creates a new named team working memory
```

<!-- page: 7 -->
```text
which persists for the duration of the joint behavior. This     on his side, causing Grace to spawn the goal at her root
team memory, which can be written to and read from by           and enter another copy of the behavior. At this point each
any member of the team, can be used as a communication          is pursuing two copies of the joint behavior
mechanism for coordinating team activity. The first             jSuggestMartini(), one copy rooted at the subgoal
subgoal of Trip’s behavior is annotated with a post-to          within OfferDrink(), and the other rooted at the root
annotation; for any subgoal marked with post-to, a              of the ABT. This is not what the behavior author
CompletedGoalWME is added to the named memory                   intended; rather it was intended that when the characters
when the subgoal completes (with either success or              synchronize on jSuggestMartini(), they would each
failure). A CompletedGoalWME, the definition of which           begin      pursing       their     local     version      of
is provided by the ABL runtime, contains the name of the        jSuggestMartini() rooted at the respective subgoals
goal, its completion state (success or failure), the name of    within their local versions of OfferDrink(). The
the agent who performed the goal, any goal arguments,           synchronize annotation allows a behavior author to
and a timestamp. The post-to annotation automatically           specify that a joint behavior should be rooted at a specific
fills in the appropriate arguments. This facility, inspired     subgoal, rather than at the ABT root. Synchronize is
by the sign management system in Senger’s extension of          only allowed within joint behaviors as an annotation on a
Hap (Sengers 1998a, Sengers 1998b), can be used to              goal that has at least one joint behavior with matching
provide an agent with a selective episodic memory. This         signature in the behavior library. In the case of sequential
facility is useful even in a single agent situation, as the     joint behaviors, synchronization on a synchronize
future behavior of an agent may conditionally depend on         subgoal forces the success of all steps between the current
past episodic sequences. Since the ABT no longer has            step counter position and the synchronize subgoal,
state for already completed subgoals and actions, an ABL        and moves the step counter up to the synchronize
agent’s reflective access to its own ABT doesn’t provide        subgoal.
access to past episodic sequences. However, in a team              The example used in this section did not take account
situation, access to episodic state can be used to              of player interaction. Multi-agent beats use the same
coordinate team members. In the first line of Grace’s           idioms as described above for coordinating beat goals,
behavior, a demon step monitors the team memory for the         responding to player interaction, and pursing longer term
completion of iInitialDrinkOffer(). In the                      goals; the various beat behaviors just become joint
behavior spec above, Grace doesn’t begin directly               behaviors instead of individual behaviors.
reacting to Trip until after Trip’s first line. Keep in mind
that an ABL agent pursues multiple lines of expansion, so
while Grace is waiting for Trip to complete his first line,                           Conclusion
she will continue to behave, in this case engaging in small
idle movements as she smiles at the player. When Trip           ABL provides a rich programming framework for
completes      his   first    subgoal,      an    appropriate   authoring story-based believable agents. Here we’ve
CompletedGoalWME is posted to the team memory; Trip             described ABL’s novel features and provided examples of
then moves onto his second subgoal, to look at the player       how we’re using these features to author characters for
and wait for about half a second. The posting of the            Façade, an interactive dramatic world.
CompletedGoalWME causes Grace’s first line to succeed,
and she also, independently, waits for about half a second.
One of them will be first to finish waiting, and will move
                                                                                      References
onto the next line, which, being a joint behavior,              Bates, J. 1992. Virtual Reality, Art, and Entertainment.
reestablishes synchronization.                                  Presence: The Journal of Teleoperators and Virtual
   The last two subgoals of Grace’s and Trip’s behaviors        Environments 1(1): 133-138.
are annotated with a synchronize annotation. To                 Bates, J., Loyall, A. B., and Reilly, W. S. 1992.
understand what this does, first imagine the case where         Integrating Reactivity, Goals, and Emotion in a Broad
the annotation is absent. Assume Grace is the first to          Agent. Proceedings of the Fourteenth Annual Conference
finish the second subgoal (the goal to look at the player       of the Cognitive Science Society, Bloomington, Indiana,
and wait). Grace will then attempt to satisfy the subgoal       July 1992.
jSuggestMartini(), causing Trip to spawn this goal
at the root of his ABT and enter his local version of           Loyall, A. B. 1997. Believable Agents. Ph.D. thesis, Tech
jSuggestMartini(). As they jointly pursue the                   report CMU-CS-97-123, Carnegie Mellon University.
jSuggestMartini() line of expansion, Trip will                  Loyall, A. B., and Bates, J. 1991. Hap: A Reactive,
continue to pursue the OfferDrink() line of                     Adaptive Architecture for Agents. Technical Report
expansion, eventually initiating jSuggestMartini()              CMU-CS-91-147. Department of Computer Science.
                                                                Carnegie Mellon University.
1
 By default the name of the team memory is the                  Mateas, M. and Stern, A. 2000. Towards Integrating Plot
concatenation of the name of the behavior and the string        and Character for Interactive Drama. In Working notes of
“Memory”.                                                       the Social Intelligent Agents: The Human in the Loop
```

<!-- page: 8 -->
```text
Symposium. AAAI Fall Symposium Series. Menlo Park,
CA: AAAI Press.
Mateas, M. and Stern, A. 2002 (forthcoming). Towards
Integrating Plot and Character for Interactive Drama. In
K. Dautenhahn (Ed.), Socially Intelligent Agents: The
Human in the Loop. Kluwer.
McKee, R. 1997. Story: Substance, Structure, Style, and
the Principles of Screenwriting. New York, NY:
HarperCollins.
Sengers, P. 1998a. Anti-Boxology: Agent Design in
Cultural Context. Ph.D. Thesis. School of Computer
Science, Carnegie Mellon University.
Sengers, P. 1998b. Do the Thing Right: An Architecture
for Action-Expression. In Proceedings of the Second
International Conference on Autonomous Agents. pp. 24-
31.
Tambe, M. 1997. Towards Flexible Teamwork. Journal of
Artificial Intelligence Research (7) 83-124.
```
