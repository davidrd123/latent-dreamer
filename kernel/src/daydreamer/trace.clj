(ns daydreamer.trace
  "Structured trace logs for comparison with the Python engine.

  Each cognitive cycle produces an immutable snapshot. The trace is an
  append-only tree where context sprouts are branches and backtracking
  is recorded as path selection. Output formats: EDN (native) and a
  comparison-friendly JSON adapter shape.

  Trace fields per cycle: cycle number, selected goal, current mode,
  context id and depth, sprouted children, active plan chain, retrieval
  hits and thresholds, backtrack events, mutation attempts, and
  termination events."
  (:require [clojure.set :as set]
            [daydreamer.context :as cx]))

(def ^:private reporter-top-level-keys
  ["started_at"
   "seed"
   "world_path"
   "graph_path"
   "feedback_path"
   "palette_path"
   "git_commit"
   "engine_path"
   "engine_sha256"
   "world_sha256"
   "graph_sha256"
   "feedback_sha256"
   "palette_sha256"
   "graph_nodes"
   "graph_edges"])

(defn- scalar->json
  [value]
  (cond
    (keyword? value) (name value)
    (symbol? value) (name value)
    :else value))

(defn- json-value
  [value]
  (cond
    (map? value)
    (into {}
          (map (fn [[k v]]
                 [(scalar->json k) (json-value v)]))
          value)

    (set? value)
    (->> value
         (map json-value)
         (sort-by str)
         vec)

    (sequential? value)
    (mapv json-value value)

    :else
    (scalar->json value)))

(defn- qualified-scalar->json
  [value]
  (cond
    (keyword? value) (if-let [ns-part (namespace value)]
                       (str ns-part "/" (name value))
                       (name value))
    (symbol? value) (str value)
    :else value))

(defn- provenance-json-value
  [value]
  (cond
    (map? value)
    (into {}
          (map (fn [[k v]]
                 [(scalar->json k) (provenance-json-value v)]))
          value)

    (set? value)
    (->> value
         (map provenance-json-value)
         (sort-by str)
         vec)

    (sequential? value)
    (mapv provenance-json-value value)

    :else
    (qualified-scalar->json value)))

(defn- goal-or-throw
  [world goal-id]
  (or (get-in world [:goals goal-id])
      (throw (ex-info "Unknown goal for trace snapshot"
                      {:goal-id goal-id}))))

(defn goal-summary
  "Project a goal into a compact trace payload."
  [world goal-id]
  (let [{:keys [id goal-type strength planning-type situation-id main-motiv]}
        (goal-or-throw world goal-id)]
    (cond-> {:id id
             :goal-type goal-type
             :strength strength}
      planning-type
      (assoc :planning-type planning-type)

      situation-id
      (assoc :situation-id situation-id)

      main-motiv
      (assoc :main-motiv main-motiv))))

(defn- context-depth
  [world context-id]
  (if (and context-id (contains? (:contexts world) context-id))
    (count (cx/ancestors world context-id))
    0))

(defn- reporter-goal
  [goal]
  (cond-> {"id" (some-> (:id goal) scalar->json)
           "goal_type" (some-> (:goal-type goal) scalar->json)
           "strength" (:strength goal)
           "planning_type" (some-> (:planning-type goal) scalar->json)
           "situation_id" (some-> (:situation-id goal) scalar->json)}
    (:family goal)
    (assoc "family" (some-> (:family goal) scalar->json))

    (:kind goal)
    (assoc "kind" (some-> (:kind goal) scalar->json))

    (:goal-id goal)
    (assoc "goal_id" (some-> (:goal-id goal) scalar->json))

    (:operator-key goal)
    (assoc "operator_key" (some-> (:operator-key goal) scalar->json))

    (:operator-id goal)
    (assoc "operator_id" (some-> (:operator-id goal) scalar->json))

    (contains? goal :trigger-selection-policy)
    (assoc "trigger_selection_policy" (some-> (:trigger-selection-policy goal)
                                              scalar->json))

    (contains? goal :frontier-bridge-trigger?)
    (assoc "frontier_bridge_trigger" (boolean (:frontier-bridge-trigger? goal)))))

(defn- reporter-activation
  [activation]
  {"goal_id" (some-> (:goal-id activation) scalar->json)
   "goal_type" (some-> (:goal-type activation) scalar->json)
   "trigger_context_id" (some-> (:trigger-context-id activation) scalar->json)
   "failed_goal_id" (some-> (:failed-goal-id activation) scalar->json)
   "emotion_id" (some-> (:emotion-id activation) scalar->json)
   "emotion_strength" (:emotion-strength activation)
   "activation_policy" (some-> (:activation-policy activation) scalar->json)
   "activation_reasons" (json-value (:activation-reasons activation))
   "rule_provenance" (provenance-json-value (:rule-provenance activation))})

(defn- reporter-emotion-shift
  [shift]
  {"emotion_id" (some-> (:emotion-id shift) scalar->json)
   "from_strength" (:from-strength shift)
   "to_strength" (:to-strength shift)
   "delta" (:delta shift)
   "valence" (some-> (:valence shift) scalar->json)
   "affect" (some-> (:affect shift) scalar->json)
   "situation_id" (some-> (:situation-id shift) scalar->json)
   "context_id" (some-> (:context-id shift) scalar->json)
   "role" (some-> (:role shift) scalar->json)})

(defn- reporter-emotional-state
  [emotion]
  (cond-> {"emotion_id" (some-> (:emotion-id emotion) scalar->json)
           "strength" (:strength emotion)
           "valence" (some-> (:valence emotion) scalar->json)
           "affect" (some-> (:affect emotion) scalar->json)
           "situation_id" (some-> (:situation-id emotion) scalar->json)
           "context_id" (some-> (:context-id emotion) scalar->json)
           "role" (some-> (:role emotion) scalar->json)}
    (:provenance emotion)
    (assoc "provenance" (some-> (:provenance emotion) scalar->json))

    (:projection-kind emotion)
    (assoc "projection_kind" (some-> (:projection-kind emotion) scalar->json))))

(defn- fact-id
  [fact]
  (or (:fact/id fact)
      (:goal-id fact)
      (:emotion-id fact)
      (:id fact)))

(defn- fact-type
  [fact]
  (:fact/type fact))

(defn- distinct-vec
  [values]
  (->> values
       (remove nil?)
       distinct
       vec))

(defn- branch-fact-summary
  [facts]
  {:fact-ids (->> facts (map fact-id) distinct-vec)
   :fact-types (->> facts (map fact-type) distinct-vec)})

(defn- branch-event
  [mutation]
  (when-let [target-context (:target-context mutation)]
    (let [{fact-ids :fact-ids
           fact-types :fact-types}
          (branch-fact-summary (concat (:input-facts mutation)
                                       (:reframe-facts mutation)))
          {retracted-fact-ids :fact-ids
           retracted-fact-types :fact-types}
          (branch-fact-summary (:retracted-facts mutation))
          episode-ids (distinct-vec
                       (concat [(:seed-episode-id mutation)]
                               (:reminded-episode-ids mutation)))
          active-indices (distinct-vec (:active-indices mutation))]
      (cond-> {:family (:family mutation)
               :source-context (:source-context mutation)
               :target-context target-context
               :fact-ids fact-ids
               :fact-types fact-types}
        (seq episode-ids)
        (assoc :episode-ids episode-ids)

        (seq active-indices)
        (assoc :active-indices active-indices)

        (seq retracted-fact-ids)
        (assoc :retracted-fact-ids retracted-fact-ids
               :retracted-fact-types retracted-fact-types)))))

(defn derive-branch-events
  "Derive canonical branch events from the snapshot mutation stream."
  [snapshot]
  (->> (:mutations snapshot)
       (keep branch-event)
       vec))

(defn- reporter-branch-event
  [event]
  {"family" (some-> (:family event) scalar->json)
   "source_context" (some-> (:source-context event) scalar->json)
   "target_context" (some-> (:target-context event) scalar->json)
   "fact_ids" (json-value (or (:fact-ids event) []))
   "fact_types" (json-value (or (:fact-types event) []))
   "episode_ids" (json-value (or (:episode-ids event) []))
   "active_indices" (json-value (or (:active-indices event) []))
   "retracted_fact_ids" (json-value (or (:retracted-fact-ids event) []))
   "retracted_fact_types" (json-value (or (:retracted-fact-types event) []))})

(defn cycle-snapshot
  "Build an internal cycle snapshot. Fields that are not yet produced by the
  kernel can be supplied explicitly by the caller."
  [world {:keys [goal-id selected-goal top-candidate-ids top-candidates
                 active-indices retrievals episodic-retrievals chosen-node-id selection
                 feedback-applied serendipity-bias situations context-id
                 sprouted active-plan backtrack-events activations mutations
                 terminations timestamp goal-selection emotion-shifts
                 branch-events rule-provenance
                 emotional-state]
          :or {active-indices []
               retrievals []
               episodic-retrievals []
               active-plan []
               backtrack-events []
               activations []
               mutations []
               branch-events nil
               emotion-shifts []
               emotional-state []
               situations {}
               goal-selection :highest_strength}}]
  (let [selected-goal (or selected-goal
                          (when goal-id
                            (goal-summary world goal-id)))
        context-id (or context-id
                       (:context-id selected-goal)
                       (when goal-id
                         (get-in world [:goals goal-id :next-cx]))
                       (:reality-lookahead world)
                       (:reality-context world))
        sprouted (or sprouted
                     (if (and context-id (contains? (:contexts world) context-id))
                       (->> (cx/children world context-id)
                            (sort-by str)
                            vec)
                       []))
        top-candidates (or top-candidates
                           (mapv #(goal-summary world %) top-candidate-ids))
        terminations (or terminations (:termination-events world) [])
        branch-events (or branch-events
                          (-> {:mutations (vec mutations)}
                              derive-branch-events))]
    {:cycle-num (:cycle world)
     :timestamp timestamp
     :mode (:mode world)
     :goal-selection goal-selection
     :selected-goal selected-goal
     :top-candidates top-candidates
     :context-id context-id
     :context-depth (context-depth world context-id)
     :sprouted sprouted
     :active-plan (vec active-plan)
     :active-indices (vec active-indices)
     :retrievals (vec retrievals)
     :episodic-retrievals (vec episodic-retrievals)
     :chosen-node-id chosen-node-id
     :selection selection
     :rule-provenance rule-provenance
     :feedback-applied feedback-applied
     :serendipity-bias serendipity-bias
     :situations situations
     :backtrack-events (vec backtrack-events)
     :activations (vec activations)
     :mutations (vec mutations)
     :branch-events (vec branch-events)
     :emotion-shifts (vec emotion-shifts)
     :emotional-state (vec emotional-state)
     :terminations (vec terminations)}))

(def ^:private reporter-cycle-known-keys
  #{:cycle-num
    :timestamp
    :mode
    :goal-selection
    :selected-goal
    :top-candidates
    :context-id
    :context-depth
    :sprouted
    :active-plan
    :active-indices
    :retrievals
    :episodic-retrievals
    :chosen-node-id
    :selection
    :rule-provenance
    :feedback-applied
    :serendipity-bias
    :situations
    :backtrack-events
    :activations
    :mutations
    :branch-events
    :emotion-shifts
    :emotional-state
    :terminations
    :runtime-thought
    :world-delta
    :world-debug-state})

(defn- episode-debug-summary
  [episode]
  {:episode-id (:id episode)
   :family (get-in episode [:provenance :family])
   :admission-status (:admission-status episode)
   :promotion-eligible? (boolean (:promotion-eligible? episode))
   :promotion-basis (:promotion-basis episode)
   :cycle-created (:cycle-created episode)
   :rule-path (vec (:rule-path episode))
   :retrieval-indices (vec (or (:content-indices episode)
                               (:indices episode)
                               []))
   :use-history (vec (:use-history episode))
   :promotion-evidence (vec (:promotion-evidence episode))
   :promotion-history (vec (:promotion-history episode))
   :anti-residue-history (vec (:anti-residue-history episode))
   :anti-residue-flags (vec (:anti-residue-flags episode))})

(defn- rule-access-debug-summary
  [[rule-id entry]]
  [rule-id {:rule-id rule-id
            :status (:status entry)
            :source (:source entry)
            :opened-cycle (:opened-cycle entry)
            :opened-by (:opened-by entry)
            :history (vec (:history entry))}])

(defn- world-debug-state
  [world]
  {:episodes (into {}
                   (map (fn [[episode-id episode]]
                          [episode-id (episode-debug-summary episode)]))
                   (:episodes world))
   :rule-access (into {}
                      (map rule-access-debug-summary)
                      (:rule-access world))
   :recent-indices (vec (:recent-indices world))})

(defn- episode-added-payload
  [episode]
  (select-keys episode
               [:episode-id
                :family
                :admission-status
                :promotion-eligible?
                :promotion-basis
                :cycle-created
                :rule-path
                :retrieval-indices]))

(defn- drop-prefix
  [previous current]
  (if (<= (count previous) (count current))
    (subvec current (count previous))
    current))

(defn- changed-use-records
  [previous current]
  (let [previous-by-id (into {} (map (juxt :use-id identity)) previous)]
    (->> current
         (keep (fn [record]
                 (let [prev-record (get previous-by-id (:use-id record))]
                   (when (and prev-record (not= prev-record record))
                     {:episode_id (:episode-id record)
                      :use_id (:use-id record)
                      :from_status (some-> (:status prev-record) scalar->json)
                      :to_status (some-> (:status record) scalar->json)
                      :outcome (some-> (:outcome record) scalar->json)
                      :resolved_cycle (:resolved-cycle record)
                      :source_family (some-> (:source-family record) scalar->json)
                      :target_family (some-> (:target-family record) scalar->json)
                      :reason (some-> (:outcome-reason record) scalar->json)}))))
         vec)))

(defn- world-delta
  [previous-state current-state]
  (let [previous-state (or previous-state
                           {:episodes {}
                            :rule-access {}
                            :recent-indices []})
        previous-episodes (:episodes previous-state)
        current-episodes (:episodes current-state)
        added-episode-ids (sort (set/difference (set (keys current-episodes))
                                                (set (keys previous-episodes))))
        common-episode-ids (sort (set/intersection (set (keys current-episodes))
                                                   (set (keys previous-episodes))))
        episodes-added (mapv (fn [episode-id]
                               (episode-added-payload
                                (get current-episodes episode-id)))
                             added-episode-ids)
        episode-status-changed
        (->> common-episode-ids
             (keep (fn [episode-id]
                     (let [before (get previous-episodes episode-id)
                           after (get current-episodes episode-id)]
                       (when (not= (:admission-status before)
                                   (:admission-status after))
                         {:episode_id episode-id
                          :family (some-> (:family after) scalar->json)
                          :from (some-> (:admission-status before) scalar->json)
                          :to (some-> (:admission-status after) scalar->json)}))))
             vec)
        episode-uses-added
        (->> (concat added-episode-ids common-episode-ids)
             (mapcat (fn [episode-id]
                       (let [before (get previous-episodes episode-id {:use-history []})
                             after (get current-episodes episode-id)
                             new-records (drop-prefix (vec (:use-history before))
                                                      (vec (:use-history after)))]
                         (map (fn [record]
                                {:episode_id episode-id
                                 :use_id (:use-id record)
                                 :status (some-> (:status record) scalar->json)
                                 :outcome (some-> (:outcome record) scalar->json)
                                 :cycle (:cycle record)
                                 :source_family (some-> (:source-family record) scalar->json)
                                 :target_family (some-> (:target-family record) scalar->json)
                                 :reason (some-> (:reason record) scalar->json)
                                 :outcome_reason (some-> (:outcome-reason record) scalar->json)
                                 :use_role (some-> (:use-role record) scalar->json)
                                 :goal_id (some-> (:goal-id record) scalar->json)})
                              new-records))))
             vec)
        episode-uses-resolved
        (->> common-episode-ids
             (mapcat (fn [episode-id]
                       (changed-use-records
                        (vec (:use-history (get previous-episodes episode-id)))
                        (vec (:use-history (get current-episodes episode-id))))))
             vec)
        promotion-evidence-added
        (->> (concat added-episode-ids common-episode-ids)
             (mapcat (fn [episode-id]
                       (let [before (get previous-episodes episode-id {:promotion-evidence []})
                             after (get current-episodes episode-id)
                             new-records (drop-prefix (vec (:promotion-evidence before))
                                                      (vec (:promotion-evidence after)))]
                         (map (fn [record]
                                {:episode_id episode-id
                                 :type (some-> (:type record) scalar->json)
                                 :cycle (:cycle record)
                                 :use_id (:use-id record)
                                 :source_family (some-> (:source-family record) scalar->json)
                                 :target_family (some-> (:target-family record) scalar->json)})
                              new-records))))
             vec)
        promotion-events
        (->> (concat added-episode-ids common-episode-ids)
             (mapcat (fn [episode-id]
                       (let [before (get previous-episodes episode-id {:promotion-history []})
                             after (get current-episodes episode-id)
                             new-records (drop-prefix (vec (:promotion-history before))
                                                      (vec (:promotion-history after)))]
                         (map (fn [record]
                                {:episode_id episode-id
                                 :from (some-> (:from-status record) scalar->json)
                                 :to (some-> (:to-status record) scalar->json)
                                 :cycle (:cycle record)
                                 :reason (some-> (:reason record) scalar->json)
                                 :source_family (some-> (:source-family record) scalar->json)
                                 :target_family (some-> (:target-family record) scalar->json)})
                              new-records))))
             vec)
        flag-events
        (->> (concat added-episode-ids common-episode-ids)
             (mapcat (fn [episode-id]
                       (let [before (get previous-episodes episode-id {:anti-residue-history []})
                             after (get current-episodes episode-id)
                             new-records (drop-prefix (vec (:anti-residue-history before))
                                                      (vec (:anti-residue-history after)))]
                         (map (fn [record]
                                {:episode_id episode-id
                                 :flag (some-> (:flag record) scalar->json)
                                 :cycle (:cycle record)
                                 :action (some-> (:action record) scalar->json)
                                 :reason (some-> (:reason record) scalar->json)
                                 :use_id (:use-id record)})
                              new-records))))
             vec)
        previous-rule-access (:rule-access previous-state)
        current-rule-access (:rule-access current-state)
        all-rule-ids (sort (set/union (set (keys previous-rule-access))
                                      (set (keys current-rule-access))))
        rule-access-changed
        (->> all-rule-ids
             (mapcat (fn [rule-id]
                       (let [before (get previous-rule-access rule-id {:history []})
                             after (get current-rule-access rule-id)]
                         (cond
                           (nil? after)
                           []

                           (not= (:status before) (:status after))
                           [{:rule_id (scalar->json rule-id)
                             :from (some-> (:status before) scalar->json)
                             :to (some-> (:status after) scalar->json)
                             :opened_cycle (:opened-cycle after)
                             :opened_by (some-> (:opened-by after) scalar->json)}]

                           :else
                           (->> (drop-prefix (vec (:history before))
                                             (vec (:history after)))
                                (map (fn [record]
                                       {:rule_id (scalar->json rule-id)
                                        :from (some-> (:from-status record) scalar->json)
                                        :to (some-> (:to-status record) scalar->json)
                                        :cycle (:cycle record)
                                        :reason (some-> (:reason record) scalar->json)
                                        :episode_id (some-> (:episode-id record) scalar->json)
                                        :branch_context_id (some-> (:branch-context-id record)
                                                                   scalar->json)}))
                                vec)))))
             vec)
        previous-recent (vec (:recent-indices previous-state))
        current-recent (vec (:recent-indices current-state))]
    {:episodes {:added episodes-added
                :status_changed episode-status-changed}
     :episode_use {:added episode-uses-added
                   :resolved episode-uses-resolved}
     :promotion {:evidence_added promotion-evidence-added
                 :events promotion-events}
     :flags {:events flag-events}
     :rule_access {:changed rule-access-changed}
     :recent_indices {:pushed (vec (remove (set previous-recent) current-recent))
                      :evicted (vec (remove (set current-recent) previous-recent))}}))

(defn- attach-world-debug
  [snapshot previous-state current-world]
  (let [current-state (world-debug-state current-world)]
    (assoc snapshot
           :world-debug-state current-state
           :world-delta (world-delta previous-state current-state))))

(defn append-cycle
  "Append a cycle snapshot to the world's trace."
  [world snapshot-opts]
  (let [previous-state (some-> world :trace last :world-debug-state)
        snapshot (-> (cycle-snapshot world snapshot-opts)
                     (attach-world-debug previous-state world))]
    (update world :trace (fnil conj []) snapshot)))

(defn- merge-snapshot-fields
  [snapshot snapshot-fields]
  (let [merged (merge-with (fn [left right]
                             (if (and (map? left) (map? right))
                               (merge left right)
                               right))
                           snapshot
                           snapshot-fields)]
    (if (contains? snapshot-fields :mutations)
      (assoc merged :branch-events (derive-branch-events merged))
      merged)))

(defn merge-latest-cycle
  "Merge additional fields into the latest trace snapshot. If no snapshot
  exists yet, append a new one instead."
  [world snapshot-fields]
  (if (seq (:trace world))
    (update world :trace
            (fn [trace]
              (let [last-idx (dec (count trace))]
                (update trace last-idx
                        (fn [snapshot]
                          (-> (merge-snapshot-fields snapshot snapshot-fields)
                              (attach-world-debug
                               (some-> trace butlast last :world-debug-state)
                               world)))))))
    (append-cycle world snapshot-fields)))

(defn reporter-cycle
  "Project an internal snapshot into the cycle shape expected by the existing
  Python HTML reporter."
  [snapshot]
  (let [snapshot (if (contains? snapshot :branch-events)
                   snapshot
                   (assoc snapshot :branch-events (derive-branch-events snapshot)))
        extra-debug (json-value (apply dissoc snapshot reporter-cycle-known-keys))]
    {"cycle" (:cycle-num snapshot)
     "timestamp" (:timestamp snapshot)
     "goal_selection" (scalar->json (:goal-selection snapshot))
     "selected_goal" (reporter-goal (:selected-goal snapshot))
     "top_candidates" (mapv #(assoc (reporter-goal %)
                                    "reasons"
                                    (json-value (:reasons % [])))
                            (:top-candidates snapshot))
     "active_indices" (json-value (:active-indices snapshot))
     "retrieved" (mapv (fn [hit]
                         (cond-> {"node_id" (or (some-> (:node-id hit) scalar->json)
                                                (some-> (:episode-id hit) scalar->json)
                                                "n/a")
                                  "episode_id" (some-> (:episode-id hit) scalar->json)
                                  "retrieval_score" (or (:retrieval-score hit)
                                                        (:marks hit)
                                                        0.0)
                                  "overlap" (json-value (or (:overlap hit) []))
                                  "threshold" (:threshold hit)}
                           (:origin hit)
                           (assoc "origin" (some-> (:origin hit) scalar->json))

                           (:source-family hit)
                           (assoc "source_family" (some-> (:source-family hit)
                                                          scalar->json))

                           (:selection-policy hit)
                           (assoc "selection_policy" (some-> (:selection-policy hit)
                                                             scalar->json))

                           (:selection-reasons hit)
                           (assoc "selection_reasons" (json-value (:selection-reasons hit)))

                           (:frame-id hit)
                           (assoc "frame_id" (some-> (:frame-id hit) scalar->json))

                           (:goal-id hit)
                           (assoc "goal_id" (some-> (:goal-id hit) scalar->json))

                           (:source-id hit)
                           (assoc "source_id" (some-> (:source-id hit) scalar->json))

                           (contains? hit :frontier-bridge?)
                           (assoc "frontier_bridge" (boolean (:frontier-bridge? hit)))

                           (:repair-target hit)
                           (assoc "repair_target" (some-> (:repair-target hit)
                                                          scalar->json))

                           (:admission-status hit)
                           (assoc "admission_status" (some-> (:admission-status hit)
                                                             scalar->json))

                           (:rule-path hit)
                           (assoc "rule_path" (json-value (:rule-path hit)))

                           (:candidate-rank hit)
                           (assoc "candidate_rank" (:candidate-rank hit))

                           (:breakdown-surface-overlap-count hit)
                           (assoc "breakdown_surface_overlap_count"
                                  (:breakdown-surface-overlap-count hit))))
                       (:retrievals snapshot))
     "retrieved_episodes" (mapv (fn [hit]
                                  {"episode_id" (some-> (:episode-id hit) scalar->json)
                                   "retrieval_score" (or (:retrieval-score hit)
                                                         (:marks hit)
                                                         0.0)
                                   "overlap" (json-value (or (:overlap hit) []))
                                   "threshold" (:threshold hit)
                                   "episode_created_cycle" (:episode-created-cycle hit)
                                   "same_cycle" (boolean (:same-cycle? hit))
                                   "admission_status" (some-> (:admission-status hit)
                                                              scalar->json)
                                   "provenance_reason" (some-> (:provenance-reason hit)
                                                               scalar->json)
                                   "retention_reason" (some-> (:retention-reason hit)
                                                              scalar->json)})
                                (:episodic-retrievals snapshot))
     "chosen_node_id" (or (:chosen-node-id snapshot) "n/a")
     "selection" (json-value (:selection snapshot))
     "rule_provenance" (provenance-json-value (:rule-provenance snapshot))
     "sprouted_contexts" (json-value (:sprouted snapshot))
     "activations" (mapv reporter-activation (:activations snapshot))
     "mutations" (json-value (:mutations snapshot))
     "branch_events" (mapv reporter-branch-event (:branch-events snapshot))
     "emotion_shifts" (mapv reporter-emotion-shift (:emotion-shifts snapshot))
     "emotional_state" (mapv reporter-emotional-state (:emotional-state snapshot))
     "feedback_applied" (json-value (:feedback-applied snapshot))
     "runtime_thought" (json-value (:runtime-thought snapshot))
     "serendipity_bias" (:serendipity-bias snapshot)
     "situations" (json-value (:situations snapshot))
     "world_delta" (json-value (:world-delta snapshot))
     "debug" extra-debug}))

(defn reporter-log
  "Project the world's trace into the top-level shape expected by the existing
  Python reporter."
  [world metadata]
  (let [metadata (json-value metadata)]
    (into {"cycles" (mapv reporter-cycle (:trace world))}
          (map (fn [key]
                 [key (or (get metadata key)
                          (get metadata (keyword key)))])
               reporter-top-level-keys))))

(defn dreamer-state-packet
  "Project a cycle snapshot into a reduced Director-facing packet."
  [snapshot]
  {"mode" (some-> (:mode snapshot) scalar->json)
   "goal_type" (some-> snapshot :selected-goal :goal-type scalar->json)
   "active_indices" (json-value (:active-indices snapshot))
   "retrieved_episodes" (mapv #(scalar->json (:episode-id %))
                              (:retrievals snapshot))
   "active_plan_chain" (json-value (:active-plan snapshot))
   "episode_cause" (:episode-cause snapshot)
   "trace_context_id" (some-> (:context-id snapshot) scalar->json)})

(comment
  (cycle-snapshot {:cycle 1
                   :mode :daydreaming
                   :goals {}
                   :contexts {}
                   :trace []}
                  {:selected-goal {:id :g-1
                                   :goal-type :reversal
                                   :strength 0.8}}))
