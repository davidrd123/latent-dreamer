# Authoring-Time Generation Prototype Run

- fixture: `daydreaming/fixtures/authoring_time_generation_rhea_credit_meeting_v1.yaml`
- provider: `gemini`
- model: `gemini-3.1-pro-preview`

## Results

### middle-step-01

- graph valid: `True`
- selected operator: `rehearsal`
- semantic checks: `{'opening-line rehearsal is present': False, 'the meeting has not begun': True, 'the studio remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': False, 'no_cross_fixture_contamination': True}`

### middle-step-02

- graph valid: `True`
- selected operator: `rehearsal`
- semantic checks: `{'opening-line rehearsal is present': False, 'the meeting has not begun': True, 'the studio remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`

### middle-step-03

- graph valid: `True`
- selected operator: `rehearsal`
- semantic checks: `{'opening-line rehearsal is present': True, 'the meeting has not begun': True, 'the studio remains psychologically active': True, 'rehearsal sharpens rather than resolves the choice': True, 'no_cross_fixture_contamination': True}`
