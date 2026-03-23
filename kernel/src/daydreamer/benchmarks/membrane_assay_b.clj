(ns daydreamer.benchmarks.membrane-assay-b
  "Deterministic positive-evidence membrane assay.

  Assay A already proves dynamic stored-source entry and first defensive flag
  movement. Assay B is the sibling live assay for the positive path:
  a bridged family-plan episode earns repeated cross-family success evidence,
  promotes to durable, and opens a real frontier rule."
  (:require [daydreamer.benchmarks.membrane-fixture :as assay-a]
            [daydreamer.context :as cx]
            [daydreamer.episodic-memory :as episodic]
            [daydreamer.goal-families :as families]
            [daydreamer.rules :as rules]
            [daydreamer.trace :as trace]))

(def benchmark-brief-path
  "Source spec for the membrane assay ladder."
  "daydreaming/Notes/membrane-fixture-spec.md")

(def ^:private bridge-rule-id :goal-family/reversal-aftershock-to-rationalization)
(def ^:private shared-failed-goal-id :g_performance_failure)
(def ^:private bridge-frame-id :rf_doubt_is_preparation)
(def ^:private a-emotion-id :e_rehearsal_dread)
(def ^:private probe-target-family :reversal)

(defn build-world
  "Create the deterministic Assay B world.

  This starts from the shipped Assay A world, then suppresses ordinary
  rationalization activation in situation A so the frontier bridge is the
  unique live on-ramp for the rationalization episode we want to test."
  []
  (let [world (assay-a/build-world)
        a-context-id (get-in world [:membrane-fixture :situation-contexts :a])
        b-context-id (get-in world [:membrane-fixture :situation-contexts :b])
        world (cx/retract-fact world
                               a-context-id
                               {:fact/type :dependency
                                :from-id a-emotion-id
                                :to-id shared-failed-goal-id})]
    (assoc world
           :membrane-assay-b {:bridge-context-id a-context-id
                              :probe-context-id b-context-id
                              :bridge-frame-id bridge-frame-id
                              :bridge-rule-id bridge-rule-id
                              :probe-target-family probe-target-family})))

(defn- reset-cycle-events
  [world]
  (assoc world
         :mutation-events []
         :retrieval-events []
         :backtrack-events []
         :termination-events []))

(defn- append-cycle
  [world payload]
  (-> world
      reset-cycle-events
      (update :cycle inc)
      (trace/append-cycle payload)))

(defn- bridged-rationalization-goal
  [world]
  (->> (vals (:goals world))
       (filter #(and (= :rationalization (:goal-type %))
                     (= :reversal_aftershock_rationalization_frame
                        (:activation-policy %))))
       (sort-by (comp str :id))
       first))

(defn- summarize-bridged-rationalization
  [world goal family-plan]
  {:cycle (:cycle world)
   :step :bridged-rationalization-a
   :family :rationalization
   :goal-id (:id goal)
   :family-plan-episode-id (get-in family-plan
                                   [:selection :family_plan_episode_id])
   :winner-origin (get-in family-plan
                          [:selection :rationalization_frame_winner_origin])
   :source-episode-id (get-in family-plan
                              [:selection :rationalization_source_episode])
   :candidate-race (get-in family-plan
                           [:selection :rationalization_frame_candidates])
   :activation-policy (:activation-policy goal)
   :activation-reasons (:activation-reasons goal)
   :rule-provenance (:rule-provenance family-plan)
   :admission-status (:admission-status family-plan)})

(defn- run-bridged-rationalization-cycle
  [world]
  (let [world (families/activate-family-goals world)
        goal (or (bridged-rationalization-goal world)
                 (throw (ex-info "Assay B expected a bridged rationalization goal"
                                 {:rule-id bridge-rule-id})))
        context-id (get-in world [:goals (:id goal) :next-cx])
        bridge-context-id (get-in world [:membrane-assay-b :bridge-context-id])
        world (append-cycle world
                            {:selected-goal (trace/goal-summary world (:id goal))
                             :fixture-step {:step :bridged-rationalization-a
                                            :family :rationalization
                                            :situation-id :situation_a}})
        [world family-plan]
        (families/run-family-plan world
                                  {:goal-id (:id goal)
                                   :context-id context-id
                                   :trigger-context-id bridge-context-id
                                   :failed-goal-id shared-failed-goal-id
                                   :frame-id bridge-frame-id})]
    (when-not family-plan
      (throw (ex-info "Assay B expected bridged rationalization to yield a plan"
                      {:goal-id (:id goal)})))
    (let [summary (summarize-bridged-rationalization world goal family-plan)
          world (trace/merge-latest-cycle
                 world
                 {:selection (:selection family-plan)
                  :sprouted (:sprouted-context-ids family-plan)
                  :mutations (:mutation-events world)
                  :rule-provenance (:rule-provenance family-plan)
                  :family-plan-episode-id (:family-plan-episode-id summary)
                  :membrane-selection {:winner-origin (:winner-origin summary)
                                       :source-episode-id (:source-episode-id summary)
                                       :candidate-kind :frame
                                       :candidate-race (:candidate-race summary)}
                  :episode-use-records (get-in family-plan
                                               [:result :episode-use-records]
                                               [])})]
      [world summary])))

(defn- probe-visible-indices
  [world]
  (let [probe-context-id (get-in world [:membrane-assay-b :probe-context-id])]
    (->> (cx/visible-facts world probe-context-id)
         (keep :fact/id)
         distinct
         vec)))

(defn- cross-family-source-candidates
  [world]
  (let [hits (episodic/retrieve-episodes
              world
              (probe-visible-indices world)
              {:threshold-key :plan-threshold
               :active-rule-path [:goal-family/reversal-plan-dispatch]
               :active-family probe-target-family})]
    (->> hits
         (mapv (fn [hit]
                 (let [episode (get-in world [:episodes (:episode-id hit)])
                       provenance (:provenance episode)
                       source-family (:family provenance)
                       rule-path (vec (:rule-path episode))
                       family-plan-source? (= :family-plan (:source provenance))
                       exclusion-reasons (cond-> []
                                           (not family-plan-source?)
                                           (conj :not_family_plan_source)

                                           (and family-plan-source?
                                                (not= :rationalization source-family))
                                           (conj :wrong_source_family)

                                           (and family-plan-source?
                                                (= :rationalization source-family)
                                                (not (some #{bridge-rule-id}
                                                           rule-path)))
                                           (conj :missing_frontier_rule_path))]
                   (cond-> {:episode-id (:episode-id hit)
                            :source-family source-family
                            :admission-status (:admission-status episode)
                            :cycle-created (:cycle-created episode)
                            :same-cycle? (= (:cycle-created episode)
                                            (:cycle world))
                            :marks (:marks hit)
                            :effective-marks (or (:effective-marks hit)
                                                 (:marks hit))
                            :rule-path rule-path
                            :selection-policy :assay-b-cross-family-source
                            :selected? (empty? exclusion-reasons)}
                     (:provenance-reason hit)
                     (assoc :provenance-reason (:provenance-reason hit))

                     (seq exclusion-reasons)
                     (assoc :exclusion-reasons exclusion-reasons)))))
         (sort-by (juxt (complement :selected?)
                        (comp - :effective-marks)
                        (comp str :episode-id))))))

(defn- summarize-cross-family-evidence
  [world step-key use-info outcome-info transition access-transitions candidates]
  (let [episode-id (:episode-id use-info)
        episode (get-in world [:episodes episode-id])]
    {:cycle (:cycle world)
     :step step-key
     :family probe-target-family
     :situation-id :situation_b
     :selected-episode-id episode-id
     :candidate-race candidates
     :use-id (:use-id use-info)
     :outcome (:outcome outcome-info)
     :admission-transition transition
     :access-transitions access-transitions
     :promotion-evidence-count (count (:promotion-evidence episode))
     :use-history-count (count (:use-history episode))
     :admission-status (:admission-status episode)}))

(defn- run-cross-family-evidence-cycle
  [world {:keys [step-key goal-id]}]
  (let [probe-context-id (get-in world [:membrane-assay-b :probe-context-id])
        world (append-cycle world
                            {:fixture-step {:step step-key
                                            :family probe-target-family
                                            :situation-id :situation_b}})
        candidates (cross-family-source-candidates world)
        selected (first (filter :selected? candidates))]
    (if-not selected
      (let [summary {:cycle (:cycle world)
                     :step step-key
                     :family probe-target-family
                     :situation-id :situation_b
                     :selected-episode-id nil
                     :candidate-race candidates
                     :outcome nil
                     :admission-transition nil
                     :access-transitions []
                     :promotion-evidence-count 0
                     :use-history-count 0
                     :admission-status nil}
            world (trace/merge-latest-cycle
                   world
                   {:selection {:family probe-target-family
                                :situation-id :situation_b
                                :status :no-cross-family-source}
                    :membrane-selection {:winner-origin nil
                                         :source-episode-id nil
                                         :candidate-kind :episode
                                         :candidate-race candidates}})]
        [world summary])
      (let [episode-id (:episode-id selected)
            episode (get-in world [:episodes episode-id])
            [world use-info]
            (episodic/note-episode-use
             world
             episode-id
             {:reason :assay-b-cross-family-use
              :use-role :assay-b-cross-family-source
              :goal-id goal-id
              :branch-context-id probe-context-id
              :source-family (get-in episode [:provenance :family])
              :target-family probe-target-family
              :source-rule (last (:rule-path episode))
              :target-rule :goal-family/reversal-plan-dispatch})
            [world outcome-info]
            (episodic/resolve-episode-use-outcome
             world
             episode-id
             (:use-id use-info)
             {:outcome :succeeded
              :reason :cross-family-family-plan-success})
            [world transition]
            (episodic/reconcile-episode-admission world episode-id)
            episode (get-in world [:episodes episode-id])
            [world access-transitions]
            (rules/reconcile-episode-rule-access world
                                                 (families/family-rules)
                                                 episode
                                                 transition
                                                 {:branch-context-id probe-context-id})
            summary (summarize-cross-family-evidence world
                                                     step-key
                                                     use-info
                                                     outcome-info
                                                     transition
                                                     access-transitions
                                                     candidates)
            world (trace/merge-latest-cycle
                   world
                   {:selection {:family probe-target-family
                                :situation-id :situation_b
                                :status :cross-family-source-used
                                :selected_episode_id episode-id
                                :selected_goal_id goal-id}
                    :membrane-selection {:winner-origin :dynamic
                                         :source-episode-id episode-id
                                         :candidate-kind :episode
                                         :candidate-race candidates}
                    :episode-use-records [use-info]
                    :admission-transition transition
                    :rule-access-transitions access-transitions})]
        [world summary]))))

(defn summary-line
  "Compact terminal summary for one Assay B cycle."
  [cycle-summary]
  (case (:step cycle-summary)
    nil
    (assay-a/summary-line cycle-summary)

    :bridged-rationalization-a
    (str "Cycle " (:cycle cycle-summary)
         " | bridged rationalization"
         " | winner: " (or (some-> (:winner-origin cycle-summary) name) "none")
         " | episode: " (some-> (:family-plan-episode-id cycle-summary) name))

    (str "Cycle " (:cycle cycle-summary)
         " | " (name (:step cycle-summary))
         " | source: " (or (some-> (:selected-episode-id cycle-summary) name) "none")
         " | admission: " (or (some-> (:admission-status cycle-summary) name) "none")
         " | promotion-evidence: " (:promotion-evidence-count cycle-summary 0))))

(defn benchmark-metadata
  ([] (benchmark-metadata {}))
  ([overrides]
   (merge {:started_at "2026-03-22T00:00:00Z"
           :engine_path "kernel/src/daydreamer"
           :seed "membrane-assay-b"
           :benchmark "membrane_assay_b"
           :world_path benchmark-brief-path
           :graph_path "benchmark: code-defined"
           :feedback_path nil
           :palette_path "membrane_assay_b"
           :graph_nodes 8
           :graph_edges 8}
          overrides)))

(defn run-soak
  "Run the deterministic Assay B benchmark.

  Cycle order:
  1. authored reversal in situation B
  2. dynamic reversal in situation A
  3. bridged rationalization in situation A
  4. first cross-family success probe in situation B
  5. second cross-family success probe in situation B"
  ([] (run-soak {}))
  ([{:keys [world]
     :or {world (build-world)}}]
   (let [[world cycle-1] (assay-a/run-cycle world
                                            {:family :reversal
                                             :situation-key :b})
         [world cycle-2] (assay-a/run-cycle world
                                            {:family :reversal
                                             :situation-key :a})
         [world cycle-3] (run-bridged-rationalization-cycle world)
         [world cycle-4] (run-cross-family-evidence-cycle
                          world
                          {:step-key :cross-family-success-1
                           :goal-id :g-assay-b-cross-family-1})
         [world cycle-5] (run-cross-family-evidence-cycle
                          world
                          {:step-key :cross-family-success-2
                           :goal-id :g-assay-b-cross-family-2})
         cycle-summaries [cycle-1 cycle-2 cycle-3 cycle-4 cycle-5]]
     {:world world
      :cycle-summaries cycle-summaries
      :summaries (mapv summary-line cycle-summaries)
      :log (trace/reporter-log world (benchmark-metadata))})))

(comment
  (run-soak))
