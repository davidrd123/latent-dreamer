(ns daydreamer.benchmarks.stalker-zone-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.stalker-zone :as zone]))

(deftest benchmark-metadata-includes-brief-path
  (let [metadata (zone/benchmark-metadata {:git_commit "abc123"})]
    (is (= "2026-03-12T14:00:06Z" (:started_at metadata)))
    (is (= "kernel/src/daydreamer" (:engine_path metadata)))
    (is (= zone/benchmark-brief-path (:world_path metadata)))
    (is (= "stalker_zone" (:palette_path metadata)))
    (is (= 8 (:graph_nodes metadata)))
    (is (= 9 (:graph_edges metadata)))
    (is (= "abc123" (:git_commit metadata)))))

(deftest run-benchmark-emits-scripted-repercussions-to-rationalization-window
  (let [{:keys [world log benchmark-state]} (zone/run-benchmark {:git_commit "abc123"})
        cycles (get log "cycles")]
    (testing "the benchmark is the intended two-cycle bridge"
      (is (= [6 7] (mapv #(get % "cycle") cycles)))
      (is (= 2 (count (:trace world)))))
    (testing "cycle 6 holds dread at the Room"
      (is (= "repercussions" (get-in cycles [0 "selected_goal" "goal_type"])))
      (is (= "n06_phone_without_voice" (get-in cycles [0 "chosen_node_id"])))
      (is (= :failed (get-in world [:trace 0 :terminations 0 :status]))))
    (testing "cycle 7 uses the real rationalization branch"
      (is (= (name (:preferred-frame-id benchmark-state))
             (get-in cycles [1 "selection" "rationalization_frame_id"])))
      (is (= "rationalization" (get-in cycles [1 "mutations" 0 "family"])))
      (is (= "rationalization" (get-in cycles [1 "selected_goal" "goal_type"])))
      (is (= "s5_the_guide"
             (get-in cycles [1 "selected_goal" "situation_id"])))
      (is (= "n07_zone_is_mercy" (get-in cycles [1 "chosen_node_id"])))
      (is (= ["faith" "guide" "sincerity"]
             (get-in cycles [1 "retrieved" 0 "overlap"])))
      (is (= ["s5_the_guide" "zone_is_mercy" "delay_is_faith"]
             (get-in cycles [1 "selection" "rationalization_reframe_fact_ids"]))))))

(deftest run-semi-unscripted-benchmark-auto-activates-rationalization
  (let [{:keys [world log benchmark-state]}
        (zone/run-semi-unscripted-benchmark {:git_commit "abc123"})
        cycles (get log "cycles")]
    (testing "the semi-unscripted run stays in the same benchmark window"
      (is (= [6 7] (mapv #(get % "cycle") cycles)))
      (is (= 2 (count (:trace world)))))
    (testing "rationalization activation is autonomous on the dread cycle"
      (is (some #{"rationalization"}
                (map #(get % "goal_type")
                     (get-in cycles [0 "activations"]))))
      (is (= (name (:trigger-context-id benchmark-state))
             (get-in cycles [0 "activations" 0 "trigger_context_id"])))
      (is (= (name (:failed-goal-id benchmark-state))
             (get-in cycles [0 "activations" 0 "failed_goal_id"])))
      (is (= (name (:emotion-id benchmark-state))
             (get-in cycles [0 "activations" 0 "emotion_id"]))))
    (testing "cycle 7 reaches the guide through automatic rationalization + adapter"
      (is (= "rationalization" (get-in cycles [1 "selected_goal" "goal_type"])))
      (is (= "branch_visible_facts"
             (get-in cycles [1 "selection" "adapter_policy"])))
      (is (= "s5_the_guide"
             (get-in cycles [1 "selection" "adapter_selected_situation"])))
      (is (= "s5_the_guide"
             (get-in cycles [1 "selected_goal" "situation_id"])))
      (is (= "n07_zone_is_mercy"
             (get-in cycles [1 "chosen_node_id"])))
      (is (= "reinterpretation_bridge"
             (get-in cycles [1 "selection" "edge_kind"])))
      (is (= ["faith" "guide" "sincerity" "trust"]
             (get-in cycles [1 "active_indices"])))
      (is (= (name (:preferred-frame-id benchmark-state))
             (get-in cycles [1 "selection" "rationalization_frame_id"]))))))
