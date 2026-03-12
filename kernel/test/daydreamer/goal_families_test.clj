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
  [goal-id top-level-goal status activation-context]
  {:fact/type :goal
   :goal-id goal-id
   :top-level-goal top-level-goal
   :status status
   :activation-context activation-context})

(defn intends-fact
  [from-goal-id to-goal-id top-level-goal]
  {:fact/type :intends
   :from-goal-id from-goal-id
   :to-goal-id to-goal-id
   :top-level-goal top-level-goal})

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
             :input-facts [counterfactual-fact]}]
           (:mutation-events world)))))
