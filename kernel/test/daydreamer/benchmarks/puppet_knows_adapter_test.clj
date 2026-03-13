(ns daydreamer.benchmarks.puppet-knows-adapter-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.puppet-knows-adapter :as adapter]
            [daydreamer.context :as cx]
            [daydreamer.runner :as runner]))

(deftest apply-branch-derived-state-projects-relevant-facts-only
  (let [[world branch-id] (cx/sprout (runner/initial-world) :cx-1)
        world (-> world
                  (cx/assert-fact branch-id {:fact/type :situation
                                             :fact/id :s4_the_ring})
                  (cx/assert-fact branch-id {:fact/type :counterfactual
                                             :fact/id :performance_is_admitted})
                  (cx/assert-fact branch-id {:fact/type :goal
                                             :goal-id :g-noisy})
                  (cx/assert-fact branch-id {:fact/type :emotion
                                             :emotion-id :e-noisy
                                             :strength 0.7})
                  (assoc :trace [{:cycle-num 9
                                  :selection {}
                                  :situations {:s1_seeing_through {:activation 0.34
                                                                   :ripeness 0.70
                                                                   :anger 0.18
                                                                   :hope 0.22
                                                                   :threat 0.28}
                                               :s4_the_ring {:activation 0.33
                                                             :ripeness 0.47
                                                             :anger 0.16
                                                             :hope 0.61
                                                             :threat 0.14}}}
                                 {:cycle-num 10
                                  :selected-goal {:id :g-10
                                                  :goal-type :reversal
                                                  :situation-id :s1_seeing_through}
                                  :selection {:reversal_branch_context branch-id}
                                  :situations {}
                                  :active-indices []
                                  :retrievals []}]))
        world (adapter/apply-branch-derived-state world :g-10 {})
        latest (peek (:trace world))]
    (testing "adapter-visible facts exclude unrelated goal and emotion ids"
      (is (= ["performance_is_admitted" "s4_the_ring"]
             (get-in latest [:selection :adapter_visible_fact_ids]))))
    (testing "branch facts drive the ring arrival"
      (is (= :branch_visible_facts
             (get-in latest [:selection :adapter_policy])))
      (is (= :s4_the_ring
             (get-in latest [:selected-goal :situation-id])))
      (is (= "n10_honest_ring"
             (:chosen-node-id latest)))
      (is (= [:ritual :sincerity :non_directed_light :performance :honesty]
             (:active-indices latest)))
      (is (= [:honesty :performance :ritual]
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
