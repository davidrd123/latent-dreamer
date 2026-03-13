(ns daydreamer.runner
  "Tiny orchestration layer for scripted kernel sessions.

  This namespace is intentionally small. It does not implement the missing
  planner; it provides a pure harness that can:

  - build an initial world
  - activate top-level goals
  - run control cycles
  - apply scripted planner/retrieval outcomes
  - invoke goal-family primitives from scripted fixtures
  - export a reporter-shaped log through `daydreamer.trace`"
  (:require [daydreamer.context :as cx]
            [daydreamer.control :as control]
            [daydreamer.goal-families :as families]
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
     :roving-episodes []
     :auto-activate-family-goals? false
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

(defn- goal-type
  [world goal-id]
  (get-in world [:goals goal-id :goal-type]))

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

(defn- append-sprouted-contexts
  [world sprout-ids]
  (if (seq sprout-ids)
    (let [existing-sprouts (or (get-in world [:trace (dec (count (:trace world))) :sprouted])
                               [])
          merged-sprouts (->> (concat existing-sprouts sprout-ids)
                              distinct
                              vec)]
      (trace/merge-latest-cycle world {:sprouted merged-sprouts}))
    world))

(defn- resolve-reversal-branch
  [world reversal-spec]
  (cond
    (nil? reversal-spec)
    nil

    (or (:discover-leaf? reversal-spec)
        (nil? (:old-context-id reversal-spec)))
    (let [selected-leaf (families/select-reversal-leaf world)]
      (when-not selected-leaf
        (throw (ex-info "No reversal leaf candidates available"
                        {:reversal-spec reversal-spec})))
      (merge selected-leaf reversal-spec))

    :else
    reversal-spec))

(defn- resolve-reversal-counterfactuals
  [world reversal-spec]
  (cond
    (nil? reversal-spec)
    nil

    (:derive-counterfactuals? reversal-spec)
    (if-let [derived-cause (families/reverse-undo-causes world reversal-spec)]
      (merge (dissoc reversal-spec :derive-counterfactuals?)
             derived-cause)
      (throw (ex-info "No reversal counterfactual causes available"
                      {:reversal-spec reversal-spec})))

    :else
    reversal-spec))

(defn- apply-scripted-reversal
  [world selected-goal-id reversal-spec]
  (if-not reversal-spec
    [world nil]
    (let [reversal-spec (resolve-reversal-branch world reversal-spec)
          reversal-spec (resolve-reversal-counterfactuals world reversal-spec)
          new-top-level-goal-id (or (:new-top-level-goal-id reversal-spec)
                                    selected-goal-id)
          new-context-id (or (:new-context-id reversal-spec)
                             (goal-context world new-top-level-goal-id))]
      (when-not new-context-id
        (throw (ex-info "Scripted reversal branch needs a target planning context"
                        {:selected-goal-id selected-goal-id
                         :new-top-level-goal-id new-top-level-goal-id
                         :reversal-spec reversal-spec})))
      (let [[world branch-results]
            (if (:discover-leaf? reversal-spec)
              (families/reverse-leafs
               world
               (assoc reversal-spec
                      :new-top-level-goal-id new-top-level-goal-id
                      :new-context-id new-context-id))
              (let [[next-world sprouted-context-id]
                    (families/reversal-sprout-alternative
                     world
                     (assoc reversal-spec
                            :new-top-level-goal-id new-top-level-goal-id
                            :new-context-id new-context-id))]
                [next-world
                 [{:sprouted-context-id sprouted-context-id
                   :old-context-id (:old-context-id reversal-spec)
                   :leaf-goal-id (:failed-goal-id reversal-spec)
                   :selection-policy :fixture_selected
                   :selection-reasons [:fixture_selected]
                   :retracted-fact-ids []}]]))
            primary-branch (or (first branch-results)
                               (throw (ex-info "No weak reversal leaf branches available"
                                               {:reversal-spec reversal-spec})))
            sprouted-context-ids (mapv :sprouted-context-id branch-results)
            world (append-sprouted-contexts world sprouted-context-ids)
            world (trace/merge-latest-cycle
                   world
                   {:mutations (:mutation-events world)
                    :selection {:goal_family :reversal
                                :family_goal_id new-top-level-goal-id
                                :reversal_target_policy (or (:selection-policy reversal-spec)
                                                            :fixture_selected)
                                :reversal_target_goal (:old-top-level-goal-id reversal-spec)
                                :reversal_source_context (:old-context-id primary-branch)
                                :reversal_leaf_policy (:selection-policy primary-branch)
                                :reversal_leaf_goal (:leaf-goal-id primary-branch)
                                :reversal_leaf_strength (:leaf-strength primary-branch)
                                :reversal_leaf_depth (:context-depth reversal-spec)
                                :reversal_leaf_emotion_pressure (:emotion-pressure reversal-spec)
                                :reversal_leaf_reasons (:selection-reasons primary-branch)
                                :reversal_leaf_retracted_facts (:retracted-fact-ids primary-branch)
                                :reversal_branch_count (count branch-results)
                                :reversal_branch_contexts sprouted-context-ids
                                :reversal_counterfactual_policy (:counterfactual-policy reversal-spec)
                                :reversal_counterfactual_source (:counterfactual-cause-id reversal-spec)
                                :reversal_counterfactual_goal (:counterfactual-goal-id reversal-spec)
                                :reversal_counterfactual_reasons (:counterfactual-reasons reversal-spec)
                                :reversal_counterfactual_fact_ids (:counterfactual-fact-ids reversal-spec)
                                :reversal_branch_context (:sprouted-context-id primary-branch)}})]
        [world (:sprouted-context-id primary-branch)]))))

(defn- apply-scripted-roving
  [world selected-goal-id roving-spec]
  (if-not roving-spec
    [world nil]
    (let [context-id (or (:context-id roving-spec)
                         (goal-context world selected-goal-id)
                         (:reality-lookahead world)
                         (:reality-context world))
          [world roving-result]
          (families/roving-plan world
                                (assoc roving-spec
                                       :goal-id selected-goal-id
                                       :context-id context-id))
          world (append-sprouted-contexts world [(:sprouted-context-id roving-result)])
          world (trace/merge-latest-cycle
                 world
                 {:mutations (:mutation-events world)
                  :selection {:goal_family :roving
                              :family_goal_id selected-goal-id
                              :roving_selection_policy (:selection-policy roving-result)
                              :roving_seed_episode (:episode-id roving-result)
                              :roving_reminded_episodes (:reminded-episode-ids roving-result)
                              :roving_active_indices (:active-indices roving-result)
                              :roving_branch_context (:sprouted-context-id roving-result)}})]
      [world (:sprouted-context-id roving-result)])))

(defn- apply-automatic-family-plan
  [world selected-goal-id script]
  (if-not (:auto-family-plans? script)
    [world nil]
    (case (goal-type world selected-goal-id)
      :reversal
      (apply-scripted-reversal
       world
       selected-goal-id
       {:discover-leaf? true
        :derive-counterfactuals? true
        :selection-policy (get-in world [:goals selected-goal-id :activation-policy])
        :selection-reasons (get-in world [:goals selected-goal-id :activation-reasons])
        :old-context-id (get-in world [:goals selected-goal-id :trigger-context-id])
        :old-top-level-goal-id (get-in world [:goals selected-goal-id :trigger-failed-goal-id])
        :failed-goal-id (get-in world [:goals selected-goal-id :trigger-failed-goal-id])
        :context-depth (count (cx/ancestors world
                                            (get-in world [:goals selected-goal-id :trigger-context-id])))
        :emotion-pressure (get-in world [:goals selected-goal-id :trigger-emotion-strength])})

      :roving
      (apply-scripted-roving world selected-goal-id {})

      [world nil])))

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

(defn- apply-cycle-adapter
  [world selected-goal-id script]
  (if-let [cycle-adapter (:cycle-adapter script)]
    (cycle-adapter world selected-goal-id script)
    world))

(defn run-scripted-cycle
  "Run one scripted control cycle.

  Script keys:
  - `:timestamp`, `:active-indices`, `:retrievals`, `:chosen-node-id`,
    `:selection`, `:feedback-applied`, `:serendipity-bias`, `:situations`
    -> merged into the cycle trace
  - `:reversal-branch` -> invoke the real REVERSAL branch primitive with a
    fixture-selected or kernel-discovered failed leaf. Optional
    `:derive-counterfactuals? true` asks the kernel to choose input facts from
    stored failure-cause facts instead of taking fixture-supplied input facts.
  - `:roving-branch` -> invoke the real ROVING plan body, optionally seeding a
    specific pleasant episode via `:episode-id`
  - `:cycle-adapter` -> pure function `(fn [world selected-goal-id script])`
    that can derive additional trace state from the mutated branch
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
      (let [[world _] (apply-scripted-reversal world
                                               selected-goal-id
                                               (:reversal-branch script))
            [world _] (apply-scripted-roving world
                                             selected-goal-id
                                             (:roving-branch script))
            [world _] (if (or (:reversal-branch script)
                              (:roving-branch script))
                        [world nil]
                        (apply-automatic-family-plan world
                                                     selected-goal-id
                                                     script))
            world (apply-cycle-adapter world selected-goal-id script)
            [world sprout-ids]
            (create-scripted-sprouts world selected-goal-id (:sprouts script []))
            world (append-sprouted-contexts world sprout-ids)
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
