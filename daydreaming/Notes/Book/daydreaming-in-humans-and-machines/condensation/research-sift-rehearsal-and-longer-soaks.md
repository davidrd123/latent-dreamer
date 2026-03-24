# Sift: Prompt 23 — Rehearsal Kernel Family and Longer Soaks

Two replies (5think, 5pro). High convergence on overall design.
5pro adds three concrete gotchas and sharper measurement proposals.

## 1. Rehearsal kernelization — settled design

Both replies converge on the same architecture. This section records
the settled points plus the gotchas.

### Trigger: regulation-need + affordance (settled)

Both replies: use regulation-need + affordance as the primary trigger.
Not Mueller's positive-interest trigger (that's a second trigger rule,
starting as `:authored-frontier`).

The trigger should match: failed goal + negative emotion + dependency
link + `:rehearsal-affordance` typed fact. Do NOT match raw Graffito
facts (`:monk_counts_a_holdable_beat`, etc.) in the trigger rule.
Promote the readiness check into one typed fact so the family isn't
hardcoded to one benchmark.

### `:motivation-strength` as a separate field (5pro, new)

The current activation path uses `:emotion-strength` as the
family-ranking scalar. That works for reversal/rationalization but
is **wrong for rehearsal**, where motivation is regulation need, not
raw negative emotion.

Rehearsal is the first family where `motivation ≠ emotion-strength`.
Thread `:motivation-strength` through trigger → activation → goal
selection. Without this, rehearsal goals will always lose to
high-emotion reversal/rationalization goals even when regulation
need is high.

### Episode payload: `:routine-facts` + `:anticipated-cues` (settled)

Both replies: the rehearsal equivalent of rationalization's
`:reframe-facts` and reversal's `:input-facts` is `:routine-facts`
(what was practiced) plus `:anticipated-cues` (what the routine
prepares for, so later retrieval under pressure can find it).

Promotion-eligible when `:routine-facts` is non-empty. Uses the
same membrane path as other families — no special rehearsal
promotion route.

### The retrieval-index threshold trap (5pro, new)

Each content index increments `:plan-threshold` for the episode.
If you index every routine fact, precondition, and anticipated cue,
the episode disappears behind its own threshold — unfindable.

**Keep retrieval indices to 3-5 max:**

```
:retrieval-indices [failed-goal-id
                    :can_corresponds_to_sensory_capacity
                    :repeated_stroke_returns_precision
                    :control_can_meet_demand]
```

Put routine details and preconditions in the episode payload or
support indices, not content indices. This is a concrete gotcha
from the existing retrieval code, not a design preference.

### Generic post-effect reappraisal hook (settled)

Both replies: reappraisal should be a generic post-effect kernel
pass inside `run-family-plan`, not a rehearsal-only trick. Any
family that changes persistent character state should trigger
reappraisal on the same situation before episode storage.

Order: apply effects → update character state → reappraise →
then store episode → then use-history/promotion bookkeeping.

### Rehearsal episodes are NOT direct sources for other families (settled)

Both replies: rehearsal episodes should not masquerade as
rationalization frames (`:reframe-facts`) or reversal causes
(`:input-facts`). The bridge is through state change → reappraisal,
not payload reuse. Rehearsal episodes contribute cross-family use
history and promotion evidence through retrieval, not through
source-selection.

### Two deliverables, not one (5think)

Making rehearsal a family AND preserving the current miniworld's
cross-family support-source probe are separate deliverables. If you
skip the second, the current 20-cycle summary counters will get
worse, not better. Be explicit about sequencing.

## 2. Frontier opening is cosmetic in the current benchmark (5pro, new)

The `serendipity-graph` already includes both `:accessible` AND
`:frontier` rules. So the bridge rule
`:goal-family/reversal-aftershock-to-rationalization` was visible
to cross-family trigger generation **before** promotion opened it
to `:accessible`.

The access transition is real bookkeeping but did not actually
unlock new behavior in this benchmark. `rule-access-transition-count`
should not be over-read as "new capability emerged."

This matters for how we interpret the miniworld results. The promoted
episode and the opened frontier rule are real membrane movements, but
they're not yet proof that the system can do something it couldn't
before.

## 3. Longer soak predictions — settled

Both replies converge on the same forecast with slightly different
detail. The 5pro reply provides exact coefficient analysis.

### Short attractor, not collapse (80-85%)

The fatigue penalties prevent one-step lock-in:
- exact repeat: 0.16
- same situation in last 2: 0.08
- same family in last 3: 0.05

But they don't break a 4-5 step loop when base scores are 0.7-1.0.

Likely late-window pattern:
street-reversal → apartment-rehearsal → mural-rationalization →
mural-rationalization → repeat.

### Tony state bands, not climbs (90%)

The `0.86 * current + 0.14 * baseline` decay means repeated
execution has a fixed point. Late-cycle band roughly:
- sensory-load: ~0.75-0.82
- entrainment: ~0.58-0.80
- felt-agency: ~0.55-0.68
- perceived-control: ~0.65-0.81

Banded oscillation, not monotonic learning.

### Source episode oligarchy (75-80%)

The selector feeds wins to already-winning episodes. No novelty
term, no recency penalty. `payload-exemplar-cluster-cap` is 2, so
only the two newest exemplars in a payload cluster stay accessible.
Rich-get-richer with a hard cap.

### Accumulation plateaus early (85%)

Only one explicit frontier bridge in the benchmark. Once it opens,
there's nothing structurally new to unlock.
`rule-access-transition-count` will rise early then flatline.

### The honest forecast

**50-100 cycles will probably validate stability, not learning.**
The chooser doesn't consume enough memory for genuine accumulation.
The dynamical core isn't collapsing, which is valuable. But the
system is not becoming more capable over time in the current
benchmark.

## 4. Measurements for longer soaks — best proposals

### Operator 8-gram motif diversity (5pro, sharpest new metric)

Distinct operator 8-grams in the last 40 cycles.
- Real accumulation: >8
- Stable cycling: 4-6
- Degraded collapse: 1-2

Better than family entropy, which can't catch a 4-step loop.

### Cross-family source concentration (5pro)

Share of rehearsal-support uses by top 2 source episode IDs in
last 30 relevant cycles.
- Real accumulation: <0.7
- Stable cycling: 0.7-0.9
- Degraded: >0.9

Direct check on whether the same few episodes are immortal.

### Memory-ablation delta (5think, sharpest capability test)

At each cycle, compare chosen candidate in full world vs same
world with episodes/rule-access zeroed. Track
`memory_changed_choice?`. If near 0 after cycle 20, memory is
decorative. If >10% of late cycles, memory alters behavior.

### Distinct source diversity (both)

- `distinct-cross-family-source-episode-count`
- `distinct-promoted-episode-count`

If these stop growing while total use counts keep growing, you
have oligarchy, not accumulation.

### Usable memory ratio (5pro)

`accessible_non_trace_episode_count / stored_episode_count`.
If stored keeps rising but accessible stays flat, the system is
growing sludge, not capability.

### Time since last structural novelty (5think)

- `cycles-since-last-promotion`
- `cycles-since-last-rule-access-transition`
- `cycles-since-last-new-source-episode-win`

If all three >20, the soak is in maintenance mode.

### Tony-state band width (both)

Per 20-cycle window: max-min and mean of felt-agency and
perceived-control. Stddev near 0 = saturated. Wide oscillation
with stable mean = orbit. Drift in mean = actual change.

### Candidate margin (both)

`top_candidate_strength - second_candidate_strength`.
Margin >0.25 for 10 consecutive cycles = lock-in territory.

## 5. "It learned something" criterion (both converge)

The sharpest version (5think):

A later cycle chooses mural rationalization while mural appraisal
is still threat-dominant. That choice disappears in the ablated
no-memory twin. And the source episode that enabled it wasn't
already dominant by cycle 10.

That's a real gain in reachable behavior, not just more stored
episodes.

## 6. Build order for rehearsal kernelization (both converge)

1. Add rehearsal rules to `goal_family_rules.clj`
   (trigger + activation + request + dispatch)
2. Add `:motivation-strength` to trigger/activation path
3. Add rehearsal executor + runtime case to `goal_families.clj`
4. Add `:character/blend-state` or `:rehearsal/apply-routine`
   typed effect
5. Add generic post-effect reappraisal hook in `run-family-plan`
6. Add rehearsal payload handling to promotion/evaluation functions
7. Port the current miniworld support-source probe into the family
   (preserves current accumulation behavior)
8. Run 50 cycles with new measurements
