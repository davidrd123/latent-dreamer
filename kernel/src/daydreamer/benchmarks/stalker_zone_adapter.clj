(ns daydreamer.benchmarks.stalker-zone-adapter
  "Translate rationalization branch facts into Stalker Zone trace state.

  This is benchmark-local adapter code. The kernel produces rationalization
  branch facts; this namespace projects them into the conducted benchmark's
  situation and node vocabulary."
  (:require [daydreamer.context :as cx]
            [daydreamer.trace :as trace]))

(def ^:private branch-fact->situation-deltas
  {:s5_the_guide {:s5_the_guide {:activation 0.24
                                 :ripeness 0.12
                                 :hope 0.13}
                  :s4_the_room {:activation -0.07
                                :threat -0.06}}
   :zone_is_mercy {:s5_the_guide {:activation 0.11
                                  :ripeness 0.07
                                  :hope 0.10}
                   :s4_the_room {:activation -0.05
                                 :threat -0.05}}
   :delay_is_faith {:s5_the_guide {:activation 0.09
                                   :ripeness 0.08
                                   :hope 0.09}
                    :s1_the_approach {:activation -0.03}
                    :s4_the_room {:activation -0.04
                                  :threat -0.04}}})

(def ^:private branch-fact->indices
  {:s5_the_guide [:guide :sincerity]
   :zone_is_mercy [:faith :guide]
   :delay_is_faith [:sincerity :trust]})

(def ^:private ordered-bridge-facts
  [:s5_the_guide :zone_is_mercy :delay_is_faith])

(def ^:private zone-arrival-overlap
  [:faith :guide :sincerity])

(def ^:private ordered-arrival-indices
  [:faith :guide :sincerity :trust])

(defn- latest-trace-index
  [world]
  (dec (count (:trace world))))

(defn- previous-cycle-situations
  [world]
  (if (> (count (:trace world)) 1)
    (or (get-in world [:trace (- (count (:trace world)) 2) :situations])
        {})
    {}))

(defn- branch-context-id
  [world]
  (get-in world [:trace (latest-trace-index world) :selection :rationalization_branch_context]))

(defn- fact-id
  [fact]
  (when (contains? #{:situation :rationalization} (:fact/type fact))
    (:fact/id fact)))

(defn- branch-fact-ids
  [world context-id]
  (->> (cx/visible-facts world context-id)
       (keep fact-id)
       set))

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
  [situations fact-ids]
  (reduce (fn [current-situations fact-id]
            (reduce-kv (fn [next-situations situation-id metric-deltas]
                         (update next-situations
                                 situation-id
                                 (fn [state]
                                   (apply-metric-deltas (or state {}) metric-deltas))))
                       current-situations
                       (get branch-fact->situation-deltas fact-id {})))
          situations
          fact-ids))

(defn- derived-situation-id
  [fact-ids situations]
  (or (when (contains? fact-ids :s5_the_guide)
        :s5_the_guide)
      (when-let [[situation-id _]
                 (first
                  (sort-by (fn [[candidate-id situation-state]]
                             [(- (double (or (:activation situation-state) 0.0)))
                              (str candidate-id)])
                           situations))]
        situation-id)))

(defn- derived-active-indices
  [fact-ids]
  (let [active-index-set (->> ordered-bridge-facts
                              (filter fact-ids)
                              (mapcat #(get branch-fact->indices % []))
                              set)]
    (->> ordered-arrival-indices
         (filter active-index-set)
         vec)))

(defn- zone-arrival?
  [situation-id active-indices]
  (and (= situation-id :s5_the_guide)
       (every? (set active-indices) zone-arrival-overlap)))

(defn- derived-retrievals
  [situation-id active-indices]
  (if (zone-arrival? situation-id active-indices)
    [{:node-id "n07_zone_is_mercy"
      :episode-id :ep-7
      :marks 3
      :threshold 2
      :overlap zone-arrival-overlap}]
    []))

(defn apply-branch-derived-state
  "Project rationalization branch facts into Stalker Zone situation and node
  state."
  [world selected-goal-id _script]
  (if-let [context-id (branch-context-id world)]
    (let [fact-ids (branch-fact-ids world context-id)
          situations (apply-situation-deltas (previous-cycle-situations world)
                                             fact-ids)
          situation-id (derived-situation-id fact-ids situations)
          active-indices (derived-active-indices fact-ids)
          retrievals (derived-retrievals situation-id active-indices)
          chosen-node-id (some-> retrievals first :node-id)
          selection-updates (cond-> {:adapter_policy :branch_visible_facts
                                     :adapter_branch_context context-id
                                     :adapter_visible_fact_ids (->> fact-ids
                                                                    (map name)
                                                                    sort
                                                                    vec)
                                     :adapter_selected_situation situation-id
                                     :adapter_active_indices active-indices}
                              chosen-node-id
                              (assoc :edge_kind :reinterpretation_bridge
                                     :benchmark_step :cycle_7_rationalization_arrival))]
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
     {:selection {:adapter_policy :no_branch_context
                  :adapter_selected_situation nil
                  :adapter_active_indices []}})))
