# Why Rules as Data

The kernel was built to match Mueller's behavioral description: a
character that worries, rehearses, rationalizes, reverses. And it
works at that level. But Mueller's actual contribution wasn't the
behavior — it was the substrate.

## The problem with procedural families

The hand-coded kernel has correct behavior. Roving activates when
emotion is low and no rationalization frame exists. Rationalization
fires when a stored reframe is available. Reversal does context
tree surgery. All tested, all passing.

But that logic is locked inside function bodies. When goal_families
checks "is there a failed goal with negative emotion above threshold
and no rationalization frame? → activate roving," that relationship
is invisible to everything except the function that runs it. No
other part of the system can discover it. It can't be searched,
connected, or composed with other relationships.

## What changes when behavior becomes data

When the same logic becomes a RuleV1 with typed antecedent and
consequent schemas, three things change:

**The connection graph can see it.** A rule's output schema and
another rule's input schema create a computable edge. That edge
exists whether or not the rules have ever fired together. That's
what makes serendipity possible — finding structural paths that
nobody hand-coded as connections.

**Episodes can carry it.** When a rule fires, the episode stores
the rule-path — which rules fired, which edges were traversed.
Later retrieval can boost episodes whose cognitive history connects
to the current situation through the graph. The system uses its
own structural history to shape what it remembers.

**The system can reason about its own behavior.** A path through
the connection graph from rationalization-afterglow to roving-
activation is an inspectable, testable, discoverable structural
claim. Remove the bridge rule, and behavior changes. The procedural
version has the same truth buried in an if-statement — true but
opaque.

## What the condensation revealed

The project was building from chat-level summaries of Mueller. The
condensation forced reading every chapter at mechanism resolution —
not "what does this chapter describe?" but "what are the inputs,
outputs, state changes, and structural properties?"

At that resolution, the rule connection graph stops being a nice-
to-have and becomes the load-bearing infrastructure that planning,
serendipity, and mutation all depend on. Chapter 4 isn't "characters
rehearse conversations." It's "episodes are decomposable planning
trees, and retrieval over explicit indices enables recursive
reminding cascades." Chapter 5 isn't "characters have creative
insights." It's "the rule connection graph is a precomputed search
space, and serendipity is structural path verification through
that space."

The behavioral kernel was always a correct first step. The migration
to rules-as-data is the second step that makes the first one
compound.

## The migration opens the architecture

The procedural kernel was a closed system. Each family's behavior
was a bespoke function that knew about its own domain. You couldn't
plug in ideas from other cognitive architectures because there was
no shared substrate.

With rules-as-data and a connection graph, the substrate becomes
a lingua franca:

**ACT-R's production system** — condition/action rules over working
memory. RuleV1 antecedent/consequent schemas are structurally the
same. Buffer discipline, typed retrieval outcomes, utility learning
become features of the rule dispatch layer.

**SOAR's chunking** — when a subgoal resolves, compile the
resolution into a new rule. The system creates a new RuleV1 from
the chain's bindings, adds it to the graph, and the connection
graph automatically derives new edges.

**CoALA's tool-use patterns** — map onto the :llm-backed executor.
The difference: rules are graphable data, not opaque tool calls.

**Copycat's fluid concepts** — the hardest to absorb, but if the
LLM can propose new rules with different descriptor vocabularies,
and those rules enter the graph and get validated structurally,
that's a mechanism for gradual descriptor evolution.

The migration didn't just make existing behavior graphable. It
gave the project a shared substrate that ideas from other cognitive
architectures can be expressed in.

## The honest state

The kernel's behavioral coverage of Mueller is ~40% — episodic
memory, context tree, goal competition, reminding, three family
plan bodies. That hasn't regressed.

The rule engine's coverage of the kernel is ~15-20% — activation
and plan dispatch for three families, migrated into graphable
RuleV1 data. One cross-family bridge discovered (rationalization
afterglow → roving activation). Provenance-weighted retrieval
working.

The remaining ~60% of Mueller's mechanisms are designed (19
mechanism cards) but not yet extracted into the rule substrate.
The creative engine — serendipity path search, analogical repair,
mutation — is designed and has a clear build path but is not built.

The bet: persistent typed cognitive structure, owned by a symbolic
engine and open to ideas from ACT-R, SOAR, and the broader
cognitive architecture literature, produces behavior that
accumulates in ways no prompt-only system can match.
