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
