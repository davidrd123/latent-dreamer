# Authoring-Time Generation Prototype Run

- fixture: `daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml`
- provider: `anthropic`
- model: `claude-sonnet-4-6`

## Results

### middle-step-01

- graph valid: `True`
- selected operator: `avoidance`
- candidate compiler: selected `1` / `4` by `highest_soft_score_after_hard_filter`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': True, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`

### middle-step-02

- graph valid: `True`
- selected operator: `avoidance`
- candidate compiler: selected `1` / `4` by `highest_soft_score_after_hard_filter`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': True, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`

### middle-step-03

- graph valid: `True`
- selected operator: `avoidance`
- candidate compiler: selected `2` / `4` by `highest_soft_score_after_hard_filter`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': False, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`
