# L1 Critic Test Synthesis

Purpose: translate the `L1` source cluster into a concrete critic-test
direction aligned with `11-settled-architecture.md`.

Sources folded here:

- `08-sentient-sketchbook-extraction.md`
- `09-minstrel-extraction.md`

---

## Core Claim

`L1` is a narrow authoring critic and proposal-repair layer.

It is not:

- a free-running world generator
- a replacement for authored graph construction
- a character cognition system
- a drama manager
- a full planning engine

Its job is narrower:

- inspect authored graph material for computable structural deficiencies
- propose local repairs or expansions
- evaluate those proposals visibly
- reject weak proposals cheaply
- mutate or adapt rejected proposals rather than regenerate blindly

In the settled architecture, `L1` is research-stage support for the
authored graph. The graph remains the interface between lanes. `L1`
helps improve that interface, but does not become the world itself.

---

## What the Sources Actually Support

The `L1` cluster supports a very specific workflow:

### From Sentient Sketchbook

- continuous visible evaluation
- proposal generation under explicit constraints
- novelty as a diversity term, not a free-standing objective
- cheap rejection of low-value candidates

### From MINSTREL

- dead-end recovery by transforming an existing candidate
- recall-adapt over generate-from-scratch
- mutation as guided repair

Together they support:

- critic-guided proposal generation
- iterative repair over brute-force search
- visible reasons for acceptance or rejection

They do **not** support:

- unconstrained divergent creativity as the core mechanism
- semantic free play detached from graph deficiencies
- runtime character cognition in `L1`

---

## What `L1` Should Actually Do

The first `L1` experiment should take an authored graph and answer:

1. what local structural deficiency is present?
2. what small repair or expansion could address it?
3. why is that proposal better than doing nothing?
4. if it fails, how should it be repaired or mutated?

That means `L1` should operate on deficiency classes, not on a vague
"make this more interesting" prompt.

---

## Recommended Deficiency Classes

The first critic test should stay narrow and computable.

Useful first-pass deficiency types:

- dangling setup: a seeded element has no plausible payoff path
- unsupported transition: edge or beat transition lacks enough bridge
- under-motivated node: event or node exists without enough pressure
  attachment
- pressure dead-end: active pressure has no authored continuation
- duplicate function: two nearby nodes do the same structural work
- isolated cluster: locally interesting material does not connect to the
  main graph trajectory
- weak escalation: pressure rises are too flat or repetitive
- premature closure: a tension resolves before earning it

These are narrow enough to score and broad enough to matter.

---

## Proposal Unit

`L1` should propose **small graph edits**, not whole rewritten
structures.

The right proposal unit is something like:

- add node
- add edge
- split node
- retag node
- insert bridge node
- add payoff hook
- add alternate continuation
- defer or reroute a resolution

This keeps `L1` critic work local and inspectable.

---

## Proposal Record

Each proposal should have an explicit record.

Minimum fields:

- proposal id
- target deficiency id
- intended effect
- edit type
- touched graph refs
- rationale
- evaluation breakdown
- disposition: accepted | rejected | mutate

This is the right level of formality for the first critic test. It
keeps the loop inspectable without dragging in heavy infrastructure.

---

## The Critic Test Loop

The first useful `L1` loop is:

1. detect a local deficiency
2. generate a small set of candidate repairs
3. score each candidate against explicit criteria
4. reject weak candidates immediately
5. mutate or adapt one rejected-near-miss if nothing passes
6. surface the surviving proposal with explanation

This is the main lesson from the source pair: evaluation is continuous,
and repair is often better than restart.

---

## Evaluation Criteria

The critic needs visible, boring criteria before it needs cleverness.

First-pass criteria:

- deficiency coverage: does this actually address the detected problem?
- local coherence: does it fit adjacent graph material?
- pressure relevance: does it strengthen or clarify an active pressure?
- cost: how invasive is the edit?
- novelty or diversity: is it meaningfully different from existing
  local patterns?
- future utility: does it open usable continuation space?

Important constraint:

Novelty must stay subordinate. It is a tiebreaker or diversity term
among deficiency-grounded proposals, not an equal independent goal.

---

## Cheap Rejection Rules

The first experiment should bias toward fast rejection.

Immediate reject conditions:

- does not address the target deficiency
- duplicates a nearby existing function
- creates a contradiction with current authored constraints
- requires too many coupled edits for a local repair pass
- opens no new continuation space
- improves novelty only by weakening coherence

This is where Sentient Sketchbook matters most. The critic earns its
keep by discarding weak ideas cheaply.

---

## Mutation and Repair

When a candidate is close but fails, do not start over immediately.

The MINSTREL-style move is:

- identify why the candidate failed
- transform one structural aspect
- rescore the transformed proposal

Useful mutation operators:

- change target node
- change motivating pressure
- swap bridge type
- defer payoff timing
- reduce scope of the edit
- repurpose an existing node instead of adding a new one

This is a repair loop, not open-ended creative expansion.

---

## What the Critic Should See

`L1` should read a limited graph projection, not every latent state in
the system.

Useful critic inputs:

- local graph neighborhood around the deficiency
- active pressure tags already projected from `L2`
- authored constraints and node types
- recent `L3` traversal trouble signals, if available

That is enough to evaluate repair proposals without collapsing lanes.

---

## Minimal `L1 -> Graph` Write Surface

`L1` should write proposals and annotations, not silently rewrite the
graph.

The first write surface should include:

- deficiency annotations
- candidate proposal records
- evaluation breakdowns
- accepted proposal recommendation
- optional mutation lineage

Whether accepted proposals are auto-applied should remain separate from
the critic test itself.

---

## Non-Goals for the Critic Test

Do not let `L1` expand into:

- full story generation
- holistic graph redesign in one pass
- unconstrained novelty search
- runtime character decision-making
- `L3` scheduling
- final authorship replacement

The critic test should prove that narrow structural feedback is useful.
Nothing more.

---

## Practical Experiment Shape

The first `L1` critic test can be small.

### Input

- one authored graph region
- one detected deficiency
- optional pressure tags from current `L2` projection

### Output

- 2-5 candidate local repairs
- visible score breakdown
- one chosen recommendation
- one mutated fallback if all first-pass candidates fail

### Comparison Question

The real question is:

- does critic-guided local repair produce better graph improvements than
  ad hoc human patching or unconstrained generative suggestion?

That is the right test, not "can `L1` invent the world?"

---

## Recommended Build Order

### Step 1. Fix the deficiency vocabulary

Choose a small, computable set of structural deficiencies and make sure
they can be detected consistently.

### Step 2. Define the proposal grammar

Lock the set of allowed local edit types before tuning generation.

### Step 3. Build the visible critic

Make scoring explicit and inspectable before making it sophisticated.

### Step 4. Add cheap rejection

Bias toward discarding obviously weak candidates fast.

### Step 5. Add mutation or repair

Only after rejection is working should the system try adapted retries.

### Step 6. Evaluate whether the critic pays rent

The critic wins if it makes authored graph revision faster, clearer, and
more structurally grounded.

---

## What the Critic Test Should Prove

If `L1` is worth keeping, three things should become true:

1. local graph weaknesses can be identified in a stable, computable way
2. small repair proposals are better than unconstrained suggestions
3. visible evaluation and mutation outperform blind regenerate-until-
   something-looks-good behavior

That is enough to justify `L1` as a real lane.
