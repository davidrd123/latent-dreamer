(ns daydreamer.benchmarks.puppet-knows-autonomous
  "Fixture-driven autonomous Puppet Knows run.

  This namespace sits at the conducted-system edge, not in the pure Mueller
  kernel. It loads the shared Puppet Knows fixture, derives top-level goal
  pressure from situation state, retrieves against the shared dream graph, and
  lets the existing kernel control/family machinery do the branch work."
  (:require [clj-yaml.core :as yaml]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [daydreamer.director :as director]
            [daydreamer.runtime-thought :as runtime-thought]
            [daydreamer.benchmarks.puppet-knows :as puppet]
            [daydreamer.benchmarks.puppet-knows-adapter :as adapter]
            [daydreamer.context :as cx]
            [daydreamer.control :as control]
            [daydreamer.episodic-memory :as episodic]
            [daydreamer.goal-families :as families]
            [daydreamer.goals :as goals]
            [daydreamer.trace :as trace]))

(def ^:private terminal-goal-statuses
  #{:failed :succeeded :terminated})

(def ^:private goal-priority-bonus
  {:rationalization 0.06
   :roving 0.04
   :reversal 0.03
   :recovery 0.02
   :rehearsal 0.01
   :revenge 0.05
   :repercussions 0.03})

(def ^:private autonomous-started-at
  "2026-03-12T12:30:00Z")

(def ^:private autonomous-rationalization-frames
  {:s1_seeing_through {:priority 0.78
                       :reframe-facts [{:fact/type :rationalization
                                        :fact/id :seam_is_honesty}]}
   :s4_the_ring {:priority 0.88
                 :reframe-facts [{:fact/type :rationalization
                                  :fact/id :ring_is_honest_stage}]}})

(defn- cwd-file
  []
  (.getCanonicalFile (io/file (System/getProperty "user.dir"))))

(defn- latent-root
  []
  (let [cwd (cwd-file)]
    (if (= "kernel" (.getName cwd))
      (.getCanonicalFile (.getParentFile cwd))
      cwd)))

(defn- default-scope-root
  []
  (let [repo-root (latent-root)
        lora-root (.getParentFile repo-root)]
    (.getCanonicalPath (io/file lora-root "scope-drd"))))

(defn- clamp01
  [value]
  (-> value double (max 0.0) (min 1.0)))

(defn- as-keyword
  [value]
  (cond
    (keyword? value) value
    (string? value) (keyword value)
    (nil? value) nil
    :else (keyword (str value))))

(defn- ordered-unique
  [values]
  (reduce (fn [acc value]
            (if (some #{value} acc)
              acc
              (conj acc value)))
          []
          values))

(defn- load-yaml
  [path]
  (yaml/parse-string (slurp path) :keywords true))

(defn- load-json
  [path]
  (json/read-str (slurp path) :key-fn keyword))

(defn- fixture-paths
  [scope-root]
  (let [scope-root (io/file scope-root)]
    {:world-path (.getCanonicalPath (io/file scope-root
                                             (:world-path puppet/fixture-relative-paths)))
     :graph-path (.getCanonicalPath (io/file scope-root
                                             (:graph-path puppet/fixture-relative-paths)))
     :feedback-path (.getCanonicalPath (io/file scope-root
                                                (:feedback-path puppet/fixture-relative-paths)))
     :reporter-path (.getCanonicalPath (io/file scope-root
                                                (:reporter-path puppet/fixture-relative-paths)))}))

(defn- parse-situation
  [situation]
  (let [situation-id (as-keyword (:id situation))]
    [situation-id
     {:id situation-id
      :description (:description situation)
      :ripeness (double (or (:ripeness situation) 0.0))
      :activation (double (or (:activation situation) 0.0))
      :place (as-keyword (:place situation))
      :anger (double (or (:anger situation) 0.0))
      :grief (double (or (:grief situation) 0.0))
      :hope (double (or (:hope situation) 0.0))
      :threat (double (or (:threat situation) 0.0))
      :waiting (double (or (:waiting situation) 0.0))
      :visits 0
      :directed-target (as-keyword (:directed_target situation))
      :inferred (boolean (:inferred situation))
      :external (boolean (:external situation))
      :indices (mapv as-keyword (:indices situation))}]))

(defn- parse-node
  [raw]
  {:id (str (:id raw))
   :goal-type (as-keyword (get-in raw [:mind :goal_type]))
   :situation-ids (mapv as-keyword (get-in raw [:world :situation_ids]))
   :indices (mapv as-keyword (:indices raw))
   :threshold (int (or (:threshold raw) 2))
   :place (as-keyword (:place raw))
   :compatibility (as-keyword (get-in raw [:world :compatibility]))
   :tension (double (or (get-in raw [:mind :tension]) 0.0))
   :energy (double (or (get-in raw [:mind :energy]) 0.0))
   :novelty (double (or (get-in raw [:mind :novelty]) 0.0))
   :hold (boolean (get-in raw [:mind :hold]))
   :dwell-s (double (or (:dwell_s raw) 5.0))
   :tags (mapv str (:tags raw []))})

(defn- load-fixture
  [scope-root]
  (let [{:keys [world-path graph-path feedback-path reporter-path] :as paths}
        (fixture-paths scope-root)
        world-data (load-yaml world-path)
        graph-data (load-json graph-path)
        situations (into {} (map parse-situation (:situations world-data)))
        nodes (mapv parse-node (:nodes graph-data))
        node-by-id (into {} (map (juxt :id identity) nodes))
        edge-lookup (into {}
                          (map (fn [edge]
                                 [[(str (:from edge)) (str (:to edge))]
                                  {:weight (double (or (:weight edge) 0.0))
                                   :kind (as-keyword (:kind edge))}]))
                          (:edges graph-data))]
    {:title (:title world-data)
     :paths paths
     :world-path world-path
     :graph-path graph-path
     :feedback-path feedback-path
     :reporter-path reporter-path
     :palette-path (:source_palette world-data)
     :graph-nodes (count nodes)
     :graph-edges (count (:edges graph-data))
     :situations situations
     :nodes nodes
     :node-by-id node-by-id
     :edge-lookup edge-lookup
     :world-data world-data}))

(defn- negative-pressure
  [{:keys [anger grief threat]}]
  (max (double (or anger 0.0))
       (double (or grief 0.0))
       (double (or threat 0.0))))

(defn- positive-pressure
  [{:keys [hope waiting]}]
  (max (double (or hope 0.0))
       (double (or waiting 0.0))))

(defn- drift-window
  [activation]
  (clamp01 (- 1.0 (/ (Math/abs (- (double activation) 0.35)) 0.35))))

(defn- goal-planning-type
  [goal-type]
  (if (contains? #{:rehearsal :recovery} goal-type)
    :real
    :imaginary))

(defn- current-goal-key
  [world]
  (get-in world [:autonomous :current-goal-key]))

(defn- current-node-id
  [world]
  (get-in world [:autonomous :current-node-id]))

(defn- recent-nodes
  [world]
  (get-in world [:autonomous :recent-nodes] []))

(defn- current-visits
  [situations world]
  (let [[situation-id _] (current-goal-key world)]
    (if situation-id
      (get-in situations [situation-id :visits] 0)
      0)))

(defn- candidate-main-motiv
  [situation-id goal-type]
  (keyword (str (name situation-id) "-" (name goal-type) "-motiv")))

(defn- situation-goal-congruence
  [situation goal-type]
  (let [indices (set (:indices situation))]
    (cond
      (and (= goal-type :rehearsal)
           (or (contains? indices :honesty)
               (contains? indices :performance)
               (contains? indices :ritual)))
      [(if (> (:activation situation) 0.55) 0.14 0.08)
       [:honest_place]]

      (and (= goal-type :reversal) (:inferred situation))
      [0.08 [:inferred_scene]]

      (and (= goal-type :revenge) (:directed-target situation))
      [0.10 [:directed_target]]

      (and (= goal-type :repercussions) (:external situation))
      [0.07 [:external_pressure]]

      :else
      [0.0 []])))

(defn- goal-candidate
  [situation goal-type strength reasons]
  (let [[congruence congruence-reasons] (situation-goal-congruence situation goal-type)]
    {:key [(:id situation) goal-type]
     :situation-id (:id situation)
     :goal-type goal-type
     :planning-type (goal-planning-type goal-type)
     :strength (clamp01 (+ strength congruence))
     :main-motiv (candidate-main-motiv (:id situation) goal-type)
     :reasons (vec (concat reasons congruence-reasons))}))

(defn- fatigue-adjustment
  [candidate situations world]
  (let [current-situation-id (first (current-goal-key world))
        current-goal-type (second (current-goal-key world))
        visits (current-visits situations world)
        candidate-indices (set (get-in situations [(:situation-id candidate) :indices]))]
    (cond
      (and (= (:situation-id candidate) current-situation-id)
           (= (:goal-type candidate) current-goal-type)
           (contains? #{:rationalization :reversal} current-goal-type)
           (>= visits 4))
      (-> candidate
          (update :strength #(clamp01 (- (double %) (+ 0.10 (* (max 0 (- visits 3)) 0.04)))))
          (update :reasons conj :fatigue))

      (and (not= (:situation-id candidate) current-situation-id)
           (= (:goal-type candidate) :roving)
           (contains? #{:rationalization :reversal} current-goal-type)
           (>= visits 4))
      (-> candidate
          (update :strength #(clamp01 (+ (double %) (min 0.26 (* visits 0.06)))))
          (update :reasons conj :fatigue_escape))

      (and (not= (:situation-id candidate) current-situation-id)
           (= (:goal-type candidate) :rehearsal)
           (contains? #{:rationalization :reversal} current-goal-type)
           (>= visits 3)
           (<= visits 5)
           (or (contains? candidate-indices :mission)
               (contains? candidate-indices :urgency)
               (contains? candidate-indices :timer)))
      (-> candidate
          (update :strength #(clamp01 (+ (double %) (min 0.20 (* (max 0 (- visits 2)) 0.07)))))
          (update :reasons conj :mission_escape))

      :else
      candidate)))

(defn compute-goal-candidates
  "Derive competing top-level goal pressure from the current situation map.

  This is a conducted-system adapter mirroring the Python scheduler's broad
  scoring logic without pulling that machinery into the pure kernel."
  [situations world]
  (let [current-situation-id (first (current-goal-key world))
        current-goal-type (second (current-goal-key world))
        visits (current-visits situations world)]
    (->> (vals situations)
         (mapcat
          (fn [situation]
            (let [base (+ (* (:activation situation) 0.6)
                          (* (:ripeness situation) 0.4))
                  negative (negative-pressure situation)
                  positive (positive-pressure situation)
                  exhaustion (min 0.45 (* (double (or (:visits situation) 0)) 0.08))
                  escape-pull (if (and current-situation-id
                                       (not= current-situation-id (:id situation))
                                       (contains? #{:rationalization :reversal}
                                                  current-goal-type))
                                (min 0.28 (* visits 0.05))
                                0.0)
                  roving-strength (when (and (> negative 0.35)
                                             (or (> (:activation situation) 0.08)
                                                 (> exhaustion 0.1)))
                                    (+ (* negative 0.12)
                                       (* base 0.04)
                                       (goal-priority-bonus :roving)
                                       (* exhaustion 0.18)
                                       (* (drift-window (:activation situation)) 0.22)
                                       escape-pull))
                  reversal-strength (when (> negative 0.2)
                                      (+ (* base 0.32)
                                         (* negative 0.22)
                                         (if (:inferred situation) 0.08 0.0)
                                         (goal-priority-bonus :reversal)
                                         (* exhaustion 0.55)
                                         (* (max 0 (- (:visits situation) 5)) -0.05)))
                  rationalization-strength (when (> negative 0.2)
                                             (+ (* base 0.6)
                                                (* negative 0.35)
                                                (goal-priority-bonus :rationalization)
                                                (- exhaustion)))
                  recovery-strength (when (> (+ positive negative) 0.35)
                                      (+ (* base 0.25)
                                         (* (:threat situation) 0.1)
                                         (* (:hope situation) 0.1)
                                         (goal-priority-bonus :recovery)
                                         (* exhaustion 0.35)))
                  rehearsal-strength (when (or (> positive 0.25)
                                               (> (:waiting situation) 0.25))
                                       (+ (* base 0.25)
                                          (* (:waiting situation) 0.25)
                                          (* (:hope situation) 0.2)
                                          (goal-priority-bonus :rehearsal)
                                          (* exhaustion -0.15)))
                  repercussions-strength (when (or (:external situation)
                                                   (> (:threat situation) 0.55))
                                           (+ (* base 0.3)
                                              (* (:threat situation) 0.25)
                                              (goal-priority-bonus :repercussions)
                                              (* exhaustion 0.25)))
                  revenge-strength (when (and (:directed-target situation)
                                              (> negative 0.3))
                                     (+ (* base 0.28)
                                        (* (:anger situation) 0.26)
                                        (goal-priority-bonus :revenge)
                                        (* exhaustion -0.08)))]
              (->> [(when rationalization-strength
                      (goal-candidate situation
                                      :rationalization
                                      rationalization-strength
                                      [:failed_pressure :negative_affect]))
                    (when roving-strength
                      (goal-candidate situation
                                      :roving
                                      roving-strength
                                      [:overload :distraction]))
                    (when reversal-strength
                      (goal-candidate situation
                                      :reversal
                                      reversal-strength
                                      [:counterfactual_pressure :failed_plan]))
                    (when recovery-strength
                      (goal-candidate situation
                                      :recovery
                                      recovery-strength
                                      [:retry :goal_repair]))
                    (when revenge-strength
                      (goal-candidate situation
                                      :revenge
                                      revenge-strength
                                      [:directed_anger :counterparty]))
                    (when rehearsal-strength
                      (goal-candidate situation
                                      :rehearsal
                                      rehearsal-strength
                                      [:anticipation :waiting_goal]))
                    (when repercussions-strength
                      (goal-candidate situation
                                      :repercussions
                                      repercussions-strength
                                      [:external_threat :what_follows]))]
                   (remove nil?)))))
         (map #(fatigue-adjustment % situations world))
         (sort-by (juxt (comp - :strength)
                        (comp str :situation-id)
                        (comp str :goal-type)))
         vec)))

(defn- autonomous-goal-ids
  [world]
  (get-in world [:autonomous :goal-ids] {}))

(defn- goal-entry
  [world candidate]
  (let [goal-key (:key candidate)
        goal-id (get (autonomous-goal-ids world) goal-key)
        goal (when goal-id (get-in world [:goals goal-id]))]
    [goal-id goal]))

(defn- goal-fields
  [candidate]
  (-> candidate
      (select-keys [:goal-type :planning-type :strength :main-motiv :situation-id])
      (assoc :status :runable
             :candidate-reasons (:reasons candidate)
             :activation-policy :autonomous_adapter
             :activation-reasons (:reasons candidate))))

(defn- suppress-stale-goals
  [world active-keys]
  (reduce-kv (fn [current-world goal-key goal-id]
               (if (contains? active-keys goal-key)
                 current-world
                 (-> current-world
                     (assoc-in [:goals goal-id :strength] 0.0)
                     (assoc-in [:goals goal-id :candidate-reasons] [])
                     (assoc-in [:goals goal-id :activation-reasons] []))))
             world
             (autonomous-goal-ids world)))

(defn sync-goals
  "Upsert adapter-derived goals into the kernel world.

  Returns `[world activation-events candidate-summaries]`."
  [world candidates]
  (let [active-keys (set (map :key candidates))
        activation-cx (:reality-lookahead world)
        world (suppress-stale-goals world active-keys)]
    (reduce
     (fn [[current-world activation-events candidate-summaries] candidate]
       (let [[goal-id goal] (goal-entry current-world candidate)
             candidate-summary {:id (or goal-id (:key candidate))
                                :goal-type (:goal-type candidate)
                                :strength (:strength candidate)
                                :planning-type (:planning-type candidate)
                                :situation-id (:situation-id candidate)
                                :reasons (:reasons candidate)}]
         (if (and goal (not (contains? terminal-goal-statuses (:status goal))))
           [(update-in current-world [:goals goal-id] merge (goal-fields candidate))
            activation-events
            (conj candidate-summaries candidate-summary)]
           (let [[next-world next-goal-id]
                 (goals/activate-top-level-goal current-world
                                                activation-cx
                                                (goal-fields candidate))
                 next-world (assoc-in next-world
                                      [:autonomous :goal-ids (:key candidate)]
                                      next-goal-id)
                 activation-event {:goal-id next-goal-id
                                   :goal-type (:goal-type candidate)
                                   :trigger-context-id activation-cx
                                   :failed-goal-id nil
                                   :emotion-id (:main-motiv candidate)
                                   :emotion-strength (:strength candidate)
                                   :activation-policy :autonomous_adapter
                                   :activation-reasons (:reasons candidate)}
                 candidate-summary (assoc candidate-summary :id next-goal-id)]
             [next-world
              (conj activation-events activation-event)
              (conj candidate-summaries candidate-summary)]))))
     [world [] []]
     candidates)))

(defn- retrieve-node-hits
  [fixture active-indices]
  (let [active-set (set active-indices)]
    (->> (:nodes fixture)
         (keep (fn [node]
                 (let [overlap (->> (:indices node)
                                    (filter active-set)
                                    vec)
                       marks (count overlap)]
                   (when (pos? marks)
                     {:node-id (:id node)
                      :marks marks
                      :threshold (:threshold node)
                      :overlap overlap
                      :retrieval-score (double marks)}))))
         (sort-by (juxt (comp - :retrieval-score) (comp str :node-id)))
         vec)))

(defn- compatibility-bias
  [goal-type compatibility]
  (case goal-type
    :rehearsal (if (= compatibility :anticipated_future) 0.24 0.0)
    :recovery (if (= compatibility :anticipated_future) 0.18 0.0)
    :reversal (if (contains? #{:alternative_past :alternative_present} compatibility) 0.22 0.0)
    :revenge (if (contains? #{:alternative_present :present_compatible} compatibility) 0.16 0.0)
    :repercussions (if (= compatibility :projected_consequence) 0.22 0.0)
    :rationalization (if (contains? #{:present_compatible :alternative_present} compatibility) 0.12 0.0)
    :roving (if (= compatibility :alternative_present) 0.10 0.0)
    0.0))

(defn- score-node
  [fixture node hit goal situations world]
  (let [activation-pull (reduce max 0.0
                                (map #(get-in situations [% :activation] 0.0)
                                     (:situation-ids node)))
        edge (get-in fixture [:edge-lookup [(current-node-id world) (:id node)]])
        repeat-count (count (filter #{(:id node)} (recent-nodes world)))
        same-family? (= (:goal-type node) (:goal-type goal))
        same-situation? (some #{(:situation-id goal)} (:situation-ids node))
        score (+ (:retrieval-score hit)
                 (if same-family? 1.4 0.0)
                 (if (= (set [(:goal-type node) (:goal-type goal)])
                        #{:reversal :rationalization})
                   0.4
                   0.0)
                 (if same-situation? 1.2 0.0)
                 (* activation-pull 1.5)
                 (if edge (* 0.8 (:weight edge)) 0.0)
                 (if (= (:place node)
                        (get-in situations [(:situation-id goal) :place]))
                   0.2
                   0.0)
                 (compatibility-bias (:goal-type goal) (:compatibility node))
                 (* repeat-count -0.55))]
    {:score score
     :reasons (cond-> [(str "retrieval:" (:marks hit))]
                same-family? (conj "goal_match")
                same-situation? (conj "situation_match")
                edge (conj (str "local:" (name (:kind edge))))
                (> activation-pull 0.0) (conj "return_pressure")
                (pos? repeat-count) (conj "recent_penalty"))}))

(defn- choose-node
  [fixture hits goal situations world]
  (let [fallback-nodes (filter #(and (= (:goal-type %) (:goal-type goal))
                                     (some #{(:situation-id goal)} (:situation-ids %)))
                               (:nodes fixture))
        scored (->> hits
                    (map (fn [hit]
                           (let [node (get-in fixture [:node-by-id (:node-id hit)])
                                 {:keys [score reasons]}
                                 (score-node fixture node hit goal situations world)]
                             {:node node
                              :hit hit
                              :score score
                              :reasons reasons})))
                    (sort-by (juxt (comp - :score)
                                   (comp str :id :node)))
                    vec)
        selected (or (first scored)
                     (when-let [node (first fallback-nodes)]
                       {:node node
                        :hit {:node-id (:id node)
                              :marks 0
                              :threshold (:threshold node)
                              :overlap []
                              :retrieval-score 0.0}
                        :score 0.0
                        :reasons ["fallback"]}))]
    (when selected
      {:node (:node selected)
       :hit (:hit selected)
       :selection {:policy :highest_score
                   :score (double (:score selected))
                   :reasons (:reasons selected)}})))

(defn- branch-state
  [world situations]
  (adapter/branch-derived-state world situations))

(defn compute-active-indices
  [world goal situations derived-branch-state]
  (let [situation (get situations (:situation-id goal))
        indices (concat (:indices situation)
                        (get derived-branch-state :active-indices [])
                        (:recent-indices world)
                        (when (> (negative-pressure situation) 0.55)
                          [:emotion:neg])
                        (when (or (> (positive-pressure situation) 0.55)
                                  (= (:goal-type goal) :rehearsal))
                          [:emotion:pos]))]
    (ordered-unique indices)))

(defn- apply-metric-deltas
  [situation-state metric-deltas]
  (reduce-kv (fn [state metric delta]
               (update state metric
                       (fn [current]
                         (clamp01 (+ (double (or current 0.0))
                                     (double delta))))))
             situation-state
             metric-deltas))

(defn- goal-type-deltas
  [goal-type]
  (case goal-type
    :revenge {:anger 0.12 :threat -0.03}
    :reversal {:threat 0.06 :grief 0.04}
    :recovery {:hope 0.08 :anger -0.04}
    :rehearsal {:hope 0.08 :waiting 0.05}
    :roving {:hope 0.05 :threat -0.08}
    :rationalization {:grief -0.05 :hope 0.03}
    :repercussions {:threat 0.08 :grief 0.03}
    {}))

(defn- autonomous-rationalization-frame
  [situation-id]
  (or (get autonomous-rationalization-frames situation-id)
      {:priority 0.72
       :reframe-facts [{:fact/type :rationalization
                        :fact/id :present_is_reframed}]}))

(defn- autonomous-cycle-id
  [world situation-id suffix]
  (keyword (str (name situation-id) "-" suffix "-" (:cycle world))))

(defn- seed-autonomous-rationalization-bridge
  [world goal-id context-id]
  (let [goal (get-in world [:goals goal-id])
        situation-id (:situation-id goal)
        situation (get-in world [:situations-state situation-id] {})
        {:keys [priority reframe-facts]} (autonomous-rationalization-frame situation-id)
        failed-goal-id (autonomous-cycle-id world situation-id "autonomous-failure")
        emotion-id (autonomous-cycle-id world situation-id "autonomous-dread")
        frame-id (autonomous-cycle-id world situation-id "autonomous-reframe")
        trigger-strength (max 0.12 (negative-pressure situation))
        facts [{:fact/type :goal
                :goal-id failed-goal-id
                :top-level-goal failed-goal-id
                :status :failed
                :activation-context context-id}
               {:fact/type :emotion
                :emotion-id emotion-id
                :strength trigger-strength
                :valence :negative
                :affect :dread
                :situation-id situation-id}
               {:fact/type :dependency
                :from-id emotion-id
                :to-id failed-goal-id}
               {:fact/type :rationalization-frame
                :fact/id frame-id
                :goal-id failed-goal-id
                :priority priority
                :situation-id situation-id
                :reframe-facts reframe-facts}]
        world (reduce (fn [current-world fact]
                        (cx/assert-fact current-world context-id fact))
                      world
                      facts)]
    [world {:trigger-context-id context-id
            :failed-goal-id failed-goal-id
            :frame-id frame-id
            :priority priority}]))

(defn- decay-situation
  [situation]
  (-> situation
      (update :activation #(* 0.92 (double (or % 0.0))))
      (update :ripeness #(clamp01 (+ (* 0.99 (double (or % 0.0))) 0.01)))
      (update :anger #(* 0.97 (double (or % 0.0))))
      (update :grief #(* 0.97 (double (or % 0.0))))
      (update :hope #(* 0.98 (double (or % 0.0))))
      (update :threat #(* 0.97 (double (or % 0.0))))
      (update :waiting #(* 0.98 (double (or % 0.0))))))

(defn advance-situations
  [situations goal node derived-branch-state]
  (let [situations (reduce-kv (fn [acc situation-id situation]
                                (assoc acc situation-id (decay-situation situation)))
                              {}
                              situations)
        situations (merge-with merge situations (:situations derived-branch-state {}))
        affected-situations (set (concat [(:situation-id goal)]
                                         (:situation-ids node)))]
    (reduce-kv (fn [acc situation-id situation]
                 (let [base-deltas (if (contains? affected-situations situation-id)
                                     {:activation (if (= situation-id (:situation-id goal))
                                                    0.22
                                                    0.14)
                                      :ripeness 0.04}
                                     {})
                       type-deltas (if (= situation-id (:situation-id goal))
                                     (goal-type-deltas (:goal-type goal))
                                     {})
                       updated (-> situation
                                   (apply-metric-deltas base-deltas)
                                   (apply-metric-deltas type-deltas)
                                   (update :visits
                                           (fn [value]
                                             (+ (long (or value 0))
                                                (if (= situation-id (:situation-id goal))
                                                  1
                                                  0)))))]
                   (assoc acc situation-id updated)))
               {}
               situations)))

(defn- summary-line
  [snapshot]
  (let [goal (:selected-goal snapshot)
        retrieved (take 2 (:retrievals snapshot))
        retrieved-summary (if (seq retrieved)
                            (str/join ", "
                                      (map (fn [{:keys [node-id retrieval-score marks]}]
                                             (str node-id " (" (or retrieval-score marks 0) " hits)"))
                                           retrieved))
                            "none")
        sprouted-summary (if (seq (:sprouted snapshot))
                           (str/join ", " (map name (:sprouted snapshot)))
                           "none")
        node-id (or (:chosen-node-id snapshot) "n/a")]
    (format (str "Cycle %d | %s × %s | %s\n"
                 "  retrieved: %s\n"
                 "  sprouted: %s\n"
                 "  mode: %s")
            (:cycle-num snapshot)
            (name (or (:goal-type goal) :none))
            (name (or (:situation-id goal) :none))
            node-id
            retrieved-summary
            sprouted-summary
            (name (or (:mode snapshot) :daydreaming)))))

(defn- scene-situation-ids
  [snapshot fixture]
  (let [chosen-node-id (:chosen-node-id snapshot)
        node-situation-ids (get-in fixture [:node-by-id chosen-node-id :situation-ids] [])
        selected-situation-id (get-in snapshot [:selected-goal :situation-id])]
    (ordered-unique
     (concat node-situation-ids
             (when selected-situation-id
               [selected-situation-id])))))

(defn- scene-situation-descriptions
  [fixture snapshot]
  (into {}
        (keep (fn [situation-id]
                (when-let [description (get-in fixture
                                               [:situations situation-id :description])]
                  [(name situation-id) description])))
        (scene-situation-ids snapshot fixture)))

(defn- current-node-scene
  [fixture snapshot]
  (let [chosen-node-id (:chosen-node-id snapshot)
        node (get-in fixture [:node-by-id chosen-node-id])
        goal (:selected-goal snapshot)]
    {:graph_node_id chosen-node-id
     :tags (vec (:tags node))
     :indices (mapv name (:active-indices snapshot))
     :mind {:goal_type (some-> (:goal-type goal) name)
            :strength (:strength goal)}
     :world {:compatibility (some-> (:compatibility node) name)
             :situation_ids (mapv name (scene-situation-ids snapshot fixture))}
     :situation_descriptions (scene-situation-descriptions fixture snapshot)}))

(defn- runtime-thought-packet
  [world]
  (let [snapshot (last (:trace world))
        fixture (get-in world [:autonomous :fixture])
        chosen-node-id (:chosen-node-id snapshot)
        node (get-in fixture [:node-by-id chosen-node-id])
        goal (:selected-goal snapshot)
        situation-id (:situation-id goal)
        situation (get (:situations-state world) situation-id)
        retrieval-overlap (mapcat :overlap (take 2 (:retrievals snapshot)))
        allowed-residue-indices (->> (concat retrieval-overlap
                                             (:indices node)
                                             (:indices situation)
                                             (:active-indices snapshot))
                                     (remove nil?)
                                     ordered-unique
                                     (mapv name))]
    {:packet_type "RuntimeThoughtBeatV1"
     :cycle (:cycle-num snapshot)
     :selected_goal {:id (some-> (:id goal) name)
                     :goal_type (some-> (:goal-type goal) name)
                     :strength (:strength goal)
                     :planning_type (some-> (:planning-type goal) name)
                     :situation_id (some-> situation-id name)}
     :chosen_node_id chosen-node-id
     :node_tags (vec (:tags node))
     :active_indices (mapv name (:active-indices snapshot))
     :allowed_residue_indices allowed-residue-indices
     :situation {:id (some-> (:id situation) name)
                 :description (:description situation)
                 :place (some-> (:place situation) name)
                 :activation (:activation situation)
                 :threat (:threat situation)
                 :hope (:hope situation)
                 :waiting (:waiting situation)
                 :ripeness (:ripeness situation)
                 :grief (:grief situation)
                 :anger (:anger situation)
                 :indices (mapv name (:indices situation))}
     :retrieved_fragments (mapv (fn [{:keys [node-id retrieval-score overlap threshold]}]
                                  {:node_id node-id
                                   :retrieval_score retrieval-score
                                   :overlap (mapv name overlap)
                                   :threshold threshold})
                                (take 2 (:retrievals snapshot)))
     :top_competing_goals (mapv (fn [candidate]
                                  {:id (some-> (:id candidate) name)
                                   :goal_type (some-> (:goal-type candidate) name)
                                   :strength (:strength candidate)
                                   :planning_type (some-> (:planning-type candidate) name)
                                   :situation_id (some-> (:situation-id candidate) name)})
                                (take 3 (:top-candidates snapshot)))
     :branch_events (mapv (fn [event]
                            {:family (some-> (:family event) name)
                             :source_context (some-> (:source-context event) name)
                             :target_context (some-> (:target-context event) name)
                             :fact_ids (mapv name (:fact-ids event))
                             :fact_types (mapv name (:fact-types event))
                             :episode_ids (mapv name (:episode-ids event))})
                          (take 2 (:branch-events snapshot)))
     :emotional_state (mapv (fn [{:keys [emotion-id affect valence strength role situation-id]}]
                              {:emotion_id (some-> emotion-id name)
                               :affect (some-> affect name)
                               :valence (some-> valence name)
                               :strength strength
                               :role (some-> role name)
                               :situation_id (some-> situation-id name)})
                            (take 3 (:emotional-state snapshot)))
     :previous_residue_summary (get-in world [:autonomous :runtime-thought-last :residue-summary])}))

(defn- director-cycle-input
  [world]
  (let [snapshot (last (:trace world))
        fixture (get-in world [:autonomous :fixture])]
    {:packet (trace/dreamer-state-packet snapshot)
     :scene (current-node-scene fixture snapshot)
     :world-situations (->> (:situations fixture)
                            keys
                            (map name)
                            sort
                            vec)
     :previous-director-feedback (->> (get world :director-recent-concepts [])
                                      (map name)
                                      vec)}))

(defn- maybe-apply-runtime-thought-feedback
  [world thought-fn]
  (if-not thought-fn
    world
    (let [packet (runtime-thought-packet world)
          raw-feedback (thought-fn packet)
          [world feedback-applied] (runtime-thought/apply-feedback
                                    world
                                    packet
                                    raw-feedback)]
      (trace/merge-latest-cycle world {:runtime-thought feedback-applied}))))

(defn- apply-family-plan
  [world goal-id]
  (let [goal-type (get-in world [:goals goal-id :goal-type])]
    (case goal-type
      :reversal
      (if-let [reversal-leaf (families/select-reversal-leaf world)]
        (let [reversal-target (merge reversal-leaf
                                     (families/reverse-undo-causes world reversal-leaf)
                                     {:new-top-level-goal-id goal-id
                                      :new-context-id (or (goals/get-next-context world goal-id)
                                                          (:reality-lookahead world))
                                      :ordering 0.9})
              [world branch-results] (families/reverse-leafs world reversal-target)
              primary-branch (first branch-results)
              sprouted-context-ids (mapv :sprouted-context-id branch-results)
              world (trace/merge-latest-cycle
                     world
                     {:sprouted sprouted-context-ids
                      :mutations (:mutation-events world)
                      :selection {:goal_family :reversal
                                  :reversal_target_policy (:selection-policy reversal-target)
                                  :reversal_target_goal (:old-top-level-goal-id reversal-target)
                                  :reversal_source_context (:old-context-id reversal-target)
                                  :reversal_leaf_policy (:selection-policy primary-branch)
                                  :reversal_leaf_goal (:leaf-goal-id primary-branch)
                                  :reversal_leaf_strength (:leaf-strength primary-branch)
                                  :reversal_leaf_depth (:context-depth reversal-target)
                                  :reversal_leaf_emotion_pressure (:emotion-pressure reversal-target)
                                  :reversal_leaf_reasons (:selection-reasons primary-branch)
                                  :reversal_leaf_retracted_facts (:retracted-fact-ids primary-branch)
                                  :reversal_branch_count (count branch-results)
                                  :reversal_branch_contexts sprouted-context-ids
                                  :reversal_counterfactual_policy (:counterfactual-policy reversal-target)
                                  :reversal_counterfactual_source (:counterfactual-cause-id reversal-target)
                                  :reversal_counterfactual_goal (:counterfactual-goal-id reversal-target)
                                  :reversal_counterfactual_reasons (:counterfactual-reasons reversal-target)
                                  :reversal_counterfactual_fact_ids (:counterfactual-fact-ids reversal-target)
                                  :reversal_branch_context (:sprouted-context-id primary-branch)}})]
          [world :reversal])
        [world nil])

      :roving
      (let [context-id (or (goals/get-next-context world goal-id)
                           (:reality-lookahead world)
                           (:reality-context world))
            [world roving-result] (families/roving-plan world
                                                        {:goal-id goal-id
                                                         :context-id context-id
                                                         :ordering 0.75})
            world (trace/merge-latest-cycle
                   world
                   {:sprouted [(:sprouted-context-id roving-result)]
                    :mutations (:mutation-events world)
                    :selection {:goal_family :roving
                                :roving_selection_policy (:selection-policy roving-result)
                                :roving_seed_episode (:episode-id roving-result)
                                :roving_reminded_episodes (:reminded-episode-ids roving-result)
                                :roving_active_indices (:active-indices roving-result)
                                :roving_branch_context (:sprouted-context-id roving-result)}})]
        [world :roving])

      :rationalization
      (let [context-id (or (goals/get-next-context world goal-id)
                           (:reality-lookahead world)
                           (:reality-context world))
            [world rationalization-spec]
            (seed-autonomous-rationalization-bridge world goal-id context-id)
            [world rationalization-result]
            (families/rationalization-plan world
                                           {:goal-id goal-id
                                            :context-id context-id
                                            :trigger-context-id (:trigger-context-id rationalization-spec)
                                            :failed-goal-id (:failed-goal-id rationalization-spec)
                                            :frame-id (:frame-id rationalization-spec)
                                            :ordering 0.72})
            world (trace/merge-latest-cycle
                   world
                   {:sprouted [(:sprouted-context-id rationalization-result)]
                    :mutations (:mutation-events world)
                    :emotion-shifts (:emotion-shifts rationalization-result)
                    :emotional-state (:emotional-state rationalization-result)
                    :selection {:goal_family :rationalization
                                :rationalization_selection_policy (:selection-policy rationalization-result)
                                :rationalization_frame_id (:frame-id rationalization-result)
                                :rationalization_frame_goal (:frame-goal-id rationalization-result)
                                :rationalization_frame_reasons (:selection-reasons rationalization-result)
                                :rationalization_reframe_fact_ids (:reframe-fact-ids rationalization-result)
                                :rationalization_branch_context (:sprouted-context-id rationalization-result)
                                :rationalization_diversion_policy (:diversion-policy rationalization-result)
                                :rationalization_trigger_emotion_id (:trigger-emotion-id rationalization-result)
                                :rationalization_trigger_emotion_before (:trigger-emotion-before rationalization-result)
                                :rationalization_trigger_emotion_after (:trigger-emotion-after rationalization-result)
                                :rationalization_hope_emotion_id (:hope-emotion-id rationalization-result)
                                :rationalization_hope_strength (:hope-strength rationalization-result)
                                :rationalization_hope_situation (:hope-situation-id rationalization-result)}})]
        [world :rationalization])

      [world nil])))

(defn run-autonomous-cycle
  "Run one autonomous Puppet Knows cycle, returning `[world summary]`."
  [world]
  (let [situations (:situations-state world)
        candidates (compute-goal-candidates situations world)
        [world activations candidate-summaries] (sync-goals world candidates)
        [world selected-goal-id] (control/run-cycle world)
        world (trace/merge-latest-cycle world
                                        {:activations activations
                                         :top-candidates candidate-summaries
                                         :sprouted []})]
    (if-not selected-goal-id
      (let [snapshot (last (:trace world))]
        [world (summary-line snapshot)])
      (let [[world _family] (apply-family-plan world selected-goal-id)
            goal (get-in world [:goals selected-goal-id])
            derived-branch-state (or (branch-state world situations) {})
            effective-situations (merge-with merge situations
                                             (:situations derived-branch-state {}))
            active-indices (compute-active-indices world goal effective-situations derived-branch-state)
            hits (retrieve-node-hits (get-in world [:autonomous :fixture]) active-indices)
            chosen (choose-node (get-in world [:autonomous :fixture])
                                hits
                                goal
                                effective-situations
                                world)
            node (:node chosen)
            trace-retrievals (vec
                              (map (fn [hit]
                                     {:node-id (:node-id hit)
                                      :retrieval-score (:retrieval-score hit)
                                      :marks (:marks hit)
                                      :threshold (:threshold hit)
                                      :overlap (:overlap hit)})
                                   (take 6 hits)))
            next-situations (if node
                              (advance-situations effective-situations
                                                  goal
                                                  node
                                                  derived-branch-state)
                              effective-situations)
            world (cond-> world
                    node
                    (-> (assoc :situations-state next-situations)
                        (assoc-in [:autonomous :current-node-id] (:id node))
                        (assoc-in [:autonomous :current-goal-key]
                                  [(:situation-id goal) (:goal-type goal)])
                        (update-in [:autonomous :recent-nodes]
                                   (fn [recent]
                                     (->> (conj (vec recent) (:id node))
                                          (take-last 6)
                                          vec)))
                        ((fn [current-world]
                           (reduce episodic/add-recent-index
                                   current-world
                                   (:indices node))))))
            world (trace/merge-latest-cycle
                   world
                   (cond-> {:active-indices active-indices
                            :retrievals trace-retrievals
                            :situations (or (:situations-state world) next-situations)}
                     chosen
                     (assoc :chosen-node-id (:id node)
                            :selection (merge (:selection chosen)
                                              (:selection derived-branch-state)))))
            snapshot (last (:trace world))]
        [world (summary-line snapshot)]))))

(defn- maybe-apply-director-feedback
  [world director-fn]
  (if-not director-fn
    world
    (let [director-input (director-cycle-input world)
          raw-feedback (director-fn director-input)
          selected-situation-id (or (get-in world [:trace (dec (count (:trace world)))
                                                   :selection :adapter_selected_situation])
                                    (get-in world [:trace (dec (count (:trace world)))
                                                   :selected-goal :situation-id]))
          [world feedback-applied] (director/apply-feedback
                                    world
                                    {:selected-situation-id selected-situation-id}
                                    raw-feedback)]
      (trace/merge-latest-cycle world {:feedback-applied feedback-applied}))))

(defn benchmark-metadata
  ([fixture]
   (benchmark-metadata fixture {}))
  ([fixture overrides]
   (merge {:started_at autonomous-started-at
           :engine_path "kernel/src/daydreamer"
           :seed "puppet-knows-autonomous"
           :world_path (:world-path fixture)
           :graph_path (:graph-path fixture)
           :feedback_path (:feedback-path fixture)
           :palette_path (:palette-path fixture)
           :graph_nodes (:graph-nodes fixture)
           :graph_edges (:graph-edges fixture)}
          overrides)))

(defn run-benchmark
  "Run the fixture-driven autonomous Puppet Knows benchmark.

  Returns `{:world world :log reporter-log :summaries [..]}`."
  ([] (run-benchmark {}))
  ([{:keys [cycles scope-root git-commit director-fn director-mode
            director-model director-temperature director-max-output-tokens
            thought-fn thought-mode thought-model thought-temperature
            thought-max-output-tokens]
     :or {cycles 12}}]
   (let [fixture (load-fixture (or scope-root (default-scope-root)))
         director-assets (when director-mode
                           (director/load-assets
                            {:world-path (:world-path fixture)
                             :world-data (:world-data fixture)}))
         director-fn (or director-fn
                         (when director-mode
                           (director/build-director-fn
                            director-assets
                            {:mode director-mode
                             :model director-model
                             :temperature (or director-temperature 0.2)
                             :max-output-tokens (or director-max-output-tokens
                                                    1024)})))
         thought-fn (or thought-fn
                        (runtime-thought/build-thought-fn
                         {:mode thought-mode
                          :model thought-model
                          :temperature (or thought-temperature 0.5)
                          :max-output-tokens (or thought-max-output-tokens
                                                 400)}))
         {:keys [world seed-state]} (puppet/build-autonomous-world)
         world (assoc world
                      :cycle 0
                      :situations-state (:situations fixture)
                      :autonomous {:fixture fixture
                                   :seed-state seed-state
                                   :goal-ids {}
                                   :current-node-id nil
                                   :current-goal-key nil
                                   :recent-nodes []
                                   :runtime-thought-last nil})]
     (loop [world world
            remaining cycles
            summaries []]
       (if (zero? remaining)
         {:world world
          :log (trace/reporter-log world
                                   (benchmark-metadata fixture
                                                       {:git_commit git-commit
                                                        :feedback_path
                                                        (case director-mode
                                                          :mock "live:mock-director"
                                                          :gemini "live:gemini-director"
                                                          (:feedback-path fixture))}))
          :summaries summaries}
         (let [[world summary] (run-autonomous-cycle world)
               world (maybe-apply-runtime-thought-feedback world thought-fn)
               world (maybe-apply-director-feedback world director-fn)]
           (recur world
                  (dec remaining)
                  (conj summaries summary))))))))

(comment
  (run-benchmark {:cycles 4}))
