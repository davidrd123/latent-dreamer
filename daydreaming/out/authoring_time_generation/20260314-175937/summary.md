# Authoring-Time Generation Prototype Run

- fixture: `daydreaming/fixtures/authoring_time_generation_kai_letter_v1.yaml`
- provider: `gemini`
- model: `gemini-3.1-pro-preview`

## Results

### baseline

- graph valid: `False`
- validation errors: `["setup_refs contains non-resolvable refs: ['teen_ferry_escape', 'silence_as_message']", "payoff_refs contains non-resolvable refs: ['harbor_fight_honesty']"]`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': False, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True}`

### middle

- graph valid: `False`
- validation errors: `["setup_refs contains non-resolvable refs: ['ep_last_rupture', 'ep_old_harbor_memory']"]`
- selected operator: `avoidance`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': False, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True}`

### middle (no_causal_slice)

- graph valid: `False`
- validation errors: `["setup_refs contains non-resolvable refs: ['ep_old_harbor_memory', 'ep_last_rupture']"]`
- selected operator: `avoidance`
- semantic checks: `{'delay ritual is present': True, 'the letter is not opened': True, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True}`

### middle (high_controllability)

- graph valid: `False`
- validation errors: `["setup_refs contains non-resolvable refs: ['ep_last_rupture', 'ep_old_harbor_memory']"]`
- selected operator: `rehearsal`
- semantic checks: `{'delay ritual is present': False, 'the letter is not opened': False, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True}`

### middle (swap_practice_context)

- graph valid: `False`
- validation errors: `["setup_refs contains non-resolvable refs: ['ep_last_rupture', 'ep_old_harbor_memory']"]`
- selected operator: `rehearsal`
- semantic checks: `{'delay ritual is present': False, 'the letter is not opened': True, 'the harbor remains psychologically active': True, 'avoidance sharpens rather than resolves the choice': True}`
