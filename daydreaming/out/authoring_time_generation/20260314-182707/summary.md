# Authoring-Time Generation Prototype Run

- fixture: `daydreaming/fixtures/authoring_time_generation_maren_opening_line_v1.yaml`
- provider: `gemini`
- model: `gemini-3.1-pro-preview`

## Results

### baseline

- graph valid: `True`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the rehearsal room remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True}`

### middle

- graph valid: `True`
- selected operator: `rehearsal`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the rehearsal room remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True}`

### middle (no_causal_slice)

- graph valid: `True`
- selected operator: `avoidance`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the rehearsal room remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True}`

### middle (high_controllability)

- graph valid: `True`
- selected operator: `rehearsal`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the rehearsal room remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True}`

### middle (swap_practice_context)

- graph valid: `True`
- selected operator: `avoidance`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the rehearsal room remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True}`
