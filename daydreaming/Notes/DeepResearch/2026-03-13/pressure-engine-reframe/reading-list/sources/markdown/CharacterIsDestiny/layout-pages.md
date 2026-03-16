# CharacterIsDestiny

Layout-preserving `pdftotext -layout` extraction.

<!-- page: 1 -->
```text
                                                Character is Destiny: Can Role-Playing Language Agents Make
                                                                  Persona-Driven Decisions?
                                                      Rui Xu1 , Xintao Wang1 , Jiangjie Chen1 , Siyu Yuan1 , Xinfeng Yuan1 ,
                                                        Jiaqing Liang1 , Zulong Chen2 , Xiaoqing Dong2 , Yanghua Xiao1
                                                                      1
                                                                        Fudan University 2 Alibaba Group
                                                       {ruixu21, xtwang21, syyuan21, xfyuan23}@m.fudan.edu.cn
                                                             {jjchen19, liangjiaqing, shawyh}@fudan.edu.cn
                                                             {zulong.czl, xiaoqing.dongxq}@alibaba-inc.com
                                                              Abstract                                Every man has one destiny.                  《Godfather》
                                                                                                                                                                📚
                                             Can Large Language Models (LLMs) simulate                                                                        1941.12
                                                                                                              Michael joined the navy against his
arXiv:2404.12138v2 [cs.AI] 18 Nov 2024
                                             humans in making important decisions? Re-                        father's wishes.        ——chapter 1
                                             cent research has unveiled the potential of us-
                                             ing LLMs to develop role-playing language                                                                        1946.02
                                                                                                              The negotiation between Godfather
                                             agents (RPLAs), mimicking mainly the knowl-                      and Sollozzo fails. ——chapter 3
                                             edge and tones of various characters. How-
                                             ever, imitative decision-making necessitates a                                                                   1946.06
                                                                                                              The godfather was attacked and
                                             more nuanced understanding of personas. In                       seriously injured.   ——chapter 4
                                             this paper, we benchmark the ability of LLMs
                                                                                                              Michael was hit by the police for                1946.12
                                             in persona-driven decision-making. Specifi-                      protecting his father. ——chapter 6
                                             cally, we investigate whether LLMs can pre-                                                                       1947.01
                                             dict characters’ decisions provided by the pre-       Profile of Michael Corleone
                                             ceding stories in high-quality novels. Lever-
                                                                                                    Description: Michael Corleone is the youngest
                                             aging character analyses written by literary           son of Vito Corleone, he is known for his quiet,   Memory
                                             experts, we construct a dataset L IFE C HOICE          intelligent, and introspective nature……
                                             comprising 1,462 characters’ decision points
                                                                                                                 Scenario
                                             from 388 books. Then, we conduct compre-
                                                                                                                  The Godfather was attacked and severely
                                             hensive experiments on L IFE C HOICE, with var-                      injured, lying in bed; the enemy is
                                             ious LLMs and RPLA methodologies. The                                protected by the New York police chief; the
                                                                                                                  eldest brother is irritable and the second
                                             results demonstrate that state-of-the-art LLMs                       brother is incompetent;
                                             exhibit promising capabilities in this task, yet                     A. Running away from the Corleone family.
                                             substantial room for improvement remains.              What          B. Avenging by stabbing the enemy and police.
                                             Hence, we further propose the C HAR M AP               should        C. Fearfully hiding in the protection of the family.
                                                                                                    I do?         D. Asking for help from the government.
                                             method, which adopts persona-based memory
                                             retrieval and significantly advances RPLAs on
                                             this task, achieving 5.03% increase in accuracy.    Figure 1: An example of L IFE C HOICE. Given a charac-
                                             Resources are available at https://github.          ter, a decision point and the preceding context, RPLAs
                                             com/airaer1998/LifeChoice.                          are expected to reproduce the original decision. Typi-
                                                                                                 cally, RPLAs are constructed by parsing the context into
                                         1   Introduction                                        the character’s description and memory.
                                              The fault, dear Brutus, is not in our stars,
                                              but in ourselves, that we are underlings.
                                                                                                 et al., 2024; Wang et al., 2024; Xie et al., 2024).
                                                      – Julius Caesar. Act 1, Scene 2.
                                                                                                 They emulate various characters across extensive
                                           With the recent advancements in large language        applications, including fictional characters in chat-
                                         models (LLMs) (OpenAI, 2023; Touvron et al.,            bots and video games (Wang et al., 2023, 2024), as
                                         2023), Role-Playing Language Agents (RPLAs)             well as digital clones (Gao et al., 2023) or personal-
                                         have emerged as a flourishing field of AI appli-        ized assistants (Xu et al., 2022; Salemi et al., 2024)
                                         cations and research (Chen et al., 2024). RPLAs         for real-world individuals.
                                         are LLM-based AI systems that simulate assigned            Can RPLAs reliably make decisions that align
                                         personas, reproducing their tones, knowledge, per-      with their personas, as humans do? This question is
                                         sonalities and even decisions (Park et al., 2023; Gao   vital for the practical usage of RPLAs, yet remains
```

<!-- page: 2 -->
```text
underexplored. Previous studies primarily inves-         evolution of characters and environments. 3) Intri-
tigate RPLAs’ character fidelity in terms of their       cate motives, where RPLAs are required to reason
tones (Wang et al., 2023) and knowledge (Shao            through complex and entangled backgrounds and
et al., 2023), which could be readily replicated by      motives to arrive at the decisions.
existing RPLAs via style imitation and knowledge            We conduct extensive experiments to evaluate
retrieval. However, these features are relatively        RPLAs on L IFE C HOICE. Our experiments cover
superficial compared with the underlying thinking        various LLMs and different RPLA frameworks,
and mindset of characters. Recent efforts (Wang          including memory-enhanced agents, long-context
et al., 2024) study the personality fidelity of RPLAs,   LLMs, and our proposed method C HAR M AP to-
but they fail to capture the nuances and dynamics        wards better simulation of persona-driven decisions.
of characters’ mindsets. Hence, it remains an un-        The results demonstrate that existing RPLAs have
derstudied question whether RPLAs could simulate         shown a promising accuracy of up to 62.92% on
persona-driven decisions, which challenges their         L IFE C HOICE. Furthermore, C HAR M AP signifi-
comprehensive understanding of the personas and          cantly enhances RPLAs on this task, achieving
reasoning about unobserved behaviors.                    an accuracy of 67.95%, which exceeds previous
                                                         baselines by 5.03%. However, compared to the
   In this paper, we systematically study the capa-
                                                         human performance of 92.01%, there is still sig-
bility of RPLAs to simulate persona-driven deci-
                                                         nificant room for improvement. Meanwhile, we
sions, based on characters from high-quality nov-
                                                         observe that both well-summarized character de-
els. In high-quality novels, characters’ life choices
                                                         scriptions and accurate memory retrieval are crucial
are carefully plotted and aligned with their per-
                                                         for RPLAs.
sonas. Hence, we introduce the L IFE C HOICE
                                                            In summary, our contributions include:
dataset, which evaluate whether RPLAs can faith-
fully reproduce the characters’ life choices in the          • We propose to explore RPLAs’ ability in simu-
narratives. Specifically, L IFE C HOICE comprises              lating persona-driven decisions, which is cru-
1,462 character decisions from 388 novels, leverag-            cial for future RPLA applications and chal-
ing expert-written character analyses. Each sample             lenges existing RPLAs.
is presented as a multiple-choice question with the          • We delicately craft L IFE C HOICE, the first
preceding context before the decision point. As                benchmark for persona-driven decisions of
depicted in Figure 1, RPLAs are expected to iden-              RPLAs, based on characters’ life choices
tify and reason over relevant knowledge about the              from high-quality novels. Besides, we pro-
characters to simulate their decisions. The construc-          pose C HAR M AP, which adopts persona-based
tion of L IFE C HOICE primarily involves three steps:          memory retrieval for better decision-making
decision point selection, multiple-choice question             of RPLAs.
construction, and manual examination.                        • Based on L IFE C HOICE, we conduct exten-
   Compared with previous methods for RPLA                     sive experiments. The results demonstrate the
evaluation, our task and dataset benefit from higher-          promising performance of RPLAs in decision
quality data and are more challenging. First, our              simulation. Then, we analyze and compare
questions and decisions are well-designed and                  methodologies for RPLA development, and
closely aligned with the personas, since they are              show the effectiveness of C HAR M AP.
sourced from well-crafted narratives. Hence, our
                                                         2    Related Work
data establish solid ground truth for simulating
characters’ persona-driven decisions. Second, our        Character Role-Playing Early research on
task is more challenging as it requires RPLAs            character-related studies focuses on character un-
to comprehensively understand and reason based           derstanding. Brahman et al. (2021) attempts to
on the personas, including their knowledge, ex-          predict a specific character through the text of the
periences, and personalities. Specifically, L IFE -      novel. Yu et al. (2022) provides dialogues from
C HOICE poses the following challenges: 1) Long-         movie scripts for the model to examine and then
context understanding, where RPLAs need to iden-         asks it to identify the character who speaks each
tify sparse relevant motivations from massive char-      passage. With the enhancement of model abilities,
acter contexts. 2) Temporal intelligence, where          some work attempts to make the model simulate
RPLAs should intelligently adapt to the dynamic          complex role-playing. Li et al. (2023) analyzes 32
```

<!-- page: 3 -->
```text
anime characters using 54k dialogues and person-          Book: Les Misérables
ality traits. They use sentence embeddings for dia-       Character: Jean Valjean
                                                          Context:
logue selection and evaluation. Zhou et al. (2023)        In 1815 Monsieur Charles-François-Bienvenu Myriel was
uses identity, interests, and relationships, collecting   Bishop of Digne. He was then......Jean Valjean reflections
AI behaviors for imitation and using character data       gave him a sort of frightening aspect. He was subject to one
                                                          of those violent inner tearings, which was not unknown to
for fine-tuning. They evaluate model consistency          him.
and linguistic style. Wang et al. (2023) creates          Scenario:
                                                          In the courtroom, an innocent man was wrongfully accused,
a dataset for script characters and evaluates role-       because he bore a resemblance to Jean Valjean. If Jean
playing quality based on speaking style imitation         Valjean did not come forward, this innocent man would be
and role-specific knowledge. These studies make a         sent to the gallows in his place. At this time, Jean Valjean
                                                          had transformed his identity and become a respected town
chatbot for a certain character, but they focus more      mayor, and he had also adopted a young girl named Cosette,
on imitating the character from the perspective of        with whom he had a new life.
dialogue, which is a shallow imitation. Our study         Question:
                                                          You will play the role of Jean Valjean. What will you choose
aims to evaluate RPLAs from the perspective of            to do when you discover that man is about to be convicted
behavior and decision-making, which further chal-         due to being mistaken for you?
lenges the model’s understanding of the roles.            Options:
                                                          A. Keep silent, letting an innocent person take the punish-
                                                          ment in one’s place.
Personal LLM assistants With the rapid devel-             B. Persuade the person to run away, in order to protect both
opment of artificial intelligence technology, there       from the disaster of jail.
                                                          C. Go to court and reveal the truth, sacrificing oneself to
are now many personal intelligent agents embedded         save the innocent person.
in mobile devices, providing personalized services        D. Look for legal loopholes, trying to save both the person
through analyzing user data and equipment (Kaplan         and oneself.
and Haenlein, 2019; Hoy, 2018). These agents can          Correct Answer: C
model the user’s profile and preferences through the      Motivation:
                                                          [Values and Beliefs] Jean Valjean is a person who values
user’s historical data (Gurrin et al., 2014; Dodge        honesty and justice, possessing a strong sense of morality
and Kitchin, 2007), such as extracting personal-          and righteousness. He decides to turn himself in to save an-
                                                          other innocent person, fulfilling his inner need for morality
ity from the user’s record text (Majumder et al.,         and justice.
2017; Štajner and Yenikent, 2020), reading emo-
tions from the user’s image data (Jaiswal et al.,         Table 1: Case study of L IFE C HOICE. A complete set
2020; Zad et al., 2021), modeling preferences from        of data includes book, character, scenario, question,
historical interaction information (Tang et al., 2019;    options, correct answer, motivation, and input.
Li et al., 2018), and pushing notifications from
smart phones (Li et al., 2018). These memories
can enhance the model’s decision-making and rea-          rect answer y, and the motivation m explaining
soning, bringing a better personal experience for         the character’s choice. Our data is sourced from
users. However, obtaining real user memory data           the website Supersummary1 , which provides three
is difficult and violates privacy. We model charac-       pieces of content written by literary experts: key
ters from historical data in high-quality novel texts,    character descriptions, chapter summaries, and
allowing the model to restore the real choices in         book analyses. We contact the website and obtain
the storyline based on the previous text, providing       authorization to use the data for academic research.
the first benchmark for the wide testing of personal      The dataset construction comprises the following
intelligent agents.                                       three main steps:
                                                          Selecting Decision Points To prevent data leak-
3     Dataset and Task Setups                             age, we first filter novels on the site using the fol-
3.1    Dataset Construction                               lowing criteria: (1) The narrative must exclude
                                                          non-fiction genres like biographies or documentary
We construct a comprehensive dataset called L IFE -       literature. (2) The narrative perspective must be
C HOICE. As shown in Table 1, the sample for              in the first or third person. (3) The progression
each decision point includes the preceding con-           of narrative time should be linear, avoiding stories
text p from the original book, the current scenario       with complex timelines or flashbacks. (4) Exclude
s, a question q outlining a decision faced by that
character c, a list of options a = {ai }4i=1 , the cor-      1
                                                                 https://www.supersummary.com/
```

<!-- page: 4 -->
```text
     Dataset                Source           Context Length           Task Format              Has Explanation
     TVSHOWGUESS   TV show transcripts           ～50k            Character Identification            ✗
     ROCStories Commonsense short stories        ～100          Character Behavior Prediction         ✗
     LiSCU             Literature                ～1000           Character Identification            ✗
     L IFE C HOICE     Literature                ～150k         Character Behavior Prediction         ✓
Table 2: Comparison between L IFE C HOICE and previous character understanding benchmarks: data source, context
length, task format, and whether the benchmark has explanations.
overly popular books, as measured by a high num-
ber of reviews on literary review websites. For each
book that passes these filters, we provide GPT-4
with content written by literary experts, requesting
it to output each key character’s life choice decision
points and their corresponding gold motivations.
Additionally, we also ask GPT-4 to output the chap-
ter numbers related to the decision based on the
extracted motivations. As shown in the example in
Figure 1, the literary expert’s analysis of the book
suggests that Michael Corleone’s motivation for
choosing to assassinate the enemy includes both
avenging his father and witnessing the collusion
between the police and the enemy, which exposes
him to the darker side of the government. We then
identify two corresponding chapters in the orig-         Figure 2: Statistics of motivation types in L IFE C HOICE,
inal book based on these motivations, providing          with the first words for each motivation type.
more refined data for constructing multiple-choice
questions.
                                                         data they deem low quality.
Constructing Multiple-Choice Questions We                   Ultimately, we collect 1,462 characters from 388
input the content written by literary experts and the    books and their corresponding life choices. Table
corresponding chapters identified based on motiva-       1 shows a complete data example. The specific
tion into GPT-4. Our goal is to generate multiple-       prompts and more detailed data construction pro-
choice questions that capture the complexity of the      cess can be found in Appendix A.
characters’ decision-making processes. The correct
option reflects the decision made by the characters      3.2     Dataset Analysis
in the original books, whereas the distractors are       We refer to the drama theory of Aristophanes (Som-
designed to be plausible for an arbitrary person.        merstein, 2013; Silk, 2002) as the system prompt
As shown in the example in Figure 1, Michael             and use GPT-4 to classify the motivations for char-
Corleone can ask for help from the government            acter decisions into two meta-motivations and sev-
because he was once a Navy officer who trusted           eral accompanying sub-motivations:
the government. However, in the preceding text,
Michael witnesses the dark side of the government,       Character-driven Motivation Character-driven
so he ultimately chooses to stab the police.             behavior revolves around the character’s inner
                                                         world, personality, and transformation. Sub-
Manual Examination We invite ten native                  motivations of character-driven behavior include
English-speaking university students to filter the       Personality and Traits, Emotions and Psychologi-
data and pay them according to local minimum             cal State, Social Relationships, Values and Beliefs,
wage standards. We supply the annotators with            and Desires and Goals.
content written by literary experts and the multiple-
choice questions, asking them to assess whether          Plot-driven Motivation Plot-driven behavior
the model-created questions are challenging and          stems from a series of external events and conflicts
reasonable. They are also tasked with filtering out      unfolding. Characters often react passively within
```

<!-- page: 5 -->
```text
a larger narrative structure, with their actions led by     Profile             Role-Playing
                                                                                                ACC +motivation
external events. Sub-motivations of plot-driven be-       Construction             Model
havior include External Conflicts, Tasks and Goals,                        Description Construction
                                                          Hierarchical merging LLaMA-3           42.10   83.09
Puzzles and Secrets, Pursuits and Escapes, Explo-                               GPT-3.5          39.85   80.00
ration and Discovery, Power and Control, and In-                                GPT-4            45.43   85.24
trigue and Betrayal.                                      Incremental updating LLaMA-3           43.82   83.21
   Note that each topic is assigned one category                                GPT-3.5          41.06   81.63
                                                                                GPT-4            47.02   86.47
of motivation. Figure 2 shows the proportion of           Human Description LLaMA-3              52.51   87.28
different motivations. Detailed introductions for                               GPT-3.5          52.04   86.33
each sub-motivation are in Appendix B.2.                                        GPT-4            55.17   90.23
                                                                              Memory Retrieval
                                                          BM25                  GPT-4            26.08   75.88
3.3    Task Setups                                        Embedding             GPT-4            35.66   78.24
This task can be formulated as P (y|x). Given the                           Description & Memory
                                                          Direct concatenation LLaMA-3           57.02   92.04
input x = (p, s, c, q, a), the RPLA needs to identify                           Mixtral          58.56   91.75
the correct choice y that aligns with the character’s                           Claude-3.5       62.85   96.45
decision in the narrative. For evaluation, we di-                               Gemini-1.5-pro 57.16     91.38
                                                                                GPT-3.5          55.62   90.39
rectly use the accuracy of multiple-choice question                             GPT-4            62.92   95.46
answering. As shown in Table 2, compared to other         C HAR M AP            LLaMA-3          63.72   95.93
character understanding tasks, L IFE C HOICE re-                                Mixtral          65.02   92.05
                                                                                Claude-3.5       67.13   96.90
quires understanding the character through a more                               Gemini-1.5-pro 63.94     91.39
extended context to make decisions. RPLAs must                                  GPT-3.5          61.62   90.95
locate relevant information related to the current                              GPT-4            67.95   96.87
scene in vast personal data. This behavior demands
                                                          Table 3: Results of different LLMs on L IFE C HOICE.
a more profound understanding of the characters.          ACC refers to the decision accuracy. +motivation refers
                                                          to the results after providing the motivations behind
4     Experiments                                         character decisions, which are extracted from expert
Because our inputs generally exceed 100k, it is dif-      analyses by GPT-4.
ficult for LLMs to handle them directly. Therefore,
our approach is divided into two steps: 1) Char-          sequentially, and the description is updated and re-
acter Profile Construction, which includes the            fined incrementally by concatenating summarized
character’s description and memories; 2) Reason-          chunks. The summarization model for both auto-
ing for Decisions, where different LLMs use the           mated methods is GPT-3.5. Additionally, using
constructed profile to answer the questions.              the (3) expert-written descriptions from Supersum-
                                                          mary, we employ GPT-4 to identify the positions of
4.1    Character Profile Construction
                                                          the decision points and truncate the text, providing
As shown in Figure 1, the character profile consists      only the data before these points. All descriptions
of two parts. The first part is the character’s de-       are kept within 5k tokens, the maximum for human-
scription, including their personality, experiences,      written descriptions.
hobbies, etc. The second part is the character’s
memories, specific segments from the preceding            Memory Retrieval We use two memory retrieval
text. Below, I will detail the methods for construct-     methods: (1) BM25 (Robertson et al., 2009):
ing these two parts:                                      Scores documents based on term relevance and
                                                          length, optimizing retrieval using term frequency
Description Construction We adopt two auto-               and distribution. (2) Embedding-based retrieval:
matic methods to construct character descriptions:        Uses dense vectors representing documents and
(1) Hierarchical merging (Wu et al., 2021): Books         queries to assess semantic similarity through vector
are divided into chunks that fit within the LLM con-      distance. For the embedding model, we use Ope-
text window. The LLM summarizes each chunk,               nAI’s text-embedding-ada-002(Neelakantan et al.,
then merges and summarizes adjacent summarized            2022) model.
chunks iteratively to produce the final descrip-
tion. (2) Incremental updating (Chang et al., 2023):      Description & Memory Using only Descrip-
Books are divided into chunks and summarized              tion or Memory alone may lead to information
```

<!-- page: 6 -->
```text
 Query
                                                                                          Raw text Concat. C HAR M AP
              You will play the role of Jaime Lannister. What will you
              do with the Army of the Dead invading the continent                GPT-4       -       63.05      68.10
                                                                                 human     92.01     66.82      74.78
 STEP1: Generate the description
 Jaime is the eldest son of Duke Tywin Lannister and Lady Joanna         Table 4: Results of the human evaluation. Concat. refers
 of Casterly Rock... During his journey and conversations with
 Brienne of Tarth, Jaime begins to question his moral values and         to the direct concatenation of Description and Memory.
 loyalties... After Cersei deceives him and other lords, not intending
 to abide by the agreement against the White Walkers...
 STEP2: Locate memory through description                                5     Analysis
  Memory a: “It's about loyalty Brienne stared at him... “I have to nd
  her rst. I promised Jaime. He named that sword 'Oathkeeper.' I         In the experiments, we wish to answer two re-
  must go to save her, succeed or die trying.”          ——chapter 11
                                                                         search questions: RQ1) Can LLMs make decisions
 Memory b: That boy, who wanted to be Arthur Dayne when he was
 young, nally became the Smiling Knight... He mounted his                based on historical data? RQ2) What influences the
 warhorse heading north.                         ——chapter 20
                                                                         decision-making of LLMs?
   fi
        fi
                                                                   fi
Figure 3: An overview of C HAR M AP, a two-step                          5.1    Can LLMs Make Decisions Based on
scenario-specific character profile building approach.                          Historical Data?
                                                                         Analysis of Model Results Table 3 presents the
loss (Wang et al., 2024). Therefore, we also ex-                         accuracy results of different RPLA methods on the
periment by combining the results of both meth-                          L IFE C HOICE. Additionally, we evaluate the results
ods to form the character’s profile. We adopt two                        when the model is provided with gold motivation,
methods: (1) Direct concatenation: This method                           and several observations can be made: First, the
concatenates the results from both approaches by                         method that uses both Description and Memory sur-
prompting the model to role-play the correspond-                         passes the one that uses only one, suggesting that
ing character. By default, it uses the results from                      both holistic and detailed data of key characters are
Human Description and Embedding retrieval. (2)                           essential in final decision-making. Second, when
C HAR M AP: To better utilize the information in                         gold motivation is provided, the accuracy consis-
the Description, we propose CHARacter MAP-                               tently exceeds 80%, indicating the rationality of
ping Profile Synthesis (C HAR M AP), constructing                        these motivations in the data. Third, the perfor-
a more scenario-specific profile in two steps. As                        mance gap among different LLMs is not significant
shown in Figure 3, first, after obtaining the descrip-                   while reasoning the answer. This indicates that
tion, we input it along with the question into the                       the main factor for the result is the generated pro-
model, asking it to locate the plot in the Description                   file rather than reasoning ability. Last, C HAR M AP
relevant to the current scene based on the question.                     outperforms the method that directly concatenates
Second, we use these episodes as queries to retrieve                     Description and Memory by 5.03%, proving its
related memories and then input them into the in-                        effectiveness. This scenario-specific profile better
ference model and the description. This leverages                        assists RPLA in decision-making.
the overall character storyline in the description,
                                                                         Humans are Good Decision-makers We invite
thereby better retrieving related memories.
                                                                         three native English-speaking university students
4.2          Reasoning for Decisions                                     to take a test in which we select six novels they
                                                                         have never heard of before. Each novel has be-
After compressing the original input x into a char-
                                                                         tween 3 to 5 characters and their corresponding
acter profile, we feed it into the LLMs. For methods
                                                                         multiple-choice questions. We provide each per-
using only description or memory, we use GPT-3.5,
                                                                         son with three data sets for each key character in
GPT-4, and LLaMA-3(Team, 2024b). For methods
                                                                         two books: the full original text before the deci-
using both, we also include Claude-3(Anthropic,
                                                                         sion point, direct concatenation Description and
2024), Gemini(Team, 2024a), and Mixtral (Jiang
                                                                         Memory result, and the result from C HAR M AP. As
et al., 2024). For all these models, we adopt the
                                                                         shown in Table 4, compared to direct concatena-
official instruction formats where available 2 .
                                                                         tion, the C HAR M AP results are easier for humans
   2
     The versions in this paper are gpt-3.5-turbo-1106,                  to understand. Additionally, humans slightly out-
gpt-4-1106-preview, Llama-3-70B-Instruct, Claude
-3.5-Sonnet, Gemini-1.5-pro and Mixtral-8x7B-v0.1                        perform GPT-4 in reasoning answers based on the
respectively.                                                            profiles, indicating that humans can understand sub-
```

<!-- page: 7 -->
```text
               90                                                                  LLMs              Method         Accuracy
                                                                                   Claude-3.5        long-context    64.83
                                                                                   Claude-3.5        C HAR M AP      67.13
              82.5                                                                 Genmini-1.5 Pro   long-context    61.14
                                                                                   Genmini-1.5 Pro   C HAR M AP      63.94
   Accuracy
               75
                                                                            Table 5: The results of using long-context models for
                                                                            L IFE C HOICE.
              67.5
               60
                     <1k   1k-3k   3k-5k   5k-7k   7k-9k   >9k   new book
                             Number of Reviews
Figure 4: The impact of the number of book reviews on
accuracy in L IFE C HOICE, with new books being those
not present in the training corpus of LLMs.
tle character decisions better than models. When
given the raw text, humans can achieve an accuracy
rate of 92.01%, suggesting there is still significant
room for improvement in RPLA methods.
Mitigation and Analysis of Data Leakage Data
leakage is a significant challenge since our data
might appear in the model’s pre-training corpus.                            Figure 5: Heatmap of the impact of motivation types
During the data collection phases in section 3.1, we                        on the results. The results are predicted from the In-
adopt various preventive measures. For evaluation,                          cremental updating, the embedding-retrieved memory,
we employ an entity replacement strategy, substi-                           the direct concatenation of both, and C HAR M AP. The
tuting character names, locations, and other entities                       role-playing model uses GPT-4.
with placeholders. We believe data leakage relates
to the amount of relevant corpus used during LLM                            Analysis of Long-Context LLMs Long context
pre-training, with more popular books having more                           is an essential feature of L IFE C HOICE, and di-
related corpus. To verify this, we use the number of                        rectly using long-context models for role-playing
reviews on the book review website3 to indicate a                           is an exciting topic. Making decisions based on
book’s popularity and evaluate the results of books                         extensive context tests a model’s ability to under-
with different review counts on L IFE C HOICE. We                           stand global data and reason from a character’s
use C HAR M AP to build profiles and GPT-4 as the                           perspective. We evaluate two long-context models:
role-playing model, sampling thirty books with dif-                         Claude3-sonnect and kimi-chat. As shown in Table
ferent numbers of reviews, including thirty books                           5, although the performance of long-context mod-
not in the LLMs’ corpus (published after November                           els is not as strong as C HAR M AP, they still demon-
6 for gpt-4-1106-preview). As shown in Figure 4,                            strate potential in role-playing. L IFE C HOICE, as
the model’s accuracy significantly improves when                            a task requiring multiple reasoning points and an
the number of reviews exceeds 5,000. In contrast,                           overall understanding of the context, can also serve
books with fewer than 5,000 reviews show slight                             as a vital benchmark for evaluating long-context
fluctuation and results similar to those not in the                         models.
LLMs’ corpus. Therefore, it can be considered
that for books with a low number of reviews, data                           5.2   What Influences the Decision-making of
leakage has little impact on C HAR M AP. In section                               LLMs?
3.1, we use 5,000 reviews as a threshold to filter                          The Impact of Motivation Types In line with
the books.                                                                  the motivation types presented in Section 3.2, we
   3
       https://www.douban.com/                                              examine how different types of motivation influ-
```

<!-- page: 8 -->
```text
 Science Fiction                                   67.34
                                                                                100
                                                                                        character-driven
      Romance                                 64.05                                     plot-driven
        Fantasy                             60.08                                75
                                                                     Accuracy
      Adventure                            57.73
              ... 0                                                             50
    Family Saga                    45.27
        Mystery                   42.46
                                                                                 25
          Crime                  41.55
                   0   17.5      35        52.5            70
                                                                                 0
                              Accuracy                                            1/5     2/5     3/5      4/5   5/5
                                                                                           Information Ratio
Figure 6: The result of the impact of different novel
genres on accuracy.                                             Figure 7: Analysis of whether character selection will
                                                                change. The x-axis represents the input length relative
                                                                to the point truncation.
ence characters’ decision-making. For profiles, we
evaluate four methods: the Incremental updating,
                                                                you make the same choices? We conduct a study
the embedding-retrieved memory, the direct con-
                                                                on this matter. Specifically, we randomly sam-
catenation of both, and C HAR M AP. For reasoning,
                                                                ple 40 characters, half character-driven, and half
we use GPT-4 uniformly. The results are shown in
                                                                plot-driven. We split the content preceding the
Figure 5. We find that tasks requiring coherent rea-
                                                                decision points into five equal sections and used
soning, such as puzzles and mysteries, are not well
                                                                these various content lengths as input. We conduct
answered for all methods. This might be because
                                                                experiments on the combination of human descrip-
these questions need multi-step reasoning and de-
                                                                tion + embedding-retrieved memories, and the role-
tails from various memories. Moreover, plot-driven
                                                                playing model is GPT-4. As shown in Figure 7, in
questions have lower accuracy when descriptions
                                                                the early stages, the accuracy of most characters’
are used only for the profile. Conversely, character-
                                                                decisions is close to random (25%), potentially due
driven questions are challenging to answer when
                                                                to insufficient information. As more information
relying only on memories. We believe this is be-
                                                                becomes available, the characters’ decisions tend
cause character summaries in descriptions better
                                                                to be closer to the correct choice. For character-
capture the overall essence of the characters, while
                                                                driven decisions, accuracy tends to be stable. For
memories provide direct access to relevant events.
                                                                plot-driven, the accuracy rate may change abruptly.
The Impact of Novel Genres We use the genre                     This could be due to the relatively stable character-
tags from novels on the website to analyze the ac-              istics of a character, while some sudden events may
curacy of character selection across different gen-             greatly influence the final choices of the character.
res. We conduct experiments on the the direct con-
catenation of description and memories, and the
                                                                6   Conclusion
role-playing model using GPT-4. As depicted in                  In this work, we propose the first task to evaluate
Figure 6, the accuracy of science fiction, fantasy              the decision-making of RPLAs, testing whether
novels, and romance novels is quite high. This                  LLMs can accurately reconstruct decisions using
could be because the characters in these novels are             historical data. We construct L IFE C HOICE, which
often stylized or have fixed creative patterns and              includes 1,462 characters from 388 books and
archetypes. In contrast, crime and mystery nov-                 their life choices. Extensive experiments on L IFE -
els perform poorly, which might be because they                 C HOICE demonstrate the promising performance
involve complex logical chains, and characters in               of RPLAs in decision simulation. Additionally,
these novels frequently take abnormal actions. Fur-             we propose C HAR M AP, which uses persona-based
ther details about each genre and the complete table            memory retrieval to enhance decision-making. We
can be found in Appendix B.1.                                   hope this work provides better evaluation bench-
                                                                marks for RPLAs and directs the future develop-
The Impact of Temporal Data If faced with                       ment of personal LLM assistants.
the decisions of years past at this moment, would
```

<!-- page: 9 -->
```text
Limitations                                                  Martin Dodge and Rob Kitchin. 2007. ‘outlines of a
                                                              world coming into existence’: pervasive computing
In this paper, we primarily investigate whether fic-          and the ethics of forgetting. Environment and plan-
tional characters can recreate their choices within           ning B: planning and design, 34(3):431–445.
a book. Although we have controlled the quality of
                                                             Jingsheng Gao, Yixin Lian, Ziyi Zhou, Yuzhuo Fu,
the novels, there may still be issues with the plot             and Baoyuan Wang. 2023. Livechat: A large-
and characters since the author designed the story-             scale personalized dialogue dataset automatically
line, which can result in illogical choices within              constructed from live streaming. arXiv preprint
the book. Furthermore, as our research focuses                  arXiv:2306.08401.
mainly on fictional characters, there is a certain           Yunfan Gao, Yun Xiong, Xinyu Gao, Kangxiang Jia,
gap compared to real-world humans. For example,                Jinliu Pan, Yuxi Bi, Yi Dai, Jiawei Sun, Qianyu Guo,
the author fictionalizes some story backgrounds,               Meng Wang, and Haofen Wang. 2024. Retrieval-
which may impact the model’s generation results.               augmented generation for large language models: A
                                                               survey.
Ethics Statement                                             Cathal Gurrin, Alan F Smeaton, Aiden R Doherty, et al.
                                                               2014. Lifelogging: Personal big data. Foundations
Use of Human Annotations Our institution re-                   and Trends® in information retrieval, 8(1):1–125.
cruits annotators to implement the annotations of
motivation recognition dataset construction. We              Matthew B Hoy. 2018. Alexa, siri, cortana, and more:
ensure the privacy rights of the annotators are re-           an introduction to voice assistants. Medical reference
                                                              services quarterly, 37(1):81–88.
spected during the annotation process. The anno-
tators receive compensation exceeding the local              Akriti Jaiswal, A Krishnama Raju, and Suman Deb.
minimum wage and have consented to use moti-                   2020. Facial emotion detection using deep learn-
vation recognition data they process for research              ing. In 2020 international conference for emerging
                                                               technology (INCET), pages 1–5. IEEE.
purposes. Appendix C provides further details on
the annotations.                                             Albert Q. Jiang, Alexandre Sablayrolles, Antoine
                                                               Roux, Arthur Mensch, Blanche Savary, Chris
Risks The L IFE C HOICE dataset is sourced from                Bamford, Devendra Singh Chaplot, Diego de las
novels and analysis data written by human literary             Casas, Emma Bou Hanna, Florian Bressand, Gi-
experts. However, we cannot guarantee that this                anna Lengyel, Guillaume Bour, Guillaume Lam-
data is entirely free from toxic or discriminatory             ple, Lélio Renard Lavaud, Lucile Saulnier, Marie-
                                                               Anne Lachaux, Pierre Stock, Sandeep Subramanian,
language. Additionally, our data is generated by               Sophia Yang, Szymon Antoniak, Teven Le Scao,
GPT-4, which may introduce some inherent biases                Théophile Gervet, Thibaut Lavril, Thomas Wang,
and hallucinations of the model.                               Timothée Lacroix, and William El Sayed. 2024. Mix-
                                                               tral of experts.
                                                             Andreas Kaplan and Michael Haenlein. 2019. Siri, siri,
References                                                     in my hand: Who’s the fairest in the land? on the
Anthropic. 2024. The claude 3 model family: Opus,              interpretations, illustrations, and implications of arti-
  sonnet, haiku.                                               ficial intelligence. Business horizons, 62(1):15–25.
Faeze Brahman, Meng Huang, Oyvind Tafjord, Chao              Cheng Li, Ziang Leng, Chenxi Yan, Junyi Shen, Hao
  Zhao, Mrinmaya Sachan, and Snigdha Chaturvedi.               Wang, Weishi MI, Yaying Fei, Xiaoyang Feng, Song
  2021. " let your characters tell their story": A dataset     Yan, HaoSheng Wang, et al. 2023. Chatharuhi: Re-
  for character-centric narrative understanding. arXiv         viving anime character in reality via large language
  preprint arXiv:2109.05438.                                   model. arXiv preprint arXiv:2308.09597.
Yapei Chang, Kyle Lo, Tanya Goyal, and Mohit Iyyer.          Yuanchun Li, Ziyue Yang, Yao Guo, Xiangqun Chen,
  2023. Booookscore: A systematic exploration of               Yuvraj Agarwal, and Jason I Hong. 2018. Automated
  book-length summarization in the era of llms. arXiv          extraction of personal knowledge from smartphone
  preprint arXiv:2310.00785.                                   push notifications. In 2018 IEEE International Con-
                                                               ference on Big Data (Big Data), pages 733–742.
Jiangjie Chen, Xintao Wang, Rui Xu, Siyu Yuan, Yikai           IEEE.
   Zhang, Wei Shi, Jian Xie, Shuang Li, Ruihan Yang,
   Tinghui Zhu, Aili Chen, Nianqi Li, Lida Chen, Caiyu       Navonil Majumder, Soujanya Poria, Alexander Gelbukh,
   Hu, Siye Wu, Scott Ren, Ziquan Fu, and Yanghua              and Erik Cambria. 2017. Deep learning-based doc-
   Xiao. 2024. From persona to personalization: A              ument modeling for personality detection from text.
   survey on role-playing language agents.                     IEEE Intelligent Systems, 32(2):74–79.
```

<!-- page: 10 -->
```text
Arvind Neelakantan, Tao Xu, Raul Puri, Alec Rad-          Zekun Moore Wang, Zhongyuan Peng, Haoran Que,
  ford, Jesse Michael Han, Jerry Tworek, Qiming             Jiaheng Liu, Wangchunshu Zhou, Yuhan Wu,
  Yuan, Nikolas Tezak, Jong Wook Kim, Chris Hallacy,        Hongcheng Guo, Ruitong Gan, Zehao Ni, Man
  Johannes Heidecke, Pranav Shyam, Boris Power,             Zhang, Zhaoxiang Zhang, Wanli Ouyang, Ke Xu,
  Tyna Eloundou Nekoul, Girish Sastry, Gretchen             Wenhu Chen, Jie Fu, and Junran Peng. 2023.
  Krueger, David Schnurr, Felipe Petroski Such, Kenny       Rolellm: Benchmarking, eliciting, and enhancing
  Hsu, Madeleine Thompson, Tabarak Khan, Toki               role-playing abilities of large language models.
  Sherbakov, Joanne Jang, Peter Welinder, and Lilian
  Weng. 2022. Text and code embeddings by con-            Jeff Wu, Long Ouyang, Daniel M. Ziegler, Nisan Sti-
  trastive pre-training.                                     ennon, Ryan Lowe, Jan Leike, and Paul Christiano.
                                                             2021. Recursively summarizing books with human
OpenAI. 2023. Gpt-4 technical report.                        feedback.
Joon Sung Park, Joseph C. O’Brien, Carrie J. Cai,         Chengxing Xie, Canyu Chen, Feiran Jia, Ziyu Ye,
  Meredith Ringel Morris, Percy Liang, and Michael S.       Kai Shu, Adel Bibi, Ziniu Hu, Philip Torr, Bernard
  Bernstein. 2023. Generative agents: Interactive sim-      Ghanem, and Guohao Li. 2024. Can large language
  ulacra of human behavior.                                 model agents simulate human trust behaviors? arXiv
                                                            preprint arXiv:2402.04559.
Stephen Robertson, Hugo Zaragoza, et al. 2009. The
   probabilistic relevance framework: Bm25 and be-        Chen Xu, Piji Li, Wei Wang, Haoran Yang, Siyun Wang,
  yond. Foundations and Trends® in Information Re-          and Chuangbai Xiao. 2022. Cosplay: Concept set
   trieval, 3(4):333–389.                                   guided personalized dialogue generation across both
                                                            party personas. In Proceedings of the 45th Inter-
Alireza Salemi, Sheshera Mysore, Michael Bendersky,         national ACM SIGIR Conference on Research and
  and Hamed Zamani. 2024. Lamp: When large lan-             Development in Information Retrieval, SIGIR ’22.
  guage models meet personalization.                        ACM.
Yunfan Shao, Linyang Li, Junqi Dai, and Xipeng Qiu.
                                                          Mo Yu, Yisi Sang, Kangsheng Pu, Zekai Wei, Han
  2023. Character-llm: A trainable agent for role-
                                                           Wang, Jing Li, Yue Yu, and Jie Zhou. 2022. Few-shot
  playing. arXiv preprint arXiv:2310.10158.
                                                           character understanding in movies as an assessment
Michael Stephen Silk. 2002. Aristophanes and the Defi-     to meta-learning of theory-of-mind. arXiv preprint
  nition of Comedy. Oxford University Press, USA.          arXiv:2211.04684.
Alan Sommerstein. 2013. Aristophanes. The Encyclo-        Samira Zad, Maryam Heidari, H James Jr, and Ozlem
  pedia of Ancient History.                                 Uzuner. 2021. Emotion detection of textual data: An
                                                            interdisciplinary survey. In 2021 IEEE World AI IoT
Sanja Štajner and Seren Yenikent. 2020. A survey of         Congress (AIIoT), pages 0255–0261. IEEE.
  automatic personality detection from texts. In Pro-
  ceedings of the 28th international conference on com-   Jinfeng Zhou, Zhuang Chen, Dazhen Wan, Bosi Wen,
  putational linguistics, pages 6284–6295.                   Yi Song, Jifan Yu, Yongkang Huang, Libiao Peng,
                                                             Jiaming Yang, Xiyao Xiao, et al. 2023. Character-
Xiaoli Tang, Tengyun Wang, Haizhi Yang, and                  glm: Customizing chinese conversational ai char-
  Hengjie Song. 2019. Akupm: Attention-enhanced              acters with large language models. arXiv preprint
  knowledge-aware user preference model for rec-             arXiv:2311.16832.
  ommendation. In Proceedings of the 25th ACM
  SIGKDD international conference on knowledge dis-
  covery & data mining, pages 1891–1899.
Gemini Team. 2024a. Gemini: A family of highly
  capable multimodal models.
Meta LLaMA Team. 2024b. Introducing meta llama
 3: The most capable openly available llm to date.
 https://ai.meta.com/blog/meta-llama-3/.
 Accessed: 2023-10-03.
Hugo Touvron, Louis Martin, Kevin Stone, Peter Al-
  bert, and Amjad Almahairi. 2023. Llama 2: Open
  foundation and fine-tuned chat models.
Xintao Wang, Yunze Xiao, Jen tse Huang, Siyu Yuan,
  Rui Xu, Haoran Guo, Quan Tu, Yaying Fei, Ziang
  Leng, Wei Wang, Jiangjie Chen, Cheng Li, and
  Yanghua Xiao. 2024. Incharacter: Evaluating per-
  sonality fidelity in role-playing agents through psy-
  chological interviews.
```

<!-- page: 11 -->
```text
A     Prompts                                           B     Dateset Details
In this section, we provide the key prompts we used,    B.1    Categories of novel
including prompts for selecting decision points,
locating the node’s position, constructing multiple-    Below is a complete classification of novel gen-
choice questions, system prompts for role-playing       res, from the literary experts at the Supersummary
characters, and the prompt of C HAR M AP.               website:
                                                            Mystery Novels: The mystery genre includes
A.1    Selecting Decision Points                        general mystery, noir mystery, historical mystery,
As mentioned in section 3.1, the first step in con-     police procedural mystery, and supernatural mys-
structing our data is selecting the character’s Deci-   tery.
sion Points. In this step, our input data consists of       Thriller Novels: The thriller genre includes su-
all raw data from Supersummary, including the cur-      pernatural thrillers, historical thrillers, environmen-
rent character’s description, all chapter summaries,    tal thrillers, medical thrillers, legal thrillers, po-
and book analysis. We provide this data to GPT-         litical thrillers, military thrillers, and espionage
4, requesting it to output three types of data: the     stories.
character’s decision point, the corresponding gold          Science Fiction Novels: Science fiction stories
motivation, and the chapters related to this choice.    take place in the future or the past but are almost al-
Table 6 presents the character’s description, Ta-       ways set in a dimension different from our present.
ble 7 presents the chapter summaries, and Table 8       They are characterized by entirely new, imagined
presents the book analysis, these examples are all      realities and universes, where the setting is indis-
from the 2024 novel "A Calamity of Souls.". Ta-         pensable. High technology also plays an important
ble 9 illustrates the prompt for selecting decision     role in these stories. Space opera, romantic science
points.                                                 fiction, military science fiction, alternate history,
                                                        dystopian and utopian tales, as well as steampunk,
A.2    Locating the Node
                                                        are considered sub-genres of science fiction.
Furthermore, for the character’s decision points,           Romance Novels: Romance novels feature ro-
we provide the previously identified decision points    mantic relationships between at least two people,
and their corresponding chapters from the original      characterized by tension and desire. Romance
book to GPT-4. This allows it to precisely deter-       novel themes include supernatural romance, con-
mine the position in the original book that should      temporary romance, historical romance, western
be segmented, helping to avoid data leakage. Table      romance, gothic romance, regency romance, and
10 shows the prompt for locating.                       romantic suspense.
A.3    Constructing Multiple-Choice Questions               Fantasy Novels: Fantasy stories are centered
                                                        around mythical kingdoms and magic. Fantasy
After selecting the Decision Points, our next step
                                                        novel genres include contemporary fantasy, tradi-
is constructing Multiple-Choice Questions. In this
                                                        tional fantasy, horror fantasy, weird fantasy, epic
step, our input data consists of all the input and
                                                        fantasy, historical fantasy, dark fantasy, urban fan-
output data from the previous step(Appendix A.1).
                                                        tasy, and anime fantasy.
We ask GPT-4 to construct a multiple-choice ques-
                                                            Action Adventure Novels: Action-adventure
tion regarding the character’s decision based on this
                                                        novels place the protagonist in various realistic dan-
data, outputting the scenario in which the character
                                                        gers. This is a fast-paced genre where the climax
is situated, the question, and four options. Table 11
                                                        should provide some form of thrill for the audience
shows the prompt for constructing multiple-choice
                                                        or reader.
questions.
                                                            Speculative Novels: Speculative fiction is char-
A.4    System Prompts for Role-Playing                  acterized by overlapping with our world but differ-
The prompt for role-playing as the character can be     ing in key aspects, introducing "what if" scenarios.
found in Table 12.                                          Mystery Thriller Novels: Mystery thriller sto-
                                                        ries are usually filled with suspense, with one or
A.5    Prompts for C HAR M AP                           more characters’ lives in danger. In gripping scenes,
The prompt for C HAR M AP can be found in Table         these characters are often chased and manage to
13.                                                     escape narrowly.
```

<!-- page: 12 -->
```text
   Young Adult Novels: Young Adult fiction, com-          B.2   Categories of motivations
monly abbreviated as YA, is intended for teenagers        Below are the motivations for each topic and their
aged 12-18. Most YA novels feature coming-of-             corresponding proportions:
age stories, often with elements of science fiction
or fantasy.                                               Character-driven motivation Character-driven
                                                          narrative is centered on the inner world, growth,
   New Adult Novels: New Adult novels target
                                                          and transformation of characters. In character-
college-aged adults and usually explore stories of
                                                          driven stories, the progression of the plot and the
first adventures on one’s own.
                                                          resolution of conflicts are often propelled by the
  Horror and Supernatural Novels: Horror, su-             characters’ personalities, desires, fears, and psycho-
pernatural, and ghost story genres aim to scare           logical development. Such stories typically delve
the reader and audience by playing on common              deeply into the characters’ mental states and de-
fears. The protagonist usually has to overcome            velopment, focusing on how characters influence
supernatural threats, and the stories often include       each other and how their actions reflect their inner
supernatural elements.                                    emotions and thoughts. The choices and changes
   Crime Mystery Novels: Crime mystery sto-               of the characters serve as the main engine for the
ries focus on a central problem or crime to be            story’s development, influencing the direction of
solved, or a mysterious event that must be an-            the plot. Sub-motivations of character-driven be-
swered. Throughout the story, the reader or au-           havior include:
dience and characters are given clues that help the          Personality and Traits: (27.12%) These refer
protagonist eventually find the solution.                 to a character’s characteristics such as being intro-
                                                          verted, extroverted, brave, or guilt-ridden, which
  Detective Novels: In detective fiction, a com-          influence their choices and lifestyle.
mon element is a police officer or detective embark-         Emotions and Psychological State: (7.53%)
ing on solving a crime. The plot is filled with evi-      A character’s emotional responses, psychological
dence gathering, forensic studies, and legal drama.       traumas, or sense of personal well-being are key
   Historical Novels: Historical novels are fic-          elements that drive the story forward.
tional stories set against the backdrop of real histor-      Social Relationships: (6.31%) The character’s
ical events or historical settings. Historical fiction    status and changes in family, love, friendship, or
may also portray real historical figures.                 other social connections can propel the story’s de-
                                                          velopment.
   Western Novels: Stories with a western theme
                                                             Values and Beliefs: (27.12%) The character’s
take place in the old times of the American
                                                          moral convictions, religious beliefs, or life philoso-
West, filled with adventure, cowboys, and pioneers.
                                                          phy can serve as motivation for action.
There are also Italian western novels, Asian west-
ern novels, space westerns, and other stories about          Desires and Goals: (7.22%) Personal desires,
the American West.                                        career aspirations, or specific life goals of a charac-
                                                          ter are pivotal in advancing the plot.
   Family Saga Novels: Family saga novels typi-
cally tell the stories of several generations of family   Plot-driven motivation Plot-driven narrative em-
members dealing with family affairs, family curses,       phasizes the creation and resolution of external
and family adventures. These stories usually follow       conflicts in the story. In such stories, the driving
a timeline and deal with conflicts in the present.        force of the plot comes from a series of events
                                                          and conflicts themselves, while characters are often
  Women’s Novels: Women’s fiction plotlines re-
                                                          the responders to these events. Plot-driven sto-
volve around the challenges and crises that women
                                                          ries typically highlight tense drama, complex plot
face in real life, including interpersonal relation-
                                                          structure, and frequent changes in external actions,
ships, work, family, politics, and religion.
                                                          rather than changes in the character’s internal world.
   Magical Realism Novels: Magical realism sto-           In this type of narrative, characters may act in re-
ries take place in the real world but have characters     sponse to the demands of the plot, rather than the
who take magical elements for granted. These mys-         plot following the development of the characters’
tical elements do not exist in real life, but they are    inner world. Sub-motivations of plot-driven behav-
perfectly normal in magical realism.                      ior include:
```

<!-- page: 13 -->
```text
   External Conflicts: (8.76%) Conflicts from the          human behavior data do not have this issue.
outside world, such as war, natural disasters, or          How to construct real human historical data
social upheaval, can propel the plot.                      and related behavior data is a question worth
   Tasks and Goals: (4.7%) Tasks or specific goals         exploring.
that characters must accomplish often become the
driving force behind the story’s progression.            • Improving RPLA performance in life
   Puzzles and Secrets: (7.22%) Secrets that need          choices Although C HAR M AP has achieved
revealing or mysteries that need solving can form          decent results, better methods are needed to
the core of a story.                                       balance reasoning efficiency (which depends
   Pursuits and Escapes: (4.25%) Characters                on the length of input tokens) while achieving
might chase something (e.g., power, wealth, knowl-         superior outcomes.
edge) while avoiding or fleeing from certain situa-      • More complex downstream decision tasks
tions (e.g., pursuit, personal past).                      The decisions we select are often significant
   Exploration and Discovery: (3.66%) Charac-              choices for fictional characters, resulting in
ters’ adventures or discoveries in new realms (phys-       a large decision space without a fixed task
ical, scientific, or spiritual) can move the plot for-     framework. Identifying more systematic tasks
ward.                                                      by integrating social sciences is a challenge
   Power and Control: (4.81%) The pursuit or               that needs to be addressed in the future.
struggle for power and control often serves as mo-
tivation for characters.
   Intrigue and Betrayal: (4.09%) Complex plots
and betrayals can catalyze the progression of the
story.
C    Manual Annotation
This is a supplement to Section 3.1. After construct-
ing the multiple-choice question data using GPT-4,
we perform a manual examination. For each annota-
tor, we provide key character descriptions, chapter
summaries, and book analyses written by human
literature experts on the Supersummary. Each an-
notator is asked to score the questions constructed
by GPT-4 based on the rules shown in Table 14.
We evaluated the scores of each annotator and only
retained the data with an average score of more
than 6 points.
   We provide compensation based on the local
minimum hourly wage for all individuals involved
in the annotation.
D    Future direction
Building personal agents for everyone is an excit-
ing topic. We have explored how fictional charac-
ters can determine their subsequent actions based
on historical data, proposing possibilities for com-
bining role-playing with personalized models. We
believe there are additional directions to explore:
    • Real-life version of L IFE C HOICE The be-
      havior of fictional characters often stems from
      the author’s design, which can lead to logi-
      cal inconsistencies. In contrast, real-world
```

<!-- page: 14 -->
```text
Key Character Description
John “Jack” Robert Lee
Jack Lee is the protagonist of the novel. He is a white man who sees himself as intelligent, having done well in school and
having a law degree, yet he acknowledges that he did not go to one of the best law schools. At the beginning of the novel, he
turns 33. He is disappointed with much of his professional life, having been out of law school for eight years and still “just
getting by.” He believes that he has largely failed to “change” the world—which was one of his goals in becoming a lawyer.
Jack changes throughout the course of the novel. At the novel’s start, he recognizes the racist actions of his mother and is glad
that segregation is ending. Because of his love of books, he has a vast knowledge of Black history and the hardships that Black
people have faced, yet he largely ignores these hardships, as they do not affect him. He largely chooses to go along with the
way things are and scolds himself for not being a “risk-taker.”
However, he realizes how very real injustice is for people like Jerome. He sets aside his own fear and faces the danger of
representing him. Initially, he dislikes DuBose’s interest in speaking with the press and trying to use Jerome’s trial as anything
other than a chance to save Jerome’s life. By contrast, he speaks to the press for the first time at the novel’s conclusion, making
an impassioned plea about the importance of coming together as a community.
Desiree DuBose
DuBose is a Black lawyer from Chicago. She is extremely intelligent, having gone to college at age 16 and graduated from
Yale Law School after six years. She has a vast array of experience working with the NAACP and the Legal Defense Fund to
fight against racist legislation, including, mostly recently, the Loving case, which allowed Black and white people to marry.
She initially comes to Virginia with the intent of taking over Jerome’s defense from Jack, but after seeing how committed he is
to the case, agrees to be his co-counselor.
DuBose contrasts with Jack. She is very different from him on the surface level: She is a woman, Black, has served as a lawyer
in dozens of murder trials, and recognizes the importance of Jerome’s trial to the larger picture of civil rights. However, like
Jack, she is a dynamic character in that she changes throughout the text. After Jack asks her to stop focusing on mistrials
or appeals, she brings her full effort to the courtroom, fighting back against the admission of the murder weapon instead of
utilizing it as a chance for appeal. At the novel’s end, she succeeds in Overcoming Personal Bias by putting aside her fear and
hesitancy to be involved with Jack.
Jerome Washington
Jerome is a Black man on trial for the murder of a wealthy white couple. He is a veteran of the Vietnam War and is described
as “large” and “strong,” standing at 6’5”. He is dedicated to his job working for the Randolphs before their death, riding his
bike five miles each way, never missing a day of work. He is also dedicated to his wife, Pearl and three children; he is willing
to go to prison for life if it means that Pearl is able to be acquitted, then is willing to accept a plea of five years, despite the
overwhelming evidence in his favor.
Jerome is a flat character in the novel, meaning that he doesn’t change. He serves primarily as a plot device and a way to
illuminate the systemic racism and injustice of the time. The narrative offers little information about him. Jack and DuBose
repeatedly choose not to put him on the stand to speak even in his own defense. This reflects his status in 1968 in the American
South: He has little control of his own life, and is at the mercy of the white people around him and the racist system that he
lives in.
Hilda “Hilly” Lee
Hilly is Jack’s mother. A homemaker, she cares for her developmentally disabled daughter, Lucy, well into adulthood while
blaming herself for Lucy’s disability. Jack sees his mother as “complicated.” Hilly is upset by Martin Luther King Jr.’s death
and helps the Black men who work with her husband, yet is vocal about her belief in segregation. DuBose initially thinks of
her as a “typical racist” who perpetuates the idea that she is somehow superior to the Black people around her.
Hilly is a dynamic character who changes throughout the text. As the text progresses, she reverts back to who she originally
was prior to the events of the novel—a kind, nonracist woman. After Lucy’s death, she gets to know DuBose and invites her
into her home, even lending her clothing. She then reveals that she used to love a Black man, but was forced apart from him.
Additionally, she was told by a preacher that Lucy’s disability was a punishment from God in return for loving a Black man.
These experiences, and the society in which she lived, led to years of acceptance and eventual perpetuation of racism.
Howard Pickett
Pickett is an antagonist, or villain, of the text. He is a wealthy man who owns coal mines. He is interested in the trial because
he wants to use it as a talking point for campaigning for George Wallace to win the 1968 presidential election. Throughout the
novel, he speaks with the presses and stresses the importance of Jerome being convicted to emphasize that segregation should
be legalized again. Whether Pickett believes these ideas or is simply using them to drum up support for his political campaign
is unclear; however, he is unapologetic in his racist language. Pickett represents the true problems that need to be addressed in
America. He distracts the working class by perpetuating racism and stressing that Black people are the problem. In this way,
he masks that the true issue lies with the greed and theft of the wealthy from the working class.
Table 6: Data examples of key character descriptions written by literary experts, sourced from the 2024 novel "A
Calamity of Souls."
```

<!-- page: 15 -->
```text
Chapter Summaries
Chapter 1
In Freeman County, Virginia, in 1968, an elderly white couple is dead in their home. The husband is sprawled across the floor,
while his wife’s body lays across a chair.
Two white officers—Raymond Leroy and Gene Taliaferro —have a Black man in handcuffs on the floor, referred to as “the
only suspect in the room”. Raymond struggles to read him his Miranda Rights off an index card, a new policy recently enacted
in the police force. The idea of reading them annoys both Raymond and Gene, who are bothered by the idea of criminals
getting representation, especially “those people, who had committed crimes, usually against white folks”.
Gene interrupts Raymond to hit the suspect with his club. He forces the suspect to lie down, then hits him again, then forces
him to kneel again. Gene goads the suspect into getting angry by asking him about his wife and family. When the suspect
reacts with rage, struggling against his handcuffs, Gene is excited that he can now claim the suspect was “resistin’ arrest” and
raises his club to beat him.
Chapter 2
John “Jack” Robert Lee is a lawyer from Freeman County, Virginia. He is white, single, and grew up in a working-class home,
with a love of books and debate.
He arrives at his parents’ home to celebrate his 33rd birthday. He is greeted by his older sister, Lucy. She is 37. Due to their
mother’s exposure to nitrous oxide at the dentist while pregnant, she is developmentally disabled.
Chapter 3
Jack’s mother, Hilda “Hilly” Lee,” has always cared for her home and children while her husband works. She harbors some
guilt over what happened to Lucy and chose to have Jack and his younger brother, Jefferson, without any pain killers—even
aspirin. She refers to Jack as “Robert,” insisting that she would have named him Robert E. Lee if her husband did not have a
say.
Despite naming her son after a Confederate general, she still tells Jack how upset she is about the deaths of Martin Luther King
Jr. and Robert Kennedy. Jack finds it “bewildering” that she respects Lee while mourning King and Kennedy, who “held views
diametrically opposed to all the Confederacy had stood for.”
Hilly tells Jack that Miss Jessup was by earlier looking for him. Miss Jessup is one of the only Black women in the area. She is
a housemaid to Ashby, a wealthy, retired lawyer who lives down the street.
...
Chapter 93
Jack flies to Chicago. On the flight there he thinks of his injury, and how the bullet barely missed doing any major damage. He
was also saved by the fact that the bullet went through Jerome first; he is saddened that he can’t thank him for saving his life.
He goes to DuBose’s apartment and surprises her. He tells her that he came to Chicago to work with her. She makes it clear
that they can only work together, and Jack admits that he cares for her. She tells him that she once lost a man she loved because
of the work that they do, and she can’t go through that pain again. She compares it to Jack’s losing Lucy. Jack insists that
meeting her was one of the best things that ever happened to him, despite the damage it caused.
When DuBose doesn’t respond, Jack turns to leave. She stops him, informing him that it will “be far tougher” than he thinks.
Jack insists that he is now “far tougher” than he thought he would ever be.
Table 7: Data examples of chapter summaries written by literary experts, sourced from the 2024 novel "A Calamity
of Souls."
```

<!-- page: 16 -->
```text
Book Analysis
Chapters 1-10
The setting of the novel plays a pivotal role. Set in the South in 1968, the country is on the verge of moving forward with the
end of Jim Crow and segregation, while people throughout the South fight against this. The novel discusses the death of Martin
Luther King Jr. as well as the impending presidential election as important moments in history that will decide the future.
Jack battles with Overcoming Personal Bias. He has grown up in the South with a mother who encourages segregation, and up
until the novel’s events, has not fought against racism or done something meaningful with his law degree. He struggles with
his lack of action, and contemplates whether the danger that will surround the trial is worth it. He recognizes his own bias, and
doesn’t yet see racism as an important enough cause to risk his safety. However, when he is confronted with violence on taking
Jerome’s case, it has the opposite of its intended effect: Instead of discouraging him from defending Jerome, the violence
shows him that he has ignored the problem of racism for too long.
Jack’s mother, Hilly, represents a vast number of white people throughout the South. She is complex: She helps Black people in
need but also adamantly believes in segregation. This reflects the beliefs that many people hold throughout the novel. Although
she does not believe herself to be racist and feels sympathy for Black people, she still exhibits racist biases and does not want
to go against the status quo. Like Jack, she battles with her own personal bias and reflects on whether change is truly needed.
This section begins to examine Racial Injustice and the Legal System. The text reveals the limits that the legal system has
when placed in the hands of racist people. Despite what the law says, people continue to perpetuate racism, both directly and
indirectly. This reveals that the law struggles without the support of its people.
David Baldacci raises the stakes surrounding the trial to build suspense. For example, after registering himself as Jerome’s
lawyer, Jack receives a frightening phone call. The call reinforces his belief that he is doing the right thing using his legal
skills, and that he is fighting back against injustice. It foreshadows the danger that will surround the case for both Jack and the
people in his life.
...
Chapters 76-93
This section continues to examine The Importance of Family and Community Support. Like with Lucy’s death, the end of the
trial and Jerome’s murder act as catalysts, where people of different races come together. For instance, Jerome’s funeral is
attended by many white people who did not even know him. Additionally, Jack notes that people agree with his speech after
the trial. In this way, the novel implies hope for the future, and suggests that racial injustice can be overcome.
This section continues to examine Racial Injustice and the Legal System. Jack and DuBose prove several things throughout the
trial: Several of the witnesses for the prosecution were pressured into giving false statements, Pearl could not have helped with
the crime, and Jerome could not have committed the murder due to his injury. Despite all of this, they are still not able to
get the case thrown out and are forced to consider the best plea deal Battle can offer—which still involves Jerome going to
prison. This blatant injustice reflects just how unfair the legal system was for Black people in the 1960s—and how little it did
to defend their rights, even when the law is on their side.
When Jerome is killed, the injustice of the legal system is further illuminated. As Jerome lies dying, several policemen do not
stop the shooter with force, and instead try to talk to him. Even after the shooter raises his gun to shoot Pearl, Jeff, a civilian,
shoots him. As he does so, the policeman angrily asks: “Why in the hell did you shoot him?”. The police’s rage and inaction
reflects how little the legal system does to enforce even the laws in place.
This scene establishes the importance of Overcoming Personal Bias. Jerome’s death makes it clear that even new laws and
courtroom triumphs are not enough for true change when people act on their own prejudice. Individual people need to change
if racism is going to be overcome.
The novel again presents youth as a solution to combatting personal bias. As Jack gives his speech at the conclusion of the
trial, he notes how a woman in the crowd “was looking angrily” at him, but that “her boy’s expression was more muted; he
actually appeared to be listening”. In this way, the novel shows that the youth are amenable to change. Although the grown
woman is angry at the outcome of the trial, there is hope for the future—her son seems to be internalizing what Jack is saying,
forming his own opinions instead of perpetuating his mother’s bias.
Jack and DuBose overcome their own personal bias and agree to start a romantic relationship. Their hesitancy has been
two-fold. First, they both reflect throughout the novel on how complicated it would be to be with someone of a different race,
with DuBose scolding herself for considering it. Second, their hesitancy comes from the fact that they come from such different
backgrounds. However, Jack acknowledges that, just like with the trial, there are things that are worth the fight, and he believes
that their relationship is one. He tells DuBose that he is “far tougher than [he] thought [he] would be”, reflecting his newfound
internal strength and transformation.
Table 8: Data examples of book analysis written by literary experts, sourced from the 2024 novel "A Calamity of
Souls."
```

<!-- page: 17 -->
```text
                                                           Prompt I
Your task is to help me identify a significant decision point for a character in a book. I will provide you with some
information, and you need to return outputs as required.
# Requirements:
1. The decision must be a life choice of the character, which can reflect the character’s personality, past experiences, and
interpersonal relationships.
2. The decision must have a rationale that can be found earlier in the text, which might be determined by the character’s
overall personality or by a subtle hint.
3. The decision is determined by earlier text, not revealed by reasons in later sections.
Below are the inputs I will provide and the outputs you need to return.
# Inputs:
1. Input 1: A character description written by a human literature expert
<description>
2. Input 2: The book divided into chapter summaries
<chapter>
3. Input 3: A book analysis written by a human literature expert
<analysis>
Below is the content you need to output.
# Outputs:
1. Output 1: The location of the character’s decision point. Please answer with the original text from the chapter
summaries (Input 2).
Output format: {"summary_location":<content>}
2. Output 2: The motivation for the character’s decision.
Output format: {"motivation":<content>}
3. Output 3: Chapter numbers related to this decision.
Output format: {"related_chapter":["chapter_1",...]}
# Execution Steps:
1. Read all inputs.
2. Consider the life choice that best reflects the character’s personality, past experiences, and interpersonal relationships.
3. Output the location of the character’s decision point, sourced from the chapter summaries (Input 2).
4. Output the motivation for the character’s decision.
5. Based on the motivation for the character’s decision, find the relevant chapters and output the chapter numbers related to
this decision.
                               Table 9: Prompt templates for selecting decision points.
                                                         Prompt II
  Your task is to locate the position of a given segment of text within the original text. The text segment I provide to you
  comes from a summary of the original text. I will provide you with the summary and the original text, and you need to
  find where this segment occurs in the original text. This position must be the exact wording from the original text.
  # Inputs:
  1. Input 1: Summary of the original text
  <summary>
  2. Input 2: Text from the summary of the original text
  <text>
  3. Input 3: Original text
  <original>
  # Outputs:
  Text from the summary of the original text:
                                                              .
               Table 10: Prompt templates for locate the position of the node in the original book.
```

<!-- page: 18 -->
```text
                                                     Prompt III
Your task is to create a multiple-choice question where the correct answer is a decision made by a character in a book.
You need to design three incorrect answers. Below are the detailed requirements:
# Requirements:
1. Provide the scenario in which the character is making the decision.
2. Design three incorrect answers that are reasonable and could be choices the character might make, but are not the
optimal choice.
3. Ensure that there is no data leakage in any of the outputs.
# Inputs:
1. Input 1: Character description written by a human literature expert
<description>
2. Input 2: Summary of the entire book divided by chapters
<chapter>
3. Input 3: Book analysis written by a human literature expert
<analysis>
4. Input 4: Location of the character’s decision point
<location>
5. Input 5: Motivation for the character’s decision
<motivation>
6. Input 6: Original text from chapters related to the decision
<original>
# Outputs:
1. Output 1: Scenario in which the character is situated.
Output format: {"scenario":<content>}
2. Output 2: Multiple-choice question.
Output format: {"question":<q>,"options":{"A":<o1>,"B":<o2>,"C":<o3>,"D":<o4>}}
# Execution Steps:
1. Read all inputs.
2. Output the scenario in which the character faces this decision.
3. Output the multiple-choice question, ensuring that the incorrect options are also reasonable.
                    Table 11: Prompt templates for constructing multiple-choice questions.
                                                       Prompt IV
Please play the role of <Character A> based on the <Profile> and make your life choice under the <Scenario> regarding
<Question>. Return the option letter (A, B, C, or D) that your character should most appropriately choose in the current
scenario. The <Profile> consists of <Description> and <Memory>, where <Description> is an overall description of the
character, and <Memory> consists of specific events the character has experienced.
# Inputs:
1. Profile:
1.1. Description
<description>
1.2. Memory
<memory>
2. Scenario:
<scenario>
3. Question:
<question>
4. Options:
<option>
# Outputs:
Your choice(A, B, C, or D):
                          Table 12: Prompt templates for role-playing as the character.
```

<!-- page: 19 -->
```text
                                                             Prompt V
Your task is to find segments within a character’s <Description> that may relate to the content of the <Scenario> and <Question>.
The <Scenario> describes the situation the character is in, and the <Question> asks what choice the character should make. The
segments you need to find could influence the character’s motivation for making their choice, including aspects that shape the
character’s personality and foreshadowing related to the decision scenario.
# Inputs:
1. Description:
<description>
2. Scenario:
<scenario>
3. Question:
<question>
# Outputs:
Segments that may influence the character’s choice:
                                     Table 13: Prompt templates for C HAR M AP.
```

<!-- page: 20 -->
```text
                                                  Manual Examination Rules
1. Comprehensiveness
Rule:
Evaluators must ensure that each multiple-choice question fully considers the character’s background, context, and motivation.
The questions should reflect the true decisions and experiences of the character within the narrative.
Scoring Guide:
Score 2 (Excellent): The question is detailed and comprehensive, aligning perfectly with the character’s background and
motivation.
Score 1 (Average): The question aligns generally but is missing key aspects of the character’s background information or
motivational nuances.
Score 1 (Poor): The question significantly misaligns with the character’s background or motivation.
2. Logical Consistency
Rule:
Evaluators should assess the internal consistency and plausibility of the question within the narrative thread. The content and
structure of the multiple-choice question must be consistent with the plot and the character’s logical decision-making process.
Scoring Guide:
Score 2 (Excellent): The question is entirely consistent with the character’s known decisions and the structure of the plot.
Score 1 (Average): The question is generally consistent but has minor inconsistencies in detail.
Score 0 (Poor): The question is logically inconsistent with the character’s known decisions or the structure of the plot.
3. Challenge Level
Rule:
Evaluators need to assess the plausibility of the incorrect options. Wrong options should be reasonably believable and
attractive within the constraints of the character’s background and motivations, making the questions sufficiently challenging.
Scoring Guide:
Score 2 (Excellent): All incorrect options are highly plausible and convincingly misleading.
Score 1 (Average): Most incorrect options are reasonable, but one or two lack plausibility.
Score 0 (Poor): Incorrect options are obviously illogical and lack the ability to mislead.
4. Alignment with Character Motivation
Rule:
Evaluators must assess whether the question correctly guides the testing model to step into the role and make a choice, i.e.,
testing if the model can replicate the real storyline’s choices. It is crucial that the character’s motivations, as articulated by
literary experts, are a central component reflected in these questions.
Scoring Guide:
Score 2 (Excellent): The question unambiguously points to a specific character decision point, accurately testing the model’s
ability to role-play.
Score 1 (Average): The question points to a character decision point to some extent, but the indicators are not clear enough,
potentially reducing the accuracy of the model’s role-playing test.
Score 0 (Poor): The question fails to clearly define the character decision point, unable to test the model’s role-playing ability
effectively.
Additional Notes:
1. Before starting the evaluation, each evaluator must understand the core motives and development axes of the character by
reading summaries and analyses of the novels created by literary experts.
2. Ensure that evaluators are familiar with all background material before scoring any questions.
3. Evaluators should reference the analyses by literary experts of the characters to evaluate each of GPT-4’s multiple-choice
questions, maintaining consistency of standards.
4. Application of the evaluation rules should be flexible and adapted to the specific context; scoring standards may be adjusted
for special cases.
        Table 14: Guidelines for Manual Examination of Multiple-Choice Questions in Literary Analysis.
```
