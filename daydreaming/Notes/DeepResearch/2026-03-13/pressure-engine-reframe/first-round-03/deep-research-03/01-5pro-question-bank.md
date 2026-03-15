# 5 Pro Question Bank — Round 2

Date: 2026-03-15

## How this works

"5 Pro" is ChatGPT 5.4 Pro running in the ChatGPT interface as a chat.
Each question below gets pasted into the chat along with relevant
source files as inline context. 5 Pro replies directly in the chat
conversation. David then saves the reply as a markdown file under
`deep-research-03/replies/` (e.g., `Q5-operator-diversity.md`).

So this document is a **prompt bank**, not an API spec or agent
handoff. The "Context to include" sections list files to paste
into the chat alongside each question. The "Preferred answer format"
section goes at the end of the pasted prompt.

Prerequisite: `00-5pro-question-bank.md` was already given to
5 Pro as shared context in an earlier chat turn.

Purpose: address specific obstacles identified by the Q1/Q3/Q4
replies and independent code review. Each question asks for both an
MVP approach (cheapest thing that works now) and a higher-octane
approach (what you'd build if the MVP proves insufficient).

---

## Shared context update

Since the previous question bank, these findings are now established:

- **Operator-template collapse** is the primary diversity obstacle.
  Multi-step sequences produce the same operator winner at every step
  because the reappraisal formula only modifies `base_intensity`,
  which feeds into an operator-agnostic pressure term. Appraisal_fit,
  practice_fit, and episodic_resonance — the terms that differentiate
  operators — do not change through reappraisal. Sequences are
  structurally stationary in operator selection unless an external
  event perturbs the appraisal inputs.

- **Generated-episode self-priming** is a structural property of the
  retrieval sort order. Generated episodes inherit retrieval tags from
  the dominant concern and get the highest recency rank. After 2-3
  steps, both retrieved episodes can be self-generated material,
  crowding out authored backstory.

- **Admission hardening has moved forward.** The current batch
  admission path now includes:
  - `duplicate_function_signature`
  - `overdetermination_gain`
  - semantic-gate hard rejection
  - sequence-diversity pressure
  So the next asks should not pretend admission is still text-only.
  The real remaining question is what to do when the pool itself is
  still narrow after these controls are in place.

- **Graph-valid is not graph-useful.** The LLM generates prose
  carrying mixed pressure (e.g., attachment + obligation) but
  collapses the graph projection to the dominant concern only. The
  sidecar preserves the richness; the graph seam loses it.

- **Scene-state progression is only partially real.** The prototype
  now carries `current_active_situation_id` forward from accepted
  graph output, so later steps do not always rebuild from
  `fixture["situations"][0]`. But fixture world-state still does not
  update in a principled way, most fixtures still expose only one
  meaningful authored situation, and later-step affordances are still
  only lightly coupled to what earlier accepted nodes actually did.

- **The first real curation artifact now exists.** Kai has a batch
  curation packet that confirms the current bottleneck is not sentence
  quality but narrow graph function: strong avoidance texture, weak
  progression / closure, no pressure or practice spread.

- **Tessa is now the better diversity test.** Kai proved the batch path
  works, but its world is too narrow to tell us much about richer
  shortlist quality. Tessa now passes as a single-sequence
  rationalization benchmark on the live derived path, so it should be
  the next serious batch signal once the retrieval / stationarity
  question is answered.

- **Semantic evaluation is still heuristic.**
  `make_semantic_checks()` has been hardened for Tessa and now passes on
  the live rationalization run, but it remains a mixed structural /
  token heuristic with noise across fixtures. This affects both
  candidate compilation (`legibility` score) and batch admission
  (`semantic_expectations_failed` hard gate). Measuring diversity
  interventions still requires evaluation that can distinguish between
  real diversity and relabeled templates.

- **Prompt conditioning uses raw JSON.** `build_middle_prompt()`
  serializes CausalSlice, AppraisalFrame, PracticeContext as JSON
  blocks. The acting insight (doc 34) suggests "given circumstances"
  should feel like circumstances, not a data dump. This likely
  affects prose quality, graph projection richness, and operator
  distinctiveness.

- **Practice-fit is a static lookup table** indexed on practice_type.
  Since situation doesn't advance (Q7) and appraisal doesn't change
  through reappraisal (Q5), practice_fit contributes a fixed 0.20-
  weighted anchor to the same operator at every step. For Kai/evasion,
  practice_fit + episodic_resonance together create a 0.182 gap
  favoring avoidance — larger than any plausible fatigue penalty.

Key code: `daydreaming/authoring_time_generation_prototype.py`
Key fixtures: `daydreaming/fixtures/authoring_time_generation_*.yaml`
Key spec: `pressure-engine-reframe/30-authoring-time-generation-prototype-spec.md`
Key schema: `pressure-engine-reframe/28-l2-schema-from-5pro.md`
Key curation note: `pressure-engine-reframe/34-kai-batch-curation-packet.md`

---

## Q5. Operator diversity across multi-step sequences and batches

### The problem

The operator scoring formula is:

```
score = 0.35 * pressure
      + 0.30 * appraisal_fit
      + 0.20 * practice_fit
      + 0.20 * episodic_resonance
      - 0.10 * repetition_penalty
```

`pressure` = `base_intensity`, which is operator-agnostic. Salience
reappraisal adds +0.10 to dominant concern intensity, which lifts
all operators equally. So the same operator wins at every step unless
controllability shifts (requires `option_effect: open`, which the
model rarely emits).

The `repetition_penalty` is currently static per-family (0.05/0.08/
0.12), not accumulated from actual usage.

### What we want

Multi-step sequences and batch runs that produce material across
multiple operator families without hand-seeding the operator at
each step.

### Prompt to paste

Design two solutions:

**MVP (smallest change that works now):**

What is the cheapest mechanism to produce operator diversity across
a 3-5 step sequence and across 5 sequences in a batch? Consider:

- Operator fatigue (penalty that grows with repeated selection)
- Forced operator rotation across batch sequences
- Appraisal perturbation after sustained same-operator selection
- Practice-context evolution triggered by reappraisal
- Some combination

For each candidate mechanism:
- How many lines of code is the change?
- Does it require new fixture schema?
- Does it produce artificial diversity (forced) or emergent
  diversity (structural)?
- What's the risk of producing incoherent operator transitions?

**Higher octane (what you'd build if the MVP isn't enough):**

What would a principled solution look like? Consider:

- Operator-specific reappraisal effects (sustained avoidance should
  shift appraisal dimensions, not just pressure)
- Mueller's original mode oscillation (performance ↔ daydreaming)
  adapted for authoring-time sequences
- EMA's coping-strategy switching driven by reappraisal of coping
  outcomes (if avoidance didn't reduce the concern, switch strategy)
- Concern competition where a secondary concern can overtake the
  dominant after sustained exploration of the dominant
- Something from predictive processing: prediction-error reduction
  failure should shift the error-reduction strategy

What is the smallest version of the principled solution that could
replace the MVP if needed?

### Context to include (paste into chat)

- `authoring_time_generation_prototype.py` (score_operators,
  reappraise_concerns, run_middle_sequence)
- `28-l2-schema-from-5pro.md`
- `30-authoring-time-generation-prototype-spec.md`
- Q3 reply (`Q3-gen-quality-curation-rev.md`)

---

## Q6. Graph seam richness: from graph-valid to graph-useful

### The problem

The LLM generates prose carrying mixed pressure (attachment threat +
obligation, status damage + anticipation), but when it fills the
graph projection fields it collapses to the dominant concern only.
The provenance sidecar preserves the full picture; the graph node
loses it.

Current graph projection is generated alongside the scene text in
a single structured output call. The LLM appears to default to the
most salient concern when filling `pressure_tags[]` and
`origin_pressure_refs[]`.

Additionally, `setup_refs[]` and `payoff_refs[]` are prompt-inferred
— the LLM guesses what the scene sets up or pays off. These
sometimes feel opportunistic rather than causally grounded.

### What we want

Graph nodes that preserve the mixed-pressure signature of the prose
and have mechanically grounded setup/payoff refs.

### Prompt to paste

Design two solutions:

**MVP (smallest change that works now):**

Consider a generate-then-project pipeline:

1. LLM generates the scene text (free response, no structured
   fields)
2. A second pass extracts graph projection fields from the
   generated text + the middle-layer sidecar

Would this produce richer `pressure_tags` and `origin_pressure_refs`
than simultaneous generation + projection? What should the
extraction prompt look like? What validation should enforce
multi-concern projection when the sidecar shows multiple active
concerns?

Also consider: should the extraction pass have access to the
sidecar (causal slice, appraisal frame, active concerns) or should
it work from the generated text alone? What are the tradeoffs?

**Higher octane (POCL-lite causal sketcher):**

The three-layer compiler proposal (from earlier 5 Pro deep research)
includes a partial-order causal-link sketcher that would make
`setup_refs[]` / `payoff_refs[]` mechanically grounded:

- A small operator schema library (20-40 event-level schemas)
- Each schema has preconditions, effects, and threat relations
- Given a candidate moment, the sketcher identifies what it
  establishes, what it requires, and what it threatens
- `setup_refs` and `payoff_refs` are derived, not prompted

What would a v1 operator schema library look like for the current
benchmark fixtures (Kai attachment/avoidance, Maren obligation/
rehearsal, Rhea status/rehearsal, Tessa guilt/rationalization)?

How many schemas are needed for a useful v1?
What fields per schema?
How does the sketcher interact with the graph seam contract?
Is this additive to the generate-then-project MVP, or does it
replace the extraction pass?

### Context to include (paste into chat)

- `authoring_time_generation_prototype.py` (build_middle_prompt,
  validate_graph_projection, build_sidecar)
- `21-graph-interface-contract.md`
- Q3 reply (`Q3-gen-quality-curation-rev.md`, especially §7)
- `first-round-03/deep-research-further/03_reply.md` (§2 on POCL)

---

## Q7. Scene-state progression across multi-step sequences

### The problem

The prototype generates multi-step sequences, but later steps
rebuild situation state from `fixture["situations"][0]` rather than
carrying forward the effects of accepted steps. The fixture only
defines one active situation. So step 3 generates from the same
"unopened letter on the kitchen table" state as step 1, even if
step 1's accepted node implied "letter turned face-down, kettle
filled, harbor memory surfaced."

The Q3 review confirmed this reads in the output: "yes, step 3 is
shaped by step 1, but mostly as iterative fixation and rewrite, not
as genuine scene-state progression."

Memory carry-forward is real (generated episodes re-enter retrieval).
But situation-state carry-forward is not.

### What we want

Later steps in a sequence should generate from a situation state
that reflects what happened in earlier accepted steps. The generated
prose should feel like genuine progression, not paraphrase.

### Prompt to paste

Design two solutions:

**MVP (smallest change that works now):**

The simplest version: after each accepted step, update the active
situation description based on the accepted graph projection's
`option_effect` and the generated scene text.

Options:

1. **Template-based update:** option_effect = clarify → append
   "the choice is now sharper." option_effect = open → append
   "a new possibility has appeared." Simple, predictable, shallow.

2. **LLM-mediated situation update:** After accepting a candidate,
   ask the LLM: "Given what just happened [accepted scene text],
   update the situation description. What has changed? What remains
   unresolved? What is now sharper?" Parse the result into a
   revised situation object for the next step.

3. **Effect accumulation:** Each accepted graph projection's
   `setup_refs`, `payoff_refs`, and `option_effect` are accumulated
   into a running situation delta. The next step's prompt includes
   the original situation PLUS the accumulated deltas.

Which of these is most likely to produce genuine progression without
over-engineering? What are the failure modes of each?

**Higher octane (situation calculus / STRIPS-lite):**

The classical AI approach: define situation state as a set of
propositions. Each accepted step has add-effects and delete-effects.
The next step's situation is the original state + accumulated
effects.

What would a minimal proposition vocabulary look like for the
current fixtures? Consider:

- `letter_status: unopened | turned_face_down | opened | read`
- `harbor_memory: dormant | surfaced | actively_painful`
- `contact_status: no_response | delay_chosen | response_drafted`
- `emotional_register: contained | leaking | overwhelmed`

How many propositions per fixture?
How are they derived from accepted graph projections?
How do they interact with CausalSlice construction for the next
step?
Is this additive to the LLM-mediated update, or does it replace it?

Also consider: does this connect to the POCL-lite sketcher from Q6?
If operator schemas have preconditions and effects, do those effects
automatically produce situation-state deltas?

### Context to include (paste into chat)

- `authoring_time_generation_prototype.py` (run_middle_sequence,
  build_causal_slice, reappraise_concerns)
- `28-l2-schema-from-5pro.md`
- `pending-items-2026-03-15.md` (item 6)
- `first-round-03/deep-research-further/03_reply.md` (§2 on POCL,
  §1 on abduction)

---

## Q8. Generated-episode self-priming in retrieval

### The problem

Generated episodes re-enter the retrieval pool with:
- Retrieval tags inherited from the dominant concern at generation
  time
- The highest recency rank in the pool

Retrieval sorts by `(-score, -recency_rank, fixture_order)`. So a
generated episode that matches on 3-4 keys and has highest recency
always beats a backstory episode with the same score. After 2-3
steps, both retrieved episodes can be self-generated, creating an
echo chamber.

### What we want

Retrieval that maintains meaningful contact with authored backstory
across multi-step sequences, while still allowing generated episodes
to contribute when genuinely relevant.

### Prompt to paste

Design two solutions:

**MVP:**

- Source-weighted retrieval: backstory episodes get a 2x score
  multiplier? Or a recency bonus that authored episodes always
  carry?
- Hard slot reservation: of 2 retrieved episodes, at least 1 must
  be authored backstory?
- Recency decay: generated episodes lose recency advantage over
  time (step distance)?

Which mechanism is simplest and most robust? What happens when
authored backstory legitimately becomes less relevant than recent
generated material?

**Higher octane:**

Mueller's original reminding cascade involves recursive
reactivation — retrieving one episode activates its indices, which
may trigger further retrievals. The current prototype has a
one-hop reminder mechanism but it's limited.

Would a richer reminding cascade naturally balance self-priming
by re-surfacing authored backstory through associative paths?
Or would it make the echo chamber worse by amplifying recent
material?

What would a v1 reminding cascade look like that specifically
addresses self-priming?

### Context to include (paste into chat)

- `authoring_time_generation_prototype.py` (retrieve_episodes,
  retrieve_one_hop_reminder, run_middle_sequence)
- Mueller §5.3 and §7.5 (serendipity and episodic memory)

---

## Q9. Next intervention ordering: what actually moves diversity now?

### The problem

Several mechanisms are now in play at once:

- admission already has duplicate-function detection and pooled
  overdetermination
- retrieval still structurally favors generated episodes through
  recency
- operator selection is still sticky within a sequence because
  salience reappraisal mostly changes an operator-agnostic pressure
  term
- seam flattening still collapses mixed-pressure prose into thinner
  graph projections

So the practical question is no longer "what mechanisms exist?" but:
**which intervention should happen next, and in what order, if the
goal is to improve graph-functional diversity with the least churn?**

### What we want

A ranked, concrete answer about which one or two changes are most
likely to improve the next real batch result on `Tessa`, and which
changes should wait until after that signal exists.

### Prompt to paste

Given the current state of the code and question bank, rank these
candidate interventions by **expected short-term leverage**:

1. retrieval source-balancing
   - cap generated episodes in the retrieved set
   - or weight authored backstory more heavily

2. operator exploration policy
   - operator fatigue within a sequence
   - or forced operator rotation across batch sequences

3. richer graph projection
   - preserve multiple `pressure_tags` / `origin_pressure_refs` when
     the sidecar and prose support it

4. scene-state carry-forward
   - make later steps generate from an updated situation state rather
     than the original fixture state

For each intervention:
- what specific failure does it address?
- what observable batch change should it produce if it is working?
- what is the smallest code change that tests it honestly?
- what would make it a false friend or premature?

Then answer the practical sequencing question:

**If the next concrete milestone is a useful `Tessa` batch and then the
Kai micrograph patch test, what is the correct next order of work?**

Please be explicit about whether:
- retrieval balancing should happen before any operator exploration
- operator exploration is necessary now or only if Tessa remains narrow
- seam-rich projection should be blocked on batch results or pulled
  forward first
- scene-state carry-forward matters before the first patch test or only
  after

### Context to include (paste into chat)

- `authoring_time_generation_prototype.py`
- `34-kai-batch-curation-packet.md`
- `Q1-batch-admission-answer.md`
- `Q3-gen-quality-curation-rev.md`
- `Q4-end-to-end.md`
- this question bank (`01-5pro-question-bank.md`)

---

## Q10. Evaluation infrastructure: can we actually measure diversity?

### The problem

All the interventions in Q5-Q9 assume we can measure their effect.
But the evaluation layer is built on keyword-match semantic checks.

`make_semantic_checks()` (line ~1818) works like this:

- "delay ritual is present" checks for `kettle|coffee|scrub|wipe|
  counter|faucet`
- "rationalization reframes rather than resolves" requires BOTH a
  justification marker (`because|so that|had to|needed|in order`)
  AND a harm marker (`toast|credit|what I did|what I said`)
- "the harbor remains psychologically active" fires on the word
  `harbor`

These checks have two failure modes:

1. **False negatives:** The strict rationalization predicate fails on
   every selected Tessa step (from Q2/Q2a review), even when the
   prose is clearly doing rationalization. A model that writes
   "the simplification was necessary" instead of "she had to
   simplify" fails the check.

2. **False positives:** Any text containing "harbor" passes the
   harbor-presence check regardless of context. "Small printed
   letters" triggered the letter-presence check before whole-word
   matching was added.

This matters at two levels:

- **Candidate compilation:** `legibility` = fraction of semantic
  checks that pass. Keyword sensitivity directly affects which
  candidate wins compilation scoring.
- **Batch admission:** `semantic_expectations_failed` is a hard gate
  requiring ≥75% pass rate. A candidate that does genuine
  rationalization in unfamiliar vocabulary gets rejected.

If the evaluation can't distinguish real operator diversity from
relabeled templates, then measuring the effect of Q5's operator
rotation or Q8's retrieval balancing is unreliable.

Q5's reply sharpens this: "If evaluation cannot distinguish a
genuine rationalization beat from an avoidance beat wearing
rationalization tokens, you will over-credit the MVP." And Q5's
MVP adds trace signals (`history_penalty`, `exploration_bonus`,
`sequence_target_family`) that are structural, not prose-dependent.
So there may be two evaluation tiers:

- **Structural tier:** Did different operators actually win? Was
  the exploration bonus the reason? Did graph projections carry
  different pressure_tags and option_effects? These signals don't
  depend on keyword checks.
- **Prose tier:** Is the generated text actually *doing* the
  operator's dramatic work, or just wearing its label? This is
  where the semantic checks matter.

The structural tier is already partially available through trace
output and admission metrics. The prose tier is the weak link.

### What we want

Evaluation that can tell us whether two candidates are doing the
same dramatic work, regardless of surface vocabulary. Not an LLM
judge (too slow for batch), but something better than keyword
presence. And clarity about which evaluation questions can be
answered structurally (from traces and graph projections) vs.
which require prose-level checks.

### Prompt to paste

**MVP:**

The current checks are per-fixture hand-authored predicates. Can
they be made structural without becoming an LLM call?

Consider:

- **Structural cue families** instead of exact tokens: require ONE
  of a family of causal connectives, not a specific phrase. Require
  acknowledgment of the specific harm type (from the fixture's
  concern definition), not a fixed token list.

- **Negation guards:** "she did NOT go to the harbor" should not
  pass "harbor is psychologically active."

- **Cross-operator discrimination tests:** A check that passes for
  avoidance should fail for rationalization and vice versa. Currently
  the checks are independent — a single text can pass both
  avoidance and rationalization checks if it contains tokens from
  both families.

- **Graph-projection consistency checks:** Instead of checking the
  prose, check whether the graph projection's `pressure_tags`,
  `operator_family`, and `option_effect` are internally consistent
  AND consistent with the sidecar's appraisal frame. This moves
  evaluation from prose surface to structural coherence.

How many of the current per-fixture checks can be replaced with
fixture-parameterized structural checks (where the fixture supplies
the concern type, harm description, and practice type, and the
check logic is shared)?

**Higher octane:**

An embedding-based semantic discriminator: embed the generated text,
embed a small exemplar set per operator family (3-5 exemplars each
for avoidance, rehearsal, rationalization), compute cosine distance
to each cluster. The candidate's operator assignment should place it
closest to the matching cluster.

- Would this work with a small open embedding model (e.g., a
  sentence transformer)?
- How many exemplars per cluster are needed for reliable
  discrimination?
- Can the exemplars be drawn from existing accepted batch outputs?
- What's the latency overhead per candidate?
- Is this an admission gate, a compilation scoring term, or a
  post-hoc batch evaluation?

### Context to include (paste into chat)

- `authoring_time_generation_prototype.py` (make_semantic_checks,
  score_candidate_for_compilation, admit_candidate_pool)
- Q2/Q2a replies on Tessa semantic check failures
- `31-generation-experiment-review-checklist.md`

---

## Q11. Prompt architecture: JSON dump vs. given circumstances

### The problem

`build_middle_prompt()` (line ~1038) serializes the full middle-layer
state — CausalSlice, AppraisalFrame, PracticeContext, retrieved
episodes, operator scoring breakdown — as raw JSON blocks inside the
generation prompt. The LLM receives something like:

```
CAUSAL SLICE:
{"temporal_status": "prospective", "threatened_goal": "preserving
connection with Maren", "blocker": "the letter implies a claim he
is not ready to process", ...}

APPRAISAL FRAME:
{"controllability": 0.28, "likelihood": 0.82, "praiseworthiness":
-0.15, ...}
```

This is a lot of structured data that the LLM has to parse and
translate into behavioral prose. The acting insight (doc 34) says
the design target is "given circumstances" — what the character
wants, what's in the way, what they're doing about it, what just
happened. But the current prompt gives the model a data dump, not
given circumstances.

This likely affects:

- **Prose quality:** The model may "indicate" (narrate the emotion
  rather than showing it through action) because the data format
  invites analytical processing rather than behavioral generation.

- **Graph projection richness (Q6):** If the model is already in
  "fill structured fields" mode from parsing JSON input, it may
  default to mechanical projection rather than rich interpretation.

- **Operator distinctiveness (Q5):** Different operators should
  produce different prose textures. But if the prompt presents all
  operators the same way (as JSON with an operator_family label),
  the model has to do the texture differentiation itself. Q5's
  reply flagged this directly: "Because `build_middle_prompt()`
  names the selected operator explicitly, the model may comply
  with the label more than with the changed situation logic."
  A natural-language prompt would let the model infer behavior
  from circumstances rather than comply with a named label.

### What we want

A prompt format that embodies the acting principle: give the model
the given circumstances in a form that naturally produces action
under pressure, not emotional performance.

### Prompt to paste

**MVP:**

Translate the JSON middle-layer state into a natural-language
"given circumstances" block. For example:

```
THE SITUATION:
Kai is alone in his kitchen. Maren's letter arrived this morning
and sits unopened on the table. He has not spoken to her in months.

WHAT HE WANTS:
To preserve his connection with Maren without confronting what the
letter might say.

WHAT'S IN THE WAY:
The letter forces a choice he's not ready to make. He can't control
what it says. It's probably about [the thing he's been avoiding].

WHAT HE'S DOING ABOUT IT:
Avoidance. Filling time with domestic routine so the moment of
opening keeps not arriving.

WHAT JUST HAPPENED:
He noticed the letter again after putting the kettle on. His hand
went to it, then pulled back.
```

Compare this to the current JSON block. Predict:

- Does the natural-language version produce more "action under
  pressure" and less "emotional performance"?
- Does it produce richer graph projections (more pressure_tags)?
- Does it make operator distinctiveness more legible?
- What information is lost in translation from JSON to natural
  language?
- Is the translation automatable from the middle-layer objects, or
  does it require per-fixture authoring?

**Higher octane:**

The Meisner technique uses "repetition" — the actor responds to what
the partner just did, not to an internal script. For multi-step
sequences, the prompt at step N should be shaped by the accepted
text from step N-1, not just by the updated middle-layer state.

What would a "reactive prompt" look like where step N's given
circumstances are derived from:

1. The accepted scene text from step N-1 (what just happened, in
   the character's terms)
2. The updated concern intensities (what shifted)
3. The retrieved episode (what memory surfaced)

Rather than from:

1. The raw fixture situation (which hasn't changed)
2. The rebuilt CausalSlice (derived from the same fixture)
3. The full appraisal frame as JSON

Does this connect to Q7 (scene-state progression)? If the prompt
at step N already embodies "what happened at step N-1," does
explicit situation-state carry-forward become less necessary?

### Context to include (paste into chat)

- `authoring_time_generation_prototype.py` (build_middle_prompt,
  build_operator_behavior_description)
- `34-broader-application-surface.md` (§ acting craft principles)
- `31-generation-experiment-review-checklist.md`
- Q3 reply (§ "action under pressure" evaluation)

---

## Q12. Practice-context stationarity: the third anchor

### The problem

Q5 identifies that `pressure` is operator-agnostic (doesn't
differentiate operators). But there's a second stationarity problem
that Q5 doesn't cover: `practice_fit` is a static lookup table.

`practice_fit` (lines ~727-735) is hardcoded per practice type:

```
evasion:    rehearsal=0.35  rationalization=0.58  avoidance=0.90
confession: rehearsal=0.26  rationalization=0.78  avoidance=0.42
anticipated_confrontation: rehearsal=0.88  rationalization=0.32
                           avoidance=0.45
```

This table is indexed on `practice_type`, which is derived from
`derive_practice_context()`, which reads from the situation and
appraisal frame. Since the situation doesn't advance (Q7) and the
appraisal frame doesn't change through reappraisal (Q5), the
practice type is also fixed within a sequence.

So even after fixing:
- Q5 (operator fatigue / exploration)
- Q7 (situation carry-forward)
- Q8 (retrieval self-priming)

...the practice_fit contribution at weight 0.20 continues anchoring
the same operator unless the practice type itself shifts.

For Kai: `evasion` gives avoidance 0.90 at every step. That's a
0.18 score contribution that doesn't budge. For avoidance to lose,
everything else has to overcome that 0.18 floor — which is hard
because `episodic_resonance` with evasion episodes also favors
avoidance (0.34 per evasion episode × 0.20 weight).

Together, `practice_fit + episodic_resonance` contribute
`(0.90 + 0.68) * 0.20 = 0.316` to avoidance in the Kai evasion
case. Rehearsal gets `(0.35 + 0.32) * 0.20 = 0.134`. That's a
0.182 gap from these two terms alone — larger than any plausible
fatigue penalty.

### What we want

Practice context that can evolve within a sequence, reflecting that
sustained avoidance in an evasion context may shift the social frame
toward confrontation, or that sustained rationalization in a
confession context may shift toward evasion (giving up on
confessing).

### Prompt to paste

**MVP:**

Should `practice_fit` respond to within-sequence history?

Consider:

- **Practice-fit decay:** After N consecutive steps with the same
  practice type anchoring the same operator, reduce the practice_fit
  contribution by a decay factor. E.g., step 1: full 0.90, step 2:
  0.72, step 3: 0.58. This weakens the anchor without changing the
  practice type itself.

- **Practice-type evolution rules:** After 2 steps of evasion where
  avoidance won, the practice context shifts to "exhausted_evasion"
  or "emerging_confrontation" — a new practice type with different
  fit constants. This is more principled but requires defining
  transition rules.

- **Practice-fit from retrieval:** Instead of a hardcoded table,
  derive practice_fit from the retrieved episodes' practice types.
  If step 3's retrieval includes a confrontation episode (because
  the retrieval self-priming fix from Q8 resurfaced backstory), the
  practice_fit should partially reflect confrontation, not just
  the fixture's base practice type.

Which mechanism is most defensible? Does Mueller's original system
have practice-type transitions, or is this an extension?

**Higher octane:**

Versu's social practices have entry conditions, exit conditions,
and affordance menus that change based on what moves have been made.
A practice is not just a label — it's a state machine.

What would a minimal practice state machine look like for the
current four fixtures? Consider:

- Evasion → {sustained_evasion, confrontation_threshold,
  exhausted_displacement}
- Confession → {initial_confession, rationalized_confession,
  abandoned_confession}
- Anticipated_confrontation → {preparation, threshold,
  post_confrontation}

How many transitions per practice type?
What triggers transitions?
How do transitions interact with operator scoring?
Is this additive to the static lookup table, or does it replace it?

### Context to include (paste into chat)

- `authoring_time_generation_prototype.py` (score_operators
  practice_fit section, derive_practice_context)
- `07-versu-extraction.md` (social practices, affordance menus)
- `28-l2-schema-from-5pro.md` (PracticeContextV1)
- Q5 (operator stationarity) — this question addresses the
  remaining stationarity after Q5's fix

---

## Q13. Retrieval-source balancing vs. batch-time operator exploration

### The problem

Two diversity blockers are now clearer than they were when Q5/Q8 were
written:

1. **Generated-episode self-priming is structural.**
   `build_generated_episode()` inherits the current concern /
   situation / practice tags and assigns `max_recency + 1`.
   `retrieve_episodes()` sorts by `(-score, -recency_rank,
   fixture_order)`. So a same-score generated episode will beat
   authored backstory on the next step.

2. **Operator winners are sticky in the salience lane.**
   Reappraisal usually changes `base_intensity`, which feeds the
   operator-agnostic `pressure` term. The terms that differentiate
   operators (`appraisal_fit`, `practice_fit`,
   `episodic_resonance`, `family_bonus`) barely move unless an
   external event or different retrieval signal perturbs them.

Admission hardening is already in place:
- `duplicate_function_signature`
- `overdetermination_gain`
- semantic-gate rejection
- sequence-diversity pressure

So the next question is not "how do we detect duplicates?" but
"what is the cheapest intervention that actually widens the pool
before admission?"

### What we want

A batch policy that broadens the candidate pool without silently
distorting the benchmark. The pool should remain backstory-grounded,
not collapse into self-echo, and should be able to produce
functionally distinct candidates before the admission layer starts
throwing things away.

### Prompt to paste

Design two solutions:

**MVP (smallest change that works now):**

Compare these interventions directly and rank them:

- **Retrieval-source balancing**
  - cap generated episodes at `1 of 2` retrieved slots
  - weight backstory above generated on equal score
  - decay generated recency faster than authored backstory
- **Batch-time exploration policy**
  - operator fatigue penalty that accumulates within a sequence
  - forced operator seeding / rotation across sequences
  - alternating retrieval bias across sequences
    (e.g. one sequence backstory-heavy, another generated-heavy)

For each intervention:
- What concrete code change is required?
- Does it distort the benchmark or merely remove a structural bias?
- What metric would tell us it worked?
- In what order should we introduce these changes?

**Higher octane (if the MVP is not enough):**

What would a principled **batch controller** look like?

Possibilities:
- a coverage target over operator family / practice / pressure
- a bandit-style exploration policy over sequence starts
- retrieval-source quotas that adapt when the pool becomes self-echoing
- an explicit "seek a different graph function than what is already in
  the shortlist" controller

What is the smallest controller that is actually worth building if
simple source balancing + light exploration still leaves the pool
narrow?

### Context to include (paste into chat)

- `authoring_time_generation_prototype.py`
  - `retrieve_episodes`
  - `build_generated_episode`
  - `run_middle_sequence`
  - `admit_candidate_pool`
- `34-kai-batch-curation-packet.md`
- Q1 reply (`Q1-batch-admission-rev.md`)
- Q3 reply (`Q3-gen-quality-curation-rev.md`)

---

## Q14. Patch-test bridge: generated-curated patch into a hand-authored micrograph

### The problem

The prototype now proves:
- operator control across four fixtures
- graph-valid generation
- multi-step accumulation
- batch fanout + admission

What it does **not** yet prove is that generated-curated material can
replace a region of a real graph and still behave well under traversal.

The current best next milestone is a **patch test**:
- one character (`Kai`)
- one small authored micrograph
- one generated-curated region inserted into it
- same scheduler, same seed set, same conductor

### What we want

A small, decisive end-to-end experiment that can fail cleanly at one
of three layers:
- generation
- admission / curation
- traversal

### Prompt to paste

Design two solutions:

**MVP (smallest decisive patch test):**

How exactly should we stage the first Kai patch test?

Please specify:
- the size of the authored micrograph
- how many authored scaffold nodes vs generated patch nodes
- the shape of the patch region
  (e.g. precontact kitchen only, 4 nodes)
- the minimum keeper threshold before traversal
  (e.g. 4 distinct keepers from ~16 candidates)
- what the traversal comparison should measure
  (looping, flattening, missed payoff, pressure continuity,
  conductor responsiveness)

Also specify:
- what to hold constant between H (hand-authored) and G
  (generated patch) conditions
- what should count as a generation failure vs admission failure vs
  traversal failure

**Higher octane (if the MVP works):**

What is the smallest extension after the first patch test?

Possibilities:
- patch two regions instead of one
- compare multiple generated patches against one scaffold
- allow generated nodes to contribute richer multi-pressure graph
  projection than the authored baseline
- expand from Kai to Tessa or Rhea as a second patch-test case

What is the next experiment that actually increases confidence rather
than just repeating the same result?

### Context to include (paste into chat)

- `34-kai-batch-curation-packet.md`
- Q4 reply (`Q4-gen-patch-test-bridge.md`)
- `21-graph-interface-contract.md`
- `30-authoring-time-generation-prototype-spec.md`

---

## Preferred answer format

For each question:

1. **MVP recommendation** — the one change to make now
2. **Why it works** — the mechanism, not just the claim
3. **Failure modes** — how the MVP breaks
4. **Higher-octane design** — what to build when the MVP fails
5. **Connection to other questions** — where the answers interact
   (e.g., Q6's POCL sketcher + Q7's situation effects)
6. **What NOT to build yet** — explicit scope boundary

Be concrete. Reference the actual code structures and fixture
shapes. Do not redesign the architecture.
