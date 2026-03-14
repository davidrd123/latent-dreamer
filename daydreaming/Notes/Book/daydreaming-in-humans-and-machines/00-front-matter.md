# Front Matter

<!-- page: 1 -->

Daydreaming in Humans and Machines

Erik T. Mueller

A Computer Model of the Stream of Thought

<!-- page: 2 -->

Robert Manning Strozier Library

Florida State University

Tallahassee

<!-- page: 3 -->

[Blank page]

<!-- page: 4 -->

Digitized by the Internet Archive in 2024

https://archive.org/details/daydreaminginhum0000muel

<!-- page: 5 -->

Daydreaming in Humans and Machines

A Computer Model of the Stream of Thought

Erik T. Mueller

Ablex Publishing Corporation

Norwood, New Jersey

<!-- page: 6 -->

Robert Manning Strozier Library

Jun 7 1990

Tallahassee, Florida

Cover art © 1989 by Robert E. Mueller.

Cover design by Deb Hoeffner.

Copyright © 1990 by Erik T. Mueller.

All Rights Reserved. No part of this publication may be reproduced, stored in a retrieval system, or transmitted, in any form or by any means, electronic, mechanical, photocopying, microfilming, recording, or otherwise, without permission of the publisher.

Printed in the United States of America.

Library of Congress Cataloging-in-Publication Data

Mueller, Erik T.

Daydreaming in humans and machines.

Bibliography: p.

Includes index.

1. Fantasy, computer simulation.

2. Fantasy.

3. Artificial intelligence.

I. Title.

BF408.M84 1988

154.3

89-6483

ISBN 0-89391-562-9

Ablex Publishing Corporation

355 Chestnut St.

Norwood, NJ 07648

<!-- page: 7 -->

## Contents

### 1. Introduction — 1

- `1.1` The DAYDREAMER Program — `2`
- `1.2` Research Objectives and Issues — `5`
- `1.2.1` Learning from Imagined Experiences — `6`
- `1.2.2` Creative Problem Solving — `12`
- `1.2.3` Emotions — `16`
- `1.3` Foundations of DAYDREAMER — `18`
- `1.3.1` Excluded Phenomena — `18`
- `1.3.2` Definition of Daydreaming — `20`
- `1.4` Daydream Protocol Analysis — `23`
- `1.4.1` Retrospective Reports — `23`
- `1.4.2` Think-Aloud Protocols — `25`
- `1.4.3` Thought Sampling and Event Recording — `26`
- `1.5` Contents of the Book — `27`

### 2. Architecture of DAYDREAMER — 29

- `2.1` Overview of Program — `29`
- `2.2` Daydreaming as Planning — `31`
- `2.2.1` Previous Personal Goal Sets — `31`
- `2.2.2` A Set of Personal Goals — `32`
- `2.2.3` Planning and Inference Rules — `34`
- `2.3` Daydreaming as Multiple Goal Planning — `36`
- `2.4` Daydreaming as Emotion-Driven Planning — `37`
- `2.5` Procedures for Daydreaming — `43`

### 3. Emotions and Daydreaming — 49

- `3.1` Emotional Responses in Daydreaming — `50`
- `3.2` Emotion Representation — `53`
- `3.3` Daydreaming Goals — `57`
- `3.4` Emotional Feedback System — `60`
- `3.5` Rationalization — `63`
- `3.5.1` Rationalization by Mixed Blessing — `64`
- `3.5.2` Rationalization by Hidden Blessing — `65`
- `3.5.3` Rationalization by External Attribution — `66`
- `3.5.4` Rationalization by Minimization — `66`
- `3.6` Revenge — `67`
- `3.6.1` The Purpose of Revenge Daydreams — `68`
- `3.6.2` Achievement of Revenge — `69`
- `3.7` Roving — `70`
- `3.8` Emotions as Motivation — `71`
- `3.9` Idiosyncratic Feeling States — `74`
- `3.10` Previous Related Work in Emotions — `76`
- `3.10.1` Simon's Interruption Mechanism — `76`
- `3.10.2` Colby's PARRY — `76`
- `3.10.3` Abelson's Affect Taxonomy — `77`
- `3.10.4` Weiner's Causal Attribution Model of Emotions — `77`
- `3.10.5` The Emotion Model of Bower and Cohen — `77`
- `3.10.6` Dyer's BORIS Affect Model — `78`
- `3.10.7` Pfeifer's FEELER — `78`
- `3.11` Work Related to Daydreaming Goals — `79`
- `3.11.1` Defense Mechanisms — `79`
- `3.11.2` McDougall's Emotions and Instincts — `79`
- `3.11.3` Festinger's Reduction of Cognitive Dissonance — `80`
- `3.11.4` Abelson's Simulation of Hot Cognition — `80`
- `3.11.5` Colby's Neurosis Model — `81`
- `3.11.6` Janis and Mann's Bolstering Strategies — `82`
- `3.11.7` Summary and Comparison of Strategies — `82`

### 4. Learning through Daydreaming — 85

- `4.1` Episodic Memory and Analogical Planning — `86`
- `4.1.1` Representation of Episodes — `87`
- `4.1.2` Storage of Episodes — `88`
- `4.1.3` Analogical Planning — `89`
- `4.1.4` Issues for Theories of Episodic Memory — `95`
- `4.2` Definition of Learning — `96`
- `4.2.1` Episodes for Improvement of Future Search — `98`
- `4.2.2` Episodes for Changing Knowledge Accessibility — `100`
- `4.2.3` Survival and Growth — `101`
- `4.3` Daydreaming Goals for Learning — `103`
- `4.3.1` Reversal — `103`
- `4.3.2` Rehearsal and Recovery — `107`
- `4.3.3` Repercussions — `108`
- `4.4` Evaluation and Decision Making — `109`
- `4.4.1` Daydream Evaluation Metrics — `109`
- `4.4.2` Daydream Selection — `112`
- `4.5` Intention Formation and Application — `113`
- `4.6` Related Work in Machine Learning — `116`

### 5. Everyday Creativity in Daydreaming — 121

- `5.1` Definition of Creativity — `122`
- `5.2` Aspects of Creativity in Daydreaming — `123`
- `5.2.1` Breaking Away — `123`
- `5.2.2` Fanciful Possibility Generation — `123`
- `5.2.3` Finding New Connections — `124`
- `5.2.4` Mechanisms in DAYDREAMER — `124`
- `5.3` Serendipity in Daydreaming — `125`
- `5.3.1` Serendipity-Based Learning — `126`
- `5.3.2` Serendipity Recognition — `127`
- `5.3.3` Episode-Driven Serendipity — `129`
- `5.3.4` Object-Driven Serendipity — `130`
- `5.3.5` Input-State-Driven Serendipity — `132`
- `5.4` Action Mutation — `132`
- `5.4.1` The Purpose of Action Mutation — `132`
- `5.4.2` Strategies for Action Mutation — `134`
- `5.5` The Phenomena of Incubation and Insight — `134`
- `5.6` Previous Work in Problem Solving — `137`
- `5.6.1` GPS and STRIPS — `137`
- `5.6.2` ABSTRIPS — `139`
- `5.6.3` NOAH — `139`
- `5.6.4` PANDORA — `140`
- `5.6.5` Opportunistic Planning — `144`
- `5.7` Previous Work in Creativity — `144`
- `5.7.1` TALE-SPIN — `144`
- `5.7.2` AM and EURISKO — `146`
- `5.7.3` BACON — `148`

### 6. Daydreaming in the Interpersonal Domain — 151

- `6.1` Basic States and Actions — `152`
- `6.1.1` Conceptual Dependency Representation — `152`
- `6.1.2` Conceptual Dependency in DAYDREAMER — `153`
- `6.2` Other Planning — `153`
- `6.3` Attitudes — `154`
- `6.3.1` Basic Attitude Representation — `155`
- `6.3.2` Assessing, Forming, and Modifying Attitudes — `157`
- `6.3.3` Attitudes and Meeting People — `157`
- `6.4` Interpersonal Relationships — `159`
- `6.4.1` Lovers — `159`
- `6.4.2` Friends — `161`
- `6.4.3` Employment — `161`
- `6.5` Strategies for Fanciful Planning — `162`
- `6.5.1` Plausible Planning — `163`
- `6.5.2` Subgoal Relaxation — `164`
- `6.5.3` Inference Inhibition — `164`

### 7. Implementation of DAYDREAMER — 167

- `7.1` Summary Statistics — `167`
- `7.2` The GATE Representation Language — `169`
- `7.2.1` Slot-Filler Objects — `169`
- `7.2.2` Unification — `170`
- `7.2.3` Instantiation — `172`
- `7.2.4` Variabilization — `172`
- `7.2.5` Contexts — `172`
- `7.3` Planning and Inference Rules — `173`
- `7.3.1` Planning Rules — `174`
- `7.3.2` Inference Rules — `174`
- `7.3.3` Rules for Actions — `175`
- `7.3.4` Rules for Concern Initiation — `177`
- `7.4` Basic Procedures for Daydreaming — `178`
- `7.4.1` Emotion-Driven Control — `178`
- `7.4.2` Planner — `179`
- `7.4.3` Concern Termination — `181`
- `7.4.4` Rule Application — `182`
- `7.4.5` Planning Rule Application — `183`
- `7.4.6` Inference Rule Application — `184`
- `7.4.7` Concern Initiation — `186`
- `7.5` Episodic Memory and Analogical Planning — `186`
- `7.5.1` Episode Storage — `187`
- `7.5.2` Episode Retrieval — `187`
- `7.5.3` Reminding — `188`
- `7.5.4` Revised Planning Rule Application — `189`
- `7.5.5` Analogical Rule Application — `189`
- `7.5.6` Subgoal Creation — `189`
- `7.6` Other Planning — `190`
- `7.6.1` Forward Other Planning — `190`
- `7.6.2` Backward Other Planning — `191`
- `7.7` Serendipity — `192`
- `7.7.1` Serendipity Recognition — `192`
- `7.7.2` Action Mutation — `194`

### 8. Comparison of Episodic Memory Schemes — 195

- `8.1` Discrimination Nets — `195`
- `8.2` Spreading Activation Models — `198`
- `8.3` Connectionism — `203`
- `8.4` Intersection Indexing — `208`

### 9. Review of the Literature on Daydreaming — 211

- `9.1` Previous Work in Daydreaming — `211`
- `9.1.1` Associationist Theories of Thought — `211`
- `9.1.2` James's Stream of Thought — `213`
- `9.1.3` Freud's Theory of Dreaming and Daydreaming — `214`
- `9.1.4` Bleuler's Autistic Thinking — `216`
- `9.1.5` Varendonck's Psychology of Daydreaming — `217`
- `9.1.6` Green's Study of Daydreaming and Development — `219`
- `9.1.7` The Research of Singer and Colleagues — `219`
- `9.1.8` Klinger's Theory of Fantasy — `222`
- `9.1.9` Pope's Studies of the Stream of Thought — `223`
- `9.1.10` Shanon's Investigation of Thought Sequences — `223`
- `9.2` Related Work in Night Dreaming — `224`
- `9.2.1` Foulkes's Scoring System for Latent Structure — `225`
- `9.2.2` The Computer Analogy for Night Dreaming — `226`
- `9.2.3` The Dream Simulation of Moser et al. — `227`
- `9.2.4` Hypnagogic Dreams — `230`
- `9.2.5` Lucid Dreams — `231`

### 10. Underpinnings of a Daydreaming Theory — 233

- `10.1` Philosophical Underpinnings — `233`
- `10.1.1` Functionalist Approach to Mind-Body Problem — `234`
- `10.1.2` Problems with Theories of Subjective Phenomena — `235`
- `10.2` The Problem of Semantics — `239`
- `10.2.1` Structure-Content Distinctions — `239`
- `10.2.2` Semantics of the Stream of Consciousness — `240`
- `10.2.3` The Functionalist Approach to Semantics — `241`
- `10.2.4` The Elusiveness of Concepts — `241`
- `10.2.5` Connectionist Approaches to Semantics — `243`
- `10.3` Scientific Underpinnings — `247`
- `10.3.1` Scientific Objectives and Methodology — `247`
- `10.3.2` Goals and Limits of Artificial Intelligence — `253`
- `10.3.3` Theory Construction, Testing, and Evaluation — `256`
- `10.3.4` Pylyshyn's Strong Equivalence — `258`
- `10.3.5` Discussion — `261`

### 11. Future Work and Conclusions — 263

- `11.1` Eventual Applications and Extensions — `265`
- `11.1.1` Autonomous Robots — `265`
- `11.1.2` Operating Systems — `265`
- `11.1.3` Creative Writing — `266`
- `11.1.4` Conversation — `267`
- `11.1.5` Art and Music — `267`
- `11.1.6` Psychotherapy and Psychiatry — `269`
- `11.1.7` Education and Games — `271`
- `11.2` Shortcomings of the Program — `271`
- `11.3` Overdetermination of Daydreaming — `273`
- `11.3.1` An Overdetermination Mechanism — `274`
- `11.3.2` Potential Benefits — `275`
- `11.3.3` Problems for the Mechanism — `276`
- `11.3.4` Initial Solutions — `278`
- `11.3.5` DAYDREAMER* — `279`
- `11.3.6` Relationship to Previous Work — `280`
- `11.4` Was It Worth It? — `282`

### Back Matter

- `References` — `283`
- `A` Annotated Traces from DAYDREAMER — `303`
- `A.1` LOVERS1 Experience — `303`
- `A.2` REVENGE1 Daydream — `321`
- `A.3` RATIONALIZATION1 Daydream — `325`
- `A.4` RATIONALIZATION2 Daydream — `329`
- `A.5` RATIONALIZATION3 Daydream — `330`
- `A.6` ROVING1 Daydream — `332`
- `A.7` RECOVERY2 Daydream — `333`
- `A.8` RECOVERY3 Daydream — `342`
- `A.9` REVENGE3 Daydream — `347`
- `A.10` COMPUTER-SERENDIPITY — `348`
- `B` English Generation for Daydreaming — `351`
- `B.1` The Generator — `351`
- `B.1.1` The Form of Generational Knowledge — `351`
- `B.1.2` Subject Generation — `352`
- `B.2` Generational Templates — `352`
- `B.2.1` Mental Transfer — `353`
- `B.2.2` Other Actions and States — `353`
- `B.2.3` Goals — `356`
- `B.2.4` Emotions — `357`
- `B.2.5` Attitudes — `359`
- `B.2.6` Beliefs — `359`
- `B.2.7` Objects and Locations — `359`
- `B.2.8` Persons — `360`
- `B.2.9` Other Concepts — `360`
- `B.3` Global Generation Alterations — `361`
- `B.4` Generation Pruning — `362`
- `B.5` Additional Generation — `363`
- `Index` — `365`

<!-- page: 13 -->

## List of Figures

- `2.1` Needs with Subsumption States — `34`
- `2.2` Interleaving of Concerns — `36`
- `2.3` Meta-Planning/Universal Subgoaling — `37`
- `2.4` Emotion-Driven Planning — `38`
- `2.5` Emotions and Concerns 1 — `40`
- `2.6` Emotions and Concerns 2 — `40`
- `2.7` Emotions and Concerns 3 — `41`
- `2.8` Emotions and Concerns 4 — `41`
- `2.9` Emotions and Concerns 5 — `42`
- `2.10` Emotions and Concerns — `42`
- `2.11` DAYDREAMER Procedures — `44`
- `3.1` Goal Taxonomy — `58`
- `3.2` The Emotional Feedback System — `62`
- `3.3` The Emotional Daydreaming Goals — `63`
- `4.1` Planning Tree for REVENGE1 — `88`
- `4.2` Indexing of Episodes by Rules — `89`
- `4.3` Cases in Analogical Planning — `90`
- `4.4` Planning Tree for REVENGE2 — `92`
- `4.5` Planning Tree for REVENGE3 — `92`
- `4.6` Analogical Planning in RECOVERY1 — `93`
- `4.7` Partial Tree for RATIONALIZATION1 — `94`
- `4.8` Making Inaccessible Rules Accessible — `101`
- `4.9` Inference Chain for a Social Regard Failure — `105`
- `5.1` Serendipity — `127`
- `5.2` Rule Intersection for RECOVERY3 — `129`
- `5.3` Verification Episode for RECOVERY3 — `130`
- `5.4` ABSTRIPS vs. DAYDREAMER — `139`
- `5.5` Opportunistic Planning vs. Serendipity — `145`
- `7.1` Basic Procedure Dependencies — `178`
- `7.2` Tree of Planning Contexts — `180`
- `7.3` Personal and Daydreaming Goal Contexts — `186`
- `7.4` Revised Procedure Dependencies — `187`
- `7.5` Further Revised Procedure Dependencies — `192`
- `8.1` Discrimination Net — `196`
- `8.2` Fully Redundant Discrimination Net — `197`
- `8.3` Redundant Discrimination Net with Delayed Expansion — `198`
- `8.4` Network for Spreading Activation — `201`
- `8.5` Connectionist Net for Episodes: First Attempt — `205`
- `8.6` Connectionist Net for Episodes: Second Attempt — `206`
- `8.7` Connectionist Net for Episodes: Third Attempt — `207`
- `8.8` Connectionist Net for Episodes: Fourth Attempt — `207`
- `8.9` Intersection Indexing Net — `209`
- `11.1` Overdetermined Stream of Consciousness — `280`

<!-- page: 15 -->

## List of Tables

- `3.1` Positive Emotions in DAYDREAMER — `54`
- `3.2` Negative Emotions in DAYDREAMER — `54`
- `3.3` Daydreaming Goals — `59`
- `3.4` Comparison of Emotion Regulation Strategies — `83`
- `4.1` Ordering of Retrieved Episodes — `112`

<!-- page: 16 -->

[Blank page]

<!-- page: 17 -->

For Robert and Diana

<!-- page: 18 -->

[Blank page]

<!-- page: 19 -->

## Acknowledgements

This book is a revised version of a dissertation submitted to UCLA in March 1987 for the degree of doctor of philosophy in computer science.

First, I would like to thank my advisor Michael Dyer. This work would not have existed without him. He created the Artificial Intelligence Laboratory at UCLA that enabled me and my fellow students to pursue our research. He encouraged me to pursue daydreaming, a topic few others would have agreed to. In the early stages of my research, he helped me clarify the significance of daydreaming as a subject of investigation. Throughout the almost four years of work on this topic, he provided ideas, guidance, and constant support. I also thank him for emphasizing the importance of presenting our work to the outside world, both at conferences and program demonstration sessions in the lab.

Second, I want to thank the other members of my committee for their valuable comments and suggestions: Margot Flowers, Judea Pearl, Kenneth Mark Colby, and Robert Bjork. Kenneth Mark Colby was especially generous with his time. I thank him for many useful meetings and discussions.

Third, I would like to thank the other members of the lab: Sergio Alvarado, Stephanie August, Charles Dolan, Richard Feifer, Michael Gasser, Seth Goldman, Jack Hodges, Michael Pazzani, Alex Quilici, John Reeves, Ron Sumida, Scott Turner, and Uri Zernik. Uri, Scott, and John read earlier drafts of this book and provided helpful comments. I especially thank Uri for the many interesting and productive discussions we had on our respective research topics.

Fourth, I want to thank Janene McNeil from the UCLA psychology department who supplied me with a collection of daydream diaries she collected from her subjects in a pilot study. I also thank those friends who told me about or sent me their daydreams.

Fifth, for their efforts in keeping the Apollo workstations in the lab up and running, I would like to thank Eve Schooler and Seth Goldman. I also thank Bill Davis, Doris McClure, and other members of the Center for Experimental Computer Science at UCLA for providing additional computing support. For lab administrative assistance, I thank Anna Gibbons and Anne Finestone.

Sixth, I want to thank the following friends during my graduate school years on the West Coast: Mark Joseph, Ann Gardner, Karen Lever, Patty Liu, Andy Lust, Johanna Moore, Raya Palm, Ellen Perlman, Tony Thijssen, and Carrie Young.

Seventh, I would like to thank my grandmother, Ilus Lobl, my great-aunt Ellie Bermowitz, and my great-uncle Jack Bermowitz.

Finally, I want to thank my wonderful parents, Robert Mueller and Diana Mueller, and my fantastic sister, Rachel Mueller-Lust.

This research was supported by a fellowship from Atlantic Richfield, a grant from the Hughes Artificial Intelligence Center, an IBM Faculty Development award, and a grant from the W. M. Keck Foundation.

<!-- page: 20 -->

Preparation of the revision for publication as a book was made possible by the Analytical Proprietary Trading unit of Morgan Stanley & Company in New York.

I would like to thank Barbara Bernstein and Carol Davidson at Ablex for their efforts, and anonymous reviewers for helpful suggestions.
