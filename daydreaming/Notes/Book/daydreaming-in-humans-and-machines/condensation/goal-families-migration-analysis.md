# goal_families.clj Migration Analysis

Purpose: identify what gets extracted into a generic rule substrate, what stays family-specific, and what the extraction order should be.

Source: `kernel/src/daydreamer/goal_families.clj` (~1068 lines)

## What the file actually does

This is the biggest single substitute for the missing generic rule engine. It contains three layers that a generic substrate would separate:

### Layer 1: Activation logic (mechanism 16 — daydreaming goal activation)

**Functions:** `reversal-activation-candidates`, `rationalization-activation-candidates`, `roving-activation-candidates`, `activate-family-goals`

**What it does:** scans contexts for failed-goal + negative-emotion pairs, applies family-specific thresholds and preconditions, creates new top-level goals with typed metadata (trigger context, failed goal, emotion, frame).

**RuleV1 mapping:** These are inference rules. Each activation candidate function is essentially:
- antecedent: failed goal + negative emotion above threshold + (for rationalization: stored frame exists)
- consequent: new daydreaming goal concern with typed metadata
- The `activate-family-goals` function is the inference loop that runs all three

**Extract or keep procedural?** EXTRACT. This is the clearest candidate. The activation conditions are already structural thresholds. They map directly to inference rules with `:instantiate` executors. The only family-specific logic is the threshold values and the precondition check (rationalization requires a stored frame; roving requires NO stored frame).

### Layer 2: Plan bodies (mechanisms 17, 19 — strategy execution)

**Functions:** `roving-plan`, `rationalization-plan`, `reverse-leafs`, `reverse-undo-causes`, `reversal-sprout-alternative`

**What it does:**
- **Roving:** sprouts a side context, seeds it with a pleasant episode, invokes reminding cascade
- **Rationalization:** sprouts a reinterpretation branch, asserts stored reframe facts, applies emotional diversion (reduces trigger emotion, creates hope emotion)
- **Reversal:** finds weak planning leafs, looks up stored failure causes, sprouts alternative-past branches via `sprout-alternative-past`, retracts shaky assumptions

**RuleV1 mapping:** These are planning rules with procedural consequents — exactly the `:clojure-fn` executor pattern. The rule fires structurally (active daydreaming goal of matching type), but the plan body is Clojure code that manipulates contexts, episodes, and emotions.

**Extract or keep procedural?** KEEP PROCEDURAL for now. These are complex multi-step procedures with side effects (context sprouting, fact assertion/retraction, emotion modification, episode reminding). They could eventually become `:clojure-fn` executors inside `RuleV1` objects, but extracting the activation logic first is higher value and lower risk.

### Layer 3: Support infrastructure

**Functions:** `fact-type?`, `goal-fact?`, `emotion-fact?`, `negative-emotion-fact?`, `positive-emotion-fact?`, `fact-id`, `fact-ref-ids`, `linked-emotion-facts`, `strongest-emotion`, `local-failed-goal-facts`, `emotion-pressure`, `leaf-context?`, `primary-situation-id`, `clamp-strength`, `replace-emotion-fact`, `retract-facts`, `gc-emotions`, `gc-plans`, `rebind-planning-facts`, `sprout-alternative-past`

**What it does:** fact querying, emotion manipulation, context cleanup, planning-tree rebinding. These are the helpers that the activation logic and plan bodies use.

**RuleV1 mapping:** These are not rules. They are the operations that rule executors call. Some belong in the rule engine (fact querying, matching predicates). Some belong in specific mechanism implementations (emotion diversion, planning-tree rebinding). Some are general context utilities that should move to `context.clj`.

**Extract or keep?** SPLIT. Fact querying predicates (`fact-type?`, `goal-fact?`, `emotion-fact?`, `negative-emotion-fact?`) belong in a shared fact-query namespace or in the rule engine's pattern language. Context operations (`gc-emotions`, `gc-plans`, `rebind-planning-facts`, `sprout-alternative-past`) should stay close to their current callsites for now.

## What's already close to RuleV1 shape

The activation candidates functions are strikingly close to rules already:

```
reversal-activation-candidates:
  antecedent: leaf context + failed goal + negative emotion > 0.5
  consequent: {:goal-type :reversal, :emotion-id ..., :failed-goal-id ..., :trigger-context-id ...}

rationalization-activation-candidates:
  antecedent: failed goal + negative emotion > 0.7 + dependency link + stored rationalization frame
  consequent: {:goal-type :rationalization, :frame-id ..., :failed-goal-id ..., ...}

roving-activation-candidates:
  antecedent: failed goal + negative emotion > 0.04 + NO rationalization frame available
  consequent: {:goal-type :roving, :failed-goal-id ..., ...}
```

The `activate-family-goals` function is essentially the inference loop: find all matching rules, check for duplicate goals, create new concerns.

## Extraction order

### Phase 1: Fact-query predicates (low risk, immediate value)

Move `fact-type?`, `goal-fact?`, `emotion-fact?`, `negative-emotion-fact?`, `positive-emotion-fact?`, `fact-id`, `fact-ref-ids` into a shared namespace (or into the rule engine's pattern-matching layer). These are the building blocks for antecedent matching.

### Phase 2: Activation rules as RuleV1 objects (medium risk, high value)

Express the three activation candidate functions as `RuleV1` maps with `:instantiate` executors. The antecedent schemas are the matching conditions. The consequent schemas are the typed goal-activation outputs. The thresholds become plausibility or precondition fields.

This is the first real test of whether `RuleV1` works in practice — can you express these existing working rules in the new schema and get the same behavior?

### Phase 3: Plan bodies as :clojure-fn executors (medium risk, medium value)

Wrap `roving-plan`, `rationalization-plan`, and the reversal procedures as `:clojure-fn` executors that return `RuleResultV1`. The plan logic stays procedural but now lives inside a rule that participates in the connection graph.

### Phase 4: Connection graph over extracted rules (the payoff)

Once the activation rules and plan-body rules are `RuleV1` objects with schemas, build the connection graph from their antecedent/consequent overlap. This is where serendipity becomes possible — the graph can find paths between, say, a rationalization rule's consequent and a recovery rule's antecedent.

## What stays family-specific

- The emotional diversion arithmetic in `apply-rationalization-emotional-diversion` — this is Mueller-specific rationalization math, not generic rule behavior
- The `sprout-alternative-past` procedure — this is REVERSAL's core operation, deeply tied to context copying, emotion GC, and plan rebinding
- The `reverse-leaf-branches` scan — this is REVERSAL's specific strategy for finding repair targets
- Threshold constants — these are family-specific policy, not generic rule parameters (though they could become rule plausibility values)

## Key observation

The file's docstrings already say the quiet part out loud:
- `rationalization-frame-candidates`: "this is an honest bridge for the missing rule engine"
- `activate-family-goals`: "this approximates Mueller's Theme rules as a pure pre-competition pass"
- `rationalization-plan`: "this is a bounded bridge to Mueller's rationalization planner"

The code was written as a deliberate substitute for the rule engine. Extracting it into `RuleV1` is completing the migration the code already anticipated.
