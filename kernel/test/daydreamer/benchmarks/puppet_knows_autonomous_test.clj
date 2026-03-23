(ns daydreamer.benchmarks.puppet-knows-autonomous-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.director :as director]
            [daydreamer.benchmarks.puppet-knows-autonomous :as autonomous]))

(defn- longest-true-run
  [xs]
  (->> xs
       (partition-by identity)
       (keep (fn [run]
               (when (true? (first run))
                 (count run))))
       (reduce max 0)))

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

(deftest run-benchmark-can-apply-director-feedback-live-loop
  (let [baseline (autonomous/run-benchmark {:cycles 6})
        with-director
        (autonomous/run-benchmark
         {:cycles 6
          :director-fn (fn [director-input]
                         (director/mock-director nil director-input))})
        baseline-cycles (get-in baseline [:log "cycles"])
        director-cycles (get-in with-director [:log "cycles"])
        baseline-ring-activation (or (get-in baseline-cycles
                                             [0 "situations" "s4_the_ring" "activation"])
                                     0.0)
        director-ring-activation (or (get-in director-cycles
                                             [1 "situations" "s4_the_ring" "activation"])
                                     0.0)]
    (testing "the trace records live Director feedback on each cycle"
      (is (= 6 (count director-cycles)))
      (is (every? map?
                  (map #(get % "feedback_applied")
                       director-cycles)))
      (is (= ["honesty" "performance" "mercy"]
             (get-in director-cycles
                     [0 "feedback_applied" "director_concepts"]))))
    (testing "Director feedback changes the forward state, not just the trace echo"
      (is (> director-ring-activation baseline-ring-activation))
      (is (not= (map #(get % "chosen_node_id")
                     baseline-cycles)
                (map #(get % "chosen_node_id")
                     director-cycles))))))

(deftest run-benchmark-preserves-no-hook-baseline
  (let [{:keys [world summaries]} (autonomous/run-benchmark {:cycles 12})]
    (is (= ["n02_corridor_repeat"
            "n02_corridor_repeat"
            "n01_notice_seams"
            "n02_corridor_repeat"
            "n04_spotlight_choices"
            "n01_notice_seams"
            "n13_countdown_clock"
            "n01_notice_seams"
            "n07_backstage_headlights"
            "n06_overhead_set_edge"
            "n07_backstage_headlights"
            "n06_overhead_set_edge"]
           (mapv :chosen-node-id (:trace world))))
    (is (= [:backstage
            :darkness
            :non_directed_light
            :silence
            :stage_light
            :awareness]
           (:recent-indices world)))
    (is (= [:ep-3]
           (:recent-episodes world)))
    (is (= 12
           (count summaries)))))

(deftest run-benchmark-preserves-mock-thought-and-director-baseline
  (let [{:keys [world summaries]}
        (autonomous/run-benchmark {:cycles 12
                                   :thought-mode :mock
                                   :director-mode :mock})]
    (is (= ["n02_corridor_repeat"
            "n02_corridor_repeat"
            "n01_notice_seams"
            "n01_notice_seams"
            "n13_countdown_clock"
            "n01_notice_seams"
            "n03_mission_walk"
            "n02_corridor_repeat"
            "n07_backstage_headlights"
            "n15_subway_self_scrutiny"
            "n05_peel_the_wall"
            "n09_tear_the_set"]
           (mapv :chosen-node-id (:trace world))))
    (is (= [:combat
            :honesty
            :crowd
            :anger
            :performance
            :apparatus]
           (:recent-indices world)))
    (is (= [:ring
            :apparatus
            :horizon
            :ritual
            :light
            :hinge]
           (:director-recent-concepts world)))
    (is (= [:ep-3]
           (:recent-episodes world)))
    (is (= 12
           (count summaries)))))

(deftest fatigue-adjustment-saturates-repeated-rehearsal-on-the-same-situation
  (let [candidate {:situation-id :s4_the_ring
                   :goal-type :rehearsal
                   :strength 0.7
                   :reasons [:anticipation :waiting_goal]}
        situations {:s4_the_ring {:id :s4_the_ring
                                  :visits 2
                                  :indices [:ritual :honesty :performance]}}
        world {:autonomous {:current-goal-key [:s4_the_ring :rehearsal]}}
        adjusted (#'autonomous/fatigue-adjustment candidate situations world)]
    (is (< (:strength adjusted)
           (:strength candidate)))
    (is (some #{:rehearsal_saturation}
              (:reasons adjusted)))))

(deftest fatigue-adjustment-saturates-repeated-repercussions-on-the-same-situation
  (let [candidate {:situation-id :s3_the_edge
                   :goal-type :repercussions
                   :strength 0.65
                   :reasons [:external_threat :what_follows]}
        situations {:s3_the_edge {:id :s3_the_edge
                                  :visits 3
                                  :indices [:edge :void :backstage]}}
        world {:autonomous {:current-goal-key [:s3_the_edge :repercussions]}}
        adjusted (#'autonomous/fatigue-adjustment candidate situations world)]
    (is (< (:strength adjusted)
           (:strength candidate)))
    (is (some #{:repercussions_saturation}
              (:reasons adjusted)))))

(deftest run-benchmark-50-cycle-tail-stays-mixed-after-retune
  (let [{:keys [log]} (autonomous/run-benchmark {:cycles 50})
        cycles (get log "cycles")
        post-cycle-13 (drop 13 cycles)
        selected-goal-types (map #(get-in % ["selected_goal" "goal_type"])
                                 post-cycle-13)
        late-s3-repercussions (map #(and (= "repercussions"
                                            (get-in % ["selected_goal" "goal_type"]))
                                         (= "s3_the_edge"
                                            (get-in % ["selected_goal" "situation_id"])))
                                   (drop 33 cycles))]
    (testing "the long soak still visits multiple families after cycle 13"
      (is (every? (set selected-goal-types)
                  ["reversal" "roving" "rehearsal" "repercussions"])))
    (testing "same-situation repercussions no longer lock for the rest of the run"
      (is (<= (longest-true-run late-s3-repercussions) 1)))))

(deftest run-benchmark-can-apply-external-family-evaluator
  (let [archive-evaluator (fn [_family-plan _default-evaluation]
                            {:realism :plausible
                             :desirability :negative
                             :retention-class :cold-provenance
                             :keep-decision :archive-cold
                             :evaluation-reasons [:mock_archive_review]
                             :evaluation-source :mock-llm})
        {:keys [world]} (autonomous/run-benchmark {:cycles 2
                                                   :family-evaluator-fn archive-evaluator})
        family-plan-episodes (->> (:episodes world)
                                  vals
                                  (filter #(= :family-plan
                                              (get-in % [:provenance :source])))
                                  vec)]
    (is (seq family-plan-episodes))
    (is (every? #(= :mock-llm
                    (get-in % [:evaluation :evaluation-source]))
                family-plan-episodes))
    (is (every? #(= :archive-cold (:keep-decision %))
                family-plan-episodes))
    (is (every? #(= :cold-provenance (:retention-class %))
                family-plan-episodes))))
