---
title: "GradMem: Learning to Write Context into Memory with Test-Time Gradient Descent"
source: "https://arxiv.org/html/2603.13875v1"
language: "en"
word_count: 9806
---

Yuri Kuratov    Matvey Kairov    Aydar Bulatov    Ivan Rodkin    Mikhail Burtsev

###### Abstract

Many large language model applications require conditioning on long contexts. Transformers typically support this by storing a large per-layer KV-cache of past activations, which incurs substantial memory overhead. A desirable alternative is *compressive memory*: read a context once, store it in a compact state, and answer many queries from that state. We study this in a context removal setting, where the model must generate an answer without access to the original context at inference time. We introduce GradMem, which writes context into memory via *per-sample test-time optimization*. Given a context, GradMem performs a few steps of gradient descent on a small set of prefix *memory tokens* while keeping model weights frozen. GradMem explicitly optimizes a model-level self-supervised context reconstruction loss, resulting in a loss-driven write operation with iterative error correction, unlike forward-only methods. On associative key–value retrieval, GradMem outperforms forward-only memory writers with the same memory size, and additional gradient steps scale capacity much more effectively than repeated forward writes. We further show that GradMem transfers beyond synthetic benchmarks: with pretrained language models, it attains competitive results on natural language tasks including bAbI and SQuAD variants, relying only on information encoded in memory.

Machine Learning, Deep Learning, test-time training, test-time optimization, context compression, transformers, memory mechanisms

## 1 Introduction

Large language models are increasingly deployed in settings where task-relevant information resides in long, external contexts: documents, codebases, tool interactions in agent workflows, and dialogue histories spanning multiple sessions [^24] [^45] [^40] [^41]. In these regimes, the challenge is not only to support long contexts, but to do so efficiently and *reusably* —ideally, the model reads a context once, stores what matters, and answers many queries without repeatedly re-processing the same tokens. The dominant approach is to retain intermediate activations via the KV-cache (and various compression schemes thereof), which reduces recomputation but can impose substantial memory overhead and does not naturally produce a portable representation of the context. A complementary alternative is to provide the model with a *compact memory state* that is constructed from a context and then reused across subsequent queries. Crucially, many applications require incorporating new information *without retraining or fine-tuning the full model*: we want to adapt the model to the current context by writing into a separate memory representation, while keeping the pretrained parameters fixed.

![Refer to caption](https://arxiv.org/html/2603.13875v1/figs/GradMem_overview_short.png)

Refer to caption

Recent work on *test-time training* shows that a model can adapt to the current context via gradient-based updates during inference, and that iterative optimization of input embeddings can losslessly encode thousands of tokens given enough steps [^38] [^21]. Motivated by this observation, we introduce GradMem.<sup>1</sup> GradMem writes context into memory by *direct per-sample optimization* at test time (Figure 1). Specifically, GradMem treats embeddings of special memory tokens as *writable state* and performs a small number of *gradient descent* updates on this state for each context. This is *test-time training in the literal sense*: during inference, we execute a short inner-loop optimization on the current example. Crucially, GradMem cleanly separates *memory* from *model weights*: the base model parameters remain fixed, while adaptation to new contexts occurs solely through updates to the memory state. Unlike forward-only writing rules, this loss-driven inner loop provides per-example feedback, enabling GradMem to iteratively correct write errors as it forms a compact memory representation.

A key design choice in GradMem is the use of an *explicit, model-level WRITE objective* that is independent of the downstream supervision. In this paper, we focus on a simple self-supervised WRITE objective— *reconstruction* —computed from the language model’s own predictions and backpropagated to the memory tokens. Because the objective is explicit, GradMem provides a direct way to trade compute for compression: additional gradient steps lead to a better memory state.

The intuition behind GradMem is simple. First, standard training with SGD can be viewed as a mechanism that *writes data into parameters* of a model via gradient updates (i.e., train set memorization); analogously, we treat memory as a parameter-like state to store the current context. Second, unlike one-shot forward writing (e.g., with text encoders), optimization provides an explicit signal of *what has not been encoded yet*: the reconstruction loss concentrates on the parts of the context that the model currently predicts poorly. Thus, gradient-based writing naturally prioritizes novel, unpredictable or high-entropy inputs and iteratively reduces reconstruction error. Third, while *lossless* context encoding via iterative optimization is known to be possible, it typically requires *hundreds to thousands* of gradient steps to achieve near-perfect reconstruction [^21]. In contrast, GradMem targets the few-step regime: by meta-learning the memory initialization and model parameters, we enable effective context writing with only a small number of test-time gradient steps.

We evaluate GradMem primarily on associative KV-retrieval task under context removal setting, a clean synthetic benchmark that directly measures how much information can be stored in a fixed-size memory. Across a wide range of settings, GradMem stores more key–value pairs than forward-only methods that encode the context into memory with the same memory size. Our results also show that *how* the memory state is updated matters as much as *how many times* it is updated: even a single gradient-based WRITE update can write more information than a single forward-only update, and additional gradient descent steps further increase capacity. In contrast, repeating WRITE using only forward operations (e.g., re-processing the context multiple times) yields much weaker or less consistent gains. Beyond this synthetic setting, we study how performance varies with the number of WRITE steps, context length, and demonstrate that the same task-agnostic reconstruction objective transfers to pretrained language models on natural language tasks such as QA on bAbI, short SQuAD variants, and language modeling.

This paper makes the following contributions:

1\. GradMem: gradient-based context memorization. We introduce GradMem, a memory mechanism that encodes a context into a compact memory state by performing a small number of test-time gradient descent steps on memory tokens while keeping the base model weights fixed. GradMem constructs memory using an explicit self-supervised WRITE objective (context reconstruction) computed at the model level, without requiring specialized per-layer memory update rules.

2\. Few-step gradient writing. We show that a small set of memory tokens can be meta-trained so that $K\leq 5$ gradient descent steps reliably write task-relevant information into memory, enabling downstream tasks prediction with the original context removed.

3\. Gradient-based memory updates outperform forward-only writing. On associative retrieval, gradient-based updates store substantially more information in a fixed-size memory state than WRITE mechanisms that use only forward computation. Moreover, increasing the number of gradient updates consistently improves memory capacity, whereas repeating forward-only writes provides limited or inconsistent gains.

4\. Capacity scaling and transfer to natural language. We characterize how performance scales with the number of WRITE steps and context length on associative retrieval, and provide evidence of transfer to pretrained language models on natural language tasks (e.g., bAbI, SQuAD variants, language modeling) using the same task-agnostic reconstruction objective.

## 2 GradMem

![Refer to caption](https://arxiv.org/html/2603.13875v1/figs/GradMem_main_figure_v1.drawio.png)

Refer to caption

### 2.1 Problem Setup: Context Removal Setting

Many sequence modeling problems can be expressed by separating (i) external information that can be used, (ii) a task specification, and (iii) the desired output. We formalize this by representing each task instance as three sequences: *context* $C$, *query* $Q$, and *target* $Y$. Our goal is to enable prediction of $Y$ from $Q$ without direct access to $C$ at inference time, by first compressing $C$ into a small, fixed-size memory $\mathcal{M}$.

The context $C$ contains information that the model can use, but which may be long or expensive to repeatedly process (e.g., a document, a list of facts, a repository codebase, or previous dialogue). The query $Q$ specifies what should be done with this information (e.g., a question, a key for retrieval, an instruction, or a prompt). The target $Y$ is the sequence to be predicted.

Let $f_{\theta}$ be a causal language model parameterized by $\theta$. We use $f_{\theta}(Y\mid X)$ to denote the probability assigned by the model to an output sequence $Y$ conditioned on an input sequence $X$ under the standard autoregressive factorization. In the standard causal language modeling setting, the model conditions on the concatenation of context and query:

$$
f_{\theta}(Y\mid C,Q)\triangleq f_{\theta}\bigl(Y\mid[C;Q]\bigr).
$$

This approach requires repeatedly attending to the full context for each query at increased compute cost. We instead consider a memory-augmented view with a WRITE/READ phase decomposition. We introduce a memory representation $\mathcal{M}$ (e.g., KV-cache, or $m$ input vectors of dimension $d$, or recurrent state) and define two phases:

WRITE (encode context into memory). A *context encoder* produces a memory state from the context:

$$
\mathcal{M}=\mathcal{E}_{\theta}(C).
$$

READ (decode using memory and query). The model predicts the target from the memory and the query:

$$
f_{\theta}(Y\mid\mathcal{M},Q)\triangleq f_{\theta}\bigl(Y\mid[\mathcal{M};Q]\bigr).
$$

The central evaluation constraint we consider is the *context removal*: during READ phase, the model does not have direct access to the original context $C$. All information needed to predict $Y$ must pass through the memory state $\mathcal{M}$ computed in WRITE phase. Under this setting (Figure 2a), a method is considered successful if the memory $\mathcal{M}$ captures enough task-relevant information from $C$ to solve the task *using memory and query only*.

### 2.2 GradMem: Test-Time Gradient Descent Memory

GradMem *directly optimizes* the memory representation $\mathcal{M}$: for every example, it performs *test-time training* by running a few gradient descent steps. Crucially, parameters of the model are frozen; instead, only the memory states $\mathcal{M}$ are trained on the current context $C$, resulting in context-relevant representation for the subsequent READ phase.

Memory parameterization. We represent memory as $m$ vectors of dimension $d$, $\mathcal{M}\in\mathbb{R}^{m\times d}$. In a decoder-only transformer, these vectors are used as *prefix embeddings* prepended to the model input. GradMem maintains a meta-learned initialization $\mathcal{M}_{0}$ shared across examples, and produces an example-specific memory $\mathcal{M}_{K}$ after $K$ WRITE updates. While transformer stores KV-cache of size $\text{num\_layers}\times N\times d\times 2$ for context of length $N$, GradMem memory $\mathcal{M}$ is independent of context length and requires only $m$ $d$ -dim vectors.

GradMem is closely related to Test-Time Training (TTT) layers [^38], where an update is performed by gradient descent *online per token* (or small token mini-batches). The self-supervised objective in TTT is typically an $\ell_{2}$ reconstruction loss on the *layer input* $x_{i}$. TTT layers reconstruct layer inputs/activations, while GradMem reconstructs the context tokens, and does so once per context rather than at every layer and every token. Conceptually, instead of maintaining and updating a separate adaptive state in every layer, GradMem concentrates all test-time adaptation into a single memory state at the model input.

WRITE: optimize memory to encode the context. Given a context sequence $C=(t_{1},\dots,t_{N})$ of $N$ tokens, GradMem uses an explicit WRITE objective that is task-agnostic and depends only on the ability of the model to reconstruct the context when conditioned on memory:

$$
\mathcal{L}_{\text{write}}(\mathcal{M};C)\;=\;-\sum_{i=1}^{N}\log f_{\theta}\!\left(t_{i}\mid[\mathcal{M};t_{<i}]\right),
$$

i.e., an autoregressive cross-entropy loss over the context tokens computed while prepending the current memory $\mathcal{M}$. Intuitively, minimizing $\mathcal{L}_{\text{write}}$ forces memory $\mathcal{M}$ to encode information about $C$ that is *not* predictable from the prefix $t_{<i}$ alone (e.g., in high-entropy, novel or surprising contexts). In such setting, reducing the $\mathcal{L}_{\text{write}}$ loss requires the model to use the fixed-size prefix $\mathcal{M}$ for storing context content.

Starting from the meta-learned initialization $\mathcal{M}_{0}$, GradMem performs $K$ steps of gradient descent *on the memory parameters only*:

$$
\mathcal{M}_{k+1}\;=\;\mathcal{M}_{k}\;-\;\alpha\,\nabla_{\mathcal{M}_{k}}\mathcal{L}_{\text{write}}(\mathcal{M}_{k};C),
$$

where $\alpha$ is a WRITE-phase learning rate. We denote the final memory by $\hat{\mathcal{M}}\triangleq\mathcal{M}_{K}$, and define the context encoder as the composition of these optimization steps:

$$
\hat{\mathcal{M}}\;=\;\mathcal{E}_{\theta}(C)\;\triangleq\;\mathrm{GD}_{K}\!\left(\mathcal{M}_{0},\;\mathcal{L}_{\text{write}}(\cdot;C)\right).
$$

In practice, the update in Equation 5 can be stabilized with standard techniques such as gradient clipping. We also augmented it with (i) a learned linear layer applied to the memory before/after the updates and (ii) separate prediction heads for the WRITE and READ phases (we discuss these implementation variants in Appendix B).

READ: predict only from memory and query. In the READ phase, the model receives only $\hat{\mathcal{M}}$ and the query $Q$ and predicts the target:

$$
f_{\theta}(Y\mid\hat{\mathcal{M}},Q).
$$

The overall training objective is the downstream task loss (e.g., next-token cross-entropy on $Y$) computed in the READ phase under context removal:

$$
\mathcal{L}_{\text{task}}(\hat{\mathcal{M}},Q,Y)\;=\;-\log f_{\theta}\!\left(Y\mid\hat{\mathcal{M}},Q\right).\vskip-7.22743pt
$$

During training, we minimize $\mathcal{L}_{\text{task}}(\hat{\mathcal{M}},Q,Y)$ w.r.t. $\theta$ and $\mathcal{M}_{0}$ by differentiating through the WRITE phase optimization steps that produce $\hat{\mathcal{M}}$. In this way, the model learns to use few gradient descent optimization steps as operation to write useful information about current context $C$ into memory. Importantly, the WRITE objective $\mathcal{L}_{\text{write}}$ is not designed for any specific downstream task; it is a generic reconstruction loss used to form a memory state. GradMem training is summarized on the Figure 2c.

GradMem can be viewed through a meta-learning lens (Figure 2b, [^13]): the WRITE phase performs a small number of per-sample optimization steps on $\mathcal{M}$, while the model parameters $\theta$ and the shared initialization $\mathcal{M}_{0}$ are trained so that these few steps reliably produce useful memories. In this view, the WRITE updates in Equation 5 correspond to an *inner optimization (inner loop)* over per-sample memory variables, and the task loss in Equation 8 defines an *outer objective (outer loop)* used to learn $\theta$ and $\mathcal{M}_{0}$ across training examples. We backpropagate through the WRITE optimization (yielding second-order gradients).

A common way to implement the WRITE phase is with a forward-only context encoder that maps $C\mapsto\mathcal{M}$ in a single pass (e.g., an encoder network, or a recurrent/segment-level state update) [^22] [^20] [^7] [^26] [^14] [^32] [^9] [^2]. Such encoders must learn to produce a useful memory *without any per-sample feedback* at inference time: once $\mathcal{M}$ is emitted, the write operation cannot verify whether the context was encoded well enough, nor correct mistakes made during compression. GradMem instead treats memory formation as an explicit optimization problem. By defining a task-agnostic reconstruction objective $\mathcal{L}_{\text{write}}(\mathcal{M};C)$ and taking a small number of gradient descent steps on $\mathcal{M}$, GradMem obtains a direct signal of *how well* the current memory explains the context and can iteratively refine $\mathcal{M}$ to reduce this loss. This iterative, loss-driven write mechanism is more expressive than a fixed forward computation: it can allocate compute to the specific context at hand, correct earlier write errors, and trade additional test-time compute (more gradient steps) for improved memory capacity.

## 3 Experiments and Results

### 3.1 Datasets

All experiments follow the context removal setting (Section 2.1) with two input segments for each of READ and WRITE phases. Each example is decomposed into a *context* $C$, a *query* $Q$, and a *target* $Y$.

Associative KV-retrieval. Associative retrieval is our main synthetic and controllable benchmark for comparing different memory mechanisms. Each example contains $N$ key–value pairs $(k_{i},v_{i})$, where each key and each value consists of 2 symbols from a 62-character vocabulary. The context is a sequence of key–value pairs with special delimiters:

$$
C=\texttt{!}k_{1}\texttt{:}v_{1}\texttt{!!}k_{2}\texttt{:}v_{2}\texttt{!}\cdots\texttt{!}k_{N}\texttt{:}v_{N}\texttt{!}
$$

The query $Q$ asks for the value associated with key $k_{j}$ and the target $Y$ is the corresponding value:

$$
Q=\texttt{?!}k_{j}\texttt{:},\qquad Y=v_{j}.
$$

The model can answer correctly only if the mapping from keys to values is written into memory during WRITE phase.

bAbI [^43] is a question answering benchmark that tests reasoning over stories (e.g., tracking entities, locations, and interactions across multiple sentences). We use tasks QA1–QA5, which progressively increase the amount of multi-sentence composition required: QA1–QA3 require combining one, two, or three supporting facts, while QA4–QA5 require reasoning over two-argument and three-argument relations expressed across sentences. Each example consists of a story (a sequence of sentences) followed by a question and a short answer. We define the context $C$ as the story text, the query $Q$ as the question, and the target $Y$ as the ground truth answer string.

SQuAD [^34] is an extractive question answering dataset, where each example consists of a paragraph, a question, and an answer span within the paragraph. We construct a short context variant (Short SQuAD) to control context length and isolate whether GradMem’s writing mechanism transfers to natural language by extracting sentences containing the annotated answer span only. We define the context $C$ as the passage with the answer span, the query $Q$ as the question, and the target $Y$ as the answer text.

Language Modeling task evaluates next-token prediction ability of the model, where conditioning on a preceding context typically reduces the cross-entropy loss (perplexity) on subsequent tokens. We use WikiText-103 [^27] (wikitext-103-raw-v1) and form examples by taking contiguous 256-token chunks. For segmented models (RMT, ARMT, GradMem), we split each chunk into two 128-token segments: the first segment is the context $C$ and the second is the target $Y$. There is no separate query in this setup ($Q=\emptyset$): after writing $C$ into memory, the model must predict the continuation $Y$ from memory alone. We report average cross-entropy on the last 128 tokens of each segment (segment 2, target). For non-segmented models, we compute the same metric by averaging token-level losses over positions 128–255 of the chunk, enabling a position-matched comparison to the segmented setting.

### 3.2 Baselines

Full-Attention Transformer This trivial baseline presents an upper bound of what can be memorized: the memory of a Transformer is uncompressed and contains all input hidden states. For associative retrieval experiments we train a small 4-layer Llama model [^42], and for downstream tasks we finetune pretrained GPT-2 [^31] and Pythia [^3] models.

Mamba We include pretrained Mamba-2 model (130M) as a strong state-space baseline for sequence modeling [^11]. Mamba replaces quadratic self-attention with a selective state-space model (SSM) update, yielding linear-time processing in sequence length while retaining strong performance via input-dependent selection/gating. In our experiments, Mamba provides a natural comparison point: it maintains an internal recurrent state that summarizes the prefix and can be reused when processing subsequent tokens, without requiring an explicit attention cache.

RMT The Recurrent Memory Transformer (RMT) [^5] is used as the straightforward forward-only memory write baseline. RMT splits the context into segments and iteratively processes them one after another with an LLM. It passes special memory vectors alongside segment tokens in order to memorize important information and reuse it. We use 2-segment version of RMT: the first segment contains the context, and the second one starts with the query and generate the answer. In this setting RMT is fully equivalent to GradMem except for the memory write operation that is performed by the forward pass. For associative retrieval experiments we wrap the 4-layer llama model with hidden\_size 128 and 4 attention heads, while for our natural language experiments we wrap the GPT-2 (124M) model. The same applies to ARMT model. For more training details see the Appendix B.

Table 1: KV-retrieval: gradient-based WRITE outperforms forward-only WRITE. Exact match retrieval accuracy (mean $\pm$ std over 3 runs) for predicting a 2-token value from a 2-token key as the number of key–value pairs increases. Upper bound: a standard Transformer with full KV-cache retains all past activations. Per-layer memory baselines: Mamba and ARMT maintain state in every layer. Same memory state: RMT (forward-only write) and GradMem both use the same base architecture and the same memory size of $m{=}8$ vectors, but different WRITE rule. Repeating the forward-only WRITE by re-reading the same context segment (x2–x5) gives limited or inconsistent gains, whereas additional gradient-based WRITE steps (x2, x5) consistently improve retrieval at larger numbers of pairs.

<table><tbody><tr><th></th><td colspan="6">Number of KV-pairs</td></tr><tr><th>Model</th><td>4</td><td>8</td><td>16</td><td>32</td><td>64</td><td>96</td></tr><tr><th>Transformer: <math><semantics><mi>ℳ</mi> <annotation>\mathcal{M}</annotation></semantics></math> =KV-cache</th><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.8 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.8 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.3</td><td>96.5 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 2.9</td><td>98.8 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td></tr><tr><th>Mamba: <math><semantics><mi>ℳ</mi> <annotation>\mathcal{M}</annotation></semantics></math> =per-layer recurrent state</th><td>99.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>98.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.4</td><td>98.7 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.2</td><td>90.2 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 10.2</td><td>95.2 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>92.2 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.4</td></tr><tr><th>ARMT: <math><semantics><mi>ℳ</mi> <annotation>\mathcal{M}</annotation></semantics></math> =per-layer associative matrix</th><td>99.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.3</td><td>98.5 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.5</td><td>97.4 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.3</td><td>54.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 2.1</td><td>22.6 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 3.9</td><td>15.2 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.2</td></tr><tr><th>Forward-only write (RMT): <math><semantics><mi>ℳ</mi> <annotation>\mathcal{M}</annotation></semantics></math> =8 mem vectors</th><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>45.5 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.2</td><td>44.3 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>19.3 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 3.1</td><td>12.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.2</td></tr><tr><th>x2 memory updates</th><td></td><td></td><td>69.6 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 28.1</td><td>18.7 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 3.4</td><td>–</td><td>–</td></tr><tr><th>x3 memory updates</th><td></td><td></td><td>60.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 42.0</td><td>38.1 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>–</td><td>–</td></tr><tr><th>x4 memory updates</th><td></td><td></td><td>–</td><td>31.5 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>–</td><td>–</td></tr><tr><th>x5 memory updates</th><td></td><td></td><td>–</td><td>37.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 1.7</td><td>–</td><td>–</td></tr><tr><th>GradMem: <math><semantics><mi>ℳ</mi> <annotation>\mathcal{M}</annotation></semantics></math> =8 mem vectors</th><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.7 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>96.3 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.9</td><td>86.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.5</td><td>58.6 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.7</td><td>32.6 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td></tr><tr><th>x2 memory updates</th><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.6 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>98.3 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>72.8 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.5</td><td>34.2 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td></tr><tr><th>x5 memory updates</th><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>99.1 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.3</td><td>88.4 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 2.3</td></tr><tr><th>x1, w/o meta-learning</th><td></td><td>12.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 8.1</td><td>3.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.6</td><td>–</td><td>–</td><td>–</td></tr><tr><th>x2, w/o meta-learning</th><td></td><td>46.7 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 8.3</td><td>4.2 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.7</td><td>–</td><td>–</td><td>–</td></tr></tbody></table>

ARMT Associative Recurrent Memory Transformer [^36] accumulates information segment by segment into a small set of memory tokens and stores them in an associative matrix with a DeltaNet-style update [^44]. ARMT performs a forward-only WRITE: as it processes a segment, it produces memory tokens and writes them into the associative memory on each layer, which is then queried over future segments. In this paper we use a two-segment setup as for RMT. This makes ARMT a strong baseline for assessing whether *gradient-based* writing (GradMem) into memory tokens stores more task-relevant information than *forward-only* writing into per-layer associative matrices.

### 3.3 Results on KV-retrieval task

We start with associative KV-retrieval and organize compared methods into four groups (Table 1). First, a standard Transformer that attends to the full context serves as an upper bound: it is not a compressive-memory method, since it retains all past activations (KV-cache). Second, sequence models with *per-layer* memory (e.g., Mamba and ARMT) implement a different memory interface: their state is distributed across layers and is therefore not directly matched to a single model-level memory state. We include them as strong reference points, but focus our main comparison on methods that write into the same memory parameterization (RMT, GradMem). Third, we consider forward-only writing into a memory (RMT), which updates memory purely through forward computation. Finally, we evaluate GradMem, which uses the same base architecture and the same memory size ($m{=}8$ memory vectors), but differs in the WRITE rule: GradMem updates memory tokens by a small number of test-time gradient steps. All models we compare here have 4 layers and 128-dim (for transformer-based models we use 4 attention heads). Unless stated otherwise, we use the same number of KV-pairs and memory updates for training and inference (RMT, GradMem). Once a method’s accuracy dropped sharply at smaller $N$, we did not scale it further (entries marked with “–” in the Table 1).

Gradient-based WRITE improves performance and scales with more steps. Figure 3 compares forward-only writing (RMT) with GradMem under the same memory size, and varying the number of WRITE updates. We can see three trends. (i) Gradient-based updates of a model-level memory state are effective: by directly optimizing memory tokens per example at test-time, GradMem writes context information into a compact memory and attains high retrieval accuracy compared to RMT with forward-only memory update. (ii) Even a single gradient-based WRITE step ($K{=}1$) substantially outperforms a forward-only write at the same memory size. (iii) Allocating more WRITE compute to gradient updates further improves performance: increasing to $K{=}5$ enables accurate retrieval at much larger numbers of key–value pairs.

![Refer to caption](https://arxiv.org/html/2603.13875v1/x1.png)

Refer to caption

Repeated forward writes provide limited gains compared to gradient updates. Table 1 reports the full set of results on KV-retrieval. Among methods that write into a *single memory state* of 8 vectors, GradMem consistently achieves higher accuracy than forward-only writing (RMT). Moreover, repeating the forward-only WRITE by *re-reading the same context segment multiple times* yields weak or inconsistent improvements, whereas additional gradient-based WRITE steps reliably increase performance. These results support conclusion that gradient-based updates provide a more expressive write operation than forward-only computation, and additional gradient steps offer a way to trade test-time compute for better memory quality. We also found that first-order approximations (Table 1, w/o meta-learning) were not sufficient to reach strong performance, so we use the second-order variant (MAML, [^13]) in further experiments.

Table 2: GradMem remains competitive on downstream language tasks. Results on bAbI (QA1–5), SQuAD (short) shown as Exact Match (EM) in %, and language modeling (cross-entropy on token ranges). We use 32 mem tokens for SQuAD and LM, and 8 on bAbI. We report mean $\pm$ std over 3 runs.

<table><tbody><tr><td></td><td colspan="5">bAbI (EM <math><semantics><mo>↑</mo> <annotation>\uparrow</annotation></semantics></math>)</td><td>SQuAD (EM <math><semantics><mo>↑</mo> <annotation>\uparrow</annotation></semantics></math>)</td><td>LM (CE <math><semantics><mo>↓</mo> <annotation>\downarrow</annotation></semantics></math>)</td></tr><tr><td></td><td>QA1</td><td>QA2</td><td>QA3</td><td>QA4</td><td>QA5</td><td>short</td><td>128–255</td></tr><tr><td>Input length (tokens)</td><td><math><semantics><mo>∼</mo> <annotation>\sim</annotation></semantics></math> 40</td><td><math><semantics><mo>∼</mo> <annotation>\sim</annotation></semantics></math> 100</td><td><math><semantics><mo>∼</mo> <annotation>\sim</annotation></semantics></math> 300</td><td><math><semantics><mo>∼</mo> <annotation>\sim</annotation></semantics></math> 20</td><td><math><semantics><mo>∼</mo> <annotation>\sim</annotation></semantics></math> 20</td><td><math><semantics><mo>∼</mo> <annotation>\sim</annotation></semantics></math> 40</td><td>256</td></tr><tr><td colspan="8">Full context models (upper bound)</td></tr><tr><td>GPT-2-124m</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.8 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.4 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>64.2 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.3</td><td>2.72 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.00</td></tr><tr><td>Limit context to 128 tokens</td><td></td><td></td><td></td><td></td><td></td><td></td><td>3.20 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.00</td></tr><tr><td>Pythia-160m</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.7 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>95.5 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 2.7</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>48.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.4</td><td>2.84 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.05</td></tr><tr><td colspan="8">Recurrent models</td></tr><tr><td>Mamba-130m-hf</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>96.7 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.2</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.7 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.09</td><td>63.3 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.2</td><td>2.69 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.00</td></tr><tr><td>RMT (GPT-2)</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>93.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>87.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.4</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>93.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 6.9</td><td>42.6 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.3</td><td>2.91 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.00</td></tr><tr><td>ARMT (GPT-2)</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>93.8 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.6</td><td>92.3 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.8</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>98.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>39.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.2</td><td>2.85 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.00</td></tr><tr><td>GradMem (GPT-2, <math><semantics><mrow><mi>K</mi> <mo>=</mo> <mn>1</mn></mrow> <annotation>K=1</annotation></semantics></math>)</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>94.2 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.4</td><td>80.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.2</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.2 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>38.1 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>2.92 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.00</td></tr><tr><td>GradMem (GPT-2, increased <math><semantics><mi>K</mi> <annotation>K</annotation></semantics></math>)</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>93.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.5</td><td>79.3 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.2</td><td>100.0 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.0</td><td>99.2 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.1</td><td>54.9 <math><semantics><mo>±</mo> <annotation>\pm</annotation></semantics></math> 0.4</td><td>–</td></tr></tbody></table>

![Refer to caption](https://arxiv.org/html/2603.13875v1/x2.png)

Figure 4: More gradient steps at test-time lead to better performance without fine-tuning. (a) Results for K train K\_{\\text{train}} values of GradMem setups that could not achieve 99% exact match during fine-tuning. Red dashed lines denote the beginning of extrapolation. (b) Downstream task quality (Exact Match) correlates with WRITE objective in inner loop (reconstruction). Notably, GradMem reconstructs values better than keys, despite the WRITE objective treating them equally. Results on 96-pair KV-retrieval.

### 3.4 Scaling inference compute with more WRITE iterations

In GradMem, we find that increasing the number of WRITE iterations at evaluation time provides a substantial accuracy lift on KV-retrieval task. Figure 4(a) shows a trend across settings with different numbers of key–value pairs where for some models extrapolating from the training-time value $K_{\text{train}}$ to larger $K_{\text{eval}}$ yields improvements in exact match. Importantly, these gains are obtained with *fixed* model parameters and therefore reflect the effect of allocating additional WRITE refinement steps at inference. We connect this effect to better convergence on the inner task. By decomposing the inner loss into loss on key or value tokens (Figure 4(b)) we observe that inner loss correlates strongly with exact match (more results are in Appendix E).

This behavior induces a practical compute–accuracy trade-off and a way to scale computation beyond training without re-optimization. Larger $K_{\text{train}}$ is expensive under our meta-learning objective, and it becomes increasingly cumbersome to fine-tune models that must backpropagate through long WRITE trajectories—particularly when longer contexts and larger $K$ are required. In contrast, increasing $K_{\text{eval}}$ only at inference time shifts this cost to evaluation, enabling higher accuracy without any fine-tuning while preserving a small- $K$ training pipeline.

### 3.5 GradMem with pre-trained models on NLP tasks

We next evaluate GradMem with pretrained language models on natural language benchmarks to test whether the gradient-based WRITE method and reconstruction-based WRITE objective can produce useful memory states outside the controlled KV-retrieval setting.

GradMem remains competitive on downstream language tasks and scales well with $K$. The NLP task evaluation spans across three task groups: bAbI reasoning benchmark, single-sentence SQuAD QA and the LM task on the Wikitext dataset (see Table 2). The bAbI evaluation exhibits significant variability of scores on different reasoning tasks. The context size of QA1, QA4 and QA5 generally does not exceed 40 tokens, and all evaluated methods solve them well, matching the upper bound Transformer performance. QA2 and QA3 require to memorize more facts and detect them among more noise, which poses a challenge to compressive models. Mamba performs the best among recurrent models since the memory operations are already learned during extensive pretraining, unlike GradMem and recurrent Transformers. ARMT follows short, its strong performance on QA2 and QA3 can be attributed to the largest memory state size in its class and added parameters in associative layers. GradMem matches or outperforms forward-only RMT on all QA tasks except QA3 with the highest information density, and performance improves with larger $K$.

To reduce the effect of long, distractor-heavy contexts on performance we evaluate text understanding on Short SQuAD, where the context is restricted to the answer-containing sentence; for dataset details refer to Section 3.1 and Appendix B. GradMem with $K=1$ outperforms forward-only RMT, and by scaling $K$ achieves the best performance among recurrent Transformers and even the non-compressive Pythia model, GPT-2 still remains the upper bound. On the Wikitext language modeling task, recurrent models are required to compress diverse information from context to correctly predict the next token of the current segment. All compressive Transformers learn to use memory and noticeably outperform GPT-2 with context size 128; full-context GPT-2 remains the upper bound. ARMT with more capacious memory takes the lead, followed by RMT and GradMem.

## 4 Discussion and Conclusions

Compute and memory overhead. A core cost of GradMem is that it backpropagates through the WRITE inner loop and, during training, differentiates through the unrolled optimization steps. This increases both compute and GPU memory usage compared to forward-only writers and standard full-context training, since the computational graph must be retained across WRITE steps. In practice, this also constrains the choice of attention implementation: common high-performance kernels such as FlashAttention [^10] and PyTorch SDPA are designed for efficient first-order backpropagation, but do not support the higher-order differentiation required by GradMem training (i.e., taking gradients through the WRITE updates). As a result, we implement a custom double-backward that is both *more memory-efficient* and *faster* than a naive eager implementation, described in Appendix C. More broadly, the meta-learning literature provides methods that are more computationally efficient first-order/implicit approaches such as iMAML [^33] and Reptile [^29], which could further improve the efficiency of GradMem meta-learning training.

When test-time WRITE compute is worthwhile. At inference time, the WRITE phase with $K$ gradient steps is more expensive than a single forward pass over the context. Nevertheless, in applications where the same context is reused across multiple queries, the additional WRITE compute can be amortized: after encoding a long context $C$ into a compact memory $\mathcal{M}$ once, subsequent READ computations attend only over $[\mathcal{M};Q]$ rather than $[C;Q]$. When $|C|\gg|\mathcal{M}|$, this can reduce per-query computation and memory, making gradient-based writing a more compute-reasonable option. We analyze this in Appendix D.

Write objectives beyond reconstruction. We use a simple token-level reconstruction objective for $\mathcal{L}_{\text{write}}$, which is task-agnostic and easy to apply across domains. Despite its simplicity, it is already sufficient to yield strong gains on associative retrieval and to transfer to natural language tasks. At the same time, reconstruction is unlikely to be optimal for all downstream tasks. Related work on TTT layers [^38] already explored learning self-supervised reconstruction objectives via input transformations, but considered relatively restricted (e.g., linear) transformations. A promising direction is to learn WRITE objectives that better preserve task-relevant information, while retaining the key constraint that WRITE remains self-supervised on the available context.

Conclusions. We introduced GradMem, a WRITE/READ memory mechanism where a model *writes* a context into a small set of memory tokens by running a few steps of test-time gradient descent while keeping model weights fixed. Across controlled KV-retrieval experiments, this direct per-sample optimization gives a stronger WRITE rule than forward-only updates under the same architecture and memory size. Increasing the number of gradient WRITE steps reliably improves retrieval as the number of stored key–value pairs grows, unlike repeating forward WRITE passes. We further showed that the same reconstruction-based WRITE objective can be applied to pretrained language models in natural language tasks (bAbI, short SQuAD, language modeling). Overall, our results establish gradient-based test-time optimization of a model-level memory state as a promising alternative to forward-only memory writers, and motivate future work on more efficient training, scaling to longer contexts and larger models, and better self-supervised WRITE objectives.

## References

## Appendix A Related Work

Long-context modeling and efficient attention. A large body of work aims to extend the effective context length of transformers by changing the architecture or attention mechanism, thereby reducing the need for an explicit external memory. Compressive Transformers [^32] augment the standard recurrent memory with a compressed memory stream, retaining a lossy summary of distant past activations. Recurrent memory transformers such as RMT [^5] and their extensions [^9] segment long inputs into chunks and pass a trainable state between segments, enabling processing of sequences far beyond the native context window. Other approaches leverage associative memories and efficient attention mechanisms to scale to long contexts [^36] [^35].

Context compression and reusable representations. In NLP, compression has long been studied for constructing compact sentence- or document-level representations [^22] [^20] [^7], often via autoencoding pipelines [^4] [^28]. In the context of large language models, input compression is used both to reduce the quadratic cost of self-attention and to create persistent representations that can be stored or reused. For compressed data storage in LLMs, one can use dense continuous vectors or memory tokens [^6], but also LoRA parameters [^17], intermediate hidden states [^26], associative memories [^36] [^35], or the KV-cache directly [^8] [^19]. Some works focus on compressing only part of the input, such as prompts [^23] [^25] or in-context examples [^15] [^12], while others iteratively compress the entire sequence by splitting it into smaller chunks and processing them sequentially [^32] [^5] [^9] [^30]. Approaches such as task vectors [^18] and Cartridges [^12] compress information from multiple task samples into persistent vectors that can be reused to steer the model on those tasks, amortizing the cost of processing across many downstream queries.

A straightforward way to compress information into memory is to use the model itself as an encoder, e.g., via segment-level memory tokens in RMT-style architectures [^5] [^9] [^14]. ICAE [^15] and SelfCP [^14] train base LLMs to compress context using autoencoding-style objectives and other losses targeted at text understanding [^26].

Fast weights, delta rules, and associative memory offer another path to context-dependent memory. Early work used fast weights as high-level controllers [^37] or as associative storage over past representations [^16]. Recent formulations adapt fast weights to modern architectures, often in the form of associative memories or gated recurrent states that implement a learned approximation to a delta rule. Associative-memory transformers [^36] and modern Hopfield networks [^35] can be interpreted as storing and retrieving key–value pairs in a continuous memory, with the write operation implemented purely via forward computation. These mechanisms generally rely on *forward-only* update rules that are applied once per token or timestep, without an explicit per-example optimization objective that is iteratively minimized for a given context.

Test-time training. GradMem is closely related to test-time training (TTT) methods that perform gradient-based adaptation during inference. TTT layers [^38] introduce lightweight, sequence-dependent state that is updated online using self-supervised objectives, typically by reconstructing layer activations at each timestep. A related line of work uses optimization as a way to compress large amounts of data into a fixed number of weights or embeddings. Early formulations viewed fast weights as auxiliary parameters that are updated via gradient-based rules [^37]. More recent work proposes unsupervised objectives for compressing context via test-time optimization [^38], and adapts these ideas to transformers by introducing additional memory modules, routing mechanisms [^2] [^1] and end-to-end training objectives [^39]. The study of [^21] shows that, by optimizing a simple reconstruction objective with gradient descent, it is possible to achieve extremely high compression ratios (up to $\sim$ 1500 $\times$) into a single vector; however, this requires up to tens of thousands of gradient updates and produces representations that are primarily useful for text reconstruction rather than downstream tasks.

Table 3: Comparison of GradMem to test-time training (TTT) layers. Both approaches perform learning at inference time via a self-supervised loss, but differ in *what* is adapted (layer parameters vs. memory tokens), *when* it is adapted (token-level online vs. context-level WRITE), and *what* the self-supervised signal reconstructs (layer inputs/activations vs. context tokens).

|  | [^38] | GradMem (ours) |
| --- | --- | --- |
| Usage pattern | Sequence-modeling layer: updates state online while processing tokens | Explicit two-phase WRITE/READ setting |
| Inner-loop input | Token $x_{t}$ (or token mini-batches) | Whole context segment $C$ (WRITE once per context) |
| Test-time parameters | Layer-specific parameters $W_{t}$ (updated from the layer’s inputs/activations), *per layer* | Prefix memory tokens $\mathcal{M}_{k}\in\mathbb{R}^{m\times d}$ (single memory state), *per model* |
| Self-supervised loss | Activation/input reconstruction, e.g. $\ell(W;x_{t})=\\|f(\tilde{x}_{t};W)-x_{t}\\|_{2}^{2}$ (or learned multi-view projections) | Context reconstruction $\mathcal{L}_{\text{write}}(\mathcal{M};C)$ |
| Outer-loop objective | Next-token prediction (LM training) | Downstream task loss with $C$ removed at READ |
| Outer-loop parameters | Model params $\theta$ + reconstruction task/view params | Model params $\theta$ + memory init $\mathcal{M}_{0}$ (optional: memory projections / control tokens) |

Comparison to our approach Our work is distinguished from prior memory and TTT approaches along three main axes. First, GradMem uses a *single input-level memory state* that is written once per context, rather than per-layer or per-token states updated online. Second, the WRITE mechanism is *explicitly optimization-based*: memory tokens are treated as parameters and updated by gradient descent on a model-level reconstruction loss, rather than via a learned forward-only update rule. Third, we target the *few-step* regime, meta-training the base model and memory initialization so that a small number of gradient steps ($K\leq 5$) suffices for effective writing, in contrast to hundreds or thousands of iterations used in prior embedding-optimization work.

## Appendix B Hyperparameters and training details

We provide the source code as supplementary material with the submission.

Table 4 summarizes the memory configuration and training initialization used across tasks. We keep the memory embedding dimension fixed to $d_{\text{mem}}=64$ in all ARMT experiments, and vary only the number of memory tokens depending on the task. Unless otherwise noted, “from pretrained model” means initializing the base LM weights from a standard pretrained checkpoint (e.g., GPT-2 (124M) / Pythia (160M) / Mamba (130M)) and then fine-tuning with the corresponding memory mechanism.

For associative retrieval (AR), we train models from scratch and use a curriculum over context lengths: training for a larger number of key–value pairs is initialized from the final checkpoint obtained at a smaller length. For GradMem, the curriculum starts from 32 key–value pairs, as we found that the model achieves perfect retrieval on smaller contexts even without curriculum training. For bAbI, most models are fine-tuned from pretrained checkpoints; the exception is RMT, which we found to be sensitive to initialization and therefore train it starting from a GPT-2 (124M) checkpoint already fine-tuned on bAbI. For language modeling, all models are fine-tuned from pretrained checkpoints; additionally, we report a variant where GradMem is initialized from an RMT checkpoint trained on the same language modeling objective.

For GradMem, we tune the inner learning rate $\alpha\in[0.01,\,10]$. We observe that model performance does not depend strongly on its value, until it is in a reasonable range and found $\alpha=0.4$ to be a relatively strong default value for experiments on NLP tasks. For the KV-retrieval task, we still perform small search over $\alpha$ and report the scores for the best setup with every $K$, but we do not tune $\alpha$ exhaustively in every experiment. We also augment both GradMem and RMT with (i) a learned linear layer applied to the memory before/after the updates and (ii) separate prediction heads (output embeddings) for the WRITE and READ phases. These augmentations are used in all experiments unless stated otherwise. We backpropagate through the unrolled WRITE optimization by default; we explicitly note experiments that disable this meta-learning path (e.g., Table 1, w/o meta-learning).

Table 4: Task-specific memory hyperparameters and training initialization. In experiments ARMT uses $d_{\text{armt\_mem}}=64$ for associative matrix memory. Memory tokens in RMT, ARMT, and GradMem have the same dimension as models input embeddings.

| Task | num\_mem\_tokens |  | Training technique |
| --- | --- | --- | --- |
| Associative retrieval (AR) | 8 |  | Curriculum learning by number of KV-pairs (4, 8, 16, …);from randomly init model (4 layers, 4 heads, 128 hid) |
| Short SQuAD | 32 |  | From pretrained model;GradMem trained without learned memory projection |
| bAbI | 8 |  | From pretrained model |
| Language modeling | 32 |  | From pretrained model (all models);GradMem trained without separate WRITE head; |

## Appendix C Accelerating double backwards through attention.

In our meta-learning setting, the dominant computational challenge is that the inner-loop optimization requires backwards-over-backwards through attention, which substantially increases both runtime and GPU memory compared to standard training. We implement an efficient double-backward for attention that significantly reduces this overhead on longer sequences: for $L{=}1024$ tokens, backward time drops from $\sim$ 1000 ms to $\sim$ 600 ms and peak GPU memory from $\sim$ 60 GB to $\sim$ 30 GB in our setup. This improvement is critical for scaling GradMem to longer contexts and larger WRITE step counts. In order to accelerate our experiments, we evaluated a few approaches to make the double backward of GradMem more efficient:

- Eager: a baseline which fully relies on PyTorch’s autograd for first- and second-order differentiation.
- Fast forward $\rightarrow$ manual backward: the forward pass is computed using PyTorch’s SDPA kernel. The first-order backward is written analytically, and the second-order derivatives are obtained by differentiating the analytical backward with autograd.
- Fast forward $\rightarrow$ autograd: the forward pass also uses SDPA, but constructs the backward by recomputing the attention forward inside the backward and letting autograd differentiate it, enabling second-order derivatives without storing forward intermediates at the cost of recomputation.
- Manual HVP: a fully analytical implementation of forward, backward, and double backward in pure PyTorch.
- Flash HVP: fused forward and backward kernels, combined with an analytical double backward.

We compare the speed of backward methods in Figure 5. While on shorter sequences eager attention is the most practical solution, longer contexts benefit from our optimizations, with Fast forward $\rightarrow$ autograd being the fastest and Manual HVP by far the most memory-efficient. In general, Flash HVP is the most balanced approach, coming in second in both speed and memory requirements.

![Refer to caption](https://arxiv.org/html/2603.13875v1/x4.png)

Figure 5: Comparison of speed and memory consumption for attention backwards-over-backwards in GradMem. These results were obtained using an A100 GPU, with GPT-2 as the base model for GradMem with 8 memory tokens, a query size of 24, 1 inner SGD step and a batch size of 16.

## Appendix D Computational analysis: when GradMem is compute-efficient

GradMem introduces additional WRITE-time compute (test-time optimization) in exchange for cheaper READ-time inference, since subsequent queries attend only over a short memory prefix rather than the full context. Here we characterize when this trade-off is favorable.

Let $c$ be the context length, $q$ the query length with $q\ll c$ is negligible, $m$ the number of memory tokens, $N$ the number of queries asked about the *same* context, and $K$ the number of gradient updates in the WRITE phase. We use $R$ to denote the ratio between the cost of one memory update step and the cost of one forward pass over the context. We compare (i) standard full-context transformer inference that reuses the context for each query, and (ii) GradMem, which pays a WRITE cost once and then answers queries using only memory.

For a transformer-style model, self-attention over a sequence of length $L$ costs $\mathcal{O}(L^{2})$, and cross-attention from a query of length $q$ to a context of length $c$ costs $\mathcal{O}(cq)$. If we cache the context representations, standard transformer inference incurs a one-time cost to process $C$, plus a per-query cross-attention cost:

$$
T_{\text{full}}\approx c^{2}+cqN.
$$

For GradMem, the WRITE phase runs $K$ gradient descent steps on the context. Each step costs $R$ times a forward pass over $C$, giving a WRITE cost of $Rc^{2}K$. At READ time, we process $m$ memory tokens once and each query attends only over the memory tokens and query $q$ tokens. In total giving following cost of both WRITE and multiple reads:

$$
T_{\text{GradMem}}\approx R(c+m)^{2}K+m^{2}+mqN.
$$

Break-even condition. GradMem is compute-efficient when the total cost is lower than full-context inference: $T_{\text{full}}/T_{\text{GradMem}}>1.$ Equivalently,

$$
\displaystyle N\;
$$
$$
\displaystyle>\;\frac{c^{2}(RK-1)+(1+RK)m^{2}+2cmRK}{q(c-m)}.
$$

In the regime where $q$ is treated as a small constant factor and, this reduces to the simpler heuristic threshold $N\gtrsim\bigl(c(RK-1)\bigr)/q,$ which matches the form used in our empirical discussion.

Thus, when the same context is reused for more than the threshold in Equation 11 and $c>m$, GradMem yields lower total compute than repeatedly answering from the full context (Figure 6). In practice, the regime $c\gg m$ and large $N$ (many queries per context) is the most favorable for GradMem, since the amortized savings in READ grow linearly with $N$ while the WRITE cost is paid only once per context.

![Refer to caption](https://arxiv.org/html/2603.13875v1/x7.png)

Figure 6: Break-even number of queries for GradMem compute efficiency. The curve shows the minimum number of queries per context N required for GradMem to use less total compute than full-context inference with cached context representations. Estimates use query length q = 128 q{=}128 and memory size m 32 m{=}32; points above the curve favor GradMem.

Real-use READ/WRITE latency. On a GPU, models can be bound by factors other than theoretical model complexity, i.e. memory bandwidth or kernel efficiency. In order to control for those factors, we evaluate GradMem against GPT-2 (124M) and Mamba-130m baselines in terms of measured READ and WRITE operation latencies. We treat building the KV-cache in GPT-2 and the recurrent state in Mamba as WRITE operations, while subsequent forward passes using the caches correspond to READ operations.

Figure 7 reports the total latency of a WRITE phase followed by $N$ READ (subsequent queries to the same context) operations from the cached representations for contexts of length 64, 256 and 1024 tokens. GradMem has a large initial cost due to the complexity of gradient-based WRITE, which appears as a higher initial offset. However, the complexity of repeating the READ phase is smaller for GradMem compared to other models, since the underlying transformer attends only to a small set of memory tokens instead of the entire context. In contrast, GPT-2 must repeatedly process its KV-cache, resulting in a higher latency per query.

As the number of READ operations rises, the initial overhead of GradMem WRITE operation becomes less pronounced. GradMem consistently outperforms Mamba across all evaluated context lengths, and breaks even with GPT-2 after approximately 64 READ phases for the same context (for context size 256 and 1024).

![Refer to caption](https://arxiv.org/html/2603.13875v1/x8.png)

Figure 7: GradMem is competitive in latency when the same context is reused across many READ operations. Results obtained on an A100 GPU. WRITE+READ latency is shown for contexts of length 64 (a), 256 (b), and 1024 (c) tokens. The query length is 24 tokens and the batch size is 16. GradMem uses GPT-2 as the base model with K = 1 K=1. For GPT-2 and Mamba-130M, the WRITE operation corresponds to building the KV-cache and the recurrent state, respectively. The y-axis uses a logarithmic scale; the absolute latency difference (ms) increases with the number of queries per context.

## Appendix E Relation between Exact Match and inner loss

To better understand why extrapolating the number of WRITE iterations improves downstream performance, we analyze the behavior of the inner objective during the WRITE phase. Figure 8 shows that increasing $K$ yields a reduction in the inner loss, indicating that additional WRITE iterations produce a more accurate memory state under the reconstruction objective. Moreover, the reduction in inner loss correlates with improvements in Exact Match when evaluating with larger $K_{\text{eval}}$. These results provide evidence that improved context retention—as measured by the inner objective—translates into better task-level accuracy in the context-removal setting.

We further analyze *what* information is being retained by decomposing the reconstruction loss over context tokens into contributions from key tokens and value tokens in the associative retrieval data. Notably, the loss on key tokens remains comparatively stable across WRITE iterations, while the loss on value tokens decreases as $K_{\text{eval}}$ increases. This behavior suggests that the learned memory is *selective*: rather than attempting to store the full context verbatim, the model primarily refines those parts of the representation that are useful for answering queries, i.e., the values that must be produced at READ time. In this regime, keys function mainly as retrieval cues, so improving value reconstruction only is sufficient for increasing Exact Match. This supports two conclusions: (i) better memory fidelity under the inner objective leads to higher downstream accuracy, and (ii) the memory mechanism learns to allocate representational capacity toward answer-relevant content, producing a structured compression in which values are retained more precisely than the keys that index them.

![Refer to caption](https://arxiv.org/html/2603.13875v1/x11.png)

Figure 8: On KV-retrieval, GradMem learns to decrease inner loss on value tokens only. This figure was obtained for GradMem fine-tuned on 64-pair KV-retrieval with K train = 2 K\_{\\text{train}}=2.

[^1]: Atlas: learning to optimally memorize the context at test time. arXiv preprint arXiv:2505.23735. Cited by: Appendix A.

[^2]: Titans: learning to memorize at test time. arXiv preprint arXiv:2501.00663. Cited by: Appendix A, §2.2.

[^3]: Pythia: a suite for analyzing large language models across training and scaling. In International Conference on Machine Learning, pp. 2397–2430. Cited by: §3.2.

[^4]: Generating sentences from a continuous space. In Proceedings of the 20th SIGNLL Conference on Computational Natural Language Learning, S. Riezler and Y. Goldberg (Eds.), Berlin, Germany, pp. 10–21. External Links: [Link](https://aclanthology.org/K16-1002/), [Document](https://dx.doi.org/10.18653/v1/K16-1002) Cited by: Appendix A.

[^5]: Recurrent memory transformer. In Advances in Neural Information Processing Systems, S. Koyejo, S. Mohamed, A. Agarwal, D. Belgrave, K. Cho, and A. Oh (Eds.), Vol. 35, pp. 11079–11091. External Links: [Link](https://proceedings.neurips.cc/paper_files/paper/2022/file/47e288629a6996a17ce50b90a056a0e1-Paper-Conference.pdf) Cited by: Appendix A, Appendix A, Appendix A, §3.2.

[^6]: Memory transformer. External Links: 2006.11527 Cited by: Appendix A.

[^7]: Universal sentence encoder for english. In Proceedings of the 2018 conference on empirical methods in natural language processing: system demonstrations, pp. 169–174. Cited by: Appendix A, §2.2.

[^8]: KV-distill: nearly lossless learnable context compression for llms. arXiv preprint arXiv:2503.10337. Cited by: Appendix A.

[^9]: Adapting language models to compress contexts. In Proceedings of the 2023 Conference on Empirical Methods in Natural Language Processing, H. Bouamor, J. Pino, and K. Bali (Eds.), Singapore, pp. 3829–3846. External Links: [Link](https://aclanthology.org/2023.emnlp-main.232/), [Document](https://dx.doi.org/10.18653/v1/2023.emnlp-main.232) Cited by: Appendix A, Appendix A, Appendix A, §2.2.

[^10]: FlashAttention: fast and memory-efficient exact attention with io-awareness. In Advances in Neural Information Processing Systems, S. Koyejo, S. Mohamed, A. Agarwal, D. Belgrave, K. Cho, and A. Oh (Eds.), Vol. 35, pp. 16344–16359. External Links: [Link](https://proceedings.neurips.cc/paper_files/paper/2022/file/67d57c32e20fd0a7a302cb81d36e40d5-Paper-Conference.pdf) Cited by: §4.

[^11]: Transformers are ssms: generalized models and efficient algorithms through structured state space duality. External Links: 2405.21060, [Link](https://arxiv.org/abs/2405.21060) Cited by: §3.2.

[^12]: Cartridges: lightweight and general-purpose long context representations via self-study. arXiv preprint arXiv:2506.06266. Cited by: Appendix A.

[^13]: Model-agnostic meta-learning for fast adaptation of deep networks. In Proceedings of the 34th International Conference on Machine Learning, D. Precup and Y. W. Teh (Eds.), Proceedings of Machine Learning Research, Vol. 70, pp. 1126–1135. External Links: [Link](https://proceedings.mlr.press/v70/finn17a.html) Cited by: §2.2, §3.3.

[^14]: SelfCP: compressing long prompt to 1/12 using the frozen large language model itself. arXiv preprint arXiv:2405.17052. Cited by: Appendix A, §2.2.

[^15]: In-context autoencoder for context compression in a large language model. In The Twelfth International Conference on Learning Representations, External Links: [Link](https://openreview.net/forum?id=uREj4ZuGJE) Cited by: Appendix A, Appendix A.

[^16]: Using fast weights to deblur old memories. In Proceedings of the ninth annual conference of the Cognitive Science Society, pp. 177–186. Cited by: Appendix A.

[^17]: LoRA: low-rank adaptation of large language models. In International Conference on Learning Representations, External Links: [Link](https://openreview.net/forum?id=nZeVKeeFYf9) Cited by: Appendix A.

[^18]: Editing models with task arithmetic. In The Eleventh International Conference on Learning Representations, Cited by: Appendix A.

[^19]: TRELLIS: learning to compress key-value memory in attention models. In Second Conference on Language Modeling, Cited by: Appendix A.

[^20]: Skip-thought vectors. In Advances in Neural Information Processing Systems, C. Cortes, N. Lawrence, D. Lee, M. Sugiyama, and R. Garnett (Eds.), Vol. 28, pp.. External Links: [Link](https://proceedings.neurips.cc/paper_files/paper/2015/file/f442d33fa06832082290ad8544a8da27-Paper.pdf) Cited by: Appendix A, §2.2.

[^21]: Cramming 1568 tokens into a single vector and back again: exploring the limits of embedding space capacity. In Proceedings of the 63rd Annual Meeting of the Association for Computational Linguistics (Volume 1: Long Papers), W. Che, J. Nabende, E. Shutova, and M. T. Pilehvar (Eds.), Vienna, Austria, pp. 19323–19339. External Links: [Link](https://aclanthology.org/2025.acl-long.948/), [Document](https://dx.doi.org/10.18653/v1/2025.acl-long.948), ISBN 979-8-89176-251-0 Cited by: Appendix A, §1, §1.

[^22]: Distributed representations of sentences and documents. In Proceedings of the 31st International Conference on Machine Learning, E. P. Xing and T. Jebara (Eds.), Proceedings of Machine Learning Research, Vol. 32, Bejing, China, pp. 1188–1196. External Links: [Link](https://proceedings.mlr.press/v32/le14.html) Cited by: Appendix A, §2.2.

[^23]: The power of scale for parameter-efficient prompt tuning. In Proceedings of the 2021 Conference on Empirical Methods in Natural Language Processing, M. Moens, X. Huang, L. Specia, and S. W. Yih (Eds.), Online and Punta Cana, Dominican Republic, pp. 3045–3059. External Links: [Link](https://aclanthology.org/2021.emnlp-main.243/), [Document](https://dx.doi.org/10.18653/v1/2021.emnlp-main.243) Cited by: Appendix A.

[^24]: Retrieval-augmented generation for knowledge-intensive nlp tasks. In Advances in Neural Information Processing Systems, H. Larochelle, M. Ranzato, R. Hadsell, M.F. Balcan, and H. Lin (Eds.), Vol. 33, pp. 9459–9474. External Links: [Link](https://proceedings.neurips.cc/paper_files/paper/2020/file/6b493230205f780e1bc26945df7481e5-Paper.pdf) Cited by: §1.

[^25]: Prefix-tuning: optimizing continuous prompts for generation. In Proceedings of the 59th Annual Meeting of the Association for Computational Linguistics and the 11th International Joint Conference on Natural Language Processing (Volume 1: Long Papers), C. Zong, F. Xia, W. Li, and R. Navigli (Eds.), Online, pp. 4582–4597. External Links: [Link](https://aclanthology.org/2021.acl-long.353/), [Document](https://dx.doi.org/10.18653/v1/2021.acl-long.353) Cited by: Appendix A.

[^26]: 500xCompressor: generalized prompt compression for large language models. arXiv preprint arXiv:2408.03094. Cited by: Appendix A, Appendix A, §2.2.

[^27]: Pointer sentinel mixture models. In International Conference on Learning Representations, External Links: [Link](https://openreview.net/forum?id=Byj72udxe) Cited by: §3.1.

[^28]: Neural variational inference for text processing. In Proceedings of The 33rd International Conference on Machine Learning, M. F. Balcan and K. Q. Weinberger (Eds.), Proceedings of Machine Learning Research, Vol. 48, New York, New York, USA, pp. 1727–1736. External Links: [Link](https://proceedings.mlr.press/v48/miao16.html) Cited by: Appendix A.

[^29]: On first-order meta-learning algorithms. arXiv preprint arXiv:1803.02999. Cited by: §4.

[^30]: Attention and compression is all you need for controllably efficient language models. arXiv preprint arXiv:2511.05313. Cited by: Appendix A.

[^31]: Language models are unsupervised multitask learners. OpenAI blog. Cited by: §3.2.

[^32]: Compressive transformers for long-range sequence modelling. In International Conference on Learning Representations, External Links: [Link](https://openreview.net/forum?id=SylKikSYDH) Cited by: Appendix A, Appendix A, §2.2.

[^33]: Meta-learning with implicit gradients. In Advances in Neural Information Processing Systems, H. Wallach, H. Larochelle, A. Beygelzimer, F. d'Alché-Buc, E. Fox, and R. Garnett (Eds.), Vol. 32, pp.. External Links: [Link](https://proceedings.neurips.cc/paper_files/paper/2019/file/072b030ba126b2f4b2374f342be9ed44-Paper.pdf) Cited by: §4.

[^34]: SQuAD: 100,000+ questions for machine comprehension of text. In Proceedings of the 2016 Conference on Empirical Methods in Natural Language Processing, J. Su, K. Duh, and X. Carreras (Eds.), Austin, Texas, pp. 2383–2392. External Links: [Link](https://aclanthology.org/D16-1264/), [Document](https://dx.doi.org/10.18653/v1/D16-1264) Cited by: §3.1.

[^35]: Hopfield networks is all you need. In International Conference on Learning Representations, Cited by: Appendix A, Appendix A, Appendix A.

[^36]: Associative recurrent memory transformer. arXiv preprint arXiv:2407.04841. Cited by: Appendix A, Appendix A, Appendix A, §3.2.

[^37]: Learning to control fast-weight memories: an alternative to dynamic recurrent networks. Neural Computation 4 (1), pp. 131–139. Cited by: Appendix A, Appendix A.

[^38]: Learning to (learn at test time): RNNs with expressive hidden states. In Forty-second International Conference on Machine Learning, External Links: [Link](https://openreview.net/forum?id=wXfuOj9C7L) Cited by: Table 3, Appendix A, §1, §2.2, §4.

[^39]: End-to-end test-time training for long context. arXiv preprint arXiv:2512.23675. Cited by: Appendix A.

[^40]: Gemini 1.5: unlocking multimodal understanding across millions of tokens of context. arXiv preprint arXiv:2403.05530. Cited by: §1.

[^41]: Kimi k2: open agentic intelligence. External Links: 2507.20534, [Link](https://arxiv.org/abs/2507.20534) Cited by: §1.

[^42]: Llama: open and efficient foundation language models. arXiv preprint arXiv:2302.13971. Cited by: §3.2.

[^43]: Towards ai-complete question answering: A set of prerequisite toy tasks. In 4th International Conference on Learning Representations, ICLR 2016, San Juan, Puerto Rico, May 2-4, 2016, Conference Track Proceedings, Y. Bengio and Y. LeCun (Eds.), External Links: [Link](http://arxiv.org/abs/1502.05698) Cited by: §3.1.

[^44]: Parallelizing linear transformers with the delta rule over sequence length. Advances in neural information processing systems 37, pp. 115491–115522. Cited by: §3.2.

[^45]: RepoCoder: repository-level code completion through iterative retrieval and generation. In Proceedings of the 2023 Conference on Empirical Methods in Natural Language Processing, H. Bouamor, J. Pino, and K. Bali (Eds.), Singapore, pp. 2471–2484. External Links: [Link](https://aclanthology.org/2023.emnlp-main.151/), [Document](https://dx.doi.org/10.18653/v1/2023.emnlp-main.151) Cited by: §1.
