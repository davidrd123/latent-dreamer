# Authoring-Time Generation Batch Run

- fixture: `daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml`
- provider: `anthropic`
- model: `claude-sonnet-4-6`
- sequences: `5`
- steps per sequence: `3`
- candidates per step: `4`
- pooled accepted nodes: `15`
- admitted nodes: `2` / `8`

## Sequence Summaries

- sequence `01`: accepted `3` / `3`, operator path `['rehearsal', 'rehearsal', 'rehearsal']`
- sequence `02`: accepted `3` / `3`, operator path `['rehearsal', 'rehearsal', 'rehearsal']`
- sequence `03`: accepted `3` / `3`, operator path `['rehearsal', 'rehearsal', 'rehearsal']`
- sequence `04`: accepted `3` / `3`, operator path `['rehearsal', 'rehearsal', 'rehearsal']`
- sequence `05`: accepted `3` / `3`, operator path `['rehearsal', 'rehearsal', 'rehearsal']`

## Admitted Nodes

- `beat_rehearsal_sink_precontact` from sequence `01` step `1`
  operator `rehearsal`, option_effect `none`, score `0.971`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight', 'sit_threshold_departure']`, pressures `['attachment_threat', 'obligation']`, practices `['anticipated-confrontation']`
- `beat_kai_rehearsal_kitchen` from sequence `03` step `1`
  operator `rehearsal`, option_effect `none`, score `0.545`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight', 'sit_threshold_departure']`, pressures `['attachment_threat', 'obligation']`, practices `['anticipated-confrontation']`

## Rejected Nodes

- `beat_kai_sink_rehearsal_s02_c04` from sequence `01` step `2` rejected by `['function_signature_cap']`
- `beat_rehearsal_faucet_window` from sequence `01` step `3` rejected by `['function_signature_cap']`
- `beat_kai_rehearsal_sink_s01_c04` from sequence `02` step `1` rejected by `['function_signature_cap']`
- `beat_kai_sink_rehearsal` from sequence `02` step `2` rejected by `['function_signature_cap']`
- `beat_kai_sink_rehearsal_s03_c01` from sequence `02` step `3` rejected by `['function_signature_cap']`
- `beat_kai_faucet_rehearsal` from sequence `03` step `2` rejected by `['near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_kai_faucet_rehearsal_s03_c01` from sequence `03` step `3` rejected by `['near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_rehearsal_unopened_letter` from sequence `04` step `1` rejected by `['function_signature_cap']`
- `beat_kai_rehearsal_counter_s02_c04` from sequence `04` step `2` rejected by `['function_signature_cap']`
- `beat_kai_rehearsal_counter` from sequence `04` step `3` rejected by `['function_signature_cap']`
- `beat_rehearsal_unopened_letter` from sequence `05` step `1` rejected by `['function_signature_cap']`
- `beat_kai_rehearsal_kitchen` from sequence `05` step `2` rejected by `['duplicate_node_id', 'function_signature_cap']`
- `beat_kai_rehearsal_kitchen_s03_c01` from sequence `05` step `3` rejected by `['function_signature_cap']`