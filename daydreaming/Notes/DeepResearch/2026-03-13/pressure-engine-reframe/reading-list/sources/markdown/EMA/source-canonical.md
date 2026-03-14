# EMA: A Computational Model of Appraisal Dynamics

Stacy Marsella  
Jonathan Gratch

University of Southern California  
`marsella@isi.usc.edu`  
`gratch@ict.usc.edu`

_Appears in Agent Construction and Emotions, 2006._

<!-- page: 1 -->
## Abstract

A computational model of emotion must explain both the rapid dynamics of some emotional reactions and the slower responses that follow deliberation. This is often addressed by positing multiple appraisal processes, such as fast pattern-directed appraisals versus slower deliberative appraisals. In the authors' view, that conflates appraisal with inference. Instead, they argue for a single and automatic appraisal process that operates over a person's interpretation of their relationship to the environment. Dynamics arise from perceptual and inferential processes operating on this interpretation, including both deliberative and reactive processes. They illustrate this perspective through the computational modeling of a naturalistic emotional situation.

## 1. Introduction

Change is an inherent quality of emotion. Aroused by an unpleasant event, a person might explode into anger, fume at a slow boil, or collapse into depression. Once aroused, emotions influence actions and judgments concerning the event, altering what Lazarus calls the person-environment relationship. Changes to this relationship can induce new emotional responses, resulting in a cycle of change in the person's relation to the environment. These changes can be rapid, on the order of milliseconds, or unfold over days and weeks. In short, emotions are inherently dynamic, linked both to the world's dynamics and to the dynamics of the individual's physiological, cognitive, and behavioral processes.

A key challenge for any theory of emotion is to capture this dynamic emotional process. Over the last fifty years, appraisal theories have become a leading theory of emotion. These theories argue that emotion arises from a person's interpretation of their overall relationship with the environment as characterized by a set of appraisal variables, for example whether an event is desirable, who caused it, and what power the person has over its unfolding. To date, however, appraisal theories have largely focused on structural considerations, such as specifying the components of appraisal. Far less progress has been made in detailing the processes that underlie appraisal, though there are notable exceptions.

For example, Lazarus proposed a cyclical relationship between appraisal, coping, and re-appraisal. At a finer grain, Scherer's sequential checking model argues for the sequencing of distinct appraisal checks, such as relevance, that determine the appraisal variables. Smith and Kirby argue for a two-process model of appraisal whereby associative processing, a memory-based process, and reasoning, a slower and more deliberative process, operate in parallel. Moors investigates the automaticity of some appraisal processes. Reisenzein proposed a model of appraisal that distinguishes between hardwired appraisal processes for some appraisal dimensions and peripheral appraisals that can be deliberative or learned.

However, further progress is needed if appraisal theories are to provide a full account of emotional processes and their role in behavior. Theories must detail the processes by which each appraisal variable is determined, including the logical and temporal dependencies between these appraisal processes. They must also determine the basic calculus by which the results of appraisal produce emotions of varying types, intensities, and durations. Completing the cycle, the impact of emotions on coping responses and subsequent changes in the environment-person relationship must be detailed.

The authors see computational models of emotion as a powerful way to concretize and explore the dynamic properties of appraisal. Building a computational model forces specific commitments about how the person-environment relationship is represented, how appraisals are performed on those representations, the role of perception, memory, interpretation, and inference in appraisal, and the relationship between appraisals, emotions, and coping responses. These commitments often raise issues that are not apparent at more abstract levels of theory. Once realized computationally, the model can be systematically explored and manipulated through simulation, generating predictions that can be tested with human subjects.

This paper advocates a theoretical stance toward the problem of emotional dynamics informed both by the appraisal theory of Smith and Lazarus and by the authors' experience realizing that theory in a computational model called EMA, for EMotion and Adaptation. In their view, process theories of appraisal have conflated appraisal and inference. Rather than multiple appraisal modules, they argue for a single and automatic appraisal process operating in parallel over a person's interpretation of their relationship to the environment. Dynamics arise from perceptual and inferential processes operating over that interpretation independently, including both deliberative and reactive processes.

<!-- page: 2 -->
## An Example of Emotion Dynamics

_Figure 1 presents snapshots from the bird episode at frames 2, 5, 9, 22, 60, 80, and 272._

One approach to studying emotion dynamics is to use assessment tools that ask a subject to imagine an evolving situation and introspect on their emotional reactions. That approach has in turn been leveraged to evaluate computer-based models of emotion. However, the focus here is different. Instead of a slowly evolving situation, the paper is interested in an evocative situation that elicits a wide array of emotional responses over a short period of time.

Specifically, the authors analyze and model a naturalistic emotion-invoking situation recorded fortuitously during one of their lab studies. They were videotaping actors at 30 frames per second as part of a study on gestures and postures. In the midst of instructing the actors, a pigeon unexpectedly flew in the window. Figure 1 reveals the reactions of one of the two actors. Although such an unexpected and uncontrolled event makes rigorous analysis difficult, this naturalistic setting serves well to illustrate the rapid dynamics that a process model of appraisal should cover.

In the video, the actor holding the umbrella appears to move through the following sequence:

- surprise at an unexpected event
- fear
- an aggressive stance of self-protection
- relaxation
- concern for others, specifically for the bird that caused the initial negative reaction
- an active helping strategy combined with relaxed facial features and smiling suggestive of relief

The behavior sequence unfolds quickly. By frame 2, the actor has begun to turn and orient toward the sound of the bird. Her eyebrows rise through frames 3 and 4. They return toward neutral and the mouth begins to open by frame 8. The eyebrows lower and the jaw drops during frames 11 and 12. In frame 13, she begins to grab the umbrella at the base, move the left foot back away from the bird, and raise her arms. She raises the umbrella through frame 22, shifting her weight to the rear foot. Her posture and grasp suggest she is prepared to ward off a presumed attack by striking the bird with the umbrella.

She continues moving backward. Her motions slow and by frame 29 her left hand begins to leave the umbrella and move toward her mouth. The umbrella is lowered in frame 34 and by frame 42 her left hand covers her mouth. By frame 62, the backward motion stops, after moving approximately six feet, and the hand begins to lower. By frame 66, the actor begins moving forward, and the hand lowers enough to reveal relaxed facial features. In frames 72 through 80, the forward motion continues, the hand forms a stop gesture, and the face appears to be smiling. Laughter and utterances expressing concern for the bird are also heard.

The other actor shows a broadly similar sequence: raised eyebrows, lowered eyebrows and jaw drop, then expressions suggesting relief, amusement, and compassion. But the reactions are not identical. She becomes aware of the bird later, she is closer to the threat, and some responses are not shaped by the instrumentality of the umbrella.

This rapid transition in expressive state and behavior lasts only 2.6 seconds. The expression of raised eyebrows often associated with surprise takes on the order of 30 to 60 milliseconds, and the expression of lowered eyebrows and lowered jaw often associated with anger and responses to threat takes on the order of 300 milliseconds. Overall, this suggests an interesting progression from an appraisal of unexpectedness, to an assessment of personal significance, and finally to an appraisal of significance to others. Closely coupled with these appraisal dynamics, from threat-to-self to threat-to-other and from fear and anger to compassion and relief, is a corresponding progression of coping responses from defend or attack to help.

Several factors help explain these dynamics. The process of appraising the situation in terms of unexpectedness, congruency with the actor's concerns, and so on can have its own internal dynamics, grounded in inferential demands and possible logical ordering relations between appraisal steps. As Scherer's theory of appraisal checks argues, there may be a temporal course to the appraisal process. There are also perceptual and inferential processes that alter the actor's interpretation of the situation. Similarly, there are processes involved in forming a coping response or plan to deal with the event. Finally, the situation and subsequent re-appraisals evolve as the event itself unfolds, both because the actor arms herself and moves away and because other entities become the focus, such as the threat to the bird.

<!-- page: 3 -->
## Basic Theoretical Assumptions

A central tenet in cognitive appraisal theories in general, and in Smith and Lazarus's work in particular, is that appraisal and coping center around a person's interpretation of their relationship with the environment. This interpretation is constructed by cognitive processes, summarized by appraisal variables, and altered by coping responses. The goal of the work is to develop a process model of appraisal, realized as a computational process.

In the authors' view, a computational model of appraisal must explain both the rapid dynamics of some emotional reactions and the slower evolution of emotional responses that may follow deliberation and inference. Appraisal processes must be consistent with both reactive and deliberative responses, and they must operate over phenomena ranging from simple physical events to complex social situations.

These challenges are often addressed by presuming multiple appraisal processes, for example fast pattern-directed appraisal and slower, more deliberative appraisal. The authors take a different stance. First, they sharply distinguish appraisal from inference. Appraisal processes are modeled as always fast, reactive, parallel, and unique in the sense that there is a single process for each appraisal variable. Other perceptual and cognitive processes can perform inference, whether fast or slow, reactive or deliberative, over the interpretation of the person-environment relationship. As those inference processes change the interpretation, they indirectly trigger automatic reappraisal.

On this view, debates about which emotions have a cognitive or non-cognitive basis become less useful. The relationship between cognition and appraisal is that appraisals operate on the results of cognitive operations, as well as any other operation that transforms the person's interpretation of their relationship to the environment.

## A Computational Model of Appraisal

EMA is a computational model that realizes this theoretical stance. In general terms, a computational model is a process or set of processes operating on representations. A computational model of appraisal is therefore a set of processes that interpret and manipulate a representation of the person-environment relationship.

In the authors' view, a core requirement for the representation of the person-environment relation is that it support the derivation of appraisal variables. Because appraisal is assumed to be fast and uniform, the representation must facilitate that assessment across the full range of phenomena that induce emotional reactions. To address that requirement, EMA uses a representation built on the causal representations developed for decision-theoretic planning, augmented by explicit representations of intentions and beliefs, which are necessary for social attributions.

The appraisal of relevance can be expressed through a plan representation's ability to capture the causal relationship between events and states. These causal representations are also critical for assessing causal attributions necessary for appraising blame or credit-worthiness. Appraisal variables such as desirability and likelihood can be modeled through the decision-theoretic concepts of utility and probability. Explicit representations of intentions and beliefs are also required for properly reasoning about causal attributions, since those involve reasoning about whether a causal agent intended or foresaw the consequences of their actions. The same commitments to beliefs and intentions play a role in modeling coping strategies, especially what is often called emotion-focused coping.

The authors call the agent's interpretation of its agent-environment relationship the **causal interpretation**. It provides a uniform and explicit representation of the agent's beliefs, desires, intentions, plans, and probabilities, allowing uniform and fast appraisal processes regardless of the specific phenomenon being appraised. In the terminology of Smith and Lazarus, the causal interpretation is a declarative representation of the current construal of the person-environment relationship.

Reactive and more deliberative processes both write their results into this shared representation. Architecturally, EMA uses a blackboard-style model. The causal interpretation, corresponding to the agent's working memory, encodes the input, intermediate results, and output of reasoning processes that mediate between the agent's goals and its physical and social environment, including perception, planning, explanation, and natural language processing. At any given moment, the causal interpretation represents the agent's current view of the agent-environment relationship, and that view can change with further observation or inference. Appraisal is then treated as a set of feature detectors that map features of the causal interpretation into appraisal variables. For example, an effect that threatens a desired goal is assessed as a potential undesirable event.

<!-- page: 4 -->
### Appraisal Variables

EMA characterizes events in terms of domain-independent appraisal functions that examine the syntactic structure of the causal interpretation:

- **Perspective**: the viewpoint from which the event is judged.
- **Desirability**: the utility, positive or negative, of the event if it comes to pass from the perspective taken.
- **Likelihood**: how probable the outcome is, derived from the decision-theoretic plan.
- **Causal attribution**: who deserves credit or blame.
- **Temporal status**: whether the event is past, present, or future.
- **Expectedness**: whether the event was predicted or arrives unexpectedly.
- **Controllability**: whether the outcome can be altered by actions under the control of the relevant agent.
- **Changeability**: whether the outcome can be altered by some other causal agent.

Each appraised event is mapped to an emotion instance of a type and intensity following the structural scheme proposed by Ortony, Clore, and Collins. A simple activation-based focus-of-attention model computes a current emotional state based on the most recently accessed emotion instances.

### Coping Strategies

Another key aspect of EMA is that it includes a computational model of coping integrated with the appraisal process in a way aligned with Lazarus's theory. Coping determines how one responds to the appraised significance of events. Coping strategies are proposed so as to maintain desirable or overturn undesirable in-focus emotion instances. In effect, coping works in the reverse direction of appraisal, identifying the precursors of emotion in the causal interpretation that should be maintained or altered, such as beliefs, desires, intentions, and expectations.

The paper lists the following coping strategies:

- **Action**: select an action for execution.
- **Planning**: form an intention to perform some act.
- **Seek instrumental support**: ask someone who is in control of an outcome for help.
- **Procrastination**: wait for an external event to change the current circumstances.
- **Positive reinterpretation**: increase the utility of a positive side-effect of an act with a negative outcome.
- **Acceptance**: drop a threatened intention.
- **Denial**: lower the probability of a pending undesirable outcome.
- **Mental disengagement**: lower the utility of a desired state.
- **Shift blame**: shift responsibility for an action toward some other agent.
- **Seek or suppress information**: form a positive or negative intention to monitor a pending or unknown state.

Strategies provide input to the cognitive processes that actually carry out these directives. For example, planful coping generates an intention to act, which then drives the planning system to generate and execute a valid plan. Alternatively, coping strategies might abandon a goal, lower its importance, or reassess who is to blame.

Not every strategy applies to every stressor, but multiple strategies can apply to the same one. EMA proposes strategies in parallel but adopts them sequentially. A set of preferences resolves ties: problem-directed strategies are preferred when control is high; procrastination is preferred when changeability is high; and emotion-focused strategies are preferred when both control and changeability are low.

<!-- page: 5 -->
In developing this computational model of coping, the authors moved away from broad distinctions such as problem-focused versus emotion-focused coping. Formally representing coping requires a crispness that those broad distinctions often lack. In particular, much of what counts as problem-focused coping in the clinical literature is really inner-directed in an emotion-focused sense. One might form an intention to achieve a desired state, and feel better as a consequence, without ever acting on that intention. Thus, cognitive acts like planning can improve an agent's interpretation of circumstances without actually changing the physical environment.

To summarize, an agent's causal interpretation is equated with the output and intermediate results of processes that relate the agent to its physical and social environment. This configuration of beliefs, desires, plans, and intentions represents the agent's current view of the agent-environment relationship and may change with further observation or inference. Appraisal is treated as a mapping from domain-independent features of causal interpretation to individual appraisal variables. Multiple appraisals are aggregated into an overall emotional state that influences behavior. Coping, in turn, directs control signals to auxiliary reasoning modules such as planning, action selection, and belief update so as to overturn or maintain features of the causal interpretation that lead to individual appraisals.

## Model of the Bird

The bird example can be readily encoded into EMA. Some of the dynamics arise from the world itself, while others arise from the model's inferential processes. The authors describe an encoding that produces the hypothesized emotional transitions they derived from video analysis. Their goal is not to definitively reconstruct the actual inferences and emotions experienced by the actor, but to illustrate how such dynamic situations could be modeled by EMA. Many encodings are possible, though they aimed for what seemed to them plausible inferences and beliefs based on post hoc analysis of the situation.

_Figure 2 illustrates a snapshot of EMA's causal interpretation at time `t4`._

EMA makes discrete changes to this interpretation over time as a result of perceptual and inferential updates. The Soar cognitive architecture, on which EMA is based, assumes that updates occur once every 100 milliseconds. The time stamps at the bottom of the figure, such as `t0`, indicate the discrete time step in which elements are added to or deleted from the causal interpretation. Vertical rectangles indicate actions, including the degenerate actions `init` and `goal`. Ovals indicate predicates describing current beliefs about features of the world, including their likelihood and utility. Predicates are linked to actions or to other states via establishment links and threat links. Three-dimensional rectangles indicate appraisal frames. Each frame consists of a number, indicating when it was generated, together with a set of appraisal variables and an emotional label.

To complete a model, several additional elements must sit outside the causal interpretation. EMA is provided with a plan library containing both action definitions and recipes indicating how actions can be combined. These recipes are used both for plan generation and for plan recognition, for example when inferring the behavior of other agents from observed actions and world states. EMA can also be provided with simple stimulus-response rules that automatically trigger actions when certain world-state configurations are perceived. Finally, EMA must connect to a world simulation that defines how percepts change as a result of actions.

<!-- page: 6 -->
The bird scenario then unfolds as follows:

- **`t0`**: the actor has the high-utility goal of being healthy, and that goal has been established in the initial state. This is automatically appraised as desirable and certain, resulting in joy.[^1]
- **`t1`**: a sound is perceived and represented in the causal interpretation by the predicate `sound`. Because no currently executing action predicted that a sound would occur, it is appraised as unexpected, and EMA records this unknown event in the causal history. A stimulus-response rule triggers a `Look` action when a sound is perceived.
- **`t2`**: EMA perceives the flying bird. In the model, a domain-specific procedure sets `flying bird` to true 100 milliseconds after looking toward the sound. Since `flying bird` has negative utility for the actor, the effect is appraised as undesirable and certain, leading to distress.
- **`t3`**: EMA infers that the bird will attack. This follows from a general plan-recognition process informed by domain-specific plan recipes. The inferred action threatens the actor's goal of being healthy and is automatically appraised as undesirable, uncertain, and uncontrollable. The result is fear.
- **`t4`**: EMA infers that the actor can confront the threat by whacking the bird with the umbrella. This follows from general plan generation informed by the same domain-specific recipes. Once the threat is appraised as controllable, the emotion shifts to anger.

The model can continue from there in a straightforward way. It must next infer that the umbrella must be raised to satisfy the `whack` plan and then execute that initial step. However, this plan is overtaken by events when the bird becomes caught in another actor's hair, disabling the attack and introducing the new undesirable possibility that the bird will be injured.[^2]

## Concluding Remarks

Computational models of psychological phenomena are powerful research tools. Developing a model can help concretize theories, reveal shortcomings, and derive predictions through simulation.

EMA provides a framework for exploring and explaining emotion dynamics. In particular, the simulation of the bird example argues that the temporal characteristics of appraisal may be a by-product of other perceptual and cognitive processes. By modeling appraisal as a fast, uniform process, EMA roots temporal dynamics in those other processes operating on the causal interpretation. The resulting theory is more economical, because it does not require separate fast and slow appraisal systems.

## Acknowledgments

This work was supported by the HUMAINE Network.

## References

1. Lazarus, R. _Emotion and Adaptation_. New York: Oxford University Press, 1991.
2. Reisenzein, R. "Appraisal Processes Conceptualized from a Schema-Theoretic Perspective," in _Appraisal Processes in Emotion: Theory, Methods, Research_, edited by K. R. Scherer, A. Schorr, and T. Johnstone. Oxford Press, 2001.
3. Scherer, K. R. "Appraisal Considered as a Process of Multilevel Sequential Checking," in _Appraisal Processes in Emotion: Theory, Methods, Research_, edited by K. R. Scherer, A. Schorr, and T. Johnstone. Oxford University Press, 2001.
4. Smith, C. A., and L. Kirby. "Consequences Require Antecedents: Toward a Process Model of Emotion Elicitation," in _Feeling and Thinking: The Role of Affect in Social Cognition_, edited by J. P. Forgas. Cambridge University Press, 2000.
5. Moors, A., et al. "Unintentional Processing of Motivational Valence." _The Quarterly Journal of Experimental Psychology_, 2005.
6. Smith, C. A., and R. Lazarus. "Emotion and Adaptation," in _Handbook of Personality: Theory and Research_, edited by L. A. Pervin. New York: Guilford Press, 1990, 609-637.
7. Perrez, M., and M. Reicherts. _Stress, Coping, and Health_. Seattle, WA: Hogrefe and Huber Publishers, 1992.
8. Gratch, J., and S. Marsella. "Evaluating a Computational Model of Emotion." _Journal of Autonomous Agents and Multiagent Systems_ (in press), 2005.
9. Gratch, J., and S. Marsella. "A Domain Independent Framework for Modeling Emotion." _Journal of Cognitive Systems Research_ 5, no. 4 (2004): 269-306.
10. Mao, W., and J. Gratch. "Social Causality and Responsibility: Modeling and Evaluation." In _International Working Conference on Intelligent Virtual Agents_. Kos, Greece, 2005.
11. Ortony, A., G. Clore, and A. Collins. _The Cognitive Structure of Emotions_. Cambridge University Press, 1988.

[^1]: The paper notes that one might argue a person has no emotion about being healthy and only reacts when health is threatened or lost. EMA allows a utility distribution over predicates, but the example omits that distinction for simplicity.

[^2]: Strictly speaking, the effect is to make `healthy` false, that is, `injured` is shorthand for `NOT(healthy)`. The same holds for `dead bird`, which denotes `NOT(flying)`.
