(ns daydreamer.episodic-memory-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.context :as cx]
            [daydreamer.episodic-memory :as epmem]
            [daydreamer.rules :as rules]))

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
      :cycle 0
      :trace []
      :recent-indices []
      :recent-episodes []
      :id-counter 1}
     root-id]))

(defn graph-rule
  [rule-id antecedent-type consequent-type]
  {:id rule-id
   :rule-kind :planning
   :mueller-mode :both
   :antecedent-schema [{:fact/type antecedent-type
                        :fact/id '?fact-id}]
   :consequent-schema [{:fact/type consequent-type
                        :fact/id '?fact-id}]
   :plausibility 1.0
   :index-projections {:match [] :emit []}
   :denotation {:intended-effect consequent-type
                :failure-modes []
                :validation-fn nil}
   :executor {:kind :instantiate :spec {}}
   :graph-cache {:out-edge-bases [] :in-edge-bases []}
   :provenance {}})

(deftest create-episode-test
  (is (= {:id :ep-1
          :rule :reversal-plan
          :goal-id :g-1
          :context-id :cx-1
          :realism 0.7
          :desirability 0.5
          :indices #{:anger :s1}
          :content-indices #{:anger :s1}
          :reminding-indices #{:anger :s1}
          :provenance-indices #{}
          :support-indices #{}
          :cue-indices {:content #{:anger :s1}
                        :reminding #{:anger :s1}
                        :provenance #{}
                        :support #{}}
          :admission-status :durable
          :plan-threshold 2
          :reminding-threshold 1
          :children []
          :descendants [:ep-1]}
         (epmem/create-episode {:rule :reversal-plan
                                :goal-id :g-1
                                :context-id :cx-1
                                :realism 0.7
                                :desirability 0.5
                                :indices #{:anger :s1}
                                :plan-threshold 2
                                :reminding-threshold 1}))))

(deftest create-episode-preserves-optional-provenance
  (is (= {:id :ep-9
          :rule :runtime-thought-residue
          :goal-id :g-7
          :context-id :cx-4
          :realism :imaginary
          :desirability :mixed
          :indices #{:honesty}
          :content-indices #{:honesty}
          :reminding-indices #{:honesty}
          :provenance-indices #{}
          :support-indices #{}
          :cue-indices {:content #{:honesty}
                        :reminding #{:honesty}
                        :provenance #{}
                        :support #{}}
          :admission-status :durable
          :plan-threshold 0
          :reminding-threshold 0
          :children []
          :descendants [:ep-9]
          :provenance {:source :runtime-thought-feedback
                       :cycle 4}
          :rule-path [:goal-family/roving-trigger
                      :goal-family/roving-activation]
          :edge-path [{:from-rule :goal-family/roving-trigger
                       :to-rule :goal-family/roving-activation
                       :fact-type :goal-family-trigger
                       :edge-kind :state-transition}]}
         (epmem/create-episode
          {:id :ep-9
           :rule :runtime-thought-residue
           :goal-id :g-7
           :context-id :cx-4
           :realism :imaginary
           :desirability :mixed
           :indices #{:honesty}
           :provenance {:source :runtime-thought-feedback
                        :cycle 4}
           :rule-path [:goal-family/roving-trigger
                       :goal-family/roving-activation]
           :edge-path [{:from-rule :goal-family/roving-trigger
                        :to-rule :goal-family/roving-activation
                        :fact-type :goal-family-trigger
                        :edge-kind :state-transition}]}))))

(deftest add-and-store-episode-test
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :roving-plan
                                               :goal-id :g-1
                                               :context-id root-id})
        world (-> world
                  (epmem/store-episode episode-id :s1
                                       {:plan? true :reminding? true})
                  (epmem/store-episode episode-id :anger
                                       {:plan? true}))]
    (is (= #{episode-id} (get-in world [:episode-index :s1])))
    (is (= #{:s1 :anger} (get-in world [:episodes episode-id :indices])))
    (is (= #{:s1 :anger} (get-in world [:episodes episode-id :content-indices])))
    (is (= #{:s1} (get-in world [:episodes episode-id :reminding-indices])))
    (is (= 2 (get-in world [:episodes episode-id :plan-threshold])))
    (is (= 1 (get-in world [:episodes episode-id :reminding-threshold])))))

(deftest promote-episode-upgrades-provisional-to-durable
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id
                                               :admission-status :provisional})
        [world promoted?]
        (epmem/promote-episode world
                               episode-id
                               {:reason :cross-family-reuse
                                :source-family :rationalization
                                :target-family :roving
                                :source-rule :goal-family/rationalization-plan-dispatch
                                :target-rule :goal-family/roving-plan-dispatch})
        episode (get-in world [:episodes episode-id])]
    (is promoted?)
    (is (= :durable (:admission-status episode)))
    (is (= [{:from-status :provisional
             :to-status :durable
             :cycle 0
             :reason :cross-family-reuse
             :source-family :rationalization
             :target-family :roving
             :source-rule :goal-family/rationalization-plan-dispatch
             :target-rule :goal-family/roving-plan-dispatch}]
           (:promotion-history episode)))))

(deftest note-episode-use-and-resolve-outcome-promotes-cross-family-exemplar-after-repeated-success
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id
                                               :retention-class :payload-exemplar
                                               :keep-decision :keep-exemplar
                                               :admission-status :provisional
                                               :provenance {:source :family-plan
                                                            :family :rationalization}})
        [world use-info-1]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-1
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world outcome-info-1]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id use-info-1)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world first-transition]
        (epmem/reconcile-episode-admission world episode-id)
        [world use-info-2]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-2
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world outcome-info-2]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id use-info-2)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world second-transition]
        (epmem/reconcile-episode-admission world episode-id)
        episode (get-in world [:episodes episode-id])]
    (is (= :pending (:status use-info-1)))
    (is (= :succeeded (:outcome outcome-info-1)))
    (is (:evidence-recorded? outcome-info-1))
    (is (nil? first-transition))
    (is (= :pending (:status use-info-2)))
    (is (= :succeeded (:outcome outcome-info-2)))
    (is (:evidence-recorded? outcome-info-2))
    (is (= {:episode-id episode-id
            :from-status :provisional
            :to-status :durable
            :reason :cross-family-use-success}
           second-transition))
    (is (= :durable (:admission-status episode)))
    (is (= [{:type :cross-family-use-success
             :cycle 0
             :use-id (:use-id use-info-1)
             :source-family :rationalization
             :target-family :roving
             :source-rule :goal-family/rationalization-plan-dispatch
             :target-rule :goal-family/roving-plan-dispatch
             :branch-context-id root-id
             :goal-id :g-1}
            {:type :cross-family-use-success
             :cycle 0
             :use-id (:use-id use-info-2)
             :source-family :rationalization
             :target-family :roving
             :source-rule :goal-family/rationalization-plan-dispatch
             :target-rule :goal-family/roving-plan-dispatch
             :branch-context-id root-id
             :goal-id :g-2}]
           (:promotion-evidence episode)))))

(deftest resolve-episode-use-outcome-is-idempotent-for-already-resolved-use
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id
                                               :retention-class :payload-exemplar
                                               :keep-decision :keep-exemplar
                                               :admission-status :provisional
                                               :provenance {:source :family-plan
                                                            :family :rationalization}})
        [world use-info]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-1
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world first-outcome]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id use-info)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world second-outcome]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id use-info)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        episode (get-in world [:episodes episode-id])]
    (is (false? (:already-resolved? first-outcome)))
    (is (:evidence-recorded? first-outcome))
    (is (true? (:already-resolved? second-outcome)))
    (is (false? (:evidence-recorded? second-outcome)))
    (is (= 1 (get-in episode [:outcome-stats :successful-use-count])))
    (is (= 1 (count (:promotion-evidence episode))))
    (is (= 1 (count (:use-history episode))))
    (is (= :resolved (get-in episode [:use-history 0 :status])))))

(deftest resolve-episode-use-outcome-rejects-unsupported-outcomes
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id})
        [world use-info]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :source-family :rationalization
                                 :target-family :roving})]
    (try
      (epmem/resolve-episode-use-outcome world
                                         episode-id
                                         (:use-id use-info)
                                         {:outcome :success})
      (is false "expected unsupported episode use outcome to throw")
      (catch clojure.lang.ExceptionInfo ex
        (is (= "Unsupported episode use outcome" (ex-message ex)))
        (is (= {:episode-id episode-id
                :use-id (:use-id use-info)
                :outcome :success
                :allowed-outcomes #{:succeeded :failed :backfired :contradicted}}
               (ex-data ex)))))))

(deftest reconcile-episode-admission-uses-qualifying-promotion-evidence
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :reversal-plan
                                               :context-id root-id
                                               :retention-class :payload-exemplar
                                               :keep-decision :keep-exemplar
                                               :admission-status :provisional})
        [world _]
        (epmem/record-promotion-evidence world
                                         episode-id
                                         {:type :cross-family-use-success
                                          :use-id :epuse-1
                                          :source-family :reversal
                                          :target-family :roving
                                          :source-rule :goal-family/reversal-plan-dispatch
                                          :target-rule :goal-family/roving-plan-dispatch})
        [world _]
        (epmem/record-promotion-evidence world
                                         episode-id
                                         {:type :cross-family-use-success
                                          :use-id :epuse-2
                                          :source-family :reversal
                                          :target-family :roving
                                          :source-rule :goal-family/reversal-plan-dispatch
                                          :target-rule :goal-family/roving-plan-dispatch})
        [world _]
        (epmem/record-promotion-evidence world
                                         episode-id
                                         {:type :other-evidence
                                          :source-family :other
                                          :target-family :other-target
                                          :source-rule :goal-family/other
                                          :target-rule :goal-family/other-target})
        [world transition]
        (epmem/reconcile-episode-admission world episode-id)
        episode (get-in world [:episodes episode-id])]
    (is (= {:episode-id episode-id
            :from-status :provisional
            :to-status :durable
            :reason :cross-family-use-success}
           transition))
    (is (= [{:from-status :provisional
             :to-status :durable
             :cycle 0
             :reason :cross-family-use-success
             :source-family :reversal
             :target-family :roving
             :source-rule :goal-family/reversal-plan-dispatch
             :target-rule :goal-family/roving-plan-dispatch}]
           (:promotion-history episode)))))

(deftest reconcile-episode-admission-vindicates-pending-same-family-uses-from-repeated-later-cross-family-evidence
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id
                                               :retention-class :payload-exemplar
                                               :keep-decision :keep-exemplar
                                               :admission-status :durable
                                               :provenance {:source :family-plan
                                                            :family :rationalization}})
        [world _same-family-use]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :frame-source
                                 :goal-id :g-1
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :rationalization
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/rationalization-plan-dispatch})
        [world cross-family-use]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-2
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id cross-family-use)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world cross-family-use-2]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-3
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id cross-family-use-2)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world transition]
        (epmem/reconcile-episode-admission world episode-id)
        episode (get-in world [:episodes episode-id])
        same-family-record (first (:use-history episode))
        cross-family-record (second (:use-history episode))
        second-cross-family-record (nth (:use-history episode) 2)]
    (is (nil? transition))
    (is (= :resolved (:status same-family-record)))
    (is (= :succeeded (:outcome same-family-record)))
    (is (= :later-cross-family-vindication
           (:outcome-reason same-family-record)))
    (is (= :resolved (:status cross-family-record)))
    (is (= :succeeded (:outcome cross-family-record)))
    (is (= :resolved (:status second-cross-family-record)))
    (is (= :succeeded (:outcome second-cross-family-record)))
    (is (= 3 (get-in episode [:outcome-stats :successful-use-count])))
    (is (= [{:cycle 0
             :type :cross-family-use-success
             :use-id (:use-id cross-family-use)
             :source-family :rationalization
             :target-family :roving
             :source-rule :goal-family/rationalization-plan-dispatch
             :target-rule :goal-family/roving-plan-dispatch
             :branch-context-id root-id
             :goal-id :g-2}
            {:cycle 0
             :type :cross-family-use-success
             :use-id (:use-id cross-family-use-2)
             :source-family :rationalization
             :target-family :roving
             :source-rule :goal-family/rationalization-plan-dispatch
             :target-rule :goal-family/roving-plan-dispatch
             :branch-context-id root-id
             :goal-id :g-3}]
           (:promotion-evidence episode)))
    (is (= :durable (:admission-status episode)))
    (is (nil? (:promotion-history episode)))))

(deftest reconcile-episode-admission-does-not-vindicate-same-family-use-from-only-one-later-cross-family-success
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id
                                               :retention-class :payload-exemplar
                                               :keep-decision :keep-exemplar
                                               :admission-status :durable
                                               :provenance {:source :family-plan
                                                            :family :rationalization}})
        [world same-family-use]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :frame-source
                                 :goal-id :g-1
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :rationalization
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/rationalization-plan-dispatch})
        [world cross-family-use]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-2
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id cross-family-use)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world transition]
        (epmem/reconcile-episode-admission world episode-id)
        episode (get-in world [:episodes episode-id])
        same-family-record (first (:use-history episode))]
    (is (nil? transition))
    (is (= :pending (:status same-family-record)))
    (is (nil? (:outcome same-family-record)))
    (is (nil? (:outcome-reason same-family-record)))
    (is (= 1 (get-in episode [:outcome-stats :successful-use-count])))
    (is (= [{:cycle 0
             :type :cross-family-use-success
             :use-id (:use-id cross-family-use)
             :source-family :rationalization
             :target-family :roving
             :source-rule :goal-family/rationalization-plan-dispatch
             :target-rule :goal-family/roving-plan-dispatch
             :branch-context-id root-id
             :goal-id :g-2}]
           (:promotion-evidence episode)))
    (is (= (:use-id same-family-use)
           (:use-id same-family-record)))))

(deftest reconcile-episode-admission-does-not-vindicate-same-family-use-from-earlier-cross-family-evidence
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id
                                               :retention-class :payload-exemplar
                                               :keep-decision :keep-exemplar
                                               :admission-status :durable
                                               :provenance {:source :family-plan
                                                            :family :rationalization}})
        [world cross-family-use]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-1
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id cross-family-use)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world same-family-use]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :frame-source
                                 :goal-id :g-2
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :rationalization
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/rationalization-plan-dispatch})
        [world transition]
        (epmem/reconcile-episode-admission world episode-id)
        episode (get-in world [:episodes episode-id])
        same-family-record (second (:use-history episode))]
    (is (nil? transition))
    (is (= :pending (:status same-family-record)))
    (is (nil? (:outcome same-family-record)))
    (is (nil? (:outcome-reason same-family-record)))
    (is (= 1 (get-in episode [:outcome-stats :successful-use-count])))
    (is (= [{:cycle 0
             :type :cross-family-use-success
             :use-id (:use-id cross-family-use)
             :source-family :rationalization
             :target-family :roving
             :source-rule :goal-family/rationalization-plan-dispatch
             :target-rule :goal-family/roving-plan-dispatch
             :branch-context-id root-id
             :goal-id :g-1}]
           (:promotion-evidence episode)))
    (is (= :durable (:admission-status episode)))
    (is (nil? (:promotion-history episode)))
    (is (= (:use-id same-family-use)
           (:use-id same-family-record)))))

(deftest repeated-failed-same-family-use-flags-stale-and-demotes-durable-episode
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id
                                               :admission-status :durable
                                               :provenance {:source :family-plan
                                                            :family :rationalization}})
        world (epmem/store-episode world episode-id :shared-frame {:plan? true})
        [world use-1]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :seed
                                 :goal-id :g-1
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :rationalization
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/rationalization-plan-dispatch})
        [world outcome-1]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id use-1)
                                           {:outcome :failed
                                            :reason :frame-did-not-hold})
        [world use-2]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :seed
                                 :goal-id :g-2
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :rationalization
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/rationalization-plan-dispatch})
        [world outcome-2]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id use-2)
                                           {:outcome :failed
                                            :reason :frame-did-not-hold})
        episode-before (get-in world [:episodes episode-id])
        [world transition]
        (epmem/reconcile-episode-admission world episode-id)
        episode-after (get-in world [:episodes episode-id])]
    (is (false? (:flagged? outcome-1)))
    (is (true? (:flagged? outcome-2)))
    (is (= 2 (get-in episode-before [:outcome-stats :failed-use-count])))
    (is (= #{:same-family-loop :stale}
           (set (:anti-residue-flags episode-before))))
    (is (= {:episode-id episode-id
            :from-status :durable
            :to-status :provisional
            :reason :stale-use-demotion}
           transition))
    (is (= :provisional (:admission-status episode-after)))))

(deftest stale-flag-blocks-re-promotion-from-later-cross-family-success
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id
                                               :retention-class :payload-exemplar
                                               :keep-decision :keep-exemplar
                                               :admission-status :durable
                                               :provenance {:source :family-plan
                                                            :family :rationalization}})
        world (epmem/store-episode world episode-id :shared-frame {:plan? true})
        [world same-family-use-1]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :seed
                                 :goal-id :g-1
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :rationalization
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/rationalization-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id same-family-use-1)
                                           {:outcome :failed
                                            :reason :frame-did-not-hold})
        [world same-family-use-2]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :seed
                                 :goal-id :g-2
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :rationalization
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/rationalization-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id same-family-use-2)
                                           {:outcome :failed
                                            :reason :frame-did-not-hold})
        [world demotion]
        (epmem/reconcile-episode-admission world episode-id)
        [world cross-family-use]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-3
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world cross-family-outcome]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id cross-family-use)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world promotion-attempt]
        (epmem/reconcile-episode-admission world episode-id)
        episode (get-in world [:episodes episode-id])]
    (is (= :stale-use-demotion (:reason demotion)))
    (is (:evidence-recorded? cross-family-outcome))
    (is (nil? promotion-attempt))
    (is (= :provisional (:admission-status episode)))
    (is (contains? (set (:anti-residue-flags episode)) :stale))))

(deftest repeated-cross-family-successes-rehabilitate-stale-episode
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id
                                               :retention-class :payload-exemplar
                                               :keep-decision :keep-exemplar
                                               :admission-status :durable
                                               :provenance {:source :family-plan
                                                            :family :rationalization}})
        world (epmem/store-episode world episode-id :shared-frame {:plan? true})
        [world same-family-use-1]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :seed
                                 :goal-id :g-1
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :rationalization
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/rationalization-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id same-family-use-1)
                                           {:outcome :failed
                                            :reason :frame-did-not-hold})
        [world same-family-use-2]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :seed
                                 :goal-id :g-2
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :rationalization
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/rationalization-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id same-family-use-2)
                                           {:outcome :failed
                                            :reason :frame-did-not-hold})
        [world _]
        (epmem/reconcile-episode-admission world episode-id)
        [world cross-family-use-1]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-3
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id cross-family-use-1)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world first-promotion-attempt]
        (epmem/reconcile-episode-admission world episode-id)
        [world cross-family-use-2]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-4
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id cross-family-use-2)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world second-promotion-attempt]
        (epmem/reconcile-episode-admission world episode-id)
        episode (get-in world [:episodes episode-id])]
    (is (nil? first-promotion-attempt))
    (is (= {:episode-id episode-id
            :from-status :provisional
            :to-status :durable
            :reason :cross-family-use-success}
           second-promotion-attempt))
    (is (= :durable (:admission-status episode)))
    (is (= #{:same-family-loop}
           (set (:anti-residue-flags episode))))
    (is (= {:flag :stale
            :cycle 0
            :action :cleared
            :reason :cross-family-rehabilitation
            :episode-id episode-id}
           (last (:anti-residue-history episode))))
    (is (= [{:from-status :provisional
             :to-status :durable
             :cycle 0
             :reason :cross-family-use-success
             :source-family :rationalization
             :target-family :roving
             :source-rule :goal-family/rationalization-plan-dispatch
             :target-rule :goal-family/roving-plan-dispatch}]
           (:promotion-history episode)))))

(deftest durable-stale-episode-demotes-before-rehabilitation-can-clear-stale
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id
                                               :retention-class :payload-exemplar
                                               :keep-decision :keep-exemplar
                                               :admission-status :durable
                                               :provenance {:source :family-plan
                                                            :family :rationalization}})
        world (epmem/store-episode world episode-id :shared-frame {:plan? true})
        [world same-family-use-1]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :seed
                                 :goal-id :g-1
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :rationalization
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/rationalization-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id same-family-use-1)
                                           {:outcome :failed
                                            :reason :frame-did-not-hold})
        [world same-family-use-2]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :seed
                                 :goal-id :g-2
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :rationalization
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/rationalization-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id same-family-use-2)
                                           {:outcome :failed
                                            :reason :frame-did-not-hold})
        [world cross-family-use-1]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-3
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id cross-family-use-1)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world cross-family-use-2]
        (epmem/note-episode-use world
                                episode-id
                                {:reason :family-plan-use
                                 :use-role :reminded
                                 :goal-id :g-4
                                 :branch-context-id root-id
                                 :source-family :rationalization
                                 :target-family :roving
                                 :source-rule :goal-family/rationalization-plan-dispatch
                                 :target-rule :goal-family/roving-plan-dispatch})
        [world _]
        (epmem/resolve-episode-use-outcome world
                                           episode-id
                                           (:use-id cross-family-use-2)
                                           {:outcome :succeeded
                                            :reason :cross-family-family-plan-success})
        [world first-transition]
        (epmem/reconcile-episode-admission world episode-id)
        episode-after-first (get-in world [:episodes episode-id])]
    (is (= {:episode-id episode-id
            :from-status :durable
            :to-status :provisional
            :reason :stale-use-demotion}
           first-transition))
    (is (= :provisional (:admission-status episode-after-first)))
    (is (contains? (set (:anti-residue-flags episode-after-first)) :stale))
    (is (nil? (some #(when (= :cleared (:action %)) %)
                    (:anti-residue-history episode-after-first))))))

(deftest same-family-loop-flag-blocks-same-family-reentry
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :rationalization-plan
                                               :context-id root-id
                                               :provenance {:source :family-plan
                                                            :family :rationalization}
                                               :admission-status :durable})
        world (epmem/store-episode world episode-id :shared-frame {:plan? true})
        [world first-reuse]
        (epmem/note-episode-reuse world
                                  episode-id
                                  {:reason :family-plan-reuse
                                   :source-family :rationalization
                                   :target-family :rationalization})
        [world second-reuse]
        (epmem/note-episode-reuse world
                                  episode-id
                                  {:reason :family-plan-reuse
                                   :source-family :rationalization
                                   :target-family :rationalization})
        episode (get-in world [:episodes episode-id])]
    (is (= {:same-family? true
            :same-family-reuse-count 1
            :cross-family-reuse-count 0
            :loop-risk? false
            :flagged? false}
           first-reuse))
    (is (= {:same-family? true
            :same-family-reuse-count 2
            :cross-family-reuse-count 0
            :loop-risk? true
            :flagged? true}
           second-reuse))
    (is (= [:same-family-loop] (:anti-residue-flags episode)))
    (is (= []
           (epmem/retrieve-episodes world
                                    [:shared-frame]
                                    {:active-family :rationalization})))
    (is (= [{:episode-id episode-id
             :marks 1
             :threshold 1
             :admission-status :durable}]
           (epmem/retrieve-episodes world
                                    [:shared-frame]
                                    {:active-family :roving})))))

(deftest episode-accessibility-info-respects-loop-and-recent-gates
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :roving-plan
                                               :context-id root-id
                                               :retention-class :hot-cues
                                               :keep-decision :keep-hot
                                               :provenance {:source :family-plan
                                                            :family :roving}
                                               :admission-status :durable})
        world (epmem/store-episode world episode-id :sunlight {:plan? true :reminding? true})
        [world _] (epmem/flag-episode world
                                      episode-id
                                      :same-family-loop
                                      {:reason :test-loop})]
    (is (= {:episode-id episode-id
            :admission-status :durable
            :recent? false
            :accessible? false
            :retention-adjustment 0.0
            :retention-reason :flagged-same-family-loop}
           (epmem/episode-accessibility-info world
                                             episode-id
                                             {:active-family :roving})))
    (is (= {:episode-id episode-id
            :admission-status :durable
            :recent? true
            :accessible? false
            :retention-adjustment 0.0
            :retention-reason :hot-cue-fresh
            :retention-age 0}
           (epmem/episode-accessibility-info
            (-> world
                (update :recent-episodes conj episode-id)
                (update :recent-indices into [:sunlight]))
            episode-id
            {:active-family :other-family})))))

(deftest contradicted-and-backfired-flags-block-retrieval
  (let [[world root-id] (world-with-root)
        [world contradicted-id] (epmem/add-episode world
                                                   {:rule :rationalization-plan
                                                    :context-id root-id
                                                    :admission-status :durable})
        [world backfired-id] (epmem/add-episode world
                                                {:rule :reversal-plan
                                                 :context-id root-id
                                                 :admission-status :durable})
        world (-> world
                  (epmem/store-episode contradicted-id :shared-cue {:plan? true})
                  (epmem/store-episode backfired-id :shared-cue {:plan? true}))
        [world contradicted-flagged?]
        (epmem/flag-episode world
                            contradicted-id
                            :contradicted
                            {:reason :test-contradiction})
        [world backfired-flagged?]
        (epmem/flag-episode world
                            backfired-id
                            :backfired
                            {:reason :test-backfire})]
    (is contradicted-flagged?)
    (is backfired-flagged?)
    (is (= [] (epmem/retrieve-episodes world [:shared-cue] {})))))

(deftest stale-flag-penalizes-otherwise-retrievable-episodes
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :roving-plan
                                               :context-id root-id
                                               :admission-status :durable})
        world (-> world
                  (epmem/store-episode episode-id :cue-a {:plan? true})
                  (epmem/store-episode episode-id :cue-b {:plan? true}))
        [world flagged?]
        (epmem/flag-episode world
                            episode-id
                            :stale
                            {:reason :test-staleness})]
    (is flagged?)
    (is (= []
           (epmem/retrieve-episodes world
                                    [:cue-a :cue-b]
                                    {})))))

(deftest retrieve-episodes-threshold-test
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :reversal-plan
                                               :goal-id :g-1
                                               :context-id root-id})
        world (-> world
                  (epmem/store-episode episode-id :s1 {:plan? true})
                  (epmem/store-episode episode-id :anger {:plan? true}))]
    (testing "one cue is below the threshold"
      (is (= [] (epmem/retrieve-episodes world [:s1] {}))))
    (testing "two cues meet the threshold"
      (is (= [{:episode-id episode-id
               :marks 2
               :threshold 2
               :admission-status :durable}]
             (epmem/retrieve-episodes world [:s1 :anger] {}))))))

(deftest serendipity-lowers-threshold
  (let [[world root-id] (world-with-root)
        [world episode-id] (epmem/add-episode world
                                              {:rule :reversal-plan
                                               :goal-id :g-1
                                               :context-id root-id})
        world (-> world
                  (epmem/store-episode episode-id :s1 {:reminding? true})
                  (epmem/store-episode episode-id :anger {:reminding? true}))]
    (is (= [{:episode-id episode-id
             :marks 1
             :threshold 1
             :admission-status :durable}]
           (epmem/retrieve-episodes world
                                    [:s1]
                                    {:serendipity? true
                                     :threshold-key :reminding-threshold})))))

(deftest retrieve-episodes-adds-provenance-bonus-for-graph-connected-history
  (let [[world root-id] (world-with-root)
        [world connected-episode-id]
        (epmem/add-episode world
                           {:rule :connected-memory
                            :context-id root-id
                            :rule-path [:test/terminal]
                            :reminding-threshold 1})
        [world unrelated-episode-id]
        (epmem/add-episode world
                           {:rule :unrelated-memory
                            :context-id root-id
                            :rule-path [:test/unrelated]
                            :reminding-threshold 1})
        world (-> world
                  (epmem/store-episode connected-episode-id :shared-cue
                                       {:reminding? true})
                  (epmem/store-episode unrelated-episode-id :shared-cue
                                       {:reminding? true}))
        connection-graph
        (rules/build-connection-graph
         [(graph-rule :test/source :source-state :bridge-state)
          (graph-rule :test/bridge :bridge-state :target-state)
          (graph-rule :test/terminal :target-state :terminal-state)
          (graph-rule :test/unrelated :other-state :other-terminal-state)])]
    (testing "without provenance opts the single cue stays below threshold"
      (is (= []
             (epmem/retrieve-episodes world
                                      [:shared-cue]
                                      {:threshold-key :reminding-threshold}))))
    (testing "graph-connected history earns a retrieval bonus"
      (is (= [{:episode-id connected-episode-id
               :marks 1
               :threshold 2
               :admission-status :durable
               :provenance-bonus 2.5
               :effective-marks 3.5
               :provenance-reason :graph-bridge
               :provenance-bridge-depth 2
               :provenance-bridge-path [:test/source
                                        :test/bridge
                                        :test/terminal]
               :provenance-bridge-direction :active-to-episode
               :provenance-bridge-count 1}]
             (epmem/retrieve-episodes
              world
              [:shared-cue]
              {:threshold-key :reminding-threshold
               :connection-graph connection-graph
               :active-rule-path [:test/source]}))))))

(deftest retrieve-episodes-prefers-closer-graph-bridges-for-relevance
  (let [[world root-id] (world-with-root)
        [world shallow-episode-id]
        (epmem/add-episode world
                           {:rule :shallow-memory
                            :context-id root-id
                            :rule-path [:test/bridge]
                            :reminding-threshold 1})
        [world deep-episode-id]
        (epmem/add-episode world
                           {:rule :deep-memory
                            :context-id root-id
                            :rule-path [:test/terminal]
                            :reminding-threshold 1})
        world (-> world
                  (epmem/store-episode shallow-episode-id :shared-cue
                                       {:reminding? true})
                  (epmem/store-episode deep-episode-id :shared-cue
                                       {:reminding? true}))
        connection-graph
        (rules/build-connection-graph
         [(graph-rule :test/source :source-state :bridge-state)
          (graph-rule :test/bridge :bridge-state :target-state)
          (graph-rule :test/terminal :target-state :terminal-state)])
        results (epmem/retrieve-episodes
                 world
                 [:shared-cue]
                 {:threshold-key :reminding-threshold
                  :connection-graph connection-graph
                  :active-rule-path [:test/source]})]
    (is (= [shallow-episode-id deep-episode-id]
           (mapv :episode-id results)))
    (is (= [1 2]
           (mapv :provenance-bridge-depth results)))
    (is (= [3.0 2.5]
           (mapv :provenance-bonus results)))
    (is (= [4.0 3.5]
           (mapv :effective-marks results)))))

(deftest hot-cue-episodes-lose-accessibility-as-they-age
  (let [[world root-id] (world-with-root)
        [world episode-id]
        (epmem/add-episode world
                           {:rule :roving-plan
                            :context-id root-id
                            :retention-class :hot-cues
                            :keep-decision :keep-hot})
        world (epmem/store-episode world episode-id :sunlight {:reminding? true})]
    (testing "fresh hot cues remain accessible"
      (is (= [{:episode-id episode-id
               :marks 1
               :threshold 1
               :admission-status :durable
               :retention-adjustment 0.0
               :retention-reason :hot-cue-fresh
               :retention-age 0}]
             (epmem/retrieve-episodes world
                                      [:sunlight]
                                      {:threshold-key :reminding-threshold}))))
    (testing "stale hot cues decay below the same retrieval threshold"
      (is (= []
             (epmem/retrieve-episodes (assoc world :cycle 5)
                                      [:sunlight]
                                      {:threshold-key :reminding-threshold}))))))

(deftest payload-exemplar-access-is-capped-per-cluster
  (let [[world root-id] (world-with-root)
        cluster [:family/rationalization :g-1 :frame-1]
        [world oldest-episode-id]
        (epmem/add-episode world
                           {:rule :rationalization-plan
                            :context-id root-id
                            :retention-class :payload-exemplar
                            :keep-decision :keep-exemplar
                            :evaluation {:payload-cluster cluster}})
        world (-> world
                  (assoc :cycle 1)
                  (epmem/store-episode oldest-episode-id :hope {:reminding? true}))
        [world middle-episode-id]
        (epmem/add-episode world
                           {:rule :rationalization-plan
                            :context-id root-id
                            :retention-class :payload-exemplar
                            :keep-decision :keep-exemplar
                            :evaluation {:payload-cluster cluster}})
        world (-> world
                  (assoc :cycle 2)
                  (epmem/store-episode middle-episode-id :hope {:reminding? true}))
        [world newest-episode-id]
        (epmem/add-episode world
                           {:rule :rationalization-plan
                            :context-id root-id
                            :retention-class :payload-exemplar
                            :keep-decision :keep-exemplar
                            :evaluation {:payload-cluster cluster}})
        world (-> world
                  (assoc :cycle 3)
                  (epmem/store-episode newest-episode-id :hope {:reminding? true}))
        results (epmem/retrieve-episodes world
                                         [:hope]
                                         {:threshold-key :reminding-threshold})
        result-by-id (into {} (map (juxt :episode-id identity) results))]
    (is (= #{middle-episode-id newest-episode-id}
           (set (map :episode-id results))))
    (is (= nil (get result-by-id oldest-episode-id)))
    (is (= {:retention-adjustment 0.0
            :retention-reason :payload-exemplar-active
            :payload-cluster cluster
            :payload-cluster-rank 2}
           (select-keys (get result-by-id middle-episode-id)
                        [:retention-adjustment
                         :retention-reason
                         :payload-cluster
                         :payload-cluster-rank])))
    (is (= {:retention-adjustment 0.0
            :retention-reason :payload-exemplar-active
            :payload-cluster cluster
            :payload-cluster-rank 1}
           (select-keys (get result-by-id newest-episode-id)
                        [:retention-adjustment
                         :retention-reason
                         :payload-cluster
                         :payload-cluster-rank])))))

(deftest episode-provenance-info-detects-episode-to-active-bridges
  (let [[world root-id] (world-with-root)
        [world episode-id]
        (epmem/add-episode world
                           {:rule :deep-memory
                            :context-id root-id
                            :rule-path [:test/source]})
        connection-graph
        (rules/build-connection-graph
         [(graph-rule :test/source :source-state :bridge-state)
          (graph-rule :test/bridge :bridge-state :target-state)
          (graph-rule :test/terminal :target-state :terminal-state)])
        info (epmem/episode-provenance-info
              world
              episode-id
              {:connection-graph connection-graph
               :active-rule-path [:test/terminal]})]
    (is (= {:provenance-bonus 2.5
            :provenance-reason :graph-bridge
            :provenance-bridge-depth 2
            :provenance-bridge-path [:test/source
                                     :test/bridge
                                     :test/terminal]
            :provenance-bridge-direction :episode-to-active
            :provenance-bridge-count 1}
           info))))

(deftest recent-index-and-episode-bounds
  (let [[world _] (world-with-root)
        world (reduce epmem/add-recent-index
                      world
                      [:a :b :c :d :e :f :g])
        [world ep1] (epmem/add-episode world {:rule :r1})
        [world ep2] (epmem/add-episode world {:rule :r2})
        [world ep3] (epmem/add-episode world {:rule :r3})
        [world ep4] (epmem/add-episode world {:rule :r4})
        [world ep5] (epmem/add-episode world {:rule :r5})
        world (-> world
                  (epmem/add-recent-episode ep1)
                  (epmem/add-recent-episode ep2)
                  (epmem/add-recent-episode ep3)
                  (epmem/add-recent-episode ep4)
                  (epmem/add-recent-episode ep5))]
    (is (= [:b :c :d :e :f :g] (:recent-indices world)))
    (is (= [ep2 ep3 ep4 ep5] (:recent-episodes world)))))

(deftest recent-episode-descendant-exclusion
  (let [[world _] (world-with-root)
        [world child-id] (epmem/add-episode world {:rule :child})
        [world parent-id] (epmem/add-episode world {:rule :parent
                                                    :children [child-id]})
        world (-> world
                  (epmem/add-recent-episode child-id)
                  (epmem/add-recent-episode parent-id))]
    (is (= [parent-id] (:recent-episodes world)))
    (is (epmem/recent-episode? world child-id))))

(deftest episode-reminding-cascades
  (let [[world root-id] (world-with-root)
        [world ep1] (epmem/add-episode world
                                       {:rule :r1
                                        :goal-id :g-1
                                        :context-id root-id})
        [world ep2] (epmem/add-episode world
                                       {:rule :r2
                                        :goal-id :g-2
                                        :context-id root-id})
        world (-> world
                  (epmem/store-episode ep1 :s1 {:reminding? true})
                  (epmem/store-episode ep2 :s1 {:reminding? true}))
        [world reminded] (epmem/episode-reminding world ep1)]
    (is (= [ep2] reminded))
    (is (= [ep1 ep2] (:recent-episodes world)))
    (is (= [:s1] (:recent-indices world)))))
