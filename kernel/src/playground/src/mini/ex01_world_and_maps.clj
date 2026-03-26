(ns mini.ex01-world-and-maps
  (:require [clojure.walk :as walk])
  (:require [clojure.pprint :as pprint]))

;; =============================================================
;; Exercise 1: The World Map
;; =============================================================
;;
;; The kernel's entire state lives in one Clojure map called `world`.
;; Every function takes world as its first argument and returns an
;; updated world (or [world result]). This makes the whole system
;; a chain of pure transformations on one data structure.
;;
;; Evaluate each form with Calva (Ctrl+Enter or Cmd+Enter on the
;; form) and read the result in the output panel.

;; --- The shape of a world ---
;; This is what runner/initial-world produces. Everything the kernel
;; knows lives here.

(def my-world
  {:contexts   {:cx-1 {:id :cx-1
                       :parent-id nil
                       :children #{}
                       :all-obs #{}
                       :add-obs #{}
                       :remove-obs #{}}}
   :goals      {}
   :episodes   {}
   :episode-index {}
   :emotions   {}
   :needs      {}
   :mode       :daydreaming      ;; :daydreaming | :performance | :suspended
   :cycle      0
   :trace      []
   :recent-indices []
   :recent-episodes []
   :id-counter 1
   :reality-context :cx-1})

;; Try these: what do you get?

(:mode my-world)
;; => :daydreaming

(:cycle my-world)
;; => 0

(keys my-world)
;; => all the top-level keys

(get-in my-world [:contexts :cx-1 :id])
;; => :cx-1

;; --- Maps are the only data structure ---
;; No classes, no objects, no records. Just maps, vectors, sets,
;; and keywords. The kernel NEVER uses atoms, refs, or mutable state
;; in core logic.

;; Keywords are used as both keys AND as identifiers:
:cx-1          ;; a context id
:daydreaming   ;; a mode
:rationalization  ;; a family name
:ep-3          ;; an episode id

;; Namespaced keywords carry type information:
:fact/type     ;; the "type" key in the "fact" namespace
:goal-family/rationalization-trigger  ;; a rule id


;; --- Threading: how state flows ---
;; The kernel chains transformations with ->

(-> my-world
    (assoc :cycle 1)
    (assoc :mode :performance)
    (assoc-in [:emotions :e-1] {:strength 0.8 :valence :negative}))
;; Returns a NEW world with cycle=1, mode=performance, and one emotion.
;; The original my-world is unchanged. Try it:

my-world
;; => still cycle 0, mode :daydreaming, no emotions

;; --- update and update-in: transforming values ---
;; Instead of replacing a value, transform it:

(update my-world :cycle inc)
;; => {:cycle 1, ...}

(update my-world :cycle + 5)
;; => {:cycle 5, ...}

;; Nested update:
(update-in my-world [:contexts :cx-1 :children] conj :cx-2)
;; => the :cx-1 context now has :cx-2 in its children set

;; --- The [world result] pattern ---
;; Many kernel functions return [world something] so you can
;; thread AND capture a result:

(defn add-emotion
  "Add an emotion and return [world emotion-id]."
  [world emotion-id strength valence]
  (let [emotion {:strength strength :valence valence}
        new-world (assoc-in world [:emotions emotion-id] emotion)]
    [new-world emotion-id]))

(let [[w eid] (add-emotion my-world :e-dread 0.74 :negative)]
  (println "Added emotion:" eid)
  (println "Emotion map:" (get-in w [:emotions :e-dread]))
  (println "Original still clean:" (count (:emotions my-world))))

;; --- Destructuring: pulling apart maps ---
;; The kernel uses this everywhere.

(let [{:keys [mode cycle emotions]} my-world]
  (println "Mode:" mode "Cycle:" cycle "Emotions:" (count emotions)))

;; Nested destructuring:
(let [{{cx1 :cx-1} :contexts} my-world]
  (println "Root context:" (:id cx1)))

(let [{:keys [a b]} {:a 1 :b 2}]
  (println "a:" a "b:" b))

(def my-hashmap 
  {:a "A" 
   :b "B" 
   :c "C" 
   :d "D"})

(def my-nested-hashmap 
  {:a "A" 
   :b "B" 
   :c "C" 
   :d "D" 
   :q {:x "X" 
       :y "Y" 
       :z "Z"}})


(let [{:keys [a b], {:keys [x y]} :q} my-nested-hashmap]
  (println "a:" a "b:" b "x:" x "y:" y))


;; =============================================================
;; Exercise: build a world with two emotions and one goal
;; =============================================================
;; Try building this yourself. A goal is just a map under :goals.

(def my-populated-world
  (-> my-world
      ;; Add an emotion
      (assoc-in [:emotions :e-shame] {:strength 0.6
                                      :valence :negative
                                      :affect :shame})
      ;; Add another emotion
      (assoc-in [:emotions :e-hope] {:strength 0.3
                                     :valence :positive
                                     :affect :hope})
      ;; Add a goal
      (assoc-in [:goals :g-1] {:id :g-1
                               :goal-type :rationalization
                               :strength 0.7
                               :situation-id :seeing-through
                               :status :active})))

(pprint/pprint my-populated-world)

;; Check your work:
(count (:emotions my-populated-world))
;; => 2

(get-in my-populated-world [:goals :g-1 :goal-type])
;; => :rationalization

;; This is literally how the kernel works. No magic. Maps all the
;; way down.
