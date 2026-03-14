# EMA Manual Review

Image-reviewed companion notes for layout-sensitive pages in `EMA/`.

Use this note when the text layer is structurally unreliable. It captures the process claims, appraisal-variable vocabulary, coping repertoire, and the bird-example walkthrough in a form that is easier to reason over later.

<!-- page: 1 -->
## Page 1

- Title: `EMA: A computational model of appraisal dynamics`
- Authors: Stacy Marsella and Jonathan Gratch.

Abstract, manually stabilized:
- The paper argues against multiple separate fast and slow appraisal systems.
- Instead, it proposes a single, automatic appraisal process operating over an agent’s current interpretation of its relationship to the environment.
- Emotional dynamics arise because perceptual and inferential processes keep changing that interpretation over time.
- The paper illustrates the claim with a computational model of a naturalistic emotional episode.

Core theoretical position:
- Appraisal should not be conflated with inference.
- Dynamics do not require multiple appraisal modules if other processes continuously alter the interpreted situation.

Pressure-engine relevance:
- This is the cleanest source in the reading list for “appraisal is a loop over a changing interpretation,” rather than a one-shot labeling pass.

<!-- page: 2 -->
## Page 2

Bird example setup:
- The paper uses a short real-world incident in which a bird unexpectedly flies into a room during a lab study.
- The actor with the umbrella appears to move through a rapid sequence:
- surprise
- fear
- aggressive self-protection
- relaxation
- concern for the bird
- helping behavior with relief/amusement

Figure 1:
- Presents frame snapshots from the bird episode across time.
- The example is deliberately short and dynamic so the model has to explain rapid emotional reconfiguration rather than a slow, single appraisal judgment.

Pressure-engine relevance:
- The paper’s example is valuable because it treats affect as state transition over a few seconds, not as a static category assignment.

<!-- page: 3 -->
## Page 3

Key theoretical move:
- Appraisal and inference are sharply separated.
- Appraisal is modeled as fast, parallel, and uniform.
- Perceptual and cognitive processes may be fast or slow, reactive or deliberative; they alter the causal interpretation, which then triggers reappraisal.

EMA’s central representation:
- The model operates over a `causal interpretation` of the person-environment relationship.
- This causal interpretation is the agent’s current working representation of beliefs, desires, intentions, plans, probabilities, and social attributions.

Architectural point:
- Reactive and deliberative processes both write into this shared causal interpretation.
- Appraisal then reads domain-independent features from that representation.

Pressure-engine relevance:
- This is a strong precedent for keeping a shared concern/appraisal state that many processes update, instead of splitting “fast emotion” and “slow planning” into unrelated subsystems.

<!-- page: 4 -->
## Page 4

Appraisal variables explicitly listed in the paper:
- `Perspective`
- `Desirability`
- `Likelihood`
- `Causal attribution`
- `Temporal status`
- `Expectedness`
- `Controllability`
- `Changeability`

Manual stabilization of meaning:
- `Desirability`: utility of the event for the agent’s perspective.
- `Likelihood`: probability of the event or outcome.
- `Causal attribution`: who deserves credit or blame.
- `Temporal status`: past, present, or future.
- `Expectedness`: whether the event was predicted.
- `Controllability`: whether the agent can alter the outcome through its own actions.
- `Changeability`: whether some other agent can alter the outcome.

Emotion mapping:
- Appraised events are mapped into emotion instances following the OCC structural scheme.
- Current emotional state is then determined by an activation-based focus-of-attention mechanism over recent emotion instances.

Coping strategies listed in the paper:
- `Action`
- `Planning`
- `Seek instrumental support`
- `Procrastination`
- `Positive reinterpretation`
- `Acceptance`
- `Denial`
- `Mental disengagement`
- `Shift blame`
- `Seek/suppress information`

Selection rule:
- EMA proposes multiple coping strategies in parallel but adopts them sequentially.
- It prefers problem-directed strategies when control is high, procrastination when changeability is high, and emotion-focused strategies when both are low.

Pressure-engine relevance:
- This is the most directly reusable vocabulary in the paper: appraisal variables produce a structured coping/readiness field rather than a flat emotion label.

<!-- page: 5 -->
## Page 5

Figure 2, manually reconstructed:
- The figure shows a snapshot of EMA’s causal interpretation at time `t4`.
- The diagram includes:
- causal history
- future expectation
- current beliefs/predicates with utility and probability
- actions and their causal links
- appraisal frames over time

Important representation details:
- Time proceeds in discrete steps.
- Ovals represent believed world predicates.
- Rectangles represent actions or appraisal frames.
- Links show causal establishment or threat relations between states and actions.

Extra-model components required by EMA:
- a plan library
- recipes for combining actions
- stimulus-response rules
- a world simulation that computes perceptual consequences of actions

Pressure-engine relevance:
- The key takeaway is not the exact diagram notation. It is that appraisal is reading from a live causal structure that already contains goals, plans, expectations, and social inferences.

<!-- page: 6 -->
## Page 6

Bird example walkthrough:
- `t0`: the actor has the high-utility goal of being healthy; this produces joy.
- `t1`: an unexpected sound occurs; EMA records an unexpected event and triggers a `Look` action.
- `t2`: the flying bird is perceived; the bird’s presence is undesirable and certain, producing distress.
- `t3`: EMA infers the bird may attack; this threatens the health goal and is appraised as undesirable, uncertain, and uncontrollable, producing fear.
- `t4`: EMA infers a possible response, `whack the bird with the umbrella`; this makes the threat controllable and reappraises the state as anger.
- Later events overtake the plan when the bird becomes caught in another actor’s hair, shifting the appraisal target again.

Concluding claim:
- The temporal character of appraisal may be a by-product of perceptual and cognitive processes that keep altering the causal interpretation.
- EMA therefore argues for a fast, uniform appraisal process rather than separate fast and slow appraisal systems.

Tail handling:
- This page contains article conclusion followed by references.
- Safe treatment: use the conclusion as body content and treat the references as reference tail, not narrative body.
