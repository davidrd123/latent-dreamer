# City Routes Robustness Sweep

Date: 2026-03-14

## Purpose

Check whether arm C's threshold/event gains hold across a small
seed and conductor sweep, rather than only on the seed-7 neutral
path that first exposed the feature registry's value.

## Sweep

- seeds: `3, 7, 11, 19, 23`
- presets: `neutral, sustained_high, early_release`
- cycles per run: `18`
- arms compared: `scheduler` vs `feature`

## Aggregate Comparison

- scheduler threshold runs: `9/15`
- feature threshold runs: `15/15`
- scheduler prepared-event runs: `15/15`
- feature prepared-event runs: `15/15`
- scheduler `e4` reuse runs: `3/15`
- feature `e4` reuse runs: `3/15`
- scheduler avg situations visited: `5.6`
- feature avg situations visited: `6`
- scheduler avg release moves: `0`
- feature avg release moves: `0`

## Case-level Wins For Arm C

- threshold advantage over arm B: `6` cases
- more prepared-event visits than arm B: `6` cases
- more distinct events than arm B: `6` cases
- more release moves than arm B: `0` cases
- identical path to arm B: `3` cases

## Per-run Summary

| Preset | Seed | Arm | Situations | s5 | Events | Prepared | e4 reuse | Release moves | End tension |
|---|---:|---|---:|---|---|---:|---|---:|---:|
| neutral | 3 | scheduler | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | no | 0 | 1.00 |
| neutral | 3 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | no | 0 | 1.00 |
| neutral | 7 | scheduler | 5 | no | e1_train_missed,e3_wrong_handoff | 1 | no | 0 | 0.90 |
| neutral | 7 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | yes | 0 | 1.00 |
| neutral | 11 | scheduler | 5 | no | e1_train_missed,e3_wrong_handoff | 1 | no | 0 | 0.90 |
| neutral | 11 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 2 | no | 0 | 1.00 |
| neutral | 19 | scheduler | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | yes | 0 | 1.00 |
| neutral | 19 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | no | 0 | 1.00 |
| neutral | 23 | scheduler | 6 | yes | e1_train_missed,e3_wrong_handoff,e4_bridge_lockdown | 2 | no | 0 | 0.90 |
| neutral | 23 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff | 2 | no | 0 | 1.00 |
| sustained_high | 3 | scheduler | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | no | 0 | 1.00 |
| sustained_high | 3 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | no | 0 | 1.00 |
| sustained_high | 7 | scheduler | 5 | no | e1_train_missed,e3_wrong_handoff | 1 | no | 0 | 0.90 |
| sustained_high | 7 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | yes | 0 | 1.00 |
| sustained_high | 11 | scheduler | 5 | no | e1_train_missed,e3_wrong_handoff | 1 | no | 0 | 0.90 |
| sustained_high | 11 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 2 | no | 0 | 1.00 |
| sustained_high | 19 | scheduler | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | yes | 0 | 1.00 |
| sustained_high | 19 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | no | 0 | 1.00 |
| sustained_high | 23 | scheduler | 6 | yes | e1_train_missed,e3_wrong_handoff,e4_bridge_lockdown | 3 | no | 0 | 1.00 |
| sustained_high | 23 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff | 2 | no | 0 | 1.00 |
| early_release | 3 | scheduler | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | no | 0 | 1.00 |
| early_release | 3 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | no | 0 | 1.00 |
| early_release | 7 | scheduler | 5 | no | e1_train_missed,e3_wrong_handoff | 1 | no | 0 | 0.90 |
| early_release | 7 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | yes | 0 | 1.00 |
| early_release | 11 | scheduler | 5 | no | e1_train_missed,e3_wrong_handoff | 1 | no | 0 | 0.90 |
| early_release | 11 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 2 | no | 0 | 1.00 |
| early_release | 19 | scheduler | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | yes | 0 | 1.00 |
| early_release | 19 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff,e4_bridge_lockdown | 3 | no | 0 | 1.00 |
| early_release | 23 | scheduler | 6 | yes | e1_train_missed,e3_wrong_handoff,e4_bridge_lockdown | 2 | no | 0 | 0.90 |
| early_release | 23 | feature | 6 | yes | e1_train_missed,e2_blackout_siren,e3_wrong_handoff | 2 | no | 0 | 1.00 |
