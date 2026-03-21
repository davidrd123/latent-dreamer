# Inference Rule Application

## 1. Mechanism name

Inference rule application

## 2. Source anchors

- Chapter 7, `7.4.6 Inference Rule Application` at [07-implementation-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/07-implementation-of-daydreamer.md):619
- Chapter 2 overview of rule types and inference chaining at [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):114 and [02-architecture-of-daydreamer.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/02-architecture-of-daydreamer.md):336
- Figure 7.4 dependency additions in [36-image-reviewed-chapter-7-procedure-figures.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/36-image-reviewed-chapter-7-procedure-figures.md):56
- Figure 4.9 and `REVERSAL1` discussion in [04-learning-through-daydreaming.md](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/04-learning-through-daydreaming.md):269
- Appendix A examples: `LOVERS1`, `REVERSAL1`, `REPERCUSSIONS1`
- Corresponding code: no recovered generic inference engine in the kernel; only family-specific bridges in [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj)

## 3. Cognitive phenomenon (one line)

Automatic consequence propagation: once certain facts are present, further implications unfold without deliberate planning.

## 4. Kernel status (one line)

Not recovered as a general mechanism: the kernel has family-specific activation logic in [goal_families.clj](/Users/daviddickinson/Projects/Lora/latent-dreamer/kernel/src/daydreamer/goal_families.clj), but not Mueller's generic inference-rule fixpoint loop over contexts, bindings, assertions, deletions, and concern initiation.

## 5. Loop shape

Input: a context.

Procedure:

1. find each inference rule whose antecedents are all retrieved in the context
2. for each satisfying binding set:
   - instantiate the rule's consequents and deletion consequents
   - if the instantiated consequent is a new top-level goal and the concern-type gate allows it, invoke concern initiation instead of simply asserting it
   - otherwise, assert instantiated consequents into the same context and retract instantiated deletions
3. if any assert or retract occurred, repeat the whole procedure

Important structural details:

- inference rules do not sprout alternative contexts; they change the current context because they represent inevitable consequences
- duplicate reapplication of the same rule to the same antecedent bindings must be inhibited
- when an instantiated consequent is already retrievable, its strength is incremented rather than reasserted
- the procedure is optimized by considering only rules connected to touched facts

State read:

- current context facts
- inference rules
- touched-fact bookkeeping
- current concern type

State written:

- asserted facts
- retracted facts
- updated strengths on existing facts
- newly initiated concerns when applicable
- emotional responses when a personal-goal outcome is inferred

## 6. Judgment points

Mueller treats inference as structurally deterministic, but there are clear modern pressure points:

1. **Applicability softness**: many social and interpersonal antecedents are represented as rigid unifications even though in practice they are graded or ambiguous.
2. **Inference confidence**: Mueller's consequent-strength arithmetic is structural, but the real plausibility of inferred attitudes, expectations, or social consequences may be context-sensitive.
3. **Consequence shaping**: some inferred mental-state or attitude consequents are semantically thin in the rule base and could benefit from contextual elaboration while still remaining rule-triggered.

## 7. Accumulation story

Inference rule application is one of the main ways the world model and concern set grow during a cycle. It adds facts, removes facts, increments strengths, and can create new concerns that continue running later. In reality contexts those changes persist; in daydream contexts they persist within that hypothetical branch and affect all later processing in that branch.

Because it repeats to fixpoint, small changes can cascade into larger conceptual restructuring.

## 8. Property to preserve

Inference must remain an explicit, local, repeat-until-stable consequence process over inspectable facts and bindings. Even if some matches become softer, the system still has to know which antecedents led to which consequences and when the cascade has settled.

## 9. Upstream triggers / downstream triggers

Upstream:

- rule application after asserting achieved subgoals or actions
- initialization of the first reality context

Downstream:

- concern initiation
- emotion generation in response to inferred personal-goal outcomes
- further inference applications until fixpoint
- later planning through the new facts and goals it made available

## 10. Mueller-faithful description vs. candidate hybrid cut

**Mueller-faithful**: inference rule application is a forward-chaining, no-branching procedure over the current context. It asserts inevitable consequences, retracts deletions, initiates new concerns when top-level goals are inferred, and repeats until no further changes occur. It is the mechanism that turns partial world-state changes into the broader consequences that motivate later concerns.

**Candidate hybrid cut**: keep fixpoint control, touched-fact scoping, assertion/retraction, and concern-creation gates structural. The most plausible hybrid use is narrow: soften brittle social or mental-state matches and optionally score uncertain consequents, while preserving explicit provenance for each inference.

## Interface shape (required)

**tentative schema**

Integration patterns:

- **Co-routine judgment** for soft antecedent satisfaction
- **LLM-as-evaluator** for uncertain consequent confidence

Input:

```clojure
{:context {:id keyword
           :facts [any]
           :touched-facts [keyword]}
 :rule {:id keyword
        :antecedents [any]
        :consequents [any]
        :deletions [any]}
 :bindings-candidate map
 :current-concern {:id keyword
                   :goal-type keyword
                   :mode #{:personal :daydreaming}}}
```

Output:

```clojure
{:antecedent-match {:satisfied? boolean
                    :confidence number
                    :reason string}
 :consequent-confidence [{:fact any
                          :confidence number
                          :reason string}]}
```
