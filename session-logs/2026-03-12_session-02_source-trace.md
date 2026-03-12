# Session 02 — Mueller Source Code Trace

**Date:** 2026-03-12
**Model:** Claude Opus 4.6 (Claude Code)
**Continues from:** session-01 (orientation)

---

## What This Session Did

Detailed source trace of Mueller's DAYDREAMER 3.5 codebase. Read every major file: `dd_cntrl.cl`, `dd_kb.cl` (3874 lines), `dd_epis.cl`, `dd_mutation.cl`, `dd_reversal.cl`, `gate_cx.cl`, `dd_ri.cl`. Also re-read the full Web Opus chat (`2026-03-12_01-01-48...md`) to understand what it got right and where it was too coarse.

---

## Web Opus Chat Summary

The chat at `daydreaming/Notes/ProspectiveDesign/chats/2026-03-12_01-01-48_Claude_Chat_Daydreamer_architecture_split_the_three_systems.md` covered:

1. **GPT-5 critique** (lines 1-260): "Three systems at once" (fictional world, autobiographical vault, biometric instrument). Key fixes: split storage into 5 layers, demote biometrics to modulator, explicit commit mechanic, shrink conductor vector.

2. **Web Opus reads Mueller source** (lines 382-462): Identified 5 key mechanisms to port — goal types, emotion scheduling, threshold retrieval, mutation, critic-selector. Summary: "20% of Mueller's code embodies architectural decisions worth preserving."

3. **Clojure vs Python** (lines 464-560): Real case for Clojure (persistent data structures, Datascript, core.logic, ACL2 path). Recommended split: Clojure engine + Python renderer. But too many unknowns with David's current Clojure fluency.

4. **MCP inversion** (lines 856-1053): Instead of "Clojure engine calls Claude API," flip to "Claude IS the engine, calling MCP servers." MCP servers for world state, graph, tidal/weight system, retrieval. Uses Max plan.

5. **Minsky's Critic-Selector** (lines 736-785): Monitor generation quality, switch strategies when stuck. Restructure conductor state into levels (instinctive/learned/deliberative/reflective/self-reflective).

6. **"Tidal" cleanup** (lines 1054-1081): David pushed back. Renamed to situation weights/ripeness (retrieval concern) vs Lyria parameters (rendering concern).

7. **GPT-5 reviewed consolidated doc** (lines 1142-1210): 7 fixes applied — ripeness/activation split, Claude-as-implementation-not-ontology, explicit commit mechanic, richer compatibility status (7-value enum), conversational residue cleanup, proof goals scoped down, node/edge schemas.

**Where Web Opus was right:** Architectural outline, the 5 key mechanisms, the scheduler simplicity, context tree as ancestor of counterfactual graph.

**Where Web Opus was too coarse:** Compressed motivation into "emotional queue" (it's goal strength, not emotion directly). Treated serendipity as just threshold-lowering (it's also rule intersection). Treated mutation as free-floating creativity (it's coupled to failed planning + serendipity). Named goal types without tracing trigger conditions.

---

## Detailed Source Trace Results

### 1. Scheduler (`dd_cntrl.cl`)

**Main loop** (`daydreamer-control0`, line 151):
```
loop:
  need-decay()          # multiply all needs by 0.98
  emotion-decay()       # multiply non-motivating emotions by 0.95, GC below 0.15
  candidates = most-highly-motivated-goals()
  if no candidates:
    toggle mode (performance <-> daydreaming), increment strikes
    if 3 strikes: terminate
  elif current TLG still in candidates:
    run one step (daydreamer-control1)
  elif one candidate:
    switch to it
  else:
    break tie randomly
```

**Key details:**
- Scheduling is on **goal strength**, not emotion directly. Goals have `strength` slot; emotions contribute via dependency links with weights.
- `most-highly-motivated-goals` (line 250): filters for `runable` status + (daydreaming-mode OR non-imaginary planning-type). Returns list of goals tied at highest strength.
- Mode toggle is bidirectional — daydreaming exhaustion → performance mode (unhalts waiting real goals).
- "One step" = `daydreamer-control1` (line 300): fire rules in current context, check success, if no sprouts then backtrack.
- Emotion decay factor: 0.95. Need decay factor: 0.98. Emotion GC threshold: 0.15.
- Only **non-motivating** emotions decay (line 221). If an emotion is linked to an active goal, it stays.

**Top-level goal slots** (documented at line 629):
- `status`: runable | halted | waiting
- `planning-type`: real | imaginary
- `backtrack-wall`: (imaginary only) root context for this goal's planning
- `next-context`: (imaginary only) next context to run
- `mutation-plan-contexts`: ideas for new plans from mutation
- `run-mutations?`: t/nil
- `main-motiv`: main motivating emotion
- `activation-context`: where goal was first activated
- `top-level-goal`: self-reference for TLGs, parent TLG for subgoals

### 2. Seven Goal Types — Type Hierarchy and Trigger Conditions (`dd_kb.cl`)

**Type hierarchy** (lines 169-180):
```
DD-GOAL-OBJ
├── FANCIFUL-GOAL-OBJ
│   ├── RATIONALIZATION (prop: obj)        — pure imagination
│   ├── REVENGE (prop: actor, to, obj)     — pure imagination
│   └── ROVING (prop: obj)                 — pure imagination
└── REALISTIC-GOAL-OBJ
    ├── RECOVERY (prop: obj)               — could lead to real action
    ├── REHEARSAL (prop: obj) [SKIPINDEX]   — anticipatory practice
    ├── REVERSAL (prop: obj) [SKIPINDEX]    — counterfactual replay
    └── REPERCUSSIONS (prop: obj) [SKIPINDEX] — "what if this happened to me"
```

SKIPINDEX: don't index the top-level episode directly; index the subgoal's episode instead.

**Trigger rules (exact conditions):**

| Goal Type | Rule Name | Line | Trigger Condition | Supplemental Emotion | What It Does |
|-----------|-----------|------|-------------------|---------------------|--------------|
| RATIONALIZATION | Rationalization-Theme | 2140 | DEPENDENCY(Failed-Goal, NEG-EMOTION > 0.7) | NEG-EMOTION 0.06 | Find silver lining: success→failure or failure→success via LEADTO |
| REVENGE | Revenge-Theme | 2492 | DEPENDENCY(Failed-Goal, NEG-EMOTION with `to` field) | NEG-EMOTION 0.05 | Make Other have same relationship failure |
| ROVING | Roving-Theme | 2064 | DEPENDENCY(Failed-Goal, NEG-EMOTION > 0.7) | NEG-EMOTION 0.04 | Recall random pleasant episode (distraction) |
| REVERSAL | Reversal-Theme | 2731 | DEPENDENCY(Failed-Goal [inferred TLG], NEG-EMOTION > 0.5, < learn-thresh) | NEG-EMOTION 0.03 | Replay planning tree with different choices |
| RECOVERY | Recovery-Theme | 3201 | DEPENDENCY(Failed-Goal, NEG-EMOTION > 0.5, < learn-thresh) | NEG-EMOTION 0.02 | Try to achieve the failed goal again directly |
| REHEARSAL | Rehearsal-Theme | 3242 | ACTIVE-GOAL in WAIT + POS-EMOTION > 0.5 linked to TLG + no vars | POS-EMOTION 0.01 | Re-plan the objective (mental practice) |
| REPERCUSSIONS | Repercussions-Theme1 | 3286 | External threatening event (earthquake example) | NEG-EMOTION 0.7 | Hypothesize state sequence, run inferences |

**Supplemental emotion strengths create default priority:**
RATIONALIZATION (0.06) > REVENGE (0.05) > ROVING (0.04) > REVERSAL (0.03) > RECOVERY (0.02) > REHEARSAL (0.01)

**Critical: triggers overlap deliberately.** The same failure activates RATIONALIZATION, REVENGE (if directed anger), ROVING, REVERSAL, and RECOVERY simultaneously. They compete for attention via strength. Not a state machine — a priority queue.

**REHEARSAL is the odd one out:** Driven by *positive* emotion on an *active waiting* goal. Anticipatory, not reactive.

**Rationalization has two strategies:**
- Plan1 (line 2165): "success LEADTO failure" — find something good that led to the bad thing
- Plan2 (line 2192): "failure LEADTO success" — the bad thing will lead to something good
- Success detected by Rationalization-Inf1 (line 2177): NEG-EMOTION drops below 0.3 OR POS-EMOTION appears. **Rationalization literally reduces emotional charge.**

**Reversal does actual counterfactual replay** (`dd_reversal.cl`):
- Takes the failed goal's planning tree
- Walks the path from activation to termination
- At each branch point, sprouts alternatives (contexts that were NOT taken)
- Also finds leaf subgoals with low realism (shaky assumptions) and replans without assuming them

**Threshold functions:**
- `greater-rat-activate?`: emotion strength > 0.7 (for rationalization/roving)
- `greater-reversal-activate?`: emotion strength > 0.5 (for reversal)
- `greater-recovery-activate?`: emotion strength > 0.5 (for recovery)
- `greater-rehearsal-activate?`: emotion strength > 0.5 (for rehearsal)
- `emot-less-learn-thresh?`: overall emotional state magnitude < 2.0 (prevents reversal/recovery when overwhelmed)
- `less-rat-success?`: emotion strength < 0.3 (rationalization succeeded)

### 3. Episode Indexing and Threshold Retrieval (`dd_epis.cl`)

**Storage** (`epmem-store`, line 94):
- Each episode stored under one or more indices
- Two independent thresholds per episode: `plan-threshold` (for planning retrieval) and `reminding-threshold` (for spontaneous recall)
- Thresholds increment with each new index added
- Hidden episodes get infinite threshold (never spontaneously recalled)

**Retrieval** (`epmem-retrieve1`, line 154):
```
mark-init()  # clear all marks
for each index in [active_indices]:
  for each episode stored under index:
    if not recently-recalled:
      marks[episode] += 1
      threshold = episode.threshold - (1 if serendipity else 0)
      if marks >= threshold:
        add to result
```

**Active indices:** FIFO queue, max 6 entries (line 234). Old indices fade when new ones enter.

**Recent episodes:** List, max 4 entries (line 284). Recently recalled episodes can't be retrieved again.

**Emotion indices** (`get-emotion-indices`, line 228): If overall emotional state > 1.0: add POS-EMOTION as index. If < -1.0: add NEG-EMOTION. This means emotional state itself primes retrieval.

**Reminding cascade** (`epmem-reminding`, line 304): When an episode IS recalled:
1. Add to recent episodes
2. Add its other indices to active-index queue → primes retrieval of related episodes
3. Reactivate its emotions in reality context
4. Run serendipities on its inaccessible planning rules
5. Call `remindings()` to check if newly active indices retrieve more episodes
→ Creates chaining: recalling one episode can trigger recall of related ones.

**Similarity metric** (`ob$similarity`, line 615): Structural comparison of GATE objects — type distance + recursive slot comparison. Not embedding-based. Returns float. Used in analogical plan adaptation.

### 4. Serendipity / Rule Intersection (`dd_ri.cl`)

**Two distinct mechanisms:**

**A) Threshold-based serendipity** (in `dd_epis.cl`): The `serendipity?` flag lowers retrieval threshold by 1 during object-input-triggered retrieval. Simple coincidence detection.

**B) Rule intersection** (in `dd_ri.cl`): Bidirectional graph search through the rule network.

**How rule intersection works** (`rule-intersection`, line 362):
```
Given: top-rule (from current goal's backward-chain)
       bottom-rule (from new input's forward-chain)

Bidirectional BFS:
  - Backward from top-rule: follow backward-chain-nums
  - Forward from bottom-rule: follow forward-chain-nums
  - At each step: check if rule appears in known episodes (ri-useable-rule?)
  - When paths meet: chain of rules = potential analogical plan

Verify (rip-paths->episode, line 519):
  - Attempt to instantiate rule chain as actual plan
  - Unify variables along the chain
  - If type-checks: create analogical episode, apply plan
```

- Max depth: 5 (`*max-ri-depth*`, line 347)
- Max paths found: 3 (`*max-paths-found*`, line 348)
- Max paths tried: 3 (`*max-rip-paths*`, line 349)
- For mutations: favor shorter paths. Otherwise: favor LONGER paths (richer analogies preferred, line 540-545)

**When serendipity fires** → `surprise()` (line 300): generates POS-SURPRISE or NEG-SURPRISE emotion (strength 0.25), diverts it to the goal's main motivator, and unhalts the goal. This emotional boost pushes the serendipitous goal up the scheduling priority.

**Key coupling:** Rule intersection can only traverse rules that appear in known episodes. You can't serendipitously use a rule you've never seen succeed. Episodes are the memory of what works.

**For personal goals** (`new-analogical-pers-goal-plan`, line 201): clobbers existing plans, creates new context, sets up analogical episode. For daydreaming goals (`new-analogical-dd-goal-plan`, line 220): adds the new plan alongside existing possibilities.

### 5. Mutation (`dd_mutation.cl`)

**When it fires:** Only when `all-possibilities-failed` for an imaginary goal in daydreaming mode (`dd_cntrl.cl:593-611`).

**`action-mutations`** (line 27):
1. Only runs once per TLG (`run-mutations?` flag, line 29)
2. Walk leaf contexts of backtrack wall
3. Find ACTIVE-GOALs whose objectives are ACTION types
4. Apply pattern-matched mutations from `*mutations*` list
5. **For each mutated action: run `serendipity-recognize-apply`**

**Mutation → Serendipity is a pipeline:** Mutate an action, then check via rule intersection whether the mutated action connects to known episodes. Not a standalone "expand the graph" mechanism.

**Three mutation types** (in unused code, lines 99-267):
- **Type mutation:** PTRANS→MTRANS→ATRANS (change mode: physical movement → mental transfer → abstract transfer)
- **Permutation mutation:** swap participants
- **Substitution mutation:** replace specific objects with wildcards (`some-object`)

**The active mutation** uses pattern-matched substitutions from `*mutations*` (defined elsewhere), then calls `action-mutation` (line 71) which tries serendipity from the supergoal first, then from the top.

### 6. Context System (`gate_cx.cl`)

**`cx$sprout`** (line 32): Creates child context:
- Copies parent's `all-obs` list (all facts visible)
- Copies parent's `type-hashing` (for fast type-based lookup)
- Sets ancestors chain
- Inherits `mutations-tried?`, `timeout` (decremented by 1), `pseudo-sprout?`, `gen-switches`, `touched-facts`

**`cx$assert`** (line 279): Adds fact to context's `add-obs` and `all-obs`. Type-hashes it. Generates English if gen-stream active.

**`cx$retract`** (line 316): If fact was added in this context, removes from `add-obs`. If inherited, adds to `remove-obs`. Unhashes.

**`cx$retrieve`** (line 388): Pattern-matching retrieval against all-obs (or type-hash subset), using GATE unification.

**Context-sensitive links** (line 540+): `DEPENDENCY` and `INTENDS` links are asserted in contexts. `ol-get` traverses links filtering by context visibility. This means links can be context-local.

**Pseudo-sprouts** (line 89): Used by reversal. Looks like a child but doesn't inherit facts from parent. Effectively a root context that claims to be a descendant (for ancestor checks).

---

## What This Means for the Adaptation

### What ports cleanly to the engine:
1. The scheduler loop: decay → select most motivated → run one step → repeat
2. The goal type hierarchy (fanciful vs realistic, the 7 types)
3. The emotion-driven priority mechanism (supplemental strengths, decay, GC)
4. The mode toggle (daydreaming ↔ performance)
5. The overlapping-trigger design (same failure → multiple competing goal types)

### What needs redesign, not porting:
1. **Rule intersection serendipity** has no LLM analog. The rule network doesn't exist. Needs replacement: probably multi-signal coincidence counting + graph-walk, not just embedding similarity.
2. **Mutation is coupled to failed planning + serendipity**, not free-floating. In adaptation: "LLM scene got rejected → mutate one element → try again with constraint noted." More like constrained retry.
3. **The GATE object system** (types, unification, pattern matching) is replaced by LLM structured output + validator.
4. **Episode storage** needs both the threshold-counting mechanism AND some form of structural retrieval (not just embeddings).
5. **Reversal** (counterfactual replay of planning tree) requires having a planning tree to replay. In adaptation: this becomes generating alternative scenes at decision points in the graph.

### What was missing from all prior analysis:
1. The fanciful/realistic distinction in goal types
2. The specific emotional strengths that create priority ordering
3. The two-mechanism nature of serendipity (threshold + rule intersection)
4. The mutation→serendipity pipeline coupling
5. REHEARSAL being positive-emotion-driven, not negative
6. The mode toggle being bidirectional
7. Rationalization literally reducing emotional charge as its success condition

---

## Next Session Should Do

**Write the adaptation spec.** We now have the complete source trace. The deliverable:

1. **The adapted control loop** — cycle by cycle, what runs, what calls the LLM, what's deterministic
2. **The 7 daydream goal types mapped to scene generation** — trigger conditions adapted for world-bible situations, what each type means for video/music/narration output
3. **The data structures** — situations (replacing goals), emotional weights (replacing emotions), activation/ripeness (the split from GPT-5 review)
4. **Where LLM calls go** — replacing GATE rules/unification with structured LLM generation
5. **Retrieval design** — how to approximate threshold + rule-intersection with embeddings + graph structure + multi-signal counting
6. **Mutation design** — constrained retry when generation is stuck, not free expansion
7. **How output maps to Scope REST API** — prompt, k-SAE deltas, Lyria prompt, transition type
8. **Narration channel** — goal type determines tone (rationalization: "it wasn't that bad...", reversal: "if only I had...", rehearsal: "what if I said...")
9. **Minimal world bible** — what needs to exist for the engine to run

---

## File Locations Reference

| What | Where |
|------|-------|
| This session log | `daydream-round-03/session-logs/2026-03-12_session-02_source-trace.md` |
| Previous session log | `daydream-round-03/session-logs/2026-03-12_session-01_orientation.md` |
| Mueller source | `daydream-round-03/daydreamer/` |
| Web Opus chat (Mueller analysis) | `daydream-round-03/daydreaming/Notes/ProspectiveDesign/chats/2026-03-12_01-01-48_...md` |
| v2 architecture doc | `daydream-round-03/daydreaming/Notes/ProspectiveDesign/v2/conducted-daydreaming-architecture-v2.md` |
| Scope-drd (existing instrument) | `/Users/daviddickinson/Projects/Lora/scope-drd/` |
| Claude memory | `~/.claude/projects/-Users-daviddickinson-Projects-Lora-daydream-round-03/memory/` |
