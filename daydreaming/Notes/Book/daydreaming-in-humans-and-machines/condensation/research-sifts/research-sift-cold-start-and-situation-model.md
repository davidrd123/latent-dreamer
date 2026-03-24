# Sift: Prompt 19 — Cold Start, Admission, Benchmark, Situation Model

Two replies (v1, v2). Both substantive, high convergence. This sift
extracts actionable findings and admission status per item.

## 1. Cold-start comparison — settled findings

| Architecture | How it handles new learned structures | Our membrane analog? |
|---|---|---|
| ACT-R | No gate. Activation economics (decay, competition, threshold). New chunks immediately retrievable. | No |
| Soar | Immediate admission. Later repair via EBBS and forgetting. Numeric-preference chunking withheld until enough experience (narrow exception). | Weak partial |
| CLARION | Implicit-to-explicit extraction with revision/deletion. Closest mainstream cousin. Still no probationary tier. | Partial |
| ICARUS | One-success macro-skill learning, immediate admission. | No |
| **Prodigy** | Evidence-gated retention. Kept statistics on learned rules, retained only those that improved performance. **Closest outside precedent.** | Yes, partial |

**Settled:** Our membrane is not a literature rename. It stages
*trust*, not *derivation/compilation*. This is a genuine response to
a noisier regime (self-generated symbolic residue in a persistent
system). Confidence: 80-85% across both replies.

**Settled:** The Soar utility problem is a real warning. A long-term
study found performance improved after ~90% of learned rules were
deleted. Our anti-sludge anxiety is warranted.

## 2. Staged admission design — settled and provisional

### Settled

- `:trace / :provisional / :durable` ladder: **keep**. Right
  abstraction. (80-85%)
- Cross-family-use-success as **primary** positive signal: **keep**.
  Measures exportability / transfer. Correct for groove prevention.
- Threshold 2: **keep for real system**. Do not lower yet. Fix the
  benchmark first.
- `:same-family-loop` flag: **keep**. No canonical analog in major
  architectures, but related to Soar forgetting and utility-problem
  filtering. Our failure mode is more explicit, so our mechanism
  should be too.
- Demotion (`:durable` -> `:provisional`): **keep**. Unusual and
  correct. Episodes are speculative, not proofs. Requires hysteresis:
  re-promotion must require evidence *after* the demotion event, not
  lifetime totals. Code already does this via
  `later-qualifying-promotion-evidence-count`.
- Evaluator: veto/triage only, not decision authority. Matches Soar RL
  lesson (numeric preferences influence selection only when other
  knowledge is insufficient).
- Roving default non-promotable: **correct for now**.

### Provisional — act on after fixture passes

- **Broaden positive evidence** beyond cross-family-use-success alone.
  Both replies agree this is too narrow as the *only* positive witness.
  Add:
  1. World-confirmed downstream success (episode contributes to a
     branch whose consequences align with later facts or resolved
     concerns)
  2. Same-family success with external corroboration (demonstrably
     changed later appraisal, not just looped self-soothing)
  Status: provisional. Implement after Assay A/B, not before.

- **Roving `regulation-anchor` lane.** Both replies say "never
  promotable" is too rigid long-term. A subset of roving episodes
  that repeatedly reduce pressure across families without backfire
  should eventually have a distinct durable path. Not the same lane
  as payload-exemplar.
  Status: provisional. Park until data demands it.

## 3. Membrane fixture benchmark — settled and provisional

### Settled

- The 2-situation fixture is the right test. Much stronger than
  Puppet Knows. Converts promotion from theoretical possibility into
  structurally forced opportunity.
- Pattern name: "scaffolded transfer benchmark" or "forced cold-start
  transfer test" (v2). "Asymmetric authored coverage" is the local
  name.
- Wray/Lebiere framework: evaluate taskability, incrementality, and
  knowledge utilization, not just task performance (v2 cite).
- Staged observables (Level 1-4) are correct. Final task score alone
  would be useless.
- 20 cycles realistic for Level 1/2. Not enough for stable promotion
  stats — need many seeds or 50-100 cycles for Level 3.
- Treat it as **Assay A in a benchmark ladder**, not the final verdict.

### Provisional — add to fixture spec

- **Exclusion-reason logging.** When a dynamic source loses, log WHY:
  threshold miss, structural mismatch, same-family-loop block, stale
  block, contradicted block, authored outrank, recent-episode gate.
  Without this, failures are undiagnosable. (v2, strong)
  Status: add to fixture spec before codex builds it.

- **Additional metrics for later assays:**
  - Dynamic share of candidate races over time
  - Dynamic win rate conditional on eligibility (authored vs durable
    vs provisional)
  - Pending-use resolution rate and average time-to-resolution
  - Promotion precision (promoted items later contribute again)
  - Frontier utilization (opened rules actually used)
  - Flag incidence by cause (stale, loop, contradicted, backfired)
  Status: provisional. Add to Assay B spec, not Assay A.

- **Benchmark ladder** (v1):
  - Assay A: 20-cycle smoke test (dynamic visibility and selection)
  - Assay B: 100-cycle soak (promotion/demotion/frontier movement)
  - Assay C: Negative controls (incompatible goals, false bridge,
    complete-authored baseline)
  - Assay D: Ablation (no membrane vs membrane vs forced provisional
    exploration vs broader evidence types)
  Status: provisional. Assay A is the immediate target.

- **Most important missing comparison** (v1):
  1. Immediate durable (bad old world)
  2. Current membrane
  3. Current membrane + forced provisional exploration
  4. Current membrane + broader evidence types
  Without this matrix, cannot tell whether failures come from the
  membrane or from starvation.
  Status: provisional. Assay D territory.

### Warnings

- v1: the fixture is a membrane smoke test, not a general benchmark.
  Tests near-transfer (shared failed-goal identity), not oblique
  transfer. Good first assay, weak final assay.
- v1: bundles too many things into one pass condition (cue overlap,
  structural compatibility, ranking, use, vindication, promotion,
  frontier). When it fails, diagnosis will be muddy.
- v1: stretch criteria (Level 3/4) are not honest enough for 20
  cycles. Treat Level 1/2 as the true target.

## 4. Situation model — settled direction

### Settled

- The flat pressure map is a **derived appraisal cache with the world
  model amputated** (v1 phrasing). Not a situation model.
- Keep pressure numbers as **derived scheduler state**, not primary
  representation.
- **Three-layer representation** (both replies converge):
  1. Canonical fact-space (entities, relationships, events, beliefs,
     commitments, hidden facts)
  2. Situation/appraisal layer (unresolved concerns anchored to facts,
     with appraisal fields per character)
  3. Derived scheduler pressures (activation, threat, hope, etc.)
- v1 adds a fourth layer (appraisal cache per character between
  situation layer and scheduler pressures). Same structure, finer
  grain.

### Key outside systems

| System | What it teaches | Use for |
|---|---|---|
| **Versu** | Roles, desires, social practices, role evaluations with *explained judgments* | Socially rich situations, relationship modeling |
| **Facade** | Beat gating, preconditions, tension effects | Scene-scale progression (warning: 2500 authored behaviors for 20 min) |
| **MINSTREL** | Transformable story graphs (state, act, goal, belief nodes) | Creative operator design (reversal, mutation), not runtime situation model |
| **EMA/OCC/FAtiMA** | Appraisal dimensions, coping feedback loops | What the appraisal layer must contain |
| **GOLEM** | Characters, relationships, events, narrative ontology | Archival/interchange (overkill for runtime v1) |

### Appraisal theory guidance

- **Scherer SEC:** novelty, goal relevance, coping potential,
  norm compatibility. The 7-category kernel brief covers relevance
  and exposure well. Misses: novelty/expectedness, coping potential,
  norm/fairness explicitly.
- **Lazarus:** primary appraisal (relevance, congruence,
  ego-involvement) + secondary (blame/credit, coping potential,
  future expectancy). Kernel brief covers relevance and some blame.
  Missing: explicit coping potential and future expectancy.
- **EMA dynamics:** appraisal tracks changes in person-environment
  relationship over time, not one-shot labels. Coping feeds back
  into later appraisal. Situations should carry coping state.

### Concrete schema (v1)

~8-15 typed facts per situation, 2-4 belief asymmetries, 1-3 pending
events, 4-7 derived appraisal scalars. That is enough.

v1 gives explicit Clojure-shaped example:

```
{:situation/id :seeing-through
 :participants [:tony :grandma]
 :roles {:child :tony :guardian :grandma}
 :salient-facts [...]
 :private-beliefs {:tony [...] :grandma [...] :monk [...]}
 :relationship-state [...]
 :pending-events [...]
 :counterfactual-pivots [...]
 :active-practices [...]
 :appraisal-cache {:tony {...} :grandma {...}}
 :derived-pressure {:activation 0.61 :threat 0.44 :hope 0.52
                    :shame 0.68 :flow-pull 0.77}}
```

## 5. Graffito-specific findings

### Both replies agree

- Tony's neurodivergence should be a **causal cluster**, not a tag.
  High sensory load -> overwhelm; drawing/flow -> regulation;
  misunderstanding -> blame appraisals; Can/Monk contact -> coping
  potential change.
- Monk should not be "absent father" as lore text. He should be a
  source of both skill transmission and instability.
- Grandma should hold explained role evaluations and opposed goals
  (protect Tony vs preserve lineage vs avoid repeating Monk's damage).
- Cops should be surveillance geometry (who can see whom, where, with
  what consequence), not a generic antagonist faction.

### v1's 4-situation breakdown (actionable)

**:seeing-through** — "Is Tony broken, gifted, or both?"
- Carry: Tony's self-model, coping facts, Grandma's counter-belief,
  lineage evidence, judgment/exposure facts, Grandma's value conflict
- Families: rationalization, rehearsal, roving

**:mission** — "Is the Can a calling, burden, or trap?"
- Carry: lineage chain, object affordance (Can requires flow),
  Monk's teaching, Tony's mastery desire, Grandma's fear of cost
- Families: rehearsal, recovery, repercussions

**:edge** — "Can art happen without repeating Monk's loss?"
- Carry: event history (arrest, absence), surveillance model,
  Tony's mixed model of Monk, risk factors, causal pivots
- Families: reversal, repercussions

**:ring** — "Is the mark for ego, belonging, healing, or purpose?"
- Carry: Grandma/Motherload rebuke, public-name facts, lineage
  semantics, norm conflict (expression vs inscription)
- Families: rationalization, reversal, repercussions

### v1's 8 required fact types

1. Belief asymmetries
2. Ambivalent relationships
3. Causal wound history
4. Embodied skill facts
5. Symbolic object lineage
6. Dual-identity / cross-world correspondence
7. Surveillance geometry
8. Norm/value conflicts

### v2's runtime structures

- Character traits/regulation (sensory threshold, flow
  susceptibility, creative compulsion, protective authority)
- Role evaluations with explanations (Versu-style)
- Epistemic asymmetries (who knows what)
- Social practices (recurring frames, not one-off scenes)
- Active concerns (control/overload, belonging/abandonment,
  calling/safety, ego/purpose)
- Event affordances (near miss, grounding, flow demonstration,
  Can activation, police interruption)

### Next benchmark (v1 proposal)

After the membrane fixture passes: a **4-situation Graffito miniworld**
with two true bridges, one false bridge, and one hard negative. Tests
membrane AND richer situation model together.

## 6. What to do next

### Immediate (before handing fixture to codex)

1. Add exclusion-reason logging to the membrane fixture spec
2. Explicitly label the fixture as Assay A in a benchmark ladder
3. Confirm Level 1/2 as the true 20-cycle target; Level 3/4 are
   later assays

### After fixture passes Level 1/2

4. Broaden positive evidence (world-confirmed success,
   same-family-with-corroboration)
5. Run Assay B (100 cycles, multiple seeds)
6. Design Assay C (negative controls) and D (ablation)

### For Graffito situation model

7. Use v1's 4-situation breakdown and 8 fact types as the design
   input for the first Graffito kernel run
8. Focus prompt 20 on the gaps these replies didn't fully cover:
   layered reality (baseline/myth/magic), sensory-state-as-mechanism,
   object state progression

### Parked

- Roving `regulation-anchor` lane (wait for data)
- GOLEM ontology for archival (overkill for v1)
- Facade-style beat gating (relevant but heavy authoring cost)
