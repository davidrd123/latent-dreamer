(ns daydreamer.benchmarks.stalker-zone-adapter-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.stalker-zone-adapter :as adapter]
            [daydreamer.context :as cx]
            [daydreamer.runner :as runner]))

(deftest apply-branch-derived-state-projects-relevant-facts-only
  (let [[world branch-id] (cx/sprout (runner/initial-world) :cx-1)
        world (-> world
                  (cx/assert-fact branch-id {:fact/type :situation
                                             :fact/id :s5_the_guide})
                  (cx/assert-fact branch-id {:fact/type :rationalization
                                             :fact/id :zone_is_mercy})
                  (cx/assert-fact branch-id {:fact/type :rationalization
                                             :fact/id :delay_is_faith})
                  (cx/assert-fact branch-id {:fact/type :goal
                                             :goal-id :g-noisy})
                  (assoc :trace [{:cycle-num 6
                                  :selection {}
                                  :situations {:s4_the_room {:activation 0.52
                                                             :ripeness 0.82
                                                             :hope 0.22
                                                             :threat 0.67}
                                               :s5_the_guide {:activation 0.26
                                                              :ripeness 0.51
                                                              :hope 0.31
                                                              :threat 0.24}}}
                                 {:cycle-num 7
                                  :selected-goal {:id :g-7
                                                  :goal-type :rationalization
                                                  :situation-id :s4_the_room}
                                  :selection {:rationalization_branch_context branch-id}
                                  :situations {}
                                  :active-indices []
                                  :retrievals []}]))
        world (adapter/apply-branch-derived-state world :g-7 {})
        latest (peek (:trace world))]
    (testing "adapter-visible facts exclude unrelated ids"
      (is (= ["delay_is_faith" "s5_the_guide" "zone_is_mercy"]
             (get-in latest [:selection :adapter_visible_fact_ids]))))
    (testing "branch facts drive the guide-centered arrival"
      (is (= :branch_visible_facts
             (get-in latest [:selection :adapter_policy])))
      (is (= :rationalization
             (get-in latest [:selected-goal :goal-type])))
      (is (= :s5_the_guide
             (get-in latest [:selected-goal :situation-id])))
      (is (= "n07_zone_is_mercy"
             (:chosen-node-id latest)))
      (is (= [:faith :guide :sincerity :trust]
             (:active-indices latest)))
      (is (= [:faith :guide :sincerity]
             (get-in latest [:retrievals 0 :overlap]))))))

(deftest apply-branch-derived-state-annotates-no-branch-context
  (let [world (assoc (runner/initial-world)
                     :trace [{:cycle-num 1
                              :selection {:policy :highest_strength}
                              :situations {}}])
        world (adapter/apply-branch-derived-state world :g-1 {})
        latest (peek (:trace world))]
    (is (= :highest_strength
           (get-in latest [:selection :policy])))
    (is (= :no_branch_context
           (get-in latest [:selection :adapter_policy])))
    (is (= []
           (get-in latest [:selection :adapter_active_indices])))))
