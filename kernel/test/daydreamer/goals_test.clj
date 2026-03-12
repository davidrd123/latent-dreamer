(ns daydreamer.goals-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.context :as cx]
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
      :id-counter 1}
     root-id]))

(deftest create-goal-test
  (is (= {:id :g-1
          :goal-type :reversal
          :planning-type :real
          :status :runable
          :strength 0.8
          :main-motiv :e-1
          :activation-cx nil
          :termination-cx nil
          :next-cx nil
          :backtrack-wall nil
          :top-level-goal :g-1}
         (goals/create-goal {:goal-type :reversal
                             :strength 0.8
                             :main-motiv :e-1}))))

(deftest activate-top-level-goal-real
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :rehearsal
                          :strength 0.7
                          :main-motiv :e-1})
        goal (get-in world [:goals goal-id])]
    (is (= :real (:planning-type goal)))
    (is (= root-id (:activation-cx goal)))
    (is (= nil (:backtrack-wall goal)))
    (is (= nil (:next-cx goal)))
    (is (= goal-id (:top-level-goal goal)))))

(deftest activate-top-level-goal-imaginary
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :reversal
                          :planning-type :imaginary
                          :strength 0.9
                          :main-motiv :e-2})
        goal (get-in world [:goals goal-id])
        planning-cx (:activation-cx goal)]
    (testing "imaginary activation sprouts a planning context"
      (is (= :imaginary (:planning-type goal)))
      (is (= planning-cx (:backtrack-wall goal)))
      (is (= planning-cx (:next-cx goal)))
      (is (= planning-cx (:activation-cx goal)))
      (is (= root-id (:parent-id (get-in world [:contexts planning-cx])))))))

(deftest status-and-context-pointer-updates
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :roving
                          :planning-type :imaginary
                          :strength 0.6
                          :main-motiv :e-3})
        [world next-cx] (cx/sprout world (:activation-cx (get-in world [:goals goal-id])))
        world (-> world
                  (goals/change-status goal-id :halted)
                  (goals/set-next-context goal-id next-cx)
                  (goals/record-termination goal-id root-id))]
    (is (= :halted (get-in world [:goals goal-id :status])))
    (is (= next-cx (goals/get-next-context world goal-id)))
    (is (= (:activation-cx (get-in world [:goals goal-id]))
           (goals/get-backtrack-wall world goal-id)))
    (is (= root-id (get-in world [:goals goal-id :termination-cx])))))

(deftest motivating-emotion-test
  (let [[world root-id] (world-with-root)
        [world _] (goals/activate-top-level-goal
                   world
                   root-id
                   {:goal-type :reversal
                    :strength 0.8
                    :main-motiv :e-1})]
    (is (goals/motivating-emotion? world :e-1))
    (is (not (goals/motivating-emotion? world :e-9)))))

(deftest most-highly-motivated-goals-test
  (let [[world root-id] (world-with-root)
        [world g1] (goals/activate-top-level-goal
                    world
                    root-id
                    {:goal-type :reversal
                     :planning-type :imaginary
                     :strength 0.9
                     :main-motiv :e-1})
        [world g2] (goals/activate-top-level-goal
                    world
                    root-id
                    {:goal-type :rehearsal
                     :strength 0.9
                     :main-motiv :e-2})
        [world g3] (goals/activate-top-level-goal
                    world
                    root-id
                    {:goal-type :roving
                     :strength 0.4
                     :main-motiv :e-3})]
    (testing "daydreaming mode keeps both strongest tied goals"
      (is (= [g1 g2] (goals/most-highly-motivated-goals world))))
    (testing "performance mode filters out imaginary goals"
      (is (= [g2]
             (goals/most-highly-motivated-goals
              (assoc world :mode :performance)))))
    (testing "halted goals are excluded from competition"
      (is (= [g1]
             (goals/most-highly-motivated-goals
              (goals/change-status world g2 :halted)))))
    (testing "lower-strength goals never win the competition"
      (is (not (some #{g3} (goals/most-highly-motivated-goals world)))))))
