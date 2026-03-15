# 5 Pro Question Bank — Round 2

## Q7. Scene-state progression across multi-step sequences

### The problem

The prototype generates multi-step sequences, but later steps largely rebuild from fixture-authored situation state. `run_middle_sequence()` does thread `current_active_situation_id` forward, and accepted generated episodes do re-enter retrieval, so **memory carry-forward is real**. But `build_causal_slice()`, `build_middle_prompt()`, and `retrieve_episodes()` still resolve the active situation through `resolve_situation()` against the small authored fixture world. That means **situation-state carry-forward is mostly not real yet**.

So there are two different things happening now:

- **Real carry-forward:** accepted prior prose re-enters memory via `build_generated_episode()`, later retrieval sees it, and later prompts feel shaped by earlier beats.
- **Mostly fake carry-forward:** unless the model changes `graph_projection["situation_id"]` to another authored fixture situation, step N is still generating from the same authored situation description and sparse `current_state` booleans as step 1.

That is why the current multi-step sequences read as iterative fixation and rewrite rather than genuine scene-state progression.

---

## Answer

### 1. MVP recommendation

Pick **option 3: effect accumulation**, but tighten it into a **small typed, lane-local derived situation state**. Do **not** do template append. Do **not** do a free-form LLM rewrite of the situation object after every accepted step.

Blunt comparison of the three MVP options:

| Option | Verdict | Why |
|---|---|---|
| **1. Template-based update** | No | Cheapest, but too shallow. It produces rhetorical progression, not causal progression. Appending “the choice is sharper now” does not change what `build_causal_slice()` or `derive_appraisal_frame()` can actually see. |
| **2. LLM-mediated situation update** | No | Richest surface output, worst state discipline. It will happily hallucinate irreversible changes, blur canon with inner shift, and create prose that sounds plausible but is hard to diff, validate, or feed back into the middle layer. |
| **3. Effect accumulation** | Yes | Smallest honest change. It can alter next-step causal/appraisal inputs without pretending canon changed and without handing state authorship to unconstrained prose. |

The one change to make now is this:

> After each accepted step, build a **runtime-only `DerivedSituationState`** from the accepted node’s structured residue and a tiny hand-authored delta vocabulary. The next step should read **(a)** the base authored situation, **(b)** the accumulated derived state, **(c)** the accepted previous step as a short recap, and **(d)** the carried-forward `current_active_situation_id`.

Use existing accepted residue as the input signal, not a new generative rewrite:

- `graph_projection.situation_id`
- `graph_projection.option_effect`
- `graph_projection.pressure_tags`
- `graph_projection.practice_tags`
- `graph_projection.origin_pressure_refs`
- selected operator family / affordances from the sidecar
- one short “what just happened” recap from the accepted scene text, kept as commentary rather than source of truth

The key rule is simple:

> **Do not let prose author state. Let accepted structured residue author state.**

And keep that state **lane-local**. `21-graph-interface-contract.md` is clear: live L2 runtime state should stay out of the shared graph seam. So this `DerivedSituationState` belongs in sequence runtime / trace state, not in admitted graph node fields.

---

### 2. Why it works

This works because it fixes the actual missing link, not the symptom.

Right now the harness has a real memory loop and a weak world-state loop:

- `build_generated_episode()` writes accepted scenes back into memory with retrieval tags, so later steps really do remember earlier steps.
- `run_middle_sequence()` threads `current_active_situation_id` forward, but that is only a **pointer carry-forward**, not a derived situation update.
- `build_causal_slice()`, `build_middle_prompt()`, and `retrieve_episodes()` still read the authored fixture situation almost unchanged.

Effect accumulation is the smallest thing that changes the actual next-step inputs.

#### Why option 3 beats the other two

**Template append** does not change the causal substrate. It gives you a nicer paragraph and the same step logic. That is cosmetic.

**LLM-mediated rewrite** gives you new text, but not stable state. It is likely to confuse three different things that must stay separate in this architecture:

1. **canon** — what is true in the world,
2. **derived sequence state** — what has become sharper or more stalled in this local chain of thought,
3. **counterfactual / inner action** — what the prose imagines but has not committed.

The prototype is not ready to let a free-text rewrite mediate those distinctions.

**Typed effect accumulation** stays honest about the actual fixture scale. The fixtures are tiny. Each one has:

- one concrete active authored situation,
- one threshold/payoff situation,
- a few sparse booleans in `current_state`.

That means you do **not** need general situation synthesis. You need **4–6 fields that can actually move**.

#### Why it creates real progression

Because the next step can now build a different `CausalSliceV1` from the same authored scene.

Examples:

- **Kai** should stop being “there is an unopened letter on the table” three times in a row. After one accepted avoidance beat, the next step should know something like: delay is now active, harbor salience has surfaced, ritual momentum is entrenched. That should change `self_options`, `other_options`, and eventually the likely next situation.
- **Maren / Rhea** should stop being “outside the door rehearsing” in static form. Repeated rehearsal should move the threshold state: opening line drafted vs overrevised, hand on handle vs still outside, exposure pressure sharper vs intolerable.
- **Tessa** should stop being a generic aftermath tableau. Rationalization should change the local aftermath state: half-apology forming, motive-defense strengthening, donor-hall afterimage still ringing, reply posture drifting toward send / delay / self-exoneration.

This also preserves the v1 constraint from `28-l2-schema-from-5pro.md`: only **ontic** change resolves or mutates canon. A salience or policy move can still alter the next step’s **derived situation state** without pretending the world itself changed.

That is the right separation.

---

### 3. Failure modes

There are several obvious ways to get this wrong.

#### 1. Fake progression through generic wording

If the delta layer is just:

- `clarify -> choice sharper`
- `open -> new possibility`
- `close -> less optional`

then you did not build progression. You built a slogan generator.

#### 2. Letting accepted prose directly mutate state

This is the main danger in option 2. If the system reads “she steadies her hand on the handle” and turns that into “door opened” or “meeting started,” it will invent irreversible changes that never occurred. That will destroy the canon / counterfactual distinction.

#### 3. Over-trusting `setup_refs[]` / `payoff_refs[]`

Those fields are still partly prompt-inferred. They are useful hints, but they are not yet a trustworthy causal state machine. If you let them drive most state mutation now, you are letting the noisiest part of the seam decide progression.

#### 4. Building a vocabulary that is too large

If you jump from almost-no-state to a full symbolic scene calculus, you will bog down immediately. The fixtures do not justify that yet.

#### 5. State changes that do not feed the middle layer

If `DerivedSituationState` is only written to the trace but does not affect:

- `build_causal_slice()`,
- `derive_appraisal_frame()`,
- `derive_practice_context()`, or
- `build_middle_prompt()`,

then progression is still fake. The state has to change what the next step can do.

#### 6. Leaking live state into the shared graph

Do not write this derived state into the admitted graph seam. The graph contract is intentionally narrow. This progression layer belongs to lane-local runtime and traces until it proves it is producing stable residue worth exporting.

---

### 4. Higher-octane design

The higher-octane design is **not** “ask the LLM to rewrite the world better.” It is a **tiny proposition/state vocabulary** plus **schema-driven effects**.

Call it `DerivedSituationState`, not a world model. It sits between the authored fixture situation and the next-step `CausalSliceV1`.

#### Shared core across current fixtures

Use a small shared core first:

| Field | Example values | Why it matters |
|---|---|---|
| `interaction_phase` | `precontact`, `threshold`, `aftermath` | Distinguishes Kai/Maren/Rhea threshold cases from Tessa’s actual aftermath case. |
| `response_state` | `untouched`, `delaying`, `drafting`, `poised`, `sent/spoken` | Tracks whether the character is still avoiding first contact, preparing it, or crossing into action. |
| `target_immediacy` | `distant`, `text-open`, `behind-threshold`, `physically-imminent` | Changes appraisal and available options. |
| `pressure_focus` | `latent`, `surfaced`, `sharp`, `dominant` | Makes “clarify” and “salience” do real work without mutating canon. |
| `self-regulation-mode` | `avoidance`, `rehearsal`, `rationalization`, `confession` | Optional as a derived runtime field; useful if practice state later becomes dynamic. |

That shared core is enough to move the middle layer without introducing a giant ontology.

#### Fixture-specific overlay

Then add **2–4 fixture-specific propositions** where the world actually differs.

| Fixture | Minimal proposition/state vocabulary |
|---|---|
| **Kai** | `letter_status ∈ {unopened, handled, face_down, opened}`; `harbor_salience ∈ {latent, surfaced, intrusive}`; `ritual_momentum ∈ {none, active, entrenched}`; `departure_posture ∈ {not_leaving, thresholded, leaving}` |
| **Maren** | `opening_line_state ∈ {none, drafting, overrevised, settled}`; `entry_status ∈ {outside, at_handle, entered}`; `revision_momentum ∈ {low, active, exhausted}`; `room_pressure ∈ {live, sharp, critical}` |
| **Rhea** | `opening_line_state ∈ {none, drafting, self_defensive, admitting}`; `door_relation ∈ {outside, at_handle, inside}`; `panel_sting_salience ∈ {latent, surfaced, dominant}`; `meeting_status ∈ {not_started, thresholded, underway}` |
| **Tessa** | `reply_state ∈ {unsent, half_drafted, reframed, sent}`; `blame_register ∈ {raw, defended, self_exonerating, acknowledging}`; `donor_hall_afterimage ∈ {latent, ringing, intrusive}`; `exit_posture ∈ {stalled, leaving, committed}` |

That is enough. Do not go broader yet.

#### How it should interact with `CausalSliceV1`

This is the point of the exercise: the state vocabulary must actually change the next `CausalSlice`.

Examples:

- If **Kai** has `ritual_momentum=entrenched` and `harbor_salience=intrusive`, then `self_options` should no longer be just `open-letter` vs `delay-contact`. The slice should start exposing a harder branch such as departure threshold or hardened delay.
- If **Maren / Rhea** have `opening_line_state=overrevised` and `entry_status=at_handle`, controllability should not stay stationary. Practice context may drift from clean rehearsal toward strained evasion or exhausted threshold entry.
- If **Tessa** has `blame_register=self_exonerating` and `reply_state=half_drafted`, then rationalization should be represented as aftermath management, not a fresh generic guilt scene.

So the relationship is:

> **Base fixture situation + derived propositions -> next `CausalSliceV1` -> next `AppraisalFrame` -> possibly shifted `PracticeContextV1`.**

That is how progression becomes structural instead of stylistic.

#### Relation to the POCL-lite sketcher from Q6

This is where Q7 and Q6 meet cleanly.

The MVP should use **hand-authored effect rules** tied to current operator / affordance / option-effect combinations.

Later, if Q6’s POCL-lite operator schema library exists, those schemas should become the authoritative source of situation deltas.

Example:

- schema `delay-contact`
  - preconditions: `response_state=untouched`
  - effects: `response_state=delaying`, `ritual_momentum+=1`, `pressure_focus=sharp`
- schema `draft-opening-line`
  - preconditions: `interaction_phase=threshold`
  - effects: `opening_line_state=drafting|settled`, `room_pressure+=1`
- schema `justify-simplification`
  - preconditions: `interaction_phase=aftermath`
  - effects: `blame_register=defended`, `reply_state=half_drafted`

That means the higher-octane design is **additive to the MVP**, not a replacement from day one:

- **Now:** simple typed deltas from accepted residue.
- **Later:** schema preconditions/effects produce the deltas more mechanically.

That is the right sequencing.

---

### 5. Connection to other questions

#### Q6: graph seam richness / POCL-lite

Q7 should **not** wait for a full POCL-lite sketcher. But Q6 is the natural future home for authoritative state effects. Right now, `setup_refs[]` / `payoff_refs[]` are too opportunistic to drive the whole update loop. Later, a schema library can make them causally meaningful.

#### Q5 and Q12: operator and practice stationarity

Q7 is one of the honest ways to break stationarity. Right now, operator selection stays sticky partly because the situation substrate barely changes. Without state progression, operator fatigue and practice decay are partly artificial pressure valves. They may still be useful, but they are compensating for missing state change.

#### Q8: generated-episode self-priming

Do not confuse these problems. Retrieval writeback is why memory carry-forward is real. It is also why the system can echo itself. Fixing Q7 does **not** solve Q8. It just means later steps have a changed local scene state instead of the same authored tableau plus self-memory.

#### Q11: prompt architecture / given circumstances

Once Q7 exists, Q11 gets much easier. The prompt can stop dumping static situation JSON and instead express:

- what just happened,
- what is now sharper,
- what is still unsent / unspoken / unentered,
- what options are now live.

That is closer to “given circumstances” and less like asking the model to parse the same fixture over and over.

#### Q9: intervention ordering

If the next milestone is a genuinely informative Tessa or Kai batch, Q7 matters because it changes the dramatic function of later steps, not just their wording. But it still should be kept small. The right move is a **tiny derived-state layer**, not a big planner.

---

### 6. What NOT to build yet

Do **not** build any of the following now.

1. **Do not build a full situation calculus or general STRIPS planner.** The fixtures are too small, and the state vocabulary is not stable enough yet.
2. **Do not let the LLM rewrite the whole situation object every step.** That is prose drift disguised as state management.
3. **Do not write live derived state into the shared graph seam.** Keep it lane-local until it proves it deserves export.
4. **Do not treat `current_active_situation_id` carry-forward as if the problem were solved.** That is pointer carry-forward, not state progression.
5. **Do not let `setup_refs[]` / `payoff_refs[]` become authoritative state transitions yet.** They are still too prompt-shaped.
6. **Do not create dozens of propositions per fixture.** Start with a tiny shared core plus a 2–4 field overlay per fixture.
7. **Do not blur ontic mutation with salience/policy progression.** Only events mutate canon in v1. The rest should mutate local sequence state and appraisal conditions.

The honest next move is small:

> Build a lane-local derived situation state that makes step N+1 see a changed situation because something actually changed in sequence state, not because the model paraphrased step N.

