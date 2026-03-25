(ns daydreamer.benchmarks.graffito-miniworld-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.graffito-miniworld :as mini]
            [daydreamer.context :as cx]
            [daydreamer.goal-families :as families]
            [daydreamer.rules :as rules]))

(deftest build-world-seeds-three-situations-and-initial-threat-read
  (let [world (mini/build-world)
        street-context-id (get-in world [:graffito-miniworld :contexts :street])
        apartment-context-id (get-in world [:graffito-miniworld :contexts :apartment])
        mural-context-id (get-in world [:graffito-miniworld :contexts :mural])
        street-facts (set (cx/visible-facts world street-context-id))
        apartment-facts (set (cx/visible-facts world apartment-context-id))
        mural-facts (set (cx/visible-facts world mural-context-id))]
    (testing "the miniworld carries Tony state and phaseful object state"
      (is (= {:sensory-load 0.84
              :entrainment 0.12
              :felt-agency 0.2
              :perceived-control 0.23}
             (mini/current-tony-state world)))
      (is (= {:can {:phase :dormant}
              :mural {:phase :under_siege}}
             (mini/current-object-state world))))
    (testing "all three situations are seeded with typed facts"
      (is (contains? street-facts
                     {:fact/type :sensorimotor-input
                      :fact/id :school_bell_floods_attention}))
      (is (contains? apartment-facts
                     {:fact/type :sensorimotor-input
                      :fact/id :monk_counts_a_holdable_beat}))
      (is (contains? mural-facts
                     {:fact/type :sensorimotor-input
                      :fact/id :light_jolt_floods_attention})))
    (testing "the initial mural reread is threat-dominant"
      (is (= :threat-dominant
             (get-in world [:graffito-miniworld :mural-projection :appraisal-mode])))
      (is (contains? mural-facts
                     {:fact/type :appraisal
                      :fact/id :same_light_reads_as_threat})))
    (testing "the initial scene emotions are benchmark-projected from the live carried state rather than left implicit"
      (is (contains? street-facts
                     {:fact/type :emotion
                      :emotion-id :e_tony_mockery_panic
                      :strength 0.82
                      :valence :negative
                      :affect :fear}))
      (is (contains? apartment-facts
                     {:fact/type :emotion
                      :emotion-id :e_tony_wrongness_shame
                      :strength 0.79
                      :valence :negative
                      :affect :shame}))
      (is (contains? mural-facts
                     {:fact/type :emotion
                      :emotion-id :e_mural_capture_panic
                      :strength 0.87
                      :valence :negative
                      :affect :fear}))
      (is (= {:street {:situation-id :graffito_street_overload
                       :emotion-id :e_tony_mockery_panic
                       :affect :fear
                       :strength 0.82
                       :provenance :benchmark-projected
                       :projection-kind :derived}
              :apartment {:situation-id :graffito_grandma_apartment
                          :emotion-id :e_tony_wrongness_shame
                          :affect :shame
                          :strength 0.79
                          :provenance :benchmark-projected
                          :projection-kind :derived}
              :mural {:situation-id :graffito_night_mural
                      :emotion-id :e_mural_capture_panic
                      :affect :fear
                      :strength 0.87
                      :provenance :benchmark-projected
                      :projection-kind :derived}}
             (get-in world [:graffito-miniworld :emotion-projections]))))))

(deftest autonomous-miniworld-stays-diverse-and-produces-live-rereads
  (let [{:keys [world cycle-summaries summaries log]} (mini/run-miniworld {:cycles 12})
        families (set (map :selected-family cycle-summaries))
        situations (set (map :selected-situation-id cycle-summaries))
        challenge-mural-cycles (filter #(and (= :graffito_night_mural
                                                (:selected-situation-id %))
                                             (= :rationalization
                                                (:selected-family %)))
                                       cycle-summaries)
        flips (filter :reappraisal-flip? cycle-summaries)]
    (testing "the autonomous run visits all three situations and more than one family"
      (is (= #{:graffito_street_overload
               :graffito_grandma_apartment
               :graffito_night_mural}
             situations))
      (is (contains? families :reversal))
      (is (contains? families :rationalization))
      (is (contains? families :rehearsal)))
    (testing "the same mural inputs are reread differently in live autonomous play"
      (is (seq flips))
      (is (some #(and (= :graffito_grandma_apartment (:selected-situation-id %))
                      (= :rehearsal (:selected-family %))
                      (= :threat-dominant (:mural-appraisal-before %))
                      (= :challenge-dominant (:mural-appraisal-after %)))
                cycle-summaries))
      (is (seq challenge-mural-cycles)))
    (testing "family execution continues to store honest episodes while Tony state stays transient"
      (is (pos? (count (:episodes world))))
      (is (= #{:sensory-load :entrainment :felt-agency :perceived-control}
             (set (keys (mini/current-tony-state world)))))
      (let [stored-cycles (filter :family-plan-episode-id cycle-summaries)
            activation-only-cycles (remove :family-plan-episode-id cycle-summaries)]
        (is (>= (count stored-cycles) 8))
        (is (every? #(and (= :reversal (:selected-family %))
                          (= :graffito_street_overload
                             (:selected-situation-id %)))
                    activation-only-cycles))))
    (testing "the trace remains inspectable"
      (is (= 12 (count summaries)))
      (is (re-find #"graffito_night_mural"
                   (apply str summaries)))
      (is (map? log))
      (is (= 12 (count (get log "cycles"))))
      (is (= "graffito-miniworld" (get log "seed"))))))

(deftest rehearsal-cycles-run-through-kernel-activation-goals
  (let [{:keys [cycle-summaries log]} (mini/run-miniworld {:cycles 12})
        rehearsal-cycle (first (filter #(= :rehearsal (:selected-family %))
                                       cycle-summaries))
        goal-id (:goal-id rehearsal-cycle)
        log-cycle (nth (get log "cycles") (dec (:cycle rehearsal-cycle)))
        selected-goal (get log-cycle "selected_goal")
        selection (get log-cycle "selection")
        rehearsal-candidate (first (filter #(= :rehearsal (:family %))
                                           (:top-candidates rehearsal-cycle)))
        trigger-motivation-strength (get-in rehearsal-candidate [:trigger :motivation-strength])]
    (testing "live rehearsal execution now uses an activated rehearsal goal instead of calling the plan directly by routine id"
      (is rehearsal-cycle)
      (is goal-id)
      (is (= "rehearsal" (get selected-goal "goal_type")))
      (is (= (name goal-id)
             (get selected-goal "id")))
      (is (pos? (double (or trigger-motivation-strength 0.0))))
      (is (< (Math/abs (- (double trigger-motivation-strength)
                          (double (get selected-goal "strength"))))
             1.0e-9))
      (is (= (name goal-id)
             (get selection "family_goal_id")))
      (is (= "rt_counted_stroke_rehearsal"
             (get selection "rehearsal_routine_id")))
      (is (= "op_counted_stroke_with_sketchbook"
             (get selection "rehearsal_operator_id"))))))

(deftest twenty-cycle-miniworld-summary-stays-in-a-healthy-band
  (let [{:keys [run-summary]} (mini/run-miniworld {:cycles 20})
        family-counts (:family-counts run-summary)
        situation-counts (:situation-counts run-summary)]
    (testing "the 20-cycle miniworld stays non-collapsed without pinning exact choreography"
      (is (pos? (get family-counts :reversal 0)))
      (is (pos? (get family-counts :rationalization 0)))
      (is (pos? (get family-counts :rehearsal 0)))
      (is (pos? (get situation-counts :graffito_street_overload 0)))
      (is (pos? (get situation-counts :graffito_grandma_apartment 0)))
      (is (pos? (get situation-counts :graffito_night_mural 0)))
      (is (>= (:reappraisal-flips run-summary) 4))
      (is (>= (:challenge-mural-cycles run-summary) 6))
      (is (>= (:stored-episode-count run-summary) 12)))
    (testing "accumulation observability is explicit once cross-family reuse starts moving the membrane"
      (is (integer? (:episodes-with-use-history run-summary)))
      (is (integer? (:episodes-with-cross-family-use-history run-summary)))
      (is (integer? (:vindicated-use-count run-summary)))
      (is (integer? (:episodes-with-promotion-history run-summary)))
      (is (integer? (:episodes-with-demotion-history run-summary)))
      (is (integer? (:episodes-with-rehabilitation-history run-summary)))
      (is (integer? (:frontier-bridge-cycles run-summary)))
      (is (integer? (:rule-access-transition-count run-summary)))
      (is (pos? (:episodes-with-use-history run-summary)))
      (is (pos? (:episodes-with-cross-family-use-history run-summary)))
      (is (pos? (:episodes-with-flags run-summary)))
      (is (pos? (:episodes-with-promotion-history run-summary)))
      (is (pos? (:durable-episode-count run-summary)))
      (is (integer? (:provisional-episode-count run-summary)))
      (is (pos? (:dynamic-source-candidate-cycles run-summary)))
      (is (pos? (:dynamic-source-win-cycles run-summary)))
      (is (pos? (:dynamic-rationalization-candidate-cycles run-summary)))
      (is (integer? (:dynamic-reversal-candidate-cycles run-summary)))
      (is (pos? (:cross-family-source-candidate-cycles run-summary)))
      (is (pos? (:cross-family-source-win-cycles run-summary)))
      (is (pos? (:cross-family-rationalization-candidate-cycles run-summary)))
      (is (pos? (:cross-family-reversal-candidate-cycles run-summary)))
      (is (pos? (:cross-family-rationalization-win-cycles run-summary)))
      (is (pos? (:cross-family-reversal-win-cycles run-summary)))
      (is (>= (:distinct-cross-family-source-episode-count run-summary) 2))
      (is (>= (:episodes-with-cross-family-use-history run-summary) 2))
      (is (>= (:episodes-with-promotion-history run-summary) 2))
      (is (>= (:durable-episode-count run-summary) 2))
      (is (pos? (:frontier-bridge-cycles run-summary)))
      (is (pos? (:rule-access-transition-count run-summary))))))

(deftest twenty-cycle-miniworld-assigns-distinct-cross-family-use-ids
  (let [{:keys [world]} (mini/run-miniworld {:cycles 20})
        cross-family-promoted
        (->> (vals (:episodes world))
             (filter #(seq (:promotion-history %)))
             (filter #(some #{:goal-family/reversal-aftershock-to-rationalization}
                            (:rule-path %)))
             (filter (fn [episode]
                       (some #(not= (:source-family %)
                                    (:target-family %))
                             (:use-history episode)))))
        promoted-episode (first cross-family-promoted)
        distinct-cross-use-ids (->> (:use-history promoted-episode)
                                    (filter #(not= (:source-family %)
                                                   (:target-family %)))
                                    (map :use-id)
                                    distinct)]
    (testing "cross-family Graffito promotion is driven by distinct later uses, not one recycled use-id"
      (is promoted-episode)
      (is (= :durable (:admission-status promoted-episode)))
      (is (>= (count distinct-cross-use-ids) 2))
      (is (>= (count (:promotion-evidence promoted-episode)) 2)))))

(deftest twenty-cycle-miniworld-opens-frontier-bridge-rule-entry
  (let [{:keys [world]} (mini/run-miniworld {:cycles 20})
        entry (rules/rule-access-info
               world
               (families/family-rules)
               :goal-family/reversal-aftershock-to-rationalization)]
    (testing "the promoted frontier-path Graffito episode opens the expected rule-access entry"
      (is (= :accessible (:status entry)))
      (is (= :authored-frontier (:source entry)))
      (is (= 9 (:opened-cycle entry)))
      (is (= :ep-24 (:opened-by entry)))
      (is (= [{:rule-id :goal-family/reversal-aftershock-to-rationalization
               :from-status :frontier
               :to-status :accessible
               :cycle 9
               :reason :durable-episode-opened-rule
               :episode-id :ep-24
               :branch-context-id :cx-3}]
             (:history entry))))))

(deftest reporter-log-includes-world-delta-and-benchmark-debug
  (let [{:keys [log]} (mini/run-miniworld {:cycles 20})
        cycles (get log "cycles")
        world-deltas (map #(get % "world_delta") cycles)]
    (testing "every exported cycle carries a world-delta projection"
      (is (= 20 (count cycles)))
      (is (every? map? world-deltas)))
    (testing "the canopy-facing canonical trace slots stay populated for Graffito runs"
      (is (every? #(seq (get % "top_candidates")) cycles))
      (is (every? #(seq (get % "emotional_state")) cycles))
      (is (every? #(seq (get % "active_indices")) cycles))
      (is (some #(seq (get % "retrieved")) cycles))
      (is (some #(seq (get % "retrieved_episodes")) cycles)))
    (testing "the exported log shows episode additions, promotion, and rule-access movement"
      (is (some #(seq (get-in % ["episodes" "added"])) world-deltas))
      (is (some #(seq (get-in % ["promotion" "events"])) world-deltas))
      (is (every? #(contains? % "demotion") world-deltas))
      (is (some #(seq (get-in % ["rule_access" "changed"])) world-deltas)))
    (testing "benchmark-local debug payloads survive projection for the canopy viewer"
      (is (some #(contains? (get % "debug") "graffito_miniworld") cycles))
      (is (some #(seq (get-in % ["debug" "rule-access-transitions"])) cycles))
      (is (some #(seq (get-in % ["debug" "episode-use-records"])) cycles))
      (is (some #(seq (get-in % ["debug" "episode-lifecycle" "episode-uses"])) cycles))
      (is (some #(map? (get-in % ["debug" "episode-lifecycle" "summary"])) cycles))
      (is (every? #(seq (get-in % ["debug" "graffito_miniworld" "emotion-projections-before"]))
                  cycles)))
    (testing "exported emotional state now labels benchmark-projected provenance explicitly"
      (is (every? (fn [cycle]
                    (every? #(= "benchmark-projected" (get % "provenance"))
                            (get cycle "emotional_state")))
                  cycles))
      (is (every? (fn [cycle]
                    (every? #(= "derived" (get % "projection_kind"))
                            (get cycle "emotional_state")))
                  cycles)))))

(deftest projected-scene-emotions-vary-over-a-live-run
  (let [{:keys [log]} (mini/run-miniworld {:cycles 12})
        cycles (get log "cycles")
        emotional-states (mapcat #(get % "emotional_state") cycles)
        strengths-for (fn [emotion-id]
                        (->> emotional-states
                             (filter #(= emotion-id (get % "emotion_id")))
                             (map #(get % "strength"))
                             distinct
                             vec))]
    (testing "street and apartment affect are no longer frozen scene constants"
      (is (> (count (strengths-for "e_tony_mockery_panic")) 1))
      (is (> (count (strengths-for "e_tony_wrongness_shame")) 1)))
    (testing "mural affect now varies in both kind and magnitude across rereads"
      (is (> (count (concat (strengths-for "e_mural_capture_panic")
                            (strengths-for "e_mural_mark_doubt")))
             1)))))

(deftest twenty-cycle-miniworld-produces-a-reversal-to-rehearsal-promoted-path
  (let [{:keys [world]} (mini/run-miniworld {:cycles 20})
        cross-family-promoted
        (->> (vals (:episodes world))
             (filter #(seq (:promotion-history %)))
             (filter (fn [episode]
                       (some #(not= (:source-family %)
                                    (:target-family %))
                             (:use-history episode)))))
        rationalization-promoted
        (filter #(= :rationalization (get-in % [:provenance :family]))
                cross-family-promoted)
        reversal-promoted
        (filter #(= :reversal (get-in % [:provenance :family]))
                cross-family-promoted)]
    (testing "the miniworld now carries both the frontier rationalization path and a matched reversal-to-rehearsal repair path"
      (is (>= (count cross-family-promoted) 2))
      (is (seq rationalization-promoted))
      (is (seq reversal-promoted))
      (is (some #(some #{:goal-family/reversal-aftershock-to-rationalization}
                       (:rule-path %))
                rationalization-promoted))
      (is (some #(= :precision_under_pulse
                    (get-in % [:payload :repair-target]))
                reversal-promoted))
      (is (some #(= [:reversal :rehearsal]
                    (get-in % [:payload :bridge-family-pair]))
                reversal-promoted))
      (is (some #(>= (count (filter (fn [use]
                                      (not= (:source-family use)
                                            (:target-family use)))
                                    (:use-history %)))
                     2)
                reversal-promoted))
      (is (every? #(= :durable (:admission-status %))
                  cross-family-promoted)))))

(deftest fifty-cycle-miniworld-stays-legible-in-parking-lot-laps
  (let [{:keys [run-summary]} (mini/run-miniworld {:cycles 50})
        family-counts (:family-counts run-summary)
        situation-counts (:situation-counts run-summary)]
    (testing "the longer autonomous run stays active without collapsing to one family or one source"
      (is (>= (:cycle-count run-summary) 50))
      (is (>= (get family-counts :rationalization 0) 20))
      (is (>= (get family-counts :reversal 0) 10))
      (is (>= (get family-counts :rehearsal 0) 10))
      (is (>= (get situation-counts :graffito_night_mural 0) 20))
      (is (>= (get situation-counts :graffito_grandma_apartment 0) 10))
      (is (>= (get situation-counts :graffito_street_overload 0) 10))
      (is (>= (:reappraisal-flips run-summary) 10))
      (is (>= (:challenge-mural-cycles run-summary) 20))
      (is (>= (:cross-family-source-win-cycles run-summary) 10))
      (is (>= (:distinct-cross-family-source-episode-count run-summary) 2))
      (is (pos? (:cross-family-reversal-win-cycles run-summary)))
      (is (>= (:episodes-with-promotion-history run-summary) 2))
      (is (>= (:durable-episode-count run-summary) 2))
      (is (pos? (:rule-access-transition-count run-summary))))))
