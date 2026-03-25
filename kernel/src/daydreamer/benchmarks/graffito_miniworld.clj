(ns daydreamer.benchmarks.graffito-miniworld
  "First autonomous Graffito miniworld.

  This moves beyond the scripted slices without jumping to creative-output
  evaluation. The world is still small and typed: street overload, apartment
  support, mural crisis. Tony carries a transient regulation state across
  cycles. The benchmark reruns appraisal every cycle, lets the kernel execute
  live families where they exist, and now routes rehearsal provenance,
  source-use, and episode storage through the kernel while keeping the
  Graffito-specific control delta benchmark-local."
  (:require [clojure.set :as set]
            [daydreamer.context :as cx]
            [daydreamer.episodic-memory :as episodic]
            [daydreamer.goal-families :as families]
            [daydreamer.goal-family-rules :as gf-rules]
            [daydreamer.goals :as goals]
            [daydreamer.rules :as rules]
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
(def ^:private mural-bridge-frame-id :rf_counterfactual_aftershock_holds_the_mark)
(def ^:private rehearsal-routine-id :rt_counted_stroke_rehearsal)
(def ^:private rehearsal-operator-id :op_counted_stroke_with_sketchbook)
(def ^:private rehearsal-source-rule-id :graffito-miniworld/rehearsal-routine)
(def ^:private rehearsal-repair-target :precision_under_pulse)
(def ^:private frontier-bridge-rule-id :goal-family/reversal-aftershock-to-rationalization)

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
   :episode-use-counter
   :episode-event-counter
   :id-counter
   :recent-episodes
   :recent-indices
   :rule-access
   :provenance-episode-index
   :support-episode-index
   :roving-episodes])

(defn- carryover-fact
  [fact context-id]
  (cond-> (assoc fact :context-id context-id)
    (:branch-context-id fact)
    (dissoc :branch-context-id)))

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

(def ^:private rehearsal-retrieval-fact-ids
  [:monk_counts_a_holdable_beat
   :repeated_stroke_returns_precision])

(def ^:private mural-reversal-breakdown-surface-ids
  [:light_jolt_floods_attention
   :siren_pulse_hits_body
   :noise_fragments_precision])

(def ^:private reversal-breakdown-overlap-threshold 2)

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

(def ^:private mural-bridge-reframe-facts
  [(typed-fact :sensorimotor-input :light_jolt_floods_attention)
   (typed-fact :sensorimotor-input :siren_pulse_hits_body)
   (typed-fact :sensorimotor-input :noise_fragments_precision)
   (typed-fact :person-object-relation :can_waits_for_a_steady_hand)
   (typed-fact :cross-layer-correspondence :can_corresponds_to_sensory_capacity)
   (typed-fact :appraisal :counterfactual_aftershock_reopens_the_mark)
   (typed-fact :appraisal :pressure_can_be_line_tension)])

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

(defn- retract-facts-by-type
  [world context-id fact-type]
  (reduce (fn [current-world fact]
            (cx/retract-fact current-world context-id fact))
          world
          (filter #(= fact-type (:fact/type %))
                  (cx/visible-facts world context-id))))

(defn- retract-facts-by-types
  [world context-id fact-types]
  (reduce (fn [current-world fact]
            (cx/retract-fact current-world context-id fact))
          world
          (filter #(contains? fact-types (:fact/type %))
                  (cx/visible-facts world context-id))))

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

(declare street-context-id)
(declare apartment-context-id)
(declare rehearsal-routine-ready?)

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

(def ^:private street-projection-fact-types
  #{:situation :emotion :dependency :goal :intends :failure-cause})

(def ^:private apartment-projection-fact-types
  #{:situation :emotion :dependency :goal :rationalization-frame})

(def ^:private mural-projection-fact-types
  #{:emotion :dependency :goal :intends :failure-cause :rationalization-frame :appraisal})

(defn- tony-delta
  [tony-state metric]
  (- (double (get tony-state metric 0.0))
     (double (get default-tony-state metric 0.0))))

(defn- situation-delta
  [situations situation-id metric]
  (- (double (get-in situations [situation-id metric] 0.0))
     (double (get-in initial-situations-state [situation-id metric] 0.0))))

(def ^:private baseline-control-deficit
  (- 1.0 (:perceived-control default-tony-state)))

(def ^:private baseline-agency-deficit
  (- 1.0 (:felt-agency default-tony-state)))

(defn- control-deficit-delta
  [tony-state]
  (- (- 1.0 (double (:perceived-control tony-state)))
     baseline-control-deficit))

(defn- agency-deficit-delta
  [tony-state]
  (- (- 1.0 (double (:felt-agency tony-state)))
     baseline-agency-deficit))

(defn- street-emotion-strength
  [world]
  (let [tony-state (current-tony-state world)
        situations (current-situations-state world)]
    (clamp01
     (+ 0.82
        (* 0.24 (tony-delta tony-state :sensory-load))
        (* 0.20 (control-deficit-delta tony-state))
        (* 0.14 (situation-delta situations street-situation-id :activation))
        (* 0.08 (situation-delta situations street-situation-id :ripeness))
        (* -0.12 (tony-delta tony-state :entrainment))
        (* -0.10 (tony-delta tony-state :felt-agency))))))

(defn- apartment-emotion-strength
  [world]
  (let [tony-state (current-tony-state world)
        situations (current-situations-state world)]
    (clamp01
     (+ 0.79
        (* 0.12 (tony-delta tony-state :sensory-load))
        (* 0.22 (agency-deficit-delta tony-state))
        (* 0.18 (control-deficit-delta tony-state))
        (* 0.12 (situation-delta situations apartment-situation-id :activation))
        (* 0.08 (situation-delta situations apartment-situation-id :ripeness))
        (* -0.10 (tony-delta tony-state :entrainment))))))

(defn- mural-threat-strength
  [world]
  (let [tony-state (current-tony-state world)
        situations (current-situations-state world)]
    (clamp01
     (+ 0.87
        (* 0.20 (tony-delta tony-state :sensory-load))
        (* 0.18 (control-deficit-delta tony-state))
        (* 0.14 (situation-delta situations mural-situation-id :activation))
        (* 0.08 (situation-delta situations mural-situation-id :ripeness))
        (* -0.14 (tony-delta tony-state :felt-agency))
        (* -0.12 (tony-delta tony-state :entrainment))))))

(defn- mural-challenge-strength
  [world]
  (let [tony-state (current-tony-state world)
        situations (current-situations-state world)]
    (clamp01
     (+ 0.74
        (* -0.16 (tony-delta tony-state :sensory-load))
        (* 0.18 (tony-delta tony-state :perceived-control))
        (* 0.18 (tony-delta tony-state :felt-agency))
        (* 0.16 (tony-delta tony-state :entrainment))
        (* 0.12 (situation-delta situations mural-situation-id :activation))
        (* 0.08 (situation-delta situations mural-situation-id :ripeness))))))

(defn- emotion-projection-summary
  [situation-id emotion-id affect strength]
  {:situation-id situation-id
   :emotion-id emotion-id
   :affect affect
   :strength strength
   :provenance :benchmark-projected
   :projection-kind :derived})

(defn- project-street-reversal
  [world]
  (let [context-id (street-context-id world)
        emotion-strength (street-emotion-strength world)
        projected-facts [{:fact/type :situation
                          :fact/id street-situation-id}
                         {:fact/type :emotion
                          :emotion-id street-emotion-id
                          :strength emotion-strength
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
                                             street-counterfactual-facts)]
        world (retract-facts-by-types world context-id street-projection-fact-types)
        world (assert-facts world context-id projected-facts)]
    (assoc-in world
              [:graffito-miniworld :emotion-projections :street]
              (emotion-projection-summary street-situation-id
                                          street-emotion-id
                                          :fear
                                          emotion-strength))))

(defn- project-apartment-support
  [world]
  (let [context-id (apartment-context-id world)
        emotion-strength (apartment-emotion-strength world)
        projected-facts [{:fact/type :situation
                          :fact/id apartment-situation-id}
                         {:fact/type :emotion
                          :emotion-id apartment-emotion-id
                          :strength emotion-strength
                          :valence :negative
                          :affect :shame}
                         {:fact/type :dependency
                          :from-id apartment-emotion-id
                          :to-id apartment-goal-id}
                         (goal-fact apartment-goal-id apartment-goal-id :failed context-id)
                         (rationalization-frame-fact apartment-frame-id
                                                     apartment-goal-id
                                                     0.91
                                                     apartment-reframe-facts)]
        world (retract-facts-by-types world context-id apartment-projection-fact-types)
        world (assert-facts world context-id projected-facts)]
    (assoc-in world
              [:graffito-miniworld :emotion-projections :apartment]
              (emotion-projection-summary apartment-situation-id
                                          apartment-emotion-id
                                          :shame
                                          emotion-strength))))

(defn project-mural-appraisal
  "Project Tony's carried state into the mural situation as derived appraisal."
  [world]
  (let [mural-context-id (get-in world [:graffito-miniworld :contexts :mural])
        tony-state (current-tony-state world)
        regulation-mode (derive-regulation-mode tony-state)
        appraisal-mode (derive-appraisal-mode tony-state)
        challenge-strength (mural-challenge-strength world)
        threat-strength (mural-threat-strength world)
        projected-facts
        (if (= :challenge-dominant appraisal-mode)
          [{:fact/type :emotion
            :emotion-id mural-challenge-emotion-id
            :strength challenge-strength
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
            :strength threat-strength
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
           (rationalization-frame-fact mural-bridge-frame-id
                                       mural-threat-goal-id
                                       0.88
                                       mural-bridge-reframe-facts)
           (typed-fact :appraisal :same_light_reads_as_threat)
           (typed-fact :appraisal :capture_feels_inevitable)])
        world (retract-facts-by-types world mural-context-id mural-projection-fact-types)
        world (assert-facts world mural-context-id projected-facts)]
    (-> world
        (assoc-in [:graffito-miniworld :mural-projection]
                  {:appraisal-mode appraisal-mode
                   :regulation-mode regulation-mode
                   :tony-state tony-state
                   :object-state (current-object-state world)})
        (assoc-in [:graffito-miniworld :emotion-projections :mural]
                  (emotion-projection-summary mural-situation-id
                                              (if (= :challenge-dominant appraisal-mode)
                                                mural-challenge-emotion-id
                                                mural-threat-emotion-id)
                                              (if (= :challenge-dominant appraisal-mode)
                                                :shame
                                                :fear)
                                              (if (= :challenge-dominant appraisal-mode)
                                                challenge-strength
                                                threat-strength))))))

(defn- rehearsal-motivation-strength
  [world]
  (let [tony-state (current-tony-state world)
        situations (current-situations-state world)
        regulation-mode (derive-regulation-mode tony-state)
        agency (:felt-agency tony-state)
        entrainment (:entrainment tony-state)
        control (:perceived-control tony-state)
        control-deficit (- 1.0 control)
        agency-deficit (- 1.0 agency)
        apartment (get situations apartment-situation-id)]
    (clamp01
     (+ (* 0.28 control-deficit)
        (* 0.18 agency-deficit)
        (* 0.14 entrainment)
        (* 0.10 (:ripeness apartment))
        (if (rehearsal-routine-ready? world) 0.18 0.0)
        (if (>= agency 0.4) 0.18 0.0)
        (if (contains? #{:bracing :entraining} regulation-mode) 0.14 0.0)
        (if (>= control 0.72) -0.18 0.0)
        (if (= regulation-mode :creating) -0.22 0.0)))))

(defn- project-rehearsal-affordance
  [world]
  (let [context-id (apartment-context-id world)
        ready? (rehearsal-routine-ready? world)
        motivation-strength (rehearsal-motivation-strength world)
        selection-reasons (cond-> [:support_need
                                   :rhythm_routine
                                   :control_repair
                                   :rehearsal_affordance]
                            (>= motivation-strength gf-rules/rehearsal-motivation-threshold)
                            (conj :motivation_above_threshold))
        affordance-fact {:fact/type :rehearsal-affordance
                         :fact/id rehearsal-routine-id
                         :goal-id apartment-goal-id
                         :routine-id rehearsal-routine-id
                         :operator-id rehearsal-operator-id
                         :motivation-strength motivation-strength
                         :selection-policy gf-rules/rehearsal-affordance-policy
                         :selection-reasons selection-reasons
                         :situation-id apartment-situation-id
                         :repair-target rehearsal-repair-target}
        world (retract-facts-by-type world context-id :rehearsal-affordance)
        world (cond-> world
                ready?
                (assert-facts context-id [affordance-fact]))]
    (assoc-in world
              [:graffito-miniworld :rehearsal-affordance]
              {:ready? ready?
               :motivation-strength motivation-strength
               :selection-policy gf-rules/rehearsal-affordance-policy
               :selection-reasons selection-reasons
               :repair-target rehearsal-repair-target})))

(defn- project-graffito-state
  [world]
  (-> world
      project-street-reversal
      project-apartment-support
      project-mural-appraisal
      project-rehearsal-affordance))

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
                                    [street-leaf-objective]))
        world (assert-facts world
                            apartment-context-id
                            apartment-facts)
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
                      :pending-context-facts {}
                      :emotion-projections {}
                      :flip-history []
                      :mural-raw-input-ids (->> mural-raw-input-facts
                                                (keep :fact/id)
                                                vec)
                      :rehearsal-routine {:routine-id rehearsal-routine-id
                                          :operator-id rehearsal-operator-id
                                          :precondition-ids (sort rehearsal-precondition-ids)}})]
    (project-graffito-state world)))

(defn- rebuild-world-with-state
  [world]
  (let [fresh-world (build-world)
        carried-root (select-keys world persistent-root-keys)
        decayed-tony-state (decay-tony-state (current-tony-state world))
        object-state (current-object-state world)
        prior-gm (:graffito-miniworld world)
        fresh-gm (:graffito-miniworld fresh-world)
        pending-context-facts (:pending-context-facts prior-gm)
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
        world (assoc-in world [:graffito-miniworld :situations-state] decayed-situations)
        world (reduce-kv
               (fn [acc logical-context facts]
                 (if-let [context-id (get-in acc [:graffito-miniworld :contexts logical-context])]
                   (assert-facts acc
                                 context-id
                                 (map #(carryover-fact % context-id) facts))
                   acc))
               world
               pending-context-facts)
        world (assoc-in world [:graffito-miniworld :pending-context-facts] {})]
    (project-graffito-state world)))

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

(defn- summarize-preplan-source-candidates
  [candidates]
  {:source-candidate-count (count candidates)
   :dynamic-source-candidate-count (count (filter #(= :dynamic
                                                      (candidate-origin-keyword
                                                       (:candidate-origin %)))
                                                  candidates))
   :authored-source-candidate-count (count (filter #(= :authored
                                                       (candidate-origin-keyword
                                                        (:candidate-origin %)))
                                                   candidates))
   :top-source-candidates
   (mapv (fn [candidate]
           {:origin (candidate-origin-keyword (:candidate-origin candidate))
            :candidate-rank (:candidate-rank candidate)
            :goal-id (:goal-id candidate)
            :frame-id (:frame-id candidate)
            :episode-id (:episode-id candidate)
            :priority (:priority candidate)
            :selection-policy (:selection-policy candidate)
            :selection-reasons (:selection-reasons candidate)})
         (take 4 candidates))})

(defn- mural-rationalization-preplan-race
  [world goal-id]
  (let [goal (get-in world [:goals goal-id])
        trigger-context-id (:trigger-context-id goal)
        failed-goal-id (:trigger-failed-goal-id goal)
        candidates (families/rationalization-frame-candidates
                    world
                    {:trigger-context-id trigger-context-id
                     :failed-goal-id failed-goal-id})]
    (assoc (summarize-preplan-source-candidates candidates)
           :trigger-context-id trigger-context-id
           :failed-goal-id failed-goal-id)))

(defn- maybe-withdraw-authored-mural-frame
  [world chosen-candidate goal-id]
  (if-not (= :mural-rationalization (:operator-key chosen-candidate))
    [world {:authored-source-withdrawn? false}]
    (let [preplan-race (mural-rationalization-preplan-race world goal-id)
          dynamic-visible? (pos? (:dynamic-source-candidate-count preplan-race))]
      [(cond-> world
         dynamic-visible?
         (cx/retract-fact (mural-context-id world)
                          (rationalization-frame-fact mural-challenge-frame-id
                                                      mural-challenge-goal-id
                                                      0.9
                                                      mural-challenge-reframe-facts)))
       (assoc preplan-race
              :dynamic-source-visible? dynamic-visible?
              :authored-source-withdrawn? dynamic-visible?)])))

(defn- mural-aftershock-rationalization-trigger
  [world]
  (let [active-cross-family-rules (ns-resolve 'daydreamer.goal-families
                                              'active-cross-family-rules)
        instantiate-cross-family-trigger (ns-resolve 'daydreamer.goal-families
                                                     'instantiate-cross-family-trigger)
        facts (cx/visible-facts world (mural-context-id world))
        rule (some #(when (= frontier-bridge-rule-id (:id %)) %)
                   (when active-cross-family-rules
                     (active-cross-family-rules world)))]
    (when (and rule instantiate-cross-family-trigger)
      (some->> (rules/match-rule rule facts)
               (keep (fn [{:keys [bindings matched-facts]}]
                       (let [producer-fact (first matched-facts)
                             producer-provenance (:rule-provenance producer-fact)]
                         (when producer-provenance
                           (instantiate-cross-family-trigger
                            producer-provenance
                            rule
                            bindings)))))
               first))))

(defn- annotate-mural-reversal-episode-for-rehearsal
  [world chosen-candidate family-plan]
  (if-let [episode-id (when (= :mural-reversal (:operator-key chosen-candidate))
                        (get-in family-plan [:selection :family_plan_episode_id]))]
    (reduce (fn [acc fact-id]
              (episodic/store-episode acc episode-id fact-id {:zone :content}))
            (-> world
                (assoc-in [:episodes episode-id :payload :repair-target]
                          rehearsal-repair-target)
                (assoc-in [:episodes episode-id :payload :breakdown-surface]
                          (vec mural-reversal-breakdown-surface-ids))
                (assoc-in [:episodes episode-id :payload :bridge-family-pair]
                          [:reversal :rehearsal]))
            mural-reversal-breakdown-surface-ids)
    world))

(defn- cross-family-rehearsal-source-candidates
  [world]
  (let [context-id (mural-context-id world)
        visible-indices (->> (cx/visible-facts world context-id)
                             (keep :fact/id)
                             distinct
                             vec)
        visible-id-set (set visible-indices)]
    (if-not (seq visible-indices)
      []
      (->> (episodic/retrieve-episodes
            world
            visible-indices
            {:threshold-key :plan-threshold
             :active-rule-path [rehearsal-source-rule-id]
             :active-family :rehearsal})
           (keep (fn [hit]
                   (let [episode (get-in world [:episodes (:episode-id hit)])
                         provenance (:provenance episode)
                         source-family (:family provenance)
                         common-fields
                         {:candidate-origin :dynamic
                          :episode-id (:episode-id hit)
                          :source-family source-family
                          :rule-path (vec (:rule-path episode))
                          :priority (double (or (:effective-marks hit)
                                                (:marks hit)
                                                0.0))
                          :admission-status (:admission-status episode)
                          :cross-family-use-count (count (filter #(not= (:source-family %)
                                                                        (:target-family %))
                                                                 (:use-history episode)))
                          :use-history-count (count (:use-history episode))
                          :promotion-evidence-count (count (:promotion-evidence episode))
                          :anti-residue-flags (vec (:anti-residue-flags episode))}
                         breakdown-surface (set (get-in episode [:payload :breakdown-surface]))
                         breakdown-overlap (set/intersection visible-id-set
                                                             breakdown-surface)]
                     (when (= :family-plan (:source provenance))
                       (case source-family
                         :rationalization
                         (merge common-fields
                                {:frontier-bridge? (boolean
                                                    (some #{:goal-family/reversal-aftershock-to-rationalization}
                                                          (:rule-path episode)))
                                 :frame-id (or (get-in episode [:payload :frame-id])
                                               (get-in provenance
                                                       [:selection :rationalization_frame_id]))
                                 :goal-id (or (get-in episode [:payload :frame-goal-id])
                                              (get-in provenance
                                                      [:selection :rationalization_frame_goal]))
                                 :selection-policy :stored_cross_family_support_episode
                                 :selection-reasons (cond-> [:stored_cross_family_support_episode
                                                             :anticipated_mural_projection]
                                                      (:provenance-reason hit)
                                                      (conj (:provenance-reason hit)))})

                         :reversal
                         (when (and (= rehearsal-repair-target
                                       (get-in episode [:payload :repair-target]))
                                    (>= (count breakdown-overlap)
                                        reversal-breakdown-overlap-threshold))
                           (merge common-fields
                                  {:frontier-bridge? false
                                   :goal-id (or (get-in provenance
                                                        [:selection :reversal_counterfactual_goal])
                                                (get-in episode [:goal-id]))
                                   :source-id (or (get-in provenance
                                                          [:selection :reversal_counterfactual_source])
                                                  (get-in episode [:payload :source-id]))
                                   :repair-target rehearsal-repair-target
                                   :breakdown-surface-overlap (->> breakdown-overlap
                                                                   (sort-by str)
                                                                   vec)
                                   :breakdown-surface (vec breakdown-surface)
                                   :breakdown-surface-overlap-count
                                   (count breakdown-overlap)
                                   :selection-policy :stored_cross_family_breakdown_episode
                                   :selection-reasons (cond-> [:stored_cross_family_breakdown_episode
                                                               :matched_repair_target
                                                               :matched_breakdown_surface]
                                                        (:provenance-reason hit)
                                                        (conj (:provenance-reason hit)))}))

                         nil)))))
           (sort-by (juxt (comp not :frontier-bridge?)
                          (fn [candidate]
                            (case (:source-family candidate)
                              :reversal 0
                              :rationalization 1
                              2))
                          (comp - #(or % 0) :breakdown-surface-overlap-count)
                          (comp - :priority)
                          (comp str :episode-id)))
           vec))))

(defn- summarize-cross-family-rehearsal-candidates
  [candidates]
  {:cross-family-source-candidate-count (count candidates)
   :cross-family-rationalization-candidate-count
   (count (filter #(= :rationalization (:source-family %)) candidates))
   :cross-family-reversal-candidate-count
   (count (filter #(= :reversal (:source-family %)) candidates))
   :cross-family-source-candidates
   (mapv (fn [candidate]
           {:origin (candidate-origin-keyword (:candidate-origin candidate))
            :episode-id (:episode-id candidate)
            :source-family (:source-family candidate)
            :rule-path (:rule-path candidate)
            :frontier-bridge? (:frontier-bridge? candidate)
            :frame-id (:frame-id candidate)
            :goal-id (:goal-id candidate)
            :source-id (:source-id candidate)
            :priority (:priority candidate)
            :repair-target (:repair-target candidate)
            :breakdown-surface-overlap (:breakdown-surface-overlap candidate)
            :breakdown-surface-overlap-count (:breakdown-surface-overlap-count candidate)
            :selection-policy (:selection-policy candidate)
            :selection-reasons (:selection-reasons candidate)
            :admission-status (:admission-status candidate)
            :cross-family-use-count (:cross-family-use-count candidate)
            :use-history-count (:use-history-count candidate)
            :promotion-evidence-count (:promotion-evidence-count candidate)
            :anti-residue-flags (:anti-residue-flags candidate)})
         (take 4 candidates))})

(defn- select-cross-family-rehearsal-source
  [candidates]
  (let [frontier-candidate (first (filter :frontier-bridge? candidates))
        frontier-needs-open? (and frontier-candidate
                                  (not= :durable (:admission-status frontier-candidate)))
        reversal-candidate
        (->> candidates
             (filter #(= :reversal (:source-family %)))
             (sort-by (juxt (comp - #(or % 0) :breakdown-surface-overlap-count)
                            (comp - :promotion-evidence-count)
                            (comp - :priority)
                            (comp str :episode-id)))
             first)
        seeded-alt-candidate
        (->> candidates
             (filter #(= :rationalization (:source-family %)))
             (remove :frontier-bridge?)
             (sort-by (juxt (comp - :use-history-count)
                            (comp - :priority)
                            (comp str :episode-id)))
             first)]
    (cond
      frontier-needs-open?
      frontier-candidate

      reversal-candidate
      reversal-candidate

      seeded-alt-candidate
      seeded-alt-candidate

      :else
      (first candidates))))

(defn- rehearsal-cross-family-probe
  [world]
  (let [candidates (cross-family-rehearsal-source-candidates world)
        selected (select-cross-family-rehearsal-source candidates)]
    [(assoc (summarize-cross-family-rehearsal-candidates candidates)
            :cross-family-source-context-id (mural-context-id world)
            :cross-family-source-visible? (boolean selected)
            :cross-family-source-episode-id (:episode-id selected)
            :cross-family-source-family (:source-family selected)
            :cross-family-source-selection-policy (:selection-policy selected))
     selected]))

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
        rehearsal-triggers (families/rehearsal-activation-candidates world)
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
        apartment-rehearsal-trigger
        (some-> (->> rehearsal-triggers
                     (filter #(= (apartment-context-id world)
                                 (or (:trigger-context-id %)
                                     (:context-id %))))
                     first)
                (assoc :goal-type :rehearsal))
        regular-mural-rationalization-trigger
        (some-> (->> rationalization-triggers
                     (filter #(= (mural-context-id world)
                                 (trigger-context-id %)))
                     first)
                (assoc :goal-type :rationalization))
        mural-bridge-rationalization-trigger
        (mural-aftershock-rationalization-trigger world)
        mural-rationalization-trigger
        (or mural-bridge-rationalization-trigger
            regular-mural-rationalization-trigger)
        mural-bridge-trigger? (boolean mural-bridge-rationalization-trigger)
        tony-state (current-tony-state world)
        situations (current-situations-state world)
        regulation-mode (derive-regulation-mode tony-state)
        appraisal-mode (get-in world [:graffito-miniworld :mural-projection :appraisal-mode])
        load (:sensory-load tony-state)
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

          apartment-rehearsal-trigger
          (conj
           (candidate
            {:kind :routine
             :situation-id apartment-situation-id
             :family :rehearsal
             :operator-key :apartment-rehearsal
             :operator-id (:operator-id apartment-rehearsal-trigger)
             :trigger apartment-rehearsal-trigger
             :strength (+ (* 0.30 (:activation apartment))
                          (* 0.12 (:ripeness apartment))
                          (* 0.58 (double (or (:motivation-strength apartment-rehearsal-trigger)
                                              0.0))))
             :reasons (vec (:selection-reasons apartment-rehearsal-trigger))}))

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

          (and mural-rationalization-trigger
               (or (= :challenge-dominant appraisal-mode)
                   mural-bridge-trigger?))
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
                          (if (= :challenge-dominant appraisal-mode) 0.22 0.0)
                          (if mural-bridge-trigger? 0.24 0.0))
             :reasons (cond-> [:mural_reread]
                        (= :challenge-dominant appraisal-mode)
                        (into [:held_mark :challenge_appraisal])

                        mural-bridge-trigger?
                        (into [:reversal_aftershock_bridge
                               :counterfactual_reopened
                               :rationalization_frame]))})))]
    [world
     (->> candidates
          (mapv #(apply-fatigue-adjustment world %))
          (sort-by (juxt (comp - :strength)
                         (comp str :situation-id)
                         (comp str :family))))]))

(defn- choose-candidate
  [candidates]
  (first candidates))

(defn- mural-aftershock-carryover-facts
  [world]
  (->> (cx/visible-facts world (mural-context-id world))
       (filter #(and (= :family-affect-state (:fact/type %))
                     (= :reversal (:source-family %))
                     (= :counterfactual_reopened (:transition %))
                     (= mural-threat-goal-id (:failed-goal-id %))))
       vec))

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

(defn- post-effect-reappraisal-hook
  [chosen-candidate]
  (fn [world _family-plan]
    (-> world
        (update-tony-and-object-state (:operator-key chosen-candidate))
        (advance-situations (:operator-key chosen-candidate))
        (update-recent-choices chosen-candidate)
        project-graffito-state)))

(defn- trace-candidate-summary
  [candidate]
  (cond-> {:id (or (:goal-id candidate)
                   (:operator-id candidate)
                   (:operator-key candidate))
           :goal-type (:family candidate)
           :kind (:kind candidate)
           :situation-id (:situation-id candidate)
           :family (:family candidate)
           :operator-key (:operator-key candidate)
           :operator-id (:operator-id candidate)
           :goal-id (:goal-id candidate)
           :strength (:strength candidate)
           :reasons (:reasons candidate)}
    (:trigger candidate)
    (assoc :trigger-selection-policy (get-in candidate [:trigger :selection-policy])
           :trigger-emotion-id (get-in candidate [:trigger :emotion-id])
           :trigger-emotion-strength (get-in candidate [:trigger :emotion-strength])
           :trigger-motivation-strength (get-in candidate [:trigger :motivation-strength])
           :trigger-context-id (or (get-in candidate [:trigger :trigger-context-id])
                                   (get-in candidate [:trigger :old-context-id])
                                   (get-in candidate [:trigger :context-id]))
           :frontier-bridge-trigger? (= :reversal_aftershock_rationalization_frame
                                        (get-in candidate
                                                [:trigger :selection-policy])))))

(def ^:private logical-context->situation-id
  {:street street-situation-id
   :apartment apartment-situation-id
   :mural mural-situation-id})

(defn- situation-id-for-context
  [world context-id]
  (some (fn [[logical-context known-context-id]]
          (when (= known-context-id context-id)
            (get logical-context->situation-id logical-context)))
        (get-in world [:graffito-miniworld :contexts])))

(defn- context-id-for-situation
  [world situation-id]
  (some (fn [[logical-context known-situation-id]]
          (when (= known-situation-id situation-id)
            (get-in world [:graffito-miniworld :contexts logical-context])))
        logical-context->situation-id))

(defn- context-active-indices
  [world context-id]
  (->> (cx/visible-facts world context-id)
       (keep :fact/id)
       distinct
       vec))

(defn- emotional-state-for-context
  [world context-id]
  (let [situation-id (situation-id-for-context world context-id)
        projection-info (case situation-id
                          :graffito_street_overload
                          (get-in world [:graffito-miniworld :emotion-projections :street])

                          :graffito_grandma_apartment
                          (get-in world [:graffito-miniworld :emotion-projections :apartment])

                          :graffito_night_mural
                          (get-in world [:graffito-miniworld :emotion-projections :mural])

                          nil)]
    (->> (cx/visible-facts world context-id)
         (filter #(= :emotion (:fact/type %)))
         (map (fn [fact]
                {:emotion-id (:emotion-id fact)
                 :strength (:strength fact)
                 :valence (:valence fact)
                 :affect (:affect fact)
                 :provenance (:provenance projection-info)
                 :projection-kind (:projection-kind projection-info)
                 :situation-id situation-id
                 :context-id context-id}))
         (sort-by (juxt (comp str :situation-id)
                        (comp - double :strength)
                        (comp str :emotion-id)))
         vec)))

(defn- current-emotional-state
  [world]
  (->> (vals (get-in world [:graffito-miniworld :contexts]))
       (mapcat #(emotional-state-for-context world %))
       vec))

(defn- trace-retrieval-summary
  [candidate]
  (let [origin (or (candidate-origin-keyword (:candidate-origin candidate))
                   (candidate-origin-keyword (:origin candidate)))
        overlap (cond
                  (seq (:overlap candidate))
                  (vec (:overlap candidate))

                  (seq (:breakdown-surface-overlap candidate))
                  (vec (:breakdown-surface-overlap candidate))

                  :else
                  [])]
    (cond-> {:node-id (or (:frame-id candidate)
                          (:source-id candidate)
                          (:goal-id candidate)
                          (:episode-id candidate)
                          (:operator-id candidate))
             :episode-id (:episode-id candidate)
             :retrieval-score (double (or (:retrieval-score candidate)
                                          (:priority candidate)
                                          (:marks candidate)
                                          0.0))
             :overlap overlap
             :origin origin
             :selection-policy (:selection-policy candidate)
             :selection-reasons (:selection-reasons candidate)
             :source-family (:source-family candidate)
             :frame-id (:frame-id candidate)
             :goal-id (:goal-id candidate)
             :source-id (:source-id candidate)
             :frontier-bridge? (:frontier-bridge? candidate)
             :repair-target (:repair-target candidate)
             :admission-status (:admission-status candidate)
             :rule-path (:rule-path candidate)}
      (:candidate-rank candidate)
      (assoc :candidate-rank (:candidate-rank candidate))

      (:rank candidate)
      (assoc :candidate-rank (:rank candidate))

      (:breakdown-surface-overlap-count candidate)
      (assoc :breakdown-surface-overlap-count
             (:breakdown-surface-overlap-count candidate)))))

(defn- trace-episodic-retrieval-summary
  [world candidate]
  (when-let [episode-id (:episode-id candidate)]
    (let [episode (get-in world [:episodes episode-id])]
      {:episode-id episode-id
       :retrieval-score (double (or (:retrieval-score candidate)
                                    (:priority candidate)
                                    (:marks candidate)
                                    0.0))
       :overlap (vec (or (:overlap candidate)
                         (:breakdown-surface-overlap candidate)
                         []))
       :threshold nil
       :episode-created-cycle (:cycle-created episode)
       :same-cycle? (= (:cycle world) (:cycle-created episode))
       :admission-status (:admission-status episode)
       :provenance-reason (:selection-policy candidate)})))

(defn- trace-cycle-base
  [world chosen-candidate top-candidates]
  (let [selected-context-id (or (some-> chosen-candidate :trigger :trigger-context-id)
                                (some-> chosen-candidate :trigger :old-context-id)
                                (some-> chosen-candidate :trigger :context-id)
                                (context-id-for-situation world
                                                          (:situation-id chosen-candidate)))]
    (cond-> {:top-candidates (mapv trace-candidate-summary
                                   (take 4 top-candidates))
             :active-indices (context-active-indices world selected-context-id)
             :emotional-state (current-emotional-state world)
             :situations (current-situations-state world)
             :graffito_miniworld
             {:selected-candidate (trace-candidate-summary chosen-candidate)
              :top-candidates (mapv trace-candidate-summary
                                    (take 4 top-candidates))
              :emotion-projections-before (get-in world [:graffito-miniworld :emotion-projections])
              :mural-projection-before (get-in world [:graffito-miniworld :mural-projection])}}
      (:goal-id chosen-candidate)
      (assoc :selected-goal (trace-selected-goal world (:goal-id chosen-candidate))))))

(defn- activate-family-goal-from-trigger
  [world {:keys [goal-type situation-id emotion-id emotion-strength
                 motivation-strength affordance-id
                 routine-id operator-id
                 selection-policy selection-reasons activation-policy
                 activation-reasons context-id trigger-context-id old-context-id failed-goal-id
                 frame-id rule-provenance]}]
  (let [activation-context-id (or (:reality-lookahead world)
                                  (:reality-context world))
        trigger-context-id (or trigger-context-id old-context-id context-id)
        goal-strength (double (or (when (= :rehearsal goal-type)
                                    motivation-strength)
                                  emotion-strength
                                  0.0))
        [world goal-id]
        (goals/activate-top-level-goal
         world
         activation-context-id
         {:goal-type goal-type
          :planning-type :imaginary
          :strength goal-strength
          :main-motiv emotion-id
          :situation-id situation-id
          :trigger-context-id trigger-context-id
          :trigger-failed-goal-id failed-goal-id
          :trigger-emotion-id emotion-id
          :trigger-emotion-strength emotion-strength
          :trigger-motivation-strength motivation-strength
          :trigger-affordance-id affordance-id
          :trigger-routine-id routine-id
          :trigger-operator-id operator-id
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
                  :motivation-strength motivation-strength
                  :affordance-id affordance-id
                  :routine-id routine-id
                  :operator-id operator-id
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
        [world preplan-race] (maybe-withdraw-authored-mural-frame world
                                                                  chosen-candidate
                                                                  goal-id)
        [world family-plan] (families/run-family-plan world {:goal-id (:goal-id chosen-candidate)})
        world (annotate-mural-reversal-episode-for-rehearsal world
                                                             chosen-candidate
                                                             family-plan)
        [world family-plan] (families/apply-post-effect-hook
                             world
                             family-plan
                             (post-effect-reappraisal-hook chosen-candidate))
        appraisal-after (get-in world [:graffito-miniworld :mural-projection :appraisal-mode])
        regulation-after (get-in world [:graffito-miniworld :mural-projection :regulation-mode])
        family-plan-episode-id (get-in family-plan [:selection :family_plan_episode_id])
        world (assoc-in world
                        [:graffito-miniworld :pending-context-facts]
                        (if (= :reversal (:family chosen-candidate))
                          {:mural (mural-aftershock-carryover-facts world)}
                          {}))
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
                 :trigger-selection-policy (:selection-policy (:trigger chosen-candidate))
                 :frontier-bridge-trigger? (= :reversal_aftershock_rationalization_frame
                                              (:selection-policy (:trigger chosen-candidate)))
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
                 :preplan-race preplan-race
                 :frame-id (get-in family-plan [:selection :rationalization_frame_id])
                 :source-id (get-in family-plan [:selection :reversal_counterfactual_source])
                 :rule-provenance (:rule-provenance family-plan)
                 :stored-episode-count (count (:episodes world))}]
    [(trace/merge-latest-cycle
      world
      {:selection (:selection family-plan)
       :active-indices (context-active-indices
                        world
                        (or (:trigger-context-id preplan-race)
                            (context-id-for-situation world
                                                      (:situation-id chosen-candidate))))
       :retrievals (mapv trace-retrieval-summary
                         (or (:top-source-candidates preplan-race)
                             source-candidates
                             []))
       :episodic-retrievals (->> (or (:top-source-candidates preplan-race)
                                     source-candidates
                                     [])
                                 (keep #(trace-episodic-retrieval-summary world %))
                                 vec)
       :sprouted (:sprouted-context-ids family-plan)
       :mutations (:mutation-events world)
       :rule-provenance (:rule-provenance family-plan)
       :family-plan-episode-id family-plan-episode-id
       :episode-lifecycle (get-in family-plan [:result :episode-lifecycle])
       :graffito_miniworld
       {:tony-state-before tony-state-before
        :tony-state-after (:tony-state-after summary)
        :emotion-projections-before (get-in world [:graffito-miniworld :emotion-projections])
        :mural-appraisal-before appraisal-before
        :mural-appraisal-after appraisal-after
        :reappraisal-flip? (:reappraisal-flip? summary)
        :trigger-selection-policy (:trigger-selection-policy summary)
        :frontier-bridge-trigger? (:frontier-bridge-trigger? summary)
        :preplan-race preplan-race}})
     summary]))

(defn- execute-rehearsal-candidate
  [world chosen-candidate top-candidates]
  (let [[world goal-id] (activate-family-goal-from-trigger world (:trigger chosen-candidate))
        chosen-candidate (assoc chosen-candidate :goal-id goal-id)
        tony-state-before (current-tony-state world)
        object-state-before (current-object-state world)
        appraisal-before (get-in world [:graffito-miniworld :mural-projection :appraisal-mode])
        regulation-before (get-in world [:graffito-miniworld :mural-projection :regulation-mode])
        [cross-family-race selected-source] (rehearsal-cross-family-probe world)
        world (append-cycle world (trace-cycle-base world chosen-candidate top-candidates))
        [world _] (families/apply-post-effect-hook
                   world
                   nil
                   (post-effect-reappraisal-hook chosen-candidate))
        [world family-plan]
        (families/run-family-plan
         world
         {:goal-id goal-id
          :context-id (apartment-context-id world)
          :trigger-context-id (apartment-context-id world)
          :precondition-ids (sort rehearsal-precondition-ids)
          :selection-reasons (:reasons chosen-candidate)
          :routine-fact-ids (sort rehearsal-retrieval-fact-ids)
          :support-indices (sort rehearsal-precondition-ids)
          :source-episode-id (:episode-id selected-source)
          :source-use-role :rehearsal-support-source
          :source-use-outcome (when selected-source
                                {:outcome :succeeded
                                 :reason :rehearsal-regulation-success})
          :episode-payload {:situation-id apartment-situation-id
                            :routine-id rehearsal-routine-id
                            :operator-id rehearsal-operator-id
                            :repair-target rehearsal-repair-target
                            :routine-retrieval-fact-ids (vec (sort rehearsal-retrieval-fact-ids))
                            :routine-support-fact-ids (vec (sort rehearsal-precondition-ids))}})
        _ (when-not family-plan
            (throw (ex-info "Graffito miniworld expected rehearsal family plan to run"
                            {:cycle (:cycle world)
                             :candidate chosen-candidate
                             :cross-family-race cross-family-race})))
        family-plan-result (:result family-plan)
        appraisal-after (get-in world [:graffito-miniworld :mural-projection :appraisal-mode])
        regulation-after (get-in world [:graffito-miniworld :mural-projection :regulation-mode])
        summary {:cycle (:cycle world)
                 :selected-situation-id (:situation-id chosen-candidate)
                 :selected-family :rehearsal
                 :operator-key (:operator-key chosen-candidate)
                 :goal-id goal-id
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
                 :family-plan-episode-id (:family-episode-id family-plan)
                 :admission-status (:admission-status family-plan)
                 :winner-origin (when selected-source :dynamic)
                 :source-candidate-count 0
                 :dynamic-source-candidate-count 0
                 :authored-source-candidate-count 0
                 :dynamic-source-visible? false
                 :dynamic-source-won? false
                 :cross-family-source-candidate-count (:cross-family-source-candidate-count
                                                       cross-family-race)
                 :cross-family-rationalization-candidate-visible?
                 (pos? (:cross-family-rationalization-candidate-count cross-family-race))
                 :cross-family-reversal-candidate-visible?
                 (pos? (:cross-family-reversal-candidate-count cross-family-race))
                 :cross-family-source-visible? (:cross-family-source-visible?
                                                cross-family-race)
                 :cross-family-source-family (:cross-family-source-family
                                              cross-family-race)
                 :cross-family-source-episode-id (:cross-family-source-episode-id
                                                  cross-family-race)
                 :cross-family-source-won? (boolean (seq (:episode-use-records family-plan-result)))
                 :cross-family-use-outcome (get-in family-plan-result
                                                   [:episode-source-outcome :outcome])
                 :cross-family-admission-transition (:episode-source-admission-transition
                                                     family-plan-result)
                 :cross-family-access-transitions (:episode-source-rule-access-transitions
                                                   family-plan-result)
                 :stored-episode-count (count (:episodes world))}]
    [(trace/merge-latest-cycle
      world
      {:selection (:selection family-plan)
       :active-indices (context-active-indices
                        world
                        (:cross-family-source-context-id cross-family-race))
       :retrievals (mapv trace-retrieval-summary
                         (:cross-family-source-candidates cross-family-race))
       :episodic-retrievals (->> (:cross-family-source-candidates cross-family-race)
                                 (keep #(trace-episodic-retrieval-summary world %))
                                 vec)
       :rule-provenance (:rule-provenance family-plan)
       :family-plan-episode-id (:family-episode-id family-plan)
       :episode-lifecycle (get-in family-plan [:result :episode-lifecycle])
       :episode-use-records (vec (:episode-use-records family-plan-result))
       :admission-transition (:episode-source-admission-transition family-plan-result)
       :rule-access-transitions (:episode-source-rule-access-transitions family-plan-result)
       :graffito_miniworld
       {:operator-id rehearsal-operator-id
        :routine-id rehearsal-routine-id
        :tony-state-before tony-state-before
        :tony-state-after (:tony-state-after summary)
        :emotion-projections-before (get-in world [:graffito-miniworld :emotion-projections])
        :mural-appraisal-before appraisal-before
        :mural-appraisal-after appraisal-after
        :reappraisal-flip? (:reappraisal-flip? summary)
        :cross-family-race cross-family-race
        :cross-family-source-won? (:cross-family-source-won? summary)
        :cross-family-use-outcome (:cross-family-use-outcome summary)}})
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

(def ^:private attractor-tail-window 12)

(defn- cycle-snapshot-by-number
  [world cycle-number]
  (some #(when (= cycle-number (:cycle-num %))
           %)
        (:trace world)))

(defn- rehabilitation-flag-event?
  [event]
  (and (= :cleared (:action event))
       (contains? #{:cross-family-loop-rehabilitation
                    :cross-family-rehabilitation}
                  (:reason event))))

(defn- attractor-digest
  [world cycle-summaries]
  (let [cycle-count (count cycle-summaries)
        tail-window (min attractor-tail-window cycle-count)
        tail-cycles (vec (take-last tail-window cycle-summaries))
        tail-cycle-range [(-> tail-cycles first :cycle)
                          (-> tail-cycles last :cycle)]
        cross-family-source-first-seen
        (reduce (fn [acc cycle-summary]
                  (let [episode-id (:cross-family-source-episode-id cycle-summary)]
                    (if (or (nil? episode-id)
                            (contains? acc episode-id))
                      acc
                      (assoc acc episode-id (:cycle cycle-summary)))))
                {}
                cycle-summaries)
        structural-change-cycles
        (->> cycle-summaries
             (keep (fn [cycle-summary]
                     (let [cycle (:cycle cycle-summary)
                           snapshot (cycle-snapshot-by-number world cycle)
                           world-delta (:world-delta snapshot)
                           episode-id (:cross-family-source-episode-id cycle-summary)
                           new-cross-family-source? (= cycle
                                                       (get cross-family-source-first-seen
                                                            episode-id))
                           promotion-events (get-in world-delta [:promotion :events])
                           demotion-events (get-in world-delta [:demotion :events])
                           rule-access-events (get-in world-delta [:rule_access :changed])
                           rehabilitation-events (->> (get-in world-delta [:flags :events])
                                                      (filter rehabilitation-flag-event?)
                                                      seq)]
                       (when (or new-cross-family-source?
                                 (seq promotion-events)
                                 (seq demotion-events)
                                 (seq rule-access-events)
                                 rehabilitation-events)
                         cycle))))
             vec)
        tail-cross-family-source-counts
        (->> tail-cycles
             (keep :cross-family-source-episode-id)
             frequencies
             (into (sorted-map-by (fn [left right]
                                    (compare (str left) (str right))))))
        dominant-tail-source
        (first (sort-by (juxt (comp - val)
                              (comp str key))
                        tail-cross-family-source-counts))
        tail-world-deltas
        (->> tail-cycles
             (keep #(cycle-snapshot-by-number world (:cycle %)))
             (map :world-delta)
             vec)]
    {:tail-window tail-window
     :tail-cycle-range tail-cycle-range
     :last-structural-change-cycle (last structural-change-cycles)
     :tail-family-counts (frequencies (map :selected-family tail-cycles))
     :tail-situation-counts (frequencies (map :selected-situation-id tail-cycles))
     :tail-regulation-mode-counts (frequencies (map :regulation-mode-after tail-cycles))
     :tail-appraisal-mode-counts (frequencies (map :mural-appraisal-after tail-cycles))
     :tail-reappraisal-flip-count (count (filter :reappraisal-flip? tail-cycles))
     :tail-challenge-mural-cycles (count (filter #(and (= :graffito_night_mural
                                                          (:selected-situation-id %))
                                                       (= :rationalization
                                                          (:selected-family %)))
                                                 tail-cycles))
     :tail-cross-family-source-episode-counts tail-cross-family-source-counts
     :tail-distinct-cross-family-source-episode-count (count tail-cross-family-source-counts)
     :dominant-tail-cross-family-source-episode-id (key dominant-tail-source)
     :dominant-tail-cross-family-source-cycle-count (val dominant-tail-source)
     :tail-promotion-event-count (reduce + 0
                                         (map #(count (get-in % [:promotion :events]))
                                              tail-world-deltas))
     :tail-demotion-event-count (reduce + 0
                                        (map #(count (get-in % [:demotion :events]))
                                             tail-world-deltas))
     :tail-rule-access-transition-count (reduce + 0
                                                (map #(count (get-in % [:rule_access :changed]))
                                                     tail-world-deltas))
     :tail-rehabilitation-event-count (reduce + 0
                                              (map #(count (filter rehabilitation-flag-event?
                                                                   (get-in % [:flags :events])))
                                                   tail-world-deltas))
     :tail-vindicated-use-count (count (for [delta tail-world-deltas
                                             resolved (get-in delta [:episode_use :resolved])
                                             :when (= :later-cross-family-vindication
                                                      (:reason resolved))]
                                         resolved))}))

(defn- attractor-digest-lines
  [digest]
  [(format "Attractor digest: tail cycles %s-%s, last structural change cycle %s"
           (first (:tail-cycle-range digest))
           (second (:tail-cycle-range digest))
           (or (:last-structural-change-cycle digest) "n/a"))
   (format "Tail family counts: %s | regulation: %s | appraisal: %s"
           (:tail-family-counts digest)
           (:tail-regulation-mode-counts digest)
           (:tail-appraisal-mode-counts digest))
   (format "Tail dominant cross-family source: %s (%s/%s cycles) | distinct sources: %s"
           (or (:dominant-tail-cross-family-source-episode-id digest) "none")
           (or (:dominant-tail-cross-family-source-cycle-count digest) 0)
           (:tail-window digest)
           (:tail-distinct-cross-family-source-episode-count digest))
   (format "Tail movement: flips=%s challenge-mural=%s promotions=%s demotions=%s rehabilitations=%s vindications=%s rule-access=%s"
           (:tail-reappraisal-flip-count digest)
           (:tail-challenge-mural-cycles digest)
           (:tail-promotion-event-count digest)
           (:tail-demotion-event-count digest)
           (:tail-rehabilitation-event-count digest)
           (:tail-vindicated-use-count digest)
           (:tail-rule-access-transition-count digest))])

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
        cross-family-source-candidate-cycles
        (count (filter :cross-family-source-visible?
                       cycle-summaries))
        cross-family-source-win-cycles
        (count (filter :cross-family-source-won?
                       cycle-summaries))
        cross-family-rationalization-candidate-cycles
        (count (filter :cross-family-rationalization-candidate-visible?
                       cycle-summaries))
        cross-family-reversal-candidate-cycles
        (count (filter :cross-family-reversal-candidate-visible?
                       cycle-summaries))
        cross-family-rationalization-win-cycles
        (count (filter #(= :rationalization
                           (:cross-family-source-family %))
                       cycle-summaries))
        cross-family-reversal-win-cycles
        (count (filter #(= :reversal
                           (:cross-family-source-family %))
                       cycle-summaries))
        distinct-cross-family-source-episode-count
        (count (->> cycle-summaries
                    (keep :cross-family-source-episode-id)
                    distinct))
        dynamic-rationalization-candidate-cycles
        (count (filter #(and (= :rationalization (:selected-family %))
                             (:dynamic-source-visible? %))
                       cycle-summaries))
        dynamic-reversal-candidate-cycles
        (count (filter #(and (= :reversal (:selected-family %))
                             (:dynamic-source-visible? %))
                       cycle-summaries))
        rule-access-transition-count
        (reduce + 0
                (map #(count (:cross-family-access-transitions %))
                     cycle-summaries))
        frontier-bridge-cycles
        (count (filter :frontier-bridge-trigger?
                       cycle-summaries))
        vindicated-use-count
        (count (for [episode episodes
                     use-record (:use-history episode)
                     :when (= :later-cross-family-vindication
                              (:outcome-reason use-record))]
                 use-record))
        episodes-with-rehabilitation-history
        (count (filter (fn [episode]
                         (some (fn [record]
                                 (and (= :cleared (:action record))
                                      (contains? #{:cross-family-loop-rehabilitation
                                                   :cross-family-rehabilitation}
                                                 (:reason record))))
                               (:anti-residue-history episode)))
                       episodes))
        episodes-with-demotion-history
        (count (filter #(seq (:demotion-history %))
                       episodes))]
    {:cycle-count (count cycle-summaries)
     :family-counts family-counts
     :situation-counts situation-counts
     :reappraisal-flips reappraisal-flips
     :challenge-mural-cycles challenge-mural-cycles
     :stored-episode-count (count episodes)
     :episodes-with-use-history (count (filter #(seq (:use-history %)) episodes))
     :episodes-with-cross-family-use-history
     (count (filter (fn [episode]
                      (seq (filter #(not= (:source-family %)
                                          (:target-family %))
                                   (:use-history episode))))
                    episodes))
     :vindicated-use-count vindicated-use-count
     :episodes-with-promotion-history (count (filter #(seq (:promotion-history %))
                                                     episodes))
     :episodes-with-demotion-history episodes-with-demotion-history
     :episodes-with-flags (count (filter #(seq (:anti-residue-flags %)) episodes))
     :episodes-with-rehabilitation-history episodes-with-rehabilitation-history
     :durable-episode-count (count (filter #(= :durable (:admission-status %))
                                           episodes))
     :provisional-episode-count (count (filter #(= :provisional (:admission-status %))
                                               episodes))
     :dynamic-source-candidate-cycles dynamic-source-candidate-cycles
     :dynamic-source-win-cycles dynamic-source-win-cycles
     :cross-family-source-candidate-cycles cross-family-source-candidate-cycles
     :cross-family-source-win-cycles cross-family-source-win-cycles
     :cross-family-rationalization-candidate-cycles
     cross-family-rationalization-candidate-cycles
     :cross-family-reversal-candidate-cycles
     cross-family-reversal-candidate-cycles
     :cross-family-rationalization-win-cycles
     cross-family-rationalization-win-cycles
     :cross-family-reversal-win-cycles
     cross-family-reversal-win-cycles
     :distinct-cross-family-source-episode-count
     distinct-cross-family-source-episode-count
     :dynamic-rationalization-candidate-cycles dynamic-rationalization-candidate-cycles
     :dynamic-reversal-candidate-cycles dynamic-reversal-candidate-cycles
     :rule-access-transition-count rule-access-transition-count
     :frontier-bridge-cycles frontier-bridge-cycles}))

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
       (let [digest (attractor-digest world cycle-summaries)]
         {:world world
          :cycle-summaries cycle-summaries
          :run-summary (assoc (run-summary world cycle-summaries)
                              :attractor-digest digest)
          :attractor-digest digest
          :summaries (vec (concat (mapv summary-line cycle-summaries)
                                  [""]
                                  (attractor-digest-lines digest)))
          :log (trace/reporter-log world
                                   (benchmark-metadata {:attractor_digest digest}))})
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
