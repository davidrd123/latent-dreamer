(ns daydreamer.benchmarks.arctic-expedition
  "Scripted and semi-unscripted Arctic Expedition benchmark.

  This benchmark exercises ROVING rather than REVERSAL. The target bridge is
  from pressure and waiting inside the trapped ship toward the brief's roving
  capstone: watching the aurora and briefly forgetting that escape is
  impossible."
  (:require [daydreamer.benchmarks.arctic-expedition-adapter :as adapter]
            [daydreamer.context :as cx]
            [daydreamer.episodic-memory :as episodic]
            [daydreamer.runner :as runner]))

(def benchmark-brief-path
  "Source brief for the synthetic Arctic benchmark."
  "daydreaming/Notes/experiential-design/09-brief-arctic-expedition.md")

(def graph-counts
  "Synthetic mini-graph size for the benchmark window."
  {:graph_nodes 8
   :graph_edges 10})

(def goal-specs
  [{:goal-type :repercussions
    :planning-type :imaginary
    :strength 0.78
    :main-motiv :e-hull-pressure
    :situation-id :s5_the_hull}
   {:goal-type :roving
    :planning-type :imaginary
    :strength 0.32
    :main-motiv :e-aurora-relief
    :situation-id :s4_the_horizon}])

(def semi-unscripted-goal-specs
  [{:goal-type :repercussions
    :planning-type :imaginary
    :strength 0.78
    :main-motiv :e-hull-pressure
    :situation-id :s5_the_hull}])

(defn- assert-facts
  [world context-id facts]
  (reduce (fn [current-world fact]
            (cx/assert-fact current-world context-id fact))
          world
          facts))

(defn- seed-roving-trigger
  [world root-id]
  (let [[world trigger-context-id] (cx/sprout world root-id)
        failed-goal-id :g_fixture_wait_failure
        emotion-id :e_fixture_cabin_fatigue
        facts [{:fact/type :situation
                :fact/id :s3_the_wait}
               {:fact/type :situation
                :fact/id :s5_the_hull}
               {:fact/type :goal
                :goal-id failed-goal-id
                :top-level-goal failed-goal-id
                :status :failed
                :activation-context trigger-context-id}
               {:fact/type :emotion
                :emotion-id emotion-id
                :strength 0.32
                :valence :negative}
               {:fact/type :dependency
                :from-id emotion-id
                :to-id failed-goal-id}]
        world (assert-facts world trigger-context-id facts)]
    [world {:trigger-context-id trigger-context-id
            :failed-goal-id failed-goal-id
            :emotion-id emotion-id}]))

(defn- seed-roving-episodes
  [world]
  (let [[world pleasant-episode-id]
        (episodic/add-episode world {:rule :arctic-roving-seed})
        world (-> world
                  (episodic/store-episode pleasant-episode-id :aurora {:reminding? true})
                  (episodic/store-episode pleasant-episode-id :light {:reminding? true})
                  (episodic/store-episode pleasant-episode-id :wonder {:reminding? true})
                  (episodic/store-episode pleasant-episode-id :horizon {:reminding? true}))
        [world linked-episode-id]
        (episodic/add-episode world {:rule :arctic-roving-follow-on})
        world (-> world
                  (episodic/store-episode linked-episode-id :aurora {:reminding? true})
                  (episodic/store-episode linked-episode-id :light {:reminding? true})
                  (episodic/store-episode linked-episode-id :horizon {:reminding? true})
                  (assoc :roving-episodes [pleasant-episode-id]))]
    [world {:pleasant-episode-id pleasant-episode-id
            :linked-episode-id linked-episode-id}]))

(defn- benchmark-goal-ids
  [goal-ids]
  {:repercussions-goal-id (nth goal-ids 0 nil)
   :roving-goal-id (nth goal-ids 1 nil)})

(defn- semi-unscripted-goal-ids
  [goal-ids]
  {:repercussions-goal-id (nth goal-ids 0 nil)})

(defn cycle-scripts
  [{:keys [pleasant-episode-id linked-episode-id]}]
  [{:timestamp "2026-03-12T13:00:06Z"
    :active-indices [:pressure :hull :silence :collapse]
    :retrievals [{:node-id "n06_hull_groan"
                  :episode-id :ep-hull
                  :marks 3
                  :threshold 3
                  :overlap [:pressure :hull :silence]}]
    :chosen-node-id "n06_hull_groan"
    :selection {:policy :highest_strength
                :edge_kind :pressure_return
                :benchmark_step :cycle_6_hull_pressure}
    :feedback-applied {:director_concepts [:pressure_ridge :hull]
                       :situation_boosts {:s5_the_hull 0.08}
                       :emotional_episodes [{:affect :dread
                                             :target :the_hull
                                             :source_situation :s5_the_hull
                                             :intensity 0.21
                                             :decay 0.92
                                             :indices [:pressure
                                                       :hull
                                                       :silence]}]
                       :notes "The hull groan turns maintenance into dread."}
    :serendipity-bias 0.03
    :situations {:s1_the_ship {:activation 0.41
                               :ripeness 0.72
                               :hope 0.38
                               :threat 0.22}
                 :s2_the_ice {:activation 0.36
                              :ripeness 0.64
                              :hope 0.12
                              :threat 0.44}
                 :s3_the_wait {:activation 0.47
                               :ripeness 0.58
                               :hope 0.11
                               :threat 0.52}
                 :s4_the_horizon {:activation 0.04
                                  :ripeness 0.28
                                  :hope 0.31
                                  :threat 0.07}
                 :s5_the_hull {:activation 0.53
                               :ripeness 0.67
                               :hope 0.09
                               :threat 0.63}}
    :terminate {:status :failed}}
   {:timestamp "2026-03-12T13:00:07Z"
    :roving-branch {:episode-id pleasant-episode-id}
    :active-indices [:aurora :light :horizon :wonder]
    :retrievals [{:node-id "n07_aurora_watch"
                  :episode-id linked-episode-id
                  :marks 3
                  :threshold 2
                  :overlap [:aurora :light :horizon]}]
    :chosen-node-id "n07_aurora_watch"
    :selection {:policy :pleasant_episode_seed
                :edge_kind :reminding_bridge
                :benchmark_step :cycle_7_aurora_arrival}
    :feedback-applied {:director_concepts [:aurora :horizon :cathedral_light]
                       :situation_boosts {:s4_the_horizon 0.27}
                       :valence_delta 0.18
                       :surprise 0.11
                       :notes "Wonder at the aurora briefly displaces the trapped-ship frame."}
    :serendipity-bias 0.09
    :situations {:s1_the_ship {:activation 0.33
                               :ripeness 0.70
                               :hope 0.42
                               :threat 0.18}
                 :s2_the_ice {:activation 0.31
                              :ripeness 0.60
                              :hope 0.19
                              :threat 0.34}
                 :s3_the_wait {:activation 0.25
                               :ripeness 0.55
                               :hope 0.20
                               :threat 0.31}
                 :s4_the_horizon {:activation 0.41
                                  :ripeness 0.44
                                  :hope 0.58
                                  :threat 0.06}
                 :s5_the_hull {:activation 0.29
                               :ripeness 0.61
                               :hope 0.10
                               :threat 0.47}}}])

(defn benchmark-metadata
  ([] (benchmark-metadata {}))
  ([overrides]
   (merge {:started_at "2026-03-12T13:00:06Z"
           :engine_path "kernel/src/daydreamer"
           :seed "arctic-expedition-benchmark"
           :benchmark "arctic_expedition_6_7"
           :world_path benchmark-brief-path
           :graph_path "benchmark: synthetic"
           :feedback_path nil
           :palette_path "arctic_expedition"}
          graph-counts
          overrides)))

(defn semi-unscripted-benchmark-metadata
  ([] (semi-unscripted-benchmark-metadata {}))
  ([overrides]
   (benchmark-metadata (merge {:benchmark "arctic_expedition_semi_unscripted"} overrides))))

(defn benchmark-world
  []
  (assoc (runner/initial-world) :cycle 5))

(defn semi-unscripted-cycle-scripts
  []
  [{:timestamp "2026-03-12T13:00:06Z"
    :auto-family-plans? true
    :active-indices [:pressure :hull :silence :collapse]
    :retrievals [{:node-id "n06_hull_groan"
                  :episode-id :ep-hull
                  :marks 3
                  :threshold 3
                  :overlap [:pressure :hull :silence]}]
    :chosen-node-id "n06_hull_groan"
    :selection {:policy :highest_strength
                :edge_kind :pressure_return
                :benchmark_step :cycle_6_hull_pressure}
    :feedback-applied {:director_concepts [:pressure_ridge :hull]
                       :situation_boosts {:s5_the_hull 0.08}
                       :notes "The hull groan turns maintenance into dread."}
    :serendipity-bias 0.03
    :situations {:s1_the_ship {:activation 0.41
                               :ripeness 0.72
                               :hope 0.38
                               :threat 0.22}
                 :s2_the_ice {:activation 0.36
                              :ripeness 0.64
                              :hope 0.12
                              :threat 0.44}
                 :s3_the_wait {:activation 0.47
                               :ripeness 0.58
                               :hope 0.11
                               :threat 0.52}
                 :s4_the_horizon {:activation 0.04
                                  :ripeness 0.28
                                  :hope 0.31
                                  :threat 0.07}
                 :s5_the_hull {:activation 0.53
                               :ripeness 0.67
                               :hope 0.09
                               :threat 0.63}}
    :terminate {:status :failed}}
   {:timestamp "2026-03-12T13:00:07Z"
    :auto-family-plans? true
    :cycle-adapter adapter/apply-reminding-derived-state
    :serendipity-bias 0.09}])

(defn run-benchmark
  "Run the scripted Arctic Expedition benchmark."
  ([] (run-benchmark {}))
  ([metadata-overrides]
   (let [root-id :cx-1
         [world trigger-state] (seed-roving-trigger (benchmark-world) root-id)
         [world roving-state] (seed-roving-episodes world)
         [world goal-ids] (runner/activate-goals world root-id goal-specs)
         benchmark-state (merge trigger-state
                                roving-state
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
  "Run the Arctic Expedition benchmark with automatic family activation and
  ROVING plan execution."
  ([] (run-semi-unscripted-benchmark {}))
  ([metadata-overrides]
   (let [root-id :cx-1
         [world trigger-state]
         (seed-roving-trigger (assoc (benchmark-world)
                                     :auto-activate-family-goals? true)
                              root-id)
         [world roving-state] (seed-roving-episodes world)
         [world goal-ids] (runner/activate-goals world root-id semi-unscripted-goal-specs)
         benchmark-state (merge trigger-state
                                roving-state
                                (semi-unscripted-goal-ids goal-ids))
         {:keys [world log]}
         (runner/run-scripted-session
          world
          (semi-unscripted-cycle-scripts)
          (semi-unscripted-benchmark-metadata metadata-overrides))]
     {:world world
      :log log
      :benchmark-state benchmark-state})))
