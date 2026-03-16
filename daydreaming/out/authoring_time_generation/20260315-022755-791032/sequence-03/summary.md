# Authoring-Time Generation Prototype Run

- fixture: `daydreaming/fixtures/authoring_time_generation_tessa_toast_rationalization_v1.yaml`
- provider: `gemini`
- model: `gemini-3.1-pro-preview`

## Results

### middle-step-01

- graph valid: `True`
- selected operator: `rationalization`
- candidate compiler: selected `3` / `4` by `highest_soft_score_after_hard_filter`
- semantic checks: `{'rationalization is present': True, 'the apology has not been sent': True, 'the donor hall remains psychologically active': True, 'rationalization reframes rather than resolves the damage': False, 'no_cross_fixture_contamination': True}`

### middle-step-02

- graph valid: `True`
- selected operator: `rationalization`
- candidate compiler: selected `4` / `4` by `highest_soft_score_after_hard_filter`
- semantic checks: `{'rationalization is present': True, 'the apology has not been sent': True, 'the donor hall remains psychologically active': True, 'rationalization reframes rather than resolves the damage': True, 'no_cross_fixture_contamination': True}`

### middle-step-03

- graph valid: `True`
- selected operator: `rationalization`
- candidate compiler: selected `4` / `4` by `highest_soft_score_after_hard_filter`
- semantic checks: `{'rationalization is present': True, 'the apology has not been sent': True, 'the donor hall remains psychologically active': True, 'rationalization reframes rather than resolves the damage': True, 'no_cross_fixture_contamination': True}`
