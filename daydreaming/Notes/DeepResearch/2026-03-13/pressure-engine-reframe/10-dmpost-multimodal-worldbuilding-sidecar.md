# DMPOST as a Multimodal World-Building Sidecar

This note captures a concrete possibility that emerged while waiting on
the GPT-5 Pro replies:

> The world-building track may become multimodal earlier than expected,
> because there is already external infrastructure for image generation,
> editing, segmentation, analysis, and compositing in
> `/Users/daviddickinson/Projects/LLM/DMPOST31`.

The point is not to collapse DMPOST into Daydream's shipping path.
The point is to identify what can be stolen now for the **research /
authoring lane** of the pressure-engine reframe.

---

## What exists already

The DMPOST repo is not just a loose idea. It already contains a usable
multi-tool pipeline:

- **Gemini Vision MCP** with:
  - `nano_edit_image`
  - `nano_segment`
  - `nano_analyze_media`
- **After Effects MCP** with a proven compositing/animation surface
- **Illustrator MCP** with export pipelines into AE
- File-based artifact handoff and run-ledger patterns

Relevant docs:

- `/Users/daviddickinson/Projects/LLM/DMPOST31/README.md`
- `/Users/daviddickinson/Projects/LLM/DMPOST31/Notes/system-architecture.md`
- `/Users/daviddickinson/Projects/LLM/DMPOST31/Notes/ai-mcp-dmpost/research/production-workflow-gap-analysis.md`
- `/Users/daviddickinson/Projects/LLM/DMPOST31/Notes/ae-mcp-dmpost/creative-direction.md`
- `/Users/daviddickinson/Projects/LLM/DMPOST31/ae-mcp-dmpost/dmpost-gemini-mcp/README.md`

The important point is that this is already a **conversational
orchestration loop**:

```text
generate/edit -> analyze/segment -> import/compose -> export/inspect -> refine
```

That is directly relevant to multimodal world-building.

---

## Why it matters to the pressure-engine reframe

So far the L1 world-building notes mostly assume text outputs:

- character profiles
- location descriptions
- tensions
- relationships

But the world-building problem may also need **visual anchors**:

- what a character looks like
- what a location looks like
- what two characters look like in the same space
- what mood variants of a space look like

That matters because visual anchors can serve multiple roles at once:

- design reference
- image-model prompt target
- LoRA training data
- consistency anchor for recurring characters/locations
- caption vocabulary source

In other words: world-building output may not just be "text that later
becomes imagery." It may include approved visual anchors as first-class
artifacts.

---

## What is immediately stealable

### 1. The multimodal refinement loop

For a proof of concept, the useful pattern is simple:

```text
structured text profile
  -> generate image candidate
  -> analyze against target description
  -> refine prompt/edit
  -> approve visual anchor
```

This is much more concrete than abstract L1 creativity claims.

### 2. Visual-anchor status in world state

The L1 structured world state can grow one small field:

```text
visual_ref_status: none | candidate | approved
visual_ref_ids: [...]
```

This immediately enables one computable pressure:

- `visual_ungrounded` — an entity exists textually but has no approved
  visual anchor

That is much easier to detect than more abstract L1 pressures like
`tonal_monotony` or `setup_without_payoff`.

### 3. Image-generation tasks as L1 outputs

At L1, not every proposal needs to be a story/world fact.
Some proposals can be **asset-generation tasks**:

- "generate character portrait"
- "generate location reference"
- "generate character-in-location composition"
- "generate 4 mood variants for this location"

These are not canonical world facts. They are authoring-time support
artifacts.

### 4. Artifact ledger / manifest discipline

DMPOST already uses:

- run roots
- manifests
- import plans
- run ledgers

That pattern is worth stealing wholesale for any multimodal world-
building experiment, because otherwise generated assets will become
untraceable immediately.

---

## What should not be stolen yet

### Do not pull AE/Illustrator into the Daydream shipping path now

The watched-run critical path is still:

```text
authored graph -> traversal -> playback packet -> renderer/stage
```

DMPOST belongs on the research side for now.

### Do not make multimodal world-building a prerequisite for Graffito v0

Graffito already has a narrower aesthetic path and different immediate
constraints. The multimodal sidecar is more relevant to:

- future worlds
- richer authoring workflows
- non-Graffito briefs

### Do not pretend this proves L1 pressure loops yet

A multimodal refinement loop can exist **without** a true pressure
engine. It may begin as a structured iterative workflow first, then
later acquire pressure scheduling if that proves useful.

---

## Honest architectural position

The honest position is:

- **Daydream shipping path:** unchanged
- **L1 research path:** may become multimodal sooner than expected
- **DMPOST:** usable external sidecar for visual world-building and
  asset refinement

This suggests a narrower near-term synthesis:

```text
L1 world-building research
  -> text proposals
  -> optional visual-anchor generation/refinement
  -> human curation
  -> approved world pack
  -> graph authoring
```

That is more believable than claiming the full pressure engine already
owns image generation, segmentation, AE composition, and LoRA prep.

---

## Smallest useful experiment

If this gets tested, keep it tiny:

1. One character
2. One location
3. Two mood variants
4. One character-in-location composition

Pipeline:

1. Write structured text profiles
2. Generate image candidates via `nano_edit_image`
3. Analyze/refine with `nano_analyze_media`
4. Optionally segment key elements with `nano_segment`
5. Approve a minimal visual anchor pack

Success criterion:

- Does this produce a reusable, coherent visual pack faster and with
  less grind than doing it manually?

Failure criterion:

- Asset drift is too high
- evaluation is too subjective
- the loop does not converge
- the resulting images are not useful as stable anchors

---

## Follow-on questions this creates

- Should `06-world-building-pressure-questions-for-5pro.md` eventually
  include multimodal pressures explicitly?
- Is `visual_ungrounded` a real L1 pressure type or just a workflow
  status flag?
- How much of this should stay as iterative authoring workflow versus
  becoming pressure-scheduled?
- At what point do approved visual anchors become LoRA/captioning
  assets rather than just design references?
