# Project Page Plan v2

Working document. Keeps v1 options alive, adds beat-led alternative.

For actual chronology, do not infer from this file alone. See
`round3-timeline-reconstruction.md`.

---

## Compressed build arc for the page

This is the page-sized version of the full timeline. Use this, not
the full reconstruction note, when writing public-facing copy.

### Three-line version

1. Earlier hackathon rounds built the stage: live video, control,
   and audio.
2. This round built the mind: a Clojure kernel with persistent
   pressure, episodic memory, rules-as-data, and disciplined
   accumulation.
3. Next round connects them: conductor input shapes the kernel, and
   the kernel's inner life drives narration, audio, and eventually
   video.

### Slightly richer paragraph

Previous rounds proved the performance surface: Scope, APC control,
and reactive audio. This round built the cognitive substrate that
can feed that surface: a Clojure kernel with episodic memory,
structured rules, writeback, and a memory membrane that keeps
accumulation from collapsing into self-reinforcing grooves. The next
step is to reconnect them so a performer is steering a mind, not
just a prompt.

### What not to put on the page

- the full Round 3 archaeology
- every internal phase boundary
- the whole pressure-engine / condensation correction story

Those matter for us, but the page only needs the clarified result.

---

## Two possible structures

### Option A: Diagram-led (from v1)

1. Opening — instrument + this round built the mind
2. Hero visual — generated Conductor → Kernel → Stage diagram
3. What this round built — bridge visual (stage | mind | next)
4. Proof it runs — cognitive trace screenshot
5. The innovations — membrane, writeback, rules-as-data
6. What's proven / what's not
7. Next round
8. Honest claim

Strength: explains the architecture clearly up front.
Risk: reads as a technical report. Generated diagrams carry the
page. If they're not great, the page is flat.

### Option B: Beat-led (new)

1. Opening — one paragraph, instrument + mind
2. The beats — 4 screenshots stacked, minimal caption
3. What produced this — brief kernel cycle explanation
4. What's new here — membrane, feedback loop, rules-as-data
   (with generated diagrams as supporting visuals, not heroes)
5. Where it came from — Mueller condensation, brief
6. Connection to the stage — where this goes next
7. Honest claim

Strength: leads with real output. The prose is compelling on
its own. Diagrams support rather than carry.
Risk: people might not understand what they're looking at
without more framing. The beats are impressive but opaque
without context.

### Option C: Hybrid

1. Opening — instrument + this round
2. Brief "what this is" — 2-3 sentences, not full architecture
3. The beats — 4 screenshots
4. Caption that explains what's happening — "The operator changes
   because the emotional pressure changes. The prose changes
   because the memory changes. Each beat is generated from
   persistent cognitive state, not a prompt."
5. The kernel cycle — generated diagram, explains what produces
   the beats
6. The innovations — membrane + one diagram, rules-as-data +
   one diagram. Brief.
7. Connection to stage — generated bridge diagram or sketch
8. Honest claim

Strength: beats are the emotional hook, diagrams explain the
machinery, the page has both proof and explanation.
Risk: might be too long. Need to keep sections tight.

---

## The beats we have

Four screenshots in drafting/beats/:

01 — Cycle 1, RATIONALIZATION (amber), "seeing through"
     "He knows the seam is real..."
02 — Cycle 3, REVERSAL (magenta), "the ring"
     "What if the fingerprint had been left deliberately..."
03 — Cycle 9, ROVING (cyan), "seeing through"
     "Something loosens..."
04 — Cycle 12, REVERSAL (magenta), "the ring"
     "The final pass doesn't resolve..."

Arc: rationalize → reframe → drift → accept.
Color: amber → magenta → cyan → magenta.

These are real output from the hybrid routing run
(Haiku default, Sonnet on reversal). Not cherry-picked
from multiple runs — this is one continuous 12-cycle trace.

---

## Generated visuals — priority list

Hard ranking for now:

1. Kernel Cycle
2. Memory Membrane
3. Conductor -> Kernel -> Stage bridge
4. Rule graph / frontier
5. Writeback divergence proof graphic

If only two images get made first, make `Kernel Cycle` and
`Memory Membrane`.

### Visual bar from Round 02

Use the screenshots in `drafting/round02/` as the quality bar.

What those visuals got right:

- dense and specific, not airy concept art
- labeled like a real product spec
- dark background with restrained neon accents
- actual system structure, not mood boards
- clear hierarchy: title, panels, labels, highlight color
- concrete nouns everywhere: pad grid, faders, FPS stages,
  learned features, named palettes

What that means for Round 03:

- show the actual kernel cycle, not "a mind thinking"
- show the actual membrane states, not "memory flowing"
- show the actual family / rule structure, not abstract nodes
- use real labels from the system: concern, episode, provisional,
  durable, frontier, planner-visible, reversal, roving,
  rationalization
- prefer diagrams that feel like technical posters over diagrams
  that feel like startup explainers

### Prompt guardrails

Every generated visual prompt should specify:

- exact labels to render
- exact states or operators to show
- a dense editorial / technical-poster layout
- restrained glow, not sci-fi haze
- no humanoid figures, no brains, no floating code rain
- no generic "AI mind" imagery

The goal is: "real technical visualization of a real system," not
"art inspired by the system."

### Must-have (needed for any option)

1. **Kernel Cycle** — the inner loop that produces the beats.
   Ring flow: select → plan → retrieve → store → project.
   Feedback arrow: residue → memory. Labels: membrane, graph.
   This is the "how" visual.

2. **Memory Membrane** — episodes flowing through tiers.
   Trace → provisional → durable. Flags. Frontier/planner.
   This is the "what's new" visual.

### Nice-to-have

3. **Conductor → Stage Bridge** — where this connects to the
   performance instrument from earlier rounds. Three columns:
   conductor | kernel | stage outputs.

4. **Rules-as-Data / Connection Graph** — family clusters with
   cross-family bridges. Frontier halo. Only if page needs
   more depth.

5. **Writeback Divergence** — baseline vs. writeback trace split.
   Best as a proof graphic or real screenshot, not a speculative
   generated image.

### Probably skip

6. Concern Competition — interesting but redundant if the beats
   already show family switching.
7. Same-Situation Reappraisal — hints at Graffito direction
   but may not belong on this page.
8. What This Round Built (two-column) — could be text instead.

### Prompt revision note

The first prompt drafts were too generic compared to the Round 02
visual bar. Revised prompts should be:

- denser
- more labeled
- more product-spec-like
- closer to real dashboard / architecture print graphics

That is especially important for:

- Kernel Cycle
- Memory Membrane
- Conductor -> Kernel -> Stage bridge

---

## Open questions (carried from v1 + new)

- Which option (A/B/C)? Leaning C but not committed.
- How much Mueller? The book story is interesting but the page
  might not need it. Could be one sentence: "Built from Erik
  Mueller's 1990 cognitive architecture for daydreaming,
  condensed from 90K words into 19 typed mechanism cards."
- Test count on the page? 247 tests, 1311 assertions shows
  rigor but might read wrong for a creative-tech audience.
- Other applications section? Writing companion, research daemon,
  etc. — dilutes or enriches?
- Should the thought stream HTML be linked as a live artifact?
  (out/thought_stream.html)
- Do we need the full dashboard screenshot from the cognitive
  viz, or do the beat screenshots replace it?
- How much text total? The current page_draft.md is ~280 lines.
  That's long for a project page. Target maybe 150?

---

## What we actually have right now

Real artifacts:
- 4 beat screenshots (ready)
- thought_stream.html (ready, linkable)
- cognitive trace HTML pages (existing, screenshottable)
- writeback comparison data (existing, could screenshot)

Generated visuals:
- none yet

Text:
- page_draft.md — full but needs reframing/trimming
- page-plan.md (v1) — structure + prompts
- this file (v2) — options

---

## Immediate next actions

Pick 1-2:
- [ ] Try generating the Kernel Cycle diagram
- [ ] Try generating the Memory Membrane diagram
- [ ] Draft the 3-line build arc into page-ready prose
- [ ] Draft the beat caption text
- [ ] Trim page_draft.md to ~150 lines in Option C structure
- [ ] Screenshot the writeback divergence for proof section
- [ ] Decide on Mueller prominence
