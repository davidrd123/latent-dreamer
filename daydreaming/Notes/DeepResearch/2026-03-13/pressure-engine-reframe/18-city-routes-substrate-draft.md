# City Routes Substrate Draft

Date: 2026-03-14

## Purpose

This note captures the first authored City Routes traversal substrate for
the full L3 experiment.

It turns the brief in
`daydreaming/Notes/experiential-design/23-brief-city-night-pursuit.md`
into:

- a concrete node inventory
- explicit situation / event clustering
- an authored graph fixture skeleton

Fixture:

- `daydreaming/fixtures/city_routes_experiment_1_v0.yaml`

## Counts

The first draft lands at:

- `29` nodes
- `6` places
- `6` situations
- `4` named events
- `40` edges

That is inside the intended experiment range.

## Situation Clusters

### `s1_the_run` / platform

- `c_s1_n01_last_train_doors_shut`
- `c_s1_n02_platform_sprint`
- `c_s1_n03_track_edge_hesitation`
- `c_s1_n04_train_leaving_reflection`
- `c_s1_n05_fluorescent_waiters`

Function:

- establish route pressure
- commit the first event
- make object visibility immediately costly

### `s2_the_detour` / market

- `c_s2_n01_alley_squeeze`
- `c_s2_n02_produce_cart_bottleneck`
- `c_s2_n03_tarp_corridor`
- `c_s2_n04_wrong_turn_through_steam`
- `c_s2_n05_fence_jump_setup`

Function:

- convert linear motion into rerouting
- let concealment and drag coexist
- hand off into threshold, spectacle, or refuge

### `s5_the_threshold` / rooftop + bridge

- `c_s5_n01_ladder_climb`
- `c_s5_n02_roof_gap_commit`
- `c_s5_n03_sodium_bridge_view`
- `c_s5_n04_wire_fence_scrape`
- `c_s5_n05_checkpoint_seen_from_above`

Function:

- tighten the route
- make crossings inspectable
- anchor `e4_bridge_lockdown`

### `s4_public_spectacle` / club

- `c_s4_n01_strobe_crowd_swallow`
- `c_s4_n02_back_room_cut_through`
- `c_s4_n03_stage_light_freeze`
- `c_s4_n04_side_door_exit`

Function:

- give the route a spectacle zone that can hide or expose
- anchor `e2_blackout_siren`
- provide side-door re-entry into threshold pressure

### `s3_false_refuge` / apartment

- `c_s3_n01_kitchen_table_set_down`
- `c_s3_n02_sink_water_breath_return`
- `c_s3_n03_map_spread_under_tungsten`
- `c_s3_n04_red_string_key_on_hook`
- `c_s3_n05_window_watch`

Function:

- make refuge real but costly
- create a meaningful return from shelter back into motion
- foreground the borrowed table / red-string key / reroute logic

### `s6_the_exchange` / warehouse

- `c_s6_n01_river_edge_loading_bay`
- `c_s6_n02_wrong_receiver_silhouette`
- `c_s6_n03_silver_case_under_headlight_sweep`
- `c_s6_n04_almost_handoff`
- `c_s6_n05_corrected_transfer`

Function:

- stop the run from being pure movement
- anchor `e3_wrong_handoff`
- test exchange / identity / object legibility

## Event Anchors

### `e1_train_missed`

Primary anchor:

- `c_s1_n01_last_train_doors_shut`

Purpose:

- commit route failure early
- force the first reroute honestly

### `e2_blackout_siren`

Primary anchor:

- `c_s4_n03_stage_light_freeze`

Purpose:

- flip spectacle into exposure
- give the club cluster a real event payload rather than only mood

### `e3_wrong_handoff`

Primary anchors:

- `c_s6_n02_wrong_receiver_silhouette`
- `c_s6_n04_almost_handoff`

Purpose:

- make event approach legible across more than one exchange node

### `e4_bridge_lockdown`

Primary anchor:

- `c_s5_n05_checkpoint_seen_from_above`

Purpose:

- make threshold pressure explicit
- create a real tightening event rather than a generic bridge mood

## Edge Strategy

The first draft aims for:

- one strong internal continuity path in every cluster
- at least one cross-cluster exit from every cluster
- at least one meaningful refuge return:
  - `c_s3_n05_window_watch -> c_s1_n05_fluorescent_waiters`
- at least one exchange aftermath return:
  - `c_s6_n05_corrected_transfer -> c_s5_n03_sodium_bridge_view`

The edge set is intentionally authored rather than dense.
The goal is not maximal connectivity.
The goal is legible route logic with multiple exits from hot zones.

## Annotation Strategy

Every node already includes the first-pass experiment fields:

- `availability_test`
- `priority_tier`
- `delta_tension`
- `delta_energy`
- `setup_refs[]`
- `payoff_refs[]`
- `line_id`
- `subplot_id`
- `situation_id`
- `event_id?`
- `event_commit_potential?`
- `pressure_tags[]`
- `option_effect`
- `importance`
- `resolution_path_count`

These stay graph-native.
There are no L2 concern objects or internal contexts in the fixture.

## Current Status

This is a real authored substrate draft, not just a note.
But it is still a first pass.

What remains before scheduler integration:

- read the edge set cold and trim or strengthen weak transitions
- review the per-node deltas for cluster contour
- decide whether one more spectacle node or one more warehouse node is
  needed before the three-arm experiment
- then wire the City Routes graph into the shared traversal harness

## Immediate Next Move

Use this draft as the new gating artifact.

The next pass should not be abstract architecture discussion.
It should be:

1. cold-review the fixture
2. tune nodes / edges / annotations
3. adapt the harness from Graffito to City Routes
