# Authoring-Time Generation Batch Run

- fixture: `daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml`
- provider: `anthropic`
- model: `claude-sonnet-4-6`
- sequences: `5`
- steps per sequence: `3`
- candidates per step: `4`
- pooled accepted nodes: `15`
- admitted nodes: `4` / `8`

## Sequence Summaries

- sequence `01`: accepted `3` / `3`, operator path `['rehearsal', 'rehearsal', 'rehearsal']`
- sequence `02`: accepted `3` / `3`, operator path `['rehearsal', 'rehearsal', 'rehearsal']`
- sequence `03`: accepted `3` / `3`, operator path `['rehearsal', 'rehearsal', 'rehearsal']`
- sequence `04`: accepted `3` / `3`, operator path `['rehearsal', 'rehearsal', 'rehearsal']`
- sequence `05`: accepted `3` / `3`, operator path `['rehearsal', 'rehearsal', 'rehearsal']`

## Admitted Nodes

- `beat_kai_rehearsal_kitchen_s01_c04` from sequence `02` step `1`
  operator `rehearsal`, option_effect `none`, score `0.971`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight', 'sit_threshold_departure']`, pressures `['attachment_threat', 'obligation']`, practices `['anticipated-confrontation']`
- `beat_rehearsal_kitchen_precontact` from sequence `05` step `1`
  operator `rehearsal`, option_effect `open`, score `0.615`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight', 'sit_threshold_departure']`, pressures `['attachment_threat', 'obligation']`, practices `['anticipated-confrontation']`
- `beat_rehearsal_unopened_letter_s01_c02` from sequence `04` step `1`
  operator `rehearsal`, option_effect `none`, score `0.539`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight', 'sit_threshold_departure']`, pressures `['attachment_threat', 'obligation']`, practices `['anticipated-confrontation']`
- `beat_rehearsal_opening_line` from sequence `03` step `3`
  operator `rehearsal`, option_effect `open`, score `0.506`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight', 'sit_threshold_departure']`, pressures `['attachment_threat', 'obligation']`, practices `['anticipated-confrontation']`

## Rejected Nodes

- `beat_rehearsal_unopened_letter` from sequence `01` step `1` rejected by `['function_signature_cap']`
- `beat_rehearsal_sealed_letter` from sequence `01` step `2` rejected by `['function_signature_cap']`
- `beat_kai_rehearsal_kitchen` from sequence `01` step `3` rejected by `['function_signature_cap']`
- `beat_kai_rehearsal_faucet` from sequence `02` step `2` rejected by `['function_signature_cap']`
- `beat_rehearsal_faucet_line` from sequence `02` step `3` rejected by `['function_signature_cap']`
- `beat_rehearsal_opening_line_s01_c02` from sequence `03` step `1` rejected by `['function_signature_cap']`
- `beat_rehearsal_opening_line_s02_c03` from sequence `03` step `2` rejected by `['function_signature_cap']`
- `beat_rehearsal_cold_mug` from sequence `04` step `2` rejected by `['near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_rehearsal_cold_mug_s03_c01` from sequence `04` step `3` rejected by `['near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_rehearsal_faucet_mouthing` from sequence `05` step `2` rejected by `['function_signature_cap']`
- `beat_kai_faucet_rehearsal` from sequence `05` step `3` rejected by `['function_signature_cap']`