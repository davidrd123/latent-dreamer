(ns daydreamer.benchmarks.graffito-miniworld
  "First autonomous Graffito miniworld.

  This moves beyond the scripted slices without jumping to creative-output
  evaluation. The world is still small and typed: street overload, apartment
  support, mural crisis. Tony carries a transient regulation state across
  cycles. The benchmark reruns appraisal every cycle, lets the kernel execute
  live families where they exist, and uses one benchmark-local rehearsal
  routine where the kernel does not yet provide a rehearsal planner."
  (:require [daydreamer.context :as cx]
            [daydreamer.goal-families :as families]
            [daydreamer.goals :as goals]
            [daydreamer.runner :as runner]
            [daydreamer.trace :as trace]))

(def benchmark-brief-path
  "Source brief for the Graffito miniworld benchmark."
  "daydreaming/Notes/experiential-design/24-graffito-kernel-brief.md")

(def ^:private tony-id :tony)
(def ^:private street-situation-id :graffito_street_overload)
(def ^:private apartment-situation-id :graffito_grandma_apartment)
(def ^:private mural-situation-id :graffito_night_mural)

(def ^:private street-goal-id :g_tony_get_past_the_laughing_line)
(def ^:private street-leaf-goal-id :g_tony_make_it_to_shelter)
(def ^:private mural-threat-goal-id :g_tony_hold_line_at_mural)
(def ^:private mural-challenge-goal-id :g_tony_make_the_mark_hold)
(def ^:private apartment-goal-id mural-challenge-goal-id)
(def ^:private mural-leaf-goal-id :g_tony_stay_unseen)

(def ^:private street-emotion-id :e_tony_mockery_panic)
(def ^:private apartment-emotion-id :e_tony_wrongness_shame)
(def ^:private mural-threat-emotion-id :e_mural_capture_panic)
(def ^:private mural-challenge-emotion-id :e_mural_mark_doubt)

(def ^:private street-cause-id :fc_mockery_turns_exit_into_gauntlet)
(def ^:private apartment-frame-id :rf_intensity_becomes_style)
(def ^:private mural-threat-cause-id :fc_cop_light_turns_art_to_capture)
(def ^:private mural-challenge-frame-id :rf_rhythm_can_hold_the_mark)
(def ^:private rehearsal-routine-id :rt_counted_stroke_rehearsal)
(def ^:private rehearsal-operator-id :op_counted_stroke_with_sketchbook)

(def ^:private street-leaf-objective
  {:fact/type :assumption
   :fact/id :doorway_stays_reachable})

(def ^:private mural-leaf-objective
  {:fact/type :assumption
   :fact/id :wall_stays_closed})

(def ^:private default-tony-state
  {:sensory-load 0.84
   :entrainment 0.12
   :felt-agency 0.2
   :perceived-control 0.23})

(def ^:private default-object-state
  {:can {:phase :dormant}
   :mural {:phase :under_siege}})

(def ^:private initial-situations-state
  {street-situation-id {:activation 0.84
                        :ripeness 0.78
                        :baseline-activation 0.48
                        :baseline-ripeness 0.56
                        :visits 0}
   apartment-situation-id {:activation 0.53
                           :ripeness 0.64
                           :baseline-activation 0.34
                           :baseline-ripeness 0.48
                           :visits 0}
   mural-situation-id {:activation 0.6
                       :ripeness 0.74
                       :baseline-activation 0.42
                       :baseline-ripeness 0.58
                       :visits 0}})

(def ^:private persistent-root-keys
  [:cycle
   :trace
   :episodes
   :episode-index
   :id-counter
   :recent-episodes
   :recent-indices
   :rule-access
   :provenance-episode-index
   :support-episode-index
   :roving-episodes])

(defn- clamp01
  [value]
  (-> value double (max 0.0) (min 1.0)))

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

(def ^:private street-facts
  [(typed-fact :present-actor :tony_present)
   (typed-fact :present-actor :older_kids_present)
   (typed-fact :present-actor :teacher_distant)
   (typed-fact :relationship :tony_feels_seen_as_wrong)
   (typed-fact :role-obligation :tony_must_get_home)
   (typed-fact :exposure :school_release_is_loud)
   (typed-fact :exposure :hallway_is_socially_exposing)
   (typed-fact :exposure :older_kids_clock_tony)
   (typed-fact :recent-event :older_kids_mock_tony)
   (typed-fact :recent-event :dismissal_bell_jolts_tony)
   (typed-fact :sensorimotor-input :school_bell_floods_attention)
   (typed-fact :sensorimotor-input :crowd_noise_fragments_precision)
   (typed-fact :sensorimotor-input :mockery_turns_exit_into_gauntlet)])

(def ^:private apartment-facts
  [(typed-fact :present-actor :tony_present)
   (typed-fact :present-actor :monk_present)
   (typed-fact :present-actor :grandma_present)
   (typed-fact :relationship :tony_trusts_monk)
   (typed-fact :relationship :tony_seeks_recognition_from_monk)
   (typed-fact :relationship :grandma_distrusts_monk_mission)
   (typed-fact :role-obligation :grandma_must_protect_tony)
   (typed-fact :role-obligation :monk_owes_presence_not_only_mission)
   (typed-fact :artifact-state :sketchbook_is_regulation_tool)
   (typed-fact :artifact-state :can_is_inheritance)
   (typed-fact :recent-event :monk_returned_home)
   (typed-fact :recent-event :grandma_challenged_monk)
   (typed-fact :sensorimotor-input :monk_counts_a_holdable_beat)
   (typed-fact :sensorimotor-input :sketchbook_offers_small_surface)
   (typed-fact :sensorimotor-input :repeated_stroke_returns_precision)
   (typed-fact :person-object-relation :monk_co_regulates_tony_with_rhythm)
   (typed-fact :person-object-relation :sketchbook_turns_sprawl_into_sequence)
   (typed-fact :person-object-relation :tony_trusts_can_lineage)
   (typed-fact :cross-layer-correspondence :grandma_counterpart_of_motherload)
   (typed-fact :cross-layer-correspondence :school_counterpart_of_castle)
   (typed-fact :appraisal :crookedness_can_be_style)])

(def ^:private apartment-reframe-facts
  [(typed-fact :cross-layer-correspondence :can_corresponds_to_sensory_capacity)
   (typed-fact :sensorimotor-input :light_jolt_floods_attention)
   (typed-fact :sensorimotor-input :noise_fragments_precision)
   (typed-fact :person-object-relation :tony_trusts_can_lineage)
   (typed-fact :person-object-relation :sketchbook_turns_sprawl_into_sequence)
   (typed-fact :person-object-relation :monk_co_regulates_tony_with_rhythm)
   (typed-fact :cross-layer-correspondence :grandma_counterpart_of_motherload)
   (typed-fact :cross-layer-correspondence :school_counterpart_of_castle)
   (typed-fact :artifact-state :can_phase_responsive)
   (typed-fact :appraisal :intensity_can_be_style)
   (typed-fact :appraisal :creation_restores_control)])

(def ^:private rehearsal-precondition-ids
  #{:monk_counts_a_holdable_beat
    :sketchbook_offers_small_surface
    :repeated_stroke_returns_precision
    :monk_co_regulates_tony_with_rhythm})

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

(def ^:private street-counterfactual-facts
  [(typed-fact :appraisal :mockery_breaks_off)
   (typed-fact :relationship :teacher_notices_tony_as_person)])

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

(def ^:private tony-baseline-state
  default-tony-state)

(defn current-tony-state
  [world]
  (get-in world [:character-state tony-id]))

(defn current-object-state
  [world]
  (get-in world [:graffito-miniworld :objects]))

(defn current-situations-state
  [world]
  (get-in world [:graffito-miniworld :situations-state]))

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

(defn- clamp-tony-state
  [tony-state]
  (reduce-kv (fn [acc metric value]
               (assoc acc metric (clamp01 value)))
             {}
             tony-state))

(defn- blend-tony-state
  [tony-state deltas]
  (clamp-tony-state
   (reduce-kv (fn [acc metric delta]
                (update acc metric
                        (fn [current]
                          (+ (double (or current 0.0))
                             (double delta)))))
              tony-state
              deltas)))

(defn- decay-tony-state
  [tony-state]
  (clamp-tony-state
   (reduce-kv (fn [acc metric current]
                (let [baseline (get tony-baseline-state metric 0.0)]
                  (assoc acc metric
                         (+ (* 0.86 (double current))
                            (* 0.14 (double baseline))))))
              {}
              tony-state)))

(def ^:private removable-mural-projection-facts
  [{:fact/type :emotion
    :emotion-id mural-threat-emotion-id
    :strength 0.87
    :valence :negative
    :affect :fear}
   {:fact/type :dependency
    :from-id mural-threat-emotion-id
    :to-id mural-threat-goal-id}
   (goal-fact mural-threat-goal-id mural-threat-goal-id :failed nil)
   (goal-fact mural-leaf-goal-id mural-threat-goal-id :runable nil
              {:strength 0.34
               :objective-fact mural-leaf-objective})
   (intends-fact mural-threat-goal-id mural-leaf-goal-id mural-threat-goal-id)
   (failure-cause-fact mural-threat-cause-id
                       mural-threat-goal-id
                       0.89
                       mural-threat-counterfactual-facts)
   (typed-fact :appraisal :same_light_reads_as_threat)
   (typed-fact :appraisal :capture_feels_inevitable)
   {:fact/type :emotion
    :emotion-id mural-challenge-emotion-id
    :strength 0.74
    :valence :negative
    :affect :shame}
   {:fact/type :dependency
    :from-id mural-challenge-emotion-id
    :to-id mural-challenge-goal-id}
   (goal-fact mural-challenge-goal-id mural-challenge-goal-id :failed nil)
   (rationalization-frame-fact mural-challenge-frame-id
                               mural-challenge-goal-id
                               0.9
                               mural-challenge-reframe-facts)
   (typed-fact :appraisal :same_light_reads_as_challenge)
   (typed-fact :appraisal :rhythm_supports_precision)])

(defn project-mural-appraisal
  "Project Tony's carried state into the mural situation as derived appraisal."
  [world]
  (let [mural-context-id (get-in world [:graffito-miniworld :contexts :mural])
        tony-state (current-tony-state world)
        regulation-mode (derive-regulation-mode tony-state)
        appraisal-mode (derive-appraisal-mode tony-state)
        projected-facts
        (if (= :challenge-dominant appraisal-mode)
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
                      mural-context-id)
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
                      mural-context-id)
           (goal-fact mural-leaf-goal-id
                      mural-threat-goal-id
                      :runable
                      mural-context-id
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
           (typed-fact :appraisal :capture_feels_inevitable)])
        world (retract-facts world mural-context-id removable-mural-projection-facts)
        world (assert-facts world mural-context-id projected-facts)]
    (assoc-in world
              [:graffito-miniworld :mural-projection]
              {:appraisal-mode appraisal-mode
               :regulation-mode regulation-mode
               :tony-state tony-state
               :object-state (current-object-state world)})))

(defn- street-reversal-facts
  [context-id]
  [{:fact/type :situation
    :fact/id street-situation-id}
   {:fact/type :emotion
    :emotion-id street-emotion-id
    :strength 0.82
    :valence :negative
    :affect :fear}
   {:fact/type :dependency
    :from-id street-emotion-id
    :to-id street-goal-id}
   (goal-fact street-goal-id street-goal-id :failed context-id)
   (goal-fact street-leaf-goal-id
              street-goal-id
              :runable
              context-id
              {:strength 0.36
               :objective-fact street-leaf-objective})
   (intends-fact street-goal-id street-leaf-goal-id street-goal-id)
   (failure-cause-fact street-cause-id
                       street-goal-id
                       0.88
                       street-counterfactual-facts)])

(defn- apartment-support-facts
  [context-id]
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
   (goal-fact apartment-goal-id apartment-goal-id :failed context-id)
   (rationalization-frame-fact apartment-frame-id
                               apartment-goal-id
                               0.91
                               apartment-reframe-facts)])

(defn build-world
  "Create the first autonomous Graffito miniworld."
  []
  (let [world (-> (runner/initial-world)
                  (assoc-in [:character-state tony-id] default-tony-state))
        root-id (:reality-context world)
        [world street-context-id] (cx/sprout world root-id)
        [world apartment-context-id] (cx/sprout world root-id)
        [world mural-context-id] (cx/sprout world root-id)
        world (assert-facts world
                            street-context-id
                            (concat street-facts
                                    [street-leaf-objective]
                                    (street-reversal-facts street-context-id)))
        world (assert-facts world
                            apartment-context-id
                            (concat apartment-facts
                                    (apartment-support-facts apartment-context-id)))
        world (assert-facts world
                            mural-context-id
                            (concat mural-raw-input-facts
                                    [mural-leaf-objective
                                     {:fact/type :situation
                                      :fact/id mural-situation-id}
                                     (typed-fact :present-actor :tony_present)
                                     (typed-fact :present-actor :monk_present)
                                     (typed-fact :relationship :monk_teaches_flow)]))
        world (assoc world
                     :graffito-miniworld
                     {:root-id root-id
                      :contexts {:street street-context-id
                                 :apartment apartment-context-id
                                 :mural mural-context-id}
                      :objects default-object-state
                      :situations-state initial-situations-state
                      :recent-choices []
                      :flip-history []
                      :mural-raw-input-ids (->> mural-raw-input-facts
                                                (keep :fact/id)
                                                vec)
                      :rehearsal-routine {:routine-id rehearsal-routine-id
                                          :operator-id rehearsal-operator-id
                                          :precondition-ids (sort rehearsal-precondition-ids)}})]
    (project-mural-appraisal world)))

(defn- rebuild-world-with-state
  [world]
  (let [fresh-world (build-world)
        carried-root (select-keys world persistent-root-keys)
        decayed-tony-state (decay-tony-state (current-tony-state world))
        object-state (current-object-state world)
        prior-gm (:graffito-miniworld world)
        fresh-gm (:graffito-miniworld fresh-world)
        decayed-situations
        (reduce-kv
         (fn [acc situation-id situation]
           (let [baseline-activation (:baseline-activation situation)
                 baseline-ripeness (:baseline-ripeness situation)
                 next-situation
                 (-> situation
                     (update :activation
                             (fn [value]
                               (clamp01 (+ (* 0.88 (double (or value 0.0)))
                                           (* 0.12 (double baseline-activation))))))
                     (update :ripeness
                             (fn [value]
                               (clamp01 (+ (* 0.9 (double (or value 0.0)))
                                           (* 0.1 (double baseline-ripeness)))))))]
             (assoc acc situation-id next-situation)))
         {}
         (:situations-state prior-gm))
        world (merge fresh-world carried-root)
        world (assoc-in world [:character-state tony-id] decayed-tony-state)
        world (assoc world
                     :graffito-miniworld
                     (merge fresh-gm
                            (select-keys prior-gm [:recent-choices
                                                   :flip-history])))
        world (assoc-in world [:graffito-miniworld :objects] object-state)
        world (assoc-in world [:graffito-miniworld :situations-state] decayed-situations)]
    (project-mural-appraisal world)))

(defn- reset-cycle-events
  [world]
  (assoc world
         :activation-events []
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

(defn- street-context-id
  [world]
  (get-in world [:graffito-miniworld :contexts :street]))

(defn- apartment-context-id
  [world]
  (get-in world [:graffito-miniworld :contexts :apartment]))

(defn- mural-context-id
  [world]
  (get-in world [:graffito-miniworld :contexts :mural]))

(defn rehearsal-routine-ready?
  [world]
  (let [fact-ids (->> (cx/visible-facts world (apartment-context-id world))
                      (keep :fact/id)
                      set)]
    (every? fact-ids rehearsal-precondition-ids)))

(defn- candidate
  [{:keys [kind situation-id family operator-key operator-id goal-id trigger strength reasons]}]
  {:kind kind
   :situation-id situation-id
   :family family
   :operator-key operator-key
   :operator-id operator-id
   :goal-id goal-id
   :trigger trigger
   :strength (clamp01 strength)
   :reasons (vec reasons)})

(defn- candidate-origin-keyword
  [origin]
  (cond
    (keyword? origin) origin
    (string? origin) (keyword origin)
    :else nil))

(defn- apply-fatigue-adjustment
  [world candidate]
  (let [recent (get-in world [:graffito-miniworld :recent-choices] [])
        last-choice (last recent)
        same-situation-count (count (filter #(= (:situation-id %) (:situation-id candidate))
                                            (take-last 2 recent)))
        same-family-count (count (filter #(= (:family %) (:family candidate))
                                         (take-last 3 recent)))
        exact-repeat? (and last-choice
                           (= (:operator-key last-choice) (:operator-key candidate)))
        bridge-bonus (cond
                       (and (= (:situation-id last-choice) street-situation-id)
                            (= (:situation-id candidate) apartment-situation-id))
                       0.06

                       (and (= (:situation-id last-choice) apartment-situation-id)
                            (= (:situation-id candidate) mural-situation-id))
                       0.14

                       (and (= (:situation-id last-choice) mural-situation-id)
                            (= (:situation-id candidate) apartment-situation-id))
                       0.02

                       :else 0.0)
        fatigue-penalty (+ (* same-situation-count 0.08)
                           (* same-family-count 0.05)
                           (if exact-repeat? 0.16 0.0))]
    (cond-> (update candidate :strength #(clamp01 (+ % bridge-bonus (- fatigue-penalty))))
      exact-repeat? (update :reasons conj :exact_repeat_penalty)
      (pos? same-situation-count) (update :reasons conj :situation_fatigue)
      (pos? same-family-count) (update :reasons conj :family_fatigue)
      (pos? bridge-bonus) (update :reasons conj :bridge_pull))))

(defn compute-candidates
  "Compute autonomous miniworld candidates from live goals and Tony state."
  [world]
  (let [reversal-triggers (families/reversal-activation-candidates world)
        rationalization-triggers (families/rationalization-activation-candidates world)
        trigger-context-id (fn [trigger]
                             (or (:old-context-id trigger)
                                 (:context-id trigger)))
        street-reversal-trigger (some-> (->> reversal-triggers
                                             (filter #(= (street-context-id world)
                                                         (trigger-context-id %)))
                                             first)
                                        (assoc :goal-type :reversal))
        mural-reversal-trigger (some-> (->> reversal-triggers
                                            (filter #(= (mural-context-id world)
                                                        (trigger-context-id %)))
                                            first)
                                       (assoc :goal-type :reversal))
        apartment-rationalization-trigger
        (some-> (->> rationalization-triggers
                     (filter #(= (apartment-context-id world)
                                 (trigger-context-id %)))
                     first)
                (assoc :goal-type :rationalization))
        mural-rationalization-trigger
        (some-> (->> rationalization-triggers
                     (filter #(= (mural-context-id world)
                                 (trigger-context-id %)))
                     first)
                (assoc :goal-type :rationalization))
        tony-state (current-tony-state world)
        situations (current-situations-state world)
        regulation-mode (derive-regulation-mode tony-state)
        appraisal-mode (get-in world [:graffito-miniworld :mural-projection :appraisal-mode])
        load (:sensory-load tony-state)
        entrainment (:entrainment tony-state)
        agency (:felt-agency tony-state)
        control (:perceived-control tony-state)
        control-deficit (- 1.0 control)
        agency-deficit (- 1.0 agency)
        street (get situations street-situation-id)
        apartment (get situations apartment-situation-id)
        mural (get situations mural-situation-id)
        candidates
        (cond-> []
          street-reversal-trigger
          (conj
           (candidate
            {:kind :goal
             :situation-id street-situation-id
             :family :reversal
             :operator-key :street-reversal
             :trigger street-reversal-trigger
             :strength (+ (* 0.38 (:activation street))
                          (* 0.16 (:ripeness street))
                          (* 0.16 load)
                          (* 0.18 control-deficit)
                          (if (= regulation-mode :overloaded) 0.10 0.0))
             :reasons [:street_overload :social_exposure :failed_exit]}))

          apartment-rationalization-trigger
          (conj
           (candidate
            {:kind :goal
             :situation-id apartment-situation-id
             :family :rationalization
             :operator-key :apartment-rationalization
             :trigger apartment-rationalization-trigger
             :strength (+ (* 0.34 (:activation apartment))
                          (* 0.14 (:ripeness apartment))
                          (* 0.14 load)
                          (* 0.2 agency-deficit)
                          (* 0.16 control-deficit)
                          (if (< agency 0.45) 0.15 0.0)
                          (if (= regulation-mode :overloaded) 0.08 0.0)
                          (if (>= agency 0.45) -0.08 0.0)
                          (if (contains? #{:entraining :flowing :creating}
                                         regulation-mode)
                            -0.16
                            0.0))
             :reasons [:support_need :meaning_repair :family_repair]}))

          (rehearsal-routine-ready? world)
          (conj
           (candidate
            {:kind :routine
             :situation-id apartment-situation-id
             :family :rehearsal
             :operator-key :apartment-rehearsal
             :operator-id rehearsal-operator-id
             :strength (+ (* 0.30 (:activation apartment))
                          (* 0.12 (:ripeness apartment))
                          (* 0.16 control-deficit)
                          (* 0.12 entrainment)
                          (if (>= agency 0.4) 0.16 0.0)
                          (if (contains? #{:bracing :entraining} regulation-mode) 0.12 0.0)
                          (if (>= control 0.72) -0.22 0.0)
                          (if (= regulation-mode :creating) -0.22 0.0))
             :reasons [:support_need :rhythm_routine :control_repair]}))

          (and (= :threat-dominant appraisal-mode)
               mural-reversal-trigger)
          (conj
           (candidate
            {:kind :goal
             :situation-id mural-situation-id
             :family :reversal
             :operator-key :mural-reversal
             :trigger mural-reversal-trigger
             :strength (+ (* 0.36 (:activation mural))
                          (* 0.18 (:ripeness mural))
                          (* 0.15 load)
                          (* 0.18 control-deficit)
                          0.12)
             :reasons [:mural_threat :cop_pressure :failed_mark]}))

          (and (= :challenge-dominant appraisal-mode)
               mural-rationalization-trigger)
          (conj
           (candidate
            {:kind :goal
             :situation-id mural-situation-id
             :family :rationalization
             :operator-key :mural-rationalization
             :trigger mural-rationalization-trigger
             :strength (+ (* 0.38 (:activation mural))
                          (* 0.16 (:ripeness mural))
                          (* 0.16 agency)
                          (* 0.18 control)
                          0.22)
             :reasons [:mural_reread :held_mark :challenge_appraisal]})))]
    [world
     (->> candidates
          (mapv #(apply-fatigue-adjustment world %))
          (sort-by (juxt (comp - :strength)
                         (comp str :situation-id)
                         (comp str :family))))]))

(defn- choose-candidate
  [candidates]
  (first candidates))

(defn- update-tony-and-object-state
  [world operator-key]
  (let [tony-state (current-tony-state world)
        object-state (current-object-state world)
        [next-tony-state next-object-state]
        (case operator-key
          :street-reversal
          [(blend-tony-state tony-state
                             {:sensory-load 0.06
                              :entrainment -0.02
                              :felt-agency -0.05
                              :perceived-control -0.05})
           (assoc object-state
                  :can {:phase :dormant}
                  :mural {:phase :under_siege})]

          :apartment-rationalization
          [(blend-tony-state tony-state
                             {:sensory-load -0.08
                              :entrainment 0.16
                              :felt-agency 0.26
                              :perceived-control 0.22})
           (assoc object-state
                  :can {:phase :warming}
                  :mural {:phase :under_siege})]

          :apartment-rehearsal
          [(blend-tony-state tony-state
                             {:sensory-load -0.04
                              :entrainment 0.30
                              :felt-agency 0.18
                              :perceived-control 0.24})
           (assoc object-state
                  :can {:phase :attuning}
                  :mural {:phase :reachable})]

          :mural-reversal
          [(blend-tony-state tony-state
                             {:sensory-load 0.04
                              :entrainment -0.04
                              :felt-agency -0.03
                              :perceived-control -0.04})
           (assoc object-state
                  :can {:phase :warming}
                  :mural {:phase :under_siege})]

          :mural-rationalization
          [(blend-tony-state tony-state
                             {:sensory-load -0.03
                              :entrainment 0.05
                              :felt-agency 0.08
                              :perceived-control 0.08})
           (assoc object-state
                  :can {:phase :responsive}
                  :mural {:phase :reachable})]

          [tony-state object-state])]
    (-> world
        (assoc-in [:character-state tony-id] next-tony-state)
        (assoc-in [:graffito-miniworld :objects] next-object-state))))

(defn- advance-situations
  [world operator-key]
  (let [situations (current-situations-state world)
        updates (case operator-key
                  :street-reversal
                  {street-situation-id {:activation -0.18 :ripeness -0.05}
                   apartment-situation-id {:activation 0.24 :ripeness 0.04}
                   mural-situation-id {:activation 0.08 :ripeness 0.02}}

                  :apartment-rationalization
                  {apartment-situation-id {:activation -0.18 :ripeness -0.03}
                   mural-situation-id {:activation 0.28 :ripeness 0.06}
                   street-situation-id {:activation -0.04}}

                  :apartment-rehearsal
                  {apartment-situation-id {:activation -0.22 :ripeness -0.05}
                   mural-situation-id {:activation 0.32 :ripeness 0.08}
                   street-situation-id {:activation -0.03}}

                  :mural-reversal
                  {mural-situation-id {:activation -0.06 :ripeness 0.02}
                   apartment-situation-id {:activation 0.16 :ripeness 0.03}
                   street-situation-id {:activation 0.05}}

                  :mural-rationalization
                  {mural-situation-id {:activation -0.18 :ripeness -0.04}
                   street-situation-id {:activation 0.12 :ripeness 0.03}
                   apartment-situation-id {:activation 0.04}}

                  {})]
    (assoc-in world
              [:graffito-miniworld :situations-state]
              (reduce-kv
               (fn [acc situation-id situation]
                 (let [{:keys [activation ripeness]} (get updates situation-id {})]
                   (assoc acc
                          situation-id
                          (-> situation
                              (update :activation #(clamp01 (+ (double (or % 0.0))
                                                               (double (or activation 0.0)))))
                              (update :ripeness #(clamp01 (+ (double (or % 0.0))
                                                             (double (or ripeness 0.0)))))
                              (update :visits (fn [value]
                                                (+ (long (or value 0))
                                                   (if (= situation-id
                                                          (case operator-key
                                                            :street-reversal street-situation-id
                                                            :apartment-rationalization apartment-situation-id
                                                            :apartment-rehearsal apartment-situation-id
                                                            :mural-reversal mural-situation-id
                                                            :mural-rationalization mural-situation-id
                                                            nil))
                                                     1
                                                     0))))))))
               {}
               situations))))

(defn- update-recent-choices
  [world candidate]
  (update-in world
             [:graffito-miniworld :recent-choices]
             (fn [recent]
               (->> (conj (vec recent)
                          (select-keys candidate [:situation-id :family :operator-key]))
                    (take-last 6)
                    vec))))

(defn- trace-cycle-base
  [world chosen-candidate top-candidates]
  (cond-> {:graffito_miniworld
           {:selected-candidate (select-keys chosen-candidate
                                             [:situation-id :family :operator-key :strength :reasons])
            :top-candidates (mapv #(select-keys %
                                                [:situation-id :family :operator-key :strength :reasons])
                                  (take 4 top-candidates))
            :mural-projection-before (get-in world [:graffito-miniworld :mural-projection])}}
    (:goal-id chosen-candidate)
    (assoc :selected-goal (trace-selected-goal world (:goal-id chosen-candidate)))))

(defn- activate-family-goal-from-trigger
  [world {:keys [goal-type situation-id emotion-id emotion-strength
                 selection-policy selection-reasons activation-policy
                 activation-reasons context-id old-context-id failed-goal-id
                 frame-id rule-provenance]}]
  (let [activation-context-id (or (:reality-lookahead world)
                                  (:reality-context world))
        trigger-context-id (or old-context-id context-id)
        [world goal-id]
        (goals/activate-top-level-goal
         world
         activation-context-id
         {:goal-type goal-type
          :planning-type :imaginary
          :strength emotion-strength
          :main-motiv emotion-id
          :situation-id situation-id
          :trigger-context-id trigger-context-id
          :trigger-failed-goal-id failed-goal-id
          :trigger-emotion-id emotion-id
          :trigger-emotion-strength emotion-strength
          :trigger-frame-id frame-id
          :activation-policy (or activation-policy selection-policy)
          :activation-reasons (or activation-reasons selection-reasons)})]
    [(-> world
         (assoc-in [:goals goal-id :rule-provenance] rule-provenance)
         (update :activation-events
                 (fnil conj [])
                 {:goal-id goal-id
                  :goal-type goal-type
                  :trigger-context-id trigger-context-id
                  :failed-goal-id failed-goal-id
                  :emotion-id emotion-id
                  :emotion-strength emotion-strength
                  :frame-id frame-id
                  :activation-policy (or activation-policy selection-policy)
                  :activation-reasons (or activation-reasons selection-reasons)
                  :rule-provenance rule-provenance}))
     goal-id]))

(defn- execute-goal-candidate
  [world chosen-candidate top-candidates]
  (let [[world goal-id] (activate-family-goal-from-trigger world (:trigger chosen-candidate))
        chosen-candidate (assoc chosen-candidate :goal-id goal-id)
        tony-state-before (current-tony-state world)
        object-state-before (current-object-state world)
        appraisal-before (get-in world [:graffito-miniworld :mural-projection :appraisal-mode])
        regulation-before (get-in world [:graffito-miniworld :mural-projection :regulation-mode])
        world (append-cycle world (trace-cycle-base world chosen-candidate top-candidates))
        [world family-plan] (families/run-family-plan world {:goal-id (:goal-id chosen-candidate)})
        world (-> world
                  (update-tony-and-object-state (:operator-key chosen-candidate))
                  (advance-situations (:operator-key chosen-candidate))
                  (update-recent-choices chosen-candidate)
                  project-mural-appraisal)
        appraisal-after (get-in world [:graffito-miniworld :mural-projection :appraisal-mode])
        regulation-after (get-in world [:graffito-miniworld :mural-projection :regulation-mode])
        family-plan-episode-id (get-in family-plan [:selection :family_plan_episode_id])
        source-candidates (vec (or (get-in family-plan [:selection :rationalization_frame_candidates])
                                   (get-in family-plan [:selection :reversal_counterfactual_candidates])
                                   []))
        winner-origin (or (get-in family-plan [:selection :rationalization_frame_winner_origin])
                          (get-in family-plan [:selection :reversal_counterfactual_winner_origin]))
        dynamic-source-candidate-count (count (filter #(= :dynamic
                                                          (candidate-origin-keyword
                                                           (:origin %)))
                                                      source-candidates))
        authored-source-candidate-count (count (filter #(= :authored
                                                           (candidate-origin-keyword
                                                            (:origin %)))
                                                       source-candidates))
        summary {:cycle (:cycle world)
                 :selected-situation-id (:situation-id chosen-candidate)
                 :selected-family (:family chosen-candidate)
                 :operator-key (:operator-key chosen-candidate)
                 :goal-id (:goal-id chosen-candidate)
                 :top-candidates (take 4 top-candidates)
                 :tony-state-before tony-state-before
                 :tony-state-after (current-tony-state world)
                 :object-state-before object-state-before
                 :object-state-after (current-object-state world)
                 :regulation-mode-before regulation-before
                 :regulation-mode-after regulation-after
                 :mural-appraisal-before appraisal-before
                 :mural-appraisal-after appraisal-after
                 :reappraisal-flip? (not= appraisal-before appraisal-after)
                 :family-plan-episode-id family-plan-episode-id
                 :admission-status (:admission-status family-plan)
                 :winner-origin winner-origin
                 :source-candidate-count (count source-candidates)
                 :dynamic-source-candidate-count dynamic-source-candidate-count
                 :authored-source-candidate-count authored-source-candidate-count
                 :dynamic-source-visible? (pos? dynamic-source-candidate-count)
                 :dynamic-source-won? (= :dynamic
                                         (candidate-origin-keyword winner-origin))
                 :frame-id (get-in family-plan [:selection :rationalization_frame_id])
                 :source-id (get-in family-plan [:selection :reversal_counterfactual_source])
                 :rule-provenance (:rule-provenance family-plan)
                 :stored-episode-count (count (:episodes world))}]
    [(trace/merge-latest-cycle
      world
      {:selection (:selection family-plan)
       :sprouted (:sprouted-context-ids family-plan)
       :mutations (:mutation-events world)
       :rule-provenance (:rule-provenance family-plan)
       :family-plan-episode-id family-plan-episode-id
       :graffito_miniworld
       {:tony-state-before tony-state-before
        :tony-state-after (:tony-state-after summary)
        :mural-appraisal-before appraisal-before
        :mural-appraisal-after appraisal-after
        :reappraisal-flip? (:reappraisal-flip? summary)}})
     summary]))

(defn- execute-rehearsal-candidate
  [world chosen-candidate top-candidates]
  (let [tony-state-before (current-tony-state world)
        object-state-before (current-object-state world)
        appraisal-before (get-in world [:graffito-miniworld :mural-projection :appraisal-mode])
        regulation-before (get-in world [:graffito-miniworld :mural-projection :regulation-mode])
        world (append-cycle world (trace-cycle-base world chosen-candidate top-candidates))
        world (-> world
                  (update-tony-and-object-state (:operator-key chosen-candidate))
                  (advance-situations (:operator-key chosen-candidate))
                  (update-recent-choices chosen-candidate)
                  project-mural-appraisal)
        appraisal-after (get-in world [:graffito-miniworld :mural-projection :appraisal-mode])
        regulation-after (get-in world [:graffito-miniworld :mural-projection :regulation-mode])
        summary {:cycle (:cycle world)
                 :selected-situation-id (:situation-id chosen-candidate)
                 :selected-family :rehearsal
                 :operator-key (:operator-key chosen-candidate)
                 :operator-id rehearsal-operator-id
                 :routine-id rehearsal-routine-id
                 :top-candidates (take 4 top-candidates)
                 :tony-state-before tony-state-before
                 :tony-state-after (current-tony-state world)
                 :object-state-before object-state-before
                 :object-state-after (current-object-state world)
                 :regulation-mode-before regulation-before
                 :regulation-mode-after regulation-after
                 :mural-appraisal-before appraisal-before
                 :mural-appraisal-after appraisal-after
                 :reappraisal-flip? (not= appraisal-before appraisal-after)
                 :family-plan-episode-id nil
                 :winner-origin nil
                 :source-candidate-count 0
                 :dynamic-source-candidate-count 0
                 :authored-source-candidate-count 0
                 :dynamic-source-visible? false
                 :dynamic-source-won? false
                 :stored-episode-count (count (:episodes world))}]
    [(trace/merge-latest-cycle
      world
      {:graffito_miniworld
       {:operator-id rehearsal-operator-id
        :routine-id rehearsal-routine-id
        :tony-state-before tony-state-before
        :tony-state-after (:tony-state-after summary)
        :mural-appraisal-before appraisal-before
        :mural-appraisal-after appraisal-after
        :reappraisal-flip? (:reappraisal-flip? summary)}})
     summary]))

(defn run-miniworld-cycle
  "Run one autonomous Graffito miniworld cycle."
  [world]
  (let [[world candidates] (compute-candidates world)
        chosen-candidate (choose-candidate candidates)]
    (when-not chosen-candidate
      (throw (ex-info "Graffito miniworld found no candidate to run" {})))
    (case (:kind chosen-candidate)
      :goal (execute-goal-candidate world chosen-candidate candidates)
      :routine (execute-rehearsal-candidate world chosen-candidate candidates))))

(defn- summary-line
  [cycle-summary]
  (str "Cycle " (:cycle cycle-summary)
       " | " (name (:selected-situation-id cycle-summary))
       " | " (name (:selected-family cycle-summary))
       (when-let [operator-id (:operator-id cycle-summary)]
         (str " | operator: " (name operator-id)))
       " | mural " (name (:mural-appraisal-before cycle-summary))
       " -> " (name (:mural-appraisal-after cycle-summary))
       " | regulation " (name (:regulation-mode-before cycle-summary))
       " -> " (name (:regulation-mode-after cycle-summary))))

(defn benchmark-metadata
  ([] (benchmark-metadata {}))
  ([overrides]
   (merge {:started_at "2026-03-23T00:00:00Z"
           :engine_path "kernel/src/daydreamer"
           :seed "graffito-miniworld"
           :benchmark "graffito_miniworld"
           :world_path benchmark-brief-path
           :graph_path "benchmark: code-defined"
           :feedback_path nil
           :palette_path "graffito_miniworld"
           :graph_nodes 3
           :graph_edges 3}
          overrides)))

(defn run-summary
  "Summarize a Graffito miniworld run for stable regression checks and
  accumulation observability."
  [world cycle-summaries]
  (let [episodes (vals (:episodes world))
        family-counts (frequencies (map :selected-family cycle-summaries))
        situation-counts (frequencies (map :selected-situation-id cycle-summaries))
        challenge-mural-cycles (count (filter #(and (= :graffito_night_mural
                                                       (:selected-situation-id %))
                                                    (= :rationalization
                                                       (:selected-family %)))
                                              cycle-summaries))
        reappraisal-flips (count (filter :reappraisal-flip? cycle-summaries))
        dynamic-source-candidate-cycles (count (filter :dynamic-source-visible?
                                                       cycle-summaries))
        dynamic-source-win-cycles (count (filter :dynamic-source-won?
                                                 cycle-summaries))
        dynamic-rationalization-candidate-cycles
        (count (filter #(and (= :rationalization (:selected-family %))
                             (:dynamic-source-visible? %))
                       cycle-summaries))
        dynamic-reversal-candidate-cycles
        (count (filter #(and (= :reversal (:selected-family %))
                             (:dynamic-source-visible? %))
                       cycle-summaries))]
    {:cycle-count (count cycle-summaries)
     :family-counts family-counts
     :situation-counts situation-counts
     :reappraisal-flips reappraisal-flips
     :challenge-mural-cycles challenge-mural-cycles
     :stored-episode-count (count episodes)
     :episodes-with-use-history (count (filter #(seq (:use-history %)) episodes))
     :episodes-with-promotion-history (count (filter #(seq (:promotion-history %))
                                                     episodes))
     :episodes-with-flags (count (filter #(seq (:anti-residue-flags %)) episodes))
     :durable-episode-count (count (filter #(= :durable (:admission-status %))
                                           episodes))
     :provisional-episode-count (count (filter #(= :provisional (:admission-status %))
                                               episodes))
     :dynamic-source-candidate-cycles dynamic-source-candidate-cycles
     :dynamic-source-win-cycles dynamic-source-win-cycles
     :dynamic-rationalization-candidate-cycles dynamic-rationalization-candidate-cycles
     :dynamic-reversal-candidate-cycles dynamic-reversal-candidate-cycles}))

(defn run-miniworld
  "Run the autonomous Graffito miniworld for `cycles` turns."
  ([] (run-miniworld {}))
  ([{:keys [cycles world]
     :or {cycles 24
          world (build-world)}}]
   (loop [step 0
          world world
          cycle-summaries []]
     (if (>= step cycles)
       {:world world
        :cycle-summaries cycle-summaries
        :run-summary (run-summary world cycle-summaries)
        :summaries (mapv summary-line cycle-summaries)
        :log (trace/reporter-log world (benchmark-metadata))}
       (let [prepared-world (if (zero? step)
                              world
                              (rebuild-world-with-state world))
             [next-world cycle-summary] (run-miniworld-cycle prepared-world)
             next-world (update-in next-world [:graffito-miniworld :flip-history]
                                   (fn [history]
                                     (cond-> (vec history)
                                       (:reappraisal-flip? cycle-summary)
                                       (conj {:cycle (:cycle cycle-summary)
                                              :situation-id (:selected-situation-id cycle-summary)
                                              :before (:mural-appraisal-before cycle-summary)
                                              :after (:mural-appraisal-after cycle-summary)}))))]
         (recur (inc step)
                next-world
                (conj cycle-summaries cycle-summary)))))))

(comment
  (run-miniworld {:cycles 12}))
