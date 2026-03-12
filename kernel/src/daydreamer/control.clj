(ns daydreamer.control
  "Top-level control loop: decay, goal selection, mode oscillation, planning step.

  Recovers the emotion-driven control loop from Mueller's dd_cntrl.cl.
  Each cycle: decay needs and emotions, select most-motivated goal,
  advance one planning step, check for success or backtrack.
  The system oscillates between :performance and :daydreaming modes.

  Source: dd_cntrl.cl (daydreamer-control0, daydreamer-control1,
          most-highly-motivated-goals, backtrack-top-level-goal)"
  (:require [daydreamer.context :as cx]
            [daydreamer.goals :as goals]))

(def ^:private need-decay-factor 0.98)
(def ^:private emotion-decay-factor 0.95)
(def ^:private emotion-gc-threshold 0.15)

(defn set-state
  "Update the control state for the world."
  [world mode]
  (when-not (contains? #{:suspended :performance :daydreaming} mode)
    (throw (ex-info "Invalid control state"
                    {:mode mode})))
  (assoc world :mode mode))

(defn performance-mode?
  "Return true when the world is in performance mode."
  [world]
  (= (:mode world) :performance))

(defn daydreaming-mode?
  "Return true when the world is in daydreaming mode."
  [world]
  (= (:mode world) :daydreaming))

(defn need-decay
  "Decay need strengths by Mueller's 0.98 factor. Worlds without a `:needs`
  map are returned unchanged."
  [world]
  (update world :needs
          (fn [needs]
            (into {}
                  (map (fn [[need-id need]]
                         [need-id
                          (update need :strength
                                  (fn [strength]
                                    (* need-decay-factor
                                       (or strength 0.0))))]))
                  needs))))

(defn emotion-decay
  "Decay non-motivating emotions and garbage collect those that fall below the
  configured threshold."
  [world]
  (update world :emotions
          (fn [emotions]
            (into {}
                  (comp
                   (map (fn [[emotion-id emotion]]
                          [emotion-id
                           (if (goals/motivating-emotion? world emotion-id)
                             emotion
                             (update emotion :strength
                                     (fn [strength]
                                       (* emotion-decay-factor
                                          (or strength 0.0)))))]))
                   (remove (fn [[_ {:keys [strength]}]]
                             (< (or strength 0.0) emotion-gc-threshold))))
                  emotions))))

(defn- wake-waiting-real-goals
  [world]
  (update world :goals
          (fn [goal-map]
            (into {}
                  (map (fn [[goal-id goal]]
                         [goal-id
                          (if (and (= (:status goal) :waiting)
                                   (= (:planning-type goal) :real))
                            (assoc goal :status :runable)
                            goal)]))
                  goal-map))))

(defn prune-possibilities
  "Filter and order candidate contexts for backtracking/advancement. Contexts
  that have already run rules or are marked as a dedicated daydream-goal sprout
  are skipped."
  [world context-ids]
  (->> context-ids
       (map #(get-in world [:contexts %]))
       (remove nil?)
       (remove (fn [{:keys [rules-run? dd-goal-sprout?]}]
                 (or rules-run? dd-goal-sprout?)))
       (sort-by (juxt (comp - :ordering) (comp str :id)))
       (map :id)
       vec))

(defn terminate-top-level-goal
  "Terminate a top-level goal in a resolution context. If `:reality-context`
  is present, the stabilized reality is sprouted after optional result-fact
  assertion, mirroring Mueller's write-back step."
  ([world goal-id resolution-cx]
   (terminate-top-level-goal world goal-id resolution-cx {}))
  ([world goal-id resolution-cx {:keys [status result-fact]
                                 :or {status :terminated}}]
   (let [goal (get-in world [:goals goal-id])
         world (cond-> world
                 result-fact
                 (cx/assert-fact (or (:reality-context world)
                                     resolution-cx)
                                 result-fact))
         world (-> world
                   (assoc-in [:goals goal-id :status] status)
                   (assoc-in [:goals goal-id :termination-cx] resolution-cx)
                   (update :termination-events
                           (fnil conj [])
                           {:goal-id goal-id
                            :status status
                            :resolution-cx resolution-cx
                            :planning-type (:planning-type goal)}))]
     (if-let [reality-cx (:reality-context world)]
       (let [[world new-reality-cx] (cx/sprout world reality-cx)]
         (-> world
             (assoc :reality-context new-reality-cx)
             (assoc :reality-lookahead new-reality-cx)))
       world))))

(defn all-possibilities-failed
  "Fallback when backtracking hits the wall with no surviving branches."
  [world goal-id backtrack-wall]
  (terminate-top-level-goal world goal-id backtrack-wall {:status :failed}))

(defn backtrack-top-level-goal
  "Walk up the context tree looking for the next surviving sprout. Returns
  `[world next-context-id]`. If all possibilities are exhausted, the goal is
  failed and the context id is nil."
  [world goal-id current-cx]
  (loop [world world
         next-cx current-cx]
    (let [backtrack-wall (goals/get-backtrack-wall world goal-id)]
      (if (= backtrack-wall next-cx)
        [(all-possibilities-failed world goal-id backtrack-wall) nil]
        (let [parent-cx (get-in world [:contexts next-cx :parent-id])
              sprouts (prune-possibilities world (cx/children world parent-cx))]
          (if (seq sprouts)
            (let [selected-cx (first sprouts)]
              [(goals/set-next-context world goal-id selected-cx) selected-cx])
            (recur world parent-cx)))))))

(defn run-goal-step
  "Advance one control step for the current top-level goal after planning has
  run in its next context. Returns `[world next-context-id]`.

  This is the control kernel without the planner itself: timeout checks,
  fired-halt normalization, sprout selection, and fallback backtracking."
  [world goal-id]
  (let [current-cx (goals/get-next-context world goal-id)
        timeout (get-in world [:contexts current-cx :timeout])
        world (assoc-in world [:contexts current-cx :rules-run?] true)]
    (cond
      (and (number? timeout) (<= timeout 0))
      (backtrack-top-level-goal world goal-id current-cx)

      (= :fired-halt (get-in world [:goals goal-id :status]))
      [(goals/change-status world goal-id :halted) nil]

      (contains? #{:runable :halted} (get-in world [:goals goal-id :status]))
      (let [sprouts (prune-possibilities world (cx/children world current-cx))]
        (if (seq sprouts)
          (let [selected-cx (first sprouts)]
            [(goals/set-next-context world goal-id selected-cx) selected-cx])
          (backtrack-top-level-goal world goal-id current-cx)))

      :else
      [world nil])))

(defn run-cycle
  "Run one control cycle: decay needs and emotions, select the strongest
  runnable goal, and flip modes when no candidates are available.

  Returns `[world selected-goal-id]`. When no goal is selected, the id is nil."
  [world]
  (let [world (-> world
                  need-decay
                  emotion-decay
                  (update :cycle (fnil inc 0)))
        candidates (goals/most-highly-motivated-goals world)]
    (cond
      (seq candidates)
      [world (first candidates)]

      (performance-mode? world)
      [(set-state world :daydreaming) nil]

      :else
      [(-> world
           wake-waiting-real-goals
           (set-state :performance))
       nil])))

(comment
  (run-cycle {:goals {}
              :emotions {}
              :mode :daydreaming
              :cycle 0}))
