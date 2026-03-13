(ns daydreamer.benchmarks.puppet-knows-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.puppet-knows :as puppet]))

(deftest benchmark-metadata-includes-shared-graph-shape
  (let [metadata (puppet/benchmark-metadata {:git_commit "abc123"})]
    (is (= "2026-03-12T12:00:08Z" (:started_at metadata)))
    (is (= "kernel/src/daydreamer" (:engine_path metadata)))
    (is (= 20 (:graph_nodes metadata)))
    (is (= 28 (:graph_edges metadata)))
    (is (= "abc123" (:git_commit metadata)))))

(deftest run-benchmark-emits-explicit-8-to-10-window
  (let [{:keys [world log benchmark-state]} (puppet/run-benchmark {:git_commit "abc123"})
        cycles (get log "cycles")]
    (testing "the benchmark is the intended three-cycle bridge"
      (is (= [8 9 10] (mapv #(get % "cycle") cycles)))
      (is (= 3 (count (:trace world)))))
    (testing "cycle 8 includes a real reversal branch"
      (is (some? (:reversal-goal-id benchmark-state)))
      (is (= 1 (count (get-in world [:trace 0 :sprouted]))))
      (is (= "reversal" (get-in cycles [0 "mutations" 0 "family"])))
      (is (= (name (:old-context-id benchmark-state))
             (get-in cycles [0 "selection" "reversal_source_context"])))
      (is (= (name (:old-top-level-goal-id benchmark-state))
             (get-in cycles [0 "selection" "reversal_leaf_goal"])))
      (is (= "emotion_then_depth"
             (get-in cycles [0 "selection" "reversal_leaf_policy"])))
      (is (= (name (:preferred-cause-id benchmark-state))
             (get-in cycles [0 "selection" "reversal_counterfactual_source"])))
      (is (= "stored_priority"
             (get-in cycles [0 "selection" "reversal_counterfactual_policy"])))
      (is (= ["performance_is_admitted" "s4_the_ring"]
             (get-in cycles [0 "selection" "reversal_counterfactual_fact_ids"])))
      (is (= ["performance_is_admitted" "s4_the_ring"]
             (mapv #(get % "id")
                   (get-in cycles [0 "mutations" 0 "input-facts"]))))
      (let [reversal-branch-id (first (get-in world [:trace 0 :sprouted]))]
        (is (= true (get-in world [:contexts reversal-branch-id :alternative-past?])))
        (is (= true (get-in world [:contexts reversal-branch-id :pseudo-sprout?])))))
    (testing "the key transition is revenge into ring rehearsal"
      (is (= "repercussions" (get-in cycles [0 "selected_goal" "goal_type"])))
      (is (= "revenge" (get-in cycles [1 "selected_goal" "goal_type"])))
      (is (= "s1_seeing_through"
             (get-in cycles [1 "selected_goal" "situation_id"])))
      (is (= "n09_tear_the_set" (get-in cycles [1 "chosen_node_id"])))
      (is (= "rehearsal" (get-in cycles [2 "selected_goal" "goal_type"])))
      (is (= "s4_the_ring"
             (get-in cycles [2 "selected_goal" "situation_id"])))
      (is (= "n10_honest_ring" (get-in cycles [2 "chosen_node_id"])))
      (is (= "association" (get-in cycles [2 "selection" "edge_kind"]))))
    (testing "director feedback on cycle 9 wakes the ring"
      (is (= 0.12
             (get-in cycles [1 "feedback_applied" "situation_boosts" "s4_the_ring"]))))
    (testing "trace retains termination on the bridge cycles"
      (is (= :succeeded (get-in world [:trace 0 :terminations 0 :status])))
      (is (= :succeeded (get-in world [:trace 1 :terminations 0 :status]))))))
