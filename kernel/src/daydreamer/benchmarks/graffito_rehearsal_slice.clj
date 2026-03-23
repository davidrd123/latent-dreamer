(ns daydreamer.benchmarks.graffito-rehearsal-slice
  "Third kernel-facing Graffito slice.

  Slice 2 proved that a support/rationalization cycle can change Tony's carried
  state and cause the same mural inputs to be reappraised differently. This
  slice proves the other half of the mechanism: a narrow authored rehearsal
  routine changes embodied control, then the same mural inputs are reread
  through that updated state. Accumulation remains deferred to the memory
  membrane rather than becoming a sticky Tony-state trait."
  (:require [clojure.set :as set]
            [daydreamer.context :as cx]
            [daydreamer.goal-families :as families]
            [daydreamer.runner :as runner]
            [daydreamer.trace :as trace]))

(def benchmark-brief-path
  "Source brief for the Graffito rehearsal slice."
  "daydreaming/Notes/experiential-design/24-graffito-kernel-brief.md")

(def ^:private tony-id :tony)
(def ^:private apartment-situation-id :graffito_grandma_apartment)
(def ^:private mural-situation-id :graffito_night_mural)
(def ^:private mural-threat-goal-id :g_tony_hold_line_at_mural)
(def ^:private mural-challenge-goal-id :g_tony_make_the_mark_hold)
(def ^:private mural-leaf-goal-id :g_tony_stay_unseen)
(def ^:private mural-threat-emotion-id :e_mural_capture_panic)
(def ^:private mural-challenge-emotion-id :e_mural_mark_doubt)
(def ^:private mural-threat-cause-id :fc_cop_light_turns_art_to_capture)
(def ^:private mural-challenge-frame-id :rf_rhythm_can_hold_the_mark)
(def ^:private rehearsal-routine-id :rt_counted_stroke_rehearsal)
(def ^:private rehearsal-operator-id :op_counted_stroke_with_sketchbook)
(def ^:private mural-leaf-objective
  {:fact/type :assumption
   :fact/id :wall_stays_closed})

(def ^:private default-tony-state
  {:sensory-load 0.86
   :entrainment 0.11
   :felt-agency 0.22
   :perceived-control 0.24})

(def ^:private rehearsed-tony-state
  {:sensory-load 0.77
   :entrainment 0.73
   :felt-agency 0.67
   :perceived-control 0.7})

(def ^:private default-object-state
  {:can {:phase :dormant}
   :mural {:phase :under_siege}})

(def ^:private rehearsed-object-state
  {:can {:phase :attuning}
   :mural {:phase :reachable}})

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
   (typed-fact :role-obligation :grandma_must_protect_tony)
   (typed-fact :role-obligation :monk_owes_presence_not_only_mission)
   (typed-fact :artifact-state :sketchbook_is_regulation_tool)
   (typed-fact :artifact-state :can_is_inheritance)
   (typed-fact :recent-event :monk_returned_home)
   (typed-fact :recent-event :grandma_keeps_room_safe)
   (typed-fact :sensorimotor-input :monk_counts_a_holdable_beat)
   (typed-fact :sensorimotor-input :sketchbook_offers_small_surface)
   (typed-fact :sensorimotor-input :repeated_stroke_returns_precision)
   (typed-fact :person-object-relation :monk_co_regulates_tony_with_rhythm)
   (typed-fact :person-object-relation :sketchbook_turns_sprawl_into_sequence)
   (typed-fact :person-object-relation :tony_trusts_can_lineage)
   (typed-fact :cross-layer-correspondence :grandma_counterpart_of_motherload)
   (typed-fact :cross-layer-correspondence :school_counterpart_of_castle)])

(def ^:private rehearsal-precondition-ids
  #{:monk_counts_a_holdable_beat
    :sketchbook_offers_small_surface
    :repeated_stroke_returns_precision
    :monk_co_regulates_tony_with_rhythm})

(def ^:private mural-threat-counterfactual-facts
  [(typed-fact :appraisal :cop_light_breaks_off)
   (typed-fact :relationship :monk_stays_with_tony)])

(def ^:private mural-challenge-reframe-facts
  [(typed-fact :sensorimotor-input :light_jolt_floods_attention)
   (typed-fact :sensorimotor-input :siren_pulse_hits_body)
   (typed-fact :sensorimotor-input :noise_fragments_precision)
   (typed-fact :person-object-relation :can_waits_for_a_steady_hand)
   (typed-fact :person-object-relation :sketchbook_turns_sprawl_into_sequence)
   (typed-fact :cross-layer-correspondence :can_corresponds_to_sensory_capacity)
   (typed-fact :appraisal :rhythm_can_hold_the_mark)
   (typed-fact :appraisal :control_can_meet_demand)])

(defn current-tony-state
  [world]
  (get-in world [:character-state tony-id]))

(defn current-object-state
  [world]
  (get-in world [:graffito-rehearsal-slice :objects]))

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
      :strength 0.74
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
                                 0.9
                                 mural-challenge-reframe-facts)
     (typed-fact :appraisal :same_light_reads_as_challenge)
     (typed-fact :appraisal :rhythm_supports_precision)]

    [{:fact/type :emotion
      :emotion-id mural-threat-emotion-id
      :strength 0.87
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
                         0.89
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
  "Project Tony's carried state into the mural situation as derived appraisal."
  [world]
  (let [mural-context-id (get-in world [:graffito-rehearsal-slice :contexts :mural])
        tony-state (current-tony-state world)
        regulation-mode (derive-regulation-mode tony-state)
        appraisal-mode (derive-appraisal-mode tony-state)
        projection-state {:regulation-mode regulation-mode
                          :appraisal-mode appraisal-mode
                          :tony-state tony-state
                          :object-state (current-object-state world)}
        projected-facts (projection-facts projection-state)
        world (retract-facts world mural-context-id removable-mural-projection-facts)
        projected-facts (mapv (fn [fact]
                                (if (= :goal (:fact/type fact))
                                  (assoc fact :activation-context mural-context-id)
                                  fact))
                              projected-facts)
        world (assert-facts world mural-context-id projected-facts)]
    (assoc-in world [:graffito-rehearsal-slice :mural-projection] projection-state)))

(defn build-world
  "Create the third Graffito slice with persistent Tony state and an authored
  rehearsal routine that changes embodied control."
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
                                      :fact/id apartment-situation-id}]))
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
                 :objects default-object-state
                 :mural-raw-input-ids (->> mural-raw-input-facts
                                           (keep :fact/id)
                                           vec)
                 :rehearsal-routine {:routine-id rehearsal-routine-id
                                     :operator-id rehearsal-operator-id
                                     :precondition-ids (sort rehearsal-precondition-ids)}
                 :authored {:mural-threat-cause mural-threat-cause-id
                            :mural-challenge-frame mural-challenge-frame-id}}]
    (-> (assoc world :graffito-rehearsal-slice fixture)
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

(defn- apartment-context-id
  [world]
  (get-in world [:graffito-rehearsal-slice :contexts :apartment]))

(defn- mural-context-id
  [world]
  (get-in world [:graffito-rehearsal-slice :contexts :mural]))

(defn- apartment-fact-ids
  [world]
  (->> (cx/visible-facts world (apartment-context-id world))
       (keep :fact/id)
       set))

(defn rehearsal-routine-ready?
  [world]
  (set/subset? rehearsal-precondition-ids
               (apartment-fact-ids world)))

(defn- mural-reversal-goal
  [world]
  (let [context-id (mural-context-id world)]
    (->> (vals (:goals world))
         (filter #(and (= :reversal (:goal-type %))
                       (= context-id (:trigger-context-id %))))
         (sort-by (comp str :id))
         first)))

(defn- mural-rationalization-goal
  [world]
  (let [context-id (mural-context-id world)]
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
                  :object-state (current-object-state world)
                  :regulation-mode (derive-regulation-mode tony-state)
                  :appraisal-mode (derive-appraisal-mode tony-state)
                  :raw-input-ids (get-in world
                                         [:graffito-rehearsal-slice
                                          :mural-raw-input-ids])
                  :selected-family (selected-mural-family mural-choice)
                  :reversal-goal-id (some-> (:mural-reversal-goal mural-choice) :id)
                  :rationalization-goal-id (some-> (:mural-rationalization-goal mural-choice) :id)}]
     [world summary])))

(defn- apply-rehearsal-state-delta
  [world]
  (-> world
      (assoc-in [:character-state tony-id] rehearsed-tony-state)
      (assoc-in [:graffito-rehearsal-slice :objects] rehearsed-object-state)))

(defn run-apartment-rehearsal-cycle
  "Run one authored apartment rehearsal routine. This is intentionally
  benchmark-local: it proves rehearsal can change Tony's embodied control
  without introducing a full rehearsal planner yet."
  [world]
  (when-not (rehearsal-routine-ready? world)
    (throw (ex-info "Graffito Slice 3 expected authored rehearsal preconditions"
                    {:missing (sort (set/difference rehearsal-precondition-ids
                                                    (apartment-fact-ids world)))})))
  (let [tony-state-before (current-tony-state world)
        object-state-before (current-object-state world)
        world (append-cycle world
                            {:fixture-step {:step :apartment-rehearsal
                                            :family :rehearsal
                                            :situation-id apartment-situation-id}})
        world (apply-rehearsal-state-delta world)
        summary {:cycle (:cycle world)
                 :step :apartment-rehearsal
                 :family :rehearsal
                 :situation-id apartment-situation-id
                 :routine-id rehearsal-routine-id
                 :operator-id rehearsal-operator-id
                 :winner-origin :authored-routine
                 :precondition-ids (sort rehearsal-precondition-ids)
                 :tony-state-before tony-state-before
                 :tony-state-after (current-tony-state world)
                 :object-state-before object-state-before
                 :object-state-after (current-object-state world)
                 :regulation-mode-after (derive-regulation-mode
                                         (current-tony-state world))}
        world (trace/merge-latest-cycle
               world
               {:graffito_rehearsal_slice
                {:routine-id rehearsal-routine-id
                 :operator-id rehearsal-operator-id
                 :tony-state-before tony-state-before
                 :tony-state-after (:tony-state-after summary)
                 :object-state-before object-state-before
                 :object-state-after (:object-state-after summary)
                 :regulation-mode-after (:regulation-mode-after summary)}})]
    [world summary]))

(defn carry-state-into-fresh-world
  [world]
  (let [fresh-world (build-world)
        tony-state (current-tony-state world)
        object-state (current-object-state world)]
    (-> fresh-world
        (assoc-in [:character-state tony-id] tony-state)
        (assoc-in [:graffito-rehearsal-slice :objects] object-state)
        project-mural-appraisal)))

(defn- summarize-mural-rationalization
  [world goal family-plan]
  {:cycle (:cycle world)
   :step :mural-after-rehearsal-run
   :family :rationalization
   :situation-id mural-situation-id
   :goal-id (:id goal)
   :frame-id (get-in family-plan [:selection :rationalization_frame_id])
   :winner-origin (get-in family-plan [:selection :rationalization_frame_winner_origin])
   :family-plan-episode-id (get-in family-plan [:selection :family_plan_episode_id])
   :admission-status (:admission-status family-plan)
   :rule-provenance (:rule-provenance family-plan)})

(defn- run-mural-rationalization-after-rehearsal
  [world]
  (let [world (families/activate-family-goals world)
        goal (or (mural-rationalization-goal world)
                 (throw (ex-info "Graffito Slice 3 expected mural rationalization after rehearsal"
                                 {})))
        world (append-cycle world
                            {:selected-goal (trace-selected-goal world (:id goal))
                             :fixture-step {:step :mural-after-rehearsal-run
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
    :mural-before-rehearsal
    (str "Cycle " (:cycle cycle-summary)
         " | mural before rehearsal"
         " | selected-family: " (some-> (:selected-family cycle-summary) name)
         " | appraisal: " (some-> (:appraisal-mode cycle-summary) name))

    :apartment-rehearsal
    (str "Cycle " (:cycle cycle-summary)
         " | apartment rehearsal"
         " | operator: " (some-> (:operator-id cycle-summary) name)
         " | regulation-after: " (some-> (:regulation-mode-after cycle-summary) name))

    :mural-after-rehearsal
    (str "Cycle " (:cycle cycle-summary)
         " | mural after rehearsal"
         " | selected-family: " (some-> (:selected-family cycle-summary) name)
         " | appraisal: " (some-> (:appraisal-mode cycle-summary) name))

    :mural-after-rehearsal-run
    (str "Cycle " (:cycle cycle-summary)
         " | mural rationalization after rehearsal"
         " | frame: " (some-> (:frame-id cycle-summary) name))

    (str "Cycle " (:cycle cycle-summary))))

(defn benchmark-metadata
  ([] (benchmark-metadata {}))
  ([overrides]
   (merge {:started_at "2026-03-23T00:00:00Z"
           :engine_path "kernel/src/daydreamer"
           :seed "graffito-rehearsal-slice"
           :benchmark "graffito_rehearsal_slice"
           :world_path benchmark-brief-path
           :graph_path "benchmark: code-defined"
           :feedback_path nil
           :palette_path "graffito_rehearsal_slice"
           :graph_nodes 2
           :graph_edges 1}
          overrides)))

(defn run-slice
  "Run the third deterministic Graffito slice.

  1. read the mural before rehearsal
  2. run one authored apartment rehearsal routine and update Tony state
  3. reproject the same mural raw inputs through the new state
  4. show that mural family choice flips and the new choice can run"
  ([] (run-slice {}))
  ([{:keys [world]
     :or {world (build-world)}}]
   (let [[_ baseline] (mural-choice-read world :mural-before-rehearsal)
         [rehearsal-world rehearsal] (run-apartment-rehearsal-cycle world)
         reappraised-world (carry-state-into-fresh-world rehearsal-world)
         [_ reappraised] (mural-choice-read reappraised-world
                                            :mural-after-rehearsal)
         [final-world mural-run] (run-mural-rationalization-after-rehearsal
                                  reappraised-world)
         cycle-summaries [baseline rehearsal reappraised mural-run]]
     {:world final-world
      :rehearsal-world rehearsal-world
      :baseline baseline
      :reappraised reappraised
      :cycle-summaries cycle-summaries
      :summaries (mapv summary-line cycle-summaries)
      :log (trace/reporter-log final-world (benchmark-metadata))})))

(comment
  (run-slice))
