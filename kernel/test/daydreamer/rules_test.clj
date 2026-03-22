(ns daydreamer.rules-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.context :as cx]
            [daydreamer.goals :as goals]
            [daydreamer.rules :as rules]))

(defmacro thrown-with-msg?
  [expected-type expected-msg expr]
  `(try
     ~expr
     false
     (catch ~expected-type e#
       (boolean (re-find ~expected-msg (.getMessage e#))))))

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

(def same-type-cloud-source-rule
  {:id :test/cloud-source
   :rule-kind :planning
   :mueller-mode :both
   :antecedent-schema [{:fact/type :situation
                        :fact/id '?situation-id}]
   :consequent-schema [{:fact/type :goal
                        :goal-id '?goal-id}]
   :plausibility 0.7
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :create-goal-stub
                :failure-modes []
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors []
                :kernel-status :proposed
                :notes "Synthetic source rule with only goal-id on output."}})

(def same-type-cloud-target-rule
  {:id :test/cloud-target
   :rule-kind :inference
   :mueller-mode :inference-only
   :antecedent-schema [{:fact/type :goal
                        :status :failed}]
   :consequent-schema [{:fact/type :concern-trigger
                        :goal-status :failed}]
   :plausibility 0.5
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :create-concern-from-failed-goal
                :failure-modes []
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors []
                :kernel-status :proposed
                :notes "Synthetic target rule sharing only :fact/type."}})

(def bridge-terminal-rule
  {:id :test/terminal
   :rule-kind :planning
   :mueller-mode :both
   :antecedent-schema [{:fact/type :concern-trigger
                        :goal-id '?failed-goal-id
                        :emotion-id '?emotion-id}]
   :consequent-schema [{:fact/type :repair-step
                        :goal-id '?failed-goal-id
                        :emotion-id '?emotion-id
                        :step :test-repair}]
   :plausibility 0.4
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :bridge-to-repair-step
                :failure-modes []
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors []
                :kernel-status :proposed
                :notes "Synthetic terminal rule for path-search tests."}})

(defn with-deployment-role
  [rule deployment-role]
  (assoc-in rule [:provenance :deployment-role] deployment-role))

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

(deftest execute-rule-preserves-instantiate-behavior
  (let [bindings {'?context-id :cx-2
                  '?failed-goal-id :g-failed
                  '?emotion-id :e-shame
                  '?emotion-strength 0.25}]
    (is (= (rules/instantiate-rule sample-rule bindings)
           (rules/execute-rule sample-rule {:bindings bindings})))))

(deftest execute-rule-can-resolve-clojure-executor-from-call-registry
  (let [bindings {'?context-id :cx-2
                  '?failed-goal-id :g-failed
                  '?emotion-id :e-shame
                  '?emotion-strength 0.25}
        rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch
                                      :effect-ops [:test/noop]}})
        result (rules/execute-rule
                rule
                {:bindings bindings
                 :executor-registry
                 {:sample/dispatch
                  (fn [{:keys [rule call]}]
                    {:consequents [{:context-id :cx-2
                                    :failed-goal-id :g-failed
                                    :emotion-id :e-shame
                                    :emotion-strength 0.25
                                    :selection-policy :failed_goal_negative_emotion}]
                     :confidence (double (:plausibility rule))
                     :reason (str (:rule-id call))
                     :aux-indices [{:fact/type :goal
                                    :goal-id :g-failed}]
                     :surface-summary "registry-dispatch"
                     :effects [{:op :test/noop}]})}})]
    (is (= [{:context-id :cx-2
             :failed-goal-id :g-failed
             :emotion-id :e-shame
             :emotion-strength 0.25
             :selection-policy :failed_goal_negative_emotion}]
           (:consequents result)))
    (is (= "registry-dispatch" (:surface-summary result)))
    (is (= [{:op :test/noop}] (:effects result)))))

(deftest execute-rule-validates-effects-against-effect-schema
  (let [bindings {'?context-id :cx-2
                  '?failed-goal-id :g-failed
                  '?emotion-id :e-shame
                  '?emotion-strength 0.25}
        rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch
                                      :effect-ops [:test/noop]}}
                    :effect-schema [{:op :test/noop
                                     :goal-id '?failed-goal-id}])
        result (rules/execute-rule
                rule
                {:bindings bindings
                 :executor-registry
                 {:sample/dispatch
                  (fn [{:keys [rule]}]
                    {:consequents [{:context-id :cx-2
                                    :failed-goal-id :g-failed
                                    :emotion-id :e-shame
                                    :emotion-strength 0.25
                                    :selection-policy :failed_goal_negative_emotion}]
                     :confidence (double (:plausibility rule))
                     :reason "effect-schema-pass"
                     :aux-indices []
                     :surface-summary nil
                     :effects [{:op :test/noop
                                :goal-id :g-failed}]})}})]
    (is (= [{:op :test/noop
             :goal-id :g-failed}]
           (:effects result)))))

(deftest execute-rule-runs-call-supplied-effect-validator
  (let [bindings {'?context-id :cx-2
                  '?failed-goal-id :g-failed
                  '?emotion-id :e-shame
                  '?emotion-strength 0.25}
        rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch
                                      :effect-ops [:test/noop]}})]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"custom effect validation failed"
                          (rules/execute-rule
                           rule
                           {:bindings bindings
                            :executor-registry
                            {:sample/dispatch
                             (fn [{:keys [rule]}]
                               {:consequents [{:context-id :cx-2
                                               :failed-goal-id :g-failed
                                               :emotion-id :e-shame
                                               :emotion-strength 0.25
                                               :selection-policy :failed_goal_negative_emotion}]
                                :confidence (double (:plausibility rule))
                                :reason "registry-dispatch"
                                :aux-indices []
                                :surface-summary nil
                                :effects [{:op :test/noop}]})}
                            :effect-validator
                            (fn [{:keys [effect]}]
                              (when-not (contains? effect :goal-id)
                                (throw (ex-info "custom effect validation failed"
                                                {:effect effect}))))})))))

(deftest execute-rule-runs-call-supplied-effect-program-validator
  (let [bindings {'?context-id :cx-2
                  '?failed-goal-id :g-failed
                  '?emotion-id :e-shame
                  '?emotion-strength 0.25}
        rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch
                                      :effect-ops [:test/noop]}})]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"custom effect-program validation failed"
                          (rules/execute-rule
                           rule
                           {:bindings bindings
                            :executor-registry
                            {:sample/dispatch
                             (fn [{:keys [rule]}]
                               {:consequents [{:context-id :cx-2
                                               :failed-goal-id :g-failed
                                               :emotion-id :e-shame
                                               :emotion-strength 0.25
                                               :selection-policy :failed_goal_negative_emotion}]
                                :confidence (double (:plausibility rule))
                                :reason "registry-dispatch"
                                :aux-indices []
                                :surface-summary nil
                                :effects [{:op :test/noop}]})}
                            :effect-program-validator
                            (fn [{:keys [result]}]
                              (when (= [{:op :test/noop}] (:effects result))
                                (throw (ex-info "custom effect-program validation failed"
                                                {:result result}))))})))))

(deftest execute-rule-rejects-effect-count-mismatch
  (let [bindings {'?context-id :cx-2
                  '?failed-goal-id :g-failed
                  '?emotion-id :e-shame
                  '?emotion-strength 0.25}
        rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch
                                      :effect-ops [:test/noop]}}
                    :effect-schema [{:op :test/noop
                                     :goal-id '?failed-goal-id}])]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"effect count mismatch"
                          (rules/execute-rule
                           rule
                           {:bindings bindings
                            :executor-registry
                            {:sample/dispatch
                             (fn [{:keys [rule]}]
                               {:consequents [{:context-id :cx-2
                                               :failed-goal-id :g-failed
                                               :emotion-id :e-shame
                                               :emotion-strength 0.25
                                               :selection-policy :failed_goal_negative_emotion}]
                                :confidence (double (:plausibility rule))
                                :reason "effect-count-mismatch"
                                :aux-indices []
                                :surface-summary nil
                                :effects []})}})))))

(deftest execute-rule-rejects-effect-schema-mismatch
  (let [bindings {'?context-id :cx-2
                  '?failed-goal-id :g-failed
                  '?emotion-id :e-shame
                  '?emotion-strength 0.25}
        rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch
                                      :effect-ops [:test/noop]}}
                    :effect-schema [{:op :test/noop
                                     :goal-id '?failed-goal-id}])]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"effects do not satisfy effect-schema"
                          (rules/execute-rule
                           rule
                           {:bindings bindings
                            :executor-registry
                            {:sample/dispatch
                             (fn [{:keys [rule]}]
                               {:consequents [{:context-id :cx-2
                                               :failed-goal-id :g-failed
                                               :emotion-id :e-shame
                                               :emotion-strength 0.25
                                               :selection-policy :failed_goal_negative_emotion}]
                                :confidence (double (:plausibility rule))
                                :reason "effect-schema-mismatch"
                                :aux-indices []
                                :surface-summary nil
                                :effects [{:op :test/noop
                                           :goal-id :g-other}]})}})))))

(deftest execute-rule-rejects-effect-schema-order-mismatch
  (let [bindings {'?context-id :cx-2
                  '?failed-goal-id :g-failed
                  '?emotion-id :e-shame
                  '?emotion-strength 0.25}
        rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch
                                      :effect-ops [:test/a :test/b]}}
                    :effect-schema [{:op :test/a
                                     :goal-id '?failed-goal-id}
                                    {:op :test/b
                                     :context-id '?context-id}])]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"effects do not satisfy effect-schema"
                          (rules/execute-rule
                           rule
                           {:bindings bindings
                            :executor-registry
                            {:sample/dispatch
                             (fn [{:keys [rule]}]
                               {:consequents [{:context-id :cx-2
                                               :failed-goal-id :g-failed
                                               :emotion-id :e-shame
                                               :emotion-strength 0.25
                                               :selection-policy :failed_goal_negative_emotion}]
                                :confidence (double (:plausibility rule))
                                :reason "effect-schema-order-mismatch"
                                :aux-indices []
                                :surface-summary nil
                                :effects [{:op :test/b
                                           :context-id :cx-2}
                                          {:op :test/a
                                           :goal-id :g-failed}]})}})))))

(deftest apply-effects-threads-world-and-effect-state
  (let [[world effect-state]
        (rules/apply-effects
         {:events []}
         [{:op :test/append
           :value 1}
          {:op :test/append
           :value 2}]
         {:effect-handlers
          {:test/append
           (fn [{:keys [world effect effect-state]}]
             [(update world :events conj (:value effect))
              (update effect-state :count (fnil inc 0))])}
          :initial-effect-state {:count 0}})]
    (is (= {:events [1 2]} world))
    (is (= {:count 2} effect-state))))

(deftest apply-effects-preserves-explicit-falsey-initial-state
  (is (= [{:events []} nil]
         (rules/apply-effects
          {:events []}
          []
          {:effect-handler
           (fn [{:keys [world effect-state]}]
             [world effect-state])
           :initial-effect-state nil})))
  (is (= [{:events []} false]
         (rules/apply-effects
          {:events []}
          []
          {:effect-handler
           (fn [{:keys [world effect-state]}]
             [world effect-state])
           :initial-effect-state false}))))

(deftest apply-effects-can-run-builtin-context-and-goal-ops
  (let [root-id :cx-1
        world {:id-counter 1
               :contexts {root-id (assoc (cx/create-context) :id root-id)}
               :goals {:g-1 (goals/create-goal {:id :g-1
                                                :goal-type :roving
                                                :next-cx root-id})}}
        fact {:fact/type :test/fact
              :fact/id :f-1}
        [world effect-state]
        (rules/apply-effects
         world
         [{:op :context/sprout
           :source-context-id root-id
           :ref :branch-context}
          {:op :fact/assert
           :context-ref :branch-context
           :fact fact}
          {:op :context/set-ordering
           :context-ref :branch-context
           :ordering 0.75}
          {:op :goal/set-next-context
           :goal-id :g-1
           :context-ref :branch-context}]
         {})
        branch-id (get-in effect-state [:context-refs :branch-context])]
    (is (= :cx-2 branch-id))
    (is (cx/fact-true? world branch-id fact))
    (is (= 0.75
           (get-in world [:contexts branch-id :ordering])))
    (is (= branch-id
           (get-in world [:goals :g-1 :next-cx])))))

(deftest apply-effects-rejects-unresolved-context-ref
  (let [root-id :cx-1
        world {:id-counter 1
               :contexts {root-id (assoc (cx/create-context) :id root-id)}
               :goals {}}]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"Unknown effect context ref"
                          (rules/apply-effects
                           world
                           [{:op :context/set-ordering
                             :context-ref :branch-context
                             :ordering 0.75}]
                           {})))))

(deftest apply-effects-rejects-missing-goal-for-goal-set-next-context
  (let [root-id :cx-1
        world {:id-counter 1
               :contexts {root-id (assoc (cx/create-context) :id root-id)}
               :goals {}}]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"Unknown goal for :goal/set-next-context"
                          (rules/apply-effects
                           world
                           [{:op :goal/set-next-context
                             :goal-id :g-missing
                             :context-ref root-id}]
                           {})))))

(deftest apply-effects-rejects-builtin-handler-overrides
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        #"Builtin effect handlers cannot be overridden"
                        (rules/apply-effects
                         {:id-counter 1
                          :contexts {:cx-1 (assoc (cx/create-context) :id :cx-1)}}
                         [{:op :context/sprout
                           :source-context-id :cx-1
                           :ref :branch-context}]
                         {:effect-handlers
                          {:context/sprout
                           (fn [{:keys [world effect-state]}]
                             [world (assoc effect-state :override true)])}}))))

(deftest apply-effects-does-not-route-builtin-ops-through-catch-all-handler
  (let [[world effect-state]
        (rules/apply-effects
         {:id-counter 1
          :contexts {:cx-1 (assoc (cx/create-context) :id :cx-1)}}
         [{:op :context/sprout
           :source-context-id :cx-1
           :ref :branch-context}]
         {:effect-handler
          (fn [_]
            (throw (ex-info "catch-all should not run for builtin op" {})))})]
    (is (= :cx-2
           (get-in effect-state [:context-refs :branch-context])))
    (is (contains? (:contexts world) :cx-2))))

(deftest apply-effects-rejects-malformed-handler-result
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        #"Effect handler must return"
                        (rules/apply-effects
                         {}
                         [{:op :test/noop}]
                         {:effect-handler
                          (fn [_]
                            {:world {}
                             :effect-state {}})}))))

(deftest apply-effects-rejects-missing-op-handler
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        #"No effect handler registered for op"
                        (rules/apply-effects
                         {}
                         [{:op :test/noop}]
                         {:effect-handlers
                          {:test/other
                           (fn [{:keys [world effect-state]}]
                             [world effect-state])}}))))

(deftest execute-rule-rejects-unsupported-effect-op
  (let [bindings {'?context-id :cx-2
                  '?failed-goal-id :g-failed
                  '?emotion-id :e-shame
                  '?emotion-strength 0.25}
        rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch
                                      :effect-ops [:test/noop]}})]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"effect op is not declared"
                          (rules/execute-rule
                           rule
                           {:bindings bindings
                            :executor-registry
                            {:sample/dispatch
                             (fn [{:keys [rule]}]
                               {:consequents [{:context-id :cx-2
                                               :failed-goal-id :g-failed
                                               :emotion-id :e-shame
                                               :emotion-strength 0.25
                                               :selection-policy :failed_goal_negative_emotion}]
                                :confidence (double (:plausibility rule))
                                :reason "bad-effect"
                                :aux-indices []
                                :surface-summary "bad-effect"
                                :effects [{:op :test/other}]})}})))))

(deftest execute-rule-rejects-effect-without-keyword-op
  (let [bindings {'?context-id :cx-2
                  '?failed-goal-id :g-failed
                  '?emotion-id :e-shame
                  '?emotion-strength 0.25}
        rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch
                                      :effect-ops [:test/noop]}})]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"effects must declare keyword :op"
                          (rules/execute-rule
                           rule
                           {:bindings bindings
                            :executor-registry
                            {:sample/dispatch
                             (fn [{:keys [rule]}]
                               {:consequents [{:context-id :cx-2
                                               :failed-goal-id :g-failed
                                               :emotion-id :e-shame
                                               :emotion-strength 0.25
                                               :selection-policy :failed_goal_negative_emotion}]
                                :confidence (double (:plausibility rule))
                                :reason "missing-op"
                                :aux-indices []
                                :surface-summary "missing-op"
                                :effects [{:context-ref :branch-context}]})}})))))

(deftest execute-rule-rejects-effects-without-declared-effect-ops
  (let [bindings {'?context-id :cx-2
                  '?failed-goal-id :g-failed
                  '?emotion-id :e-shame
                  '?emotion-strength 0.25}
        rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch}})]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"require declared :effect-ops"
                          (rules/execute-rule
                           rule
                           {:bindings bindings
                            :executor-registry
                            {:sample/dispatch
                             (fn [{:keys [rule]}]
                               {:consequents [{:context-id :cx-2
                                               :failed-goal-id :g-failed
                                               :emotion-id :e-shame
                                               :emotion-strength 0.25
                                               :selection-policy :failed_goal_negative_emotion}]
                                :confidence (double (:plausibility rule))
                                :reason "missing-effect-ops"
                                :aux-indices []
                                :surface-summary "missing-effect-ops"
                                :effects [{:op :test/noop}]})}})))))

(deftest matched-rule-applications-drops-nil-executor-results
  (let [rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch}})]
    (is (= []
           (rules/matched-rule-applications
            rule
            [{:fact/type :goal
              :goal-id :g-failed
              :activation-context :cx-2}
             {:fact/type :emotion
              :emotion-id :e-shame
              :strength 0.25}
             {:fact/type :dependency
              :from-id :e-shame
              :to-id :g-failed}]
            {}
            {:executor-registry {:sample/dispatch (fn [_] nil)}})))))

(deftest execute-rule-rejects-false-executor-result
  (let [rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:executor-id :sample/dispatch}})]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"illegal false result"
                          (rules/execute-rule
                           rule
                           {:bindings {'?context-id :cx-2
                                       '?failed-goal-id :g-failed
                                       '?emotion-id :e-shame
                                       '?emotion-strength 0.25}
                            :executor-registry {:sample/dispatch (fn [_] false)}})))))

(deftest execute-rule-rejects-consequent-count-mismatch
  (let [rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:fn (fn [_]
                                            {:consequents []
                                             :confidence 0.04
                                             :reason "count mismatch"
                                             :aux-indices []
                                             :surface-summary nil})}})]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"consequent count mismatch"
                          (rules/execute-rule rule
                                              {:bindings {'?context-id :cx-2
                                                          '?failed-goal-id :g-failed
                                                          '?emotion-id :e-shame
                                                          '?emotion-strength 0.25}})))))

(deftest execute-rule-rejects-binding-drift
  (let [rule (assoc sample-rule
                    :executor {:kind :clojure-fn
                               :spec {:fn (fn [_]
                                            {:consequents [{:context-id :cx-2
                                                            :failed-goal-id :g-other
                                                            :emotion-id :e-shame
                                                            :emotion-strength 0.25
                                                            :selection-policy :failed_goal_negative_emotion}]
                                             :confidence 0.04
                                             :reason "binding drift"
                                             :aux-indices [{:fact/type :goal
                                                            :goal-id :g-other}]
                                             :surface-summary nil})}})]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"do not satisfy consequent-schema"
                          (rules/execute-rule rule
                                              {:bindings {'?context-id :cx-2
                                                          '?failed-goal-id :g-failed
                                                          '?emotion-id :e-shame
                                                          '?emotion-strength 0.25}})))))

(deftest execute-rule-runs-denotation-validation-fn
  (let [rule (-> sample-rule
                 (assoc-in [:denotation :failure-modes] [:missing-surface-summary])
                 (assoc-in [:denotation :validation-fn]
                           (fn [{:keys [result]}]
                             (when (nil? (:surface-summary result))
                               [:missing-surface-summary])))
                 (assoc :executor {:kind :clojure-fn
                                   :spec {:fn (fn [_]
                                                {:consequents [{:context-id :cx-2
                                                                :failed-goal-id :g-failed
                                                                :emotion-id :e-shame
                                                                :emotion-strength 0.25
                                                                :selection-policy :failed_goal_negative_emotion}]
                                                 :confidence 0.04
                                                 :reason "denotation check"
                                                 :aux-indices [{:fact/type :goal
                                                                :goal-id :g-failed}]
                                                 :surface-summary nil})}}))]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"failed denotational validation"
                          (rules/execute-rule rule
                                              {:bindings {'?context-id :cx-2
                                                          '?failed-goal-id :g-failed
                                                          '?emotion-id :e-shame
                                                          '?emotion-strength 0.25}})))))

(deftest execute-rule-rejects-undeclared-denotation-failures
  (let [rule (-> sample-rule
                 (assoc-in [:denotation :failure-modes] [:missing-surface-summary])
                 (assoc-in [:denotation :validation-fn]
                           (fn [_]
                             [:undeclared-failure]))
                 (assoc :executor {:kind :clojure-fn
                                   :spec {:fn (fn [_]
                                                {:consequents [{:context-id :cx-2
                                                                :failed-goal-id :g-failed
                                                                :emotion-id :e-shame
                                                                :emotion-strength 0.25
                                                                :selection-policy :failed_goal_negative_emotion}]
                                                 :confidence 0.04
                                                 :reason "denotation check"
                                                 :aux-indices [{:fact/type :goal
                                                                :goal-id :g-failed}]
                                                 :surface-summary "ok"})}}))]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"undeclared failure mode"
                          (rules/execute-rule rule
                                              {:bindings {'?context-id :cx-2
                                                          '?failed-goal-id :g-failed
                                                          '?emotion-id :e-shame
                                                          '?emotion-strength 0.25}})))))

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

(deftest build-connection-graph-requires-shared-structural-keys
  (let [graph (rules/build-connection-graph
               [same-type-cloud-source-rule
                same-type-cloud-target-rule])]
    (is (= [] (:edges graph)))
    (is (nil? (get (:outgoing graph) :test/cloud-source)))
    (is (nil? (get (:incoming graph) :test/cloud-target)))))

(deftest graph-query-helpers-explain-bounded-paths
  (let [graph (rules/build-connection-graph
               [bridge-source-rule
                bridge-target-rule
                bridge-terminal-rule])
        paths (rules/reachable-paths graph :test/source {:max-depth 3})]
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
           (rules/outgoing-edges graph :test/source)))
    (is (= [{:from-rule :test/target
             :to-rule :test/terminal
             :from-projection {:fact/type :concern-trigger
                               :goal-id '?failed-goal-id
                               :emotion-id '?emotion-id}
             :to-projection {:fact/type :concern-trigger
                             :goal-id '?failed-goal-id
                             :emotion-id '?emotion-id}
             :bindings {}
             :shared-keys #{:goal-id :emotion-id}
             :edge-kind :state-transition}]
           (rules/incoming-edges graph :test/terminal)))
    (is (= [[:test/source :test/target]
            [:test/source :test/target :test/terminal]]
           (mapv :rule-path paths)))
    (is (= {:rule-path [:test/source :test/target :test/terminal]
            :edges [{:from-rule :test/source
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
                     :edge-kind :goal-decomposition}
                    {:from-rule :test/target
                     :to-rule :test/terminal
                     :from-projection {:fact/type :concern-trigger
                                       :goal-id '?failed-goal-id
                                       :emotion-id '?emotion-id}
                     :to-projection {:fact/type :concern-trigger
                                     :goal-id '?failed-goal-id
                                     :emotion-id '?emotion-id}
                     :bindings {}
                     :shared-keys #{:goal-id :emotion-id}
                     :edge-kind :state-transition}]
            :fact-types [:goal :concern-trigger]
            :edge-kinds [:goal-decomposition :state-transition]
            :shared-keys [#{:goal-id :status :activation-context}
                          #{:goal-id :emotion-id}]}
           (dissoc (rules/explain-path graph
                                       [:test/source
                                        :test/target
                                        :test/terminal])
                   :depth)))))

(deftest intervention-delta-removing-bridge-rule-breaks-terminal-reachability
  (let [graph (rules/build-connection-graph
               [bridge-source-rule
                bridge-target-rule
                bridge-terminal-rule])
        delta (rules/intervention-delta graph
                                        :test/source
                                        [:test/target]
                                        {:max-depth 3})]
    (is (= [:test/target]
           (:removed-rule-ids delta)))
    (is (= [[:test/source :test/target]
            [:test/source :test/target :test/terminal]]
           (mapv :rule-path (:before-paths delta))))
    (is (= []
           (mapv :rule-path (:after-paths delta))))
    (is (= [[:test/source :test/target]
            [:test/source :test/target :test/terminal]]
           (mapv :rule-path (:removed-paths delta))))
    (is (= []
           (:preserved-paths delta)))))

(deftest bridge-paths-require-an-explicit-two-hop-bridge
  (let [graph (rules/build-connection-graph
               [bridge-source-rule
                bridge-target-rule
                bridge-terminal-rule])]
    (is (= [[:test/source :test/target :test/terminal]]
           (mapv :rule-path
                 (rules/bridge-paths graph
                                     :test/source
                                     :test/terminal))))
    (is (= []
           (rules/bridge-paths graph
                               :test/source
                               :test/target)))
    (is (= [[:test/source :test/target]]
           (mapv :rule-path
                 (rules/bridge-paths graph
                                     :test/source
                                     :test/target
                                     {:min-depth 1
                                      :max-depth 2}))))))

(deftest sync-rule-access-registry-derives-default-statuses-from-deployment-role
  (let [world (rules/sync-rule-access-registry
               {:cycle 7}
               [(with-deployment-role bridge-source-rule :authored-core)
                (with-deployment-role bridge-target-rule :authored-frontier)
                (with-deployment-role bridge-terminal-rule :induced)])]
    (is (= {:status :accessible
            :source :authored-core
            :opened-cycle 7
            :opened-by nil
            :history []}
           (get-in world [:rule-access :test/source])))
    (is (= {:status :frontier
            :source :authored-frontier
            :opened-cycle nil
            :opened-by nil
            :history []}
           (get-in world [:rule-access :test/target])))
    (is (= {:status :frontier
            :source :induced
            :opened-cycle nil
            :opened-by nil
            :history []}
           (get-in world [:rule-access :test/terminal])))))

(deftest sync-rule-access-registry-refreshes-default-derived-entries-when-deployment-role-changes
  (let [world (rules/sync-rule-access-registry
               {:cycle 2}
               [(with-deployment-role bridge-source-rule :authored-core)])
        world (rules/sync-rule-access-registry
               world
               [(with-deployment-role bridge-source-rule :authored-frontier)])]
    (is (= {:status :frontier
            :source :authored-frontier
            :opened-cycle nil
            :opened-by nil
            :history []}
           (get-in world [:rule-access :test/source])))))

(deftest sync-rule-access-registry-fails-closed-on-unknown-deployment-role
  (let [world (rules/sync-rule-access-registry
               {:cycle 11}
               [(with-deployment-role bridge-target-rule :typo-frontier)])]
    (is (= {:status :quarantined
            :source :typo-frontier
            :opened-cycle nil
            :opened-by nil
            :history []}
           (get-in world [:rule-access :test/target])))))

(deftest sync-rule-access-registry-preserves-explicit-transitions-while-refreshing-source
  (let [frontier-rule (with-deployment-role bridge-target-rule :authored-frontier)
        world (rules/sync-rule-access-registry {:cycle 4} [frontier-rule])
        [world _]
        (rules/set-rule-access-status world
                                      [frontier-rule]
                                      :test/target
                                      :accessible
                                      {:reason :durable-episode-opened-rule
                                       :episode-id :ep-frontier})
        world (rules/sync-rule-access-registry
               world
               [(with-deployment-role bridge-target-rule :authored-core)])]
    (is (= {:status :accessible
            :source :authored-core
            :opened-cycle 4
            :opened-by :ep-frontier
            :history [{:rule-id :test/target
                       :from-status :frontier
                       :to-status :accessible
                       :cycle 4
                       :reason :durable-episode-opened-rule
                       :episode-id :ep-frontier}]}
           (get-in world [:rule-access :test/target])))))

(deftest planning-and-serendipity-graphs-filter-by-effective-rule-access
  (let [graph (rules/build-connection-graph
               [(with-deployment-role bridge-source-rule :authored-core)
                (with-deployment-role bridge-target-rule :authored-frontier)
                (with-deployment-role bridge-terminal-rule :quarantined)])
        planning (rules/planning-graph {:cycle 0} graph)
        serendipity (rules/serendipity-graph {:cycle 0} graph)]
    (is (= [] (rules/outgoing-edges planning :test/source)))
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
           (rules/outgoing-edges serendipity :test/source)))
    (is (= [] (rules/outgoing-edges serendipity :test/target)))))

(deftest durable-promotion-opens-frontier-rules-to-accessible
  (let [graph (rules/build-connection-graph
               [(with-deployment-role bridge-source-rule :authored-core)
                (with-deployment-role bridge-target-rule :authored-frontier)])
        world (rules/sync-rule-access-registry {:cycle 3} graph)
        episode {:id :ep-frontier
                 :rule-path [:test/source :test/target]
                 :anti-residue-flags []}
        [world transitions]
        (rules/reconcile-episode-rule-access world
                                             graph
                                             episode
                                             {:to-status :durable}
                                             {:branch-context-id :cx-17})]
    (is (= :accessible
           (get-in world [:rule-access :test/target :status])))
    (is (= :accessible
           (get-in world [:rule-access :test/source :status])))
    (is (= [{:rule-id :test/target
             :from-status :frontier
             :to-status :accessible
             :cycle 3
             :reason :durable-episode-opened-rule
             :episode-id :ep-frontier
             :branch-context-id :cx-17}]
           transitions))))

(deftest durable-transition-with-hard-failure-does-not-open-frontier-rules
  (let [graph (rules/build-connection-graph
               [(with-deployment-role bridge-source-rule :authored-core)
                (with-deployment-role bridge-target-rule :authored-frontier)])
        world (rules/sync-rule-access-registry {:cycle 6} graph)
        episode {:id :ep-contradicted
                 :rule-path [:test/source :test/target]
                 :anti-residue-flags [:contradicted]}
        [world transitions]
        (rules/reconcile-episode-rule-access world
                                             graph
                                             episode
                                             {:to-status :durable}
                                             {:branch-context-id :cx-23})]
    (is (= :frontier
           (get-in world [:rule-access :test/target :status])))
    (is (= :accessible
           (get-in world [:rule-access :test/source :status])))
    (is (= [] transitions))))

(deftest hard-failure-demotion-can-quarantine-noncore-rules
  (let [graph (rules/build-connection-graph
               [(with-deployment-role bridge-source-rule :authored-core)
                (with-deployment-role bridge-target-rule :induced)])
        world (rules/sync-rule-access-registry {:cycle 5} graph)
        [world _]
        (rules/set-rule-access-status world
                                      graph
                                      :test/target
                                      :accessible
                                      {:reason :durable-episode-opened-rule
                                       :episode-id :ep-frontier})
        episode {:id :ep-frontier
                 :rule-path [:test/source :test/target]
                 :anti-residue-flags [:contradicted]}
        [world transitions]
        (rules/reconcile-episode-rule-access world
                                             graph
                                             episode
                                             {:to-status :provisional}
                                             {:branch-context-id :cx-22})]
    (is (= :accessible
           (get-in world [:rule-access :test/source :status])))
    (is (= :quarantined
           (get-in world [:rule-access :test/target :status])))
    (is (= [{:rule-id :test/target
             :from-status :accessible
             :to-status :quarantined
             :cycle 5
             :reason :outcome-driven-rule-quarantine
             :episode-id :ep-frontier
             :branch-context-id :cx-22}]
           transitions))))

(deftest later-durable-evidence-can-reopen-quarantined-noncore-rules-to-frontier
  (let [graph (rules/build-connection-graph
               [(with-deployment-role bridge-source-rule :authored-core)
                (with-deployment-role bridge-target-rule :induced)])
        world (rules/sync-rule-access-registry {:cycle 7} graph)
        [world _]
        (rules/set-rule-access-status world
                                      graph
                                      :test/target
                                      :accessible
                                      {:reason :durable-episode-opened-rule
                                       :episode-id :ep-initial})
        [world _]
        (rules/reconcile-episode-rule-access world
                                             graph
                                             {:id :ep-failed
                                              :rule-path [:test/source :test/target]
                                              :anti-residue-flags [:contradicted]}
                                             {:to-status :provisional}
                                             {:branch-context-id :cx-quarantine})
        [world transitions]
        (rules/reconcile-episode-rule-access world
                                             graph
                                             {:id :ep-rehab
                                              :rule-path [:test/source :test/target]
                                              :anti-residue-flags []}
                                             {:to-status :durable}
                                             {:branch-context-id :cx-reopen})]
    (is (= :frontier
           (get-in world [:rule-access :test/target :status])))
    (is (= [{:rule-id :test/target
             :from-status :quarantined
             :to-status :frontier
             :cycle 7
             :reason :durable-evidence-reopened-rule
             :episode-id :ep-rehab
             :branch-context-id :cx-reopen}]
           transitions))))

(deftest durable-evidence-does-not-reopen-default-quarantined-unknown-role-rules
  (let [graph (rules/build-connection-graph
               [(with-deployment-role bridge-source-rule :authored-core)
                (with-deployment-role bridge-target-rule :typo-frontier)])
        world (rules/sync-rule-access-registry {:cycle 8} graph)
        [world transitions]
        (rules/reconcile-episode-rule-access world
                                             graph
                                             {:id :ep-rehab
                                              :rule-path [:test/source :test/target]
                                              :anti-residue-flags []}
                                             {:to-status :durable}
                                             {:branch-context-id :cx-reopen})]
    (is (= :quarantined
           (get-in world [:rule-access :test/target :status])))
    (is (= [] transitions))))
