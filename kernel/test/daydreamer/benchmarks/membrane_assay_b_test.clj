(ns daydreamer.benchmarks.membrane-assay-b-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.membrane-assay-b :as assay-b]
            [daydreamer.context :as cx]))

(deftest build-world-suppresses-ordinary-rationalization-in-situation-a
  (let [world (assay-b/build-world)
        a-context-id (get-in world [:membrane-assay-b :bridge-context-id])
        facts (set (cx/visible-facts world a-context-id))]
    (is (not (contains? facts
                        {:fact/type :dependency
                         :from-id :e_rehearsal_dread
                         :to-id :g_performance_failure})))))

(deftest run-soak-promotes-a-bridged-rationalization-episode-live
  (let [{:keys [world cycle-summaries summaries]} (assay-b/run-soak)
        dynamic-reversal (nth cycle-summaries 1)
        bridged-rationalization (nth cycle-summaries 2)
        first-success (nth cycle-summaries 3)
        second-success (nth cycle-summaries 4)
        bridged-episode-id (:family-plan-episode-id bridged-rationalization)
        bridged-episode (get-in world [:episodes bridged-episode-id])]
    (testing "the live path seeds dynamic reversal before the bridged rationalization episode"
      (is (= :dynamic (:winner-origin dynamic-reversal)))
      (is (= :reversal_aftershock_rationalization_frame
             (:activation-policy bridged-rationalization)))
      (is (some #{:goal-family/reversal-aftershock-to-rationalization}
                (get-in bridged-rationalization [:rule-provenance :rule-path]))))
    (testing "the first cross-family success records evidence but does not promote yet"
      (is (= bridged-episode-id
             (:selected-episode-id first-success)))
      (is (= :succeeded (:outcome first-success)))
      (is (nil? (:admission-transition first-success)))
      (is (= 1 (:promotion-evidence-count first-success))))
    (testing "the second cross-family success promotes the episode and opens the frontier rule"
      (is (= bridged-episode-id
             (:selected-episode-id second-success)))
      (is (= {:episode-id bridged-episode-id
              :from-status :provisional
              :to-status :durable
              :reason :cross-family-use-success}
             (:admission-transition second-success)))
      (is (= [{:rule-id :goal-family/reversal-aftershock-to-rationalization
               :from-status :frontier
               :to-status :accessible
               :cycle 5
               :reason :durable-episode-opened-rule
               :episode-id bridged-episode-id
               :branch-context-id (get-in world [:membrane-assay-b :probe-context-id])}]
             (:access-transitions second-success))))
    (testing "the final stored episode shows the full live positive path"
      (is (= :durable (:admission-status bridged-episode)))
      (is (= 2 (count (:use-history bridged-episode))))
      (is (= [:succeeded :succeeded]
             (mapv :outcome (:use-history bridged-episode))))
      (is (= 2 (count (:promotion-evidence bridged-episode))))
      (is (= :accessible
             (get-in world
                     [:rule-access
                      :goal-family/reversal-aftershock-to-rationalization
                      :status]))))
    (testing "the benchmark still returns readable summaries"
      (is (= 5 (count summaries)))
      (is (re-find #"bridged rationalization"
                   (nth summaries 2)))
      (is (re-find #"cross-family-success-2"
                   (nth summaries 4))))))

(deftest run-soak-logs-cross-family-candidate-race-and-exclusion-reasons
  (let [{:keys [cycle-summaries]} (assay-b/run-soak)
        first-success (nth cycle-summaries 3)
        candidate-race (:candidate-race first-success)
        selected (first (filter :selected? candidate-race))
        excluded (first (remove :selected? candidate-race))]
    (testing "the selected candidate is the bridged rationalization episode"
      (is (= :rationalization (:source-family selected)))
      (is (some #{:goal-family/reversal-aftershock-to-rationalization}
                (:rule-path selected)))
      (is (false? (:same-cycle? selected))))
    (testing "non-family-plan or wrong-family hits are logged with explicit exclusions"
      (is (some? excluded))
      (is (seq (:exclusion-reasons excluded))))))
