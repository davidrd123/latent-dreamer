# RLM Recursive Exploration: What a Session Actually Looks Like

**Purpose:** Concrete walkthrough of how RLM-style recursive decomposition works for corpus exploration -- both literature search and personal notes at scale. Not architecture, not diagrams. A session trace.

**Key principle:** The root model never sees the full corpus. It writes code to navigate, peek, decompose, and recurse. Summaries are never the substrate -- raw text is always accessible via selective inspection.

---

## Example 1: Literature Search with Ground-Truth Calibration

**Scenario:** You want to understand whether LLM-generated summaries overgeneralize. You have the Peters & Chin-Yee (2025) paper, plus 5 related papers from the deep research. You want to recurse in with a specific question, not just "summarize."

### Root call

The root model receives ONLY your query, not the papers:

```
Query: "What specific mechanisms cause LLMs to overgeneralize 
when summarizing? Not just 'they do it' -- what's the causal 
story? And does prompting to be careful make it worse, and if 
so why?"

Context variable: corpus (6 papers, ~120 pages, ~250K tokens)
```

Root model doesn't try to read all 250K tokens. It writes code:

```python
# Root model's first move: inspect what's available
papers = context.list_documents()
for p in papers:
    print(p.title, p.length, p.sections[:5])
```

Output:
```
Peters & Chin-Yee 2025 - "Overgeneralization in LLM summaries" - 14K tokens
  [Introduction, Methods, Results, Discussion, Supplementary]
Wang et al. 2025 - "Spurious correlations in hallucination" - 18K tokens
  [Abstract, Related Work, Methodology, Experiments, Analysis]
Dunivin 2025 - "Scaling Hermeneutics" - 22K tokens
  [Introduction, Framework, Experiments, Results, Discussion]
...
```

Root model reasons: "Peters & Chin-Yee is the primary source. I need their Methods (what did they measure) and Discussion (causal explanation). Wang et al. might explain the mechanism. Let me recurse."

### First recursion: targeted section reads

```python
# Root model spawns sub-calls with specific questions
sub1 = rlm.completion(
    query="What three types of overgeneralization did they define, "
          "and what measurement method did they use for each?",
    context=corpus.get_section("Peters_2025", "Methods")
)

sub2 = rlm.completion(
    query="Do they explain WHY prompting for accuracy increased "
          "overgeneralization? What mechanism do they propose?",
    context=corpus.get_section("Peters_2025", "Discussion")
)

sub3 = rlm.completion(
    query="What is the relationship between spurious correlations "
          "and overgeneralization? Is overgeneralization a special "
          "case of spurious correlation-driven hallucination?",
    context=corpus.get_section("Wang_2025", "Analysis")
)
```

Each sub-call sees only the relevant section (~2-4K tokens), not the full paper. The question is specific. The sub-model can focus.

### Second recursion: following a thread

Sub2 comes back with: "Peters & Chin-Yee suggest that accuracy prompts trigger a 'hedging-then-overcorrecting' pattern -- the model tries to be more precise, which activates more confident language patterns, which paradoxically increases generalization. They cite evidence from the present-tense shift data but acknowledge the mechanism is speculative."

Root model: "Interesting. 'Hedging-then-overcorrecting' -- is this observed elsewhere? Let me check Wang et al. and the calibration literature."

```python
# Root model searches across papers for related claims
hits = context.search("overcorrect OR overconfiden OR calibrat")
for hit in hits:
    print(hit.paper, hit.section, hit.snippet[:200])
```

This finds a passage in the KalshiBench paper about extended reasoning worsening calibration. Root model recurses into that:

```python
sub4 = rlm.completion(
    query="Does extended reasoning / chain-of-thought make models "
          "more confident without making them more accurate? Is "
          "this the same mechanism as accuracy-prompted "
          "overgeneralization?",
    context=corpus.get_section("KalshiBench", "Results") 
          + corpus.get_section("Peters_2025", "Discussion")
)
```

Now sub4 sees two relevant sections side by side -- still small enough to fit in a window, but combining evidence across papers that the root model identified as potentially connected.

### Synthesis

Root model now has sub1-sub4 results. It assembles:

```python
synthesis = rlm.completion(
    query="Given these findings, construct the causal story: "
          "what mechanisms cause overgeneralization, why does "
          "prompting for accuracy make it worse, and what does "
          "this imply for pipeline design?",
    context=format_sub_results([sub1, sub2, sub3, sub4])
)
```

The synthesis call sees ~4K tokens of *targeted extractions*, not 250K tokens of raw papers. But every claim traces back to a specific section of a specific paper. No summarization was performed -- the root model *navigated* to the relevant passages and brought them together.

### What you get that summarization can't give you

- The connection between Peters & Chin-Yee's "hedging-then-overcorrecting" and KalshiBench's "extended reasoning worsens calibration" -- a cross-paper link that a summary of either paper alone wouldn't surface
- Direct quotes and section references, not paraphrased summaries
- The ability to recurse *further* if the synthesis raises new questions ("wait, is this also related to the sycophancy literature?") without re-reading anything
- The raw papers are still there. Nothing was compressed. You can go back and peek at any section with any new question.

---

## Example 2: Personal Notes at Scale

**Scenario:** 200 notes in an Obsidian vault, accumulated over 8 months. You want to understand how your thinking about a project evolved, without pre-summarizing.

### Root call

```
Query: "How has my thinking about [the conductor project] 
changed over time? What ideas appeared, disappeared, merged, 
or split? Where am I stuck now vs. where I was stuck 3 months ago?"

Context variable: vault (200 notes, ~150K tokens)
```

Root model's first move isn't to read everything:

```python
# Find notes mentioning the conductor project
hits = context.search("conductor OR gestural OR instrument OR performance interface")
print(f"Found {len(hits)} relevant notes")

# Sort by date
hits.sort(key=lambda h: h.date)

# Sample early, middle, recent
early = hits[:3]
middle = hits[len(hits)//2 - 1 : len(hits)//2 + 2]
recent = hits[-3:]

for note in early + middle + recent:
    print(f"{note.date}: {note.title} ({note.length} tokens)")
    print(f"  First 200 chars: {note.text[:200]}")
```

Root model now has a temporal skeleton: 9 notes spanning the project, with previews. It decides what to recurse into:

```python
# Read early notes fully (they're short, and origin matters)
sub_early = rlm.completion(
    query="What was the initial conception of the conductor project? "
          "What was the core problem being solved, and what was the "
          "proposed approach?",
    context=concat([n.text for n in early])
)

# For middle period, ask about shifts
sub_mid = rlm.completion(
    query="Compared to the initial conception, what changed? "
          "New constraints, new ideas, abandoned approaches, "
          "new influences?",
    context=concat([n.text for n in middle])
)

# For recent, ask about current state and stuckness
sub_recent = rlm.completion(
    query="What is the current state? Where is momentum, where "
          "is stuckness? What questions are open?",
    context=concat([n.text for n in recent])
)
```

### Following threads that emerge

Sub_mid comes back mentioning "you started talking about Tidal Cycles around this period and it shifted the framing from visual-conductor to audio-reactive."

Root model: "Interesting. Let me find the Tidal Cycles thread specifically."

```python
tidal_hits = context.search("Tidal OR Strudel OR algorave OR live coding")
tidal_hits.sort(key=lambda h: h.date)

sub_tidal = rlm.completion(
    query="Trace the Tidal Cycles / live coding influence: "
          "when did it enter, what did it change about the "
          "conductor project, and is it still active or did "
          "it fade?",
    context=concat([n.text for n in tidal_hits[:8]])
)
```

### Where lenses would plug in

At any recursion level, you could apply the codebook lenses:

```python
# Run force dynamics on the "stuckness" in recent notes
sub_stuck_fd = rlm.completion(
    system=FORCE_DYNAMICS_CODEBOOK,  # from the other artifact
    query=recent_notes_text
)

# Run structure-mapping on the Tidal Cycles influence
sub_tidal_sm = rlm.completion(
    system=STRUCTURE_MAPPING_CODEBOOK,
    query=tidal_notes_text
)
```

The lenses operate on *selectively retrieved raw text*, not on summaries. The RLM navigation decides *what* to lens. The lenses decide *how* to analyze it. These are independent concerns.

### What you get that a flat context window can't

- **Temporal navigation without summarization.** You're not asking "summarize 8 months of notes." You're asking specific questions at specific time slices, with the ability to follow threads that emerge.
- **The 200 notes are never compressed.** Any note can be re-read with any new question at any time. The RLM navigates; it doesn't digest.
- **Recursive depth matches the question's depth.** "How has my thinking changed" requires temporal comparison. "What was the Tidal Cycles influence" requires thematic threading. "Where am I stuck" requires force-dynamic analysis of recent notes. Each recursion level does one thing well.
- **Emergent connections.** The root model might notice that a note from month 2 about "latency as creative material" connects to a note from month 7 about "delay as compositional element" -- a link you didn't see because they were 5 months apart with 80 notes between them. It can surface this because it can *search and compare* without having to hold everything in working memory simultaneously.

---

## The Pattern

Both examples follow the same loop:

```
1. Root receives query + opaque context reference
2. Root inspects context structure (search, list, sample)
3. Root identifies relevant slices
4. Root spawns sub-calls with specific questions on specific slices
5. Sub-results may trigger further recursion (following threads)
6. Synthesis call combines targeted extractions
7. At any level, lenses/codebooks can be applied to raw slices
```

Summarization never enters the loop. The context is navigated, not compressed.

---

## Practical Notes

**What exists today:**
- `pip install rlms` -- the official library
- `dspy.RLM` -- built into DSPy 3.x
- Supports OpenAI, Anthropic, OpenRouter, local models via vLLM
- Environments: local (fast, no isolation), Docker, Modal (isolated)

**What you'd need to build:**
- The note/paper loading layer (Obsidian vault → context variable, or PDF → context variable)
- The codebook integration (wrapping lens prompts as sub-call system prompts)
- A logging layer so you can see the recursion tree after the fact

**When to bother:**
- NOT for the 5-fragment lens test (plain API calls, no RLM needed)
- YES when you're exploring a corpus of 50+ documents with specific questions
- YES when the question requires temporal or thematic threading across many sources
- YES when you want to apply lenses to selectively retrieved raw text at scale

**Cost model:**
RLM(GPT-5-mini) at 132K tokens ≈ same cost as a single GPT-5 call but +34 points on accuracy. The recursion adds calls but each call is small. For a 200-note vault exploration, expect 15-30 sub-calls, mostly on small context windows. Cheaper than one massive context-stuffed call, and significantly better.
