# MINSTREL — L1 Extraction

Source: Turner, "A Case-Based Model of Creativity"
(AAAI Spring Symposium, 1993)

Source file: `sources/markdown/MINSTREL/source.md`

Purpose: Extract the Transform-Recall-Adapt model for dead-end
recovery and variation generation in L1 authoring operators.

Note: In the settled architecture (doc 11), MINSTREL is an L1
authoring-side precedent. L2 character cognition uses Mueller's
own mechanisms (rehearsal, reversal, rationalization, roving)
which are grounded in cognitive science. TRAM-style creativity
heuristics should not be imported into L2 runtime — that would
blur the separation between authoring-time exploration and
performance-time cognition.

---

## What MINSTREL actually describes

MINSTREL is a case-based storytelling system (King Arthur domain)
that creates stories by solving authorial goals ("create a scene
where a knight kills himself") through creative problem-solving.

The core insight: **creativity is driven by the failure of
standard problem-solving.** When the system can't recall a
directly matching case, it doesn't give up — it transforms the
problem slightly, recalls a solution for the transformed problem,
and adapts that solution back.

---

## Transform-Recall-Adapt Methods (TRAMs)

TRAMs are creativity heuristics that bundle three steps:

```
1. Transform: change the problem slightly
2. Recall: find a solution for the changed problem
3. Adapt: reverse the transformation on the solution
```

Because the adaptation is **paired with the transformation**,
it's not arbitrary — the system knows how to undo what it
changed.

### The three TRAMs demonstrated

| TRAM | Transform | Recall | Adapt |
|------|-----------|--------|-------|
| Generalize-Constraint | Remove a constraint from the problem (e.g., "the victim is the actor" → "the victim is any violent character") | Recall episode matching relaxed specification | Re-add the constraint to the recalled solution |
| Similar-Outcomes | Change magnitude of a state change (e.g., "killed" → "injured") | Recall episode matching the weaker version | Strengthen the outcome back to original |
| Cross-Domain-Reminding | Translate problem to a different domain | Recall solution from new domain | Map solution back to original domain |

### Example: inventing three suicides

Starting from only two known stories (a knight fight and a
princess drinking a potion), MINSTREL invents:

1. Knight loses fight with dragon on purpose (Generalize-Constraint)
2. Knight drinks poison (Cross-Domain-Reminding via "princess
   drinks potion")
3. Knight hits himself with his sword (Similar-Outcomes:
   injury → death)

---

## Imaginative memory

The key recursive trick: if recall fails (no matching episode
exists), the system recursively applies TRAMs to the *recall
features themselves*. Each recursion transforms the problem
further. When a match is eventually found, the chain of
adaptations is applied in reverse.

This means: **episodic memory becomes imaginative.** When an
appropriate episode exists, it's recalled normally. When none
exists, the system "imagines" an appropriate episode by chaining
small transformations.

Each individual TRAM makes a small change. But recursive
application can produce solutions that are very different from
any known case.

---

## The boredom assessment

MINSTREL includes a **boredom assessment** that rejects solutions
too similar to past solutions. This forces the creative process
to keep producing novel output rather than converging on one
approach.

---

## What maps to L1

| MINSTREL concept | L1 equivalent | Notes |
|-----------------|---------------|-------|
| TRAM | Dead-end recovery strategy for L1 operators | When an L1 proposal is rejected, mutate rather than generate from scratch |
| Generalize-Constraint | Relax one constraint on a rejected proposal | Remove the aspect that caused rejection, re-try, re-add |
| Similar-Outcomes | Soften or strengthen a proposed change | If "major conflict" was rejected, try "minor tension" |
| Cross-Domain-Reminding | Analogical transfer from different world context | Retrieve a successful pattern from a different brief or setting |
| Boredom assessment | Novelty gate on L1 proposals | Reject proposals too similar to recently accepted ones |
| Imaginative memory | Recursive retrieval with transformation | When L1 episodic retrieval fails, transform features and try again |

---

## What's genuinely useful

### 1. Mutation over generation when L1 proposals are rejected

When an L1 operator produces a proposal that the human rejects,
don't start over. **Mutate the rejected proposal.** Remove or
weaken the aspect that caused rejection. This is cheaper than
fresh generation and preserves whatever was good about the
original idea.

Mueller §5.4 (Action Mutation) describes the same principle:
substitution, permutation, relaxation when a planning branch
is exhausted. MINSTREL gives it a procedural form.

Note: This pattern belongs at authoring time (L1), not at
character-cognition runtime (L2). L2 has its own mechanisms
for dead-end recovery (backtracking, mode oscillation) that
are grounded in Mueller's cognitive model.

### 2. Paired transform-adapt as a design pattern

The TRAM structure solves the creativity paradox: random
exploration is too expensive to adapt, but nearby cases are
too similar to be novel. Pairing each transformation with its
specific adaptation makes creative retrieval practical.

For L1 operators: each operator should know not just how to
generate proposals, but how to *transform rejected proposals*
in a specific, reversible way.

### 3. Boredom/novelty as a gate

The boredom assessment is simple but important: if the system
keeps proposing similar ideas, the human will stop paying
attention. Gate proposals on novelty relative to recent
acceptances. Sentient Sketchbook's novelty search reaches for
the same principle from a different direction.

---

## What to take

1. **The TRAM pattern for L1 dead-end recovery.** When L1
   authoring proposals are rejected, apply paired transform-adapt
   mutations rather than generating from scratch.

2. **A novelty gate on proposals.** Reject proposals too similar
   to recently accepted or recently rejected material.

3. **Recursive retrieval as fallback.** When direct episodic
   retrieval fails, transform the retrieval features and try
   again. Each transformation is small; chaining produces
   genuine novelty.

What NOT to take:

- The King Arthur domain
- The specific Lisp implementation
- Full "imaginative memory" as a first implementation —
  start with single-step TRAMs, not recursive chains
- The claim that this constitutes a general theory of creativity
