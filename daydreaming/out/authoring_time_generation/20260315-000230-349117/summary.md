# Authoring-Time Generation Prototype Run

- fixture: `daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml`
- provider: `gemini`
- model: `gemini-3.1-pro-preview`

## Results

### middle-step-01

- graph valid: `True`
- selected operator: `avoidance`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': True, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`

### middle-step-02

- graph valid: `True`
- selected operator: `avoidance`
- semantic checks: `{'delay ritual is present': False, 'the letter is not opened': True, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`

### middle-step-03

- graph valid: `True`
- selected operator: `avoidance`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': True, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`
