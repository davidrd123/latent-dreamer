(ns daydreamer.benchmarks.arctic-expedition-adapter-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.arctic-expedition-adapter :as adapter]
            [daydreamer.runner :as runner]))

(deftest apply-reminding-derived-state-projects-roving-indices
  (let [world (assoc (runner/initial-world)
                     :trace [{:cycle-num 6
                              :selection {}
                              :situations {:s1_the_ship {:activation 0.41
                                                         :ripeness 0.72
                                                         :hope 0.38
                                                         :threat 0.22}
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
                                                         :threat 0.63}}}
                             {:cycle-num 7
                              :selected-goal {:id :g-7
                                              :goal-type :roving
                                              :situation-id :s3_the_wait}
                              :selection {:roving_active_indices [:wonder
                                                                  :horizon
                                                                  :light
                                                                  :aurora]
                                          :roving_reminded_episodes [:ep-9]}
                              :situations {}
                              :active-indices []
                              :retrievals []}])
        world (adapter/apply-reminding-derived-state world :g-7 {})
        latest (peek (:trace world))]
    (testing "adapter orders and records the salient roving indices"
      (is (= :roving_reminding_indices
             (get-in latest [:selection :adapter_policy])))
      (is (= [:aurora :light :horizon :wonder]
             (get-in latest [:selection :adapter_active_indices]))))
    (testing "roving indices shift attention to the horizon arrival"
      (is (= :s4_the_horizon
             (get-in latest [:selection :adapter_selected_situation])))
      (is (= :roving
             (get-in latest [:selected-goal :goal-type])))
      (is (= :s4_the_horizon
             (get-in latest [:selected-goal :situation-id])))
      (is (= "n07_aurora_watch"
             (:chosen-node-id latest)))
      (is (= :ep-9
             (get-in latest [:retrievals 0 :episode-id])))
      (is (= [:aurora :light :horizon]
             (get-in latest [:retrievals 0 :overlap]))))))

(deftest apply-reminding-derived-state-annotates-no-roving-indices
  (let [world (assoc (runner/initial-world)
                     :trace [{:cycle-num 1
                              :selection {:policy :highest_strength}
                              :situations {}}])
        world (adapter/apply-reminding-derived-state world :g-1 {})
        latest (peek (:trace world))]
    (is (= :highest_strength
           (get-in latest [:selection :policy])))
    (is (= :no_roving_indices
           (get-in latest [:selection :adapter_policy])))
    (is (= []
           (get-in latest [:selection :adapter_active_indices])))))

(deftest apply-reminding-derived-state-ignores-generic-active-indices
  (let [world (assoc (runner/initial-world)
                     :trace [{:cycle-num 6
                              :selection {}
                              :situations {}}
                             {:cycle-num 7
                              :selected-goal {:id :g-7
                                              :goal-type :roving
                                              :situation-id :s3_the_wait}
                              :selection {}
                              :situations {}
                              :active-indices [:aurora :light :horizon]
                              :retrievals []}])
        world (adapter/apply-reminding-derived-state world :g-7 {})
        latest (peek (:trace world))]
    (is (= :no_roving_indices
           (get-in latest [:selection :adapter_policy])))
    (is (= :roving
           (get-in latest [:selected-goal :goal-type])))
    (is (= :s3_the_wait
           (get-in latest [:selected-goal :situation-id])))
    (is (nil? (:chosen-node-id latest)))))
