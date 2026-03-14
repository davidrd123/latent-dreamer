# OCC Manual Review

Image-reviewed companion notes for the key pages in `OCC/`.

This paper is short and the text layer is mostly usable. The image review mainly matters for the two hierarchy diagrams and for keeping the clarifications tied to the exact restructuring the authors propose.

<!-- page: 1 -->
## Page 1

- Title: `The OCC Model Revisited`
- Authors: Bas R. Steunebrink, Mehdi Dastani, and John-Jules Ch. Meyer.

Abstract, stabilized:
- The paper argues that the OCC model is useful but logically ambiguous in ways that hinder faithful implementation.
- Its contribution is not a new emotion theory from scratch.
- Instead, it identifies ambiguities, clarifies them, and proposes a cleaner inheritance-based logical structure for the OCC emotion taxonomy.

<!-- page: 2 -->
## Page 2

Figure 1, image-reviewed:
- The original OCC diagram is organized under a top-level `valenced reaction` split.
- It branches into reactions to:
- consequences of events,
- actions of agents,
- aspects of objects.
- The event branch further divides along self/other and prospective/actual lines.
- The action branch divides around self/other action.
- The object branch has `liking/disliking` and `love/hate`.
- Compound emotions such as `gratification`, `remorse`, `gratitude`, and `anger` sit under linked event/action structure.

Core taxonomy claim:
- The OCC model classifies `22 emotion types`.
- The three major branches are:
- consequences of events,
- actions of agents,
- aspects of objects.
- Some compound emotions bridge branches.

Fear example:
- The paper reproduces OCC’s fear specification:
- fear emotions are “(displeased about) the prospect of an undesirable event.”
- It lists undesirable degree and likelihood as intensity variables.

<!-- page: 3 -->
## Page 3

Table 1 summary:
- The paper reproduces compact type specifications for all 22 OCC emotion types.
- The format has three parts:
- a concise eliciting-condition sentence,
- example lexical tokens,
- variables affecting intensity.

Representative structure:
- Event emotions:
- `joy`, `distress`, `hope`, `fear`, `satisfaction`, `fears-confirmed`, `relief`, `disappointment`, plus the fortunes-of-others group.
- Action emotions:
- `pride`, `shame`, `admiration`, `reproach`.
- Compound action-plus-consequence emotions:
- `gratification`, `remorse`, `gratitude`, `anger`.
- Object emotions:
- `love`, `hate`.

Why the paper matters:
- It is useful precisely because it compresses the whole OCC taxonomy into implementation-facing sentences that can be mapped onto agent state and appraisal variables.

<!-- page: 4 -->
## Page 4

Major clarification themes begin here:
- `desirable event` should really be read as `desirable consequence of an event`.
- `prospect` is ambiguous in OCC and is used for both future events and uncertain past/current events.
- The top labels in the original hierarchy (`pleased/displeased`, `approving/disapproving`, `liking/disliking`) behave more like generalized parent types than like mere informal headings.

Interpretive stance:
- The authors explicitly choose to read the model as an inheritance hierarchy because that is the cleanest way to formalize it computationally.

<!-- page: 6 -->
## Page 6

Additional ambiguity clarifications:
- The authors argue that some apparent opposite pairs duplicate positivity/negativity constraints in a way that creates logical redundancy.
- They also argue that `love/hate` need a differentiating condition beyond simple appealingness/unappealingness.
- Their proposal is to use `familiarity` to distinguish:
- `love/hate` for familiar objects,
- `interest/disgust` for unfamiliar objects.

This is one of the paper’s most useful implementation-level moves:
- it turns an awkward branch into a more uniform inheritance story.

<!-- page: 6 -->
## Page 6, revised-model setup

Section `The OCC Model Revisited` states the high-level revision:
- The new hierarchy is meant to make inheritance explicit.
- Child-node conditions should be supersets of parent conditions.
- The authors replace ambiguous phrases like `focusing on` and `prospects (ir)relevant` with clearer inheritance conditions.
- They reclassify several emotions to better align with logical structure rather than with the original presentation diagram.

<!-- page: 7 -->
## Page 7

Figure 2, image-reviewed:
- The revised tree starts from `positive/negative` valenced reaction.
- It then splits into consequence, action, and aspect branches with explicit intermediate nodes.
- Event emotions are separated cleanly into `prospective consequence` and `actual consequence`.
- Action emotions split into `self agent` and `other agent`.
- Object emotions split into `familiar aspect` and `unfamiliar aspect`.

The page also states several concrete restructurings:
- `satisfaction`, `fears-confirmed`, `relief`, and `disappointment` move under `joy/distress`, not under `hope/fear`.
- The fortunes-of-others emotions (`happy-for`, `resentment`, `gloating`, `pity`) move to the bottom-right of the revised hierarchy.
- `interest/disgust` are added as additional specializations of `liking/disliking`, alongside `love/hate`.

Important interpretive claim:
- `relief` is not a specialization of `fear`.
- Instead, it requires an attendant fear emotion while itself being modeled as a specialization under the actual-consequence side of the hierarchy.

<!-- page: 8 -->
## Page 8

Table 2, revised specifications:
- The authors derive the revised type specifications directly from the new hierarchy.
- The table makes the inheritance logic operational.

Notable examples from the revised table:
- `hope`: being pleased about a prospective consequence.
- `fear`: being displeased about a prospective consequence.
- `joy`: being pleased about an actual consequence.
- `distress`: being displeased about an actual consequence.
- `love`: liking a familiar aspect of an object.
- `hate`: disliking a familiar aspect of an object.
- `interest`: liking an unfamiliar aspect of an object.
- `disgust`: disliking an unfamiliar aspect of an object.

Overall takeaway:
- For implementation work, this paper is best treated as a cleanup layer on top of OCC.
- Its real value is not new emotional content but a stricter inheritance structure that removes ambiguity from the appraisal taxonomy.
