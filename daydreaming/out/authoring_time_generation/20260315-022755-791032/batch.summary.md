# Authoring-Time Generation Batch Run

- fixture: `daydreaming/fixtures/authoring_time_generation_tessa_toast_rationalization_v1.yaml`
- provider: `gemini`
- model: `gemini-3.1-pro-preview`
- sequences: `5`
- steps per sequence: `3`
- candidates per step: `4`
- pooled accepted nodes: `15`
- admitted nodes: `8` / `8`

## Sequence Summaries

- sequence `01`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `02`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `03`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `04`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `05`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`

## Admitted Nodes

- `beat_rationalize_coatroom` from sequence `04` step `1`
  operator `rationalization`, option_effect `clarify`, score `0.889`
  refs `['ev_toast_credit_cut', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage']`, practices `['confession']`
- `beat_tessa_rationalizes_text_draft` from sequence `05` step `2`
  operator `rationalization`, option_effect `clarify`, score `0.506`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'sit_text_reply_threshold']`, pressures `['status_damage']`, practices `['confession']`
- `beat_tessa_rationalizes_text` from sequence `01` step `1`
  operator `rationalization`, option_effect `clarify`, score `0.447`
  refs `['ev_toast_credit_cut', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage']`, practices `['confession']`
- `beat_tessa_rationalizes_omission_s02_c04` from sequence `02` step `2`
  operator `rationalization`, option_effect `clarify`, score `0.428`
  refs `['ev_toast_credit_cut', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage']`, practices `['confession']`
- `beat_rationalize_simplification` from sequence `03` step `1`
  operator `rationalization`, option_effect `clarify`, score `0.422`
  refs `['ev_toast_credit_cut', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage']`, practices `['confession']`
- `beat_tessa_rationalizes_omission` from sequence `01` step `2`
  operator `rationalization`, option_effect `clarify`, score `0.327`
  refs `['ev_toast_credit_cut', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage']`, practices `['confession']`
- `beat_tessa_rationalize_coatroom_01` from sequence `02` step `3`
  operator `rationalization`, option_effect `clarify`, score `0.322`
  refs `['ev_toast_credit_cut', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage']`, practices `['confession']`
- `beat_rationalize_coatroom_s02_c03` from sequence `04` step `2`
  operator `rationalization`, option_effect `clarify`, score `0.318`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'sit_text_reply_threshold']`, pressures `['status_damage']`, practices `['confession']`

## Rejected Nodes

- `beat_tessa_rationalizes_text_s03_c02` from sequence `01` step `3` rejected by `lower_soft_score`
- `beat_rationalize_coatroom_draft` from sequence `02` step `1` rejected by `lower_soft_score`
- `node_tessa_rationalizes_coatroom` from sequence `03` step `2` rejected by `lower_soft_score`
- `beat_tessa_rationalizes_coatroom` from sequence `03` step `3` rejected by `lower_soft_score`
- `node_tessa_rationalize_coatroom` from sequence `04` step `3` rejected by `lower_soft_score`
- `node_rationalize_coatroom_draft` from sequence `05` step `1` rejected by `lower_soft_score`
- `beat_rationalize_coatroom_draft_s03_c02` from sequence `05` step `3` rejected by `lower_soft_score`