# Injection Context for Running 5 Pro Asks

Paste this into the currently running Ask A / Ask B / Ask C
chats as a follow-up message. It contains intermediate analysis
and current thinking from team discussion.

**Important framing:** This is where our heads are right now,
not what's decided. If you see a better decomposition, a
different naming, a different interface shape, or a reason our
current direction is wrong — say so. We want your independent
analysis, not validation of ours. Challenge anything that
deserves challenging.

---

## For Ask A (inject before or after reply)

An intermediate analysis already mapped the design space. Key
findings worth building on (not repeating):

- **Representation seam** (compiled artifact boundary vs.
  continuous end-to-end generation) is a critical axis we
  hadn't named. Our project sits at the compiled-artifact end
  — authored graph as the membrane between authoring and
  performance.

- **Legibility** is a first-class design axis, not just a nice
  property. Our project requires that inner dynamics be
  *watchable*, not just functional. This means the inner-life
  visualization is load-bearing, not decorative.

- **"Conducted cognitive character" is coherent if defined
  narrowly** as: a steerable instrument where an explicit
  inner-process model produces and/or selects expressive
  material, and the experience includes designed legibility of
  that inner process. It becomes incoherent if it tries to
  simultaneously be a cognitive architecture, an autonomous
  agent, a world simulator, a drama engine, and a performance
  instrument.

- **Core + adapters** decomposition: cognitive core (concerns,
  operators, memory, pressure, reappraisal) is portable.
  Adapters for: (a) authoring-time generation/curation,
  (b) runtime traversal/performance, (c) persistent daemon
  externalizations.

Go deeper on the alternative foundations — especially: what
would the system look like if built on predictive processing /
active inference? On BDI? On LLMs-as-cognitive-engine-directly?
The intermediate table was broad but thin (one sentence per
foundation). We want the actual architectural shape, not just
gain/loss bullets.

---

## For Ask B (inject before or after reply)

### Current team thinking (open to challenge)

**Naming (provisional):** We're currently calling the
authoring-time agent that introduces world events the
**Provocation Generator**, and keeping **Director** for the
runtime feedback agent. If there's a better decomposition or
naming, we want to hear it.

**Interface object (our current proposal — is this right?):**
We think the missing seam is a typed diff against the authoring
fixture, tentatively called **`FixtureDeltaV1`**:

```yaml
FixtureDeltaV1:
  delta_id: str
  source_lane: human | L1 | provocation_generator
  scope: proposal
  adds:
    events:    # id + description + anchor places + tags
    situations: # id + place_id + description + target_ref
                # + current_state seeds + indices
    reference_markers:  # optional
  updates:
    situations:
      - id: existing_situation_id
        current_state_patch: {key: value}
        # NO description_append — prose mutation of canon
        # is forbidden per Q7 analysis. If the situation
        # description changes, add a new situation instead.
  provenance:
    source_ref: str
    notes: str
    confidence: low | medium | high
```

- **Who writes:** Provocation Generator or human
- **Who reads:** generation pipeline + graph compiler
- **Mutability:** append-only proposals during a run; accepted
  deltas get compiled into fixture versions
- **Where it lives:** `fixture_deltas.jsonl` during runs,
  curated YAML under `fixtures/` after acceptance

**Integration sentence (our current model):** "The Provocation
Generator writes FixtureDeltaV1; the pipeline applies it to
produce an updated fixture view, then generates reactions from
that view." Neither system knows about the other. The human
curates both. Is this the right separation, or is there a
reason these should be more tightly coupled?

### Architecture version we're leaning toward (but open to alternatives)

**Version B: Provocation Generator (authoring) + Director (runtime)**

- Authoring loop: Provocation Generator reads brief + world
  state + coverage targets → outputs FixtureDeltaV1 → pipeline
  generates reactions from updated fixture → human curates
- Performance loop: Director reads cycle packet + brief →
  outputs feedback echo (concepts/boosts/valence) → mutates
  next-cycle runtime state (non-canonical)
- These are two separate agents with two separate prompts

An intermediate analysis also proposed Version C (event bank:
authoring-time provocations become a bank that the runtime
Director can trigger with conductor veto/confirm). We deferred
it. Is that right, or should it be pulled forward?

### Open seam question (please address)

When the Provocation Generator introduces a new situation via
FixtureDeltaV1, what happens to Q7's DerivedSituationState
from the previous situation?

- DerivedSituationState = internal character progression
  (letter turned face-down, harbor memory surfaced, ritual
  momentum entrenched)
- FixtureDeltaV1 = external world change (new event, new
  situation — "Eli sends a second text")

Does the Provocation Generator need to see accumulated internal
state to decide what world event to introduce? (Probably yes —
"the character has been avoiding for 3 steps" informs "now the
world pushes back.") Does a new situation from FixtureDeltaV1
reset or modify or carry forward Q7's accumulated state?

### Vocabulary map (intermediate analysis — challenge or refine)

| Component (experiential) | Component (pressure-engine) | Time | Function |
|---|---|---|---|
| Prep | upstream assets (no L-number) | Authoring | World/meaning |
| *(Provocation Generator)* | *(no L-number; writes FixtureDeltaV1)* | Authoring | World event introduction |
| *(Authoring loop tooling)* | L1 | Authoring | Material supply |
| *(Middle-layer conditioning)* | L2 (authoring mode) | Authoring | Cognition-shaped generation |
| Dreamer | L3 + optional live L2 | Performance | Traversal + cognition |
| Director | *(no L-number; perturbation)* | Performance | Interpretive feedback |
| Stage | Adapter + outputs | Performance | Externalization |

Note: "Dreamer spans L2+L3" is true at performance time only.
At authoring time, L2 conditioning exists without L3 traversal.

---

## For Ask C (inject after A + B replies are in)

Additional context for the orientation questions:

- The supply pilot has a practical pass. Keeper yield is proven.
  The bridge tests (Tessa + Kai) both pass. The generation
  pipeline works.

- The biggest current unknown is NOT supply or traversal. It is:
  (a) whether the dynamics are interesting to watch (Level 3 of
  the evaluation ladder — untested), and (b) how the Provocation
  Generator and generation pipeline form a working authoring loop
  (the FixtureDeltaV1 integration).

- The cheapest high-value experiment is probably a minimal
  Provocation Generator → FixtureDeltaV1 → reaction generation
  loop on one existing fixture. This directly tests whether the
  "world event → character reaction" loop produces usable graph
  material.

- A key risk to address: could the LLM generation layer undermine
  the cognitive architecture? The temperature experiment showed
  that sampling parameters + system prompt produced more staging
  diversity than the operator scoring formula. If the LLM can
  produce good material with just a prompt and temperature, why
  maintain CausalSlice and operator scoring at all? (We believe
  the answer is: the cognitive layer provides structural control
  and provenance that temperature can't, but this deserves
  honest scrutiny.)
