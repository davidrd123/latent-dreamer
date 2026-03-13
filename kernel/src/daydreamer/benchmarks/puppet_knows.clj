(ns daydreamer.benchmarks.puppet-knows
  "Scripted Puppet Knows benchmark for cross-engine trace comparison.

  This is intentionally small and explicit. It does not parse the Python
  fixture files inside the pure kernel. Instead it encodes the first benchmark
  window the parallel-track spec cares about: the cycle 9 -> 10 transition
  from revenge in s1 to rehearsal in s4, with cycle 8 included as the causal
  bridge from apparatus-dread into anger at the set."
  (:require [daydreamer.benchmarks.puppet-knows-adapter :as adapter]
            [daydreamer.context :as cx]
            [daydreamer.episodic-memory :as episodic]
            [daydreamer.runner :as runner]))

(def fixture-relative-paths
  "Paths to the shared Puppet Knows assets, relative to the `scope-drd` repo."
  {:world-path "content/daydreams/puppet_knows/world.yaml"
   :graph-path "content/daydreams/puppet_knows/dream_graph.json"
   :feedback-path "content/daydreams/puppet_knows/director_feedback.json"
   :reporter-path "tools/daydream_trace_report.py"})

(def graph-counts
  "Graph size from the shared Puppet Knows fixture."
  {:graph_nodes 20
   :graph_edges 28})

(def goal-specs
  "Top-level goals for the benchmark window.

  Strengths here represent control pressure in the benchmark window, not the
  node-local tension values in the graph."
  [{:goal-type :repercussions
    :planning-type :imaginary
    :strength 0.86
    :main-motiv :e-apparatus-dread
    :situation-id :s3_the_edge}
   {:goal-type :reversal
    :planning-type :imaginary
    :strength 0.18
    :main-motiv :e-counterfactual-pressure
    :situation-id :s1_seeing_through}
   {:goal-type :revenge
    :planning-type :imaginary
    :strength 0.82
    :main-motiv :e-set-anger
    :situation-id :s1_seeing_through}
   {:goal-type :rehearsal
    :planning-type :real
    :strength 0.42
    :main-motiv :e-honest-performance
    :situation-id :s4_the_ring}])

(def semi-unscripted-goal-specs
  [{:goal-type :repercussions
    :planning-type :imaginary
    :strength 0.86
    :main-motiv :e-apparatus-dread
    :situation-id :s3_the_edge}
   {:goal-type :revenge
    :planning-type :imaginary
    :strength 0.82
    :main-motiv :e-set-anger
    :situation-id :s1_seeing_through}
   {:goal-type :rehearsal
    :planning-type :real
    :strength 0.42
    :main-motiv :e-honest-performance
    :situation-id :s4_the_ring}])

(defn- assert-facts
  [world context-id facts]
  (reduce (fn [current-world fact]
            (cx/assert-fact current-world context-id fact))
          world
          facts))

(defn- seed-reversal-setup
  [world root-id]
  (let [[world old-context-id] (cx/sprout world root-id)
        old-top-level-goal-id :g_fixture_old_failure
        old-leaf-goal-id :g_fixture_old_leaf
        other-goal-id :g_fixture_other_plan
        preferred-cause-id :fc_admit_performance
        leaf-objective-fact {:fact/type :assumption
                             :fact/id :performance_stays_hidden}
        facts [{:fact/type :situation
                :fact/id :s1_seeing_through}
               {:fact/type :situation
                :fact/id :s3_the_edge}
               leaf-objective-fact
               {:fact/type :emotion
                :emotion-id :e_fixture_apparatus_dread
                :strength 0.74
                :valence :negative}
               {:fact/type :dependency
                :from-id :e_fixture_apparatus_dread
                :to-id old-top-level-goal-id}
               {:fact/type :goal
                :goal-id old-top-level-goal-id
                :top-level-goal old-top-level-goal-id
                :status :failed
                :activation-context old-context-id}
               {:fact/type :goal
                :goal-id old-leaf-goal-id
                :top-level-goal old-top-level-goal-id
                :status :runable
                :activation-context old-context-id
                :strength 0.32
                :objective-fact leaf-objective-fact}
               {:fact/type :goal
                :goal-id other-goal-id
                :top-level-goal other-goal-id
                :status :runable
                :activation-context old-context-id}
               {:fact/type :intends
                :from-goal-id old-top-level-goal-id
                :to-goal-id old-leaf-goal-id
                :top-level-goal old-top-level-goal-id}
               {:fact/type :intends
                :from-goal-id other-goal-id
                :to-goal-id :g_fixture_other_leaf
                :top-level-goal other-goal-id}
               {:fact/type :failure-cause
                :fact/id preferred-cause-id
                :goal-id old-top-level-goal-id
                :priority 0.91
                :counterfactual-facts [{:fact/type :counterfactual
                                        :fact/id :performance_is_admitted}
                                       {:fact/type :situation
                                        :fact/id :s4_the_ring}]}
               {:fact/type :failure-cause
                :fact/id :fc_hide_in_backstage
                :goal-id old-top-level-goal-id
                :priority 0.22
                :counterfactual-facts [{:fact/type :counterfactual
                                        :fact/id :stay_backstage}]}]
        world (assert-facts world old-context-id facts)]
    [world {:old-context-id old-context-id
            :old-top-level-goal-id old-top-level-goal-id
            :old-leaf-goal-id old-leaf-goal-id
            :leaf-objective-fact leaf-objective-fact
            :preferred-cause-id preferred-cause-id
            :expected-counterfactual-facts [{:fact/type :counterfactual
                                             :fact/id :performance_is_admitted}
                                            {:fact/type :situation
                                             :fact/id :s4_the_ring}]}]))

(defn- seed-roving-episodes
  [world]
  (let [[world pleasant-episode-id]
        (episodic/add-episode world {:rule :puppet-knows-roving-seed})
        world (-> world
                  (episodic/store-episode pleasant-episode-id :honesty {:reminding? true})
                  (episodic/store-episode pleasant-episode-id :performance {:reminding? true})
                  (episodic/store-episode pleasant-episode-id :crowd {:reminding? true}))
        [world linked-episode-id]
        (episodic/add-episode world {:rule :puppet-knows-roving-follow-on})
        world (-> world
                  (episodic/store-episode linked-episode-id :performance {:reminding? true})
                  (episodic/store-episode linked-episode-id :ritual {:reminding? true})
                  (assoc :roving-episodes [pleasant-episode-id]))]
    [world {:pleasant-episode-id pleasant-episode-id
            :linked-episode-id linked-episode-id}]))

(defn- benchmark-goal-ids
  [goal-ids]
  {:repercussions-goal-id (nth goal-ids 0 nil)
   :reversal-goal-id (nth goal-ids 1 nil)
   :revenge-goal-id (nth goal-ids 2 nil)
   :rehearsal-goal-id (nth goal-ids 3 nil)})

(defn- semi-unscripted-goal-ids
  [goal-ids]
  {:repercussions-goal-id (nth goal-ids 0 nil)
   :revenge-goal-id (nth goal-ids 1 nil)
   :rehearsal-goal-id (nth goal-ids 2 nil)})

(defn cycle-scripts
  "Three-cycle benchmark bridge:

  - cycle 8: n08_inventory_dominos
  - cycle 9: n09_tear_the_set
  - cycle 10: n10_honest_ring

  The leaf choice now goes through kernel discovery (`reverse-leafs` style
  candidate selection), and the counterfactual input facts are derived from
  stored failure-cause facts on that leaf."
  [{:keys [reversal-goal-id]}]
  [{:timestamp "2026-03-12T12:00:08Z"
    :active-indices [:edge :void :backstage :stored_scenery :consequence :darkness]
    :retrievals [{:node-id "n05_peel_the_wall"
                  :episode-id :ep-5
                  :marks 3
                  :threshold 3
                  :overlap [:backstage :darkness :edge]}
                 {:node-id "n06_overhead_set_edge"
                  :episode-id :ep-6
                  :marks 3
                  :threshold 3
                  :overlap [:awareness :backstage :void]}]
    :chosen-node-id "n08_inventory_dominos"
    :selection {:policy :highest_strength
                :edge_kind :coincidence
                :benchmark_step :cycle_8_bridge}
    :feedback-applied {:director_concepts [:stored_scenery :apparatus]
                       :situation_boosts {:s1_seeing_through 0.05}
                       :emotional_episodes [{:affect :anger
                                             :target :the_set
                                             :source_situation :s3_the_edge
                                             :intensity 0.24
                                             :decay 0.91
                                             :indices [:stored_scenery
                                                       :seam
                                                       :performance
                                                       :anger]}]
                       :notes "Stored scenery reads as apparatus and turns dread into anger at the set."}
    :reversal-branch {:discover-leaf? true
                      :derive-counterfactuals? true
                      :new-top-level-goal-id reversal-goal-id
                      :ordering 0.61}
    :serendipity-bias 0.04
    :situations {:s1_seeing_through {:activation 0.48
                                     :ripeness 0.72
                                     :anger 0.24
                                     :hope 0.19
                                     :threat 0.47}
                 :s2_the_mission {:activation 0.31
                                  :ripeness 0.52
                                  :anger 0.10
                                  :hope 0.71
                                  :threat 0.34}
                 :s3_the_edge {:activation 0.44
                               :ripeness 0.43
                               :anger 0.18
                               :hope 0.11
                               :threat 0.63}
                 :s4_the_ring {:activation 0.03
                               :ripeness 0.30
                               :anger 0.17
                               :hope 0.40
                               :threat 0.22}}
    :terminate {:status :succeeded}}
   {:timestamp "2026-03-12T12:00:09Z"
    :active-indices [:ritual :combat :honesty :crowd :seam :anger :performance]
    :retrievals [{:node-id "n08_inventory_dominos"
                  :episode-id :ep-8
                  :marks 3
                  :threshold 3
                  :overlap [:anger :performance :seam]}]
    :chosen-node-id "n09_tear_the_set"
    :selection {:policy :highest_strength
                :edge_kind :return
                :benchmark_step :cycle_9_revenge}
    :feedback-applied {:director_concepts [:honesty :performance]
                       :situation_boosts {:s4_the_ring 0.12}
                       :valence_delta 0.04
                       :surprise 0.06
                       :notes "Revenge inside the ring wakes the honest performance frame."}
    :serendipity-bias 0.06
    :situations {:s1_seeing_through {:activation 0.55
                                     :ripeness 0.76
                                     :anger 0.31
                                     :hope 0.17
                                     :threat 0.36}
                 :s2_the_mission {:activation 0.29
                                  :ripeness 0.50
                                  :anger 0.10
                                  :hope 0.66
                                  :threat 0.33}
                 :s3_the_edge {:activation 0.38
                               :ripeness 0.40
                               :anger 0.17
                               :hope 0.09
                               :threat 0.56}
                 :s4_the_ring {:activation 0.15
                               :ripeness 0.34
                               :anger 0.21
                               :hope 0.52
                               :threat 0.20}}
    :terminate {:status :succeeded}}
   {:timestamp "2026-03-12T12:00:10Z"
    :active-indices [:ritual :honesty :crowd :performance :sincerity :non_directed_light]
    :retrievals [{:node-id "n09_tear_the_set"
                  :episode-id :ep-9
                  :marks 2
                  :threshold 2
                  :overlap [:honesty :performance :ritual]}]
    :chosen-node-id "n10_honest_ring"
    :selection {:policy :feedback_woke_ring
                :edge_kind :association
                :benchmark_step :cycle_10_arrival}
    :serendipity-bias 0.08
    :situations {:s1_seeing_through {:activation 0.34
                                     :ripeness 0.70
                                     :anger 0.18
                                     :hope 0.22
                                     :threat 0.28}
                 :s2_the_mission {:activation 0.36
                                  :ripeness 0.55
                                  :anger 0.10
                                  :hope 0.68
                                  :threat 0.30}
                 :s3_the_edge {:activation 0.22
                               :ripeness 0.37
                               :anger 0.11
                               :hope 0.16
                               :threat 0.42}
                 :s4_the_ring {:activation 0.33
                               :ripeness 0.47
                               :anger 0.16
                               :hope 0.61
                               :threat 0.14}}}])

(defn benchmark-metadata
  "Build reporter metadata for the benchmark run."
  ([] (benchmark-metadata {}))
  ([overrides]
   (merge {:started_at "2026-03-12T12:00:08Z"
           :engine_path "kernel/src/daydreamer"
           :seed "puppet-knows-benchmark"
           :benchmark "puppet_knows_8_10"}
          graph-counts
          overrides)))

(defn semi-unscripted-benchmark-metadata
  ([] (semi-unscripted-benchmark-metadata {}))
  ([overrides]
   (benchmark-metadata (merge {:benchmark "puppet_knows_semi_unscripted"} overrides))))

(defn benchmark-world
  "Seed a world so the scripted session emits cycles 8, 9, and 10."
  []
  (assoc (runner/initial-world) :cycle 7))

(defn semi-unscripted-cycle-scripts
  []
  [{:timestamp "2026-03-12T12:00:08Z"
    :auto-family-plans? true
    :active-indices [:edge :void :backstage :stored_scenery :consequence :darkness]
    :feedback-applied {:director_concepts [:stored_scenery :apparatus]
                       :situation_boosts {:s1_seeing_through 0.05}
                       :notes "Stored scenery reads as apparatus and turns dread into anger at the set."}
    :serendipity-bias 0.04
    :situations {:s1_seeing_through {:activation 0.48
                                     :ripeness 0.72
                                     :anger 0.24
                                     :hope 0.19
                                     :threat 0.47}
                 :s2_the_mission {:activation 0.31
                                  :ripeness 0.52
                                  :anger 0.10
                                  :hope 0.71
                                  :threat 0.34}
                 :s3_the_edge {:activation 0.44
                               :ripeness 0.43
                               :anger 0.18
                               :hope 0.11
                               :threat 0.63}
                 :s4_the_ring {:activation 0.03
                               :ripeness 0.30
                               :anger 0.17
                               :hope 0.40
                               :threat 0.22}}
    :terminate {:status :succeeded}}
   {:timestamp "2026-03-12T12:00:09Z"
    :auto-family-plans? true
    :active-indices [:ritual :combat :honesty :crowd :seam :anger :performance]
    :feedback-applied {:director_concepts [:honesty :performance]
                       :situation_boosts {:s4_the_ring 0.12}
                       :valence_delta 0.04
                       :surprise 0.06
                       :notes "Revenge inside the ring wakes the honest performance frame."}
    :serendipity-bias 0.06
    :situations {:s1_seeing_through {:activation 0.55
                                     :ripeness 0.76
                                     :anger 0.31
                                     :hope 0.17
                                     :threat 0.36}
                 :s2_the_mission {:activation 0.29
                                  :ripeness 0.50
                                  :anger 0.10
                                  :hope 0.66
                                  :threat 0.33}
                 :s3_the_edge {:activation 0.38
                               :ripeness 0.40
                               :anger 0.17
                               :hope 0.09
                               :threat 0.56}
                 :s4_the_ring {:activation 0.15
                               :ripeness 0.34
                               :anger 0.21
                               :hope 0.52
                               :threat 0.20}}
    :terminate {:status :succeeded}}
   {:timestamp "2026-03-12T12:00:10Z"
    :auto-family-plans? true
    :cycle-adapter adapter/apply-branch-derived-state
    :serendipity-bias 0.08
    :terminate {:status :succeeded}}])

(defn run-benchmark
  "Run the scripted Puppet Knows benchmark and return
  `{:world final-world :log reporter-log}`."
  ([] (run-benchmark {}))
  ([metadata-overrides]
   (let [root-id :cx-1
         [world reversal-setup] (seed-reversal-setup (benchmark-world) root-id)
         [world goal-ids] (runner/activate-goals world root-id goal-specs)
         benchmark-state (merge reversal-setup
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
  "Run the Puppet Knows benchmark with family activations and automatic family
  plans enabled, but without scripted family branch invocation."
  ([] (run-semi-unscripted-benchmark {}))
  ([metadata-overrides]
   (let [root-id :cx-1
         [world reversal-setup] (seed-reversal-setup (assoc (benchmark-world)
                                                            :auto-activate-family-goals? true)
                                                     root-id)
         [world roving-setup] (seed-roving-episodes world)
         [world goal-ids] (runner/activate-goals world root-id semi-unscripted-goal-specs)
         benchmark-state (merge reversal-setup
                                roving-setup
                                (semi-unscripted-goal-ids goal-ids))
         {:keys [world log]}
         (runner/run-scripted-session
          world
          (semi-unscripted-cycle-scripts)
          (semi-unscripted-benchmark-metadata metadata-overrides))]
     {:world world
      :log log
      :benchmark-state benchmark-state})))
