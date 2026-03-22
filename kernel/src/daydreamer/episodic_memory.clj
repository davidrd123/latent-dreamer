(ns daydreamer.episodic-memory
  "Episode store with index-threshold retrieval and reminding cascade.

  Recovers episodic memory from Mueller's dd_epis.cl.
  Episodes are stored under multiple indices. Retrieval increments marks
  per matched index; retrieval fires when marks reach threshold.
  Serendipity lowers threshold by one. Recent episodes are excluded.
  Reminding cascades: retrieving one episode activates its other indices,
  which may retrieve more.

  Source: dd_epis.cl (epmem-retrieve1, epmem-reminding, add-recent-index)")

(def ^:private recent-index-max-length 6)
(def ^:private recent-episode-max-length 4)

(defn create-episode
  "Create an episode map. This is a pure constructor for fixtures and tests."
  [{:keys [id rule goal-id context-id realism desirability indices
           plan-threshold reminding-threshold children descendants
           provenance rule-path edge-path]
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
    (assoc :edge-path (vec edge-path))))

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

(defn retrieve-episodes
  "Retrieve episodes by coincidence counting over indices.

  Returns hit maps with `:episode-id`, `:marks`, and `:threshold`.
  Marks are transient and are not stored on episodes."
  [world indices {:keys [serendipity? threshold-key]
                  :or {serendipity? false
                       threshold-key :plan-threshold}}]
  (let [marks (frequencies
               (mapcat #(get-in world [:episode-index %] #{}) indices))]
    (->> marks
         (keep (fn [[episode-id mark-count]]
                 (let [episode (episode-or-throw world episode-id)
                       threshold (max 0
                                      (if serendipity?
                                        (dec (or (threshold-key episode) 0))
                                        (or (threshold-key episode) 0)))]
                   (when (and (not (recent-episode? world episode-id))
                              (>= mark-count threshold))
                     {:episode-id episode-id
                      :marks mark-count
                      :threshold threshold}))))
         (sort-by (juxt (comp - :marks) (comp str :episode-id)))
         vec)))

(defn remindings
  "Retrieve reminding episodes from active recent indices plus any extra cue
  indices supplied by the caller."
  ([world]
   (remindings world []))
  ([world extra-indices]
   (retrieve-episodes world
                      (concat (:recent-indices world []) extra-indices)
                      {:threshold-key :reminding-threshold})))

(defn episode-reminding
  "Activate an episode's indices, add it to the recent set, and recursively
  process any newly retrieved remindings.

  Returns `[world reminded-episode-ids]`, where the vector contains only the
  additional retrieved episodes, not the seed episode."
  [world episode-id]
  (if (recent-episode? world episode-id)
    [world []]
    (loop [world (reduce add-recent-index
                         (add-recent-episode world episode-id)
                         (:indices (episode-or-throw world episode-id)))
           pending (mapv :episode-id (remindings world))
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
                                  (map :episode-id (remindings world)))]
            (recur world
                   new-pending
                   (conj seen next-episode-id)
                   (conj reminded next-episode-id))))
        [world reminded]))))

(comment
  (create-episode {:rule :reversal-plan
                   :goal-id :g-1
                   :indices #{:s1 :anger}}))
