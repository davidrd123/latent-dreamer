# Graffito Kernel Brief

Purpose: a short kernel-facing brief for Graffito as a psychological
world, not just a renderer brief or shot list.

This note complements, rather than replaces:

- `18-graffito-vertical-slice.md`
- `19-graffito-node-schema.md`
- `21-graffito-v0-playback-contract.md`

Those docs are about slice scope, node shape, and playback seams.
This doc is about what the kernel should preserve if Graffito becomes
the first real creative brief for conducted daydreaming.

## Core Thesis

Graffito is not mainly "a graffiti fantasy with a magic can."
It is a story about a child whose overwhelm becomes agency through
creation.

Tony's sensory overload, nonlinear attention, and "crooked" making are
not side color. They are the central mechanism of the story:

- what looks like liability is actually latent power
- control arrives through flow, rhythm, and trust rather than force
- the magical world externalizes the same conflicts already present in
  family life, street life, and artistic identity

If the kernel flattens Graffito into generic `:threat`, `:hope`, and
`spray-can magic`, it misses the brief.

## The Human Center

The real dramatic center is a triangle:

- Tony: gifted, overwhelmed, observant, still learning what his
  difference means
- Monk: artist, father, absent/present, charismatic, hiding, divided
  between mission and care
- Grandma: boundary, wisdom, skepticism, protection, memory

This triangle should stay live in the situation model. The fantasy
layer does not replace it.

## What The Story Is About

The recurring pressures in Graffito are:

- authenticity versus performance
- mission versus self-escape
- hiding versus being seen
- danger versus flow
- inheritance versus self-discovery
- regulation versus overwhelm

The script keeps returning to these through concrete scene logic:

- Tony is mocked, startled, and overloaded
- Monk turns art into dance and mission, but also disappears
- Grandma frames Monk's searching as inward rather than heroic
- cops arrive as light, sound, and pressure rather than full explicit
  characters
- the can works only when Tony stops forcing and starts flowing
- Motherload's reframe is the same one the real world is asking:
  "what is wrong with you is exactly what's right with you"

## Kernel-Level Implications

Graffito needs a richer situation model than a flat pressure map.
The right next step is not full simulation. It is a typed fact-space
plus a small carried character state, with derived pressure.

Each situation should carry at least:

- present actors
- relationship facts
- role and obligation facts
- exposure and surveillance facts
- artifact-state facts
- person-object relation facts
- recent event facts
- sensorimotor inputs and affordances
- cross-layer correspondence facts

Each scene should also be read through a small carried character state.
For Tony, that should start with:

- `:sensory-load`
- `:entrainment`
- `:felt-agency`
- `:perceived-control`

Those variables belong to Tony's state in the world, not to the
situation itself. Situations provide the inputs. Tony carries the
processing across scenes.

The appraisal summary still matters. The current family scheduler can
continue to read values like `:threat`, `:hope`, `:anger`, `:grief`,
and scheduler state like `:waiting`. But those should increasingly be
computed from situation facts plus carried character state instead of
hand-authored as isolated gauges.

The point is not to add a new family for regulation. The point is to
let family execution change appraisal-relevant character state, then
re-run appraisal on the same situation. Rationalization changes
meaning. Rehearsal changes control and agency. The same mural crisis
should be able to read as threat before support and challenge after
support, without inventing a new stimulus.

## Minimal Situation Schema V1

### 1. Present Actors

Who is actually in the scene or immediately pressing on it.

Examples:

- Tony is with Monk
- Grandma is present
- cops are not visually present but are bearing down through lights and
  commands

### 2. Relationship Facts

Persistent interpersonal structure.

Examples:

- Tony trusts Monk
- Grandma distrusts Monk's mission logic
- Monk feels responsible for Tony
- Tony seeks recognition from Monk

### 3. Role And Obligation Facts

What each person thinks they are supposed to do.

Examples:

- Monk is trying to make a difference
- Grandma is protecting Tony
- Tony wants to join the mission
- Tony is supposed to go to bed

### 4. Exposure And Surveillance Facts

Who is watching, judging, threatening, or closing in.

Examples:

- the street is socially exposing
- the cops are converging
- Monk is laying low
- Tony is visible and vulnerable

### 5. Artifact-State Facts

Objects with symbolic and practical force.

Examples:

- the old spray can is shared inheritance
- the can is inert, then unstable, then world-making
- the sketchbook is a regulation tool
- the mural is becoming a portal surface

### 6. Recent Event Facts

What just happened that the branch is metabolizing.

Examples:

- Tony got mocked by older kids
- Tony received the "He's Back" message
- Monk returned home
- Grandma challenged Monk's motives
- cops interrupted the mural

### 7. Person-Object Relation Facts

How characters currently stand with the objects that regulate or
externalize thought.

Examples:

- Tony trusts the can as inheritance even when he cannot use it yet
- the sketchbook compresses a too-large scene into workable form
- Monk is co-regulating Tony through movement and rhythm

### 8. Sensorimotor Inputs And Affordances

What the scene is doing to Tony's body and what it affords in return.

Examples:

- the light jolt floods attention
- siren pulse hits the body
- noise fragments precision
- rhythm is available through Monk's movement
- the wall and can offer a channel for control if Tony can stabilize

### 9. Cross-Layer Correspondence Facts

Typed counterpart structure across baseline, mythic, and magic
registers.

Examples:

- Grandma is a counterpart of Motherload
- school is a counterpart of the castle
- the can corresponds to Tony's sensory capacity

### Character State

What Tony is carrying from one scene into the next.

Examples:

- sensory load remains high after school release
- entrainment rises after apartment movement with Monk
- felt agency rises before the mural return
- perceived control is low in the street and higher after co-regulation

Readable labels like `:overloaded`, `:entraining`, or
`:challenge-dominant` should be derived from this state plus the
current situation, not stored as the primitive facts.

### 10. Derived Appraisal Summary

Not hand-authored by itself, but available for scheduling after reading
the situation through the current character state.

Examples:

- threat rises from cop convergence and social exposure
- hope rises from reunion, dance, and co-creation
- grief rises from Monk's instability and disappearance
- waiting/imminence rises in the bedtime and hiding beats

## First Graffito Situation Set

The current broad scaffold still looks right, but it should be
understood in kernel terms:

1. Street / school release
   Sensory overload, mockery, searching, exposure.

2. Grandma's apartment
   Warmth, co-creation, family tension, mission dispute.

3. Bedtime / legend transfer
   Inheritance, myth, hidden history, role invitation.

4. Night crisis at the mural
   Flow, teaching, police pressure, terror, threshold moment.

5. Space / emergence
   Wonder, disorientation, first constructive agency.

6. Motherload world
   Reframe, quest, crookedness, identity, trust.

7. Return through the door
   Rescue, authorship, agency, re-entry into danger with new power.

## What The Kernel Should Not Flatten

- Cops should not become merely a generic antagonist faction. In the
  story they function as pressure, light, sound, and capture.
- Motherload should not become a random dragon quest enemy. She is a
  crooked mirror, a gatekeeper, and effectively a Grandma-shaped
  reframe force.
- The can should not become a generic magic item. It is inheritance,
  trust, transformation, and artistic agency.
- Tony's overwhelm should not be represented only as high threat. It is
  also associative richness and the basis of later creation.
- Monk should not be reduced to "artist dad." His conflict is mission,
  absence, charisma, hiding, and failed integration.

## Family-Level Reading

Graffito already suggests real family uses:

- `:reversal`
  Overwhelm, near-miss, cop-light pressure, and "what if this could be
  turned?"

- `:rationalization`
  Grandma's and Motherload's reframes; Monk's stories; Tony's movement
  from defect-story to gift-story.

- `:roving`
  Escape into the can-opened world, but not as pure whimsy. The fantasy
  space should still carry unresolved real-world structure.

- `:rehearsal`
  Mime-spraying, shaky mountains, practicing flow, trying to make the
  can work before it truly does.

These should stay tied to story tensions, not become abstract operator
labels detached from the brief.

## Best Source Stack

If a future agent needs to re-center on Graffito quickly, start with:

- `ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/Script/script.md`
- `ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/Script/shots.md`
- `ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/Mark/scenes2_and_3.md`
- `ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/Mark/scenes3_and_4.md`
- `ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/Feedback/D_Notes/08-23-25.md`
- `ComfyPromptByAPI/SourceMaterial/Contexts/Graffito/Feedback/VideoRecording/08-17-25/Segments/S03.md`

Those are the fastest path back to the real psychological and visual
center.

## Immediate Use

This brief is most useful for:

- designing a Graffito situation schema richer than flat pressure maps
- deciding what facts a future Graffito benchmark or pilot world should
  carry
- keeping family planning grounded in the actual brief
- preventing the project page or architecture notes from treating
  Graffito as a generic "stylized short film" rather than a specific
  psychological world
