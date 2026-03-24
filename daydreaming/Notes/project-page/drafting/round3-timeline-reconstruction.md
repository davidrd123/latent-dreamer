# Round 3 Timeline Reconstruction

Date: 2026-03-23

Purpose: reconstruct the actual arc of `latent-dreamer` from repo
evidence, not from compressed memory.

This note is for project-page work. It separates:

- prior hackathon rounds from this repo
- Round 3 internal phases from each other
- architecture vocabulary (`L1/L2/L3`) from proof vocabulary
  (`mechanism integrity / behavioral dynamics / meaningful domain adequacy`)

---

## Boundary first

The Python / Scope / APC Mini / Lyria instrument work is **not**
an early phase of this repo. That belongs to prior hackathon rounds
and lives primarily in `scope-drd`.

This repo, `latent-dreamer`, is **Round 3**.

So the page arc should distinguish:

1. **Earlier hackathon rounds** — the stage / performance surface
2. **Round 3 early** — kernel bootstrap, benchmarks, early L2/L3 bridge
3. **Round 3 middle** — pressure-engine reframe, generation/traversal framing
4. **Round 3 late** — Mueller condensation, rule engine, membrane,
   executor boundary, Graffito mechanistic slices

---

## Evidence sources

Primary sources used here:

- `git log --reverse`
- `daydreaming/Notes/current-sprint.md`
- `daydreaming/Notes/dashboard.md`
- `daydreaming/Notes/DeepResearch/2026-03-13/pressure-engine-reframe/`
- `daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/`

Current verified repo state while writing this note:

- `bb check` passes
- `247 tests`
- `1311 assertions`
- `0 failures`
- `0 errors`

---

## The actual Round 3 arc

### Before Round 3: the stage already existed

This is outside `latent-dreamer`.

Earlier hackathon rounds built the live instrument surface:

- Scope real-time video
- APC Mini control surface
- Lyria audio coupling
- Python bridge / performance stack

This matters because Round 3 is not "build the instrument from
scratch." It is "build the mind that can eventually feed the
instrument."

### Phase 1: kernel bootstrap and early runnable cognition

**Dates:** 2026-03-12 to 2026-03-13

Key evidence from early commits:

- `befb405` Initial commit: design exploration + Conducted Daydreaming architecture
- `f7804ee` Add Clojure kernel: wave 1 implementation with context, goals, control, episodic memory, and trace
- `64cd2ac` Add Puppet Knows benchmark: end-to-end trace from Clojure kernel
- `118c262` Add wave 2 REVERSAL primitive and wire into benchmark
- `350d133` Add ROVING goal family
- `0209979` Add RATIONALIZATION family and Stalker Zone benchmark
- `b9b946d` Add standalone cognitive trace visualizer
- `16e0e66` Add docs 14-21, Graffito vertical slice, director, and narration layer spec

What this phase really was:

- resurrect DAYDREAMER in Clojure quickly
- get a runnable kernel loop
- get end-to-end benchmarks
- add the first family repertoire
- make traces visible
- start connecting L2 inner life to L3 presentation/narration

This was not yet "the membrane / rules / condensation phase."
It was the first working kernel phase.

### Phase 2: the pressure-engine reframe and the generation/traversal turn

**Dates:** 2026-03-14 to 2026-03-16

Key evidence:

- `60b1fea` Add pressure engine reframe, visual world-building pipeline, reading list, and session docs
- `ce2d9e4` Add Graffito L3 pilot (complete), City Routes substrate, synthesis docs, and 5 Pro review packets
- `9239cd1` Add City Routes full experiment
- `89f460c` Add generation prototype harness, comparison memo, and AGENTS.md
- `797e386` Complete generation prototype validation across three benchmarks
- `65b096c` Add operator taxonomy note, implement multi-step accumulation
- `5a4b5c8` Add project control plane: dashboard, sprint, canonical map, architecture reconciliation prompt
- `d26bd4e` Add supply_v1 closeout, nearest-neighbor extractions, provocation seam, architecture reconciliation
- `929ccee` Add RuntimeThoughtBeatV1 as primary target: the deepest gap is runtime content, not dynamics

What this phase really was:

- zoom out from "Mueller families in code"
- articulate the `L1 / L2 / L3` architecture
- treat pressure as the driving abstraction
- separate world-building, character inner life, and traversal
- build authoring-time generation and supply
- validate traversal/generation experiments
- identify runtime thought as the next missing layer

This was the **middle** Round 3 phase, not the final architecture.

### Phase 3: prosthetic inner life and first legible thought

**Dates:** 2026-03-19 to 2026-03-21

Key evidence:

- `b04bdcc` Add plan of attack: prosthetic inner life as unifying frame
- `23947c4` Add image-reviewed book passes and condensation pilot
- `7ad97aa` Complete first-pass Mueller condensation inventory
- `43131f1` Tighten condensation mechanism boundaries
- `05c5b9a` Add condensation cross-cut summary and trace refinements
- `7cacb3e` Add runtime thought feedback loop to puppet knows
- `7458fe8` Add hybrid runtime thought routing comparison
- `7d9647f` Add runtime thought replay, cognitive viz updates, deep research chats, sprint updates

What this phase really was:

- make the kernel produce legible inner-life output
- prove writeback changes later cognition
- begin reading Mueller at higher resolution
- start realizing the project had been building from a low-resolution
  picture of DAYDREAMER

This is where the older thought-beat artifacts belong.

Important page implication:
those artifacts are still useful, but they represent the **first
legible inner-life proof**, not the full current substrate.

### Phase 4: condensation, source audit, and the deep substrate rebuild

**Dates:** 2026-03-20 to 2026-03-22

Key evidence:

- `17d155c` Add architectural framing, research replies, denotational contract, and migration analysis
- `d14c36c` Add initial RuleV1 slice
- `cab60ef` Add RuleV1 antecedent matcher
- `f7dfcfb` Add RuleV1 connection graph derivation
- `69dbee6` Add cross-family rationalization-to-roving bridge
- `73e2dc9` Execute rationalization afterglow bridge at runtime
- `74027df` Extract autonomous session conductor
- `fa0447c` Add build order checkpoint and 5Pro/5Thinking code reviews
- `3a839d3` Add family memory admission tiers and cue zones
- `b0cfc8d` Promote cross-family family-plan episodes
- `a95a665` Track family loop risk and recent anti-echo
- `5ca98eb` Track episode uses and outcomes
- `4f138ad` Add rule accessibility frontier scaffolding
- `f1ff021` Start Step 2 with roving rule executor
- `5e56be6` Route rationalization through rule executor
- `0dc6f65` Route reversal through rule executor
- `88a8123` Extract generic effect runtime scaffold
- `0343773` Add declarative effect schema validation
- `5be1f41` Narrow evaluator authority at family-plan admission
- `9fc536d` Gate activation and planning by rule access

What this phase really was:

- go past the pressure-engine reframe
- read Mueller's book and Lisp source at implementation resolution
- recover the real substrate disciplines that had been collapsed
- stop hardcoding more behavior into family functions
- move toward graphable rules, explicit access states, admission
  discipline, and kernel-owned execution contracts

This is the phase where Round 3 stops being "Mueller-shaped behavior"
and becomes "Mueller at higher resolution plus post-Mueller extensions."

### Phase 5: proving the new substrate in narrow, honest benchmarks

**Dates:** 2026-03-22 to 2026-03-23

Key evidence:

- `f00400c` Prove promotion chain and tag episodic self-hits
- `8dbe716` Trace family source candidate races
- `ebfffa3` Add membrane fixture benchmark harness
- `4da8684` Add membrane Assay B benchmark
- `13ac456` Add first Graffito microfixture benchmark
- `e001c6e` Add Graffito character-state reappraisal slice
- `2ef2597` Add Graffito rehearsal reappraisal slice

And from `current-sprint.md`:

- Assay A — passed
- Fixed-chain test — passed
- Assay B — passed
- Graffito Slice 1 — passed
- Graffito Slice 2 — passed
- Graffito Slice 3 — passed
- Graffito miniworld — next

What this phase really was:

- stop asking Puppet Knows to prove everything
- build narrow mechanistic assays for the membrane
- prove positive promotion/frontier movement
- start testing richer psychological structure through Graffito
  slices instead of only toy pressure maps

This is the current frontier.

---

## What corrected what

The phases are not random. Each one corrected the previous one.

1. **Kernel bootstrap** corrected pure design speculation.
   It made the project runnable.

2. **Pressure-engine reframe** corrected the first kernel's narrowness.
   It gave the project a clearer architecture vocabulary and a broader
   generative/traversal framing.

3. **Condensation + source audit** corrected the reframe's
   low-resolution view of Mueller.
   It recovered the missing substrate disciplines.

4. **Membrane + executor + rule-access rebuild** corrected the
   danger of persistent, loosely structured accumulation.
   It made the architecture safer and more inspectable.

5. **Membrane assays + Graffito slices** corrected the gap between
   substrate claims and narrow proof.
   They turned the new architecture into benchmarked mechanism.

---

## Vocabulary caution

Do not blur these two frames:

### Architecture frame

- `L1` world-building / authoring-time generation
- `L2` character inner life / kernel
- `L3` traversal / performance

### Proof frame

- mechanism integrity
- behavioral dynamics
- meaningful domain adequacy

The page should use both, but not interchange them.

---

## Page implications

### 1. The page needs a chronology section

Not just "what exists now."

It needs a short build arc that says:

- earlier rounds built the stage
- early Round 3 built the first runnable kernel
- the pressure-engine reframe broadened the architecture
- the Mueller condensation/source audit deepened it
- the current round of work made it structurally honest and
  benchmarked again

### 2. Old thought-beat screenshots are valid, but must be framed correctly

They should be introduced as:

- the first legible inner-life milestone
- proof of output form
- not proof of the current membrane / executor / Graffito state

### 3. The deepest current contribution is not "nicer prose"

It is:

- rules-as-data
- the memory membrane
- the executor boundary
- attributed use/outcome
- rule accessibility
- richer mechanistic Graffito slices

### 4. The page should say "what this round did"

For Round 3, the cleanest summary is:

> built the mind in layers, then rebuilt it at higher resolution

Not:

> built one thing all at once

---

## Recommended next step for page work

Use this note to write a short `How We Got Here` section in
`page_draft.md`, then update the page plan so visuals map to the
five-phase arc above.
