# City Routes Targeted Conductor Sweep

Date: 2026-03-14

## Purpose

Test whether targeted feature-level conductor biases can produce
more expressive traversal changes than the earlier global
tension/energy presets did on City Routes.

## Sweep

- seeds: `7, 11, 19`
- presets: `neutral`, `spectacle_blackout`, `threshold_lockdown`, `refuge_pause`
- arm: `feature` only

## Aggregate

- seeds with distinct preset paths: `0/3`
- avg distinct paths per seed: `1`
- spectacle bias increased `s4` presence: `0` seeds
- threshold bias increased `s5` presence: `0` seeds
- refuge bias increased `s3` presence: `0` seeds
- spectacle bias reached `e2` earlier: `0` seeds
- threshold bias reached `e4` earlier: `0` seeds

## Per-run Summary

| Seed | Preset | s3 | s4 | s5 | Events | first e2 | first e4 |
|---|---|---:|---:|---:|---|---:|---:|
| 7 | neutral | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 7 | 10 |
| 7 | spectacle_blackout | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 7 | 10 |
| 7 | threshold_lockdown | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 7 | 10 |
| 7 | refuge_pause | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 7 | 10 |
| 11 | neutral | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 18 |
| 11 | spectacle_blackout | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 18 |
| 11 | threshold_lockdown | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 18 |
| 11 | refuge_pause | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 18 |
| 19 | neutral | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 18 |
| 19 | spectacle_blackout | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 18 |
| 19 | threshold_lockdown | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 18 |
| 19 | refuge_pause | 3 | 2 | 5 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 18 |
