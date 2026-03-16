# Authoring-Time Generation Prototype Run

- fixture: `daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml`
- provider: `gemini`
- model: `gemini-3.1-pro-preview`

## Results

### baseline

- graph valid: `True`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': False, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True}`

### middle

- graph valid: `True`
- selected operator: `avoidance`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': False, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True}`

### middle (no_causal_slice)

- graph valid: `True`
- selected operator: `avoidance`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': True, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True}`

### middle (high_controllability)

- graph valid: `True`
- selected operator: `rehearsal`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': True, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True}`

### middle (swap_practice_context)

- graph valid: `True`
- selected operator: `rehearsal`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': True, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True}`
