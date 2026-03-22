(ns daydreamer.episodic-memory
  "Episode store with index-threshold retrieval and reminding cascade.

  Recovers episodic memory from Mueller's dd_epis.cl.
  Episodes are stored under multiple indices. Retrieval increments marks
  per matched index; retrieval fires when marks reach threshold.
  Serendipity lowers threshold by one. Recent episodes are excluded.
  Reminding cascades: retrieving one episode activates its other indices,
  which may retrieve more.

  Source: dd_epis.cl (epmem-retrieve1, epmem-reminding, add-recent-index)"
  (:require [clojure.set :as set]
            [daydreamer.rules :as rules]))

(def ^:private recent-index-max-length 6)
(def ^:private recent-episode-max-length 4)
(def ^:private shared-edge-bonus-scale 5.0)
(def ^:private shared-rule-bonus-scale 4.0)
(def ^:private payload-exemplar-cluster-cap 2)

(defn- graph-bridge-bonus-scale
  [depth]
  (case depth
    1 3.0
    2 2.5
    3 2.0
    1.5))

(defn create-episode
  "Create an episode map. This is a pure constructor for fixtures and tests."
  [{:keys [id rule goal-id context-id realism desirability indices
           plan-threshold reminding-threshold children descendants
           provenance rule-path edge-path payload evaluation
           retention-class keep-decision cycle-created]
    :or {id :ep-1
         indices #{}
         plan-threshold 0
         reminding-threshold 0
         children []}}]
  (cond-> {:id id
           :rule rule
           :goal-id goal-id
           :context-id context-id
           :realism realism
           :desirability desirability
           :indices (set indices)
           :plan-threshold plan-threshold
           :reminding-threshold reminding-threshold
           :children (vec children)
           :descendants (vec (or descendants (cons id children)))}
    provenance
    (assoc :provenance provenance)

    (seq rule-path)
    (assoc :rule-path (vec rule-path))

    (seq edge-path)
    (assoc :edge-path (vec edge-path))

    payload
    (assoc :payload payload)

    evaluation
    (assoc :evaluation evaluation)

    retention-class
    (assoc :retention-class retention-class)

    keep-decision
    (assoc :keep-decision keep-decision)

    (some? cycle-created)
    (assoc :cycle-created cycle-created)))

(defn- payload-cluster
  [episode]
  (or (:payload-cluster episode)
      (get-in episode [:evaluation :payload-cluster])))

(defn- episode-age
  [world episode]
  (max 0
       (- (long (or (:cycle world) 0))
          (long (or (:cycle-created episode) 0)))))

(defn- ranked-cluster-episode-ids
  [world cluster]
  (->> (:episodes world)
       vals
       (filter (fn [episode]
                 (and (= :payload-exemplar (:retention-class episode))
                      (= :keep-exemplar (:keep-decision episode))
                      (= cluster (payload-cluster episode)))))
       (sort-by (juxt (comp - #(long (or % 0)) :cycle-created)
                      (comp str :id)))
       (mapv :id)))

(defn- retention-accessibility-info
  [world episode]
  (let [retention-class (:retention-class episode)
        keep-decision (:keep-decision episode)
        age (episode-age world episode)
        cluster (payload-cluster episode)]
    (cond
      (= :hot-cues retention-class)
      (cond
        (>= age 4)
        {:retention-adjustment -1.0
         :retention-reason :hot-cue-stale
         :retention-age age}

        (>= age 2)
        {:retention-adjustment -0.5
         :retention-reason :hot-cue-aging
         :retention-age age}

        :else
        {:retention-adjustment 0.0
         :retention-reason :hot-cue-fresh
         :retention-age age})

      (and (= :payload-exemplar retention-class)
           (= :keep-exemplar keep-decision)
           cluster)
      (let [ranked-ids (ranked-cluster-episode-ids world cluster)
            rank (.indexOf ranked-ids (:id episode))]
        (if (and (not= -1 rank)
                 (< rank payload-exemplar-cluster-cap))
          {:retention-adjustment 0.0
           :retention-reason :payload-exemplar-active
           :payload-cluster cluster
           :payload-cluster-rank (inc rank)}
          {:retention-accessible? false
           :retention-adjustment 0.0
           :retention-reason :payload-cluster-capped
           :payload-cluster cluster
           :payload-cluster-rank (when (not= -1 rank) (inc rank))}))

      :else
      nil)))

(defn- next-episode-id
  [world]
  (let [next-id (inc (or (:id-counter world) 0))
        episode-id (keyword (str "ep-" next-id))]
    [(assoc world :id-counter next-id) episode-id]))

(defn- episode-or-throw
  [world episode-id]
  (or (get-in world [:episodes episode-id])
      (throw (ex-info "Unknown episode"
                      {:episode-id episode-id}))))

(defn add-episode
  "Insert an episode into the world. Descendants default to the episode id plus
  descendants of any child episodes already stored."
  [world episode-spec]
  (let [[world episode-id] (next-episode-id world)
        child-descendants (mapcat (fn [child-id]
                                    (get-in world [:episodes child-id :descendants]
                                            [child-id]))
                                  (:children episode-spec))
        episode (create-episode (assoc episode-spec
                                       :id episode-id
                                       :cycle-created (:cycle world)
                                       :descendants (vec (distinct
                                                          (cons episode-id
                                                                child-descendants)))))]
    [(assoc-in world [:episodes episode-id] episode) episode-id]))

(defn store-episode
  "Store an episode under an index, incrementing the requested thresholds."
  [world episode-id index {:keys [plan? reminding?] :or {plan? false
                                                         reminding? false}}]
  (-> world
      (update-in [:episode-index index] (fnil conj #{}) episode-id)
      (update-in [:episodes episode-id :indices] (fnil conj #{}) index)
      (update-in [:episodes episode-id :plan-threshold]
                 (fnil (fn [threshold]
                         (if plan?
                           (inc threshold)
                           threshold))
                       0))
      (update-in [:episodes episode-id :reminding-threshold]
                 (fnil (fn [threshold]
                         (if reminding?
                           (inc threshold)
                           threshold))
                       0))))

(defn recent-episode?
  "Return true if an episode is already recent or is a descendant of a recent
  episode."
  [world episode-id]
  (boolean
   (some (fn [recent-id]
           (contains? (set (get-in world [:episodes recent-id :descendants] []))
                      episode-id))
         (:recent-episodes world))))

(defn add-recent-index
  "Add an index to the FIFO recent-index list, skipping duplicates."
  [world index]
  (let [recent-indices (vec (:recent-indices world []))]
    (if (some #{index} recent-indices)
      world
      (assoc world :recent-indices
             (->> (conj recent-indices index)
                  (take-last recent-index-max-length)
                  vec)))))

(defn add-recent-episode
  "Add an episode to the FIFO recent-episode list, removing any recent episode
  already contained in the new episode's descendant set."
  [world episode-id]
  (let [descendants (set (get-in world [:episodes episode-id :descendants]
                                 [episode-id]))
        recent-episodes (->> (:recent-episodes world [])
                             (remove descendants)
                             vec)]
    (assoc world :recent-episodes
           (->> (conj recent-episodes episode-id)
                (take-last recent-episode-max-length)
                vec))))

(defn- comparable-edge
  [edge]
  (select-keys edge [:from-rule :to-rule :fact-type :edge-kind]))

(defn- bridge-candidates
  [connection-graph active-rule-path episode-rule-path provenance-max-depth]
  (letfn [(candidate-paths [direction start-rules target-rules]
            (for [start-rule-id start-rules
                  target-rule-id target-rules
                  bridge (rules/bridge-paths connection-graph
                                             start-rule-id
                                             target-rule-id
                                             {:min-depth 1
                                              :max-depth provenance-max-depth})]
              (assoc bridge
                     :direction direction
                     :start-rule start-rule-id
                     :target-rule target-rule-id)))]
    (->> (concat (candidate-paths :active-to-episode
                                  active-rule-path
                                  episode-rule-path)
                 (candidate-paths :episode-to-active
                                  episode-rule-path
                                  active-rule-path))
         distinct
         vec)))

(defn- best-bridge-candidate
  [candidates]
  (first (sort-by (juxt :depth
                        (comp pr-str :rule-path)
                        (comp str :direction))
                  candidates)))

(defn- provenance-bonus-info
  [episode {:keys [connection-graph
                   active-rule-path
                   active-edge-path
                   provenance-bonus
                   provenance-max-depth]
            :or {provenance-bonus 1.0
                 provenance-max-depth 4}}]
  (let [episode-rule-path (vec (:rule-path episode))
        episode-edge-path (->> (:edge-path episode)
                               (map comparable-edge)
                               set)
        active-rule-path (vec active-rule-path)
        active-edge-path (->> active-edge-path
                              (map comparable-edge)
                              set)
        shared-edge? (seq (set/intersection active-edge-path episode-edge-path))
        shared-rule? (seq (set/intersection (set active-rule-path)
                                            (set episode-rule-path)))
        bridge-candidates (when (and connection-graph
                                     (seq active-rule-path)
                                     (seq episode-rule-path))
                            (bridge-candidates connection-graph
                                               active-rule-path
                                               episode-rule-path
                                               provenance-max-depth))
        best-bridge (when (seq bridge-candidates)
                      (best-bridge-candidate bridge-candidates))]
    (cond
      shared-edge?
      {:provenance-bonus (* provenance-bonus
                            shared-edge-bonus-scale)
       :provenance-reason :shared-edge}

      shared-rule?
      {:provenance-bonus (* provenance-bonus
                            shared-rule-bonus-scale)
       :provenance-reason :shared-rule}

      best-bridge
      {:provenance-bonus (* provenance-bonus
                            (graph-bridge-bonus-scale
                             (long (:depth best-bridge))))
       :provenance-reason :graph-bridge
       :provenance-bridge-depth (:depth best-bridge)
       :provenance-bridge-path (:rule-path best-bridge)
       :provenance-bridge-direction (:direction best-bridge)
       :provenance-bridge-count (count bridge-candidates)}

      :else
      nil)))

(defn episode-provenance-info
  "Return structural provenance overlap info for one stored episode."
  [world episode-id retrieval-opts]
  (some-> (get-in world [:episodes episode-id])
          (provenance-bonus-info retrieval-opts)))

(defn retrieve-episodes
  "Retrieve episodes by coincidence counting over indices.

  Returns hit maps with `:episode-id`, `:marks`, and `:threshold`.
  Marks are transient and are not stored on episodes."
  [world indices {:keys [serendipity? threshold-key]
                  :or {serendipity? false
                       threshold-key :plan-threshold}
                  :as retrieval-opts}]
  (let [marks (frequencies
               (mapcat #(get-in world [:episode-index %] #{}) indices))]
    (->> marks
         (keep (fn [[episode-id mark-count]]
                 (let [episode (episode-or-throw world episode-id)
                       bonus-info (provenance-bonus-info episode retrieval-opts)
                       retention-info (retention-accessibility-info world episode)
                       retention-accessible? (not= false
                                                   (:retention-accessible?
                                                    retention-info))
                       provenance-bonus (double (or (:provenance-bonus bonus-info)
                                                    0.0))
                       retention-adjustment (double
                                             (or (:retention-adjustment
                                                  retention-info)
                                                 0.0))
                       effective-marks (+ (double mark-count)
                                          provenance-bonus
                                          retention-adjustment)
                       threshold (max 0
                                      (if serendipity?
                                        (dec (or (threshold-key episode) 0))
                                        (or (threshold-key episode) 0)))]
                   (when (and retention-accessible?
                              (not (recent-episode? world episode-id))
                              (>= effective-marks threshold))
                     (cond-> {:episode-id episode-id
                              :marks mark-count
                              :threshold threshold}
                       (pos? provenance-bonus)
                       (merge {:provenance-bonus provenance-bonus
                               :effective-marks effective-marks
                               :provenance-reason (:provenance-reason bonus-info)}
                              (select-keys bonus-info
                                           [:provenance-bridge-depth
                                            :provenance-bridge-path
                                            :provenance-bridge-direction
                                            :provenance-bridge-count]))

                       (or (not (zero? retention-adjustment))
                           (:retention-reason retention-info))
                       (merge {:retention-adjustment retention-adjustment}
                              (select-keys retention-info
                                           [:retention-reason
                                            :retention-age
                                            :payload-cluster
                                            :payload-cluster-rank])))))))
         (sort-by (juxt (comp - #(or % 0.0) :effective-marks)
                        (comp - :marks)
                        (comp str :episode-id)))
         vec)))

(defn remindings
  "Retrieve reminding episodes from active recent indices plus any extra cue
  indices supplied by the caller."
  ([world]
   (remindings world [] {}))
  ([world extra-indices]
   (remindings world extra-indices {}))
  ([world extra-indices retrieval-opts]
   (retrieve-episodes world
                      (concat (:recent-indices world []) extra-indices)
                      (assoc retrieval-opts
                             :threshold-key :reminding-threshold))))

(defn episode-reminding
  "Activate an episode's indices, add it to the recent set, and recursively
  process any newly retrieved remindings.

  Returns `[world reminded-episode-ids]`, where the vector contains only the
  additional retrieved episodes, not the seed episode."
  ([world episode-id]
   (episode-reminding world episode-id {}))
  ([world episode-id retrieval-opts]
   (if (recent-episode? world episode-id)
     [world []]
     (loop [world (reduce add-recent-index
                          (add-recent-episode world episode-id)
                          (:indices (episode-or-throw world episode-id)))
            pending (mapv :episode-id (remindings world [] retrieval-opts))
            seen #{episode-id}
            reminded []]
       (if-let [next-episode-id (first pending)]
         (if (or (contains? seen next-episode-id)
                 (recent-episode? world next-episode-id))
           (recur world (subvec pending 1) seen reminded)
           (let [world (reduce add-recent-index
                               (add-recent-episode world next-episode-id)
                               (:indices (episode-or-throw world next-episode-id)))
                 new-pending (into (subvec pending 1)
                                   (map :episode-id
                                        (remindings world [] retrieval-opts)))]
             (recur world
                    new-pending
                    (conj seen next-episode-id)
                    (conj reminded next-episode-id))))
         [world reminded])))))

(comment
  (create-episode {:rule :reversal-plan
                   :goal-id :g-1
                   :indices #{:s1 :anger}}))
