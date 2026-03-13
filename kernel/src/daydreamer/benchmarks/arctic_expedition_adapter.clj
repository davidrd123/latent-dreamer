(ns daydreamer.benchmarks.arctic-expedition-adapter
  "Translate ROVING reminder output into Arctic Expedition trace state.

  This is a benchmark-local adapter. The kernel exposes reminded indices and
  branch choice; this namespace projects those signals into the conducted
  benchmark's situation and node vocabulary."
  (:require [daydreamer.trace :as trace]))

(def ^:private index->situation-deltas
  {:aurora {:s4_the_horizon {:activation 0.22
                             :ripeness 0.10
                             :hope 0.16}
            :s3_the_wait {:activation -0.08
                          :threat -0.06}}
   :light {:s4_the_horizon {:activation 0.14
                            :ripeness 0.06
                            :hope 0.11}
           :s5_the_hull {:activation -0.04
                         :threat -0.03}}
   :horizon {:s4_the_horizon {:activation 0.12
                              :ripeness 0.08
                              :hope 0.08}
             :s1_the_ship {:activation -0.03}}
   :wonder {:s4_the_horizon {:activation 0.09
                             :ripeness 0.09
                             :hope 0.12}
            :s3_the_wait {:threat -0.04}}})

(def ^:private ordered-roving-indices
  [:aurora :light :horizon :wonder])

(def ^:private aurora-arrival-overlap
  [:aurora :light :horizon])

(defn- latest-trace-index
  [world]
  (dec (count (:trace world))))

(defn- previous-cycle-situations
  [world]
  (if (> (count (:trace world)) 1)
    (or (get-in world [:trace (- (count (:trace world)) 2) :situations])
        {})
    {}))

(defn- roving-active-indices
  [world]
  (or (get-in world [:trace (latest-trace-index world) :selection :roving_active_indices])
      []))

(defn- roving-reminded-episode-id
  [world]
  (first (get-in world [:trace (latest-trace-index world) :selection :roving_reminded_episodes])))

(defn- clamp01
  [value]
  (-> value
      (max 0.0)
      (min 1.0)))

(defn- apply-metric-deltas
  [situation-state metric-deltas]
  (reduce-kv (fn [state metric delta]
               (update state metric
                       (fn [current]
                         (clamp01 (+ (double (or current 0.0))
                                     (double delta))))))
             situation-state
             metric-deltas))

(defn- apply-situation-deltas
  [situations active-indices]
  (reduce (fn [current-situations index]
            (reduce-kv (fn [next-situations situation-id metric-deltas]
                         (update next-situations
                                 situation-id
                                 (fn [state]
                                   (apply-metric-deltas (or state {}) metric-deltas))))
                       current-situations
                       (get index->situation-deltas index {})))
          situations
          active-indices))

(defn- derived-situation-id
  [active-index-set situations]
  (or (when (every? active-index-set aurora-arrival-overlap)
        :s4_the_horizon)
      (when-let [[situation-id _]
                 (first
                  (sort-by (fn [[candidate-id situation-state]]
                             [(- (double (or (:activation situation-state) 0.0)))
                              (str candidate-id)])
                           situations))]
        situation-id)))

(defn- ordered-active-indices
  [active-indices]
  (let [active-index-set (set active-indices)]
    (->> ordered-roving-indices
         (filter active-index-set)
         vec)))

(defn- derived-retrievals
  [situation-id reminded-episode-id active-index-set]
  (if (and (= situation-id :s4_the_horizon)
           (every? active-index-set aurora-arrival-overlap))
    [{:node-id "n07_aurora_watch"
      :episode-id reminded-episode-id
      :marks 3
      :threshold 2
      :overlap aurora-arrival-overlap}]
    []))

(defn apply-reminding-derived-state
  "Project ROVING reminding output into Arctic Expedition situation and node
  state."
  [world selected-goal-id _script]
  (let [raw-active-indices (roving-active-indices world)
        active-index-set (set raw-active-indices)
        active-indices (ordered-active-indices raw-active-indices)]
    (if (seq active-indices)
      (let [situations (apply-situation-deltas (previous-cycle-situations world)
                                               active-indices)
            situation-id (derived-situation-id active-index-set situations)
            reminded-episode-id (roving-reminded-episode-id world)
            retrievals (derived-retrievals situation-id
                                           reminded-episode-id
                                           active-index-set)
            chosen-node-id (some-> retrievals first :node-id)
            selection-updates (cond-> {:adapter_policy :roving_reminding_indices
                                       :adapter_active_indices active-indices
                                       :adapter_selected_situation situation-id}
                                reminded-episode-id
                                (assoc :adapter_reminded_episode reminded-episode-id)

                                chosen-node-id
                                (assoc :edge_kind :reminding_bridge
                                       :benchmark_step :cycle_7_aurora_arrival))]
        (trace/merge-latest-cycle
         world
         (cond-> {:selection selection-updates
                  :situations situations
                  :active-indices active-indices}
           situation-id
           (assoc :selected-goal {:id selected-goal-id
                                  :situation-id situation-id})

           (seq retrievals)
           (assoc :retrievals retrievals
                  :chosen-node-id chosen-node-id))))
      (trace/merge-latest-cycle
       world
       {:selection {:adapter_policy :no_roving_indices
                    :adapter_selected_situation nil
                    :adapter_active_indices []}}))))
