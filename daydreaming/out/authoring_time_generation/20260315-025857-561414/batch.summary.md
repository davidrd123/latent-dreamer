# Authoring-Time Generation Batch Run

- fixture: `daydreaming/fixtures/authoring_time_generation_tessa_toast_rationalization_v1.yaml`
- provider: `gemini`
- model: `gemini-3.1-pro-preview`
- sequences: `5`
- steps per sequence: `3`
- candidates per step: `4`
- pooled accepted nodes: `15`
- admitted nodes: `4` / `8`

## Sequence Summaries

- sequence `01`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `02`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `03`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `04`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `05`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`

## Admitted Nodes

- `beat_rationalize_coatroom` from sequence `01` step `1`
  operator `rationalization`, option_effect `none`, score `0.887`
  refs `['ev_toast_credit_cut', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage']`, practices `['confession']`
- `beat_rationalize_coatroom_s03_c02` from sequence `04` step `3`
  operator `rationalization`, option_effect `clarify`, score `0.589`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'ev_eli_texts_afterward']`, pressures `['status_damage']`, practices `['confession']`
- `beat_rationalize_toast_omission` from sequence `03` step `1`
  operator `rationalization`, option_effect `clarify`, score `0.448`
  refs `['ev_toast_credit_cut', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage']`, practices `['confession']`
- `node_tessa_rationalizes_coatroom` from sequence `02` step `3`
  operator `rationalization`, option_effect `none`, score `0.427`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'sit_text_reply_threshold']`, pressures `['status_damage']`, practices `['confession']`

## Rejected Nodes

- `beat_rationalize_coatroom_text_s02_c04` from sequence `01` step `2` rejected by `['function_signature_cap']`
- `beat_tessa_rationalizes_text_s03_c04` from sequence `01` step `3` rejected by `['function_signature_cap']`
- `beat_rationalize_coatroom_text_s01_c02` from sequence `02` step `1` rejected by `['function_signature_cap']`
- `beat_rationalize_coatroom` from sequence `02` step `2` rejected by `['duplicate_node_id', 'function_signature_cap']`
- `beat_rationalize_omission` from sequence `03` step `2` rejected by `['function_signature_cap']`
- `beat_tessa_rationalizes_text` from sequence `03` step `3` rejected by `['function_signature_cap']`
- `beat_tessa_rationalizes_toast_omission` from sequence `04` step `1` rejected by `['function_signature_cap']`
- `beat_rationalize_coatroom` from sequence `04` step `2` rejected by `['duplicate_node_id', 'function_signature_cap']`
- `beat_rationalize_coatroom` from sequence `05` step `1` rejected by `['duplicate_node_id', 'function_signature_cap']`
- `beat_rationalize_coatroom_text` from sequence `05` step `2` rejected by `['semantic_expectations_failed', 'function_signature_cap']`
- `beat_tessa_rationalizes_omission_s03_c03` from sequence `05` step `3` rejected by `['function_signature_cap']`