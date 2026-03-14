# Experiential Design Track

This subfolder is the creative/experiential side of the daydream system. It
exists separately from the mechanical/implementation docs because the two tracks
are being developed in parallel:

- **This track (David + Claude):** What does the experience want to be? What
  does "feeling like dreaming" mean concretely? What scenarios are visually
  productive? How do Mueller's cognitive modes translate into aesthetic
  treatments?

- **Parallel track (Codex):** Build the v0 scheduler and traversal machinery.
  Mechanical. Doesn't need the creative questions answered — it needs the
  contracts and data structures.

The goal is that the creative exploration here informs what the v0 scheduler
should *do* once it's running, without blocking it from being built.

## Documents in this folder

- `01-the-experience.md` — What the experience wants to be. The core creative
  vision that everything else serves. Includes the Dreamer + Director
  two-agent model (Mueller emotional logic + artistic visual interpreter),
  the Director's Toolkit section mapping the team's existing prompt
  engineering work to the architecture, and a goal-type-to-cinematography
  mapping table for conversations with the team.
- `02-codex-v0-brief.md` — Self-contained brief for Codex to build the v0
  scheduler. Mechanical spec, no creative dependencies.
- `03-architecture.md` — Full system architecture for Conducted Daydreaming.
  Not a Mueller port — a new architecture that uses Mueller for the emotional
  scheduler and adds the Director layer, feedback loop, competing lenses,
  three-voice narration, and the team's prompt engineering toolkit.
- `04-review-findings.md` — Consolidated feedback from three external Opus
  reads (PCE, MR_IMS, VC). Convergent findings, unique contributions,
  and 11 concrete design recommendations ordered by build phase.
- `05-phase3-tuning.md` — The three hardest open questions (gap width,
  contradiction handling, semantic drift) resolved into measurable
  control parameters with specific logging requirements for Phase 3.
- `06-prep-layer.md` — Creative brief format and prototype ("The Puppet
  Knows"). What a brief contains, how it flows through the architecture,
  example situations with distinct index signatures. Prep agent scaling
  deferred to Phase 5+.
- `07-dream-sequence-01.md` — Hand-composed 12-cycle dream sequence using
  the Puppet Knows brief. Tests drift, return, obsession, goal-type
  variation, Director-driven situation activation, and surprise arrival
  via coincidence.
- `08-brief-stalker-zone.md` — Creative brief: "The Zone Knows What You
  Want." Stalker/Tarkovsky palette. The Zone reads the traveler's desire
  — landscape as handwriting, traps as gifts. Five situations. Tests the
  format against a spiritual-contemplative register.
- `09-brief-arctic-expedition.md` — Creative brief: "The Ship Becomes the
  Place." Arctic expedition palette. Stasis by force — the ship frozen in
  ice becomes the most interesting place in the world. Five situations
  with deliberate s2/s5 `pressure` overlap for coincidence retrieval
  testing.
- `10-daydreamer-parallel-track.md` — Source-grounded spec for the bounded
  Clojure DAYDREAMER resurrection track. Defines what to recover from the
  original architecture, what to defer, and how the sidecar should relate to
  the production Python engine.
- `11-source-inventory.md` — Deliverable #0 for the resurrection track.
  Maps Mueller source files and functions to sidecar modules, implementation
  waves, benchmark fixtures, and the specific mechanisms still absent from the
  Python engine.

- `12-director-prompt-spec.md` — Phase 2 proper Director spec. Defines
  what the LLM Director receives (trimmed DreamNode + brief + style
  extensions), what it produces (structured feedback echo), the full
  prompt template, a worked n08 example (hardest case: anger episode
  discovery), and a three-step calibration strategy.
- `13-project-state.md` — Project state map for David. System diagram,
  maturity map, evaluation ladder, decision dashboard, falsification
  criteria. Written to enable questioning the direction, not just
  executing it.
- `14-operating-map.md` — Current terrain map. Defines the active tracks
  (conducted mainline vs Mueller fidelity), the stable seams
  (DreamNode, Director packet, Director feedback, trace schema), what
  "kernel -> Director integration" actually means, and what can safely
  run in parallel without destabilizing the system.
- `15-arctic-control-fixture.md` — Why `Arctic Expedition` should be the
  control fixture for understanding and tuning the machinery, plus the
  minimum full package shape and milestone set needed to make it useful.
- `16-rendering-agents.md` — Three-agent rendering architecture:
  Director (interprets the bend), Cinematographer (constructs the
  video prompt using prompt craft rubrics), Composer (constructs the
  music prompt). Supersedes the single-Director rendering model in
  docs 01 and 03. Provisional.

## Key Connections to Other Work

The Director's Toolkit section references the team's prompt engineering work:

- `ComfyPromptByAPI/WorkingSpace/davidrd/OutsideStruct/PromptWriting/` —
  Shared patterns and numbered strategy docs
- `ComfyPromptByAPI/WorkingSpace/davidrd/OutsideStruct/John/Prompting/` —
  Akira style guide, Wan model spec, creative briefs

These are the existing resources that become Director style guides in the
daydream architecture. The team has solved "how to turn creative intent into
achievable prompts" — the daydream system needs to wire the Dreamer's
emotional output into that pipeline.
