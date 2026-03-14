Yes. Here is the integrated answer.

**Verdict**

Do **not** jump from Architecture A to a full graph-first Architecture B. The evidence supports a narrower move: stay file-first now, and when you upgrade, upgrade first to a **one-way compiled layer** where Markdown remains the source of truth and the compiled structure is rebuildable, disposable, and query-oriented. The breakpoint is **not raw vault size by itself**. It is a mix of **query type**, **reuse frequency**, and **whether typed operations are paying rent in live work**. ([arXiv][1])

## 1) What this corpus already establishes locally

This repo already has a real Architecture A, not a hypothetical one.

* `AGENT_PROTOCOL.md` and `VAULT_DESIGN.md` define the running model: semantic matching over Markdown, provenance logging, no requirement that the human maintain a graph, and separate human/agent surfaces.
* `DESIGN_CRITIQUE.md` says the current weakness is **cohabitation**, not missing graph structure. The agent’s work is hidden in `_memory/` instead of appearing where the writing happens.
* `THE_MEMBRANE.md`, `THE_DRIFT.md`, `THE_COMPOST.md`, `THE_SEANCE.md`, and `THE_TABLE.md` make the same point from different angles: the missing thing is **surfacing, salience, feedback, and arrangement**, not merely more edges.
* `established-findings.md` already contains the strongest local hybrid prior art: the **SNT pattern**. Markdown is canonical, a compiler emits a structured retrieval layer, and MCP exposes typed operations like support tracing and impact analysis.
* `queries_01-04.md` and `convergence-map.md` already reject “Graphiti by default.” They say flat files are the correct current default and explicitly leave the breakpoint question open.

So the local burden of proof is asymmetric: Architecture A is running and aligned with the vault’s goals; Architecture B is not.

## 2) What is still only proposal-language or architectural inference

John’s `notes_team.md` Graphiti pitch is **proposal**, not evidence. It is useful as a list of hoped-for properties, but not as validation.

Also still in inference-land:

* the repo’s belief that a graph DB should only appear once file traversal becomes slow,
* the idea that a compiled graph will automatically improve creative work,
* any threshold like 500 atoms, 5,000 atoms, or 50,000 atoms.

The repo has **no local benchmark** for those.

## 3) What the web research adds

### A. The production shape of Architecture A is real

The strongest production pattern I found is still basically Architecture A: Claude Code persists project knowledge with `CLAUDE.md`, auto memory, path-scoped rules in `.claude/rules/`, and MCP as the tool/data transport. That is file-first, inference-time assembly with thin bridges, not graph-first knowledge infrastructure. ([Claude][2])

### B. Graph structure helps on specific query classes, not across the board

The cleanest direct comparison does **not** say “graphs beat RAG.” It says something narrower:

* plain RAG does better on **single-hop** and **detail-heavy** questions,
* Community-GraphRAG (Local) does better on **multi-hop/reasoning** questions,
* graph construction itself is lossy: only about **65.8%** of answer entities were present in the HotpotQA graph and **65.5%** in NQ,
* routing or integrating RAG + GraphRAG improves QA, but integration costs more because both pipelines run,
* for summarization, integration often ends up only **comparable to RAG**. ([arXiv][1])

That matters because Query 6 is really asking when A stops being enough. The answer from the literature is: **when your dominant questions stop being local/detail questions and start becoming multi-hop, cross-document, global, or impact-tracing questions**.

### C. GraphRAG’s strongest win is global sensemaking over large corpora

Microsoft’s original GraphRAG result is real, but narrower than the surrounding hype. On **global sensemaking questions** over corpora in the **~1M token range**, GraphRAG improved **comprehensiveness** and **diversity** over conventional vector RAG. It also reduced context-token usage versus text-only map-reduce summarization in some settings. That is a real win for “what are the themes / implications / overall structure?” style questions. It is **not** evidence that every working knowledge base should become a persistent graph. ([arXiv][3])

### D. Long context alone is still not a silver bullet

Three separate lines of evidence point the same way.

* **Lost in the Middle**: models degrade when relevant information is buried in the middle of long contexts; performance drops as context grows, and reader performance saturates before retriever recall. ([arXiv][4])
* **RULER**: despite advertised 32K+ windows, only about half the tested models maintained satisfactory performance at 32K. ([OpenReview][5])
* **OP-RAG / “In Defense of RAG”**: answer quality follows an inverted-U with more retrieved chunks; there is a sweet spot, and brute-force long context can lose to better retrieval ordering. ([arXiv][6])
* **EMNLP 2024 LC vs RAG**: when fully resourced, long-context LLMs can outperform RAG on average, but RAG remains much cheaper, and a routing hybrid (“Self-Route”) can keep most of LC quality at lower cost. ([ACL Anthology][7])

So there is no clean “once the model has a huge context window, flat files are enough forever” result. But there is also no clean “graphs after N files” result either.

### E. The literature keeps drifting toward hybrids

The most interesting thing in the external evidence is not full graph-first systems. It is the number of systems that end up as **hybrids**.

* Microsoft’s **GraphRAG local search** explicitly combines graph-derived structure with **raw text chunks** at query time. ([Microsoft GitHub][8])
* GraphRAG’s indexing package is itself a **pipeline** over unstructured text, with extracted entities, relations, community summaries, and embeddings stored as artifacts. ([Microsoft GitHub][9])
* LlamaIndex’s **PropertyGraphIndex** extracts entities/relations **per chunk**, can persist the index, reload from storage, and still include source text during query. ([LlamaIndex OSS Documentation][10])
* Microsoft’s **LazyGraphRAG** is basically an argument for “compile less, defer more”: same indexing cost as vector RAG, 0.1% of full GraphRAG indexing cost, and strong cost-quality performance by deferring expensive structure use until query time. ([microsoft.com][11])
* **CAG** is another hybrid-ish answer: for constrained corpora that fit comfortably in context, preload the knowledge and cache it instead of running retrieval at inference time. That only works for small/manageable knowledge bases, but it matters because it shows the alternative to full graph-first is not only “vanilla RAG.” ([OpenReview][12])

This is very close to the SNT pattern already described in `established-findings.md`.

### F. Maintenance burden is real, and vendors are quietly admitting it

Microsoft’s own GraphRAG material now reads like an internal critique of full eager graphing.

* Static global search is described as **expensive and inefficient** because many community reports are irrelevant to a given query. That is why they introduced **dynamic community selection**. ([microsoft.com][13])
* LazyGraphRAG exists specifically to avoid the **up-front indexing cost** of full GraphRAG. ([microsoft.com][11])

So even the strongest graph advocates are moving toward “build less, prune early, defer LLM work, and only use structure where it clearly helps.”

Adjacent memory-work shows the same pattern. A March 2026 cost study finds that long-context inference and structured memory have **different cost curves**: long-context cost grows with context length per turn, while memory has a one-time write cost plus flatter reads. At **100k tokens**, the memory system becomes cheaper after about **10 interaction turns**. That is not the same as a Markdown vault benchmark, but it is a concrete reuse threshold. Separately, Mem0 shows that structured memory can cut latency and token cost dramatically versus full context, but its graph variant only adds about **2%** overall score over the non-graph version. ([arXiv][14])

That is the right mental model: **structure pays when you reuse it often enough**, not because graphs are intrinsically superior.

### G. Creative/exploratory evidence is thin

This matters for your repo because Track B is not just QA over notes.

The direct external evidence here is weak. The best targeted benchmark I found is **GraphRAG-Bench**, which includes contextual summarization and creative generation. It reports GraphRAG advantages on harder reasoning/summarization/creative tasks in its structured domains, but plain RAG still wins fact retrieval, and on the Novel dataset RAG is actually stronger on **small-corpus creative generation** than the GraphRAG system they report. ([arXiv][15])

So the web does **not** justify claiming that precomputed structure will automatically improve Drift, Membrane, Séance, or Table. For those, the local design docs are more informative than the current benchmarks.

## 4) The actual breakpoint rule for this repo

Here is the blunt version:

**Flat-file inference-time assembly stops being enough when the cost of repeatedly re-deriving structure exceeds the cost of maintaining a compiled artifact.**
Not before.

For this repo, that means:

### Stay on Architecture A when

* most questions are local, detail-heavy, frame-based, or exploratory,
* the main pain is **surfacing/cohabitation** rather than recall,
* the ontology is still unstable,
* you want Drift/Membrane/Compost/Table behaviors that rely on soft semantic movement and productive ambiguity.

That is your current state.

### Move to an **A+ compiled layer** when all of this starts to happen repeatedly

* you keep asking **typed questions** that are annoying to recover from raw files:

  * “what supports this claim?”
  * “what contradicts this?”
  * “what changed?”
  * “what would break if X changed?”
  * “trace the dependency path”
* the same working set is being reused over and over, so rereading costs matter. The best concrete adjacent number I found is the memory-paper result: at **100k tokens**, persistent memory becomes cheaper after roughly **10 turns**, with the break-even arriving earlier as context grows. Treat that as an economic nudge, not a vault-specific law. ([arXiv][14])
* your substrate is boring enough to compile: claims/questions/evidence, or SNT-style claims + typed edges. Not vibes. Not “frames as communities.”

When those conditions hold, the right move is **not** full graph-first. It is:

> Markdown source of truth → one-way compiler → SQLite/property graph/MCP retrieval layer.

That matches both the repo’s SNT prior art and the external trend toward hybrids. ([LlamaIndex OSS Documentation][10])

### Only move to heavier Architecture B when

* multi-hop/global reasoning is a **core workload**, not an occasional one,
* the graph itself becomes a user-facing artifact or product surface,
* you are willing to own extraction quality, rebuilds, staleness, pruning, entity resolution, and cost,
* the graph is demonstrably paying rent in live cognition, not just existing invisibly.

That is nowhere near proven for this repo yet.

## 5) What Query 6 still needs from external research

Even after the web pass, some holes are still open.

I did **not** find a controlled benchmark that compares:

* raw Markdown/file-first assembly,
* one-way compiled SQLite/property-graph indexing,
* full GraphRAG / persistent graph,

on the **same live PKM vault** with **creative-writing/exploratory tasks** and **real editing churn**.

The papers I found are about:

* news/podcast corpora and global summarization,
* QA over NQ/Hotpot/MultiHop/NovelQA,
* code-repository conversations,
* conversational memory systems,
* structured corpora with creative-generation tasks,

not live Obsidian-style knowledge vaults for writerly work. ([arXiv][3])

So Query 6 still needs:

1. a same-corpus A/B/C benchmark on real Markdown vaults,
2. production reports on sync/staleness under frequent note edits,
3. creative-work metrics for Membrane/Drift/Table-style use,
4. a size-and-reuse sweep: same vault, same queries, different artifact layers.

## Net answer

For **this** repo:

* **Architecture A stays the default.**
* The next justified move is **not Graphiti**.
* The next justified move is a **one-way compiled retrieval artifact** over a boring typed substrate, probably SNT-style, exposed through MCP.
* The trigger is **repeated multi-hop/global/impact/temporal query failure plus repeated reuse**, not “graphs seem smarter.”

If you want the shortest policy sentence:

> **A until the query mix changes. A+compiled index when typed operations and reuse economics dominate. Full B only when the graph itself becomes core work, not background infrastructure.**

[1]: https://arxiv.org/html/2502.11371v1 "https://arxiv.org/html/2502.11371v1"
[2]: https://code.claude.com/docs/en/memory "https://code.claude.com/docs/en/memory"
[3]: https://arxiv.org/html/2404.16130v2 "https://arxiv.org/html/2404.16130v2"
[4]: https://arxiv.org/html/2307.03172v1 "https://arxiv.org/html/2307.03172v1"
[5]: https://openreview.net/forum?id=kIoBbc76Sy "https://openreview.net/forum?id=kIoBbc76Sy"
[6]: https://arxiv.org/html/2409.01666v1 "https://arxiv.org/html/2409.01666v1"
[7]: https://aclanthology.org/2024.emnlp-industry.66/ "https://aclanthology.org/2024.emnlp-industry.66/"
[8]: https://microsoft.github.io/graphrag/query/local_search/ "https://microsoft.github.io/graphrag/query/local_search/"
[9]: https://microsoft.github.io/graphrag/index/overview/ "https://microsoft.github.io/graphrag/index/overview/"
[10]: https://developers.llamaindex.ai/python/framework/module_guides/indexing/lpg_index_guide/ "https://developers.llamaindex.ai/python/framework/module_guides/indexing/lpg_index_guide/"
[11]: https://www.microsoft.com/en-us/research/blog/lazygraphrag-setting-a-new-standard-for-quality-and-cost/ "https://www.microsoft.com/en-us/research/blog/lazygraphrag-setting-a-new-standard-for-quality-and-cost/"
[12]: https://openreview.net/pdf?id=EOG15VvlY4 "https://openreview.net/pdf?id=EOG15VvlY4"
[13]: https://www.microsoft.com/en-us/research/blog/graphrag-improving-global-search-via-dynamic-community-selection/ "https://www.microsoft.com/en-us/research/blog/graphrag-improving-global-search-via-dynamic-community-selection/"
[14]: https://arxiv.org/abs/2603.04814 "https://arxiv.org/abs/2603.04814"
[15]: https://arxiv.org/html/2506.05690v3 "https://arxiv.org/html/2506.05690v3"
