(ns daydreamer.context-test
  (:require [clojure.test :refer [deftest is testing]]
            [daydreamer.context :as cx]))

(def betrayal-fact
  {:fact/type :situation
   :fact/id :s1})

(def rehearsal-fact
  {:fact/type :situation
   :fact/id :s4})

(def anger-fact
  {:fact/type :emotion
   :fact/id :anger})

(defn world-with-root
  ([] (world-with-root []))
  ([facts]
   (let [root (cx/create-context)
         root-id (:id root)
         base-world {:contexts {root-id root}
                     :goals {}
                     :episodes {}
                     :emotions {}
                     :mode :daydreaming
                     :cycle 0
                     :trace []
                     :id-counter 1}
         world (reduce
                (fn [current-world fact]
                  (cx/assert-fact current-world root-id fact))
                base-world
                facts)]
     [world root-id])))

(deftest create-context-test
  (is (= {:id :cx-1
          :parent-id nil
          :children #{}
          :ancestors []
          :all-obs #{}
          :add-obs #{}
          :remove-obs #{}
          :pseudo-sprout? false
          :mutations-tried? false
          :timeout 10
          :ordering 0
          :touched-facts #{}}
         (cx/create-context))))

(deftest sprout-inherits-parent-state
  (let [[world root-id] (world-with-root [betrayal-fact anger-fact])
        [world child-id] (cx/sprout world root-id)
        root (get-in world [:contexts root-id])
        child (get-in world [:contexts child-id])]
    (testing "sprouting copies visible content and ancestry"
      (is (= root-id (:parent-id child)))
      (is (= [root-id] (:ancestors child)))
      (is (= #{betrayal-fact anger-fact} (cx/visible-facts world child-id)))
      (is (= #{betrayal-fact anger-fact} (:touched-facts child)))
      (is (= 9 (:timeout child)))
      (is (= false (:pseudo-sprout? child))))
    (testing "parent tracks the new child"
      (is (= #{child-id} (:children root))))))

(deftest pseudo-sprout-reparents-without-inheriting-content
  (let [[world root-id] (world-with-root [betrayal-fact])
        alternate-root (assoc (cx/create-context) :id :cx-99)
        world (assoc world :id-counter 99)
        world (assoc-in world [:contexts :cx-99] alternate-root)
        world (cx/assert-fact world :cx-99 rehearsal-fact)
        [world grandchild-id] (cx/sprout world :cx-99)
        world (cx/pseudo-sprout world :cx-99 root-id)]
    (testing "pseudo-sprout attaches the child to the new parent"
      (is (= root-id (:parent-id (get-in world [:contexts :cx-99]))))
      (is (= #{:cx-99} (cx/children world root-id)))
      (is (= [root-id] (cx/ancestors world :cx-99))))
    (testing "pseudo-sprouts do not inherit the parent's content"
      (is (= #{rehearsal-fact} (cx/visible-facts world :cx-99)))
      (is (not (cx/fact-true? world :cx-99 betrayal-fact))))
    (testing "existing descendants are rebased onto the new ancestry"
      (is (= [:cx-99 root-id] (cx/ancestors world grandchild-id)))
      (is (= true (:pseudo-sprout? (get-in world [:contexts :cx-99]))))
      (is (= true (:pseudo-sprout? (get-in world [:contexts grandchild-id])))))))

(deftest assert-and-retract-roundtrip
  (let [[world root-id] (world-with-root)
        roundtrip-world (-> world
                            (cx/assert-fact root-id rehearsal-fact)
                            (cx/retract-fact root-id rehearsal-fact))]
    (is (= world roundtrip-world))))

(deftest retracting-inherited-fact-hides-it-locally
  (let [[world root-id] (world-with-root [betrayal-fact])
        [world child-id] (cx/sprout world root-id)
        world (cx/retract-fact world child-id betrayal-fact)]
    (is (= #{} (cx/visible-facts world child-id)))
    (is (= #{betrayal-fact} (get-in world [:contexts child-id :remove-obs])))
    (is (= #{betrayal-fact} (cx/visible-facts world root-id)))))

(deftest navigation-helpers-return-tree-shape
  (let [[world root-id] (world-with-root)
        [world child-a] (cx/sprout world root-id)
        [world child-b] (cx/sprout world root-id)
        [world grandchild] (cx/sprout world child-a)]
    (is (= [root-id] (cx/ancestors world child-a)))
    (is (= #{child-a child-b} (cx/children world root-id)))
    (is (= #{grandchild child-b} (cx/leaf-descendants world root-id)))
    (is (= #{child-b} (cx/leaf-descendants world child-b)))))

(deftest copy-context-sprouts-and-reasserts-source-facts
  (let [[world root-id] (world-with-root [betrayal-fact])
        alternate-root (assoc (cx/create-context) :id :cx-99)
        world (assoc world :id-counter 99)
        world (assoc-in world [:contexts :cx-99] alternate-root)
        world (cx/assert-fact world :cx-99 rehearsal-fact)
        [world copied-id] (cx/copy-context world :cx-99 root-id)]
    (is (= root-id (:parent-id (get-in world [:contexts copied-id]))))
    (is (= #{betrayal-fact rehearsal-fact}
           (cx/visible-facts world copied-id)))
    (is (= #{rehearsal-fact}
           (get-in world [:contexts copied-id :add-obs])))))

(deftest copy-context-with-nil-parent-creates-a-root-clone
  (let [[world root-id] (world-with-root [betrayal-fact anger-fact])
        [world copied-id] (cx/copy-context world root-id nil)
        copied (get-in world [:contexts copied-id])]
    (is (= nil (:parent-id copied)))
    (is (= [] (:ancestors copied)))
    (is (= #{betrayal-fact anger-fact}
           (cx/visible-facts world copied-id)))
    (is (= #{betrayal-fact anger-fact}
           (get-in world [:contexts copied-id :add-obs])))))
