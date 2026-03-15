# Operator Taxonomy Status

Purpose: record what's canonical about Mueller's seven daydreaming
goals, what's not, and what the project might add later.

---

## Mueller's seven (Ch. 3, §3.3)

Mueller defines these as daydreaming *goals*, not operators. They
are a fixed set in DAYDREAMER, derived from cognitive psychology
and daydream protocol analysis.

### Emotional goals (primarily regulate affect)

1. **RATIONALIZATION** — modify interpretation of failure to reduce
   negative emotion
2. **ROVING** — shift attention to positive material
3. **REVENGE** — generate imaginary retaliation scenarios

### Learning goals (primarily produce useful knowledge)

4. **REVERSAL** — generate alternative past/future where failure
   is prevented
5. **RECOVERY** — generate future scenarios for achieving a failed
   goal
6. **REHEARSAL** — generate future scenarios for an active goal
7. **REPERCUSSIONS** — explore consequences of hypothetical
   situations

## What's canonical

- Each goal activates under specific emotional conditions
- Each produces a structurally different kind of inner moment
- Mueller derives them from established psychology (defense
  mechanisms, cognitive dissonance, bolstering strategies)
- The taxonomy is well-grounded and demonstrably useful

## What's NOT canonical

Mueller does not claim this is exhaustive. He says these are the
goals he found most important and implementable. The book does not
argue "exactly seven and no more."

## What the taxonomy doesn't cover

- **Curiosity / information-seeking** — epistemic concern with its
  own logic, not reducible to the seven. (5 Pro flagged this.)
- **Empathic simulation** — imagining what someone else feels.
  Mueller's "forward other planning" does this procedurally but
  it's not a named daydreaming goal.
- **Savoring / dwelling** — staying with a positive experience.
  Mueller's system is negatively-driven; positive dwelling has no
  operator.
- **Confrontation** — directly facing the avoided thing. Implicit
  in the settled architecture but not in Mueller's original seven.
  Mueller has REVERSAL and RECOVERY but not "turn and face it."
- **Confession / self-disclosure** — revealing hidden information
  under social pressure. Related to RATIONALIZATION but
  structurally different.

## Project's current v1 cut

Three operators for the prototype:
- **rehearsal**
- **rationalization**
- **avoidance** (project's name — collapses Mueller's ROVING with
  behavioral non-engagement)

All seven are expected in provenance tags even when only three
are implemented, so generated nodes record which Mueller-type
daydreaming produced them.

## Later expansion candidates

When the v1 operators are validated and the pipeline is producing
multi-step material:

- **reversal** — strong for graph growth (exposes blocked
  dependencies and missing preparations)
- **confrontation** — the high-stakes complement to avoidance
- **repercussions** — consequence exploration, good for graph
  density
- **curiosity** — epistemic concern, needs its own activation
  logic
- **recovery** — forward-looking repair after failure

These should be added one at a time, each with its own benchmark
fixture and ablation test, not as a batch.

## The governing principle

Each operator must produce a *structurally different kind of inner
moment*. If two operators produce material that's
indistinguishable in content, tension profile, and traversal
properties, they should be merged. The test is behavioral
difference, not taxonomic completeness.
