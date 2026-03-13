(ns daydreamer.benchmarks.puppet-knows-adapter
  "Translate branch-local kernel facts into Puppet Knows trace state.

  This is not Mueller core logic. It is the conducted-system adapter that turns
  branch-visible facts into the benchmark's situation and node vocabulary."
  (:require [daydreamer.context :as cx]
            [daydreamer.trace :as trace]))

(def ^:private branch-fact->situation-deltas
  {:s4_the_ring {:s4_the_ring {:activation 0.14
                               :ripeness 0.09
                               :hope 0.08}
                 :s1_seeing_through {:activation -0.08
                                     :threat -0.05}}
   :performance_is_admitted {:s4_the_ring {:activation 0.06
                                           :ripeness 0.05
                                           :hope 0.06}
                             :s1_seeing_through {:activation -0.05
                                                 :anger -0.05}}
   :seam_is_honesty {:s1_seeing_through {:activation 0.10
                                         :ripeness 0.07
                                         :hope 0.11
                                         :threat -0.08}
                     :s4_the_ring {:activation 0.03
                                   :hope 0.04}}
   :ring_is_honest_stage {:s4_the_ring {:activation 0.11
                                        :ripeness 0.08
                                        :hope 0.12}
                          :s1_seeing_through {:activation -0.04
                                              :anger -0.04}}
   :present_is_reframed {:s1_seeing_through {:hope 0.05
                                             :threat -0.04}}})

(def ^:private branch-fact->indices
  {:s4_the_ring [:ritual :sincerity :non_directed_light]
   :performance_is_admitted [:performance :honesty]
   :seam_is_honesty [:honesty :clarity]
   :ring_is_honest_stage [:ritual :honesty :performance]
   :present_is_reframed [:clarity]})

(def ^:private ordered-bridge-facts
  [:s4_the_ring
   :performance_is_admitted
   :ring_is_honest_stage
   :seam_is_honesty
   :present_is_reframed])

(def ^:private honest-ring-overlap
  [:honesty :performance :ritual])

(declare branch-derived-state)

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
  (or (get-in world [:trace (latest-trace-index world) :selection :reversal_branch_context])
      (get-in world [:trace (latest-trace-index world) :selection :roving_branch_context])
      (get-in world [:trace (latest-trace-index world) :selection :rationalization_branch_context])))

(defn- fact-id
  [fact]
  (when (contains? #{:situation :counterfactual :rationalization} (:fact/type fact))
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
  (or (when (contains? fact-ids :s4_the_ring)
        :s4_the_ring)
      (when (contains? fact-ids :ring_is_honest_stage)
        :s4_the_ring)
      (when (contains? fact-ids :seam_is_honesty)
        :s1_seeing_through)
      (when-let [[situation-id _]
                 (first
                  (sort-by (fn [[candidate-id situation-state]]
                             [(- (double (or (:activation situation-state) 0.0)))
                              (str candidate-id)])
                           situations))]
        situation-id)))

(defn- derived-active-indices
  [fact-ids]
  (->> ordered-bridge-facts
       (filter fact-ids)
       (mapcat #(get branch-fact->indices % []))
       distinct
       vec))

(defn- honest-ring-arrival?
  [situation-id active-indices]
  (and (= situation-id :s4_the_ring)
       (every? (set active-indices) honest-ring-overlap)))

(defn- derived-retrievals
  [situation-id active-indices]
  (if (honest-ring-arrival? situation-id active-indices)
    [{:node-id "n10_honest_ring"
      :episode-id :ep-10
      :marks 3
      :threshold 2
      :overlap honest-ring-overlap}]
    []))

(defn apply-branch-derived-state
  "Project branch-visible facts into Puppet Knows situation and node state.

  This is intentionally bounded to the benchmark's known bridge:
  counterfactual admission plus `s4_the_ring` should move the cycle toward the
  honest-ring arrival."
  [world selected-goal-id _script]
  (trace/merge-latest-cycle
   world
   (if-let [derived-state (branch-derived-state world)]
     (let [{:keys [selection situations active-indices retrievals chosen-node-id
                   selected-situation-id]} derived-state]
       (cond-> {:selection selection
                :situations situations
                :active-indices active-indices}
         selected-situation-id
         (assoc :selected-goal {:id selected-goal-id
                                :situation-id selected-situation-id})

         (seq retrievals)
         (assoc :retrievals retrievals
                :chosen-node-id chosen-node-id)))
     {:selection {:adapter_policy :no_branch_context
                  :adapter_selected_situation nil
                  :adapter_active_indices []}})))

(defn branch-derived-state
  "Return benchmark-specific situation/index consequences of the current branch.

  When `situations` is omitted, the previous cycle's situation state is used,
  matching the scripted benchmark behavior. Autonomous runs can pass the current
  situation map explicitly."
  ([world]
   (branch-derived-state world (previous-cycle-situations world)))
  ([world situations]
   (when-let [context-id (branch-context-id world)]
     (let [fact-ids (branch-fact-ids world context-id)
           situations (apply-situation-deltas situations fact-ids)
           situation-id (derived-situation-id fact-ids situations)
           active-indices (derived-active-indices fact-ids)
           retrievals (derived-retrievals situation-id active-indices)
           chosen-node-id (some-> retrievals first :node-id)
           selection-updates {:adapter_policy :branch_visible_facts
                              :adapter_branch_context context-id
                              :adapter_visible_fact_ids (->> fact-ids
                                                             (map name)
                                                             sort
                                                             vec)
                              :adapter_selected_situation situation-id
                              :adapter_active_indices active-indices}
           selection-updates (cond-> selection-updates
                               chosen-node-id
                               (assoc :edge_kind :counterfactual_bridge
                                      :benchmark_step :cycle_10_branch_arrival))]
       {:selection selection-updates
        :situations situations
        :active-indices active-indices
        :retrievals retrievals
        :chosen-node-id chosen-node-id
        :selected-situation-id situation-id}))))
