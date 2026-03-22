(ns daydreamer.fact-query
  "Shared fact predicates and identity/reference helpers.

  Facts are plain Clojure maps with a :fact/type key that says what kind
  of fact they are (:goal, :emotion, :dependency, etc). This namespace
  provides reusable predicates for querying and identifying facts.")

;; --- Type predicates ---
;; Each one asks: is this fact of a particular type?
;; Used by the rule matcher, goal families, and episodic retrieval.

(defn fact-type?
  "Is this fact of the given type? Checks :fact/type key."
  [fact expected]
  (and (map? fact) (= (:fact/type fact) expected)))

(defn goal-fact?       [fact] (fact-type? fact :goal))
(defn emotion-fact?    [fact] (fact-type? fact :emotion))
(defn dependency-fact? [fact] (fact-type? fact :dependency))
(defn intends-fact?    [fact] (fact-type? fact :intends))

(defn failure-cause-fact?
  [fact]
  (fact-type? fact :failure-cause))

(defn rationalization-frame-fact?
  [fact]
  (fact-type? fact :rationalization-frame))

;; --- Valence predicates ---
;; Check emotional polarity. Defensive — handles multiple field names
;; because facts come from different sources (kernel, fixtures, adapters).

(defn negative-emotion-fact?
  "Is this a negative emotion? Checks :valence, :polarity, and :negative? fields."
  [fact]
  (and (emotion-fact? fact)
       (or (= :negative (:valence fact))
           (= :negative (:polarity fact))
           (= true (:negative? fact)))))

(defn positive-emotion-fact?
  "Is this a positive emotion? Checks :valence, :polarity, and :positive? fields."
  [fact]
  (and (emotion-fact? fact)
       (or (= :positive (:valence fact))
           (= :positive (:polarity fact))
           (= true (:positive? fact)))))

;; --- Identity extraction ---
;; Different fact types store their ID in different keys.

(defn fact-id
  "Get the primary ID of a fact. Goals use :goal-id, emotions use :emotion-id,
  others fall back through :fact/id, :id, etc."
  [fact]
  (cond
    (goal-fact? fact) (:goal-id fact)
    (emotion-fact? fact) (:emotion-id fact)
    :else (or (:fact/id fact)
              (:id fact)
              (:goal-id fact)
              (:emotion-id fact))))

;; --- Reference collection ---
;; A fact may reference other facts by ID (e.g., a dependency links an
;; emotion to a goal). fact-ref-ids collects all referenced IDs.

(def ^:private fact-ref-id-keys
  "All the keys that might hold a reference to another fact's ID."
  [:emotion-id
   :goal-id
   :from-id
   :to-id
   :from-goal-id
   :to-goal-id
   :from-emotion-id
   :to-emotion-id
   :source-id
   :target-id])

(defn fact-ref-ids
  "Collect all IDs that this fact references.

  Uses each key as a lookup function on the fact map — in Clojure,
  a map is a function of its keys, so (fact :goal-id) returns the value
  or nil. `keep` drops the nils, `set` removes duplicates."
  [fact]
  (->> fact-ref-id-keys
       (keep fact)    ; look up each key in the fact, keep non-nil values
       set))
