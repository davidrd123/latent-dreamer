(ns daydreamer.benchmarks.stalker-zone
  "Scripted and semi-unscripted Stalker Zone benchmark.

  This benchmark exercises RATIONALIZATION. The target bridge is from dread at
  the Room toward the guide-centered explanation that the Zone's delays are
  merciful rather than merely obstructive."
  (:require [daydreamer.benchmarks.stalker-zone-adapter :as adapter]
            [daydreamer.context :as cx]
            [daydreamer.runner :as runner]))

(def benchmark-brief-path
  "Source brief for the synthetic Zone benchmark."
  "daydreaming/Notes/experiential-design/08-brief-stalker-zone.md")

(def graph-counts
  "Synthetic mini-graph size for the benchmark window."
  {:graph_nodes 8
   :graph_edges 9})

(def goal-specs
  [{:goal-type :repercussions
    :planning-type :imaginary
    :strength 0.88
    :main-motiv :e-room-dread
    :situation-id :s4_the_room}
   {:goal-type :rationalization
    :planning-type :imaginary
    :strength 0.36
    :main-motiv :e-zone-mercy
    :situation-id :s5_the_guide}])

(def semi-unscripted-goal-specs
  [{:goal-type :repercussions
    :planning-type :imaginary
    :strength 0.88
    :main-motiv :e-room-dread
    :situation-id :s4_the_room}])

(defn- assert-facts
  [world context-id facts]
  (reduce (fn [current-world fact]
            (cx/assert-fact current-world context-id fact))
          world
          facts))

(defn- seed-rationalization-trigger
  [world root-id]
  (let [[world trigger-context-id] (cx/sprout world root-id)
        failed-goal-id :g_fixture_room_failure
        emotion-id :e_fixture_room_dread
        preferred-frame-id :rf_zone_mercy
        preferred-reframe-facts [{:fact/type :situation
                                  :fact/id :s5_the_guide}
                                 {:fact/type :rationalization
                                  :fact/id :zone_is_mercy}
                                 {:fact/type :rationalization
                                  :fact/id :delay_is_faith}]
        fallback-reframe-facts [{:fact/type :situation
                                 :fact/id :s1_the_approach}
                                {:fact/type :rationalization
                                 :fact/id :delay_is_caution}]
        facts [{:fact/type :situation
                :fact/id :s4_the_room}
               {:fact/type :situation
                :fact/id :s5_the_guide}
               {:fact/type :goal
                :goal-id failed-goal-id
                :top-level-goal failed-goal-id
                :status :failed
                :activation-context trigger-context-id}
               {:fact/type :emotion
                :emotion-id emotion-id
                :strength 0.82
                :valence :negative}
               {:fact/type :dependency
                :from-id emotion-id
                :to-id failed-goal-id}
               {:fact/type :rationalization-frame
                :fact/id preferred-frame-id
                :goal-id failed-goal-id
                :priority 0.93
                :reframe-facts preferred-reframe-facts}
               {:fact/type :rationalization-frame
                :fact/id :rf_turn_back
                :goal-id failed-goal-id
                :priority 0.24
                :reframe-facts fallback-reframe-facts}]
        world (assert-facts world trigger-context-id facts)
        [world _] (cx/sprout world trigger-context-id)]
    [world {:trigger-context-id trigger-context-id
            :failed-goal-id failed-goal-id
            :emotion-id emotion-id
            :preferred-frame-id preferred-frame-id
            :preferred-reframe-facts preferred-reframe-facts}]))

(defn- benchmark-goal-ids
  [goal-ids]
  {:repercussions-goal-id (nth goal-ids 0 nil)
   :rationalization-goal-id (nth goal-ids 1 nil)})

(defn- semi-unscripted-goal-ids
  [goal-ids]
  {:repercussions-goal-id (nth goal-ids 0 nil)})

(defn cycle-scripts
  [{:keys [trigger-context-id failed-goal-id preferred-frame-id]}]
  [{:timestamp "2026-03-12T14:00:06Z"
    :active-indices [:wish :fear :threshold :honesty]
    :retrievals [{:node-id "n06_phone_without_voice"
                  :episode-id :ep-6
                  :marks 3
                  :threshold 3
                  :overlap [:fear :honesty :wish]}]
    :chosen-node-id "n06_phone_without_voice"
    :selection {:policy :gravitational_pull
                :edge_kind :gravitational_pull
                :benchmark_step :cycle_6_room_dread}
    :feedback-applied {:director_concepts [:room :phone :confession]
                       :situation_boosts {:s4_the_room 0.11}
                       :emotional_episodes [{:affect :dread
                                             :target :the_room
                                             :source_situation :s4_the_room
                                             :intensity 0.26
                                             :decay 0.93
                                             :indices [:wish
                                                       :fear
                                                       :threshold]}]
                       :notes "The phone at the center makes arrival feel like confession."}
    :serendipity-bias 0.04
    :situations {:s1_the_approach {:activation 0.33
                                   :ripeness 0.54
                                   :hope 0.17
                                   :threat 0.38}
                 :s2_the_path {:activation 0.39
                               :ripeness 0.62
                               :hope 0.36
                               :threat 0.19}
                 :s3_the_ruins {:activation 0.28
                                :ripeness 0.45
                                :hope 0.10
                                :threat 0.34}
                 :s4_the_room {:activation 0.52
                               :ripeness 0.82
                               :hope 0.22
                               :threat 0.67}
                 :s5_the_guide {:activation 0.26
                                :ripeness 0.51
                                :hope 0.31
                                :threat 0.24}}
    :terminate {:status :failed}}
   {:timestamp "2026-03-12T14:00:07Z"
    :rationalization-branch {:trigger-context-id trigger-context-id
                             :failed-goal-id failed-goal-id
                             :frame-id preferred-frame-id}
    :active-indices [:faith :guide :sincerity :trust]
    :retrievals [{:node-id "n07_zone_is_mercy"
                  :episode-id :ep-7
                  :marks 3
                  :threshold 2
                  :overlap [:faith :guide :sincerity]}]
    :chosen-node-id "n07_zone_is_mercy"
    :selection {:policy :stored_rationalization_frame
                :edge_kind :reinterpretation_bridge
                :benchmark_step :cycle_7_rationalization_arrival}
    :feedback-applied {:director_concepts [:guide :faith :mercy]
                       :situation_boosts {:s5_the_guide 0.19}
                       :valence_delta 0.12
                       :surprise 0.07
                       :notes "The delay becomes an explanation rather than a failure."}
    :serendipity-bias 0.08
    :situations {:s1_the_approach {:activation 0.29
                                   :ripeness 0.52
                                   :hope 0.18
                                   :threat 0.31}
                 :s2_the_path {:activation 0.34
                               :ripeness 0.61
                               :hope 0.38
                               :threat 0.16}
                 :s3_the_ruins {:activation 0.24
                                :ripeness 0.44
                                :hope 0.12
                                :threat 0.28}
                 :s4_the_room {:activation 0.37
                               :ripeness 0.80
                               :hope 0.25
                               :threat 0.48}
                 :s5_the_guide {:activation 0.46
                                :ripeness 0.58
                                :hope 0.47
                                :threat 0.17}}}])

(defn benchmark-metadata
  ([] (benchmark-metadata {}))
  ([overrides]
   (merge {:started_at "2026-03-12T14:00:06Z"
           :engine_path "kernel/src/daydreamer"
           :seed "stalker-zone-benchmark"
           :benchmark "stalker_zone_6_7"
           :world_path benchmark-brief-path
           :graph_path "benchmark: synthetic"
           :feedback_path nil
           :palette_path "stalker_zone"}
          graph-counts
          overrides)))

(defn semi-unscripted-benchmark-metadata
  ([] (semi-unscripted-benchmark-metadata {}))
  ([overrides]
   (benchmark-metadata (merge {:benchmark "stalker_zone_semi_unscripted"} overrides))))

(defn benchmark-world
  []
  (assoc (runner/initial-world) :cycle 5))

(defn semi-unscripted-cycle-scripts
  []
  [{:timestamp "2026-03-12T14:00:06Z"
    :auto-family-plans? true
    :active-indices [:wish :fear :threshold :honesty]
    :retrievals [{:node-id "n06_phone_without_voice"
                  :episode-id :ep-6
                  :marks 3
                  :threshold 3
                  :overlap [:fear :honesty :wish]}]
    :chosen-node-id "n06_phone_without_voice"
    :selection {:policy :gravitational_pull
                :edge_kind :gravitational_pull
                :benchmark_step :cycle_6_room_dread}
    :feedback-applied {:director_concepts [:room :phone :confession]
                       :situation_boosts {:s4_the_room 0.11}
                       :notes "The phone at the center makes arrival feel like confession."}
    :serendipity-bias 0.04
    :situations {:s1_the_approach {:activation 0.33
                                   :ripeness 0.54
                                   :hope 0.17
                                   :threat 0.38}
                 :s2_the_path {:activation 0.39
                               :ripeness 0.62
                               :hope 0.36
                               :threat 0.19}
                 :s3_the_ruins {:activation 0.28
                                :ripeness 0.45
                                :hope 0.10
                                :threat 0.34}
                 :s4_the_room {:activation 0.52
                               :ripeness 0.82
                               :hope 0.22
                               :threat 0.67}
                 :s5_the_guide {:activation 0.26
                                :ripeness 0.51
                                :hope 0.31
                                :threat 0.24}}
    :terminate {:status :failed}}
   {:timestamp "2026-03-12T14:00:07Z"
    :auto-family-plans? true
    :cycle-adapter adapter/apply-branch-derived-state
    :serendipity-bias 0.08}])

(defn run-benchmark
  "Run the scripted Stalker Zone benchmark."
  ([] (run-benchmark {}))
  ([metadata-overrides]
   (let [root-id :cx-1
         [world trigger-state] (seed-rationalization-trigger (benchmark-world) root-id)
         [world goal-ids] (runner/activate-goals world root-id goal-specs)
         benchmark-state (merge trigger-state
                                (benchmark-goal-ids goal-ids))
         {:keys [world log]}
         (runner/run-scripted-session
          world
          (cycle-scripts benchmark-state)
          (benchmark-metadata metadata-overrides))]
     {:world world
      :log log
      :benchmark-state benchmark-state})))

(defn run-semi-unscripted-benchmark
  "Run the Stalker Zone benchmark with automatic family activation and
  rationalization planning."
  ([] (run-semi-unscripted-benchmark {}))
  ([metadata-overrides]
   (let [root-id :cx-1
         [world trigger-state]
         (seed-rationalization-trigger (assoc (benchmark-world)
                                              :auto-activate-family-goals? true)
                                       root-id)
         [world goal-ids] (runner/activate-goals world root-id semi-unscripted-goal-specs)
         benchmark-state (merge trigger-state
                                (semi-unscripted-goal-ids goal-ids))
         {:keys [world log]}
         (runner/run-scripted-session
          world
          (semi-unscripted-cycle-scripts)
          (semi-unscripted-benchmark-metadata metadata-overrides))]
     {:world world
      :log log
      :benchmark-state benchmark-state})))
