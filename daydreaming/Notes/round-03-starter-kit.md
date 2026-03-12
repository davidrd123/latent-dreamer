# Round 03 Starter Kit (Material + Coherence)

This is a practical way to "get ducks in a row" for a coherent daydream system
that plugs into the existing Scope+Lyria stage.

The aim is not to perfect theory. The aim is a small, testable scaffold:

- a source material set
- a minimal situation model
- a traversal policy that produces believable return patterns
- optional narration that makes the cognitive posture legible

## Recommended v0 Material: One Palette

Start with a single palette that already has Lyria-aligned layered prompts:

- `/Users/daviddickinson/Projects/Lora/scope-drd/content/palettes/escape_new_york.v2-layered.palette.yaml`

Why:

- Visual prompts are already tuned to the real-time model.
- Music prompts are already structured as layered `identity/instruments/production`.
- Many cells include suggested `density/brightness/guidance` values.
- The grid itself gives you an immediate "state space" to traverse without asking an LLM.

## Minimal World Overlay (No LLM Required)

Add just enough story pressure to make traversal feel like mind-wandering.

### Situations (3)

Define 3 unresolved situations. These are not plot points; they are gravity wells.

- `s1_escape`: "I need to get out, but the wall is everywhere."
- `s2_bargain`: "Someone offers safety, but it costs something."
- `s3_betrayal`: "A small sabotage changes what the whole place is for."

Each situation has:

- `ripeness` (slow, cross-session pressure)
- `activation` (fast, within-session salience)

### Events (2)

Events are irreversible canonical commits. Keep them rare.

- `e1_cross_the_gate` resolves `s1_escape`, spawns a new situation.
- `e2_drop_the_tape` resolves `s3_betrayal`, flips compatibility of some nodes.

In v0, you can fake events as markers in the log without a full validator.

## Coherence Policy (Traversal Heuristics)

You can get "coherent dreaming" surprisingly far with just 4 rules:

1. **Local continuity by default**
   - Prefer staying near the current cell (grid-neighborhood moves).
2. **Return pressure**
   - Each situation maps to a subset of cells (or just a region by row/col).
   - High activation increases probability of jumping back into its region.
3. **Hold states**
   - When multiple situations are highly activated, enter `hold=true` and dwell longer.
4. **Occasional counterfactual jump**
   - 5–10% of the time, choose a far cell and mark compatibility as counterfactual.

This produces the key phenomenology:

- drift, then return
- repetition with variation
- charged loops

## Narration: Make the Cognitive Posture Legible

Narration should not explain the plot. It should expose the *type of daydream*.

Start with captions only (no TTS). Templates keep this deterministic:

- `rehearsal`: "If I go there again, what do I do differently?"
- `reversal`: "What if I hadn't gone through that door?"
- `rationalization`: "It wasn't my fault. It had already started."
- `roving`: "Anything else. Somewhere quieter."
- `repercussions`: "If I do this, what happens next?"

Tie narration to `goal_type + situation_id + compatibility`.

If it feels good, later swap templates for an offline LLM pass that writes
1–2 lines per node.

## Two Starting Variants

### Variant A: Palette-Only Dreaming (fastest)

- Material: 1 palette
- Output: dream graph nodes reference palette cells
- Narration: templates
- Events: just log markers

This answers: "does it feel like dreaming?"

### Variant B: Palette + Tiny Canon (still small)

- Material: 1 palette + a 20-line `world_state.yaml`
- Output: same, but nodes carry `assumed_facts`
- Events: actually mutate world state and retag compatibility

This answers: "does it accumulate?"

## When To Add LLMs

Only after Variant A feels right.

Good first LLM use:

- offline: generate extra edges + justifications for a hand-authored graph
- offline: rewrite captions into stylized narration

Bad first LLM use:

- live: generate every prompt on the fly

The stage already runs at 20–27 fps; keep the dream layer cheap enough to steer
it without stalling.
