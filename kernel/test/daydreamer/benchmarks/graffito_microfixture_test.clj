(ns daydreamer.benchmarks.graffito-microfixture-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.benchmarks.graffito-microfixture :as fixture]
            [daydreamer.context :as cx]))

(deftest build-world-seeds-typed-graffito-fact-space
  (let [world (fixture/build-world)
        apartment-context-id (get-in world [:graffito-microfixture :contexts :apartment])
        mural-context-id (get-in world [:graffito-microfixture :contexts :mural])
        apartment-facts (set (cx/visible-facts world apartment-context-id))
        mural-facts (set (cx/visible-facts world mural-context-id))
        apartment-types (set (map :fact/type apartment-facts))
        mural-types (set (map :fact/type mural-facts))]
    (testing "the apartment context carries richer typed state than flat pressure alone"
      (is (every? apartment-types
                  [:present-actor
                   :relationship
                   :role-obligation
                   :artifact-state
                   :recent-event
                   :appraisal
                   :rationalization-frame]))
      (is (contains? apartment-facts
                     {:fact/type :relationship
                      :fact/id :tony_seeks_recognition_from_monk}))
      (is (contains? apartment-facts
                     {:fact/type :artifact-state
                      :fact/id :can_is_inheritance})))
    (testing "the mural context carries exposure and pressure structure"
      (is (every? mural-types
                  [:present-actor
                   :relationship
                   :exposure
                   :artifact-state
                   :recent-event
                   :appraisal
                   :failure-cause]))
      (is (contains? mural-facts
                     {:fact/type :exposure
                      :fact/id :cop_light_closing}))
      (is (contains? mural-facts
                     {:fact/type :recent-event
                      :fact/id :cops_interrupted_mural})))))

(deftest activation-read-selects-the-expected-graffito-families
  (let [[world activation] (fixture/activation-read (fixture/build-world))
        rationalization-goal (:rationalization-goal activation)
        reversal-goal (:reversal-goal activation)
        apartment-context-id (get-in world [:graffito-microfixture :contexts :apartment])
        mural-context-id (get-in world [:graffito-microfixture :contexts :mural])]
    (testing "rationalization is grounded in Grandma's apartment"
      (is (some? rationalization-goal))
      (is (= apartment-context-id
             (:trigger-context-id rationalization-goal)))
      (is (= :rf_crooked_is_power
             (:trigger-frame-id rationalization-goal))))
    (testing "reversal is grounded in the mural crisis"
      (is (some? reversal-goal))
      (is (= mural-context-id
             (:trigger-context-id reversal-goal)))
      (is (= :g_tony_escape_capture
             (:trigger-failed-goal-id reversal-goal))))))

(deftest run-slice-keeps-graffito-mechanism-honest-on-typed-facts
  (let [{:keys [world cycle-summaries summaries]} (fixture/run-slice)
        apartment-rationalization (nth cycle-summaries 0)
        mural-reversal (nth cycle-summaries 1)
        retrieval-probe (nth cycle-summaries 2)
        rationalization-episode-id (:family-plan-episode-id apartment-rationalization)
        rationalization-episode (get-in world [:episodes rationalization-episode-id])
        reversal-episode-id (:family-plan-episode-id mural-reversal)
        reversal-episode (get-in world [:episodes reversal-episode-id])
        first-hit (first (:hits retrieval-probe))]
    (testing "the apartment rationalization stays about typed psychological structure"
      (is (= :authored (:winner-origin apartment-rationalization)))
      (is (= :rf_crooked_is_power (:frame-id apartment-rationalization)))
      (is (= #{:can_is_inheritance
               :tony_seeks_recognition_from_monk
               :creation_is_regulation}
             (:content-indices rationalization-episode)))
      (is (= :provisional (:admission-status rationalization-episode)))
      (is (some #{:goal-family/rationalization-plan-dispatch}
                (:rule-path rationalization-episode))))
    (testing "the mural reversal finds the expected authored counterfactual pivot"
      (is (= :authored (:winner-origin mural-reversal)))
      (is (= :fc_cop_light_turns_art_to_capture
             (:source-id mural-reversal)))
      (is (= :provisional (:admission-status reversal-episode)))
      (is (some #{:goal-family/reversal-plan-dispatch}
                (:rule-path reversal-episode))))
    (testing "typed-fact retrieval from the mural context can see the stored apartment episode"
      (is (= rationalization-episode-id
             (:episode-id first-hit)))
      (is (= #{:can_is_inheritance
               :tony_seeks_recognition_from_monk
               :creation_is_regulation}
             (set (:overlap first-hit))))
      (is (= :provisional (:admission-status first-hit))))
    (testing "the microfixture returns readable summaries"
      (is (= 3 (count summaries)))
      (is (re-find #"apartment rationalization"
                   (first summaries)))
      (is (re-find #"mural retrieval probe"
                   (nth summaries 2))))))
