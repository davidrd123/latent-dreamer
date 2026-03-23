# Graffito Situation Model — What A Cognitive Architecture Needs

You have the full repo context. Do NOT summarize what the documents
say. Bring outside knowledge — name specific systems, papers, results,
and mechanisms that bear on these questions.

## Context

We are building a cognitive architecture for persistent inner life
(a Mueller-style DAYDREAMER kernel in Clojure with LLM judgment at
typed call sites). The first real creative brief is Graffito, a Mark
Friedberg short film. We have a kernel brief already written
(`daydreaming/Notes/experiential-design/24-graffito-kernel-brief.md`)
that identifies 7 situation fact categories and maps the families to
story logic. The vendored source material is in
`daydreaming/vendor/graffito/`.

Our current situation model is a flat pressure map:

```
{:activation 0.55, :ripeness 0.6, :anger 0.24, :hope 0.19,
 :threat 0.47, :indices [:perception :awareness :seam ...]}
```

Goal family selection is driven by weighted sums. The kernel HAS
fact/context machinery (assert-fact, context tree, sprouting) but
benchmarks don't use it yet.

The kernel brief proposes a 7-category typed fact-space with derived
pressure. This prompt asks: what can outside knowledge tell us about
how to design that fact-space well?

## The Material

Graffito is not a generic fantasy. Key structural features:

**Three reality layers** (from Mark's working notes):
- Baseline reality: South Bronx, school, street, apartment
- The myth: Graffito legend, Scribe, the mission statement
- The magic world: space, Motherload, Elephant, the castle

These are not separate stories. They are the same conflicts
(hiding vs being seen, liability vs power, ego vs purpose)
playing out in different registers. Motherload IS Grandma
("Tony looks long into her (Grandma's) Eyes"). The castle
relates to the school.

**Sensory state as mechanism:**
Tony has ADHD/spectrum traits. His sensory overload (the "animatic
burst" of deconstructed images) is initially overwhelming but
becomes the exact capacity that powers the Magic Can. The Can's
progression is: mist -> fluid -> solid -> thing. The story's
central reversal is: "what is wrong with you is exactly what's
right with you."

**Embodied knowledge transmission:**
Monk doesn't teach Tony techniques. He teaches him to "give your
mind to your body and let it flow." Art emerges from movement, not
planning. The spinning/dancing/swaying is the mechanism, not
decoration. The Can only works when Tony stops forcing.

**The family triangle:**
- Tony: gifted, overwhelmed, observant, learning what his
  difference means
- Monk: artist, father, absent/present, charismatic, hiding,
  divided between mission and care
- Grandma: boundary, wisdom, skepticism, protection, memory.
  Her challenge ("seeing YOUR name on their wall makes YOU feel
  better?") is the story's deepest question about ego vs purpose.

**Object states:**
- The Can: inherited (Grandma -> Monk -> Tony), inert, then
  unstable, then world-making. Only works through embodied flow.
- The Sketchbook: Tony's regulation tool, where 3D overwhelm
  becomes 2D comprehension
- Elephant: comfort object -> real companion -> guide
- The Mural: becomes a portal surface (the Orange Door)

## Question 1: Appraisal structure

Our kernel brief proposes 7 fact categories (present actors,
relationship facts, role/obligation, exposure/surveillance,
artifact-state, recent events, derived appraisal summary).

How does this map to established appraisal theory?

- Scherer's SEC (stimulus evaluation checks): novelty, intrinsic
  pleasantness, goal significance, coping potential, norm
  compatibility. Which of these does the 7-category schema cover
  well? Which does it miss?

- Lazarus's primary/secondary appraisal: relevance, congruence,
  ego-involvement (primary); blame/credit, coping potential,
  future expectancy (secondary). Where does the kernel brief's
  schema map?

- OCC (Ortony/Clore/Collins) emotion types: are the kernel's
  current emotion dimensions (threat, hope, anger, grief, waiting)
  the right ones for Graffito? OCC distinguishes well-being
  emotions, attribution emotions, and attraction emotions. Which
  matter most for Tony/Monk/Grandma?

- Is there a specific appraisal framework that handles the
  "liability becomes asset" reversal — where the SAME stimulus
  property (sensory overload) shifts from negative to positive
  through reappraisal?

## Question 2: Representing layered reality

The three reality layers (baseline / myth / magic) are structurally
important. The same tensions play out in different registers.
Motherload IS Grandma. The castle IS the school.

- How do narrative AI systems represent parallel or layered
  realities? Does MINSTREL (Turner) have anything like this?
  Versu (Evans/Short)? Facade (Mateas/Stern)?

- In cognitive architectures, how do you represent that two
  situations are "the same conflict in a different register"?
  Analogical mapping (Gentner's structure-mapping)? Conceptual
  blending (Fauconnier/Turner)? Something else?

- Should the kernel represent Motherload and Grandma as the same
  entity in different layers, or as separate entities with a
  structural correspondence? What are the tradeoffs?

- Mueller's DAYDREAMER had "personal-goal" and "daydreaming-goal"
  layers. His daydreaming goals (rationalization, reversal, etc.)
  operated on the personal goals. Is there a natural third layer
  for mythic/fantasy content, or does the existing two-layer
  structure handle it if the situations carry enough fact structure?

## Question 3: Sensory state and regulation

Tony's sensory processing state (overwhelm -> regulation ->
flow -> creation) is not just color — it's the central mechanism.
The Can's magic is literally his sensory capacity channeled
through trust and rhythm.

- Are there cognitive architecture models that represent sensory
  processing state as a variable that affects cognition? Not just
  "arousal" as a scalar, but a qualitative state
  (overwhelmed / regulating / flowing / creating)?

- How does ACT-R or Soar represent the transition from "too much
  input" to "productive processing"? Is there a buffer/bottleneck
  model that maps to Tony's experience?

- The "flow state" literature (Csikszentmihalyi) describes
  challenge/skill balance. Does this translate into a computational
  variable? Has anyone built a flow-state model into a cognitive
  architecture?

- Tony's regulation strategy is embodied (movement, rhythm,
  dancing). How do embodied cognition frameworks (4E cognition:
  embodied, embedded, enacted, extended) model the relationship
  between bodily rhythm and cognitive regulation? Any computational
  implementations?

## Question 4: Object state and symbolic agency

The Can, Sketchbook, Elephant, and Mural are not props. They have
state progressions and symbolic agency:

- The Can: inert -> unstable -> world-making (only through flow)
- The Sketchbook: 3D overwhelm -> 2D comprehension (regulation)
- Elephant: comfort object -> companion -> guide
- The Mural: surface -> portal (the Orange Door)

- How do narrative AI systems represent object state progression?
  Not inventory management, but objects whose capabilities change
  with the story.

- Is there a formalism for "symbolic agency" — where an object
  acts as an externalization of an internal process? The Can IS
  Tony's sensory capacity made material. The Sketchbook IS his
  regulation strategy made visible.

- Mueller had "personal-relations" as a fact type. Should we add
  "person-object-relations" with similar structure? (Tony trusts
  the Can, the Can requires flow, Grandma gave the Can to Monk...)

## Question 5: What should a first Graffito kernel run look like?

Given all of the above, what is the minimum viable situation model
for a first Graffito kernel run that produces psychologically
interesting daydreaming — not just family selection from pressure
numbers, but actual reasoning about Tony's relationship with Monk,
the Can's state, and the overwhelm-to-flow transition?

- How many typed facts per situation is enough? 5? 15? 50?
- Should the first run try all 7 situations from the kernel brief,
  or start with a subset (e.g., just the apartment and the mural)?
- What would "psychologically interesting" output look like from a
  Mueller-style system running on this material? What should we
  expect from rationalization, reversal, and roving when they
  operate on these facts?
- Is there a danger of over-engineering the situation model before
  the membrane dynamics (promotion, anti-residue, frontier) are
  proven in a live soak? Should we prove the membrane on a simpler
  fixture first, then enrich the situation model for Graffito?
