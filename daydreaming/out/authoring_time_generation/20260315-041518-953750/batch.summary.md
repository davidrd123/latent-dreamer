# Authoring-Time Generation Batch Run

- fixture: `daydreaming/fixtures/authoring_time_generation_tessa_toast_rationalization_v1.yaml`
- provider: `anthropic`
- model: `claude-sonnet-4-6`
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

- `beat_coatroom_rationalization_01_s01_c04` from sequence `05` step `1`
  operator `rationalization`, option_effect `open`, score `0.987`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage', 'obligation']`, practices `['confession']`
- `beat_coatroom_rationalization_draft` from sequence `04` step `1`
  operator `rationalization`, option_effect `clarify`, score `0.641`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage', 'obligation']`, practices `['confession']`
- `beat_coatroom_rationalization_01` from sequence `01` step `2`
  operator `rationalization`, option_effect `open`, score `0.463`
  refs `['ev_toast_credit_cut', 'ev_eli_texts_afterward', 'ref_program_credit_line', 'sit_text_reply_threshold']`, pressures `['status_damage', 'obligation']`, practices `['confession']`
- `beat_coatroom_rationalization_01_s03_c01` from sequence `04` step `3`
  operator `rationalization`, option_effect `clarify`, score `0.379`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage', 'obligation']`, practices `['confession']`

## Rejected Nodes

- `beat_coatroom_rationalization_01_s01_c04` from sequence `01` step `1` rejected by `['duplicate_node_id', 'near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01_s03_c01` from sequence `01` step `3` rejected by `['duplicate_node_id', 'near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01_s01_c04` from sequence `02` step `1` rejected by `['duplicate_node_id', 'near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01` from sequence `02` step `2` rejected by `['duplicate_node_id', 'near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01_s03_c01` from sequence `02` step `3` rejected by `['duplicate_node_id', 'near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01_s01_c04` from sequence `03` step `1` rejected by `['duplicate_node_id', 'near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01` from sequence `03` step `2` rejected by `['duplicate_node_id', 'near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01_s03_c01` from sequence `03` step `3` rejected by `['duplicate_node_id', 'near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01` from sequence `04` step `2` rejected by `['duplicate_node_id', 'near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01` from sequence `05` step `2` rejected by `['duplicate_node_id', 'near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01_s03_c01` from sequence `05` step `3` rejected by `['duplicate_node_id', 'near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`