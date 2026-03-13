# Mueller Context Propagation Audit

Question: how does a planning-context change such as REVERSAL's
alternative-past branch propagate into changed later salience?

Short answer: Mueller does not appear to maintain a separate numeric
"situation activation" table. The propagation path is indirect:

1. control picks a branch-local `next-context`
2. GATE determines which facts are true in that context
3. `run-rules` fires inference and plan rules against those visible facts
4. those rules activate goals, assert states, and drive episodic retrieval
5. later behavior changes because the branch exposes a different fact set and
   different dependencies, not because a special situation score was updated

That matters for the sidecar. The kernel's `:situations` table is a modern
reporting/adaptation layer, not a direct Mueller mechanism. If we want
REVERSAL to change later situation/node selection in a Mueller-shaped way, the
missing pass is "derive new salience from branch-visible facts and reminding
indices", not "mutate the situation table directly."

## Source Findings

### 1. Control always runs the planner in the current branch context

In `dd_cntrl.cl`, `daydreamer-control1` gets the top-level goal's
`next-context`, runs `run-rules` there, then either terminates, backtracks, or
moves into one of that context's sprouts.

Relevant source:

- `daydreamer/dd_cntrl.cl:300`
- `daydreamer/dd_cntrl.cl:318`
- `daydreamer/dd_cntrl.cl:332`
- `daydreamer/dd_cntrl.cl:336`

This is the control-level propagation point. Whatever facts are visible in that
context are the facts the planner sees on that cycle.

### 2. Imaginary goal activation creates a fresh planning branch

`activate-top-level-goal` in `dd_cntrl.cl` creates an imaginary goal's
planning context by sprouting the current context, then stores that new context
as the goal's `activation-context`, `backtrack-wall`, and `next-context`.

Relevant source:

- `daydreamer/dd_cntrl.cl:656`
- `daydreamer/dd_cntrl.cl:693`
- `daydreamer/dd_cntrl.cl:696`

So later planning always proceeds inside a specific branch, not against a
global world state.

### 3. GATE controls visibility by branch, not by global mutation

`cx$sprout` copies the parent's visible facts (`all-obs`) and differential
metadata. `cx$pseudo-sprout-of` re-parents a root context under another
context without making it inherit the parent's content.

Relevant source:

- `daydreamer/gate_cx.cl:26`
- `daydreamer/gate_cx.cl:36`
- `daydreamer/gate_cx.cl:82`
- `daydreamer/gate_cx.cl:89`

This is the crucial REVERSAL shape:

- ordinary sprouts inherit content
- pseudo-sprouts keep independent content but participate in the tree

That means an alternative-past branch can sit under the current planning tree
while still carrying its own copied past facts.

### 4. Fact truth is context-sensitive

`cx$assert`, `cx$retract`, and `cx$true?` implement truth in context via
`add-obs`, `remove-obs`, `all-obs`, and ancestor checks. An object's
`top-context` records where it was asserted, and truth depends on whether that
assertion remains valid in the queried branch.

Relevant source:

- `daydreamer/gate_cx.cl:282`
- `daydreamer/gate_cx.cl:319`
- `daydreamer/gate_cx.cl:430`

So when REVERSAL copies a context and asserts counterfactual input states into
that copied branch, those facts become true there and only there.

### 5. REVERSAL changes branch content, ownership, and activation roots

`sprout-alternative-past` in `dd_reversal.cl`:

- copies the old context with `cx$copy`
- pseudo-sprouts that copy under the new planning context
- removes emotions
- garbage-collects planning structure unrelated to the old top-level goal
- rewrites remaining planning structure onto the new top-level goal
- resets active goals' `activation-context` to the new branch

Relevant source:

- `daydreamer/dd_reversal.cl:321`
- `daydreamer/dd_reversal.cl:330`
- `daydreamer/dd_reversal.cl:341`
- `daydreamer/dd_reversal.cl:344`
- `daydreamer/dd_reversal.cl:366`
- `daydreamer/dd_reversal.cl:375`

This is not just branch creation. It actively retargets planning ownership and
future reasoning to the alternative-past context.

### 6. Counterfactual input states are asserted into the sprouted branch

`reverse-undo-causes` collects `input-states` from the backwards planning path
and asserts them into the alternative-past branch after
`reversal-sprout-alternative`.

Relevant source:

- `daydreamer/dd_reversal.cl:146`
- `daydreamer/dd_reversal.cl:242`
- `daydreamer/dd_reversal.cl:264`
- `daydreamer/dd_reversal.cl:268`
- `daydreamer/dd_reversal.cl:273`

This is the most direct answer to "where do the counterfactuals go?"

They are not stored in a side channel. They are asserted as branch-local facts
inside the new planning context.

### 7. Theme rules fire from visible facts in the current context

Mueller's family activations happen as inference rules during `run-rules`, not
as a separate global scheduler pass. The sidecar's pre-competition activation
pass is an approximation of this.

Relevant source:

- `daydreamer/dd_rule1.cl:381`
- `daydreamer/dd_kb.cl:2064`
- `daydreamer/dd_kb.cl:2731`

`Roving-Theme` and `Reversal-Theme` both activate from dependencies between
failed goals and negative emotions that are true in the current context. So if
the branch changes those visible dependencies, later theme activations change.

### 8. Episodic indexing is also scoped by activation context

`dd_epis.cl` does not expose "situation activation" as a scalar, but it does
use activation-context ancestry to decide which states/goals count as part of a
scenario and which indices get stored.

Relevant source:

- `daydreamer/dd_epis.cl:489`
- `daydreamer/dd_epis.cl:579`
- `daydreamer/dd_epis.cl:582`

Two important consequences:

- branch-local facts can change what gets indexed or reminded
- the activation-context boundary is part of what defines the scenario

So altered branch content can change later recall pressure even without any
explicit "situation score" update.

## What This Means For The Kernel

The sidecar already has the branch mechanics:

- branch-local contexts
- REVERSAL alternative-past creation
- branch-local counterfactual fact assertion
- family activation
- episodic reminding

What it does not yet have is the Mueller-shaped propagation step from
"different visible branch facts" to "different derived situation/node
salience."

Right now the Puppet Knows semi-unscripted run shows exactly that gap:

- cycle 10 selects `reversal` instead of `rehearsal`
- the branch contains counterfactual facts
- but the exported `:situations` table and node arrival are still mostly
  fixture-driven

So the next implementation target should be:

1. derive situation/node salience from branch-visible facts and reminding state
2. run that derivation after family-plan mutation, especially after REVERSAL
3. let the branch-local counterfactual facts change the next visible
   situation/node instead of directly scripting that arrival

## Recommended Kernel Pass

Do not try to find a Mueller function that updates a `:situations` map. There
is no clear equivalent in these sources.

Instead, implement a bounded adapter layer that:

1. reads `visible-facts` from the selected branch context
2. reads branch-local retrieval/reminding output
3. maps those facts/indices onto the conducted system's situation ids
4. updates the kernel's derived situation salience from that mapping

In other words: the missing piece is not a new control rule. It is the
translation from Mueller-style branch content into the sidecar's
Director-facing situation representation.
