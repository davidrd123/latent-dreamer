(ns daydreamer.rules-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.rules :as rules]))

(def sample-rule
  {:id :test/activate-roving
   :rule-kind :inference
   :mueller-mode :inference-only
   :antecedent-schema [{:fact/type :goal
                        :goal-id '?failed-goal-id
                        :activation-context '?context-id}
                       {:fact/type :emotion
                        :emotion-id '?emotion-id
                        :strength '?emotion-strength}
                       {:fact/type :dependency
                        :from-id '?emotion-id
                        :to-id '?failed-goal-id}]
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

(def bridge-source-rule
  {:id :test/source
   :rule-kind :planning
   :mueller-mode :both
   :antecedent-schema [{:fact/type :situation
                        :fact/id '?situation-id}]
   :consequent-schema [{:fact/type :goal
                        :goal-id '?goal-id
                        :status :failed
                        :activation-context '?context-id}]
   :plausibility 0.7
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :create-failed-goal
                :failure-modes []
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors []
                :kernel-status :proposed
                :notes "Synthetic source rule for graph tests."}})

(def bridge-target-rule
  {:id :test/target
   :rule-kind :inference
   :mueller-mode :inference-only
   :antecedent-schema [{:fact/type :goal
                        :goal-id '?failed-goal-id
                        :status :failed
                        :activation-context '?context-id}
                       {:fact/type :emotion
                        :emotion-id '?emotion-id
                        :strength '?emotion-strength}]
   :consequent-schema [{:fact/type :concern-trigger
                        :goal-id '?failed-goal-id
                        :emotion-id '?emotion-id}]
   :plausibility 0.5
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :activate-concern
                :failure-modes []
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors []
                :kernel-status :proposed
                :notes "Synthetic target rule for graph tests."}})

(def incompatible-target-rule
  (assoc-in bridge-target-rule
            [:antecedent-schema 0 :status]
            :succeeded))

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

(deftest match-rule-binds-shared-variables-across-facts
  (let [facts [{:fact/type :goal
                :goal-id :g-failed
                :top-level-goal :g-failed
                :status :failed
                :activation-context :cx-2
                :extra :ignored}
               {:fact/type :emotion
                :emotion-id :e-shame
                :strength 0.25
                :valence :negative}
               {:fact/type :dependency
                :from-id :e-shame
                :to-id :g-failed}
               {:fact/type :goal
                :goal-id :g-other
                :top-level-goal :g-other
                :status :failed
                :activation-context :cx-2}]
        matches (rules/match-rule sample-rule facts {'?context-id :cx-2})]
    (is (= 1 (count matches)))
    (is (= {'?context-id :cx-2
            '?failed-goal-id :g-failed
            '?emotion-id :e-shame
            '?emotion-strength 0.25}
           (:bindings (first matches))))
    (is (= [:goal :emotion :dependency]
           (mapv :fact/type (:matched-facts (first matches)))))))

(deftest match-rule-respects-prebound-variables-and-shared-links
  (let [facts [{:fact/type :goal
                :goal-id :g-failed
                :top-level-goal :g-failed
                :status :failed
                :activation-context :cx-2}
               {:fact/type :emotion
                :emotion-id :e-shame
                :strength 0.25}
               {:fact/type :dependency
                :from-id :e-other
                :to-id :g-failed}]
        matches (rules/match-rule sample-rule facts {'?context-id :cx-2})]
    (is (= [] matches))))

(deftest derive-graph-cache-exposes-graphable-projections
  (let [cached-rule (rules/derive-graph-cache bridge-source-rule)]
    (is (= [{:fact-type :goal
             :projection {:fact/type :goal
                          :goal-id '?goal-id
                          :status :failed
                          :activation-context '?context-id}
             :required-keys #{:goal-id :status :activation-context}
             :open-fields #{:goal-id :activation-context}
             :constant-fields {:status :failed}}]
           (get-in cached-rule [:graph-cache :out-edge-bases])))
    (is (= [{:fact-type :situation
             :projection {:fact/type :situation
                          :fact/id '?situation-id}
             :required-keys #{:fact/id}
             :open-fields #{:fact/id}
             :constant-fields {}}]
           (get-in cached-rule [:graph-cache :in-edge-bases])))))

(deftest build-connection-graph-creates-structural-edges-only-for-compatible-rules
  (let [graph (rules/build-connection-graph
               [bridge-source-rule
                bridge-target-rule
                incompatible-target-rule])]
    (is (= [{:from-rule :test/source
             :to-rule :test/target
             :from-projection {:fact/type :goal
                               :goal-id '?goal-id
                               :status :failed
                               :activation-context '?context-id}
             :to-projection {:fact/type :goal
                             :goal-id '?failed-goal-id
                             :status :failed
                             :activation-context '?context-id}
             :bindings {}
             :shared-keys #{:goal-id :status :activation-context}
             :edge-kind :goal-decomposition}]
           (:edges graph)))
    (is (= [:test/target]
           (mapv :to-rule (get (:outgoing graph) :test/source))))
    (is (nil? (get (:outgoing graph) :test/target)))
    (is (nil? (get (:incoming graph) :test/source)))))
