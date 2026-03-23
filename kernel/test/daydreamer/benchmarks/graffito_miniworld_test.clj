(ns daydreamer.benchmarks.graffito-miniworld-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.graffito-miniworld :as mini]
            [daydreamer.context :as cx]))

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
                      :fact/id :same_light_reads_as_threat})))))

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
      (is (every? #(or (= :rehearsal (:selected-family %))
                       (:family-plan-episode-id %))
                  cycle-summaries)))
    (testing "the trace remains inspectable"
      (is (= 12 (count summaries)))
      (is (re-find #"graffito_night_mural"
                   (apply str summaries)))
      (is (map? log))
      (is (= 12 (count (get log "cycles"))))
      (is (= "graffito-miniworld" (get log "seed"))))))

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
      (is (integer? (:episodes-with-promotion-history run-summary)))
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
      (is (pos? (:cross-family-source-win-cycles run-summary))))))

(deftest twenty-cycle-miniworld-assigns-distinct-cross-family-use-ids
  (let [{:keys [world]} (mini/run-miniworld {:cycles 20})
        cross-family-promoted
        (->> (vals (:episodes world))
             (filter #(seq (:promotion-history %)))
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
