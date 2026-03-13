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

(deftest run-benchmark-emits-rationalization-emotion-payloads
  (let [{:keys [log]} (autonomous/run-benchmark {:cycles 2})
        cycles (get log "cycles")
        first-cycle (first cycles)
        second-cycle (second cycles)]
    (testing "the opening autonomous cycles execute rationalization rather than flat wins"
      (is (= "rationalization"
             (get-in first-cycle ["selected_goal" "goal_type"])))
      (is (= "rationalization"
             (get-in second-cycle ["selected_goal" "goal_type"])))
      (is (= "branch_visible_facts"
             (get-in first-cycle ["selection" "adapter_policy"])))
      (is (= "s1_seeing_through"
             (get-in first-cycle ["selection" "adapter_selected_situation"]))))
    (testing "rationalization now exports real emotional change at the cycle level"
      (is (= 2 (count (get first-cycle "emotion_shifts"))))
      (is (= 2 (count (get first-cycle "emotional_state"))))
      (is (= "s1_seeing_through-autonomous-dread-1"
             (get-in first-cycle ["emotion_shifts" 0 "emotion_id"])))
      (is (= "s1_seeing_through-autonomous-reframe-1-hope"
             (get-in first-cycle ["emotion_shifts" 1 "emotion_id"])))
      (is (< (get-in first-cycle ["emotion_shifts" 0 "to_strength"])
             (get-in first-cycle ["emotion_shifts" 0 "from_strength"])))
      (is (= "s1_seeing_through"
             (get-in first-cycle ["emotional_state" 1 "situation_id"]))))
    (testing "the rationalization branch now carries canonical trace payloads"
      (is (= "rationalization"
             (get-in first-cycle ["branch_events" 0 "family"])))
      (is (= ["seam_is_honesty"]
             (get-in first-cycle ["branch_events" 0 "fact_ids"])))
      (is (= ["honesty" "clarity"]
             (get-in first-cycle ["selection" "adapter_active_indices"]))))))
