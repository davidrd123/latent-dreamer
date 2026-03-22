(ns daydreamer.goal-families
  "Per-family activation rules and plan bodies.

  Wave 2 starts with REVERSAL because it exercises the context machinery that
  the Python scheduler does not have: copying an old planning state, attaching
  it as a pseudo-sprout under a new branch, clearing emotional residue, and
  rebinding surviving plan structure to a new top-level goal.

  This namespace implements that first kernel. It does not attempt Mueller's
  full search over failure leafs yet; it provides the alternative-past branch
  primitive that later goal-family plans can call."
  (:require [clojure.set :as set]
            [daydreamer.context :as cx]
            [daydreamer.episodic-memory :as episodic]
            [daydreamer.fact-query :refer [dependency-fact?
                                           emotion-fact?
                                           fact-id
                                           fact-ref-ids
                                           fact-type?
                                           failure-cause-fact?
                                           goal-fact?
                                           intends-fact?
                                           negative-emotion-fact?
                                           positive-emotion-fact?
                                           rationalization-frame-fact?]]
            [daydreamer.goals :as goals]
            [daydreamer.rules :as rules]))

(def ^:private supported-families
  #{:reversal :roving :rationalization :recovery :rehearsal})

(def ^:private terminal-goal-statuses
  #{:failed :succeeded :terminated})

(defn supported-family?
  "Return true when the kernel has a namespace-level implementation target for
  the given family."
  [goal-type]
  (contains? supported-families goal-type))

(def ^:private reversal-leaf-policy
  :emotion_then_depth)

(def ^:private reversal-cause-policy
  :stored_priority)

(def ^:private reverse-leaf-policy
  :intends_weak_leaf)

(def ^:private reverse-leaf-threshold
  0.5)

(def ^:private roving-emotion-threshold
  0.04)

(def ^:private rationalization-emotion-threshold
  0.7)

(def ^:private reversal-emotion-threshold
  0.5)

(def ^:private roving-plan-policy
  :pleasant_episode_seed)

(def ^:private rationalization-frame-policy
  :stored_priority)

(def ^:private rationalization-plan-policy
  :stored_rationalization_frame)

(def ^:private rationalization-diversion-policy
  :divert_emot_to_tlg_bridge)

(def ^:private rationalization-diversion-scale
  0.35)

(def ^:private roving-plan-request-rule
  {:id :goal-family/roving-plan-request
   :rule-kind :planning
   :mueller-mode :plan-only
   :antecedent-schema [{:goal-type :roving
                        :goal-id '?goal-id
                        :context-id '?context-id
                        :episode-id '?episode-id
                        :ordering '?ordering}]
   :consequent-schema [{:fact/type :family-plan-request
                        :goal-type :roving
                        :goal-id '?goal-id
                        :context-id '?context-id
                        :episode-id '?episode-id
                        :ordering '?ordering
                        :selection-policy roving-plan-policy}]
   :plausibility 1.0
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :emit-roving-plan-request
                :failure-modes [:missing-seed-episode
                                :missing-planning-context]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:requires-episode? true
                     :requires-context? true}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-roving]
                :kernel-status :partial
                :notes "Graphable request fact for roving branch planning."}})

(def ^:private roving-plan-dispatch-rule
  {:id :goal-family/roving-plan-dispatch
   :rule-kind :planning
   :mueller-mode :plan-only
   :antecedent-schema [{:fact/type :family-plan-request
                        :goal-type :roving
                        :goal-id '?goal-id
                        :context-id '?context-id
                        :episode-id '?episode-id
                        :ordering '?ordering
                        :selection-policy '?selection-policy}]
   :consequent-schema [{:goal-id '?goal-id
                        :context-id '?context-id
                        :episode-id '?episode-id
                        :ordering '?ordering
                        :selection-policy '?selection-policy}]
   :plausibility 1.0
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :dispatch-roving-plan-request
                :failure-modes [:missing-family-plan-request]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:request-goal-type :roving}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-roving]
                :kernel-status :partial
                :notes "Consumes a roving plan request into the current bounded plan payload."}})

(def ^:private rationalization-plan-request-rule
  {:id :goal-family/rationalization-plan-request
   :rule-kind :planning
   :mueller-mode :plan-only
   :antecedent-schema [{:goal-type :rationalization
                        :goal-id '?goal-id
                        :context-id '?context-id
                        :trigger-context-id '?trigger-context-id
                        :failed-goal-id '?failed-goal-id
                        :trigger-emotion-id '?trigger-emotion-id
                        :trigger-emotion-strength '?trigger-emotion-strength
                        :frame-id '?frame-id
                        :ordering '?ordering}]
   :consequent-schema [{:fact/type :family-plan-request
                        :goal-type :rationalization
                        :goal-id '?goal-id
                        :context-id '?context-id
                        :trigger-context-id '?trigger-context-id
                        :failed-goal-id '?failed-goal-id
                        :trigger-emotion-id '?trigger-emotion-id
                        :trigger-emotion-strength '?trigger-emotion-strength
                        :frame-id '?frame-id
                        :ordering '?ordering
                        :selection-policy rationalization-plan-policy}]
   :plausibility 1.0
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :emit-rationalization-plan-request
                :failure-modes [:missing-rationalization-frame
                                :missing-planning-context]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:requires-frame? true
                     :requires-context? true}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-rationalization]
                :kernel-status :partial
                :notes "Graphable request fact for rationalization branch planning."}})

(def ^:private rationalization-plan-dispatch-rule
  {:id :goal-family/rationalization-plan-dispatch
   :rule-kind :planning
   :mueller-mode :plan-only
   :antecedent-schema [{:fact/type :family-plan-request
                        :goal-type :rationalization
                        :goal-id '?goal-id
                        :context-id '?context-id
                        :trigger-context-id '?trigger-context-id
                        :failed-goal-id '?failed-goal-id
                        :trigger-emotion-id '?trigger-emotion-id
                        :trigger-emotion-strength '?trigger-emotion-strength
                        :frame-id '?frame-id
                        :ordering '?ordering
                        :selection-policy '?selection-policy}]
   :consequent-schema [{:goal-id '?goal-id
                        :context-id '?context-id
                        :trigger-context-id '?trigger-context-id
                        :failed-goal-id '?failed-goal-id
                        :trigger-emotion-id '?trigger-emotion-id
                        :trigger-emotion-strength '?trigger-emotion-strength
                        :frame-id '?frame-id
                        :ordering '?ordering
                        :selection-policy '?selection-policy}
                       {:fact/type :family-affect-state
                        :source-family :rationalization
                        :goal-id '?goal-id
                        :context-id '?context-id
                        :failed-goal-id '?failed-goal-id
                        :trigger-emotion-id '?trigger-emotion-id
                        :trigger-emotion-strength '?trigger-emotion-strength
                        :frame-id '?frame-id
                        :affect :hope
                        :transition :reappraised}]
   :plausibility 1.0
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :dispatch-rationalization-plan-request
                :failure-modes [:missing-family-plan-request]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:request-goal-type :rationalization}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-rationalization]
                :kernel-status :partial
                :notes "Consumes a rationalization plan request into the current bounded plan payload."}})

(def ^:private rationalization-afterglow-to-roving-rule
  {:id :goal-family/rationalization-afterglow-to-roving
   :rule-kind :inference
   :mueller-mode :both
   :antecedent-schema [{:fact/type :family-affect-state
                        :source-family :rationalization
                        :goal-id '?goal-id
                        :context-id '?context-id
                        :failed-goal-id '?failed-goal-id
                        :trigger-emotion-id '?trigger-emotion-id
                        :trigger-emotion-strength '?trigger-emotion-strength
                        :frame-id '?frame-id
                        :affect :hope
                        :transition :reappraised}]
   :consequent-schema [{:fact/type :goal-family-trigger
                        :goal-type :roving
                        :trigger-context-id '?context-id
                        :failed-goal-id '?failed-goal-id
                        :emotion-id '?trigger-emotion-id
                        :emotion-strength '?trigger-emotion-strength
                        :selection-policy :rationalization_afterglow
                        :selection-reasons [:rationalization_afterglow
                                            :hope_reframe]}]
   :plausibility roving-emotion-threshold
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :emit-roving-trigger-from-rationalization-afterglow
                :failure-modes [:missing-rationalization-afterglow]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:source-family :rationalization
                     :target-goal-type :roving}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-rationalization
                               :theme-roving]
                :kernel-status :proposed
                :notes "Cross-family bridge from rationalization's hopeful afterglow into a roving trigger."}})

(def ^:private reversal-plan-request-rule
  {:id :goal-family/reversal-plan-request
   :rule-kind :planning
   :mueller-mode :plan-only
   :antecedent-schema [{:goal-type :reversal
                        :old-context-id '?old-context-id
                        :old-top-level-goal-id '?old-top-level-goal-id
                        :new-context-id '?new-context-id
                        :new-top-level-goal-id '?new-top-level-goal-id}]
   :consequent-schema [{:fact/type :family-plan-request
                        :goal-type :reversal
                        :old-context-id '?old-context-id
                        :old-top-level-goal-id '?old-top-level-goal-id
                        :new-context-id '?new-context-id
                        :new-top-level-goal-id '?new-top-level-goal-id
                        :selection-policy reverse-leaf-policy}]
   :plausibility 1.0
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :emit-reversal-plan-request
                :failure-modes [:missing-old-context
                                :missing-new-context]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:requires-old-context? true
                     :requires-new-context? true}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-reversal]
                :kernel-status :partial
                :notes "Graphable request fact for reversal branch planning."}})

(def ^:private reversal-plan-dispatch-rule
  {:id :goal-family/reversal-plan-dispatch
   :rule-kind :planning
   :mueller-mode :plan-only
   :antecedent-schema [{:fact/type :family-plan-request
                        :goal-type :reversal
                        :old-context-id '?old-context-id
                        :old-top-level-goal-id '?old-top-level-goal-id
                        :new-context-id '?new-context-id
                        :new-top-level-goal-id '?new-top-level-goal-id
                        :selection-policy '?selection-policy}]
   :consequent-schema [{:old-context-id '?old-context-id
                        :old-top-level-goal-id '?old-top-level-goal-id
                        :new-context-id '?new-context-id
                        :new-top-level-goal-id '?new-top-level-goal-id
                        :selection-policy '?selection-policy}]
   :plausibility 1.0
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :dispatch-reversal-plan-request
                :failure-modes [:missing-family-plan-request]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:request-goal-type :reversal}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-reversal]
                :kernel-status :partial
                :notes "Consumes a reversal plan request into the current bounded plan payload."}})

(def ^:private roving-trigger-rule
  {:id :goal-family/roving-trigger
   :rule-kind :inference
   :mueller-mode :inference-only
   :antecedent-schema [{:fact/type :goal
                        :goal-id '?failed-goal-id
                        :top-level-goal '?failed-goal-id
                        :status :failed
                        :activation-context '?context-id}
                       {:fact/type :emotion
                        :emotion-id '?emotion-id
                        :strength '?emotion-strength}
                       {:fact/type :dependency
                        :from-id '?emotion-id
                        :to-id '?failed-goal-id}]
   :consequent-schema [{:fact/type :goal-family-trigger
                        :goal-type :roving
                        :trigger-context-id '?context-id
                        :failed-goal-id '?failed-goal-id
                        :emotion-id '?emotion-id
                        :emotion-strength '?emotion-strength
                        :selection-policy :failed_goal_negative_emotion
                        :selection-reasons [:failed_goal
                                            :negative_emotion
                                            :dependency_link]}]
   :plausibility roving-emotion-threshold
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :emit-roving-goal-family-trigger
                :failure-modes [:emotion-below-threshold
                                :rationalization-frame-available
                                :missing-dependency-link]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:emotion-threshold roving-emotion-threshold
                     :requires-negative-emotion? true
                     :requires-frame-absence? true}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-roving]
                :kernel-status :partial
                :notes "First graphable trigger slice from goal_families activation logic."}})

(def ^:private roving-activation-rule
  {:id :goal-family/roving-activation
   :rule-kind :inference
   :mueller-mode :inference-only
   :antecedent-schema [{:fact/type :goal-family-trigger
                        :goal-type :roving
                        :trigger-context-id '?context-id
                        :failed-goal-id '?failed-goal-id
                        :emotion-id '?emotion-id
                        :emotion-strength '?emotion-strength
                        :selection-policy '?selection-policy
                        :selection-reasons '?selection-reasons}]
   :consequent-schema [{:context-id '?context-id
                        :failed-goal-id '?failed-goal-id
                        :emotion-id '?emotion-id
                        :emotion-strength '?emotion-strength
                        :selection-policy '?selection-policy
                        :selection-reasons '?selection-reasons}]
   :plausibility roving-emotion-threshold
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :activate-roving-daydream-goal
                :failure-modes [:missing-goal-family-trigger]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:trigger-goal-type :roving}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-roving]
                :kernel-status :partial
                :notes "Consumes the structural roving trigger into an activation payload."}})

(def ^:private rationalization-trigger-rule
  {:id :goal-family/rationalization-trigger
   :rule-kind :inference
   :mueller-mode :inference-only
   :antecedent-schema [{:fact/type :goal
                        :goal-id '?failed-goal-id
                        :top-level-goal '?failed-goal-id
                        :status :failed
                        :activation-context '?context-id}
                       {:fact/type :emotion
                        :emotion-id '?emotion-id
                        :strength '?emotion-strength}
                       {:fact/type :dependency
                        :from-id '?emotion-id
                        :to-id '?failed-goal-id}
                       {:fact/type :rationalization-frame
                        :fact/id '?frame-id
                        :goal-id '?failed-goal-id}]
   :consequent-schema [{:fact/type :goal-family-trigger
                        :goal-type :rationalization
                        :trigger-context-id '?context-id
                        :failed-goal-id '?failed-goal-id
                        :emotion-id '?emotion-id
                        :emotion-strength '?emotion-strength
                        :frame-id '?frame-id
                        :frame-priority '?frame-priority
                        :frame-count '?frame-count
                        :situation-id '?situation-id
                        :selection-policy :failed_goal_negative_emotion_rationalization_frame
                        :selection-reasons [:failed_goal
                                            :negative_emotion
                                            :dependency_link
                                            :rationalization_frame]}]
   :plausibility rationalization-emotion-threshold
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :emit-rationalization-goal-family-trigger
                :failure-modes [:emotion-below-threshold
                                :missing-dependency-link
                                :missing-rationalization-frame]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:emotion-threshold rationalization-emotion-threshold
                     :requires-negative-emotion? true
                     :requires-frame? true}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-rationalization]
                :kernel-status :partial
                :notes "Graphable rationalization trigger emitted from activation logic."}})

(def ^:private rationalization-activation-rule
  {:id :goal-family/rationalization-activation
   :rule-kind :inference
   :mueller-mode :inference-only
   :antecedent-schema [{:fact/type :goal-family-trigger
                        :goal-type :rationalization
                        :trigger-context-id '?context-id
                        :failed-goal-id '?failed-goal-id
                        :emotion-id '?emotion-id
                        :emotion-strength '?emotion-strength
                        :frame-id '?frame-id
                        :frame-priority '?frame-priority
                        :frame-count '?frame-count
                        :situation-id '?situation-id
                        :selection-policy '?selection-policy
                        :selection-reasons '?selection-reasons}]
   :consequent-schema [{:context-id '?context-id
                        :failed-goal-id '?failed-goal-id
                        :emotion-id '?emotion-id
                        :emotion-strength '?emotion-strength
                        :frame-id '?frame-id
                        :frame-priority '?frame-priority
                        :frame-count '?frame-count
                        :situation-id '?situation-id
                        :selection-policy '?selection-policy
                        :selection-reasons '?selection-reasons}]
   :plausibility rationalization-emotion-threshold
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :activate-rationalization-daydream-goal
                :failure-modes [:missing-goal-family-trigger]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:trigger-goal-type :rationalization}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-rationalization]
                :kernel-status :partial
                :notes "Consumes the structural rationalization trigger into an activation payload."}})

(def ^:private reversal-trigger-rule
  {:id :goal-family/reversal-trigger
   :rule-kind :inference
   :mueller-mode :inference-only
   :antecedent-schema [{:fact/type :goal
                        :goal-id '?failed-goal-id
                        :top-level-goal '?old-top-level-goal-id
                        :status :failed
                        :activation-context '?old-context-id}
                       {:fact/type :emotion
                        :emotion-id '?emotion-id
                        :strength '?emotion-strength}]
   :consequent-schema [{:fact/type :goal-family-trigger
                        :goal-type :reversal
                        :old-context-id '?old-context-id
                        :old-top-level-goal-id '?old-top-level-goal-id
                        :failed-goal-id '?failed-goal-id
                        :context-depth '?context-depth
                        :emotion-pressure '?emotion-pressure
                        :failure-count '?failure-count
                        :selection-policy '?selection-policy
                        :selection-reasons '?selection-reasons
                        :emotion-id '?emotion-id
                        :emotion-strength '?emotion-strength
                        :situation-id '?situation-id}]
   :plausibility reversal-emotion-threshold
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :emit-reversal-goal-family-trigger
                :failure-modes [:emotion-below-threshold
                                :missing-negative-emotion
                                :missing-failed-leaf]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:emotion-threshold reversal-emotion-threshold
                     :requires-negative-emotion? true
                     :requires-failed-leaf? true}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-reversal]
                :kernel-status :partial
                :notes "Graphable reversal trigger emitted from failed-leaf selection."}})

(def ^:private reversal-activation-rule
  {:id :goal-family/reversal-activation
   :rule-kind :inference
   :mueller-mode :inference-only
   :antecedent-schema [{:fact/type :goal-family-trigger
                        :goal-type :reversal
                        :old-context-id '?old-context-id
                        :old-top-level-goal-id '?old-top-level-goal-id
                        :failed-goal-id '?failed-goal-id
                        :context-depth '?context-depth
                        :emotion-pressure '?emotion-pressure
                        :failure-count '?failure-count
                        :selection-policy '?selection-policy
                        :selection-reasons '?selection-reasons
                        :emotion-id '?emotion-id
                        :emotion-strength '?emotion-strength
                        :situation-id '?situation-id}]
   :consequent-schema [{:old-context-id '?old-context-id
                        :old-top-level-goal-id '?old-top-level-goal-id
                        :failed-goal-id '?failed-goal-id
                        :context-depth '?context-depth
                        :emotion-pressure '?emotion-pressure
                        :failure-count '?failure-count
                        :selection-policy '?selection-policy
                        :selection-reasons '?selection-reasons
                        :emotion-id '?emotion-id
                        :emotion-strength '?emotion-strength
                        :activation-policy :failed_goal_negative_emotion
                        :activation-reasons [:failed_goal
                                             :negative_emotion
                                             :reversal_candidate]
                        :situation-id '?situation-id}]
   :plausibility reversal-emotion-threshold
   :index-projections {:match []
                       :emit []}
   :denotation {:intended-effect :activate-reversal-daydream-goal
                :failure-modes [:emotion-below-threshold
                                :missing-negative-emotion
                                :missing-failed-leaf]
                :validation-fn nil}
   :executor {:kind :instantiate
              :spec {:emotion-threshold reversal-emotion-threshold
                     :requires-negative-emotion? true
                     :requires-failed-leaf? true}}
   :graph-cache {:out-edge-bases []
                 :in-edge-bases []}
   :provenance {:book-anchors [:theme-reversal]
                :kernel-status :partial
                :notes "Third extracted RuleV1 slice from goal_families activation logic."}})

(defn activation-rules
  "Return the currently extracted RuleV1 activation slices.

  This is the first honest kernel-facing rule registry: a small subset of
  family activation logic expressed as structural rules rather than hidden
  procedural scans."
  []
  [roving-trigger-rule
   roving-activation-rule
   rationalization-trigger-rule
   rationalization-activation-rule
   reversal-trigger-rule
   reversal-activation-rule])

(defn planning-rules
  "Return the currently extracted RuleV1 planning slices."
  []
  [roving-plan-request-rule
   roving-plan-dispatch-rule
   rationalization-plan-request-rule
   rationalization-plan-dispatch-rule
   reversal-plan-request-rule
   reversal-plan-dispatch-rule])

(defn cross-family-rules
  "Return the currently extracted cross-family handoff rules."
  []
  [rationalization-afterglow-to-roving-rule])

(defn family-rules
  "Return the current combined family rule registry."
  []
  (vec (concat (planning-rules)
               (cross-family-rules)
               (activation-rules))))

(defn- seed-rule-provenance
  [rule]
  {:rule-path [(:id rule)]
   :edge-path []})

(defn- extend-rule-provenance
  [producer-provenance consumer-rule fact-type]
  (let [producer-path (vec (or (:rule-path producer-provenance) []))
        edge-path (vec (or (:edge-path producer-provenance) []))
        from-rule (last producer-path)]
    (cond-> {:rule-path (->> (conj producer-path (:id consumer-rule))
                             distinct
                             vec)
             :edge-path edge-path}
      from-rule
      (update :edge-path conj {:from-rule from-rule
                               :to-rule (:id consumer-rule)
                               :fact-type fact-type
                               :edge-kind :state-transition}))))

(defn- emit-rule-fact
  [rule bindings]
  (assoc (-> (rules/instantiate-rule rule bindings)
             :consequents
             first)
         :rule-provenance (seed-rule-provenance rule)))

(declare reversal-sprout-alternative
         reversal-trigger-facts
         retract-facts
         instantiate-rationalization-trigger)

(defn- fact-consumer-rule?
  [fact-type rule]
  (= fact-type
     (get-in rule [:antecedent-schema 0 :fact/type])))

(defn- activation-candidates-from-trigger-facts
  [trigger-facts]
  (->> trigger-facts
       (mapcat (fn [trigger-fact]
                 (->> (activation-rules)
                      (filter (partial fact-consumer-rule? :goal-family-trigger))
                      (keep (fn [rule]
                              (when-let [match (first (rules/match-rule rule
                                                                        [trigger-fact]))]
                                (assoc (-> (rules/instantiate-rule rule
                                                                   (:bindings match))
                                           :consequents
                                           first)
                                       :goal-type (:goal-type trigger-fact)
                                       :rule-provenance
                                       (extend-rule-provenance
                                        (:rule-provenance trigger-fact)
                                        rule
                                        :goal-family-trigger))))))))
       (sort-by (juxt (fn [candidate]
                        (- (double (or (:emotion-strength candidate) 0.0))))
                      (fn [candidate]
                        (- (double (or (:frame-priority candidate) 0.0))))
                      (fn [candidate]
                        (- (double (or (:frame-count candidate) 0.0))))
                      (comp str :goal-type)
                      (comp str :failed-goal-id)
                      (comp str :emotion-id)))
       vec))

(defn- activation-candidates-for-goal-type
  [goal-type trigger-facts]
  (->> (activation-candidates-from-trigger-facts trigger-facts)
       (filter #(= goal-type (:goal-type %)))
       (mapv #(dissoc % :goal-type))))

(defn- plan-payloads-from-request-facts
  [request-facts]
  (->> request-facts
       (mapcat (fn [request-fact]
                 (->> (planning-rules)
                      (filter (partial fact-consumer-rule? :family-plan-request))
                      (keep (fn [rule]
                              (when-let [match (first (rules/match-rule rule
                                                                        [request-fact]))]
                                (assoc (-> (rules/instantiate-rule rule
                                                                   (:bindings match))
                                           :consequents
                                           first)
                                       :goal-type (:goal-type request-fact)
                                       :rule-provenance
                                       (extend-rule-provenance
                                        (:rule-provenance request-fact)
                                        rule
                                        :family-plan-request))))))))
       (sort-by (juxt (comp str :goal-type)
                      (comp str :goal-id)
                      (comp str :context-id)
                      (comp str :episode-id)))
       vec))

(defn- top-level-owner
  [fact]
  (or (:top-level-goal fact)
      (when (goal-fact? fact)
        (fact-id fact))))

(defn- goal-status
  [fact]
  (:status fact))

(defn- local-failed-goal-facts
  [world context-id]
  (->> (get-in world [:contexts context-id :add-obs] #{})
       (filter goal-fact?)
       (filter #(= :failed (goal-status %)))
       (sort-by pr-str)
       vec))

(defn- emotion-pressure
  [world context-id]
  (->> (cx/visible-facts world context-id)
       (filter emotion-fact?)
       (map #(double (or (:strength %) 0.0)))
       (reduce max 0.0)))

(defn- leaf-context?
  [world context-id]
  (empty? (cx/children world context-id)))

(defn- primary-situation-id
  [world context-id]
  (->> (cx/visible-facts world context-id)
       (filter #(fact-type? % :situation))
       (map fact-id)
       (sort-by str)
       first))

(defn- clamp-strength
  [strength]
  (-> (double (or strength 0.0))
      (max 0.0)
      (min 1.0)))

(defn- linked-emotion-facts
  [world context-id failed-goal-id predicate]
  (let [facts (cx/visible-facts world context-id)
        dependencies (filter dependency-fact? facts)]
    (->> facts
         (filter predicate)
         (filter (fn [emotion-fact]
                   (some (fn [dependency-fact]
                           (let [ref-ids (fact-ref-ids dependency-fact)]
                             (and (contains? ref-ids failed-goal-id)
                                  (contains? ref-ids (fact-id emotion-fact)))))
                         dependencies)))
         (sort-by (juxt (comp - #(double (or % 0.0)) :strength)
                        (comp str fact-id)))
         vec)))

(defn- select-trigger-emotion
  [world trigger-context-id failed-goal-id]
  (first (linked-emotion-facts world
                               trigger-context-id
                               failed-goal-id
                               negative-emotion-fact?)))

(defn- hope-emotion-id
  [frame-id]
  (keyword (str (name frame-id) "-hope")))

(defn- emotion-state-entry
  [fact role context-id]
  {:emotion-id (fact-id fact)
   :strength (double (or (:strength fact) 0.0))
   :valence (or (:valence fact)
                (when (positive-emotion-fact? fact) :positive)
                (when (negative-emotion-fact? fact) :negative))
   :affect (:affect fact)
   :situation-id (:situation-id fact)
   :context-id context-id
   :role role})

(defn- emotion-shift-entry
  [{:keys [emotion-id from-strength to-strength valence affect
           situation-id context-id role]}]
  {:emotion-id emotion-id
   :from-strength (double (or from-strength 0.0))
   :to-strength (double (or to-strength 0.0))
   :delta (- (double (or to-strength 0.0))
             (double (or from-strength 0.0)))
   :valence valence
   :affect affect
   :situation-id situation-id
   :context-id context-id
   :role role})

(defn- replace-emotion-fact
  [world context-id emotion-id new-fact]
  (let [existing-facts (->> (cx/visible-facts world context-id)
                            (filter emotion-fact?)
                            (filter #(= emotion-id (fact-id %)))
                            (sort-by pr-str)
                            vec)
        world (retract-facts world context-id existing-facts)]
    (cx/assert-fact world context-id new-fact)))

(defn reversal-leaf-candidates
  "Find candidate failure leaf contexts for REVERSAL.

  Candidate discovery is intentionally conservative for this first pass:
  - the context must be a leaf in the planning tree
  - it must locally assert at least one failed goal fact

  Ranking is provisional until realism metadata exists. For now, candidates are
  ordered by strongest visible emotion, then by context depth, then by failure
  count."
  [world]
  (->> (keys (:contexts world))
       (keep (fn [context-id]
               (let [failed-goals (local-failed-goal-facts world context-id)]
                 (when (and (seq failed-goals)
                            (leaf-context? world context-id))
                   (let [primary-goal (first failed-goals)
                         depth (count (cx/ancestors world context-id))
                         pressure (emotion-pressure world context-id)
                         reasons (cond-> [:failed_leaf]
                                   (pos? pressure) (conj :negative_emotion)
                                   (pos? depth) (conj :deep_context)
                                   (> (count failed-goals) 1) (conj :multiple_failures))]
                     {:old-context-id context-id
                      :old-top-level-goal-id (top-level-owner primary-goal)
                      :failed-goal-id (fact-id primary-goal)
                      :context-depth depth
                      :emotion-pressure pressure
                      :failure-count (count failed-goals)
                      :selection-reasons reasons
                      :selection-policy reversal-leaf-policy})))))
       (sort-by (juxt (comp - :emotion-pressure)
                      (comp - :context-depth)
                      (comp - :failure-count)
                      (comp str :old-context-id)))
       vec))

(defn select-reversal-leaf
  "Choose the strongest candidate failure leaf for REVERSAL."
  [world]
  (first (reversal-leaf-candidates world)))

(defn reversal-activation-candidates
  "Find failed-goal / negative-emotion pairs strong enough to activate
  REVERSAL."
  [world]
  (activation-candidates-for-goal-type :reversal
                                       (reversal-trigger-facts world)))

(defn- reversal-trigger-facts
  [world]
  (->> (reversal-leaf-candidates world)
       (keep (fn [candidate]
               (let [facts (cx/visible-facts world (:old-context-id candidate))]
                 (->> (rules/match-rule reversal-trigger-rule
                                        facts
                                        {'?old-context-id (:old-context-id candidate)
                                         '?old-top-level-goal-id (:old-top-level-goal-id candidate)
                                         '?failed-goal-id (:failed-goal-id candidate)})
                      (keep (fn [{:keys [bindings matched-facts]}]
                              (let [emotion-fact (second matched-facts)
                                    emotion-strength (double
                                                      (or ('?emotion-strength bindings)
                                                          0.0))]
                                (when (and (negative-emotion-fact? emotion-fact)
                                           (> emotion-strength
                                              reversal-emotion-threshold))
                                  (emit-rule-fact
                                   reversal-trigger-rule
                                   {'?old-context-id (:old-context-id candidate)
                                    '?old-top-level-goal-id (:old-top-level-goal-id candidate)
                                    '?failed-goal-id (:failed-goal-id candidate)
                                    '?context-depth (:context-depth candidate)
                                    '?emotion-pressure (:emotion-pressure candidate)
                                    '?failure-count (:failure-count candidate)
                                    '?selection-policy (:selection-policy candidate)
                                    '?selection-reasons (:selection-reasons candidate)
                                    '?emotion-id ('?emotion-id bindings)
                                    '?emotion-strength emotion-strength
                                    '?situation-id (primary-situation-id
                                                    world
                                                    (:old-context-id candidate))})))))
                      (sort-by (juxt (comp - :emotion-strength)
                                     (comp str :emotion-id)))
                      first))))
       (sort-by (juxt (comp - :emotion-strength)
                      (comp str :old-context-id)
                      (comp str :failed-goal-id)))
       vec))

(defn- goal-strength
  [world goal-fact]
  (double (or (:strength goal-fact)
              (get-in world [:goals (fact-id goal-fact) :strength])
              1.0)))

(defn- goal-objective-facts
  [goal-fact]
  (vec (or (:objective-facts goal-fact)
           (when-let [objective-fact (:objective-fact goal-fact)]
             [objective-fact])
           [])))

(defn- plan-goal-facts
  [world context-id top-level-goal-id]
  (->> (cx/visible-facts world context-id)
       (filter goal-fact?)
       (filter #(= top-level-goal-id (top-level-owner %)))
       (sort-by (comp str fact-id))
       vec))

(defn- plan-intends-facts
  [world context-id top-level-goal-id]
  (->> (cx/visible-facts world context-id)
       (filter intends-fact?)
       (filter #(= top-level-goal-id (top-level-owner %)))
       (sort-by pr-str)
       vec))

(defn- reachable-goal-ids
  [children-by-goal root-goal-id]
  (loop [frontier (conj clojure.lang.PersistentQueue/EMPTY root-goal-id)
         seen #{}]
    (if-let [goal-id (peek frontier)]
      (let [frontier (pop frontier)]
        (if (contains? seen goal-id)
          (recur frontier seen)
          (recur (into frontier (get children-by-goal goal-id []))
                 (conj seen goal-id))))
      seen)))

(defn reverse-leaf-branches
  "Find weak planning leafs reachable from the selected failed top-level goal.

  This corrects the earlier shortcut of scanning the entire context tree. The
  search is scoped to the selected failed goal's `:intends` structure, and it
  returns one candidate per weak leaf, ordered by inverse strength."
  [world {:keys [old-context-id old-top-level-goal-id]}]
  (let [goal-facts (plan-goal-facts world old-context-id old-top-level-goal-id)
        goal-facts-by-id (into {} (map (juxt fact-id identity)) goal-facts)
        children-by-goal (reduce (fn [acc {:keys [from-goal-id to-goal-id]}]
                                   (update acc from-goal-id (fnil conj []) to-goal-id))
                                 {}
                                 (plan-intends-facts world
                                                     old-context-id
                                                     old-top-level-goal-id))
        reachable-ids (reachable-goal-ids children-by-goal old-top-level-goal-id)]
    (->> reachable-ids
         (keep goal-facts-by-id)
         (filter (fn [goal-fact]
                   (empty? (get children-by-goal (fact-id goal-fact) []))))
         (keep (fn [goal-fact]
                 (let [strength (goal-strength world goal-fact)]
                   (when (< strength reverse-leaf-threshold)
                     {:old-context-id (or (:activation-context goal-fact)
                                          old-context-id)
                      :old-top-level-goal-id old-top-level-goal-id
                      :leaf-goal-id (fact-id goal-fact)
                      :leaf-strength strength
                      :ordering (/ 1.0 (max strength 1.0e-9))
                      :objective-facts (goal-objective-facts goal-fact)
                      :selection-policy reverse-leaf-policy
                      :selection-reasons (cond-> [:intends_leaf
                                                  :weak_assumption]
                                           (seq (goal-objective-facts goal-fact))
                                           (conj :objective_retraction))}))))
         (sort-by (juxt (comp - :ordering)
                        (comp str :leaf-goal-id)))
         vec)))

(defn- selected-goal-ids
  [{:keys [failed-goal-id old-top-level-goal-id]}]
  (->> [failed-goal-id old-top-level-goal-id]
       (remove nil?)
       set))

(defn- failure-cause-matches-leaf?
  [fact reversal-leaf]
  (let [goal-ids (selected-goal-ids reversal-leaf)
        fact-goal-ids (->> [(:goal-id fact)
                            (:failed-goal-id fact)
                            (:top-level-goal fact)]
                           (remove nil?)
                           set)]
    (seq (set/intersection goal-ids fact-goal-ids))))

(defn reverse-undo-cause-candidates
  "Find stored causal explanations for a selected failed leaf.

  This first pass is intentionally narrow: it does not infer new causes.
  Instead it looks for explicit `:failure-cause` facts visible from the failed
  leaf and ranks them by stored priority, then by how many counterfactual facts
  they provide."
  [world {:keys [old-context-id] :as reversal-leaf}]
  (->> (cx/visible-facts world old-context-id)
       (filter failure-cause-fact?)
       (filter #(failure-cause-matches-leaf? % reversal-leaf))
       (keep (fn [fact]
               (let [counterfactual-facts (vec (:counterfactual-facts fact []))]
                 (when (seq counterfactual-facts)
                   {:cause-id (fact-id fact)
                    :goal-id (or (:failed-goal-id fact)
                                 (:goal-id fact)
                                 (:top-level-goal fact))
                    :priority (double (or (:priority fact) 0.0))
                    :counterfactual-count (count counterfactual-facts)
                    :counterfactual-facts counterfactual-facts
                    :selection-policy reversal-cause-policy
                    :selection-reasons (cond-> [:stored_failure_cause
                                                :matching_failed_goal]
                                         (> (count counterfactual-facts) 1)
                                         (conj :multi_fact_counterfactual))}))))
       (sort-by (juxt (comp - :priority)
                      (comp - :counterfactual-count)
                      (comp str :cause-id)))
       vec))

(defn reverse-undo-causes
  "Choose stored counterfactual input facts for a selected failed leaf.

  Returns nil when the kernel has no explicit causal explanation for the leaf.
  The runner decides whether that absence is acceptable for a given benchmark."
  [world reversal-leaf]
  (when-let [cause (first (reverse-undo-cause-candidates world reversal-leaf))]
    {:input-facts (:counterfactual-facts cause)
     :counterfactual-fact-ids (mapv fact-id (:counterfactual-facts cause))
     :counterfactual-cause-id (:cause-id cause)
     :counterfactual-goal-id (:goal-id cause)
     :counterfactual-policy (:selection-policy cause)
     :counterfactual-reasons (:selection-reasons cause)}))

(defn- matching-rationalization-frame?
  [fact failed-goal-id]
  (let [goal-ids (->> [failed-goal-id] (remove nil?) set)
        fact-goal-ids (->> [(:goal-id fact)
                            (:failed-goal-id fact)
                            (:top-level-goal fact)]
                           (remove nil?)
                           set)]
    (seq (set/intersection goal-ids fact-goal-ids))))

(defn- rationalization-frame-situation-id
  [reframe-facts]
  (->> reframe-facts
       (filter #(fact-type? % :situation))
       (map fact-id)
       (sort-by str)
       first))

(defn- rationalization-frame-goal-id
  [fact]
  (or (:goal-id fact)
      (:failed-goal-id fact)
      (:top-level-goal fact)))

(defn- rationalization-frame-counts
  [facts]
  (->> facts
       (keep (fn [fact]
               (let [reframe-facts (vec (:reframe-facts fact []))
                     goal-id (rationalization-frame-goal-id fact)]
                 (when (and (rationalization-frame-fact? fact)
                            goal-id
                            (seq reframe-facts))
                   goal-id))))
       frequencies))

(defn rationalization-frame-candidates
  "Find explicit rationalization frames for a failed goal.

  This is an honest bridge for the missing rule engine: instead of inferring
  LEADTO/minimization chains, the kernel looks for stored reinterpretation
  frames that can be asserted into a rationalization branch."
  [world {:keys [trigger-context-id failed-goal-id]}]
  (->> (cx/visible-facts world trigger-context-id)
       (filter rationalization-frame-fact?)
       (filter #(matching-rationalization-frame? % failed-goal-id))
       (keep (fn [fact]
               (let [reframe-facts (vec (:reframe-facts fact []))]
                 (when (seq reframe-facts)
                   {:frame-id (fact-id fact)
                    :goal-id (or (:failed-goal-id fact)
                                 (:goal-id fact)
                                 (:top-level-goal fact))
                    :priority (double (or (:priority fact) 0.0))
                    :reframe-fact-count (count reframe-facts)
                    :reframe-facts reframe-facts
                    :situation-id (or (:situation-id fact)
                                      (rationalization-frame-situation-id reframe-facts))
                    :selection-policy rationalization-frame-policy
                    :selection-reasons (cond-> [:stored_rationalization_frame
                                                :matching_failed_goal]
                                         (> (count reframe-facts) 1)
                                         (conj :multi_fact_reframe))}))))
       (sort-by (juxt (comp - :priority)
                      (comp - :reframe-fact-count)
                      (comp str :frame-id)))
       vec))

(defn- rationalization-trigger-facts
  [world]
  (->> (keys (:contexts world))
       (mapcat (fn [context-id]
                 (let [facts (cx/visible-facts world context-id)
                       frame-counts (rationalization-frame-counts facts)]
                   (->> (rules/match-rule rationalization-trigger-rule
                                          facts
                                          {'?context-id context-id})
                        (keep (fn [{:keys [bindings matched-facts]}]
                                (let [emotion-fact (second matched-facts)
                                      frame-fact (nth matched-facts 3)
                                      emotion-strength (double
                                                        (or ('?emotion-strength bindings)
                                                            0.0))]
                                  (when (and (negative-emotion-fact? emotion-fact)
                                             (> emotion-strength
                                                rationalization-emotion-threshold)
                                             (seq (:reframe-facts frame-fact)))
                                    (instantiate-rationalization-trigger
                                     context-id
                                     ('?failed-goal-id bindings)
                                     ('?emotion-id bindings)
                                     emotion-strength
                                     ('?frame-id bindings)
                                     (double (or (:priority frame-fact)
                                                 0.0))
                                     (get frame-counts
                                          (rationalization-frame-goal-id
                                           frame-fact)
                                          0)
                                     (or (:situation-id frame-fact)
                                         (rationalization-frame-situation-id
                                          (:reframe-facts frame-fact))
                                         (primary-situation-id
                                          world
                                          context-id)))))))
                        vec))))
       (sort-by (juxt (comp - :emotion-strength)
                      (comp - :frame-priority)
                      (comp - :frame-count)
                      (comp str :context-id)
                      (comp str :failed-goal-id)))
       vec))

(defn rationalization-activation-candidates
  "Find failed-goal / negative-emotion pairs with an explicit rationalization
  frame strong enough to activate RATIONALIZATION."
  [world]
  (activation-candidates-for-goal-type :rationalization
                                       (rationalization-trigger-facts world)))

(defn select-rationalization-trigger
  "Choose the strongest failed-goal / negative-emotion trigger for
  RATIONALIZATION."
  [world]
  (first (rationalization-activation-candidates world)))

(defn- instantiate-rationalization-trigger
  [context-id
   failed-goal-id
   emotion-id
   emotion-strength
   frame-id
   frame-priority
   frame-count
   situation-id]
  (emit-rule-fact rationalization-trigger-rule
                  {'?context-id context-id
                   '?failed-goal-id failed-goal-id
                   '?emotion-id emotion-id
                   '?emotion-strength emotion-strength
                   '?frame-id frame-id
                   '?frame-priority frame-priority
                   '?frame-count frame-count
                   '?situation-id situation-id}))

(defn- instantiate-roving-trigger
  [context-id failed-goal-id emotion-id emotion-strength]
  (emit-rule-fact roving-trigger-rule
                  {'?context-id context-id
                   '?failed-goal-id failed-goal-id
                   '?emotion-id emotion-id
                   '?emotion-strength emotion-strength}))

(defn- instantiate-cross-family-trigger
  [producer-provenance rule bindings]
  (assoc (-> (rules/instantiate-rule rule bindings)
             :consequents
             first)
         :rule-provenance (extend-rule-provenance producer-provenance
                                                  rule
                                                  :family-affect-state)))

(defn- roving-trigger-facts
  [world]
  (->> (keys (:contexts world))
       (mapcat (fn [context-id]
                 (let [facts (cx/visible-facts world context-id)
                       frame-counts (rationalization-frame-counts facts)]
                   (->> (rules/match-rule roving-trigger-rule
                                          facts
                                          {'?context-id context-id})
                        (keep (fn [{:keys [bindings matched-facts]}]
                                (let [emotion-fact (second matched-facts)
                                      emotion-strength (double
                                                        (or ('?emotion-strength bindings)
                                                            0.0))
                                      failed-goal-id ('?failed-goal-id bindings)]
                                  (when (and (negative-emotion-fact? emotion-fact)
                                             (> emotion-strength
                                                roving-emotion-threshold)
                                             (zero? (get frame-counts failed-goal-id 0)))
                                    (instantiate-roving-trigger
                                     context-id
                                     failed-goal-id
                                     ('?emotion-id bindings)
                                     emotion-strength)))))
                        vec))))
       (sort-by (juxt (comp - :emotion-strength)
                      (comp str :context-id)
                      (comp str :failed-goal-id)
                      (comp str :emotion-id)))
       vec))

(defn- cross-family-trigger-facts
  [world]
  (->> (keys (:contexts world))
       (mapcat (fn [context-id]
                 (let [facts (cx/visible-facts world context-id)]
                   (->> (cross-family-rules)
                        (mapcat (fn [rule]
                                  (->> (rules/match-rule rule facts)
                                       (keep (fn [{:keys [bindings matched-facts]}]
                                               (let [producer-fact (first matched-facts)]
                                                 (when-let [producer-provenance (:rule-provenance producer-fact)]
                                                   (instantiate-cross-family-trigger
                                                    producer-provenance
                                                    rule
                                                    bindings))))))))
                        vec))))
       (sort-by (juxt (comp - :emotion-strength)
                      (comp str :trigger-context-id)
                      (comp str :failed-goal-id)
                      (comp str :emotion-id)
                      (comp str :selection-policy)))
       vec))

(defn roving-activation-candidates
  "Find failed-goal / negative-emotion pairs that can activate ROVING."
  [world]
  (activation-candidates-for-goal-type :roving
                                       (roving-trigger-facts world)))

(defn select-roving-trigger
  "Choose the strongest failed-goal / negative-emotion trigger for ROVING."
  [world]
  (first (roving-activation-candidates world)))

(defn- existing-family-goal?
  [world goal-type trigger-context-id failed-goal-id]
  (some (fn [goal]
          (and (= goal-type (:goal-type goal))
               (not (contains? terminal-goal-statuses (:status goal)))
               (= trigger-context-id (:trigger-context-id goal))
               (= failed-goal-id (:trigger-failed-goal-id goal))))
        (vals (:goals world))))

(defn- goal-family-trigger-facts
  [world]
  (vec (concat (reversal-trigger-facts world)
               (rationalization-trigger-facts world)
               (cross-family-trigger-facts world)
               (roving-trigger-facts world))))

(def ^:private trigger-dispatch-goal-types
  [:reversal :rationalization :roving])

(defn- trigger-dispatch-candidates
  [world]
  (let [candidates-by-type (group-by :goal-type
                                     (activation-candidates-from-trigger-facts
                                      (goal-family-trigger-facts world)))]
    (->> trigger-dispatch-goal-types
         (keep (fn [goal-type]
                 (first (get candidates-by-type goal-type))))
         vec)))

(defn activate-family-goals
  "Infer family-goal activations from the current world state.

  This approximates Mueller's Theme rules as a pure pre-competition pass.
  To keep the kernel deterministic and bounded, activate at most one candidate
  per family per cycle."
  [world]
  (let [activation-context-id (or (:reality-lookahead world)
                                  (:reality-context world))
        candidates (trigger-dispatch-candidates world)]
    (reduce
     (fn [current-world {:keys [goal-type situation-id emotion-id emotion-strength
                                selection-policy selection-reasons
                                activation-policy activation-reasons
                                context-id old-context-id failed-goal-id
                                frame-id rule-provenance]}]
       (let [trigger-context-id (or old-context-id context-id)]
         (if (existing-family-goal? current-world
                                    goal-type
                                    trigger-context-id
                                    failed-goal-id)
           current-world
           (let [[next-world goal-id]
                 (goals/activate-top-level-goal
                  current-world
                  activation-context-id
                  {:goal-type goal-type
                   :planning-type :imaginary
                   :strength emotion-strength
                   :main-motiv emotion-id
                   :situation-id situation-id
                   :trigger-context-id trigger-context-id
                   :trigger-failed-goal-id failed-goal-id
                   :trigger-emotion-id emotion-id
                   :trigger-emotion-strength emotion-strength
                   :trigger-frame-id frame-id
                   :activation-policy (or activation-policy selection-policy)
                   :activation-reasons (or activation-reasons selection-reasons)})]
             (update next-world :activation-events
                     (fnil conj [])
                     {:goal-id goal-id
                      :goal-type goal-type
                      :trigger-context-id trigger-context-id
                      :failed-goal-id failed-goal-id
                      :emotion-id emotion-id
                      :emotion-strength emotion-strength
                      :frame-id frame-id
                      :activation-policy (or activation-policy selection-policy)
                      :activation-reasons (or activation-reasons selection-reasons)
                      :rule-provenance rule-provenance})))))
     world
     candidates)))

(defn- choose-roving-episode-id
  [world {:keys [episode-id episode-ids]}]
  (cond
    episode-id
    episode-id

    (seq episode-ids)
    (first episode-ids)

    (seq (:roving-episodes world))
    (first (:roving-episodes world))

    :else
    nil))

(defn- instantiate-roving-plan-request
  [goal-id context-id episode-id ordering]
  (emit-rule-fact roving-plan-request-rule
                  {'?goal-id goal-id
                   '?context-id context-id
                   '?episode-id episode-id
                   '?ordering ordering}))

(defn- roving-plan-request-facts
  [world {:keys [goal-id context-id ordering]
          :or {ordering 1.0}
          :as opts}]
  (when-let [episode-id (choose-roving-episode-id world opts)]
    [(instantiate-roving-plan-request goal-id
                                      context-id
                                      episode-id
                                      ordering)]))

(defn- instantiate-rationalization-plan-request
  [goal-id
   context-id
   trigger-context-id
   failed-goal-id
   trigger-emotion-id
   trigger-emotion-strength
   frame-id
   ordering]
  (emit-rule-fact rationalization-plan-request-rule
                  {'?goal-id goal-id
                   '?context-id context-id
                   '?trigger-context-id trigger-context-id
                   '?failed-goal-id failed-goal-id
                   '?trigger-emotion-id trigger-emotion-id
                   '?trigger-emotion-strength trigger-emotion-strength
                   '?frame-id frame-id
                   '?ordering ordering}))

(defn- rationalization-plan-request-facts
  [world {:keys [goal-id context-id trigger-context-id failed-goal-id frame-id ordering]
          :or {ordering 1.0}}]
  (let [goal (get-in world [:goals goal-id])
        trigger-context-id (or trigger-context-id context-id)
        trigger-emotion-id (or (:trigger-emotion-id goal)
                               (:main-motiv goal))
        trigger-emotion-strength (double (or (:trigger-emotion-strength goal)
                                             (:strength goal)
                                             0.0))
        frame-candidates (rationalization-frame-candidates
                          world
                          {:trigger-context-id trigger-context-id
                           :failed-goal-id failed-goal-id})
        frame (or (when frame-id
                    (some #(when (= frame-id (:frame-id %)) %) frame-candidates))
                  (first frame-candidates))]
    (when frame
      [(instantiate-rationalization-plan-request goal-id
                                                 context-id
                                                 trigger-context-id
                                                 failed-goal-id
                                                 trigger-emotion-id
                                                 trigger-emotion-strength
                                                 (:frame-id frame)
                                                 ordering)])))

(defn- instantiate-reversal-plan-request
  [old-context-id old-top-level-goal-id new-context-id new-top-level-goal-id]
  (emit-rule-fact reversal-plan-request-rule
                  {'?old-context-id old-context-id
                   '?old-top-level-goal-id old-top-level-goal-id
                   '?new-context-id new-context-id
                   '?new-top-level-goal-id new-top-level-goal-id}))

(defn- reversal-plan-request-facts
  [{:keys [old-context-id old-top-level-goal-id new-context-id new-top-level-goal-id]}]
  (when (and old-context-id old-top-level-goal-id new-context-id new-top-level-goal-id)
    [(instantiate-reversal-plan-request old-context-id
                                        old-top-level-goal-id
                                        new-context-id
                                        new-top-level-goal-id)]))

(defn- default-plan-context
  [world goal-id]
  (or (when goal-id
        (goals/get-next-context world goal-id))
      (:reality-lookahead world)
      (:reality-context world)))

(defn- resolve-reversal-plan-target
  [world goal-id opts]
  (cond
    (:old-context-id opts)
    (assoc opts
           :new-top-level-goal-id (or (:new-top-level-goal-id opts) goal-id)
           :new-context-id (or (:new-context-id opts)
                               (default-plan-context world
                                                     (or (:new-top-level-goal-id opts)
                                                         goal-id))))

    :else
    (when-let [reversal-leaf (select-reversal-leaf world)]
      (merge reversal-leaf
             (reverse-undo-causes world reversal-leaf)
             opts
             {:new-top-level-goal-id (or (:new-top-level-goal-id opts) goal-id)
              :new-context-id (or (:new-context-id opts)
                                  (default-plan-context world
                                                        (or (:new-top-level-goal-id opts)
                                                            goal-id)))
              :ordering (or (:ordering opts) 0.9)}))))

(defn roving-plan
  "Sprout a side-channel context and seed it with a pleasant episode reminder.

  The episode reminding cascade is the heavy lifter here; this plan mostly
  creates a fresh branch, invokes episodic memory, and records the result."
  [world {:keys [goal-id context-id]
          :as opts}]
  (let [plan-request-facts (or (seq (roving-plan-request-facts world opts))
                               (throw (ex-info "ROVING needs a pleasant episode"
                                               {:opts opts})))
        {:keys [episode-id ordering selection-policy rule-provenance]}
        (or (first (plan-payloads-from-request-facts plan-request-facts))
            (throw (ex-info "ROVING needs a plan payload"
                            {:opts opts
                             :plan-request-facts plan-request-facts})))
        connection-graph (rules/build-connection-graph (family-rules))
        [world sprouted-context-id] (cx/sprout world context-id)
        success-intends {:fact/type :intends
                         :from-goal-id goal-id
                         :to-goal-id :rtrue
                         :top-level-goal goal-id
                         :status :succeeded
                         :rule :roving-plan1}
        world (cx/assert-fact world sprouted-context-id success-intends)
        [world reminded-episode-ids]
        (episodic/episode-reminding
         world
         episode-id
         {:connection-graph connection-graph
          :active-rule-path (:rule-path rule-provenance)
          :active-edge-path (:edge-path rule-provenance)})
        world (assoc-in world [:contexts sprouted-context-id :ordering] ordering)
        world (if (contains? (:goals world) goal-id)
                (goals/set-next-context world goal-id sprouted-context-id)
                world)
        world (update world :mutation-events
                      (fnil conj [])
                      {:family :roving
                       :source-context context-id
                       :target-context sprouted-context-id
                       :seed-episode-id episode-id
                       :reminded-episode-ids (vec reminded-episode-ids)
                       :active-indices (vec (:recent-indices world))})]
    [world {:sprouted-context-id sprouted-context-id
            :episode-id episode-id
            :reminded-episode-ids (vec reminded-episode-ids)
            :active-indices (vec (:recent-indices world))
            :selection-policy selection-policy
            :rule-provenance rule-provenance}]))

(defn- apply-rationalization-emotional-diversion
  [world {:keys [goal-id trigger-context-id failed-goal-id sprouted-context-id]
          :as plan-state}
   frame]
  (if-let [trigger-emotion (select-trigger-emotion world
                                                   trigger-context-id
                                                   failed-goal-id)]
    (let [trigger-emotion-id (fact-id trigger-emotion)
          trigger-strength-before (double (or (:strength trigger-emotion) 0.0))
          diverted-strength (* trigger-strength-before
                               rationalization-diversion-scale
                               (double (or (:priority frame) 0.0)))
          trigger-strength-after (clamp-strength
                                  (- trigger-strength-before diverted-strength))
          updated-trigger-emotion (assoc trigger-emotion
                                         :strength trigger-strength-after)
          hope-strength (clamp-strength diverted-strength)
          reframe-situation-id (or (:situation-id frame)
                                   (rationalization-frame-situation-id
                                    (:reframe-facts frame)))
          hope-emotion {:fact/type :emotion
                        :emotion-id (hope-emotion-id (:frame-id frame))
                        :strength hope-strength
                        :valence :positive
                        :affect :hope
                        :goal-id goal-id
                        :source-emotion-id trigger-emotion-id
                        :frame-id (:frame-id frame)
                        :situation-id reframe-situation-id}
          world (-> world
                    (replace-emotion-fact sprouted-context-id
                                          trigger-emotion-id
                                          updated-trigger-emotion)
                    (cx/assert-fact sprouted-context-id hope-emotion)
                    (assoc-in [:emotions trigger-emotion-id]
                              (dissoc updated-trigger-emotion :fact/type))
                    (assoc-in [:emotions (:emotion-id hope-emotion)]
                              (dissoc hope-emotion :fact/type)))
          emotion-shifts [(emotion-shift-entry
                           {:emotion-id trigger-emotion-id
                            :from-strength trigger-strength-before
                            :to-strength trigger-strength-after
                            :valence (or (:valence trigger-emotion) :negative)
                            :affect (:affect trigger-emotion)
                            :context-id sprouted-context-id
                            :role :trigger})
                          (emotion-shift-entry
                           {:emotion-id (:emotion-id hope-emotion)
                            :from-strength 0.0
                            :to-strength hope-strength
                            :valence :positive
                            :affect :hope
                            :situation-id reframe-situation-id
                            :context-id sprouted-context-id
                            :role :reframe})]
          emotional-state [(emotion-state-entry updated-trigger-emotion
                                                :trigger
                                                sprouted-context-id)
                           (emotion-state-entry hope-emotion
                                                :reframe
                                                sprouted-context-id)]]
      [world (assoc plan-state
                    :diversion-policy rationalization-diversion-policy
                    :trigger-emotion-id trigger-emotion-id
                    :trigger-emotion-before trigger-strength-before
                    :trigger-emotion-after trigger-strength-after
                    :hope-emotion-id (:emotion-id hope-emotion)
                    :hope-strength hope-strength
                    :hope-situation-id reframe-situation-id
                    :emotion-shifts emotion-shifts
                    :emotional-state emotional-state)])
    [world (assoc plan-state
                  :diversion-policy rationalization-diversion-policy
                  :emotion-shifts []
                  :emotional-state [])]))

(defn- rationalization-afterglow-fact
  [{:keys [goal-id context-id sprouted-context-id failed-goal-id frame-id
           trigger-emotion-id trigger-emotion-strength rule-provenance]}]
  (when (and goal-id
             context-id
             sprouted-context-id
             failed-goal-id
             trigger-emotion-id
             rule-provenance)
    {:fact/type :family-affect-state
     :source-family :rationalization
     :goal-id goal-id
     :context-id context-id
     :branch-context-id sprouted-context-id
     :failed-goal-id failed-goal-id
     :trigger-emotion-id trigger-emotion-id
     :trigger-emotion-strength (double (or trigger-emotion-strength 0.0))
     :frame-id frame-id
     :affect :hope
     :transition :reappraised
     :rule-provenance rule-provenance}))

(defn rationalization-plan
  "Sprout a reinterpretation branch from an explicit rationalization frame.

  This is a bounded bridge to Mueller's rationalization planner: instead of
  deriving LEADTO/minimization chains dynamically, choose a stored frame and
  assert its reframe facts into a fresh branch."
  [world {:keys [goal-id context-id trigger-context-id failed-goal-id frame-id ordering]
          :or {ordering 1.0}}]
  (let [plan-request-facts (or (seq (rationalization-plan-request-facts
                                     world
                                     {:goal-id goal-id
                                      :context-id context-id
                                      :trigger-context-id trigger-context-id
                                      :failed-goal-id failed-goal-id
                                      :frame-id frame-id
                                      :ordering ordering}))
                               (throw (ex-info "RATIONALIZATION needs a matching frame"
                                               {:goal-id goal-id
                                                :context-id context-id
                                                :trigger-context-id trigger-context-id
                                                :failed-goal-id failed-goal-id
                                                :frame-id frame-id})))
        {:keys [trigger-context-id failed-goal-id frame-id ordering
                selection-policy rule-provenance]}
        (or (first (plan-payloads-from-request-facts plan-request-facts))
            (throw (ex-info "RATIONALIZATION needs a plan payload"
                            {:goal-id goal-id
                             :context-id context-id
                             :plan-request-facts plan-request-facts})))
        frame-candidates (rationalization-frame-candidates
                          world
                          {:trigger-context-id trigger-context-id
                           :failed-goal-id failed-goal-id})
        frame (or (when frame-id
                    (some #(when (= frame-id (:frame-id %)) %) frame-candidates))
                  (first frame-candidates)
                  (throw (ex-info "RATIONALIZATION plan payload resolved to missing frame"
                                  {:goal-id goal-id
                                   :context-id context-id
                                   :trigger-context-id trigger-context-id
                                   :failed-goal-id failed-goal-id
                                   :frame-id frame-id
                                   :plan-request-facts plan-request-facts})))
        [world sprouted-context-id] (cx/sprout world context-id)
        success-intends {:fact/type :intends
                         :from-goal-id goal-id
                         :to-goal-id :rtrue
                         :top-level-goal goal-id
                         :status :succeeded
                         :rule :rationalization-plan1}
        world (reduce (fn [current-world fact]
                        (cx/assert-fact current-world sprouted-context-id fact))
                      world
                      (cons success-intends (:reframe-facts frame)))
        world (assoc-in world [:contexts sprouted-context-id :ordering] ordering)
        [world diversion]
        (apply-rationalization-emotional-diversion
         world
         {:goal-id goal-id
          :trigger-context-id trigger-context-id
          :failed-goal-id failed-goal-id
          :sprouted-context-id sprouted-context-id}
         frame)
        affect-state-fact (rationalization-afterglow-fact
                           {:goal-id goal-id
                            :context-id context-id
                            :sprouted-context-id sprouted-context-id
                            :failed-goal-id failed-goal-id
                            :frame-id (:frame-id frame)
                            :trigger-emotion-id (:trigger-emotion-id diversion)
                            :trigger-emotion-strength (:trigger-emotion-after diversion)
                            :rule-provenance rule-provenance})
        world (cond-> world
                affect-state-fact
                (cx/assert-fact sprouted-context-id affect-state-fact))
        world (if (contains? (:goals world) goal-id)
                (goals/set-next-context world goal-id sprouted-context-id)
                world)
        world (update world :mutation-events
                      (fnil conj [])
                      {:family :rationalization
                       :source-context context-id
                       :trigger-context trigger-context-id
                       :target-context sprouted-context-id
                       :failed-goal-id failed-goal-id
                       :frame-id (:frame-id frame)
                       :reframe-facts (:reframe-facts frame)
                       :emotion-diversion (select-keys diversion
                                                       [:diversion-policy
                                                        :trigger-emotion-id
                                                        :trigger-emotion-before
                                                        :trigger-emotion-after
                                                        :hope-emotion-id
                                                        :hope-strength
                                                        :hope-situation-id])})]
    [world {:sprouted-context-id sprouted-context-id
            :frame-id (:frame-id frame)
            :frame-goal-id (:goal-id frame)
            :reframe-fact-ids (mapv fact-id (:reframe-facts frame))
            :selection-policy selection-policy
            :rule-provenance rule-provenance
            :selection-reasons (:selection-reasons frame)
            :situation-id (:situation-id frame)
            :diversion-policy (:diversion-policy diversion)
            :trigger-emotion-id (:trigger-emotion-id diversion)
            :trigger-emotion-before (:trigger-emotion-before diversion)
            :trigger-emotion-after (:trigger-emotion-after diversion)
            :hope-emotion-id (:hope-emotion-id diversion)
            :hope-strength (:hope-strength diversion)
            :hope-situation-id (:hope-situation-id diversion)
            :affect-state-fact affect-state-fact
            :emotion-shifts (:emotion-shifts diversion)
            :emotional-state (:emotional-state diversion)}]))

(defn reverse-leafs
  "Sprout one alternative-past branch per weak leaf under the selected failed
  top-level goal, retracting the leaf objective in each branch to force
  replanning."
  [world {:keys [input-facts]
          :or {input-facts []}
          :as reversal-target}]
  (let [plan-request-facts (or (seq (reversal-plan-request-facts reversal-target))
                               (throw (ex-info "REVERSAL needs a planning target"
                                               {:reversal-target reversal-target})))
        {:keys [old-context-id old-top-level-goal-id new-context-id
                new-top-level-goal-id rule-provenance]}
        (or (first (plan-payloads-from-request-facts plan-request-facts))
            (throw (ex-info "REVERSAL needs a plan payload"
                            {:reversal-target reversal-target
                             :plan-request-facts plan-request-facts})))
        reversal-target (assoc reversal-target
                               :old-context-id old-context-id
                               :old-top-level-goal-id old-top-level-goal-id
                               :new-context-id new-context-id
                               :new-top-level-goal-id new-top-level-goal-id)]
    (reduce (fn [[current-world branch-results] branch]
              (let [[next-world sprouted-context-id]
                    (reversal-sprout-alternative
                     current-world
                     {:old-context-id (:old-context-id branch)
                      :old-top-level-goal-id (:old-top-level-goal-id reversal-target)
                      :new-context-id (:new-context-id reversal-target)
                      :new-top-level-goal-id (:new-top-level-goal-id reversal-target)
                      :ordering (:ordering branch)
                      :input-facts input-facts
                      :retract-facts (:objective-facts branch)})]
                [next-world
                 (conj branch-results
                       (assoc branch
                              :sprouted-context-id sprouted-context-id
                              :rule-provenance rule-provenance
                              :retracted-fact-ids (mapv fact-id (:objective-facts branch))))]))
            [world []]
            (reverse-leaf-branches world reversal-target))))

(defn run-family-plan
  "Execute one family plan through the extracted request/dispatch layer.

  Returns `[world result]`, where `result` is nil when no plan can run, or a
  trace-ready map with:
  - `:family`
  - `:sprouted-context-ids`
  - `:selection`
  - optional `:emotion-shifts` / `:emotional-state`
  - optional raw family-specific `:result` / `:branch-results` payloads"
  [world {:keys [goal-id goal-type] :as opts}]
  (let [goal-type (or goal-type
                      (get-in world [:goals goal-id :goal-type]))]
    (case goal-type
      :reversal
      (if-let [reversal-target (resolve-reversal-plan-target world goal-id opts)]
        (let [[world branch-results] (reverse-leafs world reversal-target)
              primary-branch (first branch-results)
              sprouted-context-ids (mapv :sprouted-context-id branch-results)]
          (if-not primary-branch
            [world nil]
            [world {:family :reversal
                    :sprouted-context-ids sprouted-context-ids
                    :rule-provenance (:rule-provenance primary-branch)
                    :selection {:goal_family :reversal
                                :family_goal_id (:new-top-level-goal-id reversal-target)
                                :reversal_target_policy (:selection-policy reversal-target)
                                :reversal_target_goal (:old-top-level-goal-id reversal-target)
                                :reversal_source_context (:old-context-id primary-branch)
                                :reversal_leaf_policy (:selection-policy primary-branch)
                                :reversal_leaf_goal (:leaf-goal-id primary-branch)
                                :reversal_leaf_strength (:leaf-strength primary-branch)
                                :reversal_leaf_depth (:context-depth reversal-target)
                                :reversal_leaf_emotion_pressure (:emotion-pressure reversal-target)
                                :reversal_leaf_reasons (:selection-reasons primary-branch)
                                :reversal_leaf_retracted_facts (:retracted-fact-ids primary-branch)
                                :reversal_branch_count (count branch-results)
                                :reversal_branch_contexts sprouted-context-ids
                                :reversal_counterfactual_policy (:counterfactual-policy reversal-target)
                                :reversal_counterfactual_source (:counterfactual-cause-id reversal-target)
                                :reversal_counterfactual_goal (:counterfactual-goal-id reversal-target)
                                :reversal_counterfactual_reasons (:counterfactual-reasons reversal-target)
                                :reversal_counterfactual_fact_ids (:counterfactual-fact-ids reversal-target)
                                :reversal_branch_context (:sprouted-context-id primary-branch)}
                    :reversal-target reversal-target
                    :primary-branch primary-branch
                    :branch-results branch-results}]))
        [world nil])

      :roving
      (let [context-id (or (:context-id opts)
                           (default-plan-context world goal-id))
            [world roving-result] (roving-plan world
                                               (assoc opts
                                                      :goal-id goal-id
                                                      :context-id context-id))]
        [world {:family :roving
                :sprouted-context-ids [(:sprouted-context-id roving-result)]
                :rule-provenance (:rule-provenance roving-result)
                :selection {:goal_family :roving
                            :family_goal_id goal-id
                            :roving_selection_policy (:selection-policy roving-result)
                            :roving_seed_episode (:episode-id roving-result)
                            :roving_reminded_episodes (:reminded-episode-ids roving-result)
                            :roving_active_indices (:active-indices roving-result)
                            :roving_branch_context (:sprouted-context-id roving-result)}
                :result roving-result}])

      :rationalization
      (let [context-id (or (:context-id opts)
                           (default-plan-context world goal-id))
            [world rationalization-result]
            (rationalization-plan
             world
             (assoc opts
                    :goal-id goal-id
                    :context-id context-id
                    :trigger-context-id (or (:trigger-context-id opts)
                                            (get-in world [:goals goal-id :trigger-context-id]))
                    :failed-goal-id (or (:failed-goal-id opts)
                                        (get-in world [:goals goal-id :trigger-failed-goal-id]))
                    :frame-id (or (:frame-id opts)
                                  (get-in world [:goals goal-id :trigger-frame-id]))))]
        [world {:family :rationalization
                :sprouted-context-ids [(:sprouted-context-id rationalization-result)]
                :rule-provenance (:rule-provenance rationalization-result)
                :selection {:goal_family :rationalization
                            :family_goal_id goal-id
                            :rationalization_selection_policy (:selection-policy rationalization-result)
                            :rationalization_frame_id (:frame-id rationalization-result)
                            :rationalization_frame_goal (:frame-goal-id rationalization-result)
                            :rationalization_frame_reasons (:selection-reasons rationalization-result)
                            :rationalization_reframe_fact_ids (:reframe-fact-ids rationalization-result)
                            :rationalization_branch_context (:sprouted-context-id rationalization-result)
                            :rationalization_diversion_policy (:diversion-policy rationalization-result)
                            :rationalization_trigger_emotion_id (:trigger-emotion-id rationalization-result)
                            :rationalization_trigger_emotion_before (:trigger-emotion-before rationalization-result)
                            :rationalization_trigger_emotion_after (:trigger-emotion-after rationalization-result)
                            :rationalization_hope_emotion_id (:hope-emotion-id rationalization-result)
                            :rationalization_hope_strength (:hope-strength rationalization-result)
                            :rationalization_hope_situation (:hope-situation-id rationalization-result)}
                :emotion-shifts (:emotion-shifts rationalization-result)
                :emotional-state (:emotional-state rationalization-result)
                :result rationalization-result}])

      [world nil])))

(defn- retract-facts
  [world context-id facts]
  (reduce (fn [current-world fact]
            (cx/retract-fact current-world context-id fact))
          world
          facts))

(defn- replace-goal-ref
  [fact key old-goal-id new-goal-id]
  (if (= (get fact key) old-goal-id)
    (assoc fact key new-goal-id)
    fact))

(defn- rebind-planning-fact
  [fact old-top-level-goal-id new-top-level-goal-id context-id]
  (let [fact (-> fact
                 (replace-goal-ref :goal-id old-top-level-goal-id new-top-level-goal-id)
                 (replace-goal-ref :top-level-goal old-top-level-goal-id new-top-level-goal-id)
                 (replace-goal-ref :from-goal-id old-top-level-goal-id new-top-level-goal-id)
                 (replace-goal-ref :to-goal-id old-top-level-goal-id new-top-level-goal-id))]
    (cond-> fact
      (goal-fact? fact)
      (assoc :top-level-goal new-top-level-goal-id
             :activation-context context-id)

      (and (goal-fact? fact)
           (contains? terminal-goal-statuses (:status fact)))
      (assoc :status :runable)

      (and (intends-fact? fact)
           (contains? fact :top-level-goal))
      (assoc :top-level-goal new-top-level-goal-id)

      (contains? fact :activation-context)
      (assoc :activation-context context-id))))

(defn gc-emotions
  "Remove emotion facts and their dependency links from a context."
  [world context-id]
  (let [facts (cx/visible-facts world context-id)
        emotion-ids (->> facts
                         (filter emotion-fact?)
                         (map fact-id)
                         set)
        removable-facts (filter (fn [fact]
                                  (or (emotion-fact? fact)
                                      (and (dependency-fact? fact)
                                           (seq (set/intersection
                                                 emotion-ids
                                                 (fact-ref-ids fact))))))
                                facts)]
    (retract-facts world context-id removable-facts)))

(defn gc-plans
  "Remove planning structure not owned by the specified top-level goals."
  [world context-id top-level-goal-ids]
  (let [allowed-goal-ids (set top-level-goal-ids)
        facts (cx/visible-facts world context-id)
        removed-goal-ids (->> facts
                              (filter goal-fact?)
                              (remove (fn [fact]
                                        (contains? allowed-goal-ids
                                                   (top-level-owner fact))))
                              (map fact-id)
                              set)
        removable-facts (filter (fn [fact]
                                  (or (and (goal-fact? fact)
                                           (not (contains? allowed-goal-ids
                                                           (top-level-owner fact))))
                                      (and (intends-fact? fact)
                                           (or (and (top-level-owner fact)
                                                    (not (contains? allowed-goal-ids
                                                                    (top-level-owner fact))))
                                               (seq (set/intersection
                                                     removed-goal-ids
                                                     (fact-ref-ids fact)))))))
                                facts)]
    (retract-facts world context-id removable-facts)))

(defn- rebind-planning-facts
  [world context-id old-top-level-goal-id new-top-level-goal-id]
  (let [facts (->> (cx/visible-facts world context-id)
                   (filter (fn [fact]
                             (or (goal-fact? fact)
                                 (intends-fact? fact))))
                   (sort-by pr-str))]
    (reduce (fn [current-world fact]
              (let [rebound-fact (rebind-planning-fact fact
                                                       old-top-level-goal-id
                                                       new-top-level-goal-id
                                                       context-id)]
                (if (= fact rebound-fact)
                  current-world
                  (-> current-world
                      (cx/retract-fact context-id fact)
                      (cx/assert-fact context-id rebound-fact)))))
            world
            facts)))

(defn sprout-alternative-past
  "Copy an old planning context into a fresh root, pseudo-sprout it under the
  new planning context, clear emotional residue, and rebind surviving plan
  structure to the new top-level goal."
  [world {:keys [old-context-id old-top-level-goal-id new-context-id
                 new-top-level-goal-id]}]
  (let [[world sprouted-context-id] (cx/copy-context world old-context-id nil)
        world (cx/pseudo-sprout world sprouted-context-id new-context-id)
        world (-> world
                  (assoc-in [:contexts sprouted-context-id :alternative-past?] true)
                  (assoc-in [:contexts sprouted-context-id :generation-switches]
                            {:tense :conditional-present-perfect}))
        world (gc-emotions world sprouted-context-id)
        world (gc-plans world sprouted-context-id [old-top-level-goal-id])
        world (rebind-planning-facts world
                                     sprouted-context-id
                                     old-top-level-goal-id
                                     new-top-level-goal-id)
        world (if (contains? (:goals world) new-top-level-goal-id)
                (goals/set-next-context world
                                        new-top-level-goal-id
                                        sprouted-context-id)
                world)]
    [world sprouted-context-id]))

(defn reversal-sprout-alternative
  "REVERSAL family wrapper around `sprout-alternative-past`.

  Optional keys:
  - `:ordering` -> branch ordering used by backtracking
  - `:input-facts` -> state facts asserted into the alternative past
  - `:retract-facts` -> shaky leaf assumptions removed from the branch"
  [world {:keys [old-context-id ordering input-facts]
          :or {input-facts []}
          :as opts}]
  (let [retraction-facts (vec (:retract-facts opts []))
        [world sprouted-context-id] (sprout-alternative-past world opts)
        world (cond-> world
                (some? ordering)
                (assoc-in [:contexts sprouted-context-id :ordering] ordering))
        world (reduce (fn [current-world fact]
                        (cx/assert-fact current-world sprouted-context-id fact))
                      world
                      input-facts)
        world (reduce (fn [current-world fact]
                        (cx/retract-fact current-world sprouted-context-id fact))
                      world
                      retraction-facts)
        world (update world :mutation-events
                      (fnil conj [])
                      {:family :reversal
                       :source-context old-context-id
                       :target-context sprouted-context-id
                       :input-facts (vec input-facts)
                       :retracted-facts retraction-facts})]
    [world sprouted-context-id]))
