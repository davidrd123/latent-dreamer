(ns daydreamer.director-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.director :as director]))

(defn world-with-situations
  []
  {:situations-state
   {:s1_seeing_through {:activation 0.42
                        :hope 0.14
                        :anger 0.31
                        :threat 0.58
                        :waiting 0.09
                        :directed-target :the_set}
    :s4_the_ring {:activation 0.05
                  :hope 0.33
                  :anger 0.11
                  :threat 0.21
                  :waiting 0.19
                  :directed-target :the_crowd}}
   :recent-indices []
   :serendipity-bias 0.0})

(deftest normalize-feedback-coerces-director-schema
  (let [feedback (director/normalize-feedback
                  {"director_concepts" ["Honesty" "performance" "honesty"]
                   "situation_boosts" {"s4_the_ring" 0.12}
                   "valence_delta" 0.04
                   "surprise" 0.07
                   "emotional_episodes"
                   [{"affect" "anger"
                     "target" "the_set"
                     "source_situation" "s1_seeing_through"
                     "intensity" 0.2
                     "decay" 0.91
                     "indices" ["seam" "performance"]}]
                   "interpretation_note" "The stage turns honest."})]
    (is (= [:honesty :performance]
           (:director-concepts feedback)))
    (is (= {:s4_the_ring 0.12}
           (:situation-boosts feedback)))
    (is (= 0.04 (:valence-delta feedback)))
    (is (= 0.07 (:surprise feedback)))
    (is (= :anger (get-in feedback [:emotional-episodes 0 :affect])))
    (is (= [:seam :performance]
           (get-in feedback [:emotional-episodes 0 :indices])))
    (is (= "The stage turns honest."
           (:notes feedback)))))

(deftest apply-feedback-updates-situations-and-recent-indices
  (let [[world applied]
        (director/apply-feedback
         (world-with-situations)
         {:selected-situation-id :s1_seeing_through}
         {:director_concepts ["honesty" "performance"]
          :situation_boosts {"s4_the_ring" 0.10}
          :valence_delta 0.05
          :surprise 0.06
          :emotional_episodes [{:affect "anger"
                                :target "the_set"
                                :source_situation "s1_seeing_through"
                                :intensity 0.2
                                :decay 0.91
                                :indices ["apparatus" "anger"]}]
          :interpretation_note "The admission wakes the ring."})]
    (testing "feedback is normalized into the trace echo"
      (is (= [:honesty :performance]
             (:director_concepts applied)))
      (is (= {:s4_the_ring 0.1}
             (:situation_boosts applied)))
      (is (= 0.05 (:valence_delta applied)))
      (is (= 0.06 (:surprise applied)))
      (is (= "The admission wakes the ring."
             (:notes applied))))
    (testing "situation state shifts for the next cycle"
      (is (> (get-in world [:situations-state :s4_the_ring :activation])
             0.05))
      (is (> (get-in world [:situations-state :s1_seeing_through :hope])
             0.14))
      (is (> (get-in world [:situations-state :s1_seeing_through :anger])
             0.31))
      (is (< (get-in world [:situations-state :s1_seeing_through :threat])
             0.58)))
    (testing "feedback also influences retrieval pressure"
      (is (= #{:honesty :performance :apparatus :anger}
             (set (:recent-indices world))))
      (is (= [:honesty :performance]
             (:director-recent-concepts world)))
      (is (= 0.06
             (:serendipity-bias world))))))

(deftest apply-feedback-translates-dread-episodes-into-threat
  (let [[world _]
        (director/apply-feedback
         (world-with-situations)
         {:selected-situation-id :s1_seeing_through}
         {:director_concepts []
          :situation_boosts {}
          :valence_delta 0.0
          :surprise 0.0
          :emotional_episodes [{:affect "dread"
                                :source_situation "s1_seeing_through"
                                :intensity 0.15
                                :decay 0.9
                                :indices ["seam"]}]
          :interpretation_note "The stage remains unsettling."})]
    (is (> (get-in world [:situations-state :s1_seeing_through :threat])
           0.58))))
