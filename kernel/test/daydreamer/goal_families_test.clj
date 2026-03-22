(ns daydreamer.goal-families-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.context :as cx]
            [daydreamer.episodic-memory :as episodic]
            [daydreamer.goal-families :as families]
            [daydreamer.goals :as goals]
            [daydreamer.rules :as rules]))

(def state-fact
  {:fact/type :situation
   :fact/id :s1_seeing_through})

(def mission-fact
  {:fact/type :situation
   :fact/id :s2_the_mission})

(def guide-fact
  {:fact/type :situation
   :fact/id :s5_the_guide})

(def counterfactual-fact
  {:fact/type :counterfactual
   :fact/id :wall_was_open})

(def leaf-objective-a
  {:fact/type :assumption
   :fact/id :wall_is_open})

(def leaf-objective-b
  {:fact/type :assumption
   :fact/id :lights_stay_low})

(defn emotion-fact
  [emotion-id]
  {:fact/type :emotion
   :emotion-id emotion-id
   :strength 0.7})

(defn dependency-fact
  [from-id to-id]
  {:fact/type :dependency
   :from-id from-id
   :to-id to-id})

(defn goal-fact
  ([goal-id top-level-goal status activation-context]
   (goal-fact goal-id top-level-goal status activation-context {}))
  ([goal-id top-level-goal status activation-context extras]
   (merge {:fact/type :goal
           :goal-id goal-id
           :top-level-goal top-level-goal
           :status status
           :activation-context activation-context}
          extras)))

(defn intends-fact
  [from-goal-id to-goal-id top-level-goal]
  {:fact/type :intends
   :from-goal-id from-goal-id
   :to-goal-id to-goal-id
   :top-level-goal top-level-goal})

(defn failure-cause-fact
  [fact-id goal-id priority counterfactual-facts]
  {:fact/type :failure-cause
   :fact/id fact-id
   :goal-id goal-id
   :priority priority
   :counterfactual-facts counterfactual-facts})

(defn rationalization-frame-fact
  [fact-id goal-id priority reframe-facts]
  {:fact/type :rationalization-frame
   :fact/id fact-id
   :goal-id goal-id
   :priority priority
   :reframe-facts reframe-facts})

(defn expected-rule-provenance
  [from-rule to-rule fact-type]
  {:rule-path [from-rule to-rule]
   :edge-path [{:from-rule from-rule
                :to-rule to-rule
                :fact-type fact-type
                :edge-kind :state-transition}]})

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
      :mutation-events []
      :roving-episodes []
      :id-counter 1}
     root-id]))

(defn seed-old-plan-context
  [world root-id]
  (let [[world old-context-id] (cx/sprout world root-id)
        old-top-level-goal-id :g-old
        old-subgoal-id :g-old-child
        other-top-level-goal-id :g-other
        other-subgoal-id :g-other-child
        facts [state-fact
               mission-fact
               (emotion-fact :e-fear)
               (dependency-fact :e-fear old-top-level-goal-id)
               (goal-fact old-top-level-goal-id
                          old-top-level-goal-id
                          :failed
                          old-context-id)
               (goal-fact old-subgoal-id
                          old-top-level-goal-id
                          :runable
                          old-context-id)
               (goal-fact other-subgoal-id
                          other-top-level-goal-id
                          :runable
                          old-context-id)
               (intends-fact old-top-level-goal-id
                             old-subgoal-id
                             old-top-level-goal-id)
               (intends-fact other-top-level-goal-id
                             other-subgoal-id
                             other-top-level-goal-id)]
        world (reduce (fn [current-world fact]
                        (cx/assert-fact current-world old-context-id fact))
                      world
                      facts)]
    [world old-context-id old-top-level-goal-id old-subgoal-id other-subgoal-id]))

(defn seed-failed-leaf
  [world parent-id {:keys [goal-id emotion-id emotion-strength]
                    :or {emotion-strength 0.0}}]
  (let [[world context-id] (cx/sprout world parent-id)
        facts [{:fact/type :goal
                :goal-id goal-id
                :top-level-goal goal-id
                :status :failed
                :activation-context context-id}
               {:fact/type :emotion
                :emotion-id emotion-id
                :strength emotion-strength}]
        world (reduce (fn [current-world fact]
                        (cx/assert-fact current-world context-id fact))
                      world
                      facts)]
    [world context-id]))

(deftest gc-emotions-removes-emotions-and-dependencies
  (let [[world root-id] (world-with-root)
        [world old-context-id old-top-level-goal-id _ _]
        (seed-old-plan-context world root-id)
        world (families/gc-emotions world old-context-id)
        facts (cx/visible-facts world old-context-id)]
    (is (contains? facts state-fact))
    (is (contains? facts (goal-fact old-top-level-goal-id
                                    old-top-level-goal-id
                                    :failed
                                    old-context-id)))
    (is (not-any? #(= :emotion (:fact/type %)) facts))
    (is (not-any? #(= :dependency (:fact/type %)) facts))))

(deftest gc-plans-keeps-only-the-selected-top-level-goal
  (let [[world root-id] (world-with-root)
        [world old-context-id old-top-level-goal-id old-subgoal-id other-subgoal-id]
        (seed-old-plan-context world root-id)
        world (families/gc-plans world old-context-id [old-top-level-goal-id])
        facts (cx/visible-facts world old-context-id)]
    (is (contains? facts state-fact))
    (is (contains? facts (goal-fact old-top-level-goal-id
                                    old-top-level-goal-id
                                    :failed
                                    old-context-id)))
    (is (contains? facts (goal-fact old-subgoal-id
                                    old-top-level-goal-id
                                    :runable
                                    old-context-id)))
    (is (contains? facts (intends-fact old-top-level-goal-id
                                       old-subgoal-id
                                       old-top-level-goal-id)))
    (is (not-any? #(= other-subgoal-id (:goal-id %)) facts))
    (is (not-any? #(= other-subgoal-id (:to-goal-id %)) facts))))

(deftest sprout-alternative-past-creates-pseudo-sprouted-branch
  (let [[world root-id] (world-with-root)
        [world old-context-id old-top-level-goal-id old-subgoal-id other-subgoal-id]
        (seed-old-plan-context world root-id)
        [world new-top-level-goal-id]
        (goals/activate-top-level-goal
         world
         root-id
         {:goal-type :reversal
          :planning-type :imaginary
          :strength 0.9
          :main-motiv :e-new})
        new-context-id (get-in world [:goals new-top-level-goal-id :next-cx])
        [world sprouted-context-id]
        (families/sprout-alternative-past
         world
         {:old-context-id old-context-id
          :old-top-level-goal-id old-top-level-goal-id
          :new-context-id new-context-id
          :new-top-level-goal-id new-top-level-goal-id})
        facts (cx/visible-facts world sprouted-context-id)
        rebound-top-goal {:fact/type :goal
                          :goal-id new-top-level-goal-id
                          :top-level-goal new-top-level-goal-id
                          :status :runable
                          :activation-context sprouted-context-id}
        rebound-subgoal {:fact/type :goal
                         :goal-id old-subgoal-id
                         :top-level-goal new-top-level-goal-id
                         :status :runable
                         :activation-context sprouted-context-id}
        rebound-intends {:fact/type :intends
                         :from-goal-id new-top-level-goal-id
                         :to-goal-id old-subgoal-id
                         :top-level-goal new-top-level-goal-id}]
    (testing "the new branch is a pseudo-sprouted alternative past"
      (is (= new-context-id (:parent-id (get-in world [:contexts sprouted-context-id]))))
      (is (= true (get-in world [:contexts sprouted-context-id :pseudo-sprout?])))
      (is (= true (get-in world [:contexts sprouted-context-id :alternative-past?])))
      (is (= {:tense :conditional-present-perfect}
             (get-in world [:contexts sprouted-context-id :generation-switches]))))
    (testing "state facts survive while emotions and unrelated plans are removed"
      (is (contains? facts state-fact))
      (is (contains? facts mission-fact))
      (is (not-any? #(= :emotion (:fact/type %)) facts))
      (is (not-any? #(= :dependency (:fact/type %)) facts))
      (is (not-any? #(= other-subgoal-id (:goal-id %)) facts)))
    (testing "surviving plan structure is rebound onto the new top-level goal"
      (is (contains? facts rebound-top-goal))
      (is (contains? facts rebound-subgoal))
      (is (contains? facts rebound-intends))
      (is (= sprouted-context-id
             (goals/get-next-context world new-top-level-goal-id))))))

(deftest reversal-sprout-alternative-sets-ordering-and-input-facts
  (let [[world root-id] (world-with-root)
        [world old-context-id old-top-level-goal-id _ _]
        (seed-old-plan-context world root-id)
        [world new-top-level-goal-id]
        (goals/activate-top-level-goal
         world
         root-id
         {:goal-type :reversal
          :planning-type :imaginary
          :strength 0.9
          :main-motiv :e-new})
        new-context-id (get-in world [:goals new-top-level-goal-id :next-cx])
        [world sprouted-context-id]
        (families/reversal-sprout-alternative
         world
         {:old-context-id old-context-id
          :old-top-level-goal-id old-top-level-goal-id
          :new-context-id new-context-id
          :new-top-level-goal-id new-top-level-goal-id
          :ordering 0.85
          :input-facts [counterfactual-fact]})]
    (is (= 0.85 (get-in world [:contexts sprouted-context-id :ordering])))
    (is (cx/fact-true? world sprouted-context-id counterfactual-fact))
    (is (= [{:family :reversal
             :source-context old-context-id
             :target-context sprouted-context-id
             :input-facts [counterfactual-fact]
             :retracted-facts []}]
           (:mutation-events world)))))

(deftest reversal-leaf-candidates-discover-and-rank-failed-leafs
  (let [[world root-id] (world-with-root)
        [world shallow-id] (seed-failed-leaf world root-id
                                             {:goal-id :g-shallow
                                              :emotion-id :e-shallow
                                              :emotion-strength 0.4})
        [world branch-id] (cx/sprout world root-id)
        [world deep-id] (seed-failed-leaf world branch-id
                                          {:goal-id :g-deep
                                           :emotion-id :e-deep
                                           :emotion-strength 0.8})
        [world non-leaf-id] (seed-failed-leaf world root-id
                                              {:goal-id :g-non-leaf
                                               :emotion-id :e-parent
                                               :emotion-strength 0.9})
        [world _] (cx/sprout world non-leaf-id)
        candidates (families/reversal-leaf-candidates world)]
    (testing "only failed leaf contexts are returned"
      (is (= [deep-id shallow-id]
             (mapv :old-context-id candidates)))
      (is (not-any? #(= non-leaf-id (:old-context-id %)) candidates)))
    (testing "ranking prefers stronger emotion, then depth"
      (is (= :g-deep (:failed-goal-id (first candidates))))
      (is (= 2 (:context-depth (first candidates))))
      (is (= 0.8 (:emotion-pressure (first candidates)))))
    (testing "selection returns the top-ranked candidate"
      (is (= (first candidates)
             (families/select-reversal-leaf world))))))

(deftest reversal-activation-candidates-detect-failed-goal-negative-emotion
  (let [[world root-id] (world-with-root)
        [world context-id] (cx/sprout world root-id)
        failed-goal-id :g-failed
        emotion-id :e-dread
        world (-> world
                  (cx/assert-fact context-id state-fact)
                  (cx/assert-fact context-id {:fact/type :goal
                                              :goal-id failed-goal-id
                                              :top-level-goal failed-goal-id
                                              :status :failed
                                              :activation-context context-id})
                  (cx/assert-fact context-id {:fact/type :emotion
                                              :emotion-id emotion-id
                                              :strength 0.82
                                              :valence :negative}))
        candidates (families/reversal-activation-candidates world)
        selected-leaf (families/select-reversal-leaf world)
        candidate (first candidates)]
    (is (= [{:old-context-id context-id
             :old-top-level-goal-id failed-goal-id
             :failed-goal-id failed-goal-id
             :context-depth 1
             :emotion-pressure 0.82
             :failure-count 1
             :selection-policy :emotion_then_depth
             :selection-reasons [:failed_leaf :negative_emotion :deep_context]
             :emotion-id emotion-id
             :emotion-strength 0.82
             :activation-policy :failed_goal_negative_emotion
             :activation-reasons [:failed_goal
                                  :negative_emotion
                                  :reversal_candidate]
             :situation-id :s1_seeing_through}]
           (mapv #(dissoc % :rule-provenance) candidates)))
    (is (= (expected-rule-provenance :goal-family/reversal-trigger
                                     :goal-family/reversal-activation
                                     :goal-family-trigger)
           (:rule-provenance candidate)))
    (is (= selected-leaf
           (select-keys candidate
                        [:old-context-id
                         :old-top-level-goal-id
                         :failed-goal-id
                         :context-depth
                         :emotion-pressure
                         :failure-count
                         :selection-policy
                         :selection-reasons])))))

(deftest reverse-undo-cause-candidates-rank-stored-counterfactuals
  (let [[world root-id] (world-with-root)
        [world old-context-id old-top-level-goal-id _ _]
        (seed-old-plan-context world root-id)
        high-priority-fact (failure-cause-fact
                            :fc-open-wall
                            old-top-level-goal-id
                            0.9
                            [counterfactual-fact state-fact])
        low-priority-fact (failure-cause-fact
                           :fc-stay-hidden
                           old-top-level-goal-id
                           0.2
                           [{:fact/type :counterfactual
                             :fact/id :stay_hidden}])
        world (-> world
                  (cx/assert-fact old-context-id high-priority-fact)
                  (cx/assert-fact old-context-id low-priority-fact))
        reversal-leaf {:old-context-id old-context-id
                       :old-top-level-goal-id old-top-level-goal-id
                       :failed-goal-id old-top-level-goal-id}
        candidates (families/reverse-undo-cause-candidates world reversal-leaf)
        selected-cause (families/reverse-undo-causes world reversal-leaf)]
    (testing "only matching failure-cause facts are returned"
      (is (= [:fc-open-wall :fc-stay-hidden]
             (mapv :cause-id candidates))))
    (testing "ranking prefers higher-priority causes with richer counterfactuals"
      (is (= 0.9 (:priority (first candidates))))
      (is (= [counterfactual-fact state-fact]
             (:counterfactual-facts (first candidates)))))
    (testing "selected counterfactuals expose the input-fact payload"
      (is (= [counterfactual-fact state-fact]
             (:input-facts selected-cause)))
      (is (= [:wall_was_open :s1_seeing_through]
             (:counterfactual-fact-ids selected-cause)))
      (is (= :fc-open-wall
             (:counterfactual-cause-id selected-cause)))
      (is (= :stored_priority
             (:counterfactual-policy selected-cause))))))

(deftest roving-activation-candidates-detect-failed-goal-negative-emotion
  (let [[world root-id] (world-with-root)
        [world context-id] (cx/sprout world root-id)
        failed-goal-id :g-failed
        emotion-id :e-shame
        world (-> world
                  (cx/assert-fact context-id {:fact/type :goal
                                              :goal-id failed-goal-id
                                              :top-level-goal failed-goal-id
                                              :status :failed
                                              :activation-context context-id})
                  (cx/assert-fact context-id {:fact/type :emotion
                                              :emotion-id emotion-id
                                              :strength 0.25
                                              :valence :negative})
                  (cx/assert-fact context-id {:fact/type :dependency
                                              :from-id emotion-id
                                              :to-id failed-goal-id}))
        candidates (families/roving-activation-candidates world)
        candidate (first candidates)]
    (is (= [{:context-id context-id
             :failed-goal-id failed-goal-id
             :emotion-id emotion-id
             :emotion-strength 0.25
             :selection-policy :failed_goal_negative_emotion
             :selection-reasons [:failed_goal
                                 :negative_emotion
                                 :dependency_link]}]
           (mapv #(dissoc % :rule-provenance) candidates)))
    (is (= (expected-rule-provenance :goal-family/roving-trigger
                                     :goal-family/roving-activation
                                     :goal-family-trigger)
           (:rule-provenance candidate)))
    (is (= candidate
           (families/select-roving-trigger world)))))

(deftest roving-plan-sprouts-and-runs-reminding-cascade
  (let [[world root-id] (world-with-root)
        [world pleasant-episode-id]
        (episodic/add-episode world {:rule :pleasant-memory})
        world (-> world
                  (episodic/store-episode pleasant-episode-id :calm {:reminding? true})
                  (episodic/store-episode pleasant-episode-id :sunlight {:reminding? true}))
        [world linked-episode-id]
        (episodic/add-episode world {:rule :follow-on-memory})
        world (-> world
                  (episodic/store-episode linked-episode-id :calm {:reminding? true})
                  (assoc :roving-episodes [pleasant-episode-id]))
        [world roving-goal-id]
        (goals/activate-top-level-goal
         world
         root-id
         {:goal-type :roving
          :planning-type :imaginary
          :strength 0.6
          :main-motiv :e-relief})
        context-id (get-in world [:goals roving-goal-id :next-cx])
        [world {:keys [sprouted-context-id episode-id reminded-episode-ids
                       active-indices selection-policy rule-provenance]}]
        (families/roving-plan world {:goal-id roving-goal-id
                                     :context-id context-id})]
    (testing "roving chooses the pleasant episode seed and sprouts once"
      (is (= pleasant-episode-id episode-id))
      (is (= context-id
             (get-in world [:contexts sprouted-context-id :parent-id])))
      (is (= 1.0
             (get-in world [:contexts sprouted-context-id :ordering]))))
    (testing "reminding cascade activates shared indices and linked episodes"
      (is (= [linked-episode-id] reminded-episode-ids))
      (is (= [pleasant-episode-id linked-episode-id]
             (:recent-episodes world)))
      (is (= #{:calm :sunlight}
             (set active-indices))))
    (testing "roving records a trivial success intention and mutation event"
      (is (some #(and (= :intends (:fact/type %))
                      (= roving-goal-id (:from-goal-id %))
                      (= :rtrue (:to-goal-id %)))
                (cx/visible-facts world sprouted-context-id)))
      (is (= :pleasant_episode_seed selection-policy))
      (is (= (expected-rule-provenance :goal-family/roving-plan-request
                                       :goal-family/roving-plan-dispatch
                                       :family-plan-request)
             rule-provenance))
      (is (= [{:family :roving
               :source-context context-id
               :target-context sprouted-context-id
               :seed-episode-id pleasant-episode-id
               :reminded-episode-ids [linked-episode-id]
               :active-indices [:calm :sunlight]}]
             (:mutation-events world))))))

(deftest rationalization-activation-candidates-detect-framed-failures
  (let [[world root-id] (world-with-root)
        [world context-id] (cx/sprout world root-id)
        failed-goal-id :g-failed
        emotion-id :e-dread
        frame-id :rf-zone-mercy
        frame-facts [guide-fact
                     {:fact/type :rationalization
                      :fact/id :zone_is_mercy}
                     {:fact/type :rationalization
                      :fact/id :delay_is_faith}]
        world (-> world
                  (cx/assert-fact context-id {:fact/type :goal
                                              :goal-id failed-goal-id
                                              :top-level-goal failed-goal-id
                                              :status :failed
                                              :activation-context context-id})
                  (cx/assert-fact context-id {:fact/type :emotion
                                              :emotion-id emotion-id
                                              :strength 0.82
                                              :valence :negative})
                  (cx/assert-fact context-id {:fact/type :dependency
                                              :from-id emotion-id
                                              :to-id failed-goal-id})
                  (cx/assert-fact context-id
                                  (rationalization-frame-fact frame-id
                                                              failed-goal-id
                                                              0.91
                                                              frame-facts)))
        candidates (families/rationalization-activation-candidates world)
        candidate (first candidates)]
    (is (= [{:context-id context-id
             :failed-goal-id failed-goal-id
             :emotion-id emotion-id
             :emotion-strength 0.82
             :frame-id frame-id
             :frame-priority 0.91
             :frame-count 1
             :situation-id :s5_the_guide
             :selection-policy :failed_goal_negative_emotion_rationalization_frame
             :selection-reasons [:failed_goal
                                 :negative_emotion
                                 :dependency_link
                                 :rationalization_frame]}]
           (mapv #(dissoc % :rule-provenance) candidates)))
    (is (= (expected-rule-provenance :goal-family/rationalization-trigger
                                     :goal-family/rationalization-activation
                                     :goal-family-trigger)
           (:rule-provenance candidate)))
    (is (= candidate
           (families/select-rationalization-trigger world)))))

(deftest activate-family-goals-dispatches-roving-via-trigger-facts
  (let [[world root-id] (world-with-root)
        world (assoc world :reality-context root-id)
        [world context-id] (cx/sprout world root-id)
        failed-goal-id :g-failed
        emotion-id :e-shame
        world (-> world
                  (cx/assert-fact context-id {:fact/type :goal
                                              :goal-id failed-goal-id
                                              :top-level-goal failed-goal-id
                                              :status :failed
                                              :activation-context context-id})
                  (cx/assert-fact context-id {:fact/type :emotion
                                              :emotion-id emotion-id
                                              :strength 0.25
                                              :valence :negative})
                  (cx/assert-fact context-id {:fact/type :dependency
                                              :from-id emotion-id
                                              :to-id failed-goal-id}))
        world (families/activate-family-goals world)
        roving-goal (->> (vals (:goals world))
                         (filter #(= :roving (:goal-type %)))
                         first)
        activation-event (->> (:activation-events world)
                              (filter #(= :roving (:goal-type %)))
                              first)]
    (is (some? roving-goal))
    (is (= context-id (:trigger-context-id roving-goal)))
    (is (= failed-goal-id (:trigger-failed-goal-id roving-goal)))
    (is (= emotion-id (:trigger-emotion-id roving-goal)))
    (is (= 0.25 (:trigger-emotion-strength roving-goal)))
    (is (= :failed_goal_negative_emotion
           (:activation-policy roving-goal)))
    (is (= [:failed_goal
            :negative_emotion
            :dependency_link]
           (:activation-reasons roving-goal)))
    (is (= (:id roving-goal)
           (:goal-id activation-event)))))

(deftest activate-family-goals-dispatches-rationalization-via-trigger-facts
  (let [[world root-id] (world-with-root)
        world (assoc world :reality-context root-id)
        [world context-id] (cx/sprout world root-id)
        failed-goal-id :g-failed
        emotion-id :e-dread
        frame-id :rf-zone-mercy
        frame-facts [guide-fact
                     {:fact/type :rationalization
                      :fact/id :zone_is_mercy}
                     {:fact/type :rationalization
                      :fact/id :delay_is_faith}]
        world (-> world
                  (cx/assert-fact context-id {:fact/type :goal
                                              :goal-id failed-goal-id
                                              :top-level-goal failed-goal-id
                                              :status :failed
                                              :activation-context context-id})
                  (cx/assert-fact context-id {:fact/type :emotion
                                              :emotion-id emotion-id
                                              :strength 0.82
                                              :valence :negative})
                  (cx/assert-fact context-id {:fact/type :dependency
                                              :from-id emotion-id
                                              :to-id failed-goal-id})
                  (cx/assert-fact context-id
                                  (rationalization-frame-fact frame-id
                                                              failed-goal-id
                                                              0.91
                                                              frame-facts)))
        world (families/activate-family-goals world)
        rationalization-goal (->> (vals (:goals world))
                                  (filter #(= :rationalization
                                              (:goal-type %)))
                                  first)
        activation-event (->> (:activation-events world)
                              (filter #(= :rationalization (:goal-type %)))
                              first)]
    (is (some? rationalization-goal))
    (is (= context-id (:trigger-context-id rationalization-goal)))
    (is (= failed-goal-id
           (:trigger-failed-goal-id rationalization-goal)))
    (is (= emotion-id (:trigger-emotion-id rationalization-goal)))
    (is (= 0.82 (:trigger-emotion-strength rationalization-goal)))
    (is (= frame-id (:trigger-frame-id rationalization-goal)))
    (is (= :failed_goal_negative_emotion_rationalization_frame
           (:activation-policy rationalization-goal)))
    (is (= [:failed_goal
            :negative_emotion
            :dependency_link
            :rationalization_frame]
           (:activation-reasons rationalization-goal)))
    (is (= (:id rationalization-goal)
           (:goal-id activation-event)))))

(deftest activation-rules-register-cleanly-with-first-honest-graph-edge
  (let [graph (rules/build-connection-graph (families/activation-rules))
        rules-by-id (:rules-by-id graph)]
    (is (= #{:goal-family/roving-trigger
             :goal-family/roving-activation
             :goal-family/rationalization-trigger
             :goal-family/rationalization-activation
             :goal-family/reversal-trigger
             :goal-family/reversal-activation}
           (set (keys rules-by-id))))
    (is (= [{:from-rule :goal-family/rationalization-trigger
             :to-rule :goal-family/rationalization-activation
             :from-projection {:fact/type :goal-family-trigger
                               :goal-type :rationalization
                               :trigger-context-id '?context-id
                               :failed-goal-id '?failed-goal-id
                               :emotion-id '?emotion-id
                               :emotion-strength '?emotion-strength
                               :frame-id '?frame-id
                               :frame-priority '?frame-priority
                               :frame-count '?frame-count
                               :situation-id '?situation-id
                               :selection-policy :failed_goal_negative_emotion_rationalization_frame
                               :selection-reasons [:failed_goal
                                                   :negative_emotion
                                                   :dependency_link
                                                   :rationalization_frame]}
             :to-projection {:fact/type :goal-family-trigger
                             :goal-type :rationalization
                             :trigger-context-id '?context-id
                             :failed-goal-id '?failed-goal-id
                             :emotion-id '?emotion-id
                             :emotion-strength '?emotion-strength
                             :frame-id '?frame-id
                             :frame-priority '?frame-priority
                             :frame-count '?frame-count
                             :situation-id '?situation-id
                             :selection-policy '?selection-policy
                             :selection-reasons '?selection-reasons}
             :bindings {}
             :shared-keys #{:goal-type
                            :trigger-context-id
                            :failed-goal-id
                            :emotion-id
                            :emotion-strength
                            :frame-id
                            :frame-priority
                            :frame-count
                            :situation-id
                            :selection-policy
                            :selection-reasons}
             :edge-kind :state-transition}
            {:from-rule :goal-family/reversal-trigger
             :to-rule :goal-family/reversal-activation
             :from-projection {:fact/type :goal-family-trigger
                               :goal-type :reversal
                               :old-context-id '?old-context-id
                               :old-top-level-goal-id '?old-top-level-goal-id
                               :failed-goal-id '?failed-goal-id
                               :context-depth '?context-depth
                               :emotion-pressure '?emotion-pressure
                               :failure-count '?failure-count
                               :selection-policy '?selection-policy
                               :selection-reasons '?selection-reasons
                               :emotion-id '?emotion-id
                               :emotion-strength '?emotion-strength
                               :situation-id '?situation-id}
             :to-projection {:fact/type :goal-family-trigger
                             :goal-type :reversal
                             :old-context-id '?old-context-id
                             :old-top-level-goal-id '?old-top-level-goal-id
                             :failed-goal-id '?failed-goal-id
                             :context-depth '?context-depth
                             :emotion-pressure '?emotion-pressure
                             :failure-count '?failure-count
                             :selection-policy '?selection-policy
                             :selection-reasons '?selection-reasons
                             :emotion-id '?emotion-id
                             :emotion-strength '?emotion-strength
                             :situation-id '?situation-id}
             :bindings {}
             :shared-keys #{:goal-type
                            :old-context-id
                            :old-top-level-goal-id
                            :failed-goal-id
                            :context-depth
                            :emotion-pressure
                            :failure-count
                            :selection-policy
                            :selection-reasons
                            :emotion-id
                            :emotion-strength
                            :situation-id}
             :edge-kind :state-transition}
            {:from-rule :goal-family/roving-trigger
             :to-rule :goal-family/roving-activation
             :from-projection {:fact/type :goal-family-trigger
                               :goal-type :roving
                               :trigger-context-id '?context-id
                               :failed-goal-id '?failed-goal-id
                               :emotion-id '?emotion-id
                               :emotion-strength '?emotion-strength
                               :selection-policy :failed_goal_negative_emotion
                               :selection-reasons [:failed_goal
                                                   :negative_emotion
                                                   :dependency_link]}
             :to-projection {:fact/type :goal-family-trigger
                             :goal-type :roving
                             :trigger-context-id '?context-id
                             :failed-goal-id '?failed-goal-id
                             :emotion-id '?emotion-id
                             :emotion-strength '?emotion-strength
                             :selection-policy '?selection-policy
                             :selection-reasons '?selection-reasons}
             :bindings {}
             :shared-keys #{:goal-type
                            :trigger-context-id
                            :failed-goal-id
                            :emotion-id
                            :emotion-strength
                            :selection-policy
                            :selection-reasons}
             :edge-kind :state-transition}]
           (:edges graph)))
    (is (= 3
           (count (get-in rules-by-id
                          [:goal-family/roving-trigger :graph-cache :in-edge-bases]))))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/roving-activation :graph-cache :in-edge-bases]))))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/roving-trigger :graph-cache :out-edge-bases]))))
    (is (= 0
           (count (get-in rules-by-id
                          [:goal-family/roving-activation :graph-cache :out-edge-bases]))))
    (is (= 4
           (count (get-in rules-by-id
                          [:goal-family/rationalization-trigger :graph-cache :in-edge-bases]))))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/rationalization-trigger :graph-cache :out-edge-bases]))))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/rationalization-activation :graph-cache :in-edge-bases]))))
    (is (= 0
           (count (get-in rules-by-id
                          [:goal-family/rationalization-activation :graph-cache :out-edge-bases]))))
    (is (= 2
           (count (get-in rules-by-id
                          [:goal-family/reversal-trigger :graph-cache :in-edge-bases]))))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/reversal-trigger :graph-cache :out-edge-bases]))))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/reversal-activation :graph-cache :in-edge-bases]))))
    (is (= 0
           (count (get-in rules-by-id
                          [:goal-family/reversal-activation :graph-cache :out-edge-bases]))))))

(deftest planning-rules-register-cleanly-with-family-plan-edges
  (let [graph (rules/build-connection-graph (families/planning-rules))
        rules-by-id (:rules-by-id graph)]
    (is (= #{:goal-family/roving-plan-request
             :goal-family/roving-plan-dispatch
             :goal-family/rationalization-plan-request
             :goal-family/rationalization-plan-dispatch
             :goal-family/reversal-plan-request
             :goal-family/reversal-plan-dispatch}
           (set (keys rules-by-id))))
    (is (= [{:from-rule :goal-family/rationalization-plan-request
             :to-rule :goal-family/rationalization-plan-dispatch
             :from-projection {:fact/type :family-plan-request
                               :goal-type :rationalization
                               :goal-id '?goal-id
                               :context-id '?context-id
                               :trigger-context-id '?trigger-context-id
                               :failed-goal-id '?failed-goal-id
                               :trigger-emotion-id '?trigger-emotion-id
                               :trigger-emotion-strength '?trigger-emotion-strength
                               :frame-id '?frame-id
                               :ordering '?ordering
                               :selection-policy :stored_rationalization_frame}
             :to-projection {:fact/type :family-plan-request
                             :goal-type :rationalization
                             :goal-id '?goal-id
                             :context-id '?context-id
                             :trigger-context-id '?trigger-context-id
                             :failed-goal-id '?failed-goal-id
                             :trigger-emotion-id '?trigger-emotion-id
                             :trigger-emotion-strength '?trigger-emotion-strength
                             :frame-id '?frame-id
                             :ordering '?ordering
                             :selection-policy '?selection-policy}
             :bindings {}
             :shared-keys #{:goal-type
                            :goal-id
                            :context-id
                            :trigger-context-id
                            :failed-goal-id
                            :trigger-emotion-id
                            :trigger-emotion-strength
                            :frame-id
                            :ordering
                            :selection-policy}
             :edge-kind :state-transition}
            {:from-rule :goal-family/reversal-plan-request
             :to-rule :goal-family/reversal-plan-dispatch
             :from-projection {:fact/type :family-plan-request
                               :goal-type :reversal
                               :old-context-id '?old-context-id
                               :old-top-level-goal-id '?old-top-level-goal-id
                               :new-context-id '?new-context-id
                               :new-top-level-goal-id '?new-top-level-goal-id
                               :selection-policy :intends_weak_leaf}
             :to-projection {:fact/type :family-plan-request
                             :goal-type :reversal
                             :old-context-id '?old-context-id
                             :old-top-level-goal-id '?old-top-level-goal-id
                             :new-context-id '?new-context-id
                             :new-top-level-goal-id '?new-top-level-goal-id
                             :selection-policy '?selection-policy}
             :bindings {}
             :shared-keys #{:goal-type
                            :old-context-id
                            :old-top-level-goal-id
                            :new-context-id
                            :new-top-level-goal-id
                            :selection-policy}
             :edge-kind :state-transition}
            {:from-rule :goal-family/roving-plan-request
             :to-rule :goal-family/roving-plan-dispatch
             :from-projection {:fact/type :family-plan-request
                               :goal-type :roving
                               :goal-id '?goal-id
                               :context-id '?context-id
                               :episode-id '?episode-id
                               :ordering '?ordering
                               :selection-policy :pleasant_episode_seed}
             :to-projection {:fact/type :family-plan-request
                             :goal-type :roving
                             :goal-id '?goal-id
                             :context-id '?context-id
                             :episode-id '?episode-id
                             :ordering '?ordering
                             :selection-policy '?selection-policy}
             :bindings {}
             :shared-keys #{:goal-type
                            :goal-id
                            :context-id
                            :episode-id
                            :ordering
                            :selection-policy}
             :edge-kind :state-transition}]
           (:edges graph)))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/roving-plan-request :graph-cache :out-edge-bases]))))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/roving-plan-dispatch :graph-cache :in-edge-bases]))))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/rationalization-plan-request :graph-cache :out-edge-bases]))))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/rationalization-plan-dispatch :graph-cache :in-edge-bases]))))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/reversal-plan-request :graph-cache :out-edge-bases]))))
    (is (= 1
           (count (get-in rules-by-id
                          [:goal-family/reversal-plan-dispatch :graph-cache :in-edge-bases]))))))

(deftest planning-rule-intervention-removes-rationalization-dispatch-path
  (let [graph (rules/build-connection-graph (families/planning-rules))
        delta (rules/intervention-delta
               graph
               :goal-family/rationalization-plan-request
               [:goal-family/rationalization-plan-dispatch]
               {:max-depth 2})]
    (is (= [:goal-family/rationalization-plan-dispatch]
           (:removed-rule-ids delta)))
    (is (= [[:goal-family/rationalization-plan-request
             :goal-family/rationalization-plan-dispatch]]
           (mapv :rule-path (:before-paths delta))))
    (is (= []
           (mapv :rule-path (:after-paths delta))))
    (is (= [[:goal-family/rationalization-plan-request
             :goal-family/rationalization-plan-dispatch]]
           (mapv :rule-path (:removed-paths delta))))
    (is (= []
           (:preserved-paths delta)))))

(deftest planning-rule-graph-has-no-multi-hop-bridges-yet
  (let [graph (rules/build-connection-graph (families/planning-rules))]
    (is (= []
           (rules/bridge-paths graph
                               :goal-family/roving-plan-request
                               :goal-family/roving-plan-dispatch)))
    (is (= []
           (rules/bridge-paths graph
                               :goal-family/roving-plan-request
                               :goal-family/rationalization-plan-dispatch)))
    (is (= []
           (rules/bridge-paths graph
                               :goal-family/rationalization-plan-request
                               :goal-family/reversal-plan-dispatch)))))

(deftest family-rule-graph-exposes-rationalization-to-roving-bridge
  (let [graph (rules/build-connection-graph (families/family-rules))
        bridges (rules/bridge-paths graph
                                    :goal-family/rationalization-plan-dispatch
                                    :goal-family/roving-activation)]
    (is (= [[:goal-family/rationalization-plan-dispatch
             :goal-family/rationalization-afterglow-to-roving
             :goal-family/roving-activation]]
           (mapv :rule-path bridges)))
    (is (= {:rule-path [:goal-family/rationalization-plan-dispatch
                        :goal-family/rationalization-afterglow-to-roving
                        :goal-family/roving-activation]
            :fact-types [:family-affect-state
                         :goal-family-trigger]
            :edge-kinds [:state-transition
                         :state-transition]}
           (select-keys (first bridges)
                        [:rule-path
                         :fact-types
                         :edge-kinds])))))

(deftest rationalization-plan-sprouts-and-asserts-reframe-facts
  (let [[world root-id] (world-with-root)
        [world trigger-context-id] (cx/sprout world root-id)
        failed-goal-id :g-failed
        emotion-id :e-dread
        frame-id :rf-zone-mercy
        frame-facts [guide-fact
                     {:fact/type :rationalization
                      :fact/id :zone_is_mercy}
                     {:fact/type :rationalization
                      :fact/id :delay_is_faith}]
        world (-> world
                  (cx/assert-fact trigger-context-id {:fact/type :goal
                                                      :goal-id failed-goal-id
                                                      :top-level-goal failed-goal-id
                                                      :status :failed
                                                      :activation-context trigger-context-id})
                  (cx/assert-fact trigger-context-id {:fact/type :emotion
                                                      :emotion-id emotion-id
                                                      :strength 0.82
                                                      :valence :negative
                                                      :affect :dread})
                  (cx/assert-fact trigger-context-id {:fact/type :dependency
                                                      :from-id emotion-id
                                                      :to-id failed-goal-id})
                  (cx/assert-fact trigger-context-id
                                  (rationalization-frame-fact frame-id
                                                              failed-goal-id
                                                              0.91
                                                              frame-facts)))
        [world rationalization-goal-id]
        (goals/activate-top-level-goal
         world
         root-id
         {:goal-type :rationalization
          :planning-type :imaginary
          :strength 0.82
          :main-motiv :e-dread})
        context-id (get-in world [:goals rationalization-goal-id :next-cx])
        expected-trigger-after (- 0.82 (* 0.82 0.35 0.91))
        expected-hope-strength (* 0.82 0.35 0.91)
        [world {:keys [sprouted-context-id frame-id frame-goal-id
                       reframe-fact-ids selection-policy rule-provenance
                       diversion-policy trigger-emotion-id
                       trigger-emotion-before trigger-emotion-after
                       hope-emotion-id hope-strength hope-situation-id
                       affect-state-fact
                       emotion-shifts emotional-state]}]
        (families/rationalization-plan world
                                       {:goal-id rationalization-goal-id
                                        :context-id context-id
                                        :trigger-context-id trigger-context-id
                                        :failed-goal-id failed-goal-id})]
    (testing "rationalization chooses the stored frame and sprouts once"
      (is (= context-id
             (get-in world [:contexts sprouted-context-id :parent-id])))
      (is (= 1.0
             (get-in world [:contexts sprouted-context-id :ordering])))
      (is (= :rf-zone-mercy frame-id))
      (is (= failed-goal-id frame-goal-id)))
    (testing "the reframe facts are asserted into the branch"
      (is (every? #(cx/fact-true? world sprouted-context-id %)
                  frame-facts))
      (is (= [:s5_the_guide :zone_is_mercy :delay_is_faith]
             reframe-fact-ids)))
    (testing "rationalization diverts the trigger emotion into hopeful reframe energy"
      (is (= :divert_emot_to_tlg_bridge diversion-policy))
      (is (= emotion-id trigger-emotion-id))
      (is (= 0.82 trigger-emotion-before))
      (is (= expected-trigger-after trigger-emotion-after))
      (is (= :rf-zone-mercy-hope hope-emotion-id))
      (is (= expected-hope-strength hope-strength))
      (is (= :s5_the_guide hope-situation-id))
      (is (cx/fact-true? world
                         sprouted-context-id
                         {:fact/type :emotion
                          :emotion-id emotion-id
                          :strength expected-trigger-after
                          :valence :negative
                          :affect :dread}))
      (is (cx/fact-true? world
                         sprouted-context-id
                         {:fact/type :emotion
                          :emotion-id :rf-zone-mercy-hope
                          :strength expected-hope-strength
                          :valence :positive
                          :affect :hope
                          :goal-id rationalization-goal-id
                          :source-emotion-id emotion-id
                          :frame-id :rf-zone-mercy
                          :situation-id :s5_the_guide}))
      (is (= {:fact/type :family-affect-state
              :source-family :rationalization
              :goal-id rationalization-goal-id
              :context-id context-id
              :branch-context-id sprouted-context-id
              :failed-goal-id failed-goal-id
              :trigger-emotion-id emotion-id
              :trigger-emotion-strength expected-trigger-after
              :frame-id :rf-zone-mercy
              :affect :hope
              :transition :reappraised}
             (dissoc affect-state-fact :rule-provenance)))
      (is (cx/fact-true? world
                         sprouted-context-id
                         affect-state-fact))
      (is (= [{:emotion-id emotion-id
               :from-strength 0.82
               :to-strength expected-trigger-after
               :delta (- expected-trigger-after 0.82)
               :valence :negative
               :affect :dread
               :situation-id nil
               :context-id sprouted-context-id
               :role :trigger}
              {:emotion-id :rf-zone-mercy-hope
               :from-strength 0.0
               :to-strength expected-hope-strength
               :delta expected-hope-strength
               :valence :positive
               :affect :hope
               :situation-id :s5_the_guide
               :context-id sprouted-context-id
               :role :reframe}]
             emotion-shifts))
      (is (= [{:emotion-id emotion-id
               :strength expected-trigger-after
               :valence :negative
               :affect :dread
               :situation-id nil
               :context-id sprouted-context-id
               :role :trigger}
              {:emotion-id :rf-zone-mercy-hope
               :strength expected-hope-strength
               :valence :positive
               :affect :hope
               :situation-id :s5_the_guide
               :context-id sprouted-context-id
               :role :reframe}]
             emotional-state)))
    (testing "the plan records a trivial success intention and mutation event"
      (is (some #(and (= :intends (:fact/type %))
                      (= rationalization-goal-id (:from-goal-id %))
                      (= :rtrue (:to-goal-id %)))
                (cx/visible-facts world sprouted-context-id)))
      (is (= :stored_rationalization_frame selection-policy))
      (is (= (expected-rule-provenance :goal-family/rationalization-plan-request
                                       :goal-family/rationalization-plan-dispatch
                                       :family-plan-request)
             rule-provenance))
      (is (= [{:family :rationalization
               :source-context context-id
               :trigger-context trigger-context-id
               :target-context sprouted-context-id
               :failed-goal-id failed-goal-id
               :frame-id :rf-zone-mercy
               :reframe-facts frame-facts
               :emotion-diversion {:diversion-policy :divert_emot_to_tlg_bridge
                                   :trigger-emotion-id emotion-id
                                   :trigger-emotion-before 0.82
                                   :trigger-emotion-after expected-trigger-after
                                   :hope-emotion-id :rf-zone-mercy-hope
                                   :hope-strength expected-hope-strength
                                   :hope-situation-id :s5_the_guide}}]
             (:mutation-events world))))))

(deftest activate-family-goals-can-dispatch-roving-from-rationalization-afterglow
  (let [[world root-id] (world-with-root)
        world (assoc world :reality-context root-id)
        [world trigger-context-id] (cx/sprout world root-id)
        failed-goal-id :g-failed
        emotion-id :e-dread
        frame-id :rf-zone-mercy
        frame-facts [guide-fact
                     {:fact/type :rationalization
                      :fact/id :zone_is_mercy}
                     {:fact/type :rationalization
                      :fact/id :delay_is_faith}]
        world (-> world
                  (cx/assert-fact trigger-context-id {:fact/type :goal
                                                      :goal-id failed-goal-id
                                                      :top-level-goal failed-goal-id
                                                      :status :failed
                                                      :activation-context trigger-context-id})
                  (cx/assert-fact trigger-context-id {:fact/type :emotion
                                                      :emotion-id emotion-id
                                                      :strength 0.82
                                                      :valence :negative
                                                      :affect :dread})
                  (cx/assert-fact trigger-context-id {:fact/type :dependency
                                                      :from-id emotion-id
                                                      :to-id failed-goal-id})
                  (cx/assert-fact trigger-context-id
                                  (rationalization-frame-fact frame-id
                                                              failed-goal-id
                                                              0.91
                                                              frame-facts)))
        [world rationalization-goal-id]
        (goals/activate-top-level-goal
         world
         root-id
         {:goal-type :rationalization
          :planning-type :imaginary
          :strength 0.82
          :main-motiv emotion-id
          :trigger-context-id trigger-context-id
          :trigger-failed-goal-id failed-goal-id
          :trigger-emotion-id emotion-id
          :trigger-emotion-strength 0.82
          :trigger-frame-id frame-id})
        context-id (get-in world [:goals rationalization-goal-id :next-cx])
        [world rationalization-result]
        (families/rationalization-plan world
                                       {:goal-id rationalization-goal-id
                                        :context-id context-id
                                        :trigger-context-id trigger-context-id
                                        :failed-goal-id failed-goal-id})
        world (families/activate-family-goals world)
        roving-goal (->> (vals (:goals world))
                         (filter #(= :roving (:goal-type %)))
                         first)
        activation-event (->> (:activation-events world)
                              (filter #(= :roving (:goal-type %)))
                              first)
        expected-rule-provenance
        {:rule-path [:goal-family/rationalization-plan-request
                     :goal-family/rationalization-plan-dispatch
                     :goal-family/rationalization-afterglow-to-roving
                     :goal-family/roving-activation]
         :edge-path [{:from-rule :goal-family/rationalization-plan-request
                      :to-rule :goal-family/rationalization-plan-dispatch
                      :fact-type :family-plan-request
                      :edge-kind :state-transition}
                     {:from-rule :goal-family/rationalization-plan-dispatch
                      :to-rule :goal-family/rationalization-afterglow-to-roving
                      :fact-type :family-affect-state
                      :edge-kind :state-transition}
                     {:from-rule :goal-family/rationalization-afterglow-to-roving
                      :to-rule :goal-family/roving-activation
                      :fact-type :goal-family-trigger
                      :edge-kind :state-transition}]}]
    (is (some? (:affect-state-fact rationalization-result)))
    (is (some? roving-goal))
    (is (= context-id (:trigger-context-id roving-goal)))
    (is (= failed-goal-id (:trigger-failed-goal-id roving-goal)))
    (is (= emotion-id (:trigger-emotion-id roving-goal)))
    (is (= (:trigger-emotion-after rationalization-result)
           (:trigger-emotion-strength roving-goal)))
    (is (= :rationalization_afterglow
           (:activation-policy roving-goal)))
    (is (= [:rationalization_afterglow
            :hope_reframe]
           (:activation-reasons roving-goal)))
    (is (= expected-rule-provenance
           (:rule-provenance activation-event)))
    (is (= (:id roving-goal)
           (:goal-id activation-event)))))

(deftest removing-cross-family-bridge-stops-roving-after-rationalization
  (let [[world root-id] (world-with-root)
        world (assoc world :reality-context root-id)
        [world trigger-context-id] (cx/sprout world root-id)
        failed-goal-id :g-failed
        emotion-id :e-dread
        frame-id :rf-zone-mercy
        frame-facts [guide-fact
                     {:fact/type :rationalization
                      :fact/id :zone_is_mercy}
                     {:fact/type :rationalization
                      :fact/id :delay_is_faith}]
        world (-> world
                  (cx/assert-fact trigger-context-id {:fact/type :goal
                                                      :goal-id failed-goal-id
                                                      :top-level-goal failed-goal-id
                                                      :status :failed
                                                      :activation-context trigger-context-id})
                  (cx/assert-fact trigger-context-id {:fact/type :emotion
                                                      :emotion-id emotion-id
                                                      :strength 0.82
                                                      :valence :negative
                                                      :affect :dread})
                  (cx/assert-fact trigger-context-id {:fact/type :dependency
                                                      :from-id emotion-id
                                                      :to-id failed-goal-id})
                  (cx/assert-fact trigger-context-id
                                  (rationalization-frame-fact frame-id
                                                              failed-goal-id
                                                              0.91
                                                              frame-facts)))
        [world rationalization-goal-id]
        (goals/activate-top-level-goal
         world
         root-id
         {:goal-type :rationalization
          :planning-type :imaginary
          :strength 0.82
          :main-motiv emotion-id
          :trigger-context-id trigger-context-id
          :trigger-failed-goal-id failed-goal-id
          :trigger-emotion-id emotion-id
          :trigger-emotion-strength 0.82
          :trigger-frame-id frame-id})
        context-id (get-in world [:goals rationalization-goal-id :next-cx])
        [world rationalization-result]
        (families/rationalization-plan world
                                       {:goal-id rationalization-goal-id
                                        :context-id context-id
                                        :trigger-context-id trigger-context-id
                                        :failed-goal-id failed-goal-id})
        world (with-redefs [families/cross-family-rules (constantly [])]
                (families/activate-family-goals world))
        roving-goals (->> (vals (:goals world))
                          (filter #(= :roving (:goal-type %)))
                          vec)
        roving-events (->> (:activation-events world)
                           (filter #(= :roving (:goal-type %)))
                           vec)]
    (is (some? (:affect-state-fact rationalization-result)))
    (is (cx/fact-true? world
                       (:sprouted-context-id rationalization-result)
                       (:affect-state-fact rationalization-result)))
    (is (= []
           roving-goals))
    (is (= []
           roving-events))))

(deftest reverse-leafs-follow-intends-and-retract-weak-leaf-objectives
  (let [[world root-id] (world-with-root)
        [world old-context-id] (cx/sprout world root-id)
        old-top-level-goal-id :g-old
        weak-leaf-a-id :g-old-leaf-a
        weak-leaf-b-id :g-old-leaf-b
        strong-leaf-id :g-old-strong
        facts [leaf-objective-a
               leaf-objective-b
               mission-fact
               (goal-fact old-top-level-goal-id
                          old-top-level-goal-id
                          :failed
                          old-context-id)
               (goal-fact weak-leaf-a-id
                          old-top-level-goal-id
                          :runable
                          old-context-id
                          {:strength 0.3
                           :objective-fact leaf-objective-a})
               (goal-fact weak-leaf-b-id
                          old-top-level-goal-id
                          :runable
                          old-context-id
                          {:strength 0.4
                           :objective-fact leaf-objective-b})
               (goal-fact strong-leaf-id
                          old-top-level-goal-id
                          :runable
                          old-context-id
                          {:strength 0.8
                           :objective-fact mission-fact})
               (intends-fact old-top-level-goal-id weak-leaf-a-id old-top-level-goal-id)
               (intends-fact old-top-level-goal-id weak-leaf-b-id old-top-level-goal-id)
               (intends-fact old-top-level-goal-id strong-leaf-id old-top-level-goal-id)]
        world (reduce (fn [current-world fact]
                        (cx/assert-fact current-world old-context-id fact))
                      world
                      facts)
        [world new-top-level-goal-id]
        (goals/activate-top-level-goal
         world
         root-id
         {:goal-type :reversal
          :planning-type :imaginary
          :strength 0.9
          :main-motiv :e-new})
        reversal-target {:old-context-id old-context-id
                         :old-top-level-goal-id old-top-level-goal-id
                         :new-context-id (get-in world [:goals new-top-level-goal-id :next-cx])
                         :new-top-level-goal-id new-top-level-goal-id
                         :input-facts [counterfactual-fact]}
        leaf-branches (families/reverse-leaf-branches world reversal-target)
        [world branch-results] (families/reverse-leafs world reversal-target)]
    (testing "only weak leaf goals under the failed top-level goal qualify"
      (is (= [weak-leaf-a-id weak-leaf-b-id]
             (mapv :leaf-goal-id leaf-branches)))
      (is (= [weak-leaf-a-id weak-leaf-b-id]
             (mapv :leaf-goal-id branch-results))))
    (testing "ordering is inverse strength"
      (is (= [(/ 1.0 0.3) (/ 1.0 0.4)]
             (mapv :ordering leaf-branches))))
    (testing "each sprouted branch retracts its own leaf objective and keeps counterfactual input"
      (is (= 2 (count branch-results)))
      (is (= (expected-rule-provenance :goal-family/reversal-plan-request
                                       :goal-family/reversal-plan-dispatch
                                       :family-plan-request)
             (:rule-provenance (first branch-results))))
      (doseq [{:keys [sprouted-context-id objective-facts retracted-fact-ids]} branch-results]
        (is (cx/fact-true? world sprouted-context-id counterfactual-fact))
        (is (= (mapv :fact/id objective-facts) retracted-fact-ids))
        (doseq [objective-fact objective-facts]
          (is (not (cx/fact-true? world sprouted-context-id objective-fact))))))
    (testing "mutation events capture the retracted weak assumptions"
      (is (= 2 (count (:mutation-events world))))
      (is (= [[leaf-objective-a] [leaf-objective-b]]
             (mapv :retracted-facts (:mutation-events world)))))))
