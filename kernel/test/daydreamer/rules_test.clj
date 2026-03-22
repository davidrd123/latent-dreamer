(ns daydreamer.rules-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.rules :as rules]))

(def sample-rule
  {:id :test/activate-roving
   :rule-kind :inference
   :mueller-mode :inference-only
   :antecedent-schema [{:fact/type :goal
                        :goal-id '?failed-goal-id}
                       {:fact/type :emotion
                        :emotion-id '?emotion-id
                        :strength '?emotion-strength}]
   :consequent-schema [{:context-id '?context-id
                        :failed-goal-id '?failed-goal-id
                        :emotion-id '?emotion-id
                        :emotion-strength '?emotion-strength
                        :selection-policy :failed_goal_negative_emotion}]
   :plausibility 0.04
   :index-projections {:match []
                       :emit [{:fact/type :goal
                               :goal-id '?failed-goal-id}]}
   :denotation {:intended-effect :activate-roving-daydream-goal
                :failure-modes [:emotion-below-threshold]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors []
                :kernel-status :proposed
                :notes "Test rule"}})

(deftest instantiate-rule-substitutes-bindings
  (testing "RuleV1 validation accepts a complete instantiate rule"
    (is (true? (rules/valid-rule? sample-rule))))
  (testing "instantiate-rule replaces logic variables in consequents and indices"
    (is (= {:consequents [{:context-id :cx-2
                           :failed-goal-id :g-failed
                           :emotion-id :e-shame
                           :emotion-strength 0.25
                           :selection-policy :failed_goal_negative_emotion}]
            :confidence 0.04
            :reason ":activate-roving-daydream-goal"
            :aux-indices [{:fact/type :goal
                           :goal-id :g-failed}]
            :surface-summary nil}
           (rules/instantiate-rule sample-rule
                                   {'?context-id :cx-2
                                    '?failed-goal-id :g-failed
                                    '?emotion-id :e-shame
                                    '?emotion-strength 0.25})))))
