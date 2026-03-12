(ns daydreamer.runner
  "Tiny orchestration layer for scripted kernel sessions.

  This namespace is intentionally small. It does not implement the missing
  planner; it provides a pure harness that can:

  - build an initial world
  - activate top-level goals
  - run control cycles
  - apply scripted planner/retrieval outcomes
  - export a reporter-shaped log through `daydreamer.trace`"
  (:require [daydreamer.context :as cx]
            [daydreamer.control :as control]
            [daydreamer.goals :as goals]
            [daydreamer.trace :as trace]))

(defn initial-world
  "Create a minimal kernel world with a single root context."
  []
  (let [root (cx/create-context)
        root-id (:id root)]
    {:contexts {root-id root}
     :goals {}
     :episodes {}
     :episode-index {}
     :emotions {}
     :needs {}
     :mode :daydreaming
     :cycle 0
     :trace []
     :recent-indices []
     :recent-episodes []
     :retrieval-events []
     :backtrack-events []
     :mutation-events []
     :termination-events []
     :reality-context root-id
     :reality-lookahead root-id
     :id-counter 1}))

(defn activate-goals
  "Activate a sequence of goal specs, returning `[world goal-ids]`."
  [world context-id goal-specs]
  (reduce (fn [[current-world goal-ids] goal-spec]
            (let [[next-world goal-id]
                  (goals/activate-top-level-goal current-world context-id goal-spec)]
              [next-world (conj goal-ids goal-id)]))
          [world []]
          goal-specs))

(defn- goal-context
  [world goal-id]
  (goals/get-next-context world goal-id))

(defn- create-scripted-sprouts
  [world goal-id sprout-specs]
  (reduce (fn [[current-world child-ids] {:keys [ordering timeout]
                                          :as sprout-spec}]
            (let [[next-world child-id] (cx/sprout current-world
                                                   (goal-context current-world goal-id))
                  next-world (cond-> next-world
                               (contains? sprout-spec :ordering)
                               (assoc-in [:contexts child-id :ordering] ordering)

                               (contains? sprout-spec :timeout)
                               (assoc-in [:contexts child-id :timeout] timeout))]
              [next-world (conj child-ids child-id)]))
          [world []]
          sprout-specs))

(defn- enrich-latest-trace
  [world script]
  (trace/merge-latest-cycle
   world
   (cond-> {}
     (:timestamp script)
     (assoc :timestamp (:timestamp script))

     (:active-indices script)
     (assoc :active-indices (vec (:active-indices script)))

     (:retrievals script)
     (assoc :retrievals (vec (:retrievals script)))

     (contains? script :chosen-node-id)
     (assoc :chosen-node-id (:chosen-node-id script))

     (contains? script :selection)
     (assoc :selection (:selection script))

     (contains? script :feedback-applied)
     (assoc :feedback-applied (:feedback-applied script))

     (contains? script :serendipity-bias)
     (assoc :serendipity-bias (:serendipity-bias script))

     (:situations script)
     (assoc :situations (:situations script)))))

(defn run-scripted-cycle
  "Run one scripted control cycle.

  Script keys:
  - `:timestamp`, `:active-indices`, `:retrievals`, `:chosen-node-id`,
    `:selection`, `:feedback-applied`, `:serendipity-bias`, `:situations`
    -> merged into the cycle trace
  - `:sprouts` -> vector of child context specs, each with optional
    `:ordering` / `:timeout`
  - `:goal-status` -> applied to the selected goal before `run-goal-step`
  - `:step?` -> when true, execute `control/run-goal-step`
  - `:terminate` -> map accepted by `control/terminate-top-level-goal`"
  [world script]
  (let [[world selected-goal-id] (control/run-cycle world)
        world (enrich-latest-trace world script)]
    (if-not selected-goal-id
      [world nil]
      (let [[world sprout-ids]
            (create-scripted-sprouts world selected-goal-id (:sprouts script []))
            world (if (seq sprout-ids)
                    (trace/merge-latest-cycle world {:sprouted sprout-ids})
                    world)
            world (if-let [goal-status (:goal-status script)]
                    (goals/change-status world selected-goal-id goal-status)
                    world)
            [world next-context-id]
            (if (:step? script)
              (control/run-goal-step world selected-goal-id)
              [world (goals/get-next-context world selected-goal-id)])
            resolution-cx (or (get-in script [:terminate :resolution-cx])
                              next-context-id
                              (goals/get-next-context world selected-goal-id))
            world (if-let [termination (:terminate script)]
                    (control/terminate-top-level-goal world
                                                      selected-goal-id
                                                      resolution-cx
                                                      (dissoc termination :resolution-cx))
                    world)]
        [world selected-goal-id]))))

(defn run-scripted-session
  "Run a sequence of scripted cycles and return both the final world and a
  reporter-shaped log.

  Input:
  - `world`: initial kernel world
  - `scripts`: vector of cycle directives for `run-scripted-cycle`
  - `metadata`: top-level metadata for `trace/reporter-log`

  Output:
  `{:world final-world :log reporter-log}`"
  [world scripts metadata]
  (let [final-world (reduce (fn [current-world script]
                              (first (run-scripted-cycle current-world script)))
                            world
                            scripts)]
    {:world final-world
     :log (trace/reporter-log final-world metadata)}))

(comment
  (let [[world _]
        (activate-goals
         (initial-world)
         :cx-1
         [{:goal-type :reversal
           :planning-type :imaginary
           :strength 0.9
           :main-motiv :e-1
           :situation-id :s1_seeing_through}])]
    (run-scripted-session
     world
     [{:timestamp "2026-03-12T12:00:00Z"
       :active-indices [:s1_seeing_through :reversal]
       :sprouts [{:ordering 0.9} {:ordering 0.4}]
       :step? true
       :chosen-node-id "n09_tear_the_set"
       :situations {:s1_seeing_through {:activation 0.9
                                        :ripeness 0.8}}}]
     {:started_at "2026-03-12T12:00:00Z"
      :engine_path "kernel/src/daydreamer"})))
