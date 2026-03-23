(ns daydreamer.benchmarks.graffito-regulation-slice
  "Second kernel-facing Graffito slice.

  Slice 1 proved typed situations and honest family-plan storage. This slice
  adds the next architectural step from the Graffito situation-model reviews:
  persistent Tony state plus a benchmark-local reappraisal pass. The question is
  narrower than a miniworld: can the same mural raw inputs produce a different
  live family choice after an apartment support cycle changes Tony's carried
  state?"
  (:require [daydreamer.context :as cx]
            [daydreamer.goal-families :as families]
            [daydreamer.runner :as runner]
            [daydreamer.trace :as trace]))

(def benchmark-brief-path
  "Source brief for the Graffito regulation slice."
  "daydreaming/Notes/experiential-design/24-graffito-kernel-brief.md")

(def ^:private tony-id :tony)
(def ^:private apartment-situation-id :graffito_grandma_apartment)
(def ^:private mural-situation-id :graffito_night_mural)
(def ^:private apartment-goal-id :g_tony_be_seen_rightly)
(def ^:private mural-threat-goal-id :g_tony_hold_line_at_mural)
(def ^:private mural-challenge-goal-id :g_tony_make_the_mark_hold)
(def ^:private mural-leaf-goal-id :g_tony_stay_unseen)
(def ^:private apartment-emotion-id :e_tony_wrongness_shame)
(def ^:private mural-threat-emotion-id :e_mural_capture_panic)
(def ^:private mural-challenge-emotion-id :e_mural_self_doubt)
(def ^:private apartment-frame-id :rf_intensity_becomes_style)
(def ^:private mural-threat-cause-id :fc_cop_light_turns_art_to_capture)
(def ^:private mural-challenge-frame-id :rf_same_light_can_be_held)
(def ^:private mural-leaf-objective
  {:fact/type :assumption
   :fact/id :wall_stays_closed})

(def ^:private default-tony-state
  {:sensory-load 0.84
   :entrainment 0.12
   :felt-agency 0.18
   :perceived-control 0.21})

(def ^:private supported-tony-state
  {:sensory-load 0.78
   :entrainment 0.68
   :felt-agency 0.74
   :perceived-control 0.66})

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

(def ^:private mural-raw-input-facts
  [(typed-fact :sensorimotor-input :light_jolt_floods_attention)
   (typed-fact :sensorimotor-input :siren_pulse_hits_body)
   (typed-fact :sensorimotor-input :noise_fragments_precision)
   (typed-fact :exposure :cop_light_closing)
   (typed-fact :exposure :street_is_exposing)
   (typed-fact :exposure :sirens_near)
   (typed-fact :artifact-state :wall_is_portal_surface)
   (typed-fact :artifact-state :can_is_inheritance)
   (typed-fact :recent-event :cops_interrupted_mural)
   (typed-fact :recent-event :tony_startled_by_light)
   (typed-fact :person-object-relation :can_waits_for_a_steady_hand)
   (typed-fact :person-object-relation :sketchbook_cannot_help_at_full_scale)
   (typed-fact :cross-layer-correspondence :can_corresponds_to_sensory_capacity)
   (typed-fact :cross-layer-correspondence :mural_corresponds_to_threshold_crossing)])

(def ^:private apartment-facts
  [(typed-fact :present-actor :tony_present)
   (typed-fact :present-actor :monk_present)
   (typed-fact :present-actor :grandma_present)
   (typed-fact :relationship :tony_trusts_monk)
   (typed-fact :relationship :grandma_distrusts_monk_mission)
   (typed-fact :relationship :tony_seeks_recognition_from_monk)
   (typed-fact :role-obligation :grandma_must_protect_tony)
   (typed-fact :role-obligation :monk_owes_presence_not_only_mission)
   (typed-fact :artifact-state :sketchbook_is_regulation_tool)
   (typed-fact :artifact-state :can_is_inheritance)
   (typed-fact :recent-event :monk_returned_home)
   (typed-fact :recent-event :grandma_challenged_monk)
   (typed-fact :person-object-relation :monk_moves_tony_into_rhythm)
   (typed-fact :person-object-relation :tony_trusts_can_lineage)
   (typed-fact :person-object-relation :sketchbook_compresses_the_scene)
   (typed-fact :cross-layer-correspondence :grandma_counterpart_of_motherload)
   (typed-fact :cross-layer-correspondence :school_counterpart_of_castle)
   (typed-fact :appraisal :crookedness_can_be_style)])

(def ^:private apartment-reframe-facts
  [(typed-fact :person-object-relation :tony_trusts_can_lineage)
   (typed-fact :person-object-relation :sketchbook_compresses_the_scene)
   (typed-fact :person-object-relation :monk_moves_tony_into_rhythm)
   (typed-fact :cross-layer-correspondence :grandma_counterpart_of_motherload)
   (typed-fact :cross-layer-correspondence :school_counterpart_of_castle)
   (typed-fact :cross-layer-correspondence :can_corresponds_to_sensory_capacity)
   (typed-fact :artifact-state :can_phase_responsive)
   (typed-fact :appraisal :intensity_can_be_style)
   (typed-fact :appraisal :creation_restores_control)])

(def ^:private mural-threat-counterfactual-facts
  [(typed-fact :appraisal :cop_light_breaks_off)
   (typed-fact :relationship :monk_stays_with_tony)])

(def ^:private mural-challenge-reframe-facts
  [(typed-fact :sensorimotor-input :light_jolt_floods_attention)
   (typed-fact :sensorimotor-input :siren_pulse_hits_body)
   (typed-fact :sensorimotor-input :noise_fragments_precision)
   (typed-fact :person-object-relation :can_waits_for_a_steady_hand)
   (typed-fact :cross-layer-correspondence :can_corresponds_to_sensory_capacity)
   (typed-fact :appraisal :same_light_can_be_held)
   (typed-fact :appraisal :control_can_meet_demand)])

(defn current-tony-state
  [world]
  (get-in world [:character-state tony-id]))

(defn derive-regulation-mode
  [{:keys [entrainment felt-agency perceived-control]}]
  (cond
    (and (< entrainment 0.25)
         (< felt-agency 0.3)
         (< perceived-control 0.3))
    :overloaded

    (< entrainment 0.55)
    :bracing

    (< entrainment 0.8)
    :entraining

    (and (>= felt-agency 0.8)
         (>= perceived-control 0.8))
    :creating

    :else
    :flowing))

(defn- derive-appraisal-mode
  [{:keys [felt-agency perceived-control]}]
  (if (and (>= felt-agency 0.6)
           (>= perceived-control 0.6))
    :challenge-dominant
    :threat-dominant))

(defn- projection-facts
  [{:keys [appraisal-mode]}]
  (case appraisal-mode
    :challenge-dominant
    [{:fact/type :emotion
      :emotion-id mural-challenge-emotion-id
      :strength 0.79
      :valence :negative
      :affect :shame}
     {:fact/type :dependency
      :from-id mural-challenge-emotion-id
      :to-id mural-challenge-goal-id}
     (goal-fact mural-challenge-goal-id
                mural-challenge-goal-id
                :failed
                nil)
     (rationalization-frame-fact mural-challenge-frame-id
                                 mural-challenge-goal-id
                                 0.93
                                 mural-challenge-reframe-facts)
     (typed-fact :appraisal :same_light_reads_as_challenge)
     (typed-fact :appraisal :agency_can_hold_the_mark)]

    [{:fact/type :emotion
      :emotion-id mural-threat-emotion-id
      :strength 0.86
      :valence :negative
      :affect :fear}
     {:fact/type :dependency
      :from-id mural-threat-emotion-id
      :to-id mural-threat-goal-id}
     (goal-fact mural-threat-goal-id
                mural-threat-goal-id
                :failed
                nil)
     (goal-fact mural-leaf-goal-id
                mural-threat-goal-id
                :runable
                nil
                {:strength 0.34
                 :objective-fact mural-leaf-objective})
     (intends-fact mural-threat-goal-id
                   mural-leaf-goal-id
                   mural-threat-goal-id)
     (failure-cause-fact mural-threat-cause-id
                         mural-threat-goal-id
                         0.88
                         mural-threat-counterfactual-facts)
     (typed-fact :appraisal :same_light_reads_as_threat)
     (typed-fact :appraisal :capture_feels_inevitable)]))

(def ^:private removable-mural-projection-facts
  (vec (concat (projection-facts {:appraisal-mode :threat-dominant})
               (projection-facts {:appraisal-mode :challenge-dominant}))))

(defn- retract-facts
  [world context-id facts]
  (reduce (fn [current-world fact]
            (cx/retract-fact current-world context-id fact))
          world
          facts))

(defn- assert-facts
  [world context-id facts]
  (reduce (fn [current-world fact]
            (cx/assert-fact current-world context-id fact))
          world
          facts))

(defn project-mural-appraisal
  "Project Tony's carried state into the mural situation as derived appraisal.

  The raw mural inputs stay fixed. What changes is the derived appraisal and the
  family-planning affordance facts asserted into the mural context."
  [world]
  (let [mural-context-id (get-in world [:graffito-regulation-slice :contexts :mural])
        tony-state (current-tony-state world)
        regulation-mode (derive-regulation-mode tony-state)
        appraisal-mode (derive-appraisal-mode tony-state)
        projection-state {:regulation-mode regulation-mode
                          :appraisal-mode appraisal-mode
                          :tony-state tony-state}
        projected-facts (projection-facts projection-state)
        world (retract-facts world mural-context-id removable-mural-projection-facts)
        projected-facts (mapv (fn [fact]
                                (if (= :goal (:fact/type fact))
                                  (assoc fact :activation-context mural-context-id)
                                  fact))
                              projected-facts)
        world (assert-facts world mural-context-id projected-facts)]
    (assoc-in world [:graffito-regulation-slice :mural-projection] projection-state)))

(defn build-world
  "Create the second Graffito slice with carried Tony state and mural
  reappraisal projection."
  []
  (let [world (-> (runner/initial-world)
                  (assoc-in [:character-state tony-id] default-tony-state))
        root-id (:reality-context world)
        [world apartment-context-id] (cx/sprout world root-id)
        [world mural-context-id] (cx/sprout world root-id)
        world (assert-facts world
                            apartment-context-id
                            (concat apartment-facts
                                    [{:fact/type :situation
                                      :fact/id apartment-situation-id}
                                     {:fact/type :emotion
                                      :emotion-id apartment-emotion-id
                                      :strength 0.79
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
                                                                 0.91
                                                                 apartment-reframe-facts)]))
        world (assert-facts world
                            mural-context-id
                            (concat mural-raw-input-facts
                                    [mural-leaf-objective
                                     {:fact/type :situation
                                      :fact/id mural-situation-id}
                                     (typed-fact :present-actor :tony_present)
                                     (typed-fact :present-actor :monk_present)
                                     (typed-fact :relationship :monk_teaches_flow)]))
        fixture {:root-id root-id
                 :contexts {:apartment apartment-context-id
                            :mural mural-context-id}
                 :tony-id tony-id
                 :mural-raw-input-ids (->> mural-raw-input-facts
                                           (keep :fact/id)
                                           vec)
                 :authored {:apartment-frame apartment-frame-id
                            :mural-threat-cause mural-threat-cause-id
                            :mural-challenge-frame mural-challenge-frame-id}}]
    (-> (assoc world :graffito-regulation-slice fixture)
        project-mural-appraisal)))

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

(defn- trace-selected-goal
  [world goal-id]
  (trace/goal-summary world goal-id))

(defn- apartment-rationalization-goal
  [world]
  (let [context-id (get-in world [:graffito-regulation-slice :contexts :apartment])]
    (->> (vals (:goals world))
         (filter #(and (= :rationalization (:goal-type %))
                       (= context-id (:trigger-context-id %))))
         (sort-by (comp str :id))
         first)))

(defn- mural-reversal-goal
  [world]
  (let [context-id (get-in world [:graffito-regulation-slice :contexts :mural])]
    (->> (vals (:goals world))
         (filter #(and (= :reversal (:goal-type %))
                       (= context-id (:trigger-context-id %))))
         (sort-by (comp str :id))
         first)))

(defn- mural-rationalization-goal
  [world]
  (let [context-id (get-in world [:graffito-regulation-slice :contexts :mural])]
    (->> (vals (:goals world))
         (filter #(and (= :rationalization (:goal-type %))
                       (= context-id (:trigger-context-id %))))
         (sort-by (comp str :id))
         first)))

(defn- selected-mural-family
  [{:keys [mural-rationalization-goal mural-reversal-goal]}]
  (cond
    mural-rationalization-goal :rationalization
    mural-reversal-goal :reversal
    :else nil))

(defn mural-choice-read
  "Activate family goals and summarize the current mural family choice."
  ([world] (mural-choice-read world :mural-choice))
  ([world step-key]
   (let [world (families/activate-family-goals world)
         mural-choice {:mural-reversal-goal (mural-reversal-goal world)
                       :mural-rationalization-goal (mural-rationalization-goal world)}
         tony-state (current-tony-state world)
         summary {:cycle (:cycle world)
                  :step step-key
                  :situation-id mural-situation-id
                  :tony-state tony-state
                  :regulation-mode (derive-regulation-mode tony-state)
                  :appraisal-mode (derive-appraisal-mode tony-state)
                  :raw-input-ids (get-in world
                                         [:graffito-regulation-slice
                                          :mural-raw-input-ids])
                  :selected-family (selected-mural-family mural-choice)
                  :reversal-goal-id (some-> (:mural-reversal-goal mural-choice) :id)
                  :rationalization-goal-id (some-> (:mural-rationalization-goal mural-choice) :id)}]
     [world summary])))

(defn- update-tony-state-after-support
  [world]
  (assoc-in world [:character-state tony-id] supported-tony-state))

(defn- summarize-apartment-support
  [world goal family-plan tony-state-before]
  {:cycle (:cycle world)
   :step :apartment-support
   :family :rationalization
   :situation-id apartment-situation-id
   :goal-id (:id goal)
   :frame-id (get-in family-plan [:selection :rationalization_frame_id])
   :winner-origin (get-in family-plan [:selection :rationalization_frame_winner_origin])
   :family-plan-episode-id (get-in family-plan [:selection :family_plan_episode_id])
   :admission-status (:admission-status family-plan)
   :tony-state-before tony-state-before
   :tony-state-after (current-tony-state world)
   :regulation-mode-after (derive-regulation-mode (current-tony-state world))
   :rule-provenance (:rule-provenance family-plan)})

(defn run-apartment-support-cycle
  "Run the apartment rationalization cycle and update Tony's carried state."
  [world]
  (let [world (families/activate-family-goals world)
        goal (or (apartment-rationalization-goal world)
                 (throw (ex-info "Graffito Slice 2 expected an apartment rationalization goal"
                                 {})))
        tony-state-before (current-tony-state world)
        world (append-cycle world
                            {:selected-goal (trace-selected-goal world (:id goal))
                             :fixture-step {:step :apartment-support
                                            :family :rationalization
                                            :situation-id apartment-situation-id}})
        [world family-plan] (families/run-family-plan world {:goal-id (:id goal)})
        world (update-tony-state-after-support world)
        summary (summarize-apartment-support world
                                             goal
                                             family-plan
                                             tony-state-before)
        world (trace/merge-latest-cycle
               world
               {:selection (:selection family-plan)
                :sprouted (:sprouted-context-ids family-plan)
                :mutations (:mutation-events world)
                :rule-provenance (:rule-provenance family-plan)
                :family-plan-episode-id (:family-plan-episode-id summary)
                :graffito_regulation_slice
                {:tony-state-before tony-state-before
                 :tony-state-after (:tony-state-after summary)
                 :regulation-mode-after (:regulation-mode-after summary)}})]
    [world summary]))

(defn carry-tony-state-into-fresh-world
  [world]
  (let [fresh-world (build-world)
        tony-state (current-tony-state world)]
    (-> fresh-world
        (assoc-in [:character-state tony-id] tony-state)
        project-mural-appraisal)))

(defn- summarize-mural-rationalization
  [world goal family-plan]
  {:cycle (:cycle world)
   :step :mural-after-support-run
   :family :rationalization
   :situation-id mural-situation-id
   :goal-id (:id goal)
   :frame-id (get-in family-plan [:selection :rationalization_frame_id])
   :winner-origin (get-in family-plan [:selection :rationalization_frame_winner_origin])
   :family-plan-episode-id (get-in family-plan [:selection :family_plan_episode_id])
   :admission-status (:admission-status family-plan)
   :rule-provenance (:rule-provenance family-plan)})

(defn- run-mural-rationalization-after-support
  [world]
  (let [world (families/activate-family-goals world)
        goal (or (mural-rationalization-goal world)
                 (throw (ex-info "Graffito Slice 2 expected mural rationalization after support"
                                 {})))
        world (append-cycle world
                            {:selected-goal (trace-selected-goal world (:id goal))
                             :fixture-step {:step :mural-after-support-run
                                            :family :rationalization
                                            :situation-id mural-situation-id}})
        [world family-plan] (families/run-family-plan world {:goal-id (:id goal)})
        summary (summarize-mural-rationalization world goal family-plan)
        world (trace/merge-latest-cycle
               world
               {:selection (:selection family-plan)
                :sprouted (:sprouted-context-ids family-plan)
                :mutations (:mutation-events world)
                :rule-provenance (:rule-provenance family-plan)
                :family-plan-episode-id (:family-plan-episode-id summary)})]
    [world summary]))

(defn summary-line
  [cycle-summary]
  (case (:step cycle-summary)
    :mural-before-support
    (str "Cycle " (:cycle cycle-summary)
         " | mural before support"
         " | selected-family: " (some-> (:selected-family cycle-summary) name)
         " | appraisal: " (some-> (:appraisal-mode cycle-summary) name))

    :apartment-support
    (str "Cycle " (:cycle cycle-summary)
         " | apartment support rationalization"
         " | frame: " (some-> (:frame-id cycle-summary) name)
         " | regulation-after: " (some-> (:regulation-mode-after cycle-summary) name))

    :mural-after-support
    (str "Cycle " (:cycle cycle-summary)
         " | mural after support"
         " | selected-family: " (some-> (:selected-family cycle-summary) name)
         " | appraisal: " (some-> (:appraisal-mode cycle-summary) name))

    :mural-after-support-run
    (str "Cycle " (:cycle cycle-summary)
         " | mural rationalization after support"
         " | frame: " (some-> (:frame-id cycle-summary) name))

    (str "Cycle " (:cycle cycle-summary))))

(defn benchmark-metadata
  ([] (benchmark-metadata {}))
  ([overrides]
   (merge {:started_at "2026-03-22T00:00:00Z"
           :engine_path "kernel/src/daydreamer"
           :seed "graffito-regulation-slice"
           :benchmark "graffito_regulation_slice"
           :world_path benchmark-brief-path
           :graph_path "benchmark: code-defined"
           :feedback_path nil
           :palette_path "graffito_regulation_slice"
           :graph_nodes 2
           :graph_edges 1}
          overrides)))

(defn run-slice
  "Run the second deterministic Graffito slice.

  1. read the mural before support
  2. run apartment rationalization and update Tony's carried state
  3. reproject the same mural raw inputs through the new state
  4. show that mural family choice flips and the new choice can run"
  ([] (run-slice {}))
  ([{:keys [world]
     :or {world (build-world)}}]
   (let [[_ baseline] (mural-choice-read world :mural-before-support)
         [support-world support] (run-apartment-support-cycle world)
         reappraised-world (carry-tony-state-into-fresh-world support-world)
         [_ reappraised] (mural-choice-read reappraised-world :mural-after-support)
         [final-world mural-run] (run-mural-rationalization-after-support
                                  reappraised-world)
         cycle-summaries [baseline support reappraised mural-run]]
     {:world final-world
      :support-world support-world
      :baseline baseline
      :reappraised reappraised
      :cycle-summaries cycle-summaries
      :summaries (mapv summary-line cycle-summaries)
      :log (trace/reporter-log final-world (benchmark-metadata))})))

(comment
  (run-slice))
