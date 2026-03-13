(ns daydreamer.runner-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.context :as cx]
            [daydreamer.episodic-memory :as episodic]
            [daydreamer.runner :as runner]))

(defn seed-reversal-target
  [world]
  (let [[world old-context-id] (cx/sprout world :cx-1)
        old-top-level-goal-id :g-old
        old-leaf-goal-id :g-old-leaf
        leaf-objective-fact {:fact/type :assumption
                             :fact/id :admission_stays_hidden}
        world (-> world
                  (cx/assert-fact old-context-id {:fact/type :situation
                                                  :fact/id :s1_seeing_through})
                  (cx/assert-fact old-context-id leaf-objective-fact)
                  (cx/assert-fact old-context-id {:fact/type :emotion
                                                  :emotion-id :e-fear
                                                  :strength 0.7})
                  (cx/assert-fact old-context-id {:fact/type :goal
                                                  :goal-id old-top-level-goal-id
                                                  :top-level-goal old-top-level-goal-id
                                                  :status :failed
                                                  :activation-context old-context-id})
                  (cx/assert-fact old-context-id {:fact/type :goal
                                                  :goal-id old-leaf-goal-id
                                                  :top-level-goal old-top-level-goal-id
                                                  :status :runable
                                                  :activation-context old-context-id
                                                  :strength 0.3
                                                  :objective-fact leaf-objective-fact})
                  (cx/assert-fact old-context-id {:fact/type :intends
                                                  :from-goal-id old-top-level-goal-id
                                                  :to-goal-id old-leaf-goal-id
                                                  :top-level-goal old-top-level-goal-id}))]
    [world {:old-context-id old-context-id
            :old-top-level-goal-id old-top-level-goal-id
            :old-leaf-goal-id old-leaf-goal-id
            :leaf-objective-fact leaf-objective-fact}]))

(deftest initial-world-test
  (let [world (runner/initial-world)]
    (is (= :daydreaming (:mode world)))
    (is (= :cx-1 (:reality-context world)))
    (is (= [] (:trace world)))))

(deftest activate-goals-test
  (let [[world goal-ids]
        (runner/activate-goals
         (runner/initial-world)
         :cx-1
         [{:goal-type :reversal
           :planning-type :imaginary
           :strength 0.9
           :main-motiv :e-1
           :situation-id :s1_seeing_through}
          {:goal-type :rehearsal
           :strength 0.4
           :main-motiv :e-2
           :situation-id :s4_the_ring}])]
    (is (= 2 (count goal-ids)))
    (is (= 2 (count (:goals world))))
    (is (= :s1_seeing_through
           (get-in world [:goals (first goal-ids) :situation-id])))))

(deftest run-scripted-cycle-enriches-trace-and-step
  (let [[world goal-ids]
        (runner/activate-goals
         (runner/initial-world)
         :cx-1
         [{:goal-type :reversal
           :planning-type :imaginary
           :strength 0.9
           :main-motiv :e-1
           :situation-id :s1_seeing_through}])
        [world selected-goal-id]
        (runner/run-scripted-cycle
         world
         {:timestamp "2026-03-12T12:00:00Z"
          :active-indices [:s1_seeing_through :reversal]
          :retrievals [{:episode-id :ep-7
                        :marks 2
                        :threshold 2}]
          :chosen-node-id "n09_tear_the_set"
          :sprouts [{:ordering 0.9} {:ordering 0.3}]
          :step? true
          :situations {:s1_seeing_through {:activation 0.95
                                           :ripeness 0.8}}})]
    (is (= (first goal-ids) selected-goal-id))
    (is (= 1 (count (:trace world))))
    (is (= "n09_tear_the_set" (get-in world [:trace 0 :chosen-node-id])))
    (is (= [:s1_seeing_through :reversal]
           (get-in world [:trace 0 :active-indices])))
    (is (= 2 (count (get-in world [:trace 0 :sprouted]))))
    (is (some? (get-in world [:trace 0 :selection :next-context])))))

(deftest run-scripted-session-exports-reporter-log
  (let [[world _]
        (runner/activate-goals
         (runner/initial-world)
         :cx-1
         [{:goal-type :reversal
           :planning-type :imaginary
           :strength 0.9
           :main-motiv :e-1
           :situation-id :s1_seeing_through}
          {:goal-type :rehearsal
           :strength 0.4
           :main-motiv :e-2
           :situation-id :s4_the_ring}])
        {:keys [world log]}
        (runner/run-scripted-session
         world
         [{:timestamp "2026-03-12T12:00:00Z"
           :active-indices [:s1_seeing_through :reversal]
           :retrievals [{:episode-id :ep-7
                         :marks 2
                         :threshold 2
                         :overlap [:s1_seeing_through]}]
           :chosen-node-id "n09_tear_the_set"
           :sprouts [{:ordering 0.9}]
           :step? true
           :situations {:s1_seeing_through {:activation 0.95
                                            :ripeness 0.8}}}
          {:timestamp "2026-03-12T12:00:01Z"
           :chosen-node-id "n10_the_ring"
           :active-indices [:s4_the_ring :rehearsal]
           :selection {:policy :followup}
           :terminate {:status :succeeded
                       :result-fact {:fact/type :goal-outcome
                                     :goal-id :placeholder
                                     :status :succeeded}}
           :situations {:s4_the_ring {:activation 0.88
                                      :ripeness 0.72}}}]
         {:started_at "2026-03-12T12:00:00Z"
          :git_commit "abc123"
          :engine_path "kernel/src/daydreamer"})]
    (testing "trace is accumulated across cycles"
      (is (= 2 (count (:trace world))))
      (is (= 2 (count (get log "cycles")))))
    (testing "reporter payload includes the current schema"
      (is (= "abc123" (get log "git_commit")))
      (is (= "kernel/src/daydreamer" (get log "engine_path")))
      (is (= "reversal" (get-in log ["cycles" 0 "selected_goal" "goal_type"])))
      (is (= "n09_tear_the_set" (get-in log ["cycles" 0 "chosen_node_id"])))
      (is (= "n10_the_ring" (get-in log ["cycles" 1 "chosen_node_id"]))))
    (testing "termination enriches the later trace"
      (is (= :succeeded (get-in world [:trace 1 :terminations 0 :status]))))))

(deftest run-scripted-cycle-can-invoke-real-reversal-branch
  (let [[world goal-ids]
        (runner/activate-goals
         (runner/initial-world)
         :cx-1
         [{:goal-type :reversal
           :planning-type :imaginary
           :strength 0.9
           :main-motiv :e-1
           :situation-id :s1_seeing_through}])
        [world old-context-id] (cx/sprout world :cx-1)
        old-top-level-goal-id :g-old
        world (-> world
                  (cx/assert-fact old-context-id {:fact/type :situation
                                                  :fact/id :s1_seeing_through})
                  (cx/assert-fact old-context-id {:fact/type :emotion
                                                  :emotion-id :e-fear
                                                  :strength 0.7})
                  (cx/assert-fact old-context-id {:fact/type :dependency
                                                  :from-id :e-fear
                                                  :to-id old-top-level-goal-id})
                  (cx/assert-fact old-context-id {:fact/type :goal
                                                  :goal-id old-top-level-goal-id
                                                  :top-level-goal old-top-level-goal-id
                                                  :status :failed
                                                  :activation-context old-context-id}))
        [world selected-goal-id]
        (runner/run-scripted-cycle
         world
         {:timestamp "2026-03-12T12:00:00Z"
          :active-indices [:s1_seeing_through :reversal]
          :reversal-branch {:old-context-id old-context-id
                            :old-top-level-goal-id old-top-level-goal-id
                            :ordering 0.9
                            :input-facts [{:fact/type :counterfactual
                                           :fact/id :wall_was_open}]}
          :situations {:s1_seeing_through {:activation 0.95
                                           :ripeness 0.8}}})]
    (is (= (first goal-ids) selected-goal-id))
    (is (= 1 (count (:trace world))))
    (is (= 1 (count (get-in world [:trace 0 :sprouted]))))
    (is (= :reversal (get-in world [:trace 0 :mutations 0 :family])))
    (let [sprouted-context-id (first (get-in world [:trace 0 :sprouted]))]
      (is (= true (get-in world [:contexts sprouted-context-id :alternative-past?])))
      (is (= true (get-in world [:contexts sprouted-context-id :pseudo-sprout?])))
      (is (= :reversal
             (get-in (-> world :trace first :selection)
                     [:goal_family]))))))

(deftest run-scripted-cycle-can-discover-reversal-leaf
  (let [[world goal-ids]
        (runner/activate-goals
         (runner/initial-world)
         :cx-1
         [{:goal-type :reversal
           :planning-type :imaginary
           :strength 0.9
           :main-motiv :e-1
           :situation-id :s1_seeing_through}])
        [world {:keys [old-context-id old-top-level-goal-id old-leaf-goal-id
                       leaf-objective-fact]}]
        (seed-reversal-target world)
        [world selected-goal-id]
        (runner/run-scripted-cycle
         world
         {:timestamp "2026-03-12T12:00:00Z"
          :active-indices [:s1_seeing_through :reversal]
          :reversal-branch {:discover-leaf? true
                            :ordering 0.9
                            :input-facts [{:fact/type :counterfactual
                                           :fact/id :wall_was_open}]}
          :situations {:s1_seeing_through {:activation 0.95
                                           :ripeness 0.8}}})]
    (is (= (first goal-ids) selected-goal-id))
    (is (= old-context-id
           (get-in world [:trace 0 :selection :reversal_source_context])))
    (is (= old-top-level-goal-id
           (get-in world [:trace 0 :selection :reversal_target_goal])))
    (is (= old-leaf-goal-id
           (get-in world [:trace 0 :selection :reversal_leaf_goal])))
    (is (= :intends_weak_leaf
           (get-in world [:trace 0 :selection :reversal_leaf_policy])))
    (is (= :emotion_then_depth
           (get-in world [:trace 0 :selection :reversal_target_policy])))
    (is (= [:admission_stays_hidden]
           (get-in world [:trace 0 :selection :reversal_leaf_retracted_facts])))
    (is (= 1
           (get-in world [:trace 0 :selection :reversal_branch_count])))
    (let [sprouted-context-id (first (get-in world [:trace 0 :sprouted]))]
      (is (not (cx/fact-true? world sprouted-context-id leaf-objective-fact))))))

(deftest run-scripted-cycle-can-derive-reversal-counterfactuals
  (let [[world goal-ids]
        (runner/activate-goals
         (runner/initial-world)
         :cx-1
         [{:goal-type :reversal
           :planning-type :imaginary
           :strength 0.9
           :main-motiv :e-1
           :situation-id :s1_seeing_through}])
        [world {:keys [old-context-id old-top-level-goal-id old-leaf-goal-id
                       leaf-objective-fact]}]
        (seed-reversal-target world)
        cause-id :fc-admit-performance
        expected-input-facts [{:fact/type :counterfactual
                               :fact/id :performance_is_admitted}
                              {:fact/type :situation
                               :fact/id :s4_the_ring}]
        world (cx/assert-fact world old-context-id
                              {:fact/type :failure-cause
                               :fact/id cause-id
                               :goal-id old-top-level-goal-id
                               :priority 0.9
                               :counterfactual-facts expected-input-facts})
        [world selected-goal-id]
        (runner/run-scripted-cycle
         world
         {:timestamp "2026-03-12T12:00:00Z"
          :active-indices [:s1_seeing_through :reversal]
          :reversal-branch {:discover-leaf? true
                            :derive-counterfactuals? true
                            :ordering 0.9}
          :situations {:s1_seeing_through {:activation 0.95
                                           :ripeness 0.8}}})]
    (is (= (first goal-ids) selected-goal-id))
    (is (= expected-input-facts
           (get-in world [:trace 0 :mutations 0 :input-facts])))
    (is (= cause-id
           (get-in world [:trace 0 :selection :reversal_counterfactual_source])))
    (is (= :stored_priority
           (get-in world [:trace 0 :selection :reversal_counterfactual_policy])))
    (is (= old-leaf-goal-id
           (get-in world [:trace 0 :selection :reversal_leaf_goal])))
    (let [sprouted-context-id (first (get-in world [:trace 0 :sprouted]))]
      (is (not (cx/fact-true? world sprouted-context-id leaf-objective-fact))))
    (is (= [:performance_is_admitted :s4_the_ring]
           (get-in world [:trace 0 :selection :reversal_counterfactual_fact_ids])))))

(deftest run-scripted-cycle-can-invoke-real-roving-branch
  (let [[world goal-ids]
        (runner/activate-goals
         (runner/initial-world)
         :cx-1
         [{:goal-type :roving
           :planning-type :imaginary
           :strength 0.7
           :main-motiv :e-relief
           :situation-id :s2_the_mission}])
        [world pleasant-episode-id]
        (episodic/add-episode world {:rule :pleasant-memory})
        world (-> world
                  (episodic/store-episode pleasant-episode-id :warmth {:reminding? true})
                  (episodic/store-episode pleasant-episode-id :crowd {:reminding? true}))
        [world linked-episode-id]
        (episodic/add-episode world {:rule :linked-memory})
        world (-> world
                  (episodic/store-episode linked-episode-id :warmth {:reminding? true})
                  (assoc :roving-episodes [pleasant-episode-id]))
        [world selected-goal-id]
        (runner/run-scripted-cycle
         world
         {:timestamp "2026-03-12T12:00:00Z"
          :active-indices [:s2_the_mission :roving]
          :roving-branch {}
          :situations {:s2_the_mission {:activation 0.72
                                        :ripeness 0.66}}})]
    (is (= (first goal-ids) selected-goal-id))
    (is (= 1 (count (get-in world [:trace 0 :sprouted]))))
    (is (= :roving (get-in world [:trace 0 :mutations 0 :family])))
    (is (= pleasant-episode-id
           (get-in world [:trace 0 :selection :roving_seed_episode])))
    (is (= [linked-episode-id]
           (get-in world [:trace 0 :selection :roving_reminded_episodes])))
    (is (= :pleasant_episode_seed
           (get-in world [:trace 0 :selection :roving_selection_policy])))))

(deftest run-scripted-cycle-can-apply-cycle-adapter
  (let [[world goal-ids]
        (runner/activate-goals
         (runner/initial-world)
         :cx-1
         [{:goal-type :reversal
           :planning-type :imaginary
           :strength 0.9
           :main-motiv :e-1
           :situation-id :s1_seeing_through}])
        [world selected-goal-id]
        (runner/run-scripted-cycle
         world
         {:timestamp "2026-03-12T12:00:00Z"
          :cycle-adapter (fn [current-world _selected-goal-id _script]
                           (assoc-in current-world
                                     [:trace 0 :chosen-node-id]
                                     "n10_honest_ring"))})]
    (is (= (first goal-ids) selected-goal-id))
    (is (= "n10_honest_ring"
           (get-in world [:trace 0 :chosen-node-id])))))

(deftest run-scripted-cycle-can-invoke-real-rationalization-branch
  (let [[world goal-ids]
        (runner/activate-goals
         (runner/initial-world)
         :cx-1
         [{:goal-type :rationalization
           :planning-type :imaginary
           :strength 0.82
           :main-motiv :e-dread
           :situation-id :s4_the_room}])
        [world trigger-context-id] (cx/sprout world :cx-1)
        failed-goal-id :g-room-failure
        frame-id :rf-zone-mercy
        reframe-facts [{:fact/type :situation
                        :fact/id :s5_the_guide}
                       {:fact/type :rationalization
                        :fact/id :zone_is_mercy}
                       {:fact/type :rationalization
                        :fact/id :delay_is_faith}]
        world (-> world
                  (cx/assert-fact trigger-context-id {:fact/type :goal
                                                      :goal-id failed-goal-id
                                                      :top-level-goal failed-goal-id
                                                      :status :failed
                                                      :activation-context trigger-context-id})
                  (cx/assert-fact trigger-context-id {:fact/type :emotion
                                                      :emotion-id :e-dread
                                                      :strength 0.82
                                                      :valence :negative})
                  (cx/assert-fact trigger-context-id {:fact/type :dependency
                                                      :from-id :e-dread
                                                      :to-id failed-goal-id})
                  (cx/assert-fact trigger-context-id {:fact/type :rationalization-frame
                                                      :fact/id frame-id
                                                      :goal-id failed-goal-id
                                                      :priority 0.91
                                                      :reframe-facts reframe-facts}))
        [world selected-goal-id]
        (runner/run-scripted-cycle
         world
         {:timestamp "2026-03-12T12:00:00Z"
          :active-indices [:wish :fear :honesty]
          :rationalization-branch {:trigger-context-id trigger-context-id
                                   :failed-goal-id failed-goal-id}
          :situations {:s4_the_room {:activation 0.82
                                     :ripeness 0.88}}})]
    (is (= (first goal-ids) selected-goal-id))
    (is (= 1 (count (get-in world [:trace 0 :sprouted]))))
    (is (= :rationalization (get-in world [:trace 0 :mutations 0 :family])))
    (is (= :rf-zone-mercy
           (get-in world [:trace 0 :selection :rationalization_frame_id])))
    (is (= [:s5_the_guide :zone_is_mercy :delay_is_faith]
           (get-in world [:trace 0 :selection :rationalization_reframe_fact_ids])))
    (is (= :divert_emot_to_tlg_bridge
           (get-in world [:trace 0 :selection :rationalization_diversion_policy])))
    (is (= :e-dread
           (get-in world [:trace 0 :selection :rationalization_trigger_emotion_id])))
    (is (= 0.82
           (get-in world [:trace 0 :selection :rationalization_trigger_emotion_before])))
    (is (= (- 0.82 (* 0.82 0.35 0.91))
           (get-in world [:trace 0 :selection :rationalization_trigger_emotion_after])))
    (is (= :rf-zone-mercy-hope
           (get-in world [:trace 0 :selection :rationalization_hope_emotion_id])))
    (is (= (* 0.82 0.35 0.91)
           (get-in world [:trace 0 :selection :rationalization_hope_strength])))
    (is (= :s5_the_guide
           (get-in world [:trace 0 :selection :rationalization_hope_situation])))
    (is (= [{:emotion-id :e-dread
             :from-strength 0.82
             :to-strength (- 0.82 (* 0.82 0.35 0.91))
             :delta (- (- 0.82 (* 0.82 0.35 0.91)) 0.82)
             :valence :negative
             :affect nil
             :situation-id nil
             :context-id (first (get-in world [:trace 0 :sprouted]))
             :role :trigger}
            {:emotion-id :rf-zone-mercy-hope
             :from-strength 0.0
             :to-strength (* 0.82 0.35 0.91)
             :delta (* 0.82 0.35 0.91)
             :valence :positive
             :affect :hope
             :situation-id :s5_the_guide
             :context-id (first (get-in world [:trace 0 :sprouted]))
             :role :reframe}]
           (get-in world [:trace 0 :emotion-shifts])))
    (let [sprouted-context-id (first (get-in world [:trace 0 :sprouted]))]
      (is (cx/fact-true? world sprouted-context-id
                         {:fact/type :situation
                          :fact/id :s5_the_guide}))
      (is (= :rationalization
             (get-in (-> world :trace first :selection)
                     [:goal_family]))))))
