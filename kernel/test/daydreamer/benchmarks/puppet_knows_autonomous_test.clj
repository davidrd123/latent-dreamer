(ns daydreamer.benchmarks.puppet-knows-autonomous-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.puppet-knows-autonomous :as autonomous]))

(deftest run-benchmark-emits-readable-autonomous-trace
  (let [{:keys [world log summaries]} (autonomous/run-benchmark {:cycles 6})
        cycles (get log "cycles")
        selected-situations (keep #(get-in % ["selected_goal" "situation_id"]) cycles)
        selected-goal-types (keep #(get-in % ["selected_goal" "goal_type"]) cycles)]
    (testing "the run emits the requested cycle window"
      (is (= [1 2 3 4 5 6]
             (mapv #(get % "cycle") cycles)))
      (is (= 6 (count (:trace world))))
      (is (= 6 (count summaries))))
    (testing "the summaries are readable in the terminal"
      (is (every? string? summaries))
      (is (re-find #"Cycle 1 \|"
                   (first summaries)))
      (is (re-find #"retrieved:"
                   (first summaries))))
    (testing "the autonomous trace includes real branch work"
      (is (some #{"reversal"} selected-goal-types))
      (is (some seq
                (map #(get % "sprouted_contexts") cycles))))
    (testing "the run is not stuck in one situation"
      (is (> (count (set selected-situations))
             1)))))
