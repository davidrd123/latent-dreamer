# Project Chronology — Round 3

Built from git history. 234 commits, March 12-23, 2026.

---

## March 12: Experiential design + kernel from scratch

The repo opens with experiential design docs — what the audience
should feel, the Dreamer/Director dual-layer concept, creative
briefs, the dream sequence benchmark. Then DAYDREAMER sidecar
planning, and the Clojure kernel goes from zero to working in one
day: context tree, goals, control loop, episodic memory, trace
export, orchestrated runner, Puppet Knows benchmark.

Wave 2 immediately: REVERSAL (context tree surgery, counterfactual
sprouting, weak-leaf retraction), ROVING (pleasant episode seed,
reminding cascade), family activation rules, semi-unscripted
benchmark, side-by-side trace comparison.

Then: knowledge-coupling research, branch-fact adapter, Arctic
benchmark, RATIONALIZATION (emotional diversion, stored reframes),
Stalker Zone benchmark, autonomous Puppet Knows, cognitive trace
visualizer.

By end of day: three families working, four benchmarks, a
cognitive visualizer, autonomous mode. All hand-coded.

## March 13: Mueller assessment + pressure-engine reframe begins

Mueller coverage assessment. Autonomous emotional diversion and
ROVING branch payloads. Then: docs 14-21, Graffito vertical slice
spec, Director spec, narration layer spec.

Late March 13 / March 14: the pressure-engine reframe. 37 docs.
Compares kernel against ChatGPT clean-room architecture and 5 Pro
review. Settles L1/L2/L3 framework. "Use Mueller's control geometry
but not his label set." Pressure as the driving abstraction.
Operators as pluggable exploration moves.

## March 14: L3 validation + generation pipeline

Graffito L3 pilot (complete — feature arm beats baseline). City
Routes substrate and full experiment (arms A/B/C, robustness
sweep, conductor tests). 5 Pro round-2 and round-3 responses.

Then the authoring-time generation reframe: L1 is orchestration
over L2-style machinery, not autonomous world-building. L2 schema
from 5 Pro (CausalSlice, AppraisalFrame, EmotionVector). Worked
trace (Kai's unopened letter). Generation prototype spec.

Generation prototype built and validated across three benchmarks.
Broader application surface note. Illustrated explainer.

## March 15: Supply pilot + bridge tests + evaluation ladder

Generation pipeline: batch generation, admission, keeper bank,
boundary detection, prompt compiler. 5 Pro Q5-Q14 replies.
Operating model and lesson doc. Curation packet.

Project control plane established: dashboard, sprint, canonical
map, executor-verifier protocol.

Supply pilot passes (Tessa clean, Kai narrow). Bridge tests pass
(generated material survives traversal). Patch tests for both
fixtures.

Current synthesis established: evaluation ladder (scheduler
dynamics → director changes trajectory → stage output feels like
anything → 12-cycle dreamlike → different briefs different dreams).

Inner-life trace viewers: watch_trace.py, listen_trace.py (Lyria
RT audio from traversal traces).

## March 16: Supply closeout + RuntimeThoughtBeatV1 identified

Supply_v1 closed. Provocation seam proven (state/event provocations
load-bearing, pure salience provocations not).

The deepest gap identified: runtime content, not just dynamics.
RuntimeThoughtBeatV1 as the primary target. Mueller generates the
specific thought inside the cognitive loop. Our kernel fires the
operator and selects a pre-authored node. The missing object is
the inner-life beat.

## March 19-20: The book + condensation + plan of attack

Plan of attack: "Prosthetic Inner Life" as the unifying frame.
Three threads converge: thought beats, provocations, generation
pipeline are one loop.

Then: back to the actual book. Image-reviewed chapter passes.
First-pass Mueller condensation inventory. 19 mechanism cards.
Cross-cut summary. Chain traces (planning/analogy, reminding/
serendipity). Rule connection graph spec.

Key correction: the project was building from Mueller's domain
application (Chapters 3, 6) rather than his core engine
(Chapters 4, 5 — learning through daydreaming, everyday
creativity, serendipity, mutation).

Kernel rule schema bridge note: RuleV1 with three-layer split
(schema / denotation / executor).

## March 21: Rule engine + writeback proof + deep build begins

Architectural framing doc. 5 Pro research replies received
(architecture, visioning, implementation, prompt-set). Denotational
contract added. Migration analysis of goal_families.clj.

Then: code. Fact query extraction. RuleV1 slices for all three
families. Antecedent matcher. Connection graph derivation. Runtime
thought feedback loop built and wired into Puppet Knows.

Gate 2 passes: writeback changes the trace. Cycles 4-12 diverge
from baseline. Different models produce different downstream
retrieval/selection.

Then: graphable trigger/activation pairs. Cross-family bridges.
Planning request rules. Plan dispatch through goal_families.
Rule provenance threading. Graph query helpers. Intervention
sensitivity. Bridge path queries.

Conductor extraction: general session orchestrator.

## March 22: Memory ecology + executor boundary + reviews

Five rounds of code review from 5 Pro and 5 Thinking. Findings:
groove risks, executor seam, verified paths, promotion design,
episode loop risks.

Mueller source code audit: discovered six admission disciplines
in the original Lisp that we'd collapsed. Mueller-to-kernel
mapping document.

Memory ecology built: admission tiers, cue zones, anti-residue
flags, episode-use attribution, evidence-driven promotion,
rule accessibility frontier.

Executor boundary: execute-rule in rules.clj, declarative typed
effects, builtin handlers, effect schema validation. All three
families routed through.

Benchmark re-entry: 12-cycle regression clean, 50-cycle soak
partial. Membrane holds but benchmark under-exercises it.

Membrane assays: Assay A (dynamic entry + loop flag), Assay B
(live promotion → durable → frontier open).

## March 23: Graffito microfixtures + character state

First Graffito kernel slice: Grandma's apartment + mural crisis
with typed facts. Rationalization finds reframes, reversal finds
counterfactuals, retrieval connects across situations through
shared typed cues.

Second slice: persistent Tony character state. Sensory load,
entrainment, felt agency, perceived control. Same-situation
reappraisal: apartment support updates character state, mural
situation's appraisal shifts from threat to challenge. The
"what's wrong with you is what's right with you" arc working
mechanistically.

5 Pro Graffito situation model reviews: sensorimotor regulation
as 8th category, cross-layer correspondence (Grandma ≠
Motherload), appraisal shift mechanism through existing families.

---

## The arc in one paragraph

Round 3 started by building the traversal instrument and proving
it works (L3 validation, generation pipeline, supply pilot).
Then it zoomed out to settle the architecture (pressure-engine
reframe, L1/L2/L3 framework). Then it went back to Mueller's
actual book and source code and found the project had been
building from a low-resolution picture. The condensation of
all 19 mechanisms corrected that. The rest of the round has
been rebuilding the kernel substrate at full Mueller resolution
— rules-as-data, memory membrane, executor boundary — plus the
extensions Mueller never needed for persistent accumulation.
The Graffito microfixtures are the first step toward running
this substrate on psychologically rich material.

---

## By the numbers

- 234 commits in 12 days
- Kernel: ~40% Mueller behavioral coverage
- Rule engine: ~25% of kernel behavior migrated to graphable rules
- Tests: 241 tests, 1242 assertions (as of latest)
- 19 mechanism cards condensed from Mueller's book
- 5 rounds of external code review
- 4 benchmarks (Puppet Knows, Arctic, Stalker Zone, membrane fixture)
- 2 Graffito microfixtures with typed psychological state
- 1 proven feedback loop (writeback changes traces)
