(ns daydreamer.runner-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.context :as cx]
            [daydreamer.runner :as runner]))

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
