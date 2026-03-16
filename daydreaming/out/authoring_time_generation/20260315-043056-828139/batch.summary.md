# Authoring-Time Generation Batch Run

- fixture: `daydreaming/fixtures/authoring_time_generation_tessa_toast_rationalization_v1.yaml`
- provider: `anthropic`
- model: `claude-sonnet-4-6`
- sequences: `5`
- steps per sequence: `3`
- candidates per step: `4`
- pooled accepted nodes: `15`
- admitted nodes: `3` / `8`

## Sequence Summaries

- sequence `01`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `02`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `03`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `04`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`
- sequence `05`: accepted `3` / `3`, operator path `['rationalization', 'rationalization', 'rationalization']`

## Admitted Nodes

- `beat_coatroom_rationalization_01_s01_c04` from sequence `03` step `1`
  operator `rationalization`, option_effect `clarify`, score `0.991`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage', 'obligation']`, practices `['confession']`
- `beat_coatroom_rationalization_01_s01_c03` from sequence `05` step `1`
  operator `rationalization`, option_effect `open`, score `0.658`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage', 'obligation']`, practices `['confession']`
- `beat_coatroom_rationalization_01_s01_c02` from sequence `02` step `1`
  operator `rationalization`, option_effect `clarify`, score `0.556`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage', 'obligation']`, practices `['confession']`

## Rejected Nodes

- `beat_coatroom_rationalization_01_s01_c04` from sequence `01` step `1` rejected by `['duplicate_node_id', 'function_signature_cap']`
- `beat_coatroom_rationalization_01` from sequence `01` step `2` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_01_s03_c01` from sequence `01` step `3` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_01` from sequence `02` step `2` rejected by `['near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01_s03_c01` from sequence `02` step `3` rejected by `['near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01_s02_c03` from sequence `03` step `2` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_01` from sequence `03` step `3` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_01_s01_c02` from sequence `04` step `1` rejected by `['duplicate_node_id', 'function_signature_cap']`
- `beat_tessa_coatroom_rationalize_01` from sequence `04` step `2` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_01` from sequence `04` step `3` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_01_s02_c02` from sequence `05` step `2` rejected by `['function_signature_cap']`
- `beat_tessa_coatroom_rationalization_01` from sequence `05` step `3` rejected by `['function_signature_cap']`