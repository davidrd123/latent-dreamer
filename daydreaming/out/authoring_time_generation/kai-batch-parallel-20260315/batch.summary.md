# Authoring-Time Generation Batch Run

- fixture: `daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml`
- provider: `gemini`
- model: `gemini-3.1-pro-preview`
- sequences: `5`
- steps per sequence: `5`
- candidates per step: `4`
- pooled accepted nodes: `25`
- admitted nodes: `8` / `8`

## Sequence Summaries

- sequence `01`: accepted `5` / `5`, operator path `['avoidance', 'avoidance', 'avoidance', 'avoidance', 'avoidance']`
- sequence `02`: accepted `5` / `5`, operator path `['avoidance', 'avoidance', 'avoidance', 'avoidance', 'avoidance']`
- sequence `03`: accepted `5` / `5`, operator path `['avoidance', 'avoidance', 'avoidance', 'avoidance', 'avoidance']`
- sequence `04`: accepted `5` / `5`, operator path `['avoidance', 'avoidance', 'avoidance', 'avoidance', 'avoidance']`
- sequence `05`: accepted `5` / `5`, operator path `['avoidance', 'avoidance', 'avoidance', 'avoidance', 'avoidance']`

## Admitted Nodes

- `beat_avoidance_scrubbing_01_s01_c03` from sequence `03` step `1`
  operator `avoidance`, option_effect `none`, score `0.992`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight']`, pressures `['attachment_threat']`, practices `['evasion']`
- `beat_kai_kitchen_avoidance_01` from sequence `05` step `1`
  operator `avoidance`, option_effect `none`, score `0.556`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight']`, pressures `['attachment_threat']`, practices `['evasion']`
- `beat_kai_kitchen_avoidance_01_s01_c03` from sequence `04` step `1`
  operator `avoidance`, option_effect `none`, score `0.551`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight']`, pressures `['attachment_threat']`, practices `['evasion']`
- `beat_avoidance_scrubbing_01` from sequence `01` step `1`
  operator `avoidance`, option_effect `none`, score `0.532`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight']`, pressures `['attachment_threat']`, practices `['evasion']`
- `beat_kai_kitchen_avoidance_01_s01_c04` from sequence `02` step `1`
  operator `avoidance`, option_effect `none`, score `0.516`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight']`, pressures `['attachment_threat']`, practices `['evasion']`
- `beat_evasion_coffee_maker_01` from sequence `05` step `3`
  operator `avoidance`, option_effect `none`, score `0.413`
  refs `['sit_unopened_letter', 'ev_harbor_meeting_tonight']`, pressures `['attachment_threat']`, practices `['evasion']`
- `beat_kai_silverware_avoidance` from sequence `03` step `3`
  operator `avoidance`, option_effect `none`, score `0.408`
  refs `['sit_unopened_letter', 'harbor_as_shared_place', 'ev_harbor_meeting_tonight']`, pressures `['attachment_threat']`, practices `['evasion']`
- `beat_kai_grout_avoidance` from sequence `05` step `5`
  operator `avoidance`, option_effect `none`, score `0.406`
  refs `['sit_unopened_letter', 'ev_harbor_meeting_tonight', 'harbor_as_shared_place']`, pressures `['attachment_threat']`, practices `['evasion']`

## Rejected Nodes

- `beat_faucet_scrubbing_avoidance` from sequence `01` step `2` rejected by `lower_soft_score`
- `beat_kai_grout_avoidance` from sequence `01` step `3` rejected by `['duplicate_node_id']`
- `beat_silverware_avoidance` from sequence `01` step `4` rejected by `lower_soft_score`
- `beat_kai_kitchen_avoidance` from sequence `01` step `5` rejected by `lower_soft_score`
- `beat_kai_kitchen_avoidance_01` from sequence `02` step `2` rejected by `['duplicate_node_id']`
- `beat_kai_grinder_avoidance` from sequence `02` step `3` rejected by `lower_soft_score`
- `beat_avoid_faucet_polish` from sequence `02` step `4` rejected by `lower_soft_score`
- `beat_evasion_silverware` from sequence `02` step `5` rejected by `lower_soft_score`
- `beat_evasion_sink_scrub` from sequence `03` step `2` rejected by `lower_soft_score`
- `beat_evasion_grout_scrubbing` from sequence `03` step `4` rejected by `lower_soft_score`
- `beat_avoidance_mug_alignment` from sequence `03` step `5` rejected by `lower_soft_score`
- `beat_kai_skillet_avoidance` from sequence `04` step `2` rejected by `lower_soft_score`
- `beat_avoidance_skillet_01` from sequence `04` step `3` rejected by `lower_soft_score`
- `beat_evasion_skillet_scrub` from sequence `04` step `4` rejected by `lower_soft_score`
- `beat_evasion_kitchen_sink` from sequence `04` step `5` rejected by `lower_soft_score`
- `beat_avoidance_kitchen_sink` from sequence `05` step `2` rejected by `lower_soft_score`
- `beat_silverware_avoidance` from sequence `05` step `4` rejected by `lower_soft_score`