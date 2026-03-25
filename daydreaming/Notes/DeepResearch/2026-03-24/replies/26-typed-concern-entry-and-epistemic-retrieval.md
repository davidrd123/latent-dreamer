You should move now.

But not to “LLM concerns” in the strong sense.
Move to one bounded proposal seam with a stricter rule:

**the model may nominate provisional issue objects from bounded source packets; it may not activate them directly, durable-promote them, lower retrieval thresholds, or open rule access.**

That is the clean reconciliation between the March 24 strategy notes and the March 22 boundary reviews.

The March 24 notes already define the trigger: the 100-cycle Graffito attractor has stabilized, no new clearly reusable Graffito seam is opening, and the next productive move may be one bounded collaborator-side proposal experiment rather than another benchmark-local bridge. The March 22 reviews do not really argue against that. They argue against a sloppier move: letting LLM output behave like policy, admission, or rule-access authority.

## 0. Decision rule

My answer is **yes, move now**.

But use this exact rule:

Move now to one bounded collaborator-side proposal seam **iff** all four are true:

1. the 20 / 50 / 100-cycle reads agree that the current miniworld has settled into the same attractor,
2. the last two observation passes exposed no new clearly reusable kernel seam,
3. the first proposal seam can be represented as append-only proposal traces plus provisional issue objects,
4. there is **no path** in v1 from model proposal → `:durable` authority / rule access / retrieval-threshold change / external action.

Do **one more kernel-side pass first** only if one of these is false:

1. you still cannot read `proposed -> validated -> admitted -> retrieved -> used -> outcome -> persisted/dropped` without reconstructing it by hand,
2. you do not yet have a validator that can reject **ungrounded, duplicate, or already-settled** issue proposals,
3. the only way to make issue proposals useful is to piggyback on the current evaluator authority path.

That is the key distinction:

- **knowledge-entry seam** → ready now
- **control-substrate rewrite** → not ready now

So do **not** spend another general sprint on use/outcome hardening before testing a live knowledge-entry seam. But also do **not** let the first knowledge-entry seam inherit the evaluator’s current authority leak.

## 1. First bounded proposal seam

### Pick this one first

**Typed issue / concern initiation** is the right first seam.

Not retrieval reformulation.
Not contradiction-only.

Why:

1. it plugs directly into the kernel’s strongest existing asset: one attention economy,
2. it tests the actual missing layer: how new unresolved material enters the system,
3. it is easy to falsify,
4. it does not require replacing planning or retrieval strategy wholesale.

### Why not the other two first?

**Contradiction / contestation proposal** should be a subtype of issue initiation, not a separate first seam.
It is too narrow as the first experiment. You would get a critic without a broader agenda.

**Retrieval reformulation** is later.
Right now you do not yet have enough first-class epistemic objects for retrieval reformulation to be meaningfully observable. If you do it first, success collapses into “the prompt got a bit smarter.” That is too slippery to falsify.

So the order I would use is:

1. **typed issue initiation**
2. **contradiction / contestation as a sharpened issue subtype**
3. **retrieval reformulation over the now-real issue/epistemic layer**

## 2. The first object should be an issue, not a full graph ontology

Do not start by trying to extract a full discourse graph.
Do not start with poetic ontology either.

Start with one boring umbrella object:

- `:issue`

with a small subtype enum:

- `:question`
- `:decision`
- `:contradiction`
- `:hypothesis`
- `:commitment`
- `:aesthetic-bet`

That keeps the first seam aligned with the current concern economy while still borrowing the better epistemic object discipline from the discourse-graph notes.

### Why issue first?

Because the kernel already knows what to do with unresolved pressure.
It does **not** yet know what to do with a large typed graph of claims and evidence.

So the bridge object is:

- epistemically sharper than an episode,
- narrow enough to map into concern competition,
- small enough to validate strictly,
- boring enough not to turn into ontology theater.

Claims and evidence should exist in v1 only as:

- grounded source spans,
- support / opposition records,
- later compilation targets if the seam proves real.

## 3. Model output packet vs admitted issue object

Do not let the model emit the final kernel object directly.
Use a two-step shape:

1. **proposal packet** from the model
2. **admitted issue object** from the kernel normalizer / validator

That split matters for the same reason it mattered in the family evaluator notes: the model should not be sole judge of admissibility.

### 3.1 Proposal packet the model may emit

```clojure
{:schema/version 1
 :packet/id "pkt-20260326-a"
 :source/packet-id "src-20260326-a"
 :proposals
 [{:proposal/id "prop-001"
   :proposal/type :issue
   :issue/kind :decision
   :issue/title "Move now to a bounded proposal seam or harden the kernel first"
   :source/phrasing ["bounded collaborator-side proposal experiment"
                     "one more kernel-side use/outcome generalization pass"]
   :why/unresolved "Recent notes disagree on whether the stabilized attractor is enough to justify the first proposal seam now."
   :why/now "The current attractor has stabilized and no new clearly reusable Graffito seam is opening."
   :source/spans [{:doc "right-now.md"
                   :span-id "s1"
                   :quote "If that read stabilizes without exposing another clear reusable Graffito seam, the next branch may be one bounded collaborator-side proposal experiment"
                   :span/role :support}
                  {:doc "14-kernel-review.md"
                   :span-id "s2"
                   :quote "The safe rule is: LLM proposes triage, kernel decides transitions"
                   :span/role :counterpressure}]
   :anchor/phrases {:topics ["proposal seam" "use/outcome generalization"]
                    :tensions [["move now" "harden first"]
                               ["proposal" "authority"]]
                    :stakes ["kernel integrity" "next branch choice"]
                    :actors ["kernel" "LLM" "Graffito"]}
   :timing/hint :now
   :opposing/span-ids ["s2"]}]
 :abstain/reason nil}
```

### 3.2 Kernel-admitted issue object

```clojure
{:schema/version 1
 :object/type :issue
 :issue/id "iss-20260326-001"
 :issue/kind :decision
 :issue/status :provisional     ;; :trace | :provisional | :durable
 :issue/activation :cold        ;; :cold | :warm | :active
 :issue/title "Move now to a bounded proposal seam or harden the kernel first"
 :issue/source-packet-id "src-20260326-a"
 :issue/source-spans ["s1" "s2"]
 :issue/source-phrasing ["bounded collaborator-side proposal experiment"
                         "one more kernel-side use/outcome generalization pass"]
 :issue/normalized-label :issue/choose-first-proposal-seam
 :issue/content-cues {:topic [:topic/typed-proposal-entry
                              :topic/use-outcome]
                      :tension [:tension/pivot-vs-hardening
                                :tension/proposal-vs-authority]
                      :stake [:stake/kernel-integrity
                              :stake/next-branch]
                      :actor [:actor/kernel
                              :actor/llm
                              :actor/graffito]
                      :task [:task/choose-next-branch]}
 :issue/support-tags [:issue-kind/decision
                      :surface/architecture-review
                      :admission/provisional]
 :issue/provenance {:proposed-by :llm
                    :validated-by :kernel
                    :proposal-id "prop-001"}
 :issue/outcome-log []
 :issue/human-uptake {:freeze 0 :dismiss 0 :respond 0 :cut 0}}
```

That is thin, and in v1 thin is correct.

## 4. Should the first proposal become a real concern immediately?

No.

The first proposal should enter as a **provisional issue object** only.
It should create live concern pressure **only after kernel validation and activation**.

That gives you three distinct states:

1. **proposal** — model output only
2. **issue** — kernel-admitted unresolved object
3. **active concern** — issue that entered the attention economy

Do not collapse these.

### The smallest adapter is basically right

Yes, the smallest adapter is close to:

- issue id
- source provenance
- bridge-index bundle
- small status enum
- kernel-derived activation strength

That is not too thin for v1. That is the right thickness.

What it should **not** include in v1:

- free-form model confidence scores,
- direct motivation strength from the model,
- direct `:durable` requests that the kernel obeys,
- direct retrieval threshold edits,
- direct scheduling / interruption rights.

## 5. Prompt design for reliable typed proposals

Use a two-pass prompt, not one giant clever prompt.

### Pass A — grounded extraction

Task: find up to **three** unresolved issues in a bounded packet, or abstain.

Rules:

- `0` proposals is allowed.
- each proposal needs at least **two grounded spans**,
- at least one span must show unresolvedness, conflict, or decision pressure,
- include one opposing span or explicitly say none was found,
- do not use kernel jargon in the title unless it appears in the source.

### Pass B — normalization

Task: map anchor phrases from Pass A into canonical bridge indices.

This is where controlled vocab and aliasing live.
Do **not** ask the model to invent canonical index tokens in the same pass where it is extracting the issue.

### Good prompt constraints

Use these.

1. **Return at most 3 proposals.** Scarcity is part of the mechanism.
2. **Allow abstention.** Otherwise the model will manufacture concerns.
3. **Require exact source quotes.** Not summaries.
4. **Require one opposing span.** This sharply reduces sycophantic one-sided extraction.
5. **Separate `source_phrasing` from `normalized_label`.** This is the cleanest defense against jargon mirroring.
6. **Ban project jargon in user-facing labels unless quoted.** `concern`, `membrane`, `rule access`, `retrieval reformulation` should not magically appear in the source-facing title unless the source itself used them.

## 6. Concrete validation at the kernel boundary

Validation has to be semantic, not just JSON cleanup.

Use this chain.

### 6.1 Structural validation

- schema version valid
- enum values valid
- max proposals per packet <= 3
- all cited spans exist
- quoted text matches source exactly

### 6.2 Grounding validation

Reject if any of these are true:

- fewer than 2 grounded spans,
- all spans come from one sentence/paragraph and merely paraphrase each other,
- there is no visible unresolvedness cue,
- the issue title cannot be recovered from the source without project jargon.

### 6.3 Unresolvedness validation

An issue must satisfy at least one:

- explicit open question,
- explicit decision point,
- explicit contradiction between sources,
- repeated “next / later / blocked / not yet settled” signal,
- repeated avoidance or oscillation across notes.

If none of those is present, store as `:trace` or reject.

### 6.4 Settledness validation

Reject or demote if a later standing note clearly settles it.

Example:

- if `right-now.md` or a newer canonical note already chose the branch,
- do not re-admit the older disagreement as a live issue.

### 6.5 Deduplication

Do not dedupe on title string alone.
Dedupe on canonical bridge indices + issue kind + normalized label.

If near-duplicate of an already-open issue:

- merge new spans into the existing issue,
- increment corroboration count,
- do not create a second open issue.

### 6.6 Activation gate

Not every admitted issue becomes active.

Use a kernel-side activation heuristic like:

```text
activation_score =
  +2 if issue-kind ∈ {decision, contradiction}
  +2 if source packet includes a standing-now / next-step note
  +1 for each corroborating document beyond the first
  +1 if bridge indices overlap an active/durable object across type
  -2 if near-duplicate of an already-active issue
  -2 if only one source surface is involved
```

Only `activation_score >= 4` enters the live concern economy.

That keeps the kernel scarce.

## 7. Urgency / ripeness / interruptibility

Keep return-ticket timing mostly **out of scope** for the first experiment.

The first schema may carry a coarse `:timing/hint` enum:

- `:now`
- `:soon`
- `:later`

But treat it as **logged advisory metadata only**.
Do not let it drive interruption policy yet.

Why:

- the model is decent at noticing salience,
- it is bad at calibrated interruption rights,
- timing is relational state, and the relational layer is not what you are testing first.

So:

- record timing hints now,
- do not give them policy force yet.

## 8. Human uptake: include from day one, but not as an admission gate

Human uptake should be in the first experiment as **logged evidence**, not as a prerequisite for entry.

Use the vault-style uptake signals because they are concrete:

- `freeze`
- `dismiss`
- `respond`
- `cut`

Interpret them like this:

- **freeze** → “keep warm”; positive persistence evidence
- **dismiss** → negative evidence; candidate demotion
- **respond** → strongest uptake signal; issue shaped a real exchange
- **cut** → local/contextual suppression, not global deletion

So yes: include uptake logging from day one.
But no: do not require human uptake for first admission.

## 9. Retrieval indices for epistemic objects

Coincidence-mark retrieval over epistemic objects is **not** a bad idea.
Keep it.

But do not try to make claims, questions, contradictions, commitments, and episodes all retrieve each other through one flat soup of tags.

Use a **shared bridge-index layer**.

### 9.1 Three cue zones still apply

Exactly as in the current kernel:

1. **content cues** — counted toward threshold
2. **provenance** — tie-break only
3. **support tags** — metadata only

For epistemic objects, the mistake would be to let `:issue-kind/contradiction` or `:surface/code-review` count toward firing. Those should be support tags, not content cues.

### 9.2 The bridge-index families

Use these as content cues:

#### A. `:topic/*`
What the object is about.

Examples:

- `:topic/typed-proposal-entry`
- `:topic/use-outcome`
- `:topic/common-ground`
- `:topic/contradiction-detection`

#### B. `:tension/*`
What fracture or opposition is live.
This is the most important new family.

Examples:

- `:tension/pivot-vs-hardening`
- `:tension/proposal-vs-authority`
- `:tension/reuse-vs-groove`
- `:tension/mirroring-vs-grounding`

#### C. `:stake/*`
Why it matters.

Examples:

- `:stake/kernel-integrity`
- `:stake/next-branch`
- `:stake/collaboration-quality`
- `:stake/truthfulness`

#### D. `:actor/*`
Who or what is implicated.

Examples:

- `:actor/kernel`
- `:actor/llm`
- `:actor/graffito`
- `:actor/vault`

#### E. `:project/*`
Which working context it belongs to.

Examples:

- `:project/collaborative-pivot`
- `:project/graffito`
- `:project/symbiotic-vault`

#### F. `:task/*`
What kind of move this issue wants.

Examples:

- `:task/choose-next-branch`
- `:task/verify-boundary`
- `:task/test-contradiction`
- `:task/name-unresolvedness`

#### G. `:term/*`
Load-bearing source terms that recur across surfaces.

Examples:

- `:term/common-ground`
- `:term/issue-centered`
- `:term/proposer-committer`

These should be used sparingly. They are useful when a specific term genuinely keeps recurring.

### 9.3 Support tags, not content cues

Keep these **out** of threshold counts:

- `:issue-kind/*`
- `:epistemic-status/*`
- `:surface/*`
- `:admission/*`
- `:source-doc/*`
- `:timing/*`

Otherwise everything of the same object class will clump.

### 9.4 How different object kinds should index

#### Contradiction
Must carry at least:

- one `:tension/*`
- one `:topic/*`
- one `:stake/*`

Optional:

- refs to the two opposed span clusters

#### Hypothesis
Must carry at least:

- one `:topic/*`
- one `:stake/*`
- one `:task/test-*`

#### Commitment
Must carry at least:

- one `:task/*`
- one `:stake/*`
- one `:actor/*`

#### Question
Must carry at least:

- one `:topic/*`
- one `:task/*`
- one `:stake/*` or `:tension/*`

That makes retrieval about the unresolved shape, not just topical overlap.

## 10. Mixed-object retrieval: issues + episodes

Do not replace episodes in v1.
Add issues alongside them.

### The right retrieval design

**Both should race through a shared bridge-index layer.**

Not:

- issue store only,
- episode store only,
- one collapsed universal object store.

### Retrieval policy

1. active concern / issue emits bridge indices,
2. those indices query both issue and episode stores,
3. each store has its own threshold and budget,
4. returned objects are merged and re-ranked,
5. provenance and support tags may break ties, but not fire retrieval.

### The right bridge between issues and episodes

The best bridge indices are:

- `:topic/*`
- `:tension/*`
- `:stake/*`
- `:actor/*`
- `:project/*`

That is what lets an unresolved issue retrieve an older episode without collapsing the two types into one.

### Extra gate to prevent abstract drift

Do not allow issue retrieval on `:topic/*` alone.
Require:

- `topic + tension`, or
- `topic + stake`, or
- `topic + actor + project`

Likewise, when issues retrieve episodes, require at least one bridge hit that is not purely topical.

Otherwise abstract issues will drag in generic history.

## 11. The membrane transfer question

The membrane should transfer structurally, but **not with the same evidence semantics**.

That is important.

For family-plan episodes, promotion evidence was things like cross-family reuse and later outcome.
For epistemic objects, that exact phrase does not make sense.

So keep the same machinery:

- `:trace / :provisional / :durable`
- cue zones
- use/outcome logging
- stale/backfired/contradicted suppression

But change what counts as promotion evidence.

### For issues, the analogue of cross-family use is:

**cross-surface corroboration + cross-object reuse + human uptake**

That means:

1. issue appears in more than one independent source packet,
2. issue retrieves or is retrieved by a different object type,
3. issue receives positive human uptake,
4. issue transforms into a more specific downstream object or decision,
5. issue survives without being settled or contradicted.

### Issue promotion rule I would actually use

Promote `:provisional -> :durable` only if:

- `distinct-source-packets >= 2`
- **and** one of:
  - `freeze >= 1`
  - `respond >= 1`
  - `cross-object-success >= 1`
  - `spawned-downstream-object >= 1`
- **and not** dismissed / resolved / contradicted

### Issue demotion / suppression rules

Demote or suppress if any of these hold:

- dismissed twice,
- explicitly resolved in a later standing note,
- contradicted by later material,
- resurfaced 3+ times with no uptake or downstream effect,
- only ever recurs on the same source surface.

That last one is the epistemic analogue of `:same-family-loop`.
Call it:

- `:same-surface-loop`

An issue that only recurs inside architecture reviews and never surfaces in standing notes, drafts, decisions, or replies should not get more authority forever.

## 12. The overgeneration trap

Do **not** flood the concern economy and hope competition sorts it out.
That is wrong.

The attention economy is valuable precisely because entry is scarce.
If you let the model dump ten plausible concerns per chunk, you will turn the kernel into a salience landfill.

### The right throttle

Use **both**:

1. a hard limit on proposals per packet,
2. a kernel-side quality gate before activation.

My recommendation:

- max proposals emitted per packet: **3**
- max admitted `:provisional` issues per packet: **2**
- max newly `:active` issues per packet: **1**

Also allow:

- `0` proposals
- `0` active issues

If the model cannot abstain, the seam will rot.

## 13. The mirroring / jargon trap

This is real.

If the source is from this repo, the model will happily echo your own architecture language back at you and call it insight.

The best defenses are mechanical, not moral.

### Use all four

1. **Separate `source_phrasing` from `normalized_label`.**
2. **Require exact source spans.**
3. **Ban kernel jargon in issue titles unless quoted.**
4. **Run a recoverability check:** can a validator reconstruct why this issue exists from the cited source spans alone?

A fifth useful guard:

5. **require at least one source phrase that is not already a repo keyword**.

If the issue can only be named in repo-internal jargon, it is probably normalization theater.

## 14. Source-material ranking

### Best source for the very first bounded typed-concern experiment

**1. A bounded code review / architecture discussion packet from this repo**

This is the best first source.

Why:

- high unresolved-concern density,
- explicit contradictions and decision points,
- source spans are easy to validate,
- downstream success is easy to judge,
- it exercises epistemic pressure without dragging in full relational-state complexity.

This is where the first experiment should live.

### Best source for stress-testing contradiction detection

**2. A research reading session with annotations**

This is better than internal chat for contradiction testing because the source already has clearer claim/evidence structure.

### Best source for avoiding overfitting to existing architecture language

**3. A research reading session with annotations**

Same reason.
It gives you epistemic objects without the repo’s own self-description contaminating the labels.

### Second-best general source

**4. Project notes and research sifts from this repo**

Good density, good recurrence, still fairly easy to validate.
Slightly broader and mushier than a bounded architecture discussion.

### Conversation transcript

**5. Actual conversation transcript**

Useful later, not first.

Why not first:

- strong mirroring risk,
- turn structure encourages role-following and summary behavior,
- more relational pressure mixed into the packet,
- harder to tell “live issue” from “thing both participants just said.”

### Worst source to start from

**6. Raw vault journal entries**

Worst starting source.

Why:

- concern density is too diffuse,
- epistemic objects are weakly marked,
- relational and emotional material dominates,
- validation is much harder,
- overreach risk is highest.

Use vault material later, after the issue path is proven on sharper sources.

## 15. Concern density + risk profile by source type

| Source type | Expected concern density | Main strength | Main risk |
|---|---:|---|---|
| Bounded code review / architecture packet | High | explicit design tension, explicit decisions | jargon mirroring |
| Project notes / research sifts | Medium-high | recurrence across notes, stable provenance | summary drift |
| Research reading + annotations | Medium | good claim/evidence structure | lower immediate project pressure |
| Conversation transcript | Medium-high | live unresolvedness | mirroring + role entanglement |
| Vault journal entry | Low-medium epistemic / high affective | live human material | diffuseness + relational overreach |

## 16. Conversation vs vault journal: which failure mode should v1 prefer?

If those are the only two options, pick the **conversation-side mirroring risk** over the **vault-side diffusion risk**.

Why:

- mirroring is easier to detect and reject,
- source spans make it auditable,
- jargon leakage can be mechanically filtered,
- diffuse journal material is harder to validate and much easier to overinterpret.

But my actual answer is: do **not** start from either if you can avoid it.
Start from a bounded architecture / code-review packet first.

## 17. Smallest end-to-end experiment I would actually run

### Exact source material type

One **bounded architecture-decision packet** from this repo.

For the first run, I would use three short labeled excerpts:

1. `right-now.md` — only the “Next After This” and “Decision Point” sections
2. `steering-balance-2026-03-24.md` — only the “Now / Later / near-pivot trigger” parts
3. one March 22 review excerpt — either the evaluator-authority warning or the “do not let model output become durable memory + opened rule access” section

That packet has:

- one live decision,
- one strategic argument for moving now,
- one boundary argument for restraint.

Good. That is enough.

### Approximate input size / context window shape

Target:

- **1,500–2,500 words total**
- **3 documents max**
- each excerpt labeled with doc id and paragraph ids

Not whole notes.
Not whole chats.
Not whole repo context.

### Exact typed output packet

```clojure
{:schema/version 1
 :packet/id ...
 :source/packet-id ...
 :proposals
 [{:proposal/id ...
   :proposal/type :issue
   :issue/kind :decision|:contradiction|:question|:hypothesis|:commitment|:aesthetic-bet
   :issue/title ...
   :source/phrasing [...]
   :why/unresolved ...
   :why/now ...
   :source/spans [{:doc ... :span-id ... :quote ... :span/role :support|:counterpressure} ...]
   :anchor/phrases {:topics [...]
                    :tensions [[... ...] ...]
                    :stakes [...]
                    :actors [...]
                    :tasks [...]}
   :timing/hint :now|:soon|:later
   :opposing/span-ids [...]}]
 :abstain/reason nil}
```

### Kernel-side observability / verifier packet

You need append-only records for each transition.

At minimum:

```clojure
{:proposal-log
 [{:proposal-id ...
   :source-packet-id ...
   :issue-kind ...
   :source-span-ids [...]
   :validator/result :accepted|:rejected|:trace-only
   :validator/reasons [...]
   :normalized-label ...
   :bridge-indices {...}}]

 :issue-lifecycle-log
 [{:issue-id ...
   :event :admitted|:activated|:retrieved|:used|:merged|:promoted|:demoted|:resolved|:dismissed
   :cycle-or-session ...
   :related-object-id ...
   :evidence [...]}]

 :uptake-log
 [{:issue-id ...
   :signal :freeze|:dismiss|:respond|:cut
   :session-id ...
   :context ...}]

 :counters
 {:proposal-count ...
  :abstain-count ...
  :ungrounded-reject-count ...
  :duplicate-merge-count ...
  :settled-reject-count ...
  :trace-only-count ...
  :provisional-admit-count ...
  :active-count ...
  :issue-retrieval-count ...
  :issue->episode-bridge-count ...
  :issue->issue-bridge-count ...
  :promotion-count ...
  :demotion-count ...
  :same-surface-loop-count ...}}
```

If you cannot read the seam from those logs directly, you are not ready to interpret the experiment.

### What kernel-side validation should do

Exactly this:

1. validate packet shape,
2. validate quoted spans,
3. validate unresolvedness,
4. dedupe / merge against open issues,
5. normalize anchor phrases into canonical bridge indices,
6. admit as `:trace` or `:provisional`,
7. compute activation score,
8. maybe activate exactly one issue.

### What retrieval should be allowed to do

Allowed:

- issue emits bridge indices,
- issue retrieves issues and episodes via shared bridge layer,
- issue retrieval is logged,
- issue retrieval can influence ranking / salience.

Not allowed:

- issue proposal edits retrieval thresholds,
- issue proposal edits cue zones,
- issue proposal edits rule access,
- issue proposal force-promotes itself.

### What persistence should and should not do

Should:

- keep rejected proposals as traces for audit,
- keep admitted issues in agent-side state only,
- track corroboration and uptake,
- allow promotion/demotion on later evidence.

Should not:

- write into human notes,
- create durable authority from one proposal packet,
- collapse issue objects into the episode store,
- mutate canonical project state automatically.

## 18. What success looks like

### After 1 session

Success:

1. the model emits `0–3` issues, not a dump,
2. at least one proposal is admitted as `:provisional`,
3. at least one proposal is rejected for a good reason,
4. the admitted issue can be retrieved later by bridge indices,
5. a human can say “yes, that is a real unresolved object in this packet” without also saying “you just paraphrased my own jargon back to me.”

### After 3–5 sessions

Success:

1. at least one issue survives across distinct source packets,
2. at least one issue bridges across object types,
3. at least one issue gets positive uptake,
4. at least one issue is explicitly demoted or resolved,
5. duplicate inflation stays low,
6. the issue layer affects what resurfaces later.

That last one matters most.
If issues never alter later resurfacing, they are decorative.

## 19. What failure means the seam is wrong, not just under-tuned

The seam is wrong, not merely under-tuned, if any of these happen:

1. proposals are almost always generic summaries,
2. titles only exist in repo jargon and cannot be recovered from the source,
3. most admitted issues are duplicates of already-open issues,
4. issue retrieval almost never affects later resurfacing,
5. the only useful source packets are ones already written in your architecture language,
6. the validator cannot reliably distinguish live issue vs plausible-sounding invention,
7. you discover that the only way to make the seam useful is to let the model set policy or durable authority.

If that happens, do not paper over it with more prompt work.
It means typed issue initiation was the wrong first seam or the source type was too loose.

## 20. Bottom line

The smallest honest move is:

1. **pick typed issue initiation as the first seam**,
2. **start with bounded architecture/code-review packets**,
3. **emit proposal packets, not live concerns**,
4. **let the kernel validate and activate**,
5. **use a shared bridge-index layer across issues and episodes**,
6. **transfer the membrane structurally, but change the evidence semantics to cross-surface corroboration + uptake + cross-object reuse**,
7. **keep proposal rate hard-capped and abstention legal**.

One sentence version:

**Make the model good at naming a few source-grounded unresolved objects; do not let it decide which of them deserve authority.**
