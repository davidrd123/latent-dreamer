(ns daydreamer.goal-family-rules
  "Pure policy constants and RuleV1 data for family activation, planning, and
  cross-family bridges.")

(def supported-families
  #{:reversal :roving :rationalization :recovery :rehearsal})

(def terminal-goal-statuses
  #{:failed :succeeded :terminated})

(defn supported-family?
  "Return true when the kernel has a namespace-level implementation target for
  the given family."
  [goal-type]
  (contains? supported-families goal-type))

(def reversal-leaf-policy
  :emotion_then_depth)

(def reversal-cause-policy
  :stored_priority)

(def reverse-leaf-policy
  :intends_weak_leaf)

(def reverse-leaf-threshold
  0.5)

(def roving-emotion-threshold
  0.04)

(def rationalization-emotion-threshold
  0.7)

(def reversal-emotion-threshold
  0.5)

(def roving-plan-policy
  :pleasant_episode_seed)

(def rationalization-frame-policy
  :stored_priority)

(def rationalization-plan-policy
  :stored_rationalization_frame)

(def rationalization-diversion-policy
  :divert_emot_to_tlg_bridge)

(def rationalization-diversion-scale
  0.35)

(def ^:private empty-index-projections
  {:match []
   :emit []})

(def ^:private empty-graph-cache
  {:out-edge-bases []
   :in-edge-bases []})

(defn- rule-provenance
  [book-anchors kernel-status notes]
  {:book-anchors book-anchors
   :kernel-status kernel-status
   :notes notes})

(defn- denotation
  [intended-effect failure-modes]
  {:intended-effect intended-effect
   :failure-modes failure-modes
   :validation-fn nil})

(defn- instantiate-rule
  [{:keys [id rule-kind mueller-mode antecedent consequent plausibility
           denotation executor-spec provenance]}]
  {:id id
   :rule-kind rule-kind
   :mueller-mode mueller-mode
   :antecedent-schema antecedent
   :consequent-schema consequent
   :plausibility plausibility
   :index-projections empty-index-projections
   :denotation denotation
   :executor {:kind :instantiate
              :spec executor-spec}
   :graph-cache empty-graph-cache
   :provenance provenance})

(defn- planning-rule
  [spec]
  (instantiate-rule (assoc spec
                           :rule-kind :planning
                           :mueller-mode :plan-only)))

(defn- inference-rule
  [spec]
  (instantiate-rule (merge {:mueller-mode :inference-only}
                           spec
                           {:rule-kind :inference})))

(def roving-plan-request-rule
  (planning-rule
   {:id :goal-family/roving-plan-request
    :antecedent [{:fact/type :family-plan-ready
                  :goal-type :roving
                  :goal-id '?goal-id
                  :context-id '?context-id
                  :episode-id '?episode-id
                  :ordering '?ordering}]
    :consequent [{:fact/type :family-plan-request
                  :goal-type :roving
                  :goal-id '?goal-id
                  :context-id '?context-id
                  :episode-id '?episode-id
                  :ordering '?ordering
                  :selection-policy roving-plan-policy}]
    :plausibility 1.0
    :denotation (denotation :emit-roving-plan-request
                            [:missing-seed-episode
                             :missing-planning-context])
    :executor-spec {:requires-episode? true
                    :requires-context? true}
    :provenance (rule-provenance
                 [:theme-roving]
                 :partial
                 "Graphable request fact for roving branch planning.")}))

(def roving-plan-dispatch-rule
  (planning-rule
   {:id :goal-family/roving-plan-dispatch
    :antecedent [{:fact/type :family-plan-request
                  :goal-type :roving
                  :goal-id '?goal-id
                  :context-id '?context-id
                  :episode-id '?episode-id
                  :ordering '?ordering
                  :selection-policy '?selection-policy}]
    :consequent [{:goal-id '?goal-id
                  :context-id '?context-id
                  :episode-id '?episode-id
                  :ordering '?ordering
                  :selection-policy '?selection-policy}]
    :plausibility 1.0
    :denotation (denotation :dispatch-roving-plan-request
                            [:missing-family-plan-request])
    :executor-spec {:request-goal-type :roving}
    :provenance (rule-provenance
                 [:theme-roving]
                 :partial
                 "Consumes a roving plan request into the current bounded plan payload.")}))

(def rationalization-plan-request-rule
  (planning-rule
   {:id :goal-family/rationalization-plan-request
    :antecedent [{:fact/type :family-plan-ready
                  :goal-type :rationalization
                  :goal-id '?goal-id
                  :context-id '?context-id
                  :trigger-context-id '?trigger-context-id
                  :failed-goal-id '?failed-goal-id
                  :trigger-emotion-id '?trigger-emotion-id
                  :trigger-emotion-strength '?trigger-emotion-strength
                  :frame-id '?frame-id
                  :ordering '?ordering}]
    :consequent [{:fact/type :family-plan-request
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
    :denotation (denotation :emit-rationalization-plan-request
                            [:missing-rationalization-frame
                             :missing-planning-context])
    :executor-spec {:requires-frame? true
                    :requires-context? true}
    :provenance (rule-provenance
                 [:theme-rationalization]
                 :partial
                 "Graphable request fact for rationalization branch planning.")}))

(def rationalization-plan-dispatch-rule
  (planning-rule
   {:id :goal-family/rationalization-plan-dispatch
    :antecedent [{:fact/type :family-plan-request
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
    :consequent [{:goal-id '?goal-id
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
    :denotation (denotation :dispatch-rationalization-plan-request
                            [:missing-family-plan-request])
    :executor-spec {:request-goal-type :rationalization}
    :provenance (rule-provenance
                 [:theme-rationalization]
                 :partial
                 "Consumes a rationalization plan request into the current bounded plan payload.")}))

(def rationalization-afterglow-to-roving-rule
  (inference-rule
   {:id :goal-family/rationalization-afterglow-to-roving
    :mueller-mode :both
    :antecedent [{:fact/type :family-affect-state
                  :source-family :rationalization
                  :goal-id '?goal-id
                  :context-id '?context-id
                  :failed-goal-id '?failed-goal-id
                  :trigger-emotion-id '?trigger-emotion-id
                  :trigger-emotion-strength '?trigger-emotion-strength
                  :frame-id '?frame-id
                  :affect :hope
                  :transition :reappraised}]
    :consequent [{:fact/type :goal-family-trigger
                  :goal-type :roving
                  :trigger-context-id '?context-id
                  :failed-goal-id '?failed-goal-id
                  :emotion-id '?trigger-emotion-id
                  :emotion-strength '?trigger-emotion-strength
                  :selection-policy :rationalization_afterglow
                  :selection-reasons [:rationalization_afterglow
                                      :hope_reframe]}]
    :plausibility roving-emotion-threshold
    :denotation (denotation :emit-roving-trigger-from-rationalization-afterglow
                            [:missing-rationalization-afterglow])
    :executor-spec {:source-family :rationalization
                    :target-goal-type :roving}
    :provenance (rule-provenance
                 [:theme-rationalization :theme-roving]
                 :proposed
                 "Cross-family bridge from rationalization's hopeful afterglow into a roving trigger.")}))

(def reversal-aftershock-to-roving-rule
  (inference-rule
   {:id :goal-family/reversal-aftershock-to-roving
    :mueller-mode :both
    :antecedent [{:fact/type :family-affect-state
                  :source-family :reversal
                  :goal-id '?goal-id
                  :context-id '?context-id
                  :failed-goal-id '?failed-goal-id
                  :trigger-emotion-id '?trigger-emotion-id
                  :trigger-emotion-strength '?trigger-emotion-strength
                  :transition :counterfactual_reopened}]
    :consequent [{:fact/type :goal-family-trigger
                  :goal-type :roving
                  :trigger-context-id '?context-id
                  :failed-goal-id '?failed-goal-id
                  :emotion-id '?trigger-emotion-id
                  :emotion-strength '?trigger-emotion-strength
                  :selection-policy :reversal_aftershock
                  :selection-reasons [:reversal_aftershock
                                      :counterfactual_reopened]}]
    :plausibility roving-emotion-threshold
    :denotation (denotation :emit-roving-trigger-from-reversal-aftershock
                            [:missing-reversal-aftershock])
    :executor-spec {:source-family :reversal
                    :target-goal-type :roving}
    :provenance (rule-provenance
                 [:theme-reversal :theme-roving]
                 :proposed
                 "Cross-family bridge from reversal's counterfactual aftershock into a roving trigger.")}))

(def reversal-aftershock-to-rationalization-rule
  (inference-rule
   {:id :goal-family/reversal-aftershock-to-rationalization
    :mueller-mode :both
    :antecedent [{:fact/type :family-affect-state
                  :source-family :reversal
                  :goal-id '?goal-id
                  :context-id '?context-id
                  :failed-goal-id '?failed-goal-id
                  :trigger-emotion-id '?trigger-emotion-id
                  :trigger-emotion-strength '?trigger-emotion-strength
                  :transition :counterfactual_reopened}
                 {:fact/type :rationalization-frame
                  :fact/id '?frame-id
                  :goal-id '?failed-goal-id
                  :priority '?frame-priority}]
    :consequent [{:fact/type :goal-family-trigger
                  :goal-type :rationalization
                  :trigger-context-id '?context-id
                  :failed-goal-id '?failed-goal-id
                  :emotion-id '?trigger-emotion-id
                  :emotion-strength '?trigger-emotion-strength
                  :frame-id '?frame-id
                  :frame-priority '?frame-priority
                  :frame-count 1
                  :situation-id nil
                  :selection-policy :reversal_aftershock_rationalization_frame
                  :selection-reasons [:reversal_aftershock
                                      :counterfactual_reopened
                                      :rationalization_frame]}]
    :plausibility rationalization-emotion-threshold
    :denotation (denotation :emit-rationalization-trigger-from-reversal-aftershock
                            [:missing-reversal-aftershock
                             :missing-rationalization-frame])
    :executor-spec {:source-family :reversal
                    :target-goal-type :rationalization}
    :provenance (rule-provenance
                 [:theme-reversal :theme-rationalization]
                 :proposed
                 "Cross-family bridge from reversal's counterfactual aftershock into a rationalization trigger when a reframing frame is available.")}))

(def reversal-plan-request-rule
  (planning-rule
   {:id :goal-family/reversal-plan-request
    :antecedent [{:fact/type :family-plan-ready
                  :goal-type :reversal
                  :goal-id '?goal-id
                  :old-context-id '?old-context-id
                  :old-top-level-goal-id '?old-top-level-goal-id
                  :failed-goal-id '?failed-goal-id
                  :trigger-emotion-id '?trigger-emotion-id
                  :trigger-emotion-strength '?trigger-emotion-strength
                  :new-context-id '?new-context-id
                  :new-top-level-goal-id '?new-top-level-goal-id}]
    :consequent [{:fact/type :family-plan-request
                  :goal-type :reversal
                  :goal-id '?goal-id
                  :old-context-id '?old-context-id
                  :old-top-level-goal-id '?old-top-level-goal-id
                  :failed-goal-id '?failed-goal-id
                  :trigger-emotion-id '?trigger-emotion-id
                  :trigger-emotion-strength '?trigger-emotion-strength
                  :new-context-id '?new-context-id
                  :new-top-level-goal-id '?new-top-level-goal-id
                  :selection-policy reverse-leaf-policy}]
    :plausibility 1.0
    :denotation (denotation :emit-reversal-plan-request
                            [:missing-old-context
                             :missing-new-context])
    :executor-spec {:requires-old-context? true
                    :requires-new-context? true}
    :provenance (rule-provenance
                 [:theme-reversal]
                 :partial
                 "Graphable request fact for reversal branch planning.")}))

(def reversal-plan-dispatch-rule
  (planning-rule
   {:id :goal-family/reversal-plan-dispatch
    :antecedent [{:fact/type :family-plan-request
                  :goal-type :reversal
                  :goal-id '?goal-id
                  :old-context-id '?old-context-id
                  :old-top-level-goal-id '?old-top-level-goal-id
                  :failed-goal-id '?failed-goal-id
                  :trigger-emotion-id '?trigger-emotion-id
                  :trigger-emotion-strength '?trigger-emotion-strength
                  :new-context-id '?new-context-id
                  :new-top-level-goal-id '?new-top-level-goal-id
                  :selection-policy '?selection-policy}]
    :consequent [{:goal-id '?goal-id
                  :old-context-id '?old-context-id
                  :old-top-level-goal-id '?old-top-level-goal-id
                  :failed-goal-id '?failed-goal-id
                  :trigger-emotion-id '?trigger-emotion-id
                  :trigger-emotion-strength '?trigger-emotion-strength
                  :new-context-id '?new-context-id
                  :new-top-level-goal-id '?new-top-level-goal-id
                  :selection-policy '?selection-policy}
                 {:fact/type :family-affect-state
                  :source-family :reversal
                  :goal-id '?goal-id
                  :context-id '?old-context-id
                  :failed-goal-id '?failed-goal-id
                  :trigger-emotion-id '?trigger-emotion-id
                  :trigger-emotion-strength '?trigger-emotion-strength
                  :transition :counterfactual_reopened}]
    :plausibility 1.0
    :denotation (denotation :dispatch-reversal-plan-request
                            [:missing-family-plan-request])
    :executor-spec {:request-goal-type :reversal}
    :provenance (rule-provenance
                 [:theme-reversal]
                 :partial
                 "Consumes a reversal plan request into the current bounded plan payload.")}))

(def roving-trigger-rule
  (inference-rule
   {:id :goal-family/roving-trigger
    :antecedent [{:fact/type :goal
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
    :consequent [{:fact/type :goal-family-trigger
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
    :denotation (denotation :emit-roving-goal-family-trigger
                            [:emotion-below-threshold
                             :rationalization-frame-available
                             :missing-dependency-link])
    :executor-spec {:emotion-threshold roving-emotion-threshold
                    :requires-negative-emotion? true
                    :requires-frame-absence? true}
    :provenance (rule-provenance
                 [:theme-roving]
                 :partial
                 "First graphable trigger slice from goal_families activation logic.")}))

(def roving-activation-rule
  (inference-rule
   {:id :goal-family/roving-activation
    :antecedent [{:fact/type :goal-family-trigger
                  :goal-type :roving
                  :trigger-context-id '?context-id
                  :failed-goal-id '?failed-goal-id
                  :emotion-id '?emotion-id
                  :emotion-strength '?emotion-strength
                  :selection-policy '?selection-policy
                  :selection-reasons '?selection-reasons}]
    :consequent [{:context-id '?context-id
                  :failed-goal-id '?failed-goal-id
                  :emotion-id '?emotion-id
                  :emotion-strength '?emotion-strength
                  :selection-policy '?selection-policy
                  :selection-reasons '?selection-reasons}
                 {:fact/type :family-plan-ready
                  :goal-type :roving
                  :trigger-context-id '?context-id
                  :failed-goal-id '?failed-goal-id
                  :emotion-id '?emotion-id
                  :emotion-strength '?emotion-strength}]
    :plausibility roving-emotion-threshold
    :denotation (denotation :activate-roving-daydream-goal
                            [:missing-goal-family-trigger])
    :executor-spec {:trigger-goal-type :roving}
    :provenance (rule-provenance
                 [:theme-roving]
                 :partial
                 "Consumes the structural roving trigger into an activation payload.")}))

(def rationalization-trigger-rule
  (inference-rule
   {:id :goal-family/rationalization-trigger
    :antecedent [{:fact/type :goal
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
    :consequent [{:fact/type :goal-family-trigger
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
    :denotation (denotation :emit-rationalization-goal-family-trigger
                            [:emotion-below-threshold
                             :missing-dependency-link
                             :missing-rationalization-frame])
    :executor-spec {:emotion-threshold rationalization-emotion-threshold
                    :requires-negative-emotion? true
                    :requires-frame? true}
    :provenance (rule-provenance
                 [:theme-rationalization]
                 :partial
                 "Graphable rationalization trigger emitted from activation logic.")}))

(def rationalization-activation-rule
  (inference-rule
   {:id :goal-family/rationalization-activation
    :antecedent [{:fact/type :goal-family-trigger
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
    :consequent [{:context-id '?context-id
                  :failed-goal-id '?failed-goal-id
                  :emotion-id '?emotion-id
                  :emotion-strength '?emotion-strength
                  :frame-id '?frame-id
                  :frame-priority '?frame-priority
                  :frame-count '?frame-count
                  :situation-id '?situation-id
                  :selection-policy '?selection-policy
                  :selection-reasons '?selection-reasons}
                 {:fact/type :family-plan-ready
                  :goal-type :rationalization
                  :trigger-context-id '?context-id
                  :failed-goal-id '?failed-goal-id
                  :emotion-id '?emotion-id
                  :emotion-strength '?emotion-strength
                  :frame-id '?frame-id}]
    :plausibility rationalization-emotion-threshold
    :denotation (denotation :activate-rationalization-daydream-goal
                            [:missing-goal-family-trigger])
    :executor-spec {:trigger-goal-type :rationalization}
    :provenance (rule-provenance
                 [:theme-rationalization]
                 :partial
                 "Consumes the structural rationalization trigger into an activation payload.")}))

(def reversal-trigger-rule
  (inference-rule
   {:id :goal-family/reversal-trigger
    :antecedent [{:fact/type :goal
                  :goal-id '?failed-goal-id
                  :top-level-goal '?old-top-level-goal-id
                  :status :failed
                  :activation-context '?old-context-id}
                 {:fact/type :emotion
                  :emotion-id '?emotion-id
                  :strength '?emotion-strength}]
    :consequent [{:fact/type :goal-family-trigger
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
    :denotation (denotation :emit-reversal-goal-family-trigger
                            [:emotion-below-threshold
                             :missing-negative-emotion
                             :missing-failed-leaf])
    :executor-spec {:emotion-threshold reversal-emotion-threshold
                    :requires-negative-emotion? true
                    :requires-failed-leaf? true}
    :provenance (rule-provenance
                 [:theme-reversal]
                 :partial
                 "Graphable reversal trigger emitted from failed-leaf selection.")}))

(def reversal-activation-rule
  (inference-rule
   {:id :goal-family/reversal-activation
    :antecedent [{:fact/type :goal-family-trigger
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
    :consequent [{:old-context-id '?old-context-id
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
                  :situation-id '?situation-id}
                 {:fact/type :family-plan-ready
                  :goal-type :reversal
                  :old-context-id '?old-context-id
                  :old-top-level-goal-id '?old-top-level-goal-id
                  :failed-goal-id '?failed-goal-id
                  :emotion-id '?emotion-id
                  :emotion-strength '?emotion-strength}]
    :plausibility reversal-emotion-threshold
    :denotation (denotation :activate-reversal-daydream-goal
                            [:emotion-below-threshold
                             :missing-negative-emotion
                             :missing-failed-leaf])
    :executor-spec {:emotion-threshold reversal-emotion-threshold
                    :requires-negative-emotion? true
                    :requires-failed-leaf? true}
    :provenance (rule-provenance
                 [:theme-reversal]
                 :partial
                 "Third extracted RuleV1 slice from goal_families activation logic.")}))

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
  [rationalization-afterglow-to-roving-rule
   reversal-aftershock-to-roving-rule
   reversal-aftershock-to-rationalization-rule])

(defn family-rules
  "Return the current combined family rule registry."
  []
  (vec (concat (planning-rules)
               (cross-family-rules)
               (activation-rules))))
