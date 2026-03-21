# Chain Trace B: Reminding / Serendipity

First-pass trace. This stays at Mueller's mechanism level. It describes the accident-driven path by which a salient input or episode can redirect thought.

## Flow

1. **Input or retrieved episode -> Episode retrieval**
   - Link type: structural
   - Typed handoff: `{:indices [concepts objects recent-indices rule-indices]}`
   - Meaning: a salient object, state, action, or just-retrieved episode contributes explicit indices that cue episodic memory.

2. **Episode retrieval -> Reminding**
   - Link type: structural
   - Typed handoff: `{:episode-id ... :matched-indices [...] :marks n}`
   - Meaning: each newly retrieved episode is eligible to seed the reminding cascade.

3. **Reminding -> Episode retrieval**
   - Link type: structural
   - Typed handoff: `{:indices episode.indices}`
   - Meaning: the episode's other indices are added to the recent-index FIFO and used to retrieve more episodes recursively.

4. **Reminding -> Serendipity recognition**
   - Link type: structural
   - Typed handoff: `{:concern-id ... :episode-id ... :recent-episodes [...] :recent-indices [...]}`
   - Meaning: the salient reminded episode is tested against active concerns for accidental applicability.

5. **Serendipity recognition -> Rule intersection search**
   - Link type: structural
   - Typed handoff: `{:top-rule ... :bottom-rule ... :rule-graph ...}`
   - Meaning: the mechanism looks for a path from a rule associated with the concern to a rule associated with the salient episode or concept.

6. **Rule intersection search -> Path verification**
   - Link type: structural in Mueller, candidate judgment site in adaptation work
   - Typed handoff: `{:rule-path [...] :goal ... :bindings ...}`
   - Meaning: any candidate path must unify progressively and yield a coherent concrete plan.

7. **Path verification -> Concern context update**
   - Link type: structural
   - Typed handoff: `{:verified-episode ... :target-goal ... :target-context ...}`
   - Meaning: if verification succeeds, a new analogical plan is inserted into the appropriate context.

8. **Serendipity recognition -> Surprise emotion generation**
   - Link type: structural
   - Typed handoff: `{:concern-id ... :surprise-strength ...}`
   - Meaning: the concern becomes more motivationally urgent.

9. **Serendipity recognition -> Concern initiation**
   - Link type: structural when the result creates a new concern
   - Typed handoff: `{:top-level-goal ... :trigger-rule ... :bindings ...}`
   - Meaning: a newly revealed path can become its own active concern rather than just a suggestion.

## What accumulates along this chain

- recent indices and recent episodes change during the reminding cascade
- surprise emotion changes later concern selection
- a verified serendipitous plan can become a new analogical plan and later a stored episode

## Why this chain matters

This is the engine that turns accidental salience into new capability. It is the clearest place where Mueller's system exceeds stateless prompting: a chance cue becomes a verified path, then becomes reusable structure and future motivational pressure.
