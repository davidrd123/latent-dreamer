# Chapter 7: Implementation of DAYDREAMER

<!-- page: 187 -->

This chapter describes the implementation of DAYDREAMER in detail: First, we present statistics on the program. Second, we present the GATE representation language. Third, we discuss the representation of DAYDREAMER planning and inference rules in this language. Fourth, we discuss the basic procedures for daydreaming. Fifth, we revise these basic procedures to incorporate analogical planning and episodic memory. Sixth, we discuss the modifications necessary to support forward and backward planning from the viewpoint of others. Seventh, we discuss the procedures for serendipity and mutation.

## 7.1 Summary Statistics

DAYDREAMER consists of 12,739 lines of code, broken down by major sections as follows: Component

TControl

Lines

~eeevewe 717,

Planner Episodic memory Reversal Serendipity Mutation Misc Generator Representations Total

2186 915 553 762 447 1315 2122 SA 12,739

A large percentage of the code is devoted to the planner and the representations and planning knowledge for the interpersonal domain. The rest of the

<!-- page: 188 -->

components are relatively compact. DAYDREAMER consumes 2,521,088 bytes of memory - Gmeeeprered yiin T version 3.0 on an Apollo DN460. This figure does not include the GATE language,

which consumes 346,112 bytes of memory (compiled). A number of statistics on the program were obtained from the traces of a complete run; annotated trace excerpts are provided in Appendix A. The following statistics were obtained for the planner: Event Rule firings Context sprouts Planning rule firings Inference rule firings Subgoal relaxations

Count 322 186 186 136 15

Input states/actions

6

Output actions Side-effect personal goals

20 8

The planning rule firings broke down as follows: Type

Count

Percentage

130 44 9 3

69.9 % 23.6 % 4.8 % 1.67 %

Regular planning rules Analogical planning rules Coded (Lisp) planning rules Backward other planning rules

Thus almost one quarter of the planning rule applications were carried out through analogical application of a planning episode. There were four serendipities, and one mutation. The following statistics were obtained for concerns: Event Initiation

Count 22

Success Failure

15 4

The program switched from daydreaming mode to performance mode four times, and switched from performance mode to daydreaming mode three times. The following statistics were obtained for episodic memory: Event Stored episodes Retrieved episodes Activated indices

Count 12 8 71

Sixteen hand-coded episodes were initially provided to the program. After adding 12 more episodes, the program contained 28 episodes. Five new planning rules were added to the program’s collection of rules. The program ended up using two of them later on.

<!-- page: 189 -->

The distribution of rule utilization was as follows:

Times Used

Number of Rules

22 20 12 10 9 ff

1 2 1 1 1 3

Believe-plan1 Mtrans-plan2 At-plan2 Ptrans-plan Enable-future-vprox-plan1 Vprox-plan2 Vprox-plan3 Vprox-plan1 Mtrans-plan1

i) 4

6 10

Rprox-plan M-phone-plan1 M-agree-plan Romantic-interest-plan Reversal-theme

3 2 1

8 31 52

Know-plan2 Employment-theme-plan Under-doorway-plan Reversal-plan Well-dressed-plan2 Star-plan

Sample Rules

A total of 116 rules were used; 114 of those rules were initially provided to the program. Since the program was provided with 135 rules initially, 21 rules ended up never being used.

## 7.2 The GATE Representation Language

DAYDREAMER is implemented using GATE (E. T. Mueller, 1987a; E. T. Mueller & Zernik, 1984), a graphical artificial intelligence program development tool for the T language (Rees, N. I. Adams, & Meehan, 1984), a version of the Scheme dialect of Lisp (Winston & Horn, 1981; McCarthy, Abrahams, Edwards, Hart, & Levin, 1965), running on Apollo Domain and Hewlett-Packard Bobcat workstations. GATE consists of slot-filler objects, a textual representation for these objects, operations on these objects such as unification, instantiation, and variabilization, and a context mechanism. GATE also includes a graphical interface which provides visual animation of a running program as it manipulates slot-filler objects, and enables one to create, view, and modify objects graphically.

### 7.2.1 Slot-Filler Objects

Slot-filler objects are similar to the slot-filler objects of Schank and Riesbeck (1981), frames (Minsky, 1975), Lisp a-lists (McCarthy et al., 1965), and the structures or records of traditional programming languages such as Pascal (Wirth, 1971).

Slot-filler objects consist of: (a) one or more object names, (b) a type, and or more pairs, where each pair consists of a slot name and a slot value. zero (c) Object names and slot names are Lisp atoms. Types are organized into a hierarchy; types are themselves slot-filler objects. A slot value is either a slot-filler object or some other Lisp object (such as a character string or a procedure). Several pairs with the same slot name are permitted.

<!-- page: 190 -->

Basic operations which can be performed on slot-filler object include: create, add-name, get-names, set-type, get-type, add-slot-value, remove-slot-value, getslot-values, copy, and so on. A textual representation for slot-filler objects enables printing to.and reading from a terminal, window, file, string, or Lisp list. For example: (PTRANS

actor John1 from

(RESIDENCE

obj John1)

to Storel obj John1

Mary1)

The slot-filler object represented above is of type PTRANS and consists of five pairs. One pair of the object consists of the slot name actor and slot value John1. This slot value refers to another slot-filler object whose name is John1. Whenever a slot value is a slot-filler object having a nonautomatically generated name, only the name is printed. The slot value of the pair whose slot name is from is another slot-filler object which does not have a name. In this case, the full textual representation of this slot-filler object is recursively printed. There are two pairs of the top-level slot-filler object whose slot name is obj. The slot value of one pair is John1 while the slot value of the other is Mary1.

### 7.2.2 Unification

Unification (see, for example, Charniak, Riesbeck, & McDermott, 1980) is a pattern-matching operation performed on two slot-filler objects. The objects may contain a special kind of slot-filler object called a variable. Two slot-filler objects unify if values for variables can be found such that substituting the values for those variables in the objects would produce equivalent structures.! For example, if unification is invoked on (PTRANS

actor?Person to?Location)

and (PTRANS

actor John1 to Storel)

the result is the following list of variable bindings:?Person?Location

=

Johnl =

Storel

1 Actually, the structures resulting from substitution do not have to be equivalent. Rather, one structure must be a substructure of the other: one slot-filler object unifies with another if each pair in the first object unifies with a unique pair in the second object; however, each pair in the second object need not have been accounted for. Thus unification in GATE is asymmetrical.

<!-- page: 191 -->

7.2,

THE GATE REPRESENTATION LANGUAGE

Al

Unification takes two objects and an initial list of bindings, normally empty, and returns an augmented list of bindings if the two objects unify. Three cases involving variables in unification must be considered: First, an unbound variable unifies with a slot-filler object if the object is an instance of the type of the variable. A variable of type TYPE may be represented as either of the following:?name: TYPE?Type...

When an unbound variable successfully unifies with an object, that object becomes that variable’s value in the list of bindings. Second, a bound variable unifies with an object if the value of that variable unifies with the object. Third, two variables unify if the type of one variable is an improper supertype of the type of the other. GATE provides an extended syntax and semantics for unification. In particular, it supports several special slot-filler objects of type UAND, UOR, UNOT, and UPROC: (UAND

obj obj1 obj 0bj2...)

A UAND object unifies with another object if all of 0b71 0b72... unify with that object. Bindings are augmented in a cumulative manner from each unification. (UOR obj obj1 obj 0bj2...)

A UOR object unifies with another object if any of 0b71 0bj2... unifies with that object. Bindings are augmented by the first successful unification. (UNOT

obj 0bj)

A UNOT object unifies with another object if obj does not unify with that object. Bindings are not augmented. (UPROC

proc proc)

A UPROC object unifies with another object if proc (a Lisp lambda expression) applied to that object returns a non-NIL value. Bindings are not augmented. These features were inspired by the pattern matcher of the DIRECTOR

language (Kahn, 1978). We have extended the constructs to full unification—

that is, the constructs may now be used in both arguments to the matcher, with a, well-defined semantics. We have also added appropriate extensions for typed variables and slot-filler objects, multiple slot values per slot name, and cyclic

data structures. E. T. Mueller (1987a) discusses the GATE unification language in greater detail.

<!-- page: 192 -->

Instantiation

Instantiation (see, for example, Charniak, Riesbeck, & McDermott, 1980) takes a slot-filler object and binding list, and returns a copy of the object in which any variables have been replaced by their values in the binding list. For example, if the slot-filler object: (PTRANS

actor?Person to?Location)

is instantiated using the binding list:?Person

?Location

=

Johnl

=

Storel

the returned slot-filler object is: (PTRANS

actor John1 to Store1)

Any unbound variables remain as variables in the copy. The slot-filler object may contain other slot-filler objects-—the complete structure with the given object as root is copied, with any cycles preserved in the copy.

### 7.2.4 Variabilization

Variabilization (see, for example, Charniak & McDermott, 1985) takes a slotfiller object, a predicate, and a procedure, and returns a complete copy of the

slot-filler object (with cycles preserved) in which any enclosed objects answering true to the predicate have been replaced by unique variables of a type determined by applying the procedure to the object. Multiple occurrences of the same object will become the same variable. For example, given: (PTRANS

actor John1 to Storel obj John1

Mary1)

and an appropriate predicate and procedure, variabilization returns: (PTRANS

actor?Person1 to?Location1 obj?Person1?Person2)

### 7.2.5 Contexts

A context is a collection of slot-filler objects—called facts—which specify the state of a possible world. GATE contexts are similar to other context mechanisms such as OMEGA viewpoints (Barber, 1983) and AP3 contexts (Goldman, 1982), all of which derive from the original contexts of QA4 (Rulifson, Derksen, & Waldinger, 1972).

<!-- page: 193 -->

Several operations may be performed on contexts: The create operation returns a new context containing no facts. The assert operation adds a fact to a context. The retract operation removes a fact from a context. The retrieve operation returns: (a) a list of facts in the context unifying with a given retrieval pattern,” and (b) the corresponding variable bindings. The sprout operation creates a new context which is a copy of an existing context. Initially, the new context consists of the same collection of facts as the old context. Thereafter, if the new context is modified, the old context remains unaffected.2 The new context is called a child of the old context; the old context is called the parent of the new context; we also speak of ancestor and descendant contexts. For example, suppose that the following two objects are asserted into a newly created context: (PTRANS

actor John1 to Store1)

(PTRANS

actor Mary1 to Storel)

A retrieval from this context using the pattern: (PTRANS

actor

?Person

to Store1)

will return the first two slot-filler objects, and the following two binding lists which correspond to those objects:?Person

=

John1l

?Person

=

Maryl

and

## 7.3 Planning and Inference Rules

DAYDREAMER planning and inference rules are represented in the GATE language as follows: (RULE subgoa! subgoal-pattern goal goal-pattern delete delete-pattern1 delete-pattern2... emotion emotion1 emotion2... is?INFERENCE-ONLY

| ’?PLAN-ONLY

|?>ACTION-PLAN

|?GLOBAL-INFERENCE

plausibility number) 2Hashing on the types of facts is employed in the current version of GATE to improve the efficiency of this operation. 3We do not specify whether modifications to the old context affect the new context, since the procedures which we will present here do not modify the old context once a new context has been created.

<!-- page: 194 -->

The delete, emotion, is, and plausibility slots are optional. By default, a rule may be employed both as a planning rule and as an inference rule. However, if the is slot of the rule is?Inference-only, the rule may be employed only as an inference; if the is slot of the rule is ’Plan-only, the rule may be employed only as a plan.

### 7.3.1 Planning Rules

When a rule functions as a planning rule, the goal slot specifies the antecedent and the subgoal slot specifies the consequent. That is, if the objective of an active goal unifies with goal-pattern, the active goal may be split into the subgoals whose objectives are specified by subgoal-pattern, instantiated with the bindings resulting from the unification with goal-pattern. Specifically, if subgoal-patiern is of the form: (RSEQ obj subgoal-pattern1 subgoal-pattern2...)

then subgoal-pattern1, subgoal-pattern2, and so on, suitably instantiated, are the subgoals. Otherwise, subgoal-pattern, suitably instantiated, is the subgoal. Here is a sample planning rule employed in DAYDREAMER: Rule Lovers-plan

(RULE subgoal (RSEQ obj (ACQUAINTED

actor?Self?O ther)

(ROMANTIC-INTEREST (BELIEVE

obj?0ther)

actor?Other

obj (GOAL obj (LOVERS

actor?Self?0 ther)))

(M-DATE actor?Self?O ther) (M-AGREE actor?Self?O ther

obj (LOVERS goal (LOVERS

actor?Self?Other)))

actor?Self?0 ther)

is?’PLAN-ONLY

plausibility 0.95)

THEN

### 7.3.2 ACTIVE-GOAL ACTIVE-GOAL

ACTIVE-GOAL and ACTIVE-GOAL of LOVERS with ACTIVE-GOAL and ACTIVE-GOAL to LOVERS

Inference

for LOVERS with person for ACQUAINTED with person and for ROMANTIC-INTEREST in person

for person to have ACTIVE-GOAL self and for M-DATE with self and person for self and person to M-AGREE

Rules

When a rule functions as an inference rule, the subgoal slot specifies the antecedent and the goal slot specifies the consequent. That is, whenever subgoalpattern is shown to be true in a given context for some collection of bindings, the

<!-- page: 195 -->

instantiated goal-pattern is asserted in that context and the instantiated deletepattern1, delete-pattern2, and so on, if any, are retracted from the context. If subgoal-pattern is of the form: (RAND

0bj subgoal-pattern1 subgoal-pattern2...)

or

(RSEQ obj subgoal-pattern1 subgoal-pattern?2...)

then all of subgoal-pattern1, subgoal-pattern2, and so on, must be shown to be true in the given context. If subgoal-pattern is of the form: (ROR obj subgoal-pattern1 subgoal-pattern2...)

then one of subgoal-pattern1, subgoal-pattern2, and so on, must be shown to be true in the given context. Finally, if subgoal-pattern is of the form: (RNOT

obj subgoal-pattern1)

then subgoal-pattern1 must not be shown to be true in the given context. RAND, ROR, and RNOT are to be distinguished from their counterparts UAND, UOR, and UNOT. A RAND, for example, is shown to be true if each of its elements matches some, possible different, fact in the context; a UAND in the same position would require each of its elements to match the same fact in the context. Here is a sample inference rule employed in DAYDREAMER: Rule Neg-attitude-inf

(RULE subgoal (RAND

obj (RICH actor?0ther) (AT actor?Other obj?Location)

(AT actor?Self obj?Location)

(MTRANS

actor?Self from?Self to?Other

obj?Anything:NOTYPE)

(RNOT obj (WELL-DRESSED goal (BELIEVE

actor

actor?Self)))

?Other

obj (NEG-ATTITUDE

obj?Seif))

is? INFERENCE-ONLY

plausibility 0.9) person is RICH self is AT

and

same location as person and

self not WELL-DRESSED person has NEG-ATTITUDE

### 7.3.3 toward self Rules for Actions

Inference rules are commonly used to specify the effects of actions. For example, DAYDREAMER employs the following inference rule for PTRANS:

<!-- page: 196 -->

Rule At-plan

(RULE subgoal (PTRANS

actor?Person from to

?Location1

?Location2

obj?Person)

goal (AT actor?Person

obj?Location2) delete (AT actor

?Person

obj?Location1) initial (AT

actor

?Person

obj?Location1)

plausibility 1.0) IF THEN

IF THEN

person PTRANS from location1 to location2 person AT location2 and delete person AT location1

ACTIVE-GOALE ACTIVE-GOAL

|

for person to be AT location for person to PTRANS to location

This rule specifies that when a person performs a PTRANS, it is necessary to retract the old location (AT) of the person and assert the new one. The above rule may also function in reverse as a planning rule: When an AT goal is present, a PTRANS subgoal may be asserted. Such action subgoals are treated similarly to other subgoals—the preconditions of the action become further subgoals to be achieved. For example, DAYDREAMER employs the following rule for PTRANS: Rule Ptrans-plan

(RULE subgoal (KNOW

actor?Person obj

goal (PTRANS

?Location2)

actor?Person from

?Location1

to?Location2 obj?Free-obj)

is ’ACTION-PLAN

plausibility 1.0) IF THEN

ACTIVE-GOAL ACTIVE-GOAL

for person to PTRANS to location for person to KNOW location

Once the KNOW subgoal succeeds—that is, once the person knows the location being PTRANSed to—the action is performed (asserted as an action into the appropriate context). Since it is an undesirable behavior for an action to be performed whenever its preconditions happen to be satisfied, rules whose goal is an action are not run as forward inferences. Some rule must correspond to each possible action goal. If there are no preconditions for a given action goal, the corresponding subgoal should be:

(RTRUE) Only one rule should apply to each action goal and each rule should contain all the necessary preconditions for any action goal to which that rule applies.

<!-- page: 197 -->

## 7.3 PLANNING AND INFERENCE RULES

### 7.3.4 Rules for Concern

177

Initiation

A new concern is initiated by an inference rule (called a theme, after Schank &

Abelson, 1977) of the following form: (RULE subgoal activation-condition goal (ACTIVE-GOAL obj goal-objective) emotion (POS-EMOTION strength emotion-strength)

is *INFERENCE-ONLY) When activation-condition is shown in the context, a new concern is initiated whose top-level goal is specified by the goal slot. The emotion slot specifies motivating emotions to be instantiated and associated with the new concern. Here is a sample rule employed in DAYDREAMER for initiating an EMPLOYMENT personal goal concern: Rule Em ployment-theme

(RULE

subgoal

(RNOT

obj (EMPLOYMENT

goal (ACTIVE-GOAL

actor

?7Self))

obj (EMPLOYMENT

actor 7Self 70 ther organization

emotion (POS-EMOTION is 7 INFERENCE-ONLY

strength

7Organization))

0.85)

plausibility 1.0)

self not have EMPLOYMENT with anyone and satisfaction level of MONEY or POSSESSIONS need below threshold ACTIVE-GOAL for EMPLOYMENT with person

Here is a sample rule employed in DAYDREAMER NALIZATION daydreaming goal concern:

for initiating

a RATIO-

Rule Rationalization-theme

(RULE

subgoal (DEPENDENCY

linked-from linked-to

goal (ACTIVE-GOAL emotion

?Failed-goal

(UAND

obj?Neg-emotion

(UPROC proc ’>THRESH7))) obj (RATIONALIZATION 0b)?Failed-goal))

7Neg-emotion

(NEG-EMOTION

strength

0.05)

is ’ INFERENCE-ONLY plausibility 1.0)

NEG-EMOTION of sufficient strength resulting from a FAILED-GOAL ACTIVE-GOAL for RATIONALIZATION of failure

<!-- page: 198 -->

Emotion-driven control

Planner

Rule application

Inference rule application

Concern termination

Planning rule application

Concern initiation

Figure 7.1: Basic Procedure Dependencies

## 7.4 Basic Procedures for Daydreaming

DAYDREAMER, in its basic form, is built out of seven procedures, whose dependencies are shown in Figure 7.1. The top-level emotion-driven control procedure repeatedly selects the most highly motivated concern, and invokes the planner on this concern. The planner invokes rule application to carry out one step of planning for the concern. If the top-level goal of the concern succeeds or fails, the planner invokes concern termination. Rule application invokes inference rule application and planning rule application. Inference rule application invokes concern initiation if a new top-level goal is inferred.

### 7.4.1 Emotion-Driven Control

The emotion-driven control procedure is as follows:

1. To get off the ground, invoke inference rule application.

2. Select concern with highest emotional motivation.

3. Invoke planner on that concern.

4. Go to step 2.

In performance mode, only personal goal concerns may be selected, while in daydreaming mode, both personal goal concerns and daydreaming goal concerns may be selected. The program switches modes if no concerus are eligible in the current mode. Optional physical objects are accepted as input upon switching modes from daydreaming to performance. If input is provided, the switch is not carried out. On each cycle of the control loop, the strengths of needs and nonmotivating emotions are subject to decay. Emotions which decay below a certain threshold are removed.

<!-- page: 199 -->

Planner

A planning rule specifies how to split a goal into subgoals: The antecedent of the rule specifies the goal and the consequent specifies the subgoals. There may be several planning rules whose antecedent matches a given goal. That is, for any particular goal, there may be more than one way of decomposing that goal into subgoals. Each such method specifies an alternative continuation of the current hypothetical world state. DAYDREAMER keeps track of alternative worlds using GATE contexts— snapshots of a hypothetical or real world at some moment in time. When several planning rules apply to a given subgoal, each alternative set of subgoals may be activated in a separate context. This enables DAYDREAMER to follow one line of planning, and then, should that line of planning prove unsatisfactory, back up and attempt an alternative plan already activated in its own isolated context. This is similar to the notion of nested transactions (Reed, 1978; Moss, 1981; E. T. Mueller, J. D. Moore, & Popek, 1983) in distributed operating systems and recovery blocks (Randell, 1975) in programming languages: If one means for accomplishing a subtask is unsuccessful, any changes already performed are undone and an alternative method may be invoked. A planning rule is applied to a given active goal in a given context as follows: A new context is sprouted from the given context, and the subgoals specified by the consequent are asserted as new active subgoals in the sprouted context. Thus each alternative set of subgoals is maintained in a separate context. Subgoals in each such context are in turn split via planning rules into further subgoals, sprouting further contexts. In general, a tree of contexts results as shown in Figure 7.2. Eventually, subgoals are reached whose objectives are already true (retrieved in the context): a subgoal succeeds in a context when its objective is retrieved in the context or when all of its subgoals have succeeded. The objectives of some subgoals are actions; when such a subgoal succeeds, the action is performed. A concern terminates successfully when its top-level goal succeeds in a context. If all possibilities are exhausted without success, the concern terminates unsuccessfully and the top-level goal fails. The planner conducts a depth-first search (Nilsson, 1980) through the space of contexts. That is, when several contexts are sprouted from the current context (corresponding to several alternative planning rule applications), one of those contexts is selected as the new current context. Contexts are in turn sprouted from this new current context. One of those contexts is again selected, and this process continues. If a dead end is reached—because no planning rules are applicable to the remaining subgoals—backtracking is performed. It should be noted that the creative problem-solving aspect of DAYDREAMER arises, not from the depth-first search algorithm, but from the analogical planning, mutation, and serendipity mechanisms which are superimposed upon this basic algorithm. Specifically, the planner procedure for a given concern is as follows:

1. Invoke rule application in the current context of concern.

<!-- page: 200 -->

CX.3

CX.2

AY RULE15

firme

CURRENT RULE15 RULE62

RULE15 RULE44

Figure 7.2: Tree of Planning Contexts

2. Mark current context as applied.

3. If top-level goal succeeded, invoke concern termination with “success.” 4, Otherwise, if no contexts were sprouted:

(a) (Attempt to backtrack) Until there are unapplied sprouted contexts of current context or until current context is equal to the root context of the concern,‘ set current context to the parent of the current context.

(b) (If backtrack unsuccessful, terminate concern) If current context is root, invoke concern termination with “failure.”

(c) (Else if backtrack successful, set current context) Otherwise, set current context to an unapplied sprouted context of current context.®

5. Otherwise, set current context to one of the sprouted contexts.® Traces of the execution of the planning algorithm are given in Appendix A. DAYDREAMER explores hypothetical past, present, and future worlds. However, the program must also have a notion of the “real” world—both past and present. At any time, one context is designated as the reality context. This context contains the current state of the world. 4The root context of a concern is the real or imagined world state in which that concern was first initiated. 5The “best” context is chosen according to various heuristics: For nonanalogical planning, the plausibility of the planning rule which resulted in the context is used. For analogical planning, the metrics of episode similarity, desirability, and realism (see Chapter 4) are used. S Again, the “best” context is chosen according to various heuristics.

<!-- page: 201 -->

Hewitt (1975) has pointed out that using contexts it is difficult to reason about situations in multiple contexts. He gives the example of two assertions, Neil Armstrong is standing on the earth and Neil Armstrong is standing on the moon, which are asserted in different contexts. The assertion, however, that Neil Armstrong’s weight in the first contert is greater than his weight in the second context cannot be made in either of these contexts alone. Instead of a context mechanism, Hewitt proposes the explicit use of situational tags (McCarthy & Hayes, 1969) within assertions (no longer confined to any particular context). In DAYDREAMER, references to contexts may be incorporated into facts if desired; however, since all facts must be asserted into some context, a global context may have to be created for this purpose.

Even so, as Sacerdoti (1977) points out, it is difficult to represent and use knowledge about planning in a context scheme. For example, critics (Sacerdoti,

1977) and meta-goals (Wilensky, 1983) are not easily implemented in such a scheme. Since DAYDREAMER is not concerned with daydreaming or learning about the planning process itself (although a complete model of daydreaming would certainly have to account for such metacognitions [Flavell, 1979]), metaplanning strategies are hard-coded in DAYDREAMER. Contexts are employed in DAYDREAMER for two reasons: to enable backtracking to a previous choice point in daydreaming, and to maintain a trace of each explored alternative. A stack-based undo mechanism (such as that employed in Prolog [Clocksin & Mellish, 1981]) enables backtracking, but does not enable retention of alternatives once abandoned. It is important in DAYDREAMER to retain alternative traces because each may be indexed in episodic memory for possible future use; furthermore, the order of backtracking need not conform to a stack discipline. It would not be particularly convenient to implement DAYDREAMER in Prolog (Clocksin & Mellish, 1981) because of the specialized control structure which daydreaming requires. The control structure of Prolog—planning rule application (backward chaining) to achieve a single goal—does not facilitate implementation of multiple active goals, interleaved application of inference rules (forward chaining), and the storage and later application of planning episodes.

### 7.4.3 Concern Termination

The concern termination procedure given a termination status is as follows:’

1. Destroy otherwise unconnected emotions associated with concern.

2. Create a new positive or negative emotion, whose strength is equal to the dynamic importance of the top-level goal associated with the concern.® 7Recall that concerns are initiated by inference rules. The procedure for concern initiation will be presented once we have presented the procedure for inference rule application. 8Response emotions are only created here if the top-level goal is a personal goal or the

REVENGE

daydreaming goal.

<!-- page: 202 -->

3. If termination status is “success,” assert objective of top-level goal in the reality context.

4. If termination status is “failure,” failed.

change top-level goal from active to

5. Destroy concern.? Before the emotion-driven control procedure is first invoked, the program must first be initialized. This consists of:

- initializing various global variables

- creating an initial reality context and loading initial facts into this context

- invoking the inference rule application procedure in the initial reality context, to start up the very first concerns of the program

### 7.4.4 Rule Application

The rule application procedure in a given context is as follows:

1. For each active subgoal in context whose subgoals have all succeeded or

whose objective is retrieved in the context:!° 14

(a) Change subgoal from active to succeeded.!? (b) If objective was not retrieved in context, assert objective in context.

(c) (Detect possible fortuitous subgoal success) If the concern of subgoal is not the current concern, create a surprise emotion and associate it with the concern of the subgoal.

2. If any objective was asserted in Step 1, invoke inference rule application in context.

3. If any subgoals succeeded in Step 1, go to Step 1. ® Any planning structure associated with the concern in the reality context is also garbage collected.

10Tf the subgoal contains variables, (a) a context must be sprouted for each set of bindings resulting from retrieval, (b) the body of this loop must be performed for each such sprouted context, and (c) the body of the loop must also instantiate all the subgoals of the current concern with the bindings, so that any instances of a given variable anywhere in the plan will take on the value specified in the bindings. 11It is also necessary to handle protection violations: If a succeeded subgoal is no longer retrieved in the context, it must be replanned. Script-like plans (for M-DATE, MRESTAURANT, and so on) are not subject to this check. 121f subgoal happens to be an instance of a personal goal, an emotional response must be generated and associated with the current concern. The rule for emotional responses is discussed in Chapter 3.

<!-- page: 203 -->

4. Invoke planning rule application in context.

While planning for one concern, a subgoal of another concern will sometimes succeed. This is the case of a fortuitous subgoal success. In such a situation, a new surprise emotion is created and added as an additional force of motivation to the other concern. If, as a result, the other concern has a higher motivation than the current concern, processing will switch to that other concern on the next cycle. The objectives of goals may be states or actions. If the objective of a goal is an action, a planning rule for that goal specifies preconditions for that action as subgoals. Once all the subgoals have succeeded, the action is performed by asserting it as an action into the context in Step 1b above.!% In performance mode, when the actor of an action goal is another person and all precondition subgoals of that goal have succeeded, the program stops and waits for input. The parsed input is compared (through unification) to the expected action. If the input action is not the same as the action goal objective, the goal fails (although some other subgoal or concern may still succeed as an indirect result, through subgoal success detection and serendipity). For example, in LOVERS1, when DAYDREAMER waits for input which is expected to be Harrison Ford agreeing to have dinner together, and this input is not received, this subgoal fails, in turn causing failure of the top-level goal. In addition, after an action of the daydreamer is performed in performance mode, the program stops and waits for optional English input of an action or state in the external world. This input concept is then asserted into the context. The applicability of the concept to the current top-level goals and subgoals of the program, if any, is determined through (a) serendipity, to be described, and

(b) detection of fortuitous subgoal success in the above procedure.

### 7.4.5 Planning Rule Application

The planning rule application procedure in a given context is as follows:

1. For each active subgoal in context whose concern is the current concern and having no subgoals;'4 for each planning rule whose antecedent unifies 13However, actions may be performed only in performance mode. If an action is about to be performed and the program is in daydreaming mode, the concern is placed into a watting state. Only concerns in the runable state are eligible for selection in the emotion-driven control procedure. When the program switches from daydreaming mode to performance mode, all waiting concerns are changed to runable concerns. Furthermore, actions with variables in them may never be performed. If such an action is about to performed, the concern is placed into a halted state. A halted concern is only made runable when a serendipity or fortuitous j subgoal success occurs for that concern. 14S ubgoals of a given goal are expanded in a left-to-right sequence, rather than in all possible orders. For example, if a given goal has two subgoals, the second subgoal is not selected for

expansion until the first subgoal has succeeded.

<!-- page: 204 -->

with the objective of the active subgoal:'° 1° (a) Sprout a new context from context. (b) For each subgoal S in the consequent of planning rule, create a new subgoal of the active subgoal in the sprouted context whose objective is the instantiation of S with the bindings from the above

unification.1”

### 7.4.6 Inference Rule Application

Unlike planning rules, when the antecedent pattern of an inference rule is retrieved in a context, a fact specified by the consequent pattern is asserted in the same context. Thus whereas planning rules sprout contexts, inference rules do not (since inference rules specify inevitable results rather than alternative possibilities). Inferences rules also have deletion consequents (specified by the delete slot) which specify facts to be retracted from the context. In English descriptions of rules, any consequent preceded by “retract” is a deletion consequent.

The inference rule application procedure in a given context (which also may initiate new concerns), is as follows:

1. For each inference rule R whose antecedents are all retrieved in context;18

for each set of retrieval bindings:19 7° (a) Instantiate each consequent and deletion consequent of R with the above bindings.

(b) If the (single) instantiated consequent of R specifies a new top-level goal T, and (a) T is a daydreaming goal or (b) the current concern is a personal goal concern, invoke concern initiation on T, R, and the

above bindings.?1 15These two loops are nested. 16Finding planning rules whose antecedent unifies with a subgoal is optimized through use of the rule connection graph: only planning rules connected to the planning rule from which the subgoal was derived are considered. The rule connection graph is created when rules are loaded during program initialization (and updated if new rules are added). 17Instead of specifying subgoals, the consequent may specify code to be executed. It is the responsibility of this code to create appropriate subgoals. This feature is used in planning for daydreaming goals such as RATIONALIZATION and REVERSAL. 18This is optimized using the rule connection graph: An inference rule is only considered if it relates to a “touched” fact in the context (i.e., a fact asserted or modified since the last application of inference rules). 19These two loops are nested.

20Multiple applications of a given inference rule to the same antecedent facts with the same set of bindings must be inhibited.

1 Concern initiation is not invoked if an identical top-level goal already exists.

<!-- page: 205 -->

(c) Otherwise, assert each instantiated consequent in the context,2? and retract each instantiated deletion consequent from the context.?3

2. If an assert or retract was made in Step 1, go to Step 1.74 The action taken in the above procedure when a new top-level goal is activated depends on whether the current concern is a personal goal concern or a daydreaming goal concern: When the current concern is a personal goal concern, any new top-level goals—both personal and daydreaming goals—result in the creation of a new concern. When the current concern is a daydreaming goal concern, only daydreaming goals result in the creation of a new concern. Personal goals activated in a daydreaming goal concern do not create new concerns, but rather are performed under the existing current concern. Why should some top-level goals be performed under a separate concern, and others not? Each concern is intended to correspond to a real top-level goal of the system. Top-level goals which are activated in daydreaming goal concerns are not real top-level goals. Rather, they are top-level goals which would be activated in a particular hypothetical situation. If such top-level goals should motivate activity, such as planning to avoid an anticipated goal failure, then a daydreaming goal to fulfill this function, rather than a personal goal, is activated. Daydreaming goals activated in a daydreaming goal concern do create new concerns. Although a personal goal concern modifies the reality context through planning, the only modifications which a daydreaming goal concern can perform to

the reality context are: (a) assertion of new top-level goals, and (b) assertion of

the eventual outcome—success or failure—of the daydreaming goal.”° Planning for daydreaming goal concerns takes place in imaginary contexts which are sprouted off of past or current reality contexts. Any imagined changes to the world state do not affect the reality context but rather are performed in their own isolated contexts. Personal goal concerns are carried out in the exter-

nal world (e.g., LOVERS1

and FOOD1) and thus repeatedly sprout a context

from the old realityindexcontexts;reality context which becomes the new reality context. As shown in Figure 7.3, personal goal concerns may be thought of as 221f the instantiated consequent can already be retrieved in the context, (a) the strength of the retrieved fact is incremented by the strength of the new instantiated consequent, and (b) no assertion is performed. 231f an instantiated consequent is a personal goal outcome, an emotional response must

be generated and associated with the current concern. The rule for emotional responses is discussed in Chapter 3. 24Qne may insert at this point a limit on the number of times inferences are applied. There is no need for this in the current version of DAYDREAMER since the particular set of rules does not result in infinite inferencing loops. Johnson-Laird (1983, pp. 34-39) discusses how humans might limit inferencing. 25Daydreaming goal concerns have other side effects, including: (a) modification of emotional state, and (b) storage of generated planning sequences in episodic memory for future use.

<!-- page: 206 -->

personal goal concerns

LOVERS 1—£-

CIXT! ———» CTXT2 ——_» CIXTS ——» {

REVERSAL1

CIXT3

—_

reality

CIXTI0

CIXT6 ——» 4

REVENGE1

CIXT7

¢

daydreaming goal concerns

CIXT4

CIXT8

FOOD1 —g>

CIXT9

Figure 7.3: Personal and Daydreaming Goal Contexts proceeding horizontally to the right with reality being the rightmost context, and daydreaming goal concerns (e.g., REVERSAL1 and REVENGE1) may be thought of as proceeding vertically, sprouting off of present or past reality contexts.

### 7.4.7 Concern Initiation

The concern initiation procedure for a given top-level goal, inference rule, and bindings is as follows:

1. Create a new concern.

2. For each emotion F specified by inference rule: (a) (Daydreaming goal concern activated and motivated by existing emotion.) If E is an existing emotion, associate # with the concern.

(b) (Concern motivated by new emotion.) Otherwise, create a new emotion by instantiating F with bindings and associate the new emotion

with the concern.?°

3. If top-level goal is a personal goal, set the current context of the concern to the reality context and assert the top-level goal in this context.

4. Otherwise, set the current context of the concern to a new context sprouted from the reality context and assert the top-level goal in this new sprouted context.

## 7.5 Episodic Memory and Analogical Planning

In this section, we revise and augment the above procedures for analogical planning. Figure 7.4 shows the revised procedure dependencies. 261f the top-level goal is a personal goal, the strength of E specifies its intrinsic importance. If the top-level goal is a daydreaming goal, E specifies an additional motivating emotion.

<!-- page: 207 -->

Emotion-driven control

Planner Rule application

Inference rule application

Concern initiation

Concern termination

Planning rule application

Episode retrieval

Reminding

Episode storage

Subgoal creation Analogical rule application

Figure 7.4: Revised Procedure Dependencies

### 7.5.1 Episode Storage

The episode storage procedure given an episode (a planning tree) and an index (either a concept not containing variables or a planning rule) is as follows:

1. (Intern the index) Set I to the index, if any, in a global list of unique indices which is equal to, or unifies with, the given index. If there is no such index, add the given index to the global list of unique indices and set I to the given index.

2. Create a link from J to episode. Episode storage is invoked in two situations:

- Top-level goal success: The planning tree for the top-level goal and the subtrees for each of its descendant subgoals are stored as episodes. Each goal is indexed under the planning rule which was used to break that goal down into subgoals. The top-level goal is also stored under additional indices (for persons, physical objects, locations, organizations, and the like) which are derived automatically from the planning context. The deszrabil-

ity and realism values (see Chapter 4) are also evaluated and stored along with the episodes.

- Program initialization: Hand-coded episodes are stored. The programmer may specify various indices in addition to the planning rules corresponding to each subgoal.

### 7.5.2 Episode Retrieval

The episode retrieval procedure given a list of indices is as follows:

1. For each index J in indices:

<!-- page: 208 -->

(a) (Intern the index) Set I to the index, if any, in the global list of unique indices which is equal to, or unifies with, J.

(b) If there is such an index, for each episode F linked from J: i. Increment the number of marks associated with E. ii. Add E to a list of marked episodes. iii. If the number of marks associated with £ is equal to the number

of marks required for retrieval of that episode,?’ add E to a list of retrieved episodes.

2. For each episode FE in the list of marked episodes, reset the number of marks associated with E to zero.

3. Return the list of retrieved episodes.

### 7.5.3 Reminding

A global list of recent indices is maintained.

This is a short list (currently of

length at most 6); addition of a new index (when the list is full) causes the oldest index to be dropped. Whenever new indices are added to the global list of recent indices, the reminding procedure is invoked for each episode retrieved using that list. The current overall emotional state (a positive or negative emotion) is included in the global list of recent indices. The reminding procedure for a given episode is as follows:

1. If episode is already a member of a global list of recent episodes (currently of length at most 4), exit this procedure.

2. Add episode to the global list of recent episodes; drop the oldest episode

from the list (if the list is full).

3. Reactivate emotions associated with episode.

4. For each index I of episode, if J is not already contained in the global list of recent indices, add J to that list and invoke episode retrieval on J and reminding procedure recursively on each retrieved episode.

5. (Unless this procedure was itself invoked as a result of an object-driven serendipity), invoke the serendipity recognition procedure for all concerns and episode.

Step 4 above is what enables the generation of associative streams of remindings: when an episode is retrieved by one index, its other indices are activated; these other indices potentially result in the retrieval of other episodes, whose other indices are in turn activated, retrieving further episodes; and so on. 27T wo different retrieval thresholds are employed: the number required for retrieval during analogical planning (generally one index, the rule, is sufficient), and the number required in other situations (generally set as some percentage of the total number of indices). These thresholds may be set by the programmer for hand-coded episodes.

<!-- page: 209 -->

Revised Planning Rule Application

The planning rule application procedure, described above, is revised for analogical planning. This procedure, given a context, is now as follows:

1. For each active subgoal S in context whose concern is the current concern and having no subgoals:

(a) (Continue with existing analogical plan if still applicable in the target domain) If an analogical episode E is associated with S and the objective of S unifies with the antecedent of the planning rule R associated with E, invoke the analogical planning procedure on context, S, E, R, and the bindings from the unification.

(b) Otherwise, for each planning rule R whose antecedent unifies with the objective of 9:8 i. Set F to the value returned by invoking the episode retrieval procedure on a list containing R. ii. (If analogical episodes retrieved, begin new analogical plans) If F is not empty, for each episode F in F’,, (a) invoke the reminding procedure on E, (b) invoke the analogical rule application procedure on context, S, E, R, and the bindings from the unification.

iii. (Else, perform regular planning) Otherwise, invoke the subgoal creation procedure for context, S, NIL, R, and the bindings from the unification.

### 7.5.5 Analogical Rule Application

The analogical rule application procedure for a given context, subgoal, episode, planning rule, and bindings is as follows:

1. (Use episode to instantiate unbound variables in current plan) Augment bindings with bindings resulting from the unification of (a) the antecedent and consequents of planning rule with (b) the concrete goal and subgoals

of episode.?9

2. Invoke the subgoal creation procedure on context, subgoal, episode, planning rule, and bindings.

### 7.5.6 Subgoal Creation

The subgoal creation procedure for a given context, subgoal, episode, planning rule, and bindings is as follows: 28 Episodic planning rules are not eligible here. They may only be applied when they are contained within analogical episodes (for example, as a result of serendipity). 29Whenever a variable in the given subgoal is provided with a value, instances of that variable in other subgoals of the current plan must take on the same value.

<!-- page: 210 -->

1. Set C to a new context sprouted from context.

2. (Break subgoal into further subgoals) For each subgoal S in the consequent of planning rule: (a) Create a new subgoal of subgoal in C whose objective is the instantiation of S with bindings.

(b) (Carry along episode for analogical planning of subgoal) Associate the appropriate subepisode (if any) of episode (if not NIL) with the new subgoal.

## 7.6 Other Planning

This section describes the modifications to the above procedures necessary to implement forward other planning and backward other planning.

### 7.6.1 Forward Other Planning

Mental states or beliefs of the daydreamer and of others coexist in each context. Every fact in a given context is assumed to be a belief of the daydreamer. Some of these beliefs are beliefs about the beliefs of others. While a belief of the daydreamer that it is raining in a certain location is represented as: (RAINING

obj Location1)

the belief that Other believes that it is raining in that location is represented as: (BELIEVE

actor Other obj (RAINING

obj Location1))

“Objective” beliefs of others cannot be represented in this scheme. All facts are assumed to be beliefs of the daydreamer; thus one may only represent beliefs of the daydreamer about the beliefs of others. A belief of a belief of the daydreamer (who is represented as the slot-filler object Me), such as: (BELIEVE

actor Me obj (RAINING

obj Location1))

is not currently permitted. Such a structure would be required to model metacognition (Flavell, 1979), which is beyond the scope of the present work. The planning procedures described in the previous sections are modified to make use of a list called the belief path. This list determines the planning viewpoint. Normally, the belief path is set to (Me). However, in order to simulate the behavior of Harrison, the belief path would be set to (Harrison Me). This path means “Harrison as seen through my eyes.” In order to simulate

<!-- page: 211 -->

the simulation of the behavior of another person Debra by Harrison, the belief path is set to (Debra Harrison Me). This path means “Debra as seen through Harrison’s eyes as seen through my eyes.” The last element of a belief path

must always be (Me).

DAYDREAMER rules are expressed relative to the person who is planning, specified by the?Self variable. When the daydreamer is planning,?Self is bound to Me. If planning for another person is being performed,?Self is bound to that person. In general,?Self is bound to the first element of the belief path. All assertions, retractions, and retrievals in the planning procedures are performed relative to the current belief path. For example, if the belief path is (Harrison Me), then before performing an assertion, retraction, or retrieval on a given fact, it is enclosed in the following way: (BELIEVE

actor Harrison

obj Fact)

If the belief path is (Debra Harrison Me), then the fact is enclosed as follows: (BELIEVE

actor Harrison obj (BELIEVE

actor Debra

obj Fact))

The rule application procedure is revised to:

(a) perform rule application

with a belief path of (Me), and (b) perform rule application with a belief path of (Other Me) for each other person of current interest, indicated by the presence of an assertion of the form: (OTHER

actor Other)

in the current planning context. (This assertion is added, for example, when DAYDREAMER is in a LOVERS relationship with another person.)

### 7.6.2 Backward Other Planning

Instead of finding a rule whose antecedent goal unifies with: (BELIEVE

actor Other obj?Mental-state)

one is found whose antecedent goal unifies with?Mental-State. In this unification,?Self is bound to Other rather than to Me. Then the subgoals are instantiated and enclosed in a BELIEVE before being asserted: (BELIEVE

actor Other obj?Subgoal)

<!-- page: 212 -->

Emotion-driven control

Planner

Rule application

Inference rule application

Concern initiation

Concern termination

Planning rule application

Sad Nin Ene nt

Episode retrieval

(Input) —————

Serendipity recognition

Episode storage

Reminding

<@—

Subgoal creation

_— Analogical rule application

Mutation

Figure 7.5: Further Revised Procedure Dependencies

## 7.7 Serendipity

In this section, we further augment the above procedures for serendipity. ure 7.5 shows the revised procedure dependencies.

### 7.7.1 Fig- Serendipity Recognition

The serendipity recognition procedure given a concern and a concept or episode is as follows:

1. Find a top rule T whose antecedent unifies with an appropriate goal G of

the concern.°*°

2. If given an episode, find an episodic rule, the bottom rule B, contained in the episode; otherwise, find a rule B having a consequent subgoal which unifies with the given concept.

3. Perform an intersection search from T to B in rule connection graph.*! 3? 3°If the concern is a personal goal, the personal goal is used. If the concern is a learning daydreaming goal, the associated personal goal is used. If the concern is an emotional daydreaming goal, the daydreaming goal is used. 31 This search finds up to a certain number of paths (currently 3), including those with cycles, up to some maximum length (currently 8). A recursive procedure is employed which works

its way down from T and up from B simultaneously; whenever an intersection is detected, the two partial paths are appended together and added to a list of found paths. To reduce search somewhat, a given rule may occur at most twice in a path.

32 Only (a) nonepisodic planning rules, or (b) episodic planning rules contained in the given episode or the global list of recent episodes, are eligible in the search.

<!-- page: 213 -->

4, If a path is found:

(a) Verify the path by (1) unifying the objective of G with the antecedent of the first rule of the path, and then (2) propagating bindings through progressive unification from the beginning to the end of ae sees while simultaneously constructing an episode (planning

tree). (b) If verification succeeds: i. If concern is a daydreaming goal concern, set C' to a new context sprouted from the context in which G was first activated, and set the current context of concern to C. ii. Otherwise,

set C' to a new

context

sprouted from the reality

context (which becomes the new reality context).*4 iii. Add the constructed episode as a new analogical plan for G in

C. iv. Generate a surprise emotion and associate it with the concern.

The serendipity recognition procedure is invoked in several situations:

Input-state-driven serendipity: When a (state or action) concept is received as input, the serendipity recognition procedure is invoked for all concerns and the given concept. Object-driven serendipity: When a physical object is received as input, the serendipity recognition procedure is invoked for all concerns, and for all

episodes retrieved using the indices*® of: (a) the given physical object, and (b) indices in the global list of recent indices. The reminding procedure is invoked for any retrieved episode which results in a serendipity. Concern-activation-driven serendipity: When a new concern is activated, the serendipity recognition procedure is invoked on that concern for all episodes in the global list of recent episodes. Episode-driven serendipity. When the reminding procedure is invoked for an episode (unless that procedure has already been invoked from objectdriven serendipity), the serendipity recognition procedure is invoked for all concerns and that episode.

Mutation-driven serendipity: When an mutated action is generated, the serendipity recognition procedure is invoked on the current concern and 33This procedure is discussed in greater detail and an example is given in Chapter 5. 34 Any existing planning structure for G in C is also garbage-collected here. 35In this case, an episode is retrieved whenever the number of marks is one less than the number normally required: the serendipity, if any, serves to provide the remaining “index.”

<!-- page: 214 -->

the mutated action.*© If a serendipity occurs, then the mutation is deemed successful.

### 7.7.2 Action Mutation

The action mutation procedure for a given concern is as follows:

1. For each of the descendant leaf contexts C of the root context of concern; for each active goal in C' whose objective A is an action; for each mutation?” M of A:°8

(a) Invoke serendipity recognition on concern and M. Action mutation is currently invoked in DAYDREAMER achieving a given daydreaming goal have failed.

when

all plans for

3°In this case, the top rule for the intersection search is derived from the parent goal of the

action subgoal being mutated. 37The strategies for mutating an action are described in Chapter 5. 38 These three loops are nested.
