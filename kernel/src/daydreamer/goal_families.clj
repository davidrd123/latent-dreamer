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
   roving-plan-dispatch-rule])

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
                                       :goal-type (:goal-type trigger-fact))))))))
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
                                       :goal-type (:goal-type request-fact))))))))
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
                                  (-> (rules/instantiate-rule
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
                                                        (:old-context-id candidate))})
                                      :consequents
                                      first)))))
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
  (-> (rules/instantiate-rule rationalization-trigger-rule
                              {'?context-id context-id
                               '?failed-goal-id failed-goal-id
                               '?emotion-id emotion-id
                               '?emotion-strength emotion-strength
                               '?frame-id frame-id
                               '?frame-priority frame-priority
                               '?frame-count frame-count
                               '?situation-id situation-id})
      :consequents
      first))

(defn- instantiate-roving-trigger
  [context-id failed-goal-id emotion-id emotion-strength]
  (-> (rules/instantiate-rule roving-trigger-rule
                              {'?context-id context-id
                               '?failed-goal-id failed-goal-id
                               '?emotion-id emotion-id
                               '?emotion-strength emotion-strength})
      :consequents
      first))

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
                                frame-id]}]
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
                      :activation-reasons (or activation-reasons selection-reasons)})))))
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
  (-> (rules/instantiate-rule roving-plan-request-rule
                              {'?goal-id goal-id
                               '?context-id context-id
                               '?episode-id episode-id
                               '?ordering ordering})
      :consequents
      first))

(defn- roving-plan-request-facts
  [world {:keys [goal-id context-id ordering]
          :or {ordering 1.0}
          :as opts}]
  (when-let [episode-id (choose-roving-episode-id world opts)]
    [(instantiate-roving-plan-request goal-id
                                      context-id
                                      episode-id
                                      ordering)]))

(defn roving-plan
  "Sprout a side-channel context and seed it with a pleasant episode reminder.

  The episode reminding cascade is the heavy lifter here; this plan mostly
  creates a fresh branch, invokes episodic memory, and records the result."
  [world {:keys [goal-id context-id]
          :as opts}]
  (let [plan-request-facts (or (seq (roving-plan-request-facts world opts))
                               (throw (ex-info "ROVING needs a pleasant episode"
                                               {:opts opts})))
        {:keys [episode-id ordering selection-policy]}
        (or (first (plan-payloads-from-request-facts plan-request-facts))
            (throw (ex-info "ROVING needs a plan payload"
                            {:opts opts
                             :plan-request-facts plan-request-facts})))
        [world sprouted-context-id] (cx/sprout world context-id)
        success-intends {:fact/type :intends
                         :from-goal-id goal-id
                         :to-goal-id :rtrue
                         :top-level-goal goal-id
                         :status :succeeded
                         :rule :roving-plan1}
        world (cx/assert-fact world sprouted-context-id success-intends)
        [world reminded-episode-ids] (episodic/episode-reminding world episode-id)
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
            :selection-policy selection-policy}]))

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

(defn rationalization-plan
  "Sprout a reinterpretation branch from an explicit rationalization frame.

  This is a bounded bridge to Mueller's rationalization planner: instead of
  deriving LEADTO/minimization chains dynamically, choose a stored frame and
  assert its reframe facts into a fresh branch."
  [world {:keys [goal-id context-id trigger-context-id failed-goal-id frame-id ordering]
          :or {ordering 1.0}}]
  (let [trigger-context-id (or trigger-context-id context-id)
        frame-candidates (rationalization-frame-candidates
                          world
                          {:trigger-context-id trigger-context-id
                           :failed-goal-id failed-goal-id})
        frame (or (when frame-id
                    (some #(when (= frame-id (:frame-id %)) %) frame-candidates))
                  (first frame-candidates)
                  (throw (ex-info "RATIONALIZATION needs a matching frame"
                                  {:goal-id goal-id
                                   :context-id context-id
                                   :trigger-context-id trigger-context-id
                                   :failed-goal-id failed-goal-id})))
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
            :selection-policy rationalization-plan-policy
            :selection-reasons (:selection-reasons frame)
            :situation-id (:situation-id frame)
            :diversion-policy (:diversion-policy diversion)
            :trigger-emotion-id (:trigger-emotion-id diversion)
            :trigger-emotion-before (:trigger-emotion-before diversion)
            :trigger-emotion-after (:trigger-emotion-after diversion)
            :hope-emotion-id (:hope-emotion-id diversion)
            :hope-strength (:hope-strength diversion)
            :hope-situation-id (:hope-situation-id diversion)
            :emotion-shifts (:emotion-shifts diversion)
            :emotional-state (:emotional-state diversion)}]))

(defn reverse-leafs
  "Sprout one alternative-past branch per weak leaf under the selected failed
  top-level goal, retracting the leaf objective in each branch to force
  replanning."
  [world {:keys [input-facts]
          :or {input-facts []}
          :as reversal-target}]
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
                            :retracted-fact-ids (mapv fact-id (:objective-facts branch))))]))
          [world []]
          (reverse-leaf-branches world reversal-target)))

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
