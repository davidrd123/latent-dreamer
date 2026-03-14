# Mueller Ch. 7 — Kernel Procedures Extraction

Source: Mueller, "Daydreaming in Humans and Machines," Ch. 7:
Implementation of DAYDREAMER

Source file: `Notes/Book/daydreaming-in-humans-and-machines/
07-implementation-of-daydreamer.md`

Purpose: Extract the actual procedural shape of DAYDREAMER for
the L2 kernel refactor and shared infrastructure design.

---

## The top-level control loop

```
1. Invoke inference rule application (to get things started)
2. Select concern with highest emotional motivation
3. Invoke planner on that concern
4. Go to step 2
```

Mode switching: In performance mode, only personal-goal concerns
are eligible. In daydreaming mode, both personal and daydreaming
goals are eligible. The program switches modes when no concerns
are eligible in the current mode.

On each cycle: needs and non-motivating emotions decay. Emotions
below threshold are removed.

This is simpler than the kernel's current control.clj but
matches its shape. The critical thing the kernel is missing:
**the decay + threshold removal on every cycle**, which is what
prevents the system from getting stuck on exhausted concerns.

---

## Concern initiation (theme rules)

Concerns are initiated by **inference rules** (called "themes"):

```
IF: activation-condition is true in context
THEN: create new concern with top-level goal
      create/attach motivating emotion with specified strength
```

Two cases:

1. **Daydreaming goal concern activated by existing emotion:**
   The negative emotion from a failure already exists. It
   becomes the motivating emotion for the new concern (e.g.,
   anger → REVENGE concern).

2. **Personal goal concern motivated by new emotion:**
   A new positive emotion is created with strength equal to the
   goal's intrinsic importance.

**Key insight for the kernel:** Concern initiation is rule-driven,
not ad-hoc. Each concern type has a theme rule specifying exactly
what conditions trigger it and what initial emotion motivates it.
The kernel's current goal activation is close but doesn't
formalize the theme-rule pattern.

---

## The two kinds of concerns

Mueller makes a critical distinction the kernel needs to respect
more sharply:

**Personal-goal concerns** modify the reality context through
planning. They proceed "horizontally" — each step sprouts from
and replaces the reality context.

**Daydreaming-goal concerns** never modify reality directly.
They sprout "vertically" off of past or present reality contexts
into isolated hypothetical contexts. The only thing they write
back to reality is: (a) new top-level goals, and (b) the
eventual outcome (success/failure) of the daydreaming goal.

This maps directly to doc 11's commit types:
- Personal-goal concerns → `ontic` commits
- Daydreaming-goal concerns → `policy` and `salience` commits

---

## Contexts: two jobs, one mechanism

Mueller uses contexts (GATE sprouting) for two purposes:

1. **Backtracking:** When a planning branch fails, walk up the
   context tree to find unapplied alternatives. Depth-first
   search through the context space.

2. **Trace retention:** Abandoned alternatives are kept as
   episodes for future analogical planning. This is why a simple
   stack-based undo won't work — you need to *remember* the
   abandoned branches, not just discard them.

Mueller explicitly acknowledges the weakness: "Using contexts
it is difficult to reason about situations in multiple contexts"
(citing Hewitt 1975). You can't easily compare facts across
branches or assert relationships between contexts.

**This is the exact seam where ATMS enters.** ATMS solves the
multi-context reasoning problem by giving every fact a label
showing which assumption sets support it. The kernel's current
add-obs/remove-obs model is closer to Mueller's contexts than
to ATMS — and it has the same limitation.

---

## Serendipity recognition (the full mechanism)

This is much more concrete than "a bias." The actual procedure:

```
1. Find a top rule T whose antecedent matches a goal G of
   the concern

2. Find a bottom rule B:
   - If given an episode: B is a rule contained in the episode
   - If given a concept: B is a rule whose consequent matches
     the concept

3. Perform intersection search from T to B in the rule
   connection graph
   - Up to 3 paths
   - Maximum length 8
   - Works bidirectionally (down from T, up from B)
   - Cycles allowed (rule may occur at most twice)

4. If path found:
   (a) Verify by propagating bindings through the path
   (b) If verification succeeds:
       - Construct an episode (planning tree) from the path
       - Sprout a new context
       - Add constructed episode as analogical plan
       - Generate surprise emotion → boosts concern motivation
```

### Five triggers for serendipity recognition

| Trigger | When | Input |
|---------|------|-------|
| Input-state-driven | External state/action received | All concerns × given concept |
| Object-driven | Physical object received | All concerns × episodes retrieved from object's indices |
| Concern-activation-driven | New concern created | New concern × all recent episodes |
| Episode-driven | Episode retrieved (reminding) | All concerns × that episode |
| Mutation-driven | Action mutated | Current concern × mutated action |

**Key insight:** Mutation is subordinate to serendipity. Mueller
mutates actions, then uses serendipity recognition to decide
whether the mutation is worth keeping. A mutation only counts
if the intersection search finds a verifiable path from the
mutated action to a concern. This is a much stronger pattern
than "mutate and score later."

**Implication for the kernel:** The kernel's current serendipity
is a scalar bias (0.0-0.35). Mueller's serendipity is explicit
graph search in the rule-connection space plus path verification.
The kernel needs:
1. A rule-connection graph (which rules connect to which)
2. Bidirectional intersection search
3. Path verification via binding propagation
4. Surprise emotion generation on success

---

## Reminding (the associative stream)

The reminding procedure is richer than simple retrieval:

```
1. If episode already in recent-episodes list, exit
2. Add episode to recent-episodes list
3. Reactivate emotions associated with episode
4. For each index of episode not already in recent-indices:
   (a) Add index to recent-indices
   (b) Invoke episode retrieval on that index
   (c) Invoke reminding recursively on each retrieved episode
5. Invoke serendipity recognition for all concerns × episode
```

**Key properties:**

- **Emotion reactivation:** Retrieved episodes bring back their
  associated emotions. This is the mechanism by which a memory
  can change the character's current emotional state.

- **Index expansion:** When an episode is retrieved, its *other*
  indices are added to the recent-indices pool, potentially
  retrieving further episodes. This produces associative chains.

- **Recursion:** Reminding is recursive — retrieved episodes
  trigger further retrieval. Bounded by the recent-episodes
  list (length 4) and recent-indices list (length 6).

- **Serendipity integration:** Every reminding invokes
  serendipity recognition. This is how recalled episodes can
  accidentally provide solutions to active concerns.

**Implication for the kernel:** The kernel's episodic memory
does coincidence-mark retrieval but doesn't do recursive
index expansion or emotion reactivation. Adding these two
mechanisms would produce the associative-stream behavior
Mueller describes — and would make the companion/narration
layer much more interesting, because remindings with emotional
content are naturally narratable.

---

## Evaluation metrics (from Ch. 4, relevant to procedures)

Mueller's episode selection uses three metrics:

```
For personal/learning goals:
  ordering = desirability × realism × similarity
  (all must exceed threshold)

For emotional goals:
  ordering = similarity only
  (no realism threshold)
```

- **Desirability:** Sum of importances of goals activated/resolved
  after top-level goal activation. Positive for successes,
  negative for failures.

- **Realism:** 0-1 scale based on plausibility of rules used.
  **Realism thresholds differ by goal type:**
  personal > learning > emotional. This is why revenge
  daydreams can be wildly unrealistic while rehearsal stays
  grounded.

- **Similarity:** Structural comparison of source and target
  goals, weighted by depth in the type hierarchy.

**Key insight for L3:** The desirability × realism product could
inform node scoring. A graph node associated with a high-
desirability, high-realism episode should score higher than one
with low-realism provenance. The realism threshold also suggests
that different L3 modes (autonomous/assisted/forced) could have
different realism requirements.

---

## The emotional feedback loop (from Ch. 3, relevant to procedures)

Mueller describes a bidirectional cycle:

```
Negative emotion → activates daydreaming goal concern →
  scenario generation → emotional reappraisal of stored
  episode → next retrieval of that episode carries modified
  emotion → cycle continues
```

Critical distinction:
- **RATIONALIZATION permanently modifies the emotion stored with
  an episode.** Next retrieval carries the updated valence.
- **ROVING only provides temporary distraction.** The episode
  is unchanged.

**Implication for the kernel:** Episodes should store mutable
emotional valence. After rationalization commits (salience),
update the originating episode's emotional tag. This is the
mechanism by which characters *get over things*.

---

## What to take for the kernel refactor

### Immediate

1. **Formalize concern initiation as theme rules.** Each concern
   type has an explicit activation condition and initial emotion.

2. **Add per-cycle decay + threshold removal.** Emotions and
   needs decay every cycle; below-threshold emotions are removed.

3. **Distinguish personal-goal vs. daydreaming-goal concern
   write-back.** Personal goals modify reality; daydreaming
   goals modify only isolated contexts and write back goals +
   outcomes.

### Next

4. **Build a rule-connection graph** for serendipity search.
   Replace scalar bias with bidirectional intersection search.

5. **Add recursive index expansion to reminding.** When an
   episode is retrieved, activate its other indices and
   recursively retrieve.

6. **Add emotion reactivation on retrieval.** Retrieved episodes
   bring back their associated emotions.

7. **Make episode emotional valence mutable.** After
   rationalization, update the episode's stored emotion.

### Research track

8. **ATMS as context upgrade.** Mueller's contexts + Hewitt's
   critique point directly at ATMS as the principled solution
   for multi-context reasoning.
