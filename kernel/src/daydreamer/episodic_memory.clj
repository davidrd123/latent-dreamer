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
(def ^:private same-family-provenance-max 0.75)
(def ^:private same-family-loop-threshold 2)
(def ^:private stale-same-family-failed-use-threshold 2)
(def ^:private stale-rehabilitation-success-threshold 2)
(def ^:private valid-episode-use-outcomes
  #{:succeeded :failed :backfired :contradicted})

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
           content-indices reminding-indices provenance-indices support-indices
           plan-threshold reminding-threshold children descendants
           provenance rule-path edge-path payload evaluation
           retention-class keep-decision cycle-created admission-status
           anti-residue-flags]
    :or {id :ep-1
         indices #{}
         plan-threshold 0
         admission-status :durable
         reminding-threshold 0
         children []}}]
  (let [content-indices (set (or content-indices indices))
        reminding-indices (set (or reminding-indices content-indices))
        provenance-indices (set provenance-indices)
        support-indices (set support-indices)
        all-indices (set/union content-indices
                               provenance-indices
                               support-indices)]
    (cond-> {:id id
             :rule rule
             :goal-id goal-id
             :context-id context-id
             :realism realism
             :desirability desirability
             :indices all-indices
             :content-indices content-indices
             :reminding-indices reminding-indices
             :provenance-indices provenance-indices
             :support-indices support-indices
             :cue-indices {:content content-indices
                           :reminding reminding-indices
                           :provenance provenance-indices
                           :support support-indices}
             :admission-status admission-status
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

      (seq anti-residue-flags)
      (assoc :anti-residue-flags (vec anti-residue-flags))

      (some? cycle-created)
      (assoc :cycle-created cycle-created))))

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
  [world episode retrieval-opts]
  (let [retention-class (:retention-class episode)
        keep-decision (:keep-decision episode)
        age (episode-age world episode)
        cluster (payload-cluster episode)
        anti-residue-flags (set (:anti-residue-flags episode))
        active-family (:active-family retrieval-opts)
        source-family (get-in episode [:provenance :family])
        same-family-reentry? (and active-family
                                  source-family
                                  (= active-family source-family))
        base-info
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
          nil)]
    (cond
      (contains? anti-residue-flags :contradicted)
      {:retention-accessible? false
       :retention-adjustment 0.0
       :retention-reason :flagged-contradicted}

      (contains? anti-residue-flags :backfired)
      {:retention-accessible? false
       :retention-adjustment 0.0
       :retention-reason :flagged-backfired}

      (and same-family-reentry?
           (contains? anti-residue-flags :same-family-loop))
      {:retention-accessible? false
       :retention-adjustment 0.0
       :retention-reason :flagged-same-family-loop}

      (contains? anti-residue-flags :stale)
      (merge (or base-info {:retention-adjustment 0.0})
             {:retention-adjustment (+ -1.0
                                       (double (or (:retention-adjustment base-info)
                                                   0.0)))
              :retention-reason :flagged-stale})

      :else
      base-info)))

(defn- next-episode-id
  [world]
  (let [next-id (inc (or (:id-counter world) 0))
        episode-id (keyword (str "ep-" next-id))]
    [(assoc world :id-counter next-id) episode-id]))

(defn- next-episode-use-id
  [world]
  (let [next-id (inc (or (:episode-use-counter world) 0))
        use-id (keyword (str "epuse-" next-id))]
    [(assoc world :episode-use-counter next-id) use-id]))

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

(defn promote-episode
  "Promote an episode from provisional to durable with a structured reason.

  Returns `[world promoted?]`. Promotion is monotonic and leaves non-provisional
  episodes unchanged."
  [world episode-id promotion]
  (let [episode (episode-or-throw world episode-id)
        current-status (:admission-status episode :durable)]
    (if (not= :provisional current-status)
      [world false]
      (let [promotion-record (cond-> {:from-status current-status
                                      :to-status :durable
                                      :cycle (:cycle world)}
                               (:reason promotion)
                               (assoc :reason (:reason promotion))

                               (:source-family promotion)
                               (assoc :source-family (:source-family promotion))

                               (:target-family promotion)
                               (assoc :target-family (:target-family promotion))

                               (:source-rule promotion)
                               (assoc :source-rule (:source-rule promotion))

                               (:target-rule promotion)
                               (assoc :target-rule (:target-rule promotion)))]
        [(-> world
             (assoc-in [:episodes episode-id :admission-status] :durable)
             (assoc-in [:episodes episode-id :promoted-cycle] (:cycle world))
             (update-in [:episodes episode-id :promotion-history]
                        (fnil conj [])
                        promotion-record))
         true]))))

(defn demote-episode
  "Demote an episode to a lower admission tier with a structured reason.

  Returns `[world demoted?]`. Demotion only applies when the target status is
  lower than the current status."
  [world episode-id demotion]
  (let [episode (episode-or-throw world episode-id)
        current-status (:admission-status episode :durable)
        target-status (:to-status demotion)
        rank {:trace 0
              :provisional 1
              :durable 2}]
    (if (or (nil? target-status)
            (>= (get rank target-status -1)
                (get rank current-status -1)))
      [world false]
      (let [demotion-record (cond-> {:from-status current-status
                                     :to-status target-status
                                     :cycle (:cycle world)}
                              (:reason demotion)
                              (assoc :reason (:reason demotion))

                              (:source-family demotion)
                              (assoc :source-family (:source-family demotion))

                              (:target-family demotion)
                              (assoc :target-family (:target-family demotion))

                              (:source-rule demotion)
                              (assoc :source-rule (:source-rule demotion))

                              (:target-rule demotion)
                              (assoc :target-rule (:target-rule demotion)))]
        [(-> world
             (assoc-in [:episodes episode-id :admission-status] target-status)
             (assoc-in [:episodes episode-id :demoted-cycle] (:cycle world))
             (update-in [:episodes episode-id :demotion-history]
                        (fnil conj [])
                        demotion-record))
         true]))))

(defn flag-episode
  "Attach an anti-residue flag to an episode with structured reason metadata.

  Returns `[world flagged?]` and is idempotent for an existing flag."
  [world episode-id flag flag-record]
  (let [episode (episode-or-throw world episode-id)
        current-flags (set (:anti-residue-flags episode))]
    (if (contains? current-flags flag)
      [world false]
      [(-> world
           (update-in [:episodes episode-id :anti-residue-flags]
                      (fnil (fn [flags new-flag]
                              (->> (conj (vec flags) new-flag)
                                   distinct
                                   vec))
                            [])
                      flag)
           (update-in [:episodes episode-id :anti-residue-history]
                      (fnil conj [])
                      (cond-> {:flag flag
                               :cycle (:cycle world)}
                        (:reason flag-record)
                        (assoc :reason (:reason flag-record))

                        (:source-family flag-record)
                        (assoc :source-family (:source-family flag-record))

                        (:target-family flag-record)
                        (assoc :target-family (:target-family flag-record))

                        (:episode-id flag-record)
                        (assoc :episode-id (:episode-id flag-record)))))
       true])))

(defn clear-episode-flag
  "Clear an active anti-residue flag from an episode.

  Returns `[world cleared?]` and is idempotent when the flag is already absent."
  [world episode-id flag clear-record]
  (let [episode (episode-or-throw world episode-id)
        current-flags (set (:anti-residue-flags episode))]
    (if-not (contains? current-flags flag)
      [world false]
      [(-> world
           (update-in [:episodes episode-id :anti-residue-flags]
                      (fn [flags]
                        (->> flags
                             (remove #{flag})
                             vec)))
           (update-in [:episodes episode-id :anti-residue-history]
                      (fnil conj [])
                      (cond-> {:flag flag
                               :cycle (:cycle world)
                               :action :cleared}
                        (:reason clear-record)
                        (assoc :reason (:reason clear-record))

                        (:source-family clear-record)
                        (assoc :source-family (:source-family clear-record))

                        (:target-family clear-record)
                        (assoc :target-family (:target-family clear-record))

                        (:episode-id clear-record)
                        (assoc :episode-id (:episode-id clear-record)))))
       true])))

(defn note-episode-use
  "Record attributed use of an episode by a later family plan.

  Returns `[world use-info]`. Repeated same-family use without any
  cross-family use is flagged as `:same-family-loop`."
  [world episode-id use]
  (let [episode (episode-or-throw world episode-id)
        [world use-id] (next-episode-use-id world)
        source-family (or (:source-family use)
                          (get-in episode [:provenance :family]))
        target-family (:target-family use)
        same-family? (and source-family
                          target-family
                          (= source-family target-family))
        use-record (cond-> {:use-id use-id
                            :episode-id episode-id
                            :cycle (:cycle world)
                            :source-family source-family
                            :target-family target-family
                            :status :pending}
                     (:reason use)
                     (assoc :reason (:reason use))

                     (:use-role use)
                     (assoc :use-role (:use-role use))

                     (:goal-id use)
                     (assoc :goal-id (:goal-id use))

                     (:branch-context-id use)
                     (assoc :branch-context-id (:branch-context-id use))

                     (:source-rule use)
                     (assoc :source-rule (:source-rule use))

                     (:target-rule use)
                     (assoc :target-rule (:target-rule use)))
        world (-> world
                  (update-in [:episodes episode-id :use-history]
                             (fnil conj [])
                             use-record)
                  (update-in [:episodes episode-id :use-stats]
                             (fnil merge {:same-family-use-count 0
                                          :cross-family-use-count 0
                                          :successful-use-count 0
                                          :failed-use-count 0
                                          :backfire-count 0
                                          :contradiction-count 0}))
                  (update-in [:episodes episode-id :use-stats :same-family-use-count]
                             (fnil + 0)
                             (if same-family? 1 0))
                  (update-in [:episodes episode-id :use-stats :cross-family-use-count]
                             (fnil + 0)
                             (if same-family? 0 1))
                  (assoc-in [:episodes episode-id :use-stats :last-use-cycle]
                            (:cycle world))
                  (assoc-in [:episodes episode-id :use-stats :last-source-family]
                            source-family)
                  (assoc-in [:episodes episode-id :use-stats :last-target-family]
                            target-family))
        use-stats (get-in world [:episodes episode-id :use-stats])
        same-family-count (:same-family-use-count use-stats 0)
        cross-family-count (:cross-family-use-count use-stats 0)
        loop-risk? (and same-family?
                        (>= same-family-count same-family-loop-threshold)
                        (zero? cross-family-count))
        [world flagged?] (if loop-risk?
                           (flag-episode world
                                         episode-id
                                         :same-family-loop
                                         {:reason :same-family-reuse-threshold
                                          :source-family source-family
                                          :target-family target-family
                                          :episode-id episode-id})
                           [world false])]
    [world (assoc use-record
                  :same-family? same-family?
                  :same-family-use-count same-family-count
                  :cross-family-use-count cross-family-count
                  :loop-risk? loop-risk?
                  :flagged? flagged?)]))

(defn note-episode-reuse
  "Compatibility wrapper over `note-episode-use`.

  Returns the historical reuse-shaped keys while storing canonical use data."
  [world episode-id reuse]
  (let [[world use-info] (note-episode-use world episode-id reuse)]
    [world {:same-family? (:same-family? use-info)
            :same-family-reuse-count (:same-family-use-count use-info)
            :cross-family-reuse-count (:cross-family-use-count use-info)
            :loop-risk? (:loop-risk? use-info)
            :flagged? (:flagged? use-info)}]))

(defn record-promotion-evidence
  "Attach structured promotion evidence to an episode."
  [world episode-id evidence]
  (let [evidence-record (cond-> {:cycle (:cycle world)
                                 :type (:type evidence)}
                          (:use-id evidence)
                          (assoc :use-id (:use-id evidence))

                          (:source-family evidence)
                          (assoc :source-family (:source-family evidence))

                          (:target-family evidence)
                          (assoc :target-family (:target-family evidence))

                          (:source-rule evidence)
                          (assoc :source-rule (:source-rule evidence))

                          (:target-rule evidence)
                          (assoc :target-rule (:target-rule evidence))

                          (:branch-context-id evidence)
                          (assoc :branch-context-id (:branch-context-id evidence))

                          (:goal-id evidence)
                          (assoc :goal-id (:goal-id evidence)))]
    [(update-in world [:episodes episode-id :promotion-evidence]
                (fnil conj [])
                evidence-record)
     evidence-record]))

(defn- qualifying-promotion-evidence
  [episode]
  (->> (:promotion-evidence episode)
       (filter #(= :cross-family-use-success (:type %)))
       last))

(defn- qualifying-promotion-evidence-count
  [episode]
  (->> (:promotion-evidence episode)
       (filter #(= :cross-family-use-success (:type %)))
       count))

(defn- same-family-failed-use-count
  [use-history]
  (->> use-history
       (filter #(and (= :resolved (:status %))
                     (= :failed (:outcome %))
                     (= (:source-family %) (:target-family %))))
       count))

(defn- successful-use-count
  [use-history]
  (->> use-history
       (filter #(and (= :resolved (:status %))
                     (= :succeeded (:outcome %))))
       count))

(defn resolve-episode-use-outcome
  "Resolve a previously recorded episode use with an attributed outcome.

  Returns `[world outcome-info]`. Backfire/contradiction outcomes flag the
  episode immediately. Cross-family success records promotion evidence.
  Re-resolving the same use with the same outcome is a no-op."
  [world episode-id use-id outcome]
  (let [episode (episode-or-throw world episode-id)
        use-history (vec (:use-history episode))
        use-record (some #(when (= use-id (:use-id %)) %) use-history)
        outcome-type (:outcome outcome)]
    (when-not use-record
      (throw (ex-info "Unknown episode use"
                      {:episode-id episode-id
                       :use-id use-id})))
    (when-not (contains? valid-episode-use-outcomes outcome-type)
      (throw (ex-info "Unsupported episode use outcome"
                      {:episode-id episode-id
                       :use-id use-id
                       :outcome outcome-type
                       :allowed-outcomes valid-episode-use-outcomes})))
    (if (= :resolved (:status use-record))
      (do
        (when-not (= outcome-type (:outcome use-record))
          (throw (ex-info "Episode use already resolved with different outcome"
                          {:episode-id episode-id
                           :use-id use-id
                           :existing-outcome (:outcome use-record)
                           :requested-outcome outcome-type})))
        [world {:use-id use-id
                :episode-id episode-id
                :source-family (:source-family use-record)
                :target-family (:target-family use-record)
                :outcome outcome-type
                :already-resolved? true
                :evidence-recorded? false
                :flagged? false}])
      (let [resolved-record (cond-> use-record
                              true
                              (assoc :outcome outcome-type
                                     :resolved-cycle (:cycle world)
                                     :status :resolved)

                              (:reason outcome)
                              (assoc :outcome-reason (:reason outcome)))
            world (assoc-in world
                            [:episodes episode-id :use-history]
                            (mapv (fn [record]
                                    (if (= use-id (:use-id record))
                                      resolved-record
                                      record))
                                  use-history))
            resolved-use-history (vec (get-in world [:episodes episode-id :use-history]))
            world (update-in world
                             [:episodes episode-id :outcome-stats]
                             (fnil merge {:successful-use-count 0
                                          :failed-use-count 0
                                          :backfire-count 0
                                          :contradiction-count 0}))
            world (case outcome-type
                    :succeeded
                    (-> world
                        (update-in [:episodes episode-id :outcome-stats :successful-use-count]
                                   (fnil + 0) 1)
                        (assoc-in [:episodes episode-id :outcome-stats :last-success-cycle]
                                  (:cycle world)))

                    :failed
                    (-> world
                        (update-in [:episodes episode-id :outcome-stats :failed-use-count]
                                   (fnil + 0) 1)
                        (assoc-in [:episodes episode-id :outcome-stats :last-failed-cycle]
                                  (:cycle world)))

                    :backfired
                    (-> world
                        (update-in [:episodes episode-id :outcome-stats :backfire-count]
                                   (fnil + 0) 1)
                        (assoc-in [:episodes episode-id :outcome-stats :last-backfire-cycle]
                                  (:cycle world)))

                    :contradicted
                    (-> world
                        (update-in [:episodes episode-id :outcome-stats :contradiction-count]
                                   (fnil + 0) 1)
                        (assoc-in [:episodes episode-id :outcome-stats :last-contradiction-cycle]
                                  (:cycle world))))
            stale-risk? (and (= :failed outcome-type)
                             (= (:source-family resolved-record)
                                (:target-family resolved-record))
                             (>= (same-family-failed-use-count resolved-use-history)
                                 stale-same-family-failed-use-threshold)
                             (zero? (successful-use-count resolved-use-history))
                             (not (seq (set/intersection #{:backfired :contradicted}
                                                         (set (:anti-residue-flags
                                                               (get-in world
                                                                       [:episodes episode-id]
                                                                       {})))))))
            [world evidence-record]
            (if (and (= :succeeded outcome-type)
                     (not= (:source-family use-record)
                           (:target-family use-record)))
              (record-promotion-evidence world
                                         episode-id
                                         {:type :cross-family-use-success
                                          :use-id use-id
                                          :source-family (:source-family use-record)
                                          :target-family (:target-family use-record)
                                          :source-rule (:source-rule use-record)
                                          :target-rule (:target-rule use-record)
                                          :branch-context-id (:branch-context-id use-record)
                                          :goal-id (:goal-id use-record)})
              [world nil])
            [world flagged?]
            (cond
              (= :backfired outcome-type)
              (flag-episode world
                            episode-id
                            :backfired
                            {:reason (:reason outcome)
                             :source-family (:source-family use-record)
                             :target-family (:target-family use-record)
                             :episode-id episode-id})

              (= :contradicted outcome-type)
              (flag-episode world
                            episode-id
                            :contradicted
                            {:reason (:reason outcome)
                             :source-family (:source-family use-record)
                             :target-family (:target-family use-record)
                             :episode-id episode-id})

              stale-risk?
              (flag-episode world
                            episode-id
                            :stale
                            {:reason :same-family-failed-use-threshold
                             :source-family (:source-family use-record)
                             :target-family (:target-family use-record)
                             :episode-id episode-id})

              :else
              [world false])]
        [world {:use-id use-id
                :episode-id episode-id
                :source-family (:source-family use-record)
                :target-family (:target-family use-record)
                :outcome outcome-type
                :already-resolved? false
                :evidence-recorded? (boolean evidence-record)
                :evidence evidence-record
                :flagged? flagged?}]))))

(defn- durable-candidate?
  [episode]
  (and (= :provisional (:admission-status episode))
       (= :payload-exemplar (:retention-class episode))
       (= :keep-exemplar (:keep-decision episode))
       (empty? (set/intersection #{:backfired :contradicted :stale}
                                 (set (:anti-residue-flags episode))))))

(defn eligible-for-promotion?
  "Return true when an episode has enough current evidence to become durable.

  This first pass is intentionally conservative and only considers
  cross-family use success evidence, not later demotion/frontier policy."
  [episode]
  (and (durable-candidate? episode)
       (some? (qualifying-promotion-evidence episode))))

(defn- stale-rehabilitation-ready?
  [episode]
  (and (contains? (set (:anti-residue-flags episode)) :stale)
       (empty? (set/intersection #{:backfired :contradicted}
                                 (set (:anti-residue-flags episode))))
       (>= (qualifying-promotion-evidence-count episode)
           stale-rehabilitation-success-threshold)))

(defn reconcile-episode-admission
  "Promote or demote an episode according to the current evidence and flags.

  Returns `[world transition]` where `transition` is nil or a map describing
  the applied status change."
  [world episode-id]
  (let [episode (episode-or-throw world episode-id)
        current-status (:admission-status episode :durable)
        [world _stale-cleared?]
        (if (and (not= :durable current-status)
                 (stale-rehabilitation-ready? episode))
          (clear-episode-flag world
                              episode-id
                              :stale
                              {:reason :cross-family-rehabilitation
                               :episode-id episode-id})
          [world false])
        episode (episode-or-throw world episode-id)
        current-status (:admission-status episode :durable)
        promotion-evidence (qualifying-promotion-evidence episode)
        hard-failure? (seq (set/intersection #{:backfired :contradicted}
                                             (set (:anti-residue-flags episode))))]
    (cond
      (and (= :durable current-status)
           hard-failure?)
      (let [[world demoted?]
            (demote-episode world
                            episode-id
                            {:to-status :provisional
                             :reason :outcome-driven-demotion})]
        [world (when demoted?
                 {:episode-id episode-id
                  :from-status :durable
                  :to-status :provisional
                  :reason :outcome-driven-demotion})])

      (and (= :durable current-status)
           (contains? (set (:anti-residue-flags episode)) :stale))
      (let [[world demoted?]
            (demote-episode world
                            episode-id
                            {:to-status :provisional
                             :reason :stale-use-demotion})]
        [world (when demoted?
                 {:episode-id episode-id
                  :from-status :durable
                  :to-status :provisional
                  :reason :stale-use-demotion})])

      (eligible-for-promotion? episode)
      (let [[world promoted?]
            (promote-episode world
                             episode-id
                             {:reason (or (:type promotion-evidence)
                                          :evidence-driven-promotion)
                              :source-family (:source-family promotion-evidence)
                              :target-family (:target-family promotion-evidence)
                              :source-rule (:source-rule promotion-evidence)
                              :target-rule (:target-rule promotion-evidence)})]
        [world (when promoted?
                 {:episode-id episode-id
                  :from-status current-status
                  :to-status :durable
                  :reason (or (:type promotion-evidence)
                              :evidence-driven-promotion)})])

      :else
      [world nil])))

(defn store-episode
  "Store an episode under an index, incrementing the requested thresholds."
  [world episode-id index {:keys [plan? reminding? zone]
                           :or {plan? false
                                reminding? false}}]
  (let [zone (or zone
                 (if (or plan? reminding?)
                   :content
                   :support))
        [index-path episode-field]
        (case zone
          :content [[:episode-index index] :content-indices]
          :provenance [[:provenance-episode-index index] :provenance-indices]
          :support [[:support-episode-index index] :support-indices]
          (throw (ex-info "Unknown episode index zone"
                          {:zone zone
                           :episode-id episode-id
                           :index index})))]
    (cond-> (-> world
                (update-in index-path (fnil conj #{}) episode-id)
                (update-in [:episodes episode-id episode-field] (fnil conj #{}) index)
                (update-in [:episodes episode-id :indices] (fnil conj #{}) index)
                (update-in [:episodes episode-id :cue-indices episode-field]
                           (fnil conj #{})
                           index))
      (and (= :content zone)
           reminding?)
      (-> (update-in [:episodes episode-id :reminding-indices]
                     (fnil conj #{})
                     index)
          (update-in [:episodes episode-id :cue-indices :reminding]
                     (fnil conj #{})
                     index))

      (= :content zone)
      (-> (update-in [:episodes episode-id :plan-threshold]
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
                           0))))))

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

(defn- episode-family
  [episode]
  (or (get-in episode [:provenance :family])
      (some-> (:rule episode) namespace keyword)))

(defn- provenance-min-content-marks
  [episode]
  (if (#{:imaginary :counterfactual} (:realism episode))
    2
    1))

(defn- same-family-provenance-cap
  [bonus-info retrieval-opts episode]
  (let [active-family (:active-family retrieval-opts)
        same-family? (and active-family
                          (= active-family (episode-family episode)))
        raw-bonus (double (or (:provenance-bonus bonus-info) 0.0))
        capped-bonus (if same-family?
                       (min raw-bonus same-family-provenance-max)
                       raw-bonus)]
    (cond-> bonus-info
      (and (pos? raw-bonus)
           same-family?)
      (assoc :same-family-provenance? true
             :provenance-bonus-raw raw-bonus
             :provenance-bonus capped-bonus)

      (and same-family?
           (< capped-bonus raw-bonus))
      (assoc :provenance-capped? true))))

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
                 provenance-max-depth 4}
            :as retrieval-opts}]
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
    (some-> (cond
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
              nil)
            (same-family-provenance-cap retrieval-opts episode))))

(defn- episode-reminding-indices
  [episode]
  (cond
    (contains? episode :reminding-indices)
    (:reminding-indices episode)

    (contains? episode :content-indices)
    (:content-indices episode)

    :else
    (:indices episode)))

(defn episode-provenance-info
  "Return structural provenance overlap info for one stored episode."
  [world episode-id retrieval-opts]
  (some-> (get-in world [:episodes episode-id])
          (provenance-bonus-info retrieval-opts)))

(defn episode-accessibility-info
  "Return retrieval accessibility info for one stored episode under the given
  retrieval context."
  [world episode-id retrieval-opts]
  (when-let [episode (get-in world [:episodes episode-id])]
    (let [admission-status (:admission-status episode :durable)
          respect-recent? (not= false (:respect-recent? retrieval-opts))
          recent? (recent-episode? world episode-id)
          retention-info (retention-accessibility-info world
                                                       episode
                                                       retrieval-opts)
          retention-accessible? (not= false (:retention-accessible?
                                             retention-info))
          accessible? (and (not= :trace admission-status)
                           (or (not respect-recent?)
                               (not recent?))
                           retention-accessible?)]
      (cond-> {:episode-id episode-id
               :admission-status admission-status
               :recent? recent?
               :accessible? accessible?}
        (or (:retention-reason retention-info)
            (contains? retention-info :retention-adjustment))
        (merge (select-keys retention-info
                            [:retention-adjustment
                             :retention-reason
                             :retention-age
                             :payload-cluster
                             :payload-cluster-rank]))))))

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
                       admission-status (:admission-status episode :durable)
                       provenance-eligible? (>= mark-count
                                                (provenance-min-content-marks
                                                 episode))
                       bonus-info (when provenance-eligible?
                                    (provenance-bonus-info episode retrieval-opts))
                       retention-info (retention-accessibility-info world
                                                                    episode
                                                                    retrieval-opts)
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
                   (when (and (not= :trace admission-status)
                              retention-accessible?
                              (not (recent-episode? world episode-id))
                              (>= effective-marks threshold))
                     (cond-> {:episode-id episode-id
                              :marks mark-count
                              :threshold threshold
                              :admission-status admission-status}
                       (pos? provenance-bonus)
                       (merge {:provenance-bonus provenance-bonus
                               :effective-marks effective-marks
                               :provenance-reason (:provenance-reason bonus-info)}
                              (select-keys bonus-info
                                           [:provenance-bridge-depth
                                            :provenance-bridge-path
                                            :provenance-bridge-direction
                                            :provenance-bridge-count
                                            :provenance-bonus-raw
                                            :same-family-provenance?
                                            :provenance-capped?]))

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
                          (episode-reminding-indices
                           (episode-or-throw world episode-id)))
            pending (mapv :episode-id (remindings world [] retrieval-opts))
            seen #{episode-id}
            reminded []]
       (if-let [next-episode-id (first pending)]
         (if (or (contains? seen next-episode-id)
                 (recent-episode? world next-episode-id))
           (recur world (subvec pending 1) seen reminded)
           (let [world (reduce add-recent-index
                               (add-recent-episode world next-episode-id)
                               (episode-reminding-indices
                                (episode-or-throw world next-episode-id)))
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
