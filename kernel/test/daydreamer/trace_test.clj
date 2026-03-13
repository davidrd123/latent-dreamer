(ns daydreamer.trace-test
  (:require [clojure.test :refer [deftest is]]
            [daydreamer.context :as cx]
            [daydreamer.goals :as goals]
            [daydreamer.trace :as trace]))

(defn world-with-root
  []
  (let [root (cx/create-context)
        root-id (:id root)]
    [{:contexts {root-id root}
      :goals {}
      :episodes {}
      :emotions {}
      :mode :daydreaming
      :cycle 1
      :trace []
      :termination-events []
      :reality-context root-id
      :reality-lookahead root-id
      :id-counter 1}
     root-id]))

(deftest goal-summary-preserves-trace-fields
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :reversal
                          :planning-type :imaginary
                          :strength 0.8
                          :main-motiv :e-1
                          :situation-id :s1_seeing_through})]
    (is (= {:id goal-id
            :goal-type :reversal
            :strength 0.8
            :planning-type :imaginary
            :situation-id :s1_seeing_through
            :main-motiv :e-1}
           (trace/goal-summary world goal-id)))))

(deftest cycle-snapshot-builds-guardrails-shape
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :reversal
                          :planning-type :imaginary
                          :strength 0.8
                          :main-motiv :e-1
                          :situation-id :s1_seeing_through})
        planning-cx (get-in world [:goals goal-id :next-cx])
        [world child-cx] (cx/sprout world planning-cx)
        world (assoc world :termination-events [{:goal-id goal-id
                                                 :status :failed
                                                 :resolution-cx planning-cx}])
        snapshot (trace/cycle-snapshot
                  world
                  {:goal-id goal-id
                   :timestamp "2026-03-12T12:00:00Z"
                   :active-indices [:s1_seeing_through :reversal]
                   :retrievals [{:episode-id :ep-3
                                 :marks 2
                                 :threshold 2}]
                   :chosen-node-id "n09_tear_the_set"
                   :situations {:s1_seeing_through {:activation 0.9
                                                    :ripeness 0.8}}
                   :backtrack-events [{:from planning-cx
                                       :to child-cx}]})]
    (is (= 1 (:cycle-num snapshot)))
    (is (= :daydreaming (:mode snapshot)))
    (is (= planning-cx (:context-id snapshot)))
    (is (= 1 (:context-depth snapshot)))
    (is (= [child-cx] (:sprouted snapshot)))
    (is (= [{:episode-id :ep-3
             :marks 2
             :threshold 2}]
           (:retrievals snapshot)))
    (is (= [{:goal-id goal-id
             :status :failed
             :resolution-cx planning-cx}]
           (:terminations snapshot)))))

(deftest append-cycle-adds-to-world-trace
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :roving
                          :strength 0.5
                          :main-motiv :e-1
                          :situation-id :s2_the_puppet})
        world (trace/append-cycle world {:goal-id goal-id})]
    (is (= 1 (count (:trace world))))
    (is (= goal-id (get-in world [:trace 0 :selected-goal :id])))))

(deftest reporter-cycle-projects-python-shape
  (let [snapshot {:cycle-num 9
                  :timestamp "2026-03-12T12:00:09Z"
                  :goal-selection :highest_strength
                  :selected-goal {:id :g-2
                                  :goal-type :revenge
                                  :strength 0.9
                                  :situation-id :s1_seeing_through}
                  :top-candidates [{:id :g-2
                                    :goal-type :revenge
                                    :strength 0.9
                                    :situation-id :s1_seeing_through
                                    :reasons [:dominant :charged]}]
                  :active-indices [:s1_seeing_through :revenge]
                  :retrievals [{:episode-id :ep-7
                                :marks 2
                                :threshold 2
                                :overlap [:s1_seeing_through]}]
                  :chosen-node-id "n09_tear_the_set"
                  :selection {:policy :highest_strength}
                  :activations [{:goal-id :g-9
                                 :goal-type :roving}]
                  :mutations [{:family :reversal
                               :source-context :cx-2
                               :target-context :cx-3
                               :input-facts [{:fact/type :counterfactual
                                              :fact/id :performance_is_admitted}
                                             {:fact/type :situation
                                              :fact/id :s4_the_ring}]
                               :retracted-facts [{:fact/type :assumption
                                                  :fact/id :performance_stays_hidden}]}]
                  :emotion-shifts [{:emotion-id :e-dread
                                    :from-strength 0.82
                                    :to-strength 0.55
                                    :delta -0.27
                                    :valence :negative
                                    :role :trigger}]
                  :emotional-state [{:emotion-id :e-dread
                                     :strength 0.55
                                     :valence :negative
                                     :role :trigger}]
                  :feedback-applied nil
                  :serendipity-bias 0.15
                  :situations {:s1_seeing_through {:activation 0.95
                                                   :ripeness 0.8}}}
        exported (trace/reporter-cycle snapshot)]
    (is (= 9 (get exported "cycle")))
    (is (= "revenge" (get-in exported ["selected_goal" "goal_type"])))
    (is (= "s1_seeing_through"
           (get-in exported ["selected_goal" "situation_id"])))
    (is (= ["dominant" "charged"]
           (get-in exported ["top_candidates" 0 "reasons"])))
    (is (= "ep-7" (get-in exported ["retrieved" 0 "node_id"])))
    (is (= "roving" (get-in exported ["activations" 0 "goal_type"])))
    (is (= "reversal" (get-in exported ["branch_events" 0 "family"])))
    (is (= ["performance_is_admitted" "s4_the_ring"]
           (get-in exported ["branch_events" 0 "fact_ids"])))
    (is (= ["performance_stays_hidden"]
           (get-in exported ["branch_events" 0 "retracted_fact_ids"])))
    (is (= "e-dread" (get-in exported ["emotion_shifts" 0 "emotion_id"])))
    (is (= 0.55 (get-in exported ["emotional_state" 0 "strength"])))
    (is (= {"activation" 0.95
            "ripeness" 0.8}
           (get-in exported ["situations" "s1_seeing_through"])))))

(deftest reporter-cycle-emits-roving-episodic-branch-payload
  (let [snapshot {:cycle-num 7
                  :selected-goal {:id :g-7
                                  :goal-type :roving
                                  :strength 0.61
                                  :situation-id :s3_the_wait}
                  :mutations [{:family :roving
                               :source-context :cx-3
                               :target-context :cx-4
                               :seed-episode-id :ep-8
                               :reminded-episode-ids [:ep-9]
                               :active-indices [:aurora :light :horizon :wonder]}]}
        exported (trace/reporter-cycle snapshot)]
    (is (= "roving" (get-in exported ["branch_events" 0 "family"])))
    (is (= [] (get-in exported ["branch_events" 0 "fact_ids"])))
    (is (= ["ep-8" "ep-9"]
           (get-in exported ["branch_events" 0 "episode_ids"])))
    (is (= #{"aurora" "light" "horizon" "wonder"}
           (set (get-in exported ["branch_events" 0 "active_indices"]))))))

(deftest reporter-log-emits-top-level-schema
  (let [[world root-id] (world-with-root)
        [world goal-id] (goals/activate-top-level-goal
                         world
                         root-id
                         {:goal-type :rehearsal
                          :strength 0.7
                          :main-motiv :e-1
                          :situation-id :s4_the_ring})
        world (trace/append-cycle world
                                  {:goal-id goal-id
                                   :situations {:s4_the_ring {:activation 0.8
                                                              :ripeness 0.6}}})
        exported (trace/reporter-log
                  world
                  {:started_at "2026-03-12T12:00:00Z"
                   :git_commit "abc123"
                   :engine_path "kernel/src/daydreamer"})]
    (is (= "2026-03-12T12:00:00Z" (get exported "started_at")))
    (is (= "abc123" (get exported "git_commit")))
    (is (= "kernel/src/daydreamer" (get exported "engine_path")))
    (is (= 1 (count (get exported "cycles"))))))

(deftest dreamer-state-packet-projects-director-packet
  (let [packet (trace/dreamer-state-packet
                {:mode :daydreaming
                 :selected-goal {:goal-type :reversal}
                 :active-indices [:s1_seeing_through :reversal]
                 :retrievals [{:episode-id :ep-7} {:episode-id :ep-9}]
                 :active-plan [:g-1 :g-2]
                 :episode-cause "plan failure on s3 reactivated s1"
                 :context-id :cx-19})]
    (is (= "daydreaming" (get packet "mode")))
    (is (= "reversal" (get packet "goal_type")))
    (is (= ["ep-7" "ep-9"] (get packet "retrieved_episodes")))
    (is (= "cx-19" (get packet "trace_context_id")))))
