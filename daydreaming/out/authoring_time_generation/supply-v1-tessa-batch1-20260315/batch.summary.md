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

- `beat_coatroom_program_rationalization` from sequence `01` step `1`
  operator `rationalization`, option_effect `open`, score `0.994`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage', 'obligation']`, practices `['confession']`
- `beat_coatroom_rationalization_lapel` from sequence `02` step `1`
  operator `rationalization`, option_effect `clarify`, score `0.718`
  refs `['ev_toast_credit_cut', 'sit_coatroom_after_toast', 'sit_text_reply_threshold', 'ev_eli_texts_afterward']`, pressures `['status_damage', 'obligation']`, practices `['confession']`
- `beat_tessa_rationalization_coatroom_s01_c03` from sequence `05` step `1`
  operator `rationalization`, option_effect `open`, score `0.569`
  refs `['ev_toast_credit_cut', 'sit_coatroom_after_toast', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage', 'obligation']`, practices `['confession']`
- `beat_coatroom_rationalization_01_s02_c04` from sequence `04` step `2`
  operator `rationalization`, option_effect `clarify`, score `0.523`
  refs `['ev_toast_credit_cut', 'ref_program_credit_line', 'ev_eli_texts_afterward', 'sit_text_reply_threshold']`, pressures `['status_damage', 'obligation']`, practices `['confession']`

## Rejected Nodes

- `beat_coatroom_rationalization_program` from sequence `01` step `2` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_01` from sequence `01` step `3` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_lapel_s02_c04` from sequence `02` step `2` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_01` from sequence `02` step `3` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_tag` from sequence `03` step `1` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_draft_s02_c03` from sequence `03` step `2` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_draft_s03_c03` from sequence `03` step `3` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_01` from sequence `04` step `1` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_01_s03_c03` from sequence `04` step `3` rejected by `['near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_coatroom_rationalization_01` from sequence `05` step `2` rejected by `['function_signature_cap']`
- `beat_coatroom_rationalization_01_s03_c02` from sequence `05` step `3` rejected by `['function_signature_cap']`