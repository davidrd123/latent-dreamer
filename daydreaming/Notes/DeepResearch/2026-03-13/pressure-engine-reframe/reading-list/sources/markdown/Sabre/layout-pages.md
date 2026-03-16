# Sabre

Layout-preserving `pdftotext -layout` extraction.

<!-- page: 1 -->
```text
                Proceedings of the Seventeenth AAAI Conference on Artificial Intelligence and Interactive Digital Entertainment
                                                              (AIIDE 2021)
     Sabre: A Narrative Planner Supporting Intention and Deep Theory of Mind
                                                  Stephen G. Ware, Cory Siler
                                                      Narrative Intelligence Lab
                                         University of Kentucky, Lexington, KY, USA 40506
                                              sgware@cs.uky.edu, cory.siler@uky.edu
                           Abstract                                        To our knowledge it is the first such system to support full
                                                                           ADL action syntax (Pednault 1987) and an arbitrarily deep
  Sabre is a narrative planner—a centralized, omniscient de-               theory of mind. We measure Sabre’s performance on bench-
  cision maker that solves a multi-agent storytelling problem.             mark problems and compare its solution space to ablated
  The planner has an author goal it must achieve, but ev-
  ery action taken by an agent must make sense according to
                                                                           versions to motivate its unique combination of features.
  that agent’s individual intentions and limited, possibly wrong
  beliefs. This paper describes the implementation of Sabre,                                        Related Work
  which supports a rich action syntax and imposes no arbitrary
  limit on the depth of theory of mind. We present a search                Kybartas and Bidarra (2016) survey many approaches to nar-
  procedure for generating plans that achieve the author goals             rative generation, including expert systems, planning, multi-
  while ensuring all agent actions are explained, and we re-               agent simulations, and case-based reasoning. Since then,
  port the system’s performance on several narrative planning              deep learning techniques have also been applied (Martin
  benchmark problems.
                                                                           et al. 2018). We focus this survey on planning systems.
                                                                              One branch of research prefers to use existing plan-
                       Introduction                                        ning algorithms to generate stories. Examples include HTN
                                                                           planning to generate plots for the television show Friends
Virtual environments with interactive stories, like games,                 (Cavazza, Charles, and Mead 2002) and fast classical plan-
training simulations, and tutoring systems, often give the                 ners to make The Merchant of Venice interactive (Porteous,
player control of one or more characters while the sys-                    Cavazza, and Charles 2010). Work by Porteous et al. (2010),
tem controls the others. These virtual characters need to                  in particular, highlights the use of PDDL 3 trajectory con-
seem like realistic agents with their own goals and limited,               straints and planning landmarks to control story pacing.
possibly wrong beliefs. Story generation techniques fall on
                                                                              Another branch integrates computational models of nar-
a spectrum from emergent to planned (Riedl and Bulitko
                                                                           rative directly into the knowledge representation and search
2013). Emergent systems generate a story from the inter-
                                                                           of the planner. Young et al. (2013) survey planning algo-
actions of many realistic agents but struggle with “herd-
                                                                           rithms that model intentionality, conflict, surprise, suspense,
ing cats” when the narrative is required to contain certain
                                                                           and other phenomena. Some focus on generating the events
content. Planning systems centralize decision making to en-
                                                                           of the story (plot), while others focus on the telling (dis-
sure they meet the author’s constraints but struggle to make
                                                                           course). Sabre integrates narrative models into the algorithm
agents seem realistic. Narrative planning is an interesting
                                                                           and generates plot, so we focus our survey here.
problem because it can be performed by a single agent but
needs to generate a solution befitting a multi-agent system.
A traditional planner ignores agent realism, while a tradi-
                                                                           Intention Riedl and Young’s IPOCL (2010) introduced in-
tional multi-agent system suffers limitations that don’t ap-
                                                                           tentional planning, which defines both author and charac-
ply in virtual worlds, where an omniscient and omnipotent
                                                                           ter goals. A valid plan accomplishes the author goal but can
author can coordinate the story behind the scenes.
                                                                           only be composed of actions that contribute to the goals of
   This paper presents Sabre, a forward-chaining state-space
                                                                           the characters who take them. Ware et al. (2014) extended
narrative planning system that models both the intentions of
                                                                           their model to allow conflict and failed plans. In CPOCL
the author (i.e. the system designer’s constraints on the so-
                                                                           (Ware and Young 2011) and Glaive (Ware and Young 2014),
lution) as well as the intentions and beliefs of each virtual
                                                                           a character action no longer needs to be part of a success-
character. Sabre finds plans that improve the author’s utility
                                                                           fully executed plan to achieve their goal; as long as such a
but are only composed of actions that can be explained by
                                                                           plan exists, the action is reasonable, even if that plan is never
the intentions and beliefs of the characters who take them.
                                                                           actually executed, perhaps because it failed or conflicted an-
Copyright c 2021, Association for the Advancement of Artificial            other agent’s plan. Sabre uses a similar model, except that it
Intelligence (www.aaai.org). All rights reserved.                          uses utility functions instead of discrete propositional goals.
```

<!-- page: 2 -->
```text
Theory of Mind Several narrative planners focus on char-                 is an agent. Tom, the merchant, the guard, and the bandit are
acter knowledge, partial observability, and wrong beliefs.               the characters in our example domain.
Virtual Storyteller (Brinke, Linssen, and Theune 2014),                     A problem defines some number of state fluents, proper-
HeadSpace (Thorne and Young 2017), and work by Chris-                    ties whose values can change over time. For some fluent f ,
tensen et al. (2020) use a 1 layer theory of mind, meaning               let Df denote the set of possible values f can take on, called
they reason about what is true, and what each character be-              the domain of f . Sabre supports two kinds of fluents: nom-
lieves is true, but not what x believes y believes, and so               inal and numeric. For nominal fluents, Df is a finite set of
on. IMPRACTical (Teutenberg and Porteous 2013) uses a                    possible nominal values. For numeric fluents, Df = R. We
1 layer model, and past that defers to a shared global set               denote numeric fluents as fR .
of popular beliefs. Shirvani et al. (2017) demonstrate that                 Sabre supports seven types of logical literals l. The first
planner-generated stories need a multi-level theory of mind              six are given by this grammar:
to model some forms of deception, cooperation, anticipa-
                                                                           l :=    f = v | f 6= v | fR > n | fR ≥ n | fR < n | fR ≤ n
tion, and surprise. Sabre places no arbitrary limit on the                 v :=    any value in Df
depth of theory of mind.                                                   n :=    v | fR | n + n | n − n | n · n | n ÷ n
   Si and Marsella’s Thespian (2014) treats theory of mind
as central, while Ryan et al.’s Talk of the Town (2015) mod-                These six kinds of literals have a fluent f on the left, a re-
els characters that observe, forget, and lie. These and others           lation (=, 6=, > etc.) in the middle, and a value on the right.
like them are multi-agent systems; they use a host of agents             For nominal literals, a value is one of Df . For numeric lit-
with true partial observability and leverage little centralized          erals, a value is a real number, a numeric fluent, or an arith-
planning to coordinate the story.                                        metic expression. Tom’s location is an example of a nominal
                                                                         fluent whose domain is the cottage, market, camp, or cross-
                                                                         roads. Tom’s wealth is an example of a numeric fluent.
Intention + Theory of Mind At least two systems model                       The seventh kind of literal takes the form believes(c, l),
intentions and theory of mind for centralized planning. Pre-             which we abbreviate b(c, l), and which means that character
viously we defined a state space with these features (Shir-              c believes literal l is true. Beliefs can be arbitrarily nested, so
vani, Ware, and Farrell 2017) but focused on validating                  b(c1 , b(c2 , f = v)) means that character c1 believes charac-
the knowledge representation and did not provide a plan-                 ter c2 believes that fluent f has value v. Sabre does not limit
ning algorithm or measuring performance. Ostari (Eger and                how deeply nested literals may be.
Martens 2017) has these features but models true uncertainty
via Dynamic Epistemic Logic. The high cost of modeling                   Logical Expressions
all doxastically accessible possible worlds limits the scope             Sabre uses three kinds of complex logical expressions: pre-
of problems it can solve. In Sabre’s model, agents can have              conditions that must be checked, effects that describe how
arbitrarily nested and wrong beliefs, but must always com-               states are modified, and utility functions that define prefer-
mit to specific beliefs. We found this tradeoff allows us to             ences over states.
tell most of the stories we want to tell, and it allows Sabre to             During pre-processing, preconditions are converted to
solve larger problems.                                                   disjunctive normal form. We use a process similar to Weld’s
                                                                         (1994) to compile out first order quantifiers. Universal quan-
                  Problem Definition                                     tifications are replaced by conjunctions, and existential by
We use a domain from our previous work (Ware et al. 2019)                disjunctions, like so:
as a running example. Tom needs to get a potion for his                              ∀v(f = v) ↔ (f = v1 ) ∧ (f = v2 ) ∧ ...
grandmother. He begins in her cottage with one coin. There                           ∃v(f = v) ↔ (f = v1 ) ∨ (f = v2 ) ∨ ...
is a merchant at the market who is selling medicine and a                In keeping with classical planners, preconditions and effects
sword for one coin each. Also at the market is a guard with              must be finite, so quantifiers over numeric fluents are not
a sword who wants to punish criminals. In a nearby camp, a               permitted. We consider > and ⊥ to be in disjunctive normal
bandit has a sword and one coin and wants to acquire more                form. > is a disjunction of one empty clause and always
valuable items, like coins and medicine. A crossroads con-               true, while ⊥ is a disjunction of zero clauses and always
nects the cottage, the market, and the bandit’s camp. Char-              false. Negated literals are compiled out like so:
acters can walk from place to place. Characters with a coin
can buy an item from the merchant. Armed characters (i.e.                                     ¬(f = v) ↔ (f 6= v)
                                                                                             ¬(f > v) ↔ (f ≤ v) etc.
those who have swords) can attack and kill other characters.
Armed characters can steal items from unarmed characters.                   Complex doxastic expressions about what a character be-
                                                                         lieves are compiled out using these equivalencies:
Characters and Fluents                                                                          ¬b(c, x) ↔ b(c, ¬x)
A Sabre problem defines a finite number of characters, spe-                               b(c, x ∧ y) ↔ b(c, x) ∧ b(c, y)
                                                                                          b(c, x ∨ y) ↔ b(c, x) ∨ b(c, y)
cial entities which should appear to have beliefs and inten-
tions. Henceforth we use the term “character” rather than                These axioms, especially the first (not believing x is equiva-
“agent” because the narrative planner is the only decision               lent to believing ¬x), reflects Sabre’s constraint that charac-
maker, though it creates the appearance that each character              ters cannot be uncertain in their beliefs.
```

<!-- page: 3 -->
```text
   Preconditions may not be contradictions. For example, the             the initial state. When a character’s belief about a fluent is
precondition b(c, f = v) ∧ b(c, f 6= v) is not allowed (be-              not specified, we assume they believe its actual value. So if
cause characters must commit to beliefs).                                f = v and there is no explicit statement otherwise, we as-
   Effects describe how the state after an event differs from            sume b(c, f = v). Similarly, characters assume other char-
the state before. Effects can be conditional, meaning they               acters have the same beliefs they do. If the initial state ex-
may not apply, depending on the state before the event. Like             plicitly defines b(c1 , f = v) but has no explicit statement for
UCPOP (Penberthy and Weld 1992) and Fast Downward                        what c1 believes c2 believes, we assume b(c1 , b(c2 , f = v)).
(Helmert 2006), Sabre represents all effects as having a con-            These assumptions are only used for the initial state.
dition, even if that condition is simply >.                                 A problem defines an author utility function, which ex-
   A single effect e can be described by this grammar:                   presses preferences for the states the planner should attempt
                e :=   p→g                                               to reach. Recall the planner is the only decision maker, but
                g :=   f = v | fR = n | b(c, g)                          each character should appear realistic. A Sabre problem also
All effects have a condition p in disjunctive normal form.               defines a utility function for every character, and characters
The effect p → (f = v) means that, when p holds in the                   should act to improve their utility, but the planner chooses
state before, fluent f has value v in the state after. Numeric           when and how they act to maximize the author’s utility. In
fluents can be assigned numeric values following the gram-               other words, the puppetmaster tells the best story it can with-
mar for n given above (i.e. n is a number, numeric fluent, or            out making its puppets act out of character.
arithmetic expression). For example, > → (fR = fR + 1)                      Let u(cauthor , s) denote the author’s utility in state s. Let
means that fR has a value one higher in the state after.                 u(c, s) denote the utility of character c in state s. In our ex-
   Effects can modify character beliefs directly. p →                    ample domain, the author’s utility is as follows (where T is
b(c, f = v) means that, when p holds in the state before,                Tom, C the cottage, S the medicine, and loc is “location”):
character c believes fluent f has value v in the state af-
ter. Belief effects can also be arbitrarily nested, e.g. p →              hloc(T ) = C ∧ loc(S) = T → 2, alive(T ) = ⊥ → 1, > → 0i
b(c1 , b(c2 , f = v)) and so on. Having defined a single effect,         The system prefers stories where Tom succeeds in bringing
we define an effect expression as a conjunction of effects:              home the medicine, and will accept stories where he dies.
                 (p1 → g1 ) ∧ (p2 → g2 ) ∧ ...
In keeping with classical planning, effects must be deter-               Events: Actions and Triggers
ministic, so disjunctions and expressions equivalent to dis-             Events change the world state. Domain authors can create
junctions (like existential quantifications) are not permitted           two kinds of events: actions, which the planner can choose
in effect expressions. Effects may not be contradictions.                to take, and triggers, which must occur when they can.
   Utility functions are compiled into a normal form similar                An action a defines PRE(a) and EFF(a), which are its pre-
to effects. They are conditional, but exactly one condition              condition and effect expressions respectively. An action also
must hold in any state. A utility function is an ordered se-             defines CON(a), a set of 0 to many consenting characters.
quence of m conditional numeric expressions p → n:                       Consenting characters are the ones responsible for taking the
              hp1 → n1 , p2 → n2 , ..., > → nm i                         action. For an action to make sense in a narrative plan, every
                                                                         consenting character needs a reason to take the action.
Here, m ≥ 1, p1...m−1 are conditions in disjunctive normal                  Actions also define when characters observe them occur-
form, and n1...m are numeric values following the gram-                  ring. Formally, an action a defines a function OBS(a, c). For
mar for n above (a number, numeric fluent, or arithmetic                 any character c, OBS(a, c) returns a precondition expression
expression). The last condition pm must be > to ensure                   p such that, when p holds, c observes action a occur. When a
that one case will always hold. Utility functions are similar            character observes an action, they update their beliefs based
to if/elseif/else statements in a programming language. To               on the action’s effects. When a character does not observe
evaluate a utility function in a state, we consider each condi-          an action, their beliefs remain the same (unless explicitly
tional expression pi → ni in order until we find one where               modified by the effect). Consider this example action, where
pi holds, then we evaluate ni . In our example domain, the               character c1 attacks c2 at location x:
honest merchant (M ) wants money. Her utility function is:
                                                                           Action a: attack(c1 , c2 , x)
         hcriminal(M ) = > → 0, > → wealth(M )i                              PRE (a) =    alive(c1 ) = > ∧ loc(c1 ) = x ∧ loc(c2 ) = x
If she is a criminal, her utility is 0, else her utility is her              EFF(a) =     armed(c1 ) → alive(c2 ) = ⊥
number of coins. She prefers plans that increase her wealth,                CON (a) =     {c1 }
but will not commit crimes to do so.                                       OBS(a, c) =    loc(c) = x
                                                                         Suppose c1 is the bandit, c2 is Tom, and x is the crossroads.
Initial State and Goal                                                   This action can only occur if the bandit is alive and the ban-
The initial state defines an assignment of a value to every              dit and Tom are both at the crossroads. After it occurs, if
fluent; i.e. ∀f : f = v where v ∈ Df . It also defines                   the bandit was armed then Tom is dead, or else the action
any wrong beliefs that characters initially hold; for example,           has no effect. Only the bandit is a consenting character, be-
(f = v) ∧ b(c, f = u).                                                   cause only she needs a reason to take the action (Tom is a
   We use Shirvani et. al’s (2017) variant of the closed world           victim, and the action can occur whether or not he wants
assumption to assume any beliefs not explicitly defined in               it to happen). The observation function means that, for any
```

<!-- page: 4 -->
```text
character c, when loc(c) = x holds, c observes the action;                 includes the effect p → g, and character c observes action a
i.e. any characters also at the crossroads will see the action             occur, and c believes the condition p holds, then c will now
happen. They will know Tom is dead, whereas those not at                   believe g. If the guard is at the crossroads when the bandit
the crossroads will not know.                                              attacks Tom, and the guard believes the bandit is armed, then
   Triggers are events that must happen when their precondi-               the guard now believes Tom is dead even though this was not
tions are met. A trigger t defines PRE(t) and EFF(t) as above.             explicitly authored as an effect of the attack action.
Any time the world is in a state where PRE(t) holds, EFF(t)                   Sabre’s belief and observation model is a key strength.
must immediately be applied to change the world state. Ac-                 Authors can explicitly specify beliefs in preconditions and
tions advance time, but triggers do not. In other words, after             modify them in effects using the believes predicate (for ex-
time is advanced by taking an action, any number of triggers               ample, to create the lie action used in some domains (Farrell
may then apply to update the state, but they happen instantly.             and Ware 2020)), but Sabre automates many common belief
If multiple triggers can apply in a state, Sabre chooses arbi-             updates via observed actions and triggers.
trarily. To ensure determinism, triggers should not be defined
such that their outcome depends on order of execution.                     State Space
   Triggers are similar to the axioms and derived predi-
                                                                           Sabre starts at the initial state and searches forward through
cates of PDDL planners (Thiébaux, Hoffmann, and Nebel
                                                                           the space of possible future states for a solution. To save
2005), but not identical. Triggers modify state fluents di-
                                                                           memory, Sabre does not store a state as an array of values
rectly, whereas PDDL planners have basic predicates which
                                                                           for each fluent; rather, it stores the history of events that led
can be directly modified by effects and special derived pred-
                                                                           to that state, and when it needs to know the value of a fluent,
icates whose values are deduced from basic predicates.
                                                                           it looks back through the history for the last time the fluent
   Triggers are commonly used for sensing and belief up-
                                                                           was modified. Let s be a state, and let σ(e, s) denote the state
dates based on character observations. Consider this exam-
                                                                           after event e (recall an event is an action or a trigger). σ(e, s)
ple, which means “When characters c1 and c2 are in the same
                                                                           is only defined when s |= PRE(e), i.e. when the event’s pre-
place, c1 sees that c2 is there.”
                                                                           condition holds in s. For any literal l, we can determine if l
  Trigger t: see(c1 , c2 , x)                                              holds in σ(e, s) using this procedure:
  PRE (t) =    loc(c1 ) = x ∧ loc(c2 ) = x ∧ b(c1 , loc(c2 ) 6= x)           if e has result p → g, and p holds in s, and g |= l then
  EFF(t) =     > → b(c1 , loc(c2 ) = x)                                           return >                                            . Case 1
Suppose the bandit is at the crossroads and believes Tom                     else if e has result p → g, and p holds in s, and g ∧ l → ⊥ then
is in the cabin when Tom is actually at the market. If the                        return ⊥                                            . Case 2
bandit walks to the market, she should notice Tom is there                   else if PRE(e) |= l then return >                        . Case 3
and update her wrong belief about Tom’s location.                            else if PRE(e) ∧ l → ⊥ then return ⊥                     . Case 4
                                                                             else if l holds in s then return >                       . Case 5
   Triggers represent rules of the world common to all, so
                                                                             else return ⊥                                            . Case 6
they do not define consenting characters or observation func-
tions (i.e. if a character believes a trigger can happen, it does,         There are six cases. Case 1 states that l holds if e has a result
regardless of whether they want it to).                                    (whose condition holds) which makes l true. Case 2 states l
                                                                           does not hold if e has a result which contradicts l. Consider
                                                                           the precondition fR > 1. The effect fR = 0 is not an exact
Action Results Action effects have a finite number of con-                 negation of it, but fR > 1 ∧ fR = 0 is a contradiction, so this
juncts as specified by the domain author, but because actions              effect would make that precondition ⊥.
can be observed and because we impose no limit on the                         Characters can have wrong beliefs, so it is possible for
depth of theory of mind, actions can cause infinitely many                 them to observe an action happen which they did not believe
changes to the world state. In the above example, when Tom                 was possible. We call this a surprise action. When a charac-
is killed, the bandit knows it, and knows that she knows it,               ter is surprised, they first update their beliefs so the action’s
and knows that any bystanders know it, and knows that they                 precondition holds and then update their beliefs based on its
know that she knows it, and so on. Shirvani et al. (2017)                  effect. If the guard believes the bandit is dead, but then ob-
showed these changes can be expressed in a finite graph,                   serves the bandit attack Tom, this would be a surprise. The
and at any rate only a finite number of these changes actu-                guard updates his beliefs to realize the bandit is alive and
ally need to be calculated.                                                then considers the results of the attack. Cases 3 expresses
   Let RES(a) be the results of action a, a possibly infinite              this—that l holds if it is implied by e’s precondition. Case 4
conjunction composed of the action’s explicitly authored ef-               states that l does not hold if it contradicts e’s precondition.
fects and any effects implied by observations. Its conjuncts               Since cases 1 and 2 are checked first, 3 and 4 only apply
are in the same format as above, p → g, where p is a condi-                when l was not changed by a result of the event. If none of
tion and g is either an assignment of a value to a fluent or a             these cases apply, event e has no bearing on l, so we check l
belief. RES(a) is defined by these two rules:                              in the previous state s (cases 5 and 6).
        1.   RES (a) |= EFF ( A )                                             Recall that a trigger must be applied when its precondition
        2.   ∀c : (RES(a) |= p → g) ⇒                                      holds, and triggers happen instantly. When the world state is
             (RES(a) |= (OBS(a, c) ∧ b(c, p)) → b(c, g))                   s and there exists a trigger t such that s |= PRE(t), the world
The first rule states the effects of a are also results of a. The          must immediately transition to σ(t, s). For an event e and
second defines results implied by observations. If RES(a)                  state s, we define α(e, s) to be the state of the world after
```

<!-- page: 5 -->
```text
taking event e and then applying any relevant triggers. α is              see what things each other have via a trigger. Would Tom
defined recursively as:                                                   walking to the market be explained?
   function α(e, s)                                                          To be explained, it must be explained for all of its con-
      if σ(e, s) is undefined (i.e. s 6|= PRE(e)) then                    senting characters, which in this case is just Tom. Tom can
          return undefined                                                imagine a plan to walk to the market, buy the medicine, and
      else if ∃ trigger t such that σ(e, s) |= PRE(t) then                return to the cottage (#1). This plan is possible based on
          return α(t, σ(e, s))                                            his current beliefs (#2), and he thinks it will lead to a state
      else return σ(e, s)                                                 where his utility is higher (#3). Both Tom and the merchant
As shorthand, let α({a1 , a2 , ..., an }, s) represent the state          are consenting characters for the buy action. Tom anticipates
after a sequence of n actions taken in that order from state s.           the merchant will let him buy, which is reasonable given his
                                                                          beliefs about her (#4). Note that, in the initial state, the mer-
Explanations and Solutions                                                chant cannot form a plan to walk to the cottage and have
Having defined Sabre’s representation of the problem and                  Tom buy medicine, because the merchant does not think
its search space, we can now define a solution. Informally, a             Tom has any money. However, once Tom arrives at the mar-
solution is:                                                              ket, the merchant will see he has money via a trigger that
                                                                          updates the merchant’s beliefs. Now the merchant will con-
• A sequence of actions which can be executed and leads to                sent to buy. Requirement #5 means Tom cannot plan to walk
  a state where the author’s utility is improved.                         to the market, walk home, walk back to the market, buy the
• Every action taken by a character can be explained; i.e.                medicine, and walk home. This plan is redundant; the first
  each character believes the action can lead to increasing               two actions could be removed and it would still achieve the
  their utility.                                                          same utility.
                                                                             Finally, we define a solution. Let s0 be the initial state.
To state this formally, we need a way to refer to what a char-            A solution to a Sabre problem is a sequence of n actions
acter believes the state to be. Let c be some character, and              {a1 , a2 , ..., an } such that:
let s be any state. We define β(c, s) to be the state that c
believes to be the case when the actual state is s. This equiv-           1. α({a1 , a2 , ..., an }, s0 ) is defined.
alence defines β(c, s):                                                   2. u(cauthor , α({a1 , a2 , ..., an }, s0 )) > u(cauthor , s0 ).
                    s |= b(c, l) ↔ β(c, s) |= l                           3. All actions in {a1 , a2 , ..., an } are explained in the state
   We say an action a1 is explained in state s when it is ex-                immediately before they occur.
plained for all of CON(a1 ), its consenting characters. An ac-            4. No strict subsequence of {a1 , ..., an } meets these criteria.
tion a1 is explained for character c in state s when:                     In short, the plan is possible, leads to a state where the au-
1. There exists a sequence of n ≥ 1 actions {a1 , a2 , ..., an }          thor’s utility is higher, has only actions that make sense for
   that starts with a1 .                                                  characters, and is non-redundant. We validated this model
                                                                          of believable actions with human subjects (Shirvani, Farrell,
2. α({a1 , a2 , ..., an }, β(c, s)) is defined.
                                                                          and Ware 2018), but we also acknowledge believable char-
3. u(c, α({a1 , a2 , ..., an }, β(c, s))) > u(c, β(c, s)).                acter behavior is more than simply seeking higher utilities.
4. All actions ai where i > 1 are explained in the state be-              Parallel work is using Sabre to expand our definition to in-
   fore they occur; i.e. a2 is explained in α(a1 , β(c, s)), a3           clude emotion and personality (Shirvani and Ware 2020).
   is explained in α({a1 , a2 }, β(c, s)), and so on.
                                                                          Pre-Processing and Simplification
5. No strict subsequence of {a1 , a2 , ..., an } also meets these
                                                                          Sabre does several pre-processing steps before planning be-
   5 criteria and achieves the same or higher utility.
                                                                          gins. Recall that actions can have an infinite number of re-
Requirement #1 states that there exists a plan which starts               sults. Fortunately, there are a finite number of preconditions
with action a1 . #2 states that character c believes the plan             that ever need to be checked—those that appear in event pre-
can be executed. In other words, the plan can be executed                 conditions, action observation functions, effect conditions,
from β(c, s), even if it cannot be executed from s, perhaps               and utility conditions. As long as Sabre calculates all rele-
because c has wrong beliefs. #3 states that c believes the                vant results (i.e. all results that would affect this finite list of
plan will lead to a state where their utility is higher (even if          preconditions) it can be sound. During pre-processing, Sabre
it actually won’t). #4 states that the rest of the actions (after         does this, adding explicit effects to actions so that all rele-
a1 ) must also be explained. For example, if the plan relies on           vant results are accounted for.
actions by other characters, those actions must make sense                   The astute reader may also notice a challenge that arises
for those characters. #5 states that the sequence should not              when dealing with triggers and beliefs. Triggers are not only
include redundant or unnecessary actions.                                 checked in the current state s, but also in every character’s
   Suppose Tom is at the cottage, has one coin, and wants                 beliefs. Consider an example trigger, where x and y are
medicine. The merchant is at the market, has medicine, and                shorthand for any two literals:
wants coins. Tom knows where the merchant is and that                                              Example trigger t:
the merchant has medicine, but the merchant does not know                                          PRE (t) =   x ∧ ¬y
Tom has money. When characters are in the same place, they                                         EFF(t) =    >→y
```

<!-- page: 6 -->
```text
Algorithm 1 The Sabre algorithm                                            uses the special author character cauthor , the initial state of
 1: Let A be the set of all actions defined in the domain.                 the problem s0 , and the empty plan ∅.
 2: SABRE(cauthor , s0 , ∅, s0 )                                              SABRE starts by checking if the current plan is a solution
 3: function SABRE(c, r, π, s)                                             (line 5). It is a solution if character c’s utility is higher in s
 4:     Input: character c, start state r, plan π, current state s         than it was in r and the plan does not contain unnecessary
 5:     if u(c, s) > u(c, r) and π is non-redundant then                   actions. If the current plan is not a solution, we nondeter-
 6:         return π                                                       ministically choose an action a to add to the plan. Before
 7:     Choose an action a ∈ A such that s |= PRE(a).                      exploring further, Sabre checks whether that action can be
 8:     for all c0 ∈ CON(a) such that c0 6= c do                           explained for all the consenting characters other than c (lines
 9:         Let state b = α(a, β(c0 , s)).
10:         if b is undefined then return failure.
                                                                           8 to 11). When c is cauthor Sabre needs to find an explana-
11:         else if SABRE(c0 , b, ∅, b) fails then return failure.         tion for all consenting characters. When c is a character, they
12:     return SABRE(c, r, π ∪ a, α(a, s))                                 must be able to anticipate the cooperation of other characters
                                                                           who are part of their plan. Tom must expect the merchant
                                                                           will consent to the buy action to include it in his plan.
                                                                              There are two cases where a cannot be explained for a
And imagine c is a character and the state is:                             consenting character c0 . The first is when α(a, β(c0 , s)) is
                   x ∧ y ∧ b(c, x) ∧ b(c, ¬y)                              undefined (line 10), meaning c0 does not think a is possible.
The trigger does not apply in this state, but it does apply in             The second is when c0 cannot imagine a plan starting with a
β(c, s). The state should immediately transition to:                       that improves their utility (line 11), so they have no reason
                                                                           to consent to it. Tom cannot expect the merchant to sim-
                    x ∧ y ∧ b(c, x) ∧ b(c, y)                              ply give him the medicine, because it wouldn’t improve the
The above procedure defining α fails to capture this case.                 merchant’s utility. If the action can be explained for all con-
Even if it did, since Sabre does not limit how far theory of               senting characters besides c, we add a to the plan, advance
mind can be nested, it is difficult to know how many lev-                  the current state to α(a, s), and recursively call SABRE.
els of beliefs need to be checked to make sure all triggers                   If the first call to SABRE on line 2 returns a plan, it is a so-
have been applied. We previously proposed a solution to this               lution to the problem; i.e. it can be executed from the initial
problem (Shirvani, Ware, and Farrell 2017), but it requires                state, leads to a state where the author’s utility is higher, and
graph isomorphism checks. Sabre works differently.                         all actions can be explained for all characters. SABRE may
   During pre-processing, for every character c, if there exist            not terminate if no solution exists, so in practice we impose
a trigger t with the effect p → g, and b(c, g) is in the set of            a maximum depth on the search.
all possible preconditions, a new trigger t0 is generated with
PRE (t0 ) = b(c, PRE (t)) and EFF(t0 ) = b(c, EFF(t)). This en-                                     Evaluation
sures triggers also account for all relevant effects. To use the
earlier example:                                                           Sabre has a unique set of features; it is a centralized planner
                                                0                          that reasons about intentions and beliefs with no limit on the-
  Original trigger t:            New trigger t :
  PRE (t) =    x ∧ ¬y      ⇒           0
                                 PRE (t ) =   b(c, x) ∧ b(c, ¬y)
                                                                           ory of mind but without uncertainty. These features are ideal
  EFF(t) =     >→y                     0
                                 EFF(t ) =    > → b(c, y)                  for the interactive narratives we generate, but they make it
                                                                           difficult to compare to other systems, so we use benchmark
The preconditions of these new triggers may add to the set of              problems to convey the scope of what Sabre can solve and
all possible precondition literals, so the process is repeated             an ablation study to motivate its combination of features.
until no new triggers are needed. It is possible to construct                 Intentional planners like IPOCL (Riedl and Young 2010),
triggers that would cause this process to run infinitely, in               Glaive (Ware and Young 2014), and the original IMPRAC-
which case we would need to revert to our previous graph-                  Tical (Teutenberg and Porteous 2013) do not reason about
based solution, but in practice we have never encountered                  beliefs, so either Sabre must reason about belief when it is
such a domain.                                                             not required, or the problems are unsolvable by the inten-
   Sabre also uses methods adapted from other planners                     tional planners. HeadSpace (Thorne and Young 2017) and
(Hoffmann 2003; Helmert 2006) to simplify the problem by                   a later version of IMPRACTical (Teutenberg and Porteous
detecting propositions which must always be true or false.                 2015) limit theory of mind. Multi-agent systems like Thes-
This sometimes allows the removal of fluents, actions, and                 pian (Si and Marsella 2014) and Talk of the Town (Ryan
triggers from the domain to reduce the time and memory re-                 et al. 2015) reason about theory of mind but have limited
quired during search.                                                      centralized planning, so they would need to run many times
                                                                           until they happen to achieve the author’s goal. Ostari (Eger
                             Search                                        and Martens 2017) is centralized with intentions and beliefs
Sabre’s search procedure is given in Algorithm 1. It takes                 but allows true uncertainty, which dramatically increases the
four inputs: the character c for whom we want to find a plan,              size of its search space. As a test, the smallest example from
the state r where the search began, a plan π, and the current              the Lovers domain (described below) was implemented and
state s. The plan π is the sequence of actions that can be                 tested in Ostari, but it quickly ran out of memory before find-
executed from r to reach s. SABRE can find a plan for any                  ing any solutions. None of these makes for a fair empirical
character from any state. The initial call to SABRE (line 2)               comparison.
```

<!-- page: 7 -->
```text
                                                                                                    Sabre     Intention      Belief
    Domain        Char.    Fluents   Actions    Triggers     Time       Visited     Generated
                                                                                                       3       3      7     3       7
  Raiders (1)         3         21         39         66      1.4 s         905        17,815           3       0     0      0    110
   Space (1)          2         16         32          0      6 ms           18           192           2       1     1      1      0
  Treasure (1)        3          4         34          0      1 ms           22           288           2       0     0      2      2
   Hubris (1)         2         29         14          0     47 ms           58           831           1       0     0      1      2
 BearBirdJr (1)       2         13         20          0    14.0 m      290,711    34,084,608           6       0     0      0    110
   Lovers (9)         3      111.3      312.0      370.0    40.3 m      126,983     5,198,414        10.0     0.0 0.0      3.2 50.7
 Grandma (2)          4       61.0      836.0      896.0      6.2 h     598,577   105,178,466         1.0     0.0 0.0      1.0 13.0
       Table 1: Performance on benchmark problems (left) and ablation study solution counts for those problems (right)
   Since we cannot make a direct comparison, we demon-                  found vs. the ablated planners. 3 counts solutions which
strate the scale of problems Sabre can solve with a suite               were also valid Sabre plans, and 7 counts those which were
of benchmark narrative domains from the literature by sev-              valid according to the ablated model but not Sabre. As em-
eral authors. Raiders was introduced with Glaive (Ware and              phasized above, it is unfair to compare these ablated plan-
Young 2014) as a problem that requires failed plans and con-            ners on problems they were not designed to solve. These
flict; Space is an additional Glaive domain. Since Glaive               numbers do not prove Sabre better than previous planners;
domains were authored for a planner that did not support                we report them only to motivate reasoning about intention
belief, we added common sense beliefs and triggers (e.g.                and belief together. We want to show that one could not,
characters observe actions that occur in their location, etc.).         for example, simply add an extra check on solutions gen-
Raiders had a pseudo-belief predicate (“knows location of”)             erated by an intentional planner to get a system that effec-
which we replaced with true beliefs. Treasure (Shirvani, Far-           tively reasons about belief as well. We previously demon-
rell, and Ware 2018) and Hubris (Christensen, Nelson, and               strated that audiences significantly prefer stories with both
Cardona-Rivera 2020) require intentions and wrong beliefs.              intention and belief and notice flaws when one or the other
BearBirdJr simplifies Sack’s (1992) Micro-TaleSpin version              is lacking (Shirvani, Farrell, and Ware 2018).
of Meehan’s (1977) story generator. Lovers was introduced
for belief and intention recognition tasks (Farrell and Ware                                Implementation
2020); this domain is parameterizable, so we randomly gen-
erated 10 instances known to be solvable. 9 were success-               The planner and relevant documentation can be found here:
fully solved by Sabre. Grandma is from a recent planning-
                                                                                  http://cs.uky.edu/∼sgware/projects/sabre
based narrative game (Ware et al. 2019) with intention and
belief and has two instances: one where the player wins and
one where the player dies. We will publish these domains                  Limitations, Future Work, and Conclusion
and problems with our modifications in a technical report.              This paper describes Sabre, the first narrative planner to sup-
   Table 1 shows the number of characters, fluents, actions,            port ADL, numeric fluents, intention, and deep theory of
and triggers for the problems in each domain after ground-              mind. Though Sabre enables automated story generation, the
ing and simplification. The table shows the time required to            general problem of author burden remains; domain authors
find the first solution for each problem in the domain and the          must still define fluents, actions, triggers, and utility func-
number of nodes visited and generated by the search. When               tions, and debugging narrative planning domains is hard.
a domain has several problems (and thus may have a differ-              Supporting theory of mind enables new stories (Shirvani,
ent numbers of triggers, solutions, etc.), we give averages.            Ware, and Farrell 2017), but adds significant cost above an
We used a deterministic A* version of Algorithm 1 (Pohl                 intention-only planner. Sabre does not support uncertainty,
1970), using Bonet and Geffner’s h+ heuristic (2001) on a               which may be necessary for some stories, like murder mys-
Dell Precision 7920 x64 with a 2.1 GHz processor.                       teries (Eger 2020).
   Since we cannot compare directly to other planners, we                  The most obvious direction for future work is to explore
motivate Sabre’s features via ablation. For our test prob-              improvements to the search process via pruning and better
lems, we generated all plans at or below a fixed length and             heuristics that account for the beliefs and intentions, an ap-
counted which are solutions according to full Sabre, Sabre              proach which dramatically improved performance for other
with only intention, and Sabre with only belief. Only inten-            narrative planners like Glaive and IMPRACTical.
tion means characters try to improve their utility, but beliefs
are not tracked and all characters are omniscient; it approx-
imates intentional planners like IPOCL, Glaive, and early                                  Acknowledgments
IMPRACTical. Only belief means beliefs are tracked and                  This work was supported by the National Science Founda-
characters only take actions they think possible, even if the           tion, Grant No. IIS-1911053. All opinions are our own. We
actions can’t lead to improving their utility; it approximates          thank Markus Eger for implementing a sample problem in
belief-only planners like HeadSpace. The fixed length for a             Ostari to evaluate the suitability of comparing it to Sabre.
problem is the min depth at which any Sabre solutions exist.            We thank Rachelyn Farrell for her help in converting do-
   The right of Table 1 show how many solutions Sabre                   mains to Sabre’s format.
```

<!-- page: 8 -->
```text
                        References                                       Ryan, J. O.; Summerville, A.; Mateas, M.; and Wardrip-
Bonet, B.; and Geffner, H. 2001. Planning as heuristic                   Fruin, N. 2015. Toward characters who observe, tell, mis-
search. Artificial Intelligence 129(1): 5–33.                            remember, and lie. In Proceedings of EXAG Workshop at
                                                                         AIIDE, 56–62.
Brinke, H. t.; Linssen, J.; and Theune, M. 2014. Hide and
                                                                         Sack, W. 1992. Micro-TaleSpin: a story generator. http:
Sneak: story generation with characters that perceive and as-
                                                                         //lispm.de/source/misc/micro-talespin.lisp. Accessed: 2021-
sume. In Proceedings of AIIDE, 174–180.
                                                                         08-06.
Cavazza, M.; Charles, F.; and Mead, S. J. 2002. Character-               Shirvani, A.; Farrell, R.; and Ware, S. G. 2018. Combin-
based interactive storytelling. IEEE Intelligent Systems spe-            ing intentionality and belief: revisiting believable character
cial issue on AI in Interactive Entertainment 17(4): 17–24.              plans. In Proceedings of AIIDE, 222–228.
Christensen, M.; Nelson, J.; and Cardona-Rivera, R. E.                   Shirvani, A.; and Ware, S. G. 2020. A formalization of emo-
2020. Using domain compilation to add belief to narrative                tional planning for strong-story systems. In Proceedings of
planners. In Proceedings of AIIDE, volume 16, 38–44.                     AIIDE, 116–122.
Eger, M. 2020. Murder mysteries: the white whale of narra-               Shirvani, A.; Ware, S. G.; and Farrell, R. 2017. A possible
tive generation? In Proceedings of AIIDE, 210–216.                       worlds model of belief for state-space narrative planning. In
Eger, M.; and Martens, C. 2017. Character beliefs in story               Proceedings of AIIDE, 101–107.
generation. In Proc. of INT Workshop at AIIDE, 184–190.                  Si, M.; and Marsella, S. C. 2014. Encoding Theory of Mind
Farrell, R.; and Ware, S. G. 2020. Narrative planning for                in character design for pedagogical interactive narrative. Ad-
belief and intention recognition. In Proc. of AIIDE, 52–58.              vances in Human-Computer Interaction .
Helmert, M. 2006. The Fast Downward planning system.                     Teutenberg, J.; and Porteous, J. 2013. Efficient intent-based
Journal of Artificial Intelligence Research 26: 191–246.                 narrative generation using multiple planning agents. In
                                                                         Proceedings of the 2013 international conference on Au-
Hoffmann, J. 2003. The Metric-FF planning system: trans-                 tonomous Agents and Multiagent Systems, 603–610.
lating “ignoring delete lists” to numeric state variables.
                                                                         Teutenberg, J.; and Porteous, J. 2015. Incorporating global
Journal of Artificial Intelligence Research 20: 291–341.
                                                                         and local knowledge in intentional narrative planning. In
Kybartas, Q.; and Bidarra, R. 2016. A survey on story gen-               Proceedings of the 2015 international conference on Au-
eration techniques for authoring computational narratives.               tonomous Agents and Multiagent Systems, 1539–1546.
IEEE Transactions on Computational Intelligence and Ar-                  Thiébaux, S.; Hoffmann, J.; and Nebel, B. 2005. In defense
tificial Intelligence in Games 9(3): 239–253.                            of PDDL axioms. Artificial Intelligence 168(1-2): 38–69.
Martin, L. J.; Ammanabrolu, P.; Wang, X.; Hancock, W.;                   Thorne, B. R.; and Young, R. M. 2017. Generating stories
Singh, S.; Harrison, B.; and Riedl, M. O. 2018. Event repre-             that include failed actions by modeling false character be-
sentations for automated story generation with deep neural               liefs. In Proceedings of INT Workshop at AIIDE, 244–251.
nets. In Proceedings of AAAI, 868–875.
                                                                         Ware, S. G.; Garcia, E. T.; Shirvani, A.; and Farrell, R. 2019.
Meehan, J. R. 1977. TALE-SPIN, an interactive program                    Multi-agent narrative experience management as story graph
that writes stories. In Proceedings of the 5th International             pruning. In Proceedings of AIIDE, 87–93.
Joint Conference on Artificial Intelligence, 91–98.
                                                                         Ware, S. G.; and Young, R. M. 2011. CPOCL: a narrative
Pednault, E. P. D. 1987. Formulating multiagent, dynamic-                planner supporting conflict. In Proc. of AIIDE, 97–102.
world problems in the classical planning framework. In Rea-              Ware, S. G.; and Young, R. M. 2014. Glaive: a state-space
soning About Actions & Plans, 47–82.                                     narrative planner supporting intentionality and conflict. In
Penberthy, J. S.; and Weld, D. S. 1992. UCPOP: a sound,                  Proceedings of AIIDE, 80–86.
complete, partial order planner for ADL. In Proceedings of               Ware, S. G.; Young, R. M.; Harrison, B.; and Roberts, D. L.
the 3rd International Conference on Principles of Knowl-                 2014. A computational model of narrative conflict at the fab-
edge Representation and Reasoning, volume 92, 103–114.                   ula level. IEEE Transactions on Computational Intelligence
Pohl, I. 1970. First results on the effect of error in heuristic         and Artificial Intelligence in Games 6(3): 271–288.
search. Machine Intelligence 5: 219–236.                                 Weld, D. S. 1994. An introduction to least commitment
Porteous, J.; Cavazza, M.; and Charles, F. 2010. Applying                planning. AI magazine 15(4): 27–61.
planning to interactive storytelling: Narrative control using            Young, R. M.; Ware, S. G.; Cassell, B. A.; and Robertson,
state constraints. ACM Transactions on Intelligent Systems               J. 2013. Plans and planning in narrative generation: a re-
and Technology 1(2): 1–21.                                               view of plan-based approaches to the generation of story,
Riedl, M. O.; and Bulitko, V. 2013. Interactive narrative: an            discourse and interactivity in narratives. Sprache und Daten-
intelligent systems approach. AI Magazine 34(1): 67–77.                  verarbeitung, Special Issue on Formal and Computational
                                                                         Models of Narrative 37(1-2): 41–64.
Riedl, M. O.; and Young, R. M. 2010. Narrative planning:
balancing plot and character. JAIR 39(1): 217–268.
```
