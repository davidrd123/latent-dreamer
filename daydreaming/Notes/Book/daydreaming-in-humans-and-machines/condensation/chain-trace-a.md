# Chain Trace A: Planning / Analogy

First-pass trace. This stays at Mueller's mechanism level. It is a faithful execution sketch, not a modernization proposal.

## Flow

1. **Emotion-driven control -> Planner**
   - Link type: structural
   - Typed handoff: `{:concern-id ... :mode ... :current-context ... :motivation ...}`
   - Meaning: the scheduler picks the most highly motivated eligible concern and hands its current execution state to the planner.

2. **Planner -> Rule application**
   - Link type: structural
   - Typed handoff: `{:concern-id ... :context-id ...}`
   - Meaning: one planning step begins in the concern's current context.

3. **Rule application -> Planning rule application**
   - Link type: structural
   - Typed handoff: `{:context-id ... :active-subgoals [...] :current-concern ...}`
   - Meaning: rule application separates planning from inference and sends the planning half the current active leaf subgoals.

4. **Planning rule application -> Episode retrieval**
   - Link type: structural
   - Typed handoff: `{:indices [rule-id]}`
   - Meaning: each applicable planning rule is used as a retrieval cue for prior episodes indexed under that rule.

5. **Episode retrieval -> Reminding**
   - Link type: structural
   - Typed handoff: `{:episode-id ... :matched-indices [...] :marks n}`
   - Meaning: any retrieved analogical episode becomes salient enough to seed the reminding cascade.

6. **Planning rule application -> Analogical rule application**
   - Link type: structural
   - Typed handoff: `{:context-id ... :subgoal ... :episode-id ... :rule-id ... :bindings ...}`
   - Meaning: a retrieved episode is used to continue or begin an analogical plan for the current subgoal.

7. **Analogical rule application -> Subgoal creation**
   - Link type: structural
   - Typed handoff: `{:context-id ... :subgoal ... :episode-id-or-nil ... :rule-id ... :augmented-bindings ...}`
   - Meaning: the planning tree grows in a fresh sprouted context, optionally carrying a matching subepisode down to child subgoals.

8. **Subgoal creation -> Context sprouting / backtracking**
   - Link type: structural
   - Typed handoff: `{:parent-context ... :child-context ... :subgoals [...]}`
   - Meaning: the new alternative world-state is inserted into the context tree so the planner can keep descending or later backtrack.

9. **Planner -> Concern termination**
   - Link type: structural
   - Typed handoff: `{:concern-id ... :resolution-context ... :success? ...}`
   - Meaning: once the top-level goal succeeds or all paths fail, the concern resolves.

10. **Concern termination -> Episode evaluation**
    - Link type: structural in Mueller, with explicit evaluator-style judgment points
    - Typed handoff: `{:planning-tree ... :used-rules [...] :goal-outcomes [...] :concern-type ...}`
    - Meaning: the completed branch is scored for realism and desirability, and any branch-abandonment or goal-deactivation consequences are resolved.

11. **Episode evaluation -> Episode storage**
    - Link type: structural
    - Typed handoff: `{:planning-tree ... :rule-indices [...] :surface-indices [...] :realism ... :desirability ...}`
    - Meaning: the evaluated experience is decomposed into reusable episodes and subepisodes for later planning.

## What accumulates along this chain

- concern motivation is consumed but persists across cycles
- retrieval can update recent memory through reminding
- planning grows a context tree and a planning tree
- successful or otherwise storable runs add episodes plus stored realism/desirability metadata to long-term episodic memory

## Why this chain matters

This is the basic "thought becomes reusable experience" pipeline. It shows how an active concern gets one planning step, pulls in prior episodes, grows a hypothetical branch, and can eventually feed new material back into memory.
