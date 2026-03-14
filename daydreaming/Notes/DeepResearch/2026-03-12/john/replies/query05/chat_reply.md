I put a repo-ready note here: [query05_integrated_answer.md](sandbox:/mnt/data/query05_integrated_answer.md)

Here’s the integrated answer in the `queries_01-04.md` register.

Blunt verdict: Query 5 comes back **yes in narrow pieces, no in the strong form**. There is credible evidence that learner artifacts can support learner modeling, but the strongest validated artifacts are revised explanations, reflective writing, concept maps, and instrumented process logs inside designed learning environments. I did **not** find strong evidence that an open-ended personal PKM vault can, by itself, serve as a reliable passive mastery model without explicit assessment or explicit learner signals. ([ETASR][1])

I treated this as four evidence layers: local repo documents, imported external findings already present in `query03.md` / `query04.md` / `bidirectional_knowledge_text_coupling.md`, fresh web research, and the residue that still looks open.

## What the repo already establishes

Locally, the repo is already asking the right question. `launch-queries-john-round2.md` reframes Thread 5 away from “do temporal graphs exist?” and toward “what counts as evidence that a learner knows, half-knows, forgot, or restructured something?” `established-findings.md` sharpens that further: the valuable temporal object is changing **commitments, salience, confidence, and neglected regions with provenance**, not timestamped edges. `AGENT_PROTOCOL.md`, `VAULT_VISION.md`, `DESIGN_CRITIQUE.md`, `writing_concept_vault-as-personalized-learning.md`, and the 2026-03-09 journal note define the learner’s own artifacts here: journals, drafts, project notes, atoms, frames, and memory traces.

That local inference is still the strongest one. The vault has plenty of candidate evidence streams. What it does **not** yet establish is that those streams are sufficient to infer mastery reliably.

## What Round 1 already added

`query04.md` already landed the key correction: temporal KG infrastructure exists, but temporal learner modeling for a personal vault mostly does not. The practical lesson was “log events and evidence, do not assume topology drift is enough.”

`query03.md` added the second correction: in deployed learning systems, the learner model is usually an **overlay** on an authoritative curriculum graph, not a personal PKM graph standing in for mastery.

And the imported landscape note, `bidirectional_knowledge_text_coupling.md`, already brought in the nearest adjacent precedents: discourse graphs, KSG, ARTIST, provenance-rich dual-view systems, and the general idea that writing artifacts can be partially formalized. None of that, though, is a proof that a free-form vault can silently infer what someone now understands.

## What fresh web research adds

### 1. The clean deployed bucket is small

The strongest live graph-aware learner model I found is **Math Academy**. Their public system description says student answers are overlaid on a knowledge graph, the student model computes a “knowledge profile,” diagnostics estimate a “knowledge frontier,” and spaced repetitions can “trickle down” the graph to simpler encompassed topics. That is real graph-aware learner modeling. But it is still built on explicit answers, explicit diagnostics, and a domain graph authored by the platform, not on passive reading of personal notes. ([Math Academy][2])

The closest recent deployed-paper hit is a 2025 **Scientific Reports** English-learning system that combines a knowledge graph, graph-based propagation, forgetting, and reinforcement learning. It was deployed as a WeChat Mini Program and evaluated on **200 active learners over 3 months with 18,742 valid interactions**, reporting **Precision 0.85, Recall 0.82, F1 0.84, MAE 0.12, RMSE 0.18**. But it still begins with a **diagnostic entrance test** and updates mastery from explicit interaction feedback. So this is “graph-aware learner modeling from platform interactions,” not “infer mastery from an open-ended personal vault.” ([ResearchGate][3])

The **CourseMapper / PKG** line is transparent and interesting, but not passive. In the UMAP 2024 transparent learner-model paper, learners explicitly mark concepts as **Did Not Understand**; the learner model is then built from those DNU concepts inside a scrutable PKG. That is useful because it shows a middle path between black-box mastery models and full quizzes. It is still not silent inference from notes. ([University of Duisburg-Essen][4])

The consumer note-tool side is weaker than the repo might hope. **FSRS** is a real improvement for spaced repetition and models memory state in terms of **difficulty, stability, and retrievability**; RemNote’s docs say it can reduce reviews by **20–30%** for the same retention. But that is still per-card, per-history memory modeling. It is not graph-aware learner modeling in the strong sense. ([GitHub][5])

### 2. The right conceptual umbrella is stealth assessment, not “graph intelligence”

The assessment literature already has a name for what Query 5 is reaching for: **stealth assessment**. In that framing, you unobtrusively collect interaction traces, define a competency model, an evidence model, and a task model, and update estimated mastery probabilistically over time. That is exactly the right shape for “infer progress without stopping to quiz.” But the catch matters: stealth assessment usually assumes a heavily instrumented digital environment or game. It does **not** solve the harder repo-specific problem of interpreting free-form journals, drafts, and note links with enough reliability to call them mastery evidence. ([ERIC][6])

### 3. ENA is real, useful, and narrower than the repo’s hope

**Epistemic Network Analysis** is legitimate machinery here. The official ENA description says it identifies and quantifies connections among elements in coded data, represents them as dynamic network models, and lets you link those models back to the original data. A 2025 scoping review in science education shows ENA now gets used beyond dialogue alone, including artifacts like drawings and textbook representations. So ENA is not just a discourse toy. ([Epistemic Network][7])

But the concrete applications I found still cluster around classroom-ish artifacts, not PKM vaults. A 2024 reflective-writing study tracked **43 students** across **10 reflective-writing tasks** over a **16-week** semester and used ENA to distinguish higher- and lower-performing reflection patterns. That is encouraging. It is also a long way from “we can read someone’s Obsidian vault and infer evolving mastery.” ([ETASR][1])

I did **not** find a clear line of work applying ENA directly to something like Symbiotic Vault artifacts: free-form journals, drafts, atoms, and memory traces as a persistent personal notebook. The examples I found clustered around discourse, revised explanations, reflective writing, drawings, and collaborative/course artifacts. ([Springer Link][8])

### 4. Revised explanations are the strongest artifact signal I found

The best evidence is not “graph topology changed.” It is “the learner revised an explanation in a way that reflects better integration.” A 2025 study with **162 seventh graders** found that a full NLP-driven adaptive dialog improved knowledge-integration scores relative to a partial-dialog condition, with **β = 1.113, z = 2.54, p = 0.0112** and a post-instruction interaction of **β = 1.112, z = 1.96, p = 0.0496**. A related deployment summary reports gains across **1,036 students**, **10 teachers**, and **5 schools**. That is solid evidence that artifact revision, especially explanation revision, carries learning signal. ([MDPI][9])

The Oxford chapter on pedagogically informed NLP makes the same point more sharply: students who actually **integrated ideas during revision** showed larger pre-post gains than those who did not, with **n = 181, mean gain 0.81, SD 1.16** versus **n = 159, mean gain 0.43, SD 0.96**, **t(338) = 3.19, p = 0.002**. That is close to Query 5’s core hope. Revision behavior is evidence. ([OUP Academic][10])

### 5. Process traces matter, but mind the target variable

A lot of learning-analytics work on writing does find strong signal in activity traces. One 2024 keystroke-logging study with **158 students** found that process features explained **56.1% to 69.3%** of the variance in text quality. Another 2025 multi-source writing study found that high- and low-performing students differed in time allocation, self-regulated-learning transitions, and network properties of their trace data. These are real signals. But the distinction matters: many of these studies predict **text quality** or **performance class**, not durable conceptual mastery. That is useful for the repo, but it is not the same claim. ([ScienceDirect][11])

### 6. Concept maps support structure-aware inference, but they cheat in your favor

The strongest topology-flavored evidence comes from **concept maps**, because concept maps are already semi-formalized learner artifacts. A 2023 study collected **359 concept maps** from **36 students** across weeks 2–12 and reconstructed them as directed network topologies alongside exam scores. **Cronus**, an automated concept-map feedback tool, was evaluated on **78 concept maps** and compares student maps against an instructor map to flag missing concepts, misconceptions, hierarchy mismatches, and topological differences. This is real structure-aware artifact analysis. But it transfers imperfectly to prose notes because concept maps have already done half the formalization work for you. ([PMC][12])

## Net answer for John’s track

So the integrated answer is:

* **Yes**, learner modeling without explicit quizzes exists in pieces.
* The good pieces mostly come from **revised explanations, reflective writing, concept maps, and instrumented interaction traces**.
* **No**, I did not find strong evidence that a free-form personal PKM vault can passively infer mastery with the reliability people usually want from a learner model.
* The deployed systems that look closest still rely on either **diagnostics**, **explicit learner signals** like DNU, or dense **platform interaction logs**. ([Math Academy][2])

That means the repo’s current direction is mostly right. The next move is not “believe the graph.” The next move is:

1. keep the vault **file-first and provenance-first**
2. treat the learner model as an **overlay**, not an authority
3. compile artifact events such as **asserted claim, revised claim, added support, noted contradiction, revisit after delay, successful retrieval, failed retrieval, abandoned thread**
4. use ENA / NLP scoring / topology as **analytic layers**, not as a truth machine
5. ask for **minimal explicit signals** when ambiguity is high, like DNU, confidence, “stuck,” or a short self-explanation
6. where there is an authoritative curriculum graph, align to it; where there is not, do **not** let the vault pretend to be a mastery graph. ([Epistemic Network][7])

My confidence in that overall read is high$_{85%}$. The weak point is not whether artifacts carry signal. They do. The weak point is whether free-form PKM artifacts carry **enough** signal, with **enough** calibration, to replace explicit learner feedback. I don’t see that established yet.

Look away for ten seconds and relax your shoulders.

[1]: https://etasr.com/index.php/ETASR/article/download/7274/3645 "https://etasr.com/index.php/ETASR/article/download/7274/3645"
[2]: https://www.mathacademy.com/how-our-ai-works "https://www.mathacademy.com/how-our-ai-works"
[3]: https://www.researchgate.net/publication/396169307_Simulation_of_personalized_english_learning_path_recommendation_system_based_on_knowledge_graph_and_deep_reinforcement_learning "https://www.researchgate.net/publication/396169307_Simulation_of_personalized_english_learning_path_recommendation_system_based_on_knowledge_graph_and_deep_reinforcement_learning"
[4]: https://www.uni-due.de/imperia/md/content/soco/alatrash_ukde24_final.pdf "https://www.uni-due.de/imperia/md/content/soco/alatrash_ukde24_final.pdf"
[5]: https://github.com/open-spaced-repetition/fsrs4anki/wiki/The-Algorithm "https://github.com/open-spaced-repetition/fsrs4anki/wiki/The-Algorithm"
[6]: https://files.eric.ed.gov/fulltext/ED612156.pdf "https://files.eric.ed.gov/fulltext/ED612156.pdf"
[7]: https://www.epistemicnetwork.org/ "https://www.epistemicnetwork.org/"
[8]: https://link.springer.com/article/10.1007/s10956-024-10193-x "https://link.springer.com/article/10.1007/s10956-024-10193-x"
[9]: https://www.mdpi.com/2227-7102/15/2/207 "https://www.mdpi.com/2227-7102/15/2/207"
[10]: https://academic.oup.com/book/58946/chapter/492996783 "https://academic.oup.com/book/58946/chapter/492996783"
[11]: https://www.sciencedirect.com/science/article/pii/S1060374324000201 "https://www.sciencedirect.com/science/article/pii/S1060374324000201"
[12]: https://pmc.ncbi.nlm.nih.gov/articles/PMC9939863/ "https://pmc.ncbi.nlm.nih.gov/articles/PMC9939863/"
