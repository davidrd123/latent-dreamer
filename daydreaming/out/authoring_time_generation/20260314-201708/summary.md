# Authoring-Time Generation Prototype Run

- fixture: `daydreaming/fixtures/authoring_time_generation_rhea_credit_meeting_v1.yaml`
- provider: `stub`
- model: `none`

## Results

### baseline

- graph valid: `True`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the studio remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`

### middle

- graph valid: `True`
- selected operator: `rehearsal`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the studio remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`

### middle (no_causal_slice)

- graph valid: `True`
- selected operator: `avoidance`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the studio remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`

### middle (high_controllability)

- graph valid: `True`
- selected operator: `rehearsal`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the studio remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`

### middle (low_controllability)

- graph valid: `True`
- selected operator: `avoidance`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the studio remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`

### middle (swap_practice_context)

- graph valid: `True`
- selected operator: `avoidance`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the studio remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`
