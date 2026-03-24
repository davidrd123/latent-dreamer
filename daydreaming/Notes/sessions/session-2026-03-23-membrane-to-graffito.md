# Session Log: 2026-03-23 — Membrane Bootstrapping to Graffito Miniworld

Duration: extended session
Agent: Claude Opus 4.6 (1M context)
Context used: ~380k/1000k tokens

## What we accomplished

This session went from "the membrane substrate exists but has never
exercised on a live benchmark" to "the full accumulation loop closes
on psychologically typed Graffito material in an autonomous 20-cycle
miniworld."

### Phase 1: Membrane bootstrapping diagnosis

- Diagnosed why the 50-cycle Puppet Knows soak showed zero membrane
  movement despite good family diversity
- Initial diagnosis (retrieve-episodes never called) was corrected by
  codex: it IS called, but gated on `:durable`, and authored sources
  always win because Puppet Knows has complete authored coverage
- Identified the bootstrapping deadlock: promotion requires
  cross-family use → stored-source retrieval requires `:durable` →
  `:durable` requires promotion → nothing can start
- Codex fixed: provisional candidates now enter stored-source races
  with ranking penalty below authored sources

### Phase 2: Fixed-chain proof and observability

- Codex proved the full promotion chain in a unit test: provisional
  episode → 2 cross-family successes → promotes to `:durable` →
  opens a frontier rule
- Codex added observability: same-cycle tagging on episodic hits,
  candidate-race tracing for reversal and rationalization
- Puppet Knows 20-cycle rerun confirmed: authored-source dominance
  by construction, zero dynamic candidates in the race

### Phase 3: Membrane fixture (Assay A)

- Designed the 2-situation membrane fixture with deliberately
  incomplete authored coverage (asymmetric: situation A has
  rationalization frames but no reversal causes, situation B the
  reverse)
- Codex reviewed and corrected: structural compatibility requirements
  (shared goal identity + retracted-fact overlap), honest outcome
  staging, stale gate question removed
- Codex shipped Assay A: L1/L2 passed + first `:same-family-loop`
  flag fired in 8-cycle deterministic run

### Phase 4: Assay B and benchmark ladder

- Codex shipped Assay B: live cross-family-use-success → durable
  promotion → frontier rule opening
- Established benchmark ladder:
  - Puppet Knows: runtime/seam regression
  - Assay A: L1/L2 + first flag — PASSED
  - Fixed-chain test: direct promotion/access proof — PASSED
  - Assay B: cross-family → durable → frontier — PASSED

### Phase 5: 5 Pro research (prompts 19-21)

- Prompt 19: cold-start bootstrapping, staged admission design,
  benchmark methodology, situation models
- Prompt 20: Graffito situation model (appraisal structure, layered
  reality, sensory regulation, object state, first run scope)
- Prompt 21: regulation × emotion interaction, rehearsal as
  regulation, mechanistic success criteria
- Seven replies received and sifted into two condensation documents

Key findings:
- Our membrane is genuinely novel (not a literature rename) — stages
  trust, not derivation/compilation. Closest precedent: Prodigy.
- Broaden positive evidence beyond cross-family-use-success (later)
- 9-category situation schema (original 7 + sensorimotor regulation
  + cross-layer correspondence)
- Regulation lives at character level, not situation level
- No new family needed — add reappraisal as kernel process
- RATIONALIZATION changes meaning, REHEARSAL changes control,
  reappraisal rereads the same situation after those changes
- Transient regulation (decays) vs accumulated capability (through
  the membrane)
- Family strength bias term for autonomous selection
- Versu for social practices, EMA for appraisal dynamics

### Phase 6: Graffito kernel slices

- Codex shipped Graffito Slice 1 (microfixture): typed facts drive
  family activation and retrieval across apartment + mural situations
- Codex shipped Slice 2 (regulation): Tony character state with 4
  continuous vars, derived regulation mode, reappraisal after family
  execution, same mural reads differently after apartment support
- Codex shipped Slice 3 (rehearsal): apartment rehearsal changes
  Tony's state, mural flips from threat to challenge

### Phase 7: Graffito miniworld

- Built autonomous 3-situation miniworld: street overload → apartment
  support → mural crisis
- 20-cycle trace showed:
  - Tony starts overloaded, builds capacity, gets knocked back by
    street adversity, recovers stronger, eventually stabilizes
  - 6 autonomous reappraisal flips
  - Family diversity: rationalization 10, reversal 6, rehearsal 4
  - 16 stored episodes
  - Tony reaches "flowing" regulation mode by cycle 17

### Phase 8: Accumulation bridge

- Connected transient regulation to the membrane: stored episodes
  get cross-family reuse, accumulate evidence, promote to durable
- First dynamic source candidates entered the race (8 cycles)
- First dynamic wins (7 cycles)
- First cross-family use on Graffito material
- First promotion on Graffito material
- First frontier rule opening on Graffito material
- Second distinct cross-family path established (not leaning on one
  bridge)

Final 20-cycle miniworld counters:
- dynamic-source-candidate-cycles: 7
- dynamic-source-win-cycles: 7
- cross-family-source-win-cycles: 4
- episodes-with-cross-family-use-history: 2
- episodes-with-promotion-history: 2
- durable-episode-count: 2
- frontier-bridge-cycles: 1
- rule-access-transition-count: 1

### Phase 9: Project housekeeping

- Vendored Graffito source material into latent-dreamer
- Archived DeepResearch replies 01-16, updated all cross-references
- Updated README to reflect current kernel-first architecture
- Updated evaluation framing in current-sprint.md (three-level stack)
- Killed a runaway bb nrepl-eval process

## Key decisions settled

1. Puppet Knows stays as runtime regression; membrane testing moved
   to dedicated fixtures
2. Benchmark ladder: assays prove one thing each, don't bloat
3. Evaluation framing: mechanism integrity → behavioral dynamics →
   meaningful domain adequacy
4. "Does this feel like a mind?" is the guiding question, answered
   mechanistically through dynamics on meaningful material
5. Regulation at character level, not situation level
6. No new family — reappraisal as kernel process
7. Transient state vs accumulated capability split: accumulation goes
   through the membrane, not through sticky character-state floats
8. Graffito is not mentioned in the public README

## Key correction received

David and codex corrected an overclaim: "non-collapsed dynamics are
the product" was too strong. The dynamics are essential mechanisms.
The product is those mechanisms operating over semantically rich
material. Neither alone is sufficient. The evaluation framing was
revised to reflect this.

## Documents produced / updated

| Document | What |
|----------|------|
| `condensation/research-sifts/research-sift-cold-start-and-situation-model.md` | Sift of prompts 19 replies |
| `condensation/research-sifts/research-sift-graffito-situation-model.md` | Sift of prompt 20 replies (updated 3x) |
| `daydreaming/Notes/membrane-fixture-spec.md` | Assay A spec (updated with ladder) |
| `DeepResearch/prompts/19-cold-start-and-admission-comparison.md` | 5 Pro prompt |
| `DeepResearch/prompts/20-graffito-situation-model.md` | 5 Pro prompt |
| `DeepResearch/prompts/21-regulation-conduction-accumulation.md` | 5 Pro prompt (updated) |
| `daydreaming/vendor/graffito/` | Vendored source material (8 files) |
| `README.md` | Rewritten from scratch |
| `current-sprint.md` | Evaluation framing + benchmark ladder |
| `dashboard.md` | Synced to current state |
| `24-graffito-kernel-brief.md` | Situation/character state split |
| `build-order-checkpoint-2026-03-22.md` | Well-definedness gate note |

## Code landed by codex (commits this session)

| Commit | What |
|--------|------|
| `2512177` | goal_families nil contract + Puppet Knows pressure retune |
| `f00400c` | Fixed-chain promotion proof + provisional on-ramp |
| `8dbe716` | Candidate-race observability |
| `ebfffa3` | Membrane fixture benchmark (Assay A) |
| `4da8684` | Membrane Assay B |
| `13ac456` | First Graffito microfixture |
| `e001c6e` | Graffito character-state reappraisal slice |
| `2ef2597` | Graffito rehearsal slice |
| `c5c49e0` | Autonomous Graffito miniworld |
| `21ad7af` | Miniworld run summary + counters |
| `f31837c` | Miniworld source-race on-ramp |
| `df61a80` | Cross-family Graffito bridge |
| `baf1c83` | Frontier-path bridge + rule opening |
| `0f260e2` | Second distinct cross-family path |

253 tests, 1377 assertions, 0 failures, 0 errors.

## What to read on restart

If compacted, read in this order:

1. This session log
2. `daydreaming/Notes/current-sprint.md` (evaluation framing + benchmark ladder)
3. `condensation/research-sifts/research-sift-graffito-situation-model.md` (all Graffito research findings)
4. `condensation/research-sifts/research-sift-cold-start-and-situation-model.md` (membrane research findings)
5. `experiential-design/24-graffito-kernel-brief.md` (kernel-facing Graffito brief)
6. `daydreaming/Notes/dashboard.md` (current project status)
