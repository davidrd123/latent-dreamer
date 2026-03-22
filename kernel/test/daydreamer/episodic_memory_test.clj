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
   :antecedent-schema [{:fact/type antecedent-type}]
   :consequent-schema [{:fact/type consequent-type}]
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
    (is (= 2 (get-in world [:episodes episode-id :plan-threshold])))
    (is (= 1 (get-in world [:episodes episode-id :reminding-threshold])))))

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
               :threshold 2}]
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
             :threshold 1}]
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
               :provenance-bonus 2.0
               :effective-marks 3.0
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

(deftest retrieve-episodes-prefers-deeper-graph-bridges
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
    (is (= [deep-episode-id shallow-episode-id]
           (mapv :episode-id results)))
    (is (= [2 1]
           (mapv :provenance-bridge-depth results)))
    (is (= [2.0 1.0]
           (mapv :provenance-bonus results)))
    (is (= [3.0 2.0]
           (mapv :effective-marks results)))))

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
