# EMA

<!-- page: 1 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: title/authors/abstract page._

```text
                           App ea rs in Ag en t Con s truct ion and Emo tion s, 2006
             EMA: A computational model of appraisal dynamics
                Stacy Marsella                                            Jonathan Gratch
        University of Southern California                          University of Southern California
                   marsella@isi.usc.edu                                        gratch@ict.usc.edu
                                                     Abstract
    A computational model of emotion must explain both the rapid dynamics of some emotional reactions as
    well as the slower responses that follow deliberation. This is often addressed by positing multiple ap-
    praisal processes such as fast pattern directed vs. slower deliberative appraisals. In our view, this con-
    fuses appraisal with inference. Rather, we argue for a single and automatic appraisal process that operates
    over a person’s interpretation of their relationship to the environment. Dynamics arise from perceptual
    and inferential processes operating on this interpretation (including deliberative and reactive processes).
    We illustrate this perspective through the computational modeling of a naturalistic emotional situation.
    1   Introduction
Change is an inherent quality of emotion. Aroused by        praisal dimensions and peripheral appraisals that can
an unpleasant event, a person might explode into an-        be deliberative or learned [2].
ger, fume at a slow boil or collapse into a depression.        However, further progress is needed if appraisal
Once aroused, emotions influence our actions and            theories are to provide a full account of emotional
judgments concerning the event, altering what Lazarus       processes and their role in behavior. Specifically, theo-
[1] calls the person-environment relationship. Changes      ries must detail the processes by which each appraisal
to this relationship can induce new emotional re-           variable is determined, including the logical and tem-
sponses, resulting in a cycle of change in the person’s     poral dependencies between these appraisal processes
relation to the environment. These changes can be           [4]. Additionally, the basic calculus of how the results
rapid, on the order of milliseconds, or unfold over         of appraisal result in emotions of varying types, inten-
days and weeks. In short, emotions are inherently dy-
                                                            sities and durations must be determined. Completing
namic, linked to both the world’s dynamics and the
                                                            the cycle, the impact of emotions on coping responses
dynamics of the individual’s physiological, cognitive
and behavioral processes.                                   and subsequent changes in the environment-person
   A key challenge for any theory of emotion is to cap-     relationship must be detailed.
ture this dynamic emotional process. Over the last 50          We see computational models of emotion as a pow-
years appraisal theories have been established as a         erful approach to concretizing and exploring the dy-
leading theory of emotion. These theories argue that        namic properties of appraisal. The construction of a
emotion arises from a person’s interpretation of their      computational model forces specific commitments
overall relationship with the environment as character-     about how the person-environment relationship is rep-
ized by a set of appraisal variables (e.g., is this event   resented, how appraisals are performed on those repre-
desirable, who caused it, what power do I have over         sentations, the role of perception, memory, interpreta-
its unfolding). To date, however, appraisal theories        tion and inference in appraisal, and the relationship
have largely focused on structural considerations           between appraisals, emotions and coping responses.
(e.g., specifying the components of appraisal) [2]. Far     Often these commitments raise issues that are unfore-
less progress has been made in detailing the processes      seen at more abstract specifications of a theory. Fur-
that underlie appraisal, with some notable exceptions.      ther, once computationally realized, simulation allows
For example, Lazarus proposed a cyclical relationship       the model to be systematically explored and manipu-
between appraisal, coping and re-appraisal. At a finer      lated, thereby generating predictions that can be fur-
grain, Scherer’s sequential checking model argues for       ther tested with human subjects. Indeed given the
the sequencing of distinct appraisal checks (relevance,     complexity of appraisal theories, exploring dynamic
etc.) that determine the appraisal variables [3]. Smith     properties of a theory and contrasting alternative theo-
and Kirby argues for a two process model of appraisal       ries from a process-based perspective may arguably
whereby associative processing, a memory-based              hinge on their computer realization.
process and reasoning, a slower and more deliberative          This paper advocates a theoretical stance towards
process, operate in parallel [4]. Moors investigates        the problem of capturing emotional dynamic informed
automaticity of some appraisal processes [5]. Reisen-       both by the appraisal theory of Smith and Lazarus [6]
zein proposed a model of appraisal that distinguishes       and our experience in realizing this theory in a compu-
between hardwired appraisal processes for some ap-          tational model called EMA (EMotion and Adaptation).
                                                            In our view, process theories of appraisal have con-
```

<!-- page: 2 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: tabular spacing heuristic._

```text
  Frame 2                       Frame 5                      Frame 9                      Frame 22
 Frame 60                                 Frame 80                              Frame 272
                                                       Figure 1
flated appraisal and inference. Rather, we argue for a       the reactions of one of the two actors. Although such
single and automatic appraisal process that operates in      unexpected, uncontrolled event makes a rigorous
parallel over a person’s interpretation of their relation-   analysis problematic, this naturalistic setting serves
ship to the environment. Dynamics arises from percep-        well to illustrate the rapid dynamics that we would
tual and inferential processes operating independently       want to cover in a process model of appraisal.
over this interpretation (including deliberative and            In the video, the actor holding the umbrella goes
reactive processes). We illustrate this perspective by       through a sequence of behaviors that suggest the fol-
modeling of a naturalistic emotional situation in EMA.       lowing interpretation:
                                                             • surprise at an unexpected event (frame 2),
An example of emotion dynamics.                              • fear (12),
One approach to studying emotion dynamics is to use          • an aggressive stance of self-protection (13-23),
assessment tools that ask a subject to imagine an evolv-     • relaxation (29),
ing situation and to introspect on their emotional reac-     • concern for others (29-42), specifically for the bird
tions [7]. This approach has in turn been leveraged to           that caused the initial negative reaction and, finally,
evaluate computer-based models of emotion [8].               • an active helping strategy (62-80) combined with re-
   However, the focus in this paper is different. Instead        laxed facial features and smiling suggestive of relief.
of a slowly evolving situation, our interest here is in      The sequence of behaviors that suggest that interpretation
an evocative situation that elicits a wide array of emo-     is as follows. By frame 2 (F2), the actor has begun to turn
tional responses over a short time period. Specifically,     and orient toward the sound of the bird. Her eyebrows
we will analyze and model a naturalistic emotion-            rise (F3 through F4). The eyebrows return to a more neu-
invoking situation recorded fortuitously during one of       tral level and the mouth begins to open by F8. The Eye-
our lab studies. We were videotaping actors at 30            brows lower and the jaw then drops during F11 and F12.
frames per second as part of a study on gestures and         In F13, she begins to grab the umbrella at the base, move
postures. In the midst of instructing the actors, a pi-      the left foot back away from bird and starts to raise arms.
geon unexpectedly flew in window. Figure 1 reveals           She raises the umbrella (F14 through F22), shifting her
```

<!-- page: 3 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: tabular spacing heuristic._

```text
weight to her right, rear foot away from the bird. Her         and Smith and Lazarus’ work in particular, is that ap-
posture and grasp of the umbrella suggests she is pre-         praisal and coping center around a person’s interpreta-
pared to ward off a presumed attack of the bird by             tion of their relationship with the environment. This in-
whacking it with the umbrella. She continues her back-         terpretation is constructed by cognitive processes, sum-
ward motion. Her motions slow and by F29 her left hand         marized by appraisal variables and altered by coping re-
starts to let go of the umbrella and move towards her          sponses. The goal of our work is to develop a process
mouth. The umbrella is lowered in F34 and her left hand        model of appraisal, realized as a computational process.
covers her mouth by frame F42. By F62 the backward                In our view, there are several key challenges for a
motion stops (she moves approximately 6 feet) and the          computational model of appraisal. The model must
left hand begins to lower from covering her mouth. By          explain both the rapid dynamics of some emotional
F66, the actor begins to move forward and the hand low-        reactions as well as the slower evolution of emotional
ers sufficiently to reveal relaxed facial features. In F72     responses that may follow some deliberation and in-
through F80, the forward motion continues, the hand            ferences. The appraisal processes must in some way be
forms into a stop gesture and the face appears to be smil-     consistent with both these reactive and deliberative
ing (laughter and utterances expressing concern for the        responses. In addition, appraisal processes must oper-
bird are also heard).                                          ate over a range of phenomena spanning simple physi-
   A seemingly identical sequence of reactions is visi-        cal phenomena as well as complex social situations.
ble in the other actor: raised eyebrows, lowered eye-             These challenges are often addressed by presuming
brows and jaw drop, followed by expressions suggest-           multiple appraisal processes, for example fast pattern
ing relief/amusement and compassion. But reactions             directed appraisal processes and slower more delibera-
also differ, for she becomes aware of the bird later, she      tive appraisals (e.g., [2, 4, 5]). Our theoretical stance
is closer to the threat and certain responses are not          on this issue differs considerably.
facilitated by the instrumentality of the umbrella.               First and foremost, we cleanly delineate between
   This rapid transition in the actor’s expressive state       appraisal and inference. We argue that appraisal proc-
and behaviors lasts 2.6 seconds. The expression of             esses are always fast (reactive), parallel and unique in
raised eyebrows often associated with surprise takes           the sense that we postulate a single process for each
on the order 30-60 milliseconds and the expression of          appraisal variable. However, multiple other perceptual
lowered eyebrows and lowered jaw often associated              and cognitive processes perform inference (both fast
with anger and responses to threat takes on the order          and slow, both deliberative and reactive) over the in-
300 milliseconds. Overall, this suggests an interesting        terpretation of the person-environment relationship. As
progression from an appraisal of unexpectedness, to an         those inference processes change the interpretation,
assessment of personal significance, and finally an            they indirectly trigger automatic reappraisal.
appraisal of significance to others (cf. Scherer’s se-            Thus, debates about which emotions have a cogni-
quential checking, [3]). Tightly coupled with these            tive or non-cognitive basis become moot. The relation
appraisal dynamics from threat-to-self to threat-to-           between cognition and appraisal is that appraisals op-
other and emotion dynamics of Fear/Anger to Com-               erate on the results of cognitive operations as well as
passion/Relief, there is a corresponding progression of        any other operation that transforms the person’s inter-
coping responses from defend/attack to help.                   pretation of their relationship to the environment.
   Several factors can help us explain these dynamics.
The process of appraising the situation in terms of its        A computational model of appraisal
unexpectedness, congruency with the actor’s concerns,          EMA is a computational model that realizes this theoreti-
etc. can have its own internal dynamics grounded in            cal stance. We now sketch the basic outlines of the model
such factors as inferential demands that underlie the          (See [9] for a more complete description.) In general
appraisal and/or potential logical ordering relations          terms, we characterize a computational model as a proc-
between steps of the appraisal. As Scherer’s theory of         ess or processes operating on representations. A compu-
appraisal checks argues, there may be a temporal               tational model of appraisal is a set of processes that inter-
course to the appraisal process. There are also percep-        pret and manipulate a representation of the person-
tual processes and inferential processes that alter the        environment relationship.
actor’s interpretation of the situation appraisal. Simi-          In our view, a core requirement for the representa-
larly there are the processes of forming a coping re-          tion of the person-environment relation is that it sup-
sponse or plan to deal with the event. Finally, the            port the derivation of appraisal variables. Moreover, as
situation and subsequent re-appraisals occur due to            we argue that appraisal is fast and uniform, the repre-
changes as the event unfolds. This unfolding occurs            sentation must facilitate that assessment over the range
both due to the actor’s response of “arming herself”           of phenomena that induce emotional reactions.
and moving away from the event as well as other enti-             To address those requirements, EMA uses a repre-
ties becoming the focus (such as the threat to the bird).      sentation built on the causal representations developed
                                                               for decision-theoretic planning augmented by explicit
Basic Theoretical Assumptions                                  representation of intentions and beliefs (necessary for
A central tenant in cognitive appraisal theories in general,   social attributions). The appraisal of relevance can be
```

<!-- page: 4 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: short-line density suggests columns._

```text
expressed by a plan representation’s ability to express         •    Controllability: can the outcome be altered by ac-
the causal relationship between events and states.                   tions under control of the agent whose perspective is
These causal representations are also critical for as-               taken. This is derived by looking for actions in the
sessing causal attributions necessary for appraising                 causal interpretation that could establish or block
blame or credit-worthiness. Appraisal variables of de-               some effect, and that are under the control of the
sirability and likelihood can be modeled by the deci-                agent who’s perspective is being judged (i.e, agent X
sion-theoretic concepts of utility and probability. Ex-              could execute the action).
plicit representations of intentions and beliefs are also       • Changeability: can the outcome be altered by some
critical for properly reasoning about causal attribu-                other causal agent.
tions, as these involve reasoning if the causal agent           Each appraised event is mapped to an emotion instance
intended or foresaw the consequences of their actions.          of a type and intensity following the structural scheme
The commitments to beliefs and intentions also play a           proposed by Ortony et al. [11]. A simple activation-based
role in modeling coping strategies, especially what is          focus of attention model computes a current emotional
often called emotion-focused coping.                            state based on most recently accessed emotion instances.
   We call the agent’s interpretation of its “agent-               Another key aspect of EMA is that it includes a
environment relationship” the causal interpretation. It         computational model of coping integrated with the
provides a uniform, explicit representation of the              appraisal process (according to Lazarus’s theory).
agent’s beliefs, desires, intentions, plans and prob-           Coping determines how one responds to the appraised
abilities that allows uniform, fast appraisal processes,        significance of events. Coping strategies are proposed
regardless of differences in the underlying phenomena           to maintain desirable or overturn undesirable in-focus
being appraised. In the terminology of Smith and                emotion instances. Coping strategies essentially work
Lazarus, the causal interpretation is a declarative rep-        in the reverse direction of appraisal, identifying the
resentation of the current construal of the person-             precursors of emotion in the causal interpretation that
environment relationship.                                       should be maintained or altered (e.g., beliefs, desires,
   Reactive and more deliberative processes map their           intentions and expectations). Strategies include:
results into this uniform representation. Architectur-          • Action: select an action for execution
ally, this is achieved by a blackboard-style model. The         • Planning: form an intention to perform some act (the
causal interpretation (corresponding to the agent’s                  planner uses intentions to drive its plan generation)
working memory) encodes the input, intermediate re-             • Seek instrumental support: ask someone that is in
sults and output of reasoning processes that mediate                 control of an outcome for help
between the agent’s goals and its physical and social           • Procrastination: wait for an external event to change
environment (e.g., perception, planning, explanation,                the current circumstances
and natural language processing). At any point in time,
                                                                • Positive reinterpretation: increase utility of positive
the causal interpretation represents the agent’s current
                                                                     side-effect of an act with a negative outcome
view of the agent-environment relationship, which
                                                                • Acceptance: drop a threatened intention
changes with further observation or inference. We
                                                                • Denial: lower the probability of a pending undesir-
treat appraisal as a set of feature detectors that map
                                                                     able outcome
features of the causal interpretation into appraisal
variables. For example, an effect that threatens a de-          • Mental disengagement: lower utility of desired state
sired goal is assessed as a potential undesirable event.        • Shift blame: shift responsibility for an action toward
   Events are characterized in terms of appraisal vari-              some other agent
ables via domain-independent functions that examine             • Seek/suppress information: form positive or negative
the syntactic structure of the causal interpretation:                intention to monitor a pending or unknown state
• Perspective: viewpoint that the event judged                  Strategies give input to the cognitive processes that actu-
• Desirability: what is the utility (positive or negative)      ally execute these directives. For example, planful coping
     of the event if it comes to pass, from the perspective     generates an intention to act, which in turn leads to the
     taken (e.g., does it causally advance or inhibit a state   planning system to generate and execute a valid plan to
     of some utility). The utility of a state may be intrin-    accomplish this act. Alternatively, coping strategies
     sic (agent X attributes utility Y to state Z) or derived   might abandon the goal, lower the goal’s importance, or
     (state Z is a precondition of a plan that, with some       re-assess who is to blame.
     likelihood, will achieve an end with intrinsic utility).      Not every strategy applies to a stressor (e.g., an agent
                                                                cannot be problem directed if it is unaware of actions
• Likelihood: how probable is the outcome of the
                                                                impacting the situation), but multiple strategies can ap-
     event, derived from the decision-theoretic plan.
                                                                ply. EMA proposes strategies in parallel but adopts them
• Causal attribution: who deserves credit/blame. This
                                                                sequentially. A set of preferences resolve ties: e.g., EMA
     depends on what agent was responsible for executing
                                                                prefers problem directed strategies if control is appraised
     the action, but may also involve considerations of in-
                                                                as high (take action, plan, seek information), procrastina-
     tention, foreknowledge and coercion (see [10]).
                                                                tion if changeability is high, and emotion-focus strategies
• Temporal status: is this past, present, or future
                                                                if control and changeability are low.
```

<!-- page: 5 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: tabular spacing heuristic._

```text
                                                                                  from our video analysis.
                                                                                     Our goal is not to definitively ex-
                                                                                  plain and reconstruct the inferences
                                                                                  and emotions experienced by this ac-
                                                                                  tor, but rather to illustrate how such
                                                                                  dynamic situations could be modeled
                                                                                  by EMA. Many encodings are possi-
                                                                                  ble. We did, however, try to adopt
                                                                                  what we felt were plausible inferences
                                                                                  and beliefs based on post hoc analysis
                                                                                  of the situation.
                                                                                     Figure 2 illustrates a snapshot of
                                                                                  EMA's causal interpretation several
                                                                                  steps into the situation. EMA makes
                                                                                  discrete changes to this interpretation
                                                                                  over time, as a result of perceptual and
                 Figure 2: The causal interpretation at time t4                   inferential updates. (The Soar cogni-
                                                                                  tive architecture, upon which EMA is
   In developing a computational model of coping, we
                                                                based, assumes that updates occur once every 100 mil-
have moved away from broad distinctions of problem-
                                                                liseconds.) The time stamps at the bottom (e.g., t0)
focused and emotion-focused strategies. Formally rep-
                                                                indicate the discrete time step in which elements are
resenting coping requires a crispness lacking from the
                                                                added or deleted from the causal interpretation. Verti-
problem-focused/emotion-focused distinction. In par-
                                                                cal rectangles indicate actions including two degener-
ticular, much of what counts as problem-focused cop-
                                                                ate actions *init* (whose effects generate the initial
ing in the clinical literature is really inner-directed in a
                                                                state of the simulation) and *goal* (whose precondi-
emotion-focused sense. For example, one might form
                                                                tions correspond to the agent's goals). Ovals indicate
an intention to achieve a desired state – and feel better
                                                                predicates that describe the current beliefs of some
as a consequence – without ever acting on the inten-
                                                                feature of the world, including its likelihood of being
tion. Thus, by performing cognitive acts like planning,
                                                                true and its utility. For example, "have Um" indicates
one can improve ones interpretation of circumstances
                                                                that the actor has an umbrella (Um) and believes this
without actually changing the physical environment.
                                                                with certainty (Pr=1.0) and that this fact has no intrin-
   To summarize, an agent’s causal interpretation is
                                                                sic value (U=0.0). Predicates are linked to actions
equated with the output and intermediate results of
                                                                (representing the actions preconditions or effects) or to
processes that relate the agent to its physical and so-
                                                                other states via establishment links (i.e., this effect
cial environment. This configuration of beliefs, de-
                                                                establishes a precondition for some other action) or
sires, plans, and intentions represents the agent’s cur-
                                                                threat links (i.e., this effect deletes a precondition for
rent view of the agent-environment relationship, which
                                                                some other action). So, for example, the simulation
may subsequently change with further observation or
                                                                began at time t0 and the actor was healthy, holding an
inference. We treat appraisal as a mapping from do-
                                                                umbrella and the umbrella was lowered. Finally, 3D
main-independent features of causal interpretation to
                                                                rectangles indicate appraisal frames. Each frame con-
individual appraisal variables. Multiple appraisals are
                                                                sists of number, indicating at which time step it was
aggregated into an overall emotional state that influ-
                                                                generated, a set of appraisal variables and an emo-
ences behavior. Coping directs control signals to auxil-
                                                                tional label (due to space, the appraisal variables are
iary reasoning modules (i.e., planning, action selec-
                                                                omitted for all but the 4th frame.
tion, belief updates, etc.) to overturn or maintain fea-
                                                                   To complete a model, one must provide several ad-
tures of the causal interpretation that lead to individual
                                                                ditional elements that sit outside the causal interpreta-
appraisals. For example, coping may resign the agent
                                                                tion. EMA is provided with a plan library that consists
to a threat by abandoning the desire. The causal inter-
                                                                of a set action definitions and set of recipes indicating
pretation could be viewed as a representation of work-
                                                                how these actions could be combined. These recipes
ing memory (for those familiar with psychological
                                                                are used both for plan generation and plan recognition
theories) or as a blackboard (for those familiar with
                                                                (as when inferring behavior of other agents based on
blackboard architectures).
                                                                observed actions and world states). EMA can also be
Model of the Bird                                               provided with a set of simple stimulus-response rules
The bird example can be readily encoded into EMA.               that automatically trigger actions when certain world
Some of the dynamics of the situation arise from the            state configurations are perceived. Finally, EMA must
world but others arise from the inferential processes of        be connected to some world simulation that defines
the model. Here we describe an encoding that produces           how percepts change as a result of actions. We de-
the hypothesized emotional transitions that we derived          scribe these additional elements in the course of de-
                                                                scribing the evolving situation.
```

<!-- page: 6 -->
_Layout-sensitive page. Review page image if fidelity matters. Reason: tabular spacing heuristic._

```text
   At time t0 the actor has the high utility goal of being            plan step. This plan, however, is overtaken by events
healthy and this goal has been established in the initial             as the bird becomes caught in another actor's hair, dis-
state. This establishment is automatically appraised as               abling the "attack" and possibly resulting in the unde-
desirable and certain, resulting in joy.1                             sirable state that the bird will be injured.
   At time t1 a sound is perceived, denoted in the
causal interpretation by the predicate "sound". As no                 Concluding Remarks
currently executing action predicted that a sound                     Computational models of psychological phenomena are
would occur, this is unexpected and EMA records this                  powerful research tools. The development of a model can
unknown event in the causal history. EMA supports                     help concretize theories, reveal shortcomings and can
partial observability of the world's state and this model             help derive predictions through simulations.
we assume the bird cannot be seen unless the actor                       EMA provides a framework for exploring and ex-
looks at it. To model this, we define a S-R rule that                 plaining emotion dynamics. In particular, the simula-
executes the “Look” action if a sound is perceived.                   tion of the bird example, and the emotional dynamics
   At time t2, EMA has perceived the flying bird. Typi-               it reveals, argues that the temporal characteristics of
cally, EMA is attached to a simulation environment                    appraisal may be a by-product of other perceptual and
that would maintain the world state and compute the                   cognitive processes. By modeling appraisal as a fast,
observable effects of actions such as "look." For the                 uniform processes, EMA roots the temporal dynamics
model, we accomplish this through a domain-specific                   in those other processes that operate on the causal in-
procedure that sets "flying bird" to be true 100 ms af-               terpretation. Further, EMA’s description of appraisal is
ter looking at the sound. By definition in the model,                 economical, not requiring appeal to alternative fast and
"flying bird" has some negative utility for the action                slow appraisal processes.
(U=−10). Thus, the effect of this action is appraised as
undesirable and certain, leading to distress.
                                                                      Acknowledgments
                                                                      This work was supported by the HUMAINE Network
   At time t3, EMA infers the bird will attack. This fol-
lows from a general plan recognition approach in-                     References
formed by the domain-specific plan recipes. This ac-                  1. Lazarus, R., Emotion and Adaptation. 1991, NY: Oxford
tion has one precondition "bird-flying" that is estab-                    University Press.
lished by the unexpected event in t1. It has one effect               2. Reisenzein, R., Appraisal Processes Conceptualized from a
"injured" denoting that the actor will become un-                         Schema-Theoretic Perspective, in Appraisal Processes in
healthy as a result of this action and this threatens the                 Emotion: Theory, Methods, Research, K.R. Scherer, A.
actor's goal of being healthy.2 This threat relation is                   Schorr, and T. Johnstone, Editors. 2001, Oxford Press. p..
automatically appraised as undesirable, uncertain (as                 3. Scherer, K.R., Appraisal Considered as a Process of Multi-
                                                                          level Sequential Checking, in Appraisal Processes in Emo-
the effect may not occur), and uncontrollable (as there
                                                                          tion: Theory, Methods, Research, K.R. Scherer, A. Schorr,
is no explicit way to respond to the threat, given the                    and T. Johnstone, Editors. 2001, Oxford University Press..
current causal interpretation). The result is Fear.                   4. Smith, C.A. and L. Kirby, Consequences require antece-
   At time t4, EMA infers that there is a way to con-                     dents: Toward a process model of emotion elicitation, in
front the threat to the actor's health: whack the bird                    Feeling and Thinking: The role of affect in social cognition,
with the umbrella. This follows from the general plan                     J.P. Forgas, Editor. 2000, Cambridge University Press.
generation approach informed by the domain-specific                   5. Moors, A., et al., Unintentional processing of motivational
recipes. The planner has determined that the bird's                       valence. The Quarterly Journal of Exp. Psychology, 2005.
"plan" can be confronted by blocking its precondition.                6. Smith, C.A. and R. Lazarus, Emotion and Adaptation, in
                                                                          Handbook of Personality: theory & research, L.A. Pervin,
The planner further infers that the probability of the
                                                                          Editor. 1990, Guilford Press: NY. p. 609-637.
bird's "plan" succeeding is significantly reduced.                    7. Perrez, M. and M. Reicherts, Stress, Coping, and Health.
Given that there is now an action in the causal inter-                    1992, Seattle, WA: Hogrefe and Huber Publishers.
pretation under control of the actor and addressing the               8. Gratch, J. and S. Marsella, Evaluating a computational
threat to health, the threat to health is automatically                   model of emotion. Journal of Autonomous Agents and Mul-
reappraised as controllable. The result is Anger.                         tiagent Systems (in press), 2005.
   We have modeled the scenario to this point, though                 9. Gratch, J. and S. Marsella, A domain independent frame-
the remainder is straightforward. The model must next                     work for modeling emotion. Journal of Cognitive Systems
infer that it must raise the umbrella to satisfy the                      Research, 2004. 5(4): p. 269-306.
                                                                      10. Mao, W. and J. Gratch. Social Causality and Responsibility:
"whack" plan and subsequently execute this initial
                                                                          Modeling and Evaluation. in International Working Confer-
                                                                          ence on Intelligent Virtual Agents. 2005. Kos, Greece.
    1                                                                 11. Ortony, A., G. Clore, and A. Collins, The Cognitive Struc-
       Arguably, one has a no emotions to being healthy but only
                                                                          ture of Emotions. 1988: Cambridge University Press.
reacts when there is a threat to health or they are unhealthy.
EMA allows a utility distribution over predicates but we omit
this distinction in our example for simplicity.
     2
       In actuality, the effect of this action is to make “healthy”
false (i.e., “injured” is shorthand for NOT(healthy)). The same
holds for “dead bird” which denotes NOT(flying).
```
