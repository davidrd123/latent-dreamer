# Executor Migration, Episode-Use Attribution, and Accessibility Frontier

You have the full repo context. Do NOT summarize it back. The codex is about to implement these three things. I need concrete guidance on how they compose in the current codebase.

## Context

Review 10 (`replies/archive-01-16/10-executor-boundary-5pro.md`) gave a detailed spec for `execute-rule` in `rules.clj` with a five-step migration path. But since then, the codex added local typed effects for roving inside `goal_families.clj` — a `roving-plan-effects` function that returns effect descriptors instead of mutating world directly. Reviews 11a and 11b gave the spec for episode-use attribution and promotion logic.

The codex now needs to reconcile three things that are about to land simultaneously.

## Question 1: Reconcile local roving effects with the execute-rule spec

`goal_families.clj` now has local typed effects for roving (effect descriptors like `:context/sprout`, `:fact/assert`, `:episode/reminding`). Review 10 says the executor boundary belongs in `rules.clj` as `execute-rule`, not growing inside `goal_families.clj`.

- Look at the current roving effects code in `goal_families.clj` and the `execute-rule` spec from review 10. What's the cleanest way to reconcile them? Does the roving effects code become the body of a `:clojure-fn` executor that returns a `RuleResultV1` with an `:effects` channel? Or does it stay separate and `execute-rule` only handles the pure instantiate slice for now?
- Review 10's migration steps A-E assume `goal_families.clj` is still calling `instantiate-rule` directly. That's partially true but the roving path has already diverged. How does the migration path change given what exists now?
- Should `execute-rule` handle the effects channel (`:context/sprout`, etc.) in its first version, or should effects stay family-owned until the full `RuleResultV1` with effects is designed? What's the minimum `execute-rule` that's useful without being premature?

## Question 2: Episode-use attribution integration points

Review 11b proposed `note-episode-use` and `resolve-episode-use-outcome` as the core abstraction. The codex needs to know where exactly in the current code these calls should go.

- Look at `roving-plan`, `rationalization-plan`, and the reversal procedures in `goal_families.clj`. Where exactly does `note-episode-use` get called? When the episode is first retrieved as a candidate? When it's actually used in a branch? When the branch completes?
- `resolve-episode-use-outcome` needs evaluator output + branch result. Currently `family_evaluator.clj` runs after the plan body. Is that the right hook point, or does outcome resolution need to happen later (e.g., after the next cycle shows whether emotion shifted)?
- The current `promote-cross-family-retrieval-episodes` function does both bookkeeping and policy. 11b says split it into `record-cross-family-use` + `reconcile-episode-admission`. Look at the current callers and propose the exact refactor.

## Question 3: Rule accessibility frontier — where it hooks in

Review 11b proposed a `:rule-access` registry in world state with `:accessible` / `:frontier` / `:quarantined` states, and `planning-graph` / `serendipity-graph` helpers that filter the structural graph.

- Look at the current callers of `build-connection-graph`, `bridge-paths`, and `reachable-paths` in `goal_families.clj` and `episodic_memory.clj`. Where would the planning/serendipity graph split be applied? Which callers need the planning view vs. the serendipity view?
- The current authored rules in `goal_family_rules.clj` should start `:accessible`. But the graph currently gets rebuilt inside `ranked-roving-episode-ids` — review 03 already flagged that as wrong-layer caching. If the graph moves to a registry, how does that interact with the accessibility state? Should the registry own both the rules AND the graph AND the accessibility state?
- 11b says provisional episodes should NOT open rules to accessible — only durable ones. But we don't have promotion yet. Should the accessibility frontier wait for promotion logic, or can it land independently with authored rules starting accessible and no frontier rules existing yet?

## What I need

Not principles — I have those from reviews 03-11. I need: which functions to change, in which order, with which intermediate states that keep the test suite green. Think about the current test expectations in `rules_test.clj`, `goal_families_test.clj`, and `episodic_memory_test.clj` and propose a migration that doesn't break them.
