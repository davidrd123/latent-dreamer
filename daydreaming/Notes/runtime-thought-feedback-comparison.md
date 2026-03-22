# Runtime Thought Feedback Comparison

Date: 2026-03-21

## Question

When `RuntimeThoughtBeatV1` is written back into episodic memory during the live
`Puppet Knows` autonomous run, does the trace diverge structurally from the
no-feedback baseline, and how do `Haiku`, `Sonnet`, and a simple hybrid route
compare?

## Comparison set

- Baseline:
  [puppet_knows_autonomous.json](/Users/daviddickinson/Projects/Lora/latent-dreamer/out/puppet_knows_autonomous.json)
- Haiku feedback:
  [puppet_knows_autonomous_feedback_haiku12.json](/Users/daviddickinson/Projects/Lora/latent-dreamer/out/puppet_knows_autonomous_feedback_haiku12.json)
- Sonnet feedback:
  [puppet_knows_autonomous_feedback_sonnet12.json](/Users/daviddickinson/Projects/Lora/latent-dreamer/out/puppet_knows_autonomous_feedback_sonnet12.json)
- Hybrid feedback:
  [puppet_knows_autonomous_feedback_hybrid12.json](/Users/daviddickinson/Projects/Lora/latent-dreamer/out/puppet_knows_autonomous_feedback_hybrid12.json)

## Watchable artifacts

- Haiku:
  [puppet_knows_autonomous_feedback_haiku12_cognitive.html](/Users/daviddickinson/Projects/Lora/latent-dreamer/out/puppet_knows_autonomous_feedback_haiku12_cognitive.html)
- Sonnet:
  [puppet_knows_autonomous_feedback_sonnet12_cognitive.html](/Users/daviddickinson/Projects/Lora/latent-dreamer/out/puppet_knows_autonomous_feedback_sonnet12_cognitive.html)
- Hybrid:
  [puppet_knows_autonomous_feedback_hybrid12_cognitive.html](/Users/daviddickinson/Projects/Lora/latent-dreamer/out/puppet_knows_autonomous_feedback_hybrid12_cognitive.html)

## Result

All three feedback modes diverge from the no-feedback baseline on the same broad
window:

- changed cycles vs baseline: `4-12`

That is the main sprint result. Runtime thought writeback is not just continuity
text in a prompt. It changes retrieval and later node selection inside the live
kernel loop.

## Model-by-model summary

### Haiku

- model usage:
  - `claude-haiku-4-5-20251001 x12`
- changed cycles vs baseline:
  - `4, 5, 6, 7, 8, 9, 10, 11, 12`
- useful property:
  - cheapest live path that still changes the trace materially

### Sonnet

- model usage:
  - `claude-sonnet-4-6 x12`
- changed cycles vs baseline:
  - `4, 5, 6, 7, 8, 9, 10, 11, 12`
- measured wall time:
  - about `80.08s` for the full `12`-cycle run
- useful property:
  - highest-quality runtime thought beats and strongest showcase artifact

### Hybrid

- routing policy:
  - base `Haiku`
  - escalate to `Sonnet` on `reversal`
- model usage:
  - `claude-haiku-4-5-20251001 x6`
  - `claude-sonnet-4-6 x6`
- route reasons:
  - `goal_family:reversal x6`
  - `branch_event x6`
- changed cycles vs baseline:
  - `4, 5, 6, 7, 8, 9, 10, 11, 12`
- measured wall time:
  - about `57.84s` for the full `12`-cycle run

## Structural differences between Haiku and Sonnet

Even though both diverge from baseline on the same window, they are not
producing the same downstream trace.

Different late behavior shows up at least on:

- cycle `8`
- cycle `10`
- cycle `11`
- cycle `12`

Examples:

- cycle `8`:
  - Haiku chooses `n15_subway_self_scrutiny`
  - Sonnet chooses `n02_corridor_repeat`
- cycle `10`:
  - Haiku chooses `n05_peel_the_wall`
  - Sonnet chooses `n06_overhead_set_edge`
- cycle `12`:
  - Haiku chooses `n01_notice_seams`
  - Sonnet chooses `n06_overhead_set_edge`

So the model is not only changing prose quality. It is changing the writeback
residue enough to alter later retrieval and selection.

## Recommendation

Use:

- `Haiku` for default runtime feedback experiments
- `Sonnet` for showcase playback and quality-first runs
- `Hybrid` for the current best tradeoff

Why `Hybrid` is the current best tradeoff:

- it preserves the same structural result as the all-Sonnet path
- it limits expensive calls to the highest-value goal family
- it keeps the runtime cost materially below the full Sonnet pass

## Claim boundary

What is proven:

- runtime thought writeback can change the live kernel trace
- this is true with `Haiku`, `Sonnet`, and a simple hybrid route
- different models produce different downstream retrieval/selection behavior

What is not yet proven:

- the best routing policy for a truly continuous live instrument
- whether the same routing should be used beyond `Puppet Knows`
- whether feedback should be written every cycle or selectively
