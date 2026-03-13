(ns daydreamer.goal-families-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.context :as cx]
            [daydreamer.goal-families :as families]
            [daydreamer.goals :as goals]))

(def state-fact
  {:fact/type :situation
   :fact/id :s1_seeing_through})

(def mission-fact
  {:fact/type :situation
   :fact/id :s2_the_mission})

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

(defn world-with-root
  []
  (let [root (cx/create-context)
        root-id (:id root)]
    [{:contexts {root-id root}
      :goals {}
      :episodes {}
      :emotions {}
      :mode :daydreaming
      :cycle 0
      :trace []
      :mutation-events []
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
      (doseq [{:keys [sprouted-context-id objective-facts retracted-fact-ids]} branch-results]
        (is (cx/fact-true? world sprouted-context-id counterfactual-fact))
        (is (= (mapv :fact/id objective-facts) retracted-fact-ids))
        (doseq [objective-fact objective-facts]
          (is (not (cx/fact-true? world sprouted-context-id objective-fact))))))
    (testing "mutation events capture the retracted weak assumptions"
      (is (= 2 (count (:mutation-events world))))
      (is (= [[leaf-objective-a] [leaf-objective-b]]
             (mapv :retracted-facts (:mutation-events world)))))))
