# Cold-Start Bootstrapping and Evidence-Gated Admission

You have the full repo context. Do NOT summarize what the documents
say. Bring outside knowledge — name specific systems, papers, results,
and mechanisms from the cognitive architecture and AI memory
literatures that bear on these questions.

## What just happened

We discovered a bootstrapping deadlock in the kernel's memory ecology.
The chain:

1. Family-plan episodes enter as `:provisional` (not yet trusted for
   reuse in planning)
2. To promote to `:durable`, an episode needs 2 cross-family use
   successes (i.e., a rationalization episode reused by reversal, or
   vice versa)
3. But the stored-source retrieval paths for rationalization and
   reversal required `(= :durable (:admission-status episode))` as a
   candidate filter
4. So no provisional episode could ever be retrieved → no cross-family
   use → no promotion evidence → nothing ever promotes

The fix: allow provisional candidates into the stored-source race with
a ranking penalty below authored sources. This preserves authored-first
default while letting the promotion chain bootstrap. A unit test now
proves the full chain works: provisional episode → 2 cross-family
successes → promotes to `:durable` → opens a frontier rule.

The next step is a 2-situation benchmark fixture with deliberately
incomplete authored coverage (each situation has authored sources for
one family but not the other) so the system is structurally forced to
try dynamic stored episodes from the other situation.

## Question 1: Cold-start in other cognitive architectures

How do ACT-R, Soar, CLARION, ICARUS, and other cognitive
architectures handle the cold-start problem when system-generated
structures need to enter the planning/retrieval pipeline?

Specific sub-questions:

- In ACT-R, new chunks enter declarative memory with base-level
  activation. There's no admission gate — everything is immediately
  retrievable, just with lower activation. How does ACT-R prevent
  spurious chunks from dominating retrieval in long-running systems?
  Is the decay function sufficient, or do people add additional
  filtering?

- In Soar, newly learned chunks (from chunking) are immediately
  available to the recognition memory. Is there any mechanism that
  delays or gates when a new chunk can participate in operator
  proposal? Or is the chunk immediately full-strength?

- In CLARION, the explicit and implicit systems interact through
  top-down and bottom-up learning. When a new explicit rule is
  extracted from implicit learning, does it enter the explicit
  system at full strength or with a probationary period?

- Are there systems that use something like our evidence-gated
  admission — where a generated structure must demonstrate its
  utility (cross-module reuse, downstream success) before becoming
  fully available to planning?

- What happens in systems that DON'T gate and run for many cycles?
  Do they develop the "groove formation" problem we're trying to
  prevent (self-reinforcing loops of retrieved-then-stored material)?

## Question 2: Our staged admission as a design

Our admission ladder is:

```
:trace       — raw cognitive trace, not retrievable for planning
:provisional — retrievable with ranking penalty, can accumulate
               use evidence, not yet trusted for full planning reuse
:durable     — fully retrievable, proven by cross-family use evidence,
               can open frontier rules
```

Plus anti-residue flags: `:stale`, `:same-family-loop`, `:backfired`,
`:contradicted` — which can demote durable back to provisional or
suppress retrieval.

Plus promotion evidence: only `:cross-family-use-success` counts
(threshold: 2). Evaluator (LLM) is advisory/gating, not sole
authority. Roving episodes are never promotion-eligible (attentional
surface, not reusable payload). Only rationalization (with
`:reframe-facts`) and reversal (with `:input-facts`) are
promotion-eligible.

Questions:

- Is the cross-family-use-success criterion the right proxy for
  "this generated structure proved useful"? Are there better or
  complementary evidence types we should track?

- Is threshold 2 reasonable? Too low (promotes too easily)? Too high
  (promotion never happens in short runs)?

- The asymmetry between roving (never promotes) and
  rationalization/reversal (can promote) — does this match how
  cognitive architectures typically treat exploratory vs. strategic
  content? Or should roving episodes have a different promotion path?

- Our `:same-family-loop` flag is an anti-groove mechanism. Are there
  established anti-groove or anti-perseveration mechanisms in the
  cognitive architecture literature? What works?

- The demotion path (`:durable` → `:provisional` on hard failure) is
  unusual — most cognitive architectures don't demote learned
  structures. Is there precedent? Is it architecturally dangerous
  (instability, oscillation)?

## Question 3: Benchmark design for memory ecology

We're building a 2-situation fixture to test whether the membrane
state machine exercises in a live soak. The key design choice:
deliberately incomplete authored coverage, so the system must use
self-generated material from one situation in the other.

Questions:

- Are there established evaluation patterns in cognitive architecture
  research for testing whether a system can productively reuse its
  own generated structures? What benchmarks or metrics do ACT-R /
  Soar / CLARION researchers use?

- Our success criteria are staged:
  1. Dynamic candidates appear in the retrieval race
  2. Use-history is recorded on a stored episode
  3. A promotion/demotion/flag transition fires
  4. Promoted episode opens a frontier rule that gets used

  Are there other intermediate observables we should track? What would
  a cognitive architecture researcher want to see in this trace?

- The "asymmetric authored coverage" design is our invention. Is there
  a name for this pattern in evaluation methodology? Has anyone done
  something similar — deliberately leaving gaps in the initial
  knowledge base to test whether the system fills them from
  experience?

- How many cycles is reasonable to expect visible membrane dynamics?
  We're targeting 20. Is that realistic for a 2-situation world, or
  should we expect 50-100?

## Question 4: Situation models for richer psychological scenarios

Our current situation model is a flat pressure map:

```
{:activation 0.55, :ripeness 0.6, :anger 0.24, :hope 0.19,
 :threat 0.47, :indices [:perception :awareness :seam ...]}
```

Goal family selection is driven by weighted sums of these numbers.
There's no internal narrative structure, no character beliefs or
relationships, no causal chains, no events that evolve from branch
outcomes. The kernel HAS fact/context machinery (assert-fact, context
tree, sprouting) but the benchmark situations don't use it — they're
conductor-fed pressure gauges.

Mueller's original situations were richer: reality contexts full of
asserted interpersonal facts (Mary likes John, John was fired, etc.)
with causal dependencies. The mechanisms reasoned about those facts
directly.

Questions:

- How should the situation model evolve to support psychologically
  richer scenarios? Specifically: what should a "situation" carry
  beyond pressure numbers? Character states, relationship graphs,
  event histories, belief models?

- In Mueller's DAYDREAMER, the interpersonal domain was narrow but
  structured (fired from job, relationship breakup, lottery win).
  What contemporary frameworks exist for representing the KIND of
  situation content that would make daydreaming psychologically
  interesting? Appraisal theory (Scherer, Lazarus)? Narrative
  structure models? Dramaturgical approaches?

- How do MINSTREL (Turner), Versu (Evans/Short), and Facade (Mateas/
  Stern) represent their situation spaces? Those systems aimed for
  psychologically rich scenarios. What can we learn from their
  situation models even though they had different architectures?

- There's a tension between "situation as pressure gauge" (simple,
  fast, composable) and "situation as fact-space" (rich, slow,
  expressive). Is there a middle ground? For example: a small set
  of typed facts per situation that the family planning reads, with
  the pressure numbers derived from those facts rather than set
  directly?

- For Graffito (our first creative brief — a Mark Friedberg short
  film script), what situation structure would support psychologically
  interesting daydreaming? The script has deep material:

  - Tony, a 7-year-old who is neurodivergent ("everything comes out
    crooked," "the only way I can stay calm is drawing"), discovering
    he is the reincarnation of Scribe, a mythic graffiti artist
  - Monk, his absent father, a graffiti artist who can't stop
    creating even when it costs him (arrest, instability), who
    transmits creative knowledge through the body ("give your mind to
    your body and let it flow")
  - Grandma/Motherload — the same person in real/fantasy worlds,
    who understands the creative lineage but fears its cost, who
    challenges ego ("seeing YOUR name on their wall makes YOU feel
    better?")
  - The Magic Can — intergenerational creative inheritance
    (Grandma -> Monk -> Tony), only works through embodied flow
  - Themes: "what is wrong with you is exactly what's right with
    you," inherited artistic calling vs. its cost (police, absence),
    ego vs. purpose, father-son transmission through the body

  What should the kernel's situation model carry to support daydreaming
  ABOUT this material — not just selecting family plans from pressure
  numbers, but actually reasoning about Tony's relationship with Monk,
  Grandma's dual fear/pride, the tension between art-as-calling and
  art-as-danger? What facts, relationships, or appraisals would a
  Mueller-style cognitive system need to daydream interestingly about
  these themes?
