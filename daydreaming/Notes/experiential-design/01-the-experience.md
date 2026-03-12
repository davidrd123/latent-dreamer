# The Experience

What does this system want to be? Working backwards from the experience, not
forward from the architecture.

## The Core Feeling

The audience should feel like they are **inside a mind that is dreaming**. Not
watching a dream — being the dreaming. Two things are happening:

1. **The landscape** — video + music. Scenes, places, moods. This is the
   content of the dream.

2. **The visible interior process** — the mind *working on* the content.
   Obsessing. Drifting. Rehearsing. Rationalizing. This is what makes it feel
   like a mind and not a slideshow.

These two layers are not always simultaneous. Sometimes you're immersed in the
landscape (performance mode — the engine is quiet). Sometimes the interior
process becomes visible, audible, legible (daydreaming mode — the engine's
state is rendered). Sometimes both are happening at once.

That oscillation — being-in-it vs. watching-the-process-of-it — is the
fundamental rhythm of the piece.

## What's Visually Interesting About a Mind Dreaming

Working backwards from what we'd actually want to see and hear:

### The Switch
You're thinking about one thing and suddenly you're somewhere else. Hard cut.
Disorienting. Then you realize why — something connected them. That connection
wasn't obvious. This is serendipity rendered.

### The Return
You keep coming back to the same scene but it's slightly different each time.
Same place, but the weather changed. Same object, but now you're angry instead
of wistful. This is goal-type variation on the same situation — rehearsal of
the harbor vs. revenge fantasy about the harbor vs. roving away from the
harbor.

### The Overlay
Two situations are active simultaneously and the imagery starts to bleed. The
mind can't separate them. Visually this could be superposition, split screen,
rapid alternation. This is competing activation made visible.

### The Narration Intrusion
Text appears — not explaining, but revealing the cognitive posture. Not "she
walked to the harbor" but "What if I had just..." (reversal) or "It wasn't
really about that" (rationalization) or "Again. Again." (obsessive return).
The narration makes the *type of thinking* legible to the audience.

### The Dwelling
The system holds on something. The video barely moves. The music sustains or
slowly shifts. Something is unresolved and the mind can't let go. This is
hold state — the opposite of a cut.

### The Acceleration
Suddenly the mind is jumping fast between scenes. Revenge fantasy, then roving
escape, then back to rehearsal. The cuts get faster, the music shifts
direction. Emotional pressure is building. Multiple goal types are all firing
at high activation.

## Two Modes of the System (Repurposing Mueller)

Mueller's architecture has `performance` and `daydreaming` as system modes.
Repurposed for this experience:

### Performance Mode
The stage is dominant. Full immersion in the scene. The engine is running but
its interior state is not rendered. The audience is *in* the dream. Video fills
the frame, music is the full experience, narration (if present) is sparse and
atmospheric.

### Daydreaming Mode
The engine's interior process becomes visible. You see *what the mind is doing*
with the material. The stage is still running but it's now illustrating the
thinking, not just presenting scenes. Narration becomes more present. The
visualization of interior state (goal types competing, activation levels,
situation tensions) could be rendered as an additional visual layer.

The toggle between these modes is itself a creative parameter. It could be:

- Automatic (driven by activation levels — high tension makes the interior
  state leak through)
- Manual (David controls it via the APC Mini, like a fader between immersion
  and introspection)
- Both (automatic baseline with manual override)

## Scenarios Need to Be Visually Productive

Mueller's test scenarios (relationship anxiety, earthquake fear) are
psychologically interesting but not visually interesting. For this system,
situations need to:

- Map to **distinct visual territories** — so switching between situations
  creates visible contrast
- Support **goal-type variation** — so the same situation can be rehearsed,
  reversed, rationalized, and each treatment looks/sounds different
- Create **pacing that works for audiovisual media** — not just cognitive
  plausibility but rhythm, tension, release

The existing palettes are a starting point. Each palette already has visual
regions with different moods, density, color temperature. The question is:
what situations make it interesting to *dream through* those regions?

## Is Mueller the Only Engine?

The four-layer pipeline (world state → engine → graph → stage) is general.
Mueller is one specific engine. The architecture could support others:

- **Purely associative engine** — no goal types, just drift by visual/tonal
  similarity. Ambient, meditative, no emotional pressure.
- **Tension-arc engine** — build pressure, release, build again. More
  musical than cognitive. Driven by density/energy curves.
- **Spatial navigation engine** — navigate a place, get lost, find landmarks.
  The "dreaming" is being lost in a space rather than processing emotions.
- **Mueller engine** — motivated wandering. Wanders *because of* something.
  The type of wandering tells you something about the emotional state.

Mueller's contribution is the *motivation*. His system doesn't drift
randomly — it obsesses, and the obsession has character (rehearsal vs
revenge vs roving). That's more interesting than ambient drift. But you
don't necessarily need all seven goal types, and you don't need his
specific interpersonal triggers.

The question is whether the motivation layer — the thing that gives the
wandering purpose and texture — needs to be Mueller-shaped, or whether
a simpler pressure/release model could produce equally compelling output.
That's an empirical question, not a design question. Build it and see.

## The Layered Mind: Dreamer + Director

The system isn't one mind dreaming. It's a layered mind — there's the
dreaming, and then there's an interpreter watching the dreaming and producing
their own visual translation of it.

### The Dreamer (Mueller's architecture)

The emotional logic layer. Obsessing about situations, switching between goal
types, returning to charged material. Produces:

- **What** the mind keeps coming back to
- **How** it's processing it (rehearsal, revenge, rationalization, etc.)
- **When** it switches, returns, holds, or breaks

The Dreamer's voice is the **narration** — inner monologue, first-person,
intimate. Text-to-speech. Whispered. "I keep coming back to that door."
"It wasn't my fault. It had already started." "What if I just..."

### The Director (the artistic interpreter)

A second agent — think Freud meets Jodorowsky meets Lynch — that watches what
the Dreamer is doing and interprets it into wild visual language. The Dreamer
is stuck on a betrayal? The Director doesn't show a literal betrayal. The
Director shows a figure kneeling before an enormous weighing scale made of
bones, or a baby being handed through a slot in a concrete wall.

The Director translates emotional obsession into **visual imagination**. The
text-to-video isn't rendering the dream content — it's rendering what a
visionary director's mind does with the dream content.

The Director's voice is the **visual stream** — wild, associative, surrealist.
The imagery is not literal illustration of the Dreamer's narration. It's a
productive misreading of it. The gap between what the Dreamer says and what
the Director shows is where the art lives.

### The Gap Between Them

The Dreamer says: "I can't stop thinking about the deal I made."

The Director shows: a mouth opening in a wall of ice, exhaling golden dust
that solidifies into a chain.

That gap — between emotional logic and visual interpretation — is the creative
engine. It's what makes this feel like a mind dreaming rather than an
illustrated audiobook. Dreams don't illustrate their own logic. They
translate it into imagery that is associatively connected but visually
autonomous.

### How This Changes the Architecture

Current pipeline:
```
situation → goal type → palette cell lookup → Scope prompt
```

This is a lookup. The visual output is limited to what's in the palette.

New pipeline:
```
situation → goal type → Dreamer state (emotional, obsessive)
                          ↓
                    Director interprets
                          ↓
                    wild prompt → Scope
```

The Director is where the LLM lives — not as a planner, not as a retriever,
but as an **interpreter with a specific artistic sensibility**. You could give
it a system prompt that encodes a directorial voice:

- **Jodorowsky** sees everything through ritual, body, sacred geometry
- **Lynch** sees everything through suburban dread, electricity, doubled selves
- **Tarkovsky** sees everything through water, memory, rooms that breathe
- **Kubrick** sees everything through symmetry, institutional space, the gaze

The Director could even be swappable. Same Dreamer, different Director =
same emotional journey, completely different visual language.

### Three Voices, Not Two

The Dreamer has a voice: intimate inner monologue. "I keep coming back to
that door." "It wasn't my fault." Whispered, first-person.

The Director also has a voice — and it's a different voice. It's the artist
thinking through their interpretation: "No, no — the wall isn't a wall. The
wall is his mother. Make the wall breathe." This is the directorial process
made audible. It's not intimate — it's urgent, associative, commanding. It
talks to itself the way a filmmaker talks while editing.

And the visual stream is a third "voice" — what the Director actually
produces. The imagery.

So there are three channels:

1. **Dreamer narration** (TTS, whispered) — what the mind is feeling
2. **Director narration** (TTS, different voice/register) — how the artist
   is interpreting it
3. **Director imagery** (Scope video) — what that interpretation looks like

These don't all run at once. In performance mode, maybe only the imagery.
In daydreaming mode, the Dreamer's whisper leaks in. In full introspection,
the Director's own voice becomes audible — you hear the artist thinking.

The mixing between these three channels is itself a creative parameter.

### Society of Mind: Multiple Competing Directors

This is closer to Minsky's "Society of Mind" than Mueller alone. Mueller
gave us the single-agent dreaming architecture. Minsky argued that cognition
is multiple specialized agents competing and collaborating.

But the society-of-mind idea extends beyond Dreamer + Director. What if there
are **multiple Directors active simultaneously**, competing the way Mueller's
goal types compete?

- **The Freudian** reads everything as desire and repression
- **The Jodorowsky** reads everything as mythic/alchemical transformation
- **The Paranoid** reads everything as conspiracy and surveillance
- **The Sentimental** reads everything as loss and nostalgia

They all receive the same Dreamer state ("obsessing about entrapment in
REVERSAL mode"). Each produces a different interpretation. The strongest
one drives the imagery for a while, until another takes over.

That switching between interpretive frames — not just switching between
situations, but switching between *ways of seeing the same situation* — would
itself feel deeply dreamlike. Dreams don't have one consistent aesthetic.
They shift register. One moment it's mythic, the next it's paranoid, the
next it's tender.

The competition could use Mueller-like dynamics: each Director-lens has
activation strength, activation decays over time, serendipitous connections
can boost a lens unexpectedly. The Freudian lens might activate when the
Dreamer's situation involves interpersonal tension. The Paranoid lens might
activate when the situation involves surveillance or entrapment. But they
all compete, and sometimes the "wrong" lens wins — and that accidental
misfit is what makes it feel like dreaming.

### Bidirectional Coupling: The Director Feeds Back

The current architecture is a pipeline:

```
Dreamer → Director → Stage
```

But what if the Director's interpretation feeds *back* into the Dreamer?

The Director goes wild with a wall-as-mother reading of "entrapment." That
interpretation — which the Dreamer never intended — activates a new
situation about maternal loss. The emotional valence shifts. A different
goal type triggers. The Dreamer starts obsessing about something the
Director accidentally surfaced.

```
Dreamer → Director → Stage
   ▲          │
   └──────────┘
   interpretation
   feeds back as
   new emotional
   input
```

This creates a genuine feedback loop. The dream mutates through its own
rendering. The Director's misreading of the graph isn't a bug — it's how
dreams actually work. You start dreaming about one thing, the imagery
goes somewhere unexpected, and suddenly the dream is about something else
entirely. The content and the interpretation co-evolve.

From the DeepResearch paper on bidirectional knowledge coupling
(`daydreaming/Notes/DeepResearch/2026-03-11/bidirectional_knowledge_text_coupling.md`):
the graph and the imagery are two views of the same object, and keeping
them coupled bidirectionally is an unsolved problem in scholarly knowledge
systems. But in a dream system, perfect fidelity isn't needed. Dreams
*should* be lossy. The mutation introduced by the feedback loop is a
feature, not a defect.

What feeds back concretely:

- **New indices** — the Director's interpretation contains concepts (mother,
  birth, mirror) that become active indices in the Dreamer's coincidence
  retrieval. This is how the Director's imagery can trigger unexpected
  episodic recall.
- **Emotional shifts** — the Director's imagery has emotional valence.
  If the Director produces something unexpectedly tender or violent, that
  shifts the Dreamer's emotional state, which changes goal-type selection.
- **New situation activation** — the Director's interpretation might
  resonate with a situation that wasn't currently active. The wall-as-mother
  reading activates a latent situation about origin or belonging.
- **Surprise** — Mueller has an explicit surprise mechanism (emotional boost
  of 0.25 strength when serendipity fires). The Director producing something
  unexpected could trigger the same response: surprise unhalts a stalled
  goal, re-energizes a decayed situation.

This feedback loop is the most speculative part of the architecture. It's
also potentially the most powerful. It means the system can surprise itself.

### The Director as Mutation Engine

Mueller's mutation mechanism (`dd_mutation.cl`) is structural and mechanical:
when an imaginary goal's action path is blocked, enumerate permutations of
the action (swap participant, swap action type), then check whether
serendipity can connect the mutated variant to known episodes. It's clever
but narrow — it can swap a character or an action type, but it can't
reimagine the entire scene.

The Director's imagination *replaces* Mueller's mutation mechanism — and
it's vastly richer.

When the Director interprets "entrapment in REVERSAL mode" and produces a
wall made of breathing skin, that's not just a visual treatment. It's a
**mutation of the environment**. The wall has become something else. And
that mutated environment feeds back into the Dreamer as new material. Now
there's a breathing wall in the dream-space. What does entrapment mean
when the walls are alive? The Dreamer's emotional response changes. New
goal types activate. New situations become relevant.

The Director produces environment mutations, scenario mutations, and
relational mutations as a *byproduct* of interpretation:

- **Environment mutation:** The Director reimagines a harbor as a flooded
  cathedral. Now the Dreamer has a cathedral to obsess about — new indices,
  new associations, new emotional resonances.
- **Scenario mutation:** The Director interprets a "bargain" situation as a
  ritual sacrifice. Now the stakes are different. Recovery means something
  different after a sacrifice than after a business deal.
- **Relational mutation:** The Director casts two characters as reflections
  of each other (the Freudian lens sees them as split selves). Now the
  Dreamer's interpersonal tension becomes internal tension. The situation
  changes character without changing structure.

Mueller needed explicit mutation rules because his output was literal (plan
steps, action goals). The Director doesn't need mutation rules because
**imaginative interpretation is inherently mutative**. Every time the
Director interprets the Dreamer's state, it introduces novel elements that
weren't in the original material. Those novel elements are mutations. They
cascade through the feedback loop into new Dreamer states, which produce
new Director interpretations, which introduce new mutations.

This is how dreams actually evolve. You don't dream about the same thing
in the same way for long. The imagery mutates the content, and the mutated
content produces new imagery. The dreaming mind and the dreaming eye are
in a feedback relationship, and neither controls the other.

The practical consequence: when the Director is running, it's the primary
source of novelty and mutation. But Mueller's structural mutation (swap one
element, check serendipity) still matters as a complement and fallback:

- **When the Director gets stuck or repetitive** — even LLMs can fall into
  loops. Mueller's mechanical permutation can break the Director out of a
  rut by forcing a different element into the scene.
- **In v0 (no LLM)** — Mueller's mutation is the only mutation mechanism.
- **For structural moves the Director can't make** — the Director produces
  associative, aesthetic mutations. Mueller produces structural ones (swap
  this character for that one, try a different location). Both are useful.

The two mutation mechanisms complement each other. The Director is
associative and wild. Mueller is structural and disciplined. Together
they cover different kinds of stuckness.

### DreamNodes as Attributed Interpretations

With the Director layer, each DreamNode is richer than "a scene." It's an
attributed interpretation:

- **This situation** (from the Dreamer's world state)
- **Read through this goal type** (from the Dreamer's emotional scheduler)
- **Interpreted by this Director lens** (Freudian, Jodorowsky, etc.)
- **Producing this visual** (the Scope prompt)
- **With this narration** (Dreamer whisper and/or Director monologue)

That provenance matters. When the system revisits a situation, it can
revisit it through a different goal type, or through a different Director
lens, or both. Same situation, completely different treatment. That's the
"repetition with variation" phenomenology, but now with a structural
explanation for why the variation happens.

### The Dreamer Could Be Visualized Too

The Dreamer's interior state — activation levels, competing goals, emotional
pressure — could itself have a visual representation, separate from the
Director's imagery:

- Abstract geometry reflecting activation and competition
- Text fragments surfacing and dissolving based on cognitive posture
- A secondary visual layer or overlay — "the mind watching itself think"

In performance mode, only the Director's imagery is visible (full immersion).
In daydreaming mode, the Dreamer's state becomes visible alongside or
underneath the Director's imagery. The audience sees both the dream and the
mind that is dreaming it.

## The Director's Toolkit: Existing Prompt Engineering Work

The team (John and others) has already built the infrastructure that the
Director agent needs. This section maps their existing prompt engineering
work to the Dreamer + Director architecture.

### What Already Exists

The following resources live in the ComfyPromptByAPI workspace:

**Shared Prompt Writing Patterns**
(`ComfyPromptByAPI/WorkingSpace/davidrd/OutsideStruct/PromptWriting/Prompt_Writing_Patterns_Shared.md`)

The general theory of prompt construction, style-agnostic. Key concepts that
directly apply to the Director:

- **Attention Budget & Conceptual Distance.** The further a prompt pushes
  from the model's training distribution, the less room there is for extra
  detail. This is the Director's primary constraint: wildness must stay
  within what the model can render. High conceptual distance on the subject
  → simplify camera, lighting, FX. High conceptual distance on the setting
  → simplify the character action.

- **One Singular Core Idea per prompt.** Each shot is about one thing. The
  Director can be wild, but it must be focused. One novel element per prompt,
  everything else supports it.

- **Thematic Capstone.** End the prompt with a single feeling-sentence:
  "The scene feels like a tense rehearsal of something that hasn't happened
  yet." This is where the Dreamer's goal type becomes legible in the prompt
  — the capstone is the emotional instruction to the model.

- **Directing Performance (emotion + physicality).** Pair an emotion with
  concrete physical cues. Not "he is afraid" but "he draws back a step,
  shoulders tightening, eyes flicking toward the exit." The Dreamer's
  emotional state needs to be translated into body language.

- **Two-Stage VFX (setup → payoff).** Effects work as charge → release.
  This maps directly to the Dreamer's tension/energy dynamics — high tension
  = long setup, release = the switch or the break.

- **Sequential vs Simultaneous action.** One micro-sequence per prompt.
  "First... Then..." for temporal chains, commas for simultaneous. The
  Dreamer's pacing (dwell vs accelerate) maps to how many beats the
  Director packs into a single prompt.

**Numbered Strategy Documents**
(`ComfyPromptByAPI/WorkingSpace/davidrd/OutsideStruct/PromptWriting/Prompt Writing Strategy *.md`)

Nine detailed strategy docs that flesh out specific aspects:

- **Strategy 2: Conceptual Distance.** Dedicate the model's attention budget
  to one difficult thing. Never ask for multiple out-of-distribution elements
  simultaneously. *For the Director: when the Dreamer's state is emotionally
  extreme, the Director can push one visual element far but must simplify
  everything else.*

- **Strategy 2.1: OOD Generalization.** Use synonymic, parsimonious language
  for novel subjects. Weaken strong training biases by avoiding trigger tokens.
  *For the Director: when interpreting the Dreamer's obsessions into unusual
  imagery, avoid tokens that would pull the model toward its defaults.*

- **Strategy 3: Strategic Simplification ("Clear to a Kid Reader").** Strip
  away obvious details. Focus tokens only on what matters. Trust the model.
  *For the Director: the prompt should be as lean as possible while
  capturing the one wild image.*

- **Strategy 4.1: Thematic Capstone.** End every prompt with a feeling-
  sentence that unifies the shot. *This is the Director's translation of the
  Dreamer's goal type into emotional direction for the model.*

- **Strategy 4.2: Aesthetic Control (Cinematography & Style).** Precise
  cinematic vocabulary — dolly, chiaroscuro, teal-and-orange — that the
  model actually responds to. *The Director needs this vocabulary. Different
  goal types could map to different default cinematography: rehearsal gets
  static medium shots, revenge gets low-angle push-ins, roving gets wide
  tracking shots.*

- **Strategy 5: Material & Motion Translation ("Graffito" Lexicon).** Describe
  all objects as tangible, handcrafted props. Ask "how would they build it?"
  *For style-specific Directors (Akira cel animation, stop-motion kaiju, etc.)
  this is the material vocabulary that keeps the Director on-model.*

- **Strategy 6: Character Consistency.** Include full canonical descriptions,
  use explicit negation for non-canonical elements. *For the Director: when
  the Dreamer revisits the same character across different goal types, the
  character must remain visually recognizable even as the treatment changes.*

- **Strategy 7: Directing Performance.** Lead with primary emotion, anchor
  with physical action cues. *Direct mapping from Dreamer state: the goal
  type determines the primary emotion, the Director translates it into
  posture/gesture/movement.*

- **Strategy 8: Active Props & Tangible VFX.** Props are participants, not
  decoration. Effects are physical and staged. *For the Director: the
  Dreamer's obsessive objects (the letter, the gate, the tape) should be
  active participants in every scene, not static background elements.*

- **Strategy 9: Technical Analogy.** Borrow terms from specialized fields
  (mycology, art history, cinematography) to evoke complex visuals efficiently.
  *For the Director: "cracks branching like a Lichtenberg figure" is worth
  more than a paragraph of description. Technical analogies are the
  Director's power tools.*

**Akira Captioning Style Guide**
(`ComfyPromptByAPI/WorkingSpace/davidrd/OutsideStruct/John/Prompting/Akira_CollabPrompting_Guide.md`)

A complete Director voice for a specific aesthetic world (1988 cel animation).
This is the best existing example of what a "Director style guide" looks like
in practice:

- **Visual Grammar** — phrase bank of materials, surfaces, FX
  (FLAT-COLORED CELS, BOLD BLACK INK OUTLINES, OPAQUE PAINTED SMOKE PLUMES,
  GRADIENT AIRBRUSHING, ANAMORPHIC-STYLE LENS FLARE)
- **Lighting Language** — DEEP BLACKS, BRILLIANT HIGHLIGHTS, neon color
  vocabulary
- **Camera and Framing** — approved shot types with specific tokens
- **Motion and Timing** — "animated on ones/twos," impact frames, parallax
- **Canon Tokens** — character identifiers that ensure consistency
- **Palette and Atmosphere** — color narratives for different scene types
- **Negative Space** — explicit list of what to avoid
- **Prompt Scaffolds** — fill-in-the-blank templates for baseline shots,
  action FX, water/reflections, laser effects
- **OOD Strategies** — how to push into novel territory while staying
  on-model

This structure — phrase bank + templates + constraints + OOD rules — is
exactly what every Director style guide should contain.

**Wan Video Model Spec**
(`ComfyPromptByAPI/WorkingSpace/davidrd/OutsideStruct/John/Prompting/WanPromptingSpec.md`)

Model-specific technical vocabulary. Approved keywords for lighting, shot
size, camera movement. Three prompt architecture patterns:

- **Pattern A: Establishing Shot (Setting-First)** — setting → aesthetic →
  subject. Use for roving, drift, environmental scenes.
- **Pattern B: Interaction Shot (Character-First)** — subject → setting →
  aesthetic. Use for rehearsal, revenge, character-driven scenes.
- **Pattern C: Insert Shot (Object-First)** — focal object → context. Use
  for hold states, obsessive focus on charged objects.

The Director can select prompt architecture pattern based on the Dreamer's
current goal type.

**Creative Brief: "That One Friend"**
(`ComfyPromptByAPI/WorkingSpace/davidrd/OutsideStruct/John/Prompting/Creative_Briefs/ThatOneFriend.md`)

A complete creative package for a specific project (Akira meme shorts). Most
interesting as a template for how to structure world + Director instructions:

1. Guiding philosophy (what collision drives the piece)
2. Core creative engine (the formula that generates scenes)
3. Character dynamics (what triggers what)
4. Production design rules (camera, setting, wardrobe)
5. Narrative rules (transformations are permanent within the shot)

For the daydream system, this structure splits into two:
- Items 1-3 feed the **Dreamer** (world bible, situations, triggers)
- Items 4-5 feed the **Director** (style guide, achievability rules)

### How the Director Uses This

The Director is not a freeform creative agent. It's a **constrained** creative
agent. The constraints are:

1. **What the Dreamer is feeling** — goal type, situation, emotion, tension
2. **What the model can render** — attention budget, achievability, OOD limits
3. **What the style guide allows** — phrase bank, templates, negative space

The wildness happens *within* the constraints. The Director can produce
surrealist imagery, but every image must be one clear thing, achievable by
the model, using the approved vocabulary. That's what makes it renderable
rather than just imaginative.

### Dreamer State → Director Decisions

The Dreamer's output maps to specific prompt strategy choices:

| Dreamer State | Director Decision | Strategy Reference |
|---|---|---|
| Goal type: **rehearsal** | Prompt Pattern B (character-first), static medium shot, character's physical state is primary | Strategy 7 (performance) |
| Goal type: **revenge** | Prompt Pattern B, low-angle push-in, aggressive physicality, two-stage FX (charge → strike) | Strategy 8 (active props) |
| Goal type: **roving** | Prompt Pattern A (setting-first), wide tracking shot, atmospheric, minimal character | Strategy 4.2 (cinematography) |
| Goal type: **reversal** | Same location as recent scene but altered — weather, time, lighting shift. Character in opposite posture | Strategy 6 (consistency + variation) |
| Goal type: **rationalization** | Softer lighting, character turning away or withdrawing, capstone: "The scene feels like a quiet justification" | Strategy 4.1 (capstone) |
| Goal type: **repercussions** | Prompt Pattern A, wider shot, cascading environmental change, two-stage VFX | Strategy 8 (tangible VFX) |
| Goal type: **recovery** | Medium shot, gentler motion, character's physical ease returning, warmer lighting | Strategy 7 (performance) |
| **High tension** | Director pushes one element far OOD, simplifies everything else | Strategy 2 (conceptual distance) |
| **Low tension** | Director stays closer to the model's comfort zone, more detail allowed | Strategy 2 (attention budget) |
| **Hold state** | Prompt Pattern C (insert/object-first), extreme close-up, minimal motion, sustained atmosphere | Wan spec Pattern C |
| **Acceleration** | Shorter prompts, one beat only, hard cuts between contrasting shots | Strategy 3 (simplification) |
| **Serendipity trigger** | Director makes an associative leap — novel subject or setting, but using familiar style tokens | Strategy 2.1 (OOD generalization) |

### What the Director Style Guide Should Contain

Based on the existing examples, every Director style guide needs:

1. **Phrase bank** — approved vocabulary for materials, surfaces, lighting,
   camera, motion, FX. These are the building blocks the LLM draws from.
2. **Prompt scaffolds** — templates that guarantee well-formed output.
   At least one scaffold per prompt pattern (establishing, interaction,
   insert).
3. **Canon tokens** — character/object identifiers that ensure consistency
   across scenes.
4. **Negative space** — what to never generate. What breaks the style.
5. **OOD rules** — how to go off-script while staying renderable. Which
   axis to push (subject vs setting vs FX) and which to hold stable.
6. **Achievability rules** — attention budget awareness. What the model
   handles well, what it struggles with, and how to work within limits.
7. **Goal-type mappings** — default cinematography, lighting, and
   performance direction for each of the Dreamer's seven goal types.

### Swappable Directors

Different style guides = different Directors:

- **Akira Director** — 1988 cel animation vocabulary, DDPB backgrounds,
  bold ink outlines, impact frames, neon night palette
- **Surrealist Director** — Jodorowsky-scale imagery, ritual and body,
  sacred geometry, desert and gold
- **Noir Director** — high contrast, venetian blind shadows, rain-slick
  streets, practical lighting, slow push-ins
- **Tarkovsky Director** — water, long takes, rooms that breathe,
  overcast natural light, decaying interiors

Same Dreamer underneath. Same emotional journey. Completely different visual
language. The style guide is the variable. The Dreamer's emotional logic is
the constant.

### What to Ask the Team

In the morning, the most productive conversation with teammates:

1. **"What are your latest style guides? What model are they tuned for?"**
   The Akira guide is great but dated (December). If they've iterated or
   built guides for other models/LoRAs, those are directly usable as
   Director voices.

2. **"Have you thought about automated prompt generation from structured
   input?"** The Director is essentially: take structured state (emotion,
   situation, goal type) → produce an achievable prompt following the style
   guide. If they've built anything like this, even manually, it's relevant.

3. **"What does the model struggle with? Where are the OOD cliffs?"** The
   attention budget concept is crucial. Knowing the model's limits shapes
   what the Director can attempt at different tension levels.

4. **"Could you imagine a style guide structured as Director instructions?"**
   Show them this section. The Akira guide is already 80% of the way there.
   The missing 20% is the goal-type-to-cinematography mapping — the part
   where the Dreamer's emotional state drives specific prompt decisions.

5. **"What would a creative brief for a 'daydream' look like?"** The
   ThatOneFriend brief is a great template. What would the equivalent be
   for a piece that isn't narrative comedy but *feels like being inside a
   mind that is dreaming*? What's the guiding philosophy, the core engine,
   the character dynamics, the production design?

## Open Questions (What Needs Feeling Out)

1. **Does the performance/daydreaming toggle work as a live creative
   parameter?** Can David use the APC Mini to move between immersion and
   introspection in a way that feels musical?

2. **How much of Mueller's goal-type system is actually visible in the
   output?** If the audience can't tell the difference between a rehearsal and
   a reversal, the goal types are metadata, not experience. What makes them
   perceptible?

3. **What does the interior state visualization look like in practice?** This
   can only be answered by prototyping it — trying different treatments and
   seeing what reads.

4. **What situations work for the escape_new_york palette?** The three proposed
   (escape, bargain, betrayal) need to be tested against actual cells. Do
   they carve up the palette in ways that produce interesting traversal?

5. **Does narration work as inner monologue?** The first-person whispered
   narration idea is powerful in concept. Does it actually work alongside
   Lyria's music without competing for attention?

6. **What's the right density?** How often should the system switch situations?
   How long should holds last? How rare should counterfactual jumps be? These
   are rhythm questions, not architecture questions.

7. **What style guide should the first Director use?** Do we build a new one
   for KREA 14B, adapt an existing one (Akira?), or start with a minimal
   one that just covers the basics?

8. **How much latitude does the Director get at different tension levels?**
   At low tension, the Director might stay close to existing palette material.
   At high tension, the Director might push far OOD with wild surrealist
   imagery. What's the right curve?

9. **Can the team's existing prompt engineering pipeline be extended to take
   structured emotional input?** The style guides assume a human writing
   prompts. The Director is an LLM writing prompts from Dreamer state. What
   changes in the workflow?

10. **Does the gap between Dreamer narration and Director imagery actually
    work?** The theory is that dreams don't illustrate their own logic. But
    if the gap is too wide, the narration and video feel disconnected. If
    the gap is too narrow, it feels like an illustrated audiobook. Where's
    the sweet spot?

## How to Continue This Track

This is not a spec. It's a living exploration. The way forward is:

- **Hand-compose a dream sequence.** Manually pick palette cells, goal types,
  transitions, narration lines, and see if the *sequence* feels like
  dreaming. No code. Just creative choices in order.
- **Test on the actual stage.** Play a hand-composed sequence through Scope +
  Lyria and see what works.
- **Let the answers feed back into the engine design.** Whatever rules emerge
  from hand-composing dreams become the rules the v0 scheduler implements.
- **Talk to the team about Director style guides.** Bring the Dreamer +
  Director framing and the goal-type-to-cinematography mapping table. See
  what they think, what existing work applies, what they'd want to build.
