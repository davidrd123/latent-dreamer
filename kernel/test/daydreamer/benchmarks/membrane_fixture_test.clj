(ns daydreamer.benchmarks.membrane-fixture-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.membrane-fixture :as fixture]
            [daydreamer.goal-families :as families]))

(deftest build-world-seeds-deliberately-asymmetric-authored-coverage
  (let [world (fixture/build-world)
        a-context-id (get-in world [:membrane-fixture :situation-contexts :a])
        b-context-id (get-in world [:membrane-fixture :situation-contexts :b])
        a-rationalization-candidates
        (families/rationalization-frame-candidates world
                                                   {:trigger-context-id a-context-id
                                                    :failed-goal-id :g_performance_failure})
        b-rationalization-candidates
        (families/rationalization-frame-candidates world
                                                   {:trigger-context-id b-context-id
                                                    :failed-goal-id :g_performance_failure})
        a-reversal-candidates
        (families/reverse-undo-cause-candidates
         world
         {:old-context-id a-context-id
          :old-top-level-goal-id :g_performance_failure
          :failed-goal-id :g_performance_failure})
        b-reversal-candidates
        (families/reverse-undo-cause-candidates
         world
         {:old-context-id b-context-id
          :old-top-level-goal-id :g_performance_failure
          :failed-goal-id :g_performance_failure})]
    (testing "situation A starts with authored rationalization but no authored reversal cause"
      (is (= [:authored]
             (mapv :candidate-origin a-rationalization-candidates)))
      (is (= []
             a-reversal-candidates)))
    (testing "situation B starts with authored reversal cause but no authored rationalization"
      (is (= []
             b-rationalization-candidates))
      (is (= [:authored]
             (mapv :candidate-origin b-reversal-candidates))))))

(deftest run-soak-drives-the-first-live-dynamic-source-races
  (let [{:keys [world cycle-summaries summaries]} (fixture/run-soak 4)
        authored-rationalization (nth cycle-summaries 0)
        authored-reversal (nth cycle-summaries 1)
        dynamic-rationalization (nth cycle-summaries 2)
        dynamic-reversal (nth cycle-summaries 3)
        authored-rationalization-episode
        (get-in world [:episodes (:family-plan-episode-id authored-rationalization)])
        authored-reversal-episode
        (get-in world [:episodes (:family-plan-episode-id authored-reversal)])]
    (testing "the first two cycles seed authored material"
      (is (= :authored (:winner-origin authored-rationalization)))
      (is (= :authored (:winner-origin authored-reversal)))
      (is (= :provisional (:admission-status authored-rationalization-episode)))
      (is (= :provisional (:admission-status authored-reversal-episode))))
    (testing "later cycles select dynamic stored sources"
      (is (= :dynamic (:winner-origin dynamic-rationalization)))
      (is (= (:family-plan-episode-id authored-rationalization)
             (:source-episode-id dynamic-rationalization)))
      (is (= :stored_rationalization_episode
             (get-in dynamic-rationalization [:candidate-race 0 :selection-policy])))
      (is (= :provisional
             (get-in dynamic-rationalization [:candidate-race 0 :admission-status])))
      (is (= :dynamic (:winner-origin dynamic-reversal)))
      (is (= (:family-plan-episode-id authored-reversal)
             (:source-episode-id dynamic-reversal)))
      (is (= :stored_reversal_episode
             (get-in dynamic-reversal [:candidate-race 0 :selection-policy])))
      (is (= :provisional
             (get-in dynamic-reversal [:candidate-race 0 :admission-status]))))
    (testing "dynamic source selection records live pending same-family uses"
      (is (= 1 (count (:use-history authored-rationalization-episode))))
      (is (= 1 (count (:use-history authored-reversal-episode))))
      (is (= :pending
             (get-in authored-rationalization-episode [:use-history 0 :status])))
      (is (= :pending
             (get-in authored-reversal-episode [:use-history 0 :status])))
      (is (= :rationalization
             (get-in authored-rationalization-episode [:use-history 0 :target-family])))
      (is (= :reversal
             (get-in authored-reversal-episode [:use-history 0 :target-family]))))
    (testing "the benchmark returns readable summaries"
      (is (= 4 (count summaries)))
      (is (re-find #"Cycle 3 \| rationalization @ situation_b \| winner: dynamic \| source: ep-"
                   (nth summaries 2)))
      (is (re-find #"Cycle 4 \| reversal @ situation_a \| winner: dynamic \| source: ep-"
                   (nth summaries 3))))))

(deftest run-soak-eight-cycles-can-surface-live-loop-pressure
  (let [{:keys [world cycle-summaries]} (fixture/run-soak 8)
        first-authored-reversal-id (:family-plan-episode-id (nth cycle-summaries 1))
        flagged-episodes
        (->> (:episodes world)
             vals
             (filter #(contains? (set (:anti-residue-flags %))
                                 :same-family-loop))
             (map :id)
             vec)]
    (is (seq flagged-episodes))
    (is (some #{first-authored-reversal-id} flagged-episodes))))
