(ns daydreamer.runtime-thought-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.context :as cx]
            [daydreamer.runtime-thought :as runtime-thought]))

(defn world-with-root
  []
  (let [root (cx/create-context)
        root-id (:id root)]
    [{:contexts {root-id root}
      :goals {}
      :episodes {}
      :episode-index {}
      :emotions {}
      :mode :daydreaming
      :cycle 4
      :trace [{:cycle-num 4
               :selected-goal {:id :g-7
                               :goal-type :roving}
               :activations [{:goal-id :g-7
                              :goal-type :roving
                              :rule-provenance {:rule-path [:goal-family/roving-trigger
                                                            :goal-family/roving-activation]
                                                :edge-path [{:from-rule :goal-family/roving-trigger
                                                             :to-rule :goal-family/roving-activation
                                                             :fact-type :goal-family-trigger
                                                             :edge-kind :state-transition}]}}]
               :rule-provenance {:rule-path [:goal-family/roving-plan-request
                                             :goal-family/roving-plan-dispatch]
                                 :edge-path [{:from-rule :goal-family/roving-plan-request
                                              :to-rule :goal-family/roving-plan-dispatch
                                              :fact-type :family-plan-request
                                              :edge-kind :state-transition}]}}]
      :recent-indices []
      :recent-episodes []
      :id-counter 0}
     root-id]))

(deftest apply-feedback-stores-rule-path-provenance-on-episode
  (let [[world root-id] (world-with-root)
        packet {:selected_goal {:id "g-7"
                                :context_id (name root-id)}
                :allowed_residue_indices ["honesty" "performance"]}
        raw-feedback {:provider :test
                      :model :mock
                      :thought_beat_text "He worries the crowd will hear the break in his cover."
                      :mood_tags ["guarded" "tense"]
                      :residue_summary "The cover story still rubs against the honest seam."
                      :residue_indices ["honesty" "performance"]
                      :image_hint "hand on curtain"
                      :audio_hint "thin metallic hum"}
        [world feedback-applied] (runtime-thought/apply-feedback world packet raw-feedback)
        episode-id (:episode-id feedback-applied)
        episode (get-in world [:episodes episode-id])]
    (testing "residue episode keeps the structural rule path that produced the cycle"
      (is (= [:goal-family/roving-trigger
              :goal-family/roving-activation
              :goal-family/roving-plan-request
              :goal-family/roving-plan-dispatch]
             (:rule-path episode)))
      (is (= {:source :runtime-thought-feedback
              :cycle 4
              :goal-id :g-7
              :activation-rule-provenance {:rule-path [:goal-family/roving-trigger
                                                       :goal-family/roving-activation]
                                           :edge-path [{:from-rule :goal-family/roving-trigger
                                                        :to-rule :goal-family/roving-activation
                                                        :fact-type :goal-family-trigger
                                                        :edge-kind :state-transition}]}
              :planning-rule-provenance {:rule-path [:goal-family/roving-plan-request
                                                     :goal-family/roving-plan-dispatch]
                                         :edge-path [{:from-rule :goal-family/roving-plan-request
                                                      :to-rule :goal-family/roving-plan-dispatch
                                                      :fact-type :family-plan-request
                                                      :edge-kind :state-transition}]}}
             (:provenance episode))))
    (testing "rule-path indices are stored without inflating residue thresholds"
      (is (= #{episode-id} (get-in world [:episode-index :honesty])))
      (is (= #{episode-id} (get-in world [:episode-index :performance])))
      (is (= #{episode-id}
             (get-in world [:provenance-episode-index :goal-family/roving-trigger])))
      (is (= #{episode-id}
             (get-in world [:provenance-episode-index
                            :goal-family/roving-plan-dispatch])))
      (is (= #{:honesty :performance} (:content-indices episode)))
      (is (= #{:goal-family/roving-trigger
               :goal-family/roving-activation
               :goal-family/roving-plan-request
               :goal-family/roving-plan-dispatch}
             (:provenance-indices episode)))
      (is (= 2 (:plan-threshold episode)))
      (is (= 2 (:reminding-threshold episode))))))
