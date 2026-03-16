# Authoring-Time Generation Prototype Run

- fixture: `daydreaming/fixtures/authoring_time_generation_tessa_toast_rationalization_v1.yaml`
- provider: `anthropic`
- model: `claude-sonnet-4-6`

## Results

### middle

- graph valid: `True`
- selected operator: `rationalization`
- candidate compiler: selected `1` / `4` by `highest_soft_score_after_hard_filter`
- semantic checks: `{'rationalization is present': False, 'the apology has not been sent': True, 'the donor hall remains psychologically active': True, 'rationalization reframes rather than resolves the damage': True, 'no_cross_fixture_contamination': True}`
