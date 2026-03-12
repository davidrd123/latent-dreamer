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
  (:require [daydreamer.context :as cx]))

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
  {"id" (some-> (:id goal) scalar->json)
   "goal_type" (some-> (:goal-type goal) scalar->json)
   "strength" (:strength goal)
   "planning_type" (some-> (:planning-type goal) scalar->json)
   "situation_id" (some-> (:situation-id goal) scalar->json)})

(defn cycle-snapshot
  "Build an internal cycle snapshot. Fields that are not yet produced by the
  kernel can be supplied explicitly by the caller."
  [world {:keys [goal-id selected-goal top-candidate-ids top-candidates
                 active-indices retrievals chosen-node-id selection
                 feedback-applied serendipity-bias situations context-id
                 sprouted active-plan backtrack-events mutations
                 terminations timestamp goal-selection]
          :or {active-indices []
               retrievals []
               active-plan []
               backtrack-events []
               mutations []
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
        terminations (or terminations (:termination-events world) [])]
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
     :chosen-node-id chosen-node-id
     :selection selection
     :feedback-applied feedback-applied
     :serendipity-bias serendipity-bias
     :situations situations
     :backtrack-events (vec backtrack-events)
     :mutations (vec mutations)
     :terminations (vec terminations)}))

(defn append-cycle
  "Append a cycle snapshot to the world's trace."
  [world snapshot-opts]
  (update world :trace (fnil conj []) (cycle-snapshot world snapshot-opts)))

(defn- merge-snapshot-fields
  [snapshot snapshot-fields]
  (merge-with (fn [left right]
                (if (and (map? left) (map? right))
                  (merge left right)
                  right))
              snapshot
              snapshot-fields))

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
                          (merge-snapshot-fields snapshot snapshot-fields))))))
    (append-cycle world snapshot-fields)))

(defn reporter-cycle
  "Project an internal snapshot into the cycle shape expected by the existing
  Python HTML reporter."
  [snapshot]
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
                       {"node_id" (or (some-> (:node-id hit) scalar->json)
                                      (some-> (:episode-id hit) scalar->json)
                                      "n/a")
                        "episode_id" (some-> (:episode-id hit) scalar->json)
                        "retrieval_score" (or (:retrieval-score hit)
                                              (:marks hit)
                                              0.0)
                        "overlap" (json-value (or (:overlap hit) []))
                        "threshold" (:threshold hit)})
                     (:retrievals snapshot))
   "chosen_node_id" (or (:chosen-node-id snapshot) "n/a")
   "selection" (json-value (:selection snapshot))
   "feedback_applied" (json-value (:feedback-applied snapshot))
   "serendipity_bias" (:serendipity-bias snapshot)
   "situations" (json-value (:situations snapshot))})

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
