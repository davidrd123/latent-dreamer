# Mueller-to-Kernel Mapping: Episode Admission and Retrieval Discipline

Date: 2026-03-22

Source: direct inspection of Mueller's original Lisp code alongside
the kernel's Clojure implementation and five code reviews (03-09).

Purpose: ground the memory ecology fix in Mueller's actual
implementation, not just abstract design principles.

---

## What Mueller had

### 1. Hidden episodes (trace-only storage)

Source: `daydreamer/dd_epis.cl:25-50`

```lisp
(if hidden?
    (progn
     (ob$set ep 'plan-threshold *infinite-thresh*)    ; 100
     (ob$set ep 'reminding-threshold *infinite-thresh*) ; 100
     (epmem-store ep rule nil nil))
    (progn
     (ob$set rule 'accessible? t)
     (epmem-store ep rule t t)))
```

Hidden episodes are stored for structure but effectively
unretrievable — threshold of 100 is unreachable by coincidence
counting. Non-hidden episodes get normal thresholds and make their
rule accessible for future planning.

### 2. Per-index plan/reminding eligibility

Source: `daydreamer/dd_epis.cl:94-104`

```lisp
(defun epmem-store (episode index needed-for-plan? needed-for-reminding?)
  (setq index (index-intern index 'new-ok))
  (ob$add index 'indexes episode)
  (ob$add episode 'indexed-under index)
  (if needed-for-plan?
      (ob$set episode 'plan-threshold
              (+ 1 (or (ob$get episode 'plan-threshold) 0))))
  (if needed-for-reminding?
      (ob$set episode 'reminding-threshold
              (+ 1 (or (ob$get episode 'reminding-threshold) 0)))))
```

Every index link specifies whether it counts toward plan retrieval,
reminding retrieval, both, or neither. The thresholds are computed
from these flags — an episode with 3 plan-eligible indices needs 3
matching plan cues to be retrieved for planning.

### 3. Misc indices as non-priming metadata

Source: `daydreamer/dd_epis.cl:539-546`

```lisp
(if result-emot (epmem-store ep result-emot nil nil))
...
(yloop (yfor index in (find-misc-indices context top-level-goal))
       (ydo (epmem-store ep index nil nil)))
```

Surface-level indices (people, places, objects) and result emotions
are stored with `nil nil` — they don't count toward either
threshold. They help FIND an episode if other cues are already
active, but they can't bring an episode above threshold on their
own.

### 4. Two separate retrieval thresholds

Source: `daydreamer/dd_epis.cl:154-172`

Retrieval takes a `threshold-type` parameter — `'plan-threshold`
or `'reminding-threshold`. Different callers use different
thresholds:

- `episode-retrieve` (line 131): `'plan-threshold`
- `remindings` (line 215): `'reminding-threshold`

An episode can be easy to find for reminding but hard to find for
planning, or vice versa.

### 5. Rule accessibility frontier

Source: `daydreamer/dd_epis.cl:49,53-57` and `daydreamer/dd_ri.cl:273-282`

Rules start inaccessible. A rule becomes `accessible? = t` only
when it produces a stored, non-hidden episode.

Serendipity specifically searches for INACCESSIBLE rules —
`inaccessible-planning-rules` finds rules that an episode uses
but that haven't yet been accessed through planning. Finding and
verifying a path through an inaccessible rule is what promotes it.

This is the mechanism by which serendipity expands the planner's
repertoire. It doesn't just find a connection — it unlocks a rule.

### 6. Realism and desirability as ordering/pruning criteria

Source: `daydreamer/dd_epis.cl:372-430` (realism from rule
plausibilities), `daydreamer/dd_epis.cl:489-536` (desirability
from goal outcomes), `daydreamer/dd_rule2.cl:97-137` (ordering)

Realism is computed from the plausibilities of rules used in the
planning tree (approximately the product). Desirability is computed
from goal outcome importances. Both are attached to episodes at
storage time and used in later retrieval ordering:
`ordering = desirability * realism * similarity`.

Episodes with ordering > 0.0 get analogical planning. Episodes
with ordering = 0.0 are pruned. Note: these are retrieval-time
ordering/pruning criteria, not storage-time admission gates.
Realism and desirability are ATTACHED at storage time but USED
at retrieval time.

If all retrieved episodes for a goal are negative/low desirability,
the goal is deactivated.

### 7. Post-storage reminding with serendipity suppressed

Source: `daydreamer/dd_epis.cl:550`

```lisp
(epmem-reminding ep t t)  ; no-serendipities? = t, new-stored-ep? = t
```

After storing a top-level episode, Mueller immediately calls
reminding on it. This pushes the episode's indices into the
recent-indices FIFO, so the new episode influences future
retrieval. But both flags are `t`:

- `no-serendipities?` = t — suppresses serendipity recognition
- `new-stored-ep?` = t — suppresses narration of the reminding

This prevents the system from "discovering" its own just-stored
episode as a serendipitous finding. The episode primes future
retrieval through the FIFO, but doesn't trigger creative
discovery on itself.

Design implication for us: when a new episode is stored, its
content-zone indices should enter recent-indices, but serendipity
should NOT fire on the storage event itself.

---

## What we collapsed

| Mueller's discipline | Kernel's current state | Consequence |
|---|---|---|
| `hidden?` → threshold = 100 | No concept of trace-only storage | Every stored episode is retrievable |
| `needed-for-plan?` / `needed-for-reminding?` per index | Flat `:indices` set, everything counts | Rule IDs and support tags become live reminding cues |
| Misc indices stored as `nil nil` | Support indices stored with `:plan? false :reminding? false` but still enter flat `:indices` and `:episode-index` | Support leaks into reminding ecology |
| Two separate thresholds | Kernel has plan/reminding thresholds but cue roles are collapsed | Threshold distinction exists but doesn't function because all indices count equally |
| `accessible?` toggle on rules | All rules always accessible | No frontier between "known to planner" and "discoverable by serendipity" |
| Realism from rule plausibilities | `:realism :imaginary` or heuristic default | No actual realism computation, no pruning |
| Desirability from goal outcomes | `:desirability :mixed` or heuristic default | No actual desirability computation, no goal deactivation |

---

## What we've now restored (partially)

From the recent deep-build work:

- Admission membrane on family-plan episodes:
  - `:trace` — not retrievable for planning
  - `:provisional` — retrievable under stricter conditions
  - `:durable` — now reachable through cross-family reuse promotion
- Typed cue zones on episodes:
  - content cues in the normal retrieval index
  - reminding cues as the only indices that enter the recent FIFO
  - provenance indices stored separately as weak structural ties
  - support tags stored separately as non-priming metadata
- Evaluation metadata fields on episodes (`:evaluation`,
  `:retention-class`, `:keep-decision`, `:cycle-created`)
- Heuristic evaluation per family (roving = hot-cues,
  rationalization/reversal payloads = exemplar)
- Typed evaluation facts (`:episode-evaluation`) asserted into
  branch contexts
- Retrieval hit facts (`:retrieval-hit`) for roving seed/reminded
  episodes
- Typed promotion facts (`:episode-promotion`) for cross-family reuse
- Provenance bonus flipped to shorter=stronger
- Graph edges tightened (require non-empty shared-keys)
- Rule constructors factored to reduce boilerplate

---

## What remains to reconstruct

### From Mueller (regression fix)

1. **Rule accessibility frontier** — rules start inaccessible.
   A rule becomes accessible only when it produces a promoted
   episode. Serendipity searches the inaccessible frontier.

2. **Realism/desirability as stronger retrieval discipline** —
   our heuristic evaluation scaffold exists, but we still do not
   compute or use these signals with Mueller's level of pruning
   force. The first external evaluator seam is in place, but not
   yet the full admission discipline.

### Genuinely new (not in Mueller)

3. **Cross-session consolidation** — Mueller's system ran once.
   Ours accumulates. Need: decay by usefulness, cluster capping,
   archive never-reused episodes. (See `extension-consolidation.md`)

4. **Anti-residue flags** — `:backfired`, `:stale`, `:contradicted`,
   `:same-family-loop`. Mueller didn't need these because episodes
   didn't reuse themselves.

5. **Same-family provenance cap** — Mueller's episodes didn't carry
   structural graph provenance. Our provenance bonus can rescue
   weak episodes through structural proximity alone. Require at
   least one world-anchored content cue.

6. **LLM evaluator for realism/desirability** — Mueller hard-coded
   realism from rule plausibilities. Our hybrid version uses an LLM
   to assess contextual realism/desirability, gating promotion from
   `:provisional` to `:durable`.

---

## The discipline chain (Mueller + extensions)

```
Episode produced
  → Mueller's gates:
      hidden? → trace-only (threshold = infinity)
      non-hidden → normal thresholds
      per-index: plan-eligible? reminding-eligible? neither?
      realism computed from rule plausibilities
      desirability computed from goal outcomes
      rule marked accessible
  → Our extensions:
      admission tier: :trace / :provisional / :durable
      LLM evaluator scores realism/desirability (gates promotion)
      anti-residue flags checked
      same-family provenance capped
      consolidation: decay, cluster cap, archive
```

---

## Source files

Mueller's original:
- `daydreamer/dd_epis.cl` — episode storage, retrieval, reminding
- `daydreamer/dd_ri.cl` — rule interpretation, serendipity, accessibility
- `daydreamer/dd_rule2.cl` — rule application, evaluation ordering

Kernel:
- `kernel/src/daydreamer/episodic_memory.clj`
- `kernel/src/daydreamer/goal_families.clj`
- `kernel/src/daydreamer/goal_family_rules.clj`
- `kernel/src/daydreamer/rules.clj`

Reviews that surfaced these gaps:
- `replies/archive-01-16/03-episode-loop-risks-5thinking.md`
- `replies/archive-01-16/06-family-graph-rev-5pro.md`
- `replies/archive-01-16/09-episode-loop-risks-5pro.md`
