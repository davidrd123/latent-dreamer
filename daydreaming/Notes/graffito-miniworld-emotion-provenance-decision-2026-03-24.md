# Graffito Miniworld Emotion Provenance Decision

Last updated: 2026-03-24

This is a short support note about one specific question:

Should the Graffito miniworld be pushed toward the full kernel control
cycle right now, or should it remain a hybrid assay and first make its
emotion projection more honest?

---

## Diagnosis

The real kernel control loop has live emotion decay in:

- `kernel/src/daydreamer/control.clj`
  - `emotion-decay-factor` = `0.95`
  - `emotion-decay`

The Graffito miniworld does not currently use that path.

Instead:

- `build-world` seeds fixed scene emotions each cycle
  - street `0.82`
  - apartment `0.79`
  - mural threat/challenge `0.87 / 0.74`
- `rebuild-world-with-state` calls `build-world` fresh each cycle
- `run-miniworld-cycle` does not call `control/run-cycle`
- Tony-state and situation activation/ripeness decay
- projected context emotions do not

So the current issue is real:

**some emotion facts in the Graffito trace look like live affect, but
are actually benchmark-projected scene anchors.**

This is not evidence that the kernel’s emotion model is broken.
It is evidence that the benchmark’s projection boundary is now
interpretively load-bearing.

---

## Decision

Do **not** force the miniworld through the full kernel control cycle
yet.

Do **not** treat “miniworld does not run `control/run-cycle`” as the
main bug.

Instead:

**keep the miniworld as a hybrid domain assay, and first make projected
emotion strengths derived rather than static.**

That is the smallest correction that:

- makes the trace more honest
- preserves the miniworld as a meaningful assay
- avoids prematurely collapsing benchmark-local world logic into the
  kernel

---

## Why this is the first move

The miniworld is still supposed to be a benchmark harness, not a full
world runtime.

Benchmark-local logic is still acceptable for:

- situation definitions
- world-specific candidate weighting
- situation cycling
- Graffito-specific appraisal projection

The current problem is narrower:

- projected emotions are static enough to read as decoration rather
  than operators

So the right first standard is:

**state should operate, not just label.**

If the projected emotions derive from carried Tony-state and current
situation activation, then they become part of the actual mechanism
instead of painted scenery.

---

## Fix 1: Shape

### Goal

Replace fixed projected scene-emotion strengths with derived strengths.

### Targeted sites

- `project-mural-appraisal`
- `street-reversal-facts`
- `apartment-support-facts`
- any canopy/export field that currently labels these as plain
  `emotional_state`

### Shape

Introduce small benchmark-local derivation helpers, for example:

- `street-emotion-strength`
- `apartment-emotion-strength`
- `mural-threat-strength`
- `mural-challenge-strength`

These should be computed from:

- current Tony-state
  - `:sensory-load`
  - `:entrainment`
  - `:felt-agency`
  - `:perceived-control`
- current situation activation / ripeness
- current appraisal mode where relevant

### Intended semantics

- street fear should rise when Tony is more overloaded / less in
  control and the street situation is more active
- apartment shame / support pressure should vary with current agency,
  control deficit, and apartment ripeness
- mural threat / challenge should vary not only in kind
  (`threat` vs `challenge`) but also in strength, based on Tony-state
  and mural activation

### Important constraint

This is still benchmark-local projection.

That is acceptable for now.
The point is not to pretend these are kernel-native emotions.
The point is to stop pretending they are static scene constants.

---

## Provenance cleanup

Alongside Fix 1, debug/canopy presentation should make provenance
explicit.

Suggested categories:

- `kernel-persistent`
- `benchmark-carried`
- `benchmark-projected`
- `derived`

At minimum, projected Graffito emotions should not be displayed in a way
that implies they are identical to carried, decaying kernel emotion
state.

---

## What Fix 1 should answer

After Fix 1, the question becomes:

Do the miniworld dynamics still read as interesting and honest when
emotion strength is derived from live carried state instead of fixed
scene anchors?

If yes, stop there for now.

If no, then the next move is not “full kernel control loop.”
It is a second benchmark-local step.

---

## Fix 2: Later, if needed

If derived projection is still too staged, add explicit carried
benchmark-local emotion state with decay across rebuilds.

That would mean:

- benchmark-local emotion table
- per-cycle decay / refresh
- projection from carried emotion state into contexts
- clearer separation between:
  - carried affect
  - projected appraisal facts

This would be a stronger simulation of the kernel’s intended semantics,
while still keeping the miniworld a hybrid assay.

---

## What we are explicitly not doing yet

- not routing the miniworld through `control/run-cycle`
- not forcing all Graffito world logic into the kernel
- not treating this as evidence that the kernel’s control loop is wrong
- not broadening the architectural question beyond this first boundary

The first job is simply:

**make the current assay honest before deciding whether to absorb more
of it into the kernel.**
