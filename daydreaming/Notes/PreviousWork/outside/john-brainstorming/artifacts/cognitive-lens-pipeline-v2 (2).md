# Cognitive Lens Pipeline: Unified Working Document v3

**Status:** Pre-validation. No code exists yet. Test prompts drafted (separate artifact). Ready to run.
**Date:** 2026-02-19
**Sources:** John's instrument-calibration chat with Claude, Jim White's DSPy→SFT→GRPO sketch, PROTEUS output from ChatGPT (triaged), deep research on cognitive scaffolds for LLM structuring, representation geometry literature, cross-Claude review of v2.

---

## The Core Idea

Take messy inputs (personal notes, fragments, transcripts) and run them through multiple analytical lenses in parallel, then use *disagreement between lenses* as the primary signal for what's interesting, what's imposed, and what's residue (uncaptured by any lens).

## The Two Questions That Determine Everything

Before any architecture, two empirical questions must be answered. Everything else is conditional on them.

**Q1: Can you skip cogsci entirely?** If an atheoretic codebook with arbitrary slots ("central tension, thing that repeats, thing that's missing, emotional weight") produces comparable divergence to framework-based codebooks, then the cogsci frameworks are taxonomy convenience, not analytical tools. This is cheap to test and should run in parallel with the framework lenses from the start.

**Q2: Inter-model vs. inter-framework -- which produces more useful divergence?** Running one framework across three models, or three frameworks on one model? The FAccT data (inter-model α=0.13 vs. intra-model α=0.85-0.89) suggests inter-model is the stronger signal, but nobody has compared head-to-head for interpretive tasks. If inter-model wins, you want one good codebook and a model ensemble, not three frameworks on one model.

## Why This Matters

### The oracle attractor

LLMs post-trained via RLHF have a strong attractor toward "answer-shaped completion": confident tone, conversational closure, premature unification. This is what preference optimization rewards (Sharma et al. 2023, Huang et al. 2023).

A "thinking partner" is a different objective function, not a different tone. It requires maintaining multiple live hypotheses, tracking provenance, preserving residue, separating FOUND/INFERRED/IMPOSED, and rewarding disagreement between well-grounded structures over premature consensus.

### The interpretation gap

John's conversation with Claude surfaced a distinction that anchors the whole project: almost all commercial LLM structuring is *extraction* (entities, facts, predefined schema). What we're building is *interpretation* (what's the structure of this person's thinking, and how is it changing?). Extraction can be verified against ground truth. Interpretation cannot, at least not by the same means.

This is a genuinely underexplored use case. The deep research confirmed: **no papers were found on LLM qualitative coding of personal or creative texts specifically.** Enterprise tools fill predefined fields. Qualitative research uses LLMs on other people's data with institutional codebooks. Using an LLM to structure *your own evolving thinking* for *your own creative and learning purposes* is a gap.

### Why residue matters (from John)

"Structure implies resolution, and some of the most valuable unstructured thinking is valuable precisely because it's unresolved. The mess is doing work. When I clean it up, I may be cleaning up the very thing that was generative."

This is the design principle: the pipeline must protect generative ambiguity. The value isn't in the structure the model produces; it's in the human's *reaction* to the structure. Structured artifacts are prompts for human thinking, not replacements for it.

## What John Got Right

John's chat with Claude was an instrument-calibration conversation. He pushed Claude to assess its own mechanism as an analogy engine, demonstrate structuring capability while narrating what the demonstration conceals, and then situate that self-assessment against real-world practice. His key contributions:

**1. The multi-pass architecture with blind/constrained passes.** Each pass should be blind or constrained relative to the others, so that divergence becomes information. His specific design:
- Pass 1: Inventory (faithful catalog, no grouping)
- Pass 2: First structure (with confidence tags, from inventory not raw notes)
- Pass 3: Adversarial restructure (from raw notes, explicitly avoiding Pass 2's categories)
- Pass 4: Residue and tension (what appears in both? in neither? where do they disagree about what's central?)
- Pass 5: Human writes what they actually think

**2. The "always finds structure" caveat.** Claude told John: "I will *always* find structure, whether or not it's actually there. I have no way to distinguish 'this data genuinely clusters into three themes' from 'three is a common number of themes and I'm imposing it.' The architecture has no null result." This is correct and now empirically quantified: LLM summaries are **~5x more likely to contain broad generalizations** than human-authored equivalents (Peters & Chin-Yee 2025, n=4900 summaries, OR=4.85, 95% CI [3.06, 7.70], p<0.001 -- note the wide CI, and this was on *scientific* summaries; transfer to personal/creative text is plausible but unverified). Prompting to avoid inaccuracies makes it *worse*.

**3. The meta-commentary on example selection.** John got Claude to acknowledge its own rhetorical choices: escalating difficulty, favoring thematic over temporal/relational/emotional/network structuring, consistently positioning unstructured data as wanting to become structured. This reflexivity is the right instinct but it's still the same instrument reporting on itself.

**4. Extraction vs. interpretation distinction.** Grounds the whole project.

## Where John Stopped (and What We're Adding)

**1. His passes aren't analytically orthogonal.** Pass 3 ("organize differently, avoid these categories") is adversarial but not structurally different. It's asking the same analogy engine to do the same operation with a negative constraint. The research confirms this matters: at the *inductive* level, LLMs converge toward a default semantic-keyword mode regardless of instructions.

**2. Self-labeling is circular.** The FOUND/INFERRED/IMPOSED distinction relies on the same model to both measure and report measurement error. Claude's eloquent metacognition about its own failure modes is the same architecture doing pattern-completion, pointed at a different target. John accepted "I always find structure whether it's there or not" as calibration; it's actually performance.

**3. The "analogy engine" framing is imprecise.** Built on king-man+woman=queen (word2vec, 2013), not transformer mechanics. And: Qin et al. (ACL 2025) found that **self-generated random examples achieve comparable or better performance** to relevant analogical examples. The benefit comes from chain-of-thought scaffolding, not genuine analogical transfer. This complicates John's theoretical motivation but doesn't eliminate the practical value.

**4. No implementation path.** John stopped at prompt templates. We now have: DSPy for pipeline optimization, Jim White's SFT→GRPO for distillation, GEPA optimizer for text-feedback-based prompt improvement, RLM for long-context handling.

## The Key Research Finding: Deductive vs. Inductive Divergence

**This is the most important empirical result from the deep research.**

When LLMs do qualitative coding:
- At the **deductive/application level** (applying existing codebooks with specific slots), framework choice produces measurably different outputs. Procedural-level instructions ("code item-by-item using constant comparison") outperform generic instructions.
- At the **inductive/generation level** (generating themes from scratch), LLMs converge toward a default mode that is semantic rather than latent, broad rather than nuanced, keyword-based rather than idea-based. This default mode resembles codebook thematic analysis **regardless of whether the prompt specifies grounded theory, reflexive TA, or content analysis**.

Evidence sources:
- DeTAILS (Sharma et al. 2025): LLM-assisted reflexive TA **naturally aligned with codebook TA** due to stabilized codebooks needed at scale
- LATA (Wang et al., CSCW 2025): GPT-4 produced codes as keywords/concepts rather than the "ideas" reflexive TA demands; cosine similarity with human analysis: 0.76
- Vikan et al. (2025, *Qualitative Health Research*): "At this stage, base LLMs provide limited support and do not increase the efficiency of RTA"

**Design implication:** The lenses must be implemented as **structured codebooks** (explicit slots, specific operations, typed outputs), not as vibes ("think about forces"). Codebook-style → divergence. Vibes-style → convergence.

## The Lens Question: Are These Arbitrary?

### Computational maturity of the three frameworks

| Framework | Computational implementations | LLM prompt implementations |
|---|---|---|
| Structure-mapping (Gentner) | Most mature. SME in Common Lisp (1989), ANASIME in Python, SME-clj in Clojure. Full algorithm: local match → structural evaluation → greedy merge → candidate inferences → breakdown points. | **None as LLM prompt architecture.** A 2025 paper found LLMs don't exhibit systematic structure mapping without a two-stage data curriculum. |
| Force dynamics (Talmy) | Wolff's vector model (2007). VerbNet parser links verb classes to force-dynamic roles. | **Zero implementations.** Completely unexplored as LLM prompting. |
| Conceptual blending (Fauconnier/Turner) | Richest landscape. COINVENT uses ASP + category-theoretic colimits. Divago uses genetic algorithms. | **PopBlends (CHI 2023):** compared KB-driven vs. LLM-driven blending; found "very different characteristics" across methods at similar accuracy. Users found **2x as many blend suggestions** with **half the mental demand.** Best evidence that framework choice produces structural divergence. |

### What deep learning tells us about concept geometry

- **Platonic Representation Hypothesis** (Huh et al. 2024, ICML oral): Different neural nets converge on similar internal representations as they scale. Gärdenfors' conceptual spaces (concepts = convex regions, similarity = distance) is the closest classical framework to what's observed.
- **Linear Representation Hypothesis:** High-level concepts encoded as approximately linear directions in activation space. Not the same kind of structure Gentner/Talmy theorized about.
- **LLMs and analogical reasoning:** Advanced LLMs match human performance on semantic structure-mapping tasks but probably via a different mechanism. "How-possibly" not "how-actually" explanations.
- **Representation geometry (Li et al. 2025):** LLM representations go through three spectral phases during pretraining. Task-relevant information lives in the spectral tail, not dominant eigendirections.

### Bottom line

The classical cogsci lenses are one decomposition of meaning. They're not privileged. But: they decompose along plausibly orthogonal axes (analogical, causal, role-based), and when implemented as codebooks (not vibes), they demonstrably produce different outputs. The PopBlends result is the strongest evidence. The question is whether this holds for personal/creative text interpretation, which nobody has tested.

## Inter-Model Disagreement: The Right Quality Signal

Self-consistency (sampling 40 reasoning paths, majority vote) assumes a single correct answer. Unsuitable for interpretive tasks.

Inter-model disagreement is empirically stronger:
- ICLR 2026 submission: cross-model semantic disagreement captures **epistemic uncertainty** that self-consistency misses, flagging confident failures where single-model sampling shows spurious agreement
- FAccT 2025, moral dilemmas: intra-model self-consistency α = 0.85-0.89 (Claude, GPT-4), but inter-model agreement dramatically lower: GPT-3.5↔Claude at α = **0.13**. Direct evidence that models impose different systematic interpretive frames.
- Confidence-Diversity Calibration (arXiv:2508.02029): R² = 0.979 predicting inter-coder agreement on accessible tasks. Improvement of 6.6-113.7% over single-signal baselines. Caveat: may not generalize to complex interpretive tasks.

LLMs are universally overconfident: ECE 0.12-0.57. Verbalized confidence clusters 80-100% regardless of accuracy. Extended reasoning **worsens** calibration (KalshiBench). Human superforecasters: ECE ~0.03-0.05.

## Overgeneralization: Quantified

Peters & Chin-Yee (Royal Society Open Science, 2025): 10 LLMs, 4900 summaries. LLM summaries nearly **5x more likely** to contain broad generalizations (OR=4.85, 95% CI [3.06, 7.70], p<0.001). Three types: generic generalizations (dropping quantifiers), present-tense generalizations (past→present shift), action-guiding generalizations (description→recommendation). DeepSeek, GPT-4o, LLaMA 3.3: overgeneralized in 26-73% of cases. Claude showed lowest rates. Prompting to avoid inaccuracies made models **nearly 2x more likely** to overgeneralize.

## Implementation Substrate: DSPy 3.x + GEPA

### DSPy (v3.1.3, ~4.17M monthly downloads)

Core abstractions: Signatures (declarative I/O with Pydantic typing), Modules (composable LM components with `forward()`), Adapters (signature→prompt translators).

**No existing DSPy pipelines for qualitative coding exist.** This would be novel.

### GEPA optimizer (arXiv:2507.19457)

Uniquely accepts **text feedback** rather than just scalar scores. Reflects on execution traces to understand *why* scores were low and proposes improved prompts. +22 percentage points over vanilla structured outputs on extraction. For interpretive tasks: define LLM-as-judge, validate against human annotations, optimize pipeline against judge. Explosion.ai showed Spearman correlation ~0.56 with human judgment (vs. BERTScore 0.14).

### RLM for long context (separate workstream)

`dspy.RLM` module. RLM decomposes *context*, not reasoning (unlike Tree-of-Thought). RLM(GPT-5-mini) outperformed GPT-5 by 34+ points on OOLONG at 132K tokens. Relevant for scaling to large note collections and literature search, but **not needed for the validation test** -- that's 5 fragments, plain API calls suffice. See separate RLM session trace artifact for the exploration use case.

---

### Downstream (conditional on validation): SFT→GRPO

*This section exists to document the full pipeline vision. None of it should be built until the validation test produces a go signal.*

If the lens pipeline produces validated, high-quality outputs:
1. DSPy optimizes prompts + LLM-judge evaluation
2. Collect (input, optimized-output) pairs as SFT data
3. Fine-tune on SFT data
4. GRPO via PrimeIntellect's Verifiers (Jim White's recipe)
5. Budget: 8xH100 x 24h, <$500

Bottleneck: no working DSPy pipeline yet. Everything downstream waits.

---

## What to Keep from PROTEUS

- Output schema: FOUND/INFERRED/IMPOSED + evidence_ids (the anti-oracle mechanism)
- Multi-objective metric: evidence coverage, format correctness, residue presence, map diversity
- Pitfalls: lens tyranny, recursive drift, Goodharting the metric
- Discard: combined architecture nouns, RLM integration, cognitive framework summaries, Pattern A vs B

## Circularity Problem

Self-labeling (FOUND/INFERRED/IMPOSED) is circular: same instrument measures and reports its own error.

Mitigations, ordered by strength:
1. **Inter-model disagreement** (strongest signal, empirically validated, cheapest to scale)
2. **Counterfactual controls:** run pipeline on shuffled/randomized text; if same structure emerges, it's apophenia
3. **Ground-truth calibration:** run lenses on texts where a human has already performed the same analysis -- not on the framework inventor's own papers (that's extraction), but on case studies analyzed *using* the framework by a third party (e.g., a therapy transcript analyzed with force dynamics, an organizational change case study analyzed with structure-mapping). This gives external validation that the lenses find what human interpreters find.
4. **Confidence-Diversity dual signal:** combine self-reported confidence with inter-model entropy
5. **Human spot checks** (most expensive, ground truth for metric validation)

## Residue as a Dedicated Module

None of the three framework codebooks naturally produce residue. Structure-mapping has "unmapped content" (adjacent), force dynamics has "unaddressed by this framework" (close), blending has "unblended content" (close). But residue in John's sense -- what's generatively ambiguous, what the mess is doing -- requires a dedicated pass.

**The residue module** runs *after* all framework lenses, taking their outputs + the original text as input:

```
Given the original text and these three analyses [A, B, C]:

UNCAPTURED: What in the original text was not addressed by 
ANY of the three analyses? List specific phrases or ideas.

DISAGREEMENT: Where do the analyses disagree about what's 
central or what's happening? State each disagreement as 
"Lens X says ___, Lens Y says ___."

TENSION: What in the original text resists being structured 
at all? What would be lost or distorted by any of these 
framings?

GENERATIVE AMBIGUITY: What in the text might be valuable 
*because* it's unresolved? What should be protected from 
premature structuring?
```

This is a fourth module in the pipeline, not a property of the three lenses. It turns the multi-lens comparison into an explicit operation rather than leaving it to the human reader (though the human still gets the final word via John's Pass 5).

## The Validation Test

**Design change based on research:** prompts must be codebook-style, not vibes-style. See separate artifact for full prompts.

**Time budget (realistic):** Drafting 4 codebook prompts (done, see artifact). Gathering 5 fragments + 1 counterfactual (~30 min). Running 24 API calls (~10 min). Evaluation (~1-2 hours). **Total: half a day, not 30 minutes.**

**Four lenses, not three.** The atheoretic baseline runs in parallel from the start -- it's cheap and its information value is high (see Q1 above).

1. Write four **codebook prompts** with explicit typed slots:
   - Structure-mapping: base domain, target domain, relational correspondences, systematicity score, mapping failures, candidate inferences
   - Force dynamics: agonist, antagonist, tendency, barrier, enabler, resultant, equilibrium state
   - Conceptual blending: input spaces, generic space, cross-space mappings, blended space, emergent structure, vital relations
   - Atheoretic baseline: central tension, recurring element, absent but expected, emotional weight, implicit assumption, most surprising element, direction of movement
2. Run each on 5 real fragments (escalating difficulty) + 1 shuffled counterfactual
3. Evaluate at TWO levels:
   - **Deductive divergence:** do the filled slots contain different information? (Expected: yes, by construction)
   - **Inductive divergence:** do the *interpretations* differ, or just the form? Does structure-mapping surface claims that force dynamics misses, and vice versa? (This is the real test.)
   - **Atheoretic comparison:** does the baseline capture roughly the same stuff as the framework lenses? If so, frameworks are decorative.
4. **Counterfactual control:** run all 4 on the shuffled fragment. If coherent structure still emerges, that's the imposition baseline.

**Why conceptual blending, not frame semantics:** The other Claude caught that v2 silently swapped blending for frame semantics. Frame semantics (Fillmore/FrameNet) is closer to NER+SRL -- structured extraction, not interpretation. Its 1,200 pre-defined frames make it easy to codebookify, but that ease comes at the cost of interpretive leverage. PopBlends (CHI 2023) is the strongest evidence that framework choice produces structural divergence, and that was blending. Keep blending, drop frame semantics.

### Decision tree after the test

```
Run 4 codebook lenses on 5 fragments + 1 counterfactual
         |
         v
Atheoretic baseline vs. framework lenses?
         |
    +----+----+
    Comparable    Frameworks add
    divergence    distinct signal
    |             |
    v             v
    Frameworks    Keep frameworks.
    decorative.   Proceed with
    Use best      multi-lens
    single        architecture.
    codebook.          |
    |                  v
    v            Inductive divergence?
    Skip to      (different claims, not just form)
    inter-model       |
    disagreement +----+----+
    as signal.   Yes       No
                 |         |
                 v         v
                Build     Lenses produce different FORMS
                DSPy      but same INTERPRETATION.
                pipe      Pivot to: inter-model disagreement
                with      as divergence source, with
                multi-    a single best codebook.
                lens
                arch.
```

### Counterfactual control interpretation

```
Shuffled fragment through all 4 lenses
         |
         v
Did coherent structure emerge anyway?
         |
    +----+----+
    Yes       No
    |         |
    v         v
Apophenia    Good: lenses are
baseline     responding to actual
measured.    content, not imposing
Use this     regardless.
rate as
noise floor
for real
fragments.
```

## Open Questions

1. **Codebook granularity.** How specific do slot definitions need to be before the LLM reliably fills them differently? Too loose → convergence. Too tight → the codebook does all the work and the LLM adds nothing.
2. **Fragment granularity.** Sentence, paragraph, or theme level? Affects everything downstream.
3. **Is the disagreement detector a module or a diff?** Can you just compare typed outputs programmatically, or does a separate LLM call (the residue module) add value beyond what diffing gives you?
4. **Does backbone choice affect inductive divergence?** Sonnet might collapse lenses that Opus distinguishes. Matters for SFT data quality.
5. **Residue module calibration.** The residue module is itself an LLM call on LLM outputs. Does it find genuine gaps, or does it just generate plausible-sounding "tensions" regardless? Needs its own counterfactual test.

## Immediate Next Steps

### John's workstream: lens validation
1. ~~Draft four codebook-style system prompts~~ **Done** (structure-mapping, force dynamics, conceptual blending, atheoretic baseline -- see lens-test-codebook-prompts.md)
2. **Gather 5 real fragments** at escalating difficulty (brainstorm → learning arc → personal/creative → messy multi-topic → hardest thing you have) + **1 shuffled counterfactual**
3. **Run the test** (4 lenses × 6 fragments = 24 API calls, ~10 min)
4. **Evaluate at deductive, inductive, and atheoretic-comparison levels** (~1-2 hours)
5. **Based on result:** proceed to DSPy, pivot to inter-model disagreement, or abandon cogsci framing

### Your workstream: RLM recursive exploration
1. **Pick a concrete corpus** to explore (Obsidian vault, paper collection, or both)
2. **Install rlms** and run the basic examples from the repo
3. **Build the note/paper loading layer** (corpus → context variable)
4. **Try one recursive exploration** with a real question, no lenses yet
5. **Then:** integrate codebook lenses into RLM sub-calls, if the lens validation passed

---

*This document is v3. Update after the validation test. The two questions at the top of this doc determine the entire architecture.*
