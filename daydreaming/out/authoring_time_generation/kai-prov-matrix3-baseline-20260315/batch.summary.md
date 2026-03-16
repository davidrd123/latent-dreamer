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

- sequence `01`: accepted `3` / `3`, operator path `['avoidance', 'avoidance', 'avoidance']`
- sequence `02`: accepted `3` / `3`, operator path `['avoidance', 'avoidance', 'avoidance']`
- sequence `03`: accepted `3` / `3`, operator path `['avoidance', 'avoidance', 'avoidance']`
- sequence `04`: accepted `3` / `3`, operator path `['avoidance', 'avoidance', 'avoidance']`
- sequence `05`: accepted `3` / `3`, operator path `['avoidance', 'avoidance', 'avoidance']`

## Admitted Nodes

- `beat_kai_counter_ritual` from sequence `01` step `1`
  operator `avoidance`, option_effect `none`, score `0.972`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight', 'sit_threshold_departure']`, pressures `['attachment_threat', 'obligation']`, practices `['evasion']`
- `beat_kai_kitchen_avoidance_ritual` from sequence `02` step `1`
  operator `avoidance`, option_effect `none`, score `0.548`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight', 'sit_threshold_departure']`, pressures `['attachment_threat', 'obligation']`, practices `['evasion']`

## Rejected Nodes

- `beat_kai_kettle_avoidance` from sequence `01` step `2` rejected by `['function_signature_cap']`
- `beat_kai_ritual_distraction_letter` from sequence `01` step `3` rejected by `['function_signature_cap']`
- `beat_kai_kettle_avoidance` from sequence `02` step `2` rejected by `['near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_kai_kettle_avoidance_s03_c01` from sequence `02` step `3` rejected by `['near_duplicate_text', 'function_signature_cap', 'duplicate_function_signature']`
- `beat_kai_avoidance_kitchen_ritual` from sequence `03` step `1` rejected by `['function_signature_cap']`
- `beat_kai_kettle_avoidance` from sequence `03` step `2` rejected by `['function_signature_cap']`
- `beat_kai_kettle_avoidance_s03_c01` from sequence `03` step `3` rejected by `['function_signature_cap']`
- `beat_kai_kettle_avoidance` from sequence `04` step `1` rejected by `['function_signature_cap']`
- `beat_kai_sponge_avoidance` from sequence `04` step `2` rejected by `['function_signature_cap']`
- `beat_kai_sponge_ritual_s03_c03` from sequence `04` step `3` rejected by `['function_signature_cap']`
- `beat_kai_kettle_avoidance` from sequence `05` step `1` rejected by `['function_signature_cap']`
- `beat_kai_kettle_avoidance_s02_c04` from sequence `05` step `2` rejected by `['function_signature_cap']`
- `beat_kai_kettle_avoidance_s03_c01` from sequence `05` step `3` rejected by `['function_signature_cap']`