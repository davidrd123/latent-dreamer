(ns daydreamer.fact-query
  "Shared fact predicates and identity/reference helpers.

  Facts remain plain Clojure maps keyed by `:fact/type`, so this namespace
  gives the rule substrate a reusable query layer without introducing a second
  state model.")

(defn fact-type?
  [fact expected]
  (and (map? fact) (= (:fact/type fact) expected)))

(defn goal-fact?
  [fact]
  (fact-type? fact :goal))

(defn emotion-fact?
  [fact]
  (fact-type? fact :emotion))

(defn dependency-fact?
  [fact]
  (fact-type? fact :dependency))

(defn intends-fact?
  [fact]
  (fact-type? fact :intends))

(defn failure-cause-fact?
  [fact]
  (fact-type? fact :failure-cause))

(defn rationalization-frame-fact?
  [fact]
  (fact-type? fact :rationalization-frame))

(defn negative-emotion-fact?
  [fact]
  (and (emotion-fact? fact)
       (or (= :negative (:valence fact))
           (= :negative (:polarity fact))
           (= true (:negative? fact)))))

(defn positive-emotion-fact?
  [fact]
  (and (emotion-fact? fact)
       (or (= :positive (:valence fact))
           (= :positive (:polarity fact))
           (= true (:positive? fact)))))

(defn fact-id
  [fact]
  (cond
    (goal-fact? fact) (:goal-id fact)
    (emotion-fact? fact) (:emotion-id fact)
    :else (or (:fact/id fact)
              (:id fact)
              (:goal-id fact)
              (:emotion-id fact))))

(def ^:private fact-ref-id-keys
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
  [fact]
  (->> fact-ref-id-keys
       (keep fact)
       set))
