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
