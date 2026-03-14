# Authorial Leverage — L3 Extraction

Source: Chen, Nelson, Mateas, "Evaluating the Authorial Leverage
of Drama Management" (AIIDE 2009)

Source file: `sources/markdown/authorial-leverage/source.md`

Purpose: Extract the evaluation framework for determining whether
the L3 scheduler is worth building.

---

## What the paper actually is

This is not an architecture paper. It's an **evaluation methodology
paper**. It asks: given a drama manager, how do you know it's
providing real value to the author beyond what hand-scripted
trigger logic could achieve?

The paper proposes three criteria, applies them to DODM using
decision-tree learning to capture equivalent trigger logic, and
shows that DODM does provide measurable authorial leverage.

---

## The three criteria

### 1. Script-and-trigger complexity

If you converted the DM's policy into equivalent hand-authored
if/then trigger logic, how complex would that logic be?

**Method:** Run the DM to generate thousands of story traces.
Train a decision tree on (partial-story, dm-action) pairs.
The decision tree IS the script-and-trigger equivalent. Its
size measures the authoring burden.

**Finding:** In EMPath (a small 25-room Zelda-like game with only
10 plot points and 33 DM actions), the best-performing decision
tree had **70 nodes**. Even in this tiny domain, the equivalent
hand-authored trigger logic is substantial. A 17-node tree
(manageable to hand-author) performed barely better than no
drama management at all.

### 2. Ease of policy change

When the story world changes (new plot points, new locations),
how much authoring work is needed to update the policy?

**Method:** Create three variants of EMPath with increasing
complexity:

| Variant | Plot points | DM actions | Quests | Map size |
|---------|------------|------------|--------|----------|
| empath-small | 10 | 33 | 3 | 25 |
| empath-med | 14 | 47 | 5 | 64 |
| empath-large | 18 | 62 | 6 | 64 |

**Finding:** Decision tree sizes grow **significantly** with each
expansion. Going from small to large requires hundreds of edits
to hand-authored trigger logic. With DODM, the author just adds
new plot point and DM action annotations — the optimizer handles
the policy change automatically.

Key insight: "To incorporate the logic for the new subquests,
all the author has to do is provide the DM with the new plot
points and DM actions." The complexity is absorbed by the
optimizer, not the author.

### 3. Variability of experiences

Does the DM produce diverse experiences, not just one "optimal"
path?

**Finding:** The best-fitting decision tree (137 nodes) still
produced over **6,000 unique stories** out of 50,000 simulated
runs. Smaller trees (more generalization) produced even more
unique stories (14,000+) but with worse quality. The DM-equivalent
tree produces **simultaneously higher quality and wide variety**.

---

## What this means for Experiment 1

### The evaluation frame

Authorial leverage gives us the right question for the L3
scheduler experiment. Not just "does it produce better output?"
but three separable sub-questions:

**1. Complexity question:** What's the equivalent hand-authored
logic for the scheduler's policy?

- If the scheduler's behavior can be replicated by a few simple
  rules (weighted random, highest-tension-first, round-robin),
  then it's not providing leverage. It's just dressed-up heuristics.
- If reproducing the scheduler's behavior requires complex
  state-dependent logic, then the scheduler is earning its keep.

**2. Change resilience question:** When you modify the Graffito
graph (add nodes, change situations, adjust tension values), does
the scheduler automatically adapt, or does the policy need manual
rework?

- If adding a situation to the graph requires re-tuning all the
  diagnostic thresholds, the scheduler is fragile.
- If the scheduler automatically incorporates new material through
  its feature weights and scoring, it's providing real leverage.

**3. Variety question:** Does the scheduler produce diverse runs,
or does it converge on one "optimal" traversal?

- For conducted performance, variety is essential — multiple
  performances should feel different.
- The scheduler should produce noticeably different traversals
  with the same graph + different conductor biases.

### Concrete application to Experiment 1

Doc 11's Experiment 1 currently measures:

- Revisit gap distribution
- Overexposure rate
- Purposeless loops
- Event approach before commit
- Human judgment of arc legibility

Add from authorial leverage:

- **Equivalent complexity:** How many hand-authored rules would
  reproduce the scheduler's top-20 traversal decisions? If the
  answer is "a few edge-weight tweaks," then the scheduler isn't
  worth its complexity.

- **Graph resilience:** Add 3 nodes to the Graffito graph. Does
  the scheduler automatically incorporate them with sensible
  timing, or does it need manual adjustment?

- **Run diversity:** Generate 20 traversals with the same graph.
  How many unique node orderings? How many distinct arc shapes?
  Compare against weighted-random baseline.

---

## The deeper lesson

The paper's real contribution is methodological: **don't just
evaluate whether the output is good; evaluate whether the tool
gives the author more power than the alternative.**

For our project, the "author" is two people:

1. **Graph author** (you + Mark) — who benefits if the scheduler
   reduces the need for hand-sequencing
2. **Conductor** (live performer) — who benefits if the scheduler
   responds to simple control inputs with complex, legible
   traversal behavior

The scheduler provides leverage if either of these roles gets
more from the system than they put in.

---

## What to take for Experiment 1

1. **The three evaluation criteria** as explicit measures in the
   experiment design.

2. **The "equivalent trigger logic" thought experiment** — after
   running the scheduler, ask: could I have gotten the same result
   with simpler logic? If yes, simplify. If no, the scheduler is
   earning its keep.

3. **Variety-under-quality as a metric** — not just "are runs
   good?" but "are runs good AND different?"

4. **Change resilience as a secondary test** — add material to
   the graph and see if the scheduler adapts gracefully.

What NOT to take:

- The decision-tree induction methodology (overkill for our
  scale — we can evaluate complexity by inspection)
- The specific EMPath domain
- Any expectation of statistical rigor at this stage (we're
  looking for directional signal, not p-values)

---

## What this paper adds to the lineage

This paper is the missing bridge between "interesting scheduler
architecture" and "worth building in practice."

- **Facade** asks: how should selection work?
- **DODM** asks: how should scoring be expressed?
- **Authorial Leverage** asks: when does this machinery actually
  buy the author something that simpler scripting would not?

That makes this paper downstream of the first two. It should
shape the experiment rubric, not the runtime ontology.

---

## Minimal evaluation contract implied by Authorial Leverage

If we take this paper seriously, Experiment 1 cannot stop at
"the runs felt good." It needs an explicit leverage scorecard.

### 1. Equivalent-policy complexity

For a given traversal policy, ask:

`Could I have replaced this scheduler with a small set of
hand-authored sequencing rules over the same graph?`

For our scale, we do not need decision-tree induction. But we do
need a disciplined proxy, for example:

- how many distinct state tests are actually used?
- how many override cases are needed to explain decisions?
- how often does the policy depend on interactions between
  recency, motivation, and arc state that would be painful to
  hand-script?

If the answer stays small, L3 is not earning its keep.

### 2. Policy-change resilience

When the graph changes, ask:

`Does the same scheduler architecture absorb the new material
with local annotation edits, or does its policy need to be
hand-retuned everywhere?`

This is the practical DODM promise. For Daydream, the relevant
change cases are:

- add 2-3 new nodes to an existing situation
- add a new situation cluster
- change one event's motivating prerequisites
- retune target arc bias

If the scheduler adapts with only local metadata changes, that is
real leverage.

### 3. Variety-under-control

The paper's third criterion matters a lot here because the whole
point of conducted performance is not just quality, but
repeatable variation.

The scheduler should support:

- multiple distinct runs over the same graph
- different traversals under different conductor bias
- variation without collapsing into incoherence

The important measure is not raw randomness. It is controlled
variety under the same authorial apparatus.

---

## What not to cargo-cult from this paper

### 1. Do not mistake evaluation for implementation guidance

This paper does not tell us how to build the scheduler. It tells
us how to judge whether the scheduler deserves to exist.

### 2. Do not overinvest in decision-tree equivalence

The decision-tree method is useful as a proof in a paper. It is
not the main thing we need to build right now. A lighter
equivalent-logic inspection is enough for v0.

### 3. Do not reduce leverage to graph-author convenience only

In our system there are at least two authorial roles:

- graph author / curator
- live conductor

The scheduler has leverage if it helps either role do something
that would otherwise require much more explicit scripting or
micromanagement.

### 4. Do not count variety without quality

Random traversal also yields variety. The leverage question is
whether the system yields **quality plus variety**, not one or
the other alone.

---

## Experiment 1 scorecard after reading Authorial Leverage

The experiment should now have two layers of evaluation.

### Runtime quality layer

- trajectory fit
- overexposure / exhaustion
- event approach legibility
- recall quality
- subjective coherence of watched runs

### Leverage layer

- equivalent policy complexity
- resilience under graph edits
- controlled run diversity
- conductor expressivity

### Conductor expressivity

This is the one metric the paper does not name explicitly but is
important for us. Ask:

`Does a small change in conductor input produce a meaningful but
legible change in traversal policy?`

If not, the performer is not getting leverage either.

### Failure modes this paper helps expose

- a scheduler that looks smart but is really a small bag of
  heuristics
- a scheduler that only works on one frozen graph
- a scheduler that produces the same run every time
- a scheduler that requires so much annotation that it erases its
  own benefit
