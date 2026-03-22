(ns daydreamer.family-evaluator-test
  (:require [clojure.test :refer [deftest is]]
            [daydreamer.family-evaluator :as evaluator]))

(deftest normalize-family-evaluation-preserves-defaults-for-invalid-fields
  (let [default-evaluation {:realism :imaginary
                            :desirability :positive
                            :retention-class :hot-cues
                            :keep-decision :keep-hot
                            :promotion-decision :stay-provisional
                            :anti-residue-flags []
                            :payload-cluster [:family/roving :g-1 :pleasant_episode_seed]
                            :evaluation-source :heuristic
                            :evaluation-reasons [:pleasant_seed]}
        normalized (evaluator/normalize-family-evaluation
                    {:realism "not-real"
                     :desirability "negative"
                     :retention_class "cold-provenance"
                     :keep_decision "archive-cold"
                     :promotion_decision "promote-durable"
                     :anti_residue_flags ["contradicted" "unknown_flag"]
                     :evaluation_reasons ["mock_archive_review" "still_salient"]
                     :evaluation_source "mock-llm"}
                    default-evaluation)]
    (is (= {:realism :imaginary
            :desirability :negative
            :retention-class :cold-provenance
            :keep-decision :archive-cold
            :promotion-decision :promote-durable
            :anti-residue-flags [:contradicted]
            :payload-cluster [:family/roving :g-1 :pleasant_episode_seed]
            :evaluation-source :mock-llm
            :evaluation-reasons [:mock_archive_review :still_salient]}
           normalized))))

(deftest build-family-evaluator-mock-mode-returns-normalized-output
  (let [evaluate-plan (evaluator/build-family-evaluator {:mode :mock})
        default-evaluation {:realism :imaginary
                            :desirability :mixed
                            :retention-class :hot-cues
                            :keep-decision :keep-hot
                            :promotion-decision :stay-provisional
                            :anti-residue-flags []
                            :payload-cluster [:family/reversal :g-2 :stored_priority]
                            :evaluation-source :heuristic
                            :evaluation-reasons [:fallback]}
        evaluation (evaluate-plan {:family :reversal
                                   :episode-payload {:input-facts [{:fact/type :counterfactual
                                                                    :fact/id :wall_was_open}]}}
                                  default-evaluation)]
    (is (= :counterfactual (:realism evaluation)))
    (is (= :payload-exemplar (:retention-class evaluation)))
    (is (= :keep-exemplar (:keep-decision evaluation)))
    (is (= :promote-durable (:promotion-decision evaluation)))
    (is (= [] (:anti-residue-flags evaluation)))
    (is (= :mock-llm (:evaluation-source evaluation)))
    (is (seq (:evaluation-reasons evaluation)))))
