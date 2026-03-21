# Mueller Mechanism Condensation Spec

## Purpose

Produce prose-level descriptions of Mueller's cognitive mechanisms, suitable for reasoning about where LLM judgment plugs into persistent Clojure structure. Not a code port. Not a summary. Not redesign. A description precise enough to identify loop shapes, judgment points, accumulation effects, and the properties those depend on -- and to trace how mechanisms chain together into behavior that stateless prompting cannot replicate.

## Output location

```
daydreaming/Notes/Book/daydreaming-in-humans-and-machines/
  condensation/
    mechanisms/              <- one file per mechanism from the inventory below
    chain-trace-a.md         <- planning / analogy (goal-directed)
    chain-trace-b.md         <- reminding / serendipity (accident-driven)
  mueller-mechanism-condensation-spec.md   <- this file (lives alongside chapters)
```

## Mechanism inventory

Fixed list. Every pass must cover all of these. If a mechanism turns out to be trivial or purely domain-specific, say so in its file rather than omitting it.

**Control & scheduling:**
1. Emotion-driven control (top-level cycle, mode oscillation, concern selection)
2. Need decay / emotion decay
3. Concern initiation
4. Concern termination

**Planning:**
5. Planning rule application (regular)
6. Inference rule application (chaining to fixpoint)
7. Subgoal creation
8. Context sprouting / backtracking
9. Analogical rule application (episode-guided planning)

**Episodic memory:**
10. Episode storage
11. Episode retrieval (index-threshold)
12. Reminding cascade

**Creativity:**
13. Serendipity recognition (rule intersection search + path verification)
14. Mutation

**Emotions & daydreaming goals:**
15. Emotion generation (from goal outcomes)
16. Daydreaming goal activation (rationalization, revenge, roving, reversal, recovery, rehearsal, repercussions)
17. Rationalization strategies (hidden blessing, mixed blessing, external attribution)

**Learning / evaluation:**
18. Episode evaluation (realism, desirability scoring)
19. Analogical repair cases (the four: reversal, recovery, rehearsal, repercussions as planning strategies)

This is approximately 19 mechanisms. Some may merge or split during the pass -- that's fine, but document why.

## Per-mechanism format (10 fields)

### 1. Mechanism name

From the inventory above. Use the canonical name.

### 2. Source anchors

Chapter, page range, figures, table references, and Appendix A trace examples where this mechanism is visible in action. Also note the corresponding `.cl` source file(s).

### 3. Cognitive phenomenon (one line)

What human cognitive experience does this mechanism model? Associative drift, shower insight, counterfactual replay, mood-congruent attention, cognitive dissonance reduction, fantasy elaboration, etc. This is the filter for whether the mechanism is load-bearing for "giving LLMs inner life" or is an artifact of 1990 implementation constraints.

### 4. Kernel status (one line)

Does the Clojure kernel (`kernel/src/daydreamer/`) already implement this? Fully, partially, or not at all? Cite the namespace if it exists. Gaps here are actual work items.

### 5. Loop shape

What triggers this mechanism? What does it iterate over? What does it produce? What state does it read and write? This is the Clojure skeleton -- the part that stays deterministic and persistent. Described in prose, not code, but precise enough that you could write the function signature.

### 6. Judgment points

Where within the loop does Mueller use rigid matching or hard-coded evaluation that could be contextual? Each judgment point is a moment where the execution pauses and asks "what should happen here?" -- pattern match that could be soft, plausibility score that could be contextual, consequent that could be generated, evaluation that could be nuanced. Not every mechanism has these. Some are purely structural. If none, say so explicitly.

### 7. Accumulation story

What persists after this mechanism runs? How does it change what happens next time? What grows? What decays? This is the reason the mechanism can't be replaced by prompting alone. If there's no accumulation, flag that -- it may be a candidate for pure LLM replacement.

### 8. Property to preserve

The durable architectural constraint this mechanism depends on, independent of implementation technique. Example: "intersection indexing preserves *which* indices matched, not just *that* something was similar -- the reminding cascade needs the individual indices to propagate." This field survives technology changes. When someone later proposes replacing a mechanism with embeddings or a different retrieval method, this field tells you what you can't lose.

### 9. Upstream triggers / downstream triggers

What mechanism(s) activate this one? What does this mechanism activate in turn? This field makes the chain traces possible -- if every mechanism lists its triggers, the chain practically writes itself.

### 10. Mueller-faithful description vs. candidate hybrid cut

Hard split. Two separate paragraphs, clearly labeled.

**Mueller-faithful**: What Mueller actually built. Describe the mechanism as it exists in the book. No modernization, no "an LLM could do this." Ground truth. The book is the primary source for intent and rationale. When the code diverges from the book's description, note the divergence rather than silently choosing one.

**Candidate hybrid cut**: Where the modern cut might go, referencing specific judgment points from field 6. This is a proposal that can be wrong. Keep it separate so the faithful description remains a stable reference.

### Interface shape (required)

When this mechanism calls out for judgment (per field 6), what typed structure crosses the boundary? What does the LLM receive, what must it return? Must be one of:

- **none** -- purely structural, no judgment points
- **unclear** -- judgment point exists but the interface shape is not yet determined (explain why)
- **tentative schema** -- concrete enough that you could write the Clojure function signature for the call site

"Unclear" is acceptable during initial extraction but must include a note on what's blocking resolution. The goal is that no mechanism leaves this field blank.

## Integration patterns (vocabulary for judgment points)

When tagging judgment points (field 6) and interface shapes, use these named patterns so the condensation is specific about *how* the LLM would plug in, not just *that* it could:

- **Co-routine judgment**: Clojure asks a typed question, LLM returns a typed answer. Example: "is this analogy apt?" -> {apt: bool, reasoning: string}
- **Rule-with-LLM-consequent**: Rule fires structurally (found via connection graph, matched by indices) but its consequent is generated by LLM rather than pattern-instantiated. The rule stays in the graph -- searchable, indexable, part of the connection structure -- but what it produces is contextual.
- **LLM-as-evaluator**: Clojure generates candidates (plans, episodes, paths), LLM scores or ranks them. Example: realism scoring, desirability assessment, path usefulness.
- **LLM-as-content-generator**: Clojure provides structured context (concern, operator, retrieved episodes, emotional state), LLM generates scenario content that becomes a new episode or plan fragment.

Not every judgment point will fit cleanly into one pattern. But naming the pattern makes the interface shape concrete rather than hand-wavy.

## Chain trace documents

Two separate chain traces, each one page. Not Appendix A's step-level detail -- mechanism-level. Show which mechanisms fire in what order, what each one passes to the next, and what accumulates at each stage.

These traces describe Mueller's system as it runs. They are faithful descriptions of execution flow, not modern redesign.

### Trace A: Planning / analogy (goal-directed)

Concern selected -> planner invoked -> rule found via connection graph -> episode retrieved -> analogical application -> context sprouted -> evaluation -> episode stored.

### Trace B: Reminding / serendipity (accident-driven)

Input arrives -> indices activate -> episode retrieved -> reminding cascade -> intersection search across rule graph -> surprise path found -> verification -> new plan -> concern initiated.

### At each link, note:

- What typed structure passes between the two mechanisms
- **Structural** (deterministic graph/arithmetic) or **judgment** (rigid matching / hard-coded evaluation in Mueller's implementation)

Do NOT add modern runtime analysis (sync/async, latency budget, LLM call placement) to these traces. That belongs in a separate downstream document alongside adaptation work. These traces describe Mueller's execution flow as-built.

## Filter criterion

Produce every mechanism in the inventory on first pass. Filter after, not before. Connections between mechanisms aren't visible until they're all laid out. The thing that looks minor in Chapter 7 might be the key interface point when read alongside Chapter 5's serendipity.

When filtering later, the question is: **does this mechanism's implementation preserve a property that the accumulation or cascade depends on?** Not "is this the best 2026 way to do it."

## Source hierarchy

- **Primary**: Chapter prose (files `02`-`11`), image-reviewed supplements (files `17`-`39`). The book is the source for intent, rationale, and mechanism description.
- **Verification**: Mueller source (`.cl` files in `daydreamer/`), kernel Clojure (`kernel/src/daydreamer/`). Code resolves ambiguity in the prose and confirms actual behavior.
- **Divergence rule**: When book and code disagree, note both and flag the divergence. Do not silently pick one. The book describes what Mueller intended; the code describes what he shipped. Both matter.

## Scope

Chapters in priority order: 7 (implementation), 2 (architecture), 3 (emotions), 4 (learning), 5 (creativity), 6 (interpersonal domain). Chapter 8 for memory design rationale. Chapter 9 as context, not extraction target.

## Relationship to other documents

- `daydreaming/Notes/mueller-adaptation-working-spec.md` is a downstream stage-adaptation document. Its simplification choices should be evaluated against the "property to preserve" fields produced by this condensation. Where it drops something flagged as load-bearing, that must be a conscious, documented decision.

## Success criterion

After condensation, you should be able to:

1. Point at any mechanism and say: the loop shape is here, these are the judgment points, this is what accumulates, this is the property that can't be lost.
2. Trace one input through each chain trace and see where it touches each mechanism and what typed structure passes between them.
3. Read the interface shape for every mechanism that has judgment points -- none left blank.
4. Distinguish what Mueller built from what we propose to change, with divergences between book and code noted.
5. Check the inventory and confirm every mechanism has a file in `condensation/mechanisms/`.
