(ns daydreamer.benchmarks.graffito-regulation-slice-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.graffito-regulation-slice :as slice]
            [daydreamer.context :as cx]))

(deftest build-world-seeds-character-state-and-raw-mural-inputs
  (let [world (slice/build-world)
        mural-context-id (get-in world [:graffito-regulation-slice :contexts :mural])
        apartment-context-id (get-in world [:graffito-regulation-slice :contexts :apartment])
        mural-facts (set (cx/visible-facts world mural-context-id))
        apartment-facts (set (cx/visible-facts world apartment-context-id))
        tony-state (slice/current-tony-state world)]
    (testing "Tony's carried state lives on the world map, not as the primitive scene state"
      (is (= {:sensory-load 0.84
              :entrainment 0.12
              :felt-agency 0.18
              :perceived-control 0.21}
             tony-state))
      (is (= :overloaded
             (slice/derive-regulation-mode tony-state))))
    (testing "the mural keeps raw inputs while projected appraisal stays derived"
      (is (contains? mural-facts
                     {:fact/type :sensorimotor-input
                      :fact/id :light_jolt_floods_attention}))
      (is (contains? mural-facts
                     {:fact/type :sensorimotor-input
                      :fact/id :siren_pulse_hits_body}))
      (is (contains? mural-facts
                     {:fact/type :appraisal
                      :fact/id :same_light_reads_as_threat}))
      (is (not (contains? mural-facts
                          {:fact/type :sensorimotor-regulation
                           :fact/id :regulation_mode_overloaded}))))
    (testing "the apartment still carries the typed support structure"
      (is (contains? apartment-facts
                     {:fact/type :person-object-relation
                      :fact/id :tony_trusts_can_lineage}))
      (is (contains? apartment-facts
                     {:fact/type :cross-layer-correspondence
                      :fact/id :grandma_counterpart_of_motherload}))
      (is (some #(and (= :rationalization-frame (:fact/type %))
                      (= :rf_intensity_becomes_style (:fact/id %)))
                apartment-facts)))))

(deftest mural-choice-flips-after-apartment-support
  (let [[_ baseline] (slice/mural-choice-read (slice/build-world) :mural-before-support)
        [support-world support] (slice/run-apartment-support-cycle (slice/build-world))
        carried-world (-> support-world
                          slice/carry-tony-state-into-fresh-world)
        [_ reappraised] (slice/mural-choice-read carried-world :mural-after-support)]
    (testing "before support the mural reads as threat and selects reversal"
      (is (= :threat-dominant (:appraisal-mode baseline)))
      (is (= :reversal (:selected-family baseline)))
      (is (= #{:light_jolt_floods_attention
               :siren_pulse_hits_body
               :noise_fragments_precision
               :cop_light_closing
               :street_is_exposing
               :sirens_near
               :wall_is_portal_surface
               :can_is_inheritance
               :cops_interrupted_mural
               :tony_startled_by_light
               :can_waits_for_a_steady_hand
               :sketchbook_cannot_help_at_full_scale
               :can_corresponds_to_sensory_capacity
               :mural_corresponds_to_threshold_crossing}
             (set (:raw-input-ids baseline)))))
    (testing "apartment support raises Tony's carried state"
      (is (= :authored (:winner-origin support)))
      (is (= :rf_intensity_becomes_style (:frame-id support)))
      (is (= {:sensory-load 0.78
              :entrainment 0.68
              :felt-agency 0.74
              :perceived-control 0.66}
             (:tony-state-after support)))
      (is (= :entraining (:regulation-mode-after support))))
    (testing "the same mural inputs now reappraise and flip the selected family"
      (is (= (:raw-input-ids baseline)
             (:raw-input-ids reappraised)))
      (is (= :challenge-dominant (:appraisal-mode reappraised)))
      (is (= :rationalization (:selected-family reappraised)))
      (is (= :entraining (:regulation-mode reappraised))))))

(deftest run-slice-keeps-the-reappraisal-step-mechanistic
  (let [{:keys [support-world world cycle-summaries summaries]} (slice/run-slice)
        baseline (nth cycle-summaries 0)
        support (nth cycle-summaries 1)
        reappraised (nth cycle-summaries 2)
        mural-run (nth cycle-summaries 3)
        apartment-episode-id (:family-plan-episode-id support)
        apartment-episode (get-in support-world [:episodes apartment-episode-id])
        mural-episode-id (:family-plan-episode-id mural-run)
        mural-episode (get-in world [:episodes mural-episode-id])]
    (testing "the apartment support cycle stores an honest rationalization episode"
      (is (= :provisional (:admission-status apartment-episode)))
      (is (some #{:goal-family/rationalization-plan-dispatch}
                (:rule-path apartment-episode)))
      (is (every? (set (:content-indices apartment-episode))
                  [:tony_trusts_can_lineage
                   :sketchbook_compresses_the_scene
                   :monk_moves_tony_into_rhythm])))
    (testing "the reappraised mural cycle runs the new family choice honestly"
      (is (= :rationalization (:selected-family reappraised)))
      (is (= :rf_same_light_can_be_held (:frame-id mural-run)))
      (is (= :provisional (:admission-status mural-episode)))
      (is (some #{:goal-family/rationalization-plan-dispatch}
                (:rule-path mural-episode))))
    (testing "the slice returns readable summaries for the before/after contrast"
      (is (= 4 (count summaries)))
      (is (re-find #"mural before support"
                   (first summaries)))
      (is (re-find #"apartment support rationalization"
                   (second summaries)))
      (is (re-find #"mural after support"
                   (nth summaries 2)))
      (is (re-find #"mural rationalization after support"
                   (nth summaries 3)))
      (is (= :reversal (:selected-family baseline))))))
