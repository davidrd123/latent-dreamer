# Latent Dreamer

A conducted daydreaming instrument. A human performer steers a
character's inner life while a cognitive engine maintains persistent
concerns, episodic memory, and involuntary daydreaming between
interactions. The stage makes that visible through narration, reactive
music, and eventually real-time video.

The architecture is Erik Mueller's DAYDREAMER (1990), reconstructed
in Clojure with LLM judgment at typed call sites inside the
structural loops Mueller's system owns.

## What exists

**Kernel** ([`kernel/`](kernel/)) — a Clojure implementation of Mueller's
cognitive architecture:

- Emotion-driven control loop with five daydreaming families
  (rationalization, reversal, roving, rehearsal, repercussions)
- Rule engine with typed rules, connection graph, and structural
  bridge discovery
- Episodic memory with coincidence-mark retrieval and reminding
- Memory membrane: admission tiers (trace/provisional/durable),
  anti-residue flags, evidence-driven promotion, rule accessibility
  frontier
- Executor boundary: family plans return declarative effects, kernel
  validates and applies
- Runtime thought projection with LLM-generated inner-life prose
- Writeback loop: generated thought changes future retrieval and
  cognition
- Tony character state with sensory regulation (overloaded → bracing
  → entraining → flowing → creating), reappraisal after family
  execution

**Benchmarks** ([`kernel/src/daydreamer/benchmarks/`](kernel/src/daydreamer/benchmarks/)):

- Puppet Knows: runtime/seam regression on authored fixture
- Membrane Assays A and B: live dynamic reuse, same-family-loop flag,
  cross-family promotion, frontier rule opening
- Graffito microfixture, regulation slice, rehearsal slice: typed
  psychological facts drive family selection and retrieval
- Graffito miniworld: 3-situation autonomous run with persistent Tony
  state, reappraisal flips, cross-family episode reuse

**Condensation** ([`daydreaming/Notes/Book/`](daydreaming/Notes/Book/)) — Mueller's 19
mechanisms condensed into implementation-grade cards, verified against
the original Common Lisp source.

**Research** ([`daydreaming/Notes/DeepResearch/`](daydreaming/Notes/DeepResearch/)) — prompts and
replies from external architecture reviews covering cold-start
bootstrapping, appraisal theory, situation models, regulation
mechanics.

**Graffito** ([`daydreaming/vendor/graffito/`](daydreaming/vendor/graffito/)) — vendored source
material for the first creative brief (Mark Friedberg short film).
Kernel-facing brief at
[`daydreaming/Notes/experiential-design/24-graffito-kernel-brief.md`](daydreaming/Notes/experiential-design/24-graffito-kernel-brief.md).

## Orientation

Start from the control plane:

1. [`daydreaming/Notes/current-sprint.md`](daydreaming/Notes/current-sprint.md) — current objective, what's proven, what's next
2. [`daydreaming/Notes/dashboard.md`](daydreaming/Notes/dashboard.md) — broader project map, benchmark ladder, milestone status
3. [`daydreaming/Notes/canonical-map.md`](daydreaming/Notes/canonical-map.md) — where everything lives

For the architecture:

4. [`condensation/architectural-framing.md`](daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/architectural-framing.md) — the hybrid Clojure/LLM boundary design
5. [`condensation/build-order-checkpoint-2026-03-22.md`](daydreaming/Notes/Book/daydreaming-in-humans-and-machines/condensation/build-order-checkpoint-2026-03-22.md) — settled build sequence from five rounds of review

## Running the kernel

```bash
cd kernel

# Run all tests
bb check          # format + lint + test

# Or individually
bb test           # 250 tests, ~1350 assertions
bb format         # cljfmt
bb lint           # clj-kondo

# Start nREPL for interactive work
bb nrepl          # port 7888
```

Run a Graffito miniworld:

```clojure
(require '[daydreamer.benchmarks.graffito-miniworld :as mini])
(let [{:keys [cycle-summaries]} (mini/run-miniworld {:cycles 20})]
  (doseq [s cycle-summaries]
    (println (:cycle s) (:selected-situation-id s)
             (:selected-family s) (:reappraisal-flip? s))))
```

## What is proven

- Inner-life prose from persistent cognitive state (not just a prompt)
- Feedback loop: generated thought changes later cognition, traces
  diverge from cycle 4
- Memory membrane prevents self-reinforcing grooves
- Same-family loop suppression and cross-family promotion with
  frontier opening (Assays A and B)
- Same raw cues produce different behavior after regulation state
  changes (6 autonomous reappraisal flips in 20 cycles)
- First cross-family episode reuse on Graffito material

## What is not proven yet

- Cross-session accumulation (the hard falsification test)
- Full serendipity path verification
- First real LLM-backed rule evaluation
- A full staged run with narration, audio, and visuals together
- Rich authored worlds beyond microfixtures

## The falsification criterion

If a new rule or path discovered in session 1 does not change
reachable behavior in session 2 without re-pasting the trace, we do
not yet have anything beyond strong memory-augmented generation.

## Stack

- Kernel: JVM Clojure 1.12, deps.edn, Babashka tasks
- LLM: Gemini, Claude, or OpenAI via runtime_thought.clj
- Stage (separate repo): Scope real-time video, APC Mini MK2, Lyria
  RealTime
- Python tooling: uv-managed, pyproject.toml

## Previous work

The stage layer (real-time video, MIDI instrument, Lyria integration)
lives in `scope-drd`. The generation pipeline and L3 traversal from
earlier rounds are documented in `daydreaming/Notes/` but are no
longer the primary architecture.

Mueller's original Common Lisp source is in [`daydreamer/`](daydreamer/).
