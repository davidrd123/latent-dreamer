# Project Page Plan

Working document for the page. Not the page itself.

---

## The story

Last round built the stage: real-time video, live control,
reactive audio.

This round built the mind: a cognitive engine with persistent
concerns, earned memory, and a feedback loop where generated
thought changes what the character thinks next.

Next round connects them.

---

## What makes this round interesting to talk about

Not just "we built an architecture." The innovations themselves
are the story:

1. **The feedback loop** — an LLM generates inner-life prose from
   kernel state, the residue writes back as a structured episode,
   and that changes future retrieval and behavior. Nobody else is
   doing this with a symbolic cognitive engine underneath.

2. **The memory membrane** — episodes earn their influence through
   attributed downstream evidence. Prevents the system from
   developing confirmation bias or recycling its own best stories.
   Mueller never needed it (single session). We do (accumulation).

3. **Rules-as-data** — cognitive behavior moved from opaque
   functions into typed searchable structures. The system can
   derive connections between mechanisms that nobody coded, store
   provenance about which cognitive paths produced each memory,
   and eventually discover non-obvious creative links.

4. **The condensation method** — 90K words of cognitive science
   → 19 typed mechanism cards → buildable hybrid architecture.
   A methodology for giving LLMs structured psychological
   capabilities from serious theory.

5. **A valiant effort** — in an LLM-centric, deep-learning-centric
   world, this is an argument that structured cognitive machinery
   still matters. The LLM provides the words. The engine provides
   the mind.

---

## Page structure

1. **Opening** — one sentence about the instrument, one about this
   round building the mind
2. **Hero visual** — Conductor → Kernel → Stage
3. **What this round built** — bridge visual (stage | mind | next)
4. **Proof it runs** — cognitive trace screenshot (real artifact)
5. **The innovations** — membrane, writeback, rules-as-data
   (with generated visuals for each)
6. **What's proven / what's not** — short, honest
7. **Next round** — connect the mind to the stage
8. **The honest claim** — same as current draft, compressed

---

## Visual plan

### Real artifact visuals (don't generate, screenshot)

- Cognitive trace HTML — one of the existing benchmark outputs
  showing a 12-cycle run with thought beats
- Writeback comparison — baseline vs feedback traces diverging

### Generated visuals (6-7 images)

Priority order for generation:

1. **Hero: The Instrument**
   Conductor → Kernel → Stage flow
   Clean editorial systems poster
   Dark bg, cyan/amber/magenta, restrained glow

2. **Memory Membrane**
   Episodes flowing through trace → provisional → durable
   Side channels: stale, backfired, contradicted
   Rule-access: quarantined → frontier → planner-visible
   Precise systems anatomy

3. **Writeback Loop**
   Kernel cycle ring with thought beat exiting, curving back
   as residue, causing next retrieval to diverge
   Data-viz style proof graphic

4. **Rule Graph / Frontier**
   Bright inner cluster (planner-visible), halo (frontier),
   dim outer (quarantined)
   Cross-family bridges highlighted
   Inspectable wiring diagram aesthetic

5. **What This Round Built**
   Two-column: Round 02 (stage) | This Round (mind)
   Bridge connecting: "Next: connect"

6. **Concern Competition**
   Five family operators orbiting one active concern
   Emotional pressure selecting the winner
   Dynamic control diagram for inner life

7. **Same-Situation Reappraisal**
   Split panel: same scene, different character state
   Left: overloaded/threat. Right: entrained/challenge
   Kernel pass between them updating appraisal
   Hint at where this is going (without naming Graffito)

### Visual language

- Dark background
- Cyan / amber / magenta / acid green accents
- Thin luminous lines, not chunky SaaS graphics
- Restrained glow, not fizzy
- Crisp typography, high legibility
- VJ-tech editorial meets cognitive machinery
- No humanoid robots, no giant brains, no code rain

---

## What NOT to do

- Don't lead with architecture jargon
- Don't make the page depend on visuals that don't exist yet
- Don't pretend this is a performance reveal
- Don't name Graffito prominently (Mark's material)
- Don't over-explain the kernel to people who want the instrument
- Don't fake evidence with generated images — use them for
  structure explanation only

---

## Open questions

- How much of the "innovations" section should be on the page
  vs. in a linked deep-dive?
- Should we include the test count (238 tests, 1215 assertions)?
  Shows rigor but might read as flex.
- How prominent should the Mueller / 1990 book story be? It's
  interesting but could read as academic.
- Do we want the "other applications" section (writing companion,
  research daemon, etc.) or does that dilute the instrument story?

---

## Prompts ready to generate

See codex's prompt set above. Start with:
1. Hero: The Instrument
2. Memory Membrane
3. Rule Graph / Frontier

Those three give enough to reshape the page.

---

## Connection to existing drafts

- `page_draft.md` — current full text, needs reframing
- `stage-connection-sketch.md` — the conductor→kernel→stage flow
- `viewpoint.md` — earlier draft, superseded by page_draft
- `why-rules-as-data.md` — absorbed into page_draft
- `system-walkthrough.md` — teaching doc, not page material
- `system-mechanics-detailed.md` — reference doc, not page material
