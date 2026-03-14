# City Routes Contrasting Conductor Sweep

Date: 2026-03-14

## Purpose

Test whether contrasting feature-level conductor biases can
bend the City Routes feature arm away from its default
structural route rather than simply reinforcing it.

## Sweep

- seeds: `7, 11, 19`
- presets: `neutral`, `spectacle_hold`, `threshold_drive`, `refuge_hold`, `exchange_fast`
- arm: `feature` only

## Aggregate

- seeds with distinct preset paths: `1/3`
- avg distinct paths per seed: `1.33`
- spectacle hold increased `s4` presence: `0` seeds
- threshold drive increased `s5` presence: `0` seeds
- refuge hold increased `s3` presence: `0` seeds
- exchange fast increased `s6` presence: `0` seeds
- spectacle hold reached `e2` earlier: `0` seeds
- threshold drive reached `e4` earlier: `0` seeds
- exchange fast reached `e3` earlier: `0` seeds

## Per-run Summary

| Seed | Preset | s3 | s4 | s5 | s6 | Events | first e2 | first e3 | first e4 |
|---|---|---:|---:|---:|---:|---|---:|---:|---:|
| 7 | neutral | 3 | 2 | 5 | 3 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 7 | 12 | 10 |
| 7 | spectacle_hold | 3 | 2 | 5 | 3 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 7 | 12 | 10 |
| 7 | threshold_drive | 3 | 2 | 5 | 3 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 7 | 12 | 10 |
| 7 | refuge_hold | 3 | 2 | 5 | 3 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 7 | 12 | 10 |
| 7 | exchange_fast | 3 | 2 | 5 | 3 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 7 | 12 | 10 |
| 11 | neutral | 3 | 2 | 5 | 4 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 12 | 18 |
| 11 | spectacle_hold | 3 | 2 | 5 | 4 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 12 | 18 |
| 11 | threshold_drive | 3 | 2 | 5 | 4 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 12 | 18 |
| 11 | refuge_hold | 3 | 2 | 5 | 4 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 12 | 18 |
| 11 | exchange_fast | 3 | 2 | 5 | 4 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 12 | 18 |
| 19 | neutral | 3 | 2 | 5 | 4 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 11 | 18 |
| 19 | spectacle_hold | 3 | 2 | 3 | 4 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff | 6 | 11 | - |
| 19 | threshold_drive | 3 | 2 | 5 | 4 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 11 | 18 |
| 19 | refuge_hold | 3 | 2 | 3 | 4 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff | 6 | 11 | - |
| 19 | exchange_fast | 3 | 2 | 5 | 4 | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 6 | 11 | 18 |
