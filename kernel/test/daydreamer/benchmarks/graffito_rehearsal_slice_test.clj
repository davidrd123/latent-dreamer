(ns daydreamer.benchmarks.graffito-rehearsal-slice-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.graffito-rehearsal-slice :as slice]
            [daydreamer.context :as cx]))

(deftest build-world-seeds-transient-tony-state-and-object-phase
  (let [world (slice/build-world)
        mural-context-id (get-in world [:graffito-rehearsal-slice :contexts :mural])
        apartment-context-id (get-in world [:graffito-rehearsal-slice :contexts :apartment])
        mural-facts (set (cx/visible-facts world mural-context-id))
        apartment-facts (set (cx/visible-facts world apartment-context-id))
        tony-state (slice/current-tony-state world)
        object-state (slice/current-object-state world)]
    (testing "Tony state is transient carried state, not a learned capability map"
      (is (= {:sensory-load 0.86
              :entrainment 0.11
              :felt-agency 0.22
              :perceived-control 0.24}
             tony-state))
      (is (= #{:sensory-load :entrainment :felt-agency :perceived-control}
             (set (keys tony-state))))
      (is (= :overloaded
             (slice/derive-regulation-mode tony-state))))
    (testing "phaseful objects live in a dedicated map"
      (is (= {:can {:phase :dormant}
              :mural {:phase :under_siege}}
             object-state)))
    (testing "the mural keeps raw inputs while appraisal remains projected"
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
    (testing "the apartment carries the authored rehearsal preconditions"
      (is (contains? apartment-facts
                     {:fact/type :sensorimotor-input
                      :fact/id :monk_counts_a_holdable_beat}))
      (is (contains? apartment-facts
                     {:fact/type :sensorimotor-input
                      :fact/id :sketchbook_offers_small_surface}))
      (is (contains? apartment-facts
                     {:fact/type :person-object-relation
                      :fact/id :monk_co_regulates_tony_with_rhythm}))
      (is (true? (slice/rehearsal-routine-ready? world))))))

(deftest mural-choice-flips-after-apartment-rehearsal
  (let [[_ baseline] (slice/mural-choice-read (slice/build-world) :mural-before-rehearsal)
        [rehearsal-world rehearsal] (slice/run-apartment-rehearsal-cycle
                                     (slice/build-world))
        carried-world (slice/carry-state-into-fresh-world rehearsal-world)
        [_ reappraised] (slice/mural-choice-read carried-world
                                                 :mural-after-rehearsal)]
    (testing "before rehearsal the mural reads as threat and selects reversal"
      (is (= :threat-dominant (:appraisal-mode baseline)))
      (is (= :reversal (:selected-family baseline))))
    (testing "the authored rehearsal routine changes Tony state and object phase"
      (is (= :authored-routine (:winner-origin rehearsal)))
      (is (= :rt_counted_stroke_rehearsal (:routine-id rehearsal)))
      (is (= :op_counted_stroke_with_sketchbook (:operator-id rehearsal)))
      (is (= {:sensory-load 0.77
              :entrainment 0.73
              :felt-agency 0.67
              :perceived-control 0.7}
             (:tony-state-after rehearsal)))
      (is (= {:can {:phase :attuning}
              :mural {:phase :reachable}}
             (:object-state-after rehearsal)))
      (is (= :entraining (:regulation-mode-after rehearsal))))
    (testing "the same mural inputs now reappraise and flip the selected family"
      (is (= (:raw-input-ids baseline)
             (:raw-input-ids reappraised)))
      (is (= :challenge-dominant (:appraisal-mode reappraised)))
      (is (= :rationalization (:selected-family reappraised)))
      (is (= :entraining (:regulation-mode reappraised))))))

(deftest run-slice-keeps-rehearsal-mechanistic-and-accumulation-out-of-tony-state
  (let [{:keys [rehearsal-world world cycle-summaries summaries]} (slice/run-slice)
        baseline (nth cycle-summaries 0)
        rehearsal (nth cycle-summaries 1)
        reappraised (nth cycle-summaries 2)
        mural-run (nth cycle-summaries 3)
        mural-episode-id (:family-plan-episode-id mural-run)
        mural-episode (get-in world [:episodes mural-episode-id])]
    (testing "rehearsal stays a local routine and does not smuggle learned traits into Tony state"
      (is (= :rehearsal (:family rehearsal)))
      (is (nil? (:family-plan-episode-id rehearsal)))
      (is (= #{:sensory-load :entrainment :felt-agency :perceived-control}
             (set (keys (slice/current-tony-state rehearsal-world))))))
    (testing "the reread flips the later mural family honestly"
      (is (= :reversal (:selected-family baseline)))
      (is (= :rationalization (:selected-family reappraised)))
      (is (= :rf_rhythm_can_hold_the_mark (:frame-id mural-run)))
      (is (= :provisional (:admission-status mural-episode))))
    (testing "the slice returns readable summaries for the before/after contrast"
      (is (= 4 (count summaries)))
      (is (re-find #"mural before rehearsal"
                   (first summaries)))
      (is (re-find #"apartment rehearsal"
                   (second summaries)))
      (is (re-find #"mural after rehearsal"
                   (nth summaries 2)))
      (is (re-find #"mural rationalization after rehearsal"
                   (nth summaries 3))))))
