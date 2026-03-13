(ns daydreamer.benchmarks.arctic-expedition-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.arctic-expedition :as arctic]))

(deftest benchmark-metadata-includes-brief-path
  (let [metadata (arctic/benchmark-metadata {:git_commit "abc123"})]
    (is (= "2026-03-12T13:00:06Z" (:started_at metadata)))
    (is (= "kernel/src/daydreamer" (:engine_path metadata)))
    (is (= arctic/benchmark-brief-path (:world_path metadata)))
    (is (= "arctic_expedition" (:palette_path metadata)))
    (is (= 8 (:graph_nodes metadata)))
    (is (= 10 (:graph_edges metadata)))
    (is (= "abc123" (:git_commit metadata)))))

(deftest run-benchmark-emits-scripted-repercussions-to-roving-window
  (let [{:keys [world log benchmark-state]} (arctic/run-benchmark {:git_commit "abc123"})
        cycles (get log "cycles")]
    (testing "the benchmark is the intended two-cycle bridge"
      (is (= [6 7] (mapv #(get % "cycle") cycles)))
      (is (= 2 (count (:trace world)))))
    (testing "cycle 6 holds the ship in pressure"
      (is (= "repercussions" (get-in cycles [0 "selected_goal" "goal_type"])))
      (is (= "n06_hull_groan" (get-in cycles [0 "chosen_node_id"])))
      (is (= :failed (get-in world [:trace 0 :terminations 0 :status]))))
    (testing "cycle 7 uses the real roving branch"
      (is (= (name (:pleasant-episode-id benchmark-state))
             (get-in cycles [1 "selection" "roving_seed_episode"])))
      (is (= [(name (:linked-episode-id benchmark-state))]
             (get-in cycles [1 "selection" "roving_reminded_episodes"])))
      (is (= "roving" (get-in cycles [1 "mutations" 0 "family"])))
      (is (= "roving" (get-in cycles [1 "branch_events" 0 "family"])))
      (is (= [] (get-in cycles [1 "branch_events" 0 "fact_ids"])))
      (is (= [(name (:pleasant-episode-id benchmark-state))
              (name (:linked-episode-id benchmark-state))]
             (get-in cycles [1 "branch_events" 0 "episode_ids"])))
      (is (= #{"aurora" "light" "horizon" "wonder"}
             (set (get-in cycles [1 "branch_events" 0 "active_indices"]))))
      (is (= "roving" (get-in cycles [1 "selected_goal" "goal_type"])))
      (is (= "s4_the_horizon"
             (get-in cycles [1 "selected_goal" "situation_id"])))
      (is (= "n07_aurora_watch" (get-in cycles [1 "chosen_node_id"])))
      (is (= ["aurora" "light" "horizon"]
             (get-in cycles [1 "retrieved" 0 "overlap"]))))))

(deftest run-semi-unscripted-benchmark-auto-activates-roving
  (let [{:keys [world log benchmark-state]}
        (arctic/run-semi-unscripted-benchmark {:git_commit "abc123"})
        cycles (get log "cycles")]
    (testing "the semi-unscripted run stays in the same benchmark window"
      (is (= [6 7] (mapv #(get % "cycle") cycles)))
      (is (= 2 (count (:trace world)))))
    (testing "roving activation is autonomous on the pressure cycle"
      (is (= ["roving"]
             (mapv #(get % "goal_type")
                   (get-in cycles [0 "activations"]))))
      (is (= (name (:trigger-context-id benchmark-state))
             (get-in cycles [0 "activations" 0 "trigger_context_id"])))
      (is (= (name (:failed-goal-id benchmark-state))
             (get-in cycles [0 "activations" 0 "failed_goal_id"])))
      (is (= (name (:emotion-id benchmark-state))
             (get-in cycles [0 "activations" 0 "emotion_id"]))))
    (testing "cycle 7 reaches the aurora through automatic roving + adapter"
      (is (= "roving" (get-in cycles [1 "selected_goal" "goal_type"])))
      (is (= "roving_reminding_indices"
             (get-in cycles [1 "selection" "adapter_policy"])))
      (is (= "s4_the_horizon"
             (get-in cycles [1 "selection" "adapter_selected_situation"])))
      (is (= "s4_the_horizon"
             (get-in cycles [1 "selected_goal" "situation_id"])))
      (is (= "n07_aurora_watch"
             (get-in cycles [1 "chosen_node_id"])))
      (is (= "reminding_bridge"
             (get-in cycles [1 "selection" "edge_kind"])))
      (is (= ["aurora" "light" "horizon" "wonder"]
             (get-in cycles [1 "active_indices"])))
      (is (= "roving" (get-in cycles [1 "branch_events" 0 "family"])))
      (is (= [(name (:pleasant-episode-id benchmark-state))
              (name (:linked-episode-id benchmark-state))]
             (get-in cycles [1 "branch_events" 0 "episode_ids"])))
      (is (= #{"aurora" "light" "horizon" "wonder"}
             (set (get-in cycles [1 "branch_events" 0 "active_indices"]))))
      (is (= [(name (:linked-episode-id benchmark-state))]
             (get-in cycles [1 "selection" "roving_reminded_episodes"]))))))
