(ns daydreamer.benchmarks.graffito-microfixture
  "First kernel-facing Graffito microfixture.

  This is intentionally smaller than a Graffito miniworld. Its job is to keep
  one foot in the live kernel while moving from toy pressure maps toward typed
  psychological state: relationship facts, obligation facts, artifact state,
  exposure, and recent events."
  (:require [daydreamer.context :as cx]
            [daydreamer.episodic-memory :as episodic]
            [daydreamer.goal-families :as families]
            [daydreamer.runner :as runner]
            [daydreamer.trace :as trace]))

(def benchmark-brief-path
  "Source brief for the first Graffito kernel slice."
  "daydreaming/Notes/experiential-design/24-graffito-kernel-brief.md")

(defn- typed-fact
  [fact-type fact-id]
  {:fact/type fact-type
   :fact/id fact-id})

(defn- goal-fact
  ([goal-id top-level-goal status activation-context]
   (goal-fact goal-id top-level-goal status activation-context {}))
  ([goal-id top-level-goal status activation-context extras]
   (merge {:fact/type :goal
           :goal-id goal-id
           :top-level-goal top-level-goal
           :status status
           :activation-context activation-context}
          extras)))

(defn- intends-fact
  [from-goal-id to-goal-id top-level-goal]
  {:fact/type :intends
   :from-goal-id from-goal-id
   :to-goal-id to-goal-id
   :top-level-goal top-level-goal})

(defn- failure-cause-fact
  [fact-id goal-id priority counterfactual-facts]
  {:fact/type :failure-cause
   :fact/id fact-id
   :goal-id goal-id
   :priority priority
   :counterfactual-facts counterfactual-facts})

(defn- rationalization-frame-fact
  [fact-id goal-id priority reframe-facts]
  {:fact/type :rationalization-frame
   :fact/id fact-id
   :goal-id goal-id
   :priority priority
   :reframe-facts reframe-facts})

(def ^:private apartment-situation-id :graffito_grandma_apartment)
(def ^:private mural-situation-id :graffito_night_mural)
(def ^:private apartment-goal-id :g_tony_be_seen_rightly)
(def ^:private mural-goal-id :g_tony_escape_capture)
(def ^:private mural-leaf-goal-id :g_tony_stay_unseen)
(def ^:private apartment-emotion-id :e_tony_wrongness_dread)
(def ^:private mural-emotion-id :e_coplight_panic)
(def ^:private apartment-frame-id :rf_crooked_is_power)
(def ^:private mural-cause-id :fc_cop_light_turns_art_to_capture)
(def ^:private mural-leaf-objective
  {:fact/type :assumption
   :fact/id :wall_stays_closed})

(def ^:private shared-typed-facts
  [(typed-fact :artifact-state :can_is_inheritance)
   (typed-fact :relationship :tony_seeks_recognition_from_monk)
   (typed-fact :appraisal :creation_is_regulation)])

(def ^:private apartment-reframe-facts
  (vec
   (concat shared-typed-facts
           [(typed-fact :rationalization :crooked_is_power)
            (typed-fact :artifact-state :sketchbook_is_regulation_tool)])))

(def ^:private mural-counterfactual-facts
  [(typed-fact :exposure :cop_light_breaks_off)
   (typed-fact :relationship :monk_stays_with_tony)])

(def ^:private apartment-facts
  [(typed-fact :present-actor :tony_present)
   (typed-fact :present-actor :monk_present)
   (typed-fact :present-actor :grandma_present)
   (typed-fact :relationship :tony_trusts_monk)
   (typed-fact :relationship :grandma_distrusts_monk_mission)
   (typed-fact :role-obligation :grandma_must_protect_tony)
   (typed-fact :role-obligation :monk_owes_presence_not_only_mission)
   (typed-fact :artifact-state :sketchbook_is_regulation_tool)
   (typed-fact :recent-event :monk_returned_home)
   (typed-fact :recent-event :grandma_challenged_monk)
   (typed-fact :appraisal :overwhelm_needs_reframe)])

(def ^:private mural-facts
  [(typed-fact :present-actor :tony_present)
   (typed-fact :present-actor :monk_present)
   (typed-fact :exposure :cop_light_closing)
   (typed-fact :exposure :sirens_near)
   (typed-fact :exposure :street_is_exposing)
   (typed-fact :artifact-state :wall_is_portal_surface)
   (typed-fact :recent-event :cops_interrupted_mural)
   (typed-fact :recent-event :tony_startled_by_light)
   (typed-fact :relationship :monk_teaches_flow)
   (typed-fact :appraisal :overwhelm_needs_turn_not_force)])

(defn- assert-facts
  [world context-id facts]
  (reduce (fn [current-world fact]
            (cx/assert-fact current-world context-id fact))
          world
          facts))

(defn build-world
  "Create the first typed-fact Graffito microfixture world."
  []
  (let [world (runner/initial-world)
        root-id (:reality-context world)
        [world apartment-context-id] (cx/sprout world root-id)
        [world mural-context-id] (cx/sprout world root-id)
        world (assert-facts world
                            apartment-context-id
                            (concat apartment-facts
                                    shared-typed-facts
                                    [{:fact/type :situation
                                      :fact/id apartment-situation-id}
                                     {:fact/type :emotion
                                      :emotion-id apartment-emotion-id
                                      :strength 0.76
                                      :valence :negative
                                      :affect :shame}
                                     {:fact/type :dependency
                                      :from-id apartment-emotion-id
                                      :to-id apartment-goal-id}
                                     (goal-fact apartment-goal-id
                                                apartment-goal-id
                                                :failed
                                                apartment-context-id)
                                     (rationalization-frame-fact apartment-frame-id
                                                                 apartment-goal-id
                                                                 0.84
                                                                 apartment-reframe-facts)]))
        world (assert-facts world
                            mural-context-id
                            (concat mural-facts
                                    shared-typed-facts
                                    [mural-leaf-objective
                                     {:fact/type :situation
                                      :fact/id mural-situation-id}
                                     {:fact/type :emotion
                                      :emotion-id mural-emotion-id
                                      :strength 0.83
                                      :valence :negative
                                      :affect :fear}
                                     {:fact/type :dependency
                                      :from-id mural-emotion-id
                                      :to-id mural-goal-id}
                                     (goal-fact mural-goal-id
                                                mural-goal-id
                                                :failed
                                                mural-context-id)
                                     (goal-fact mural-leaf-goal-id
                                                mural-goal-id
                                                :runable
                                                mural-context-id
                                                {:strength 0.34
                                                 :objective-fact mural-leaf-objective})
                                     (intends-fact mural-goal-id
                                                   mural-leaf-goal-id
                                                   mural-goal-id)
                                     (failure-cause-fact mural-cause-id
                                                         mural-goal-id
                                                         0.87
                                                         mural-counterfactual-facts)]))
        fixture {:root-id root-id
                 :contexts {:apartment apartment-context-id
                            :mural mural-context-id}
                 :authored {:rationalization-frame apartment-frame-id
                            :reversal-cause mural-cause-id}
                 :shared-fact-ids (mapv :fact/id shared-typed-facts)}]
    (assoc world :graffito-microfixture fixture)))

(defn- reset-cycle-events
  [world]
  (assoc world
         :mutation-events []
         :retrieval-events []
         :backtrack-events []
         :termination-events []))

(defn- trace-selected-goal
  [world goal-id]
  (trace/goal-summary world goal-id))

(defn- append-cycle
  [world payload]
  (-> world
      reset-cycle-events
      (update :cycle inc)
      (trace/append-cycle payload)))

(defn- apartment-rationalization-goal
  [world]
  (let [apartment-context-id (get-in world [:graffito-microfixture :contexts :apartment])]
    (->> (vals (:goals world))
         (filter #(and (= :rationalization (:goal-type %))
                       (= apartment-context-id (:trigger-context-id %))))
         (sort-by (comp str :id))
         first)))

(defn- mural-reversal-goal
  [world]
  (let [mural-context-id (get-in world [:graffito-microfixture :contexts :mural])]
    (->> (vals (:goals world))
         (filter #(and (= :reversal (:goal-type %))
                       (= mural-context-id (:trigger-context-id %))))
         (sort-by (comp str :id))
         first)))

(defn activation-read
  "Activate family goals in the Graffito world and summarize the live result."
  [world]
  (let [world (families/activate-family-goals world)
        apartment-goal (apartment-rationalization-goal world)
        mural-goal (mural-reversal-goal world)]
    [world {:rationalization-goal apartment-goal
            :reversal-goal mural-goal
            :activation-events (:activation-events world)}]))

(defn- summarize-rationalization-cycle
  [world goal family-plan]
  {:cycle (:cycle world)
   :step :apartment-rationalization
   :family :rationalization
   :situation-id apartment-situation-id
   :goal-id (:id goal)
   :activation-policy (:activation-policy goal)
   :frame-id (get-in family-plan [:selection :rationalization_frame_id])
   :winner-origin (get-in family-plan [:selection :rationalization_frame_winner_origin])
   :candidate-race (get-in family-plan [:selection :rationalization_frame_candidates])
   :family-plan-episode-id (get-in family-plan [:selection :family_plan_episode_id])
   :admission-status (:admission-status family-plan)
   :rule-provenance (:rule-provenance family-plan)})

(defn- summarize-reversal-cycle
  [world goal family-plan]
  {:cycle (:cycle world)
   :step :mural-reversal
   :family :reversal
   :situation-id mural-situation-id
   :goal-id (:id goal)
   :activation-policy (:activation-policy goal)
   :source-id (get-in family-plan [:selection :reversal_counterfactual_source])
   :winner-origin (get-in family-plan [:selection :reversal_counterfactual_winner_origin])
   :candidate-race (get-in family-plan [:selection :reversal_counterfactual_candidates])
   :family-plan-episode-id (get-in family-plan [:selection :family_plan_episode_id])
   :admission-status (:admission-status family-plan)
   :rule-provenance (:rule-provenance family-plan)})

(defn- run-apartment-rationalization
  [world goal]
  (let [world (append-cycle world
                            {:selected-goal (trace-selected-goal world (:id goal))
                             :fixture-step {:step :apartment-rationalization
                                            :family :rationalization
                                            :situation-id apartment-situation-id}})
        [world family-plan] (families/run-family-plan world {:goal-id (:id goal)})
        summary (summarize-rationalization-cycle world goal family-plan)
        world (trace/merge-latest-cycle
               world
               {:selection (:selection family-plan)
                :sprouted (:sprouted-context-ids family-plan)
                :mutations (:mutation-events world)
                :rule-provenance (:rule-provenance family-plan)
                :family-plan-episode-id (:family-plan-episode-id summary)
                :graffito_microfixture {:winner-origin (:winner-origin summary)
                                        :candidate-race (:candidate-race summary)}})]
    [world summary]))

(defn- run-mural-reversal
  [world goal]
  (let [world (append-cycle world
                            {:selected-goal (trace-selected-goal world (:id goal))
                             :fixture-step {:step :mural-reversal
                                            :family :reversal
                                            :situation-id mural-situation-id}})
        [world family-plan] (families/run-family-plan world {:goal-id (:id goal)})
        summary (summarize-reversal-cycle world goal family-plan)
        world (trace/merge-latest-cycle
               world
               {:selection (:selection family-plan)
                :sprouted (:sprouted-context-ids family-plan)
                :mutations (:mutation-events world)
                :rule-provenance (:rule-provenance family-plan)
                :family-plan-episode-id (:family-plan-episode-id summary)
                :graffito_microfixture {:winner-origin (:winner-origin summary)
                                        :candidate-race (:candidate-race summary)}})]
    [world summary]))

(defn retrieval-probe
  "Probe episodic retrieval from the mural context back into stored
  family-plan episodes using the shared typed facts."
  [world]
  (let [mural-context-id (get-in world [:graffito-microfixture :contexts :mural])
        visible-facts (cx/visible-facts world mural-context-id)
        visible-indices (->> visible-facts
                             (keep :fact/id)
                             distinct
                             vec)
        hits (->> (episodic/retrieve-episodes world
                                              visible-indices
                                              {:threshold-key :plan-threshold
                                               :active-family :reversal
                                               :active-rule-path [:goal-family/reversal-plan-dispatch]})
                  (keep (fn [hit]
                          (let [episode (get-in world [:episodes (:episode-id hit)])
                                provenance (:provenance episode)]
                            (when (and (= :family-plan (:source provenance))
                                       (= :rationalization (:family provenance)))
                              {:episode-id (:episode-id hit)
                               :marks (:marks hit)
                               :effective-marks (or (:effective-marks hit)
                                                    (:marks hit))
                               :overlap (->> visible-indices
                                             (filter (set (:content-indices episode)))
                                             vec)
                               :content-indices (:content-indices episode)
                               :rule-path (:rule-path episode)
                               :admission-status (:admission-status episode)}))))
                  vec)
        world (append-cycle world
                            {:fixture-step {:step :mural-retrieval-probe
                                            :family :retrieval
                                            :situation-id mural-situation-id}})
        summary {:cycle (:cycle world)
                 :step :mural-retrieval-probe
                 :family :retrieval
                 :situation-id mural-situation-id
                 :visible-indices visible-indices
                 :hits hits}
        world (trace/merge-latest-cycle
               world
               {:retrieved_episodes hits
                :graffito_microfixture {:retrieval_probe hits}})]
    [world summary]))

(defn summary-line
  [cycle-summary]
  (case (:step cycle-summary)
    :apartment-rationalization
    (str "Cycle " (:cycle cycle-summary)
         " | apartment rationalization"
         " | winner: " (or (some-> (:winner-origin cycle-summary) name) "none")
         " | frame: " (some-> (:frame-id cycle-summary) name))

    :mural-reversal
    (str "Cycle " (:cycle cycle-summary)
         " | mural reversal"
         " | winner: " (or (some-> (:winner-origin cycle-summary) name) "none")
         " | source: " (some-> (:source-id cycle-summary) name))

    :mural-retrieval-probe
    (str "Cycle " (:cycle cycle-summary)
         " | mural retrieval probe"
         " | hits: " (count (:hits cycle-summary)))

    (str "Cycle " (:cycle cycle-summary))))

(defn benchmark-metadata
  ([] (benchmark-metadata {}))
  ([overrides]
   (merge {:started_at "2026-03-22T00:00:00Z"
           :engine_path "kernel/src/daydreamer"
           :seed "graffito-microfixture"
           :benchmark "graffito_microfixture"
           :world_path benchmark-brief-path
           :graph_path "benchmark: code-defined"
           :feedback_path nil
           :palette_path "graffito_microfixture"
           :graph_nodes 2
           :graph_edges 1}
          overrides)))

(defn run-slice
  "Run the first deterministic Graffito microfixture slice.

  The slice is intentionally narrow:
  1. activate family goals from typed fact-space
  2. run apartment rationalization
  3. run mural reversal
  4. probe typed-fact retrieval from mural context back into the stored
     apartment rationalization episode"
  ([] (run-slice {}))
  ([{:keys [world]
     :or {world (build-world)}}]
   (let [[world activation] (activation-read world)
         apartment-goal (:rationalization-goal activation)
         mural-goal (:reversal-goal activation)
         [world cycle-1] (run-apartment-rationalization world apartment-goal)
         [world cycle-2] (run-mural-reversal world mural-goal)
         [world cycle-3] (retrieval-probe world)
         cycle-summaries [cycle-1 cycle-2 cycle-3]]
     {:world world
      :activation activation
      :cycle-summaries cycle-summaries
      :summaries (mapv summary-line cycle-summaries)
      :log (trace/reporter-log world (benchmark-metadata))})))

(comment
  (run-slice))
