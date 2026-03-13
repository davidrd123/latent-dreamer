(ns daydreamer.control-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.context :as cx]
            [daydreamer.control :as control]
            [daydreamer.goals :as goals]))

(defn world-with-root
  []
  (let [root (cx/create-context)
        root-id (:id root)]
    [{:contexts {root-id root}
      :goals {}
      :episodes {}
      :emotions {}
      :mode :daydreaming
      :cycle 0
      :trace []
      :termination-events []
      :reality-context root-id
      :reality-lookahead root-id
      :id-counter 1}
     root-id]))

(deftest state-helpers-test
  (let [[world _] (world-with-root)
        performance-world (control/set-state world :performance)
        daydreaming-world (control/set-state world :daydreaming)]
    (is (control/performance-mode? performance-world))
    (is (not (control/daydreaming-mode? performance-world)))
    (is (control/daydreaming-mode? daydreaming-world))))

(deftest need-decay-test
  (let [[world _] (world-with-root)
        world (assoc world :needs {:n-1 {:id :n-1 :strength 1.0}})
        world (control/need-decay world)]
    (is (= 0.98 (get-in world [:needs :n-1 :strength])))))

(deftest emotion-decay-test
  (let [[world root-id] (world-with-root)
        world (assoc world
                     :emotions {:e-1 {:id :e-1 :strength 1.0}
                                :e-2 {:id :e-2 :strength 0.1}})
        [world _] (goals/activate-top-level-goal
                   world
                   root-id
                   {:goal-type :reversal
                    :strength 0.7
                    :main-motiv :e-1})
        world (control/emotion-decay world)]
    (testing "motivating emotions are exempt from decay"
      (is (= 1.0 (get-in world [:emotions :e-1 :strength]))))
    (testing "weak non-motivating emotions decay below threshold and are removed"
      (is (= nil (get-in world [:emotions :e-2]))))))

(deftest run-cycle-selects-strongest-goal
  (let [[world root-id] (world-with-root)
        [world g1] (goals/activate-top-level-goal
                    world
                    root-id
                    {:goal-type :reversal
                     :planning-type :imaginary
                     :strength 0.9
                     :main-motiv :e-1
                     :situation-id :s1_seeing_through})
        [world _] (goals/activate-top-level-goal
                   world
                   root-id
                   {:goal-type :roving
                    :strength 0.4
                    :main-motiv :e-2
                    :situation-id :s2_the_puppet})
        [world selected-goal] (control/run-cycle world)]
    (is (= g1 selected-goal))
    (is (= 1 (:cycle world)))
    (is (= 1 (count (:trace world))))
    (is (= g1 (get-in world [:trace 0 :selected-goal :id])))
    (is (= [g1] (mapv :id (get-in world [:trace 0 :top-candidates]))))
    (is (= :highest_strength (get-in world [:trace 0 :goal-selection])))))

(deftest run-cycle-activates-family-goals-before-competition
  (let [[world root-id] (world-with-root)
        [world failed-context-id] (cx/sprout world root-id)
        failed-goal-id :g-failed
        weak-leaf-id :g-failed-leaf
        leaf-objective {:fact/type :assumption
                        :fact/id :the_set_stays_closed}
        world (-> world
                  (assoc :auto-activate-family-goals? true)
                  (cx/assert-fact failed-context-id leaf-objective)
                  (cx/assert-fact failed-context-id {:fact/type :goal
                                                     :goal-id failed-goal-id
                                                     :top-level-goal failed-goal-id
                                                     :status :failed
                                                     :activation-context failed-context-id})
                  (cx/assert-fact failed-context-id {:fact/type :goal
                                                     :goal-id weak-leaf-id
                                                     :top-level-goal failed-goal-id
                                                     :status :runable
                                                     :activation-context failed-context-id
                                                     :strength 0.3
                                                     :objective-fact leaf-objective})
                  (cx/assert-fact failed-context-id {:fact/type :intends
                                                     :from-goal-id failed-goal-id
                                                     :to-goal-id weak-leaf-id
                                                     :top-level-goal failed-goal-id})
                  (cx/assert-fact failed-context-id {:fact/type :emotion
                                                     :emotion-id :e-shock
                                                     :strength 0.72
                                                     :valence :negative})
                  (cx/assert-fact failed-context-id {:fact/type :dependency
                                                     :from-id :e-shock
                                                     :to-id failed-goal-id}))
        [world selected-goal-id] (control/run-cycle world)
        activated-goals (mapv #(get-in world [:goals (:goal-id %)])
                              (:activation-events world))]
    (testing "reversal and roving are inferred from the failed-goal state"
      (is (= 2 (count (:activation-events world))))
      (is (= #{:reversal :roving}
             (set (map :goal-type activated-goals)))))
    (testing "activated goals carry trigger metadata and compete immediately"
      (is (every? #(= failed-context-id (:trigger-context-id %)) activated-goals))
      (is (every? #(= failed-goal-id (:trigger-failed-goal-id %)) activated-goals))
      (is (= selected-goal-id (get-in world [:trace 0 :selected-goal :id])))
      (is (= #{:reversal :roving}
             (set (map :goal-type (get-in world [:trace 0 :top-candidates]))))))
    (testing "trace records activation events for the cycle"
      (is (= 2 (count (get-in world [:trace 0 :activations]))))
      (is (= #{:reversal :roving}
             (set (map :goal-type (get-in world [:trace 0 :activations]))))))))

(deftest run-cycle-switches-to-daydreaming-when-performance-has-no-goals
  (let [[world _] (world-with-root)
        [world selected-goal] (control/run-cycle
                               (assoc world :mode :performance))]
    (is (= nil selected-goal))
    (is (= :daydreaming (:mode world)))
    (is (= 1 (:cycle world)))
    (is (= :no_candidates (get-in world [:trace 0 :goal-selection])))
    (is (= :daydreaming (get-in world [:trace 0 :selection :mode-switch])))))

(deftest run-cycle-wakes-waiting-real-goals
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :rehearsal
                          :planning-type :real
                          :status :waiting
                          :strength 0.5
                          :main-motiv :e-1})
        [world selected-goal] (control/run-cycle world)]
    (is (= nil selected-goal))
    (is (= :performance (:mode world)))
    (is (= :runable (get-in world [:goals goal-id :status])))
    (is (= :performance (get-in world [:trace 0 :selection :mode-switch])))))

(deftest prune-possibilities-orders-and-filters
  (let [[world root-id] (world-with-root)
        [world child-a] (cx/sprout world root-id)
        [world child-b] (cx/sprout world root-id)
        [world child-c] (cx/sprout world root-id)
        world (-> world
                  (assoc-in [:contexts child-a :ordering] 0.2)
                  (assoc-in [:contexts child-b :ordering] 0.9)
                  (assoc-in [:contexts child-c :ordering] 0.5)
                  (assoc-in [:contexts child-c :rules-run?] true))]
    (is (= [child-b child-a]
           (control/prune-possibilities world [child-a child-b child-c])))))

(deftest run-goal-step-selects-best-sprout
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :reversal
                          :planning-type :imaginary
                          :strength 0.8
                          :main-motiv :e-1})
        [world _] (control/run-cycle world)
        current-cx (goals/get-next-context world goal-id)
        [world child-a] (cx/sprout world current-cx)
        [world child-b] (cx/sprout world current-cx)
        world (-> world
                  (assoc-in [:contexts child-a :ordering] 0.4)
                  (assoc-in [:contexts child-b :ordering] 0.9))
        [world next-cx] (control/run-goal-step world goal-id)]
    (is (= child-b next-cx))
    (is (= child-b (goals/get-next-context world goal-id)))
    (is (= true (get-in world [:contexts current-cx :rules-run?])))
    (is (= current-cx (get-in world [:trace 0 :context-id])))
    (is (= [child-b child-a] (get-in world [:trace 0 :sprouted])))
    (is (= child-b (get-in world [:trace 0 :selection :next-context])))))

(deftest backtrack-top-level-goal-walks-to-surviving-sibling
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :reversal
                          :planning-type :imaginary
                          :strength 0.8
                          :main-motiv :e-1})
        [world _] (control/run-cycle world)
        wall (goals/get-backtrack-wall world goal-id)
        [world branch-a] (cx/sprout world wall)
        [world branch-b] (cx/sprout world wall)
        world (-> world
                  (goals/set-next-context goal-id branch-a)
                  (assoc-in [:contexts branch-a :rules-run?] true)
                  (assoc-in [:contexts branch-a :ordering] 0.1)
                  (assoc-in [:contexts branch-b :ordering] 0.9))
        [world next-cx] (control/backtrack-top-level-goal world goal-id branch-a)]
    (is (= branch-b next-cx))
    (is (= branch-b (goals/get-next-context world goal-id)))
    (is (= [{:goal-id goal-id
             :from branch-a
             :to branch-b
             :reason :exhausted
             :wall wall}]
           (get-in world [:trace 0 :backtrack-events])))))

(deftest run-goal-step-times-out-into-backtracking
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :reversal
                          :planning-type :imaginary
                          :strength 0.8
                          :main-motiv :e-1})
        [world _] (control/run-cycle world)
        wall (goals/get-backtrack-wall world goal-id)
        [world timeout-cx] (cx/sprout world wall)
        [world sibling-cx] (cx/sprout world wall)
        world (-> world
                  (goals/set-next-context goal-id timeout-cx)
                  (assoc-in [:contexts timeout-cx :timeout] 0)
                  (assoc-in [:contexts timeout-cx :rules-run?] true)
                  (assoc-in [:contexts sibling-cx :ordering] 0.8))
        [world next-cx] (control/run-goal-step world goal-id)]
    (is (= sibling-cx next-cx))
    (is (= sibling-cx (goals/get-next-context world goal-id)))
    (is (= :timeout (get-in world [:trace 0 :backtrack-events 0 :reason])))))

(deftest terminate-top-level-goal-records-and-stabilizes
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :reversal
                          :planning-type :imaginary
                          :strength 0.8
                          :main-motiv :e-1})
        [world _] (control/run-cycle world)
        result-fact {:fact/type :goal-outcome
                     :goal-id goal-id
                     :status :succeeded}
        old-reality (:reality-context world)
        world (control/terminate-top-level-goal world
                                                goal-id
                                                root-id
                                                {:status :succeeded
                                                 :result-fact result-fact})]
    (is (= :succeeded (get-in world [:goals goal-id :status])))
    (is (= root-id (get-in world [:goals goal-id :termination-cx])))
    (is (= result-fact
           (first (filter #(= (:goal-id %) goal-id)
                          (cx/visible-facts world old-reality)))))
    (is (not= old-reality (:reality-context world)))
    (is (= (:reality-context world) (:reality-lookahead world)))
    (is (= [{:goal-id goal-id
             :status :succeeded
             :resolution-cx root-id
             :planning-type :imaginary}]
           (:termination-events world)))
    (is (= (:termination-events world)
           (get-in world [:trace 0 :terminations])))))

(deftest all-possibilities-failed-terminates-at-backtrack-wall
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :reversal
                          :planning-type :imaginary
                          :strength 0.8
                          :main-motiv :e-1})
        [world _] (control/run-cycle world)
        wall (goals/get-backtrack-wall world goal-id)
        [world result] (control/backtrack-top-level-goal world goal-id wall)]
    (is (= nil result))
    (is (= :failed (get-in world [:goals goal-id :status])))
    (is (= wall (get-in world [:goals goal-id :termination-cx])))
    (is (= :failed (get-in world [:trace 0 :terminations 0 :status])))))
